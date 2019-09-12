package com.sourceartists.remotecontrol.driver;

import com.sourceartists.remotecontrol.exception.MovementSpottedExcpetion;
import com.sourceartists.remotecontrol.model.SecurityStats;

import java.time.LocalDateTime;

public interface SecurityDriver {
    void startMovementRecording(Integer minutes);

    boolean shouldStopRecording(LocalDateTime now);

    void checkForMovement() throws MovementSpottedExcpetion;

    SecurityStats getLastMonthStats();
}
