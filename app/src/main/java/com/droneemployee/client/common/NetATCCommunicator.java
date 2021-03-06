package com.droneemployee.client.common;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

/**
 * Created by simon on 19.06.16.
 */
public class NetATCCommunicator extends ATCCommunicator {
    private static final String TAG = "NetATCCommunicator";
    private static final int BUFFER_LEN = 1024;
    private static final String defaultPort = "7453";

    private String urlAddress = "";
    private String port = defaultPort;

    public NetATCCommunicator(String address){
        this.urlAddress = address;
    }

    public NetATCCommunicator(String address, String port){
        this.urlAddress = address;
        this.port = port;
    }

    @Override
    public DroneATC fetchDroneAtc(Coordinate currentLocation) {
        try {
            String url = Uri.parse(urlAddress + ":" + port)
                    .buildUpon()
                    .appendQueryParameter("lat", String.valueOf(currentLocation.lat))
                    .appendQueryParameter("lng", String.valueOf(currentLocation.lng))
                    .build()
                    .toString();
            Log.i(TAG, "url: " + url);
            String jsonString = getUrlString(url);
            Log.i(TAG, "JSON Response: " + jsonString);
            JSONObject jsonBody = new JSONObject(jsonString);
            return parseDroneAtc(jsonBody);
        } catch (IOException ioe) {
            Log.e(TAG, "Failed to fetch DroneATC", ioe);
        } catch (JSONException je) {
            Log.e(TAG, "Failed to parse JSON", je);
        }
        return null;
    }

    //TODO: Real bye
    @Override
    public Ticket buyTicket(Drone drone) {
        return new Ticket(drone, genirateHexRandomString());
    }

    //TODO: Real send
    @Override
    public boolean sendTasks(List<Task> tasks) {
        Log.i(TAG, "SEND: " + tasks);
        boolean result = false;

        try {
            URL url = new URL(urlAddress + ":" + port);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            try {
                connection.setDoOutput(true);

                OutputStream stream = connection.getOutputStream();
                stream.write(generateJSONTasks(tasks).toString().getBytes());
                stream.close();

                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    Log.i(TAG, "SEND: OK");
                    result = true;
                }
            } catch (IOException ioe) {
                Log.e(TAG, "SEND: FAILED", ioe);
            } finally {
                connection.disconnect();
            }
        } catch (MalformedURLException mue) {
            Log.e(TAG, "Failed to parse url", mue);
        } catch (IOException ioe) {
            Log.e(TAG, "Filed to open connection", ioe);
        } finally {
            return result;
        }
    }

    private byte[] getUrlBytes(String urlSpec) throws IOException {
        URL url = new URL(urlSpec);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        try{
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream in = connection.getInputStream();

            if(connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new IOException(connection.getResponseMessage() +
                        ": with " +
                        urlSpec);
            }

            int bytesRead;
            byte[] buffer = new byte[BUFFER_LEN];
            while ((bytesRead = in.read(buffer)) > 0){
                out.write(buffer, 0, bytesRead);
            }
            out.close();
            return out.toByteArray();
        } finally {
            connection.disconnect();
        }
    }

    private String getUrlString(String urlSpec) throws IOException {
        return new String(getUrlBytes(urlSpec));
    }

    private DroneATC parseDroneAtc(JSONObject jsonBody) throws JSONException, IOException {
        DroneATC atc = new DroneATC(jsonBody.getString("id"), jsonBody.getString("name"));
        JSONArray perimeter = jsonBody.getJSONArray("perimeter");
        for (int i = 0; i < perimeter.length(); i++) {
            JSONObject point = perimeter.getJSONObject(i);
            atc.getPerimeter().add(new Coordinate(point.getDouble("lat"), point.getDouble("lng")));
        }
        JSONArray jsonDrones = jsonBody.getJSONArray("drones");
        for (int i = 0; i < jsonDrones.length(); i++) {
            JSONObject jsonDrone = jsonDrones.getJSONObject(i);
            JSONObject jsonHomebase = jsonDrone.getJSONObject("homebase");
            Coordinate homebase = new Coordinate(
                    jsonHomebase.getDouble("lat"), jsonHomebase.getDouble("lng"));
            Drone drone = new Drone(jsonDrone.getString("id"),
                    homebase,
                    Drone.State.toState(jsonDrone.getString("status")));
            atc.getDrones().add(drone);
        }
        Log.i(TAG, atc.toString());
        return atc;
    }

    private JSONObject generateJSONOneTask(Task task) {
        try {
            JSONArray jsonRoute = new JSONArray();
            for (int i = 0; i < task.size(); i++) {
                Coordinate coordinate = task.getWaypoint(i);
                JSONObject jsonCoordinate = new JSONObject();
                jsonCoordinate
                        .put("lat", coordinate.lat)
                        .put("lng", coordinate.lng)
                        .put("alt", coordinate.alt);
                jsonRoute.put(jsonCoordinate);
            }

            JSONObject jsonTask = new JSONObject();
            jsonTask
                    .put("id", task.getDroneAdress())
                    .put("route", jsonRoute);

            return jsonTask;
        } catch (JSONException je) {
            Log.e(TAG, "Failed generate JSON", je);
        }
        return null;
    }

    private JSONArray generateJSONTasks(List<Task> tasks) {
        JSONArray jsonTasks = new JSONArray();
        for (Task task : tasks) {
            jsonTasks.put(generateJSONOneTask(task));
        }
        return jsonTasks;
    }

    //TODO: Remove this method
    @NonNull
    private String genirateHexRandomString(){
        return Integer.toHexString((int) (Math.random() * 15728639 + 1048576)).toUpperCase();
    }
}
