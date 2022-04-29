package com.example.cienanuncios;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

//clase que guarda todos los usuarios
public class ListaUsuariosMAE
{
    private HashMap<String,Usuario> mapUsuarios;
    private String usuarioActual;
    private static ListaUsuariosMAE misUsuarios =null;

    private ListaUsuariosMAE ()
    {
        this.mapUsuarios =new HashMap<String,Usuario>();
        usuarioActual="";
    }
    public static synchronized ListaUsuariosMAE getUsuarios()
    {
        if (misUsuarios ==null)
        {
            misUsuarios =new ListaUsuariosMAE();
        }
        return misUsuarios;
    }
    public void anadirUsuario(String pNombre, String pContrasena, String pTelefono, String pLocalizacion, double pSaldo)
    {
        if (this.devolverUsuario(pNombre)==null)
        {
            Usuario nuevo = new Usuario(pNombre, pContrasena, pTelefono, pLocalizacion, pSaldo);
            mapUsuarios.put(pNombre, nuevo);
        }
    }

    public Usuario devolverUsuario(String pNombre)
    {
        Usuario user = null;
        if (mapUsuarios.containsKey(pNombre))
        {
            user = mapUsuarios.get(pNombre);
        }
        return user;
    }

    public ArrayList<String> devolverNombreUsuarios()
    {
        ArrayList<String> listaUsuarios = new ArrayList<>();
        for (String key : mapUsuarios.keySet() )
        {
            listaUsuarios.add(key);
        }
        return listaUsuarios;
    }

    public void borrarTodo()
    {
        mapUsuarios.clear();
        usuarioActual="";
    }

    public int devolverSize()
    {
        return this.mapUsuarios.size();
    }
    public void actualizarUsuario(String pUsuario)
    {
        usuarioActual=pUsuario;
    }

    public String devolverUsuarioActual()
    {
        return usuarioActual;
    }
}