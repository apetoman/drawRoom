package com.eju.cy.drawlibrary.plug;

/**
 * @author-eju
 * @time-2018/6/19 下午3:05
 * @function- 存放App定义的接口回调
 */
public interface DialogInterface {
    void dialogCommit(String msg);//dialog确定

    void dialogFinish(String msg);//dialog取消
    void dialogFinish();//dialog取消
}
