package com.lstu.oiproject;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class TestActivity extends AppCompatActivity {

    WebView mWebView; String download_file_path = "";
    String pathToVideo, ID,  IndexEmployee, EmployeeName, TestURL;

    private class MyWebViewClient extends WebViewClient

    {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url)
        {
            view.loadUrl(url);
            return true;
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        Intent intent = getIntent();
        IndexEmployee = intent.getStringExtra("IndexEmployee");
        EmployeeName = intent.getStringExtra("EmployeeName");
        download_file_path = intent.getStringExtra("URL");
        pathToVideo = intent.getStringExtra("VideoURL");
        ID = intent.getStringExtra("ID");
        TestURL = intent.getStringExtra("TestURL");
        if (TestURL=="")
        {
            Toast toast = Toast.makeText(getApplicationContext(),
                    "Для вас сегодня тестов нет!", Toast.LENGTH_SHORT);
            toast.show();
            Intent intent1 = new Intent(TestActivity.this, EmployeeActivity.class);
            intent.putExtra("EmployeeName",  EmployeeName);
            intent.putExtra("URL",download_file_path);
            intent.putExtra("VideoURL", pathToVideo);
            intent.putExtra("ID", ID);
            intent.putExtra("IndexEmployee", IndexEmployee);
            startActivity(intent1);
            finish();
        }
        mWebView = (WebView) findViewById(R.id.webView);
        mWebView.setWebViewClient(new MyWebViewClient());
        // включаем поддержку JavaScript
        mWebView.getSettings().setJavaScriptEnabled(true);
        // указываем страницу загрузки
        mWebView.loadUrl(TestURL);
    }
    @Override
    public void onBackPressed() {
//        if(mWebView.canGoBack()) {
//            mWebView.goBack();
//        } else {
//            super.onBackPressed();
//        }
        Intent intent = new Intent(TestActivity.this, EmployeeActivity.class);
        intent.putExtra("EmployeeName",  EmployeeName);
        intent.putExtra("URL",download_file_path);
        intent.putExtra("VideoURL", pathToVideo);
        intent.putExtra("ID", ID);
        intent.putExtra("IndexEmployee", IndexEmployee);
        startActivity(intent);
        finish();
    }

}

