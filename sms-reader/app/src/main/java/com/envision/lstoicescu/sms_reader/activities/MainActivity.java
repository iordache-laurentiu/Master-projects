package com.envision.lstoicescu.sms_reader.activities;

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

import com.envision.lstoicescu.sms_reader.R;
import com.envision.lstoicescu.sms_reader.controller.PermissionController;
import com.envision.lstoicescu.sms_reader.controller.SmsController;
import com.envision.lstoicescu.sms_reader.controller.TextToSpeechController;
import com.envision.lstoicescu.sms_reader.dto.SmsPOJO;
import com.envision.lstoicescu.sms_reader.utils.DialogBox;


public class MainActivity extends AppCompatActivity {


    @Override // This will run every time when the application is started
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        PermissionController.getInstance().readSmsPermission(this); // Ask the user's permission to read sms list
        SmsController.getInstance().populate(this);                 // Create sms list with SmsPOJO entities
        populateListView();                                                // Populate the main list form mainActivity
        addOnListItemClickedEvent();                                       // Create listItem event for click
        TextToSpeechController.getInstance().createtts(this);       // Initialize TextToSpeech entity
    }

    @Override
    // This will run every time when the application is paused (by paused it means that the application still run in background, is not closed)
    protected void onPause() {
        TextToSpeechController.getInstance().pauseTTS();                   // Pause the speech process
        super.onPause();
    }

    //--------------------- Menu ---------------------
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
                DialogBox.showAlertDialogBox(DialogBox.DialogType.EXIT, this, null);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    //--------------------- List ---------------------

    private void populateListView() {
        ArrayAdapter<SmsPOJO> adaptor = new MyListAdapter();
        ListView list = (ListView) findViewById(R.id.list);
        list.setAdapter(adaptor);
    }

    private void addOnListItemClickedEvent() {
        ListView list = (ListView) findViewById(R.id.list);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View viewClicked, int position, long id) {
                SmsPOJO sms = SmsController.getInstance().getSmsList().get(position);
                DialogBox.showAlertDialogBox(DialogBox.DialogType.MESSAGE_CLICKED, MainActivity.this, sms);
            }
        });
    }

    private class MyListAdapter extends ArrayAdapter<SmsPOJO> {
        public MyListAdapter() {
            super(MainActivity.this, R.layout.sms_item, SmsController.getInstance().getSmsList());
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View itemView = convertView;
            if (itemView == null) {
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
