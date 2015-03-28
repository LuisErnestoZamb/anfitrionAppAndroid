package com.yaraguasoluciones.anfitrion;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.io.File;

/**
 * Created by luiszambrano on 26/03/15.
 */
public class Publicar {

    public void obtenerCodigo(final Context context, final File nombreArchivo, final String cedula, final String nac,final String optionTipo) {

    /*
    http://0.0.0.0:3000/archivos/mostrarafiliado.json?nac=V&cedula=274015
    */

        try{
            Ion.with(context)
                    .load("http://inscripciones.cnpven.org/archivos/mostrarafiliado.json")
                    .setMultipartParameter("nac", nac)
                    .setMultipartParameter("cedula", cedula)
                    .asJsonObject().setCallback(new FutureCallback<JsonObject>() {
                @Override
                public void onCompleted(Exception e, JsonObject result) {
                    if (e!=null){
                        Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show();

                        // Show error message.
                        // Montrando error
                        AlertDialog dialog = new AlertDialog.Builder(context)
                                .setTitle("Error")
                                .setMessage("La cédula ingresada es incorrecta.")
                                .setCancelable(true)
                                .setNegativeButton("Close", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (dialog != null) {
                                            dialog.cancel();
                                            dialog = null;
                                        }
                                    }
                                })
                                .create();
                        dialog.show();

                    }else if (result!=null){
                        Toast.makeText(context, result.toString(), Toast.LENGTH_LONG).show();

                        String afiliado_id = result.get("id").toString();
                        enviarArchivo(context, nombreArchivo, afiliado_id, optionTipo);
                    }
                }
            });

        }catch (Exception e){
            Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    public void enviarArchivo(Context context, File nombreArchivo, String afiliado_id, String tipodocumento_id) {

        String url = "http://inscripciones.cnpven.org/archivos";

        Ion.with(context)
                .load(url)
                //.uploadProgressBar(uploadProgressBar)
                .setMultipartParameter("archivo[tipodocumento_id]", tipodocumento_id)
                .setMultipartParameter("archivo[afiliado_id]", afiliado_id)
                .setMultipartFile("archivo[ruta]", "image/jpeg", nombreArchivo)
                .asJsonObject();
    }
}
