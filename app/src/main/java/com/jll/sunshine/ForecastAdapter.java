package com.jll.sunshine;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jll.sunshine.data.WeatherContract;

import java.util.HashMap;
import java.util.Map;


/**
 * {@link ForecastAdapter} exposes a list of weather forecasts
 * from a {@link android.database.Cursor} to a {@link android.widget.ListView}.
 */
public class ForecastAdapter extends CursorAdapter {

    private enum ForecastViewType {
        TODAY(0, R.layout.list_item_forecast_today), FUTURE_DAY(1, R.layout.list_item_forecast);


        private static final Map<Integer, ForecastViewType> fromId = new HashMap<>();
        static {
            for(ForecastViewType type : ForecastViewType.values()) {
                fromId.put(type.id, type);
            }
        }

        final int id;
        final int layoutId;


        ForecastViewType(int id, int layoutId) {
            this.id = id;
            this.layoutId = layoutId;
        }

        static ForecastViewType fromId(int id) {
            return fromId.get(id);
        }

    }

    private static class ViewHolder {
        public final ImageView iconView;
        public final TextView dateView;
        public final TextView descriptionView;
        public final TextView highTempView;
        public final TextView lowTempView;

        public ViewHolder(View view) {
            iconView = (ImageView) view.findViewById(R.id.list_item_icon);
            dateView = (TextView) view.findViewById(R.id.list_item_date_textview);
            descriptionView = (TextView) view.findViewById(R.id.list_item_forecast_textview);
            highTempView = (TextView) view.findViewById(R.id.list_item_high_textview);
            lowTempView = (TextView) view.findViewById(R.id.list_item_low_textview);
        }
    }

    public ForecastAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public int getItemViewType(int position) {
        return position == 0 ? ForecastViewType.TODAY.id : ForecastViewType.FUTURE_DAY.id;
    }

    @Override
    public int getViewTypeCount() {
        return ForecastViewType.values().length;
    }

    /*
            Remember that these views are reused as needed.
         */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        int viewTypeId = getItemViewType(cursor.getPosition());
        int layoutId = ForecastViewType.fromId(viewTypeId).layoutId;

        View view = LayoutInflater.from(context).inflate(layoutId, parent, false);
        view.setTag(new ViewHolder(view));

        return view;
    }

    /*
        This is where we fill-in the views with the contents of the cursor.
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // our view is pretty simple here --- just a text view
        // we'll keep the UI functional with a simple (and slow!) binding.

        ViewHolder viewHolder = (ViewHolder) view.getTag();

        viewHolder.dateView.setText(Utility.getFriendlyDayString(context, cursor.getLong(WeatherContract.ForecastProjection.COL_WEATHER_DATE)));
        viewHolder.descriptionView.setText(cursor.getString(WeatherContract.ForecastProjection.COL_WEATHER_DESC));

        boolean isMetric = Utility.isMetric(mContext);

        viewHolder.highTempView.setText(Utility.formatTemperature(cursor.getDouble(WeatherContract.ForecastProjection.COL_WEATHER_MAX_TEMP), isMetric));
        viewHolder.lowTempView.setText(Utility.formatTemperature(cursor.getDouble(WeatherContract.ForecastProjection.COL_WEATHER_MIN_TEMP), isMetric));


        // Read weather icon ID from cursor
        int weatherId = cursor.getInt(WeatherContract.ForecastProjection.COL_WEATHER_ID);
        // FIXME Use placeholder image for now

        viewHolder.iconView.setImageResource(R.drawable.ic_launcher);



    }
}