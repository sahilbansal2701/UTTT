package edu.brown.cs.student.UTTT;

import edu.brown.cs.student.Game.Game;
import edu.brown.cs.student.Game.IllegalMoveException;
import edu.brown.cs.student.Game.Status;
import edu.brown.cs.student.Pair.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * The Ultimate Tic Tac Toe class that contains the logic for the UTTT game.
 * Implements the game interface.
 */
public class UltimateTicTacToe implements Game<Pair<Integer[][], Integer>, Pair<Integer, Integer>> {
  // Added due to magic number checkstyle.
  private static final int SEVEN = 7;
  private static final int EIGHT = 8;
  private static final int NINE = 9;
  private static final int TEN = 10;
  private static final int TWELVE = 12;
  private static final int TWENTYTHREE = 23;
  private static final double WINP1VALUE = 10000000000000000000000.0;
  private static final double WINP2VALUE = -10000000000000000000000.0;
  private static final double THREEINROWVALUE = 10000000000000000.0;
  private static final double TWOINROWNEXTMOVEMINEVALUE = 100000000000.0;
  private static final double TWOINROWVALUE = 100.0;

  /*
    Our gameBoard is represented as a pair of an integer matrix and an integer.
    gameBoard[i] refers to which small tic tac toe board we are working with.
    The first 9 are the small boards (indices 0-8), the last one (index 9) is the big board.
    gameBoard[i][j] refers to a specific square on a specific board.
    gameBoard[i][j]=0 means that the spot is empty.
    gameBoard[i][j]=1 means that player 1's token is there.
    gameBoard[i][j]=-1 means that player 2's token is there.
    The integer of the gameBoard represents, which board can currently be played on.
      0-8 represents which specific little board can be played on.
      9 represents that any little board can be played on.
    A move is represented as a pair of integers. The first integer represents what board we can
      play on: 0-8 represents which specific little board can be played on.
      The second integer represents which space on the specific little board to play on.
   */

  private Pair<Pair<Integer[][], Integer>, Status> currentState;

  /**
   * Ultimate TicTacToe Game Constructor.
   */
  public UltimateTicTacToe() {
    // Make a clean array full of 0s.
    Integer[][] temp = new Integer[TEN][NINE];
    for (int i = 0; i < TEN; i++) {
      Arrays.fill(temp[i], 0);
    }
    currentState = new Pair<>(new Pair<>(temp, NINE), Status.ONGOINGP1);
  }
  @Override
  public void reset() {
    // Make a clean array full of 0s.
    Integer[][] temp = new Integer[TEN][NINE];
    for (int i = 0; i < TEN; i++) {
      Arrays.fill(temp[i], 0);
    }
    currentState = new Pair<>(new Pair<>(temp, NINE), Status.ONGOINGP1);
  }
  @Override
  public void updateState(Pair<Integer, Integer> move) throws IllegalMoveException {
    currentState = nextState(currentState, move);
  }

  /**
   * Helper function that makes a deep copy of a 2D integer matrix used in the nextState method.
   * @param input - The 2D integer matrix to make a deep copy of.
   * @return - Returns a copy of the passed in 2D integer matrix.
   */
  private Integer[][] makeDeepCopy(Integer[][] input) {
    Integer[][] output = new Integer[input.length][];

    for (int i = 0; i < input.length; i++) {
      output[i] = Arrays.copyOf(input[i], input[i].length);
    }
    return output;
  }
  @Override
  public Pair<Pair<Integer[][], Integer>, Status> nextState(
      Pair<Pair<Integer[][], Integer>, Status> oldState, Pair<Integer, Integer> move)
      throws IllegalMoveException {
    if (!legalMoves(oldState).contains(move)) {
      throw new IllegalMoveException("Invalid Move For Next State.");
    }
    Status status = oldState.getRight();
    Integer[][] bigBoard = makeDeepCopy(oldState.getLeft().getLeft());
    Integer whichSmallBoard = move.getLeft();
    Integer smallSquareSmallBoard = move.getRight();

    // Check what the current status is.
    if (status == Status.ONGOINGP1) {
      bigBoard[whichSmallBoard][smallSquareSmallBoard] = 1;
      if (checkThreeInRow(bigBoard[whichSmallBoard]) == 1) {
        // Setting the big bigBoard as we found 3 in a row.
        bigBoard[NINE][whichSmallBoard] = 1;
        if (checkThreeInRow(bigBoard[NINE]) == 1) {
          // If three in a row on the big board: player 1 wins.
          return new Pair<>(new Pair<>(bigBoard, -1), Status.WINP1);
        }
      }
      // If setting the big board results in a draw: game ends in draw.
      if (checkDrawBigBoard(bigBoard)) {
        return new Pair<>(new Pair<>(bigBoard, -1), Status.DRAW);
      }
      if (checkDraw(bigBoard[smallSquareSmallBoard])) {
        // If the next move's board is a draw.
        // Next player can make a move on any (valid) little board.
        return new Pair<>(new Pair<>(bigBoard, NINE), Status.ONGOINGP2);
      }
      if (checkThreeInRow(bigBoard[smallSquareSmallBoard]) != 0) {
        // If the next move's board has three in a row.
        // Next player can make a move on any (valid) little board.
        return new Pair<>(new Pair<>(bigBoard, NINE), Status.ONGOINGP2);
      }
      return new Pair<>(new Pair<>(bigBoard, smallSquareSmallBoard), Status.ONGOINGP2);

    } else if (status == Status.ONGOINGP2) {
      bigBoard[whichSmallBoard][smallSquareSmallBoard] = -1;
      if (checkThreeInRow(bigBoard[whichSmallBoard]) == -1) {
        // Setting the big bigBoard as we found 3 in a row.
        bigBoard[NINE][whichSmallBoard] = -1;
        if (checkThreeInRow(bigBoard[NINE]) == -1) {
          // If three in a row on the big board: player 2 wins.
          return new Pair<>(new Pair<>(bigBoard, -1), Status.WINP2);
        }
      }
      // If setting the big board results in a draw: game ends in a draw.
      if (checkDrawBigBoard(bigBoard)) {
        return new Pair<>(new Pair<>(bigBoard, -1), Status.DRAW);
      }
      if (checkDraw(bigBoard[smallSquareSmallBoard])) {
        // If the next move's board is a draw.
        // Next player can make a move on any (valid) little board.
        return new Pair<>(new Pair<>(bigBoard, NINE), Status.ONGOINGP1);
      }
      if (checkThreeInRow(bigBoard[smallSquareSmallBoard]) != 0) {
        // If the next move's board has three in a row.
        // Next player can make a move on any (valid) little board.
        return new Pair<>(new Pair<>(bigBoard, NINE), Status.ONGOINGP1);
      }
      return new Pair<>(new Pair<>(bigBoard, smallSquareSmallBoard), Status.ONGOINGP1);
    } else {
      // If the status of the game is not "ONGOING".
      return new Pair<>(new Pair<>(bigBoard, oldState.getLeft().getRight()), status);
    }
  }

  @Override
  public Pair<Pair<Integer[][], Integer>, Status> getState() {
    return currentState;
  }

  @Override
  public List<Pair<Integer, Integer>> legalMoves(Pair<Pair<Integer[][], Integer>, Status> state) {
    List<Pair<Integer, Integer>> legalMoves = new ArrayList<>();
    int boardToPlayOn = state.getLeft().getRight();
    Status status = state.getRight();
    if ((status == Status.WINP1) || (status == Status.DRAW) || (status == Status.WINP2)) {
      return legalMoves;
    }
    if (boardToPlayOn == NINE) {
      // We can play on any of the little boards.
      for (int i = 0; i < NINE; i++) {
        // for all the small boards
        Integer[] smallBoard = state.getLeft().getLeft()[i];
        if (checkThreeInRow(smallBoard) == 0) {
          for (int j = 0; j < NINE; j++) {
            // for each space in the small board
            if (smallBoard[j] == 0) {
              // if the space is empty it is a legal move.
              legalMoves.add(new Pair<>(i, j));
            }
          }
        }
      }
    } else {
      for (int i = 0; i < NINE; i++) {
        Integer[] smallBoard = state.getLeft().getLeft()[boardToPlayOn];
        // for each space in the small board
        if (smallBoard[i] == 0) {
          // if the space is empty it is a legal move.
          legalMoves.add(new Pair<>(boardToPlayOn, i));
        }
      }
    }
    return legalMoves;
  }

  @Override
  public Double estimateValue(Pair<Pair<Integer[][], Integer>, Status> state) {
    Integer[][] board = state.getLeft().getLeft();
    Status status = state.getRight();
    if (status == Status.DRAW) {
      return 0.0;
    } else if (status == Status.WINP1) {
      return WINP1VALUE;
    } else if (status == Status.WINP2) {
      return WINP2VALUE;
    } else {
      double sum = 0.0;
      for (int i = 0; i < TEN; i++) {
        int threeInARow = checkThreeInRow(board[i]);
        if (threeInARow == 1) {
          sum += THREEINROWVALUE;
        } else if (threeInARow == -1) {
          sum -= THREEINROWVALUE;
        } else {
          double add = checkTwoInRow(board[i], 1);
          double subtract = checkTwoInRow(board[i], -1);
          if ((Double.compare(add, 0.0) != 0)
                  && (status == Status.ONGOINGP1)
                  && ((state.getLeft().getRight() == NINE) || (i == state.getLeft().getRight()))) {
            sum += TWOINROWNEXTMOVEMINEVALUE;
            sum -= subtract;
          } else if ((Double.compare(subtract, 0.0) != 0)
                  && (status == Status.ONGOINGP2)
                  && ((state.getLeft().getRight() == NINE) || (i == state.getLeft().getRight()))) {
            sum -= TWOINROWNEXTMOVEMINEVALUE;
            sum += add;
          } else {
            sum += add;
            sum -= subtract;
          }
        }
      }
      return sum;
    }
  }

  // String should be formatted as: int int (with the space)
  //    to be a valid move.
  // ex) if you want to move in top right corner of top right board: 2 2
  // ex) if you want top left corner of middle board: 4 0
  @Override
  public Pair<Integer, Integer> stringToMove(String inputMove) throws IllegalMoveException {
    String[] makeMove = inputMove.split(" ");
    if (makeMove.length != 2) {
      throw new IllegalMoveException("Invalid Move");
    }
    try {
      int whichBoard = Integer.parseInt(makeMove[0]);
      int grid = Integer.parseInt(makeMove[1]);
      return new Pair<>(whichBoard, grid);
    } catch (NumberFormatException e) {
      throw new IllegalMoveException("Invalid Move");
    }
  }

  @Override
  public String stringOfCurrentState() {
    return stringOfState(currentState);
  }

  @Override
  public String stringOfState(Pair<Pair<Integer[][], Integer>, Status> state) {
    Integer[][] board = state.getLeft().getLeft();
    String build = "\n Little Boards: \n";
    for (int n = 0; n < SEVEN; n += 3) {
      for (int i = 1; i < 4; i++) {
        build = build.concat(printRow(board[n], i) + "\033[0;35m | \033[0m"
            + printRow(board[n + 1], i)
            + "\033[0;35m | \033[0m" + printRow(board[n + 2], i) + "\n");
        if (i != 3) {
          build = build.concat(
              "---|---|---\033[0;35m | \033[0m---|---|---\033[0;35m | \033[0m---|---|---" + "\n");
        }
      }
      if (n != 6) {
        build = build.concat("\033[0;35m------------|-------------|------------\033[0m" + "\n");
      }
    }
    build = build.concat("\n Big Board: \n");
    build = build.concat(printTTTBoard(board[NINE]));
    return build;
  }

  /**
   * A helper method that prints a row of the passed in TicTacToe board.
   * @param grid - TicTacToe board represented as an array to check.
   * @param row - Row that is to be printed of the TicTacToe board. 1 or 2 or 3
   * @return - Return a string of the row of the TicTacToe board that is to be printed.
   */
  private String printRow(Integer[] grid, int row) {
    String build = "";
    int i = -1;
    if (row == 1) {
      i = 0;
    } else if (row == 2) {
      i = 3;
    } else if (row == 3) {
      i = 6;
    }
    for (int j = i; j < i + 3; j++) {
      if (grid[j] == 1) {
        build = build.concat(" X |");
      } else if (grid[j] == -1) {
        build = build.concat(" O |");
      } else {
        build = build.concat("   |");
      }
    }
    build = build.substring(0, build.length() - 1);
    return build;
  }

  /**
   * Method to print a TicTacToe board in its entirety.
   * @param board - TicTacToe board represented as an array to print.
   * @return - Return a string that visually represents a TicTacToe board.
   */
  private String printTTTBoard(Integer[] board) {
    String build = "\n";
    for (int i = 0; i < SEVEN; i += 3) {
      for (int j = 0; j < 3; j++) {
        if (board[i + j] == 1) {
          build = build.concat(" X \033[0;35m|\033[0m");
        } else if (board[i + j] == -1) {
          build = build.concat(" O \033[0;35m|\033[0m");
        } else {
          build = build.concat("   \033[0;35m|\033[0m");
        }
      }

      build = build.substring(0, build.length() - TWELVE);

      build = build.concat("\n");

      build = build.concat("\033[0;35m---|---|---\033[0m" + "\n");

    }
    build = build.substring(0, build.length() - TWENTYTHREE);
    return build;
  }

  /**
   * Checks for three in a row.
   * @param grid - TicTacToe board represented as an array to check.
   * @return - Returns token of player who has 3 in a row if there is one,
   *       else returns 0.
   */
  private int checkThreeInRow(Integer[] grid) {
    if (grid[0].equals(grid[1]) && grid[0].equals(grid[2]) && !grid[0].equals(0)) {
      // top row is all non-empty
      return grid[0];
    } else if (grid[3].equals(grid[4]) && grid[3].equals(grid[5]) && !grid[3].equals(0)) {
      // middle row is all non-empty
      return grid[3];
    } else if (grid[6].equals(grid[SEVEN]) && grid[6].equals(grid[EIGHT]) && !grid[6].equals(0)) {
      // bottom row is all non-empty
      return grid[6];
    } else if (grid[0].equals(grid[3]) && grid[0].equals(grid[6]) && !grid[0].equals(0)) {
      // left column is all non-empty
      return grid[0];
    }  else if (grid[1].equals(grid[4]) && grid[1].equals(grid[SEVEN]) && !grid[1].equals(0)) {
      // middle column is all non-empty
      return grid[1];
    } else if (grid[2].equals(grid[5]) && grid[2].equals(grid[EIGHT]) && !grid[2].equals(0)) {
      // right column is all non-empty
      return grid[2];
    } else if (grid[0].equals(grid[4]) && grid[0].equals(grid[EIGHT]) && !grid[0].equals(0)) {
      // negative sloped diagonal is all non-empty
      return grid[0];
    }   else if (grid[2].equals(grid[4]) && grid[2].equals(grid[6]) && !grid[2].equals(0)) {
      // positively sloped diagonal is all non-empty
      return grid[2];
    } else {
      return 0;
    }
  }

  /**
   * Checks whether or not a 3x3 grid is a draw (all moves filled, but no winner).
   * @param grid - TicTacToe board represented as an array to check.
   * @return - Returns a boolean whether or not the passed in grid is a draw.
   *    assumes that win condition checked before calling this method.
   */
  private boolean checkDraw(Integer[] grid) {
    boolean draw = true;
    for (int i = 0; i < NINE; i++) {
      if (grid[i] == 0) {
        draw = false;
        break;
      }
    }
    return draw;
  }

  /**
   * Checks for whether or not the big board of the uttt game is at a draw.
   * @param board - The uttt board.
   * @return - Returns a boolean representing whether or not the big board is a draw.
   *    Assumes checked win condition previously
   */
  private boolean checkDrawBigBoard(Integer[][] board) {
    boolean draw = true;
    // If the big board is a draw: game is a draw.
    if (checkDraw(board[NINE])) {
      return true;
    }
    // If each small board is a draw or win, the game ends in a draw.

    for (int i = 0; i < NINE; i++) {
      if (board[NINE][i] == 0) {
        if (!checkDraw(board[i])) {
          draw = false;
          break;
        }
      }
    }
    return draw;
  }

  /**
   * Checks whether the passed in TicTacToe board (grid) has two in a row for estimate value.
   * @param grid - TicTacToe board represented as an array to check.
   * @param token - Token (-1 or 1) of the player that is being checked for two in a row.
   * @return - Return a double scoring whether or not the passed in token (player) is at an
   *       advantage (2 in a row) or not.
   */
  private Double checkTwoInRow(Integer[] grid, Integer token) {
    double sum = 0.0;
    if (Arrays.equals(new Integer[]{grid[0], grid[1], grid[2]},
           new Integer[]{0, token, token})
        || Arrays.equals(new Integer[]{grid[0], grid[1], grid[2]},
           new Integer[]{token, 0, token})
        || Arrays.equals(new Integer[]{grid[0], grid[1], grid[2]},
           new Integer[]{token, token, 0})) {
      // check top row
      sum += TWOINROWVALUE;
    }
    if (Arrays.equals(new Integer[]{grid[3], grid[4], grid[5]},
           new Integer[]{0, token, token})
        || Arrays.equals(new Integer[]{grid[3], grid[4], grid[5]},
           new Integer[]{token, 0, token})
        || Arrays.equals(new Integer[]{grid[3], grid[4], grid[5]},
           new Integer[]{token, token, 0})) {
      // check middle row
      sum += TWOINROWVALUE;
    }
    if (Arrays.equals(new Integer[]{grid[6], grid[SEVEN], grid[EIGHT]},
           new Integer[]{0, token, token})
        || Arrays.equals(new Integer[]{grid[6], grid[SEVEN], grid[EIGHT]},
           new Integer[]{token, 0, token})
        || Arrays.equals(new Integer[]{grid[6], grid[SEVEN], grid[EIGHT]},
           new Integer[]{token, token, 0})) {
      // check bottom row
      sum += TWOINROWVALUE;
    }
    if (Arrays.equals(new Integer[]{grid[0], grid[3], grid[6]},
           new Integer[]{0, token, token})
        || Arrays.equals(new Integer[]{grid[0], grid[3], grid[6]},
           new Integer[]{token, 0, token})
        || Arrays.equals(new Integer[]{grid[0], grid[3], grid[6]},
           new Integer[]{token, token, 0})) {
      // check left column
      sum += TWOINROWVALUE;
    }
    if (Arrays.equals(new Integer[]{grid[1], grid[4], grid[SEVEN]},
           new Integer[]{0, token, token})
        || Arrays.equals(new Integer[]{grid[1], grid[4], grid[SEVEN]},
           new Integer[]{token, 0, token})
        || Arrays.equals(new Integer[]{grid[1], grid[4], grid[SEVEN]},
           new Integer[]{token, token, 0})) {
      // check middle column
      sum += TWOINROWVALUE;
    }
    if (Arrays.equals(new Integer[]{grid[2], grid[5], grid[EIGHT]},
           new Integer[]{0, token, token})
        || Arrays.equals(new Integer[]{grid[2], grid[5], grid[EIGHT]},
           new Integer[]{token, 0, token})
        || Arrays.equals(new Integer[]{grid[2], grid[5], grid[EIGHT]},
           new Integer[]{token, token, 0})) {
      // check right column
      sum += TWOINROWVALUE;
    }
    if (Arrays.equals(new Integer[]{grid[0], grid[4], grid[EIGHT]},
           new Integer[]{0, token, token})
        || Arrays.equals(new Integer[]{grid[0], grid[4], grid[EIGHT]},
           new Integer[]{token, 0, token})
        || Arrays.equals(new Integer[]{grid[0], grid[4], grid[EIGHT]},
           new Integer[]{token, token, 0})) {
      // check negative sloped diagonal
      sum += TWOINROWVALUE;
    }
    if (Arrays.equals(new Integer[]{grid[2], grid[4], grid[6]},
           new Integer[]{0, token, token})
        || Arrays.equals(new Integer[]{grid[2], grid[4], grid[6]},
           new Integer[]{token, 0, token})
        || Arrays.equals(new Integer[]{grid[2], grid[4], grid[6]},
           new Integer[]{token, token, 0})) {
      // check positively sloped diagonal
      sum += TWOINROWVALUE;
    }
    return sum;
  }
}
