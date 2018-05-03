package com.envision.lstoicescu.sms_reader;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.envision.lstoicescu.sms_reader.dto.SmsPOJO;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    List<SmsPOJO> smsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        popList();
        registerClickCallback();
        populateListView();

    }

    private void popList() {
        smsList.add(new SmsPOJO("Laur", "Buna, ce faci?","26/08/1994"));
        smsList.add(new SmsPOJO("Andrei", "Inchide, te sun eu","26/06/2000"));
    }

    private void populateListView() {
        ArrayAdapter<SmsPOJO> adaptor = new MyListAdapter();
        ListView list = (ListView) findViewById(R.id.list);
        list.setAdapter(adaptor);
    }

    private void registerClickCallback() {
        ListView list = (ListView) findViewById(R.id.list);
//        list.setOnClickListener(); toata lista inregistreaza un click;
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View viewClicked, int position, long id) {
                Toast.makeText(MainActivity.this, "Heh, lol", Toast.LENGTH_LONG).show();
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

    private class MyListAdapter extends ArrayAdapter<SmsPOJO>{
        public MyListAdapter(){
            super(MainActivity.this, R.layout.sms_item, smsList);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent){
            View itemView = convertView;
            if(itemView == null){
                itemView = getLayoutInflater().inflate(R.layout.sms_item, parent, false);
            }
            // Find the sms
            SmsPOJO sms = smsList.get(position);

            // Fill the view
            TextView sender = (TextView) itemView.findViewById(R.id.item_sender);
            TextView message = (TextView) itemView.findViewById(R.id.item_messagePreview);
            TextView date = (TextView) itemView.findViewById(R.id.item_date);

            sender.setText(sms.getSender());
            message.setText(sms.getMessage());
            date.setText(sms.getDate());

            return itemView;
        }
    }
}
