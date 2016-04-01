package com.capstone.innovationproject.routeguidance;

import android.os.AsyncTask;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class GetData extends AsyncTask<String, String, String> {
    public AsyncResponse delegate = null;
    HttpURLConnection urlConnection;
    public URL url;

    public GetData(URL urli) {
        url = urli;
    }

    @Override
    protected String doInBackground(String... args) {
        StringBuilder result = new StringBuilder();
            try {
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());

                BufferedReader reader = new BufferedReader(new InputStreamReader(in));

                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                urlConnection.disconnect();
            }
            return result.toString();

    }

    @Override
    protected void onPostExecute(String result) {
        //Do something with the JSON string
        delegate.processFinish(result);
    }

}