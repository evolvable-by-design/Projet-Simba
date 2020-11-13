import React from 'react'

import { useApiVersion, setApiVersion } from '../utils/apiVersionManager';
import './Header.css'

const Header = () => {

  const apiVersion = useApiVersion()
  const checked = apiVersion === '2'

  const onSwitchChange = (e) => {
    const version = e.target.checked ? 2 : 1
    setApiVersion(version)
  }

  return (<div className="header">
    <p>Use V1 of API</p>
    <Switch checked={checked} onChange={onSwitchChange} />
    <p>Use V2 of API</p>
  </div>
)}

const Switch = ({ checked, onChange }) => (
  <label class="switch">
    <input type="checkbox" checked={checked} onChange={onChange} />
    <span class="slider round"/>
  </label>
)

export default Header