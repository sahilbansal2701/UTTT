package edu.brown.cs.student.TTT;

import edu.brown.cs.student.Game.Game;
import edu.brown.cs.student.Game.IllegalMoveException;
import edu.brown.cs.student.Game.Status;
import edu.brown.cs.student.Pair.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * TicTacToe game class (for testing purposes).
 */
public class TicTacToe implements Game<Integer[], Integer> {
  // Added due to magic number checkstyle.
  private static final int BOARDSIZE = 9;
  private static final double WINP1VALUE = 10000000000000000000.0;
  private static final double WINP2VALUE = -10000000000000000000.0;
  private static final double TWOINAROWVALUE = 100.0;
  private static final int SEVEN = 7;
  private static final int EIGHT = 8;
  private static final int TWELVE = 12;

  private Pair<Integer[], Status> currentState;

  /**
   * TicTacToe constructor.
   */
  public TicTacToe() {
    Integer[] temp = new Integer[BOARDSIZE];
    Arrays.fill(temp, 0);
    currentState = new Pair<>(temp, Status.ONGOINGP1);
  }

  @Override
  public void reset() {
    Integer[] temp = new Integer[BOARDSIZE];
    Arrays.fill(temp, 0);
    currentState = new Pair<>(temp, Status.ONGOINGP1);
  }

  // Method used to make a deep copy of an integer array
  private Integer[] makeDeepCopy(Integer[] input) {
    return Arrays.copyOf(input, input.length);
  }

  @Override
  public Pair<Integer[], Status> nextState(Pair<Integer[], Status> oldState, Integer move)
          throws IllegalMoveException {
    if (!legalMoves(oldState).contains(move)) {
      throw new IllegalMoveException("Invalid Move For Next State.");
    }
    Status status = oldState.getRight();
    Integer[] board = makeDeepCopy(oldState.getLeft());

    // Check what the current status is
    if (status == Status.ONGOINGP1) {
      board[move] = 1;
      if (checkThreeInRow(board) == 1) {
        // If three in a row on the board
        return new Pair<>(board, Status.WINP1);
      }
      if (checkDraw(board)) {
        // If game is a draw.
        return new Pair<>(board, Status.DRAW);
      }

      return new Pair<>(board, Status.ONGOINGP2);
    } else if (status == Status.ONGOINGP2) {
      board[move] = -1;
      if (checkThreeInRow(board) == -1) {
        // If three in a row on the board
        return new Pair<>(board, Status.WINP2);
      }
      if (checkDraw(board)) {
        // If game is a draw.
        return new Pair<>(board, Status.DRAW);
      }

      return new Pair<>(board, Status.ONGOINGP1);
    } else {
      // If game is not ongoing output state as it is.
      return new Pair<>(board, status);
    }
  }

  @Override
  public void updateState(Integer move) throws IllegalMoveException {
    currentState = nextState(currentState, move);
  }

  @Override
  public Pair<Integer[], Status> getState() {
    return currentState;
  }

  @Override
  public List<Integer> legalMoves(Pair<Integer[], Status> state) {
    List<Integer> legalMoves = new ArrayList<>();
    Integer[] smallBoard = state.getLeft();
    Status status = state.getRight();
    // Check if game in terminal state if so output no legal moves
    if ((status == Status.WINP1) || (status == Status.DRAW) || (status == Status.WINP2)) {
      return legalMoves;
    }
    for (int i = 0; i < BOARDSIZE; i++) {
      // for each space in the board
      if (smallBoard[i] == 0) {
        // if the space is empty it is a legal move.
        legalMoves.add(i);
      }
    }

    return legalMoves;
  }

  @Override
  public Double estimateValue(Pair<Integer[], Status> state) {
    Integer[] board = state.getLeft();
    Status status = state.getRight();
    if (status == Status.DRAW) {
      return 0.0;
    } else if (status == Status.WINP1) {
      return WINP1VALUE;
    } else if (status == Status.WINP2) {
      return WINP2VALUE;
    } else {
      return checkTwoInRow(board, 1) - checkTwoInRow(board, -1);
    }
  }

  @Override
  public Integer stringToMove(String inputMove) throws IllegalMoveException {
    try {
      return Integer.parseInt(inputMove);
    } catch (NumberFormatException e) {
      throw new IllegalMoveException("Invalid Move");
    }
  }

  @Override
  public String stringOfCurrentState() {
    return stringOfState(currentState);
  }

  @Override
  public String stringOfState(Pair<Integer[], Status> state) {
    Integer[] board = state.getLeft();
    String build = "\n";
    for (int i = 0; i < SEVEN; i += 3) {
      for (int j = 0; j < 3; j++) {
        if (board[i + j] == 1) {
          build = build.concat(" X |");
        } else if (board[i + j] == -1) {
          build = build.concat(" O |");
        } else {
          build = build.concat("   |");
        }
      }
      build = build.substring(0, build.length() - 1);
      build = build.concat("\n");
      build = build.concat("---|---|---" + "\n");
    }
    build = build.substring(0, build.length() - TWELVE);
    return build;
  }

  // Method that checks if the game board has three tokens in a row. If
  //    token of either player is three in a row output that token else
  //    if there are no three in a row output 0.
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
    } else if (grid[1].equals(grid[4]) && grid[1].equals(grid[SEVEN]) && !grid[1].equals(0)) {
      // middle column is all non-empty
      return grid[1];
    } else if (grid[2].equals(grid[5]) && grid[2].equals(grid[EIGHT]) && !grid[2].equals(0)) {
      // right column is all non-empty
      return grid[2];
    } else if (grid[0].equals(grid[4]) && grid[0].equals(grid[EIGHT]) && !grid[0].equals(0)) {
      // negative sloped diagonal is all non-empty
      return grid[0];
    } else if (grid[2].equals(grid[4]) && grid[2].equals(grid[6]) && !grid[2].equals(0)) {
      // positively sloped diagonal is all non-empty
      return grid[2];
    } else {
      return 0;
    }
  }

  // Method used to check if board is a draw or not.
  //    True if it is a draw, else false.
  private boolean checkDraw(Integer[] grid) {
    boolean draw = true;
    for (int i = 0; i < BOARDSIZE; i++) {
      if (grid[i] == 0) {
        draw = false;
        break;
      }
    }
    return draw;
  }

  // Method used to score for a certain player token how many two in a row are there.
  private Double checkTwoInRow(Integer[] grid, Integer token) {
    double sum = 0.0;
    if (Arrays.equals(new Integer[]{grid[0], grid[1], grid[2]}, new Integer[]{0, token, token})
            || Arrays.equals(new Integer[]{grid[0], grid[1], grid[2]},
              new Integer[]{token, 0, token})
            || Arrays.equals(new Integer[]{grid[0], grid[1], grid[2]},
              new Integer[]{token, token, 0})) {
      // check top row
      sum += TWOINAROWVALUE;
    }
    if (Arrays.equals(new Integer[]{grid[3], grid[4], grid[5]}, new Integer[]{0, token, token})
            || Arrays.equals(new Integer[]{grid[3], grid[4], grid[5]},
               new Integer[]{token, 0, token})
            || Arrays.equals(new Integer[]{grid[3], grid[4], grid[5]},
               new Integer[]{token, token, 0})) {
      // check middle row
      sum += TWOINAROWVALUE;
    }
    if (Arrays.equals(new Integer[]{grid[6], grid[SEVEN], grid[EIGHT]},
            new Integer[]{0, token, token})
            || Arrays.equals(new Integer[]{grid[6], grid[SEVEN], grid[EIGHT]},
              new Integer[]{token, 0, token})
            || Arrays.equals(new Integer[]{grid[6], grid[SEVEN], grid[EIGHT]},
              new Integer[]{token, token, 0})) {
      // check bottom row
      sum += TWOINAROWVALUE;
    }
    if (Arrays.equals(new Integer[]{grid[0], grid[3], grid[6]}, new Integer[]{0, token, token})
            || Arrays.equals(new Integer[]{grid[0], grid[3], grid[6]},
              new Integer[]{token, 0, token})
            || Arrays.equals(new Integer[]{grid[0], grid[3], grid[6]},
              new Integer[]{token, token, 0})) {
      // check left column
      sum += TWOINAROWVALUE;
    }
    if (Arrays.equals(new Integer[]{grid[1], grid[4], grid[SEVEN]}, new Integer[]{0, token, token})
            || Arrays.equals(new Integer[]{grid[1], grid[4], grid[SEVEN]},
               new Integer[]{token, 0, token})
            || Arrays.equals(new Integer[]{grid[1], grid[4], grid[SEVEN]},
               new Integer[]{token, token, 0})) {
      // check middle column
      sum += TWOINAROWVALUE;
    }
    if (Arrays.equals(new Integer[]{grid[2], grid[5], grid[EIGHT]}, new Integer[]{0, token, token})
            || Arrays.equals(new Integer[]{grid[2], grid[5], grid[EIGHT]},
               new Integer[]{token, 0, token})
            || Arrays.equals(new Integer[]{grid[2], grid[5], grid[EIGHT]},
               new Integer[]{token, token, 0})) {
      // check right column
      sum += TWOINAROWVALUE;
    }
    if (Arrays.equals(new Integer[]{grid[0], grid[4], grid[EIGHT]}, new Integer[]{0, token, token})
            || Arrays.equals(new Integer[]{grid[0], grid[4], grid[EIGHT]},
               new Integer[]{token, 0, token})
            || Arrays.equals(new Integer[]{grid[0], grid[4], grid[EIGHT]},
               new Integer[]{token, token, 0})) {
      // check negative sloped diagonal
      sum += TWOINAROWVALUE;
    }
    if (Arrays.equals(new Integer[]{grid[2], grid[4], grid[6]}, new Integer[]{0, token, token})
            || Arrays.equals(new Integer[]{grid[2], grid[4], grid[6]},
               new Integer[]{token, 0, token})
            || Arrays.equals(new Integer[]{grid[2], grid[4], grid[6]},
               new Integer[]{token, token, 0})) {
      // check positively sloped diagonal
      sum += TWOINAROWVALUE;
    }

    return sum;
  }
}
