package com.jll.sunshine;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.jll.sunshine.data.WeatherContract;

public class ForecastFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int FORECAST_LOADER_ID = 0;

    private ForecastAdapter forecastAdapter;

    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */
    public interface Callback {
        /**
         * DetailFragmentCallback for when an item has been selected.
         */
        public void onItemSelected(Uri dateUri);
    }

    public ForecastFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.forecastfragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        int id = item.getItemId();
        if(id == R.id.action_refresh) {
            updateWeather();
            return true;
        }

        return super.onOptionsItemSelected(item);

    }

    void onLocationChanged() {
        updateWeather();
        getActivity().getSupportLoaderManager().restartLoader(FORECAST_LOADER_ID, null, this);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().getSupportLoaderManager().initLoader(FORECAST_LOADER_ID, null, this);

    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        Context context = getActivity();
        String locationSetting = Utility.getPreferredLocation(context);
        Uri queryUri = WeatherContract.WeatherEntry.buildWeatherLocationWithStartDate(locationSetting, System.currentTimeMillis());

        return new CursorLoader(context, queryUri, WeatherContract.ForecastProjection.FORECAST_COLUMNS, null, null,
                WeatherContract.WeatherEntry.COLUMN_DATE + " ASC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        forecastAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        forecastAdapter.swapCursor(null);

    }

    private void updateWeather() {
        String postalCode = PreferenceManager
                .getDefaultSharedPreferences(getActivity())
                .getString(getString(R.string.pref_location_key), getString(R.string.pref_location_default));

        FetchWeatherTask fetcher = new FetchWeatherTask(this.getActivity());
        fetcher.execute(postalCode);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);


        Context context = this.getActivity();
        String locationSetting = Utility.getPreferredLocation(context);

        Uri queryUri = WeatherContract.WeatherEntry.buildWeatherLocationWithStartDate(locationSetting, System.currentTimeMillis());

        Cursor cursor = context.getContentResolver().query(
                queryUri,
                null,
                null,
                null,
                WeatherContract.WeatherEntry.COLUMN_DATE + " ASC"

        );

        forecastAdapter = new ForecastAdapter(context, cursor, 0);

        ListView forecastListView = (ListView) rootView.findViewById(R.id.listview_forecast);
        forecastListView.setAdapter(forecastAdapter);
        forecastListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Cursor itemCursor = (Cursor) parent.getItemAtPosition(position);



                if (itemCursor != null) {
                    Context context = getActivity();

                    String locationSetting = Utility.getPreferredLocation(context);
                    Callback callback = (Callback) getActivity();
                    callback.onItemSelected(WeatherContract.WeatherEntry.buildWeatherLocationWithDate(locationSetting,
                            itemCursor.getLong(WeatherContract.ForecastProjection.COL_WEATHER_DATE)));


                }

            }
        });


        return rootView;
    }
}
