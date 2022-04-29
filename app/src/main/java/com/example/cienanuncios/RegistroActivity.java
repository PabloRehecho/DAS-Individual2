package com.example.cienanuncios;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

//actividad que realiza los registros
public class RegistroActivity extends AppCompatActivity
{
    private String nombreUsuario;
    private String contrasena;
    private String telefono;
    private String localizacion = "patata";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);
    }

    //se comprueba si se cumplen los requisitos y se cumplen
    //se crea un nuevo usaurio en la base de datos
    public void onClickTerminarRegistro(View v) {
        if (comprobarRequisitos())
        {
            calcularLocalizacion();

            Intent intent = new Intent();
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    //método que comprueba varios factores y genera Dialogs para informar
    private boolean comprobarRequisitos() {
        boolean aceptar = false;
        TextView textViewUsuarioNuevo = findViewById(R.id.EditTextUsuarioNuevo);
        TextView textViewContraseña1 = findViewById(R.id.EditTextContraseñaNueva);
        TextView textViewContraseña2 = findViewById(R.id.EditTextRepetirContraseña);
        nombreUsuario = textViewUsuarioNuevo.getText().toString();
        contrasena = textViewContraseña1.getText().toString();
        String contraseña2 = textViewContraseña2.getText().toString();

        Usuario user = ListaUsuariosMAE.getUsuarios().devolverUsuario(nombreUsuario);

        if (user == null)
        {
            if (contrasena.equals(contraseña2))
            {
                if (contrasena.length() >= 4)
                {
                    aceptar = true;
                }
                else
                {
                    DialogFragment dialogoAlerta = new Dialogos(12);
                    dialogoAlerta.show(getSupportFragmentManager(), "etiqueta");
                }
            }
            else
            {
                DialogFragment dialogoAlerta = new Dialogos(11);
                dialogoAlerta.show(getSupportFragmentManager(), "etiqueta");
            }
        }
        else
        {
            DialogFragment dialogoAlerta = new Dialogos(10);
            dialogoAlerta.show(getSupportFragmentManager(), "etiqueta");
        }
        return aceptar;
    }

    //lanza el WorkerInsertUsuario para insertar el usuario en la base de datos remota
    private void crearUsuario()
    {
        TextView textViewTelefono = findViewById(R.id.EditTextTelefonoNuevo);
        telefono = textViewTelefono.getText().toString();
        ListaUsuariosMAE.getUsuarios().anadirUsuario(nombreUsuario, contrasena, telefono, localizacion, 0.0);

        Data.Builder data = new Data.Builder();
        data.putString("Nombre", nombreUsuario);
        data.putString("Contrasena", contrasena);
        data.putString("Telefono", telefono);
        data.putString("Localizacion", localizacion);

        OneTimeWorkRequest otwr = new OneTimeWorkRequest.Builder(WorkerInsertUser.class)
                .setInputData(data.build())
                .build();
        WorkManager.getInstance(this).getWorkInfoByIdLiveData(otwr.getId())
                .observe(this, new Observer<WorkInfo>() {
                    @Override
                    public void onChanged(WorkInfo workInfo) {
                        if (workInfo != null && workInfo.getState().isFinished()) {
                            Log.i("workerPHP", "Usuario insertado");
                        }
                    }
                });
        WorkManager.getInstance(this).enqueue(otwr);
    }

    //imprime el token del usuario
    private void comprobarToken()
    {
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>()
        {
            @Override
            public void onComplete(@NonNull Task<String> task)
            {
                if (!task.isSuccessful())
                {
                    return;
                }
                String token = task.getResult();
                Log.i("token",token);
                insertarToken(token);
            }
        });
    }

    //inserta el token en la base de datos
    private void insertarToken(String token)
    {
        Data.Builder data = new Data.Builder();
        data.putString("Nombre", nombreUsuario);
        data.putString("Token", token);
        OneTimeWorkRequest otwr = new OneTimeWorkRequest.Builder(WorkerInsertToken.class)
                .setInputData(data.build())
                .build();
        WorkManager.getInstance(this).getWorkInfoByIdLiveData(otwr.getId())
                .observe(this, new Observer<WorkInfo>() {
                    @Override
                    public void onChanged(WorkInfo workInfo) {
                        if (workInfo != null && workInfo.getState().isFinished()) {
                            Log.i("workerPHP", "Token insertado");
                        }
                    }
                });
        WorkManager.getInstance(this).enqueue(otwr);
    }

    //petición del permiso de geolocaclización
    private void calcularLocalizacion()
    {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            localizacion = "noPermitido";
            Log.i("geolocalizacion","noPermitido");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},345);
            crearUsuario();
            comprobarToken();
        }
        else
        {
            //localizacion="kiwi";
            Log.i("geolocalizacion","siPermitido");
            FusedLocationProviderClient proveedordelocalizacion = LocationServices.getFusedLocationProviderClient(this);
            proveedordelocalizacion.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>()
                    {
                        @Override
                        public void onSuccess(Location location)
                        {
                            if (location != null)
                            {
                                Log.i("geolocalizacion","successUtil");
                                localizacion=String.valueOf(location.getLatitude())+String.valueOf(location.getLongitude());
                                crearUsuario();
                                comprobarToken();
                            }
                            else
                            {
                                Log.i("geolocalizacion","successInutil");
                                localizacion ="vacio";
                                crearUsuario();
                                comprobarToken();
                            }
                        }
                    })
                    .addOnFailureListener(this, new OnFailureListener()
                    {
                        @Override
                        public void onFailure(@NonNull Exception e)
                        {
                            Log.i("geolocalizacion","fallo");
                            localizacion ="fallo";
                        }
                    });
        }
    }

    //tratado del permiso de geolocalización
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode)
        {
            case 345:{
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    calcularLocalizacion();
                }
                else
                {
                    crearUsuario();
                    comprobarToken();
                }
            }
        }
    }
}