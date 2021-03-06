/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved
 */
package com.hybris.backoffice.excel.validators;

import static org.assertj.core.api.Assertions.assertThat;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.type.TypeService;
import de.hybris.platform.testframework.Transactional;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.assertj.core.api.JUnitSoftAssertions;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.hybris.backoffice.excel.data.ExcelColumn;
import com.hybris.backoffice.excel.data.ExcelWorkbook;
import com.hybris.backoffice.excel.data.ExcelWorksheet;
import com.hybris.backoffice.excel.data.ImportParameters;
import com.hybris.backoffice.excel.data.SelectedAttribute;
import com.hybris.backoffice.excel.importing.parser.DefaultImportParameterParser;
import com.hybris.backoffice.excel.importing.parser.ParsedValues;
import com.hybris.backoffice.excel.translators.generic.ExcelGenericReferenceTranslator;
import com.hybris.backoffice.excel.validators.data.ExcelValidationResult;


@Transactional
@IntegrationTest
public class ExcelGenericReferenceValidatorIntegrationTest extends ServicelayerTest
{

	private static final String CATALOG_ID = "Default";
	private static final String CATALOG_VERSION_ID = "Online";
	private static final String FIRST_CATEGORY_ID = "First";
	private static final String SECOND_CATEGORY_ID = "Second";

	@Rule
	public JUnitSoftAssertions soft = new JUnitSoftAssertions();

	@Resource
	ExcelGenericReferenceValidator excelGenericReferenceValidator;

	@Resource
	ExcelGenericReferenceTranslator excelGenericReferenceTranslator;

	@Resource
	TypeService typeService;

	@Resource
	ModelService modelService;

	@Resource
	DefaultImportParameterParser defaultImportParameterParser;

	@Before
	public void setupData()
	{
		final CatalogModel defaultCatalog = createCatalog(CATALOG_ID);
		final CatalogVersionModel catalogVersion = createCatalogVersion(defaultCatalog, CATALOG_VERSION_ID);
		createCategory(catalogVersion, FIRST_CATEGORY_ID);
		createCategory(catalogVersion, SECOND_CATEGORY_ID);
	}

	@Test
	public void shouldNotReportAnyValidationErrorsWhenCatalogVersionExist()
	{
		// given
		final ParsedValues parsedValues = defaultImportParameterParser.parseValue("CatalogVersion.version:Catalog.id", "",
				String.format("%s:%s", CATALOG_VERSION_ID, CATALOG_ID));
		final ImportParameters importParameters = new ImportParameters(ProductModel._TYPECODE, null, parsedValues.getCellValue(),
				null, parsedValues.getParameters());

		final AttributeDescriptorModel attributeDescriptor = typeService.getAttributeDescriptor(ProductModel._TYPECODE,
				ProductModel.CATALOGVERSION);

		// when
		final ExcelValidationResult validationResult = excelGenericReferenceValidator.validate(importParameters,
				attributeDescriptor, new HashMap<>());

		// then
		assertThat(validationResult.hasErrors()).isFalse();
	}

	@Test
	public void shouldReportValidationErrorsWhenCatalogNotExist()
	{
		// given
		final ParsedValues parsedValues = defaultImportParameterParser.parseValue("CatalogVersion.version:Catalog.id", "",
				String.format("%s:%s", "NotExistingVersion", "NotExistingCatalog"));
		final ImportParameters importParameters = new ImportParameters(ProductModel._TYPECODE, null, parsedValues.getCellValue(),
				null, parsedValues.getParameters());

		final AttributeDescriptorModel attributeDescriptor = typeService.getAttributeDescriptor(ProductModel._TYPECODE,
				ProductModel.CATALOGVERSION);

		// when
		final ExcelValidationResult validationResult = excelGenericReferenceValidator.validate(importParameters,
				attributeDescriptor, new HashMap<>());

		// then
		soft.assertThat(validationResult.hasErrors()).isTrue();
		soft.assertThat(validationResult.getValidationErrors()).hasSize(1);
		soft.assertThat(validationResult.getValidationErrors().get(0).getMessageKey())
				.isEqualTo(ExcelGenericReferenceValidator.VALIDATION_MESSAGE_KEY);
		soft.assertThat(validationResult.getValidationErrors().get(0).getParams()).contains(CatalogModel._TYPECODE,
				"{Catalog.id=NotExistingCatalog}");
	}

	@Test
	public void shouldReportValidationErrorsWhenCatalogVersionNotExist()
	{
		// given
		final ParsedValues parsedValues = defaultImportParameterParser.parseValue("CatalogVersion.version:Catalog.id", "",
				String.format("%s:%s", "NotExistingVersion", CATALOG_ID));
		final ImportParameters importParameters = new ImportParameters(ProductModel._TYPECODE, null, parsedValues.getCellValue(),
				null, parsedValues.getParameters());

		final AttributeDescriptorModel attributeDescriptor = typeService.getAttributeDescriptor(ProductModel._TYPECODE,
				ProductModel.CATALOGVERSION);

		// when
		final ExcelValidationResult validationResult = excelGenericReferenceValidator.validate(importParameters,
				attributeDescriptor, new HashMap<>());

		// then
		soft.assertThat(validationResult.hasErrors()).isTrue();
		soft.assertThat(validationResult.getValidationErrors()).hasSize(1);
		soft.assertThat(validationResult.getValidationErrors().get(0).getMessageKey())
				.isEqualTo(ExcelGenericReferenceValidator.VALIDATION_MESSAGE_KEY);
		soft.assertThat(validationResult.getValidationErrors().get(0).getParams()).contains(CatalogVersionModel._TYPECODE,
				"{CatalogVersion.version=NotExistingVersion, Catalog.id=Default}");
	}

	@Test
	public void shouldNotReportAnyValidationErrorsWhenSupercategoriesExist()
	{
		// given
		final ParsedValues parsedValues = defaultImportParameterParser.parseValue("Category.code:CatalogVersion.version:Catalog.id",
				"", "First:Online:Default,Second:Online:Default");
		final ImportParameters importParameters = new ImportParameters(null, null, parsedValues.getCellValue(), null,
				parsedValues.getParameters());

		final AttributeDescriptorModel attributeDescriptor = typeService.getAttributeDescriptor(ProductModel._TYPECODE,
				ProductModel.SUPERCATEGORIES);

		// when
		final ExcelValidationResult validationResult = excelGenericReferenceValidator.validate(importParameters,
				attributeDescriptor, new HashMap<>());

		// then
		assertThat(validationResult.hasErrors()).isFalse();
	}

	@Test
	public void shouldReportValidationErrorsWhenCatalogOfCategoryDoesNotExist()
	{
		// given
		final ParsedValues parsedValues = defaultImportParameterParser.parseValue("Category.code:CatalogVersion.version:Catalog.id",
				"", "First:Online:NotExistingCatalog,Second:Online:NotExistingCatalog");
		final ImportParameters importParameters = new ImportParameters(null, null, parsedValues.getCellValue(), null,
				parsedValues.getParameters());

		final AttributeDescriptorModel attributeDescriptor = typeService.getAttributeDescriptor(ProductModel._TYPECODE,
				ProductModel.SUPERCATEGORIES);

		// when
		final ExcelValidationResult validationResult = excelGenericReferenceValidator.validate(importParameters,
				attributeDescriptor, new HashMap<>());

		// then
		soft.assertThat(validationResult.hasErrors()).isTrue();
		soft.assertThat(validationResult.getValidationErrors()).hasSize(2);
		soft.assertThat(validationResult.getValidationErrors().get(0).getMessageKey())
				.isEqualTo(ExcelGenericReferenceValidator.VALIDATION_MESSAGE_KEY);
		soft.assertThat(validationResult.getValidationErrors().get(1).getMessageKey())
				.isEqualTo(ExcelGenericReferenceValidator.VALIDATION_MESSAGE_KEY);
		soft.assertThat(validationResult.getValidationErrors().get(0).getParams()).contains(CatalogModel._TYPECODE,
				"{Catalog.id=NotExistingCatalog}");
		soft.assertThat(validationResult.getValidationErrors().get(1).getParams()).contains(CatalogModel._TYPECODE,
				"{Catalog.id=NotExistingCatalog}");
	}

	@Test
	public void shouldReportValidationErrorsWhenCatalogVersionOfCategoryDoesNotExist()
	{
		// given
		final ParsedValues parsedValues = defaultImportParameterParser.parseValue("Category.code:CatalogVersion.version:Catalog.id",
				"", "First:Online:Default,Second:NotExistingVersion:Default");
		final ImportParameters importParameters = new ImportParameters(null, null, parsedValues.getCellValue(), null,
				parsedValues.getParameters());

		final AttributeDescriptorModel attributeDescriptor = typeService.getAttributeDescriptor(ProductModel._TYPECODE,
				ProductModel.SUPERCATEGORIES);

		// when
		final ExcelValidationResult validationResult = excelGenericReferenceValidator.validate(importParameters,
				attributeDescriptor, new HashMap<>());

		// then
		soft.assertThat(validationResult.hasErrors()).isTrue();
		soft.assertThat(validationResult.getValidationErrors()).hasSize(1);
		soft.assertThat(validationResult.getValidationErrors().get(0).getMessageKey())
				.isEqualTo(ExcelGenericReferenceValidator.VALIDATION_MESSAGE_KEY);
		soft.assertThat(validationResult.getValidationErrors().get(0).getParams()).contains(CatalogVersionModel._TYPECODE,
				"{CatalogVersion.version=NotExistingVersion, Catalog.id=Default}");
	}

	@Test
	public void shouldReportValidationErrorsWhenCodeOfCategoryDoesNotExist()
	{
		// given
		final ParsedValues parsedValues = defaultImportParameterParser.parseValue("Category.code:CatalogVersion.version:Catalog.id",
				"", "FirstNotExisting:Online:Default,SecondNotExisting:Online:Default");
		final ImportParameters importParameters = new ImportParameters(null, null, parsedValues.getCellValue(), null,
				parsedValues.getParameters());

		final AttributeDescriptorModel attributeDescriptor = typeService.getAttributeDescriptor(ProductModel._TYPECODE,
				ProductModel.SUPERCATEGORIES);

		// when
		final ExcelValidationResult validationResult = excelGenericReferenceValidator.validate(importParameters,
				attributeDescriptor, new HashMap<>());

		// then
		soft.assertThat(validationResult.hasErrors()).isTrue();
		soft.assertThat(validationResult.getValidationErrors()).hasSize(2);
		soft.assertThat(validationResult.getValidationErrors().get(0).getMessageKey())
				.isEqualTo(ExcelGenericReferenceValidator.VALIDATION_MESSAGE_KEY);
		soft.assertThat(validationResult.getValidationErrors().get(1).getMessageKey())
				.isEqualTo(ExcelGenericReferenceValidator.VALIDATION_MESSAGE_KEY);
		soft.assertThat(validationResult.getValidationErrors().get(0).getParams()).contains(CategoryModel._TYPECODE,
				"{Category.code=FirstNotExisting, CatalogVersion.version=Online, Catalog.id=Default}");
		soft.assertThat(validationResult.getValidationErrors().get(1).getParams()).contains(CategoryModel._TYPECODE,
				"{Category.code=SecondNotExisting, CatalogVersion.version=Online, Catalog.id=Default}");
	}

	@Test
	public void shouldNotReportAnyValidationErrorsWhenReferencedEntryExistInExcelSheet()
	{
		// given
		final AttributeDescriptorModel attributeDescriptor = typeService.getAttributeDescriptor(CategoryModel._TYPECODE,
				CategoryModel.SUPERCATEGORIES);
		final ParsedValues parsedValues = defaultImportParameterParser
				.parseValue(excelGenericReferenceTranslator.referenceFormat(attributeDescriptor), "", "firstCategory:Online:Default");
		final ImportParameters importParameters = new ImportParameters(null, null, parsedValues.getCellValue(), null,
				parsedValues.getParameters());
		final Map<String, Object> context = new HashMap<>();
		context.put(ExcelWorkbook.class.getCanonicalName(), prepareExcelWorkbook());

		// when
		final ExcelValidationResult validationResult = excelGenericReferenceValidator.validate(importParameters,
				attributeDescriptor, context);

		// then
		assertThat(validationResult.hasErrors()).isFalse();
	}

	@Test
	public void shouldReportValidationErrorsWhenReferencedEntryDoesNotExistInExcelSheet()
	{
		// given
		final AttributeDescriptorModel attributeDescriptor = typeService.getAttributeDescriptor(CategoryModel._TYPECODE,
				CategoryModel.SUPERCATEGORIES);
		final ParsedValues parsedValues = defaultImportParameterParser.parseValue(
				excelGenericReferenceTranslator.referenceFormat(attributeDescriptor), "", "secondCategory:Online:Default");
		final ImportParameters importParameters = new ImportParameters(null, null, parsedValues.getCellValue(), null,
				parsedValues.getParameters());
		final Map<String, Object> context = new HashMap<>();
		context.put(ExcelWorkbook.class.getCanonicalName(), prepareExcelWorkbook());

		// when
		final ExcelValidationResult validationResult = excelGenericReferenceValidator.validate(importParameters,
				attributeDescriptor, context);

		// then
		assertThat(validationResult.hasErrors()).isTrue();
		assertThat(validationResult.getValidationErrors()).extracting("messageKey")
				.containsOnly("excel.import.validation.generic.translator.not.existing.item");
	}

	private ExcelWorkbook prepareExcelWorkbook()
	{
		final ExcelWorkbook excelWorkbook = new ExcelWorkbook();
		final ExcelWorksheet worksheet = new ExcelWorksheet(CategoryModel._TYPECODE);
		final AttributeDescriptorModel code = typeService.getAttributeDescriptor(CategoryModel._TYPECODE, CategoryModel.CODE);
		final AttributeDescriptorModel catalogVersion = typeService.getAttributeDescriptor(CategoryModel._TYPECODE,
				CategoryModel.CATALOGVERSION);
		worksheet.add(0, new ExcelColumn(new SelectedAttribute(code), 0), prepareImportParameters(null, "firstCategory"));
		worksheet.add(0, new ExcelColumn(new SelectedAttribute(catalogVersion), 1),
				prepareImportParameters(catalogVersion, "Online:Default"));
		excelWorkbook.add(worksheet);
		return excelWorkbook;
	}

	private ImportParameters prepareImportParameters(final AttributeDescriptorModel attributeDescriptor, final String cellValue)
	{
		final ParsedValues parsedValues = defaultImportParameterParser.parseValue(
				attributeDescriptor != null ? excelGenericReferenceTranslator.referenceFormat(attributeDescriptor) : "", "",
				cellValue);
		return new ImportParameters(null, null, parsedValues.getCellValue(), null, parsedValues.getParameters());
	}

	private CatalogModel createCatalog(final String catalogId)
	{
		final CatalogModel defaultCatalog = modelService.create(CatalogModel._TYPECODE);
		defaultCatalog.setId(catalogId);
		modelService.save(defaultCatalog);
		return defaultCatalog;
	}

	private CatalogVersionModel createCatalogVersion(final CatalogModel defaultCatalog, final String version)
	{
		final CatalogVersionModel onlineVersion = modelService.create(CatalogVersionModel._TYPECODE);
		onlineVersion.setCatalog(defaultCatalog);
		onlineVersion.setVersion(version);
		modelService.save(onlineVersion);
		return onlineVersion;
	}

	private CategoryModel createCategory(final CatalogVersionModel catalogVersion, final String code)
	{
		final CategoryModel category = modelService.create(CategoryModel._TYPECODE);
		category.setCatalogVersion(catalogVersion);
		category.setCode(code);
		modelService.save(category);
		return category;
	}
}
