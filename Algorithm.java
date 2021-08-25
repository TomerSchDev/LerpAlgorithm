import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class Algorithm {
    public static Point ultimateLerp(List<Point> points, Point p0, Point pn, double t, List<MyLine> lines, boolean ALL_POINTS_VIEW) {
        if (points.size() == 1) {
            Point nP1 = lerp(p0, points.get(0), t);
            Point nP2 = lerp(points.get(0), pn, t);
            if (ALL_POINTS_VIEW) {
                lines.add(new MyLine(nP1, nP2));
            }
            return lerp(nP1, nP2, t);
        }
        List<Point> l1 = new ArrayList<>(points);
        l1.remove(pn);
        List<Point> l2 = new ArrayList<>(points);
        l2.remove(p0);
        Point nP1 = ultimateLerp(l1, p0, l1.get(l1.size() - 1), t, lines, ALL_POINTS_VIEW);
        Point nP2 = ultimateLerp(l2, l2.get(0), pn, t, lines, ALL_POINTS_VIEW);
        if (ALL_POINTS_VIEW) {
           lines.add(new MyLine(nP1, nP2));
        }
        return lerp(nP1, nP2, t);
    }

    private static Point lerp(Point p0, Point p1, double t) {
        int x = (int) Math.round(p0.getX() + (p1.getX() - p0.getX()) * t);
        int y = (int) Math.round(p0.getY() + (p1.getY() - p0.getY()) * t);
        return new Point(x, y);
    }
}
