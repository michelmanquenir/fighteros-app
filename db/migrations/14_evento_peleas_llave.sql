-- =====================================================================
-- 14. Llave de torneo: liga las peleas pactadas a un evento_torneo
--     y les da un número de ronda para poder armar el árbol.
-- =====================================================================

alter table public.peleas
  add column torneo_id uuid references public.evento_torneos(id) on delete set null;

alter table public.peleas
  add column ronda smallint not null default 1;

create index idx_peleas_torneo on public.peleas(torneo_id);
