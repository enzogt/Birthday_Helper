package es.enzogt.birthday.Clases;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class PreferenciasCompartidas {

    final static String horaKey = "hora";
    final static String minutosKey = "minutos";
    final static String indexIcono = "indexIcono";
    final static String autoActualizacion = "autoActualizacion";
    final static String mensajeDefecto = "mensajeDefecto";


    public static void guardarHora(Context contexto, int[] hora) {

        if (hora.length == 2) {
            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(contexto).edit();
            editor.putInt(horaKey, hora[0]);
            editor.putInt(minutosKey, hora[1]);
            editor.commit();
        }
    }

    public static int[] leerHora(Context contexto) {

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(contexto);
        return new int[]{preferences.getInt(horaKey, 0), preferences.getInt(minutosKey, 0)};
    }

    public static void setIndexIcono (Context contexto, int index) {

        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(contexto).edit();
        editor.putInt(indexIcono, index);
        editor.commit();
    }

    public static int getIndexIcono (Context contexto) {

        return (PreferenceManager.getDefaultSharedPreferences(contexto)).getInt(indexIcono, 0);
    }

    public static void setAutoActualizacion (Context contexto, boolean preferencia) {

        PreferenceManager.getDefaultSharedPreferences(contexto).edit().putBoolean(autoActualizacion, preferencia).commit();
    }

    public static boolean getAutoActualizacion (Context contexto) {

        return (PreferenceManager.getDefaultSharedPreferences(contexto)).getBoolean(autoActualizacion, false);
    }

    public static void setMensajeDefecto (Context contexto, String mensaje) {

        PreferenceManager.getDefaultSharedPreferences(contexto).edit().putString(mensajeDefecto, mensaje).commit();
    }

    public static String getMensajeDefecto (Context contexto) {

        return (PreferenceManager.getDefaultSharedPreferences(contexto)).getString(mensajeDefecto, "");
    }

    private static void guardarRecursoCompartido(Context contexto, String key, int value) {

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(contexto);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    private static int buscarRecursoCompartido(Context contexto, String key) {

        //SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(contexto);
        //return preferences.getInt(key, 0);

        return (PreferenceManager.getDefaultSharedPreferences(contexto)).getInt(key, 0);
    }

}