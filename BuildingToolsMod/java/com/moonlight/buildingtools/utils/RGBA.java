package com.moonlight.buildingtools.utils;

public class RGBA
{
    public static final RGBA Red = new RGBA(255, 0, 0, 255);
    public static final RGBA Green = new RGBA(0, 255, 0, 255);
    public static final RGBA Blue = new RGBA(0, 0, 255, 255);
    public static final RGBA White = new RGBA(255, 255, 255, 255);
    public static final RGBA Black = new RGBA(0, 0, 0, 255);

    public int red = 0;
    public int green = 0;
    public int blue = 0;
    public int alpha = 0;

    public RGBA(int red, int green, int blue, int alpha)
    {
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.alpha = alpha;
    }

    public RGBA copy()
    {
        return new RGBA(this.red, this.green, this.blue, this.alpha);
    }

    public RGBA setAlpha(int alpha)
    {
        RGBA copy = this.copy();
        copy.alpha = alpha;
        return copy;
    }
}