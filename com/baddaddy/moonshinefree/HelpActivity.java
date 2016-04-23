package com.baddaddy.moonshinefree;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class HelpActivity extends Activity {
    private TextView forecast;

    public HelpActivity() {
        this.forecast = null;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(C0012R.layout.help);
    }
}
