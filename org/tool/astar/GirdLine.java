package org.tool.astar;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class GirdLine {

    public static void main(String[] args) {
        Point2D rl = getIntersectLine(5, 10, 10, 20, 7, 12, 15, 12);
        if (rl == null) {
            System.out.println("两线段不相交");
        } else {
            System.out.println("线段的交点为: (" + rl.getX() + ", " + rl.getY() + ")");
        }

        Point2D rp = getIntersectPoint(5, 10, 10, 20, 7, 12, 15, 12);
        if(rp == null) {
            System.out.println("直线不相交");
        } else {
            System.out.println("直线的交点为: (" + rp.getX() + ", " + rp.getY() + ")");
        }

        Set<Point2D> rps = getIntersectRect(5, 10, 10, 20, 7, 12, 5, 10);
        System.out.println("与矩形的交点为: " + rps);

        List<Tile> girds = getGirdRoad(0,0,60, 50 );
//        Collections.reverse(girds);
        System.out.println("网格路径为: " +  girds);
    }

    /** 获得两条直线交点 */
    public static Point2D getIntersectPoint(double x1, double y1,
            double x2, double y2,
            double x3, double y3,
            double x4, double y4) {
        // 计算向量
        double d = (x1 - x2) * (y3 - y4) - (y1 - y2) * (x3 - x4);
        if (d != 0) {
            double x = ((x3 - x4) * (x1 * y2 - y1 * x2) - (x1 - x2) * (x3 * y4 - y3 * x4)) / d;
            double y = ((y3 - y4) * (x1 * y2 - y1 * x2) - (y1 - y2) * (x3 * y4 - y3 * x4)) / d;
            return new Point2D.Double(x, y);
        }
        return null;
    }

    /** 获得两条线段的交点 */
    public static Point2D getIntersectLine(double x1, double y1, double x2, double y2,
            double x3, double y3, double x4, double y4) {
        if(Line2D.linesIntersect(x1, y1, x2, y2, x3, y3, x4, y4)) {
            return getIntersectPoint(x1, y1, x2, y2, x3, y3, x4, y4);
        }
        return null;
    }

    /** 获得线段与矩形的交点 */
    public static Set<Point2D> getIntersectRect(double x1, double y1, double x2, double y2,
            double x, double y, double w, double h) {
        Set<Point2D> points = new HashSet<>(2);
        Point2D point = getIntersectLine(x1, y1, x2, y2, x, y, x, y + h);
        if (point != null) {
            points.add(point);
        }
        point = getIntersectLine(x1, y1, x2, y2, x, y, x + w, y);
        if (point != null) {
            points.add(point);
            if (points.size() == 2) {
                return points;
            }
        }
        point = getIntersectLine(x1, y1, x2, y2, x + w, y, x + w, y + h);
        if (point != null) {
            points.add(point);
            if (points.size() == 2) {
                return points;
            }
        }
        point = getIntersectLine(x1, y1, x2, y2, x, y + h, x + w, y + h);
        if (point != null) {
            points.add(point);
        }
        return points;
    }

    /**
     * 获取网格最短路径图
     */
    public static List<Tile> getGirdRoad(int x1, int y1, int x2, int y2) {
        LinkedList<Tile> road = new LinkedList<>();
        road.add(new GirdTile(x1, y1));
        float mv = Math.max(Math.abs(x2 - x1), Math.abs(y2 - y1));
        float vx = (x2 - x1) / mv;
        float vy = (y2 - y1) / mv;
        float x = x1, y = y1;
        do {
            x += vx;
            y += vy;
            if (road.isEmpty() || !road.peek().equals(x, y)) {
                GirdTile p = new GirdTile(x, y);
                x1 = p.x;
                y1 = p.y;
                road.add(p);
            }
        } while ((vx > 0 && x1 < x2) || (vx < 0 && x1 > x2) || ((vy > 0 && y1 < y2) || (vy < 0 && y1 > y2)));
        return road;
    }

}
