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
        Ion.with(context)
                .load("http://inscripciones.cnpven.org/documentos/new")
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        // do stuff with the result or error
                        Toast.makeText(context, result.toString(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    public void enviarArchivo(final Context context, File data) {

        Ion.with(context)
                .load("http://inscripciones.cnpven.org/documentos/new")
                //.uploadProgressBar(uploadProgressBar)
                .setMultipartParameter("afiliado_id", "1")
                .setMultipartParameter("tipodocumento_id", "1")
                .setMultipartFile("filename.jpg", data)
                .asJsonObject().setCallback(new FutureCallback<JsonObject>() {
            @Override
            public void onCompleted(Exception e, JsonObject result) {
                Toast.makeText(context, result.toString(), Toast.LENGTH_LONG).show();

            }
        });
    }

}
