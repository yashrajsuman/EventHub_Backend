package com.eventhub.event;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;

@Entity
public class Registration {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String email;
    private String phoneNumber;
    private Integer age;
    private String gender;
    private String location;
    private Integer height;
    private Integer weight;
    private String education;
    private String experience;
    @Lob
    private String picture;
    @ManyToOne(optional = false)
    private Event event;

    protected Registration() { }

    public Registration(String name, String email, String phoneNumber, Integer age, String gender,
                        String location, Integer height, Integer weight, String education,
                        String experience, String picture) {
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.age = age;
        this.gender = gender;
        this.location = location;
        this.height = height;
        this.weight = weight;
        this.education = education;
        this.experience = experience;
        this.picture = picture;
    }

    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPhoneNumber() { return phoneNumber; }
    public Integer getAge() { return age; }
    public String getGender() { return gender; }
    public String getLocation() { return location; }
    public Integer getHeight() { return height; }
    public Integer getWeight() { return weight; }
    public String getEducation() { return education; }
    public String getExperience() { return experience; }
    public String getPicture() { return picture; }
    void setEvent(Event event) { this.event = event; }
}
