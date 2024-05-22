CREATE DATABASE image_scaling;

USE image_scaling;

CREATE TABLE user (
    username VARCHAR(255) PRIMARY KEY,
    password VARCHAR(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE image (
    id VARCHAR(255) PRIMARY KEY,
    name VARCHAR(255) NULL,
    uploadAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    user_id VARCHAR(255),
    FOREIGN KEY (user_id) REFERENCES user(username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO user (username, password) VALUES
('user1', '11111111'),
('user2', '22222222'),
('user3', '33333333'),
('user4', '44444444'),
('user5', '55555555');