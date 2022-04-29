package com.example.cienanuncios;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

//clase que crea los Dialogs que se encuentran a través de la aplicación
public class Dialogos extends DialogFragment {
    private int code = -1;

    public Dialogos(int tipo)
    {
        super();
        code = tipo;
    }

    //este método determina que Dialog hay que mostrar
    //@NonNull
    //@Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState)
    {
        super.onCreateDialog(savedInstanceState);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        switch(code)
        {
            case 0 :
                crearDialogo0(builder);
                break;
            case 1 :
                crearDialogo1(builder);
                break;
            case 10 :
                crearDialogo10(builder);
                break;
            case 11 :
                crearDialogo11(builder);
                break;
            case 12 :
                crearDialogo12(builder);
                break;
            case 13 :
                crearDialogo13(builder);
                break;
            case 40 :
                crearDialogo40(builder);
                break;
            case 41 :
                crearDialogo41(builder);
                break;
            case 50 :
                crearDialogo50(builder);
                break;
            default :
                crearDialogoIndefinido(builder);
                break;
        }
        builder.setPositiveButton(getResources().getString(R.string.Entendido), new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {}
        });
        return builder.create();
    }

    private AlertDialog.Builder crearDialogoIndefinido(AlertDialog.Builder builder)
    {
        builder.setMessage(getResources().getString(R.string.DialogError));
        return builder;
    }

    private AlertDialog.Builder crearDialogo0(AlertDialog.Builder builder)
    {
        builder.setTitle(getResources().getString(R.string.Dialogo0Titulo));
        builder.setMessage(getResources().getString(R.string.Dialogo0Texto));
        return builder;
    }
    private AlertDialog.Builder crearDialogo1(AlertDialog.Builder builder)
    {
        builder.setTitle(getResources().getString(R.string.Dialogo1Titulo));
        builder.setMessage(getResources().getString(R.string.Dialogo1Texto));
        return builder;
    }
    private AlertDialog.Builder crearDialogo10(AlertDialog.Builder builder)
    {
        builder.setTitle(getResources().getString(R.string.Dialogo10Titulo));
        builder.setMessage(getResources().getString(R.string.Dialogo10Texto));
        return builder;
    }
    private AlertDialog.Builder crearDialogo11(AlertDialog.Builder builder)
    {
        builder.setTitle(getResources().getString(R.string.Dialogo11Titulo));
        builder.setMessage(getResources().getString(R.string.Dialogo11Texto));
        return builder;
    }
    private AlertDialog.Builder crearDialogo12(AlertDialog.Builder builder)
    {
        builder.setTitle(getResources().getString(R.string.Dialogo12Titulo));
        builder.setMessage(getResources().getString(R.string.Dialogo12Texto));
        return builder;
    }
    private AlertDialog.Builder crearDialogo13(AlertDialog.Builder builder)
    {
        builder.setTitle(getResources().getString(R.string.Dialogo13Titulo));
        builder.setMessage(getResources().getString(R.string.Dialogo13Texto));
        return builder;
    }
    private AlertDialog.Builder crearDialogo40(AlertDialog.Builder builder)
    {
        builder.setTitle(getResources().getString(R.string.Dialogo40Titulo));
        builder.setMessage(getResources().getString(R.string.Dialogo40Texto));
        return builder;
    }
    private AlertDialog.Builder crearDialogo41(AlertDialog.Builder builder)
    {
        builder.setTitle("Faltan datos");
        builder.setMessage("Falta algún dato que introducir");
        return builder;
    }
    private AlertDialog.Builder crearDialogo50(AlertDialog.Builder builder)
    {
        builder.setTitle("Saldo insuficiente");
        builder.setMessage("Por favor, añade más saldo a tu cuenta");
        return builder;
    }
}