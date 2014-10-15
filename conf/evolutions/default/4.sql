# Tasks schema

# --- !Ups

ALTER TABLE task
   ADD fecha date;

# --- !Downs

ALTER TABLE task
	DROP COLUMN fecha;