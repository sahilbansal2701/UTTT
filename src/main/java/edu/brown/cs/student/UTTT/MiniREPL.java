package edu.brown.cs.student.UTTT;

import edu.brown.cs.student.Game.Referee;
import edu.brown.cs.student.TTT.RefereeTicTacToe;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

/**
 * REPL class for testing purposes of the UTTT game.
 *  (Can also run a TTT game)
 */
public class MiniREPL {

  private final BufferedReader inputReader = new BufferedReader(new InputStreamReader(System.in));

  /**
   * Method to run the REPL.
   */
  public void run() {
    try {
      String line = inputReader.readLine();

      // MiniREPL Loop.
      while (line != null) {
        // Parse Input.
        String[] splitUserInput = line.split(" ");
        // Evaluate Input.
        if (splitUserInput.length != 0) {
          if (splitUserInput[0].equals("start_game")) {
            if ((splitUserInput.length == 4) || (splitUserInput.length == 5)
                || (splitUserInput.length == 6)) {
              if (splitUserInput[1].equals("uttt")) {
                Referee referee = new RefereeUltimateTicTacToe();
                referee.startGame(Arrays.copyOfRange(splitUserInput, 0,
                    splitUserInput.length));
              } else if (splitUserInput[1].equals("ttt")) {
                Referee referee = new RefereeTicTacToe();
                referee.startGame(Arrays.copyOfRange(splitUserInput, 0,
                    splitUserInput.length));
              } else {
                System.out.println("The Game is Not Supported Yet!");
              }
            } else {
              System.out.println("ERROR: Incorrect Number of Arguments");
            }
          } else if (splitUserInput[0].equals("quit")) {
            System.out.println("Quitting");
            return;
          } else {
            System.out.println("ERROR: Invalid Command Entered");
          }
        }
        // Loop
        line = inputReader.readLine();
      }
      inputReader.close();
    } catch (IOException e) {
      System.out.println("FATAL ERROR: IOException in MiniREPL");
      System.exit(1);
    }
  }
}
