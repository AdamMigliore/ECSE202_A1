package assignment1;

import acm.program.GraphicsProgram;
import java.awt.Color;
import acm.graphics.GOval;
import acm.graphics.GRect;

public class Bounce extends GraphicsProgram {

	// input parameters
	private static double Vo, theta, loss, bSize;

	// Windows properties
	private static final int WIDTH = 600;// Width of the windows
	private static final int HEIGHT = 600;// Height of the window
	private static final int OFFSET = 200;// distance between the bottom of the window and the ground plane

	// Simulation variables Constants
	private static final double g = 9.8; // MKS gravitational constant 9.8 m/s^2
	private static final double Pi = 3.141592654; // To convert degrees to radians
	private static final double Xinit = 5.0; // Initial ball location (X)
	private static final double Yinit = bSize; // Initial ball location (Y)
	private static final double TICK = 0.1; // Clock tick duration (sec)
	private static final double ETHR = 0.01; // If either Vx or Vy < ETHR STOP
	private static final double XMAX = 100.0; // Maximum value of X
	private static final double YMAX = 100.0; // Maximum value of Y
	private static final double PD = 1; // Trace point diameter
	private static final double SCALE = HEIGHT / XMAX; // Pixels/meter
	private static final double k = 0.0016;// constant K value
	private static final boolean TEST = true;// print if test is true
	private static final int GPHeight = 3;// height of our ground plane

	// Simulation variables
	private static double Vx, Vy, X, Y, Vox, Voy, time, Vt, ScrX, ScrY, Xlast, Ylast, KEx = 1.00, KEy = 1.00,
			Xo = Xinit, Yo = Yinit;

	// Display components
	private static GRect GROUND_PLANE;
	private static GOval myBall;

	public void run() {
		this.resize(WIDTH, HEIGHT + OFFSET);

		inputParameters();
		initialDisplay();
		initializeParameters();
		pause(1500);

		do {

			calculateVariables();
			addTracePoint();

			if (TEST)
				System.out.printf("t: %.2f X: %.2f Y: %.2f Vx: %.2f Vy:%.2f ScrX:%.2f ScrY:%.2f\n", time, Xo + X, Y, Vx,
						Vy, ScrX, ScrY);

			if (Vy < 0 && Y <= bSize) {
				if (TEST)
					System.out.printf("t: %.2f X: %.2f Y: %.2f Vx: %.2f Vy:%.2f\n", time, Xo + X, Y, Vx, Vy);
			}

			myBall.setLocation(ScrX, ScrY);
			time += TICK;// increase time by a tick
			pause(TICK * 1000);
			if (KEy <= ETHR || KEx <= ETHR)
				break;
		} while (Vx > ETHR || Math.abs(Vy) >= 0);
	}

	// Creates and adds the ground plane to our canvas
	private void initialDisplay() {
		GROUND_PLANE = new GRect(0, HEIGHT, WIDTH, GPHeight);// creates a ground plane @(0,600) of [600,3](width,height)
		GROUND_PLANE.setFilled(true);// fills the plane
		GROUND_PLANE.setColor(Color.black);// sets its color to black

		myBall = new GOval(Xinit * SCALE, HEIGHT - (2 * bSize * SCALE), 2 * bSize * SCALE, 2 * bSize * SCALE);// places
		// the
		// ball
		// at
		// its
		// initial
		// position
		myBall.setFilled(true);// fill the ball
		myBall.setColor(Color.red);// set the color to blue

		add(myBall);// adds the ball to the canvas
		add(GROUND_PLANE);// adds the plane to the canvas
	}

	// Prompts the user for the initial parameters of the simulation
	// does not continue until valid inputs are given
	private void inputParameters() {
		do {
			Vo = readDouble("Enter the initial velocity of the ball in meters/second [0,100]");
			theta = readDouble("Enter the launch angle in degrees [0,90]");
			loss = readDouble("Enter the energy loss parameter [0,1]");
			bSize = readDouble("Enter the radius of the ball [0.1,5.0]");
		} while (!validateParameters(Vo, 0, 100) || !validateParameters(theta, 0, 90) || !validateParameters(loss, 0, 1)
				|| !validateParameters(bSize, 0.1, 5.0));
	}

	// Validates the parameter is between a certain interval
	private boolean validateParameters(double parameter, double min, double max) {
		if (parameter < min || parameter > max) {
			return false;
		} else {
			return true;
		}
	}

	// initializes initial simulation variables
	private void initializeParameters() {
		Vt = g / (4 * Pi * bSize * bSize * k); // Terminal velocity
		Vox = Vo * Math.cos(theta * Pi / 180);// Initial velocity in X
		Voy = Vo * Math.sin(theta * Pi / 180);// Initial velocity in Y
	}

	// Calculate X, Y, Vt
	private void calculateVariables() {
		if (Vy < 0 && Y <= bSize) {
			KEx = 0.5 * Vx * Vx * (1 - loss); // Kinetic energy in X direction after collision
			KEy = 0.5 * Vy * Vy * (1 - loss); // Kinetic energy in Y direction after collision
			Vox = Math.sqrt(2 * KEx); // Resulting horizontal velocity
			Voy = Math.sqrt(2 * KEy); // Resulting vertical velocity
			Xo = Xlast;// the offset will be equal to the last coordinate minus the beginning of the
						// simulation
			time = 0;// reset the time
			Yo = bSize;
			Ylast = bSize;
			Xlast = 0;
		}

		X = Xo + Vox * Vt / g * (1 - Math.exp(-g * time / Vt));// calculate X
		Y = Yo + Vt / g * (Voy + Vt) * (1 - Math.exp(-g * time / Vt)) - Vt * time;// Calculate Y
		Vx = (X - Xlast) / TICK;// Calculate horizontal velocity
		Vy = (Y - Ylast) / TICK;// calculate vertical velocity

		ScrX = (int) ((X) * SCALE);// convert to screen units
		ScrY = (int) (HEIGHT - (Y + 2 * bSize) * SCALE);// convert to screen units
		Xlast = X;// save last X
		Ylast = Y;// save last Y
	}

	// adds trace points to the simulation
	private void addTracePoint() {
		GOval tracePoint = new GOval(myBall.getX() + (bSize * SCALE), myBall.getY() + (bSize * SCALE), PD, PD);// follows
																												// the
																												// center
																												// of
																												// the
																												// ball
		tracePoint.setFilled(true);
		tracePoint.setColor(Color.black);
		add(tracePoint);
	}

}