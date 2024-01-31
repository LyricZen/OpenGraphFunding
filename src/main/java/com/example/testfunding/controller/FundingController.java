package com.example.testfunding.controller;

import com.example.testfunding.dto.AddLinkRequestDto;
import com.example.testfunding.dto.FundingCreateRequestDto;
import com.example.testfunding.dto.FundingCreateResponseDto;
import com.example.testfunding.entity.FundingItem;
import com.example.testfunding.service.FundingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/funding")
public class FundingController {

    private final FundingService fundingService;

    public FundingController(FundingService fundingService) {
        this.fundingService = fundingService;
    }

    // 링크 추가 및 캐시 저장 요청 처리
    @PostMapping("/addLink")
    public ResponseEntity<?> addLinkAndSaveToCache(@RequestBody AddLinkRequestDto requestDto) {
        try {
            FundingItem fundingItem = fundingService.previewItem(requestDto.getItemLink());
            fundingService.saveToCache(fundingItem);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error adding link: " + e.getMessage());
        }
    }

    // 펀딩 상세 정보 입력 및 DB 저장 요청 처리
    @PostMapping("/create")
    public ResponseEntity<?> createFunding(@RequestBody FundingCreateRequestDto requestDto) {
        try {
            FundingCreateResponseDto responseDto = fundingService.saveToDatabase(requestDto);
            return ResponseEntity.ok(responseDto);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error creating funding: " + e.getMessage());
        }
    }

    // 캐시에서 아이템 삭제 요청 처리
    @DeleteMapping("/clearCache")
    public ResponseEntity<?> clearCache() {
        try {
            fundingService.clearCache();
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error clearing cache: " + e.getMessage());
        }
    }
}