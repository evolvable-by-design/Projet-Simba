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
import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api")
public class PollResource {

    @Autowired
    private ChoiceRepository choiceRepository;
    @Autowired
    private PollRepository pollRepository;
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/polls")
    public ResponseEntity<List<Poll>> retrieveAllpolls() {
        // On récupère la liste de tous les poll qu'on trie ensuite par titre
        List<Poll> polls = pollRepository.findAll(Sort.by(Sort.Direction.ASC,"title"));
        return new ResponseEntity<>(polls, HttpStatus.OK);
    }

    @GetMapping("/polls/{idPoll}")
    public ResponseEntity<Poll> retrievePoll(@PathVariable long idPoll) {
        // On vérifie que le poll existe
        Optional<Poll> poll = pollRepository.findById(idPoll);
        if (!poll.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(poll.get(), HttpStatus.OK);
    }

    @DeleteMapping("/polls/{idPoll}")
    public ResponseEntity<Poll> deletePoll(@PathVariable long idPoll) {
        // On vérifie que le poll existe
        Optional<Poll> poll = pollRepository.findById(idPoll);
        if (!poll.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        // On supprime tous les choix du poll
        // Fait automatiquement par le cascade type ALL

        // On supprime le poll de la bdd
        pollRepository.deleteById(idPoll);
        return new ResponseEntity<>(poll.get(), HttpStatus.OK);
    }

    @PostMapping("/polls")
    public ResponseEntity<Poll> createPoll(@Valid @RequestBody Poll poll) {
        // On enregistre le poll dans la bdd
        Poll savedPoll = pollRepository.save(poll);
        return new ResponseEntity<>(savedPoll, HttpStatus.CREATED);
    }

    @PutMapping("/polls/{idPoll}")
    public ResponseEntity<Object> updatePoll(@Valid @RequestBody Poll poll, @PathVariable long idPoll) {
        // On vérifie que le poll existe
        Optional<Poll> optionalPoll = pollRepository.findById(idPoll);
        if (!optionalPoll.isPresent())
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        // On met au poll le bon id
        poll.setId(idPoll);
        // On enregistre le poll dans la bdd
        Poll updatedPoll = pollRepository.save(poll);
        return new ResponseEntity<>(updatedPoll, HttpStatus.OK);
    }
}
