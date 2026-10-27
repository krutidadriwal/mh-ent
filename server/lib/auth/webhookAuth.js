const crypto = require('crypto');

// Checks the shared secret Supabase sends in the x-webhook-secret header on
// every call to api/supabase/*. Returns false (never throws) for a missing or
// wrong secret; throws only if the server itself isn't configured.
function isValidWebhookSecret(req) {
  const expected = process.env.SUPABASE_WEBHOOK_SECRET;
  if (!expected) {
    throw new Error('SUPABASE_WEBHOOK_SECRET environment variable is not set.');
  }
  const provided = req.headers?.['x-webhook-secret'];
  if (typeof provided !== 'string') {
    return false;
  }
  const a = Buffer.from(provided);
  const b = Buffer.from(expected);
  return a.length === b.length && crypto.timingSafeEqual(a, b);
}

module.exports = { isValidWebhookSecret };
