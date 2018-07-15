package com.pruebatecnica.pablocastrillon.pruebatecnica_pablocastrillon.core.network;

import android.content.Context;
import android.net.Uri;
import android.support.v4.util.ArrayMap;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.pruebatecnica.pablocastrillon.pruebatecnica_pablocastrillon.MotionActivity;
import com.pruebatecnica.pablocastrillon.pruebatecnica_pablocastrillon.model.NotificationBody;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Map;

public class WebService {

    private static final int VOLLEY_TIME_OUT = 10000;
    private RequestQueue mainQueue;
    private WebServiceListener webServiceListener;
    private UpdateNotificationListener updateNotificationListener;


    public WebService(Context context, WebServiceListener webServiceListener) {
        mainQueue = Volley.newRequestQueue(context);
        this.webServiceListener = webServiceListener;
    }

    public WebService(Context context, UpdateNotificationListener updateNotificationListener) {
        mainQueue = Volley.newRequestQueue(context);
        this.updateNotificationListener = updateNotificationListener;
    }


    private String getApiRestUrl() {
        return "http://proyectos.tekus.co/Test/api/notifications";
    }

    public void getNotifications() {


        try {
            Uri.Builder uriBuilder = Uri.parse(getApiRestUrl())
                    .buildUpon();
            String uri = uriBuilder.toString();

            Request request = new JsonArrayRequest(Request.Method.GET, uri, null,
                    new Response.Listener<JSONArray>() {

                        @Override
                        public void onResponse(JSONArray response) {
                            Gson gson = new Gson();
                            try {
                                NotificationBody[] resp = gson.fromJson(response.toString(), NotificationBody[].class);
                                webServiceListener.onGetNotifications(resp);
                            } catch (Exception ex) {
                                Log.e(this.toString(), ex.getMessage());
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                            Log.e(this.toString(), error.getMessage());
                        }
                    }) {
                @Override
                protected Response<JSONArray> parseNetworkResponse(NetworkResponse response) {

                    return handleEmptyArrayResponse(response);
                }

                public Map<String, String> getHeaders() {
                    Map<String, String> headers = new ArrayMap<>();
                    headers.put("Authorization", "Basic 1036612823");
                    return headers;
                }
            };
            request.setRetryPolicy(new DefaultRetryPolicy(
                    VOLLEY_TIME_OUT,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            request.setShouldCache(false);
            mainQueue.add(request);

        } catch (Exception ex) {
            Log.e(this.toString(), ex.getMessage());
        }


    }


    public void getNotification(String NotificationId) {

        try {
            Uri.Builder uriBuilder = Uri.parse(getApiRestUrl())
                    .buildUpon()
                    .appendPath(NotificationId);

            String uri = uriBuilder.toString();

            Request request = new JsonObjectRequest(Request.Method.GET, uri, null,
                    new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            Gson gson = new Gson();
                            try {
                                NotificationBody resp = gson.fromJson(response.toString(), NotificationBody.class);
                            } catch (Exception ex) {
                                Log.e(this.toString(), ex.getMessage());
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                            Log.e(this.toString(), error.getMessage());
                        }
                    }) {
                @Override
                protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                    return handleEmptyObjectResponse(response);
                }

                public Map<String, String> getHeaders() {
                    Map<String, String> headers = new ArrayMap<>();
                    headers.put("Authorization", "Basic 1036612823");
                    return headers;
                }
            };
            request.setRetryPolicy(new DefaultRetryPolicy(
                    VOLLEY_TIME_OUT,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            request.setShouldCache(false);
            mainQueue.add(request);

        } catch (Exception ex) {
            Log.e(this.toString(), ex.getMessage());
        }


    }


    public void postNotification(NotificationBody notificationBody) {
        try {

            Gson gson = new GsonBuilder().create();

            JSONObject data = new JSONObject(gson.toJson(notificationBody));
            Uri.Builder uriBuilder = Uri.parse(getApiRestUrl())
                    .buildUpon();

            Request jsObjRequest = new JsonObjectRequest
                    (Request.Method.POST, uriBuilder.toString(), data, new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            try {

                                Gson gson = new GsonBuilder().create();
                                NotificationBody resp = gson.fromJson(response.toString(), NotificationBody.class);
                                webServiceListener.onGetNotification(resp);

                            } catch (Exception ex) {
                                Log.e("onResponse", ex.getMessage());
                            }
                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {

                        }
                    }) {
                @Override
                protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                    return handleEmptyObjectResponse(response);
                }

                public Map<String, String> getHeaders() {
                    Map<String, String> headers = new ArrayMap<>();
                    headers.put("Authorization", "Basic 1036612823");
                    return headers;
                }
            };

            jsObjRequest.setRetryPolicy(new DefaultRetryPolicy(
                    VOLLEY_TIME_OUT,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            jsObjRequest.setShouldCache(false);

            mainQueue.add(jsObjRequest);

        } catch (Exception ex) {
            Log.e(this.toString(), ex.getMessage());
        }
    }


    public void putNotification(NotificationBody notificationBody, String NotificationId) {
        try {

            Gson gson = new GsonBuilder().create();

            JSONObject data = new JSONObject(gson.toJson(notificationBody));
            Uri.Builder uriBuilder = Uri.parse(getApiRestUrl())
                    .buildUpon()
                    .appendPath(NotificationId);

            Request jsObjRequest = new JsonObjectRequest
                    (Request.Method.PUT, uriBuilder.toString(), data, new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            try {

                                Gson gson = new GsonBuilder().create();
                                NotificationBody resp = gson.fromJson(response.toString(), NotificationBody.class);
                                webServiceListener.onPutNotification(resp);
                            } catch (Exception ex) {
                                Log.e("onResponse", ex.getMessage());
                            }
                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                        }
                    }) {
                @Override
                protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                    return handleEmptyObjectResponse(response);
                }

                public Map<String, String> getHeaders() {
                    Map<String, String> headers = new ArrayMap<>();
                    headers.put("Authorization", "Basic 1036612823");
                    return headers;
                }
            };

            jsObjRequest.setRetryPolicy(new DefaultRetryPolicy(
                    VOLLEY_TIME_OUT,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            jsObjRequest.setShouldCache(false);

            mainQueue.add(jsObjRequest);

        } catch (Exception ex) {
            Log.e(this.toString(), ex.getMessage());
        }
    }


    public void postNotificationService(NotificationBody notificationBody) {
        try {

            Gson gson = new GsonBuilder().create();

            JSONObject data = new JSONObject(gson.toJson(notificationBody));
            Uri.Builder uriBuilder = Uri.parse(getApiRestUrl())
                    .buildUpon();

            Request jsObjRequest = new JsonObjectRequest
                    (Request.Method.POST, uriBuilder.toString(), data, new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            try {

                                Gson gson = new GsonBuilder().create();
                                NotificationBody resp = gson.fromJson(response.toString(), NotificationBody.class);
//                                webServiceListener.onGetNotificationService(resp);
//                                webServiceListener.onGetNotificationService(resp);
                                updateNotificationListener.onGetNotificationService(resp);

                            } catch (Exception ex) {
                                Log.e("onResponse", ex.getMessage());
                            }
                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {

                        }
                    }) {
                @Override
                protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                    return handleEmptyObjectResponse(response);
                }

                public Map<String, String> getHeaders() {
                    Map<String, String> headers = new ArrayMap<>();
                    headers.put("Authorization", "Basic 1036612823");
                    return headers;
                }
            };

            jsObjRequest.setRetryPolicy(new DefaultRetryPolicy(
                    VOLLEY_TIME_OUT,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            jsObjRequest.setShouldCache(false);

            mainQueue.add(jsObjRequest);

        } catch (Exception ex) {
            Log.e(this.toString(), ex.getMessage());
        }
    }


    public void putNotificationService(NotificationBody notificationBody, String NotificationId) {
        try {

            Gson gson = new GsonBuilder().create();

            JSONObject data = new JSONObject(gson.toJson(notificationBody));
            Uri.Builder uriBuilder = Uri.parse(getApiRestUrl())
                    .buildUpon()
                    .appendPath(NotificationId);

            Request jsObjRequest = new JsonObjectRequest
                    (Request.Method.PUT, uriBuilder.toString(), data, new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            try {

                                Gson gson = new GsonBuilder().create();
                                NotificationBody resp = gson.fromJson(response.toString(), NotificationBody.class);
//                                webServiceListener.onPutNotificationService();
                                updateNotificationListener.onPutNotificationService();
                            } catch (Exception ex) {
                                Log.e("onResponse", ex.getMessage());
                            }
                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                        }
                    }) {
                @Override
                protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                    return handleEmptyObjectResponse(response);
                }

                public Map<String, String> getHeaders() {
                    Map<String, String> headers = new ArrayMap<>();
                    headers.put("Authorization", "Basic 1036612823");
                    return headers;
                }
            };

            jsObjRequest.setRetryPolicy(new DefaultRetryPolicy(
                    VOLLEY_TIME_OUT,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            jsObjRequest.setShouldCache(false);

            mainQueue.add(jsObjRequest);

        } catch (Exception ex) {
            Log.e(this.toString(), ex.getMessage());
        }
    }

    public void delNotification(String NotificationId) {

        try {
            Uri.Builder uriBuilder = Uri.parse(getApiRestUrl())
                    .buildUpon()
                    .appendPath(NotificationId);

            String uri = uriBuilder.toString();

            Request request = new JsonObjectRequest(Request.Method.DELETE, uri, null,
                    new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {

                            try {

                            } catch (Exception ex) {
                                Log.e(this.toString(), ex.getMessage());
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                            Log.e(this.toString(), error.getMessage());
                        }
                    }) {
                @Override
                protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                    return handleEmptyObjectResponse(response);
                }

                public Map<String, String> getHeaders() {
                    Map<String, String> headers = new ArrayMap<>();
                    headers.put("Authorization", "Basic 1036612823");
                    return headers;
                }
            };
            request.setRetryPolicy(new DefaultRetryPolicy(
                    VOLLEY_TIME_OUT,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            request.setShouldCache(false);
            mainQueue.add(request);

        } catch (Exception ex) {
            Log.e(this.toString(), ex.getMessage());
        }


    }

    private Response<JSONObject> handleEmptyObjectResponse(NetworkResponse response) {
        try {
            String jsonString = new String(response.data,
                    HttpHeaderParser.parseCharset(response.headers, "utf-8"));
            JSONObject result = null;
            if (jsonString.length() > 0)
                result = new JSONObject(jsonString);
            return Response.success(result,
                    HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JsonSyntaxException e) {
            return Response.error(new ParseError(e));
        } catch (JSONException e) {
            return Response.error(new ParseError(e));
        }
    }


    private Response<JSONArray> handleEmptyArrayResponse(NetworkResponse response) {
        try {
            String jsonString = new String(response.data,
                    HttpHeaderParser.parseCharset(response.headers, "utf-8"));
            JSONArray result = null;
            if (jsonString.length() > 0)
                result = new JSONArray(jsonString);

            return Response.success(result,
                    HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JsonSyntaxException e) {
            return Response.error(new ParseError(e));
        } catch (JSONException e) {
            return Response.error(new ParseError(e));
        }
    }

}
