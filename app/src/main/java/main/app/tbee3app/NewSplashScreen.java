package main.app.tbee3app;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.widget.ImageView;

/**
 * Created by Chinni on 08-12-2015.
 */
public class NewSplashScreen extends Activity {
    ImageView img;
    private final Handler handler = new Handler();

    private final Runnable startActivityRunnable = new Runnable() {

        @Override
        public void run() {
            SharedPreferences sharedPref;
            SharedPreferences.Editor editor;
            sharedPref = PreferenceManager.getDefaultSharedPreferences(NewSplashScreen.this);
            String tbee3_user = sharedPref.getString("first_launch","-1");

            if(tbee3_user.equals("-1"))
            {
                Intent intent = new Intent(NewSplashScreen.this, welcome_activity.class);
                startActivity(intent);
                finish();
            }
            else {
                Intent intent = new Intent(NewSplashScreen.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
        }

    };

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_splash_screen);
        img = (ImageView) findViewById (R.id.splash_image);
       // PlayServicesHelper playServicesHelper = new PlayServicesHelper(this);
       // Log.e("reg_id",playServicesHelper.getRegistrationId() );
    }

    @Override
    protected void onResume() {
        super.onResume();

        AnimationSet set = new AnimationSet(true);

        Animation fadeIn = FadeIn(1000);
        fadeIn.setStartOffset(0);
        set.addAnimation(fadeIn);

        Animation fadeOut = FadeOut(1000);
        fadeOut.setStartOffset(2000);
       // set.addAnimation(fadeOut);

        img.startAnimation(set);

        handler.postDelayed(startActivityRunnable, 1500);
    }

    public void onPause()
    {
        super.onPause();
        handler.removeCallbacks(startActivityRunnable);
    }

    private Animation FadeIn(int t)
    {
        Animation fade;
        fade = new AlphaAnimation(0.0f,1.0f);
        fade.setDuration(t);
        fade.setInterpolator(new AccelerateInterpolator());
        return fade;
    }
    private Animation FadeOut(int t)
    {
        Animation fade;
        fade = new AlphaAnimation(1.0f,0.0f);
        fade.setDuration(t);
        fade.setInterpolator(new AccelerateInterpolator());
        return fade;
    }


}
