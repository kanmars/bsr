package cn.kanmars.bsr.http.session;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;

public class BSRHttpSession implements HttpSession {

	public long creationTime;

	public long getCreationTime() {
		return creationTime;
	}

	public void setCreationTime(long creationTime) {
		this.creationTime = creationTime;
	}

	public String id;
	
	

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public long lastAccessedTime;

	
	public long getLastAccessedTime() {
		return lastAccessedTime;
	}

	public void setLastAccessedTime(long lastAccessedTime) {
		this.lastAccessedTime = lastAccessedTime;
	}

	/**
	 * 超时的秒数，默认为30分钟
	 */
	public int maxInactiveInterval = 30*60;

	/**
	 * 获取session超时的秒数
	 */
	public int getMaxInactiveInterval() {
		return maxInactiveInterval;
	}

	/**
	 * 设置session超时的秒数
	 */
	public void setMaxInactiveInterval(int maxInactiveInterval) {
		if(maxInactiveInterval>0){
			this.maxInactiveInterval = maxInactiveInterval;
		}
	}

	public ServletContext getServletContext() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public HttpSessionContext getSessionContext() {
		// TODO Auto-generated method stub
		return null;
	}

	
	public final Map<String, Object> attributes = new HashMap<String, Object>();
	

	public Map<String, Object> getAttributes() {
		return attributes;
	}
	public Object getAttribute(String name) {
		return attributes.get(name);
	}
	public void setAttribute(String name, Object value) {
		attributes.put(name, value);
	}
	
	public void removeAttribute(String name) {
		attributes.remove(name);
	}
	
	/**
	 * 获取属性迭代器
	 */
	public Enumeration<String> getAttributeNames() {
		return new Enumeration<String>() {
			Iterator<String> attributes_iterator = attributes.keySet().iterator();
			public boolean hasMoreElements() {
				return attributes_iterator.hasNext();
			}
			public String nextElement() {
				return attributes_iterator.next();
			}
			
		};
	}
	
	public final Map<String, Object> values = new HashMap<String, Object>();

	public Object getValue(String name) {
		return values.get(name);
	}

	
	public String[] getValueNames() {
		
		List<String> key_array = new ArrayList<String>();
		for(String tmp :values.keySet()){
			key_array.add(tmp);
		}
		String[] valueNames = new String[key_array.size()];
		for(int i = 0 ; i<key_array.size();i++){
			valueNames[i] = key_array.get(i);
		}
		return valueNames;
	}

	

	public void putValue(String name, Object value) {
		values.put(name, value);
	}

	

	public void removeValue(String name) {
		values.remove(name);
	}
	
	/**
	 * 注销方法
	 */
	public void invalidate() {
		
	}
	
	private boolean isnew = true;

	public boolean isNew() {
		return isnew;
	}
	
	public void setIsNew(boolean isnew){
		this.isnew = isnew;
	}

}
