-- create_tables.sql
CREATE TABLE players (
                         id UUID PRIMARY KEY,
                         available_money INT
);

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE bets (
                      id UUID DEFAULT uuid_generate_v4() PRIMARY KEY,
                      bet_type VARCHAR,
                      player_id UUID,
                      bet_number INTEGER,
                      bet_odd_or_even VARCHAR,
                      bet_color VARCHAR,
                      bet_amount INTEGER,
                      random_number INTEGER
);
