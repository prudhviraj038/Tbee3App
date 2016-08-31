package main.app.tbee3app;

/**
 * Created by Chinni on 13-05-2015.
 */

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class
        NotifylistAdapter extends BaseAdapter {
    private Activity activity;
    public List<String> mItems = new ArrayList<String>();
    public List<String> mIds = new ArrayList<String>();
    public List<String> mImages = new ArrayList<String>();
    public List<String> mPrices = new ArrayList<String>();
    public List<String> mDates = new ArrayList<String>();
    private static LayoutInflater inflater=null;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    public NotifylistAdapter(Activity activity, List<String> mItems, List<String> mIds, List<String> mPrices, List<String> mImages, List mDates) {

        this.activity = activity;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.mItems=mItems;
        this.mImages=mImages;
        this.mIds=mIds;
        this.mPrices = mPrices;
        this.mDates = mDates;
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


    public View getView(int position, View convertView, ViewGroup parent) {
        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.notifications_preview, null);
        if (imageLoader == null)
            imageLoader = AppController.getInstance().getImageLoader();
        ImageView thumbNail = (ImageView) convertView
                .findViewById(R.id.cust_img);
        Picasso.with(activity).load(mImages.get(position)).placeholder(R.drawable.default_profilepic).into(thumbNail);
        TextView cat_name = (TextView) convertView.findViewById(R.id.offer_tittle);
        cat_name.setText(mItems.get(position));
        TextView cat_price = (TextView) convertView.findViewById(R.id.offer_price_txt);
        cat_price.setText( mPrices.get(position));
        TextView cat_price_date = (TextView) convertView.findViewById(R.id.offer_time_last);
        cat_price_date.setText(get_different_dates(mDates.get(position)));

        return  convertView;
    }

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
            if(days == 0) {
                if(hours==0) {
                    temp = String.valueOf(minutes) + (minutes <= 1 ? " m" : " m");
                }
                else
                    temp = String.valueOf(hours) + (hours == 1 ?" h":" h");
            }
            else if(days<7)
                temp = String.valueOf(days) + (days==1?" d":" d");
            else if(days < 30)
                temp = String.valueOf(days/7) + (days/7==1?" w":" w");
            else if(days < 365)
                temp = String.valueOf(days/30) + (days/30==1?" M":" M");
            else
                temp = String.valueOf(days/365) + (days/365==1?" y":" y");

        } catch (ParseException e) {

            e.printStackTrace();
        }
        return temp;
    }


}
