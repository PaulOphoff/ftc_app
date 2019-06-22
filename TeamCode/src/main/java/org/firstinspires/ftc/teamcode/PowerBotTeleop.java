package org.firstinspires.ftc.teamcode;

import android.app.Activity;
import android.graphics.Color;
import android.view.View;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.NormalizedColorSensor;
import com.qualcomm.robotcore.hardware.NormalizedRGBA;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.SwitchableLight;

@TeleOp
public class PowerBotTeleop extends OpMode {
    private DcMotor ShoulderMotor1;
    private DcMotor RightDrive;
    private DcMotor LeftDrive;
    private DcMotor SpinnerMotor;

    private Servo SorterServo;
    private Servo ArmLock;
    private Servo MarkerServo;


    NormalizedColorSensor colorSensorLeft;
    NormalizedColorSensor colorSensorRight;

    View relativeLayout;

    private int SorterState;
    private double shoulder;
    private int ShoulderOffset;
    private int armPosition = 0;
    private double center = .7;
    private double left = .3;
    private double right = 1;
    private double open = 0;
    private boolean firstPressy = true;
    private boolean firstPressa = true;
    private boolean encoderEngaged = true;
    private boolean firstPressRight_Trigger = true;
    private boolean firstPressLeft_Trigger = true;
    private boolean firstPressRight_Bumper = true;
    private boolean scoring = false;
    private boolean sweeperEngaged = false;
    private boolean colorSensorsEngaged = true;
    float[] hsvValues = new float[3];
    private int centerArmPosition = -2150;
    private int crater = 30;
    private int hover = -400;
    private int lander = -3100;
    private int hang = -4800;
    private double fastSpeed = 1;
    private double mediumSpeed = .6;
    private double slowSpeed = .1;
    private double downMediumSpeed = .6;
    private double downSlowSpeed = .25;

    private boolean rightIsYellow;
    private boolean leftIsYellow;
    private boolean isScoringSilver;

    //static final double COUNTS_PER_MOTOR_REV = 1120;

    @Override
    public void init() {
        ShoulderMotor1 = hardwareMap.dcMotor.get("ShoulderMotor1");
        ShoulderMotor1.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        telemetry.addData("ShoulderMotor1", ShoulderMotor1.getCurrentPosition());
        telemetry.update();
        SpinnerMotor = hardwareMap.dcMotor.get("SpinnerMotor");
        SorterServo = hardwareMap.servo.get("SorterServo");
        ArmLock = hardwareMap.servo.get("ArmLock");
        MarkerServo = hardwareMap.servo.get("MarkerServo");
        LeftDrive = hardwareMap.dcMotor.get("LeftDrive");
        RightDrive = hardwareMap.dcMotor.get("RightDrive");
        SorterServo.setPosition(center);
        int relativeLayoutId = hardwareMap.appContext.getResources().getIdentifier("RelativeLayout", "id", hardwareMap.appContext.getPackageName());
        relativeLayout = ((Activity) hardwareMap.appContext).findViewById(relativeLayoutId);

        // values is a reference to the hsvValues array.

        final float values[] = hsvValues;


        // Get a reference to our sensor object.
        colorSensorLeft = hardwareMap.get(NormalizedColorSensor.class, "colorSensorLeft");
        colorSensorRight = hardwareMap.get(NormalizedColorSensor.class, "colorSensorRight");
        SorterServo = hardwareMap.get(Servo.class, "SorterServo");

        if (gamepad2.left_stick_y < -.5) {
            ShoulderOffset = 150;
        }
        if (colorSensorLeft instanceof SwitchableLight) {
            ((SwitchableLight)colorSensorLeft).enableLight(true);
        }

        if (colorSensorRight instanceof SwitchableLight) {
            ((SwitchableLight)colorSensorRight).enableLight(true);
        }
    }//init

    @Override
    public void loop() {
        if (!gamepad2.dpad_up && !gamepad1.dpad_up) {
            //Shoulder ------------------------------------------------------------------------------------------------------------------------------
            //Uses the Y button on Gamepad1 to determine if it should use the encoders
            if (gamepad2.y == true) {
                if (firstPressy == true) {
                    encoderEngaged = !encoderEngaged;
                    firstPressy = false;
                }
            }
            else {
                firstPressy = true;
            }
            //Reset the encoder position
            if (gamepad2.x == true) {
                ShoulderMotor1.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            }
            //Uses Encoder Positioning
            if (encoderEngaged == true) {
                if (gamepad1.right_trigger > .5) {
                    if (firstPressRight_Trigger == true) {
                        if (armPosition == 1) {
                            armPosition = 0;
                            scoring = false;
                        }
                        else {
                            armPosition = 1;
                            scoring = true;
                        }
                        firstPressRight_Trigger = false;
                    }
                }
                else {
                    firstPressRight_Trigger = true;
                }
                if (gamepad1.left_trigger > .5) {
                    if (firstPressLeft_Trigger == true) {
                        if (armPosition == 2) {
                            armPosition = 0;
                        }
                        else {
                            armPosition = 2;
                        }
                        firstPressLeft_Trigger = false;
                    }
                }
                else {
                    firstPressLeft_Trigger = true;
                }
                if (gamepad2.right_trigger > .5 && gamepad2.left_trigger > .5) {
                    armPosition = 3;
                }
                if (gamepad2.left_bumper && gamepad2.right_bumper) {
                    armPosition = 4;
                }

                if (armPosition == 0) { // move to crater
                    shoulderPosition(crater);
                }
                else if (armPosition == 1) { // move to lander
                    shoulderPosition(lander);
                }
                else if (armPosition == 2) { // move above crater
                    shoulderPosition(hover);
                }
                else if (armPosition == 3) {
                    shoulderPosition(centerArmPosition);
                }
                else if (armPosition == 4) {
                    shoulderPosition(hang);
                }

                if (gamepad2.dpad_left == true) {
                    ShoulderOffset = ShoulderOffset+3;
                }
                else if (gamepad2.dpad_right == true) {
                    ShoulderOffset = ShoulderOffset-3;
                }
            }
            //Uses Values from the Triggers on Gamepad1
            else {
                ShoulderMotor1.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
                shoulder = gamepad1.left_trigger-gamepad1.right_trigger;
                ShoulderMotor1.setPower(shoulder);
                telemetry.addData("Power", shoulder);
            }

            //Sweeper-------------------------------------------------------------------------------------------------------------------------------
            if (gamepad1.right_bumper == true) {
                if (firstPressRight_Bumper == true) {
                    if (sweeperEngaged == false) {
                        SpinnerMotor.setPower(-1);
                        sweeperEngaged = true;
                    }
                    else {
                        SpinnerMotor.setPower(0);
                        sweeperEngaged = false;
                    }
                    firstPressRight_Bumper = false;
                }
            }
            else {
                firstPressRight_Bumper = true;
            }
            if (gamepad1.left_bumper) {
                SpinnerMotor.setPower(1);
                sweeperEngaged = true;
            }
            //Mineral Sorter -----------------------------------------------------------------------------------------------------------------------
            if (gamepad2.a) {
                if (firstPressa) {
                    colorSensorsEngaged = !colorSensorsEngaged;
                    firstPressa = false;
                }
            }
            else {
                firstPressa = true;
            }
            if (colorSensorsEngaged) {
                // Read the sensor
                if (!gamepad1.a && !gamepad1.b) {
                    NormalizedRGBA colorsLeft = colorSensorLeft.getNormalizedColors();
                    NormalizedRGBA colorsRight = colorSensorRight.getNormalizedColors();

                    // display the colors seen by the right color sensor
                    Color.colorToHSV(colorsRight.toColor(), hsvValues);
                    telemetry.addLine("right ")
                            .addData("r", "%.3f", colorsRight.red)
                            .addData("g", "%.3f", colorsRight.green)
                            .addData("b", "%.3f", colorsRight.blue);

                    // display the colors seen by the left sensor
                    Color.colorToHSV(colorsLeft.toColor(), hsvValues);
                    telemetry.addLine("Left")
                            .addData("r", "%.3f", colorsLeft.red)
                            .addData("g", "%.3f", colorsLeft.green)
                            .addData("b", "%.3f", colorsLeft.blue);


                    // max the values
                    float maxRight = Math.max(Math.max(Math.max(colorsRight.red, colorsRight.green), colorsRight.blue), colorsRight.alpha);
                    colorsRight.red   /= maxRight;
                    colorsRight.green /= maxRight;
                    colorsRight.blue  /= maxRight;
                    int colorRight = colorsRight.toColor();

                    float maxLeft = Math.max(Math.max(Math.max(colorsLeft.red, colorsLeft.green), colorsLeft.blue), colorsLeft.alpha);
                    colorsLeft.red   /= maxLeft;
                    colorsLeft.green /= maxLeft;
                    colorsLeft.blue  /= maxLeft;
                    int colorLeft = colorsLeft.toColor();

                    // decide whether or not the right sees yellow so we can act on this info later
                    double goldnessRight = 75 * (colorsRight.red + colorsRight.green - 2 * colorsRight.blue);
                    rightIsYellow = false;

                    if (goldnessRight >= 25) {
                        rightIsYellow = true;
                    }

                    telemetry.addLine("Saw yellow:  ")
                            .addData(" right is yellow", rightIsYellow)
                            .addData("goldness", "%.3f", goldnessRight);

                    // decide
                    double goldnessLeft = 75 * (colorsLeft.red + colorsLeft.green - 2 * colorsLeft.blue);
                    leftIsYellow = false;

                    if (goldnessLeft >= 25) {
                        leftIsYellow = true;
                    }

                    telemetry.addLine("Saw yellow:  ")
                            .addData(" left is yellow", leftIsYellow)
                            .addData("goldness", "%.3f", goldnessLeft);
                }
                if (gamepad1.b) { // drop gold
                    telemetry.addLine("B Yellow:  ")
                            .addData(" right is yellow", rightIsYellow);
                    telemetry.addLine("B Yellow:  ")
                            .addData(" left is yellow", leftIsYellow);

                    if (leftIsYellow && rightIsYellow) {
                        SorterServo.setPosition(open); // check Position in old teleop
                        telemetry.addData("Yellow","Both");
                    }
                    else if(!leftIsYellow && rightIsYellow) {
                        SorterServo.setPosition(right); // check old teleop
                        telemetry.addData("Yellow","Right");
                    }

                    else if(leftIsYellow && !rightIsYellow) {
                        SorterServo.setPosition(left);
                        telemetry.addData("Yellow","Left");
                    }
                    else {
                        SorterServo.setPosition(center);
                        telemetry.addData("Yellow","Neither");
                    }
                }
                else if (gamepad1.a) { // drop silver
                    if (!leftIsYellow && !rightIsYellow) {
                        SorterServo.setPosition(open); // check Position in old teleop
                        isScoringSilver = true;
                    }
                    else if(leftIsYellow && !rightIsYellow) {
                        SorterServo.setPosition(right); // check old teleop
                        isScoringSilver = true;
                    }
                    else if(!leftIsYellow && rightIsYellow) {
                        SorterServo.setPosition(left);
                        isScoringSilver = true;
                    }
                    else {
                        SorterServo.setPosition(center);
                    }
                }
                else if (gamepad1.y) {
                    SorterServo.setPosition(open);
                }
                else {
                    SorterServo.setPosition(center);
                }
            }
            else {
                if (gamepad1.x == true) {
                    SorterServo.setPosition(right);
                }
                else if (gamepad1.y == true) {
                    SorterServo.setPosition(open);
                }
                else if (gamepad1.b == true) {
                    SorterServo.setPosition(left);
                }
                else {
                    SorterServo.setPosition(center);
                }
            }

            //Drivetrain -----------------------------------------------------------------------------------------------------------------------------
            if(!scoring){
                double rightSpeed = gamepad1.left_stick_y;
                RightDrive.setPower(rightSpeed);

                double leftSpeed = -gamepad1.right_stick_y;
                LeftDrive.setPower(leftSpeed);
            }
            else{
                double rightSpeed = -gamepad1.right_stick_y;
                RightDrive.setPower(rightSpeed);

                double leftSpeed = gamepad1.left_stick_y;
                LeftDrive.setPower(leftSpeed);
            }
            //Latch -------------------------------------------------------------------------------------------------------------
            if (gamepad2.b) {
                ArmLock.setPosition(1);
            }
            else {
                ArmLock.setPosition(0);
            }
            //MarkerServo ---------------------------------------------------------------------------------------------------------
            if (gamepad2.dpad_down) {
                MarkerServo.setPosition(1);
            }
            else {
                MarkerServo.setPosition(0);
            }
            if (gamepad2.left_stick_y < -.5) {
                ShoulderOffset = 150;
            }
        }
        else {
            ShoulderMotor1.setPower(0);
            LeftDrive.setPower(0);
            RightDrive.setPower(0);
            SpinnerMotor.setPower(0);
            telemetry.addData("EMERGENCY STOP","ENGAGED");
        }
        //Telemetry -----------------------------------------------------------------------------------------------------------------------------
        telemetry.addData("Sweeper Engaged", sweeperEngaged);
        telemetry.addData("Color Sensor Scoring", colorSensorsEngaged);
        telemetry.addData("Encoder Engaged", encoderEngaged);
        telemetry.addData("Arm Position", armPosition);
        telemetry.addData("Shoulder Offset", ShoulderOffset);
        telemetry.addData("ShoulderMotor1", ShoulderMotor1.getCurrentPosition());
        telemetry.update ();
    }//ends loop

    private void shoulderPosition(int position) {
        ShoulderMotor1.setTargetPosition(position+ShoulderOffset);
        ShoulderMotor1.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        double encoderPosition = (ShoulderMotor1.getCurrentPosition());

        if (armPosition == 0) {
            if (encoderPosition > (hover-200+ShoulderOffset) && encoderPosition <= (crater+ShoulderOffset)) {
                ShoulderMotor1.setPower(downSlowSpeed);
                telemetry.addData("Shoulder Speed","Slow");
            }
            else if (encoderPosition > (centerArmPosition+ShoulderOffset) && encoderPosition <= (hover+ShoulderOffset)) {
                ShoulderMotor1.setPower(downMediumSpeed);
                telemetry.addData("Shoulder Speed","Medium");
            }
            else {
                ShoulderMotor1.setPower(fastSpeed);
                telemetry.addData("Shoulder Speed","Fast");
            }
        }
        else if (armPosition == 2) {
            if (encoderPosition > (hover-400+ShoulderOffset) && encoderPosition <= (hover+ShoulderOffset)) {
                ShoulderMotor1.setPower(slowSpeed);
                telemetry.addData("Shoulder Speed","Slow");
            }
            else if (encoderPosition > (centerArmPosition+ShoulderOffset) && encoderPosition <= (hover-400+ShoulderOffset)) {
                ShoulderMotor1.setPower(mediumSpeed);
                telemetry.addData("Shoulder Speed","Medium");
            }
            else {
                ShoulderMotor1.setPower(fastSpeed);
                telemetry.addData("Shoulder Speed","Fast");
            }
        }
        else if (armPosition == 1) {
            if (encoderPosition > (lander+ShoulderOffset) && encoderPosition <= (lander+200+ShoulderOffset)) {
                ShoulderMotor1.setPower(slowSpeed);
                telemetry.addData("Shoulder Speed","Slow");
            }
            else if (encoderPosition > (lander+200+ShoulderOffset) && encoderPosition < (centerArmPosition+ShoulderOffset)) {
                ShoulderMotor1.setPower(mediumSpeed);
                telemetry.addData("Shoulder Speed","Medium");
            }
            else {
                ShoulderMotor1.setPower(fastSpeed);
                telemetry.addData("Shoulder Speed","Fast");
            }
        }
        else if (armPosition == 3) {
            ShoulderMotor1.setPower(fastSpeed);
            telemetry.addData("Shoulder Speed","Fast");
        }
        else if (armPosition == 4) {
            ShoulderMotor1.setPower(fastSpeed);
            telemetry.addData("Shoulder Speed","Fast");
        }
        telemetry.addData("Speed", ShoulderMotor1.getPower());
    }
}//ends class

