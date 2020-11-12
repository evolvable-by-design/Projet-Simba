import React from 'react';
import { Router, Route } from "react-router-dom";

import './App.css';
import Header from './Header'
import CreatePoll from './CreatePoll';
import Poll from './Poll';
import EditPoll from './EditPoll';
import Home from './Home';
import { history } from './History';
import { getApiVersion, setApiVersion } from './utils/apiVersionManager';

function App() {

  initialiseApiVersionUsage()

  return (
    <Router history={history}>
      <Header />
      <Route path='/' exact component={Home}/>
      <Route path='/create' exact component={CreatePoll}/>
      <Route path='/polls/:slug' exact component={Poll}/>
      <Route path='/polls/:slug/edit' exact component={EditPoll}/>
    </Router>
  );
}

function initialiseApiVersionUsage() {
  if (getApiVersion() === null) {
    setApiVersion(1)
  }
}

export default App;
