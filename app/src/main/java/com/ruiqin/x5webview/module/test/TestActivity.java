package com.ruiqin.x5webview.module.test;

import android.os.Bundle;

import com.ruiqin.x5webview.R;
import com.ruiqin.x5webview.base.BaseActivity;

public class TestActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
    }

    @Override
    protected int getFragmentId() {
        return 0;
    }
}
