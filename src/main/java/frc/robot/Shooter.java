/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.TalonFX;

import edu.wpi.first.wpilibj.Servo;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import java.util.Timer;


public class Shooter {
    private static Shooter instance;
    // private static TalonSRX shooterMotor = new TalonSRX(Wiring.SHOOTER>MOTOR_ID);
    private static TalonFX shooterMotor = new TalonFX(Wiring.SHOOTER_MOTOR_ID);
    private static Servo gate = new Servo(Wiring.SERVO_PWM_ID);
    // private static Timer timer = new Timer();
    // private static TalonFX shooterMotor1 = new TalonFX(Wiring.SHOOTER1_MOTOR_ID);
    private static boolean isEnabled = false;
    private static boolean isGateOpen = false;
    private static boolean oneShot = false;
    private static boolean continuousShooting = false;
    private static int timer = 0;
    private static double targetSpeed = Calibration.SHOOTER_DEFAULT_SPEED;
    private static final int kPIDLoopIdx = 0;
    private static int timerCurrent = 0;
    private static double adjustmentFactor = 1;
    private static double desiredShootSpeed = Calibration.SHOOTER_DEFAULT_SPEED * adjustmentFactor;
    private static double currentShooterSpeed = 0;


    public Shooter() {
        // shooterMotor.set(shooterMotor1.follower, Wiring.SHOOTER_MOTOR_ID);
        // shooterMotor1.follow(shooterMotor);
        shooterMotor.configFactoryDefault(10);
        shooterMotor.setInverted(true);
        shooterMotor.setSensorPhase(false);
       
		/* set the relevant frame periods to be at least as fast as periodic rate */
		// shooterMotor.setStatusFramePeriod(StatusFrameEnhanced.Status_13_Base_PIDF0, 10, 0);
        // shooterMotor.setStatusFramePeriod(StatusFrameEnhanced., 10, 0);

		/* set the peak and nominal outputs */
		shooterMotor.configNominalOutputForward(0, 0);
		shooterMotor.configNominalOutputReverse(0, 0);
		shooterMotor.configPeakOutputForward(1, 0);
		shooterMotor.configPeakOutputReverse(-1, 0);
		shooterMotor.setNeutralMode(NeutralMode.Coast);
        shooterMotor.configSelectedFeedbackSensor(FeedbackDevice.IntegratedSensor, kPIDLoopIdx, 0);

		shooterMotor.configClosedloopRamp(.45, 0);
		/* set closed loop gains in slot0 - see documentation */
        shooterMotor.selectProfileSlot(0, 0);


		shooterMotor.config_kP(kPIDLoopIdx, Calibration.SHOOTER_P, 0);
		shooterMotor.config_kI(kPIDLoopIdx, Calibration.SHOOTER_I, 0);
		shooterMotor.config_kD(kPIDLoopIdx, Calibration.SHOOTER_D, 0);
        shooterMotor.config_kF(kPIDLoopIdx, Calibration.SHOOTER_F, 0);
		/* zero the sensor */
        shooterMotor.setSelectedSensorPosition(0, kPIDLoopIdx, 0);
 	
		// Current limit
        // shooterMotor.configContinuousCurrentLimit(25);
        // shooterMotor1.configContinuousCurrentLimit(25);
        // MAY NEED THAT LATER
        
        SmartDashboard.putNumber("Shoot P", Calibration.SHOOTER_P);
		SmartDashboard.putNumber("Shoot I", Calibration.SHOOTER_I);
		SmartDashboard.putNumber("Shoot D", Calibration.SHOOTER_D);
        SmartDashboard.putNumber("Shoot F", Calibration.SHOOTER_F);
        SmartDashboard.putNumber("Shoot Setpoint", Calibration.SHOOTER_DEFAULT_SPEED);
        SmartDashboard.putNumber("Adjustment Factor", adjustmentFactor);

        closeGate();

        // feederMotor.setNeutralMode(NeutralMode.Brake);
    }

    public static Shooter getInstance() {
        if (instance == null)
            instance = new Shooter();
        return instance;
    }

    public static void tick() {

        // if (isEnabled && isAtSpeed()) {
        //     feederMotor.set(ControlMode.PercentOutput,1);
        // } else {
        //     feederMotor.set(ControlMode.PercentOutput,0);
        // }

        if (SmartDashboard.getBoolean("Shooter TUNE", true)) {
			shooterMotor.config_kF(kPIDLoopIdx, SmartDashboard.getNumber("Shoot F", 0), 0);
			shooterMotor.config_kP(kPIDLoopIdx, SmartDashboard.getNumber("Shoot P", 0), 0);
			shooterMotor.config_kI(kPIDLoopIdx, SmartDashboard.getNumber("Shoot I", 0), 0);
            shooterMotor.config_kD(kPIDLoopIdx, SmartDashboard.getNumber("Shoot D", 0), 0);
        }

            if (isEnabled) {
                shooterMotor.set(ControlMode.Velocity, SmartDashboard.getNumber("Shoot Setpoint", Calibration.SHOOTER_DEFAULT_SPEED) * adjustmentFactor);
                SmartDashboard.putNumber("SHOOTER VELOCITY", shooterMotor.getSelectedSensorVelocity());

                System.out.println(timer);

                if (oneShot) {
                    timer += 1; // ONE TIMER UNIT EQUALS ABOUT 20 MILLISECONDS
                    openGate();
                    if (timer >= 3) {
                        closeGate();
                        resetTimer();
                        oneShot = false;
                    }
                }

                if (continuousShooting) {
                    timer += 1; // ONE TIMER UNIT EQUALS ABOUT 20 MILLISECONDS
                    openGate();
                    if (timer >= 200) {
                        closeGate();
                        resetTimer();
                        continuousShooting = false;
                    }
                }
                
            }
        
        
        SmartDashboard.putNumber("Shoot Enc", shooterMotor.getSensorCollection().getIntegratedSensorVelocity());
        SmartDashboard.putNumber("Shoot Err", shooterMotor.getClosedLoopError());
        SmartDashboard.putBoolean("Is At Speed", isAtSpeed());
    }

    public static void StartShooter() {
        isEnabled = true;
        oneShot = false;
        continuousShooting = false;
    }

    public static boolean isShooterEnabled () {
        return isEnabled;
    }

    public static void StopShooter() {
        isEnabled = false;
        closeGate();
        ShooterPivoter.resetPivoter();
        oneShot = false;
        continuousShooting = false;
        resetTimer();
        shooterMotor.set(ControlMode.PercentOutput,0);
    }

    public static void setAdjustmentFactor (double adjustmentFactor) {
        adjustmentFactor = (((adjustmentFactor + 1) / 2) + 2) * 0.4;
        Shooter.adjustmentFactor = adjustmentFactor;
        System.out.println(Shooter.adjustmentFactor);
    } 

    public static double getShooterSpeed() {
        // return shooterMotor.getSensorCollection().getIntegratedSensorVelocity();
        return shooterMotor.getSelectedSensorVelocity();
    }

    public static boolean isAtSpeed() {
        return (getShooterSpeed() > 0 && Math.abs(getShooterSpeed() - targetSpeed) < 300);
    }

    public static void closeGate () {
       
        if (Calibration.isPracticeBot()) {
            gate.set(0.8); // PRACTICE
        } else {
            gate.set(0.45); // COMP BOT
        }
       
        isGateOpen = false;
    }

    public static void openGate () {
        if (Calibration.isPracticeBot()) {
            gate.set(0.50); // PRACTICE
        } else {
            gate.set(0.31); // COMP BOT
        }
       
        isGateOpen = true;
    }

    public static void oneShot () {
        oneShot = true;
        continuousShooting = false;
    }

    public static void continuousShooting () {
        continuousShooting = true;
        oneShot = false;
    }

    public static boolean isGateOpen () {
        return isGateOpen;
    }

    public static void resetTimer () {
        timer = 0;
    }

    public static void stopTimer () {
        
    }



}

