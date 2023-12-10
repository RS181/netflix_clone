package com.example.NETFRIXAPP;

import android.os.AsyncTask;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class CheckServerTask extends AsyncTask<Void, Void, Boolean> {

    private ServerStatusListener serverStatusListener;

    public CheckServerTask(ServerStatusListener listener) {
        this.serverStatusListener = listener;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        try {
            // Replace the URL with the actual API endpoint
            URL url = new URL("https://35.195.14.2/api/upload");
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            try {
                // Set a timeout for the connection
                urlConnection.setConnectTimeout(5000); // 5 seconds timeout

                // Check if the response code indicates success
                int statusCode = urlConnection.getResponseCode();
                return statusCode == HttpURLConnection.HTTP_OK;
            } finally {
                urlConnection.disconnect();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean isServerOnline) {
        // Notify the listener in MainActivity about the server status
        if (serverStatusListener != null) {
            serverStatusListener.onServerStatusChanged(isServerOnline);
        }
    }
}