const test = require('node:test');
const assert = require('node:assert/strict');
const handler = require('../api/app/[...path]');
const { mockReq, mockRes } = require('../testUtils/mockReqRes');

const STORE_ID = '7b0d8c1e-3f2a-4c5b-9d6e-1a2b3c4d5e6f';

async function call(opts) {
  const res = mockRes();
  await handler(mockReq(opts), res);
  return res;
}

test('unknown /api/app/<route> returns 404', async () => {
  const res = await call({ url: '/api/app/nope' });
  assert.equal(res.statusCode, 404);
});

test('GET /api/app/health returns ok without auth', async () => {
  const res = await call({ url: '/api/app/health' });
  assert.equal(res.statusCode, 200);
  assert.deepEqual(res.body, { ok: true });
});

test('POST /api/app/today is not allowed', async () => {
  const res = await call({ method: 'POST', url: '/api/app/today' });
  assert.equal(res.statusCode, 405);
  assert.equal(res.headers.Allow, 'GET');
});

test('GET /api/app/today without a bearer token returns 401', async () => {
  const res = await call({ url: '/api/app/today' });
  assert.equal(res.statusCode, 401);
});

test('POST /api/app/visits without storeId returns 400', async () => {
  const res = await call({ method: 'POST', url: '/api/app/visits', body: {} });
  assert.equal(res.statusCode, 400);
  assert.match(res.body.error, /storeId/);
});

test('POST /api/app/collections with a non-positive amount returns 400', async () => {
  const res = await call({
    method: 'POST',
    url: '/api/app/collections',
    body: { storeId: STORE_ID, amount: 0, mode: 'Cash' }
  });
  assert.equal(res.statusCode, 400);
  assert.match(res.body.error, /amount/);
});

test('POST /api/app/collections with more than 2 decimals returns 400', async () => {
  const res = await call({
    method: 'POST',
    url: '/api/app/collections',
    body: { storeId: STORE_ID, amount: 10.555, mode: 'Cash' }
  });
  assert.equal(res.statusCode, 400);
  assert.match(res.body.error, /decimal/);
});

test('POST /api/app/collections accepts a 2-decimal amount like 19.99', async () => {
  const res = await call({
    method: 'POST',
    url: '/api/app/collections',
    body: { storeId: STORE_ID, amount: 19.99, mode: 'Cash' }
  });
  // Gets past validation to the auth check.
  assert.equal(res.statusCode, 401);
});

test('POST /api/app/collections with an unknown mode returns 400', async () => {
  const res = await call({
    method: 'POST',
    url: '/api/app/collections',
    body: { storeId: STORE_ID, amount: 500, mode: 'Barter' }
  });
  assert.equal(res.statusCode, 400);
  assert.match(res.body.error, /mode/);
});

test('POST /api/app/collections with valid input but no token returns 401', async () => {
  const res = await call({
    method: 'POST',
    url: '/api/app/collections',
    body: { storeId: STORE_ID, amount: 500, mode: 'UPI' }
  });
  assert.equal(res.statusCode, 401);
});
