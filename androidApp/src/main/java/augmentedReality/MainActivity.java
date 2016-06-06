package augmentedReality;

import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AugmentedActivity {
    private static final String TAG = "MainActivity";
    private static final String locale = "en";
    private static final BlockingQueue<Runnable> queue = new ArrayBlockingQueue<Runnable>(1);
    private static final ThreadPoolExecutor exeService = new ThreadPoolExecutor(1, 1, 20, TimeUnit.SECONDS, queue);
	private static final Map<String,NetworkDataSource> sources = new ConcurrentHashMap<String,NetworkDataSource>();
    private Timer timer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LocalDataSource localData = new LocalDataSource(this.getResources(), this);
        ARData.addMarkers(localData.getMarkers());
		Log.e("증강", "MainActivity");
        NetworkDataSource wikipedia = new WikipediaDataSource(this.getResources(), this);
        sources.put("wiki",wikipedia);
        //데이터 2초마다 갱신
        timer = new Timer();
        int period = 2000;
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Location last = ARData.getCurrentLocation();
                updateData(last.getLatitude(),last.getLongitude(),last.getAltitude());
            }
        },0, period);
    }

	@Override
    public void onStart() {
        super.onStart();
        
        Location last = ARData.getCurrentLocation();
        updateData(last.getLatitude(),last.getLongitude(),last.getAltitude());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timer.cancel();
    }

    @Override
    public void onLocationChanged(Location location) {
        super.onLocationChanged(location);
        
        updateData(location.getLatitude(),location.getLongitude(),location.getAltitude());
    }

	@Override
	protected void markerTouched(Marker marker) {
		Log.e("증강", "markerTouchedcultureKey"+marker.getMarkerMap().get("cultureKey"));
		Log.e("증강", "markerTouchedCourseNum"+marker.getMarkerMap().get("courseNum"));
		
		if(marker.getMarkerMap().get("cultureKey") != "" && marker.getMarkerMap().get("cultureKey") != null){
		//	Intent intent = new Intent(MainActivity.this, OlleCultureInfomationActivity.class);
			Log.e("증강", "markerTouchedcultureKey"+marker.getMarkerMap().get("cultureKey"));
			
		//	intent.putExtra("cultureKey", marker.getMarkerMap().get("cultureKey"));
		//	startActivity(intent);
		} else if(marker.getMarkerMap().get("courseNum") != "" && marker.getMarkerMap().get("courseNum") != null){
		//	Intent intent = new Intent(MainActivity.this, OlleCourseActivity.class);
		//	intent.putExtra("courseNum", marker.getMarkerMap().get("courseNum"));
		//	startActivity(intent);
		}
		
//        Toast t = Toast.makeText(getApplicationContext(), marker.getToastString(), Toast.LENGTH_SHORT);
//        t.setGravity(Gravity.CENTER, 0, 0);
//        t.show();


	}

    @Override
	protected void updateDataOnZoom() {
	    super.updateDataOnZoom();
        Location last = ARData.getCurrentLocation();
        updateData(last.getLatitude(),last.getLongitude(),last.getAltitude());
	}
    
    private void updateData(final double lat, final double lon, final double alt) {
        try {
            exeService.execute(
                new Runnable() {
                    
                    public void run() {
                        for (NetworkDataSource source : sources.values())
                            download(source, lat, lon, alt);
                    }
                }
            );
        } catch (RejectedExecutionException rej) {
            Log.w(TAG, "Not running new download Runnable, queue is full.");
        } catch (Exception e) {
            Log.e(TAG, "Exception running download Runnable.",e);
        }
    }
    
    private static boolean download(NetworkDataSource source, double lat, double lon, double alt) {
		if (source==null) return false;
		
		String url = null;
		try {
			url = source.createRequestURL(lat, lon, alt, ARData.getRadius(), locale);    	
		} catch (NullPointerException e) {
			return false;
		}
    	
		List<Marker> markers = null;
		try {
			markers = source.parse(url);
		} catch (NullPointerException e) {
			return false;
		}

        ARData.addMarkers(markers);
    	return true;
    }
}