package com.example.asgard_gym

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import java.text.SimpleDateFormat
import java.util.*

class DatabaseHelper(private val context: Context) : SQLiteOpenHelper(
    context, DATABASE_NAME, null, DATABASE_VERSION
)
 {
    companion object {
        private const val DATABASE_NAME = "asgard_gym.db"
        private const val DATABASE_VERSION = 177
        // Tablas
        private const val TABLE_ACTIVIDADES = "actividad"
        private const val TABLE_RESERVAS = "reserva"
        private const val TABLE_USUARIO = "usuario"
        private const val TABLE_HISTORICORESERVA = "historicoReserva"

        // Columnas de ACTIVIDAD
        private const val COLUMN_ID = "idActividad"
        private const val COLUMN_NAME = "nombre"
        private const val COLUMN_TYPE = "tipo"
        private const val COLUMN_DESCRIPTION = "descripcion"
        private const val COLUMN_HORARIO = "horario"
        private const val COLUMN_INSTRUCTOR = "instructor"
        private const val COLUMN_DURACION = "duracion"
        private const val COLUMN_IMAGE = "imagen"
        private const val COLUMN_DIA = "diaSemana"
        private const val COLUMN_PLAZAS = "plazasDisponibles"

        // Columnas de RESERVA
        private const val COLUMN_RESERVA_ID = "idReserva"
        private const val COLUMN_ACTIVIDAD_ID = "idActividad"
        private const val COLUMN_USUARIO_ID_RESERVA = "idUsuario"
        private const val COLUMN_FECHA_CLASE = "fechaClase"
        private const val COLUMN_FECHA = "fecha"
        private const val COLUMN_HORA = "hora"

        // Columnas de USUARIO
        private const val COLUMN_USUARIO_ID = "idUsuario"
        private const val COLUMN_USUARIO_NOMBRE = "nombre"
        private const val COLUMN_USUARIO_APELLIDOS = "apellidos"
        private const val COLUMN_USUARIO_DNI = "dni"
        private const val COLUMN_USUARIO_FECHA_NACIMIENTO = "fechaNacimiento"
        private const val COLUMN_USUARIO_CORREO = "correo"
        private const val COLUMN_USUARIO_CODIGO_POSTAL = "codigoPostal"
        private const val COLUMN_USUARIO_ACEPTACION_DATOS = "aceptacionDatos"
        private const val COLUMN_USUARIO_ADMIN = "esAdmin"
        private const val COLUMN_USUARIO_ENFERMEDADES = "enfermedades"

        // Columnas HISTORICORESERVA
        private const val COLUMN_HISTORICO_ID = "idHistorico"
        private const val COLUMN_HISTORICO_ID_RESERVA = "idReserva"
        private const val COLUMN_HISTORICO_FECHA_MODIFICACION = "fechaModificacion"
        private const val COLUMN_HISTORICO_VALOR_NUEVO = "valorNuevo"
        private const val COLUMN_HISTORICO_ACCION = "accion"
        private const val COLUMN_HISTORICO_NOMBRE_USUARIO = "nombreUsuario"
        private const val COLUMN_HISTORICO_DNI_USUARIO = "dniUsuario"
        private const val COLUMN_HISTORICO_NOMBRE_ACTIVIDAD = "nombreActividad"
        private const val COLUMN_HISTORICO_FECHA_CLASE = "fechaClase"
    }

     override fun onCreate(db: SQLiteDatabase?) {

         db?.execSQL("PRAGMA foreign_keys = ON;")

         db?.execSQL("""
        CREATE TABLE $TABLE_ACTIVIDADES (
            $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
            $COLUMN_NAME TEXT NOT NULL,
            $COLUMN_TYPE TEXT,
            $COLUMN_DESCRIPTION TEXT,
            $COLUMN_HORARIO TEXT,
            $COLUMN_INSTRUCTOR TEXT,
            $COLUMN_DURACION TEXT,
            $COLUMN_IMAGE INTEGER NOT NULL,
            $COLUMN_DIA TEXT NOT NULL,
            $COLUMN_PLAZAS INTEGER DEFAULT 15
        )
    """.trimIndent())

         db?.execSQL("""
        CREATE TABLE $TABLE_RESERVAS (
            $COLUMN_RESERVA_ID INTEGER PRIMARY KEY AUTOINCREMENT,
            $COLUMN_ACTIVIDAD_ID INTEGER NOT NULL,
            $COLUMN_USUARIO_ID_RESERVA INTEGER NOT NULL,
            $COLUMN_FECHA_CLASE TEXT NOT NULL,
            $COLUMN_FECHA TEXT NOT NULL,
            $COLUMN_HORA TEXT NOT NULL,
            FOREIGN KEY($COLUMN_ACTIVIDAD_ID) REFERENCES $TABLE_ACTIVIDADES($COLUMN_ID) ON DELETE CASCADE,
            FOREIGN KEY($COLUMN_USUARIO_ID_RESERVA) REFERENCES $TABLE_USUARIO($COLUMN_USUARIO_ID) ON DELETE CASCADE
        )
    """.trimIndent())

         db?.execSQL("""
        CREATE TABLE $TABLE_USUARIO (
            $COLUMN_USUARIO_ID INTEGER PRIMARY KEY AUTOINCREMENT,
            $COLUMN_USUARIO_NOMBRE TEXT NOT NULL,
            $COLUMN_USUARIO_APELLIDOS TEXT NOT NULL,
            $COLUMN_USUARIO_DNI TEXT NOT NULL UNIQUE,
            $COLUMN_USUARIO_FECHA_NACIMIENTO TEXT NOT NULL,
            $COLUMN_USUARIO_CORREO TEXT NOT NULL UNIQUE,
            $COLUMN_USUARIO_CODIGO_POSTAL TEXT,
            $COLUMN_USUARIO_ACEPTACION_DATOS BOOLEAN DEFAULT 0,
            $COLUMN_USUARIO_ADMIN INTEGER DEFAULT 0,
            $COLUMN_USUARIO_ENFERMEDADES TEXT

        )
    """.trimIndent())

         db?.execSQL("""
    CREATE TABLE $TABLE_HISTORICORESERVA (
        $COLUMN_HISTORICO_ID INTEGER PRIMARY KEY AUTOINCREMENT,
        $COLUMN_HISTORICO_ID_RESERVA INTEGER NOT NULL,
        $COLUMN_HISTORICO_FECHA_MODIFICACION TEXT NOT NULL,
        $COLUMN_HISTORICO_VALOR_NUEVO INTEGER,
        $COLUMN_HISTORICO_ACCION TEXT NOT NULL,
        $COLUMN_HISTORICO_NOMBRE_USUARIO TEXT,
        $COLUMN_HISTORICO_DNI_USUARIO TEXT,
        $COLUMN_HISTORICO_NOMBRE_ACTIVIDAD TEXT,
        $COLUMN_HISTORICO_FECHA_CLASE TEXT,
        FOREIGN KEY($COLUMN_HISTORICO_ID_RESERVA) REFERENCES $TABLE_RESERVAS($COLUMN_RESERVA_ID)
    )
""".trimIndent())

         insertarUsuariosIniciales(db)
         insertarActividadesIniciales(db)
         ejecutarScriptDesdeAssets(db!!, "triggers.sql", context)
     }

     override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        if (oldVersion < newVersion) {
            db?.execSQL("DROP TABLE IF EXISTS $TABLE_RESERVAS")
            db?.execSQL("DROP TABLE IF EXISTS $TABLE_ACTIVIDADES")
            db?.execSQL("DROP TABLE IF EXISTS $TABLE_USUARIO")
            db?.execSQL("DROP TABLE IF EXISTS $TABLE_HISTORICORESERVA")
            onCreate(db)
        }
    }

     private fun ejecutarScriptDesdeAssets(db: SQLiteDatabase, archivo: String, context: Context) {
         try {
             val fEntrada = context.assets.open(archivo)
             val contenidoScript  = fEntrada.bufferedReader().use { it.readText() }

             val sentenciasSql  = contenidoScript .split(Regex("(?<=END);\\s*\\n")).map { it.trim() }.filter { it.isNotEmpty() }

             for (sentencia  in sentenciasSql ) {
                 try {
                     Log.d("SQL_EXEC", "Ejecutando trigger:\n$sentencia ")
                     db.execSQL(sentencia )
                 } catch (e: Exception) {
                     Log.e("SQL_ERROR", "Error ejecutando:\n$sentencia ", e)
                 }
             }

         } catch (e: Exception) {
             Log.e("SCRIPT_ERROR", "Error al cargar el script desde assets", e)

         }
     }

    /******************************CONSULTAS TABLA ACTIVIDAD**********************************************************************/

    // Actualiza una actividad existente en la BBDD
    fun actualizarActividad(actividad: DTOActividad): Int {
         val db = writableDatabase
         val values = ContentValues().apply {
             put(COLUMN_NAME, actividad.nombre)
             put(COLUMN_TYPE, actividad.tipo)
             put(COLUMN_DESCRIPTION, actividad.descripcion)
             put(COLUMN_HORARIO, actividad.horario)
             put(COLUMN_INSTRUCTOR, actividad.instructor)
             put(COLUMN_DURACION, actividad.duracion)
             put(COLUMN_IMAGE, actividad.imagen)
             put(COLUMN_DIA, actividad.diaSemana)
             put(COLUMN_PLAZAS, actividad.plazasDisponibles)
         }
         val result = db.update(TABLE_ACTIVIDADES, values, "$COLUMN_ID = ?", arrayOf(actividad.idActividad.toString()))
         db.close()
         return result
     }

     // Obtiene todas las actividades ordenadas por día y horario
     fun obtenerTodasLasActividades(): MutableList<DTOActividad> {
         val listaActividades = mutableListOf<DTOActividad>()
         val db = readableDatabase

         val cursor = db.query(
             TABLE_ACTIVIDADES,
             null,
             null,
             null,
             null,
             null,
             "$COLUMN_DIA, time($COLUMN_HORARIO)" // orden por día y hora
         )

         while (cursor.moveToNext()) {
             listaActividades.add(cursorToDTOActividad(cursor))
         }

         cursor.close()
         db.close()
         return listaActividades
     }

     // Elimina una actividad por su ID
     fun eliminarActividadPorId(idActividad: Int): Int {
         val db = writableDatabase
         val result = db.delete(TABLE_ACTIVIDADES, "$COLUMN_ID = ?", arrayOf(idActividad.toString()))
         db.close()
         return result
     }

     // Inserta una nueva actividad en la BBDD
     fun insertarActividad(actividad: DTOActividad): Long {
         val db = writableDatabase
         val values = ContentValues().apply {
             put("nombre", actividad.nombre)
             put("tipo", actividad.tipo)
             put("descripcion", actividad.descripcion)
             put("horario", actividad.horario)
             put("instructor", actividad.instructor)
             put("duracion", actividad.duracion)
             put("imagen", actividad.imagen)
             put("diaSemana", actividad.diaSemana)
             put("plazasDisponibles", actividad.plazasDisponibles)
         }
         val result = db.insert("actividad", null, values)
         db.close()
         return result
     }

     // Filtra las actividades según nombre, monitor y tipo
     fun filtrarActividades(nombre: String, monitor: String, tipo: String): MutableList<DTOActividad> {
        val listaActividades = mutableListOf<DTOActividad>()
        val db = readableDatabase
        val condFiltro = mutableListOf<String>()
        val valorFiltro = mutableListOf<String>()

        if (nombre != "Todos") {
            condFiltro.add("$COLUMN_NAME = ?")
            valorFiltro.add(nombre)
        }
        if (monitor != "Todos") {
            condFiltro.add("$COLUMN_INSTRUCTOR = ?")
            valorFiltro.add(monitor)
        }
        if (tipo != "Todos") {
            condFiltro.add("$COLUMN_TYPE = ?")
            valorFiltro.add(tipo)
        }

        val whereStatement = if (condFiltro.isNotEmpty())
            condFiltro.joinToString(" AND ") else null

         val cursor: Cursor = db.query(
            TABLE_ACTIVIDADES,
            null,
            whereStatement, if (whereStatement != null) valorFiltro.toTypedArray() else null,
            null, null, null
        )
        while (cursor.moveToNext()) {
            listaActividades.add(cursorToDTOActividad(cursor))
        }
        cursor.close()
        db.close()
        return listaActividades
    }

     // Convierte una fila del cursor en un objeto DTOActividad
     private fun cursorToDTOActividad(cursor: Cursor): DTOActividad {
        return DTOActividad(
            idActividad = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
            nombre = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)),
            tipo = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TYPE)),
            descripcion = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION)),
            horario = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_HORARIO)),
            instructor = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_INSTRUCTOR)),
            duracion = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DURACION)),
            imagen = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IMAGE)),
            diaSemana = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DIA)),
            plazasDisponibles = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PLAZAS))
        )
    }

     // Obtiene todos los nombres de actividades sin repetir
    fun obtenerNombresActividades(): List<String> {
        val nombres = mutableListOf("Todos")
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT DISTINCT $COLUMN_NAME FROM $TABLE_ACTIVIDADES", null)

        while (cursor.moveToNext()) {
            nombres.add(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)))
        }

        cursor.close()
        db.close()
        return nombres
    }

     // Obtiene todos los nombres de monitores sin repetir
    fun obtenerNombresMonitores(): List<String> {
        val monitores = mutableListOf("Todos")
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT DISTINCT $COLUMN_INSTRUCTOR FROM $TABLE_ACTIVIDADES", null)

        while (cursor.moveToNext()) {
            monitores.add(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_INSTRUCTOR)))
        }

        cursor.close()
        db.close()
        return monitores
    }

     // Obtiene todos los tipos de actividades sin repetir
    fun obtenerTiposActividades(): List<String> {
        val tipos = mutableListOf("Todos")
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT DISTINCT $COLUMN_TYPE FROM $TABLE_ACTIVIDADES", null)

        while (cursor.moveToNext()) {
            tipos.add(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TYPE)))
        }

        cursor.close()
        db.close()
        return tipos
    }

     // Obtiene actividades específicas para el carrusel (predefinidas)
    fun obtenerActividadesCarrusel(): List<DTOActividad> {
        val actividadesFijas = listOf(
            "Ciclo Indoor", "Pilates", "Judo", "Body Active", "Zumba", "Minitramp"
        )

        val listaFinal = mutableListOf<DTOActividad>()
        val db = readableDatabase

        for (nombre in actividadesFijas) {
            val cursor = db.query(
                TABLE_ACTIVIDADES,
                null,
                "$COLUMN_NAME = ?",
                arrayOf(nombre),
                null, null, null
            )

            if (cursor.moveToFirst()) {
                listaFinal.add(cursorToDTOActividad(cursor))
            }

            cursor.close()
        }

        db.close()
        return listaFinal
    }

     // Obtiene una actividad concreta a partir de su ID
     fun obtenerActividadPorId(idActividad: Int): DTOActividad? {
         val db = readableDatabase
         val cursor = db.query(
             TABLE_ACTIVIDADES,
             null,
             "$COLUMN_ID = ?",
             arrayOf(idActividad.toString()),
             null, null, null
         )

         val actividad = if (cursor.moveToFirst()) {
             cursorToDTOActividad(cursor)
         } else {
             null
         }

         cursor.close()
         db.close()
         return actividad
     }

     /**************************CONSULTAS TABLA RESERVA********************************************************************************/

     // Insertar una reserva en la BBDD
    fun insertarReserva(idActividad: Int, idUsuario: Int, fechaClase: String): Long {
        val db = writableDatabase
        val fechaReserva = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val horaReserva = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())

        val values = ContentValues().apply {
            put(COLUMN_ACTIVIDAD_ID, idActividad)
            put(COLUMN_USUARIO_ID_RESERVA, idUsuario)
            put(COLUMN_FECHA_CLASE, fechaClase)
            put(COLUMN_FECHA, fechaReserva)
            put(COLUMN_HORA, horaReserva)
        }
        val result = db.insert(TABLE_RESERVAS, null, values)
        db.close()
        return result
    }

     // Comprobar si el usuario ya está reservado en esa actividad y fecha
    fun yaEstaReservado(idUsuario: Int, idActividad: Int, fechaClase: String): Boolean {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM $TABLE_RESERVAS " +
                    "WHERE $COLUMN_USUARIO_ID_RESERVA = ? " +
                    "AND $COLUMN_ACTIVIDAD_ID = ? AND $COLUMN_FECHA_CLASE = ?",
            arrayOf(idUsuario.toString(), idActividad.toString(), fechaClase)
        )
        val yaExiste = cursor.count > 0
        cursor.close()
        db.close()
        return yaExiste
    }

     // Contar cuántas reservas hay en una fecha concreta para una actividad
    fun contarReservasPorFecha(idActividad: Int, fechaClase: String): Int {
        val db = readableDatabase
        val cursor = db.rawQuery(
                "SELECT COUNT(*) FROM $TABLE_RESERVAS " +
                    "WHERE $COLUMN_ACTIVIDAD_ID = ? AND $COLUMN_FECHA_CLASE = ?",
            arrayOf(idActividad.toString(), fechaClase)
        )
        var total = 0
        if (cursor.moveToFirst()) {
            total = cursor.getInt(0)
        }
        cursor.close()
        db.close()
        return total
    }

     // Obtener todas las reservas de un usuario con los datos de la actividad
    fun obtenerReservasPorUsuario(idUsuario: Int): List<DTOReserva> {
        val lista = mutableListOf<DTOReserva>()
        val db = readableDatabase

        val query = """
        SELECT 
            r.idReserva, 
            r.idActividad, 
            r.idUsuario, 
            a.nombre, 
            a.diaSemana, 
            a.horario, 
            r.fechaClase,
            r.fecha, 
            r.hora
        FROM $TABLE_RESERVAS r
        JOIN $TABLE_ACTIVIDADES a ON r.$COLUMN_ACTIVIDAD_ID = a.$COLUMN_ID
        WHERE r.$COLUMN_USUARIO_ID_RESERVA = ?
        ORDER BY r.$COLUMN_FECHA_CLASE DESC, r.$COLUMN_HORA DESC
    """.trimIndent()

        val cursor = db.rawQuery(query, arrayOf(idUsuario.toString()))

        while (cursor.moveToNext()) {
            val reserva = DTOReserva(
                idReserva = cursor.getInt(0),
                idActividad = cursor.getInt(1),
                idUsuario = cursor.getInt(2),
                nombreActividad = cursor.getString(3),
                diaSemana = cursor.getString(4),
                horarioActividad = cursor.getString(5),
                fechaClase = cursor.getString(6),
                fecha = cursor.getString(7),
                hora = cursor.getString(8)
            )
            lista.add(reserva)
        }

        cursor.close()
        db.close()
        return lista
    }

     // Eliminar una reserva por su id
    fun eliminarReserva(idReserva: Int): Int {
        val db = writableDatabase
        val filasEliminadas = db.delete(TABLE_RESERVAS, "$COLUMN_RESERVA_ID = ?", arrayOf(idReserva.toString()))
        db.close()
        return filasEliminadas
    }

    /***********************************CONSULTAS TABLA USUARIO********************************************************************/

    // Obtener datos del usuario por su id
    fun obtenerUsuarioPorId(id: Int): DTOUsuario? {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM $TABLE_USUARIO WHERE $COLUMN_USUARIO_ID = ?",
            arrayOf(id.toString())
        )

        var usuario: DTOUsuario? = null
        if (cursor.moveToFirst()) {
            usuario = DTOUsuario(
                idUsuario = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_USUARIO_ID)),
                nombre = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USUARIO_NOMBRE)),
                apellidos = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USUARIO_APELLIDOS)),
                DNI = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USUARIO_DNI)),
                fechaNacimiento = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USUARIO_FECHA_NACIMIENTO)),
                correo = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USUARIO_CORREO)),
                codigoPostal = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USUARIO_CODIGO_POSTAL)),
                aceptacionDatos = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_USUARIO_ACEPTACION_DATOS)) == 1,
                esAdmin = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_USUARIO_ADMIN)) == 1
            )
        }
        cursor.close()
        db.close()
        return usuario
    }

     // Obtener usuario por correo
    fun obtenerUsuarioPorCorreo(correo: String): DTOUsuario? {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM $TABLE_USUARIO WHERE $COLUMN_USUARIO_CORREO = ?",
            arrayOf(correo)
        )

        var usuario: DTOUsuario? = null
        if (cursor.moveToFirst()) {
            usuario = DTOUsuario(
                idUsuario = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_USUARIO_ID)),
                nombre = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USUARIO_NOMBRE)),
                apellidos = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USUARIO_APELLIDOS)),
                DNI = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USUARIO_DNI)),
                fechaNacimiento = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USUARIO_FECHA_NACIMIENTO)),
                correo = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USUARIO_CORREO)),
                codigoPostal = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USUARIO_CODIGO_POSTAL)),
                aceptacionDatos = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_USUARIO_ACEPTACION_DATOS)) == 1,
                esAdmin = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_USUARIO_ADMIN)) == 1
            )
        }
        cursor.close()
        db.close()
        return usuario
    }

     //Actualizar usuario como admin
     //Puede actualizar todos los campos
     fun actualizarUsuarioComoAdmin(usuario: DTOUsuario): Int {
         val db = writableDatabase
         val values = ContentValues().apply {
             put(COLUMN_USUARIO_NOMBRE, usuario.nombre)
             put(COLUMN_USUARIO_APELLIDOS, usuario.apellidos)
             put(COLUMN_USUARIO_DNI, usuario.DNI)
             put(COLUMN_USUARIO_FECHA_NACIMIENTO, usuario.fechaNacimiento)
             put(COLUMN_USUARIO_CORREO, usuario.correo)
             put(COLUMN_USUARIO_CODIGO_POSTAL, usuario.codigoPostal)
             put(COLUMN_USUARIO_ACEPTACION_DATOS, if (usuario.aceptacionDatos) 1 else 0)
             put(COLUMN_USUARIO_ADMIN, if (usuario.esAdmin) 1 else 0)
             put(COLUMN_USUARIO_ENFERMEDADES, usuario.enfermedades ?: "")
         }
         val result = db.update(TABLE_USUARIO, values, "$COLUMN_USUARIO_ID = ?", arrayOf(usuario.idUsuario.toString()))
         db.close()
         return result
     }

     // Actualizar solo datos básicos del usuario(CP)
     fun actualizarUsuarioBasico(usuario: DTOUsuario): Int {
         val db = writableDatabase
         val values = ContentValues().apply {
             put(COLUMN_USUARIO_CODIGO_POSTAL, usuario.codigoPostal)

         }
         val resultado = db.update(TABLE_USUARIO, values, "$COLUMN_USUARIO_ID = ?", arrayOf(usuario.idUsuario.toString()))
         db.close()
         return resultado
     }

     // Eliminar usuario por su id
     fun eliminarUsuarioPorId(idUsuario: Int): Int {
         val db = writableDatabase
         val resultado = db.delete(
             TABLE_USUARIO,
             "$COLUMN_USUARIO_ID = ?",
             arrayOf(idUsuario.toString())
         )
         db.close()
         return resultado
     }

     // Obtener todos los usuarios en forma de lista
     fun obtenerTodosLosUsuarios(): MutableList<DTOUsuario> {
         val usuarios = mutableListOf<DTOUsuario>()
         val db = readableDatabase
         val cursor = db.rawQuery("SELECT * FROM $TABLE_USUARIO", null)

         if (cursor.moveToFirst()) {
             do {
                 val usuario = DTOUsuario(
                     idUsuario = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_USUARIO_ID)),
                     nombre = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USUARIO_NOMBRE)),
                     apellidos = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USUARIO_APELLIDOS)),
                     DNI = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USUARIO_DNI)),
                     fechaNacimiento = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USUARIO_FECHA_NACIMIENTO)),
                     correo = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USUARIO_CORREO)),
                     codigoPostal = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USUARIO_CODIGO_POSTAL)),
                     aceptacionDatos = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_USUARIO_ACEPTACION_DATOS)) == 1,
                     esAdmin = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_USUARIO_ADMIN)) == 1,
                     enfermedades = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USUARIO_ENFERMEDADES))
                 )
                 usuarios.add(usuario)
             } while (cursor.moveToNext())
         }

         cursor.close()
         db.close()
         return usuarios
     }

     // Insertar un nuevo usuario en la BBDD
     fun insertarUsuario(usuario: DTOUsuario): Long {
         val db = writableDatabase
         val values = ContentValues().apply {
             put(COLUMN_USUARIO_NOMBRE, usuario.nombre)
             put(COLUMN_USUARIO_APELLIDOS, usuario.apellidos)
             put(COLUMN_USUARIO_DNI, usuario.DNI)
             put(COLUMN_USUARIO_FECHA_NACIMIENTO, usuario.fechaNacimiento)
             put(COLUMN_USUARIO_CORREO, usuario.correo)
             put(COLUMN_USUARIO_CODIGO_POSTAL, usuario.codigoPostal)
             put(COLUMN_USUARIO_ACEPTACION_DATOS, if (usuario.aceptacionDatos) 1 else 0)
             put(COLUMN_USUARIO_ADMIN, if (usuario.esAdmin) 1 else 0)
             put(COLUMN_USUARIO_ENFERMEDADES, usuario.enfermedades ?: "")
         }
         val result = db.insert(TABLE_USUARIO, null, values)
         db.close()
         return result
     }

     /*********************************CONSULTAS TABLA HISTORICO RESERVA******************************************************************/

     // Registrar un cambio en el historial de reservas
     fun registrarHistorico(
         idReserva: Long,
         accion: String,
         nombreUsuario: String?,
         dniUsuario: String?,
         nombreActividad: String?,
         fechaClase: String?,
         plazasDisponiblesActualizadas: Int
     ) {
         val db = writableDatabase
         val fechaActual = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
         val horaActual = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
         val fechaModificacion = "$fechaActual $horaActual"

         val values = ContentValues().apply {
             put(COLUMN_HISTORICO_ID_RESERVA, idReserva.toInt())
             put(COLUMN_HISTORICO_FECHA_MODIFICACION, fechaModificacion)
             put(COLUMN_HISTORICO_ACCION, accion)
             put(COLUMN_HISTORICO_VALOR_NUEVO, plazasDisponiblesActualizadas)
             put(COLUMN_HISTORICO_NOMBRE_USUARIO, nombreUsuario ?: "Desconocido")
             put(COLUMN_HISTORICO_DNI_USUARIO, dniUsuario ?: "-")
             put(COLUMN_HISTORICO_NOMBRE_ACTIVIDAD, nombreActividad ?: "Desconocido")
             put(COLUMN_HISTORICO_FECHA_CLASE, fechaClase ?: "Fecha no disponible")
         }

         try {
             db.insert(TABLE_HISTORICORESERVA, null, values)
         } catch (e: Exception) {

         } finally {
             db.close()
         }
     }

     // Obtener lista completa del historial con unión a las tablas relacionadas
     fun obtenerHistorialCompleto(): List<DTOHistorialCompleto> {
         val lista = mutableListOf<DTOHistorialCompleto>()
         val db = readableDatabase

         val query = """
        SELECT h.idHistorico, h.fechaModificacion, h.accion, h.valorNuevo,
               COALESCE(a.nombre, h.nombreActividad) AS nombreActividad,
               h.fechaClase,
               COALESCE(u.nombre, h.nombreUsuario) AS nombreUsuario,
               COALESCE(u.dni, h.dniUsuario) AS dniUsuario
        FROM historicoReserva h
        LEFT JOIN reserva r ON h.idReserva = r.idReserva
        LEFT JOIN actividad a ON r.idActividad = a.idActividad
        LEFT JOIN usuario u ON r.idUsuario = u.idUsuario
        ORDER BY h.fechaModificacion DESC
    """.trimIndent()

         val cursor = db.rawQuery(query, null)

         if (cursor.moveToFirst()) {
             do {
                 lista.add(
                     DTOHistorialCompleto(
                         idHistorico = cursor.getInt(0),
                         fechaModificacion = cursor.getString(1),
                         accion = cursor.getString(2),
                         valorNuevo = cursor.getInt(3),
                         nombreActividad = cursor.getString(4) ?: "-",
                         fechaClase = cursor.getString(5),
                         nombreUsuario = cursor.getString(6) ?: "-",
                         dniUsuario = cursor.getString(7)
                     )
                 )
             } while (cursor.moveToNext())
         }

         cursor.close()
         return lista
     }

     /*************************PRECARGAR DATOS INICIALES**********************************************************************/

    // PRECARGAR USUARIOS DE PRUEBA
    private fun insertarUsuariosIniciales(db: SQLiteDatabase?) {
        val usuarios = listOf(
            DTOUsuario(
                nombre = "Vanesa",
                apellidos = "Rosete Suárez",
                DNI = "12345678A",
                fechaNacimiento = "1977-06-01",
                correo = "admin@asgardgym.com",
                codigoPostal = "33204",
                aceptacionDatos = true,
                esAdmin = true
            ),
            DTOUsuario(
                nombre = "Maria",
                apellidos = "Rosete Suárez",
                DNI = "12345678B",
                fechaNacimiento = "1989-11-24",
                correo = "maria@email.com",
                codigoPostal = "33202",
                aceptacionDatos = true
            )
        )

        for (usuario in usuarios) {
            val values = ContentValues().apply {
                put(COLUMN_USUARIO_NOMBRE, usuario.nombre)
                put(COLUMN_USUARIO_APELLIDOS, usuario.apellidos)
                put(COLUMN_USUARIO_DNI, usuario.DNI)
                put(COLUMN_USUARIO_FECHA_NACIMIENTO, usuario.fechaNacimiento)
                put(COLUMN_USUARIO_CORREO, usuario.correo)
                put(COLUMN_USUARIO_CODIGO_POSTAL, usuario.codigoPostal)
                put(COLUMN_USUARIO_ACEPTACION_DATOS, if (usuario.aceptacionDatos) 1 else 0)
                put(COLUMN_USUARIO_ADMIN, if (usuario.esAdmin) 1 else 0)

            }
            db?.insertWithOnConflict(TABLE_USUARIO, null, values, SQLiteDatabase.CONFLICT_IGNORE)
        }
    }

    // INSERTAR ACTIVIDADES INICIALES
    private fun insertarActividadesIniciales(db: SQLiteDatabase?) {
        val actividades = listOf(
            // Lunes
            DTOActividad(0, "Ciclo Indoor", "Cardiovascular", "Ciclo-Indoor es una clase de ejercicio cardiovascular que se realiza sobre una bicicleta estática, simulando recorridos de ciclismo en diferentes intensidades y terrenos. Con música motivadora, es ideal para mejorar la resistencia.",  "9:15", "Julio Aportela", "60 min", R.drawable.ciclo, "Lunes",plazasDisponibles = 5),

            DTOActividad(0, "Pilates", "Relajación y estiramiento", "Pilates es un método de ejercicio que se enfoca en fortalecer el core, mejorar la flexibilidad y la postura. A través de movimientos controlados y respiración, se trabaja la conexión mente-cuerpo, aumentando la fuerza.",  "10:15", "Vanesa Rosete", "60 min", R.drawable.pilates, "Lunes",plazasDisponibles = 5),

            DTOActividad(0, "Judo", "Artes marciales", "Judo es un arte marcial japonés que se basa en el principio de usar la fuerza del oponente en su contra. A través de técnicas de proyección, control y agarre, se mejora la coordinación, el equilibrio y la disciplina. Es una práctica completa que desarrolla tanto el cuerpo como la mente, fomentando el respeto, la confianza y la superación personal.",  "17:15", "Fernando", "60 min", R.drawable.judo, "Lunes",plazasDisponibles = 1),

            DTOActividad(0, "Body Active", "Fuerza y cardio", "Body Active es una clase de entrenamiento de alta intensidad diseñada para quemar calorías, reducir grasa corporal y tonificar todo el cuerpo. Utiliza ejercicios con pesas y TRX (Total Body Resistance Exercise). Acompañada de música motivadora, es ideal para mejorar la resistencia muscular y alcanzar un estado físico óptimo.",  "18:30", "Vanesa Rosete", "60 min", R.drawable.body_active, "Lunes",plazasDisponibles = 2),

            DTOActividad(0, "Ciclo Indoor", "Cardiovascular", "Ciclo-Indoor es una clase de ejercicio cardiovascular que se realiza sobre una bicicleta estática, simulando recorridos de ciclismo en diferentes intensidades y terrenos. Con música motivadora, es ideal para mejorar la resistencia.",  "19:30", "Julio Aportela", "60 min", R.drawable.ciclo, "Lunes",plazasDisponibles = 1),

            DTOActividad(0, "Zumba", "Baile con ritmo", "Zumba es una clase de ejercicio cardiovascular que combina ritmos latinos y otros estilos de música, como salsa, reguetón y cumbia, con movimientos de baile enérgicos. Es una forma divertida de mejorar la resistencia, quemar calorías y tonificar el cuerpo mientras disfrutas de la música.",  "20:30", "Vanesa Rosete", "60 min", R.drawable.zumba, "Lunes",plazasDisponibles = 5),

            // Martes
            DTOActividad(0, "Body Active", "Fuerza y cardio", "Body Active es una clase de entrenamiento de alta intensidad que combina ejercicios con pesas y barras para tonificar y fortalecer todo el cuerpo. Con música motivadora, es ideal para mejorar la resistencia muscular y alcanzar un estado físico óptimo.",  "9:15", "Julio Aportela", "60 min", R.drawable.body_active, "Martes",plazasDisponibles = 1),

            DTOActividad(0, "Judo", "Artes marciales", "Judo es un arte marcial japonés que se basa en el principio de usar la fuerza del oponente en su contra. A través de técnicas de proyección, control y agarre, se mejora la coordinación, el equilibrio y la disciplina. Es una práctica completa que desarrolla tanto el cuerpo como la mente, fomentando el respeto, la confianza y la superación personal.",  "17:15", "Fernando", "60 min", R.drawable.judo, "Martes",plazasDisponibles = 1),

            DTOActividad(0, "Zumba", "Baile con ritmo", "Zumba es una clase de ejercicio cardiovascular que combina ritmos latinos y otros estilos de música, como salsa, reguetón y cumbia, con movimientos de baile enérgicos. Es una forma divertida de mejorar la resistencia, quemar calorías y tonificar el cuerpo mientras disfrutas de la música.",  "19:00", "Vanesa Rosete", "60 min", R.drawable.zumba, "Martes",plazasDisponibles = 0),

            DTOActividad(0, "Ciclo Indoor", "Cardiovascular", "Ciclo-Indoor es una clase de ejercicio cardiovascular que se realiza sobre una bicicleta estática, simulando recorridos de ciclismo en diferentes intensidades y terrenos. Con música motivadora, es ideal para mejorar la resistencia.",  "20:00", "Julio Aportela", "60 min", R.drawable.ciclo, "Martes"),

            DTOActividad(0, "Pilates", "Relajación y estiramiento", "Pilates es un método de ejercicio que se enfoca en fortalecer el core, mejorar la flexibilidad y la postura. A través de movimientos controlados y respiración, se trabaja la conexión mente-cuerpo, aumentando la fuerza.",  "21:00", "Vanesa Rosete", "60 min", R.drawable.pilates, "Martes",plazasDisponibles = 2),

            // Miércoles
            DTOActividad(0, "Ciclo Indoor", "Cardiovascular", "Ciclo-Indoor es una clase de ejercicio cardiovascular que se realiza sobre una bicicleta estática, simulando recorridos de ciclismo en diferentes intensidades y terrenos. Con música motivadora, es ideal para mejorar la resistencia.",  "9:15", "Julio Aportela", "60 min", R.drawable.ciclo, "Miércoles",plazasDisponibles = 1),

            DTOActividad(0, "Pilates", "Relajación y estiramiento", "Pilates es un método de ejercicio que se enfoca en fortalecer el core, mejorar la flexibilidad y la postura. A través de movimientos controlados y respiración, se trabaja la conexión mente-cuerpo, aumentando la fuerza.",  "10:15", "Vanesa Rosete", "60 min", R.drawable.pilates, "Miércoles",plazasDisponibles = 0),

            DTOActividad(0, "Judo", "Artes marciales", "Judo es un arte marcial japonés que se basa en el principio de usar la fuerza del oponente en su contra. A través de técnicas de proyección, control y agarre, se mejora la coordinación, el equilibrio y la disciplina. Es una práctica completa que desarrolla tanto el cuerpo como la mente, fomentando el respeto, la confianza y la superación personal.",  "17:15", "Fernando", "60 min", R.drawable.judo, "Miércoles",plazasDisponibles = 5),

            DTOActividad(0, "Body Active", "Fuerza y cardio", "Body Active es una clase de entrenamiento de alta intensidad diseñada para quemar calorías, reducir grasa corporal y tonificar todo el cuerpo. Utiliza ejercicios con pesas y TRX (Total Body Resistance Exercise). Acompañada de música motivadora, es ideal para mejorar la resistencia muscular y alcanzar un estado físico óptimo.",  "18:30", "Vanesa Rosete", "60 min", R.drawable.body_active, "Miércoles",plazasDisponibles = 1),

            DTOActividad(0, "Ciclo Indoor", "Cardiovascular", "Ciclo-Indoor es una clase de ejercicio cardiovascular que se realiza sobre una bicicleta estática, simulando recorridos de ciclismo en diferentes intensidades y terrenos. Con música motivadora, es ideal para mejorar la resistencia.",  "19:30", "Julio Aportela", "60 min", R.drawable.ciclo, "Miércoles",plazasDisponibles = 2),

            DTOActividad(0, "Zumba", "Baile con ritmo", "Zumba es una clase de ejercicio cardiovascular que combina ritmos latinos y otros estilos de música, como salsa, reguetón y cumbia, con movimientos de baile enérgicos. Es una forma divertida de mejorar la resistencia, quemar calorías y tonificar el cuerpo mientras disfrutas de la música.",  "20:30", "Vanesa Rosete", "60 min", R.drawable.zumba, "Miércoles",plazasDisponibles = 1),

            // Jueves
            DTOActividad(0, "Body Active", "Fuerza y cardio", "Body Active es una clase de entrenamiento de alta intensidad que combina ejercicios con pesas y barras para tonificar y fortalecer todo el cuerpo. Con música motivadora, es ideal para mejorar la resistencia muscular y alcanzar un estado físico óptimo.",  "9:15", "Julio Aportela", "60 min", R.drawable.body_active, "Jueves",plazasDisponibles = 2),

            DTOActividad(0, "Judo", "Artes marciales", "Judo es un arte marcial japonés que se basa en el principio de usar la fuerza del oponente en su contra. A través de técnicas de proyección, control y agarre, se mejora la coordinación, el equilibrio y la disciplina. Es una práctica completa que desarrolla tanto el cuerpo como la mente, fomentando el respeto, la confianza y la superación personal.",  "17:15", "Fernando", "60 min", R.drawable.judo, "Jueves",plazasDisponibles = 0),

            DTOActividad(0, "Zumba", "Baile con ritmo", "Zumba es una clase de ejercicio cardiovascular que combina ritmos latinos y otros estilos de música, como salsa, reguetón y cumbia, con movimientos de baile enérgicos. Es una forma divertida de mejorar la resistencia, quemar calorías y tonificar el cuerpo mientras disfrutas de la música.",  "19:00", "Vanesa Rosete", "60 min", R.drawable.zumba, "Jueves",plazasDisponibles = 2),

            DTOActividad(0, "Ciclo Indoor", "Cardiovascular", "Ciclo-Indoor es una clase de ejercicio cardiovascular que se realiza sobre una bicicleta estática, simulando recorridos de ciclismo en diferentes intensidades y terrenos. Con música motivadora, es ideal para mejorar la resistencia.",  "20:00", "Julio Aportela", "60 min", R.drawable.ciclo, "Jueves",plazasDisponibles = 1),

            DTOActividad(0, "Pilates", "Relajación y estiramiento", "Pilates es un método de ejercicio que se enfoca en fortalecer el core, mejorar la flexibilidad y la postura. A través de movimientos controlados y respiración, se trabaja la conexión mente-cuerpo, aumentando la fuerza.",  "21:00", "Vanesa Rosete", "60 min", R.drawable.pilates, "Jueves",plazasDisponibles = 2),

            // Viernes
            DTOActividad(0, "Ciclo Indoor", "Cardiovascular", "Ciclo-Indoor es una clase de ejercicio cardiovascular que se realiza sobre una bicicleta estática, simulando recorridos de ciclismo en diferentes intensidades y terrenos. Con música motivadora, es ideal para mejorar la resistencia.",  "9:15", "Julio Aportela", "60 min", R.drawable.ciclo, "Viernes",plazasDisponibles = 5),

            DTOActividad(0, "Minitramp", "Cardiovascular", "Mini-Tramp es una clase cardiovascular que utiliza trampolines pequeños para mejorar la coordinación, tonificar el cuerpo y quemar calorías de manera divertida y de bajo impacto.",  "18:00", "Vanesa Rosete", "60 min", R.drawable.tramp, "Viernes",plazasDisponibles = 1),

            DTOActividad(0, "Ciclo Indoor", "Cardiovascular", "Ciclo-Indoor es una clase de ejercicio cardiovascular que se realiza sobre una bicicleta estática, simulando recorridos de ciclismo en diferentes intensidades y terrenos. Con música motivadora, es ideal para mejorar la resistencia.",  "19:00", "Julio Aportela", "60 min", R.drawable.ciclo, "Viernes",plazasDisponibles = 1)
        )

        actividades.forEach { actividad ->
            val values = ContentValues().apply {
                put(COLUMN_NAME, actividad.nombre)
                put(COLUMN_TYPE, actividad.tipo)
                put(COLUMN_DESCRIPTION, actividad.descripcion)
                put(COLUMN_HORARIO, actividad.horario)
                put(COLUMN_INSTRUCTOR, actividad.instructor)
                put(COLUMN_DURACION, actividad.duracion)
                put(COLUMN_IMAGE, actividad.imagen)
                put(COLUMN_DIA, actividad.diaSemana)
                put(COLUMN_PLAZAS, actividad.plazasDisponibles)
            }
            db?.insert(TABLE_ACTIVIDADES, null, values)
        }
    }
}
