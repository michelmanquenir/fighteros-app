# Esquema de base de datos — Fighteros

Scripts SQL para crear el esquema completo en Supabase (Postgres), cubriendo los 23 módulos de la plataforma. Se ejecutan **manualmente** en el SQL Editor de Supabase (se probó Flyway para automatizarlo, pero se revirtió — ver nota al final).

## ⚠️ Antes de correrlos: limpia el schema `public`

Se intentó correr estos scripts automáticamente vía Flyway y quedó a medio camino — como mínimo el tipo `sexo_enum` (y posiblemente otros objetos de `V1__extensions_types.sql`) ya existen en tu base de Supabase. Si corres `00_extensions_types.sql` tal cual, vas a chocar con errores `type "..." already exists`.

Antes de empezar, en el SQL Editor de Supabase ejecuta esto para dejar `public` completamente limpio:

```sql
drop schema public cascade;
create schema public;
grant all on schema public to postgres;
grant all on schema public to public;
```

Esto borra **todo** lo que haya en el schema `public` (los objetos parciales de Flyway y cualquier otra cosa que hayas creado ahí). Como el proyecto es nuevo y sin datos reales todavía, es seguro. Después de esto, el schema queda como recién creado y puedes correr los 7 scripts desde cero sin conflictos.

## Cómo aplicarlos

En el **SQL Editor** de tu proyecto Supabase, ejecuta los archivos **en orden** (cada uno depende de tablas creadas en el anterior):

1. `00_extensions_types.sql` — tipos ENUM y función de trigger `updated_at`
2. `01_identidad.sql` — regiones, usuarios, roles, ligas, gimnasios
3. `02_personas_deportivas.sql` — categorías de peso, entrenadores, árbitros, jueces, fotógrafos, productoras, boxeadores, fichas, control médico
4. `03_eventos.sql` — eventos, rings, premios, patrocinadores, campeones de liga
5. `04_emparejamientos_peleas.sql` — solicitudes de pelea, peleas oficiales, validaciones, reemplazos, pesaje, rankings
6. `05_multimedia_documentos_monetizacion.sql` — multimedia, marketplace, documentos, inscripciones, pagos, suscripciones
7. `06_vistas.sql` — vistas de estadísticas y perfiles públicos derivados

Puedes pegar cada archivo completo y ejecutar (`Run`) uno por uno, o concatenarlos en un solo script respetando el orden.

## Decisiones de diseño

- **No usa `auth.users` de Supabase ni RLS.** El backend Spring Boot maneja su propio JWT y se conecta directo vía JDBC/Hikari con un rol privilegiado, sin pasar por PostgREST. Por eso existe una tabla `usuarios` propia con `password_hash`, en vez de apoyarse en el sistema de Auth de Supabase.
- **RUT**: se valida solo el *formato* (`########-#`) vía `check`. El dígito verificador debe validarse en la capa de aplicación (Java).
- **UUID nativos**: se usa `gen_random_uuid()`, disponible de forma nativa desde Postgres 13 (confirmado Postgres 17.6 en tu instancia de Supabase), sin necesitar la extensión `pgcrypto`.
- **Estadísticas como vistas, no tablas**: récord, victorias/derrotas, campeones por gimnasio, etc. se calculan on-the-fly en `06_vistas.sql`.
- **Relaciones "compartiendo PK"**: `boxeadores`, `entrenadores`, `arbitros`, `jueces`, `fotografos`, `productoras_audiovisuales` usan como `id` el mismo `id` de `usuarios` (1 a 1).

## ⚠️ Importante: `spring.jpa.hibernate.ddl-auto=validate`

`application.properties` tiene `spring.jpa.hibernate.ddl-auto=validate`. Esto significa que **la aplicación no arrancará hasta que hayas corrido estos 7 scripts** — Hibernate valida que las tablas existan y coincidan con las entidades JPA, pero no las crea ni las modifica. Corre las migraciones primero, después haz deploy.

## Nota: por qué no quedó con Flyway

Se integró Flyway para correr estas migraciones automáticamente en cada deploy de Render, pero Supabase entrega el schema `public` con algunos objetos propios de la plataforma (no vacío), lo que complicó el baseline y terminó ejecutando `V1` dos veces en intentos fallidos, dejando el schema a medias. Se revirtió a ejecución manual por simplicidad. Si más adelante se quiere retomar la automatización, la clave es partir de un schema `public` completamente limpio antes de habilitar Flyway con `baseline-on-migrate=true` y `baseline-version=0`.
