-- =====================================================================
-- 12. Inscripción de peleadores a eventos
-- Un dueño de gimnasio inscribe boxeadores de su propio gimnasio en un
-- evento. Esto es un roster simple de participantes, previo a
-- cualquier emparejamiento/pelea oficial.
-- =====================================================================

create table public.evento_inscripciones (
  id uuid primary key default gen_random_uuid(),
  evento_id uuid not null references public.eventos(id) on delete cascade,
  boxeador_id uuid not null references public.boxeadores(id) on delete cascade,
  gimnasio_id uuid not null references public.gimnasios(id),
  created_at timestamptz not null default now(),
  unique (evento_id, boxeador_id)
);

create index idx_evento_inscripciones_evento on public.evento_inscripciones(evento_id);
create index idx_evento_inscripciones_boxeador on public.evento_inscripciones(boxeador_id);
create index idx_evento_inscripciones_gimnasio on public.evento_inscripciones(gimnasio_id);
