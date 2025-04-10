CREATE TABLE Books (
    ID SERIAL,
    title VARCHAR(64) NOT NULL,
    author VARCHAR(31) NOT NULL,
    description VARCHAR,
    pages INT NOT NULL,
    price DECIMAL DEFAULT 0,
    availability INT DEFAULT 0,
    ISBN VARCHAR(31),

    PRIMARY KEY(ID)
);

CREATE TABLE Genres (
    book_ID INT,
    genre VARCHAR(31) NOT NULL,

    PRIMARY KEY(book_ID, genre),
    FOREIGN KEY (book_ID) REFERENCES Books(ID)
);



CREATE TABLE Users (
    ID SERIAL,
    email VARCHAR(64) NOT NULL,
    username VARCHAR(31) NOT NULL,
    hash_password VARCHAR(63) NOT NULL,
    role VARCHAR(15) NOT NULL,
    firstName VARCHAR(31) NOT NULL,
    lastName VARCHAR(31) NOT NULL,
    signup_date DATE NOT NULL,

    PRIMARY KEY(ID)
);

CREATE TABLE Favourites (
    user_ID INT,
    book_ID INT,

    PRIMARY KEY(user_ID, book_ID),
    FOREIGN KEY (user_ID) REFERENCES Users(ID),
    FOREIGN KEY (book_ID) REFERENCES Books(ID)
);

CREATE TABLE Orders (
    ID SERIAL,
    user_ID INT NOT NULL,
    total_amount DECIMAL NOT NULL,
    status VARCHAR(15) NOT NULL,
    order_date DATE NOT NULL,

    PRIMARY KEY(ID),
    FOREIGN KEY (user_ID) REFERENCES Users(ID)
);

CREATE TABLE OrderItems (
    order_ID INT,
    book_ID INT,
    quantity INT DEFAULT 1,

    PRIMARY KEY(order_ID, book_ID),
    FOREIGN KEY (order_ID) REFERENCES Orders(ID),
    FOREIGN KEY (book_ID) REFERENCES Books(ID)
);


CREATE TABLE Carts (
    ID SERIAL,
    user_ID INT NOT NULL,

    PRIMARY KEY(ID),
    FOREIGN KEY (user_ID) REFERENCES Users(ID)
);

CREATE TABLE CartItems (
    cart_ID INT,
    user_ID INT,
    quantity INT DEFAULT 1,

    PRIMARY KEY(cart_ID, user_ID),
    FOREIGN KEY (cart_ID) REFERENCES Carts(ID),
    FOREIGN KEY (user_ID) REFERENCES Users(ID)
);