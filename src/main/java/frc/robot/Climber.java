/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.revrobotics.CANSparkMax;
import com.revrobotics.SparkMaxPIDController;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.PneumaticsModuleType;

public class Climber {
	private static CANSparkMax climberMotor;
	private static CANSparkMax climberMotor2;
	private static SparkMaxPIDController climberPID;
	private static DoubleSolenoid climberSolenoid1;
	private static DoubleSolenoid climberSolenoid2;
	public static enum ClimberPosition {
		Straight, 
		OffCenter
	}
	private static enum Rung {
		LowRung, 
		MediumRung, 
		ExtendToNextRung,
		Retract,
	}
	private static Rung rungToClimb;
	private static boolean climbRung;
	private static double timer;
	private static double timeToClimb;
	
	public static void init() {
        climberMotor = new CANSparkMax(Wiring.CLIMBER_MOTOR_1, MotorType.kBrushless);
        climberMotor.restoreFactoryDefaults(); 
        climberMotor.setClosedLoopRampRate(0.5);
        climberMotor.setSmartCurrentLimit(20);
        climberMotor.setIdleMode(IdleMode.kBrake);
		climberMotor2 = new CANSparkMax(Wiring.CLIMBER_MOTOR_2, MotorType.kBrushless);
		climberMotor2.follow(climberMotor);
		
		climberPID.setP(Calibration.CLIMBER_MOTOR_D);
		climberPID.setI(Calibration.CLIMBER_MOTOR_I);
		climberPID.setD(Calibration.CLIMBER_MOTOR_D);
		climberPID.setIZone(Calibration.CLIMBER_MOTOR_IZONE);
		climberPID.setOutputRange(0, 50);

		climberPID.setSmartMotionMaxVelocity(2000, 0);
		climberPID.setSmartMotionMinOutputVelocity(0, 0);
		climberPID.setSmartMotionMaxAccel(1500, 0);
		climberPID.setSmartMotionAllowedClosedLoopError(0.5, 0);

		climberSolenoid1 = new DoubleSolenoid(PneumaticsModuleType.REVPH, Wiring.CLIMBER1_SOLENOID_FORWARD, Wiring.CLIMBER1_SOLENOID_REVERSE);
		climberSolenoid1 = new DoubleSolenoid(PneumaticsModuleType.REVPH, Wiring.CLIMBER2_SOLENOID_FORWARD, Wiring.CLIMBER2_SOLENOID_REVERSE);
	}
	public static void tick() {
		
	}

	public static void climber(double speed){
		if (climberMotor.getEncoder().getPosition() > 500 && speed > 0) {
			climberMotor.set(0);
		} else if (climberMotor.getEncoder().getPosition() <= 0 && speed < 0) {
			climberMotor.set(0);
		} else {
			climberMotor.set(speed);
		}
	}
	
	public static void climberStop() {
		climberMotor.set(0);
	}

	public static void climberPosition(ClimberPosition position) {
		switch(position) {
			case Straight:
				climberSolenoid1.set(DoubleSolenoid.Value.kReverse);
				climberSolenoid2.set(DoubleSolenoid.Value.kReverse);
				break; 
			case OffCenter:
				climberSolenoid2.set(DoubleSolenoid.Value.kForward);
				climberSolenoid2.set(DoubleSolenoid.Value.kForward);
				break;
		}
	}

	public static void climbTo(Rung rung) { 
		// rungToClimb = rung;
		// climbRung = true;
		double encoderDistance = 0;
		double power = 0;
		switch(rung) {
			case LowRung:
				climberPID.setReference(400, CANSparkMax.ControlType.kSmartMotion);
				break;
			case MediumRung:
				climberPID.setReference(500, CANSparkMax.ControlType.kSmartMotion);
				break;
			case ExtendToNextRung:
				climberPID.setReference(500, CANSparkMax.ControlType.kSmartMotion);
				break;
			case Retract:
				climberPID.setReference(0, CANSparkMax.ControlType.kSmartMotion);
				break;
		}
	}
}