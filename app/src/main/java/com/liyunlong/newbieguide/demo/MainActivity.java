package com.liyunlong.newbieguide.demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.liyunlong.newbieguide.GuideLayout;
import com.liyunlong.newbieguide.NewbieGuide;
import com.liyunlong.newbieguide.ShapeType;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Toolbar toolbar;

    private MenuItem menuItem;
    private TextView menuView;
    private NewbieGuide menuGuide;
    private NewbieGuide memberGuide;
    private NewbieGuide buttonGuide;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.common_toolbar);
        setSupportActionBar(toolbar);

        findViewById(R.id.member).setOnClickListener(this);
        findViewById(R.id.button).setOnClickListener(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        menuItem = menu.findItem(R.id.menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu:
                menuView = (TextView) findViewById(R.id.menu);
                showMenuGuide(menuView);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.member:
                showMemberGuide(v);
                break;
            case R.id.button:
                showButtonGuide(v);
                break;
        }
    }

    private void showMenuGuide(View view) {
        if (menuGuide == null) {
            int[] loc = new int[2];
            view.getLocationOnScreen(loc);
            ImageView imageView = new ImageView(this);
            imageView.setImageResource(R.drawable.right_top);
            menuGuide = NewbieGuide.with(this)
                    .addHighLight(view, ShapeType.CIRCLE)
                    .addGuideTip(imageView, loc[0] - view.getWidth() / 2, loc[1] + view.getHeight())
                    .setAlwaysShow(true)
                    .setAnyWhereCancelable(true)
                    .setPerformHighLightClick(false)
                    .build();
        }
        if (menuGuide.isShowing()) {
            menuGuide.dismiss();
        } else {
            menuGuide.show();
        }
    }

    private void showMemberGuide(View view) {
        if (memberGuide == null) {
            int[] loc = new int[2];
            view.getLocationOnScreen(loc);
            memberGuide = NewbieGuide.with(this)
                    .addHighLight(view, ShapeType.RECTANGLE) // 设置高亮控件
                    .addGuideTip(R.layout.layout_member_view, loc[0] + view.getWidth() / 2, loc[1]) // 设置引导提示
                    .setLabel("member_guide") // 设置用于识别引导提示的标签
                    .setAlwaysShow(false) // 是否每次都显示引导层，默认false
                    .setAnyWhereCancelable(true) // 设置点击任何区域消失，默认为true
                    .setBackgroundColor(GuideLayout.DEFAULT_COLOR) //设置引导层背景色
                    .build(); // 构建引导层的控制器
        }
        if (memberGuide.isShowing()) {
            memberGuide.dismiss(); // 取消引导提示
        } else {
            memberGuide.show(); // 显示引导提示
            memberGuide.resetLabel("member_guide"); // 重置用于识别引导提示的标签
        }
    }

    private void showButtonGuide(View view) {
        if (buttonGuide == null) {
            int[] loc = new int[2];
            view.getLocationOnScreen(loc);
            View guideView = LayoutInflater.from(this).inflate(R.layout.layout_tips_view, null);
            buttonGuide = NewbieGuide.with(this)
                    .addHighLight(view, ShapeType.ROUND_RECTANGLE, 25) // 设置高亮控件
                    .addGuideTip(guideView, 0, loc[1] + view.getHeight(), new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT), R.id.iv_isee) // 设置引导提示
                    .setLabel("button_guide") // 设置用于识别引导提示的标签
                    .setAlwaysShow(true) // 是否每次都显示引导层，默认false
                    .setAnyWhereCancelable(false) // 设置点击任何区域消失，默认为true
                    .setBackgroundColor(GuideLayout.DEFAULT_COLOR) //设置引导层背景色
                    .setOnGuideChangedListener(new NewbieGuide.OnGuideChangedListener() {// 设置监听
                        @Override
                        public void onShow() {
                            Log.i("TAG", "ButtonGuide显示...");
                        }

                        @Override
                        public void onDismiss() {
                            Log.i("TAG", "ButtonGuide隐藏...");
                        }

                        @Override
                        public void onHeightlightViewClick(View view) {
                            Log.i("TAG", "点击了Button");
                        }
                    })
                    .build(); // 构建引导层的控制器
        }
        buttonGuide.resetLabel("button_guide"); // 重置用于识别引导提示的标签
        if (buttonGuide.isShowing()) {
            buttonGuide.dismiss(); // 取消引导提示
        } else {
            buttonGuide.show(); // 显示引导提示
        }
    }
}
