/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
/* jshint unused:false, undef:false */
module.exports = function() {

    /***
     *  Naming:
     *  File or Files masks should end in File or Files,
     *  ex: someRoot.path.myBlaFiles = /root/../*.*
     *
     *  General rules:
     *  No copy paste
     *  No duplicates
     *  Avoid specific files when possible, try to specify folders
     *  What happens to personalizationsearchsmartedit, happens to personalizationsearchsmarteditContainer
     *  Try to avoid special cases and exceptions
     */
    var lodash = require('lodash');

    var paths = {};

    paths.tests = {};

    paths.tests.root = 'jsTests';
    paths.tests.testsRoot = paths.tests.root + '/tests';
    paths.tests.personalizationsearchsmarteditContainerTestsRoot = paths.tests.testsRoot + '/personalizationsearchsmarteditContainer';
    paths.tests.personalizationsearchsmarteditContainere2eTestsRoot = paths.tests.personalizationsearchsmarteditContainerTestsRoot + '/e2e';

    paths.tests.personalizationsearchsmartedite2eTestFiles = paths.tests.root + '/e2e/**/*Test.js';
    paths.tests.personalizationsearchsmarteditContainere2eTestFiles = paths.tests.personalizationsearchsmarteditContainere2eTestsRoot + '/**/*Test.js';

    paths.e2eFiles = [
        paths.tests.personalizationsearchsmartedite2eTestFiles,
        paths.tests.personalizationsearchsmarteditContainere2eTestFiles
    ];

    /**
     * Code coverage
     */
    paths.coverage = {
        dir: './jsTarget/test/coverage',
        smarteditDirName: 'smartedit',
        smarteditcontainerDirName: 'smarteditcontainer',
        smarteditcommonsDirName: 'smarteditcommons'
    };

    return paths;

}();
