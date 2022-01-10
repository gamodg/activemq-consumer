package com.activemq.consumer.config;

import javax.jms.DeliveryMode;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.RedeliveryPolicy;
import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.core.JmsTemplate;

import com.activemq.consumer.converter.EmployeeMessageConverter;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Configuration
@EnableJms
public class JMSConfig {

	@Value("${spring.activemq.broker-url}")
	private String brokerUrl;
	@Value("${activemq.queue}")
	private String queueName;
	@Value("${activemq.initialRedeliveryDelay}")
	private int initialRedeliveryDelay;
	@Value("${activemq.useCollisionAvoidance}")
	private boolean useCollisionAvoidance;
	@Value("${activemq.maximumRedeliveries}")
	private int maximumRedeliveries;
	@Value("${activemq.useExponentialBackOff}")
	private boolean useExponentialBackOff;
	@Value("${activemq.backOffMultiplier}")
	private double backOffMultiplier;

	private final EmployeeMessageConverter employeeMessageConverter;

	@Bean
	public ActiveMQConnectionFactory connectionFactory() {

		ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory();
		activeMQConnectionFactory.setBrokerURL(brokerUrl);

		ActiveMQQueue queue = new ActiveMQQueue(queueName);
		RedeliveryPolicy redeliveryPolicy = new RedeliveryPolicy();
		redeliveryPolicy.setInitialRedeliveryDelay(initialRedeliveryDelay);
		redeliveryPolicy.setUseCollisionAvoidance(useCollisionAvoidance);
		redeliveryPolicy.setUseExponentialBackOff(useExponentialBackOff);
		redeliveryPolicy.setBackOffMultiplier(backOffMultiplier);
		redeliveryPolicy.setMaximumRedeliveries(maximumRedeliveries);
		redeliveryPolicy.setDestination(queue);
		activeMQConnectionFactory.setRedeliveryPolicy(redeliveryPolicy);
		return activeMQConnectionFactory;
	}

	@Bean
	public DefaultJmsListenerContainerFactory jmsListenerContainerFactory() {
		DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
		factory.setConnectionFactory(connectionFactory());
		factory.setSessionTransacted(false);
		factory.setSessionAcknowledgeMode(Session.CLIENT_ACKNOWLEDGE);
		factory.setMessageConverter(employeeMessageConverter);
		return factory;
	}

	@Bean
	public JmsTemplate jmsAckTemplate() {
		JmsTemplate template = new JmsTemplate();
		template.setConnectionFactory(connectionFactory());
		template.setSessionAcknowledgeMode(Session.CLIENT_ACKNOWLEDGE);
		template.setDeliveryMode(DeliveryMode.PERSISTENT);
		return template;
	}
}
