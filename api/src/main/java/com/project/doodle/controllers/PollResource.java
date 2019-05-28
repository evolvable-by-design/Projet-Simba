package com.project.doodle.controllers;

import com.project.doodle.models.Poll;
import com.project.doodle.repositories.PollRepository;
import net.gjerull.etherpad.client.EPLiteClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

import static com.project.doodle.Utils.generateSlug;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api")
public class PollResource {

    @Autowired
    private PollRepository pollRepository;

    private final String padUrl = "http://localhost:9001/";
    private final String apikey = "fa8cce291d03acaf1dce7d137f73ce60aa2eeebdec77be42bcb8461d0e4278ea";
    private EPLiteClient client = new EPLiteClient(padUrl, apikey);

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

    @GetMapping("/polls/{slug}/pad")
    public ResponseEntity<String> retrievePadURL(@PathVariable String slug){
        // On vérifie que le poll existe
        Optional<Poll> poll = pollRepository.findBySlug(slug);
        if (!poll.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(poll.get().getPadURL(), HttpStatus.OK);
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

        // On supprime le pad
        client.deletePad(getPadId(poll.get()));
        // On supprime le poll de la bdd
        pollRepository.deleteById(poll.get().getId());
        return new ResponseEntity<>(poll.get(), HttpStatus.OK);
    }

    @PostMapping("/polls")
    public ResponseEntity<Poll> createPoll(@Valid @RequestBody Poll poll) {
        // On enregistre le poll dans la bdd
        String padId = generateSlug(6);
        client.createPad(padId);
        initPad(poll.getTitle(), poll.getLocation(), poll.getDescription(), client, padId);
        poll.setPadURL(padUrl+"p/"+padId);
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
        // On se connecte au pad
        String padId = getPadId(ancientPoll);

        // On sauvegarde les anciennes données pour mettre à jour le pad
        String title = ancientPoll.getTitle();
        String location = ancientPoll.getLocation();
        String description = ancientPoll.getDescription();

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
        // On update le pad
        String ancientPad = (String) client.getText(padId).get("text");
        ancientPad = ancientPad.replaceFirst(title, ancientPoll.getTitle());
        ancientPad = ancientPad.replaceFirst(location, ancientPoll.getLocation());
        ancientPad = ancientPad.replaceFirst(description, ancientPoll.getDescription());
        client.setText(padId, ancientPad);
        // On enregistre le poll dans la bdd
        Poll updatedPoll = pollRepository.save(ancientPoll);
        return new ResponseEntity<>(updatedPoll, HttpStatus.OK);
    }

    private static void initPad(String pollTitle, String pollLocation, String pollDescription, EPLiteClient client, String padId) {
        final String str = pollTitle+'\n'+
                "Localisation : "+pollLocation+'\n'+
                "Description : "+pollDescription+'\n';
        client.setText(padId, str);
    }

    private static String getPadId(Poll poll){
        return poll.getPadURL().substring(poll.getPadURL().length()-6);
    }
}
