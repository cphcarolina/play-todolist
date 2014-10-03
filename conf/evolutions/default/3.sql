# Tasks schema

# --- !Ups

CREATE SEQUENCE user_id_seq;

CREATE TABLE usuario (
   id integer NOT NULL DEFAULT nextval('user_id_seq'),
   nombre varchar(255)
);

INSERT INTO usuario (nombre) 
   VALUES ('anonimo');
INSERT INTO usuario (nombre) 
   VALUES ('cphcarolina');
INSERT INTO usuario (nombre) 
   VALUES ('manolobombo');


ALTER TABLE task
   ADD usuarioFK integer;

UPDATE task
   SET usuarioFK = 1;

ALTER TABLE task
   ADD FOREIGN KEY (usuarioFK) REFERENCES usuario(id);


# --- !Downs

ALTER TABLE task
	DROP FOREIGN KEY usuarioFK;

DROP TABLE usuario;

DROP SEQUENCE user_id_seq;