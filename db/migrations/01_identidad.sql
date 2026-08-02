-- =====================================================================
-- 01. IDENTIDAD BASE: regiones, usuarios, roles, ligas, gimnasios
-- Módulos: 3 (Perfil del Gimnasio), 9 (Ligas), soporte transversal
-- =====================================================================

-- ---------------------------------------------------------------------
-- Regiones (Chile)
-- ---------------------------------------------------------------------
create table public.regiones (
  id smallint primary key,
  nombre text not null unique
);

insert into public.regiones (id, nombre) values
  (1, 'Arica y Parinacota'), (2, 'Tarapacá'), (3, 'Antofagasta'),
  (4, 'Atacama'), (5, 'Coquimbo'), (6, 'Valparaíso'),
  (7, 'Metropolitana de Santiago'), (8, 'O''Higgins'), (9, 'Maule'),
  (10, 'Ñuble'), (11, 'Biobío'), (12, 'La Araucanía'),
  (13, 'Los Ríos'), (14, 'Los Lagos'), (15, 'Aysén'), (16, 'Magallanes');

-- ---------------------------------------------------------------------
-- Usuarios (cuenta base — el backend Spring gestiona el JWT contra esta tabla)
-- ---------------------------------------------------------------------
create table public.usuarios (
  id uuid primary key default gen_random_uuid(),
  nombre text not null,
  email text not null unique,
  password_hash text not null,
  telefono text,
  avatar_url text,
  region_id smallint references public.regiones(id),
  activo boolean not null default true,
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now()
);

create trigger trg_usuarios_updated_at
  before update on public.usuarios
  for each row execute function public.set_updated_at();

-- Un usuario puede tener varios roles simultáneos (ej: entrenador que también es organizador)
create table public.usuario_roles (
  usuario_id uuid not null references public.usuarios(id) on delete cascade,
  rol rol_usuario_enum not null,
  created_at timestamptz not null default now(),
  primary key (usuario_id, rol)
);

-- ---------------------------------------------------------------------
-- Ligas (Módulo 9)
-- ---------------------------------------------------------------------
create table public.ligas (
  id uuid primary key default gen_random_uuid(),
  nombre text not null,
  region_id smallint references public.regiones(id),
  descripcion text,
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now()
);

create trigger trg_ligas_updated_at
  before update on public.ligas
  for each row execute function public.set_updated_at();

-- ---------------------------------------------------------------------
-- Gimnasios (Módulo 3)
-- ---------------------------------------------------------------------
create table public.gimnasios (
  id uuid primary key default gen_random_uuid(),
  usuario_admin_id uuid not null references public.usuarios(id),
  nombre text not null,
  logo_url text,
  direccion text,
  region_id smallint references public.regiones(id),
  telefono text,
  email text,
  redes_sociales jsonb not null default '{}'::jsonb,
  descripcion text,
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now()
);

create trigger trg_gimnasios_updated_at
  before update on public.gimnasios
  for each row execute function public.set_updated_at();

create index idx_gimnasios_region on public.gimnasios(region_id);

create table public.gimnasio_galeria (
  id uuid primary key default gen_random_uuid(),
  gimnasio_id uuid not null references public.gimnasios(id) on delete cascade,
  tipo tipo_multimedia_enum not null,
  url text not null,
  created_at timestamptz not null default now()
);

create index idx_gimnasio_galeria_gimnasio on public.gimnasio_galeria(gimnasio_id);

create table public.gimnasio_instalaciones (
  id uuid primary key default gen_random_uuid(),
  gimnasio_id uuid not null references public.gimnasios(id) on delete cascade,
  descripcion text not null
);

create table public.gimnasio_resenas (
  id uuid primary key default gen_random_uuid(),
  gimnasio_id uuid not null references public.gimnasios(id) on delete cascade,
  usuario_id uuid not null references public.usuarios(id),
  calificacion smallint not null check (calificacion between 1 and 5),
  comentario text,
  created_at timestamptz not null default now(),
  unique (gimnasio_id, usuario_id)
);

create index idx_gimnasio_resenas_gimnasio on public.gimnasio_resenas(gimnasio_id);
