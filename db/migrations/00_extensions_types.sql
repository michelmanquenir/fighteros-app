-- =====================================================================
-- 00. EXTENSIONES, TIPOS ENUM Y FUNCIONES DE APOYO
-- Requiere Postgres 13+ (Supabase corre 15/17, gen_random_uuid() es nativo)
-- =====================================================================

-- Trigger genérico para mantener updated_at
create or replace function public.set_updated_at()
returns trigger
language plpgsql
as $$
begin
  new.updated_at = now();
  return new;
end;
$$;

-- ---------------------------------------------------------------------
-- ENUMS
-- ---------------------------------------------------------------------
create type sexo_enum as enum ('M', 'F');

create type rol_usuario_enum as enum (
  'boxeador', 'entrenador', 'gimnasio_admin', 'organizador',
  'arbitro', 'juez', 'fotografo', 'productora', 'admin'
);

create type estado_deportivo_enum as enum ('activo', 'retirado', 'suspendido', 'lesionado');

create type nivel_progresion_enum as enum (
  'debutante', 'novato', 'intermedio', 'avanzado', 'elite_amateur'
);

create type tipo_medalla_enum as enum ('oro', 'plata', 'bronce');

create type tipo_evento_enum as enum ('torneo', 'velada', 'exhibicion', 'campeonato');

create type estado_evento_enum as enum (
  'planificado', 'inscripciones_abiertas', 'en_curso', 'finalizado', 'cancelado'
);

create type tipo_premio_enum as enum (
  'medalla', 'copa', 'dinero', 'cinturon', 'producto', 'diploma'
);

create type estado_solicitud_enum as enum ('pendiente', 'aceptada', 'rechazada', 'cancelada');

create type estado_reemplazo_enum as enum ('buscando', 'cubierto', 'cancelado');

create type resultado_pelea_enum as enum (
  'victoria_a', 'victoria_b', 'empate', 'no_contest', 'cancelada'
);

create type metodo_victoria_enum as enum (
  'ko', 'tko_rsc', 'decision_unanime', 'decision_dividida', 'wo', 'descalificacion', 'no_contest'
);

create type estado_validacion_enum as enum ('pendiente', 'validada', 'rechazada');

create type rol_validador_enum as enum ('entrenador_a', 'entrenador_b', 'organizador', 'arbitro');

create type tipo_accion_arbitro_enum as enum (
  'ko', 'rsc', 'wo', 'advertencia', 'descuento_puntos', 'descalificacion'
);

create type modalidad_juez_enum as enum ('digital', 'papel');

create type estado_certificado_enum as enum ('vigente', 'vencido', 'suspendido');

create type estado_pesaje_enum as enum ('cumple', 'no_cumple', 'pendiente');

create type estado_inscripcion_enum as enum ('pendiente', 'aprobada', 'rechazada');

create type estado_pago_enum as enum ('pendiente', 'pagado', 'fallido', 'reembolsado');

create type tipo_multimedia_enum as enum ('foto', 'video');

create type tipo_compra_multimedia_enum as enum (
  'foto', 'video', 'poster', 'diseno', 'paquete', 'edicion', 'highlight'
);

create type estado_compra_enum as enum ('pendiente', 'pagado', 'entregado');

create type tipo_documento_enum as enum ('acta', 'certificado', 'licencia', 'foto', 'video', 'otro');

create type entidad_documento_enum as enum ('pelea', 'evento', 'boxeador', 'gimnasio');

create type estado_suscripcion_enum as enum ('activa', 'vencida', 'cancelada');

create type tipo_plan_enum as enum ('gimnasio', 'organizador');

create type tipo_comision_enum as enum ('fotografia', 'video', 'entrada', 'inscripcion');
