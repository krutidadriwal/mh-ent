const { getSupabase, unwrap } = require('./client');

// The DSO's region for a given day, with every store in it and that day's
// visit/collection progress -- the data behind the app's Daily Target card.
// Returns null if the DSO has no region assigned that day.
async function getDailyAssignment(dsoId, date) {
  const supabase = getSupabase();

  const assignment = unwrap(
    await supabase
      .from('daily_assignments')
      .select('id, assigned_date, region:regions(id, name, region_stores(store:stores(id, name, area, outstanding_amount)))')
      .eq('dso_id', dsoId)
      .eq('assigned_date', date)
      .maybeSingle()
  );
  if (!assignment) {
    return null;
  }

  const [visits, collections] = await Promise.all([
    supabase.from('store_visits').select('store_id').eq('assignment_id', assignment.id).then(unwrap),
    supabase.from('collections').select('store_id, amount').eq('assignment_id', assignment.id).then(unwrap)
  ]);

  const visitedIds = new Set(visits.map((v) => v.store_id));
  const collectedByStore = new Map();
  for (const c of collections) {
    collectedByStore.set(c.store_id, (collectedByStore.get(c.store_id) ?? 0) + Number(c.amount));
  }

  const stores = assignment.region.region_stores
    .map(({ store }) => ({
      id: store.id,
      storeName: store.name,
      area: store.area,
      isVisited: visitedIds.has(store.id),
      collectionDue: Number(store.outstanding_amount),
      collectedToday: collectedByStore.get(store.id) ?? 0
    }))
    .sort((a, b) => a.storeName.localeCompare(b.storeName));

  return {
    assignmentId: assignment.id,
    date: assignment.assigned_date,
    region: { id: assignment.region.id, name: assignment.region.name },
    stores,
    summary: {
      totalStores: stores.length,
      visitedStores: stores.filter((s) => s.isVisited).length,
      collectionDue: stores.reduce((sum, s) => sum + s.collectionDue, 0),
      collected: stores.reduce((sum, s) => sum + s.collectedToday, 0)
    }
  };
}

// Idempotent -- visiting the same store twice in one assignment is a no-op.
async function markStoreVisited(assignmentId, storeId, notes) {
  unwrap(
    await getSupabase()
      .from('store_visits')
      .upsert(
        { assignment_id: assignmentId, store_id: storeId, notes: notes ?? null },
        { onConflict: 'assignment_id,store_id', ignoreDuplicates: true }
      )
  );
}

// Collecting from a store implies visiting it, so the visit is recorded too.
async function recordCollection({ assignmentId, storeId, dsoId, amount, mode, referenceNo }) {
  const collection = unwrap(
    await getSupabase()
      .from('collections')
      .insert({
        assignment_id: assignmentId,
        store_id: storeId,
        dso_id: dsoId,
        amount,
        mode,
        reference_no: referenceNo ?? null
      })
      .select('id, store_id, amount, mode, reference_no, collected_at')
      .single()
  );
  await markStoreVisited(assignmentId, storeId);
  return collection;
}

module.exports = { getDailyAssignment, markStoreVisited, recordCollection };
