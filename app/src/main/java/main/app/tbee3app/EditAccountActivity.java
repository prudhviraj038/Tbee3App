package main.app.tbee3app;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Chinni on 02-11-2015.
 */
public class EditAccountActivity extends Activity{
    ArrayList<String> country_names,country_ids;
    ArrayList<String> state_names,state_ids;
    Spinner mySpinner, mySpinner_edit;
    EditText name,email,phone,city,facebook,instagram,twitter,skype;
    private String selected_country_id;
    private String selected_state_id;
    RequestParams params = new RequestParams();
    String imgPath, fileName;
    Bitmap bitmap;
    String encodedString;
    private Uri mImageCaptureUri;
    private static final int PICK_FROM_CAMERA = 1;
    private static final int PICK_FROM_FILE = 2;
    Bitmap sample;
    CircleImageView profile_image_change,profile_image;
    ProgressDialog progressDialog;
    TextView save_btn;
    LinearLayout social_details;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Settings.forceRTLIfSupported(this);
        setContentView(R.layout.register_page_layout_edit);
        social_details = (LinearLayout) findViewById(R.id.social_info_edit);
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        country_names= new ArrayList<>();
        country_ids = new ArrayList<>();
        country_names.add("--select your country--");
        country_ids.add("-1");
        state_names = new ArrayList<>();
        state_names.add("--select your state--");
        state_ids = new ArrayList<>();
        state_ids.add("-1");
        mySpinner = (Spinner) findViewById(R.id.spinner5);
        mySpinner_edit = (Spinner) findViewById(R.id.spinner6);
        phone = (EditText) findViewById(R.id.phone);
        name = (EditText) findViewById(R.id.name);
        email = (EditText) findViewById(R.id.email);
        city = (EditText) findViewById(R.id.city);
        facebook = (EditText) findViewById(R.id.facebook);
        instagram = (EditText) findViewById(R.id.instagram);
        twitter = (EditText) findViewById(R.id.twitter);
        skype = (EditText) findViewById(R.id.skype);
        save_btn = (TextView) findViewById(R.id.save_btn_settings);
        save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (imgPath == null) {
                    set_user_details();
                } else {
                    encodeImagetoString();
                }
            }
        });
        ImageView back_btn = (ImageView) findViewById(R.id.back_btn);
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        get_user_details();
        get_countries_from_database();

        mySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Log.e(country_names.get(i), country_ids.get(i));
                if (!country_ids.get(i).equals("-1"))
                    get_states_from_database(country_ids.get(i), country_names.get(i));
                selected_country_id = country_names.get(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        mySpinner_edit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Log.e(state_names.get(i), state_ids.get(i));
                selected_state_id = state_names.get(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        Button submit_btn = (Button) findViewById(R.id.submit_btn_in_edit);
        submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(imgPath==null) {
                    set_user_details();
                }
                else {
                    encodeImagetoString();
                }
            }
        });

        profile_image = (CircleImageView) findViewById(R.id.profile_image);
        profile_image_change = (CircleImageView) findViewById(R.id.profile_image_edit);
        profile_image_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(EditAccountActivity.this, "working", Toast.LENGTH_SHORT).show();
                selectphotos(view);
            }
        });

    }
    private void get_countries_from_database(){
        String url;


        url = "http://q8hat.com/soap-api/countries.php";


        Log.e("url--->", url);
        JsonArrayRequest jsObjRequest = new JsonArrayRequest( url, new Response.Listener<JSONArray>() {

            @Override
            public void onResponse(JSONArray jsonArray) {
                Log.e("response is: ", jsonArray.toString());


                try {
                    for(int i=0;i<jsonArray.length();i++){
                        JSONObject person = (JSONObject)jsonArray.get(i);
                        String country_name = person.getString("name");
                        String country_id_temp = person.getString("country_id");
                        country_names.add(country_name);
                        country_ids.add(country_id_temp);
                        Log.e(country_id_temp,country_name);
                    }

                    mySpinner.setAdapter(new ArrayAdapter<String>(EditAccountActivity.this,
                            android.R.layout.simple_spinner_dropdown_item,
                            country_names));
                    mySpinner_edit.setAdapter(new ArrayAdapter<String>(EditAccountActivity.this,
                            android.R.layout.simple_spinner_dropdown_item,
                            state_names));




                }catch (Exception e){
                    e.printStackTrace();
                }


            }


        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO Auto-generated method stub
                Log.e("response is:",error.toString());

            }
        });

// Access the RequestQueue through your singleton class.
        AppController.getInstance().addToRequestQueue(jsObjRequest);

    }

    private void get_states_from_database(String country_id,String country_name){
        String url;
        final ProgressDialog progressDialog1;
        progressDialog1 = new ProgressDialog(EditAccountActivity.this);
        progressDialog1.setMessage("loading state names from "+country_name+"...");
        progressDialog1.show();
        state_names.clear();
        state_ids.clear();
        state_names.add("--select a state from " + country_name +"--");
        state_ids.add("-1");
        url = "http://q8hat.com/soap-api/states.php?country_id="+country_id;


        Log.e("url--->", url);
        JsonArrayRequest jsObjRequest = new JsonArrayRequest( url, new Response.Listener<JSONArray>() {

            @Override
            public void onResponse(JSONArray jsonArray) {
                Log.e("response is: ", jsonArray.toString());
                if(progressDialog1!=null)
                    progressDialog1.dismiss();
                try {
                    for(int i=0;i<jsonArray.length();i++){
                        JSONObject person = (JSONObject)jsonArray.get(i);
                        String country_name = person.getString("name");
                        String country_id_temp = person.getString("state_id");
                        state_names.add(country_name);
                        state_ids.add(country_id_temp);
                        Log.e(country_id_temp,country_name);
                    }

                 /*   mySpinner_state.setAdapter(new ArrayAdapter<String>(context,
                            android.R.layout.simple_spinner_dropdown_item,
                            state_names));

                    mySpinner_state_edit.setAdapter(new ArrayAdapter<String>(context,
                            android.R.layout.simple_spinner_dropdown_item,
                            state_names)); */



                }catch (Exception e){
                    e.printStackTrace();
                }


            }


        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO Auto-generated method stub
                Log.e("response is:",error.toString());
                if(progressDialog1!=null)
                    progressDialog1.dismiss();

            }
        });

// Access the RequestQueue through your singleton class.
        AppController.getInstance().addToRequestQueue(jsObjRequest);

    }

    private void get_user_details(){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String cust_id = sharedPref.getString("tbee3_user","-1");
        String url = Settings.SERVER_URL+"user_details.php?cust_id="+cust_id;
        Log.e("get_user_details",url);
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, url,null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject jsonArray) {
                Log.e("response is: ", jsonArray.toString());
                try {

                    JSONObject details_object = jsonArray.getJSONObject("details");
                    Log.e("detaisls is :", details_object.toString());

                    phone.setText(details_object.getString("phone"));
                     name.setText(details_object.getString("name"));
                    email.setText(details_object.getString("email"));
                     city.setText(details_object.getString("city"));
                    facebook.setText(details_object.getString("facebook"));
                    instagram.setText(details_object.getString("instagram"));
                    twitter.setText(details_object.getString("twitter"));
                    skype.setText(details_object.getString("skype"));
                    String into_visible = details_object.getString("social_info_visible");
                    if(into_visible.equals("0")){
                        social_details.setVisibility(View.GONE);
                    }
                    Picasso.with(EditAccountActivity.this).load(details_object.getString("image")).
                            placeholder(get_default_drawable()).into(profile_image);
                }catch (Exception e){
                    e.printStackTrace();
                }


            }


        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO Auto-generated method stub
                Log.e("response is:",error.toString());

            }
        });

// Access the RequestQueue through your singleton class.
        AppController.getInstance().addToRequestQueue(jsObjRequest);




    }

    private void set_user_details(){
        progressDialog.setMessage("updating your details...");
        progressDialog.show();
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String cust_id = sharedPref.getString("tbee3_user", "-1");
        String name_str,phone_str,email_str,country_str,state_str,city_str,facebook_str,twitter_str,instagram_str,skype_str;
        name_str = name.getText().toString();
        phone_str = phone.getText().toString();
        email_str = email.getText().toString();
        city_str = city.getText().toString();
        facebook_str = facebook.getText().toString();
        twitter_str = twitter.getText().toString();
        instagram_str = instagram.getText().toString();
        skype_str = skype.getText().toString();

        String url = null;
        try {
            url = Settings.SERVER_URL+"edit-profile.php?customer_id="+cust_id+
                    "&name="+ URLEncoder.encode(name_str, "utf-8")+
                    "&phone="+ URLEncoder.encode(phone_str, "utf-8")+
                    "&email="+ URLEncoder.encode(email_str, "utf-8")+
                    "&city="+ URLEncoder.encode(city_str, "utf-8")+
                    "&facebook="+ URLEncoder.encode(facebook_str, "utf-8")+
                    "&twitter="+ URLEncoder.encode(twitter_str, "utf-8")+
                    "&instagram="+ URLEncoder.encode(instagram_str, "utf-8")+
                    "&skype="+ URLEncoder.encode(skype_str, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
            Log.e("set_url",url);
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, url,null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject jsonArray) {
                Log.e("response is: ", jsonArray.toString());
                if(progressDialog!=null)
                    progressDialog.dismiss();

                try {
                    Toast.makeText(getApplicationContext(), "your profile updated success fully",
                            Toast.LENGTH_LONG).show();
                            finish();

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
                Toast.makeText(getApplicationContext(), "please try again",
                        Toast.LENGTH_LONG).show();

            }
        });

// Access the RequestQueue through your singleton class.
        AppController.getInstance().addToRequestQueue(jsObjRequest);



    }

    public void encodeImagetoString() {

        new AsyncTask<Void, Void, String>() {
            ProgressDialog pdia;
            @Override
            protected void onPreExecute(){
                super.onPreExecute();
                pdia = new ProgressDialog(EditAccountActivity.this);
                pdia.setMessage("Processing profile image...");
                pdia.setCancelable(false);
                pdia.show();
            }

            @Override
            protected String doInBackground(Void... params) {
                BitmapFactory.Options options = null;
                options = new BitmapFactory.Options();
                options.inSampleSize = 3;
               // imgPath = image_path.get(0);
                    bitmap = BitmapFactory.decodeFile(imgPath,
                            options);
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    // Must compress the Image to reduce image size to make upload easy
                    bitmap.compress(Bitmap.CompressFormat.PNG, 50, stream);
                    byte[] byte_arr = stream.toByteArray();
                    // Encode Image to String
                    encodedString = Base64.encodeToString(byte_arr, 0);

                return "";
            }

            @Override
            protected void onPostExecute(String msg) {
//                prgDialog.setMessage("Calling Upload");
                // Put converted Image string into Async Http Post param

                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(EditAccountActivity.this);
                String cust_id = sharedPref.getString("tbee3_user", "-1");
                String name_str,phone_str,email_str,country_str,state_str,city_str,facebook_str,twitter_str,instagram_str,skype_str;
                name_str = name.getText().toString();
                phone_str = phone.getText().toString();
                email_str = email.getText().toString();
                city_str = city.getText().toString();
                facebook_str = facebook.getText().toString();
                twitter_str = twitter.getText().toString();
                instagram_str = instagram.getText().toString();
                skype_str = skype.getText().toString();

                try {
                    params.put("name", name_str);
                    params.put("phone", phone_str);
                    params.put("email", email_str);
                    params.put("city", city_str);
                    params.put("facebook", facebook_str);
                    params.put("instagram", instagram_str);
                    params.put("twitter", twitter_str);
                    params.put("skype", skype_str);
                    params.put("imagestr", encodedString);
                    params.put("ext", "jpg");
                    params.put("customer_id", String.valueOf(cust_id));
                    // Trigger Image upload
                } catch (Exception e) {
                    e.printStackTrace();
                }
                pdia.dismiss();
                   // triggerImageUpload();
                makeHTTPCall();
            }
        }.execute(null, null, null);
    }

    public void triggerImageUpload() {
        makeHTTPCall();
    }

    public void makeHTTPCall() {
   final ProgressDialog progressDialoghttp = new ProgressDialog(EditAccountActivity.this);
        progressDialoghttp.setMessage("uploading profile image...");
        progressDialoghttp.setCancelable(false);
        progressDialoghttp.show();
        AsyncHttpClient client = new AsyncHttpClient();
        // Don't forget to change the IP address to your LAN address. Port no as well.
        client.post(Settings.SERVER_URL + "edit-profile.php",
                params, new AsyncHttpResponseHandler() {
                    // When the response returned by REST has Http
                    // response code '200'
                    @Override
                    public void onSuccess(String response) {
                        // Hide Progress Dialog
                        progressDialoghttp.dismiss();
                        Toast.makeText(getApplicationContext(), "your profile updated success fully",
                                Toast.LENGTH_LONG).show();
                        finish();
                    }

                    // When the response returned by REST has Http
                    // response code other than '200' such as '404',
                    // '500' or '403' etc
                    @Override
                    public void onFailure(int statusCode, Throwable error,
                                          String content) {
                        // Hide Progress Dialog
                        progressDialoghttp.dismiss();
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

    public void selectphotos(View view) {
        final String[] items = new String[]{"From Camera", "From SD Card"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.select_dialog_item, items);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Select Image");
        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                if (item == 0) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    File file = new File(Environment.getExternalStorageDirectory(),
                            "tmp_avatar_" + String.valueOf(System.currentTimeMillis()) + ".jpg");
                    mImageCaptureUri = Uri.fromFile(file);

                    try {
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
                        intent.putExtra("return-data", true);

                        startActivityForResult(intent, PICK_FROM_CAMERA);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    dialog.cancel();
                } else {
                    //  Intent intent = new Intent();
                    // intent.setType("image/*");
                    // intent.setAction(Intent.ACTION_GET_CONTENT);
                    Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                    startActivityForResult(galleryIntent, PICK_FROM_FILE);
                }
            }
        });

        final AlertDialog dialog = builder.create();
        // mImageView = (ImageView) findViewById(R.id.imageView);
        dialog.show();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
                    if (requestCode == PICK_FROM_FILE && resultCode == RESULT_OK ) {
                    mImageCaptureUri = data.getData();
                    String path = getRealPathFromURI(mImageCaptureUri); //from Gallery

                    if (path == null)
                        path = mImageCaptureUri.getPath();
                    //from File Manag\\
                    if (path != null)
                        imgPath = path;
                    Intent intent = new Intent(EditAccountActivity.this, ImageEditActivity.class);
                    intent.putExtra("image_path", imgPath);
                    intent.putExtra("image_source", "gallery");
                        intent.putExtra("rotation", String.valueOf(getCameraPhotoOrientation(this,mImageCaptureUri,imgPath)));
                        startActivityForResult(intent, 4);

                    } else if (requestCode == PICK_FROM_CAMERA && resultCode == RESULT_OK ) {
                    String path = mImageCaptureUri.getPath();
                        imgPath = path;
                    Intent intent = new Intent(EditAccountActivity.this, ImageEditActivity.class);
                    intent.putExtra("image_path", imgPath);
                    intent.putExtra("image_source", "device_cam");
                        intent.putExtra("rotation", String.valueOf(getCameraPhotoOrientation(this,mImageCaptureUri,imgPath)));
                        startActivityForResult(intent, 4);
                }else if (requestCode == 4) {
                        String file_path = data.getStringExtra("image_path");
                        Log.e("ile_path", file_path);
                        sample = BitmapFactory.decodeFile(file_path);
                        profile_image.setImageBitmap(sample);
                        //Picasso.with(this).load(new File(file_path)).rotate(getCameraPhotoOrientation(this,mImageCaptureUri,file_path))
                        //  .into(profile_image);
                        imgPath = file_path;
                    }
        else{
                        Log.e("activity","not returned");
                    }

    }

    public String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(contentUri, proj, null, null, null);

        if (cursor == null) return null;

        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

        cursor.moveToFirst();

        return cursor.getString(column_index);
    }


    private Drawable get_default_drawable(){
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        return getResources().getDrawable(R.drawable.splash_logo,getTheme());
    } else {
        return getResources().getDrawable(R.drawable.splash_logo);
    }
}

    private static File getOutputMediaFile() {
        //make a new file directory inside the "sdcard" folder
        File mediaStorageDir = new File("/sdcard/", "Tbee_Camera");

        if (!mediaStorageDir.exists()) {
            //if you cannot make this folder return
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }

        //take the current timeStamp
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        //and make a media file:
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");

        return mediaFile;
    }

    public int getCameraPhotoOrientation(Context context, Uri imageUri, String imagePath){
        int rotate = 0;
        try {
          //  context.getContentResolver().notifyChange(imageUri, null);
            File imageFile = new File(imagePath);

            ExifInterface exif = new ExifInterface(imageFile.getAbsolutePath());
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate = 270;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate = 90;
                    break;
            }

            Log.i("RotateImage", "Exif orientation: " + orientation);
            Log.i("RotateImage", "Rotate value: " + rotate);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rotate;
    }


}
