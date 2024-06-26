-- Create the database if it doesn't exist
CREATE DATABASE IF NOT EXISTS image_scaling;

-- Switch to the image_scaling database
USE image_scaling;

-- Create the user table
CREATE TABLE IF NOT EXISTS user (
    username VARCHAR(255) PRIMARY KEY,
    password VARCHAR(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Create the image table with the status column
CREATE TABLE IF NOT EXISTS image (
    id INT AUTO_INCREMENT PRIMARY KEY,
    url LONGBLOB,
    uploadAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    user_id VARCHAR(255),
    status VARCHAR(20) DEFAULT 'WAITING' NOT NULL, -- Added status column
    FOREIGN KEY (user_id) REFERENCES user(username)
        ON DELETE CASCADE
        ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Insert sample data into the user table
INSERT INTO user (username, password) VALUES
    ('user1', '11111111'),
    ('user2', '22222222'),
    ('user3', '33333333'),
    ('user4', '44444444'),
    ('user5', '55555555')
ON DUPLICATE KEY UPDATE password=VALUES(password);
