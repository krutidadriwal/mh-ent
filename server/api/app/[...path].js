// App -> server API. Everything the Android app calls lives under /api/app/*
// and authenticates with the DSO's Supabase access token (lib/auth/dsoAuth.js).
// Database access goes through lib/supabase/* only.
//
// One catch-all file rather than a file per endpoint: Vercel's Hobby plan
// caps a deployment at 12 Serverless Functions and every api/ file counts as
// one (2 of 12 in use: this + api/supabase/[...path].js).
//
//   GET  /api/app/health       no auth
//   GET  /api/app/me           the signed-in DSO's profile
//   GET  /api/app/today        today's region, stores, visit + collection progress
//   POST /api/app/visits       { storeId, notes? }
//   POST /api/app/collections  { storeId, amount, mode, referenceNo? }

const { requireDso, AuthError } = require('../../lib/auth/dsoAuth');
const { getDailyAssignment, markStoreVisited, recordCollection } = require('../../lib/supabase/assignments');
const { todayInIndia } = require('../../lib/dates');
const { routeSegmentsAfter } = require('../../lib/routeSegments');

const COLLECTION_MODES = ['Cash', 'UPI', 'Cheque', 'NEFT', 'RTGS'];
const MAX_NOTES_LENGTH = 1000;
const MAX_REFERENCE_LENGTH = 100;
const MAX_COLLECTION_AMOUNT = 10_000_000;
const UUID_RE = /^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$/i;

module.exports = async function handler(req, res) {
  // .../api/app/<...> -- drop "api", "app".
  const route = routeSegmentsAfter(req, 2).join('/');

  switch (route) {
    case 'health':
      return health(req, res);
    case 'me':
      return withDso(req, res, 'GET', getMe);
    case 'today':
      return withDso(req, res, 'GET', getToday);
    case 'visits':
      return withDso(req, res, 'POST', postVisit);
    case 'collections':
      return withDso(req, res, 'POST', postCollection);
    default:
      return res.status(404).json({ error: 'Not found' });
  }
};

function health(req, res) {
  if (req.method !== 'GET') {
    res.setHeader('Allow', 'GET');
    return res.status(405).json({ error: 'Method not allowed' });
  }
  return res.status(200).json({ ok: true });
}

// Shared method check + input validation + DSO auth + error mapping, so each
// route below only holds its own logic. `validate` runs before auth so bad
// input gets a 400 without a token/DB round trip.
async function withDso(req, res, method, route) {
  if (req.method !== method) {
    res.setHeader('Allow', method);
    return res.status(405).json({ error: 'Method not allowed' });
  }

  const validationError = route.validate?.(req);
  if (validationError) {
    return res.status(400).json({ error: validationError });
  }

  try {
    const dso = await requireDso(req);
    return await route(req, res, dso);
  } catch (err) {
    if (err instanceof AuthError) {
      return res.status(err.status).json({ error: err.message });
    }
    console.error(`app ${req.method} ${req.url} error`, err);
    return res.status(500).json({ error: 'Internal server error' });
  }
}

async function getMe(req, res, dso) {
  return res.status(200).json(dso);
}

async function getToday(req, res, dso) {
  const assignment = await getDailyAssignment(dso.id, todayInIndia());
  if (!assignment) {
    return res.status(404).json({ error: 'No region assigned for today' });
  }
  return res.status(200).json(assignment);
}

async function postVisit(req, res, dso) {
  const { storeId, notes } = req.body;
  const assignment = await getDailyAssignment(dso.id, todayInIndia());
  const storeError = checkStoreInAssignment(assignment, storeId);
  if (storeError) {
    return res.status(storeError.status).json({ error: storeError.message });
  }

  await markStoreVisited(assignment.assignmentId, storeId, notes?.trim() || null);
  return res.status(200).json(await getDailyAssignment(dso.id, todayInIndia()));
}
postVisit.validate = (req) => {
  const { storeId, notes } = req.body ?? {};
  if (typeof storeId !== 'string' || !UUID_RE.test(storeId)) return 'storeId is required';
  if (notes !== undefined && (typeof notes !== 'string' || notes.length > MAX_NOTES_LENGTH)) {
    return `notes must be a string of at most ${MAX_NOTES_LENGTH} characters`;
  }
  return null;
};

async function postCollection(req, res, dso) {
  const { storeId, amount, mode, referenceNo } = req.body;
  const assignment = await getDailyAssignment(dso.id, todayInIndia());
  const storeError = checkStoreInAssignment(assignment, storeId);
  if (storeError) {
    return res.status(storeError.status).json({ error: storeError.message });
  }

  const collection = await recordCollection({
    assignmentId: assignment.assignmentId,
    storeId,
    dsoId: dso.id,
    amount,
    mode,
    referenceNo: referenceNo?.trim() || null
  });
  return res.status(201).json({ collection, today: await getDailyAssignment(dso.id, todayInIndia()) });
}
postCollection.validate = (req) => {
  const { storeId, amount, mode, referenceNo } = req.body ?? {};
  if (typeof storeId !== 'string' || !UUID_RE.test(storeId)) return 'storeId is required';
  if (typeof amount !== 'number' || !Number.isFinite(amount) || amount <= 0 || amount > MAX_COLLECTION_AMOUNT) {
    return `amount must be a number greater than 0 and at most ${MAX_COLLECTION_AMOUNT}`;
  }
  // Tolerance, not ===, because e.g. 19.99 * 100 is 1998.9999999999998 in floating point.
  if (Math.abs(Math.round(amount * 100) - amount * 100) > 1e-6) return 'amount can have at most 2 decimal places';
  if (!COLLECTION_MODES.includes(mode)) return `mode must be one of: ${COLLECTION_MODES.join(', ')}`;
  if (referenceNo !== undefined && (typeof referenceNo !== 'string' || referenceNo.length > MAX_REFERENCE_LENGTH)) {
    return `referenceNo must be a string of at most ${MAX_REFERENCE_LENGTH} characters`;
  }
  return null;
};

// A DSO can only log visits/collections against stores in today's region.
function checkStoreInAssignment(assignment, storeId) {
  if (!assignment) {
    return { status: 409, message: 'No region assigned for today' };
  }
  if (!assignment.stores.some((s) => s.id === storeId)) {
    return { status: 403, message: "Store is not in today's region" };
  }
  return null;
}
