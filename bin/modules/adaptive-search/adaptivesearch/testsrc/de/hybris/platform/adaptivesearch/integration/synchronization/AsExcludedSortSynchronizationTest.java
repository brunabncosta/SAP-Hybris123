/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.adaptivesearch.integration.synchronization;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.adaptivesearch.enums.AsSortOrder;
import de.hybris.platform.adaptivesearch.model.AbstractAsConfigurableSearchConfigurationModel;
import de.hybris.platform.adaptivesearch.model.AbstractAsSortConfigurationModel;
import de.hybris.platform.adaptivesearch.model.AsExcludedSortModel;
import de.hybris.platform.adaptivesearch.model.AsSortExpressionModel;
import de.hybris.platform.adaptivesearch.services.AsConfigurationService;
import de.hybris.platform.adaptivesearch.services.AsSearchConfigurationService;
import de.hybris.platform.adaptivesearch.services.AsSearchProfileService;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.catalog.synchronization.CatalogSynchronizationService;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.servicelayer.model.ModelService;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Optional;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


@IntegrationTest
public class AsExcludedSortSynchronizationTest extends AbstractAsSynchronizationTest
{
	private static final String CATALOG_ID = "hwcatalog";
	private static final String VERSION_STAGED = "Staged";
	private static final String VERSION_ONLINE = "Online";

	private static final String SEARCH_CONFIGURATION_UID = "searchConfiguration";

	private static final String UID1 = "cde588ec-d453-48bd-a3b1-b9aa00402256";

	private static final String CODE1 = "code1";
	private static final String NAME1 = "name1";

	private static final String INDEX_PROPERTY1 = "property1";

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	@Resource
	private ModelService modelService;

	@Resource
	private CatalogVersionService catalogVersionService;

	@Resource
	private CatalogSynchronizationService catalogSynchronizationService;

	@Resource
	private AsSearchProfileService asSearchProfileService;

	@Resource
	private AsSearchConfigurationService asSearchConfigurationService;

	@Resource
	private AsConfigurationService asConfigurationService;

	private CatalogVersionModel onlineCatalogVersion;
	private CatalogVersionModel stagedCatalogVersion;
	private AbstractAsConfigurableSearchConfigurationModel searchConfiguration;

	@Before
	public void setUp() throws ImpExException
	{
		importCsv("/adaptivesearch/test/integration/asBase.impex", StandardCharsets.UTF_8.name());
		importCsv("/adaptivesearch/test/integration/asSimpleSearchProfile.impex", StandardCharsets.UTF_8.name());
		importCsv("/adaptivesearch/test/integration/asSimpleSearchConfiguration.impex", StandardCharsets.UTF_8.name());

		stagedCatalogVersion = catalogVersionService.getCatalogVersion(CATALOG_ID, VERSION_STAGED);
		onlineCatalogVersion = catalogVersionService.getCatalogVersion(CATALOG_ID, VERSION_ONLINE);

		final Optional<AbstractAsConfigurableSearchConfigurationModel> searchConfigurationOptional = asSearchConfigurationService
				.getSearchConfigurationForUid(stagedCatalogVersion, SEARCH_CONFIGURATION_UID);
		searchConfiguration = searchConfigurationOptional.orElseThrow();
	}

	@Test
	public void excludedSortNotFoundBeforeSynchronization()
	{
		// given
		final AsExcludedSortModel excludedSort = asConfigurationService.createConfiguration(AsExcludedSortModel.class);
		excludedSort.setCatalogVersion(stagedCatalogVersion);
		excludedSort.setUid(UID1);
		excludedSort.setSearchConfiguration(searchConfiguration);
		excludedSort.setCode(CODE1);
		excludedSort.setName(NAME1);

		// when
		asConfigurationService.saveConfiguration(excludedSort);

		final Optional<AsExcludedSortModel> synchronizedExcludedSortOptional = asConfigurationService
				.getConfigurationForUid(AsExcludedSortModel.class, onlineCatalogVersion, UID1);

		// then
		assertFalse(synchronizedExcludedSortOptional.isPresent());
	}

	@Test
	public void synchronizeNewExcludedSort()
	{
		// given
		final AsExcludedSortModel excludedSort = asConfigurationService.createConfiguration(AsExcludedSortModel.class);
		excludedSort.setCatalogVersion(stagedCatalogVersion);
		excludedSort.setUid(UID1);
		excludedSort.setSearchConfiguration(searchConfiguration);
		excludedSort.setCode(CODE1);
		excludedSort.setName(NAME1);

		final AsSortExpressionModel sortExpression = asConfigurationService.createConfiguration(AsSortExpressionModel.class);
		sortExpression.setCatalogVersion(stagedCatalogVersion);
		sortExpression.setSortConfiguration(excludedSort);
		sortExpression.setExpression(INDEX_PROPERTY1);
		sortExpression.setOrder(AsSortOrder.ASCENDING);

		// when
		asConfigurationService.saveConfiguration(excludedSort);
		asConfigurationService.saveConfiguration(sortExpression);
		asConfigurationService.refreshConfiguration(excludedSort);

		catalogSynchronizationService.synchronizeFully(stagedCatalogVersion, onlineCatalogVersion);

		final Optional<AsExcludedSortModel> synchronizedExcludedSortOptional = asConfigurationService
				.getConfigurationForUid(AsExcludedSortModel.class, onlineCatalogVersion, UID1);

		// then
		assertTrue(synchronizedExcludedSortOptional.isPresent());

		final AsExcludedSortModel synchronizedExcludedSort = synchronizedExcludedSortOptional.orElseThrow();
		assertFalse(synchronizedExcludedSort.isCorrupted());
		assertSynchronized(excludedSort, synchronizedExcludedSort, AbstractAsSortConfigurationModel.UNIQUEIDX);
	}

	@Test
	public void synchronizeUpdatedExcludedSort()
	{
		// given
		final AsExcludedSortModel excludedSort = asConfigurationService.createConfiguration(AsExcludedSortModel.class);
		excludedSort.setCatalogVersion(stagedCatalogVersion);
		excludedSort.setUid(UID1);
		excludedSort.setSearchConfiguration(searchConfiguration);
		excludedSort.setCode(CODE1);
		excludedSort.setName(NAME1);

		final AsSortExpressionModel sortExpression = asConfigurationService.createConfiguration(AsSortExpressionModel.class);
		sortExpression.setCatalogVersion(stagedCatalogVersion);
		sortExpression.setSortConfiguration(excludedSort);
		sortExpression.setExpression(INDEX_PROPERTY1);
		sortExpression.setOrder(AsSortOrder.ASCENDING);

		// when
		asConfigurationService.saveConfiguration(excludedSort);
		asConfigurationService.saveConfiguration(sortExpression);
		asConfigurationService.refreshConfiguration(excludedSort);

		catalogSynchronizationService.synchronizeFully(stagedCatalogVersion, onlineCatalogVersion);

		excludedSort.setPriority(Integer.valueOf(200));
		excludedSort.setExpressions(new ArrayList<>());

		asConfigurationService.saveConfiguration(excludedSort);
		modelService.refresh(excludedSort);

		catalogSynchronizationService.synchronizeFully(stagedCatalogVersion, onlineCatalogVersion);

		final Optional<AsExcludedSortModel> synchronizedExcludedSortOptional = asConfigurationService
				.getConfigurationForUid(AsExcludedSortModel.class, onlineCatalogVersion, UID1);

		// then
		assertTrue(synchronizedExcludedSortOptional.isPresent());

		final AsExcludedSortModel synchronizedExcludedSort = synchronizedExcludedSortOptional.orElseThrow();
		assertFalse(synchronizedExcludedSort.isCorrupted());
		assertSynchronized(excludedSort, synchronizedExcludedSort, AbstractAsSortConfigurationModel.UNIQUEIDX);
	}

	@Test
	public void synchronizeRemovedExcludedSort()
	{
		// given
		final AsExcludedSortModel excludedSort = asConfigurationService.createConfiguration(AsExcludedSortModel.class);
		excludedSort.setCatalogVersion(stagedCatalogVersion);
		excludedSort.setUid(UID1);
		excludedSort.setSearchConfiguration(searchConfiguration);
		excludedSort.setCode(CODE1);
		excludedSort.setName(NAME1);

		// when
		asConfigurationService.saveConfiguration(excludedSort);
		modelService.refresh(excludedSort);

		catalogSynchronizationService.synchronizeFully(stagedCatalogVersion, onlineCatalogVersion);

		asConfigurationService.removeConfiguration(excludedSort);

		catalogSynchronizationService.synchronizeFully(stagedCatalogVersion, onlineCatalogVersion);

		final Optional<AsExcludedSortModel> synchronizedExcludedSortOptional = asConfigurationService
				.getConfigurationForUid(AsExcludedSortModel.class, onlineCatalogVersion, UID1);

		// then
		assertFalse(synchronizedExcludedSortOptional.isPresent());
	}
}
