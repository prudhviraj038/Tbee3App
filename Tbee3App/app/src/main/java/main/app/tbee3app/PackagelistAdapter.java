package main.app.tbee3app;

/**
 * Created by Chinni on 13-05-2015.
 */

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.ArrayList;

public class
        PackagelistAdapter extends BaseAdapter {
    private Context mContext;
    String selected_id = "fsdf";
    private Activity activity;
    public  ArrayList<String> id,name,no_of_posts,no_of_images,gallery_option,pinned,social_media,instagram,featured,price_option,price;
    public  String user_pack_id;
    private static LayoutInflater inflater=null;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    public PackagelistAdapter(Activity activity,Context context,ArrayList<String> id,ArrayList<String> name,ArrayList<String> no_of_posts,ArrayList<String> no_of_images,ArrayList<String> gallery_option,
                              ArrayList<String> pinned, ArrayList<String> social_media,
                              ArrayList<String> instagram,ArrayList<String> featured,
                              ArrayList<String> price_option,String user_pack_id,ArrayList<String> price) {

        this.activity = activity;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                this.id = id;
                this.name = name;
                this.price = price;
                this.no_of_posts = no_of_posts;
                this.no_of_images = no_of_images;
                this.gallery_option = gallery_option;
                this.pinned = pinned;
                this.social_media = social_media;
                this.instagram = instagram;
                this.featured = featured;
                this.price_option = price_option;
                this.mContext=context;
                this.user_pack_id = user_pack_id;
                selected_id = user_pack_id;

    }

    public int getCount() {
        return id.size();
    }


    public Object getItem(int position) {
        return position;
    }


    public long getItemId(int position) {
        return position;
    }


    public View getView(final int position, View convertView, ViewGroup parent) {
        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.package_preview, null);
        if (imageLoader == null)
            imageLoader = AppController.getInstance().getImageLoader();

        TextView name_tx,no_of_posts_tx,no_of_images_tx,gallery_option_tx,pinned_tx,social_media_tx,instagram_tx,featured_tx,price_option_tx,price_tx;

        name_tx = (TextView)convertView.findViewById(R.id.package_tittle);
        price_tx = (TextView)convertView.findViewById(R.id.package_price);
        no_of_posts_tx = (TextView)convertView.findViewById(R.id.no_of_posts);
        no_of_images_tx = (TextView)convertView.findViewById(R.id.no_of_pictures);
        gallery_option_tx = (TextView)convertView.findViewById(R.id.gallery_permission);
        pinned_tx = (TextView)convertView.findViewById(R.id.pinned_or_special);
        social_media_tx = (TextView)convertView.findViewById(R.id.social_media);
        instagram_tx = (TextView)convertView.findViewById(R.id.instagram_posts);
        featured_tx = (TextView)convertView.findViewById(R.id.featured_add);
        price_option_tx = (TextView)convertView.findViewById(R.id.price_of_product);


        name_tx.setText(name.get(position));
        price_tx.setText("KD "+price.get(position));
        no_of_posts_tx.setText(no_of_posts.get(position));
        no_of_images_tx.setText(no_of_images.get(position));
        gallery_option_tx.setText(gallery_option.get(position).equals("0")?"Not Allowed":"Allowed");
        pinned_tx.setText(pinned.get(position));
        social_media_tx.setText(social_media.get(position).equals("0")?"Not Allowed":"Allowed");
        instagram_tx.setText(instagram.get(position));
        featured_tx.setText(featured.get(position).equals("0")?"Not Allowed":"Allowed");
        price_option_tx.setText(price_option.get(position).equals("0") ? "Not Allowed" : "Allowed");

        TextView buy_now_btn = (TextView) convertView.findViewById(R.id.buy_now_btn);
        if(selected_id.equals(id.get(position))) {
            buy_now_btn.setText("Activated");
            buy_now_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //subscribe_to_package(id.get(position));
                }
            });
        }
        else {
            buy_now_btn.setText("Buy Now");
            buy_now_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mContext instanceof PackageActivity){
                        ((PackageActivity)mContext).start_payment_activity(id.get(position),price.get(position));
                    }


                }
            });
        }
        return  convertView;
    }

    private void subscribe_to_package(final String pack_id){
        final ProgressDialog progressDialog = new ProgressDialog(activity);
        progressDialog.setMessage("please wait.....");
        progressDialog.show();
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(activity);
        String cust_id = sharedPref.getString("tbee3_user", "-1");
        String url = null;
        url = Settings.SERVER_URL+"package_subscribe.php?cust_id="+cust_id+"&package_id="+pack_id;
        Log.e("set_url", url);
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, url,null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject jsonArray) {
                Log.e("response is: ", jsonArray.toString());
                if(progressDialog!=null)
                    progressDialog.dismiss();
                try {

                    selected_id = pack_id;
                    notifyDataSetChanged();
                }catch (Exception e){
                    e.printStackTrace();
                }


            }


        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO Auto-generated method stub
                Log.e("response is:",error.toString());
                if(progressDialog!=null)
                    progressDialog.dismiss();
            }
        });

// Access the RequestQueue through your singleton class.
        AppController.getInstance().addToRequestQueue(jsObjRequest);





    }
    public void setUser_pack_id(String user_pack_id){
        selected_id = user_pack_id;
        notifyDataSetChanged();
    }

}
