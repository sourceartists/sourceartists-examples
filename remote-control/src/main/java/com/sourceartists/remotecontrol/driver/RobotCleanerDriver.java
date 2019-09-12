package com.sourceartists.remotecontrol.driver;

import com.sourceartists.remotecontrol.model.RobotCleanerStatistics;
import com.sourceartists.remotecontrol.model.RobotStats;

public interface RobotCleanerDriver {
    void returnToDockingStation();

    void emptyBin();

    boolean isFull();

    void startCleaning();

    void loadGeneralProgram();

    boolean allRoomsAccessed();

    RobotCleanerStatistics getLastCleanStatistics();

    RobotStats getLastMonthStats();
}
