INSERT INTO poll(id, slug, title, location, description, has_meal, type, created_at, listUsers, admin, comments, choices)
	VALUES (nextval('hibernate_sequence'), 'slug', 'premier poll', 'esir', 'rencontre des grands', false, Text, to_date('07-05-2019','dd-MM-YYYY'),
		null, 'moi', null, null) 
