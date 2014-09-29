package models

import anorm._
import anorm.SqlParser._

import play.api.db._
import play.api.Play.current

import play.api.libs.json._

// Definición de los parámetros de la tarea
case class Task(id: Long, label: String, usuario: String)

// DEfinición de los métodos de la tarea
object Task {

   // Auxiliar para extraer la lista 
   // de tareas de la BD (parser)
   val task = {
      get[Long]("task.id") ~
      get[String]("task.label") ~
      get[String]("usuario.nombre")map {
      case id~label~usuario => Task(id, label, usuario)
      }
   }

   // Hace un cast entre Option[Long] y Long
   // para devolver los id tras las sentencias SQL
   def unroll(opt: Option[Long]): Long = opt getOrElse 0

   
   // Método para obtener todas las tareas
   def all(usuario: String): List[Task] = DB.withConnection { implicit c =>
      SQL("""
         Select task.id, task.label, usuario.nombre 
         from task, usuario
         where usuario.nombre = {usuario}
         and task.usuarioFK = usuario.id
         """)
      .on('usuario -> usuario)
      .as(task *) 
      // (task *) sirve para crear tantas tareas como líneas en la tabla existan
   }

   // Método para obtener una sola tarea por identificador
   // sino la tarea no existe, devuelve una tarea vacía {"id":0,"label":""}
   def obtener(id: Long): Task = DB.withConnection { implicit c =>
      val rows = SQL("""
         select task.id, task.label, usuario.nombre 
         from task, usuario
         where task.id = {id}
         and usuario.id = task.usuarioFK
         """)
      .on('id -> id)
      .apply()
      
      if(!rows.isEmpty){
      val firstRow = rows.head
         new Task(
            firstRow[Long]("task.id"),
            firstRow[String]("task.label"),
            firstRow[String]("usuario.nombre"))
      }
      else{ new Task(0,"","") }
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
   def delete(id: Long): Int = {
      val lineas: Int = DB.withConnection { implicit c =>
         SQL("delete from task where id = {id}").on(
            'id -> id
         ).executeUpdate()
      }
      lineas
   }
}