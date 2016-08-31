package main.app.tbee3app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

/**
 * Created by sriven on 12/28/2015.
 */
public class PaymentActivity extends Activity {
    String pack_id,pack_price;
    ProgressBar progress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Settings.forceRTLIfSupported(this);
        setContentView(R.layout.payment_screen);
        WebView webView = (WebView) findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.addJavascriptInterface(new WebAppInterface(this), "app");
        webView.setWebViewClient(new WebViewClient());
        webView.setWebChromeClient(new MyWebViewClient());
        String cust_id = getIntent().getStringExtra("cust_id");
        pack_id = getIntent().getStringExtra("pack_id");
        pack_price = getIntent().getStringExtra("pack_price");
        webView.loadUrl("http://clients.yellowsoft.in/tbee3/Tap2.php?cust_id=" + cust_id + "&package_id=" + pack_id + "price=" + pack_price);
        progress = (ProgressBar) findViewById(R.id.progressBar);
        progress.setMax(100);
        progress.setProgress(0);

    }
    public class WebAppInterface {
        Context mContext;

        WebAppInterface(Context c) {
            mContext = c;
        }

        @JavascriptInterface
        public void send_message(String toast,Boolean success) {
            if(success){
                Toast.makeText(mContext, "payment success", Toast.LENGTH_SHORT).show();
                Intent intent=new Intent();
                intent.putExtra("msg","OK");
                intent.putExtra("pack_id",pack_id);
                setResult(7, intent);
                finish();
            }

            else{
                Toast.makeText(mContext, "payment failed", Toast.LENGTH_SHORT).show();
                Intent intent=new Intent();
                intent.putExtra("msg","NotOK");
                intent.putExtra("pack_id",pack_id);
                setResult(7, intent);
                finish();
            }
        }
    }

    private class MyWebViewClient extends WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            PaymentActivity.this.setValue(newProgress);
            super.onProgressChanged(view, newProgress);
        }
    }
    public void setValue(int progress) {
        this.progress.setProgress(progress);
    }


}
