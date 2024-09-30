package heptathlon;

import common.CalcTrackAndField;
import common.InputResult;
import decathlon.InvalidResultException;

public class HeptJavelinThrow {

	private int score;
	private double A = 15.9803;
	private double B = 3.8;
	private double C = 1.04;
	boolean active = true;
	CalcTrackAndField calc = new CalcTrackAndField();
	InputResult inputResult = new InputResult();

	// Calculate the score based on distance and height. Measured in metres.
	public int calculateResult(double distance) throws InvalidResultException {


				if (distance < 0) {
					System.out.println("Value too low");
					throw new InvalidResultException("Value too low");
				} else if (distance > 110) {

					System.out.println("Value too high");
					throw new InvalidResultException("Value too high");

				}

				int	score = calc.calculateField(A, B, C, distance);

		System.out.println("The result is: " + score);

		return score;
	}

}
