CREATE TABLE `challenge` (
    _id             INTEGER  PRIMARY KEY AUTOINCREMENT,
    name            TEXT     NOT NULL,
    description     TEXT,
    alarm           DATETIME,
    snoozeInterval  INTEGER  DEFAULT 15
);

CREATE TABLE `challengeList` (
    _id             INTEGER  PRIMARY KEY AUTOINCREMENT,
    name            TEXT     NOT NULL
);

CREATE TABLE `challengeListLink` (
    list_id         INTEGER NOT NULL,
    challenge_id    INTEGER NOT NULL,

    FOREIGN KEY (list_id)
        REFERENCES challengeList (_id)
        ON DELETE CASCADE,

    FOREIGN KEY (challenge_id)
        REFERENCES challenge (_id)
        ON DELETE CASCADE
);

CREATE TABLE `challengeAttempt` (
    _id             INTEGER  NOT NULL,
    challenge_id    INTEGER  NOT NULL,
    firstDay        DATETIME NOT NULL,
    status          INTEGER  NOT NULL DEFAULT 0,

    PRIMARY KEY (_id, challenge_id),

    FOREIGN KEY (challenge_id)
        REFERENCES challenge (_id)
        ON DELETE CASCADE
);

CREATE TABLE `challengeAttemptDay` (
    _id             INTEGER NOT NULL,
    challenge_id    INTEGER NOT NULL,
    attempt_id      INTEGER NOT NULL,
    status          INTEGER NOT NULL DEFAULT 0,
    note            TEXT,

    PRIMARY KEY (_id, challenge_id, attempt_id),

    FOREIGN KEY (attempt_id, challenge_id)
        REFERENCES challengeAttempt(_id, challenge_id)
        ON DELETE CASCADE
);
