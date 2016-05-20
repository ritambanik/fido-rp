/**
 * 
 */
package poc.samsung.fido.rp.contoller;

import static org.junit.Assert.assertNotNull;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import poc.samsung.fido.rp.FidoRpApplication;

/**
 * @author user
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = FidoRpApplication.class)
@WebIntegrationTest
public class UafIntegrationControllerTest {

	// Required to Generate JSON content from Java objects
	public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

	// Test RestTemplate to invoke the APIs.
	private RestTemplate restTemplate = new TestRestTemplate();

	@SuppressWarnings("unchecked")
	@Test
	public void testUafRegistrationRequest() {
		//String rpAppReqMsg = "{ \"context\":\"{\"rpContext\":\"{\\\"deviceId\\\":\\\"d1ce4ce325dbc391\\\",\\ \"registrationData\\\":\\\"fidoTest01\\\",\\\"userName\\\":\\\"uquiet7167 \\\"}\"}\",\"op\":\"Reg\"}";
		String rpAppReqMsg1 = "{ \"context\": {\"rpContext\":{\"deviceId\":\"d1ce4ce325dbc391\",\"registrationData\":\"fidoTest01\",\"userName\":\"uquiet7167\"}},\"op\":\"Reg\"}";
		/**
		JsonObject rpCtx = new JsonObject();
		rpCtx.addProperty("deviceId", "d1ce4ce325dbc391");
		rpCtx.addProperty("registrationData", "fidoTest01");
		rpCtx.addProperty("userName", "uquiet7167");
		JsonObject op = new JsonObject();
		op.addProperty("op", "Reg");
		JsonObject ctx = new JsonObject();
		JsonArray ctxArr = new JsonArray();
		ctxArr.add(rpCtx);
		ctxArr.add(op);
		ctx.add("context", ctxArr);
		**/
		Map<String, Object> requestBody = new HashMap<String, Object>();
		requestBody.put("requestBody", rpAppReqMsg1);
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set("Content-Type", "application/fido+uaf; charset=utf-8");
		requestHeaders.set("Accept", "application/fido+uaf");
		requestHeaders.set("User-Agent", "O2RPC/1.0");
		// Creating http entity object with request body and headers
		try {
			HttpEntity<String> httpEntity = new HttpEntity<String>(OBJECT_MAPPER.writeValueAsString(requestBody),
					requestHeaders);
			// Invoking the API
			String apiResponse = restTemplate.postForObject(
					"http://localhost:8080/rp1/uaf/request", httpEntity, String.class,
					Collections.EMPTY_MAP);
			System.out.println("Output = " + apiResponse);
			assertNotNull(apiResponse);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}

	}

}
