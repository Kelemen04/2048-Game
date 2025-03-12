import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Component extends JComponent {
    private Board board;

    // Constructor to pass the board object to the Component class
    public Component(Board board) {
        this.board = board;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Font font1;
        try {
            // Load custom font for the numbers
            font1 = Font.createFont(Font.TRUETYPE_FONT, new File("resources/Gelline.otf")).deriveFont(48f);
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, new File("resources/Gelline.otf")));
        } catch (IOException | FontFormatException e) {
            throw new RuntimeException(e);
        }

        // Create a Graphics2D object for better rendering control
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); // Smoothens edges and lines

        // Fill the background with a light color
        g2d.setColor(new Color(216, 219, 189));
        g2d.fillRect(0, 0, getWidth(), getHeight());

        // Calculate the size of the board (4x4 grid)
        ArrayList<ArrayList<Integer>> matrix = board.getGrid();
        int tileSize = 100;  // Size of each tile
        int boardSize = tileSize * 4;  // Total size of the board

        // Calculate offsets to center the board in the middle of the screen
        int startX = (getWidth() - boardSize) / 2;
        int startY = (getHeight() - boardSize) / 2;

        // Draw the grid and tiles
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                int value = matrix.get(i).get(j);
                if (value != 0) {
                    // Draw square for non-zero values
                    int x = startX + j * tileSize;
                    int y = startY + i * tileSize;

                    g2d.setColor(getSquareColor(value));  // Set the tile color based on the value
                    g2d.fill(new RoundRectangle2D.Double(x, y, tileSize, tileSize, 10, 10));  // Draw rounded rectangle
                    g2d.setColor(new Color(42, 54, 99));  // Set border color
                    g2d.draw(new RoundRectangle2D.Double(x, y, tileSize, tileSize, 10, 10));  // Draw border
                    g2d.setColor(Color.WHITE);  // Set color for the text
                    g2d.setFont(font1);  // Set font for the text

                    // Center the text inside the square
                    String text = String.valueOf(value);
                    FontMetrics metrics = g2d.getFontMetrics();
                    int textWidth = metrics.stringWidth(text);
                    int textHeight = metrics.getAscent();

                    g2d.drawString(text, x + (tileSize - textWidth) / 2, y + (tileSize + textHeight) / 2); // Draw the number
                }
            }
        }

        g2d.dispose(); // Clean up resources
    }

    // Method to return the color of the square based on its value
    private Color getSquareColor(int value) {
        switch (value) {
            case 2:
                return new Color(235, 227, 213);
            case 4:
                return new Color(176, 166, 149);
            case 8:
                return new Color(222, 170, 121);
            case 16:
                return new Color(240, 193, 225);
            case 32:
                return new Color(232, 37, 97);
            case 64:
                return new Color(171, 68, 89);
            case 128:
                return new Color(255, 128, 0);
            case 256:
                return new Color(76, 31, 122);
            case 512:
                return new Color(178, 47, 205);
            case 1024:
                return new Color(27, 135, 74);
            case 2048:
                return new Color(79, 140, 34);
            default:
                return new Color(33, 155, 157); // Default color for larger values
        }
    }
}
