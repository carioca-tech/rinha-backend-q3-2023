CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE TABLE if not exists pessoa( id UUID NOT NULL, apelido VARCHAR(32) NOT NULL UNIQUE, nome VARCHAR(100), nascimento date NOT NULL, stack varchar[], primary key(id));
-- CREATE TABLE if not exists pessoa_0 PARTITION OF pessoa FOR VALUES WITH (MODULUS 7, REMAINDER 0);
-- CREATE TABLE if not exists pessoa_1 PARTITION OF pessoa FOR VALUES WITH (MODULUS 7, REMAINDER 1);
-- CREATE TABLE if not exists pessoa_2 PARTITION OF pessoa FOR VALUES WITH (MODULUS 7, REMAINDER 2);
-- CREATE TABLE if not exists pessoa_3 PARTITION OF pessoa FOR VALUES WITH (MODULUS 7, REMAINDER 3);
-- CREATE TABLE if not exists pessoa_4 PARTITION OF pessoa FOR VALUES WITH (MODULUS 7, REMAINDER 4);
-- CREATE TABLE if not exists pessoa_5 PARTITION OF pessoa FOR VALUES WITH (MODULUS 7, REMAINDER 5);
-- CREATE TABLE if not exists pessoa_6 PARTITION OF pessoa FOR VALUES WITH (MODULUS 7, REMAINDER 6);
CREATE INDEX if not exists idx_01 ON pessoa (upper(apelido), upper(nome), stack);

