import 'bootstrap/dist/css/bootstrap.min.css';
import Options from "./Options";
import Game from "./Game";
import {useState, useRef} from "react";

function Referee() {
    // reference for the type of player1: human or computer
    const player1Ref = useRef("");
    // reference for the type of player2: human or computer
    const player2Ref = useRef("");
    // reference for level of player1 if it is a computer player
    // easy, medium, hard
    // or an empty string if player1 is human
    const computer1Ref = useRef("");
    // reference for level of player2 if it is a computer player
    // easy, medium, hard
    // or an empty string if player2 is human
    const computer2Ref = useRef("");
    // reference for counter that changes when any options have been changed
    const optionsChangeRef = useRef(0);
    // reference for counter to count how many times the create game button has been selected
    const [startGame, setStartGame] = useState(0);
    //reference for different devices
    const [differentDevice, setDifferentDevice] = useState(false)
    const differentDeviceRef = useRef(false)
    //reference to the room code entered by another player
    const roomNumRef = useRef(0)

    return (
        <div className="Referee">
            <section className="light" id = {"options"}>
                <Options
                          p1 = {player1Ref} p2 = {player2Ref}
                          c1 = {computer1Ref} c2 = {computer2Ref}
                          setgame = {setStartGame} startgame = {startGame}
                          optionschangeref={optionsChangeRef} diffDevice = {differentDevice}
                          diffDeviceRef = {differentDeviceRef}
                          setDiffDevice = {setDifferentDevice} roomNumRef = {roomNumRef}/>
            </section>
            <section className="white" id = {"game"}>
                <Game p1 = {player1Ref} p2 = {player2Ref}
                      c1 = {computer1Ref} c2 = {computer2Ref}
                      setgame = {setStartGame} startgame = {startGame}
                      optionschangeref={optionsChangeRef}
                      diffDevice = {differentDevice} diffDeviceRef = {differentDeviceRef}
                      roomNumRef = {roomNumRef}/>
            </section>
        </div>
    );
}

export default Referee;