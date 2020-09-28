package com.example.cameldemo.util;

import static java.lang.Boolean.TRUE;
import static java.util.Objects.nonNull;
import static javax.xml.bind.JAXBContext.newInstance;
import static javax.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT;
import static org.apache.logging.log4j.util.Strings.isNotBlank;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;
import javax.xml.transform.stream.StreamSource;

public class JaxbTransformer {
	public static String toXml(Object object, final String contextPath, String qName) {
		String xml = null;
		
		if(nonNull(object)) {
			try {
				StringWriter writer = new StringWriter();
				Marshaller marshaller = newInstance(contextPath).createMarshaller();
				marshaller.setProperty(JAXB_FORMATTED_OUTPUT, TRUE);
				marshaller.marshal(toJaxbElement(object,qName),writer);
				xml = writer.toString();
			}catch(JAXBException cause) {
				System.out.println(cause.getLocalizedMessage());
			}
		}
		return xml;
	}
	public static<T> T fromXml(final String xml, final String contextPath, final Class<T> type) {
		T object = null;
		if(isNotBlank(xml)) {
			try {
				object = newInstance(contextPath).createUnmarshaller().unmarshal(new StreamSource(new StringReader(xml)),type).getValue();
			}catch(JAXBException cause) {
				System.out.println(cause.getLocalizedMessage());
			}
		}
		return object;
	}
	public static JAXBElement<?> toJaxbElement(final Object object, final String qName) {
		return new JAXBElement(new QName("", qName), object.getClass(), object);
	}
}
