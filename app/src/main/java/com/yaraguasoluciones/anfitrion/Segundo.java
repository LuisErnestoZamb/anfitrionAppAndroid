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
import android.widget.Toast;

import java.io.File;
import java.util.Random;


public class Segundo extends ActionBarActivity {

    private Context contexto = null;
    private File archivo = null;
    private EditText cedula = null;
    private static int TAKE_PICTURE = 1;
    private Uri imageUri;

    public static final String PREFS_NAME = "rutaImagen";


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
            Toast.makeText(this, selectedImage.toString(), Toast.LENGTH_LONG).show();

        } catch (Exception e) {
            Log.e("Camera", e.toString());
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
                if (contexto != null) {


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
                                        publicar.obtenerCodigo(archivo, cedula.getText().toString(), "V", "1");
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
