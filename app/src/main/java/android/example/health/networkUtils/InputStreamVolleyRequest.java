package android.example.health.networkUtils;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;

import java.util.Map;

public class InputStreamVolleyRequest extends Request<byte[]> {
    private final Response.Listener<byte[]> mListener;
    private Map<String, String> mParams;
    public Map<String, String> responseHeaders;

    /**
     * Creates a new request with the given method.
     *
     * @param method        the request {@link Method} to use
     * @param url           URL to fetch the string at
     * @param listener      Listener to receive the String response
     * @param errorListener Error listener, or null to ignore errors
     */
    public InputStreamVolleyRequest(int method, String url, Response.Listener<byte[]> listener,
                                    Response.ErrorListener errorListener, Map<String, String> mParams) {
        super(method, url, errorListener);
        // this request would never use cache.
        setShouldCache(false);
        mListener = listener;
        this.mParams = mParams;
    }


    @Override
    protected void deliverResponse(byte[] response) {
        mListener.onResponse(response);
    }

    @Override
    protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
        return mParams;
    }

    ;

    @Override
    protected Response<byte[]> parseNetworkResponse(NetworkResponse response) {

        //Initialise local responseHeaders map with response headers received
        responseHeaders = response.headers;

        //Pass the response data here
        return Response.success(response.data, HttpHeaderParser.parseCacheHeaders(response));
    }
}