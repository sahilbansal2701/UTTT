package edu.brown.cs.student.Game;

import edu.brown.cs.student.Pair.Pair;

import java.util.List;

/**
 * Interface for games.
 * @param <B> - type parameter for what type of Board the game has.
 * @param <M> - type parameter for what type of Move the game has.
 */
public interface Game<B, M> {

  /**
   * Resets the game held in the game object.
   */
  void reset();

  /**
   * Checks the next state of the passed in state of the game and move.
   * @param oldState - The state of the game before the move is applied.
   * @param move - The move to be applied to the state of the game passed in.
   * @return - Returns a new copy of next state of the board with the passed in move applied.
   * @throws IllegalMoveException - If the passed in move is illegal.
   */
  Pair<B, Status> nextState(Pair<B, Status> oldState, M move) throws IllegalMoveException;

  /**
   * Changes the state of the game held in the game object to the next state after
   *    applying the move passed in.
   * @param move - The move to update the state of the game held in the game object.
   * @throws IllegalMoveException - If the passed in move is illegal.
   */
  void updateState(M move) throws IllegalMoveException;

  /**
   * Getter for the current state of the game held in the game object.
   * @return - Returns the current state of the game held in the game object.
   */
  Pair<B, Status> getState();

  /**
   * Method that gets all of the legal moves that can be made with the state passed in.
   * @param state - The state of the game you want to get the legal moves of.
   * @return - Returns a list of legal moves.
   */
  List<M> legalMoves(Pair<B, Status> state);

  /**
   * Method used by the mini-max/alpha-beta pruning algorithms.
   * This method estimates the value of the passed in state of the game.
   * @param state - The state of the board of which to estimate the value.
   * @return - Returns a double with a "score" of the value of the state.
   *    Positive means it is better for player 1 and negative means it is better for player 2.
   */
  Double estimateValue(Pair<B, Status> state);

  /**
   * Method that converts an input move of type string to type M.
   * @param inputMove - The inputted move of type string.
   * @return - The inputted move as type M.
   * @throws IllegalMoveException - Exception if the inputted move is illegal
   *    in that it cannot be converted.
   */
  M stringToMove(String inputMove) throws IllegalMoveException;

  /**
   * Method that gets the current state of the game held in the game object as a string.
   * @return - Returns a string of the current state of the game held in the game object.
   */
  String stringOfCurrentState();

  /**
   * Method that outputs a string representation of the passed in state of the game.
   * @param state - The state of to turn into a string.
   * @return - Returns a string of the passed in state of the game.
   */
  String stringOfState(Pair<B, Status> state);
}
