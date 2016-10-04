import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class SNGame extends JFrame {
    JSlider sizeSlider;
    JSlider winSlider;
    public SNGame(Form owner) {
        setTitle("Options");
        setSize(300, 265);
        setLocation(900, 300);
        setResizable(false);
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
        JLabel jl1 = new JLabel("Игроки");
        jl1.setFont(new Font("Arial", Font.BOLD, 18));
        add(jl1);
        JRadioButton jrb1 = new JRadioButton("Игрок vs Компьютер");
        JRadioButton jrb2 = new JRadioButton("Игрок vs Игрок");
        jrb1.setSelected(true);
        ButtonGroup bg = new ButtonGroup();
        bg.add(jrb1);
        bg.add(jrb2);
        add(jrb1);
        add(jrb2);

        //Слайдеры
        JLabel jMapSizeLabel = new JLabel("Map size : 5");
        jMapSizeLabel.setFont(new Font("serif", Font.BOLD, 18));
        add(jMapSizeLabel);
        sizeSlider = new JSlider();
        sizeSlider.setValue(5);
        sizeSlider.setMinimum(2);
        sizeSlider.setMaximum(10);
        add(sizeSlider);

        JLabel jWinRowLabel = new JLabel(" Число фишек для победы : 4");
        jWinRowLabel.setFont(new Font("serif", Font.BOLD, 18));
        add(jWinRowLabel);
        winSlider = new JSlider();
        winSlider.setValue(4);
        winSlider.setMinimum(2);
        winSlider.setMaximum(10);
        add(winSlider);

        JLabel jTurnOrder = new JLabel(" Порядок ходов");
        jTurnOrder.setFont(new Font("serif", Font.BOLD, 18));
        add(jTurnOrder);
        JCheckBox jChB = new JCheckBox(" Я первый...");
        jChB.setSelected(true);
        add(jChB);

        jrb1.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if(jrb1.isSelected()) {
                    jChB.setVisible(true);
                    jTurnOrder.setVisible(true);
                } else {
                    jChB.setVisible(false);
                    jTurnOrder.setVisible(false);
                }
            }
        });

        sizeSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                jMapSizeLabel.setText(" Map size: " + sizeSlider.getValue());
                restrictWinSlider();
            }
        });

        winSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                jWinRowLabel.setText(" Число фишек для победы : " + winSlider.getValue());
            }
        });

        JButton jbOk = new JButton("Ok");
        add(jbOk);

        jbOk.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                PlayerClass player1;
                PlayerClass player2;
                if (jrb1.isSelected()) {
                    if (jChB.isSelected()){
                        player1 = new PlayerClass(PlayerClass.HUMAN, PlayerClass.PLAYER1, Field.HUMAN_FLAG);
                        player2 = new PlayerClass(PlayerClass.CPU, PlayerClass.PLAYER2, Field.AI_FLAG);
                    } else {
                        player1 = new PlayerClass(PlayerClass.CPU, PlayerClass.PLAYER1, Field.AI_FLAG);
                        player2 = new PlayerClass(PlayerClass.HUMAN, PlayerClass.PLAYER2, Field.HUMAN_FLAG);
                    }
                } else {
                    player1 = new PlayerClass(PlayerClass.HUMAN, PlayerClass.PLAYER1, Field.HUMAN_FLAG);
                    player2 = new PlayerClass(PlayerClass.HUMAN, PlayerClass.PLAYER2, Field.AI_FLAG);
                }
                restrictWinSlider();
                owner.setEnabled(true);
                owner.createNewGame(sizeSlider.getValue(), winSlider.getValue(), player1, player2);
                setVisible(false);
            }
        });

    }

    private void restrictWinSlider () {
        if (sizeSlider.getValue() < winSlider.getValue()) {
            winSlider.setValue(sizeSlider.getValue());
        }
    }
}
