package main.app.tbee3app;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.quickblox.chat.model.QBDialog;
import com.quickblox.core.QBEntityCallbackImpl;
import com.squareup.picasso.Picasso;

//import com.quickblox.sample.chat.pushnotifications.Consts;
//import com.quickblox.sample.chat.pushnotifications.PlayServicesHelper;
//import com.quickblox.sample.chat.ui.adapters.DialogsAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.blurry.Blurry;

public class DialogsActivity extends BaseActivity {

    private static final String TAG = DialogsActivity.class.getSimpleName();

    private ListView dialogsListView;
    private ProgressBar progressBar;

    private PlayServicesHelper playServicesHelper;
    final ArrayList<String> usernames = new ArrayList<String>();
    final ArrayList<String> userimage = new ArrayList<String>();
     ArrayList<QBDialog> dialogs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Settings.forceRTLIfSupported(this);
        setContentView(R.layout.dialogs_activity);
        try {
            getActionBar().hide();
        }
        catch (Exception ex){

        }

        try {
            getSupportActionBar().hide();
        }
        catch (Exception ex){

        }

        Log.e("in dailog","activity");
        playServicesHelper = new PlayServicesHelper(this);

        dialogsListView = (ListView) findViewById(R.id.roomsList);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);


        // Register to receive push notifications events
        //
        LocalBroadcastManager.getInstance(this).registerReceiver(mPushReceiver,
                new IntentFilter(Consts.NEW_PUSH_EVENT));

        // Get dialogs if the session is active
        //
        if(isSessionActive()){
            getDialogs();
        }
    }

    private void getDialogs(){
        progressBar.setVisibility(View.VISIBLE);

        // Get dialogs
        //
        ChatService.getInstance().getDialogs(new QBEntityCallbackImpl() {
            @Override
            public void onSuccess(Object object, Bundle bundle) {
                progressBar.setVisibility(View.GONE);

                dialogs = (ArrayList<QBDialog>)object;
                for(int i=0;i<dialogs.size();i++){
                            if(i==dialogs.size()-1)
                                get_user_details(dialogs.get(i).getRecipientId().toString(),true);
                            else
                                get_user_details(dialogs.get(i).getRecipientId().toString(),false);
                }



            }

            @Override
            public void onError(List errors) {
                progressBar.setVisibility(View.GONE);

                AlertDialog.Builder dialog = new AlertDialog.Builder(DialogsActivity.this);
                dialog.setMessage("get dialogs errors: " + errors).create().show();
            }
        });
    }


    void buildListView(List<QBDialog> dialogs){
        final DialogsAdapter adapter = new DialogsAdapter(dialogs, DialogsActivity.this,userimage);
        dialogsListView.setAdapter(adapter);

        // choose dialog
        //
        dialogsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                QBDialog selectedDialog = (QBDialog) adapter.getItem(position);

                Bundle bundle = new Bundle();
                bundle.putSerializable(ChatActivity.EXTRA_DIALOG, selectedDialog);

                // Open chat activity
                //
                ChatActivity.start(DialogsActivity.this, bundle);

                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        playServicesHelper.checkPlayServices();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return super.onOptionsItemSelected(item);
    }

    // Our handler for received Intents.
    //
    private BroadcastReceiver mPushReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            // Get extra data included in the Intent
            String message = intent.getStringExtra(Consts.EXTRA_MESSAGE);

            Log.i(TAG, "Receiving event " + Consts.NEW_PUSH_EVENT + " with data: " + message);
        }
    };


    //
    // ApplicationSessionStateCallback
    //

    @Override
    public void onStartSessionRecreation() {

    }

    @Override
    public void onFinishSessionRecreation(final boolean success) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (success) {
                    getDialogs();
                }
            }
        });
    }

    private void get_user_details(String cust_id, final boolean stop) {

        String url = Settings.SERVER_URL + "user_details.php?qb_id=" + cust_id;
        Log.e("get_user_details", url);
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject jsonArray) {
                Log.e("response is: ", jsonArray.toString());
                try {
                    JSONObject details_object = jsonArray.getJSONObject("details");
                   String name =  details_object.getString("name");
                    String image =  details_object.getString("image");
                    userimage.add(image);
                    Log.e("changed",name);
                    if(stop){
                     buildListView(dialogs);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO Auto-generated method stub
                Log.e("response is:", error.toString());

            }
        });

// Access the RequestQueue through your singleton class.
        AppController.getInstance().addToRequestQueue(jsObjRequest);
    }

}
