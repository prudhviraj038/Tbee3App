package main.app.tbee3app;

/**
 * Created by Chinni on 12-10-2015.
 */
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class CatListFragment extends Fragment {

    private static final String ARG_POSITION = "position";

    private int position;

    public static CatListFragment newInstance(int position) {
        CatListFragment f = new CatListFragment();
        Bundle b = new Bundle();
        b.putInt(ARG_POSITION, position);
        f.setArguments(b);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        position = getArguments().getInt(ARG_POSITION);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

       /* LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

        FrameLayout fl = new FrameLayout(getActivity());
        fl.setLayoutParams(params);

        final int margin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources()
                .getDisplayMetrics());

        TextView v = new TextView(getActivity());
        params.setMargins(margin, margin, margin, margin);
        v.setLayoutParams(params);
        v.setLayoutParams(params);
        v.setGravity(Gravity.CENTER);
        v.setBackgroundResource(R.drawable.card);
        v.setText("CARD " + (position + 1));

        fl.addView(v);
        return fl;*/
        View rootView = inflater.inflate(R.layout.fragment_layout, container, false);
        LinearLayout touch_pad = (LinearLayout)rootView.findViewById(R.id.touch_panel);
        touch_pad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent newintent = new Intent(getActivity(), ProductDetailsActivity.class);
                startActivity(newintent);
            }
        });
return rootView;

    }

}
