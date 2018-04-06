package cwo.pakidermo.dgom.com.mx.cwo.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;

import cwo.pakidermo.dgom.com.mx.cwo.app.AppController;

import static cwo.pakidermo.dgom.com.mx.cwo.app.AppController.TAG;

/**
 * Created by beto on 07/01/18.
 */

public class Networking {


    public static void getJsonArray(String url, Context context){
        // Tag used to cancel the request
        String tag_json_arry = "json_array_req";



        final ProgressDialog pDialog = new ProgressDialog(context);
        pDialog.setMessage("Loading...");
        pDialog.show();

        JsonArrayRequest req = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, response.toString());
                        pDialog.hide();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                pDialog.hide();
            }
        });

// Adding request to request queue
        AppController.getInstance().addToRequestQueue(req, tag_json_arry);
    }
}
