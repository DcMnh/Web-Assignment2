/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cst8218.jeffin.slider.entity;

import jakarta.enterprise.inject.Instance;
import jakarta.enterprise.inject.spi.CDI;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.security.enterprise.identitystore.DatabaseIdentityStoreDefinition;
import jakarta.security.enterprise.identitystore.PasswordHash;
import jakarta.security.enterprise.identitystore.Pbkdf2PasswordHash;
import java.io.Serializable;
import java.util.HashMap;

/**
 * Entity class representing an application user.
 * This class is used for managing user credentials and group information in the database.
 * It also integrates with security mechanisms for password hashing and identity management.
 * 
 * @author User
 */
@DatabaseIdentityStoreDefinition(
    dataSourceLookup = "${'java:comp/DefaultDataSource'}",
    callerQuery = "#{'select password from app.appuser where userid = ?'}",
    groupsQuery = "select groupname from app.appuser where userid = ?",
    hashAlgorithm = PasswordHash.class,
    priority = 10
)

/**
 * AppUser main class
 */
@Entity
public class AppUser implements Serializable {

    /** Serial version UID for serialization. */
    private static final long serialVersionUID = 1L;

    /** The primary key for the user entity. */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    /** The user identifier. */
    private String userid;
    
    /** The hashed password of the user. */
    private String password;
    
    /** The group name associated with the user. */
    private String groupname;

    /**
     * Gets the ID of the user.
     * 
     * @return The user's ID.
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the ID of the user.
     * 
     * @param id The ID to set.
     */
    public void setId(Long id) {
        this.id = id;
    }
    
    /**
     * Gets the user ID.
     * 
     * @return The user ID.
     */
    public String getUserid() {
        return userid;
    }
    
    /**
     * Sets the user ID.
     * 
     * @param userid The user ID to set.
     */
    public void setUserid(String userid) {
        this.userid = userid;
    }
    
    /**
     * Gets the user's password (returns empty string to prevent accidental exposure).
     * 
     * @return An empty string (password is not directly accessible).
     */
    public String getPassword() {
        return "";
    }
    
    /**
     * Sets the user's password, hashing it before storage.
     * Uses the PBKDF2 algorithm for password hashing.
     * 
     * @param password The password to set for the user.
     */
    public void setPassword(String password) {
        if (password != null && !password.isEmpty()) {
            // Obtain an instance of PasswordHash and initialize it
            Instance<? extends PasswordHash> instance = CDI.current().select(Pbkdf2PasswordHash.class);
            PasswordHash passwordHash = instance.get();
            passwordHash.initialize(new HashMap<String, String>());
            
            // Generate the hashed password and store it
            this.password = passwordHash.generate(password.toCharArray());
        }
    }
    
    /**
     * Gets the group name associated with the user.
     * 
     * @return The group name of the user.
     */
    public String getGroupname() {
        return groupname;
    }
    
    /**
     * Sets the group name for the user.
     * 
     * @param groupname The group name to set.
     */
    public void setGroupname(String groupname) {
        this.groupname = groupname;
    }

    /**
     * Calculates the hash code for the AppUser entity.
     * 
     * @return The hash code for the user.
     */
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    /**
     * Checks if this AppUser is equal to another object.
     * Compares based on the user's ID.
     * 
     * @param object The object to compare to.
     * @return true if the objects are equal, false otherwise.
     */
    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof AppUser)) {
            return false;
        }
        AppUser other = (AppUser) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    /**
     * Provides a string representation of the AppUser entity.
     * 
     * @return A string representation of the user, including the ID.
     */
    @Override
    public String toString() {
        return "cst8218.slider.entity.AppUser[ id=" + id + " ]";
    }
    
}
