/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.wpilibj.Encoder;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.InvertType;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DutyCycleEncoder;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.controller.PIDController;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

// ALL COMMENTED OUT CODE IS HERE FOR TESTING PURPOSES PLEASE DO NOT DELETE - IS

public class ShooterPivoter {

    private static ShooterPivoter instance;
    private static double encoderPosition;
    private static DutyCycleEncoder throughBore;
    private static boolean isConn;
    private static boolean limitGet;
    private static WPI_TalonSRX pivotMotor;
    private static PIDController positionPID;

    private static boolean shooterAtPosition = false;

    private static double lowShotPosition;
    private static double highShotPosition;
    private static double targetShaftPosition = highShotPosition;

    public ShooterPivoter () {
        pivotMotor = new WPI_TalonSRX(Wiring.SHOOTER_PIVOT_MOTOR_ID);
        pivotMotor.setInverted(InvertType.InvertMotorOutput);

        if (Calibration.isPracticeBot()) {
            lowShotPosition = .796; // forward position .... .828  PRACT
            highShotPosition = .837; // back position .... .877
        } else {
            lowShotPosition = .290; // forward position .... .828  COMP
            highShotPosition = .670; // back position .... .877
        }
        
        // NOTE - none of this current limiting seems to work.
        pivotMotor.configPeakCurrentDuration(200);
        pivotMotor.configPeakCurrentLimit(10);
        pivotMotor.configContinuousCurrentLimit(4);
        pivotMotor.enableCurrentLimit(true);

        throughBore = new DutyCycleEncoder(Wiring.SHOOTER_PIVOTER_DIO_ID); 
        throughBore.setConnectedFrequencyThreshold(900); 
        positionPID = new PIDController(20,0,0,20);
        positionPID.setTolerance(.075);
        // SmartDashboard.putNumber("SHOOTER SHAFT ADJUSTMENT", 0.5);

        //SmartDashboard.getNumber("ShootPivot pos", encoderPosition);
    }

    public static ShooterPivoter getInstance () {
        if (instance == null) {
            instance = new ShooterPivoter();
        }
        return instance;
    }

    public static void tick() {

        encoderPosition = getShaftEncoderPosition();

        // System.out.println("RAW COMMAND:" + targetShaftPosition);

        // if (encoderPosition >= maxPivotPosition + .02) {
        //     targetShaftPosition = maxPivotPosition;
        // } else if (encoderPosition <= minPivotPosition - .05) {
        //     targetShaftPosition = minPivotPosition;
        // }

        double calculatedPower = positionPID.calculate(encoderPosition,targetShaftPosition);

        // System.out.println("filtered command:" + targetShaftPosition);

        if (Math.abs(calculatedPower) > .8) {
            calculatedPower = .8 * Math.signum(calculatedPower);
        }

        if (encoderPosition > highShotPosition + .05 && calculatedPower > 0)
            calculatedPower = 0;
        if (encoderPosition < lowShotPosition - .05 && calculatedPower < 0) 
            calculatedPower = 0;
        if (encoderPosition == 0) // we probably lost the connection
            calculatedPower = 0;

        SmartDashboard.putNumber("ShootPivot pos", encoderPosition);
        SmartDashboard.putNumber("ShootPivot err", positionPID.getPositionError());
        SmartDashboard.putNumber("ShootPivot target",targetShaftPosition);
        SmartDashboard.putNumber("ShootPivot pwr", calculatedPower);
           
        pivotMotor.set(ControlMode.PercentOutput, calculatedPower);

    }

    private static double removeRotations(double encoderValue) {
        // if more than 1, remove the 1 or however many revolutions there were
        
        if (encoderValue < 0) 
            encoderValue = encoderValue * -1;

        encoderValue = (encoderValue * 1000 -  (((int) encoderValue) * 1000)) /1000;

        return encoderValue;
    }

    public static double getShaftEncoderPosition() {
        double encValue ;
        encValue = throughBore.get();  // e.g. 1.5 for 1 and half revolutions
        SmartDashboard.putNumber("ShootPivot RAW", encValue);
        
        if (encValue <= 0 || encValue >= 1)
            throughBore.reset();

        // if (encValue < 0) 
        //     encValue = encValue + 1; // make it positive
        // encValue = Math.abs(encValue);
        // // if more than 1, remove the 1 or however many revolutions there were
        // if (Math.abs(encValue)>=1) {
        //     encValue = (encValue * 1000 -  (((int) encValue) * 1000)) /1000;
        // }
        return encValue;
    }

    public static void resetPivoter() {
        targetShaftPosition = lowShotPosition; // low is forward
    }

    public static void shootClosePosition () {
        setDesiredShootPosition(0); 
    }

    public static void shootOnInitiationLine() {
        setDesiredShootPosition(.45); 
    }

    public static void shootFromFrontOfTrench () {
        setDesiredShootPosition(0.74390215); 
    }

    public static void shootFromBackOfTrench () {
        setDesiredShootPosition(1); 
    }

    public static void setDesiredShootPosition (double desiredPosition) {
        targetShaftPosition = lowShotPosition + ((highShotPosition - lowShotPosition) * desiredPosition);
        SmartDashboard.putNumber("desiredPosition", desiredPosition);
    }

    public static boolean shooterAtPosition () {
        return shooterAtPosition;
    }

    public static void manualMove (double direction) {  // NEED TO BE BETTER ADDS AND SETPOINTS
        
        double newSetpoint;

		if (direction < 0) {
			newSetpoint = getShaftEncoderPosition() - 0.03;
			if (newSetpoint < lowShotPosition) {
				newSetpoint = lowShotPosition;
			}
		} else {
			newSetpoint = getShaftEncoderPosition() + 0.03;
			if (newSetpoint > highShotPosition) {
				newSetpoint = highShotPosition; 
			}
		}

        targetShaftPosition = newSetpoint;

        // TEMP
        //SmartDashboard.putNumber("ShootPivotPwrCall", direction * .5);
        //pivotMotor.set(ControlMode.PercentOutput, direction * .5);
        // System.out.println("Target Shaft Position:" + newSetpoint);
        
    }

    // public static float getDesiredShaftPosition () {
    //     return (float) SmartDashboard.getNumber("SHOOTER SHAFT ADJUSTMENT", 0.5);
    // }
}