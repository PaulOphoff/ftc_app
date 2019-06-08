package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.NormalizedColorSensor;
import com.qualcomm.robotcore.hardware.NormalizedRGBA;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

@Autonomous(name="CRI_Silver", group="Linear Opmode")
//@Disabled
public class CRI_Silver extends BlackoutAutonomousOpMode {

    private ElapsedTime runtime = new ElapsedTime();
    private DcMotor leftLift;
    private DcMotor rightLift;
    private DcMotor MtDew;
    private DcMotor MineralLifter;
    private Servo latch;
    private NormalizedColorSensor SampleSensor;
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

    @Override
    public void runOpMode() {
        telemetry.addData("Status", "Initialized");
        telemetry.update();

        leftBackDrive  = hardwareMap.get(DcMotor.class, "leftBackDrive");
        rightBackDrive = hardwareMap.get(DcMotor.class, "rightBackDrive");
        leftLift = hardwareMap.get(DcMotor.class, "leftLift");
        rightLift = hardwareMap.get(DcMotor.class, "rightLift");
        MtDew = hardwareMap.get(DcMotor.class, "MtDew");
        MineralLifter = hardwareMap.get(DcMotor.class, "MineralLifter");
        latch = hardwareMap.servo.get("latch");
        SampleSensor = hardwareMap.get(NormalizedColorSensor.class, "color");
        leftLift.setDirection(DcMotor.Direction.FORWARD);
        rightLift.setDirection(DcMotor.Direction.FORWARD);
        leftBackDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightBackDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        leftBackDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rightBackDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        leftBackDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        rightBackDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        leftBackDrive.setDirection(DcMotor.Direction.FORWARD);
        rightBackDrive.setDirection(DcMotor.Direction.FORWARD);
        MineralLifter.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        MineralLifter.setMode(DcMotor.RunMode.RUN_TO_POSITION);



        waitForStart();

        land();

        unhook();

        moveToSample();

        if (isSampleGold()) {
            sample("Middle");
        }
        else {
            stopMotors();
        }

        goToNextSample();

        if (isSampleGold()) {
            sample("Right");
        }
        else {
            stopMotors();
        }

        goToLastSample();

        if (isSampleGold()) {
            sample("Left");
        }
        else {
            stopMotors();
        }

        MineralLifter.setTargetPosition(-150);
        MineralLifter.setPower(1);

    }

    private void land() {
        double startTime = getRuntime();
        while (opModeIsActive() && (getRuntime() - startTime <= .4)) {
            rightLift.setPower(1);
            leftLift.setPower(-1);
            updateTelemetry("Landing");
        }
        stopMotors();
    }

    private void unhook() {
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

    private void moveToSample() {
        double startTime = getRuntime();

        stopMotors();
        encoderDrive(.5, .375, -.375, .1);
        updateTelemetry("Positioning To Sample");
        encoderDrive(.5, -18.8, 18.8, 5);
        updateTelemetry("Driving To First Sample");
        stopMotors();
        encoderSpin(.5, 84);
        updateTelemetry("Spinning 90 Degrees");
        stopMotors();
    }

    private void sample(String samplePosition) {
        updateTelemetry("Sampling " + samplePosition);

        encoderDrive(.5, -5, 5, 1);
        encoderSpin(.5, 81);
        encoderSpin(.5, -81);
        encoderDrive(.5, 5, -5, 1);

        updateTelemetry("Sampling " + samplePosition + " - Done!");
    }

    private void goToNextSample() {
        double startTime = getRuntime();

        encoderDrive(.5, 14.5, -14.5, 3);
        updateTelemetry("MovingToNextSample");

        stopMotors();
    }

    private void goToLastSample() {
        double startTime = getRuntime();

        encoderDrive(.5, -29, 29, 5);
        updateTelemetry("MovingToLastSample");

        stopMotors();
    }

    private void crater() {
        double startTime = getRuntime();

        encoderDrive(.5, 14.5, -14.5, 3);
        encoderSpin(.5, -84);
        encoderDrive(.5, -10, 10, 2);
    }





    private void Push(double speed) {
        MtDew.setPower(speed);
    }

    private void stopMotors() {
        leftBackDrive.setPower(0);
        rightBackDrive.setPower(0);
        leftLift.setPower(0);
        rightLift.setPower(0);
        MtDew.setPower(0);
    }

    private boolean isSampleGold() {
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
}