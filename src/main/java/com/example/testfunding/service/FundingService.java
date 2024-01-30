package com.example.testfunding.service;

import com.example.testfunding.dto.FundingDetails;
import com.example.testfunding.entity.Funding;
import com.example.testfunding.entity.FundingProduct;
import com.example.testfunding.entity.Product;
import com.example.testfunding.repository.FundingRepository;
import com.example.testfunding.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

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

    // 새로운 메서드 추가
    public FundingProduct previewProduct(String productLink) {
        FundingProduct fundingProduct = new FundingProduct();
        fundingProduct.setProductLink(productLink);

        try {
            Document document = Jsoup.connect(productLink).timeout(10000).get();

            // 예외 처리: 상품 정보를 가져오지 못할 경우
            if (document == null) {
                return null;
            }

            String productName = getMetaTagContent(document, "og:title");
            String productImage = getMetaTagContent(document, "og:image");

            // 예외 처리: 필수 정보가 없을 경우
            if (productName == null || productImage == null) {
                return null;
            }

            fundingProduct.setProductName(productName);
            fundingProduct.setProductImage(productImage);
        } catch (IOException e) {
            e.printStackTrace();
            // 예외 처리: 상품 정보를 가져오지 못할 경우
            return null;
        }

        return fundingProduct;
    }

    @Transactional
    public Funding saveToDatabase(FundingDetails fundingDetails) {
        FundingProduct fundingProduct = getCachedFundingProduct();
        if (fundingProduct != null) {
            String finalProductName = StringUtils.hasText(fundingDetails.getPublicName())
                    ? fundingDetails.getPublicName()
                    : fundingProduct.getProductName();

            Product product = new Product(finalProductName, fundingProduct.getProductImage());
            productRepository.save(product);

            Funding funding = new Funding(
                    fundingDetails.getTitle(),
                    fundingDetails.getContent(),
                    fundingDetails.getGoalAmount(),
                    product,
                    fundingDetails.isPublicFlag(),
                    fundingDetails.getEndDate()
            );
            return fundingRepository.save(funding);
        }
        return null;
    }

    public void clearCache() {
        redisTemplate.delete("cachedFundingProduct");
    }

    private static String getMetaTagContent(Document document, String property) {
        Element metaTag = document.select("meta[property=" + property + "]").first();
        return (metaTag != null) ? metaTag.attr("content") : null;
    }

}