-- =====================================================================
-- DATOS DE EJEMPLO — boxeadores (usuarios + usuario_roles + boxeadores)
-- Solo para pruebas/desarrollo. No es parte del esquema (db/migrations/).
-- =====================================================================

-- ⚠️ password_hash es un placeholder, NO un hash bcrypt real.
-- Estas cuentas quedan creadas en la DB pero NO van a poder loguearse
-- por /api/auth/login (Spring Security va a rechazar el hash inválido).
-- Si necesitas cuentas con login funcional, regístralas vía
-- POST /api/auth/registro/boxeador, o pídeme que te genere un hash bcrypt real.

-- ⚠️ Los RUT usados abajo tienen el FORMATO correcto (el que exige la DB),
-- pero el dígito verificador no está calculado real. La DB no lo valida
-- (solo el formato); esa validación de verdad solo la hace el backend
-- al registrar vía API.

with nuevos_usuarios as (
  insert into public.usuarios (nombre, email, password_hash, activo, region_id)
  values
    ('Juan Pérez',       'juan.perez@ejemplo.cl',       '$2a$10$placeholderHashNoValido.CambiarAntesDeUsarEnLogin', true, 13),
    ('María González',   'maria.gonzalez@ejemplo.cl',   '$2a$10$placeholderHashNoValido.CambiarAntesDeUsarEnLogin', true, 7),
    ('Diego Fuentes',    'diego.fuentes@ejemplo.cl',    '$2a$10$placeholderHashNoValido.CambiarAntesDeUsarEnLogin', true, 8),
    ('Camila Rojas',     'camila.rojas@ejemplo.cl',     '$2a$10$placeholderHashNoValido.CambiarAntesDeUsarEnLogin', true, 6),
    ('Matías Silva',     'matias.silva@ejemplo.cl',     '$2a$10$placeholderHashNoValido.CambiarAntesDeUsarEnLogin', true, 5)
  returning id, email
),
roles as (
  insert into public.usuario_roles (usuario_id, rol)
  select id, 'boxeador' from nuevos_usuarios
  returning usuario_id
)
insert into public.boxeadores (
  id, rut, fecha_nacimiento, sexo, peso_actual, peso_habitual,
  categoria_id, region_id, estado_deportivo, nivel_progresion
)
select
  nu.id,
  v.rut,
  v.fecha_nacimiento,
  v.sexo::sexo_enum,
  v.peso_actual,
  v.peso_habitual,
  (
    select cp.id from public.categorias_peso cp
    where cp.sexo = v.sexo::sexo_enum
      and v.peso_actual between cp.peso_min and cp.peso_max
    limit 1
  ) as categoria_id,
  v.region_id,
  'activo'::estado_deportivo_enum,
  'debutante'::nivel_progresion_enum
from nuevos_usuarios nu
join (values
  ('juan.perez@ejemplo.cl',     '11111111-1', date '2001-05-14', 'M', 63.40, 64.00, 13),
  ('maria.gonzalez@ejemplo.cl', '12222222-2', date '2003-11-02', 'F', 57.10, 58.00, 7),
  ('diego.fuentes@ejemplo.cl',  '13333333-3', date '1999-02-27', 'M', 75.80, 77.00, 8),
  ('camila.rojas@ejemplo.cl',   '14444444-4', date '2005-07-19', 'F', 50.30, 51.00, 6),
  ('matias.silva@ejemplo.cl',   '15555555-5', date '2000-09-30', 'M', 81.20, 82.00, 5)
) as v(email, rut, fecha_nacimiento, sexo, peso_actual, peso_habitual, region_id)
  on nu.email = v.email;
