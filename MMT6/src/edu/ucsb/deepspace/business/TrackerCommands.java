package edu.ucsb.deepspace.business;

/**
 * Possible commands that {@link TrackerGuard} accepts.<BR>
 * These are not the only commands available, but are the ones used in switch statements.
 * @author Reed Sanpore
 */
public class TrackerCommands {
	/**
	 * Possible commands:<BR>
	 * CONNECT, DISCONNECT, ABORT, WEATHER, HOME, HEALTHCHECKS, STARTUPCHECKS, TARGETTYPE
	 */
	public static enum NoArgCommands {
	CONNECT, DISCONNECT, ABORT, WEATHER, HOME, HEALTHCHECKS, STARTUPCHECKS, TARGETTYPE;
	}
	
	/**
	 * Possible commands:<BR>
	 * COMPENSATION, UPDATEREFLPOS, GOTOREFL, SAVEREFL
	 */
	public static enum OneReflCommands {
		COMPENSATION, UPDATEREFLPOS, GOTOREFL, SAVEREFL;
	}
}