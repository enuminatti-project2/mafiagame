DROP DATABASE IF EXISTS Mafia;
CREATE DATABASE Mafia;
USE Mafia;
CREATE TABLE `Players` (
  `username` varchar(100) NOT NULL,
  `pwd` varchar(100) NOT NULL,
  PRIMARY KEY (username)
);