package com.example.cienanuncios;

import android.graphics.Bitmap;
import android.net.Uri;

//Clase que guarda la información de un anuncio
public class Anuncio
{
    private int id;
    private String titulo;
    private String descripcion;
    private double precio;
    private String autor;
    private String localizacion;
    private Uri uri;

    public Anuncio(int pId, String pTitulo, String pDescripcion, double pPrecio, String pAutor, String pLocalizacion, Uri pUri)
    {
        this.id = pId;
        this.titulo = pTitulo;
        this.descripcion = pDescripcion;
        this.precio = pPrecio;
        this.autor = pAutor;
        this.localizacion = pLocalizacion;
        this.uri = pUri;
    }

    public int getId()
    {
        return this.id;
    }
    public String getTitulo()
    {
        return this.titulo;
    }

    public String getDescripcion()
    {
        return this.descripcion;
    }
    public double getPrecio(){return this.precio;}

    public String getAutor()
    {
        return this.autor;
    }

    public String getLocalizacion() { return this.localizacion;}

    public Uri getUri() { return this.uri; }
}
