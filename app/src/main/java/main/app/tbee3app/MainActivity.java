package main.app.tbee3app;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.daimajia.androidanimations.library.Techniques;
import com.quickblox.core.QBEntityCallbackImpl;
import com.quickblox.users.model.QBUser;
import com.viksaa.sssplash.lib.activity.AwesomeSplash;
import com.viksaa.sssplash.lib.cnst.Flags;
import com.viksaa.sssplash.lib.model.ConfigSplash;

import java.util.List;


public class MainActivity extends AwesomeSplash {

    @Override
    public void initSplash(ConfigSplash configSplash) {

            /* you don't have to override every property */
        getSupportActionBar().hide();
        //Customize Circular Reveal
        configSplash.setBackgroundColor(R.color.primary); //any color you want form colors.xml
        configSplash.setAnimCircularRevealDuration(2000); //int ms
       // configSplash.setRevealFlagX(Flags.WITH_LOGO);  //or Flags.REVEAL_LEFT
        configSplash.setRevealFlagY(Flags.WITH_LOGO); //or Flags.REVEAL_TOP

        //Choose LOGO OR PATH; if you don't provide String value for path it's logo by default

        //Customize Logo
        configSplash.setLogoSplash(R.drawable.splash_logo); //or any other drawable
        configSplash.setAnimLogoSplashDuration(2000); //int ms
        configSplash.setAnimLogoSplashTechnique(Techniques.FadeIn); //choose one form Techniques (ref: https://github.com/daimajia/AndroidViewAnimations)


        //Customize Path
      // configSplash.setPathSplash("M100,250 C100,100 400,100 400,250"); //set path String
      /*  configSplash.setOriginalHeight(400); //in relation to your svg (path) resource
        configSplash.setOriginalWidth(400); //in relation to your svg (path) resource
        configSplash.setAnimPathStrokeDrawingDuration(3000);
        configSplash.setPathSplashStrokeSize(3); //I advise value be <5
        configSplash.setPathSplashStrokeColor(R.color.accent); //any color you want form colors.xml
        configSplash.setAnimPathFillingDuration(3000);
        configSplash.setPathSplashFillColor(R.color.Wheat); //path object filling color
*/

        //Customize Title
        configSplash.setTitleSplash("new & used stuff");
        configSplash.setTitleTextColor(R.color.white);
        configSplash.setTitleTextSize(25f); //float value
        configSplash.setAnimTitleDuration(3000);
        configSplash.setAnimTitleTechnique(Techniques.FadeIn);
       // configSplash.setTitleFont("fonts/myfont.ttf"); //provide string to your font located in assets/fonts/
        final QBUser user = new QBUser();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String userid = sharedPreferences.getString("tbee3_user","-1");
        user.setLogin(userid);
        user.setPassword("12345678");
        ChatService.initIfNeed(this);

        ChatService.getInstance().login(user, new QBEntityCallbackImpl() {

            @Override
            public void onSuccess() {
                Log.e("success", "chat_service");
                // Go to Dialogs screen
                //
                Log.e("chat service","ok");

            }

            @Override
            public void onError(List errors) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                dialog.setMessage("chat login errors: " + errors).create().show();
            }
        });
    }




    @Override
    public void animationsFinished() {

        //transit to another activity here
        //or do whatever you want

        SharedPreferences sharedPref;
        SharedPreferences.Editor editor;
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String tbee3_user = sharedPref.getString("tbee3_user","-1");

        if(tbee3_user.equals("-1"))
        {
            Intent intent = new Intent(MainActivity.this, welcome_activity.class);
            startActivity(intent);
            finish();
        }
        else {
            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
