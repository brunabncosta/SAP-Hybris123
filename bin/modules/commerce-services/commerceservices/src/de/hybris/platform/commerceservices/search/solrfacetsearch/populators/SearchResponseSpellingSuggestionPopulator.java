/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.commerceservices.search.solrfacetsearch.populators;

import de.hybris.platform.commerceservices.search.facetdata.ProductSearchPageData;
import de.hybris.platform.commerceservices.search.facetdata.SpellingSuggestionData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchQueryData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchResponse;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.solrfacetsearch.search.SearchResult;


/**
 * Populator to provide spelling suggestions
 */
public class SearchResponseSpellingSuggestionPopulator<FACET_SEARCH_CONFIG_TYPE, INDEXED_TYPE_TYPE, INDEXED_PROPERTY_TYPE, SEARCH_QUERY_TYPE, INDEXED_TYPE_SORT_TYPE, ITEM>
		implements
		Populator<SolrSearchResponse<FACET_SEARCH_CONFIG_TYPE, INDEXED_TYPE_TYPE, INDEXED_PROPERTY_TYPE, SEARCH_QUERY_TYPE, INDEXED_TYPE_SORT_TYPE, SearchResult>, ProductSearchPageData<SolrSearchQueryData, ITEM>>
{
	@Override
	public void populate(
			final SolrSearchResponse<FACET_SEARCH_CONFIG_TYPE, INDEXED_TYPE_TYPE, INDEXED_PROPERTY_TYPE, SEARCH_QUERY_TYPE, INDEXED_TYPE_SORT_TYPE, SearchResult> source,
			final ProductSearchPageData<SolrSearchQueryData, ITEM> target)
	{
		target.setSpellingSuggestion(buildSpellingSuggestionData(source.getSearchResult(), target.getCurrentQuery()));
	}

	protected SpellingSuggestionData<SolrSearchQueryData> buildSpellingSuggestionData(final SearchResult solrSearchResult,
			final SolrSearchQueryData queryData)
	{
		if (solrSearchResult != null)
		{
			final String spellingSuggestion = solrSearchResult.getSpellingSuggestion();
			if (spellingSuggestion != null && !spellingSuggestion.isEmpty())
			{
				final SpellingSuggestionData<SolrSearchQueryData> spellingSuggestionData = createSpellingSuggestionData();

				spellingSuggestionData.setSuggestion(spellingSuggestion);
				final SolrSearchQueryData correctedQuery = cloneSearchQueryData(queryData);
				correctedQuery.setFreeTextSearch(spellingSuggestion);
				spellingSuggestionData.setQuery(correctedQuery);

				return spellingSuggestionData;

			}
		}
		return null;
	}

	protected SpellingSuggestionData<SolrSearchQueryData> createSpellingSuggestionData()
	{
		return new SpellingSuggestionData<SolrSearchQueryData>();
	}

	protected SolrSearchQueryData cloneSearchQueryData(final SolrSearchQueryData source)
	{
		final SolrSearchQueryData target = createSearchQueryData();
		target.setFreeTextSearch(source.getFreeTextSearch());
		target.setCategoryCode(source.getCategoryCode());
		target.setSort(source.getSort());
		target.setFilterTerms(source.getFilterTerms());
		return target;
	}

	protected SolrSearchQueryData createSearchQueryData()
	{
		return new SolrSearchQueryData();
	}
}
