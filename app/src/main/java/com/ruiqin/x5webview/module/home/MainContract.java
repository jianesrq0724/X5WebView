package com.ruiqin.x5webview.module.home;

import com.ruiqin.x5webview.base.BaseModel;
import com.ruiqin.x5webview.base.BasePresenter;
import com.ruiqin.x5webview.base.BaseView;
import com.ruiqin.x5webview.module.home.adapter.MainRecyclerAdapter;
import com.ruiqin.x5webview.module.home.bean.MainRecyclerData;

import java.util.List;

/**
 * Created by ruiqin.shen
 * 类说明：
 */

public interface MainContract {
    interface Model extends BaseModel {
        List<MainRecyclerData> initData();
    }

    interface View extends BaseView {
        void setRecyclerAdapterSuccess(MainRecyclerAdapter mainRecyclerAdapter);
    }

    abstract class Presenter extends BasePresenter<View, Model> {
        abstract void setAdapter();
    }
}
