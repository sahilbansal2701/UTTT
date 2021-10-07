package edu.brown.cs.student;

import edu.brown.cs.student.Game.IllegalMoveException;
import edu.brown.cs.student.Game.Player;
import edu.brown.cs.student.Game.Players.AIPlayer;
import edu.brown.cs.student.Game.Status;
import edu.brown.cs.student.Pair.Pair;
import edu.brown.cs.student.TTT.TicTacToe;
import edu.brown.cs.student.UTTT.UltimateTicTacToe;
import org.junit.Test;

import static org.junit.Assert.*;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class AIPlayerModelBasedTest {
  private final int numberOfIterations = 4;

  private final TicTacToe ttt = new TicTacToe();
  private final Player<TicTacToe, Integer[], Integer> minimaxAITTT = new AIPlayer<>(ttt, 4, false, false);
  private final Player<TicTacToe, Integer[], Integer> alphaBetaPruningAITTT = new AIPlayer<>(ttt, 4, true, false);

  private final UltimateTicTacToe uttt = new UltimateTicTacToe();
  private final Player<UltimateTicTacToe, Pair<Integer[][], Integer>, Pair<Integer, Integer>> minimaxAIUTTT = new AIPlayer<>(uttt, 4, false, false);
  private final Player<UltimateTicTacToe, Pair<Integer[][], Integer>, Pair<Integer, Integer>> alphaBetaPruningAIUTTT = new AIPlayer<>(uttt, 4, true, false);

  private Pair<Integer[], Status> generateRandomTTTState() {
    ttt.reset();
    Pair<Integer[], Status> state = ttt.getState();
    int randomNumberOfMovesToApply = ThreadLocalRandom.current().nextInt(0, 9);
    for (int i = 0; i < randomNumberOfMovesToApply; i++) {
      List<Integer> legalMoves = ttt.legalMoves(state);
      int randomMove = legalMoves.get(ThreadLocalRandom.current().nextInt(0, legalMoves.size()));
      try {
        state = ttt.nextState(state, randomMove);
      } catch (IllegalMoveException e) {
        fail("ERROR: Error Setting Up Test: Random TTT State Generator");
      }
      if ((state.getRight() != Status.ONGOINGP1) && (state.getRight() != Status.ONGOINGP2)) {
        state = ttt.getState();
      }
    }

    return state;
  }

  private Pair<Pair<Integer[][], Integer>, Status> generateRandomUTTTState() {
    uttt.reset();
    Pair<Pair<Integer[][], Integer>, Status> state = uttt.getState();
    int randomNumberOfMovesToApply = ThreadLocalRandom.current().nextInt(0, 81);
    for (int i = 0; i < randomNumberOfMovesToApply; i++) {
      List<Pair<Integer, Integer>> legalMoves = uttt.legalMoves(state);
      Pair<Integer, Integer> randomMove = legalMoves.get(ThreadLocalRandom.current().nextInt(0, legalMoves.size()));
      try {
        state = uttt.nextState(state, randomMove);
      } catch (IllegalMoveException e) {
        fail("ERROR: Error Setting Up Test: Random TTT State Generator");
      }
      if ((state.getRight() != Status.ONGOINGP1) && (state.getRight() != Status.ONGOINGP2)) {
        state = uttt.getState();
      }
    }

    return state;
  }

  @Test
  public void testMinimaxAndAlphaBetaPruningTTT() {
    for (int i = 0; i < numberOfIterations; i++) {
      Pair<Integer[], Status> randomState = generateRandomTTTState();
      while ((randomState.getRight() != Status.ONGOINGP1) && (randomState.getRight() != Status.ONGOINGP2)) {
        randomState = generateRandomTTTState();
      }

//      System.out.println("Random State");
//      System.out.println(ttt.stringOfState(randomState));
//      System.out.println("Status");
//      System.out.println(randomState.getRight());

      Integer minimaxMove = minimaxAITTT.nextMove(randomState);
      Integer alphaBetaPruningMove = alphaBetaPruningAITTT.nextMove(randomState);

//      System.out.println("Minimax Move");
//      System.out.println(minimaxMove);
//      System.out.println("AlphaBetaPruning Move");
//      System.out.println(alphaBetaPruningMove);

      try {
        Pair<Integer[], Status> minimaxNextState = ttt.nextState(randomState, minimaxMove);
        Pair<Integer[], Status> alphaBetaPruningNextState = ttt.nextState(randomState, alphaBetaPruningMove);

        Double valueOfMinimaxMove = ttt.estimateValue(minimaxNextState);
        Double valueOfAlphaBetaPruningMove = ttt.estimateValue(alphaBetaPruningNextState);

//        System.out.println("Minimax Next State");
//        System.out.println(ttt.stringOfState(minimaxNextState));
//        System.out.println("Minimax Value of Move");
//        System.out.println(valueOfMinimaxMove);
//
//        System.out.println("AlphaBetaPruning Next State");
//        System.out.println(ttt.stringOfState(alphaBetaPruningNextState));
//        System.out.println("AlphaBetaPruning Value of Move");
//        System.out.println(valueOfAlphaBetaPruningMove);

        try {
          assertEquals(minimaxMove, alphaBetaPruningMove);
        } catch (AssertionError e) {
          assertEquals(valueOfMinimaxMove, valueOfAlphaBetaPruningMove, 0.01);
        }
      } catch (IllegalMoveException e) {
        fail("ERROR: Error Setting Up Test: AI Player Gave Illegal Move");
      }
    }
  }

  @Test
  public void testMinimaxAndAlphaBetaPruningUTTT() {
    for (int i = 0; i < numberOfIterations; i++) {
      Pair<Pair<Integer[][], Integer>, Status> randomState = generateRandomUTTTState();
      while ((randomState.getRight() != Status.ONGOINGP1) && (randomState.getRight() != Status.ONGOINGP2)) {
        randomState = generateRandomUTTTState();
      }

//      System.out.println("Random State");
//      System.out.println(uttt.stringOfState(randomState));
//      System.out.println("Status");
//      System.out.println(randomState.getRight());

      Pair<Integer, Integer> minimaxMove = minimaxAIUTTT.nextMove(randomState);
      Pair<Integer, Integer> alphaBetaPruningMove = alphaBetaPruningAIUTTT.nextMove(randomState);

//      System.out.println("Minimax Move");
//      System.out.println(minimaxMove);
//      System.out.println("AlphaBetaPruning Move");
//      System.out.println(alphaBetaPruningMove);

      try {
        Pair<Pair<Integer[][], Integer>, Status> minimaxNextState = uttt.nextState(randomState, minimaxMove);
        Pair<Pair<Integer[][], Integer>, Status> alphaBetaPruningNextState = uttt.nextState(randomState, alphaBetaPruningMove);

        Double valueOfMinimaxMove = uttt.estimateValue(minimaxNextState);
        Double valueOfAlphaBetaPruningMove = uttt.estimateValue(alphaBetaPruningNextState);

//        System.out.println("Minimax Next State");
//        System.out.println(uttt.stringOfState(minimaxNextState));
//        System.out.println("Minimax Value of Move");
//        System.out.println(valueOfMinimaxMove);
//
//        System.out.println("AlphaBetaPruning Next State");
//        System.out.println(uttt.stringOfState(alphaBetaPruningNextState));
//        System.out.println("AlphaBetaPruning Value of Move");
//        System.out.println(valueOfAlphaBetaPruningMove);

        try {
          assertEquals(minimaxMove, alphaBetaPruningMove);
        } catch (AssertionError e) {
          assertEquals(valueOfMinimaxMove, valueOfAlphaBetaPruningMove, 0.01);
        }
      } catch (IllegalMoveException e) {
        fail("ERROR: Error Setting Up Test: AI Player Gave Illegal Move");
      }
    }
  }

}
