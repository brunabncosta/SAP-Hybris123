/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.personalizationyprofile.yaas.client;

import de.hybris.platform.personalizationyprofile.yaas.ProfileReference;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import rx.Observable;

import com.hybris.charon.annotations.Control;
import com.hybris.charon.annotations.Http;
import com.hybris.charon.annotations.OAuth;


/**
 * Client reading information from yaas identity service.
 */

@OAuth
@Http
public interface CxIdentityServiceClient
{
	/**
	 * Finds profile references based on identity (e.g. user email, session identifier)
	 *
	 * @param identityKey
	 *           Identity key
	 * @param identityType
	 *           Identity type
	 * @param identityOrigin
	 *           Identity origin
	 * @return Profile references
	 */
	@GET
	@Control(retries = "${retries:0}", retriesInterval = "${retriesInterval:500}", timeout = "${timeout:2000}")
	@Path("/${tenant}/profileReferences?identityKey={key}&identityType={type}&identityOrigin={origin}")
	List<ProfileReference> getProfileReferences(@PathParam("key") String identityKey, @PathParam("type") String identityType,
			@PathParam("origin") String identityOrigin);

	/**
	 * Finds profile references based on identity (e.g. user email, session identifier)
	 *
	 * @param identityKey
	 *           Identity key
	 * @param identityType
	 *           Identity type
	 * @param identityOrigin
	 *           Identity origin
	 * @param limit
	 *           Limit of profile references to return
	 * @param sortBy
	 *           Attribute on which profile references will be sorted
	 * @param sortDirection
	 *           Sorting direction
	 * @return Profile references
	 */
	@GET
	@Control(retries = "${retries:0}", retriesInterval = "${retriesInterval:500}", timeout = "${timeout:2000}")
	@Path("/${tenant}/profileReferences?identityKey={key}&identityType={type}&identityOrigin={origin}&limit={limit}&sortBy={sortBy}&sortDirection={sortDirection}")
	List<ProfileReference> getProfileReferences(@PathParam("key") String identityKey, @PathParam("type") String identityType,
			@PathParam("origin") String identityOrigin, @PathParam("limit") int limit, @PathParam("sortBy") String sortBy,
			@PathParam("sortDirection") String sortDirection);

	/**
	 * Finds profile references based on identity (e.g. user email, session identifier). Method runs asynchronously.
	 *
	 * @param identityKey
	 *           Identity key
	 * @param identityType
	 *           Identity type
	 * @param identityOrigin
	 *           Identity origin
	 * @return Profile references
	 */
	@GET
	@Control(retries = "${retries:0}", retriesInterval = "${retriesInterval:500}", timeout = "${timeout:2000}")
	@Path("/${tenant}/profileReferences?identityKey={key}&identityType={type}&identityOrigin={origin}")
	Observable<List<ProfileReference>> getProfileReferencesAsync(@PathParam("key") String identityKey,
			@PathParam("type") String identityType, @PathParam("origin") String identityOrigin);


	/**
	 * Finds profile references based on identity (e.g. user email, session identifier). Method runs asynchronously.
	 *
	 * @param identityKey
	 *           Identity key
	 * @param identityType
	 *           Identity type
	 * @param identityOrigin
	 *           Identity origin
	 * @param limit
	 *           Limit of profile references to return
	 * @param sortBy
	 *           Attribute on which profile references will be sorted
	 * @param sortDirection
	 *           Sorting direction
	 * @return Profile references
	 */
	@GET
	@Control(retries = "${retries:0}", retriesInterval = "${retriesInterval:500}", timeout = "${timeout:2000}")
	@Path("/${tenant}/profileReferences?identityKey={key}&identityType={type}&identityOrigin={origin}&limit={limit}&sortBy={sortBy}&sortDirection={sortDirection}")
	Observable<List<ProfileReference>> getProfileReferencesAsync(@PathParam("key") String identityKey,
			@PathParam("type") String identityType, @PathParam("origin") String identityOrigin, @PathParam("limit") int limit,
			@PathParam("sortBy") String sortBy, @PathParam("sortDirection") String sortDirection);

}
