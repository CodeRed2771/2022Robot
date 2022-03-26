package frc.robot;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.Shooter.ManualShotPreset;

public class AutoTarmacShoot2Vision extends AutoBaseClass {

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
                    VisionShooter.setLED(true);
                    driveInches(64, 0, .8);
                    setTimerAndAdvanceStep(6000);
                    break;
                case 1:
                    if (driveCompleted()) {
                        advanceStep();
                    }
                    break;
                case 2:
                    Shooter.alignAndShoot();
                    setTimerAndAdvanceStep(1000);
                case 3:
                    break;
                case 4:
                    Intake.startIntake();
                    advanceStep();
                    break;
                case 5:
                    setTimerAndAdvanceStep(1000);
                    break;
                case 6:
                    break;
                case 7:
                    if (VisionBall.ballInView()) {
                        setStep(20);
                    } else {
                        turnDegrees(25, .8);
                        setTimerAndAdvanceStep(1500);
                    }
                    break;
                case 8:
                    if (turnCompleted()) {
                        advanceStep();
                    }
                    break;
                case 9:
                    setTimerAndAdvanceStep(1000);
                    break;
                case 10:
                    break;
                case 11:
                    if (VisionBall.ballInView()) {
                        setStep(20);
                    } else {
                        turnDegrees(-50, .8);
                        setTimerAndAdvanceStep(2500);
                    }
                    break;
                case 12:
                    if (turnCompleted()) {
                        advanceStep();
                    }
                    break;
                case 13:
                    setTimerAndAdvanceStep(1000);
                    break;
                case 14:
                    break;
                case 15:
                    if (VisionBall.ballInView()) {
                        setStep(20);
                    } else {
                        setStep(24);
                    }
                    break;
                case 20:
                    double ballDistance = VisionBall.distanceToBall();
                    if(ballDistance > 36)
                        ballDistance = 36;
                    driveInches(VisionBall.distanceToBall(), VisionBall.degreesToBall(), .8);
                    setTimerAndAdvanceStep(7000);
                    break;
                case 21: 
                    if (driveCompleted()){
                        advanceStep();
                    }
                    break;
                case 22:
                    driveInches(12,0,.8);
                    setTimerAndAdvanceStep(7500);
                    break;
                case 23:
                    if(driveCompleted()) {
                        advanceStep();
                    }
                case 24:
                    Shooter.alignAndShoot();
                    setTimerAndAdvanceStep(1000);
                case 25:
                    break;
                case 26://end
                    stop();
                    break;
            } 
        }
    }
}