package com.project.doodle.controllers;

import com.project.doodle.models.Choice;
import com.project.doodle.models.Poll;
import com.project.doodle.models.User;
import com.project.doodle.repositories.ChoiceRepository;
import com.project.doodle.repositories.PollRepository;
import com.project.doodle.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api")
public class ChoiceResource {

    @Autowired
    private ChoiceRepository choiceRepository;
    @Autowired
    private PollRepository pollRepository;
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/polls/{idPoll}/choices")
    public ResponseEntity<List<Choice>> retrieveAllChoicesFromPoll(@PathVariable long idPoll) {
        // On vérifie que le choix existe
        Optional<Poll> poll = pollRepository.findById(idPoll);
        if (!poll.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(poll.get().getPollChoices(), HttpStatus.OK);
    }

    @GetMapping("/users/{idUser}/choices")
    public ResponseEntity<List<Choice>> retrieveAllChoicesFromUser(@PathVariable long idUser) {
        // On vérifie que l'utilisateur existe
        Optional<User> user = userRepository.findById(idUser);
        if (!user.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(user.get().getUserChoices(), HttpStatus.OK);
    }

    @GetMapping("/polls/{idPoll}/choices/{idChoice}")
    public ResponseEntity<Choice> retrieveChoiceFromPoll(@PathVariable long idPoll, @PathVariable long idChoice) {
        // On vérifie que le choix et le poll existent
        Optional<Poll> poll = pollRepository.findById(idPoll);
        Optional<Choice> choice = choiceRepository.findById(idChoice);
        if (!poll.isPresent() || !choice.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        // On vérifie que le choix appartienne bien au poll
        if(!poll.get().getPollChoices().contains(choice.get())){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(choice.get(), HttpStatus.OK);
    }

    @GetMapping("/users/{idUser}/choices/{idChoice}")
    public ResponseEntity<Choice> retrieveChoiceFromUser(@PathVariable long idUser, @PathVariable long idChoice) {
        // On vérifie que le choix et l'utilisateur existent
        Optional<User> user = userRepository.findById(idUser);
        Optional<Choice> choice = choiceRepository.findById(idChoice);
        if (!user.isPresent() || !choice.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        // On vérifie que le choix appartienne bien à l'utilisateur
        if(!user.get().getUserChoices().contains(choice.get())){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(choice.get(), HttpStatus.OK);
    }

    @DeleteMapping("/polls/{idPoll}/choices/{idChoice}")
    public ResponseEntity<Choice> deleteChoiceFromPoll(@PathVariable long idPoll, @PathVariable long idChoice) {
        // On vérifie que le poll et le choix existent
        Optional<Poll> poll = pollRepository.findById(idPoll);
        Optional<Choice> choice = choiceRepository.findById(idChoice);
        if (!poll.isPresent() || !choice.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        // On vérifie que le choix appartienne bien au poll
        if(!poll.get().getPollChoices().contains(choice.get())){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        // On enlève le choix du poll
        poll.get().removeChoice(choice.get());
        pollRepository.save(poll.get());
        // On enlève le choix de la liste de chaque utilisateur
        for (User user:choice.get().getUsers()) {
            user.removeChoice(choice.get());
            userRepository.save(user);
        }
        // On supprime le choix de la bdd
        choiceRepository.deleteById(idChoice);
        return new ResponseEntity<>(choice.get(), HttpStatus.OK);
    }

    @PostMapping("/polls/{idPoll}/choices")
    public ResponseEntity<List<Choice>> createChoices(@Valid @RequestBody List<Choice> choices, @PathVariable long idPoll) {
        // On vérifie que le poll existe
        Optional<Poll> poll = pollRepository.findById(idPoll);
        if (!poll.isPresent()){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        // On ajoute chaque choix au poll et vice versa
        for (Choice choice:choices) {
            poll.get().addChoice(choice);
            pollRepository.save(poll.get());
        }
        return new ResponseEntity<>(choices, HttpStatus.CREATED);
    }

    @PutMapping("/polls/{idPoll}/choices/{idChoice}")
    public ResponseEntity<Choice> updateChoice(@Valid @RequestBody Choice choice, @PathVariable long idPoll, @PathVariable long idChoice) {
        // On vérifie que le poll et le choix existent
        Optional<Poll> pollOptional = pollRepository.findById(idPoll);
        Optional<Choice> choiceOptional = choiceRepository.findById(idChoice);
        if (!pollOptional.isPresent() || !choiceOptional.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        // On vérifie que le choix appartienne bien au poll
        if(!pollOptional.get().getPollChoices().contains(choiceOptional.get())){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        // On met le bon id sur le nouveau choix
        choice.setId(idChoice);
        // On update la bdd
        Choice updatedChoice = choiceRepository.save(choice);
        return new ResponseEntity<>(updatedChoice, HttpStatus.OK);
    }

    @PostMapping("/polls/{idPoll}/choices/{idChoice}/vote/{idUser}")
    public ResponseEntity<Object> vote(@PathVariable long idPoll, @PathVariable long idChoice, @PathVariable long idUser) {
        // On vérifie que le poll, le choix et l'utilisateur existent
        Optional<Poll> poll = pollRepository.findById(idPoll);
        Optional<Choice> choice = choiceRepository.findById(idChoice);
        Optional<User> user = userRepository.findById(idUser);
        if (!poll.isPresent() || !choice.isPresent() || !user.isPresent()){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        // On vérifie que le choix appartienne bien au poll
        if(!poll.get().getPollChoices().contains(choice.get())){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        // On vérifie que le user n'ai pas déjà voté pour ce choix
        if(user.get().getUserChoices().contains(choice.get())){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        // On ajoute le choix à la liste de l'utilisateur et vice versa
        choice.get().addUser(user.get());
        choiceRepository.save(choice.get());

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/polls/{idPoll}/choices/{idChoice}/removevote/{idUser}")
    public ResponseEntity<Object> removeVote(@PathVariable long idPoll, @PathVariable long idChoice, @PathVariable long idUser) {
        // On vérifie que le poll, le choix et l'utilisateur existent
        Optional<Poll> poll = pollRepository.findById(idPoll);
        Optional<Choice> choice = choiceRepository.findById(idChoice);
        Optional<User> user = userRepository.findById(idUser);
        if (!poll.isPresent() || !choice.isPresent() || !user.isPresent()){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        // On vérifie que le choix appartienne bien au poll
        if(!poll.get().getPollChoices().contains(choice.get())){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        // On vérifie que le user ait bien voté pour ce choix
        if(!user.get().getUserChoices().contains(choice.get())){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        // On retire le choix à la liste de l'utilisateur et vice versa
        choice.get().removeUser(user.get());
        choiceRepository.save(choice.get());
        user.get().removeChoice(choice.get());
        userRepository.save(user.get());

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/polls/{idPoll}/choices/{idChoice}/count")
    public ResponseEntity<Object> numberOfVoteForChoice(@PathVariable long idPoll,@PathVariable long idChoice){
        // On vérifie que le poll et choix existent
        Optional<Poll> poll = pollRepository.findById(idPoll);
        Optional<Choice> choice = choiceRepository.findById(idChoice);
        if (!poll.isPresent() || !choice.isPresent()){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        // On vérifie que le choix appartienne bien au poll
        if(!poll.get().getPollChoices().contains(choice.get())){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        // On compte le nombre de vote pour le choix
        return new ResponseEntity<>(choice.get().getUsers().size(),HttpStatus.OK);
    }
}
