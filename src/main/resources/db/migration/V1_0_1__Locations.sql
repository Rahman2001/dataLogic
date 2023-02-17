DROP TABLE IF EXISTS locations;

CREATE TABLE IF NOT EXISTS locations
(city VARCHAR(25) NOT NULL PRIMARY KEY ,
country VARCHAR(25),
updated_time DATETIME DEFAULT NOW()
);
# INSERT INTO locations (city, country) VALUES ("Ankara", "Turkey");