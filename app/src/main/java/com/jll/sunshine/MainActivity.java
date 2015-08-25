package com.jll.sunshine;

import android.content.Intent;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;


public class MainActivity extends ActionBarActivity {

    private static final String FORECASTFRAGMENT_TAG = "FORECASTFRAGMENT_TAG";

    private String mLocation;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLocation = PreferenceManager.getDefaultSharedPreferences(this)
                .getString(getString(R.string.pref_location_key), getString(R.string.pref_location_default));

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new ForecastFragment(), FORECASTFRAGMENT_TAG)
                    .commit();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        String currentLocation = Utility.getPreferredLocation(this);
        boolean locationHasChanged = currentLocation != null && !currentLocation.equals(mLocation);
        if(locationHasChanged) {
            ForecastFragment ff = (ForecastFragment)getSupportFragmentManager().findFragmentByTag(FORECASTFRAGMENT_TAG);
            if (ff != null) {
                ff.onLocationChanged();
            }

            mLocation = Utility.getPreferredLocation(this);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {

            Intent settingsStart = new Intent(this, SettingsActivity.class);
            startActivity(settingsStart);

            return true;
        } else if(id == R.id.action_view_location) {

            Intent viewLocation = new Intent(Intent.ACTION_VIEW);
            String location = PreferenceManager.getDefaultSharedPreferences(this)
                    .getString(getString(R.string.pref_location_key), getString(R.string.pref_location_default));
            Uri locationUri = Uri.parse("geo:0,0?").buildUpon().appendQueryParameter("q", location).build();
            viewLocation.setData(locationUri);

            if (viewLocation.resolveActivity(getPackageManager()) != null) {
                startActivity(viewLocation);
            }

            return true;

        }

        return super.onOptionsItemSelected(item);
    }

}
