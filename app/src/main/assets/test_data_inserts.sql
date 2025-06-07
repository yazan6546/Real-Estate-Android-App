INSERT INTO users (email, first_name, last_name, password, gender, country, city, phone, is_admin, profile_image)
VALUES
('john.smith@example.com', 'John', 'Smith', 'password123', 'Male', 'Jordan', 'Amman', '789123456', 0, NULL),
('sara.ahmad@example.com', 'Sara', 'Ahmad', 'securePwd456!', 'Female', 'Palestine', 'Ramallah', '599123456', 0, NULL),
('mohamed.ali@example.com', 'Mohamed', 'Ali', 'userPass789@', 'Male', 'UAE', 'Dubai', '501234567', 0, NULL),
('layla.hassan@example.com', 'Layla', 'Hassan', 'laylaPass2023!', 'Female', 'Jordan', 'Irbid', '778456789', 0, NULL),
('omar.khalid@example.com', 'Omar', 'Khalid', 'omar2023Secure!', 'Male', 'Palestine', 'Nablus', '592345678', 0, NULL),
('admin.user@realestate.com', 'Admin', 'User', 'adminSecure123!', 'Male', 'Jordan', 'Al Karak', '777888999', 0, NULL),
('nour.admin@realestate.com', 'Nour', 'Admin', 'nourAdmin456!', 'Female', 'Palestine', 'Tulkarem', '598765432', 0, NULL),
('ahmad.manager@realestate.com', 'Ahmad', 'Manager', 'ahmadSuper789!', 'Male', 'UAE', 'Abu Dhabi', '502345678', 0, NULL),
('rania.admin@realestate.com', 'Rania', 'Admin', 'rania2023Pwd!', 'Female', 'Jordan', 'Irbid', '790123456', 0, NULL),
('zaid.super@realestate.com', 'Zaid', 'Super', 'zaidMaster2023!', 'Male', 'UAE', 'Sharjah', '503456789', 0, NULL);

INSERT INTO properties (property_id, title, type, price, location, area, bedrooms, bathrooms, image, description, is_featured, discount)
VALUES
(101, 'Modern 2-Bedroom Apartment', 'Apartment', 85000, 'Ramallah, Palestine', '120 m²', 2, 2, 'https://example.com/images/apartment1.jpg', 'Beautiful apartment with city view, close to shopping malls.', 1, 0),
(102, 'Luxury Family Villa', 'Villa', 450000, 'Amman, Jordan', '350 m²', 5, 4, 'https://example.com/images/villa1.jpg', 'Spacious villa with private pool and large garden.', 0, 0),
(103, 'Residential Land Plot', 'Land', 120000, 'Aleppo, Syria', '500 m²', 0, 0, 'https://example.com/images/land1.jpg', 'Prime land suitable for residential development.', 0, 0),
(104, 'Penthouse Apartment with Roof Access', 'Apartment', 140000, 'Nablus, Palestine', '180 m²', 3, 3, 'https://example.com/images/apartment2.jpg', 'Luxury penthouse with rooftop terrace.', 1, 0),
(105, 'Beachside Villa', 'Villa', 600000, 'Latakia, Syria', '400 m²', 6, 5, 'https://example.com/images/villa2.jpg', 'Elegant beachfront villa with panoramic sea views.', 1, 0),
(106, 'Compact Studio Apartment', 'Apartment', 45000, 'Gaza, Palestine', '50 m²', 1, 1, 'https://example.com/images/apartment3.jpg', 'Affordable studio perfect for singles or students.', 0, 5),
(107, 'Country Style Villa', 'Villa', 320000, 'Zarqa, Jordan', '270 m²', 4, 3, 'https://example.com/images/villa3.jpg', 'Villa with large outdoor area and rustic charm.', 0, 0),
(108, 'Agricultural Land', 'Land', 95000, 'Homs, Syria', '700 m²', 0, 0, 'https://example.com/images/land2.jpg', 'Land suitable for farming and agriculture.', 0, 0),
(109, 'Family Apartment with Garden View', 'Apartment', 99000, 'Bethlehem, Palestine', '140 m²', 3, 2, 'https://example.com/images/apartment4.jpg', 'Spacious apartment with access to a shared garden.', 0, 0),
(110, 'Mountain View Villa', 'Villa', 510000, 'Ajloun, Jordan', '380 m²', 5, 4, 'https://example.com/images/villa4.jpg', 'Villa with stunning mountain views.', 1, 0),
(111, 'Urban Residential Land', 'Land', 135000, 'Damascus, Syria', '450 m²', 0, 0, 'https://example.com/images/land3.jpg', 'Land located near city amenities and services.', 0, 0),
(112, 'Luxury City Apartment', 'Apartment', 160000, 'Amman, Jordan', '200 m²', 3, 3, 'https://example.com/images/apartment5.jpg', 'Modern apartment in the city center.', 1, 10),
(113, 'Seaside Land Plot', 'Land', 250000, 'Latakia, Syria', '600 m²', 0, 0, 'https://example.com/images/land4.jpg', 'Land close to the beach, ideal for resorts.', 0, 0),
(114, 'Duplex Family Home', 'Villa', 370000, 'Ramallah, Palestine', '300 m²', 4, 3, 'https://example.com/images/villa5.jpg', 'Modern duplex villa with private backyard.', 0, 5),
(115, 'Compact Apartment Near University', 'Apartment', 70000, 'Jerusalem, Palestine', '90 m²', 2, 1, 'https://example.com/images/apartment6.jpg', 'Convenient apartment perfect for students.', 0, 0),
(116, 'Luxury Resort Villa', 'Villa', 850000, 'Aqaba, Jordan', '500 m²', 7, 6, 'https://example.com/images/villa6.jpg', 'High-end villa located in a luxury resort.', 1, 0),
(117, 'Industrial Land Plot', 'Land', 300000, 'Industrial Zone, Gaza', '1000 m²', 0, 0, 'https://example.com/images/land5.jpg', 'Land suited for industrial development.', 0, 0),
(118, 'Seaview Apartment', 'Apartment', 180000, 'Acre, Palestine', '160 m²', 3, 2, 'https://example.com/images/apartment7.jpg', 'Apartment with a direct view of the Mediterranean.', 1, 0),
(119, 'Private Ranch Villa', 'Villa', 620000, 'Irbid, Jordan', '420 m²', 6, 5, 'https://example.com/images/villa7.jpg', 'Large ranch-style villa with private farmland.', 0, 0),
(120, 'Green Land for Eco Projects', 'Land', 110000, 'Deir ez-Zor, Syria', '550 m²', 0, 0, 'https://example.com/images/land6.jpg', 'Green land ideal for eco-friendly housing projects.', 0, 0);

INSERT INTO reservations (email, property_id, start_date, end_date, status)
VALUES
('john.smith@example.com', 101, 1751317200000, 1751749200000, 'Confirmed'),
('john.smith@example.com', 106, 1753908000000, 1754340000000, 'Pending'),
('john.smith@example.com', 112, 1757602800000, 1759071600000, 'Confirmed'),
('john.smith@example.com', 104, 1759589600000, 1760458800000, 'Cancelled'),
('john.smith@example.com', 109, 1761196800000, 1762060800000, 'Pending'),
('john.smith@example.com', 115, 1764993600000, 1765943600000, 'Confirmed'),
('john.smith@example.com', 118, 1767290400000, 1768240400000, 'Pending'),
('john.smith@example.com', 101, 1770055200000, 1770487200000, 'Confirmed'),
('john.smith@example.com', 106, 1772074800000, 1772919600000, 'Pending'),
('john.smith@example.com', 112, 1776037200000, 1777506000000, 'Confirmed');

INSERT INTO reservations (email, property_id, start_date, end_date, status)
VALUES
('sara.ahmad@example.com', 102, 1751749200000, 1752613200000, 'Confirmed'),
('sara.ahmad@example.com', 107, 1754340000000, 1755204000000, 'Pending'),
('sara.ahmad@example.com', 110, 1756684800000, 1757548800000, 'Confirmed'),
('sara.ahmad@example.com', 114, 1759157600000, 1760021600000, 'Completed'),
('sara.ahmad@example.com', 116, 1762060800000, 1762924800000, 'Cancelled'),
('sara.ahmad@example.com', 119, 1763788800000, 1764652800000, 'Confirmed'),
('sara.ahmad@example.com', 102, 1768154400000, 1769018400000, 'Pending'),
('sara.ahmad@example.com', 107, 1770487200000, 1771351200000, 'Confirmed'),
('sara.ahmad@example.com', 110, 1772919600000, 1773783600000, 'Pending'),
('sara.ahmad@example.com', 114, 1775299200000, 1776163200000, 'Confirmed');

INSERT INTO reservations (email, property_id, start_date, end_date, status)
VALUES
('mohamed.ali@example.com', 103, 1751299200000, 1751904000000, 'Confirmed'),
('mohamed.ali@example.com', 108, 1754340000000, 1754944800000, 'Pending'),
('mohamed.ali@example.com', 111, 1757602800000, 1758207600000, 'Confirmed'),
('mohamed.ali@example.com', 113, 1760458800000, 1761063600000, 'Cancelled'),
('mohamed.ali@example.com', 117, 1763788800000, 1764393600000, 'Confirmed'),
('mohamed.ali@example.com', 120, 1766685600000, 1767290400000, 'Pending'),
('mohamed.ali@example.com', 103, 1769450400000, 1770055200000, 'Confirmed'),
('mohamed.ali@example.com', 108, 1772919600000, 1773524400000, 'Pending'),
('mohamed.ali@example.com', 111, 1776037200000, 1776642000000, 'Confirmed'),
('mohamed.ali@example.com', 117, 1778629200000, 1779234000000, 'Pending');

INSERT INTO reservations (email, property_id, start_date, end_date, status)
VALUES
('layla.hassan@example.com', 104, 1751317200000, 1751749200000, 'Confirmed'),
('layla.hassan@example.com', 109, 1753908000000, 1754340000000, 'Pending'),
('layla.hassan@example.com', 115, 1757602800000, 1759071600000, 'Confirmed'),
('layla.hassan@example.com', 118, 1760458800000, 1760926800000, 'Completed'),
('layla.hassan@example.com', 101, 1762060800000, 1762492800000, 'Cancelled'),
('layla.hassan@example.com', 106, 1764993600000, 1765947600000, 'Confirmed'),
('layla.hassan@example.com', 112, 1768154400000, 1769018400000, 'Pending'),
('layla.hassan@example.com', 104, 1770487200000, 1770919200000, 'Confirmed'),
('layla.hassan@example.com', 109, 1772919600000, 1773351600000, 'Pending'),
('layla.hassan@example.com', 115, 1775299200000, 1776163200000, 'Confirmed');

INSERT INTO reservations (email, property_id, start_date, end_date, status)
VALUES
('omar.khalid@example.com', 105, 1751317200000, 1752613200000, 'Confirmed'),
('omar.khalid@example.com', 110, 1756684800000, 1757548800000, 'Pending'),
('omar.khalid@example.com', 114, 1759157600000, 1760021600000, 'Confirmed'),
('omar.khalid@example.com', 116, 1762060800000, 1762924800000, 'Cancelled'),
('omar.khalid@example.com', 119, 1764993600000, 1765947600000, 'Confirmed'),
('omar.khalid@example.com', 102, 1768154400000, 1769018400000, 'Pending'),
('omar.khalid@example.com', 107, 1770487200000, 1771351200000, 'Confirmed'),
('omar.khalid@example.com', 105, 1772919600000, 1773351600000, 'Pending'),
('omar.khalid@example.com', 110, 1775299200000, 1776163200000, 'Confirmed'),
('omar.khalid@example.com', 114, 1777602000000, 1778246400000, 'Pending');
