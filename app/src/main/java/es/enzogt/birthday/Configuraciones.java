package es.enzogt.birthday;

import android.app.TimePickerDialog;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import es.enzogt.birthday.Clases.PreferenciasCompartidas;
import es.enzogt.birthday.Receptores.PonerAlarma;

public class Configuraciones extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuraciones);

        //Se pone en el label la hora que se ha guardado (si el usuario aún no la ha modificado es 00:00).
        ponerHoraLabel(PreferenciasCompartidas.leerHora(this));

        //Se premarca el icono por defecto en curso
        ponerBordeIcono(PreferenciasCompartidas.getIndexIcono(this));

        //Se premarca y se asigna el oyente del switch.
        ponerSwitchActualizacionAutomatica((Switch) findViewById(R.id.switchAutoactualizacion));

        //Se pone el edittext del mensaje por defecto
        ponerMensajeDefecto((EditText) findViewById(R.id.txtMensajeDefecto));
    }

    public void cambiarHora (View v){

        int [] horaGuardada = PreferenciasCompartidas.leerHora(this);

        new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {

                int [] hora = new int[]{selectedHour, selectedMinute};

                ponerHoraLabel(hora);
                PreferenciasCompartidas.guardarHora(Configuraciones.this, hora);
                PonerAlarma.ponerAlarma(Configuraciones.this);

            }

        }, horaGuardada[0], horaGuardada[1], true).show(); //True para 24h

    }

    private void ponerHoraLabel (int [] hora) {

        if (hora.length == 2) {
            ((TextView) findViewById(R.id.lblHoraEstablecida)).setText(
                (hora[0] < 10 ? "0" + hora[0] : hora[0])
                + ":" +
                (hora[1] < 10 ? "0" + hora[1] : hora[1])
            );
        }
    }

    public void cambiarIconoDefecto (View v) {

        ImageButton icono = (ImageButton) v;
        int indexEnArray = -1;

        switch (icono.getId()) {

            case R.id.iconoUno:
                indexEnArray = 0;
                break;
            case R.id.iconoDos:
                indexEnArray = 1;
                break;
            case R.id.iconoTres:
                indexEnArray = 2;
                break;
            case R.id.iconoCuatro:
                indexEnArray = 3;
                break;
        }

        if (indexEnArray != -1 && indexEnArray != PreferenciasCompartidas.getIndexIcono(this)) {

            /*Se elimina el borde de la imagen que sea.*/
            findViewById(R.id.iconoUno).setBackground(null);
            findViewById(R.id.iconoDos).setBackground(null);
            findViewById(R.id.iconoTres).setBackground(null);
            findViewById(R.id.iconoCuatro).setBackground(null);

            //Y se pone el borde al icono que corresponda.
            ponerBordeIcono(indexEnArray);

            //Finalmente se guarda el indice del nuevo icono por defecto.
            PreferenciasCompartidas.setIndexIcono(this, indexEnArray);
        }

    }

    private void ponerBordeIcono (int indice) {

        View target = null;

        switch (indice){
            case 0:
                target = findViewById(R.id.iconoUno);
                break;
            case 1:
                target = findViewById(R.id.iconoDos);
                break;
            case 2:
                target = findViewById(R.id.iconoTres);
                break;
            case 3:
                target = findViewById(R.id.iconoCuatro);
                break;
        }

        if (target != null) {

            Drawable highlight = getResources().getDrawable( R.drawable.borde );
            target.setBackground(highlight);
        }

    }

    private void ponerSwitchActualizacionAutomatica (Switch switchOperar){

        boolean estado = PreferenciasCompartidas.getAutoActualizacion(this);

        switchOperar.setText(getResources().getString( estado ? R.string.activado : R.string.desactivado));
        switchOperar.setChecked(estado);

        switchOperar.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                PreferenciasCompartidas.setAutoActualizacion(Configuraciones.this, b);
                compoundButton.setText(getResources().getString( b ? R.string.activado : R.string.desactivado));
            }
        });

    }

    private void ponerMensajeDefecto (EditText et) {

        et.setText(PreferenciasCompartidas.getMensajeDefecto(Configuraciones.this));

        et.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                PreferenciasCompartidas.setMensajeDefecto(Configuraciones.this, s.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void afterTextChanged(Editable s) {}
        });

    }
}