package main.app.tbee3app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.ToxicBakery.viewpager.transforms.DepthPageTransformer;
import com.squareup.picasso.Picasso;

import me.relex.circleindicator.CircleIndicator;

/**
 * Created by Chinni on 29-10-2015.
 */
public class welcome_activity extends FragmentActivity {

    private static final int NUM_PAGES = 5;
    private ViewPager mPager;
    private PagerAdapter mPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Settings.forceRTLIfSupported(this);
        setContentView(R.layout.welcome_screeen);
        mPager = (ViewPager) findViewById(R.id.view5);
        CircleIndicator customIndicator = (CircleIndicator) findViewById(R.id.indicator_custom);
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        mPager.setPageTransformer(true, new DepthPageTransformer());
        customIndicator.setViewPager(mPager);
        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i2) {

            }

            @Override
            public void onPageSelected(int i) {
                Log.d("OnPageChangeListener", "Current selected = " + i);
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

        // Instantiate a ViewPager and a PagerAdapter.


    }


    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return new ScreenSlidePageFragment().newInstance(position);
        }

        @Override
        public int getCount() {
            return 9;
        }
    }

    public static class ScreenSlidePageFragment extends Fragment {
        private int position;
        public static ScreenSlidePageFragment newInstance(int position){
            ScreenSlidePageFragment f = new ScreenSlidePageFragment();
            Bundle b = new Bundle();
            b.putInt("position", position);
            f.setArguments(b);
            return f;
        }
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            position = getArguments().getInt("position");
            ViewGroup rootView = (ViewGroup) inflater.inflate(
                    R.layout.welcome_screen2, container, false);
            ImageView image_view ;
            switch (position)
            {
                case 0:
                    rootView = (ViewGroup) inflater.inflate(R.layout.welcome_screen1, container, false);
                    break;
                case 1:
                    rootView = (ViewGroup) inflater.inflate(R.layout.welcome_screen2, container, false);
                    image_view = (ImageView) rootView.findViewById(R.id.image_view);
                    Picasso.with(getActivity()).load(R.drawable.wa).into(image_view);
                    break;

                case 2:
                    rootView = (ViewGroup) inflater.inflate(R.layout.welcome_screen2, container, false);
                    image_view = (ImageView) rootView.findViewById(R.id.image_view);
                    Picasso.with(getActivity()).load(R.drawable.wb).into(image_view);
                    break;
                case 3:
                    rootView = (ViewGroup) inflater.inflate(R.layout.welcome_screen2, container, false);
                    image_view = (ImageView) rootView.findViewById(R.id.image_view);
                    Picasso.with(getActivity()).load(R.drawable.wc).into(image_view);

                    break;
                case 4:
                    rootView = (ViewGroup) inflater.inflate(R.layout.welcome_screen2, container, false);
                    image_view = (ImageView) rootView.findViewById(R.id.image_view);
                    Picasso.with(getActivity()).load(R.drawable.wd).into(image_view);

                    break;
                case 5:
                    rootView = (ViewGroup) inflater.inflate(R.layout.welcome_screen2, container, false);
                    image_view = (ImageView) rootView.findViewById(R.id.image_view);
                    Picasso.with(getActivity()).load(R.drawable.we).into(image_view);
                    break;
                case 6:
                    rootView = (ViewGroup) inflater.inflate(R.layout.welcome_screen2, container, false);
                    image_view = (ImageView) rootView.findViewById(R.id.image_view);
                    Picasso.with(getActivity()).load(R.drawable.wf).into(image_view);
                    break;
                case 7:
                    rootView = (ViewGroup) inflater.inflate(R.layout.welcome_screen2, container, false);
                    image_view = (ImageView) rootView.findViewById(R.id.image_view);
                    Picasso.with(getActivity()).load(R.drawable.wg).into(image_view);
                    break;
                case 8:
                    rootView = (ViewGroup) inflater.inflate(R.layout.welcome_screen_end, container, false);
                    Button continue_log = (Button) rootView.findViewById(R.id.continue_log);
                    Button skip_for_now = (Button) rootView.findViewById(R.id.skip_for_now);
                    SharedPreferences sharedPref;
                    final SharedPreferences.Editor editor;
                    sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
                    editor = sharedPref.edit();
                    continue_log.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            editor.putString("first_launch", "101");
                            editor.commit();
                            Intent log_intent = new Intent(getActivity(),AccountActivity.class);
                            log_intent.putExtra("goto","home");
                            startActivity(log_intent);
                            getActivity().finish();
                        }
                    });
                    skip_for_now.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            editor.putString("first_launch", "102");
                            editor.commit();
                            Intent log_intent = new Intent(getActivity(),HomeActivity.class);
                            startActivity(log_intent);
                            getActivity().finish();
                        }
                    });
                    break;
            }
            return rootView;
        }
    }
}
