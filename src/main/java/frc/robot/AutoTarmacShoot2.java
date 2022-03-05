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
                    driveInches(44, 0, 0.75);
                    Shooter.setManualPresets(ManualShotPreset.BackOfTarmac);
                    setTimerAndAdvanceStep(3000);
                    break;
                case 1:
                    break;
                case 2://segment 2
                    Shooter.oneShot();
                    setTimerAndAdvanceStep(1000);
                    break;
                case 3:
                    break;
                case 4:
                    Intake.deployIntake();
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
                case 8:
                    Intake.stopIntake();
                    Intake.retractIntake();
                    advanceStep();
                    break;
                case 9:
                    Shooter.oneShot();
                    setTimerAndAdvanceStep(1000);
                case 10:
                    break;
                case 11://stop
                    stop();
                    break;
            } 
        }
    }
}