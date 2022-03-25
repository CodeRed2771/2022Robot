package frc.robot;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.Shooter.ManualShotPreset;

public class AutoTarmacShoot2Vision extends AutoBaseClass{

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
            double degrees = 0;
            switch (getCurrentStep()) {
                case 0:
                    driveInches(64, 0, .8);
                    setTimerAndAdvanceStep(6000);
                    break;
                case 1:
                    if (driveCompleted()) {
                        advanceStep();
                    }
                    break;
                case 2:
                    AutoAlign.ticks();
                    setTimerAndAdvanceStep(1000);
                case 3:
                    break;
                case 4:
                    if (VisionBall.ballInView()) {
                        setStep(7);
                    } else {
                        turnDegrees(50, .8);
                    }
                case 5:
                    if (turnCompleted()) {
                        advanceStep();
                    }
                    break;
                case 6:
                    if (VisionBall.ballInView()) {
                        setStep(7);
                    } else {
                        turnDegrees(-100, .8);
                    }
                case 7: 
                    driveInches(VisionBall.distanceToBall(), VisionBall.degreesToBall(), .8);
                    setTimerAndAdvanceStep(7000);
                    break;
                case 8: 
                    if (driveCompleted()){
                        advanceStep();
                    }
                    break;
                case 9:
                    break;
                case 10:
                    AutoAlign.ticks();
                    setTimerAndAdvanceStep(1000);
                case 11:
                    break;
                case 12:
                    stop();
                    break;
            } 
        }
    }
}