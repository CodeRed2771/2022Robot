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
                    break;
                case 5:
                    Shooter.alignAndShoot(true);
                    setTimerAndAdvanceStep(3000);
                    break;
                case 6:
                    break;
                case 7: 
                    Shooter.oneShotAuto();
                    setTimerAndAdvanceStep(1000);
                    break;
                case 8: 
                    break;
                case 9: 
                    Intake.stopIntake();
                    if (DriverStation.getLocation() ==1) {
                        driveInches(10, 0, .8);
                        setTimerAndAdvanceStep(3000);
                    } else if (DriverStation.getLocation() ==2) {
                        driveInches(10, -10, .8);
                        setTimerAndAdvanceStep(3000);
                    } else {
                        driveInches(20, -20, .8);
                        setTimerAndAdvanceStep(3000);
                    }
                    break;
                case 10:
                    if(driveCompleted())
                        advanceStep();
                    break;
                case 11:
                    if (DriverStation.getLocation() ==1) {
                        driveInches(20, -10, .8);
                        setTimerAndAdvanceStep(3000);
                    } else {
                        setStep(13);
                    }
                case 12:
                    if (driveCompleted())
                        advanceStep();
                    break;
                case 13:
                    Intake.startIntake();
                    if (fourBall) {
                        setTimerAndAdvanceStep(4000);
                    } else {
                        setStep(15);
                    }
                    break;
                case 14: 
                    break;
                case 15:
                    Shooter.setManualPresets(ManualShotPreset.HumanPlayerStation);
                    if(VisionShooter.seeTarget() && DriverStation.getLocation() == 1) {
                        turnDegrees(50, .8);
                        setTimerAndAdvanceStep(3000);
                    } else if(VisionShooter.seeTarget() && DriverStation.getLocation() == 2) {
                        turnDegrees(20, .8);
                        setTimerAndAdvanceStep(2000);
                    } else if (VisionShooter.seeTarget() && DriverStation.getLocation() == 3) {
                        turnDegrees(-50, .8);
                        setTimerAndAdvanceStep(2000);
                    } else {
                        setStep(15);
                    }
                    break;
                case 16:
                    if(turnCompleted()) {
                        advanceStep();
                    } 
                    break;
                case 17:
                    Shooter.alignAndShoot(true);
                    setTimerAndAdvanceStep(3000);
                    break;
                case 18:
                    break;
                case 19:
                    if (fourBall) {
                        Shooter.oneShotAuto();
                        setTimerAndAdvanceStep(1000);
                    } else {
                        setStep(21);
                    }
                    break;
                case 20: 
                    break;
                case 21:
                    Intake.stopIntake();
                    stop();
                    break;
            } 
        }
    }
}