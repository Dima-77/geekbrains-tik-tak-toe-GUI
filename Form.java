import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Form extends JFrame{
    Map jMap;
    SNGame snGame;
    //Game type
    public static final int PLAYERvsPLAYER = 0;
    public static final int PLAYERvsCPU = 1;

    public Form(){
        setSize(507,555);
        setResizable(false);
        setLocation(500,200);
        setTitle("Tic Tac Toe");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        jMap = new Map();
        JPanel jbPanel = new JPanel(new GridLayout());
        add(jMap, BorderLayout.CENTER);
        add(jbPanel, BorderLayout.SOUTH);
        JButton jbStart = new JButton("Start New Game");
        JButton jbEnd = new JButton("End Game");
        jbPanel.add(jbStart);
        jbPanel.add(jbEnd);
        snGame = new SNGame(this);
        createNewGame(5, 4, new PlayerClass(0, 1, 'X'), new PlayerClass(1, 2, 'O'));

        jbStart.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                snGame.setVisible(true);
                setEnabled(false);
            }
        });

        jbEnd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        setVisible(true);
    }
    public void createNewGame (int linesCount, int winLine, PlayerClass player1, PlayerClass player2) {
        jMap.startNewGame(linesCount, winLine, player1, player2);
    }
}