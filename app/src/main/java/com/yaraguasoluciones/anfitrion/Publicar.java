package com.yaraguasoluciones.anfitrion;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.io.File;

/**
 * Created by luiszambrano on 26/03/15.
 */
public class Publicar {

    public void obtenerCodigo(final Context context, Intent data) {


        try{
            Ion.with(context)
                    .load("http://inscripciones.cnpven.org/archivos/new")
                    .asJsonObject().setCallback(new FutureCallback<JsonObject>() {
                @Override
                public void onCompleted(Exception e, JsonObject result) {
                    if (e!=null){
                        Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show();
                    }else if (result!=null){
                        Toast.makeText(context, result.toString(), Toast.LENGTH_LONG).show();
                    }
                }
            });

        }catch (Exception e){
            Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    public void enviarArchivo(final Context context, File nombreArchivo) {

        String url = "http://inscripciones.cnpven.org/archivos";

        Toast.makeText(context, nombreArchivo.getAbsolutePath().toString(), Toast.LENGTH_LONG).show();

        Ion.with(context)
                .load(url)
                //.uploadProgressBar(uploadProgressBar)
                .setMultipartParameter("archivo[tipodocumento_id]", "1")
                .setMultipartParameter("archivo[afiliado_id]", "1")
                .setMultipartFile("archivo[ruta]", "image/jpeg", nombreArchivo)
                .asJsonObject();
    }
}
