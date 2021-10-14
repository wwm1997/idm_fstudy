package com.shareniu.bpmn.ch9;

import org.flowable.common.engine.api.delegate.Expression;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

/**
 * <bean id="serviceTaskClass" class="com.shareniu.bpmn.ch9.ServiceTaskClass"></bean>
 * @Component("serviceTaskClass")
 */

public class ServiceTaskClass implements JavaDelegate {
    Expression age;
    public void execute(DelegateExecution execution) {
        //  测试服务任务节点力流程，到时候写总结记得重点
        System.out.println(age);
        String expressionText = age.getExpressionText();
        System.out.println(expressionText);
        Object value = age.getValue(execution);
        System.out.println(value);
        execution.setVariable("分享牛","分享牛");
    }
}
