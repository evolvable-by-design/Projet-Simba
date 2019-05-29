import React from 'react';
import { BrowserRouter as Router, Route } from "react-router-dom";

import './App.css';
import Header from './Header'
import CreatePoll from './CreatePoll';
import Poll from './Poll';
import EditPoll from './EditPoll';
import Home from './Home';

function App() {
  return (
    <Router>
      <Header />
      <Route path='/' exact component={Home}/>
      <Route path='/create' exact component={CreatePoll}/>
      <Route path='/polls/:slug' exact component={Poll}/>
      <Route path='/polls/:slug/edit' exact component={EditPoll}/>
    </Router>
  );
}

export default App;
