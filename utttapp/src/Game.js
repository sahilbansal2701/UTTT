import 'bootstrap/dist/css/bootstrap.min.css';
import Card from "react-bootstrap/Card";
import Button from "react-bootstrap/Button"
import React, {useRef, useEffect, useState, useCallback} from 'react';
import {CreateGame, ApplyHumanMove, Hint, AIMove, stayingAlive} from "./PostRequest";
import {Alert, Modal} from "react-bootstrap";
import {conn} from "./index.js";



// length of single side of square canvas of ttt board
const DIMENSION = Math.floor((window.innerHeight/11) * 9)
// variable for the canvas, used to draw uttt board
let ctx;
let canvas;
let canvasRef;
// variables to hold the results of all post requests
let nextBoardToMove; // the small board the next valid move can be made on
let board; // the current state of the board (with all pieces on it)
let legalMoves; // a list of legal positions to place the next move
// boolean on whether the newGame button was pressed (used to control when the modal pops up)
let newGame = false;
// boolean whether the hint button was clicked (used to draw over hints)
// let hintUsed = false;
// variables for timing out a click (to prevent double clicking on the board during one move)
let time;
let timeoutTime = 400;
// variables to hold sizing for the uttt board, scaled to fit the screen
const square = Math.floor(window.innerHeight/11) // the size of a single small square
const xoffset = Math.floor(window.innerHeight/41) // how much to offset x by when drawing tokens
const yoffset = Math.floor(window.innerHeight/16) // how much to offset x by when drawing tokens
// enum to indicate numbers for all status
const STATUS = {
    ONGOINGP1: 0,
    ONGOINGP2: 1,
    WINP1: 2,
    WINP2: 3,
    DRAW: 4
};
const MESSAGE_TYPE = {
    CONNECT: 0,
    UPDATE: 1,
    MESSAGE: 2
};
function Game(props) {
    // variable to hold to hold the status of the board resulting from a post request
    let status = useRef(0);
    // variable for the canvas to draw uttt board
    canvasRef = useRef(null);
    // variables holding the current moving player
    let currentPlayerRef = useRef("");
    const [currentPlayer, setCurrentPlayer] = useState("");
    const whoAmI = useRef("Player 1")
    // variables for end game modal
    const [show, setShow] = useState(false);
    const [winMessage, setWinMessage] = useState("");
    const handleClose = () => setShow(false);
    const handleShow = () => setShow(true);
    // variables for showing the hint button (based on which players are selected)
    const [hintButton, setHintButton] = useState(<div/>)
    // variable to indicate the game room
    const gameNumberRef = useRef("")
    // variable for error message modal
    const [errorShow, setErrorShow] = useState(false);
    const handleErrorClose = () => setErrorShow(false);
    const handleErrorShow = () => setErrorShow(true);

    // variable for connection error message modal
    const [connErrorShow, setConnErrorShow] = useState(false);
    const handleConnErrorClose = () => setConnErrorShow(false);
    const handleConnErrorShow = () => setConnErrorShow(true);

    const [roomErrorShow, setRoomErrorShow] = useState(false);
    const handleRoomErrorClose = () => setRoomErrorShow(false);
    const handleRoomErrorShow = () => setRoomErrorShow(true);
    // variable to hold the board that was used to make the previous hint
    let oldHintBoard = [];
    // button for copying room number
    let clipboard = <div/>;
    const [clip, setClipboard] = useState(<div/>);

    // Used for alert for user connected
    const [userConnectedShow, setUserConnectedShow] = useState(false);
    // Used for alert for user diconnected
    const [userDisconnectedShow, setUserDisconnectedShow] = useState(false);

    useEffect( () => {
        conn.addEventListener("message", messageHandle);
    },[])
    /**
     * Function to create the grid for a uttt board.
     */
    const makeBoard = () =>{
        ctx.beginPath();
        ctx.strokeStyle = "black";
        ctx.lineWidth = 2;
        ctx.moveTo(square,10)
        ctx.lineTo(square, DIMENSION - 10)
        ctx.moveTo(square*2, 10)
        ctx.lineTo(square*2, DIMENSION - 10)
        ctx.moveTo(square*4, 10)
        ctx.lineTo(square*4, DIMENSION - 10)
        ctx.moveTo(square*5, 10)
        ctx.lineTo(square*5, DIMENSION - 10)
        ctx.moveTo(square*7, 10)
        ctx.lineTo(square*7, DIMENSION - 10)
        ctx.moveTo(square*8, 10)
        ctx.lineTo(square*8, DIMENSION - 10)
        ctx.moveTo(10, square)
        ctx.lineTo(DIMENSION - 10, square)
        ctx.moveTo(10, square*2)
        ctx.lineTo(DIMENSION - 10, square*2)
        ctx.moveTo(10, square*4)
        ctx.lineTo(DIMENSION - 10, square*4)
        ctx.moveTo(10, square*5)
        ctx.lineTo(DIMENSION - 10, square*5)
        ctx.moveTo(10, square*7)
        ctx.lineTo(DIMENSION - 10, square*7)
        ctx.moveTo(10, square*8)
        ctx.lineTo(DIMENSION - 10, square*8)
        ctx.stroke();
        // draw the large uttt board
        drawOuterBox();
    }

    /**
     * Function to draw the skeleton for the big uttt board.
     */
    const drawOuterBox = () => {
        // draw white grid to create space between small boards
        ctx.beginPath();
        ctx.strokeStyle = "white"
        ctx.lineWidth = 20;
        ctx.moveTo(square*3, 0)
        ctx.lineTo(square*3, DIMENSION)
        ctx.moveTo(square*6, 0)
        ctx.lineTo(square*6, DIMENSION )
        ctx.moveTo(0, square*3)
        ctx.lineTo(DIMENSION, square*3)
        ctx.moveTo(0, square*6)
        ctx.lineTo(DIMENSION, square*6)
        ctx.moveTo(0, 0)
        ctx.lineTo(0, DIMENSION)
        ctx.lineTo(DIMENSION, DIMENSION)
        ctx.lineTo(DIMENSION, 0)
        ctx.lineTo(0,0)
        ctx.stroke();
        // draw actual grey lines of skeleton grid
        ctx.beginPath();
        ctx.strokeStyle = "#c8c8c8"
        ctx.lineWidth = 2;
        ctx.moveTo(square*3, 0)
        ctx.lineTo(square*3, DIMENSION)
        ctx.moveTo(square*6, 0)
        ctx.lineTo(square*6, DIMENSION )
        ctx.moveTo(0, square*3)
        ctx.lineTo(DIMENSION, square*3)
        ctx.moveTo(0, square*6)
        ctx.lineTo(DIMENSION, square*6)
        ctx.stroke();
    }

    /**
     * Function to convert canvas coordinates to row, col indicating which square the click was on.
     * Must be calculated because the tokens must be placed on a centered area of the board.
     * @param x The x-coordinate on the canvas
     * @param y The y-coordinate on the canvas
     * @returns {(number|number)[]} row, col to be centered on canvas and normal row, col based on grids.
     */
    const canvasToSquare = (x, y) => {
        const col = Math.floor(x/square);
        const row = Math.floor(y/square);
        return [row*square,col*square, row, col]
    }

    /**
     * Function to convert row, col values on grid to Board coordinates.
     * Needed to return BB, SB in post requests.
     * @param row The row of the grid.
     * @param col The col of the grid.
     * @returns {[number, number]} The big uttt slot the token is on and the small ttt slot the token is on.
     */
    const rowColToBoards = (row, col) => {
        let BB;
        let SB;
        // determine which big board the square is in
        if (col >= 0 && col <= 2 && row >= 0 && row <= 2) {
            BB = 0;
        } else if (col >= 3 && col <= 5 && row >= 0 && row <= 2) {
            BB = 1;
        } else if (col >= 6 && col <= 8 && row >= 0 && row <= 2) {
            BB = 2;
        } else if (col >= 0 && col <= 2 && row >= 3 && row <= 5) {
            BB = 3;
        } else if (col >= 3 && col <= 5 && row >= 3 && row <= 5) {
            BB = 4;
        } else if (col >= 6 && col <= 8 && row >= 3 && row <= 5) {
            BB = 5;
        } else if (col >= 0 && col <= 2 && row >= 6 && row <= 8) {
            BB = 6;
        } else if (col >= 3 && col <= 5 && row >= 6 && row <= 8) {
            BB = 7;
        } else if (col >= 6 && col <= 8 && row >= 6 && row <= 8) {
            BB = 8;
        } else {
            console.log("ERROR: row, col not within bounds when converting to boards.");
        }
        SB = ((row % 3) * 3) + (col % 3)
        return [BB, SB]
    }

    /**
     * Function to draw an X token on the board.
     * @param x The centered x-coordinate the token should be on.
     * @param y The centered y-coordinate the token should be on.
     * @param fsize The size of the token (changes depending on screen size)
     * @param color The color of the token.
     */
    const drawX = (x, y, fsize, color) => {
        ctx.font = fsize;
        ctx.fillStyle = color;
        ctx.fillText("x", x + xoffset, y + yoffset);
    }

    /**
     * Function to draw an O token on the board.
     * @param x The centered x-coordinate the token should be on.
     * @param y The centered y-coordinate the token should be on.
     * @param fsize The size of the token (changes depending on screen size)
     * @param color The color of the token.
     */
    const drawO = (x, y, fsize, color) => {
        ctx.font = fsize;
        ctx.fillStyle = color;
        ctx.fillText("o", x + xoffset, y + yoffset);
    }

    // callback to change the current player for status bar rendering
    const setPlayerState = useCallback(ref => {
        ref.current === "Player 1" ? setCurrentPlayer("Player 1") : setCurrentPlayer("Player 2");
    });

    /**
     * Function that determines if a move made is valid.
     * @param BB The big slot the move was made on.
     * @param SB The small slot the move was made on.
     * @returns {boolean} Returns true if move valid, false if not.
     */
    function moveValid(BB, SB) {
        let legal = false;
        // if the move was made on the next board destined for a move
        if (nextBoardToMove === 9 || nextBoardToMove === BB) {
            // iterate through the list of possible moves and determine if the move made is there.
            for (const [index, pair] of legalMoves.entries()) {
                if ((pair["slot"] === SB) && (pair["board"] === BB)) {
                    legal = true;
                }
            }
        }
        return legal;
    }

    /**
     * Function that converts board values (BB, SB) to row, col.
     * @param BB The big slot the token is on.
     * @param SB The small slot the token is on.
     * @returns {(number|number)[]}
     * @constructor
     */
    function BoardToRowCol(BB, SB) {
        let row, col;
        // determine upper left corner row,col of small board based on BB and SB
        if (BB === 0) {
            row = 0;
            col = 0;
        } else if (BB === 1) {
            row = 0;
            col = 3;
        } else if (BB === 2) {
            row = 0;
            col = 6;
        } else if (BB === 3) {
            row = 3;
            col = 0;
        } else if (BB === 4) {
            row = 3;
            col = 3;
        } else if (BB === 5) {
            row = 3;
            col = 6;
        } else if (BB === 6) {
            row = 6;
            col = 0;
        } else if (BB === 7) {
            row = 6;
            col = 3;
        } else if (BB === 8) {
            row = 6;
            col = 6;
        } else {
            console.log("ERROR: BB of AI move invalid");
        }
        // determine final col
        col = col + (SB % 3);
        //determine final row
        if (SB === 0 || SB === 1 || SB === 2) {
            row = row + 0;
        } else if (SB === 3 || SB === 4 || SB === 5) {
            row = row + 1;
        } else if (SB === 6 || SB === 7 || SB === 8) {
            row = row + 2;
        } else {
            console.log("ERROR: SB of AI move invalid");
        }
        return [row*square, col*square, row, col];
    }

    /**
     * Draws all the pieces that should currently exist on the board
     * @param board
     */
    const drawXandO = (board) => {
        const fontSize = `${square*0.85}px sans-serif`;
        for(let i = 0; i < 9; i++){
            for(let j = 0; j < 9; j++){
                if(board[i][j] === 1) {
                    const rowCol = BoardToRowCol(i,j)
                    drawX(rowCol[1], rowCol[0],fontSize, '#4082bc')
                } else if (board[i][j] === -1) {
                    const rowCol = BoardToRowCol(i,j)
                    drawO(rowCol[1], rowCol[0],fontSize, '#4082bc')
                }

            }
        }
    }
    /**
     * Function that makes an error modal appear if a post request returns undefined.
     * @param newStatus Result of post request.
     * @returns {boolean} True if the status should error, false if there is no error.
     */
    const errorModal = (newStatus) => {
        if (newStatus === undefined) {
            // clear the game
            clearGame();
            // make the error modal show up
            handleErrorShow();
            return true;
        }
        return false;
    }
    /**
     * Function that handles the message returned by the server
     * @param message
     */
    const messageHandle = (message) => {
        if (conn.readyState === WebSocket.CLOSED) {
            handleConnErrorShow();
        }
        let object = JSON.parse(message.data)
        if(object.type === MESSAGE_TYPE.CONNECT){
            stayingAlive();
        }
        if(object.type !== MESSAGE_TYPE.CONNECT){
            switch (object.handler) {
                case "createGame":
                    let create = JSON.parse(object.state)
                    gameNumberRef.current = create["gameNumber"]
                    status.current = create["status"];
                    nextBoardToMove = create["nextBoardToMove"];
                    board = create["board"];
                    legalMoves = create["legalMoves"];
                    runGameFunction(status.current)
                    break;
                case "applyHumanMove":
                    let human = JSON.parse(object.state)
                    status.current = human["status"];
                    nextBoardToMove = human["nextBoardToMove"];
                    board = human["board"];
                    legalMoves = human["legalMoves"];
                    drawXandO(board)
                    drawBigPieces(board)
                    ApplyHumanMoveFunction(status.current);
                    break;
                case "AIMove":
                    let aiResult = JSON.parse(object.state)
                    status.current = aiResult["status"];
                    nextBoardToMove = aiResult["nextBoardToMove"];
                    board = aiResult["board"];
                    legalMoves = aiResult["legalMoves"];
                    const aiMove = aiResult["aiMove"];
                    drawBigPieces(board)
                    new AIMoveFunction(aiMove, status.current)
                    break;
                case "hint":
                    let hintResult = JSON.parse(object.state)
                    let BB = hintResult["hintBigBoard"]
                    let SB = hintResult["hintSmallBoard"]
                    new hint(BB, SB, status.current)
                    break;
                case "connectGame":
                    let results = object.state

                    if(results !== "undefined"){
                        let connect = JSON.parse(object.state)
                        status.current = connect["status"];
                        nextBoardToMove = connect["nextBoardToMove"];
                        board = connect["board"];
                        legalMoves = connect["legalMoves"];
                        gameNumberRef.current = props.roomNumRef.current
                        whoAmI.current = "Player 2"
                        props.p1.current = "Human"
                        props.p2.current = "Human"
                        props.c1.current = ""
                        props.c2.current = ""
                        currentPlayerRef.current = "Player 1"
                        setPlayerState(currentPlayerRef)
                        canvasRef.current.removeEventListener("click", handleClick, false);
                        canvasRef.current.addEventListener("click", function(event) {
                            event.preventDefault();
                            clearTimeout(time);
                            time = setTimeout(handleClick, timeoutTime, event);
                        }, false);
                        //run game
                        runGameFunction(status.current)
                    } else if(props.roomNumRef.current !== gameNumberRef.current){
                        //make a modal to show error if no room found
                        handleRoomErrorShow()
                    }
                    break;
                case "stayingAlive":
                    setTimeout(stayingAlive, 10000);
                    break;
                case "userConnected":
                    setUserConnectedShow(true)
                    break;
                case "userDisconnected":
                    setUserConnectedShow(false)
                    setUserDisconnectedShow(true)
                    break;
                default:
                    console.log("Unexpected Message!");
                    break;
            }
        }
    }

    /**
     * Function that executes the required actions after the apply human move message has been received.
     * @param newStatus the status of the board
     * @returns {*}
     */

    function ApplyHumanMoveFunction(newStatus) {
        // if the hint button was clicked, draw over the hint and set the boolean back
        makeBoard()

        // if the value returned from the post request is not undefined
        if (!errorModal(newStatus)) {
            drawXandO(board)
            highlightBoard(nextBoardToMove);
            drawBigPieces(board)
            if (currentPlayerRef.current === "Player 1" && status.current !== STATUS.WINP1
                && status.current !== STATUS.WINP2 && status.current !== STATUS.DRAW){
                currentPlayerRef.current = "Player 2"
                setPlayerState(currentPlayerRef)
                if(props.p2.current === "Computer") {
                    AIMove(gameNumberRef.current)
                }
            } else if (currentPlayerRef.current === "Player 2" && status.current !== STATUS.WINP1
                && status.current !== STATUS.WINP2 && status.current !== STATUS.DRAW) {
                currentPlayerRef.current = "Player 1"
                setPlayerState(currentPlayerRef)
                if(props.p1.current === "Computer") {
                    AIMove(gameNumberRef.current)
                }
            }
                // if the move resulted in a win, make the results modal pop up.
            if(status.current === STATUS.WINP1 || status.current === STATUS.WINP2 || status.current === STATUS.DRAW) {
                showHintButton(<div/>)
                showResults();
            }
        }
        return newStatus;
    }

    /**
     * Function executes the necessary actions for an ai player
     */
     function AIMoveFunction(aiMove,aiResult) {
        if (conn.readyState === WebSocket.CLOSED) {
            handleConnErrorShow();
        }
        // if the result of the post request is not undefined
        if (!errorModal(aiResult)) {
            // computer which slot the AI move should be made on
            const BB = aiMove["board"];
            const SB = aiMove["slot"]
            const squares = BoardToRowCol(BB, SB);
            const fontSize = `${square * 0.85}px sans-serif`;
            // if the current player is player 1, draw an X, else, draw an O
            if (currentPlayerRef.current === "Player 1"){
                drawX(squares[1], squares[0], fontSize, '#4082bc');
                currentPlayerRef.current = "Player 2";
                // sets the player state, wraps the setter in a callback
                setPlayerState(currentPlayerRef);
                // highlight the next board for the next move
                highlightBoard(nextBoardToMove)
                drawBigPieces(board)
                if (status.current !== STATUS.WINP1
                    && status.current !== STATUS.WINP2 && status.current !== STATUS.DRAW && newGame === false ) {
                    if(props.p2.current === "Computer") {
                        AIMove(gameNumberRef.current)
                    }

                }
            } else if(currentPlayerRef.current === "Player 2"){
                drawO(squares[1], squares[0], fontSize, '#4082bc');
                currentPlayerRef.current = "Player 1";
                // sets the player state, wraps the setter in a callback
                setPlayerState(currentPlayerRef);
                // highlight the next board for the next move
                highlightBoard(nextBoardToMove)
                drawBigPieces(board)
                if(status.current !== STATUS.WINP1
                    && status.current !== STATUS.WINP2 && status.current !== STATUS.DRAW && newGame === false) {
                    if(props.p1.current === "Computer"){
                        AIMove(gameNumberRef.current)
                    }
                }
            }
            if (newGame) {
                ctx.fillStyle = "white"
                ctx.fillRect(0, 0, ctx.canvas.width, ctx.canvas.height);
                showHintButton(<div/>)
            }

            // if the move resulted in a win, make the results modal pop up.
            if (status.current === STATUS.WINP1 || status.current === STATUS.WINP2 || status.current === STATUS.DRAW) {
                showHintButton(<div/>)
                showResults();
            }
        }
        return aiResult;
     }

    /**
     * Function to highlight the next valid board to make a move on.
     * @param markedBoard The board marked with what board the move should be made on.
     */
    function highlightBoard(markedBoard) {
        // redraw the board to get rid of any past highlights
        makeBoard();
        // redraw the big pieces because the grid would have been drawn over it
        drawBigPieces(board)
        ctx.beginPath()
        ctx.lineWidth = 3
        ctx.strokeStyle = "#02b5fc"
        // iterate through each smaller board and draw a border around the appropriate board
        if (markedBoard === 0){
            ctx.moveTo(3,3)
            ctx.lineTo(square*3, 3)
            ctx.lineTo(square*3, square*3)
            ctx.lineTo(3, square*3)
            ctx.lineTo(3,3)

        } else if (markedBoard === 1) {
            ctx.moveTo(square*3, 3)
            ctx.lineTo(square*6, 3)
            ctx.lineTo(square*6, square*3)
            ctx.lineTo(square*3, square*3)
            ctx.lineTo(square*3,3)
        } else if (markedBoard === 2) {
            ctx.moveTo(square*6, 3)
            ctx.lineTo(square*9, 3)
            ctx.lineTo(square*9, square*3)
            ctx.lineTo(square*6, square*3)
            ctx.lineTo(square*6,3)

        } else if (markedBoard === 3) {
            ctx.moveTo(3, square*3)
            ctx.lineTo(square*3, square*3)
            ctx.lineTo(square*3, square*6)
            ctx.lineTo(3, square*6)
            ctx.lineTo(3,square*3)
        } else if (markedBoard === 4) {
            ctx.moveTo(square*3, square*3)
            ctx.lineTo(square*6, square*3)
            ctx.lineTo(square*6, square*6)
            ctx.lineTo(square*3, square*6)
            ctx.lineTo(square*3,square*3)
        } else if (markedBoard === 5) {
            ctx.moveTo(square*6, square*3)
            ctx.lineTo(square*9, square*3)
            ctx.lineTo(square*9, square*6)
            ctx.lineTo(square*6, square*6)
            ctx.lineTo(square*6,square*3)

        } else if (markedBoard === 6) {
            ctx.moveTo(3, square*6)
            ctx.lineTo(square*3, square*6)
            ctx.lineTo(square*3, square*9 )
            ctx.lineTo(3, square*9)
            ctx.lineTo(3,square*6)

        } else if (markedBoard === 7) {
            ctx.moveTo(square*3, square*6)
            ctx.lineTo(square*6, square*6)
            ctx.lineTo(square*6, square*9)
            ctx.lineTo(square*3, square*9)
            ctx.lineTo(square*3,square*6)

        } else if (markedBoard === 8) {
            ctx.moveTo(square*6, square*6)
            ctx.lineTo(square*9, square*6)
            ctx.lineTo(square*9, square*9)
            ctx.lineTo(square*6, square*9)
            ctx.lineTo(square*6,square*6)
        } else if (markedBoard === 9) {
            ctx.moveTo(3, 3)
            ctx.lineTo(square*9, 3)
            ctx.lineTo(square*9, square*9)
            ctx.lineTo(3, square*9)
            ctx.lineTo(3,3)
        }
        ctx.stroke();
    }

    /**
     * Function to draw the big pieces on the uttt board.
     */
    const drawBigPieces = (board) => {
        //-1 player 2 won, 1 player 1 won
        let winStatus =  board[9];
        const fontSize = `${square*4.7}px sans-serif`;
        // iterate through the smaller boards
        for (let i = 0; i < winStatus.length; i++) {
            // if there is a win for player 1, draw an X
            if(winStatus[i] === 1) {
                const rowcol = BoardToRowCol(i, 6)
                drawX(rowcol[1] + 5, rowcol[0], fontSize, '#94c3dd')
                // if there is a win for player 2, draw an O
            } else if (winStatus[i] === -1) {
                const rowCol = BoardToRowCol(i, 6)
                drawO(rowCol[1], rowCol[0], fontSize, '#94c3dd')
            }
        }
    }

    /**
     * Set the message of the results modal depending on the status.
     * @type {function(*): void} The reference for the current status
     */
    const setWinMessageState= useCallback((ref) => {
        if(ref === STATUS.WINP1) {
            setWinMessage("Player 1 Wins!")
        } else if (ref === STATUS.WINP2) {
            setWinMessage("Player 2 Wins!")
        } else if (ref === STATUS.DRAW) {
            setWinMessage("Draw!")
        }
    });

    /**
     * Function to show the results modal
     */
    function showResults() {
        // set the current player to no player.
        currentPlayerRef.current = "";
        setPlayerState(currentPlayerRef);
        // set the winning message using the current status
        setWinMessageState(status.current)
        // make the modal pop up
        handleShow();
    }

    /**
     * Function to handle click on the canvas
     * @type {function(*): Promise<void>}
     */
    const handleClick = useCallback(async (event) => {
        if (conn.readyState === WebSocket.CLOSED) {
            handleConnErrorShow();
        }
        console.log("click")
        // determine the x and y coordinates on canvas where the board was clicked
        const boundClient = event.target.getBoundingClientRect();
        const clickX = event.clientX - boundClient.left;
        const clickY = event.clientY - boundClient.top;
        // convert the x and y coordinates to BB, SB, and centered board coordinates
        const squares = canvasToSquare(clickX, clickY);
        const convertedBoard = rowColToBoards(squares[2], squares[3]);
        const BB = convertedBoard[0];
        const SB = convertedBoard[1];
        const fontSize = `${square*0.85}px sans-serif`;
        // if there isn't a win or a draw, interpret the click
        if (status.current !== STATUS.WINP1 && status.current !== STATUS.WINP2 && status.current !== STATUS.DRAW) {
            // if player 1 made a VALID move and is human, show the move and send it to the back end
            console.log("who am I:", whoAmI.current)
            console.log("diff device:", props.diffDeviceRef.current)
            console.log("currentplayer:", currentPlayerRef.current)
            if (currentPlayerRef.current === "Player 1" && props.p1.current === "Human") {
                if ((props.diffDeviceRef.current && whoAmI.current === "Player 1") || (!props.diffDeviceRef.current)){
                    // if the move made was valid, interpret the move
                    if (moveValid(BB, SB)) {
                        // draw an outer box to get rid of any leftover highlighted boxes
                        drawOuterBox()
                        // draw the token on the appropriate square
                        drawX(squares[1], squares[0], fontSize, '#4082bc');
                        //sends the Apply Human move message
                        ApplyHumanMove(BB, SB, gameNumberRef.current)
                        highlightBoard(nextBoardToMove)

                    }
                }
            }
            // if the current player is a human and is player 2
            else if (currentPlayerRef.current === "Player 2" && props.p2.current === "Human") {
                if ((props.diffDeviceRef.current && whoAmI.current === "Player 2") || (!props.diffDeviceRef.current)){
                    // if the move made was valid
                    if (moveValid(BB, SB)) {
                        // draw outer box to get rid of residual highlighted boxes
                        drawOuterBox()
                        // draw token for the move
                        drawO(squares[1], squares[0], fontSize, '#4082bc');
                        // apply the human move and highlight the next board for the next move if there were no errors with the post request
                        ApplyHumanMove(BB, SB, gameNumberRef.current)
                        highlightBoard(nextBoardToMove)

                    }
                }
            }
        }

        }, [])

    /**
     * Function to clear the game whenever a change is made to settings or if "new game" button is clicked
     */
    const clearGame = () => {
        // set the current player to no player.
        currentPlayerRef.current = "";
        setPlayerState(currentPlayerRef);
        // remove the hint button
        showHintButton(<div/>)
        // draw over the old board
        ctx.fillStyle = "white"
        ctx.fillRect(0, 0, ctx.canvas.width, ctx.canvas.height);
        // remove any event listeners to prevent further clicking
        canvasRef.current.removeEventListener("click", handleClick, false);
        newGame = true;
        gameNumberRef.current = "" ;
    }

    /**
     * Send the hint message
     */
    const sendHintMessage = () => {
        const hint = new Hint(gameNumberRef.current);
    }


    /**
     * Function to draw a hint using post request
     */
    function hint(BB, SB, hintStatus) {
        // if the current player is human
        console.log("inside hint function")
        console.log("status", status.current)
        if((status.current === STATUS.ONGOINGP1 && props.p1.current === "Human") ||
            (status.current === STATUS.ONGOINGP2 && props.p2.current === "Human")) {
            console.log("first if in hint")
            if (oldHintBoard !== board) {
                console.log("second if in hint")
                // if there is no error with the message received
                if (!errorModal(hintStatus)) {
                    console.log("HERE")
                    // get and convert values for where the hint should appear
                    const rowCol = BoardToRowCol(BB, SB)
                    const x = rowCol[1]
                    const y = rowCol[0]
                    // hintUsed = true;
                    // draw the board to get rid of residual hints
                    makeBoard();
                    // highlight the next board
                    highlightBoard(nextBoardToMove);
                    // draw the hint
                    ctx.strokeStyle = "#09ff00"
                    ctx.lineWidth = 2
                    ctx.beginPath();
                    ctx.moveTo(x, y)
                    ctx.lineTo(x + square, y)
                    ctx.lineTo(x + square, y + square)
                    ctx.lineTo(x, y + square)
                    ctx.lineTo(x, y)
                    ctx.stroke()
                    drawBigPieces(board)
                }
                oldHintBoard = board;
            }
        }
    }

    /**
     *  callback to add or remove hint button
     * @type {function(*=): void}
     */
    const showHintButton = useCallback((ref) => {
        setHintButton(ref);
    })
    /**
     *  callback to add or remove clipboard button
     * @type {function(*=): void}
     */
    const showClipboard = useCallback((ref) => {
        setClipboard(ref);
    })
    const runGame = () =>{
        if (conn.readyState === WebSocket.CLOSED) {
            handleConnErrorShow();
        }
        // draw initial board
        makeBoard();
        // start initial game
        let create;
        // make post request depending on the selected options
        if (props.p1.current === "Human" && props.p2.current === "Human") {
            create = new CreateGame("", "", 0);
        } else if (props.p1.current === "Human" && props.p2.current === "Computer") {
            create = new CreateGame(props.c2.current, "", 1);
        } else if (props.p1.current === "Computer" && props.p2.current === "Human") {
            create = new CreateGame(props.c1.current, "", 1);
        } else if (props.p1.current === "Computer" && props.p2.current === "Computer") {
            create = new CreateGame(props.c1.current, props.c2.current, 2);
        }
    }
    /**
     * Function to set up the game and run a while loop for computer v computer games
     */
    function runGameFunction(create) {
        if (conn.readyState === WebSocket.CLOSED) {
            handleConnErrorShow();
        }
        // if the results of the post request are not undefined
        if (!errorModal(create)) {

            let buttonDisplay;
            // if the human v. computer, show the hint button
            if ((props.p1.current === "Human" && props.p2.current === "Human") ||
                (props.p1.current === "Computer" && props.p2.current === "Computer")) {
                buttonDisplay = <div/>
            } else {
                buttonDisplay = <div>
                    <Button variant="outline-primary" onClick={sendHintMessage}>Hint</Button>
                    <br/>
                    <br/>
                </div>
            }
            showHintButton(buttonDisplay)
            // highlight the board for the first move
            highlightBoard(nextBoardToMove)
            // if the first player is AI and the second player is Human,
            // start off game by making an ai move
            if (props.p1.current === "Computer" && props.p2.current === "Human") {
                new AIMove(gameNumberRef.current);

            }
            // if both players are AI, make a while loop to keep game running while the game is not ended.
            newGame = false;
            if((props.p1.current === "Computer") && (props.p2.current === "Computer")) {
                new AIMove(gameNumberRef.current);
            }
            //if the game has ended
            if (newGame) {
                ctx.fillStyle = "white"
                ctx.fillRect(0, 0, ctx.canvas.width, ctx.canvas.height);
                showHintButton(<div/>)
            }
        }
    }

    /**
     * useEffect for initial rendering of the board to set up canvas for drawing uttt board
     */
    useEffect( () => {
        canvas = canvasRef.current
        ctx = canvas.getContext('2d')
        ctx.canvas.width = DIMENSION;
        ctx.canvas.height = DIMENSION;
        console.log("message handler added")
    },[])


    /**
     * useEffect for rendering when create game button is pressed
     */
    useEffect( () => {
        const timer = setTimeout(() => {
            // if this is not the first time clicking startgame
            if (props.startgame > 0) {
                // add a click handler that can only be clicked every timeout (to prevent double clicking)
                console.log("click handler added")
                canvasRef.current.addEventListener("click", function(event) {
                    event.preventDefault();
                    clearTimeout(time);
                    time = setTimeout(handleClick, timeoutTime, event);
                }, false);
                // set down a white base for board
                ctx.fillStyle = "white"
                ctx.fillRect(0, 0, ctx.canvas.width, ctx.canvas.height);
                // set current player ot player 1
                currentPlayerRef.current = "Player 1";
                // sets the player state, wraps the setter in a callback
                setPlayerState(currentPlayerRef);
                // start the game
                runGame();
                if(props.diffDeviceRef.current) {
                    clipboard =  <div className="clipboardButton">
                        <Button onClick={() => {navigator.clipboard.writeText(gameNumberRef.current)}} variant="outline-primary">Copy Room</Button>
                    </div>
                } else {
                    clipboard = <div/>;
                }
                showClipboard(clipboard);
            }
        }, 1000);
        return () => clearTimeout(timer);
    },[props.startgame])

    /**
     * useEffect for clearing the uttt board whenever an option is changed
     */
    useEffect(() =>{
        clearGame();
    }, [props.optionschangeref.current])

    return (
        <div className="Game">
            <h2 className="gameheader">Game</h2>
            <div className= "gametext">
                <div className = "statusbar">
                    <Card style={{ borderColor: '#4082bc', borderWidth: '2px', borderRadius: '25px'}}>
                        <Card.Header style={{ background: '#4082bc', color: 'white', textAlign: 'left', paddingTop: '4%',
                            borderTopLeftRadius: '20px', borderTopRightRadius: '20px', paddingBottom: '2%'}}>
                            <Card.Title style={{fontSize:'calc(6px + 2vmin)'}}>Status</Card.Title>
                        </Card.Header>
                        <Card.Body>
                            <h5 className="gameRoom"> {(props.diffDeviceRef.current) ? "You are: " + (whoAmI.current) : ""}</h5>
                            <h5 className="gameRoom"> {(props.diffDeviceRef.current) ? "Room: " + (gameNumberRef.current) : ""}</h5>
                            {clip}
                            <hr className="divide"/>
                            <Card.Text style ={{textAlign: 'left'}}>

                                Waiting for move from: {currentPlayerRef.current}<br/>
                                <br/>
                                Player 1 (X): {props.p1.current} {(props.c1.current !== "") ? "(" + props.c1.current +")" : ""}
                                <br/>
                                <br/>
                                Player 2 (O): {props.p2.current} {(props.c2.current !== "") ? "(" + props.c2.current +")" : ""}
                                <br/>

                            </Card.Text>
                            <br/>
                            {hintButton}
                            <Button variant="primary" href = "/#options" onClick = {clearGame}>New Game</Button>
                        </Card.Body>
                    </Card>
                    <Alert show={userConnectedShow} variant="success" style={{marginTop: '4%', borderRadius: '25px'}}>
                        <Alert.Heading>Other Player Has Connected!</Alert.Heading>
                        <div className="d-flex justify-content-end">
                            <Button onClick={() => setUserConnectedShow(false)} variant="outline-success">
                                Close
                            </Button>
                        </div>
                    </Alert>
                    <Alert show={userDisconnectedShow} variant="danger" style={{marginTop: '4%', borderRadius: '25px'}}>
                        <Alert.Heading>Other Player Has Disconnected!</Alert.Heading>
                        <div className="d-flex justify-content-end">
                            <Button onClick={() => setUserDisconnectedShow(false)} variant="outline-danger">
                                Close
                            </Button>
                        </div>
                    </Alert>
                </div>
                <div className = "boardbar">
                    <canvas ref={canvasRef} />
                </div>
            </div>
            <Modal show={show} onHide={handleClose}>
                <Modal.Header closeButton>
                    <Modal.Title>{winMessage}
                    <span role="img" aria-label="trophy">🏆</span>
                    </Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    <span style={{fontSize:'25px'}}>&#10029;</span>
                    CONGRATULATIONS!
                    <span style={{fontSize:'25px'}}>&#10029;</span>
                </Modal.Body>
                <Modal.Footer>
                    <Button variant="primary" onClick={handleClose}>
                        Close
                    </Button>
                </Modal.Footer>
            </Modal>

            <Modal show={errorShow} onHide={handleErrorClose}>
                <Modal.Header closeButton>
                    <Modal.Title>Uh Oh! There was a small problem...</Modal.Title>
                </Modal.Header>
                <Modal.Body>Please create a new game or refresh the page. </Modal.Body>
                <Modal.Footer>
                    <Button variant="primary" onClick={handleErrorClose}>
                        Close
                    </Button>
                </Modal.Footer>
            </Modal>

            <Modal show={connErrorShow} onHide={handleConnErrorClose}>
                <Modal.Header closeButton>
                    <Modal.Title> Uh oh! Unable to connect to server. </Modal.Title>
                </Modal.Header>
                <Modal.Body>Please try refreshing the page. </Modal.Body>
                <Modal.Footer>
                    <Button variant="primary" onClick={handleConnErrorClose}>
                        Close
                    </Button>
                </Modal.Footer>
            </Modal>

            <Modal show={roomErrorShow} onHide={handleRoomErrorClose}>
                <Modal.Header closeButton>
                    <Modal.Title>Uh Oh! You entered an invalid number...</Modal.Title>
                </Modal.Header>
                <Modal.Body>Please check the room number you have entered and try again! </Modal.Body>
                <Modal.Footer>
                    <Button variant="primary" onClick={handleRoomErrorClose}>
                        Close
                    </Button>
                </Modal.Footer>
            </Modal>
        </div>
    );
}

export default Game;