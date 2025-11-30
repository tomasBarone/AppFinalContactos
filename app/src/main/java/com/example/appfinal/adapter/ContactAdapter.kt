package com.example.appfinal.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.appfinal.databinding.ItemContactBinding
import com.example.appfinal.model.Contact

class ContactAdapter (
    private val onDeleteClick:(Contact) -> Unit,

    private val editClickListener: (Contact) -> Unit
) : ListAdapter<Contact,ContactAdapter.ContactViewHolder>(ContactDiffCallback()){


    class ContactViewHolder(
        private val binding: ItemContactBinding,
        private val onDeleteClick:(Contact) -> Unit,

        private val onEditClick:(Contact) -> Unit
    ): RecyclerView.ViewHolder(binding.root){
        fun bind(contact: Contact){
            binding.apply {
                tvNombre.text = contact.nombre
                tvTelefono.text = contact.telefono

                // Listener de Eliminación
                btnEliminar.setOnClickListener{
                    onDeleteClick(contact)
                }

                //  ADJUNTAR EL LISTENER AL ELEMENTO RAÍZ (toda la tarjeta)
                // Cuando el usuario toca la tarjeta completa, se dispara la edición.
                root.setOnClickListener {
                    onEditClick(contact)
                }
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val binding = ItemContactBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )


        // Se pasa la función de eliminar y la función de editar.
        return ContactViewHolder(binding, onDeleteClick, editClickListener)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

}

class ContactDiffCallback: DiffUtil.ItemCallback<Contact>(){
    override fun areItemsTheSame(oldItem: Contact, newItem: Contact): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Contact, newItem: Contact): Boolean {
        return oldItem == newItem
    }
}