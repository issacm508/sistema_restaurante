-- ============================================================
-- Base de datos: sistema_restaurante
-- Adaptado de MariaDB 13.0.2 a MySQL (5.7 / 8.x)
-- ============================================================

CREATE DATABASE IF NOT EXISTS `sistema_restaurante`
  DEFAULT CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE `sistema_restaurante`;

SET NAMES utf8mb4;

-- ------------------------------------------------------------
-- Limpieza (orden inverso de dependencias)
-- ------------------------------------------------------------
DROP TABLE IF EXISTS `pagos`;
DROP TABLE IF EXISTS `detalle_pedido`;
DROP TABLE IF EXISTS `pedidos`;
DROP TABLE IF EXISTS `platillos`;
DROP TABLE IF EXISTS `meseros`;
DROP TABLE IF EXISTS `usuarios`;
DROP TABLE IF EXISTS `roles`;
DROP TABLE IF EXISTS `metodos_pago`;
DROP TABLE IF EXISTS `mesas`;
DROP TABLE IF EXISTS `estados_pedido`;
DROP TABLE IF EXISTS `estados_empleado`;
DROP TABLE IF EXISTS `categorias`;

-- ------------------------------------------------------------
-- Tablas
-- ------------------------------------------------------------
CREATE TABLE `categorias` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `nombre` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `nombre` (`nombre`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `estados_empleado` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `nombreEstado` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `nombreEstado` (`nombreEstado`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `estados_pedido` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `nombreEstado` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `nombreEstado` (`nombreEstado`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `mesas` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `numeroMesa` INT NOT NULL,
  `estado` VARCHAR(255) NOT NULL DEFAULT 'Disponible',
  PRIMARY KEY (`id`),
  UNIQUE KEY `numeroMesa` (`numeroMesa`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `metodos_pago` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `nombreMetodo` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `nombreMetodo` (`nombreMetodo`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `roles` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `nombreRol` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `nombreRol` (`nombreRol`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `usuarios` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `idRol` INT NOT NULL,
  `username` VARCHAR(255) NOT NULL,
  `password` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`),
  KEY `fk_usuarios_roles` (`idRol`),
  CONSTRAINT `fk_usuarios_roles` FOREIGN KEY (`idRol`) REFERENCES `roles` (`id`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `meseros` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `idUsuario` INT NOT NULL,
  `idEstado` INT NOT NULL,
  `nombre` VARCHAR(255) NOT NULL,
  `apellido` VARCHAR(255) NOT NULL,
  `telefono` VARCHAR(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idUsuario` (`idUsuario`),
  KEY `fk_meseros_estados` (`idEstado`),
  CONSTRAINT `fk_meseros_estados` FOREIGN KEY (`idEstado`) REFERENCES `estados_empleado` (`id`) ON UPDATE CASCADE,
  CONSTRAINT `fk_meseros_usuarios` FOREIGN KEY (`idUsuario`) REFERENCES `usuarios` (`id`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `platillos` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `idCategoria` INT NOT NULL,
  `nombre` VARCHAR(255) NOT NULL,
  `precio` DECIMAL(10,2) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_platillos_categorias` (`idCategoria`),
  CONSTRAINT `fk_platillos_categorias` FOREIGN KEY (`idCategoria`) REFERENCES `categorias` (`id`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `pedidos` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `idMesero` INT NOT NULL,
  `idMesa` INT NOT NULL,
  `idEstado` INT NOT NULL DEFAULT 1,
  `fechaHora` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `fk_pedidos_meseros` (`idMesero`),
  KEY `fk_pedidos_mesas` (`idMesa`),
  KEY `fk_pedidos_estados` (`idEstado`),
  CONSTRAINT `fk_pedidos_estados` FOREIGN KEY (`idEstado`) REFERENCES `estados_pedido` (`id`) ON UPDATE CASCADE,
  CONSTRAINT `fk_pedidos_mesas` FOREIGN KEY (`idMesa`) REFERENCES `mesas` (`id`) ON UPDATE CASCADE,
  CONSTRAINT `fk_pedidos_meseros` FOREIGN KEY (`idMesero`) REFERENCES `meseros` (`id`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `detalle_pedido` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `idPedido` INT NOT NULL,
  `idPlatillo` INT NOT NULL,
  `cantidad` INT NOT NULL,
  `subtotal` DECIMAL(10,2) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_detalle_pedidos` (`idPedido`),
  KEY `fk_detalle_platillos` (`idPlatillo`),
  CONSTRAINT `chk_detalle_cantidad` CHECK (`cantidad` > 0),
  CONSTRAINT `fk_detalle_pedidos` FOREIGN KEY (`idPedido`) REFERENCES `pedidos` (`id`) ON UPDATE CASCADE,
  CONSTRAINT `fk_detalle_platillos` FOREIGN KEY (`idPlatillo`) REFERENCES `platillos` (`id`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `pagos` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `idPedido` INT NOT NULL,
  `monto` DECIMAL(10,2) NOT NULL,
  `idMetodoPago` INT NOT NULL DEFAULT 1,
  `fechaPago` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idPedido` (`idPedido`),
  KEY `fk_pagos_metodos` (`idMetodoPago`),
  CONSTRAINT `fk_pagos_metodos` FOREIGN KEY (`idMetodoPago`) REFERENCES `metodos_pago` (`id`) ON UPDATE CASCADE,
  CONSTRAINT `fk_pagos_pedidos` FOREIGN KEY (`idPedido`) REFERENCES `pedidos` (`id`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ------------------------------------------------------------
-- Datos
-- ------------------------------------------------------------
INSERT INTO `categorias` (`id`, `nombre`) VALUES
(1, 'Bebidas'),
(2, 'Platillos Fuertes'),
(3, 'Postres');

INSERT INTO `estados_empleado` (`id`, `nombreEstado`) VALUES
(1, 'Activo'),
(2, 'Inactivo');

INSERT INTO `estados_pedido` (`id`, `nombreEstado`) VALUES
(1, 'En Proceso'),
(2, 'Completado'),
(3, 'Cancelado');

INSERT INTO `mesas` (`id`, `numeroMesa`, `estado`) VALUES
(1, 1, 'Disponible'),
(2, 2, 'Ocupada');

INSERT INTO `metodos_pago` (`id`, `nombreMetodo`) VALUES
(1, 'Efectivo'),
(2, 'Tarjeta de Crédito'),
(3, 'Tarjeta de Débito'),
(4, 'Transferencia');

INSERT INTO `roles` (`id`, `nombreRol`) VALUES
(1, 'Super Administrador'),
(2, 'Recepcionista'),
(3, 'Mesero'),
(4, 'Cocina (Cocinero)'),
(5, 'Adminstrador');

INSERT INTO `usuarios` (`id`, `idRol`, `username`, `password`) VALUES
(1, 1, 'Ariadna Cornejo', '123'),
(2, 2, 'Maria Lopez', '321'),
(3, 3, 'Carlos Vallarta', '098'),
(4, 4, 'Jessica Cruz', '890'),
(5, 5, 'Juan Perez', '456'),
(6, 3, 'Ana Martinez', '567'),
(7, 3, 'Luis Hernandez', '789'),
(8, 3, 'Sofia Ramirez', '964'),
(9, 3, 'Diego Torres', '106');

INSERT INTO `meseros` (`id`, `idUsuario`, `idEstado`, `nombre`, `apellido`, `telefono`) VALUES
(1, 3, 1, 'Carlos', 'Vallarta', '5512345678´'),
(2, 6, 1, 'Ana', 'Martinez', '5523456789´'),
(3, 7, 1, 'Luis', 'Hernandez', '5534567890´'),
(4, 8, 2, 'Sofia', 'Ramirez', '5545678901´'),
(5, 9, 1, 'Diego', 'Torres', '5556789012´');

INSERT INTO `platillos` (`id`, `idCategoria`, `nombre`, `precio`) VALUES
(1, 1, 'Agua de Horchata', 25.00),
(2, 2, 'Milanesa de Res', 120.00),
(3, 3, 'Flan Napolitano', 45.00);

INSERT INTO `pedidos` (`id`, `idMesero`, `idMesa`, `idEstado`, `fechaHora`) VALUES
(1, 1, 2, 1, '2026-11-02 14:30:00');

INSERT INTO `detalle_pedido` (`id`, `idPedido`, `idPlatillo`, `cantidad`, `subtotal`) VALUES
(1, 1, 1, 2, 50.00),
(2, 1, 2, 1, 120.00);

INSERT INTO `pagos` (`id`, `idPedido`, `monto`, `idMetodoPago`, `fechaPago`) VALUES
(1, 1, 170.00, 1, '2026-11-02 15:15:00');
