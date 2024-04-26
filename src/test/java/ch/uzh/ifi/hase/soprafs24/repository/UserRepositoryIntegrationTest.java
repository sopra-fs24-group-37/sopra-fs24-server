package ch.uzh.ifi.hase.soprafs24.repository;

import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class UserRepositoryIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void findByUsername_success() {
        // given
        User user = new User();
        user.setUsername("usernameById");
        user.setPassword("password");
        user.setStatus(UserStatus.ONLINE);
        user.setGamesPlayed(0);
        user.setGamesWon(0);
        user.setTotalScores(0);
        user.setToken("token");

        entityManager.persist(user);
        entityManager.flush();

        // when
        User found = userRepository.findByUsername(user.getUsername());

        // then
        assertNotNull(found);
        assertEquals(user.getUsername(), found.getUsername());
        assertEquals(user.getToken(), found.getToken());
        assertEquals(user.getStatus(), found.getStatus());
    }

    @Test
    public void findByUsername_notFound() {
        // when
        User found = userRepository.findByUsername("nonexistent");

        // then
        assertNull(found);
    }

    @Test
    public void findById_success() {
        // given
        User user = new User();
        user.setUsername("usernameById");
        user.setPassword("password");
        user.setStatus(UserStatus.ONLINE);
        user.setGamesPlayed(0);
        user.setGamesWon(0);
        user.setTotalScores(0);
        user.setToken("token");


        entityManager.persist(user);
        entityManager.flush();

        // when
        Optional<User> found = userRepository.findById(user.getUserId());

        // then
        assertTrue(found.isPresent());
        assertEquals(user.getUsername(), found.get().getUsername());
        assertEquals(UserStatus.ONLINE, found.get().getStatus());
    }

    @Test
    public void findById_notFound() {
        // when
        Optional<User> found = userRepository.findById(999L); // Assuming 999L is a non-existent ID

        // then
        assertFalse(found.isPresent());
    }
}
