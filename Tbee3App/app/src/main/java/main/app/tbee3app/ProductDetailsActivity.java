package main.app.tbee3app;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.daimajia.slider.library.Indicators.PagerIndicator;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.DefaultSliderView;
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.util.Attributes;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ProductDetailsActivity extends AppCompatActivity {
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();
    //ParallaxScrollView parallaxScrollView;
    AlertDialog alertDialogcreate = null;
    String phone_str;
    ArrayList<String> offer_id,offer_name,offer_price,offer_image,offer_time,offer_phone,offer_qbid,offer_uid,
            product_atr_names,product_atr_values,
            comment_id,comment_name,comment_price,comment_image,comment_time,comment_phone,comment_qbid,comment_uid;
    OfferPricelistAdapter offerPricelistAdapter;
    CommentslistAdapter CommentsAdapter;
    ListView offer_listview,comments_listview;
    String add_id,cust_id;
    boolean iswishitem;
    DBhelper db;
    SharedPreferences sharedPref;
    TextView view_count,fav_count,days_ago,condition;
    LinearLayout extra_attributes;
    NetworkImageView thumbNail;
    SliderLayout sliderShow;
    List<TextView> allEds = new ArrayList<TextView>();
    TextView uploader_name ;
    ImageView uploader_image;
    TextView price_area;
    TextView make_an_offer;
    TextView no_comments;
    String highest_offer = "0";
    String can_bid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Settings.forceRTLIfSupported(this);
        setContentView(R.layout.product_detail_view);
        ImageView back_btn = (ImageView) findViewById(R.id.back_btn);
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        offer_listview = (ListView) findViewById(R.id.offers_listview);

// set creator
        uploader_name = (TextView) findViewById(R.id.uploader_name);
        uploader_image = (ImageView) findViewById(R.id.profile_image);
        uploader_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 Intent intent = new Intent(ProductDetailsActivity.this,AccountDetailsActivity.class);
                intent.putExtra("cust_id", getIntent().getStringExtra("add_custid"));
                startActivity(intent);

            }
        });
        uploader_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProductDetailsActivity.this,AccountDetailsActivity.class);
                intent.putExtra("cust_id", getIntent().getStringExtra("add_custid"));
                startActivity(intent);

            }
        });

        if (imageLoader == null)
            imageLoader = AppController.getInstance().getImageLoader();
        db = new DBhelper(getApplicationContext());
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        cust_id = sharedPref.getString("tbee3_user", "-1");
        LayoutInflater inflater = getLayoutInflater();
        ViewGroup header = (ViewGroup)inflater.inflate(R.layout.detail_view_header, offer_listview, false);
        ViewGroup footer = (ViewGroup)inflater.inflate(R.layout.details_view_footer, offer_listview, false);

        TextView send_btn = (TextView) footer.findViewById(R.id.send_button);
        no_comments = (TextView) footer.findViewById(R.id.no_comments);
        send_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // make_a_comment("hello",cust_id);
                cust_id = sharedPref.getString("tbee3_user", "-1");
                if (!cust_id.equals("-1")) {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(ProductDetailsActivity.this);
                    alertDialog.setMessage("Enter your comment");
                    final EditText input = new EditText(ProductDetailsActivity.this);
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.MATCH_PARENT,1f);
                    input.setLayoutParams(lp);
                    alertDialog.setView(input);
                    alertDialog.setNegativeButton("cancel",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });

                    alertDialog.setPositiveButton("submit",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                     make_a_comment(input.getText().toString(),cust_id);

                                }
                            });

                    alertDialog.show();
                } else {
                    Intent i = new Intent(ProductDetailsActivity.this, AccountActivity.class);
                    startActivity(i);
                }

            }
        });

        sliderShow = (SliderLayout) header.findViewById(R.id.slider);
        sliderShow.stopAutoCycle();
        sliderShow.setCustomIndicator((PagerIndicator) header.findViewById(R.id.pager_indicator));

        offer_listview.addHeaderView(header, null, false);
       // offer_listview.addFooterView(footer, null, false);
        comments_listview = (SwipeMenuListView) footer.findViewById(R.id.comments_view);
        uploader_name.setText(getIntent().getStringExtra("add_custname"));
        Picasso.with(this).load(getIntent().getStringExtra("add_custimage")).placeholder(R.drawable.default_profilepic).into(uploader_image);
        thumbNail = (NetworkImageView) header.findViewById(R.id.product_image_detail);
        Log.e("img_url", getIntent().getStringExtra("add_custimage"));
//        thumbNail.setImageUrl(getIntent().getStringExtra("add_image").replaceAll(" ", "%20"), imageLoader);
        //parallaxScrollView = (ParallaxScrollView) findViewById(R.id.scroll);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(0xffff2621));
        TextView description = (TextView) findViewById(R.id.desc);
        description.setText(getIntent().getStringExtra("add_desc"));

        getSupportActionBar().hide();
        extra_attributes = (LinearLayout) header.findViewById(R.id.extra_attributes_layout);
        view_count = (TextView) header.findViewById(R.id.view_count);
        fav_count = (TextView) header.findViewById(R.id.fav_count);
        days_ago = (TextView) header.findViewById(R.id.days_ago);
        condition = (TextView) header.findViewById(R.id.condition_display);
        offer_id = new ArrayList<>();
        offer_name = new ArrayList<>();
        offer_price = new ArrayList<>();
        offer_image = new ArrayList<>();
        offer_time = new ArrayList<>();
        offer_phone = new ArrayList<>();
        offer_qbid = new ArrayList<>();
        offer_uid = new ArrayList<>();

        comment_id = new ArrayList<>();
        comment_name = new ArrayList<>();
        comment_price = new ArrayList<>();
        comment_image = new ArrayList<>();
        comment_time = new ArrayList<>();
        comment_phone = new ArrayList<>();
        comment_qbid = new ArrayList<>();
        comment_uid = new ArrayList<>();


        product_atr_names = new ArrayList<>();
        product_atr_values = new ArrayList<>();
        offerPricelistAdapter = new OfferPricelistAdapter(this,true,offer_name,offer_id,offer_uid,offer_price,offer_image,offer_time,offer_phone,offer_qbid,getIntent().getStringExtra("add_custid"));
        CommentsAdapter = new CommentslistAdapter(this,true,comment_name,comment_id,comment_uid,comment_price,comment_image,comment_time,comment_phone,comment_qbid,getIntent().getStringExtra("add_custid"));
        offer_listview.setAdapter(offerPricelistAdapter);
        comments_listview.setAdapter(CommentsAdapter);
        offerPricelistAdapter.setMode(Attributes.Mode.Single);
        CommentsAdapter.setMode(Attributes.Mode.Single);
        offer_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ((SwipeLayout) (offer_listview.getChildAt(position - offer_listview.getFirstVisiblePosition()))).open(true);
                final Intent intent = new Intent(ProductDetailsActivity.this,AccountDetailsActivity.class);
                intent.putExtra("cust_id", offer_uid.get(position - 1));
                startActivity(intent);
            }
        });
        offer_listview.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.e("ListView", "OnTouch");
                return false;
            }
        });

        comments_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ((SwipeLayout) (comments_listview.getChildAt(position - comments_listview.getFirstVisiblePosition()))).open(true);
                final Intent intent = new Intent(ProductDetailsActivity.this,AccountDetailsActivity.class);
                intent.putExtra("cust_id", comment_uid.get(position - 1));
                startActivity(intent);

            }
        });
        comments_listview.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.e("ListView", "OnTouch");
                return false;
            }
        });

        get_product_details(getIntent().getStringExtra("add_id"),true);
        add_id = getIntent().getStringExtra("add_id");
        make_an_offer = (TextView)header.findViewById(R.id.make_an_offer);
        make_an_offer.setVisibility(View.GONE);
        phone_str = getIntent().getStringExtra("add_phone");
        Log.e("phone", phone_str);

        make_an_offer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                cust_id = sharedPref.getString("tbee3_user", "-1");
                if (!cust_id.equals("-1")) {
                    final AlertDialog.Builder alertDialog = new AlertDialog.Builder(ProductDetailsActivity.this);
                    alertDialog.setMessage(getResources().getString(R.string.key_offer_price_text));

                    final EditText input = new EditText(ProductDetailsActivity.this);
                    input.setInputType(InputType.TYPE_CLASS_NUMBER);
                    input.setImeOptions(EditorInfo.IME_ACTION_NONE);
                    input.setOnEditorActionListener(new EditText.OnEditorActionListener() {
                                                        @Override
                                                        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                                                            if (actionId == EditorInfo.IME_ACTION_DONE) {
                                                               // submit_btn.performClick();
                                                                boolean canupload = true;
                                                                if(!can_bid.equals("2"))
                                                                if (Integer.parseInt(highest_offer) >= Integer.parseInt(input.getText().toString())) {
                                                                    Toast.makeText(ProductDetailsActivity.this, getResources().getString(R.string.offer_should_be_greater) +" "+ Integer.parseInt(highest_offer), Toast.LENGTH_SHORT).show();
                                                                    canupload = false;
                                                                } else
                                                                    for (int i = 0; i < offer_price.size(); i++) {
                                                                        if (Integer.parseInt(offer_price.get(i)) >= Integer.parseInt(input.getText().toString())) {
                                                                            Toast.makeText(ProductDetailsActivity.this, getResources().getString(R.string.offer_should_be_greater) +" " + offer_price.get(i), Toast.LENGTH_SHORT).show();
                                                                            canupload = false;
                                                                            break;
                                                                        }
                                                                    }
                                                                if (canupload) {
                                                                    offer_price_upload(input.getText().toString(), cust_id);
                                                                    alertDialogcreate.dismiss();
                                                                }


                                                                return true;
                                                            }
                                                            return false;
                                                        }
                                                    });
                        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.MATCH_PARENT);
                        input.setLayoutParams(lp);
                        alertDialog.setView(input);
                        alertDialog.setNegativeButton(getResources().getString(R.string.cancel),
                                new DialogInterface.OnClickListener()
                                {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                }

                        );

                        alertDialog.setPositiveButton(getResources().getString(R.string.save),
                                new DialogInterface.OnClickListener()

                                {
                                    boolean canupload = true;

                                    public void onClick(DialogInterface dialog, int which) {
                                        if(!can_bid.equals("1"))
                                        if (Integer.parseInt(highest_offer) >= Integer.parseInt(input.getText().toString())) {
                                            Toast.makeText(ProductDetailsActivity.this, getResources().getString(R.string.offer_should_be_greater)+" " + Integer.parseInt(highest_offer), Toast.LENGTH_SHORT).show();
                                            canupload = false;
                                        } else
                                            for (int i = 0; i < offer_price.size(); i++) {
                                                if (Integer.parseInt(offer_price.get(i)) >= Integer.parseInt(input.getText().toString())) {
                                                    Toast.makeText(ProductDetailsActivity.this, getResources().getString(R.string.offer_should_be_greater)+" "  + offer_price.get(i), Toast.LENGTH_SHORT).show();
                                                    canupload = false;
                                                    break;
                                                }
                                            }
                                        if (canupload)
                                            offer_price_upload(input.getText().toString(), cust_id);
                                    }
                                });

                    alertDialog.show();
                    alertDialogcreate=alertDialog.create();
                } else {
                    Intent i = new Intent(ProductDetailsActivity.this, AccountActivity.class);
                    startActivity(i);
                }
            }

        });

         price_area = (TextView) header.findViewById(R.id.price_area);
        price_area.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                price_area(offer_listview);
            }
        });

        ImageView call_btn = (ImageView) header.findViewById(R.id.call_btn);
        call_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + phone_str));
                startActivity(callIntent);

            }
        });

        ImageView sms_btn = (ImageView) header.findViewById(R.id.msg_btn);
        sms_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(cust_id.equals(getIntent().getStringExtra("add_custid"))) {
                    Toast.makeText(ProductDetailsActivity.this,"this product is uploaded by you only!!",Toast.LENGTH_SHORT).show();
                }
                else if(getIntent().getStringExtra("add_qbid").equals("") || getIntent().getStringExtra("add_custid") == null)
                {
                    Toast.makeText(ProductDetailsActivity.this,"Quickblox id not created for this user..",Toast.LENGTH_SHORT).show();
                }
                else{
                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ProductDetailsActivity.this);
                    if(sharedPreferences.getString("tbee3_user","-1").equals("-1")) {

                        Intent intent = new Intent(ProductDetailsActivity.this, AccountActivity.class);
                        intent.putExtra("opponentid", getIntent().getStringExtra("add_qbid"));
                        intent.putExtra("mode", "other");
                        intent.putExtra("goto", "chat");
                        startActivity(intent);

                    }
                    else{
                        Intent intent = new Intent(ProductDetailsActivity.this, ChatStartActivity.class);
                        intent.putExtra("opponentid", getIntent().getStringExtra("add_qbid"));
                        intent.putExtra("mode", "other");
                        startActivity(intent);
                    }
                }

            }
        });

        ImageView share_btn = (ImageView) header.findViewById(R.id.share_btn);
        share_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/html");
                sharingIntent.putExtra(Intent.EXTRA_TEXT, Html.fromHtml("<p>This is the text that will be shared.</p>"));
                startActivity(Intent.createChooser(sharingIntent,"Share using"));
            }
        });

        final ImageView wish_btn = (ImageView) header.findViewById(R.id.wish_btn);

        int wish_count = db.get_wish_item_count(Integer.parseInt(add_id));
        Log.e("wish_count",String.valueOf(wish_count));
        if(wish_count==0)
            iswishitem=false;
        else
            iswishitem=true;

        if(iswishitem)
            wish_btn.setImageResource(R.drawable.chat_bar_heart_fill);
        else
            wish_btn.setImageResource(R.drawable.chat_bar_heart);

        wish_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (iswishitem) {
                    iswishitem = false;
                    remove_from_wishlist(add_id);
                    db.delete_wish_item(Integer.parseInt(add_id), "default");
                    wish_btn.setImageResource(R.drawable.chat_bar_heart);

                } else {
                    iswishitem = true;
                    add_to_wishlist(add_id);
                    db.add_wish_item(Integer.parseInt(add_id), "default");
                    wish_btn.setImageResource(R.drawable.chat_bar_heart_fill);


                }
            }
        });
            }



    protected void get_product_details(String add_id1, final Boolean reload) {
        String url;
        url = Settings.SERVER_URL + "product-details-json.php?add_id="+add_id1;
        Log.e("url--->", url);

        offer_id.clear();
        offer_name.clear();
        offer_price.clear();
        offer_image.clear();
        offer_time.clear();
        offer_phone.clear();
        offer_qbid.clear();
        offer_uid.clear();

        comment_id.clear();
        comment_name.clear();
        comment_price.clear();
        comment_image.clear();
        comment_time.clear();
        comment_phone.clear();
        comment_qbid.clear();
        comment_uid.clear();


        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject jsonObject) {
                Log.e("response is: ", jsonObject.toString());
                try {
                    JSONArray products = jsonObject.getJSONArray("products");
                    JSONObject first_product = products.getJSONObject(0);
                    String view_count_str = first_product.getString("view_count");
                    String wish_count_str = first_product.getString("fav_count");
                    can_bid = first_product.getString("make_offer");
                    String max_offer = first_product.getString("max_offer");
                    String price = first_product.getString("price");

                    highest_offer = price;

                    if(can_bid.equals("0")) {
                        make_an_offer.setVisibility(View.GONE);
                        price_area.setVisibility(View.VISIBLE);
                        price_area.setText("KD " + price);
                        price_area.setGravity(Gravity.CENTER);
                    }
                    else if(can_bid.equals("1")) {
                        make_an_offer.setVisibility(View.VISIBLE);
                        price_area.setVisibility(View.VISIBLE);

                        highest_offer = "0";
                        price_area.setText("KD " + price);
                        make_an_offer.setText(getResources().getString(R.string.make_an_offer));
                    }
                    else{
                        make_an_offer.setVisibility(View.VISIBLE);
                        price_area.setVisibility(View.VISIBLE);
                        price_area.setText(getResources().getString(R.string.price_starts_at)+" "+ highest_offer);
                        make_an_offer.setText(getResources().getString(R.string.key_no_bid));

                    }
                    view_count.setText(" "+view_count_str+ " views");
                    fav_count.setText(" "+wish_count_str+ " likes");
                    condition.setText(getResources().getString(R.string.select_condition) +" : " + first_product.getString("condition"));
                    days_ago.setText(get_different_dates(first_product.getString("date")));
                    JSONArray product_images = first_product.getJSONArray("images");
                    for(int j=0;j<product_images.length();j++){
                        JSONObject person = (JSONObject)product_images.get(j);
                        String image = person.getString("image");
                        DefaultSliderView defaultSliderView = new DefaultSliderView(ProductDetailsActivity.this);
                        defaultSliderView
                                .description("image")
                                .setScaleType(BaseSliderView.ScaleType.Fit)
                                .image(image);

                        sliderShow.addSlider( defaultSliderView);
                    }

                    JSONArray first_product_offers = first_product.getJSONArray("offer_prices");
                    if(!can_bid.equals("0"))
                        for(int i = 0;i<first_product_offers.length();i++){
                        Log.e("price", first_product_offers.getJSONObject(i).getString("price"));
                        offer_id.add(first_product_offers.getJSONObject(i).getString("offer_id"));
                        offer_uid.add(first_product_offers.getJSONObject(i).getString("cust_id"));
                        offer_price.add(first_product_offers.getJSONObject(i).getString("price"));
                        offer_name.add(first_product_offers.getJSONObject(i).getString("cust_name"));
                        offer_image.add(first_product_offers.getJSONObject(i).getString("cust_image"));
                        offer_time.add(first_product_offers.getJSONObject(i).getString("times"));
                            offer_phone.add(first_product_offers.getJSONObject(i).getString("phone"));
                            offer_qbid.add(first_product_offers.getJSONObject(i).getString("qb_id"));
                    }

                    JSONArray comments = first_product.getJSONArray("comments");
                        for(int i = 0;i<comments.length();i++) {
                            Log.e("comments", comments.getJSONObject(i).getString("comments"));
                            comment_id.add(comments.getJSONObject(i).getString("offer_id"));
                            comment_uid.add(comments.getJSONObject(i).getString("cust_id"));
                            comment_price.add(comments.getJSONObject(i).getString("comments"));
                            comment_name.add(comments.getJSONObject(i).getString("cust_name"));
                            comment_image.add(comments.getJSONObject(i).getString("cust_image"));
                            comment_time.add(comments.getJSONObject(i).getString("times"));
                            comment_phone.add(comments.getJSONObject(i).getString("phone"));
                            comment_qbid.add(comments.getJSONObject(i).getString("qb_id"));
                            no_comments.setText("comments");
                        }
                    if(reload) {
                        JSONArray first_product_attributes = first_product.getJSONArray("attributes");

                        for (int i = 0; i < first_product_attributes.length(); i++) {
                            product_atr_names.add(first_product_attributes.getJSONObject(i).getString(getResources().getString(R.string.zatrname)));
                            product_atr_values.add(first_product_attributes.getJSONObject(i).getString("attribute_value"));
                            TextView editText = new TextView(ProductDetailsActivity.this);
                            // editText.setBackgroundColor(Color.parseColor("#e9e9e9"));
                            editText.setText(first_product_attributes.getJSONObject(i).getString(getResources().getString(R.string.zatrname)) + " : " + first_product_attributes.getJSONObject(i).getString("attribute_value"));
                            editText.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
                            editText.setPadding(5, 0, 0, 0);
                            editText.setGravity(Gravity.CENTER);
                            allEds.add(editText);
                            extra_attributes.addView(editText);
                        }

                        offerPricelistAdapter = new OfferPricelistAdapter(ProductDetailsActivity.this, true, offer_name, offer_id, offer_uid, offer_price, offer_image, offer_time, offer_phone, offer_qbid, getIntent().getStringExtra("add_custid"));
                        offer_listview.setAdapter(offerPricelistAdapter);
                        CommentsAdapter = new CommentslistAdapter(ProductDetailsActivity.this, true, comment_name, comment_id, comment_uid, comment_price, comment_image, comment_time, comment_phone, comment_qbid, getIntent().getStringExtra("add_custid"));
                        comments_listview.setAdapter(CommentsAdapter);
                    }
                    setListViewHeightBasedOnChildren(comments_listview);
                } catch (JSONException e) {
                    e.printStackTrace();
                    setListViewHeightBasedOnChildren(comments_listview);

                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO Auto-generated method stub
                Log.e("response is:", error.toString());
                setListViewHeightBasedOnChildren(comments_listview);

            }
        });

// Access the RequestQueue through your singleton class.
        AppController.getInstance().addToRequestQueue(jsObjRequest);

    }

    private void offer_price_upload(String price,String cust_id){

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("uploading your price...");
        progressDialog.show();
        String url;


        url = Settings.SERVER_URL + "add-to-offers.php?cust_id="+cust_id+"&add_id="+getIntent().getStringExtra("add_id")+"&price="+price;
        Log.e("url--->", url);
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject jsonObject) {
                if(progressDialog!=null){
                    progressDialog.dismiss();

                    Toast.makeText(ProductDetailsActivity.this,"your offer has been submitted",Toast.LENGTH_SHORT).show();
                    get_product_details(add_id,false);
                }
                Log.e("response is: ", jsonObject.toString());

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO Auto-generated method stub
                Log.e("response is:", error.toString());
                if(progressDialog!=null){
                    progressDialog.dismiss();
                    Toast.makeText(ProductDetailsActivity.this,"There was an eroor please try agian",Toast.LENGTH_SHORT).show();
                }
            }
        });

// Access the RequestQueue through your singleton class.
        AppController.getInstance().addToRequestQueue(jsObjRequest);
    }

    private void make_a_comment(String comment,String cust_id){

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("uploading your comment...");
        progressDialog.show();
        String url = "null";


        try {
            url = Settings.SERVER_URL + "add-comments.php?cust_id="+cust_id+"&item_id="+getIntent().getStringExtra("add_id")+"&comments="+ URLEncoder.encode(comment,"utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        Log.e("url--->", url);
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject jsonObject) {
                if(progressDialog!=null){
                    progressDialog.dismiss();

                    Toast.makeText(ProductDetailsActivity.this,"your comment has been submitted",Toast.LENGTH_SHORT).show();
                    get_product_details(add_id,false);
                }
                Log.e("response is: ", jsonObject.toString());

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO Auto-generated method stub
                Log.e("response is:", error.toString());
                if(progressDialog!=null){
                    progressDialog.dismiss();
                    Toast.makeText(ProductDetailsActivity.this,"There was an error please try agian",Toast.LENGTH_SHORT).show();
                }
            }
        });

// Access the RequestQueue through your singleton class.
        AppController.getInstance().addToRequestQueue(jsObjRequest);
    }
    private void price_area(final ListView scroll)
    {
        scroll.post(new Runnable() {
            @Override
            public void run() {
                //scroll.setSelection(offerPricelistAdapter.getCount()-1);
                scroll.smoothScrollToPosition(offerPricelistAdapter.getCount());
            }
        });
    }
    public static void setListViewHeightBasedOnChildren(ListView listView) {
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
        params.height = (totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1)));
        listView.setLayoutParams(params);
    }
    private void add_to_wishlist(String add_id){

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("adding product to your wishlist...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        String url;
        url = Settings.SERVER_URL + "add-to-wishlist.php?cust_id="+cust_id+"&item_id="+add_id;

        Log.e("url--->", url);
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject jsonObject) {
                if(progressDialog!=null){
                    progressDialog.dismiss();

                    Toast.makeText(ProductDetailsActivity.this,"product added to wishlist",Toast.LENGTH_SHORT).show();

                }
                Log.e("response is: ", jsonObject.toString());

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO Auto-generated method stub
                Log.e("response is:", error.toString());
                if(progressDialog!=null){
                    progressDialog.dismiss();
                    Toast.makeText(ProductDetailsActivity.this,"There was an error please try agian",Toast.LENGTH_SHORT).show();
                }
            }
        });

// Access the RequestQueue through your singleton class.
        AppController.getInstance().addToRequestQueue(jsObjRequest);
    }
    private void remove_from_wishlist(String add_id){

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("adding product to your wishlist...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        String url;
        url = Settings.SERVER_URL + "add-to-wishlist.php?type=remove&cust_id="+cust_id+"&item_id="+add_id;

        Log.e("url--->", url);
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject jsonObject) {
                if(progressDialog!=null){
                    progressDialog.dismiss();

                    Toast.makeText(ProductDetailsActivity.this,"product removed from wishlist",Toast.LENGTH_SHORT).show();

                }
                Log.e("response is: ", jsonObject.toString());

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO Auto-generated method stub
                Log.e("response is:", error.toString());
                if(progressDialog!=null){
                    progressDialog.dismiss();
                    Toast.makeText(ProductDetailsActivity.this,"There was an error please try agian",Toast.LENGTH_SHORT).show();
                }
            }
        });

// Access the RequestQueue through your singleton class.
        AppController.getInstance().addToRequestQueue(jsObjRequest);
    }
    private String get_different_dates(String date) {
        String temp = "";
            String toyBornTime = date;
            SimpleDateFormat dateFormat = new SimpleDateFormat(
            "yyyy-MM-dd HH:mm:ss");

    try {

        Date oldDate = dateFormat.parse(toyBornTime);
        System.out.println(oldDate);

        Date currentDate = new Date();

        long diff = currentDate.getTime() - oldDate.getTime();
        long seconds = diff / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;

        if (oldDate.before(currentDate)) {

            Log.e("oldDate", "is previous date");
            Log.e("Difference: ", " seconds: " + seconds + " minutes: " + minutes
                    + " hours: " + hours + " days: " + days);

        }

        // Log.e("toyBornTime", "" + toyBornTime);
        if(days == 0) {
            if(hours==0) {
                temp = String.valueOf(minutes) + (minutes <= 1 ? " min" : " mins");
            }
            else
                temp = String.valueOf(hours) + (hours == 1 ?" hour":" hours");
        }
              else  if(days<7)
                    temp = String.valueOf(days) + (days==1?" day":" days");
                else if(days < 30)
                    temp = String.valueOf(days/7) + (days/7==1?" week":" weeks");
                else if(days < 365)
                    temp = String.valueOf(days/30) + (days/30==1?" month":" months");
                else
                    temp = String.valueOf(days/365) + (days/365==1?" year":" years");



    } catch (ParseException e) {

        e.printStackTrace();
        }
        return temp +" ago";
    }
    public void delete_offer_from_server(int offerid){
        String url;


        url = Settings.SERVER_URL + "add-to-offers.php?type=remove&offer_id=" + offer_id.get(offerid);

        offer_id.remove(offerid);
        offer_name.remove(offerid);
        offer_price.remove(offerid);
        offer_image.remove(offerid);
        offer_time.remove(offerid);
        offer_phone.remove(offerid);
        offer_qbid.remove(offerid);
        offer_uid.remove(offerid);
        offerPricelistAdapter = new OfferPricelistAdapter(this,true,offer_name,offer_id,offer_uid,offer_price,offer_image,offer_time,offer_phone,offer_qbid,getIntent().getStringExtra("add_custid"));
        offer_listview.setAdapter(offerPricelistAdapter);
        offerPricelistAdapter.notifyDataSetChanged();


        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("deleting the offer....");
        progressDialog.show();
                Log.e("url--->", url);
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject jsonObject) {
                if(progressDialog!=null){
                    progressDialog.dismiss();

                  //  Toast.makeText(ProductDetailsActivity.this,"your offer has been submitted",Toast.LENGTH_SHORT).show();

                }
                Log.e("response is: ", jsonObject.toString());

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO Auto-generated method stub
                Log.e("response is:", error.toString());
                if(progressDialog!=null){
                    progressDialog.dismiss();
                    Toast.makeText(ProductDetailsActivity.this,"There was an eroor please try agian",Toast.LENGTH_SHORT).show();
                }
            }
        });

// Access the RequestQueue through your singleton class.
        AppController.getInstance().addToRequestQueue(jsObjRequest);


    }
    public void delete_comment_from_server(int offerid){
        String url;
        url = Settings.SERVER_URL + "add-comments.php?type=remove&offer_id=" + comment_id.get(offerid);

        comment_id.remove(offerid);
        comment_name.remove(offerid);
        comment_price.remove(offerid);
        comment_image.remove(offerid);
        comment_time.remove(offerid);
        comment_phone.remove(offerid);
        comment_qbid.remove(offerid);
        comment_uid.remove(offerid);
        CommentsAdapter = new CommentslistAdapter(ProductDetailsActivity.this,true,comment_name,comment_id,comment_uid,comment_price,comment_image,comment_time,comment_phone,comment_qbid,getIntent().getStringExtra("add_custid"));
        comments_listview.setAdapter(CommentsAdapter);
        CommentsAdapter.notifyDataSetChanged();


        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("deleting the comment....");
        progressDialog.show();
                Log.e("url--->", url);
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject jsonObject) {
                if(progressDialog!=null){
                    progressDialog.dismiss();

                   // Toast.makeText(ProductDetailsActivity.this,"your offer has been submitted",Toast.LENGTH_SHORT).show();

                }
                Log.e("response is: ", jsonObject.toString());

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO Auto-generated method stub
                Log.e("response is:", error.toString());
                if(progressDialog!=null){
                    progressDialog.dismiss();
                    Toast.makeText(ProductDetailsActivity.this,"There was an eroor please try agian",Toast.LENGTH_SHORT).show();
                }
            }
        });

// Access the RequestQueue through your singleton class.
        AppController.getInstance().addToRequestQueue(jsObjRequest);


    }



}
