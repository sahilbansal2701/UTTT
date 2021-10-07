package edu.brown.cs.student.Game;

/**
 * Interface for the Referee.
 *    Used in the REPL to play games.
 *    Made for testing purposes.
 */
public interface Referee {
  /**
   * Starts the game to manage.
   * @param splitUserInput - The user input as an array of strings,
   *                       so it can setup the game with the appropriate
   *                       types of players it supports.
   */
  void startGame(String[] splitUserInput);
}
