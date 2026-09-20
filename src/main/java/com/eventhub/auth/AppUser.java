package com.eventhub.auth;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "app_users")
public class AppUser {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false, unique = true) private String email;
    @Column(nullable = false) private String passwordHash;
    private boolean emailVerified;
    private String otpHash;
    private LocalDateTime otpExpiresAt;
    private String sessionToken;
    private String name, phoneNumber, gender, location, education, experience;
    private Integer age, height, weight;
    @Lob private String picture;
    @Lob private String aadhaarDocument;

    protected AppUser() { }
    public AppUser(String email, String passwordHash) { this.email = email.toLowerCase(); this.passwordHash = passwordHash; }
    public Long getId() { return id; } public String getEmail() { return email; } public String getPasswordHash() { return passwordHash; }
    public boolean isEmailVerified() { return emailVerified; } public String getOtpHash() { return otpHash; } public LocalDateTime getOtpExpiresAt() { return otpExpiresAt; }
    public String getSessionToken() { return sessionToken; }
    public boolean isProfileComplete() { return name != null; }
    public String getName() { return name; } public String getPhoneNumber() { return phoneNumber; } public Integer getAge() { return age; }
    public String getGender() { return gender; } public String getLocation() { return location; } public Integer getHeight() { return height; }
    public Integer getWeight() { return weight; } public String getEducation() { return education; } public String getExperience() { return experience; } public String getPicture() { return picture; } public String getAadhaarDocument() { return aadhaarDocument; }
    public void setOtp(String hash, LocalDateTime expiresAt) { otpHash = hash; otpExpiresAt = expiresAt; }
    public void clearOtp() { otpHash = null; otpExpiresAt = null; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public void verifyEmail() { emailVerified = true; otpHash = null; otpExpiresAt = null; }
    public void startSession(String token) { sessionToken = token; }
    public void clearSession() { sessionToken = null; }
    public void completeProfile(ProfileRequest p) { name = p.name(); phoneNumber = p.phoneNumber(); age = p.age(); gender = p.gender(); location = p.location(); height = p.height(); weight = p.weight(); education = p.education(); experience = p.experience(); picture = p.picture(); aadhaarDocument = p.aadhaarDocument(); }
}
