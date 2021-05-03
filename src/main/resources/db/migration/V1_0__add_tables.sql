create table AUTHORITY
(
    NAME VARCHAR(50) not null
        primary key
);

create table COMMAND
(
    ID         BIGINT not null
        primary key,
    PLAYER     VARCHAR(255),
    PHASE      INTEGER,
    DISCARD    BOOLEAN,
    DRAW       BOOLEAN,
    MATCH_ID   BIGINT,
    COLOR      INTEGER,
    CARD_COLOR INTEGER,
    CARD_VALUE INTEGER
);

create index MATCH_ID
    on COMMAND (MATCH_ID);

create table USER
(
    ID                 BIGINT      not null
        primary key,
    EMAIL              VARCHAR(255),
    LOGIN              VARCHAR(255),
    PASSWORD_HASH      VARCHAR(60) not null,
    FIRST_NAME         VARCHAR(50) not null,
    LAST_NAME          VARCHAR(50),
    IMAGE_URL          VARCHAR(256),
    LANG_KEY           VARCHAR(10),
    ACTIVATED          BOOLEAN     not null,
    ACTIVATION_KEY     VARCHAR(20),
    RESET_DATE         TIMESTAMP,
    RESET_KEY          VARCHAR(20),
    CREATED_BY         BIGINT,
    CREATED_DATE       TIMESTAMP,
    LAST_MODIFIED_BY   VARCHAR(50),
    LAST_MODIFIED_DATE TIMESTAMP
);

create table MATCH
(
    ID                 BIGINT not null
        primary key,
    SEED               BIGINT,
    PLAYER_1           BIGINT not null,
    PLAYER_2           BIGINT,
    CONCEDED_BY        BIGINT,
    SCORE_1            INTEGER,
    SCORE_2            INTEGER,
    IS_COMPLETED       BOOLEAN,
    IS_READY           BOOLEAN,
    IS_STARTED         BOOLEAN,
    CREATED_BY         BIGINT,
    CREATED_DATE       TIMESTAMP,
    LAST_MODIFIED_BY   VARCHAR(50),
    LAST_MODIFIED_DATE TIMESTAMP,
    constraint CONCEDED_BY_FOREIGN_KEY
        foreign key (CONCEDED_BY) references USER,
    constraint PLAYER_1_FOREIGN_KEY
        foreign key (PLAYER_1) references USER,
    constraint PLAYER_2_FOREIGN_KEY
        foreign key (PLAYER_2) references USER
);

create index PLAYER_1_INDEX
    on MATCH (PLAYER_1);

create index PLAYER_2_INDEX
    on MATCH (PLAYER_2);

create table USER_AUTHORITY
(
    USER_ID        BIGINT      not null,
    AUTHORITY_NAME VARCHAR(50) not null,
    primary key (USER_ID, AUTHORITY_NAME),
    constraint AUTHORITY_FOREIGN_KEY
        foreign key (AUTHORITY_NAME) references AUTHORITY,
    constraint AUTHORITY_NAME_FOREIGN_KEY
        foreign key (AUTHORITY_NAME) references AUTHORITY,
    constraint USER_AUTHORITY_FOREIGN_KEY
        foreign key (USER_ID) references USER
);

create sequence SEQUENCE_GENERATOR
    increment by 50;