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
      get[String]("usuario.nombre") map {
         case id~label~usuario => Task(id, label, usuario)
      }
   }
   
   // Método para obtener todas las tareas
   def all(usuario: String): Option[List[Task]] = DB.withConnection { implicit c =>
      // Comprobamos si existe el usuario
      val rows = SQL("""
         select id from usuario
         where nombre = {usuario}
         """)
      .on('usuario -> usuario)
      .apply()

      if(!rows.isEmpty) {
         // Como existe, listamos las tareas
         val firstRow = rows.head
         val user: Long = firstRow[Long]("id")

         var lista: List[Task] = SQL("""
            Select task.id, task.label, usuario.nombre 
            from task, usuario
            where task.usuarioFK = usuario.id
            and usuario.id = {user}
         """)
         .on('user -> user)
         .as(task *)

         Some(lista)
      }
      else { None }
   }

   // Método para obtener una sola tarea por identificador
   // sino la tarea no existe, devuelve una tarea vacía {"id":0,"label":""}
   def obtener(id: Long): Option[Task] = DB.withConnection { implicit c =>
      var rows = SQL("""
         select task.id, task.label, usuario.nombre 
         from task, usuario
         where task.id = {id}
         and usuario.id = task.usuarioFK
         """)
      .on('id -> id)
      .apply()

//      .as(Task.task.singleOpt)      
      
      if(!rows.isEmpty) {
         var firstRow = rows.head
         Some(new Task(
            firstRow[Long]("task.id"),
            firstRow[String]("task.label"),
            firstRow[String]("usuario.nombre")))
      }
      else { None }
   }


   // Método para crear tareas
   // devuelve el identificador de la tarea creada
   // o 0 en caso de que exista un error
   def create(label: String, usuario: String): Option[Long] = DB.withConnection { implicit c =>
      // Comprobamos si existe el usuario
      val rows = SQL("""
         select id from usuario
         where nombre = {usuario}
         """)
      .on('usuario -> usuario)
      .apply()

      if(!rows.isEmpty) {
         // Como existe, creamos la tarea
         val firstRow = rows.head
         val user: Long = firstRow[Long]("id")

         val id: Option[Long] = SQL("""
            insert into task (label, usuarioFK) values ({label}, {user})
            """)
         .on(
            'label -> label,
            'user -> user)
         .executeInsert()
         
         id
      }
      else { None }
   }

   // Eliminación de tareas
   def delete(id: Long): Int = {
      DB.withConnection { implicit c =>
         SQL("delete from task where id = {id}").on(
            'id -> id
         ).executeUpdate()
      }

   }
}