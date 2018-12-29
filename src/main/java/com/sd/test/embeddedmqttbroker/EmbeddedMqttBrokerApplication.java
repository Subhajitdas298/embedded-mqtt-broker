package com.sd.test.embeddedmqttbroker;

import com.sd.test.embeddedmqttbroker.broker.authplugin.AABrokerPlugin;
import org.apache.activemq.broker.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class EmbeddedMqttBrokerApplication {

	public static void main(String[] args) {
		SpringApplication.run(EmbeddedMqttBrokerApplication.class, args);
	}

	@Bean
	@Autowired
	BrokerService brokerService(AABrokerPlugin aaBrokerPlugin) throws Exception {
		BrokerService broker = new BrokerService();
		broker.setPersistent(false);
		broker.setUseJmx(false);
		broker.setBrokerName("e-mqtt");
		broker.setUseShutdownHook(false);
		//Add plugin
		BrokerPlugin[] brokerPlugins = new BrokerPlugin[1];
		brokerPlugins[0] = aaBrokerPlugin;

		broker.setPlugins(brokerPlugins);

		broker.addConnector("mqtt://localhost:1883");

		broker.start();
		return broker;
	}
}

