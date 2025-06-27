package com.port.portcoin.domain.portfolio.service;

import com.port.portcoin.common.exception.BaseException;
import com.port.portcoin.common.exception.ExceptionEnum;
import com.port.portcoin.domain.coin.entity.Coin;
import com.port.portcoin.domain.coin.repository.CoinRepository;
import com.port.portcoin.domain.portfolio.dto.request.PortfolioCoinRequestDto;
import com.port.portcoin.domain.portfolio.dto.request.PortfolioRequest;
import com.port.portcoin.domain.portfolio.dto.response.PortfolioDetailResponse;
import com.port.portcoin.domain.portfolio.dto.response.PortfolioResponse;
import com.port.portcoin.domain.portfolio.entity.Portfolio;
import com.port.portcoin.domain.portfolio.repository.PortfolioRepository;
import com.port.portcoin.domain.portfoliocoin.entity.PortfolioCoin;
import com.port.portcoin.domain.user.dto.AuthUser;
import com.port.portcoin.domain.user.entity.User;
import com.port.portcoin.domain.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PortfolioServiceImpl implements PortfolioService {

    private final UserRepository userRepository;
    private final PortfolioRepository portfolioRepository;
    private final CoinRepository coinRepository;


    // 포트폴리오 생성
    @Override
    @Transactional
    public void createPortfolio(PortfolioRequest portfolioRequest, AuthUser authUser){
        User user = getUser(authUser.getId());

        // 2. 포트폴리오 생성
        Portfolio portfolio = new Portfolio(portfolioRequest.getTitle(), user);

        // 3. 코인 리스트 돌면서 PortfolioCoin 생성 및 추가
        for (PortfolioCoinRequestDto coinRequest : portfolioRequest.getCoins()) {
            Coin coin = coinRepository.findById(coinRequest.getCoinId())
                    .orElseThrow(() -> new BaseException(ExceptionEnum.COIN_NOT_FOUND));

            PortfolioCoin portfolioCoin = new PortfolioCoin(
                    portfolio,
                    coin,
                    coinRequest.getAmount(),
                    coinRequest.getPurchasePrice(),
                    0.0,  // currentPrice는 API에서 받아오거나 0.0으로 초기화 가능
                    coinRequest.getPurchaseDate()
            );

            portfolio.addPortfolioCoin(portfolioCoin);
        }

        // 4. 저장
        portfolioRepository.save(portfolio);
    }

    //포트폴리오 수정
    @Override
    public void updatePortfolioName(PortfolioRequest portfolioRequest, AuthUser authUser, Long portfolioId){
        Portfolio portfolio = validatePortfolio(authUser,  portfolioId);

        portfolio.updateName(portfolioRequest.getTitle());
        portfolioRepository.save(portfolio);
    }


    //포트폴리오 삭제
    public void deletePortfolio(AuthUser authUser,  Long portfolioId){
        Portfolio portfolio = validatePortfolio(authUser,  portfolioId);

        portfolio.updateDeletedAt();
        portfolioRepository.save(portfolio);
    }

    //포트폴리오 전체 조회
    public ArrayList<PortfolioResponse> getPortfolios(AuthUser authUser){
        User user = getUser(authUser.getId());

        List<Portfolio> portfolios = portfolioRepository.findAllByUserIdAndDeletedAtIsNull(user.getId());

        ArrayList<PortfolioResponse> responses = new ArrayList<>();
        for (Portfolio portfolio : portfolios) {
            responses.add(new PortfolioResponse(portfolio));
        }

        return responses;
    }

    //포트폴리오 상세조회
    @Transactional
    public PortfolioDetailResponse getDetail(Long portfolioId, AuthUser authUser){
        Portfolio portfolio = validatePortfolio(authUser,  portfolioId);

        return new PortfolioDetailResponse(portfolio);
    }


    private User getUser(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new BaseException(ExceptionEnum.USER_NOT_FOUND));
    }

    private Portfolio validatePortfolio(AuthUser authUser,  Long portfolioId){
        Portfolio portfolio = portfolioRepository.findByPortfolioId(portfolioId)
                .orElseThrow(() -> new BaseException(ExceptionEnum.PORTFOLIO_NOT_FOUND));

        if(!portfolio.getUser().getId().equals(authUser.getId())){
            throw new BaseException(ExceptionEnum.USER_NOT_FOUND);
        }

        if (portfolio.getDeletedAt()!= null) throw new BaseException(ExceptionEnum.PORTFOLIO_NOT_FOUND);

        return portfolio;
    }
}
