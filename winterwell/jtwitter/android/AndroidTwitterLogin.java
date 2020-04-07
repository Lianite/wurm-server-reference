// 
// Decompiled by Procyon v0.5.30
// 

package winterwell.jtwitter.android;

import java.net.URI;
import android.os.Handler;
import android.widget.Toast;
import android.view.MotionEvent;
import winterwell.jtwitter.Twitter;
import android.net.Uri;
import android.graphics.Bitmap;
import android.webkit.WebViewClient;
import android.view.View;
import android.app.Dialog;
import android.content.Context;
import android.webkit.WebView;
import android.util.Log;
import winterwell.jtwitter.OAuthSignpostClient;
import android.app.Activity;

public abstract class AndroidTwitterLogin
{
    private String callbackUrl;
    private Activity context;
    OAuthSignpostClient client;
    private String authoriseMessage;
    private String consumerSecret;
    private String consumerKey;
    
    public void setAuthoriseMessage(final String authoriseMessage) {
        this.authoriseMessage = authoriseMessage;
    }
    
    public AndroidTwitterLogin(final Activity myActivity, final String oauthAppKey, final String oauthAppSecret, final String calbackUrl) {
        this.authoriseMessage = "Please authorize with Twitter";
        this.context = myActivity;
        this.consumerKey = oauthAppKey;
        this.consumerSecret = oauthAppSecret;
        this.callbackUrl = calbackUrl;
        this.client = new OAuthSignpostClient(this.consumerKey, this.consumerSecret, this.callbackUrl);
    }
    
    public final void run() {
        Log.i("jtwitter", "TwitterAuth run!");
        final WebView webview = new WebView((Context)this.context);
        webview.setBackgroundColor(-16777216);
        webview.setVisibility(0);
        final Dialog dialog = new Dialog((Context)this.context, 16973834);
        dialog.setContentView((View)webview);
        dialog.show();
        webview.getSettings().setJavaScriptEnabled(true);
        webview.setWebViewClient((WebViewClient)new WebViewClient() {
            public void onPageStarted(final WebView view, final String url, final Bitmap favicon) {
                Log.d("jtwitter", "url: " + url);
                if (!url.contains(AndroidTwitterLogin.this.callbackUrl)) {
                    return;
                }
                final Uri uri = Uri.parse(url);
                final String verifier = uri.getQueryParameter("oauth_verifier");
                if (verifier == null) {
                    Log.i("jtwitter", "Auth-fail: " + url);
                    dialog.dismiss();
                    AndroidTwitterLogin.this.onFail(new Exception(url));
                    return;
                }
                AndroidTwitterLogin.this.client.setAuthorizationCode(verifier);
                final String[] tokens = AndroidTwitterLogin.this.client.getAccessToken();
                final Twitter jtwitter = new Twitter(null, AndroidTwitterLogin.this.client);
                Log.i("jtwitter", "Authorised :)");
                dialog.dismiss();
                AndroidTwitterLogin.this.onSuccess(jtwitter, tokens);
            }
            
            public void onPageFinished(final WebView view, final String url) {
                Log.i("jtwitter", "url finished: " + url);
            }
        });
        webview.requestFocus(130);
        webview.setOnTouchListener((View.OnTouchListener)new View.OnTouchListener() {
            public boolean onTouch(final View v, final MotionEvent e) {
                if ((e.getAction() == 0 || e.getAction() == 1) && !v.hasFocus()) {
                    v.requestFocus();
                }
                return false;
            }
        });
        Toast.makeText((Context)this.context, (CharSequence)this.authoriseMessage, 0).show();
        final Handler handler = new Handler();
        handler.postDelayed((Runnable)new Runnable() {
            @Override
            public void run() {
                try {
                    final URI authUrl = AndroidTwitterLogin.this.client.authorizeUrl();
                    webview.loadUrl(authUrl.toString());
                }
                catch (Exception e) {
                    AndroidTwitterLogin.this.onFail(e);
                }
            }
        }, 10L);
    }
    
    protected abstract void onSuccess(final Twitter p0, final String[] p1);
    
    protected void onFail(final Exception e) {
        Toast.makeText((Context)this.context, (CharSequence)"Twitter authorisation failed?!", 1).show();
        Log.w("jtwitter", e.toString());
    }
}
