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


//TODO Finish the GUI and make it actually do something
public class Gui extends JFrame implements ActionListener {

    public Gui(){
        super("Image Convolution");
//        JFrame frame = new JFrame("Image Convolution");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setJMenuBar(createMenuBar());
        setSize(512,512);
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
            BufferedImage img = loadImage();
            ImageIcon imageIcon = new ImageIcon(img);
            JLabel jLabel = new JLabel();
            jLabel.setIcon(imageIcon);
            this.getContentPane().add(jLabel, BorderLayout.CENTER);

        }

    }
}
