import javax.swing.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Board {
    private final int SIZE = 4; // Board size (4x4 grid)
    private ArrayList<ArrayList<Integer>> matrix; // 2D grid for storing the board state
    private Random random; // Random number generator for placing new numbers
    private Frame frame; // Reference to the Frame object for UI updates
    private int[] toplist = new int[10]; // Array to store top 10 high scores

    public Board() {
        // Initialize a 4x4 grid with zeros
        matrix = IntStream.range(0, 4).mapToObj(i -> IntStream.generate(() -> 0) // Generate zeros for each column
                        .limit(4) // Define the number of columns
                        .boxed() // Convert int to Integer
                        .collect(Collectors.toCollection(ArrayList::new))) // Collect into rows of ArrayLists
                .collect(Collectors.toCollection(ArrayList::new));

        // Add two random numbers (2 or 4) at the beginning
        addNewNumber();
        addNewNumber();
    }

    // Returns the current grid state
    public ArrayList<ArrayList<Integer>> getGrid() {
        return matrix;
    }

    public int getBiggestScore(){
        return toplist[0];
    }

    // Set the Frame object to be used for UI updates
    public void setFrame(Frame frame) {
        this.frame = frame;
    }

    // Returns the total score of the board (sum of all numbers)
    public int getScore() {
        return matrix.stream().flatMapToInt(row -> row.stream().mapToInt(Integer::intValue)).sum();
    }

    // Adds a new number (2 or 4) at a random empty position on the board
    public void addNewNumber() {
        random = new Random();
        int row, col;
        // Find an empty spot (value 0)
        do {
            row = random.nextInt(SIZE);
            col = random.nextInt(SIZE);
        } while (matrix.get(row).get(col) != 0);

        // Randomly decide if the new number will be 2 or 4
        int rand = random.nextInt(10);
        if (rand < 8) {
            rand = 2;
        } else {
            rand = 4;
        }
        matrix.get(row).set(col, rand);
        if (frame != null) {
            frame.updateScoreLabel(); // Update the score display
        }
    }

    // Checks if the game is over (no empty spots and no moves left)
    public boolean isOver() {
        for (ArrayList<Integer> row : matrix) {
            if (row.contains(0)) { // If there is any empty spot
                return false;
            }
        }

        int goodNeighbour = 0;
        // Check for adjacent cells in rows that have the same value
        for (int i = 0; i < matrix.size(); i++) {
            for (int j = 0; j < matrix.size() - 1; j++) {
                if (Objects.equals(matrix.get(i).get(j), matrix.get(i).get(j + 1))) {
                    goodNeighbour++;
                }
            }
        }

        // Check for adjacent cells in columns that have the same value
        for (int i = 0; i < matrix.size() - 1; i++) {
            for (int j = 0; j < matrix.size(); j++) {
                if (Objects.equals(matrix.get(i).get(j), matrix.get(i + 1).get(j))) {
                    goodNeighbour++;
                }
            }
        }

        return goodNeighbour == 0; // If there are no valid moves left, the game is over
    }

    // Move all elements to the left, combining adjacent equal values
    public void moveLeft() {
        boolean moved = false;
        for (int i = 0; i < SIZE; i++) { // Loop through each row
            int[] row = new int[SIZE];
            int index = 0;
            // Collect all non-zero elements in the row
            for (int j = 0; j < SIZE; j++) {
                if (matrix.get(i).get(j) != 0) {
                    row[index++] = matrix.get(i).get(j);
                }
            }
            // Combine adjacent equal elements
            for (int j = 0; j < index - 1; j++) {
                if (row[j] == row[j + 1]) {
                    row[j] *= 2; // Double the value of the element
                    row[j + 1] = 0; // Set the next element to 0
                    moved = true;
                }
            }
            int[] merged = new int[SIZE];
            index = 0;
            // Move non-zero elements to the left
            for (int j = 0; j < SIZE; j++) {
                if (row[j] != 0) {
                    merged[index++] = row[j];
                }
            }
            // Update the row with the final merged values
            for (int j = 0; j < SIZE; j++) {
                if (matrix.get(i).get(j) != merged[j]) {
                    moved = true;
                }
                matrix.get(i).set(j, merged[j]);
            }
        }
        // If the board changed, add a new number
        if (moved) {
            addNewNumber();
        } else {
            // If the game is over, restart
            if (isOver()) {
                frame.newGame();
            }
        }
    }

    // Same as moveLeft, but moves elements to the right
    public void moveRight() {
        boolean moved = false;
        for (int i = 0; i < SIZE; i++) {
            int[] row = new int[SIZE];
            int index = 0;
            // Collect all non-zero elements in the row
            for (int j = 0; j < SIZE; j++) {
                if (matrix.get(i).get(j) != 0) {
                    row[index++] = matrix.get(i).get(j);
                }
            }
            // Combine adjacent equal elements
            for (int j = 0; j < index - 1; j++) {
                if (row[j] == row[j + 1]) {
                    row[j] *= 2;
                    row[j + 1] = 0;
                    moved = true;
                }
            }
            int[] merged = new int[SIZE];
            index = SIZE - 1;
            // Move non-zero elements to the right
            for (int j = SIZE - 1; j >= 0; j--) {
                if (row[j] != 0) {
                    merged[index--] = row[j];
                }
            }
            // Update the row with the final merged values
            for (int j = SIZE - 1; j >= 0; j--) {
                if (matrix.get(i).get(j) != merged[j]) {
                    moved = true;
                }
                matrix.get(i).set(j, merged[j]);
            }
        }
        if (moved) {
            addNewNumber();
        } else {
            if (isOver()) {
                frame.newGame();
            }
        }
    }

    // Same as moveLeft, but moves elements upwards (columns)
    public void moveUp() {
        boolean moved = false;
        for (int j = 0; j < SIZE; j++) {
            int[] column = new int[SIZE];
            int index = 0;
            // Collect all non-zero elements in the column
            for (int i = 0; i < SIZE; i++) {
                if (matrix.get(i).get(j) != 0) {
                    column[index++] = matrix.get(i).get(j);
                }
            }
            // Combine adjacent equal elements
            for (int i = 0; i < index - 1; i++) {
                if (column[i] == column[i + 1]) {
                    column[i] *= 2;
                    column[i + 1] = 0;
                    moved = true;
                }
            }
            int[] merged = new int[SIZE];
            index = 0;
            // Move non-zero elements upwards
            for (int i = 0; i < SIZE; i++) {
                if (column[i] != 0) {
                    merged[index++] = column[i];
                }
            }
            // Update the column with the final merged values
            for (int i = 0; i < SIZE; i++) {
                if (matrix.get(i).get(j) != merged[i]) {
                    moved = true;
                }
                matrix.get(i).set(j, merged[i]);
            }
        }
        if (moved) {
            addNewNumber();
        } else {
            if (isOver()) {
                frame.newGame();
            }
        }
    }

    // Same as moveUp, but moves elements downwards (columns)
    public void moveDown() {
        boolean moved = false;
        for (int j = 0; j < SIZE; j++) {
            int[] column = new int[SIZE];
            int index = 0;
            // Collect all non-zero elements in the column
            for (int i = 0; i < SIZE; i++) {
                if (matrix.get(i).get(j) != 0) {
                    column[index++] = matrix.get(i).get(j);
                }
            }
            // Combine adjacent equal elements
            for (int i = 0; i < index - 1; i++) {
                if (column[i] == column[i + 1]) {
                    column[i] *= 2;
                    column[i + 1] = 0;
                    moved = true;
                }
            }
            int[] merged = new int[SIZE];
            index = SIZE - 1;
            // Move non-zero elements downwards
            for (int i = SIZE - 1; i >= 0; i--) {
                if (column[i] != 0) {
                    merged[index--] = column[i];
                }
            }
            // Update the column with the final merged values
            for (int i = SIZE - 1; i >= 0; i--) {
                if (matrix.get(i).get(j) != merged[i]) {
                    moved = true;
                }
                matrix.get(i).set(j, merged[i]);
            }
        }
        if (moved) {
            addNewNumber();
        } else {
            if (isOver()) {
                frame.newGame();
            }
        }
    }

    // Save the current board state to a file
    public void saveMatrix() {
        saveTopList();
        JFileChooser fileChooser = new JFileChooser(new File("."));
        fileChooser.setSelectedFile(new File("save.txt"));

        int files = fileChooser.showSaveDialog(frame != null ? frame.getFrame() : null);
        if (files == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                for (int i = 0; i < matrix.size(); i++) {
                    for (int j = 0; j < matrix.size(); j++) {
                        writer.write(matrix.get(i).get(j) + " ");
                    }
                    writer.newLine();
                }
            } catch (IOException e) {
                System.out.println("Error saving to file!");
            }
        }
    }

    // Load a previously saved board state from a file
    public void loadMatrix() {
        JFileChooser fileChooser = new JFileChooser(new File("."));
        fileChooser.setSelectedFile(new File("save.txt"));
        int files = fileChooser.showOpenDialog(frame != null ? frame.getFrame() : null);
        if (files == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                int x = 0;
                while ((line = reader.readLine()) != null) {
                    String[] words = line.split(" "); // Split the line into numbers
                    for (int y = 0; y < words.length; y++) {
                        matrix.get(x).set(y, Integer.valueOf(words[y]));
                    }
                    x++;
                }
            } catch (IOException e) {
                System.out.println("Error reading from file!");
            }
        }
        frame.updateScoreLabel(); // Update the score after loading
    }

    // Save the current score to the top 10 list
    public void saveTopList() {
        int score = getScore(); // Get the current score to save it
        for (int i = 0; i < 10; i++) {
            if (toplist[i] < score) {
                int temp = toplist[i];
                toplist[i] = score;
                i++;
                while (i < 10) {
                    int temp2 = toplist[i];
                    toplist[i] = temp;
                    temp = temp2;
                    i++;
                }
                break;
            }
        }
        File file = new File("TopList.txt");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (int i = 0; i < 10; i++) {
                writer.write(String.valueOf(toplist[i]));
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error saving to topList file!");
        }
    }

    // Load the top 10 high scores from a file
    public void loadTopList() {
        File file = new File("TopList.txt");
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            int i = 0;
            while ((line = reader.readLine()) != null) {
                toplist[i] = Integer.valueOf(line);
                i++;
            }
        } catch (IOException e) {
            System.out.println("Error reading from topList file!");
        }
    }

    // Convert the top 10 scores to a formatted string for display
    public StringBuilder listToString() {
        StringBuilder builder = new StringBuilder();
        builder.append("<html>");
        for (int j = 0; j < 10; j++) {
            builder.append((j + 1) + ". ").append(toplist[j]).append("<br>");
        }
        builder.append("</html>");
        return builder;
    }

    // Reset the board to its initial state
    public void reset() {
        matrix = IntStream.range(0, 4).mapToObj(i -> IntStream.generate(() -> 0) // Generate zeros for each column
                        .limit(4) // Define the number of columns
                        .boxed() // Convert int to Integer
                        .collect(Collectors.toCollection(ArrayList::new))) // Collect into rows of ArrayLists
                .collect(Collectors.toCollection(ArrayList::new));

        // Add two random numbers at the beginning
        addNewNumber();
        addNewNumber();
    }
}

