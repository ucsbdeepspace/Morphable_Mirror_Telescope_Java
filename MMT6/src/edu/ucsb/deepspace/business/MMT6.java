package edu.ucsb.deepspace.business;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import edu.ucsb.deepspace.gui.MMT6MainWindow;

public class MMT6 {
	
	private static Mediator mediator;
	private static MMT6MainWindow window;
	
	public static void main(String[] args) {
		mediator = Mediator.getInstance();
		Shell shell = new Shell(Display.getDefault(), SWT.CLOSE | SWT.TITLE | SWT.MIN | SWT.MAX | SWT.RESIZE);
		window = new MMT6MainWindow(shell, SWT.NULL, mediator);
		window.alive();
	}
	
}