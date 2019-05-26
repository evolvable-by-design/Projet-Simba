import React from 'react'
import { Link } from 'react-router-dom'

const Home = (props) => (
  <div className="Container">
    <Link to="/create" className="Btn-primary" style={{textDecoration: 'none'}}>Créer un sondage</Link>
  </div>
)

export default Home