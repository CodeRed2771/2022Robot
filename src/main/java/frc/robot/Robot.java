/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

import javax.lang.model.util.ElementScanner6;

import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.PneumaticsModuleType;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.Climber.ClimberPosition;
import frc.robot.Shooter.ManualShotPreset;
import frc.robot.Shooter.ShooterPosition;
import frc.robot.libs.HID.Gamepad;
import pabeles.concurrency.IntOperatorTask.Max;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;

public class Robot extends TimedRobot { 

    SendableChooser<String> autoChooser;
    SendableChooser<String> positionChooser;
    //SendableChooser<String> positionAllianceChooser;
    SendableChooser<String> driveChooser;
    String autoSelected;
    Gamepad gamepad1;
    Gamepad gamepad2;
    SwerveTurnTest swtest;
    Compressor compressor = new Compressor(1, PneumaticsModuleType.REVPH);
    boolean reverseShooter = false;
        
    AutoBaseClass mAutoProgram;

    final String autoCalibrator = "Auto Calibrator";
    final String autoWheelAlign = "Auto Wheel Align";
    final String autoAlign = "Auto Align";
    final String ballPickUp = "Auto Ball Pick Up";
    final String AutoLeaveTarmac = "Auto Leave Tarmac";
    final String AutoTarmacShoot1 = "Auto Tarmac Shoot 1";
    final String AutoTarmacShoot2 = "Auto Tarmac Shoot 2";
    final String AutoTarmacShoot2Vision = "Auto Tarmac Shoot 2 Vision";

    private boolean isIntakeUpPosition = true;
    private boolean intakeKeyAlreadyPressed = false;

    private double lastFWDvalue = 0; 
    private double lastSTRvalue = 0;
    private double lastROTvalue = 0;

    @Override
    public void robotInit() {
        gamepad1 = new Gamepad(0);
        gamepad2 = new Gamepad(1);

        compressor.enableAnalog(100, 120);

        Intake.init();
        Shooter.init();
        RobotGyro.init();
        Calibration.loadSwerveCalibration();
        
        // swtest = new SwerveTurnTest();

        if (Calibration.isPracticeBot()) 
            DriveTrain.init("NEO");
        else
            DriveTrain.init("FALCON");
        
        DriveAuto.init();

        SmartDashboard.putNumber("Current Position", 0);
        SmartDashboard.putNumber("New Position", 0);

        VisionShooter.init(); // Limelight shooter vision tracking
        setupAutoChoices();
        mAutoProgram = new AutoDoNothing();


        SmartDashboard.putBoolean("Show Encoders", true);
        SmartDashboard.putBoolean("Tune Drive-Turn PIDs", false);
        SmartDashboard.putString("Alliance R or B", "R");
    }

    @Override
    public void teleopInit() {
        mAutoProgram.stop();
      
        DriveTrain.stopDriveAndTurnMotors();
        DriveTrain.allowTurnEncoderReset();
        DriveTrain.resetTurnEncoders();
        DriveTrain.setAllTurnOrientation(0, false); // sets them back to calibrated zero position
        
        VisionBall.init(); // Ball vision tracking setup
        VisionBall.start(); // Start the vision thread
        Shooter.StartShooter();
        Shooter.setSpeed(6000);
        //Climber.init();
    }

    @Override
    public void teleopPeriodic() {
        SmartDashboard.putNumber("DIST", VisionShooter.getDistanceFromTarget());
        // SmartDashboard.putNumber("Ball # Selected" , VisionBall.getClosestBallIndex());
        // SmartDashboard.putNumber("Y Offset", VisionBall.getBallYOffset());
        // SmartDashboard.putNumber("Number of Balls", VisionBall.getBallNumber());
        // SmartDashboard.putNumber("Score", VisionBall.getBallScore());
       
        if (gamepad1.getAButton()) {
            Shooter.StartShooter();
            
        }

        if (gamepad1.getRightTriggerAxis() > 0 || gamepad2.getRightTriggerAxis() > 0) {
            Intake.startIntake();
        } else {
            Intake.stopIntake();
        }
        if (gamepad2.getLeftBumper()) {
            Intake.reverseIntake();
        }

        if (gamepad2.getDPadUp() && !Intake.getIntakePosition()) {
            Intake.deployIntake();
         } else if (gamepad2.getDPadDown() && Intake.getIntakePosition()) {
             Intake.retractIntake();
         }
        SmartDashboard.putNumber("D pad", gamepad1.getPOV(1));

        if (gamepad1.getLeftBumper() && !mAutoProgram.isRunning() && !AutoAlign.getAllignment()) {
            mAutoProgram = new AutoAlign();
            mAutoProgram.start(true);
        } else if (gamepad1.getLeftBumper() && AutoAlign.getAllignment()) {
            Shooter.alignAndShoot();
        }
        
        if (gamepad2.getLeftTriggerAxis() > 0 ||gamepad1.getLeftTriggerAxis() > 0) {
            Shooter.alignAndShoot();
        }
        if (gamepad2.getAButton()) {
            Shooter.setManualPresets(ManualShotPreset.LowGoal);
        } else if (gamepad2.getBButton()) {
            Shooter.setManualPresets(ManualShotPreset.TarmacLine);
        } else if (gamepad2.getYButton()) {
            // Shooter.setManualPresets(ManualShotPreset.Backwards);
            Shooter.setManualPresets(ManualShotPreset.SafeZone);
        } else if (gamepad2.getXButton()) {
            Shooter.setManualPresets(ManualShotPreset.SuperCloseHighShot);
        }
        if (gamepad2.getStartButton()) {
            Shooter.reverseShooter();
        } else {
            Shooter.endReverseShooter();
        }
            

        if (gamepad2.getRightBumper() || gamepad1.getRightBumper()) {
            Shooter.StopShooter();
        }

        if (gamepad2.getLeftStickButtonPressed()) {
            Shooter.setBallLiftDown();
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

        if (gamepad1.getXButton()) {
            VisionShooter.setLED(true);
        } else if (gamepad1.getYButton()) {
            VisionShooter.setLED(false);
        }

        // if (gamepad2.getRightX() > 0.05) {
        //     Climber.climber(gamepad2.r);
        // }
        // Climber.climber(gamepad2.getRightX());
        // if (gamepad2.getLeftY() > 0.5) {
        //     Climber.climberPosition(ClimberPosition.OffCenter);
        // } else if (gamepad2.getLeftY() > -0.5) {
        //     Climber.climberPosition(ClimberPosition.Straight);
        // }

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
        SmartDashboard.putNumber("Best Position", TurnPosition.getBestPosition());
        
        if (VisionBall.ballInView()) {
            // if (Intake.isRunning()) {
            //     if (ballLaneAssist > 0.05) 
            //         driveStrafeAmount += .2;
            //     else if (ballLaneAssist < -0.05) 
            //         driveStrafeAmount -= .2;
            // }


            if (Intake.isRunning()) {
                if (RobotGyro.getAngle() == 0 || RobotGyro.getAngle() == 180) {
                    if(ballLaneAssist > 0.05) {
                        driveStrafeAmount += .2;
                    } else if (ballLaneAssist < -0.05) {
                        driveStrafeAmount += -.2;
                    }
                } else if (RobotGyro.getAngle() == 90 || RobotGyro.getAngle() == 270) {
                    if(ballLaneAssist > 0.05) {
                        driveFWDAmount += .2;
                    } else if (ballLaneAssist < -0.05) {
                        driveFWDAmount += -.2;
                    }
                }
                else {
                    if(ballLaneAssist > 0.05) {
                        driveFWDAmount += Math.cos(VisionBall.degreesToBall()) * 0.2;
                        driveStrafeAmount += Math.sin(VisionBall.degreesToBall()) * 0.2;
                    }else if (ballLaneAssist < -0.05) {
                        driveFWDAmount += Math.cos(VisionBall.degreesToBall()) * -0.2;
                        driveStrafeAmount += Math.sin(VisionBall.degreesToBall()) * -0.2;
                    }
                }
            }
        }


        SmartDashboard.putNumber("Outputs FWD", driveFWDAmount);
        SmartDashboard.putNumber("Outputs Strafe", driveStrafeAmount);
        

        if (Math.abs(driveFWDAmount) > .5 || Math.abs(driveRotAmount) > .5) {
            if (mAutoProgram.isRunning())
                mAutoProgram.stop();
        }

        if (!mAutoProgram.isRunning()) {
            if (gamepad1.getBackButton()) {
                DriveTrain.humanDrive(driveFWDAmount, driveStrafeAmount, driveRotAmount);
                AutoAlign.setAllignment(false);
            } else {
                DriveTrain.fieldCentricDrive(driveFWDAmount, driveStrafeAmount, driveRotAmount);
                AutoAlign.setAllignment(false);
            }
        }


        showDashboardInfo();
    }


    @Override
    public void robotPeriodic() {
        SmartDashboard.updateValues();

        Shooter.tick();
        Intake.tick();
        DriveAuto.tick();

        // SmartDashboard.putNumber("Ball X Offset", VisionBall.getBallXOffset());
        SmartDashboard.putNumber("Distance to Target", VisionShooter.getDistanceFromTarget());

        SmartDashboard.putNumber("Gyro", RobotGyro.getAngle());

         // Sets the PID values based on input from the SmartDashboard
        // This is only needed during tuning
        if (SmartDashboard.getBoolean("Tune Drive-Turn PIDs", false)) {
       
            DriveTrain.setDrivePIDValues(SmartDashboard.getNumber("AUTO DRIVE P", Calibration.getDriveP()),
                    SmartDashboard.getNumber("AUTO DRIVE I", Calibration.getDriveI()),
                    SmartDashboard.getNumber("AUTO DRIVE D", Calibration.getDriveD()),
                    SmartDashboard.getNumber("AUTO DRIVE F", Calibration.getDriveF()));

            DriveTrain.setTurnPIDValues(SmartDashboard.getNumber("TURN P", Calibration.getTurnP()),
                    SmartDashboard.getNumber("TURN I", Calibration.getTurnI()),
                    SmartDashboard.getNumber("TURN D", Calibration.getTurnD()),
                    SmartDashboard.getNumber("TURN I ZONE", Calibration.getTurnIZone()),
                    SmartDashboard.getNumber("TURN F", Calibration.getTurnF()));

            DriveTrain.setDriveMMAccel((int) SmartDashboard.getNumber("DRIVE MM ACCEL", Calibration.DT_MM_ACCEL));
            DriveTrain.setDriveMMVelocity(
                    (int) SmartDashboard.getNumber("DRIVE MM VELOCITY", Calibration.DT_MM_VELOCITY));
        }
    }

    @Override
    public void autonomousInit() {

        // RobotGyro.reset();

        DriveTrain.stopDriveAndTurnMotors();
        DriveTrain.allowTurnEncoderReset();
        DriveTrain.resetTurnEncoders();
        DriveTrain.setAllTurnOrientation(0, false); // sets them back to calibrated zero position

        mAutoProgram.stop();
        String selectedPos = positionChooser.getSelected();
        SmartDashboard.putString("Position Chooser Selected", selectedPos);
        char robotPosition = selectedPos.toCharArray()[0];
        System.out.println("Robot position: " + robotPosition);

        Shooter.setShooterPosition(ShooterPosition.Medium); // releases the starting position block
        Intake.startIntake(); // releases the intake strap

        autoSelected = (String) autoChooser.getSelected();
        SmartDashboard.putString("Auto Selected: ", autoSelected);
        String postionAllianceSelected = (String) autoChooser.getSelected();
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
        case AutoLeaveTarmac:
            mAutoProgram = new AutoLeaveTarmack();
            mAutoProgram.start();
            break;
        case AutoTarmacShoot1:
            mAutoProgram = new AutoTarmacShoot1();
            mAutoProgram.start();
            break;
        case AutoTarmacShoot2:
            mAutoProgram = new AutoTarmacShoot2();
            mAutoProgram.start();
            break;
        case AutoTarmacShoot2Vision:
            mAutoProgram = new AutoTarmacShoot2Vision();
            mAutoProgram.start();
            break;
        }
        // double degreesOffset;
        // switch (postionAllianceSelected) {
        //     case "Red 1":
        //         degreesOffset = 15;
        //         break;
        //     case "Red 2":
        //         degreesOffset = 0;
        //         break;
        //     case "Red 3":
        //         degreesOffset = -15;
        //         break;
        //     case "Blue 1":
        //         degreesOffset = 15;
        //         break;
        //     case "Blue 2":
        //         degreesOffset = 0;
        //         break;
        //     case "Blue 3":
        //         degreesOffset = -15; 
        //         break;
        // }

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
        autoChooser.addOption(AutoLeaveTarmac, AutoLeaveTarmac);
        autoChooser.addOption(AutoTarmacShoot1, AutoTarmacShoot1);
        autoChooser.addOption(AutoTarmacShoot2, AutoTarmacShoot2);
        autoChooser.addOption(AutoTarmacShoot2Vision, AutoTarmacShoot2Vision);

        SmartDashboard.putData("Auto Chose:", autoChooser);

        // Position Chooser 2
        // positionAllianceChooser = new SendableChooser<String>();
        // positionAllianceChooser.addOption("Red 1", "Red 1");
        // positionAllianceChooser.addOption("Red 2", "Red 2");
        // positionAllianceChooser.addOption("Red 3", "Red 3");
        // positionAllianceChooser.addOption("Blue 1", "Blue 1");
        // positionAllianceChooser.addOption("Blue 2", "Blue 2");
        // positionAllianceChooser.addOption("Blue 3", "Blue 3");
    }

    /**
     * This function is called periodically during autonomous.
     */
    @Override
    public void autonomousPeriodic() {
        if (mAutoProgram.isRunning()) {
            mAutoProgram.tick();
        }
        showDashboardInfo();
    }

    public void disabledInit() {
        // allows the turn encoders to be reset once during disabled periodic
        // DriveTrain.allowTurnEncoderReset();
        // DriveTrain.resetDriveEncoders();
        // DriveTrain.resetTurnEncoders();

        Shooter.StopShooter();
        
        Calibration.initializeSmartDashboard();

        VisionShooter.setLED(false);
    }

    public void disabledPeriodic() {
        showDashboardInfo();
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
        final double maxFWDchange = .02;
        double adjustedFWD = 0;

        if (normalDrive) {
            adjustedFWD = fwd * 1;
        } else {
            adjustedFWD = fwd * .45;
        }
        // ramp in non zero direction only
        if (adjustedFWD >= 0) {
            if (adjustedFWD > lastFWDvalue && adjustedFWD > .2) // speeding up so control it
                if (adjustedFWD >  lastFWDvalue + maxFWDchange) {
                    adjustedFWD = lastFWDvalue + maxFWDchange;
                } 
        } else {
            if (adjustedFWD < lastFWDvalue && adjustedFWD < -.2) // speeding up in reverse
                if (adjustedFWD < lastFWDvalue - maxFWDchange) {
                    adjustedFWD = lastFWDvalue - maxFWDchange;
                }
        }

        lastFWDvalue = adjustedFWD;
        return adjustedFWD;
    }

    private double strafeAdjust(double strafeAmt, boolean normalDrive) {
        final double maxSTRchange = .02;
        double adjustedSTR = 0;
 
        if (normalDrive) {
            adjustedSTR = strafeAmt * 1;
        } else {
            adjustedSTR = strafeAmt * .45;
        }
        // ramp in non zero direction only
        if (adjustedSTR >= 0) {
            if (adjustedSTR > lastFWDvalue && adjustedSTR > .2) // speeding up so control it
                if (adjustedSTR >  lastSTRvalue + maxSTRchange) {
                    adjustedSTR = lastSTRvalue + maxSTRchange;
                } 
        } else {
            if (adjustedSTR < lastFWDvalue && adjustedSTR < -.2) // speeding up in reverse
                if (adjustedSTR < lastSTRvalue - maxSTRchange) {
                    adjustedSTR = lastSTRvalue - maxSTRchange;
                }
        }

        lastSTRvalue = adjustedSTR;
        
        return adjustedSTR;
    }

    private double strafeAdjustOLD(double strafeAmt, boolean normalDrive) {
        final double maxSTRchange = .02;
        double adjustedSTR = 0;
        double adjustedAmt = 0;

        if (Math.abs(strafeAmt) < .05) {
            adjustedSTR = 0;
        } else {
            if (Math.abs(strafeAmt) < .4) {
                adjustedSTR = .1 * Math.signum(strafeAmt);
            } else {
                if (Math.abs(strafeAmt) < .7) {
                    adjustedSTR = strafeAmt * .35;
                } else {
                    if (Math.abs(strafeAmt) < .99) {
                        adjustedSTR = strafeAmt * .5;
                    } else {
                        adjustedSTR = strafeAmt * .8;
                    }
                }
            }
        }
        return adjustedSTR;
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


