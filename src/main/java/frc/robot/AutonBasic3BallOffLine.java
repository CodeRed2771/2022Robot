/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class AutonBasic3BallOffLine extends AutoBaseClass {

    private AutoAlign mAutoAlign = new AutoAlign();

    public void start(char robotPosition) {
        super.start(robotPosition);
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
                Shooter.StartShooter();
                ShooterPivoter.shootOnInitiationLine();
                Shooter.closeGate();
                if (robotPosition() == Position.LEFT) {
                    DriveAuto.turnDegrees(15, 1);
                    setTimerAndAdvanceStep(1000);
                } else if (robotPosition() == Position.CENTER) {
                    setStep(2);
                } else if (robotPosition() == Position.RIGHT) {
                    DriveAuto.turnDegrees(-15, 1);
                    setTimerAndAdvanceStep(1000);
                } else {
                    System.out.println("AUTON NOT RUNNING");
                    stop();
                }

                break;
            case 1:
                if (driveCompleted()) {
                    advanceStep();
                } 
                break;
            case 2:
                mAutoAlign.start();
                advanceStep();
                break;
            case 3:
                mAutoAlign.tick();
                if (mAutoAlign.wasAligned() && Shooter.isAtSpeed()) {
                    advanceStep();
                }
                break;
            case 4:
                Shooter.openGate();
                setTimerAndAdvanceStep(3000);
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
