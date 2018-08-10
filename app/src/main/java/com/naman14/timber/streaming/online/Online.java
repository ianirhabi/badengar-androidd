package com.naman14.timber.streaming.online;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.os.Bundle;
import android.webkit.WebView;
import android.widget.Toast;

import com.naman14.timber.R;

import im.delight.android.webview.AdvancedWebView;

public class Online extends Activity implements AdvancedWebView.Listener,SwipeRefreshLayout.OnRefreshListener {

    private AdvancedWebView mWebView;
    SwipeRefreshLayout layoutswipe;
    WebView webview ;
    private boolean timerHasStarted = false;
    private CountDownTimer countDownTimer;
    private final long startTime = 3 * 1000;
    private final long interval = 1 * 1000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online);
        Bundle b = getIntent().getExtras();
        String get_data = b.getString("parse_situs");
        String url ="http://"+ get_data;
        layoutswipe = (SwipeRefreshLayout)findViewById(R.id.swype);
        layoutswipe.setOnRefreshListener(this);
        onLoadweb(url);
    }

    public void onLoadweb(String url){
        mWebView = (AdvancedWebView) findViewById(R.id.webview);
        mWebView.setListener(Online.this, this);
        mWebView.loadUrl(url);
    }

    @Override
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                layoutswipe.setRefreshing(false);
                Bundle b = getIntent().getExtras();
                String get_data = b.getString("parse_situs");
                String url = "http://"+get_data;
                onLoadweb(url);
            }
        },1000);
    }

    @Override
    public void onPageStarted(String url, Bitmap favicon) {

    }

    @Override
    public void onPageFinished(String url) {

    }

    @Override
    public void onPageError(int errorCode, String description, String failingUrl) {

    }

    @Override
    public void onDownloadRequested(String url, String suggestedFilename, String mimeType, long contentLength, String contentDisposition, String userAgent) {

    }

    @Override
    public void onExternalPageRequest(String url) {

    }
}
