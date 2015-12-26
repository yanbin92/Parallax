package com.definewidgest.parallax;

import android.annotation.TargetApi;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.definewidgest.parallax.view.MyParallaxListView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final MyParallaxListView lv= (MyParallaxListView) findViewById(R.id.lv);

        View header=View.inflate(this,R.layout.img_header,null);
        lv.addHeaderView(header);
        final ImageView headerImageView= (ImageView) header.findViewById(R.id.iv);

        headerImageView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onGlobalLayout() {

                lv.setHeaderView(headerImageView);

                headerImageView.getViewTreeObserver().removeOnGlobalLayoutListener(this);

            }
        });


        lv.setAdapter(new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,Cheeses.NAMES));

    }
}
