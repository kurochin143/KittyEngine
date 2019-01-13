package KittyEngine.Math;

public class KMath {

    public static final float SMALL_NUM = 1.e-8f;
    public static final float PI = 3.14159265358f;
    public static final float PI2 = (2.f * PI);
    public static final float INV_PI = 0.31830988618f;
    public static final float HALF_PI = 1.57079632679f;

    public static float invSqrt(float val) {
        return 1.0f / (float)Math.sqrt(val);
    }

    public static float sin(float radians) {
        return (float)Math.sin(radians);
    }

    public static float cos(float radians) {
        return (float)Math.cos(radians);
    }

    public static float[] sinCos(float radians)
    {
        float quotient = (INV_PI*0.5f)*radians;
        if (radians >= 0.0f)
        {
            quotient = (float)((int)(quotient + 0.5f));
        }
        else
        {
            quotient = (float)((int)(quotient - 0.5f));
        }
        float y = radians - (2.0f*PI)*quotient;

        float sign;
        if (y > HALF_PI)
        {
            y = PI - y;
            sign = -1.0f;
        }
        else if (y < -HALF_PI)
        {
            y = -PI - y;
            sign = -1.0f;
        }
        else
        {
            sign = +1.0f;
        }

        float y2 = y * y;

        // 11-degree minimax approximation
        float sin = (((((-2.3889859e-08f * y2 + 2.7525562e-06f) * y2 - 0.00019840874f) * y2 + 0.0083333310f) * y2 - 0.16666667f) * y2 + 1.0f) * y;

        // 10-degree minimax approximation
        float p = ((((-2.6051615e-07f * y2 + 2.4760495e-05f) * y2 - 0.0013888378f) * y2 + 0.041666638f) * y2 - 0.5f) * y2 + 1.0f;
        float cos = sign * p;

        return new float[] {sin, cos};
    }
}
