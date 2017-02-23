package es.enzogt.birthday.Clases;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Contacto {

    private int id;
    private char tipoNotificacion = 'n';
    private String mensaje = "";
    private String telefono = "";
    private String fechaNacimiento = "";
    private String nombre = "";
    private String uriFoto = "";

    public Contacto() {}

    public Contacto(int id, char tipoNotificacion, String mensaje, String telefono, String fechaNacimiento, String nombre, String uriFoto) {
        this.id = id;
        this.tipoNotificacion = tipoNotificacion;
        this.mensaje = mensaje;
        this.telefono = telefono;
        this.fechaNacimiento = fechaNacimiento;
        this.nombre = nombre;
        this.uriFoto = uriFoto;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    /*Notificación*/
    public char getTipoNotificacion() {
        return tipoNotificacion;
    }

    public String getTipoNotificacionTexto() {

        switch (tipoNotificacion) {
            case 'm': //m de mail
                return "Aviso: notificación y email";
            default: //n de notificación
                return "Aviso: solo notificación";
        }
    }

    public void setTipoNotificacion(char tipoNotificacion) {
        this.tipoNotificacion = tipoNotificacion;
    }

    /*Telefono*/
    public String getTelefono() {
        return telefono;
    }

    public String getTelefonoTexto() {
        return esNulo(telefono) ? "sin teléfono registrado" : telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    /*Nombre*/
    public String getNombre() {
        return nombre;
    }

    public String getNombreTexto() {
        return esNulo(nombre) ? "sin nombre registrado" : nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getMensaje() {
        return mensaje;
    }

    public String getMensajeTexto() {
        return esNulo(mensaje) ? "" : mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getFechaNacimiento() {
        return fechaNacimiento;
    }

    public String getFechaNacimientoTexto() {
        return esNulo(fechaNacimiento) ? "sin definir" : fechaNacimiento;
    }

    public void setFechaNacimiento(String fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public String getFechaNacimientoComparacion() {

        //Este formato se produce cuando a un contacto le ponenmos la fecha de cumpleaños (sin año).
        SimpleDateFormat MMddFormat = new SimpleDateFormat("--MM-dd");
        //Este formato se produce cuando a un contacto le ponenmos la fecha de nacimiento (con año).
        SimpleDateFormat yyyyMMddFormat = new SimpleDateFormat("yyyy-MM-dd");
        //Otro, que salie en un ejemplo (no he visto que se genere de tal forma).
        SimpleDateFormat ddMMyyyyFormat = new SimpleDateFormat("dd-MM-yyyy");

        // possible formats, --mm-dd, yyyy-mm-dd, dd-mm-yyyy
        if (fechaNacimiento == null || fechaNacimiento.isEmpty()) {
            return null;
        }

        Date fecha = null;
        String cumple = null;

        try {

            switch (fechaNacimiento.length()) {
                case 7: //--mm-dd
                    fecha = MMddFormat.parse(fechaNacimiento);
                    break;

                case 10: //yyyy-MM-dd
                    fecha = (fechaNacimiento.charAt(2) == '-' ? ddMMyyyyFormat : yyyyMMddFormat).parse(fechaNacimiento);
                    break;
            }

            if (fecha != null) {
                Calendar calendario = Calendar.getInstance();
                calendario.setTime(fecha);
                cumple = String.valueOf(calendario.get(Calendar.DAY_OF_MONTH)) + "-" + String.valueOf(calendario.get(Calendar.MONTH) + 1);
            }

        } catch (ParseException e) {
            Log.e("App cumpleaños", "Se encontro un problema al parsear la fecha '" + fechaNacimiento + "': Traza: " + e.getMessage());
            cumple = null;
        }

        return cumple;
    }

    public String getUriFoto() {
        return uriFoto;
    }

    public void setUriFoto(String uriFoto) {
        this.uriFoto = uriFoto;
    }

    private boolean esNulo(String valor) {
        return valor == null || valor.equals("") || valor.equals("null");
    }
}
