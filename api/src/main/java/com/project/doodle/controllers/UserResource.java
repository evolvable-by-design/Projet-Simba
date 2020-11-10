package com.project.doodle.controllers;

import com.project.doodle.evolvablebydesign.HypermediaControlsManager;
import com.project.doodle.evolvablebydesign.HypermediaRepresentation;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api")
public class UserResource {

    @Autowired
    private ChoiceRepository choiceRepository;
    @Autowired
    private PollRepository pollRepository;
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/users")
    public ResponseEntity<List<HypermediaRepresentation<User>>> retrieveAllUsers() {
        // On récupère tous les utilisateurs qu'on trie ensuite par username
        List<User> users = userRepository.findAll(Sort.by(Sort.Direction.ASC,"username"));
        return new ResponseEntity<>(withLinks(users), HttpStatus.OK);
    }

    @GetMapping("/users/{idUser}")
    public ResponseEntity<HypermediaRepresentation<User>> retrieveUser(@PathVariable long idUser) {
        // On vérifie que l'utilisateur existe
        Optional<User> user = userRepository.findById(idUser);
        if (!user.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(HypermediaControlsManager.addUserLinks(user.get()), HttpStatus.OK);
    }

    @GetMapping("/polls/{slug}/users")
    public ResponseEntity<List<HypermediaRepresentation<User>>> getAllUserFromPoll(@PathVariable String slug){
        List<User> users = new ArrayList<>();
        // On vérifie que le poll existe
        Optional<Poll> poll = pollRepository.findBySlug(slug);
        if (!poll.isPresent()){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        // On parcours les choix du poll pour récupérer les users ayant voté
        if (!poll.get().getPollChoices().isEmpty()) {
            for (Choice choice : poll.get().getPollChoices()) {
                if (!choice.getUsers().isEmpty()) {
                    for (User user : choice.getUsers()) {
                        // On vérifie que le user ne soit pas déjà dans la liste
                        if (!users.contains(user)) {
                            users.add(user);
                        }
                    }
                }
            }
        }
        return new ResponseEntity<>(withLinks(users), HttpStatus.OK);
    }

    @DeleteMapping("/users/{idUser}")
    public ResponseEntity<HypermediaRepresentation<User>> deleteUser(@PathVariable long idUser) {
        // On vérifie que l'utilisateur existe
        Optional<User> user = userRepository.findById(idUser);
        if (!user.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        // On supprime l'utilisateur de la liste d'utilisateur de chaque choix
        for (Choice choice: user.get().getUserChoices()) {
            choice.removeUser(user.get());
            choiceRepository.save(choice);
        }
        // On supprime les commentaires de l'utilisateurs
        // Fait automatiquement par le cascade type ALL

        // On supprime l'utilisateur de la bdd
        userRepository.deleteById(idUser);
        return new ResponseEntity<>(HypermediaControlsManager.addUserLinks(user.get()), HttpStatus.OK);
    }

    @PostMapping("/users")
    public ResponseEntity<HypermediaRepresentation<User>> createUser(@Valid @RequestBody User user) {
       // On sauvegarde l'utilisateur dans la bdd
        User savedUser = userRepository.save(user);
        return new ResponseEntity<>(HypermediaControlsManager.addUserLinks(savedUser), HttpStatus.CREATED);
    }

    @PutMapping("/users/{idUser}")
    public ResponseEntity<HypermediaRepresentation<User>> updateUser(@PathVariable long idUser, @Valid @RequestBody User user) {
        // On vérifie que l'utilisateur existe
        Optional<User> optionalUser = userRepository.findById(idUser);
        if (!optionalUser.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        // On met le bon id sur l'utilisateur
        User ancientUser = optionalUser.get();
        if (user.getUsername()!=null){
            ancientUser.setUsername(user.getUsername());
        }
        // On update l'utilisateur dans la bdd
        User updatedUser = userRepository.save(ancientUser);
        return new ResponseEntity<>(HypermediaControlsManager.addUserLinks(updatedUser), HttpStatus.OK);
    }

    private List<HypermediaRepresentation<User>> withLinks(List<User> users) {
        return users.stream()
          .map(HypermediaControlsManager::addUserLinks)
          .collect(Collectors.toList());
    }
}
