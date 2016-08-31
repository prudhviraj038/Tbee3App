package main.app.tbee3app;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.nirhart.parallaxscroll.views.ParallaxScrollView;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.Locale;

import jp.wasabeef.blurry.Blurry;

public class ProfileSettings extends Activity {
    LinearLayout linearLayout,linearLayoutchild,contact_us,contact_us_child,packages,change_language,profile_layout,change_number;
    ImageView settings_drop,contacts_drop,back_btn,back_btn2;
    ViewFlipper viewFlipper;
    TextView user_type,username,name,facebook,instagram,twitter,skype;
    ParallaxScrollView parallaxScrollView;
    TextView edit_btn_settings;
    ImageView profile_pic,profile_pic_detail;
    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first

        // Get the Camera instance as the activity achieves full user focus
        get_user_details();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Settings.forceRTLIfSupported(this);
        setContentView(R.layout.settings_screen);
        parallaxScrollView = (ParallaxScrollView) findViewById(R.id.paralax_scroll);
        parallaxScrollView.post(new Runnable() {
            @Override
            public void run() {
                parallaxScrollView.fullScroll(View.FOCUS_DOWN);
            }
        });
        edit_btn_settings = (TextView) findViewById(R.id.edit_btn_settings);
        edit_btn_settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileSettings.this,EditAccountActivity.class);
                startActivity(intent);
            }
        });
        username = (TextView) findViewById(R.id.user_name);
        user_type = (TextView) findViewById(R.id.user_type);
        name = (TextView) findViewById(R.id.name);
        facebook = (TextView) findViewById(R.id.facebook);
        instagram = (TextView) findViewById(R.id.instagram);
        twitter = (TextView) findViewById(R.id.twitter);
        skype = (TextView) findViewById(R.id.skype);
        back_btn = (ImageView) findViewById(R.id.back_btn);
        back_btn2 = (ImageView) findViewById(R.id.back_btn2);
        linearLayout = (LinearLayout) findViewById(R.id.settings);
        profile_layout = (LinearLayout) findViewById(R.id.profile_layout);
        viewFlipper = (ViewFlipper) findViewById(R.id.viewFlipper2);
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(viewFlipper.getDisplayedChild()!=0)
                    viewFlipper.showPrevious();
                else
                    finish();
            }
        });
        back_btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(viewFlipper.getDisplayedChild()!=0)
                    viewFlipper.showPrevious();
                else
                    finish();
            }
        });

        profile_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewFlipper.setDisplayedChild(1);
            }
        });
        change_number = (LinearLayout) findViewById(R.id.change_number);
        change_number.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileSettings.this,AccountActivity.class);
                startActivity(intent);
            }
        });
        linearLayoutchild = (LinearLayout) findViewById(R.id.settings_child);
        settings_drop = (ImageView) findViewById(R.id.settings_dropdown);
        change_language = (LinearLayout) findViewById(R.id.change_language);
        change_language.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                select_language(view);
            }
        });
        contact_us = (LinearLayout) findViewById(R.id.contact_us);
        contact_us_child = (LinearLayout) findViewById(R.id.contact_us_child);
        contacts_drop = (ImageView) findViewById(R.id.contacts_drop);

        packages = (LinearLayout) findViewById(R.id.packages);
        packages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileSettings.this, PackageActivity.class);
                startActivity(intent);
            }
        });
        String mname = getIntent().getStringExtra("name");
        String url = getIntent().getStringExtra("profile_pic");
        String fb = getIntent().getStringExtra("facebook");
        String ins = getIntent().getStringExtra("instagram");
        String te = getIntent().getStringExtra("twitter");
        String mskype = getIntent().getStringExtra("skype");
        String muser_type = getIntent().getStringExtra("user_type");
        username.setText(mname);
        name.setText(mname);
        facebook.setText(fb);
        instagram.setText(ins);
        twitter.setText(te);
        skype.setText(mskype);
        user_type.setText(muser_type + "User");
        Log.e(url, "url");
         profile_pic = (ImageView) findViewById(R.id.profile_pic);
         profile_pic_detail = (ImageView) findViewById(R.id.detail_profile_pic);
        Picasso.with(this).load(url).into(profile_pic);
        Picasso.with(this).load(url).into(profile_pic_detail);
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showorhide(linearLayoutchild, settings_drop);
            }
        });
        contact_us.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showorhide(contact_us_child, contacts_drop);
            }
        });
    }

    public void showorhide(View view,ImageView imageview){
        if(view.getVisibility()==View.GONE) {
            view.setVisibility(View.VISIBLE);
            imageview.setImageResource(R.drawable.drop_up_icon);
        }
        else if(view.getVisibility()==View.VISIBLE) {
            view.setVisibility(View.GONE);
            imageview.setImageResource(R.drawable.drop_down_icon);
        }
    }

    public void slideToBottom(View view){
        TranslateAnimation animate = new TranslateAnimation(0,0,0,view.getHeight());
        animate.setDuration(500);
        animate.setFillAfter(true);
        view.startAnimation(animate);
        view.setVisibility(View.GONE);
    }
    // To animate view slide out from bottom to top
    public void slideToTop(View view){
        TranslateAnimation animate = new TranslateAnimation(0,0,0,-view.getHeight());
        animate.setDuration(500);
        animate.setFillAfter(true);
        view.startAnimation(animate);
        view.setVisibility(View.VISIBLE);
    }

    public void select_language(View view) {
        final String[] items = new String[]{"English", "Arabic"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.select_dialog_item, items);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Select Language");
        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                if (item == 0) {
                    SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(ProfileSettings.this);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString("lan","en");
                    editor.commit();
                    Resources res = ProfileSettings.this.getResources();
                    // Change locale settings in the app.
                    DisplayMetrics dm = res.getDisplayMetrics();
                    android.content.res.Configuration conf = res.getConfiguration();
                    conf.locale = new Locale("en".toLowerCase());
                    res.updateConfiguration(conf, dm);
                    dialog.cancel();
                    Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                } else {
                    SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(ProfileSettings.this);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString("lan","ar");
                    editor.commit();

                    Resources res = ProfileSettings.this.getResources();
                    // Change locale settings in the app.
                    DisplayMetrics dm = res.getDisplayMetrics();
                    android.content.res.Configuration conf = res.getConfiguration();
                    conf.locale = new Locale("ar".toLowerCase());
                    res.updateConfiguration(conf, dm);
                    dialog.cancel();
                    Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            }
        });

        final AlertDialog dialog = builder.create();
        // mImageView = (ImageView) findViewById(R.id.imageView);
        dialog.show();
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

                   // phone.setText(details_object.getString("phone"));
                    name.setText(details_object.getString("name"));
                    //email.setText(details_object.getString("email"));
                    //city.setText(details_object.getString("city"));
                    facebook.setText(details_object.getString("facebook"));
                    instagram.setText(details_object.getString("instagram"));
                    twitter.setText(details_object.getString("twitter"));
                    skype.setText(details_object.getString("skype"));

                    Picasso.with(ProfileSettings.this).load(details_object.getString("image")).into(profile_pic);
                    Picasso.with(ProfileSettings.this).load(details_object.getString("image")).into(profile_pic_detail);
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


}
