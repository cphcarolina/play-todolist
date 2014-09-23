# Tasks schema

# --- !Ups

INSERT INTO task (id, label) VALUES (1,'Comprar el pan');
INSERT INTO task (id, label) VALUES (2,'Recoger el libro');



# --- !Downs

DELETE FROM task;