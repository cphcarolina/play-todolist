package models

import anorm._
import anorm.SqlParser._

import play.api.db._
import play.api.Play.current

import java.util.Date
import java.util.Calendar
import java.text.SimpleDateFormat
//import org.joda.time.{DateTime}

import play.api.libs.json._
import play.api.libs.functional.syntax._

// Definición de los parámetros de la tarea
case class Task(id: Long, label: String, usuario: String, fecha: Option[Date]) {
   val getFecha = fecha 
}

// Definición del json
object DateWrites extends Writes[Option[Date]] {
   val df = new SimpleDateFormat("yyyy-MM-dd")

   def writes(fecha: Option[Date]): JsValue = {
      fecha match {
         case Some(fecha) => {
           JsString(df.format(fecha))
         }
         case None => JsString(" - ")
      }
   }
}

// Definición de los métodos de la tarea
object Task {

   // Auxiliar para extraer la lista 
   // de tareas de la BD (parser)
   val task = {
      get[Long]("task.id") ~
      get[String]("task.label") ~
      get[String]("usuario.nombre") ~
      get[Option[Date]]("task.fecha") map {
         case id~label~usuario~fecha => Task(id, label, usuario, fecha)
      }
   }

   implicit val taskWrites: Writes[Task] = (
       (JsPath \ "id").write[Long] and
       (JsPath \ "label").write[String] and
       (JsPath \ "usuario").write[String] and
       (JsPath \ "fecha").write(DateWrites)
       //(JsPath \ "fecha").write[String].contramap[DateTime](dt => dtf.print(dt))
     )(unlift(Task.unapply))
   
   // Método para obtener todas las tareas
   def all(usuario: String): Option[JsValue] = DB.withConnection { implicit c =>
      // Comprobamos si existe el usuario
      var rows = SQL("""
         select id from usuario
         where nombre = {usuario}
         """)
      .on('usuario -> usuario)
      .apply()

      if(!rows.isEmpty) {
         // Como existe, listamos las tareas
         var firstRow = rows.head
         var user: Long = firstRow[Long]("id")

         var lista: List[Task] = SQL("""
            Select task.id, task.label, usuario.nombre, task.fecha 
            from task, usuario
            where task.usuarioFK = usuario.id
            and usuario.id = {user}
         """)
         .on('user -> user)
         .as(task *)

         Some(Json.toJson(lista))
      }
      else { None }
   }

   // Método para obtener todas las tareas futuras (fecha >= hoy)
   def upcoming(usuario: String): Option[JsValue] = DB.withConnection { implicit c =>
      // Comprobamos si existe el usuario
      var rows = SQL("""
         select id from usuario
         where nombre = {usuario}
         """)
      .on('usuario -> usuario)
      .apply()

      if(!rows.isEmpty) {
         // Como existe, listamos las tareas
         var firstRow = rows.head
         var user: Long = firstRow[Long]("id")

         var fecha: Date = new Date()

         var lista: List[Task] = SQL("""
            Select task.id, task.label, usuario.nombre, task.fecha 
            from task, usuario
            where task.usuarioFK = usuario.id
            and usuario.id = {user}
            and (task.fecha is null
               or task.fecha >= {fecha})
         """)
         .on('user -> user,
             'fecha -> fecha)
         .as(task *)
         var json = Json.toJson(lista)

         Some(json)
      }
      else { None }
   }


   // Método para obtener una sola tarea por identificador
   // sino la tarea no existe, devuelve una tarea vacía {"id":0,"label":""}
   def obtain(id: Long): Option[JsValue] = DB.withConnection { implicit c =>
      var rows = SQL("""
         select task.id, task.label, usuario.nombre, task.fecha
         from task, usuario
         where task.id = {id}
         and usuario.id = task.usuarioFK
         """)
      .on('id -> id)
      .apply()
//      .as(Task.task.singleOpt)      
      
      if(!rows.isEmpty) {
         var firstRow = rows.head
         var tarea = new Task(
            firstRow[Long]("task.id"),
            firstRow[String]("task.label"),
            firstRow[String]("usuario.nombre"),
            firstRow[Option[Date]]("task.fecha"))

         Some(Json.toJson(tarea))
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
         val fecha: Date = new Date()

         val id: Option[Long] = SQL("""
            insert into task (label, usuarioFK, fecha) values ({label}, {user}, {fecha})
            """)
         .on(
            'label -> label,
            'user -> user,
            'fecha -> fecha)
         .executeInsert()

         id
      }
      else { None }
   }

   // Pospone una tarea los días especificados
   def postpone(id: Long, day: Int): Option[JsValue] = DB.withConnection { implicit c =>
      var tarea = obtain(id)
      var rows = SQL("""
         select task.id, task.label, usuario.nombre, task.fecha
         from task, usuario
         where task.id = {id}
         and usuario.id = task.usuarioFK
         """)
      .on('id -> id)
      .apply()
     
      
      if(!rows.isEmpty) {
         var firstRow = rows.head
         var tarea = new Task(
            firstRow[Long]("task.id"),
            firstRow[String]("task.label"),
            firstRow[String]("usuario.nombre"),
            firstRow[Option[Date]]("task.fecha"))

         var fecha = tarea.getFecha
         var aux: Date = new Date()
         fecha match {
            case Some(fecha) => {
              aux = fecha
            }
         }

         var cal: Calendar = Calendar.getInstance()
         cal.setTime(aux)
         cal.add(Calendar.DATE,day)
         aux = cal.getTime()

         SQL("""
            update task
            set fecha = {aux}
            where id = {id}
         """)
         .on('aux -> aux,
             'id -> id)
         .executeUpdate()

         obtain(id)
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