package com.example.william.harusem.slides;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.william.harusem.R;

import agency.tango.materialintroscreen.SlideFragment;


public class SlideTwoFragment extends SlideFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.slide_2, parent, false);

        TextView slideOneTitle = rootView.findViewById(R.id.slide_2_title);
        TextView slideOneDescription = rootView.findViewById(R.id.slide_2_description);

        Typeface custom_font = Typeface.createFromAsset(getActivity().getAssets(), "fonts/GothamMedium.ttf");
        slideOneTitle.setTypeface(custom_font);
        slideOneDescription.setTypeface(custom_font);

        return rootView;

    }

    @Override
    public int backgroundColor() {
        return android.R.color.transparent;
    }

    @Override
    public int buttonsColor() {
        return R.color.slide_two_button;
    }


}
