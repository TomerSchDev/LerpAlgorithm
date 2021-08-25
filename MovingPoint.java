import java.awt.*;
import java.util.Random;

public class MovingPoint {
    private Point center;
    private double dx;
    private double dy;

    public MovingPoint(Point center) {
        this.center = center;
        this.dx = new Random().nextInt(4) * 3 + 1;
        this.dy = new Random().nextInt(5) * 2 + 1;
    }

    public Point getCenter() {
        return center;
    }

    public void setCenter(Point center) {
        this.center = center;
    }

    public void update() {
        double oldX = this.center.getX();
        double oldY = this.center.getY();
        if (this.dx + oldX >= Main.WIDTH || this.dx + oldX < 0) {
            this.dx *= -1;
        }
        if (this.dy + oldY >= Main.HEIGHT || this.dy + oldY < 0) {
            this.dy *= -1;
        }
        this.center = new Point((int) (this.dx + oldX), (int)( this.dy + oldY));

    }
}
