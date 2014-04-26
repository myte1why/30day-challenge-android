CREATE TABLE `challenge` (
    _id             INTEGER PRIMARY KEY AUTOINCREMENT,
    name            TEXT    NOT NULL,
    description     TEXT,
    alarm           INTEGER DEFAULT 1,
    alarmTime       TIME    NOT NULL,
    snoozeInterval  INTEGER DEFAULT 30
);

CREATE TABLE `challengeAttempt` (
    _id             INTEGER NOT NULL,
    challenge_id    INTEGER NOT NULL,
    firstDay        DATE    NOT NULL DEFAULT CURRENT_DATE,
    status          INTEGER NOT NULL DEFAULT 1,

    PRIMARY KEY (_id, challenge_id),

    FOREIGN KEY (challenge_id)
        REFERENCES challenge (_id)
        ON DELETE CASCADE,

    CHECK (status IN (1, 2, 3))
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
        ON DELETE CASCADE,

    CHECK (status IN (1, 2, 3))
);

INSERT INTO challenge VALUES (1, 'Challenge A', NULL, 1, '20:00:00', 15);
    INSERT INTO challengeAttempt VALUES (1, 1, '2014-02-01', 3);
        INSERT INTO challengeAttemptDay VALUES ( 1, 1, 1, 2, NULL);
        INSERT INTO challengeAttemptDay VALUES ( 2, 1, 1, 2, NULL);
        INSERT INTO challengeAttemptDay VALUES ( 3, 1, 1, 2, NULL);
        INSERT INTO challengeAttemptDay VALUES ( 4, 1, 1, 2, NULL);
        INSERT INTO challengeAttemptDay VALUES ( 5, 1, 1, 2, NULL);
        INSERT INTO challengeAttemptDay VALUES ( 6, 1, 1, 2, NULL);
        INSERT INTO challengeAttemptDay VALUES ( 7, 1, 1, 2, NULL);
        INSERT INTO challengeAttemptDay VALUES ( 8, 1, 1, 2, NULL);
        INSERT INTO challengeAttemptDay VALUES ( 9, 1, 1, 2, NULL);
        INSERT INTO challengeAttemptDay VALUES (10, 1, 1, 2, NULL);
        INSERT INTO challengeAttemptDay VALUES (11, 1, 1, 2, NULL);
        INSERT INTO challengeAttemptDay VALUES (12, 1, 1, 2, NULL);
        INSERT INTO challengeAttemptDay VALUES (13, 1, 1, 2, NULL);
        INSERT INTO challengeAttemptDay VALUES (14, 1, 1, 3, NULL);

    INSERT INTO challengeAttempt VALUES (2, 1, '2014-02-20', 2);
        INSERT INTO challengeAttemptDay VALUES ( 1, 1, 2, 2, NULL);
        INSERT INTO challengeAttemptDay VALUES ( 2, 1, 2, 2, NULL);
        INSERT INTO challengeAttemptDay VALUES ( 3, 1, 2, 2, NULL);
        INSERT INTO challengeAttemptDay VALUES ( 4, 1, 2, 2, NULL);
        INSERT INTO challengeAttemptDay VALUES ( 5, 1, 2, 2, NULL);
        INSERT INTO challengeAttemptDay VALUES ( 6, 1, 2, 2, NULL);
        INSERT INTO challengeAttemptDay VALUES ( 7, 1, 2, 2, NULL);
        INSERT INTO challengeAttemptDay VALUES ( 8, 1, 2, 2, NULL);
        INSERT INTO challengeAttemptDay VALUES ( 9, 1, 2, 2, NULL);
        INSERT INTO challengeAttemptDay VALUES (10, 1, 2, 2, NULL);
        INSERT INTO challengeAttemptDay VALUES (11, 1, 2, 2, NULL);
        INSERT INTO challengeAttemptDay VALUES (12, 1, 2, 2, NULL);
        INSERT INTO challengeAttemptDay VALUES (13, 1, 2, 2, NULL);
        INSERT INTO challengeAttemptDay VALUES (14, 1, 2, 2, NULL);
        INSERT INTO challengeAttemptDay VALUES (15, 1, 2, 2, NULL);
        INSERT INTO challengeAttemptDay VALUES (16, 1, 2, 2, NULL);
        INSERT INTO challengeAttemptDay VALUES (17, 1, 2, 2, NULL);
        INSERT INTO challengeAttemptDay VALUES (18, 1, 2, 2, NULL);
        INSERT INTO challengeAttemptDay VALUES (19, 1, 2, 2, NULL);
        INSERT INTO challengeAttemptDay VALUES (20, 1, 2, 2, NULL);
        INSERT INTO challengeAttemptDay VALUES (21, 1, 2, 2, NULL);
        INSERT INTO challengeAttemptDay VALUES (22, 1, 2, 2, NULL);
        INSERT INTO challengeAttemptDay VALUES (23, 1, 2, 2, NULL);
        INSERT INTO challengeAttemptDay VALUES (24, 1, 2, 2, NULL);
        INSERT INTO challengeAttemptDay VALUES (25, 1, 2, 2, NULL);
        INSERT INTO challengeAttemptDay VALUES (26, 1, 2, 2, NULL);
        INSERT INTO challengeAttemptDay VALUES (27, 1, 2, 2, NULL);
        INSERT INTO challengeAttemptDay VALUES (28, 1, 2, 2, NULL);
        INSERT INTO challengeAttemptDay VALUES (29, 1, 2, 2, NULL);
        INSERT INTO challengeAttemptDay VALUES (30, 1, 2, 2, NULL);

    INSERT INTO challengeAttempt VALUES (3, 1, DATE('now'), 1);
        INSERT INTO challengeAttemptDay VALUES (1, 1, 3, 1, NULL);