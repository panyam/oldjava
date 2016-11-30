package com.sri.games.snake;
import java.awt.*;
import java.util.*;
import java.awt.event.*;

public class Snake{
    Point head;

    public static final int UP = 0;
    public static final int RIGHT = 1;
    public static final int DOWN = 2;
    public static final int LEFT = 3;

    int direction;
    int headDir;
    int tailDir;

    private int length;
    private int increment = 0;

    Vector points = new Vector();

    public Snake(Point location,int direction, int length) {
        head = location;
        this.length = length + 6;
        headDir = this.direction = direction;
        tailDir = getOppositeDir(headDir);
    }

    void increaseBy(int incr) {
        increment += incr;
    }

    int getOppositeDir(int dir) {
        switch (dir) {
            case UP : return DOWN;
            case RIGHT : return LEFT;
            case DOWN : return UP;
            case LEFT : return RIGHT;
            default : return -1;
        }
    }

    void advance() {
        if (increment > 0) {
            length++;
            increment--;
        }
        if (direction == UP) head.y--;
        else if (direction == DOWN) head.y ++;
        else if (direction == RIGHT) head.x++;
        else if (direction == LEFT) head.x --;
    }

    void changeDir(int dir) {
        Point p = new Point(head.x,head.y);
        if (direction == UP || direction == DOWN) {
            if (dir == LEFT) head.x --;
            else if  (dir == RIGHT) head.x++;
        }
        else if (direction == LEFT || direction == RIGHT) {
            if (dir == UP) head.y --;
            else if  (dir == DOWN) head.y ++;
        }
        points.insertElementAt(p,0);
        this.direction = dir;
    }

    private void drawHead(Graphics g,boolean drawEyes) {
        int size = SnakeCanvas.GRID_SIZE;
        int currX = head.x;     // current grid location
        int currY = head.y;
        int cx = currX * size, cy = currY * size;
        int mid = (size/2) + 1;
        Polygon p = new Polygon();
        // view is based on the current location and the current direction
        if (direction == UP) {
            p.addPoint(cx + mid,cy);
            p.addPoint(cx,cy + size);
            p.addPoint(cx + size,cy + size);
            p.addPoint(cx + mid,cy);
            g.fillPolygon(p);
            if (drawEyes) { }
        }
        else if (direction == DOWN) {
            p.addPoint(cx,cy);
            p.addPoint(cx + mid,cy + size);
            p.addPoint(cx + size,cy);
            p.addPoint(cx,cy);
            g.fillPolygon(p);
            if (drawEyes) { }
        }
        else if (direction == RIGHT) {
            p.addPoint(cx,cy);
            p.addPoint(cx + size,cy + mid);
            p.addPoint(cx,cy + size);
            p.addPoint(cx,cy);
            g.fillPolygon(p);
            if (drawEyes) { }
        }
        else {
            p.addPoint(cx + size,cy);
            p.addPoint(cx,cy + mid);
            p.addPoint(cx + size,cy + size);
            p.addPoint(cx + size,cy);
            g.fillPolygon(p);
            if (drawEyes) { }
        }
    }

    private void drawLimb(Graphics g,Point from,Point to) {
        int size = SnakeCanvas.GRID_SIZE;
        int px = from.x * size, py = from.y * size;
        int cx = to.x * size, cy = to.y * size;
        int width = Math.abs(cx - px) + size;
        int height = Math.abs(cy - py) + size;

        px = Math.min(px,cx);
        py = Math.min(py,cy);

        g.fillRect(px,py,width,height);
    }

    private void drawTail(Graphics g,Point from,int len) {
        Point to = new Point(from);
        int n = len - 1;

        if (tailDir == UP) to.y -= n;
        else if (tailDir == DOWN) to.y += n;
        else if (tailDir == RIGHT) to.x += n;
        else to.x -= n;

        drawLimb(g,from,to);
        //int dir = direction;
    }

    void drawSnake(Graphics g) {
        int len = length - 1;       // why -1 coz we are about to draw
                                    // the head as well....

        int i = 0;

        Point afterHead = new Point(head);
        Point prev, curr, prevprev;

        drawHead(g,true);

        if (points.size() != 0) {
            if (direction == UP) afterHead.y++;
            else if (direction == RIGHT) afterHead.x--;
            else if (direction == LEFT) afterHead.x++;
            else afterHead.y--;
        }
        else {
            tailDir = getOppositeDir(direction);

                    // if there are no points available 
                    // then we need to have the next point after the
                    // head in the direction of the tail.
            if (tailDir == UP) afterHead.y--;
            else if (tailDir == RIGHT) afterHead.x++;
            else if (tailDir == LEFT) afterHead.x--;
            else afterHead.y++;
        }

        prevprev = prev = curr = afterHead;

        while (len > 0) {
            prevprev = prev;
            prev = curr;

            if (i == points.size()) {
                    // it means that we have reached the end of it...
                    // So just draw the tail now...

                drawTail(g,prev,len);
                len = 0;
                for (;i < points.size();i++) 
                            points.removeElementAt(points.size()-1);
                return ;
            }

            curr = (Point)points.elementAt(i++);

            // draw the limb only if the remaining length
            // is greater than the distance between the two points...
            int dist = Math.abs(curr.x == prev.x ?  curr.y - prev.y : 
                                                    curr.x - prev.x);

            if (len >= dist) {
                drawLimb(g,prev,curr);
                len -= dist;
            }
            else {
                        // if there is not enough of the snake left
                        // ie length <= dist, then we also need
                        // to change the snake's tail direction.
                        //
                        // This will be the direction of the last 2 points.
                    
                        // first find the direction....
                if (curr.x == prev.x) {
                                // means the direction is up or down.
                    tailDir = (curr.y < prev.y ? UP : DOWN);
                }
                else {
                    tailDir = (curr.x < prev.x ? LEFT : RIGHT);
                }
                drawTail(g,prev,len);
                len -= dist;
            }
        }

        for (;i < points.size();i++) points.removeElementAt(points.size()-1);
    }

    public boolean intersectsItself(Point p,boolean first) {
        int size = points.size();

        Point prev = head, curr = head;
        Enumeration e = points.elements();
        int len = length;

        while (e.hasMoreElements()) {
            prev = curr;
            curr = (Point)e.nextElement();

            int dist = Math.abs(prev.x == curr.x ?  prev.y - curr.y : 
                                                    prev.x - curr.x);

            len -= dist;
            if (len < 0) {
                    // it means we have reached the last set of points.
                int l = -len;
                if (curr.x == prev.x) {
                    if (curr.y < prev.y) curr.y += l;
                    else curr.y -= l;
                }
                else {      // if curry == prevy
                    if (curr.x < prev.x) curr.x += l;
                    else curr.x -= l;
                }
            }

                // now that we have 2 points, check if 
                // head lies between prev and curr 
            if (prev.x == curr.x) {
                if (p.x == prev.x) {
                    if (prev.y < curr.y) {
                        if (!first) {
                            if (p.y >= prev.y && p.y <= curr.y) 
                                                                return true;
                        }
                        else {
                            if (p.y > prev.y && p.y <= curr.y) 
                                                                return true;
                            first = false;
                        }
                    }
                    else {
                        if (!first) {
                            if (p.y >= curr.y && p.y <= prev.y) 
                                                                return true;
                        }
                        else {
                            if (p.y >= curr.y && p.y < prev.y) 
                                                                return true;
                            first = false;
                        }
                    }
                }
            }
            else {
                if (p.y == prev.y) {
                    if (prev.x < curr.x) {
                        if (!first) {
                            if (p.x >= prev.x && p.x <= curr.x) 
                                                                return true;
                        }
                        else {
                            if (p.x > prev.x && p.x <= curr.x) 
                                                                return true;
                            first = false;
                        }
                    }
                    else {
                        if (!first) {
                            if (p.x >= curr.x && p.x <= prev.x) 
                                                                return true;
                        }
                        else {
                            if (p.x >= curr.x && p.x < prev.x) 
                                                                return true;
                            first = false;
                        }
                    }
                }
            }
        }
        return false;
    }
}
