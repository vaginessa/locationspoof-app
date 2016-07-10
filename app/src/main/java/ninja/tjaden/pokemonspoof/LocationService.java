package ninja.tjaden.pokemonspoof;

import android.annotation.TargetApi;
import android.app.IntentService;
import android.app.admin.SystemUpdatePolicy;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Build;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by tjaden on 7/9/16.
 */
public class LocationService extends IntentService {
    private static final String TAG = "LocationService";
    public static volatile boolean shouldContinue = true;
    private RequestQueue queue;
    private Context context;
    private static final int duration = Toast.LENGTH_SHORT;
    @Override
    protected void onHandleIntent(Intent workIntent) {
        context = getApplicationContext();
        queue = Volley.newRequestQueue(context);


        Log.d(TAG, "Handle intent");
        String server = workIntent.getDataString();
        String username = workIntent.getStringExtra("username");
        String url = server+"?user="+username;
        Log.i(TAG,url);
        Boolean check = workIntent.getBooleanExtra("checked",false);

        if(check) {
            while(shouldContinue) {

                fetchMockLocation(url);
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {

                }
            }
            resetLocation();
        }
        else
            resetLocation();
    }

    private void setMockLocation(double lat, double lon){
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationManager.addTestProvider(LocationManager.GPS_PROVIDER, false, false, false, false, true, true, true, Criteria.POWER_LOW, Criteria.ACCURACY_FINE);
        Location newLocation = new Location(LocationManager.GPS_PROVIDER);

        newLocation.setLatitude(lat);
        newLocation.setLongitude(lon);
        newLocation.setAccuracy(Criteria.ACCURACY_FINE);
        newLocation.setTime(System.currentTimeMillis());
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
            newLocation.setElapsedRealtimeNanos(SystemClock.elapsedRealtimeNanos());
        }

        locationManager.setTestProviderEnabled(LocationManager.GPS_PROVIDER, true);
        locationManager.setTestProviderStatus(LocationManager.GPS_PROVIDER, LocationProvider.AVAILABLE, null, System.currentTimeMillis());

        locationManager.setTestProviderLocation(LocationManager.GPS_PROVIDER, newLocation);

    }

    private void fetchMockLocation(String url){

        Log.i(TAG,"fetchingLocation");
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>(){
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG,"Got response");
                        try {
                            JSONObject json = new JSONObject(response);

                            double latitude = json.getDouble("latitude");
                            double longitude = json.getDouble("longitude");
                            setMockLocation(latitude, longitude);


                        } catch (JSONException e){
                            Log.e(TAG, "onResponse: ", e);

                            CharSequence text = "Error: User not found";
                            shouldContinue = false;
                            Toast toast = Toast.makeText(context, text, duration);
                            toast.show();

                            setMockLocation(0, 0);

                        }
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        CharSequence text = "Error: Couldn't contact server";
                        shouldContinue = false;
                        Toast toast = Toast.makeText(context, text, duration);
                        toast.show();
                    }
                });


        queue.add(stringRequest);


    }

    private void resetLocation (){
        Log.d(TAG,"Location reset");
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Log.d(TAG, "Reseting...");
            //locationManager.clearTestProviderEnabled(LocationManager.GPS_PROVIDER);
            //locationManager.clearTestProviderLocation(LocationManager.GPS_PROVIDER);
            //locationManager.clearTestProviderStatus(LocationManager.GPS_PROVIDER);
            //locationManager.removeTestProvider(LocationManager.GPS_PROVIDER);
        }
    }

    public LocationService() {
        super("LocationService");
        Log.d(TAG, "Service init");
    }
}
