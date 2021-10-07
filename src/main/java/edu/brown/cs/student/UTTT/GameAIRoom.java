package edu.brown.cs.student.UTTT;

import com.google.common.collect.ImmutableMap;
import edu.brown.cs.student.Game.Game;
import edu.brown.cs.student.Game.IllegalMoveException;
import edu.brown.cs.student.Game.Player;
import edu.brown.cs.student.Game.Players.AIPlayer;
import edu.brown.cs.student.Game.Status;
import edu.brown.cs.student.Pair.Pair;
import com.google.gson.JsonObject;
import org.eclipse.jetty.websocket.api.Session;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Class used for the front end so that we can associate a room
 *    with each game. Rooms contain the AI when needed.
 */
public class GameAIRoom {

  // Difficulty Levels for AI
  private static final int EASY = 3;
  private static final int MEDIUM = 5;
  private static final int HARD = 8;

  private final int gameNumber;

  private final Game<Pair<Integer[][], Integer>, Pair<Integer, Integer>> game;
  private Player<Game<Pair<Integer[][], Integer>, Pair<Integer, Integer>>,
          Pair<Integer[][], Integer>, Pair<Integer, Integer>> aiPlayer1;
  private Player<Game<Pair<Integer[][], Integer>, Pair<Integer, Integer>>,
          Pair<Integer[][], Integer>, Pair<Integer, Integer>> aiPlayer2;
  private final Queue<Session> roomSessions = new ConcurrentLinkedQueue<>();

  /**
   * Message types used by the front end.
   */
  private enum MessageType {
    CONNECT,
    UPDATE,
    SEND
  }
  /**
   * Game AI room class, makes a new room for each game. Sets AI to medium by default.
   * @param game - game associated with the room.
   * @param gameNumber - the id of this GameAIRoom.
   */
  public GameAIRoom(Game<Pair<Integer[][], Integer>, Pair<Integer, Integer>> game, int gameNumber) {
    this.game = game;
    setAiPlayer("medium", "P1");
    setAiPlayer("medium", "P2");
    this.gameNumber = gameNumber;
  }

  /**
   * Method to get ai player 1.
   * @return - returns ai player 1.
   */
  public Player<Game<Pair<Integer[][], Integer>, Pair<Integer, Integer>>,
          Pair<Integer[][], Integer>, Pair<Integer, Integer>> getAiPlayer1() {
    return aiPlayer1;
  }

  /**
   * Method to get ai player 2.
   * @return - returns ai player 2.
   */
  public Player<Game<Pair<Integer[][], Integer>, Pair<Integer, Integer>>,
          Pair<Integer[][], Integer>, Pair<Integer, Integer>> getAiPlayer2() {
    return aiPlayer2;
  }

  /**
   * Method to return the game held.
   * @return - the game held in the room.
   */
  public Game<Pair<Integer[][], Integer>, Pair<Integer, Integer>> getGame() {
    return game;
  }

  /**
   * Method to set an AI Player for the game room.
   * @param difficulty - The difficulty of the AI player we want.
   * @param player - which player we want to set to this difficulty ai.
   */
  public void setAiPlayer(String difficulty, String player) {
    int maxDepth;
    if (difficulty.equals("hard")) {
      maxDepth = HARD;
    } else if (difficulty.equals("medium")) {
      maxDepth = MEDIUM;
    } else {
      maxDepth = EASY;
    }
    if (player.equals("P1")) {
      aiPlayer1 = new AIPlayer<>(game, maxDepth, true, true);
    } else {
      aiPlayer2 = new AIPlayer<>(game, maxDepth, true, true);
    }
  }

  // Method used to convert a game into a hashmap.
  private ImmutableMap.Builder<String, Object> gameStateToHashmap() {
    Pair<Pair<Integer[][], Integer>, Status> state = game.getState();
    ImmutableMap.Builder<String, Object> output = ImmutableMap.builder();
    output.put("status", state.getRight().ordinal());
    output.put("nextBoardToMove", state.getLeft().getRight());
    output.put("board", state.getLeft().getLeft());
    output.put("legalMoves", listPairsToListMap(game.legalMoves(state)));
    return output;
  }

  // Method used to turn a list of integer pairs into a list of hashmaps.
  private List<Map<String, Integer>> listPairsToListMap(List<Pair<Integer, Integer>> list) {
    List<Map<String, Integer>> output = new LinkedList<>();
    for (Pair<Integer, Integer> move : list) {
      Map<String, Integer> moveMap = new HashMap<>();
      moveMap.put("board", move.getLeft());
      moveMap.put("slot", move.getRight());
      output.add(moveMap);
    }
    return output;
  }

  /**
   * Method used to setup the room when a game is created.
   * @param data - JsonObject that holds the data for how
   *             many ai players we need and their difficulties.
   * @param inputSession - the session that is associated with this game room.
   * @throws IOException - throws exception if cannot send something to
   *      the session passed in.
   */
  public void createGame(JsonObject data, Session inputSession) throws IOException {
    // Add the session to the list of sessions associated with this game room.
    roomSessions.add(inputSession);

    // Setup the ai players appropriately using the data passed in.
    int numAI = data.get("numAIPlayers").getAsInt();
    String difficulty1 = data.get("difficulty1").getAsString();
    if (numAI == 1) {
      setAiPlayer(difficulty1, "P1");
      setAiPlayer(difficulty1, "P2");
    } else if (numAI == 2) {
      String difficulty2 = data.get("difficulty2").getAsString();
      setAiPlayer(difficulty1, "P1");
      setAiPlayer(difficulty2, "P2");
    }
    // Construct a message with all the game information
    ImmutableMap.Builder<String, Object> output = gameStateToHashmap();
    output.put("gameNumber", gameNumber);
    JsonObject update = new JsonObject();
    update.addProperty("type", MessageType.UPDATE.ordinal());
    update.addProperty("handler", "createGame");
    update.addProperty("state", UTTTGUI.GSON.toJson(output.build()));
    // Send the message to the session passed in.
    for (Session session : roomSessions) {
      session.getRemote().sendString(UTTTGUI.GSON.toJson(update));
    }
  }

  /**
   * Method used to take in data and apply a move to the game.
   * @param input - the JsonObject to parse for the move we need to apply to the game.
   *              Automatically send this update to the sessions stored in the room.
   */
  public void applyHumanMove(JsonObject input) {
    try {
      // Parse input for the move to apply
      int bigBoard = input.get("bigBoard").getAsInt();
      int smallBoard = input.get("smallBoard").getAsInt();
      Pair<Integer, Integer> move = new Pair<>(bigBoard, smallBoard);
      // Apply the move.
      game.updateState(move);
      // Construct a message with all the game information
      ImmutableMap.Builder<String, Object> output = gameStateToHashmap();
      String result = UTTTGUI.GSON.toJson(output.build());
      JsonObject update = new JsonObject();
      update.addProperty("type", MessageType.UPDATE.ordinal());
      update.addProperty("handler", "applyHumanMove");
      update.addProperty("state", result);
      // Send the message to all the sessions stored/associated with this game room.
      for (Session session : roomSessions) {
        session.getRemote().sendString(UTTTGUI.GSON.toJson(update));
      }
    } catch (IllegalMoveException | IOException e) {
      System.out.print("ERROR: GameAIRoom: Apply Human Move");
    }
  }

  /**
   * Method used to ask an AI for their move, and apply it to the game. Then,
   *               Automatically send this update to the sessions stored in the room.
   */
  public void applyAIMove() {
    Pair<Integer, Integer> move;
    try {
      // Check which AI to ask for a move and ask them.
      if (game.getState().getRight().equals(Status.ONGOINGP1)) {
        move = aiPlayer1.nextMove(game.getState());
      } else {
        move = aiPlayer2.nextMove(game.getState());
      }
      // Apply the move.
      game.updateState(move);

      // Construct a message with all the game information
      ImmutableMap.Builder<String, Object> output = gameStateToHashmap();
      Map<String, Integer> moveMap = new HashMap<>();
      moveMap.put("board", move.getLeft());
      moveMap.put("slot", move.getRight());
      output.put("aiMove", moveMap);
      String results = UTTTGUI.GSON.toJson(output.build());
      JsonObject update = new JsonObject();
      update.addProperty("type", MessageType.UPDATE.ordinal());
      update.addProperty("handler", "AIMove");
      update.addProperty("state", results);
      // Send the message to all the sessions stored/associated with this game room.
      for (Session session : roomSessions) {
        session.getRemote().sendString(UTTTGUI.GSON.toJson(update));
      }

    } catch (IllegalMoveException | IOException e) {
      System.out.print("ERROR: GameAIRoom: Apply AI Move");
    }
  }

  /**
   * Method used to ask an AI for what move they would make as a hint for what move a player
   *    should make. Then, automatically send this move to the sessions stored in the room.
   * @throws IOException - throws exception if cannot send something to
   *         the session passed in.
   */
  public void hint() throws IOException {
    // Ask AI for what move they would make given the current state of the game.
    Pair<Integer, Integer> move =  aiPlayer1.nextMove(game.getState());
    String results =
            UTTTGUI.GSON.toJson(ImmutableMap.of("hintBigBoard", move.getLeft(), "hintSmallBoard",
            move.getRight()));
    // Construct a message for what move the ai would make.
    JsonObject update = new JsonObject();
    update.addProperty("type", MessageType.UPDATE.ordinal());
    update.addProperty("handler", "hint");
    update.addProperty("state", results);
    // Send the message to all the sessions stored/associated with this game room.
    for (Session session : roomSessions) {
      session.getRemote().sendString(UTTTGUI.GSON.toJson(update));
    }
  }

  /**
   * Method used to try and connect a session to this game room.
   * @param inputSession - the session we want to try and connect to the room
   * @return - a boolean indicating whether the session was successfully connected
   *    to the game room.
   * @throws IOException - throws exception if cannot send something to
   *         the session passed in.
   */
  public boolean connectGame(Session inputSession) throws IOException {
    try {
      // Check if the room is not full already
      if (roomSessions.size() == 1) {
        roomSessions.add(inputSession);

        // Construct a message with all the game information
        ImmutableMap.Builder<String, Object> output = gameStateToHashmap();

        JsonObject update = new JsonObject();
        update.addProperty("type", MessageType.UPDATE.ordinal());
        update.addProperty("handler", "connectGame");
        update.addProperty("state", UTTTGUI.GSON.toJson(output.build()));

        // Send the message to the session who has just connected to the room.
        inputSession.getRemote().sendString(UTTTGUI.GSON.toJson(update));

        // Construct a message to the session who was already here that the other
        //    user has connected.
        JsonObject userConnected = new JsonObject();
        userConnected.addProperty("type", MessageType.UPDATE.ordinal());
        userConnected.addProperty("handler", "userConnected");
        // We checked above that roomSessions is already 1 and we added a session
        //    so will not produce nullPointerException
        roomSessions.peek().getRemote().sendString(UTTTGUI.GSON.toJson(userConnected));
        return true;
      }
      return false;
    } catch (ClassCastException | IllegalStateException e) {
      return false;
    }
  }

  /**
   * Method used to send a message to the session left in the game room that the
   *    input session has disconnected.
   * @param inputSession - session that has disconnected from the game room.
   * @throws IOException - throws exception if cannot send something to
   *            the session passed in.
   */
  public void sendDisconnectedMessage(Session inputSession) throws IOException {
    if (roomSessions.size() == 2) {
      roomSessions.remove(inputSession);
      JsonObject userDisconnected = new JsonObject();
      userDisconnected.addProperty("type", MessageType.UPDATE.ordinal());
      userDisconnected.addProperty("handler", "userDisconnected");
      System.out.println("Send Disconnect Message: " + UTTTGUI.GSON.toJson(userDisconnected));
      // We checked above that roomSessions is already 1 and we added a session so will not produce
      //    nullPointerException
      roomSessions.peek().getRemote().sendString(UTTTGUI.GSON.toJson(userDisconnected));
    }
  }
}
