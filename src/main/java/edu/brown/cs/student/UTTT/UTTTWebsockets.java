package edu.brown.cs.student.UTTT;


import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketError;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

/**
 * Class that holds all the logic for websockets to connect the backend and
 *    frontend.
 */
@WebSocket
public class UTTTWebsockets {
  private static final Gson GSON = new Gson();
  private static final ConcurrentHashMap<Session, Integer> SESSIONS = new ConcurrentHashMap<>();
  public static final ConcurrentHashMap<Integer, GameAIRoom> ROOMS = new ConcurrentHashMap<>();
  private static final int MAXROOMNUMBER = 1000000000;

  /**
   * Message types used by the front end.
   */
  private enum MessageType {
    CONNECT,
    UPDATE,
    SEND
  }

  /**
   * Constructor for the class.
   */
  public UTTTWebsockets() { }

  /**
   * Method that says what to do when a websocket connects.
   * @param session - the session that connected.
   * @throws IOException - throws exception if cannot send something to
   *         the session passed in.
   */
  @OnWebSocketConnect
  public void connected(Session session) throws IOException {
    // When a session connects, tell the session it is connected.
    JsonObject output = new JsonObject();
    output.addProperty("type", MessageType.CONNECT.ordinal());
    session.getRemote().sendString(GSON.toJson(output));
  }

  /**
   * Method that says what to do when a websocket closes.
   * @param session - the session that was closed
   * @param statuscode - the statuscode for its closing
   * @param reason - the reason why it closed.
   * @throws IOException - throws exception if cannot send something to
   *           the session passed in.
   */
  @OnWebSocketClose
  public void close(Session session, int statuscode, String reason) throws IOException {
    // When a session closes remove it from the sessions hashmap,
    //    then remove the gameRoom it is associated to.
    if (SESSIONS.containsKey(session)) {
      int gameRoomId = SESSIONS.get(session);
      if (ROOMS.containsKey(gameRoomId)) {
        GameAIRoom gameRoom = ROOMS.get(gameRoomId);
        gameRoom.sendDisconnectedMessage(session);
        ROOMS.remove(gameRoomId);
      }
      SESSIONS.remove(session);
    }
  }

  /**
   * Method that says what to do when a websocket errors/closese.
   * @param session - the session that errored
   * @param cause - why the session errored.
   * @throws IOException - throws exception if cannot send something to
   *             the session passed in.
   */
  @OnWebSocketError
  public void error(Session session, Throwable cause) throws IOException {
    // When a session errors remove it from the sessions hashmap,
    //    then remove the gameRoom it is associated to.
    if (SESSIONS.containsKey(session)) {
      int gameRoomId = SESSIONS.get(session);
      if (ROOMS.containsKey(gameRoomId)) {
        GameAIRoom gameRoom = ROOMS.get(gameRoomId);
        gameRoom.sendDisconnectedMessage(session);
        ROOMS.remove(gameRoomId);
      }
      SESSIONS.remove(session);
    }
  }

  /**
   * Method that says what to do when a websocket sends a message.
   * @param session - the session that sent a message
   * @param message - the message that was sent.
   * @throws IOException - throws exception if cannot send something to
   *               the session passed in.
   */
  @OnWebSocketMessage
  public void message(Session session, String message) throws IOException {
    JsonObject input = GSON.fromJson(message, JsonObject.class);

    if (session != null) {
      // Check which handler sent a message.
      if (input.get("handler").getAsString().equals("createGame")) {
        // Check if session already has room, if it does clean it up
        Integer checkGameNumber = SESSIONS.get(session);
        if (checkGameNumber != null) {
          GameAIRoom oldRoom = ROOMS.get(checkGameNumber);
          if (oldRoom != null) {
            oldRoom.sendDisconnectedMessage(session);
            ROOMS.remove(checkGameNumber);
          }
          SESSIONS.remove(session);
        }
        // If we are creating a game, make a game room, give it an id, and store the room
        //    in connection to the session.
        UltimateTicTacToe uttt = new UltimateTicTacToe();
        // Generate the id for the new game room. Make sure no other room already has this id.
        int gameNumber = ThreadLocalRandom.current().nextInt(0, MAXROOMNUMBER);
        while (ROOMS.containsKey(gameNumber)) {
          gameNumber = ThreadLocalRandom.current().nextInt(0, MAXROOMNUMBER);
        }
        GameAIRoom newRoom = new GameAIRoom(uttt, gameNumber);
        newRoom.createGame(input, session);
        SESSIONS.put(session, gameNumber);
        ROOMS.put(gameNumber, newRoom);
      } else if (input.get("handler").getAsString().equals("applyHumanMove")) {
        // If we were given a move by a human apply the move to the game
        //    associated to the session.
        int roomNumber = input.get("gameNumber").getAsInt();

        GameAIRoom currentRoom = ROOMS.get(roomNumber);
        currentRoom.applyHumanMove(input);
      } else if (input.get("handler").getAsString().equals("AIMove")) {
        // If we want the AI to make a move, for the game associated to the
        //    session ask the ai to make a move.
        int roomNumber = input.get("gameNumber").getAsInt();
        GameAIRoom currentRoom = ROOMS.get(roomNumber);
        currentRoom.applyAIMove();
      } else if (input.get("handler").getAsString().equals("hint")) {
        // If we want a hint call a method in the appropriate game associated
        //    to the session for a hint.
        int roomNumber = input.get("gameNumber").getAsInt();
        GameAIRoom currentRoom = ROOMS.get(roomNumber);
        currentRoom.hint();
      } else if (input.get("handler").getAsString().equals("connectGame")) {
        // If a user attempts to connect to a game room.
        int roomNumber = input.get("gameNumber").getAsInt();
        GameAIRoom currentRoom = ROOMS.get(roomNumber);
        // Check if the gameRoom exists, then try to connect them to the room.
        //    if it fails tell them it failed.
        if (currentRoom == null) {
          JsonObject update = new JsonObject();
          update.addProperty("type", MessageType.UPDATE.ordinal());
          update.addProperty("handler", input.get("handler").getAsString());
          update.addProperty("state", "undefined");
          session.getRemote().sendString(GSON.toJson(update));
        } else if (!currentRoom.connectGame(session)) {
          JsonObject update = new JsonObject();
          update.addProperty("type", MessageType.UPDATE.ordinal());
          update.addProperty("handler", input.get("handler").getAsString());
          update.addProperty("state", "undefined");
          session.getRemote().sendString(GSON.toJson(update));
        } else {
          SESSIONS.put(session, roomNumber);
        }
      } else if (input.get("handler").getAsString().equals("stayingAlive")) {
        // A message used to keep websockets alive for the duration of the time the
        //    webpage is open. Basically a message that is received and sent back.
        JsonObject update = new JsonObject();
        update.addProperty("type", MessageType.UPDATE.ordinal());
        update.addProperty("handler", input.get("handler").getAsString());
        session.getRemote().sendString(GSON.toJson(update));
      }
    }
  }
}

