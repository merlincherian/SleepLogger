CREATE TABLE sleep_logs (
    id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL REFERENCES users(id),
    sleep_date DATE NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    total_time_in_bed INTERVAL NOT NULL,
    feeling VARCHAR(10) CHECK (feeling IN ('BAD', 'OK', 'GOOD')) NOT NULL
);

-- Create Sleep Logs for user1
INSERT INTO sleep_logs (user_id, sleep_date, start_time, end_time, total_time_in_bed, feeling) VALUES
(1, CURRENT_DATE - INTERVAL '1 day', '22:00:00', '06:00:00', 'PT8H', 'GOOD');

-- Create Sleep Logs for user1
INSERT INTO sleep_logs (user_id, sleep_date, start_time, end_time, total_time_in_bed, feeling) VALUES
(1, CURRENT_DATE, '20:00:00', '06:30:00', 'PT10H30M', 'GOOD');

-- Create Sleep Logs for user1
INSERT INTO sleep_logs (user_id, sleep_date, start_time, end_time, total_time_in_bed, feeling) VALUES
(1, CURRENT_DATE - INTERVAL '3 day', '20:00:00', '05:00:00', 'PT9H', 'OK');

INSERT INTO sleep_logs (user_id, sleep_date, start_time, end_time, total_time_in_bed, feeling) VALUES
(1, CURRENT_DATE - INTERVAL '4 day', '23:00:00', '03:00:00', 'PT4H', 'BAD');

-- Create Sleep Logs for user1
INSERT INTO sleep_logs (user_id, sleep_date, start_time, end_time, total_time_in_bed, feeling) VALUES
(1, CURRENT_DATE, '20:00:00', '06:30:00', 'PT8H', 'GOOD');

-- Create Sleep Logs for user1
INSERT INTO sleep_logs (user_id, sleep_date, start_time, end_time, total_time_in_bed, feeling) VALUES
(1, CURRENT_DATE - INTERVAL '2 day', '20:00:00', '05:00:00', 'PT8H', 'OK');

-- Create Sleep Logs for user2
INSERT INTO sleep_logs (user_id, sleep_date, start_time, end_time, total_time_in_bed, feeling) VALUES
(2, CURRENT_DATE - INTERVAL '1 day', '22:30:00', '06:30:00', 'PT8H', 'BAD');