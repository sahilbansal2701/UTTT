package edu.brown.cs.student.UTTT;

import edu.brown.cs.student.Game.Game;
import edu.brown.cs.student.Game.IllegalMoveException;
import edu.brown.cs.student.Game.Player;
import edu.brown.cs.student.Game.Referee;
import edu.brown.cs.student.Game.Players.AIPlayer;
import edu.brown.cs.student.Game.Players.HumanPlayer;
import edu.brown.cs.student.Pair.Pair;

/**
 * Referee class for Ultimate TicTacToe.
 */
public class RefereeUltimateTicTacToe implements Referee {

  /**
   * Ultimate TicTacToe Referee Constructor.
   */
  public RefereeUltimateTicTacToe() {

  }
  // Difficulty Levels for AI
  private static final int EASY = 3;
  private static final int MEDIUM = 5;
  private static final int HARD = 8;

  // Method used to run the game between two players of the game.
  private void gameLoop(Game<Pair<Integer[][], Integer>, Pair<Integer, Integer>> game,
                        Player<Game<Pair<Integer[][], Integer>, Pair<Integer, Integer>>,
                            Pair<Integer[][], Integer>, Pair<Integer, Integer>> p1,
                        Player<Game<Pair<Integer[][], Integer>, Pair<Integer, Integer>>,
                            Pair<Integer[][], Integer>, Pair<Integer, Integer>> p2) {
    boolean stop = false;
    // While game has not terminated keep playing.
    // If terminal state end game.
    // If not terminal state ask the player whose turn it is for their move,
    //    given the current state of the game.
    while (!stop) {
      System.out.println(game.stringOfCurrentState());
      switch (game.getState().getRight()) {
        case WINP1:
          System.out.println("P1 WINS!!");
          stop = true;
          break;
        case WINP2:
          System.out.println("P2 WINS!!");
          stop = true;
          break;
        case DRAW:
          System.out.println("DRAW");
          stop = true;
          break;
        case ONGOINGP1:
          System.out.println("P1's Turn");
          try {
            game.updateState(p1.nextMove(game.getState()));
          } catch (IllegalMoveException e) {
            System.out.println("FATAL ERROR: Players Should Only Return Valid Moves");
            System.exit(1);
          }
          break;
        case ONGOINGP2:
          System.out.println("P2's Turn");
          try {
            game.updateState(p2.nextMove(game.getState()));
          } catch (IllegalMoveException e) {
            System.out.println("FATAL ERROR: Players Should Only Return Valid Moves");
            System.exit(1);
          }
          break;
        default:
          System.out.println("FATAL ERROR: Unknown Game Status");
          System.exit(1);
          break;
      }
    }
    System.out.println("Game Has Ended!");
  }

  @Override
  public void startGame(String[] splitUserInput) {
    Game<Pair<Integer[][], Integer>, Pair<Integer, Integer>> game = new UltimateTicTacToe();
    Player<Game<Pair<Integer[][], Integer>, Pair<Integer, Integer>>,
        Pair<Integer[][], Integer>, Pair<Integer, Integer>> p1;
    Player<Game<Pair<Integer[][], Integer>, Pair<Integer, Integer>>,
        Pair<Integer[][], Integer>, Pair<Integer, Integer>> p2;
    boolean p1AI = false;
    if (splitUserInput[2].equals("human")) {
      p1 = new HumanPlayer<>(game);
    } else if (splitUserInput[2].equals("ai")) {
      p1AI = true;
      switch (splitUserInput[3]) {
        case "easy":
          p1 = new AIPlayer<>(game, EASY, true, true);
          break;
        case "medium":
          p1 = new AIPlayer<>(game, MEDIUM, true, true);
          break;
        case "hard":
          p1 = new AIPlayer<>(game, HARD, true, true);
          break;
        default:
          System.out.println("ERROR: Incorrect AI Difficulty");
          return;
      }
    } else {
      System.out.println("Player 1 Type is Not Supported Yet!");
      return;
    }

    int indexToCheck = 3;
    if (p1AI) {
      indexToCheck += 1;
    }

    if (splitUserInput[indexToCheck].equals("human")) {
      p2 = new HumanPlayer<>(game);
    } else if (splitUserInput[indexToCheck].equals("ai")) {
      switch (splitUserInput[indexToCheck + 1]) {
        case "easy":
          p2 = new AIPlayer<>(game, EASY, true, true);
          break;
        case "medium":
          p2 = new AIPlayer<>(game, MEDIUM, true, true);
          break;
        case "hard":
          p2 = new AIPlayer<>(game, HARD, true, true);
          break;
        default:
          System.out.println("ERROR: Incorrect AI Difficulty");
          return;
      }
    } else {
      System.out.println("Player 2 Type is Not Supported Yet!");
      return;
    }
    gameLoop(game, p1, p2);
  }
}

