/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.wpi.first.wpilibj.templates;

import edu.wpi.first.wpilibj.Relay;
import edu.wpi.first.wpilibj.DoubleSolenoid;

/**
 *
 * @author FRC 3634
 */
public class Pickup
{

    Relay relay1;
    Relay relay2;
    DoubleSolenoid solenoid;

    public Pickup()
    {
        relay1 = new Relay(8);
        relay2 = new Relay(7);
        solenoid = new DoubleSolenoid(1, 2);
    }

    public void putDown()
    {
        relay1.set(Relay.Value.kReverse);
        relay2.set(Relay.Value.kForward);
    }
    
    public void pickUp(){
        relay1.set(Relay.Value.kForward);
        relay2.set(Relay.Value.kReverse);
    }

    public void off()
    {
        relay1.set(Relay.Value.kOff);
        relay2.set(Relay.Value.kOff);
    }

    public void armsUp()
    {
        solenoid.set(DoubleSolenoid.Value.kForward);
    }

    public void armsDown()
    {
        solenoid.set(DoubleSolenoid.Value.kReverse);
        pickUp();
    }
}
