import 'bootstrap/dist/css/bootstrap.min.css';
import Button from "react-bootstrap/Button";

function Home() {
    return (
        <div className="Home">
            <h1 className="homeheader">Ultimate Tic Tac Toe</h1>
            {/*<p className>*/}
            {/*    Welcome! To all the avid Ultimate-Tic-Tac-Toe players and beginners:*/}
            {/*    we hope you have fun playing this brain stimulating game! <br></br>*/}
            {/*</p>*/}
            <div className= "description2">
                <h4>INSTRUCTIONS:</h4>
                <ul>
                    <li> Find out how to play the game on the <a href = "/#rules" style={{color:"#89f1ff"}}>Rules</a> and <a href = "/#tutorial" style={{color:"#89f1ff"}}>Tutorial</a> pages. </li>
                    <li> To begin a game, visit <a href = "/#options" style={{color:"#89f1ff"}}>Options</a> to select settings for a new game. </li>
                    <li> To enter an existing room code to play with a friend, visit <a href = "/#options" style={{color:"#89f1ff"}}>Options</a> and select the "Different Devices" feature under "Human" v. "Human". </li>
                    <li> Hints can only be used when playing a Human v. Computer game.</li>
                    <li> Keep in mind that higher levels of Computer players take longer to make their move.</li>
                    <li> If you expect something to happen and nothing has happened yet, please be patient! The functionality of this program is dependent on your internet connection.</li>
            </ul>
            </div>
            <Button href = "/#options" variant="primary">Play Game</Button>
        </div>
    );
}

export default Home;