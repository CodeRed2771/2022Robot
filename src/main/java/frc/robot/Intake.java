package frc.robot;

import java.util.ResourceBundle.Control;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.StatusFrameEnhanced;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.libs.CurrentBreaker;

public class Intake {

    private static CANSparkMax intakeMotor;
    private static WPI_TalonSRX intakeDeployMotor;
    public static CurrentBreaker currentBreaker;
    private static boolean running = false;

    public static void init() {
        intakeDeployMotor = new WPI_TalonSRX(Wiring.INTAKE_DEPLOY_MOTOR_ID);
        intakeMotor = new CANSparkMax(Wiring.INTAKE_MOTOR_ID, MotorType.kBrushless);
        intakeMotor.restoreFactoryDefaults(); 
        intakeMotor.setClosedLoopRampRate(0.5);
        intakeMotor.setSmartCurrentLimit(20);
        intakeMotor.setIdleMode(IdleMode.kBrake);

        intakeDeployMotor.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative,0,0);

        intakeDeployMotor.setStatusFramePeriod(StatusFrameEnhanced.Status_13_Base_PIDF0,10,0);
        intakeDeployMotor.setStatusFramePeriod(StatusFrameEnhanced.Status_10_MotionMagic, 10,0);

        intakeDeployMotor.configNominalOutputForward(0,0);
        intakeDeployMotor.configNominalOutputReverse(0,0);
        intakeDeployMotor.configPeakOutputForward(.3,0);
        intakeDeployMotor.configPeakOutputReverse(-.3,0);
        intakeDeployMotor.setNeutralMode(NeutralMode.Brake);

        intakeDeployMotor.configClosedloopRamp(.25,0);

        intakeDeployMotor.selectProfileSlot(0, 0);
        intakeDeployMotor.config_kF(0, 0,0);
        intakeDeployMotor.config_kP(0, 5, 0);
        intakeDeployMotor.config_kI(0, 0, 0);
        intakeDeployMotor.config_kD(0, 50,0);

        intakeDeployMotor.configMotionCruiseVelocity(10000,0);
        intakeDeployMotor.configMotionAcceleration(7800, 0);

        int startingPosition = calculateRelativePosition(getAbsolutePosition(),Calibration.INTAKE_DEPLOY_ZERO_INITIAL);
        // SmartDashboard.putNumber("SP", startingPosition);
        intakeDeployMotor.set(ControlMode.PercentOutput,0);
        intakeDeployMotor.setSelectedSensorPosition(startingPosition,0,0);
    }

    public static void tick() {
        // SmartDashboard.putNumber("Intake Deploy Encoder", intakeDeployMotor.getSelectedSensorPosition(0));
        // SmartDashboard.putNumber("Intake ABS", intakeDeployMotor.getSensorCollection().getPulseWidthPosition());
        SmartDashboard.putNumber("Intake ABS", getAbsolutePosition());
        
    }

    public static void move(double speed) {
        intakeDeployMotor.set(ControlMode.PercentOutput, speed);
    }

    public static void moveSetpoint(double direction) {
        double newSetpoint;
        
        if(direction > 0) {
            newSetpoint = intakeDeployMotor.getSelectedSensorPosition(0)+40;
            if(newSetpoint>=0) {
                newSetpoint=0;
            }
        } else {
            newSetpoint = intakeDeployMotor.getSelectedSensorPosition(0)-40;
            if(newSetpoint < -4300) {
                newSetpoint = -4300;
            }
        }
        intakeDeployMotor.set(ControlMode.MotionMagic,newSetpoint);
    }

    public static void deployIntake() {
        intakeDeployMotor.set(ControlMode.MotionMagic,2048);
    }

    public static void retractIntake() {
        intakeDeployMotor.set(ControlMode.MotionMagic,0);
    }

    public static void startIntake() {
        deployIntake();
        intakeMotor.set(.5);
        running = true; 
    }
    
    public static void reverseIntake() {
        deployIntake();
        intakeMotor.set(-.5);
        running = true; 
    }

    public static void stopIntake() {
        intakeMotor.set(0);
        running = false; 
    }

    public static boolean isRunning() {
        return running;
    }

    public static int getAbsolutePosition() {
		int encPos = intakeDeployMotor.getSensorCollection().getPulseWidthPosition() % 4096;

		if (encPos < 0)
			encPos = encPos + 4096; // e.g. -4090 = 6

            return encPos;
	}

    private static int calculateRelativePosition(int currentPosition, int calibrationZeroPosition) {
        // 4096 is one revolution
        if (currentPosition - calibrationZeroPosition >= 0) {
            return currentPosition - calibrationZeroPosition;
        } else {
            return (4096 - calibrationZeroPosition) + currentPosition;
        }
    }
}