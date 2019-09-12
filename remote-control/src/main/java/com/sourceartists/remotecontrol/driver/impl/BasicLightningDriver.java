package com.sourceartists.remotecontrol.driver.impl;

import com.sourceartists.remotecontrol.driver.LightningDriver;
import com.sourceartists.remotecontrol.model.LightningStats;
import org.springframework.stereotype.Component;

@Component
public class BasicLightningDriver implements LightningDriver {
    @Override
    public LightningStats getLastMonthStats() {
        return null;
    }
}
