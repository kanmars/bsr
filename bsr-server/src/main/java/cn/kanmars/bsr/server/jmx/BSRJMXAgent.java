package cn.kanmars.bsr.server.jmx;


import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.ObjectName;
import javax.management.remote.JMXConnectorServer;
import javax.management.remote.JMXConnectorServerFactory;
import javax.management.remote.JMXServiceURL;

import cn.kanmars.bsr.server.config.BSRConfiger;
import cn.kanmars.bsr.server.constant.BSRConstants;
import cn.kanmars.bsr.server.log.Logger;

public class BSRJMXAgent {
	public static void main(String[] args) throws Exception {
		initBSRJMXAgent();
	}
	
	public static void initBSRJMXAgent() throws Exception {
		
		MBeanServer server = MBeanServerFactory.createMBeanServer();
		
		BSRManagmentMBean bsrManagmentMBean = BSRManagment.getInstance();
		ObjectName object_name = new ObjectName("Standard_BSRManagment_MBeans:name=BSRManagmentMBean");
		server.registerMBean(bsrManagmentMBean, object_name);
		Logger.info("BSRJMXAgent...SERVER...STARTED......");
		String host = BSRConfiger.getConfig(BSRConstants.BIND_IP);
		int port = Integer.parseInt(BSRConfiger.getConfig(BSRConstants.JMX_PORT));
		
		Registry registry = LocateRegistry.createRegistry(port);
		
		String u = "service:jmx:rmi:///jndi/rmi://"+host+":"+port+"/server";
		Logger.info("BSRJMX...ADDRESS["+u+"]");
		JMXServiceURL url = new JMXServiceURL(u);
		JMXConnectorServer jmxserver = JMXConnectorServerFactory.newJMXConnectorServer(url, null, server);
		jmxserver.start();
		
	}

}
