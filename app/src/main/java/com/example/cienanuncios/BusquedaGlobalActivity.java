package com.example.cienanuncios;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

//método que muestra los anuncios disponibles en un ListView
public class BusquedaGlobalActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_busqueda_global);

        Bundle extras = getIntent().getExtras();
        if (extras != null)
        {
            boolean global= extras.getBoolean("global");
            generarListado(global);
        }
    }

    //generación del ListView
    protected void generarListado(boolean pGlobal)
    {
        ArrayList<Integer> listaAnuncios = ListaAnunciosMAE.getAnuncios().devolverIdAnuncios();

        int[] ids = new int[listaAnuncios.size()];
        String[] titulos = new String[listaAnuncios.size()];
        Uri[] imagenes = new Uri[listaAnuncios.size()];
        String usuarioActual = ListaUsuariosMAE.getUsuarios().devolverUsuarioActual();

        int j=0;
        for (int i=0; i<listaAnuncios.size(); i++)
        {
            int id = listaAnuncios.get(i);
            Anuncio ad = ListaAnunciosMAE.getAnuncios().devolverAnuncio(id);
            if (ad.getAutor().equals(usuarioActual) != pGlobal) //comprueba si debe mostrar los anuncios de tu usuario o de los demás
            {
                ids[j]=id;
                titulos[j]=ad.getTitulo();
                imagenes[j]=ad.getUri();
                j++;
            }
        }

        ListView busqueda= (ListView) findViewById(R.id.ListViewRanking);
        AdaptadorListView eladap= new AdaptadorListView(getApplicationContext(),titulos,imagenes);
        busqueda.setAdapter(eladap);

        busqueda.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                Log.i("listView", "pos " + position + "id " + ids[position] + "titulo " + titulos[position]);
                if (ids[position]!=0)
                {

                    abrirNuevo(ids[position]);
                }
            }
        });
    }

    //abre la actividad MostrarAnuncio con el anuncio indicado
    private void abrirNuevo(int id)
    {
        Intent i = new Intent (this, MostrarAnuncioActivity.class);
        i.putExtra("id", id);
        startActivity(i);
    }

}