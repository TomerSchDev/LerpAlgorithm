import biuoop.DrawSurface;

import java.awt.*;

public class MyLine {
    private Point p1;
    private Point p2;

    public MyLine(int x1, int y1, int x2, int y2) {
        this.p1 = new Point(x1, y1);
        this.p2 = new Point(x2, y2);
    }

    public MyLine(Point p0, Point p1) {
        this.p1 = p0;
        this.p2 = p1;
    }

    public void drawOn(DrawSurface drawSurface) {
        drawSurface.drawLine(p1.x, p1.y, p2.x, p2.y);
    }
}
