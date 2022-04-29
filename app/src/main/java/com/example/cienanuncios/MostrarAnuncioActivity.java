package com.example.cienanuncios;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

//clase que muestra el anuncio seleccionado de la ListView,
//también permite eliminarlo o comprar el producto
public class MostrarAnuncioActivity extends AppCompatActivity
{
    int id;
    String titulo;
    String autor;
    double precio;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mostrar_anuncio);

        Bundle extras = getIntent().getExtras();
        if (extras != null)
        {
            id = extras.getInt("id");


            Anuncio ad = ListaAnunciosMAE.getAnuncios().devolverAnuncio(id);
            titulo = ad.getTitulo();
            String descripcion = ad.getDescripcion();
            autor = ad.getAutor();
            String localizacion = ad.getLocalizacion();
            precio = ad.getPrecio();
            Uri imagen = ad.getUri();

            TextView textViewTitulo = findViewById(R.id.TextViewMostrarAnuncioTitulo);
            textViewTitulo.setText(titulo);
            TextView textViewDescripcion = findViewById(R.id.TextViewMostrarAnuncioDescripcion);
            textViewDescripcion.setText(descripcion);
            TextView textViewPrecio = findViewById(R.id.TextViewMostrarAnuncioPrecio);
            textViewPrecio.setText("Precio: " + String.valueOf(precio));
            ImageView imageViewImagen = findViewById(R.id.ImageViewMostrarAnuncio);
            Glide.with(this)
                    .load(imagen)
                    .timeout(10000)
                    .into(imageViewImagen);

            TextView textViewAutorDistancia = findViewById(R.id.TextViewMostrarAnuncioAutorDistancia);

            Button boton = findViewById(R.id.BotonComprarEliminar);
            String usuarioActual = ListaUsuariosMAE.getUsuarios().devolverUsuarioActual();
            if (usuarioActual.equals(autor)) {
                boton.setText("Eliminar");
                textViewAutorDistancia.setVisibility(View.INVISIBLE);
            }
            else
            {
                boton.setText("Comprar");
                String posicionActual = ListaUsuariosMAE.getUsuarios().devolverUsuario(usuarioActual).getLocalizacion();
                double distancia = calcularDistancia(posicionActual, localizacion);
                textViewAutorDistancia.setVisibility(View.VISIBLE);
                textViewAutorDistancia.setText("Autor: " + autor + "\nDistancia: " + distancia + " kilómetros");
            }
        }

    }

    //calcula la distancia entre dos localizaciones
    private double calcularDistancia(String posicionActual, String localizacion)
    {
        String[] actual = posicionActual.split("-");
        double actual1 = Double.valueOf(actual[0]);
        double actual2 = Double.valueOf(actual[1]);

        String[] lejos = localizacion.split("-");
        double lejos1 = Double.valueOf(lejos[0]);
        double lejos2 = Double.valueOf(actual[1]);

        Location startPoint=new Location("locationA");
        startPoint.setLatitude(actual1);
        startPoint.setLongitude(actual2);

        Location endPoint=new Location("locationB");
        endPoint.setLatitude(lejos1);
        endPoint.setLongitude(lejos2);

        double distance=startPoint.distanceTo(endPoint);
        return distance/1000;
    }


    //realiza la compra o la eliminación del anuncio, depende se eres el autor o no
    public void onClickComprarEliminar(View v)
    {
        String usuarioActual = ListaUsuariosMAE.getUsuarios().devolverUsuarioActual();
        if (usuarioActual.equals(autor))
        {
            String usuario = ListaUsuariosMAE.getUsuarios().devolverUsuarioActual();

            Data.Builder data = new Data.Builder();
            data.putInt("Id", id);

            OneTimeWorkRequest otwr = new OneTimeWorkRequest.Builder(WorkerDeleteAnuncio.class)
                    .setInputData(data.build())
                    .build();
            WorkManager.getInstance(this).getWorkInfoByIdLiveData(otwr.getId())
                    .observe(this, new Observer<WorkInfo>() {
                        @Override
                        public void onChanged(WorkInfo workInfo) {
                            if (workInfo != null && workInfo.getState().isFinished())
                            {
                                Log.i("workerPHP", "Anuncio eliminado");
                                fuera();
                            }
                        }
                    });
            WorkManager.getInstance(this).enqueue(otwr);
        }
        else
        {
            double saldoActual = ListaUsuariosMAE.getUsuarios().devolverUsuario(usuarioActual).getSaldo();
            if (saldoActual < precio)
            {
                DialogFragment dialogoAlerta = new Dialogos(50);
                dialogoAlerta.show(getSupportFragmentManager(), "etiqueta");
            }
            else
            {
                String usuario = ListaUsuariosMAE.getUsuarios().devolverUsuarioActual();

                Data.Builder data = new Data.Builder();
                data.putString("Operacion", "Compra");
                data.putDouble("Cantidad", precio);
                data.putString("Sumador", autor);
                data.putString("Restador", usuarioActual);

                OneTimeWorkRequest otwr = new OneTimeWorkRequest.Builder(WorkerUpdateSaldo.class)
                        .setInputData(data.build())
                        .build();
                WorkManager.getInstance(this).getWorkInfoByIdLiveData(otwr.getId())
                        .observe(this, new Observer<WorkInfo>() {
                            @Override
                            public void onChanged(WorkInfo workInfo) {
                                if (workInfo != null && workInfo.getState().isFinished())
                                {
                                    Log.i("workerPHP", "Compra realizada");
                                    mensajeFCM();
                                    fuera2();
                                }
                            }
                        });
                WorkManager.getInstance(this).enqueue(otwr);
            }

        }
    }


    //permite salir al menú cuando se finalice el WorkerDeleteAnuncio, borra el anuncio de ListaAnunciosMAE
    public void fuera()
    {
        ListaAnunciosMAE.getAnuncios().borrarAnuncio(id);
        Intent intentReset = new Intent(this, MenuActivity.class);
        startActivity(intentReset);
    }
    //permite salir al menú cuando se finalice el WorkerUpdateSaldo, actualiza los saldos de los usuarios
    public void fuera2()
    {
        String usuario = ListaUsuariosMAE.getUsuarios().devolverUsuarioActual();
        ListaUsuariosMAE.getUsuarios().devolverUsuario(autor).anadirSaldo(precio);
        ListaUsuariosMAE.getUsuarios().devolverUsuario(usuario).anadirSaldo(precio*-1);
        Intent intentReset = new Intent(this, MenuActivity.class);
        startActivity(intentReset);
    }

    public void mensajeFCM()
    {
        String usuario = ListaUsuariosMAE.getUsuarios().devolverUsuarioActual();

        Data.Builder data = new Data.Builder();
        data.putString("Nombre", autor);

        OneTimeWorkRequest otwr = new OneTimeWorkRequest.Builder(WorkerSelectToken.class)
                .setInputData(data.build())
                .build();
        WorkManager.getInstance(this).getWorkInfoByIdLiveData(otwr.getId())
                .observe(this, new Observer<WorkInfo>() {
                    @Override
                    public void onChanged(WorkInfo workInfo) {
                        Log.i("FCM", "work info: " + workInfo);
                        if(workInfo != null && workInfo.getState().isFinished())
                        {
                            String token = workInfo.getOutputData().getString("Token");
                            Log.i("FCM", "token: " + token);
                            lanzarFCM(token);
                        }
                    }
                });
        WorkManager.getInstance(this).enqueue(otwr);
    }

    private void lanzarFCM(String pToken)
    {
        String usuario = ListaUsuariosMAE.getUsuarios().devolverUsuarioActual();

        Data.Builder data = new Data.Builder();
        data.putString("Token", pToken);
        data.putString("Titulo", titulo);

        OneTimeWorkRequest otwr = new OneTimeWorkRequest.Builder(WorkerSendFCM.class)
                .setInputData(data.build())
                .build();
        WorkManager.getInstance(this).getWorkInfoByIdLiveData(otwr.getId())
                .observe(this, new Observer<WorkInfo>() {
                    @Override
                    public void onChanged(WorkInfo workInfo) {
                        if(workInfo != null && workInfo.getState().isFinished())
                        {
                            Log.i("FMC","fin");
                        }
                    }
                });
        WorkManager.getInstance(this).enqueue(otwr);
    }


}