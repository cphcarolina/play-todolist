package models

import anorm._
import anorm.SqlParser._

import play.api.db._
import play.api.Play.current

import play.api.libs.json._

// Definición de los parámetros de la tarea
case class Task(id: Long, label: String)

// DEfinición de los métodos de la tarea
object Task {

   // Auxiliar para extraer las tareas de la BD (parser)
   val task = {
      get[Long]("id") ~
      get[String]("label") map {
      case id~label => Task(id, label)
      }
   }

   
   // Método para obtener todas las tareas
   def all(): List[Task] = DB.withConnection { implicit c =>
      SQL("Select * from task").as(task *) 
      // (task *) sirve para crear tantas tareas como líneas en la tabla existan
   }

   // Método para obtener una sola tarea por identificador
   // sino la tarea no existe, devuelve una tarea vacía con el identificador 0
   def obtener(id: Long): Task = DB.withConnection { implicit c =>
      val rows = SQL("select * from task where id = {id}").on("id" -> id).apply()
      if(!rows.isEmpty){
      val firstRow = rows.head
         new Task(firstRow[Long]("id"),firstRow[String]("label"))
      }
      else{ new Task(0,"") }
   }


   // Método para crear tareas
   def create(label: String) {
      DB.withConnection { implicit c =>
         SQL("insert into task (label) values ({label})").on(
            'label -> label
         ).executeUpdate()
      }
   }

   // Eliminación de tareas
   def delete(id: Long) {
      DB.withConnection { implicit c =>
         SQL("delete from task where id = {id}").on(
            'id -> id
         ).executeUpdate()
      }
   }
}