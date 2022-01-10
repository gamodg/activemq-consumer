package com.activemq.consumer.service;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.support.JmsHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

import com.activemq.consumer.model.Employee;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class JMSConsumer {

	@JmsListener(destination = "${activemq.queue}")
	public void receive(Employee employee, Message message, Session session,
			@Header(name = "JMSXDeliveryCount", defaultValue = "1") String redeliveryCount,
			@Header(name = JmsHeaders.MESSAGE_ID, defaultValue = "1") String messageId) throws JMSException {
		log.info("Received - redeliveryCount: {}, messageId: {}, employee {}", redeliveryCount, messageId, employee);
		try {
			if (employee != null && employee.getName() != null && employee.getName().contains("err")) {
				throw new Exception("Forcing reading jms message problem for message read in queue ");
			}
			message.acknowledge();
		} catch (Exception e) {
			session.recover();
		}

	}
}
