package lu.kas.vsy;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.Window;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;


public class MainActivity extends AppCompatActivity implements View.OnClickListener, PopupMenu.OnMenuItemClickListener {

    ProgressDialog mProgressDialog;
    File localFile;

    private class DownloadTask extends AsyncTask<String, Integer, String>
    {
        Context context;

        public DownloadTask( Context context )
        {
            this.context = context;
        }

        @Override
        protected String doInBackground(String... params) {
            InputStream input = null;
            OutputStream output = null;
            HttpURLConnection connection = null;
            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                // expect HTTP 200 OK, so we don't mistakenly save error report
                // instead of the file
                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    url = new URL(params[1]);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.connect();

                    // expect HTTP 200 OK, so we don't mistakenly save error report
                    // instead of the file
                    if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                        return "Server returned HTTP " + connection.getResponseCode()
                                + " " + connection.getResponseMessage();
                    }
                }

                // this will be useful to display download percentage
                // might be -1: server did not report the length
                int fileLength = connection.getContentLength();

                // download the file
                input = connection.getInputStream();
                if( localFile.exists() )
                {
                    localFile.delete();
                }
                localFile.createNewFile();
                output = new FileOutputStream(localFile.getAbsolutePath());

                byte data[] = new byte[4096];
                long total = 0;
                int count;
                while ((count = input.read(data)) != -1) {
                    // allow canceling with back button
                    if (isCancelled()) {
                        input.close();
                        return null;
                    }
                    total += count;
                    // publishing the progress....
                    if (fileLength > 0) // only if total length is known
                        publishProgress((int) (total * 100 / fileLength));
                    output.write(data, 0, count);
                }
            } catch (Exception e) {
                return e.toString();
            } finally {
                try {
                    if (output != null)
                        output.close();
                    if (input != null)
                        input.close();
                } catch (IOException ignored) {
                }

                if (connection != null)
                    connection.disconnect();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog.show();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
            // if we get here, length is known, now set indeterminate to false
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setMax(100);
            mProgressDialog.setProgress(progress[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            mProgressDialog.dismiss();
            if (result != null) {
                Toast.makeText(context, getString(R.string.update_error) + " " + result, Toast.LENGTH_LONG).show();
            }
            else
            {
                WebView webView = (WebView)findViewById(R.id.webView);
                webView.reload();
                Toast.makeText(context, getString(R.string.update_success), Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        localFile = new File(getFilesDir() + "/" + getString(R.string.local_file));

        mProgressDialog = new ProgressDialog(MainActivity.this);
        mProgressDialog.setMessage(getString(R.string.update_progress));
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setCancelable(true);

        //update();

        WebView webView = (WebView)findViewById(R.id.webView);
        webView.loadUrl("file://" + localFile.getAbsolutePath());


        ImageButton imageButton = (ImageButton)findViewById(R.id.button_menu);
        imageButton.setOnClickListener( this );
        if( ViewConfiguration.get(this).hasPermanentMenuKey() )
        {
            imageButton.setVisibility( View.INVISIBLE );
        }
    }

    public void onClick( View view ) {
        if(view.getId() == R.id.button_menu )
        {
            PopupMenu popup = new PopupMenu(MainActivity.this, view );
            //Inflating the Popup using xml file
            popup.getMenuInflater().inflate(R.menu.menu_main, popup.getMenu());

            //registering popup with OnMenuItemClickListener
            popup.setOnMenuItemClickListener(this);

            popup.show();//showing popup menu
        }
    }

    protected void update()
    {
        localFile.getParentFile().mkdirs();
        if( localFile.getParentFile().canWrite() )
        {
            final DownloadTask downloadTask = new DownloadTask(MainActivity.this);
            downloadTask.execute(getString(R.string.remote_file), getString(R.string.remote_file_fallback));

            mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    downloadTask.cancel(true);
                }
            });
        }
        else
        {
            Toast.makeText(this, getString(R.string.update_error) + " Can't write to File " + localFile.getAbsolutePath(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_update)
        {
            update();
            return true;
        }
        else if (id == R.id.action_reload)
        {
            finish();
            startActivity(getIntent());
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        return this.onOptionsItemSelected( item );
    }
}
