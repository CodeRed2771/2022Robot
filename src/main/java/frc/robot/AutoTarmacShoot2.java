/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class AutoTarmacShoot2 extends AutoBaseClass {
    public void start() {
        super.start();
    }

    public void stop() {
        super.stop();
    }
    //AutoTarmacShoot2
    @Override
    public void tick() {
        if (isRunning()) {
            Vision.setTargetForShooting();
            DriveAuto.tick();
            SmartDashboard.putNumber("Auto Step", getCurrentStep());
            
            switch (getCurrentStep()) {
                case 0://segment 1 
                    driveInches(75, 0, -0.5);
                    setTimerAndAdvanceStep(2000);
                    break;
                case 1:
                    if (driveCompleted()) {
                        advanceStep();
                    } 
                    break;
                case 2://segment 2
                    turnDegrees(180, 0.5);
                    setTimerAndAdvanceStep(1500);
                    break;
                case 3:
                    if(turnCompleted()) {
                        advanceStep();
                    }
                    break;
                case 4://segment 3
                    Shooter.StartShooter();
                    setTimerAndAdvanceStep(2000);
                    break;
                case 5://segment 4
                    Shooter.StopShooter();
                    advanceStep();
                    break;
                case 6:
                    driveInches(VisionBall.distanceToBall(), 90, 0.5);
                    setTimerAndAdvanceStep(5000);
                    break;
                case 7:
                    if(driveCompleted()) {
                        advanceStep();
                    }
                    break;
                case 8:
                    Intake.startIntake();
                    break;
                case 9:
                    driveInches(VisionBall.distanceToBall(), 0, 0.5);
                    setTimerAndAdvanceStep(2000);
                    break;
                case 10:
                    if(driveCompleted())
                        advanceStep();
                    break;
                case 11:
                    Intake.stopIntake();
                    break;
                case 12://segment 3
                    Shooter.StartShooter();
                    setTimerAndAdvanceStep(2000);
                    break;
                case 13://segment 4
                    Shooter.StopShooter();
                    advanceStep();
                    break;  
                case 14://stop
                    stop();
                    break;
            } 
        }
    }
}
