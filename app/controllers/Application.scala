package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.libs.json._
import play.api.libs.functional.syntax._
import models.Task

object Application extends Controller {

  // Variable formulario
  val taskForm = Form(
    "label" -> nonEmptyText
  )

  // Para la conversión a JSON
  implicit val taskWrites: Writes[Task] = (
    (JsPath \ "id").write[Long] and
    (JsPath \ "label").write[String] and
    (JsPath \ "usuario").write[String]
  )(unlift(Task.unapply))

   // Acceso a la raíz (índice)
  def index = Action {
    Redirect(routes.Application.tasks("anonimo"))
  }

  // Listado de tareas
  def tasks(usuario: String = "anonimo") = Action {
    var json = Json.toJson(Task.all(usuario))
    var error = Json.toJson(List(new Task(0,"","")))

    if(json == error) {
      NotFound("El usuario "+usuario+" no existe.")
    }
    else {
      Ok(json)
    }
  }

  // Obtención de una tarea contreta
  def obtenerTask(id: Long) = Action  {
    var tarea = Task.obtener(id)
    
    if(tarea.id!=0) {
      var json = Json.toJson(tarea)
      Ok(json)
    }
    else {
      NotFound("La tarea con el identificador "+id+" no existe.")
    }
  }

  // Creación de una tarea (desde el template)
  def newTask(usuario: String = "anonimo") = Action { implicit request =>
    taskForm.bindFromRequest.fold(
         errors => BadRequest("Error en la petición realizada."),
         label => {
            val id: Long = Task.create(label, usuario)

            var tarea = Task.obtener(id)
            
            if(tarea.id!=0) {
              var json = Json.toJson(tarea)
              Ok(json)
            }
            else {
              NotFound("El usuario "+usuario+" no existe.")
            }
         }
      )
  }

  // Eliminación de tareas (desde el template)
  def deleteTask(id: Long) = Action {
    if(Task.delete(id)==0){
      NotFound("La tarea "+id+" no existe");
    }
    else{
      Redirect(routes.Application.index)
    }
    
  }

}