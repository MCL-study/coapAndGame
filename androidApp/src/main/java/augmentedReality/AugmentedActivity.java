package augmentedReality;

import java.text.DecimalFormat;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.location.Location;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.*;
import android.widget.SeekBar.OnSeekBarChangeListener;
import com.sylphe.app.android.R;

public class AugmentedActivity extends SensorsActivity implements OnTouchListener {
    private static final String TAG = "AugmentedActivity";
    private static final DecimalFormat FORMAT = new DecimalFormat("#.##");
    private static final int ZOOMBAR_BACKGROUND_COLOR = Color.argb(125,55,55,55);
    private static final String END_TEXT = "80 km";
    private static final int END_TEXT_COLOR = Color.WHITE;

    protected static WakeLock wakeLock = null;
    protected static CameraSurface camScreen = null;    
    protected static VerticalSeekBar myZoomBar = null;
    protected static TextView endLabel = null;
    protected static LinearLayout zoomLayout = null;
    protected static AugmentedView augmentedView = null;

    public static final float MAX_ZOOM = 100; //in KM
    public static final float ONE_PERCENT = MAX_ZOOM/100f;
    public static final float TEN_PERCENT = 10f*ONE_PERCENT;
    public static final float TWENTY_PERCENT = 2f*TEN_PERCENT;
    public static final float EIGHTY_PERCENTY = 4f*TWENTY_PERCENT;

    public static boolean useCollisionDetection = true;
    public static boolean showRadar = true;
    public static boolean showZoomBar = true;

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        camScreen = new CameraSurface(this);
        setContentView(camScreen);

        augmentedView = new AugmentedView(this);
        augmentedView.setOnTouchListener(this);
        LayoutParams augLayout = new LayoutParams(  LayoutParams.WRAP_CONTENT, 
                                                    LayoutParams.WRAP_CONTENT);
        addContentView(augmentedView,augLayout);
        
        zoomLayout = new LinearLayout(this);
        zoomLayout.setVisibility((showZoomBar)?LinearLayout.VISIBLE:LinearLayout.GONE);
        zoomLayout.setOrientation(LinearLayout.VERTICAL);
        zoomLayout.setPadding(5, 5, 5, 5);
        zoomLayout.setBackgroundColor(ZOOMBAR_BACKGROUND_COLOR);

        myZoomBar = new VerticalSeekBar(this);
        myZoomBar.setMax(100);
//        myZoomBar.setProgress(50);    // 11km
        myZoomBar.setProgress(75);      // 36km
        myZoomBar.setOnSeekBarChangeListener(myZoomBarOnSeekBarChangeListener);
        LinearLayout.LayoutParams zoomBarParams =  new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.FILL_PARENT);
        zoomBarParams.gravity = Gravity.CENTER_HORIZONTAL;

		ImageView imageView = new ImageView(this);
		imageView.setImageBitmap(BitmapFactory.decodeResource(this.getResources(),
                R.drawable.augument_close));

        FrameLayout.LayoutParams frameLayoutParams = new FrameLayout.LayoutParams(  LayoutParams.WRAP_CONTENT, 
                                                                                    LayoutParams.MATCH_PARENT,
                                                                                    Gravity.RIGHT);

		Display display = getWindowManager().getDefaultDisplay();

		frameLayoutParams.bottomMargin = display.getHeight()-100;
		
		imageView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		addContentView(imageView, frameLayoutParams);
        
        updateDataOnZoom();

        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "DimScreen");
    }

	@Override
	public void onResume() {
		super.onResume();

		wakeLock.acquire();
	}

	@Override
	public void onPause() {
		super.onPause();

		wakeLock.release();
	}
	
	@Override
    public void onSensorChanged(SensorEvent evt) {
        super.onSensorChanged(evt);

        if (    evt.sensor.getType() == Sensor.TYPE_ACCELEROMETER || 
                evt.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
        {
            augmentedView.postInvalidate();
        }
    }
    
    private OnSeekBarChangeListener myZoomBarOnSeekBarChangeListener = new OnSeekBarChangeListener() {
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            updateDataOnZoom();
            camScreen.invalidate();
        }

        public void onStartTrackingTouch(SeekBar seekBar) {
            //Not used
        }

        public void onStopTrackingTouch(SeekBar seekBar) {
            updateDataOnZoom();
            camScreen.invalidate();
        }
    };

    private static float calcZoomLevel(){
        int myZoomLevel = myZoomBar.getProgress();
        float out = 0;

        float percent = 0;
        if (myZoomLevel <= 25) {
            percent = myZoomLevel/25f;
            out = ONE_PERCENT*percent;
        } else if (myZoomLevel > 25 && myZoomLevel <= 50) {
            percent = (myZoomLevel-25f)/25f;
            out = ONE_PERCENT+(TEN_PERCENT*percent);
        } else if (myZoomLevel > 50 && myZoomLevel <= 75) {
            percent = (myZoomLevel-50f)/25f;
            out = TEN_PERCENT+(TWENTY_PERCENT*percent);
        } else {
            percent = (myZoomLevel-75f)/25f;
            out = TWENTY_PERCENT+(EIGHTY_PERCENTY*percent);
        }
        return out;
    }

    protected void updateDataOnZoom() {
        float zoomLevel = calcZoomLevel();
        ARData.setRadius(zoomLevel);
        ARData.setZoomLevel(FORMAT.format(zoomLevel));
        ARData.setZoomProgress(myZoomBar.getProgress());
    }

	public boolean onTouch(View view, MotionEvent me) {

	    for (Marker marker : ARData.getMarkers()) {
	        if (marker.handleClick(me.getX(), me.getY())) {
	            if (me.getAction() == MotionEvent.ACTION_UP) markerTouched(marker);
	            return true;
	        }
            //todo
            //마커의 위치를 가지고 그 방향에 적이 있는지 계산하고 있으면 서버에 전송해야함
            PhysicalLocationUtility location = marker.getLocation2();
            Location currentLocation = ARData.getCurrentLocation();
            float azimuth = ARData.getAzimuth();
            float pitch = ARData.getPitch();
            float roll = ARData.getRoll();
            float distance[] = new float[3];
            Location.distanceBetween(currentLocation.getLatitude(),currentLocation.getLongitude(),location.getLatitude(),location.getLongitude(),distance);
            //if(적이 카메라 방향에 있고 일정거리 안일 경우)
            //      서버로 누구를 잡았다고 결과 전송
            Toast.makeText(this,location.getLatitude()+","+location.getLongitude()+"에 적이 있음: d="+distance[0]+"=="+marker.getDistance(),Toast.LENGTH_SHORT).show();

            //todo end
        }
        //Todo
        Toast.makeText(this,"화면 터치 됨: 여기 카메라 찍는 코드 추가 필요",Toast.LENGTH_SHORT).show();
        //또는 버튼을 만들어 버튼에만 터치 이벤트를 등록 해도 됨
        //Todo end
		return super.onTouchEvent(me);
	};
	
	protected void markerTouched(Marker marker) {
		Log.w(TAG,"markerTouched() not implemented.");
	}
}