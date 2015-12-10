package helpers;

import java.util.ArrayList;

import processing.core.PVector;

public class SutherlandHodgmanClipping {

	public static ArrayList<PVector> clipPolygon(ArrayList<PVector> subject, ArrayList<PVector> clipper) {
		ArrayList<PVector> result = new ArrayList<PVector>(subject);
		int len = clipper.size();
		for (int i = 0; i < len; i++) {

			int len2 = result.size();
			ArrayList<PVector> input = result;
			result = new ArrayList<>(len2);

			PVector A = clipper.get((i + len - 1) % len);
			PVector B = clipper.get(i);

			for (int j = 0; j < len2; j++) {

				PVector P = input.get((j + len2 - 1) % len2);
				PVector Q = input.get(j);

				if (isInside(A, B, Q)) {
					if (!isInside(A, B, P))
						result.add(intersection(A, B, P, Q));
					result.add(Q);
				} else if (isInside(A, B, P))
					result.add(intersection(A, B, P, Q));
			}
		}
		return result;
	}

	private static boolean isInside(PVector a, PVector b, PVector c) {
		return (a.x - c.x) * (b.y - c.y) > (a.y - c.y) * (b.x - c.x);
	}

	private static PVector intersection(PVector a, PVector b, PVector p, PVector q) {
		double A1 = b.y - a.y;
		double B1 = a.x - b.x;
		double C1 = A1 * a.x + B1 * a.y;

		double A2 = q.y - p.y;
		double B2 = p.x - q.x;
		double C2 = A2 * p.x + B2 * p.y;

		double det = A1 * B2 - A2 * B1;
		double x = (B2 * C1 - B1 * C2) / det;
		double y = (A1 * C2 - A2 * C1) / det;

		return new PVector((float) x, (float) y);
	}
}
