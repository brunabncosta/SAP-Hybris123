/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.textfieldconfiguratortemplateocctest

import de.hybris.bootstrap.annotations.IntegrationTest
import de.hybris.platform.textfieldconfiguratortemplateocctest.controllers.ProductTextfieldConfiguratorControllerTest

import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Suite
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@RunWith(Suite.class)
@Suite.SuiteClasses([ProductTextfieldConfiguratorControllerTest])
@IntegrationTest
class AllTextFieldConfiguratorSpockTests {

    private static final Logger LOG = LoggerFactory.getLogger(AllTextFieldConfiguratorSpockTests.class)

    @BeforeClass
    static void setUpClass() {
   	  TestSetupStandalone.startServer()
        TestSetupStandalone.loadData()
    }

    @AfterClass
    static void tearDown() {
        TestSetupStandalone.stopServer()
        TestSetupStandalone.cleanData()
    }

    @Test
    static void testing() {
        //dummy test class
    }
}
