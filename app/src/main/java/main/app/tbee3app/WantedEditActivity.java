package main.app.tbee3app;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by sriven on 12/11/2015.
 */
public class WantedEditActivity extends Activity {
    EditText tittle, desc, price_from, price_to;
    String category;
    LinearLayout select_category_layout;
    TextView select_cat_lable;
    String selected_category_id;
    Button send_btn;
    SharedPreferences sharedPreferences;
    String cust_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Settings.forceRTLIfSupported(this);
        setContentView(R.layout.edit_wanted_product_details_layout);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        cust_id = sharedPreferences.getString("tbee3_user", "-1");
        tittle = (EditText) findViewById(R.id.post_tittle);
        desc = (EditText) findViewById(R.id.post_desc);
        price_from = (EditText) findViewById(R.id.post_price_from);
        price_to = (EditText) findViewById(R.id.post_price_to);
        select_category_layout = (LinearLayout) findViewById(R.id.select_category_layout);
        select_cat_lable = (TextView) findViewById(R.id.select_category);
        select_category_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(WantedEditActivity.this, CategorySelectActivity.class);
                startActivityForResult(intent, 7);
            }
        });
        send_btn  = (Button) findViewById(R.id.add_submit_btn);
        send_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    submit_wanted_post();
            }
        });

        tittle.setText(getIntent().getStringExtra("tittle"));
        desc.setText(getIntent().getStringExtra("desc"));
        price_from.setText(getIntent().getStringExtra("price_from"));
        price_to.setText(getIntent().getStringExtra("price_to"));

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // check if the request code is same as what is passed  here it is 2
        if (requestCode == 7 && resultCode != 0) {

            select_cat_lable.setText(data.getStringExtra("cat_name"));
            selected_category_id = data.getStringExtra("cat_id");

        }
    }

    private  void submit_wanted_post(){
        final ProgressDialog progressDialog = new ProgressDialog(WantedEditActivity.this);
        progressDialog.setMessage("uploading product details...");
        progressDialog.show();
        String url = "";
                try {
            url = Settings.SERVER_URL + "edit-wanted.php?category=" + selected_category_id +
                    "&title=" + URLEncoder.encode(tittle.getText().toString(), "UTF-8") +
                    "&key_words=" + URLEncoder.encode(desc.getText().toString(), "UTF-8") +
                    "&price_from=" + URLEncoder.encode(price_from.getText().toString(), "UTF-8") +
                    "&price_to=" + URLEncoder.encode(price_to.getText().toString(), "UTF-8") +
                    "&cust_id=" + cust_id +
                    "&wanted_id=" + getIntent().getStringExtra("wanted_id");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        Log.e("url;;;;", url);
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject jsonObject) {
                if (progressDialog != null)
                    progressDialog.dismiss();
                Log.e("response is", jsonObject.toString());
                try {
                    Log.e("response is", jsonObject.getString("response"));
                    JSONArray jsonArray = jsonObject.getJSONArray("response");
                    JSONObject jsonObject1 = jsonArray.getJSONObject(0);
                    String response_json = jsonObject1.optString("message");
                    if (response_json.equals("Inserted Successfully")) {
                         Toast.makeText(WantedEditActivity.this, "Your add was posted successfullt", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO Auto-generated method stub
                Log.e("error response is:", error.toString());
                if (progressDialog != null)
                    progressDialog.dismiss();
            }
        });

        AppController.getInstance().addToRequestQueue(jsObjRequest);


    }


}
