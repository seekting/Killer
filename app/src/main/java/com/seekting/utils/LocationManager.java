/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.seekting.utils;

import android.content.Context;
import android.location.Location;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.HandlerThread;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

/**
 * A class that handles everything about location.
 */
public class LocationManager {
    private static final String TAG = "LocationManager";

    private Listener mListener;
    private android.location.LocationManager mLocationManager;
    private boolean mRecordLocation;
    // use cache location to avoid location miss for provider status changed,
    // or no cache in location provider in global version or android one/go device,
    // or user change location settings as medium/low in system settings.
    private Location mCacheLocation;
    // the lastknownlocation need to send to gallery in Miui build when receive no location here,
    // gallery will check the location time to detect which level of position can be used,
    // such as use street(for the location is produced at last 3 minute)
    // use district(last 30 minute)/ use city (last 1 hour) / use province(other case)
    private Location mLastKnownLocation;
    private static final int GPS_REQUEST_LOCATION_TIME_OUT = 1000 * 60;
    private static final long LOCATION_TIME_THRESHOLD = 1000 * 60 * 60; // 1 hour
    private static final long VALID_LAST_KNOWN_LOCATION_AGE = 1000 * 60 * 3; // 3 min
    private Timer mTimer;
    private HandlerThread mThreadHandler;

    public void setLocationUpdateListener(LocationUpdateListener locationUpdateListener) {
        mLocationUpdateListener = locationUpdateListener;
    }

    private LocationUpdateListener mLocationUpdateListener;

    LocationListener[] mLocationListeners = new LocationListener[]{
            new LocationListener(android.location.LocationManager.GPS_PROVIDER),
            new LocationListener(android.location.LocationManager.NETWORK_PROVIDER)
    };

    public interface LocationUpdateListener {
        void onLocationUpdate(Location location);

    }

    public interface Listener {
        void showGpsOnScreenIndicator(boolean hasSignal);

        void hideGpsOnScreenIndicator();
    }

    private LocationManager() {
        mThreadHandler = new HandlerThread("Camera Handler Thread");
        mThreadHandler.start();
    }

    public static LocationManager instance() {
        return LocationManagerHolder.sLocationManager;
    }

    public void setListener(Listener listener) {
        mListener = listener;
    }

    public void unsetListener(Listener listener) {
        if (mListener == listener) {
            mListener = null;
        }
    }

    public Location getCurrentLocation() {
        if (!mRecordLocation) return null;

        // go in best to worst order
        for (int i = 0; i < mLocationListeners.length; i++) {
            Location l = mLocationListeners[i].current();
            if (l != null) {
                Log.v(TAG, "get current location, it is from " + mLocationListeners[i].mProvider);
                return validateLocation(l);
            }
        }
        Log.d(TAG, "No location received yet. cache location is "
                + (mCacheLocation != null ? "not null" : "null"));

        return validateLocation(mCacheLocation);
    }

    private static Location validateLocation(Location location) {
        long now = System.currentTimeMillis();
        if (location != null && Math.abs(location.getTime() - now) > LOCATION_TIME_THRESHOLD) {
            Log.d(TAG, "validateLocation: modify to now from " + location.getTime());
            location.setTime(now);
        }
        return location;
    }

    public void recordLocation(boolean recordLocation, Context context) {
        if (mRecordLocation != recordLocation) {
            mRecordLocation = recordLocation;
            if (recordLocation && PermissionUtil.hasLocationPermissions(context)) {
                startReceivingLocationUpdates(context);
            } else {
                stopReceivingLocationUpdates();
            }
        }
    }

    /**
     * cancel gps location listener timer
     */
    private void cancelTimer() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
    }

    private void startReceivingLocationUpdates(Context context) {
        if (mLocationManager == null) {
            mLocationManager = (android.location.LocationManager)
                    context.getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
        if (mLocationManager != null) {
            try {
                mLocationManager.requestLocationUpdates(
                        android.location.LocationManager.NETWORK_PROVIDER,
                        1000,
                        0F,
                        mLocationListeners[1],
                        mThreadHandler.getLooper());
            } catch (SecurityException ex) {
                Log.i(TAG, "fail to request location update, ignore", ex);
            } catch (IllegalArgumentException ex) {
                Log.d(TAG, "provider does not exist " + ex.getMessage());
            }
            try {
                mLocationManager.requestLocationUpdates(
                        android.location.LocationManager.GPS_PROVIDER,
                        1000,
                        0F,
                        mLocationListeners[0],
                        mThreadHandler.getLooper());
                // 如果1分钟后仍然没有GPS位置信息更新，则取消监听
                cancelTimer();
                mTimer = new Timer(true);
                mTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        stopReceivingGPSLocationUpdates();
                        mTimer = null;
                    }
                }, GPS_REQUEST_LOCATION_TIME_OUT);
                if (mListener != null) mListener.showGpsOnScreenIndicator(false);
            } catch (SecurityException ex) {
                Log.i(TAG, "fail to request location update, ignore", ex);
            } catch (IllegalArgumentException ex) {
                Log.d(TAG, "provider does not exist " + ex.getMessage());
            }
            Log.d(TAG, "startReceivingLocationUpdates");
            getLastLocation();
        }
    }

    private void stopReceivingLocationUpdates() {
        cancelTimer();
        if (mLocationManager != null) {
            for (int i = 0; i < mLocationListeners.length; i++) {
                try {
                    mLocationManager.removeUpdates(mLocationListeners[i]);
                } catch (Exception ex) {
                    Log.i(TAG, "fail to remove location listeners, ignore", ex);
                }
                mLocationListeners[i].mValid = false;
            }
            Log.d(TAG, "stopReceivingLocationUpdates");
        }
        if (mListener != null) mListener.hideGpsOnScreenIndicator();
    }

    private void getLastLocation() {
        Location location;
        try {
            Location gps = mLocationManager.getLastKnownLocation(
                    android.location.LocationManager.GPS_PROVIDER);
            Location network = mLocationManager.getLastKnownLocation(
                    android.location.LocationManager.NETWORK_PROVIDER);
            mLastKnownLocation = getBetterLocation(gps, network);
            // 1, lastknownlocation maybe unavailable from provider,
            // 2, times from lastknown location of provider maybe wrong,
            // so we compare between mCacheLocation from Camera & location from provider too.
            location = getBetterLocation(mCacheLocation, mLastKnownLocation);
        } catch (SecurityException ex) {
            Log.e(TAG, "fail to request last location update, ignore", ex);
            location = mCacheLocation;
        }
        if (isValidLastKnownLocation(location)) {
            mCacheLocation = location;
        } else {
            mCacheLocation = null;
        }
        Log.d(TAG, "last cache location is " + (mCacheLocation != null ? "not null" : "null"));
    }

    private Location getBetterLocation(Location first, Location second) {
        Location location = first;
        if (second == null) {
            return location;
        }
        if (first == null
                || first.getTime() < second.getTime()
                || (first.getTime() == second.getTime()
                && android.location.LocationManager.GPS_PROVIDER.equals(second.getProvider()))) {
            location = second;
        }
        return location;
    }

    private void updateCacheLocation(Location location) {
        Location l = getBetterLocation(mCacheLocation, location);
        if (mCacheLocation != null) {
            mCacheLocation.set(l);
        } else {
            mCacheLocation = new Location(l);
        }

    }

    private boolean isValidLastKnownLocation(Location l) {
        if (l != null) {
            long now = System.currentTimeMillis();
            if (Math.abs(now - l.getTime()) < VALID_LAST_KNOWN_LOCATION_AGE) {
                return true;
            }
        }
        return false;
    }

    private void stopReceivingGPSLocationUpdates() {
        if (mLocationManager != null) {
            try {
                mLocationManager.removeUpdates(mLocationListeners[0]);
            } catch (Exception ex) {
                Log.i(TAG, "fail to remove location listeners, ignore", ex);
            }
            mLocationListeners[0].mValid = false;
            Log.d(TAG, "stopReceivingGPSLocationUpdates");
        }
        if (mListener != null) mListener.hideGpsOnScreenIndicator();
    }

    private class LocationListener
            implements android.location.LocationListener {
        Location mLastLocation;
        boolean mValid = false;
        String mProvider;

        public LocationListener(String provider) {
            mProvider = provider;
            mLastLocation = new Location(mProvider);
        }

        @Override
        public void onLocationChanged(Location newLocation) {
            if (newLocation.getLatitude() == 0.0 && newLocation.getLongitude() == 0.0) {
                // Hack to filter out 0.0,0.0 locations
                return;
            }
            // If GPS is available before start camera, we won't get status
            // update so update GPS indicator when we receive data.
            if (mRecordLocation && android.location.LocationManager.GPS_PROVIDER.equals(mProvider)) {
                cancelTimer();
                if (mListener != null) {
                    mListener.showGpsOnScreenIndicator(true);
                }
            }
            if (!mValid) {
                Log.d(TAG, "Got first location, it is from " + mProvider);
            } else {
                Log.v(TAG, "update location, it is from " + mProvider);
            }
            mLastLocation.set(newLocation);
            // add the received location to cache for status change
            updateCacheLocation(mLastLocation);
            mValid = true;
            if (mLocationUpdateListener != null) {
                mLocationUpdateListener.onLocationUpdate(mCacheLocation);
            }
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onProviderDisabled(String provider) {
            mValid = false;
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            switch (status) {
            case LocationProvider.OUT_OF_SERVICE:
            case LocationProvider.TEMPORARILY_UNAVAILABLE: {
                mValid = false;
                if (mListener != null && mRecordLocation &&
                        android.location.LocationManager.GPS_PROVIDER.equals(provider)) {
                    mListener.showGpsOnScreenIndicator(false);
                }
                break;
            }
            }
        }

        public Location current() {
            return mValid ? mLastLocation : null;
        }
    }

    public Location getLastKnownLocation() {
        if (!mRecordLocation) return null;
        return mLastKnownLocation;
    }

    private static class LocationManagerHolder {
        private static LocationManager sLocationManager = new LocationManager();
    }
}