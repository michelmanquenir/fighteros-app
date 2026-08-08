-- =====================================================================
-- 09. Visibilidad de perfil de boxeador (público/privado)
-- =====================================================================

alter table public.boxeadores
  add column perfil_publico boolean not null default true;
