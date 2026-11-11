CREATE DATABASE  IF NOT EXISTS `sajvdb` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `sajvdb`;
-- MySQL dump 10.13  Distrib 8.0.46, for Win64 (x86_64)
--
-- Host: localhost    Database: sajvdb
-- ------------------------------------------------------
-- Server version	8.0.46

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `audiencavirt`
--

DROP TABLE IF EXISTS `audiencavirt`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `audiencavirt` (
  `audId` int NOT NULL AUTO_INCREMENT,
  `audNroExpediente` varchar(20) NOT NULL,
  `audSalaId` int NOT NULL,
  `audResponsable` varchar(80) NOT NULL,
  `audCoHost` varchar(80) DEFAULT NULL,
  `audFecha` date NOT NULL,
  `audHrInicio` time NOT NULL,
  `audHrFin` time NOT NULL,
  `audTipo` int NOT NULL,
  `audComentarios` varchar(200) DEFAULT NULL,
  `audUsuId` int NOT NULL,
  `audEstado` int NOT NULL,
  `audSituacion` int NOT NULL DEFAULT '1',
  PRIMARY KEY (`audId`),
  KEY `audSalaId` (`audSalaId`),
  KEY `audUsuId` (`audUsuId`),
  CONSTRAINT `audiencavirt_ibfk_1` FOREIGN KEY (`audSalaId`) REFERENCES `sala` (`salId`),
  CONSTRAINT `audiencavirt_ibfk_2` FOREIGN KEY (`audUsuId`) REFERENCES `usuario` (`usuId`)
) ENGINE=InnoDB AUTO_INCREMENT=27 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `audiencavirt`
--

LOCK TABLES `audiencavirt` WRITE;
/*!40000 ALTER TABLE `audiencavirt` DISABLE KEYS */;
INSERT INTO `audiencavirt` VALUES (2,'00125-2026',3,'Coordinador de sala','Asistente virtual','2026-11-03','09:00:00','11:00:00',1,'Audiencia de prueba',2,1,3),(3,'00157-2025',4,'Coordinador de Sala','Asistente','2026-10-31','14:00:00','15:00:00',1,'Audiencia de prueba 2',2,1,4),(4,'00166-2026',2,'Javier Vargas',NULL,'2026-11-02','11:00:00','12:00:00',1,'Audiencia de prueba',2,1,3),(5,'00166-2026',1,'Javier Rentería',NULL,'2026-11-02','11:00:00','12:00:00',1,'prueba',2,1,3),(6,'00188-2026',4,'Daniela Bravo',NULL,'2026-11-02','11:00:00','12:00:00',1,'prueba',3,1,3),(7,'00200-2026',4,'Javier Vargas',NULL,'2026-11-03','15:00:00','16:30:00',1,'prueba',2,1,3),(8,'00300-2026',5,'Yuri Pinedo',NULL,'2026-11-07','22:55:00','23:05:00',1,'prueba',2,1,3),(9,'00310-2026',2,'Javier Vargas',NULL,'2026-11-07','23:00:00','23:10:00',1,'prueba',2,1,3),(10,'00320-2026',4,'Javier Vargas',NULL,'2026-11-08','00:17:00','00:30:00',1,'prueba',2,1,4),(11,'00350-2026',4,'Javier Vargas',NULL,'2026-11-08','00:35:00','00:50:00',1,'prueba',2,1,4),(12,'00360-2026',5,'Yuri Pinedo',NULL,'2026-11-08','00:38:00','00:45:00',1,'prueba',2,1,3),(13,'00370-2026',1,'Javier Vargas',NULL,'2026-11-08','09:53:00','10:00:00',1,'prueba',2,1,3),(14,'00380-2026',2,'Yuri Pinedo',NULL,'2026-11-08','13:03:00','13:08:00',1,'preuba',2,1,3),(15,'00390-2026',4,'Javier Rentería',NULL,'2026-11-08','22:44:00','22:48:00',1,'prueba',2,1,3),(16,'00295-2026',3,'Javier Rentería',NULL,'2026-11-08','22:53:00','22:58:00',1,'prueba',2,1,3),(17,'00310-2026',5,'Coordinador de sala',NULL,'2026-11-08','23:53:00','23:58:00',1,'prueba',2,1,3),(18,'00320-2026',1,'Javier Vargas',NULL,'2026-11-09','01:25:00','01:30:00',1,'prueba',2,1,3),(19,'00340-2026',5,'Yuri Pinedo',NULL,'2026-11-16','09:00:00','09:05:00',1,'prueba',2,1,1),(20,'00350-2026',2,'Yuri Pinedo',NULL,'2026-11-09','01:36:00','01:40:00',1,'prueba',2,1,3),(21,'003500-2026',2,'Javier Rentería','Asistente virtual','2026-11-09','09:55:00','10:00:00',1,'prueba',2,1,3),(22,'00355-2026',2,'Yuri Pinedo',NULL,'2026-11-09','10:05:00','10:10:00',1,'prueba',2,1,3),(23,'00370-2026',1,'Yuri Pinedo',NULL,'2026-11-09','10:11:00','10:15:00',1,'prueba',2,1,3),(24,'00380-2026',1,'Javier Vargas',NULL,'2026-11-09','10:25:00','10:28:00',1,'prueba',2,1,3),(25,'00400-2026',4,'Yuri Pinedo',NULL,'2026-11-10','10:30:00','10:40:00',1,'prueba',2,1,1),(26,'00410-2026',6,'Javier Rentería',NULL,'2026-11-09','17:51:00','17:55:00',1,'prueba',2,1,3);
/*!40000 ALTER TABLE `audiencavirt` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `auditoria`
--

DROP TABLE IF EXISTS `auditoria`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `auditoria` (
  `audId` int NOT NULL AUTO_INCREMENT,
  `audUsuarioId` int DEFAULT NULL,
  `audUsuario` varchar(50) NOT NULL,
  `audModulo` varchar(50) NOT NULL,
  `audAccion` varchar(50) NOT NULL,
  `audDescripcion` varchar(500) DEFAULT NULL,
  `audIp` varchar(45) DEFAULT NULL,
  `audFecha` datetime NOT NULL,
  PRIMARY KEY (`audId`),
  KEY `idx_auditoria_fecha` (`audFecha`),
  KEY `idx_auditoria_usuario` (`audUsuario`),
  KEY `idx_auditoria_modulo_accion` (`audModulo`,`audAccion`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `auditoria`
--

LOCK TABLES `auditoria` WRITE;
/*!40000 ALTER TABLE `auditoria` DISABLE KEYS */;
INSERT INTO `auditoria` VALUES (1,2,'ypinedo','USUARIOS','RESTABLECER_CONTRASENA','Se restableció la contraseña del usuario cperez y se solicitó el cambio en el siguiente ingreso.','0:0:0:0:0:0:0:1','2026-11-09 17:14:37'),(2,4,'cperez','SEGURIDAD','CAMBIAR_CONTRASENA','El usuario actualizó su contraseña.','0:0:0:0:0:0:0:1','2026-11-09 17:19:10'),(3,2,'ypinedo','SEDES','CREAR','Se registró la sede Trujillo (ID 4).','0:0:0:0:0:0:0:1','2026-11-09 17:44:56'),(4,2,'ypinedo','SALAS','CREAR','Se registró la sala Sala 03 (ID 6) en la sede Sede Chiclayo.','0:0:0:0:0:0:0:1','2026-11-09 17:45:49'),(5,2,'ypinedo','AUDIENCIAS','CREAR','Se registró la audiencia 00410-2026 (ID 26) en la sala Sala 03.','0:0:0:0:0:0:0:1','2026-11-09 17:48:52'),(6,NULL,'SISTEMA','AUDIENCIAS','ACTUALIZACION_AUTOMATICA','El sistema actualizó situaciones: 1 audiencias en curso y 0 audiencias finalizadas.',NULL,'2026-11-09 17:51:25'),(7,NULL,'SISTEMA','AUDIENCIAS','ACTUALIZACION_AUTOMATICA','El sistema actualizó situaciones: 0 audiencias en curso y 1 audiencias finalizadas.',NULL,'2026-11-09 17:55:25');
/*!40000 ALTER TABLE `auditoria` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `equipo_red`
--

DROP TABLE IF EXISTS `equipo_red`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `equipo_red` (
  `equId` int NOT NULL AUTO_INCREMENT,
  `equSedId` int NOT NULL,
  `equNombre` varchar(100) NOT NULL,
  `equHostname` varchar(100) NOT NULL,
  `equIP` varchar(45) NOT NULL,
  `equPuerto` int NOT NULL,
  `equUsuario` varchar(100) NOT NULL,
  `equContrasena` varchar(512) DEFAULT NULL,
  `equInterfazQos` varchar(50) NOT NULL,
  `equFabricante` varchar(50) NOT NULL,
  `equModelo` varchar(50) NOT NULL,
  `equEstado` int NOT NULL,
  PRIMARY KEY (`equId`),
  KEY `equSedId` (`equSedId`),
  CONSTRAINT `equipo_red_ibfk_1` FOREIGN KEY (`equSedId`) REFERENCES `sede` (`sedId`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `equipo_red`
--

LOCK TABLES `equipo_red` WRITE;
/*!40000 ALTER TABLE `equipo_red` DISABLE KEYS */;
INSERT INTO `equipo_red` VALUES (1,1,'R2-ZAVALA','R2-ZAVALA','192.168.10.1',22,'qosadmin','keXsDCseeqQP2/hCx1qFQ0cB8HuhnX6xj7n2wh1qj35c5XAR','Serial1/0','Cisco','3660',1),(2,2,'R1-CHICLAYO','R1-CHICLAYO','192.168.20.1',22,'qosadmin','keXsDCseeqQP2/hCx1qFQ0cB8HuhnX6xj7n2wh1qj35c5XAR','Serial1/0','Cisco','3660',1);
/*!40000 ALTER TABLE `equipo_red` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `politica_qos`
--

DROP TABLE IF EXISTS `politica_qos`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `politica_qos` (
  `polId` int NOT NULL AUTO_INCREMENT,
  `polCodigo` varchar(50) NOT NULL,
  `polNombre` varchar(100) NOT NULL,
  `polDescripcion` varchar(200) NOT NULL,
  `polDscp` int NOT NULL,
  `polAnchoBanda` int NOT NULL,
  `polEstado` int NOT NULL,
  `polClassMap` varchar(100) DEFAULT NULL,
  `polPolicyMap` varchar(100) DEFAULT NULL,
  `polDireccion` varchar(10) NOT NULL DEFAULT 'output',
  PRIMARY KEY (`polId`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `politica_qos`
--

LOCK TABLES `politica_qos` WRITE;
/*!40000 ALTER TABLE `politica_qos` DISABLE KEYS */;
INSERT INTO `politica_qos` VALUES (1,'POLITICA_QOS_ZAVALA','Política QoS Zavala','Priorización de tráfico de audiencias virtuales mediante LLQ',0,40,1,'CLASS_AUDIENCIAS','POLICY_AUDIENCIAS','output');
/*!40000 ALTER TABLE `politica_qos` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `qos_control_manual`
--

DROP TABLE IF EXISTS `qos_control_manual`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `qos_control_manual` (
  `qcmId` int NOT NULL AUTO_INCREMENT,
  `qcmEquId` int NOT NULL,
  `qcmPolId` int NOT NULL,
  `qcmModo` varchar(25) NOT NULL DEFAULT 'AUTOMATICO',
  `qcmMotivo` varchar(500) DEFAULT NULL,
  `qcmUsuId` int DEFAULT NULL,
  `qcmFechaInicio` datetime DEFAULT NULL,
  `qcmFechaFin` datetime DEFAULT NULL,
  `qcmActivo` int NOT NULL DEFAULT '0',
  `qcmFechaActualizacion` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`qcmId`),
  UNIQUE KEY `uk_qcm_equipo_politica` (`qcmEquId`,`qcmPolId`),
  KEY `fk_qcm_politica` (`qcmPolId`),
  KEY `fk_qcm_usuario` (`qcmUsuId`),
  CONSTRAINT `fk_qcm_equipo` FOREIGN KEY (`qcmEquId`) REFERENCES `equipo_red` (`equId`),
  CONSTRAINT `fk_qcm_politica` FOREIGN KEY (`qcmPolId`) REFERENCES `politica_qos` (`polId`),
  CONSTRAINT `fk_qcm_usuario` FOREIGN KEY (`qcmUsuId`) REFERENCES `usuario` (`usuId`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `qos_control_manual`
--

LOCK TABLES `qos_control_manual` WRITE;
/*!40000 ALTER TABLE `qos_control_manual` DISABLE KEYS */;
INSERT INTO `qos_control_manual` VALUES (1,1,1,'AUTOMATICO','prueba activacion manual',2,'2026-11-09 22:00:13','2026-11-09 22:10:13',0,'2026-11-09 22:10:14'),(2,2,1,'AUTOMATICO','activación de prueba',2,'2026-11-09 21:05:48','2026-11-09 21:15:48',0,'2026-11-09 21:16:13');
/*!40000 ALTER TABLE `qos_control_manual` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `qos_operacion`
--

DROP TABLE IF EXISTS `qos_operacion`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `qos_operacion` (
  `qopId` int NOT NULL AUTO_INCREMENT,
  `qopAudId` int DEFAULT NULL,
  `qopEquId` int NOT NULL,
  `qopPolId` int NOT NULL,
  `qopAccion` int NOT NULL,
  `qopOrigen` int NOT NULL,
  `qopFecInicio` datetime NOT NULL,
  `qopFecFin` datetime NOT NULL,
  `qopResultado` int NOT NULL,
  `qopMensaje` varchar(500) DEFAULT NULL,
  `qopSalida` text,
  `qopUsuId` int DEFAULT NULL,
  `qopMotivo` varchar(500) DEFAULT NULL,
  PRIMARY KEY (`qopId`),
  KEY `qopAudId` (`qopAudId`),
  KEY `qopEquId` (`qopEquId`),
  KEY `qopPolId` (`qopPolId`),
  KEY `fk_qop_usuario` (`qopUsuId`),
  CONSTRAINT `fk_qop_usuario` FOREIGN KEY (`qopUsuId`) REFERENCES `usuario` (`usuId`),
  CONSTRAINT `qos_operacion_ibfk_1` FOREIGN KEY (`qopAudId`) REFERENCES `audiencavirt` (`audId`),
  CONSTRAINT `qos_operacion_ibfk_2` FOREIGN KEY (`qopEquId`) REFERENCES `equipo_red` (`equId`),
  CONSTRAINT `qos_operacion_ibfk_3` FOREIGN KEY (`qopPolId`) REFERENCES `politica_qos` (`polId`)
) ENGINE=InnoDB AUTO_INCREMENT=98 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `qos_operacion`
--

LOCK TABLES `qos_operacion` WRITE;
/*!40000 ALTER TABLE `qos_operacion` DISABLE KEYS */;
INSERT INTO `qos_operacion` VALUES (1,NULL,1,1,1,2,'2026-11-04 17:20:00','2026-11-04 17:20:00',1,'PRUEBA SIMULADA: activación QoS','SIMULACION - No se ejecutaron comandos en el router Cisco.',NULL,NULL),(2,NULL,1,1,2,2,'2026-11-04 17:20:00','2026-11-04 17:20:00',1,'PRUEBA SIMULADA: desactivación QoS','SIMULACION - No se ejecutaron comandos en el router Cisco.',NULL,NULL),(3,2,1,1,1,2,'2026-11-05 16:31:50','2026-11-05 16:31:51',2,'No se pudo completar la operación QoS.',NULL,NULL,NULL),(4,2,1,1,1,2,'2026-11-05 16:37:11','2026-11-05 16:37:11',2,'No se pudo completar la operación QoS.',NULL,NULL,NULL),(5,2,1,1,1,2,'2026-11-05 16:37:24','2026-11-05 16:37:24',2,'No se pudo completar la operación QoS.',NULL,NULL,NULL),(6,2,1,1,1,2,'2026-11-05 16:40:38','2026-11-05 16:40:38',2,'No se pudo completar la operación QoS.',NULL,NULL,NULL),(7,2,1,1,1,2,'2026-11-05 16:58:56','2026-11-05 16:58:57',2,'No se pudo completar la operación QoS.',NULL,NULL,NULL),(8,2,1,1,1,2,'2026-11-05 17:44:26','2026-11-05 17:44:26',2,'No se pudo completar la operación QoS.',NULL,NULL,NULL),(9,2,1,1,1,2,'2026-11-05 18:17:03','2026-11-05 18:17:03',2,'No se pudo completar la operación QoS.',NULL,NULL,NULL),(10,2,1,1,1,2,'2026-11-06 13:10:55','2026-11-06 13:10:56',2,'No se pudo completar la operación QoS.',NULL,NULL,NULL),(11,2,1,1,1,2,'2026-11-06 13:17:58','2026-11-06 13:17:58',2,'No se pudo completar la operación QoS.',NULL,NULL,NULL),(12,2,1,1,1,2,'2026-11-06 13:18:11','2026-11-06 13:18:11',2,'No se pudo completar la operación QoS.',NULL,NULL,NULL),(13,2,1,1,1,2,'2026-11-06 14:07:29','2026-11-06 14:07:30',2,'No se pudo completar la operación QoS.',NULL,NULL,NULL),(14,2,1,1,1,2,'2026-11-06 14:52:42','2026-11-06 14:52:42',2,'No se pudo completar la operación QoS.',NULL,NULL,NULL),(15,2,1,1,1,2,'2026-11-06 15:02:12','2026-11-06 15:02:12',2,'No se pudo completar la operación QoS.',NULL,NULL,NULL),(16,4,1,1,1,2,'2026-11-06 15:46:40','2026-11-06 15:46:40',2,'No se pudo completar la operación QoS.',NULL,NULL,NULL),(17,2,1,1,1,2,'2026-11-06 16:52:19','2026-11-06 16:52:19',2,'No se pudo completar la operación QoS.',NULL,NULL,NULL),(18,2,1,1,1,2,'2026-11-06 17:27:02','2026-11-06 17:27:02',2,'No se pudo completar la operación QoS.',NULL,NULL,NULL),(19,2,1,1,1,2,'2026-11-06 17:27:17','2026-11-06 17:27:17',2,'No se pudo completar la operación QoS.',NULL,NULL,NULL),(20,2,1,1,1,2,'2026-11-06 17:27:28','2026-11-06 17:27:28',2,'No se pudo completar la operación QoS.',NULL,NULL,NULL),(21,2,1,1,1,2,'2026-11-06 17:44:38','2026-11-06 17:44:38',2,'No se pudo completar la operación QoS.',NULL,NULL,NULL),(22,2,1,1,1,2,'2026-11-06 17:44:51','2026-11-06 17:44:52',2,'No se pudo completar la operación QoS.',NULL,NULL,NULL),(23,2,1,1,1,2,'2026-11-06 17:44:57','2026-11-06 17:44:57',2,'No se pudo completar la operación QoS.',NULL,NULL,NULL),(24,2,1,1,1,2,'2026-11-06 17:51:47','2026-11-06 17:51:48',2,'No se pudo completar la operación QoS.',NULL,NULL,NULL),(25,2,1,1,1,2,'2026-11-06 17:52:00','2026-11-06 17:52:00',2,'No se pudo completar la operación QoS.',NULL,NULL,NULL),(26,2,1,1,1,2,'2026-11-06 17:52:26','2026-11-06 17:52:26',2,'No se pudo completar la operación QoS.',NULL,NULL,NULL),(27,2,1,1,1,2,'2026-11-06 17:57:20','2026-11-06 17:57:21',2,'No se pudo completar la operación QoS.',NULL,NULL,NULL),(28,2,1,1,1,2,'2026-11-06 17:57:42','2026-11-06 17:57:42',2,'No se pudo completar la operación QoS.',NULL,NULL,NULL),(29,2,1,1,1,2,'2026-11-06 17:57:51','2026-11-06 17:57:51',2,'No se pudo completar la operación QoS.',NULL,NULL,NULL),(30,2,1,1,1,2,'2026-11-06 18:10:28','2026-11-06 18:10:28',2,'FastAPI respondió HTTP 422. Detalle: {\"detail\":[{\"type\":\"missing\",\"loc\":[\"body\"],\"msg\":\"Field required\",\"input\":null}]}',NULL,NULL,NULL),(31,2,1,1,1,2,'2026-11-06 18:22:14','2026-11-06 18:22:15',2,'FastAPI respondió HTTP 422. Detalle: {\"detail\":[{\"type\":\"missing\",\"loc\":[\"body\"],\"msg\":\"Field required\",\"input\":null}]}',NULL,NULL,NULL),(32,2,1,1,1,2,'2026-11-06 18:31:58','2026-11-06 18:31:59',1,'Simulación de activar QoS para la audiencia 2',NULL,NULL,NULL),(33,2,1,1,2,2,'2026-11-06 20:21:17','2026-11-06 20:21:17',1,'Simulación de desactivar QoS para la audiencia 2',NULL,NULL,NULL),(34,2,1,1,2,2,'2026-11-07 11:58:34','2026-11-07 11:58:34',2,'No es posible desactivar QoS porque no existe una activación exitosa vigente.',NULL,NULL,NULL),(35,2,1,1,1,2,'2026-11-07 11:59:35','2026-11-07 11:59:36',1,'Simulación de activar QoS para la audiencia 2',NULL,NULL,NULL),(36,2,1,1,1,2,'2026-11-07 11:59:40','2026-11-07 11:59:40',2,'La política QoS ya se encuentra activa para esta audiencia y equipo.',NULL,NULL,NULL),(37,2,1,1,2,2,'2026-11-07 12:00:04','2026-11-07 12:00:04',1,'Simulación de desactivar QoS para la audiencia 2',NULL,NULL,NULL),(38,2,1,1,1,2,'2026-11-07 12:00:15','2026-11-07 12:00:15',1,'Simulación de activar QoS para la audiencia 2',NULL,NULL,NULL),(39,NULL,1,1,2,1,'2026-11-07 20:51:53','2026-11-07 20:51:53',1,'Simulación de desactivar QoS para la audiencia None',NULL,NULL,NULL),(40,8,1,1,1,1,'2026-11-07 21:55:44','2026-11-07 21:55:45',1,'Simulación de activar QoS para la audiencia 8',NULL,NULL,NULL),(41,NULL,1,1,2,1,'2026-11-07 22:10:17','2026-11-07 22:10:17',1,'Simulación de desactivar QoS para la audiencia None',NULL,NULL,NULL),(42,8,2,1,1,1,'2026-11-07 22:55:24','2026-11-07 22:55:24',1,'Simulación de activar QoS para la audiencia 8',NULL,NULL,NULL),(43,9,1,1,1,1,'2026-11-07 23:00:25','2026-11-07 23:00:25',1,'Simulación de activar QoS para la audiencia 9',NULL,NULL,NULL),(44,NULL,2,1,2,1,'2026-11-07 23:05:26','2026-11-07 23:05:26',1,'Simulación de desactivar QoS para la audiencia None',NULL,NULL,NULL),(45,NULL,1,1,2,1,'2026-11-07 23:10:27','2026-11-07 23:10:27',1,'Simulación de desactivar QoS para la audiencia None',NULL,NULL,NULL),(46,10,2,1,1,1,'2026-11-08 00:17:05','2026-11-08 00:17:05',1,'Simulación de activar QoS para la audiencia 10',NULL,NULL,NULL),(47,NULL,2,1,2,1,'2026-11-08 00:21:06','2026-11-08 00:21:06',1,'Simulación de desactivar QoS para la audiencia None',NULL,NULL,NULL),(48,11,2,1,1,1,'2026-11-08 00:35:08','2026-11-08 00:35:08',1,'Simulación de activar QoS para la audiencia 11',NULL,NULL,NULL),(49,NULL,2,1,2,1,'2026-11-08 00:45:09','2026-11-08 00:45:09',1,'Simulación de desactivar QoS para la audiencia None',NULL,NULL,NULL),(50,NULL,1,1,1,2,'2026-11-08 08:25:16','2026-11-08 08:25:16',1,'Simulación de activar QoS para la audiencia None',NULL,2,'Activación manual por incidencia en videoconferencia'),(51,NULL,1,1,2,1,'2026-11-08 08:25:31','2026-11-08 08:25:31',1,'Simulación de desactivar QoS para la audiencia None',NULL,NULL,NULL),(52,NULL,1,1,1,2,'2026-11-08 09:06:19','2026-11-08 09:06:19',1,'Simulación de activar QoS para la audiencia None',NULL,2,'Prueba controlada de activación manual'),(53,NULL,1,1,2,1,'2026-11-08 09:06:38','2026-11-08 09:06:38',1,'Simulación de desactivar QoS para la audiencia None',NULL,NULL,NULL),(54,NULL,1,1,1,2,'2026-11-08 09:37:58','2026-11-08 09:37:58',1,'Simulación de activar QoS para la audiencia None',NULL,2,'Prueba de intervención manual QoS por 10 minutos'),(55,NULL,1,1,2,1,'2026-11-08 09:48:24','2026-11-08 09:48:24',1,'Simulación de desactivar QoS para la audiencia None',NULL,NULL,NULL),(56,13,1,1,1,1,'2026-11-08 09:53:25','2026-11-08 09:53:25',1,'Simulación de activar QoS para la audiencia 13',NULL,NULL,NULL),(57,NULL,1,1,2,1,'2026-11-08 10:00:27','2026-11-08 10:00:27',1,'Simulación de desactivar QoS para la audiencia None',NULL,NULL,NULL),(58,14,1,1,1,1,'2026-11-08 13:03:06','2026-11-08 13:03:07',1,'Simulación de activar QoS para la audiencia 14',NULL,NULL,NULL),(59,NULL,1,1,2,1,'2026-11-08 13:08:07','2026-11-08 13:08:07',1,'Simulación de desactivar QoS para la audiencia None',NULL,NULL,NULL),(60,15,2,1,1,1,'2026-11-08 22:44:12','2026-11-08 22:44:12',2,'No se pudo establecer la comunicación HTTP con FastAPI.',NULL,NULL,NULL),(61,15,2,1,1,1,'2026-11-08 22:44:42','2026-11-08 22:44:42',2,'No se pudo establecer la comunicación HTTP con FastAPI.',NULL,NULL,NULL),(62,15,2,1,1,1,'2026-11-08 22:45:12','2026-11-08 22:45:12',2,'No se pudo establecer la comunicación HTTP con FastAPI.',NULL,NULL,NULL),(63,15,2,1,1,1,'2026-11-08 22:45:44','2026-11-08 22:45:45',2,'No se pudo establecer la comunicación HTTP con FastAPI.',NULL,NULL,NULL),(64,15,2,1,1,1,'2026-11-08 22:46:16','2026-11-08 22:46:16',2,'No se pudo establecer la comunicación HTTP con FastAPI.',NULL,NULL,NULL),(65,15,2,1,1,1,'2026-11-08 22:46:48','2026-11-08 22:46:48',2,'No se pudo establecer la comunicación HTTP con FastAPI.',NULL,NULL,NULL),(66,15,2,1,1,1,'2026-11-08 22:47:19','2026-11-08 22:47:19',2,'No se pudo establecer la comunicación HTTP con FastAPI.',NULL,NULL,NULL),(67,15,2,1,1,1,'2026-11-08 22:47:49','2026-11-08 22:47:49',2,'No se pudo establecer la comunicación HTTP con FastAPI.',NULL,NULL,NULL),(68,16,1,1,1,1,'2026-11-08 22:53:20','2026-11-08 22:53:20',2,'FastAPI respondió HTTP 500. Detalle: Internal Server Error',NULL,NULL,NULL),(69,16,1,1,1,1,'2026-11-08 22:53:50','2026-11-08 22:53:50',2,'FastAPI respondió HTTP 500. Detalle: Internal Server Error',NULL,NULL,NULL),(70,16,1,1,1,1,'2026-11-08 22:54:20','2026-11-08 22:54:20',2,'FastAPI respondió HTTP 500. Detalle: Internal Server Error',NULL,NULL,NULL),(71,16,1,1,1,1,'2026-11-08 22:54:50','2026-11-08 22:54:50',2,'FastAPI respondió HTTP 500. Detalle: Internal Server Error',NULL,NULL,NULL),(72,16,1,1,1,1,'2026-11-08 22:55:20','2026-11-08 22:55:20',2,'FastAPI respondió HTTP 500. Detalle: Internal Server Error',NULL,NULL,NULL),(73,16,1,1,1,1,'2026-11-08 22:55:50','2026-11-08 22:55:50',2,'FastAPI respondió HTTP 500. Detalle: Internal Server Error',NULL,NULL,NULL),(74,16,1,1,1,1,'2026-11-08 22:56:21','2026-11-08 22:56:21',2,'FastAPI respondió HTTP 500. Detalle: Internal Server Error',NULL,NULL,NULL),(75,16,1,1,1,1,'2026-11-08 22:56:51','2026-11-08 22:56:51',2,'FastAPI respondió HTTP 500. Detalle: Internal Server Error',NULL,NULL,NULL),(76,16,1,1,1,1,'2026-11-08 22:57:21','2026-11-08 22:57:21',2,'FastAPI respondió HTTP 500. Detalle: Internal Server Error',NULL,NULL,NULL),(77,16,1,1,1,1,'2026-11-08 22:57:51','2026-11-08 22:57:51',2,'FastAPI respondió HTTP 500. Detalle: Internal Server Error',NULL,NULL,NULL),(78,17,2,1,1,1,'2026-11-08 23:53:17','2026-11-08 23:53:17',1,'Simulación de activar QoS para la audiencia 17',NULL,NULL,NULL),(79,NULL,2,1,2,1,'2026-11-08 23:58:18','2026-11-08 23:58:18',1,'Simulación de desactivar QoS para la audiencia None',NULL,NULL,NULL),(80,18,1,1,1,1,'2026-11-09 01:25:06','2026-11-09 01:25:07',1,'Simulación de activar QoS para la audiencia 18',NULL,NULL,NULL),(81,NULL,1,1,2,1,'2026-11-09 01:30:08','2026-11-09 01:30:08',1,'Simulación de desactivar QoS para la audiencia None',NULL,NULL,NULL),(82,20,1,1,1,1,'2026-11-09 01:36:08','2026-11-09 01:36:08',1,'Simulación de activar QoS para la audiencia 20',NULL,NULL,NULL),(83,NULL,1,1,2,1,'2026-11-09 01:40:09','2026-11-09 01:40:09',1,'Simulación de desactivar QoS para la audiencia None',NULL,NULL,NULL),(84,21,1,1,1,1,'2026-11-09 09:55:25','2026-11-09 09:55:25',1,'Simulación de activar QoS para la audiencia 21',NULL,NULL,NULL),(85,NULL,1,1,2,1,'2026-11-09 10:00:26','2026-11-09 10:00:26',1,'Simulación de desactivar QoS para la audiencia None',NULL,NULL,NULL),(86,22,1,1,1,1,'2026-11-09 10:05:27','2026-11-09 10:05:27',1,'Simulación de activar QoS para la audiencia 22',NULL,NULL,NULL),(87,NULL,1,1,2,1,'2026-11-09 10:10:28','2026-11-09 10:10:28',1,'Simulación de desactivar QoS para la audiencia None',NULL,NULL,NULL),(88,24,1,1,1,1,'2026-11-09 10:25:01','2026-11-09 10:25:01',1,'Simulación de activar QoS para la audiencia 24',NULL,NULL,NULL),(89,NULL,1,1,2,1,'2026-11-09 10:28:01','2026-11-09 10:28:01',1,'Simulación de desactivar QoS para la audiencia None',NULL,NULL,NULL),(90,26,2,1,1,1,'2026-11-09 17:51:03','2026-11-09 17:51:03',1,'Simulación de activar QoS para la audiencia 26',NULL,NULL,NULL),(91,NULL,2,1,2,1,'2026-11-09 17:55:04','2026-11-09 17:55:04',1,'Simulación de desactivar QoS para la audiencia None',NULL,NULL,NULL),(92,NULL,2,1,1,2,'2026-11-09 21:05:48','2026-11-09 21:05:48',1,'Simulación de activar QoS para la audiencia None',NULL,2,'activación de prueba'),(93,NULL,2,1,2,1,'2026-11-09 21:16:13','2026-11-09 21:16:13',1,'Simulación de desactivar QoS para la audiencia None',NULL,NULL,NULL),(94,NULL,1,1,1,2,'2026-11-09 21:18:49','2026-11-09 21:18:49',1,'Simulación de activar QoS para la audiencia None',NULL,2,'prueba act manual'),(95,NULL,1,1,2,1,'2026-11-09 21:35:57','2026-11-09 21:35:58',1,'Simulación de desactivar QoS para la audiencia None',NULL,NULL,NULL),(96,NULL,1,1,1,2,'2026-11-09 22:00:13','2026-11-09 22:00:13',1,'Simulación de activar QoS para la audiencia None',NULL,2,'prueba activacion manual'),(97,NULL,1,1,2,1,'2026-11-09 22:10:14','2026-11-09 22:10:14',1,'Simulación de desactivar QoS para la audiencia None',NULL,NULL,NULL);
/*!40000 ALTER TABLE `qos_operacion` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `rol`
--

DROP TABLE IF EXISTS `rol`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `rol` (
  `rolId` int NOT NULL AUTO_INCREMENT,
  `rolDescripcion` varchar(50) NOT NULL,
  `rolEstado` int NOT NULL,
  PRIMARY KEY (`rolId`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `rol`
--

LOCK TABLES `rol` WRITE;
/*!40000 ALTER TABLE `rol` DISABLE KEYS */;
INSERT INTO `rol` VALUES (1,'Administrador',1),(2,'Operador',1),(3,'Consulta',1);
/*!40000 ALTER TABLE `rol` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sala`
--

DROP TABLE IF EXISTS `sala`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sala` (
  `salId` int NOT NULL AUTO_INCREMENT,
  `salSedId` int NOT NULL,
  `salDescripcion` varchar(100) NOT NULL,
  `salEstado` int NOT NULL,
  PRIMARY KEY (`salId`),
  KEY `salSedId` (`salSedId`),
  CONSTRAINT `sala_ibfk_1` FOREIGN KEY (`salSedId`) REFERENCES `sede` (`sedId`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sala`
--

LOCK TABLES `sala` WRITE;
/*!40000 ALTER TABLE `sala` DISABLE KEYS */;
INSERT INTO `sala` VALUES (1,1,'Sala 01',1),(2,1,'Sala 02',1),(3,1,'Sala 03',1),(4,2,'Sala 01',1),(5,2,'Sala 02',1),(6,2,'Sala 03',1);
/*!40000 ALTER TABLE `sala` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sede`
--

DROP TABLE IF EXISTS `sede`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sede` (
  `sedId` int NOT NULL AUTO_INCREMENT,
  `sedDescripcion` varchar(100) NOT NULL,
  `sedDireccion` varchar(200) NOT NULL,
  `sedEstado` int NOT NULL,
  PRIMARY KEY (`sedId`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sede`
--

LOCK TABLES `sede` WRITE;
/*!40000 ALTER TABLE `sede` DISABLE KEYS */;
INSERT INTO `sede` VALUES (1,'Sede Carlos Zavala Loayza','España 734 -  Breña',1),(2,'Sede Chiclayo','Chiclayo',1),(3,'Sede Tumbes','Tumbes',1),(4,'Trujillo','Trujillo',1);
/*!40000 ALTER TABLE `sede` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `usuario`
--

DROP TABLE IF EXISTS `usuario`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `usuario` (
  `usuId` int NOT NULL AUTO_INCREMENT,
  `usuNombre` varchar(80) NOT NULL,
  `usuApellidos` varchar(100) NOT NULL,
  `usuUsuario` varchar(20) NOT NULL,
  `usuCorreo` varchar(50) NOT NULL,
  `usuContrasena` varchar(200) NOT NULL,
  `usuRol` int NOT NULL,
  `usuEstado` int NOT NULL,
  `usuSedId` int DEFAULT NULL,
  `usuDebeCambiarContrasena` int NOT NULL DEFAULT '0',
  PRIMARY KEY (`usuId`),
  KEY `usuRol` (`usuRol`),
  KEY `idx_usuario_sede` (`usuSedId`),
  CONSTRAINT `fk_usuario_sede` FOREIGN KEY (`usuSedId`) REFERENCES `sede` (`sedId`),
  CONSTRAINT `usuario_ibfk_1` FOREIGN KEY (`usuRol`) REFERENCES `rol` (`rolId`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `usuario`
--

LOCK TABLES `usuario` WRITE;
/*!40000 ALTER TABLE `usuario` DISABLE KEYS */;
INSERT INTO `usuario` VALUES (2,'Yuri','Pinedo Bravo','ypinedo','ypinedob@gmail.com','$2a$10$gMuIOru2/LaY2ZBR3.jXvucSdS78vNLEHi.n3kcFsJg09JX6VojSW',1,1,NULL,0),(3,'Carlos','Mejía','cmejia','cmejia@gmail.com','$2a$10$.o.A1Q1t9aihlt3K/BdWeuwiBNTz78ii/9JAdtJ0fLUvv7Gatx//6',2,1,2,0),(4,'Camila','Pérez','cperez','cperez@gmail.com','$2a$10$SueiujocyeGRqvoXvaR71OCM684251Q3sD95HrPntCt2WGcbB7Voy',3,1,3,0),(5,'Gustavo','Campos','gcampos','gcampos@gmail.com','$2a$10$PdmZhvSciv4YREZ9aSQUzuJN1uiYVaucIlXhziBHnYMwBE.oiHdCq',1,1,1,0);
/*!40000 ALTER TABLE `usuario` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping events for database 'sajvdb'
--

--
-- Dumping routines for database 'sajvdb'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-11-09 22:13:14
