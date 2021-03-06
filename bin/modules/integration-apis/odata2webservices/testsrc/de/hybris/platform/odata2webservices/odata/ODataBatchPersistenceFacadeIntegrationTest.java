/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.odata2webservices.odata;

import static de.hybris.platform.integrationservices.util.IntegrationTestUtil.assertModelDoesNotExist;
import static de.hybris.platform.integrationservices.util.IntegrationTestUtil.assertModelExists;
import static de.hybris.platform.odata2services.odata.content.ODataBatchBuilder.BATCH_BOUNDARY;
import static de.hybris.platform.odata2services.odata.content.ODataBatchBuilder.batchBuilder;
import static de.hybris.platform.odata2services.odata.content.ODataChangeSetBuilder.changeSetBuilder;
import static de.hybris.platform.odata2services.odata.content.ODataChangeSetPartBuilder.partBuilder;
import static de.hybris.platform.odata2webservices.odata.ODataFacadeTestUtils.catalogVersionContent;
import static de.hybris.platform.odata2webservices.odata.ODataFacadeTestUtils.createContext;
import static de.hybris.platform.odata2webservices.odata.ODataFacadeTestUtils.productModel;
import static org.assertj.core.api.Assertions.assertThat;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.integrationservices.util.IntegrationTestUtil;
import de.hybris.platform.odata2services.odata.asserts.ODataAssertions;
import de.hybris.platform.odata2webservices.odata.builders.ODataRequestBuilder;
import de.hybris.platform.servicelayer.ServicelayerTest;

import java.io.IOException;
import java.util.Locale;

import javax.annotation.Resource;

import org.apache.olingo.odata2.api.commons.HttpStatusCodes;
import org.apache.olingo.odata2.api.exception.ODataException;
import org.apache.olingo.odata2.api.processor.ODataContext;
import org.apache.olingo.odata2.api.processor.ODataRequest;
import org.apache.olingo.odata2.api.processor.ODataResponse;
import org.junit.Before;
import org.junit.Test;

@IntegrationTest
public class ODataBatchPersistenceFacadeIntegrationTest extends ServicelayerTest
{
	private static final String TEST_CODE = "ODataBatchPersistenceFacade";
	private static final String SERVICE_NAME = TEST_CODE.concat("_MyProduct");
	private static final String PRODUCT_CODE = TEST_CODE.concat("_TestProduct");
	private static final String PRODUCT_CODE_2 = TEST_CODE.concat("_TestProduct2");
	private static final String PRODUCT_CODE_3 = TEST_CODE.concat("_TestProduct3");
	private static final String PRODUCT_NAME_FRENCH = "Le Produit";
	private static final String PRODUCT_NAME_GERMAN = "Das Produkt";
	private static final String CATALOG_ID = TEST_CODE.concat("_Default");
	private static final String ENTITYSET_NAME = "Products";

	@Resource(name = "defaultODataFacade")
	private ODataFacade facade;

	@Before
	public void setUp() throws Exception
	{
		createCoreData();
		IntegrationTestUtil.importImpEx(
				"INSERT_UPDATE IntegrationObject; code[unique = true]; integrationType(code)",
											   "; "+SERVICE_NAME+"   ; INBOUND",
				"INSERT_UPDATE IntegrationObjectItem; integrationObject(code)[unique = true]; code[unique = true]; type(code)",
				"; "+SERVICE_NAME+" ; Product        ; Product",
				"; "+SERVICE_NAME+" ; Catalog        ; Catalog",
				"; "+SERVICE_NAME+" ; CatalogVersion ; CatalogVersion",
				"INSERT_UPDATE IntegrationObjectItemAttribute; integrationObjectItem(integrationObject(code), code)[unique = true]; attributeName[unique = true]; attributeDescriptor(enclosingType(code), qualifier); returnIntegrationObjectItem(integrationObject(code), code); unique[default = false]",
				"; "+SERVICE_NAME+":Product        ; code           ; Product:code           ;",
				"; "+SERVICE_NAME+":Product        ; catalogVersion ; Product:catalogVersion ;"+SERVICE_NAME+":CatalogVersion",
				"; "+SERVICE_NAME+":Product        ; name           ; Product:name           ;",
				"; "+SERVICE_NAME+":Catalog        ; id             ; Catalog:id             ;",
				"; "+SERVICE_NAME+":CatalogVersion ; catalog        ; CatalogVersion:catalog ;"+SERVICE_NAME+":Catalog",
				"; "+SERVICE_NAME+":CatalogVersion ; version        ; CatalogVersion:version ;",
				"INSERT_UPDATE Catalog; id[unique = true]; name[lang = en]; defaultCatalog;",
				"; "+CATALOG_ID+" ; Default ; true",
				"INSERT_UPDATE CatalogVersion; catalog(id)[unique = true]; version[unique = true]; active;",
				"; "+CATALOG_ID+" ; Staged ; true",
				"INSERT_UPDATE Language; isocode[unique = true]",
				"; fr");
	}

	private static String product(final String productCode, final String productName, final String catalogVersion)
	{
		return "{" +
				"	\"code\": \"" + productCode + "\"," +
				"	\"name\": \"" + productName + "\"," +
				"	\"catalogVersion\": " + catalogVersionContent(CATALOG_ID, catalogVersion) +
				"}\n".replace("\t", "");
	}

	private static String product(final String productCode, final String productName)
	{
		return product(productCode, productName, "Staged");
	}

	@Test
	public void testBatchWithGetCount() throws ODataException, IOException
	{
		final String getBody = "GET " + "Products/$count" + " HTTP/1.1" + '\n' +
				"Accept: text/plain" + '\n';
		final String content = batchBuilder()
				.withBoundary(BATCH_BOUNDARY)
				.withBatchPart(partBuilder().withBody(getBody).build())
				.build();
		final ODataResponse responseEntity = facade.handleRequest(batchPostRequest(content));

		ODataAssertions.assertThat(responseEntity).hasStatus(HttpStatusCodes.ACCEPTED)
				.body()
				.contains("HTTP/1.1 200 OK\r")
				.contains("Content-Type: text/plain\r")
				.contains("Content-Length: 1\r")
				.contains("0\r");
	}

	@Test
	public void testPersistBatchWithLocalizedAttribute()
	{
		final ProductModel productModel = productModel(PRODUCT_CODE);
		assertModelDoesNotExist(productModel);

		final String content = batchBuilder()
				.withBoundary(BATCH_BOUNDARY)
				.withChangeSet(changeSetBuilder()
						.withUri(ENTITYSET_NAME)
						.withPart(partBuilder().withContentLanguage(Locale.FRENCH).withBody(product(PRODUCT_CODE, PRODUCT_NAME_FRENCH)))
						.withPart(partBuilder().withContentLanguage(Locale.GERMAN).withBody(product(PRODUCT_CODE, PRODUCT_NAME_GERMAN))))
				.build();
		facade.handleRequest(batchPostRequest(content));

		final ProductModel persistedModel = assertModelExists(productModel);
		assertCommonProductAttributes(PRODUCT_CODE, persistedModel);
		assertThat(persistedModel.getName(Locale.FRENCH)).isEqualTo(PRODUCT_NAME_FRENCH);
		assertThat(persistedModel.getName(Locale.GERMAN)).isEqualTo(PRODUCT_NAME_GERMAN);
	}

	@Test
	public void testPersistBatchWithLocalizedAttribute_WithException()
	{
		final ProductModel productModel = productModel(PRODUCT_CODE);
		assertModelDoesNotExist(productModel);

		final String content = batchBuilder()
				.withBoundary(BATCH_BOUNDARY)
				.withChangeSet(changeSetBuilder()
						.withUri(ENTITYSET_NAME)
						.withPart(partBuilder().withContentLanguage(Locale.FRENCH).withBody(product(PRODUCT_CODE, PRODUCT_NAME_FRENCH)))
						.withPart(partBuilder().withContentLanguage(Locale.GERMAN).withBody(product(PRODUCT_CODE, PRODUCT_NAME_GERMAN, "MissingVersion"))))
				.build();
		facade.handleRequest(batchPostRequest(content));

		assertModelDoesNotExist(productModel);
	}

	@Test
	public void testPersistBulkWithLocalisedAttribute()
	{
		final ProductModel productModel = productModel(PRODUCT_CODE);
		final ProductModel productModel2 = productModel(PRODUCT_CODE_2);
		final ProductModel productModel3 = productModel(PRODUCT_CODE_3);
		assertModelDoesNotExist(productModel);
		assertModelDoesNotExist(productModel2);

		final String content = batchBuilder()
				.withBoundary(BATCH_BOUNDARY)
				.withChangeSet(changeSetBuilder()
						.withUri(ENTITYSET_NAME)
						.withPart(partBuilder().withContentLanguage(Locale.FRENCH).withBody(product(PRODUCT_CODE, PRODUCT_NAME_FRENCH)))
						.withPart(partBuilder().withContentLanguage(Locale.GERMAN).withBody(product(PRODUCT_CODE, PRODUCT_NAME_GERMAN))))
				.withChangeSet(changeSetBuilder()
						.withUri(ENTITYSET_NAME)
						.withPart(partBuilder().withContentLanguage(Locale.FRENCH).withBody(product(PRODUCT_CODE_2, PRODUCT_NAME_FRENCH)))
						.withPart(partBuilder().withContentLanguage(Locale.GERMAN).withBody(product(PRODUCT_CODE_2, PRODUCT_NAME_GERMAN, "MissingVersion"))))
				.withChangeSet(changeSetBuilder()
						.withUri(ENTITYSET_NAME)
						.withPart(partBuilder().withContentLanguage(Locale.FRENCH).withBody(product(PRODUCT_CODE_3, PRODUCT_NAME_FRENCH)))
						.withPart(partBuilder().withContentLanguage(Locale.GERMAN).withBody(product(PRODUCT_CODE_3, PRODUCT_NAME_GERMAN))))
				.build();
		facade.handleRequest(batchPostRequest(content));

		final ProductModel persistedModel = assertModelExists(productModel);
		assertCommonProductAttributes(PRODUCT_CODE, persistedModel);
		assertThat(persistedModel.getName(Locale.FRENCH)).isEqualTo(PRODUCT_NAME_FRENCH);
		assertThat(persistedModel.getName(Locale.GERMAN)).isEqualTo(PRODUCT_NAME_GERMAN);

		assertModelDoesNotExist(productModel2);

		final ProductModel persistedModel3 = assertModelExists(productModel3);
		assertCommonProductAttributes(PRODUCT_CODE_3, persistedModel3);
		assertThat(persistedModel3.getName(Locale.FRENCH)).isEqualTo(PRODUCT_NAME_FRENCH);
		assertThat(persistedModel3.getName(Locale.GERMAN)).isEqualTo(PRODUCT_NAME_GERMAN);
	}

	@Test
	public void testPersistBatchWithNotSanitizedCRLF()
	{
		final ProductModel productModel = productModel(PRODUCT_CODE);
		assertModelDoesNotExist(productModel);

		final String notSanitizedContent = batchBuilder()
				.withBoundary(BATCH_BOUNDARY)
				.withChangeSet(changeSetBuilder()
						.withUri(ENTITYSET_NAME)
						.withPart(partBuilder().withContentLanguage(Locale.FRENCH).withBody(product(PRODUCT_CODE, PRODUCT_NAME_FRENCH)))
						.withPart(partBuilder().withContentLanguage(Locale.GERMAN).withBody(product(PRODUCT_CODE, PRODUCT_NAME_GERMAN))))
				.build()
				.replace("\r", ""); // we remove the CR from message;

		assertThat(notSanitizedContent).doesNotContain("\r");

		facade.handleRequest(batchPostRequest(notSanitizedContent));

		final ProductModel persistedModel = assertModelExists(productModel);
		assertCommonProductAttributes(PRODUCT_CODE, persistedModel);
		assertThat(persistedModel.getName(Locale.FRENCH)).isEqualTo(PRODUCT_NAME_FRENCH);
		assertThat(persistedModel.getName(Locale.GERMAN)).isEqualTo(PRODUCT_NAME_GERMAN);
	}

	@Test
	public void testPersistBatchRespondsWithDataInTheLanguageSpecifiedByAcceptLanguageHeader()
	{
		final String content = batchBuilder()
				.withBoundary(BATCH_BOUNDARY)
				.withChangeSet(changeSetBuilder()
						.withUri(ENTITYSET_NAME)
						.withPart(partBuilder()
								.withContentLanguage(Locale.FRENCH)
								.withAcceptLanguage(Locale.GERMAN)
								.withBody(product(PRODUCT_CODE, PRODUCT_NAME_FRENCH)))
						.withPart(partBuilder()
								.withContentLanguage(Locale.GERMAN)
								.withBody(product(PRODUCT_CODE, PRODUCT_NAME_GERMAN))))
				.build();
		final ODataRequest request = batchPostRequest()
				.withBody(content)
				.build();

		final ODataResponse response = facade.handleRequest(createContext(request));

		ODataAssertions.assertThat(response).hasStatus(HttpStatusCodes.ACCEPTED)
				.body()
				.contains(PRODUCT_NAME_GERMAN)
				.doesNotContain(PRODUCT_NAME_FRENCH);
	}

	private ODataContext batchPostRequest(final String content)
	{
		return createContext(ODataFacadeTestUtils.batchODataPostRequest(SERVICE_NAME, content));
	}

	private ODataRequestBuilder batchPostRequest()
	{
		return ODataFacadeTestUtils.batchPostRequestBuilder(SERVICE_NAME);
	}

	private static void assertCommonProductAttributes(final String code, final ProductModel persistedModel)
	{
		assertThat(persistedModel.getCode()).isEqualTo(code);
		assertThat(persistedModel.getName(Locale.ENGLISH)).isNull();
		assertThat(persistedModel.getCatalogVersion().getVersion()).isEqualTo("Staged");
		assertThat(persistedModel.getCatalogVersion().getCatalog().getId()).isEqualTo(CATALOG_ID);
	}
}