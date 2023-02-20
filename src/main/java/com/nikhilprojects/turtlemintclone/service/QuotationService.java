package com.nikhilprojects.turtlemintclone.service;

import com.nikhilprojects.turtlemintclone.exception.ResourceNotFoundException;
import com.nikhilprojects.turtlemintclone.model.Insurer;
import com.nikhilprojects.turtlemintclone.model.Profile;
import com.nikhilprojects.turtlemintclone.model.Quotation;
import com.nikhilprojects.turtlemintclone.repository.ProfileRepository;
import com.nikhilprojects.turtlemintclone.repository.QuotationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class QuotationService {
    @Autowired
    QuotationRepository quotationRepository;
    @Autowired
    ProfileRepository profileRepository;

//    public Quotation addQuotation(Quotation quotation)
//    {
//        long id = Math.round(Math.random() * 1e5);
//        while(getQuoteById(id).isPresent()){
//            id = Math.round(Math.random() * 1e5);
//        }
//        quotation.setRequestId(id);
//        quotationRepository.save(quotation);
//        return quotationRepository.save(quotation);
//    }
    public List<Quotation> getAllQuotes()
    {
        return quotationRepository.findAll();
    }
//    public Optional<Quotation> getQuoteById(Long id)
//    {
//        return quotationRepository.findById(id);
//    }


//    public void deleteQuote(Long id)
//    {
//        quotationRepository.deleteById( id);
//    }

    public List<Insurer> getSupportedInsurers(Long id)
    {
        Profile profile=profileRepository.findById(id).get();
        String Vertical= profile.getVertical();
        String vehicleMake=profile.getMake();
        String vehicleModel=profile.getModel();
        Quotation matchquote=quotationRepository.findByVerticalAndVehicleMakeAndVehicleModel(Vertical,vehicleMake,vehicleModel);
        return matchquote.getSupportedInsurers();
    }
}
