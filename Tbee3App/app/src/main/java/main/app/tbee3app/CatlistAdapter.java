package main.app.tbee3app;

/**
 * Created by Chinni on 13-05-2015.
 */

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;

import java.util.ArrayList;
import java.util.List;

public class
        CatlistAdapter extends BaseAdapter {
    private static  int mSelected = -1;
    private Activity activity;
    public List<String> mItems = new ArrayList<String>();
    private static LayoutInflater inflater=null;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    public CatlistAdapter(Activity activity, List<String> mItems,List<String> mIds) {

        this.activity = activity;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.mItems=mItems;

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
            convertView = inflater.inflate(R.layout.cat_preview, null);
        if (imageLoader == null)
            imageLoader = AppController.getInstance().getImageLoader();
        TextView cat_name = (TextView) convertView.findViewById(R.id.cat_tittle);
        cat_name.setText(mItems.get(position));
        if(position == mSelected)
            cat_name.setBackgroundResource(R.drawable.shadow_png_bend);
        else
            cat_name.setBackgroundResource(R.drawable.shadow_png_new);
        return  convertView;
    }

    public void setmSelected(int position){
        mSelected=position;
    }
    public int getmSelected(){
        return mSelected;
    }
}
