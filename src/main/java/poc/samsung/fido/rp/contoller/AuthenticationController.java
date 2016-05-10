/**
 * 
 */
package poc.samsung.fido.rp.contoller;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

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

import poc.samsung.fido.rp.contoller.message.RPResponseMsg;
import poc.samsung.fido.rp.domain.AuthRequest;
import poc.samsung.fido.rp.domain.Customer;
import poc.samsung.fido.rp.repositories.AuthRequestRepository;
import poc.samsung.fido.rp.repositories.CustomerRepository;

/**
 * @author user
 *
 */
@RestController
@RequestMapping(value = "/fido-rp")
public class AuthenticationController {

	private static SimpleDateFormat SDF = new SimpleDateFormat("yyyyMMddHHmmss");

	@Autowired
	private CustomerRepository customerRepository;
	
	@Autowired
	private AuthRequestRepository authRequestRepository;

	@RequestMapping(value = "/login/{user}", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public RPResponseMsg<Void> verifyLogin(@PathVariable String user,
			@RequestParam(name = "pwd", required = true) String password) {
		Stopwatch watch = Stopwatch.createStarted();
		Date invokedAt = Calendar.getInstance().getTime();
		try {
			Customer customer = customerRepository.findOne(user);
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

	@RequestMapping(value = "/requestAuthentication/{user}", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public RPResponseMsg<Long> requestAuthentication(@PathVariable String user, @RequestBody AuthRequest authRequest) {
		Stopwatch watch = Stopwatch.createStarted();
		Date invokedAt = Calendar.getInstance().getTime();
		try {
			if (customerRepository.exists(user)) {
				authRequestRepository.save(authRequest);
				return createMsg("requestAuthentication", "SUCCESS", invokedAt, watch.elapsed(TimeUnit.MILLISECONDS),
						Optional.absent(), Optional.of(authRequest.getAuthId()));
			} else {
				return createMsg("requestAuthentication", "FAILURE", invokedAt, watch.elapsed(TimeUnit.MILLISECONDS),
						Optional.of("User '" + user + "' is not registered"), Optional.of(0l));
			}
		} catch (Exception ex) {
			return createMsg("requestAuthentication", "FAILURE", invokedAt, watch.elapsed(TimeUnit.MILLISECONDS),
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
