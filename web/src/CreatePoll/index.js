import React, { useState } from 'react'
import { Wizard, Steps, Step } from 'react-albus';
import Card from '../Card';
import './CreatePoll.css'

const Informations = ({next, previous}) => {


  let [title, setTitle] = useState("")
  let [description, setDescription] = useState("")
  let [location, setLocation] = useState("")
  const footer = (
    <>
      { previous && 
      <button className="Btn-primary" onClick={previous}>Précedent</button>
      }
      { next && 
      <button className="Btn-primary" onClick={next}>next</button>
      }
    </>
  )

  return (
    <Card title="Informations" footer={footer}>
      <div className="CreatePoll_Form">
        <div className="CreatePoll_Input">
          <input value={title} type="text" placeholder="Titre" onChange={(e)=>setTitle(e.target.value)}/>
        </div>
        <div className="CreatePoll_Input">
          <textarea placeholder="Description" onChange={(e)=>setDescription(e.target.value)}>
          {description}
          </textarea>
        </div>
        <div className="CreatePoll_Input">
          <input value={location} type="text" placeholder="Localisation" onChange={(e)=>setLocation(e.target.value)}/>
        </div>
      </div>
    </Card>
  )
}

const CreatePoll = () => {


  return (

    <Wizard>
    <Steps>
      <Step
        id="merlin"
        render={({ next }) => (
          <Informations next={next}/>
        )}
      />
      <Step
        id="gandalf"
        render={({ next, previous }) => (
          <div>
            <h1>Gandalf</h1>
            <button onClick={next}>Next</button>
            <button onClick={previous}>Previous</button>
          </div>
        )}
      />
      </Steps>
    </Wizard>
    
  )
}

export default CreatePoll