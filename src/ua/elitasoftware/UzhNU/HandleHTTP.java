package ua.elitasoftware.UzhNU;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class HandleHTTP {

    public String makeRequest(String url){
        try {
            HttpGet httpGet = new HttpGet(url);
//            HttpParams httpParameters = new BasicHttpParams();
//            int timeoutConnection = 100;
//            HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);

//            int timeoutSocket = 120;
//            HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);

            DefaultHttpClient httpClient = new DefaultHttpClient();
//            HttpParams params = httpClient.getParams();
//            HttpConnectionParams.setConnectionTimeout(params, 200);
//            HttpConnectionParams.setSoTimeout(params, 500);
//            httpClient.setParams(params);
            HttpEntity httpEntity = null;
            HttpResponse httpResponse = null;

            httpResponse = httpClient.execute(httpGet);

            httpEntity = httpResponse.getEntity();
            return EntityUtils.toString(httpEntity);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

}
