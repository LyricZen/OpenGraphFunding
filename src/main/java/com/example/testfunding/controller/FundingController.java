package com.example.testfunding.controller;

import com.example.testfunding.dto.FundingDetails;
import com.example.testfunding.entity.Funding;
import com.example.testfunding.service.FundingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/funding")
public class FundingController {

    @Autowired
    private FundingService fundingService;

    @GetMapping("/form")
    public String showFundingForm(Model model) {
        model.addAttribute("fundingProduct", fundingService.getCachedFundingProduct());
        model.addAttribute("fundingDetails", new FundingDetails());
        return "fundingForm";
    }

    @PostMapping("/saveToCache")
    public String saveToCache(String productLink) {
        fundingService.saveToCache(productLink);
        return "redirect:/funding/details";
    }

    @PostMapping("/saveToDatabase")
    public String saveToDatabase(@ModelAttribute FundingDetails fundingDetails, Model model) {
        Funding savedFunding = fundingService.saveToDatabase(fundingDetails.getTitle(), fundingDetails.getContent(),fundingDetails.getGoalAmount());

        if (savedFunding != null) {
            model.addAttribute("savedFunding", savedFunding);
            return "success";
        } else {
            // Handle the case where saving failed
            return "redirect:/funding/error";
        }
    }

    @PostMapping("/cancelFundingCreation")
    public String cancelFundingCreation() {
        fundingService.clearCache();
        return "redirect:/funding/cancel";
    }

    @GetMapping("/details")
    public String showFundingDetails(Model model) {
        model.addAttribute("fundingProduct", fundingService.getCachedFundingProduct());
        model.addAttribute("fundingDetails", new FundingDetails());
        return "fundingDetailsForm";
    }
}