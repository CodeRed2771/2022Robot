/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class AutonSkillsOne extends AutoBaseClass {

    private AutoAlign mAutoAlign = new AutoAlign();

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
            SmartDashboard.putNumber("Skill One Auto Step", getCurrentStep());
         
            switch (getCurrentStep()) {
            case 0:
                DriveAuto.driveInches(24,45, 1);
                setTimerAndAdvanceStep(2000);
                break;
            case 1:
                if (driveCompleted()) {
                    advanceStep();
                } 
                break;
            case 2:
                
                advanceStep();
                break;
            case 3:
               
                break;
            case 4:
                
                break;
            case 5:
                
                break;
            case 6:
                Shooter.StopShooter();
                advanceStep();
                break;
            case 7:
                DriveAuto.driveInches(36, 180, 1, false, true);
                setTimerAndAdvanceStep(3000);
                break;
            case 8:
                if (driveCompleted()) {
                    advanceStep();
                }
                break;
            case 9:
                stop();
                break;
            }
        }
    }
}
