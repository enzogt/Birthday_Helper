package es.enzogt.birthday;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import es.enzogt.birthday.Clases.AdaptadorFilaContacto;
import es.enzogt.birthday.Clases.PreferenciasCompartidas;
import es.enzogt.birthday.Clases.SQLite;
import es.enzogt.birthday.Receptores.PonerAlarma;

public class ListadoContactos extends AppCompatActivity {

    ListView lv;
    EditText txtNombre;
    View vistaNoEncontrado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listado_contactos);

        //Se pone o reactiva la alarma de los cumpleaños.
        PonerAlarma.ponerAlarma(this);

        //Inicialización vistas.
        lv = (ListView) findViewById(R.id.listadoContactos);
        txtNombre = (EditText) findViewById(R.id.txtNombre);

        //Se asigna el evento textCahnged
        ponerEventoTextChanged(txtNombre);

        //Se añade un layout que se mostrara tras usar el buscador y no encontrar registros.
        addNoEncontradoLayout(lv);

        //Se añade el onLongClick al ListView.
        ponerOnClickListener(lv);

        //Si se esta activado se actualizaran los contactos
        if(PreferenciasCompartidas.getAutoActualizacion(this))
            actualizarContactosBD();
    }

    @Override
    protected void onResume() {
        super.onResume();

        //Se llena el listado en el inicio.
        setListado(txtNombre.getText().toString());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Add our menu
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()) {

            case R.id.action_refresh:

                actualizarContactosBD();
                break;

            case R.id.action_configure:

                startActivity(new Intent(this, Configuraciones.class));
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void addNoEncontradoLayout (ListView lista) {

        //Se guarda en variable el layout de contacto no encontrado.
        View noEncontrado = getLayoutInflater().inflate(R.layout.contacto_no_encontrado, lista, false);

        //Se añade al layout del activity
        addContentView(noEncontrado, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        //Ahora que ya esta añadido, se puede buscar para guardarlo y operarlo mas tarde (mostrarlo / ocultarlo).
        vistaNoEncontrado = findViewById(R.id.layoutNoEncontrado);

        //Y finalmente se relaciona con el ListView
        lista.setEmptyView(noEncontrado);
    }

    private void ponerEventoTextChanged (EditText txt) {

        txt.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                setListado(s.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void ponerOnClickListener (ListView lista){

        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                int idContacto = ( (AdaptadorFilaContacto.TAGpersonalizado) view.getTag() ).getIdContacto();

                Intent intent = new Intent(ListadoContactos.this, DetalleContacto.class);
                intent.putExtra(SQLite.ID, idContacto);
                startActivity(intent);
            }
        });

    }

    /*Esta función llenaré el listview con los contactos*/
    private void setListado (String nombreBuscado) {

        final int icono = getResources().obtainTypedArray(R.array.imagenes_defecto).getResourceId(PreferenciasCompartidas.getIndexIcono(this), 0);

        AdaptadorFilaContacto adaptadorPersonalizado = new AdaptadorFilaContacto(this, icono);

        //Del proveedor
        //adaptadorPersonalizado.setContactos(Proveedor.getContactosProveedor(this, nombreBuscado));

        //De BBDD
        adaptadorPersonalizado.setContactos(SQLite.getlistado(this, nombreBuscado));


        lv.setAdapter(adaptadorPersonalizado);
        //customAdapter.notifyDataSetChanged();
    }

    /*Esta funcion unificara la información de nuestra base de datos con la obtenida por el provider.
      Se llamará desde el icono de Action Bar, o si se requiere se podria llamar en el onCreate. */
    private void actualizarContactosBD () {

        new SincronizacionContactosAsincrona().execute();
    }

    private class SincronizacionContactosAsincrona extends AsyncTask<Void, Integer, String> {

        ProgressDialog dialogoProceso = null;

        @Override
        protected String doInBackground(Void... voids) {
            SQLite.sincronizarBDconProveedor(ListadoContactos.this);
            return null;
        }

        @Override
        protected void onPreExecute() {

            dialogoProceso = new ProgressDialog(ListadoContactos.this);

            dialogoProceso.setIndeterminate(true);

            dialogoProceso.setCancelable(false);

            dialogoProceso.setCanceledOnTouchOutside(false); //Siempre en primer plano, si se toca el fondo no se "pierde".

            dialogoProceso.setProgressStyle(ProgressDialog.STYLE_SPINNER);

            dialogoProceso.setMessage(getResources().getString(R.string.actualizando_contactos));

            dialogoProceso.setCancelable(false);

            dialogoProceso.show();
        }

        @Override
        protected void onPostExecute(String result) {

            if (dialogoProceso != null && dialogoProceso.isShowing()) {
                dialogoProceso.dismiss();
            }

            setListado(txtNombre.getText().toString());
        }
    }

}