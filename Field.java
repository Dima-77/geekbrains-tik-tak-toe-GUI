import java.util.Random;

/**
 * Created by ���� on 07.08.2016.
 */
public class Field {
    private char[][] map;
    int winLine;
    static final char VOID_CELL = '*';
    //������ �������� ������ �� ��������� � ���� ������ (���� ��� ���)
    //��� ������ ��� ������ ���� �����
    static final char HUMAN_FLAG = 'X';
    static final char AI_FLAG = 'O';
    private int[][][] linesWin = null;
    private int[][][] linesFork = null;

    public Field(int lines, int winLine) {
        map = new char[lines][lines];
        this.winLine = winLine;
        initMap();
        if (winLine <= lines) {  //����� ��������� ����� �� ������ ��������� ������ ����
            linesWin = getLines(winLine).clone();
        }
        if (winLine < lines) {
            //����� �� ����� ����� ��������� ������,
            //���� ����� ���������� ����� ������ ������� ����
            linesFork = getLines(winLine + 1).clone();
        }
    }

    public void initMap() {
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[0].length; j++) {
                map[i][j] = VOID_CELL;
            }
        }
    }

    public void setFlag(int x, int y, char flag) {
        map[x][y] = flag;
    }

    public boolean checkVoid(int x, int y) {
        if (x < 0 || x > map.length - 1 || y < 0 || y > map[0].length - 1 || (map[x][y] != VOID_CELL)) {
            return false;
        }
        return true;
    }

    public char[][] getMap() {
        return map;
    }

    boolean checkFull() {
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[0].length; j++) {
                if (map[i][j] == VOID_CELL) {
                    return false;
                }
            }
        }
        return true;
    }

    void turnOfAI(char myAIFlag) {
        int x, y;
        char enemyFlag;
        int[] voidCell;
        if (myAIFlag == AI_FLAG) {
            enemyFlag = HUMAN_FLAG;
        } else {
            enemyFlag = AI_FLAG;
        }
        //�������� ������ ��� ������
        int nextWin[] = checkNextWin(myAIFlag).clone();
        //�������� ������ ��� ����������, ���� � ����. ���� ��������� ������ ����������
        int nextEnemyWin[] = checkNextWin(enemyFlag).clone();
        //�������� ������ ��� ��������� ��� ����������, ���� � ����. ���� ��������� ����� �� �����
        int nextFork[] = checkLinesFork(myAIFlag).clone();
        int nextEnemyFork[] = checkLinesFork(enemyFlag).clone();
        if (nextWin[0] > -1) {
            x = nextWin[0];
            y = nextWin[1];
        } else if (nextEnemyWin[0] > -1) {
            x = nextEnemyWin[0];
            y = nextEnemyWin[1];
        } else if (nextFork[0] > -1) {
            x = nextFork[0];
            y = nextFork[1];
        } else if (nextEnemyFork[0] > -1) {
            x = nextEnemyFork[0];
            y = nextEnemyFork[1];
        } else {    //���������� ������ ��� ����� �� ������� ������ �� ������ �����
            voidCell = getRandomVoidCell().clone();
            x = voidCell[0];
            y = voidCell[1];
        }
        setFlag(x, y, myAIFlag);
    }

    boolean checkWin(char flag) {
        //�������� ����� ����� winLine
        if (linesWin == null) {
            return false;
        }
        int winRow;
        for (int i = 0; i < linesWin.length; i++) {
            winRow = 0;
            for (int j = 0; j < linesWin[0].length; j++) {
                if (map[linesWin[i][j][0]][linesWin[i][j][1]] == flag) {
                    winRow++;
                }
            }
            if (winRow == winLine) {
                return true;
            }
        }
        return false;
    }

    //����� ������ ���� ����� � ������������ �� ����� ��� ��������� ��������
    private int[][][] getLines(int length) {
        int[][][] lines;
        int offset = map.length - length + 1; //����� �������� � ������� ��� ������ ��� ������� ���������
        //����� ������������ �������� � ����� �����������
        int diagonalLines = offset;
        for (int i = 1; i < offset; i++) {
            diagonalLines += 2 * (offset - i);
        }
        int numOfLine = 2 * offset * map.length + 2 * diagonalLines;
        lines = new int[numOfLine][length][2];
        numOfLine = 0;
        for (int i = 0; i < offset; i++) {   //�������� ������ �����
            for (int j = 0; j < map.length; j++) {   //������ �� �������� ����
                lines[numOfLine] = getLine(j, i, 0, 1, length).clone();    //�������������� �����
                lines[numOfLine + 1] = getLine(i, j, 1, 0, length).clone();  //������������ �����
                numOfLine += 2;   //��� ����� �������
            }
        }   //������������ � �������������� ����� �������
        for (int i = 0; i < offset; i++) {   //�������� �� �����������
            for (int k = 0; k < offset; k++) {   //�������� �� ���������
                lines[numOfLine] = getLine(i, k, 1, 1, length).clone();    //������ �����
                lines[numOfLine + 1] = getLine(i, map.length - 1 - k, 1, -1, length).clone();    //������ ������
                numOfLine += 2;
            }
        }   //��������� �������
        return lines;
    }

    private int[][] getLine(int cx, int cy, int vx, int vy, int l) {
        int[][] line = new int[l][2];  //���������� ����� �����
        for (int i = 0; i < l; i++) {
            line[i][0] = cx + i * vx;
            line[i][1] = cy + i * vy;
        }
        return line;
    }

    //��������� ����� ����� winLine �� ������� winLine-1 ������ �
    //������ ��������� ������ �� ��� ��� ��������� ������������, ������
    private int[] checkNextWin(char flag) {
        int[] freeCell = new int[2];
        //������� ����� �� ���������� - �������� �� ����� ������
        if (linesWin == null) {
            freeCell[0] = -1;
            freeCell[1] = -1;
            return freeCell;
        }
        char secondFlag;
        if (flag == HUMAN_FLAG) {
            secondFlag = AI_FLAG;
        } else {
            secondFlag = HUMAN_FLAG;
        }
        int winRow;
        for (int i = 0; i < linesWin.length; i++) {
            winRow = 0;
            for (int j = 0; j < linesWin[0].length; j++) {
                if (map[linesWin[i][j][0]][linesWin[i][j][1]] == flag) {
                    winRow++;
                } else if (map[linesWin[i][j][0]][linesWin[i][j][1]] == secondFlag) {
                    winRow--;
                    continue;
                } else {
                    freeCell[0] = linesWin[i][j][0];
                    freeCell[1] = linesWin[i][j][1];
                }
            }
            if (winRow == winLine - 1) {
                return freeCell;
            }
        }
        //���� �� ����� �� ������� � �����, �� ���������� ���� ��������
        freeCell[0] = -1;
        freeCell[1] = -1;
        return freeCell;
    }

    //��������� ����� ����� winLine+1 �� ������� min winLine-2 ������ �� � �������
    //�������� � ������ ��������� ������ �� ��� ��� ���������
    private int[] checkLinesFork(char flag) {
        int[] freeCell = new int[2];
        //������� ����� �� ���������� - �������� �� ����� ������
        if (linesFork == null) {
            freeCell[0] = -1;
            freeCell[1] = -1;
            return freeCell;
        }
        //������������ ������ ���� � ����������� �� ���� ��� ���� ���� ����� - ��� ���� ��� ����������
        char secondPlayerFlag;
        if (flag == HUMAN_FLAG) {
            secondPlayerFlag = AI_FLAG;
        } else {
            secondPlayerFlag = HUMAN_FLAG;
        }
        int forkRow;
        for (int i = 0; i < linesFork.length; i++) {
            forkRow = 0;
            for (int j = 0; j < linesFork[0].length; j++) {
                if (map[linesFork[i][j][0]][linesFork[i][j][1]] == flag) {
                    if (j > 0 && j < winLine) {
                        forkRow++;
                    }
                } else if (map[linesFork[i][j][0]][linesFork[i][j][1]] == secondPlayerFlag) {
                    forkRow--;
                    continue;
                } else {
                    if (j > 0 && j < winLine) {
                        freeCell[0] = linesFork[i][j][0];
                        freeCell[1] = linesFork[i][j][1];
                    }
                }
            }
            if (forkRow == winLine - 2) {
                return freeCell;
            }
        }
        freeCell[0] = -1;
        freeCell[1] = -1;
        return freeCell;
    }

    Random rnd = new Random();

    //���������� ��������� ������ ������
    private int[] getRandomVoidCell() {
        int[] voidCell = {-1, -1};
        int voidCellCount = 0;
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[0].length; j++) {
                if (map[i][j] == VOID_CELL) {
                    voidCellCount++;
                }
            }
        }

        int[][] voidCells = new int[voidCellCount][2];
        int k = 0;
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[0].length; j++) {
                if (map[i][j] == VOID_CELL) {
                    voidCells[k][0] = i;
                    voidCells[k][1] = j;
                    k++;
                }
            }
        }

        int rand = rnd.nextInt(voidCellCount);
        voidCell[0] = voidCells[rand][0];
        voidCell[1] = voidCells[rand][1];
        return voidCell;
    }
}
