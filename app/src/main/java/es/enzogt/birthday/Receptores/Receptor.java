package es.enzogt.birthday.Receptores;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;

import java.util.ArrayList;

import es.enzogt.birthday.Clases.Contacto;
import es.enzogt.birthday.Clases.SQLite;
import es.enzogt.birthday.ListadoContactos;
import es.enzogt.birthday.R;

public class Receptor extends BroadcastReceiver {

    public static final String COMPROBAR_CUMPLES_ID_UNICO = "es.enzogt.birthday.Receptores.BUSCAR_CUMPLEANEROS";
    public static final int ID_UNICO_NUMERICO = 2147483647;

    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();
        if (COMPROBAR_CUMPLES_ID_UNICO.equals(action)) {

            ArrayList<Contacto> cumpleaneros = SQLite.getCumpleaneros(context);

            //Se lanza la notificacion solo si hay algo que notificar evidentemente.
            if (cumpleaneros.size() > 0) {

                final int notificacionID = 1;
                final String textoNotificacion = context.getString(R.string.notificacion_expandida_texto);

                //Notificacion con la configuracion basica.
                NotificationCompat.Builder constructor = new NotificationCompat.Builder(context);
                constructor.setAutoCancel(true);
                constructor.setSmallIcon(R.drawable.icono_notificacion);
                constructor.setContentTitle(context.getString(R.string.notificacion_compacta_titulo));
                constructor.setContentText(context.getString(R.string.notificacion_compacta_texto));

                //Se le asigna el evento al tocal la notificación.
                TaskStackBuilder pila = TaskStackBuilder.create(context);
                pila.addParentStack(ListadoContactos.class);
                pila.addNextIntent(new Intent(context, ListadoContactos.class));
                PendingIntent resultadoPendingIntent = pila.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                constructor.setContentIntent(resultadoPendingIntent);

                //Notificación expandida.
                NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
                inboxStyle.setBigContentTitle(context.getString(R.string.notificacion_expandida_titulo));

                for (Contacto contacto : cumpleaneros) {
                    inboxStyle.addLine(textoNotificacion.replace("{0}", contacto.getNombreTexto()));
                }

                //Configuración de la notificación.
                constructor.setWhen(0);
                constructor.setPriority(Notification.PRIORITY_MAX);
                constructor.setStyle(inboxStyle);

                //Se lanza la notificación.
                NotificationManager notificador = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                notificador.notify(notificacionID, constructor.build());

                ((Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE)).vibrate(new long[]{0, 25, 50, 25, 75, 25, 100, 0}, -1); //Solo una vez (-1)
                MediaPlayer.create(context, R.raw.hp).start();
            }

        }

    }
}