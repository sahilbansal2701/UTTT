package edu.brown.cs.student;

import edu.brown.cs.student.Game.IllegalMoveException;
import edu.brown.cs.student.Game.Player;
import edu.brown.cs.student.Game.Players.AIPlayer;
import edu.brown.cs.student.Game.Status;
import edu.brown.cs.student.Pair.Pair;
import edu.brown.cs.student.TTT.TicTacToe;
import edu.brown.cs.student.UTTT.UltimateTicTacToe;
import org.junit.Test;


public class AIPlayerResultsTest {
  private final int numberOfIterations = 4;
  private static final int EASY = 3;
  private static final int MEDIUM = 5;
  private static final int HARD = 8;

  private final TicTacToe ttt = new TicTacToe();
  private final UltimateTicTacToe uttt = new UltimateTicTacToe();


  private Player<TicTacToe, Integer[], Integer> makeAITTT(int difficulty) {
    return new AIPlayer<>(ttt, difficulty, true, true);
  }

  private Player<UltimateTicTacToe, Pair<Integer[][], Integer>, Pair<Integer, Integer>> makeAIUTTT(int difficulty) {
    return new AIPlayer<>(uttt, difficulty, true, true);
  }

  private Pair<Status, Integer> gameLoopTTT(TicTacToe game,
                        Player<TicTacToe, Integer[], Integer> p1,
                        Player<TicTacToe, Integer[], Integer> p2) {
    int numberOfMoves = 0;
    while (true) {
      switch (game.getState().getRight()) {
        case WINP1:
          return new Pair<>(Status.WINP1, numberOfMoves);
        case WINP2:
          return new Pair<>(Status.WINP2, numberOfMoves);
        case DRAW:
          return new Pair<>(Status.DRAW, numberOfMoves);
        case ONGOINGP1:
          try {
            game.updateState(p1.nextMove(game.getState()));
          } catch (IllegalMoveException e) {
            System.out.println("FATAL ERROR: Players Should Only Return Valid Moves");
            System.exit(1);
          }
          numberOfMoves += 1;
          break;
        case ONGOINGP2:
          try {
            game.updateState(p2.nextMove(game.getState()));
          } catch (IllegalMoveException e) {
            System.out.println("FATAL ERROR: Players Should Only Return Valid Moves");
            System.exit(1);
          }
          numberOfMoves += 1;
          break;
        default:
          System.out.println("FATAL ERROR: Unknown Game Status");
          System.exit(1);
          break;
      }
    }
  }

  private Pair<Status, Integer> gameLoopUTTT(UltimateTicTacToe game,
                             Player<UltimateTicTacToe, Pair<Integer[][], Integer>, Pair<Integer, Integer>> p1,
                             Player<UltimateTicTacToe, Pair<Integer[][], Integer>, Pair<Integer, Integer>> p2) {
    int numberOfMoves = 0;
    while (true) {
      switch (game.getState().getRight()) {
        case WINP1:
          return new Pair<>(Status.WINP1, numberOfMoves);
        case WINP2:
          return new Pair<>(Status.WINP2, numberOfMoves);
        case DRAW:
          return new Pair<>(Status.DRAW, numberOfMoves);
        case ONGOINGP1:
          try {
            game.updateState(p1.nextMove(game.getState()));
          } catch (IllegalMoveException e) {
            System.out.println("FATAL ERROR: Players Should Only Return Valid Moves");
            System.exit(1);
          }
          numberOfMoves += 1;
          break;
        case ONGOINGP2:
          try {
            game.updateState(p2.nextMove(game.getState()));
          } catch (IllegalMoveException e) {
            System.out.println("FATAL ERROR: Players Should Only Return Valid Moves");
            System.exit(1);
          }
          numberOfMoves += 1;
          break;
        default:
          System.out.println("FATAL ERROR: Unknown Game Status");
          System.exit(1);
          break;
      }
    }
  }

  private String formatOutputString(String game, int p1, int p2, double percentP1Wins, double percentP2Wins, double percentDraws, double numberOfMoves) {
    String build = "";
    if (game.equals("TTT")) {
      build = build.concat("TTT: ");
    } else if (game.equals("UTTT")) {
      build = build.concat("UTTT: ");
    }
    if (p1 == EASY) {
      build = build.concat("EasyAI ");
    } else if (p1 == MEDIUM) {
      build = build.concat("MediumAI ");
    } else if (p1 == HARD) {
      build = build.concat("HardAI ");
    }
    build = build.concat("vs ");
    if (p2 == EASY) {
      build = build.concat("EasyAI: ");
    } else if (p2 == MEDIUM) {
      build = build.concat("MediumAI: ");
    } else if (p2 == HARD) {
      build = build.concat("HardAI: ");
    }
    build = build.concat(percentP1Wins + "% P1 Wins, " + percentP2Wins + "% P2 Wins, " + percentDraws + "% Draws, " + numberOfMoves+" Avg. Number Of Moves");
    return build;
  }

  @Test
  public void testResultsTTT() {
    System.out.println("--------------------------------------------------------------------------------");
    int[] difficulties = new int[]{EASY, MEDIUM, HARD};
    for (int i = 0; i < 3; i++) {
      for (int j = 0; j < 3; j++) {
        double numberP1Wins = 0.0;
        double numberP2Wins = 0.0;
        double numberDraws = 0.0;
        int totalNumberOfMoves = 0;
        for (int iteration = 0; iteration < numberOfIterations; iteration++) {
          ttt.reset();
          Pair<Status, Integer> result = gameLoopTTT(ttt, makeAITTT(difficulties[i]), makeAITTT(difficulties[j]));
          if (result.getLeft() == Status.WINP1) {
            numberP1Wins += 1.0;
          } else if (result.getLeft() == Status.WINP2) {
            numberP2Wins += 1.0;
          } else if (result.getLeft() == Status.DRAW) {
            numberDraws += 1.0;
          }
          totalNumberOfMoves += result.getRight();
        }
        double percentP1Wins = (numberP1Wins / numberOfIterations) * 100;
        double percentP2Wins = (numberP2Wins / numberOfIterations) * 100;
        double percentDraws = (numberDraws / numberOfIterations) * 100;
        double avgNumberOfMoves = (double) totalNumberOfMoves / (double) numberOfIterations;

        System.out.println(formatOutputString("TTT", difficulties[i], difficulties[j], percentP1Wins, percentP2Wins, percentDraws, avgNumberOfMoves));
      }
    }
    System.out.println("--------------------------------------------------------------------------------");
  }

  @Test
  public void testResultsUTTT() {
    System.out.println("--------------------------------------------------------------------------------");

    int[] difficulties = new int[]{EASY,MEDIUM, HARD};
    for (int i = 0; i < 3; i++) {
      for (int j = 0; j < 3; j++) {
        double numberP1Wins = 0.0;
        double numberP2Wins = 0.0;
        double numberDraws = 0.0;
        int totalNumberOfMoves = 0;

        for (int iteration = 0; iteration < numberOfIterations; iteration++) {
          uttt.reset();
          Pair<Status, Integer> result = gameLoopUTTT(uttt, makeAIUTTT(difficulties[i]), makeAIUTTT(difficulties[j]));
          if (result.getLeft() == Status.WINP1) {
            numberP1Wins += 1.0;
          } else if (result.getLeft() == Status.WINP2) {
            numberP2Wins += 1.0;
          } else if (result.getLeft() == Status.DRAW) {
            numberDraws += 1.0;
          }
          totalNumberOfMoves += result.getRight();
        }
        double percentP1Wins = (numberP1Wins / numberOfIterations) * 100;
        double percentP2Wins = (numberP2Wins / numberOfIterations) * 100;
        double percentDraws = (numberDraws / numberOfIterations) * 100;
        double avgNumberOfMoves = (double) totalNumberOfMoves / (double) numberOfIterations;

        System.out.println(formatOutputString("UTTT", difficulties[i], difficulties[j], percentP1Wins, percentP2Wins, percentDraws, avgNumberOfMoves));
      }
    }
    System.out.println("--------------------------------------------------------------------------------");
  }
}
