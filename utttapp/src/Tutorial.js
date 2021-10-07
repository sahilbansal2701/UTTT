import 'bootstrap/dist/css/bootstrap.min.css';
import image1 from './images/tutorial1-initial.PNG';
import image2 from './images/tutorial2-p1.PNG'
import image3 from './images/tutorial3-p2.PNG'
import image4 from './images/tutorial4-almost3p1.PNG'
import image5 from './images/tutorial5-3p1.PNG'
import image51 from './images/tutorial-forgot1-entireboard.PNG'
import image6 from './images/tutorial6-winp2.PNG'
import image7 from './images/tutorial7-draw.PNG'
import image8 from './images/tutorial8-hint.PNG'
function Tutorial() {
    return (
        <div className="Tutorial">
            <h2 className="tutorialheader">Tutorial</h2>
            <div className= "tutorialtext">
                <div className = "tutorialrow">
                    <div className = "tutorialcol">
                        <p style={{paddingTop:'25%'}} className = "tutorialp">
                            When an Ultimate Tic Tac Toe game first starts,
                            the first player can choose to place their token in any
                            slot on the board. </p>
                    </div>
                    <div className = "tutorialcol">
                        <img className = "tutorialphoto" src={image1}
                             alt="Blank Ultimate Tic Tac Toe board."/>
                    </div>
                </div>

                <div className = "tutorialrow">
                    <div className = "tutorialcol">
                        <p style={{paddingTop:'15%'}} className = "tutorialp">
                            Let's assume that Player 1, with the token X, places their token on the
                            upper right slot of the middle board (row: 4, col: 6). Then, in the following move,
                            Player 2 must make their move in the corresponding board: the upper right board.
                            The game will highlight the board that the next move must be made on.
                        </p>
                    </div>
                    <div className = "tutorialcol">
                        <img className = "tutorialphoto" src={image2}
                             alt="Ultimate Tic Tac Toe board where Player 1, X, has placed their token
                             on the upper right-hand slot of the middle board (row: 4, col: 6)."/>
                    </div>
                </div>

                <div className = "tutorialrow">
                    <div className = "tutorialcol">
                        <p style={{paddingTop:'15%'}} className = "tutorialp">
                            Player 2, with the token O, is restricted to making their move on the upper right board.
                            Let's assume that Player 2 places their token on the lower left slot of the upper right
                            board (row: 3, col: 7). Then, in the following move, Player 1 must make their move in
                            the corresponding board: the lower left board. Again, the game will highlight the
                            board that the next move must be made on.
                        </p>
                    </div>
                    <div className = "tutorialcol">
                        <img className = "tutorialphoto" src={image3}
                             alt="Ultimate Tic Tac Toe board where Player 2, 0, has placed their token
                             on the lower left-hand slot of the upper right-hand board (row: 3, col: 7)."/>
                    </div>
                </div>

                <div className = "tutorialrow">
                    <div className = "tutorialcol">
                        <p style={{paddingTop:'10%'}} className = "tutorialp">
                            Let's fast forward the game. We can see here that the next player (in this case, Player 1, with the token X)
                            is restricted to make their next move in the middle board. The objective of this game is to
                            win 3 boards in a row, either vertically, horizontally, or diagonally. In order to win a small board,
                            one must place 3 of their tokens in a row. For this next
                            move, Player 1 is one token away from making a vertical row in the middle board, which would be a win.
                        </p>
                    </div>
                    <div className = "tutorialcol">
                        <img className = "tutorialphoto" src={image4}
                             alt="Ultimate Tic Tac Toe board where Player 1, X, has almost achieved
                             3 X tokens in a vertical row on the middle board."/>
                    </div>
                </div>

                <div className = "tutorialrow">
                    <div className = "tutorialcol">
                        <p style={{paddingTop:'20%'}} className = "tutorialp">
                            Player 1 (X), has placed their token on the middle right slot of the middle board,
                            effectively completing a vertical row and winning the middle board! Now, in the
                            following move, Player 2 must make their move in the corresponding board:
                            the middle right board.
                            Again, the game will highlight the board that the next move must be made on.
                        </p>
                    </div>
                    <div className = "tutorialcol">
                        <img className = "tutorialphoto" src={image5}
                             alt="Ultimate Tic Tac Toe board where Player 1, X, won the middle board."/>
                    </div>
                </div>

                <div className = "tutorialrow">
                    <div className = "tutorialcol">
                        <p style={{paddingTop:'15%'}} className = "tutorialp">
                            If the designated board for the next move to be made has already been won or ended in a draw,
                            the next player can choose to place their token anywhere on the board. In this case,
                            Player 2, with the token O, has chosen to place their token in the middle slot of
                            the middle right board (row: 5, col: 8). The corresponding board, the middle board, has already been won.
                            Therefore, Player 1 has the ability to place their token in any of the boards that
                            have not yet been won or ended in a draw.
                        </p>
                    </div>
                    <div className = "tutorialcol">
                        <img className = "tutorialphoto" src={image51}
                             alt="Ultimate Tic Tac Toe board where Player 2, O, has made a move where the next
                             board has already been won, so player X can place their token on any board."/>
                    </div>
                </div>

                <div className = "tutorialrow">
                    <div className = "tutorialcol">
                        <p style={{paddingTop:'20%'}} className = "tutorialp">
                            Let's fast forward the game again. Player 2 has won the game! Player 2 has won
                            3 boards in a row, creating a vertical row of O's to win the game. One can also
                            win the game by making a horizontal or diagonal row of wins.
                        </p>
                    </div>
                    <div className = "tutorialcol">
                        <img className = "tutorialphoto" src={image6}
                             alt="Ultimate Tic Tac Toe board where Player 2, 0, won the game by making a vertical row on the big board."/>
                    </div>
                </div>

                <hr className={"divide"}/>
                <br/>
                <div className = "tutorialrow">
                    <div className = "tutorialcol">
                        <p style={{paddingTop:'20%'}} className = "tutorialp">
                            Another possibility for the game to end is if no player wins and the game ends in
                            a draw. This happens when all of the boards have been finished (they have either been won
                            or have ended in a draw themselves), but no rows of 3 wins have been made.
                        </p>
                    </div>
                    <div className = "tutorialcol">
                        <img className = "tutorialphoto" src={image7}
                             alt="Ultimate Tic Tac Toe board where the game had ended bu no player had won."/>
                    </div>
                </div>

                <hr className={"divide"}/>
                <br/>
                <div className = "tutorialrow">
                    <div className = "tutorialcol">
                        <p style={{paddingTop:'20%'}} className = "tutorialp">
                            As a tip: when playing against a Computer player as a Human player, you can
                            select the "Hint" button to receive a hint on what move to make next.
                            The square where your next move should be made will be highlighted.
                        </p>
                    </div>
                    <div className = "tutorialcol">
                        <img className = "tutorialphoto" src={image8}
                             alt="Ultimate Tic Tac Toe board where a square is highlighted as a hint."/>
                    </div>
                </div>
            </div>
        </div>
    );
}

export default Tutorial;