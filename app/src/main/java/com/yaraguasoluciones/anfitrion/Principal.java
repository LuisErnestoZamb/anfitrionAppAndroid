package com.yaraguasoluciones.anfitrion;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;


public class Principal extends ActionBarActivity {

    private int YOUR_RESULT_CODE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_principal, menu);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        Button envio = (Button)findViewById(R.id.enviar);
        envio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("application/pdf");
                startActivityForResult(intent, YOUR_RESULT_CODE);
            }
        });
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    /*
    http://stackoverflow.com/questions/3642928/adding-a-library-jar-to-an-eclipse-android-project
    http:/www.stackoverflow.com/questions/8248196/how-to-add-a-library-project-to-a-android-project

    Revisar detalle en versiones de android:
    http://hmkcode.com/android-display-selected-image-and-its-real-path/

    http://stackoverflow.com/questions/10288385/android-image-picker-wrong-image
    http://stackoverflow.com/questions/2975197/convert-file-uri-to-file-in-android

    */

        if (data!=null){
            int currentPage = 0 ;



            Uri selectedImageUri = Uri.parse(data.getDataString());

//            File file = new File(selectedImageUri.toString());
            File file = new File("/sdcard/Download/22_Menen.pdf");

            PdfRenderer renderer = null;
            try {

                /*
                https://developer.android.com/reference/android/graphics/pdf/PdfRenderer.html
                http://stackoverflow.com/questions/6715898/what-is-parcelfiledescriptor-in-android
                https://github.com/digipost/Android-Pdf
                 */

                renderer = new PdfRenderer(ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY));
            } catch (IOException e) {
                e.printStackTrace();
            }


            if(currentPage > renderer.getPageCount()){
                currentPage = renderer.getPageCount()-1;
            }
            //renderer.openPage(currentPage).render(bitmap, new Rect(0, 0, 500, 500), new Matrix(), PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);


            final int pageCount = renderer.getPageCount();
            Bitmap mBitmap = null;

            for (int i = 0; i < pageCount; i++) {
                PdfRenderer.Page page = renderer.openPage(i);

                // say we render for showing on the screen
                //imageView = (ImageView) findViewById(R.id.imageView);
                Bitmap bitmap = Bitmap.createBitmap(500, 500, Bitmap.Config.ARGB_4444);
                page.render(mBitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);

                // do stuff with the bitmap

                // close the page
                page.close();
            }

            // close the renderer
            renderer.close();



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
