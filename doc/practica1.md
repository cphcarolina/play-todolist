# Play Todolist Documentación v1.0

Documentación de la primera iteración del API REST Play Todolist desarrollada para la asignatura Metodologías Ágiles de Desarrollo del Software de la Universidad de Alicante.

## Tareas

Las tareas están compuestas de los siguientes elementos:
* id: identificador único.
* label: contenido.
* usuario: usuario a la que pertenece.

Todas las tareas pertenecen a un usuario del sistema, no pudiendo existir tareas sin usuario. Pero existe un usuario por defecto llamado _"anonimo"_.

### Obtención de una tarea

get /tasks/{id}

### Listados de tareas
Se puede obtener un

get /tasks

get /{usuario}/tasks

### Creación de tareas

post /tasks

post /{usuario}/tasks

### Eliminación de tareas

delete /tasks/{id}
