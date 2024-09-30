package decathlon;

import common.*;

public class DecaPoleVault {

	private int score;
	private double A = 0.2797;
	private double B = 100;
	private double C = 1.35;
	boolean active = true;

	CalcTrackAndField calc = new CalcTrackAndField();
	InputResult inputResult = new InputResult();

	// Calculate the score based on distance and height. Measured in centimetres.
	public int calculateResult(double distance) throws InvalidResultException {


				if (distance < 0) {
					System.out.println("Value too low");
					throw new InvalidResultException("Value too low");

				} else if (distance > 1000) {

					System.out.println("Value too high");
					throw new InvalidResultException("Value too high");

				}

					int score = calc.calculateField(A, B, C, distance);

		System.out.println("The result is: " + score);

		return score;
	}

}
