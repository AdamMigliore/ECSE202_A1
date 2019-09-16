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
	private static final double Yinit = bSize; // Initial ball location (Y) -- must be changed by the input
	private static final double TICK = 0.1; // Clock tick duration (sec)
	private static final double ETHR = 0.01; // If either Vx or Vy < ETHR STOP
	private static final double XMAX = 100.0; // Maximum value of X
	private static final double YMAX = 100.0; // Maximum value of Y
	private static final double PD = 1; // Trace point diameter
	private static final double SCALE = HEIGHT / XMAX; // Pixels/meter
	private static final double k = 0.0016;// constant K value
	private static final boolean TEST = true;// print if test is true

	// Simulation variables
	private static double Vx, Vy, X, Y, Vox, Voy, time, Vt, ScrX, ScrY, Xlast = 0.00, Ylast = 0.00, ScrXlast = 0.00,
			ScrYlast = 0.00, KEx=1.00, KEy=1.00, Xo;

	// Display components
	GRect GROUND_PLANE;
	GOval myBall;

	public void run() {
		this.resize(WIDTH, HEIGHT);

		inputParameters();
		initialDisplay();
		initializeParameters();

		do {
			calculateVariables();
			myBall.setLocation(ScrX, ScrY);
			addTracePoint();
			pause(100);
			if (KEy <= ETHR || KEx <= ETHR)
				break;
		} while (Vx > ETHR || Vy > ETHR);
	}

	// Creates and adds the ground plane to our canvas
	private void initialDisplay() {
		GROUND_PLANE = new GRect(0, HEIGHT - OFFSET, WIDTH, 3);// creates a ground plane @(0,600) of [600,3]
																// (width,height)
		GROUND_PLANE.setFilled(true);// fills the plane
		GROUND_PLANE.setColor(Color.black);// sets its color to black

		myBall = new GOval(Xinit * SCALE, Yinit * SCALE, 2 * bSize, 2 * bSize);// places it at its intial coordinate
		myBall.setFilled(true);// fill the ball
		myBall.setColor(Color.blue);// set the color to blue

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
		Voy = Vo * Math.sin(theta * Pi / 100);// Initial velocity in Y
	}

	// Calculate X, Y, Vt
	private void calculateVariables() {
		if (Vy < 0 && Y <= bSize) {

			KEx = 0.5 * Vx * Vx * (1 - loss); // Kinetic energy in X direction after collision
			KEy = 0.5 * Vy * Vy * (1 - loss); // Kinetic energy in Y direction after collision
			Vox = Math.sqrt(2 * KEx); // Resulting horizontal velocity
			Voy = Math.sqrt(2 * KEy); // Resulting vertical velocity

			Xo = Xlast;// the offset will be equal to the last coordinate
			Xlast = 0;// the last coordinate is now the beginning of the current parabola
			Ylast = bSize;// the initial y coordinate is the radius of the ball

			if (TEST)
				System.out.printf("t: %.2f X: %.2f Y: %.2f Vx: %.2f Vy:%.2f\n", time, Xo + X, Y, Vx, Vy);
		
			time = 0;// reset the time
		}
		time += TICK;

		X = Xo + Vox * Vt / g * (1 - Math.exp(-g * time / Vt)); // X position
		Y = bSize + Vt / g * (Voy + Vt) * (1 - Math.exp(-g * time / Vt)) - Vt * time; // Y position
		Vx = (X - Xlast) / TICK; // Estimate Vx from difference
		Vy = (Y - Ylast) / TICK; // Estimate Vy from difference

		ScrX = (int) ((X - bSize) * SCALE);// Convert to simulation units
		ScrY = (int) (HEIGHT - OFFSET - (Y + bSize) * SCALE);// Convert to simulation units
		ScrXlast = (int) ((Xlast - bSize) * SCALE);// Convert to simulation units
		ScrYlast = (int) (HEIGHT - OFFSET - (Ylast + bSize) * SCALE);// Convert to simulation units

		Xlast = X;// save last X
		Ylast = Y;// save last Y
	}

	// adds trace points to the simulation
	private void addTracePoint() {
		GOval tracePoint = new GOval(ScrXlast, ScrYlast, 2 * PD, 2 * PD);
		tracePoint.setFilled(true);
		tracePoint.setColor(Color.black);
		add(tracePoint);
	}

}