import React, { useState, useEffect } from 'react'
import { Wizard, Steps, Step } from 'react-albus';
import { Link } from 'react-router-dom'
import axios from 'axios';
import moment from 'moment'
import BigCalendar from 'react-big-calendar'
import withDragAndDrop from 'react-big-calendar/lib/addons/dragAndDrop'

import 'react-big-calendar/lib/addons/dragAndDrop/styles.css'
import { Informations } from '../CreatePoll';
import { CALENDAR_MESSAGES } from '../utils/constants'
import { useBaseUrl } from '../utils/apiVersionManager'
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

  const handleCreate = (event) => {
    if(!sameDay(event.start, event.end)) return
    setChoices([...choices, {start: event.start, end: event.end, title: ""}])

    setCreatedChoices([...createdChoices, {start: event.start, end: event.end, title: ""}])
  }

  const handleSelect = (event) => {

    console.log("select")

    const newChoices = [...choices]
    newChoices.splice(choices.indexOf(event), 1)
    // Enregistrer les choices deleted
    setChoices(newChoices)

    if(event.resource !== undefined) {
      setDeletedChoices([...deletedChoices, event])
    }

  }

  const handleResize = (event) => {
    let idx = -1;

    for (let i = 0; i < choices.length; i++) {
      const choice = choices[i];
      if(choice.resource === event.event.resource) {
        idx = i;
        break;
      }
    }


    if(idx === -1) return;

    const editedEvent = {start: event.start, end: event.end, resource: choices[idx].resource}

    const newChoices = [...choices]
    console.log(newChoices)
    newChoices.splice(idx, 1)

    newChoices.push(editedEvent)
    setChoices(newChoices)

    const newEditedChoices = editedChoices.filter(choice => {
      return choice.resource !== choices[idx].resource
    })

    setEditedChoices([...newEditedChoices, editedEvent])
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
  const [initialChoices, setInitialChoices] = useState([])
  const [createdChoices, setCreatedChoices] = useState([])
  const [editedChoices, setEditedChoices] = useState([])
  const [deletedChoices, setDeletedChoices] = useState([])

  const apiBaseUrl = useBaseUrl()

  useEffect(() => {
    axios.get(`${apiBaseUrl}/polls/${slug}`)
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
          start: new Date(choice.startDate),
          end: new Date(choice.endDate),
          resource: choice.id,
        }
      })
      setChoices(newChoices)
      setInitialChoices(newChoices)
    })
    .catch((err) => {
      props.history.push('/')
    })
  }, [slug])

  const editPoll = () => {
    let requests = []

    requests.push(axios.put(`${apiBaseUrl}/polls/${slug}?token=${token}`, {
        title,
        location,
        description,
        has_meal: hasMeal,
        pollChoices: [],
      }))

      const initialChoicesDic = {}
      initialChoices.forEach(choice => initialChoicesDic[choice.resource] = choice)

      const initialIds = initialChoices.map(choice => choice.resource)
     

      choices.forEach(choice => {
        if(choice.resource !== undefined) {
          let initChoice = initialChoicesDic[choice.resource]
          console.log(initChoice)
          console.log(choice)
          if(initChoice.start !== choice.start || initChoice.end !== choice.end) {
            requests.push(axios.put(`${apiBaseUrl}/polls/${slug}/choices/${choice.resource}?token=${token}`, {
              startDate: choice.start,
              endDate: choice.end,
            }))
          }
        }
      })

      let choiceIds = choices.map(choice => choice.resource)
      choiceIds = choiceIds.filter(choice => choice !== undefined)
      let deletedIds = []
    
      initialIds.forEach(id => {
        if(choiceIds.indexOf(id) === -1) {
          deletedIds.push(id)
        }
      })

      if(deletedIds.length !== 0) {
        requests.push(axios.delete(`${apiBaseUrl}/polls/${slug}/choices/?token=${token}`, { data: { choices: deletedIds}}))
      }


      const createdChoices = choices.filter(choice => (choice.resource === undefined))
      const createdChoicesFormatted = createdChoices.map((choice) => {
        return {
          startDate: choice.start,
          endDate: choice.end,
          name: choice.name
        }
      })

      if(createdChoicesFormatted.length !== 0) {
        requests.push(axios.post(`${apiBaseUrl}/polls/${slug}/choices?token=${token}`, createdChoicesFormatted))
      }  

      
      Promise.all(requests)
      .then((v) => {
        props.history.push(`/polls/${slug}?t=${token}`)
      })
      .catch(err => {
        console.error(err)
      })
  }

  return (
    <>
      <div className="Container">
        <h1>
          Édition de <Link to={`/polls/${slug}?t=${token}`} className="Link">{title}</Link>
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