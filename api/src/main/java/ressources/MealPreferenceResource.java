package ressources;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import io.quarkus.panache.common.Sort;
import models.MealPreference;
import models.UserEntity;

@Path("/mealpreferences")
@ApplicationScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class MealPreferenceResource {
	
	@GET
	public List<MealPreference> getAll(){
		return MealPreference.listAll(Sort.by("preference"));
	}
	
	@GET
	@Path("{id}")
	public MealPreference getone(@PathParam("id") Long id) {
		MealPreference entity = MealPreference.findById(id);
		
		if(entity == null) {
			throw new WebApplicationException("MealPreference with id of " + id + " does not exist.", 404);
		}
		
		return entity;
	}
	
	@POST
	@Transactional
	public Response create(@Valid MealPreference pref) {
		MealPreference preference = new MealPreference();
		preference.id = pref.id;
		preference.preference = pref.preference;
		preference.persist();
		
		return Response.status(Response.Status.CREATED).entity(preference).build();

	}
}
