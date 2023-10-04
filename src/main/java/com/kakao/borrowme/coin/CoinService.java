package com.kakao.borrowme.coin;

import com.kakao.borrowme.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CoinService {

    private final CoinJPARepository coinJPARepository;

    @Autowired
    public CoinService(CoinJPARepository coinJPARepository) {
        this.coinJPARepository = coinJPARepository;
    }

    public CoinResponse.FindByIdDTO getUserCoin(User user) {

        Optional<Coin> coinOptional = coinJPARepository.findByUserId(user.getId());
        Coin coin;

        if (coinOptional.isPresent()) {
            coin = coinOptional.get();
        } else {
            // 코인 정보가 없을 경우 기본값으로 0으로 설정
            coin = Coin.builder()
                    .user(user)
                    .piece(0L)
                    .build();
        }

        return new CoinResponse.FindByIdDTO(coin.getPiece());
    }

    public CoinResponse.FindByIdDTO chargeCoin(User user, Long piece) {

        Optional<Coin> coinOptional = coinJPARepository.findByUserId(user.getId());
        Coin coin;

        if (coinOptional.isPresent()) {
            coin = coinOptional.get();
            coin.setPiece(coin.getPiece() + piece);
        } else {
            coin = Coin.builder()
                    .user(user)
                    .piece(piece)
                    .build();
        }

        coinJPARepository.save(coin);

        return new CoinResponse.FindByIdDTO(coin.getPiece());

    }

    public void useCoin(User user, Long rentalPrice) {

        Optional<Coin> coinOptional = coinJPARepository.findByUserId(user.getId());

        if (coinOptional.isPresent()) {
            Coin coin = coinOptional.get();
            if (coin.getPiece() >= rentalPrice) {
                coin.setPiece(coin.getPiece() - rentalPrice);
                coinJPARepository.save(coin);
                // 결제 로직 추가
            } else {
                // 코인 잔액이 부족한 경우 예외 처리
                throw new IllegalArgumentException("코인 잔액이 부족합니다.");
            }
        } else {
            // 코인 엔티티가 없는 경우 예외 처리
            throw new IllegalArgumentException("코인 정보가 없습니다.");
        }

    }
}