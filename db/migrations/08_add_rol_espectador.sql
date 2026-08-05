-- =====================================================================
-- 08. Agrega el rol 'espectador' a rol_usuario_enum
-- Va en su propio script: Postgres no permite usar un valor de enum
-- recien agregado dentro de la misma transaccion que lo crea.
-- =====================================================================

alter type rol_usuario_enum add value 'espectador';
