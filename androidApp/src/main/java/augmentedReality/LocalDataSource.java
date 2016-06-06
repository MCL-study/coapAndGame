package augmentedReality;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import com.sylphe.app.android.R;


public class LocalDataSource extends DataSource{
    private final Context context;
    private List<Marker> cachedMarkers = new ArrayList<Marker>();
    private static Bitmap icon = null;
    
    public LocalDataSource(Resources res, Context context) {
        if (res==null) throw new NullPointerException();
        
        createIcon(res);
        this.context = context;
    }
    
    protected void createIcon(Resources res) {
        if (res==null) throw new NullPointerException();
        
        icon=BitmapFactory.decodeResource(res, R.drawable.ic_launcher);
    }
    
    public List<Marker> getMarkers() {
//        Marker atl = new IconMarker("ATL", 39.931269, -75.051261, 0, Color.DKGRAY, icon, "Toast출력 문자");
//        cachedMarkers.add(atl);
//
//        Marker home = new Marker("Mt Laurel", 39.95, -74.9, 0, Color.YELLOW, "Mt Laurel");
//        cachedMarkers.add(home);

        return cachedMarkers;
    }
}