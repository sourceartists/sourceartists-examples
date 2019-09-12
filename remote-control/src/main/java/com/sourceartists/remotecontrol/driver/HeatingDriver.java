package com.sourceartists.remotecontrol.driver;

import com.sourceartists.remotecontrol.model.HeatingStats;

public interface HeatingDriver {
    HeatingStats getLastMonthStats();
}
