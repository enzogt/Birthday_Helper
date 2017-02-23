package es.enzogt.birthday.Receptores;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

//Este receptor se encarga de reactivar tras un reinicio del telefono el receptor que comprueba los cumples
public class Reactivador extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        PonerAlarma.ponerAlarma(context);

    }
}