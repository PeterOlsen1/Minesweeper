//written by Peter Olsen, olse0321

//Import Section
import org.w3c.dom.ls.LSOutput;

import java.util.Random;
import java.util.Scanner;

/*
 * Provided in this class is the neccessary code to get started with your game's implementation
 * You will find a while loop that should take your minefield's gameOver() method as its conditional
 * Then you will prompt the user with input and manipulate the data as before in project 2
 * 
 * Things to Note:
 * 1. Think back to project 1 when we asked our user to give a shape. In this project we will be asking the user to provide a mode. Then create a minefield accordingly
 * 2. You must implement a way to check if we are playing in debug mode or not.
 * 3. When working inside your while loop think about what happens each turn. We get input, user our methods, check their return values. repeat.
 * 4. Once while loop is complete figure out how to determine if the user won or lost. Print appropriate statement.
 */

public class main {
    private static Minefield minefield;

    public static void main(String[] args){


        System.out.print("Select a difficulty, type 'help' for a list of different difficulties: ");
        Scanner s = new Scanner(System.in);
        String inp = s.nextLine();
        if (inp.equals("help")){
            System.out.println();
            System.out.println("There are three difficulties: easy, medium, and hard");
            System.out.println("Easy: 5x5 grid 5 mines and 5 flags");
            System.out.println("Medium: 9x9 grid with 12 mines 12 flags");
            System.out.println("Hard: 20x20 grid with 40 mines and 40 flags");
            System.out.println();
            System.out.print("Enter a difficulty: ");
            inp = s.nextLine();
        }

        int mines;
        int dimensions;

        //loop to set the game mode, ensure that the user picks one of the specified ones
        while (true){
            if (inp.equals("easy") || inp.equals("Easy")){
                minefield = new Minefield(5, 5, 5);
                mines = 5;
                dimensions = 5;
                break;
            }
            else if (inp.equals("medium") || inp.equals("Medium")){
                minefield = new Minefield(9, 9, 12);
                mines = 12;
                dimensions = 9;
                break;
            }
            else if (inp.equals("hard") || inp.equals("Hard")){
                minefield = new Minefield(20, 20, 40);
                mines = 40;
                dimensions = 20;
                break;
            }

            //the user did not enter any of the game modes, so they must input again
            System.out.print("Invalid input, please choose either easy, medium, or hard difficulty: ");
            inp = s.nextLine();
        }

        int flags = mines;

        //prompt user to read the rules
        System.out.print("If you would like to read the rules, type 'help', otherwise skip this input ");
        String help = s.nextLine();
        if (help.equals("help") || help.equals("Help")){
            //instructions
            System.out.println();
            System.out.println("In this game of minesweeper, there is a " + dimensions + "x" + dimensions + " board with " + mines + " mines");
            System.out.println("You are given " + mines + " flags, and the main goal in this game is to cover all mines with flags or uncover all non-mine tiles. A flag cannot be removed after it is placed");
            System.out.println("A flag cannot be placed on the first move, since the board is created in a way that a mine will never be placed in your starting area");
            System.out.println("Each square of the board will either contain a mine, a number representing the number of mines in the 8 surrounding tiles, or a user placed flag" + "\n");
            System.out.println("The board will display the numbered rows and columns, starting at (0, 0) in the top left and increasing as you move to the bottom right");
            System.out.println("Each move will take 3 inputs: row, col, and flag");
            System.out.println("You are technically allowed to move on places you have already uncovered, but that serves no purpose (unless you really want to put a flag there for some reason)");
            System.out.println("row and col will both represent the row and column coordinates of the move, and flag will be a boolean representing whether or not you want to place a flag ");
            System.out.println("Flag will be represented as t or f, (true / false) with t representing placing a flag, and f representing no flag");
            System.out.println("If you have no flags left but choose to play a flag, the game will make your move, but with no flag" + '\n');
            System.out.println("You can only input integers for the row/col values, and not other data types such as 'b' or 0.0. This will cause the program to error");
            System.out.println("For example, a move would look like the following: 4 3 false, or 4 3 f");
            System.out.println("If you wish to play in debug mode, input 3 entries instead of 2 on your first move, as such: 4 3 d");
            System.out.println();
        }

        //gather the first move
        System.out.print("Enter your first move (row col): ");
        inp = s.nextLine();

        //input entered is not properly formatted, while loop has condition length < 2 since a guess x y must be at least length 3
        while (!inp.contains(" ") || inp.split(" ").length < 2){
            System.out.print("Enter a properly formatted guess (row col): ");
            inp = s.nextLine();
        }

        //split the proper input and get x y
        String[] temp = inp.split(" ");
        int x = Integer.parseInt(temp[0]);
        int y = Integer.parseInt(temp[1]);
        String f;

        //while loop will be entered if the guess is out of bounds
        while (x < 0 || x >= dimensions || y < 0 || y >= dimensions){
            System.out.println("Please enter an in bounds guess");
            System.out.print("Enter a new move (row col): ");
            inp = s.nextLine();

            //even if an input was out of bounds, it might also still be improperly formatted. Make sure it is correct
            while (!inp.contains(" ") || inp.split(" ").length < 2){
                System.out.println("You must have two values separated by a space in your guess");
                System.out.print("Enter a new guess (row col): ");
                inp = s.nextLine();
            }

            //the guess is properly formatted, update x y
            temp = inp.split(" ");
            x = Integer.parseInt(temp[0]);
            y = Integer.parseInt(temp[1]);
        }

        //if they entered an extra character, enter debug mode
        boolean debug = false;
        if (temp.length == 3) {
            debug = true;
        }

        //create the mines and numbers
        minefield.createMines(x, y, mines);
        minefield.evaluateField();
        minefield.revealStartingArea(x, y);

        if (debug) minefield.debug();
        else System.out.println(minefield);

        while( ! minefield.gameOver() ){

            System.out.println("You have " + flags + " flags left.");
            System.out.print("Enter your next move (row col t/f): ");
            inp = s.nextLine();

            //entered only if input structure is incorrect
            //necessary inp length of 5 is chosen since moves are in the form x y z, which has a min of 5 characters
            while (inp.length() < 5 || !inp.contains(" ") || inp.split(" ").length < 3){
                System.out.print("Please enter a valid input (row col t/f): ");
                inp = s.nextLine();
            }

            //input structure is correct, set x, y, and f
            temp = inp.split(" ");
            x = Integer.parseInt(temp[0]);
            y = Integer.parseInt(temp[1]);
            f = temp[2];

            //enter this block if input entered is out of bounds
            while (x < 0 || x >= dimensions || y < 0 || y >= dimensions){
                System.out.println("Please enter an in bounds and properly formatted guess");
                System.out.print("Enter a new move (x y t/f): ");
                inp = s.nextLine();

                //ensure that this new input is formatted properly
                if (inp.length() >= 5 && inp.contains(" ") && inp.split(" ").length >= 3) {
                    temp = inp.split(" ");
                    x = Integer.parseInt(temp[0]);
                    y = Integer.parseInt(temp[1]);
                    f = temp[2];
                }
            }

            //flag will automatically be set to false, change if it's true
            boolean flag = false;
            if (f.equals("t") || f.equals("true") || f.equals("T") || f.equals("True")){
                flag = true;
                flags--;
            }

            //no flags left
            if (flag && flags < 0){
                System.out.println("You have played a flag but have no flags left. A normal move with no flag will be played on your specified spot.");
                flag = false;
                flags = 0;
            }

            //guess returns true if you hit a mine with no flag
            if (minefield.guess(x, y, flag)){
                System.out.println(minefield);
                System.out.println("You hit a mine!" + "\n" + "You lose!");
                return;
            }

            //check if no mines are left after each move where a flag is placed
            if (flag && minefield.noMinesLeft()) {
                System.out.println(minefield);
                System.out.println("You win!");
                return;
            }

            if (debug) minefield.debug();
            else System.out.println(minefield);
        }
        //the game is won by uncovering all non-mine cells
        System.out.println("You uncovered all non-mine tiles! You win!");
    }
    
}
