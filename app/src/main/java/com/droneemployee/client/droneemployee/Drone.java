package com.droneemployee.client.droneemployee;

public class Drone {
	public enum State { AVAILABLE, BUSY }

    public Drone(String address) {
        this(address, null, State.AVAILABLE);
    }

    public Drone(String address, Coordinate lastPosition, State state){
		mAddress = address;
        mState = state;
        mLastPosition = lastPosition;
	}

    public String getAddress() {
        return mAddress;
    }

    public State getState() {
        return mState;
    }

    public Coordinate getLastPosition(){
        return mLastPosition;
    }

	@Override
	public String toString(){
        return "Drone(" + mAddress + ", " + mState + ", " +
                (mLastPosition != null? mLastPosition: "null") + ")";
	}

	private String mAddress;
    private State mState;
    private Coordinate mLastPosition;
}