# NewbieGuide-master —— Android 新手引导视图

## 效果演示 ##
![](/screenshots/picture1.jpg)
![](/screenshots/picture2.jpg)

## 基本使用 ##
```java
    int[] loc = new int[2];
    view.getLocationOnScreen(loc);
    NewbieGuide.with(this)
            .addHighLight(view, ShapeType.RECTANGLE) // 设置高亮控件
            .addGuideTip(R.layout.layout_member_view, loc[0] + view.getWidth() / 2, loc[1]) // 设置引导提示
            .setLabel("member_guide") // 设置用于识别引导提示的标签
            .show(); // 显示引导提示
```

## 参数配置 ##
```java
    int[] loc = new int[2];
    view.getLocationOnScreen(loc);
    View guideView = LayoutInflater.from(this).inflate(R.layout.layout_tips_view, null);
    newbieGuide = NewbieGuide.with(this)
            .addHighLight(view, ShapeType.ROUND_RECTANGLE, 25) // 设置高亮控件
            .addGuideTip(guideView, 0, loc[1] + view.getHeight(), R.id.iv_isee) // 设置引导提示
            .setLabel("newbie_guide") // 设置用于识别引导提示的标签
            .setAlwaysShow(true) // 是否每次都显示引导层，默认false
            .setAnyWhereCancelable(false) // 设置点击任何区域消失，默认为true
            .setBackgroundColor(GuideLayout.DEFAULT_COLOR) //设置引导层背景色
            .setOnGuideChangedListener(new NewbieGuide.OnGuideChangedListener() {// 设置监听
                @Override
                public void onShow() {
                    Log.i("TAG", "NewbieGuide显示...");
                }

                @Override
                public void onDismiss() {
                    Log.i("TAG", "NewbieGuide隐藏...");
                }

                @Override
                public void onHeightlightViewClick(View view) {
                    Log.i("TAG", "点击了Button");
                }
            })
            .build(); // 构建引导层的控制器
    newbieGuide.resetLabel("newbie_guide"); // 重置用于识别引导提示的标签
    newbieGuide.dismiss(); // 取消引导提示
    newbieGuide.show(); // 显示引导提示
```





