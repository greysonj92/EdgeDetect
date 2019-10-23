package Blur;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Blur {
   static int[][] kernel = {{-1, -1, -1}, {-1 ,8 ,-1}, {-1, -1, -1}};

   public static int kernelCount(int[][] kernel){
       int total = 0;
       for (int i = 0; i < 3; i++){
           for (int j = 0; j < 3; j++){
             total = total +kernel[i][j];
           }
       }
       if(total == 0){
           return 1;
       }
       else{
           return total;
       }
   }

   public BufferedImage loadImage(){
       BufferedImage img = null;
       JFileChooser chooser = new JFileChooser();
       chooser.setDialogTitle("Choose an image...");
       FileNameExtensionFilter filter = new FileNameExtensionFilter("*.Images", "jpg", "gif", "png");
       chooser.addChoosableFileFilter(filter);
       if(chooser.showOpenDialog((null))==JFileChooser.APPROVE_OPTION){
           File file = chooser.getSelectedFile();
           try {
               img = ImageIO.read(file);
           } catch(IOException e){
               e.printStackTrace();
           }
       }
       return img;

   }


    public static void main(String args[]) throws IOException {

        BufferedImage img = null;
        try{
            img = ImageIO.read(new File("imagetest.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }


        BufferedImage img2 = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB);

        for(int i = 1; i < img.getHeight() -1; i++){
            for( int j = 1; j < img.getWidth() -1; j++){

                //gets the color for each pixel in the 3x3 matrix around the target pixel
                Color c1 = new Color(img.getRGB(j - 1, i - 1));
                Color c2 = new Color(img.getRGB(j-1,i-1));
                Color c3 = new Color(img.getRGB(j+1,i+1));
                Color c4 = new Color(img.getRGB(j-1, i));
                Color c5 = new Color(img.getRGB(j,i-1));
                Color c6 = new Color(img.getRGB(j+1,i));
                Color c7 = new Color(img.getRGB(j-1,i+1));
                Color c8 = new Color(img.getRGB(j,i+1));
                Color c9 = new Color(img.getRGB(j+1,i+1));

                //calculates the RGB values by getting them from each pixel, multiplying them by the matrix entry, and then normalizing them
                int reds = (c1.getRed()*kernel[0][0] + c2.getRed()*kernel[0][1] + c3.getRed()*kernel[0][2] + c4.getRed()*kernel[1][0] + c5.getRed()*kernel[1][1] + c6.getRed()*kernel[1][2] + c7.getRed()*kernel[2][0] + c8.getRed()*kernel[2][1] + c9.getRed()*kernel[2][2])/kernelCount(kernel);
                int greens = (c1.getGreen()*kernel[0][0] + c2.getGreen()*kernel[0][1] + c3.getGreen()*kernel[0][2] + c4.getGreen()*kernel[1][0] + c5.getGreen()*kernel[1][1] + c6.getGreen()*kernel[1][2] + c7.getGreen()*kernel[2][0] + c8.getGreen()*kernel[2][1] + c9.getGreen()*kernel[2][2])/kernelCount(kernel);
                int blues = (c1.getBlue()*kernel[0][0] + c2.getBlue()*kernel[0][1] + c3.getBlue()*kernel[0][2] + c4.getBlue()*kernel[1][0] + c5.getBlue()*kernel[1][1] + c6.getBlue()*kernel[1][2] + c7.getBlue()*kernel[2][0] + c8.getBlue()*kernel[2][1] + c9.getBlue()*kernel[2][2])/kernelCount(kernel);

                //sets the target pixel's color.  If an RGB value is outside the range of 0-255, it sets it to the closest value of either 0 or 255
                Color pixelColor = new Color(reds >= 0 && reds <=255 ? reds : reds > 255 ? 255 : 0, greens >= 0  && greens <=255? greens : greens > 255 ? 255 : 0, blues >= 0 && blues <=255 ? blues : blues > 255 ? 255 : 0);
                img2.setRGB(j,i,pixelColor.getRGB());

            }
        }
        //saves bufferedImage to a file
        File outputImage = new File("saved.png");
        ImageIO.write(img2, "png", outputImage);


    }
}
