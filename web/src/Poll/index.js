import React, { useState , useEffect } from 'react'
import { Wizard, Steps, Step } from 'react-albus';
import axios from 'axios';
import BigCalendar from 'react-big-calendar'
import moment from 'moment'


import 'react-big-calendar/lib/css/react-big-calendar.css'
import { BASE_URL } from '../utils/constants'
import Card from '../Card';
import './Poll.css';

moment.locale('fr');
const localizer = BigCalendar.momentLocalizer(moment)


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


const Informations = ({pollId, data, users, refreshDataAndUsers}) => {
  const [choices, setChoices] = useState([])
  const [username, setUsername] = useState("")
  const [view, setView] = useState(0)
  const [usernameError, setUsernameError] = useState(false)
  const [hasMeal, setMeal] = useState(false)

  if(!data) {
    return <>Loading...</>
  }

  const slots = data.pollChoices.map(choice => {
    let title = ""
    /*const usernames = choice.users.map(u => u.username)
    if(usernames.length !== 0) {
      title += usernames.join('\n')
    }*/

    if(choices.indexOf(choice.id) !== -1){
      title += "✅"
    }
    return {start: new Date(choice.start_date), end: new Date(choice.end_date), title: title, resource: choice.id}
  }) 

  const handleSelectEvent = (e) => {
    let choiceId = e.resource
    let idx = choices.indexOf(choiceId)
    if(idx === -1) {
      setChoices([...choices, choiceId])
    } else {
      let newChoices = [...choices]
      newChoices.splice(idx, 1)
      setChoices(newChoices)
    }
  }

  const handleVote = () => {

    if(username.trim() === "") {
      setUsernameError("Veuillez rentrer un username")
    }

    axios.post(`${BASE_URL}/users`, {
      username,
      first_name: "",
      last_name: "",
      email: ""
    })
      .then((res) => {
        let {id:  userId} = res.data
        axios.post(`${BASE_URL}/polls/${pollId}/vote/${userId}`, {choices})
        .then((voteRes) => {
          refreshDataAndUsers()
        })
        .catch((err) => {
          console.log(err)
        })
      })
      .catch((err) => {
        console.log(`Erreur lors de la création d'user ${err}`)
      })


  }

  const footer = (
    <>
      { 
        <button className="Btn-primary" onClick={handleVote}>Confirmer</button>
      }
    </>
  )

  const subtitle = (
    <>
      <span>Créé {moment(data.createdAt).fromNow()}</span>
    </>
  )

  return (
    <Card title={data.title} subtitle={subtitle} footer={footer}>
      <div className="Poll_Form">
        { data.location && <p className="Poll_Location">{data.location}</p>}
        { data.has_meal && 
          <div className="Poll_Has_Meal">
            <p>Cet évènement contient un repas</p>
            <button className="Btn-primary" id="Meal_Preferences_Button" title="Bientôt disponible" onClick={setMealPreference}>Indiquer ses préférences alimentaires</button>
          </div>
        }
        { data.description && <p className="Poll_Description">{data.description}</p>}
        
        <div className="Poll_Btns">
          <button className={"Poll_View_Btn" +  (view === 0 ? ' active' : '')} onClick={() => setView(0)}>Tableau</button>
          <button className={"Poll_View_Btn" +  (view === 1 ? ' active' : '')} onClick={() => setView(1)}>Calendrier</button>
        </div>

        { view === 0 &&
          <div className="Poll_Vote_Wrapper">
            <PollTable username={username} setUsername={setUsername} data={data} setChoices={setChoices} choices={choices} users={users} usernameError={usernameError}/>
          </div>
        }
        { view === 1 && 
          <CalendarView slots={slots} handleSelect={handleSelectEvent} />
        }

      </div>  
      <div className="Meal_Preferences_Toggle">
        <span>Régime alimentaire particulier ?</span>
        <label class="switch" for="hasMeal">
            <input id="hasMeal" type="checkbox" checked={hasMeal} onChange={(e) => setMeal(e.target.checked)} value=""/>
              <div class="slider round"></div>
        </label>
      </div>
      { hasMeal && 
        <textarea></textarea>
      }
    </Card>
  )
}

const CalendarView = ({slots, handleSelect}) => {
  return (
    <BigCalendar 
          events={slots}
          min={new Date(1970, 1, 1, 6)}
          views={['day', 'week', 'month']}
          localizer={localizer}
          startAccessor="start"
          endAccessor="end"
          defaultView="week"
          messages={messages}
          onSelectEvent={handleSelect}
          defaultDate={slots[0].start}
      />
  )
}

const PollTable = (props) => {
  return (
    <div className="Poll_Vote_Content">
        <Users users={props.users} username={props.username} setUsername={props.setUsername} error={props.usernameError} />
        <Choices {...props}/>
    </div>
  )
}

const Users =({users, username, setUsername, error}) =>{
  if (!users) {
    return(<p>Loading ...</p>)
  }
  return (
    <aside>
      <header className="Cell_Poll_Header">
        <div className="Cell_Participants_Header"></div>
        <div className="Cell_Participant_Count">
          <span>{users.length} participant{users.length<2 ? "":"s"}</span>
        </div>
        <div className="Cell_New_Participant">
          <input type="text" id="newParticipantName" placeholder="Saisissez un nom" maxLength="64" value={username} className={"Cell_New_Participant_Input" + (error ?  " error" : "")} onChange={(e) => setUsername(e.target.value)}/>
        </div>
      </header>
      <ul className="Cell_Participants">
        {users.map((user)=>(
          <li className="Cell_Participant" key={user.id}>
            {user.username}
          </li>
        )
        )}
      </ul>
    </aside>
  )
  
}

const Choices = ({data, setChoices, choices, users}) =>{

  const handleVote = (e, id) => {
    let checked = e.target.checked
    if(checked) {
      setChoices([...choices, id])
    } else {
      const index = choices.indexOf(id)
      if(index !== -1) {
        let newChoices = [...choices]
        newChoices.splice(index, 1)
        setChoices(newChoices)
      }
    }
  }

  const usersChoice = {}
  data.pollChoices.forEach(choice => {
    usersChoice[choice.id] = choice.users.map(user => user.id) 
  })
  
  return (
    <ul className="Cell_Options">
      {data.pollChoices.map((choice)=>(
          <li className="Cell_Option" key={choice.id}>
            <label className="Cell_Poll_Header">
              <div className="Cell_Option_Name">
                {moment(choice.start_date).format('Do MMMM h:mm')} - {moment(choice.end_date).format('h:mm')}
              </div>
              <div className="Cell_Option_Count">
                <span>{choice.users.length}</span>
              </div>
              <div className="Cell_Option_New_Participant_Vote">
                <input type="checkbox" checked={choices.indexOf(choice.id) !== -1} onChange={(e) => handleVote(e, choice.id)}></input>
              </div>
            </label>
            <ul className="Cell_Option_Votes">
              { users && users.map((user)=> (
                <li key={user.id} className="Cell_Vote">{usersChoice[choice.id].indexOf(user.id) === -1 ? "" : "✅"}</li>
              )
              )}
            </ul>
          </li>
        )
      )}
    </ul>


  )

}

const setMealPreference = () =>{

}


const Poll = (props) => {
  const {id} = props.match.params
  const [data, setData] = useState(false)
  const [users, setUsers] = useState(false)

  const refreshDataAndUsers = () => {
    axios.get(`${BASE_URL}/polls/${id}`)
    .then(res => {
      setData(res.data)
    })
    .catch((err) => {
      console.error(err)
    })

  axios.get(`${BASE_URL}/polls/${id}/users`)
    .then((res) => {
      setUsers(res.data)
    })
    .catch((err) => {
      console.error(err)
    })
  }

  useEffect(()=>{
    refreshDataAndUsers()
  },[id])

  return (
    <div className="Container">
    <Wizard>
    <Steps>
      <Step
        id="poll"
        render={({next }) => (
          <Informations pollId={id} data={data} next={next} users={users} refreshDataAndUsers={refreshDataAndUsers}/>
        )}
      />
      <Step
        id="voted"
        render={({ next, previous }) => (
          <div>
            <h1>Merci d'avoir voté</h1>
            <button className="Btn-primary" onClick={next}>Revenir au sondage</button>
            <button className="Btn-primary" onClick={previous}>Revenir à l'acceuil</button>
          </div>
        )}
      />
      </Steps>
    </Wizard>
    </div>
  )
}

export default Poll