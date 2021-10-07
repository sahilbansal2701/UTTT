import {conn} from "./index";



const MESSAGE_TYPE = {
    CONNECT: 0,
    UPDATE: 1,
    MESSAGE: 2
};
/**
 * Post request for starting the game
 * @param diff1 the difficulty of the computer 1 player
 * @param diff2 the difficulty of the computer 2 player
 * @param numAI the number of AI players in a game
 */
export function CreateGame(diff1, diff2, numAI){
    const toSend = {
        difficulty1: diff1,
        difficulty2: diff2,
        numAIPlayers: numAI,
        handler: "createGame",
        type: MESSAGE_TYPE.MESSAGE

    };
    console.log("create game message sent")
    conn.send(JSON.stringify(toSend))

}

/**
 * Post request to apply the human move made on the front end
 * @param bb the move on the big board
 * @param sb the move on the small board
 * @param gameNumber the room number of the game
 */
export function ApplyHumanMove(bb, sb, gameNumber){
    const toSend = {
        bigBoard: bb,
        smallBoard: sb,
        gameNumber: gameNumber,
        handler: "applyHumanMove",
        type: MESSAGE_TYPE.MESSAGE

    };
    console.log("apply human move message sent")
    conn.send(JSON.stringify(toSend))

}

/**
 * Post request to get the AI player's move
 * @param gameNumber the room number to play in
 */
export function AIMove(gameNumber){
    const toSend = {
        gameNumber: gameNumber,
        handler: "AIMove",
        type: MESSAGE_TYPE.MESSAGE

    };
    console.log(" ai move message sent")
    conn.send(JSON.stringify(toSend))

}

/**
 * The Post request to get the hint for a player
 * @param gameNumber the game room to get the hint for
 */
export function Hint(gameNumber){
    const toSend = {
        gameNumber: gameNumber,
        handler: "hint",
        type: MESSAGE_TYPE.MESSAGE
    };
    console.log("hint message sent")
    conn.send(JSON.stringify(toSend))
}

/**
 * The Post request to send the room number
 * @param gameNumber the game room to get the hint for
 */
export function connectGame(gameNumber){
    const toSend = {
        gameNumber: gameNumber,
        handler: "connectGame",
        type: MESSAGE_TYPE.MESSAGE
    };
    console.log("connect game message sent")
    conn.send(JSON.stringify(toSend))
}
/**
 * The Post request to make websocket stay alive
 */
export function stayingAlive(){
    const toSend = {
        handler: "stayingAlive",
        type: MESSAGE_TYPE.MESSAGE
    };
    console.log("staying alive")
    conn.send(JSON.stringify(toSend))
}
