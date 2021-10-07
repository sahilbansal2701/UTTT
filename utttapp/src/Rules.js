import 'bootstrap/dist/css/bootstrap.min.css';

function Rules() {
    return (
        <div className="Rules">
            <h2 className="rulesheader">Rules</h2>
            <p className= "rulestext">
                - The basic rules of Ultimate Tic Tac Toe are derived from a regular game of Tic Tac Toe. <br/> <br/>

                - In Ultimate Tic Tac Toe, there are 9 large squares to play on, each of which contains a regular Tic Tac Toe board.
                Essentially, an Ultimate Tic Tac Toe Board is made up of 9 smaller Tic Tac Toe Boards. <br/> <br/>

                - To play the game, one player starts by putting their respective token on an open spot on any of the smaller
                boards. <br/> <br/>

                - Then the next player must play on the small board corresponding to the slot of the small board
                where the previous player placed their token. <br/> <br/>

                - If a player places their piece on the board and next board corresponding to this move has already been won or ended in a draw,
                the next player can freely choose which board they want to play on next.
                The game continues until one player wins on the big Ultimate Tic Tac Toe board or when there is a draw. <br/> <br/>

                - In order for a player to win on the big Ultimate Tic Tac Toe board, they must win three smaller Tic Tac Toe board in a row
                (either horizontally, vertically, or diagonally). <br/>
            </p>

        </div>
    );
}

export default Rules;