-- Simultaneous lending limit

CREATE TABLE simultaneous_renew (
    holding_id integer NOT NULL,
    user_id integer NOT NULL,
    count integer not NULL,
    created timestamp without time zone DEFAULT now() NOT NULL,
    created_by integer
);

ALTER TABLE bib4template.simultaneous_renew OWNER TO biblivre;

CREATE SEQUENCE simultaneous_renew_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE bib4template.simultaneous_renew_id_seq OWNER TO biblivre;

ALTER SEQUENCE simultaneous_renew_id_seq OWNED BY lendings.id;


SELECT pg_catalog.setval('simultaneous_renew_id_seq', 1, false);