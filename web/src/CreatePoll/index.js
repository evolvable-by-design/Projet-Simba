import React, { useState } from 'react'
import { Wizard, Steps, Step } from 'react-albus';
import BigCalendar from 'react-big-calendar'
import moment from 'moment'
import axios from 'axios';
import 'moment/locale/fr'
import { Link } from 'react-router-dom'

import Card from '../Card';
import 'react-big-calendar/lib/css/react-big-calendar.css'
import './CreatePoll.css'
import {BASE_URL, CALENDAR_MESSAGES} from '../utils/constants'
import copy from 'copy-text-to-clipboard'

const Informations = ({next, title, location, description, setLocation, setTitle, setDescription, hasMeal, setMeal}) => {

  const [errorName, setErrorName] = useState(undefined)

  const checkInputs = () => {
    setErrorName(undefined)
    let hasErrors = false
    
    if(title.trim() === "") {
      hasErrors = true
      setErrorName("Veuillez donner un nom au doodle.")
    }

    if(!hasErrors) next()
  }

  const footer = (
    <div className="CreatePoll_Button">
      { next && 
        <button className="Btn-primary" onClick={checkInputs}>Suivant</button>
      }
    </div>
  )

  return (
    <Card title="Informations (1/2)" footer={footer}>
      <div className="CreatePoll_Form">
        <div className="CreatePoll_Input">
          { errorName && 
            <span className="CreatePoll_LabelError">{ errorName }</span>
          }
          <input value={title} type="text" placeholder="Soirée SI" onChange={(e)=>setTitle(e.target.value)}/>
        </div>
        <div className="CreatePoll_Input">
          <input value={location} type="text" placeholder="Chez Kévin" onChange={(e)=>setLocation(e.target.value)}/>
        </div>
        <div className="CreatePoll_Input">
          <textarea placeholder="Description" onChange={(e)=>setDescription(e.target.value)} value={description}>
          </textarea>
        </div>
        <div className="CreatePoll_Input CreatePoll_Switch">
          <span>Cet événement contient un repas :</span>
          <label className="switch" htmlFor="hasMeal">
            <input id="hasMeal" type="checkbox" checked={hasMeal} onChange={(e) => setMeal(e.target.checked)} value="Doit-on inclure un repas ?"/>
            <div className="slider round"></div>
          </label>
        </div>
      </div>
    </Card>
  )
}

moment.locale('fr');
const localizer = BigCalendar.momentLocalizer(moment)

const sameDay = (d1, d2) => {
  return d1.getFullYear() === d2.getFullYear() &&
    d1.getMonth() === d2.getMonth() &&
    d1.getDate() === d2.getDate();
}


const Choices = ({next, previous, choices, setChoices, createPoll, buttonName}) => {

  const handleCreate = ({start, end}) => {
    if(!sameDay(start, end)) return
    setChoices([...choices, {start, end, name: ""}])
  }

  const handleSelect = (event) => {
    const newChoices = [...choices]
    newChoices.splice(choices.indexOf(event), 1)
    setChoices(newChoices)
  }

  const handleVote = () => {
    if(choices.length === 0) return

    createPoll()
    next()
  }

  const footer = (
    <div className="CreatePoll_Buttons">
      { previous && 
        <button className="Btn-primary" onClick={previous}>Précedent</button>
      }
      <button className="Btn-primary" onClick={handleVote}>{buttonName}</button>
    </div>
  )

  return (
    <Card title="Choix (2/2)" footer={footer}>
      <BigCalendar 
        selectable
        events={choices}
        min={new Date(1970, 1, 1, 6)}
        views={['day', 'week', 'month']}
        localizer={localizer}
        startAccessor="start"
        endAccessor="end"
        defaultView="week"
        messages={CALENDAR_MESSAGES}
        onSelectSlot={handleCreate}
        onSelectEvent={handleSelect}
      />
    </Card>
  )
}

const Recap = (props) => {
  const location = window.location
  const [hasCopiedLink, setHasCopiedLink] = useState(false) 
  const [hasCopiedAdminLink, setHasCopiedAdminLink] = useState(false) 
  return (
    <Card title="Récapitulatif">
      <div className="Recap_Link">
      <Link className="Link Poll_Link" to={`/polls/${props.data.slug}`}>Lien vers le Poll</Link>
      <button className={"Copy_Link" + (hasCopiedLink ? " text-green" : "")} onClick={() => { copy(`${window.location.protocol}//${location.host}/polls/${props.data.slug}`); setHasCopiedLink(true)}}>
        <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" className="feather feather-clipboard"><path d="M16 4h2a2 2 0 0 1 2 2v14a2 2 0 0 1-2 2H6a2 2 0 0 1-2-2V6a2 2 0 0 1 2-2h2"></path><rect x="8" y="2" width="8" height="4" rx="1" ry="1"></rect></svg>  Copier
      </button>
      </div>
      <div className="Recap_Link">
      <Link className="Link Poll_Link" to={`/polls/${props.data.slug}?t=${props.data.slugAdmin}`}>Lien vers l'aministration du Poll</Link>
      <button className={"Copy_Link" + (hasCopiedAdminLink ? " text-green" : "")} onClick={() => { copy(`${location.protocol}//${location.host}/polls/${props.data.slug}?t=${props.data.slugAdmin}`); setHasCopiedAdminLink(true)}}>
        <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" className="feather feather-clipboard"><path d="M16 4h2a2 2 0 0 1 2 2v14a2 2 0 0 1-2 2H6a2 2 0 0 1-2-2V6a2 2 0 0 1 2-2h2"></path><rect x="8" y="2" width="8" height="4" rx="1" ry="1"></rect></svg>  Copier
      </button>
      </div>
    </Card>
  )
}

const CreatePoll = (props) => {
  const [title, setTitle] = useState("")
  const [description, setDescription] = useState("")
  const [location, setLocation] = useState("")
  const [hasMeal, setMeal] = useState(false)

  const [choices, setChoices] = useState([])

  const [data, setData] = useState({})

  const createPoll = () => {
    const sendChoices = choices.map((choice) => {
      return {
        startDate: choice.start,
        endDate: choice.end,
        name: choice.name
      }
    })

    axios.post(`${BASE_URL}/polls`, {
      title,
      description,
      location,
      pollChoices: sendChoices,
      has_meal: hasMeal,
    }).then(res => {
      if(res.status === 201) {
        //props.history.push(`/polls/${res.data.slug}`)
        setData(res.data)
      }
    })
  }

  return (
    <>
    <div className="Container">
      <h1>
        Création d'un poll
      </h1>
      <Wizard>
      <Steps>
        <Step
          id="informations"
          render={({ next }) => (
            <Informations next={next} title={title} location={location} description={description} setDescription={setDescription} setLocation={setLocation} setTitle={setTitle} hasMeal={hasMeal} setMeal={setMeal}/>
          )}
        />
        <Step
          id="choices"
          render={(nav) => (
            <Choices {...nav} choices={choices} setChoices={setChoices} createPoll={createPoll} buttonName="Créer"/>
          )}
        />
        <Step
          id="poll"
          render={(nav) => (
            <Recap data={data} />
          )}
        />
        </Steps>
      </Wizard>
    </div>
    </>
    
  )
}

export default CreatePoll
export { Informations, Choices}