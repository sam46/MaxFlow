package AutoLayout;

public class Vector {
    public double X, Y;

    public Vector mult(double mu){
        Vector result = new Vector(0, 0);
        result.X = X*mu;
        result.Y = Y*mu;
        return  result;
    }

    public Vector() {
    }
    public Point ToPoint() {
        return new Point(X, Y);
    }

    public Vector(double magnitude, double direction) {
        X = magnitude * Math.cos((Math.PI / 180.0) * direction);
        Y = magnitude * Math.sin((Math.PI / 180.0) * direction);
    }

    public static Vector add(Vector a, Vector b) {
        Vector result = new Vector(0, 0);
        result.X = a.X + b.X;
        result.Y = a.Y + b.Y;
        return result;
    }


    public static Vector mult(Vector vector, double multiplier) {
        Vector result = new Vector(1, 0);
        result.X = vector.X * multiplier;
        result.Y = vector.Y * multiplier;
        return result;
    }

}