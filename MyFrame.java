import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class MyFrame extends JFrame implements MouseListener {

    Square[][] array = new Square[10][10];
    final int X = 20;
    final int Y = 20;
    int xPos = 0;
    int yPos = 0;
    int newX = 0;
    int newY = 0;
    int mines = 0;
    final int SIZE = 10;
    final double PROBABILITY = 0.16;

    MyFrame() {
        this.setLayout(null);
        this.setSize(500, 500);
        this.setVisible(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setBackground(Color.WHITE);
        addMouseListener(this);
        for(int j = 0; j < SIZE; j++) {
            for(int i = 0; i < SIZE; i++) {
                array[j][i] = new Square();
                if (Math.random() < PROBABILITY) {
                    array[j][i].mine = true;
                } else {
                    array[j][i].revealed = true;
                }
            }
        }
        getNumMines();
        countNumbers();
        repaint();
    }

    public void paint(Graphics g) {
        g.setColor(Color.WHITE);
        g.fillRect(0,0,500,500);
        g.setColor(Color.GRAY);
        for (int j = 0; j < SIZE; j++) {
            for (int i = 0; i < SIZE; i++) {
                if (array[j][i].mine) {
                    g.setColor(Color.RED);
                }
                g.fillRect(i * (X + 2) + 8, j * (Y + 2) + 31, X, Y);
                if (array[j][i].revealed) {
                    g.setColor(Color.BLACK);
                    g.drawString(String.valueOf(array[j][i].number), i * (X + 2) + 15, j * (Y + 2) + 49);
                }
                g.setColor(Color.GRAY);
            }
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        xPos = e.getX();
        yPos = e.getY();
        newX = (xPos - 8) / (X + 2);
        newY = (yPos - 31) / (Y + 2);


        repaint();
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

    public void getNumMines() {
        for(int j = 0; j < SIZE; j++) {
            for(int i = 0; i < SIZE; i++) {
                if (array[j][i].mine) {
                    mines++;
                }
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
}
