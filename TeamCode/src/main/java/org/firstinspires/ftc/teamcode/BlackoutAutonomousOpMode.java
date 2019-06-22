package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.NormalizedColorSensor;
import com.qualcomm.robotcore.hardware.NormalizedRGBA;
import com.qualcomm.robotcore.hardware.Servo;

abstract class BlackoutAutonomousOpMode extends LinearOpMode {


    protected DcMotor leftBackDrive;
    protected DcMotor rightBackDrive;
    protected DcMotor leftLift;
    protected DcMotor rightLift;
    protected DcMotor MtDew;
    protected DcMotor DrPepper;
    protected DcMotor MineralLifter;
    protected Servo latch;
    protected NormalizedColorSensor SampleSensor;
    double leftBackPower;
    double rightBackPower;
    static final double countsPerMotor          = 1120 ;
    static final double gearReduction           = 1.0 ;
    static final double wheelDiameter           = 4.0 ;
    static final double countsPerInch           = (countsPerMotor * gearReduction) /
            (wheelDiameter * Math.PI);
    static final double spinInchesPerDegrees    = (15.375 * Math.PI) / 334.0206185567;
    static final double rotateDegrees           = (30.75 * Math.PI) / 360;
    static final double spinCountsPerDegree     = (countsPerInch * spinInchesPerDegrees);
    private boolean _isSampleGold = false;
    private boolean _isSampleSilver = false;

    public void land() {
        double startTime = getRuntime();
        while (opModeIsActive() && (getRuntime() - startTime <= .4)) {
            rightLift.setPower(1);
            leftLift.setPower(-1);
            updateTelemetry("Landing");
        }
        stopMotors();
    }

    public void unhook() {
        double startTime = getRuntime();
        while (opModeIsActive() && (getRuntime() - startTime <= .5)) {
            rightLift.setPower(-1);
            leftLift.setPower(1);
            updateTelemetry("Unhooking");
            latch.setPosition(0);
            updateTelemetry("unlatching");
            stopMotors();
        }
    }

    public void moveToSample() {
        double startTime = getRuntime();

        stopMotors();
        encoderDrive(.5, .375, -.375, .1);
        updateTelemetry("Positioning To Sample");
        encoderDrive(.5, -18, 18, 5);
        updateTelemetry("Driving To First Sample");
        stopMotors();
        encoderSpin(.5, 84);
        updateTelemetry("Spinning 90 Degrees");
        stopMotors();
    }

    public void sample(String samplePosition) {
        updateTelemetry("Sampling " + samplePosition);

        encoderDrive(.5, -5, 5, 1);
        encoderSpin(.5, 81);
        encoderSpin(.5, -81);
        encoderDrive(.5, 5, -5, 1);

        updateTelemetry("Sampling " + samplePosition + " - Done!");
    }


    public boolean isSampleGold() {
        double startTime = getRuntime();
        long goldCount = 0;
        long silverCount = 0;

        while(getRuntime() - startTime <= .5) {
            NormalizedRGBA sampleColors = SampleSensor.getNormalizedColors();
            float max = Math.max(Math.max(sampleColors.red, sampleColors.green), sampleColors.blue);
            sampleColors.red /= max;
            sampleColors.green /= max;
            sampleColors.blue /= max;
            double goldness = 75 * (sampleColors.red + sampleColors.green -2 * sampleColors.blue);
            if(goldness >= 55) {
                goldCount++;
            } else {
                silverCount++;
            }
            telemetry.addData("Sampling", "Gold : (%d)\tSilver : (%d)\t Goldness : (%.2f)", goldCount, silverCount, goldness);
            telemetry.update();
        }

        return goldCount > silverCount;
    }

    public boolean isSampleSilver() {
        double startTime = getRuntime();
        long goldCount = 0;
        long silverCount = 0;

        while(getRuntime() - startTime <= .5) {
            NormalizedRGBA sampleColors = SampleSensor.getNormalizedColors();
            float max = Math.max(Math.max(sampleColors.red, sampleColors.green), sampleColors.blue);
            sampleColors.red /= max;
            sampleColors.green /= max;
            sampleColors.blue /= max;
            double goldness = 75 * (sampleColors.red + sampleColors.green -2 * sampleColors.blue);
            if(goldness <= 25) {
                goldCount++;
            } else {
                silverCount++;
            }
            telemetry.addData("Sampling", "Gold : (%d)\tSilver : (%d)\t Goldness : (%.2f)", goldCount, silverCount, goldness);
            telemetry.update();
        }

        return goldCount > silverCount;
    }
    public boolean isOpModeStillActive() {
        return opModeIsActive();
    }
    private void stopMotors() {
        leftBackDrive.setPower(0);
        rightBackDrive.setPower(0);
        leftLift.setPower(0);
        rightLift.setPower(0);
        MtDew.setPower(0);
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
