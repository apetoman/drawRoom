package com.eju.cy.drawlibrary.pop;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.widget.TextView;

import com.blankj.utilcode.util.ToastUtils;
import com.eju.cy.drawlibrary.R;

import razerdp.basepopup.BasePopupWindow;

public class MorePopup extends BasePopupWindow implements View.OnClickListener {

    private TextView tv_draw_img, tv_limian_img, tv_3_img, tv_jilu_img, tv_qingdan_img;


    public MorePopup(Context context, MoreInterface moreInterface) {
        super(context);
        this.moreInterface = moreInterface;
    }

    private MoreInterface moreInterface;

    public MorePopup(Context context) {
        super(context);
    }

    @Override
    public View onCreateContentView() {
        View view = createPopupById(R.layout.more_pop_layout);
        tv_draw_img = view.findViewById(R.id.tv_draw_img);
        tv_limian_img = view.findViewById(R.id.tv_limian_img);
        tv_3_img = view.findViewById(R.id.tv_3_img);

        tv_jilu_img = view.findViewById(R.id.tv_jilu_img);
        tv_qingdan_img = view.findViewById(R.id.tv_qingdan_img);

        tv_draw_img.setOnClickListener(this);
        tv_limian_img.setOnClickListener(this);
        tv_3_img.setOnClickListener(this);

        tv_jilu_img.setOnClickListener(this);
        tv_qingdan_img.setOnClickListener(this);


        return view;
    }

    // 以下为可选代码（非必须实现）
    // 返回作用于PopupWindow的show和dismiss动画，本库提供了默认的几款动画，这里可以自由实现
    @Override
    protected Animation onCreateShowAnimation() {
        return getDefaultScaleAnimation(true);
    }

    @Override
    protected Animation onCreateDismissAnimation() {
        return getDefaultScaleAnimation(false);
    }

    @Override
    public void onClick(View v) {

        if (v == tv_draw_img) {
            moreInterface.popDraw();
        } else if (v == tv_limian_img) {
            moreInterface.popFacade();
        } else if (v == tv_3_img) {
            moreInterface.pop3D();
        } else if (v == tv_jilu_img) {
            moreInterface.popHistory();
        } else if (v == tv_qingdan_img) {
            moreInterface.popRepertoire();
        }

        dismiss();

    }
}
