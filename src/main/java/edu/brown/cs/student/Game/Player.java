package edu.brown.cs.student.Game;

import edu.brown.cs.student.Pair.Pair;

/**
 * Interface for players.
 * @param <G> - type parameter for Game type.
 * @param <B> - type parameter for what type of Board the game has.
 * @param <M> - type parameter for what type of Move the game has.
 */
public interface Player<G extends Game<B, M>, B, M> {

  /**
   * Method for the player to make its next move in the game.
   * @param currentState - The current state of the game.
   * @return - Returns the move the player wishes to make.
   */
  M nextMove(Pair<B, Status> currentState);
}
