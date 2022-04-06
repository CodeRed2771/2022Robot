package frc.robot;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.Shooter.ManualShotPreset;

public class AutoTarmacShoot3Vision extends AutoBaseClass {
    boolean fourBall;
	public void start(boolean ball) {
		super.start();
        fourBall = ball;
	}
    
    public void stop() {
        super.stop();
    }

    @Override
    public void tick() {
        if (isRunning()) {
            SmartDashboard.putNumber("Auto Step", getCurrentStep());
            switch (getCurrentStep()) {
                case 0://step 1
                    VisionShooter.setLED(true);
                    Intake.startIntake();
                    Shooter.StartShooter();
                    Shooter.setManualPresets(ManualShotPreset.TarmacLine);
                    driveInches(55, 0, .4); // drive slowly toward 2nd ball
                    setTimerAndAdvanceStep(5000);
                    break;
                case 1:
                    if(driveCompleted())
                        advanceStep();
                    break;
                case 2://step 2
                    driveInches(-15, 0, .4); // drive back up to the line
                    setTimerAndAdvanceStep(2000);
                    break;
                case 3:
                    if(driveCompleted())
                        advanceStep();
                    break;
                case 4:
                    Shooter.alignAndShoot(true);
                    setTimerAndAdvanceStep(3000);
                    break;
                case 5:
                    break;
                case 6: 
                    Shooter.oneShotAuto();
                    setTimerAndAdvanceStep(1000);
                    break;
                case 7: 
                    break;
                case 8:
                    if (true) {
                       double angle = 162- RobotGyro.getRelativeAngle(); 
                       turnDegrees(angle, 0.8);
                       setTimerAndAdvanceStep(4000);
                    } else {
                        setStep(10);
                    }
                    break;
                case 9:
                    if(turnCompleted()) {
                        advanceStep();
                    }
                    break;
                case 10:
                    driveInches(200, 0, 0.8);
                    setTimerAndAdvanceStep(5000);
                    break;
                case 11:
                    if (driveCompleted()) {
                        advanceStep();
                    }
                    break;
                case 12:
                    Shooter.setManualPresets(ManualShotPreset.TarmacLine);
                    if (fourBall) {
                        setTimerAndAdvanceStep(3000);
                    } else {
                        setStep(14);
                    }
                    break;
                case 13:
                    break;
                case 14:
                    driveInches(-168, 0, 1);
                    setTimerAndAdvanceStep(3000);
                    break;
                case 15:
                    if(driveCompleted()) {
                        advanceStep();
                    }
                    break;
                case 16:
                    Shooter.alignAndShoot(true);
                    setTimerAndAdvanceStep(2000);
                    break;
                case 17:
                    break;
                case 18:
                    Shooter.oneShotAuto();
                    setTimerAndAdvanceStep(1000);
                    break;
                case 19:
                    break;
                case 20:
                    Intake.stopIntake();
                    stop();
                    break;
            } 
        }
    }
}