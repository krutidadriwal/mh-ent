const { createClient } = require('@supabase/supabase-js');

let client;

// Server-only client using the service role key. Every table has RLS on with
// no policies, so this client is the only way in -- the Android app talks to
// api/app/* and never to Supabase directly.
function getSupabase() {
  if (!client) {
    const url = process.env.SUPABASE_URL;
    const serviceRoleKey = process.env.SUPABASE_SERVICE_ROLE_KEY;
    if (!url || !serviceRoleKey) {
      throw new Error('SUPABASE_URL and SUPABASE_SERVICE_ROLE_KEY environment variables must be set.');
    }
    client = createClient(url, serviceRoleKey, {
      auth: { autoRefreshToken: false, persistSession: false }
    });
  }
  return client;
}

// supabase-js returns { data, error } instead of throwing -- turn errors into
// exceptions so callers can use plain try/catch.
function unwrap({ data, error }) {
  if (error) {
    throw error;
  }
  return data;
}

module.exports = { getSupabase, unwrap };
