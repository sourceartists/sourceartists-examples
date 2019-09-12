package com.sourceartists.remotecontrol.driver.impl;

import com.sourceartists.remotecontrol.driver.RobotCleanerDriver;
import com.sourceartists.remotecontrol.model.RobotCleanerStatistics;
import com.sourceartists.remotecontrol.model.RobotStats;
import org.springframework.stereotype.Component;

@Component
public class BasicRobotCleanerDriver implements RobotCleanerDriver {
    @Override
    public void returnToDockingStation() {

    }

    @Override
    public void emptyBin() {

    }

    @Override
    public boolean isFull() {
        return false;
    }

    @Override
    public void startCleaning() {

    }

    @Override
    public void loadGeneralProgram() {

    }

    @Override
    public boolean allRoomsAccessed() {
        return false;
    }

    @Override
    public RobotCleanerStatistics getLastCleanStatistics() {
        return null;
    }

    @Override
    public RobotStats getLastMonthStats() {
        return null;
    }
}
