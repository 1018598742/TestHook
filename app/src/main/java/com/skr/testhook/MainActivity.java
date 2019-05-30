package com.skr.testhook;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.skr.testhook.config.TagConfig;
import com.taobao.android.dexposed.DexposedBridge;
import com.taobao.android.dexposed.XC_MethodHook;
import com.taobao.android.dexposed.callbacks.XCallback;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = TagConfig.TAG;
    private TextView mShowInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        mShowInfo = ((TextView) findViewById(R.id.showInfo));
    }

    public void aboutThread(View view) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.i(TAG, "MainActivity-run: 线程运行:" + Thread.currentThread().getName());
            }
        }).start();
    }



    class ThreadMethodHook extends XC_MethodHook {
        @Override
        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
            super.beforeHookedMethod(param);
            Thread t = (Thread) param.thisObject;
            Log.i(TAG, "thread:" + t + ", started..");
        }

        @Override
        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
            super.afterHookedMethod(param);
            Thread t = (Thread) param.thisObject;
            Log.i(TAG, "thread:" + t + ", exit..");
        }
    }

    public void epicHook(View view) {
        DexposedBridge.hookAllConstructors(Thread.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                Thread thread = (Thread) param.thisObject;
                Class<?> clazz = thread.getClass();
                if (clazz != Thread.class) {
                    Log.d(TAG, "found class extend Thread:" + clazz);
                    DexposedBridge.findAndHookMethod(clazz, "run", new ThreadMethodHook());
                }
                Log.d(TAG, "Thread: " + thread.getName() + " class:" + thread.getClass() + " is created.");
            }
        });
        DexposedBridge.findAndHookMethod(Thread.class, "run", new ThreadMethodHook());
    }


    private void showText(String text) {
        mShowInfo.setText(text);
        String release = Build.VERSION.RELEASE;
        Log.i(TAG, "MainActivity-showText: "+release);
    }

    public void setInfo(View view) {
        showText("默认的");
    }

    public void setHookInfo(View view) {
        DexposedBridge.findAndHookMethod(MainActivity.class, "showText", String.class, new XC_MethodHook() {

            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                Log.i(TAG, "MainActivity-beforeHookedMethod: ");
                String arg = (String) param.args[0];
                Log.i(TAG, "MainActivity-beforeHookedMethod: string="+arg);
                param.args[0] = "hook的";
            }

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                Log.i(TAG, "MainActivity-afterHookedMethod: ");
            }

            @Override
            protected void call(Param param) throws Throwable {
                Log.i(TAG, "MainActivity-call: ");
            }
        });
    }
}
