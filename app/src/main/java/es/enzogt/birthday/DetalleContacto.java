package es.enzogt.birthday;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;

import es.enzogt.birthday.Clases.Contacto;
import es.enzogt.birthday.Clases.PreferenciasCompartidas;
import es.enzogt.birthday.Clases.Proveedor;
import es.enzogt.birthday.Clases.SQLite;

public class DetalleContacto extends Activity {

    private Spinner spinner;
    private EditText mensaje;
    private int idContacto;
    private Contacto contacto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Para quitar la barra del titulo.
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        //Leemos el id del contacto que se le ha pasado desde la otra activity.
        Intent intent = getIntent();
        idContacto = intent.getIntExtra(SQLite.ID, -1);

        setContentView(R.layout.activity_detalle_contacto);

        spinner = (Spinner) findViewById(R.id.spinnerDetalleTelefono);
        mensaje = (EditText) findViewById(R.id.txtDetalleMensaje);

        if (idContacto > 0)
            buscarPonerContacto(idContacto);
    }

    private void buscarPonerContacto(int idContacto) {

        contacto = SQLite.getContacto(this, idContacto);
        ponerInformacionContacto(contacto);
    }

    private void ponerInformacionContacto(Contacto contacto) {

        if (contacto != null) {

            ImageView imagen = (ImageView) findViewById(R.id.imagenDetalleContacto);

            if (contacto.getUriFoto() != null && !contacto.getUriFoto().equals("null")) {

                try {
                    //http://www.viralandroid.com/2015/11/how-to-make-imageview-image-rounded-corner-in-android.html
                    Bitmap mbitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Uri.parse(contacto.getUriFoto()));
                    Bitmap imageRounded = Bitmap.createBitmap(mbitmap.getWidth(), mbitmap.getHeight(), mbitmap.getConfig());
                    Canvas canvas = new Canvas(imageRounded);
                    Paint mpaint = new Paint();
                    mpaint.setAntiAlias(true);
                    mpaint.setShader(new BitmapShader(mbitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));
                    canvas.drawRoundRect((new RectF(0, 0, mbitmap.getWidth(), mbitmap.getHeight())), 100, 100, mpaint);// Round Image Corner 100 100 100 100
                    imagen.setImageBitmap(imageRounded);

                } catch (IOException e) {
                    //Si falla se pone si recortar y ya.
                    imagen.setImageURI(Uri.parse(contacto.getUriFoto()));
                }

            } else {
                imagen.setImageResource(getResources().obtainTypedArray(R.array.imagenes_defecto).getResourceId(PreferenciasCompartidas.getIndexIcono(this), 0));
            }

            ((TextView) findViewById(R.id.lblDetalleNombreContenido)).setText(contacto.getNombreTexto());

            ((TextView) findViewById(R.id.lblDetalleFechaCumpleContenido)).setText(contacto.getFechaNacimientoTexto());

            ArrayList<String> telefonos = Proveedor.getTelefonosContacto(this, String.valueOf(idContacto));

            spinner.setAdapter(new ArrayAdapter<String>(this, R.layout.fila_spinner, telefonos));
            spinner.setSelection(telefonos.indexOf(contacto.getTelefono()));

            mensaje.setText(contacto.getMensajeTexto().equals("") ? PreferenciasCompartidas.getMensajeDefecto(this) : contacto.getMensajeTexto());
        }
    }

    public void clickCancelar(View v) {

        finish();
    }

    public void clickAceptar(View v) {

        contacto.setTelefono(spinner.getSelectedItem().toString());
        contacto.setMensaje(mensaje.getText().toString());

        realizarOperacion(idContacto, contacto, 1);
    }

    public void clickRefrescarContacto(View v) {

        realizarOperacion(idContacto, null, 0);
    }

    public void clickVerFicha(View v) {

        if (idContacto > 0) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_URI, String.valueOf(idContacto));
            intent.setData(uri);
            startActivity(intent);
        }
    }

    private void realizarOperacion(int idDelContacto, Contacto contacto, int operacion) {

        SincronizacionContactoAsincrona tareaAsincrona = new SincronizacionContactoAsincrona();
        tareaAsincrona.setIdContacto(idDelContacto);
        tareaAsincrona.setContacto(contacto);
        tareaAsincrona.setModo(operacion);
        tareaAsincrona.execute();
    }

    private class SincronizacionContactoAsincrona extends AsyncTask<Void, Void, Void> {

        ProgressDialog dialogoProceso = null;
        Contacto contacto = null;
        int idContacto;
        int modo;//Operacion a realizar (0 para actualizar y 1 para guardar)

        public void setIdContacto(int idContacto) {
            this.idContacto = idContacto;
        }

        public void setModo(int modo) {
            this.modo = modo;
        }

        public void setContacto(Contacto contacto) {
            this.contacto = contacto;
        }

        @Override
        protected Void doInBackground(Void... voids) {

            //Es una tonteria, pero el proceso es tan rápido que casi no da tiempo a ver el dialogo.
            try { Thread.sleep(500); } catch (InterruptedException e) {}

            if (modo == 1) { //Guardar

                SQLite.setContacto(DetalleContacto.this, contacto);

            } else { //Actualizar

                SQLite.sincronizarContacto(DetalleContacto.this, idContacto);
                contacto = SQLite.getContacto(DetalleContacto.this, idContacto);
            }

            return null;
        }

        @Override
        protected void onPreExecute() {

            dialogoProceso = new ProgressDialog(DetalleContacto.this);

            dialogoProceso.setIndeterminate(true);

            dialogoProceso.setCancelable(false);

            dialogoProceso.setCanceledOnTouchOutside(false); //Siempre en primer plano, si se toca el fondo no se "pierde".

            dialogoProceso.setProgressStyle(ProgressDialog.STYLE_SPINNER);

            dialogoProceso.setMessage(getResources().getString(modo == 1 ? R.string.guardando_contacto : R.string.actualizando_contacto));

            dialogoProceso.setCancelable(false);

            dialogoProceso.show();
        }

        @Override
        protected void onPostExecute(Void result) {

            if (dialogoProceso != null && dialogoProceso.isShowing()) {
                dialogoProceso.dismiss();
            }

            if (modo == 1) //Guardar
                finish();
            else //Actualizar
                ponerInformacionContacto(contacto);
        }
    }

}