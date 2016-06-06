package augmentedReality;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.widget.Toast;
import com.sylphe.app.android.R;
import org.json.JSONObject;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Log;

public class WikipediaDataSource extends NetworkDataSource {
	private static final String BASE_URL = "http://ws.geonames.org/findNearbyWikipediaJSON";
    private Context context;
	private static Bitmap icon = null;
//	private JoinManager joinManager;
	
	public WikipediaDataSource(Resources res, Context context) {
	    if (res==null) throw new NullPointerException();
        
        createIcon(res);
        this.context = context;
    }

    protected void createIcon(Resources res) {
        if (res==null) throw new NullPointerException();
        
        icon=BitmapFactory.decodeResource(res, R.drawable.ic_launcher);
    }

	@Override
	public String createRequestURL(double lat, double lon, double alt, float radius, String locale) {
		return BASE_URL+
        "?lat=" + lat +
        "&lng=" + lon +
        "&radius="+ radius +
        "&maxRows=40" +
        "&lang=" + locale;

	}

	@Override
	public List<Marker> parse(JSONObject root) {
		//todo
		//여기서 적들 데이터 받아 오는것 추가하면 됨
		//아래 코드는 지우면 됨
		//todo end
		// 마커 아이콘 변경
        Bitmap markerIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher);
        Bitmap augumentMarker = BitmapFactory.decodeResource(context.getResources(), R.drawable.augument_marker);
        Bitmap augumentClose = BitmapFactory.decodeResource(context.getResources(), R.drawable.augument_close);
        Bitmap augumentCheck = BitmapFactory.decodeResource(context.getResources(), R.drawable.augument_check);
        Bitmap culture = BitmapFactory.decodeResource(context.getResources(), R.drawable.wikipedia);
		Bitmap course2 = BitmapFactory.decodeResource(context.getResources(), R.drawable.wikipedia);

        List<Marker> markerList = new ArrayList<Marker>();

       // MapInformationDAO mapInfoManager = new MapInformationDAO(context);
      //  List<MapInformationVO> mapInfo = mapInfoManager.selectCourse("1");
		Map<String, String> markerMap = new HashMap<String, String>();
		
        /*for (MapInformationVO olleData : mapInfo) {
	        markerMap.put("courseNum", "1");
            markerList.add(new Marker(olleData.getTitle(), olleData.getLat(), olleData.getLng(), 45, Color.WHITE, markerMap));
        }*/
		// 파라미터 1 : 마커 이름 ( 변경 )
		// 파라미터 2 : 위도 ( 변경 )
		// 파라미터 3 : 경도 ( 변경 )
		// 파라미터 4 : 고도 ( 45로 고정 또는 계산해서 추후 처리 )
		// 파라미터 5 : 안씀
		// 파라미터 6 : 마커 아이콘 변경시 사용 ( 변경 )
		// 파라미터 7 : 토스트 출력 문자 정의 ( 변경 )		
		Map<String, String> markerMap1 = new HashMap<String, String>();
		markerMap1.put("cultureKey", "1-1/0");
		
		IconMarker test2 = new IconMarker("우도의 영등당들(돈짓당)", 33.40086,126.501728, 49.0, Color.WHITE, culture, markerMap1);

		Log.e("증강", "WikipediaDataSource");
		markerList.add(test2);


		return markerList;
	}
}