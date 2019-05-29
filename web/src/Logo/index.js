import React from 'react'
import flatLogo from './flat_logo.png'
import logo from './Logo.png'

const FlatLogo = (props) => {
  return (
    <img src={flatLogo} alt="Logo Simba" {...props}>
    </img>
  )
}

const Logo = (props) => {
  return (
    <img src={logo} alt="Logo Simba" {...props}>
    </img>
  )
}

export { FlatLogo, Logo}