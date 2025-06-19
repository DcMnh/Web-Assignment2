/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cst8218.jeffin.slider.service;

import cst8218.jeffin.slider.entity.Slider;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.List;

/**
 *
 * @author leoje
 */
@Stateless
@Path("slider")
public class SliderFacadeREST extends AbstractFacade<Slider> {
    
    private static final Integer DEFAULT_SIZE = 50;
    private static final Integer DEFAULT_X = 0;
    private static final Integer DEFAULT_Y = 0;
    private static final Integer DEFAULT_TRAVEL = 0;
    private static final Integer DEFAULT_MAX_TRAVEL = 100;
    private static final Integer DEFAULT_DIR_CHANGE = 0;
    private static final Integer DEFAULT_DIRECTION = 1;
    
    @PersistenceContext(unitName = "my_persistence_unit")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
    
    public SliderFacadeREST() {
        super(Slider.class);
    }

    @POST
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response createPost(Slider entity, @Context UriInfo uriInfo) {
        super.create(entity);
        URI location = URI.create(uriInfo.getRequestUri().getPath() + "/" + entity.getId());
        return Response.status(Response.Status.CREATED).location(location).entity(entity).build();
    }

    @PUT
    @Path("{id}")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response edit(@PathParam("id") Long id, Slider entity) {
        Slider existingSlider = super.find(id);
        if (existingSlider == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        entity.setId(id);
        super.edit(entity);
        // Return a 200 OK response with the updated entity
        return Response.status(Response.Status.OK).entity(entity).build();
    }

    @DELETE
    @Path("{id}")
    public Response remove(@PathParam("id") Long id) {
        Slider slider = super.find(id);
        if (slider == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        super.remove(slider);
        return Response.status(Response.Status.NO_CONTENT).build(); // Return 204 No Content
    }

    @GET
    @Path("{id}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response find(@PathParam("id") Long id) {
        Slider slider = super.find(id);
        if (slider == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(slider).build(); // Return 200 OK with the found entity
    }

    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response findAllSlider() {
        List<Slider> sliders = super.findAll();
        return Response.ok(sliders).build(); // Return 200 OK with the list of sliders
    }

    @GET
    @Path("{from}/{to}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response findRange(@PathParam("from") Integer from, @PathParam("to") Integer to) {
        List<Slider> sliders = super.findRange(new int[]{from, to});
        return Response.ok(sliders).build();
    }

    /*@GET
    @Path("count")
    @Produces(MediaType.TEXT_PLAIN)
    public Response countREST() {
        return Response.ok(String.valueOf(super.count())).build();
    }*/
    
    @GET
    @Path("count")
    @Produces(MediaType.TEXT_PLAIN)
    public Response countSliders() {
        // Return the count of Slider entities in the database
        return Response.ok(String.valueOf(super.count())).build();
    }

    @POST
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public Response createOrUpdateSlider(Slider slider, @Context UriInfo uriInfo) {
        if (slider.getId() == null) {
            // ID is null, so create a new Slider
            super.create(slider);
            URI location = uriInfo.getAbsolutePathBuilder().path(slider.getId().toString()).build();
            return Response.status(Response.Status.CREATED).location(location).entity(slider).build();
        } else {
            // ID is provided, so we need to check if it exists
            Slider existingSlider = super.find(slider.getId());
            if (existingSlider != null) {
                // ID exists, update the existing Slider
                existingSlider.updateWithNonNullValues(slider); // Assuming you have this method in Slider class
                super.edit(existingSlider);
                return Response.ok(existingSlider).build();
            } else {
                // ID does not exist, return 400 Bad Request
                return Response.status(Response.Status.BAD_REQUEST)
                               .entity("Slider with ID " + slider.getId() + " does not exist.")
                               .build();
            }
        }
    }

    @POST
    @Path("{id}")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public Response updateSlider(@PathParam("id") Long id, Slider newSlider) {
        // Step 1: Check if the Slider with the given ID exists
        Slider existingSlider = super.find(id);
        if (existingSlider == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                           .entity("Slider with ID " + id + " does not exist.")
                           .build();
        }

        // Step 2: Validate that the ID in the request body matches the URL
        if (newSlider.getId() != null && !newSlider.getId().equals(id)) {
            return Response.status(Response.Status.BAD_REQUEST)
                           .entity("ID in request body does not match URL ID.")
                           .build();
        }

        // Step 3: Update the existing Slider with new values from the request body using updateWithNewValues
        existingSlider.updateWithNonNullValues(newSlider);

        // Step 4: Save the updated Slider back to the database
        super.edit(existingSlider);

        // Step 5: Return a success response
        return Response.status(Response.Status.OK)
                       .entity(existingSlider)
                       .build();
    }


    @PUT
    @Path("{id}")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public Response replaceSlider(@PathParam("id") Long id, Slider newSlider) {
        // Step 1: Check if the ID in the request body matches the URL ID (if ID is provided in body)
        if (newSlider.getId() != null && !newSlider.getId().equals(id)) {
            return Response.status(Response.Status.BAD_REQUEST)
                           .entity("ID in the request body does not match the URL ID.")
                           .build();
        }

        // Step 2: Check if the Slider with the given ID exists
        Slider existingSlider = super.find(id);
        if (existingSlider == null) {
            return Response.status(Response.Status.NOT_FOUND)
                           .entity("Slider with ID " + id + " does not exist.")
                           .build();
        }

        // Step 3: Replace the existing Slider's attributes with new values or set default values
        existingSlider.setSize(newSlider.getSize() != null ? newSlider.getSize() : DEFAULT_SIZE);
        existingSlider.setX(newSlider.getX() != null ? newSlider.getX() : DEFAULT_X);
        existingSlider.setY(newSlider.getY() != null ? newSlider.getY() : DEFAULT_Y);
        existingSlider.setCurrentTravel(newSlider.getCurrentTravel() != null ? newSlider.getCurrentTravel() : DEFAULT_TRAVEL);
        existingSlider.setMaxTravel(newSlider.getMaxTravel() != null ? newSlider.getMaxTravel() : DEFAULT_MAX_TRAVEL);
        existingSlider.setDirChangeCount(newSlider.getDirChangeCount() != null ? newSlider.getDirChangeCount() : DEFAULT_DIR_CHANGE);
        existingSlider.setMvtDirection(newSlider.getMvtDirection() != null ? newSlider.getMvtDirection() : DEFAULT_DIRECTION);

        // Step 4: Persist the changes by updating the entity in the database
        super.edit(existingSlider);

        // Step 5: Return a successful response with the updated entity
        return Response.ok(existingSlider).build();
    }

    @PUT
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public Response handlePutOnRoot() {
        // Step 6: Return a 405 Method Not Allowed response for PUT on the root resource
        return Response.status(Response.Status.METHOD_NOT_ALLOWED)
                       .entity("PUT on the root resource (entire Slider table) is not allowed.")
                       .build();
    }

}
