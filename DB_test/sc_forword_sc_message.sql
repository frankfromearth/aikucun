-- MySQL dump 10.13  Distrib 5.7.21, for Win64 (x86_64)
--
-- Host: rm-uf6q8mnyuo748g2o7o.mysql.rds.aliyuncs.com    Database: akapp
-- ------------------------------------------------------
-- Server version	5.6.34

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
SET @MYSQLDUMP_TEMP_LOG_BIN = @@SESSION.SQL_LOG_BIN;
SET @@SESSION.SQL_LOG_BIN= 0;

--
-- GTID state at the beginning of the backup 
--

SET @@GLOBAL.GTID_PURGED='0122f033-cda2-11e7-9516-7cd30ad3a666:1-68792100,
1fe25643-803f-11e7-9c77-7cd30abc989e:1-47531119,
9e84df7d-6edc-11e7-ab1a-7cd30abeae02:1-2748173';

--
-- Table structure for table `sc_forword`
--

DROP TABLE IF EXISTS `sc_forword`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sc_forword` (
  `requestid` char(32) DEFAULT NULL,
  `id` char(32) NOT NULL,
  `zhuanfaren` varchar(250) DEFAULT NULL,
  `zhuanfashijian` varchar(250) DEFAULT NULL,
  `shangpin` varchar(250) DEFAULT NULL,
  `zhuanfazhuangtai` int(11) DEFAULT '0',
  `zhuanfashijianshuzi` int(11) DEFAULT '0',
  `riqi` varchar(50) DEFAULT NULL,
  `dest` int(11) DEFAULT '0',
  `corpid` varchar(250) DEFAULT NULL,
  `liveid` varchar(250) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `sc_forword_zhuanfaren_shangpin` (`zhuanfaren`,`shangpin`),
  KEY `IDX_SHANGPIN_ZHUANFAREN` (`shangpin`,`zhuanfaren`),
  KEY `sc_forword_liveid` (`liveid`),
  KEY `sc_forword_corpid` (`corpid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sc_message`
--

DROP TABLE IF EXISTS `sc_message`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sc_message` (
  `requestid` char(32) DEFAULT NULL,
  `id` char(32) NOT NULL,
  `messagetime` varchar(50) DEFAULT NULL,
  `messagetimeshuzi` int(11) DEFAULT '0',
  `content` varchar(250) DEFAULT NULL,
  `type` int(11) DEFAULT '0',
  `title` varchar(128) DEFAULT NULL,
  `receiver` varchar(250) DEFAULT NULL,
  `sender` varchar(250) DEFAULT NULL,
  `readflag` int(11) DEFAULT '0',
  `invalidflag` int(11) DEFAULT '0',
  `pushtime` varchar(50) DEFAULT NULL,
  `pushflag` int(11) DEFAULT '0',
  `needpush` int(11) DEFAULT '0',
  `typestr` varchar(32) DEFAULT NULL,
  `allflag` int(11) DEFAULT '0',
  `readtime` varchar(50) DEFAULT NULL,
  `receivername` varchar(64) DEFAULT NULL,
  `sendername` varchar(64) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `sc_message_receiver` (`receiver`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
SET @@SESSION.SQL_LOG_BIN = @MYSQLDUMP_TEMP_LOG_BIN;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2018-04-23 10:01:40
