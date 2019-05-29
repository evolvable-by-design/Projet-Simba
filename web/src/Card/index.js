import React from 'react'
import './Card.css'

const Card = ({title, subtitle, children, footer, style}) => {
  return (
    <div className="Card_Container" style={style}>
      <div className="Card_Header">
        <h1>{ title }</h1>
        <div className="Card_Header_Subtitle">{ subtitle }</div>
      </div>
      <div className="Card_Content">
        { children }
      </div>
      <div className="Card_Footer">
        { footer }
      </div>
    </div>
  )
}

export default Card