package org.pwte.example.app;

import java.util.HashSet;
import java.util.Set;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

import org.pwte.example.resources.CategoryResource;
import org.pwte.example.resources.CustomerOrderResource;
import org.pwte.example.resources.ProductResource;

@ApplicationPath("/jaxrs")

public class CustomerServicesApp extends Application {

	@Override
	public Set<Class<?>> getClasses() {

		Set<Class<?>> classes = new HashSet<Class<?>>();

		classes.add(CategoryResource.class);
		classes.add(CustomerOrderResource.class);
		classes.add(ProductResource.class);
		
		classes.add(com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider.class);

		return classes;

	}
}
