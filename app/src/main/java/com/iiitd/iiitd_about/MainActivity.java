package com.iiitd.iiitd_about;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private TextView webdata;

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        String data1 = savedInstanceState.getString("data");
        webdata.setText(data1);
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        String data = webdata.getText().toString();
        outState.putString("data",data);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        webdata = (TextView) findViewById(R.id.web_text);
        webdata.setMovementMethod(new ScrollingMovementMethod());
        urldata();
    }

    public void urldata()
    {
        String stringUrl = "https://www.iiitd.ac.in/about";
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            new DownloadWebpageTask().execute(stringUrl);
        }
        else {
            webdata.setText("No network connection available.");
        }
    }

    //async task for loading data from website
    private class DownloadWebpageTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            // params comes from the execute() call: params[0] is the url.
            try {
                return downloadUrl(urls[0]);
            } catch (IOException e) {
                return "Unable to retrieve web page. URL may be invalid.";
            }
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            webdata.setText(result);
        }
    }

    private String downloadUrl(String myurl) throws IOException {

        InputStream is = null;
        int len = 2500;  //length of content to be taken from the website
        try {
            URL url = new URL(myurl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);

            // Starts the query
            conn.connect();
            int response = conn.getResponseCode();
            Log.d("iiitd-about", "The response is: " + response);//response code
            is = conn.getInputStream();

            // Convert the InputStream into a string
            String contentAsString = readIt(is, len);
            return contentAsString;

            // Makes sure that the InputStream is closed after the app is
            // finished using it.
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    public String readIt(InputStream stream, int len)
            throws IOException{
        Reader reader = null;
        BufferedReader br = null;
        reader = new InputStreamReader(stream, "UTF-8");
        char[] buffer = new char[len];
        reader.read(buffer);
        br = new BufferedReader(reader);
        String line="";
        while((line = br.readLine())!=null)  //displaying the restover data
            Log.d("iiitd-about",line);
        return new String(buffer);
    }
}
