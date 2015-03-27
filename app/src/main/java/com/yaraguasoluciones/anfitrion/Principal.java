package com.yaraguasoluciones.anfitrion;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Random;


public class Principal extends ActionBarActivity {

    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1;
    private Uri fileUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);
        Button envio = (Button)findViewById(R.id.enviar);
        envio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                // start the image capture Intent
                startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);

            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_principal, menu);
        return true;
    }


    public String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri,  proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private String buscarRuta(Intent data){

        String realPath;
        // SDK < API11
        if (Build.VERSION.SDK_INT < 11)
            realPath = RealPathUtil.getRealPathFromURI_BelowAPI11(this, data.getData());

            // SDK >= 11 && SDK < 19
        else if (Build.VERSION.SDK_INT < 19)
            realPath = RealPathUtil.getRealPathFromURI_API11to18(this, data.getData());

            // SDK > 19 (Android 4.4)
        else
            realPath = RealPathUtil.getRealPathFromURI_API19(this, data.getData());
        return realPath;
    }


    private File guardar(Bitmap bm){
        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/req_images");
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data!=null) {


            /*
            http://stackoverflow.com/questions/5570343/android-compatible-library-for-creating-image-from-pdf-file
             */
            //String ruta = buscarRuta(data);

            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            ImageView mostrar = (ImageView)findViewById(R.id.foto);
            mostrar.setImageBitmap(imageBitmap);
            File archivo = guardar(imageBitmap);



            Publicar publicar = new Publicar();

            Toast.makeText(Principal.this, data.getType(), Toast.LENGTH_LONG).show();


            //publicar.obtenerCodigo(this.getApplicationContext(), data);

            publicar.enviarArchivo(this.getApplicationContext(), archivo);





        }


    /*
    http://stackoverflow.com/questions/3642928/adding-a-library-jar-to-an-eclipse-android-project
    http:/www.stackoverflow.com/questions/8248196/how-to-add-a-library-project-to-a-android-project

    Revisar detalle en versiones de android:
    http://hmkcode.com/android-display-selected-image-and-its-real-path/

    http://stackoverflow.com/questions/10288385/android-image-picker-wrong-image
    http://stackoverflow.com/questions/2975197/convert-file-uri-to-file-in-android

    */
        String imageUrl;

        if (data!=null){
            int currentPage = 0 ;



            PdfRenderer renderer = null;




            /*
            http://stackoverflow.com/questions/4886042/pdf-to-image-using-java
            PDFDocument document = new PDFDocument();

            try {
                document.load(new File(data.getDataString()));
                SimpleRenderer renderer = new SimpleRenderer();

                // set resolution (in DPI)
                renderer.setResolution(300);


            } catch (IOException e) {
                e.printStackTrace();
            }
            */


            Toast.makeText(Principal.this, data.getDataString(), Toast.LENGTH_LONG).show();
        }
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
