const { getSupabase, unwrap } = require('./client');

// Returns { id, email, name, phone, role } or null if no profile exists.
async function findDsoProfile(id) {
  return unwrap(
    await getSupabase()
      .from('dso_profiles')
      .select('id, email, name, phone, role')
      .eq('id', id)
      .maybeSingle()
  );
}

module.exports = { findDsoProfile };
