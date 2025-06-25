package com.port.portcoin.domain.portfolioCoin;

import com.port.portcoin.domain.coin.repository.CoinRepository;
import com.port.portcoin.domain.portfolio.repository.PortfolioRepository;
import com.port.portcoin.domain.portfolio.service.PortfolioServiceImpl;
import com.port.portcoin.domain.portfoliocoin.repository.PortfolioCoinRepository;
import com.port.portcoin.domain.user.repository.UserRepository;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class PortfolioCoinServiceImplTest {
    @Mock
    private PortfolioRepository portfolioRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CoinRepository coinRepository;

    @Mock
    private PortfolioCoinRepository portfolioCoinRepository;

    @InjectMocks
    private PortfolioServiceImpl portfolioService;

}
