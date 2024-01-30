package com.example.testfunding.service;

import com.example.testfunding.dto.FundingDetails;
import com.example.testfunding.entity.Funding;
import com.example.testfunding.entity.FundingItem;
import com.example.testfunding.repository.FundingRepository;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class FundingService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final FundingRepository fundingRepository;

    public void saveToCache(String itemLink) {
        FundingItem fundingItem = new FundingItem();
        fundingItem.setItemLink(itemLink);

        try {
            Document document = Jsoup.connect(itemLink).timeout(5000).get();
            String itemImage = getMetaTagContent(document, "og:image");

            fundingItem.setItemImage(itemImage);
        } catch (IOException e) {
            e.printStackTrace();
            // 예외 처리: 상품 정보를 가져오지 못할 경우
        }
        redisTemplate.opsForValue().set("cachedFundingItem", fundingItem);
    }

    public FundingItem getCachedFundingProduct() {
        return (FundingItem) redisTemplate.opsForValue().get("cachedFundingItem");
    }

    // 새로운 메서드 추가
    public FundingItem previewItem(String itemLink) {
        FundingItem fundingItem = new FundingItem();
        fundingItem.setItemLink(itemLink);

        try {
            Document document = Jsoup.connect(itemLink).timeout(5000).get();

            // 예외 처리: 상품 정보를 가져오지 못할 경우
            if (document == null) {
                return null;
            }

            String itemImage = getMetaTagContent(document, "og:image");
            // 예외 처리: 필수 정보가 없을 경우
            if (itemImage == null) {
                return null;
            }
            fundingItem.setItemImage(itemImage);
        } catch (IOException e) {
            e.printStackTrace();
            // 예외 처리: 상품 정보를 가져오지 못할 경우
            return null;
        }
        return fundingItem;
    }

    @Transactional
    public Funding saveToDatabase(FundingDetails fundingDetails) {
        FundingItem fundingItem = getCachedFundingProduct();
        if (fundingItem != null) {
            Funding funding = new Funding(
                    fundingItem.getItemLink(),
                    fundingItem.getItemImage(),
                    fundingDetails.getTitle(),
                    fundingDetails.getContent(),
                    fundingDetails.getGoalAmount(),
                    fundingDetails.isPublicFlag(),
                    fundingDetails.getEndDate()
            );
            return fundingRepository.save(funding);
        }
        return null;
    }

    public void clearCache() {
        redisTemplate.delete("cachedFundingItem");
    }

    private static String getMetaTagContent(Document document, String property) {
        Element metaTag = document.select("meta[property=" + property + "]").first();
        return (metaTag != null) ? metaTag.attr("content") : null;
    }

}