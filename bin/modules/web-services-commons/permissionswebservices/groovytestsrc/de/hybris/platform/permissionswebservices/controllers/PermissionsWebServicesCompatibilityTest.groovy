/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.permissionswebservices.controllers

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.oauth2.constants.OAuth2Constants
import de.hybris.platform.permissionswebservices.constants.PermissionswebservicesConstants
import de.hybris.platform.servicelayer.impex.impl.ClasspathImpExResource
import de.hybris.platform.webservicescommons.testsupport.client.WsSecuredRequestBuilder
import de.hybris.platform.webservicescommons.testsupport.server.NeedsEmbeddedServer

import groovy.json.JsonSlurper

import javax.ws.rs.client.Entity

import org.junit.Before
import org.junit.Test

import static javax.ws.rs.core.MediaType.*
import static javax.ws.rs.core.Response.Status.*

import static org.junit.Assume.assumeTrue;


@IntegrationTest
@NeedsEmbeddedServer(webExtensions =
[ PermissionswebservicesConstants.EXTENSIONNAME, OAuth2Constants.EXTENSIONNAME ])
public class PermissionsWebServicesCompatibilityTest extends AbstractPermissionsWebServicesTest {
	static final String SUBGROUP2 = "subgroup2"

	private static final String TESTADMIN_UID = "testadmin";
	private static final String TESTADMIN_PASSWORD = "1234";

	WsSecuredRequestBuilder wsSecuredRequestBuilder

	@Before
	void setup() {
		wsSecuredRequestBuilder = new WsSecuredRequestBuilder()
				.extensionName(PermissionswebservicesConstants.EXTENSIONNAME)
				.path("v1")
				.client("mobile_android", "secret");
		importData(new ClasspathImpExResource("/permissionswebservices/test/testpermissions.impex", "UTF-8"));
		insertGlobalPermission(SUBGROUP2, "globalpermission1");
	}

	/**
	 * Compatibility test for 6.1 format. If this test breaks, it means that you might have broken the backward
	 * compatility of this webservice /json format with 6.1 version.
	 */
	@Test
	public void testSearchJSONCompatibility_6_1(){
		'search permissions'("json", APPLICATION_JSON)
	}

	/**
	 * Compatibility test for 6.1 format. If this test breaks, it means that you might have broken the backward
	 * compatility of this webservice /json format with 6.1 version.
	 */
	@Test
	public void testSearchXMLCompatibility_6_1(){
		'search permissions'("xml", APPLICATION_XML)
	}


	def 'search permissions'(ext, format) {
		given: "predefined response"
		def expected = loadObject("/permissionswebservices/test/wstests/permissions-response."+ext, format )

		when: "actual request is made"
		def response = wsSecuredRequestBuilder//
				.path("permissions")//
				.path("types")//
				.path("search")//
				.queryParam("types", "User,Order")//
				.queryParam("permissionNames", "read,change,create,remove,changerights")//
				.resourceOwner(TESTADMIN_UID, TESTADMIN_PASSWORD)//
				.grantResourceOwnerPasswordCredentials()//
				.build()//
				.accept(format)//
				.post(Entity.json(Map.of("principalUid", "admin")))

		def actual = parse(response, format)

		then: "request was made "
		assert response.status == OK.statusCode

		then: "actual response is the same as expected"
		assert actual == expected

	}

	/**
	 * Compatibility test for 6.1 format. If this test breaks, it means that you might have broken the backward
	 * compatility of this webservice /json format with 6.1 version.
	 * @deprecated Since 2105
	 */
	@Deprecated(since = "2105", forRemoval = true)
	@Test
	public void testJSONCompatibility_6_1() {
		assumeTrue(isGetPermissionsTestEnabled());
		'get permissions'("json", APPLICATION_JSON)
	}

	/**
	 * Compatibility test for 6.1 format. If this test breaks, it means that you might have broken the backward
	 * compatility of this webservice /json format with 6.1 version.
	 * @deprecated Since 2105
	 */
	@Deprecated(since = "2105", forRemoval = true)
	@Test
	public void testXMLCompatibility_6_1() {
		assumeTrue(isGetPermissionsTestEnabled());
		'get permissions'("xml", APPLICATION_XML)
	}

	@Deprecated(since = "2105", forRemoval = true)
	def "get permissions"(ext, format) {
		given: "predefined response"
		def expected = loadObject("/permissionswebservices/test/wstests/permissions-response."+ext, format )

		when: "actual request is made"
		def response = wsSecuredRequestBuilder//
				.path("permissions")//
				.path("principals")//
				.path("admin")//
				.path("types")//
				.queryParam("types", "User,Order")//
				.queryParam("permissionNames", "read,change,create,remove,changerights")//
				.resourceOwner(TESTADMIN_UID, TESTADMIN_PASSWORD)//
				.grantResourceOwnerPasswordCredentials()//
				.build()//
				.accept(format)//
				.get();

		def actual = parse(response, format)

		then: "request was made "
		assert response.status == OK.statusCode

		then: "actual response is the same as expected"
		assert actual == expected

	}

	def loadText(name) {
		this.getClass().getResource(name).text
	}

	def loadObject(name, format) {
		stringParse( loadText(name), format )
	}

	def parse(response, format) {
		def text = response.readEntity(String.class)
		stringParse(text, format)
	}

	def stringParse(text, format) {
		switch(format) {
			case APPLICATION_JSON:
				return new JsonSlurper().parseText(text);
			case APPLICATION_XML:
				return new XmlSlurper().parseText(text);
			default:
				return null;
		}
	}
}
