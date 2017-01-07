package be.storefront.imicloud.web.helper;

import java.awt.*;

/**
 * Created by wouter on 07/01/2017.
 */
public class CssHelper {

    public String darken(String hexColor, double factor) {
        factor = 1 - factor;

        Color c = Color.decode(hexColor);

        Color darkerColor = new Color(Math.max((int) (c.getRed() * factor), 0),
            Math.max((int) (c.getGreen() * factor), 0),
            Math.max((int) (c.getBlue() * factor), 0),
            c.getAlpha());

        String r = colorToHex(darkerColor);
        return r;
    }

    private String colorToHex(Color color){
        int r = color.getRed();
        int g = color.getGreen();
        int b = color.getBlue();

        String rHex = Integer.toString(r, 16);
        String gHex = Integer.toString(g, 16);
        String bHex = Integer.toString(b, 16);

        String hexValue = "#" +(rHex.length() == 2 ? "" + rHex : "0" + rHex)
            + (gHex.length() == 2 ? "" + gHex : "0" + gHex)
            + (bHex.length() == 2 ? "" + bHex : "0" + bHex);

        return hexValue;
    }
}
