package decathlon;

import common.CalcTrackAndField;
import common.InputResult;

public class DecaShotPut {

	private int score;
	private double A = 51.39;
	private double B = 1.5;
	private double C = 1.05;
	boolean active = true;
	CalcTrackAndField calc = new CalcTrackAndField();
	InputResult inputResult = new InputResult();

	// Calculate the score based on distance and height. Measured in meters.
	public int calculateResult(double distance)throws InvalidResultException {


				if (distance < 0) {
					System.out.println("Value too low");
					throw new InvalidResultException("Value too low");
				} else if (distance > 30) {

					System.out.println("Value too high");
					throw new InvalidResultException("Value too high");
				}

					int score = calc.calculateField(A, B, C, distance);

		System.out.println("The result is: " + score);

		return score;
	}

}
