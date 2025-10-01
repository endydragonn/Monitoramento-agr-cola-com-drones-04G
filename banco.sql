
-- Criação do banco de dados
CREATE DATABASE drone_db;

-- Criação das tabelas

CREATE TABLE usuario (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    login VARCHAR(255) UNIQUE NOT NULL,
    senha VARCHAR(255) NOT NULL 
);

CREATE TABLE area_agricola (
    id SERIAL PRIMARY KEY,
    tamanho DOUBLE PRECISION NOT NULL CHECK (tamanho > 0),
    localizacao VARCHAR(255) NOT NULL,
    tipo_cultivo VARCHAR(255) NOT NULL
);

CREATE TABLE drone (
    id VARCHAR(50) PRIMARY KEY,
    status VARCHAR(50) NOT NULL,
    bateria INT NOT NULL CHECK (bateria >= 0 AND bateria <= 100)
);

CREATE TABLE drone_sensor (
    drone_id VARCHAR(50) REFERENCES drone(id) ON DELETE CASCADE,
    sensor VARCHAR(100) NOT NULL,
    PRIMARY KEY (drone_id, sensor)
);

CREATE TABLE missao_voo (
    id SERIAL PRIMARY KEY,
    data DATE NOT NULL, 
    status VARCHAR(50) NOT NULL,
    drone_id VARCHAR(50) REFERENCES drone(id) ON DELETE RESTRICT,
    area_id INT REFERENCES area_agricola(id) ON DELETE RESTRICT
);

CREATE TABLE missao_sensor (
    missao_id INT REFERENCES missao_voo(id) ON DELETE CASCADE,
    sensor VARCHAR(100) NOT NULL,
    PRIMARY KEY (missao_id, sensor)
);

CREATE TABLE dados_coletados (
    id SERIAL PRIMARY KEY,
    temperatura DOUBLE PRECISION NOT NULL CHECK (temperatura >= -50 AND temperatura <= 50),
    umidade DOUBLE PRECISION NOT NULL CHECK (umidade >= 0 AND umidade <= 100),
    pragas VARCHAR(255),
    missao_id INT REFERENCES missao_voo(id) ON DELETE CASCADE NOT NULL
);

CREATE TABLE dados_imagem (
    dados_id INT REFERENCES dados_coletados(id) ON DELETE CASCADE,
    imagem VARCHAR(255) NOT NULL, -- URL da imagem
    PRIMARY KEY (dados_id, imagem)
);


-- Conceder permissões mínimas: SELECT, INSERT, UPDATE
GRANT SELECT, INSERT, UPDATE ON usuario TO app_user;
GRANT SELECT, INSERT, UPDATE ON area_agricola TO app_user;
GRANT SELECT, INSERT, UPDATE ON drone TO app_user;
GRANT SELECT, INSERT, UPDATE ON drone_sensor TO app_user;
GRANT SELECT, INSERT, UPDATE ON missao_voo TO app_user;
GRANT SELECT, INSERT, UPDATE ON missao_sensor TO app_user;
GRANT SELECT, INSERT, UPDATE ON dados_coletados TO app_user;
GRANT SELECT, INSERT, UPDATE ON dados_imagem TO app_user;
GRANT USAGE, SELECT ON ALL SEQUENCES IN SCHEMA public TO app_user;

