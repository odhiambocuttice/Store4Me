package com.android.store4me.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.android.store4me.R;

public class SliderAdapter extends PagerAdapter {

    Context context;
    LayoutInflater layoutInflater;

    public SliderAdapter(Context context) {
        this.context = context;

    }

    public int[] slideimages = {
            R.drawable.shelf2,
            R.drawable.request1,
            R.drawable.directions1,
            R.drawable.nearby1,


    };
    public String[] slideDescription = {
            "Select the Store Nearest to your location",
            "Request or Call the Selected Store",
            "Find Directions to the Store",
            "Find Nearby Places",


    };

    @Override
    public int getCount() {

        return slideimages.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == (RelativeLayout) object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.slideviewlayout, container, false);

        ImageView slideImageView = (ImageView) view.findViewById(R.id.imageView2);
        TextView slideDesc = (TextView) view.findViewById(R.id.textView4);

        slideImageView.setImageResource(slideimages[position]);
        slideDesc.setText(slideDescription[position]);

        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((RelativeLayout) object);
    }
}
