package com.example.appfinal

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.appfinal.adapter.ContactAdapter
import com.example.appfinal.databinding.ActivityMainBinding
import com.example.appfinal.model.Contact
import com.example.appfinal.repository.ContactRepositorySingleton
import com.example.appfinal.viewmodel.ContactViewModel

// La Activity es la VISTA (View) en el patrón MVVM.
// Su rol es configurar la UI, capturar eventos y observar datos.
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var contactViewModel: ContactViewModel
    private lateinit var contactAdapter: ContactAdapter

    // Variable de estado para controlar el modo Edición.
    // Si tiene un valor, estamos editando ese ID. Si es null, estamos agregando.
    private var currentContactId: Long? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ContactRepositorySingleton.initialize(this)

        contactViewModel = ViewModelProvider(this)[ContactViewModel::class.java]

        setupRecyclerView()
        setupObservers()
        setupListeners()
        // Inicializa la UI en modo Agregar
        updateFormUI(isEditing = false)
    }

    // Configura el RecyclerView con el adaptador y el LayoutManager.
    private fun setupRecyclerView(){

        //  Se pasan AMBAS funciones lambda al Adapter.
        contactAdapter = ContactAdapter(
            onDeleteClick = { contact ->
                showDeleteConfirmationDialog(contact)
            },
            editClickListener = { contact ->
                // Lógica de Edición: Llama a la función que carga los datos
                onEditContactSelected(contact)
            }
        )

        binding.rvContactos.apply {
            adapter = contactAdapter
            layoutManager = LinearLayoutManager(this@MainActivity)
        }
    }

    // Configura los observadores de LiveData.
    private fun setupObservers(){
        contactViewModel.allContacts.observe(this){contacts ->
            contactAdapter.submitList(contacts)
        }

        // Observa mensajes de estado (éxito o error).
        contactViewModel.message.observe(this){message ->
            if(message.isNotEmpty()){
                showMessage(message)
                contactViewModel.clearMessage()

                //  Si la operación fue un éxito (agregar/actualizar),
                // y estábamos en modo edición, reseteamos el formulario.
                if (currentContactId != null && message.contains("exitosamente")) {
                    cancelEdit()
                }
            }
        }
    }

    // Configura los listeners de interacción del usuario.
    private fun setupListeners(){
        // El botón principal llama a una función que decide si AGREGAR o ACTUALIZAR.
        binding.btnAgregar.setOnClickListener{
            addOrUpdateContact()
        }

        //  Listener para el botón Cancelar.
        binding.btnCancelar.setOnClickListener{
            cancelEdit()
        }
    }


    // Muestra el diálogo de confirmación.
    private fun showDeleteConfirmationDialog(contact: Contact) {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.confirm_deletion_title)) // "¿Confirmar Eliminación?"
            .setMessage(getString(R.string.confirm_deletion_message, contact.nombre)) // "¿Está seguro de eliminar a [Nombre]?"
            // Botón POSITIVO
            .setPositiveButton(getString(R.string.delete_button_text)) { dialog, which ->
                // Si el usuario confirma, ahora sí llamamos a la lógica de eliminación.
                contactViewModel.deleteContact(contact)
                if (contact.id == currentContactId) cancelEdit()
            }
            // Botón NEGATIVO
            .setNegativeButton(getString(R.string.cancel_button_text)) { dialog, which ->
                dialog.dismiss() // Cierra el diálogo sin hacer nada más.
            }
            .show() // Muestra el diálogo al usuario.
    }




    // Unifica la lógica de agregar y actualizar.
    private fun addOrUpdateContact(){
        val nombre = binding.etNombre.text.toString().trim()
        val telefono = binding.etTelefono.text.toString().trim()


        if (currentContactId != null) {
            //  el ID existe, llama a la función de ACTUALIZAR.
            val updatedContact = Contact(currentContactId!!, nombre, telefono)
            contactViewModel.updateContacto(updatedContact)

        } else {
            // MODO AGREGAR: El ID es null, llama a la función de INSERTAR.
            contactViewModel.insertContact(nombre, telefono)
        }


        // Se mantiene la llamada aquí por si la inserción es exitosa.
        if (currentContactId == null) clearFields()
    }

   // Carga los datos del contacto seleccionado en el formulario.
    private fun onEditContactSelected(contact: Contact){
        // 1. Cargar el ID del contacto en el estado
        currentContactId = contact.id

        // 2. Cargar los datos en los EditText
        binding.etNombre.setText(contact.nombre)
        binding.etTelefono.setText(contact.telefono)

        // 3. Cambiar la UI a modo "Editar"
        updateFormUI(isEditing = true)

        // Enfoca el cursor para que el usuario pueda empezar a editar
        binding.etNombre.requestFocus()
    }

   //Resetea la UI al modo "Agregar".
    private fun cancelEdit(){
        currentContactId = null // Resetear el estado de edición
        clearFields()
        updateFormUI(isEditing = false)
    }

    // Gestiona el texto y la visibilidad de los botones y el título del formulario.
    private fun updateFormUI(isEditing: Boolean){
        if(isEditing){
            binding.tvFormTitle.text = getString(R.string.edit_contact_title)
            binding.btnAgregar.text = getString(R.string.update_button_text)
            binding.btnCancelar.visibility = View.VISIBLE
        } else {
            binding.tvFormTitle.text = getString(R.string.add_contact_title)
            binding.btnAgregar.text = getString(R.string.add_button_text)
            binding.btnCancelar.visibility = View.GONE
        }
    }


    // Limpia los campos de texto.
    private fun clearFields(){
        binding.etNombre.text?.clear()
        binding.etTelefono.text?.clear()
    }


    // Muestra un mensaje temporal en el TextView de estado.
    private fun showMessage(message: String){
        binding.tvMensaje.apply {
            text = message
            visibility = View.VISIBLE
        }

        // Oculta el mensaje después de 3.8 segundos.
        binding.root.postDelayed({binding.tvMensaje.visibility = View.GONE}, 3800)
    }
}