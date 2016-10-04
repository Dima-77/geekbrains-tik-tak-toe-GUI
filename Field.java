import java.util.Random;

/**
 * Created by Дима on 07.08.2016.
 */
public class Field {
    private char[][] map;
    int winLine;
    static final char VOID_CELL = '*';
    //Теперь названия флагов не относятся к типу игрока (комп или чел)
    //это просто два разных типа флага
    static final char HUMAN_FLAG = 'X';
    static final char AI_FLAG = 'O';
    private int[][][] linesWin = null;
    private int[][][] linesFork = null;

    public Field(int lines, int winLine) {
        map = new char[lines][lines];
        this.winLine = winLine;
        initMap();
        if (winLine <= lines) {  //Длина выигышной линии не должна превышать размер поля
            linesWin = getLines(winLine).clone();
        }
        if (winLine < lines) {
            //Вилка на линии может появиться только,
            //если длина выигрышной линии меньше размера поля
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
        //Получить ячейку для победы
        int nextWin[] = checkNextWin(myAIFlag).clone();
        //Получить ячейку для блокировки, если в след. ходу ожидается победа противника
        int nextEnemyWin[] = checkNextWin(enemyFlag).clone();
        //Получить ячейку для установки или блокировки, если в след. ходу ожидается вилка на линии
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
        } else {    //Переделать рандом так чтобы он выбирал только из пустых ячеек
            voidCell = getRandomVoidCell().clone();
            x = voidCell[0];
            y = voidCell[1];
        }
        setFlag(x, y, myAIFlag);
    }

    boolean checkWin(char flag) {
        //Проверка линий длины winLine
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

    //Выдаёт массив всех линий с координатами их ячеек для различных проверок
    private int[][][] getLines(int length) {
        int[][][] lines;
        int offset = map.length - length + 1; //Число сублиний в столбце или строке или главной диагонали
        //Число диагональных сублиний в одном направлении
        int diagonalLines = offset;
        for (int i = 1; i < offset; i++) {
            diagonalLines += 2 * (offset - i);
        }
        int numOfLine = 2 * offset * map.length + 2 * diagonalLines;
        lines = new int[numOfLine][length][2];
        numOfLine = 0;
        for (int i = 0; i < offset; i++) {   //Смещение начала линии
            for (int j = 0; j < map.length; j++) {   //Пробег по игровому полю
                lines[numOfLine] = getLine(j, i, 0, 1, length).clone();    //Горизонтальная линия
                lines[numOfLine + 1] = getLine(i, j, 1, 0, length).clone();  //Вертикальная линия
                numOfLine += 2;   //Две линии найдены
            }
        }   //Вертикальные и горизонтальные линии найдены
        for (int i = 0; i < offset; i++) {   //Смещение по горизонтали
            for (int k = 0; k < offset; k++) {   //Смещение по вертикали
                lines[numOfLine] = getLine(i, k, 1, 1, length).clone();    //Сверху слева
                lines[numOfLine + 1] = getLine(i, map.length - 1 - k, 1, -1, length).clone();    //Сверху справа
                numOfLine += 2;
            }
        }   //Диагонали найдены
        return lines;
    }

    private int[][] getLine(int cx, int cy, int vx, int vy, int l) {
        int[][] line = new int[l][2];  //Координаты ячеек линии
        for (int i = 0; i < l; i++) {
            line[i][0] = cx + i * vx;
            line[i][1] = cy + i * vy;
        }
        return line;
    }

    //Проверяет линии длины winLine на наличие winLine-1 флагов и
    //выдает свободную ячейку на ней для установки блокирования, победы
    private int[] checkNextWin(char flag) {
        int[] freeCell = new int[2];
        //Массива линий не существует - проверка не имеет смысла
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
        //Если не вышли из функции в цикле, то координаты надо очистить
        freeCell[0] = -1;
        freeCell[1] = -1;
        return freeCell;
    }

    //Проверяет линии длины winLine+1 на наличие min winLine-2 флагов не в крайних
    //позициях и выдает свободную ячейку на ней для установки
    private int[] checkLinesFork(char flag) {
        int[] freeCell = new int[2];
        //Массива линий не существует - проверка не имеет смысла
        if (linesFork == null) {
            freeCell[0] = -1;
            freeCell[1] = -1;
            return freeCell;
        }
        //Устанвливаем второй флаг в зависимости от того для кого ищем вилку - для себя или противника
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

    //Возвращает случайную пустую ячейку
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
