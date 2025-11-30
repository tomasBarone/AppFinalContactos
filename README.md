AppFinal - Sistema Básico de Gestión de Contactos (CRUD)

** Descripción del Proyecto **

Este es un proyecto de ejemplo que implementa un sistema básico y funcional para la gestión de contactos, demostrando la aplicación de la arquitectura Model-View-ViewModel (MVVM) en Kotlin.

La aplicación permite a los usuarios realizar las cuatro operaciones fundamentales de persistencia de datos (CRUD): Crear, Leer, Actualizar y Eliminar contactos, utilizando una base de datos local (SQLite o Room simulado por el DbHelper).



** Características Principales **

Creación de Contactos (C): Agregar nuevos registros a la base de datos.

Listado de Contactos (R): Mostrar todos los contactos en tiempo real mediante un RecyclerView que observa un LiveData.

Edición de Contactos (U): Permite modificar el nombre y el teléfono de un contacto existente al hacer clic en su elemento de la lista.

Eliminación Segura (D): Al hacer clic en el ícono de eliminar, se muestra un diálogo de confirmación (AlertDialog) para prevenir borrados accidentales.

Validación de Datos: Restringe el campo de Nombre a solo caracteres alfabéticos, espacios, puntos, guiones y apóstrofes, rechazando números o símbolos para asegurar la integridad de los datos.




** Arquitectura y Tecnologías**

El proyecto sigue el patrón Model-View-ViewModel (MVVM) para lograr una clara separación de responsabilidades y facilitar la mantenibilidad y las pruebas unitarias.




**Componentes Clave:

View (Vista: MainActivity.kt):

Maneja la interfaz de usuario, los clics del usuario y la gestión del estado del formulario (modo "Agregar" vs. "Editar").

Observa el LiveData expuesto por el ViewModel.

Contiene la lógica para mostrar el diálogo de confirmación antes de eliminar.

ViewModel (Lógica: ContactViewModel.kt):

Contiene la lógica de negocio (ej: validación de campos vacíos, validación de formato con Regex).

Expone el estado de los datos (allContacts: LiveData) y mensajes de estado (message: LiveData).

Utiliza viewModelScope y Coroutines para delegar las operaciones de persistencia al Repositorio sin bloquear el hilo principal.

Repository (Abstracción: ContactRepositorySingleton.kt):

Actúa como una fuente única de verdad, mediando entre el ViewModel y las fuentes de datos (en este caso, el DbHelper).

Es responsable de la lógica de hilos, asegurando que las operaciones de I/O se ejecuten en el background.

Model (Contact.kt):

La simple data class que define la estructura del dato (id, nombre, teléfono).



**Stack Tecnológico**

Lenguaje: Kotlin

Arquitectura: MVVM

Concurrencia: Kotlin Coroutines (viewModelScope)

Datos: SQLite (accedido vía DbHelper o simulando una capa DAO)

UI: RecyclerView, LiveData, ViewModel de AndroidX


Video Demostrativo
A traves del siguiente enlace se puede acceder al video que muestra la funcionalidad de la Aplicacion.

https://drive.google.com/drive/folders/16pbyHb2z6v9zGcBMU4YxSugwSUZkoDxS
