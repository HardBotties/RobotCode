/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.wpi.first.wpilibj.templates;

import edu.wpi.first.wpilibj.CounterBase;
import edu.wpi.first.wpilibj.Victor;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.PIDSource;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 *
 * @author FRC 3634
 */
public class Drive
{
    public static final double PVAL = 0.5, IVAL = 1, DVAL = 0.0, FVAL = 0.1;
    Victor leftvictor;
    Victor rightvictor;
    Encoder leftSpeedCounter;
    Encoder rightSpeedCounter;
    PIDController rightController;
    SmartDashboard dashboard;

    public Drive()
    {
        leftvictor = new Victor(3);
        rightvictor = new Victor(4)
        {
            public void pidWrite(double val)
            {
                super.pidWrite(-val);
            }
        };
        rightSpeedCounter = new Encoder(3, 4, false, CounterBase.EncodingType.k4X);
        leftSpeedCounter = new Encoder(1, 2, true, CounterBase.EncodingType.k4X);
        rightSpeedCounter.start();
        leftSpeedCounter.start();
        rightSpeedCounter.setDistancePerPulse(1.0/36000);
        leftSpeedCounter.setDistancePerPulse(1.0/36000);
        rightSpeedCounter.setSamplesToAverage(20);
        leftSpeedCounter.setSamplesToAverage(20);
        rightSpeedCounter.setPIDSourceParameter(PIDSource.PIDSourceParameter.kRate);
        rightController = new PIDController(PVAL, IVAL, DVAL,FVAL, rightSpeedCounter, rightvictor,0.025/2);
        rightController.enable();
        
        LiveWindow.addActuator("Drive", "Left", leftvictor);
        LiveWindow.addActuator("Drive", "Right", rightvictor);
        LiveWindow.addActuator("Drive", "PIDController", rightController);
        LiveWindow.addSensor("Drive", "Left Speed Counter", leftSpeedCounter);
        LiveWindow.addSensor("Drive", "Right Speed Counter", rightSpeedCounter);
    }

    public void move(double left, double right)
    {
       // leftvictor.set(left);
        //rightvictor.set(right);
        leftvictor.set(left);
        double rightWheelSpeed = 0;
        double leftWheelSpeed = 0;
        rightWheelSpeed = rightSpeedCounter.getRate();
        leftWheelSpeed = leftSpeedCounter.getRate();
        rightController.setSetpoint(leftWheelSpeed);
       // rightController.setPID(PVAL, IVAL, DVAL, right);

        System.out.println(rightWheelSpeed + "," +  rightController.getSetpoint());
    }

    public void singleJoystickMove(double x, double y, boolean turbo)
    {
        double rightWheelSpeed = 0;
        double leftWheelSpeed = 0;
        double left = 0;
        double right = 0;
        if (y > x && y > -x)
        {
            left = y;
            right = y;
        }
        if (y >= -x && y <= x)
        {
            left = -x;
            right = x;

        }
        if (y < x && y < -x)
        {
            left = y;
            right = y;

        }
        if (y >= x && y <= -x)
        {
            left = -x;
            right = x;
        }
        left = changeSensitivity(left, turbo);
        right = changeSensitivity(right, turbo);
        rightWheelSpeed = rightSpeedCounter.getRate();
        leftWheelSpeed = leftSpeedCounter.getRate();

        //System.out.println((int) leftWheelSpeed + "," + (int) rightWheelSpeed);
        move(left, right);
    }

    public double changeSensitivity(double speed, boolean turbo)
    {
        /*if(speed < 0)
         {
         speed = speed * speed * -1;
         }
         else
         {
         speed = speed * speed;
         }*/
        speed = speed * speed * speed * speed * speed;
        if (turbo)
        {
            return speed;
        } else
        {
            return speed / 2;
        }
    }
}
