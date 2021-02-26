package API;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.preference.PreferenceManager;
import android.widget.ImageView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.riseapp.LoginActivity;
import com.example.riseapp.MainActivity;
import com.example.riseapp.RootActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import utilities.StorageLocal;

public class ServerHelper extends StorageLocal{

    private static String url_base = "https://url_base";
    private StorageLocal storageLocal;
    private Context context;
    private RequestQueue requestQueue;

    public static int POST =  Request.Method.POST;
    public static int GET =  Request.Method.GET;
    public static int PUT =  Request.Method.PUT;
    public static int DELETE =  Request.Method.DELETE;

    public ServerHelper(Context context) {
        super(context);
        this.context = context;
        storageLocal = new StorageLocal(context);
        requestQueue = Volley.newRequestQueue(context);;
    }

    /*Task-------------------------------------------------*/
    // - Saved in Cache
    /*------------------------------------------------------*
     *             Server request Function                  *
     *-------------------------------------------------------*/
    public void Request(int method, String url, JSONObject postData, final ServerCallback callback) {

        System.out.println("Sending: "+postData);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(method, url_base + url, postData, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    callback.onSuccess(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("VolleyError: "+error);
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    try {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("msg", "Por favor revise su conexion a internet 🤦‍♂️");
                        callback.onError(jsonObject);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        if(error.networkResponse.data != null){
                            JSONObject jsonObject = new JSONObject();

                            if(error.networkResponse.statusCode == 503){
                                jsonObject.put("msg", "Servidor no disponible 😨");
                            }else {
                                if(error.networkResponse.data != null){
                                    String stringRes = new String(error.networkResponse.data);
                                    jsonObject = new JSONObject(stringRes);
                                }
                            }
                            callback.onError(jsonObject);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json;charset=UTF-8");
                String token = readLocalData("app_token");
                if (!token.equals("")) {
                    headers.put("authorization", "Bearer " + token);
                }

                return headers;
            }
        };
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(50000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(jsonObjectRequest);
    }

    //--- Server Callback interface for responses of Volley
    public interface ServerCallback {
        void onSuccess(JSONObject response) throws JSONException;
        void onError(JSONObject error) throws JSONException;
    }
}
