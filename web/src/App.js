import React from 'react';
import { Router, Route } from "react-router-dom";

import './App.css';
import Header from './Header'
import CreatePoll from './CreatePoll';
import Poll from './Poll';
import EditPoll from './EditPoll';
import Home from './Home';
import { history } from './History';
import { useApiVersion, setApiVersion } from './utils/apiVersionManager';

function App() {
  return (
    <Router history={history}>
      <Application />
    </Router>
  );
}

function Application() {

  useInitialiseApiVersionUsage()

  return (<>
    <Header />
    <Route path='/' exact component={Home}/>
    <Route path='/create' exact component={CreatePoll}/>
    <Route path='/polls/:slug' exact component={Poll}/>
    <Route path='/polls/:slug/edit' exact component={EditPoll}/>
  </>)
}

function useInitialiseApiVersionUsage() {
  const apiVersion = useApiVersion()
  if (apiVersion === null) {
    setApiVersion(1)
  }
}

export default App;
