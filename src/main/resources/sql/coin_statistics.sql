SELECT c.symbol, SUM(pc.amount) AS sum
FROM portfolio_coin pc
         JOIN coin c ON pc.coin_id = c.id
         JOIN portfolio p ON pc.portfolio_id = p.portfolio_id
         JOIN users u ON p.user_id = u.id
GROUP BY c.symbol
ORDER BY sum DESC;
