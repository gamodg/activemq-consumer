package com.activemq.consumer.converter;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.springframework.jms.support.converter.MessageConversionException;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.stereotype.Component;

import com.activemq.consumer.model.Employee;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class EmployeeMessageConverter implements MessageConverter {

	private final ObjectMapper objectMapper;

	@Override
	public Message toMessage(Object object, Session session) throws JMSException, MessageConversionException {
		Employee person = (Employee) object;
		String payload = null;
		try {
			payload = objectMapper.writeValueAsString(person);
		} catch (JsonProcessingException e) {
			log.error("error converting form person", e);
		}

		TextMessage message = session.createTextMessage();
		message.setText(payload);

		return message;
	}

	@Override
	public Employee fromMessage(Message message) throws JMSException, MessageConversionException {
		TextMessage textMessage = (TextMessage) message;
		String payload = textMessage.getText();
		Employee employee = null;
		try {
			employee = objectMapper.readValue(payload, Employee.class);
		} catch (Exception e) {
			log.error("Error converting to Employee Object", e);
		}
		return employee;
	}

}
