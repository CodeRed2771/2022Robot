/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import java.math.BigDecimal;
import java.math.RoundingMode;

import edu.wpi.first.wpilibj.BuiltInAccelerometer;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import jdk.dynalink.support.SimpleRelinkableCallSite;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;

public class Robot extends TimedRobot {

	SendableChooser<String> autoChooser;
	SendableChooser<String> positionChooser;
	SendableChooser<String> driveChooser;
	String autoSelected;
	KeyMap gamepad;

	AutoBaseClass mAutoProgram;
	final String bluepathAutonSkillstwo = "skill two a";
	final String skillsOne = "Skill One";
	final String AutoSlalom = "Slalom"; 
	final String autoBarrelRace = "BarrelRace";
	final String AutoBouncePath = "BouncePath";
	final String threeBasicBalls = "3 Basic Balls";
	final String sixBallRight = "6 Balls Right";
	final String fiveBallsMiddle = "5 Balls Middle";
	final String sixBallsLeft = "6 Balls Left";
	final String autoCalibrator = "Auto Calibrator";
	final String autoWheelAlign = "Auto Wheel Align";
	final String autoAlign = "Auto Align";
	final String ballPickUp = "Auto Ball Pick Up";
	final String AutoAtHomeBallPickUp = "AT HOME Ball Challenge";
	private boolean isIntakeUpPosition = true;
	private boolean intakeKeyAlreadyPressed = false;

	@Override
	public void robotInit() {
		gamepad = new KeyMap();

		Shooter.getInstance();
		Shooter.StopShooter();
		ShooterPivoter.getInstance();
		Intake.getInstance();
		RobotGyro.getInstance();
		// DistanceSensor.getInstance();
		Calibration.loadSwerveCalibration();
		DriveTrain.getInstance();
		DriveAuto.getInstance();
		// Climber.getInstance();
		ColorSensorAndTraverser.getInstance();
		Vision.getInstance();
		VisionBall.SetUpBallVision();
		DistanceSensor.getInstance();
		setupAutoChoices();
		mAutoProgram = new AutoDoNothing();

		RobotGyro.reset();
		Shooter.closeGate();

		DriveTrain.allowTurnEncoderReset();
		DriveTrain.resetTurnEncoders(); // sets encoders based on absolute encoder positions

		SmartDashboard.putBoolean("Show Encoders", true);
		SmartDashboard.putBoolean("Tune Drive-Turn PIDs", false);
	}

	@Override
	public void teleopInit() {
		mAutoProgram.stop();
		ShooterPivoter.resetPivoter();
		Intake.stopIntake();
		DriveTrain.stopDriveAndTurnMotors();
		DriveTrain.setAllTurnOrientation(0, false); // sets them back to calibrated zero position
		Shooter.StopShooter();
		Shooter.closeGate();
	}

	@Override
	public void teleopPeriodic() {
		SmartDashboard.putNumber("DIST", Vision.getDistanceFromTarget());

		if (gamepad.startVision()) {
			mAutoProgram = new AutoAlign();
			mAutoProgram.start(false);
		}
		if (gamepad.startIntake()) {
			Intake.runIntakeForwards();
			Intake.moveQueuerDown();
		}
		if (gamepad.stopIntake()) {
			Intake.stopIntake();
		}

		if (gamepad.intakeUpPosition()) {
			if (!intakeKeyAlreadyPressed) {
				if (isIntakeUpPosition) {
					Intake.moveIntakeDown();
					isIntakeUpPosition = false;
				} else if (!isIntakeUpPosition) {
					Intake.moveIntakeUp();
					isIntakeUpPosition = true;
				}
				intakeKeyAlreadyPressed = true;
			}
		} else
			intakeKeyAlreadyPressed = false;

		// if (gamepad.spinWheel()) {
		// 	ColorSensorAndTraverser.start3To5TimesSpinning();
		// }
		// if (gamepad.matchColor()) {
		// 	ColorSensorAndTraverser.startMatchColorSpinning();
		// }
		if (gamepad.stopShooter() || gamepad.stopShooting()) {
			Shooter.StopShooter();
		}
		// if (gamepad.lowClimberHeight() && gamepad.stopIntake()) {
		// 	Climber.extendHook();
		// }
		// if (gamepad.colorWheelClimberHeight()) {
		// 	Climber.setColorWheelClimberPosition();
		// }
		// if (gamepad.climber() && gamepad.getRobotCentricModifier()) {
		// 	Climber.setHighClimberPosition();
		// }
		if (gamepad.closeShooterPosition()) {
			ShooterPivoter.shootClosePosition();
		}
		if (gamepad.midTrenchPosition()) {
			ShooterPivoter.shootFromFrontOfTrench();
		}
		if (gamepad.backTrenchPosition()) {
			ShooterPivoter.shootFromBackOfTrench();
		}
		if (gamepad.startShooter()) {
			Shooter.StartShooter();
			Intake.moveIntakeDown();
			Intake.moveQueruerUp();
		}
		// if (gamepad.levelScale()) {
		// 	ColorSensorAndTraverser.runTrue(true);
		// }
		// if (gamepad.turboTurning()) {
		// 	// ADD TURBO TURN
		// }
		// if (gamepad.dropBellyPan()) {
		// 	Climber.dropBellyPan(true);
		// }
		// if (gamepad.pickUpbellyPanContinueClimb()) {
		// 	Climber.pickUpBellyPanAndContinueClimbing(true);
		// }
		if (gamepad.oneShotShooter()) {
			Shooter.oneShot();
		}
		if (gamepad.continualShooter()) {
			Shooter.continuousShooting();
		}
		// if (gamepad.getRobotCentricModifier() && gamepad.oneShotShooter()) {
		// 	Climber.setIdealClimberPositionToDropBellyPan();
		// }
		if (Math.abs(gamepad.shooterPivoterAdjuster()) > 0.1) {
			ShooterPivoter.manualMove(-gamepad.shooterPivoterAdjuster());	 // THIS FUNCTIONS NEED TO BE IMPROVISED
																				// BASED ON WHAT WE ARE GIVEN
		}
		// TEMP FOR TESTING
		//ShooterPivoter.manualMove(gamepad.shooterPivoterAdjuster());	 // THIS FUNCTIONS NEED TO BE IMPROVISED
			
		// if (Math.abs(gamepad.manualClimberAdjuster()) > 0.1) {
		// 	Climber.adjustExtendedHook(gamepad.manualClimberAdjuster());
		// }
		// if (gamepad.oneShotShooter() && gamepad.continualShooter()) {
		// 	Climber.liftRobot(gamepad.liftSpeed());
		// }
		if (gamepad.turn180Degrees()) {
			DriveAuto.turnDegrees(180, 1);
		}
		// // if (gamepad.turnToZeroDegrees()) {
		// 	DriveAuto.turnToHeading(0, 1);
		// }
		if (gamepad.runIntakeBackWards()) {
			Intake.runIntakeBackwards();
		}

		// Shooter.setAdjustmentFactor(gamepad.getShooterAdjustment());

		ColorSensorAndTraverser.matchColor();
		ColorSensorAndTraverser.levelScale();

		// DistanceSensor.tick();
		// --------------------------------------------------
		// RESET - allow manual reset of systems by pressing Start
		// --------------------------------------------------
		if (gamepad.getZeroGyro()) {
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
		double driveRotAmount = -gamepad.getSwerveRotAxis();
		double driveFWDAmount = gamepad.getSwerveYAxis();
		double driveStrafeAmount = -gamepad.getSwerveXAxis();

		// SmartDashboard.putNumber("SWERVE ROT AXIS", driveRotAmount);
		driveRotAmount = rotationalAdjust(driveRotAmount);
		// SmartDashboard.putNumber("ADJUSTED SWERVE ROT AMOUNT", driveRotAmount);
		driveFWDAmount = forwardAdjust(driveFWDAmount, true);
		driveStrafeAmount = strafeAdjust(driveStrafeAmount, true);

		if (Math.abs(driveFWDAmount) > .5) {
			if (mAutoProgram.isRunning())
				mAutoProgram.stop();
		}

		if (!mAutoProgram.isRunning()) {
			if (gamepad.getRobotCentricModifier()) {
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
		ShooterPivoter.tick();
		DriveAuto.tick();
		// Climber.tick();
		ColorSensorAndTraverser.tick();

		SmartDashboard.putNumber("Gyro", RobotGyro.getAngle());
		SmartDashboard.putNumber("US Distance", DistanceSensor.GetInches());
		SmartDashboard.putNumber("ShootPivot pos", ShooterPivoter.getShaftEncoderPosition());

		 // Sets the PID values based on input from the SmartDashboard
        // This is only needed during tuning
        // if (SmartDashboard.getBoolean("Tune Drive-Turn PIDs", false)) {
       
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
        // }


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
		case skillsOne:
			mAutoProgram = new AutonSkillsOne();
			mAutoProgram.start();
			break;
		case AutoSlalom:
			mAutoProgram = new AutoSlalom();
			mAutoProgram.start();
			break;
		case autoBarrelRace:
			mAutoProgram = new AutoBarrelRace();
			mAutoProgram.start();
			break;
		case AutoBouncePath:
			mAutoProgram = new AutoBouncePath();
			mAutoProgram.start();
			break;	
		case threeBasicBalls:
			mAutoProgram = new AutonBasic3BallOffLine();
			mAutoProgram.start(robotPosition);
			break;
		case sixBallRight:
			mAutoProgram = new AutonAllTheWayRight6Ball();
			mAutoProgram.start(robotPosition);
			break;
		case fiveBallsMiddle:
			mAutoProgram = new AutonMiddle5Balls();
			mAutoProgram.start(robotPosition);
			break;
		case sixBallsLeft:
			mAutoProgram = new AutonLeft6Balls();
			mAutoProgram.start(robotPosition);
			break;
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
		case AutoAtHomeBallPickUp:
			mAutoProgram = new AutoBallAtHomeChallenge();
			mAutoProgram.start();
			break;
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
		autoChooser.setDefaultOption(skillsOne, skillsOne);
		autoChooser.addOption(AutoSlalom, AutoSlalom);
		autoChooser.addOption(threeBasicBalls, threeBasicBalls);
		autoChooser.addOption(sixBallRight, sixBallRight);
		autoChooser.addOption(fiveBallsMiddle, fiveBallsMiddle);
		autoChooser.addOption(sixBallsLeft, sixBallsLeft);
		autoChooser.addOption(autoCalibrator, autoCalibrator);
		autoChooser.addOption(autoWheelAlign, autoWheelAlign);
		autoChooser.addOption(autoAlign, autoAlign);
		autoChooser.addOption(ballPickUp, ballPickUp);
		autoChooser.addOption(AutoBouncePath, AutoBouncePath);
		autoChooser.addOption(AutoAtHomeBallPickUp, AutoAtHomeBallPickUp);
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
		DriveTrain.allowTurnEncoderReset();
		DriveTrain.resetDriveEncoders();
		DriveTrain.resetTurnEncoders();

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

		VisionBall.GetBallLocations();

		
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
