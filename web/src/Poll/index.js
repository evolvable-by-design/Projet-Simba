import React, { useState , useEffect } from 'react'
import axios from 'axios';
import BigCalendar from 'react-big-calendar'
import moment from 'moment'

import {Link} from 'react-router-dom';

import 'react-big-calendar/lib/css/react-big-calendar.css'
import { BASE_URL, CALENDAR_MESSAGES } from '../utils/constants'
import Card from '../Card';
import './Poll.css';

moment.locale('fr');
const localizer = BigCalendar.momentLocalizer(moment)

const Informations = ({adminToken, slug, data, users, refreshDataAndUsers}) => {
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

    /*if(choices.indexOf(choice.id) !== -1){
      title += "✅"
    }*/
    return {start: new Date(choice.startDate), end: new Date(choice.endDate), title: title, resource: choice.id}
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
      return
    }

    axios.post(`${BASE_URL}/users`, {
      username,
      first_name: "",
      last_name: "",
      email: ""
    })
      .then((res) => {
        let {id:  userId} = res.data
        axios.post(`${BASE_URL}/polls/${slug}/vote/${userId}`, {choices})
        .then((voteRes) => {
          refreshDataAndUsers()
          setUsername("")
          setChoices([])
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
    <div className="Poll_Vote_Action">
      { 
        <button className="Btn-primary green" onClick={handleVote}>Voter !</button>
      }
    </div>
  )

  const subtitle = (id) => (
    <div className="Poll_Subtitle">
      <div>
      <span>Créé {moment(data.createdAt).fromNow()}</span>
      { adminToken &&
        <Link className="Edit_Link" to={`/polls/${slug}/edit?t=${adminToken}`}>Modifier</Link>
      }
      </div>
    </div>
  )

  return (
    <Card title={data.title} subtitle={subtitle(slug)} footer={footer} style={{borderRadius: "5px 0 5px 5px", borderTop: "2px solid #4D3DF7"}}>
      <div className="Poll_Form">
        { data.description && 
          <div className="Poll_Description">
            <p>{data.description}</p></div>
        }
        <div className="Poll_Infos">
        { data.location && <p className="Poll_Location">
          <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" className="feather feather-map-pin"><path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0 1 18 0z"></path><circle cx="12" cy="10" r="3"></circle></svg>
        {data.location}
        </p>}
        { data.has_meal && 
          <div className="Poll_Has_Meal">
            <svg className="feather" aria-hidden="true" width="20" height="20" focusable="false" data-prefix="fas" data-icon="utensils"  role="img" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 416 512"><path fill="currentColor" d="M207.9 15.2c.8 4.7 16.1 94.5 16.1 128.8 0 52.3-27.8 89.6-68.9 104.6L168 486.7c.7 13.7-10.2 25.3-24 25.3H80c-13.7 0-24.7-11.5-24-25.3l12.9-238.1C27.7 233.6 0 196.2 0 144 0 109.6 15.3 19.9 16.1 15.2 19.3-5.1 61.4-5.4 64 16.3v141.2c1.3 3.4 15.1 3.2 16 0 1.4-25.3 7.9-139.2 8-141.8 3.3-20.8 44.7-20.8 47.9 0 .2 2.7 6.6 116.5 8 141.8.9 3.2 14.8 3.4 16 0V16.3c2.6-21.6 44.8-21.4 48-1.1zm119.2 285.7l-15 185.1c-1.2 14 9.9 26 23.9 26h56c13.3 0 24-10.7 24-24V24c0-13.2-10.7-24-24-24-82.5 0-221.4 178.5-64.9 300.9z"></path></svg>
            <p>Cet évènement contient un repas</p>
          </div>
        }
        </div>
        
        <div className="Poll_Btns">
          <button className={"Poll_View_Btn" +  (view === 0 ? ' active' : '')} onClick={() => setView(0)}>
            <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" className="feather feather-grid"><rect x="3" y="3" width="7" height="7"></rect><rect x="14" y="3" width="7" height="7"></rect><rect x="14" y="14" width="7" height="7"></rect><rect x="3" y="14" width="7" height="7"></rect></svg>
            Tableau</button>
          <button className={"Poll_View_Btn" +  (view === 1 ? ' active' : '')} onClick={() => setView(1)}>
          <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" className="feather feather-calendar"><rect x="3" y="4" width="18" height="18" rx="2" ry="2"></rect><line x1="16" y1="2" x2="16" y2="6"></line><line x1="8" y1="2" x2="8" y2="6"></line><line x1="3" y1="10" x2="21" y2="10"></line></svg>
          Calendrier</button>
        </div>

        { view === 0 &&
          <div className="Poll_Vote_Wrapper">
            <PollTable username={username} setUsername={setUsername} data={data} setChoices={setChoices} choices={choices} users={users} usernameError={usernameError}/>
          </div>
        }
        { view === 1 && 
          <CalendarView slots={slots} handleSelect={handleSelectEvent} choices={choices}/>
        }

      </div>  
      <div className="Meal_Preferences_Toggle">
        <span>Régime alimentaire particulier ?</span>
        <label className="switch" htmlFor="hasMeal">
            <input id="hasMeal" type="checkbox" checked={hasMeal} onChange={(e) => setMeal(e.target.checked)} value=""/>
            <div className="slider round"></div>
        </label>
      </div>
      { hasMeal && 
        <textarea></textarea>
      }
    </Card>
  )
}

const CalendarView = ({slots, handleSelect, choices}) => {

  const eventStyleGetter = (event, start, end, isSelected) => {
    const isChecked = choices.indexOf(event.resource) !== -1
    let backgroundColor = isChecked ? "#3EBD93" : "#4D3DF7" ; 
    var style = {
        backgroundColor: backgroundColor,
        border: '0'
    };
    return {
        style: style
    };
  }

  return (
    <BigCalendar 
          events={slots}
          min={new Date(1970, 1, 1, 6)}
          views={['day', 'week', 'month']}
          localizer={localizer}
          startAccessor="start"
          endAccessor="end"
          defaultView="week"
          messages={CALENDAR_MESSAGES}
          onSelectEvent={handleSelect}
          defaultDate={slots[0].start}
          eventPropGetter={eventStyleGetter}
      />
  )
}

const PollTable = (props) => {
  return (
    <div className="Poll_Vote_Content">
        <Choices {...props}/>
    </div>
  )
}

const Choices = ({data, setChoices, choices, users, usernameError: error, username, setUsername}) =>{

  const handleVote = (id) => {
    const index = choices.indexOf(id)
    if(index === -1) {
      setChoices([...choices, id])
    } else {
      let newChoices = [...choices]
      newChoices.splice(index, 1)
      setChoices(newChoices)
    }
  }

  const usersChoice = {}
  data.pollChoices.forEach(choice => {
    usersChoice[choice.id] = choice.users.map(user => user.id) 
  })
  
  return (
      <ul className="Cell_Options">
        <li className="Cell_Header">
            <div className="Cell_Poll_Header">
              <div className="Cell_Header_Name">
              </div>
              <div className="Cell_Participant_Count">
                <span>{users.length} participant{users.length < 2 ? "" : "s"}</span>
              </div>
              <div className="Cell_Header_New_Participant_Vote">
                
              </div>
            </div>
            <div className="Cell_New_Participant">
            <input type="text" id="newParticipantName" placeholder="Saisissez un nom" maxLength="64" value={username} className={"Cell_New_Participant_Input" + (error ?  " error" : "")} onChange={(e) => setUsername(e.target.value)}/>
          </div>
            <ul className="Cell_Participants">
              { users && users.map((user)=> (
                <li key={user.id} className="Cell_Vote">{user.username}</li>
              )
              )}
            </ul>
        </li>
        {data.pollChoices.map((choice)=>(
            <li className="Cell_Option" key={choice.id}>
              <div className="Cell_Poll_Header">
                <div className="Cell_Option_Name">
                  <div className="Cell_Day">
                  {moment(choice.startDate).format('Do MMMM')}
                  </div>
                  <div className="Poll_Date">
                    <div className="Poll_Start_Date">{moment(choice.startDate).format('H:mm')}</div>
                    <div className="Poll_Separator">-</div>
                    <div className="Poll_Start_Date">{moment(choice.endDate).format('H:mm')}</div>
                  </div>
                </div>
                <div className="Cell_Option_Count">
                  <span>{choice.users.length}</span>
                </div>
                <div className="Cell_Option_New_Participant_Vote">
                    <button className={"Checkbox_Btn" + (choices.indexOf(choice.id) !== -1 ? " Active" : "")} onClick={(e) => handleVote(choice.id)}>{choices.indexOf(choice.id) !== -1 && 
                    <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 20 20" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><polyline points="20 6 9 17 4 12"></polyline></svg>
                    }
                    </button>
                  { false && <input type="checkbox" checked={choices.indexOf(choice.id) !== -1} onChange={(e) => handleVote(choice.id)}/>}
                </div>
              </div>
              <ul className="Cell_Option_Votes">
                { users && users.map((user)=> (
                  <li key={user.id} className={"Cell_Vote" + (usersChoice[choice.id].indexOf(user.id) === -1 ? "" : " selected")}>
                    { usersChoice[choice.id].indexOf(user.id) !== -1 && 
                      <span><svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 20 20" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><polyline points="20 6 9 17 4 12"></polyline></svg></span>
                    }
                  </li>
                )
                )}
              </ul>
            </li>
          )
        )}
      </ul>
  )

}

const Poll = (props) => {
  let url = new URLSearchParams(props.location.search)
  let token = url.get('t')

  const {slug} = props.match.params
  const [data, setData] = useState(false)
  const [users, setUsers] = useState(false)

  const refreshDataAndUsers = () => {
    axios.get(`${BASE_URL}/polls/${slug}`)
    .then(res => {
      setData(res.data)
    })
    .catch((err) => {
      props.history.push('/')
    })

    axios.get(`${BASE_URL}/polls/${slug}/users`)
    .then((res) => {
      setUsers(res.data)
    })
    .catch((err) => {
      console.error(err)
    })
  }

  useEffect(()=>{
    refreshDataAndUsers()
  },[slug])

  return (
    <div className="Container">
      <div className="Links">
        <a href={data.padURL} className="Feat_Link" target="_blank" rel="noopener noreferrer">
          <svg xmlns="http://www.w3.org/2000/svg" style={{marginRight: "0.5rem"}} width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="feather feather-paperclip"><path d="M21.44 11.05l-9.19 9.19a6 6 0 0 1-8.49-8.49l9.19-9.19a4 4 0 0 1 5.66 5.66l-9.2 9.19a2 2 0 0 1-2.83-2.83l8.49-8.48"></path></svg>
          Pad
        </a>
        <a href={`${BASE_URL}/polls/${slug}/results`} className="Feat_Link" target="_blank" rel="noopener noreferrer">
        <svg xmlns="http://www.w3.org/2000/svg" style={{marginRight: "0.5rem"}} width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="feather feather-download"><path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"></path><polyline points="7 10 12 15 17 10"></polyline><line x1="12" y1="15" x2="12" y2="3"></line></svg>
        Exporter</a>
      </div>
      <Informations adminToken={token} slug={slug} data={data} users={users} refreshDataAndUsers={refreshDataAndUsers}/>
    </div>
  )
}

export default Poll