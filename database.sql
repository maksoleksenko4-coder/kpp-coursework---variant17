DROP DATABASE IF EXISTS field_monitoring;
CREATE DATABASE field_monitoring;
USE field_monitoring;

CREATE TABLE field (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       name VARCHAR(160) NOT NULL,
                       area DOUBLE NOT NULL,
                       crop_type VARCHAR(120) NOT NULL
);

CREATE TABLE observation (
                             id BIGINT AUTO_INCREMENT PRIMARY KEY,
                             field_id BIGINT NOT NULL,
                             date VARCHAR(20) NOT NULL,
                             description VARCHAR(255) NOT NULL,
                             f1_score DOUBLE NOT NULL,
                             f2_score DOUBLE NOT NULL,
                             f3_score DOUBLE NOT NULL,
                             FOREIGN KEY (field_id) REFERENCES field(id)
);

CREATE TABLE problem (
                         id BIGINT AUTO_INCREMENT PRIMARY KEY,
                         observation_id BIGINT NOT NULL,
                         state VARCHAR(30) NOT NULL,
                         f1 DOUBLE NOT NULL,
                         f2 DOUBLE NOT NULL,
                         f3 DOUBLE NOT NULL,
                         risk_index DOUBLE NOT NULL,
                         FOREIGN KEY (observation_id) REFERENCES observation(id)
);

CREATE TABLE treatment_action (
                                  id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                  problem_id BIGINT NOT NULL,
                                  description VARCHAR(500) NOT NULL,
                                  planned_date VARCHAR(20) NOT NULL,
                                  FOREIGN KEY (problem_id) REFERENCES problem(id)
);

-- Початкові демонстраційні дані
INSERT INTO field (name, area, crop_type) VALUES
                                              ('Поле №1 Північне', 120.5, 'Пшениця озима'),
                                              ('Поле №2 Центральне', 85.0, 'Кукурудза');

INSERT INTO observation (field_id, date, description, f1_score, f2_score, f3_score) VALUES
                                                                                        (1, '2026-05-10', 'Виявлено ознаки септоріозу', 15.0, 25.0, 40.0),
                                                                                        (2, '2026-06-15', 'Пошкодження стебловим метеликом', 5.0, 10.0, 30.0);