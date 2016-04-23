package com.baddaddy.moonshinefree;

import android.app.AlertDialog.Builder;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.baddaddy.astronomy.AstroFormat;
import com.baddaddy.astronomy.MoonInfo;
import com.baddaddy.astronomy.MoonTimes;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MonthlyListActivity extends ListActivity {
    private static boolean PAID_VERSION;
    private double latitude;
    private double longitude;
    private int mMonth;
    private Button mMonthlyNextButton;
    private Button mMonthlyPrevButton;
    private int[] mPhotoIds;
    private int mYear;
    RowObject[] rows;
    private long tzOffset;

    /* renamed from: com.baddaddy.moonshinefree.MonthlyListActivity.1 */
    class C00001 implements OnClickListener {
        C00001() {
        }

        public void onClick(View v) {
            if (MonthlyListActivity.PAID_VERSION) {
                MonthlyListActivity.this.addMonth(-1);
            } else {
                MonthlyListActivity.this.alert("Please upgrade to paid version to view other months");
            }
        }
    }

    /* renamed from: com.baddaddy.moonshinefree.MonthlyListActivity.2 */
    class C00012 implements OnClickListener {
        C00012() {
        }

        public void onClick(View v) {
            if (MonthlyListActivity.PAID_VERSION) {
                MonthlyListActivity.this.addMonth(1);
            } else {
                MonthlyListActivity.this.alert("Please upgrade to paid version to view other months");
            }
        }
    }

    /* renamed from: com.baddaddy.moonshinefree.MonthlyListActivity.3 */
    class C00023 implements DialogInterface.OnClickListener {
        C00023() {
        }

        public void onClick(DialogInterface dialog, int id) {
            dialog.cancel();
        }
    }

    class IconicAdapter extends ArrayAdapter {
        TextView dayText;
        TextView fractionText;
        ImageView icon;
        TextView phaseText;
        TextView riseText;
        TextView setText;
        TextView weekdayText;

        IconicAdapter() {
            super(MonthlyListActivity.this, C0012R.layout.monthly_row, MonthlyListActivity.this.rows);
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            View row = MonthlyListActivity.this.getLayoutInflater().inflate(C0012R.layout.monthly_row, parent, false);
            this.dayText = (TextView) row.findViewById(C0012R.id.monthlyDay);
            this.weekdayText = (TextView) row.findViewById(C0012R.id.monthlyWeekday);
            this.phaseText = (TextView) row.findViewById(C0012R.id.monthlyPhase);
            this.riseText = (TextView) row.findViewById(C0012R.id.monthlyRise);
            this.setText = (TextView) row.findViewById(C0012R.id.monthlySet);
            this.icon = (ImageView) row.findViewById(C0012R.id.monthlyIcon);
            this.fractionText = (TextView) row.findViewById(C0012R.id.monthlyFraction);
            this.dayText.setText(MonthlyListActivity.this.rows[position].day);
            this.weekdayText.setText(MonthlyListActivity.this.rows[position].weekday);
            this.phaseText.setText(MonthlyListActivity.this.rows[position].phase);
            this.riseText.setText(MonthlyListActivity.this.rows[position].rise);
            this.setText.setText(MonthlyListActivity.this.rows[position].set);
            this.fractionText.setText(MonthlyListActivity.this.rows[position].fraction);
            this.icon.setImageResource(MonthlyListActivity.this.rows[position].icon);
            return row;
        }
    }

    class RowObject {
        String day;
        String fraction;
        int icon;
        String phase;
        String rise;
        String set;
        String weekday;

        RowObject() {
        }
    }

    public MonthlyListActivity() {
        this.mMonth = -1;
        this.mYear = -1;
        this.mPhotoIds = new int[]{C0012R.drawable.moonphase0, C0012R.drawable.moonphase1, C0012R.drawable.moonphase2, C0012R.drawable.moonphase3, C0012R.drawable.moonphase4, C0012R.drawable.moonphase5, C0012R.drawable.moonphase6, C0012R.drawable.moonphase7, C0012R.drawable.moonphase8, C0012R.drawable.moonphase9, C0012R.drawable.moonphase10, C0012R.drawable.moonphase11, C0012R.drawable.moonphase12, C0012R.drawable.moonphase13, C0012R.drawable.moonphase14, C0012R.drawable.moonphase15, C0012R.drawable.moonphase16, C0012R.drawable.moonphase17, C0012R.drawable.moonphase18, C0012R.drawable.moonphase19, C0012R.drawable.moonphase20, C0012R.drawable.moonphase21, C0012R.drawable.moonphase22, C0012R.drawable.moonphase23, C0012R.drawable.moonphase24, C0012R.drawable.moonphase25, C0012R.drawable.moonphase26, C0012R.drawable.moonphase27, C0012R.drawable.moonphase28, C0012R.drawable.moonphase29};
    }

    static {
        PAID_VERSION = true;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(C0012R.layout.monthly_list);
        if (getPackageName().equals("com.baddaddy.moonshine")) {
            PAID_VERSION = true;
        } else {
            PAID_VERSION = false;
            toast("Demo Mode");
        }
        Bundle bundle = getIntent().getExtras();
        this.mMonth = bundle.getInt("month");
        this.mYear = bundle.getInt("year");
        this.latitude = bundle.getDouble(LocationDbAdapter.KEY_LATITUDE);
        this.longitude = bundle.getDouble(LocationDbAdapter.KEY_LONGITUDE);
        this.tzOffset = bundle.getLong("timezoneOffset");
        this.mMonthlyPrevButton = (Button) findViewById(C0012R.id.monthlyPrevBtn);
        this.mMonthlyNextButton = (Button) findViewById(C0012R.id.monthlyNextBtn);
        this.mMonthlyPrevButton.setOnClickListener(new C00001());
        this.mMonthlyNextButton.setOnClickListener(new C00012());
        updateDisplay();
    }

    private void updateDisplay() {
        SimpleDateFormat monthYear = new SimpleDateFormat("MMMM yyyy");
        SimpleDateFormat weekdayFormat = new SimpleDateFormat("EEE");
        Calendar c = Calendar.getInstance();
        c.set(5, 1);
        c.set(11, 12);
        c.set(12, 0);
        c.set(13, 0);
        if (this.mMonth > -1 && this.mYear > -1) {
            c.set(2, this.mMonth);
            c.set(1, this.mYear);
        }
        setTitle("MoonShine for " + monthYear.format(c.getTime()));
        int days = c.getActualMaximum(5);
        this.rows = new RowObject[days];
        double[] timesArray = null;
        for (int i = 0; i < days; i++) {
            c.set(5, i + 1);
            MoonInfo moonInfo = new MoonInfo(c.getTime(), this.latitude, this.longitude);
            timesArray = MoonTimes.getTimes(c.getTime(), this.latitude, this.longitude, this.tzOffset);
            this.rows[i] = new RowObject();
            this.rows[i].weekday = weekdayFormat.format(c.getTime()).substring(0, 1);
            this.rows[i].day = String.valueOf(i + 1);
            this.rows[i].icon = this.mPhotoIds[moonInfo.getPhaseImageIndex()];
            this.rows[i].phase = moonInfo.getPhaseTextShort();
            this.rows[i].rise = AstroFormat.formatTime(DateFormat.getTimeFormat(this), timesArray[0]);
            this.rows[i].set = AstroFormat.formatTime(DateFormat.getTimeFormat(this), timesArray[1]);
            this.rows[i].fraction = String.valueOf(Math.round(moonInfo.getIlluminatedFraction() * 100.0d));
        }
        setListAdapter(new IconicAdapter());
    }

    private void addMonth(int months) {
        Calendar c = Calendar.getInstance();
        c.set(this.mYear, this.mMonth, 1);
        c.add(2, months);
        this.mYear = c.get(1);
        this.mMonth = c.get(2);
        updateDisplay();
    }

    public void onListItemClick(ListView parent, View v, int position, long id) {
        Bundle bundle = new Bundle();
        bundle.putInt("month", this.mMonth);
        bundle.putInt("day", position + 1);
        bundle.putInt("year", this.mYear);
        Intent intent = new Intent(this, MoonShine.class);
        intent.putExtras(bundle);
        setResult(-1, intent);
        finish();
    }

    public void alert(String msg) {
        new Builder(this).setMessage(msg).setCancelable(false).setPositiveButton("Ok", new C00023()).show();
    }

    public void toast(String text) {
        Toast.makeText(this, text, 1).show();
    }
}
