/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class bluepathAutonSkillstwo extends AutoBaseClass {

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
            SmartDashboard.putNumber("3 ball Auto Step", getCurrentStep());
            SmartDashboard.putBoolean("Vision On Target", Vision.onTarget());
            SmartDashboard.putBoolean("Shooter At Speed", Shooter.isAtSpeed());

            switch (getCurrentStep()) {
            case 0:
                DriveAuto.driveInches(150,0, 1);
                setTimerAndAdvanceStep(2000);
                break;
            case 1:
                if (driveCompleted()) {
                    advanceStep();
                } 
                break;
            case 2:
                DriveAuto.driveInches(60, 45, 1);
                setTimerAndAdvanceStep(2000);
                break;
            case 3:
            if (driveCompleted()) {
                advanceStep();
            } 
                break;
            case 4:
            DriveAuto.driveInches(60, -90, 1);
            setTimerAndAdvanceStep(2000);
                break;
            case 5:
            DriveAuto.driveInches(30, 0, 1);
            setTimerAndAdvanceStep(2000);
                break;
            case 6:
            if (driveCompleted()) {
                advanceStep();
            }
                break;
            case 7:
            stop();
                break;
            case 8:
               
                break;
            case 9:
              
                break;
            }
        }
    }
}
