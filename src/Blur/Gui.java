package Blur;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;



//TODO Figure out why it keeps adding additional black pixels to the border
public class Gui extends JFrame implements ActionListener {
    private int[][] gaussian = {{1, 2, 1}, {2, 4, 2}, {1, 2, 1}};
    private int[][] edgeDetect={{-1, -1, -1}, {-1, 8, -1}, {-1, -1, -1}};
    private int[][] mean= {{1, 1, 1}, {1, 1, 1}, {1, 1, 1}};
    private BufferedImage img;
    private ImageIcon imageIcon;
    private JLabel jLabel;

    public Gui(){
        super("Image Convolution");
//        JFrame frame = new JFrame("Image Convolution");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setJMenuBar(createMenuBar());
        setVisible(true);

        Blur conv = new Blur();

        pack();

    }

    private JMenuBar createMenuBar(){
        JMenuBar menuBar = new JMenuBar();
        JMenuItem menuItem;
        JMenu menu = new JMenu("File");
        menu.setMnemonic(KeyEvent.VK_F);
        String[] commands = {"Open", "Save", "Save As", "Undo", "Redo"};
        String[] toolTips = {"Select file to open", "Save current file", "Save current file as", "Undo last action", "Redo last action"};
        int[] keyEvents = {KeyEvent.VK_O, KeyEvent.VK_S, KeyEvent.VK_A, KeyEvent.VK_U, KeyEvent.VK_R};
        for (int i = 0; i < commands.length; i++){
            menuItem = new JMenuItem(commands[i]);
            menuItem.setAccelerator(KeyStroke.getKeyStroke(keyEvents[i], ActionEvent.ALT_MASK));
            menuItem.addActionListener(this);
            menuItem.setToolTipText(toolTips[i]);
            menu.add(menuItem);
        }


        JMenu menu2 = new JMenu("Convolutions");
        String[] cmds = {"Mean Blur", "Gaussian Blur", "Edge Detect"};
        String[] tips = {"Performs a mean blur", "Performs a Gaussian blur", "Performs and Edge Detect"};
        for(int i = 0; i < cmds.length; i++){
            menuItem = new JMenuItem(cmds[i]);
            menuItem.addActionListener(this);
            menuItem.setToolTipText(tips[i]);
            menu2.add(menuItem);

        }

        menuBar.add(menu);
        menuBar.add(menu2);

        return menuBar;
    }

    private BufferedImage loadImage(){
        BufferedImage img = null;
        JFileChooser chooser = new JFileChooser(".");
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

    private BufferedImage gaussian(BufferedImage img){
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
                int reds = (c1.getRed()*this.gaussian[0][0] + c2.getRed()*this.gaussian[0][1] + c3.getRed()*this.gaussian[0][2] + c4.getRed()*this.gaussian[1][0] + c5.getRed()*this.gaussian[1][1] + c6.getRed()*this.gaussian[1][2] + c7.getRed()*this.gaussian[2][0] + c8.getRed()*this.gaussian[2][1] + c9.getRed()*this.gaussian[2][2])/this.kernelCount(this.gaussian);
                int greens = (c1.getGreen()*this.gaussian[0][0] + c2.getGreen()*this.gaussian[0][1] + c3.getGreen()*this.gaussian[0][2] + c4.getGreen()*this.gaussian[1][0] + c5.getGreen()*this.gaussian[1][1] + c6.getGreen()*this.gaussian[1][2] + c7.getGreen()*this.gaussian[2][0] + c8.getGreen()*this.gaussian[2][1] + c9.getGreen()*this.gaussian[2][2])/this.kernelCount(this.gaussian);
                int blues = (c1.getBlue()*this.gaussian[0][0] + c2.getBlue()*this.gaussian[0][1] + c3.getBlue()*this.gaussian[0][2] + c4.getBlue()*this.gaussian[1][0] + c5.getBlue()*this.gaussian[1][1] + c6.getBlue()*this.gaussian[1][2] + c7.getBlue()*this.gaussian[2][0] + c8.getBlue()*this.gaussian[2][1] + c9.getBlue()*this.gaussian[2][2])/this.kernelCount(this.gaussian);

                //sets the target pixel's color.  If an RGB value is outside the range of 0-255, it sets it to the closest value of either 0 or 255
                Color pixelColor = new Color(reds >= 0 && reds <=255 ? reds : reds > 255 ? 255 : 0, greens >= 0  && greens <=255? greens : greens > 255 ? 255 : 0, blues >= 0 && blues <=255 ? blues : blues > 255 ? 255 : 0);
                img2.setRGB(j,i,pixelColor.getRGB());

            }
        }
        return img2;

    }

    private BufferedImage edgeDetect(BufferedImage img){
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
                int reds = (c1.getRed()*this.edgeDetect[0][0] + c2.getRed()*this.edgeDetect[0][1] + c3.getRed()*this.edgeDetect[0][2] + c4.getRed()*this.edgeDetect[1][0] + c5.getRed()*this.edgeDetect[1][1] + c6.getRed()*this.edgeDetect[1][2] + c7.getRed()*this.edgeDetect[2][0] + c8.getRed()*this.edgeDetect[2][1] + c9.getRed()*this.edgeDetect[2][2])/this.kernelCount(this.edgeDetect);
                int greens = (c1.getGreen()*this.edgeDetect[0][0] + c2.getGreen()*this.edgeDetect[0][1] + c3.getGreen()*this.edgeDetect[0][2] + c4.getGreen()*this.edgeDetect[1][0] + c5.getGreen()*this.edgeDetect[1][1] + c6.getGreen()*this.edgeDetect[1][2] + c7.getGreen()*this.edgeDetect[2][0] + c8.getGreen()*this.edgeDetect[2][1] + c9.getGreen()*this.edgeDetect[2][2])/this.kernelCount(this.edgeDetect);
                int blues = (c1.getBlue()*this.edgeDetect[0][0] + c2.getBlue()*this.edgeDetect[0][1] + c3.getBlue()*this.edgeDetect[0][2] + c4.getBlue()*this.edgeDetect[1][0] + c5.getBlue()*this.edgeDetect[1][1] + c6.getBlue()*this.edgeDetect[1][2] + c7.getBlue()*this.edgeDetect[2][0] + c8.getBlue()*this.edgeDetect[2][1] + c9.getBlue()*this.edgeDetect[2][2])/this.kernelCount(this.edgeDetect);

                //sets the target pixel's color.  If an RGB value is outside the range of 0-255, it sets it to the closest value of either 0 or 255
                Color pixelColor = new Color(reds >= 0 && reds <=255 ? reds : reds > 255 ? 255 : 0, greens >= 0  && greens <=255? greens : greens > 255 ? 255 : 0, blues >= 0 && blues <=255 ? blues : blues > 255 ? 255 : 0);
                img2.setRGB(j,i,pixelColor.getRGB());

            }
        }
        return img2;


    }

    private BufferedImage meanBlur(BufferedImage img){
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
                int reds = (c1.getRed()*this.mean[0][0] + c2.getRed()*this.mean[0][1] + c3.getRed()*this.mean[0][2] + c4.getRed()*this.mean[1][0] + c5.getRed()*this.mean[1][1] + c6.getRed()*this.mean[1][2] + c7.getRed()*this.mean[2][0] + c8.getRed()*this.mean[2][1] + c9.getRed()*this.mean[2][2])/this.kernelCount(this.mean);
                int greens = (c1.getGreen()*this.mean[0][0] + c2.getGreen()*this.mean[0][1] + c3.getGreen()*this.mean[0][2] + c4.getGreen()*this.mean[1][0] + c5.getGreen()*this.mean[1][1] + c6.getGreen()*this.mean[1][2] + c7.getGreen()*this.mean[2][0] + c8.getGreen()*this.mean[2][1] + c9.getGreen()*this.mean[2][2])/this.kernelCount(this.mean);
                int blues = (c1.getBlue()*this.mean[0][0] + c2.getBlue()*this.mean[0][1] + c3.getBlue()*this.mean[0][2] + c4.getBlue()*this.mean[1][0] + c5.getBlue()*this.mean[1][1] + c6.getBlue()*this.mean[1][2] + c7.getBlue()*this.mean[2][0] + c8.getBlue()*this.mean[2][1] + c9.getBlue()*this.mean[2][2])/this.kernelCount(this.mean);

                //sets the target pixel's color.  If an RGB value is outside the range of 0-255, it sets it to the closest value of either 0 or 255
                Color pixelColor = new Color(reds >= 0 && reds <=255 ? reds : reds > 255 ? 255 : 0, greens >= 0  && greens <=255? greens : greens > 255 ? 255 : 0, blues >= 0 && blues <=255 ? blues : blues > 255 ? 255 : 0);
                img2.setRGB(j,i,pixelColor.getRGB());

            }
        }
        return img2;


    }


    private int kernelCount(int[][] kernel){
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









    public static void main(String[] args){
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception unused){

        }
        new Gui();
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        if(command.contentEquals("Open")){
            this.img = loadImage();
            this.imageIcon = new ImageIcon(img);
            this.jLabel = new JLabel();
            jLabel.setIcon(imageIcon);
            this.getContentPane().add(jLabel, BorderLayout.CENTER);
            this.pack();

        }
        if(command.contentEquals("Save")){
            JFileChooser jfc = new JFileChooser(".");
            jfc.setDialogTitle("Choose location to save file");
            if(jfc.showSaveDialog(null) == JFileChooser.APPROVE_OPTION){
                File file = jfc.getSelectedFile();
                try{
                    ImageIO.write(this.img, "png", new File(file.getAbsolutePath()));
                } catch(IOException ex){
                    System.out.println("Failed to save image!");
                }
            }


        }
        if(command.contentEquals("Gaussian Blur")){
            this.img = gaussian(this.img);
            this.imageIcon.setImage(this.img);
            this.jLabel.setIcon(this.imageIcon);
            this.getContentPane().add(this.jLabel, BorderLayout.CENTER);
            this.repaint();

        }
        if(command.contentEquals("Edge Detect")){
            this.img = edgeDetect(this.img);
            this.imageIcon.setImage(this.img);
            this.jLabel.setIcon(this.imageIcon);
            this.getContentPane().add(this.jLabel, BorderLayout.CENTER);
            this.repaint();
        }
        if(command.contentEquals("Mean Blur")){
            this.img = meanBlur(this.img);
            this.imageIcon.setImage(this.img);
            this.jLabel.setIcon(this.imageIcon);
            this.getContentPane().add(this.jLabel, BorderLayout.CENTER);
            this.repaint();

        }


    }
}
