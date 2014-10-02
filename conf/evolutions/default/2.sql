# Tasks schema

# --- !Ups

INSERT INTO task (label) VALUES ('Comprar el pan');
INSERT INTO task (label) VALUES ('Recoger el libro');

# --- !Downs

DELETE FROM task;