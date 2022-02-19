package frc.robot;

import com.ctre.phoenix.motorcontrol.*;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.revrobotics.CANSparkMax;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.SparkMaxPIDController;
import com.revrobotics.SparkMaxRelativeEncoder;
import com.revrobotics.CANSparkMax.ControlType;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

public class SwerveModuleNEO implements SwerveModule {
    public CANSparkMax drive;
    public WPI_TalonSRX turn;
    private final SparkMaxPIDController drivePID;
    private final RelativeEncoder driveEncoder;
	private char mModuleID;
	private final int FULL_ROTATION = 4096;
	private double TURN_P, TURN_I, TURN_D, DRIVE_P, DRIVE_I, DRIVE_D;
	private final int TURN_IZONE, DRIVE_IZONE;
	private double turnZeroPos = 0;
	private double currentDriveSetpoint = 0;
	private boolean isReversed = false;

	/**
	 * Lets make a new module :)
	 * 
	 * @param driveTalonID First I gotta know what talon we are using for driving
	 * @param turnTalonID  Next I gotta know what talon we are using to turn
	 * @param tP           I probably need to know the P constant for the turning
	 *                     PID
	 * @param tI           I probably need to know the I constant for the turning
	 *                     PID
	 * @param tD           I probably need to know the D constant for the turning
	 *                     PID
	 * @param tIZone       I might not need to know the I Zone value for the turning
	 *                     PID
	 */
	public SwerveModuleNEO(int driveMotorID, int turnTalonID, double dP, double dI, double dD, int dIZone, double tP, double tI,
			double tD, int tIZone, double tZeroPos, char moduleID) {

        drive = new CANSparkMax(driveMotorID, MotorType.kBrushless);
        drive.restoreFactoryDefaults();
        drive.setOpenLoopRampRate(.1);
        drive.setSmartCurrentLimit(40);
        drive.setIdleMode(IdleMode.kBrake);
        
        mModuleID = moduleID;

	  /**
         * In order to use PID functionality for a controller, a CANPIDController object
         * is constructed by calling the getPIDController() method on an existing
         * CANSparkMax object
         */
        drivePID = drive.getPIDController();

        // Encoder object created to display position values
        driveEncoder = drive.getEncoder();

        DRIVE_P = dP;
        DRIVE_I = dI;
        DRIVE_D = dD;
        DRIVE_IZONE = dIZone;

        drivePID.setP(DRIVE_P);
        drivePID.setI(DRIVE_I);
        drivePID.setD(DRIVE_D);
        drivePID.setIZone(DRIVE_IZONE);
        drivePID.setFF(0);
        drivePID.setOutputRange(-1, 1);

        drivePID.setSmartMotionMaxVelocity(Calibration.DT_MM_VELOCITY, 0);
        drivePID.setSmartMotionMaxAccel(Calibration.DT_MM_ACCEL, 0);

        // TURN

		turn = new WPI_TalonSRX(turnTalonID);
		turn.configFactoryDefault(10);

		turnZeroPos = tZeroPos;

		turn.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder, 0, 0); // ?? don't know if zeros are right
		TURN_P = tP;
		TURN_I = tI;
		TURN_D = tD;
		TURN_IZONE = tIZone;

		turn.config_kP(0, TURN_P, 0);
		turn.config_kI(0, TURN_I, 0);
		turn.config_kD(0, TURN_D, 0);
		turn.config_IntegralZone(0, TURN_IZONE, 0);
		turn.selectProfileSlot(0, 0);
		turn.configClosedloopRamp(.1, 0);
		//turn.setSensorPhase(true);
		
	}

	// public void setFollower(int talonToFollow) {
	// 	if (talonToFollow != 0) {
	// 		drive.set(ControlMode.Follower, talonToFollow);
	// 	} else
	// 		drive.set(ControlMode.Velocity, 0);
	// }

    public void setDriveMMAccel(final int accel) {
        drivePID.setSmartMotionMaxAccel(accel, 0);
    }

    public void setDriveMMVelocity(final int velocity) {
        drivePID.setSmartMotionMaxVelocity(velocity, 0);
    }

	// public double getDriveVelocity() {
	// 	return drive.getSelectedSensorVelocity(0);
	// }

	/**
	 * getModuleLetters
	 * @return a single character, A,B,C,D indicating which module this is
	 */
	public char getModuleLetter() {
		return mModuleID;
	}

	/**
	 * Setting turn motor power
	 * 
	 * @param p value from -1 to 1
	 */
	public void setTurnPower(double p) {
		this.turn.set(ControlMode.PercentOutput, p);
	}

	/**
	 * Setting drive motor power
	 * 
	 * @param p value from -1 to 1
	 */
	public void setDrivePower(double p) {
		this.drive.set((isReversed ? -1 : 1) * p);
	}

	/**
	 * Getting the turn encoder position (not absolute)
	 * 
	 * @return turn encoder position
	 */
	public double getTurnRelativePosition() {
		return turn.getSelectedSensorPosition(0);
	}

	/**
	 * Gets the absolute encoder position for the turn encoder It will be a value
	 * between 0 and 1
	 * 
	 * @return turn encoder absolute position
	 */
	public double getTurnAbsolutePosition() {
		return (turn.getSensorCollection().getPulseWidthPosition() & 0xFFF) / 4096d;
	}

	public double getTurnPosition() {
		// returns the 0 to 1 value of the turn position
		// uses the calibration value and the actual position
		// to determine the relative turn position

		double currentPos = getTurnAbsolutePosition();
		if (currentPos - turnZeroPos > 0) {
			return currentPos - turnZeroPos;
		} else {
			return (1 - turnZeroPos) + currentPos;
		}
	}

	public double getTurnAngle() {
		// returns the angle in -180 to 180 range
		double turnPos = getTurnPosition();
		if (turnPos > .5) {
			return (360 - (turnPos * 360));
		} else
			return turnPos * 360;
	}

	public boolean modulesReversed() {
		return isReversed;
	}

	public void unReverseModule() {
		isReversed = false;
	}

	public void resetTurnEnc() {
		this.turn.getSensorCollection().setQuadraturePosition(0, 10);
	}

	public double getDriveEnc() {
		return driveEncoder.getPosition();
	}

	public void resetDriveEnc() {
        this.driveEncoder.setPosition(0);
	}

	public void setEncPos(int d) {
		turn.getSensorCollection().setQuadraturePosition(d, 10);
	}

	/**
	 * Is electrical good? Probably not.... Is the turn encoder connected?
	 * 
	 * @return true if the encoder is connected
	 */
	public boolean isTurnEncConnected() {
		// return turn.isSensorPresent(FeedbackDevice.CtreMagEncoder_Relative) ==
		// FeedbackDeviceStatus.FeedbackStatusPresent;
		return true; // didn't immediately see a compatible replacement
	}

	public int getTurnRotations() {
		return (int) (turn.getSelectedSensorPosition(0) / FULL_ROTATION);
	}

	public double getTurnOrientation() {
		return (turn.getSelectedSensorPosition(0) % FULL_ROTATION) / FULL_ROTATION;

		// SmartDashboard.putNumber("module-a-" + this.hashCode(),
		// turn.getSelectedSensorPosition(0));
		// SmartDashboard.putNumber("module-b-" + this.hashCode(),
		// turn.getSelectedSensorPosition(0) % FULL_ROTATION);
		// SmartDashboard.putNumber("module-c-" + this.hashCode(),
		// (turn.getSelectedSensorPosition(0) % FULL_ROTATION) / FULL_ROTATION);

	}

    public double getCurrentDriveSetpoint() {
        return currentDriveSetpoint;
    }

    // These are used for driving and turning in auto.
    public void setDrivePIDToSetPoint(final double setpoint) {
        currentDriveSetpoint = setpoint;
        drivePID.setReference(setpoint, ControlType.kSmartMotion);
    }

    public boolean hasDriveCompleted(final double allowedError) {
        return Math.abs(currentDriveSetpoint - getDriveEnc()) <= allowedError;
    }
    
	public boolean hasDriveCompleted() {
		return hasDriveCompleted(0);
	}

	public void setTurnPIDToSetPoint(double setpoint) {
		turn.set(ControlMode.Position, setpoint);
	}

	public void setTurnOrientation(double position) {
		setTurnOrientation(position, true);
	}

	/**
	 * Set turn to pos from 0 to 1 using PID
	 * 
	 * @param reqPosition orientation to set to
	 */
	public void setTurnOrientation(double reqPosition, boolean optimize) {
		int base = getTurnRotations() * FULL_ROTATION;
		double currentTurnPosition = getTurnPosition();
		double reverseTurnPosition = (reqPosition + 0.5) % 1.0;
		double distanceToNormalPosition;
		double distanceToReversePosition;
		double closestTurnPosition = 0; // closest to currentTurnPosition
		double turnRelativePosition = getTurnRelativePosition();
		// double distanceToNormalPosition = Math.abs(currentTurnPosition - position);
		// double distanceToReversePosition = Math.abs(currentTurnPosition -
		// reverseTurnPosition);

		if (currentTurnPosition - reqPosition >= 0)
			if (currentTurnPosition - reqPosition > .5)
				distanceToNormalPosition = (1 - currentTurnPosition) + reqPosition;
			else
				distanceToNormalPosition = currentTurnPosition - reqPosition;
		else if (reqPosition - currentTurnPosition > .5)
			distanceToNormalPosition = (1 - reqPosition) + currentTurnPosition;
		else
			distanceToNormalPosition = reqPosition - currentTurnPosition;

		// note - this part could be eliminated because the distance to reverse
		// is a simple calculation based on the distance of the normal way.
		// I believe it would be just 1 - distance to Normal
		// if normal is .7, reverse would be .3 (1 - .7)
		// if normal is .3, reverse would be .7 (1 - .3)
		// this next line didn't work for some reason....
		// distanceToReversePosition = 1 - distanceToNormalPosition;
		if (currentTurnPosition - reverseTurnPosition >= 0)
			if (currentTurnPosition - reverseTurnPosition > .5)
				distanceToReversePosition = (1 - currentTurnPosition) + reverseTurnPosition;
			else
				distanceToReversePosition = currentTurnPosition - reverseTurnPosition;
		else if (reverseTurnPosition - currentTurnPosition > .5)
			distanceToReversePosition = (1 - reverseTurnPosition) + currentTurnPosition;
		else
			distanceToReversePosition = reverseTurnPosition - currentTurnPosition;

		if (optimize) {
			closestTurnPosition = distanceToReversePosition < distanceToNormalPosition ? reverseTurnPosition
					: reqPosition;
		} else
			closestTurnPosition = reqPosition;

		isReversed = (closestTurnPosition != reqPosition);

		if (turnRelativePosition >= 0) {
			if ((base + (closestTurnPosition * FULL_ROTATION)) - turnRelativePosition < -FULL_ROTATION / 2) {
				base += FULL_ROTATION;
			} else if ((base + (closestTurnPosition * FULL_ROTATION)) - turnRelativePosition > FULL_ROTATION / 2) {
				base -= FULL_ROTATION;
			}
			
			turn.set(ControlMode.Position, (((closestTurnPosition * FULL_ROTATION) + (base))));
		} else {
			if ((base - ((1 - closestTurnPosition) * FULL_ROTATION)) - turnRelativePosition < -FULL_ROTATION / 2) {
				base += FULL_ROTATION;
			} else if ((base - ((1 - closestTurnPosition) * FULL_ROTATION)) - turnRelativePosition > FULL_ROTATION
					/ 2) {
				base -= FULL_ROTATION;
			}
			
			turn.set(ControlMode.Position, (base - (((1 - closestTurnPosition) * FULL_ROTATION))));
		}
	}

	// private void showDetailsOnDash(int base, int turnRelative, double
	// currentTurnPosition, double requestedPosition,
	// double reverseTurnPos, double distNormal, double distReverse, double
	// closestTurn, boolean optimize,
	// int newSetpoint) {
	// if (mModuleID == 'B') {
	// System.out.println("CurrentTurn: " + currentTurnPosition + " Req Pos: " +
	// requestedPosition + " dist Norm: "
	// + distNormal + " dist Rev: " + distReverse);
	// SmartDashboard.putNumber("AAA Base", base);
	// SmartDashboard.putNumber("AAA turnRel", turnRelative);
	// SmartDashboard.putNumber("AAA req pos", requestedPosition);
	// SmartDashboard.putNumber("AAA cur pos", currentTurnPosition);
	// SmartDashboard.putNumber("AAA revPos", reverseTurnPos);
	// SmartDashboard.putNumber("AAA distnorm", distNormal);
	// SmartDashboard.putNumber("AAA distrev", distReverse);
	// SmartDashboard.putBoolean("AAA reversed", isReversed);
	// SmartDashboard.putNumber("AAA closest", closestTurn);
	// SmartDashboard.putNumber("AAA new set", newSetpoint);
	// SmartDashboard.putBoolean("AAA optimize", optimize);
	// }
	// }

	public double getTurnError() {
		return turn.getClosedLoopError(0);
	}

	public double getTurnSetpoint() {
		return turn.getClosedLoopTarget(0);
	}

	// public double getDriveError() {
		// note that when using Motion Magic, the error is not what you'd expect
		// MM sets intermediate set points, so the error is just the error to
		// that set point, not to the final setpoint.
	// 	return drive.getClosedLoopError(0);
	// }

	public void stopDriveAndTurnMotors() {
		setDrivePower(0);
		setTurnPower(0);
	}

	public void stopDrive() {
		setDrivePower(0);
	}

    public void setBrakeMode(final boolean b) {
        drive.setIdleMode(b ? IdleMode.kBrake : IdleMode.kCoast);
    }

    public void setDrivePIDValues(final double p, final double i, final double d, final double f) {
        drivePID.setP(p);
        drivePID.setI(i);
        drivePID.setD(d);
        drivePID.setFF(f);
    }

	public void setTurnPIDValues(double p, double i, double d, double izone, double f) {
		turn.config_kP(0, p, 0);
        turn.config_kI(0, i, 0);
        turn.config_IntegralZone(0,  izone);
        turn.config_kD(0, d, 0);
        turn.config_kF(0, f, 0);
	}

}