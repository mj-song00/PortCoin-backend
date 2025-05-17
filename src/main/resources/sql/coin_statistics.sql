SELECT c.symbol, SUM(pc.amount) AS sum
FROM portfolio_coin pc
         JOIN coin c ON pc.coin_id = c.id
         JOIN portfolio p ON pc.portfolio_id = p.portfolio_id
         JOIN users u ON p.user_id = u.id
GROUP BY c.symbol
ORDER BY sum DESC;


SELECT
    CASE
        WHEN coin_count = 1 THEN '1개'
        WHEN coin_count BETWEEN 2 AND 3 THEN '2~3개'
        WHEN coin_count BETWEEN 4 AND 5 THEN '4~5개'
        ELSE '6개 이상'
        END AS 구간,
    COUNT(*) AS 유저수
FROM (
         SELECT u.id AS user_id, COUNT(DISTINCT pc.coin_id) AS coin_count
         FROM users u
                  JOIN portfolio p ON u.id = p.user_id
                  JOIN portfolio_coin pc ON p.portfolio_id = pc.portfolio_id
         GROUP BY u.id
     ) AS user_coin_counts
GROUP BY 구간;
