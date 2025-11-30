package com.example.appfinal.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appfinal.repository.ContactRepositorySingleton
import com.example.appfinal.model.Contact
import kotlinx.coroutines.launch

// El ViewModel gestiona el estado y la lógica de la UI.
class ContactViewModel : ViewModel() {


    // Constante para la validación: Acepta letras (cualquier idioma con \p{L}), espacios, puntos, guiones y apóstrofes.

    private val VALID_NAME_REGEX = Regex("^[\\p{L} .'-]+$")



    // La View lo observa para actualizar la lista automáticamente.
    val allContacts : LiveData<List<Contact>> = ContactRepositorySingleton.allContacts


    // message: LiveData interno (MutableLiveData) para notificar a la View sobre estados
    private val _message = MutableLiveData<String>()
    val message: LiveData<String> = _message


    // Lógica de validación de nombres
    private fun isNameValid(nombre: String): Boolean {
        return VALID_NAME_REGEX.matches(nombre)
    }

    // Función llamada desde la View para agregar un nuevo contacto.
    fun insertContact(nombre: String, telefono: String){

        val cleanName = nombre.trim()
        val cleanPhone = telefono.trim()


        // Validación de datos simple (Lógica de Negocio).
        if(nombre.isBlank() || telefono.isBlank()){
            _message.value = "Por favor completa todos los campos"
            return
        }

        //  Llamar a la validación de formato
        if(!isNameValid(cleanName)){
            _message.value = "El nombre solo puede contener letras, espacios, puntos, guiones y apóstrofes."
            return
        }

        // Usa viewModelScope para lanzar una Coroutine,
        // asegurando que la operación se ejecuta en el hilo correcto (Repository se encargará de IO).
        viewModelScope.launch{
            try{
                // Llama al Repository para la persistencia.
                ContactRepositorySingleton.insertContact(nombre.trim(),telefono.trim())
                _message.value = "Contacto agregado exitosamente"
            }catch(e:Exception){
                // Manejo de error: notifica a la View si falla la BD.
                _message.value = "Error al agregar el contacto: ${e.message}"
            }
        }
    }



    // Función llamada desde la View para eliminar un contacto.
    fun deleteContact(contact: Contact){
        viewModelScope.launch {
            try{
                // Llama al Repository para eliminar.
                ContactRepositorySingleton.deleteContact(contact.id)
                _message.value = "Contacto eliminado exitosamente"
            }catch(e:Exception){
                _message.value = "Error al eliminar el contacto: ${e.message}"
            }
        }
    }

    // Función para actualizar un contacto (implementación CRUD).
    fun updateContacto(contact : Contact){
        // No permitir campos vacíos al editar.
        if(contact.nombre.isBlank() || contact.telefono.isBlank()){
            _message.value = "Por favor completa todos los campos para actualizar"
            return
        }

        viewModelScope.launch {
            try {
                // Llama al Repository para actualizar.
                ContactRepositorySingleton.updateContact(contact)
                _message.value = "Contacto actualizado exitosamente"

            }catch(e:Exception){
                // Se corrigió el mensaje de error para ser específico de la actualización.
                _message.value = "Error al actualizar el contacto: ${e.message}"
            }
        }
    }


    // Limpia el mensaje de estado después de que la View lo ha consumido.
    fun clearMessage(){
        _message.value= ""
    }
}