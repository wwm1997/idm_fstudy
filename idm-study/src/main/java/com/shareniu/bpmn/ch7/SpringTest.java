package com.shareniu.bpmn.ch7;

import org.apache.commons.io.FileUtils;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.common.engine.impl.util.IoUtil;
import org.flowable.engine.*;
import org.flowable.engine.repository.*;
import org.flowable.form.api.FormDefinition;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipInputStream;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:flowable-context.xml")
public class SpringTest {

    ProcessEngine processEngine;

    RepositoryService repositoryService;

    @Before
    public  void  buildProcessEngine(){
        processEngine = ProcessEngines.getDefaultProcessEngine();
        System.out.println(processEngine);
        repositoryService = processEngine.getRepositoryService();
        System.out.println(repositoryService);
//        String name = processEngine.getName();
//        System.out.println("流程引擎的名称：" + name);
//        DynamicBpmnService dynamicBpmnService = processEngine.getDynamicBpmnService();
//        System.out.println(dynamicBpmnService);
//        FormService formService = processEngine.getFormService();
//        System.out.println(formService);
//
//        HistoryService historyService = processEngine.getHistoryService();
//        System.out.println(historyService);
//        IdentityService identityService = processEngine.getIdentityService();
//        System.out.println(identityService);
//        ManagementService managementService = processEngine.getManagementService();
//        System.out.println(managementService);
//        ProcessEngineConfiguration processEngineConfiguration = processEngine.getProcessEngineConfiguration();
//        System.out.println(processEngineConfiguration);
//        RuntimeService runtimeService = processEngine.getRuntimeService();
//        System.out.println(runtimeService);
//        TaskService taskService = processEngine.getTaskService();
//        System.out.println(taskService);
    }

    @Test
    public  void  DeploymentBuild(){
        DeploymentBuilder deploymentBuilder = repositoryService
                .createDeployment()
                .category("测试分类")
                .name("名称");
        System.out.println(deploymentBuilder);
    }

    /**
     * 文本方式部署
     * 资源的名称必须是String[] { "bpmn20.xml", "bpmn" }; 结尾的才可以部署到流程定义表
     */
    @Test
    public  void  deploy(){
        DeploymentBuilder deploymentBuilder = repositoryService
                .createDeployment()
                .category("shareniu1")
                .name("Complex_compensation")
                .addClasspathResource("Complex_compensation.manualmodif.importInFlowable_NOSHAPE.bpmn20.xml");
        Deployment deploy = deploymentBuilder.deploy();
        System.out.println(deploy.getId());
    }

    /**
     * 文本方式部署
     * 资源的名称必须是String[] { "bpmn20.xml", "bpmn" }; 结尾的才可以部署到流程定义表
     */
    @Test
    public  void  addString(){
        String text= IoUtil.readFileAsString("Complex_compensation.manualmodif.importInFlowable_NOSHAPE.bpmn20.xml");
        DeploymentBuilder deploymentBuilder = repositoryService
                .createDeployment()
                .category("测试分类")
                .name("名称")
                .key("测试的key")
                .addString("吴伟铭",text);
        Deployment deploy = deploymentBuilder.deploy();
        System.out.println(deploy.getId());
    }

    @Test
    public  void  addInputStream(){
        InputStream resourceAsStream = SpringTest.class.getClassLoader()
                .getResourceAsStream("Complex_compensation.manualmodif.importInFlowable_NOSHAPE.bpmn20.xml");
        DeploymentBuilder deploymentBuilder = repositoryService
                .createDeployment()
                .category("测试分类")
                .name("名称")
                .key("测试的key")
                .addInputStream("shareniuwwmlala",resourceAsStream);
        Deployment deploy = deploymentBuilder.deploy();
        System.out.println(deploy.getId());
    }

    @Test
    public  void  addZipInputStream(){
        InputStream resourceAsStream = SpringTest.class.getClassLoader()
                .getResourceAsStream("1.zip");

        ZipInputStream zipInputStream=new ZipInputStream(resourceAsStream);
        DeploymentBuilder deploymentBuilder = repositoryService
                .createDeployment()
                .category("测试分类13")
                .name("名称113")
                .key("测试的key113")
                .deploymentProperty("create_user","wwwmmm")
                .addZipInputStream(zipInputStream);
        Deployment deploy = deploymentBuilder.deploy();
        System.out.println(deploy.getId());
    }

    @Test
    public  void  addBytes(){
        InputStream resourceAsStream = SpringTest.class.getClassLoader()
                .getResourceAsStream("Complex_compensation.manualmodif.importInFlowable_NOSHAPE.bpmn20.xml");
        String inputStreamName="分享牛";
        byte[] bytes = IoUtil.readInputStream(resourceAsStream, inputStreamName);
        DeploymentBuilder deploymentBuilder = repositoryService
                .createDeployment()
                .category("测试分类吴伟铭")
                .name("名称")
                .key("测试的key")
              .addBytes("测试分类吴伟铭.bpmn",bytes);
        Deployment deploy = deploymentBuilder.deploy();
        System.out.println(deploy.getId());
    }

    @Test
    public  void  createProcessDefinitionQuery(){
        Set<String> set = new HashSet<>();
        set.add("Complex_compensation1:2:57504");
        set.add("myProcess_1:1:50006");
        List<ProcessDefinition> list = repositoryService.createProcessDefinitionQuery()
                //.latestVersion() //查询的时候最后的一个版本
                .processDefinitionName("Complex_compensation")
                .processDefinitionCategory("shareniu1")
                .processDefinitionKeyLike("%compensation1%")
                .processDefinitionIds(set)
                .list();
        for (ProcessDefinition pd:list){
            System.out.println("###################");
            System.out.println(pd.getId());
            System.out.println(pd.getCategory());
            System.out.println(pd.getDiagramResourceName());
            System.out.println(pd.getResourceName());
            System.out.println(pd.getDeploymentId());
        }
    }

    @Test
    public  void  deleteDeployment(){
        String deploymentId="52501";
        repositoryService.deleteDeployment(deploymentId);
    }

    /**
     * 级联删除会删除当前流程定义下面所有的流程实例
     */
    @Test
    public  void  deleteDeploymentCaseCade(){
        String deploymentId="55001";
        repositoryService.deleteDeployment(deploymentId,true);
    }

    @Test
    public  void  viewImage() throws IOException {
        String deploymentId="37501";
        List<String> deploymentResourceNames = repositoryService
                .getDeploymentResourceNames(deploymentId);

        System.out.println(deploymentResourceNames);

        String imageName=null;
        for (String name : deploymentResourceNames){
            if (name.indexOf(".png")>0){
                imageName=name;
            }
        }
        System.out.println(imageName);
        if (imageName!=null){
            File file=new File("~/Downloads/idm-study/"+imageName);
            InputStream resourceAsStream = repositoryService.getResourceAsStream(deploymentId, imageName);
            FileUtils.copyInputStreamToFile(resourceAsStream,file);
        }
    }

    /**
     * select RES.* from ACT_RE_DEPLOYMENT RES WHERE RES.ID_ = ? and RES.CATEGORY_ = ? order by RES.ID_ asc
     */
    @Test
    public  void  createDeploymentQuery(){
        List<ProcessDefinition> list = repositoryService.createProcessDefinitionQuery()
                .processDefinitionKeyLike("leave")
                //.deploymentId("37501")
                .list();
        for(ProcessDefinition processDefinition :list){
            System.out.println("###################");
            System.out.println(processDefinition.getId());
            System.out.println(processDefinition.getDiagramResourceName());
            System.out.println(processDefinition.getResourceName());
            System.out.println(processDefinition.getKey());
        }
    }


    /**
     * 原生sql查询部署信息
     */
    @Test
    public  void  createNativeDeploymentQuery(){
        List<Deployment> list = repositoryService.createNativeDeploymentQuery()
                .sql("select * from ACT_RE_DEPLOYMENT").list();

        for(Deployment deployment :list){
            System.out.println("###################");
            System.out.println(deployment.getId());
            System.out.println(deployment.getKey());
        }
    }


    /**
     * 原生sql查询定义信息
     */
    @Test
    public  void  createNativeProcessDefinitionQuery(){
        List<ProcessDefinition> list = repositoryService.createNativeProcessDefinitionQuery()
                .sql("select * from ACT_RE_DEPLOYMENT").list();

        for(ProcessDefinition processDefinition :list){
            System.out.println("###################");
            System.out.println(processDefinition.getId());
            System.out.println(processDefinition.getDiagramResourceName());
            System.out.println(processDefinition.getResourceName());
            System.out.println(processDefinition.getKey());
        }
    }



    @Test
    public  void  test1(){
        List<String> list = repositoryService.getDeploymentResourceNames("62501");
    }


    /**
     * 将部署移动到其他应用程序部署父级
     */
    @Test
    public  void  test2(){
        repositoryService.changeDeploymentParentDeploymentId("57501","62501");
    }


    /**
     * 用给定的ID挂起流程定义。如果流程定义处于暂停状态，则将无法基于流程定义启动新流程实例。
     * 注意：流程定义的所有流程实例仍将处于活动状态（即未挂起）！
     */
    @Test
    public void  test3(){
        repositoryService.suspendProcessDefinitionById("Complex_compensation1:2:57504");
    }

    /**
     * 用给定的ID挂起流程定义。如果流程定义处于暂停状态，则将无法基于流程定义启动新流程实例。
     */
    @Test
    public void  test4(){
        repositoryService.suspendProcessDefinitionById("Complex_compensation1:2:57504",true,null);
    }

    /**
     * 用给定的键（= bpmn20.xml文件中的id）挂起所有进程定义。如果流程定义处于暂停状态，则将无法基于流程定义启动新流程实例
     */
    @Test
    public void  test5(){
        repositoryService.suspendProcessDefinitionByKey("Complex_compensation1");
    }


    /**
     * 使用给定的ID激活流程定义
     */
    @Test
    public void  test6(){
        repositoryService.activateProcessDefinitionById("Complex_compensation1:2:57504");
    }

    /**
     * 使用给定的ID激活流程定义
     */
    @Test
    public void  test7(){
        repositoryService.activateProcessDefinitionById("Complex_compensation1:2:57504");
    }


    /**
     * 使用给定的key激活流程定义
     */
    @Test
    public void  test8(){
        repositoryService.activateProcessDefinitionByKey("Complex_compensation1");
    }


    /**
     * 使用给定的key激活流程定义
     */
    @Test
    public void  test9(){
        repositoryService.activateProcessDefinitionByKey("Complex_compensation1",true,null);
    }


    /**
     * 设置流程定义的类别。可以按类别查询流程定义
     */
    @Test
    public void  test10(){
        repositoryService.setProcessDefinitionCategory("Complex_compensation1:2:57504","shareniu2");
    }


    /**
     * 通过字节流提供对已部署流程模型（例如BPMN 2.0 XML文件）的访问
     */
    @Test
    public void  test11() throws Exception{
        InputStream in = repositoryService.getProcessModel("Complex_compensation1:2:57504");
        //File file = new File("/data/home/umlinux/ideaProjects/idm-study/idm-study/src/main/resources/File/1.bpmn20.xml");
        //File file = new File(in);
        int index;
        byte[] bytes = new byte[1024];
        FileOutputStream downloadFile = new FileOutputStream("/data/home/umlinux/IdeaProjects/idm-study/idm-study/src/main/resources/File/1.bpmn20.xml");
        while ((index = in.read(bytes)) != -1) {
            downloadFile.write(bytes, 0, index);
            downloadFile.flush();
        }
        in.close();
        downloadFile.close();
    }


    /**
     * 通过字节流访问已部署的流程图，例如PNG图像
     */
    @Test
    public void  test12() throws Exception{
        InputStream in = repositoryService.getProcessDiagram("Complex_compensation1:2:57504");
        int index;
        byte[] bytes = new byte[1024];
        FileOutputStream downloadFile =
                new FileOutputStream("/data/home/umlinux/IdeaProjects/idm-study/idm-study/src/main/resources/File/1.png");
        while ((index = in.read(bytes)) != -1) {
            downloadFile.write(bytes, 0, index);
            downloadFile.flush();
        }
        in.close();
        downloadFile.close();
    }


    /**
     * 返回ProcessDefinition包括所有BPMN信息的信息，例如其他属性（例如文档）
     * @throws Exception
     */
    @Test
    public void  test13() throws Exception{
        ProcessDefinition processDefinition = repositoryService.getProcessDefinition("Complex_compensation1:2:57504");
        System.out.println(processDefinition.getDeploymentId());
        System.out.println(processDefinition.getName());
        System.out.println(processDefinition.getDerivedFrom());
    }


    /**
     * 提供由所提供的流程图中元素的位置和尺寸getProcessDiagram(String)。此方法需要部署过程模型和图表图像
     * @throws Exception
     */
    @Test
    public void  test14() throws Exception{
        DiagramLayout diagramLayout = repositoryService.getProcessDiagramLayout("Complex_compensation1:3:2515");
        System.out.println("=======================");
        System.out.println(diagramLayout.getElements());
        System.out.println("=======================");
        System.out.println(diagramLayout.getNode("SCRIPT_1"));
        System.out.println("=======================");
        //System.out.println(diagramLayout.getName());
        System.out.println(diagramLayout.getNodes());
        System.out.println(diagramLayout.getNode("SCRIPT_AFTER_CANCEL"));
    }


    @Test
    public void  test15() throws Exception{
        Object object = repositoryService.getAppResourceObject("Complex_compensation1:3:2515");
        System.out.println(object);
    }
}
