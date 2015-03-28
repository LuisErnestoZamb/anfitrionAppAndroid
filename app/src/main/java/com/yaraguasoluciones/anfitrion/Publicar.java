package com.yaraguasoluciones.anfitrion;

import android.content.Context;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.io.File;

/**
 * Created by luiszambrano on 26/03/15.
 */
public class Publicar {

    public void obtenerCodigo(final Context context, final File nombreArchivo, final String cedula, final String nac) {
    /*
    http://0.0.0.0:3000/archivos/mostrarafiliado.json?nac=V&cedula=1
    */
        try{
            Ion.with(context)
                    .load("http://inscripciones.cnpven.org/archivos/mostrarafiliado.json")
                    .setMultipartParameter("nac", "V")
                    .setMultipartParameter("cedula", cedula)
                    .asJsonObject().setCallback(new FutureCallback<JsonObject>() {
                @Override
                public void onCompleted(Exception e, JsonObject result) {
                    if (e!=null){
                        Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show();
                    }else if (result!=null){
                        String afiliado_id = "";
                        enviarArchivo(context, nombreArchivo, afiliado_id, nac);
                        Toast.makeText(context, result.toString(), Toast.LENGTH_LONG).show();
                    }
                }
            });

        }catch (Exception e){
            Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    public void enviarArchivo(final Context context, File nombreArchivo, String afiliado_id, String tipodocumento_id) {

        String url = "http://inscripciones.cnpven.org/archivos";

        Toast.makeText(context, nombreArchivo.getAbsolutePath().toString(), Toast.LENGTH_LONG).show();

        Ion.with(context)
                .load(url)
                //.uploadProgressBar(uploadProgressBar)
                .setMultipartParameter("archivo[tipodocumento_id]", tipodocumento_id)
                .setMultipartParameter("archivo[afiliado_id]", afiliado_id)
                .setMultipartFile("archivo[ruta]", "image/jpeg", nombreArchivo)
                .asJsonObject();
    }
}
