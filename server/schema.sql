-- Mahalaxmi Enterprise schema. Idempotent -- safe to re-run in the Supabase
-- SQL editor.
--
-- Access model: RLS is enabled on every table with NO policies, so the anon
-- and authenticated roles can't read or write anything. Only the server's
-- service role key (which bypasses RLS) touches these tables; the Android
-- app goes through the server's /api/app/* endpoints.

-- DSOs (and admins) -- one row per Supabase Auth user allowed into the app.
create table if not exists public.dso_profiles (
  id uuid primary key references auth.users (id) on delete cascade,
  email text not null unique,
  name text,
  phone text,
  role text not null default 'dso' check (role in ('dso', 'admin')),
  created_at timestamptz not null default now()
);

-- Retailers a DSO visits. outstanding_amount is what's due from the store,
-- shown as "collection due" on the app's Daily Target card.
create table if not exists public.stores (
  id uuid primary key default gen_random_uuid(),
  name text not null,
  area text,
  city text,
  phone text,
  outstanding_amount numeric(12, 2) not null default 0 check (outstanding_amount >= 0),
  created_at timestamptz not null default now()
);

create table if not exists public.regions (
  id uuid primary key default gen_random_uuid(),
  name text not null unique,
  created_at timestamptz not null default now()
);

create table if not exists public.region_stores (
  region_id uuid not null references public.regions (id) on delete cascade,
  store_id uuid not null references public.stores (id) on delete cascade,
  primary key (region_id, store_id)
);
create index if not exists region_stores_store_id_idx on public.region_stores (store_id);

-- Which region a DSO works on which day. Visiting every store in it is that
-- day's target.
create table if not exists public.daily_assignments (
  id uuid primary key default gen_random_uuid(),
  dso_id uuid not null references public.dso_profiles (id) on delete cascade,
  region_id uuid not null references public.regions (id) on delete restrict,
  assigned_date date not null,
  created_at timestamptz not null default now(),
  unique (dso_id, assigned_date)
);
create index if not exists daily_assignments_region_id_idx on public.daily_assignments (region_id);

create table if not exists public.store_visits (
  id uuid primary key default gen_random_uuid(),
  assignment_id uuid not null references public.daily_assignments (id) on delete cascade,
  store_id uuid not null references public.stores (id) on delete restrict,
  notes text,
  visited_at timestamptz not null default now(),
  unique (assignment_id, store_id)
);
create index if not exists store_visits_store_id_idx on public.store_visits (store_id);

-- Money collected by a DSO. Never cascaded away -- deleting an assignment,
-- store or DSO that has collections is refused.
create table if not exists public.collections (
  id uuid primary key default gen_random_uuid(),
  assignment_id uuid not null references public.daily_assignments (id) on delete restrict,
  store_id uuid not null references public.stores (id) on delete restrict,
  dso_id uuid not null references public.dso_profiles (id) on delete restrict,
  amount numeric(12, 2) not null check (amount > 0),
  mode text not null check (mode in ('Cash', 'UPI', 'Cheque', 'NEFT', 'RTGS')),
  reference_no text,
  collected_at timestamptz not null default now()
);
create index if not exists collections_assignment_id_idx on public.collections (assignment_id);
create index if not exists collections_store_id_idx on public.collections (store_id);
create index if not exists collections_dso_id_idx on public.collections (dso_id);

alter table public.dso_profiles enable row level security;
alter table public.stores enable row level security;
alter table public.regions enable row level security;
alter table public.region_stores enable row level security;
alter table public.daily_assignments enable row level security;
alter table public.store_visits enable row level security;
alter table public.collections enable row level security;
