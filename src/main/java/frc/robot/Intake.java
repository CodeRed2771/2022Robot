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
        intakeDeployMotor.configNominalOutputForward(0,0);
        intakeDeployMotor.configPeakOutputForward(1,0);
        intakeDeployMotor.configPeakOutputReverse(-1,0);
        intakeDeployMotor.setNeutralMode(NeutralMode.Brake);

        intakeDeployMotor.configClosedloopRamp(.25,0);

        intakeDeployMotor.selectProfileSlot(0, 0);
        intakeDeployMotor.config_kF(0, 0,0);
        intakeDeployMotor.config_kP(0, 0.5, 0);
        intakeDeployMotor.config_kI(0, 0, 0);
        intakeDeployMotor.config_kD(0, 50,0);

        intakeDeployMotor.configMotionCruiseVelocity(5000,0);
        intakeDeployMotor.configMotionAcceleration(4800, 0);

        intakeDeployMotor.setSelectedSensorPosition(0,0,0);
    }

    public static void move(double speed) {
        intakeDeployMotor.set(ControlMode.PercentOutput, speed);
    }

    public static void moveSetpoint(double direction) {
        double newSetpoint;
        
        if(direction > 0) {
            newSetpoint = intakeDeployMotor.getSelectedSensorPosition(0)+4000;
            if(newSetpoint>=0) {
                newSetpoint=0;
            }
        } else {
            newSetpoint = intakeDeployMotor.getSelectedSensorPosition(0)-4000;
            if(newSetpoint < -43000) {
                newSetpoint = -43000;
            }
        }
        intakeDeployMotor.set(ControlMode.MotionMagic,newSetpoint);
    }

    public static void deployIntake() {
        intakeDeployMotor.set(ControlMode.MotionMagic,50);
    }

    public static void retractIntake() {
        intakeDeployMotor.set(ControlMode.MotionMagic,-50);
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

}