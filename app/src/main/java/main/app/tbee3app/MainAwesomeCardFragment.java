package main.app.tbee3app;

/**
 * Created by Chinni on 12-10-2015.
 */

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;

public class MainAwesomeCardFragment extends Fragment {
    FragmentTouchListener mCallback;
    private int currentColor = 0xffff2621;
    private String filter_string="-1";
    private static final String ARG_catid = "catid";
    private static final String ARG_catname = "catname";
    private static final String ARG_subcount = "catcount";
    private static final String ARG_filter = "catfilter";
    private String category_names, category_ids, sub_cat_counts,category_filters;
    private ArrayList<String> pro_names, pro_ids, pro_count,pro_filters;
    private ArrayList<String> product_names, product_ids,product_images,
            product_price,product_desc,product_phone,product_wish_count,product_date,cust_id,cust_qb_id,cust_image,cus_tname;
    private  boolean loaded = false;
    private ProgressBar pb;
    private PagerSlidingTabStrip tabs;
    static Boolean isFiltervisible = false;
     Boolean havesubcat = false;
    LinearLayout filtering_options,filtering_options_container;
    private ViewPager pager;
    private MyPagerAdapter adapter;
    GridView listView;
    ProductlistAdapter productlistAdapter;
    GridView productsView;
    LinearLayout no_products_view;
    SwipeRefreshLayout mySwipeRefreshLayout;
    CheckBox new_check,mint_check,used_check,need_repair_check;
    EditText price_from,price_to;
    JSONObject filter_object = new JSONObject();
    public static MainAwesomeCardFragment newInstance(String cat_names, String cat_ids, String sub_cat_count, String jsonArray) {
        MainAwesomeCardFragment f = new MainAwesomeCardFragment();
        Bundle b = new Bundle();
        b.putString(ARG_catname, cat_names);
        b.putString(ARG_catid, cat_ids);
        b.putString(ARG_subcount, sub_cat_count);
        b.putString(ARG_filter, jsonArray);
        f.setArguments(b);
        return f;
    }

    public  void toggle(){
            if (isFiltervisible) {
                filtering_options.setVisibility(View.GONE);
                isFiltervisible = false;
            } else {
                filtering_options.setVisibility(View.VISIBLE);
                isFiltervisible = true;
            }
        if(havesubcat && adapter!=null && adapter.getthisfrag(pager.getCurrentItem())!=null) {
            adapter.getthisfrag(pager.getCurrentItem()).refresh();
            Log.e("size is", String.valueOf(pager.getChildCount()));
        }

                }

    public void refresh() {

            if (isFiltervisible) {
                filtering_options.setVisibility(View.VISIBLE);

            } else {
                filtering_options.setVisibility(View.GONE);

            }

    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Handler handler = new Handler();

        final Runnable r = new Runnable() {
            public void run() {
                if(filtering_options!=null)
                refresh();
                handler.postDelayed(this, 10);
            }
        };

        handler.postDelayed(r, 10);

        category_names = getArguments().getString(ARG_catname);
        category_ids = getArguments().getString(ARG_catid);
        sub_cat_counts = getArguments().getString(ARG_subcount,"0");
        category_filters = getArguments().getString(ARG_filter);

        pro_names = new ArrayList<>();
        pro_ids = new ArrayList<>();
        pro_count = new ArrayList<>();
        pro_filters = new ArrayList<>();
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
        super.onResume();
        refresh();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
                View rootView = inflater.inflate(R.layout.fragment_layout, container, false);
            if( sub_cat_counts.equals("0")){
                havesubcat=false;
                Log.e("checked","here");
                rootView = inflater.inflate(R.layout.productlist_layout, container, false);
                filtering_options = (LinearLayout)rootView.findViewById(R.id.filtering_options);
                filtering_options.setVisibility(View.GONE);
                filtering_options_container = (LinearLayout)rootView.findViewById(R.id.filtering_options_container);
                final TextView filter_apply_btn = (TextView) rootView.findViewById(R.id.filter_apply_btn);
                TextView filter_clear_btn = (TextView) rootView.findViewById(R.id.filter_clear_btn);
                price_from = (EditText) rootView.findViewById(R.id.price_from);
                price_to = (EditText) rootView.findViewById(R.id.price_to);
                new_check = (CheckBox) rootView.findViewById(R.id.check_new);
                mint_check = (CheckBox) rootView.findViewById(R.id.check_mint);
                used_check = (CheckBox) rootView.findViewById(R.id.check_used);
                need_repair_check = (CheckBox) rootView.findViewById(R.id.check_need_repair);

                final ArrayList<ArrayList<CheckBox>> textViewArrayListmain = new ArrayList<>();
                final ArrayList<EditText> textinputfrommain = new ArrayList<>();
                final ArrayList<EditText> textinputtomain = new ArrayList<>();
                filter_clear_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        list_count_start=0;
                        filter_object = new JSONObject();
                        filter_string ="-1";
                        price_from.setText("");
                        price_to.setText("");
                        new_check.setChecked(false);
                        mint_check.setChecked(false);
                        used_check.setChecked(false);
                        need_repair_check.setChecked(false);
                        for(int i=0;i<textViewArrayListmain.size();i++){
                            for (int j = 0; j<textViewArrayListmain.get(i).size(); j++){
                                textViewArrayListmain.get(i).get(j).setChecked(false);
                            }
                        }
                        for(int i=0;i<textinputfrommain.size();i++){
                            textinputfrommain.get(i).setText("");
                            textinputtomain.get(i).setText("");
                        }
                        filter_apply_btn.performClick();
                    }
                });
                filter_apply_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            filter_object = new JSONObject();
                            filter_string = "";
                        String price_from_str,price_to_str,condition_str;
                            ArrayList<String> temp_str_condition = new ArrayList<String>();
                            price_from_str = price_from.getText().toString();
                            price_to_str = price_to.getText().toString();
                            if(!price_from_str.equals("") || !price_to_str.equals(""))
                            filter_object.put("price",price_from_str+","+price_to_str);
                            filter_object.put("category",category_ids);
                            if(new_check.isChecked())
                                temp_str_condition.add("New");
                            if(mint_check.isChecked())
                                temp_str_condition.add("Mint Condition");
                            if(used_check.isChecked())
                                temp_str_condition.add("Used");
                            if(need_repair_check.isChecked())
                                temp_str_condition.add("Need to Fix");

                            condition_str = "";
                            for(int z=0;z<temp_str_condition.size();z++){
                                if(z==0)
                                    condition_str = temp_str_condition.get(z);
                                else
                                    condition_str = condition_str +","+ temp_str_condition.get(z);
                            }
                            filter_object.put("condition", URLEncoder.encode(condition_str,"utf-8"));
                            JSONArray jsonArray = new JSONArray(category_filters);
                            JSONObject attribute_object = new JSONObject();
                            int mul_count=0,num_count=0;
                            for(int z = 0; z<jsonArray.length(); z++)
                            {
                                if(jsonArray.getJSONObject(z).getString("type").equals("Multiple")) {
                                    ArrayList<CheckBox> temp = textViewArrayListmain.get(mul_count);
                                    String temp_str="";
                                    for(int ii = 0;ii<temp.size();ii++){
                                        if(temp.get(ii).isChecked())
                                        if(temp_str.equals("")){
                                            if(!temp.get(ii).getText().toString().equals("none"))
                                            temp_str = temp.get(ii).getText().toString();
                                        }
                                        else{
                                            if(!temp.get(ii).getText().toString().equals("none"))
                                            temp_str = temp_str +","+temp.get(ii).getText().toString();
                                        }

                                    }
                                    attribute_object.put(jsonArray.getJSONObject(z).getString("id"),temp_str);

                                    mul_count++;
                                }
                                if(jsonArray.getJSONObject(z).getString("type").equals("Number")) {

                                    EditText temp_to = textinputfrommain.get(num_count);
                                    EditText temp_from = textinputtomain.get(num_count);
                                    if(!temp_to.getText().toString().equals("") || !temp_to.getText().toString().equals(""))
                                    attribute_object.put(jsonArray.getJSONObject(z).getString("id"),temp_to.getText().toString()+","+temp_from.getText().toString());
                                    num_count++;
                                }
                            }

                           // attribuite_object.put("1","S,M,XL");
                            //attribuite_object.put("7","KILLER,POLO");
                          //  attribuite_object.put("3","20,100");
                           // attribuite_object.put("4","100,200");
                            filter_object.put("attributes",attribute_object);

                            Log.e("filter_string", String.valueOf(filter_object));
                            Log.e("url_encode", URLEncoder.encode(filter_object.toString(), "utf-8"));
                            filter_string = String.valueOf(filter_object);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        list_count_start=0;
                        get_products(productlistAdapter, category_ids, productsView, no_products_view,true);
                        filtering_options.setVisibility(View.GONE);
                        isFiltervisible=false;
                    }
                });

                try {
                    JSONArray jsonArray = new JSONArray(category_filters);

                    for(int i = 0; i<jsonArray.length();i++)
                    {
                        float scale = getResources().getDisplayMetrics().density;
                        int dpAsPixels = (int) (2*scale + 0.5f);
                        if(jsonArray.getJSONObject(i).getString("type").equals("Multiple")) {
                            ArrayList<CheckBox> textViewArrayList = new ArrayList<>();
                            JSONArray values_json = jsonArray.getJSONObject(i).getJSONArray("values");
                         View view =  getActivity().getLayoutInflater().inflate(R.layout.filtering_options_multiple, filtering_options_container, false);
                            LinearLayout filter_sub = (LinearLayout)view.findViewById(R.id.multiple_layout);
                            ArrayList<LinearLayout> linearLayouts = new ArrayList<>();
                            linearLayouts.add(new LinearLayout(getActivity()));
                            for (int j = 0; j < values_json.length(); j++) {
                                textViewArrayList.add(new CheckBox(getActivity()));
                                textViewArrayList.get(j).setText(values_json.getString(j));
                                JSONObject apply_save_filter;
                                if(!filter_string.equals("-1")){
                                    apply_save_filter = new JSONObject(filter_string);
                                    String temp = apply_save_filter.getString(jsonArray.getJSONObject(i).getString("id"));
                                    String string_temp[] = temp.split(",");
                                    for(int kk =0 ; kk<string_temp.length;kk++){
                                        if(string_temp[kk].equals(values_json.getString(j)))
                                            textViewArrayList.get(j).setChecked(true);
                                                                            }

                                }
                                LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.MATCH_PARENT,
                                        LinearLayout.LayoutParams.MATCH_PARENT, 1.0f);
                                textViewArrayList.get(j).setLayoutParams(param);
                                textViewArrayList.get(j).setPadding(dpAsPixels, 0, 0, 0);
                                textViewArrayList.get(j).setTextSize(12);
                                textViewArrayList.get(j).setButtonDrawable(R.drawable.check_box_selector);
                                linearLayouts.get(linearLayouts.size()-1).addView(textViewArrayList.get(j));
                                if((j+1)%3==0){
                                    filter_sub.addView(linearLayouts.get(linearLayouts.size()-1));
                                    linearLayouts.add(new LinearLayout(getActivity()));
                                }
                            }
                            if(linearLayouts.get(linearLayouts.size()-1).getChildCount()>0){
                                while(linearLayouts.get(linearLayouts.size()-1).getChildCount()<3){
                                    textViewArrayList.add(new CheckBox(getActivity()));
                                    textViewArrayList.get(textViewArrayList.size()-1).setText("none");
                                    LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                                            LinearLayout.LayoutParams.MATCH_PARENT,
                                            LinearLayout.LayoutParams.MATCH_PARENT, 1.0f);
                                    textViewArrayList.get(textViewArrayList.size()-1).setLayoutParams(param);
                                    textViewArrayList.get(textViewArrayList.size()-1).setPadding(dpAsPixels, 0, 0, 0);
                                    textViewArrayList.get(textViewArrayList.size()-1).setTextSize(12);
                                    textViewArrayList.get(textViewArrayList.size()-1).setButtonDrawable(R.drawable.check_box_selector);
                                    textViewArrayList.get(textViewArrayList.size()-1).setVisibility(View.INVISIBLE);
                                    linearLayouts.get(linearLayouts.size()-1).addView(textViewArrayList.get(textViewArrayList.size()-1));

                                }

                                filter_sub.addView(linearLayouts.get(linearLayouts.size()-1));
                            }

                            TextView textView = (TextView) view.findViewById(R.id.filter_name);
                            textView.setText(jsonArray.getJSONObject(i).getString(getResources().getString(R.string.zcat_title)));
                            textViewArrayListmain.add(textViewArrayList);
                            filtering_options_container.addView(view);

                        }
                           else if (jsonArray.getJSONObject(i).getString("type").equals("Number")) {

                                View view = getActivity().getLayoutInflater().inflate(R.layout.filter_options_number, filtering_options_container,false);
                                EditText temp_from = (EditText) view.findViewById(R.id.from);
                                EditText temp_to = (EditText) view.findViewById(R.id.to);
                                TextView filter_name = (TextView) view.findViewById(R.id.filter_name);
                                filter_name.setText(jsonArray.getJSONObject(i).getString(getResources().getString(R.string.zcat_title)));
                                if(!filter_string.equals("-1")){
                                    JSONObject jsonObject = new JSONObject(filter_string);
                                    String temp = jsonObject.getString(jsonArray.getJSONObject(i).getString("id"));
                                    if(!temp.equals("")){
                                        String[] separated = temp.split(",");
                                        temp_from.setText(separated[0]); // this will contain "Fruit"
                                        temp_to.setText(separated[1]);
                                    }
                                }
                                textinputfrommain.add(temp_from);
                                textinputtomain.add(temp_to);
                                filtering_options_container.addView(view);
                            }

                                            }

                } catch (JSONException e) {

                    e.printStackTrace();
                }
                // filtering_options.setText("Filtering options for" + category_names);

              //  filtering_options.setVisibility(View.GONE);
                    fragment_progress = (ProgressBar) rootView.findViewById(R.id.fragment_progress);
                 productsView = (GridView) rootView.findViewById(R.id.productsView);
                no_products_view = (LinearLayout) rootView.findViewById(R.id.no_products);
                no_products_view.setVisibility(View.GONE);
                productlistAdapter = new ProductlistAdapter(getActivity(),getActivity(),false, product_names, product_ids, product_images, product_price, product_wish_count, product_date,product_date, product_images);
                mySwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swiperefresh);
                mySwipeRefreshLayout.setOnRefreshListener(
                        new SwipeRefreshLayout.OnRefreshListener() {
                            @Override
                            public void onRefresh() {
                                Log.i("hello", "onRefresh called from SwipeRefreshLayout");

                                // This method performs the actual data-refresh operation.
                                // The method calls setRefreshing(false) when it's finished.
                                list_count_start=0;
                                get_products(productlistAdapter, category_ids, productsView, no_products_view,true);
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
                productsView.setOnScrollListener(new AbsListView.OnScrollListener() {
                    @Override
                    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                        final int lastItem = firstVisibleItem + visibleItemCount;
                        if ((lastItem == totalItemCount)) {
                            if (preLast != lastItem) {
                                if(lastItem%10==0) {
                                    list_count_start = list_count_start + list_count;
                                    Log.d("Last", String.valueOf(lastItem) + "--->" + list_count_start);
                                    get_products(productlistAdapter, category_ids, productsView, no_products_view,false);
                                   // new MyAsyncTask().execute();
                                }
                                // adapter.notifyDataSetChanged();
                                preLast = lastItem;
                            }

                        }
                    }

                    @Override
                    public void onScrollStateChanged(AbsListView view, int scrollState) {

                    }
                });
                    get_products(productlistAdapter, category_ids, productsView, no_products_view,true);
                    loaded = true;


            }
            else {
                havesubcat=true;
                rootView = inflater.inflate(R.layout.product_tab_layout, container, false);
                fragment_progress = (ProgressBar) rootView.findViewById(R.id.fragment_progress);
                    get_categories(category_ids);
                tabs = (PagerSlidingTabStrip)rootView.findViewById(R.id.tabs);
                pager =(ViewPager)rootView.findViewById(R.id.pagermain);
                final int pageMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources()
                        .getDisplayMetrics());
                pager.setPageMargin(pageMargin);
                filtering_options = (LinearLayout)rootView.findViewById(R.id.filtering_options);
            }
return rootView;

    }

    ProgressBar fragment_progress;
    private int preLast;
    int list_count_start = 0;
    int list_count = 10;


    protected void get_categories(final String parent_id) {
        fragment_progress.setVisibility(View.VISIBLE);
        final ProgressDialog cat_progress_dailog = new ProgressDialog(getActivity());
        cat_progress_dailog.setMessage("please wait....");
        cat_progress_dailog.setCancelable(false);
        pro_names.clear();
        pro_ids.clear();
        pro_count.clear();
        pro_filters.clear();
       // cat_progress_dailog.show();
        String url;
        url = Settings.SERVER_URL + "category-json.php?parent_id="+parent_id;
        Log.e("xyz-->cat" + category_names, url);
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject jsonObject) {
                if(cat_progress_dailog!=null)
                    cat_progress_dailog.dismiss();
                Log.e("response is: ", jsonObject.toString());
                fragment_progress.setVisibility(View.GONE);
                try {
                    JSONArray jsonArray = jsonObject.getJSONArray("categories");
                    pro_names.add("All");
                    pro_ids.add(parent_id);
                    pro_count.add("0");
                    pro_filters.add("[]");

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject cat = jsonArray.getJSONObject(i);
                        String cat_name = cat.getString(getResources().getString(R.string.zcat_title));
                        String cat_id = cat.getString("id");
                        String sub_cat_count = String.valueOf(cat.getInt("substr_cnt"));
                        JSONArray attributes = cat.getJSONArray("attributes");
                        pro_names.add(cat_name);
                        pro_ids.add(cat_id);
                        pro_count.add(sub_cat_count);
                        pro_filters.add(attributes.toString());
                        // product_images.add(img_url);
                    }

                    adapter = new MyPagerAdapter(getChildFragmentManager());
                    pager.setAdapter(adapter);
                    tabs.setViewPager(pager);

                    tabs.setIndicatorColor(0xffff2621);
                    tabs.setOnPageChangeListener(new CustomOnPageChangeListenner(tabs, adapter.getCount()));
                    pager.setCurrentItem(0);

                                  } catch (JSONException e) {
                    e.printStackTrace();

                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO Auto-generated method stub
                fragment_progress.setVisibility(View.GONE);
                Log.e("response is:", error.toString());
                if(cat_progress_dailog!=null)
                    cat_progress_dailog.dismiss();
            }
        });

// Access the RequestQueue through your singleton class.
        AppController.getInstance().addToRequestQueue(jsObjRequest);
    }

    public class MyPagerAdapter extends FragmentPagerAdapter {
        private final Map<Integer,MainAwesomeCardFragment> frags = new Hashtable<>();
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
            frags.put(position,MainAwesomeCardFragment.newInstance(pro_names.get(position), pro_ids.get(position), pro_count.get(position), pro_filters.get(position)));
            Log.e("final",pro_names.get(position)+pro_ids.get(position)+ pro_count.get(position) + pro_filters.get(position));
          //  frags.put(position,new TestFragment());
            return frags.get(position);
        }

        public MainAwesomeCardFragment getthisfrag(int pos){

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
            if(adapter.getthisfrag(i)!=null)
            adapter.getthisfrag(i).refresh();
        }

        @Override
        public void onPageScrollStateChanged(int i) {

        }
    }


    protected void get_products(final ProductlistAdapter productlistAdapter,
                                final String position,
                                final GridView gridView,
                                final LinearLayout no_products,
                                final boolean clear_results) {
        String url;
        url = Settings.SERVER_URL + "product-json.php?parent_id="+position;
        if(!filter_string.equals("-1"))
        {
            try {
                url = Settings.SERVER_URL + "product-json.php?parent_id="+position+"&filter="+URLEncoder.encode(filter_string,"utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        if(position.equals("-2"))
        {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
            String my_id = sharedPreferences.getString("tbee3_user","-1");
                        url = Settings.SERVER_URL + "product-json.php?cust_id="+my_id;
        }
        url = url+"&start="+list_count_start+"&count="+list_count;
        Log.e("xyz-->"+category_names, url);

        fragment_progress.setVisibility(View.VISIBLE);
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject jsonObject) {
                Log.e("response is: ", jsonObject.toString());
                fragment_progress.setVisibility(View.GONE);

                if(clear_results)
                {
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
                }
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
                    no_products.setVisibility(View.VISIBLE);
                    fragment_progress.setVisibility(View.GONE);
                    e.printStackTrace();
                    mySwipeRefreshLayout.setRefreshing(false);

                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO Auto-generated method stub
                Log.e("response is error:", error.toString());
                fragment_progress.setVisibility(View.GONE);
              //  have_products = false;
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
