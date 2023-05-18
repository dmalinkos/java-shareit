-- DROP TABLE IF EXISTS users CASCADE;
-- DROP TABLE IF EXISTS items CASCADE;
-- DROP TABLE IF EXISTS bookings CASCADE;
-- DROP TABLE IF EXISTS comments CASCADE;
-- DROP TABLE IF EXISTS requests CASCADE;

CREATE TABLE IF NOT EXISTS users
(
    user_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name    VARCHAR(500) NOT NULL,
    email   VARCHAR(500) NOT NULL,
    UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS items
(
    item_id      BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name         VARCHAR(1000) NOT NULL,
    description  VARCHAR(1000) NOT NULL,
    is_available BOOLEAN,
    owner_id     BIGINT        NOT NULL,
    request_id   BIGINT,
    CONSTRAINT fk_items_to_users FOREIGN KEY (owner_id) REFERENCES users (user_id)
);

CREATE TABLE IF NOT EXISTS bookings
(
    booking_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    start_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    end_date   TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    item_id    BIGINT,
    booker_id  BIGINT,
    status     VARCHAR(10),
    CONSTRAINT fk_bookings_to_items FOREIGN KEY (item_id) REFERENCES items (item_id),
    CONSTRAINT fk_bookings_to_users FOREIGN KEY (booker_id) REFERENCES users (user_id)
);

CREATE TABLE IF NOT EXISTS comments
(
    comment_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    text       VARCHAR(5000),
    item_id    BIGINT,
    author_id  BIGINT,
    CONSTRAINT fk_comments_to_items FOREIGN KEY (item_id) REFERENCES items (item_id),
    CONSTRAINT fk_comments_to_users FOREIGN KEY (author_id) REFERENCES users (user_id)
);

CREATE TABLE IF NOT EXISTS requests
(
    request_id   BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    description  VARCHAR(5000),
    requestor_id BIGINT,
    CONSTRAINT fk_requests_to_users FOREIGN KEY (requestor_id) REFERENCES users (user_id)
);
