package com.example.cienanuncios;

import android.graphics.Bitmap;
import android.net.Uri;

import java.util.ArrayList;
import java.util.HashMap;


//clase que guarda todos los anuncios
public class ListaAnunciosMAE
{
    private int lastKey;
    private HashMap<Integer,Anuncio> mapAnuncios;
    private static ListaAnunciosMAE misAnuncios =null;

    private ListaAnunciosMAE ()
    {
        this.mapAnuncios =new HashMap<Integer,Anuncio>();
        lastKey=0;
    }
    public static synchronized ListaAnunciosMAE getAnuncios()
    {
        if (misAnuncios ==null)
        {
            misAnuncios =new ListaAnunciosMAE();
        }
        return misAnuncios;
    }
    public void anadirAnuncio(int pId, String pTitulo, String pDescripcion, double pPrecio, String pAutor, String pLocalizacion, Uri pUri)
    {
        int id;
        if (pId==-1)
        {
            lastKey++;
            id = lastKey;
        }
        else
        {
            id = pId;
            if (id>lastKey)
            {
                lastKey=id;
            }
        }

        if (this.devolverAnuncio(id)==null)
        {
            Anuncio nuevo = new Anuncio(id, pTitulo, pDescripcion, pPrecio, pAutor, pLocalizacion, pUri);
            mapAnuncios.put(id, nuevo);
        }
    }

    public Anuncio devolverAnuncio(int pId)
    {
        Anuncio ad=null;
        if (mapAnuncios.containsKey(pId))
        {
            ad= mapAnuncios.get(pId);
        }
        return ad;
    }

    public ArrayList<Integer> devolverIdAnuncios()
    {
        ArrayList<Integer> listaAnuncios = new ArrayList<>();
        for (int key : mapAnuncios.keySet() )
        {
            listaAnuncios.add(key);
        }
        return listaAnuncios;
    }

    public int devolverLastKey()
    {
        return this.lastKey;
    }

    public void borrarTodo()
    {
        mapAnuncios.clear();
    }

    public void borrarAnuncio(int id)
    {
        mapAnuncios.remove(id);
    }
}