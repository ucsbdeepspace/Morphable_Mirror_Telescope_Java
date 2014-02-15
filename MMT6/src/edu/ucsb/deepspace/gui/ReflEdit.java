package edu.ucsb.deepspace.gui;

import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import edu.ucsb.deepspace.business.Actuator;
import edu.ucsb.deepspace.business.Bookkeeper;
import edu.ucsb.deepspace.business.Coordinate;
import edu.ucsb.deepspace.business.Reflectable;

public class ReflEdit {
	
	private final Display display;
	private final Shell shell;
	private Label zenith;
	private Button save;
	private Text encodeValVal;
	private Text linPotValVal;
	private Text maxDistVal;
	private Text minDistVal;
	private Text goalDistVal;
	private Text portVal;
	private Text distanceVal;
	private Text zenithVal;
	private Text azimuthVal;
	private Text nameVal;
	private Text typeVal;
	private Label port;
	private Label encodeVal;
	private Label linPotVal;
	private Label maxDist;
	private Label minDist;
	private Label goalDist;
	private Label distance;
	private Label azimuth;
	private Label name;
	private Label type;
	private Actuator act;
	private Reflectable r;
	private Bookkeeper bk;
	private String oldname;
	
	ReflEdit(String reflName) {
		this.bk = Bookkeeper.getInstance();
		display = Display.getDefault();
		shell = new Shell(display, SWT.CLOSE | SWT.TITLE | SWT.MIN | SWT.MAX);
		//shell.setImage(SWTResourceManager.getImage("images/16x16.png"));
		shell.setText("MMT Control - Reflectable Editor");
		shell.setSize(213, 343);

		this.r = bk.getReflectables().get(reflName);
		this.oldname = reflName;

		type = new Label(shell, SWT.NONE);
		type.setText("Type:");
		type.setBounds(12, 12, 60, 15);

		name = new Label(shell, SWT.NONE);
		name.setText("Name:");
		name.setBounds(12, 33, 60, 15);

		azimuth = new Label(shell, SWT.NONE);
		azimuth.setText("Azimuth:");
		azimuth.setBounds(12, 54, 60, 15);

		zenith = new Label(shell, SWT.NONE);
		zenith.setText("Zenith:");
		zenith.setBounds(12, 75, 60, 15);

		distance = new Label(shell, SWT.NONE);
		distance.setText("Distance:");
		distance.setBounds(12, 96, 60, 15);

		typeVal = new Text(shell, SWT.NONE);
		typeVal.setText(r.getType());
		typeVal.setBounds(94, 12, 60, 15);
		typeVal.setEditable(false);

		nameVal = new Text(shell, SWT.NONE);
		nameVal.setText(r.getName());
		nameVal.setBounds(94, 33, 100, 15);

		azimuthVal = new Text(shell, SWT.NONE);
		azimuthVal.setText(String.valueOf(r.getCoord().getPhi()));
		azimuthVal.setBounds(94, 54, 100, 15);

		zenithVal = new Text(shell, SWT.NONE);
		zenithVal.setText(String.valueOf(r.getCoord().getTheta()));
		zenithVal.setBounds(94, 75, 100, 15);

		distanceVal = new Text(shell, SWT.NONE);
		distanceVal.setText(String.valueOf(r.getCoord().getRadius()));
		distanceVal.setBounds(94, 96, 100, 15);

		if (r instanceof Actuator) {
			act = (Actuator) r;

			port = new Label(shell, SWT.NONE);
			port.setText("Port:");
			port.setBounds(12, 117, 60, 15);

			goalDist = new Label(shell, SWT.NONE);
			goalDist.setText("GoalDist:");
			goalDist.setBounds(12, 138, 60, 15);

			minDist = new Label(shell, SWT.NONE);
			minDist.setText("MinDist:");
			minDist.setBounds(12, 159, 60, 15);

			maxDist = new Label(shell, SWT.NONE);
			maxDist.setText("MaxDist:");
			maxDist.setBounds(12, 180, 60, 15);

			linPotVal = new Label(shell, SWT.NONE);
			linPotVal.setText("LinPotVal:");
			linPotVal.setBounds(12, 201, 60, 15);

			encodeVal = new Label(shell, SWT.NONE);
			encodeVal.setText("EncodeVal:");
			encodeVal.setBounds(12, 222, 60, 15);

			portVal = new Text(shell, SWT.NONE);
			portVal.setText(String.valueOf(act.getPort()));
			portVal.setBounds(94, 117, 100, 15);

			goalDistVal = new Text(shell, SWT.NONE);
			goalDistVal.setText(String.valueOf(act.getGoalDist()));
			goalDistVal.setBounds(94, 138, 100, 15);

			minDistVal = new Text(shell, SWT.NONE);
			minDistVal.setText(String.valueOf(act.getMinDist()));
			minDistVal.setBounds(94, 159, 100, 15);

			maxDistVal = new Text(shell, SWT.NONE);
			maxDistVal.setText(String.valueOf(act.getMaxDist()));
			maxDistVal.setBounds(94, 180, 100, 15);

			linPotValVal = new Text(shell, SWT.NONE);
			linPotValVal.setText(String.valueOf(act.getLinPotVal()));
			linPotValVal.setBounds(94, 201, 100, 15);

			encodeValVal = new Text(shell, SWT.NONE);
			encodeValVal.setText(String.valueOf(act.getEncodeVal()));
			encodeValVal.setBounds(94, 222, 100, 15);

			save = new Button(shell, SWT.PUSH | SWT.CENTER);
			save.setText("Save");
			save.setBounds(59, 259, 60, 30);
			save.addMouseListener(new MouseAdapter() {
				public void mouseDown(MouseEvent evt) {
					save();
				}
			});
		}
	}
	
	void show() {
		shell.open();
		while (!shell.isDisposed())
			if (!display.readAndDispatch())
				display.sleep();	
	}
	
	public void save() {
		r.setName(nameVal.getText());
		
		double rad = Double.parseDouble(distanceVal.getText());
		double theta = Double.parseDouble(zenithVal.getText());
		double phi = Double.parseDouble(azimuthVal.getText());
		Coordinate c = new Coordinate(rad, theta, phi, false);
		r.setCoord(c);
		if (r instanceof Actuator) {
			act.setPort(Integer.parseInt(portVal.getText()));
			act.setGoalDist(Double.parseDouble(goalDistVal.getText()));
			act.setMinDist(Double.parseDouble(minDistVal.getText()));
			act.setMaxDist(Double.parseDouble(maxDistVal.getText()));
			act.setLinPotVal(Double.parseDouble(linPotValVal.getText()));
			act.setEncodeVal(Double.parseDouble(encodeValVal.getText()));
		}
		Map<String, Reflectable> reflectables = bk.getReflectables();
		reflectables.remove(oldname);
		reflectables.put(nameVal.getText(), r);
		bk.setReflectables(reflectables);
	}
}