package com.example.appfinal.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.appfinal.database.ContactDbHelper
import com.example.appfinal.model.Contact
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

// Repositorio implementado como un Singleton (única instancia).
// Responsable de gestionar el acceso a la base de datos (BD).
object ContactRepositorySingleton {

    private var dbHelper: ContactDbHelper? = null
    // MutableLiveData que se actualizará con los datos de la BD.
    private val _allContacts = MutableLiveData<List<Contact>>()
    // LiveData inmutable que el ViewModel observa.
    val allContacts: LiveData<List<Contact>> = _allContacts


    // Método MANDATORIO para inicializar la BD antes de usar el Repositorio.
    fun initialize(context: Context){
        if(dbHelper == null){
            dbHelper = ContactDbHelper(context)
            loadcontacts() // Carga inicial de datos al iniciar la app.
        }
    }

    // Función de seguridad para asegurar que la BD fue inicializada.
    private fun requireDbHelper(): ContactDbHelper {
        return dbHelper ?: throw IllegalStateException(
            "Singleton no ha sido inicializado. LLama a initialize(context) primero"
        )
    }

    // Carga los contactos de la BD y actualiza el LiveData.
    private fun loadcontacts(){
        _allContacts.value = requireDbHelper().getAllContacts()
    }

    // --- OPERACIONES DE PERSISTENCIA (CRUD) ---



    suspend fun insertContact(nombre: String, telefono: String){


        withContext(Dispatchers.IO){
            requireDbHelper().insertContact(nombre,telefono)

            withContext(Dispatchers.Main){
                loadcontacts() // Recarga los datos para que el LiveData se actualice.
            }
        }
    }

    suspend fun updateContact(contact: Contact){
        withContext(Dispatchers.IO){
            requireDbHelper().updateContact(contact)
            withContext(Dispatchers.Main){
                loadcontacts()
            }
        }
    }

    suspend fun deleteContact(id:Long){
        withContext(Dispatchers.IO){
            requireDbHelper().deleteContact(id)
            withContext(Dispatchers.Main){
                loadcontacts()
            }
        }
    }

    // Función para eliminar TODOS los contactos
    suspend fun deleteAllContacts(){
        withContext(Dispatchers.IO){
            requireDbHelper().deleteAllContacts()
            withContext(Dispatchers.Main){
                loadcontacts()
            }
        }
    }
}