-- Demo data for interview/demo purposes.
-- Only applied when the "seed" Flyway location is active (see application-seeded.yml).

INSERT INTO users (name, email, password_hash, role) VALUES
    ('Anna Kowalska', 'anna.kowalska@example.com', 'c3058cf20d5d81b57b8b09ff583006ebc93ecb18552b0a79b9b9adda1d297319', 'USER'),
    ('Piotr Nowak', 'piotr.nowak@example.com', '5e7d8025d4fbd13b43891bf50262ff6380adbd49ea197ef0ebc7e9f7bac58933', 'USER'),
    ('Maria Wisniewska', 'maria.wisniewska@example.com', '1999c5fe3a31136011af2074fd2474d5ac9f97fdd1b0d9c0629dbb62881a8ea0', 'USER'),
    ('Tomasz Zielinski', 'tomasz.zielinski@example.com', '560e8afb3f8d3bd3f80962c00b1ffd231c51652f63e84d89f3c3ad0ba3645645', 'USER');

INSERT INTO movies (title, genre, available) VALUES
    ('The Matrix', 'Science Fiction', TRUE),
    ('Inception', 'Science Fiction', TRUE),
    ('Blade Runner 2049', 'Science Fiction', FALSE),
    ('The Godfather', 'Crime', TRUE),
    ('Goodfellas', 'Crime', TRUE),
    ('Pulp Fiction', 'Crime', FALSE),
    ('Spirited Away', 'Animation', TRUE),
    ('Toy Story', 'Animation', TRUE),
    ('Pan Tadeusz', 'Drama', TRUE),
    ('Schindler''s List', 'Drama', FALSE),
    ('The Shawshank Redemption', 'Drama', TRUE),
    ('Die Hard', 'Action', FALSE),
    ('Mad Max: Fury Road', 'Action', TRUE),
    ('John Wick', 'Action', TRUE),
    ('The Shining', 'Horror', TRUE),
    ('Get Out', 'Horror', FALSE),
    ('La La Land', 'Musical', TRUE),
    ('Coco', 'Animation', TRUE),
    ('Parasite', 'Thriller', TRUE),
    ('No Country for Old Men', 'Thriller', FALSE);


INSERT INTO rentals (user_id, movie_id, rented_at, due_date, returned_at, reminder_sent) VALUES
    -- Active rentals: movie is currently unavailable, not yet returned.
    (1, 3,  CURRENT_TIMESTAMP - INTERVAL '5 days',  CURRENT_TIMESTAMP + INTERVAL '2 days',  NULL, FALSE),
    (2, 6,  CURRENT_TIMESTAMP - INTERVAL '3 days',  CURRENT_TIMESTAMP + INTERVAL '4 days',  NULL, FALSE),
    (3, 12, CURRENT_TIMESTAMP - INTERVAL '1 day',   CURRENT_TIMESTAMP + INTERVAL '6 days',  NULL, FALSE),

    -- Active and overdue: due_date already in the past, still not returned.
    (4, 16, CURRENT_TIMESTAMP - INTERVAL '12 days', CURRENT_TIMESTAMP - INTERVAL '5 days',  NULL, FALSE),
    (1, 20, CURRENT_TIMESTAMP - INTERVAL '10 days', CURRENT_TIMESTAMP - INTERVAL '3 days',  NULL, TRUE),

    -- Active, reminder already sent, due soon (not yet overdue).
    (2, 10, CURRENT_TIMESTAMP - INTERVAL '12 days', CURRENT_TIMESTAMP + INTERVAL '1 day',   NULL, TRUE),

    -- Returned rentals: movie is available again, history of past completed rentals.
    (3, 1,  CURRENT_TIMESTAMP - INTERVAL '20 days', CURRENT_TIMESTAMP - INTERVAL '13 days', CURRENT_TIMESTAMP - INTERVAL '14 days', FALSE),
    (4, 2,  CURRENT_TIMESTAMP - INTERVAL '18 days', CURRENT_TIMESTAMP - INTERVAL '11 days', CURRENT_TIMESTAMP - INTERVAL '12 days', FALSE),
    (1, 7,  CURRENT_TIMESTAMP - INTERVAL '15 days', CURRENT_TIMESTAMP - INTERVAL '8 days',  CURRENT_TIMESTAMP - INTERVAL '10 days', FALSE),
    (2, 9,  CURRENT_TIMESTAMP - INTERVAL '25 days', CURRENT_TIMESTAMP - INTERVAL '18 days', CURRENT_TIMESTAMP - INTERVAL '20 days', FALSE),

    -- Returned late: was overdue before being returned (returned_at after due_date).
    (3, 17, CURRENT_TIMESTAMP - INTERVAL '22 days', CURRENT_TIMESTAMP - INTERVAL '15 days', CURRENT_TIMESTAMP - INTERVAL '11 days', TRUE);