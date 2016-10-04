import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Map extends JPanel {
    final static int PANEL_SIZE = 500;
    int linesCount;
    int cellSize;
    boolean gameOver;
    String gameOverMessage;
    boolean timeOfPlayer1;
    Field field;
    PlayerClass player1;
    PlayerClass player2;
    MouseEvent mouseClick;

    public Map() {
        setVisible(true);
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                mouseClick = e;
                if (!gameOver) {
                    if (timeOfPlayer1) {
                        turnOfPlayer(player1, mouseClick);
                        if (field.checkFull() && !gameOver) {
                            gameOverMessage = "Ничья.";
                            gameOver = true;
                        }
                        //Выходим чтобы не спровоцировать постановку флага в эту же ячейку вторым игроком
                        if (player2.playerType == PlayerClass.HUMAN) {
                            return;
                        }
                    }
                }
                if (!gameOver) {
                    if (!timeOfPlayer1) {
                        turnOfPlayer(player2, mouseClick);
                    }
                }
            }
        });
        //System.out.println(mouseClick.toString());
    }

    public void startNewGame(int lines, int winLine, PlayerClass player1, PlayerClass player2) {
        this.linesCount = lines;
        this.player1 = player1;
        this.player2 = player2;
        cellSize = PANEL_SIZE / linesCount;
        setBackground(Color.white);
        gameOver = false;
        timeOfPlayer1 = true;
        field = new Field(lines, winLine);
        this.repaint();
    }

    private void turnOfPlayer(PlayerClass player, MouseEvent e) {
        if (player.playerType == PlayerClass.CPU) {

            Thread t = Thread.currentThread ();
            System.out.println ("Текущий поток: " + t);
            //JOptionPane.showMessageDialog (null,"Текущий поток: " + t);

            try {
              for ( int n = 1; n > 0; n--) {
                 Thread.sleep(1000);
              }
            } catch (InterruptedException i) {
                JOptionPane.showMessageDialog(null,"Главный поток прерван");
            }

            field.turnOfAI(player.playerFlag);
            timeOfPlayer1 = !timeOfPlayer1;
            this.repaint();
            if (field.checkWin(player.playerFlag)) {
                gameOverMessage = "Выиграл компьютер.";
                gameOver = true;
            }
        } else {    //Ход человека
            //Ставим флаг если ячейка пуста
            if (field.checkVoid(e.getY() / cellSize, e.getX() / cellSize)) {
                field.setFlag(e.getY() / cellSize, e.getX() / cellSize, player.playerFlag);
            } else {
                return;     //Не та ячейка - выходим без изменений
            }
            //Если постановка флага удачна передаём ход другому игроку
            timeOfPlayer1 = !timeOfPlayer1;
            e.getComponent().repaint();
            if (field.checkWin(player.playerFlag)) {
                gameOverMessage = "Выиграл игрок №" + player.playerOrder + ".";
                gameOver = true;
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        //Рисуем сетку
        for (int i = 0; i < linesCount; i++) {
            g.setColor(Color.blue);
            g.drawLine(0, i * cellSize, PANEL_SIZE, i * cellSize);
            g.drawLine(i * cellSize, 0, i * cellSize, PANEL_SIZE);
        }
        //Рисуем содержимое ячеек
        float padding = 0.16f; //Отступы от границы ячейки (коэффициент)
        char[][] map = field.getMap();
        //Размеры фишек
        int width = (int) (cellSize * (1 - 2 * padding));
        int height = (int) (cellSize * (1 - 2 * padding));
        int tickO = (int) (cellSize * 0.08 * Math.log(map.length));   //Толщина O
        int tickX = (int) Math.sqrt(tickO * tickO / 2) + 1;    //Полутолщина Х в осевом направлении

        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[0].length; j++) {
                //Положение фишек
                int x = (int) (j * cellSize + cellSize * padding);
                int y = (int) (i * cellSize + cellSize * padding);

                switch (map[i][j]) {
                    case (Field.AI_FLAG):
                        g.setColor(Color.RED);
                        g.fillOval(x, y, width, height);
                        g.setColor(Color.WHITE);
                        g.fillOval(x + tickO, y + tickO, width - 2 * tickO, height - 2 * tickO);
                        break;
                    case (Field.HUMAN_FLAG):
                        for (int k = 0; k < tickX; k++) {
                            g.setColor(Color.GREEN);
                            g.drawLine(x + tickX, y + tickX - k, x + width - tickX + k, y + height - tickX);
                            g.drawLine(x + width - tickX, y + tickX - k, x + tickX - k, y + height - tickX);
                            g.drawLine(x + tickX - k, y + tickX, x + width - tickX, y + height - tickX + k);
                            g.drawLine(x + width - tickX + k, y + tickX, x + tickX, y + height - tickX + k);
                        }
                        break;
                    default:
                        continue;
                }
            }
        }

        if (gameOver) {
            int textLength = gameOverMessage.length()*27;
            g.setColor(Color.BLACK);
            g.fillRect((500-textLength)/2-4, 213, textLength+8, 69);
            g.setColor(Color.MAGENTA);
            g.fillRect((500-textLength)/2-2, 215, textLength+4, 65);
            g.setColor(Color.DARK_GRAY);
            g.setFont(new Font("sans serif", Font.BOLD, 46));
            g.drawString(gameOverMessage, (500-textLength)/2+2, 262);
            g.setColor(Color.ORANGE);
            g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 46));
            g.drawString(gameOverMessage, (500-textLength)/2, 260);
        }

        //Info
        if (timeOfPlayer1) {
            g.setColor(Color.BLACK);
            g.setFont(new Font(Font.MONOSPACED, Font.BOLD, 14));
            if (player1.playerType == PlayerClass.HUMAN) {
                g.drawString((player1.getPlayerGist(player1.playerType)) + " №" + (player1.playerOrder) + " ходит", 5, 13);
            } else {
                g.drawString((player1.getPlayerGist(player1.playerType)) + " ходит. Щелкните мышкой в любом месте...", 5, 13);
            }
        } else {
            g.setColor(Color.MAGENTA);
            g.setFont(new Font(Font.MONOSPACED, Font.BOLD, 14));
            if (player2.playerType == PlayerClass.HUMAN) {
                g.drawString((player2.getPlayerGist(player2.playerType)) + " №" + (player2.playerOrder) + " ходит", 5, 13);
            } else {
                g.drawString((player2.getPlayerGist(player2.playerType)) + " ходит", 5, 13);
            }
        }
    }
}
