package com.yaraguasoluciones.anfitrion;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import java.io.FileOutputStream;
import java.util.Random;


public class Segundo extends ActionBarActivity {

    private Context contexto = null;
    private File archivo = null;
    private EditText cedula = null;
    private Intent intent = null;

    private SharedPreferences mPrefs;
    private String mCurViewMode;

    private File guardar(Bitmap bm){
        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/Anfitrion");
        myDir.mkdirs();
        Random generator = new Random();
        int n = 10000;
        n = generator.nextInt(n);
        String fname = "Image-" + n + ".jpg";
        File file = new File(myDir, fname);

        if (file.exists())
            file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            bm.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_segundo);

        takePhoto();
        mPrefs = getSharedPreferences("rutaImagen", MODE_PRIVATE);

/*

        intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, 1);
 */




        Button envio = (Button)findViewById(R.id.enviar);
        envio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (contexto!=null){

                    AlertDialog dialog = new AlertDialog.Builder(v.getContext())
                            .setTitle("Enviar imagen")
                            .setMessage("Desea continuar con el envío.")
                            .setCancelable(true)
                            .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (dialog!=null){
                                        Publicar publicar = new Publicar();
                                        cedula = (EditText)findViewById(R.id.cedula);
                                        publicar.obtenerCodigo(contexto, archivo, cedula.getText().toString(), "V", "1");
                                        dialog.dismiss();
                                        dialog = null;
                                        cedula.setText("");
                                        ImageView mostrar = (ImageView)findViewById(R.id.fotoTomada);
                                        mostrar.setImageBitmap(null);
                                        finish();
                                        mCurViewMode = mPrefs.getString("rutaImagen", null);

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


    private static int TAKE_PICTURE = 1;
    private Uri imageUri;

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
        File photo = new File(myDir, fname);
        intent.putExtra(MediaStore.EXTRA_OUTPUT,
                Uri.fromFile(photo));
        imageUri = Uri.fromFile(photo);
        startActivityForResult(intent, TAKE_PICTURE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        contexto = this.getApplicationContext();



        if (resultCode == Activity.RESULT_OK) {
            Uri selectedImage = imageUri;
            getContentResolver().notifyChange(selectedImage, null);
            ImageView imageView = (ImageView) findViewById(R.id.fotoTomada);
            ContentResolver cr = getContentResolver();
            Bitmap bitmap;
            try {
                bitmap = android.provider.MediaStore.Images.Media
                        .getBitmap(cr, selectedImage);


                bitmap.createScaledBitmap(selectedImage, )

                imageView.setImageBitmap(bitmap);
                Toast.makeText(this, selectedImage.toString(),
                        Toast.LENGTH_LONG).show();





            } catch (Exception e) {
                Toast.makeText(this, "Failed to load", Toast.LENGTH_SHORT)
                        .show();
                Log.e("Camera", e.toString());
            }
        }

        if(resultCode == RESULT_OK) {





                    /*

            //file path of captured image
            String filePath = cursor.getString(columnIndex);


            //file path of captured image
            File f = new File(filePath);
            String filename = f.getName();

            Toast.makeText(contexto, "Your Path:" + filePath, Toast.LENGTH_LONG).show();
            Toast.makeText(contexto, "Your Filename:" + filename, Toast.LENGTH_LONG).show();
            cursor.close();



            mCurViewMode = mPrefs.getString("rutaImagen", filePath);

            */

        }






        if (resultCode == RESULT_OK) {
            //Bundle extras = data.getExtras();
            //Bitmap imageBitmap = (Bitmap) extras.get("data");
            //ImageView mostrar = (ImageView)findViewById(R.id.fotoTomada);
            //mostrar.setImageBitmap(imageBitmap);
            //archivo = guardar(imageBitmap);
        }else{
            this.finish();
        }

        super.onActivityResult(requestCode, resultCode, data);

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
