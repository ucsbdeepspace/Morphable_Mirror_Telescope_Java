package edu.ucsb.deepspace.business;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import smx.tracker.MeasurePointData;
import smx.tracker.TrackerException;
import edu.ucsb.deepspace.persistence.CompensationIO;

/**
 * Wraps {@link Tracker} to prevent any exceptions escaping.<P>
 * Basically "guards" the {@link Tracker} from being exposed to anyone else.<BR>
 * The methods in this class generally return futures so that {@link Secretary} can wait for them.
 * @author Reed Sanpore
 */
public class TrackerGuard {
	
	private Tracker trk;
	private final ExecutorService exec = Executors.newFixedThreadPool(1);
	
	/**
	 * Construct the tracker guard.
	 * @param ipAddress IP address of the tracker, "192.168.1.4"
	 * @param userName "user"
	 * @param password ""
	 */
	public TrackerGuard(String ipAddress, String userName, String password) {
		this.trk = new Tracker(ipAddress, userName, password);
	}
	
	/**
	 * Causes the {@link ExecutorService} to shutdown.
	 */
	public void kill() {
		exec.shutdown();
	}
	
	/**
	 * Execute a simple command that does not require an argument.
	 * @param c see {@link TrackerCommands.NoArgCommands} for available commands
	 * @return
	 */
	public Future<String> commandNoArg(final TrackerCommands.NoArgCommands c) {
		Callable<String> call = new Callable<String>() {
			@Override
			public String call() throws Exception {
				String result = "";
				try {
					switch (c) {
						case CONNECT:
							result = trk.connect(); break;
						case DISCONNECT:
							result = trk.disconnect(); break;
						case ABORT:
							result = trk.abort(); break;
						case WEATHER:
							result = trk.weather(); break;
						case HOME:
							result = trk.home(); break;
						case HEALTHCHECKS:
							result = trk.healthChecks(); break;
						case STARTUPCHECKS:
							result = trk.startupChecks(); break;
						case TARGETTYPE:
							result = trk.targetType(); break;
						default:
							assert false;  //No other types are allowed.
							System.out.println("TrackerGuard.commandNoArg no argument given.  Error.");
					}
					
				} catch (TrackerException e) {
					result = "Message from exception: " + e.getMessage();
					e.printStackTrace();
				}
				return result;
			}
		};
		return exec.submit(call);
	}
	
	/**
	 * Move to the {@link Coordinate} specified by c.
	 * @param c
	 * @return
	 */
	public Future<String> move(final Coordinate c) {
		Callable<String> call = new Callable<String>() {
			@Override
			public String call() throws Exception {
				String result = "";
				try {
					result = trk.move(c.getRadius(), c.getTheta(), c.getPhi(), false);
				} catch (TrackerException e) {
					result = "Message from exception: " + e.getMessage();
					e.printStackTrace();
				}
				return result;
			}	
		};
		return exec.submit(call);
	}
	
	/**
	 * See {@link Tracker#initialize}
	 */
	public Future<String> initialize(final boolean minimum) {
		Callable<String> call = new Callable<String>() {
			@Override
			public String call() throws Exception {
				String result = "";
				try {
					result = trk.initialize(minimum);
				} catch (TrackerException e) {
					result = "Message from exception: " + e.getMessage();
					e.printStackTrace();
				}
				return result;
			}	
		};
		return exec.submit(call);
	}
	
	/**
	 * See {@link Tracker#search}
	 */
	public Future<String> search(final double radius) {
		Callable<String> call = new Callable<String>() {
			@Override
			public String call() throws Exception {
				String result = "";
				try {
					result = trk.search(radius);
				} catch (TrackerException e) {
					result = "Message from exception: " + e.getMessage();
					e.printStackTrace();
				}
				return result;
			}	
		};
		return exec.submit(call);
	}
	
	/**
	 * See {@link Tracker#setMeasureMode}
	 */
	public Future<String> setMeasureMode(final TrackerMeasureMode mode) {
		Callable<String> call = new Callable<String>() {
			@Override
			public String call() {
				String result = "";
				try {
					result = trk.setMeasureMode(mode);
				} catch (TrackerException e) {
					result = "Message from exception: " + e.getMessage();
					e.printStackTrace();
				}
				return result;
			}	
		};
		return exec.submit(call);
	}
	
	/**
	 * Measures the offset of the tracker's angular encoders.  Known as a "compensation".<P>
	 * Measures the {@link Reflectable}'s position.  Then flips the tracker around to look
	 * at it from the other side.  <i>Ideally</i>, these would be the same.  The difference
	 * between these two coordinates is known as the offset.<BR><BR>
	 * Currently this data is not used.  That's for you to implement!
	 * @author Reed Sanpore
	 */
	public Future<String> compensate(final String refName) {
		Callable<String> call = new Callable<String>() {
			@Override
			public String call() {
				String result = "";
				Target t = (Target) Bookkeeper.getInstance().getReflectables().get(refName);
				try {
					trk.move(t.getCoord());
					Coordinate before = new Coordinate(trk.measure(10));
					trk.move(0, -2*before.getTheta(), Math.PI, true);//flip its position. ends up pointing at same spot
					Coordinate after = new Coordinate(trk.measure(10));
					double radiusError = before.getRadius() - after.getRadius();
					double thetaError = before.getTheta() - (after.getTheta() - -2*before.getTheta());
					double phiError = before.getPhi() - (after.getPhi() + Math.PI);
					Calendar time = new GregorianCalendar(TimeZone.getDefault());
					String dataOut = radiusError + "," + thetaError + "," + phiError;
					CompensationIO.appendToFile(dataOut, time, t.getName());
					result = "Compensation recorded.\n";
				} catch (TrackerException e) {
					result = "Message from exception: " + e.getMessage();
					e.printStackTrace();
				}
				return result;
			}	
		};
		return exec.submit(call);
	}
	
	/**
	 * Used to create a new {@link Reflectable}.<P>
	 * Currently it will be a {@link Target}; there is no way to make a new {@link Actuator}, except
	 * by manually creating it.
	 * @param name
	 * @return
	 */
	public Future<String> saveRefl(final String name) {
		Callable<String> call = new Callable<String>() {
			@Override
			public String call() throws Exception {
				try {
					Coordinate c = new Coordinate(trk.measure(1));
					Reflectable r = new Target(name, c);
					Map<String, Reflectable> reflectables = Bookkeeper.getInstance().getReflectables();
					reflectables.put(name, r);
					Bookkeeper.getInstance().setReflectables(reflectables);
				} catch (TrackerException e) {
					e.printStackTrace();
				}
				return "Reflectable saved.\n";
			}
		};
		return exec.submit(call);
	}
	
	/**
	 * Causes the tracker to point at the {@link Reflectable} specified by the name.
	 * @param reflName
	 * @return
	 */
	public Future<String> goToRef(final String reflName) {
		Callable<String> call = new Callable<String>() {
			@Override
			public String call() throws Exception {
				Target t = (Target) Bookkeeper.getInstance().getReflectables().get(reflName);
				String result = "";
				try {
					result = trk.move(t.getCoord());
				} catch (TrackerException e) {
					result = "Message from exception: " + e.getMessage();
					e.printStackTrace();
				}
				return result;
			}	
		};
		return exec.submit(call);
	}
	
	/**
	 * Updates the {@link Reflectable}'s position.<P>
	 * Perform's a measurement at the currently location.  Converts that measurement into a 
	 * {@link Coordinate}.  Calls {@link Reflectable#setCoord()} to update the position.
	 * @param reflName
	 * @return
	 */
	public Future<String> updateReflPos(final String reflName) {
		Callable<String> call = new Callable<String>() {
			@Override
			public String call() throws Exception {
				Target t = (Target) Bookkeeper.getInstance().getReflectables().get(reflName);
				String result = "";
				try {
					Coordinate coord = new Coordinate(trk.measure(1));
					t.setCoord(coord);
					result = "Position updated.";
				} catch (TrackerException e) {
					result = "Message from exception: " + e.getMessage();
					e.printStackTrace();
				}
				return result;
			}	
		};
		return exec.submit(call);
	}
	
	/**
	 * Moves to the {@link Actuator}'s position.  Performs a measurement.  Then 
	 * updates the actuator's position with the measured coordinate.
	 * @param actName
	 * @param numPoints
	 * @return
	 */
	public Coordinate measThenUpdate(String actName, int numPoints) {
		Target t = (Target) Bookkeeper.getInstance().getReflectables().get(actName);
		
		try {
			trk.move(t.getCoord());
			if(!trk.reflPresent()) {
				trk.move(t.getCoord());
			}
			Coordinate coord = new Coordinate(trk.measure(numPoints));
			t.setCoord(coord);
			return coord.toCartesian();
		} catch (TrackerException e) {
			e.printStackTrace();
			throw new Error("problem from measThenUpdate in TrackerGuard");
		}
	}
	
	/**
	 * Obtains the list of {@link Actuator}s from {@link Bookkeeper}.  Then measures and updates
	 * their position.  Then sets the goalDistance of each actuator to its current position.
	 */
	public void actGoalPos() {
		Map<String, Actuator> acts = Bookkeeper.getInstance().getActuators();
		for (String s : acts.keySet()) {
			Coordinate c = measThenUpdate(s, 3);
			acts.get(s).setGoalDist(c.getRadius());
		}
	}
	
	/**
	 * Performs a measurement as configured by the mc.  Used when performing a test.
	 * @param mc
	 * @return
	 */
	public String testMeasure(MeasurementConfig mc) {
		try {
			String weather = trk.weatherCsv();
			Reflectable r = Bookkeeper.getInstance().getReflectables().get(mc.getName());
			trk.move(r.getCoord());
			trk.measure(mc);
			String out = "";
			out = r.getName() + "," + r.getCoord().toCsv() + "," + weather;
			return out;
		} catch (TrackerException e) {
			e.printStackTrace();
		}
		throw new Error("error with testMeasure in TrackerGuard");
	}
	
	/**
	 * Determines if a reflectable is present or not.
	 * @return "present" or "not present" or "error" if something went wrong
	 */
	public String reflPresent() {
		String out = "error";
		try {
			boolean present = trk.reflPresent();
			if (present) out = "present";
			else out = "not present";
		} catch (TrackerException e) {
			e.printStackTrace();
		}
		return out;
	}
	
	/**
	 * Moves to the specified position.
	 * @param radius
	 * @param theta
	 * @param phi
	 * @return "moved" if the move was sucessful
	 */
	public String move2(final double radius, final double theta, final double phi) {
		String out = "error";
		try {
			trk.move(radius, theta, phi, false);
			return "moved";
		} catch (TrackerException e) {
			e.printStackTrace();
		}
		return out;
	}
	
	/**
	 * Searches for a reflectable within the specified radius.
	 * @param radius
	 * @return "present" or "not present" or "error" if something went wrong
	 */
	public String search2(final double radius) {
		String out = "error";
		try {
			trk.search(radius);
			boolean present = trk.reflPresent();
			if (present) out = "present";
			else out = "not present";
		} catch (TrackerException e) {
			e.printStackTrace();
		}
		return out;
	}
	
	/**
	 * This should only be used when a reflectable is present.<P>
	 * @return the CSV value of the reflectables current position
	 */
	public String getCoordinates() {
		String out = "error";
		try {
			boolean present = trk.reflPresent();
			if (!present) {
				out = "no reflectable";
				return out;
			}
			MeasurePointData[] point = trk.measure(1);
			Coordinate c = new Coordinate(point);
			return c.toCsv();
		} catch (TrackerException e) {
			e.printStackTrace();
		}
		return out;
	}
	
}