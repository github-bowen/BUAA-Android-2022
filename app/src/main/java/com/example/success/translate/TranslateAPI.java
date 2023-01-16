package com.example.success.translate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class TranslateAPI {
    public static String getChinese(String word) {
        AtomicReference<String> ret = new AtomicReference<>("");
        Thread a;
        a = new Thread(() -> {
            String url = "http://fanyi.youdao.com/openapi.do?keyfrom=lewe518&key=70654389&type=data&doctype=json&version=1.1&q=";
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(url + word)
                    .build();
            Response response = null;
            try {
                response = client.newCall(request).execute();
                String responseData = response.body().string();
                JSONObject jsonObject = new JSONObject(responseData).getJSONObject("basic");
                JSONArray jsonArray = jsonObject.getJSONArray("explains");
                for (int i = 0; i < jsonArray.length(); i++) {
                    ret.set(ret + jsonArray.getString(i) + "\n");
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });
        a.start();
        try {
            a.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return ret.get();
    }
}
