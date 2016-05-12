package pt.ulisboa.tecnico.cmov.ubibike.services;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;

import pt.ulisboa.tecnico.cmov.ubibike.exceptions.ErrorCodeException;

/**
 * Created by diogo on 06-05-2016.
 */
public class UBIClient {


    public  UBIClient() {

    }


    public String requestRegister(String myurl) throws Exception {
        InputStream is = null;
        // Only display the first 500 characters of the retrieved
        // web page content.
        int len = 500;

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
            Log.d("TAG", "The response is: " + response);
            String result = null;
            if (response == 200) {
                is = conn.getInputStream();
                // Convert the InputStream into a string
                result = readIt(is, len);
            } else
                throw new ErrorCodeException(response); // throw error code

            return result;
            // Makes sure that the InputStream is closed after the app is
            // finished using it.
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    public String GET(String myurl) throws Exception {
        InputStream is = null;
        // Only display the first 500 characters of the retrieved
        // web page content.
        int len = 500;

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
            Log.d("TAG", "The response is: " + response);
            String result = null;
            if (response == 200) {
                is = conn.getInputStream();
                // Convert the InputStream into a string
                result = readIt(is, len);
            } else
                throw new ErrorCodeException(response); // throw error code

            return result;
            // Makes sure that the InputStream is closed after the app is
            // finished using it.
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    // Reads an InputStream and converts it to a String.
    public String readIt(InputStream stream, int len) throws IOException {
        char[] buffer = new char[len];
        StringBuilder out = new StringBuilder();
        Reader in = new InputStreamReader(stream, "UTF-8");
            for (;;) {
                int rsz = in.read(buffer, 0, buffer.length);
                if (rsz < 0)
                    break;
                out.append(buffer, 0, rsz);
            }
        return out.toString();
    }
}
