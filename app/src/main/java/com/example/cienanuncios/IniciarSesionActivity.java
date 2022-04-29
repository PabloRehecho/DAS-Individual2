package com.example.cienanuncios;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.Console;
import java.util.ArrayList;

//primera actividad que se ejecuta, carga la información de la base de datos
public class IniciarSesionActivity extends AppCompatActivity
{

    //se cargan los usuarios
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_iniciar_sesion);
        ListaUsuariosMAE.getUsuarios().actualizarUsuario("");
        cargarUsuarios();
        cargarAnuncios();

    }

    //se comprueba que el usuario y contraseñas son correctos
    public void onClickIniciarSesion(View v)
    {
        TextView textViewUsuario = findViewById(R.id.EditTextUsuario);
        TextView textViewContraseña = findViewById(R.id.EditTextContraseña);
        String usuario = textViewUsuario.getText().toString();
        String contraseña = textViewContraseña.getText().toString();
        Usuario user = ListaUsuariosMAE.getUsuarios().devolverUsuario(usuario);
        if (user!=null)
        {
            String contraseñaGuardada = user.getContrasena();
            if (contraseñaGuardada.equals(contraseña))
            {
                ListaUsuariosMAE.getUsuarios().actualizarUsuario(usuario);
                Intent intentMenu= new Intent(this, MenuActivity.class);
                startActivity(intentMenu);
            }
            else
            {
                DialogFragment dialogoAlerta= new Dialogos(1);
                dialogoAlerta.show(getSupportFragmentManager(), "etiqueta");
            }
        }
        else
        {
            DialogFragment dialogoAlerta= new Dialogos(0);
            dialogoAlerta.show(getSupportFragmentManager(), "etiqueta");
        }
    }

    //se lanza RegistroActivity
    public void onClickRegistrarse(View v)
    {
        Intent intent= new Intent(IniciarSesionActivity.this,RegistroActivity.class);
        startActivityForResult(intent, 666);
    }

    //resultado de lanzar RegistroActivity
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 666 && resultCode == RESULT_OK)
        {
            lanzarNotificacionRegistro();
        }
    }

    //lanzar notificación que indica usuario correctamente registrado
    private void lanzarNotificacionRegistro()
    {
        NotificationManager elManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder elBuilder = new NotificationCompat.Builder(this, "IdCanal");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            NotificationChannel elCanal = new NotificationChannel("IdCanal", "Registro",
                    NotificationManager.IMPORTANCE_DEFAULT);
            elCanal.setDescription("Canal01");
            elCanal.enableLights(true);
            elCanal.setLightColor(Color.RED);
            elCanal.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            elCanal.enableVibration(true);
            elManager.createNotificationChannel(elCanal);
        }
        elBuilder.setSmallIcon(android.R.drawable.star_big_on)
                .setContentTitle(getResources().getString(R.string.NotificacionRegistroTitulo))
                .setSubText(getResources().getString(R.string.NotificacionRegistroSubTexto))
                .setVibrate(new long[]{0, 1000, 500, 1000})
                .setAutoCancel(true)
        ;
        elManager.notify(1, elBuilder.build());
    }

    // se accede a la base de datos remota y se cargan todos los usuarios
    private void cargarUsuarios ()
    {
        OneTimeWorkRequest otwr = new OneTimeWorkRequest.Builder(WorkerSelectUsers.class).build();
        WorkManager.getInstance(this).getWorkInfoByIdLiveData(otwr.getId())
                .observe(this, new Observer<WorkInfo>()
                {
                    @Override
                    public void onChanged(WorkInfo workInfo)
                    {
                        if(workInfo != null && workInfo.getState().isFinished())
                        {
                            String[] listaUsuarios = workInfo.getOutputData().getStringArray("listaUsuarios");
                            ListaUsuariosMAE.getUsuarios().borrarTodo();
                            for (int i=0; i<listaUsuarios.length; i+=5)
                            {
                                String nombre = listaUsuarios[i];
                                String contrasena = listaUsuarios[i+1];
                                String telefono = listaUsuarios[i+2];
                                String localizacion= listaUsuarios[i+3];
                                double saldo = Double.valueOf(listaUsuarios[i+4]);
                                ListaUsuariosMAE.getUsuarios().anadirUsuario(nombre,contrasena,telefono,localizacion, saldo);
                            }
                        }
                    }
                });
        WorkManager.getInstance(this).enqueue(otwr);
    }

    // se accede a la base de datos remota y se cargan todos los anuncios
    private void cargarAnuncios ()
    {
        OneTimeWorkRequest otwr = new OneTimeWorkRequest.Builder(WorkerSelectAnuncios.class).build();
        WorkManager.getInstance(this).getWorkInfoByIdLiveData(otwr.getId())
                .observe(this, new Observer<WorkInfo>()
                {
                    @Override
                    public void onChanged(WorkInfo workInfo)
                    {
                        if(workInfo != null && workInfo.getState().isFinished())
                        {
                            String[] listaAnuncios = workInfo.getOutputData().getStringArray("listaAnuncios");
                            ListaAnunciosMAE.getAnuncios().borrarTodo();
                            for (int i=0; i<listaAnuncios.length; i+=5)
                            {
                                int id = Integer.valueOf(listaAnuncios[i]);
                                String titulo = listaAnuncios[i+1];
                                String descripcion = listaAnuncios[i+2];
                                double precio = Double.valueOf(listaAnuncios[i+3]);
                                String autor = listaAnuncios[i+4];
                                String localizacion = ListaUsuariosMAE.getUsuarios().devolverUsuario(autor).getLocalizacion();

                                String nombrefich = autor + "-" + titulo + ".jpg";
                                FirebaseStorage storage = FirebaseStorage.getInstance();
                                StorageReference storageRef = storage.getReference();
                                StorageReference pathReference = storageRef.child(nombrefich);
                                pathReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>()
                                {
                                    @Override
                                    public void onSuccess(Uri uri)
                                    {
                                        Log.i("firebase",uri.toString());
                                        ListaAnunciosMAE.getAnuncios().anadirAnuncio(id, titulo, descripcion, precio, autor, localizacion, uri);
                                    }
                                });

                            }
                        }
                    }
                });
        WorkManager.getInstance(this).enqueue(otwr);
    }
}