package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import java.util.Date

import models.Task

object Application extends Controller {

  // Variable formulario
  val taskForm = Form(
    "label" -> nonEmptyText
  )

  // Acceso a la raíz (índice)
  def index = Action {
    Redirect(routes.Application.tasks("anonimo"))
  }

  // Listado de tareas
  def tasks(usuario: String = "anonimo") = Action {
    var tareas = Task.all(usuario)

    tareas match {
      case Some(tareas) => {
        Ok(tareas)
      }
      case None => NotFound("El usuario "+usuario+" no existe.")
    }
  }

  // Obtención de una tarea contreta
  def obtenerTask(id: Long) = Action  {
    var tarea = Task.obtener(id)

    tarea match {
      case Some(tarea) => {
        Ok(tarea)        
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

                tarea match {
                  case Some(tarea) => {
                    Ok(tarea)        
                  }
                  case None => NotFound("La tarea con el identificador "+id+" no existe.")
                }

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