package com.example.testfunding.service;

import com.example.testfunding.dto.FundingCreateRequestDto;
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

import static org.hibernate.query.sqm.tree.SqmNode.log;

@Service
@RequiredArgsConstructor
public class FundingService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final FundingRepository fundingRepository;

    private static final int TIMEOUT = 10000; // 10초

    public void saveToCache(String itemLink) {
        FundingItem fundingItem = new FundingItem();
        fundingItem.setItemLink(itemLink);

        try {
            Document document = Jsoup.connect(itemLink).timeout(TIMEOUT).get();
            String itemImage = getMetaTagContent(document, "og:image");

            fundingItem.setItemImage(itemImage);
        } catch (IOException e) {
            // 로깅을 개선하여 예외의 상세 정보를 기록
            log.error("Error fetching data from the link: " + itemLink, e);
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
            Document document = Jsoup.connect(itemLink).timeout(TIMEOUT).get();

            if (document == null) {
                return null;
            }

            String itemImage = getMetaTagContent(document, "og:image");
            if (itemImage == null) {
                return null;
            }
            fundingItem.setItemImage(itemImage);
        } catch (IOException e) {
            // 로깅을 개선하여 예외의 상세 정보를 기록
            log.error("Error previewing item from the link: " + itemLink, e);
            return null;
        }
        return fundingItem;
    }

    @Transactional
    public Funding saveToDatabase(FundingCreateRequestDto fundingCreateRequestDto) {
        FundingItem fundingItem = getCachedFundingProduct();
        if (fundingItem != null) {
            Funding funding = new Funding(
                    fundingItem.getItemLink(),
                    fundingItem.getItemImage(),
                    fundingCreateRequestDto.getItemName(),
                    fundingCreateRequestDto.getTitle(),
                    fundingCreateRequestDto.getContent(),
                    fundingCreateRequestDto.getGoalAmount(),
                    fundingCreateRequestDto.isPublicFlag(),
                    fundingCreateRequestDto.getEndDate()
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

    public boolean clearCachedItem(String itemLink) {
        return redisTemplate.delete(itemLink);
    }
}