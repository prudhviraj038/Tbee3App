package main.app.tbee3app;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Chinni on 07-12-2015.
 */
public class WishListActivity extends Activity {
    private ArrayList<String> product_names, product_ids,product_images,
            product_price,product_desc,product_phone,product_wish_count,product_date,cust_id,cust_qb_id,cust_image,cus_tname;
    ProductlistAdapter productlistAdapter;
    GridView productsgridview;
    TextView noproducts;
    SwipeRefreshLayout mySwipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Settings.forceRTLIfSupported(this);
        setContentView(R.layout.wish_tab_layout);
        product_names = new ArrayList<>();
        product_ids = new ArrayList<>();
        product_images = new ArrayList<>();
        product_price = new ArrayList<>();
        product_desc = new ArrayList<>();
        product_phone = new ArrayList<>();
        product_wish_count = new ArrayList<>();
        product_date = new ArrayList<>();
        cust_id = new ArrayList<>();
        cust_image = new ArrayList<>();
        cust_qb_id = new ArrayList<>();
        cus_tname = new ArrayList<>();
         mySwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
        mySwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        Log.i("hello", "onRefresh called from SwipeRefreshLayout");

                        // This method performs the actual data-refresh operation.
                        // The method calls setRefreshing(false) when it's finished.
                        get_products();
                    }
                }
        );
        productlistAdapter = new ProductlistAdapter(this,this,false, product_names, product_ids,
                product_images, product_price, product_wish_count, product_date, product_date, product_images);
        productsgridview = (GridView) findViewById(R.id.wishgridview);
        productsgridview.setAdapter(productlistAdapter);
        noproducts = (TextView) findViewById(R.id.no_products);
        noproducts.setVisibility(View.VISIBLE);
        ImageView search_icon = (ImageView) findViewById(R.id.search_icon);
        search_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent search = new Intent(WishListActivity.this, SearchActivity.class);
                startActivity(search);
            }
        });
        productsgridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent product_details = new Intent(WishListActivity.this, ProductDetailsActivity.class);
                product_details.putExtra("add_id", product_ids.get(i));
                product_details.putExtra("add_tittle", product_names.get(i));
                product_details.putExtra("add_price", product_price.get(i));
                product_details.putExtra("add_image", product_images.get(i));
                product_details.putExtra("add_desc", product_desc.get(i));
                product_details.putExtra("add_phone", product_phone.get(i));
                product_details.putExtra("add_custid", cust_id.get(i));
                product_details.putExtra("add_qbid", cust_qb_id.get(i));
                product_details.putExtra("add_custname", cus_tname.get(i));
                product_details.putExtra("add_custimage", cust_image.get(i));
                startActivity(product_details);

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
        product_desc.clear();
        product_phone.clear();
        product_wish_count.clear();
        product_date.clear();
        cust_id.clear();
        cust_image.clear();
        cust_qb_id.clear();
        cus_tname.clear();
        String url;
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
            String my_id = sharedPreferences.getString("tbee3_user","-1");
            url = Settings.SERVER_URL + "product-json.php?cust_id="+my_id;
        Log.e("url--->", url);
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject jsonObject) {
                Log.e("response is: ", jsonObject.toString());
                try {
                    JSONArray jsonArray = jsonObject.getJSONArray("products");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject cat = jsonArray.getJSONObject(i);
                        String cat_name = cat.getString("title");
                        String cat_id = cat.getString("id");
                        String cat_price = cat.getString("price");
                        String cat_desc = cat.getString("description");
                        String cat_phone = cat.getString("phone");
                        String cat_wish_count = cat.getString("fav_count");
                        String cat_date = cat.getString("date");
                        String cat_custid = cat.getString("cust_id");
                        String cat_custname = cat.getString("cust_name");
                        String cat_custqbid = cat.getString("cust_qb_id");
                        String cat_custimage= cat.getString("cust_image");
                        //   JSONArray attributes = cat.getJSONArray("attributes");
                        JSONArray images = cat.getJSONArray("images");


                        String img_url="";
                        for(int j=0;j<images.length();j++)
                        {
                            img_url = images.getJSONObject(j).getString("image");
                        }
                        Log.e("cat_name", cat_name + "--" + cat_id);
                        product_names.add(cat_name);
                        product_ids.add(cat_id);
                        product_images.add(img_url);
                        product_price.add(cat_price);
                        product_desc.add(cat_desc);
                        product_phone.add(cat_phone);
                        product_wish_count.add(cat_wish_count);
                        product_date.add(cat_date);
                        cust_id.add(cat_custid);
                        cust_image.add(cat_custimage);
                        cust_qb_id.add(cat_custqbid);
                        cus_tname.add(cat_custname);
                    }

                    productlistAdapter.notifyDataSetChanged();
                    noproducts.setVisibility(View.GONE);
                   mySwipeRefreshLayout.setRefreshing(false);

                } catch (JSONException e) {

                    e.printStackTrace();
                    productlistAdapter.notifyDataSetChanged();
                    noproducts.setVisibility(View.VISIBLE);
                    mySwipeRefreshLayout.setRefreshing(false);
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
                mySwipeRefreshLayout.setRefreshing(false);
            }
        });

// Access the RequestQueue through your singleton class.
        AppController.getInstance().addToRequestQueue(jsObjRequest);

    }

}
