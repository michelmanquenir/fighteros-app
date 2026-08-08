-- =====================================================================
-- 10. Seguidores — sistema de seguimiento entre usuarios
-- =====================================================================

create type estado_seguimiento_enum as enum ('pendiente', 'aceptado');

create table public.seguidores (
  seguidor_id uuid not null references public.usuarios(id) on delete cascade,
  seguido_id uuid not null references public.usuarios(id) on delete cascade,
  estado estado_seguimiento_enum not null default 'pendiente',
  created_at timestamptz not null default now(),
  primary key (seguidor_id, seguido_id),
  constraint seguidores_no_auto_seguimiento check (seguidor_id <> seguido_id)
);

create index idx_seguidores_seguido on public.seguidores(seguido_id, estado);
create index idx_seguidores_seguidor on public.seguidores(seguidor_id);
