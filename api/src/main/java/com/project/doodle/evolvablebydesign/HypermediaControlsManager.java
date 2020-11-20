package com.project.doodle.evolvablebydesign;

import com.project.doodle.models.Choice;
import com.project.doodle.models.Poll;
import com.project.doodle.models.User;

public interface HypermediaControlsManager {

  static HypermediaRepresentation<User> addUserLinks(User user) {
    return HypermediaRepresentation.of(user)
      .withLink("commentPoll")
      .withLink("vote")
      .build();
  }

  static HypermediaRepresentation<Poll> addPollLinks(Poll poll) {
    return HypermediaRepresentation.of(poll)
      .withLink("comment")
      .withLink("vote")
      .withLink("update")
      .withLink("addChoice")
      .withLink("deleteChoice")
      .withLink("updateChoice")
      .build();
  }

  static HypermediaRepresentation<Choice> addChoiceLinks(Choice choice) {
    return HypermediaRepresentation.of(choice)
      .withLink("delete")
      .withLink("update")
      .build();
  }

}
