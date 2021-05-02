create table AUTHORITY
(
    NAME VARCHAR(50) not null
        primary key
);

create table COMMAND
(
    ID BIGINT not null
        primary key,
    CARD_COLOR INTEGER,
    CARD_VALUE INTEGER,
    COLOR INTEGER,
    DISCARD BOOLEAN,
    DRAW BOOLEAN,
    GAME_ID BIGINT,
    PHASE INTEGER,
    PLAYER_ID BIGINT
);

create index GAME_ID
	on COMMAND (GAME_ID);

create table USER
(
    ID BIGINT not null
        primary key,
    EMAIL VARCHAR(255),
    LOGIN VARCHAR(255),
    CREATED_BY VARCHAR(50) not null,
    CREATED_DATE TIMESTAMP,
    LAST_MODIFIED_BY VARCHAR(50),
    LAST_MODIFIED_DATE TIMESTAMP,
    ACTIVATED BOOLEAN not null,
    ACTIVATION_KEY VARCHAR(20),
    FIRST_NAME VARCHAR(50),
    IMAGE_URL VARCHAR(256),
    LANG_KEY VARCHAR(10),
    LAST_NAME VARCHAR(50),
    PASSWORD_HASH VARCHAR(60) not null,
    RESET_DATE TIMESTAMP,
    RESET_KEY VARCHAR(20)
);

create table MATCH_ENTITY
(
    ID BIGINT not null
        primary key,
    CREATED_BY BIGINT,
    CREATED_DATE TIMESTAMP,
    IS_COMPLETED BOOLEAN,
    IS_READY BOOLEAN,
    IS_STARTED BOOLEAN,
    LAST_MODIFIED_DATE TIMESTAMP,
    SCORE_1 INTEGER,
    SCORE_2 INTEGER,
    SEED BIGINT,
    CONCEDED_BY BIGINT,
    PLAYER_1 BIGINT not null,
    PLAYER_2 BIGINT,
    constraint FKBAY1Y7K9W4INDL8JQ35OTB02A
        foreign key (PLAYER_2) references USER (ID),
    constraint FKEVY9M6JORDV12T1K1BJHVCW1A
        foreign key (CONCEDED_BY) references USER (ID),
    constraint FKJL1GSDTGXFOXB0PVI0QEEJ9Q1
        foreign key (PLAYER_1) references USER (ID)
);

create table USER_AUTHORITY
(
    USER_ID BIGINT not null,
    AUTHORITY_NAME VARCHAR(50) not null,
    primary key (USER_ID, AUTHORITY_NAME),
    constraint FK6KTGLPL5MJOSA283RVKEN2PY5
        foreign key (AUTHORITY_NAME) references AUTHORITY (NAME),
    constraint FKPQLSJPKYBGOS9W2SVCRI7J8XY
        foreign key (USER_ID) references USER (ID)
);

create sequence SEQUENCE_GENERATOR
    increment by 50;