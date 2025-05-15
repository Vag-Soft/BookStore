INSERT INTO Books (title, author, description, pages, price, availability, ISBN)
VALUES
    ('The Lord of the Rings', 'J. R. R. Tolkien', 'The Lord of the Rings is a series of three fantasy novels written by English author and scholar J. R. R. Tolkien.', 1178, 15, 5, '978-0-395-36381-0'),
    ('Harry Potter and the Philosopher''s Stone', 'J. K. Rowling', 'Harry Potter and the Philosopher''s Stone is a fantasy novel written by British author J. K. Rowling.', 223, 20, 10, '978-0-7-152-20664-5'),
    ('Harry Potter and the Chamber of Secrets', 'J. K. Rowling', 'Harry Potter and the Chamber of Secrets is a fantasy novel written by British author J. K. Rowling.', 251, 20, 2, '978-0-7-152-20665-2'),
    ('Pride and Prejudice', 'Jane Austen', 'Pride and Prejudice is a romantic novel written by English author Jane Austen.', 272, 15, 4, '978-0-14-143951-8'),
    ('To Kill a Mockingbird', 'Harper Lee', 'To Kill a Mockingbird is a novel written by American author Harper Lee.', 281, 10, 2, '978-0-446-31106-2'),
    ('The Great Gatsby', 'F. Scott Fitzgerald', 'The Great Gatsby is a novel written by American author F. Scott Fitzgerald.', 180, 13, 1, '978-0-7432-7356-5');


INSERT INTO Users (email, username, hashPassword, role, firstName, lastName, signupDate)
VALUES
    ('john.doe@example.com', 'john.doe', '$2a$10$1Mn20VjeC1Gvpbei3cFHdOq.FU1AQgrWvmi6R3Kbqxlos1d.La9OO', 'USER', 'John', 'Doe', '2023-04-28'),-- hashed from 'SecurePass123!'
    ('admin.jones@bookstore.com', 'admin.jones', '$2a$10$3YYtS3FDrzxWxYcN.VB2NevDPDqLsouWAiLgGXhkN6.t5dcDJbkOK', 'ADMIN', 'Robert', 'Jones', '2020-03-15'), -- hashed from 'AdminPass789!'
    ('admin@bookstore.com', 'admin', '$2a$10$6T849dwtDHzHr.VsO/yUHuuvHe4zAamoNf20C9/Zuj2cWmHJ6oZIS', 'ADMIN', 'Mr', 'Admin', '2020-03-15'), -- hashed from 'admin'
    ('user@bookstore.com', 'user', '$2a$10$zpqetztH8zDi7QTKNVoyGOYJ9J8kvNjDGujwMsiWuaq1OtYIH5BvK' , 'USER', 'Urs', 'User', '2020-03-05'); -- hashed from 'user'


INSERT INTO Favourites (userID, bookID)
VALUES
    (1, 1),
    (1, 2),
    (3, 5),
    (4, 3),
    (4, 4),
    (4, 5),
    (4, 6);


INSERT INTO Genres (bookID, genre)
VALUES
    (1, 'Fantasy'),
    (1, 'Adventure'),
    (2, 'Fantasy'),
    (2, 'Young Adult'),
    (3, 'Fantasy'),
    (3, 'Young Adult'),
    (4, 'Romance'),
    (4, 'Classic'),
    (5, 'Classic'),
    (6, 'Literary Fiction');


INSERT INTO Carts (userID)
VALUES
    (1), (2), (3), (4);

INSERT INTO CartItems (cartID, userID, quantity)
VALUES
    (1, 1, 1),
    (1, 2, 1),
    (3, 4, 1),
    (4, 2, 3);

INSERT INTO Orders (userID, totalAmount, status, orderDate)
VALUES
    (1, 20.0, 'Delivered', '2022-01-15');

INSERT INTO OrderItems (orderID, bookID, quantity)
VALUES
    (1, 4, 1),
    (1, 5, 1);