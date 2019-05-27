import React, { useState , useEffect } from 'react'
import { Wizard, Steps, Step } from 'react-albus';
import Card from '../Card';
import './Poll.css';
import axios from 'axios';
import moment from 'moment'

moment.locale('fr');

const baseURL = "http://localhost:7777/api";
let mockdata = {
  "id" : 35,
  "title" : "Cinema",
  "location" : "Gaumont Rennes",
  "description" : "Un petit cine voila quoi ...",
  "has_meal" : true,
  "choices" : [
    {id: 1, start_date: "dsd", end_date: "dsdsd"},
    {id: 3, start_date: "d", end_date: "dsdsd"},
    {id: 5, start_date: "Avengers", end_date: "dsdsd"}],
  "users" : ["Kevin","Clement","Daouda","Salome"],
  "votes" :[
      {
          "id": 5,
          "choice":"Avengers",
          "vote":["Kevin","Clement"]
      },
      {
          "id": 4,
          "choice":"Detective Pikachu",
          "vote":["Salome","Daouda"]
      },
      {
          "id": 5,
          "choice":"Tchoupi Origins",
          "vote":["Clement"]
      }
  ]
}



const Informations = ({data,next}) => {

  const [choices, setChoices] = useState([])

  const handleVote = () => {
    console.log(choices)
    console.log("Submit vote")
  }

  const footer = (
    <>
      { 
      <button className="Btn-primary" onClick={handleVote}>Confirmer</button>
      }
    </>
  )

  return (
    <Card title={data.title} subtitle="par XXXXXXX • il y a XXXXXXXXX heures" footer={footer}>
      <div className="Poll_Form">
        { data.location && <p className="Poll_Location">{data.location}</p>}
        { data.has_meal && 
          <div className="Poll_Has_Meal">
            <p>Cet évènement contient un repas</p>
            <button className="Btn-primary" id="Meal_Preferences_Button" title="Bientôt disponible" onClick={setMealPreference}>Indiquer ses préférences alimentaires</button>
          </div>
        }
        { data.description && <p className="Poll_Description">{data.description}</p>}
        

        <div className="Poll_Vote_Wrapper">
          <PollTable data={data} setChoices={setChoices} choices={choices}/>
        </div>

      </div>
    </Card>
  )
}

const PollTable = (props) => {
  return (
    <div className="Poll_Vote_Content">
        <Users data={props.data}/>
        <Choices {...props}/>

    </div>
  )
}

const Users =({data}) =>{
  if (!data) {
    return(<p>Loading ...</p>)
  }
  return (
    <aside>
      <header className="Cell_Poll_Header">
        <div className="Cell_Participants_Header"></div>
        <div className="Cell_Participant_Count">
          <span>{/*data.pollUsers.length*/} participant{/*data.pollUsers.length<2 ? "":"s"*/}</span>
        </div>
        <div className="Cell_New_Participant">
          <input type="text" id="newParticipantName" placeholder="Saisissez nom" required="required" maxLength="64"/>
        </div>
      </header>
      <ul className="Cell_Participants">
        {/*data.pollUsers.map((User)=>(
          <li className="Cell_Participant" key={User.id}>
            {User.username}
          </li>
        )
        )*/}
      </ul>
    </aside>
  )
  
}

const Choices = ({data, setChoices, choices}) =>{
  if (!data) {
    return(<p>Loading ...</p>)
  }

  const handleVote = (e, id) => {
    let checked = e.target.checked
    console.log(checked)
    console.log(choices)
    if(checked) {
      setChoices([...choices, id])
    } else {
      const index = choices.indexOf(id)
      if(index !== -1) {
        let newChoices = choices.splice(index, 1)
        setChoices(newChoices)
      }
    }
    console.log(choices)

  }
  
  return (
    <ul className="Cell_Options">
      {data.pollChoices.map((choice)=>(
          <li className="Cell_Option" key={choice.id}>
            <label className="Cell_Poll_Header">
              <div className="Cell_Option_Name">
                {moment(choice.start_date).format('Do MMMM h:mm')} - {moment(choice.end_date).format('h:mm')}
                id : {choice.id}
              </div>
              <div className="Cell_Option_Count">
                <span>{choice.users.length} V</span>
              </div>
              <div className="Cell_Option_New_Participant_Vote">
                <input type="checkbox" checked={choices.indexOf(choice.id) !== -1} onChange={(e) => handleVote(e, choice.id)}></input>
              </div>
            </label>
            <ul className="Cell_Option_Votes">
        
            </ul>
          </li>
        )
      )}

           


    </ul>


  )

}

const ChoiceEx = () =>{
  return(
  <li className="Cell_Option">
    <label className="Cell_Poll_Header">
      <div className="Cell_Option_Name">
        Nom du choix
      </div>
      <div className="Cell_Option_Count">
        <span>X V</span>
      </div>
      <div className="Cell_Option_New_Participant_Vote">
        <input type="checkbox"></input>
      </div>
    </label>
    <ul className="Cell_Option_Votes">

    </ul>
  </li>)
}


const setMealPreference = () =>{

}


const Poll = (props) => {
  const {id} = props.match.params
  const [data, setData] = useState(false)
  useEffect(()=>{
    axios.get(`${baseURL}/polls/${id}`).then(res => {
    setData(res.data)
    })
  },[])

  return (
    <div className="Container">
    <Wizard>
    <Steps>
      <Step
        id="poll"
        render={({next }) => (
          <Informations data={data} next={next}/>
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