package com.envision.lstoicescu.sms_reader;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String[] test = {"Message 1", "Message 2", "Message 3"};
        popList();
        registerClickCallback();

    }

    private void popList() {
        String[] msg = {"Message 1", "Message 2", "Message 3"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this,        // context
                R.layout.sms_item,  // layout to use (create)
                msg                 // Items to be displayed
        );
        ListView list = (ListView) findViewById(R.id.list);
        list.setAdapter(adapter);
    }


    private void registerClickCallback() {
        ListView list = (ListView) findViewById(R.id.list);
//        list.setOnClickListener(); toata lista inregistreaza un click;
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View viewClicked, int position, long id) {
                TextView textView = (TextView) viewClicked;
                String msg = "You clicked position " + position + " which is string: " + textView.getText().toString();
                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_LONG).show();
            }
        }); // listener pentru elementele listei
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the main_menu;
        // this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) { // TODO: Remove, just educational purpose
        switch (item.getItemId()) {
            case R.id.item1:
                Toast.makeText(getApplicationContext(), "S-a apăsat item1", Toast.LENGTH_LONG).show();
                showAlertDialogBox(1);
                break;
            case R.id.item2:
                Toast.makeText(getApplicationContext(), "S-a apăsat item1", Toast.LENGTH_LONG).show();
                break;
            case R.id.item3:
                Toast.makeText(getApplicationContext(), "S-a apăsat item1", Toast.LENGTH_LONG).show();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    public void showAlertDialogBox(long id) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setMessage(getString(R.string.DIALOG_POSITIVE_RESPONSE));
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                getString(R.string.DIALOG_POSITIVE_RESPONSE),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // enter in message TODO: Can move it in caller method
                        // read message
                        dialog.cancel();
                    }
                });

        builder1.setNegativeButton(
                getString(R.string.DIALOG_NEGATIVE_RESPONSE),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // enter in message
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }
}
