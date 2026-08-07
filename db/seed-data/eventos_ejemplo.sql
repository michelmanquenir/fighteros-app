-- =====================================================================
-- DATOS DE EJEMPLO — eventos (usuarios + gimnasios + eventos)
-- Solo para pruebas/desarrollo. No es parte del esquema (db/migrations/).
-- =====================================================================

-- ⚠️ password_hash es un placeholder, NO un hash bcrypt real — estas
-- cuentas admin de gimnasio NO van a poder loguearse por /api/auth/login.
-- Si quieres poder loguearte como el organizador de estos eventos,
-- regístralos vía POST /api/auth/registro/gimnasio en su lugar (o pídeme
-- que te genere un hash bcrypt real para pegar acá).

-- Paso 1: usuarios admin + rol gimnasio_admin + gimnasios
with nuevos_usuarios as (
  insert into public.usuarios (nombre, email, password_hash, activo, region_id)
  values
    ('Pedro Araya',  'pedro.araya@ejemplo.cl',  '$2a$10$placeholderHashNoValido.CambiarAntesDeUsarEnLogin', true, 7),
    ('Sofía Herrera', 'sofia.herrera@ejemplo.cl', '$2a$10$placeholderHashNoValido.CambiarAntesDeUsarEnLogin', true, 6)
  returning id, email
),
roles as (
  insert into public.usuario_roles (usuario_id, rol)
  select id, 'gimnasio_admin' from nuevos_usuarios
  returning usuario_id
),
nuevos_gimnasios as (
  insert into public.gimnasios (usuario_admin_id, nombre, direccion, region_id, telefono, email, redes_sociales, descripcion)
  select
    nu.id,
    v.nombre_gimnasio,
    v.direccion,
    v.region_id,
    v.telefono,
    v.email_gimnasio,
    '{}'::jsonb,
    v.descripcion
  from nuevos_usuarios nu
  join (values
    ('pedro.araya@ejemplo.cl',  'Boxeo Santiago Centro', 'Av. Libertador 1234, Santiago', 7, '+56911111111', 'contacto@boxeosantiago.cl', 'Gimnasio boliche amateur en el centro de Santiago.'),
    ('sofia.herrera@ejemplo.cl', 'Club Pugilístico Valparaíso', 'Calle Errázuriz 500, Valparaíso', 6, '+56922222222', 'contacto@boxvalpo.cl', 'Club histórico de boxeo amateur porteño.')
  ) as v(email, nombre_gimnasio, direccion, region_id, telefono, email_gimnasio, descripcion)
    on nu.email = v.email
  returning id, usuario_admin_id, nombre
)
-- Paso 2: eventos organizados por cada gimnasio
insert into public.eventos (
  organizador_id, nombre, tipo, fecha, lugar, region_id,
  cupos_totales, cantidad_publico, estado, afiche_url, reglamento_url
)
select
  ng.usuario_admin_id,
  v.nombre,
  v.tipo::tipo_evento_enum,
  v.fecha,
  v.lugar,
  v.region_id,
  v.cupos_totales,
  v.cantidad_publico,
  v.estado::estado_evento_enum,
  v.afiche_url,
  v.reglamento_url
from nuevos_gimnasios ng
join (values
  ('Boxeo Santiago Centro',        'Velada de Verano',              'velada',      date '2026-08-30', 'Gimnasio Boxeo Santiago Centro', 7, 200, null::int, 'inscripciones_abiertas', null::text, null::text),
  ('Boxeo Santiago Centro',        'Torneo Metropolitano Amateur',  'torneo',      date '2026-09-20', 'Estadio Municipal, Santiago',     7, 500, null,      'planificado',            null,       null),
  ('Boxeo Santiago Centro',        'Velada Cancelada de Prueba',    'velada',      date '2026-07-01', 'Gimnasio Boxeo Santiago Centro', 7, 150, null,      'cancelado',              null,       null),
  ('Club Pugilístico Valparaíso',  'Copa Valparaíso',               'campeonato',  date '2026-10-05', 'Gimnasio Municipal, Valparaíso',  6, 300, null,      'planificado',            null,       null),
  ('Club Pugilístico Valparaíso',  'Exhibición Boxeo Joven',        'exhibicion',  date '2026-06-15', 'Plaza Victoria, Valparaíso',      6, 100, 80,        'finalizado',             null,       null)
) as v(gimnasio_nombre, nombre, tipo, fecha, lugar, region_id, cupos_totales, cantidad_publico, estado, afiche_url, reglamento_url)
  on ng.nombre = v.gimnasio_nombre;
