package com.jll.sunshine;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.jll.sunshine.data.WeatherContract;


public class DetailActivity extends ActionBarActivity {

    private static final String DETAIL_FRAGMENT_ID = "DFID";
    private String mLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        if (savedInstanceState == null) {
            Uri detailUri = getIntent().getData();
            mLocation = WeatherContract.WeatherEntry.getLocationSettingFromUri(detailUri);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.weather_detail_container, DetailFragment.newInstance(detailUri), DETAIL_FRAGMENT_ID)
                    .commit();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();


        String currentLocation = Utility.getPreferredLocation(this);
        boolean locationHasChanged = currentLocation != null && !currentLocation.equals(mLocation);
        if(locationHasChanged) {
            DetailFragment df = (DetailFragment) getSupportFragmentManager().findFragmentById(R.id.weather_detail_container);
            if ( null != df ) {
                df.onLocationChanged(currentLocation);
            }

            mLocation = currentLocation;
        }
        
        
        
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detail, menu);

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
        }

        return super.onOptionsItemSelected(item);
    }

}
