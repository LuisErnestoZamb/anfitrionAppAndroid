package com.yaraguasoluciones.anfitrion;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.ghost4j.document.PDFDocument;

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
                //intent.setDataAndType(Uri.parse("/"), "application/pdf");
                intent.setType("application/pdf");
                //intent.setType("file/*.jpg");
                startActivityForResult(intent, YOUR_RESULT_CODE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        PDFDocument document = new PDFDocument();
        try {
            document.load(new File("input.pdf"));
        } catch (IOException e) {
            e.printStackTrace();
        }


        if (data!=null){
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
