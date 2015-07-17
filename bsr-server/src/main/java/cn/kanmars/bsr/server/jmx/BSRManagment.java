package cn.kanmars.bsr.server.jmx;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import javax.management.Notification;
import javax.management.NotificationBroadcasterSupport;

public class BSRManagment extends NotificationBroadcasterSupport implements BSRManagmentMBean {
	
	public static ConcurrentHashMap<String, Object> beanRegister = new ConcurrentHashMap<String, Object>();
	
	private static BSRManagment bsrManagment = null;
	
	private BSRManagment(){
		
	}
	
	public static BSRManagment getInstance(){
		synchronized(beanRegister){
			if(bsrManagment==null){
				bsrManagment =  new BSRManagment();
			}
			return bsrManagment;
		}
	}
	
	public static void addBean(String name,Object obj) throws Exception{
		beanRegister.put(name, obj);
	}
	
	public List<String> getBeanNameList() throws Exception {
		List result = new ArrayList<String>();
		for(Entry<String, Object> e: beanRegister.entrySet()){
			result.add(e.getKey());
		}
		return result;
	}

	public Object getBean(String beanName) throws Exception {
		return beanRegister.get(beanName);
	}
	
	public Object getAttribute(String beanName,String name) throws Exception {
		return getBean(beanName).getClass().getField(name).get(getBean(beanName));
	}

	public void setAttribute(String beanName,String name, String value) throws Exception {
		Field f = getBean(beanName).getClass().getField(name);
		if(f.getType().equals(Integer.class)){
			f.set(getBean(beanName), Integer.parseInt(value));
		}
		if(f.getType().equals(String.class)){
			f.set(getBean(beanName), value);
		}
		if(f.getType().equals(Double.class)){
			f.set(getBean(beanName), Double.parseDouble(value));
		}
		if(f.getType().equals(Long.class)){
			f.set(getBean(beanName), Long.parseLong(value));
		}
		if(f.getType().equals(Float.class)){
			f.set(getBean(beanName), Float.parseFloat(value));
		}
		if(f.getType().equals(Boolean.class)){
			f.set(getBean(beanName), Boolean.parseBoolean(value));
		}
	}

	public Object invoke(String beanName,String actionName) throws Exception {
		Class[] paramesClass = new Class[]{};
		Method m = null;
		try{
			//先查找本类的公共方法，父类的公共方法.....
			m = getBean(beanName).getClass().getMethod(actionName, paramesClass);
		}catch(Exception e){
			try {
				//如果公共方法未找到，再查找类自身声明的所有方法，包含public、protected和private方法。
				m = getBean(beanName).getClass().getDeclaredMethod(actionName, paramesClass);
			} catch (NoSuchMethodException e1) {
				e1.printStackTrace();
			} catch (SecurityException e1) {
				e1.printStackTrace();
			}
		}
		if(m==null){
			System.out.println("未找到对应方法");
			return null;
		}
		try {
			return m.invoke(getBean(beanName), null);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return null;
	}

}
