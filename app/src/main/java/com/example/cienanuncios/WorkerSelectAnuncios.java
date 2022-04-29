package com.example.cienanuncios;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

//worker que devuelve los anuncios
public class WorkerSelectAnuncios extends Worker
{
    public WorkerSelectAnuncios(@NonNull Context context, @NonNull WorkerParameters workerParams)
    {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork()
    {
        String direccion = "http://ec2-52-56-170-196.eu-west-2.compute.amazonaws.com/prehecho001/WEB/SelectAnuncios.php";
        HttpURLConnection urlConnection = null;
        try
        {
            URL destino = new URL(direccion);
            urlConnection = (HttpURLConnection) destino.openConnection();
            urlConnection.setConnectTimeout(5000);
            urlConnection.setReadTimeout(5000);

            int statusCode = urlConnection.getResponseCode();
            Log.i("workerPHP","statusCodeSelectAnuncios: " + statusCode);
            if (statusCode == 200)
            {
                BufferedInputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
                String line, result = "";
                while ((line = bufferedReader.readLine()) != null) {
                    result += line;
                }
                inputStream.close();
                JSONArray jsonArray = new JSONArray(result);
                String[] lista = new String[jsonArray.length()*5];
                for(int i = 0; i < jsonArray.length(); i++)
                {
                    Integer id = jsonArray.getJSONObject(i).getInt("Id");
                    String titulo = jsonArray.getJSONObject(i).getString("Titulo");
                    String descripcion = jsonArray.getJSONObject(i).getString("Descripcion");
                    double precio = jsonArray.getJSONObject(i).getDouble("Precio");
                    String autor = jsonArray.getJSONObject(i).getString("Autor");
                    lista[5*i] = String.valueOf(id);
                    lista[5*i+1] = titulo;
                    lista[5*i+2] = descripcion;
                    lista[5*i+3] = String.valueOf(precio);
                    lista[5*i+4] = String.valueOf(autor);
                }
                Log.i("workerPHP","listaJsonAnuncios: " + lista);
                Data jason = new Data.Builder()
                        .putStringArray("listaAnuncios",lista)
                        .build();
                return Result.success(jason);
            }
            return Result.failure();
        }
        catch (MalformedURLException e) {e.printStackTrace();}
        catch (IOException e) {e.printStackTrace();}
        catch (JSONException e) {e.printStackTrace();}
        return Result.failure();
    }
}