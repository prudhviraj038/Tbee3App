package main.app.tbee3app;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.view.View;

import java.util.Locale;

/**
 * Created by Chinni on 22-06-2015.
 */
public class Settings {
   public static final String SERVER_URL    = "http://hatbee3.com/api/";

   public static   void forceRTLIfSupported(Activity activity)
   {
      SharedPreferences sharedPref;
      sharedPref = PreferenceManager.getDefaultSharedPreferences(activity);
      if (sharedPref.getString("lan", "-1").equals("en")) {
         Resources res = activity.getResources();
         // Change locale settings in the app.
         DisplayMetrics dm = res.getDisplayMetrics();
         android.content.res.Configuration conf = res.getConfiguration();
         conf.locale = new Locale("en".toLowerCase());
         res.updateConfiguration(conf, dm);
      }

      else if(sharedPref.getString("lan", "-1").equals("ar")){
         Resources res = activity.getResources();
         // Change locale settings in the app.
         DisplayMetrics dm = res.getDisplayMetrics();
         android.content.res.Configuration conf = res.getConfiguration();
         conf.locale = new Locale("ar".toLowerCase());
         res.updateConfiguration(conf, dm);
         if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1){
            activity.getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
         }
      }

      else {
         Resources res = activity.getResources();
         // Change locale settings in the app.
         DisplayMetrics dm = res.getDisplayMetrics();
         android.content.res.Configuration conf = res.getConfiguration();
         conf.locale = new Locale("en".toLowerCase());
         res.updateConfiguration(conf, dm);
      }

   }
}
