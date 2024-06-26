DROP TABLE IF EXISTS comments;
DROP TABLE IF EXISTS bookings;
DROP TABLE IF EXISTS items;
DROP TABLE IF EXISTS requests;
DROP TABLE IF EXISTS users;

CREATE TABLE users (
  id bigint GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  name varchar NOT NULL,
  email varchar NOT NULL,
  CONSTRAINT unique_email UNIQUE (email)
);

CREATE TABLE requests (
  id bigint GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  name varchar,
  description varchar NOT NULL,
  requestor_id bigint,
  created TIMESTAMP WITHOUT TIME ZONE,
  available bool,
  FOREIGN KEY (requestor_id) REFERENCES users(id)
);

CREATE TABLE items (
  id bigint GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  name varchar NOT NULL,
  description varchar NOT NULL,
  available bool,
  owner_id bigint NOT NULL,
  request_id bigint,
  FOREIGN KEY (owner_id) REFERENCES users(id),
  FOREIGN KEY (request_id) REFERENCES requests(id)
);

CREATE TABLE bookings (
  id bigint GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  start_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  end_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  item_id bigint NOT NULL,
  booker_id bigint NOT NULL,
  status varchar NOT NULL,
  FOREIGN KEY (item_id) REFERENCES items(id),
  FOREIGN KEY (booker_id) REFERENCES users(id)
);

CREATE TABLE comments (
  id bigint GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  text varchar NOT NULL,
  item_id bigint NOT NULL,
  author_id bigint NOT NULL,
  created TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  FOREIGN KEY (item_id) REFERENCES items(id),
  FOREIGN KEY (author_id) REFERENCES users(id)
);