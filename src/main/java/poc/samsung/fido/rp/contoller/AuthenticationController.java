/**
 * 
 */
package poc.samsung.fido.rp.contoller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import poc.samsung.fido.rp.domain.Customer;
import poc.samsung.fido.rp.repositories.CustomerRepository;

/**
 * @author user
 *
 */
@RestController
public class AuthenticationController {
	
	@Autowired
    ConfigurableApplicationContext context;
	
	
	private CustomerRepository customerRepository;
    
	@RequestMapping(value = "/rp/login")
	public Customer verifyLogin(@RequestParam(name = "userId", required = true) String userId, @RequestParam(name = "pwd", required = true)String password) {
		customerRepository = context.getBean(CustomerRepository.class);
		Customer customer = customerRepository.findOne(userId);
		System.out.println("Count == " + customerRepository.count());
		if (customer == null || !password.equals(customer.getPassword())) {
			throw new RuntimeException("User ID '" + userId + "' is not authorized");
		}
		return customer;
	}
}
