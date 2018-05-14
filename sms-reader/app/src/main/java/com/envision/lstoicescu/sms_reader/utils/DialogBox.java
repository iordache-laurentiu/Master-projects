package com.envision.lstoicescu.sms_reader.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.envision.lstoicescu.sms_reader.R;
import com.envision.lstoicescu.sms_reader.dto.SmsPOJO;

/**
 * Created by lstoicescu on 5/3/2018.
 */

public class DialogBox {

    public enum DialogType {
        MESSAGE_CLICKED,
        EXIT
    }

    private DialogBox() {
    }

    public static void showAlertDialogBox(final DialogType dialogType, Context context, Object niceToHave) {
        final SmsPOJO sms = (niceToHave!=null)? (SmsPOJO)niceToHave : null;
        String dialogMessage;
        String dialogPositiveResponse = context.getString(R.string.DIALOG_POSITIVE_RESPONSE);
        String dialogNegativeResponse = context.getString(R.string.DIALOG_NEGATIVE_RESPONSE);

        switch (dialogType) {
            case MESSAGE_CLICKED:
                dialogMessage = context.getString(R.string.DIALOG_MESSAGE_SMS_CLICKED);
                break;
            case EXIT:
                dialogMessage = context.getString(R.string.DIALOG_MESSAGE_EXIT);
                break;
            default:
                dialogMessage = context.getString(R.string.DIALOG_ERROR);
        }

        AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
        builder1.setMessage(dialogMessage);
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                dialogPositiveResponse,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        switch (dialogType) {
                            case MESSAGE_CLICKED:
                                TextToSpeechHandler.getInstance().convertTextToSpeech(sms.getMessage());
                                break;
                            case EXIT:
                                System.exit(0);
                                break;
                            default:
                                break;
                        }
                        dialog.cancel();
                    }
                });

        builder1.setNegativeButton(
                dialogNegativeResponse,
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
