//written by Peter Olsen

import java.util.Queue;
import java.util.Random;

public class Minefield {
    /**
    Global Section
    */
    public static final String ANSI_YELLOW_BRIGHT = "\u001B[33;1m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE_BRIGHT = "\u001b[34;1m";
    public static final String ANSI_BLUE = "\u001b[34m";
    public static final String ANSI_RED_BRIGHT = "\u001b[31;1m";
    public static final String ANSI_RED = "\u001b[31m";
    public static final String ANSI_GREEN = "\u001b[32m";
    public static final String ANSI_PURPLE = "\u001b[35m";
    public static final String ANSI_CYAN = "\u001b[36m";
    public static final String ANSI_WHITE_BACKGROUND = "\u001b[47m";
    public static final String ANSI_PURPLE_BACKGROUND = "\u001b[45m";
    public static final String ANSI_GREY_BACKGROUND = "\u001b[0m";

    /* 
     * Class Variable Section
    */
    private Cell[][] board;
    private int mines;
    private int flags;

    /*Things to Note:
     * Please review ALL files given before attempting to write these functions.
     * Understand the Cell.java class to know what object our array contains and what methods you can utilize
     * Understand the StackGen.java class to know what type of stack you will be working with and methods you can utilize
     * Understand the QGen.java class to know what type of queue you will be working with and methods you can utilize
     */
    
    /**
     * Minefield
     * 
     * Build a 2-d Cell array representing your minefield.
     * Constructor
     * @param rows       Number of rows.
     * @param columns    Number of columns.
     * @param flags      Number of flags, should be equal to mines
     */
    public Minefield(int rows, int columns, int flags1) {
        board = new Cell[rows][columns];
        mines = flags;
        flags = flags1;
    }

    /**
     * evaluateField
     * 
     *
     * @function:
     * Evaluate entire array.
     * When a mine is found check the surrounding adjacent tiles. If another mine is found during this check, increment adjacent cells status by 1.
     * 
     */
    public void evaluateField() {
        for (int i = 0; i < board.length; i++){
            for (int j = 0; j < board.length; j++){

                //create two arrays that we will use to check all spots around (i, j)
                int[] iArr = {i-1, i, i+1};
                int[] jArr = {j-1, j, j+1};

                //adjust iArray if it is out of bounds
                if (iArr[0] < 0) {
                    iArr = new int[] {i, i+1};
                }
                else if (iArr[2] >= board.length){
                    iArr = new int[] {i-1, i};
                }

                //adjust jArray if it is out of bounds
                if (jArr[0] < 0) {
                    jArr = new int[] {j, j+1};
                }
                else if (jArr[2] >= board.length){
                    jArr = new int[] {j-1, j};
                }
                //new variable to count the mines around a spot
                int mineCount = 0;

                for (int p : iArr) {
                    for (int k : jArr) {
                        //if it is a mine, increment count
                        if (board[p][k].getStatus().equals("M")) mineCount++;
                    }
                }
                //we don't want to change the status if it is a mine
                if (!board[i][j].getStatus().equals("M")) board[i][j].setStatus(mineCount + "");
            }
        }
    }

    /**
     * createMines
     * 
     * Randomly generate coordinates for possible mine locations.
     * If the coordinate has not already been generated and is not equal to the starting cell set the cell to be a mine.
     * utilize rand.nextInt()
     * 
     * @param x       Start x, avoid placing on this square.
     * @param y        Start y, avoid placing on this square.
     * @param mines      Number of mines to place.
     */
    public void createMines(int x, int y, int mines) {
        Random r = new Random();

        for (int i = 0; i < mines; i++){
            //generate an array that will keep track of mine positions
            int[] mine = {r.nextInt(board.length), r.nextInt(board.length)};

            //this while loop ensures that our mine is not placed over another, and it is not in our starting position
            while ((mine[0] == x && mine[1] == y) || board[mine[0]][mine[1]] != null){
                mine[0] = r.nextInt(board.length);
                mine[1] = r.nextInt(board.length);
            }

            //create a mine
            board[mine[0]][mine[1]] = new Cell(false , "M");
        }
        for (int i = 0; i < board.length; i++){
            for (int j = 0; j < board.length; j++){
                //if the spot isn't a mine, set it to nothing
                if (board[i][j] == null) {
                    board[i][j] = new Cell(false, "0");
                }
            }
        }
    }

    /**
     * guess
     * 
     * Check if the guessed cell is inbounds (if not done in the Main class). 
     * Either place a flag on the designated cell if the flag boolean is true or clear it.
     * If the cell has a 0 call the revealZeroes() method or if the cell has a mine end the game.
     * At the end reveal the cell to the user.
     * 
     * 
     * @param x       The x value the user entered.
     * @param y       The y value the user entered.
     * @param flag    A boolean value that allows the user to place a flag on the corresponding square.
     * @return boolean Return false if guess did not hit mine or if flag was placed, true if mine found.
     */
    public boolean guess(int x, int y, boolean flag) {

        if (x > board.length || x < 0 || y < 0 || y > board[0].length) return false;

        if (flag) {
            board[x][y].setStatus("F");
        }

        //a mine was hit with no flag
        if (board[x][y].getStatus().equals("M")) {
            board[x][y].setRevealed(true);
            return true;
        }

        //found a 0, reveal 0s
        if (board[x][y].getStatus().equals("0")){
            revealZeroes(x, y);
        }

        board[x][y].setRevealed(true);

        return false;
    }

    /**
     * gameOver
     * 
     * Ways a game of Minesweeper ends:
     * 1. player guesses a cell with a mine: game over -> player loses
     * 2. player has revealed the last cell without revealing any mines -> player wins
     * 
     * @return boolean Return false if game is not over and squares have yet to be revealed, otheriwse return true.
     */
    public boolean gameOver() {

        for (int i = 0; i < board.length; i++){
            for (int j = 0; j < board.length; j++){
                //any square has not yet been revealed and is not a mine
                if (!board[i][j].getRevealed() && !board[i][j].getStatus().equals("M")) return false;
            }
        }
        //all have been revealed
        return true;
    }

    /**
     * A function created by me that checks to see how many mines are left that have not been flagged.
     * If there are no mines left, it will return true
     * @return boolean, True if no mines are left
     */
    public boolean noMinesLeft(){
        for (int i = 0; i < board.length; i++){
            for (int j = 0; j < board.length; j++){
                //any square left that is still a mine
                if (board[i][j].getStatus().equals("M")) return false;
            }
        }
        return true;
    }

    /**
     * Reveal the cells that contain zeroes that surround the inputted cell.
     * Continue revealing 0-cells in every direction until no more 0-cells are found in any direction.
     * Utilize a STACK to accomplish this.
     *
     * This method should follow the psuedocode given in the lab writeup.
     * Why might a stack be useful here rather than a queue?
     *
     * @param x      The x value the user entered.
     * @param y      The y value the user entered.
     */
    public void revealZeroes(int x, int y) {

        Stack1Gen<int[]> stack = new Stack1Gen<>();
        int[] start = {x, y};
        stack.push(start);

        while (!stack.isEmpty()){

            int[] temp = stack.pop();
            //if your spot is a 0, and it has not already been checked
            if (board[temp[0]][temp[1]].getStatus().equals("0") && !board[temp[0]][temp[1]].getRevealed()){
                //reveal the spot and push neighbors to the stack
                board[temp[0]][temp[1]].setRevealed(true);

                //create a new array full of possible spots to add to our stack
                int[][] newArr = new int[][] {{temp[0], temp[1] + 1}, {temp[0], temp[1] - 1}, {temp[0] - 1, temp[1]}, {temp[0] + 1, temp[1]}};

                for (int i = 0; i < 4; i++){
                    //for all spots in this new array, if they are not out of bounds, add them to the stack
                    if (!(newArr[i][0] < 0 || newArr[i][0] >= board.length || newArr[i][1] < 0 || newArr[i][1] >= board.length)){
                        stack.push(newArr[i]);
                    }
                }
            }

        }
    }

    /**
     * revealStartingArea
     *
     * On the starting move only reveal the neighboring cells of the inital cell and continue revealing the surrounding concealed cells until a mine is found.
     * Utilize a QUEUE to accomplish this.
     * 
     * This method should follow the psuedocode given in the lab writeup.
     * Why might a queue be useful for this function?
     *
     * @param x     The x value the user entered.
     * @param y     The y value the user entered.
     */
    public void revealStartingArea(int x, int y) {

        Q1Gen<int[]> q = new Q1Gen<>();
        int[] start = {x, y};
        q.add(start);

        while (q.length() != 0){
            int[] temp = q.remove();
            if (!board[temp[0]][temp[1]].getRevealed()){
                board[temp[0]][temp[1]].setRevealed(true);

                //if you found a mine, end the function
                if (board[temp[0]][temp[1]].getStatus().equals("M")) return;

                //same process for enqueueing items as the last method
                //create a new array full of possible spots to add to our queue
                int[][] newArr = new int[][] {{temp[0], temp[1] + 1}, {temp[0], temp[1] - 1}, {temp[0] - 1, temp[1]}, {temp[0] + 1, temp[1]}};

                for (int i = 0; i < 4; i++){
                    //for all spots in this new array, if they are not out of bounds, add them to the queue
                    if (!(newArr[i][0] < 0 || newArr[i][0] >= board.length || newArr[i][1] < 0 || newArr[i][1] >= board.length)){
                        q.add(newArr[i]);
                    }
                }
            }
        }
    }

    /**
     * For both printing methods utilize the ANSI colour codes provided!
     *
     * @function This method should print the entire minefield, regardless if the user has guessed a square.
     * *This method should print out when debug mode has been selected. 
     */
    public void debug() {
        //print the numbers on top
        System.out.print("  ");
        for (int k = 0; k < board.length; k++){
            System.out.print(k + " ");
        }
        //print for each row the number at the start, then the cells of the row
        System.out.println();
        for (int i = 0; i < board.length; i++){
            System.out.print(i + " ");
            for (int j = 0; j < board.length; j++){
                System.out.print(color(board[i][j].getStatus()) + " ");
            }
            System.out.print('\n');
        }
    }

    /**
     * toString
     *
     * @return String The string that is returned only has the squares that has been revealed to the user or that the user has guessed.
     */
    public String toString() {
        /*
        A note for toString: two spaces are added between all characters.
        This is because having >= 10 rows / columns causes the rows/columns to be disjoint from their contents,
        and using two spaces creates space for that to happen
         */
        String outString = "   ";
        //create numbers on the top
        for (int k = 0; k < board.length; k++){
            if (k >= 10) outString += k + " ";
            else outString += k + "  ";
        }
        outString += '\n';

        for (int i = 0; i < board.length; i++){
            //create numbers on the left side
            if (i >= 10) outString += i + " ";
            else outString += i + "  ";

            for (int j = 0; j < board.length; j++){
                //if you can see it, print it
                if (board[i][j].getRevealed()){
                    outString += color(board[i][j].getStatus()) + "  ";
                }
                //otherwise, print "-"
                else{
                    outString += "-" + "  ";
                }
            }
            outString += '\n';
        }
        return outString;
    }

    /**
     * This method was created by me in order to add color to the display board.
     *
     * @param s: string passed in, it will be returned with a color tag
     * @return: it is the same string as passed in but with color tags
     */
    public String color(String s){
        if (s.equals("M")) return ANSI_RED_BRIGHT + s + ANSI_GREY_BACKGROUND;
        else if (s.equals("F")) return ANSI_BLUE_BRIGHT + s + ANSI_GREY_BACKGROUND;

        //we were only given 9 non-background colors so there is repetition with M/3 and F/7
        else if (s.equals("0")) return ANSI_YELLOW + s + ANSI_GREY_BACKGROUND;
        else if (s.equals("1")) return ANSI_BLUE + s + ANSI_GREY_BACKGROUND;
        else if (s.equals("2")) return ANSI_GREEN + s + ANSI_GREY_BACKGROUND;
        else if (s.equals("3")) return ANSI_RED_BRIGHT + s + ANSI_GREY_BACKGROUND;
        else if (s.equals("4")) return ANSI_PURPLE + s + ANSI_GREY_BACKGROUND;
        else if (s.equals("5")) return ANSI_RED + s + ANSI_GREY_BACKGROUND;
        else if (s.equals("6")) return ANSI_YELLOW_BRIGHT + s + ANSI_GREY_BACKGROUND;
        else if (s.equals("7")) return ANSI_BLUE_BRIGHT + s + ANSI_GREY_BACKGROUND;
        else if (s.equals("8")) return ANSI_CYAN + s + ANSI_GREY_BACKGROUND;

        return s;
    }
}
