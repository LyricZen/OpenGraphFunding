package com.example.testfunding.service;

import com.example.testfunding.entity.Funding;
import com.example.testfunding.entity.FundingProduct;
import com.example.testfunding.entity.Product;
import com.example.testfunding.repository.FundingRepository;
import com.example.testfunding.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class FundingService {


    private final RedisTemplate<String, Object> redisTemplate;

    private final ProductRepository productRepository;

    private final FundingRepository fundingRepository;

    public void saveToCache(String productLink) {
        FundingProduct fundingProduct = new FundingProduct();
        fundingProduct.setProductLink(productLink);

        try {
            Document document = Jsoup.connect(productLink).timeout(5000).get();
            String productName = getMetaTagContent(document, "og:title");
            String productImage = getMetaTagContent(document, "og:image");

            fundingProduct.setProductName(productName);
            fundingProduct.setProductImage(productImage);
        } catch (IOException e) {
            e.printStackTrace();
            // 예외 처리: 상품 정보를 가져오지 못할 경우
        }

        redisTemplate.opsForValue().set("cachedFundingProduct", fundingProduct);
    }

    public FundingProduct getCachedFundingProduct() {
        return (FundingProduct) redisTemplate.opsForValue().get("cachedFundingProduct");
    }

    public Funding saveToDatabase(String fundingTitle,String fundingContent ,Integer fundingGoalAmount) {
        FundingProduct fundingProduct = getCachedFundingProduct();
        if (fundingProduct != null) {
            // Product를 저장
            Product product = new Product();
            product.setName(fundingProduct.getProductName());
            product.setImage(fundingProduct.getProductImage());

            // Product를 저장하고 저장된 Product를 반환
            Product savedProduct = productRepository.save(product);

            // Funding을 저장할 때 OneToOne 관계로 설정
            Funding funding = new Funding();
            funding.setTitle(fundingTitle);
            funding.setContent(fundingContent);
            funding.setGoalAmount(fundingGoalAmount);
            funding.setProduct(savedProduct);

            Funding successFunding = fundingRepository.save(funding);
            clearCache();// 캐시 비우기 (원하는 시점에 호출하도록 수정)
            // Funding을 저장하고 저장된 Funding을 반환
            return successFunding;

        }

        return null; // 혹시나 실패했을 경우 null을 반환하거나 예외 처리를 추가할 수 있습니다.
    }

    public void clearCache() {
        redisTemplate.delete("cachedFundingProduct");
    }

    private static String getMetaTagContent(Document document, String property) {
        Element metaTag = document.select("meta[property=" + property + "]").first();
        return (metaTag != null) ? metaTag.attr("content") : null;
    }
}