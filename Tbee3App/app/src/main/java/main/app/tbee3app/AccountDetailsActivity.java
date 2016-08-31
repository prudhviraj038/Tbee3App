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
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

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
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import jp.wasabeef.blurry.Blurry;


public class AccountDetailsActivity extends Activity {
    private ViewFlipper vf;
    private float oldTouchValue;
    boolean have_products;
    private ArrayList<String> product_names, product_ids, product_images, product_favs,
            product_price, product_desc, product_phone, product_dates, product_cat_id,product_status, product_dates_exp, cust_ids, cust_qb_ids, cust_image, cus_tname;
    private ArrayList<ArrayList<String>> product_multiple_images, product_multiple_images_id;
    CircleImageView profile_image_change, profile_image;
    TextView joined, posted, sold, products_txt, details_txt;
    String cust_id;
    String cust_qb_id;
    String profile_url,fb,te,ins,skype,name,user_type;
    SharedPreferences sharedPref;
    LinearLayout account_details_list;
    LinearLayout social_details_list;
    ProductlistAdapter productlistAdapter;
    GridView gridView;
    TextView uname, uphone, umail, ucity, ufb, uins, utwit, uskype, no_products, uname2;
    ImageView chat_btn, notify, setbtn;
    String chat_mode;
    ImageView blurred_image,blurred_original;
    RelativeLayout disp0, disp1, disp2, disp3;
    TextView credits, package_btn;
    private Uri mImageCaptureUri;
    private static final int PICK_FROM_CAMERA = 1;
    private static final int PICK_FROM_FILE = 2;
    private String encodedString;
    String imgPath, fileName;
    Bitmap sample;
    RequestParams params = new RequestParams();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Settings.forceRTLIfSupported(this);
        setContentView(R.layout.fragment_layout_user);
        credits = (TextView) findViewById(R.id.credits);
        social_details_list = (LinearLayout) findViewById(R.id.social_in_details);
        ImageView back_btn = (ImageView) findViewById(R.id.back_btn);
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        vf = (ViewFlipper) findViewById(R.id.switchlayout);
        vf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // Toast.makeText(AccountDetailsActivity.this, "tap", Toast.LENGTH_SHORT).show();
                vf.showNext();
            }
        });

        disp0 = (RelativeLayout) findViewById(R.id.edit_account_view);
        disp1 = (RelativeLayout) findViewById(R.id.disp1);
        disp2 = (RelativeLayout) findViewById(R.id.disp2);
        disp3 = (RelativeLayout) findViewById(R.id.disp3);
        package_btn = (TextView) findViewById(R.id.packages_btn);
        package_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AccountDetailsActivity.this, PackageActivity.class);
                startActivity(intent);
            }
        });
        product_names = new ArrayList<>();
        product_ids = new ArrayList<>();
        product_favs = new ArrayList<>();
        product_images = new ArrayList<>();
        product_price = new ArrayList<>();
        product_desc = new ArrayList<>();
        product_phone = new ArrayList<>();
        product_dates = new ArrayList<>();
        product_dates_exp = new ArrayList<>();
        product_status = new ArrayList<>();
        cust_ids = new ArrayList<>();
        cust_qb_ids = new ArrayList<>();
        cust_image = new ArrayList<>();
        cus_tname = new ArrayList<>();
        product_multiple_images = new ArrayList<>();
        product_multiple_images_id = new ArrayList<>();
        gridView = (GridView) findViewById(R.id.gridView);
        chat_btn = (ImageView) findViewById(R.id.chat_btn);

        notify = (ImageView) findViewById(R.id.notify);
        notify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AccountDetailsActivity.this, NotificationActivity.class);
                startActivity(intent);
            }
        });
        setbtn = (ImageView) findViewById(R.id.settings);
        final RelativeLayout edit_btn = (RelativeLayout) findViewById(R.id.edit_account_view);
        setbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AccountDetailsActivity.this,ProfileSettings.class);
                intent.putExtra("name",name);
                intent.putExtra("profile_pic",profile_url);
                intent.putExtra("facebook",fb);
                intent.putExtra("instagram",ins);
                intent.putExtra("twitter",te);
                intent.putExtra("skype",skype);
                intent.putExtra("user_type",user_type);
                startActivity(intent);
            }
        });
        chat_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (cust_qb_id.equals("") || cust_qb_id == null) {
                    Toast.makeText(AccountDetailsActivity.this, "Quickblox id not created for this user..", Toast.LENGTH_SHORT).show();
                } else {
                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(AccountDetailsActivity.this);
                    if(sharedPreferences.getString("tbee3_user","-1").equals("-1")) {

                        Intent intent = new Intent(AccountDetailsActivity.this, AccountActivity.class);
                        intent.putExtra("goto", "chat");
                        intent.putExtra("opponentid", cust_qb_id);
                        intent.putExtra("mode", chat_mode);
                        startActivity(intent);

                    }
                    else {
                        Intent intent = new Intent(AccountDetailsActivity.this, ChatStartActivity.class);
                        intent.putExtra("opponentid", cust_qb_id);
                        intent.putExtra("mode", chat_mode);
                        startActivity(intent);
                    }
                }
            }
        });
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        blurred_image = (ImageView) (findViewById(R.id.blurred));
        blurred_original = (ImageView) (findViewById(R.id.blured_original));
        // Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.profile_pic);
        //Bitmap blurredbp =    blurRenderScript(this, largeIcon, 25);
        //blurred_image.setImageBitmap(blurredbp);

        account_details_list = (LinearLayout) findViewById(R.id.details_view_list);
        no_products = (TextView) findViewById(R.id.no_products_textview);
        String temp_id = getIntent().getStringExtra("cust_id");
        profile_image = (CircleImageView) findViewById(R.id.profile_image);
        profile_image_change = (CircleImageView) findViewById(R.id.profile_image_edit);
        cust_id = sharedPref.getString("tbee3_user", "-1");
        Log.e("check_ids", temp_id + "-" + cust_id);
        if (!cust_id.equals(temp_id)) {
            cust_id = temp_id;
            profile_image_change.setVisibility(View.GONE);
            productlistAdapter = new ProductlistAdapter(this, this, false, product_names, product_ids, product_images, product_price, product_favs, product_dates, product_dates_exp, product_status);
            productlistAdapter.setshowdays(true);
            chat_mode = "other";
            //   disp0.setVisibility(View.GONE);
            //  disp1.setVisibility(View.VISIBLE);
            //  disp2.setVisibility(View.VISIBLE);
            //  disp3.setVisibility(View.VISIBLE);
            chat_btn.setVisibility(View.VISIBLE);
            package_btn.setVisibility(View.GONE);
            setbtn.setVisibility(View.GONE);
            notify.setVisibility(View.GONE);

        } else {
            // disp0.setVisibility(View.VISIBLE);
            //   disp1.setVisibility(View.GONE);
            //   disp2.setVisibility(View.GONE);
            //  disp3.setVisibility(View.GONE);
            chat_btn.setVisibility(View.VISIBLE);
            package_btn.setVisibility(View.VISIBLE);
            setbtn.setVisibility(View.VISIBLE);
            notify.setVisibility(View.VISIBLE);

            chat_mode = "self";
            productlistAdapter = new ProductlistAdapter(this, this, true, product_names, product_ids, product_images,
                    product_price, product_favs, product_dates, product_dates_exp,product_status);
            productlistAdapter.setshowdays(true);

            edit_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(AccountDetailsActivity.this, EditAccountActivity.class);
                   // startActivity(intent);
                    selectphotos(view);
                }
            });

        }

        gridView.setAdapter(productlistAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent product_details = new Intent(AccountDetailsActivity.this, ProductDetailsActivity.class);
                product_details.putExtra("add_id", product_ids.get(i));
                product_details.putExtra("add_tittle", product_names.get(i));
                product_details.putExtra("add_price", product_price.get(i));
                product_details.putExtra("add_image", product_images.get(i));
                product_details.putExtra("add_desc", product_desc.get(i));
                product_details.putExtra("add_phone", product_phone.get(i));
                product_details.putExtra("add_custid", cust_ids.get(i));
                product_details.putExtra("add_qbid", cust_qb_ids.get(i));
                product_details.putExtra("add_custname", cus_tname.get(i));
                product_details.putExtra("add_custimage", cust_image.get(i));
                startActivity(product_details);

            }
        });

        joined = (TextView) findViewById(R.id.joned);
        posted = (TextView) findViewById(R.id.posted);
        sold = (TextView) findViewById(R.id.sold);
        products_txt = (TextView) findViewById(R.id.products_btn);
        details_txt = (TextView) findViewById(R.id.details_btn);

        uname = (TextView) findViewById(R.id.editText);
        uname2 = (TextView) findViewById(R.id.display_username);
        uphone = (TextView) findViewById(R.id.editText2);
        umail = (TextView) findViewById(R.id.editText4);
        ucity = (TextView) findViewById(R.id.editText5);

        ufb = (TextView) findViewById(R.id.ufb);
        uins = (TextView) findViewById(R.id.uins);
        utwit = (TextView) findViewById(R.id.utwit);
        uskype = (TextView) findViewById(R.id.uskype);

        products_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vf.setDisplayedChild(0);
                if (have_products) {
                    gridView.setVisibility(View.VISIBLE);
                    no_products.setVisibility(View.GONE);
                } else {
                    gridView.setVisibility(View.GONE);
                    no_products.setVisibility(View.VISIBLE);
                }
                account_details_list.setVisibility(View.GONE);

            }
        });

        details_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gridView.setVisibility(View.GONE);
                no_products.setVisibility(View.GONE);
                account_details_list.setVisibility(View.VISIBLE);
                vf.setDisplayedChild(1);

            }
        });

        //get_products();

    }

    public static Bitmap blurRenderScript(Context context, Bitmap smallBitmap, int radius) {
        try {
            smallBitmap = RGB565toARGB888(smallBitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Bitmap bitmap = Bitmap.createBitmap(
                smallBitmap.getWidth(), smallBitmap.getHeight(),
                Bitmap.Config.ARGB_8888);

        RenderScript renderScript = RenderScript.create(context);

        Allocation blurInput = Allocation.createFromBitmap(renderScript, smallBitmap);
        Allocation blurOutput = Allocation.createFromBitmap(renderScript, bitmap);

        ScriptIntrinsicBlur blur = ScriptIntrinsicBlur.create(renderScript,
                Element.U8_4(renderScript));
        blur.setInput(blurInput);
        blur.setRadius(radius); // radius must be 0 < r <= 25
        blur.forEach(blurOutput);

        blurOutput.copyTo(bitmap);
        renderScript.destroy();

        return bitmap;

    }

    private static Bitmap RGB565toARGB888(Bitmap img) throws Exception {
        int numPixels = img.getWidth() * img.getHeight();
        int[] pixels = new int[numPixels];

        //Get JPEG pixels.  Each int is the color values for one pixel.
        img.getPixels(pixels, 0, img.getWidth(), 0, 0, img.getWidth(), img.getHeight());

        //Create a Bitmap of the appropriate format.
        Bitmap result = Bitmap.createBitmap(img.getWidth(), img.getHeight(), Bitmap.Config.ARGB_8888);

        //Set RGB pixels.
        result.setPixels(pixels, 0, result.getWidth(), 0, 0, result.getWidth(), result.getHeight());
        return result;
    }

    @Override
    public void onBackPressed() {
        SharedPreferences sharedPref;
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        if (sharedPref.getString("first_launch", "-1").equals("100")) {
            Intent intent = new Intent(AccountDetailsActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        } else
            super.onBackPressed();
    }

    private void get_user_details() {

        String url = Settings.SERVER_URL + "user_details.php?cust_id=" + cust_id;
        Log.e("get_user_details", url);
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject jsonArray) {
                Log.e("response is: ", jsonArray.toString());
                try {

                    JSONObject details_object = jsonArray.getJSONObject("details");
                    Log.e("detaisls is :", details_object.toString());
                    cust_qb_id = details_object.getString("qb_id");
                    joined.setText(convert_date(details_object.getString("join_date")));
                    posted.setText(details_object.getString("no_products"));
                    sold.setText(details_object.getString("no_sold"));
                    ucity.setText(details_object.getString("city"));
                    uname.setText(details_object.getString("name"));
                    uname2.setText(details_object.getString("name"));
                    umail.setText(details_object.getString("email"));
                    uphone.setText(details_object.getString("phone"));
                    name = details_object.getString("name");
                    ufb.setText(details_object.getString("facebook"));
                    fb = details_object.getString("facebook");
                    te = details_object.getString("twitter");
                    ins = details_object.getString("instagram");
                    skype = details_object.getString("skype");
                    user_type = details_object.getString("current_package_name");
                    utwit.setText(details_object.getString("twitter"));
                    uins.setText(details_object.getString("instagram"));
                    uskype.setText(details_object.getString("skype"));
                    String into_visible = details_object.getString("social_info_visible");
                    if(into_visible.equals("0")){
                            social_details_list.setVisibility(View.GONE);
                    }
                    credits.setText(getResources().getString(R.string.credits) +" : "+ String.valueOf(details_object.getInt("no_credits")));
                    profile_url = details_object.getString("image");
                    if(imgPath == null) {
                        Picasso.with(AccountDetailsActivity.this).load(details_object.getString("image")).
                                placeholder(get_default_drawable()).into(profile_image, new com.squareup.picasso.Callback() {
                            @Override
                            public void onSuccess() {
                                Blurry.with(AccountDetailsActivity.this).capture(blurred_original).into(blurred_image);
                            }

                            @Override
                            public void onError() {

                            }
                        });
                        Picasso.with(AccountDetailsActivity.this).load(details_object.getString("image")).resize(blurred_original.getMeasuredWidth(), blurred_original.getMeasuredHeight()).centerCrop().into(blurred_original, new com.squareup.picasso.Callback() {
                            @Override
                            public void onSuccess() {
                                Blurry.with(AccountDetailsActivity.this).capture(blurred_original).into(blurred_image);
                            }

                            @Override
                            public void onError() {

                            }
                        });
                        final Handler handler = new Handler();

                        final Runnable r = new Runnable() {
                            public void run() {
                                Blurry.with(AccountDetailsActivity.this).capture(blurred_original).into(blurred_image);
                            }
                        };

                        handler.postDelayed(r, 20);

                    }

                } catch (Exception e) {
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

    private Drawable get_default_drawable() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return getResources().getDrawable(R.drawable.default_profilepic, getTheme());
        } else {
            return getResources().getDrawable(R.drawable.default_profilepic
            );
        }
    }

    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first

        // Get the Camera instance as the activity achieves full user focus
        get_user_details();
        get_products();

    }

    private String convert_date(String date_str) {
        SimpleDateFormat form = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        java.util.Date date = null;
        try {
            date = form.parse(date_str);
        } catch (ParseException e) {
            e.printStackTrace();
            return date_str;

        }
        SimpleDateFormat postFormater = new SimpleDateFormat("dd MMM yy");
        String newDateStr = postFormater.format(date);

        return newDateStr;
    }

    protected void get_products() {

        String url;
        url = Settings.SERVER_URL + "product-json.php?my_id=" + cust_id;
        Log.e("url--->", url);
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject jsonObject) {
                Log.e("response is: ", jsonObject.toString());
                try {
                    JSONArray jsonArray = jsonObject.getJSONArray("products");
                    product_names.clear();
                    product_ids.clear();
                    product_images.clear();
                    product_multiple_images.clear();
                    product_multiple_images_id.clear();
                    product_price.clear();
                    product_desc.clear();
                    product_phone.clear();
                    product_dates.clear();
                    cust_ids.clear();
                    cust_image.clear();
                    cust_qb_ids.clear();
                    cus_tname.clear();

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject cat = jsonArray.getJSONObject(i);
                        String cat_name = cat.getString("title");
                        String cat_id = cat.getString("id");
                        String fav_count = String.valueOf(cat.getInt("fav_count"));
                        String cat_price = cat.getString("price");
                        String cat_desc = cat.getString("description");
                        String cat_phone = cat.getString("phone");
                        String cat_date = cat.getString("date");
                        String cat_date_exp = cat.getString("expiry_date");
                        String cat_custid = cat.getString("cust_id");
                        String cat_custname = cat.getString("cust_name");
                        String cat_custqbid = cat.getString("cust_qb_id");
                        String cat_custimage = cat.getString("cust_image");
                        String cat_status = cat.getString("status");

                        //   JSONArray attributes = cat.getJSONArray("attributes");
                        JSONArray images = cat.getJSONArray("images");
                        ArrayList<String> temp = new ArrayList<>();
                        ArrayList<String> temp_id = new ArrayList<>();
                        String img_url = "";
                        String img_id = "";
                        for (int j = 0; j < images.length(); j++) {
                            Log.e(String.valueOf(i), String.valueOf(j));
                            img_url = images.getJSONObject(j).getString("image");
                            try {
                                img_id = images.getJSONObject(j).getString("img_id");
                            } catch (Exception ex) {
                                img_id = "0";
                            }
                            temp.add(img_url);
                            temp_id.add(img_id);
                        }
                        Log.e("cat_name", cat_name + "--" + cat_id);
                        product_names.add(cat_name);
                        product_ids.add(cat_id);
                        product_favs.add(fav_count);
                        product_images.add(img_url);
                        product_multiple_images.add(temp);
                        product_multiple_images_id.add(temp_id);
                        product_price.add(cat_price);
                        product_desc.add(cat_desc);
                        product_phone.add(cat_phone);
                        product_dates.add(cat_date);
                        product_dates_exp.add(cat_date_exp);
                        product_status.add(cat_status);
                        cust_ids.add(cat_custid);
                        cust_image.add(cat_custimage);
                        cust_qb_ids.add(cat_custqbid);
                        cus_tname.add(cat_custname);

                    }
                    productlistAdapter.notifyDataSetChanged();
                    // setListViewHeightBasedOnChildren(gridView);
                    no_products.setVisibility(View.GONE);
                    have_products = true;

                } catch (JSONException e) {
                    e.printStackTrace();
                    no_products.setVisibility(View.VISIBLE);
                    have_products = false;
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO Auto-generated method stub
                Log.e("response is:", error.toString());
                no_products.setVisibility(View.VISIBLE);
                have_products = false;
            }
        });

// Access the RequestQueue through your singleton class.
        AppController.getInstance().addToRequestQueue(jsObjRequest);
    }

    public static void setListViewHeightBasedOnChildren(GridView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        if (listAdapter.getCount() % 2 == 0)
            params.height = totalHeight / 2;
        else
            params.height = (totalHeight + totalHeight / listAdapter.getCount()) / 2;
        listView.setLayoutParams(params);
    }


    public void updatelist(int pos) {
        String url = Settings.SERVER_URL + "del-product-json.php?cust_id=" + cust_id + "&product_id=" + product_ids.get(pos);
        product_names.remove(pos);
        product_ids.remove(pos);
        product_images.remove(pos);
        product_multiple_images.remove(pos);
        product_multiple_images_id.remove(pos);
        product_price.remove(pos);
        product_desc.remove(pos);
        product_phone.remove(pos);
        product_dates.remove(pos);
        product_dates_exp.remove(pos);
        product_status.remove(pos);
        productlistAdapter.notifyDataSetChanged();
        //  String url = Settings.SERVER_URL+"del-product-json.php?cust_id="+cust_id+"&product_id="+product_ids.get(pos);
        Log.e("get_user_details", url);
        JsonArrayRequest jsObjRequest = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {

            @Override
            public void onResponse(JSONArray jsonArray) {
                Log.e("response is: ", jsonArray.toString());
                try {
                    Log.e("product", "removed");
                } catch (Exception e) {
                    Log.e("product", "not ");

                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO Auto-generated method stub
                Log.e("response is:", error.toString());
                Log.e("product", "not ");

            }
        });

// Access the RequestQueue through your singleton class.
        AppController.getInstance().addToRequestQueue(jsObjRequest);


    }

    public void update_sold(int pos) {
        String url = Settings.SERVER_URL + "sold-product-json.php?cust_id=" + cust_id + "&product_id=" + product_ids.get(pos);
                product_status.set(pos,"3");
                productlistAdapter.notifyDataSetChanged();
        //  String url = Settings.SERVER_URL+"del-product-json.php?cust_id="+cust_id+"&product_id="+product_ids.get(pos);
        Log.e("get_user_details", url);
        JsonArrayRequest jsObjRequest = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {

            @Override
            public void onResponse(JSONArray jsonArray) {
                Log.e("response is: ", jsonArray.toString());
                try {
                    Log.e("product", "removed");
                } catch (Exception e) {
                    Log.e("product", "not ");

                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO Auto-generated method stub
                Log.e("response is:", error.toString());
                Log.e("product", "not ");

            }
        });

// Access the RequestQueue through your singleton class.
        AppController.getInstance().addToRequestQueue(jsObjRequest);


    }


    public void edit_product(int pos) {
        Intent product_edit = new Intent(AccountDetailsActivity.this, CameraActivity.class);
        product_edit.putExtra("mode", "edit");
        product_edit.putExtra("add_id", product_ids.get(pos));
        product_edit.putExtra("add_tittle", product_names.get(pos));
        product_edit.putExtra("add_desc", product_desc.get(pos));
        product_edit.putExtra("add_price", product_price.get(pos));
        product_edit.putExtra("image_list", product_multiple_images.get(pos));
        product_edit.putExtra("image_list_id", product_multiple_images_id.get(pos));

        startActivity(product_edit);
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
            Intent intent = new Intent(AccountDetailsActivity.this, ImageEditActivity.class);
            intent.putExtra("image_path", imgPath);
            intent.putExtra("image_source", "gallery");
            intent.putExtra("rotation", String.valueOf(getCameraPhotoOrientation(this,mImageCaptureUri,imgPath)));
            startActivityForResult(intent, 4);

        } else if (requestCode == PICK_FROM_CAMERA && resultCode == RESULT_OK ) {
            String path = mImageCaptureUri.getPath();
            imgPath = path;
            Intent intent = new Intent(AccountDetailsActivity.this, ImageEditActivity.class);
            intent.putExtra("image_path", imgPath);
            intent.putExtra("image_source", "device_cam");
            intent.putExtra("rotation", String.valueOf(getCameraPhotoOrientation(this,mImageCaptureUri,imgPath)));
            startActivityForResult(intent, 4);
        }else if (requestCode == 4) {
            String file_path = data.getStringExtra("image_path");
            Log.e("ile_path", file_path);
            sample = BitmapFactory.decodeFile(file_path);
            profile_image.setImageBitmap(sample);
            Blurry.with(AccountDetailsActivity.this).capture(profile_image).into(blurred_image);

            //Picasso.with(this).load(new File(file_path)).rotate(getCameraPhotoOrientation(this,mImageCaptureUri,file_path))
            //  .into(profile_image);
            imgPath = file_path;
            encodeImagetoString();
        }
        else{
            Log.e("activity","not returned");
        }
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
    public String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(contentUri, proj, null, null, null);

        if (cursor == null) return null;

        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

        cursor.moveToFirst();

        return cursor.getString(column_index);
    }
    public void encodeImagetoString() {

        new AsyncTask<Void, Void, String>() {
            ProgressDialog pdia;
            @Override
            protected void onPreExecute(){
                super.onPreExecute();
                pdia = new ProgressDialog(AccountDetailsActivity.this);
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
                sample = BitmapFactory.decodeFile(imgPath,
                        options);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                // Must compress the Image to reduce image size to make upload easy
                sample.compress(Bitmap.CompressFormat.PNG, 50, stream);
                byte[] byte_arr = stream.toByteArray();
                // Encode Image to String
                encodedString = Base64.encodeToString(byte_arr, 0);

                return "";
            }

            @Override
            protected void onPostExecute(String msg) {
//                prgDialog.setMessage("Calling Upload");
                // Put converted Image string into Async Http Post param

                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(AccountDetailsActivity.this);
                String cust_id = sharedPref.getString("tbee3_user", "-1");
                try {
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
    public void makeHTTPCall() {
        final ProgressDialog progressDialoghttp = new ProgressDialog(AccountDetailsActivity.this);
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
                       // finish();
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



}
