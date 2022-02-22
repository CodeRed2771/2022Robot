package frc.robot;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class TurnPosition {
    public double getNewTurnPosition(double currentPosition, double newPosition) {
        double absoluteCurrentPos = Math.abs(currentPosition);
        double intergerCurrentPos = Math.round(absoluteCurrentPos);
        double currentDecimal = absoluteCurrentPos - intergerCurrentPos;
        double regularBestPos;
        double reverseBestPos;
        double regular = intergerCurrentPos + newPosition; // Somewhat unsure
        double regularPlusOffset = (regular + 1) - currentPosition;
        double regularMinusOffset = Math.abs(currentPosition - regular);
        double bestPos; 
        
        if (regularPlusOffset < regularMinusOffset) {
            regularBestPos = regular +1; 
        } else if (regularPlusOffset > regularMinusOffset) {
            regularBestPos =  regular; 
        } else {
            regularBestPos = regular + 1; 
        }

        double reverse = regularBestPos - Math.abs((intergerCurrentPos+0.5)-regularBestPos); // Very Unsure
        double reversePlusOffset = (reverse + 1) - currentPosition;
        double reverseMinusOffset = Math.abs(currentPosition - reverse); 

        if (reversePlusOffset < reverseMinusOffset) {
            reverseBestPos = reverse + 1; 
        } else if (reversePlusOffset > reverseMinusOffset) {
            reverseBestPos = reverse; 
        } else {
            reverseBestPos = reverse + 1; 
        }

        double reverseOffset = 1; // Calculate
        double regularOffset = 1; // Calculate 
        if(reverseOffset < regularOffset) {
            bestPos = reverseOffset;
        } else if(reverseOffset > regularOffset) {
            bestPos = regularOffset;
        } else {
            bestPos = regularOffset;
        }

        SmartDashboard.putNumber("Optomized Encoder Position", regularBestPos);
        return regularBestPos; 
    }
}
