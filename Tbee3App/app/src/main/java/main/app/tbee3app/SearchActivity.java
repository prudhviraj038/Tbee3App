package main.app.tbee3app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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
 * Created by Chinni on 01-12-2015.
 */
public class SearchActivity extends Activity {
    private ArrayList<String> product_names, product_ids,product_images,
            product_price,product_desc,product_phone,product_wish_count,product_date,cust_id,cust_qb_id,cust_image,cus_tname;
    ProductlistAdapter productlistAdapter;
    LinearLayout no_products_view;
    ProgressBar progressBar;
    EditText search_edit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Settings.forceRTLIfSupported(this);
        setContentView(R.layout.search_layout);
        ImageView back_btn = (ImageView) findViewById(R.id.back_btn);
        progressBar = (ProgressBar) findViewById(R.id.progressBar3);
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        final GridView productsView = (GridView) findViewById(R.id.gridView2);
        no_products_view = (LinearLayout) findViewById(R.id.no_products_view);
        search_edit = (EditText) findViewById(R.id.search_edit);
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
        productlistAdapter  = new ProductlistAdapter(this,this,false, product_names, product_ids,
                product_images, product_price, product_wish_count, product_date,product_date, product_images);
            productsView.setAdapter(productlistAdapter);
      //  get_products(productlistAdapter, "0", productsView, no_products_view);
        final ImageView search_btn = (ImageView) findViewById(R.id.search_btn);
        search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                if(search_edit.getText().toString()!= "")
                get_products(search_edit.getText().toString(),productlistAdapter, "0", productsView, no_products_view);
            }
        });
        search_edit.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    search_btn.performClick();
                    return true;
                }
                return false;
            }
        });
        productsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent product_details = new Intent(SearchActivity.this, ProductDetailsActivity.class);
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


    }

    void get_products(String keyword,final ProductlistAdapter productlistAdapter,String position, final GridView gridView, final LinearLayout no_products) {
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
        url = Settings.SERVER_URL + "product-json.php?search="+keyword;
        Log.e("url--->", url);
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject jsonObject) {
                Log.e("response is: ", jsonObject.toString());
                progressBar.setVisibility(View.GONE);
                try {
                    JSONArray jsonArray = jsonObject.getJSONArray("products");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject cat = jsonArray.getJSONObject(i);
                        String cat_name = cat.getString("title");
                        String cat_id = cat.getString("id");
                        String cat_price = cat.getString("price");
                        String cat_desc = cat.getString("description");
                        String cat_phone = cat.getString("phone");
                        String cat_wish_count = cat.getString("id");
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
                        no_products.setVisibility(View.GONE);
                    }
                    gridView.setAdapter(productlistAdapter);
                    productlistAdapter.notifyDataSetChanged();

                } catch (JSONException e) {
                    no_products.setVisibility(View.VISIBLE);
                    e.printStackTrace();
                    productlistAdapter.notifyDataSetChanged();

                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO Auto-generated method stub
                Log.e("response is:", error.toString());
                // viewFlipper.setDisplayedChild(0);
                //display_child = 0;
                progressBar.setVisibility(View.GONE);
                no_products.setVisibility(View.VISIBLE);
                productlistAdapter.notifyDataSetChanged();
            }
        });

// Access the RequestQueue through your singleton class.
        AppController.getInstance().addToRequestQueue(jsObjRequest);

    }

}
