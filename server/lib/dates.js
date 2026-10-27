// Today's date in India as YYYY-MM-DD. Vercel runs in UTC, so new Date()'s
// date alone would roll over to "tomorrow" at 5:30 AM IST instead of midnight.
function todayInIndia() {
  return new Intl.DateTimeFormat('en-CA', { timeZone: 'Asia/Kolkata' }).format(new Date());
}

module.exports = { todayInIndia };
