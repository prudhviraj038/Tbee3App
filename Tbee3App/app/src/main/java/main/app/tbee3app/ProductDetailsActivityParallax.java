package main.app.tbee3app;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Chinni on 14-10-2015.
 */
public class ProductDetailsActivityParallax extends AppCompatActivity {
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();
    //ParallaxScrollView parallaxScrollView;
    String phone_str;
    ArrayList<String> offer_id,offer_name,offer_price,offer_image,offer_time;
    OfferPricelistAdapter offerPricelistAdapter;
    ListView offer_listview;
    String add_id,cust_id;
    boolean iswishitem;
    DBhelper db;
    SharedPreferences sharedPref;
    TextView view_count,fav_count,days_ago;
    private View heroImageView;
    private TextView stickyView;
    private View stickyViewSpacer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Settings.forceRTLIfSupported(this);
        setContentView(R.layout.product_detail_view_parallax);
        offer_listview = (ListView) findViewById(R.id.listView);
        heroImageView = findViewById(R.id.heroImageView);
        stickyView = (TextView) findViewById(R.id.stickyView);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View listHeader = inflater.inflate(R.layout.parallax_header, null);
        stickyViewSpacer = listHeader.findViewById(R.id.stickyViewPlaceholder);
        offer_listview.addHeaderView(listHeader);
        offer_listview.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                /* Check if the first item is already reached to top.*/
                if (offer_listview.getFirstVisiblePosition() == 0) {
                    View firstChild = offer_listview.getChildAt(0);
                    int topY = 0;
                    if (firstChild != null) {
                        topY = firstChild.getTop();
                    }

                    int heroTopY = stickyViewSpacer.getTop();
                    stickyView.setY(Math.max(0, heroTopY + topY));

                    /* Set the image to scroll half of the amount that of ListView */
                    heroImageView.setY(topY * 0.5f);
                }
            }
        });





        if (imageLoader == null)
            imageLoader = AppController.getInstance().getImageLoader();
        db = new DBhelper(getApplicationContext());
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        cust_id = sharedPref.getString("tbee3_user", "-1");
        NetworkImageView thumbNail = (NetworkImageView) findViewById(R.id.product_image_detail);
        Log.e("img_url", getIntent().getStringExtra("add_image"));
        thumbNail.setImageUrl(getIntent().getStringExtra("add_image").replaceAll(" ", "%20"), imageLoader);
        //parallaxScrollView = (ParallaxScrollView) findViewById(R.id.scroll);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(0xffff2621));
        TextView description = (TextView) findViewById(R.id.desc);
        description.setText(getIntent().getStringExtra("add_desc"));
        getSupportActionBar().hide();
        view_count = (TextView) findViewById(R.id.view_count);
        fav_count = (TextView) findViewById(R.id.fav_count);
        days_ago = (TextView) findViewById(R.id.days_ago);

        offer_id = new ArrayList<>();
        offer_name = new ArrayList<>();
        offer_price = new ArrayList<>();
        offer_image = new ArrayList<>();
        offer_time = new ArrayList<>();
        offerPricelistAdapter = new OfferPricelistAdapter(this,true,offer_name,offer_id,offer_id,offer_price,offer_image,offer_time,offer_time,offer_time,cust_id);
        offer_listview.setAdapter(offerPricelistAdapter);

        get_product_details(getIntent().getStringExtra("add_id"));
        add_id = getIntent().getStringExtra("add_id");
        TextView make_an_offer = (TextView)findViewById(R.id.make_an_offer);
        phone_str = getIntent().getStringExtra("add_phone");
        Log.e("phone", phone_str);

        make_an_offer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                 cust_id = sharedPref.getString("tbee3_user","-1");
                if(!cust_id.equals("-1")) {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(ProductDetailsActivityParallax.this);
                    alertDialog.setMessage("Enter your offer price");
                    final EditText input = new EditText(ProductDetailsActivityParallax.this);
                    input.setInputType(InputType.TYPE_CLASS_NUMBER);
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.MATCH_PARENT);
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
                                    offer_price_upload(input.getText().toString(),cust_id);
                                }
                            });

                    alertDialog.show();
                }
                else{
                    Intent i = new Intent(ProductDetailsActivityParallax.this,AccountActivity.class);
                    startActivity(i);
                }
            }

        });

        final TextView price_area = (TextView) findViewById(R.id.price_area);
        price_area.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               price_area(offer_listview);
            }
        });

        ImageView call_btn = (ImageView) findViewById(R.id.call_btn);
        call_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + phone_str));
                startActivity(callIntent);

            }
        });

        ImageView sms_btn = (ImageView) findViewById(R.id.msg_btn);
        sms_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sendIntent = new Intent(Intent.ACTION_VIEW);
                sendIntent.setData(Uri.parse("sms:"));
                startActivity(sendIntent);
            }
        });

        ImageView share_btn = (ImageView) findViewById(R.id.share_btn);
        share_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/html");
                sharingIntent.putExtra(Intent.EXTRA_TEXT, Html.fromHtml("<p>This is the text that will be shared.</p>"));
                startActivity(Intent.createChooser(sharingIntent,"Share using"));
            }
        });

        final ImageView wish_btn = (ImageView) findViewById(R.id.wish_btn);

        int wish_count = db.get_wish_item_count(Integer.parseInt(add_id));
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
        final Intent intent = new Intent(ProductDetailsActivityParallax.this,AccountDetailsActivity.class);
        offer_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                intent.putExtra("cust_id", offer_id.get(i));
                startActivity(intent);
            }
        });


    }



    protected void get_product_details(String add_id1) {
        String url;
        url = Settings.SERVER_URL + "product-details-json.php?add_id="+add_id1;
        Log.e("url--->", url);
        offer_id.clear();
        offer_name.clear();
        offer_price.clear();
        offer_image.clear();
        offer_time.clear();
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject jsonObject) {
                Log.e("response is: ", jsonObject.toString());
                try {
                    JSONArray products = jsonObject.getJSONArray("products");
                    JSONObject first_product = products.getJSONObject(0);
                    String view_count_str = first_product.getString("view_count");
                    String wish_count_str = first_product.getString("fav_count");
                    view_count.setText(" "+view_count_str+ " views");
                    fav_count.setText(" "+wish_count_str+ " likes");
                    days_ago.setText(get_different_dates(first_product.getString("date")));
                    JSONArray first_product_offers = first_product.getJSONArray("offer_prices");
                    for(int i = 0;i<first_product_offers.length();i++){
                        Log.e("price", first_product_offers.getJSONObject(i).getString("price"));
                        offer_id.add(first_product_offers.getJSONObject(i).getString("cust_id"));
                        offer_price.add(first_product_offers.getJSONObject(i).getString("price"));
                        offer_name.add(first_product_offers.getJSONObject(i).getString("cust_name"));
                        offer_image.add(first_product_offers.getJSONObject(i).getString("cust_image"));
                        offer_time.add(first_product_offers.getJSONObject(i).getString("times"));
                    }
                    offerPricelistAdapter.notifyDataSetChanged();
                    //setListViewHeightBasedOnChildren(offer_listview);

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

                    Toast.makeText(ProductDetailsActivityParallax.this,"your offer has been submitted",Toast.LENGTH_SHORT).show();
                    get_product_details(add_id);
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
                    Toast.makeText(ProductDetailsActivityParallax.this,"There was an eroor please try agian",Toast.LENGTH_SHORT).show();
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
                offer_listview.setSelection(offerPricelistAdapter.getCount()-1);
                Toast.makeText(getApplicationContext(),"scrolled",Toast.LENGTH_SHORT).show();
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
        params.height = (totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1)))/2;
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

                    Toast.makeText(ProductDetailsActivityParallax.this,"product added to wishlist",Toast.LENGTH_SHORT).show();

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
                    Toast.makeText(ProductDetailsActivityParallax.this,"There was an error please try agian",Toast.LENGTH_SHORT).show();
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

                    Toast.makeText(ProductDetailsActivityParallax.this,"product removed from wishlist",Toast.LENGTH_SHORT).show();

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
                    Toast.makeText(ProductDetailsActivityParallax.this,"There was an error please try agian",Toast.LENGTH_SHORT).show();
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
}
