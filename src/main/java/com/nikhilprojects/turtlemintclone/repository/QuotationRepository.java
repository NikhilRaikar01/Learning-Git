package com.nikhilprojects.turtlemintclone.repository;

import com.nikhilprojects.turtlemintclone.model.Quotation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuotationRepository extends MongoRepository<Quotation,String> {
    Quotation findByVerticalAndVehicleMakeAndVehicleModel(String Vertical,String vehicleMake,String vehicleModel);

}
