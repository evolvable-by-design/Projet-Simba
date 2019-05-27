package com.project.doodle.controllers;

import com.project.doodle.models.Comment;
import com.project.doodle.models.Poll;
import com.project.doodle.models.User;
import com.project.doodle.repositories.ChoiceRepository;
import com.project.doodle.repositories.CommentRepository;
import com.project.doodle.repositories.PollRepository;
import com.project.doodle.repositories.UserRepository;
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
public class CommentResource {
    @Autowired
    private ChoiceRepository choiceRepository;
    @Autowired
    private PollRepository pollRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CommentRepository commentRepository;

    @GetMapping("users/{idUser}/comments")
    public ResponseEntity<List<Comment>> getAllCommentsFromUser(@PathVariable long idUser) {
        // On vérifie que l'utilisateur existe
        Optional<User> optUser = userRepository.findById(idUser);
        if(!optUser.isPresent()){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        List<Comment> userComments = new ArrayList<>();
        List<Comment> comments = commentRepository.findAll();
        if(!comments.isEmpty()) {
            for (Comment comment : comments) {
                if (comment.getUser().getId() == idUser) {
                    userComments.add(comment);
                }
            }
        }
        return new ResponseEntity<>(userComments, HttpStatus.OK);
    }

    @GetMapping("polls/{slug}/comments")
    public ResponseEntity<Object> getAllCommentsFromPoll(@PathVariable String slug) {
        // On vérifie que le poll existe
        Optional<Poll> optPoll = pollRepository.findBySlug(slug);
        if(!optPoll.isPresent()){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(optPoll.get().getPollComments(),HttpStatus.OK);
    }

    @GetMapping("polls/{slug}/comments/{idComment}")
    public ResponseEntity<Object> getCommentFromPoll(@PathVariable String slug, @PathVariable long idComment){
        // On vérifie que le poll et le comment existe
        Optional<Poll> optPoll = pollRepository.findBySlug(slug);
        Optional<Comment> optComment = commentRepository.findById(idComment);
        if(!optPoll.isPresent() || !optComment.isPresent()){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        // On vérifie que le comment appartienne bien au poll
        if (!optPoll.get().getPollComments().contains(optComment.get())){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(optComment.get(),HttpStatus.OK);
    }

    @PostMapping("polls/{slug}/comments/{idUser}")
    public ResponseEntity<Object> createComment(@Valid @RequestBody Comment comment, @PathVariable String slug, @PathVariable long idUser){
        // On vérifie que le poll et le User existe
        Optional<Poll> poll = pollRepository.findBySlug(slug);
        Optional<User> user = userRepository.findById(idUser);
        if (!poll.isPresent() || !user.isPresent()){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        // On set le user dans comment
        comment.setUser(user.get());
        // On ajoute le commentaire dans le poll
        poll.get().addComment(comment);
        pollRepository.save(poll.get());
        // On save le commentaire
        Comment savedComment = commentRepository.save(comment);
        return new ResponseEntity<>(savedComment, HttpStatus.CREATED);
        }

}


