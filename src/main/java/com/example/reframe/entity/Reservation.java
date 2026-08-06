package com.example.reframe.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "reservations")
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id")
    private Location location;

    // ★ @Transient をやめて @Column に戻します（DBの location_name 列に保存するため）
    @Column(name = "location_name")
    private String locationName;

    @Column(name = "customer_name", nullable = false)
    private String customerName;

    @Column(name = "customer_email")
    private String customerEmail;

    @Column(name = "reserved_date", nullable = false)
    private LocalDate reservedDate;

    @Column(name = "time_slot", nullable = false)
    private String timeSlot;

    @Column(name = "status", nullable = false)
    private String status = "CONFIRMED";

    @Column(name = "reservation_code", unique = true)
    private String reservationCode;

    @PrePersist
    public void prePersist() {
        if (this.reservationCode == null || this.reservationCode.isBlank()) {
            this.reservationCode = UUID.randomUUID().toString();
        }
    }

    // --- Getter / Setter ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Location getLocation() { return location; }
    public void setLocation(Location location) { this.location = location; }

    public String getLocationName() {
        if (this.locationName != null && !this.locationName.isBlank()) {
            return this.locationName;
        }
        return this.location != null ? this.location.getName() : "";
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getCustomerEmail() { return customerEmail; }
    public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }

    public LocalDate getReservedDate() { return reservedDate; }
    public void setReservedDate(LocalDate reservedDate) { this.reservedDate = reservedDate; }

    public String getTimeSlot() { return timeSlot; }
    public void setTimeSlot(String timeSlot) { this.timeSlot = timeSlot; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getReservationCode() { return reservationCode; }
    public void setReservationCode(String reservationCode) { this.reservationCode = reservationCode; }
}