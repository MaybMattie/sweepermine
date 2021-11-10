import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class MyFrame extends JFrame implements MouseListener {

    final int X = 20;
    final int Y = 20;
    final int X_OFFSET = 15;
    final int Y_OFFSET = 69;
    int xPos = 0;
    int yPos = 0;
    int newX = 0;
    int newY = 0;
    int mines = 0;
    final int SIZE = 30;
    Square[][] array = new Square[SIZE][SIZE];
    final double PROBABILITY = 0.16;
    boolean mined = false;
    boolean lost = false;
    boolean win = false;

    final BufferedImage unrevealed = cropImage(0, 0);
    final BufferedImage flag = cropImage(20, 0);
    final BufferedImage mine = cropImage(40, 0);
    final BufferedImage empty = cropImage(60, 0);
    final BufferedImage one = cropImage(0, 20);
    final BufferedImage two = cropImage(20, 20);
    final BufferedImage three = cropImage(40, 20);
    final BufferedImage four = cropImage(60, 20);
    final BufferedImage five = cropImage(0, 40);
    final BufferedImage six = cropImage(20, 40);
    final BufferedImage seven = cropImage(40, 40);
    final BufferedImage eight = cropImage(60, 40);
    final BufferedImage exploded = cropImage(0,  60);
    final BufferedImage flagDisp = cropImage(20, 60);

    final BufferedImage[] numberImgs = {one, two, three, four, five, six, seven, eight};


    MyFrame() {
        addMouseListener(this);
        this.setLayout(null);
        this.setSize(800, 800);
        this.setVisible(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setBackground(Color.WHITE);
        for(int j = 0; j < SIZE; j++) {
            for(int i = 0; i < SIZE; i++) {
                array[j][i] = new Square();
                array[j][i].update = true;
            }
        }
        repaint();
    }

    public void paint(Graphics g) {
        g.setColor(Color.WHITE);
        if (lost) {
            g.fillRect(0, 0, 1000, 1000);
            g.setColor(Color.black);
            g.drawString("you lost suck it", 100, 100);
            return;
        } else if (win) {
            g.fillRect(0, 0, 1000, 1000);
            g.setColor(Color.black);
            g.drawString("you won but you still suck", 100, 100);
            return;
        }
        g.fillRect(0,0,50,55);
        g.setColor(Color.BLACK);
        g.drawString("" + mines, 30, 43);
        g.drawImage(flagDisp, 10, 30, 20, 20, null);
        Square position = new Square();
        for (int j = 0; j < SIZE; j++) {
            for (int i = 0; i < SIZE; i++) {
                int currentX = i * X + X_OFFSET;
                int currentY = j * Y + Y_OFFSET;
                position = array[j][i];
                if (!position.update) continue;
                if (position.mine && position.revealed) {
                    g.drawImage(mine, currentX, currentY, X, Y, null);
                    g.drawImage(exploded, currentX, currentY, X, Y, null);
                    array[j][i].update = false;
                    continue;
                }
                g.drawImage(unrevealed, currentX, currentY, X, Y, null);
                if (position.revealed && !position.mine && !position.flagged && position.number != 0) {
                    g.drawImage(numberImgs[position.number - 1], currentX, currentY, X, Y, null);
                }
                if (position.number == 0 && position.revealed) {
                    g.drawImage(empty, currentX, currentY, X, Y, null);
                }
                if (position.flagged) {
                    g.drawImage(flag, (currentX), (currentY), 20, 20, null);
                }
                array[j][i].update = false;
            }
        }
    }

    public void countNumbers() {
        for (int y = 0; y < SIZE; y++) {
            for (int x = 0; x < SIZE; x++) {
                if (array[y][x].mine) continue;
                for (int j = -1; j < 2; j++) {
                    for (int i = -1; i < 2; i++) {
                        if (y + j < 0 || y + j > SIZE - 1 || x + i < 0 || x + i > SIZE - 1) continue;
                        if (array[y + j][x + i].mine) {
                            array[y][x].number++;
                        }
                    }
                }
            }
        }
    }

    public void imageDraw(Graphics g, String path, int x, int y) {
        BufferedImage img = null;
        try {
        	java.net.URL url = Main.class.getResource("/image/" + path + ".png");
            img = ImageIO.read(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(img != null) {
        	g.drawImage(img, x, y, 20, 20, null);
        }
    }
    
    public BufferedImage cropImage(int x, int y) {
        BufferedImage img = null;
        try {
            java.net.URL url = Main.class.getResource("/image/" + "minesweeperTileset" + ".png");
            img = ImageIO.read(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
    	return (img.getSubimage(x, y, 20, 20));
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        int button = e.getButton();
        xPos = e.getX();
        yPos = e.getY();
        newX = (xPos - X_OFFSET) / X;
        newY = (yPos - Y_OFFSET) / Y;
        if (newX >= SIZE || newY >= SIZE || newX < 0 || newY < 0) return;
        Square square = new Square();
        square = array[newY][newX];
        // Left Click
        if (button == 1) {
            if (!mined) {
                for(int j = 0; j < SIZE; j++) {
                    for(int i = 0; i < SIZE; i++) {
                        if ((j == newY || j == newY -1 || j == newY + 1) && (i == newX || i == newX -1 || i == newX + 1)) continue;
                        array[j][i] = new Square();
                        if (Math.random() < PROBABILITY) {
                            mines++;
                            array[j][i].mine = true;
                            array[j][i].revealed = false;
                            array[j][i].number = -1;
                        } else {
                            array[j][i].revealed = false;
                        }
                        array[j][i].update = true;
                    }
                }
                mined = true;
                countNumbers();
            }
            if (square.flagged) return;
            if (!square.revealed) {
                array[newY][newX].revealed = true;
                array[newY][newX].update = true;
                digZeros(newX, newY);
                if (array[newY][newX].mine) {
                    lost = true;
                    repaint();
                    return;
                }
            } else {
                int numFlags = 0;
                for (int j = -1; j < 2; j++) {
                    for (int i = -1; i < 2; i++) {
                        if (newY + j < 0 || newY + j > SIZE - 1 || newX + i < 0 || newX + i > SIZE - 1) continue;
                        if (array[newY + j][newX + i].flagged) {
                            numFlags++;
                        }
                    }
                }
                if (numFlags == array[newY][newX].number) {
                    for (int j = -1; j < 2; j++) {
                        for (int i = -1; i < 2; i++) {
                            if (newY + j < 0 || newY + j > SIZE - 1 || newX + i < 0 || newX + i > SIZE - 1) continue;
                            if (array[newY + j][newX + i].flagged) continue;
                            if (array[newY + j][newX + i].mine) {
                                array[newY + j][newX + i].revealed = true;
                                array[newY + j][newX + i].update = true;
                                System.out.println("you lose, loser");
                                lost = true;
                                repaint();
                                return;
                            }
                            array[newY + j][newX + i].revealed = true;
                            array[newY + j][newX + i].update = true;
                            digZeros(newX + i, newY + j);
                        }
                    }
                }
            }
        } // Right Click
        else if (button == 3) {
            if (square.revealed) return;
            if (!square.flagged) {
                array[newY][newX].flagged = true;
                array[newY][newX].update = true;
                mines--;
            } else if (square.flagged) {
                array[newY][newX].flagged = false;
                array[newY][newX].update = true;
                mines++;
            }
        }
        win();
        repaint();
    }

    public void digZeros(int x, int y) {
        if (array[y][x].number != 0) return;
        for (int j = -1; j < 2; j++) {
            for (int i = -1; i < 2; i++) {
                if (y + j < 0 || y + j > SIZE - 1 || x + i < 0 || x + i > SIZE - 1) continue;
                if (array[y + j][x + i].revealed) continue;
                if (!array[y + j][x + i].mine) {
                    array[y + j][x + i].revealed = true;
                    array[y + j][x + i].update = true;
                    if (array[y + j][x + i].number == 0) {
                        digZeros(x + i, y + j);
                    }
                }
            }
        }

    }

    public void win() {
        boolean cleared = true;
        if (mines == 0) {
            for (int y = 0; y < SIZE; y++) {
                for (int x = 0; x < SIZE; x++) {
                    if (array[y][x].flagged && !array[y][x].mine) {
                        cleared = false;
                        break;
                    } else if (!array[y][x].mine && !array[y][x].revealed) {
                        cleared = false;
                        break;
                    }
                }
            }
            if (cleared) {
                win = true;
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
    
}