create table ducks (
    id         uuid          constraint ducks_pkey primary key,
    name       varchar(255),
    height     integer,
    created_at timestamp
);
