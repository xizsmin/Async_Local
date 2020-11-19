package com.example.async_local;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class RequestJava {
    private String mAddress;
    private URL mUrl;

    RequestJava(String address) {
        this.mAddress = address;
    }

    public String requestHttpJava() {

        StringBuilder stringBuilder = new StringBuilder();

        try {
            this.mUrl = new URL(Constants.REQUEST_URL_PREFIX + mAddress);
            HttpURLConnection httpURLConnection = (HttpURLConnection) mUrl.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setRequestProperty("Authorization", "KakaoAK " + Constants.REST_API_KEY);

            try {
                if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));

                    String tmp;
                    while ((tmp = bufferedReader.readLine()) != null) {
                        stringBuilder.append(tmp);
                    }
                    return stringBuilder.toString();

                }

            } finally {
                httpURLConnection.disconnect();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

}
