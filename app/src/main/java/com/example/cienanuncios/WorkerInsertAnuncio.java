package com.example.cienanuncios;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

//worker que inserta un anuncio
public class WorkerInsertAnuncio extends Worker
{
    public WorkerInsertAnuncio(@NonNull Context context, @NonNull WorkerParameters workerParams)
    {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork()
    {
        String direccion = "http://ec2-52-56-170-196.eu-west-2.compute.amazonaws.com/prehecho001/WEB/InsertAnuncio.php";
        HttpURLConnection urlConnection = null;
        try
        {
            URL destino = new URL(direccion);
            urlConnection = (HttpURLConnection) destino.openConnection();
            urlConnection.setConnectTimeout(5000);
            urlConnection.setReadTimeout(5000);
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoOutput(true);
            urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            String titulo = getInputData().getString("Titulo");
            String descripcion = getInputData().getString("Descripcion");
            double precio = getInputData().getDouble("Precio",29.95);
            String autor = getInputData().getString("Autor");


            PrintWriter out = new PrintWriter(urlConnection.getOutputStream());
            String parametros="Titulo="+titulo+"&Descripcion="+descripcion+"&Precio="+precio+"&Autor="+autor;
            out.print(parametros);
            out.close();

            int statusCode = urlConnection.getResponseCode();
            Log.i("workerPHP","statusCodeInsercionAnuncio: " + statusCode);
            if (statusCode == 200)
            {
                return Result.success();
            }
            return Result.failure();
        }
        catch (MalformedURLException e) {e.printStackTrace();}
        catch (IOException e) {e.printStackTrace();}
        return Result.failure();
    }
}