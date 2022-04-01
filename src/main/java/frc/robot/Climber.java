/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import javax.lang.model.util.ElementScanner6;

import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.revrobotics.CANSparkMax;
import com.revrobotics.MotorFeedbackSensor;
import com.revrobotics.SparkMaxPIDController;
import com.revrobotics.CANSparkMax.ControlType;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.PneumaticsModuleType;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.Shooter.ShooterPosition;

public class Climber {
	private static CANSparkMax climberMotor;
	private static CANSparkMax climberMotor2;
	private static SparkMaxPIDController climber1PID;
	private static SparkMaxPIDController climber2PID;
	private static DoubleSolenoid climberSolenoid;
	private final static double MAX_EXTENSION_VERTICAL = 76.5;
	private final static double MAX_EXTENSION_BACK = 105;
	private final static double MAX_RETRACTED = -2;
	private static ClimberPosition currentClimberPosition;
	public static enum ClimberPosition {
		Straight, 
		Back
	}
	public static enum Rung {
		LowRung, 
		MediumRung, 
		ExtendToNextRung,
		Retract,
		UpLittle, 
	}
	private static Rung rungToClimb;
	private static boolean climbRung;
	private static double timer;
	private static double timeToClimb;
	private static double lastPositionRequested = 0;
	private static boolean climbStarted = false;
	public static enum Motor {
		LeftMotor,
		RightMotor,
	}

	public static void init() {
        climberMotor = new CANSparkMax(Wiring.CLIMBER_MOTOR_1, MotorType.kBrushless);
        climberMotor.restoreFactoryDefaults(); 
        climberMotor.setClosedLoopRampRate(0.5);
        climberMotor.setSmartCurrentLimit(60);
        climberMotor.setIdleMode(IdleMode.kBrake);
		climberMotor.setInverted(true);

		climberMotor2 = new CANSparkMax(Wiring.CLIMBER_MOTOR_2, MotorType.kBrushless);
		climberMotor2.restoreFactoryDefaults();
		climberMotor2.setClosedLoopRampRate(0.5);
        climberMotor2.setSmartCurrentLimit(60);
		climberMotor2.setIdleMode(IdleMode.kBrake);
		climberMotor2.setInverted(false);

		
		climber1PID = climberMotor.getPIDController();
		climber2PID = climberMotor2.getPIDController();
		
		climber1PID.setP(Calibration.CLIMBER_MOTOR_P);
		climber1PID.setI(Calibration.CLIMBER_MOTOR_I);
		climber1PID.setD(Calibration.CLIMBER_MOTOR_D);
		climber1PID.setIZone(Calibration.CLIMBER_MOTOR_IZONE);
		
		climber2PID.setP(Calibration.CLIMBER_MOTOR_P);
		climber2PID.setI(Calibration.CLIMBER_MOTOR_I);
		climber2PID.setD(Calibration.CLIMBER_MOTOR_D);
		climber2PID.setIZone(Calibration.CLIMBER_MOTOR_IZONE);

		climber1PID.setOutputRange(-1, 1);
		climber2PID.setOutputRange(-1, 1);

		climber1PID.setSmartMotionMaxVelocity(25, 0);
		climber2PID.setSmartMotionMaxVelocity(25, 0);

		climber1PID.setSmartMotionMinOutputVelocity(0, 0);
		climber2PID.setSmartMotionMinOutputVelocity(0, 0);

		climber1PID.setSmartMotionMaxAccel(10, 0);
		climber2PID.setSmartMotionMaxAccel(10, 0);

		climber1PID.setSmartMotionAllowedClosedLoopError(0.5, 0);
		climber2PID.setSmartMotionAllowedClosedLoopError(0.5, 0);

		climberSolenoid = new DoubleSolenoid(PneumaticsModuleType.REVPH, Wiring.CLIMBER_SOLENOID_FORWARD, Wiring.CLIMBER_SOLENOID_REVERSE);

		currentClimberPosition = ClimberPosition.Straight;
	}

	public static void tick() {
		SmartDashboard.putNumber("Climber1 Position Actual", climberMotor.getEncoder().getPosition());
		SmartDashboard.putNumber("Climber2 Position Actual", climberMotor2.getEncoder().getPosition());
	}

	public static void move(double speed){
		if (climberMotor.getEncoder().getPosition() <= 0  && speed > 0.2) {
			climberMotor.set(-.2); // allow them to very slowly retract to allow for manual pull in 
			                       // after powering off in the extended position
			climberMotor2.set(-.2);
		} else if (climberMotor.getEncoder().getPosition() >= getMaxExtension() && speed < .2) {
			climberMotor.set(.20);
			climberMotor2.set(.20);
		}
		else if ((climberMotor.getEncoder().getPosition() <= 0  && speed > 0)) {
			climberMotor.set(0);
			climberMotor2.set(0);
		}
		else if (climberMotor.getEncoder().getPosition() >= getMaxExtension() && speed < 0) {
			climberMotor.set(0);
			climberMotor2.set(0);
		} else {
			climberMotor.set(-speed);
			climberMotor2.set(-speed);
		}
	}

	public static void moveV2(double direction) {
		if (Math.abs(direction) < 0.1) {
			return;
		} else {
			// direction is between -1 and 1 indicating the direction to manually move
			double movementFactor = 1.3;

			double newPosition = lastPositionRequested + (movementFactor * -direction);
			
			if (newPosition < MAX_RETRACTED) {
				newPosition = MAX_RETRACTED;
			} else if (newPosition > getMaxExtension()) {
				newPosition = getMaxExtension();
			}

			lastPositionRequested = newPosition;

			SmartDashboard.putNumber("Climber Position Requested", lastPositionRequested);

			climberMotor.getPIDController().setReference(lastPositionRequested, ControlType.kPosition);
			climberMotor2.getPIDController().setReference(lastPositionRequested, ControlType.kPosition);
		}
	}

	public static void moveV3(double direction, Motor motor) {
		// direction is between -1 and 1 indicating the direction to manually move
		double movementFactor = 1.3;

		double newPosition = lastPositionRequested + (movementFactor * -direction);
		
		if (newPosition < 0) {
			newPosition = 0;
		} else if (newPosition > getMaxExtension()) {
			newPosition = getMaxExtension();
		}

		lastPositionRequested = newPosition;

		SmartDashboard.putNumber("Climber Position Requested", lastPositionRequested);
		switch (motor) {
			case LeftMotor:
				climberMotor.getPIDController().setReference(lastPositionRequested, ControlType.kPosition);
				break;
			case RightMotor:
				climberMotor2.getPIDController().setReference(lastPositionRequested, ControlType.kPosition);
				break;
		}
	}

	public static void climberStop() {
		climberMotor.set(0);
		climberMotor2.set(0);
	}

	public static void reset() {
		climberMotor.getEncoder().setPosition(0);
		climberMotor2.getEncoder().setPosition(0);
		lastPositionRequested = 0;
		climbStarted = false;
	}

	public static void climberPosition(ClimberPosition position) {
		switch(position) {
			case Straight:
				climberSolenoid.set(DoubleSolenoid.Value.kReverse);
				currentClimberPosition = position;
				break; 
			case Back:
				climberSolenoid.set(DoubleSolenoid.Value.kForward);
				currentClimberPosition = position;
				climbStarted = true;
				Shooter.setShooterPosition(ShooterPosition.Medium);
				break;
		}
	}

	private static double getMaxExtension() {
		if (currentClimberPosition == ClimberPosition.Back || climbStarted) {
			return MAX_EXTENSION_BACK;
		} else	
			return MAX_EXTENSION_VERTICAL;
	}

	public static void climbTo(Rung rung) { 
		// rungToClimb = rung;
		// climbRung = true;
		switch(rung) {
			case LowRung:
				lastPositionRequested = 50;
				break;
			case MediumRung:
				lastPositionRequested = 60; // not right
				break;
			case ExtendToNextRung:
				lastPositionRequested = MAX_EXTENSION_BACK;
				break;
			case Retract:
				lastPositionRequested = MAX_RETRACTED;
				break;
			case UpLittle:
				lastPositionRequested = 20;
			}

		climber1PID.setReference(lastPositionRequested, CANSparkMax.ControlType.kPosition);
		climber2PID.setReference(lastPositionRequested, CANSparkMax.ControlType.kPosition);
	}
}