package main.app.tbee3app;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
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
public class WantedListActivity extends Activity {
    private ArrayList<String> product_names, product_ids,product_images,
            product_price_from,product_price_to,product_desc,product_phone,product_wish_count,product_date,cust_id,cust_qb_id,cust_image,cus_tname;
    WantedlistAdapter productlistAdapter;
    GridView productsgridview;
    TextView noproducts;
    @Override
    public void onResume() {
        super.onResume();
        get_products();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Settings.forceRTLIfSupported(this);
        setContentView(R.layout.wanted_tab_layout);
        product_names = new ArrayList<>();
        product_ids = new ArrayList<>();
        product_images = new ArrayList<>();
        product_price_from = new ArrayList<>();
        product_price_to = new ArrayList<>();
        product_desc = new ArrayList<>();
        product_phone = new ArrayList<>();
        product_wish_count = new ArrayList<>();
        product_date = new ArrayList<>();
        cust_id = new ArrayList<>();
        cust_image = new ArrayList<>();
        cust_qb_id = new ArrayList<>();
        cus_tname = new ArrayList<>();
        productlistAdapter = new WantedlistAdapter(this,this,false, product_names, product_ids,cust_id,
                product_images, product_price_from,product_price_to, product_wish_count, product_date, product_date);
        productsgridview = (GridView) findViewById(R.id.wishgridview);
        productsgridview.setAdapter(productlistAdapter);
        noproducts = (TextView) findViewById(R.id.no_products);
        noproducts.setVisibility(View.VISIBLE);

        ImageView back_btn = (ImageView) findViewById(R.id.back_btn);
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        TextView add_btn = (TextView) findViewById(R.id.add_wanted_list);
        add_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent wanted = new Intent(WantedListActivity.this,WantedActivity.class);
                startActivity(wanted);
            }
        });

    }

    protected void get_products() {
        product_names.clear();
        product_ids.clear();
        product_images.clear();
        product_price_from.clear();
        product_price_to.clear();
        product_desc.clear();
        product_phone.clear();
        product_wish_count.clear();
        product_date.clear();
        cust_id.clear();
        cust_image.clear();
        cust_qb_id.clear();
        cus_tname.clear();
        SharedPreferences sharedPreferences;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String cust_ids = sharedPreferences.getString("tbee3_user", "-1");
        String url;
                        url = Settings.SERVER_URL + "wanted.php?cust_id="+cust_ids;
        Log.e("url--->", url);
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject jsonObject) {
                Log.e("response is: ", jsonObject.toString());
                try {
                    JSONArray jsonArray = jsonObject.getJSONArray("wanted");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject cat = jsonArray.getJSONObject(i);
                        String cat_name = cat.getString("title");
                        String cat_id = cat.getString("id");
                        String cat_price_from = cat.getString("price_from");
                        String cat_price_to = cat.getString("price_from");
                        String cat_desc = cat.getString("key_words");


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
                        product_price_from.add(cat_price_from);
                        product_price_to.add(cat_price_to);
                        product_desc.add(cat_desc);
                        product_phone.add("0");
                        product_wish_count.add(cat_desc);
                        product_date.add(cat_date);
                        cust_id.add(cat_custid);
                        cust_image.add(cat_custimage);
                        cust_qb_id.add(cat_custqbid);
                        cus_tname.add(cat_custname);
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
    public void edit_wanted_product(int position){
        Intent intent = new Intent(WantedListActivity.this,WantedEditActivity.class) ;
        intent.putExtra("tittle",product_names.get(position));
        intent.putExtra("desc",product_desc.get(position));
        intent.putExtra("price_from",product_price_from.get(position));
        intent.putExtra("price_to",product_price_to.get(position));
        intent.putExtra("wanted_id",product_ids.get(position));
        startActivity(intent);
    }


}
