package com.yxhuang.aspectjlimitclick;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.yxhuang.clicklimit.annotation.ClickLimit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends Activity {

    private static final String TAG = "MainActivity";

    @BindView(R.id.btn_click)
    Button mBtnClick;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        TextView tvSay = findViewById(R.id.tv_say);
        tvSay.setOnClickListener(new View.OnClickListener() {

            @ClickLimit
            @Override
            public void onClick(View v) {
                Log.i(TAG, "-----onClick----");
                showToast();
            }
        });
    }


    private void showToast() {
        Toast.makeText(MainActivity.this, "被点击", Toast.LENGTH_SHORT).show();
    }

    @ClickLimit(value = 1000)
    @OnClick(R.id.btn_click)
    public void onViewClicked(View view) {
        Log.i(TAG, "-----butterknife method onClick  execution----");
        showToast();
    }

}
