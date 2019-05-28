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

    @GetMapping("/polls/{slug}")
    public ResponseEntity<Poll> retrievePoll(@PathVariable String slug, @RequestParam(required = false) String token) {
        // On vérifie que le poll existe
        Optional<Poll> poll = pollRepository.findBySlug(slug);
        if (!poll.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        // Si un token est donné, on vérifie qu'il soit bon
        if (token != null && !poll.get().getSlugAdmin().equals(token)){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<>(poll.get(), HttpStatus.OK);
    }

    @DeleteMapping("/polls/{slug}")
    public ResponseEntity<Poll> deletePoll(@PathVariable String slug, @RequestParam String token) {
        // On vérifie que le poll existe
        Optional<Poll> poll = pollRepository.findBySlug(slug);
        if (!poll.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        // On vérifie que le token soit bon
        if(!poll.get().getSlugAdmin().equals(token)){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        // On supprime tous les choix du poll
        // Fait automatiquement par le cascade type ALL

        // On supprime tous les commentaires du poll
        // Fait automatiquement par le cascade type ALL

        // On supprime le poll de la bdd
        pollRepository.deleteById(poll.get().getId());
        return new ResponseEntity<>(poll.get(), HttpStatus.OK);
    }

    @PostMapping("/polls")
    public ResponseEntity<Poll> createPoll(@Valid @RequestBody Poll poll) {
        // On enregistre le poll dans la bdd
        Poll savedPoll = pollRepository.save(poll);
        return new ResponseEntity<>(savedPoll, HttpStatus.CREATED);
    }

    @PutMapping("/polls/{slug}")
    public ResponseEntity<Object> updatePoll(@Valid @RequestBody Poll poll, @PathVariable String slug, @RequestParam String token) {
        // On vérifie que le poll existe
        Optional<Poll> optionalPoll = pollRepository.findBySlug(slug);
        if (!optionalPoll.isPresent())
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        // On vérifie que le token soit bon
        if(!optionalPoll.get().getSlugAdmin().equals(token)){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        // On met au poll le bon id et les bons slugs
        Poll ancientPoll = optionalPoll.get();
        // On met à jour l'ancien poll
        if (poll.getTitle()!=null){
            ancientPoll.setTitle(poll.getTitle());
        }
        if (poll.getLocation()!=null){
            ancientPoll.setLocation(poll.getLocation());
        }
        if (poll.getDescription()!=null){
            ancientPoll.setDescription(poll.getDescription());
        }
        ancientPoll.setHas_meal(poll.isHas_meal());
        // On enregistre le poll dans la bdd
        Poll updatedPoll = pollRepository.save(ancientPoll);
        return new ResponseEntity<>(updatedPoll, HttpStatus.OK);
    }
}
