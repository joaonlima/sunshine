package com.jll.sunshine;

import android.content.Context;
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
import android.widget.ListView;

import com.jll.sunshine.data.WeatherContract;

public class ForecastFragment extends Fragment {

    private static final int FORECAST_LOADER_ID = 1;

    private ForecastAdapter forecastAdapter;

    public ForecastFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onStart() {
        super.onStart();
        updateWeather();
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

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);



        LoaderManager.LoaderCallbacks<Cursor> loaderCallback = new LoaderManager.LoaderCallbacks<Cursor>() {
            @Override
            public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

                Context context = getActivity();
                String locationSetting = Utility.getPreferredLocation(context);
                Uri queryUri = WeatherContract.WeatherEntry.buildWeatherLocationWithStartDate(locationSetting, System.currentTimeMillis());

                return new CursorLoader(context, queryUri, null, null, null,
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
        };
        getActivity().getSupportLoaderManager().initLoader(FORECAST_LOADER_ID, null, loaderCallback);

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


        return rootView;
    }
}
