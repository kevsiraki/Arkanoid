//events/updates to gameloop
import java.awt.event.KeyListener;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.Timer;
//gui/graphics
import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
//custom colors
import java.awt.Color;
import java.util.Random;
import java.awt.Font;
//audio
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class Gameplay extends JPanel implements KeyListener, ActionListener {
    private boolean win = false;
    private boolean play = false;
    private boolean restart = false;
    private boolean dummy = false;
    private boolean isPaused = false;
    private boolean canShoot = false;
    private int score = 0;
    private int totalBricks = 21; //21;
    private int delay = 15;
    private int playerX = 310;
    private int ballPosX = 120;
    private int ballPosY = 350;
    private int ballXDir = -1;
    private int ballYDir = -2;
    private int mapNum = 0;
    private int pX = -10;
    private int pY = -10;
    private int pYDir = 0;
    private int bulletYDir = 1;
    private int bulletYPos = 550;
    private int timesShot = 0;
    private int pRandX;
    private int pRandY;

    private Random randX;
    private Random randY;

    private String pauseMsg = "PAUSED";
    private Timer timer;
    private MapGenerator[] maps = new MapGenerator[4];
    private Color bgColor = Color.decode("#01002e");
    private Color paddleColor = Color.decode("#4d8eff");
    private Color ballColor = Color.decode("#c2a8ff");
    private final Color scoreColor = new Color(130, 77, 255, 100);
    private final Color pauseColor = new Color(223, 209, 255, 50);
    private Rectangle powerup;

    public Gameplay() {
        makeMaps();
        addKeyListener(this);
        setFocusable(true);
        setFocusTraversalKeysEnabled(false);
        timer = new Timer(delay, this);
        timer.addActionListener(this);
        timer.start();
        randX = new Random();
        randY = new Random();
        makePowerup();
    }

    public void sound(String filename) {
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(this.getClass().getResource(filename));
            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.start();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void makeMaps() {
        maps[0] = new MapGenerator(3, 7);
        maps[1] = new MapGenerator(5, 11);
        for (int i = 0; i < 11; i++)
            maps[1].map[2][i] = 0;
        for (int i = 0; i < 5; i++)
            maps[1].map[i][5] = 0;
        maps[2] = new MapGenerator(6, 11);
        maps[3] = new MapGenerator(11, 7);
        
        maps[3].map[0][0] = 0;
        maps[3].map[0][6] = 0;
        maps[3].map[10][6] = 0;
        maps[3].map[10][0] = 0;
        
        maps[3].map[1][0] = 0;
        maps[3].map[1][6] = 0;
        maps[3].map[9][6] = 0;
        maps[3].map[9][0] = 0;
        
        maps[3].map[0][1] = 0;
        maps[3].map[0][5] = 0;
        maps[3].map[10][1] = 0;
        maps[3].map[10][5] = 0;
        
        
    }

    @Override
    public void paint(Graphics g) {
        //background
        g.setColor(bgColor);
        g.fillRect(1, 1, 700, 600);
        //drawing map
        maps[mapNum].draw((Graphics2D) g);
        //borders
        g.setColor(scoreColor);
        g.fillRect(0, 0, 3, 600);
        g.fillRect(0, 0, 700, 3);
        g.fillRect(690, 0, 3, 600);
        //score
        g.setColor(scoreColor);
        g.setFont(new Font("serif", Font.ITALIC, 25));
        g.drawString("Score: " + score, 540, 30);
        //paddle
        g.setColor(paddleColor);
        g.fillRect(playerX, 550, 100, 8);
        //ball
        g.setColor(ballColor);
        g.fillOval(ballPosX, ballPosY, 16, 16);
        
        //fake ball
        //g.setColor(ballColor);
        //g.fillOval(ballPosX+30, ballPosY+30, 16, 16);

        //powerup icon
        g.setColor(Color.GREEN);
        g.fillRect(pX, pY, 8, 8);
        //  bullets 
        g.setColor(Color.ORANGE);
        g.fillRect(playerX + 50, bulletYPos+30, 4, 4);
        //pause info
        g.setColor(Color.BLUE);
        g.setFont(new Font("monospaced", Font.BOLD, 10));
        g.drawString("SPACE = PAUSE/PLAY", 10, 30);
        if (isPaused == true) {
            if (dummy == false) {
                sound("sound/pausein.wav");
                dummy = true;
            }
            dummy = false;
            g.setColor(pauseColor);
            g.setFont(new Font("monospaced", Font.BOLD, 30));
            g.drawString(pauseMsg, 290, 390);
            timer.stop();
        }
        if (ballPosY > 570) {
            if (dummy == false) {
                sound("sound/loss.wav");
                dummy = true;
            }
            g.setColor(bgColor);
            g.fillRect(1, 1, 700, 600);
            win = false;
            mapNum = 0;
            play = false;
            ballXDir = 0;
            ballYDir = 0;
            g.setColor(Color.PINK);
            g.setFont(new Font("monospaced", Font.ITALIC, 30));
            g.drawString("Game Over... Score: " + score, 150, 300);
            g.setFont(new Font("serif", Font.ITALIC, 20));
            g.drawString("Press Enter to Restart: ", 230, 350);
        }
        if (totalBricks <= 0) {
            if (dummy == false) {
                sound("sound/win.wav");
                dummy = true;
            }
            win = true;
            play = false;
            ballXDir = 0;
            ballYDir = 0;
            g.setColor(Color.blue);
            g.setFont(new Font("monospaced italic", Font.ITALIC, 30));
            g.drawString("You Won!", 260, 300);
            g.setFont(new Font("serif", Font.ITALIC, 20));
            g.drawString("Press Enter for Next Level!", 230, 350);
        }
        g.dispose();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        timer.start();
        if (play) {
            if (bulletYPos == 549 && timesShot <= 5) {
                sound("sound/bullet.wav");
                timesShot++;
            }
            if (new Rectangle(ballPosX, ballPosY, 20, 20).intersects(new Rectangle(playerX, 550, 100, 8))) {
                sound("sound/hit.wav");
                ballYDir = -ballYDir;
            }
            A: for (int i = 0; i < maps[mapNum].map.length; i++) {
                for (int j = 0; j < maps[mapNum].map[0].length; j++) {
                    if (timesShot > 5){ canShoot = false; 
                    bulletYPos = 550;
                     }
                    if (new Rectangle(pX, pY, 4, 4).intersects(new Rectangle(playerX, 550, 100, 8))) {
                        if (dummy == false) {
                            sound("sound/powerup.wav");
                            dummy = true;
                        }
                        timesShot = 0;
                        canShoot = true;
                    }
                    if (maps[mapNum].map[i][j] > 0) {
                        int brickX = j * maps[mapNum].brickWidth + 80;
                        int brickY = i * maps[mapNum].brickHeight + 50;
                        int brickWidth = maps[mapNum].brickWidth;
                        int brickHeight = maps[mapNum].brickHeight;
                        Rectangle rect = new Rectangle(brickX, brickY, brickWidth, brickHeight);
                        Rectangle ballRect = new Rectangle(ballPosX, ballPosY, 20, 20);
                        Rectangle brickRect = rect;
                        if (new Rectangle(playerX + 50, bulletYPos, 4, 4).intersects(brickRect)) {
                            sound("sound/break.wav");
                            maps[mapNum].setBrickValue(0, i, j);
                            totalBricks--;
                            score += 1;
                            bulletYPos = -1;
                        }
                        if (ballRect.intersects(brickRect)) {
                            sound("sound/break.wav");
                            maps[mapNum].setBrickValue(0, i, j);
                            totalBricks--;
                            score += 1;
                            if (i == pRandX && j == pRandY) {
                                sound("sound/powerbrick.wav");
                                pX = brickX + 40;
                                pY = brickY;
                                powerup = new Rectangle(pX, pYDir, 40, 40);
                                pYDir++;
                            }
                            if (ballPosX + 19 <= brickRect.x || ballPosX + 1 >= brickRect.x + brickRect.width) {
                                ballXDir = -ballXDir;
                            } else {
                                ballYDir = -ballYDir;
                            }
                            break A;
                        }
                    }
                }
            }
            bulletYPos += bulletYDir;
            pY += pYDir;
            ballPosX += ballXDir;
            ballPosY += ballYDir;
            if (canShoot && bulletYPos >= -10000) bulletYDir--;
            else {
                bulletYPos = 550;
                bulletYDir = 0;
            }
            if (ballPosX < 0) {
                ballXDir = -ballXDir;
                sound("sound/borderhit.wav");
            }
            if (ballPosY < 0) {
                ballYDir = -ballYDir;
                sound("sound/borderhit.wav");
            }
            if (ballPosX > 670) {
                ballXDir = -ballXDir;
                sound("sound/borderhit.wav");
            }
        }
        repaint();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_1) { //for my own maptesting and possible future menu
            mapNum = 0;
            makeMaps();
            totalBricks = 21; //21 
        }
        if (e.getKeyCode() == KeyEvent.VK_2) {
            mapNum = 1;
            makeMaps();
            totalBricks = 40; //40 
        }
        if (e.getKeyCode() == KeyEvent.VK_3) {
            mapNum = 2;
            makeMaps();
            totalBricks = 66; //66
        }
        if (e.getKeyCode() == KeyEvent.VK_4) {
            mapNum = 3;
            makeMaps();
            totalBricks = 65; //66
        }
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            if (!isPaused) {
                isPaused = true;
            } else {
                isPaused = false;
                timer.start();
                if (dummy == false) {
                    sound("sound/pauseout.wav");
                    dummy = true;
                }
                dummy = false;
            }
            //isPaused = !isPaused; //may use in some cases
        }
        if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            if (playerX > 580) {
                if (dummy == false) {
                    sound("sound/xbound.wav");
                    dummy = true;
                }
                dummy = false;
                playerX = 580;
            } else {
                moveRight();
            }
        }
        if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            if (playerX < 10) {
                if (dummy == false) {
                    sound("sound/xbound.wav");
                    dummy = true;
                }
                dummy = false;
                playerX = 10;
            } else {
                moveLeft();
            }
        }
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            dummy = false;
            if (mapNum < maps.length - 1 && win == true)
                mapNum++;
            else if (mapNum >= maps.length - 1 && win == true) {
                win = false;
                restart = true;
                makeMaps();
            }
            if ((!play) && win == false || restart == true) {
                restart = false;
                score = 0;
                totalBricks = 21; //21
                mapNum = 0;
                resetter();
                makeMaps();
                repaint();
            } else if (!play && win == true) {
                win = false;
                if (mapNum == 0) {
                    totalBricks = 21; //21

                } else if (mapNum == 1) {
                    totalBricks = 40; //40

                } else if (mapNum == 2) {
                    totalBricks = 66; //66
                }
                 else if (mapNum == 3) {
                    totalBricks = 65; //65
                }

                resetter();
                repaint();
            }
        }
    }

    public void resetter() {
        canShoot = false;
        timesShot = 0;
        makePowerup();
        play = true;
        ballPosX = 400;
        ballPosY = 400;
        ballXDir = -1;
        ballYDir = -2;
        playerX = 310;
    }

    public void makePowerup() {
        pRandY = randY.nextInt(maps[mapNum].map[0].length);
        pRandX = randX.nextInt(maps[mapNum].map.length);
        System.out.println("PowerUp Location: " + pRandX + ", " + pRandY);
    }

    public void moveRight() {
        play = true;
        playerX += 20;
    }

    public void moveLeft() {
        play = true;
        playerX -= 20;
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if ((e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_RIGHT) && !isPaused)
            sound("sound/move.wav");
    }

    @Override
    public void keyTyped(KeyEvent e) {}
}