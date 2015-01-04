/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.wpi.first.wpilibj.templates;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Victor;
import edu.wpi.first.wpilibj.Solenoid;

/**
 *
 * @author FRC 3634
 */
public class Shoot
{

    Solenoid release;
    DoubleSolenoid shifter;
    Encoder rotationCounter;
    Victor winchMotor;
    //Victor winchMotor2;
    DigitalInput loadedConfirmed;
    int state;
    int startingRotation;
    int timer = 0;
    boolean newRelease = false;
    public static final int STOP_UNCOIL_TIMER = 30;
    public static final int ROTATION_LIMIT = 540;
    public static final int STOP_TIMER = 10;
    public static final int STOP_LOADING_TIMER = 200;
    public static final int START_SHOOTING = 2;
    public static final int START = 0;
    public static final int LOADING = 1;
    public static final int LOADED = 2;
    public static final int SHOOTING = 3;
    public static final int SHOOTING2 = 4;
    public static final int PASSING = 5;
    public static final int PASSING2 = 6;
    public static final int UNCOIL = 7;
    public static final int WAITT = 8;

    public Shoot()
    {
        release = new Solenoid(5);
        shifter = new DoubleSolenoid(3, 4);
        winchMotor = new Victor(8);
        //winchMotor2 = new Victor(7);
        state = START;
        loadedConfirmed = new DigitalInput(1);
        rotationCounter = new Encoder (8,10,false,Encoder.EncodingType.k1X);
        rotationCounter.start();
    }

    public void startLoading()
    {
        switch (state)
        {
            case START:
            case SHOOTING:
            case SHOOTING2:
            case PASSING:
            case PASSING2:
                state = LOADING;
                timer = 0;
                break;
        }
    }

    public boolean startShooting()
    {
        if (state == LOADED)
        {
            state = UNCOIL;
            startingRotation = rotationCounter.get();
            timer = 0;
            return true;
        } else
        {
            return false;
        }
    }

    public void startPassing()
    {
        if (state == LOADED || state == START)
        {
            state = PASSING;
            timer = 0;
        }
    }

    public int getState()
    {
        return state;
    }

    public void setNeutral()
    {
        shifter.set(DoubleSolenoid.Value.kReverse);
    }

    public void setEngaged()
    {
        shifter.set(DoubleSolenoid.Value.kForward);
    }

    public void load()
    {
        winchMotor.set(0.70);
        //winchMotor2.set(0.1);
    }

    public void unload()
    {
        winchMotor.set(-0.1);
        //winchMotor2.set(-0.1);
    }

    public void backDriveMotors()
    {
        winchMotor.set(-0.25);
    }
    
public void stopMotors()
    {
        winchMotor.set(0.0);
        //winchMotor2.set(0.0);
    }

    public void shoot()
    {
        release.set(true);
    }

    public void reset()
    {
        release.set(false);
    }
    
    public void test(){
        winchMotor.set(0.05);
        System.out.println(rotationCounter.get());
    }

    public void perform()
    {
        if (loadedConfirmed.get() == false)
                {
                    System.out.println("Button is pressed");
                    //state = LOADED;
                }
                
        switch (state)
        {
            case START:
                stopMotors();
                reset();
                setEngaged();
                System.out.println("Shoot START");
                break;
            case LOADING:
                load();
                reset();
                setEngaged();
                System.out.println("Shoot LOADING");
                if (loadedConfirmed.get() == false)
                {
                    System.out.println("Button is pressed");
                    state = LOADED;
                }
                if (timer <= STOP_LOADING_TIMER)
                {
                    timer++;
                } else
                {
                    state = LOADED;
                }
                break;
            case LOADED:
                stopMotors();
                reset();
                setEngaged();
                System.out.println("Shoot LOADED");
                break;
            case SHOOTING:
                if (newRelease == true)
                {
                    setEngaged();
                    load();
                    shoot();
                    if (timer <= START_SHOOTING)
                    {
                        timer++;
                    } else
                    {
                        state = SHOOTING2;
                        timer = 0;
                    }
                } else
                {
                    stopMotors();
                    shoot();
                    setNeutral();
                }
                System.out.println("Shoot SHOOTING");
                break;
            case SHOOTING2:
                setNeutral();
                shoot();
                stopMotors();
                System.out.println("Shoot SHOOTING2");
                break;
            case PASSING:
                if (newRelease == true)
                {
                    setEngaged();
                    load();
                    shoot();
                    if (timer <= START_SHOOTING)
                    {
                        timer++;
                    } else
                    {
                        state = PASSING2;
                        timer = 0;
                    }
                } else
                {
                    stopMotors();
                    shoot();
                    setEngaged();
                }
                System.out.println("Shoot PASSING");
                break;
            case PASSING2:
                setEngaged();
                stopMotors();
                shoot();
                System.out.println("Shoot PASSING2");
                break;
            case UNCOIL:
                reset();
                setEngaged();
                backDriveMotors();              
                System.out.println(startingRotation + "      " + rotationCounter.get());
                if (Math.abs(rotationCounter.get() - startingRotation) > ROTATION_LIMIT || timer > STOP_UNCOIL_TIMER){
                    stopMotors();
                    setNeutral();
                    timer = 0;
                    state = SHOOTING;
                }else{
                    timer++;
                }
                break;
            /*case WAITT:
                stopMotors();
                reset();
                setNeutral();
                System.out.println("Shoot WAITT");
                if (timer <= STOP_TIMER)
                {
                    timer++;
                } else
                {
                    state = SHOOTING;
                    timer = 0;
                }
                break;*/
        }
    }
}
