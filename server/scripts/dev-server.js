// Local stand-in for Vercel: routes /api/<group>/* to the matching
// api/<group>/[...path].js handler and adds the req.query / req.body /
// res.status / res.json helpers Vercel's Node runtime provides.
// Run with `npm run dev` (loads .env).
const http = require('http');

const catchAllRoutes = {
  '/api/app': require('../api/app/[...path]'),
  '/api/supabase': require('../api/supabase/[...path]')
};

function resolveHandler(pathname) {
  for (const [prefix, handler] of Object.entries(catchAllRoutes)) {
    if (pathname === prefix || pathname.startsWith(`${prefix}/`)) {
      return handler;
    }
  }
  return null;
}

const server = http.createServer((req, res) => {
  res.status = (code) => {
    res.statusCode = code;
    return res;
  };
  res.json = (obj) => {
    res.setHeader('Content-Type', 'application/json');
    res.end(JSON.stringify(obj));
  };

  const parsed = new URL(req.url, `http://${req.headers.host ?? 'localhost'}`);
  const handler = resolveHandler(parsed.pathname);
  if (!handler) {
    return res.status(404).json({ error: 'Not found' });
  }
  req.query = Object.fromEntries(parsed.searchParams);

  let body = '';
  req.on('data', (chunk) => (body += chunk));
  req.on('end', () => {
    try {
      req.body = body ? JSON.parse(body) : {};
    } catch {
      req.body = {};
    }
    Promise.resolve(handler(req, res)).catch((err) => {
      console.error(err);
      res.status(500).json({ error: 'Internal server error' });
    });
  });
});

const PORT = 3002;
server.listen(PORT, () => console.log(`Mh-Ent dev server listening on http://localhost:${PORT}`));
