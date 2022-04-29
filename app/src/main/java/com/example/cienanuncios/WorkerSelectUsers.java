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

//worker que devuelve los usuarios
public class WorkerSelectUsers extends Worker
{
    public WorkerSelectUsers(@NonNull Context context, @NonNull WorkerParameters workerParams)
    {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork()
    {
        String direccion = "http://ec2-52-56-170-196.eu-west-2.compute.amazonaws.com/prehecho001/WEB/SelectUsers.php";
        HttpURLConnection urlConnection = null;
        try
        {
            URL destino = new URL(direccion);
            urlConnection = (HttpURLConnection) destino.openConnection();
            urlConnection.setConnectTimeout(5000);
            urlConnection.setReadTimeout(5000);

            int statusCode = urlConnection.getResponseCode();
            Log.i("workerPHP","statusCodeSelectUsers: " + statusCode);
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
                    String nombre = jsonArray.getJSONObject(i).getString("Nombre");
                    String contrasena = jsonArray.getJSONObject(i).getString("Contrasena");
                    String telefono = jsonArray.getJSONObject(i).getString("Telefono");
                    String localizacion = jsonArray.getJSONObject(i).getString("Localizacion");
                    double saldo = jsonArray.getJSONObject(i).getDouble("Saldo");
                    lista[5*i] = nombre;
                    lista[5*i+1] = contrasena;
                    lista[5*i+2] = telefono;
                    lista[5*i+3] = localizacion;
                    lista[5*i+4] = String.valueOf(saldo);
                }
                Log.i("workerPHP","listaJsonUsuarios: " + lista);
                Data jason = new Data.Builder()
                        .putStringArray("listaUsuarios",lista)
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