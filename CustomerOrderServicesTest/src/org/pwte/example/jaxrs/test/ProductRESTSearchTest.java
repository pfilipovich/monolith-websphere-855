package org.pwte.example.jaxrs.test;


import org.apache.wink.client.Resource;
import org.apache.wink.client.RestClient;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;

import junit.framework.TestCase;

public class ProductRESTSearchTest extends TestCase {

	private String urlPrefix;

	public void setUp() throws Exception {
		// Use default URL for Jakarta EE environment
		urlPrefix = "https://localhost:9443/CustomerOrderServicesWeb/";
	}

	public void testProductResources() {
		RestClient client = new RestClient();
		Resource resource = client.resource(urlPrefix + "jaxrs/Product/1");

		JsonObject product = resource.accept("application/json").get(JsonObject.class);
		assertEquals("Return of the Jedi", product.get("name"));
		assertEquals(29.99, product.get("price"));
		assertEquals(1L, product.getJsonNumber("id").longValue());
		assertEquals("images/Return.jpg", product.get("image"));
		assertEquals("Episode 6, Luke has the final confrontation with his father!", product.get("description"));
	}

	public void testProductListByCategory() {
		RestClient client = new RestClient();
		Resource resource = client.resource(urlPrefix + "jaxrs/Product?categoryId=1");

		JsonArray productList = resource.accept("application/json").get(JsonArray.class);
		for (int i = 0; i < productList.size(); i++) {
			JsonObject product = (JsonObject) productList.get(i);

			switch (product.getJsonNumber("id").intValue()) {
			case 1: {
				assertEquals("Return of the Jedi", product.get("name"));
				assertEquals(29.99, product.get("price"));
				assertEquals(1L, product.getJsonNumber("id").longValue());
				assertEquals("images/Return.jpg", product.get("image"));
				assertEquals("Episode 6, Luke has the final confrontation with his father!",
						product.get("description"));
				break;
			}
			case 2: {
				assertEquals("Empire Strikes Back", product.get("name"));
				assertEquals(29.99, product.get("price"));
				assertEquals(2L, product.getJsonNumber("id").longValue());
				assertEquals("images/Empire.jpg", product.get("image"));
				assertEquals("Episode 5, Luke finds out a secret that will change his destiny",
						product.get("description"));
				break;
			}
			case 3: {
				assertEquals("New Hope", product.get("name"));
				assertEquals(29.99, product.get("price"));
				assertEquals(3L, product.getJsonNumber("id").longValue());
				assertEquals("images/NewHope.jpg", product.get("image"));
				assertEquals("Episode 4, after years of oppression, a band of rebels fight for freedom",
						product.get("description"));
				break;
			}
			default: {
				// fail("Invalid object");
			}
			}
		}
	}

	public void testCategoryResource() {
		RestClient client = new RestClient();
		Resource resource = client.resource(urlPrefix + "jaxrs/Category/1");

		JsonObject category = resource.accept("application/json").get(JsonObject.class);
		assertEquals("Entertainment", category.get("name"));
		assertEquals(1L, category.getJsonNumber("id").longValue());
		JsonArray subCategories = (JsonArray) category.get("subCategories");
		for (int i = 0; i < subCategories.size(); i++) {
			JsonObject subCategory = (JsonObject) subCategories.get(i);
			switch (subCategory.getJsonNumber("id").intValue()) {
			case 2: {
				assertEquals(2L, subCategory.getJsonNumber("id").longValue());
				assertEquals("Movies", subCategory.get("name"));
				break;
			}
			case 3: {
				assertEquals(3L, subCategory.getJsonNumber("id").longValue());
				assertEquals("Music", subCategory.get("name"));
				break;
			}
			case 4: {
				assertEquals(4L, subCategory.getJsonNumber("id").longValue());
				assertEquals("Games", subCategory.get("name"));
				break;
			}
			default: {
				fail("Invalid Subcategory");
			}
			}
		}
	}

	public void testCategoryListResources() {
		RestClient client = new RestClient();
		Resource resource = client.resource(urlPrefix + "jaxrs/Category");

		JsonArray categories = resource.accept("application/json").get(JsonArray.class);
		for (int k = 0; k < categories.size(); k++) {
			JsonObject category = (JsonObject) categories.get(k);
			switch (category.getJsonNumber("id").intValue()) {
			case 1: {
				assertEquals("Entertainment", category.get("name"));
				assertEquals(1L, category.getJsonNumber("id").longValue());
				JsonArray subCategories = (JsonArray) category.get("subCategories");
				for (int i = 0; i < subCategories.size(); i++) {
					JsonObject subCategory = (JsonObject) subCategories.get(i);
					switch (subCategory.getJsonNumber("id").intValue()) {
					case 2: {
						assertEquals(2L, subCategory.getJsonNumber("id").longValue());
						assertEquals("Movies", subCategory.get("name"));
						break;
					}
					case 3: {
						assertEquals(3L, subCategory.getJsonNumber("id").longValue());
						assertEquals("Music", subCategory.get("name"));
						break;
					}
					case 4: {
						assertEquals(4L, subCategory.getJsonNumber("id").longValue());
						assertEquals("Games", subCategory.get("name"));
						break;
					}
					default: {
						fail("Invalid Subcategory");
					}
					}
				}
				break;
			}
			case 10: {
				assertEquals("Electronics", category.get("name"));
				assertEquals(10L, category.getJsonNumber("id").longValue());
				JsonArray subCategories = (JsonArray) category.get("subCategories");
				for (int i = 0; i < subCategories.size(); i++) {
					JsonObject subCategory = (JsonObject) subCategories.get(i);
					switch (subCategory.getJsonNumber("id").intValue()) {
					case 12: {
						assertEquals(12L, subCategory.getJsonNumber("id").longValue());
						assertEquals("TV", subCategory.get("name"));
						break;
					}
					case 13: {
						assertEquals(13L, subCategory.getJsonNumber("id").longValue());
						assertEquals("Cellphones", subCategory.get("name"));
						break;
					}
					case 14: {
						assertEquals(14L, subCategory.getJsonNumber("id").longValue());
						assertEquals("DVD Players", subCategory.get("name"));
						break;
					}
					default: {
						fail("Invalid Subcategory");
					}
					}
				}
				break;
			}
			default:
				fail("Invalid Category");

			}
		}
	}

}
