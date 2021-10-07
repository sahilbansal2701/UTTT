package edu.brown.cs.student.Game.Players;

import edu.brown.cs.student.Game.Game;
import edu.brown.cs.student.Game.IllegalMoveException;
import edu.brown.cs.student.Game.Player;
import edu.brown.cs.student.Game.Status;
import edu.brown.cs.student.Pair.Pair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

/**
 * Human player class.
 * @param <G> - type parameter for Game type.
 * @param <B> - type parameter for what type of Board the game has.
 * @param <M> - type parameter for what type of Move the game has.
 */
public class HumanPlayer<G extends Game<B, M>, B, M> implements Player<G, B, M> {

  private final G game;
  private final BufferedReader readIn = new BufferedReader(new InputStreamReader(System.in));

  /**
   * Human Player constructor.
   * @param game - Game that the human player will be called on.
   */
  public HumanPlayer(G game) {
    this.game = game;
  }

  /**
   * Method for the human player to make its next move in the game.
   * @param currentState - The current state of the game.
   * @return - Returns the move the human player wishes to make.
   */
  @Override
  public M nextMove(Pair<B, Status> currentState) {
    try {
      List<M> legalMoves = game.legalMoves(currentState);
      System.out.println("Please Enter a Valid Move:");
      String line = readIn.readLine();
      // Attempt to turn user input to move.
      M move = game.stringToMove(line);
      // If user input is a legal move, return from the method
      //    else ask for input again.
      if (legalMoves.contains(move)) {
        return move;
      } else {
        System.out.println("ERROR: Illegal Move Entered");
        return nextMove(currentState);
      }
    } catch (IOException e) {
      System.err.println("FATAL ERROR: Failed to Read Line");
      System.exit(1);
    } catch (IllegalMoveException e) {
      // User input could not be parsed into a move.
      System.out.println("ERROR: Did Not Enter a Move");
      return nextMove(currentState);
    }
    // Will Never Get Here.
    return null;
  }
}
