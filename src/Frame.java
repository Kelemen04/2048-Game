import javax.imageio.ImageIO;
import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;

public class Frame {
    private JFrame frame;
    private Board board = new Board();  // The game board object
    private Component component = new Component(board);  // Component for rendering the game
    private Music music;  // Music handler
    private JLabel text;  // Label for score text
    private JLabel score;  // Label for current score
    private JPanel topList;  // Panel for displaying the leaderboard
    private CardLayout cardLayout = new CardLayout();  // Card layout to switch between panels
    private JPanel mainPanel = new JPanel(cardLayout);  // Main panel containing different views
    private JPanel game;
    private JPanel musicPanel;
    private JPanel newGame;
    private Font font1;

    public Frame() throws UnsupportedAudioFileException, LineUnavailableException, IOException {
        board.loadTopList();  // Load the leaderboard
        board.setFrame(this);  // Set the frame object for the board

        newGame = new JPanel();  // Initialize the new game panel

        // Create different views (layouts) for the game
        topListLayout();
        gameLayout();
        musicLayout();

        // Create the main JFrame window
        frame = new JFrame();
        frame.setLayout(new BorderLayout());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setTitle("2048");
        frame.setSize(438, 550);
        frame.setLocationRelativeTo(null);

        // Add views to the main panel (for switching between screens)
        mainPanel.add(game, "Game");
        mainPanel.add(topList, "Uj");
        mainPanel.add(musicPanel, "Music");
        mainPanel.add(newGame, "NewGame");

        // Add the main panel to the frame
        frame.add(mainPanel);

        menu();  // Set up the menu for the game

        frame.setVisible(true);  // Make the frame visible
    }

    // Updates the score label to reflect the current score
    public void updateScoreLabel() {
        score.setText(String.valueOf(board.getScore()));

        if(board.getScore() > board.getBiggestScore())
        {
            text.setText("HIGHSCORE:");
        }
        else{
            text.setText("SCORE:");
        }
    }

    // Allows other classes to access the frame object
    public JFrame getFrame() {
        return frame;
    }

    // Sets the custom font used in the UI
    public void setFont() {
        try {
            font1 = Font.createFont(Font.TRUETYPE_FONT, new File("resources/font1.otf")).deriveFont(48f);
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, new File("resources/font1.otf")));
        } catch (IOException | FontFormatException e) {
            throw new RuntimeException(e);
        }
    }

    // Creates the music settings layout
    public void musicLayout() throws UnsupportedAudioFileException, LineUnavailableException, IOException {
        musicPanel = new JPanel();
        musicPanel.setPreferredSize(new Dimension(438, 550));
        musicPanel.setBackground(new Color(250, 246, 227));
        musicPanel.setLayout(null);

        // Load images for the music panel (decorative images, play and stop buttons)
        BufferedImage decoration = null;
        BufferedImage stopImage = null;
        BufferedImage startImage = null;
        try {
            decoration = ImageIO.read(new File("resources/photo1.png"));
            stopImage = ImageIO.read(new File("resources/photo2.png"));
            startImage = ImageIO.read(new File("resources/photo3.png"));
        } catch (IOException e) {
            System.out.println("Error when loading images for the music layout!");
        }
        JLabel decor = new JLabel(new ImageIcon(decoration));  // Decorative image for the music panel
        decor.setBounds(89, 30, 250, 250);

        setFont();
        // Buttons for controlling volume and music playback
        JButton volumeDownButton = new JButton("Volume Down");
        volumeDownButton.setBackground(new Color(176, 166, 149));
        volumeDownButton.setBounds(40, 300, 150, 40);
        volumeDownButton.setFont(font1.deriveFont(18f));

        JButton volumeUpButton = new JButton("Volume Up");
        volumeUpButton.setBackground(new Color(176, 166, 149));
        volumeUpButton.setBounds(238, 300, 150, 40);
        volumeUpButton.setFont(font1.deriveFont(18f));

        JButton stopButton = new JButton(new ImageIcon(stopImage));  // Stop button for music
        stopButton.setBounds(119, 360, 60, 60);
        stopButton.setBackground(new Color(250, 246, 227));

        JButton startButton = new JButton(new ImageIcon(startImage));  // Start button for music
        startButton.setBounds(249, 360, 60, 60);
        startButton.setBackground(new Color(250, 246, 227));

        // Set action listeners for the buttons
        music = new Music();
        volumeDownButton.addActionListener(e -> music.volumeDown());
        volumeUpButton.addActionListener(e -> music.volumeUp());
        stopButton.addActionListener(e -> {
            music.stop();
            music.clickSound();
        });
        startButton.addActionListener(e -> {
            music.start();
            music.clickSound();
        });

        // Add components to the music panel
        musicPanel.add(volumeDownButton);
        musicPanel.add(volumeUpButton);
        musicPanel.add(stopButton);
        musicPanel.add(startButton);
        musicPanel.add(decor);
    }

    // Creates the leaderboard layout
    public void topListLayout() {
        BufferedImage scoreboard = null;  // Load the scoreboard image
        try {
            scoreboard = ImageIO.read(new File("resources/score1.png"));
        } catch (IOException e) {
            System.out.println("Error when loading images for the top list layout!");
        }
        JLabel scoreboardPhoto = new JLabel(new ImageIcon(scoreboard));  // Add the scoreboard image
        scoreboardPhoto.setBounds(60, 10, 300, 82);

        topList = new JPanel();  // Initialize the top list panel
        topList.setLayout(null);
        topList.setBackground(new Color(250, 246, 227));

        setFont();
        JLabel list = new JLabel(String.valueOf(board.listToString()));  // Display the leaderboard list
        list.setBounds(180, 90, 300, 400);
        list.setFont(font1.deriveFont(30f));
        list.setBackground(Color.WHITE);

        // Add components to the top list panel
        topList.add(scoreboardPhoto);
        topList.add(list);
        topList.setPreferredSize(new Dimension(400, 400));

        topList.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                board.loadTopList();  // Reload the leaderboard when the panel is shown
                list.setText(String.valueOf(board.listToString()));
            }
        });
    }

    // Creates the game layout where the game board is displayed
    public void gameLayout() {
        game = new JPanel(new BorderLayout());  // Initialize the game panel
        game.setPreferredSize(new Dimension(438, 550));

        game.setFocusable(true);
        game.requestFocusInWindow();
        game.addKeyListener(new KeyAdapter() {  // Add key listeners to handle game movements
            @Override
            public void keyPressed(KeyEvent e) {
                if (game.hasFocus()) {
                    switch (e.getKeyCode()) {
                        case KeyEvent.VK_LEFT:
                            board.moveLeft();
                            break;
                        case KeyEvent.VK_RIGHT:
                            board.moveRight();
                            break;
                        case KeyEvent.VK_UP:
                            board.moveUp();
                            break;
                        case KeyEvent.VK_DOWN:
                            board.moveDown();
                            break;
                        case KeyEvent.VK_A:
                            board.moveLeft();
                            break;
                        case KeyEvent.VK_D:
                            board.moveRight();
                            break;
                        case KeyEvent.VK_W:
                            board.moveUp();
                            break;
                        case KeyEvent.VK_S:
                            board.moveDown();
                            break;
                    }
                    component.repaint();  // Repaint the game board after each move
                }
            }
        });

        setFont();
        JPanel panelUp = new JPanel();  // Panel to show the score at the top
        panelUp.setBackground(new Color(250, 246, 227));
        panelUp.setPreferredSize(new Dimension(300, 80));
        text = new JLabel("SCORE:");
        text.setFont(font1);
        score = new JLabel("0");
        score.setFont(font1);
        panelUp.add(text);
        panelUp.add(score);

        // Border panels to create space around the game board
        JPanel panelLeft = new JPanel();
        panelLeft.setBackground(new Color(250, 246, 227));

        JPanel panelRight = new JPanel();
        panelRight.setBackground(new Color(250, 246, 227));

        JPanel panelDown = new JPanel();
        panelDown.setBackground(new Color(250, 246, 227));

        // Add components to the game panel
        component.setPreferredSize(new Dimension(400, 400));
        game.add(component, BorderLayout.CENTER);
        game.add(panelUp, BorderLayout.NORTH);
        game.add(panelLeft, BorderLayout.WEST);
        game.add(panelRight, BorderLayout.EAST);
        game.add(panelDown, BorderLayout.SOUTH);
    }

    public void menu() {
        setFont();
        JMenuBar bar = new JMenuBar();  // Menu bar creation
        bar.setBackground(new Color(250, 246, 227));
        bar.setFont(font1.deriveFont(14f));

        JMenu menu = new JMenu("Menu");  // Menu creation
        menu.setFont(font1.deriveFont(14f));

        // Menu items for saving, loading, and navigating to different views
        JMenuItem saveItem = new JMenuItem("Save");
        saveItem.addActionListener(e -> {
            board.saveMatrix();  // Save the game state
            board.loadTopList();
        });

        JMenuItem loadItem = new JMenuItem("Load");
        loadItem.addActionListener(e -> {
            board.loadMatrix();  // Load the saved game state
            updateScoreLabel();
            component.repaint();
        });

        JMenuItem gameMenu = new JMenuItem("Game");
        JMenuItem topList = new JMenuItem("Top List");
        JMenuItem music = new JMenuItem("Music");

        // Action listeners for menu items
        gameMenu.addActionListener((ActionEvent e) -> {
            cardLayout.show(mainPanel, "Game");
            game.requestFocusInWindow();
        });
        topList.addActionListener((ActionEvent e) -> cardLayout.show(mainPanel, "Uj"));
        music.addActionListener((ActionEvent e) -> cardLayout.show(mainPanel, "Music"));
        frame.add(mainPanel);

        // Add items to the menu
        menu.add(saveItem);
        menu.add(loadItem);
        menu.add(gameMenu);
        menu.add(topList);
        menu.add(music);

        bar.add(menu);  // Add the menu to the menu bar
        frame.setJMenuBar(bar);
        frame.setVisible(true);
    }

    // Creates the "New Game" screen after game over
    public void newGame() {
        newGame.removeAll();  // Clear the panel for new game screen

        setFont();

        try {
            Thread.sleep(4000);  // Delay before showing "GAME OVER"
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }

        newGame.setBackground(new Color(250, 246, 227));
        newGame.setPreferredSize(new Dimension(438, 550));
        newGame.setLayout(null);  // No layout manager

        // "GAME OVER" text
        JLabel gameOverText = new JLabel("GAME OVER", SwingConstants.CENTER);
        gameOverText.setFont(font1.deriveFont(70f));
        gameOverText.setBounds(50, 100, 338, 80);  // Centered text

        // "YOUR SCORE" text
        JLabel scoreLabel = new JLabel("YOUR SCORE: " + board.getScore(), SwingConstants.CENTER);
        scoreLabel.setFont(font1.deriveFont(25f));
        scoreLabel.setBounds(75, 220, 288, 50);  // Centered text

        // "NEW GAME" button
        JButton newGameButton = new JButton("NEW GAME");
        newGameButton.setFont(font1.deriveFont(20f));
        newGameButton.setBackground(new Color(103, 96, 69));
        newGameButton.setForeground(Color.WHITE);
        newGameButton.setBounds(125, 340, 180, 50);  // Centered button

        // Action listener for starting a new game
        newGameButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                board.reset();  // Reset the game board

                cardLayout.show(mainPanel, "Game");  // Show the game screen
                game.requestFocusInWindow();
                component.repaint();  // Repaint the game board with the new state
            }
        });

        // Add components to the new game screen
        newGame.add(gameOverText);
        newGame.add(scoreLabel);
        newGame.add(newGameButton);

        newGame.revalidate();
        newGame.repaint();

        // Update the view
        cardLayout.show(mainPanel, "NewGame");
        newGame.requestFocusInWindow();
    }
}
