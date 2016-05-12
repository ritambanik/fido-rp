/**
 * 
 */
package poc.samsung.fido.rp.contoller;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.text.IsEmptyString.isEmptyString;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import poc.samsung.fido.rp.FidoRpApplication;
import poc.samsung.fido.rp.domain.AuthRequest;
import poc.samsung.fido.rp.domain.type.AuthRequetStatus;
import poc.samsung.fido.rp.repositories.AuthRequestRepository;

/**
 * @author user
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = FidoRpApplication.class)
@WebIntegrationTest
public class AuthenticationControllerTest {

	// Required to Generate JSON content from Java objects
	public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

	// Test RestTemplate to invoke the APIs.
	private RestTemplate restTemplate = new TestRestTemplate();

	@Autowired
	private AuthRequestRepository authRepository;

	/**
	 * Test method for
	 * {@link poc.samsung.fido.rp.contoller.AuthenticationController#verifyLogin(java.lang.String, java.lang.String)}
	 * .
	 */
	@SuppressWarnings("unchecked")
	@Test
	public final void testVerifyLogin() {
		// Invoking the API
		Map<String, Object> apiResponse = restTemplate.getForObject(
				"http://localhost:8080/fido-rp/login/ritam?pwd=welcome", Map.class, Collections.EMPTY_MAP);
		System.out.println("Output = " + apiResponse.toString());
		assertNotNull(apiResponse);
		assertThat(apiResponse.get("status"), equalTo("SUCCESS"));
		assertThat(String.valueOf(apiResponse.get("errorMsg")), isEmptyString());
	}

	/**
	 * Test method for
	 * {@link poc.samsung.fido.rp.contoller.AuthenticationController#requestAuthentication(java.lang.String, poc.samsung.fido.rp.domain.AuthRequest)}
	 * .
	 */
	@SuppressWarnings("unchecked")
	@Test
	public final void testRequestAuthentication() {
		// Building the Request body data
		Map<String, Object> requestBody = new HashMap<String, Object>();
		requestBody.put("userId", "ritam");
		requestBody.put("reqTime", Calendar.getInstance().getTime());
		requestBody.put("reqType", "New");
		requestBody.put("reqStatus", AuthRequetStatus.PENDING);
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.setContentType(MediaType.APPLICATION_JSON);

		// Creating http entity object with request body and headers
		try {
			HttpEntity<String> httpEntity = new HttpEntity<String>(OBJECT_MAPPER.writeValueAsString(requestBody),
					requestHeaders);
			// Invoking the API
			Map<String, Object> apiResponse = restTemplate.postForObject(
					"http://localhost:8080/fido-rp/requestAuthentication/ritam", httpEntity, Map.class,
					Collections.EMPTY_MAP);
			System.out.println("Output = " + apiResponse.toString());
			assertNotNull(apiResponse);
			assertThat(apiResponse.get("status"), equalTo("SUCCESS"));
			assertNotEquals(0l, apiResponse.get("output"));
			assertThat(String.valueOf(apiResponse.get("errorMsg")), isEmptyString());
			authRepository.delete( Long.valueOf(String.valueOf((apiResponse.get("output")))));
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Test method for
	 * {@link poc.samsung.fido.rp.contoller.AuthenticationController#requestAuthentication(java.lang.String, poc.samsung.fido.rp.domain.AuthRequest)}
	 * .
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public final void testUpdateAuthenticationStatus() {
		AuthRequest req = new AuthRequest();
		req.setUserId("ritam");
		req.setReqType("New");
		req.setReqTime(Calendar.getInstance().getTime());
		req.setReqStatus(AuthRequetStatus.PENDING);
		authRepository.save(req);

		Map<String, Object> requestBody = new HashMap<String, Object>();
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.setContentType(MediaType.APPLICATION_JSON);
		// Creating http entity object with request body and headers
		try {
			HttpEntity<String> httpEntity = new HttpEntity<String>(OBJECT_MAPPER.writeValueAsString(requestBody),
					requestHeaders);

			// Invoking the API
			Map<String, Object> apiResponse = (Map) restTemplate
					.exchange("http://localhost:8080/fido-rp/updateAuthenticationStatus/ritam/" + req.getAuthId()
							+ "?newStatus=COMPLETE", HttpMethod.PUT, httpEntity, Map.class)
					.getBody();
			assertNotNull(apiResponse);
			assertThat(apiResponse.get("status"), equalTo("SUCCESS"));
			authRepository.delete(req.getAuthId());
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Test method for
	 * {@link poc.samsung.fido.rp.contoller.AuthenticationController#requestAuthentication(java.lang.String, poc.samsung.fido.rp.domain.AuthRequest)}
	 * .
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public final void testCheckAuthenticationStatus() {
		AuthRequest req = new AuthRequest();
		req.setUserId("ritam");
		req.setReqType("New");
		req.setReqTime(Calendar.getInstance().getTime());
		req.setReqStatus(AuthRequetStatus.PENDING);
		authRepository.save(req);

		// Invoking the API
		Map<String, Object> apiResponse = (Map) restTemplate.getForObject(
				"http://localhost:8080/fido-rp/getAuthenticationStatus/ritam/" + req.getAuthId(), Map.class);
		assertNotNull(apiResponse);
		assertThat(apiResponse.get("status"), equalTo("SUCCESS"));
		assertThat(AuthRequetStatus.PENDING,
				equalTo(AuthRequetStatus.valueOf(String.valueOf(apiResponse.get("output")))));
		authRepository.delete(req.getAuthId());
	}
	
	/**
	 * Test method for
	 * {@link poc.samsung.fido.rp.contoller.AuthenticationController#requestAuthentication(java.lang.String, poc.samsung.fido.rp.domain.AuthRequest)}
	 * .
	 */
	@SuppressWarnings("unchecked")
	@Test
	public final void testSaveRecipientDetails() {
		// Building the Request body data
		Map<String, Object> requestBody = new HashMap<String, Object>();
		requestBody.put("accFirstName", "Frank");
		requestBody.put("accLastName", "Underwood");
		requestBody.put("balance", 100.00);
		requestBody.put("accContactNo", "9178030435");
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.setContentType(MediaType.APPLICATION_JSON);

		// Creating http entity object with request body and headers
		try {
			HttpEntity<String> httpEntity = new HttpEntity<String>(OBJECT_MAPPER.writeValueAsString(requestBody),
					requestHeaders);
			// Invoking the API
			Map<String, Object> apiResponse = restTemplate.postForObject(
					"http://localhost:8080/fido-rp/saveRecipientDetails/ritam", httpEntity, Map.class,
					Collections.EMPTY_MAP);
			assertNotNull(apiResponse);
			assertThat(apiResponse.get("status"), equalTo("SUCCESS"));
			System.out.println("Account created with account number - " + apiResponse.get("output"));
			assertNotEquals(0.00d, apiResponse.get("output"));
			assertThat(String.valueOf(apiResponse.get("errorMsg")), isEmptyString());
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
	}
	
}
