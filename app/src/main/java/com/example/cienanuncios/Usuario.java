package com.example.cienanuncios;

import java.util.ArrayList;

//clase que guarda la información de un usuario
public class Usuario
{
    private String nombre;
    private String contrasena;
    private String telefono;
    private String localizacion;
    private double saldo;

    public Usuario(String pNombre, String pContrasena, String pTelefono, String pLocalizacion, double pSaldo)
    {
        this.nombre = pNombre;
        this.contrasena = pContrasena;
        this.telefono = pTelefono;
        this.localizacion = pLocalizacion;
        this.saldo = pSaldo;
    }

    public String getNombre()
    {
        return this.nombre;
    }

    public String getContrasena()
    {
        return this.contrasena;
    }

    public String getTelefono()
    {
        return this.telefono;
    }

    public String getLocalizacion()
    {
        return this.localizacion;
    }

    public double getSaldo() { return this.saldo;}

    public void anadirSaldo(double saldoActual) { this.saldo+=saldoActual; }
}