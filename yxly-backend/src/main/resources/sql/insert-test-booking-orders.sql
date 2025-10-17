-- 插入测试订单数据
-- 注意：此脚本假设已有用户ID为1的用户和房间ID为1、2的房间

-- 订单1：待确认状态
INSERT INTO booking_order (
    order_no, customer_id, room_id, 
    check_in_date, check_out_date, nights, guests_count,
    room_price, total_amount, paid_amount,
    booking_status, payment_status, booking_source,
    contact_phone, contact_name,
    create_time
) VALUES (
    'YX20241017001', 1, 1,
    '2024-10-25', '2024-10-27', 2, 2,
    288.00, 576.00, 576.00,
    1, 1, '网站',
    '13800138001', '张三',
    '2024-10-17 10:30:00'
);

-- 订单2：已确认状态
INSERT INTO booking_order (
    order_no, customer_id, room_id,
    check_in_date, check_out_date, nights, guests_count,
    room_price, total_amount, paid_amount,
    booking_status, payment_status, booking_source,
    contact_phone, contact_name,
    create_time
) VALUES (
    'YX20241017002', 1, 2,
    '2024-10-20', '2024-10-22', 2, 1,
    358.00, 716.00, 716.00,
    2, 1, '网站',
    '13900139001', '李四',
    '2024-10-17 11:20:00'
);

-- 订单3：已入住状态
INSERT INTO booking_order (
    order_no, customer_id, room_id,
    check_in_date, check_out_date, nights, guests_count,
    room_price, total_amount, paid_amount,
    booking_status, payment_status, booking_source,
    contact_phone, contact_name, check_in_time,
    create_time
) VALUES (
    'YX20241017003', 1, 1,
    '2024-10-18', '2024-10-20', 2, 2,
    288.00, 576.00, 576.00,
    3, 1, '网站',
    '13700137001', '王五',
    '2024-10-18 14:30:00',
    '2024-10-17 09:15:00'
);
