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
        double reverseBestOffset = 1;
        double regularBestOffset = 1;
        double bestPos; 
        boolean reverseBoolean;
        
        // Regular
        if (regularPlusOffset < regularMinusOffset) {
            regularBestPos = regular +1;
            regularBestOffset = regularPlusOffset; 
        } else if (regularPlusOffset > regularMinusOffset) {
            regularBestPos =  regular; 
            reverseBestOffset = regularMinusOffset;
        } else {
            regularBestPos = regular + 1; 
            regularBestOffset = regularPlusOffset; 
        }

        // Reverse 
        double reverse = regularBestPos - Math.abs((intergerCurrentPos+0.5)-regularBestPos); // Very Unsure
        double reversePlusOffset = (reverse + 1) - currentPosition;
        double reverseMinusOffset = Math.abs(currentPosition - reverse); 

        if (reversePlusOffset < reverseMinusOffset) {
            reverseBestPos = reverse + 1;
            reverseBestOffset = reversePlusOffset; 
            
        } else if (reversePlusOffset > reverseMinusOffset) {
            reverseBestPos = reverse; 
            reverseBestOffset = reverseMinusOffset;
        } else {
            reverseBestPos = reverse + 1; 
            reverseBestOffset = reversePlusOffset; 
        }

        // Regular vs. Reverse
        if (regularBestOffset < reverseBestOffset) {
            bestPos = regularBestPos;
            reverseBoolean = false;
        } else if (regularBestOffset > reverseBestOffset) {
            bestPos = reverseBestPos;
            reverseBoolean = true;
        } else {
            bestPos = regularBestPos;
            reverseBoolean = false;
        }

        SmartDashboard.putNumber("Optomized Encoder Position", bestPos);
        return bestPos; 
    }
}
