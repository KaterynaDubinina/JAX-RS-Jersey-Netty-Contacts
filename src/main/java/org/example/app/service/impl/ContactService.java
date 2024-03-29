package org.example.app.service.impl;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.example.app.domain.user.Contact;
import org.example.app.domain.user.ContactResponse;
import org.example.app.domain.user.ContactsResponse;
import org.example.app.repository.impl.ContactRepository;
import org.example.app.service.AppService;
import org.example.app.utils.ResponseMessage;

import java.util.*;

@Path("/api/v1/contacts")
@Produces({MediaType.APPLICATION_JSON})
public class ContactService implements AppService<Contact> {

   ContactRepository repository = new ContactRepository();

    @POST
    @Consumes({MediaType.APPLICATION_JSON})
    public Response create(Contact contact) {
        repository.create(contact);
        Optional<Contact> optional = repository.getLastContact();
        ContactResponse response;
        if (optional.isPresent()) {
            response =
                    new ContactResponse(optional.get());
            return Response.ok(response.toString())
                    .status(Response.Status.CREATED).build();
        } else {
            return Response.ok(null)
                    .status(Response.Status.NO_CONTENT).build();
        }
    }

    @GET
    public Response fetchAll() {
        Optional<List<Contact>> optional = repository.fetchAll();
        ContactsResponse response;
        if (optional.isEmpty()) {
            return Response.noContent().build();
        } else {
            response = new ContactsResponse(optional.get());
            return Response.ok(response.toString()).build();
        }
    }

    @GET
    @Path("{id: [0-9]+}")
    public Response fetchById(@PathParam("id") Long id) {
        Optional<Contact> optional = repository.fetchById(id);
        if (optional.isPresent()) {
            ContactResponse response =
                    new ContactResponse(optional.get());
            return Response.ok(response.toString())
                    .status(Response.Status.CREATED).build();
        } else {
            ResponseMessage resMsg =
                    new ResponseMessage(404, false,
                            "Entity not found.");
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(resMsg.toString()).build();
        }
    }

    @PUT
    @Path("{id: [0-9]+}")
    @Consumes({MediaType.APPLICATION_JSON})
    public Response update(@PathParam("id") Long id, Contact contact) {
        Optional<Contact> optional = repository.fetchById(id);
        if (optional.isPresent()) {
            repository.update(id, contact);
            ContactResponse response =
                    new ContactResponse(optional.get());
            return Response.ok(response.toString())
                    .status(Response.Status.CREATED).build();
        } else {
            ResponseMessage respMsg =
                    new ResponseMessage(404, false,
                            "Entity not found.");
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(respMsg.toString()).build();
        }
    }

    @DELETE
    @Path("{id: [0-9]+}")
    public Response delete(@PathParam("id") Long id) {
        if (repository.isIdExists(id)) {
            repository.delete(id);
            ResponseMessage respMsg =
                    new ResponseMessage(200, true,
                            "Entity deleted.");
            return Response.ok(respMsg.toString()).build();
        } else {
            ResponseMessage respMsg =
                    new ResponseMessage(404, false,
                            "Entity not found.");
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(respMsg.toString()).build();
        }
    }


    // http://localhost:8081/api/v1/contacts/query-by-name?name=Katya
    @GET
    @Path("/query-by-name")
    public Response getContactsByName(@QueryParam("name") String name) {
        Optional<List<Contact>> optional = repository.fetchByName(name);
        if (optional.isEmpty()) {
            return Response.noContent().build();
        }
        ContactsResponse response = new ContactsResponse(optional.get());
        return Response.ok(response.toString()).build();
    }

    // http://localhost:8081/api/v1/contacts/query-order-by?orderBy=name
    @GET
    @Path("/query-order-by")
    public Response getContactsOrderBy(
            @QueryParam("orderBy") String orderBy
    ) {
        Optional<List<Contact>> optional = repository.fetchAllOrderBy(orderBy);
        if (optional.isEmpty()) {
            return Response.noContent().build();
        }
        ContactsResponse response = new ContactsResponse(optional.get());
        return Response.ok(response.toString()).build();
    }

    // http://localhost:8081/api/v1/contacts/query-between-ids?from=1&to=3
    @GET
    @Path("/query-between-ids")
    public Response getContactsBetweenIds(
            @QueryParam("from") int from,
            @QueryParam("to") int to
    ) {
        Optional<List<Contact>> optional = repository.fetchBetweenIds(from, to);
        if (optional.isEmpty()) {
            return Response.noContent().build();
        }
        ContactsResponse response = new ContactsResponse(optional.get());
        return Response.ok(response.toString()).build();
    }

    // http://localhost:8081/api/v1/contacts/query-name-in?name1=Ira&name2=Katya
    @GET
    @Path("/query-name-in")
    public Response getContactsNameIn(
            @QueryParam("name1") String name1,
            @QueryParam("name2") String name2
    ) {
        Optional<List<Contact>> optional =
                repository.fetchNameIn(name1, name2);
        if (optional.isEmpty()) {
            return Response.noContent().build();
        }
        ContactsResponse response = new ContactsResponse(optional.get());
        return Response.ok(response.toString()).build();
    }
}

