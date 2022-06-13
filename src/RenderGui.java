import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class RenderGui {

    private JFrame frame;
    private File file;
    private JPanel panelFile;

    public ImageHelper imageHelper;

    public RenderGui(JFrame frame, File file, JPanel panelFile) {
        this.file = file;
        this.frame = frame;
        this.panelFile = panelFile;
        this.imageHelper = new ImageHelper();
    }

    private Component createComponent(
            String title, BufferedImage image) {
        JLabel label = new JLabel(new ImageIcon(image));
        JPanel panel = new JPanel(new GridLayout(1, 1));
        panel.add(label);
        panel.setBorder(BorderFactory.createTitledBorder(title));
        return panel;
    }

    public void renderImage() {

        BufferedImage image = null;
        try {
            image = imageHelper.convertToARGB(ImageIO.read(file));
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        assert image != null;
        JPanel panelBtn = new JPanel(new GridLayout(3, 2));

        JButton button = new JButton("Save Original");
        BufferedImage finalImage = image;
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    JOptionPane.showMessageDialog(null, "Saved Original");
                    imageHelper.saveAsJpeg(file,"original",finalImage);
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(null, "Error");
                    throw new RuntimeException(ex);
                }
            }
        });
        panelBtn.add(button);
        JButton button1 = new JButton("Save Flipped");
        button1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    JOptionPane.showMessageDialog(null, "Saved Flipped");
                    imageHelper.saveAsJpeg(file,"flipped",imageHelper.createFlipped(finalImage));
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(null, "Error");
                    throw new RuntimeException(ex);
                }
                ;
            }
        });
        panelBtn.add(button1);
        JButton button2 = new JButton("Save Rotated");
        button2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    JOptionPane.showMessageDialog(null, "Saved Rotated");
                    imageHelper.saveAsJpeg(file,"rotated",imageHelper.createRotated(finalImage));
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(null, "Error");
                    throw new RuntimeException(ex);
                }
            }
        });
        panelBtn.add(button2);
        JButton button3 = new JButton("Save Mirrored");
        button3.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    JOptionPane.showMessageDialog(null, "Saved Mirrored");
                    imageHelper.saveAsJpeg(file,"mirrored",imageHelper.createMirrored(finalImage));
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(null, "Error");
                    throw new RuntimeException(ex);
                }
            }
        });
        panelBtn.add(button3);
        JButton button4 = new JButton("Save Inverted");
        button4.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    JOptionPane.showMessageDialog(null, "Saved Inverted");
                    imageHelper.saveAsJpeg(file,"inverted",imageHelper.createInverted(finalImage));
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(null, "Error");
                    throw new RuntimeException(ex);
                }
            }
        });
        panelBtn.add(button4);
        JButton button5 = new JButton("Save Black And White");
        button5.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    JOptionPane.showMessageDialog(null, "Saved Black And White");
                    imageHelper.saveAsJpeg(file,"black-and-white",imageHelper.convertToBlackAndWhite(finalImage));
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(null, "Error");
                    throw new RuntimeException(ex);
                }
            }
        });
        panelBtn.add(button5);

        panelFile.add(panelBtn);

        JPanel panel = new JPanel(new GridLayout(3, 2));

        panel.add(createComponent("Original", image));
        panel.add(createComponent("Flipped", imageHelper.createFlipped(image)));
        panel.add(createComponent("Rotated", imageHelper.createRotated(image)));
        panel.add(createComponent("Mirrored", imageHelper.createMirrored(image)));
        panel.add(createComponent("Inverted", imageHelper.createInverted(image)));
        panel.add(createComponent("Black And White", imageHelper.convertToBlackAndWhite(image)));
        frame.getContentPane().add(panel);
    }

}
