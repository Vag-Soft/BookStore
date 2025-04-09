CREATE TABLE Books (
    ID INT,
    title VARCHAR(64),
    author VARCHAR(31),
    description VARCHAR,
    pages INT,
    price DECIMAL,
    availability INT DEFAULT 0,
    ISBN VARCHAR(31),

    PRIMARY KEY(ID)
);

CREATE TABLE Genres (
    book_ID INT,
    genre VARCHAR(31),

    PRIMARY KEY(book_ID, genre),
    FOREIGN KEY (book_ID) REFERENCES Books(ID)
);



CREATE TABLE Users (
    ID INT,
    email VARCHAR(64),
    username VARCHAR(31),
    hash_password VARCHAR(63),
    role VARCHAR(15),
    firstName VARCHAR(31),
    lastName VARCHAR(31),
    signup_date DATE,

    PRIMARY KEY(ID)
);