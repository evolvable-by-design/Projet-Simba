import React, { useState } from 'react'
import { Wizard, Steps, Step } from 'react-albus';
import BigCalendar from 'react-big-calendar'
import moment from 'moment'
import 'moment/locale/fr'

import Card from '../Card';
import 'react-big-calendar/lib/css/react-big-calendar.css'
import './CreatePoll.css'
import axios from 'axios';

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
          <span>Le meeting comprend un repas :</span>
          <label class="switch" for="hasMeal">
            <input id="hasMeal" type="checkbox" checked={hasMeal} onChange={(e) => setMeal(e.target.checked)} value="Doit-on inclure un repas ?"/>
            <div class="slider round"></div>
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

const messages = {
  allDay: 'journée',
  previous: 'précédent',
  next: 'suivant',
  today: 'aujourd\'hui',
  month: 'mois',
  week: 'semaine',
  day: 'jour',
  agenda: 'Agenda',
  date: 'date',
  time: 'heure',
  event: 'événement', // Or anything you want
  showMore: total => `+ ${total} événement(s) supplémentaire(s)`
}

const Choices = ({next, previous, choices, setChoices, createPoll}) => {

  const handleCreate = ({start, end}) => {
    if(!sameDay(start, end)) return
    setChoices([...choices, {start, end, name: ""}])
  }

  const handleSelect = (event) => {
    const newChoices = [...choices]
    newChoices.splice(choices.indexOf(event), 1)
    setChoices(newChoices)
  }

  const footer = (
    <div className="CreatePoll_Buttons">
      { previous && 
        <button className="Btn-primary" onClick={previous}>Précedent</button>
      }
      <button className="Btn-primary" onClick={createPoll}>Créer</button>
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
        messages={messages}
        onSelectSlot={handleCreate}
        onSelectEvent={handleSelect}
      />
    </Card>
  )
}

const CreatePoll = (props) => {
  const [title, setTitle] = useState("")
  const [description, setDescription] = useState("")
  const [location, setLocation] = useState("")
  const [hasMeal, setMeal] = useState(false)

  const [choices, setChoices] = useState([])

  const createPoll = () => {

    const sendChoices = choices.map((choice) => {
      return {
        start_date: choice.start,
        end_date: choice.end,
        name: choice.name
      }
    })

    axios.post('http://localhost:7777/api/polls', {
      title,
      description,
      location,
      pollChoices: sendChoices,
      hasMeal,
    }).then(res => {
      if(res.status === 201) {
        props.history.push(`/polls/${res.data.id}`)
      }
    })
  }

  return (
    <>
    <div className="Container">
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
            <Choices {...nav} choices={choices} setChoices={setChoices} createPoll={createPoll}/>
          )}
        />
        </Steps>
      </Wizard>
    </div>
    </>
    
  )
}

export default CreatePoll