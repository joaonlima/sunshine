package com.jll.sunshine;


import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jll.sunshine.data.WeatherContract;

public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

        private static int DETAIL_LOADER_ID = 1;

        public static final String[] FORECAST_COLUMNS = {
                WeatherContract.WeatherEntry.TABLE_NAME + "." + WeatherContract.WeatherEntry._ID,
                WeatherContract.WeatherEntry.COLUMN_DATE,
                WeatherContract.WeatherEntry.COLUMN_SHORT_DESC,
                WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
                WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
                WeatherContract.WeatherEntry.COLUMN_WEATHER_ID,
                WeatherContract.WeatherEntry.COLUMN_HUMIDITY,
                WeatherContract.WeatherEntry.COLUMN_WIND_SPEED,
                WeatherContract.WeatherEntry.COLUMN_PRESSURE,
                WeatherContract.WeatherEntry.COLUMN_DEGREES
        };

        // These indices are tied to FORECAST_COLUMNS.  If FORECAST_COLUMNS changes, these
        // must change.
        public static final int COL_WEATHER_ID = 0;
        public static final int COL_WEATHER_DATE = 1;
        public static final int COL_WEATHER_DESC = 2;
        public static final int COL_WEATHER_MAX_TEMP = 3;
        public static final int COL_WEATHER_MIN_TEMP = 4;
        public static final int COL_WEATHER_CONDITION_ID = 5;
        public static final int COL_WEATHER_HUMIDITY = 6;
        public static final int COL_WEATHER_WIND = 7;
        public static final int COL_WEATHER_PRESSURE = 8;
        public static final int COL_WEATHER_DEGREES = 9;

        private static final String LOG_TAG = "DetailFragment";
        private static final String FORECAST_SHARE_HASHTAG = " #SunshineApp";
        private ShareActionProvider shareActionProvider;
        private Uri forecastUri;
        private String shareForecastStr;


        private TextView simpleDateView;
        private TextView fullDateView;
        private TextView highTempView;
        private TextView lowTempView;
        private ImageView iconView;
        private TextView forecastView;
        private TextView humidityView;
        private TextView windView;
        private TextView pressureView;


        public DetailFragment() {
            setHasOptionsMenu(true);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

            simpleDateView = (TextView) rootView.findViewById(R.id.detail_simple_date_textview);
            fullDateView = (TextView) rootView.findViewById(R.id.detail_full_date_textview);
            highTempView = (TextView) rootView.findViewById(R.id.detail_high_textview);
            lowTempView = (TextView) rootView.findViewById(R.id.detail_low_textview);
            iconView = (ImageView) rootView.findViewById(R.id.detail_icon);
            forecastView = (TextView) rootView.findViewById(R.id.detail_forecast_textview);
            humidityView = (TextView) rootView.findViewById(R.id.detail_humidity_textview);
            windView = (TextView) rootView.findViewById(R.id.detail_wind_textview);
            pressureView = (TextView) rootView.findViewById(R.id.detail_pressure_textview);

            Intent intent = getActivity().getIntent();
            if(intent != null) {
                forecastUri = intent.getData();
                getActivity().getSupportLoaderManager().initLoader(DETAIL_LOADER_ID, null, this);
            } else {
                forecastUri = null;
            }

            return rootView;
        }

        @Override
        public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
            super.onCreateOptionsMenu(menu, inflater);


            // Inflate the menu; this adds items to the action bar if it is present.
            inflater.inflate(R.menu.menu_detail_fragment, menu);

            MenuItem item = menu.findItem(R.id.action_share);
            shareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);

            if (shareActionProvider != null && shareForecastStr != null && !shareForecastStr.isEmpty()) {
                shareActionProvider.setShareIntent(createShareForecastIntent());
            } else {
                Log.d(LOG_TAG, "Share Action Provider is null?");
            }

        }

        private Intent createShareForecastIntent() {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT,
                    shareForecastStr + FORECAST_SHARE_HASHTAG);
            return shareIntent;
        }

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            if(forecastUri == null) {
                return null;
            }

            return new CursorLoader(getActivity(), forecastUri,
                    FORECAST_COLUMNS,
                    null,
                    null,
                    null);
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
            if(cursor.moveToFirst()) {
                shareForecastStr = convertCursorRowToUXFormat(cursor);

                Context context = loader.getContext();


                boolean isMetric = Utility.isMetric(context);


                simpleDateView.setText(Utility.getDayName(context, cursor.getLong(COL_WEATHER_DATE)));
                fullDateView.setText(Utility.getFormattedMonthDay(context, cursor.getLong(COL_WEATHER_DATE)));;

                highTempView.setText(Utility.formatTemperature(
                        context, cursor.getDouble(COL_WEATHER_MAX_TEMP), isMetric));
                lowTempView.setText(Utility.formatTemperature(
                        context, cursor.getDouble(COL_WEATHER_MIN_TEMP), isMetric));

                // Read weather icon ID from cursor
                int weatherId = cursor.getInt(COL_WEATHER_CONDITION_ID);
                // FIXME Use placeholder image for now
                iconView.setImageResource(Utility.getArtResourceForWeatherCondition(weatherId));

                forecastView.setText(cursor.getString(COL_WEATHER_DESC));
                humidityView.setText(getString(R.string.format_humidity, cursor.getFloat(COL_WEATHER_HUMIDITY)));
                windView.setText(Utility.getFormattedWind(context, cursor.getFloat(COL_WEATHER_WIND), cursor.getFloat(COL_WEATHER_DEGREES)));
                pressureView.setText(getString(R.string.format_pressure, cursor.getFloat(COL_WEATHER_PRESSURE)));

                if (shareActionProvider != null ) {
                    shareActionProvider.setShareIntent(createShareForecastIntent());
                }
            }


        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
        }

        /**
         * Prepare the weather high/lows for presentation.
         */
        private String formatHighLows(double high, double low) {
            boolean isMetric = Utility.isMetric(getActivity());
            String highLowStr = Utility.formatTemperature(getActivity(), high, isMetric)
                    + "/"
                    + Utility.formatTemperature(getActivity(), low, isMetric);
            return highLowStr;
        }

        /*
            This is ported from FetchWeatherTask --- but now we go straight from the cursor to the
            string.
         */
        private String convertCursorRowToUXFormat(Cursor cursor) {
            String highAndLow = formatHighLows(
                    cursor.getDouble(COL_WEATHER_MAX_TEMP),
                    cursor.getDouble(COL_WEATHER_MIN_TEMP));

            return Utility.formatDate(cursor.getLong(COL_WEATHER_DATE)) +
                    " - " + cursor.getString(COL_WEATHER_DESC) +
                    " - " + highAndLow;
        }


    }