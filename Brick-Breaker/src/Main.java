// Importing necessary packages
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

// Class for creating the brick map
class MapGenerator {
    public int map[][];
    public int brickWidth;
    public int brickHeight;

    // Constructor for creating a brick map of size (row x col)
    public MapGenerator(int row, int col) {
        map = new int[row][col];
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[0].length; j++) {
                map[i][j] = 1; // Initialize all bricks to 1 (not hit yet)
            }
        }

        brickWidth = 540 / col; // Calculate the width of each brick
        brickHeight = 150 / row; // Calculate the height of each brick
    }

    // Draw the bricks on the screen
    public void draw(Graphics2D g) {
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[0].length; j++) {
                if (map[i][j] > 0) {
                    g.setColor(new Color(0XFF8787)); // Brick color
                    g.fillRect(j * brickWidth + 80, i * brickHeight + 50, brickWidth, brickHeight);

                    g.setStroke(new BasicStroke(4));
                    g.setColor(Color.BLACK);
                    g.drawRect(j * brickWidth + 80, i * brickHeight + 50, brickWidth, brickHeight);
                }
            }
        }
    }

    // Set the value of a brick at a given position to 0 (hit)
    public void setBrickValue(int value, int row, int col) {
        map[row][col] = value;
    }
}

// Main class for the game
class GamePlay extends JPanel implements KeyListener, ActionListener {
    private boolean play = true;
    private int score = 0;
    private int totalBricks = 21;
    private Timer timer;
    private int delay = 8;
    private int playerX = 310;
    private int ballposX = 120;
    private int ballposY = 350;
    private int ballXdir = -1;
    private int ballYdir = -2;
    private MapGenerator map;

    // Constructor for setting up the game
    public GamePlay() {
        map = new MapGenerator(3, 7); // Create the brick map with 3 rows and 7 columns
        addKeyListener(this); // Add key listener to handle keyboard input
        setFocusable(true);
        setFocusTraversalKeysEnabled(false);
        timer = new Timer(delay, this); // Timer to update the game at a given delay
        timer.start(); // Start the game timer
    }

    // Draw the game objects on the screen
    public void paint(Graphics g) {
        // Background color
        g.setColor(Color.YELLOW);
        g.fillRect(1, 1, 692, 592);

        // Draw the brick map
        map.draw((Graphics2D) g);

        // Draw borders
        g.fillRect(0, 0, 3, 592);
        g.fillRect(0, 0, 692, 3);
        g.fillRect(691, 0, 3, 592);

        // Draw the paddle
        g.setColor(Color.blue);
        g.fillRect(playerX, 550, 100, 12);

        // Draw the ball
        g.setColor(Color.RED);
        g.fillOval(ballposX, ballposY, 20, 20);

        // Draw the score
        g.setColor(Color.black);
        g.setFont(new Font("MV Boli", Font.BOLD, 25));
        g.drawString("Score: " + score, 520, 30);

        // Check if the player wins or loses
        if (totalBricks <= 0) {
            play = false;
            ballXdir = 0;
            ballYdir = 0;
            g.setColor(new Color(0XFF6464));
            g.setFont(new Font("MV Boli", Font.BOLD, 30));
            g.drawString("You Won, Score: " + score, 190, 300);

            g.setFont(new Font("MV Boli", Font.BOLD, 20));
            g.drawString("Press Enter to Restart.", 230, 350);
        }

        if (ballposY > 570) {
            play = false;
            ballXdir = 0;
            ballYdir = 0;
            g.setColor(Color.BLACK);
            g.setFont(new Font("MV Boli", Font.BOLD, 30));
            g.drawString("Game Over, Score: " + score, 190, 300);

            g.setFont(new Font("MV Boli", Font.BOLD, 20));
            g.drawString("Press Enter to Restart", 230, 350);
        }
        g.dispose(); // Release system resources
    }

    @Override
    public void actionPerformed(ActionEvent arg0) {
        timer.start();
        if (play) {
            // Ball - Pedal interaction
            if (new Rectangle(ballposX, ballposY, 20, 20).intersects(new Rectangle(playerX, 550, 100, 8))) {
                ballYdir = -ballYdir;
            }
            // Ball - Brick interaction
            for (int i = 0; i < map.map.length; i++) {
                for (int j = 0; j < map.map[0].length; j++) {
                    if (map.map[i][j] > 0) {
                        int brickX = j * map.brickWidth + 80;
                        int brickY = i * map.brickHeight + 50;
                        int brickWidth = map.brickWidth;
                        int brickHeight = map.brickHeight;

                        Rectangle rect = new Rectangle(brickX, brickY, brickWidth, brickHeight);
                        Rectangle ballRect = new Rectangle(ballposX, ballposY, 20, 20);
                        Rectangle brickRect = rect;

                        if (ballRect.intersects(brickRect)) {
                            map.setBrickValue(0, i, j); // Set the brick value to 0 (hit)
                            totalBricks--;
                            score += 5;

                            if (ballposX + 19 <= brickRect.x || ballposX + 1 >= brickRect.x + brickRect.width)
                                ballXdir = -ballXdir; // Change ball direction on collision
                            else {
                                ballYdir = -ballYdir; // Change ball direction on collision
                            }
                        }
                    }
                }
            }

            ballposX += ballXdir;
            ballposY += ballYdir;
            if (ballposX < 0) {
                ballXdir = -ballXdir; // Bounce off the left wall
            }
            if (ballposY < 0) {
                ballYdir = -ballYdir; // Bounce off the top wall
            }
            if (ballposX > 670) {
                ballXdir = -ballXdir; // Bounce off the right wall
            }
        }
        repaint(); // Redraw the screen
    }

    @Override
    public void keyTyped(KeyEvent arg0) {
        // Not used
    }

    @Override
    public void keyPressed(KeyEvent arg0) {
        if (arg0.getKeyCode() == KeyEvent.VK_RIGHT) {
            if (playerX >= 600) {
                playerX = 600;
            } else {
                moveRight(); // Move the paddle to the right
            }
        }
        if (arg0.getKeyCode() == KeyEvent.VK_LEFT) {
            if (playerX < 10) {
                playerX = 10;
            } else {
                moveLeft(); // Move the paddle to the left
            }
        }

        if (arg0.getKeyCode() == KeyEvent.VK_ENTER) {
            // Restart the game on Enter key press
            if (!play) {
                play = true;
                ballposX = 120;
                ballposY = 350;
                ballXdir = -1;
                ballYdir = -2;
                score = 0;
                totalBricks = 21;
                map = new MapGenerator(3, 7);
                repaint(); // Redraw the screen
            }
        }
    }

    // Method to move the paddle to the right
    public void moveRight() {
        play = true;
        playerX += 50;
    }

    // Method to move the paddle to the left
    public void moveLeft() {
        play = true;
        playerX -= 50;
    }

    @Override
    public void keyReleased(KeyEvent arg0) {
        // Not used
    }
}

// Main class to run the game
class Main {
    public static void main(String[] args) {
        JFrame obj = new JFrame();
        GamePlay gamePlay = new GamePlay();
        obj.setBounds(10, 10, 700, 600);
        obj.setTitle("Brick Breaker");
        obj.setResizable(false);
        obj.setVisible(true);
        obj.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        obj.add(gamePlay);
    }
}