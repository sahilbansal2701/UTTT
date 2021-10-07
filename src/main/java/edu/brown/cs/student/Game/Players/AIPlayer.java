package edu.brown.cs.student.Game.Players;

import edu.brown.cs.student.Game.Game;
import edu.brown.cs.student.Game.IllegalMoveException;
import edu.brown.cs.student.Game.Player;
import edu.brown.cs.student.Game.Status;
import edu.brown.cs.student.Pair.Pair;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * AI Player class.
 * @param <G> - type parameter for Game type.
 * @param <B> - type parameter for what type of Board the game has.
 * @param <M> - type parameter for what type of Move the game has.
 */
public class AIPlayer<G extends Game<B, M>, B, M> implements Player<G, B, M> {

  private final G game;
  private final int maxDepth;
  private final boolean useAlphaBetaPruning;
  private final boolean randomize;

  /**
   * AI Player constructor.
   * @param game - Game that the ai player will be called on.
   * @param maxDepth - Max Depth to look for in mini-max or alphaBetaPruning.
   * @param useAlphaBetaPruning - Whether or not to use alpha-beta pruning.
   * @param randomize - Boolean indicating whether to output a random move
   *                  if all moves are as good as each other.
   */
  public AIPlayer(G game, int maxDepth, boolean useAlphaBetaPruning, boolean randomize) {
    this.game = game;
    this.maxDepth = maxDepth;
    this.useAlphaBetaPruning = useAlphaBetaPruning;
    this.randomize = randomize;
  }

  /**
   * Mini-max algorithm for the AI player to make their move.
   * @param currentState - The current state of the game.
   * @param depth - How far to look for when predicting the move to make.
   * @return - Returns a double representing the value of the state, given that we
   *    look into the future of the game for depth rounds.
   */
  private Double minimax(Pair<B, Status> currentState, Integer depth) {
    // If at depth 0, or a terminal state just return value of state passed in.
    if ((depth == 0)
            || (currentState.getRight() == Status.DRAW)
            || (currentState.getRight() == Status.WINP1)
            || (currentState.getRight() == Status.WINP2)) {
      return game.estimateValue(currentState);
    }
    try {
      double value;
      List<M> legalMoves;

      // Check the Status to Know Whether we are trying to maxmize or minimize score.
      // Then for each legal move of the current state produce the nextstate and call
      //    minimax recursively.
      if (currentState.getRight() == Status.ONGOINGP1) {
        value = Double.NEGATIVE_INFINITY;
        legalMoves = game.legalMoves(currentState);
        for (M move : legalMoves) {
          Pair<B, Status> nextState = game.nextState(currentState, move);
          value = Math.max(value, minimax(nextState, depth - 1));
        }
      } else {
        value = Double.POSITIVE_INFINITY;
        legalMoves = game.legalMoves(currentState);
        for (M move : legalMoves) {
          Pair<B, Status> nextState = game.nextState(currentState, move);
          value = Math.min(value, minimax(nextState, depth - 1));
        }
      }
      return value;
    } catch (IllegalMoveException e) {
      System.out.println("FATAL ERROR: Move Illegal in AI Player");
      System.exit(1);
    }
    // Will never get here.
    return null;
  }

  /**
   * Alpha-beta pruning Algorithm.
   * @param currentState - The current state of the game.
   * @param depth - How far to look for predicting the AI player's move.
   * @param alpha - alpha used for alpha-cut in alpha beta pruning
   * @param beta - beta used for beta-cut in alpha beta pruning
   * @return - Returns a double representing the value of the state of the game, given that we
   *      look into the future of the game for depth rounds.
   */
  private Double alphaBetaPruning(Pair<B, Status> currentState, Integer depth,
                                  Double alpha, Double beta) {
    // If at depth 0, or a terminal state just return value of state passed in.
    if ((depth == 0)
            || (currentState.getRight() == Status.DRAW)
            || (currentState.getRight() == Status.WINP1)
            || (currentState.getRight() == Status.WINP2)) {
      return game.estimateValue(currentState);
    }
    try {
      double value;
      List<M> legalMoves = game.legalMoves(currentState);

      // Check the Status to Know Whether we are trying to maxmize or minimize score.
      // Then for each legal move of the current state produce the nextstate and call
      //    minimax recursively.
      if (currentState.getRight() == Status.ONGOINGP1) {
        value = Double.NEGATIVE_INFINITY;
        for (M move : legalMoves) {
          Pair<B, Status> nextState = game.nextState(currentState, move);
          value = Math.max(value, alphaBetaPruning(nextState, depth - 1, alpha, beta));
          alpha = Math.max(value, alpha);
          if (alpha >= beta) {
            break;
          }
        }
      } else {
        value = Double.POSITIVE_INFINITY;
        for (M move : legalMoves) {
          Pair<B, Status> nextState = game.nextState(currentState, move);
          value = Math.min(value, alphaBetaPruning(nextState, depth - 1, alpha, beta));
          beta = Math.min(value, beta);
          if (beta <= alpha) {
            break;
          }
        }
      }
      return value;
    } catch (IllegalMoveException e) {
      System.out.println("FATAL ERROR: Move Illegal in AI Player");
      e.printStackTrace();
      System.exit(1);
    }
    // Will never get here.
    return null;
  }

  /**
   * Helper function that finds the index of the max element in the input list.
   * @param list - The list to find the index of the max element.
   * @return - Returns an integer which is the index of the max element.
   *    If the ai was created to randomize then if all of the elements in the
   *    list are the same then outputs a random index.
   */
  private int findIndexOfMaxElementOfList(List<Double> list) {
    boolean allSame = true;
    Double maxValue = Double.NEGATIVE_INFINITY;
    int maxIndex = -1;
    for (int i = 0; i < list.size(); i++) {
      if (Double.compare(list.get(i), maxValue) > 0) {
        maxValue = list.get(i);
        maxIndex = i;
      }
      // Check if all the elements in the list are the same.
      if (Double.compare(list.get(0), list.get(i)) != 0) {
        allSame = false;
      }
    }
    if (allSame && randomize) {
      return ThreadLocalRandom.current().nextInt(0, list.size());
    } else {
      return maxIndex;
    }
  }

  /**
   * Helper function that finds the index of the min element in a list.
   * @param list - The list to find the index of the min element.
   * @return - Returns an integer which is the index of the min element.
   *    If the ai was created to randomize then if all of the elements in the
   *    list are the same then outputs a random index.
   */
  private int findIndexOfMinElementOfList(List<Double> list) {
    boolean allSame = true;
    Double minValue = Double.POSITIVE_INFINITY;
    int minIndex = -1;
    for (int i = 0; i < list.size(); i++) {
      if (list.get(i) < minValue) {
        minValue = list.get(i);
        minIndex = i;
      }
      // Check if all the elements in the list are the same.
      if (Double.compare(list.get(0), list.get(i)) != 0) {
        allSame = false;
      }
    }
    if (allSame && randomize) {
      return ThreadLocalRandom.current().nextInt(0, list.size());
    } else {
      return minIndex;
    }
  }

  /**
   * Method for the AI player to make its next move in the game.
   * @param currentState - The current state of the game.
   * @return - Returns the move of the AI player.
   *    It is not allowed to ask the AI for a move if the game has ended.
   */
  @Override
  public M nextMove(Pair<B, Status> currentState) {
    try {
      List<M> legalMoves = game.legalMoves(currentState);
      if (legalMoves.size() == 0) {
        System.out.println("FATAL ERROR: Asked AI Player For Move When Game Over");
        System.exit(1);
      }
      List<Double> bestValueGivenMove = new LinkedList<>();
      // Carries out the first level of the minimax/alpha beta pruning
      //    so that we can figure out what move we want to make.
      for (M move : legalMoves) {
        Pair<B, Status> nextState = game.nextState(currentState, move);
        if (useAlphaBetaPruning) {
          bestValueGivenMove.add(alphaBetaPruning(nextState, maxDepth,
                  Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY));
        } else {
          bestValueGivenMove.add(minimax(nextState, maxDepth));
        }
      }
      // Depending on whether we are player 1 or 2 we want to maximize or minimize the score.
      if (currentState.getRight() == Status.ONGOINGP1) {
        int maxIndex = findIndexOfMaxElementOfList(bestValueGivenMove);
        return legalMoves.get(maxIndex);
      } else if (currentState.getRight() == Status.ONGOINGP2) {
        int minIndex = findIndexOfMinElementOfList(bestValueGivenMove);
        return legalMoves.get(minIndex);
      }

    } catch (IllegalMoveException e) {
      System.out.println("FATAL ERROR: Move Illegal in AI Player");
      System.exit(1);
    }
    // Will never get here.
    return null;
  }
}
