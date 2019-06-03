package core;

import tools.Point;

public class Track {
    public Point[][] p;
    public int len = 0;
    public double xMin = 0;
    public double xMax = 0;
    public double yMin = 0;
    public double yMax = 0;

    public Track(Point[][] track) {
        p = track;
        this.len = track[0].length;

        for (int i = 0; i < this.len; i++) {
            xMin = Math.min(xMin, track[0][i].x);
            yMin = Math.min(yMin, track[0][i].y);
            xMax = Math.max(xMax, track[0][i].x);
            yMax = Math.max(yMax, track[0][i].y);
        }
    }
}
