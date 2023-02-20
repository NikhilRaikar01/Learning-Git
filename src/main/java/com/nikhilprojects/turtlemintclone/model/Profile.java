package com.nikhilprojects.turtlemintclone.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Document (collection = "Profiles")
@Data
public class Profile {

	@Transient
    public static final String SEQUENCE_NAME = "users_sequence";

	@Id
	private long id;

	@NotBlank
    @Size(max=2)
	private String vertical;
	@NotBlank
	@Size(max=100)
	private String make;

	@NotBlank
    @Size(max=100)
	private String model;

	public Profile(long id, String vertical, String make, String model) {
		this.id = id;
		this.vertical = vertical;
		this.make = make;
		this.model = model;
	}

	@Override
	public String toString() {
		return "Profiles [id=" + id + ", Vertical=" + vertical + ", Make=" + make + ", Model" + model
				+ "]";
	}	
}
