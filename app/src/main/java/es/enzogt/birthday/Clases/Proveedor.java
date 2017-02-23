package es.enzogt.birthday.Clases;

import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;

import java.util.ArrayList;

public class Proveedor {

    public static ArrayList<Contacto> getContactosProveedor(Context contexto, String nombreBuscado) {

        ArrayList<Contacto> listadoContactos = new ArrayList<>();
        Contacto contactoIteracion;

        final String proyeccion[] = {
                ContactsContract.Contacts._ID,
                ContactsContract.Contacts.DISPLAY_NAME,
                ContactsContract.Contacts.HAS_PHONE_NUMBER,
                ContactsContract.Contacts.PHOTO_URI
        };

        final String filtro = ContactsContract.Contacts.DISPLAY_NAME + " like ?";

        final String args_filtro[] = {"%" + nombreBuscado + "%"};

        final String orden = ContactsContract.Contacts.DISPLAY_NAME + " ASC";


        Cursor cursor = contexto.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, proyeccion, filtro, args_filtro, orden);

        if (cursor.getCount() > 0) {

            while (cursor.moveToNext()) {

                contactoIteracion = new Contacto();

                contactoIteracion.setId(cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts._ID)));

                contactoIteracion.setNombre(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)));

                contactoIteracion.setUriFoto(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.PHOTO_URI)));

                if (Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {

                    //Se guardará el primer telefono que tenga.

                    //Debido a un bug a veces los contacos se duplican, se comprobara que efectivamente devuelve los telefonos.
                    //contactoIteracion.setTelefono( (getTelefonosContacto(contexto, String.valueOf(contactoIteracion.getId()))).get(0) );

                    ArrayList<String> telefonos = getTelefonosContacto(contexto, String.valueOf(contactoIteracion.getId()));
                    if (telefonos.size() > 0)
                        contactoIteracion.setTelefono( telefonos.get(0) );
                }

                contactoIteracion.setFechaNacimiento(getFechaCumpleContacto(contexto, String.valueOf(contactoIteracion.getId())));

                listadoContactos.add(contactoIteracion);
            }
        }

        cursor.close();

        return listadoContactos;
    }

    public static Contacto getContactoProveedor(Context contexto, int idContacto) {

        Contacto contacto = null;

        final String proyeccion[] = {
                ContactsContract.Contacts._ID, ContactsContract.Contacts.DISPLAY_NAME,
                ContactsContract.Contacts.HAS_PHONE_NUMBER, ContactsContract.Contacts.PHOTO_URI
        };

        final String filtro = ContactsContract.Contacts._ID + " = ?";

        final String args_filtro[] = {String.valueOf(idContacto)};

        Cursor cursor = contexto.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, proyeccion, filtro, args_filtro, null);

        if (cursor.getCount() > 0) {

            cursor.moveToNext();

            contacto = new Contacto();

            contacto.setId(cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts._ID)));

            contacto.setNombre(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)));

            contacto.setUriFoto(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.PHOTO_URI)));

            contacto.setFechaNacimiento(getFechaCumpleContacto(contexto, String.valueOf(idContacto)));

            if (Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {

                ArrayList<String> telefonos = getTelefonosContacto(contexto, String.valueOf(contacto.getId()));
                if (telefonos.size() > 0)
                    contacto.setTelefono( telefonos.get(0) );
            }
        }

        cursor.close();

        return contacto;
    }

    /*Funcion que devuelve el listado de telefonos a partir del contexto y el id del contacto. */
    public static ArrayList<String> getTelefonosContacto(Context contexto, String IDContacto) {

        ArrayList<String> telefonos = new ArrayList<>();

        Cursor cursorTelefono = contexto.getContentResolver().query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null,
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = ?",
                new String[]{IDContacto},
                null
        );

        while (cursorTelefono.moveToNext()) {

            telefonos.add(cursorTelefono.getString(cursorTelefono.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DATA)));
        }

        cursorTelefono.close();

        return telefonos;
    }

    private static String getFechaCumpleContacto(Context contexto, String IDContacto) {

        String fecha = "";

        final String columns[] = {
                ContactsContract.CommonDataKinds.Event.START_DATE,
                ContactsContract.CommonDataKinds.Event.TYPE,
                ContactsContract.CommonDataKinds.Event.MIMETYPE,
        };

        final String where =
                ContactsContract.CommonDataKinds.Event.TYPE                 + " = " +
                ContactsContract.CommonDataKinds.Event.TYPE_BIRTHDAY        + " and " +
                ContactsContract.CommonDataKinds.Event.MIMETYPE             + " = '" +
                ContactsContract.CommonDataKinds.Event.CONTENT_ITEM_TYPE    + "' and " +
                ContactsContract.Data.CONTACT_ID + " = " + IDContacto;

        final String[] selectionArgs = null;

        final String sortOrder = ContactsContract.Contacts.DISPLAY_NAME;


        Cursor cursorCumple = contexto.getContentResolver().query(ContactsContract.Data.CONTENT_URI, columns, where, selectionArgs, sortOrder);

        if (cursorCumple.getCount() > 0) {
            while (cursorCumple.moveToNext()) {
                fecha = cursorCumple.getString(cursorCumple.getColumnIndex(ContactsContract.CommonDataKinds.Event.START_DATE));
            }
        }

        cursorCumple.close();

        return fecha;
    }

}
