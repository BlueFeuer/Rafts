package com.bluefire.rafts.data;

public class Vec2d
{
    /** An immutable vector with {@code 0.0F} as the x and y components. */
    public static final Vec2d ZERO = new Vec2d(0.0F, 0.0F);
    /** An immutable vector with {@code 1.0F} as the x and y components. */
    public static final Vec2d ONE = new Vec2d(1.0F, 1.0F);
    /** An immutable vector with {@code 1.0F} as the x component. */
    public static final Vec2d UNIT_X = new Vec2d(1.0F, 0.0F);
    /** An immutable vector with {@code -1.0F} as the x component. */
    public static final Vec2d NEGATIVE_UNIT_X = new Vec2d(-1.0F, 0.0F);
    /** An immutable vector with {@code 1.0F} as the y component. */
    public static final Vec2d UNIT_Y = new Vec2d(0.0F, 1.0F);
    /** An immutable vector with {@code -1.0F} as the y component. */
    public static final Vec2d NEGATIVE_UNIT_Y = new Vec2d(0.0F, -1.0F);
    /** An immutable vector with {@link Float#MAX_VALUE} as the x and y components. */
    public static final Vec2d MAX = new Vec2d(Double.MAX_VALUE, Double.MAX_VALUE);
    /** An immutable vector with {@link Float#MIN_VALUE} as the x and y components. */
    public static final Vec2d MIN = new Vec2d(Double.MIN_VALUE, Double.MIN_VALUE);

    /** The x component of this vector. */
    public final double x;
    /** The y component of this vector. */
    public final double y;

    public Vec2d(double xIn, double yIn)
    {
        this.x = xIn;
        this.y = yIn;
    }
}