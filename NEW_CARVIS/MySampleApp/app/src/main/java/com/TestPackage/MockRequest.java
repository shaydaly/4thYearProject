package com.TestPackage;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.VolleyError;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Seamus on 15/04/2017.
 */

public class MockRequest extends Request<byte[]> {
    public MockRequest() {
        super(Request.Method.GET, "https://8ssr60mlih.execute-api.us-east-1.amazonaws.com/Test/callqueryspeed?latitude=53.3539999&longitude=-6.3771", null);
    }
    public MockRequest(String url, Response.ErrorListener listener) {
        super(Request.Method.GET, url, listener);
    }
    private Map<String, String> mPostParams = new HashMap<String, String>();
    public void setPostParams(Map<String, String> postParams) {
        mPostParams = postParams;
    }
    @Override
    public Map<String, String> getPostParams() {
        return mPostParams;
    }
    private String mCacheKey = super.getCacheKey();
    public void setCacheKey(String cacheKey) {
        mCacheKey = cacheKey;
    }
    @Override
    public String getCacheKey() {
        return mCacheKey;
    }
    public boolean deliverResponse_called = false;
    public boolean parseResponse_called = false;
    @Override
    protected void deliverResponse(byte[] response) {
        deliverResponse_called = true;
    }
    public boolean deliverError_called = false;
    @Override
    public void deliverError(VolleyError error) {
        super.deliverError(error);
        deliverError_called = true;
    }
    public boolean cancel_called = false;
    @Override
    public void cancel() {
        cancel_called = true;
        super.cancel();
    }
    private Priority mPriority = super.getPriority();
    public void setPriority(Priority priority) {
        mPriority = priority;
    }
    @Override
    public Priority getPriority() {
        return mPriority;
    }
    @Override
    protected Response<byte[]> parseNetworkResponse(NetworkResponse response) {
        parseResponse_called = true;
        return Response.success(response.data, CacheTestUtil.makeRandomCacheEntry(response.data));
    }
}
