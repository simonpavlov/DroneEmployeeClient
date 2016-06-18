package com.droneemployee.client.droneemployee;

import android.util.Log;

/**
 * Created by simon on 06.06.16.
 */
public class DroneEmployeeFetcher {
    public DroneEmployeeFetcher(){}

    public DroneATC fetchData(){
        DroneATC droneATC = new DroneATC("Current ATC");
        droneATC.getDrones().add(new Drone(genirateHexRandomString(), new Coordinate(59.903322, 30.267378, 10), Drone.State.AVAILABLE));
        droneATC.getDrones().add(new Drone(genirateHexRandomString(), new Coordinate(59.900617, 30.262357, 10), Drone.State.AVAILABLE));
        droneATC.getDrones().add(new Drone(genirateHexRandomString(), new Coordinate(59.904850, 30.258881, 10), Drone.State.AVAILABLE));

        droneATC.getPerimeter().add(new Coordinate(59.89873582670883,30.264046601951126));
        droneATC.getPerimeter().add(new Coordinate(59.90170036428617,30.269991047680378));
        droneATC.getPerimeter().add(new Coordinate(59.904817626983764,30.26839211583138));
        droneATC.getPerimeter().add(new Coordinate(59.90707758975418,30.259357094764713));
        droneATC.getPerimeter().add(new Coordinate(59.90601260377334,30.256667844951153));
        droneATC.getPerimeter().add(new Coordinate(59.90298674767275,30.25417808443308));
        droneATC.getPerimeter().add(new Coordinate(59.90122941209721,30.250471271574497));
        droneATC.getPerimeter().add(new Coordinate(59.899861914715544,30.251800306141376));
        droneATC.getPerimeter().add(new Coordinate(59.90001290883071,30.258453860878944));

        return droneATC;
    }

    public Ticket byTicket(Drone drone){
        return new Ticket(drone, genirateHexRandomString());
    }

    public void sendTask(Task task){
        //System.out.println(TAG + ": SEND: " + task);
        Log.i(TAG, "SEND: " + task);
    }

    private String genirateHexRandomString(){
        return Integer.toHexString((int) (Math.random() * 15728639 + 1048576)).toUpperCase();
    }

    private static String TAG = "DroneEmployeeFetcher";
}
