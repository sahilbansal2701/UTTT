package edu.brown.cs.student.Game;

/**
 * Exception class if a move is illegal.
 */
public class IllegalMoveException extends Exception {

  /**
   * Illegal Move Exception.
   * @param errorMessage - The error message that can be printed.
   */
  public IllegalMoveException(String errorMessage) {
    super(errorMessage);
  }
}
