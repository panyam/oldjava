package com.sri.apps.cat;


public class KnownProjection extends Projection {
    protected double ATTEN = 1.0;

    protected Circle circles[] = {
        new Circle(0.5,0.3,0.3),
        new Circle(-0.3, 0.4, 0.25),
        new Circle(-0.2,-0.3,0.2)
    };

    public KnownProjection (double atten) {
        this.ATTEN = atten;
    }

    public double projection(double phi, double x) {
        double sumAtten = 0;

        for (int i = 0;i < circles.length;i++) {
            double r = circles[i].getRadius();
            double theta = circles[i].getTheta();
            double disp = Math.abs(x - (r * Math.cos(theta - phi)));
            double attenLen = 0;
            if (disp < circles[i].R) {
                attenLen = Math.sqrt(circles[i].R * circles[i].R - disp * disp);
            }
            sumAtten += attenLen;
        }

        return Math.exp(-ATTEN * sumAtten);
    }

    class Circle {
        public double x,y,R;

        public Circle(double x,double y, double R) {
            this.x = x;
            this.y = y;
            this.R = R;
        }

        public double getRadius() {
            return Math.sqrt((x * x) + (y * y));
        }

        public double getTheta() {
            return Math.atan2(y,x);
        }
    }
}
