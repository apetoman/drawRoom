package com.eju.cy.drawlibrary.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.blankj.utilcode.util.LogUtils;
import com.eju.cy.drawlibrary.R;
import com.eju.cy.drawlibrary.plug.EjuDrawEventCar;
import com.eju.cy.drawlibrary.plug.EjuDrawObserver;
import com.eju.cy.drawlibrary.view.JddDrawRoomView;

public class TestActivity extends AppCompatActivity implements EjuDrawObserver {
    JddDrawRoomView jddView;
    String APP_Id = "NGRU1AVC846EQSTB";
    String OPEN_USER_ID = "123456";
    String COMPANY_ID = "52";
    String APPPRIVATE_KEY = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCvUuJnHIlvUDHRcaQlcBkrpiexTUgO5aKpVoABLwpNbnHFDoLGwMXeOMwhykAFqNpxtUp8m5d+XZRdSJo5zfpMsuLzq4o4QKm8EHMQkiBuynUJxTpDbyw5rSRFGYbu83iGeTA+y+mPh9RJnyR3uG2BWk6W12n9xnzhY53BStR63fKcKs+li1lT7DvRiU8vv+aHYQNRyu8wlLmiMDa4TFx4AKBM6r2r9ZMVrMhiK+qrtsMVWOvXmNQeoiGe4pwqFwPTAQi+SSiS6mWBHeTHyTwUwr2rSyZaTudd3vLLQJpd2O1i1HVKaRnmK0Ixy/yhmWO9fH8+P+e0Rq7WSx0d0TB3AgMBAAECggEBAJf7leAk0M76CfWyOcVqg6dfBhGhGNIxJuz820IrcRbmoyFcDuoUunKFcg/or884LQVdTxDuIEme/bpP8cIiWNScTjlFfzB8fadV6yl2Qz9HqmWp33QNr5zgBw0Pr/T8goKwE66cPf/6k4CuwII4ElWL34zLeEpSAnewT1T8dW17//Xi2hy7ccFRITC9gVWnwWt2AFCvswpgodCskpboNML8YoRPzmQuy90RWeJ7Tk158jeNCYfcXyLlApvSPobAx9Nu/6D5XSbjfKoARr8b9azyC5r0RDzZF5B/ypQictnKHz/91lTtEU20RW3PN040bISskOO6vVCzQgkOW6wwBOECgYEAvXTZO7/K7uxYhBeM2tnNO/3mcaI433qBIygrnN1Y+bsGqa84im41N6xXGZr+J6RjkbLkSrjo1TYLQ1CYgf1vmklZx5ilQRdIbWBNpvYQAffDNFxYULXxzW9lND0IbNFni+/I4LjzUEmg6BW5/eXdx5PIWqbhna7jxtcpdDa/QYUCgYEA7OdIkMf35sTuInVWVSOsTZ/+axVj3b0Ecku9Ggdi3iwzGAMSmax4PcxNDcRly8jxCJtTZ9Wbr9Iqltp/Q3a4G/W7knkbSbuLUIyKcqIlIqQ33ClwwaFGiG6GBeoRJEhzW9D3HLrUJy51ZMCvEoSGhBhudCZEYih+xxCPLCmeDMsCgYAhj0Y/wDyZUAJp+6X2ymgBfXtJm7vJUnD3olD/a3IsYoXOnvw8AUOqBfwzy/HDYepFT9QCrHiJ9BXQqcEqHZOcV+vwYEi9m/s3bLy0m5fAUXwhlU4Llf8sLdRWiY0pgXp/Hk2OCRUIntJC6j5VDFfZ14LBFBiZDvbILSrprBz65QKBgQDEcHfMjfQy5+Lqsc9Xo8/xQhTOKJt5t41jVQhF+A/0WEQ5yfp3cPr3i1vtaYhbdZDgaSO8+vQw0527HwzeHShHDvltWHzXI+s+bHs02NzgH7muFrLH7Ho3ESaS6ucx5d26KcluikD3CGARnnDNcxSzniqgp0aW+is9165QmWXUBwKBgFq6jEAq+2kDcgjKam97bhsmg9eOsHIqYQuHQy6bNScxC6Amgg0Re1UzMvPUF0YHjw4JnpvLXzZmUYiN/7WVMTsChhIlWK49aVlgn0HgnS94fpNtxZwHi8HXW61i5LTsQoR7AUSZ2Uq+JM2DMAewcw49Bda2TQHxZcPu0Zts5zKQ";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        jddView = findViewById(R.id.jdd_view);
        jddView.initJddDrawRoomView(this, getSupportFragmentManager(), APP_Id, OPEN_USER_ID, COMPANY_ID, APPPRIVATE_KEY);
        EjuDrawEventCar.getDefault().register(this);


    }

    @Override
    public void update(Object obj) {
        LogUtils.w("我接收到的是" + (String) obj);

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
