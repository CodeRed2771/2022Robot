/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

public class Climber {
	private static CANSparkMax climberMotor;
	public static void init() {
        climberMotor = new CANSparkMax(1, MotorType.kBrushless);
        climberMotor.restoreFactoryDefaults(); 
        climberMotor.setClosedLoopRampRate(0.5);
        climberMotor.setSmartCurrentLimit(20);
        climberMotor.setIdleMode(IdleMode.kBrake);
		// 2 motors follow
		// Solinod 
	}
	public static void tick() {
		
	}

}
