import React from 'react';
import { BrowserRouter as Router, Route, Link } from "react-router-dom";

import './App.css';
import Header from './Header'
import CreatePoll from './CreatePoll';
import Home from './Home';

function App() {
  return (
    <Router>
      <Header />
      <Route path='/' exact component={Home}/>
      <Route path='/create' exact component={CreatePoll}/>
    </Router>
  );
}

export default App;
