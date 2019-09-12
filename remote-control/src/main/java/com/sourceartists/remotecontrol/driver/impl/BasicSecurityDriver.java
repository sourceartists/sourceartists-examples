package com.sourceartists.remotecontrol.driver.impl;

import com.sourceartists.remotecontrol.driver.SecurityDriver;
import com.sourceartists.remotecontrol.exception.MovementSpottedExcpetion;
import com.sourceartists.remotecontrol.model.SecurityStats;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class BasicSecurityDriver implements SecurityDriver {
    @Override
    public void startMovementRecording(Integer minutes) {

    }

    @Override
    public boolean shouldStopRecording(LocalDateTime now) {
        return false;
    }

    @Override
    public void checkForMovement() throws MovementSpottedExcpetion {

    }

    @Override
    public SecurityStats getLastMonthStats() {
        return null;
    }
}
