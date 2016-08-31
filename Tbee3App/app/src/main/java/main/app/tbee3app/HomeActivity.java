package main.app.tbee3app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.astuetz.PagerSlidingTabStrip;
import com.nullwire.trace.ExceptionHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Map;

/**
 * Created by Chinni on 12-10-2015.
 */
public class HomeActivity extends AppCompatActivity implements SuperAwesomeCardFragment.FragmentTouchListener,
        MainAwesomeCardFragment.FragmentTouchListener,MainAwesomeCardFragmentsub.FragmentTouchListener{

    private final Handler handler = new Handler();

    private PagerSlidingTabStrip tabs;
    private ViewPager pager;
    private MyPagerAdapter adapter;
    ArrayList<String> category_names, category_ids, subcat_count;
    ArrayList<String> category_atrs;

    private Drawable oldBackground = null;
    private int currentColor = 0xffff2621;
    private int blackColor = 0xff000000;

    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Settings.forceRTLIfSupported(this);
        setContentView(R.layout.home_tab_layout);

        ImageView search_icon = (ImageView) findViewById(R.id.search_icon);
        search_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent search = new Intent(HomeActivity.this, SearchActivity.class);
                startActivity(search);
            }
        });
        ImageView filter_icon = (ImageView) findViewById(R.id.filter_icon);
        filter_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent search = new Intent(HomeActivity.this,FilterDetailsActivity.class);
                //startActivity(search);

                adapter.getthisfrag(pager.getCurrentItem()).toggle();
            }
        });

        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        editor = sharedPref.edit();
        ExceptionHandler.register(this, "http://prudhviraj038.16mb.com/server.php");
        ImageView new_post = (ImageView) findViewById(R.id.add_a_product);
        new_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String user_id = sharedPref.getString("tbee3_user","-1");
                if(user_id.equals("-1")){
                    Intent i = new Intent(HomeActivity.this,AccountActivity.class);
                    i.putExtra("goto","post");
                    startActivity(i);
                }
                else {

                    Intent add_a_product = new Intent(HomeActivity.this, CameraActivity.class);
                    startActivity(add_a_product);

                }
            }
        });
        ImageView account_btn = (ImageView) findViewById(R.id.account_btn);
        account_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String user_id = sharedPref.getString("tbee3_user","-1");
                if(user_id.equals("-1")){
                    Intent i = new Intent(HomeActivity.this,AccountActivity.class);

                    i.putExtra("goto","details");
                    startActivity(i);
                }
                else{
                    Intent i = new Intent(HomeActivity.this,AccountDetailsActivity.class);
                    i.putExtra("cust_id",user_id);
                    startActivity(i);
                }

            }
        });

        ImageView pac_btn = (ImageView) findViewById(R.id.package_btn);
        pac_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String user_id = sharedPref.getString("tbee3_user", "-1");
                if (user_id.equals("-1")) {
                    Intent i = new Intent(HomeActivity.this, AccountActivity.class);
                    i.putExtra("goto","wanted");
                    startActivity(i);
                } else {

                    Intent i = new Intent(HomeActivity.this, WantedListActivity.class);
                    i.putExtra("mode", "open");
                    startActivity(i);
                }
            }
        });

        ImageView wish_btn = (ImageView) findViewById(R.id.wish_list_btn);
        wish_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String user_id = sharedPref.getString("tbee3_user", "-1");
                if (user_id.equals("-1")) {
                    Intent i = new Intent(HomeActivity.this, AccountActivity.class);
                    i.putExtra("goto","wish");
                    startActivity(i);
                } else {
                    Intent i = new Intent(HomeActivity.this, WishListActivity.class);
                    startActivity(i);
                }
                            }
        });

        category_names = new ArrayList<>();
        category_ids = new ArrayList<>();
        subcat_count = new ArrayList<>();
        category_atrs = new ArrayList<>();
        get_categories();
        tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        pager = (ViewPager) findViewById(R.id.pager);
                final int pageMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources()
                .getDisplayMetrics());
        pager.setPageMargin(pageMargin);
        // pager.setCurrentItem(3);
        tabs.setAllCaps(false);
        tabs.setDividerColor(0xff000000);
        tabs.setIndicatorColor(0xffff2621);

      //  changeColor(currentColor);
        getSupportActionBar().hide();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
       // getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

  //          case R.id.action_contact:
    //            QuickContactFragment dialog = new QuickContactFragment();
      //
      //          dialog.show(getSupportFragmentManager(), "QuickContactFragment");
        //        return true;

        }

        return super.onOptionsItemSelected(item);
    }

    private void changeColor(int newColor) {



        // change ActionBar color just if an ActionBar is available
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {

            Drawable colorDrawable = new ColorDrawable(newColor);
            Drawable bottomDrawable = getResources().getDrawable(R.drawable.abc_action_bar_item_background_material,null);
            LayerDrawable ld = new LayerDrawable(new Drawable[] { colorDrawable, bottomDrawable });

            if (oldBackground == null) {

                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    ld.setCallback(drawableCallback);
                } else {
                    getSupportActionBar().setBackgroundDrawable(ld);
                }

            } else {

                TransitionDrawable td = new TransitionDrawable(new Drawable[] { oldBackground, ld });

                // workaround for broken ActionBarContainer drawable handling on
                // pre-API 17 builds
                // https://github.com/android/platform_frameworks_base/commit/a7cc06d82e45918c37429a59b14545c6a57db4e4
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    td.setCallback(drawableCallback);
                } else {
                    getActionBar().setBackgroundDrawable(td);
                }

                td.startTransition(200);

            }

            oldBackground = ld;

            // http://stackoverflow.com/questions/11002691/actionbar-setbackgrounddrawable-nulling-background-from-thread-handler
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayShowTitleEnabled(true);

        }

        currentColor = newColor;

    }

    public void onColorClicked(View v) {

        int color = Color.parseColor(v.getTag().toString());
        changeColor(color);

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("currentColor", currentColor);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        currentColor = savedInstanceState.getInt("currentColor");
        //changeColor(currentColor);
    }

    private Drawable.Callback drawableCallback = new Drawable.Callback() {
        @Override
        public void invalidateDrawable(Drawable who) {
            getActionBar().setBackgroundDrawable(who);
        }

        @Override
        public void scheduleDrawable(Drawable who, Runnable what, long when) {
            handler.postAtTime(what, when);
        }

        @Override
        public void unscheduleDrawable(Drawable who, Runnable what) {
            handler.removeCallbacks(what);
        }
    };

    @Override
    public void update_tabs(int postion) {
        Log.e("selected_position",String.valueOf(postion));
               // ((LinearLayout) tabs.getChildAt(0)).getChildAt(postion).setSelected(true);
        //pager.setCurrentItem(postion,true);
    }

    public class MyPagerAdapter extends FragmentPagerAdapter {

        //private final String[] TITLES = { "Categories", "Home", "Top Paid", "Top Free", "Top Grossing", "Top New Paid",
          //      "Top New Free", "Trending" };
        private final Map<Integer,MainAwesomeCardFragment> frags = new Hashtable<>();
        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return category_names.get(position);
        }

        @Override
        public int getCount() {
            return category_names.size();
        }

        @Override
        public Fragment getItem(int position) {
            frags.put(position,MainAwesomeCardFragment.newInstance(category_names.get(position),
                    category_ids.get(position), subcat_count.get(position), category_atrs.get(position)));
            //frags.put(position,new TestFragment());
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

    protected void get_categories() {

        String url;
        url = Settings.SERVER_URL + "category-json.php";
        Log.e("url--->", url);
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject jsonObject) {
                Log.e("response is: ", jsonObject.toString());
                try {
                    JSONArray jsonArray = jsonObject.getJSONArray("categories");
                    category_names.add(getResources().getString(R.string.zlatest));
                    category_ids.add("l");
                    category_atrs.add("[]");
                    subcat_count.add("0");

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject cat = jsonArray.getJSONObject(i);
                        String cat_name = cat.getString(getResources().getString(R.string.zcat_title));
                        String cat_id = cat.getString("id");
                        String sub_cat_count = cat.getString("substr_cnt");
                        JSONArray attributes = cat.getJSONArray("attributes");
                        Log.e("cat_name", cat_name + "--" + cat_id + "--" + sub_cat_count);
                        category_names.add(cat_name);
                        category_ids.add(cat_id);
                        category_atrs.add(attributes.toString());
                        subcat_count.add(sub_cat_count);
                    }
                    adapter = new MyPagerAdapter(getSupportFragmentManager());
                    pager.setAdapter(adapter);
                    tabs.setViewPager(pager);
                    tabs.setOnPageChangeListener(new CustomOnPageChangeListenner(tabs, adapter.getCount()));
                  //  update_tabs(1);
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
