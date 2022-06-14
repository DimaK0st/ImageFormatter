
import javax.imageio.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.LookupOp;
import java.awt.image.LookupTable;

public class ImageHelper {

    public int getRed(int color) {
        return color >> 16;
    }         // получить красную составляющую цвета

    public int getGreen(int color) {
        return (color >> 8) & 0xFF;
    } // получить зеленую составляющую цвета

    public int getBlue(int color) {
        return color & 0xFF;
    }        // получить синюю   составляющую цвета


    private void saveImage(File image) {
//        saveAsJpeg(image);
    }

    public BufferedImage convertToARGB(BufferedImage image) {
        BufferedImage newImage = new BufferedImage(
                image.getWidth(), image.getHeight(),
                BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = newImage.createGraphics();
        g.drawImage(image, 0, 0, null);
        g.dispose();
        return newImage;
    }

    public BufferedImage createFlipped(BufferedImage image) {
        AffineTransform at = new AffineTransform();
        at.concatenate(AffineTransform.getScaleInstance(1, -1));
        at.concatenate(AffineTransform.getTranslateInstance(0, -image.getHeight()));
        return createTransformed(image, at, image.getWidth(), image.getHeight());
    }

    public BufferedImage createMirrored(BufferedImage image) {
        BufferedImage newImage = new BufferedImage(
                image.getWidth(), image.getHeight(),
                BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = newImage.createGraphics();
//        g.drawImage(image, 0, 0, null);
        g.drawImage(image, 0 + image.getWidth(), 0, -image.getWidth(), image.getHeight(), null);
        g.dispose();
        return newImage;
    }

    public BufferedImage createRotated(BufferedImage image) {
        AffineTransform at = AffineTransform.getRotateInstance(
                Math.PI / 2, image.getWidth() / 1.5, image.getHeight() / 2);
        return createTransformed(image, at, image.getHeight(), image.getWidth());
    }

    public BufferedImage createTransformed(
            BufferedImage image, AffineTransform at, int width, int height) {
        BufferedImage newImage = new BufferedImage(
                width, height,
                BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = newImage.createGraphics();
        g.transform(at);
        g.drawImage(image, 0, 0, null);
        g.dispose();
        return newImage;
    }

    public BufferedImage createInverted(BufferedImage image) {
        if (image.getType() != BufferedImage.TYPE_INT_ARGB) {
            image = convertToARGB(image);
        }
        LookupTable lookup = new LookupTable(0, 4) {
            @Override
            public int[] lookupPixel(int[] src, int[] dest) {
                dest[0] = (int) (255 - src[0]);
                dest[1] = (int) (255 - src[1]);
                dest[2] = (int) (255 - src[2]);
                return dest;
            }
        };
        LookupOp op = new LookupOp(lookup, new RenderingHints(null));
        return op.filter(image, null);
    }

    public int[] copyFromBufferedImage(BufferedImage img) {
        int[] pict = new int[img.getHeight() * img.getWidth()];
        for (int i = 0; i < img.getHeight(); i++)
            for (int j = 0; j < img.getWidth(); j++)
                pict[i * img.getWidth() + j] = img.getRGB(j, i) & 0xFFFFFF; // 0xFFFFFF: записываем только 3 младших байта RGB
        return pict;
    }
    public BufferedImage convertToBlackAndWhite(BufferedImage img) {
        int height = img.getHeight();
        int width = img.getWidth();
        int[] pixels = copyFromBufferedImage(img);
        for (int i = 0; i < height; i++)
            for (int j = 0; j < width; j++) {
                // находим среднюю арифметическую интенсивность пикселя по всем цветам
                int intens = (getRed(pixels[i * width + j]) +
                        getGreen(pixels[i * width + j]) +
                        getBlue(pixels[i * width + j])) / 3;
                // ... и записываем ее в каждый цвет за раз , сдвигая байты RGB на свои места
                pixels[i * width + j] = intens + (intens << 8) + (intens << 16);
            }
        BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int i = 0; i < height; i++)
            for (int j = 0; j < width; j++)
                bi.setRGB(j, i, pixels[i * width + j]);
        return bi;
    }

    public void saveAsJpeg(File image, String type, BufferedImage buffImg) throws IOException {
        try {
            ImageIO.write(buffImg, "PNG", new File(image.getParent() + "\\" + type + "_" + image.getName()));
        } catch (IOException ignored) {
        }
    }
}
