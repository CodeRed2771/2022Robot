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
                case 0://segment 1 
                    driveInches(70, 0, 0.75);
                    Shooter.setShooterPosition(ShooterPosition.Medium);
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
                    driveInches(12, 0, 0.75);
                    setTimerAndAdvanceStep(2000);
                    break;
                case 5:
                    
                case 6://stop
                    stop();
                    break;
            } 
        }
    }
}