import React from 'react'
import { Link } from 'react-router-dom'

import first from './1.png'
import second from './2.png'
import third from './3.png'
import { Logo } from '../Logo'

import './Home.css'


const CardSmall = ({title, subtitle, image, style}) => {
  return (
    <div className="SmallCard" style={style}>
    <div className="SmallCard_Image">
      <img src={image} height="200px"/>
    </div>
    <div className="SmallCard_Title">
      { title }
    </div>
    <div className="SmallCard_Subtitle">
      { subtitle }
    </div>      
    </div>
  )
}

const Home = () => {
  return (
    <div className="Home_Container">
      <div className="Home_Wrapper">
      <div className="Home_Logo">
        <Logo height="130px"/>
      </div>
      <div className="SmallCard_Container">
        <CardSmall style={{backgroundColor: "#44baf2", color: "white"}} image={first} title="Créez un sondage" subtitle="Définissez plusieurs créneaux pour votre réunion."/>
        <CardSmall style={{backgroundColor: "#fc506d", color: "white"}} image={second} title="Envoyez vos invitations" subtitle="Les participants aux sondages pourront voter pour les dates qui leur conviennent le mieux !"/>
        <CardSmall style={{backgroundColor: "#8f3ee8", color: "white"}} image={third} title="Faites votre choix" subtitle="Vous pourrez obtenir en direct les résultats du sondage afin de choisir au mieux la meilleure proposition."/>
      </div>
      <div className="Home_Button">
      <Link to="/create" className="Home_CreateLink">Créer votre poll !</Link>
      </div>
      </div>
    </div>
  )
}

export default Home