package frc.robot;

import javax.lang.model.util.ElementScanner6;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

//The purpose of this class is to turn the robot until we are on target.

public class AutoAlign extends AutoBaseClass {

    private double angleOffset = 0;
    private boolean wasAligned = false;
    private static boolean alingnedComplete = false;

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
    public static boolean getAllignment() {
        return alingnedComplete;
    }
    public static void setAllignment(boolean allignment) {
        alingnedComplete = allignment;
    }
    @Override
    public void tick() {
        if (isRunning()) {
            DriveAuto.tick();
            SmartDashboard.putNumber("Auto Step", getCurrentStep());
            switch (getCurrentStep()) {
            case 0:
                VisionShooter.setVisionTrackingMode();
                VisionShooter.setTargetForShooting();
                // Intake.moveIntakeDown();
                advanceStep();
                break;
            case 1:
                angleOffset = VisionShooter.getDistanceAdjustedAngle();
                if (VisionShooter.seesTarget()) {
                    advanceStep();
                }
                // SmartDashboard.putNumber("Adj Angle Offset", angleOffset);
                // SmartDashboard.putNumber("Angle Offset", Vision.getAngleOffset());
                // SmartDashboard.putBoolean("Sees Target", Vision.seesTarget());
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
                wasAligned = true;
                // Vision.flashLED();
                System.out.println("On Target!");
                advanceStep();
                break;
            case 5: 
                Shooter.setupShooterAuto();
                break;
            case 6:
                if (autoShoot()){
                    Shooter.oneShotAuto();
                }
                alingnedComplete = true;
                stop();
                break;
            }
        }
    }
}