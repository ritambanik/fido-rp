/**
 * 
 */
package poc.samsung.fido.rp.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import poc.samsung.fido.rp.domain.Customer;

/**
 * @author user
 *
 */
public interface CustomerRepository extends JpaRepository<Customer, String> {

}
