package com.example.testfunding.controller;

import com.example.testfunding.dto.FundingDetails;
import com.example.testfunding.entity.Funding;
import com.example.testfunding.entity.FundingProduct;
import com.example.testfunding.service.FundingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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

    // AJAX 요청을 처리하는 컨트롤러 메서드 추가
    @PostMapping("/previewProduct")
    @ResponseBody
    public FundingProduct previewProduct(@RequestParam String productLink) {
        FundingProduct fundingProduct = fundingService.previewProduct(productLink);
        return fundingProduct;
    }

    @PostMapping("/saveToCache")
    public String saveToCache(String productLink) {
        fundingService.saveToCache(productLink);
        return "redirect:/funding/details";
    }

    @PostMapping("/saveToDatabase")
    public String saveToDatabase(@ModelAttribute FundingDetails fundingDetails, Model model) {
        Funding savedFunding = fundingService.saveToDatabase(fundingDetails);
        if (savedFunding != null) {
            model.addAttribute("savedFunding", savedFunding);
            return "success";
        } else {
            model.addAttribute("error", "Failed to save funding. Please check your input and try again.");
            return "error";
        }
    }

    @PostMapping("/cancelFundingCreation")
    public String cancelFundingCreation() {
        fundingService.clearCache();
        return "redirect:/funding/cancel";
    }

}