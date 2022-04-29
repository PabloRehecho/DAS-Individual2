package com.example.cienanuncios;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

//actividad que permite abrir otras actividades
public class MenuActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
    }
    public void onClickBusqueda(View v)
    {
        Intent intentBuscar= new Intent(this, BusquedaGlobalActivity.class);
        intentBuscar.putExtra("global",true);
        startActivity(intentBuscar);
    }
    public void onClickCrearAnuncio(View v)
    {
        Intent intentCrearAnuncio= new Intent(this, CrearAnuncioActivity.class);
        startActivity(intentCrearAnuncio);
    }
    public void onClickMostrarMisAnuncios(View v)
    {
        Intent intentBuscar= new Intent(this, BusquedaGlobalActivity.class);
        intentBuscar.putExtra("global",false);
        startActivity(intentBuscar);
    }
    public void onClickAnadirSaldo(View v)
    {
        Intent intentSaldo= new Intent(this, AnadirSaldoActivity.class);
        startActivity(intentSaldo);
    }
    public void onClickCerrarSesion(View v)
    {
        Intent intentInit= new Intent(this, IniciarSesionActivity.class);
        startActivity(intentInit);
    }



}