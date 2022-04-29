package com.example.cienanuncios;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

//Esta clase crea el ListView personalizado que contiene
//un array de Strings titulos y uno de Uris
public class AdaptadorListView extends BaseAdapter
{
    private Context contexto;
    private LayoutInflater inflater;
    private String[] titulos;
    private Uri[] imagenes;

    @Override
    public int getCount() {
        return titulos.length;
    }

    @Override
    public Object getItem(int i)
    {
        return titulos[i];
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup)
    {
        view=inflater.inflate(R.layout.list_view_personalizado,null);
        TextView nombre = (TextView) view.findViewById(R.id.TextViewListado);
        ImageView imagen = view.findViewById(R.id.ImageViewListado);
        nombre.setText(titulos[i]);

        Glide.with(contexto)
                .load(imagenes[i])
                .timeout(10000)
                .into(imagen);
        //imagen.setImageURI(imagenes[i]);
        return view;
    }

    public AdaptadorListView(Context pcontext, String[] pTitulos, Uri[] pImagenes)
    {
        contexto = pcontext;
        titulos = pTitulos;
        imagenes = pImagenes;
        inflater = (LayoutInflater) contexto.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


}