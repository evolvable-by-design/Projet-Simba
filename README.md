# Simba-Project

Page Speed Insights API Apps Script is an application developed by [cbdt](https://github.com/cbdt). It implements 14 API evolutions (see details below) that I use as a use-case to evaluate the evolvable-by-design approach that I created.

In this repository, the evolvable-by-design approach is implemented. Other projects are used as use cases, see the [evolvable-by-design organization](https://github.com/evolvable-by-design/).


### Repositories

- Repository with backend v1 and frontend: [barais/Projet-Simba](https://github.com/barais/Projet-Simba)
- Repository with backend v2: [barais/doodlebackend](https://github.com/barais/doodlebackend)
- [Fork with the evolvable-by-design implementation](https://github.com/evolvable-by-design/Projet-Simba/)

### List of evolutions:

1. `POST /api/polls/{slug}/comments/{userId}` with request body `{ content }` -> replaced with `POST /api/poll/comment/{slug}` with request body `{ auteur, content }`. Also the return type changed -- Types of evolutions here: (i) rename method (n°5), (ii) remove parameter (n°1), (iii) add parameter (n°1), (iv) change type of return value (n°3)
2. To create a comment in a poll, it was previously necessary to create a user and then create the comment. Now the two operations can be done at once. This adds another type of evolution: `the set of operations to execute to achieve a business process changed`
3. To answer a poll, with the v1 of the API it is necessary to (i) create a user with `POST /api/users`, then (ii) to vote with `POST /api/polls/{slug}/vote/{userId}` and finally (iii) to send the meal preferences with `POST /api/polls/{slug}/mealpreference/{userId}`. With the v2 of the API, it is now required to send more information to a single operation `POST /api/poll/choiceuser` -> This is a `the set of operations to execute to achieve a business process changed`
4. POST /api/polls -> POST /api/poll to create a new poll
5. The MealPreference feature is not supported by the v2 API anymore. Indeed, when a new preference is sent to the backend, it is stored in the database but not linked to the poll. Then, it is not possible to retrieve the meal preference. Yet, this design decision is not reflected into the API documentation that still mention the meal preferences. Reading the API server implementation is necessary to identify this evolution.
6. `GET /api/polls/{slug}` must be replaced by `GET /api/poll/aslug/{aslug}` if the adminSlug have to be retrieved. Otherwise, `GET /api/poll/slug/{slug}` must be used
7. `PUT /api/polls/${slug}?token=${token}` is replaced with `PUT /api/poll/update1` where the slug and token must be put into the request body. The name of `token` is changed into `slugAdmin` while being moved to the request body -> Types of evolutions: rename method (n°5) because of the URL change, rename parameter (n°6) (token -> slugAdmin) and move parameter (n°28) (token/slugAdmin from query to request body)
8. In v2 of the API, two calls are necessary to get the poll with its comments. A first one to get the poll at `GET /api/poll/slug/{slug}` or at `GET /api/poll/aslug/{aslug}` or at `GET /api/poll/{id}` and a second call is necessary to get the comments at `GET /api/poll/polls/{slug}/comments`. -> Type of evolution: move API elements. In addition, the returned model changed.
9. In order to update a poll, in v1 it is required to call `PUT /api/polls/${slug}?token=${token}` to update the main information, then to call another endpoint to create new choices, a third one to update some choices and a fourth one to delete the rest. In the v2 of the API, a single call to `PUT /api/poll/update1` is sufficient but a lot more information must be provided (see commit log) -> Types of evolutions: combine methods (n°10) because one operation should be called instead of a single one

**Amount of evolutions:** 14 (of 9 different types)

**Types of evolutions:**

* Add parameter (n°1) -> see evolution 1
* Change type of return value (n°3) -> see evolution 1 & 8
* Rename method (n°5) -> see evolutions 1 & 4 & 7
* Rename parameter (n°6) -> see evolution 7
* Combine methods (n°10) -> see evolution 9
* Move API elements (n°17) -> see evolution 6 & 8
* The set of operations to execute to achieve a business process changed (n°26) -> see evolutions 2 & 3
* Move parameter (n°28) -> see evolution 7
* Remove return value (n°29) -> see evolution 5

**Commits with the evolutions:**

This use-case is different from the others because the authors of the web UI that I use did not modify its code to enable it to work with the v2 of the API. Indeed, the v2 has been developed by another developer that also created a new, slightly different, web UI.

Therefore in this use-case, I identified and corrected the web UI code by myself, by connecting it to the v2 of the API and fixing the bugs one by one. 

As a consequence, the evolutions are not linked to specific commits. As an alternative, all the evolutions are visible in a single Github Pull Request: https://github.com/evolvable-by-design/Projet-Simba/pull/3/files.

### Applying the evolvable-by-design approach for the observed evolutions

For this fifth use-case, I will describe the work done in one subsection per evolution because there are a lot more evolutions than in the previous use-cases. Hence, to avoid putting an overload linked to the set-up of the library and semantic vocabulary on the first evolution results (lines of code modified, etc.) I put this preliminary step in a separate subsection and commit.

Evolution 9 is the only one evolution that can not be adressed because it is of kind "combine methods", which implies changes in the semantics of the API that this work do not intend to address.

**Description of the preliminary work done:**

1. Create a docker-compose file to start the barais/doodlebackend project
2. Set up `cors` on barais/doodlebackend
3. Started the web UI and fixed the bugs one by one, [see code changes here](https://github.com/evolvable-by-design/Projet-Simba/pull/3/files)
4. Create the evolvable-by-design compliant OpenApi documentation for the two versions of the API. It only contains the methods that are studied within this use-case to ease its readbility by removing unnecessary items.
5. Serve the v1 documentation from the v1 backend and according hypermedia controls (Projet-Simba) [see commit](https://github.com/evolvable-by-design/Projet-Simba/commit/529372f533857a4a5ca989000a3f9d0feede3689) and [fix](https://github.com/evolvable-by-design/Projet-Simba/commit/be62896a0999ee5ebd160b297fd0ff4ecf94f47a)
6. Serve the v2 documentation from the v2 backend and according hypermedia controls (doodlebackend) [see commit](https://github.com/evolvable-by-design/doodlebackend/commit/79f30387c684ac1807fe008ea7f2ec8347a32a85)
7. Update the ports on both REST API servers to easily switch between the v1 to v2 on the web UI [v1 server](https://github.com/evolvable-by-design/Projet-Simba/commit/e554747012fc416808048d9de58d28a1ce56284e), [v2 server](https://github.com/evolvable-by-design/doodlebackend/commit/9c969dd00afd8e81d30bc7b2001d68450c23d4b1)
8. Add a toggle in the header of the Web UI to easily switch between the v1 and v2 of the REST API [https://github.com/evolvable-by-design/Projet-Simba/commit/4e66db033a10a9310fbf1e6a740fc5d1d154467b].
9. [Add the semantic vocabulary into the Web UI](https://github.com/evolvable-by-design/Projet-Simba/commit/3c3b3efc249c39ce152b833f2dd052de441fbeb0)
10. [Add and configure the Pivo library into the Web UI](https://github.com/evolvable-by-design/Projet-Simba/commit/a7f99f0c2b2c490d6d7bb93947787f6632249641)

**Description of the work done for the evolution 4:**

I started with the evolution 4 because it covers the creation of a poll. Indeed, the creation of a poll is the first thing to do on the Web UI to then test the other features.

API documentation requirements:
* Document the poll creation operation for both version
* Semantically describe the operation, its parameters and return model
* Use the same semantic descriptor for the operation, or link them with a `owl:sameAs` property in one of them, or both of them. (here I use the same identifier because the semantics is strictly the same)

Work done on the Web UI:
1. Make sure the semantic identifier is available in the vocabulary
2. Use Pivo to make the call to create a new poll, [see commit](https://github.com/evolvable-by-design/Projet-Simba/commit/04cafc92ce2369700ecd6b424c6906ebf6a79697)
3. Use Pivo to read the data from the returned value (same commit as before)

**Description of the work done for evolutions 1 and 2:**

API documentation requirements:
* Document all the operations necessary to vote on a poll
* Add the proper links to the documentation along with the `comment` and `nextComment` relation keys
* Ensure the documentation of the V2 API will not break the Web UI

1. Create a utility function to execute a process with Pivo
2. Use pivo and the utility function to make the right call to the API

See commit tracing the code changes [here](https://github.com/evolvable-by-design/Projet-Simba/commit/7df816bbec037aebebf3233aef42609d2a046be5)

**Description of the work done for the evolution 3:**

API documentation requirements:
* Document all the operations necessary to vote on a poll
* Add the proper links to the documentation along with the `vote` and `nextVote` relation keys
* Ensure the documentation of the V2 API will not break the Web UI

1. Create a utility function to execute a process with Pivo
2. Use pivo and the utility function to make the right call to the API

See commit tracing the code changes [here](https://github.com/evolvable-by-design/Projet-Simba/commit/add115d1cd954de4fb0c5122d354a3b87828a00a)

**Description of the work done for the evolution 5:**

1. Create a function that determines if the MealPreference feature is available, using Pivo
2. Make the previously created function accessible everywhere in the web UI
3. Hide for the UI the elements related to the meal preferences when the feature is not available on the API

See commit tracing the code changes [here](https://github.com/evolvable-by-design/Projet-Simba/commit/32828e1e9a39615a6ab82a25bf22af9e1d03a2d1)

**Description of the work done for the evolution 6:**

API documentation requirements:
* Document the poll retrieval operation for both version
* Semantically describe the operations, their parameters and return model

Work done on the Web UI:
1. Make sure the semantic identifier is available in the vocabulary
2. Use Pivo to make the call on the edit page request
3. Make the same call on the main poll page

See commit tracing the code changes [here](https://github.com/evolvable-by-design/Projet-Simba/commit/7c6f3c9641afc36808729f0ef68f236d7248fb9a)

**Description of the work done for the evolution 7:**

API documentation requirements:
* Document the poll update operation for both version
* Semantically describe the operations, their parameters and return model

1. Make sure the semantic identifier is available in the vocabulary
2. Use Pivo to make the call on the edit page request and reuse the non-evolvable-by-design code used to manage the pollChoices parameter because it is the subject of evolution 9

See commit tracing the code changes [here](https://github.com/evolvable-by-design/Projet-Simba/commit/3d63899314e6405497a36e8cb7b7e02685cbe902)

Warning: move to the commit after this one to test, it fixes a bug on the v1 API.

**Description of the work done for the evolution 8:**

API documentation requirements:
* Document the poll retrieval operation for both version and the operation to list comments on v2
* Semantically describe the operations, their parameters and return model

1. Make sure the semantic identifier is available in the vocabulary
2. Use Pivo to make the call to retrieve the poll and then get the poll comments using the pivo library also. Last, retrieve all the data necessary for the UI and instance a class with the proper format for the UI.

See commit tracing the code changes [here](https://github.com/evolvable-by-design/Projet-Simba/commit/13cd0507b6ffcb67a9367a5e922962addbcbf669)

### Report

- How many evolutions? ➜ 14
- Types of evolutions ➜ Add parameter (n°1), Change type of return value (n°3), Rename method (n°5), Rename parameter (n°6), Combine methods (n°10), Move API elements (n°17), The set of operations to execute to achieve a business process changed (n°26), Move parameter (n°28), Remove return value (n°29)
- One or several commits? ➜ Several
- How many lines per commit for the original evolution? ➜ Evol 1 & 2: 10; evol 3: 15; evol 4: 2; evol 5: 66; evol 6: 5; evol 7: 9; evol 8: 8
- How many lines of code to implement the approach on the frontend? ➜ Evol 1 & 2: 56; evol 3: 15; evol 4: 15; evol 5: 28; evol 6: 24; evol 7: 15; evol 8: 19
- One or several developers ➜ Several, one team for the first version of the backend and a single developer for the second version
- If tests, broken? ➜ no tests
- Covered or not covered? 14/15 covered

### How to test the evolutions

1. Make sure you have [Docker](https://www.docker.com/products/docker-desktop) and Java 11 on your machine
2. Pull this repository twice, one to move between the web UI history and one for the v1 API: `git clone https://github.com/evolvable-by-design/Projet-Simba.git web-ui && git clone https://github.com/evolvable-by-design/Projet-Simba.git v1-api` and the v2 API repository `git clone https://github.com/evolvable-by-design/doodlebackend`
3. In doodlebackend, make sure you are on the `evolvable-by-design` branch: `git checkout evolvable-by-design`
4. Start the v1 API: `cd v1-api/api && chmod +x Run.sh && ./Run.sh`
5. Start the v2 API: `cd doodlebackend` then in one shell window: `docker-compose up`, wait for it to finish initializing (it can take one to two minutes) and when up and running, in another shell window (with java 11+ set): `./mvwn clean compile quarkus:dev`
6. Install the frontend dependencies: `cd web-ui/frontend && yarn install`

**First test: evolution 4**

Evolution 4 is a change of the URL to create a poll

1. Move to commit before evolution `git checkout 68d72f6`
2. Start the frontend `cd frontend && yarn start`
3. Go to `http://localhost:3000` and try to create a poll with the V1, it should work.
4. Toggle to use v2 on the web UI and create another poll, this time it should not work and you will see errors in the console
5. Move to the commit implementing the evolvable-by-design approach: `git checkout 04cafc9`
6. Once again, try to create a poll for the two versions: it should work for both versions

**Second test: evolution 1 and 2**

Evolution 1 and 2 are about the addition of comments to the poll

1. Move to commit before evolution `git checkout 9e7f1b1`
2. Start the frontend `cd frontend && yarn start`
3. Go to `http://localhost:3000` create a poll and comment it.
4. Toggle to use v2 on the web UI and create another poll, try to comment it, this time it should not work and you will see errors in the console
5. Move to the commit implementing the evolvable-by-design approach: `git checkout 7df816b`
6. Once again, create a poll and comment it for each API version, it should work for both versions

**Third test: evolution 3**

Evolution 3 is about voting on a poll.

1. Move to commit before evolution `git checkout 7df816b`
2. Start the frontend `cd frontend && yarn start`
3. Go to `http://localhost:3000` create a poll and vote on it.
4. Toggle to use v2 on the web UI and create another poll, try to vote on it, this time it should not work and you will see errors in the console
5. Move to the commit implementing the evolvable-by-design approach: `git checkout add115d`
6. Once again, create a poll and vote on it for each API version, it should work for both versions

**Fourth test: evolution 5**

Evolution 5 is about the management of the meal preferences for polls with a meal. In v2, this feature has been removed.

1. Move to commit before evolution `git checkout a284b96`
2. Start the frontend `cd frontend && yarn start`
3. Go to `http://localhost:3000` create a poll with a meal and add meal preferences to it, it should work.
4. Toggle to use v2 on the web UI and create another poll with a meal, try to add meal preferences, they should never be visible after creation, because the feature is not supported anymore in the API v2
5. Move to the commit implementing the evolvable-by-design approach: `git checkout 32828e1`
6. Once again, create a poll for each API version, it should not be possible with the v2 API as the UI elements enabling this feature are not visible anymore

**Fifth test: evolution 6**

Evolution 6 is about accessing the administration zone of a poll

1. Move to commit before evolution `git checkout fb98607`
2. Start the frontend `cd frontend && yarn start`
3. Go to `http://localhost:3000` create a poll, access the administration and try to modify it by clicking on "modifier", it should work. 
4. Toggle to use v2 on the web UI and do the same, this time you should see errors in the console.
5. Move to the commit implementing the evolvable-by-design approach: `git checkout 7c6f3c9`
6. Once again, create a poll for each API version, go to the administration zone and click the "Modifier" butotn. Do it with both versions of the API, it should work in all cases

**Sixth test: evolution 7**

Evolution 7 is about editing a poll

1. Move to commit before evolution `git checkout 8a41286`
2. Start the frontend `cd frontend && yarn start`
3. Go to `http://localhost:3000` create a poll, access the administration and try to modify it by clicking on "modifier" and filling the form, it should work for the modification of the main info (choices excluded). 
4. Toggle to use v2 on the web UI and do the same, this time you should see errors in the console.
5. Move to the commit implementing the evolvable-by-design approach: `git checkout 3d63889`
6. Once again, create a poll for each API version, go to the administration zone and modify the title for example. Do it with both versions of the API, it should work in all cases

**Seventh test: evolution 8**

Evolution 8 is about listing the comments of a poll

1. Move to commit before evolution `git checkout 42f1997`
2. Start the frontend `cd frontend && yarn start`
3. Go to `http://localhost:3000` create a poll, comment it a refresh the page, you should see the comments. 
4. Toggle to use v2 on the web UI and do the same, this time you should not see the comments after the refresh.
5. Move to the commit implementing the evolvable-by-design approach: `git checkout 13cd050`
6. Once again, create a poll for each API version, comment them, refresh the page and you should see the comments after the refresh

# Original README

[Demonstration video](https://www.youtube.com/watch?v=sa0EgcHezP4)

## Goal

The Simba Project is a school project realized by a group of students from the ESIR school. 

Simba organizer is a planning and survey application that can be used in a professional, friendly or associative context to plan and coordinate a meeting. Indeed, it allows you to easily find a date but also a common time slot to gather several people. For that, it couldn't be easier. First, the event organizer creates the poll and proposes several date choices. Then, the people invited vote for the dates that best suit them. This way, the organizer will be able to choose the best option. 

Simba organizer also offers several features such as the ability to post comments, indicate food preferences or export the survey. This also includes a chat and an etherpad generated from the survey data.

## How to use

You will find a `readme.md` on both api and web folders explaining how to install, launch and use the application.
