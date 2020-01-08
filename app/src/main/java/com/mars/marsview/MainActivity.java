package com.mars.marsview;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import androidx.appcompat.app.AppCompatActivity;

import com.mars.component.annotation.LoadingStatus;
import com.mars.component.annotation.LoadingStyle;
import com.mars.component.view.loading.MutiLoadingView;

import java.lang.ref.WeakReference;

public class MainActivity extends AppCompatActivity {

    private MutiLoadingView loadingView;
    private MyHandler myHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myHandler = new MyHandler(this);
        loadingView = this.findViewById(R.id.loadingView);

        myHandler.sendEmptyMessageDelayed(0x00, 100);
    }

    private static class MyHandler extends Handler {
        WeakReference<MainActivity> weakReference;

        public MyHandler(MainActivity weakReferenceFr) {
            weakReference = new WeakReference<>(weakReferenceFr);
        }

        @Override
        public void handleMessage(Message msg) {
            MainActivity activity = weakReference.get();
            switch (msg.what) {
                case 0x00:
                    activity.loadingView.setStyle(LoadingStyle.StyleType.NORMAL);
                    activity.loadingView.setStatus("加载中", LoadingStatus.StatusType.LOADING);
                    this.sendEmptyMessageDelayed(0x01, 4000);
                    break;
                case 0x01:
                    activity.loadingView.setStyle(LoadingStyle.StyleType.DOT);
                    activity.loadingView.setStatus("加载成功!", LoadingStatus.StatusType.SUCCESS);
                    this.sendEmptyMessageDelayed(0x02, 1500);
                    break;
                case 0x02:
                    activity.loadingView.setStatus("加载中", LoadingStatus.StatusType.LOADING);
                    this.sendEmptyMessageDelayed(0x03, 4000);
                    break;
                case 0x03:
                    activity.loadingView.setStyle(LoadingStyle.StyleType.PILLAR);
                    activity.loadingView.setStatus("加载失败!", LoadingStatus.StatusType.FAILED);
                    this.sendEmptyMessageDelayed(0x04, 1500);
                    break;
                case 0x04:
                    activity.loadingView.setStatus("加载中", LoadingStatus.StatusType.LOADING);
                    this.sendEmptyMessageDelayed(0x05, 4000);
                    break;
                case 0x05:
                    activity.loadingView.setStatus("完成", LoadingStatus.StatusType.DISMISS);
                    break;
                default:
                    break;
            }
        }
    }
}
