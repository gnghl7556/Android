package com.example.bike_project;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;



@SuppressLint({ "SetJavaScriptEnabled", "NewApi" })
public class speedActivity extends Activity {
    Button btnback2;
    String temp_addres = null;
    private String mInputUrl = "http://nams.ddns.net:8080/web2/test.jsp";

    private EditText mEditText;
    private WebView mWebView;
    private WebSettings mWebSettings;
    private ProgressBar mProgressBar;
    private InputMethodManager mInputMethodManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.speed_layout2);
        final String address = getIntent().getStringExtra("bluetooth_address");
        temp_addres = address;

        btnback2 = findViewById(R.id.btnback2);

        mEditText = (EditText)findViewById(R.id.edit_Url);
        mWebView = (WebView)findViewById(R.id.webview);

        mWebView.setWebViewClient(new WebViewClient());                 //클릭 시 새창 안뜨게
        mWebSettings = mWebView.getSettings();                          //세부 세팅 등록
        mWebSettings.setJavaScriptEnabled(true);                        //웹페이지 자바스크립트 허용 여부
        mWebSettings.setSupportMultipleWindows(false);                  //새창 띄우기 허용 여부
        mWebSettings.setJavaScriptCanOpenWindowsAutomatically(false);   //자바스크립트 새창 띄우기(멀티뷰)허용 여부
        mWebSettings.setLoadWithOverviewMode(true);                     //메타태그 허용 여부
        mWebSettings.setUseWideViewPort(true);                          //화면 사이즈 맞추기 허용 여부
        mWebSettings.setSupportZoom(false);                             //화면 줌 허용 여부
        mWebSettings.setBuiltInZoomControls(false);                     //화면 확대 축소 허용 여부
        mWebSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);//컨텐츠 사이즈 맞추기
        mWebSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);           //브라우저 캐시 허용여부
        mWebSettings.setDomStorageEnabled(true);                        //로컬저장소 허용 여부


        mWebView.loadUrl(mInputUrl);
        mWebView.setVerticalScrollBarEnabled(false);
        mWebView.setHorizontalScrollBarEnabled(false);
        mWebView.setDefaultFocusHighlightEnabled(false);

        btnback2.setOnClickListener(view -> {
            onClickButtonBack();
        });
//        mProgressBar = (ProgressBar)findViewById(R.id.progressBar);
//        findViewById(R.id.btn_go).setOnClickListener(onClickListener);
//
//        mInputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
//
//        mWebView.setWebChromeClient(new webViewChrome());
//        mWebView.setWebViewClient(new webViewClient());
//
//        mWebSettings.setBuiltInZoomControls(true);
//
//        mWebView.loadUrl(mInputUrl);
//        mEditText.setHint(mInputUrl);
    }

    private void onClickButtonBack() { //저장해놨던 데이터 그대로 2에 전달
        Intent intent = new Intent(getApplicationContext(), serviceActivity.class);
        intent.putExtra("bluetooth_address",temp_addres);
        startActivity(intent);
    }

    //Button Event를 처리
    View.OnClickListener onClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            switch(v.getId()) {
                case R.id.btn_go:
                    //InputMethodManager를 이용하여 키보드를 숨김
                    mInputMethodManager.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);
                    mInputUrl = httpInputCheck(mEditText.getText().toString());

                    if(mInputUrl == null) break;

                    //페이지를 불러온다
                    mWebView.loadUrl(mInputUrl);
                    mEditText.setText("");
                    mEditText.setHint(mInputUrl);
                    break;
            }
        }
    };

    class webViewChrome extends WebChromeClient {

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            //현제 페이지 진행사항을 ProgressBar를 통해 알린다.
            if(newProgress < 100) {
                mProgressBar.setProgress(newProgress);
            } else {
                mProgressBar.setVisibility(View.INVISIBLE);
                mProgressBar.setLayoutParams(new LinearLayout.LayoutParams(0, 0));
            }
        }
    }

    class webViewClient extends WebViewClient {

        //Loading이 시작되면 ProgressBar처리를 한다.
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            mProgressBar.setVisibility(View.VISIBLE);
            mProgressBar.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 15));
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            mWebSettings.setJavaScriptEnabled(true);
            mEditText.setHint(url);
            super.onPageFinished(view, url);
        }
    }

    //http://를 체크하여 추가한다.
    private String httpInputCheck(String url) {
        if(url.isEmpty()) return null;

        if(url.indexOf("http://") == ("http://").length()) return url;
        else return "http://" + url;
    }
}
