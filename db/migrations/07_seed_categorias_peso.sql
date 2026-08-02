-- =====================================================================
-- 07. SEED — Categorías de peso amateur estándar (Módulo 1)
-- =====================================================================

insert into public.categorias_peso (nombre, sexo, peso_min, peso_max) values
  -- Hombres
  ('Minimosca', 'M', 0, 48),
  ('Mosca', 'M', 48, 51),
  ('Gallo', 'M', 51, 54),
  ('Pluma', 'M', 54, 57),
  ('Liviano', 'M', 57, 60),
  ('Wélter Ligero', 'M', 60, 63.5),
  ('Wélter', 'M', 63.5, 67),
  ('Mediano', 'M', 67, 71),
  ('Semipesado', 'M', 71, 75),
  ('Pesado Ligero', 'M', 75, 80),
  ('Pesado', 'M', 80, 92),
  ('Superpesado', 'M', 92, 999),
  -- Mujeres
  ('Minimosca', 'F', 0, 48),
  ('Mosca', 'F', 48, 50),
  ('Gallo', 'F', 50, 52),
  ('Pluma', 'F', 52, 54),
  ('Liviano', 'F', 54, 57),
  ('Wélter Ligero', 'F', 57, 60),
  ('Wélter', 'F', 60, 63),
  ('Mediano', 'F', 63, 66),
  ('Semipesado', 'F', 66, 70),
  ('Pesado', 'F', 70, 81),
  ('Superpesado', 'F', 81, 999);
