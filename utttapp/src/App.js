import './App.css';
import Navbar from 'react-bootstrap/Navbar';
import Nav from 'react-bootstrap/Nav';
import 'bootstrap/dist/css/bootstrap.min.css';
import Home from "./Home";
import Rules from "./Rules"
import Tutorial from "./Tutorial";
import Options from "./Options";
import Game from "./Game";
import Referee from "./Referee";
import {conn} from "./index.js";

function App() {
  return (
    <div className="App">
        <Navbar fixed="top" bg="light" variant="light">
          <Navbar.Brand href="#home">Ultimate-Tic-Tac-Toe</Navbar.Brand>
          <Nav className="mr-auto">
            <Nav.Link href="/#home">Home</Nav.Link>
            <Nav.Link href="/#rules">Rules</Nav.Link>
            <Nav.Link href="/#tutorial">Tutorial</Nav.Link>
            <Nav.Link href="/#options">Options</Nav.Link>
          </Nav>

        </Navbar>
        <section className='dark' id={'home'}>
            <Home/>
        </section>
        <section className='light' id={'rules'}>
            <Rules/>
        </section>
        <section className='dark' id={'tutorial'}>
            <Tutorial/>
        </section>
        <section id={'options'}>
            <Referee/>
        </section>
      <link
          rel="stylesheet"
          href="https://cdn.jsdelivr.net/npm/bootstrap@4.6.0/dist/css/bootstrap.min.css"
          integrity="sha384-B0vP5xmATw1+K9KRQjQERJvTumQW0nPEzvF6L/Z6nronJ3oUOFUFpCjEUQouq2+l"
          crossOrigin="anonymous"
      />
    </div>
  );
}

export default App;
