package com.nikhilprojects.turtlemintclone.controller;

import com.nikhilprojects.turtlemintclone.exception.ResourceNotFoundException;
import com.nikhilprojects.turtlemintclone.model.Insurer;
import com.nikhilprojects.turtlemintclone.model.Quotation;
import com.nikhilprojects.turtlemintclone.repository.QuotationRepository;
import com.nikhilprojects.turtlemintclone.service.QuotationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/turtlemint")
public class QuotationController {
    @Autowired
    private QuotationService quotationService;

    @Autowired
    private QuotationRepository quotationRepository;

    @GetMapping("/quotes/{id}")
    public List<Insurer> getSupportedInsurers(@PathVariable long id){
        return quotationService.getSupportedInsurers(id);
    }

//    @PostMapping("/quotes")
//    public String createQuotation(@RequestBody Quotation quotation){
//        quotationService.addQuotation(quotation);
//        return "Created Quote with id = " + quotation.getRequestId();
//    }

    @GetMapping("/quotes")
    public List<Quotation> getAllQuotes(){
        return quotationService.getAllQuotes();
    }

//    @PutMapping("/quotes/{requestId}")
//    public ResponseEntity<Quotation> updateQuotation(@PathVariable long requestId, @RequestBody Quotation quotedetails) throws ResourceNotFoundException {
//        Quotation updateQuote = quotationRepository.findById(requestId).orElseThrow(() -> new ResourceNotFoundException("Employee not exist with id: " + requestId));
//
//        updateQuote.setVertical(quotedetails.getVertical());
//        updateQuote.setVehicleMake(quotedetails.getVehicleMake());
//        updateQuote.setVehicleModel(quotedetails.getVehicleModel());
//
//        quotationRepository.save(updateQuote);
//
//        return ResponseEntity.ok(updateQuote);
//    }

//    @DeleteMapping("/quotes/{id}")
//    public String deleteQuotation(@PathVariable long id){
//        quotationService.deleteQuote(id);
//        return "Deleted Quote with id = " + id;
//    }


}
