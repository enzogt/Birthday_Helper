package es.enzogt.birthday.Clases;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import es.enzogt.birthday.R;

public class AdaptadorFilaContacto extends BaseAdapter {

    private LayoutInflater inflater;
    private ArrayList<Contacto> contactos;
    private int imagenContactoDefecto;

    /*Constructor*/
    public AdaptadorFilaContacto(Context contexto, int imagenContactoDefecto) {

        inflater = LayoutInflater.from(contexto);
        contactos = new ArrayList<>();
        this.imagenContactoDefecto = imagenContactoDefecto;
    }

    /*Setter del arraylist de contactos*/
    public void setContactos(ArrayList<Contacto> contactos) {
        this.contactos = contactos;
    }

    @Override
    public int getCount() {
        return contactos.size();
    }

    @Override
    public Object getItem(int position) {
        return contactos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override /*Constructor de las vistas con los datos facilitados*/
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;

        /*
            El reciclado de los holders se ha comentado debido a un comportamiento erratico:
            por alguna razón no respeta el TAG que se le asigna, provocando que setee 4 o 5 bien,
            pero luego estos se repiten una y otra vez (por ejemplo: 23, 45, 34, 67... 23, 45, 34, 67...)

        */

//        if (convertView == null) {

            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.fila_contacto, null);

            holder.nombre = (TextView) convertView.findViewById(R.id.nombre);
            holder.numero = (TextView) convertView.findViewById(R.id.telefono);
            holder.aviso = (TextView) convertView.findViewById(R.id.aviso);
            holder.imagen = (ImageView) convertView.findViewById(R.id.imagen);

            convertView.setTag(new TAGpersonalizado(holder, contactos.get(position).getId()));

//        } else {
//
//            holder = ((TAGpersonalizado) convertView.getTag()).getHolder();
//        }

        holder.nombre.setText(contactos.get(position).getNombreTexto());
        holder.numero.setText(contactos.get(position).getTelefonoTexto());
        holder.aviso.setText(contactos.get(position).getTipoNotificacionTexto());

        //Se pone la imagen.
        if (contactos.get(position).getUriFoto() != null && !contactos.get(position).getUriFoto().equals("null")) {
            holder.imagen.setImageURI(Uri.parse(contactos.get(position).getUriFoto()));
        } else {
            holder.imagen.setImageResource(imagenContactoDefecto);
        }

        return convertView;
    }

    /*Clase que contiene las vistas existentes en la cada fila*/
    private static class ViewHolder {
        TextView nombre;
        TextView numero;
        TextView aviso;
        ImageView imagen;
    }

    public static class TAGpersonalizado {

        ViewHolder holder;
        int idContacto;

        public TAGpersonalizado(ViewHolder holder, int idContacto) {
            this.holder = holder;
            this.idContacto = idContacto;
        }

        public ViewHolder getHolder() {
            return holder;
        }

        public int getIdContacto() {
            return idContacto;
        }
    }
}