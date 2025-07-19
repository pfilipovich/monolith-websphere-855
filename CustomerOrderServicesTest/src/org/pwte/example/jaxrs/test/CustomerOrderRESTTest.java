package org.pwte.example.jaxrs.test;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;

import org.apache.wink.client.ClientConfig;
import org.apache.wink.client.ClientResponse;
import org.apache.wink.client.Resource;
import org.apache.wink.client.RestClient;
import org.apache.wink.client.handlers.BasicAuthSecurityHandler;

import jakarta.json.JsonArray;
import jakarta.json.JsonObject;

import junit.framework.TestCase;

public class CustomerOrderRESTTest extends TestCase {

	private String urlPrefix; 
	private String urlTestPrefix;
	
	private ClientConfig clientConfig = new ClientConfig();
	private ClientConfig clientConfig2 = new ClientConfig();
		
	public void setUp() throws Exception 
	{
		try {
			Context envEntryContext = (Context) new InitialContext().lookup("java:comp/env");
			urlPrefix = (String) envEntryContext.lookup("CUSTOMER_ORDER_SERVICES_WEB_ENDPOINT");
			urlTestPrefix = (String) envEntryContext.lookup("CUSTOMER_ORDER_SERVICES_TEST_ENDPOINT");
		} catch (NamingException e) {
			e.printStackTrace();
			urlPrefix = "https://localhost:9443/CustomerOrderServicesWeb/";
			urlTestPrefix = "http://localhost:9080/CustomerOrderServicesTest/";
		}
		
		jakarta.ws.rs.core.Application app = new jakarta.ws.rs.core.Application() {
	        public Set<Class<?>> getClasses() {
	            Set<Class<?>> classes = new HashSet<Class<?>>();
	    		classes.add(org.codehaus.jackson.jaxrs.JacksonJaxbJsonProvider.class);
	    		
	    		// WebSphere-specific providers removed for Jakarta EE compatibility
	    		
	            return classes;
	        }
	    };
	    
	    clientConfig.applications(app);
	    clientConfig2.applications(app);
	    
		clientConfig.setLoadWinkApplications(false);
		clientConfig2.setLoadWinkApplications(false);
		
		BasicAuthSecurityHandler basicAuth = new BasicAuthSecurityHandler();
		basicAuth.setUserName("rbarcia");
		basicAuth.setPassword("bl0wfish");
		clientConfig.handlers(basicAuth);
		
		BasicAuthSecurityHandler basicAuth2 = new BasicAuthSecurityHandler();
		basicAuth2.setUserName("kbrown");
		basicAuth2.setPassword("bl0wfish");
		clientConfig2.handlers(basicAuth2);
	}
	
	public void testLoadCustomer()
	{
		RestClient client = new RestClient(clientConfig);
		
		Resource resource = client.resource(urlPrefix + "jaxrs/Customer");
		ClientResponse resourceResponse = resource.accept("application/json").get();
		JsonObject customer = resourceResponse.getEntity(JsonObject.class);
		
		RestClient clientTest = new RestClient();
		Resource resourceTest = clientTest.resource(urlTestPrefix+"sampleJSON/customer.json");
		ClientResponse clientTestResponse = resourceTest.accept("application/json").get();
		
		assertEquals(200, resourceResponse.getStatusCode());
		assertEquals(MediaType.APPLICATION_JSON, resourceResponse.getHeaders().get("Content-Type").get(0));
		
		JsonObject customerTest = clientTestResponse.getEntity(JsonObject.class);
		assertEquals(customer.get("name"), customerTest.get("name"));
		assertEquals(customer.get("householdSize"), customerTest.get("householdSize"));
		assertEquals(customer.get("RESIDENTIAL"), customerTest.get("RESIDENTIAL"));
		assertEquals(customer.get("frequentCustomer"), customerTest.get("frequentCustomer"));
		
	}
	
	public void testUpdateAddress() throws IOException
	{
		
		RestClient clientTest = new RestClient();

		Resource resourceTest = clientTest.resource(urlTestPrefix+"sampleJSON/newAddress1.json");
		JsonObject newAddress1 = resourceTest.accept("application/json").get(JsonObject.class);
		
		RestClient client = new RestClient(clientConfig);

		Resource customerAddress = client.resource(urlPrefix + "jaxrs/Customer/Address");
		ClientResponse clientResponse = customerAddress.contentType(MediaType.APPLICATION_JSON).put(newAddress1.serialize());
		
		assertEquals(204, clientResponse.getStatusCode());
		
		Resource resource = client.resource(urlPrefix + "jaxrs/Customer");
		JsonObject customer = resource.accept("application/json").get(JsonObject.class);
		
		assertEquals(newAddress1, customer.get("address"));
		
		Resource resourceTest2 = clientTest.resource(urlTestPrefix+"sampleJSON/newAddress2.json");
		JsonObject newAddress2 = resourceTest2.accept("application/json").get(JsonObject.class);
		
		Resource customerAddress2 = client.resource(urlPrefix + "jaxrs/Customer/Address");
		ClientResponse clientResponse2 = customerAddress2.contentType(MediaType.APPLICATION_JSON).put(newAddress2.serialize());
		
		assertEquals(204, clientResponse2.getStatusCode());
		
		Resource resource2 = client.resource(urlPrefix + "jaxrs/Customer");
		JsonObject customer2 = resource2.accept("application/json").get(JsonObject.class);
		
		assertEquals(newAddress2, customer2.get("address"));
	}
	
	public void testOrderProcess() throws IOException
	{
		RestClient client = new RestClient(clientConfig);
		RestClient clientTest = new RestClient();
		
		Resource liTest = clientTest.resource(urlTestPrefix+"sampleJSON/LineItem1.json");
		JsonObject li1 = liTest.accept("application/json").get(JsonObject.class);
		
		Resource addTest = client.resource(urlPrefix + "jaxrs/Customer/OpenOrder/LineItem");
		ClientResponse clientResponse = addTest.accept("application/json").contentType("application/json").post(li1.serialize());
		MultivaluedMap<String, String> headers = clientResponse.getHeaders();
		List<String> etag = headers.get("ETag");
		System.out.println("ETag -> " + etag);
		String version = "-1";
		if(etag != null) version = etag.get(0);
		
		assertEquals(200, clientResponse.getStatusCode());
		JsonObject openOrder = clientResponse.getEntity(JsonObject.class);
		
		assertEquals(1, ((JsonArray)openOrder.get("lineitems")).size());
		
		Resource custOrder = client.resource(urlPrefix + "jaxrs/Customer");
		JsonObject customer = custOrder.accept("application/json").get(JsonObject.class);
		
		JsonObject openOrder2 = (JsonObject)customer.get("openOrder");
		
		assertEquals(openOrder2.get("total"), openOrder.get("total"));
		assertEquals(openOrder2.get("status"), openOrder.get("status"));
		assertEquals(openOrder2.get("orderId"), openOrder.get("orderId"));
		assertEquals(((JsonArray)openOrder2.get("lineitems")).size(), ((JsonArray)openOrder.get("lineitems")).size());
		
		liTest = clientTest.resource(urlTestPrefix+"sampleJSON/LineItem2.json");
		JsonObject li2 = liTest.accept("application/json").get(JsonObject.class);
		
		addTest = client.resource(urlPrefix + "jaxrs/Customer/OpenOrder/LineItem");
		clientResponse = addTest.accept("application/json").contentType("application/json").post(li1.serialize());
		assertEquals(412, clientResponse.getStatusCode());
		
		addTest = client.resource(urlPrefix + "jaxrs/Customer/OpenOrder/LineItem");
		clientResponse = addTest.header("If-Match", version).accept("application/json").contentType("application/json").post(li2.serialize());
		
		assertEquals(200, clientResponse.getStatusCode());
		openOrder = clientResponse.getEntity(JsonObject.class);
		version = clientResponse.getHeaders().get("ETag").get(0);
		assertEquals(2, ((JsonArray)openOrder.get("lineitems")).size());
		
		liTest = clientTest.resource(urlTestPrefix+"sampleJSON/LineItem3.json");
		JsonObject li3 = liTest.accept("application/json").get(JsonObject.class);
		
		addTest = client.resource(urlPrefix + "jaxrs/Customer/OpenOrder/LineItem");
		clientResponse = addTest.header("If-Match", version).accept("application/json").contentType("application/json").post(li3.serialize());
		
		version = clientResponse.getHeaders().get("ETag").get(0);
		
		assertEquals(200, clientResponse.getStatusCode());
		openOrder = clientResponse.getEntity(JsonObject.class);
		assertEquals(2, ((JsonArray)openOrder.get("lineitems")).size());
		
		long newQuan = ((Long)li2.get("quantity")) +((Long)li3.get("quantity"))  ;
		
		JsonArray lis = (JsonArray)openOrder.get("lineitems");
		@SuppressWarnings("unchecked")
		ListIterator<JsonObject> liJSON = lis.listIterator();
		while(liJSON.hasNext())
		{
			JsonObject liCheck = liJSON.next();
			if(liCheck.get("productId") == li2.get("productId"))
			{
				assertEquals(newQuan, liCheck.get("quantity"));
				break;
			}
		}
		
		liTest = clientTest.resource(urlTestPrefix+"sampleJSON/LineItem4.json");
		JsonObject li4 = liTest.accept("application/json").get(JsonObject.class);
		
		addTest = client.resource(urlPrefix + "jaxrs/Customer/OpenOrder/LineItem");
		clientResponse = addTest.header("If-Match", version).accept("application/json").contentType("application/json").post(li4.serialize());
		
		version = clientResponse.getHeaders().get("ETag").get(0);
		
		assertEquals(200, clientResponse.getStatusCode());
		openOrder = clientResponse.getEntity(JsonObject.class);
		assertEquals(3, ((JsonArray)openOrder.get("lineitems")).size());
		
		
		Resource removeTest = client.resource(urlPrefix + "jaxrs/Customer/OpenOrder/LineItem/"+li4.get("productId"));
		clientResponse = removeTest.accept("application/json").delete();
		assertEquals(412, clientResponse.getStatusCode());
		
		clientResponse = removeTest.header("If-Match",version).accept("application/json").delete();
		assertEquals(200, clientResponse.getStatusCode());
		version = clientResponse.getHeaders().get("ETag").get(0);
		
		version = clientResponse.getHeaders().get("ETag").get(0);
		
		Resource submitTest = client.resource(urlPrefix + "jaxrs/Customer/OpenOrder");
		clientResponse = submitTest.post(null);
		assertEquals(412, clientResponse.getStatusCode());
		
		clientResponse = submitTest.header("If-Match",version).post(null);
		assertEquals(204, clientResponse.getStatusCode());
		
		customer = custOrder.accept("application/json").get(JsonObject.class);
		assertNull(customer.get("openOrder"));
		
	}
	
	public void testOrderHistory() throws IOException
	{
		RestClient client = new RestClient(clientConfig);
		Resource orderHistoryTest = client.resource(urlPrefix + "jaxrs/Customer/Orders");
		ClientResponse clientResponse = orderHistoryTest.accept("application/json").get();
		JsonArray orderHistory = clientResponse.getEntity(JsonArray.class);
		assertEquals(200, clientResponse.getStatusCode());
		int size = orderHistory.size();
		String lastModified = clientResponse.getHeaders().get("Last-Modified").get(0);
		
		
		clientResponse = orderHistoryTest.accept("application/json").header("If-Modified-Since", lastModified).get();
		assertEquals(304, clientResponse.getStatusCode());
		
		testOrderProcess();
		clientResponse = orderHistoryTest.accept("application/json").header("If-Modified-Since", lastModified).get();
		orderHistory = clientResponse.getEntity(JsonArray.class);
		int newSize = orderHistory.size();
		assertEquals(newSize,size+1);
		assertEquals(200, clientResponse.getStatusCode());
	}
	
	public void testFormMetaData ()
	{
		//Residential User
		RestClient client = new RestClient(clientConfig);
		Resource info = client.resource(urlPrefix + "jaxrs/Customer/TypeForm");
		ClientResponse clientResponse = info.accept("application/json").get();
		JsonObject formData = clientResponse.getEntity(JsonObject.class);
		assertEquals(formData.get("type"),"residential");
		assertEquals(formData.get("label"),"Residential Customer");
		JsonArray groups = (JsonArray)formData.get("formData");
		for (int i = 0; i < groups.size();i++)
		{
			JsonObject item = (JsonObject)groups.get(i);
			if(item.get("name").equals("frequentCustomer"))
			{
				assertEquals(item.get("name"),"frequentCustomer");
				assertEquals(item.get("label"),"Frequent Customer");
				assertEquals(item.get("type"),"string");
				assertEquals(item.get("readonly"),"true");
			}
			else if(item.get("name").equals("householdSize"))
			{
				assertEquals(item.get("name"),"householdSize");
				assertEquals(item.get("label"),"Household Size");
				assertEquals(item.get("type"),"number");
				assertEquals(item.get("required"),"true");
				assertEquals(item.get("constraints"),"{min:1,max:10,places:0}");
			}
		}
		
		
		//Business User
		RestClient client2 = new RestClient(clientConfig2);
		Resource info2 = client2.resource(urlPrefix + "jaxrs/Customer/TypeForm");
		ClientResponse clientResponse2 = info2.accept("application/json").get();
		formData = clientResponse2.getEntity(JsonObject.class);
		assertEquals(formData.get("type"),"business");
		assertEquals(formData.get("label"),"Business Customer");
		groups = (JsonArray)formData.get("formData");
		for (int i = 0; i < groups.size();i++)
		{
			JsonObject item = (JsonObject)groups.get(i);
			if(item.get("name").equals("description"))
			{
				assertEquals(item.get("name"),"description");
				assertEquals(item.get("label"),"Description");
				assertEquals(item.get("type"),"text");
			}
			else if(item.get("name").equals("businessPartner"))
			{
				assertEquals(item.get("name"),"businessPartner");
				assertEquals(item.get("label"),"Business Partner");
				assertEquals(item.get("type"),"string");
				assertEquals(item.get("readonly"),"true");
			}
			else if(item.get("name").equals("volumeDiscount"))
			{
				assertEquals(item.get("name"),"volumeDiscount");
				assertEquals(item.get("label"),"Volume Discount");
				assertEquals(item.get("type"),"string");
				assertEquals(item.get("readonly"),"true");
			}
		}
	}
	
	public void testUpdateInfo() throws IOException
	{
		//Residential User
		RestClient client = new RestClient(clientConfig);
		long householdSize = 3;
		JsonObject data = new JsonObject();
		data.put("type", "RESIDENTIAL");
		data.put("householdSize",householdSize);
		Resource customerInfo = client.resource(urlPrefix + "jaxrs/Customer/Info");
		ClientResponse clientResponse = customerInfo.contentType(MediaType.APPLICATION_JSON).post(data.serialize());
		assertEquals(204, clientResponse.getStatusCode());
		Resource resource = client.resource(urlPrefix + "jaxrs/Customer");
		JsonObject customer = resource.accept("application/json").get(JsonObject.class);
		assertEquals(customer.get("householdSize"),data.get("householdSize"));
		data.put("householdSize",6);
		clientResponse = customerInfo.contentType(MediaType.APPLICATION_JSON).post(data.serialize());
		assertEquals(204, clientResponse.getStatusCode());
		
		//Business User
		RestClient client2 = new RestClient(clientConfig2);
		String desc = "High Tech Partner";
		data = new JsonObject();
		data.put("type", "BUSINESS");
		data.put("description", desc);
		customerInfo = client2.resource(urlPrefix + "jaxrs/Customer/Info");
		clientResponse = customerInfo.contentType(MediaType.APPLICATION_JSON).post(data.serialize());
		assertEquals(204, clientResponse.getStatusCode());
		resource = client2.resource(urlPrefix + "jaxrs/Customer");
		customer = resource.accept("application/json").get(JsonObject.class);
		assertEquals(customer.get("description"),desc);
	}
	

}
