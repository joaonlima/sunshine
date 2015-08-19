package com.jll.sunshine;

import java.util.HashMap;
import java.util.Map;

public enum Unit {

    CELSIUS {
        @Override
        double convertMetricUnit(double metric) {
            return metric;
        }
    }, FAHRENHEIT {
        @Override
        double convertMetricUnit(double metric) {
            return 1.8 * metric + 32;
        }
    };

    abstract double convertMetricUnit(double metric);

}
