package com.zakimak.config;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import com.zakimak.service.ClientTest;
import com.zakimak.service.MessageConsumer;

@ApplicationPath("rs")
public class ApplicationConfig extends Application {
	
	private final Set<Class<?>> classes;

	public ApplicationConfig() {
		HashSet<Class<?>> c = new HashSet<Class<?>>();
		c.add(MessageConsumer.class);
		c.add(ClientTest.class);
		classes = Collections.unmodifiableSet(c);
	}

	@Override
	public Set<Class<?>> getClasses() {
		return classes;
	}

}
