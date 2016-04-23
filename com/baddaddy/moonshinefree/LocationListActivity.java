package com.baddaddy.moonshinefree;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

public class LocationListActivity extends ListActivity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public boolean onContextItemSelected(MenuItem item) {
        return super.onContextItemSelected(item);
    }

    protected void onListItemClick(ListView l, View v, int position, long id) {
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
    }
}
