INSERT INTO store (
    store_name, deleted, address, image, min_order_price,
    open_time, close_time, phone_number, user_id, created_at, modified_at
)
SELECT
    CONCAT('store_', t.n),
    false,
    CONCAT('서울시 강남구_', t.n),
    CONCAT('img_', t.n, '.jpg'),
    10000 + t.n,
    '09:00:00',
    '18:00:00',
    CONCAT('010-0000-', LPAD(t.n % 10000, 4, '0')),
    1,
    NOW(),
    NOW()
FROM (
         SELECT @row := @row + 1 AS n
         FROM information_schema.columns a,
             information_schema.columns b,
             (SELECT @row := 0) r
             LIMIT 1000000
     ) t;
