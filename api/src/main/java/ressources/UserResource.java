package ressources;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
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
import models.UserEntity;

@Path("/users")
@ApplicationScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserResource {
	
	@GET
	public List<UserEntity> getAll(){
		return UserEntity.listAll(Sort.by("username"));
	}
	
	@GET
	@Path("{id}")
	public UserEntity getUser(@PathParam("id") Long id) {
		UserEntity entity = UserEntity.findById(id);
		if (entity == null) {
			throw new WebApplicationException("User with id of " + id + " does not exist.", 404);
		}
		return entity;
	}
	
	@POST
	@Transactional
	public Response create(@Valid UserEntity newUser ) {
		
		UserEntity user = newUser;
//		if(newUser.) {
//			throw new WebApplicationException("Id was invalidly set on request.", 422);
//		}
		user.persist();
		return Response.status(Response.Status.CREATED).entity(user).build();
	}
	
	@PUT
	@Path("{id}")
	@Transactional
	public UserEntity update(@PathParam(value = "id") Long id, @Valid UserEntity user) {
		
		UserEntity entity = UserEntity.findById(id);
		if(entity == null) {
			throw new WebApplicationException("User with id of " + id + " does not exist.", 404);
		}
		
		entity = user;
		entity.persist();
		
		return entity;
	}
	
	@DELETE
	@Path("{id}")
	@Transactional
	public Response deleteUser(@PathParam(value ="id") Long id) {
		UserEntity entity = UserEntity.findById(id);
		if (entity == null) {
			throw new WebApplicationException("User with id of " + id + " does not exist.", 404);
		}
		
		if(entity.isPersistent())	entity.delete();
		
		return Response.status(204).build();
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
