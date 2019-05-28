package com.project.doodle.controllers;

import com.project.doodle.models.Choice;
import com.project.doodle.models.Poll;
import com.project.doodle.models.User;
import com.project.doodle.repositories.ChoiceRepository;
import com.project.doodle.repositories.PollRepository;
import com.project.doodle.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
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

    @GetMapping("/polls/{slug}/choices")
    public ResponseEntity<List<Choice>> retrieveAllChoicesFromPoll(@PathVariable String slug) {
        // On vérifie que le choix existe
        Optional<Poll> poll = pollRepository.findBySlug(slug);
        if (!poll.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        List<Choice> choices = choiceRepository.findAll(Sort.by(Sort.Direction.ASC,"startDate"));
        return new ResponseEntity<>(choices, HttpStatus.OK);
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

    @GetMapping("/polls/{slug}/choices/{idChoice}")
    public ResponseEntity<Choice> retrieveChoiceFromPoll(@PathVariable String slug, @PathVariable long idChoice) {
        // On vérifie que le choix et le poll existent
        Optional<Poll> poll = pollRepository.findBySlug(slug);
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

    @DeleteMapping("/polls/{slug}/choices")
    public ResponseEntity<?> deleteChoiceFromPoll(@RequestBody HashMap<String, List<Long>> choices, @PathVariable String slug, @RequestParam String token) {
        // On vérifie que le poll existe
        List<Long> idchoices = choices.get("choices");
        Optional<Poll> poll = pollRepository.findBySlug(slug);
        if (!poll.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        // On vérifie que le token soit bon
        if(!poll.get().getSlugAdmin().equals(token)){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        // On enlève les choix du poll
        for (Long id: idchoices) {
            // On vérifie que le choice existe
            Optional<Choice> choice = choiceRepository.findById(id);
            if (choice.isPresent()) {
                // On remove le choice du poll
                poll.get().removeChoice(choice.get());
                pollRepository.save(poll.get());
                // On remove le choices des utilisateurs
                for (User user:userRepository.findAll()) {
                    if(user.getUserChoices().contains(choice.get())){
                        user.getUserChoices().remove(choice.get());
                        userRepository.save(user);
                    }
                }
                // On supprime le choice
                choiceRepository.deleteById(id);
            }
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/polls/{slug}/choices")
    public ResponseEntity<List<Choice>> createChoices(@RequestBody List<Choice> choices, @PathVariable String slug, @RequestParam String token) {
        // On vérifie que le poll existe
        Optional<Poll> poll = pollRepository.findBySlug(slug);
        if (!poll.isPresent()){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        // On vérifie que le token soit bon
        if(!poll.get().getSlugAdmin().equals(token)){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        // On ajoute chaque choix au poll et vice versa
        for (Choice choice:choices) {
            poll.get().addChoice(choice);
            pollRepository.save(poll.get());
        }
        return new ResponseEntity<>(choices, HttpStatus.CREATED);
    }

    @PutMapping("/polls/{slug}/choices/{idChoice}")
    public ResponseEntity<Choice> updateChoice(@Valid @RequestBody Choice choice, @PathVariable String slug, @PathVariable long idChoice, @RequestParam String token) {
        // On vérifie que le poll et le choix existent
        Optional<Poll> pollOptional = pollRepository.findBySlug(slug);
        Optional<Choice> choiceOptional = choiceRepository.findById(idChoice);
        if (!pollOptional.isPresent() || !choiceOptional.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        // On vérifie que le choix appartienne bien au poll
        if(!pollOptional.get().getPollChoices().contains(choiceOptional.get())){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        // On vérifie que le token soit bon
        if(!pollOptional.get().getSlugAdmin().equals(token)){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        // On met à jour l'ancien choix
        Choice ancientChoice = choiceOptional.get();
        if (choice.getstartDate()!=null){
            ancientChoice.setstartDate(choice.getstartDate());
        }
        if (choice.getendDate()!=null){
            ancientChoice.setendDate(choice.getendDate());
        }
        // On update la bdd
        Choice updatedChoice = choiceRepository.save(ancientChoice);
        return new ResponseEntity<>(updatedChoice, HttpStatus.OK);
    }

    @PostMapping("/polls/{slug}/vote/{idUser}")
    public ResponseEntity<Object> vote(@RequestBody HashMap<String, List<Long>> choices, @PathVariable String slug, @PathVariable long idUser) {
        // On vérifie que le poll et l'utilisateur existent
        List<Long> idchoices = choices.get("choices");
        Optional<Poll> poll = pollRepository.findBySlug(slug);
        Optional<User> user = userRepository.findById(idUser);
        if (!poll.isPresent() || !user.isPresent()){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        for (Long choice : idchoices) {
            // On vérifie que le choice existe
            Optional<Choice> optchoice = choiceRepository.findById(choice);
            if (!optchoice.isPresent()){
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            // On vérifie que le choix appartienne bien au poll
            if(!poll.get().getPollChoices().contains(optchoice.get())){
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            // On vérifie que le user n'ai pas déjà voté pour ce choix
            if(user.get().getUserChoices().contains(optchoice.get())){
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            // On ajoute le choix à la liste de l'utilisateur et vice versa
            optchoice.get().addUser(user.get());
            choiceRepository.save(optchoice.get());
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/polls/{slug}/choices/{idChoice}/removevote/{idUser}")
    public ResponseEntity<Object> removeVote(@PathVariable String slug, @PathVariable long idChoice, @PathVariable long idUser) {
        // On vérifie que le poll, le choix et l'utilisateur existent
        Optional<Poll> poll = pollRepository.findBySlug(slug);
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

    @GetMapping("/polls/{slug}/choices/{idChoice}/count")
    public ResponseEntity<Object> numberOfVoteForChoice(@PathVariable String slug, @PathVariable long idChoice){
        // On vérifie que le poll et choix existent
        Optional<Poll> poll = pollRepository.findBySlug(slug);
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
