package com.skr.testhook;

import android.app.Application;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
//        HookUtil hookUtil = new HookUtil(MainActivity.class, this);
//        hookUtil.hookAms();

        //这个ProxyActivity在清单文件中注册过，以后所有的Activitiy都可以用ProxyActivity无需声明，绕过监测
        HookUtil hookAmsUtil = new HookUtil(ProxyActivity.class, this);
        hookAmsUtil.hookSystemHandler();
        hookAmsUtil.hookAms();

    }
}
