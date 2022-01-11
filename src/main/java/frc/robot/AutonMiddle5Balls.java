package frc.robot;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class AutonMiddle5Balls extends AutoBaseClass {

    private AutoAlign mAutoAlign = new AutoAlign();

    public void start() {
        super.start();
    }

    public void stop() {
        super.stop();
    }

    @Override
    public void tick() {
        if (isRunning()) {
            Vision.setTargetForShooting();
            DriveAuto.tick();
            SmartDashboard.putNumber("Auto Step", getCurrentStep());
            switch (getCurrentStep()) {
            case 0:
                mAutoAlign.start();
                advanceStep();
                break;
            case 1:
                mAutoAlign.tick();
                if (Vision.onTarget()) {
                    advanceStep();
                }
                break;
            case 2:
                Shooter.StartShooter();
                setTimerAndAdvanceStep(2000);
                break;
            case 3:
                advanceStep();
                break;
            case 4:
                Shooter.StopShooter();
                advanceStep();
                break;
            case 5:
                turnToHeading(337.5 , 1);
                setTimerAndAdvanceStep(2000);
                break;
            case 6:
                if (driveCompleted()) {
                    advanceStep();
                }
                break;
            case 7:
                driveInches(100, 180, 1, false, true);
                setTimerAndAdvanceStep(2000);
                break;
            case 8:
                if (driveCompleted()) {
                    advanceStep();
                }
                break;
            case 9:
                Intake.moveIntakeDown();
                advanceStep();
                break;
            case 10:
                Intake.runIntakeForwards();
                advanceStep();
                break;
            case 11:
                driveInches(22.5, 270, .5, false, true);
                setTimerAndAdvanceStep(1000);
                break;
            case 12:
                if (driveCompleted()) {
                    advanceStep();
                }
                break;
            case 13:
                driveInches(85, 156, 0.6, false, true);
                setTimerAndAdvanceStep(3000);
                break;
            case 14:
                if (driveCompleted()) {
                    advanceStep();
                }
                break;
            case 15:
                Intake.stopIntake();
                Intake.moveIntakeUp();
                advanceStep();
                break;
            case 16:
                mAutoAlign.tick();
                if (Vision.onTarget()) {
                    advanceStep();
                }
                break;
            case 17:
                Shooter.StartShooter();
                setTimerAndAdvanceStep(3000);
                break;
            case 18:
                advanceStep();
                break;
            case 19:
                Shooter.StopShooter();
                advanceStep();
                break;
            case 20:
                stop();
                break;
            }
        }
    }
}
