package controllers

import play.api._
import play.api.mvc._

import play.api.data._
import play.api.data.Forms._

import models.Task

object Application extends Controller {

   // Variable formulario
   val taskForm = Form(
      "label" -> nonEmptyText
   )

   // Acceso a la raíz (índice)
  def index = Action {
    Redirect(routes.Application.tasks)
  }

  // Listado de tareas y formulario de creación de las mismas
  def tasks = Action {
    Ok(views.html.index(Task.all(), taskForm))
  }

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
  def deleteTask(id: Long) = TODO
}