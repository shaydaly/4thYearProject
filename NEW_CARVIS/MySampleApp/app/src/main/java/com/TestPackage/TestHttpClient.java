//package com.TestPackage;
//
//import android.preference.PreferenceActivity;
//
///**
// * Created by Seamus on 16/04/2017.
// */
//
//public class TestHttpClient {
//    // package-local client that can be set in tests
//    static AsyncHttpClient client = new AsyncHttpClient();
//    // package-local baseUrl that can be set in tests
//    static String baseUrl = "http://pastbin.org/";
//
//    public static void getData(final ServerCallback callback) {
//        String url = baseUrl + "get";
//        client.get(url, new AsyncHttpResponseHandler() {
//            @Override
//            public void onSuccess(int statusCode, PreferenceActivity.Header[] headers, byte[] responseBody) {
//                callback.onSuccess(statusCode, new String(responseBody));
//            }
//
//            @Override
//            public void onFailure(int statusCode, PreferenceActivity.Header[] headers, byte[] responseBody, Throwable error) {
//                callback.onFailure(statusCode, new String(responseBody));
//            }
//        });
//    }
//}