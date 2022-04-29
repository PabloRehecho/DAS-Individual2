package com.example.cienanuncios;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

//esta actividad te permite añadir saldo a tu cuenta
public class AnadirSaldoActivity extends AppCompatActivity
{
    double saldoActual;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anadir_saldo);
        TextView textViewSaldo = findViewById(R.id.TextViewSaldoActual);
        String textoSaldo = textViewSaldo.getText().toString();
        String usuarioActual = ListaUsuariosMAE.getUsuarios().devolverUsuarioActual();
        saldoActual = ListaUsuariosMAE.getUsuarios().devolverUsuario(usuarioActual).getSaldo();
        textViewSaldo.setText(textoSaldo + saldoActual);
    }

    // lanza el WorkerUpdateSaldo
    public void onClickAnadirSaldo(View v)
    {
        TextView textViewCantidad = findViewById(R.id.EditTextAnadirSaldo);
        double cantidad = Double.valueOf(textViewCantidad.getText().toString());
        String usuario = ListaUsuariosMAE.getUsuarios().devolverUsuarioActual();

        Data.Builder data = new Data.Builder();
        data.putString("Operacion", "Recarga");
        data.putDouble("Cantidad", cantidad);
        data.putString("Sumador", usuario);
        data.putString("Restador", "nadie");

        OneTimeWorkRequest otwr = new OneTimeWorkRequest.Builder(WorkerUpdateSaldo.class)
                .setInputData(data.build())
                .build();
        WorkManager.getInstance(this).getWorkInfoByIdLiveData(otwr.getId())
                .observe(this, new Observer<WorkInfo>() {
                    @Override
                    public void onChanged(WorkInfo workInfo) {
                        if (workInfo != null && workInfo.getState().isFinished())
                        {
                            Log.i("workerPHP", "Saldo actualizado");
                            fuera(cantidad);
                        }
                    }
                });
        WorkManager.getInstance(this).enqueue(otwr);
    }

    //permite salir de la aplicación, llamado cuando se termine el Worker
    public void fuera(double cantidad)
    {
        String usuario = ListaUsuariosMAE.getUsuarios().devolverUsuarioActual();
        ListaUsuariosMAE.getUsuarios().devolverUsuario(usuario).anadirSaldo(cantidad);
        Intent intentReset = new Intent(this, MenuActivity.class);
        startActivity(intentReset);
    }
}