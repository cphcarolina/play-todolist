package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.libs.json._
import java.util.Date
//import org.joda.time.DateTime
//import org.joda.time.format.DateTimeFormat
import play.api.libs.functional.syntax._
import models.Task

object Application extends Controller {

  // Variable formulario
  val taskForm = Form(
    "label" -> nonEmptyText
  )

  // Para la conversión a JSON
 // val dtf = DateTimeFormat.forPattern("MM/dd/YYYY")

  implicit val taskWrites: Writes[Task] = (
    (JsPath \ "id").write[Long] and
    (JsPath \ "label").write[String] and
    (JsPath \ "usuario").write[String] and
    //(JsPath \ "fecha").write[String].contramap[DateTime](dt => dtf.print(dt))
    (JsPath \ "fecha").write[Option[Date]]
  )(unlift(Task.unapply))

   // Acceso a la raíz (índice)
  def index = Action {
    Redirect(routes.Application.tasks("anonimo"))
  }

  // Listado de tareas
  def tasks(usuario: String = "anonimo") = Action {
    var tareas = Task.all(usuario)

    tareas match {
      case Some(tareas) => {
        var json = Json.toJson(tareas)
        Ok(json)

      }
      case None => NotFound("El usuario "+usuario+" no existe.")
    }
  }

  // Obtención de una tarea contreta
  def obtenerTask(id: Long) = Action  {
    var tarea = Task.obtener(id)

    tarea match {
      case Some(tarea) => {
        var json = Json.toJson(tarea)
        Ok(json)        
      }
      case None => NotFound("La tarea con el identificador "+id+" no existe.")
    }
  }

  // Creación de una tarea (desde el template)
  def newTask(usuario: String = "anonimo") = Action { implicit request =>
    taskForm.bindFromRequest.fold(
         errors => BadRequest("Error en la petición realizada."),
         label => {
            var id: Option[Long] = Task.create(label, usuario)

            id match {
              case Some(id)  => {
                var tarea = Task.obtener(id)
                var json = Json.toJson(tarea)
                Ok(json)
              }
              case None => NotFound("El usuario "+usuario+" no existe.")
            }
         }
      )
  }

  // Eliminación de tareas (desde el template)
  def deleteTask(id: Long) = Action {
    if(Task.delete(id)==0){ NotFound("La tarea "+id+" no existe"); }
    else{ Ok("Tarea "+id+" ha sido eliminada con éxito") }  
  }

  // Listar las tareas futuras (fecha >= hoy)
  def futuras(usuario: String = "anonimo") = TODO

  // Retrasa una tarea los días indicados
  def posponer(id: Long, day: Long) = TODO

}