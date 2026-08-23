-- =====================================================================
-- 15. Modalidad de inscripción del evento (abierta/cerrada), cupo por
--     gimnasio, e invitaciones para eventos cerrados.
-- =====================================================================

create type modalidad_inscripcion_enum as enum ('abierta', 'cerrada');

alter table public.eventos
  add column modalidad modalidad_inscripcion_enum not null default 'abierta';

alter table public.eventos
  add column cupos_por_gimnasio integer;

create table public.evento_gimnasio_invitaciones (
  id uuid primary key default gen_random_uuid(),
  evento_id uuid not null references public.eventos(id) on delete cascade,
  gimnasio_id uuid not null references public.gimnasios(id) on delete cascade,
  estado estado_solicitud_enum not null default 'pendiente',
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now(),
  unique (evento_id, gimnasio_id)
);

create trigger trg_evento_gimnasio_invitaciones_updated_at
  before update on public.evento_gimnasio_invitaciones
  for each row execute function public.set_updated_at();

create index idx_evento_gimnasio_invitaciones_evento on public.evento_gimnasio_invitaciones(evento_id);
create index idx_evento_gimnasio_invitaciones_gimnasio on public.evento_gimnasio_invitaciones(gimnasio_id);
