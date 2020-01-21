package com.example.distanzclient.Rest;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.distanzclient.BerichtigungsManager;
import com.example.distanzclient.Gps.CostumLocation;
import com.example.distanzclient.Rest.ReqRes.TrackidReq;
import com.example.distanzclient.Rest.ReqRes.TrackidRes;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class RestManager {
    public static TextView ausgabe;
    public static int trackid = 0;
    static  TrackidRes resp;
    public static RequestQueue  queue;
    public static int counter = 0;
    public static int trackidInit(AppCompatActivity activity, String beschreibung, String name, int teamid) {
        TrackidReq trackidReq = new TrackidReq();
        trackidReq.teamid=teamid;trackidReq.name=name;trackidReq.beschreibung=beschreibung;
        BerichtigungsManager.isInternetGranted(activity);
        queue = Volley.newRequestQueue(activity.getApplicationContext());
        String url ="http://pi-bo.dd-dns.de:8080/LM-Server/api/v2/track";
        Gson gson = new Gson();
        String json = gson.toJson(trackidReq);
        JSONObject obj = null;
        try {
            obj = new JSONObject(json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // Request a string response from the provided URL.
        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.POST, url,obj,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        TrackidRes res = gson.fromJson(response.toString(),TrackidRes.class);
                        trackid = res.trackid;
                        resp = res;
                        Log.e("trackid","\t"+trackid);
                        ausgabe.append("the trackid is : "+trackid+"\n");
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("trackid Fehler", "\t" + trackid);
                ausgabe.append("Trackid fehlgeschlagen\n");

            }

        });
        queue.add(stringRequest);
        return trackid;
    }


    public static void postLocation(CostumLocation location) {
        counter ++;
        String url ="http://pi-bo.dd-dns.de:8080/LM-Server/api/v2/data";
        ArrayList<CostumLocation> temp = new ArrayList<>();temp.add(location);
        Gson gson = new Gson();
        String json = gson.toJson(temp);
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //ausgabe.append("Location gesandtmit Nummer: "+counter+", longitude: "+location.longitude+" lati:"+location.latitude+ "\n");
                        Log.e("Location","\t Location gesandt");
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Location Fehler", "\t Location könnte nicht gesandt");
            }}){
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                try {
                    return json == null ? null : json.getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {
                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", json, "utf-8");
                    return null;
                }
            }

        };

        queue.add(stringRequest);
    }
}
