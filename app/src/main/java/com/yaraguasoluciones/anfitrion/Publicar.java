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
                        Toast.makeText(context, "Cédula incorrecta", Toast.LENGTH_LONG).show();
                   }else if (result!=null){
                        Toast.makeText(context, "Imagen adjuntada a: " + result.get("afiliado_ap1") + ", " + result.get("afiliado_no1"), Toast.LENGTH_LONG).show();
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
                .setMultipartParameter("archivo[tipodocumento_id]", tipodocumento_id)
                .setMultipartParameter("archivo[afiliado_id]", afiliado_id)
                .setMultipartFile("archivo[ruta]", "image/jpeg", nombreArchivo)
                .asJsonObject();
    }
}
