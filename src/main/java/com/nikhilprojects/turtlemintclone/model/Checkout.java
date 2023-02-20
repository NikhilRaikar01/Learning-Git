package com.nikhilprojects.turtlemintclone.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
@Document(collection = "checkout")
@NoArgsConstructor
public class Checkout {
    @Id
    private long checkoutId;
    @NotBlank
    private String name;
    @NotBlank
    @Indexed(unique = true)
    private  String email;
    @NotBlank
    @Indexed(unique = true)
    private  String phone;
    @NotBlank
    private Insurer insurer;

//    private Profile profile;

    public Checkout(long checkoutId, String name, String email, String phone, Insurer insurer) {
        this.checkoutId = checkoutId;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.insurer = insurer;
        this.profile=profile;
    }
}
