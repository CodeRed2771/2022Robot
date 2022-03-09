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
                    driveInches(40, 0, 0.75);
                    Shooter.setManualPresets(ManualShotPreset.BackOfTarmac);
                    Intake.deployIntake();
                    setTimerAndAdvanceStep(3000);
                    break;
                case 1:
                    break;
                case 2://segment 2
                    Shooter.alignAndShoot();
                    setTimerAndAdvanceStep(1000);
                    break;
                case 3:
                    break;
                case 4:
                    Intake.startIntake();
                    advanceStep();
                    break;
                case 6:
                    driveInches(38, 0, 0.75);
                    setTimerAndAdvanceStep(2000);
                    break;
                case 7:
                    if(driveCompleted()) {
                        advanceStep();
                    }
                    break;
                case 8:
                    Intake.stopIntake();
                    advanceStep();
                    break;
                case 9: 
                    driveInches(-38,0,.75);
                    setTimerAndAdvanceStep(2000);
                    break;
                case 10: 
                    if(driveCompleted()) {
                        advanceStep();
                    }
                    break;
                case 11:
                    Shooter.oneShotAuto();
                    setTimerAndAdvanceStep(1000);
                case 12:
                    break;
                case 13: 
                    driveInches(38, 0, .75);
                    setTimerAndAdvanceStep(2000);
                    break;
                case 14:
                    if(driveCompleted()) {
                        advanceStep();
                    }
                    break;
                case 15://stop
                    stop();
                    break;
            } 
        }
    }
}