-- =====================================================================
-- 16. Fases 4-7 del flujo de organización: hora del evento (para poder
--     calcular el cierre automático de inscripciones), confirmación de
--     peleas por gimnasio, cartelera oficial publicable, y link de
--     entradas para promoción.
-- =====================================================================

alter table public.eventos
  add column hora time;

alter table public.eventos
  add column cartelera_publicada boolean not null default false;

alter table public.eventos
  add column link_entradas text;

alter table public.peleas
  add column confirmacion_gimnasio_a estado_solicitud_enum not null default 'pendiente';

alter table public.peleas
  add column confirmacion_gimnasio_b estado_solicitud_enum not null default 'pendiente';
