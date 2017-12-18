package com.dmtec.a91jiasu.views.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.sdk.app.H5PayCallback;
import com.alipay.sdk.app.PayTask;
import com.alipay.sdk.util.H5PayResultModel;

import com.dmtec.a91jiasu.R;
import com.dmtec.a91jiasu.common.Constants;
import com.dmtec.a91jiasu.common.CustomToast;
import com.dmtec.a91jiasu.common.PingUtil;
import com.dmtec.a91jiasu.logic.BaseActivity;
import com.dmtec.a91jiasu.logic.Callbacks;
import com.dmtec.a91jiasu.logic.LogicHelper;
import com.dmtec.a91jiasu.ui.MainActivity;
import com.dmtec.a91jiasu.ui.StrongSwanApplication;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class WebActivity extends BaseActivity {

    private WebView webView;
    private boolean isBuy;
    private String u;
    private View back,webLoader;
    private TextView title,web_hint;
    private boolean wxb;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        Intent intent = getIntent();
        isBuy = intent.getBooleanExtra(Constants.Flags.INTENT_IS_BUYING,false);
        web_hint = (TextView)findViewById(R.id.web_hint);
        webLoader = findViewById(R.id.web_loading);
        progressBar = (ProgressBar)findViewById(R.id.pb_progress);
        u = intent.getStringExtra(Constants.Flags.INTENT_URL);
        init();
        if(intent.getBooleanExtra(Constants.Flags.INTENT_USER_PRO,false)){
            webView.setInitialScale(200);
        }

        webView.loadUrl(u);
        setResult(20925,new Intent());
        webView.setWebChromeClient(new WebChromeClient(){
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100) {
                    // 网页加载完成
                    progressBar.setVisibility(View.GONE);
                } else {
                    // 加载中
                    progressBar.setVisibility(View.VISIBLE);
                    progressBar.setProgress(newProgress);
                }
                super.onProgressChanged(view, newProgress);
            }
        });
        webView.setWebViewClient(new WebViewClient(){

            private String MIME_JS = "application/x-javascript";
            private String MIME_PNG = "image/png";

            private String match(String url){
                String resources[] = {
                        "jquery.min.js",
                        "bootstrap.min.js",
                        "vue.js",
                        "vue.min.js",
                        "vue-tap.js",
                        "vue-tap.min.js",
                        "bg.png",
                        "Android-dl.png",
                        "iOS-dl.png",
                        "phone.png"
                };
                for(String res : resources){
                    if(url.endsWith(res)){
                        Log.e("local resource:",res+"");
                        return res;
                    }
                }
                return null;
            }

            /**
             * @return 本地jquery
             */
            private WebResourceResponse editResponse(String jsName,String mime) {
                try {
                    return new WebResourceResponse(mime, "utf-8", getAssets().open(jsName));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //需处理特殊情况
                return null;
            }


            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                if(url.contains(Constants.getPlanConfirmUrl(0)) ||url.contains(Constants.getPlanConfirmUrl(1)) ||url.contains(Constants.getUserProUrl()) ||url.contains(Constants.getWebPageUrl())){
                    webLoader.setVisibility(View.VISIBLE);
                }
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                if(url.contains(Constants.getPlanConfirmUrl(0)) ||url.contains(Constants.getPlanConfirmUrl(1)) ||url.contains(Constants.getUserProUrl()) ||url.contains(Constants.getWebPageUrl())){
                    webLoader.setVisibility(View.GONE);
                }
                super.onPageFinished(view, url);
            }

            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
                if (Build.VERSION.SDK_INT < 21) {
                    String jsName = match(url);
                    if (jsName != null) {
                        String mime = (jsName.endsWith(".png")) ? MIME_PNG : MIME_JS;
                        return editResponse(jsName,mime);
                    }
                }
                return super.shouldInterceptRequest(view, url);
            }
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                if (Build.VERSION.SDK_INT >= 21) {
                    String url = request.getUrl().toString();
                    String jsName = match(url);
                    if (!TextUtils.isEmpty(url) && (jsName!= null)) {
                        String mime = (jsName.endsWith(".png")) ? MIME_PNG : MIME_JS;
                        return editResponse(jsName,mime);
                    }
                }
                return super.shouldInterceptRequest(view, request);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                //检测回调页及支付结果
                checkUrl(url);

                /**
                 * 推荐采用的新的二合一接口(payInterceptorWithUrl),只需调用一次
                 */
                final PayTask task = new PayTask(WebActivity.this);
                boolean isIntercepted = task.payInterceptorWithUrl(url, true, new H5PayCallback() {
                    @Override
                    public void onPayResult(final H5PayResultModel result) {
                        // 支付结果返回
                        final String url = result.getReturnUrl();
                        String status = result.getResultCode();
                        if (!TextUtils.isEmpty(url)) {
                            if(status.equals("9000")){
                                Intent data = new Intent();
                                if(url.contains("home/index/isok.html")){
                                    data.putExtra(Constants.Flags.AR_PAY_SUCCESS,true);
                                }
                                setResult(20925,data);
                            }
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    webView.loadUrl(url);
                                }
                            });
                        }
                    }
                });

                if (isIntercepted) {
                    return true;
                }


                //微信支付调起支付页
                if (url.contains("weixin://wap/pay")){
                    wxb = true;
                    try{
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        startActivity( intent );
                    }catch (Exception e){
                        CustomToast.toast(WebActivity.this,getApplicationContext(),"微信支付调起失败,请检查是否已安装微信");
                    }
                }
                //其它页面
                else{
                    //微信支付的中间页,回调页
                    if(isBuy && wxb){
                        Map extraHeaders = new HashMap();
                        String refUrl = Constants.getRefererUrl(MainActivity.SERVER_AREA);
                        Log.e("REF",refUrl);
                        extraHeaders.put("Referer",refUrl);
                        webView.loadUrl(url,extraHeaders);
                    }
                    //其它网页、官网
                    else{
                        webView.loadUrl(url);
                    }
                }
                return true;
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        webView.onPause();
        webView.pauseTimers();
    }

    @Override
    public void onResume() {
        super.onResume();
        webView.resumeTimers();
        webView.onResume();
    }

    private void init(){
        wxb = false;
        webView = (WebView)findViewById(R.id.webview);
        back = findViewById(R.id.toolbar_back);
        title = (TextView) findViewById(R.id.title);
        if(isBuy){
            title.setText("套餐购买");
        }else{
            title.setText("91加速");
        }
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        WebSettings settings = webView.getSettings();
        webView.addJavascriptInterface(new JsOperation(getApplicationContext(),WebActivity.this),"Android");
        settings.setJavaScriptEnabled(true);
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        settings.setDomStorageEnabled(true);
        settings.setSupportZoom(false);
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setLoadsImagesAutomatically(true);
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
    }


    class JsOperation{

        Context context;
        Activity activity;

        public JsOperation(Context context,Activity activity){
            context = context;
            activity = activity;
        }

        @JavascriptInterface
        public void apk(){
            String url = Constants.getApkUrl();
            if(url.contains(".apk")){
                Uri uri = Uri.parse(url);
                Intent viewIntent = new Intent(Intent.ACTION_VIEW,uri);
                WebActivity.this.startActivity(viewIntent);
            }
        }
        @JavascriptInterface
        public void alert(final String msg){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    CustomToast.toast(WebActivity.this,getApplicationContext(),msg);
                }
            });
        }

        @JavascriptInterface
        public String  getUserToken(){
            return ((StrongSwanApplication)getApplication()).getToken();
        }

        @JavascriptInterface
        public void  login(){
            Intent intent = new Intent(context,LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            context.startActivity(intent);
            activity.finish();
        }
    }



    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK)
        {
            if(webView.canGoBack())
            {
                webView.goBack();//返回上一页面
                return true;
            }
            else
            {
                finish();
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    //根据url判断结果
    private void checkUrl(String url){
        Log.e("url",url);
        Uri uri = Uri.parse(url);
        String host = uri.getHost();
        if(host.equals("wx.tenpay.com")){wxb = true;}
        Intent data = new Intent();
        data.putExtra(Constants.Flags.AR_FROM_BUY,true);
        if(url.contains("home/index/isok.html")){
            data.putExtra(Constants.Flags.AR_PAY_SUCCESS,true);
        }
        this.setResult(20925,data);
    }

}
