package main.app.tbee3app;

/**
 * Created by Chinni on 12-10-2015.
 */

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
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

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.astuetz.PagerSlidingTabStrip;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;

public class MainAwesomeCardFragmentsub extends Fragment {
    FragmentTouchListener mCallback;
    private static final String ARG_catid = "catid";
    private static final String ARG_catname = "catname";
    private static final String ARG_subcount = "catcount";
    private String category_names, category_ids, sub_cat_counts;
    private ArrayList<String> pro_names, pro_ids, pro_count;
    private ArrayList<String> product_names, product_ids,product_images,
            product_price,product_desc,product_phone,product_wish_count,product_date,cust_id,cust_qb_id,cust_image,cus_tname;
    private  boolean loaded = false;
    private ProgressBar pb;
    private PagerSlidingTabStrip tabs;
    private ViewPager pager;
    private MyPagerAdapter adapter;
    GridView listView;
    ProductlistAdapter productlistAdapter;
    GridView productsView;
    LinearLayout no_products_view;
    SwipeRefreshLayout mySwipeRefreshLayout;
    public static MainAwesomeCardFragmentsub newInstance(String cat_names,String cat_ids,String sub_cat_count) {
        MainAwesomeCardFragmentsub f = new MainAwesomeCardFragmentsub();
        Bundle b = new Bundle();
        b.putString(ARG_catname, cat_names);
        b.putString(ARG_catid, cat_ids);
        b.putString(ARG_subcount, sub_cat_count);
        f.setArguments(b);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        category_names = getArguments().getString(ARG_catname);
        category_ids = getArguments().getString(ARG_catid);
        sub_cat_counts = getArguments().getString(ARG_subcount,"0");

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
            }

    @Override
    public void onResume() {
        Log.e("DEBUG", "onResume of fragment"+getArguments().getString(ARG_catname));
        Log.e("DEBUG", "onResume of fragment"+getArguments().getString(ARG_catid));
        Log.e("DEBUG", "onResume of fragment"+getArguments().getString(ARG_subcount));
        super.onResume();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_layout, container, false);

            if( sub_cat_counts.equals("0")){
                Log.e("checked","here from new fragment");
                rootView = inflater.inflate(R.layout.productlist_layout, container, false);
                final GridView productsView = (GridView) rootView.findViewById(R.id.productsView);
                final LinearLayout no_products_view = (LinearLayout) rootView.findViewById(R.id.no_products);
                no_products_view.setVisibility(View.GONE);
                final ProductlistAdapter productlistAdapter = new ProductlistAdapter(getActivity(),getActivity(),false, product_names, product_ids, product_images, product_price, product_wish_count, product_date,product_date, product_images);
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
                    loaded = true;
                }
                else{
                    if(product_ids.size()==0)
                        no_products_view.setVisibility(View.VISIBLE);
                    else
                        no_products_view.setVisibility(View.GONE);
                }


            }

return rootView;

    }



    protected void get_categories(final String parent_id) {
        final ProgressDialog cat_progress_dailog = new ProgressDialog(getActivity());
        cat_progress_dailog.setMessage("please wait....");
        cat_progress_dailog.setCancelable(false);
        pro_names.clear();
        pro_ids.clear();
        pro_count.clear();
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
                    pro_ids.add(category_ids);
                    pro_count.add("0");

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject cat = jsonArray.getJSONObject(i);
                        String cat_name = cat.getString("title");
                        String cat_id = cat.getString("id");
                        String sub_cat_count = cat.getString("substr_cnt");
                        JSONArray attributes = cat.getJSONArray("attributes");
                        pro_names.add(cat_name);
                        pro_ids.add(cat_id);
                        pro_count.add(sub_cat_count);

                        // product_images.add(img_url);
                    }

                    adapter = new MyPagerAdapter(getActivity().getSupportFragmentManager());
                    pager.setAdapter(adapter);
                    tabs.setViewPager(pager);
                    //tabs.setOnPageChangeListener(new CustomOnPageChangeListenner(tabs, adapter.getCount()));

                                  } catch (JSONException e) {
                    e.printStackTrace();

                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO Auto-generated method stub
                Log.e("response is:", error.toString());
                if(cat_progress_dailog!=null)
                    cat_progress_dailog.dismiss();
            }
        });

// Access the RequestQueue through your singleton class.
        AppController.getInstance().addToRequestQueue(jsObjRequest);

    }

    public class MyPagerAdapter extends FragmentPagerAdapter {

        private final Map<Integer,MainAwesomeCardFragmentsub> frags = new Hashtable<>();
        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return pro_names.get(position);
        }

        @Override
        public int getCount() {
            return pro_names.size();
        }

        @Override
        public Fragment getItem(int position) {
            frags.put(position, MainAwesomeCardFragmentsub.newInstance(pro_names.get(position), pro_ids.get(position), pro_count.get(position)));
            return frags.get(position);
        }

        public MainAwesomeCardFragmentsub getthisfrag(int pos){

            return    frags.get(pos);
        }

    }



    private class CustomOnPageChangeListenner implements ViewPager.OnPageChangeListener{

        private PagerSlidingTabStrip tabStrip;
        private int previousPage=0;
        private int count=0;
        //Constructor initiate with TapStrip
        //
        public CustomOnPageChangeListenner(PagerSlidingTabStrip tab,int count){
            tabStrip=tab;
            this.count = count;
            //Set the first image button in tabStrip to selected,

            for (int ta=0;ta<count;ta++)
            {
                ((LinearLayout) tabStrip.getChildAt(0)).getChildAt(ta).setBackgroundResource(R.drawable.shadow_png_new);
            }
            ((LinearLayout)tabStrip.getChildAt(0)).getChildAt(0).setSelected(true);
            ((LinearLayout) tabStrip.getChildAt(0)).getChildAt(0).setBackgroundResource(R.drawable.shadow_png_bend);

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
//            adapter.getthisfrag(i).refresh();
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
        url = Settings.SERVER_URL + "product-json.php?parent_id=l";
        if(position.equals("-2"))
        {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
            String my_id = sharedPreferences.getString("tbee3_user","-1");
                        url = Settings.SERVER_URL + "product-json.php?cust_id="+my_id;
        }
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
                        no_products.setVisibility(View.GONE);
                    }

                    productlistAdapter.notifyDataSetChanged();
                    mySwipeRefreshLayout.setRefreshing(false);

                                   } catch (JSONException e) {
                //    have_products = false;
                  //  no_products.setVisibility(View.VISIBLE);
                    e.printStackTrace();
                    productlistAdapter.notifyDataSetChanged();
                    mySwipeRefreshLayout.setRefreshing(false);

                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO Auto-generated method stub
                Log.e("response is error:", error.toString());

              //  have_products = false;
               // viewFlipper.setDisplayedChild(0);
                //display_child = 0;
                show_error();
                //no_products.setVisibility(View.VISIBLE);
                //productlistAdapter.notifyDataSetChanged();
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
