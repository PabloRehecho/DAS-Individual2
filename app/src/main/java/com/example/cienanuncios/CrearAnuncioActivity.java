package com.example.cienanuncios;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

//actividad que permite crear los anuncios
public class CrearAnuncioActivity extends AppCompatActivity {

    File fichImg;
    Uri uriimagen;
    Bitmap bitmapredimensionado=null;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_anuncio);
    }

    // pide permiso para hacer fotos
    public void onClickHacerFoto(View v)
    {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
        {
            Log.i("camara","noPermitido");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},725);
        }
        else
        {
            hacerFoto();
        }
    }

    //lo que pasa cuando se da permiso para hacer fotos
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode)
        {
            case 725:{
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    hacerFoto();
                }
                else
                {

                }
            }
        }
    }

    //abre la cámara para hacer una foto
    public void hacerFoto()
    {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String nombrefich = "IMG_" + timeStamp + "_";
        File directorio=this.getFilesDir();
        fichImg = null;
        uriimagen = null;
        try
        {
            fichImg = File.createTempFile(nombrefich, ".jpg",directorio);
            uriimagen = FileProvider.getUriForFile(this, "com.example.cienanuncios", fichImg);
        } catch (IOException e) {}
        Intent elIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        elIntent.putExtra(MediaStore.EXTRA_OUTPUT, uriimagen);
        startActivityForResult(elIntent, 123);
    }

    //procesa la imagen captada por la cámara
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 123 && resultCode == RESULT_OK)
        {
            Bitmap bitmapFoto = BitmapFactory.decodeFile(fichImg.toString());
            ImageView elImageView = findViewById(R.id.ImageViewCrearAnuncio);
            int anchoDestino = elImageView.getWidth();
            int altoDestino = elImageView.getHeight();
            int anchoImagen = bitmapFoto.getWidth();
            int altoImagen = bitmapFoto.getHeight();
            float ratioImagen = (float) anchoImagen / (float) altoImagen;
            float ratioDestino = (float) anchoDestino / (float) altoDestino;
            int anchoFinal = anchoDestino;
            int altoFinal = altoDestino;
            if (ratioDestino > ratioImagen) {
                anchoFinal = (int) ((float)altoDestino * ratioImagen);
            } else {
                altoFinal = (int) ((float)anchoDestino / ratioImagen);
            }
            bitmapredimensionado = Bitmap.createScaledBitmap(bitmapFoto,anchoFinal,altoFinal,true);
            elImageView.setImageURI(uriimagen);

            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            Uri contentUri = Uri.fromFile(fichImg);
            mediaScanIntent.setData(contentUri);
            this.sendBroadcast(mediaScanIntent);
        }
    }

    //se carga el anuncio
    public void onClickTerminarAnuncio(View v)
    {
        TextView textViewTitulo = findViewById(R.id.EditTextCrearAnuncioTitulo);
        TextView textViewDescripcion = findViewById(R.id.EditTextAnuncioNuevoDescripcion);
        TextView textViewPrecio = findViewById(R.id.EditTextAnuncioNuevoPrecio);

        String titulo = textViewTitulo.getText().toString();
        String descripcion = textViewDescripcion.getText().toString();
        double precio = Double.parseDouble(textViewPrecio.getText().toString());
        String autor = ListaUsuariosMAE.getUsuarios().devolverUsuarioActual();
        String localizacion = ListaUsuariosMAE.getUsuarios().devolverUsuario(autor).getLocalizacion();

        if (titulo==null || descripcion ==null  || localizacion==null)
        {
            DialogFragment dialogoAlerta = new Dialogos(41);
            dialogoAlerta.show(getSupportFragmentManager(), "etiqueta");
            return;
        }
        ListaAnunciosMAE.getAnuncios().anadirAnuncio(-1, titulo, descripcion, precio, autor, localizacion, uriimagen);

        subirImagen(titulo, autor);
        Data.Builder data = new Data.Builder();
        data.putString("Titulo", titulo);
        data.putString("Descripcion", descripcion);
        data.putDouble("Precio", precio);
        data.putString("Autor", autor);

        OneTimeWorkRequest otwr = new OneTimeWorkRequest.Builder(WorkerInsertAnuncio.class)
                .setInputData(data.build())
                .build();
        WorkManager.getInstance(this).getWorkInfoByIdLiveData(otwr.getId())
                .observe(this, new Observer<WorkInfo>() {
                    @Override
                    public void onChanged(WorkInfo workInfo) {
                        if (workInfo != null && workInfo.getState().isFinished())
                        {
                            Log.i("workerPHP", "Anuncio insertado");
                        }
                    }
                });
        WorkManager.getInstance(this).enqueue(otwr);
        Intent intentMenu= new Intent(this, MenuActivity.class);
        startActivity(intentMenu);
    }

    //sube la imagen al servidor de Firebase
    private void subirImagen(String pTitulo, String pAutor)
    {
        if (uriimagen!=null)
        {
            String nombrefich = pAutor + "-" + pTitulo + ".jpg";

            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference();
            StorageReference imageRef = storageRef.child(nombrefich);
            imageRef.putFile(uriimagen);
        }
        else
        {
            DialogFragment dialogoAlerta= new Dialogos(40);
            dialogoAlerta.show(getSupportFragmentManager(), "etiqueta");
        }
    }

}