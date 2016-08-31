package main.app.tbee3app;

/**
 * Created by Chinni on 13-05-2015.
 */

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class
        ProductlistAdapter extends BaseAdapter {
    DBhelper dBhelper;
    private Activity activity;
    public List<String> mItems = new ArrayList<String>();
    public List<String> mIds = new ArrayList<String>();
    public List<String> mImages = new ArrayList<String>();
    public List<String> mPrices = new ArrayList<String>();
    public List<String> mWishcount = new ArrayList<String>();
    public List<String> mDates = new ArrayList<String>();
    public List<String> mDatesexp = new ArrayList<String>();
    public List<String> product_status = new ArrayList<String>();
    public  boolean showdays=false;
    public boolean editable;
    int current_position=0;
    private Context mContext;
    private static LayoutInflater inflater=null;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();
    public  void setshowdays(boolean showval)
    {
        showdays = showval;
    }
    public ProductlistAdapter(Activity activity, Context context, boolean editable, List<String> mItems, List<String> mIds,
                              List<String> mImages, List<String> mPrices, List<String> mWishcount,
                              List<String> mDates, List<String> mDatesexp, ArrayList<String> product_status) {

        this.activity = activity;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.mItems=mItems;
        this.mImages=mImages;
        this.mIds=mIds;
        this.mPrices = mPrices;
        this.mWishcount = mWishcount;
        this.mDates = mDates;
        this.mDatesexp = mDatesexp;
        this.editable=editable;
        this.mContext=context;
        this.product_status=product_status;
        dBhelper = new DBhelper(context);
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


    public View getView(final int position, View convertView, final ViewGroup parent) {
        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.product_preview, null);
        if (imageLoader == null)
            imageLoader = AppController.getInstance().getImageLoader();
        ImageView thumbNail = (ImageView) convertView
                .findViewById(R.id.product_img);
        Picasso.with(activity).load(mImages.get(position).replaceAll(" ", "%20")).fit().placeholder(R.drawable.splash_logo).into(thumbNail);
        TextView cat_name = (TextView) convertView.findViewById(R.id.product_tittle);
        cat_name.setText(mItems.get(position));
        TextView cat_price = (TextView) convertView.findViewById(R.id.product_price);
        cat_price.setText("KD "+ mPrices.get(position));
        TextView cat_wish_count = (TextView) convertView.findViewById(R.id.wish_count);
        cat_wish_count.setText(mWishcount.get(position));
        ImageView  wish_sym = (ImageView) convertView.findViewById(R.id.wish_symbol);
        int userWished = dBhelper.get_wish_item_count(Integer.parseInt(mIds.get(position)));
        if(userWished!=0)
            wish_sym.setImageResource(R.drawable.heart_white_red);
        else
            wish_sym.setImageResource(R.drawable.chat_bar_heart_red);
        TextView cat_last_date = (TextView) convertView.findViewById(R.id.product_time_last);
        cat_last_date.setText(get_different_dates(mDates.get(position),false));
        LinearLayout edit_options = (LinearLayout) convertView.findViewById(R.id.edit_options);
        TextView daysleft = (TextView) convertView.findViewById(R.id.days_left);
        if(product_status.get(position).equals("3"))
            daysleft.setText(activity.getResources().getString(R.string.zsold));
        else
            daysleft.setText(get_different_dates(mDatesexp.get(position),true));
        TextView edit_product = (TextView) convertView.findViewById(R.id.edit_product);
        TextView del_product = (TextView) convertView.findViewById(R.id.delete_product);
        TextView sold_product = (TextView) convertView.findViewById(R.id.sold_product);

        if(showdays)
            daysleft.setVisibility(View.VISIBLE);
        else
        daysleft.setVisibility(View.GONE);
        if(editable){
            edit_product.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mContext instanceof AccountDetailsActivity){
                        ((AccountDetailsActivity)mContext).edit_product(position);
                    }
                }
            });

            del_product.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                    builder.setMessage("Are you sure you want to delete?")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                   // MyActivity.this.finish();
                                    //mItems.remove(position);
                                  //  mImages.remove(position);
                                   // mIds.remove(position);
                                   // mDates.remove(position);
                                   // mPrices.remove(position);
                                   // mWishcount.remove(position);
                                   // notifyDataSetInvalidated();
                                    if(mContext instanceof AccountDetailsActivity){
                                        ((AccountDetailsActivity)mContext).updatelist(position);
                                    }
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();


                }
            });
            sold_product.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mContext instanceof AccountDetailsActivity){
                        ((AccountDetailsActivity)mContext).update_sold(position);
                    }
                }
            });
        }
        else{
            edit_options.setVisibility(View.GONE);
        }

        return  convertView;
    }

    private String get_different_dates(String date,Boolean exp) {
        String temp = "";
        String toyBornTime = date;
        SimpleDateFormat dateFormat;
        if(exp)
            dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            else
            dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        try {

            Date oldDate = dateFormat.parse(toyBornTime);
            System.out.println(oldDate);

            Date currentDate = new Date();
            long diff;
            if(exp)
                 diff = oldDate.getTime() - currentDate.getTime();
            else
                diff = currentDate.getTime() - oldDate.getTime();
            long seconds = diff / 1000;
            long minutes = seconds / 60;
            long hours = minutes / 60;
            long days = hours / 24;

            if (oldDate.before(currentDate)) {

               // Log.e("oldDate", "is previous date");
                //Log.e("Difference: ", " seconds: " + seconds + " minutes: " + minutes
                  //      + " hours: " + hours + " days: " + days);

            }

        if(exp) {
            //if(days == 0) {
              //  return "1" + activity.getResources().getString(R.string.days_left);
                //            }
            //else
              //  return days +" "+activity.getResources().getString(R.string.days_left);
            if((hours % 24)>0)
                days=days+1;
            days = days+1;
            return days +" "+activity.getResources().getString(R.string.days_left);
        }

            else {
            if(days == 0) {
                if(hours==0) {
                    temp = String.valueOf(minutes) + (minutes <= 1 ? " m" : " m");
                }
                else
                    temp = String.valueOf(hours) + (hours == 1 ?" h":" h");
            }
            else if(days<7)
                temp = days<=1? "1 d": String.valueOf(days)+" d";
            else if(days < 365)
                temp = String.valueOf(days/7) + (days/7==1?" w":" w");
                //else if(days < 365)
                //  temp = String.valueOf(days/30) + (days/30==1?" month":" months");
            else
                temp = String.valueOf(days/365) + (days/365==1?" y":" y");
            return temp + "";
        }
        }catch (ParseException e) {

            e.printStackTrace();
        }
        return temp + "";
    }


}
