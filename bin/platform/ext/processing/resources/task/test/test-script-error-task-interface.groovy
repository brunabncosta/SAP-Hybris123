/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package task.test

import de.hybris.platform.core.Registry
import de.hybris.platform.core.model.user.TitleModel
import de.hybris.platform.task.TaskModel
import de.hybris.platform.task.TaskRunner
import de.hybris.platform.task.TaskService

class MyScriptRunner implements TaskRunner<TaskModel> {

    @Override
    void run(TaskService taskService, TaskModel task) {
        println 'hello groovy! ' + new Date();
        def modelService = Registry.getApplicationContext().getBean("modelService")
        def title = modelService.create(TitleModel.class)
        title.pk.longValue //this should throw NullPointerException, which is expected here!
        title.code = "CorrectGroovyTitle"
        modelService.save(title)
    }

    @Override
    void handleError(TaskService taskService, TaskModel task, Throwable error) {
        println 'my script has errors'
        def modelService = Registry.getApplicationContext().getBean("modelService")
        def title = modelService.create(TitleModel.class)
        title.code = "ErrorGroovyTitle"
        modelService.save(title)
    }

}

new MyScriptRunner();