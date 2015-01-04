/*----------------------------------------------------------------------------*/
/* Copyright (c) FIRST 2008. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/
package edu.wpi.first.wpilibj.templates;

import edu.wpi.first.wpilibj.Relay;
import edu.wpi.first.wpilibj.SimpleRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.Compressor;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the SimpleRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * * directory.
 */
public class Robot2014 extends SimpleRobot
{

    int state = START;
    int timer = 0;
    long bucky = 0;
    long time = 0;
    public static final int IDLE_TIMER = 150;
    public static final int STOP_TIMER = 25;
    public static final int START_DRIVE_TIMER = 0; //300
    public static final int STOP_DRIVE_TIMER = 50;
    public static final int MOVE_BACKWARD = 25;
    public static final int MOVE_FORWARD = 30;
    public static final int VISION_DELAY = 50;
    public static final int START = 0;
    public static final int LOADING = 1;
    public static final int LOADED = 2;
    public static final int START_SHOOT = 3;
    public static final int SHOOT = 4;
    public static final int RESET = 5;
    public static final int DRIVE = 6;
    public static final int DRIVE_BACKWARD = 7;
    public static final int DRIVE_FORWARD = 8;
    public static final int STOP = 9;
    public static final int VISION = 10;
    public static final int IDLE = 11;
    public static final int LOW_SHOOT = 12;
    public static final int LOW_GOAL_TIMER = 50;
    public static final double ACCEL = 0.2, DEADZONE = 0.05;
    double oldY, oldX;
    RobotDrive robotdrive;
    Joystick joystickDrive;
    Joystick joystickShoot;
    Relay cameraLight;
    Compressor compressor;
    Pickup pickup;
    Shoot shooter;
    Vision vision;

    protected void robotInit()
    {
        robotdrive = new RobotDrive(9, 10);
        joystickDrive = new Joystick(1);
        joystickShoot = new Joystick(2);
        pickup = new Pickup();
        shooter = new Shoot();
        cameraLight = new Relay(5, Relay.Direction.kForward);
        compressor = new Compressor(7, 6);
        compressor.start();
        vision = new Vision();

    }

    public void performAuto()
    {
        switch (state)
        {
            case START:
                pickup.armsUp();
                pickup.off();
                robotdrive.arcadeDrive(0, 0);
                //shooter.startLoading();
                System.out.println("Auto START");
                state = VISION; //LOADING ; DRIVE
                break;
            case LOADING:
                pickup.armsUp();
                robotdrive.arcadeDrive(0, 0);
                System.out.println("Auto LOADING");
                if (shooter.getState() == shooter.LOADED)
                {
                    state = VISION;
                    timer = 0;
                }
                break;
            case VISION:
                if (timer <= VISION_DELAY)
                {
                    timer++;
                } else
                {
                    pickup.armsUp();
                    robotdrive.arcadeDrive(0, 0);
                    System.out.println("Auto VISION");
                    if (vision.targetHot())
                    {
                        state = DRIVE; //LOADED
                        timer = 0;
                    } else
                    {
                        state = IDLE;
                        timer = 0;
                    }
                }
                break;
            case IDLE:
                pickup.armsUp();
                robotdrive.arcadeDrive(0, 0);
                System.out.println("Auto IDLE");
                if (timer <= IDLE_TIMER)
                {
                    timer++;
                } else
                {
                    timer = 0;
                    state = DRIVE; //START_SHOOT
                }
                break;
            case LOADED:
                pickup.armsDown();
                robotdrive.arcadeDrive(0, 0);
                System.out.println("Auto LOADED");
                if (timer <= STOP_TIMER)
                {
                    timer++;
                } else
                {
                    state = START_SHOOT;
                }
                break;
            case START_SHOOT:
                pickup.armsDown();
                robotdrive.arcadeDrive(0, 0);
                shooter.startShooting();
                timer = 0;
                System.out.println("Auto START_SHOOT");
                state = SHOOT;
                break;
            case SHOOT:
                pickup.armsDown();
                robotdrive.arcadeDrive(0, 0);
                System.out.println("Auto SHOOT");
                if (timer <= STOP_TIMER)
                {
                    timer++;
                } else
                {
                    state = RESET;
                    timer = 0;
                }
                break;
            case RESET:
                pickup.armsUp();
                pickup.off();
                robotdrive.arcadeDrive(0, 0);
                System.out.println("Auto RESET");
                state = DRIVE;
                if (timer <= STOP_TIMER)
                {
                    timer++;
                } else
                {
                    state = DRIVE;
                    timer = 0;
                }
                break;
            case DRIVE:
                if (timer <= START_DRIVE_TIMER)
                {
                    timer++;
                } else
                {
                    pickup.armsUp();
                    //shooter.startLoading();
                    robotdrive.arcadeDrive(-1, 0);
                    System.out.println("Auto DRIVE");
                    if (timer <= STOP_DRIVE_TIMER + START_DRIVE_TIMER)
                    {
                        timer++;
                    } else
                    {
                        timer = 0;
                        state = LOW_SHOOT; //STOP
                    }
                }
                break;
            case LOW_SHOOT:
                System.out.println("Auto LOW_SHOOT");
                if (timer <= LOW_GOAL_TIMER)
                {
                    pickup.putDown();
                    robotdrive.arcadeDrive(0.4, 0);
                    timer++;
                } else
                {
                    timer = 0;
                    state = STOP; //DRIVE_FORWARD
                }
                break;
            case DRIVE_BACKWARD:
                System.out.println("DRIVE_BACKWARD");
                if (timer <= MOVE_BACKWARD)
                {
                    robotdrive.arcadeDrive(0.5, 0);
                    pickup.putDown();
                } else
                {
                    timer = 0;
                    state = DRIVE_FORWARD;
                }
                break;
            case DRIVE_FORWARD:
                System.out.println("DRIVE_FORWARD");
                if (timer <= MOVE_FORWARD){
                robotdrive.arcadeDrive(-0.5,0);
                pickup.putDown();
                }else{
                    timer = 0;
                    state = DRIVE_BACKWARD;
                }
                break;
            case STOP:
                pickup.armsUp();
                robotdrive.arcadeDrive(0, 0);
                System.out.println("Auto STOP");
                break;
        }
        shooter.perform();
    }

    /**
     * This function is called once each time the robot enters autonomous mode.
     */
    public void autonomous()
    {
        long startTime = System.currentTimeMillis();

        System.out.println(startTime);

        state = START;

        while (isAutonomous() && isEnabled())
        {
            cameraLight.set(Relay.Value.kForward);

            performAuto();

            Timer.delay(0.02);
        }
    }

    public void drive()
    {
        double x = joystickDrive.getX();
        double y = joystickDrive.getY();
        if (Math.abs(y) > Math.abs(oldY))
        {
            if (y > oldY)
            {
                if (y - oldY > ACCEL)
                {
                    oldY += ACCEL;
                } else
                {
                    oldY = y;
                }
            } else
            {
                if (oldY - y > ACCEL)
                {
                    oldY -= ACCEL;
                } else
                {
                    oldY = y;
                }
            }
        } else
        {
            oldY = y;
        }
        if (Math.abs(x) > Math.abs(oldX))
        {
            if (x > oldX)
            {
                if (x - oldX > ACCEL)
                {
                    oldX += ACCEL;
                } else
                {
                    oldX = x;
                }
            } else
            {
                if (oldX - x > ACCEL)
                {
                    oldX -= ACCEL;
                } else
                {
                    oldX = x;
                }
            }
        } else
        {
            oldX = x;
        }
        //System.out.println(oldY + "," + oldX);
        if (Math.abs(oldY) < DEADZONE)
        {
            oldY = 0;
        }
        if (Math.abs(oldX) < DEADZONE)
        {
            oldX = 0;
        }
        robotdrive.arcadeDrive(oldY, -oldX);
    }

    /**
     * This function is called once each time the robot enters operator control.
     */
    public void operatorControl()
    {
        while (isEnabled() && (isOperatorControl() || isTest()))
        {
            drive();
            if (joystickDrive.getRawButton(2))
            {
                cameraLight.set(Relay.Value.kOn);
            } else
            {
                cameraLight.set(Relay.Value.kOff);
            }

            if (joystickShoot.getRawButton(8))
            {
                pickup.pickUp();
            }

            if (joystickShoot.getRawButton(9))
            {
                pickup.off();
            }

            if (joystickShoot.getRawButton(3))
            {
                pickup.armsDown();
            }

            if (joystickShoot.getRawButton(2))
            {
                pickup.armsUp();
            }

            if (joystickShoot.getRawButton(4))
            {
                shooter.startLoading();
            }

            if (joystickShoot.getRawButton(1))
            {
                if (shooter.startShooting() == true)
                {
                    pickup.armsDown();
                }
            }

            if (joystickShoot.getRawButton(6))
            {
                pickup.putDown();
            }

            if (joystickShoot.getRawButton(7))
            {
                pickup.pickUp();
            }

            if (joystickShoot.getRawButton(5))
            {
                pickup.putDown();
                shooter.startPassing();
            }

            shooter.perform();

            Timer.delay(0.02);
        }
    }

    /**
     * This function is called once each time the robot enters test mode.
     */
    public void test()
    {
        long startTime = System.currentTimeMillis();

        cameraLight.set(Relay.Value.kForward);

        Timer.delay(1);



        if (vision.targetHot())
        {
            long bucky = System.currentTimeMillis();

            time = bucky - startTime;

            System.out.println("Target is HOTTTTT :)" + "        " + time);
        } else
        {
            long bucky = System.currentTimeMillis();

            time = bucky - startTime;

            System.out.println("Target is NOTTT HOTT :(        " + time);
        }

        /*while (isTest() && isEnabled())
         {
            
         shooter.test();
         }*/
    }
}
