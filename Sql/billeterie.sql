SET FOREIGN_KEY_CHECKS = 0;

CREATE DATABASE IF NOT EXISTS billeterie DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE billeterie;

-- Table des clients
CREATE TABLE IF NOT EXISTS Client (
  id_client INT PRIMARY KEY AUTO_INCREMENT,
  nom VARCHAR(100) NOT NULL,
  prenom VARCHAR(100) NOT NULL,
  email VARCHAR(150) NOT NULL UNIQUE,
  telephone VARCHAR(30),
  date_creation DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Table des lieux / salles
CREATE TABLE IF NOT EXISTS Lieu (
  id_lieu INT PRIMARY KEY AUTO_INCREMENT,
  nom VARCHAR(150) NOT NULL,
  adresse VARCHAR(255),
  ville VARCHAR(100),
  capacite INT DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Table des catégories de billets (VIP, Standard, Réduit, etc.)
CREATE TABLE IF NOT EXISTS CategorieBillet (
  id_categorie INT PRIMARY KEY AUTO_INCREMENT,
  nom VARCHAR(50) NOT NULL,
  description VARCHAR(255),
  prix_sup DECIMAL(7,2) DEFAULT 0.00 -- supplément par rapport à prix de base
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Table des événements
CREATE TABLE IF NOT EXISTS Evenement (
  id_evenement INT PRIMARY KEY AUTO_INCREMENT,
  nom VARCHAR(200) NOT NULL,
  description TEXT,
  date_evenement DATE NOT NULL,
  heure TIME DEFAULT NULL,
  id_lieu INT NOT NULL,
  prix_base DECIMAL(7,2) NOT NULL DEFAULT 20.00,
  capacite INT DEFAULT NULL,
  CONSTRAINT fk_event_lieu FOREIGN KEY (id_lieu) REFERENCES Lieu(id_lieu) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Table des paiements
CREATE TABLE IF NOT EXISTS Paiement (
  id_paiement INT PRIMARY KEY AUTO_INCREMENT,
  id_client INT,
  montant DECIMAL(9,2) NOT NULL,
  date_paiement DATETIME DEFAULT CURRENT_TIMESTAMP,
  mode VARCHAR(50),
  reference_paiement VARCHAR(150),
  CONSTRAINT fk_paiement_client FOREIGN KEY (id_client) REFERENCES Client(id_client) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Table des billets
CREATE TABLE IF NOT EXISTS Billet (
  id_billet INT PRIMARY KEY AUTO_INCREMENT,
  id_client INT,
  id_evenement INT NOT NULL,
  id_categorie INT DEFAULT NULL,
  id_paiement INT DEFAULT NULL,
  prix DECIMAL(9,2) NOT NULL,
  date_achat DATETIME DEFAULT CURRENT_TIMESTAMP,
  code_barre VARCHAR(100) UNIQUE,
  place VARCHAR(50),
  CONSTRAINT fk_billet_client FOREIGN KEY (id_client) REFERENCES Client(id_client) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT fk_billet_event FOREIGN KEY (id_evenement) REFERENCES Evenement(id_evenement) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT fk_billet_categorie FOREIGN KEY (id_categorie) REFERENCES CategorieBillet(id_categorie) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT fk_billet_paiement FOREIGN KEY (id_paiement) REFERENCES Paiement(id_paiement) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Index utiles
CREATE INDEX idx_evenement_date ON Evenement(date_evenement);
CREATE INDEX idx_billet_client ON Billet(id_client);
CREATE INDEX idx_billet_event ON Billet(id_evenement);

SET FOREIGN_KEY_CHECKS = 1;
