package com.lstu.oiproject;

import android.content.Context;
import android.webkit.WebView;

/**
 * Created by Дмитрий on 01.06.2017.
 */

public class GifWebView extends WebView {

    public GifWebView(Context context, String path) {
        super(context);
        loadUrl(path);
    }
}