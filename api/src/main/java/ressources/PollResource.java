package ressources;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import io.quarkus.panache.common.Sort;
import models.Poll;

@Path("/polls")
@ApplicationScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PollResource {


	@GET
	//@Path("{polls}")
	public List<Poll> getAll(){
		return Poll.listAll(Sort.by(("name")));
	}
	
	@GET
	@Path("{id}")
	public Poll get(@PathParam(value = "id") Long id){
		Poll entity = Poll.findById(id);
		if (entity == null) {
			throw new WebApplicationException("Poll with id of " + id + " does not exist.", 404);
		}
		return entity;
	}


	@POST
	@Transactional
	public Response create(@Valid Poll poll) {
		if(poll.id != null) {
			throw new WebApplicationException("Id was invalidly set on request.", 422);
		}

		poll.persist();
		//return Response.ok(poll).status(201).build();
		return Response.status(Response.Status.CREATED).entity(poll).build();
	}

	@PUT
	@Path("{id}")
	@Transactional
	public Poll update(@PathParam(value = "id") Long id, @Valid Poll poll) {
		if(poll.id == null) {
			throw new WebApplicationException("Poll ID was not set on request.", 422);
		}

		Poll entity = Poll.findById(id);

		if(entity == null) {
			throw new WebApplicationException("Poll with id of " + id + " does not exist.", 404);
		}

		entity.title = poll.title;
		entity.id = poll.id;
		entity.description = poll.description;
		entity.admin = poll.admin;
		entity.choices = poll.choices;
		entity.comments = poll.comments;
		entity.has_meal = poll.has_meal;
		entity.listUsers = poll.listUsers;
		entity.slug = poll.slug;
		entity.created_at = poll.created_at;
		entity.location = poll.location;
		entity.type = poll.type;

		return entity;

	}

	@DELETE
	@Path("{id}")
	@Transactional
	public Response deletePoll(@PathParam(value ="id") Long id) {
		Poll entity = Poll.findById(id);
		if (entity == null) {
			throw new WebApplicationException("Poll with id of " + id + " does not exist.", 404);
		}
		
		if(entity.isPersistent())	entity.delete();
		
		return Response.status(204).build();
	}
	
	/**
	 * Recupère le slug d'un poll dont l'id est id
	 * @param id
	 * @return
	 */
	@GET
	@Path("{id/slug}")
	public String getSlug(@PathParam("id") Long id) {
		Poll entity = Poll.findById(id);
		if (entity == null) {
			throw new WebApplicationException("Poll with id of " + id + " does not exist.", 404);
		}

		return entity.slug;
	}
	
	//TODO :
//	POST /polls/id/:slug/vote → Ajouter un vote
//	PATCH /polls/id/:slug/vote → Update pour une option
//	DELETE /polls/id/:slug/vote → Delete pour une option


}
