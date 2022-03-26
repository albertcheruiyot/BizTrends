package com.albert.biztrends;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.DownloadManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.MimeTypeMap;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class NewsActivity extends AppCompatActivity {
    WebView webView;
    ProgressBar progressBar;
    SwipeRefreshLayout swipeRefreshLayout;
    private Toolbar mToolbar;
    private boolean exit = false;

    private int ad = 0;

    String url = "https://tradingeconomics.com/calendar";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        //getSupportActionBar().hide();



        /**
         * img=findViewById(R.id.imgView2);
         *         txt1=findViewById(R.id.txt);
         *         //load animation from drawable
         *         topanim = AnimationUtils.loadAnimation(this,R.anim.top_animation);
         *         btmanim = AnimationUtils.loadAnimation(this,R.anim.bottom_animation);
         *         //set animation to image and text
         *         img.setAnimation(topanim);
         *         txt1.setAnimation(btmanim)
         */

        webView = findViewById(R.id.web);
        progressBar = findViewById(R.id.progress);
        swipeRefreshLayout = findViewById(R.id.swipe);


        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setSupportZoom(false);
        webView.getSettings().setDomStorageEnabled(true);
        webView.setWebViewClient(new myWebViewclient());
        webView.loadUrl(url);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                swipeRefreshLayout.setRefreshing(true);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                        webView.loadUrl(url);
                    }
                },  3000);
            }
        });

        swipeRefreshLayout.setColorSchemeColors(
                getResources().getColor(android.R.color.holo_blue_bright),
                getResources().getColor(android.R.color.holo_orange_dark),
                getResources().getColor(android.R.color.holo_green_dark),
                getResources().getColor(android.R.color.holo_red_dark)
        );

        //  ==================== START HERE: THIS CODE BLOCK IS TO ENABLE FILE DOWNLOAD FROM THE WEB. YOU CAN COMMENT IT OUT IF YOUR APPLICATION DOES NOT REQUIRE FILE DOWNLOAD. IT WAS ADDED ON REQUEST ======//

        webView.setDownloadListener(new DownloadListener() {
            String fileName = MimeTypeMap.getFileExtensionFromUrl(url);
            @Override
            public void onDownloadStart(String url, String userAgent,
                                        String contentDisposition, String mimetype,
                                        long contentLength) {

                DownloadManager.Request request = new DownloadManager.Request(
                        Uri.parse(url));

                request.allowScanningByMediaScanner();
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED); //Notify client once download is completed!
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);
                DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                dm.enqueue(request);
                Toast.makeText(getApplicationContext(), "Downloading File", //To notify the Client that the file is being downloaded
                        Toast.LENGTH_LONG).show();

            }
        });
        /** Nav Code Start**/
        //initialize and assign variable
        BottomNavigationView bottomNavigationView = findViewById(R.id.news_bottom_navigation);

        //set Home Selected
        bottomNavigationView.setSelectedItemId(R.id.bottom_nav_news);

        //perform ItemSelectedListener

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.bottom_nav_home:
                        startActivity(new Intent(getApplicationContext(),MainActivity.class));
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.bottom_nav_profile:
                        startActivity(new Intent(getApplicationContext(),ProfileActivity.class));
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.nav_bottom_settings:
                        startActivity(new Intent(getApplicationContext(),SettingActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.bottom_nav_news:

                        return true;


                }
                return true;
            }

        });
        /** Nav Code end**/
    }
    public class myWebViewclient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            Toast.makeText(getApplicationContext(), "No internet connection", Toast.LENGTH_LONG).show();
            //webView.loadUrl("file:///android_asset/lost.html");
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            super.onReceivedSslError(view, handler, error);
            handler.cancel();
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            progressBar.setVisibility(View.GONE);
        }
    }


    /*@Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
            webView.goBack();
            showInterval();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }*/

    @Override
    public void onBackPressed() {
        if(webView.canGoBack()) {


            webView.goBack();
        }else {
            /*if(exit) {
                //next();

                finish();
            }else {

                Toast.makeText(NewsActivity.this, "Press again to exit.", Toast.LENGTH_SHORT).show();
            }
            Timer timer=new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    exit=false;
                }
            },2000);
            exit=true;*/
            Intent addNewPostIntent = new Intent(NewsActivity.this, MainActivity.class);
            startActivity(addNewPostIntent);
        }

    }
}