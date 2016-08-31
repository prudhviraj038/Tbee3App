package main.app.tbee3app;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.daimajia.slider.library.Indicators.PagerIndicator;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.DefaultSliderView;

import java.util.ArrayList;

/**
 * Created by sriven on 12/12/2015.
 */
public class AddPreviewActivity extends Activity
{
        TextView description,price,make_an_offer;
         private ArrayList<String> image_path;
    SliderLayout sliderShow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Settings.forceRTLIfSupported(this);
        setContentView(R.layout.preview_view_header);
        image_path = new ArrayList<>();
        description = (TextView) findViewById(R.id.desc);
        price = (TextView) findViewById(R.id.price_area);
        make_an_offer = (TextView) findViewById(R.id.make_an_offer);
        sliderShow = (SliderLayout) findViewById(R.id.slider);
        sliderShow.stopAutoCycle();
        sliderShow.setCustomIndicator((PagerIndicator) findViewById(R.id.pager_indicator));

        description.setText(getIntent().getStringExtra("desc"));
        price.setText("KD " + getIntent().getStringExtra("price"));
        if(getIntent().getStringExtra("price").equals("0"))
            make_an_offer.setVisibility(View.GONE);
        image_path = (ArrayList<String>) getIntent().getSerializableExtra("image_list");
        for(int i = 0;i<image_path.size();i++){
            Log.e("image_url",image_path.get(i));
            DefaultSliderView defaultSliderView = new DefaultSliderView(AddPreviewActivity.this);
            defaultSliderView
                    .description("image")
                    .setScaleType(BaseSliderView.ScaleType.CenterCrop)
                    .image(image_path.get(i));

            sliderShow.addSlider(defaultSliderView);

        }

    }

    }
