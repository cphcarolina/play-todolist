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

   // Auxiliar para extraer la lista 
   // de tareas de la BD (parser)
   val task = {
      get[Long]("id") ~
      get[String]("label") map {
      case id~label => Task(id, label)
      }
   }

   // Hace un cast entre Option[Long] y Long
   // para devolver los id tras las sentencias SQL
   def unroll(opt: Option[Long]): Long = opt getOrElse 0

   
   // Método para obtener todas las tareas
   def all(): List[Task] = DB.withConnection { implicit c =>
      SQL("Select * from task").as(task *) 
      // (task *) sirve para crear tantas tareas como líneas en la tabla existan
   }

   // Método para obtener una sola tarea por identificador
   // sino la tarea no existe, devuelve una tarea vacía {"id":0,"label":""}
   def obtener(id: Long): Task = DB.withConnection { implicit c =>
      val rows = SQL("select * from task where id = {id}").on("id" -> id).apply()
      if(!rows.isEmpty){
      val firstRow = rows.head
         new Task(firstRow[Long]("id"),firstRow[String]("label"))
      }
      else{ new Task(0,"") }
   }


   // Método para crear tareas
   // devuelve el identificador de la tarea creada
   // o 0 en caso de que exista un error
   def create(label: String): Long = {
      val id: Option[Long] = DB.withConnection { implicit c =>
         SQL("insert into task (label) values ({label})").on(
            'label -> label
         ).executeInsert()
      }
      
      unroll(id)
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