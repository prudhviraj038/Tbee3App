package main.app.tbee3app;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.DefaultSliderView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Chinni on 07-12-2015.
 */
public class NotificationActivity extends Activity {
    private ArrayList<String> product_names, product_ids,product_images,
            product_price,product_date,pro_redirect_id;;
    String product_tittle,product_desc,product_phone,product_wish_count,cust_id,cust_qb_id,cust_image,cus_tname,pro_image,pro_price;

    NotifylistAdapter productlistAdapter;
    GridView productsgridview;
    TextView noproducts;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Settings.forceRTLIfSupported(this);
        setContentView(R.layout.notify_tab_layout);
        product_names = new ArrayList<>();
        product_ids = new ArrayList<>();
        product_images = new ArrayList<>();
        product_price = new ArrayList<>();
        product_date = new ArrayList<>();
        pro_redirect_id = new ArrayList<>();

        productlistAdapter = new NotifylistAdapter(this, product_names, product_ids,product_price, product_images, product_date);
        productsgridview = (GridView) findViewById(R.id.wishgridview);
        productsgridview.setAdapter(productlistAdapter);
        noproducts = (TextView) findViewById(R.id.no_products);
        noproducts.setVisibility(View.VISIBLE);
        ImageView search_icon = (ImageView) findViewById(R.id.search_icon);
        search_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                            }
        });
        productsgridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                get_product_details(pro_redirect_id.get(i),i);
            }
        });

       // get_products();
    }
    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first
                get_products();
                }

    protected void get_products() {
        product_names.clear();
        product_ids.clear();
        product_images.clear();
        product_price.clear();
        product_date.clear();
        pro_redirect_id.clear();
        String url;
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
            String my_id = sharedPreferences.getString("tbee3_user","-1");
            url = Settings.SERVER_URL + "notifications.php?cust_id="+my_id;
        Log.e("url--->", url);
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject jsonObject) {
                Log.e("response is: ", jsonObject.toString());
                try {
                    JSONArray jsonArray = jsonObject.getJSONArray("notifications");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject cat = jsonArray.getJSONObject(i);
                        String cat_id = cat.getString("id");
                        String cat_price = cat.getString("message");
                        String redirect_id = cat.getString("redirect_id");

                        cat = cat.getJSONObject("not_from");
                        String cat_date = cat.getString("times");
                        String cat_custid = cat.getString("cust_id");
                        String cat_custname = cat.getString("cust_name");
                        String cat_custqbid = cat.getString("cust_id");
                        String cat_custimage= cat.getString("cust_image");
                        //   JSONArray attributes = cat.getJSONArray("attributes");
                        product_names.add(cat_custname);
                        product_ids.add(cat_id);
                        product_date.add(cat_date);
                        product_images.add(cat_custimage);
                        product_price.add(cat_price);
                        pro_redirect_id.add(redirect_id);

                    }

                    productlistAdapter.notifyDataSetChanged();
                    noproducts.setVisibility(View.GONE);


                } catch (JSONException e) {

                    e.printStackTrace();
                    productlistAdapter.notifyDataSetChanged();
                    noproducts.setVisibility(View.VISIBLE);
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO Auto-generated method stub
                Log.e("response is:", error.toString());
                // viewFlipper.setDisplayedChild(0);
                //display_child = 0;

                productlistAdapter.notifyDataSetChanged();
                noproducts.setVisibility(View.VISIBLE);
            }
        });

// Access the RequestQueue through your singleton class.
        AppController.getInstance().addToRequestQueue(jsObjRequest);

    }

    protected void get_product_details(final String add_id1, final int i) {
        String url;
        url = Settings.SERVER_URL + "product-details-json.php?add_id="+add_id1;
        Log.e("url--->", url);
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject jsonObject) {
                Log.e("response is: ", jsonObject.toString());
                try {
                    JSONArray products = jsonObject.getJSONArray("products");
                    JSONObject first_product = products.getJSONObject(0);

                    product_tittle = first_product.getString("title");
                    pro_price = first_product.getString("price");
                            product_desc = first_product.getString("description");
                            product_phone = first_product.getString("phone");
                            product_wish_count = first_product.getString("fav_count");
                            cust_id = first_product.getString("cust_id");
                            cust_qb_id = first_product.getString("cust_qb_id");
                            cust_image = first_product.getString("cust_image");
                            cus_tname = first_product.getString("cust_name");;
                    Intent product_details = new Intent(NotificationActivity.this, ProductDetailsActivity.class);
                    product_details.putExtra("add_id", add_id1);
                    product_details.putExtra("add_tittle", product_tittle);
                    product_details.putExtra("add_price", pro_price);
                    product_details.putExtra("add_image", pro_image);
                    product_details.putExtra("add_desc", product_desc);
                    product_details.putExtra("add_phone", product_phone);
                    product_details.putExtra("add_custid", cust_id);
                    product_details.putExtra("add_qbid", cust_qb_id);
                    product_details.putExtra("add_custname", cus_tname);
                    product_details.putExtra("add_custimage", cust_image);
                    startActivity(product_details);


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
