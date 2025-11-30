package com.example.appfinal.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.appfinal.model.Contact

// ContactDbHelper es responsable de gestionar la conexión y el esquema de SQLite.
class ContactDbHelper(context: Context): SQLiteOpenHelper (

    context,
    DATABASE_NAME,
    null,
    DATABASE_VERSION
) {
    // Definiciones de constantes para la base de datos y la tabla (Buenas Prácticas).
    companion object{
        private const val DATABASE_NAME = "contacts.db"
        private const val DATABASE_VERSION = 1
        const val TABLE_CONTACTS = "contacts"
        const val COLUMN_ID = "id"
        const val COLUMN_NOMBRE = "nombre"
        const val COLUMN_TELEFONO = "telefono"

        // Sentencia SQL para la creación de la tabla.
        private const val CREATE_TABLE_CONTACTS = """
            CREATE TABLE $TABLE_CONTACTS(
            $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
            $COLUMN_NOMBRE TEXT NOT NULL,
            $COLUMN_TELEFONO TEXT NOT NULL
            )
        """
    }

    // Se llama solo la PRIMERA vez que se crea la base de datos.
    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(CREATE_TABLE_CONTACTS)
    }

    // Se llama cuando se cambia DATABASE_VERSION. Se usa para migrar datos.
    override fun onUpgrade(db: SQLiteDatabase?, p1: Int, p2: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_CONTACTS")
        onCreate(db)
    }

    // Inserta un nuevo contacto en la tabla.
    fun insertContact(nombre: String, telefono: String):Long{

        val db = writableDatabase // Accede a la base de datos para escribir
        val values = ContentValues().apply{
            put(COLUMN_NOMBRE,nombre)
            put(COLUMN_TELEFONO,telefono)
        }

        // db.insert retorna el ID de la nueva fila o -1 si hay error.
        return db.insert(TABLE_CONTACTS, null, values)
    }

    // Obtiene TODOS los contactos de la base de datos.
    fun getAllContacts(): List<Contact>{

        val contacts = mutableListOf<Contact>()
        val db = readableDatabase // Accede a la base de datos para leer

        // Ejecuta una consulta (SELECT * FROM contacts ORDER BY nombre ASC)
        val cursor : Cursor = db.query(
            TABLE_CONTACTS,
            arrayOf(COLUMN_ID, COLUMN_NOMBRE, COLUMN_TELEFONO),
            null, null, null, null,
            "$COLUMN_NOMBRE ASC") // Ordena alfabéticamente por nombre.

        // cursor.use asegura que el cursor se cierra después de ser usado (evita fugas de memoria).
        cursor.use {
            while(it.moveToNext()){ // Itera sobre cada fila (contacto)
                // Extracción de datos con gestión de índices segura (getColumnIndexOrThrow).
                val id = it.getLong(it.getColumnIndexOrThrow(COLUMN_ID))
                val nombre = it.getString(it.getColumnIndexOrThrow(COLUMN_NOMBRE))
                val telefono = it.getString(it.getColumnIndexOrThrow(COLUMN_TELEFONO))
                contacts.add(Contact(id,nombre,telefono))
            }
        }

        return contacts
    }

    // Actualiza los datos de un contacto existente.
    fun updateContact(contact: Contact): Int{
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NOMBRE,contact.nombre)
            put(COLUMN_TELEFONO,contact.telefono)
        }

        // WHERE clause para identificar la fila a actualizar por ID.
        return db.update(
            TABLE_CONTACTS,
            values,
            "$COLUMN_ID = ?",
            arrayOf(contact.id.toString())
        )
    }

    // Elimina un contacto por ID.
    fun deleteContact(id:Long):Int{
        val db = writableDatabase

        return db.delete(
            TABLE_CONTACTS,
            "$COLUMN_ID = ?",
            arrayOf(id.toString())
        )
    }

    // Elimina todos los contactos.
    fun deleteAllContacts():Int{
        val db = writableDatabase
        return db.delete(TABLE_CONTACTS,null,null) // Nulls para eliminar todas las filas.
    }
}