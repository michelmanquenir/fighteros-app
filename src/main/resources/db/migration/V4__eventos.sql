-- =====================================================================
-- 03. EVENTOS Y LIGAS
-- Módulos: 4 (Organización de Eventos), 9 (Ligas), 23 (Perfil del Evento)
-- =====================================================================

create table public.eventos (
  id uuid primary key default gen_random_uuid(),
  organizador_id uuid not null references public.usuarios(id),
  liga_id uuid references public.ligas(id),
  nombre text not null,
  tipo tipo_evento_enum not null,
  fecha date not null,
  lugar text,
  region_id smallint references public.regiones(id),
  reglamento_url text,
  afiche_url text,
  cupos_totales integer,
  cantidad_publico integer,
  estado estado_evento_enum not null default 'planificado',
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now()
);

create trigger trg_eventos_updated_at
  before update on public.eventos
  for each row execute function public.set_updated_at();

create index idx_eventos_organizador on public.eventos(organizador_id);
create index idx_eventos_liga on public.eventos(liga_id);
create index idx_eventos_fecha on public.eventos(fecha);
create index idx_eventos_region on public.eventos(region_id);

-- Ahora que 'eventos' existe, completamos las referencias dejadas pendientes en 02_personas_deportivas.sql
alter table public.boxeador_medallas
  add constraint fk_boxeador_medallas_evento foreign key (evento_id) references public.eventos(id);

alter table public.boxeador_copas
  add constraint fk_boxeador_copas_evento foreign key (evento_id) references public.eventos(id);

create table public.evento_rings (
  id uuid primary key default gen_random_uuid(),
  evento_id uuid not null references public.eventos(id) on delete cascade,
  nombre text not null
);

create index idx_evento_rings_evento on public.evento_rings(evento_id);

create table public.evento_categorias (
  evento_id uuid not null references public.eventos(id) on delete cascade,
  categoria_id uuid not null references public.categorias_peso(id),
  cupos integer,
  primary key (evento_id, categoria_id)
);

create table public.evento_premios (
  id uuid primary key default gen_random_uuid(),
  evento_id uuid not null references public.eventos(id) on delete cascade,
  tipo tipo_premio_enum not null,
  categoria_id uuid references public.categorias_peso(id),
  puesto smallint,
  descripcion text
);

create index idx_evento_premios_evento on public.evento_premios(evento_id);

create table public.evento_patrocinadores (
  id uuid primary key default gen_random_uuid(),
  evento_id uuid not null references public.eventos(id) on delete cascade,
  nombre text not null,
  logo_url text,
  nivel text
);

create index idx_evento_patrocinadores_evento on public.evento_patrocinadores(evento_id);

create table public.evento_arbitros (
  evento_id uuid not null references public.eventos(id) on delete cascade,
  arbitro_id uuid not null references public.arbitros(id),
  primary key (evento_id, arbitro_id)
);

create table public.evento_jueces (
  evento_id uuid not null references public.eventos(id) on delete cascade,
  juez_id uuid not null references public.jueces(id),
  primary key (evento_id, juez_id)
);

create table public.evento_gimnasios_asistentes (
  evento_id uuid not null references public.eventos(id) on delete cascade,
  gimnasio_id uuid not null references public.gimnasios(id),
  primary key (evento_id, gimnasio_id)
);

-- Convocatorias del calendario nacional (Módulo 14)
create table public.convocatorias (
  id uuid primary key default gen_random_uuid(),
  evento_id uuid not null references public.eventos(id) on delete cascade,
  gimnasio_id uuid references public.gimnasios(id),
  boxeador_id uuid references public.boxeadores(id),
  mensaje text,
  fecha_envio timestamptz not null default now()
);

create index idx_convocatorias_evento on public.convocatorias(evento_id);

-- Campeones vigentes por liga y categoría (Módulo 9)
create table public.liga_campeones (
  id uuid primary key default gen_random_uuid(),
  liga_id uuid not null references public.ligas(id) on delete cascade,
  categoria_id uuid not null references public.categorias_peso(id),
  boxeador_id uuid not null references public.boxeadores(id),
  fecha_obtenido date not null,
  vigente boolean not null default true,
  created_at timestamptz not null default now()
);

create index idx_liga_campeones_liga on public.liga_campeones(liga_id);
create unique index uq_liga_campeon_vigente
  on public.liga_campeones(liga_id, categoria_id)
  where vigente;
