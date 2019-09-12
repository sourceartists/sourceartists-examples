package com.sourceartists.remotecontrol.controller;

import com.sourceartists.remotecontrol.driver.*;
import com.sourceartists.remotecontrol.exception.MovementSpottedExcpetion;
import com.sourceartists.remotecontrol.model.*;
import com.sourceartists.remotecontrol.service.AuditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
public class RemoteControlController {

    @Autowired
    private RobotCleanerDriver robotCleanerDriver;
    @Autowired
    private HeatingDriver heatingDriver;
    @Autowired
    private LightningDriver lightningDriver;
    @Autowired
    private SecurityDriver securityDriver;
    @Autowired
    private AuditService auditService;

    public void triggerGeneralCleaning(){
        robotCleanerDriver.loadGeneralProgram();
        robotCleanerDriver.startCleaning();

        if(robotCleanerDriver.isFull()){
            robotCleanerDriver.emptyBin();
        }
        if(!robotCleanerDriver.allRoomsAccessed()){
            auditService.reportNoAllRoomsCleaned(
                    robotCleanerDriver.getLastCleanStatistics());
        }

        robotCleanerDriver.returnToDockingStation();
    }

    public void lockTheHouse(){

//        lightningDriver.turnOnNightLightsOutside();
    }

    public HouseStats generateHouseReport(){
        SecurityStats securityStats = securityDriver.getLastMonthStats();
        RobotStats robotStats = robotCleanerDriver.getLastMonthStats();
        HeatingStats heatingStats = heatingDriver.getLastMonthStats();
        LightningStats lightningStats = lightningDriver.getLastMonthStats();

        return assembleHouseStats(securityStats, robotStats, heatingStats, lightningStats);
    }

    protected HouseStats assembleHouseStats(SecurityStats securityStats
            , RobotStats robotStats
            , HeatingStats heatingStats
            , LightningStats lightningStats) {
        return null;
    }

    public List<LocalDateTime> recordMovement(Integer minutes){
        List<LocalDateTime> movements = new ArrayList<>();

        securityDriver.startMovementRecording(minutes);

        while(!securityDriver.shouldStopRecording(LocalDateTime.now())){
            try {
                securityDriver.checkForMovement();
            } catch (MovementSpottedExcpetion e){
                movements.add(LocalDateTime.now());
                auditService.logMovement(LocalDateTime.now());
            }
        }

        return movements;
    }

    public void turnOnTheLights(){

    }
}
