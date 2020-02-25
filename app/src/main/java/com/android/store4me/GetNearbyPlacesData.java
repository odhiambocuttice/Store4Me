package com.android.store4me;


import android.os.AsyncTask;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class GetNearbyPlacesData extends AsyncTask<Object, String, String> {

    String GooglePlacesData;
    GoogleMap googleMap;
    String Url;


    @Override
    protected String doInBackground(Object... objects) {
        googleMap = (GoogleMap) objects[0];
        Url = (String) objects[1];

        DownloadUrl downloadUrl = new DownloadUrl();
        try{
            GooglePlacesData = downloadUrl.readUrl(Url);

        }
        catch (IOException e){
            e.printStackTrace();
        }
        return  GooglePlacesData;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        try {
            JSONObject parentObject = new JSONObject(s);
            JSONArray resultArray = parentObject.getJSONArray("Results");
            for (int i = 0; i < resultArray.length(); i++){
                JSONObject jsonObject = resultArray.getJSONObject(i);
                JSONObject locationObject = jsonObject.getJSONObject("Geommetry").getJSONObject("location");

                String latitude = locationObject.getString("lat");
                String Longititude = locationObject.getString("lng");

                JSONObject nameObject = resultArray.getJSONObject(i);
                String name = nameObject.getString("name");

                LatLng latLng = new LatLng(Double.parseDouble(latitude), Double.parseDouble(Longititude));

                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.title(name);
                markerOptions.position(latLng);

                googleMap.addMarker(markerOptions);

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
