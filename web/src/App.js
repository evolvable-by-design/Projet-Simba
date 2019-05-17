import React from 'react';
import { BrowserRouter as Router, Route, Link } from "react-router-dom";

import './App.css';
import Header from './Header'
import CreatePoll from './CreatePoll';

function App() {
  return (
    <Router>
      <Header />
      <div className="Container">
        <Route path='/' exact component={CreatePoll}/>
      </div>
    </Router>
  );
}

export default App;
