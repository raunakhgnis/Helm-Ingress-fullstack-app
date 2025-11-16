package com.example.simpleauction.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "users")
@Data
//@NoArgsConstructor // <--- ADD THIS ANNOTATION
// @AllArgsConstructor // <-- Optional: If you also want Lombok to make the one with args
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    // You can KEEP your manual constructor if you prefer,
    // OR remove it and add @AllArgsConstructor if you want Lombok to generate it.
    // Having BOTH @NoArgsConstructor and @AllArgsConstructor is common.
    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }
    // Default constructor for JPA
    public User() {}
    
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
