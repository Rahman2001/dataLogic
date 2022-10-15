CREATE TABLE IF NOT EXISTS current_weather
(id VARCHAR(25) NOT NULL PRIMARY KEY ,
country VARCHAR(25),
temp INT,
temp_min INT,
temp_max INT,
feels_like INT,
weather_description VARCHAR(30),
pressure INT,
humidity INT,
wind INT,
clouds INT,

CONSTRAINT fr_key FOREIGN KEY (id) REFERENCES locations(city)
ON DELETE CASCADE ON UPDATE CASCADE );