package com.baddaddy.moonshinefree;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.DateFormat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.baddaddy.astronomy.AstroFormat;
import com.baddaddy.astronomy.MoonInfo;
import com.baddaddy.astronomy.MoonTimes;
import com.baddaddy.moonshinefree.utils.ErrorUtil;
import com.baddaddy.moonshinefree.utils.GeocoderUtil;
import com.baddaddy.moonshinefree.utils.LocationUtils;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class MoonShine extends Activity {
    private static final int ACTIVITY_LOCATIONS = 1;
    private static final int ACTIVITY_MONTHLY = 0;
    static final int DATE_DIALOG_ID = 0;
    private static final int MEDIUM_IMAGE_SIZE = 150;
    private static final int MEDIUM_PANEL_HEIGHT = 185;
    public static final int MENU_ABOUT = 6;
    public static final int MENU_HELP = 7;
    public static final int MENU_LOCATIONS = 3;
    public static final int MENU_MONTHLY = 2;
    public static final int MENU_REFRESH = 5;
    public static final int MENU_TODAY = 1;
    public static final int MENU_UPGRADE = 8;
    private static boolean PAID_VERSION = false;
    private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;
    private int TIMEOUT_NETWORK;
    private int TIMEOUT_SEC;
    private int TIMEOUT_SHORT;
    private Location bestLocation;
    private int counts;
    private double currentLatitude;
    private String currentLocationName;
    private double currentLongitude;
    private String currentTimezone;
    private GestureDetector gestureDetector;
    OnTouchListener gestureListener;
    private Handler handler;
    private boolean isGPSDone;
    private boolean isGPSEnabled;
    private boolean isLocationEnabled;
    private boolean isNetworkDone;
    private boolean isNetworkEnabled;
    private int lastImageIndex;
    private double latitude;
    private Location locationGPS;
    private long locationId;
    long[] locationIdArray;
    private int locationIndex;
    private String locationName;
    private Location locationNetwork;
    private double longitude;
    private TextView mAgeDisplay;
    private Button mDateButton;
    private OnDateSetListener mDateSetListener;
    private int mDay;
    private LocationDbAdapter mDbHelper;
    private TextView mDistanceDisplay;
    private TextView mIlluminatedDisplay;
    private TextView mLocationCurrentDisplay;
    private Cursor mLocationCursor;
    private TextView mLocationDisplay;
    public final LocationListener mLocationListener01;
    public final LocationListener mLocationListener02;
    private TextView mLocationNameDisplay;
    private TextView mLocationNumberDisplay;
    private int mMonth;
    private TextView mMoonPhaseDisplay;
    private Button mNextDayButton;
    private TextView mNextFullDisplay;
    private TextView mNextNewDisplay;
    private TextView mPerigeeDisplay;
    private ImageView mPhaseImage;
    private ImageView mPhaseImage2;
    private ImageView mPhasePanel;
    private int[] mPhotoIds;
    private Button mPrevDayButton;
    private TextView mRiseDiffDisplay;
    private TextView mRiseDisplay;
    private TextView mSetDiffDisplay;
    private TextView mSetDisplay;
    private TextView mTransitDiffDisplay;
    private TextView mTransitDisplay;
    private TextView mVisibleDisplay;
    private int mYear;
    private LocationManager myLocationManager;
    private Runnable showTime;
    private String timezone;

    /* renamed from: com.baddaddy.moonshinefree.MoonShine.1 */
    class C00031 implements OnDateSetListener {
        C00031() {
        }

        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            MoonShine.this.mYear = year;
            MoonShine.this.mMonth = monthOfYear;
            MoonShine.this.mDay = dayOfMonth;
            MoonShine.this.updateDisplay();
        }
    }

    /* renamed from: com.baddaddy.moonshinefree.MoonShine.2 */
    class C00042 implements Runnable {
        C00042() {
        }

        public void run() {
            try {
                MoonShine moonShine = MoonShine.this;
                moonShine.counts = moonShine.counts + MoonShine.MENU_TODAY;
                if (MoonShine.this.counts > MoonShine.this.TIMEOUT_SEC || (MoonShine.this.isNetworkDone && MoonShine.this.counts > MoonShine.this.TIMEOUT_NETWORK)) {
                    MoonShine.this.isGPSDone = true;
                    MoonShine.this.isNetworkDone = true;
                }
                MoonShine.this.bestLocation = MoonShine.this.getCurrentLocation();
                if (MoonShine.this.bestLocation == null) {
                    MoonShine.this.handler.postDelayed(this, 1000);
                } else {
                    MoonShine.this.updateCurrentLocation(MoonShine.this.bestLocation);
                }
            } catch (Exception e) {
                ErrorUtil.reportError(MoonShine.this, e);
            }
        }
    }

    /* renamed from: com.baddaddy.moonshinefree.MoonShine.3 */
    class C00053 implements LocationListener {
        C00053() {
        }

        public void onLocationChanged(Location location) {
            MoonShine.this.OnGPSChange(location);
        }

        public void onProviderDisabled(String provider) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    }

    /* renamed from: com.baddaddy.moonshinefree.MoonShine.4 */
    class C00064 implements LocationListener {
        C00064() {
        }

        public void onLocationChanged(Location location) {
            MoonShine.this.OnNetworkChange(location);
        }

        public void onProviderDisabled(String provider) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    }

    /* renamed from: com.baddaddy.moonshinefree.MoonShine.5 */
    class C00075 implements OnClickListener {
        C00075() {
        }

        public void onClick(View v) {
            MoonShine.this.showDialog(MoonShine.DATE_DIALOG_ID);
        }
    }

    /* renamed from: com.baddaddy.moonshinefree.MoonShine.6 */
    class C00086 implements OnClickListener {
        C00086() {
        }

        public void onClick(View v) {
            MoonShine.this.addDay(-1);
        }
    }

    /* renamed from: com.baddaddy.moonshinefree.MoonShine.7 */
    class C00097 implements OnClickListener {
        C00097() {
        }

        public void onClick(View v) {
            MoonShine.this.addDay(MoonShine.MENU_TODAY);
        }
    }

    /* renamed from: com.baddaddy.moonshinefree.MoonShine.8 */
    class C00108 implements OnTouchListener {
        C00108() {
        }

        public boolean onTouch(View v, MotionEvent event) {
            if (MoonShine.this.gestureDetector.onTouchEvent(event)) {
                return true;
            }
            return false;
        }
    }

    /* renamed from: com.baddaddy.moonshinefree.MoonShine.9 */
    class C00119 implements DialogInterface.OnClickListener {
        C00119() {
        }

        public void onClick(DialogInterface dialog, int id) {
            dialog.cancel();
        }
    }

    private class GetLocationNameTask extends AsyncTask<Double, Void, String> {
        private GetLocationNameTask() {
        }

        protected String doInBackground(Double... coords) {
            String locName = "";
            String defName = "Current Location";
            try {
                locName = GeocoderUtil.reverseGeocodeLocality(MoonShine.this.getBaseContext(), coords[MoonShine.DATE_DIALOG_ID].doubleValue(), coords[MoonShine.MENU_TODAY].doubleValue(), MoonShine.MENU_MONTHLY);
                if (locName == null || "".equals(locName)) {
                    return defName;
                }
                return locName;
            } catch (Exception e) {
                ErrorUtil.reportError(MoonShine.this, e);
                return locName;
            }
        }

        protected void onPostExecute(String locName) {
            try {
                MoonShine.this.locationName = locName;
                MoonShine.this.currentLocationName = locName;
                MoonShine.this.setProgressBarIndeterminateVisibility(false);
                MoonShine.this.mLocationNameDisplay.setText(locName);
            } catch (Exception e) {
                ErrorUtil.reportError(MoonShine.this, e);
            }
        }
    }

    class MyGestureDetector extends SimpleOnGestureListener {
        MyGestureDetector() {
        }

        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            try {
                if (e1.getX() - e2.getX() > 120.0f && Math.abs(velocityX) > 200.0f) {
                    MoonShine.this.addDay(MoonShine.MENU_TODAY);
                    return false;
                } else if (e2.getX() - e1.getX() > 120.0f && Math.abs(velocityX) > 200.0f) {
                    MoonShine.this.addDay(-1);
                    return false;
                } else if (e1.getY() - e2.getY() <= 120.0f || Math.abs(velocityY) <= 200.0f || !MoonShine.PAID_VERSION) {
                    if (e2.getY() - e1.getY() > 120.0f && Math.abs(velocityY) > 200.0f && MoonShine.PAID_VERSION) {
                        MoonShine.this.moveLocation(-1);
                    }
                    return false;
                } else {
                    MoonShine.this.moveLocation(MoonShine.MENU_TODAY);
                    return false;
                }
            } catch (Exception e) {
            }
        }
    }

    public MoonShine() {
        this.mYear = DATE_DIALOG_ID;
        this.mMonth = DATE_DIALOG_ID;
        this.mDay = DATE_DIALOG_ID;
        this.lastImageIndex = DATE_DIALOG_ID;
        this.TIMEOUT_SEC = 180;
        this.TIMEOUT_SHORT = 60;
        this.TIMEOUT_NETWORK = 30;
        this.isLocationEnabled = false;
        this.isGPSDone = false;
        this.isNetworkDone = false;
        this.isGPSEnabled = true;
        this.isNetworkEnabled = true;
        this.locationGPS = null;
        this.locationNetwork = null;
        this.bestLocation = null;
        this.handler = new Handler();
        this.counts = DATE_DIALOG_ID;
        this.mPhotoIds = new int[]{C0012R.drawable.moonphase0, C0012R.drawable.moonphase1, C0012R.drawable.moonphase2, C0012R.drawable.moonphase3, C0012R.drawable.moonphase4, C0012R.drawable.moonphase5, C0012R.drawable.moonphase6, C0012R.drawable.moonphase7, C0012R.drawable.moonphase8, C0012R.drawable.moonphase9, C0012R.drawable.moonphase10, C0012R.drawable.moonphase11, C0012R.drawable.moonphase12, C0012R.drawable.moonphase13, C0012R.drawable.moonphase14, C0012R.drawable.moonphase15, C0012R.drawable.moonphase16, C0012R.drawable.moonphase17, C0012R.drawable.moonphase18, C0012R.drawable.moonphase19, C0012R.drawable.moonphase20, C0012R.drawable.moonphase21, C0012R.drawable.moonphase22, C0012R.drawable.moonphase23, C0012R.drawable.moonphase24, C0012R.drawable.moonphase25, C0012R.drawable.moonphase26, C0012R.drawable.moonphase27, C0012R.drawable.moonphase28, C0012R.drawable.moonphase29};
        this.mDateSetListener = new C00031();
        this.showTime = new C00042();
        this.mLocationListener01 = new C00053();
        this.mLocationListener02 = new C00064();
    }

    static {
        PAID_VERSION = true;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(MENU_REFRESH);
        setContentView(C0012R.layout.main);
        setProgressBarIndeterminateVisibility(false);
        if (getPackageName().equals("com.baddaddy.moonshine")) {
            PAID_VERSION = true;
        } else {
            PAID_VERSION = false;
        }
        this.mPhaseImage = (ImageView) findViewById(C0012R.id.moonPhaseImage);
        this.mPhaseImage2 = (ImageView) findViewById(C0012R.id.moonPhaseImage2);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        if (dm.densityDpi < 240) {
            this.mPhaseImage.setMaxHeight(MEDIUM_IMAGE_SIZE);
            this.mPhaseImage.setMaxWidth(MEDIUM_IMAGE_SIZE);
            this.mPhaseImage2.setMaxHeight(MEDIUM_IMAGE_SIZE);
            this.mPhaseImage2.setMaxWidth(MEDIUM_IMAGE_SIZE);
            this.mPhasePanel = (ImageView) findViewById(C0012R.id.moonPanelImage);
            this.mPhasePanel.setMaxHeight(MEDIUM_PANEL_HEIGHT);
        }
        this.mLocationDisplay = (TextView) findViewById(C0012R.id.locationDisplay);
        this.mLocationNameDisplay = (TextView) findViewById(C0012R.id.locationNameDisplay);
        this.mLocationCurrentDisplay = (TextView) findViewById(C0012R.id.locationCurrentDisplay);
        this.mLocationNumberDisplay = (TextView) findViewById(C0012R.id.locationNumberDisplay);
        this.mMoonPhaseDisplay = (TextView) findViewById(C0012R.id.moonPhaseDisplay);
        this.mRiseDisplay = (TextView) findViewById(C0012R.id.riseDisplay);
        this.mRiseDiffDisplay = (TextView) findViewById(C0012R.id.riseDiffDisplay);
        this.mSetDisplay = (TextView) findViewById(C0012R.id.setDisplay);
        this.mSetDiffDisplay = (TextView) findViewById(C0012R.id.setDiffDisplay);
        this.mTransitDisplay = (TextView) findViewById(C0012R.id.transitDisplay);
        this.mTransitDiffDisplay = (TextView) findViewById(C0012R.id.transitDiffDisplay);
        this.mIlluminatedDisplay = (TextView) findViewById(C0012R.id.illuminatedDisplay);
        this.mAgeDisplay = (TextView) findViewById(C0012R.id.ageDisplay);
        this.mDistanceDisplay = (TextView) findViewById(C0012R.id.distanceDisplay);
        this.mNextNewDisplay = (TextView) findViewById(C0012R.id.nextNewDisplay);
        this.mNextFullDisplay = (TextView) findViewById(C0012R.id.nextFullDisplay);
        this.mPerigeeDisplay = (TextView) findViewById(C0012R.id.perigeeDisplay);
        this.mDateButton = (Button) findViewById(C0012R.id.dateBtn);
        this.mPrevDayButton = (Button) findViewById(C0012R.id.prevDayBtn);
        this.mNextDayButton = (Button) findViewById(C0012R.id.nextDayBtn);
        if (this.mMonth == 0 || this.mDay == 0 || this.mYear == 0) {
            Calendar c = Calendar.getInstance();
            this.mYear = c.get(MENU_TODAY);
            this.mMonth = c.get(MENU_MONTHLY);
            this.mDay = c.get(MENU_REFRESH);
        }
        if (PAID_VERSION) {
            this.mDbHelper = new LocationDbAdapter(this);
            this.mDbHelper.open();
            this.mLocationCursor = this.mDbHelper.fetchAllLocations();
            this.locationIdArray = this.mDbHelper.fetchAllIds(true);
            this.mLocationCursor.close();
            this.mDbHelper.close();
            this.locationId = 0;
            this.locationIndex = DATE_DIALOG_ID;
        }
        this.timezone = TimeZone.getDefault().getID();
        this.currentTimezone = this.timezone;
        this.myLocationManager = (LocationManager) getSystemService("location");
        if (this.myLocationManager.isProviderEnabled("gps") || this.myLocationManager.isProviderEnabled("network")) {
            this.isLocationEnabled = true;
        } else {
            this.isLocationEnabled = false;
            alertNoServices();
        }
        if (this.isLocationEnabled) {
            setProgressBarIndeterminateVisibility(true);
            initGPS(true);
        }
        this.mDateButton.setOnClickListener(new C00075());
        this.mPrevDayButton.setOnClickListener(new C00086());
        this.mNextDayButton.setOnClickListener(new C00097());
        this.gestureDetector = new GestureDetector(new MyGestureDetector());
        this.gestureListener = new C00108();
        updateDisplay();
    }

    protected void onDestroy() {
        if (PAID_VERSION) {
            this.mLocationCursor.close();
            this.mDbHelper.close();
        }
        super.onDestroy();
    }

    private void updateDisplay() {
        NumberFormat decimalFormat = new DecimalFormat("#.######");
        this.mLocationNameDisplay.setText(this.locationName);
        this.mLocationDisplay.setText(decimalFormat.format(this.latitude) + ", " + decimalFormat.format(this.longitude));
        if (PAID_VERSION) {
            this.mLocationNumberDisplay.setText(new StringBuilder(String.valueOf(this.locationIndex + MENU_TODAY)).append(" / ").append(this.locationIdArray.length).toString());
            if (this.locationId == 0) {
                this.mLocationCurrentDisplay.setText("(current)");
            } else {
                this.mLocationCurrentDisplay.setText("");
            }
        } else {
            this.mLocationNumberDisplay.setText("");
            this.mLocationCurrentDisplay.setText("");
        }
        Calendar c = Calendar.getInstance();
        int year = c.get(MENU_TODAY);
        int month = c.get(MENU_MONTHLY);
        int day = c.get(MENU_REFRESH);
        c.set(this.mYear, this.mMonth, this.mDay, 12, DATE_DIALOG_ID, DATE_DIALOG_ID);
        Date date = c.getTime();
        this.mDateButton.setText(formatDate(date));
        long timezoneOffset = (long) TimeZone.getTimeZone(this.timezone).getOffset(date.getTime());
        MoonInfo moonInfo = new MoonInfo(date, this.latitude, this.longitude);
        double[] moonTimes = MoonTimes.getTimes(date, this.latitude, this.longitude, timezoneOffset);
        this.mRiseDisplay.setText(formatTime(moonTimes[DATE_DIALOG_ID]));
        this.mSetDisplay.setText(formatTime(moonTimes[MENU_TODAY]));
        this.mTransitDisplay.setText(formatTime(moonTimes[MENU_MONTHLY]));
        this.mMoonPhaseDisplay.setText(moonInfo.getPhaseText());
        this.mIlluminatedDisplay.setText(new StringBuilder(String.valueOf(String.valueOf(Math.round(moonInfo.getIlluminatedFraction() * 4636737291354636288L)))).append("%").toString());
        this.mAgeDisplay.setText(new StringBuilder(String.valueOf(String.valueOf(Math.round(moonInfo.getAge())))).append(" days").toString());
        this.mDistanceDisplay.setText(String.valueOf(Math.round(moonInfo.getDistance())));
        this.mPerigeeDisplay.setText(new StringBuilder(String.valueOf(String.valueOf(Math.round(moonInfo.getPerigeePercent())))).append("%").toString());
        Date date2 = date;
        this.mNextFullDisplay.setText(formatDate(getNextMoonDate(MENU_REFRESH, date2, this.latitude, this.longitude)));
        date2 = date;
        this.mNextNewDisplay.setText(formatDate(getNextMoonDate(MENU_TODAY, date2, this.latitude, this.longitude)));
        int imageIndex = moonInfo.getPhaseImageIndex();
        if (this.lastImageIndex > 0) {
            int i = this.lastImageIndex;
            if (r0 != imageIndex) {
                this.mPhaseImage2.setImageResource(this.mPhotoIds[this.lastImageIndex]);
                Animation myFadeOutAnimation = AnimationUtils.loadAnimation(this, C0012R.anim.fadeout);
                this.mPhaseImage2.startAnimation(myFadeOutAnimation);
            }
        }
        if (imageIndex != this.lastImageIndex) {
            this.mPhaseImage.setImageResource(this.mPhotoIds[imageIndex]);
            Animation myFadeInAnimation = AnimationUtils.loadAnimation(this, C0012R.anim.fadein);
            this.mPhaseImage.startAnimation(myFadeInAnimation);
        }
        this.lastImageIndex = imageIndex;
        if (year == this.mYear && month == this.mMonth && day == this.mDay) {
            this.mRiseDiffDisplay.setText(AstroFormat.diff(moonTimes[DATE_DIALOG_ID]));
            this.mSetDiffDisplay.setText(AstroFormat.diff(moonTimes[MENU_TODAY]));
            this.mTransitDiffDisplay.setText(AstroFormat.diff(moonTimes[MENU_MONTHLY]));
            return;
        }
        this.mRiseDiffDisplay.setText("");
        this.mSetDiffDisplay.setText("");
        this.mTransitDiffDisplay.setText("");
    }

    private void updateCurrentLocation(Location location) {
        String str = "";
        try {
            this.mLocationNameDisplay.setText("");
            this.mLocationDisplay.setText("");
            setProgressBarIndeterminateVisibility(true);
            if (location != null && location.getLatitude() != 0.0d) {
                this.latitude = location.getLatitude();
                this.longitude = location.getLongitude();
            } else if (this.isNetworkEnabled) {
                alert("Unable to find location.\n\nIt may be due to a weak GPS signal and/or too few cellular towers.\n\nPlease try again later.");
            } else {
                alert("Unable to find location.\n\nIt may be due to a weak GPS signal.\n\nEnabling network location service in the phone settings may help.");
            }
            this.currentLatitude = this.latitude;
            this.currentLongitude = this.longitude;
            this.locationId = 0;
            this.locationIndex = DATE_DIALOG_ID;
            this.timezone = TimeZone.getDefault().getID();
            this.currentTimezone = this.timezone;
            GetLocationNameTask getLocationNameTask = new GetLocationNameTask();
            Double[] dArr = new Double[MENU_MONTHLY];
            dArr[DATE_DIALOG_ID] = Double.valueOf(this.latitude);
            dArr[MENU_TODAY] = Double.valueOf(this.longitude);
            getLocationNameTask.execute(dArr);
            updateDisplay();
        } catch (Exception e) {
            ErrorUtil.reportError(this, e);
        }
    }

    private String formatTime(double time) {
        return AstroFormat.formatTime(DateFormat.getTimeFormat(this), time);
    }

    private String formatDate(Date date) {
        return AstroFormat.formatDate(DateFormat.getDateFormat(this), date);
    }

    private String getVisibleAsString(double[] moonTimes) {
        String result = "";
        Calendar c = Calendar.getInstance();
        double currentTime = (double) (c.get(11) + (c.get(12) / 60));
        if (currentTime <= moonTimes[DATE_DIALOG_ID] || (moonTimes[DATE_DIALOG_ID] <= moonTimes[MENU_TODAY] && currentTime >= moonTimes[MENU_TODAY])) {
            return "";
        }
        return "(Moon is currently visible)";
    }

    private Date getNextMoonDate(int target, Date date, double lat, double lon) {
        int phase = -1;
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int count = DATE_DIALOG_ID;
        while (phase != target && count < 29) {
            count += MENU_TODAY;
            cal.add(MENU_REFRESH, MENU_TODAY);
            phase = new MoonInfo(cal.getTime(), lat, lon).getPhase();
        }
        return cal.getTime();
    }

    private void gotoToday() {
        Calendar c = Calendar.getInstance();
        this.mYear = c.get(MENU_TODAY);
        this.mMonth = c.get(MENU_MONTHLY);
        this.mDay = c.get(MENU_REFRESH);
        updateDisplay();
    }

    private void addDay(int days) {
        Calendar c = Calendar.getInstance();
        c.set(this.mYear, this.mMonth, this.mDay);
        c.add(MENU_REFRESH, days);
        this.mYear = c.get(MENU_TODAY);
        this.mMonth = c.get(MENU_MONTHLY);
        this.mDay = c.get(MENU_REFRESH);
        updateDisplay();
    }

    private void moveLocation(int howMany) {
        int len = this.locationIdArray.length;
        if (len != MENU_TODAY) {
            int j = this.locationIndex + howMany;
            if (j >= len) {
                j = len % j;
            } else if (j < 0) {
                j += len;
            }
            this.locationIndex = j;
            updateLocation(this.locationIdArray[j]);
        }
    }

    private void updateLocation(long id) {
        this.mDbHelper = new LocationDbAdapter(this);
        this.mDbHelper.open();
        this.locationIdArray = this.mDbHelper.fetchAllIds(true);
        this.locationId = id;
        if (this.locationId == 0) {
            this.locationName = this.currentLocationName;
            this.latitude = this.currentLatitude;
            this.longitude = this.currentLongitude;
            this.timezone = this.currentTimezone;
        } else {
            this.mLocationCursor = this.mDbHelper.fetchLocation(this.locationId);
            this.locationName = this.mLocationCursor.getString(this.mLocationCursor.getColumnIndexOrThrow(LocationDbAdapter.KEY_NAME));
            this.latitude = this.mLocationCursor.getDouble(this.mLocationCursor.getColumnIndexOrThrow(LocationDbAdapter.KEY_LATITUDE));
            this.longitude = this.mLocationCursor.getDouble(this.mLocationCursor.getColumnIndexOrThrow(LocationDbAdapter.KEY_LONGITUDE));
            this.timezone = this.mLocationCursor.getString(this.mLocationCursor.getColumnIndexOrThrow(LocationDbAdapter.KEY_TIMEZONE));
            this.mLocationCursor.close();
        }
        this.mDbHelper.close();
        updateDisplay();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        boolean result = super.onCreateOptionsMenu(menu);
        menu.add(DATE_DIALOG_ID, MENU_TODAY, DATE_DIALOG_ID, "Today").setIcon(17301588);
        menu.add(DATE_DIALOG_ID, MENU_MONTHLY, DATE_DIALOG_ID, "Month").setIcon(17301572);
        menu.add(DATE_DIALOG_ID, MENU_REFRESH, DATE_DIALOG_ID, "Refresh").setIcon(C0012R.drawable.ic_menu_refresh);
        menu.add(DATE_DIALOG_ID, MENU_LOCATIONS, DATE_DIALOG_ID, "Locations").setIcon(17301575);
        if (!PAID_VERSION) {
            menu.add(DATE_DIALOG_ID, MENU_UPGRADE, DATE_DIALOG_ID, "Upgrade").setIcon(17301589);
        }
        menu.add(DATE_DIALOG_ID, MENU_ABOUT, DATE_DIALOG_ID, "About").setIcon(17301569);
        menu.add(DATE_DIALOG_ID, MENU_HELP, DATE_DIALOG_ID, "Help").setIcon(17301568);
        return result;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        String str = "month";
        String str2 = LocationDbAdapter.KEY_LATITUDE;
        String str3 = LocationDbAdapter.KEY_LONGITUDE;
        String str4;
        switch (item.getItemId()) {
            case MENU_TODAY /*1*/:
                gotoToday();
                return true;
            case MENU_MONTHLY /*2*/:
                Intent monthlyIntent = new Intent(this, MonthlyListActivity.class);
                str4 = LocationDbAdapter.KEY_LATITUDE;
                monthlyIntent.putExtra(str2, this.latitude);
                str4 = LocationDbAdapter.KEY_LONGITUDE;
                monthlyIntent.putExtra(str3, this.longitude);
                str4 = LocationDbAdapter.KEY_LONGITUDE;
                monthlyIntent.putExtra(str3, this.longitude);
                monthlyIntent.putExtra(LocationDbAdapter.KEY_TIMEZONE, this.timezone);
                if (PAID_VERSION) {
                    str4 = "month";
                    monthlyIntent.putExtra(str, this.mMonth);
                    monthlyIntent.putExtra("year", this.mYear);
                } else {
                    str4 = "month";
                    monthlyIntent.putExtra(str, DATE_DIALOG_ID);
                    monthlyIntent.putExtra("year", 2010);
                }
                startActivityForResult(monthlyIntent, DATE_DIALOG_ID);
                return true;
            case MENU_LOCATIONS /*3*/:
                if (PAID_VERSION) {
                    Intent locationsIntent = new Intent(this, LocationListActivity.class);
                    locationsIntent.putExtra(LocationDbAdapter.KEY_NAME, this.currentLocationName);
                    str4 = LocationDbAdapter.KEY_LATITUDE;
                    locationsIntent.putExtra(str2, Double.valueOf(this.currentLatitude));
                    str4 = LocationDbAdapter.KEY_LONGITUDE;
                    locationsIntent.putExtra(str3, Double.valueOf(this.currentLongitude));
                    locationsIntent.putExtra(LocationDbAdapter.KEY_TIMEZONE, this.currentTimezone);
                    startActivityForResult(locationsIntent, MENU_TODAY);
                } else {
                    alert("Locations are not available in MoonShine Free.  Check out MoonShine in the market.");
                }
                return true;
            case MENU_REFRESH /*5*/:
                setProgressBarIndeterminateVisibility(true);
                initGPS(false);
                return true;
            case MENU_ABOUT /*6*/:
                alert(getString(C0012R.string.about_disclaimer));
                return true;
            case MENU_HELP /*7*/:
                startActivity(new Intent(this, HelpActivity.class));
                return true;
            case MENU_UPGRADE /*8*/:
                startActivity(new Intent("android.intent.action.VIEW", Uri.parse("http://market.android.com/details?id=com.baddaddy.moonshine")));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (resultCode == -1) {
            Bundle extras = intent.getExtras();
            switch (requestCode) {
                case DATE_DIALOG_ID /*0*/:
                    this.mMonth = extras.getInt("month");
                    this.mYear = extras.getInt("year");
                    this.mDay = extras.getInt("day");
                    updateDisplay();
                    return;
                case MENU_TODAY /*1*/:
                    Long rowId = Long.valueOf(extras.getLong(LocationDbAdapter.KEY_ROWID));
                    int idx = extras.getInt("locationIndex");
                    if (rowId.longValue() > 0) {
                        this.locationIndex = idx;
                        updateLocation(rowId.longValue());
                        return;
                    }
                    return;
                default:
                    return;
            }
        }
        this.mDbHelper = new LocationDbAdapter(this);
        this.mDbHelper.open();
        this.locationIdArray = this.mDbHelper.fetchAllIds(true);
        this.mDbHelper.close();
        updateDisplay();
    }

    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DATE_DIALOG_ID /*0*/:
                return new DatePickerDialog(this, this.mDateSetListener, this.mYear, this.mMonth, this.mDay);
            default:
                return null;
        }
    }

    private void initGPS(boolean quickAcquire) {
        String str = "network";
        str = "gps";
        try {
            this.isGPSEnabled = this.myLocationManager.isProviderEnabled("gps");
            this.isNetworkEnabled = this.myLocationManager.isProviderEnabled("network");
            if (this.isNetworkEnabled) {
                this.TIMEOUT_SEC = this.TIMEOUT_SHORT;
            }
            if (quickAcquire) {
                toast("Using last known location");
                this.locationGPS = this.myLocationManager.getLastKnownLocation("gps");
                this.locationNetwork = this.myLocationManager.getLastKnownLocation("network");
                if ((this.locationGPS == null || this.locationGPS.getLatitude() == 0.0d) && (this.locationNetwork == null || this.locationNetwork.getLatitude() == 0.0d)) {
                    doGPS();
                    return;
                } else if (LocationUtils.isBetterLocation(this.locationGPS, this.locationNetwork)) {
                    updateCurrentLocation(this.locationGPS);
                    return;
                } else {
                    updateCurrentLocation(this.locationNetwork);
                    return;
                }
            }
            doGPS();
        } catch (Exception e) {
            ErrorUtil.reportError(this, e);
        }
    }

    private void doGPS() {
        try {
            this.myLocationManager = (LocationManager) getSystemService("location");
            this.isGPSEnabled = this.myLocationManager.isProviderEnabled("gps");
            this.isNetworkEnabled = this.myLocationManager.isProviderEnabled("network");
            startAllUpdate();
            this.handler.postDelayed(this.showTime, 1000);
            if (this.isGPSEnabled) {
                this.isGPSDone = false;
            } else {
                this.isGPSDone = true;
            }
            if (this.isNetworkEnabled) {
                this.isNetworkDone = false;
            } else {
                this.isNetworkDone = true;
            }
            this.bestLocation = null;
            this.counts = DATE_DIALOG_ID;
        } catch (Exception e) {
            ErrorUtil.reportError(this, e);
        }
    }

    public void startAllUpdate() {
        try {
            this.myLocationManager.requestLocationUpdates("gps", 0, 0.0f, this.mLocationListener01);
            this.myLocationManager.requestLocationUpdates("network", 0, 0.0f, this.mLocationListener02);
        } catch (Exception e) {
            ErrorUtil.reportError(this, e);
        }
    }

    public void stopAllUpdate() {
        try {
            this.myLocationManager.removeUpdates(this.mLocationListener01);
            this.myLocationManager.removeUpdates(this.mLocationListener02);
        } catch (Exception e) {
            ErrorUtil.reportError(this, e);
        }
    }

    private void OnGPSChange(Location location) {
        if (location != null) {
            try {
                if (location.getLatitude() != 0.0d && location.getLongitude() != 0.0d) {
                    this.isGPSDone = true;
                    this.isNetworkDone = true;
                    stopAllUpdate();
                }
            } catch (Exception e) {
                ErrorUtil.reportError(this, e);
            }
        }
    }

    private void OnNetworkChange(Location location) {
        if (location != null) {
            try {
                if (location.getLatitude() != 0.0d && location.getLongitude() != 0.0d) {
                    this.isNetworkDone = true;
                    this.myLocationManager.removeUpdates(this.mLocationListener02);
                }
            } catch (Exception e) {
                ErrorUtil.reportError(this, e);
            }
        }
    }

    private Location getCurrentLocation() {
        String str = "passive";
        Location retLocation = null;
        try {
            if (this.isGPSDone && this.isNetworkDone) {
                this.locationGPS = this.myLocationManager.getLastKnownLocation("gps");
                this.locationNetwork = this.myLocationManager.getLastKnownLocation("network");
                if (this.locationGPS == null && this.locationNetwork == null) {
                    retLocation = this.myLocationManager.getLastKnownLocation("passive");
                    if (retLocation == null) {
                        retLocation = new Location("passive");
                    }
                } else if (LocationUtils.isBetterLocation(this.locationGPS, this.locationNetwork)) {
                    retLocation = this.locationGPS;
                } else {
                    retLocation = this.locationNetwork;
                }
                stopAllUpdate();
            }
        } catch (Exception e) {
            ErrorUtil.reportError(this, e);
        }
        return retLocation;
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (this.gestureDetector.onTouchEvent(event)) {
            return true;
        }
        return false;
    }

    public void alert(String msg) {
        new Builder(this).setMessage(msg).setCancelable(false).setPositiveButton("Ok", new C00119()).show();
    }

    public void alertNoServices() {
        try {
            new Builder(this).setMessage(getString(C0012R.string.no_services)).setCancelable(false).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                }
            }).show();
        } catch (Exception e) {
            toast(getString(C0012R.string.no_services));
        }
    }

    public void debug(String msg) {
        Log.d("MoonShine", msg);
    }

    public void toast(String text) {
        try {
            Toast.makeText(this, text, MENU_TODAY).show();
        } catch (Exception e) {
        }
    }

    public void toast(Context context, String text) {
        try {
            Toast.makeText(context, text, MENU_TODAY).show();
        } catch (Exception e) {
        }
    }
}
