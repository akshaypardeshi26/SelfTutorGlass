package com.helloglass;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.*;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.*;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.apache.http.client.*;

import android.util.Log;

public class JSONParser {

    static InputStream is = null;
    static JSONObject jObj = null;
    static String json = "";

    // constructor
    public JSONParser() {

    }

    // function get json from url
    // by making HTTP POST or GET mehtod
    public JSONObject makeHttpRequest(String url, String method,
                                      List<NameValuePair> params) {

        // Making HTTP request
        try {

            // check for request method
            if(method == "POST"){
              /*  // request method is POST
                // defaultHttpClient
                System.out.println("inside makehttp1");
                DefaultHttpClient httpClient = new DefaultHttpClient();
                System.out.println("inside makehttp2");
                HttpPost httpPost = new HttpPost(url);
                System.out.println("inside makehttp3");
                httpPost.setEntity(new UrlEncodedFormEntity(params));
                System.out.println("inside makehttp4");

                HttpResponse httpResponse = httpClient.execute(httpPost);
                System.out.println("inside makehttp5");
                HttpEntity httpEntity = httpResponse.getEntity();
                System.out.println("inside makehttp6");
                is = httpEntity.getContent();
                System.out.println("inside makehttp6");*/
                JSONObject jsonObject = new JSONObject();
                try {

                    String newName="name1";
                    System.out.println("LENGTH #############################");
                    System.out.println(params.get(0).getValue().length());
                    System.out.println(params.get(1).getName());
                    System.out.println("LENGTH #############################");
                    jsonObject.put("imageData", params.get(0).getValue());
                    jsonObject.put(params.get(1).getName(), params.get(1).getValue());
                    jsonObject.put(params.get(2).getName(), params.get(2).getValue());
                } catch(JSONException e) {}

                JSONObject resultJSON = null;

                try {
                    HttpResponse response;
                    HttpParams httpParameters = new BasicHttpParams();
                    HttpConnectionParams.setConnectionTimeout(httpParameters, 10000000);
                    HttpConnectionParams.setSoTimeout(httpParameters, 10000000);
                    HttpClient httpClient = new DefaultHttpClient(httpParameters);
                    HttpPost putConnection = new HttpPost(url);
                    putConnection.setHeader("json", jsonObject.toString());
                    StringEntity se = new StringEntity(jsonObject.toString(), "UTF-8");
                    se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE,
                            "application/json"));
                    putConnection.setEntity(se);

                    try {
                        System.out.println("Timestamp - Send Post Request");
                        System.out.println(System.currentTimeMillis());
                        response = httpClient.execute(putConnection);
                        System.out.println("Timestamp - Receive Post Response");
                        System.out.println(System.currentTimeMillis());
                        resultJSON = new JSONObject(EntityUtils.toString(response.getEntity()));
                    } catch (ClientProtocolException e) {
                        System.out.println("ClientProtocolException");
                        e.printStackTrace();
                    } catch (IOException e)
                    {System.out.println("IOException");
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    System.out.println("Exception");
                    e.printStackTrace();
                }

                return resultJSON;


            }else if(method == "GET"){
                // request method is GET
                DefaultHttpClient httpClient = new DefaultHttpClient();
                String paramString = URLEncodedUtils.format(params, "utf-8");
                url += "?" + paramString;
                HttpGet httpGet = new HttpGet(url);

                HttpResponse httpResponse = httpClient.execute(httpGet);
                HttpEntity httpEntity = httpResponse.getEntity();
                is = httpEntity.getContent();
            }

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        /*

        try {
            System.out.println("inside makehttp7");
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    is, "iso-8859-1"), 8);
            System.out.println("inside makehttp8");
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            System.out.println("inside makehttp9");
            is.close();
            json = sb.toString();
            System.out.println("inside makehttp10");
        } catch (Exception e) {
            System.out.println("inside makehttp11");
            Log.e("Buffer Error", "Error converting result " + e.toString());
        }
        */

/*        // try parse the string to a JSON object
        try {
            System.out.println("inside makehttp12");
            jObj = new JSONObject();
            jObj.put("Name","UCSD_CSE218");
        } catch (JSONException e) {
            System.out.println("inside makehttp13");
            Log.e("JSON Parser", "Error parsing data " + e.toString());
        }

        // return JSON String
        return jObj;
*/
        return null;
    }

}
