package lu.kas.vsy;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.InputType;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class JavaScriptInterface
{
    private Context context;
    private boolean booleanResult;
    private int intResult;
    private Object objectResult;

    public JavaScriptInterface( Context context )
    {
        this.context = context;
    }

    @android.webkit.JavascriptInterface
    public void toastInfo( String message )
    {
        Toast toast = Toast.makeText( context, message, Toast.LENGTH_SHORT );
        TextView textView = (TextView)toast.getView().findViewById(android.R.id.message);
        //textView.setTextColor( Color.BLUE );
        toast.show();
    }

    @android.webkit.JavascriptInterface
    public void toastSuccess( String message )
    {
        Toast toast = Toast.makeText( context, message, Toast.LENGTH_SHORT );
        TextView textView = (TextView)toast.getView().findViewById(android.R.id.message);
        textView.setTextColor( Color.GREEN );
        toast.show();
    }

    @android.webkit.JavascriptInterface
    public void toastError( String message )
    {
        Toast toast = Toast.makeText( context, message, Toast.LENGTH_SHORT );
        TextView textView = (TextView)toast.getView().findViewById(android.R.id.message);
        textView.setTextColor( Color.RED );
        toast.show();
    }


    @android.webkit.JavascriptInterface
    public boolean askYesNo( final String title, final String yes, final String no )
    {
        booleanResult = false;

        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message mesg) {
                throw new RuntimeException();
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder( context );
        //builder.setCancelable(false);
        builder.setTitle(title);
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                handler.sendMessage(handler.obtainMessage());
            }
        });

        builder.setPositiveButton(yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                booleanResult = true;
                handler.sendMessage(handler.obtainMessage());
            }
        });
        builder.setNegativeButton(no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                handler.sendMessage(handler.obtainMessage());
            }
        });
        builder.create().show();

        try { Looper.loop(); }
        catch(RuntimeException e) {
            Toast.makeText( context, e.getLocalizedMessage(), Toast.LENGTH_LONG );
        }

        return booleanResult;
    }

    @android.webkit.JavascriptInterface
    public String askText( final String title, final String value )
    {
        objectResult = "";

        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message mesg) {
                throw new RuntimeException();
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder( context );
        //builder.setCancelable(false);
        builder.setTitle(title);
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                handler.sendMessage(handler.obtainMessage());
            }
        });

        final EditText input = new EditText(context);
        input.setInputType(InputType.TYPE_CLASS_TEXT );
        input.setText( value );
        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                objectResult = input.getText().toString();
                handler.sendMessage(handler.obtainMessage());
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                handler.sendMessage(handler.obtainMessage());
            }
        });

        builder.create().show();

        try { Looper.loop(); }
        catch(RuntimeException e) {
            Toast.makeText( context, e.getLocalizedMessage(), Toast.LENGTH_LONG );
        }

        return (String)objectResult;
    }

    @android.webkit.JavascriptInterface
    public String askPassword( final String title )
    {
        objectResult = "";

        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message mesg) {
                throw new RuntimeException();
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder( context );
        //builder.setCancelable(false);
        builder.setTitle(title);
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                handler.sendMessage(handler.obtainMessage());
            }
        });

        final EditText input = new EditText(context);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                objectResult = input.getText().toString();
                handler.sendMessage(handler.obtainMessage());
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                handler.sendMessage(handler.obtainMessage());
            }
        });

        builder.create().show();

        try { Looper.loop(); }
        catch(RuntimeException e) {
            Toast.makeText( context, e.getLocalizedMessage(), Toast.LENGTH_LONG );
        }

        return (String)objectResult;
    }

    @android.webkit.JavascriptInterface
    public int askSingleChoice( final String title, final String[] list )
    {
        intResult = -1;

        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message mesg) {
                throw new RuntimeException();
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder( context );
        //builder.setCancelable(false);
        builder.setTitle(title);
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                handler.sendMessage(handler.obtainMessage());
            }
        });

        builder.setItems(list, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                intResult = which;
                handler.sendMessage(handler.obtainMessage());
            }
        });

        builder.create().show();

        try { Looper.loop(); }
        catch(RuntimeException e) {
            Toast.makeText( context, e.getLocalizedMessage(), Toast.LENGTH_LONG );
        }

        return intResult;
    }
}
