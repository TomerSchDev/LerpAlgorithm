import biuoop.DrawSurface;
import biuoop.GUI;
import biuoop.KeyboardSensor;
import biuoop.Sleeper;

import java.awt.*;
import java.util.*;
import java.util.List;

public class Main {

    public final static int WIDTH = 900;
    public final static int HEIGHT = (int) (WIDTH * (600.0 / 800.0));
    private static final int SPEED = 5;

    private static int FPS = 30;
    private static int MOVING_POINTS = 2;
    private static int DELTA = 100;
    private static int NODE_SELECTED = 0;
    private static boolean CLOSE_GAME = false;
    private static boolean LINE_VIEW = false;
    private static boolean ANCER_MODE = true;
    private static boolean ALL_POINTS_VIEW = true;
    private static boolean STOP_MOTION = false;
    private static boolean SHOW_MOVING_POINT = false;
    public static boolean ALREADY_PRESSED = false;
    public static boolean MODE_MANUAL_MOVING = false;


    public static void main(String[] args) {
        Map<String, Integer> commends = createCommends();
        GUI gui = new GUI("curve", WIDTH, HEIGHT);
        KeyboardSensor keyboardSensor = gui.getKeyboardSensor();
        Sleeper sleeper = new Sleeper();
        List<MovingPoint> movingPoints = createPoints();
        while (!CLOSE_GAME) {
            oneFrame(gui, movingPoints, commends, keyboardSensor, sleeper);
        }
    }

    private static void oneFrame(GUI gui, List<MovingPoint> movingPoints, Map<String, Integer> commends, KeyboardSensor keyboardSensor, Sleeper sleeper) {
        long start = System.currentTimeMillis();
        long nsfp = 1000 / FPS;
        double delta = 1.0 / DELTA;
        DrawSurface drawSurface = gui.getDrawSurface();
        drawSurface.setColor(Color.WHITE);
        drawSurface.fillRectangle(0, 0, WIDTH, HEIGHT);
        List<Point> points = pointsLogic(movingPoints, drawSurface);
        Point p0 = points.get(0);
        Point pn = points.get(1);
        Point old = p0;
        points.remove(p0);
        points.remove(pn);
        for (double t = 0; t <= 1; t += delta) {
            List<MyLine> lines = new ArrayList<>();
            Point newP = Algorithm.ultimateLerp(points, p0, pn, t, lines, ALL_POINTS_VIEW);
            if (LINE_VIEW) lines.add(new MyLine(newP, old));
            old = newP;
            drawLines(lines, drawSurface, t);
        }
        checkForKeyCommend(commends, keyboardSensor, movingPoints);
        if (MODE_MANUAL_MOVING) manualMovingPoints(drawSurface);
        if (CLOSE_GAME) gui.close();
        gui.show(drawSurface);
        long work = System.currentTimeMillis() - start;
        if (work <= nsfp) {
            sleeper.sleepFor(nsfp - work);
        }
    }

    private static void drawLines(List<MyLine> lines, DrawSurface drawSurface, double t) {
        drawSurface.setColor(new Color((int) ((t * 255 * 6.5) % 255), (int) (t * 255 * 5.5 % 255), (int) (t * 255 * 7.2 % 255)));
        for (MyLine line : lines) line.drawOn(drawSurface);
    }

    private static List<MovingPoint> createPoints() {
        Random random = new Random();
        List<MovingPoint> movingPoints = new ArrayList<>();
        movingPoints.add(new MovingPoint(new Point(0, HEIGHT / 2)));
        movingPoints.add(new MovingPoint(new Point(WIDTH, HEIGHT / 2)));
        for (int i = 0; i < 5; i++) {
            movingPoints.add(new MovingPoint(new Point(random.nextInt(WIDTH), random.nextInt(HEIGHT))));
        }
        return movingPoints;
    }

    private static void manualMovingPoints(DrawSurface drawSurface) {
        drawSurface.setColor(Color.WHITE);
        drawSurface.fillRectangle(1, 1, 150, 50);
        drawSurface.setColor(Color.BLACK);
        drawSurface.drawRectangle(1, 1, 150, 50);
        drawSurface.drawText(10, 30, "Selected point: " + (NODE_SELECTED + 1), 15);
    }

    private static List<Point> pointsLogic(List<MovingPoint> movingPoints, DrawSurface drawSurface) {
        List<Point> points = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            MovingPoint b = movingPoints.get(i);
            points.add(b.getCenter());
            if (!ANCER_MODE) {
                drawSurface.setColor(new Color(200, 0, 0, 255));
                drawSurface.fillCircle((int) b.getCenter().getX(), (int) b.getCenter().getY(), 5);
                if (!STOP_MOTION) b.update();
            }
        }
        for (int i = 2; i < movingPoints.size(); i++) {
            MovingPoint b = movingPoints.get(i);
            if (i < MOVING_POINTS + 2) {
                points.add(b.getCenter());
                if (SHOW_MOVING_POINT) {//drawing points
                    drawSurface.setColor(new Color(100, 0, 0, 255));
                    if (MODE_MANUAL_MOVING && (i == (NODE_SELECTED + 2))) drawSurface.setColor(new Color(19, 35, 208));
                    drawSurface.fillCircle((int) b.getCenter().getX(), (int) b.getCenter().getY(), 5);
                }
            }
            if (!STOP_MOTION) b.update();
        }
        return points;
    }

    private static Map<String, Integer> createCommends() {
        Map<String, Integer> commends = new HashMap<>();
        commends.put("space", 0);//line mode
        commends.put("s", 1);//stop movment
        commends.put("p", 2);//see points
        commends.put("+", 3);//make DElTA bigger (more percition)
        commends.put("-", 4);//make DElTA smaller (less percition)
        commends.put("a", 5);//add another point
        commends.put("d", 6);//remove point
        commends.put("e", 7);//more FPS
        commends.put("q", 8);//less FPS
        commends.put("return", 9);//close simulation
        commends.put("m", 10);//manual movement
        commends.put("k", 11);//selected point to +
        commends.put("j", 12);//selected point to -
        commends.put("left", 13);//move selected point left
        commends.put("right", 14);//move selected point right
        commends.put("up", 15);//move selected point up
        commends.put("down", 16);//move selected point down
        commends.put("g", 17);//ancer mode
        return commends;
    }

    private static void checkForKeyCommend(Map<String, Integer> commends, KeyboardSensor keyboardSensor, List<MovingPoint> movingPoints) {
        boolean pressed = false;
        for (String key : commends.keySet()) {
            if (keyboardSensor.isPressed(key)) {
                pressed = true;
                MovingPoint b;
                int addOn;
                if (!ALREADY_PRESSED) {
                    ALREADY_PRESSED = true;
                    switch (commends.get(key)) {
                        case 0:
                            ALL_POINTS_VIEW = !ALL_POINTS_VIEW;
                            LINE_VIEW = !LINE_VIEW;
                            break;
                        case 1:
                            STOP_MOTION = !STOP_MOTION;
                            break;
                        case 2:
                            SHOW_MOVING_POINT = !SHOW_MOVING_POINT;
                        case 3:
                            if (DELTA <= 200) {
                                DELTA++;
                            }
                            break;
                        case 4:
                            if (DELTA >= 15) {
                                DELTA--;
                            }
                            break;
                        case 5:
                            if (MOVING_POINTS < 5) {
                                MOVING_POINTS++;
                            }
                            break;
                        case 6:
                            if (MOVING_POINTS > 1) {
                                MOVING_POINTS--;
                            }
                            break;
                        case 7:
                            if (FPS <= 80) {
                                FPS++;
                            }
                            System.out.println(FPS);
                            break;
                        case 8:
                            if (FPS >= 30) {
                                FPS--;
                            }
                            break;
                        case 9:
                            CLOSE_GAME = true;
                            break;
                        case 10:
                            MODE_MANUAL_MOVING = !MODE_MANUAL_MOVING;
                            if (MODE_MANUAL_MOVING) {
                                SHOW_MOVING_POINT = true;
                                STOP_MOTION = true;
                                NODE_SELECTED = 0;
                            }
                            break;
                        case 11:
                            if (!MODE_MANUAL_MOVING) {
                                break;
                            }
                            addOn = 0;
                            if (ANCER_MODE)addOn=2;
                            NODE_SELECTED = (NODE_SELECTED + 1) % (MOVING_POINTS+addOn);
                            break;
                        case 12:
                            if (!MODE_MANUAL_MOVING) {
                                break;
                            }
                            addOn = 0;
                            if (ANCER_MODE)addOn=2;
                            NODE_SELECTED = (NODE_SELECTED - 1) % (MOVING_POINTS+addOn);
                            break;
                        case 13://left
                            if (!MODE_MANUAL_MOVING) {
                                break;
                            }
                            addOn = 0;
                            if (ANCER_MODE)addOn=2;
                            b = movingPoints.get(NODE_SELECTED + addOn);
                            b.setCenter(new Point(((int) b.getCenter().getX() - SPEED + WIDTH) % WIDTH, (int) b.getCenter().getY()));
                            break;
                        case 14://right
                            if (!MODE_MANUAL_MOVING) {
                                break;
                            }
                            addOn = 0;
                            if (ANCER_MODE)addOn=2;
                            b = movingPoints.get(NODE_SELECTED + addOn);
                            b.setCenter(new Point(((int) b.getCenter().getX() + SPEED + WIDTH) % WIDTH, (int) b.getCenter().getY()));
                            break;
                        case 15://up
                            if (!MODE_MANUAL_MOVING) {
                                break;
                            }
                            addOn = 0;
                            if (ANCER_MODE)addOn=2;
                            b = movingPoints.get(NODE_SELECTED + addOn);
                            b.setCenter(new Point((int) b.getCenter().getX(), (int) (b.getCenter().getY() - SPEED + HEIGHT) % HEIGHT));
                            break;
                        case 16://down
                            if (!MODE_MANUAL_MOVING) {
                                break;
                            }
                            addOn = 0;
                            if (ANCER_MODE)addOn=2;
                            b = movingPoints.get(NODE_SELECTED + addOn);
                            b.setCenter(new Point((int) b.getCenter().getX(), (int) (b.getCenter().getY() + SPEED + HEIGHT) % HEIGHT));
                            break;
                        case 17:
                            ANCER_MODE = !ANCER_MODE;
                    }
                }
            }
        }
        if (!pressed) {
            ALREADY_PRESSED = false;
        }
    }
}
