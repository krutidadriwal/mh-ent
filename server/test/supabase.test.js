const test = require('node:test');
const assert = require('node:assert/strict');
const handler = require('../api/supabase/[...path]');
const { mockReq, mockRes } = require('../testUtils/mockReqRes');

const SECRET = 'test-webhook-secret';
process.env.SUPABASE_WEBHOOK_SECRET = SECRET;

async function call(opts) {
  const res = mockRes();
  await handler(mockReq(opts), res);
  return res;
}

test('unknown /api/supabase/<route> returns 404', async () => {
  const res = await call({ method: 'POST', url: '/api/supabase/nope' });
  assert.equal(res.statusCode, 404);
});

test('GET /api/supabase/db-webhook is not allowed', async () => {
  const res = await call({ url: '/api/supabase/db-webhook' });
  assert.equal(res.statusCode, 405);
});

test('db-webhook without the secret header returns 401', async () => {
  const res = await call({
    method: 'POST',
    url: '/api/supabase/db-webhook',
    body: { type: 'INSERT', table: 'collections' }
  });
  assert.equal(res.statusCode, 401);
});

test('db-webhook with a wrong secret returns 401', async () => {
  const res = await call({
    method: 'POST',
    url: '/api/supabase/db-webhook',
    headers: { 'x-webhook-secret': 'wrong' },
    body: { type: 'INSERT', table: 'collections' }
  });
  assert.equal(res.statusCode, 401);
});

test('a DSO bearer token does not get into db-webhook', async () => {
  const res = await call({
    method: 'POST',
    url: '/api/supabase/db-webhook',
    headers: { authorization: 'Bearer some-dso-token' },
    body: { type: 'INSERT', table: 'collections' }
  });
  assert.equal(res.statusCode, 401);
});

test('db-webhook with a bad event type returns 400', async () => {
  const res = await call({
    method: 'POST',
    url: '/api/supabase/db-webhook',
    headers: { 'x-webhook-secret': SECRET },
    body: { type: 'TRUNCATE', table: 'collections' }
  });
  assert.equal(res.statusCode, 400);
});

test('db-webhook acknowledges events that have no handler', async () => {
  const res = await call({
    method: 'POST',
    url: '/api/supabase/db-webhook',
    headers: { 'x-webhook-secret': SECRET },
    body: { type: 'INSERT', table: 'collections', schema: 'public', record: {}, old_record: null }
  });
  assert.equal(res.statusCode, 200);
  assert.deepEqual(res.body, { handled: false });
});
