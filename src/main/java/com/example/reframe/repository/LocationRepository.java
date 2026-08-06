package com.example.reframe.repository; // ※ご自身のパッケージ名に調整してください

import com.example.reframe.entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {

    // 店舗名（name）で店舗情報を取得する
    Optional<Location> findByName(String name);
}