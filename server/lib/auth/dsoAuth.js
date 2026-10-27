// Verifies the app's Supabase-issued access token and loads the caller's
// profile from public.dso_profiles.
//
// Verification goes through the project's published JWKS (its public keys)
// so it works with Supabase's asymmetric (ES256) signing keys. jose's
// createRemoteJWKSet fetches and caches them, re-fetching on an unknown "kid".
//
// jose is ESM-only -- this file is CommonJS, so it's loaded with dynamic
// import(). require('jose') works locally but crashes with ERR_REQUIRE_ESM
// on Vercel's runtime.
const { findDsoProfile } = require('../supabase/dsoProfiles');

class AuthError extends Error {
  constructor(status, message) {
    super(message);
    this.status = status;
  }
}

let jwks;

async function getJwks() {
  if (!jwks) {
    const supabaseUrl = process.env.SUPABASE_URL;
    if (!supabaseUrl) {
      throw new Error('SUPABASE_URL environment variable is not set.');
    }
    const { createRemoteJWKSet } = await import('jose');
    jwks = createRemoteJWKSet(new URL(`${supabaseUrl}/auth/v1/.well-known/jwks.json`));
  }
  return jwks;
}

function extractBearerToken(req) {
  const header = req.headers?.authorization;
  if (typeof header !== 'string' || !header.startsWith('Bearer ')) {
    return null;
  }
  return header.slice('Bearer '.length).trim();
}

// Returns the caller's { id, email, name, phone, role }, or throws an
// AuthError (401 for a missing/invalid token or no profile, 403 if minRole
// is 'admin' and the caller is a DSO).
async function requireDso(req, { minRole } = {}) {
  const token = extractBearerToken(req);
  if (!token) {
    throw new AuthError(401, 'Missing bearer token');
  }

  let payload;
  try {
    const { jwtVerify } = await import('jose');
    ({ payload } = await jwtVerify(token, await getJwks()));
  } catch (err) {
    throw new AuthError(401, 'Invalid or expired token');
  }

  const profile = await findDsoProfile(payload.sub);
  if (!profile) {
    throw new AuthError(401, 'No DSO account for this token');
  }
  if (minRole === 'admin' && profile.role !== 'admin') {
    throw new AuthError(403, 'Admin role required');
  }
  return profile;
}

module.exports = { requireDso, AuthError };
