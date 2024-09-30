package decathlon;

import common.*;

public class Deca1500M {

	private int score;
	private double A = 0.03768;
	private double B = 480;
	private double C = 1.85;
	boolean active = true;

	CalcTrackAndField calc = new CalcTrackAndField();
	InputResult inputResult = new InputResult();

	// Calculate the score based on time. All running events.
	public int calculateResult(double runningTime) throws InvalidResultException {


				if (runningTime < 150) {
					System.out.println("Value too low");
					throw new InvalidResultException("Value too low");
				} else if (runningTime > 400) {
					System.out.println("Value too high");
					throw new InvalidResultException("Value too high");
				}
				int	score = calc.calculateTrack(A, B, C, runningTime);


		System.out.println("The result is: " + score);

		return score;
	}

}
