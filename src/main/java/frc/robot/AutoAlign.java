package frc.robot;

import javax.lang.model.util.ElementScanner6;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

//The purpose of this class is to turn the robot until we are on target.

public class AutoAlign extends AutoBaseClass {

    private double angleOffset = 0;
    private boolean wasAligned = false;

    public AutoAlign () {
        
    }

    public void start() {
        super.start();
        wasAligned = false;
    }

    public void start(boolean autoShoot){
        super.start(autoShoot);
        wasAligned = false;
    }

    public void stop() {
        super.stop();
    }

    public boolean wasAligned() {
        return wasAligned;
    }

    @Override
    public void tick() {
        if (isRunning()) {
            DriveAuto.tick();
            SmartDashboard.putNumber("Auto Step", getCurrentStep());
            switch (getCurrentStep()) {
            case 0:
                Vision.setVisionTrackingMode();
                Vision.setTargetForShooting();
                Intake.moveIntakeDown();
                ShooterPivoter.setDesiredShootPosition(Vision.getShooterPivoterDesiredPosition());
                advanceStep();
                break;
            case 1:
                angleOffset = Vision.getDistanceAdjustedAngle();
                if (Vision.seesTarget()) {
                    advanceStep();
                }
                break;
            case 2:
                if (Math.abs(angleOffset) > 1) {
                    DriveAuto.turnDegrees(angleOffset, 1);
                    setTimerAndAdvanceStep(8500);
                } else {
                    setStep(5);
                }
                if (autoShoot()){
                    if (!Shooter.isShooterEnabled()) {
                        Shooter.StartShooter();
                    }
                }
                break;
            case 3:
                if (driveCompleted()) {
                    advanceStep();
                }
                break;
            case 4:
                angleOffset = Vision.getDistanceAdjustedAngle();
                // SmartDashboard.putNumber("Adj Angle Offset", angleOffset);
                // SmartDashboard.putNumber("Angle Offset", Vision.getAngleOffset());
                // SmartDashboard.putBoolean("Sees Target", Vision.seesTarget());
                if (Vision.onTarget()) {
                    advanceStep();
                } else {
                    setStep(1);
                }
                break;
            case 5:
                wasAligned = true;
                // Vision.flashLED();
                System.out.println("On Target!");
                advanceStep();
                break;
            case 6:
                if (autoShoot()){
                    Shooter.oneShot();
                }
                stop();
                break;
            //     ShooterPivoter.setDesiredShootPosition(Vision.getShooterPivoterDesiredShaftLocation());
            //     setTimerAndAdvanceStep(1000);
            //     break;
            // case 5:
            //     if (ShooterPivoter.shooterAtPosition()){
            //         advanceStep();
            //     }
            //     break;
            // case 6:
            //     stop();
            //     break;
            }
        }
    }
}