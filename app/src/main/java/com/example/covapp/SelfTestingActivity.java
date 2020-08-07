package com.example.covapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class SelfTestingActivity extends AppCompatActivity {

    private String website_url = "https://landing.google.com/screener/covid19";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_self_testing);

        webView(website_url); // calling the webview
        bottomNavigation();  // calling the bottom navigation

    } // onCreate ends


    /**
     * Method that enables webView and present in the app
     */

    @SuppressLint("SetJavaScriptEnabled")
    public void webView(String website_url)
    {

        final WebView webView = findViewById(R.id.webview_selftesting);     // initializing the webView to surveyPage( webView id in xml)
        webView.loadUrl(website_url);

        webView.setWebViewClient(new SelfTestingActivity.MYBrowser());  //  helps to use the browser inside the app and not go to phone default browser.

        webView.getSettings().setLoadsImagesAutomatically(true); // enabling the Image in the app browser
        webView.getSettings().setJavaScriptEnabled(true); // enabling the java script to real from web page
        webView.setVerticalScrollBarEnabled(true);  // helps to scroll vertically
        webView.setHorizontalScrollBarEnabled(true);    // helps to scroll horizontally
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY); // making the scroll bar to go up and down

    }
    /**
     * Method that helps the app to browse the web inside the app without taking to phone default browser
     */

    private class MYBrowser extends WebViewClient
    {
        boolean survey_taken = false;

        @SuppressLint("SetJavaScriptEnabled")
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url)
        {
            view.loadUrl(url);

            if(url.equals(website_url))
            {

                //  view.loadUrl(url);
                view.getSettings().setLoadsImagesAutomatically(true); // enabling the Image in the app browser
                view.getSettings().setJavaScriptEnabled(true); // enabling the java script to real from web page
                view.setVerticalScrollBarEnabled(true);  // helps to scroll vertically
                view.setHorizontalScrollBarEnabled(true);    // helps to scroll horizontally
                view.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY); // making the scroll bar to go up and down

            }

            return false;
        }

    }



    // bottom navigation function
    private void bottomNavigation()
    {
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.navigation_services);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_covidtracker:
                        startActivity(new Intent(SelfTestingActivity.this, LocationTracer.class));
                        break;
                    case R.id.navigation_contacttracer:
                        startActivity(new Intent(SelfTestingActivity.this, ContactTracerActivity.class));
                        break;
                    case R.id.navigation_covidupdates:
                         startActivity(new Intent(SelfTestingActivity.this, CovidUpdatesActivity.class));
                        break;
                    case R.id.navigation_services:
                        startActivity(new Intent(SelfTestingActivity.this, ServiceActivity.class));
                        break;
                }
                return true;
            }
        });
    } // bottom navigation function ends

    @Override
    public void onBackPressed() {
        // moveTaskToBack(true); // exit out from the app
        startActivity(new Intent(SelfTestingActivity.this, ServiceActivity.class));
    }
}