package ressources;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import models.Comment;
import models.Poll;

///polls/id/comments
@Path("/polls/{idPoll}/comments")
@ApplicationScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CommentResource {

	/**
	 * 
	 * @param idPoll
	 * @return tous les commentaires d'un poll
	 */
	@GET
	@Path("")
	public List<Comment> getAllComments(@PathParam("idPoll") Long idPoll){
		return isPollExisting(idPoll).comments;
	}
	
	
	/**
	 * 
	 * @param idComment
	 * @return le commentaire dont l'id est IdComment d'un poll
	 */
	@GET
	@Path("/{idComment}")
	public Comment getOneComment(@PathParam("idPoll") Long idPoll, @PathParam("idComment") Long idComment){
		Poll poll = isPollExisting(idPoll);
		Comment comment = isCommentExisting(idComment);
		
		int index = poll.comments.indexOf(comment);
		
		if(index < 0) {
			throw new WebApplicationException("comment with id of " + idComment + " does not exist in this poll.", 404);
		}
		
		return comment;
	}
	
	/**
	 * Crée un commentaire dans le poll specifié par l'ID
	 */
	@Transactional
	@POST
	@Path("")
	public Comment createComment(@PathParam("idPoll") Long idPoll, @Valid Comment comment) {
		Poll poll = isPollExisting(idPoll);
		poll.comments.add(comment);
		comment.persist();
		return comment;
		
	}
	
	/**
	 * Supprime un commentaire dans un poll 
	 */
	@DELETE
	@Transactional
	@Path("{/{idComment}")
	public Response delete(@PathParam("idPoll") Long idPoll, @PathParam("idComment") Long idComment) {
		Poll poll = isPollExisting(idPoll);
		Comment comment = isCommentExisting(idComment);
		poll.comments.remove(comment);
		if(comment.isPersistent()) {
			comment.delete();
		}

		return Response.status(204).build();
	}

	private Poll isPollExisting(Long id){
		Poll poll = Poll.findById(id);
		if(poll == null) {
			throw new WebApplicationException("Poll with id of " + id + " does not exist.", 404);
		}
		return poll;
	}

	private Comment isCommentExisting(Long id){
		Comment comment = Comment.findById(id);
		if(comment == null) {
			throw new WebApplicationException("Poll with id of " + id + " does not exist.", 404);
		}
		return comment;
	}
	
}
