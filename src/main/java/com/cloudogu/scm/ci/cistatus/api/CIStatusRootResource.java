package com.cloudogu.scm.ci.cistatus.api;

import com.cloudogu.scm.ci.PermissionCheck;
import com.cloudogu.scm.ci.RepositoryResolver;
import com.cloudogu.scm.ci.cistatus.service.CIStatus;
import com.cloudogu.scm.ci.cistatus.service.CIStatusService;
import com.google.inject.Inject;
import sonia.scm.repository.NamespaceAndName;
import sonia.scm.repository.Repository;

import javax.inject.Provider;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.cloudogu.scm.ci.HalRepresentations.createCollection;


@Path(CIStatusRootResource.CI_PATH_V2)
public class CIStatusRootResource {

    public static final String CI_PATH_V2 = "v2/ci";

    private final CIStatusMapper mapper;
    private final CIStatusService ciStatusService;
    private final Provider<CIStatusResource> ciStatusResourceProvider;
    private final CIStatusPathBuilder ciStatusPathBuilder;
    private final RepositoryResolver repositoryResolver;

    @Inject
    public CIStatusRootResource(CIStatusMapper mapper, CIStatusService ciStatusService, Provider<CIStatusResource> ciStatusResourceProvider, CIStatusPathBuilder ciStatusPathBuilder, RepositoryResolver repositoryResolver) {
        this.mapper = mapper;
        this.ciStatusService = ciStatusService;
        this.ciStatusResourceProvider = ciStatusResourceProvider;
        this.ciStatusPathBuilder = ciStatusPathBuilder;
        this.repositoryResolver = repositoryResolver;
    }

    @Path("{namespace}/{name}/changesets/{changeSetId}")
    public CIStatusResource getCIStatusResource() {
        return ciStatusResourceProvider.get();
    }

    @PUT
    @Path("{namespace}/{name}/changesets/{changeSetId}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response create(@PathParam("namespace") String namespace,
                           @PathParam("name") String name,
                           @PathParam("changeSetId") String changeSetId,
                           @Valid @NotNull CIStatusDto ciStatusDto) {
        CIStatus ciStatus = mapper.map(ciStatusDto);
        Repository repository = repositoryResolver.resolve(new NamespaceAndName(namespace, name));
        ciStatusService.add(repository, changeSetId, ciStatus);
        URI location = URI.create(ciStatusPathBuilder.createCIStatusSelfUri(namespace, name, changeSetId));
        return Response.created(location).build();
    }

    @GET
    @Path("{namespace}/{name}/changesets/{changeSetId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAll(@Context UriInfo uriInfo,
                           @PathParam("namespace") String namespace,
                           @PathParam("name") String name,
                           @PathParam("changeSetId") String changeSetId) {
        Repository repository = repositoryResolver.resolve(new NamespaceAndName(namespace, name));
        List<CIStatus> list = (List<CIStatus>) ciStatusService.getAll(repository, changeSetId);
        List<CIStatusDto> dtoList = list
                .stream()
                .map(ciStatus -> mapper.map(repository, changeSetId, Collections.singletonList(ciStatus)))
                .collect(Collectors.toList());
        boolean permission = PermissionCheck.mayRead(repository);
        return Response.ok(createCollection(uriInfo, permission, dtoList, "ciStatusList")).build();
    }
}
