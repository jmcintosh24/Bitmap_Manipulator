/**
 * Course: COMP 2100
 * Assignment: Project 1
 *
 * @author Jacob McIntosh
 * @author Ian Johnston
 * @version 1.0, 9/17/2021
 */

import java.io.*;

public class Bitmap {

    private static class Color {
        int red;
        int green;
        int blue;

        public Color(FileInputStream file) throws IOException {
            blue = file.read();
            green = file.read();
            red = file.read();
        }

        public Color(int r, int g, int b) throws IOException {
            this.red = r;
            this.green = g;
            this.blue = b;

        }
    }

    private int width;
    private int height;
    private Color[][] colorData;

    public Bitmap(FileInputStream file) throws IOException {
        file.skip(18);
        width = readInt(file);
        height = readInt(file);
        int padding = (3 * width) % 4;
        if (padding != 0)
            padding = 4 - padding;
        colorData = new Color[height][width];
        file.skip(28);
        for (int i = height - 1; i >= 0; i--) {
            for (int j = 0; j < width; j++) {
                colorData[i][j] = new Color(file);
            }
            file.skip(padding);
        }
    }

    private static int readInt(FileInputStream file) throws IOException {
        int a = file.read();
        int b = file.read();
        int c = file.read();
        int d = file.read();
        return a + (b << 8) + (c << 16) + (d << 24);
    }

    private static void writeInt(int value, FileOutputStream file) throws IOException {
        int a = (value >>> 24);
        int b = (value >>> 16) & 0xff;
        int c = (value >>> 8) & 0xff;
        int d = (value) & 0xff;
        file.write(d);
        file.write(c);
        file.write(b);
        file.write(a);
    }

    private static void writeShort(int value, FileOutputStream file) throws IOException {
        int a = (value >>> 8) & 0xff;
        int b = (value) & 0xff;
        file.write(b);
        file.write(a);
    }

    public void write(String name) throws IOException {
        FileOutputStream fileOut = new FileOutputStream(name);
        int padding = (3 * width) % 4;
        if (padding != 0)
            padding = 4 - padding;
        int dataSize = height * ((width * 3) + padding);
        fileOut.write('B');
        fileOut.write('M');
        writeInt(54 + dataSize + 2, fileOut);
        writeInt(0, fileOut); //reserved
        writeInt(54, fileOut); //offset
        writeInt(40, fileOut); //header
        writeInt(width, fileOut); //width
        writeInt(height, fileOut); //height
        writeShort(1, fileOut); //planes
        writeShort(24, fileOut); //bits
        writeInt(0, fileOut); //compression
        writeInt(dataSize, fileOut); //datasize
        writeInt(72, fileOut); //horizontal resoultion
        writeInt(72, fileOut); //vertical resoultion
        writeInt(0, fileOut); //colors
        writeInt(0, fileOut); //importantColors
        for (int i = height - 1; i >= 0; i--) {
            for (int j = 0; j < width; j++) {
                fileOut.write(colorData[i][j].blue);
                fileOut.write(colorData[i][j].green);
                fileOut.write(colorData[i][j].red);
            }
            for (int k = 0; k < padding; k++) {
                fileOut.write(0);
            }
        }
        fileOut.close();
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void invert() {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                colorData[i][j].blue = 255 - colorData[i][j].blue;
                colorData[i][j].green = 255 - colorData[i][j].green;
                colorData[i][j].red = 255 - colorData[i][j].red;
            }
        }
    }

    public void grayscale() {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int b = colorData[i][j].blue;
                int g = colorData[i][j].green;
                int r = colorData[i][j].red;
                int grayPixel = (int) (Math.round(.3 * r + .59 * g + .11 * b));
                colorData[i][j].blue = grayPixel;
                colorData[i][j].green = grayPixel;
                colorData[i][j].red = grayPixel;
            }
        }
    }

    public void blur() throws IOException {
        Color[][] blur = new Color[height][width];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int r = 0;
                int g = 0;
                int b = 0;
                double count = 0;
                for (int row = Math.max(i - 2, 0); row <= Math.min(i + 2, height - 1); row++) {
                    for (int column = Math.max(j - 2, 0); column <= Math.min(j + 2, width - 1); column++) {
                        r += colorData[row][column].red;
                        g += colorData[row][column].green;
                        b += colorData[row][column].blue;
                        count++;

                    }
                }
                r = (int) Math.round(r / count);
                g = (int) Math.round(g / count);
                b = (int) Math.round(b / count);
                blur[i][j] = new Color(r, g, b);
            }
        }
        colorData = blur;
    }

    public void mirror() {
        Color[][] temp = new Color[height][width];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                temp[i][j] = colorData[(height - 1) - i][j];
            }
        }
        colorData = temp;
    }

    public void shrink() throws IOException {
        height /= 2;
        width /= 2;
        Color[][] shrunkColors = new Color[height][width];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int r = colorData[i * 2][j * 2].red + colorData[i * 2][j * 2 + 1].red + colorData[i * 2 + 1][j * 2].red + colorData[i * 2 + 1][j * 2 + 1].red;
                int g = colorData[i * 2][j * 2].green + colorData[i * 2][j * 2 + 1].green + colorData[i * 2 + 1][j * 2].green + colorData[i * 2 + 1][j * 2 + 1].green;
                int b = colorData[i * 2][j * 2].blue + colorData[i * 2][j * 2 + 1].blue + colorData[i * 2 + 1][j * 2].blue + colorData[i * 2 + 1][j * 2 + 1].blue;
                shrunkColors[i][j] = new Color((int) Math.round(r / 4.0), (int) Math.round(g / 4.0), (int) Math.round(b / 4.0));
            }
        }
        colorData = shrunkColors;
    }

    public void grow() throws IOException {
        height *= 2;
        width *= 2;
        Color[][] grownColors = new Color[height][width];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                Color color = colorData[i / 2][j / 2];
                grownColors[i][j] = new Color(color.red, color.green, color.blue);
            }
        }
        colorData = grownColors;
    }

    public void rotate() {
        Color[][] rotate = new Color[width][height];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                rotate[i][j] = colorData[height - j - 1][i];
            }
        }
        int temp = height;
        height = width;
        width = temp;
        colorData = rotate;
    }

}
