package android.example.health.networkUtils;

import android.example.health.Constants.GlobalData;
import android.example.health.utils.HttpsTrustManager;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MyVolleyRequestParsing {
    MyVolleyRequestParsing() {

    }

    /**
     * Using for post request method
     *
     * @param url
     * @param params
     * @param requestTAG
     * @param mIVolleyResponse
     */
    public static void post(final String url, final Map<String, String>
            params, final String requestTAG, final IVolleyResponse mIVolleyResponse) {
        response_code = 0;
        Log.e("Url: ", url);
        if (params != null) {
            Log.e("Parems: ", params.toString());
        }
        StringRequest strRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e(" success : ", "" +
                                response);
                        if (mIVolleyResponse != null) {
                            mIVolleyResponse.response(response);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {


                        if (error != null && error.networkResponse != null) {
                            NetworkResponse networkResponse = error.networkResponse;
                            if (networkResponse != null) {
                                response_code = networkResponse.statusCode;

                            }
                            Log.e(" response_code : ", "" +
                                    response_code);
                            Log.e(" error : ", "" +
                                    error);
                        }
                        if (response_code == 400) {
                            if (mIVolleyResponse != null) {
                                mIVolleyResponse.response("The user name or password is incorrect");
                            }
                        } else {
                            if (mIVolleyResponse != null) {
                                mIVolleyResponse.response("");
                            }
                        }

                    }
                }) {
            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                if (response != null) {
                    response_code = response.statusCode;
                    Log.e(" response_code : ", "" +
                            response_code);

                }


                return super.parseNetworkResponse(response);
            }

            @Override
            protected Map<String, String> getParams() {
                return params;
            }
        };
        int socketTimeout = 180000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        strRequest.setRetryPolicy(policy);
        GlobalData.getApp().addToRequestQueue(strRequest, requestTAG);
    }

    public static void postWithHeader(final String url, final String header, final Map<String, String>
            params, final String requestTAG, final IVolleyResponse mIVolleyResponse) {
        response_code = 0;
        Log.e("Url: ", url);
        if (params != null) {
            Log.e("Parems: ", params.toString());
        }
        StringRequest strRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e(" success : ", "" +
                                response);
                        if (mIVolleyResponse != null) {
                            mIVolleyResponse.response(response);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error != null && error.networkResponse != null) {
                            NetworkResponse networkResponse = error.networkResponse;
                            if (networkResponse != null) {
                                response_code = networkResponse.statusCode;

                            }
                            Log.e(" response_code : ", "" +
                                    response_code);
                            Log.e(" error : ", "" +
                                    error);
                        }
                        if (response_code == 401) {
                            if (mIVolleyResponse != null) {
                                mIVolleyResponse.response("UnAuthorize Access");
                            }

                        } else {
                            if (mIVolleyResponse != null) {
                                mIVolleyResponse.response("");
                            }
                        }

                    }
                }) {
            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                response_code = response.statusCode;
                Log.e(" response_code : ", "" +
                        response_code);

                return super.parseNetworkResponse(response);
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                //params.put("Authorization", header);
                params.put("signature",header);
                // params.put("authorization", "bearer " + header);

                return params;
            }

            @Override
            protected Map<String, String> getParams() {
                return params;
            }
        };
        int socketTimeout = 180000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        strRequest.setRetryPolicy(policy);
        GlobalData.getApp().addToRequestQueue(strRequest, requestTAG);
    }

    public static void postWithHeaderWithoutParems(final String url, final String header, final String requestTAG, final IVolleyResponse mIVolleyResponse) {
        response_code = 0;
        Log.e("Url: ", url);

        StringRequest strRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e(" success : ", "" +
                                response);
                        if (mIVolleyResponse != null) {
                            mIVolleyResponse.response(response);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error != null && error.networkResponse != null) {
                            NetworkResponse networkResponse = error.networkResponse;
                            if (networkResponse != null) {
                                response_code = networkResponse.statusCode;

                            }
                            Log.e(" response_code : ", "" +
                                    response_code);
                            Log.e(" error : ", "" +
                                    error);
                        }
                        if (response_code == 401) {
                            if (mIVolleyResponse != null) {
                                mIVolleyResponse.response("UnAuthorize Access");
                            }

                        } else {
                            if (mIVolleyResponse != null) {
                                mIVolleyResponse.response("");
                            }
                        }

                    }
                }) {
            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                response_code = response.statusCode;
                Log.e(" response_code : ", "" +
                        response_code);

                return super.parseNetworkResponse(response);
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "Bearer " + header);
                Log.e("Authorization: ", "Bearer " + header);

                return params;
            }


        };
        int socketTimeout = 180000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        strRequest.setRetryPolicy(policy);
        GlobalData.getApp().addToRequestQueue(strRequest, requestTAG);
    }

    public static int response_code;

    /**
     * Using for get request method
     *
     * @param url
     * @param requestTAG
     * @param mIVolleyResponse
     */
    public static void get(String url, final String requestTAG, final
    IVolleyResponse mIVolleyResponse) {
        response_code = 0;
        Log.e("Url: ", url);
        StringRequest strRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e(" success : ", "" +
                                response);
                        if (mIVolleyResponse != null) {
                            mIVolleyResponse.response(response);
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (mIVolleyResponse != null) {

                            mIVolleyResponse.response("");
                        }
                        if (error != null)
                            Log.e(" error : ", "" +
                                    error);


                    }
                }) {
            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                response_code = response.statusCode;
                if (response.headers.containsKey("data")) {

                }
                Log.e(" response_code : ", "" +
                        response_code);
                return super.parseNetworkResponse(response);
            }
        };
        int socketTimeout = 180000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        strRequest.setRetryPolicy(policy);
        GlobalData.getApp().addToRequestQueue(strRequest, requestTAG);


    }

    public static void getWithHeader(String url, final String header, final String requestTAG, final
    IVolleyResponse mIVolleyResponse) {
        response_code = 0;
        Log.e("Url: ", url);
        StringRequest strRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e(" success : ", "" +
                                response);
                        if (mIVolleyResponse != null) {
                            mIVolleyResponse.response(response);
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error != null) {
                            if (error != null && error.networkResponse != null) {
                                NetworkResponse networkResponse = error.networkResponse;
                                if (networkResponse != null) {
                                    response_code = networkResponse.statusCode;

                                }

                            }
                            Log.e(" response_code : ", "" +
                                    response_code);
                            Log.e(" error : ", "" +
                                    error);
                        }
                        if (response_code == 401) {
                            if (mIVolleyResponse != null) {
                                mIVolleyResponse.response("UnAuthorize Access");
                            }

                        } else {
                            if (mIVolleyResponse != null) {
                                mIVolleyResponse.response("");
                            }
                        }


                    }
                }) {
            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                response_code = response.statusCode;
                Log.e(" response_code : ", "" +
                        response_code);
                return super.parseNetworkResponse(response);
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "Bearer " + header);
                Log.e("Authorization: ", "Bearer " + header);
                return params;
            }

           /* @Override
            protected Map<String, String> getParams() {
                return params;
            }*/
        };
        int socketTimeout = 180000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        strRequest.setRetryPolicy(policy);
        GlobalData.getApp().addToRequestQueue(strRequest, requestTAG);


    }

    /**
     * Using for post json request method
     *
     * @param url
     * @param requestTAG
     * @param mIVolleyResponse
     */
    public static void jsonPostWithHeader(String url, final String header, JSONObject
            params, final String requestTAG, final IVolleyResponse mIVolleyResponse) {
        response_code = 0;
        Log.e("Url: ", url);
        if (params != null) {
            Log.e("Parems: ", params.toString());
        }

        HttpsTrustManager.allowAllSSL();
        JsonObjectRequest strRequest = new JsonObjectRequest(Request.Method.POST, url, params,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e(" success : ", "" +
                                response.toString());
                        if (mIVolleyResponse != null) {
                            mIVolleyResponse.response(response.toString());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error != null && error.networkResponse != null) {
                            NetworkResponse networkResponse = error.networkResponse;
                            if (networkResponse != null) {
                                response_code = networkResponse.statusCode;

                            }

                        }
                        if (response_code == 400) {
                            if (mIVolleyResponse != null) {
                                mIVolleyResponse.response("UnAuthorize Access");
                            }
                        }else if (response_code == 401) {
                            if (mIVolleyResponse != null) {
                                mIVolleyResponse.response("UnAuthorize Access");
                            }
                        }
                        else if (response_code == 201) {
                            if (mIVolleyResponse != null) {
                                mIVolleyResponse.response("This email id already registered with us..");
                            }
                        }
                        else {
                            if (mIVolleyResponse != null) {
                                mIVolleyResponse.response("");
                            }
                        }


                    }
                }) {
            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                response_code = response.statusCode;
                Log.e(" response_code : ", "" +
                        response_code);
                return super.parseNetworkResponse(response);
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("signature", header);
                Log.v("signature", header);

                return params;
            }


        };
        int socketTimeout = 180000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        strRequest.setRetryPolicy(policy);
        GlobalData.getApp().addToRequestQueue(strRequest, requestTAG);
    }


    /**
     * Using for post json request method
     *
     * @param url
     * @param requestTAG
     * @param mIVolleyResponse
     */
    public static void jsonGetWithHeader(String url, final String header, JSONObject
            params, final String requestTAG, final IVolleyResponse mIVolleyResponse) {
        response_code = 0;
        Log.e("Url: ", url);
        if (params != null) {
            Log.e("Parems: ", params.toString());
        }


        JsonObjectRequest strRequest = new JsonObjectRequest(Request.Method.GET, url, params,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e(" success : ", "" +
                                response.toString());
                        if (mIVolleyResponse != null) {
                            mIVolleyResponse.response(response.toString());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error != null && error.networkResponse != null) {
                            NetworkResponse networkResponse = error.networkResponse;
                            if (networkResponse != null) {
                                response_code = networkResponse.statusCode;

                            }

                        }
                        if (response_code == 401) {
                            if (mIVolleyResponse != null) {
                                mIVolleyResponse.response("UnAuthorize Access");
                            }

                        } else {
                            if (mIVolleyResponse != null) {
                                mIVolleyResponse.response("");
                            }
                        }


                    }
                }) {
            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                response_code = response.statusCode;
                Log.e(" response_code : ", "" +
                        response_code);
                return super.parseNetworkResponse(response);
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("signature",header);
                Log.e("signature", header);

                return params;
            }


        };
        int socketTimeout = 180000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        strRequest.setRetryPolicy(policy);
        GlobalData.getApp().addToRequestQueue(strRequest, requestTAG);
    }

    public static void jsonPost(String url, JSONObject
            params, final String requestTAG, final IVolleyResponse mIVolleyResponse) {
        response_code = 0;
        Log.e("Url: ", url);
        if (params != null) {
            Log.e("Parems: ", params.toString());
        }


        JsonObjectRequest strRequest = new JsonObjectRequest(Request.Method.POST, url, params,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e(" success : ", "" +
                                response.toString());
                        if (mIVolleyResponse != null) {
                            mIVolleyResponse.response(response.toString());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error != null && error.networkResponse != null) {
                            NetworkResponse networkResponse = error.networkResponse;
                            if (networkResponse != null) {
                                response_code = networkResponse.statusCode;

                            }

                        }
                        if (response_code == 401) {
                            if (mIVolleyResponse != null) {
                                mIVolleyResponse.response("UnAuthorize Access");
                            }

                        } else if (response_code == 400) {
                            if (mIVolleyResponse != null) {
                                mIVolleyResponse.response("UnAuthorize Access");
                            }

                        } else {
                            if (mIVolleyResponse != null) {
                                mIVolleyResponse.response("");
                            }
                        }


                    }
                }) {
            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                response_code = response.statusCode;
                Log.e(" response_code : ", "" +
                        response_code);
                return super.parseNetworkResponse(response);
            }


        };
        int socketTimeout = 180000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        strRequest.setRetryPolicy(policy);
        GlobalData.getApp().addToRequestQueue(strRequest, requestTAG);
    }

    /**
     * For handling multiple post request that may execute parallely
     *
     * @param url
     * @param requestTAG
     * @param mIVolleyResponse
     */
    public static void getMultipleRequest(String url, final String requestTAG, final
    IVolleyMultipleRequestResponse mIVolleyResponse) {
        response_code = 0;
        Log.e("Url: ", url);

        StringRequest strRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e(" success : ", "" +
                                response);
                        if (mIVolleyResponse != null) {
                            mIVolleyResponse.response(response, requestTAG);
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (mIVolleyResponse != null) {

                            mIVolleyResponse.response("", requestTAG);
                        }
                        if (error != null)
                            Log.e(" error : ", "" +
                                    error);

                    }
                }) {
            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                response_code = response.statusCode;
                Log.e(" response_code : ", "" +
                        response_code);
                return super.parseNetworkResponse(response);
            }
        };
        int socketTimeout = 180000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        strRequest.setRetryPolicy(policy);
        GlobalData.getApp().addToRequestQueue(strRequest, requestTAG);


    }

    /**
     * @param url
     * @param requestTAG
     * @param mIVolleyResponse
     */
    public static void InputStreamGet(String url, final String requestTAG, final IVolleyInputStreamResponse mIVolleyResponse) {
        response_code = 0;
        InputStreamVolleyRequest inputStreamRequest = new InputStreamVolleyRequest(Request.Method.GET, url, new Response.Listener<byte[]>() {
            @Override
            public void onResponse(byte[] response) {
                Log.e("onResponse", "onResponse");
                if (mIVolleyResponse != null) {
                    mIVolleyResponse.response(response);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (mIVolleyResponse != null) {

                    mIVolleyResponse.response(null);
                }
                if (error != null)
                    Log.e(" error : ", "" +
                            error);


            }
        }, null);
        int socketTimeout = 180000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        inputStreamRequest.setRetryPolicy(policy);
        GlobalData.getApp().addToRequestQueue(inputStreamRequest, requestTAG);
    }


}