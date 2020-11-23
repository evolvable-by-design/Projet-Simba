import React, { useState , useEffect } from 'react'
import axios from 'axios';
import BigCalendar from 'react-big-calendar'
import moment from 'moment'

import {Link} from 'react-router-dom';

import copy from 'copy-text-to-clipboard'

import 'react-big-calendar/lib/css/react-big-calendar.css'
import { BASE_URL, CALENDAR_MESSAGES } from '../utils/constants'
import Card from '../Card';
import './Poll.css';

moment.locale('fr');
const localizer = BigCalendar.momentLocalizer(moment)

const Informations = ({adminToken, slug, data, users, refreshDataAndUsers, username, setUsername}) => {
  const [choices, setChoices] = useState([])
  const [view, setView] = useState(0)
  const [usernameError, setUsernameError] = useState(false)

  if(!data) {
    return <>Loading...</>
  }

  const slots = data.pollChoices.map(choice => {
    return {start: new Date(choice.startDate), end: new Date(choice.endDate), title: "", resource: choice.id}
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

    if(choices.length === 0) {
      return
    }

    axios.post(
      `${BASE_URL}/poll/choiceuser`,
      {
        choices,
        username
      }
    ).then((voteRes) => {
      refreshDataAndUsers()
      setUsername("")
      setChoices([])
    })
    .catch((err) => {
      console.log(err)
    })
  }

  const footer = (
    <div className="Poll_Vote_Action">
      { 
        <button style={{width: '100%', height: '50px'}} className="Btn-primary green" onClick={handleVote}>Voter !</button>
      }
    </div>
  )

  const subtitle = (id) => (
    <div className="Poll_Subtitle">
      <div className="Dates">
      <span>Créé {moment(data.createdAt).fromNow()}</span>
      { data.createdAt !== data.updatedAt && 
        <span>Modifié {moment(data.updatedAt).fromNow()}</span>
      } 
      </div>
    </div>
  )

  return (
    <>
      <Card title={data.title} subtitle={subtitle(slug)} footer={footer} style={{borderRadius: "0 0 5px 5px", borderTop: "2px solid #4D3DF7"}}>
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
      </Card>
      <PollInfo data={data} username={username} setUsername={setUsername} slug={slug} />
    </>
  )
}

const PollInfo = ({data, username, setUsername, slug}) => {
  return (
    <div className="Poll_Informations">
      <Comments data={data.pollComments} username={username} setUsername={setUsername} slug={slug} />
    </div>
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

const Choices = ({data, setChoices, choices, users, usernameError: error, username, setUsername}) => {
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

  const handleUsernameChange = (e) => {
    setUsername(e.target.value)
  }
  
  return (
      <ul className="Cell_Options">
        <li className="Cell_Header">
            <div className="Cell_Poll_Header">
              <div className="Cell_Header_Name">
              </div>
              <div className="Cell_Participant_Count">
                <span>{users.length} participant{users.length > 1 ? 's' : ''}</span>
              </div>
              <div className="Cell_Header_New_Participant_Vote">
                
              </div>
            </div>
            <div className="Cell_New_Participant">
            <input type="text" id="newParticipantName" placeholder="Saisissez un nom" maxLength="64" value={username} className={"Cell_New_Participant_Input" + (error ?  " error" : "")} onChange={handleUsernameChange}/>
          </div>
            <ul className="Cell_Participants">
              { users && users.map((user)=> (
                <li key={user.id} className="Cell_Vote">{user.username}</li>
              )
              )}
            </ul>
        </li>
        {data.pollChoices.map((choice, index)=>(
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
                    <button className={"Checkbox_Btn" + (choices.indexOf(choice.id) !== -1 ? " Active" : "") + (index === (data.pollChoices.length - 1) ? " LastCheck" : "")} onClick={(e) => handleVote(choice.id)}>{choices.indexOf(choice.id) !== -1 && 
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
                      <span><svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 20 20" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><polyline points="20 6 9 17 4 12"></polyline></svg></span>
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

const Comments = ({data, username, setUsername, slug}) => {

  const [comments, setComments] = useState(data)
  const [comment, setComment] = useState("")

  if(!comments) {
    return (
      <h2>
        Loading...
      </h2>
    )
  }

  const createComment = () => {
    axios.post(`${BASE_URL}/poll/comment/${slug}`, {content: comment, auteur: username})
      .then(res => (
        {
          content: res.data.content,
          user: {
            username: res.data.content
          }
        }
      ))
      .then(newComment => {
        setComment("")
        setComments([
          ...comments,
          newComment
        ])
      })
      .catch(err => {
        console.error(err)
      })
  }

  const handleChange = (e) => {
    setUsername(e.target.value)
  }

  return (
    <div className="Comments">
      <div className="Comment">
      <h2>{comments.length} commentaire{comments.length > 1 ? 's' : ''}</h2>
      <ul>
        { comments.map((comment) => (
          <li className="MealPref" key={comment.id}>
            <span className="Author_Comment">{ comment.user.username }</span> : <span>{comment.content}</span>
          </li>
        ))}
      </ul>
      </div>
      <div style={{padding: "1rem"}}>
        <input style={{padding: "1rem"}} type="text" placeholder="Jean" value={username} onChange={handleChange}/>
        <textarea value={comment} style={{padding: "1rem"}} onChange={e => setComment(e.target.value)} placeholder="Les dates proposées ne me conviennent pas..."/>
        <div className="Poll_Vote_Action">
          <button className="Btn-primary orange" onClick={createComment}>Créer</button>
        </div>
      </div>
    </div>
  )
}

const Poll = (props) => {
  let url = new URLSearchParams(props.location.search)
  let token = url.get('t')

  const {slug} = props.match.params
  const [data, setData] = useState(false)
  const [isModalOpened, setIsModalOpened] = useState(false)
  const [users, setUsers] = useState(false)
  const [username, setUsername] = useState("")

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

  const openExportModal = () => {
    setIsModalOpened(true)
  }

  return (
    <div className="Container">

      { isModalOpened &&
        <div className="modal" onClick={() => setIsModalOpened(false)}>
          <div className="Export_Modal" >
            <a className="Export Disabled" target="_blank" rel="noopener noreferrer">
              <svg aria-hidden="true" width="40px" height="40px" focusable="false" data-prefix="fas" data-icon="file-pdf" className="Export_Icon" role="img" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 384 512"><path fill="currentColor" d="M181.9 256.1c-5-16-4.9-46.9-2-46.9 8.4 0 7.6 36.9 2 46.9zm-1.7 47.2c-7.7 20.2-17.3 43.3-28.4 62.7 18.3-7 39-17.2 62.9-21.9-12.7-9.6-24.9-23.4-34.5-40.8zM86.1 428.1c0 .8 13.2-5.4 34.9-40.2-6.7 6.3-29.1 24.5-34.9 40.2zM248 160h136v328c0 13.3-10.7 24-24 24H24c-13.3 0-24-10.7-24-24V24C0 10.7 10.7 0 24 0h200v136c0 13.2 10.8 24 24 24zm-8 171.8c-20-12.2-33.3-29-42.7-53.8 4.5-18.5 11.6-46.6 6.2-64.2-4.7-29.4-42.4-26.5-47.8-6.8-5 18.3-.4 44.1 8.1 77-11.6 27.6-28.7 64.6-40.8 85.8-.1 0-.1.1-.2.1-27.1 13.9-73.6 44.5-54.5 68 5.6 6.9 16 10 21.5 10 17.9 0 35.7-18 61.1-61.8 25.8-8.5 54.1-19.1 79-23.2 21.7 11.8 47.1 19.5 64 19.5 29.2 0 31.2-32 19.7-43.4-13.9-13.6-54.3-9.7-73.6-7.2zM377 105L279 7c-4.5-4.5-10.6-7-17-7h-6v128h128v-6.1c0-6.3-2.5-12.4-7-16.9zm-74.1 255.3c4.1-2.7-2.5-11.9-42.8-9 37.1 15.8 42.8 9 42.8 9z"></path></svg>
              <span>PDF (Premium)</span>
            </a>
            <a className="Export" target="_blank" rel="noopener noreferrer" href={`${BASE_URL}/polls/${slug}/results`}>
              <svg aria-hidden="true" width="40px" height="40px" focusable="false" data-prefix="fas" data-icon="file-excel" className="Export_Icon" role="img" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 384 512"><path fill="currentColor" d="M224 136V0H24C10.7 0 0 10.7 0 24v464c0 13.3 10.7 24 24 24h336c13.3 0 24-10.7 24-24V160H248c-13.2 0-24-10.8-24-24zm60.1 106.5L224 336l60.1 93.5c5.1 8-.6 18.5-10.1 18.5h-34.9c-4.4 0-8.5-2.4-10.6-6.3C208.9 405.5 192 373 192 373c-6.4 14.8-10 20-36.6 68.8-2.1 3.9-6.1 6.3-10.5 6.3H110c-9.5 0-15.2-10.5-10.1-18.5l60.3-93.5-60.3-93.5c-5.2-8 .6-18.5 10.1-18.5h34.8c4.4 0 8.5 2.4 10.6 6.3 26.1 48.8 20 33.6 36.6 68.5 0 0 6.1-11.7 36.6-68.5 2.1-3.9 6.2-6.3 10.6-6.3H274c9.5-.1 15.2 10.4 10.1 18.4zM384 121.9v6.1H256V0h6.1c6.4 0 12.5 2.5 17 7l97.9 98c4.5 4.5 7 10.6 7 16.9z"></path></svg>
              <span>EXCEL</span>
            </a>
          </div>
        </div>
      }

      <div className="Links">
        <div className="Links_Left">
            <Link to={`/create`} className="Feat_Link Unique">
              <svg xmlns="http://www.w3.org/2000/svg" style={{marginRight: "0.5rem"}} width="16" height="16"  viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="feather feather-plus"><line x1="12" y1="5" x2="12" y2="19"></line><line x1="5" y1="12" x2="19" y2="12"></line></svg>
              Nouveau
            </Link>
          { token && 
            <Link to={`/polls/${slug}/edit?t=${data.slugAdmin}`} className="Feat_Link Unique">
              <svg xmlns="http://www.w3.org/2000/svg" style={{marginRight: "0.5rem"}} width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" className="feather feather-edit"><path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"></path><path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"></path></svg>
              Modifier
            </Link>
          }
          <a href="#" onClick={() => copy(`${window.location.protocol}//${window.location.host}/polls/${token}`)} className="Feat_Link">
              <svg xmlns="http://www.w3.org/2000/svg" style={{marginRight: "0.5rem"}} width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="feather feather-share"><path d="M4 12v8a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2v-8"></path><polyline points="16 6 12 2 8 6"></polyline><line x1="12" y1="2" x2="12" y2="15"></line></svg>
              Partager
            </a>
        </div>
        <div className="Links_Right">
          <a href={data.tlkURL} className="Feat_Link" target="_blank" rel="noopener noreferrer">
            <svg xmlns="http://www.w3.org/2000/svg" style={{marginRight: "0.5rem"}} width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" className="feather feather-message-circle"><path d="M21 11.5a8.38 8.38 0 0 1-.9 3.8 8.5 8.5 0 0 1-7.6 4.7 8.38 8.38 0 0 1-3.8-.9L3 21l1.9-5.7a8.38 8.38 0 0 1-.9-3.8 8.5 8.5 0 0 1 4.7-7.6 8.38 8.38 0 0 1 3.8-.9h.5a8.48 8.48 0 0 1 8 8v.5z"></path></svg>
            Chat
          </a>
          <a href={data.padURL} className="Feat_Link" target="_blank" rel="noopener noreferrer">
            <svg xmlns="http://www.w3.org/2000/svg" style={{marginRight: "0.5rem"}} width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" className="feather feather-paperclip"><path d="M21.44 11.05l-9.19 9.19a6 6 0 0 1-8.49-8.49l9.19-9.19a4 4 0 0 1 5.66 5.66l-9.2 9.19a2 2 0 0 1-2.83-2.83l8.49-8.48"></path></svg>
            Pad
          </a>
          <a href="#" className="Feat_Link" onClick={openExportModal}>
            <svg xmlns="http://www.w3.org/2000/svg" style={{marginRight: "0.5rem"}} width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" className="feather feather-download"><path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"></path><polyline points="7 10 12 15 17 10"></polyline><line x1="12" y1="15" x2="12" y2="3"></line></svg>
            Exporter
          </a>
        </div>
      </div>
      <Informations 
        adminToken={token} 
        slug={slug} 
        data={data} 
        users={users} 
        refreshDataAndUsers={refreshDataAndUsers}
        username={username}
        setUsername={setUsername}/>
    </div>
  )
}

export default Poll