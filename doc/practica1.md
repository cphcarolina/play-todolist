# Play Todolist Documentación v1.0

Documentación de la primera iteración del API REST Play Todolist desarrollada para la asignatura Metodologías Ágiles de Desarrollo del Software de la Universidad de Alicante.

## Tareas

Las tareas están compuestas de los siguientes elementos:
* id: identificador único.
* label: contenido.
* usuario: usuario a la que pertenece.
* fecha: fecha límite de la tarea.

Todas las tareas pertenecen a un usuario del sistema, no pudiendo existir tareas sin usuario. Pero existe un usuario por defecto llamado _"anonimo"_.

### Creación de tareas

Ruta: post /tasks
  cuerpo: 
    String label
Descripción: Crea una tarea nueva del usuario _anonimo_ y con la fecha actual.
Resultado: Un objeto Json de la tarea creada.
  
  Ejemplo:

  Petición:
    POST /tasks HTTP/1.1
    label=Cosas

  Respuesta:

    {
      "id": 1,
      "label": "Cosas",
      "usuario": "anonimo",
      "fecha": "2014-10-16"
    }

Ruta: post /{usuario}/tasks
  cuerpo:
    String label
Descripción: Crea una tarea nueva del usuario _{usuario}_ y con la fecha actual. Se comprueba la existencia del usuario en la base de datos, en caso de no existir se transmitirá un mensaje de error 404.
Resultado: Un objeto Json de la tarea creada.
  
  Ejemplo:

  Petición:
    POST /maria/tasks HTTP/1.1
    label=Cosas

  Respuesta:

    {
      "id": 2,
      "label": "Cosas",
      "usuario": "maria",
      "fecha": "2014-10-16"
    }

### Obtención de una tarea

Ruta: get /tasks/{id}
Descripción: Obtiene una tarea especificada por el identificador. En el caso de que no existir una tarea con ese identificador se transmitirá un mensaje de error 404.
Resultado: Un objeto Json de la tarea con el identificador _{id}_.

### Listados de tareas

Ruta: get /tasks
Descripción: Obtiene una lista de tareas del usuario _anonimo_.
Resultado: Un objeto Json con la lista.

Ruta: get /{usuario}/tasks
Descripción: Obtiene una lista de tareas del usuario indicado _{usuario}_. En el caso de no existir el usuario se devolverá un mensaje de error 404.
Resultado: Un objeto Json con la lista.

  Ejemplo:

  Petición:
    GET /maria/tasks HTTP/1.1

  Respuesta:

    [
      {
        "id": 2,
        "label": "Cosas",
        "usuario": "maria",
        "fecha": "2014-10-16"
      }
    ]

Ruta: get /que_hacer
Descripción: Comprueba las tareas del usuario _anonimo_ y obtiene aquellas tareas cuya fecha de finalización coincide con el día actual o es posterior.
Resultado: Un objeto Json con la lista de tareas.

Ruta: get /{usuario}/que_hacer
Descripción: Comprueba las tareas del usuario indicado _{usuario}_ y obtiene aquellas tareas cuya fecha de finalización coincide con el día actual o es posterior. En el caso de que el usuario no existe, se devuelve un mensaje de error 404.
Resultado: Un objeto Json con la lista de tareas.

### Modificación de tareas

Ruta: put /task/{id}/{days}
Descripción: Modifica la fecha de finalización de la tarea indicada _{id}_. En el caso de que la tarea no exista, muestra un mensaje de error 404. 
Resultado: Un objeto Json con la tarea ya modificada.

  Ejemplo:

  Petición:
    PUT /tasks/2/3 HTTP/1.1    

  Respuesta:

    {
      "id": 2,
      "label": "Cosas",
      "usuario": "maria",
      "fecha": "2014-10-19"
    }


### Eliminación de tareas

Ruta: delete /tasks/{id}
Descripción: Elimina la tarea especificada por el identificación. En el caso de que el identificador no pertenezca a ninguna tarea, se mostrará un mensaje de error 404.
Resultado: El identificador de la tarea eliminada.