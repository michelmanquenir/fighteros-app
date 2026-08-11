-- =====================================================================
-- 11. Múltiples gimnasios por cuenta + gimnasio explícito por evento
-- =====================================================================

-- Antes se derivaba "el" gimnasio de un evento buscando el único
-- gimnasio del usuario organizador. Ahora que una cuenta puede tener
-- más de un gimnasio, el evento debe decir explícitamente a cuál
-- pertenece.
alter table public.eventos
  add column gimnasio_id uuid references public.gimnasios(id);

-- Backfill: para eventos ya creados, asigna el gimnasio del organizador
-- si tenía exactamente uno (si tenía más de uno no hay forma de saber
-- cuál era, se deja en null).
update public.eventos e
set gimnasio_id = g.id
from public.gimnasios g
where g.usuario_admin_id = e.organizador_id
  and e.gimnasio_id is null
  and (
    select count(*) from public.gimnasios g2
    where g2.usuario_admin_id = e.organizador_id
  ) = 1;
