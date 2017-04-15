//package com.TestPackage;
//
//import com.amazonaws.http.HttpResponse;
//import com.android.volley.AuthFailureError;
//import com.android.volley.Request;
//import com.android.volley.toolbox.HttpStack;
//
//import java.util.HashMap;
//import java.util.Map;
//
///**
// * Created by Seamus on 15/04/2017.
// */
//
//public class MockHttpStack implements HttpStack {
//
//    private HttpResponse mResponseToReturn;
//
//    private String mLastUrl;
//
//    private Map<String, String> mLastHeaders;
//
//    private byte[] mLastPostBody;
//
//    public String getLastUrl() {
//        return mLastUrl;
//    }
//
//    public Map<String, String> getLastHeaders() {
//        return mLastHeaders;
//    }
//
//    public byte[] getLastPostBody() {
//        return mLastPostBody;
//    }
//
//    public void setResponseToReturn(HttpResponse response) {
//        mResponseToReturn = response;
//    }
//
//    @Override
//    public HttpResponse performRequest(Request<?> request, Map<String, String> additionalHeaders)
//            throws AuthFailureError {
//        mLastUrl = request.getUrl();
//        mLastHeaders = new HashMap<String, String>();
//        if (request.getHeaders() != null) {
//            mLastHeaders.putAll(request.getHeaders());
//        }
//        if (additionalHeaders != null) {
//            mLastHeaders.putAll(additionalHeaders);
//        }
//        try {
//            mLastPostBody = request.getBody();
//        } catch (AuthFailureError e) {
//            mLastPostBody = null;
//        }
//        return mResponseToReturn;
//    }
//}
