package decathlon;

import common.CalcTrackAndField;
import common.InputResult;

public class DecaJavelinThrow {

	private int score;
	private double A = 10.14;
	private double B = 7;
	private double C = 1.08;
	boolean active = true;
	CalcTrackAndField calc = new CalcTrackAndField();
	InputResult inputResult = new InputResult();

	// Calculate the score based on distance and height. Measured in meters.
	public int calculateResult(double distance) throws InvalidResultException {


				if (distance < 0) {
					System.out.println("Value too low");
					throw new InvalidResultException("Value too low");
				} else if (distance > 110) {
					System.out.println("Value too high");
					throw new InvalidResultException("Value too high");

				}

					score = calc.calculateField(A, B, C, distance);

		System.out.println("The result is: " + score);

		return score;
	}

}
