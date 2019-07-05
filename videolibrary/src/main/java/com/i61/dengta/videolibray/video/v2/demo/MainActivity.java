package com.i61.dengta.videolibray.video.v2.demo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.i61.dengta.videolibray.R;

public class MainActivity extends Activity implements View.OnClickListener {

    protected Button btn_h, btn_v;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.v2_demo_activity_main);
        btn_h = findViewById(R.id.btn_h);
        btn_v = findViewById(R.id.btn_v);

        btn_h.setOnClickListener(this);
        btn_v.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btn_h) {
            /**半屏播放器*/
            startActivity(HPlayerActivity.class);
        } else if (id == R.id.btn_v) {
            /**竖屏播放器*/
            startActivity(PlayerActivity.class);
        }
    }

    private void startActivity(Class<?> cls) {
        Intent intent = new Intent(MainActivity.this, cls);
        startActivity(intent);
    }
}
