package com.ramnarayanan.foodfinder.Data.Access;

import android.os.AsyncTask;
import android.util.Log;

import com.ramnarayanan.foodfinder.Data.Models.MapPlace;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Shadow on 3/6/2017.
 */

public class GetPlacesJSONData
        extends AsyncTask<String, Integer, String>
        implements GetRawData.OnDownloadComplete {

    private static final String TAG = "GetPlacesJSONData";
    private String data = null;
    private final IJSONDataAvailable mCallBack;
    private static final String APIKEY = "AIzaSyDSqwO8QnOMqty5laLxP6tEnzZ9P70tBDk";
    private List<MapPlace> mPlaces;

    public GetPlacesJSONData(IJSONDataAvailable callBack) {
        mCallBack = callBack;
    }

    public interface IJSONDataAvailable {
        //void onDataAvailable(List<HashMap<String, String>> data, DownloadStatus status);
        void onDataAvailable(List<MapPlace> data, DownloadStatus status);

    }

    private String createURL(String... params) {
        String searchtype = params[0];
        StringBuilder urlBuild = new StringBuilder();
        switch (searchtype) {
            case "places":
                String latitude = params[1];
                String longitude = params[2];
                urlBuild.append("https://maps.googleapis.com/maps/api/place/nearbysearch/json?")
                        .append("location=" + latitude + "," + longitude)
                        .append("&radius=500")
                        .append("&rankby=prominence")
                        .append("&types=" + "restaurant")
                        .append("&sensor=true")
                        .append("&key=" + APIKEY);
                break;
            case "photos":
                String photoReference = params[1];
                urlBuild.append("https://maps.googleapis.com/maps/api/place/photo?")
                        .append("&maxwidth=400")
                        .append("&photoreference=" + photoReference)
                        .append("&key=" + APIKEY);
                break;
        }
        Log.d(TAG, "createURL: " + urlBuild);
        return urlBuild.toString();

    }

    //Invoke using execute()
    @Override
    protected String doInBackground(String... params) {
        //String latitude = params[0];
        //String longitude = params[1];
        //String destinationURL = createURL(latitude, longitude);

        String destinationURL = createURL(params);
        GetRawData getRawData = new GetRawData(this);
        getRawData.runInSameThread(destinationURL);
        return data;
    }

    @Override
    protected void onPostExecute(String result) {
        //super.onPostExecute(s);
        //PlacesJSONParser parserTask = new PlacesJSONParser();
        //parserTask.execute(result)
        Log.d(TAG, "onPostExecute: starts");
        if (mCallBack != null) {
            mCallBack.onDataAvailable(mPlaces, DownloadStatus.OK);
        }
    }

    @Override
    public void onDownloadComplete(String data, DownloadStatus status) {
        //List<HashMap<String, String>> places = null;
        PlacesJSONParser placeJson = new PlacesJSONParser();
        JSONObject jObject;
        if (status == DownloadStatus.OK) {
            mPlaces = new ArrayList<>();
            try {
                jObject = new JSONObject(data);
                mPlaces = placeJson.parse(jObject);
            } catch (Exception e) {
                Log.d("Exception", e.toString());
            }
            mCallBack.onDataAvailable(mPlaces, DownloadStatus.OK);
        }
    }
}
