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
import com.envision.lstoicescu.sms_reader.utils.PermissionHandler;
import com.envision.lstoicescu.sms_reader.controller.SmsController;
import com.envision.lstoicescu.sms_reader.utils.TextToSpeechHandler;
import com.envision.lstoicescu.sms_reader.dto.SmsPOJO;
import com.envision.lstoicescu.sms_reader.utils.DialogBox;

import static com.envision.lstoicescu.sms_reader.utils.DialogBox.DialogType.*;


public class MainActivity extends AppCompatActivity {


    @Override // This will run every time when the application is started
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        PermissionHandler.getInstance().readSmsPermission(this); // Ask the user's permission to read sms list
        SmsController.getInstance().populate(this);                 // Create sms list with SmsPOJO entities
        populateListView();                                                // Populate the main list form mainActivity
        addOnListItemClickedEvent();                                       // Create listItem event for click
        TextToSpeechHandler.getInstance().createtts(this);       // Initialize TextToSpeech entity
    }

    @Override
    // This will run every time when the application is paused (by paused it means that the application still run in background, is not closed)
    protected void onPause() {
        TextToSpeechHandler.getInstance().pauseTTS();                   // Pause the speech process
        SmsController.getInstance().dropList();                            // Drop the list of messages from memory
        super.onPause();
    }

    @Override // This will run every time when the application is resumed
    protected void onResume() {
        super.onResume();
        SmsController.getInstance().populate(this);                 // Create sms list with SmsPOJO entities
    }

    //--------------------- Menu ---------------------
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);                 // Sets the menu
        return true;
    }

    @Override // This will execute when an item from the menu is selected
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item1:
                // Display a dialog box that let the user decide to close de application
                DialogBox.showAlertDialogBox(EXIT, this, null);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    //--------------------- List ---------------------

    /**
     * This will attach the sms list to the list view
     */
    private void populateListView() {
        ArrayAdapter<SmsPOJO> adaptor = new MyListAdapter();
        ListView list = (ListView) findViewById(R.id.list);
        list.setAdapter(adaptor);
    }

    /**
     * Will perform certain action when an item from list view is clicked
     */
    private void addOnListItemClickedEvent() {
        ListView list = (ListView) findViewById(R.id.list);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View viewClicked, int position, long id) {
                SmsPOJO sms = SmsController.getInstance().getSmsList().get(position); // get the clicked sms
                // This will perform the dialog from which the user can select the option to read the message with TTS
                DialogBox.showAlertDialogBox(MESSAGE_CLICKED, MainActivity.this, sms);
            }
        });
    }

    /**
     * Private inner class used to populate the list view item.
     */
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
