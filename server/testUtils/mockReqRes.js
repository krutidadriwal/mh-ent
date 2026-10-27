// Minimal req/res fakes for exercising Vercel-style handlers without a real
// HTTP server or Supabase. Covers the branches that return before any
// network/DB call (method checks, validation, auth header checks).
function mockReq({ method = 'GET', url = '/', query = {}, headers = {}, body } = {}) {
  return { method, url, query, headers, body };
}

function mockRes() {
  return {
    statusCode: 200,
    headers: {},
    body: undefined,
    status(code) {
      this.statusCode = code;
      return this;
    },
    setHeader(name, value) {
      this.headers[name] = value;
      return this;
    },
    json(payload) {
      this.body = payload;
      return this;
    }
  };
}

module.exports = { mockReq, mockRes };
