/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class AutonLeft6Balls extends AutoBaseClass{

    private AutoAlign mAutoAlign = new AutoAlign();
    private double turnAngle = 0;

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
                case 0:
                    mAutoAlign.start();
                    advanceStep();
                    break;
                case 1:
                    mAutoAlign.tick();
                    if (Vision.onTarget()) {
                        advanceStep();
                    }
                    break;
                case 2:
                    Shooter.StartShooter();
                    setTimerAndAdvanceStep(1500);
                    break;
                case 3:
                    // Shooter.tick();
                    break;
                case 4:
                    Shooter.StopShooter();
                    // Intake.moveIntakeDown();
                    // Intake.runIntakeForwards();
                    advanceStep();
                    break;
                case 5:
                    turnToHeading(67.5, 1);
                    setTimerAndAdvanceStep(2000);
                    break;
                case 6:
                    if (driveCompleted()) {
                        advanceStep();
                    }
                    break;
                case 7:
                    driveInches(100, 225, 1, false, true);
                    setTimerAndAdvanceStep(1500);
                    break;
                case 8:
                if (driveCompleted()) {
                    advanceStep();
                }
                    break;
                case 9:
                    driveInches(65, 180, 1, false, true);
                    setTimerAndAdvanceStep(1000);
                    break;
                case 10:
                    if (driveCompleted()) {
                        advanceStep();
                    }
                    break;
                case 11:
                    driveInches(140, 67.5, 1, false, true);
                    setTimerAndAdvanceStep(1750);
                    break;
                case 12:
                    Intake.moveIntakeUp();
                    Intake.stopIntake();
                    if (driveCompleted()) {
                        advanceStep();
                    }
                    break;
                case 13:
                    turnToHeading(30, 1);
                    setTimerAndAdvanceStep(1500);
                    break;
                case 14:
                    if (driveCompleted()) {
                        advanceStep();
                    }
                    break;
                case 15:
                    mAutoAlign.start();
                    advanceStep();
                    break;
                case 16:
                    mAutoAlign.tick();
                    if (Vision.onTarget()) {
                        advanceStep();
                    }
                    break;
                case 17:
                    Shooter.StartShooter();
                    setTimerAndAdvanceStep(2000);
                    break;
                case 18:
                    break;
                case 19:
                    Shooter.StopShooter();
                    stop();
                    break;
            }
        }
    }
}
