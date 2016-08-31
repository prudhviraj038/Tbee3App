package main.app.tbee3app;

/**
 * Created by Chinni on 13-05-2015.
 */

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.daimajia.swipe.SimpleSwipeListener;
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.BaseSwipeAdapter;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class
        CommentslistAdapter extends BaseSwipeAdapter {
    private Context mContext;
    public List<String> mItems = new ArrayList<String>();
    public List<String> mIds = new ArrayList<String>();
    public List<String> muIds = new ArrayList<String>();
    public List<String> mImages = new ArrayList<String>();
    public List<String> mPrices = new ArrayList<String>();
    public List<String> mDates = new ArrayList<String>();
    public List<String> mPhone = new ArrayList<String>();
    public List<String> mQbid = new ArrayList<String>();
    public String product_uploader_id;
    public boolean mcandelete;
    private static LayoutInflater inflater=null;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    public CommentslistAdapter(Context activity,boolean candelete, List<String> mItems,
                                 List<String> mIds,List<String> muIds, List<String> mPrices,
                                 List<String> mImages,List mDates,List<String> mPhone,List<String> mQbid,String muploaderid) {

        this.mContext= activity;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.mItems=mItems;
        this.mImages=mImages;
        this.mIds=mIds;
        this.muIds=muIds;
        this.mPrices = mPrices;
        this.mDates = mDates;
        this.mcandelete = candelete;
        this.mPhone = mPhone;
        this.mQbid = mQbid;
        this.product_uploader_id = muploaderid;
    }

    public int getCount() {
        return mItems.size();
    }


    public Object getItem(int position) {
        return position;
    }


    public long getItemId(int position) {
        return position;
    }


    @Override
    public int getSwipeLayoutResourceId(int i) {
        return R.id.swipe;
    }

    @Override
    public View generateView(final int position, ViewGroup viewGroup) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.comments_preview_swipe, null);
        SwipeLayout swipeLayout = (SwipeLayout) v.findViewById(getSwipeLayoutResourceId(position));
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        String my_id = sharedPreferences.getString("tbee3_user", "-1");
        String temp_id = muIds.get(position);
        if (temp_id.length() < 2)
            temp_id = "0" + temp_id;
        Log.e("check", my_id+temp_id);

        swipeLayout.addSwipeListener(new SimpleSwipeListener() {
            @Override
            public void onOpen(SwipeLayout layout) {
                YoYo.with(Techniques.Tada).duration(500).delay(100).playOn(layout.findViewById(R.id.trash));
            }
        });
        swipeLayout.setOnDoubleClickListener(new SwipeLayout.DoubleClickListener() {
            @Override
            public void onDoubleClick(SwipeLayout layout, boolean surface) {
                // Toast.makeText(mContext, "DoubleClick", Toast.LENGTH_SHORT).show();
            }
        });
        v.findViewById(R.id.trash).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(mContext, "click delete", Toast.LENGTH_SHORT).show();
                //  delete_offer_from_server(mIds.get(position));

                if(mContext instanceof ProductDetailsActivity){
                    ((ProductDetailsActivity)mContext).delete_comment_from_server(position);
                }



            }
        });
        v.findViewById(R.id.phone).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + mPhone.get(position)));
                mContext.startActivity(callIntent);

            }
        });
        v.findViewById(R.id.chat).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mQbid.get(position).equals("") || mQbid.get(position) == null) {
                    Toast.makeText(mContext, "Quickblox id not created for this user..", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(mContext, ChatStartActivity.class);
                    intent.putExtra("opponentid", mQbid.get(position));
                    intent.putExtra("mode", "other");
                    mContext.startActivity(intent);
                }


            }
        });

       /* if (my_id.equals(temp_id)) {
            v.findViewById(R.id.trash).setVisibility(View.VISIBLE);
            v.findViewById(R.id.phone).setVisibility(View.GONE);
            v.findViewById(R.id.chat).setVisibility(View.GONE);

        }

        else{
            v.findViewById(R.id.trash).setVisibility(View.GONE);
                    }

        Log.e(my_id, product_uploader_id);
        if(my_id.equals(product_uploader_id))
            v.findViewById(R.id.trash).setVisibility(View.VISIBLE);*/

        if(my_id.equals(product_uploader_id)){
            v.findViewById(R.id.trash).setVisibility(View.VISIBLE);
            if (my_id.equals(temp_id)) {
                v.findViewById(R.id.phone).setVisibility(View.GONE);
                v.findViewById(R.id.chat).setVisibility(View.GONE);
            }

        }
        else
        {
            v.findViewById(R.id.trash).setVisibility(View.GONE);
            if (my_id.equals(temp_id)) {
                v.findViewById(R.id.trash).setVisibility(View.VISIBLE);
                v.findViewById(R.id.phone).setVisibility(View.GONE);
                v.findViewById(R.id.chat).setVisibility(View.GONE);

            }
        }


        return v;
    }

    @Override
    public void fillValues(int position, View convertView) {
        ImageView thumbNail = (ImageView) convertView
                .findViewById(R.id.cust_img);
        Picasso.with(mContext).load(mImages.get(position)).placeholder(R.drawable.default_profilepic).into(thumbNail);
        TextView cat_name = (TextView) convertView.findViewById(R.id.offer_tittle);
        cat_name.setText(mItems.get(position));
        TextView cat_price = (TextView) convertView.findViewById(R.id.offer_price_txt);
        cat_price.setText(mPrices.get(position));
        TextView cat_price_date = (TextView) convertView.findViewById(R.id.offer_time_last);
        cat_price_date.setText(get_different_dates(mDates.get(position)));


    }

  /*  public View getView(int position, View convertView, ViewGroup parent) {
        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.offer_price_preview, null);
        if (imageLoader == null)
            imageLoader = AppController.getInstance().getImageLoader();
        ImageView thumbNail = (ImageView) convertView
                .findViewById(R.id.cust_img);
        Picasso.with(activity).load(mImages.get(position)).placeholder(R.drawable.default_profilepic).into(thumbNail);
        TextView cat_name = (TextView) convertView.findViewById(R.id.offer_tittle);
        cat_name.setText(mItems.get(position));
        TextView cat_price = (TextView) convertView.findViewById(R.id.offer_price_txt);
        cat_price.setText("KD "+ mPrices.get(position));
        TextView cat_price_date = (TextView) convertView.findViewById(R.id.offer_time_last);
        cat_price_date.setText(get_different_dates(mDates.get(position)));
        ImageView delete_btn = (ImageView) convertView.findViewById(R.id.view8);
        if(mcandelete)
            delete_btn.setVisibility(View.VISIBLE);
        return  convertView;
    }
*/

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
            if (days == 0) {
                if (hours == 0) {
                    temp = String.valueOf(minutes) + (minutes <= 1 ? " m" : " m");
                } else
                    temp = String.valueOf(hours) + (hours == 1 ? " h" : " h");
            } else if (days < 7)
                temp = days <= 1 ? "1 d" : String.valueOf(days) + " d";
            else if (days < 365)
                temp = String.valueOf(days / 7) + (days / 7 == 1 ? " w" : " w");
                //else if(days < 365)
                //  temp = String.valueOf(days/30) + (days/30==1?" month":" months");
            else
                temp = String.valueOf(days / 365) + (days / 365 == 1 ? " y" : " y");
            return temp + "";
        } catch (ParseException e) {

            e.printStackTrace();
        }
        return temp + "";

    }

    private void delete_offer_from_server(String offerid){

        final ProgressDialog progressDialog = new ProgressDialog(mContext);
        progressDialog.setMessage("uploading your price...");
        progressDialog.show();
        String url;


        url = Settings.SERVER_URL + "add-to-offers.php?type=remove&offer_id="+offerid;
        Log.e("url--->", url);
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject jsonObject) {
                if(progressDialog!=null){
                    progressDialog.dismiss();

                    Toast.makeText(mContext,"your offer has been submitted",Toast.LENGTH_SHORT).show();

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
                    Toast.makeText(mContext,"There was an eroor please try agian",Toast.LENGTH_SHORT).show();
                }
            }
        });

// Access the RequestQueue through your singleton class.
        AppController.getInstance().addToRequestQueue(jsObjRequest);


    }

}
