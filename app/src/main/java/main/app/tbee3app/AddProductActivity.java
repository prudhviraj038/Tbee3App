package main.app.tbee3app;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.InputType;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.tagmanager.PreviewActivity;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Chinni on 15-10-2015.
 */
public class AddProductActivity extends Activity {
    RadioButton fixed_price,offer_price,bid_price;
    EditText tittle, desc, price;
    int cat_seleced_pos;
    Spinner category, sub_category, post_condition;
    ArrayList<String> condition_values,condition_values_tosend;
    ArrayList<String> category_names, category_ids;
    ArrayList<String> sub_category_names, sub_category_ids;
    ArrayList<String> category_names_atr, category_ids_atr, category_types_atr;
    ArrayList<ArrayList<String>> category_types_values;
    String selected_category_id, selected_sub_category_id;
    List<EditText> allEds = new ArrayList<EditText>();
    List<Spinner> allSpinners = new ArrayList<Spinner>();
    LinearLayout attributes_layout;
    int current, total;
    Integer add_id;
    ImageView display1, display2, display3, display4;
    private ArrayList<String> image_path;
    Button submit_btn;
    ProgressDialog progressDialognew, progressDialogupload;
    long totalSize = 0;
    ProgressDialog prgDialog;
    ArrayList<String> encodedString, encodedExt;
    RequestParams params = new RequestParams();
    String imgPath, fileName;
    Bitmap bitmap;
    SharedPreferences sharedPreferences;
    String cust_id;
    String scrren_mode;
    int uploaded_images = 0;
    int total_images = 0;
    LinearLayout select_category_layout;
    TextView select_cat_lable;
    Boolean isCatselected = false;
    TextView extra;
    String price_mode = "0";

    //   String files[];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Settings.forceRTLIfSupported(this);
        setContentView(R.layout.add_product_details_layout);
        //  files = getIntent().getExtras().getStringArray("files");
        Button preview_btn = (Button) findViewById(R.id.preview_btn);
        preview_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validate_form(false);
            }
        });
        encodedString = new ArrayList<>();
        encodedExt = new ArrayList<>();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        cust_id = sharedPreferences.getString("tbee3_user", "-1");
        tittle = (EditText) findViewById(R.id.post_tittle);
        desc = (EditText) findViewById(R.id.post_desc);
        price = (EditText) findViewById(R.id.post_price);
        extra = (TextView) findViewById(R.id.extra_attributes);
        fixed_price = (RadioButton) findViewById(R.id.fixed_price);
        offer_price = (RadioButton) findViewById(R.id.offer_price);
        bid_price = (RadioButton) findViewById(R.id.bid_price);
        fixed_price.setChecked(true);
        fixed_price.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (fixed_price.isChecked()) {
                    offer_price.setChecked(false);
                    bid_price.setChecked(false);
                    price_mode = "0";
                }

            }
        });
        offer_price.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(offer_price.isChecked()){
                    fixed_price.setChecked(false);
                    bid_price.setChecked(false);
                    price_mode = "1";
                }
            }
        });
        bid_price.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(bid_price.isChecked()){
                    offer_price.setChecked(false);
                    fixed_price.setChecked(false);
                    price_mode = "2";
                }
            }
        });

        scrren_mode = getIntent().getStringExtra("mode");
        select_category_layout = (LinearLayout) findViewById(R.id.select_category_layout);
        select_category_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AddProductActivity.this, CategorySelectActivity.class);
                startActivityForResult(intent, 7);
            }
        });
        select_cat_lable = (TextView) findViewById(R.id.select_category);
        if (scrren_mode != null) {
            if (scrren_mode.equals("edit")) {

                add_id = Integer.parseInt(getIntent().getStringExtra("add_id"));
                tittle.setText(getIntent().getStringExtra("add_tittle"));
                desc.setText(getIntent().getStringExtra("add_desc"));
                price.setText(getIntent().getStringExtra("add_price"));
                image_path = (ArrayList<String>) getIntent().getSerializableExtra("image_list");


            }
        } else {
            scrren_mode = "no_edit";
            image_path = (ArrayList<String>) getIntent().getSerializableExtra("image_list");

        }
        attributes_layout = (LinearLayout) findViewById(R.id.attributes_layout);
        display1 = (ImageView) findViewById(R.id.view);
        display2 = (ImageView) findViewById(R.id.view2);
        display3 = (ImageView) findViewById(R.id.view3);
        display4 = (ImageView) findViewById(R.id.view4);
        category = (Spinner) findViewById(R.id.spinner);
        post_condition = (Spinner) findViewById(R.id.spinner3);
        sub_category = (Spinner) findViewById(R.id.spinner2);
        category_names = new ArrayList<String>();
        category_names.add(getString(R.string.select_category));
        category_ids = new ArrayList<String>();
        category_ids.add("-1");
        sub_category_names = new ArrayList<String>();
        sub_category_ids = new ArrayList<String>();
        sub_category_names.add("Select Sub Category");
        sub_category_ids.add("-1");
        category_names_atr = new ArrayList<>();
        category_ids_atr = new ArrayList<>();
        condition_values = new ArrayList<>();
        condition_values_tosend = new ArrayList<>();
        condition_values.add(getString(R.string.select_condition));
        condition_values.add(getString(R.string.condition_new));
        condition_values.add(getString(R.string.condition_old));
        condition_values.add(getString(R.string.condition_working));
        condition_values.add(getString(R.string.condition_not_working));

        condition_values_tosend.add("Condition");
        condition_values_tosend.add("New");
        condition_values_tosend.add("Mint Condition");
        condition_values_tosend.add("Used");
        condition_values_tosend.add("Need to Fix");

        post_condition.setAdapter(new ArrayAdapter<String>(AddProductActivity.this,
                android.R.layout.simple_spinner_dropdown_item,
                condition_values));
        // for (int i = 0; i < image_path.size(); i++) {
        //display_image(i);
//            Log.e("files size", image_path.get(i));
        // }
        if (image_path != null)
            total = image_path.size();

        submit_btn = (Button) findViewById(R.id.add_submit_btn);
        submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validate_form(true);
            }
        });

     //    get_categories();

    }

    protected void get_categories() {

        String url;
        url = Settings.SERVER_URL + "category-json.php";
        Log.e("url--->", url);
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject jsonObject) {
                Log.e("response is: ", jsonObject.toString());
                try {
                    JSONArray jsonArray = jsonObject.getJSONArray("categories");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject cat = jsonArray.getJSONObject(i);
                        String cat_name = cat.getString("title");
                        String cat_id = cat.getString("id");
                        JSONArray attributes = cat.getJSONArray("attributes");
                        Log.e("cat_name", cat_name + "--" + cat_id);
                        ArrayList<String> temp_names = new ArrayList<>();
                        ArrayList<String> temp_ids = new ArrayList<>();
                        for (int j = 0; j < attributes.length(); j++) {
                            String atr_name = attributes.getJSONObject(j).getString("title");
                            String atr_id = attributes.getJSONObject(j).getString("id");
                            temp_names.add(atr_name);
                            temp_ids.add(atr_id);
                            Log.e("atr_name", atr_name + "==" + atr_id);
                        }
                        category_names.add(cat_name);
                        category_ids.add(cat_id);
                       // category_names_atr.add(temp_names);
                      //  category_ids_atr.add(temp_ids);
                    }
                    category.setAdapter(new ArrayAdapter<String>(AddProductActivity.this,
                            android.R.layout.simple_spinner_dropdown_item,
                            category_names));
                    sub_category.setAdapter(new ArrayAdapter<String>(AddProductActivity.this,
                            android.R.layout.simple_spinner_dropdown_item,
                            sub_category_names));

                    if (scrren_mode.equals("edit"))
                        get_product_details(getIntent().getStringExtra("add_id"));

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

    protected void get_sub_category(String cat_id, String cat_name) {

        final ProgressDialog progressDialog;
        progressDialog = new ProgressDialog(AddProductActivity.this);
        progressDialog.setMessage("loading sub categories");
        progressDialog.setCancelable(false);
        progressDialog.show();
        sub_category_names.clear();
        sub_category_ids.clear();
        sub_category_names.add("Select Sub Category");
        sub_category_ids.add("-1");

        String url;
        url = Settings.SERVER_URL + "category-json.php?parent_id=" + cat_id;
        Log.e("url--->", url);
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject jsonObject) {
                if (progressDialog != null)
                    progressDialog.dismiss();
                Log.e("response is: ", jsonObject.toString());
                try {
                    JSONArray jsonArray = jsonObject.getJSONArray("categories");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject cat = jsonArray.getJSONObject(i);
                        String cat_name = cat.getString("title");
                        String cat_id = cat.getString("id");
                        Log.e("cat_name", cat_name + "--" + cat_id);
                        sub_category_names.add(cat_name);
                        sub_category_ids.add(cat_id);
                    }
                    sub_category.setAdapter(new ArrayAdapter<String>(AddProductActivity.this,
                            android.R.layout.simple_spinner_dropdown_item,
                            sub_category_names));


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                if (progressDialog != null)
                    progressDialog.dismiss();
                // TODO Auto-generated method stub
                Log.e("response is:", error.toString());
            }
        });

// Access the RequestQueue through your singleton class.
        AppController.getInstance().addToRequestQueue(jsObjRequest);

    }


    private void display_image(int num) {
        switch (num) {
            case 0:
                Picasso.with(this).load(image_path.get(num)).resize(50, 50).into(display1);
                break;
            case 1:
                Picasso.with(this).load(image_path.get(num)).resize(50, 50).into(display2);
                break;
            case 2:
                Picasso.with(this).load(image_path.get(num)).resize(50, 50).into(display3);
                break;
            case 3:
                Picasso.with(this).load(image_path.get(num)).resize(50, 50).into(display4);
                break;
        }
    }

    private void validate_form(Boolean mode) {
        if (tittle.getText().toString().equals(""))
            Toast.makeText(AddProductActivity.this, getString(R.string.compulsory_title), Toast.LENGTH_SHORT).show();
        else if (desc.getText().toString().equals(""))
            Toast.makeText(AddProductActivity.this, getString(R.string.please_add_description), Toast.LENGTH_SHORT).show();
        else if (price.getText().toString().equals(""))
            Toast.makeText(AddProductActivity.this, getString(R.string.please_add_price), Toast.LENGTH_SHORT).show();
        else if (post_condition.getSelectedItemPosition() == 0)
            Toast.makeText(AddProductActivity.this, getString(R.string.compulsory_condition), Toast.LENGTH_SHORT).show();
        else if (!isCatselected)
            Toast.makeText(AddProductActivity.this, getString(R.string.compulsory_category), Toast.LENGTH_SHORT).show();
        else if (validate_atributes()) {
            if(mode)
            post_add_details();
            else{
                Intent previewintent = new Intent(AddProductActivity.this, AddPreviewActivity.class);
                previewintent.putExtra("title",tittle.getText().toString());
                previewintent.putExtra("price",price.getText().toString());
                previewintent.putExtra("desc",desc.getText().toString());
                previewintent.putExtra("offer",price_mode);
                previewintent.putExtra("image_list", image_path);
                startActivity(previewintent);
            }
        }
    }

    private boolean validate_atributes() {
        for (int m = 0; m < allEds.size(); m++) {
            if (allEds.get(m).getText().toString().equals("")) {
                Toast.makeText(AddProductActivity.this, "please enter " + category_names_atr.get(m), Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        for (int m = 0; m < allSpinners.size(); m++) {
            if (allSpinners.get(m).getSelectedItemPosition() == 0) {
                Toast.makeText(AddProductActivity.this, "please enter " + category_names_atr.get(m), Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        return true;
    }


    private void post_add_details() {
        submit_btn.setVisibility(View.INVISIBLE);
        prgDialog = new ProgressDialog(AddProductActivity.this);
        prgDialog.setMessage("uploading product details...");
        prgDialog.show();
        String url = null;
        if (scrren_mode.equals("edit")) {
            url = Settings.SERVER_URL + "edit-add.php?";
        } else {
            url = Settings.SERVER_URL + "insert-add.php?";
        }
        try {
            url = url + "category=" + selected_category_id +
                    "&title=" + URLEncoder.encode(tittle.getText().toString(), "UTF-8") +
                    "&description=" + URLEncoder.encode(desc.getText().toString(), "UTF-8") +
                    "&price=" + URLEncoder.encode(price.getText().toString(), "UTF-8") +
                    "&make_offer=" + (price_mode)+
                    "&condition=" + (URLEncoder.encode(condition_values_tosend.get(post_condition.getSelectedItemPosition()),"utf-8"))+
                    "&cust_id=" + cust_id;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if (category_ids_atr.size() > 0) {
            String atr_id_string = "";
            String atr_value_string = "";
            for (int a = 0; a < allEds.size(); a++) {
                if (a == 0) {
                    atr_id_string = allEds.get(a).getTag().toString();
                    atr_value_string = allEds.get(a).getText().toString();
                } else {
                    atr_id_string = atr_id_string + "@_@" + allEds.get(a).getTag().toString();
                    atr_value_string = atr_value_string + "@_@" + allEds.get(a).getText().toString();
                }
            }
            for (int a = 0; a < allSpinners.size(); a++) {
                if (a == 0) {
                    atr_id_string = allSpinners.get(a).getTag().toString();
                    atr_value_string = allSpinners.get(a).getSelectedItem().toString();
                } else {
                    atr_id_string = atr_id_string + "@_@" + allSpinners.get(a).getTag().toString();
                    atr_value_string = atr_value_string + "@_@" + allSpinners.get(a).getSelectedItem().toString();
                }
            }

            url = url + "&att_string=" + atr_id_string + "&att_value_string=" + atr_value_string;


        }
        if (scrren_mode.equals("edit")) {
            url = url + "&product_id=" + add_id;
        }

        Log.e("url;;;;", url);
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject jsonObject) {
                          Log.e("response is", jsonObject.toString());
                try {
                    Log.e("response is", jsonObject.getString("response"));
                    JSONArray jsonArray = jsonObject.getJSONArray("response");
                    JSONObject jsonObject1 = jsonArray.getJSONObject(0);
                    String response_json = jsonObject1.optString("message");
                    add_id = jsonObject1.optInt("add_id");
                    if (response_json.equals("Inserted Successfully")) {
                        Log.e("add_id", add_id.toString());
                        //  Toast.makeText(AddProductActivity.this,"Your add was posted successfullt",Toast.LENGTH_SHORT).show();
                       // prgDialog = new ProgressDialog(AddProductActivity.this);
                       // prgDialog.setMessage("Processing product images...");
                        encodeImagetoString();
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
                if (prgDialog != null)
                    prgDialog.dismiss();
            }
        });

        AppController.getInstance().addToRequestQueue(jsObjRequest);
    }


    private void showAlert(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
                .setCancelable(false)
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }


    public void encodeImagetoString() {
        new AsyncTask<Void, Void, String>() {

            protected void onPreExecute() {

            }

            ;

            @Override
            protected String doInBackground(Void... params) {
                BitmapFactory.Options options = null;
                options = new BitmapFactory.Options();
                options.inSampleSize = 3;

                for (int enc = 0; enc < image_path.size(); enc++) {
                    Log.e("imagw_path_in_loop", image_path.get(enc));
                    imgPath = image_path.get(enc);
                    if (!imgPath.contains("http:")) {
                        bitmap = BitmapFactory.decodeFile(imgPath, options);
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        // Must compress the Image to reduce image size to make upload easy
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream);
                        byte[] byte_arr = stream.toByteArray();
                        // Encode Image to String
                        encodedString.add(Base64.encodeToString(byte_arr, Base64.NO_WRAP));
                        encodedExt.add("jpg");
                    }

                }
                return "";
            }

            @Override
            protected void onPostExecute(String msg) {
                // Put converted Image string into Async Http Post param
                // Trigger Image upload
                triggerImageUpload();
            }
        }.execute(null, null, null);
    }

    public void triggerImageUpload() {
        total_images = 0;
        total_images = encodedString.size();
        uploaded_images = 0;
        if(total_images>0)
        makeHTTPCall();
        else{
            if(prgDialog!=null)
            prgDialog.hide();
            Toast.makeText(getApplicationContext(), "your product uploaded successfully",
                    Toast.LENGTH_LONG).show();
            Intent intent = new Intent(AddProductActivity.this, AccountDetailsActivity.class);
            intent.putExtra("cust_id",cust_id);
            if (scrren_mode.equals("edit")) {
                finish();
            } else {
                startActivity(intent);
                finish();
            }
        }
    }

    public void makeHTTPCall() {

        prgDialog.setMessage("uploading product image " + (uploaded_images + 1) + " of " + total_images);
        AsyncHttpClient client = new AsyncHttpClient();
        // Don't forget to change the IP address to your LAN address. Port no as well.

        params.put("image_str", encodedString.get(uploaded_images));
        params.put("ext_str", encodedExt.get(uploaded_images));
        params.put("add_id", String.valueOf(add_id));

        client.post(Settings.SERVER_URL+"/add-images.php",
                params, new AsyncHttpResponseHandler() {
                    // When the response returned by REST has Http
                    // response code '200'
                    @Override
                    public void onSuccess(String response) {
                        // Hide Progress Dialog
                        uploaded_images++;

                        if (uploaded_images < total_images)
                            makeHTTPCall();
                        else {
                            if(prgDialog!=null)
                            prgDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "your product uploaded successfully",
                                    Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(AddProductActivity.this, AccountDetailsActivity.class);
                            intent.putExtra("cust_id",cust_id);
                            if (scrren_mode.equals("edit")) {
                                finish();
                            } else {
                                startActivity(intent);
                                finish();
                            }

                        }

                    }

                    // When the response returned by REST has Http
                    // response code other than '200' such as '404',
                    // '500' or '403' etc
                    @Override
                    public void onFailure(int statusCode, Throwable error,
                                          String content) {
                        // Hide Progress Dialog
                        if(prgDialog!=null)
                        prgDialog.dismiss();
                        // When Http response code is '404'
                        if (statusCode == 404) {
                            Toast.makeText(getApplicationContext(),
                                    "Requested resource not found",
                                    Toast.LENGTH_LONG).show();
                        }
                        // When Http response code is '500'
                        else if (statusCode == 500) {
                            Toast.makeText(getApplicationContext(),
                                    "Something went wrong at server end",
                                    Toast.LENGTH_LONG).show();
                        }
                        // When Http response code other than 404, 500
                        else {
                            Toast.makeText(
                                    getApplicationContext(),
                                    "Error Occured \n Most Common Error: \n1. Device not connected to Internet\n2. Web App is not deployed in App server\n3. App server is not running\n HTTP Status code : "
                                            + statusCode, Toast.LENGTH_LONG)
                                    .show();
                        }
                    }
                });
    }

    protected void get_product_details(String add_id1) {
        String url;
        url = Settings.SERVER_URL + "product-details-json.php?add_id=" + add_id1;
        Log.e("url--->", url);
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject jsonObject) {
                Log.e("response is: ", jsonObject.toString());
                try {
                    JSONArray products = jsonObject.getJSONArray("products");
                    JSONObject first_product = products.getJSONObject(0);
                    String category_id = first_product.getString("category");


                } catch (JSONException e) {
                    e.printStackTrace();
                    // setListViewHeightBasedOnChildren(offer_listview);

                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO Auto-generated method stub
                Log.e("response is:", error.toString());
                // setListViewHeightBasedOnChildren(offer_listview);

            }
        });

// Access the RequestQueue through your singleton class.
        AppController.getInstance().addToRequestQueue(jsObjRequest);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // check if the request code is same as what is passed  here it is 2
        if (requestCode == 7 && resultCode!=0) {
            extra.setVisibility(View.GONE);
            select_cat_lable.setText(data.getStringExtra("cat_name"));
            selected_category_id = data.getStringExtra("cat_id");

            isCatselected = true;
                ArrayList<String> temp_atr_names = (ArrayList<String>)data.getSerializableExtra("cat_atr_names");
                ArrayList<String> temp_atr_ids = (ArrayList<String>)data.getSerializableExtra("cat_atr_id");
                ArrayList<String> temp_atr_types = (ArrayList<String>)data.getSerializableExtra("cat_atr_types");
            ArrayList<ArrayList<String>> temp_atr_values = (ArrayList<ArrayList<String>>)data.getSerializableExtra("cat_atr_values");



            category_names_atr = temp_atr_names;
            category_ids_atr = temp_atr_ids;
            category_types_atr = temp_atr_types;
            category_types_values = temp_atr_values;
                attributes_layout.removeAllViewsInLayout();
                allEds.clear();
                allSpinners.clear();
                for (int k = 0; k < temp_atr_names.size(); k++) {
                    EditText editText = new EditText(AddProductActivity.this);
                    editText.setHint(temp_atr_names.get(k));
                    String temp_hint="";
                    if(temp_atr_types.get(k).equals("Number"))
                    {
                        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                        editText.setBackgroundColor(Color.parseColor("#e9e9e9"));
                        //editText.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
                        editText.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT ));
                        editText.setTag(category_ids_atr.get(k));
                        allEds.add(editText);
                        attributes_layout.addView(editText);
                        extra.setVisibility(View.VISIBLE);

                    }
                     else if(temp_atr_types.get(k).equals("Multiple")){
                        ArrayList<String> spinnerArray = new ArrayList<String>();
                        spinnerArray.add("--Select "+temp_atr_names.get(k)+"--");
                        Spinner spinner = new Spinner(this);
                        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, spinnerArray);
                        spinner.setAdapter(spinnerArrayAdapter);
                        spinner.setBackgroundResource(R.drawable.btn_dropdown_disabled);
                        for(int l = 0; l<temp_atr_values.get(k).size();l++  )
                        {
                            temp_hint = temp_hint + "," + temp_atr_values.get(k).get(l);
                            spinnerArray.add(temp_atr_values.get(k).get(l));
                        }
                        spinner.setTag(category_ids_atr.get(k));
                        allSpinners.add(spinner);
                        attributes_layout.addView(spinner);
                        extra.setVisibility(View.VISIBLE);
                    }
                    else
                    {
                        editText.setBackgroundColor(Color.parseColor("#e9e9e9"));
                        editText.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
                        allEds.add(editText);
                        attributes_layout.addView(editText);
                        extra.setVisibility(View.VISIBLE);

                    }

                                    }
        }
    }
}


