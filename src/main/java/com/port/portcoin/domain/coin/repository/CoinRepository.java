package com.port.portcoin.domain.coin.repository;

import com.port.portcoin.domain.coin.entity.Coin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface CoinRepository extends JpaRepository<Coin, Long> {
    @Query("SELECT c.id, c.symbol FROM Coin c")
    List<Object[]> findAllCoinIdAndSymbol();
}
