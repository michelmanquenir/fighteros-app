-- =====================================================================
-- 04. EMPAREJAMIENTOS, PELEAS OFICIALES, REEMPLAZOS, PESAJE Y RANKINGS
-- Módulos: 5 (Emparejamientos), 6 (Reemplazos), 7 (Registro Oficial de
--          Peleas), 8 (Rankings), 10/11 (acciones árbitro/tarjetas), 13 (Pesaje)
-- =====================================================================

create type estado_pelea_enum as enum ('programada', 'realizada', 'cancelada');

-- ---------------------------------------------------------------------
-- Módulo 5: Emparejamientos inteligentes (propuestas de pelea)
-- ---------------------------------------------------------------------
create table public.solicitudes_pelea (
  id uuid primary key default gen_random_uuid(),
  boxeador_a_id uuid not null references public.boxeadores(id),
  boxeador_b_id uuid references public.boxeadores(id),
  entrenador_solicitante_id uuid not null references public.entrenadores(id),
  categoria_id uuid references public.categorias_peso(id),
  peso_pactado numeric(5,2),
  region_id smallint references public.regiones(id),
  evento_id uuid references public.eventos(id),
  estado estado_solicitud_enum not null default 'pendiente',
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now(),
  check (boxeador_b_id is null or boxeador_b_id <> boxeador_a_id)
);

create trigger trg_solicitudes_pelea_updated_at
  before update on public.solicitudes_pelea
  for each row execute function public.set_updated_at();

create index idx_solicitudes_pelea_boxeador_a on public.solicitudes_pelea(boxeador_a_id);
create index idx_solicitudes_pelea_boxeador_b on public.solicitudes_pelea(boxeador_b_id);
create index idx_solicitudes_pelea_estado on public.solicitudes_pelea(estado);

-- ---------------------------------------------------------------------
-- Módulo 7: Registro oficial de peleas
-- ---------------------------------------------------------------------
create table public.peleas (
  id uuid primary key default gen_random_uuid(),
  evento_id uuid not null references public.eventos(id),
  solicitud_pelea_id uuid references public.solicitudes_pelea(id),
  boxeador_a_id uuid not null references public.boxeadores(id),
  boxeador_b_id uuid not null references public.boxeadores(id),
  categoria_id uuid references public.categorias_peso(id),
  peso_pactado numeric(5,2),
  ring_id uuid references public.evento_rings(id),
  fecha timestamptz,
  arbitro_id uuid references public.arbitros(id),
  estado estado_pelea_enum not null default 'programada',
  resultado resultado_pelea_enum,
  metodo_victoria metodo_victoria_enum,
  round_final smallint,
  estado_validacion estado_validacion_enum not null default 'pendiente',
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now(),
  check (boxeador_b_id <> boxeador_a_id)
);

create trigger trg_peleas_updated_at
  before update on public.peleas
  for each row execute function public.set_updated_at();

create index idx_peleas_evento on public.peleas(evento_id);
create index idx_peleas_boxeador_a on public.peleas(boxeador_a_id);
create index idx_peleas_boxeador_b on public.peleas(boxeador_b_id);
create index idx_peleas_arbitro on public.peleas(arbitro_id);

create table public.pelea_jueces (
  pelea_id uuid not null references public.peleas(id) on delete cascade,
  juez_id uuid not null references public.jueces(id),
  primary key (pelea_id, juez_id)
);

-- Validación oficial: Entrenador A, Entrenador B, Organizador, Árbitro
create table public.pelea_validaciones (
  id uuid primary key default gen_random_uuid(),
  pelea_id uuid not null references public.peleas(id) on delete cascade,
  validador_id uuid not null references public.usuarios(id),
  rol_validador rol_validador_enum not null,
  aprobado boolean,
  comentario text,
  fecha timestamptz not null default now(),
  unique (pelea_id, rol_validador)
);

create index idx_pelea_validaciones_pelea on public.pelea_validaciones(pelea_id);

create table public.pelea_acciones_arbitro (
  id uuid primary key default gen_random_uuid(),
  pelea_id uuid not null references public.peleas(id) on delete cascade,
  tipo tipo_accion_arbitro_enum not null,
  round smallint,
  descripcion text,
  created_at timestamptz not null default now()
);

create index idx_pelea_acciones_arbitro_pelea on public.pelea_acciones_arbitro(pelea_id);

-- Tarjetas de jueces — digitales o escaneadas en papel vía OCR (Módulo 11)
create table public.tarjetas_jueces (
  id uuid primary key default gen_random_uuid(),
  pelea_id uuid not null references public.peleas(id) on delete cascade,
  juez_id uuid not null references public.jueces(id),
  round smallint not null,
  puntos_boxeador_a smallint not null,
  puntos_boxeador_b smallint not null,
  modalidad modalidad_juez_enum not null default 'digital',
  foto_tarjeta_url text,
  created_at timestamptz not null default now(),
  unique (pelea_id, juez_id, round)
);

create index idx_tarjetas_jueces_pelea on public.tarjetas_jueces(pelea_id);

-- ---------------------------------------------------------------------
-- Módulo 6: Gestión de reemplazos
-- ---------------------------------------------------------------------
create table public.reemplazos (
  id uuid primary key default gen_random_uuid(),
  pelea_id uuid not null references public.peleas(id) on delete cascade,
  boxeador_retirado_id uuid not null references public.boxeadores(id),
  motivo text,
  estado estado_reemplazo_enum not null default 'buscando',
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now()
);

create trigger trg_reemplazos_updated_at
  before update on public.reemplazos
  for each row execute function public.set_updated_at();

create index idx_reemplazos_pelea on public.reemplazos(pelea_id);

create table public.reemplazo_invitaciones (
  id uuid primary key default gen_random_uuid(),
  reemplazo_id uuid not null references public.reemplazos(id) on delete cascade,
  gimnasio_id uuid references public.gimnasios(id),
  entrenador_id uuid references public.entrenadores(id),
  estado estado_solicitud_enum not null default 'pendiente',
  created_at timestamptz not null default now()
);

create index idx_reemplazo_invitaciones_reemplazo on public.reemplazo_invitaciones(reemplazo_id);

-- ---------------------------------------------------------------------
-- Módulo 13: Pesaje
-- ---------------------------------------------------------------------
create table public.pesajes (
  id uuid primary key default gen_random_uuid(),
  evento_id uuid not null references public.eventos(id),
  pelea_id uuid references public.peleas(id),
  boxeador_id uuid not null references public.boxeadores(id),
  peso numeric(5,2) not null,
  hora timestamptz not null default now(),
  foto_url text,
  estado estado_pesaje_enum not null default 'pendiente',
  created_at timestamptz not null default now()
);

create index idx_pesajes_evento on public.pesajes(evento_id);
create index idx_pesajes_boxeador on public.pesajes(boxeador_id);

-- ---------------------------------------------------------------------
-- Módulo 8: Rankings
-- ---------------------------------------------------------------------
create table public.rankings (
  id uuid primary key default gen_random_uuid(),
  boxeador_id uuid not null references public.boxeadores(id) on delete cascade,
  categoria_id uuid not null references public.categorias_peso(id),
  region_id smallint references public.regiones(id),
  liga_id uuid references public.ligas(id),
  puntos numeric(8,2) not null default 0,
  posicion integer,
  actualizado_en timestamptz not null default now(),
  unique (boxeador_id, categoria_id, region_id, liga_id)
);

create index idx_rankings_categoria on public.rankings(categoria_id);
create index idx_rankings_region on public.rankings(region_id);
create index idx_rankings_liga on public.rankings(liga_id);
