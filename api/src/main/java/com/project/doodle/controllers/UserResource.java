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
@CrossOrigin(origins = "http://localhost:8080")
@RequestMapping("/api")
public class UserResource {

    @Autowired
    private UserRepository userRepository;


    @GetMapping("/users")
    public ResponseEntity<List<User>> retrieveAllUsers() {
        // On récupère tous les utilisateurs qu'on trie ensuite par username
        List<User> users = userRepository.findAll(Sort.by(Sort.Direction.ASC,"username"));
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @GetMapping("/users/{idUser}")
    public ResponseEntity<User> retrieveUser(@PathVariable long idUser) {
        // On vérifie que l'utilisateur existe
        Optional<User> user = userRepository.findById(idUser);
        if (!user.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(user.get(), HttpStatus.OK);
    }

    @DeleteMapping("/users/{idUser}")
    public ResponseEntity<User> deleteUser(@PathVariable long idUser) {
        // On vérifie que l'utilisateur existe
        Optional<User> user = userRepository.findById(idUser);
        if (!user.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        // On supprime l'utilisateur de la liste d'utilisateur de chaque poll
        for (Poll poll: user.get().getUserPolls()) {
            poll.removeUser(user.get());
        }
        // On supprime l'utilisateur de la liste d'utilisateur de chaque choix
        for (Choice choice: user.get().getUserChoices()) {
            choice.removeUser(user.get());
        }
        // On supprime l'utilisateur de la bdd
        userRepository.deleteById(idUser);
        return new ResponseEntity<>(user.get(), HttpStatus.OK);
    }

    @PostMapping("/users")
    public ResponseEntity<User> createUser(@Valid @RequestBody User user) {
        // On vérifie que l'utilisateur n'existe pas déjà
        Optional<User> optionalUser = userRepository.findById(user.getId());
        if (optionalUser.isPresent()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        // On sauvegarde l'utilisateur dans la bdd
        User savedUser = userRepository.save(user);
        return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
    }

    @PutMapping("/users/{idUser}")
    public ResponseEntity<User> updateUser(@PathVariable long idUser, @Valid @RequestBody User user) {
        // On vérifie que l'utilisateur existe
        Optional<User> optionalUser = userRepository.findById(user.getId());
        if (!optionalUser.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        // On met le bon id sur l'utilisateur
        user.setId(idUser);
        // On update l'utilisateur dans la bdd
        User updatedUser = userRepository.save(user);
        return new ResponseEntity<>(updatedUser, HttpStatus.OK);
    }
}
