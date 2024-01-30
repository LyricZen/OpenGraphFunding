package com.example.testfunding.controller;

import com.example.testfunding.dto.FundingDetails;
import com.example.testfunding.entity.Funding;
import com.example.testfunding.entity.FundingItem;
import com.example.testfunding.service.FundingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
        model.addAttribute("fundingItem", fundingService.getCachedFundingProduct());
        model.addAttribute("fundingDetails", new FundingDetails());
        return "fundingForm";
    }

    // AJAX 요청을 처리하는 컨트롤러 메서드 추가
    @PostMapping("/previewItem")
    @ResponseBody
    public FundingItem previewItem(@RequestParam String itemLink) {
        FundingItem fundingItem = fundingService.previewItem(itemLink);
        return fundingItem;
    }

    @PostMapping("/saveToCache")
    public String saveToCache(String itemLink) {
        fundingService.saveToCache(itemLink);
        return "redirect:/funding/details";
    }

    @PostMapping("/saveToDatabase")
    public String saveToDatabase(@ModelAttribute FundingDetails fundingDetails, Model model) {
        Funding savedFunding = fundingService.saveToDatabase(fundingDetails);
        System.out.println(fundingDetails.isPublicFlag());
        if (savedFunding != null) {
            model.addAttribute("savedFunding", savedFunding);
            return "success";
        } else {
            model.addAttribute("error", "Failed to save funding. Please check your input and try again.");
            return "error";
        }
    }

    @PostMapping("/clearCachedItem")
    public ResponseEntity<?> clearCachedItem(@RequestParam String itemLink) {
        // 캐시 삭제 로직 구현
        // 예를 들어, Redis를 사용한다면 아래와 같이 캐시에서 itemLink에 해당하는 데이터를 삭제할 수 있습니다.
        boolean result = fundingService.clearCachedItem(itemLink);
        if (result) {
            return ResponseEntity.ok().build(); // 성공적으로 삭제되었을 때 HTTP 200 상태 코드 반환
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // 데이터가 캐시에 없을 때 HTTP 404 상태 코드 반환
        }
    }



    @PostMapping("/cancelFundingCreation")
    public String cancelFundingCreation() {
        fundingService.clearCache();
        return "redirect:/funding/cancel";
    }

}