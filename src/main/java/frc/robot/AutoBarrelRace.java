/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class AutoBarrelRace extends AutoBaseClass {

    public void start() {
        super.start();
    }

    public void stop() {
        super.stop();
    }

    @Override
    public void tick() {
        if (isRunning()) {
            Vision.setTargetForShooting();
            DriveAuto.tick();
            SmartDashboard.putNumber("Auto Step", getCurrentStep());

            switch (getCurrentStep()) {
                case 0://segment 1 
                    driveInches(10, 0, 1);
                    setTimerAndAdvanceStep(2000);
                    break;
                case 1:
                    if (driveCompleted()) {
                        advanceStep();
                    } 
                    break;
                case 2://segment 2
                   driveInches( 170, 90, 1);
                    setTimerAndAdvanceStep(2000);
                    break;
                case 3:
                    if (driveCompleted()) {
                        advanceStep();
                    } 
                    break;
                case 4://segment 3 
                    driveInches( -30, 0, 1);
                     setTimerAndAdvanceStep(2000);
                     break;
                case 5:
                     if (driveCompleted()) {
                         advanceStep();
                     } 
                     break;
                case 6://segment 4 
                    driveInches( -30, 90, 1);
                    setTimerAndAdvanceStep(2000);
                    break;
                case 7:
                    if (driveCompleted()) {
                        advanceStep();
                    }
                    break;
                case 8://segment 5 
                    driveInches( 30, 0, 1);
                    setTimerAndAdvanceStep(2000);
                    break;
                case 9:
                    if (driveCompleted()) {
                        advanceStep();
                    }
                    break;
                case 10://segment 6 
                    driveInches( 170, 90, 1);
                    setTimerAndAdvanceStep(2000);
                    break;
                case 11:
                    if (driveCompleted()) {
                        advanceStep();
                    }
                    break;
                case 12://segment 7
                    driveInches( 50, 0, 1);
                    setTimerAndAdvanceStep(2000);
                    break;
                case 13:
                    if (driveCompleted()) {
                        advanceStep();
                    }
                    break;
                case 14://segment 8
                    driveInches( -40, 90, 1);
                    setTimerAndAdvanceStep(2000);
                    break;
                case 15:
                    if (driveCompleted()) {
                        advanceStep();
                    }
                    break;
                case 16://segment 9 
                    driveInches(  -60, 0, 1);
                    setTimerAndAdvanceStep(2000);
                    break;
                case 17:
                    if (driveCompleted()) {
                        advanceStep();
                    }
                    break;
                case 18://segment 10 
                    driveInches( -40, 90, 1);
                    setTimerAndAdvanceStep(2000);
                    break;
                case 19:
                    if (driveCompleted()) {
                        advanceStep();
                    }
                    break;
                case 20://segment 11
                    driveInches( 45, 0, 1);
                    setTimerAndAdvanceStep(2000);
                    break;
                case 21:
                    if (driveCompleted()) {
                        advanceStep();
                    }
                    break;
                case 22:
                    driveInches(165, 90, 1);
                    break;
                case 23:
                    if (driveCompleted()){
                        advanceStep();
                    }
                case 24:
                    stop();
                    break;
            } 
        }
    }
}
