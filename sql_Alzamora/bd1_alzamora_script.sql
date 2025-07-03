-- Crear la base de datos
DROP DATABASE IF EXISTS BD1_Alzamora;
CREATE DATABASE BD1_Alzamora;
USE BD1_Alzamora;

-- Tabla clientes
CREATE TABLE clientes (
    id_cliente INT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    direccion VARCHAR(200) NOT NULL
);

-- Datos de prueba para clientes
INSERT INTO clientes VALUES 
(1, 'Juan Pérez', 'Av. Lima 123'),
(2, 'María Gómez', 'Jr. Cusco 456'),
(3, 'Luis Torres', 'Psje. Arequipa 789');

-- Tabla productos
CREATE TABLE productos (
    id_prod INT PRIMARY KEY,
    nom_prod VARCHAR(100) NOT NULL,
    precio DECIMAL(10,2) NOT NULL,
    stock_actual INT NOT NULL
);

-- Datos de prueba para productos
INSERT INTO productos VALUES 
(101, 'Arroz 5kg', 25.50, 100),
(102, 'Azúcar 1kg', 5.90, 250),
(103, 'Aceite 1L', 8.30, 150);

-- Tabla factura (sin DEFAULT CURRENT_DATE)
CREATE TABLE factura (
    nro_factura INT AUTO_INCREMENT PRIMARY KEY,
    fecha DATE,
    estado ENUM('Emitido', 'Cancelado', 'Anulado') NOT NULL,
    id_cliente INT NOT NULL,
    total DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (id_cliente) REFERENCES clientes(id_cliente)
);

-- Trigger para asignar fecha automáticamente si es NULL
DELIMITER //
CREATE TRIGGER set_fecha_factura
BEFORE INSERT ON factura
FOR EACH ROW
BEGIN
  IF NEW.fecha IS NULL THEN
    SET NEW.fecha = CURDATE();
  END IF;
END;
//
DELIMITER ;

-- Tabla detallefact (clave compuesta)
CREATE TABLE detallefact (
    nro_factura INT,
    id_prod INT,
    cantidad INT NOT NULL,
    PRIMARY KEY (nro_factura, id_prod),
    FOREIGN KEY (nro_factura) REFERENCES factura(nro_factura),
    FOREIGN KEY (id_prod) REFERENCES productos(id_prod)
);


