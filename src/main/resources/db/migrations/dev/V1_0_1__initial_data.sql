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
    ('jane.smith@example.com', 'janesmith', 'hashed_password_value', 'User', 'Jane', 'Smith', '2022-01-05'),
    ('bob.johnson@example.com', 'bobjohnson', 'hashed_password_value', 'Admin', 'Bob', 'Johnson', '2022-01-10'),
    ('alice.williams@example.com', 'alicewilliams', 'hashed_password_value', 'User', 'Alice', 'Williams', '2022-01-15'),
    ('mike.davis@example.com', 'mikedavis', 'hashed_password_value', 'Admin', 'Mike', 'Davis', '2022-01-20');


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