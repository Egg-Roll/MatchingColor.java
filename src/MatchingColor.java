// Justin Do

import javax.sound.sampled.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class MatchingColor {

    private int scoreCounter = 0; // Variable used to keep track of the score

    public MatchingColor() throws UnsupportedAudioFileException, LineUnavailableException, IOException {

        playMusic(); // Calls the method to play music

        JFrame game = createFrame("Match the Color!"); // Creates the frame for the main game
        JFrame instructionFrame = createFrame("Match the Color!"); // Creates the frame for the instructions

        JPanel instructionRoot = new JPanel();
        instructionRoot.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel title = new JLabel("Instructions"); // Title for the instructions
        title.setFont(new Font("Comic Sans MS", Font.PLAIN, 20));

        /*
         * The actual instructions for the game below
         */
        JLabel instructions = new JLabel();
        instructions.setText("<html><center>In this game you will need click on the correct button that has the <br/>" +
                "same color as the field on the bottom. If you get it wrong, the <br/> game will end and your score " +
                "will reset. You can try again by clicking <br/> on the right answer next time.</center></html>");
        instructions.setFont(new Font(instructions.getName(), Font.PLAIN, 15));

        instructionRoot.add(title);
        instructionRoot.add(instructions);

        /*
         * The score JLabel that is used so that the player can see their score.
         */
        JLabel score = new JLabel("Score: " + scoreCounter);
        score.setHorizontalAlignment(0);
        score.setFont(new Font("Comic Sans MS", Font.PLAIN, 40));
        score.setBorder(new EmptyBorder(50, 0, 0, 0));

        /*
         * The JLabel on the bottom that the user has to match the color to.
         */
        JLabel winningLabel = new JLabel("Match the Color!");
        winningLabel.setPreferredSize(new Dimension(50, 100));
        winningLabel.setOpaque(true);

        /*
         * Used to set the JLabel on the bottom to a randomColor which is kept tracked of in
         * the winningColor variable.
         */
        Color winningColor = randomColor();
        winningLabel.setBackground(winningColor);
        winningLabel.setHorizontalAlignment(0);

        /*
         * Used to add a random color that the button will have
         */
        ArrayList<Color> buttonColors = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            buttonColors.add(randomColor());
        }
        /*
         * Creates a random number between 0 - 3
         * The random number that is decided will have the winningColor assigned to it
         */
        int randomNumber = (int) (Math.random() * 3);
        buttonColors.set(randomNumber, winningColor);

        /*
         * Creates the 4 buttons that the user can pick.
         * Only one button will have the winningColor.
         * The .setFocusPainted was used to remove the border around the icons.
         */
        ArrayList<JButton> buttonList = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            buttonList.add(createButton(buttonColors.get(i)));
            buttonList.get(i).setFocusPainted(false);
        }

        /*
         * Checkmark ImageIcon created and scaled down to 75x75.
         * Used to display if user got the answer correct.
         * Source used to scale down image: https://coderanch.com/t/331731/java/Resize-ImageIcon
         */
        ImageIcon checkIcon = new ImageIcon(((new ImageIcon("checkmark.png")).getImage()).getScaledInstance(75
                , 75, java.awt.Image.SCALE_SMOOTH));

        /*
         * Red X ImageIcon created and scaled down to 75x75.
         * Used to display if user got the answer wrong.
         * Source used to scale down image: https://coderanch.com/t/331731/java/Resize-ImageIcon
         */
        ImageIcon redIcon = new ImageIcon(((new ImageIcon("redmark.png")).getImage()).getScaledInstance(75
                , 75, java.awt.Image.SCALE_SMOOTH));

        JPanel root = new JPanel();
        root.setLayout(new GridLayout(2, 2, 20, 20));
        root.setBorder(new EmptyBorder(50, 50, 50, 50));
        for (JButton b : buttonList) {
            root.add(b);
        }

        /*
         * This is used to determine if the button that is clicked, if the button background is the same as the
         * winningLabel's background.
         * If so, the score will increase by 1
         * The button's icon will be set to the checkmark icon
         * And after 0.7 seconds a new game will be generated, with the checkmark icon being cleared out
         *
         * If the button background is not the same as the winningLabel's background, it will tell the user
         * they lost and to try again next time.
         * It will set the scoreCounter to 0 and display a red X.
         * Then after 4 seconds a new game will be generated, with the red X icon being cleared out.
         */
        for (Component component : root.getComponents()) {
            if (component instanceof JButton) {
                JButton button = (JButton) component;
                button.addActionListener(e -> {
                    if (button.getBackground() == winningLabel.getBackground()) {
                        scoreCounter++;
                        score.setText("Score: " + scoreCounter);
                        button.setIcon(checkIcon);
                        button.setDisabledIcon(checkIcon);
                        for (JButton b : buttonList) {
                            b.setEnabled(false);
                        }
                        Timer timer = new Timer(700, e1 -> {
                            generateNewGame(root, winningLabel);
                            for (JButton b : buttonList) {
                                b.setEnabled(true);
                            }
                            button.setIcon(null);
                            button.setDisabledIcon(null);
                        });
                        timer.start();
                        timer.setRepeats(false);
                    } else {
                        score.setText("You Lost! Your score was " + scoreCounter + "." + " Try again next time!");
                        scoreCounter = 0;
                        button.setIcon(redIcon);
                        button.setDisabledIcon(redIcon);
                        for (JButton b : buttonList) {
                            b.setEnabled(false);
                        }
                        Timer timer = new Timer(4000, e1 -> {
                            generateNewGame(root, winningLabel);
                            for (JButton b : buttonList) {
                                b.setEnabled(true);
                            }
                            button.setIcon(null);
                            button.setDisabledIcon(null);
                            score.setText("Score: " + scoreCounter);
                        });
                        timer.start();
                        timer.setRepeats(false);
                    }
                });
            }
        }

        game.add(winningLabel, BorderLayout.SOUTH);
        game.add(score, BorderLayout.NORTH);
        game.add(root);
        game.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        game.setExtendedState(JFrame.MAXIMIZED_BOTH);

        instructionFrame.getContentPane().add(instructionRoot);
        instructionFrame.setSize(500, 175);
        instructionFrame.setLocationRelativeTo(null);
        instructionFrame.toFront();
        instructionFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
    }

    /*
     * Method used to playMusic in the game
     * Also lowers the default volume of the music by 25 decibels
     * Source used: https://www.geeksforgeeks.org/play-audio-file-using-java/
     */
    private void playMusic() throws UnsupportedAudioFileException, IOException, LineUnavailableException {

        FileInputStream input = new FileInputStream("backgroundMusic.wav");

        BufferedInputStream buffInput = new BufferedInputStream(input);

        AudioInputStream audio = AudioSystem.getAudioInputStream(buffInput);
        Clip clip = AudioSystem.getClip();
        clip.open(audio);
        clip.loop(-1);
        FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
        gainControl.setValue(-25.0f);
        clip.start();
        audio.close();
    }

    /*
     * Method used to generate a new game
     */
    private void generateNewGame(JPanel root, JLabel winningLabel) {

        Color winningColor = randomColor();
        winningLabel.setBackground(winningColor);

        ArrayList<Color> buttonColors = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            buttonColors.add(randomColor());
        }
        int randomNumber = (int) (Math.random() * 3);
        buttonColors.set(randomNumber, winningColor);

        for (int i = 0; i < root.getComponents().length; i++) {
            root.getComponents()[i].setBackground(buttonColors.get(i));
        }
    }

    /*
     * Method used to create new JFrames
     * and set them to be visible
     */
    private JFrame createFrame(String text) {
        JFrame frame = new JFrame(text);
        frame.setVisible(true);
        return frame;
    }

    /*
     * Method used to create JButtons
     * And set the background to buttonColor
     */
    private JButton createButton(Color buttonColor) {
        JButton button = new JButton();
        button.setBackground(buttonColor);
        return button;
    }

    /*
     * Method used to create a randomColor.
     * It decides randomly the hue, saturation, and luminance.
     * And puts them together to get the HSB color.
     */
    private Color randomColor() {

        Color color; // random color, but can be bright or dull

        Random random = new Random();
        float hue = random.nextFloat() * 360;
        float saturation = (random.nextFloat() * .2f) + .7f; // 1.0 for brilliant, 0.0 for dull
        float luminance = (random.nextFloat() * .1f) + .8f; // 1.0 for brighter, 0.0 for black
        color = Color.getHSBColor(hue, saturation, luminance);

        return color;
    }

    /*
     * Main method
     */
    public static void main(String[] args) throws UnsupportedAudioFileException, LineUnavailableException, IOException {
        new MatchingColor();
    }
}





