/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import java.math.BigDecimal;
import java.math.RoundingMode;

import javax.lang.model.util.ElementScanner6;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.libs.HID.Gamepad;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;

public class Robot extends TimedRobot {

    SendableChooser<String> autoChooser;
    SendableChooser<String> positionChooser;
    SendableChooser<String> driveChooser;
    String autoSelected;
    XboxController gamepad1;
    XboxController gamepad2;
    SwerveTurnTest swtest;

    AutoBaseClass mAutoProgram;

    final String autoCalibrator = "Auto Calibrator";
    final String autoWheelAlign = "Auto Wheel Align";
    final String autoAlign = "Auto Align";
    final String ballPickUp = "Auto Ball Pick Up";
    final String AutoTarmacShoot1 = "Auto Tarmac Shoot 1";

    private boolean isIntakeUpPosition = true;
    private boolean intakeKeyAlreadyPressed = false;

    @Override
    public void robotInit() {
        gamepad1 = new XboxController(0);
        gamepad2 = new XboxController(1);

        Intake.init();
        RobotGyro.init();
        Calibration.loadSwerveCalibration();
        
        // swtest = new SwerveTurnTest();

        if (Calibration.isPracticeBot())
            DriveTrain.init("NEO");
        else
            DriveTrain.init("FALCON");
        
        //DriveAuto.init();

        Shooter.init();
        Vision.init(); // Limelight shooter vision tracking
        setupAutoChoices();
        mAutoProgram = new AutoDoNothing();

        RobotGyro.reset();

        DriveTrain.allowTurnEncoderReset();
        DriveTrain.resetTurnEncoders(); // sets encoders based on absolute encoder positions

        SmartDashboard.putBoolean("Show Encoders", true);
        SmartDashboard.putBoolean("Tune Drive-Turn PIDs", false);
        SmartDashboard.putString("Alliance R or B", "R");
    }

    @Override
    public void teleopInit() {
        // mAutoProgram.stop();
      
        DriveTrain.stopDriveAndTurnMotors();
        DriveTrain.setAllTurnOrientation(0, false); // sets them back to calibrated zero position
        
        VisionBall.init(); // Ball vision tracking setup
        VisionBall.start(); // Start the vision thread
    }

    @Override
    public void teleopPeriodic() {
        SmartDashboard.putNumber("DIST", Vision.getDistanceFromTarget());
        SmartDashboard.putNumber("Ball # Selected" , VisionBall.getClosestBallIndex());
        SmartDashboard.putNumber("Y Offset", VisionBall.getBallYOffset());
        SmartDashboard.putNumber("Number of Balls", VisionBall.getBallNumber());
        SmartDashboard.putNumber("Score", VisionBall.getBallScore());
        if (gamepad1.getAButton()) {
            Shooter.StartShooter();
        }
        if (gamepad1.getBButton()) {
            Shooter.StopShooter();
        }

        if (gamepad1.getRightTriggerAxis() > 0 || gamepad2.getRightTriggerAxis() > 0) {
            Intake.startIntake();
        } else {
            Intake.stopIntake();
        }
        if (gamepad2.getBButton()) {
            Intake.reverseIntake();
        }

        if (gamepad1.getRightBumper()) {
            mAutoProgram = new AutoAlign();
            mAutoProgram.start(false);
        }
        

        if (gamepad2.getXButton()) {
            if (!intakeKeyAlreadyPressed) {
                if (isIntakeUpPosition) {

                } else if (!isIntakeUpPosition) {

                }
                intakeKeyAlreadyPressed = true;
            }
        } else
            intakeKeyAlreadyPressed = false;

        if (gamepad2.getRightBumper() || gamepad1.getRightBumper()) {
            Shooter.StopShooter();
        }
        
        // --------------------------------------------------
        // RESET - allow manual reset of systems by pressing Start
        // --------------------------------------------------
        if (gamepad1.getStartButton()) {
            RobotGyro.reset();
            DriveTrain.allowTurnEncoderReset();
            DriveTrain.resetTurnEncoders(); // sets encoders based on absolute encoder positions
            DriveTrain.setAllTurnOrientation(0, false);
        }

        // DRIVE
        if (mAutoProgram.isRunning()) {
            mAutoProgram.tick();
        }

        // DRIVER CONTROL MODE
        // Issue the drive command using the parameters from
        // above that have been tweaked as needed
        double driveRotAmount = -gamepad1.getRightX();
        double driveFWDAmount = -gamepad1.getLeftY();
        double driveStrafeAmount = -gamepad1.getLeftX();

        double ballLaneAssist = VisionBall.getBallXOffset();

        // if (Math.abs(driveRotAmount)>.1) {
        //     swtest.move(driveRotAmount);
        // }

        // if (gamepad1.getAButton()) {
        //     swtest.setPosA();
        // }
        // if (gamepad1.getBButton()) {
        //     swtest.setPosB();
        // }
        // if (gamepad1.getXButton()) {
        //     swtest.setPosOrig();
        // }
        // swtest.tick();

        // SmartDashboard.putNumber("SWERVE ROT AXIS", driveRotAmount);
        driveRotAmount = rotationalAdjust(driveRotAmount);
        // SmartDashboard.putNumber("ADJUSTED SWERVE ROT AMOUNT", driveRotAmount);
        driveFWDAmount = forwardAdjust(driveFWDAmount, true);
        driveStrafeAmount = strafeAdjust(driveStrafeAmount, true);

        // if (Intake.isRunning()) {
            if (gamepad1.getLeftBumper()) {
                if (ballLaneAssist > 0) 
                    driveStrafeAmount += .2;
                else if (ballLaneAssist < 0) 
                    driveStrafeAmount -= .2;
            }
            
        // }

        if (Math.abs(driveFWDAmount) > .5) {
            if (mAutoProgram.isRunning())
                mAutoProgram.stop();
        }

        if (!mAutoProgram.isRunning()) {
            if (gamepad1.getBackButton()) {
                DriveTrain.humanDrive(driveFWDAmount, driveStrafeAmount, driveRotAmount);
            } else {
                DriveTrain.fieldCentricDrive(driveFWDAmount, driveStrafeAmount, driveRotAmount);
            }
        }


        showDashboardInfo();
    }

    @Override
    public void robotPeriodic() {
        SmartDashboard.updateValues();

        Shooter.tick();
        //DriveAuto.tick();

        SmartDashboard.putNumber("Ball X Offset", VisionBall.getBallXOffset());
        SmartDashboard.putNumber("Distance to Target", Vision.getDistanceFromTarget());

        SmartDashboard.putNumber("Gyro", RobotGyro.getAngle());

         // Sets the PID values based on input from the SmartDashboard
        // This is only needed during tuning
        if (SmartDashboard.getBoolean("Tune Drive-Turn PIDs", false)) {
       
            DriveTrain.setDrivePIDValues(SmartDashboard.getNumber("AUTO DRIVE P", Calibration.AUTO_DRIVE_P),
                    SmartDashboard.getNumber("AUTO DRIVE I", Calibration.AUTO_DRIVE_I),
                    SmartDashboard.getNumber("AUTO DRIVE D", Calibration.AUTO_DRIVE_D),
                    SmartDashboard.getNumber("AUTO DRIVE F", Calibration.AUTO_DRIVE_F));

            DriveTrain.setTurnPIDValues(SmartDashboard.getNumber("TURN P", Calibration.TURN_P),
                    SmartDashboard.getNumber("TURN I", Calibration.TURN_I),
                    SmartDashboard.getNumber("TURN D", Calibration.TURN_D),
                    (int)SmartDashboard.getNumber("TURN I ZONE", Calibration.TURN_I_ZONE),
                    SmartDashboard.getNumber("TURN F", Calibration.TURN_F));

            DriveTrain.setDriveMMAccel((int) SmartDashboard.getNumber("DRIVE MM ACCEL", Calibration.DT_MM_ACCEL));
            DriveTrain.setDriveMMVelocity(
                    (int) SmartDashboard.getNumber("DRIVE MM VELOCITY", Calibration.DT_MM_VELOCITY));
        }
    }

    @Override
    public void autonomousInit() {

        RobotGyro.reset();

        mAutoProgram.stop();
        String selectedPos = positionChooser.getSelected();
        SmartDashboard.putString("Position Chooser Selected", selectedPos);
        char robotPosition = selectedPos.toCharArray()[0];
        System.out.println("Robot position: " + robotPosition);

        autoSelected = (String) autoChooser.getSelected();
        SmartDashboard.putString("Auto Selected: ", autoSelected);

        mAutoProgram = new AutoDoNothing();
        mAutoProgram.start();

        switch (autoSelected) {
        case autoCalibrator:
            mAutoProgram = new AutoCalibrator();
            mAutoProgram.start(robotPosition);
            break;
        case autoWheelAlign:
            mAutoProgram = new AutoWheelAlignment();
            mAutoProgram.start();
            break;
        case autoAlign:
            mAutoProgram = new AutoAlign();
            mAutoProgram.start(robotPosition);
            break;
        case ballPickUp:
            mAutoProgram = new AutoBallPickUp();
            mAutoProgram.start();
            break;
        case AutoTarmacShoot1:
            mAutoProgram = new AutoTarmacShoot1();
            mAutoProgram.start();
        }
    }

    private void setupAutoChoices() {
        // Position Chooser
        positionChooser = new SendableChooser<String>();
        positionChooser.addOption("Left", "L");
        positionChooser.setDefaultOption("Center", "C");
        positionChooser.addOption("Right", "R");
        SmartDashboard.putData("Position", positionChooser);

        autoChooser = new SendableChooser<String>();
        autoChooser.addOption(autoCalibrator, autoCalibrator);
        autoChooser.addOption(autoWheelAlign, autoWheelAlign);
        autoChooser.addOption(autoAlign, autoAlign);
        autoChooser.addOption(ballPickUp, ballPickUp);
        autoChooser.addOption(AutoTarmacShoot1, AutoTarmacShoot1);

        SmartDashboard.putData("Auto Chose:", autoChooser);
    }

    /**
     * This function is called periodically during autonomous.
     */
    @Override
    public void autonomousPeriodic() {
        if (mAutoProgram.isRunning()) {
            mAutoProgram.tick();
        }
    }

    public void disabledInit() {
        // allows the turn encoders to be reset once during disabled periodic
        // DriveTrain.allowTurnEncoderReset();
        // DriveTrain.resetDriveEncoders();
        // DriveTrain.resetTurnEncoders();

        Shooter.StopShooter();
        
        Calibration.initializeSmartDashboard();

        Vision.setLED(false);
    }

    public void disabledPeriodic() {
        showDashboardInfo();
        SmartDashboard.putNumber("DIST", Vision.getDistanceFromTarget());
        SmartDashboard.putBoolean("COMP BOT", !Calibration.isPracticeBot());
        if (Calibration.shouldCalibrateSwerve()) {
            double[] pos = DriveTrain.getAllAbsoluteTurnOrientations();
            Calibration.saveSwerveCalibration(pos[0], pos[1], pos[2], pos[3]);
        }

        Calibration.checkIfShouldResetCalibration();
    }

    /**
     * This function is called periodically during test mode.
     */
    @Override
    public void testPeriodic() {
    }

    private double rotationalAdjust(double rotateAmt) {
        // put some rotational power restrictions in place to make it
        // more controlled movement
        double adjustedAmt = 0;

        if (Math.abs(rotateAmt) < .05) {
            adjustedAmt = 0;
        } else {
            if (Math.abs(rotateAmt) < .5) {
                adjustedAmt = .1 * Math.signum(rotateAmt); // take 10% of the input
            } else {
                if (Math.abs(rotateAmt) < .99) {
                    adjustedAmt = .25 * rotateAmt;
                } else {
                    adjustedAmt = rotateAmt * .4;
                }
            }
        }
        return adjustedAmt;
    }

    private double forwardAdjust(double fwd, boolean normalDrive) {
        if (normalDrive) {
            return fwd * .75;
        } else {
            return fwd * .45;
        }
    }

    private double strafeAdjust(double strafeAmt, boolean normalDrive) {
        // put some power restrictions in place to make it
        // more controlled
        double adjustedAmt = 0;

        if (Math.abs(strafeAmt) < .05) {
            adjustedAmt = 0;
        } else {
            if (Math.abs(strafeAmt) < .4) {
                adjustedAmt = .1 * Math.signum(strafeAmt);
            } else {
                if (Math.abs(strafeAmt) < .7) {
                    adjustedAmt = strafeAmt * .35;
                } else {
                    if (Math.abs(strafeAmt) < .99) {
                        adjustedAmt = strafeAmt * .5;
                    } else {
                        adjustedAmt = strafeAmt * .8;
                    }
                }
            }
        }
        return adjustedAmt;
    }

    private void showDashboardInfo() {
        SmartDashboard.putNumber("Gyro Relative", round2(RobotGyro.getRelativeAngle()));
        SmartDashboard.putNumber("Gyro Raw", round2(RobotGyro.getAngle()));

        if (SmartDashboard.getBoolean("Show Encoders", true)) {
            DriveTrain.showTurnEncodersOnDash();
            DriveTrain.showDriveEncodersOnDash();
        }
        SmartDashboard.updateValues();
    }

    private static Double round2(Double val) {
        // added this back in on 1/15/18
        return new BigDecimal(val.toString()).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }
}


