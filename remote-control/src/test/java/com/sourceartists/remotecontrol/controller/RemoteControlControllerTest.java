package com.sourceartists.remotecontrol.controller;

import com.sourceartists.remotecontrol.driver.HeatingDriver;
import com.sourceartists.remotecontrol.driver.LightningDriver;
import com.sourceartists.remotecontrol.driver.RobotCleanerDriver;
import com.sourceartists.remotecontrol.driver.SecurityDriver;
import com.sourceartists.remotecontrol.exception.MovementSpottedExcpetion;
import com.sourceartists.remotecontrol.model.*;
import com.sourceartists.remotecontrol.service.AuditService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.*;


class RemoteControlControllerTest {

    @Mock
    private RobotCleanerDriver robotCleanerDriverMock;

    @Mock
    private AuditService auditServiceMock;

    @Mock
    private SecurityDriver securityDriverMock;

    @Mock
    private LightningDriver lightningDriverMock;

    @Mock
    private HeatingDriver heatingDriverMock;

    @InjectMocks
    private RemoteControlController remoteControlControllerSUT;

    @BeforeEach
    private void init(){
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void shouldPerformCleaningWithoutInterruptions() throws Exception{
        // Given
        when(robotCleanerDriverMock.allRoomsAccessed()).thenReturn(true);
        when(robotCleanerDriverMock.isFull()).thenReturn(false);

        // When
        remoteControlControllerSUT.triggerGeneralCleaning();

        // Then
        verify(robotCleanerDriverMock).loadGeneralProgram();
        verify(robotCleanerDriverMock).startCleaning();
        verify(robotCleanerDriverMock).returnToDockingStation();
        verify(robotCleanerDriverMock).allRoomsAccessed();
        verify(robotCleanerDriverMock).isFull();

        verifyNoMoreInteractions(robotCleanerDriverMock, auditServiceMock);

    }

    @Test
    public void shouldNotRecordAnyMovement() throws Exception{
        // Given
        given(securityDriverMock.shouldStopRecording(any(LocalDateTime.class)))
                .willReturn(false)
                .willReturn(false)
                .willReturn(true);

        // When
        List<LocalDateTime> recordedMovement = remoteControlControllerSUT.recordMovement(1);

        // Then
        assertThat(recordedMovement).isEmpty();
    }

    @Test
    public void shouldRecordMovement() throws Exception{
        // Given
        given(securityDriverMock.shouldStopRecording(any(LocalDateTime.class)))
                .willReturn(false)
                .willReturn(false)
                .willReturn(true);

        willThrow(new MovementSpottedExcpetion())
                .given(securityDriverMock).checkForMovement();

        // When
        List<LocalDateTime> recordedMovement = remoteControlControllerSUT.recordMovement(1);

        // Then
        assertThat(recordedMovement).hasSize(2);

        then(auditServiceMock).should(times(2))
                .logMovement(any(LocalDateTime.class));
    }

    @Spy
    @InjectMocks
    private RemoteControlController remoteControlControllerSpy;

    @Test
    public void shouldGenerateHouseReport() throws Exception{
        // Given
        SecurityStats securityStats = new SecurityStats();
        RobotStats robotStats = new RobotStats();
        HeatingStats heatingStats = new HeatingStats();
        LightningStats lightningStats = new LightningStats();

        given(securityDriverMock.getLastMonthStats()).willReturn(securityStats);
        given(lightningDriverMock.getLastMonthStats()).willReturn(lightningStats);
        given(heatingDriverMock.getLastMonthStats()).willReturn(heatingStats);
        given(robotCleanerDriverMock.getLastMonthStats()).willReturn(robotStats);

        willReturn(new HouseStats()).given(remoteControlControllerSpy).assembleHouseStats(
                securityStats, robotStats, heatingStats, lightningStats);

        // When
        HouseStats houseStats = remoteControlControllerSpy.generateHouseReport();

        // Then
        then(heatingDriverMock).should().getLastMonthStats();
        then(lightningDriverMock).should().getLastMonthStats();
        then(robotCleanerDriverMock).should().getLastMonthStats();
        then(securityDriverMock).should().getLastMonthStats();

        assertThat(houseStats).isNotNull();
    }
}