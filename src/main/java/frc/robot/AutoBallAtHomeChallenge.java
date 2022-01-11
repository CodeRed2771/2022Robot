package frc.robot;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class AutoBallAtHomeChallenge extends AutoBaseClass {

    AutoBallPickUp mAutoBallPickUp = new AutoBallPickUp();

    public AutoBallAtHomeChallenge() {
        
    }

    public void start() {
        super.start();
    }

    public void stop() {
        super.stop();
    }

    @Override
    public void tick() {
        if (isRunning()) {
            DriveAuto.tick();
            SmartDashboard.putNumber("Auto Step", getCurrentStep());
            switch (getCurrentStep()) {
            case 0:
                mAutoBallPickUp.start();
                advanceStep();
                break;
            case 1:
                mAutoBallPickUp.tick();
                if (mAutoBallPickUp.ballCollected()) {
                    advanceStep();
                }
                break;
            case 2:
                advanceStep();
                // if (mAutoBallPickUp.ballCollected()) {
                // advanceStep();
                // }
                break;
            case 3:
                if (mAutoBallPickUp.getDegreesOffBall() == 0 && mAutoBallPickUp.distanceFromBall() == 0)
                {
                    setStep(50);
                }
                else if (mAutoBallPickUp.getDegreesOffBall() > - 4)
                {
                    setStep(90);
                }
                else
                {
                    setStep(70);
                }
                break;
// ----------------------------------------------------------------------------------------------------------------------------------
// BLUE PATH
// ----------------------------------------------------------------------------------------------------------------------------------

// BOTH PATH A AND B
            case 50:
                driveInches(90, 0, 1);
                setTimerAndAdvanceStep(2000);
                break;
            case 51:
                if (driveCompleted())
                {
                    advanceStep();
                }
                break;
            case 52:
                turnToHeading(50, 1);
                setTimerAndAdvanceStep(1000);
                break;
            case 53:
                if (driveCompleted())
                {
                    advanceStep();
                }
                break;
            case 54:
                mAutoBallPickUp.start();
                advanceStep();
                break;
            case 55:
                mAutoBallPickUp.tick();
                if (mAutoBallPickUp.ballCollected())
                {
                    advanceStep();
                }
                break;
            case 56:
                if (mAutoBallPickUp.getDegreesOffBall() > 0)
                {
                    turnToHeading(300, 1);
                    setTimerAndAdvanceStep(1500);
                }
                else 
                {
                    setStep(120);
                }
                break;
            case 57:
                if (driveCompleted())
                {
                    advanceStep();
                }
                break;
            case 58:
                mAutoBallPickUp.start();
                advanceStep();
                break;
            case 59:
                mAutoBallPickUp.tick(); 
                if (mAutoBallPickUp.ballCollected())
                {
                    advanceStep();
                }
                break;
            case 60:
                turnToHeading(10, 1);
                setTimerAndAdvanceStep(1500);
                break;
            case 61:
                if (driveCompleted())
                {
                    advanceStep();
                }
                break;
            case 62:
                mAutoBallPickUp.start();
                advanceStep();
                break;
            case 63:
                mAutoBallPickUp.tick();
                if (mAutoBallPickUp.ballCollected())
                {
                    advanceStep();
                }
                break;
            case 64:
                turnToHeading(0, 1);
                setTimerAndAdvanceStep(1000);
                break;
            case 65: 
                if (driveCompleted())
                {
                    advanceStep();
                }
                break;
            case 66:
                driveInches(80, 0, 1);
                setTimerAndAdvanceStep(3000);
                break;
            case 67:
                if (driveCompleted())
                {
                    setStep(1000);
                }                
                break;
// -----------------------------------------------------------------------------------------------------------------------------------
// RED PATHS
// -----------------------------------------------------------------------------------------------------------------------------------

// PATH A
            case 70:
                turnToHeading(30, 1);
                setTimerAndAdvanceStep(2000);
                break;
            case 71:
                if (driveCompleted())
                {
                    advanceStep();
                }
                break;
            case 72:
                mAutoBallPickUp.start();
                advanceStep();
                break;
            case 73:
                mAutoBallPickUp.tick();
                if (mAutoBallPickUp.ballCollected())
                {
                    advanceStep();
                }
                break;
            case 74:
                turnToHeading(330, 1);
                setTimerAndAdvanceStep(2000);
                break;
            case 75:
                if (driveCompleted())
                {
                    advanceStep();
                }
                break;
            case 76:
                mAutoBallPickUp.start();
                advanceStep();
                break;
            case 77:
                mAutoBallPickUp.tick();
                if (mAutoBallPickUp.ballCollected())
                {
                    advanceStep();
                }
                break;
            case 78:
                turnToHeading(0, 1);
                setTimerAndAdvanceStep(2000);
                break;
            case 79:
                if (driveCompleted())
                {
                    advanceStep();
                }
                break;
            case 80:
                driveInches(160, 0, 1);
                setTimerAndAdvanceStep(6000);
                break;
            case 81:
                if (driveCompleted())
                {
                    setStep(1000);
                }
                break;

// PATH B
            case 90:
                turnToHeading(30, 1);
                setTimerAndAdvanceStep(3000);
                break;
            case 91:
                if (driveCompleted())
                {
                    advanceStep();
                }
                break;
            case 92:
                setStep(94);
                break;
            case 94:
                mAutoBallPickUp.start();
                advanceStep();
                break;
            case 95:
                mAutoBallPickUp.tick();
                if (mAutoBallPickUp.ballCollected())
                {
                    advanceStep();
                }
                break;
            case 96:
                turnToHeading(310, 1);
                setTimerAndAdvanceStep(3000);
                break;
            case 97:
                if (driveCompleted())
                {
                    advanceStep();
                }
                break;
            case 98:
                mAutoBallPickUp.start();
                advanceStep();
                break;
            case 99:
                mAutoBallPickUp.tick();
                if (mAutoBallPickUp.ballCollected())
                {
                    advanceStep();
                }
                break;          
            case 100:
                turnToHeading(0, 1);
                setTimerAndAdvanceStep(2000);
                break;
            case 101:
                if (driveCompleted())
                {
                    advanceStep();
                }
                break;
            case 102:
                driveInches(145, 0, 1);
                setTimerAndAdvanceStep(5000);
                break;
            case 103:
                if (driveCompleted())
                {
                    setStep(1000);
                }
                break;
                
// -----------------------------------------------------------------------------------------------------------------------------------
// blue path b
            case 120:
                turnToHeading(330, 1);
                setTimerAndAdvanceStep(2000);
                break;
            case 121:
                if (driveCompleted())
                {
                    advanceStep();
                }
                break;
            case 122:
                mAutoBallPickUp.start();
                advanceStep();
                break;
            case 123:
                mAutoBallPickUp.tick(); 
                if (mAutoBallPickUp.ballCollected())
                {
                    advanceStep();
                }
                break;
            case 124:
                turnToHeading(35, 1);
                setTimerAndAdvanceStep(1500);
                break;
            case 125:
                if (driveCompleted())
                {
                    advanceStep();
                }
                break;
            case 126:
                mAutoBallPickUp.start();
                advanceStep();
                break;
            case 127:
                mAutoBallPickUp.tick();
                if (mAutoBallPickUp.ballCollected())
                {
                    advanceStep();
                }
                break;
            case 128:
                turnToHeading(0, 1);
                setTimerAndAdvanceStep(1000);
                break;
            case 129: 
                if (driveCompleted())
                {
                    advanceStep();
                }
                break;
            case 130:
                driveInches(60, 0, 1);
                setTimerAndAdvanceStep(3000);
                break;
            case 131:
                if (driveCompleted())
                {
                    setStep(1000);
                }                
                break;
// ------------------------------------------------------------------------------------------------------------------------------
            case 1000:
                stop();
                break;
            }
        }
    }
}