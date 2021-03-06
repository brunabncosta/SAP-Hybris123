/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.solrfacetsearch.integration;

import static org.junit.Assert.assertEquals;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.solrfacetsearch.config.FacetSearchConfig;
import de.hybris.platform.solrfacetsearch.config.IndexedType;
import de.hybris.platform.solrfacetsearch.config.exceptions.FacetConfigServiceException;
import de.hybris.platform.solrfacetsearch.indexer.exceptions.IndexerException;
import de.hybris.platform.solrfacetsearch.indexer.impl.DefaultIndexerService;
import de.hybris.platform.solrfacetsearch.search.FacetSearchException;
import de.hybris.platform.solrfacetsearch.search.FacetSearchService;
import de.hybris.platform.solrfacetsearch.search.SearchQuery;
import de.hybris.platform.solrfacetsearch.search.SearchResult;
import de.hybris.platform.solrfacetsearch.solr.exceptions.SolrServiceException;

import java.io.IOException;
import java.util.Collections;

import javax.annotation.Resource;

import org.apache.solr.client.solrj.SolrServerException;
import org.junit.Test;


/**
 * Test for update StopWords feature
 */
@IntegrationTest
public class SolrStopWordsIntegrationTest extends AbstractIntegrationTest
{
	private static final String STOP_WORD = "abcdef";

	@Resource
	private CatalogVersionService catalogVersionService;

	@Resource
	private DefaultIndexerService indexerService;

	@Resource
	private FacetSearchService facetSearchService;

	@Override
	protected void loadData()
			throws ImpExException, IOException, FacetConfigServiceException, SolrServiceException, SolrServerException
	{
		importConfig("/test/integration/SolrStopWordsIntegrationTest.csv");
	}

	@Test
	public void testSearchForStopWordBeforeUpdate() throws IndexerException, FacetConfigServiceException, FacetSearchException
	{
		// given
		final FacetSearchConfig facetSearchConfig = getFacetSearchConfig();
		final IndexedType indexedType = facetSearchConfig.getIndexConfig().getIndexedTypes().values().iterator().next();
		final CatalogVersionModel catalogVersionOnline = catalogVersionService.getCatalogVersion(HW_CATALOG,
				ONLINE_CATALOG_VERSION + getTestId());

		// when
		indexerService.performFullIndex(getFacetSearchConfig());

		final SearchQuery query = facetSearchService.createPopulatedSearchQuery(facetSearchConfig, indexedType);
		query.setCatalogVersions(Collections.singletonList(catalogVersionOnline));
		query.setLanguage("en");
		query.addQuery("description", STOP_WORD);

		final SearchResult searchResult = facetSearchService.search(query);

		// then
		assertEquals(2, searchResult.getNumberOfResults());
	}

	@Test
	public void testSearchForStopWordAfterUpdate()
			throws FacetConfigServiceException, ImpExException, IOException, FacetSearchException, IndexerException
	{
		final FacetSearchConfig facetSearchConfig = getFacetSearchConfig();
		final IndexedType indexedType = facetSearchConfig.getIndexConfig().getIndexedTypes().values().iterator().next();
		final CatalogVersionModel catalogVersionOnline = catalogVersionService.getCatalogVersion(HW_CATALOG,
				ONLINE_CATALOG_VERSION + getTestId());

		//given
		importConfig("/test/integration/SolrStopWordsIntegrationTest_addStopWord.csv");

		//when
		indexerService.performFullIndex(getFacetSearchConfig());

		final SearchQuery query = facetSearchService.createPopulatedSearchQuery(facetSearchConfig, indexedType);
		query.setCatalogVersions(Collections.singletonList(catalogVersionOnline));
		query.setLanguage("en");
		query.addQuery("description", STOP_WORD);

		final SearchResult searchResult = facetSearchService.search(query);

		//then
		assertEquals(0, searchResult.getNumberOfResults());
	}

	@Test
	public void testSearchForStopWordAfterUpdateWithSpecialCharacterStopword()
			throws FacetConfigServiceException, ImpExException, IOException, FacetSearchException, IndexerException
	{
		final FacetSearchConfig facetSearchConfig = getFacetSearchConfig();
		final IndexedType indexedType = facetSearchConfig.getIndexConfig().getIndexedTypes().values().iterator().next();
		final CatalogVersionModel catalogVersionOnline = catalogVersionService.getCatalogVersion(HW_CATALOG,
				ONLINE_CATALOG_VERSION + getTestId());

		//given
		//add stopwords with special character and index
		importConfig("/test/integration/SolrStopWordsIntegrationTest_addStopWord_specialChars.csv");
		indexerService.performFullIndex(getFacetSearchConfig());
		//remove stopwords and reindex
		importConfig("/test/integration/SolrStopWordsIntegrationTest_deleteStopWord_specialChars.csv");
		indexerService.performFullIndex(getFacetSearchConfig());

		final SearchQuery query = facetSearchService.createPopulatedSearchQuery(facetSearchConfig, indexedType);
		query.setCatalogVersions(Collections.singletonList(catalogVersionOnline));
		query.setLanguage("en");
		query.addQuery("description", STOP_WORD);

		final SearchResult searchResult = facetSearchService.search(query);

		//then
		assertEquals(2, searchResult.getNumberOfResults());
	}

	@Test
	public void testSearchForStopWordInOtherLanguage()
			throws FacetConfigServiceException, ImpExException, IOException, FacetSearchException, IndexerException
	{
		final FacetSearchConfig facetSearchConfig = getFacetSearchConfig();
		final IndexedType indexedType = facetSearchConfig.getIndexConfig().getIndexedTypes().values().iterator().next();
		final CatalogVersionModel catalogVersionOnline = catalogVersionService.getCatalogVersion(HW_CATALOG,
				ONLINE_CATALOG_VERSION + getTestId());

		//given
		importConfig("/test/integration/SolrStopWordsIntegrationTest_addStopWord.csv");

		//when
		indexerService.performFullIndex(getFacetSearchConfig());

		final SearchQuery query = facetSearchService.createPopulatedSearchQuery(facetSearchConfig, indexedType);
		query.setCatalogVersions(Collections.singletonList(catalogVersionOnline));
		query.setLanguage("de");
		query.addQuery("description", STOP_WORD);

		final SearchResult searchResult = facetSearchService.search(query);

		//then
		assertEquals(1, searchResult.getNumberOfResults());
	}
}
