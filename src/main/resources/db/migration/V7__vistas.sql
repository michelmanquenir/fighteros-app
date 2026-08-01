-- =====================================================================
-- 06. VISTAS DERIVADAS
-- Módulos: 19 (Perfil Público), 20 (Estadísticas), 23 (Resultados en vivo),
--          12 (Alertas de vencimiento)
-- =====================================================================

-- Estadísticas de boxeador (Módulo 20)
create view public.v_boxeador_estadisticas as
select
  b.id as boxeador_id,
  count(p.id) filter (where p.estado = 'realizada') as peleas_totales,
  count(p.id) filter (
    where p.estado = 'realizada' and (
      (p.boxeador_a_id = b.id and p.resultado = 'victoria_a') or
      (p.boxeador_b_id = b.id and p.resultado = 'victoria_b')
    )
  ) as victorias,
  count(p.id) filter (
    where p.estado = 'realizada' and (
      (p.boxeador_a_id = b.id and p.resultado = 'victoria_b') or
      (p.boxeador_b_id = b.id and p.resultado = 'victoria_a')
    )
  ) as derrotas,
  count(p.id) filter (where p.estado = 'realizada' and p.resultado = 'empate') as empates,
  count(p.id) filter (
    where p.metodo_victoria = 'ko' and (
      (p.boxeador_a_id = b.id and p.resultado = 'victoria_a') or
      (p.boxeador_b_id = b.id and p.resultado = 'victoria_b')
    )
  ) as victorias_ko,
  count(p.id) filter (
    where p.metodo_victoria in ('decision_unanime', 'decision_dividida') and (
      (p.boxeador_a_id = b.id and p.resultado = 'victoria_a') or
      (p.boxeador_b_id = b.id and p.resultado = 'victoria_b')
    )
  ) as victorias_decision,
  max(p.fecha) filter (where p.estado = 'realizada') as ultima_pelea
from public.boxeadores b
left join public.peleas p
  on p.estado = 'realizada' and (p.boxeador_a_id = b.id or p.boxeador_b_id = b.id)
group by b.id;

-- Estadísticas de gimnasio (Módulo 20)
create view public.v_gimnasio_estadisticas as
with boxeadores_gimnasio as (
  select id, gimnasio_id from public.boxeadores where gimnasio_id is not null
),
record as (
  select bg.gimnasio_id,
         sum(e.victorias) as victorias,
         sum(e.derrotas) as derrotas,
         sum(e.empates) as empates
  from boxeadores_gimnasio bg
  join public.v_boxeador_estadisticas e on e.boxeador_id = bg.id
  group by bg.gimnasio_id
),
campeones as (
  select bg.gimnasio_id, count(*) as campeones_vigentes
  from boxeadores_gimnasio bg
  join public.liga_campeones lc on lc.boxeador_id = bg.id and lc.vigente
  group by bg.gimnasio_id
),
medallas as (
  select bg.gimnasio_id, count(*) as total_medallas
  from boxeadores_gimnasio bg
  join public.boxeador_medallas m on m.boxeador_id = bg.id
  group by bg.gimnasio_id
),
copas as (
  select bg.gimnasio_id, count(*) as total_copas
  from boxeadores_gimnasio bg
  join public.boxeador_copas c on c.boxeador_id = bg.id
  group by bg.gimnasio_id
)
select
  g.id as gimnasio_id,
  coalesce(r.victorias, 0) as victorias,
  coalesce(r.derrotas, 0) as derrotas,
  coalesce(r.empates, 0) as empates,
  coalesce(c.campeones_vigentes, 0) as campeones_vigentes,
  coalesce(m.total_medallas, 0) as total_medallas,
  coalesce(cp.total_copas, 0) as total_copas
from public.gimnasios g
left join record r on r.gimnasio_id = g.id
left join campeones c on c.gimnasio_id = g.id
left join medallas m on m.gimnasio_id = g.id
left join copas cp on cp.gimnasio_id = g.id;

-- Estadísticas de organizador (Módulo 20)
create view public.v_organizador_estadisticas as
select
  e.organizador_id,
  count(distinct e.id) filter (where e.estado = 'finalizado') as eventos_realizados,
  coalesce(sum(e.cantidad_publico) filter (where e.estado = 'finalizado'), 0) as publico_total,
  count(distinct p.id) as combates_totales,
  count(distinct i.boxeador_id) as participantes_totales
from public.eventos e
left join public.peleas p on p.evento_id = e.id
left join public.inscripciones i on i.evento_id = e.id
group by e.organizador_id;

-- Perfil público del deportista (Módulo 19)
create view public.v_perfil_publico_boxeador as
select
  b.id as boxeador_id,
  u.nombre,
  b.foto_url,
  b.sexo,
  b.peso_actual,
  cp.nombre as categoria,
  b.estado_deportivo,
  b.nivel_progresion,
  g.nombre as gimnasio,
  r.nombre as region,
  e.victorias,
  e.derrotas,
  e.empates,
  e.victorias_ko,
  e.peleas_totales,
  e.ultima_pelea
from public.boxeadores b
join public.usuarios u on u.id = b.id
left join public.categorias_peso cp on cp.id = b.categoria_id
left join public.gimnasios g on g.id = b.gimnasio_id
left join public.regiones r on r.id = b.region_id
left join public.v_boxeador_estadisticas e on e.boxeador_id = b.id;

-- Resultados en tiempo real de un evento (Módulo 23)
create view public.v_evento_resultados as
select
  p.evento_id,
  p.id as pelea_id,
  ua.nombre as boxeador_a,
  ub.nombre as boxeador_b,
  p.categoria_id,
  p.estado,
  p.resultado,
  p.metodo_victoria,
  p.round_final,
  p.fecha
from public.peleas p
join public.usuarios ua on ua.id = p.boxeador_a_id
join public.usuarios ub on ub.id = p.boxeador_b_id;

-- Alertas de vencimiento de certificados médicos (Módulo 12)
create view public.v_certificados_por_vencer as
select *
from public.certificados_medicos
where estado = 'vigente'
  and fecha_vencimiento <= current_date + interval '30 days';
