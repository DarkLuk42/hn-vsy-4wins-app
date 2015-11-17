package lu.kas.vsy;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.Toast;

import lu.kas.ws.WebsocketJsInterface;

public class AdvancedWebView extends WebView
{
    private WebsocketJsInterface websocketJsInterface;
    public AdvancedWebView(Context context) {
        super(context);
        init();
    }

    public AdvancedWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AdvancedWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void init( )
    {
        getSettings().setJavaScriptEnabled(true);
        getSettings().setLoadsImagesAutomatically(true);
        addJavascriptInterface(new JavaScriptInterface(this.getContext()), "Android");
        addJavascriptInterface( new WebsocketJsInterface(this),WebsocketJsInterface.JS_FACTORY);
    }

}
