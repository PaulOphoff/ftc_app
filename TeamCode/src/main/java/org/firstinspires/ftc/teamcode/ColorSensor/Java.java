package org.firstinspires.ftc.teamcode.ColorSensor;

import org.firstinspires.ftc.robotcore.internal.opmode.OpModeManagerImpl;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.NormalizedColorSensor;
import com.qualcomm.robotcore.hardware.NormalizedRGBA;
public class Java extends OpModeManagerImpl {
    @TeleOp(name="ColorSensor", group="OpModeManagerImpl")
    private NormalizedColorSensor colorSensor;


}