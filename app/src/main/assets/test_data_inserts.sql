-- Test data for RealEstate application
-- Created on June 8, 2025
-- IMPORTANT: Order matters! Insert users, then properties, then reservations

-- =====================================================
-- USERS
-- =====================================================
-- Users with countries and cities from the CountryService
-- Palestine: Nablus, Tulkarem, Ramallah
-- Jordan: Amman, Al Karak, Irbid
-- UAE: Dubai, Abu Dhabi, Sharjah

-- Insert 10 test users (all non-admin with is_admin=0)
-- Phone numbers stored without '+' and country codes
INSERT INTO users (email, first_name, last_name, password, gender, country, city, phone_number, is_admin)
VALUES
('john.smith@example.com', 'John', 'Smith', 'password123', 'Male', 'Jordan', 'Amman', '789123456', 0),
('sara.ahmad@example.com', 'Sara', 'Ahmad', 'securePwd456!', 'Female', 'Palestine', 'Ramallah', '599123456', 0),
('mohamed.ali@example.com', 'Mohamed', 'Ali', 'userPass789@', 'Male', 'UAE', 'Dubai', '501234567', 0),
('layla.hassan@example.com', 'Layla', 'Hassan', 'laylaPass2023!', 'Female', 'Jordan', 'Irbid', '778456789', 0),
('omar.khalid@example.com', 'Omar', 'Khalid', 'omar2023Secure!', 'Male', 'Palestine', 'Nablus', '592345678', 0),
('admin.user@realestate.com', 'Admin', 'User', 'adminSecure123!', 'Male', 'Jordan', 'Al Karak', '777888999', 0),
('nour.admin@realestate.com', 'Nour', 'Admin', 'nourAdmin456!', 'Female', 'Palestine', 'Tulkarem', '598765432', 0),
('ahmad.manager@realestate.com', 'Ahmad', 'Manager', 'ahmadSuper789!', 'Male', 'UAE', 'Abu Dhabi', '502345678', 0),
('rania.admin@realestate.com', 'Rania', 'Admin', 'rania2023Pwd!', 'Female', 'Jordan', 'Irbid', '790123456', 0),
('zaid.super@realestate.com', 'Zaid', 'Super', 'zaidMaster2023!', 'Male', 'UAE', 'Sharjah', '503456789', 0);

-- =====================================================
-- PROPERTIES
-- =====================================================
-- Insert properties
-- Must include all properties referenced by reservations
INSERT INTO properties (id, title, type, price, location, area, bedrooms, bathrooms, image_url, description)
VALUES
(101, 'Modern 2-Bedroom Apartment', 'Apartment', 85000, 'Ramallah, Palestine', '120 m²', 2, 2, 'https://example.com/images/apartment1.jpg', 'Beautiful apartment with city view, close to shopping malls.'),
(102, 'Luxury Family Villa', 'Villa', 450000, 'Amman, Jordan', '350 m²', 5, 4, 'https://example.com/images/villa1.jpg', 'Spacious villa with private pool and large garden.'),
(103, 'Residential Land Plot', 'Land', 120000, 'Aleppo, Syria', '500 m²', 0, 0, 'https://example.com/images/land1.jpg', 'Prime land suitable for residential development.'),
(104, 'Penthouse Apartment with Roof Access', 'Apartment', 140000, 'Nablus, Palestine', '180 m²', 3, 3, 'https://example.com/images/apartment2.jpg', 'Luxury penthouse with rooftop terrace.'),
(105, 'Beachside Villa', 'Villa', 600000, 'Latakia, Syria', '400 m²', 6, 5, 'https://example.com/images/villa2.jpg', 'Elegant beachfront villa with panoramic sea views.'),
(106, 'Compact Studio Apartment', 'Apartment', 45000, 'Gaza, Palestine', '50 m²', 1, 1, 'https://example.com/images/apartment3.jpg', 'Affordable studio perfect for singles or students.'),
(107, 'Country Style Villa', 'Villa', 320000, 'Zarqa, Jordan', '270 m²', 4, 3, 'https://example.com/images/villa3.jpg', 'Villa with large outdoor area and rustic charm.'),
(108, 'Agricultural Land', 'Land', 95000, 'Homs, Syria', '700 m²', 0, 0, 'https://example.com/images/land2.jpg', 'Land suitable for farming and agriculture.'),
(109, 'Family Apartment with Garden View', 'Apartment', 99000, 'Bethlehem, Palestine', '140 m²', 3, 2, 'https://example.com/images/apartment4.jpg', 'Spacious apartment with access to a shared garden.'),
(110, 'Mountain View Villa', 'Villa', 510000, 'Ajloun, Jordan', '380 m²', 5, 4, 'https://example.com/images/villa4.jpg', 'Villa with stunning mountain views.'),
(111, 'Urban Residential Land', 'Land', 135000, 'Damascus, Syria', '450 m²', 0, 0, 'https://example.com/images/land3.jpg', 'Land located near city amenities and services.'),
(112, 'Luxury City Apartment', 'Apartment', 160000, 'Amman, Jordan', '200 m²', 3, 3, 'https://example.com/images/apartment5.jpg', 'Modern apartment in the city center.'),
(113, 'Seaside Land Plot', 'Land', 250000, 'Latakia, Syria', '600 m²', 0, 0, 'https://example.com/images/land4.jpg', 'Land close to the beach, ideal for resorts.'),
(114, 'Duplex Family Home', 'Villa', 370000, 'Ramallah, Palestine', '300 m²', 4, 3, 'https://example.com/images/villa5.jpg', 'Modern duplex villa with private backyard.'),
(115, 'Compact Apartment Near University', 'Apartment', 70000, 'Jerusalem, Palestine', '90 m²', 2, 1, 'https://example.com/images/apartment6.jpg', 'Convenient apartment perfect for students.'),
(116, 'Luxury Resort Villa', 'Villa', 850000, 'Aqaba, Jordan', '500 m²', 7, 6, 'https://example.com/images/villa6.jpg', 'High-end villa located in a luxury resort.'),
(117, 'Industrial Land Plot', 'Land', 300000, 'Industrial Zone, Gaza', '1000 m²', 0, 0, 'https://example.com/images/land5.jpg', 'Land suited for industrial development.'),
(118, 'Seaview Apartment', 'Apartment', 180000, 'Acre, Palestine', '160 m²', 3, 2, 'https://example.com/images/apartment7.jpg', 'Apartment with a direct view of the Mediterranean.'),
(119, 'Private Ranch Villa', 'Villa', 620000, 'Irbid, Jordan', '420 m²', 6, 5, 'https://example.com/images/villa7.jpg', 'Large ranch-style villa with private farmland.'),
(120, 'Green Land for Eco Projects', 'Land', 110000, 'Deir ez-Zor, Syria', '550 m²', 0, 0, 'https://example.com/images/land6.jpg', 'Green land ideal for eco-friendly housing projects.');

-- =====================================================
-- RESERVATIONS
-- =====================================================
-- 100 Reservations with a mixture of statuses (Pending, Confirmed, Cancelled, Completed)
-- Each user will have multiple reservations across different properties
-- Dates are stored as timestamps (milliseconds since epoch)
-- Example: 1751317200000 = 2025-07-01

-- User 1 reservations
INSERT INTO reservations (user_id, property_id, start_date, end_date, total_price, status, creation_date)
VALUES
(1, 101, 1751317200000, 1751749200000, 425, 'Confirmed', 1748896800000),  -- 2025-07-01, 2025-07-05, 2025-06-01
(1, 106, 1753908000000, 1754340000000, 225, 'Pending', 1748983200000),    -- 2025-08-05, 2025-08-10, 2025-06-02
(1, 112, 1757602800000, 1759071600000, 2400, 'Confirmed', 1749069600000), -- 2025-09-15, 2025-09-30, 2025-06-03
(1, 104, 1759589600000, 1760458800000, 1400, 'Cancelled', 1749069600000), -- 2025-10-10, 2025-10-20, 2025-06-03
(1, 109, 1761196800000, 1762060800000, 990, 'Pending', 1749156000000),    -- 2025-11-01, 2025-11-10, 2025-06-04
(1, 115, 1764993600000, 1765943600000, 700, 'Confirmed', 1749242400000),  -- 2025-12-15, 2025-12-25, 2025-06-05
(1, 118, 1767290400000, 1768240400000, 1800, 'Pending', 1749328800000),   -- 2026-01-05, 2026-01-15, 2025-06-06
(1, 101, 1770055200000, 1770487200000, 425, 'Confirmed', 1749415200000),  -- 2026-02-10, 2026-02-15, 2025-06-07
(1, 106, 1772074800000, 1772919600000, 450, 'Pending', 1749501600000),    -- 2026-03-01, 2026-03-10, 2025-06-08
(1, 112, 1776037200000, 1777506000000, 2400, 'Confirmed', 1749501600000); -- 2026-04-15, 2026-04-30, 2025-06-08

-- User 2 reservations
INSERT INTO reservations (user_id, property_id, start_date, end_date, total_price, status, creation_date)
VALUES
(2, 102, 1751749200000, 1752613200000, 4500, 'Confirmed', 1748896800000),  -- 2025-07-15, 2025-07-25, 2025-06-01
(2, 107, 1754340000000, 1755204000000, 3200, 'Pending', 1748983200000),    -- 2025-08-10, 2025-08-20, 2025-06-02
(2, 110, 1756684800000, 1757548800000, 5100, 'Confirmed', 1749069600000),  -- 2025-09-01, 2025-09-10, 2025-06-03
(2, 114, 1759157600000, 1760021600000, 3700, 'Completed', 1749069600000),  -- 2025-10-05, 2025-10-15, 2025-06-03
(2, 116, 1762060800000, 1762924800000, 8500, 'Cancelled', 1749156000000),  -- 2025-11-10, 2025-11-20, 2025-06-04
(2, 119, 1763788800000, 1764652800000, 6200, 'Confirmed', 1749242400000),  -- 2025-12-01, 2025-12-10, 2025-06-05
(2, 102, 1768154400000, 1769018400000, 4500, 'Pending', 1749328800000),    -- 2026-01-15, 2026-01-25, 2025-06-06
(2, 107, 1770487200000, 1771351200000, 3200, 'Confirmed', 1749415200000),  -- 2026-02-05, 2026-02-15, 2025-06-07
(2, 110, 1772919600000, 1773783600000, 5100, 'Pending', 1749501600000),    -- 2026-03-10, 2026-03-20, 2025-06-08
(2, 114, 1775299200000, 1776163200000, 3700, 'Confirmed', 1749501600000);  -- 2026-04-01, 2026-04-10, 2025-06-08

-- User 3 reservations
INSERT INTO reservations (user_id, property_id, start_date, end_date, total_price, status, creation_date)
VALUES
(3, 103, 1751299200000, 1751904000000, 12000, 'Confirmed', 1748896800000), -- 2025-07-01, 2025-07-08, 2025-06-01
(3, 108, 1754340000000, 1754944800000, 9500, 'Pending', 1748983200000),    -- 2025-08-10, 2025-08-17, 2025-06-02
(3, 111, 1757602800000, 1758207600000, 13500, 'Confirmed', 1749069600000), -- 2025-09-15, 2025-09-22, 2025-06-03
(3, 113, 1760458800000, 1761063600000, 25000, 'Cancelled', 1749069600000), -- 2025-10-20, 2025-10-27, 2025-06-03
(3, 117, 1763788800000, 1764393600000, 30000, 'Confirmed', 1749156000000), -- 2025-12-01, 2025-12-08, 2025-06-04
(3, 120, 1766685600000, 1767290400000, 11000, 'Pending', 1749242400000),   -- 2026-01-10, 2026-01-17, 2025-06-05
(3, 103, 1769450400000, 1770055200000, 12000, 'Confirmed', 1749328800000), -- 2026-02-10, 2026-02-17, 2025-06-06
(3, 108, 1772919600000, 1773524400000, 9500, 'Pending', 1749415200000),    -- 2026-03-15, 2026-03-22, 2025-06-07
(3, 111, 1776037200000, 1776642000000, 13500, 'Confirmed', 1749501600000), -- 2026-04-15, 2026-04-22, 2025-06-08
(3, 117, 1778629200000, 1779234000000, 30000, 'Pending', 1749501600000);   -- 2026-05-15, 2026-05-22, 2025-06-08

-- User 4 reservations
INSERT INTO reservations (user_id, property_id, start_date, end_date, total_price, status, creation_date)
VALUES
(4, 104, 1751317200000, 1751749200000, 1400, 'Confirmed', 1748896800000),  -- 2025-07-05, 2025-07-15, 2025-06-01
(4, 109, 1753908000000, 1754340000000, 990, 'Pending', 1748983200000),    -- 2025-08-10, 2025-08-20, 2025-06-02
(4, 115, 1757602800000, 1759071600000, 700, 'Confirmed', 1749069600000),  -- 2025-09-01, 2025-09-10, 2025-06-03
(4, 118, 1760458800000, 1760926800000, 1800, 'Completed', 1749069600000), -- 2025-10-05, 2025-10-15, 2025-06-03
(4, 101, 1762060800000, 1762492800000, 850, 'Cancelled', 1749156000000),  -- 2025-11-10, 2025-11-20, 2025-06-04
(4, 106, 1764993600000, 1765947600000, 450, 'Confirmed', 1749242400000),  -- 2025-12-01, 2025-12-10, 2025-06-05
(4, 112, 1768154400000, 1769018400000, 1600, 'Pending', 1749328800000),    -- 2026-01-15, 2026-01-25, 2025-06-06
(4, 104, 1770487200000, 1770919200000, 1400, 'Confirmed', 1749415200000),  -- 2026-02-05, 2026-02-15, 2025-06-07
(4, 109, 1772919600000, 1773351600000, 990, 'Pending', 1749501600000),    -- 2026-03-10, 2026-03-20, 2025-06-08
(4, 115, 1775299200000, 1776163200000, 700, 'Confirmed', 1749501600000);  -- 2026-04-01, 2026-04-10, 2025-06-08

-- User 5 reservations
INSERT INTO reservations (user_id, property_id, start_date, end_date, total_price, status, creation_date)
VALUES
(5, 105, 1751317200000, 1752613200000, 9000, 'Confirmed', 1748896800000),  -- 2025-07-10, 2025-07-25, 2025-06-01
(5, 110, 1756684800000, 1757548800000, 7650, 'Pending', 1748983200000),    -- 2025-09-01, 2025-09-15, 2025-06-02
(5, 114, 1759157600000, 1760021600000, 5550, 'Confirmed', 1749069600000),  -- 2025-09-10, 2025-09-25, 2025-06-03
(5, 116, 1762060800000, 1762924800000, 12750, 'Cancelled', 1749069600000),  -- 2025-10-01, 2025-10-15, 2025-06-03
(5, 119, 1764993600000, 1765947600000, 9300, 'Confirmed', 1749156000000),  -- 2025-11-10, 2025-11-25, 2025-06-04
(5, 102, 1768154400000, 1769018400000, 6750, 'Pending', 1749242400000),    -- 2026-01-01, 2026-01-15, 2025-06-05
(5, 107, 1770487200000, 1771351200000, 4800, 'Confirmed', 1749328800000),  -- 2026-02-01, 2026-02-15, 2025-06-06
(5, 105, 1772919600000, 1773351600000, 9000, 'Pending', 1749415200000),    -- 2026-03-01, 2026-03-15, 2025-06-07
(5, 110, 1775299200000, 1776163200000, 7650, 'Confirmed', 1749501600000),  -- 2026-04-01, 2026-04-15, 2025-06-08
(5, 114, 1777602000000, 1778246400000, 5550, 'Pending', 1749501600000);    -- 2026-05-01, 2026-05-15, 2025-06-08

-- User 6-10 reservations
-- For brevity, only including 5 users' reservations in this example
-- Add more if needed for your testing
