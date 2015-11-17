package lu.kas.ws;

import android.support.annotation.Nullable;
import android.util.Log;
import android.webkit.WebView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import de.tavendo.autobahn.WebSocketConnection;
import de.tavendo.autobahn.WebSocketException;
import de.tavendo.autobahn.WebSocketHandler;

public class WebsocketJsInterface
{
    private static final String TAG = "de.tavendo.test1";

    public static final String JS_FACTORY = "WebsocketFactory";
    private static final String WINDOW_PREFIX = "AndroidWebSocket";
    private WebView webView;
    private static ArrayList<WebSocketConnection> connections = new ArrayList<>();

    public WebsocketJsInterface( WebView webView )
    {
        Log.d(TAG, webView.getUrl() == null ? "null" : webView.getUrl() );
        this.webView = webView;
    }

    @android.webkit.JavascriptInterface
    public int create(final String url){
        //url = "ws://echo.websocket.org";
        final WebSocketConnection connection = new WebSocketConnection();
        connections.add(connection);
        final int id = connections.indexOf(connection);
        Log.d(TAG, id + " oncreate");

        try {
            connection.connect(url, new WebSocketHandler() {

                @Override
                public void onOpen() {
                    callJsWsMethod( id, "onopen", null );
                }

                @Override
                public void onTextMessage(String payload) {
                    callJsWsMethod( id, "onmessage", payload );
                }

                @Override
                public void onClose(int code, String reason) {
                    callJsWsMethod( id, "onclose", reason );
                }
            });
        } catch (WebSocketException e) {
            callJsWsMethod( id, "onerror", e.getLocalizedMessage());
        }
        return id;
    }

    @android.webkit.JavascriptInterface
    public void send(int id, String message){
        connections.get(id).sendTextMessage(message);
    }

    private void callJs( String script )
    {
        webView.loadUrl("javascript:" + script);
    }
    private void callJsWsMethod( int id, String method, @Nullable String data )
    {
        Log.d(TAG, id + " " + method + " " + data );
        webView.loadUrl("javascript:window." + WINDOW_PREFIX + id + "." + method + "(\""+data+"\")");
    }
}
