/**
 * 
 */
package poc.samsung.fido.rp.contoller;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.base.Optional;
import com.google.common.base.Stopwatch;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import poc.samsung.fido.rp.contoller.message.RPResponseMsg;
import poc.samsung.fido.rp.domain.Account;
import poc.samsung.fido.rp.domain.AuthRequest;
import poc.samsung.fido.rp.domain.Customer;
import poc.samsung.fido.rp.domain.type.AuthRequetStatus;
import poc.samsung.fido.rp.repositories.AuthRequestRepository;
import poc.samsung.fido.rp.repositories.CustomerRepository;

/**
 * @author user
 *
 */
@RestController
public class AuthenticationController {
	
	private static final Logger LOGGER = LoggerFactory
			.getLogger(AuthenticationController.class);

	private static SimpleDateFormat SDF = new SimpleDateFormat("yyyyMMddHHmmss");

	@Autowired
	private CustomerRepository customerRepository;
	
	@Autowired
	private AuthRequestRepository authRequestRepository;

	@RequestMapping(value = "/{rpId}/uaf/login", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public RPResponseMsg<Void> verifyLogin(@PathVariable String rpId,
			@RequestBody String credentials) {
		LOGGER.info("Service 'verifyLogin' called for rpId = {} with credentials = {}", rpId, credentials);
		Stopwatch watch = Stopwatch.createStarted();
		Date invokedAt = Calendar.getInstance().getTime();
		try {
			JsonObject credJson = new JsonParser().parse(credentials).getAsJsonObject();
			String userId = credJson.get("userId").toString().trim();
		    String password = credJson.get("password").toString().trim();
			LOGGER.info("Invoked by = {} with password = {}", userId, password);
			Customer customer = customerRepository.findOne(userId);
			if (customer == null || !password.equals(customer.getPassword())) {
				return createMsg("verifyLogin", "FAILURE", invokedAt, watch.elapsed(TimeUnit.MILLISECONDS),
						Optional.of("Username/password does not match"), Optional.absent());
			} else {
				return createMsg("verifyLogin", "SUCCESS", invokedAt, watch.elapsed(TimeUnit.MILLISECONDS),
						Optional.absent(), Optional.absent());
			}
		} catch (Exception ex) {
			return createMsg("verifyLogin", "FAILURE", invokedAt, watch.elapsed(TimeUnit.MILLISECONDS),
					Optional.of(ex.getMessage()), Optional.absent());
		} finally {
			watch.stop();
		}
	}

	@RequestMapping(value = "/{rpId}/uaf/requestAuthentication", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public RPResponseMsg<Long> requestAuthentication(@PathVariable String rpId, @RequestBody AuthRequest authRequest) {
		LOGGER.info("Service 'requestAuthentication' called for rpId = {} with request - {}", rpId, authRequest.toString());
		Stopwatch watch = Stopwatch.createStarted();
		Date invokedAt = Calendar.getInstance().getTime();
		try {
			if (customerRepository.exists(authRequest.getUserId())) {
				authRequestRepository.save(authRequest);
				return createMsg("requestAuthentication", "SUCCESS", invokedAt, watch.elapsed(TimeUnit.MILLISECONDS),
						Optional.absent(), Optional.of(authRequest.getAuthId()));
			} else {
				return createMsg("requestAuthentication", "FAILURE", invokedAt, watch.elapsed(TimeUnit.MILLISECONDS),
						Optional.of("User '" + authRequest.getUserId() + "' is not registered"), Optional.of(0l));
			}
		} catch (Exception ex) {
			return createMsg("requestAuthentication", "FAILURE", invokedAt, watch.elapsed(TimeUnit.MILLISECONDS),
					Optional.of(ex.getMessage()), Optional.of(0l));
		} finally {
			watch.stop();
		}
	}
	
	@RequestMapping(value = "/{rpId}/uaf/updateAuthenticationStatus/{user}/{reqId}", method = RequestMethod.PUT, produces = "application/json")
	@ResponseBody
	public RPResponseMsg<Void> updateAuthorizationStatus(@PathVariable String rpId, @PathVariable String user,@PathVariable long reqId,
			@RequestParam(name = "newStatus", required = true) String status) {
		LOGGER.info("Service 'updateAuthenticationStatus' called for rpId = {} with user = {}, request id - {} and status - {}" , rpId, user, reqId, status);
		Stopwatch watch = Stopwatch.createStarted();
		Date invokedAt = Calendar.getInstance().getTime();
		try {
			if (authRequestRepository.exists(reqId)) {
				AuthRequest req = authRequestRepository.findOne(reqId);
				if (user.equals(req.getUserId())) {
					if (status.equals(req.getReqStatus())) {
						return createMsg("updateAuthenticationStatus", "FAILURE", invokedAt, watch.elapsed(TimeUnit.MILLISECONDS),
								Optional.of("Request is in the same status; hence not updated"), Optional.absent());
					} else {
						req.setReqStatus(AuthRequetStatus.valueOf(status));
						authRequestRepository.save(req);
						return createMsg("updateAuthenticationStatus", "SUCCESS", invokedAt, watch.elapsed(TimeUnit.MILLISECONDS),
								Optional.absent(), Optional.absent());
					}
				} else {
					return createMsg("updateAuthenticationStatus", "FAILURE", invokedAt, watch.elapsed(TimeUnit.MILLISECONDS),
							Optional.of("Request ID does not belong to the given user"), Optional.absent());
				}
			} else {
				return createMsg("updateAuthenticationStatus", "FAILURE", invokedAt, watch.elapsed(TimeUnit.MILLISECONDS),
						Optional.of("Request ID does not exist"), Optional.absent());
			}
		} catch (Exception ex) {
			return createMsg("updateAuthenticationStatus", "FAILURE", invokedAt, watch.elapsed(TimeUnit.MILLISECONDS),
					Optional.of(ex.getMessage()), Optional.absent());
		} finally {
			watch.stop();
		}
	}
	
	@RequestMapping(value = "/{rpId}/uaf/getAuthenticationStatus/{user}/{reqId}", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public RPResponseMsg<AuthRequetStatus> checkAuthenticationStatus(@PathVariable String rpId, @PathVariable String user,@PathVariable long reqId) {
		LOGGER.info("Service 'getAuthenticationStatus' called for rpId = {} with user = {} and request id - {}" , rpId, user, reqId);
		Stopwatch watch = Stopwatch.createStarted();
		Date invokedAt = Calendar.getInstance().getTime();
		try {
			if (authRequestRepository.exists(reqId)) {
				AuthRequest req = authRequestRepository.findOne(reqId);
				if (user.equals(req.getUserId())) {
						return createMsg("getAuthenticationStatus", "SUCCESS", invokedAt, watch.elapsed(TimeUnit.MILLISECONDS),
								Optional.absent(), Optional.of(req.getReqStatus()));
				} else {
					return createMsg("getAuthenticationStatus", "FAILURE", invokedAt, watch.elapsed(TimeUnit.MILLISECONDS),
							Optional.of("Request ID does not belong to the given user"), Optional.absent());
				}
			} else {
				return createMsg("getAAuthenticationStatus", "FAILURE", invokedAt, watch.elapsed(TimeUnit.MILLISECONDS),
						Optional.of("Request ID does not exist"), Optional.absent());
			}
		} catch (Exception ex) {
			return createMsg("getAuthenticationStatus", "FAILURE", invokedAt, watch.elapsed(TimeUnit.MILLISECONDS),
					Optional.of(ex.getMessage()), Optional.absent());
		} finally {
			watch.stop();
		}
	}
	
	@RequestMapping(value = "/{rpId}/uaf/saveRecipientDetails/{user}", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public RPResponseMsg<Long> saveRecipientDetails(@PathVariable String rpId, @PathVariable String user, @RequestBody Account account) {
		LOGGER.info("Service 'saveRecipientDetails' called for rpId = {} with user = {} and recipient - {}" , rpId, user, account.toString());
		Stopwatch watch = Stopwatch.createStarted();
		Date invokedAt = Calendar.getInstance().getTime();
		try {
			if (customerRepository.exists(user)) {
				return createMsg("saveRecipientDetails", "SUCCESS", invokedAt, watch.elapsed(TimeUnit.MILLISECONDS),
						Optional.absent(), Optional.of(account.getAccNo()));
			} else {
				return createMsg("saveRecipientDetails", "FAILURE", invokedAt, watch.elapsed(TimeUnit.MILLISECONDS),
						Optional.of("User '" + user + "' is not registered"), Optional.of(0l));
			}
		} catch (Exception ex) {
			return createMsg("saveRecipientDetails", "FAILURE", invokedAt, watch.elapsed(TimeUnit.MILLISECONDS),
					Optional.of(ex.getMessage()), Optional.of(0l));
		} finally {
			watch.stop();
		}
	}

	/**
	 * 
	 * @param status
	 * @param invokedAt
	 * @param elapsedTime
	 * @param errorMsg
	 * @return
	 */
	private <T> RPResponseMsg<T> createMsg(String operation, String status, Date invokedAt, long elapsedTime,
			Optional<String> errorMsg, Optional<T> output) {
		return new RPResponseMsg<T>(status, operation, SDF.format(invokedAt), elapsedTime, errorMsg, output);
	}
}
