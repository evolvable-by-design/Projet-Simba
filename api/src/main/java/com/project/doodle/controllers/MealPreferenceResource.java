package com.project.doodle.controllers;

import com.project.doodle.models.MealPreference;
import com.project.doodle.models.Poll;
import com.project.doodle.models.User;
import com.project.doodle.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api")
public class MealPreferenceResource {
    @Autowired
    private ChoiceRepository choiceRepository;
    @Autowired
    private PollRepository pollRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MealPreferenceRepository mealPreferenceRepository;

    @GetMapping("polls/{slug}/mealpreferences")
    public ResponseEntity<Object> getAllMealPreferencesFromPoll(@PathVariable String slug) {
        // On vérifie que le poll existe
        Optional<Poll> optPoll = pollRepository.findBySlug(slug);
        if(!optPoll.isPresent()){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(optPoll.get().getPollMealPreferences(),HttpStatus.OK);
    }

    @GetMapping("polls/{slug}/mealpreference/{idMealPreference}")
    public ResponseEntity<Object> getMealPreferenceFromPoll(@PathVariable String slug, @PathVariable long idMealPreference){
        // On vérifie que le poll et la meal preference existe
        Optional<Poll> optPoll = pollRepository.findBySlug(slug);
        Optional<MealPreference> optMealPreference = mealPreferenceRepository.findById(idMealPreference);
        if(!optPoll.isPresent() || !optMealPreference.isPresent()){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        // On vérifie que la meal preference appartienne bien au poll
        if (!optPoll.get().getPollMealPreferences().contains(optMealPreference.get())){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(optMealPreference.get(),HttpStatus.OK);
    }

    @PostMapping("polls/{slug}/mealpreference/{idUser}")
    public ResponseEntity<Object> createMealPreference(@Valid @RequestBody MealPreference mealPreference, @PathVariable String slug, @PathVariable long idUser){
        // On vérifie que le poll et le User existe
        Optional<Poll> poll = pollRepository.findBySlug(slug);
        Optional<User> user = userRepository.findById(idUser);
        if (!poll.isPresent() || !user.isPresent()){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        // On set le user dans la meal preference
        mealPreference.setUser(user.get());
        // On ajoute la meal preference dans le poll
        poll.get().addMealPreference(mealPreference);
        pollRepository.save(poll.get());
        // On save la meal preference
        MealPreference savedMealPreference = mealPreferenceRepository.save(mealPreference);
        return new ResponseEntity<>(savedMealPreference, HttpStatus.CREATED);
    }
}



