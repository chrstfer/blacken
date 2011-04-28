package com.googlecode.blacken.colors;

import com.googlecode.blacken.exceptions.InvalidStringFormatException;

/**
 * A container class for a number of convenience functions.
 * 
 * @author Steven Black
 */
public class ColorHelper {
    /**
     * Is the color fully transparent?
     * 
     * <p>Please note, a partially transparent color would only be translucent.
     * If this is what you want, you may want to use !isOpaque.</p>
     * 
     * @param color the color to check
     * @see #isOpaque
     * @return true if the alpha channel is fully transparent, false otherwise
     */
    static boolean isTransparent(int color) {
        return (color & 0xFF000000) == 0 ? true : false;
    }

    /**
     * Is the color fully opaque?
     * 
     * @param color the color to check
     * @see isTransparent
     * @see makeOpaque
     * @return true if the alpha channel is fully opaque, false otherwise
     */
    static boolean isOpaque(int color) {
        return (color & 0xFF000000) == 0xFF000000 ? true : false;
    }

    /**
     * For a given color value, decide whether black or white is visible
     * 
     * Same as calling makevisible with a threshold of 66.
     * 
     * @param value
     * @see ColorHelper#makeVisible(int, int)
     * @return black or white, expressed as 0xAARRGGBB
     */
    public static int makeVisible(int value) {
        return makeVisible(value, 66, 0xFFffffff, 0xFF000000);
    }

    /**
     * For a given color value, decide whether black or white is visible
     * 
     * <p>The provided threshold is used to determine whether to go black or
     * white.</p>
     * 
     * @param value color value as 0xAARRRGGBB (or 0xRRGGBB)
     * @param threshold number between 0 and 100 (smaller results in more black)
     * @return black or white, expressed as 0xAARRGGBB
     */
    public static int makeVisible(int value, int threshold) {
        return makeVisible(value, threshold, 0xFFffffff, 0xFF000000);
    }
    /**
     * For a given color value, decide whether "black" or "white" is visible
     * 
     * @param value color value as 0xAARRRGGBB (or 0xRRGGBB)
     * @param threshold number between 0 and 100 (smaller results in more black)
     * @param white index to use for white
     * @param black index to use for black
     * @return the <code>white</code> or <code>black> value
     */
    public static int makeVisible(int value, int threshold, 
                                  int white, int black) {
        int[] parts = makeComponents(value);
        int g = (parts[0] * 20 + parts[1] * 69 + parts[2] * 11) / 255;
        if (g < threshold) {
            return white;
        } else {
            return black;
        }
    }
    
    
    /**
     * Make a color opaque. (Need not have valid alpha data.)
     * 
     * @param value an RGB or RGBA color (0xAARRGGBB)
     * @return RGBA color, with alpha channel set to fully opaque.
     */
    public static int makeOpaque(int value) {
        return (value & 0x00ffffff) | 0xff000000;
    }

    /**
     * Force fully transparent colors to become opaque.
     * 
     * <p>We support both RGB and RGBA colors. The problem is that when RGB
     * color information is used somewhere that expects RGBA color, the color
     * winds up being fully transparent. This solves the problem by taking
     * any fully transparent color and turning it fully opaque.</p>
     * 
     * <p>This means that the closest you can then come to fully transparent 
     * is 0.4% opaque (1/255). This approximation should be good enough for 
     * most roguelike games. Note the name of this function. The isTransparent
     * function will never return true for data returned by this function.
     * Anyplace you would use isTransparent, you'll need to use !isOpaque.)</p>
     * 
     * @param color either RGB or RGBA data
     * @return valid RGBA data, with alpha channel never zero
     */
    static int neverTransparent(int color) {
        if (isTransparent(color)) {
            color = makeOpaque(color);
        }
        return color;
    }

    /**
     * Check to see if there is a reasonable expectation of a valid color.
     * 
     * <p>This checks to see if the string is a color definition. That is, the
     * string is not a color name, but is instead an RGB or RGBA color 
     * definition.</p>
     * 
     * @param color string to check
     * @return true when it may be a valid color description, false otherwise
     */
    public static boolean isColorDefinition(String color) {
        boolean ret = false;
        if (color == null) return false;
        if (color.startsWith("#") || color.startsWith("0x") || 
                                     color.startsWith("0X")) {
            ret = true;
        }
        return ret;
    }

    /**
     * Set the color based upon a string. 
     * 
     * <p>The color can be defined in a number of ways:
     * <ul>
     * <li>0xAARRGGBB</li>
     * <li>0xRRGGBB (opaque)</li>
     * <li>#RGB (web short-cut for #RRGGBB)</li>
     * <li>#RRGGBB (web standard)</li>
     * </ul>
     * </p>
     * 
     * <p>Note that this does not handle named colors. If you want that, check
     * out the ColorNames class. If you need to check that a string could
     * be a color, try the canStringColor command.</p>
     * 
     * @param color the color we are referencing
     * @return new color
     * @throws InvalidStringFormatException
     */
    public static int makeColor(String color)
            throws InvalidStringFormatException {
        if (color.startsWith("#")) {
            color = color.substring(1);
            int c = Integer.parseInt(color, 16);
            if (color.length() == 3) {
                // web shorthand #RGB == #RRGGBB
                return makeColor((c & 0xf00) >> 8, (c & 0x0f0) >> 4,
                                 (c & 0x00f), 0xf, 0xf);
            } else if (color.length() == 6) {
                return makeOpaque(c);
            } else if (color.length() == 8) {
                // This may be disappearing or changing
                return c;
            }
        } else if (color.startsWith("0x") || color.startsWith("0X")) {
            color = color.substring(2);
            int c = Integer.parseInt(color, 16);
            if (color.length() == 6) {
                return makeOpaque(c);
            } else if (color.length() == 8) {
                return c;
            }
        }
        throw new InvalidStringFormatException();
    }

    /**
     * Make an opaque RGB color -- color components have upper value of 255.
     * 
     * @param red red value
     * @param green green value
     * @param blue blue value
     * @return valid RGBA color (opaque)
     */
    public static int makeColor(int red, int green, int blue) {
        return makeColor(red, green, blue, 255, 255);
    }

    /**
     * Make an RGBA color -- color components have upper value of 255.
     * 
     * @param red red value
     * @param green green value
     * @param blue blue value
     * @param alpha alpha value
     * @return valid RGBA color
     */
    public static int makeColor(int red, int green, int blue, int alpha) {
        return makeColor(red, green, blue, alpha, 255);
    }

    /**
     * Make an RGBA color -- color components have upper value of 1.0.
     * 
     * @param red red value
     * @param green green value
     * @param blue blue value
     * @param alpha alpha value
     * @return valid RGBA color
     */
    public static int makeColor(double red, double green, double blue, 
                                double alpha) {
        if (red > 1.0) red = 1.0;
        if (green > 1.0) green = 1.0;
        if (blue > 1.0) blue = 1.0;
        if (alpha > 1.0) alpha = 1.0;
        red = red * 255.0;
        blue = blue * 255.0;
        green = green * 255.0;
        alpha = alpha * 255.0;
        return makeColor(Math.floor(red), Math.floor(green), Math.floor(blue),
                         Math.floor(alpha));
    }

    /**
     * Make an opaque RGBA color -- color components have upper value of 1.0.
     * 
     * @param red red value
     * @param green green value
     * @param blue blue value
     * @return valid RGBA color
     */
    public static int makeColor(double red, double green, double blue) {
        return makeColor(red, green, blue, 1.0);
    }

    /**
     * Make an RGBA color using a variable upper value for color components
     * 
     * @param red red value
     * @param green green value
     * @param blue blue value
     * @param alpha alpha value
     * @param top upper value for all color components
     * @return valid RGBA color
     */
    public static int makeColor(final int red, final int green, final int blue, 
                                final int alpha, final int top) {
        int r, g, b, a;
        if (top == 255) {
            r = red;
            g = green;
            b = blue;
            a = alpha;
        } else if (top == 15) {
            r = (red << 4) | red;
            g = (green << 4) | green;
            b = (blue << 4) | blue;
            a = (alpha << 4) | alpha;
        } else if (top < 0xfffffff) {
            r = red * 255 / top;
            b = blue * 255 / top;
            g = green * 255 / top;
            a = alpha * 255 / top;
        } else {
            int t = top >>> 8;
            r = (red >>> 8) * 255 / t;
            g = (green >>> 8) * 255 / t;
            b = (blue >>> 8) * 255 / t;
            a = (alpha >>> 8) * 255 / t;
        }
        a &= 0xff; r &= 0xff; g &= 0xff; b &= 0xff;
        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    /**
     * Split a color out in to the component R,G,B,A values.
     * 
     * @param color color value
     * @see #makeColor(int, int, int, int)
     * @return array containing red, green, blue, and alpha components
     */
    public static int[] makeComponents(final int color) {
        int r, g, b, a;
        a = (color & 0xff000000) >>> 24;
        r = (color & 0x00ff0000) >>> 16;
        g = (color & 0x0000ff00) >>> 8;
        b = color & 0x000000ff;
        int[] ret = {r, g, b, a};
        return ret;
    }
    
    /**
     * Set the alpha channel for a color.
     * 
     * @param color RGB or RGBA color value
     * @param alpha new alpha value (upper-limit of 255)
     * @see #getAlpha(int)
     * @return valid RGBA value
     */
    public static int setAlpha(final int color, final int alpha) {
        int ret = alpha & 0xff;
        ret <<= 24;
        ret = ret | (color & 0x00ffffff);
        return ret;
    }

    /**
     * Get the alpha channel for a color.
     * 
     * @param color RGBA color value
     * @see #setAlpha(int, int)
     * @return the alpha channel value
     */
    public static int getAlpha(final int color) {
        return (color >>> 24);
    }

    /**
     * Modify the current alpha value of a color.
     * 
     * <p>Increase the alpha in a particular color by a percentage of the 
     * current alpha. Note that full 100% alpha is opaque, so to add some 
     * transparency you would use a negative percentage. Please note that the 
     * percentage is expressed in a number from 0 to 1, so to increase the 
     * transparency by 20% you would use an <i>amount</i> of 
     * <code>-0.20</code>.</p>
     * 
     * <p>Note also that as this operates on the existing alpha channel value if
     * the color is completely transparent this function does nothing. If this
     * isn't what you want, you could pass the <code>color</code> through 
     * <code>neverTransparent</code> first.</p>
     * 
     * @param color the RGBA color value (a valid alpha channel must be present)
     * @param amount the percentage from 0.0 to 1.0 to increase the alpha
     * @return new color value 
     */
    public static int increaseAlpha(final int color, final double amount) {
        int alpha = getAlpha(color);
        int c = (int) Math.floor((double)alpha * amount) + alpha;
        if (c > 255) {
            c = 255;
        } else if (c < 0) {
            c = 0;
        }
        return setAlpha(color, c);
    }
}
