package com.vagsoft.bookstore.repositories;

import java.util.Optional;

import com.vagsoft.bookstore.models.entities.User;
import com.vagsoft.bookstore.models.enums.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/** Repository interface for accessing user data */
@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    /**
     * Retrieves a list of users filtered by the specified parameters
     *
     * @param username
     *            the username of the users to search for (optional)
     * @param email
     *            the email of the users to search for (optional)
     * @param role
     *            the role of the users to search for (optional)
     * @param firstName
     *            the first name of the users to search for (optional)
     * @param lastName
     *            the last name of the users to search for (optional)
     * @param pageable
     *            the pagination information (optional)
     * @return a page of users
     */
    @Query("""
            SELECT u
            FROM User u
            WHERE (:username IS NULL OR u.username ILIKE %:username%)
            AND (:email IS NULL OR u.email ILIKE %:email%)
            AND (:role IS NULL OR u.role = :role)
            AND (:firstName IS NULL OR u.firstName ILIKE %:firstName%)
            AND (:lastName IS NULL OR u.lastName ILIKE %:lastName%)
            """)
    public Page<User> findUsers(String username, String email, Role role, String firstName, String lastName,
            Pageable pageable);

    /**
     * Deletes a user by its ID
     *
     * @param userID
     *            the ID of the user to be deleted
     * @return the number of users deleted (should be 1)
     */
    public Long deleteById(Long userID);

    /**
     * Finds a user by its username
     *
     * @param username
     *            the username of the user to be found
     * @return the user with the specified username
     */
    public Optional<User> findByUsername(String username);

    /**
     * Checks if a user with the given email exists in the database
     *
     * @param email
     *            the email of the user to be checked
     * @return true if the user exists, false otherwise
     */
    public boolean existsByEmail(String email);

    /**
     * Checks if a user with the given email exists in the database, excluding the
     * user with the specified ID
     *
     * @param email
     *            the email of the user to be checked
     * @return true if the user exists, false otherwise
     */
    public boolean existsByEmailAndIdNot(String email, Integer userID);

    /**
     * Checks if a user with the given username exists in the database
     *
     * @param username
     *            the username of the user to be checked
     * @return true if the user exists, false otherwise
     */
    public boolean existsByUsername(String username);

    /**
     * Checks if a user with the given username exists in the database, excluding
     * the user with the specified ID
     *
     * @param username
     *            the username of the user to be checked
     * @return true if the user exists, false otherwise
     */
    public boolean existsByUsernameAndIdNot(String username, Integer userID);
}
