package com.ruiqin.x5webview.commonality.browse.x5webview;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.blankj.utilcode.util.AppUtils;
import com.ruiqin.x5webview.R;
import com.ruiqin.x5webview.base.BaseActivity;
import com.ruiqin.x5webview.commonality.browse.x5webview.view.X5WebView;
import com.ruiqin.x5webview.constant.NetWorkType;
import com.ruiqin.x5webview.util.LogUtils;
import com.ruiqin.x5webview.util.ToastUtils;
import com.tencent.smtt.sdk.CookieManager;
import com.tencent.smtt.sdk.CookieSyncManager;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static com.ruiqin.x5webview.constant.Constant.CONST_CHARSET;

/**
 * Created by ruiqin.shen
 * 类说明：所有的H5请求都跳转到这个页面
 * 使用腾讯X5内核
 */
public class X5WebViewBrowseActivity extends BaseActivity {
    @BindView(R.id.progressBar)
    ProgressBar mProgressBar;
    @BindView(R.id.webView)
    FrameLayout mViewParent;

    private static final String PARAM_URL = "url";
    private static final String PARAM_TYPE = "type";
    private static final String PARAM_TITLE = "title";
    private static final String PARAM_POSTDATA = "postData";

    private String requestType = NetWorkType.GET;
    private String webPostData;
    private String[] webUrl = {"http://blog.csdn.net/jianesrq0724/article/details/55211918", "http://blog.csdn.net/jianesrq0724/article/details/54892758"
            , "http://blog.csdn.net/jianesrq0724/article/details/75175136"};
    private String title;

    /**
     * 启动自身
     *
     * @param context
     * @return
     */
    public static Intent newIntent(Context context, String url, String title) {
        Intent intent = new Intent(context.getApplicationContext(), X5WebViewBrowseActivity.class);
        intent.putExtra(PARAM_URL, url);
        intent.putExtra(PARAM_TITLE, title);
        return intent;
    }

    /**
     * 传递标题和H5的请求类型
     *
     * @param context
     * @param url
     * @return
     */
    public static Intent newIntent(Context context, String url, String postData, String type, String title) {//
        Intent intent = new Intent(context.getApplicationContext(), X5WebViewBrowseActivity.class);
        intent.putExtra(PARAM_URL, url);
        intent.putExtra(PARAM_POSTDATA, postData);
        intent.putExtra(PARAM_TYPE, type);
        intent.putExtra(PARAM_TITLE, title);
        return intent;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browser);
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
//        getIntentData();//从Intent中获取数据
        initToolBar();
        DelayInitWebView();
    }

    /**
     * 初始化toolBar
     */
    private void initToolBar() {
        mToolbarTitle.setText(title);//设置标题
    }

    @Override
    protected int getFragmentId() {
        return 0;
    }


    /**
     * 延时初始化webView
     */
    private void DelayInitWebView() {
        Flowable.timer(10, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(@NonNull Long aLong) throws Exception {
                        initWebView();
                        startRefresh();//开始刷新
                    }
                }, Throwable::printStackTrace);
    }

    private X5WebView mWebView;

    /**
     * 初始化webView
     */
    private void initWebView() {
        mWebView = new X5WebView(this, null);
        mViewParent.addView(mWebView, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
        initWebViewSetting();
        setWebChromeClient();
        setWebViewClient();//WebView的webViewClient事件
        loadUrl();
    }

    CountDownTimer countDownTimerRefresh;
    private int count = 0;

    private void startRefresh() {
        countDownTimerRefresh = new CountDownTimer(100000000, 2000) {
            @Override
            public void onTick(long millisUntilFinished) {
                clearCookies(mContext);
                count = (count + 1) % 3;
                mWebView.loadUrl(webUrl[count]);//刷新
            }

            @Override
            public void onFinish() {

            }
        }.start();
    }

    public static void clearCookies(Context context) {
        // Edge case: an illegal state exception is thrown if an instance of
        // CookieSyncManager has not be created.  CookieSyncManager is normally
        // created by a WebKit view, but this might happen if you start the
        // app, restore saved state, and click logout before running a UI
        // dialog in a WebView -- in which case the app crashes
        @SuppressWarnings("unused")
        CookieSyncManager cookieSyncMngr = CookieSyncManager.createInstance(context);
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.removeAllCookie();
    }

    private void canRefresh() {
        if (countDownTimerRefresh != null) {
            countDownTimerRefresh.cancel();
            countDownTimerRefresh = null;
        }
    }

    /**
     * WebView的webViewClient事件
     */
    private void setWebViewClient() {
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                try {
                    url = URLDecoder.decode(url.substring(url.lastIndexOf('|') + 1), CONST_CHARSET);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                LogUtils.e("URL", url);
                if (interceptCallPhone(url)) {//拦截打电话
                    return true;
                }
                return false;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }
        });
    }

    String telUrl;//电话地址

    /**
     * 拦截打电话
     *
     * @param url
     */
    private boolean interceptCallPhone(String url) {
        if (url.contains("tel:")) {
            telUrl = url;
            startCallPhone();
            return true;
        }
        return false;
    }

    /**
     * 加载网页
     */
    private void loadUrl() {
        LogUtils.e("TAG", "loadUrl");
        mWebView.loadUrl(webUrl[0]);
    }

    /**
     * 拨打电话
     */
    private void startCallPhone() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {//未授权打电话
            ActivityCompat.requestPermissions(X5WebViewBrowseActivity.this, new String[]{Manifest.permission.CALL_PHONE}, 1);
        } else {
            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + telUrl));
            startActivity(intent);
            finish();
        }
    }

    boolean weatherBannedPermission;//是否禁止权限

    @Override
    public void onRequestPermissionsResult(int requestCode, @android.support.annotation.NonNull String[] permissions, @android.support.annotation.NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                for (int i = 0; i < grantResults.length; i++) {
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {//权限未同意
                        boolean bannedPermission = ActivityCompat.shouldShowRequestPermissionRationale(X5WebViewBrowseActivity.this, permissions[i]);//是否未禁止权限
                        if (bannedPermission) {//用户未禁止
                            ActivityCompat.requestPermissions(X5WebViewBrowseActivity.this, new String[]{Manifest.permission.CALL_PHONE}, 1);//重新申请权限
                            return;
                        } else {//禁止权限，标记
                            weatherBannedPermission = true;
                        }
                    }
                }

                /**
                 * 判断是否禁用权限
                 */
                if (weatherBannedPermission) {
                    ToastUtils.showShort("请手动打开电话权限");
                    Uri packageURI = Uri.parse("package:" + AppUtils.getAppPackageName());
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, packageURI);
                    startActivity(intent);
                } else {
                    startCallPhone();
                }
                break;
            default:
                break;
        }
    }

    /**
     * WebView的setWebChromeClient事件
     */
    private void setWebChromeClient() {
        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView webView, int newProgress) {
                if (mProgressBar != null) {
                    LogUtils.e("URL", "进度：" + newProgress + ", Url :" + mWebView.getUrl());
                    mProgressBar.setProgress(newProgress);
                    if (newProgress == 100) {
                        Flowable.timer(600, TimeUnit.MILLISECONDS)
                                .subscribeOn(Schedulers.newThread())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Consumer<Long>() {
                                    @Override
                                    public void accept(@NonNull Long aLong) throws Exception {
                                        if (mProgressBar != null) {
                                            mProgressBar.setVisibility(View.INVISIBLE);
                                        }
                                    }
                                }, Throwable::printStackTrace);
                    } else {//不为100的时候显示
                        mProgressBar.setVisibility(View.VISIBLE);
                    }
                }
                super.onProgressChanged(webView, newProgress);
            }
        });
    }

    /**
     * 初始化WebView的setting事件
     */
    private void initWebViewSetting() {
        WebSettings webSetting = mWebView.getSettings();
        webSetting.setAllowFileAccess(true);
        webSetting.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        webSetting.setSupportZoom(true);
        webSetting.setBuiltInZoomControls(true);
        webSetting.setUseWideViewPort(true);
        webSetting.setSupportMultipleWindows(false);
        // webSetting.setLoadWithOverviewMode(true);
        webSetting.setAppCacheEnabled(true);
        // webSetting.setDatabaseEnabled(true);
        webSetting.setDomStorageEnabled(true);
        webSetting.setJavaScriptEnabled(true);
        webSetting.setGeolocationEnabled(true);
        webSetting.setAppCacheMaxSize(Long.MAX_VALUE);
        webSetting.setAppCachePath(this.getDir("appcache", 0).getPath());
        webSetting.setDatabasePath(this.getDir("databases", 0).getPath());
        webSetting.setGeolocationDatabasePath(this.getDir("geolocation", 0)
                .getPath());
        // webSetting.setPageCacheCapacity(IX5WebSettings.DEFAULT_CACHE_CAPACITY);
        webSetting.setPluginState(WebSettings.PluginState.ON_DEMAND);
        CookieSyncManager.createInstance(this);
        CookieSyncManager.getInstance().sync();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        canRefresh();
    }
}
