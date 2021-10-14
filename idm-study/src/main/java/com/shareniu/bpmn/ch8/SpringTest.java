package com.shareniu.bpmn.ch8;

import org.flowable.common.engine.impl.identity.Authentication;
import org.flowable.engine.*;
import org.flowable.engine.form.FormProperty;
import org.flowable.engine.form.TaskFormData;
import org.flowable.engine.history.HistoricActivityInstance;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.impl.util.TaskHelper;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.DeploymentBuilder;
import org.flowable.engine.runtime.*;
import org.flowable.engine.task.Attachment;
import org.flowable.engine.task.Comment;
import org.flowable.form.api.FormInfo;
import org.flowable.identitylink.api.IdentityLink;
import org.flowable.identitylink.api.IdentityLinkInfo;
import org.flowable.identitylink.api.IdentityLinkType;
import org.flowable.task.api.Task;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.flowable.task.service.impl.persistence.entity.TaskEntity;
import org.flowable.variable.api.persistence.entity.VariableInstance;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:flowable-context.xml")
public class SpringTest {
    ProcessEngine processEngine;
    RepositoryService repositoryService;
    RuntimeService runtimeService;
    TaskService taskService;
    HistoryService historyService;
    IdentityService identityService;
    ManagementService managementService;
    FormService formService;

    @Before
    public void buildProcessEngine() {
        processEngine = ProcessEngines.getDefaultProcessEngine();
        System.out.println(processEngine);
        repositoryService = processEngine.getRepositoryService();
        System.out.println(repositoryService);
        String name = processEngine.getName();
        System.out.println("流程引擎的名称：" + name);
        DynamicBpmnService dynamicBpmnService = processEngine.getDynamicBpmnService();
        System.out.println(dynamicBpmnService);
        formService = processEngine.getFormService();
        System.out.println(formService);

        historyService = processEngine.getHistoryService();
        System.out.println(historyService);
        identityService = processEngine.getIdentityService();
        System.out.println(identityService);
        managementService = processEngine.getManagementService();
        System.out.println(managementService);
        ProcessEngineConfiguration processEngineConfiguration = processEngine.getProcessEngineConfiguration();
        System.out.println(processEngineConfiguration);
        runtimeService = processEngine.getRuntimeService();
        System.out.println(runtimeService);
        taskService = processEngine.getTaskService();
        System.out.println(taskService);
    }

    @Test
    public void DeploymentBuild() {
        DeploymentBuilder deploymentBuilder = repositoryService
                .createDeployment()
                .category("测试分类")
                .name("名称");
        System.out.println(deploymentBuilder);
    }

    @Test
    public void deploy() {
        DeploymentBuilder deploymentBuilder = repositoryService
                .createDeployment()
                .category("leave")
                .name("请假流程第二版")
                .addClasspathResource("leave.bpmn20.xml");
        //.addClasspathResource("dataobject.bpmn20.xml");
        Deployment deploy = deploymentBuilder.deploy();
        System.out.println(deploy.getId());
    }

    /**
     * 启动流程实例
     */
    @Test
    public void startProcessInstanceByKey() {
        String processDefinitionKey = "leave";
        // String processDefinitionKey = "dataobject";
        // repositoryService.createDeploymentQuery().deploymentKeyLike(”)
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("leave");
        //ProcessInstance processInstance = runtimeService.startProcessInstanceById("leave:4:82504");
        System.out.println(processInstance.getId() + "," + processInstance.getActivityId());
    }


    @Test
    public void startProcessInstanceByKey1() {
        String processDefinitionKey = "leave";
        String businessKey = "";
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(processDefinitionKey, businessKey);
        System.out.println(processInstance.getId() + "," + processInstance.getActivityId());
    }

    /**
     * select distinct RES.* from ACT_RU_TASK RES inner join ACT_RE_PROCDEF D
     * on RES.PROC_DEF_ID_ = D.ID_
     * WHERE RES.ASSIGNEE_ = ? and D.KEY_ = ? order by RES.ID_ asc
     * <p>
     * 张三1(String), leave(String)
     */
    @Test
    public void queryMyTask() {

        String processDefinitionKey = "leave";
        String assignee = "王五";
        List<Task> list = taskService.createTaskQuery()
                .taskAssignee(assignee)
                //.taskCandidateUser("")
                //.taskCandidateGroup("")
                .processDefinitionKey(processDefinitionKey)
                //.taskCategory("leave")
                .taskId("145001")
                .list();

        for (Task task : list) {
            System.out.println(task.getId());
            System.out.println(task.getName());
            System.out.println(task.getTaskDefinitionKey());
            System.out.println(task.getExecutionId());
            System.out.println(task.getProcessInstanceId());
            System.out.println(task.getProcessDefinitionId());
            System.out.println(task.getCreateTime());
        }
    }


    @Test
    public void createNativeTaskQuery() {

        String processDefinitionKey = "leave";
        String assignee = "王五";
        List<Task> list = taskService.createNativeTaskQuery()
                .sql("select * from act_ru_task where assignee_ = #{assignee}" )
                .parameter("assignee", assignee).list();

        for (Task task : list) {
            System.out.println(task.getId());
            System.out.println(task.getName());
            System.out.println(task.getTaskDefinitionKey());
            System.out.println(task.getExecutionId());
            System.out.println(task.getProcessInstanceId());
            System.out.println(task.getProcessDefinitionId());
            System.out.println(task.getCreateTime());
        }
    }



    @Test
    public void createTaskBuilder() throws Exception{

        Task task = taskService.createTaskBuilder().category("测试")
                .assignee("六六").owner("wwm").id("111113").description("不知道说什么")
                .name("wwm审批").taskDefinitionKey("_4").taskDefinitionId("125035")
                .scopeId("121122").create();

//        Task task = taskService.createTaskBuilder().create();
//        task.setOwner("wwm");
//        taskService.saveTask(task);

        System.out.println(task.getId());



    }


    @Test
    public void createAttachment() throws Exception{
        InputStream in = new FileInputStream("/home/umlinux/Desktop/daily/图片/1.jpg");
        taskService.createAttachment("images","125036","125035","图片1","测试上传图片",in);
    }


    @Test
    public void getTaskAttachments() {

        List<Attachment> list = taskService.getTaskAttachments("125036");
        System.out.println(list);

    }


    @Test
    public void addComment() {
        //taskService.addComment("125061","125060","没事测着玩");
        taskService.addComment("125061","125060","wwm","没事测着玩lalal");
    }


    @Test
    public void saveComment() {
        Comment comment = taskService.getComment("185001");
        comment.setFullMessage("没事测着玩啦啦啦");
        taskService.saveComment(comment);
    }


    @Test
    public void getComment() {
        Comment comment = taskService.getComment("187501");
        //List<Comment> list = taskService.getProcessInstanceComments("125035");
        System.out.println(comment.getFullMessage());
        //taskService.deleteComments("125061","125060");
    }


    @Test
    public void newTask() {
        Task task = taskService.newTask();
        task.setName("吴伟铭审核");
        taskService.saveTask(task);
    }


    @Test
    public void deleteTask() {
        // taskService.deleteTask("195004");
        // taskService.deleteTask("195004",true);
        // TaskEntity task;
        //task.setCanceled();
        taskService.deleteTask("212501","删除理由");
    }


    @Test
    public void addCandidateUser() {
        taskService.addCandidateUser("217501","001,002");
    }


    @Test
    public void addCandidateGroup() {
        taskService.addCandidateGroup("217501","001,002");
    }


    @Test
    public void deleteCandidateUser() {
        taskService.deleteCandidateUser("217501","001,002");
    }


    @Test
    public void deleteCandidateGroup() {
        taskService.deleteCandidateGroup("217501","001,002");
    }


    @Test
    public void addUserIdentityLink() {
        // taskService.addUserIdentityLink("217501","008",IdentityLinkType.CANDIDATE);
        taskService.addUserIdentityLink("217501","009",IdentityLinkType.STARTER);
        taskService.deleteUserIdentityLink("217501","009",IdentityLinkType.STARTER);
    }

    @Test
    public void addGroupIdentityLink() {
        // taskService.addUserIdentityLink("217501","008",IdentityLinkType.CANDIDATE);
        taskService.addGroupIdentityLink("217501","009",IdentityLinkType.STARTER);
        taskService.deleteGroupIdentityLink("217501","009",IdentityLinkType.STARTER);
    }


    @Test
    public void getIdentityLinksForTask() {
        String assignee = "wwm";
        String procInsId = "202501";
//        Task task = taskService.createTaskQuery().active().taskAssignee(assignee).processInstanceId(procInsId).singleResult();
//        // task.setCandidateUsers("217501","009",IdentityLinkType.STARTER);
//        List<? extends IdentityLinkInfo> identityLinks = task.getIdentityLinks();
//        for (IdentityLinkInfo identityLinkInfo : identityLinks){
//            System.out.println(identityLinkInfo.getUserId());
//            System.out.println(identityLinkInfo.getGroupId());
//        }
        List<IdentityLink> identityLinkList = taskService.getIdentityLinksForTask("217501");
        for (IdentityLink identityLink : identityLinkList){
            System.out.println(identityLink.getUserId());
            System.out.println(identityLink.getGroupId());
        }
    }


    @Test
    public void setAssignee() {
        String assignee = "wwm";
        String procInsId = "202501";
        taskService.setAssignee("","");
    }



    @Test
    public void completeMyTask() {
        String taskId = "257504";
        taskService.complete(taskId);
//        Map<String,Object> variables = new HashMap<>();
//        variables.put("name", "吴彦祖");
//        variables.put("age", 25);
//        variables.put("address", "揭阳");
//        taskService.complete(taskId, variables);
    }


    @Test
    public void resolveTask() {
        String taskId = "207502";
        //taskService.complete(taskId);
        Map<String,Object> variables = new HashMap<>();
        variables.put("name", "吴彦祖");
        variables.put("age", 25);
        variables.put("address", "揭阳");

        Map<String,Object> transientVariables = new HashMap<>();
        variables.put("name", "吴彦祖");
        variables.put("age", 25);
        variables.put("address", "揭阳");

        taskService.resolveTask(taskId);
        taskService.resolveTask(taskId, variables);
        taskService.resolveTask(taskId, variables, transientVariables);
    }


    @Test
    public void other() {
        String taskId = "232505";
        //FormInfo formInfo = taskService.getTaskFormModel(taskId);
        //System.out.println(formInfo);

        TaskFormData taskFormData = formService.getTaskFormData(taskId);
        System.out.println(taskFormData.getFormKey());
        List<FormProperty> list = taskFormData.getFormProperties();
        for (FormProperty formProperty : list) {
            System.out.println(formProperty.getId());
            System.out.println(formProperty.getName());
            System.out.println(formProperty.getValue());
            System.out.println(formProperty.getType());
        }
        //taskService.setAssignee(taskId, "userId"); //将给定任务的受让人更改为给定的用户Id
        //taskService.setOwner(taskId, "userId");    //将此任务的所有权转移给其他用户
        //taskService.setDueDate(taskId, new Date());        //设置任务的截止日期
        //taskService.setPriority(taskId, 10);     //更改任务的优先级。授权：实际所有者/业务管理员
    }


    @Test
    public void taskVariable() {
        String taskId = "232505";

        // 获取指定名称的变量
        Object object = taskService.getVariable(taskId,"age");
        System.out.println(object);

        // 获取指定名称的变量，同时指定变量类型
        Integer age = taskService.getVariable(taskId, "age", Integer.class);
        System.out.println(age);

        // 根据任务id获取该任务节点所有的变量，仅有名称和对应值
        Map<String,Object> map1  = taskService.getVariables(taskId);
        System.out.println(map1);

        // 根据任务id获取该任务节点指定名称的变量，仅有名称和对应值
        List<String> keyList = new ArrayList<>();
        keyList.add("age");
        keyList.add("name");
        Map<String,Object> map2 = taskService.getVariables(taskId, keyList);
        System.out.println(map2);

        // 根据任务节点id获取指定名称的变量对象，对应表 act_ru_variable 中的数据
        VariableInstance variableInstance = taskService.getVariableInstance(taskId,"age");
        System.out.println(variableInstance);

        // 根据任务节点id获取所有的变量对象，封装成map结构，key为变量名称，value为变量对象
        Map<String,VariableInstance> map3 = taskService.getVariableInstances(taskId);
        System.out.println(map3);

        // 根据任务节点id获取指定名称的变量对象，封装成map结构，key为变量名称，value为变量对象
        Map<String,VariableInstance> map4 = taskService.getVariableInstances(taskId, keyList) ;
        System.out.println(map4);

        // 不搜索父作用域
        age = taskService.getVariableLocal(taskId, "age", Integer.class);
        System.out.println(age);

        // 为任务节点设置单个变量，如果已经存在，不会重复设置
        taskService.setVariable(taskId, "room", "206");

        Map<String,Object> variableMap = new HashMap<>();
        variableMap.put("key1","value1");
        variableMap.put("key3","value3");
        // 为任务节点设置多个变量，如果已经存在，不会重复设置
        taskService.setVariables(taskId, variableMap);

        // 判断某个任务是否含有某个变量
        boolean isExist = taskService.hasVariable(taskId,"age");
        // 不搜索父作用域
        // boolean isExist = taskService.hasVariableLocal(taskId, "age");
        System.out.println(isExist);

        // 删除指定名称变量
        taskService.removeVariable(taskId,"key1");

        List<String> keyRemove = new ArrayList<>();
        keyRemove.add("key4");
        keyRemove.add("key2");
        // 删除指定一组名称的变量
        taskService.removeVariables(taskId,keyRemove);
    }



    @Test
    public void createProcessInstanceBuilder() {
        String taskId = "137502";
        runtimeService.createProcessInstanceBuilder()
                .processDefinitionKey("someKey")
                .transientVariable("configParam01", "A")
                .transientVariable("configParam02", "B")
                .transientVariable("configParam03", "C")
                .start();
    }


    @Test
    public void addEventListener() {
//        String taskId = "137502";
//        runtimeService.addEventListener();
//        runtimeService.removeEventListener();
    }

    /**
     * select distinct RES.* , P.KEY_ as ProcessDefinitionKey, P.ID_ as
     * ProcessDefinitionId, P.NAME_ as ProcessDefinitionName,
     * P.VERSION_ as ProcessDefinitionVersion,
     * P.DEPLOYMENT_ID_ as DeploymentId
     * from ACT_RU_EXECUTION RES
     * inner join ACT_RE_PROCDEF P on RES.PROC_DEF_ID_ = P.ID_
     * <p>
     * WHERE RES.PARENT_ID_ is null and RES.ID_ = ? and RES.PROC_INST_ID_ = ? order by RES.ID_ asc
     */
    // 查询流程实例
    @Test
    public void queryProcessInstanceState() {
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
                .processInstanceId("59501")
                //.deploymentId()
                .singleResult();
        if (processInstance != null) {
            System.out.println("当前的流程实例正在运行");
        } else {
            System.out.println("当前的流程实例已经结束");
        }
    }


    // 查询流程实例
    @Test
    public void createProcessInstanceQuery() {
        List<ProcessInstance> list = runtimeService.createProcessInstanceQuery().list();
                //.processInstanceId("40001")
                //.processDefinitionCategory("http://www.activiti.org/test")
                //.deploymentId("7501")
                //.singleResult();
        //System.out.println(processInstance);
        System.out.println("list的长度：" + list.size());
        for (ProcessInstance processInstance : list){
            System.out.println("----------------------------------");
            System.out.println(processInstance.getId());
            System.out.println(processInstance.getName());
            System.out.println(processInstance.getDeploymentId());
            System.out.println(processInstance.getTenantId());
            System.out.println(processInstance.getDescription());
        }
    }


    // 查询执行实例
    @Test
    public void createNativeProcessInstanceQuery() {
        List<ProcessInstance> list = runtimeService.createNativeProcessInstanceQuery()
                .sql("select distinct RES.* , P.KEY_ as ProcessDefinitionKey, P.ID_ as ProcessDefinitionId, P.NAME_ as ProcessDefinitionName, P.VERSION_ as ProcessDefinitionVersion, P.DEPLOYMENT_ID_ as DeploymentId from ACT_RU_EXECUTION RES inner join ACT_RE_PROCDEF P on RES.PROC_DEF_ID_ = P.ID_").list();
        System.out.println("list的长度：" + list.size());
        for (ProcessInstance processInstance : list){
            System.out.println(processInstance.getId());
            System.out.println(processInstance.getName());
            System.out.println(processInstance.getDeploymentId());
            System.out.println(processInstance.getTenantId());
            System.out.println(processInstance.getDescription());
        }
    }


    // 查询执行实例
    @Test
    public void createNativeExecutionQuery() {
        List<Execution> list = runtimeService.createNativeExecutionQuery()
                .sql("select * from act_ru_execution").list();

        System.out.println("list的长度：" + list.size());
        for (Execution execution : list){
            System.out.println("----------------------------------");
            System.out.println(execution.getId());
            System.out.println(execution.getName());
            System.out.println(execution.getActivityId());
            System.out.println(execution.getProcessInstanceId());
            System.out.println(execution.getTenantId());
            System.out.println(execution.getDescription());
        }
    }


    /**
     * select distinct RES.* , P.KEY_ as ProcessDefinitionKey, P.ID_
     * as ProcessDefinitionId from ACT_RU_EXECUTION RES
     * inner join ACT_RE_PROCDEF P on RES.PROC_DEF_ID_ = P.ID_ order by RES.ID_ asc
     * 查询执行实例
     */
    @Test
    public void createExecutionQuery() {
        List<Execution> executions = runtimeService.createExecutionQuery()
                .executionId("")
                .processDefinitionCategory("")
                .orderByTenantId()
                .list();
        for (Execution execution : executions) {
            System.out.println(execution.getId() + "," + execution.getActivityId());
        }
    }


    // 跳过节点
    @Test
    public void createActivityInstanceQuery() {
        runtimeService.createChangeActivityStateBuilder()
                .processInstanceId("105003")
                .moveActivityIdTo("_4","_5")
                .changeState();

    }


    @Test
    public void rollback() {
        String processInstanceId = "";
        List<String> currTaskKeys = new ArrayList<>();
        String targetKey = "";
        runtimeService.createChangeActivityStateBuilder()
                .processInstanceId(processInstanceId)
                .moveActivityIdsToSingleActivityId(currTaskKeys, targetKey)
                .changeState();
    }



//    @Test
//    public void createEventSubscriptionQuery() {
//        runtimeService.createEventSubscriptionQuery()
//                .processInstanceId("105003")
//                .moveActivityIdTo("_4","_5")
//                .changeState();
//    }


    /**
     * 查询历史流程实例
     * select distinct RES.* , DEF.KEY_ as PROC_DEF_KEY_,
     * DEF.NAME_ as PROC_DEF_NAME_, DEF.VERSION_
     * as PROC_DEF_VERSION_, DEF.DEPLOYMENT_ID_ as DEPLOYMENT_ID_
     * from ACT_HI_PROCINST RES left outer join ACT_RE_PROCDEF DEF
     * on RES.PROC_DEF_ID_ = DEF.ID_ WHERE RES.PROC_INST_ID_ = ? order by RES.ID_ asc
     */
    @Test
    public void createHistoricProcessInstanceQuery() {
        String processInstanceId = "59501";
        HistoricProcessInstance hpi = historyService.createHistoricProcessInstanceQuery()
                .processInstanceId(processInstanceId).
                        singleResult();
        System.out.println("流程定义ID：" + hpi.getProcessDefinitionId());
        System.out.println("流程实例ID：" + hpi.getId());
        System.out.println(hpi.getStartTime());
        System.out.println(hpi.getStartActivityId());
        System.out.println(hpi.getEndTime());
    }


    @Test
    public void queryProcessInstanceState2() {
        String processInstanceId = "59501";
        HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery()
                .processInstanceId(processInstanceId).singleResult();
        if (historicProcessInstance.getEndTime() == null) {
            System.out.println("当前的流程实例正在运行");
        } else {
            System.out.println("当前的流程实例已经结束");
        }
    }

    /**
     * : select RES.* from ACT_HI_ACTINST RES order by RES.ID_ asc
     */
    @Test
    public void createHistoricActivityInstanceQuery() {
        List<HistoricActivityInstance> list = historyService.createHistoricActivityInstanceQuery()
                .list();
        for (HistoricActivityInstance hai : list) {
            System.out.println(hai.getId());
        }
    }

    /**
     * select distinct RES.* from ACT_HI_TASKINST RES order by RES.ID_ asc
     */
    @Test
    public void createHistoricTaskInstanceQuery() {
        List<HistoricTaskInstance> list = historyService.createHistoricTaskInstanceQuery()
                .list();
        for (HistoricTaskInstance ht : list) {
            System.out.println(ht.getId());
            System.out.println(ht.getName());
            System.out.println(ht.getAssignee());
        }
    }


    @Test
    public void addParticipantGroup() {
        runtimeService.addParticipantGroup("57504","00122222");
//        runtimeService.addUserIdentityLink("57504","wwm",IdentityLinkType.CANDIDATE);
    }


    @Test
    public void addParticipantUser() {
        runtimeService.addParticipantUser("87501","1997");

//        runtimeService.addParticipantGroup("57504","wwm",IdentityLinkType.CANDIDATE);
    }


    @Test
    public void deleteParticipantUser() {
        runtimeService.deleteParticipantUser("57504","1997");
//        runtimeService.deleteParticipantGroup("57504","wwm",IdentityLinkType.CANDIDATE);
    }





    @Test
    public void getIdentityLinksForProcessInstance() {
        String instanceId = "57504";
        List<IdentityLink>  list = runtimeService.getIdentityLinksForProcessInstance(instanceId);
        for (IdentityLink identityLink : list){
            System.out.println(identityLink.getProcessDefinitionId());
            System.out.println(identityLink.getType());
            System.out.println(identityLink.getGroupId());
            System.out.println(identityLink.getTaskId());
            System.out.println(identityLink.getUserId());
        }
    }



    /**
     * 设置流程实例的启动人
     */
    @Test
    public void setAuthenticatedUserId1() {
        String authenticatedUserId = "分享牛";
        identityService.setAuthenticatedUserId(authenticatedUserId);
        String processDefinitionKey = "leave";
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(processDefinitionKey);
        System.out.println(processInstance.getId() + "," + processInstance.getActivityId());
    }


    @Test
    public void setAuthenticatedUserId2() {
        String authenticatedUserId = "分享牛2";
        Authentication.setAuthenticatedUserId(authenticatedUserId);
        String processDefinitionKey = "leave";
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(processDefinitionKey);
        System.out.println(processInstance.getId() + "," + processInstance.getActivityId());
    }

    /**
     * select * from ACT_RU_VARIABLE where EXECUTION_ID_ = ? and NAME_= ? and TASK_ID_ is null
     * - ==> Parameters: 77001(String), day(String)
     */
    @Test
    public void getDataObject() {
        String executionId = "150030";
        String dataObject = "shareniuInitiator";
        DataObject dataObject1 = runtimeService.getDataObject(executionId, dataObject);
        if (dataObject1 != null) {
            System.out.println(dataObject1.getDataObjectDefinitionKey());
            System.out.println(dataObject1.getDescription());
            System.out.println(dataObject1.getExecutionId());
            System.out.println(dataObject1.getName());
            System.out.println(dataObject1.getValue());
            System.out.println(dataObject1.getType());
        }
    }


    @Test
    public void getDataObjectLocal() {
        String executionId = "150030";
        String dataObject = "shareniuInitiator";
        DataObject dataObject1 = runtimeService.getDataObjectLocal(executionId, dataObject);
        if (dataObject1 != null) {
            System.out.println(dataObject1.getDataObjectDefinitionKey());
            System.out.println(dataObject1.getDescription());
            System.out.println(dataObject1.getExecutionId());
            System.out.println(dataObject1.getName());
            System.out.println(dataObject1.getValue());
            System.out.println(dataObject1.getType());
        }
    }

    /**
     * select * from ACT_RU_VARIABLE where EXECUTION_ID_ = ? and TASK_ID_ is null
     */
    @Test
    public void getDataObject2() {
        String executionId = "150030";
        Map<String, DataObject> dataObject1 = runtimeService.getDataObjects(executionId);
        Set<Map.Entry<String, DataObject>> entries = dataObject1.entrySet();
        for (Map.Entry<String, DataObject> dataObjectEntry : entries) {
            DataObject dataObject = dataObjectEntry.getValue();
            if (dataObject != null) {
                System.out.println(dataObject.getDataObjectDefinitionKey());
                System.out.println(dataObject.getDescription());
                System.out.println(dataObject.getExecutionId());
                System.out.println(dataObject.getName());
                System.out.println(dataObject.getValue());
                System.out.println(dataObject.getType());
            }
        }
    }


    @Test
    public void getDataObjectsLocal() {
        String executionId = "150030";
        Map<String, DataObject> dataObject1 = runtimeService.getDataObjectsLocal(executionId);
        Set<Map.Entry<String, DataObject>> entries = dataObject1.entrySet();
        for (Map.Entry<String, DataObject> dataObjectEntry : entries) {
            DataObject dataObject = dataObjectEntry.getValue();
            if (dataObject != null) {
                System.out.println(dataObject.getDataObjectDefinitionKey());
                System.out.println(dataObject.getDescription());
                System.out.println(dataObject.getExecutionId());
                System.out.println(dataObject.getName());
                System.out.println(dataObject.getValue());
                System.out.println(dataObject.getType());
            }
        }
    }



    @Test
    public void getVariable() {
        String executionId = "150030";
        String name = "shareniuInitiator";
        Object object = runtimeService.getVariable(executionId, name);
        System.out.println("==============================");
        System.out.println(object);
    }


    @Test
    public void getVariables() {
        String executionId = "125032";
        Map<String,Object> map = runtimeService.getVariables(executionId);
        for (String s : map.keySet()) {
            Object object = map.get(s);
            System.out.println("==============================");
            System.out.println(object);
        }
    }


    @Test
    public void getVariables1() {
        String executionId = "160086";
        List<String> names = new ArrayList<>();
        names.add("bbb");
        names.add("firstname");
        Map<String,Object> map = runtimeService.getVariables(executionId,names);
        for (String s : map.keySet()) {
            Object object = map.get(s);
            System.out.println("==============================");
            System.out.println(object);
        }
    }


    @Test
    public void hasVariable() {
        String executionId = "160086";
        String name = "bbb";
        boolean b = runtimeService.hasVariable(executionId,name);
        System.out.println(b);
    }


    @Test
    public void setVariable() {
        String executionId = "160086";
        //runtimeService.setVariable(executionId, "firstname","wwm");
        runtimeService.setVariable(executionId,"isdelete",true);
    }


    @Test
    public void setVariable1() {
        String executionId = "160086";
        //runtimeService.setVariable(executionId, "firstname","wwm");
        Map<String,Object> map = new HashMap<>();
        map.put("aaa","aaaavalue");
        map.put("bbb","bbbbvalue");
        runtimeService.setVariables(executionId,map);
    }


    @Test
    public void removeVariable() {
        String executionId = "160086";
        String name = "aaa";
        runtimeService.removeVariable(executionId,name);
    }



    //IdentityLinkType.PARTICIPANT

    @Test
    public void deleteProcessInstance() {
        String processInstanceId = "257501";
        String deleteReason = "无聊删除lalal";
        runtimeService.deleteProcessInstance(processInstanceId, deleteReason);
    }

    @Test
    public void deleteProcessInstanceCascade() {
        String processInstanceId = "125003";
        String deleteReason = "无聊删除";
        // ProcessEngineConfigurationImpl processEngineConfiguration = (ProcessEngineConfigurationImpl) processEngine.getProcessEngineConfiguration();
        //processEngineConfiguration.getExecutionEntityManager().deleteProcessInstance(processInstanceId,deleteReason,true);

        //DeleteProcessInstanceCaCadeCmd
        managementService.executeCommand(new DeleteProcessInstanceCaCadeCmd(processInstanceId, deleteReason));
    }

    @Test
    public void getActiveActivityIds() {
        String executionId = "77005";
        List<String> activeActivityIds = runtimeService.getActiveActivityIds(executionId);
        for (String s : activeActivityIds) {
            System.out.println(s);
        }
    }

    @Test
    public void startProcessInstanceById() {
        String processDefinitionId = "dataobject:1:74504";
        ProcessInstance processInstance = runtimeService.startProcessInstanceById(processDefinitionId);
    }

    /**
     * org.flowable.common.engine.api.FlowableObjectNotFoundException: no processes deployed with key 'leave' for tenant identifier 'oo1'
     */
    @Test
    public void startProcessInstanceByKeyAndTenantId() {
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKeyAndTenantId("leave", "001");
    }

    @Test
    public void isProcessDefinitionSuspended() {
        String processDefinitionId = "leave:3:125031";
        boolean processDefinitionSuspended = repositoryService.isProcessDefinitionSuspended(processDefinitionId);
        System.out.println(processDefinitionSuspended);

    }

    /**
     * 流程定义表状态是2 表示已经被挂起，1的话是没有被挂起。
     */
    @Test
    public void suspendProcessDefinitionById() {
        String processDefinitionId = "leave:3:125031";
    repositoryService
                .suspendProcessDefinitionById(processDefinitionId);
        System.out.println(processDefinitionId);

    }

    @Test
    public void startProcessInstanceById2() {
        String processDefinitionId = "leave:3:125031";
        runtimeService.startProcessInstanceById(processDefinitionId);
    }


    //repositoryService的激活
    @Test
    public void repositoryServiceactivateProcessDefinitionById() {
        String processDefinitionId = "leave:3:125031";
        repositoryService.activateProcessDefinitionById(processDefinitionId);
    }


    //runtimeService的激活
    @Test
    public void runtimeServiceactivateProcessDefinitionById() {
        String processDefinitionId = "142504";
        //runtimeService.activateProcessInstanceById(processDefinitionId);
        runtimeService.suspendProcessInstanceById("");
    }

    @Test
    public void suspendProcessDefinitionById2() {
        String processDefinitionId = "leave:2:67004";
        repositoryService.suspendProcessDefinitionById(processDefinitionId,true,null);
    }


}
