package edu.brown.cs.student.UTTT;


import com.google.gson.Gson;
import spark.ExceptionHandler;
import spark.Request;
import spark.Response;
import spark.Spark;
import java.io.PrintWriter;
import java.io.StringWriter;


/**
 * This class handles setting up the backend using Spark.
 */
public class UTTTGUI {
  private static final int DEFAULT_PORT = 4567;
  public static final Gson GSON = new Gson();
  /**
   * Constructor for this class.
   */
  public UTTTGUI() {

  }

  /**
   * Method to the run the spark server.
   */
  public void run() {
    runSparkServer();
  }

  /**
   * Spark server for the UltimateTicTacToe GUI.
   */
  private static void runSparkServer() {
    Spark.port(getHerokuAssignedPort());
//    Spark.port(DEFAULT_PORT);
    Spark.externalStaticFileLocation("src/main/resources/spark/template/static");
    Spark.exception(Exception.class, new ExceptionPrinter());
    Spark.webSocket("/message", UTTTWebsockets.class);
    Spark.options("/*", (request, response) -> {
      String accessControlRequestHeaders = request.headers("Access-Control-Request-Headers");
      if (accessControlRequestHeaders != null) {
        response.header("Access-Control-Allow-Headers", accessControlRequestHeaders);
      }

      String accessControlRequestMethod = request.headers("Access-Control-Request-Method");

      if (accessControlRequestMethod != null) {
        response.header("Access-Control-Allow-Methods", accessControlRequestMethod);
      }

      return "OK";
    });
    Spark.before((request, response) -> response.header("Access-Control-Allow-Origin", "*"));
  }

  /**
   * Display an error page when an exception occurs in the server.
   */
  private static class ExceptionPrinter implements ExceptionHandler {
    @Override
    public void handle(Exception e, Request req, Response res) {
      res.status(500);
      StringWriter stacktrace = new StringWriter();
      try (PrintWriter pw = new PrintWriter(stacktrace)) {
        pw.println("<pre>");
        e.printStackTrace(pw);
        pw.println("</pre>");
      }
      res.body(stacktrace.toString());
    }
  }

  /**
   * Method used to generate port for heroku.
   * @return - returns port number generated for heroku.
   */
  static int getHerokuAssignedPort() {
    ProcessBuilder processBuilder = new ProcessBuilder();
    if (processBuilder.environment().get("PORT") != null) {
      return Integer.parseInt(processBuilder.environment().get("PORT"));
    }
    return DEFAULT_PORT; //return default port if heroku-port isn't set (i.e. on localhost)
  }

}
