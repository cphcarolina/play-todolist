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
    (JsPath \ "label").write[String]
  )(unlift(Task.unapply))

   // Acceso a la raíz (índice)
  def index = Action {
    Redirect(routes.Application.tasks)
  }

  // Listado de tareas
  def tasks = Action {
    var json = Json.toJson(Task.all())
    Ok(json)
  }

  // Obtención de una tarea contreta
  def getTask(id: Long) = TODO

  // Creación de una tarea (desde el template)
  def newTask = Action { implicit request =>
    taskForm.bindFromRequest.fold(
         errors => BadRequest(views.html.index(Task.all(), errors)),
         label => {
            Task.create(label)
            Redirect(routes.Application.tasks)
         }
      )
  }

  // Eliminación de tareas (desde el template)
  def deleteTask(id: Long) = Action {
    Task.delete(id)
    Redirect(routes.Application.tasks)
  }
}