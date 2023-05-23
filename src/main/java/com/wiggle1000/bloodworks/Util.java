package com.wiggle1000.bloodworks;

@SuppressWarnings("unused")
public class Util
{
    public static float Lerp(float a, float b, float interpolation)
    {
        return a + interpolation * (b - a);
    }

    public static double Lerp(double a, double b, double interpolation)
    {
        return a + interpolation * (b - a);
    }
}
