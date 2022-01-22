/**
 * Course: COMP 2100
 * Assignment: Project 1
 *
 * @author Jacob McIntosh
 * @author Ian Johnston
 * @version 1.0, 9/17/2021
 */

import java.util.Scanner;
import java.io.*;

public class Manipulator {
    public static void main(String[] args) throws IOException {
        Scanner in = new Scanner(System.in);
        System.out.print("What image file would you like to edit: ");
        FileInputStream file = new FileInputStream(in.next());
        Bitmap bmp = new Bitmap(file);
        String command = "";
        while (!(command.equals("q"))) {
            System.out.print("What command would you like to perform (i, g, b, v, s, d, r, or q): ");
            command = in.next();
            if (command.equals("i")) {
                bmp.invert();
            } else if (command.equals("g")) {
                bmp.grayscale();
            } else if (command.equals("b")) {
                bmp.blur();
            } else if (command.equals("v")) {
                bmp.mirror();
            } else if (command.equals("s")) {
                bmp.shrink();
            } else if (command.equals("d")) {
                bmp.grow();
            } else if (command.equals("r")) {
                bmp.rotate();
            }
        }
        System.out.print("What do you want to name your new image file: ");
        bmp.write(in.next());
    }
}