
public class PlayerClass {
    //Player type
    public static final int HUMAN = 0;
    public static final int CPU = 1;
    //Player order
    public static final int PLAYER1 = 1;
    public static final int PLAYER2 = 2;
    //Type of instance
    public int playerType;
    public int playerOrder;
    public char playerFlag;

    public PlayerClass() {
    }

    public PlayerClass(int playerType, int playerOrder, char playerFlag) {
        this.playerType = playerType;
        this.playerOrder = playerOrder;
        this.playerFlag = playerFlag;
    }

    public String getPlayerGist (int playerType) {
        if (playerType == HUMAN){
            return "Игрок";
        } else {
            return "Компьютер";
        }
    }
}
