package com.example.mythirdapp;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity {
	
	String logglyAPIKey = "a7cfbe8f-471d-4c79-a5af-c72cf7134a0a";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
        
        //starting location manager to get the GPS coordinates
        LocationManager locManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        LocationListener locListener = new MyLocationListener();
        
        int minTime = 0;
        int minDist = 0;
        locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime, minDist, locListener);
    }
    
    public static InputStream sendGetRequest(String url) {
    	
    	InputStream content = null;
    	try {
    		HttpClient httpclient = new DefaultHttpClient();
    	    HttpResponse response = httpclient.execute(new HttpGet(url));
    	    content = response.getEntity().getContent();
    	} catch (Exception e) {
    		//unable to make the below line work; hence commented out
    	    //Log.("[GET REQUEST]", "Network exception", e);
    	}
    	return content;
    }
    

    public class MyLocationListener implements LocationListener {
    
    	public String getDeviceId() {
    		
    		return Secure.getString(getBaseContext().getContentResolver(), Secure.ANDROID_ID);
    	}
    	
    	public String getTimestamp() {
    		
    		SimpleDateFormat dateFormat = new SimpleDateFormat("ddMMyyyyhhmmss");
    		return dateFormat.format(new Date());
    	}
    	
    	
    	@Override
    	public void onLocationChanged(Location loc) {
    		
    		loc.getLatitude();
    		loc.getLongitude();
    		String Text = "My current location is: " + "latitude = " + loc.getLatitude() + " longitude = " + loc.getLongitude();
    		
    		String timestamp = getTimestamp();
    		
    		String androidId = getDeviceId(); 
    		
    		String url = "http://logs-01.loggly.com/inputs/" + logglyAPIKey + ".gif?" 
    						+ "lat=" + loc.getLatitude() 
    						+ "&lng=" + loc.getLongitude() 
    						+ "&date=" + timestamp 
    						+ "&deviceId=" + androidId;
    		
            InputStream content = sendGetRequest(url); //sending the log
    		
    		Toast.makeText(getApplicationContext(), Text, Toast.LENGTH_SHORT).show();    	
    	}
    	
    	@Override
    	public void onProviderDisabled(String provider) {
    		
    		Toast.makeText(getApplicationContext(), "GPS disabled", Toast.LENGTH_SHORT).show();
    	}
    	
    	@Override
    	public void onProviderEnabled(String provider) {
    		
    		Toast.makeText(getApplicationContext(), "GPS Enabled", Toast.LENGTH_SHORT).show();
    	}
    	
    	@Override
    	public void onStatusChanged(String provider, int status, Bundle extras) {
    	}
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }
    }
}