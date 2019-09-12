package com.sourceartists.remotecontrol.service;

import com.sourceartists.remotecontrol.model.RobotCleanerStatistics;

import java.time.LocalDateTime;

public interface AuditService {
    void reportNoAllRoomsCleaned(RobotCleanerStatistics lastCleanStatistics);

    void logMovement(LocalDateTime now);
}
