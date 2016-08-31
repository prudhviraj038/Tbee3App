package main.app.tbee3app;

/**
 * Created by Chinni on 12-10-2015.
 */

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.astuetz.PagerSlidingTabStrip;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SuperAwesomeCardFragment extends Fragment {
    FragmentTouchListener mCallback;
    private static final String ARG_POSITION = "position";
    private static  String category_names, category_ids, sub_cat_counts;
    static Boolean isFiltervisible = false;
    private ArrayList<String> pro_names, pro_ids, pro_count;

    private ArrayList<String> product_names, product_ids,product_images,
            product_price,product_desc,product_phone,product_wish_count,product_date,cust_id,cust_qb_id,cust_image,cus_tname;
    private int position;
    private  boolean loaded = false;
    private  String last_loaded_id = "";
    private int display_child;
    private boolean have_products,have_categories ;
    private ProgressBar pb;
    int progress_count;

    private PagerSlidingTabStrip tabs;
    private ViewPager pager;
    private MyPagerAdapter adapter;

    TextView refresh_btn , refesh_msg;
    GridView listView;
    ProductlistAdapter productlistAdapter;
    GridView productsView;
    TextView filtering_options;
    LinearLayout no_products_view;
    SwipeRefreshLayout mySwipeRefreshLayout;
    public static SuperAwesomeCardFragment newInstance(int position,String cat_names,String cat_ids,String sub_cat_count) {
        SuperAwesomeCardFragment f = new SuperAwesomeCardFragment();
        category_names = cat_names;
        category_ids = cat_ids;
        sub_cat_counts = sub_cat_count;
        Bundle b = new Bundle();
        b.putInt(ARG_POSITION, position);
        f.setArguments(b);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        position = getArguments().getInt(ARG_POSITION);
        pro_names = new ArrayList<>();
        pro_ids = new ArrayList<>();
        pro_count = new ArrayList<>();
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
        loaded = false;
        display_child = 0;
        have_products = false;
        have_categories = false;
    }

    @Override
    public void onResume() {
        Log.e("DEBUG", "onResume of fragment"+String.valueOf(position));
        super.onResume();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.productlist_layout, container, false);

                Log.e("checked","here");

                final GridView productsView = (GridView) rootView.findViewById(R.id.productsView);
                final LinearLayout no_products_view = (LinearLayout) rootView.findViewById(R.id.no_products);
                no_products_view.setVisibility(View.GONE);
                final ProductlistAdapter productlistAdapter = new ProductlistAdapter(getActivity(),getActivity(),false, product_names, product_ids, product_images, product_price, product_wish_count, product_date,product_date, product_images);
                filtering_options = (TextView)rootView.findViewById(R.id.filtering_options);
                filtering_options.setText("Filtering options for" + category_names);
                filtering_options.setVisibility(View.GONE);

                refresh_btn = (TextView)rootView.findViewById(R.id.refresh_btn);
                refesh_msg = (TextView)rootView.findViewById(R.id.refresh_btn);
                refresh_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(position==0)
                            get_products(productlistAdapter,"l", productsView, no_products_view);
                            else
                        get_products(productlistAdapter, category_ids, productsView, no_products_view);
                    }
                });
                mySwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swiperefresh);
                mySwipeRefreshLayout.setOnRefreshListener(
                        new SwipeRefreshLayout.OnRefreshListener() {
                            @Override
                            public void onRefresh() {
                                Log.i("hello", "onRefresh called from SwipeRefreshLayout");

                                // This method performs the actual data-refresh operation.
                                // The method calls setRefreshing(false) when it's finished.
                                get_products(productlistAdapter, category_ids, productsView, no_products_view);
                            }
                        }
                );

                productsView.setAdapter(productlistAdapter);
                productsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        Intent product_details = new Intent(getActivity(), ProductDetailsActivity.class);
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
                if(!loaded) {
                    get_products(productlistAdapter, category_ids, productsView, no_products_view);
                    Log.e("at_name", category_names);
                    loaded = true;
                }
                else{
                    if(product_ids.size()==0)
                        no_products_view.setVisibility(View.VISIBLE);
                    else
                        no_products_view.setVisibility(View.GONE);
                }



            return rootView;

    }

    public void toggle(){
        if(isFiltervisible) {
            filtering_options.setVisibility(View.GONE);
            isFiltervisible=false;
        }
        else {
            filtering_options.setVisibility(View.VISIBLE);
            isFiltervisible=true;
        }
    }
    public void refresh(){
        if(isFiltervisible) {
            filtering_options.setVisibility(View.VISIBLE);

        }
        else {
            filtering_options.setVisibility(View.GONE);

        }
    }

    protected void get_categories(final String parent_id, final CatlistAdapter catlistAdapter) {
        final ProgressDialog cat_progress_dailog = new ProgressDialog(getActivity());
        cat_progress_dailog.setMessage("please wait....");
        cat_progress_dailog.setCancelable(false);
     //   cat_progress_dailog.show();
        String url;
        url = Settings.SERVER_URL + "category-json.php?parent_id="+parent_id;
        Log.e("url--->", url);
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject jsonObject) {
                if(cat_progress_dailog!=null)
                    cat_progress_dailog.dismiss();
                Log.e("response is: ", jsonObject.toString());
                try {
                    JSONArray jsonArray = jsonObject.getJSONArray("categories");
                    pro_names.add("All");
                    pro_ids.add(parent_id);

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject cat = jsonArray.getJSONObject(i);
                        String cat_name = cat.getString("title");
                        String cat_id = cat.getString("id");
                        JSONArray attributes = cat.getJSONArray("attributes");
                        pro_names.add(cat_name);
                        pro_ids.add(cat_id);
                       // product_images.add(img_url);
                    }
                    catlistAdapter.notifyDataSetChanged();
                    listView.setNumColumns(catlistAdapter.getCount());
                   //  adapter = new MyPagerAdapter(getActivity().getSupportFragmentManager());
                    // adapter.notifyDataSetChanged();
                    get_products(productlistAdapter, pro_ids.get(0), productsView, no_products_view);
                    catlistAdapter.setmSelected(0);
                    catlistAdapter.notifyDataSetChanged();

                    display_child = 0;
                    have_categories = true;
                    if(tabs == null){
                        Log.e("tabs","tabs is null");
                    }
                   // tabs.setOnPageChangeListener(new CustomOnPageChangeListenner(tabs, adapter.getCount()));
                                  } catch (JSONException e) {
                    have_categories = false;
                    e.printStackTrace();

                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO Auto-generated method stub
                Log.e("response is:", error.toString());
                have_categories = false;
                if(cat_progress_dailog!=null)
                    cat_progress_dailog.dismiss();
            }
        });

// Access the RequestQueue through your singleton class.
        AppController.getInstance().addToRequestQueue(jsObjRequest);

    }

    public class MyPagerAdapter extends FragmentPagerAdapter {

        //private final String[] TITLES = { "Categories", "Home", "Top Paid", "Top Free", "Top Grossing", "Top New Paid",
        //      "Top New Free", "Trending" };

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return pro_names.get(position);
        }

        @Override
        public int getCount() {
            return pro_ids.size();
        }

        @Override
        public Fragment getItem(int position) {
            return SuperAwesomeCardFragment.newInstance(position,pro_names.get(position),pro_ids.get(position),pro_count.get(position));
        }

    }
    private class CustomOnPageChangeListenner implements ViewPager.OnPageChangeListener{

        private PagerSlidingTabStrip tabStrip;
        private int previousPage=0;
        private int count=0;
        //Constructor initiate with TapStrip
        //
        public CustomOnPageChangeListenner(PagerSlidingTabStrip tab,int count){
            if(tab == null){
                Log.e("tabs in 123","tabs is null");
            }
            this.tabStrip=tab;
            this.count = count;
            //Set the first image button in tabStrip to selected,
            for (int ta=0;ta<count;ta++)
            {
               // ((LinearLayout) tabStrip.getChildAt(0)).getChildAt(ta).setBackgroundResource(R.drawable.shadow_png_new);
            }
          //  ((LinearLayout)tabStrip.getChildAt(0)).getChildAt(0).setSelected(true);
           // ((LinearLayout) tabStrip.getChildAt(0)).getChildAt(0).setBackgroundResource(R.drawable.shadow_png_bend);

        }
        @Override
        public void onPageScrolled(int i, float v, int i2) {

        }

        @Override
        public void onPageSelected(int i) {


            //set the previous selected page to state_selected = false
            ((LinearLayout)tabStrip.getChildAt(0)).getChildAt(previousPage).setSelected(false);
            //set the selected page to state_selected = true
            ((LinearLayout) tabStrip.getChildAt(0)).getChildAt(i).setSelected(true);
            ((LinearLayout) tabStrip.getChildAt(0)).getChildAt(i).setBackgroundResource(R.drawable.shadow_png_bend);
            ((LinearLayout) tabStrip.getChildAt(0)).getChildAt(previousPage).setBackgroundResource(R.drawable.shadow_png_new );
            //((LinearLayout) tabStrip.getChildAt(0)).getChildAt(i).
            //remember the current page
            previousPage=i;

        }

        @Override
        public void onPageScrollStateChanged(int i) {

        }
    }


    protected void get_products(final ProductlistAdapter productlistAdapter,String position, final GridView gridView, final LinearLayout no_products) {
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
        url = Settings.SERVER_URL + "product-json.php?parent_id="+position;
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
                        display_child = 1;
                        have_products = true;
                        no_products.setVisibility(View.GONE);
                    }

                    productlistAdapter.notifyDataSetChanged();
                    mySwipeRefreshLayout.setRefreshing(false);

                                   } catch (JSONException e) {
                    have_products = false;
                    no_products.setVisibility(View.VISIBLE);
                    e.printStackTrace();
                    productlistAdapter.notifyDataSetChanged();
                    mySwipeRefreshLayout.setRefreshing(false);

                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO Auto-generated method stub
                Log.e("response is:", error.toString());
                have_products = false;
               // viewFlipper.setDisplayedChild(0);
                //display_child = 0;
                show_error();
                no_products.setVisibility(View.VISIBLE);
                productlistAdapter.notifyDataSetChanged();
                mySwipeRefreshLayout.setRefreshing(false);
            }
        });

// Access the RequestQueue through your singleton class.
        AppController.getInstance().addToRequestQueue(jsObjRequest);

    }

    private void show_error(){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
// ...Irrelevant code for customizing the buttons and title
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.error_view, null);
        dialogBuilder.setView(dialogView);
        AlertDialog alertDialog = dialogBuilder.create();
        //alertDialog.show();
    }


    public interface FragmentTouchListener {
        public void update_tabs(int postion);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (FragmentTouchListener) activity;

        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }


}
