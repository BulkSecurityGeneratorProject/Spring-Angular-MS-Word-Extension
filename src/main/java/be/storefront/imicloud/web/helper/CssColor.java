package be.storefront.imicloud.web.helper;


/**
 * Created by wouter on 09/02/2017.
 */
public class CssColor {

    protected int red;
    protected int green;
    protected int blue;
    protected double alpha = 1.0;

    public CssColor(String hex) {
        this.red = Integer.valueOf(hex.substring(1, 3), 16);
        this.green = Integer.valueOf(hex.substring(3, 5), 16);
        this.blue = Integer.valueOf(hex.substring(5, 7), 16);
    }

    public CssColor alpha(double alpha) {
        this.alpha = alpha;
        return this;
    }

    public CssColor darken(double factor) {
        factor = 1 - factor;

        this.red = Math.max((int) (red * factor), 0);
        this.green = Math.max((int) (green * factor), 0);
        this.blue = Math.max((int) (blue * factor), 0);

        return this;
    }

    public String toRgba() {
        return "rgba(" + red + "," + green + "," + blue + "," + alpha + ")";
    }

    public String toString() {
        return toRgba();
    }

    public String toHex() {
        String rHex = Integer.toString(red, 16);
        String gHex = Integer.toString(green, 16);
        String bHex = Integer.toString(blue, 16);

        String hexValue = "#" + (rHex.length() == 2 ? "" + rHex : "0" + rHex)
            + (gHex.length() == 2 ? "" + gHex : "0" + gHex)
            + (bHex.length() == 2 ? "" + bHex : "0" + bHex);

        return hexValue;
    }


}
