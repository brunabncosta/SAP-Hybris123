/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.commerceservices.customer;

import de.hybris.platform.commerceservices.model.CustomerListModel;

import java.util.List;


/**
 * Customer List Service interface holding service layer methods for dealing with Customer List
 *
 */
public interface CustomerListService
{
	/**
	 * Gets list of customer lists available for specific employee
	 *
	 * @param employeeUid
	 *           the employee to get the list for
	 * @return set containing matching customer list model
	 */
	List<CustomerListModel> getCustomerListsForEmployee(String employeeUid);

	/**
	 * retrieves a specific customer list model based on its ID
	 *
	 * @param customerListUid
	 *           the Id of the customer list model to retrieve its data
	 * @param employeeUid
	 *           the employee UId for which this customer list belongs to
	 * @return customer list model
	 */
	CustomerListModel getCustomerListForEmployee(String customerListUid, String employeeUid);
}
