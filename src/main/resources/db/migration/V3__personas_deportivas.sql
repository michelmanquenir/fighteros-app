-- =====================================================================
-- 02. PERSONAS DEPORTIVAS
-- Módulos: 1 (Boxeadores), 2 (Entrenador), 10 (Árbitros), 11 (Jueces),
--          12 (Control Médico), 15 (Fotógrafos), 17 (Productoras)
-- =====================================================================

-- ---------------------------------------------------------------------
-- Categorías de peso (catálogo)
-- ---------------------------------------------------------------------
create table public.categorias_peso (
  id uuid primary key default gen_random_uuid(),
  nombre text not null,
  sexo sexo_enum not null,
  peso_min numeric(5,2) not null,
  peso_max numeric(5,2) not null,
  unique (nombre, sexo)
);

-- ---------------------------------------------------------------------
-- Entrenadores
-- ---------------------------------------------------------------------
create table public.entrenadores (
  id uuid primary key references public.usuarios(id),
  gimnasio_id uuid references public.gimnasios(id),
  licencia text,
  especialidad text,
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now()
);

create trigger trg_entrenadores_updated_at
  before update on public.entrenadores
  for each row execute function public.set_updated_at();

create index idx_entrenadores_gimnasio on public.entrenadores(gimnasio_id);

create table public.disponibilidad_entrenador (
  id uuid primary key default gen_random_uuid(),
  entrenador_id uuid not null references public.entrenadores(id) on delete cascade,
  fecha_desde date not null,
  fecha_hasta date not null,
  disponible boolean not null default true,
  check (fecha_hasta >= fecha_desde)
);

create index idx_disponibilidad_entrenador on public.disponibilidad_entrenador(entrenador_id);

-- ---------------------------------------------------------------------
-- Árbitros y Jueces (Módulos 10 y 11)
-- ---------------------------------------------------------------------
create table public.arbitros (
  id uuid primary key references public.usuarios(id),
  licencia text,
  asociacion text,
  disponibilidad jsonb not null default '{}'::jsonb,
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now()
);

create trigger trg_arbitros_updated_at
  before update on public.arbitros
  for each row execute function public.set_updated_at();

create table public.jueces (
  id uuid primary key references public.usuarios(id),
  licencia text,
  asociacion text,
  modalidad_preferida modalidad_juez_enum not null default 'digital',
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now()
);

create trigger trg_jueces_updated_at
  before update on public.jueces
  for each row execute function public.set_updated_at();

-- ---------------------------------------------------------------------
-- Fotógrafos y Productoras audiovisuales (Módulos 15 y 17)
-- ---------------------------------------------------------------------
create table public.fotografos (
  id uuid primary key references public.usuarios(id),
  portafolio_url text,
  tarifas jsonb not null default '{}'::jsonb,
  disponibilidad jsonb not null default '{}'::jsonb,
  calificacion_promedio numeric(2,1) not null default 0,
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now()
);

create trigger trg_fotografos_updated_at
  before update on public.fotografos
  for each row execute function public.set_updated_at();

create table public.productoras_audiovisuales (
  id uuid primary key references public.usuarios(id),
  nombre_empresa text not null,
  servicios jsonb not null default '{}'::jsonb,
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now()
);

create trigger trg_productoras_updated_at
  before update on public.productoras_audiovisuales
  for each row execute function public.set_updated_at();

-- ---------------------------------------------------------------------
-- Boxeadores (Módulo 1)
-- ---------------------------------------------------------------------
create table public.boxeadores (
  id uuid primary key references public.usuarios(id),
  foto_url text,
  rut text not null unique check (rut ~ '^[0-9]{7,8}-[0-9Kk]$'),
  fecha_nacimiento date not null,
  sexo sexo_enum not null,
  peso_actual numeric(5,2),
  peso_habitual numeric(5,2),
  categoria_id uuid references public.categorias_peso(id),
  gimnasio_id uuid references public.gimnasios(id),
  entrenador_id uuid references public.entrenadores(id),
  region_id smallint references public.regiones(id),
  estado_deportivo estado_deportivo_enum not null default 'activo',
  nivel_progresion nivel_progresion_enum not null default 'debutante',
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now()
);

create trigger trg_boxeadores_updated_at
  before update on public.boxeadores
  for each row execute function public.set_updated_at();

create index idx_boxeadores_gimnasio on public.boxeadores(gimnasio_id);
create index idx_boxeadores_entrenador on public.boxeadores(entrenador_id);
create index idx_boxeadores_categoria on public.boxeadores(categoria_id);
create index idx_boxeadores_region on public.boxeadores(region_id);

-- Pesos pactados aceptados (historial)
create table public.boxeador_pesos_pactados (
  id uuid primary key default gen_random_uuid(),
  boxeador_id uuid not null references public.boxeadores(id) on delete cascade,
  peso_pactado numeric(5,2) not null,
  created_at timestamptz not null default now()
);

create index idx_pesos_pactados_boxeador on public.boxeador_pesos_pactados(boxeador_id);

create table public.boxeador_medallas (
  id uuid primary key default gen_random_uuid(),
  boxeador_id uuid not null references public.boxeadores(id) on delete cascade,
  evento_id uuid,
  tipo tipo_medalla_enum not null,
  nombre text not null,
  fecha date not null,
  descripcion text,
  created_at timestamptz not null default now()
);

create index idx_boxeador_medallas_boxeador on public.boxeador_medallas(boxeador_id);

create table public.boxeador_copas (
  id uuid primary key default gen_random_uuid(),
  boxeador_id uuid not null references public.boxeadores(id) on delete cascade,
  evento_id uuid,
  nombre text not null,
  fecha date not null,
  created_at timestamptz not null default now()
);

create index idx_boxeador_copas_boxeador on public.boxeador_copas(boxeador_id);

create table public.boxeador_campeonatos (
  id uuid primary key default gen_random_uuid(),
  boxeador_id uuid not null references public.boxeadores(id) on delete cascade,
  liga_id uuid references public.ligas(id),
  categoria_id uuid references public.categorias_peso(id),
  titulo text not null,
  fecha_obtenido date not null,
  vigente boolean not null default true,
  created_at timestamptz not null default now()
);

create index idx_boxeador_campeonatos_boxeador on public.boxeador_campeonatos(boxeador_id);

-- Patrocinios (futuro, ya modelado)
create table public.boxeador_patrocinios (
  id uuid primary key default gen_random_uuid(),
  boxeador_id uuid not null references public.boxeadores(id) on delete cascade,
  patrocinador_nombre text not null,
  descripcion text,
  fecha_inicio date not null,
  fecha_fin date,
  created_at timestamptz not null default now()
);

create index idx_boxeador_patrocinios_boxeador on public.boxeador_patrocinios(boxeador_id);

-- ---------------------------------------------------------------------
-- Gestión deportiva del entrenador (Módulo 2)
-- ---------------------------------------------------------------------
create table public.fichas_deportivas (
  id uuid primary key default gen_random_uuid(),
  boxeador_id uuid not null references public.boxeadores(id) on delete cascade,
  entrenador_id uuid not null references public.entrenadores(id),
  fecha date not null default current_date,
  notas text,
  created_at timestamptz not null default now()
);

create index idx_fichas_deportivas_boxeador on public.fichas_deportivas(boxeador_id);

create table public.registro_evolucion (
  id uuid primary key default gen_random_uuid(),
  boxeador_id uuid not null references public.boxeadores(id) on delete cascade,
  fecha date not null default current_date,
  peso numeric(5,2),
  progreso_notas text,
  created_at timestamptz not null default now()
);

create index idx_registro_evolucion_boxeador on public.registro_evolucion(boxeador_id);

-- ---------------------------------------------------------------------
-- Control médico y licencias (Módulo 12)
-- ---------------------------------------------------------------------
create table public.certificados_medicos (
  id uuid primary key default gen_random_uuid(),
  boxeador_id uuid not null references public.boxeadores(id) on delete cascade,
  tipo text not null,
  fecha_emision date not null,
  fecha_vencimiento date not null,
  archivo_url text,
  estado estado_certificado_enum not null default 'vigente',
  created_at timestamptz not null default now(),
  check (fecha_vencimiento >= fecha_emision)
);

create index idx_certificados_medicos_boxeador on public.certificados_medicos(boxeador_id);
create index idx_certificados_medicos_vencimiento on public.certificados_medicos(fecha_vencimiento);

create table public.suspensiones (
  id uuid primary key default gen_random_uuid(),
  boxeador_id uuid not null references public.boxeadores(id) on delete cascade,
  motivo text not null,
  fecha_inicio date not null,
  fecha_fin date,
  activa boolean not null default true,
  created_at timestamptz not null default now()
);

create index idx_suspensiones_boxeador on public.suspensiones(boxeador_id);

-- Licencias genéricas (boxeador, entrenador, árbitro, juez)
create table public.licencias (
  id uuid primary key default gen_random_uuid(),
  usuario_id uuid not null references public.usuarios(id) on delete cascade,
  tipo text not null,
  numero text,
  entidad_emisora text,
  fecha_emision date not null,
  fecha_vencimiento date,
  archivo_url text,
  created_at timestamptz not null default now()
);

create index idx_licencias_usuario on public.licencias(usuario_id);
