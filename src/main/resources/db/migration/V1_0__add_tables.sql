create table match
(
    id bigint not null
        constraint match_pkey
            primary key,
    seed bigint,

    conceded_by varchar(255),

    player_1 varchar(255),
    player_2 varchar(255),

    current_player varchar(255),

    score_1 integer,
    score_2 integer,

    is_completed boolean,
    is_ready boolean,
    is_started boolean,

    created_by varchar(50) not null,
    created_date timestamp,
    last_modified_by varchar(50),
    last_modified_date timestamp
);

alter table match owner to matches;

create index player_1_index
    on match (player_1);

create index player_2_index
    on match (player_2);

