## 项目(**户型绘制**)

### Step1
```
//gradle添加
- implementation 'com.eju.cy.drawlibrary:drawroom:1.2.1'
//添加支持NDK架构类型支持
    ndk {
            abiFilters "armeabi", "armeabi-v7a", "x86", "mips"

        }

```
### Step2
```
// 在应用程序主入口 IApplication 中的 onCreate中 添加  JDHomeSdk.init(getApplicationContext())，详细使用可参考sample 中 IApplication.java
public class IApplication extends Application {


    @Override
    public void onCreate() {
        super.onCreate();

        JDHomeSdk.init(getApplicationContext());

    }
}
```

### Step3
```
//布局文件引入
 <com.eju.cy.drawlibrary.view.JddDrawRoomView
        android:id="@+id/jdd_view"
        android:layout_width="match_parent"
        android:layout_height="match_parent"/>
```

### Step4
```
//layout对应控制器中实现   EjuDrawObserver，且在对应生命周期调用生命周期方法，详细使用可参考sample 中 TestActivity.java
public class TestActivity extends AppCompatActivity implements EjuDrawObserver {
    JddDrawRoomView jddView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        jddView = findViewById(R.id.jdd_view);

        String openUserId = "123456";//此ID为用户身份唯一标示
        jddView.initJddDrawRoomView(this, getSupportFragmentManager(),   openUserId );
        EjuDrawEventCar.getDefault().register(this);


    }

    @Override
    public void update(Object obj) {
        LogUtils.w("埋点回调" + (String) obj);

    }

    @Override
    protected void onPause() {
        super.onPause();
        jddView.onPause();
    }


    @Override
    protected void onResume() {
        super.onResume();
        jddView.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EjuDrawEventCar.getDefault().unregister(this);
        jddView.onDestroy();

    }
}
```

### Step5
```
//添加混淆配置
-keep class com.eju.cy.drawlibrary.**{*;}
-dontwarn com.eju.cy.drawlibrary.**

-dontwarn com.tencent.smtt.**
-keep public class com.tencent.smtt.**{*;}
-dontwarn com.tencent.tbs.**
-keep public class com.tencent.tbs.**{*;}

-keep class com.wang.avi.** { *; }
-keep class com.wang.avi.indicators.** { *; }

```
