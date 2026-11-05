package com.platform.marketplace.config;

import com.platform.marketplace.model.Category;
import com.platform.marketplace.model.Product;
import com.platform.marketplace.repository.CategoryRepository;
import com.platform.marketplace.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    public static final String PREMIUM = "Premium";
    public static final String FREE_RANGE = "Free-Range";
    public static final String HOUSE = "House";
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    @Override
    public void run(String... args) {
        if (categoryRepository.count() > 0) return;

        Category beef = categoryRepository.save(Category.builder()
                .name("Beef").description("Premium beef cuts").build());
        Category lamb = categoryRepository.save(Category.builder()
                .name("Lamb").description("Tender lamb cuts").build());
        Category chicken = categoryRepository.save(Category.builder()
                .name("Chicken").description("Free-range chicken").build());
        Category pork = categoryRepository.save(Category.builder()
                .name("Pork").description("Farm-fresh pork").build());
        Category specials = categoryRepository.save(Category.builder()
                .name("Specials").description("Weekly special deals").build());

        String adminId = "678b828b-f0d6-405e-ad70-de568e852cd7";

        productRepository.save(Product.builder().ownerId(adminId).name("Beef Fillet Steak")
                .description("Premium grass-fed fillet, perfect for grilling")
                .price(BigDecimal.valueOf(189.99)).stockQuantity(50).categoryId(beef.getId())
                .imageUrl("🥩").brand(PREMIUM).rating(4.8).reviewCount(120).active(true).build());
        productRepository.save(Product.builder().ownerId(adminId).name("Beef Rump Steak")
                .description("Tender rump cut with rich flavour")
                .price(BigDecimal.valueOf(139.99)).stockQuantity(40).categoryId(beef.getId())
                .imageUrl("🥩").brand(PREMIUM).rating(4.5).reviewCount(85).active(true).build());
        productRepository.save(Product.builder().ownerId(adminId).name("Beef Mince")
                .description("Lean beef mince, ideal for everyday cooking")
                .price(BigDecimal.valueOf(79.99)).stockQuantity(100).categoryId(beef.getId())
                .imageUrl("🥩").brand("Standard").rating(4.2).reviewCount(200).active(true).build());
        productRepository.save(Product.builder().ownerId(adminId).name("Beef Short Ribs")
                .description("Slow-cook short ribs, fall-off-the-bone tender")
                .price(BigDecimal.valueOf(159.99)).stockQuantity(30).categoryId(beef.getId())
                .imageUrl("🥩").brand(PREMIUM).rating(4.7).reviewCount(65).active(true).build());
        productRepository.save(Product.builder().ownerId(adminId).name("Lamb Leg Roast")
                .description("Whole lamb leg, perfect for Sunday roast")
                .price(BigDecimal.valueOf(149.99)).stockQuantity(25).categoryId(lamb.getId())
                .imageUrl("🍖").brand(PREMIUM).rating(4.9).reviewCount(150).active(true).build());
        productRepository.save(Product.builder().ownerId(adminId).name("Lamb Chops")
                .description("Premium loin chops, quick and easy to cook")
                .price(BigDecimal.valueOf(179.99)).stockQuantity(35).categoryId(lamb.getId())
                .imageUrl("🍖").brand(PREMIUM).rating(4.6).reviewCount(90).active(true).build());
        productRepository.save(Product.builder().ownerId(adminId).name("Lamb Shoulder")
                .description("Slow-roast shoulder with incredible flavour")
                .price(BigDecimal.valueOf(119.99)).stockQuantity(0).categoryId(lamb.getId())
                .imageUrl("🍖").brand(PREMIUM).rating(4.4).reviewCount(45).active(true).build());
        productRepository.save(Product.builder().ownerId(adminId).name("Whole Chicken")
                .description("Free-range whole chicken, approx 1.8kg")
                .price(BigDecimal.valueOf(69.99)).stockQuantity(60).categoryId(chicken.getId())
                .imageUrl("🍗").brand(FREE_RANGE).rating(4.3).reviewCount(180).active(true).build());
        productRepository.save(Product.builder().ownerId(adminId).name("Chicken Breast Fillets")
                .description("Boneless skinless breast fillets")
                .price(BigDecimal.valueOf(89.99)).stockQuantity(80).categoryId(chicken.getId())
                .imageUrl("🍗").brand(FREE_RANGE).rating(4.1).reviewCount(130).active(true).build());
        productRepository.save(Product.builder().ownerId(adminId).name("Chicken Wings")
                .description("Wings perfect for braai or oven baking")
                .price(BigDecimal.valueOf(59.99)).stockQuantity(90).categoryId(chicken.getId())
                .imageUrl("🍗").brand(FREE_RANGE).rating(4.0).reviewCount(110).active(true).build());
        productRepository.save(Product.builder().ownerId(adminId).name("Pork Belly")
                .description("Fatty pork belly for crispy crackling")
                .price(BigDecimal.valueOf(99.99)).stockQuantity(40).categoryId(pork.getId())
                .imageUrl("🥓").brand("Farm-Fresh").rating(4.5).reviewCount(75).active(true).build());
        productRepository.save(Product.builder().ownerId(adminId).name("Pork Chops")
                .description("Thick-cut pork chops, juicy and tender")
                .price(BigDecimal.valueOf(109.99)).stockQuantity(45).categoryId(pork.getId())
                .imageUrl("🥓").brand("Farm-Fresh").rating(4.3).reviewCount(60).active(true).build());
        productRepository.save(Product.builder().ownerId(adminId).name("Boerewors (5kg)")
                .description("Traditional boerewors roll, fresh this week")
                .price(BigDecimal.valueOf(349.99)).stockQuantity(20).categoryId(specials.getId())
                .imageUrl("⭐").brand(HOUSE).rating(4.9).reviewCount(300).active(true).build());
        productRepository.save(Product.builder().ownerId(adminId).name("Mixed Braai Pack")
                .description("Steaks, chops, wors & sosaties for the whole family")
                .price(BigDecimal.valueOf(499.99)).stockQuantity(15).categoryId(specials.getId())
                .imageUrl("⭐").brand(HOUSE).rating(4.8).reviewCount(200).active(true).build());
        productRepository.save(Product.builder().ownerId(adminId).name("Biltong (Dry)")
                .description("Premium dry biltong, made in-house")
                .price(BigDecimal.valueOf(399.99)).stockQuantity(25).categoryId(specials.getId())
                .imageUrl("⭐").brand(HOUSE).rating(4.7).reviewCount(180).active(true).build());
    }
}
