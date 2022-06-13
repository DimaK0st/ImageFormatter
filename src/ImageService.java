

import com.sun.imageio.plugins.jpeg.*;
import com.sun.imageio.plugins.png.*;
import javax.imageio.*;
import javax.imageio.stream.*;
import java.awt.image.BufferedImage;
import java.io.*;

public class ImageService {
    public ImageService(String type, String path, String newPath) throws IOException {
        CoolImage picture = new CoolImage(path); // загружаем файл изображения
        if ("-n".equals(type)) picture.convertToNegative();
        if ("-g".equals(type)) picture.addColorGreenChannel(-100);
        if ("-bw".equals(type)) picture.convertToBlackAndWhite();
        picture.saveAsJpeg(newPath);
    }

    public static class CoolImage {
        private int height;             // высота изображения
        private int width;              // ширина изображения
        private int[] pixels;             // собственно массив цветов точек составляющих изображение

        public int getPixel(int x, int y) {
            return pixels[y * width + x];
        }   // получить пиксель в формате RGB

        public int getRed(int color) {
            return color >> 16;
        }         // получить красную составляющую цвета

        public int getGreen(int color) {
            return (color >> 8) & 0xFF;
        } // получить зеленую составляющую цвета

        public int getBlue(int color) {
            return color & 0xFF;
        }        // получить синюю   составляющую цвета

        // Конструктор - создание изображения из файла
        public CoolImage(String fileName) throws IOException {
            System.out.println(fileName);
            BufferedImage img = readFromFile(fileName);
            System.out.println(fileName);
            this.height = img.getHeight();
            this.width = img.getWidth();
            this.pixels = copyFromBufferedImage(img);
        }

        // Чтение изображения из файла в BufferedImage
        private BufferedImage readFromFile(String fileName) throws IOException {
            ImageReader r = new JPEGImageReader(new JPEGImageReaderSpi());
            r.setInput(new FileImageInputStream(new File(fileName)));
            BufferedImage bi = r.read(0, new ImageReadParam());
            ((FileImageInputStream) r.getInput()).close();
            return bi;
        }

        // Формирование BufferedImage из массива pixels
        private BufferedImage copyToBufferedImage() {
            BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            for (int i = 0; i < height; i++)
                for (int j = 0; j < width; j++)
                    bi.setRGB(j, i, pixels[i * width + j]);
            return bi;
        }

        // Формирование массива пикселей из BufferedImage
        private int[] copyFromBufferedImage(BufferedImage bi) {
            int[] pict = new int[height * width];
            for (int i = 0; i < height; i++)
                for (int j = 0; j < width; j++)
                    pict[i * width + j] = bi.getRGB(j, i) & 0xFFFFFF; // 0xFFFFFF: записываем только 3 младших байта RGB
            return pict;
        }

        // Запись изображения в jpeg-формате
        public void saveAsJpeg(String fileName) throws IOException {
            ImageWriter writer = new JPEGImageWriter(new JPEGImageWriterSpi());
            saveToImageFile(writer, fileName);
        }

        // Запись изображения в png-формате (другие графические форматы по аналогии)
        public void saveAsPng(String fileName) throws IOException {
            ImageWriter writer = new PNGImageWriter(new PNGImageWriterSpi());
            saveToImageFile(writer, fileName);
        }

        // Собственно запись файла (общая для всех форматов часть).
        private void saveToImageFile(ImageWriter iw, String fileName) throws IOException {
            iw.setOutput(new FileImageOutputStream(new File(fileName)));
            iw.write(copyToBufferedImage());
            ((FileImageOutputStream) iw.getOutput()).close();
        }

        // конвертация изображения в негатив
        public void convertToNegative() {
            for (int i = 0; i < height; i++)
                for (int j = 0; j < width; j++)
                    // Применяем логическое отрицание и отбрасываем старший байт
                    pixels[i * width + j] = ~pixels[i * width + j] & 0xFFFFFF;
        }

        // конвертация изображения в черно-белый вид
        public void convertToBlackAndWhite() {
            for (int i = 0; i < height; i++)
                for (int j = 0; j < width; j++) {
                    // находим среднюю арифметическую интенсивность пикселя по всем цветам
                    int intens = (getRed(pixels[i * width + j]) +
                            getGreen(pixels[i * width + j]) +
                            getBlue(pixels[i * width + j])) / 3;
                    // ... и записываем ее в каждый цвет за раз , сдвигая байты RGB на свои места
                    pixels[i * width + j] = intens + (intens << 8) + (intens << 16);
                }
        }

        // изменяем интесивность зеленого цвета
        public void addColorGreenChannel(int delta) {
            for (int i = 0; i < height; i++)
                for (int j = 0; j < width; j++) {
                    int newGreen = getGreen(pixels[i * width + j]) + delta;
                    if (newGreen > 255) newGreen = 255;  // Отсекаем при превышении границ байта
                    if (newGreen < 0) newGreen = 0;
                    // В итоговом пикселе R и B цвета оставляем без изменений: & 0xFF00FF
                    // Полученный новый G (зеленый) засунем в "серединку" RGB: | (newGreen << 8)
                    pixels[i * width + j] = pixels[i * width + j] & 0xFF00FF | (newGreen << 8);
                }
        }
    }
}
