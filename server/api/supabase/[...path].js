// Supabase -> server API. Only Supabase calls /api/supabase/* (Database
// Webhooks today; pg_cron / Edge Function callbacks can be added the same
// way). Authenticated by the shared SUPABASE_WEBHOOK_SECRET header
// (lib/auth/webhookAuth.js), never by a DSO token -- the app has no business
// here. Database access goes through lib/supabase/* only.
//
//   POST /api/supabase/db-webhook   Supabase Database Webhook payload:
//        { type: 'INSERT'|'UPDATE'|'DELETE', table, schema, record, old_record }

const { isValidWebhookSecret } = require('../../lib/auth/webhookAuth');
const { routeSegmentsAfter } = require('../../lib/routeSegments');

const EVENT_TYPES = ['INSERT', 'UPDATE', 'DELETE'];

// Keyed by '<table>.<type>', e.g. 'collections.INSERT'. Add a handler here
// and point a Supabase Database Webhook at /api/supabase/db-webhook for that
// table + event. Events with no handler are acknowledged and ignored.
const dbEventHandlers = {};

module.exports = async function handler(req, res) {
  // .../api/supabase/<...> -- drop "api", "supabase".
  const route = routeSegmentsAfter(req, 2).join('/');

  switch (route) {
    case 'db-webhook':
      return withWebhookSecret(req, res, 'POST', dbWebhook);
    default:
      return res.status(404).json({ error: 'Not found' });
  }
};

async function withWebhookSecret(req, res, method, route) {
  if (req.method !== method) {
    res.setHeader('Allow', method);
    return res.status(405).json({ error: 'Method not allowed' });
  }

  try {
    if (!isValidWebhookSecret(req)) {
      return res.status(401).json({ error: 'Invalid webhook secret' });
    }

    const validationError = route.validate?.(req);
    if (validationError) {
      return res.status(400).json({ error: validationError });
    }

    return await route(req, res);
  } catch (err) {
    console.error(`supabase ${req.method} ${req.url} error`, err);
    return res.status(500).json({ error: 'Internal server error' });
  }
}

async function dbWebhook(req, res) {
  const event = req.body;
  const eventHandler = dbEventHandlers[`${event.table}.${event.type}`];
  if (!eventHandler) {
    return res.status(200).json({ handled: false });
  }
  await eventHandler(event);
  return res.status(200).json({ handled: true });
}
dbWebhook.validate = (req) => {
  const { type, table } = req.body ?? {};
  if (!EVENT_TYPES.includes(type)) return `type must be one of: ${EVENT_TYPES.join(', ')}`;
  if (typeof table !== 'string' || !table) return 'table is required';
  return null;
};
