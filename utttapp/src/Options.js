import 'bootstrap/dist/css/bootstrap.min.css';
import Card from 'react-bootstrap/Card';
import React, {useState} from "react";
import './toggle.css'
import Toggle from 'react-toggle';
import Button from "react-bootstrap/Button";
import {connectGame, CreateGame} from "./PostRequest";
import {Form} from "react-bootstrap";

function Options(props) {
    // checks if player 1 and player 2 are human or computer
    // false if human, true if computer
    const [checkAIP1, setCheckAIP1] = useState(false);
    const [checkAIP2, setCheckAIP2] = useState(false);
    // the difficulty of the ai player for toggle buttons:
    // can be easy, medium, or hard
    const [AIDifficulty1, setAIDifficulty1] = useState("easy");
    const [AIDifficulty2, setAIDifficulty2] = useState("easy");

    /**
     * Player 1 toggles for computer v human
     * @param e
     */
    const handleChange1 = (e)=> {
        props.optionschangeref.current += 1;
        if(e.target.checked === true) {
            setCheckAIP1(true)
            props.setDiffDevice(false)
            props.roomNumRef.current = 0;
            props.diffDeviceRef.current = false;
        } else {
            setCheckAIP1(false)
        }

    };

    /**
     * Player 2 toggles for computer v human
     * @param e
     */
    const handleChange2 = (e)=> {
        props.optionschangeref.current += 1;
        if(e.target.checked === true) {
            setCheckAIP2(true)
            props.setDiffDevice(false)
            props.diffDeviceRef.current = false
            props.roomNumRef.current = 0;
        } else {
            setCheckAIP2(false)
        }
   ;
    };

    /**
     * If both players are human, check for same or different devices
     * @param e
     */
    const handleChange3 = (e)=> {
        props.optionschangeref.current += 1;
        if(e.target.checked === true) {
            props.setDiffDevice(true)
            console.log(e.target.checked)
            props.diffDeviceRef.current = true;
        } else {
            props.setDiffDevice(false)
            props.roomNumRef.current = 0;
            props.diffDeviceRef.current = false;
        }

    };

    /**
     * handler for change in computer difficulty for player 1
     * @param changeEvent
     * @returns {Promise<void>}
     */
    async function radioChange1(changeEvent) {
        props.optionschangeref.current += 1;
        await setAIDifficulty1(changeEvent.target.value)
    }

    /**
     * handler for change incomputer difficulty for player 2
     * @param changeEvent
     * @returns {Promise<void>}
     */
    async function radioChange2 (changeEvent) {
        props.optionschangeref.current += 1;
        await setAIDifficulty2(changeEvent.target.value)
    }
    const setGameRoom = (event) => {
        console.log("target value:", event.target.value)
        props.roomNumRef.current = event.target.value
    }
    const submitRoom = () => {
        console.log("room ref:", props.roomNumRef.current)
        connectGame(props.roomNumRef.current)
        props.diffDeviceRef.current = true;
    }

    /**
     * handler for if create game button is clicked
     * sets the values for player options
     * @returns {Promise<void>}
     */
    async function startGameButton()  {
        props.optionschangeref.current += 1;
        props.diffDeviceRef.current = props.diffDevice;
        if(checkAIP1) {
            props.p1.current = "Computer";
            props.c1.current = AIDifficulty1;
        } else {
            props.p1.current = "Human";
            props.c1.current = "";
        }
        if(checkAIP2) {
            props.p2.current = "Computer";
            props.c2.current = AIDifficulty2;
        } else {
            props.p2.current = "Human";
            props.c2.current = "";
        }
        await props.setgame(props.startgame + 1);
    }

    // display for radio buttons that should only appear if player 1 is computer
    let display = <div/>
    if(checkAIP1) {
       display =  <div className="radioOptions">
            <form>
                <div className="radio">
                    <label>
                        <input type="radio" value="easy" checked={AIDifficulty1 === 'easy'} onChange = {radioChange1}/>
                        Easy
                    </label>
                </div>
                <div className="radio">
                    <label>
                        <input type="radio" value="medium" checked={AIDifficulty1 === 'medium'} onChange = {radioChange1}/>
                        Medium
                    </label>
                </div>
                <div className="radio">
                    <label>
                        <input type="radio" value="hard" checked={AIDifficulty1 === 'hard'} onChange = {radioChange1} />
                        Hard
                    </label>
                </div>
            </form>
        </div>
    }
    // display for radio buttons that should only appear if player 2 is computer
    let display2 = <div/>
    if(checkAIP2) {
        display2 =  <div className="radioOptions">
            <form>
                <div className="radio">
                    <label>
                        <input type="radio" value= "easy" checked={AIDifficulty2 === 'easy'} onChange = {radioChange2}/>
                        Easy
                    </label>
                </div>
                <div className="radio">
                    <label>
                        <input type="radio" value="medium" checked={AIDifficulty2 === 'medium'}  onChange = {radioChange2} />
                        Medium
                    </label>
                </div>
                <div className="radio">
                    <label>
                        <input type="radio" value="hard" checked={AIDifficulty2 === 'hard'} onChange = {radioChange2} />
                        Hard
                    </label>
                </div>
            </form>
        </div>
    }
    // display for game link that should only appear if 2 human different device
    let linkDisplay = <div><br/></div>
    if (props.diffDevice) {
        linkDisplay = <div>
            <p style={{paddingTop:"1%", paddingLeft:"3%"}}>If you're playing a game with a friend across devices,
               please enter the room code! Otherwise please click the "Create Game" button to get a room code which will appear
                at the top of the game page!</p>
                <div className = "optionstext">
                    <div className = "player">
                        <Form>
                            <Form.Group controlId="formRoomInput">
                                <Form.Label>Room Number</Form.Label>
                                <Form.Control type="text" placeholder="Enter room number"  onChange={setGameRoom}/>
                                <Form.Text className="text-muted">
                                    Have fun playing!
                                </Form.Text>
                            </Form.Group>
                            <Button variant="primary" href= '/#game'onClick={submitRoom}>
                                Enter
                            </Button>
                        </Form>
                    </div>
                </div>
        </div>
    }
    let display3 = <div/>
    if (!checkAIP1 && !checkAIP2) {
        display3 = <div>
            <Card style={{ borderColor: '#4082bc', borderWidth: '2px', borderRadius: '25px', marginTop: "5%"}}>
                <Card.Header style={{textAlign: 'left', paddingTop: '3%', borderColor: '#4082bc',
                    borderTopLeftRadius: '20px', borderTopRightRadius: '20px'}}>
                    <label style={{fontSize: 16,maxWidth: 150, lineHeight: 1, paddingLeft: 10}}>Same Device</label><Toggle
                    className='HoC'
                    id='SameOrDifferent'
                    icons = {false}
                    onChange={handleChange3} />
                    <label style={{fontSize: 16,maxWidth: 200, lineHeight: 1, paddingRight: 20}}>Different Devices</label>
                </Card.Header>
                <Card.Body style={{textAlign: 'left'}}>
                    {linkDisplay}
                </Card.Body>
            </Card>
            <br/>

        </div>
    }
    return (
        <div className="Options">
            <h2 className="optionsheader">Options</h2>
            <div className= "optionstext">
                <div className="player">
                    <Card style={{ borderColor: '#4082bc', borderWidth: '2px', borderRadius: '25px'}}>
                        <Card.Header style={{ background: '#4082bc', color: 'white', textAlign: 'left', paddingTop: '6%',
                            paddingLeft: '10%', borderTopLeftRadius: '20px', borderTopRightRadius: '20px'}}>
                            <Card.Title>Player 1: (X)</Card.Title>
                        </Card.Header>
                        <Card.Body>
                            <label style={{fontSize: 16, maxWidth: 150, lineHeight: 1, paddingLeft: '2%'}}>Human</label><Toggle
                            className='HoC'
                            id='HumanOrComputer'
                            icons = {false}
                            onChange={handleChange1} />
                            <label style={{ fontSize: 16,maxWidth: 150, lineHeight: 1, paddingRight: '2%'}}>Computer</label>
                            <hr className="divide"/>
                            {display}

                        </Card.Body>
                    </Card>
                </div>
                <div className="player">
                    <Card style={{ borderColor: '#4082bc', borderWidth: '2px', borderRadius: '25px'}}>
                        <Card.Header style={{ background: '#4082bc', color: 'white', textAlign: 'left', paddingTop: '6%',
                            paddingLeft: '10%',borderTopLeftRadius: '20px', borderTopRightRadius: '20px'}}>
                            <Card.Title>Player 2: (O)</Card.Title>
                        </Card.Header>
                        <Card.Body>
                            <label style={{fontSize: 16,maxWidth: 150, lineHeight: 1, paddingLeft: 10}}>Human</label><Toggle
                            className='HoC'
                            id='HumanOrComputer2'
                            icons = {false}
                            onChange={handleChange2} />
                            <label style={{fontSize: 16,maxWidth: 150, lineHeight: 1, paddingRight: 20}}>Computer</label>
                            <hr className="divide"/>
                            {display2}

                        </Card.Body>
                    </Card>
                </div>
            </div>
            {display3}
            <Button className="startButton" variant="primary" href = "/#game" onClick={startGameButton}>Create Game</Button>
        </div>
    );
}

export default Options;