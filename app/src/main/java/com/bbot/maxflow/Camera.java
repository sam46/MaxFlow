package com.bbot.maxflow;

import android.graphics.Matrix;

/**
 * This class is NOT immutable
 */
public class Camera {
    // camera's coordinate system:
    private float px = 0.0f, py = 0.0f;    // camera position in world
    private float ux = 1.0f, uy = 0.0f;    // i basis vector
    private float vx = 0.0f, vy = 1.0f;    // j basis vector

    /**
     * multiply the view by a 3x3 matrix whose last row is always [0 0 1]
     * @return same mutated Camera object
     */
    public Camera mult(float m0, float m1, float m2, float m3, float m4, float m5) {
        float[] res = {
                ux * m0 + uy * m3, ux * m1 + uy * m4, ux * m2 + uy * m5 + px,
                vx * m0 + vy * m3, vx * m1 + vy * m4, vx * m2 + vy * m5 + py,
        };
        ux = res[0];
        uy = res[1];
        px = res[2];
        vx = res[3];
        vy = res[4];
        py = res[5];
        return this;
    }

    /**
     * rotate this view around point (x,y)
     * @param _ang angle (radian0
     * @param _x pivot point's x-coord
     * @param _y pivot point's y-coord
     * @return same mutated Camera object
     */
    public Camera rotate(float _ang, float _x, float _y) {
        float cos = (float) Math.cos(_ang), sin = (float) Math.sin(_ang);
        mult(   cos, -sin, _x - cos * _x + sin * _y,
                sin, cos, _y - sin * _x - cos * _y  );
        return this;
    }

    /**
     * @return same mutated Camera object
     */
    public Camera scale(float _sc, float _x, float _y) {
        mult(   _sc, 0, _x - _sc * _x,
                0, _sc, _y - _sc * _y   );
        return this;
    }

    /**
     * the camera's matrix <br>
     * | ux  uy  px | <br>
     * | vx  vy  py | <br>
     * | 0   0   1  |
     */
    public Matrix getMat() {
        Matrix m = new Matrix();
        m.setValues(new float[]{
                ux, uy, px,
                vx, vy, py,
                0, 0, 1
        });
        return m;
    }

    public Matrix getInverse() {
        Matrix temp = new Matrix();
        getMat().invert(temp);
        return temp;
    }

    /**
     * Apply camera's inverse  matrix to a vector (x,y)<br>
     * Can be used to convert screen point to same point in camera's coordinates
     * @param x
     * @param y
     * @return mapped vector
     */
    public float[] applyInverse(float x, float y) {
//        float a = ux, b = uy, c = px,
//                d = vx, e = vy, f = py;
//        return new float[]{
//                (e * x - b * y + b * f - c * e) / (a * e - b * d), // x
//                (-d * x + a * y + c * d - a * f) / (a * e - b * d), // y
//                // z: (a*e-b*d)/(a*e-b*d)   will always be 1, and so we dont need x/z = x and y/z = y
//        };
        Matrix temp = getInverse();
        float[] dst = new float[2];
        temp.mapPoints(dst, new float[]{x, y});
        return dst;
    }

    /**
     * @return same mutated Camera object
     */
    public Camera translate(float x, float y) {
        px += x;
        py += y;
        return this;
    }
}
