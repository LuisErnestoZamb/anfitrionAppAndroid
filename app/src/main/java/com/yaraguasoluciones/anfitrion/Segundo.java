package com.yaraguasoluciones.anfitrion;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;


public class Segundo extends ActionBarActivity {

    private Context contexto = null;
    private File archivo = null;
    private EditText cedula = null;
    private static int TAKE_PICTURE = 1;
    private Uri imageUri;

    private static final String PREFS_NAME = "rutaImagen";
    private String db = "db";
    private String notasindividuales = "notasindividuales";

    private void anadiendoValor(String valor){
        // Saving in SharedPreferences -  putStringSet
        SharedPreferences ss = getSharedPreferences(db, 0);
        Set<String> hs = ss.getStringSet(notasindividuales, new HashSet<String>());
        // Adding value from TextEdit
        hs.add(valor);
        SharedPreferences.Editor edit = ss.edit();
        edit.clear();
        edit.putStringSet(notasindividuales, hs);
        edit.commit();
    }

    private String crearJson(String ruta, String tipo, String nac, String cedula, boolean enviado){
        JSONObject object = new JSONObject();
        try {
            object.put("ruta", ruta);
            object.put("tipo", tipo);
            object.put("nac", nac);
            object.put("cedula", cedula);
            object.put("enviado", enviado);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object.toString();
    }


    private void guardarEstado(String pathFile, boolean camara) {
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("rutaImagen", pathFile);
        editor.putBoolean("valorCamara", camara);
        // Commit the edits!
        editor.commit();
    }

    private String valorAlmacenado() {
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        return settings.getString("rutaImagen", null);
    }

    private boolean setCamaraActivada() {
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        return settings.getBoolean("valorCamara", true);
    }

    private boolean setCamaraDesactivar() {
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        return settings.getBoolean("valorCamara", false);
    }


    private boolean camaraActiva() {
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        return settings.getBoolean("valorCamara", false);
    }

    private void cargarImagen() {
        Uri selectedImage = imageUri;
        getContentResolver().notifyChange(selectedImage, null);
        ImageView imageView = (ImageView) findViewById(R.id.fotoTomada);
        ContentResolver cr = getContentResolver();
        Bitmap bitmap;
        try {
            bitmap = android.provider.MediaStore.Images.Media.getBitmap(cr, selectedImage);

            // Acá reducimos el tamaño de la imagen.
            Bitmap reducido = bitmap.createScaledBitmap(bitmap, bitmap.getWidth() / 2, bitmap.getHeight() / 2, false);
            imageView.setImageBitmap(reducido);
            //Toast.makeText(this, selectedImage.toString(), Toast.LENGTH_LONG).show();

        } catch (Exception e) {
            Log.w("Camera", e.toString());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_segundo);
        String rutaArchivo = valorAlmacenado();

        if (rutaArchivo != null) {
            archivo = new File(rutaArchivo);
            imageUri = Uri.fromFile(archivo);
            cargarImagen();
            setCamaraActivada();
        }
        if (!camaraActiva()) {
            takePhoto();
        }
        Button cancelar = (Button) findViewById(R.id.cambiarFoto);
        cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cedula = (EditText) findViewById(R.id.cedula);
                cedula.setText("");
                ImageView mostrar = (ImageView) findViewById(R.id.fotoTomada);
                mostrar.setImageBitmap(null);
                guardarEstado(null, true);
                takePhoto();
            }
        });


        Button envio = (Button) findViewById(R.id.enviar);
        envio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog dialog = new AlertDialog.Builder(v.getContext())
                        .setTitle("Enviar imagen")
                        .setMessage("Desea continuar con el envío.")
                        .setCancelable(true)
                        .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (dialog != null) {
                                    Publicar publicar = new Publicar(contexto);
                                    cedula = (EditText) findViewById(R.id.cedula);

                                    anadiendoValor(crearJson(archivo.getAbsolutePath(), "1", "V", cedula.getText().toString(), false));
                                    // Muestra los mensajes pero no transmite la data.
                                    // Por tal razón el valor es true en la útima columna.
                                    publicar.obtenerCodigo(archivo, cedula.getText().toString(), "V", "1", true);
                                    dialog.dismiss();
                                    dialog = null;
                                    cedula.setText("");
                                    ImageView mostrar = (ImageView) findViewById(R.id.fotoTomada);
                                    mostrar.setImageBitmap(null);
                                    guardarEstado(null, false);
                                    finish();
                                }
                            }
                        })
                        .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
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

            }


        });

    }



    public void takePhoto() {
        // Generando la ruta de guardado

        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/Anfitrion");
        myDir.mkdirs();
        Random generator = new Random();
        int n = 10000;
        n = generator.nextInt(n);
        String fname = "Image-" + n + ".jpg";

        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        archivo = new File(myDir, fname);
        intent.putExtra(MediaStore.EXTRA_OUTPUT,
                Uri.fromFile(archivo));
        imageUri = Uri.fromFile(archivo);
        startActivityForResult(intent, TAKE_PICTURE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        contexto = this.getApplicationContext();

        if (resultCode == Activity.RESULT_OK) {
            cargarImagen();
            guardarEstado(archivo.getAbsolutePath(), true);
        }else{
            this.finish();
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_segundo, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
