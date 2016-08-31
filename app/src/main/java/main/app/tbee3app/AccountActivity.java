package main.app.tbee3app;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.text.Html;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.quickblox.auth.QBAuth;
import com.quickblox.auth.model.QBSession;
import com.quickblox.core.QBEntityCallbackImpl;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Chinni on 16-10-2015.
 */
public class AccountActivity extends Activity {
    Spinner language;
    String imgPath;
    CircleImageView profile_image_change,profile_image;
    private Uri mImageCaptureUri;
    private static final int PICK_FROM_CAMERA = 1;
    private static final int PICK_FROM_FILE = 2;
    Bitmap sample;
    ViewFlipper reg_view;
    int screen_count=0;
    EditText username,password,phonenumber,activationcode;
    String activation_str = "none";
    String member_str = "none";
    boolean valid = false;
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;
    ProgressDialog progressDialog ;
    CheckBox checkBox;
    TextView terms_textview,terms_description,terms_description_ar;
    private void get_terms(){
        String url = Settings.SERVER_URL+"terms.php";
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET,url,null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject jsonObject) {
                                Log.e("response is",jsonObject.toString());
                try {
                    terms_textview.setText(Html.fromHtml(
                            jsonObject.getJSONObject("terms").getString(getResources().getString(R.string.zcat_title))));
                    terms_description.setText(Html.fromHtml(jsonObject.getJSONObject("terms").getString("description")));
                    terms_description_ar.setText(Html.fromHtml(jsonObject.getJSONObject("terms").getString("description_ar")));

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO Auto-generated method stub
                Log.e("error response is:",error.toString());
                            }
        });

        AppController.getInstance().addToRequestQueue(jsObjRequest);


    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_page_layout);
        terms_textview = (TextView) findViewById(R.id.terms_textview);
        terms_description = (TextView) findViewById(R.id.terms_description);
        terms_description_ar = (TextView) findViewById(R.id.terms_description_ar);
        get_terms();
        progressDialog = new ProgressDialog(this);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        editor = sharedPref.edit();
        reg_view = (ViewFlipper) findViewById(R.id.viewFlipper);
        username = (EditText)findViewById(R.id.editText16);
        password = (EditText)findViewById(R.id.editText17);
        language = (Spinner) findViewById(R.id.spinner4);
        checkBox = (CheckBox) findViewById(R.id.checkBoxterms);
        ArrayList<String> language_select = new ArrayList<>();
        language_select.add("-select language-");
        language_select.add("English");
        language_select.add("Arabic");
                language.setAdapter(new ArrayAdapter<String>(AccountActivity.this,
                        android.R.layout.simple_spinner_dropdown_item,
                        language_select));
        phonenumber = (EditText) findViewById(R.id.phone_number);
        activationcode = (EditText) findViewById(R.id.act_code_edit);
        profile_image = (CircleImageView) findViewById(R.id.profile_image);
        profile_image_change = (CircleImageView) findViewById(R.id.profile_image_edit);
        profile_image_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(AccountActivity.this, "working", Toast.LENGTH_SHORT).show();
                selectphotos(view);
            }
        });

        Button next_btn = (Button) findViewById(R.id.next_btn_inreg);
        next_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (screen_count == 0) {
                    if (username.getText().toString().equals("") || password.getText().toString().equals("")) {
                        Toast.makeText(AccountActivity.this, "enter your name and password", Toast.LENGTH_SHORT).show();
                        valid = false;
                    } else if (language.getSelectedItemPosition() == 0) {
                        Toast.makeText(AccountActivity.this, "select your language", Toast.LENGTH_SHORT).show();
                        valid = false;
                    } else
                        valid = true;
                } else if (screen_count == 1) {
                    if (phonenumber.getText().toString().equals("")) {
                        Toast.makeText(AccountActivity.this, "enter phone number", Toast.LENGTH_SHORT).show();
                        valid = false;

                    } else {
                        valid = true;
                        get_data();
                    }
                } else if (screen_count == 2) {
                    if (activationcode.getText().toString().equals("")) {
                        Toast.makeText(AccountActivity.this, "enter activation code", Toast.LENGTH_SHORT).show();
                        valid = false;
                    } else if (activationcode.getText().toString().equals(activation_str)) {
                        valid = true;
                    } else {
                        Toast.makeText(AccountActivity.this, "activation code not matched", Toast.LENGTH_SHORT).show();
                        valid = true;
                    }
                } else if (screen_count == 3) {
                    if (!checkBox.isChecked()) {
                        Toast.makeText(AccountActivity.this, "agree with our terms and conditions to continue", Toast.LENGTH_SHORT).show();
                        valid = false;
                    } else {
                        valid = true;

                        progressDialog.setMessage("please wait ....");
                        progressDialog.setCancelable(false);
                        progressDialog.show();
                    //    create_qb_user("user_"+member_str, "12345678");
                        editor.putString("tbee3_user", member_str);
                        editor.putString("tbee3_qbid", "+965"+phonenumber.getText().toString());
                        editor.commit();

                        if(getIntent().getStringExtra("goto").equals("home")) {
                            Intent intent = new Intent(AccountActivity.this, HomeActivity.class);
                            startActivity(intent);
                            finish();
                        }
                        else if(getIntent().getStringExtra("goto").equals("post")){
                                Intent add_a_product = new Intent(AccountActivity.this, CameraActivity.class);
                                startActivity(add_a_product);
                            }else if(getIntent().getStringExtra("goto").equals("chat"))
                            {
                                Intent intent = new Intent(AccountActivity.this, ChatStartActivity.class);
                                intent.putExtra("opponentid", getIntent().getStringExtra("opponentid"));
                                intent.putExtra("mode", "other");
                                startActivity(intent);
                            }else if(getIntent().getStringExtra("goto").equals("details")){
                                Intent i = new Intent(AccountActivity.this,AccountDetailsActivity.class);
                                i.putExtra("cust_id",member_str);
                                startActivity(i);

                            }else if(getIntent().getStringExtra("goto").equals("wanted")){
                                Intent i = new Intent(AccountActivity.this, WantedListActivity.class);
                                i.putExtra("mode", "open");
                                startActivity(i);
                            }else if(getIntent().getStringExtra("goto").equals("wish")){
                                Intent i = new Intent(AccountActivity.this, WishListActivity.class);
                                startActivity(i);
                            }
                            finish();

                    }
                }

                if (screen_count < 3 && valid) {
                    reg_view.showNext();
                    screen_count++;
                }
                            }

        });
        language.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i==1)
                {
                    terms_description.setVisibility(View.VISIBLE);
                    terms_description_ar.setVisibility(View.GONE);
                }
                else if(i==2){
                    terms_description.setVisibility(View.GONE);
                    terms_description_ar.setVisibility(View.VISIBLE);
                }
                else{
                    terms_description.setVisibility(View.VISIBLE);
                    terms_description_ar.setVisibility(View.GONE);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

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
                    Intent intent = new Intent();

                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);

                    startActivityForResult(Intent.createChooser(intent, "Complete action using"), PICK_FROM_FILE);
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
            Intent intent = new Intent(AccountActivity.this, ImageEditActivity.class);
            intent.putExtra("image_path", imgPath);
            intent.putExtra("image_source", "gallery");
            intent.putExtra("rotation", String.valueOf(getCameraPhotoOrientation(this,mImageCaptureUri,imgPath)));
            startActivityForResult(intent, 4);

        } else if (requestCode == PICK_FROM_CAMERA && resultCode == RESULT_OK ) {
            String path = mImageCaptureUri.getPath();
            imgPath = path;
            Intent intent = new Intent(AccountActivity.this, ImageEditActivity.class);
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

    @Override
    public void onBackPressed() {
        Log.d("CDA", "onBackPressed Called");
        if(screen_count>0){
            reg_view.showPrevious();
            screen_count--;
        }
        else
            super.onBackPressed();
    }

    private void get_data(){

        final ProgressDialog  progressDialog= new ProgressDialog(AccountActivity.this);
        progressDialog.setMessage("please wait....");
        progressDialog.show();
        progressDialog.setCancelable(false);
        String url = null;
        String language_str;
        if(language.getSelectedItemPosition() == 1)
        {
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("lan","en");
            editor.commit();
            Resources res =this.getResources();
            // Change locale settings in the app.
            DisplayMetrics dm = res.getDisplayMetrics();
            android.content.res.Configuration conf = res.getConfiguration();
            conf.locale = new Locale("en".toLowerCase());
            res.updateConfiguration(conf, dm);

        }
        else{
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("lan","ar");
            editor.commit();
            Resources res =this.getResources();
            // Change locale settings in the app.
            DisplayMetrics dm = res.getDisplayMetrics();
            android.content.res.Configuration conf = res.getConfiguration();
            conf.locale = new Locale("ar".toLowerCase());
            res.updateConfiguration(conf, dm);
        }
      //  String gcm_id = new PlayServicesHelper(AccountActivity.this).getRegistrationId();
        try {
            url = Settings.SERVER_URL+"add-member.php?phone=+965"+phonenumber.getText().toString()+
                    "&name="+ URLEncoder.encode(username.getText().toString(), "utf-8")
                    +"&password="+ URLEncoder.encode(password.getText().toString(), "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        Log.e("register url",url);
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET,url,null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject jsonObject) {
                if(progressDialog!=null)
                    progressDialog.dismiss();
                    Log.e("response is",jsonObject.toString());
                try{
                    Log.e("response is",jsonObject.getString("response"));
                    JSONArray jsonArray = jsonObject.getJSONArray("response");
                    JSONObject jsonObject1 = jsonArray.getJSONObject(0);
                    String result = jsonObject1.getString("status");
                    if(result.equals("Success")) {
                        String act_code = jsonObject1.getString("act_code");
                        activation_str = act_code;
                        String member_id = jsonObject1.getString("member_id");
                        member_str = member_id;
                        Log.e("act_code:",act_code);
                       // Toast.makeText(AccountActivity.this,"act_code: "+act_code,Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO Auto-generated method stub
                Log.e("error response is:",error.toString());
                if(progressDialog!=null)
                    progressDialog.dismiss();
            }
        });

        AppController.getInstance().addToRequestQueue(jsObjRequest);
    }

    private void create_qb_user(final String usernames, final String password){
        QBAuth.createSession(new QBEntityCallbackImpl<QBSession>() {
            @Override
            public void onSuccess(QBSession session, Bundle params) {
                // success
                final QBUser user = new QBUser(usernames, password);
                user.setFullName(username.getText().toString());
                QBUsers.signUp(user, new QBEntityCallbackImpl<QBUser>() {
                    @Override
                    public void onSuccess(QBUser user, Bundle args) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (progressDialog != null)
                                    progressDialog.dismiss();
                            }
                        });

                        // success
                        Log.e("qbuser", "created");
                        Log.e("qb_user_id", String.valueOf(user.getId()));
                        update_qbid(usernames, String.valueOf(user.getId()));
                    }

                    @Override
                    public void onError(List<String> errors) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (progressDialog != null)
                                    progressDialog.dismiss();
                            }
                        });

                        // error
                        Log.e("qbuser", "failed");
                        editor.putString("tbee3_user", member_str);
                        editor.putString("first_launch", "101");
                        editor.commit();
                        Intent intent = new Intent(AccountActivity.this, HomeActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });

            }

            @Override
            public void onError(List<String> errors) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (progressDialog != null)
                            progressDialog.dismiss();
                    }
                });

                // errors
            }
        });

    }

    private void update_qbid(String member_strs,String qbid_str){

        final ProgressDialog  progressDialog= new ProgressDialog(AccountActivity.this);
        progressDialog.setMessage("please wait creting chat account....");
        progressDialog.show();
        progressDialog.setCancelable(false);
        String url =  Settings.SERVER_URL+"update_qb_id.php?cust_id="+member_strs+"&qb_id="+qbid_str;
        Log.e("url-->",url);
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET,url,null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject jsonObject) {
                if(progressDialog!=null)
                    progressDialog.dismiss();
                Log.e("response is",jsonObject.toString());
                try{
                    Log.e("response is",jsonObject.getString("response"));
                    editor.putString("tbee3_user", member_str);
                    editor.putString("first_launch", "101");
                    editor.commit();

                    Intent intent = new Intent(AccountActivity.this,HomeActivity.class);
                    startActivity(intent);
                    finish();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(AccountActivity.this,"there was an error please try again",Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO Auto-generated method stub
                Log.e("error response is:",error.toString());
                if(progressDialog!=null)
                    progressDialog.dismiss();
                Toast.makeText(AccountActivity.this,"there was an error please try again",Toast.LENGTH_SHORT).show();
            }
        });

        AppController.getInstance().addToRequestQueue(jsObjRequest);
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
