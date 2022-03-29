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
                case 0://step 1
                    VisionShooter.setLED(true);
                    driveInches(64, 0, .8);
                    setTimerAndAdvanceStep(6000);
                    break;
                case 1://step 2
                    if (driveCompleted()) {
                        advanceStep();
                    }
                    break;
                case 2://step 3
                    Shooter.alignAndShoot();
                    setTimerAndAdvanceStep(1000);
                case 3://step 4
                    break;
                case 4://step 5
                    Intake.startIntake();
                    advanceStep();
                    break;
                case 5://step 6
                    setTimerAndAdvanceStep(1000);
                    break;
                case 6://step 7
                    break;
                case 7://step 8
                    if (VisionBall.ballInView()) {
                        setStep(20);
                    } else {
                        turnDegrees(25, .8);
                        setTimerAndAdvanceStep(1500);
                    }
                    break;
                case 8://step 9
                    if (turnCompleted()) {
                        advanceStep();
                    }
                    break;
                case 9://step 10
                    setTimerAndAdvanceStep(1000);
                    break;
                case 10://step 11
                    break;
                case 11://step 12
                    if (VisionBall.ballInView()) {
                        setStep(20);
                    } else {
                        turnDegrees(-50, .8);
                        setTimerAndAdvanceStep(2500);
                    }
                    break;
                case 12://step 13
                    if (turnCompleted()) {
                        advanceStep();
                    }
                    break;
                case 13://step 14
                    setTimerAndAdvanceStep(1000);
                    break;
                case 14://step 15
                    break;
                case 15://step 16
                    if (VisionBall.ballInView()) {
                        setStep(20);
                    } else {
                        setStep(24);
                    }
                    break;
                case 20://step 17
                    double ballDistance = VisionBall.distanceToBall();
                    if(ballDistance > 36)
                        ballDistance = 36;
                    driveInches(ballDistance, VisionBall.degreesToBall(), .8);
                    setTimerAndAdvanceStep(7000);
                    break;
                case 21://step 18
                    if (driveCompleted()){
                        advanceStep();
                    }
                    break;
                case 22://step 19
                    driveInches(12,0,.8);
                    setTimerAndAdvanceStep(1500);
                    break;
                case 23://step 20
                    if(driveCompleted()) {
                        advanceStep();
                    }
                case 24://step 21
                    Shooter.alignAndShoot();
                    setTimerAndAdvanceStep(1000);
                case 25://step 22
                    break;
                case 26://end
                    stop();
                    break;
            } 
        }
    }
}