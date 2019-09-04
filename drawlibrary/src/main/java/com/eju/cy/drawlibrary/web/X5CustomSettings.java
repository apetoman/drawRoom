package com.eju.cy.drawlibrary.web;

import com.just.agentwebX5.WebDefaultSettingsManager;
import com.just.agentwebX5.WebListenerManager;
import com.just.agentwebX5.WebSettings;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebView;

public class X5CustomSettings
        extends WebDefaultSettingsManager {
    public X5CustomSettings() {
        super();
    }



    @Override
    public WebSettings toSetting(WebView webView) {
        super.toSetting(webView);
        getWebSettings().setBlockNetworkImage(false);//是否阻塞加载网络图片  协议http or https
        getWebSettings().setAllowFileAccess(true); //允许加载本地文件html  file协议, 这可能会造成不安全 , 建议重写关闭
        getWebSettings().setAllowFileAccessFromFileURLs(true); //通过 file url 加载的 Javascript 读取其他的本地文件 .建议关闭
        getWebSettings().setAllowUniversalAccessFromFileURLs(true);//允许通过 file url 加载的 Javascript 可以访问其他的源，包括其他的文件和 http，https 等其他的源
//        getWebSettings().setUserAgent("_JDMF");
        getWebSettings().setUserAgentString(getWebSettings().getUserAgentString().concat("cy" + "1"));

        return this;
    }

    @Override
    public WebListenerManager setWebChromeClient(WebView webview, WebChromeClient webChromeClient) {
        return super.setWebChromeClient(webview, webChromeClient);
    }
}
