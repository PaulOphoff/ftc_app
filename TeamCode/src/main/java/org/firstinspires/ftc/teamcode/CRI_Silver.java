package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.NormalizedColorSensor;
import com.qualcomm.robotcore.util.ElapsedTime;

@Autonomous(name="CRI_Silver", group="Linear Opmode")
//@Disabled
public class CRI_Silver extends BlackoutAutonomousOpMode {

    private ElapsedTime runtime = new ElapsedTime();
    static final double countsPerMotor          = 1120 ;
    static final double gearReduction           = 1.0 ;
    static final double wheelDiameter           = 4.0 ;
    static final double countsPerInch           = (countsPerMotor * gearReduction) /
            (wheelDiameter * Math.PI);
    static final double spinInchesPerDegrees    = (15.375 * Math.PI) / 360;
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

        telemetry.addData("status", "Waiting for start...");

        waitForStart();

        land();

        unhook();

        moveToSample();

        positionToSample();

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

        updateTelemetry("Starting Crater");

        crater();

        updateTelemetry("Crater Successful");

        CraterGo();

        updateTelemetry("CraterGo Successful");
    }

    private void positionToSample() {
        double totalDistance = 0;
        while (((isSampleGold() == false) && (isSampleSilver() == false)) && totalDistance < 8) {
            encoderDrive(.5, 1.5, -1.5, 1);
            totalDistance += 1.5;
            stopMotors();
        }
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

        MineralLifter.setTargetPosition(-300);
        MineralLifter.setPower(.6);
        encoderDrive(.5, 14.5, -14.5, 3);
        encoderSpin(.5, -84);
        encoderDrive(.5, -10, 10, 2);
        MineralLifter.setTargetPosition(0);
        MineralLifter.setPower(.6);
        stopMotors();
    }

    private void CraterGo(){
        double startTime = getRuntime();

        MineralLifter.setTargetPosition(-300);
        MineralLifter.setPower(.6);
        encoderDrive(.5, 14.5, -14.5, 3);
        encoderSpin(.5, -84);
        encoderDrive(.5, -10, 10, 2);
        MineralLifter.setTargetPosition(0);
        MineralLifter.setPower(.6);
        stopMotors();
    }





    // private void Push(double speed) {
    //     MtDew.setPower(speed);
    // }

    private void stopMotors() {
        leftBackDrive.setPower(0);
        rightBackDrive.setPower(0);
        leftLift.setPower(0);
        rightLift.setPower(0);
        MtDew.setPower(0);
        MineralLifter.setPower(0);
    }
}