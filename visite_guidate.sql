DROP DATABASE IF EXISTS visite_guidate;
CREATE DATABASE visite_guidate
  DEFAULT CHARACTER SET utf8mb4 
  COLLATE utf8mb4_0900_ai_ci;
USE visite_guidate;

CREATE TABLE config (
  id TINYINT(1) UNSIGNED PRIMARY KEY DEFAULT '1',
  numero_max_iscrizioni INT UNSIGNED DEFAULT NULL,
  initialized_on DATE DEFAULT NULL
);

CREATE TABLE comuni (
  id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  nome VARCHAR(32) NOT NULL UNIQUE,
  provincia VARCHAR(32) NOT NULL,
  regione VARCHAR(32) NOT NULL,
  config_id TINYINT UNSIGNED NOT NULL DEFAULT '1',
  FOREIGN KEY (config_id) REFERENCES config(id) ON DELETE CASCADE
);

CREATE TABLE giorni_settimana (
  id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  nome VARCHAR(9) NOT NULL UNIQUE
);

CREATE TABLE luoghi (
  id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  nome VARCHAR(64) NOT NULL UNIQUE,
  descrizione VARCHAR(256) NOT NULL,
  comune_id INT UNSIGNED NOT NULL,
  FOREIGN KEY (comune_id) REFERENCES comuni(id) ON DELETE CASCADE
);

CREATE TABLE punti_incontro (
  id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  indirizzo VARCHAR(64) NOT NULL ,
  comune_id INT UNSIGNED NOT NULL,
  UNIQUE(indirizzo, comune_id),
  FOREIGN KEY (comune_id) REFERENCES comuni(id) ON DELETE CASCADE
);

CREATE TABLE tipi_visita (
  id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  titolo VARCHAR(64) NOT NULL,
  descrizione VARCHAR(512) NOT NULL,
  punto_incontro_id INT UNSIGNED NOT NULL,
  data_inizio DATE NOT NULL,
  data_fine DATE NOT NULL,
  ora_inizio TIME NOT NULL,
  durata_minuti INT NOT NULL DEFAULT '90',
  entrata_libera TINYINT(1) NOT NULL DEFAULT '0',
  num_min_partecipanti INT NOT NULL,
  num_max_partecipanti INT NOT NULL,
  luogo_id INT UNSIGNED NOT NULL,
  FOREIGN KEY (luogo_id) REFERENCES luoghi(id) ON DELETE CASCADE,
  FOREIGN KEY (punto_incontro_id) REFERENCES punti_incontro(id) ON DELETE CASCADE
);

CREATE TABLE ruoli (
  id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  nome ENUM('CONF', 'VOL') NOT NULL UNIQUE
);

CREATE TABLE utenti (
  id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(32) NOT NULL UNIQUE,
  password_hash CHAR(64) NOT NULL,
  salt BINARY(16) NOT NULL,
  ruolo_id INT UNSIGNED NOT NULL,
  last_login DATE DEFAULT NULL,
  FOREIGN KEY (ruolo_id) REFERENCES ruoli(id) ON DELETE CASCADE
);

CREATE TABLE tipi_visita_luoghi (
  id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  tipo_visita_id INT UNSIGNED NOT NULL,
  luogo_id INT UNSIGNED NOT NULL,
  FOREIGN KEY (tipo_visita_id) REFERENCES tipi_visita(id) ON DELETE CASCADE,
  FOREIGN KEY (luogo_id) REFERENCES luoghi(id) ON DELETE CASCADE
);

CREATE TABLE tipi_visita_volontari (
  id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  tipo_visita_id INT UNSIGNED NOT NULL,
  volontario_id INT UNSIGNED NOT NULL,
  FOREIGN KEY (tipo_visita_id) REFERENCES tipi_visita(id) ON DELETE CASCADE,
  FOREIGN KEY (volontario_id) REFERENCES utenti(id) ON DELETE CASCADE
);

CREATE TABLE giorni_settimana_tipi_visita (
  id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  giorno_settimana_id INT UNSIGNED NOT NULL,
  tipo_visita_id INT UNSIGNED NOT NULL,
  FOREIGN KEY (giorno_settimana_id) REFERENCES giorni_settimana(id) ON DELETE CASCADE,
  FOREIGN KEY (tipo_visita_id) REFERENCES tipi_visita(id) ON DELETE CASCADE
);

CREATE TABLE date_precluse (
  data_preclusa DATE PRIMARY KEY
);

INSERT INTO giorni_settimana (`id`, `nome`) VALUES
(1, 'Domenica'),
(2, 'Lunedì'),
(3, 'Martedì'),
(4, 'Mercoledì'),
(5, 'Giovedì'),
(6, 'Venerdì'),
(7, 'Sabato');

INSERT INTO ruoli (`id`, `nome`) VALUES
(1, 'CONF'),
(2, 'VOL');

INSERT INTO `utenti` (`username`, `password_hash`, `salt`, `ruolo_id`, `last_login`) VALUES
('conf', 'b724e99182ef638e767025d03abb735b49a52d05c8906e08912779c395dc182e', 0xb58f6b3d21a3058fd90df2e8f8f1afc0, 1, NULL),
('volontario1', 'b724e99182ef638e767025d03abb735b49a52d05c8906e08912779c395dc182e', 0xb58f6b3d21a3058fd90df2e8f8f1afc0, 2, NULL),
('volontario2', 'b724e99182ef638e767025d03abb735b49a52d05c8906e08912779c395dc182e', 0xb58f6b3d21a3058fd90df2e8f8f1afc0, 2, NULL);
