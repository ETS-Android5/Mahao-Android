package ke.co.tonyoa.mahao.app.utils;

import android.Manifest;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.maps.model.LatLng;

import javax.inject.Inject;
import javax.inject.Singleton;

import ke.co.tonyoa.mahao.app.sharedprefs.SharedPrefs;

@Singleton
public class LocationUpdateListener implements LocationListener {

    private LocationListener mListener;
    private LocationManager mLocationManager;
    private Context mContext;
    private SharedPrefs mSharedPrefs;

    @Inject
    public LocationUpdateListener(Application context, SharedPrefs sharedPrefs){
        mContext = context;
        mLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        mSharedPrefs = sharedPrefs;
    }

    public boolean startListening(final LocationListener pListener, final long pUpdateTime, final float pUpdateDistance) {
        boolean result = false;
        mListener = pListener;
        for (final String provider : mLocationManager.getProviders(true)) {
            if (LocationManager.GPS_PROVIDER.equals(provider) || LocationManager.NETWORK_PROVIDER.equals(provider)) {
                result = true;
                if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
                mLocationManager.requestLocationUpdates(provider, pUpdateTime, pUpdateDistance, this);
            }
        }
        return result;
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        mSharedPrefs.saveLastLocation(new LatLng(location.getLatitude(), location.getLongitude()));
        if (mListener!=null){
            mListener.onLocationChanged(location);
        }
    }
}
