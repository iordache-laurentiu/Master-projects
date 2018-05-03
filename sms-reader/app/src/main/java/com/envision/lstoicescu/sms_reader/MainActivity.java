package com.envision.lstoicescu.sms_reader;

import android.Manifest;
import android.content.pm.PackageManager;
import android.speech.tts.TextToSpeech;
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

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        PermissionController.getInstance().readSmsPermission(this);
        SmsController.getInstance().populate(this);
        addOnListItemClickedEvent();
        populateListView();
        createtts();
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
                DialogBox.showAlertDialogBox(DialogBox.DialogType.EXIT, this);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    private void populateListView() {
        ArrayAdapter<SmsPOJO> adaptor = new MyListAdapter();
        ListView list = (ListView) findViewById(R.id.list);
        list.setAdapter(adaptor);
    }

    //--------------------- List ---------------------

    private void addOnListItemClickedEvent() {
        ListView list = (ListView) findViewById(R.id.list);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View viewClicked, int position, long id) {
                SmsPOJO sms = SmsController.getInstance().getSmsList().get(position);
                Toast.makeText(MainActivity.this, sms.getMessage(), Toast.LENGTH_LONG).show();
                ConvertTextToSpeech(sms.getMessage());

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

    //

    TextToSpeech tts;

    protected void createtts() {

        tts = new TextToSpeech(MainActivity.this, new TextToSpeech.OnInitListener() {

            @Override
            public void onInit(int status) {
                // TODO Auto-generated method stub
                if (status == TextToSpeech.SUCCESS) {
                    int result = tts.setLanguage(Locale.US);
                    if (result == TextToSpeech.LANG_MISSING_DATA ||
                            result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e("error", "This Language is not supported");
                    } else {
                        ConvertTextToSpeech("Fuck off");
                    }
                } else
                    Log.e("error", "Initilization Failed!");
            }
        });


    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onPause();
    }


    private void ConvertTextToSpeech(String text) {
        // TODO Auto-generated method stub
        if (text == null || "".equals(text)) {
            text = "Content not available";
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
        } else {
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
            Log.d("PLM", "De ce nu merge>>???");
        }
    }
}
