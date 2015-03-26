package com.yaraguasoluciones.anfitrion;

import android.content.Context;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

/**
 * Created by luiszambrano on 26/03/15.
 */
public class Publicar {

    public void enviarArchivo(final Context context, String ruta) {

        Ion.with(context)
                .load("http://inscripciones.cnpven.org/documentos")
                //.uploadProgressBar(uploadProgressBar)
                .setMultipartParameter("goop", "noop")
                //.setMultipartFile("filename.zip", new File(ruta))
                .asJsonObject().setCallback(new FutureCallback<JsonObject>() {
            @Override
            public void onCompleted(Exception e, JsonObject result) {
                Toast.makeText(context, result.toString(), Toast.LENGTH_LONG).show();

            }
        });
    }

}
