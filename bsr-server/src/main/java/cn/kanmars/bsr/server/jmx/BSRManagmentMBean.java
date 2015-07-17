package cn.kanmars.bsr.server.jmx;

import java.util.List;


public interface BSRManagmentMBean {
	public List<String> getBeanNameList() throws Exception;
	public Object getBean(String beanName) throws Exception;
	public Object getAttribute(String beanName,String attrName) throws Exception;
	public void setAttribute(String beanName,String name,String value) throws Exception;
	public Object invoke(String beanName,String actionName) throws Exception;
}
