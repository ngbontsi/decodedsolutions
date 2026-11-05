package com.platform.guesthouse.config;

import com.platform.guesthouse.model.Guesthouse;
import com.platform.guesthouse.model.Room;
import com.platform.guesthouse.repository.GuesthouseRepository;
import com.platform.guesthouse.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    public static final String STANDARD = "Standard";
    public static final String SUITE = "Suite";
    public static final String DELUXE = "Deluxe";
    private final GuesthouseRepository guesthouseRepository;
    private final RoomRepository roomRepository;

    @Override
    public void run(String... args) {
        if (guesthouseRepository.count() > 0) return;

        String adminId = "678b828b-f0d6-405e-ad70-de568e852cd7";

        Guesthouse oceanView = guesthouseRepository.save(Guesthouse.builder()
                .ownerId(adminId).name("Empolweni Guest House")
                .description("Wake up to stunning ocean views. Our seaside lodge offers luxury rooms with private balconies, direct beach access, and fresh seafood dining.")
                .address("12 Beach Road, Empolweni").phone("043 123 4567")
                .imageUrl("🌊").amenities("Ocean View, Beach Access, Restaurant").build());
        roomRepository.save(Room.builder().guesthouseId(oceanView.getId()).ownerId(adminId)
                .roomNumber("101").type(SUITE).pricePerNight(BigDecimal.valueOf(2500))
                .capacity(2).amenities("King Bed,Ocean Balcony,Mini Bar,Bathtub").build());
        roomRepository.save(Room.builder().guesthouseId(oceanView.getId()).ownerId(adminId)
                .roomNumber("102").type(DELUXE).pricePerNight(BigDecimal.valueOf(1800))
                .capacity(2).amenities("Queen Bed,Sea View,Work Desk").build());
        roomRepository.save(Room.builder().guesthouseId(oceanView.getId()).ownerId(adminId)
                .roomNumber("103").type("Family").pricePerNight(BigDecimal.valueOf(2200))
                .capacity(4).amenities("2 Double Beds,Balcony,TV,Fridge").available(false).build());
        roomRepository.save(Room.builder().guesthouseId(oceanView.getId()).ownerId(adminId)
                .roomNumber("104").type(STANDARD).pricePerNight(BigDecimal.valueOf(1200))
                .capacity(2).amenities("Double Bed,Garden View").build());

        Guesthouse mountainRetreat = guesthouseRepository.save(Guesthouse.builder()
                .ownerId(adminId).name("Atlanta Guest House")
                .description("Escape to the mountains. Nestled among pine forests, our retreat offers cosy fireplaces, hiking trails, and breathtaking valley views.")
                .address("42 Forest Lane, Atlanta").phone("043 234 5678")
                .imageUrl("🏔️").amenities("Mountain View, Hiking, Fireplace").build());
        roomRepository.save(Room.builder().guesthouseId(mountainRetreat.getId()).ownerId(adminId)
                .roomNumber("201").type("Chalet").pricePerNight(BigDecimal.valueOf(3500))
                .capacity(4).amenities("2 Bedrooms,Fireplace,Kitchen,Mountain View").build());
        roomRepository.save(Room.builder().guesthouseId(mountainRetreat.getId()).ownerId(adminId)
                .roomNumber("202").type(DELUXE).pricePerNight(BigDecimal.valueOf(1800))
                .capacity(2).amenities("King Bed,Forest View,Balcony").build());
        roomRepository.save(Room.builder().guesthouseId(mountainRetreat.getId()).ownerId(adminId)
                .roomNumber("203").type(STANDARD).pricePerNight(BigDecimal.valueOf(1400))
                .capacity(2).amenities("Queen Bed,Valley View,Tea Station").build());

        Guesthouse cityStay = guesthouseRepository.save(Guesthouse.builder()
                .ownerId(adminId).name("City Stay Inn")
                .description("In the heart of the city. Walking distance to restaurants, shops, and entertainment. Perfect for business or leisure travellers.")
                .address("88 Main Street, Downtown").phone("043 345 6789")
                .imageUrl("🏙️").amenities("City Centre, Business, Shopping").build());
        roomRepository.save(Room.builder().guesthouseId(cityStay.getId()).ownerId(adminId)
                .roomNumber("301").type(SUITE).pricePerNight(BigDecimal.valueOf(2000))
                .capacity(2).amenities("King Bed,City View,Lounge,Mini Bar").build());
        roomRepository.save(Room.builder().guesthouseId(cityStay.getId()).ownerId(adminId)
                .roomNumber("302").type(STANDARD).pricePerNight(BigDecimal.valueOf(950))
                .capacity(1).amenities("Single Bed,Work Desk,WiFi").build());
        roomRepository.save(Room.builder().guesthouseId(cityStay.getId()).ownerId(adminId)
                .roomNumber("303").type(STANDARD).pricePerNight(BigDecimal.valueOf(1100))
                .capacity(2).amenities("2 Single Beds,City View").build());
        roomRepository.save(Room.builder().guesthouseId(cityStay.getId()).ownerId(adminId)
                .roomNumber("304").type("Family").pricePerNight(BigDecimal.valueOf(1600))
                .capacity(3).amenities("Double + Single Bed,TV,Fridge").available(false).build());

        Guesthouse bushveld = guesthouseRepository.save(Guesthouse.builder()
                .ownerId(adminId).name("Bushveld Lodge")
                .description("Experience the African bushveld. Game drives, bush walks, and starlit dinners in our luxury tented camp.")
                .address("Safari Road, Savanna Region").phone("043 456 7890")
                .imageUrl("🌿").amenities("Safari, Nature, Luxury Tent").build());
        roomRepository.save(Room.builder().guesthouseId(bushveld.getId()).ownerId(adminId)
                .roomNumber("401").type("Tent Suite").pricePerNight(BigDecimal.valueOf(3000))
                .capacity(2).amenities("King Bed,En-suite,Deck,Game View").build());
        roomRepository.save(Room.builder().guesthouseId(bushveld.getId()).ownerId(adminId)
                .roomNumber("402").type("Tent").pricePerNight(BigDecimal.valueOf(2400))
                .capacity(4).amenities("2 Bedrooms,En-suite,Deck").build());
        roomRepository.save(Room.builder().guesthouseId(bushveld.getId()).ownerId(adminId)
                .roomNumber("403").type("Tent").pricePerNight(BigDecimal.valueOf(1500))
                .capacity(2).amenities("Double Bed,En-suite,Camp View").build());

        Guesthouse harbour = guesthouseRepository.save(Guesthouse.builder()
                .ownerId(adminId).name("Harbour House")
                .description("A boutique waterfront retreat. Only 8 rooms for an intimate, personalized experience with world-class dining.")
                .address("5 Harbour Road, Waterfront").phone("043 567 8901")
                .imageUrl("⛵").amenities("Boutique, Waterfront, Fine Dining").build());
        roomRepository.save(Room.builder().guesthouseId(harbour.getId()).ownerId(adminId)
                .roomNumber("501").type(SUITE).pricePerNight(BigDecimal.valueOf(4200))
                .capacity(2).amenities("King Bed,Harbour View,Jacuzzi,Mini Bar").build());
        roomRepository.save(Room.builder().guesthouseId(harbour.getId()).ownerId(adminId)
                .roomNumber("502").type(DELUXE).pricePerNight(BigDecimal.valueOf(2800))
                .capacity(2).amenities("Queen Bed,Marina View,Balcony").build());
        roomRepository.save(Room.builder().guesthouseId(harbour.getId()).ownerId(adminId)
                .roomNumber("503").type(STANDARD).pricePerNight(BigDecimal.valueOf(2200))
                .capacity(2).amenities("Double Bed,Garden Access,Patio").build());
    }
}
