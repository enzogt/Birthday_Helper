package es.enzogt.birthday.Receptores;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;
import java.util.TimeZone;

import es.enzogt.birthday.Clases.PreferenciasCompartidas;

public class PonerAlarma {

    public static void ponerAlarma(Context contexto) {

        //Documentación: https://developer.android.com/reference/android/app/PendingIntent.html
        AlarmManager alarmManager;
        PendingIntent pendingIntent;


        Intent intent = new Intent(contexto, Receptor.class);
        intent.setAction(Receptor.COMPROBAR_CUMPLES_ID_UNICO);


        //FLAG_CANCEL_CURRENT: Flag indicating that if the described PendingIntent already exists, the current one should be canceled before generating a new one.
        pendingIntent = PendingIntent.getBroadcast(contexto, Receptor.ID_UNICO_NUMERICO, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        //Se le asigna la fecha de hoy
        Calendar calendario = Calendar.getInstance(); //TimeZone.getTimeZone("Europe/Madrid")
        calendario.setTimeInMillis(System.currentTimeMillis());

        //Se lee la hora que se ha guardado en las preferencias (si no hay nada será 00:00):
        int[] hora = PreferenciasCompartidas.leerHora(contexto);

        //Y se ajusta a la hora a la que se comprobarán los cumpleaños
        calendario.set(Calendar.HOUR_OF_DAY, hora[0]);
        calendario.set(Calendar.MINUTE, hora[1]);
        calendario.set(Calendar.SECOND, 0);
        calendario.set(Calendar.MILLISECOND, 0);

        //Se añade un día si hoy no es posible que suene (la hora ya ha pasado).
        if ( calendario.compareTo(Calendar.getInstance()) < 0 ) {
            calendario.add(Calendar.DATE, 1);
        }


        alarmManager = (AlarmManager) contexto.getSystemService(Context.ALARM_SERVICE);

        //Se cancela la alarma si ya se había configurado una alarma con anterioridad.
        alarmManager.cancel(pendingIntent);

        //Finalmente se pone la alarma.
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendario.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
    }


}
