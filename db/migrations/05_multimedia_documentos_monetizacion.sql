-- =====================================================================
-- 05. MULTIMEDIA, MARKETPLACE, DOCUMENTACIÓN Y MONETIZACIÓN
-- Módulos: 2 (adm./multimedia), 16 (Marketplace), 17 (Productoras),
--          18 (Biblioteca), 21 (Gestión Documental), 22 (Monetización),
--          14 (notificaciones de calendario)
-- =====================================================================

create type entidad_publicidad_enum as enum ('evento', 'gimnasio', 'plataforma');

-- ---------------------------------------------------------------------
-- Biblioteca multimedia (Módulo 18) — respaldo de fotos/videos oficiales
-- ---------------------------------------------------------------------
create table public.multimedia (
  id uuid primary key default gen_random_uuid(),
  tipo tipo_multimedia_enum not null,
  url text not null,
  pelea_id uuid references public.peleas(id),
  evento_id uuid references public.eventos(id),
  boxeador_id uuid references public.boxeadores(id),
  fotografo_id uuid references public.fotografos(id),
  productora_id uuid references public.productoras_audiovisuales(id),
  es_oficial boolean not null default false,
  precio numeric(10,2),
  created_at timestamptz not null default now()
);

create index idx_multimedia_pelea on public.multimedia(pelea_id);
create index idx_multimedia_evento on public.multimedia(evento_id);
create index idx_multimedia_boxeador on public.multimedia(boxeador_id);

-- Marketplace fotográfico (Módulo 16)
create table public.multimedia_compras (
  id uuid primary key default gen_random_uuid(),
  multimedia_id uuid not null references public.multimedia(id),
  comprador_id uuid not null references public.usuarios(id),
  tipo_compra tipo_compra_multimedia_enum not null,
  precio numeric(10,2) not null,
  comision_plataforma numeric(10,2) not null default 0,
  estado estado_compra_enum not null default 'pendiente',
  created_at timestamptz not null default now()
);

create index idx_multimedia_compras_comprador on public.multimedia_compras(comprador_id);
create index idx_multimedia_compras_multimedia on public.multimedia_compras(multimedia_id);

-- ---------------------------------------------------------------------
-- Gestión documental (Módulo 21) — relación polimórfica simple vía tipo+id
-- ---------------------------------------------------------------------
create table public.documentos (
  id uuid primary key default gen_random_uuid(),
  propietario_id uuid not null references public.usuarios(id),
  tipo tipo_documento_enum not null,
  entidad_relacionada entidad_documento_enum not null,
  entidad_id uuid not null,
  url text not null,
  created_at timestamptz not null default now()
);

create index idx_documentos_propietario on public.documentos(propietario_id);
create index idx_documentos_entidad on public.documentos(entidad_relacionada, entidad_id);

-- ---------------------------------------------------------------------
-- Inscripciones y pagos (Módulo 2 — gestión administrativa)
-- ---------------------------------------------------------------------
create table public.inscripciones (
  id uuid primary key default gen_random_uuid(),
  boxeador_id uuid not null references public.boxeadores(id),
  evento_id uuid not null references public.eventos(id),
  fecha_inscripcion timestamptz not null default now(),
  estado estado_inscripcion_enum not null default 'pendiente',
  created_at timestamptz not null default now(),
  unique (boxeador_id, evento_id)
);

create index idx_inscripciones_evento on public.inscripciones(evento_id);

create table public.pagos (
  id uuid primary key default gen_random_uuid(),
  usuario_id uuid not null references public.usuarios(id),
  concepto text not null,
  monto numeric(10,2) not null,
  moneda text not null default 'CLP',
  estado estado_pago_enum not null default 'pendiente',
  referencia_externa text,
  created_at timestamptz not null default now()
);

create index idx_pagos_usuario on public.pagos(usuario_id);

-- ---------------------------------------------------------------------
-- Monetización (Módulo 22)
-- ---------------------------------------------------------------------
create table public.planes_suscripcion (
  id uuid primary key default gen_random_uuid(),
  nombre text not null,
  tipo tipo_plan_enum not null,
  precio numeric(10,2) not null,
  periodicidad text not null default 'mensual',
  beneficios jsonb not null default '{}'::jsonb
);

create table public.suscripciones (
  id uuid primary key default gen_random_uuid(),
  usuario_id uuid not null references public.usuarios(id),
  plan_id uuid not null references public.planes_suscripcion(id),
  fecha_inicio date not null default current_date,
  fecha_fin date,
  estado estado_suscripcion_enum not null default 'activa',
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now()
);

create trigger trg_suscripciones_updated_at
  before update on public.suscripciones
  for each row execute function public.set_updated_at();

create index idx_suscripciones_usuario on public.suscripciones(usuario_id);

create table public.comisiones (
  id uuid primary key default gen_random_uuid(),
  tipo tipo_comision_enum not null,
  porcentaje numeric(5,2) not null,
  vigente_desde date not null default current_date
);

create table public.entradas_venta (
  id uuid primary key default gen_random_uuid(),
  evento_id uuid not null references public.eventos(id),
  comprador_id uuid not null references public.usuarios(id),
  cantidad integer not null check (cantidad > 0),
  precio_unitario numeric(10,2) not null,
  total numeric(10,2) generated always as (cantidad * precio_unitario) stored,
  created_at timestamptz not null default now()
);

create index idx_entradas_venta_evento on public.entradas_venta(evento_id);

create table public.publicidad_sponsors (
  id uuid primary key default gen_random_uuid(),
  entidad_tipo entidad_publicidad_enum not null,
  entidad_id uuid,
  nombre_sponsor text not null,
  logo_url text,
  monto numeric(10,2),
  fecha_inicio date,
  fecha_fin date
);

-- ---------------------------------------------------------------------
-- Notificaciones (soporte transversal — Módulos 6 y 14)
-- ---------------------------------------------------------------------
create table public.notificaciones (
  id uuid primary key default gen_random_uuid(),
  usuario_id uuid not null references public.usuarios(id) on delete cascade,
  tipo text not null,
  titulo text not null,
  mensaje text,
  leido boolean not null default false,
  referencia_tipo text,
  referencia_id uuid,
  created_at timestamptz not null default now()
);

create index idx_notificaciones_usuario on public.notificaciones(usuario_id, leido);
