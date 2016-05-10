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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.base.Optional;
import com.google.common.base.Stopwatch;

import poc.samsung.fido.rp.contoller.message.RPResponseMsg;
import poc.samsung.fido.rp.domain.Customer;
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

	@RequestMapping(value = "/login/{user}", method = RequestMethod.GET, produces = "application/json")
	public RPResponseMsg<Void> verifyLogin(@PathVariable String user,
			@RequestParam(name = "pwd", required = true) String password) {
		Stopwatch watch = Stopwatch.createStarted();
		Date invokedAt = Calendar.getInstance().getTime();
		try {
			Customer customer = customerRepository.findOne(user);
			if (customer == null || !password.equals(customer.getPassword())) {
				return createMsg("FAILURE", invokedAt, watch.elapsed(TimeUnit.MILLISECONDS),
						Optional.of("Username/password does not match"));
			} else {
				return createMsg("SUCCESS", invokedAt, watch.elapsed(TimeUnit.MILLISECONDS), Optional.absent());
			}
		} catch (Exception ex) {
			return createMsg("FAILURE", invokedAt, watch.elapsed(TimeUnit.MILLISECONDS), Optional.of(ex.getMessage()));
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
	private RPResponseMsg<Void> createMsg(String status, Date invokedAt, long elapsedTime, Optional<String> errorMsg) {
		return new RPResponseMsg<Void>(status, "verifyLogin", SDF.format(invokedAt), elapsedTime, errorMsg,
				Optional.absent());
	}
}
