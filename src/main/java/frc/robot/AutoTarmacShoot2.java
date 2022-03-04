package frc.robot;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

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
                case 0: 
                    driveInches(-10, 0, 1);
                    Shooter.setShooterPosition(Shooter.ShooterPosition.Low);
                    Intake.deployIntake();
                    setTimerAndAdvanceStep(6000);
                    break;
                case 1:
                    if (driveCompleted()) {
                        advanceStep();
                    }
                    break;
                case 2: 
                    Shooter.oneShot();
                    setTimerAndAdvanceStep(7000);
                    break;
                case 3: 
                    break;
                case 4:
                    driveInches(-10, 0, .8);
                    Intake.startIntake();
                    break;
                case 5:
                    if (driveCompleted()) {
                        advanceStep();
                    }
                    break;
                case 6: 
                    Intake.retractIntake();
                    setTimerAndAdvanceStep(7000);
                    break;
                case 7:
                    break;
                case 8:
                    Shooter.oneShot();
                    setTimerAndAdvanceStep(700);
                    break;
                case 9:
                    break;
                case 10: 
                    stop();
                    break;
        }
    }
    }
}