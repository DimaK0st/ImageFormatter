
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.*;

public class Main extends JFrame
{
    private static JPanel panelT;

    public static void main(String[] args)
    {
        SwingUtilities.invokeLater(new Runnable()
        {
            @Override
            public void run()
            {
                createAndShowGUI();
            }
        });
    }

    private static void createAndShowGUI()
    {

        JFrame frame = new JFrame();
        frame.setPreferredSize(new Dimension(1000, 1000));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(new BorderLayout());

        JPanel panelFile = new JPanel(new GridLayout(4,2));
        panelFile.setBackground(Color.LIGHT_GRAY);

        panelFile.setPreferredSize(new Dimension(1000, 130));
        panelFile.setLayout(new BoxLayout(panelFile, BoxLayout.Y_AXIS));
        panelFile.add(Box.createVerticalGlue());

        final JLabel label = new JLabel("Selected file");
        label.setAlignmentX(CENTER_ALIGNMENT);
        panelFile.add(label);

        panelFile.add(Box.createRigidArea(new Dimension(10, 10)));
        JButton button = new JButton("Review");
        button.setAlignmentX(0);
        panelFile.add(button);
        panelT = new JPanel(new GridLayout(4,2));
        panelFile.add(panelT);

        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileopen = new JFileChooser();
                int ret = fileopen.showDialog(null, "Открыть файл");
                if (ret == JFileChooser.APPROVE_OPTION) {
                    File file = fileopen.getSelectedFile();
                    label.setText(file.getPath());
                    panelT.removeAll();
                    RenderGui renderGui = new RenderGui(frame, file, panelT);
                    renderGui.renderImage();
                }
            }
        });

        frame.getContentPane().add(panelFile, BorderLayout.PAGE_START);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}