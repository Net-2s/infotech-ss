package com.n2s.infotech.init;

import com.n2s.infotech.model.*;
import com.n2s.infotech.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final SellerProfileRepository sellerProfileRepository;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final ListingRepository listingRepository;

    public DataInitializer(UserRepository userRepository, SellerProfileRepository sellerProfileRepository, CategoryRepository categoryRepository, ProductRepository productRepository, ListingRepository listingRepository) {
        this.userRepository = userRepository;
        this.sellerProfileRepository = sellerProfileRepository;
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
        this.listingRepository = listingRepository;
    }

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) return; // don't re-initialize

        User admin = User.builder()
                .email("admin@local")
                .password("admin") // Pas de hashage pour simplifier les tests
                .displayName("Admin")
                .roles(Set.of(Role.ROLE_ADMIN, Role.ROLE_USER))
                .build();

        User sellerUser = User.builder()
                .email("seller@local")
                .password("seller") // Pas de hashage pour simplifier les tests
                .displayName("Seller One")
                .roles(Set.of(Role.ROLE_SELLER, Role.ROLE_USER))
                .build();

        userRepository.saveAll(List.of(admin, sellerUser));

        SellerProfile sellerProfile = SellerProfile.builder()
                .shopName("Seller Shop")
                .description("Boutique de test")
                .contactEmail("seller@local")
                .user(sellerUser)
                .build();
        sellerProfile = sellerProfileRepository.save(sellerProfile);

        // set back-reference for user
        sellerUser.setSellerProfile(sellerProfile);
        userRepository.save(sellerUser);

        Category phones = Category.builder().name("Phones").description("Smartphones").build();
        Category laptops = Category.builder().name("Laptops").description("Ordinateurs portables").build();
        categoryRepository.saveAll(List.of(phones, laptops));

        Product iphone = Product.builder()
                .title("iPhone X")
                .brand("Apple")
                .model("X")
                .condition("refurbished")
                .description("iPhone X reconditionné, testé et garanti")
                .category(phones)
                .build();

        Product mac = Product.builder()
                .title("MacBook Pro 2018")
                .brand("Apple")
                .model("MacBook Pro")
                .condition("used")
                .description("MacBook Pro en bon état")
                .category(laptops)
                .build();

        // add product images and set bidirectional relation
        ProductImage iphoneImg1 = ProductImage.builder().url("/images/iphone-x-1.jpg").altText("iPhone X front").product(iphone).build();
        ProductImage iphoneImg2 = ProductImage.builder().url("/images/iphone-x-2.jpg").altText("iPhone X back").product(iphone).build();
        iphone.getImages().add(iphoneImg1);
        iphone.getImages().add(iphoneImg2);

        ProductImage macImg1 = ProductImage.builder().url("/images/macbook-pro-2018-1.jpg").altText("MacBook Pro").product(mac).build();
        mac.getImages().add(macImg1);

        productRepository.saveAll(List.of(iphone, mac));

        Listing l1 = Listing.builder()
                .product(iphone)
                .seller(sellerProfile)
                .price(new BigDecimal("349.99"))
                .quantity(5)
                .conditionNote("Like new, batterie 90%")
                .active(true)
                .build();

        Listing l2 = Listing.builder()
                .product(mac)
                .seller(sellerProfile)
                .price(new BigDecimal("1199.00"))
                .quantity(2)
                .conditionNote("Some scratches, fully tested")
                .active(true)
                .build();

        listingRepository.saveAll(List.of(l1, l2));
    }
}
