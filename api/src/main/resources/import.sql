INSERT INTO MealPreference(id, preference ) VALUES(12345, 'vegan');

INSERT INTO UserEntity(id,  created_at, email, first_name, is_temp, last_name, Userpassword, updated_at, username, meal_preference) VALUES(12, to_date('07-05-2019','dd-MM-YYYY'),'moi@univ-rennes1.fr', 'moi', FALSE, 'Mmoi', 'password', to_date('07-05-2019','dd-MM-YYYY'), 'MMmoi', 12345);

INSERT INTO Poll(id, created_at, descriptionPoll, has_meal, locationPoll, slug, title, typePoll, adminPoll) VALUES (234, to_date('07-05-2019','dd-MM-YYYY'), 'premier poll', TRUE, 'esir', 'slug', 'rencontre des grands', 'Text', 12) ;

INSERT INTO Choice(id, choiceCounter, end_date, full_day, nameChoice,dateDebut, votes_limit, poll_id, userId) VALUES(245, 5, to_date('07-05-2019','dd-MM-YYYY'),TRUE,'premier choix', to_date('07-05-2019','dd-MM-YYYY'), 8, 234, 12); 

INSERT INTO Comment(id, comment, created_at, poll_id, userId) VALUES(23, 'commentaire', to_date('07-05-2019','dd-MM-YYYY'), 234, 12 );