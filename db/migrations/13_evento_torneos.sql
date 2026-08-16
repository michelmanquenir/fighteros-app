-- =====================================================================
-- 13. Torneos internos de un evento (brackets por categoría de peso)
-- Ej: dentro de una velada grande, "Torneo 1 - Pluma", "Torneo 2 -
-- Welter", etc. Los inscritos se pueden asignar a uno.
-- =====================================================================

create table public.evento_torneos (
  id uuid primary key default gen_random_uuid(),
  evento_id uuid not null references public.eventos(id) on delete cascade,
  nombre text not null,
  categoria_id uuid references public.categorias_peso(id),
  created_at timestamptz not null default now()
);

create index idx_evento_torneos_evento on public.evento_torneos(evento_id);

alter table public.evento_inscripciones
  add column torneo_id uuid references public.evento_torneos(id) on delete set null;

create index idx_evento_inscripciones_torneo on public.evento_inscripciones(torneo_id);
