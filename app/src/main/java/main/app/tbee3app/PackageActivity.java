package main.app.tbee3app;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Chinni on 24-10-2015.
 */

public class PackageActivity extends Activity {
    TextView basic,free,pro,vip;
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;
    String mode = "open";
    public ArrayList<String> id,name,no_of_posts,no_of_images,gallery_option,pinned,social_media,instagram,featured,price_option,price;
    PackagelistAdapter packagelistAdapter;
    GridView pack_gridview;
    String user_package_id = "no_pack";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Settings.forceRTLIfSupported(this);
        setContentView(R.layout.packages_selection);
        name= new ArrayList<>();
        price= new ArrayList<>();
        id= new ArrayList<>();
        no_of_posts=new ArrayList<>();
        no_of_images=new ArrayList<>();
        gallery_option=new ArrayList<>();
        pinned=new ArrayList<>();
        social_media=new ArrayList<>();
        instagram=new ArrayList<>();
        featured=new ArrayList<>();
        price_option = new ArrayList<>();
        packagelistAdapter = new PackagelistAdapter(PackageActivity.this,PackageActivity.this,id,name,no_of_posts,no_of_images,
                gallery_option,pinned,social_media,instagram,featured,price_option,user_package_id,price);
        basic = (TextView) findViewById(R.id.basic_pc_btn);
        free = (TextView) findViewById(R.id.free_pc_btn);
        pro = (TextView) findViewById(R.id.pro_pc_btn);
        vip = (TextView) findViewById(R.id.vip_pc_btn);
        basic.setOnClickListener(new ClickListner());
        free.setOnClickListener(new ClickListner());
        pro.setOnClickListener(new ClickListner());
        vip.setOnClickListener(new ClickListner());
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String tbee3_pos = sharedPref.getString("tbee3_pos","-1");
        mode = getIntent().getStringExtra("mode");
        if(mode==null)
            mode="open";
        if(tbee3_pos.equals("-1")){

        }
        else{
            update_btns(Integer.parseInt(tbee3_pos));
        }
        editor = sharedPref.edit();
        get_list_of_packages();
        pack_gridview = (GridView) findViewById(R.id.packages_gridview);
        pack_gridview.setAdapter(packagelistAdapter);
    }

    public class ClickListner implements View.OnClickListener{
    int pos;
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.free_pc_btn:
                    //Toast.makeText(PackageActivity.this,"free",Toast.LENGTH_SHORT).show();
                    pos =1;
                    update_btns(1);
                    editor.putString("tbee3_pos", "1");
                    editor.commit();
                    break;
                case R.id.basic_pc_btn:
                   // Toast.makeText(PackageActivity.this,"basic",Toast.LENGTH_SHORT).show();
                    update_btns(2);
                    pos =2;
                    editor.putString("tbee3_pos","2");
                    editor.commit();
                    break;
                case R.id.pro_pc_btn:
                  //  Toast.makeText(PackageActivity.this,"pro",Toast.LENGTH_SHORT).show();
                    pos =3;
                    update_btns(3);
                    editor.putString("tbee3_pos","3");
                    editor.commit();

                    break;
                case R.id.vip_pc_btn:
                  //  Toast.makeText(PackageActivity.this,"free",Toast.LENGTH_SHORT).show();
                    pos =4;
                    update_btns(4);
                    editor.putString("tbee3_pos","4");
                    editor.commit();
                    break;
                default:
            }

        }
    }

    private void update_btns(int pos){
        free.setText("Buy Now");
        basic.setText("Buy Now");
        pro.setText("Buy Now");
        vip.setText("Buy Now");
        switch (pos){
            case 1:
                free.setText("Activated");
                break;
            case 2:
                basic.setText("Activated");
                break;
            case 3:
                pro.setText("Activated");
                break;
            case 4:
                vip.setText("Activated");
                break;
        }
    }

    @Override
    public void onBackPressed() {
        Log.d("CDA", "onBackPressed Called");

        if (mode.equals("close"))
        {
            Intent intent=new Intent();
            intent.putExtra("tbee3_pos", String.valueOf(5));
            setResult(2,intent);
            finish();//finishing activity

        }
        else
            super.onBackPressed();
            }


    public void start_payment_activity(String user_package_id,String user_package_price){

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String cust_id = sharedPref.getString("tbee3_user", "-1");
        Intent payment = new Intent(PackageActivity.this,PaymentActivity.class);
        payment.putExtra("cust_id",cust_id);
        payment.putExtra("pack_id",user_package_id);
        payment.putExtra("pack_price",user_package_price);
        startActivityForResult(payment, 7);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == 7) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                // The user picked a contact.
                // The Intent's data Uri identifies which contact was selected.
                // Do something with the contact here (bigger example below)
                if(data.getStringExtra("msg").equals("OK"))
                {
                    Toast.makeText(PackageActivity.this,"Subscribing to package",Toast.LENGTH_SHORT).show();
                    subscribe_to_package(data.getStringExtra("pack_id"));
                }
            }
        }
    }

    public void subscribe_to_package(final String pack_id){

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("please wait.....");
        progressDialog.show();
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String cust_id = sharedPref.getString("tbee3_user", "-1");
        String url = null;
        url = Settings.SERVER_URL+"package_subscribe.php?cust_id="+cust_id+"&package_id="+pack_id;
        Log.e("set_url", url);
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, url,null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject jsonArray) {
                Log.e("response is: ", jsonArray.toString());
                if(progressDialog!=null)
                    progressDialog.dismiss();
                try {

                    packagelistAdapter.selected_id = pack_id;
                    packagelistAdapter.notifyDataSetChanged();
                    packageselected(pack_id);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO Auto-generated method stub
                Log.e("response is:",error.toString());
                if(progressDialog!=null)
                    progressDialog.dismiss();
            }
        });

// Access the RequestQueue through your singleton class.
        AppController.getInstance().addToRequestQueue(jsObjRequest);
    }

    private void get_list_of_packages(){
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("please wait while we get list of packages...");
        progressDialog.setCancelable(true);
        progressDialog.show();
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        final String cust_id = sharedPref.getString("tbee3_user", "-1");
        String url = null;
        url = Settings.SERVER_URL+"packages.php";
        Log.e("set_url",url);
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, url,null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject jsonArray) {
                Log.e("response is: ", jsonArray.toString());
                if(progressDialog!=null)
                    progressDialog.dismiss();
                try {

                    JSONArray jsonArray1 = jsonArray.getJSONArray("packages");
                    for(int i=0;i<jsonArray1.length();i++){
                        name.add(jsonArray1.getJSONObject(i).getString("title"));
                        try {
                            price.add(jsonArray1.getJSONObject(i).getString("amount"));
                        }catch (Exception ex){
                            price.add("20");
                        }
                        no_of_posts.add(jsonArray1.getJSONObject(i).getString("no_posts"));
                        no_of_images.add(jsonArray1.getJSONObject(i).getString("no_pictures"));
                        gallery_option.add(jsonArray1.getJSONObject(i).getString("gallery"));
                        pinned.add(jsonArray1.getJSONObject(i).getString("pinned"));
                        social_media.add(jsonArray1.getJSONObject(i).getString("Bio_Location_Website_Social Media"));
                        instagram.add(jsonArray1.getJSONObject(i).getString("instagram_posts"));
                        featured.add(jsonArray1.getJSONObject(i).getString("featured"));
                        price_option.add(jsonArray1.getJSONObject(i).getString("price_option"));
                        id.add(jsonArray1.getJSONObject(i).getString("id"));
                        Log.e("pack names",jsonArray1.getJSONObject(i).getString("id"));
                    }
                    packagelistAdapter.notifyDataSetChanged();
                    get_user_package();
                }catch (Exception e){
                    e.printStackTrace();
                }


            }


        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO Auto-generated method stub
                Log.e("response is:",error.toString());
                if(progressDialog!=null)
                    progressDialog.dismiss();
            }
        });

// Access the RequestQueue through your singleton class.
        AppController.getInstance().addToRequestQueue(jsObjRequest);





    }


    private void get_user_package(){
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("please wait while we get your package detilas....");
        progressDialog.setCancelable(true);
        progressDialog.show();

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String cust_id = sharedPref.getString("tbee3_user", "-1");
        String url = null;
        url = Settings.SERVER_URL+"user_details.php?cust_id="+cust_id;
        Log.e("set_url",url);
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, url,null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject jsonArray) {
                Log.e("response is: ", jsonArray.toString());
                if(progressDialog!=null)
                progressDialog.dismiss();
                try {
                JSONObject jsonObject = jsonArray.getJSONObject("details");
                    user_package_id = jsonObject.getString("current_package");
                    packagelistAdapter.setUser_pack_id(user_package_id);
                    packagelistAdapter.notifyDataSetChanged();
                }catch (Exception e){
                    e.printStackTrace();
                }


            }


        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO Auto-generated method stub
                Log.e("response is:", error.toString());
                if(progressDialog!=null)
                progressDialog.dismiss();

            }
        });

// Access the RequestQueue through your singleton class.
        AppController.getInstance().addToRequestQueue(jsObjRequest);

    }


    public void packageselected(String selected_id){

        if(mode.equals("close")){
            Intent intent=new Intent();
            intent.putExtra("tbee3_pos", String.valueOf(selected_id));
            setResult(2,intent);
            finish();//finishing activity
        }
    }

}
