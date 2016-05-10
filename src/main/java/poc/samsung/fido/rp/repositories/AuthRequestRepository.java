/**
 * 
 */
package poc.samsung.fido.rp.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import poc.samsung.fido.rp.domain.AuthRequest;

/**
 * @author user
 *
 */
public interface AuthRequestRepository extends JpaRepository<AuthRequest, Long> {

}
