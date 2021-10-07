package edu.brown.cs.student;

import edu.brown.cs.student.UTTT.MiniREPL;
import edu.brown.cs.student.UTTT.UTTTGUI;


/**
 * The Main class of our project. This is where execution begins.
 *
 */
public final class Main {
  /**
   * The initial method called when execution begins.
   * @param args - An array of command line arguments
   */
  public static void main(String[] args) {
    new Main(args).run();
  }

  private Main(String[] args) { }

  private void run() {
    UTTTGUI server = new UTTTGUI();
    server.run();

    MiniREPL repl = new MiniREPL();
    repl.run();
  }
}
