/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import com.revrobotics.CANSparkMax;
import com.revrobotics.ControlType;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.wpilibj.BuiltInAccelerometer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Climber {

	private static boolean dropBellyPan = false;
	private static boolean pickUpBellyPan = false;
	private static Climber instance;
	private static BuiltInAccelerometer accelerometer = new BuiltInAccelerometer();
	private static CANSparkMax extenderMotor = new CANSparkMax(Wiring.EXTEND_MOTOR_ID, MotorType.kBrushless);
	private static CANSparkMax liftMotor = new CANSparkMax(Wiring.LIFT_MOTOR_ID, MotorType.kBrushless);

	public static final double BASE_EXTENDED_POSITION = 350; // tune for actual max extension revolutions
	public static final double MAX_EXTENDED_POSITION = 400; // needs adjusting

	public static boolean isLifting = false;
	public static double liftPositionWhenLiftingStarted = 0;
	public static double extPositionWhenLiftingStarted = 0;
	public static double targetExtenderPosition = 0;

	public Climber() {
		extenderMotor.restoreFactoryDefaults();
		extenderMotor.setIdleMode(IdleMode.kCoast);
		extenderMotor.getPIDController().setOutputRange(-.3, .3);
		extenderMotor.getPIDController().setP(1);

		liftMotor.restoreFactoryDefaults();
		liftMotor.setIdleMode(IdleMode.kBrake);
		liftMotor.getPIDController().setOutputRange(-1, 1);
		liftMotor.getPIDController().setP(1);
	}

	public static Climber getInstance() {
		if (instance == null) {
			instance = new Climber();
		}
		return instance;
	}

	public static void tick() {

		// we're actively climbing when we see acceleromter motion and the lift is at
		// least half way up.
		if (accelerometer.getY() > .1 && 
			!isLifting &&
			extenderMotor.getEncoder().getPosition() > (MAX_EXTENDED_POSITION / 2)) {

			isLifting = true;
			liftPositionWhenLiftingStarted = liftMotor.getEncoder().getPosition();  
			extPositionWhenLiftingStarted = extenderMotor.getEncoder().getPosition();
			targetExtenderPosition = extenderMotor.getEncoder().getPosition();
		}

		if (isLifting) {
			targetExtenderPosition = extPositionWhenLiftingStarted - (liftMotor.getEncoder().getPosition() - liftPositionWhenLiftingStarted );
			// coordinate the extender motor with the lift motor
			extenderMotor.getPIDController().setReference(targetExtenderPosition, ControlType.kPosition);
		}

		SmartDashboard.putNumber("Climb Extend Enc", extenderMotor.getEncoder().getPosition());
		SmartDashboard.putNumber("Climb Lift Enc", liftMotor.getEncoder().getPosition());

		SmartDashboard.putNumber("Accel X", accelerometer.getX());
		SmartDashboard.putNumber("Accel Y", accelerometer.getY());
		SmartDashboard.putNumber("Accel Z", accelerometer.getZ());

		SmartDashboard.putBoolean("IsLifting", isLifting);

	}

	// extendHook - use for first main extension, then manual adjustment from there
	public static void extendHook() {
		extenderMotor.getPIDController().setReference(BASE_EXTENDED_POSITION, ControlType.kPosition);
	}

	public static void adjustExtendedHook(double direction) {
		double newSetpoint;

		if (direction > 0) {
			newSetpoint = extenderMotor.getEncoder().getPosition() + (15);
			if (newSetpoint >= MAX_EXTENDED_POSITION) {
				newSetpoint = MAX_EXTENDED_POSITION;
			}
		} else {
			newSetpoint = extenderMotor.getEncoder().getPosition() - (15);
			if (newSetpoint < 0) {
				newSetpoint = 0;
			}
		}

		extenderMotor.getPIDController().setReference(newSetpoint, ControlType.kPosition);
	}

	public static void liftRobot(double direction) {
		double newSetpoint;

		if (direction > 0) {
			newSetpoint = liftMotor.getEncoder().getPosition() + (15);
		} else {
			newSetpoint = liftMotor.getEncoder().getPosition() - (15);
			if (newSetpoint < 0) {
				newSetpoint = 0;
			}
		}

		liftMotor.getPIDController().setReference(newSetpoint, ControlType.kPosition);
		extenderMotor.set(-.1);
	}

	// public static void setColorWheelClimberPosition () {
	// // GIVE SETPOINT FOR COLOR WHEEL CLIMBER POSITION
	// }

	// public static void setHighClimberPosition () {
	// // GIVE SETPOINT FOR HIGH CLIMBER POSITION
	// }
}
