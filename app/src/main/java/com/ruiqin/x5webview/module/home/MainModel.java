package com.ruiqin.x5webview.module.home;

import com.ruiqin.x5webview.module.home.bean.MainRecyclerData;
import com.ruiqin.x5webview.module.test.TestActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ruiqin.shen
 * 类说明：
 */

public class MainModel implements MainContract.Model {
    @Override
    public List<MainRecyclerData> initData() {
        List<MainRecyclerData> recyclerDataList = new ArrayList<>();
        recyclerDataList.add(new MainRecyclerData("TestActivity", TestActivity.class));
        return recyclerDataList;
    }
}
