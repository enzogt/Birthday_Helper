package es.enzogt.birthday.Clases;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


public class SQLite {

    public final static String NOMBRE_DB = "miscumplesDB";
    public final static String NOMBRE_TABLA = "miscumples";
    public final static String ID = "ID";
    public final static String TIPO_NOTIFICACION = "TipoNotificacion";
    public final static String MENSAJES = "Mensaje";
    public final static String TELEFONO = "Telefono";
    public final static String FECHA_NACIMIENTO = "FechaNacimiento";
    public final static String NOMBRE = "Nombre";
    public final static String URI_FOTO = "uriFoto";


    private static SQLiteDatabase getSQLite (Context contexto) {

        SQLiteDatabase db;

        db = contexto.openOrCreateDatabase(NOMBRE_DB, Context.MODE_PRIVATE, null);

        db.execSQL(

            "CREATE TABLE IF NOT EXISTS " + NOMBRE_TABLA +

            " (" +
                ID +                    " INTEGER           PRIMARY KEY NOT NULL, "     +
                TIPO_NOTIFICACION +     " CHAR(1)           DEFAULT 'n', "              +
                MENSAJES +              " VARCHAR(160)      DEFAULT 'null', "           +
                TELEFONO +              " VARCHAR(15)       DEFAULT 'null', "           +
                FECHA_NACIMIENTO +      " VARCHAR(15)       DEFAULT 'null', "           +
                NOMBRE +                " VARCHAR (128)     DEFAULT 'null', "           +
                URI_FOTO +              " VARCHAR (255)     DEFAULT 'null'"             +
            ");"
        );

        return db;
    }

    private static void alCuernoConTodo(Context contexto) {

        getSQLite(contexto).execSQL("DROP TABLE IF EXISTS " + NOMBRE_TABLA + ";");
    }

    /*Listado de los contacto según nombre*/
    public static ArrayList<Contacto> getlistado (Context contexto, String nombreBuscado) {

        SQLiteDatabase db = getSQLite(contexto);

        ArrayList<Contacto> listadoContactos = new ArrayList<>();

        Cursor cursor = db.rawQuery("SELECT * FROM " + NOMBRE_TABLA + " WHERE " + NOMBRE + " like ? ORDER BY " + NOMBRE + " ASC;", new String[]{"%" + nombreBuscado + "%"});

        if (cursor.getCount() > 0) {

            while (cursor.moveToNext())
                listadoContactos.add(new Contacto(cursor.getInt(0), cursor.getString(1).charAt(0), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5),  cursor.getString(6)));
        }

        cursor.close();
        db.close();

        return listadoContactos;
    }

    public static void sincronizarBDconProveedor (Context contexto) {

        //Obtenemos todos los contactos del proveedor.
        ArrayList<Contacto> contactosProveedor = Proveedor.getContactosProveedor(contexto, "");

        //Obtenemos los contactos guardados en nuestra BD.
        ArrayList<Contacto> contactosBD = getlistado(contexto, "");

        /*SQL*/
        SQLiteDatabase db = getSQLite(contexto);

/*
        El proceso: los contactos se compararan a partir del ID así pues:

            - ID: se mantendra.
            - Nombre: se actualizará siempre al obtenido en el proveedor.
            - Fecha de nacimiento: se actualizará siempre al obtenido en el proveedor.
            - Uri_foto: : se actualizará siempre al obtenido en el proveedor.
            - Mensajes: es un campo que solo esta en nuestra BD por lo que se mantendrá.
            - Tipo de notificacion: es un campo que solo esta en nuestra BD por lo que se mantendrá.
            - Telefono: se buscaran todos los telefonos del contacto
                - Caso A: el telefono en BD esta en los obtenidos por el proveedor -> se mantiene
                - Caso B: el telefono en BD no esta en los obtenidos por el proveedor -> se guarda el primero de todos (si lo hay).
*/

        //Contactos a insertar o actualizar
        for (int i = 0, lenghtContactosProveedor = contactosProveedor.size(); i < lenghtContactosProveedor; i++){

            Contacto contactoBuscadoBD = null;

            for (Contacto contactoIteracion : contactosBD)
                if (contactoIteracion.getId() == contactosProveedor.get(i).getId())
                    contactoBuscadoBD = contactoIteracion;

            inserccionContacto(contexto, db, contactosProveedor.get(i), contactoBuscadoBD);
        }

        db.close();
    }

    public static void sincronizarContacto(Context contexto, int idContacto) {

        //Se obtiene el contacto del detalle en el proveedor.
        Contacto contactoProveedor = Proveedor.getContactoProveedor(contexto, idContacto);

        //Se obtiene el contacto de la BD.
        Contacto contactoBD = SQLite.getContacto(contexto, idContacto);

        /*SQL*/
        SQLiteDatabase db = getSQLite(contexto);

        inserccionContacto(contexto, db, contactoProveedor, contactoBD);

        db.close();
    }

    private static void inserccionContacto (Context contexto, SQLiteDatabase db, Contacto contactoProveedor, Contacto contactoBD){

        //Si es un contacto que ya existia en nuestra BD.
        if (contactoBD != null) {

            //Ni el nombre ni la fecha ni la uri de nacimiento se mantienen.
            contactoBD.setNombre(contactoProveedor.getNombre());
            contactoBD.setFechaNacimiento(contactoProveedor.getFechaNacimiento());
            contactoBD.setUriFoto(contactoProveedor.getUriFoto());

            //Se obtienen todos los telefonos del contacto.
            ArrayList<String> telefonos = Proveedor.getTelefonosContacto(contexto, String.valueOf(contactoBD.getId()));

            //Se busca si el telefono que habiamos guardado en BBDD ya no existe en el contacto (podria haberselo cambiado).
            if (!telefonos.contains(contactoBD.getTelefono())) {

                //Si no existe se le asigna el primero de los que tenga (si tiene alguno).
                telefonos = Proveedor.getTelefonosContacto(contexto, String.valueOf(contactoBD.getId()));
                if (telefonos.size() > 0)
                    contactoBD.setTelefono(telefonos.get(0));
            }
        }

        //Si es un contacto nuevo.
        else {
            contactoBD = contactoProveedor;
        }

        db.execSQL(

            "INSERT OR REPLACE INTO " + NOMBRE_TABLA + "(" +

                ID                  + ", " +
                NOMBRE              + ", " +
                URI_FOTO            + ", " +
                FECHA_NACIMIENTO    + ", " +
                TIPO_NOTIFICACION   + ", " +
                MENSAJES            + ", " +
                TELEFONO +

            ") VALUES (" +

                "'" + contactoBD.getId()                + "', " +
                "'" + contactoBD.getNombre()            + "', " +
                "'" + contactoBD.getUriFoto()           + "', " +
                "'" + contactoBD.getFechaNacimiento()   + "', " +
                "'" + contactoBD.getTipoNotificacion()  + "', " +
                "'" + contactoBD.getMensaje()           + "', " +
                "'" + contactoBD.getTelefono()          + "'" +

            ");"
        );
    }

    /*Obtención del contacto mediante el ID*/
    public static Contacto getContacto (Context contexto, int idContacto) {

        SQLiteDatabase db = getSQLite(contexto);
        Contacto contactoBuscado = null;

        Cursor cursor = db.rawQuery("SELECT * FROM " + NOMBRE_TABLA + " WHERE " + ID + " = ? LIMIT 1;", new String[]{String.valueOf(idContacto)});

        if (cursor.getCount() > 0)
            while (cursor.moveToNext())
                contactoBuscado = new Contacto(cursor.getInt(0), cursor.getString(1).charAt(0), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5),  cursor.getString(6));

        cursor.close();
        db.close();

        return contactoBuscado;
    }

    /*Edición del contacto*/
    public static void setContacto (Context contexto, Contacto contacto) {

        SQLiteDatabase db = getSQLite(contexto);
        db.execSQL(
                "UPDATE " + NOMBRE_TABLA + " SET " +

                        TELEFONO            + " = '" +  contacto.getTelefono()          + "'," +
                        MENSAJES            + " = '" +  contacto.getMensaje()           + "'," +
                        TIPO_NOTIFICACION   + " = '" +  contacto.getTipoNotificacion()  + "' "  +

                "WHERE " + ID + " = " + contacto.getId() + ";"
        );
        db.close();
    }

    public static ArrayList<Contacto> getCumpleaneros (Context contexto) {

        ArrayList<Contacto> contactos = SQLite.getlistado(contexto, "");
        ArrayList<Contacto> contactosQueCumplenAnos = new ArrayList<>();

        Calendar calendarioHoy = Calendar.getInstance();
        calendarioHoy.setTime(new Date());
        String hoy = String.valueOf(calendarioHoy.get(Calendar.DAY_OF_MONTH)) + "-" + String.valueOf(calendarioHoy.get(Calendar.MONTH) + 1);


        for (Contacto contacto : contactos) {

            String fechaContacto = contacto.getFechaNacimientoComparacion();

            if (fechaContacto != null && fechaContacto.equals(hoy)){

                contactosQueCumplenAnos.add(contacto);
            }
        }

        return contactosQueCumplenAnos;
    }

}