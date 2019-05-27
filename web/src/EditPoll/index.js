import React, { useState, useEffect } from 'react'
import { Wizard, Steps, Step } from 'react-albus';
import { Link } from 'react-router-dom'
import axios from 'axios';
import moment from 'moment'
import BigCalendar from 'react-big-calendar'
import withDragAndDrop from 'react-big-calendar/lib/addons/dragAndDrop'

import 'react-big-calendar/lib/addons/dragAndDrop/styles.css'
import { Informations } from '../CreatePoll';
import { BASE_URL, CALENDAR_MESSAGES } from '../utils/constants'
import 'moment/locale/fr'
import Card from '../Card';

moment.locale('fr');
const localizer = BigCalendar.momentLocalizer(moment)

const DraggableCalendar = withDragAndDrop(BigCalendar)

const sameDay = (d1, d2) => {
  return d1.getFullYear() === d2.getFullYear() &&
    d1.getMonth() === d2.getMonth() &&
    d1.getDate() === d2.getDate();
}

const Choices = ({previous, choices, setChoices, editPoll, buttonName, setEditedChoices, editedChoices, deletedChoices, setDeletedChoices, setCreatedChoices, createdChoices}) => {

  const handleCreate = ({start, end}) => {
    if(!sameDay(start, end)) return
    setChoices([...choices, {start, end, title: ""}])
    setCreatedChoices([...createdChoices, {start, end, title: ""}])
  }

  const handleSelect = (event) => {
    const newChoices = [...choices]
    newChoices.splice(choices.indexOf(event), 1)
    // Enregistrer les choices deleted
    setChoices(newChoices)

    setDeletedChoices([...deletedChoices, event])

  }

  const handleResize = (event) => {
    let idx = -1;
    for (let i = 0; i < choices.length; i++) {
      const choice = choices[i];
      if(choice.id === event.resource) {
        idx = i;
        break;
      }
    }

    if(idx === -1) return;

    const editedEvent = choices[idx]
    editedEvent.start = event.start
    editedEvent.end = event.end

    const newChoices = [...choices]
    newChoices.splice(idx, 1)

    newChoices.push(editedEvent)
    setChoices(newChoices)

    setEditedChoices([...editedChoices, event])
  }

  const footer = (
    <div className="CreatePoll_Buttons">
      { previous && 
        <button className="Btn-primary" onClick={previous}>Précedent</button>
      }
      <button className="Btn-primary" onClick={editPoll}>{buttonName}</button>
    </div>
  )

  return (
    <Card title="Choix (2/2)" footer={footer}>
      <DraggableCalendar 
        selectable
        events={choices}
        min={new Date(1970, 1, 1, 6)}
        views={['day', 'week', 'month']}
        localizer={localizer}
        startAccessor="start"
        endAccessor="end"
        defaultView="week"
        messages={CALENDAR_MESSAGES}
        onSelectEvent={handleSelect}
        onSelectSlot={handleCreate}
        onEventResize={handleResize}
        onEventDrop={handleResize}
        draggableAccessor={event => true}
      />
    </Card>
  )
}

const EditPoll = (props) => {
  let url = new URLSearchParams(props.location.search)
  let token = url.get('t')
  const { slug } = props.match.params

  if(!token) {
    props.history.push(`/polls/${slug}`)
  }

  
  const [title, setTitle] = useState("")
  const [description, setDescription] = useState("")
  const [location, setLocation] = useState("")
  const [hasMeal, setMeal] = useState(false)

  const [choices, setChoices] = useState([])
  const [createdChoices, setCreatedChoices] = useState([])
  const [editedChoices, setEditedChoices] = useState([])
  const [deletedChoices, setDeletedChoices] = useState([])


  useEffect(() => {
    axios.get(`${BASE_URL}/polls/${slug}`)
    .then(res => {
      const data = res.data

      if(data.slugAdmin !== token) {
        props.history.push(`/polls/${slug}`)
      }

      setTitle(data.title)
      setDescription(data.description)
      setLocation(data.location)
      setMeal(data.has_meal)

      const newChoices = data.pollChoices.map(choice => {
        return {
          title: "",
          start: new Date(choice.start_date),
          end: new Date(choice.end_date),
          resource: choice.id,
        }
      })
      setChoices(newChoices)
    })
    .catch((err) => {
      props.history.push('/')
    })
  }, [slug])

  const editPoll = () => {
    //TODO: Corriger bug quand update: la date disparait, les polls sont pas bien update
    const sendChoices = choices.map((choice) => {
      return {
        start_date: choice.start,
        end_date: choice.end,
        name: choice.name
      }
    })

    axios.put(`${BASE_URL}/polls/${slug}?token=${token}`, {
      title,
      location,
      description,
      has_meal: hasMeal,
      pollChoices: sendChoices,
    })
      .then(res => {
        console.log("POLL put ok")
        props.history.push(`/polls/${slug}`)
      })
      .catch(err => {
        console.error("POLL pull NOT ok")
      })
/*
    // Ajout des nouveaux choix
    const createdChoicesFormatted = createdChoices.map((choice) => {
      return {
        start_date: choice.start,
        end_date: choice.end,
        name: choice.name
      }
    })
    axios.post(`${BASE_URL}/polls/${slug}/choices`, createdChoicesFormatted)
    .then(res => {
        console.log("Create ok")
    })
    .catch(err => {
      console.error("Create NOT ok")
    })

    // Suppression des anciens choix
    deletedChoices.forEach(({resource:choice_id}) => {
      axios.delete(`${BASE_URL}/polls/${slug}/choices/${choice_id}`)
        .then(res => {
          console.log("Delete ok")
        })
        .catch(err => {
          console.error("Delete NOT ok")
        })
    })

    // Modification des choix actuels
    editedChoices.forEach(choice => {
      axios.put(`${BASE_URL}/polls/${slug}/choices/${choice.resource}`, {
        start_date: choice.start,
        end_date: choice.end,
        name: choice.name,
      })
        .then(res => {
          console.log("Put ok")
        })
        .catch(err => {
          console.error("Put NOT ok")
        })
    })*/
  }

  return (
    <>
      <div className="Container">
        <h1>
          Édition de <Link to={`/polls/${slug}`} className="Link">{title}</Link>
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
              <Choices 
                {...nav}
                editPoll={editPoll}
                choices={choices}
                setChoices={setChoices}
                deletedChoices={deletedChoices}
                setDeletedChoices={setDeletedChoices}
                editedChoices={editedChoices}
                setEditedChoices={setEditedChoices}
                createdChoices={createdChoices}
                setCreatedChoices={setCreatedChoices}
                buttonName="Modifier"/>
            )}
          />
          </Steps>
        </Wizard>
      </div>
    </>
  )
}

export default EditPoll