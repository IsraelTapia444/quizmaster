-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Servidor: 127.0.0.1:3306
-- Tiempo de generación: 09-02-2026 a las 15:35:09
-- Versión del servidor: 8.2.0
-- Versión de PHP: 8.3.0

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de datos: `quizmaster`
--
CREATE DATABASE IF NOT EXISTS `quizmaster` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
USE `quizmaster`;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `partidas`
--

CREATE TABLE `partidas` (
  `id` int NOT NULL,
  `usuario_id` int NOT NULL,
  `puntuacion` int NOT NULL,
  `fecha` timestamp NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Volcado de datos para la tabla `partidas`
--

INSERT INTO `partidas` (`id`, `usuario_id`, `puntuacion`, `fecha`) VALUES
(1, 1, 100, '2026-01-19 19:59:08'),
(2, 1, 120, '2026-02-01 09:00:00'),
(3, 1, 80, '2026-02-05 11:30:00'),
(4, 1, 150, '2026-02-07 17:45:00'),
(5, 2, 90, '2026-02-03 13:20:00'),
(6, 2, 110, '2026-02-06 15:10:00'),
(7, 2, 70, '2026-02-08 10:05:00'),
(8, 3, 130, '2026-02-02 08:15:00'),
(9, 3, 95, '2026-02-04 16:40:00'),
(10, 3, 160, '2026-02-08 18:00:00'),
(11, 4, 100, '2026-02-01 12:00:00'),
(12, 4, 140, '2026-02-05 19:10:00'),
(13, 4, 85, '2026-02-07 08:50:00'),
(14, 5, 75, '2026-02-02 10:30:00'),
(15, 5, 105, '2026-02-06 14:25:00'),
(16, 5, 95, '2026-02-08 17:10:00');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `preguntas`
--

CREATE TABLE `preguntas` (
  `id` int NOT NULL,
  `pregunta` text NOT NULL,
  `opcion1` varchar(255) NOT NULL,
  `opcion2` varchar(255) NOT NULL,
  `opcion3` varchar(255) NOT NULL,
  `opcion4` varchar(255) NOT NULL,
  `correcta` tinyint NOT NULL,
  `categoria` varchar(100) DEFAULT NULL,
  `dificultad` enum('facil','media','dificil') DEFAULT 'media'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Volcado de datos para la tabla `preguntas`
--

INSERT INTO `preguntas` (`id`, `pregunta`, `opcion1`, `opcion2`, `opcion3`, `opcion4`, `correcta`, `categoria`, `dificultad`) VALUES
(1, 'Cuanto pesa Israel', '80', '65', '20', '58', 2, 'Famosos', 'facil'),
(2, '¿Cuál es el planeta más cercano al Sol?', 'Venus', 'Mercurio', 'Marte', 'Tierra', 2, 'Ciencia', 'facil'),
(3, '¿Cuántos días tiene una semana?', '5', '6', '7', '8', 3, 'General', 'facil'),
(4, '¿De qué color es el cielo despejado?', 'Rojo', 'Azul', 'Verde', 'Amarillo', 2, 'General', 'facil'),
(5, '¿Cuánto es 2 + 2?', '3', '4', '5', '6', 2, 'Matemáticas', 'facil'),
(6, '¿Cuál es el animal más rápido?', 'Guepardo', 'León', 'Tigre', 'Antílope', 1, 'Animales', 'facil'),
(7, '¿Qué instrumento tiene teclas blancas y negras?', 'Guitarra', 'Piano', 'Violín', 'Flauta', 2, 'Música', 'facil'),
(8, '¿Cuál es el océano más grande?', 'Atlántico', 'Índico', 'Pacífico', 'Ártico', 3, 'Geografía', 'facil'),
(9, '¿Qué gas respiramos principalmente?', 'Oxígeno', 'Hidrógeno', 'Nitrógeno', 'Helio', 3, 'Ciencia', 'facil'),
(10, '¿Cuál es la capital de España?', 'Madrid', 'Barcelona', 'Sevilla', 'Valencia', 1, 'Geografía', 'facil'),
(11, '¿Cuántas patas tiene un perro?', '2', '3', '4', '5', 3, 'Animales', 'facil'),
(12, '¿En qué año llegó el hombre a la Luna?', '1965', '1969', '1972', '1959', 2, 'Historia', 'media'),
(13, '¿Cuál es el metal más ligero?', 'Hierro', 'Aluminio', 'Litio', 'Cobre', 3, 'Ciencia', 'media'),
(14, '¿Quién pintó La Última Cena?', 'Miguel Ángel', 'Leonardo da Vinci', 'Goya', 'Velázquez', 2, 'Arte', 'media'),
(15, '¿Cuál es el río más largo del mundo?', 'Nilo', 'Amazonas', 'Yangtsé', 'Danubio', 2, 'Geografía', 'media'),
(16, '¿Qué país inventó la pólvora?', 'India', 'China', 'Japón', 'Corea', 2, 'Historia', 'media'),
(17, '¿Cuál es la capital de Canadá?', 'Toronto', 'Vancouver', 'Ottawa', 'Montreal', 3, 'Geografía', 'media'),
(18, '¿Qué órgano bombea la sangre?', 'Pulmones', 'Hígado', 'Corazón', 'Riñones', 3, 'Ciencia', 'media'),
(19, '¿Cuántos jugadores hay en un equipo de fútbol?', '9', '10', '11', '12', 3, 'Deportes', 'media'),
(20, '¿Qué elemento tiene el símbolo O?', 'Oro', 'Osmio', 'Oxígeno', 'Plata', 3, 'Ciencia', 'media'),
(21, '¿Cuál es el idioma más hablado del mundo?', 'Inglés', 'Chino mandarín', 'Español', 'Hindi', 2, 'Cultura', 'media'),
(22, '¿Cuál es la distancia media entre la Tierra y el Sol?', '150 millones km', '200 millones km', '100 millones km', '250 millones km', 1, 'Ciencia', 'dificil'),
(23, '¿Quién escribió \"La montaña mágica\"?', 'Kafka', 'Thomas Mann', 'Hesse', 'Joyce', 2, 'Literatura', 'dificil'),
(24, '¿Cuál es el país con más islas del mundo?', 'Noruega', 'Suecia', 'Indonesia', 'Filipinas', 2, 'Geografía', 'dificil'),
(25, '¿Qué científico propuso la teoría del Big Bang?', 'Einstein', 'Lemaître', 'Hawking', 'Hubble', 2, 'Ciencia', 'dificil'),
(26, '¿Cuál es el hueso más pequeño del cuerpo humano?', 'Martillo', 'Estribo', 'Yunque', 'Falange', 2, 'Biología', 'dificil'),
(27, '¿En qué año cayó Constantinopla?', '1453', '1492', '1204', '1517', 1, 'Historia', 'dificil'),
(28, '¿Cuál es la capital de Mongolia?', 'Ulán Bator', 'Astana', 'Tashkent', 'Bishkek', 1, 'Geografía', 'dificil'),
(29, '¿Qué matemático resolvió el último teorema de Fermat?', 'Gauss', 'Euler', 'Andrew Wiles', 'Ramanujan', 3, 'Matemáticas', 'dificil'),
(30, '¿Cuál es el compuesto principal del sol?', 'Oxígeno', 'Hidrógeno', 'Helio', 'Carbono', 2, 'Ciencia', 'dificil'),
(31, '¿Qué país tiene la mayor reserva de petróleo?', 'Arabia Saudí', 'Venezuela', 'Irak', 'Rusia', 2, 'Economía', 'dificil');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `ranking`
--

CREATE TABLE `ranking` (
  `id` int NOT NULL,
  `usuario_id` int NOT NULL,
  `mejor_puntuacion` int NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Volcado de datos para la tabla `ranking`
--

INSERT INTO `ranking` (`id`, `usuario_id`, `mejor_puntuacion`) VALUES
(1, 1, 150),
(2, 2, 110),
(3, 3, 160),
(4, 4, 140),
(5, 5, 105);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `usuarios`
--

CREATE TABLE `usuarios` (
  `id` int NOT NULL,
  `nombre` varchar(100) NOT NULL,
  `email` varchar(150) NOT NULL,
  `password` varchar(255) NOT NULL,
  `fecha_registro` timestamp NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Volcado de datos para la tabla `usuarios`
--

INSERT INTO `usuarios` (`id`, `nombre`, `email`, `password`, `fecha_registro`) VALUES
(1, 'Israel', 'israeltapiagonz@gmail.com', '$2y$10$h5xHOG0A7SFirzbhX9IpO.6QR1iuHVk58eSDH3vgllxXvjWfCFKN2', '2026-01-19 19:57:18'),
(2, 'Test User', 'test@example.com', '$2y$10$AV5E.2DZtLomRVQvJqxm1e58d/i5MQhaXuu3K8wsF1p6j1CY8HF4C', '2026-02-02 18:50:09'),
(3, 'Aroa', 'aroa@gmail.com', '$2y$10$7LaTB3rlXigMVPD/CIlhI.1I7OZsklENZW5E4hCNtqJiPQtgPesyu', '2026-02-02 19:05:46'),
(4, 'Juan', 'juan@example.com', '$2y$10$Bmfq/ast0yxYXBZHXGtiOet6K2Bn897MJ.Tj.R2CiF.ktRMyarIWS', '2026-02-03 16:05:28'),
(5, 'Pepe', 'pepe@example.com', '$2y$10$pBi337XQpslLGGrQ4Zb.jOIiv/Xa2SzWiQI84pooGpL9ifYjPb6Si', '2026-02-04 15:32:27');

--
-- Índices para tablas volcadas
--

--
-- Indices de la tabla `partidas`
--
ALTER TABLE `partidas`
  ADD PRIMARY KEY (`id`),
  ADD KEY `usuario_id` (`usuario_id`);

--
-- Indices de la tabla `preguntas`
--
ALTER TABLE `preguntas`
  ADD PRIMARY KEY (`id`);

--
-- Indices de la tabla `ranking`
--
ALTER TABLE `ranking`
  ADD PRIMARY KEY (`id`),
  ADD KEY `usuario_id` (`usuario_id`);

--
-- Indices de la tabla `usuarios`
--
ALTER TABLE `usuarios`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `email` (`email`);

--
-- AUTO_INCREMENT de las tablas volcadas
--

--
-- AUTO_INCREMENT de la tabla `partidas`
--
ALTER TABLE `partidas`
  MODIFY `id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=17;

--
-- AUTO_INCREMENT de la tabla `preguntas`
--
ALTER TABLE `preguntas`
  MODIFY `id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=32;

--
-- AUTO_INCREMENT de la tabla `ranking`
--
ALTER TABLE `ranking`
  MODIFY `id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT de la tabla `usuarios`
--
ALTER TABLE `usuarios`
  MODIFY `id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- Restricciones para tablas volcadas
--

--
-- Filtros para la tabla `partidas`
--
ALTER TABLE `partidas`
  ADD CONSTRAINT `partidas_ibfk_1` FOREIGN KEY (`usuario_id`) REFERENCES `usuarios` (`id`);

--
-- Filtros para la tabla `ranking`
--
ALTER TABLE `ranking`
  ADD CONSTRAINT `ranking_ibfk_1` FOREIGN KEY (`usuario_id`) REFERENCES `usuarios` (`id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
