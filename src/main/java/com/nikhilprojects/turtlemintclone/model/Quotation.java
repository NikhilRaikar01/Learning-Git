package com.nikhilprojects.turtlemintclone.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;
@Data
@Document(collection="Quotes")
@NoArgsConstructor
public class Quotation {
    @Id
    private String requestId;
    @NotBlank
    @Size(max=100)
    private String vertical;
    @NotBlank
    @Size(max=100)
    private String vehicleMake;
    @NotBlank
    @Size(max=100)
    private String vehicleModel;
    private List<Insurer> supportedInsurers;

    public Quotation(String requestId, String vertical, String vehicleMake, String vehicleModel, List<Insurer> supportedInsurers) {
        this.requestId = requestId;
        this.vertical = vertical;
        this.vehicleMake = vehicleMake;
        this.vehicleModel = vehicleModel;
        this.supportedInsurers = supportedInsurers;
    }
}

