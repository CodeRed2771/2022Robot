package frc.robot;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.Shooter.ManualShotPreset;

public class AutoTarmacShoot2 extends AutoBaseClass{

	public void start() {
		super.start();
	}
    
    public void stop() {
        super.stop();
    }

    @Override
    public void tick() {
        if (isRunning()) {
            SmartDashboard.putNumber("Auto Step", getCurrentStep());
            switch (getCurrentStep()) {
                case 0://segment 1 
                    driveInches(64, 0, 0.75);
                    Shooter.setManualPresets(ManualShotPreset.BackOfTarmac);
                    Intake.deployIntake();
                    setTimerAndAdvanceStep(3000);
                    break;
                case 1:
                    break;
                case 2://segment 2
                    Shooter.oneShotAuto();
                    setTimerAndAdvanceStep(1000);
                    break;
                case 3:
                    break;
                case 4:
                    Intake.startIntake();
                    advanceStep();
                    break;
                case 5:
                    driveInches(40, 0, 0.75);
                    setTimerAndAdvanceStep(3000);
                    break;
                case 6:
                    if(driveCompleted()) {
                        advanceStep();
                    }
                    break;
                case 7:
                    advanceStep();
                    break;
                case 8: 
                    driveInches(-50,0,.75);
                    setTimerAndAdvanceStep(3000);
                    break;
                case 9: 
                    if(driveCompleted()) {
                        advanceStep();
                    }
                    break;
                case 10: 
                    driveInches(10,0,.75);
                    setTimerAndAdvanceStep(2000);
                    break;
                case 11: 
                    if(driveCompleted()) {
                        advanceStep();
                    }
                    break;
                case 12:
                    Intake.stopIntake();
                    setTimerAndAdvanceStep(1000); // give ball time to settle
                    break;
                case 13:
                    break;
                case 14:
                    Shooter.oneShotAuto();
                    setTimerAndAdvanceStep(1500);
                case 15:
                    break;
                case 16: 
                    driveInches(38, 0, .75);
                    setTimerAndAdvanceStep(3000);
                    break;
                case 17:
                    if(driveCompleted()) {
                        advanceStep();
                    }
                    break;
                case 18://stop
                    stop();
                    break;
            } 
        }
    }
}