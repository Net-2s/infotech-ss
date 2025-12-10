package com.n2s.infotech.controller;

import com.n2s.infotech.dto.AddressDto;
import com.n2s.infotech.model.Address;
import com.n2s.infotech.model.User;
import com.n2s.infotech.repository.AddressRepository;
import com.n2s.infotech.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/addresses")
@RequiredArgsConstructor
public class AddressController {

    private final AddressRepository addressRepository;
    private final UserRepository userRepository;

    @GetMapping
    public List<AddressDto> getUserAddresses(@RequestParam Long userId) {
        return addressRepository.findByUserId(userId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @PostMapping
    public AddressDto createAddress(@Valid @RequestBody AddressDto dto, @RequestParam Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        // If this is default, unset other defaults
        if (dto.getIsDefault()) {
            addressRepository.findByUserIdAndIsDefaultTrue(user.getId())
                    .ifPresent(addr -> {
                        addr.setIsDefault(false);
                        addressRepository.save(addr);
                    });
        }

        Address address = Address.builder()
                .user(user)
                .fullName(dto.getFullName())
                .street(dto.getStreet())
                .city(dto.getCity())
                .postalCode(dto.getPostalCode())
                .country(dto.getCountry())
                .phone(dto.getPhone())
                .isDefault(dto.getIsDefault())
                .build();

        return toDto(addressRepository.save(address));
    }

    @PutMapping("/{id}")
    public AddressDto updateAddress(@PathVariable Long id, @Valid @RequestBody AddressDto dto, @RequestParam Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Address not found"));


        // If setting as default, unset other defaults
        if (dto.getIsDefault() && !address.getIsDefault()) {
            addressRepository.findByUserIdAndIsDefaultTrue(user.getId())
                    .ifPresent(addr -> {
                        addr.setIsDefault(false);
                        addressRepository.save(addr);
                    });
        }

        address.setFullName(dto.getFullName());
        address.setStreet(dto.getStreet());
        address.setCity(dto.getCity());
        address.setPostalCode(dto.getPostalCode());
        address.setCountry(dto.getCountry());
        address.setPhone(dto.getPhone());
        address.setIsDefault(dto.getIsDefault());

        return toDto(addressRepository.save(address));
    }

    @DeleteMapping("/{id}")
    public void deleteAddress(@PathVariable Long id) {
        addressRepository.deleteById(id);
    }

    private AddressDto toDto(Address address) {
        return AddressDto.builder()
                .id(address.getId())
                .fullName(address.getFullName())
                .street(address.getStreet())
                .city(address.getCity())
                .postalCode(address.getPostalCode())
                .country(address.getCountry())
                .phone(address.getPhone())
                .isDefault(address.getIsDefault())
                .build();
    }
}

