package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

abstract class BlackoutAutonomousOpMode extends LinearOpMode {


    protected DcMotor leftBackDrive;
    protected DcMotor rightBackDrive;
    static final double countsPerMotor          = 1120 ;
    static final double gearReduction           = 1.0 ;
    static final double wheelDiameter           = 4.0 ;
    static final double countsPerInch           = (countsPerMotor * gearReduction) /
            (wheelDiameter * Math.PI);
    static final double spinInchesPerDegrees    = (15.375 * Math.PI) / 334.0206185567;
    static final double rotateDegrees           = (30.75 * Math.PI) / 360;
    static final double spinCountsPerDegree     = (countsPerInch * spinInchesPerDegrees);

    public boolean isOpModeStillActive() {
        return opModeIsActive();
    }
    public void updateTelemetry(String currentTask) {
        // Show the elapsed game time and wheel power.
        telemetry.addData("Time", "%.1f", getRuntime());
        // telemetry.addData("Motors", "left (%.2f), right (%.2f)", leftBackPower, rightBackPower);
        telemetry.addData("Task", currentTask);
        telemetry.update();
    }
    public void encoderDrive(double speed, double leftInches, double rightInches, double timeoutSeconds) {
        int newLeftTarget;
        int newRightTarget;

        // Ensure that the opmode is still active
        if (opModeIsActive()) {

            // Determine new target position, and pass to motor controller
            newLeftTarget = leftBackDrive.getCurrentPosition() + (int)(leftInches * countsPerInch);
            newRightTarget = rightBackDrive.getCurrentPosition() + (int)(rightInches * countsPerInch);
            leftBackDrive.setTargetPosition(newLeftTarget);
            rightBackDrive.setTargetPosition(newRightTarget);

            // Turn On RUN_TO_POSITION
            leftBackDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            rightBackDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);

            // reset the timeout time and start motion.
            double startTime = getRuntime();

            leftBackDrive.setPower(Math.abs(speed));
            rightBackDrive.setPower(Math.abs(speed));

            // However, if you require that BOTH motors have finished their moves before the robot continues
            // onto the next step, use (isBusy() || isBusy()) in the loop test.
            while (opModeIsActive() &&
                    (getRuntime() < startTime + timeoutSeconds) &&
                    (leftBackDrive.isBusy() && rightBackDrive.isBusy())) {

                // Display it for the driver.
                telemetry.addData("Path1",  "Running to %7d :%7d", newLeftTarget,  newRightTarget);
                telemetry.addData("Path2",  "Running at %7d :%7d",
                        leftBackDrive.getCurrentPosition(),
                        rightBackDrive.getCurrentPosition());
                telemetry.update();
            }
            leftBackDrive.setPower(0);
            rightBackDrive.setPower(0);

            leftBackDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            rightBackDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        }
    }
    public void encoderSpin(double speed, double spinDegrees) {
        int newLeftTarget;
        int newRightTarget;

        // Ensure that the opmode is still active
        if (opModeIsActive()) {

            // Determine new target position, and pass to motor controller
            newLeftTarget = leftBackDrive.getCurrentPosition() + (int)(spinDegrees * spinCountsPerDegree);
            newRightTarget = rightBackDrive.getCurrentPosition() + (int)(spinDegrees * spinCountsPerDegree);
            leftBackDrive.setTargetPosition(newLeftTarget);
            rightBackDrive.setTargetPosition(newRightTarget);

            // Turn On RUN_TO_POSITION
            leftBackDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            rightBackDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);

            // reset the timeout time and start motion.
            double startTime = getRuntime();

            leftBackDrive.setPower(Math.abs(speed));
            rightBackDrive.setPower(Math.abs(speed));

            // However, if you require that BOTH motors have finished their moves before the robot continues
            // onto the next step, use (isBusy() || isBusy()) in the loop test.
            while (opModeIsActive() &&
                    // (getRuntime() < startTime) &&
                    (leftBackDrive.isBusy() && rightBackDrive.isBusy())) {

                // Display it for the driver.
                telemetry.addData("Spinning", "Current: %7d\tTarget: %7d", leftBackDrive.getCurrentPosition(), newLeftTarget);
                telemetry.update();
            }

            leftBackDrive.setPower(0);
            rightBackDrive.setPower(0);

            leftBackDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            rightBackDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        }
    }
}
