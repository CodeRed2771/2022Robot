package frc.robot;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class TurnPosition {
    public double getNewTurnPosition(double currentPosition, double newPosition) {
        double absoluteCurrentPos = Math.abs(currentPosition);
        double intergerCurrentPos = Math.round(absoluteCurrentPos);
        double currentDecimal = absoluteCurrentPos - intergerCurrentPos;
        double bestPos; 

        if (currentDecimal > 0.5) {
            bestPos = intergerCurrentPos + 1 + newPosition; 
        } else if (currentDecimal < 0.5) {
            bestPos = intergerCurrentPos + newPosition; 
        } else {
            bestPos = intergerCurrentPos + 1 + newPosition; 
        }
        SmartDashboard.putNumber("Optomized Encoder Position", bestPos);
        return bestPos; 
    }
}
