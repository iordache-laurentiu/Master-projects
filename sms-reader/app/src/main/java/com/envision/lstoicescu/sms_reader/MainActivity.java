package com.envision.lstoicescu.sms_reader;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.envision.lstoicescu.sms_reader.controller.PermissionController;
import com.envision.lstoicescu.sms_reader.controller.SmsController;
import com.envision.lstoicescu.sms_reader.dto.SmsPOJO;
import com.envision.lstoicescu.sms_reader.utils.DialogBox;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        PermissionController.getInstance().readSmsPermission(this);
        SmsController.getInstance().populate(this);
        registerClickCallback();
        populateListView();
    }

    private void requestPermission() {
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.READ_SMS)
                    != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.READ_SMS)) {
                } else {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.READ_SMS},
                            1);
                }
            }
        }
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
                DialogBox.showAlertDialogBox(DialogBox.DialogType.EXIT, this);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    private class MyListAdapter extends ArrayAdapter<SmsPOJO>{
        public MyListAdapter(){
            super(MainActivity.this, R.layout.sms_item, SmsController.getInstance().getSmsList());
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent){
            View itemView = convertView;
            if(itemView == null){
                itemView = getLayoutInflater().inflate(R.layout.sms_item, parent, false);
            }
            // Find the sms
            SmsPOJO sms = SmsController.getInstance().getSmsList().get(position);

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
