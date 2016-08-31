package main.app.tbee3app;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import com.quickblox.chat.QBChatService;
import com.quickblox.core.QBEntityCallbackImpl;
import com.quickblox.users.model.QBUser;

import java.util.List;

/**
 * Created by Chinni on 19-11-2015.
 */

public class ChatStartActivity  extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Settings.forceRTLIfSupported(this);
        setContentView(R.layout.chat_activity);
        try {
            getActionBar().hide();
        }
        catch (Exception ex){

        }
        final QBUser user = new QBUser();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String userid = sharedPreferences.getString("tbee3_qbid","-1");
        user.setLogin(userid);
        user.setPassword("12345678");

        ChatService.initIfNeed(this);
        ChatService.getInstance().logout();
        ChatService.getInstance().login(user, new QBEntityCallbackImpl() {

            @Override
            public void onSuccess() {
                Log.e("success", "chat_service");
                // Go to Dialogs screen
                //
                if (getIntent().getStringExtra("mode").equals("self")) {
                    Intent intent = new Intent(ChatStartActivity.this, DialogsActivity.class);
                    intent.putExtra("opponentid", getIntent().getStringExtra("opponentid"));
                    startActivity(intent);
                    finish();

                } else {
                    Intent intent = new Intent(ChatStartActivity.this, ChatActivity.class);
                    intent.putExtra("opponentid", getIntent().getStringExtra("opponentid"));
                    intent.putExtra("mode", "other");
                    startActivity(intent);
                    finish();

                }
            }

            @Override
            public void onError(List errors) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(ChatStartActivity.this);
                dialog.setMessage("chat login errors: " + errors).create().show();
            }
        });
    }
}


