import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Drawer {
    public static int getVisibleSurfaces() {
        return visibleSurfaces;
    }

    public static void setVisibleSurfaces(int visibleSurfaces) {
        Drawer.visibleSurfaces = visibleSurfaces;
    }

    public static Surface[] getDrawOrder() {
        return drawOrder;
    }

    public static void setDrawOrder(Surface[] drawOrder) {
        Drawer.drawOrder = drawOrder;
    }

    public static Camera getCam() {
        return cam;
    }

    private static int visibleSurfaces = 0;
    private static Surface[] drawOrder = new Surface[50];
    private static Camera cam = null;
    public static void setCamera(Camera camIn) {
        cam = camIn;
    }


    public static void setWireframe(boolean wireframe) {
        Drawer.wireframe = wireframe;
    }

    private static boolean wireframe = false;

    public static boolean getWireframe() {
        return wireframe;
    }

    public static void setMode(Mode mode) {
        Drawer.mode = mode;
    }

    private static Mode mode = Mode.REALISTIC;



    public static void drawAll(Graphics2D g) throws IOException {
        BufferedImage bufferedImage = new BufferedImage(cam.getWidth(), cam.getHeight(), BufferedImage.TYPE_INT_ARGB);
        BufferedImage bufferedImage1 = ImageIO.read(new File("input/fon6.jpeg"));
        Graphics2D gbi = bufferedImage.createGraphics();

        gbi.setComposite(AlphaComposite.getInstance(AlphaComposite.DST_OVER));

        for (int i = 0; i < visibleSurfaces; i++) {
            draw(gbi, drawOrder[i]);
        }

        if (mode == Mode.REALISTIC) {
            g.drawImage(bufferedImage1, null, 0, 0);
        }
        g.drawImage(bufferedImage, null, 0, 0);

        visibleSurfaces = 0;
    }


    public static void draw(Graphics2D g, Surface surface) {
        Path2D p = new Path2D.Double();

        p.moveTo(surface.getPoints()[0].getXD(), surface.getPoints()[0].getYD());
        for (int i = 0; i < surface.getNumbPoints(); i++) {
            p.lineTo(surface.getPoints()[i].getXD(), surface.getPoints()[i].getYD());
        }
        p.lineTo(surface.getPoints()[0].getXD(), surface.getPoints()[0].getYD());

        if (!wireframe) {
            g.setColor(surface.getLightCorrectedFill());
            g.fill(p);
        }
        else {
            g.setColor(Color.BLACK);
            g.draw(p);
        }

    }
}
