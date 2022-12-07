--
-- PostgreSQL database dump
--

-- Dumped from database version 11.16 (Debian 11.16-1.pgdg90+1)
-- Dumped by pg_dump version 11.18 (Ubuntu 11.18-1.pgdg22.04+1)

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- Name: bib4template; Type: SCHEMA; Schema: -; Owner: biblivre
--

CREATE SCHEMA bib4template;


ALTER SCHEMA bib4template OWNER TO biblivre;

--
-- Name: clear_indexing_type(); Type: FUNCTION; Schema: bib4template; Owner: biblivre
--

CREATE FUNCTION bib4template.clear_indexing_type() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
BEGIN
	EXECUTE 'DELETE FROM "' || TG_TABLE_SCHEMA || '".' || TG_ARGV[0] || '_idx_fields WHERE indexing_group_id = ' || OLD.id;
	EXECUTE 'DELETE FROM "' || TG_TABLE_SCHEMA || '".' || TG_ARGV[0] || '_idx_sort WHERE indexing_group_id = ' || OLD.id;

	RETURN NULL;
END;
$$;


ALTER FUNCTION bib4template.clear_indexing_type() OWNER TO biblivre;

--
-- Name: clear_record(); Type: FUNCTION; Schema: bib4template; Owner: biblivre
--

CREATE FUNCTION bib4template.clear_record() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
BEGIN
	EXECUTE 'DELETE FROM "' || TG_TABLE_SCHEMA || '".' || TG_ARGV[0] || '_idx_fields WHERE record_id = ' || OLD.id;
	EXECUTE 'DELETE FROM "' || TG_TABLE_SCHEMA || '".' || TG_ARGV[0] || '_idx_sort WHERE record_id = ' || OLD.id;
	EXECUTE 'DELETE FROM "' || TG_TABLE_SCHEMA || '".' || TG_ARGV[0] || '_search_results WHERE record_id = ' || OLD.id;

	RETURN NULL;
END;
$$;


ALTER FUNCTION bib4template.clear_record() OWNER TO biblivre;

--
-- Name: access_cards_id_seq; Type: SEQUENCE; Schema: bib4template; Owner: biblivre
--

CREATE SEQUENCE bib4template.access_cards_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE bib4template.access_cards_id_seq OWNER TO biblivre;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: access_cards; Type: TABLE; Schema: bib4template; Owner: biblivre
--

CREATE TABLE bib4template.access_cards (
    id integer DEFAULT nextval('bib4template.access_cards_id_seq'::regclass) NOT NULL,
    code character varying NOT NULL,
    status character varying NOT NULL,
    created timestamp without time zone DEFAULT now() NOT NULL,
    created_by integer,
    modified timestamp without time zone DEFAULT now() NOT NULL,
    modified_by integer
);


ALTER TABLE bib4template.access_cards OWNER TO biblivre;

--
-- Name: access_control_id_seq; Type: SEQUENCE; Schema: bib4template; Owner: biblivre
--

CREATE SEQUENCE bib4template.access_control_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE bib4template.access_control_id_seq OWNER TO biblivre;

--
-- Name: access_control; Type: TABLE; Schema: bib4template; Owner: biblivre
--

CREATE TABLE bib4template.access_control (
    id integer DEFAULT nextval('bib4template.access_control_id_seq'::regclass) NOT NULL,
    access_card_id integer NOT NULL,
    user_id integer NOT NULL,
    arrival_time timestamp without time zone DEFAULT now() NOT NULL,
    departure_time timestamp without time zone,
    created timestamp without time zone DEFAULT now() NOT NULL,
    created_by integer,
    modified timestamp without time zone DEFAULT now() NOT NULL,
    modified_by integer
);


ALTER TABLE bib4template.access_control OWNER TO biblivre;

--
-- Name: authorities_brief_formats; Type: TABLE; Schema: bib4template; Owner: biblivre
--

CREATE TABLE bib4template.authorities_brief_formats (
    datafield character(3) NOT NULL,
    format text NOT NULL,
    sort_order integer,
    created timestamp without time zone DEFAULT now() NOT NULL,
    created_by integer,
    modified timestamp without time zone DEFAULT now() NOT NULL,
    modified_by integer
);


ALTER TABLE bib4template.authorities_brief_formats OWNER TO biblivre;

--
-- Name: authorities_form_datafields; Type: TABLE; Schema: bib4template; Owner: biblivre
--

CREATE TABLE bib4template.authorities_form_datafields (
    datafield character(3) NOT NULL,
    collapsed boolean DEFAULT false NOT NULL,
    repeatable boolean DEFAULT false NOT NULL,
    indicator_1 character varying,
    indicator_2 character varying,
    material_type character varying,
    created timestamp without time zone DEFAULT now() NOT NULL,
    created_by integer,
    modified timestamp without time zone DEFAULT now() NOT NULL,
    modified_by integer,
    sort_order integer
);


ALTER TABLE bib4template.authorities_form_datafields OWNER TO biblivre;

--
-- Name: authorities_form_subfields; Type: TABLE; Schema: bib4template; Owner: biblivre
--

CREATE TABLE bib4template.authorities_form_subfields (
    datafield character(3) NOT NULL,
    subfield character(1) NOT NULL,
    collapsed boolean DEFAULT false NOT NULL,
    repeatable boolean DEFAULT false NOT NULL,
    created timestamp without time zone DEFAULT now() NOT NULL,
    created_by integer,
    modified timestamp without time zone DEFAULT now() NOT NULL,
    modified_by integer,
    autocomplete_type character varying DEFAULT 'disabled'::character varying NOT NULL,
    sort_order integer
);


ALTER TABLE bib4template.authorities_form_subfields OWNER TO biblivre;

--
-- Name: authorities_idx_autocomplete; Type: TABLE; Schema: bib4template; Owner: biblivre
--

CREATE TABLE bib4template.authorities_idx_autocomplete (
    id integer NOT NULL,
    datafield character(3) NOT NULL,
    subfield character(1) NOT NULL,
    word character varying NOT NULL,
    phrase character varying NOT NULL,
    record_id integer
);


ALTER TABLE bib4template.authorities_idx_autocomplete OWNER TO biblivre;

--
-- Name: authorities_idx_autocomplete_id_seq; Type: SEQUENCE; Schema: bib4template; Owner: biblivre
--

CREATE SEQUENCE bib4template.authorities_idx_autocomplete_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE bib4template.authorities_idx_autocomplete_id_seq OWNER TO biblivre;

--
-- Name: authorities_idx_autocomplete_id_seq; Type: SEQUENCE OWNED BY; Schema: bib4template; Owner: biblivre
--

ALTER SEQUENCE bib4template.authorities_idx_autocomplete_id_seq OWNED BY bib4template.authorities_idx_autocomplete.id;


--
-- Name: authorities_idx_fields; Type: TABLE; Schema: bib4template; Owner: biblivre
--

CREATE TABLE bib4template.authorities_idx_fields (
    record_id integer NOT NULL,
    indexing_group_id integer NOT NULL,
    word character varying NOT NULL,
    datafield integer NOT NULL
);


ALTER TABLE bib4template.authorities_idx_fields OWNER TO biblivre;

--
-- Name: authorities_idx_sort; Type: TABLE; Schema: bib4template; Owner: biblivre
--

CREATE TABLE bib4template.authorities_idx_sort (
    record_id integer NOT NULL,
    indexing_group_id integer NOT NULL,
    phrase character varying,
    ignore_chars_count integer
);


ALTER TABLE bib4template.authorities_idx_sort OWNER TO biblivre;

--
-- Name: authorities_indexing_groups; Type: TABLE; Schema: bib4template; Owner: biblivre
--

CREATE TABLE bib4template.authorities_indexing_groups (
    id integer NOT NULL,
    translation_key character varying NOT NULL,
    datafields text,
    sortable boolean DEFAULT false NOT NULL,
    default_sort boolean DEFAULT false NOT NULL,
    created timestamp without time zone DEFAULT now() NOT NULL,
    created_by integer,
    modified timestamp without time zone DEFAULT now() NOT NULL,
    modified_by integer
);


ALTER TABLE bib4template.authorities_indexing_groups OWNER TO biblivre;

--
-- Name: authorities_indexing_groups_id_seq; Type: SEQUENCE; Schema: bib4template; Owner: biblivre
--

CREATE SEQUENCE bib4template.authorities_indexing_groups_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE bib4template.authorities_indexing_groups_id_seq OWNER TO biblivre;

--
-- Name: authorities_indexing_groups_id_seq; Type: SEQUENCE OWNED BY; Schema: bib4template; Owner: biblivre
--

ALTER SEQUENCE bib4template.authorities_indexing_groups_id_seq OWNED BY bib4template.authorities_indexing_groups.id;


--
-- Name: authorities_records; Type: TABLE; Schema: bib4template; Owner: biblivre
--

CREATE TABLE bib4template.authorities_records (
    id integer NOT NULL,
    iso2709 text NOT NULL,
    material character varying(20),
    database character varying(10) DEFAULT 'main'::character varying NOT NULL,
    created timestamp without time zone DEFAULT now() NOT NULL,
    created_by integer,
    modified timestamp without time zone DEFAULT now() NOT NULL,
    modified_by integer
);


ALTER TABLE bib4template.authorities_records OWNER TO biblivre;

--
-- Name: authorities_records_id_seq; Type: SEQUENCE; Schema: bib4template; Owner: biblivre
--

CREATE SEQUENCE bib4template.authorities_records_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE bib4template.authorities_records_id_seq OWNER TO biblivre;

--
-- Name: authorities_records_id_seq; Type: SEQUENCE OWNED BY; Schema: bib4template; Owner: biblivre
--

ALTER SEQUENCE bib4template.authorities_records_id_seq OWNED BY bib4template.authorities_records.id;


--
-- Name: authorities_search_results; Type: TABLE; Schema: bib4template; Owner: biblivre
--

CREATE TABLE bib4template.authorities_search_results (
    search_id integer NOT NULL,
    indexing_group_id integer NOT NULL,
    record_id integer NOT NULL
);


ALTER TABLE bib4template.authorities_search_results OWNER TO biblivre;

--
-- Name: authorities_searches; Type: TABLE; Schema: bib4template; Owner: biblivre
--

CREATE TABLE bib4template.authorities_searches (
    id integer NOT NULL,
    parameters text NOT NULL,
    created timestamp without time zone DEFAULT now() NOT NULL,
    created_by integer
);


ALTER TABLE bib4template.authorities_searches OWNER TO biblivre;

--
-- Name: authorities_searches_id_seq; Type: SEQUENCE; Schema: bib4template; Owner: biblivre
--

CREATE SEQUENCE bib4template.authorities_searches_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE bib4template.authorities_searches_id_seq OWNER TO biblivre;

--
-- Name: authorities_searches_id_seq; Type: SEQUENCE OWNED BY; Schema: bib4template; Owner: biblivre
--

ALTER SEQUENCE bib4template.authorities_searches_id_seq OWNED BY bib4template.authorities_searches.id;


--
-- Name: backups; Type: TABLE; Schema: bib4template; Owner: biblivre
--

CREATE TABLE bib4template.backups (
    id integer NOT NULL,
    created timestamp without time zone DEFAULT now() NOT NULL,
    path character varying,
    schemas character varying NOT NULL,
    type character varying NOT NULL,
    scope character varying NOT NULL,
    downloaded boolean DEFAULT false NOT NULL,
    steps integer,
    current_step integer
);


ALTER TABLE bib4template.backups OWNER TO biblivre;

--
-- Name: backups_id_seq; Type: SEQUENCE; Schema: bib4template; Owner: biblivre
--

CREATE SEQUENCE bib4template.backups_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE bib4template.backups_id_seq OWNER TO biblivre;

--
-- Name: backups_id_seq; Type: SEQUENCE OWNED BY; Schema: bib4template; Owner: biblivre
--

ALTER SEQUENCE bib4template.backups_id_seq OWNED BY bib4template.backups.id;


--
-- Name: biblio_brief_formats; Type: TABLE; Schema: bib4template; Owner: biblivre
--

CREATE TABLE bib4template.biblio_brief_formats (
    datafield character(3) NOT NULL,
    format text NOT NULL,
    sort_order integer,
    created timestamp without time zone DEFAULT now() NOT NULL,
    created_by integer,
    modified timestamp without time zone DEFAULT now() NOT NULL,
    modified_by integer
);


ALTER TABLE bib4template.biblio_brief_formats OWNER TO biblivre;

--
-- Name: biblio_form_datafields; Type: TABLE; Schema: bib4template; Owner: biblivre
--

CREATE TABLE bib4template.biblio_form_datafields (
    datafield character(3) NOT NULL,
    collapsed boolean DEFAULT false NOT NULL,
    repeatable boolean DEFAULT false NOT NULL,
    indicator_1 character varying,
    indicator_2 character varying,
    material_type character varying,
    created timestamp without time zone DEFAULT now() NOT NULL,
    created_by integer,
    modified timestamp without time zone DEFAULT now() NOT NULL,
    modified_by integer,
    sort_order integer
);


ALTER TABLE bib4template.biblio_form_datafields OWNER TO biblivre;

--
-- Name: biblio_form_subfields; Type: TABLE; Schema: bib4template; Owner: biblivre
--

CREATE TABLE bib4template.biblio_form_subfields (
    datafield character(3) NOT NULL,
    subfield character(1) NOT NULL,
    collapsed boolean DEFAULT false NOT NULL,
    repeatable boolean DEFAULT false NOT NULL,
    created timestamp without time zone DEFAULT now() NOT NULL,
    created_by integer,
    modified timestamp without time zone DEFAULT now() NOT NULL,
    modified_by integer,
    autocomplete_type character varying DEFAULT 'disabled'::character varying NOT NULL,
    sort_order integer
);


ALTER TABLE bib4template.biblio_form_subfields OWNER TO biblivre;

--
-- Name: biblio_holdings; Type: TABLE; Schema: bib4template; Owner: biblivre
--

CREATE TABLE bib4template.biblio_holdings (
    id integer NOT NULL,
    record_id integer NOT NULL,
    iso2709 text NOT NULL,
    database character varying(10) DEFAULT 'main'::character varying NOT NULL,
    accession_number character varying NOT NULL,
    location_d character varying,
    created timestamp without time zone DEFAULT now() NOT NULL,
    created_by integer,
    modified timestamp without time zone DEFAULT now() NOT NULL,
    modified_by integer,
    material character varying(20),
    availability character varying DEFAULT 'available'::character varying NOT NULL,
    label_printed boolean DEFAULT false
);


ALTER TABLE bib4template.biblio_holdings OWNER TO biblivre;

--
-- Name: biblio_holdings_id_seq; Type: SEQUENCE; Schema: bib4template; Owner: biblivre
--

CREATE SEQUENCE bib4template.biblio_holdings_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE bib4template.biblio_holdings_id_seq OWNER TO biblivre;

--
-- Name: biblio_holdings_id_seq; Type: SEQUENCE OWNED BY; Schema: bib4template; Owner: biblivre
--

ALTER SEQUENCE bib4template.biblio_holdings_id_seq OWNED BY bib4template.biblio_holdings.id;


--
-- Name: biblio_idx_autocomplete; Type: TABLE; Schema: bib4template; Owner: biblivre
--

CREATE TABLE bib4template.biblio_idx_autocomplete (
    id integer NOT NULL,
    datafield character(3) NOT NULL,
    subfield character(1) NOT NULL,
    word character varying NOT NULL,
    phrase character varying NOT NULL,
    record_id integer
);


ALTER TABLE bib4template.biblio_idx_autocomplete OWNER TO biblivre;

--
-- Name: biblio_idx_autocomplete_id_seq; Type: SEQUENCE; Schema: bib4template; Owner: biblivre
--

CREATE SEQUENCE bib4template.biblio_idx_autocomplete_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE bib4template.biblio_idx_autocomplete_id_seq OWNER TO biblivre;

--
-- Name: biblio_idx_autocomplete_id_seq; Type: SEQUENCE OWNED BY; Schema: bib4template; Owner: biblivre
--

ALTER SEQUENCE bib4template.biblio_idx_autocomplete_id_seq OWNED BY bib4template.biblio_idx_autocomplete.id;


--
-- Name: biblio_idx_fields; Type: TABLE; Schema: bib4template; Owner: biblivre
--

CREATE TABLE bib4template.biblio_idx_fields (
    record_id integer NOT NULL,
    indexing_group_id integer NOT NULL,
    word character varying NOT NULL,
    datafield integer NOT NULL
);


ALTER TABLE bib4template.biblio_idx_fields OWNER TO biblivre;

--
-- Name: biblio_idx_sort; Type: TABLE; Schema: bib4template; Owner: biblivre
--

CREATE TABLE bib4template.biblio_idx_sort (
    record_id integer NOT NULL,
    indexing_group_id integer NOT NULL,
    phrase character varying,
    ignore_chars_count integer
);


ALTER TABLE bib4template.biblio_idx_sort OWNER TO biblivre;

--
-- Name: biblio_indexing_groups; Type: TABLE; Schema: bib4template; Owner: biblivre
--

CREATE TABLE bib4template.biblio_indexing_groups (
    id integer NOT NULL,
    translation_key character varying NOT NULL,
    datafields text,
    sortable boolean DEFAULT false NOT NULL,
    default_sort boolean DEFAULT false NOT NULL,
    created timestamp without time zone DEFAULT now() NOT NULL,
    created_by integer,
    modified timestamp without time zone DEFAULT now() NOT NULL,
    modified_by integer
);


ALTER TABLE bib4template.biblio_indexing_groups OWNER TO biblivre;

--
-- Name: biblio_indexing_groups_id_seq; Type: SEQUENCE; Schema: bib4template; Owner: biblivre
--

CREATE SEQUENCE bib4template.biblio_indexing_groups_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE bib4template.biblio_indexing_groups_id_seq OWNER TO biblivre;

--
-- Name: biblio_indexing_groups_id_seq; Type: SEQUENCE OWNED BY; Schema: bib4template; Owner: biblivre
--

ALTER SEQUENCE bib4template.biblio_indexing_groups_id_seq OWNED BY bib4template.biblio_indexing_groups.id;


--
-- Name: biblio_records; Type: TABLE; Schema: bib4template; Owner: biblivre
--

CREATE TABLE bib4template.biblio_records (
    id integer NOT NULL,
    iso2709 text NOT NULL,
    material character varying(20),
    database character varying(10) DEFAULT 'main'::character varying NOT NULL,
    created timestamp without time zone DEFAULT now() NOT NULL,
    created_by integer,
    modified timestamp without time zone DEFAULT now() NOT NULL,
    modified_by integer
);


ALTER TABLE bib4template.biblio_records OWNER TO biblivre;

--
-- Name: biblio_records_id_seq; Type: SEQUENCE; Schema: bib4template; Owner: biblivre
--

CREATE SEQUENCE bib4template.biblio_records_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE bib4template.biblio_records_id_seq OWNER TO biblivre;

--
-- Name: biblio_records_id_seq; Type: SEQUENCE OWNED BY; Schema: bib4template; Owner: biblivre
--

ALTER SEQUENCE bib4template.biblio_records_id_seq OWNED BY bib4template.biblio_records.id;


--
-- Name: biblio_search_results; Type: TABLE; Schema: bib4template; Owner: biblivre
--

CREATE TABLE bib4template.biblio_search_results (
    search_id integer NOT NULL,
    indexing_group_id integer NOT NULL,
    record_id integer NOT NULL
);


ALTER TABLE bib4template.biblio_search_results OWNER TO biblivre;

--
-- Name: biblio_searches; Type: TABLE; Schema: bib4template; Owner: biblivre
--

CREATE TABLE bib4template.biblio_searches (
    id integer NOT NULL,
    parameters text NOT NULL,
    created timestamp without time zone DEFAULT now() NOT NULL,
    created_by integer
);


ALTER TABLE bib4template.biblio_searches OWNER TO biblivre;

--
-- Name: biblio_searches_id_seq; Type: SEQUENCE; Schema: bib4template; Owner: biblivre
--

CREATE SEQUENCE bib4template.biblio_searches_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE bib4template.biblio_searches_id_seq OWNER TO biblivre;

--
-- Name: biblio_searches_id_seq; Type: SEQUENCE OWNED BY; Schema: bib4template; Owner: biblivre
--

ALTER SEQUENCE bib4template.biblio_searches_id_seq OWNED BY bib4template.biblio_searches.id;


--
-- Name: configurations; Type: TABLE; Schema: bib4template; Owner: biblivre
--

CREATE TABLE bib4template.configurations (
    key character varying NOT NULL,
    value character varying NOT NULL,
    type character varying DEFAULT 'string'::character varying NOT NULL,
    required boolean DEFAULT false NOT NULL,
    modified timestamp without time zone DEFAULT now() NOT NULL,
    modified_by integer
);


ALTER TABLE bib4template.configurations OWNER TO biblivre;

--
-- Name: digital_media; Type: TABLE; Schema: bib4template; Owner: biblivre
--

CREATE TABLE bib4template.digital_media (
    id integer NOT NULL,
    name character varying,
    blob oid NOT NULL,
    content_type character varying,
    size bigint,
    created timestamp without time zone DEFAULT now() NOT NULL,
    created_by integer
);


ALTER TABLE bib4template.digital_media OWNER TO biblivre;

--
-- Name: digital_media_id_seq; Type: SEQUENCE; Schema: bib4template; Owner: biblivre
--

CREATE SEQUENCE bib4template.digital_media_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE bib4template.digital_media_id_seq OWNER TO biblivre;

--
-- Name: digital_media_id_seq; Type: SEQUENCE OWNED BY; Schema: bib4template; Owner: biblivre
--

ALTER SEQUENCE bib4template.digital_media_id_seq OWNED BY bib4template.digital_media.id;


--
-- Name: holding_creation_counter; Type: TABLE; Schema: bib4template; Owner: biblivre
--

CREATE TABLE bib4template.holding_creation_counter (
    id integer NOT NULL,
    user_name character varying(255) NOT NULL,
    user_login character varying(100),
    created timestamp without time zone DEFAULT now() NOT NULL,
    created_by integer NOT NULL
);


ALTER TABLE bib4template.holding_creation_counter OWNER TO biblivre;

--
-- Name: holding_creation_counter_id_seq; Type: SEQUENCE; Schema: bib4template; Owner: biblivre
--

CREATE SEQUENCE bib4template.holding_creation_counter_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE bib4template.holding_creation_counter_id_seq OWNER TO biblivre;

--
-- Name: holding_creation_counter_id_seq; Type: SEQUENCE OWNED BY; Schema: bib4template; Owner: biblivre
--

ALTER SEQUENCE bib4template.holding_creation_counter_id_seq OWNED BY bib4template.holding_creation_counter.id;


--
-- Name: holding_form_datafields; Type: TABLE; Schema: bib4template; Owner: biblivre
--

CREATE TABLE bib4template.holding_form_datafields (
    datafield character(3) NOT NULL,
    collapsed boolean DEFAULT false NOT NULL,
    repeatable boolean DEFAULT false NOT NULL,
    indicator_1 character varying,
    indicator_2 character varying,
    material_type character varying,
    created timestamp without time zone DEFAULT now() NOT NULL,
    created_by integer,
    modified timestamp without time zone DEFAULT now() NOT NULL,
    modified_by integer,
    sort_order integer
);


ALTER TABLE bib4template.holding_form_datafields OWNER TO biblivre;

--
-- Name: holding_form_subfields; Type: TABLE; Schema: bib4template; Owner: biblivre
--

CREATE TABLE bib4template.holding_form_subfields (
    datafield character(3) NOT NULL,
    subfield character(1) NOT NULL,
    collapsed boolean DEFAULT false NOT NULL,
    repeatable boolean DEFAULT false NOT NULL,
    created timestamp without time zone DEFAULT now() NOT NULL,
    created_by integer,
    modified timestamp without time zone DEFAULT now() NOT NULL,
    modified_by integer,
    autocomplete_type character varying DEFAULT 'disabled'::character varying NOT NULL,
    sort_order integer
);


ALTER TABLE bib4template.holding_form_subfields OWNER TO biblivre;

--
-- Name: lending_fines; Type: TABLE; Schema: bib4template; Owner: biblivre
--

CREATE TABLE bib4template.lending_fines (
    id integer NOT NULL,
    lending_id integer NOT NULL,
    user_id integer NOT NULL,
    fine_value real NOT NULL,
    payment_date timestamp without time zone,
    created timestamp without time zone DEFAULT now() NOT NULL,
    created_by integer
);


ALTER TABLE bib4template.lending_fines OWNER TO biblivre;

--
-- Name: lending_fines_id_seq; Type: SEQUENCE; Schema: bib4template; Owner: biblivre
--

CREATE SEQUENCE bib4template.lending_fines_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE bib4template.lending_fines_id_seq OWNER TO biblivre;

--
-- Name: lending_fines_id_seq; Type: SEQUENCE OWNED BY; Schema: bib4template; Owner: biblivre
--

ALTER SEQUENCE bib4template.lending_fines_id_seq OWNED BY bib4template.lending_fines.id;


--
-- Name: lendings; Type: TABLE; Schema: bib4template; Owner: biblivre
--

CREATE TABLE bib4template.lendings (
    id integer NOT NULL,
    holding_id integer NOT NULL,
    user_id integer NOT NULL,
    previous_lending_id integer,
    expected_return_date timestamp without time zone,
    return_date timestamp without time zone,
    created timestamp without time zone DEFAULT now() NOT NULL,
    created_by integer
);


ALTER TABLE bib4template.lendings OWNER TO biblivre;

--
-- Name: lendings_id_seq; Type: SEQUENCE; Schema: bib4template; Owner: biblivre
--

CREATE SEQUENCE bib4template.lendings_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE bib4template.lendings_id_seq OWNER TO biblivre;

--
-- Name: lendings_id_seq; Type: SEQUENCE OWNED BY; Schema: bib4template; Owner: biblivre
--

ALTER SEQUENCE bib4template.lendings_id_seq OWNED BY bib4template.lendings.id;


--
-- Name: logins; Type: TABLE; Schema: bib4template; Owner: biblivre
--

CREATE TABLE bib4template.logins (
    id integer NOT NULL,
    login character varying NOT NULL,
    employee boolean DEFAULT false NOT NULL,
    password text NOT NULL,
    created timestamp without time zone DEFAULT now() NOT NULL,
    created_by integer,
    modified timestamp without time zone DEFAULT now() NOT NULL,
    modified_by integer
);


ALTER TABLE bib4template.logins OWNER TO biblivre;

--
-- Name: logins_id_seq; Type: SEQUENCE; Schema: bib4template; Owner: biblivre
--

CREATE SEQUENCE bib4template.logins_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE bib4template.logins_id_seq OWNER TO biblivre;

--
-- Name: logins_id_seq; Type: SEQUENCE OWNED BY; Schema: bib4template; Owner: biblivre
--

ALTER SEQUENCE bib4template.logins_id_seq OWNED BY bib4template.logins.id;


--
-- Name: orders; Type: TABLE; Schema: bib4template; Owner: biblivre
--

CREATE TABLE bib4template.orders (
    id integer NOT NULL,
    info text,
    status character varying,
    invoice_number character varying,
    receipt_date timestamp without time zone DEFAULT now(),
    total_value numeric,
    delivered_quantity integer,
    terms_of_payment character varying,
    deadline_date timestamp without time zone DEFAULT now() NOT NULL,
    created timestamp without time zone DEFAULT now() NOT NULL,
    created_by integer,
    modified timestamp without time zone DEFAULT now() NOT NULL,
    modified_by integer,
    quotation_id integer NOT NULL
);


ALTER TABLE bib4template.orders OWNER TO biblivre;

--
-- Name: orders_id_seq; Type: SEQUENCE; Schema: bib4template; Owner: biblivre
--

CREATE SEQUENCE bib4template.orders_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE bib4template.orders_id_seq OWNER TO biblivre;

--
-- Name: orders_id_seq; Type: SEQUENCE OWNED BY; Schema: bib4template; Owner: biblivre
--

ALTER SEQUENCE bib4template.orders_id_seq OWNED BY bib4template.orders.id;


--
-- Name: permissions; Type: TABLE; Schema: bib4template; Owner: biblivre
--

CREATE TABLE bib4template.permissions (
    login_id integer NOT NULL,
    permission character varying(80) NOT NULL
);


ALTER TABLE bib4template.permissions OWNER TO biblivre;

--
-- Name: quotations_id_seq; Type: SEQUENCE; Schema: bib4template; Owner: biblivre
--

CREATE SEQUENCE bib4template.quotations_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE bib4template.quotations_id_seq OWNER TO biblivre;

--
-- Name: quotations; Type: TABLE; Schema: bib4template; Owner: biblivre
--

CREATE TABLE bib4template.quotations (
    id integer DEFAULT nextval('bib4template.quotations_id_seq'::regclass) NOT NULL,
    supplier_id integer NOT NULL,
    response_date timestamp without time zone,
    expiration_date timestamp without time zone,
    delivery_time integer,
    info text,
    created timestamp without time zone DEFAULT now() NOT NULL,
    created_by integer,
    modified timestamp without time zone DEFAULT now() NOT NULL,
    modified_by integer
);


ALTER TABLE bib4template.quotations OWNER TO biblivre;

--
-- Name: request_id_seq; Type: SEQUENCE; Schema: bib4template; Owner: biblivre
--

CREATE SEQUENCE bib4template.request_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE bib4template.request_id_seq OWNER TO biblivre;

--
-- Name: request_quotation; Type: TABLE; Schema: bib4template; Owner: biblivre
--

CREATE TABLE bib4template.request_quotation (
    request_id integer NOT NULL,
    quotation_id integer NOT NULL,
    quotation_quantity integer,
    unit_value numeric,
    response_quantity integer
);


ALTER TABLE bib4template.request_quotation OWNER TO biblivre;

--
-- Name: requests; Type: TABLE; Schema: bib4template; Owner: biblivre
--

CREATE TABLE bib4template.requests (
    id integer DEFAULT nextval('bib4template.request_id_seq'::regclass) NOT NULL,
    requester character varying,
    author character varying,
    item_title character varying,
    item_subtitle character varying,
    edition_number character varying,
    publisher character varying,
    info text,
    status character varying,
    quantity integer,
    created timestamp without time zone DEFAULT now() NOT NULL,
    created_by integer,
    modified timestamp without time zone DEFAULT now() NOT NULL,
    modified_by integer
);


ALTER TABLE bib4template.requests OWNER TO biblivre;

--
-- Name: reservations; Type: TABLE; Schema: bib4template; Owner: biblivre
--

CREATE TABLE bib4template.reservations (
    id integer NOT NULL,
    record_id integer NOT NULL,
    user_id integer NOT NULL,
    expires timestamp without time zone,
    created timestamp without time zone DEFAULT now() NOT NULL,
    created_by integer
);


ALTER TABLE bib4template.reservations OWNER TO biblivre;

--
-- Name: reservations_id_seq; Type: SEQUENCE; Schema: bib4template; Owner: biblivre
--

CREATE SEQUENCE bib4template.reservations_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE bib4template.reservations_id_seq OWNER TO biblivre;

--
-- Name: reservations_id_seq; Type: SEQUENCE OWNED BY; Schema: bib4template; Owner: biblivre
--

ALTER SEQUENCE bib4template.reservations_id_seq OWNED BY bib4template.reservations.id;


--
-- Name: supplier_id_seq; Type: SEQUENCE; Schema: bib4template; Owner: biblivre
--

CREATE SEQUENCE bib4template.supplier_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE bib4template.supplier_id_seq OWNER TO biblivre;

--
-- Name: suppliers; Type: TABLE; Schema: bib4template; Owner: biblivre
--

CREATE TABLE bib4template.suppliers (
    id integer DEFAULT nextval('bib4template.supplier_id_seq'::regclass) NOT NULL,
    trademark character varying NOT NULL,
    supplier_name character varying,
    supplier_number character varying NOT NULL,
    vat_registration_number character varying,
    address character varying,
    address_number character varying,
    address_complement character varying,
    area character varying,
    city character varying,
    state character varying,
    country character varying,
    zip_code character varying,
    telephone_1 character varying,
    telephone_2 character varying,
    telephone_3 character varying,
    telephone_4 character varying,
    contact_1 character varying,
    contact_2 character varying,
    contact_3 character varying,
    contact_4 character varying,
    info character varying,
    url character varying,
    email character varying,
    created timestamp without time zone DEFAULT now() NOT NULL,
    created_by integer,
    modified timestamp without time zone DEFAULT now() NOT NULL,
    modified_by integer
);


ALTER TABLE bib4template.suppliers OWNER TO biblivre;

--
-- Name: translations; Type: TABLE; Schema: bib4template; Owner: biblivre
--

CREATE TABLE bib4template.translations (
    language character varying NOT NULL,
    key character varying NOT NULL,
    text text NOT NULL,
    created timestamp without time zone DEFAULT now() NOT NULL,
    created_by integer,
    modified timestamp without time zone DEFAULT now() NOT NULL,
    modified_by integer,
    user_created boolean DEFAULT false NOT NULL
);


ALTER TABLE bib4template.translations OWNER TO biblivre;

--
-- Name: users; Type: TABLE; Schema: bib4template; Owner: biblivre
--

CREATE TABLE bib4template.users (
    id integer NOT NULL,
    name character varying NOT NULL,
    type integer,
    photo_id character varying,
    status character varying NOT NULL,
    login_id integer,
    created timestamp without time zone DEFAULT now() NOT NULL,
    created_by integer,
    modified timestamp without time zone DEFAULT now() NOT NULL,
    modified_by integer,
    user_card_printed boolean DEFAULT false,
    name_ascii character varying
);


ALTER TABLE bib4template.users OWNER TO biblivre;

--
-- Name: users_fields; Type: TABLE; Schema: bib4template; Owner: biblivre
--

CREATE TABLE bib4template.users_fields (
    key character varying NOT NULL,
    type character varying NOT NULL,
    required boolean DEFAULT false NOT NULL,
    max_length integer DEFAULT 0 NOT NULL,
    sort_order integer,
    created timestamp without time zone DEFAULT now() NOT NULL,
    created_by integer,
    modified timestamp without time zone DEFAULT now() NOT NULL,
    modified_by integer
);


ALTER TABLE bib4template.users_fields OWNER TO biblivre;

--
-- Name: users_id_seq; Type: SEQUENCE; Schema: bib4template; Owner: biblivre
--

CREATE SEQUENCE bib4template.users_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE bib4template.users_id_seq OWNER TO biblivre;

--
-- Name: users_id_seq; Type: SEQUENCE OWNED BY; Schema: bib4template; Owner: biblivre
--

ALTER SEQUENCE bib4template.users_id_seq OWNED BY bib4template.users.id;


--
-- Name: users_types; Type: TABLE; Schema: bib4template; Owner: biblivre
--

CREATE TABLE bib4template.users_types (
    id integer NOT NULL,
    name character varying NOT NULL,
    description character varying,
    lending_limit integer,
    reservation_limit integer,
    lending_time_limit integer,
    reservation_time_limit integer,
    fine_value real DEFAULT 0.00 NOT NULL,
    created timestamp without time zone DEFAULT now() NOT NULL,
    created_by integer,
    modified timestamp without time zone DEFAULT now() NOT NULL,
    modified_by integer
);


ALTER TABLE bib4template.users_types OWNER TO biblivre;

--
-- Name: users_types_id_seq; Type: SEQUENCE; Schema: bib4template; Owner: biblivre
--

CREATE SEQUENCE bib4template.users_types_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE bib4template.users_types_id_seq OWNER TO biblivre;

--
-- Name: users_types_id_seq; Type: SEQUENCE OWNED BY; Schema: bib4template; Owner: biblivre
--

ALTER SEQUENCE bib4template.users_types_id_seq OWNED BY bib4template.users_types.id;


--
-- Name: users_values; Type: TABLE; Schema: bib4template; Owner: biblivre
--

CREATE TABLE bib4template.users_values (
    user_id integer NOT NULL,
    key character varying NOT NULL,
    value character varying NOT NULL,
    ascii character varying
);


ALTER TABLE bib4template.users_values OWNER TO biblivre;

--
-- Name: versions; Type: TABLE; Schema: bib4template; Owner: biblivre
--

CREATE TABLE bib4template.versions (
    installed_versions character varying NOT NULL
);


ALTER TABLE bib4template.versions OWNER TO biblivre;

--
-- Name: vocabulary_brief_formats; Type: TABLE; Schema: bib4template; Owner: biblivre
--

CREATE TABLE bib4template.vocabulary_brief_formats (
    datafield character(3) NOT NULL,
    format text NOT NULL,
    sort_order integer,
    created timestamp without time zone DEFAULT now() NOT NULL,
    created_by integer,
    modified timestamp without time zone DEFAULT now() NOT NULL,
    modified_by integer
);


ALTER TABLE bib4template.vocabulary_brief_formats OWNER TO biblivre;

--
-- Name: vocabulary_form_datafields; Type: TABLE; Schema: bib4template; Owner: biblivre
--

CREATE TABLE bib4template.vocabulary_form_datafields (
    datafield character(3) NOT NULL,
    collapsed boolean DEFAULT false NOT NULL,
    repeatable boolean DEFAULT false NOT NULL,
    indicator_1 character varying,
    indicator_2 character varying,
    material_type character varying,
    created timestamp without time zone DEFAULT now() NOT NULL,
    created_by integer,
    modified timestamp without time zone DEFAULT now() NOT NULL,
    modified_by integer,
    sort_order integer
);


ALTER TABLE bib4template.vocabulary_form_datafields OWNER TO biblivre;

--
-- Name: vocabulary_form_subfields; Type: TABLE; Schema: bib4template; Owner: biblivre
--

CREATE TABLE bib4template.vocabulary_form_subfields (
    datafield character(3) NOT NULL,
    subfield character(1) NOT NULL,
    collapsed boolean DEFAULT false NOT NULL,
    repeatable boolean DEFAULT false NOT NULL,
    created timestamp without time zone DEFAULT now() NOT NULL,
    created_by integer,
    modified timestamp without time zone DEFAULT now() NOT NULL,
    modified_by integer,
    autocomplete_type character varying DEFAULT 'disabled'::character varying NOT NULL,
    sort_order integer
);


ALTER TABLE bib4template.vocabulary_form_subfields OWNER TO biblivre;

--
-- Name: vocabulary_idx_autocomplete; Type: TABLE; Schema: bib4template; Owner: biblivre
--

CREATE TABLE bib4template.vocabulary_idx_autocomplete (
    id integer NOT NULL,
    datafield character(3) NOT NULL,
    subfield character(1) NOT NULL,
    word character varying NOT NULL,
    phrase character varying NOT NULL,
    record_id integer
);


ALTER TABLE bib4template.vocabulary_idx_autocomplete OWNER TO biblivre;

--
-- Name: vocabulary_idx_autocomplete_id_seq; Type: SEQUENCE; Schema: bib4template; Owner: biblivre
--

CREATE SEQUENCE bib4template.vocabulary_idx_autocomplete_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE bib4template.vocabulary_idx_autocomplete_id_seq OWNER TO biblivre;

--
-- Name: vocabulary_idx_autocomplete_id_seq; Type: SEQUENCE OWNED BY; Schema: bib4template; Owner: biblivre
--

ALTER SEQUENCE bib4template.vocabulary_idx_autocomplete_id_seq OWNED BY bib4template.vocabulary_idx_autocomplete.id;


--
-- Name: vocabulary_idx_fields; Type: TABLE; Schema: bib4template; Owner: biblivre
--

CREATE TABLE bib4template.vocabulary_idx_fields (
    record_id integer NOT NULL,
    indexing_group_id integer NOT NULL,
    word character varying NOT NULL,
    datafield integer NOT NULL
);


ALTER TABLE bib4template.vocabulary_idx_fields OWNER TO biblivre;

--
-- Name: vocabulary_idx_sort; Type: TABLE; Schema: bib4template; Owner: biblivre
--

CREATE TABLE bib4template.vocabulary_idx_sort (
    record_id integer NOT NULL,
    indexing_group_id integer NOT NULL,
    phrase character varying,
    ignore_chars_count integer
);


ALTER TABLE bib4template.vocabulary_idx_sort OWNER TO biblivre;

--
-- Name: vocabulary_indexing_groups; Type: TABLE; Schema: bib4template; Owner: biblivre
--

CREATE TABLE bib4template.vocabulary_indexing_groups (
    id integer NOT NULL,
    translation_key character varying NOT NULL,
    datafields text,
    sortable boolean DEFAULT false NOT NULL,
    default_sort boolean DEFAULT false NOT NULL,
    created timestamp without time zone DEFAULT now() NOT NULL,
    created_by integer,
    modified timestamp without time zone DEFAULT now() NOT NULL,
    modified_by integer
);


ALTER TABLE bib4template.vocabulary_indexing_groups OWNER TO biblivre;

--
-- Name: vocabulary_indexing_groups_id_seq; Type: SEQUENCE; Schema: bib4template; Owner: biblivre
--

CREATE SEQUENCE bib4template.vocabulary_indexing_groups_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE bib4template.vocabulary_indexing_groups_id_seq OWNER TO biblivre;

--
-- Name: vocabulary_indexing_groups_id_seq; Type: SEQUENCE OWNED BY; Schema: bib4template; Owner: biblivre
--

ALTER SEQUENCE bib4template.vocabulary_indexing_groups_id_seq OWNED BY bib4template.vocabulary_indexing_groups.id;


--
-- Name: vocabulary_records; Type: TABLE; Schema: bib4template; Owner: biblivre
--

CREATE TABLE bib4template.vocabulary_records (
    id integer NOT NULL,
    iso2709 text NOT NULL,
    material character varying(20),
    database character varying(10) DEFAULT 'main'::character varying NOT NULL,
    created timestamp without time zone DEFAULT now() NOT NULL,
    created_by integer,
    modified timestamp without time zone DEFAULT now() NOT NULL,
    modified_by integer
);


ALTER TABLE bib4template.vocabulary_records OWNER TO biblivre;

--
-- Name: vocabulary_records_id_seq; Type: SEQUENCE; Schema: bib4template; Owner: biblivre
--

CREATE SEQUENCE bib4template.vocabulary_records_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE bib4template.vocabulary_records_id_seq OWNER TO biblivre;

--
-- Name: vocabulary_records_id_seq; Type: SEQUENCE OWNED BY; Schema: bib4template; Owner: biblivre
--

ALTER SEQUENCE bib4template.vocabulary_records_id_seq OWNED BY bib4template.vocabulary_records.id;


--
-- Name: vocabulary_search_results; Type: TABLE; Schema: bib4template; Owner: biblivre
--

CREATE TABLE bib4template.vocabulary_search_results (
    search_id integer NOT NULL,
    indexing_group_id integer NOT NULL,
    record_id integer NOT NULL
);


ALTER TABLE bib4template.vocabulary_search_results OWNER TO biblivre;

--
-- Name: vocabulary_searches; Type: TABLE; Schema: bib4template; Owner: biblivre
--

CREATE TABLE bib4template.vocabulary_searches (
    id integer NOT NULL,
    parameters text NOT NULL,
    created timestamp without time zone DEFAULT now() NOT NULL,
    created_by integer
);


ALTER TABLE bib4template.vocabulary_searches OWNER TO biblivre;

--
-- Name: vocabulary_searches_id_seq; Type: SEQUENCE; Schema: bib4template; Owner: biblivre
--

CREATE SEQUENCE bib4template.vocabulary_searches_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE bib4template.vocabulary_searches_id_seq OWNER TO biblivre;

--
-- Name: vocabulary_searches_id_seq; Type: SEQUENCE OWNED BY; Schema: bib4template; Owner: biblivre
--

ALTER SEQUENCE bib4template.vocabulary_searches_id_seq OWNED BY bib4template.vocabulary_searches.id;


--
-- Name: z3950_addresses; Type: TABLE; Schema: bib4template; Owner: biblivre
--

CREATE TABLE bib4template.z3950_addresses (
    id integer NOT NULL,
    name character varying NOT NULL,
    url character varying NOT NULL,
    port integer NOT NULL,
    collection character varying DEFAULT 'default'::character varying NOT NULL
);


ALTER TABLE bib4template.z3950_addresses OWNER TO biblivre;

--
-- Name: z3950_addresses_id_seq; Type: SEQUENCE; Schema: bib4template; Owner: biblivre
--

CREATE SEQUENCE bib4template.z3950_addresses_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE bib4template.z3950_addresses_id_seq OWNER TO biblivre;

--
-- Name: z3950_addresses_id_seq; Type: SEQUENCE OWNED BY; Schema: bib4template; Owner: biblivre
--

ALTER SEQUENCE bib4template.z3950_addresses_id_seq OWNED BY bib4template.z3950_addresses.id;


--
-- Name: authorities_idx_autocomplete id; Type: DEFAULT; Schema: bib4template; Owner: biblivre
--

ALTER TABLE ONLY bib4template.authorities_idx_autocomplete ALTER COLUMN id SET DEFAULT nextval('bib4template.authorities_idx_autocomplete_id_seq'::regclass);


--
-- Name: authorities_indexing_groups id; Type: DEFAULT; Schema: bib4template; Owner: biblivre
--

ALTER TABLE ONLY bib4template.authorities_indexing_groups ALTER COLUMN id SET DEFAULT nextval('bib4template.authorities_indexing_groups_id_seq'::regclass);


--
-- Name: authorities_records id; Type: DEFAULT; Schema: bib4template; Owner: biblivre
--

ALTER TABLE ONLY bib4template.authorities_records ALTER COLUMN id SET DEFAULT nextval('bib4template.authorities_records_id_seq'::regclass);


--
-- Name: authorities_searches id; Type: DEFAULT; Schema: bib4template; Owner: biblivre
--

ALTER TABLE ONLY bib4template.authorities_searches ALTER COLUMN id SET DEFAULT nextval('bib4template.authorities_searches_id_seq'::regclass);


--
-- Name: backups id; Type: DEFAULT; Schema: bib4template; Owner: biblivre
--

ALTER TABLE ONLY bib4template.backups ALTER COLUMN id SET DEFAULT nextval('bib4template.backups_id_seq'::regclass);


--
-- Name: biblio_holdings id; Type: DEFAULT; Schema: bib4template; Owner: biblivre
--

ALTER TABLE ONLY bib4template.biblio_holdings ALTER COLUMN id SET DEFAULT nextval('bib4template.biblio_holdings_id_seq'::regclass);


--
-- Name: biblio_idx_autocomplete id; Type: DEFAULT; Schema: bib4template; Owner: biblivre
--

ALTER TABLE ONLY bib4template.biblio_idx_autocomplete ALTER COLUMN id SET DEFAULT nextval('bib4template.biblio_idx_autocomplete_id_seq'::regclass);


--
-- Name: biblio_indexing_groups id; Type: DEFAULT; Schema: bib4template; Owner: biblivre
--

ALTER TABLE ONLY bib4template.biblio_indexing_groups ALTER COLUMN id SET DEFAULT nextval('bib4template.biblio_indexing_groups_id_seq'::regclass);


--
-- Name: biblio_records id; Type: DEFAULT; Schema: bib4template; Owner: biblivre
--

ALTER TABLE ONLY bib4template.biblio_records ALTER COLUMN id SET DEFAULT nextval('bib4template.biblio_records_id_seq'::regclass);


--
-- Name: biblio_searches id; Type: DEFAULT; Schema: bib4template; Owner: biblivre
--

ALTER TABLE ONLY bib4template.biblio_searches ALTER COLUMN id SET DEFAULT nextval('bib4template.biblio_searches_id_seq'::regclass);


--
-- Name: digital_media id; Type: DEFAULT; Schema: bib4template; Owner: biblivre
--

ALTER TABLE ONLY bib4template.digital_media ALTER COLUMN id SET DEFAULT nextval('bib4template.digital_media_id_seq'::regclass);


--
-- Name: holding_creation_counter id; Type: DEFAULT; Schema: bib4template; Owner: biblivre
--

ALTER TABLE ONLY bib4template.holding_creation_counter ALTER COLUMN id SET DEFAULT nextval('bib4template.holding_creation_counter_id_seq'::regclass);


--
-- Name: lending_fines id; Type: DEFAULT; Schema: bib4template; Owner: biblivre
--

ALTER TABLE ONLY bib4template.lending_fines ALTER COLUMN id SET DEFAULT nextval('bib4template.lending_fines_id_seq'::regclass);


--
-- Name: lendings id; Type: DEFAULT; Schema: bib4template; Owner: biblivre
--

ALTER TABLE ONLY bib4template.lendings ALTER COLUMN id SET DEFAULT nextval('bib4template.lendings_id_seq'::regclass);


--
-- Name: logins id; Type: DEFAULT; Schema: bib4template; Owner: biblivre
--

ALTER TABLE ONLY bib4template.logins ALTER COLUMN id SET DEFAULT nextval('bib4template.logins_id_seq'::regclass);


--
-- Name: orders id; Type: DEFAULT; Schema: bib4template; Owner: biblivre
--

ALTER TABLE ONLY bib4template.orders ALTER COLUMN id SET DEFAULT nextval('bib4template.orders_id_seq'::regclass);


--
-- Name: reservations id; Type: DEFAULT; Schema: bib4template; Owner: biblivre
--

ALTER TABLE ONLY bib4template.reservations ALTER COLUMN id SET DEFAULT nextval('bib4template.reservations_id_seq'::regclass);


--
-- Name: users id; Type: DEFAULT; Schema: bib4template; Owner: biblivre
--

ALTER TABLE ONLY bib4template.users ALTER COLUMN id SET DEFAULT nextval('bib4template.users_id_seq'::regclass);


--
-- Name: users_types id; Type: DEFAULT; Schema: bib4template; Owner: biblivre
--

ALTER TABLE ONLY bib4template.users_types ALTER COLUMN id SET DEFAULT nextval('bib4template.users_types_id_seq'::regclass);


--
-- Name: vocabulary_idx_autocomplete id; Type: DEFAULT; Schema: bib4template; Owner: biblivre
--

ALTER TABLE ONLY bib4template.vocabulary_idx_autocomplete ALTER COLUMN id SET DEFAULT nextval('bib4template.vocabulary_idx_autocomplete_id_seq'::regclass);


--
-- Name: vocabulary_indexing_groups id; Type: DEFAULT; Schema: bib4template; Owner: biblivre
--

ALTER TABLE ONLY bib4template.vocabulary_indexing_groups ALTER COLUMN id SET DEFAULT nextval('bib4template.vocabulary_indexing_groups_id_seq'::regclass);


--
-- Name: vocabulary_records id; Type: DEFAULT; Schema: bib4template; Owner: biblivre
--

ALTER TABLE ONLY bib4template.vocabulary_records ALTER COLUMN id SET DEFAULT nextval('bib4template.vocabulary_records_id_seq'::regclass);


--
-- Name: vocabulary_searches id; Type: DEFAULT; Schema: bib4template; Owner: biblivre
--

ALTER TABLE ONLY bib4template.vocabulary_searches ALTER COLUMN id SET DEFAULT nextval('bib4template.vocabulary_searches_id_seq'::regclass);


--
-- Name: z3950_addresses id; Type: DEFAULT; Schema: bib4template; Owner: biblivre
--

ALTER TABLE ONLY bib4template.z3950_addresses ALTER COLUMN id SET DEFAULT nextval('bib4template.z3950_addresses_id_seq'::regclass);


--
-- Data for Name: access_cards; Type: TABLE DATA; Schema: bib4template; Owner: biblivre
--

COPY bib4template.access_cards (id, code, status, created, created_by, modified, modified_by) FROM stdin;
\.


--
-- Data for Name: access_control; Type: TABLE DATA; Schema: bib4template; Owner: biblivre
--

COPY bib4template.access_control (id, access_card_id, user_id, arrival_time, departure_time, created, created_by, modified, modified_by) FROM stdin;
\.


--
-- Data for Name: authorities_brief_formats; Type: TABLE DATA; Schema: bib4template; Owner: biblivre
--

COPY bib4template.authorities_brief_formats (datafield, format, sort_order, created, created_by, modified, modified_by) FROM stdin;
100	${a}_{; }${c}_{ - }${d}	1	2014-03-20 12:20:01.029	\N	2014-03-20 12:20:01.029	\N
670	${a}	7	2014-03-20 12:23:36.822	\N	2014-03-20 12:23:36.822	\N
400	${a}	4	2014-03-20 12:22:53.502	\N	2014-03-20 12:22:53.502	\N
410	${a}	5	2014-03-20 12:23:04.503	\N	2014-03-20 12:23:04.503	\N
411	${a}	6	2014-03-20 12:23:14.566	\N	2014-03-20 12:23:14.566	\N
111	${a}_{; }${c}_{ - }${d}	3	2014-03-20 12:22:40.585	\N	2014-03-20 12:22:40.585	\N
110	${a}_{; }${b}_{; }${c}_{ - }${d}	2	2014-03-20 12:22:07.272	\N	2014-03-20 12:22:07.272	\N
\.


--
-- Data for Name: authorities_form_datafields; Type: TABLE DATA; Schema: bib4template; Owner: biblivre
--

COPY bib4template.authorities_form_datafields (datafield, collapsed, repeatable, indicator_1, indicator_2, material_type, created, created_by, modified, modified_by, sort_order) FROM stdin;
100	f	f	0,1,2,3		100	2014-02-08 15:21:25.813358	\N	2014-02-08 15:21:25.813358	\N	100
110	f	f	0,1,2		110	2014-02-08 15:21:25.813358	\N	2014-02-08 15:21:25.813358	\N	110
111	f	f	0,1,2		111	2014-02-08 15:21:25.813358	\N	2014-02-08 15:21:25.813358	\N	111
400	f	t			100	2014-02-08 15:21:25.813358	\N	2014-02-08 15:21:25.813358	\N	400
410	f	t			110	2014-02-08 15:21:25.813358	\N	2014-02-08 15:21:25.813358	\N	410
411	f	t			111	2014-02-08 15:21:25.813358	\N	2014-02-08 15:21:25.813358	\N	411
670	f	t			100,110,111	2014-02-08 15:21:25.813358	\N	2014-02-08 15:21:25.813358	\N	670
\.


--
-- Data for Name: authorities_form_subfields; Type: TABLE DATA; Schema: bib4template; Owner: biblivre
--

COPY bib4template.authorities_form_subfields (datafield, subfield, collapsed, repeatable, created, created_by, modified, modified_by, autocomplete_type, sort_order) FROM stdin;
100	a	f	f	2014-02-08 15:26:31.667337	\N	2014-02-08 15:26:31.667337	\N	disabled	197
100	b	f	f	2014-02-08 15:26:31.667337	\N	2014-02-08 15:26:31.667337	\N	disabled	198
100	c	f	t	2014-02-08 15:26:31.667337	\N	2014-02-08 15:26:31.667337	\N	disabled	199
100	d	f	f	2014-02-08 15:26:31.667337	\N	2014-02-08 15:26:31.667337	\N	disabled	200
100	q	f	f	2014-02-08 15:26:31.667337	\N	2014-02-08 15:26:31.667337	\N	disabled	213
400	a	f	f	2014-02-08 15:26:31.667337	\N	2014-02-08 15:26:31.667337	\N	disabled	497
670	a	f	f	2014-02-08 15:26:31.667337	\N	2014-02-08 15:26:31.667337	\N	disabled	767
670	b	f	f	2014-02-08 15:26:31.667337	\N	2014-02-08 15:26:31.667337	\N	disabled	768
111	a	f	f	2014-02-08 15:26:31.667337	\N	2014-02-08 15:26:31.667337	\N	disabled	208
111	c	f	f	2014-02-08 15:26:31.667337	\N	2014-02-08 15:26:31.667337	\N	disabled	210
111	d	f	f	2014-02-08 15:26:31.667337	\N	2014-02-08 15:26:31.667337	\N	disabled	211
111	e	f	t	2014-02-08 15:26:31.667337	\N	2014-02-08 15:26:31.667337	\N	disabled	212
111	g	f	f	2014-02-08 15:26:31.667337	\N	2014-02-08 15:26:31.667337	\N	disabled	214
111	k	f	f	2014-02-08 15:26:31.667337	\N	2014-02-08 15:26:31.667337	\N	disabled	218
111	n	f	t	2014-02-08 15:26:31.667337	\N	2014-02-08 15:26:31.667337	\N	disabled	221
411	a	f	f	2014-02-08 15:26:31.667337	\N	2014-02-08 15:26:31.667337	\N	disabled	508
110	a	f	f	2014-02-08 15:26:31.667337	\N	2014-02-08 15:26:31.667337	\N	disabled	207
110	b	f	t	2014-02-08 15:26:31.667337	\N	2014-02-08 15:26:31.667337	\N	disabled	208
110	c	f	f	2014-02-08 15:26:31.667337	\N	2014-02-08 15:26:31.667337	\N	disabled	209
110	d	f	t	2014-02-08 15:26:31.667337	\N	2014-02-08 15:26:31.667337	\N	disabled	210
110	l	f	f	2014-02-08 15:26:31.667337	\N	2014-02-08 15:26:31.667337	\N	disabled	218
110	n	f	t	2014-02-08 15:26:31.667337	\N	2014-02-08 15:26:31.667337	\N	disabled	220
410	a	f	f	2014-02-08 15:26:31.667337	\N	2014-02-08 15:26:31.667337	\N	disabled	507
\.


--
-- Data for Name: authorities_idx_autocomplete; Type: TABLE DATA; Schema: bib4template; Owner: biblivre
--

COPY bib4template.authorities_idx_autocomplete (id, datafield, subfield, word, phrase, record_id) FROM stdin;
\.


--
-- Data for Name: authorities_idx_fields; Type: TABLE DATA; Schema: bib4template; Owner: biblivre
--

COPY bib4template.authorities_idx_fields (record_id, indexing_group_id, word, datafield) FROM stdin;
\.


--
-- Data for Name: authorities_idx_sort; Type: TABLE DATA; Schema: bib4template; Owner: biblivre
--

COPY bib4template.authorities_idx_sort (record_id, indexing_group_id, phrase, ignore_chars_count) FROM stdin;
\.


--
-- Data for Name: authorities_indexing_groups; Type: TABLE DATA; Schema: bib4template; Owner: biblivre
--

COPY bib4template.authorities_indexing_groups (id, translation_key, datafields, sortable, default_sort, created, created_by, modified, modified_by) FROM stdin;
0	all	\N	f	f	2014-03-04 11:09:07.241	\N	2014-03-04 11:09:07.241	\N
1	author	100_a	t	t	2014-03-04 11:13:31.512	\N	2014-03-04 11:13:31.512	\N
2	entity	110_a	t	f	2014-03-04 11:13:46.059	\N	2014-03-04 11:13:46.059	\N
3	event	111_a	t	f	2014-03-04 11:14:39.973	\N	2014-03-04 11:14:39.973	\N
4	other_name	400_a, 410_a, 411_a	t	f	2014-03-04 11:14:55.617	\N	2014-03-04 11:14:55.617	\N
\.


--
-- Data for Name: authorities_records; Type: TABLE DATA; Schema: bib4template; Owner: biblivre
--

COPY bib4template.authorities_records (id, iso2709, material, database, created, created_by, modified, modified_by) FROM stdin;
\.


--
-- Data for Name: authorities_search_results; Type: TABLE DATA; Schema: bib4template; Owner: biblivre
--

COPY bib4template.authorities_search_results (search_id, indexing_group_id, record_id) FROM stdin;
\.


--
-- Data for Name: authorities_searches; Type: TABLE DATA; Schema: bib4template; Owner: biblivre
--

COPY bib4template.authorities_searches (id, parameters, created, created_by) FROM stdin;
\.


--
-- Data for Name: backups; Type: TABLE DATA; Schema: bib4template; Owner: biblivre
--

COPY bib4template.backups (id, created, path, schemas, type, scope, downloaded, steps, current_step) FROM stdin;
\.


--
-- Data for Name: biblio_brief_formats; Type: TABLE DATA; Schema: bib4template; Owner: biblivre
--

COPY bib4template.biblio_brief_formats (datafield, format, sort_order, created, created_by, modified, modified_by) FROM stdin;
260	${a}_{: }${b}_{, }${c}	20	2014-02-01 12:04:44.576993	\N	2014-02-01 12:04:44.576993	\N
650	${a}_{ - }${x}_{ - }${y}_{ - }${z}	41	2014-02-01 12:06:20.547937	\N	2014-02-01 12:06:20.547937	\N
651	${a}_{ - }${x}_{ - }${y}_{ - }${z}	42	2014-02-01 12:06:27.116236	\N	2014-02-01 12:06:27.116236	\N
699	${a}_{ - }${x}_{ - }${y}_{ - }${z}	43	2014-02-01 12:06:34.276548	\N	2014-02-01 12:06:34.276548	\N
306	${a}	25	2014-02-01 12:13:07.424794	\N	2014-02-01 12:13:07.424794	\N
520	${a}	27	2014-02-01 12:13:19.273647	\N	2014-02-01 12:13:19.273647	\N
020	${a}	22	2014-02-01 12:04:17.840354	\N	2014-02-01 12:04:17.840354	\N
022	${a}	23	2014-02-01 12:04:24.056557	\N	2014-02-01 12:04:24.056557	\N
024	${a}	24	2014-02-01 12:04:30.936737	\N	2014-02-01 12:04:30.936737	\N
256	${a}	44	2014-02-01 12:15:25.005212	\N	2014-02-01 12:15:25.005212	\N
043	${a}	45	2014-02-01 12:15:33.581457	\N	2014-02-01 12:15:33.581457	\N
045	${a}	46	2014-02-01 12:15:42.981651	\N	2014-02-01 12:15:42.981651	\N
255	${a}	47	2014-02-01 12:15:46.853963	\N	2014-02-01 12:15:46.853963	\N
041	${a}	48	2014-02-01 12:15:50.093832	\N	2014-02-01 12:15:50.093832	\N
090	${a}_{ }${b}_{ }${c}_{ }${d}	49	2014-02-01 12:05:17.018043	\N	2014-02-01 12:05:17.018043	\N
310	${a}	50	2014-02-01 12:16:08.046403	\N	2014-02-01 12:16:08.046403	\N
362	${a}	51	2014-02-01 12:16:11.822932	\N	2014-02-01 12:16:11.822932	\N
555	${a}	52	2014-02-01 12:16:16.422789	\N	2014-02-01 12:16:16.422789	\N
852	${a}	53	2014-02-01 12:16:20.998809	\N	2014-02-01 12:16:20.998809	\N
400	${a}	5	2014-02-01 12:03:15.21457	\N	2014-02-01 12:03:15.21457	\N
410	${a}	6	2014-02-01 12:03:19.934289	\N	2014-02-01 12:03:19.934289	\N
411	${a}	7	2014-02-01 12:03:27.070922	\N	2014-02-01 12:03:27.070922	\N
243	${a}_{ }${f}	12	2014-02-01 12:09:14.089412	\N	2014-02-01 12:09:14.089412	\N
240	${a}	13	2014-02-01 12:09:24.050018	\N	2014-02-01 12:09:24.050018	\N
730	${a}	14	2014-02-01 12:09:30.866228	\N	2014-02-01 12:09:30.866228	\N
740	${a}_{ }${n}_{ }${p}	15	2014-02-01 12:09:44.610667	\N	2014-02-01 12:09:44.610667	\N
250	${a}	17	2014-02-01 12:10:18.923422	\N	2014-02-01 12:10:18.923422	\N
257	${a}	18	2014-02-01 12:10:36.436278	\N	2014-02-01 12:10:36.436278	\N
258	${a}	19	2014-02-01 12:10:41.228113	\N	2014-02-01 12:10:41.228113	\N
300	${a}_{ }${b}_{ }${c}_{ }${e}	21	2014-02-01 12:12:58.160709	\N	2014-02-01 12:12:58.160709	\N
013	${a}_{; }${b}_{; }${c}_{; }${d}_{; }${e}_{; }${f}	56	2014-02-01 12:18:17.042647	\N	2014-02-01 12:18:17.042647	\N
095	${a}	57	2014-02-01 12:18:25.330589	\N	2014-02-01 12:18:25.330589	\N
500	${a}	28	2014-02-01 12:13:34.913539	\N	2014-02-01 12:13:34.913539	\N
504	${a}	29	2014-02-01 12:13:43.050064	\N	2014-02-01 12:13:43.050064	\N
505	${a}	30	2014-02-01 12:13:49.402136	\N	2014-02-01 12:13:49.402136	\N
521	${a}	31	2014-02-01 12:13:56.762462	\N	2014-02-01 12:13:56.762462	\N
534	${a}	32	2014-02-01 12:14:02.570381	\N	2014-02-01 12:14:02.570381	\N
590	${a}	33	2014-02-01 12:14:06.138512	\N	2014-02-01 12:14:06.138512	\N
502	${a}	34	2014-02-01 12:14:11.876239	\N	2014-02-01 12:14:11.876239	\N
506	${a}	35	2014-02-01 12:14:17.131068	\N	2014-02-01 12:14:17.131068	\N
876	${h}	36	2014-02-01 12:14:30.355534	\N	2014-02-01 12:14:30.355534	\N
080	${a}_{ }${2}	54	2014-02-01 12:16:38.015323	\N	2014-02-01 12:16:38.015323	\N
082	${a}_{ }${2}	55	2014-02-01 12:17:24.376916	\N	2014-02-01 12:17:24.376916	\N
501	${a}	28	2022-12-04 11:05:57.138539	\N	2022-12-04 11:05:57.138539	\N
530	${a}	31	2022-12-04 11:05:57.138539	\N	2022-12-04 11:05:57.138539	\N
595	${a}	33	2022-12-04 11:05:57.138539	\N	2022-12-04 11:05:57.138539	\N
245	${a}_{: }${b}_{ / }${c}	11	2013-05-11 14:09:53.242277	\N	2013-05-11 14:09:53.242277	\N
100	${a}_{ - }${d}_{ }(${q})	1	2013-05-11 14:09:22.681914	\N	2013-05-11 14:09:22.681914	\N
110	${a}_{. }${b. }_{ }(${n}_{ : }${d}_{ : }${c})	2	2013-05-11 14:09:28.849574	\N	2013-05-11 14:09:28.849574	\N
111	${a}_{. }(${n}_{ : }${d}_{ : }${c})	3	2013-05-11 14:09:33.10575	\N	2013-05-11 14:09:33.10575	\N
130	${a}_{. }${l}_{. }${f}	4	2014-02-01 11:43:41.882279	\N	2014-02-01 11:43:41.882279	\N
600	${a}_{. }${b}_{. }${c}_{. }${d}_{ - }${x}_{ - }${y}_{ - }${z}	37	2014-02-01 12:05:47.099015	\N	2014-02-01 12:05:47.099015	\N
610	${a}_{. }${b}_{ - }${x}_{ - }${y}_{ - }${z}	38	2014-02-01 12:05:55.971671	\N	2014-02-01 12:05:55.971671	\N
611	${a}_{. }${b. }_{ }(${n}_{ : }${d}_{ : }${c})_{ - }${x}_{ - }${y}_{ - }${z}	39	2014-02-01 12:06:02.963596	\N	2014-02-01 12:06:02.963596	\N
700	${a}_{. }${d}	8	2014-02-01 11:44:15.995588	\N	2014-02-01 11:44:15.995588	\N
710	${a}_{. }${b. }_{ }(${n}_{ : }${d}_{ : }${c})	9	2014-02-01 11:44:21.019794	\N	2014-02-01 11:44:21.019794	\N
711	${a}_{. }${b. }_{ }(${n}_{ : }${d}_{ : }${c})	10	2014-02-01 11:44:27.579924	\N	2014-02-01 11:44:27.579924	\N
630	${a}_{. }(${d})	40	2014-02-01 12:06:13.363821	\N	2014-02-01 12:06:13.363821	\N
490	(${a}_{ ; }${v})	26	2014-02-01 12:13:12.984999	\N	2014-02-01 12:13:12.984999	\N
830	${a}_{. }${p}_{ ; }${v}	16	2014-02-01 12:09:56.027131	\N	2014-02-01 12:09:56.027131	\N
\.


--
-- Data for Name: biblio_form_datafields; Type: TABLE DATA; Schema: bib4template; Owner: biblivre
--

COPY bib4template.biblio_form_datafields (datafield, collapsed, repeatable, indicator_1, indicator_2, material_type, created, created_by, modified, modified_by, sort_order) FROM stdin;
020	f	f			book	2013-04-13 13:42:03.23405	\N	2013-04-13 13:42:03.23405	\N	20
255	f	t			map	2013-04-13 13:42:03.23405	\N	2013-04-13 13:42:03.23405	\N	255
013	f	t			book,manuscript,pamphlet,thesis,computer_legible,map,movie,score,object_3d,photo,periodic,articles,music,nonmusical_sound	2013-04-13 13:42:03.23405	\N	2013-04-13 13:42:03.23405	\N	13
022	f	f			book,periodic,articles	2013-04-13 13:42:03.23405	\N	2013-04-13 13:42:03.23405	\N	22
029	f	t			score,music,nonmusical_sound	2013-04-13 13:42:03.23405	\N	2013-04-13 13:42:03.23405	\N	29
040	f	f			book,manuscript,pamphlet,thesis,computer_legible,map,movie,score,photo,periodic,articles,music,nonmusical_sound	2013-04-13 13:42:03.23405	\N	2013-04-13 13:42:03.23405	\N	40
045	f	f	_,0,1,2		map	2013-04-13 13:42:03.23405	\N	2013-04-13 13:42:03.23405	\N	45
080	f	f			book,manuscript,pamphlet,thesis,computer_legible,map,movie,score,object_3d,photo,periodic,articles,music,nonmusical_sound	2013-04-13 13:42:03.23405	\N	2013-04-13 13:42:03.23405	\N	80
082	f	f			book,manuscript,pamphlet,thesis,computer_legible,map,movie,score,object_3d,photo,periodic,articles,music,nonmusical_sound	2013-04-13 13:42:03.23405	\N	2013-04-13 13:42:03.23405	\N	82
090	f	f			book,manuscript,pamphlet,thesis,computer_legible,map,movie,score,object_3d,photo,periodic,articles,music,nonmusical_sound	2013-04-13 13:42:03.23405	\N	2013-04-13 13:42:03.23405	\N	90
095	f	f			book,manuscript,pamphlet,thesis,computer_legible,map,movie,score,object_3d,photo,periodic,articles,music,nonmusical_sound	2013-04-13 13:42:03.23405	\N	2013-04-13 13:42:03.23405	\N	95
100	f	f	1,0,2,3		book,manuscript,pamphlet,thesis,computer_legible,map,movie,score,object_3d,photo,articles,music,nonmusical_sound	2013-04-13 13:42:03.23405	\N	2013-04-13 13:42:03.23405	\N	100
110	f	f	0,1,2		book,manuscript,pamphlet,thesis,computer_legible,map,movie,score,object_3d,photo,articles,music,nonmusical_sound	2013-04-13 13:42:03.23405	\N	2013-04-13 13:42:03.23405	\N	110
111	f	f	0,1,2		book,manuscript,pamphlet,thesis,computer_legible,map,movie,score,object_3d,photo,articles,music,nonmusical_sound	2013-04-13 13:42:03.23405	\N	2013-04-13 13:42:03.23405	\N	111
245	f	f	1,0	0,1,2,3,4,5,6,7,8,9	book,manuscript,pamphlet,thesis,computer_legible,map,movie,score,object_3d,photo,periodic,articles,music,nonmusical_sound	2013-04-13 13:42:03.23405	\N	2013-04-13 13:42:03.23405	\N	245
250	f	f			book,thesis	2013-04-13 13:42:03.23405	\N	2013-04-13 13:42:03.23405	\N	250
256	f	f			computer_legible	2013-04-13 13:42:03.23405	\N	2013-04-13 13:42:03.23405	\N	256
257	f	f			movie	2013-04-13 13:42:03.23405	\N	2013-04-13 13:42:03.23405	\N	257
258	f	f			photo	2013-04-13 13:42:03.23405	\N	2013-04-13 13:42:03.23405	\N	258
260	f	t			book,manuscript,thesis,computer_legible,map,movie,score,photo,periodic,articles,music,nonmusical_sound	2013-04-13 13:42:03.23405	\N	2013-04-13 13:42:03.23405	\N	260
300	f	f			book,manuscript,thesis,computer_legible,map,movie,score,object_3d,photo,periodic,articles,music,nonmusical_sound	2013-04-13 13:42:03.23405	\N	2013-04-13 13:42:03.23405	\N	300
306	f	t			movie,music,nonmusical_sound	2013-04-13 13:42:03.23405	\N	2013-04-13 13:42:03.23405	\N	306
340	f	t			map	2013-04-13 13:42:03.23405	\N	2013-04-13 13:42:03.23405	\N	340
342	f	t	0,1	0,1,2,3,4,5,6,7,8	map	2013-04-13 13:42:03.23405	\N	2013-04-13 13:42:03.23405	\N	342
343	f	t			map	2013-04-13 13:42:03.23405	\N	2013-04-13 13:42:03.23405	\N	343
490	f	t	0,1		book,movie,score,object_3d,photo,periodic,articles,music,nonmusical_sound	2013-04-13 13:42:03.23405	\N	2013-04-13 13:42:03.23405	\N	490
500	f	t			book,manuscript,pamphlet,thesis,computer_legible,map,movie,score,object_3d,photo,periodic,articles,music,nonmusical_sound	2013-04-13 13:42:03.23405	\N	2013-04-13 13:42:03.23405	\N	500
501	f	t			book,manuscript,pamphlet,thesis,computer_legible,map,movie,score,object_3d,photo,periodic,articles,music,nonmusical_sound	2013-04-13 13:42:03.23405	\N	2013-04-13 13:42:03.23405	\N	501
502	f	t			book,manuscript,pamphlet,thesis,computer_legible,map,movie,score,object_3d,photo,periodic,articles,music,nonmusical_sound	2013-04-13 13:42:03.23405	\N	2013-04-13 13:42:03.23405	\N	502
504	f	t			book,manuscript,pamphlet,thesis,computer_legible,map,movie,score,object_3d,photo,periodic,articles,music,nonmusical_sound	2013-04-13 13:42:03.23405	\N	2013-04-13 13:42:03.23405	\N	504
505	f	t			book,manuscript,pamphlet,thesis,computer_legible,map,movie,score,object_3d,photo,periodic,articles,music,nonmusical_sound	2013-04-13 13:42:03.23405	\N	2013-04-13 13:42:03.23405	\N	505
520	f	t			book,manuscript,pamphlet,thesis,computer_legible,map,movie,score,object_3d,photo,periodic,articles,music,nonmusical_sound	2013-04-13 13:42:03.23405	\N	2013-04-13 13:42:03.23405	\N	520
521	f	t			book,manuscript,pamphlet,thesis,computer_legible,map,movie,score,object_3d,photo,periodic,articles,music,nonmusical_sound	2013-04-13 13:42:03.23405	\N	2013-04-13 13:42:03.23405	\N	521
530	f	t			book,manuscript,pamphlet,thesis,computer_legible,map,movie,score,object_3d,photo,periodic,articles,music,nonmusical_sound	2013-04-13 13:42:03.23405	\N	2013-04-13 13:42:03.23405	\N	530
534	f	t			book,manuscript,pamphlet,thesis,computer_legible,map,movie,score,object_3d,photo,periodic,articles,music,nonmusical_sound	2013-04-13 13:42:03.23405	\N	2013-04-13 13:42:03.23405	\N	534
590	f	t			book,manuscript,pamphlet,thesis,computer_legible,map,movie,score,object_3d,photo,periodic,articles,music,nonmusical_sound	2013-04-13 13:42:03.23405	\N	2013-04-13 13:42:03.23405	\N	590
595	f	t			book,manuscript,pamphlet,thesis,computer_legible,map,movie,score,object_3d,photo,periodic,articles,music,nonmusical_sound	2013-04-13 13:42:03.23405	\N	2013-04-13 13:42:03.23405	\N	595
600	f	t	0,1,2,3		book,manuscript,pamphlet,thesis,computer_legible,map,movie,score,object_3d,photo,periodic,articles,music,nonmusical_sound	2013-04-13 13:42:03.23405	\N	2013-04-13 13:42:03.23405	\N	600
610	f	t	0,1,2		book,manuscript,pamphlet,thesis,computer_legible,map,movie,score,object_3d,photo,periodic,articles,music,nonmusical_sound	2013-04-13 13:42:03.23405	\N	2013-04-13 13:42:03.23405	\N	610
611	f	t	0,1,2		book,manuscript,pamphlet,thesis,computer_legible,map,movie,score,object_3d,photo,periodic,articles,music,nonmusical_sound	2013-04-13 13:42:03.23405	\N	2013-04-13 13:42:03.23405	\N	611
650	f	t			book,manuscript,pamphlet,thesis,computer_legible,map,movie,score,object_3d,photo,periodic,articles,music,nonmusical_sound	2013-04-13 13:42:03.23405	\N	2013-04-13 13:42:03.23405	\N	650
651	f	t			book,manuscript,pamphlet,thesis,computer_legible,map,movie,score,object_3d,photo,periodic,articles,music,nonmusical_sound	2013-04-13 13:42:03.23405	\N	2013-04-13 13:42:03.23405	\N	651
700	f	t	1,0,2,3	_,2	book,manuscript,pamphlet,thesis,computer_legible,map,movie,score,object_3d,photo,periodic,articles,music,nonmusical_sound	2013-04-13 13:42:03.23405	\N	2013-04-13 13:42:03.23405	\N	700
710	f	t	0,1,2	_,2	book,manuscript,pamphlet,thesis,computer_legible,map,movie,score,object_3d,photo,periodic,articles,music,nonmusical_sound	2013-04-13 13:42:03.23405	\N	2013-04-13 13:42:03.23405	\N	710
711	f	t	0,1,2		book,manuscript,pamphlet,thesis,computer_legible,map,movie,score,object_3d,photo,periodic,articles,music,nonmusical_sound	2013-04-13 13:42:03.23405	\N	2013-04-13 13:42:03.23405	\N	711
856	f	t			book,manuscript,pamphlet,thesis,computer_legible,map,movie,score,object_3d,photo,periodic,articles,music,nonmusical_sound	2013-04-13 13:42:03.23405	\N	2013-04-13 13:42:03.23405	\N	856
630	f	t	0,1,2,3,4,5,6,7,8,9		book,manuscript,pamphlet,thesis,computer_legible,map,movie,score,object_3d,photo,periodic,articles,music,nonmusical_sound	2013-04-13 13:42:03.23405	\N	2013-04-13 13:42:03.23405	\N	630
730	f	t	0,1,2,3,4,5,6,7,8,9	_,2	book,manuscript,pamphlet,thesis,computer_legible,map,movie,score,object_3d,photo,periodic,articles,music,nonmusical_sound	2013-04-13 13:42:03.23405	\N	2013-04-13 13:42:03.23405	\N	730
740	f	t	0,1,2,3,4,5,6,7,8,9	_,2	book,manuscript,pamphlet,thesis,computer_legible,map,movie,score,object_3d,photo,periodic,articles,music,nonmusical_sound	2013-04-13 13:42:03.23405	\N	2013-04-13 13:42:03.23405	\N	740
043	f	f			map,periodic	2013-04-13 13:42:03.23405	\N	2013-04-13 13:42:03.23405	\N	43
830	f	t		0,1,2,3,4,5,6,7,8,9	book,manuscript,pamphlet,thesis,computer_legible,map,movie,score,object_3d,photo,periodic,articles,music,nonmusical_sound	2013-04-13 13:42:03.23405	\N	2013-04-13 13:42:03.23405	\N	830
041	f	t	0,1		book,manuscript,pamphlet,thesis,computer_legible,map,movie,score,photo,periodic,articles,music,nonmusical_sound	2013-04-13 13:42:03.23405	\N	2013-04-13 13:42:03.23405	\N	41
240	f	f	1,0	0,1,2,3,4,5,6,7,8,9	book,manuscript,pamphlet,thesis,computer_legible,map,movie,score,object_3d,photo,periodic,articles,music,nonmusical_sound	2013-04-13 13:42:03.23405	\N	2013-04-13 13:42:03.23405	\N	240
243	f	f	1,0	0,1,2,3,4,5,6,7,8,9	book,manuscript,pamphlet,thesis,computer_legible,map,movie,score,object_3d,photo,periodic,articles,music,nonmusical_sound	2013-04-13 13:42:03.23405	\N	2013-04-13 13:42:03.23405	\N	243
130	f	f	0,1,2,3,4,5,6,7,8,9		book,manuscript,pamphlet,thesis,computer_legible,map,movie,score,object_3d,photo,periodic,articles,music,nonmusical_sound	2013-04-13 13:42:03.23405	\N	2013-04-13 13:42:03.23405	\N	130
210	f	t	0,1	_,0	periodic	2014-04-28 19:25:12.931	1	2014-04-28 19:25:12.931	\N	210
246	f	t	0,1,2,3	_,0,1,2,3,4,5,6,7,8	periodic	2014-04-28 19:25:12.931	1	2014-04-28 19:25:12.931	\N	246
310	f	f			periodic	2014-04-28 19:25:12.931	1	2014-04-28 19:25:12.931	\N	310
321	f	f			periodic	2014-04-28 19:25:12.931	1	2014-04-28 19:25:12.931	\N	321
362	f	t	0,1		periodic	2014-04-28 19:25:12.931	1	2014-04-28 19:25:12.931	\N	362
515	f	t			periodic	2014-04-28 19:25:12.931	1	2014-04-28 19:25:12.931	\N	515
525	f	t			periodic	2014-04-28 19:25:12.931	1	2014-04-28 19:25:12.931	\N	525
550	f	t			periodic	2014-04-28 19:25:12.931	1	2014-04-28 19:25:12.931	\N	550
555	f	t	_,0,8		periodic	2014-04-28 19:25:12.931	1	2014-04-28 19:25:12.931	\N	555
580	f	t			periodic	2014-04-28 19:25:12.931	1	2014-04-28 19:25:12.931	\N	580
947	f	t			periodic	2014-04-28 19:25:12.931	1	2014-04-28 19:25:12.931	\N	947
\.


--
-- Data for Name: biblio_form_subfields; Type: TABLE DATA; Schema: bib4template; Owner: biblivre
--

COPY bib4template.biblio_form_subfields (datafield, subfield, collapsed, repeatable, created, created_by, modified, modified_by, autocomplete_type, sort_order) FROM stdin;
013	a	f	f	2013-04-13 13:43:11.351056	\N	2013-04-13 13:43:11.351056	\N	disabled	110
013	b	f	f	2013-04-13 13:43:11.351056	\N	2013-04-13 13:43:11.351056	\N	disabled	111
013	c	f	f	2013-04-13 13:43:11.351056	\N	2013-04-13 13:43:11.351056	\N	disabled	112
520	u	f	f	2013-04-13 13:43:11.351056	\N	2013-04-13 13:43:11.351056	\N	disabled	637
521	a	f	f	2013-04-13 13:43:11.351056	\N	2013-04-13 13:43:11.351056	\N	disabled	618
530	a	f	f	2013-04-13 13:43:11.351056	\N	2013-04-13 13:43:11.351056	\N	disabled	627
534	a	f	f	2013-04-13 13:43:11.351056	\N	2013-04-13 13:43:11.351056	\N	disabled	631
590	a	f	f	2013-04-13 13:43:11.351056	\N	2013-04-13 13:43:11.351056	\N	disabled	687
595	a	f	f	2013-04-13 13:43:11.351056	\N	2013-04-13 13:43:11.351056	\N	disabled	692
013	d	f	t	2013-04-13 13:43:11.351056	\N	2013-04-13 13:43:11.351056	\N	disabled	113
013	e	f	t	2013-04-13 13:43:11.351056	\N	2013-04-13 13:43:11.351056	\N	disabled	114
013	f	f	t	2013-04-13 13:43:11.351056	\N	2013-04-13 13:43:11.351056	\N	disabled	115
020	a	f	f	2013-04-13 13:43:11.351056	\N	2013-04-13 13:43:11.351056	\N	disabled	117
022	a	f	f	2013-04-13 13:43:11.351056	\N	2013-04-13 13:43:11.351056	\N	disabled	119
029	a	f	f	2013-04-13 13:43:11.351056	\N	2013-04-13 13:43:11.351056	\N	disabled	126
040	a	f	f	2013-04-13 13:43:11.351056	\N	2013-04-13 13:43:11.351056	\N	disabled	137
040	b	f	f	2013-04-13 13:43:11.351056	\N	2013-04-13 13:43:11.351056	\N	disabled	138
041	a	f	f	2013-04-13 13:43:11.351056	\N	2013-04-13 13:43:11.351056	\N	disabled	138
041	b	f	f	2013-04-13 13:43:11.351056	\N	2013-04-13 13:43:11.351056	\N	disabled	139
041	h	f	t	2013-04-13 13:43:11.351056	\N	2013-04-13 13:43:11.351056	\N	disabled	145
043	a	f	t	2013-04-13 13:43:11.351056	\N	2013-04-13 13:43:11.351056	\N	disabled	140
045	a	f	t	2013-04-13 13:43:11.351056	\N	2013-04-13 13:43:11.351056	\N	disabled	142
045	b	f	t	2013-04-13 13:43:11.351056	\N	2013-04-13 13:43:11.351056	\N	disabled	143
045	c	f	t	2013-04-13 13:43:11.351056	\N	2013-04-13 13:43:11.351056	\N	disabled	144
080	2	f	f	2013-04-13 13:43:11.351056	\N	2013-04-13 13:43:11.351056	\N	disabled	130
080	a	f	t	2013-04-13 13:43:11.351056	\N	2013-04-13 13:43:11.351056	\N	disabled	177
082	2	f	f	2013-04-13 13:43:11.351056	\N	2013-04-13 13:43:11.351056	\N	disabled	132
082	a	f	t	2013-04-13 13:43:11.351056	\N	2013-04-13 13:43:11.351056	\N	disabled	179
090	a	f	f	2013-04-13 13:43:11.351056	\N	2013-04-13 13:43:11.351056	\N	disabled	187
090	b	f	f	2013-04-13 13:43:11.351056	\N	2013-04-13 13:43:11.351056	\N	disabled	188
090	c	f	f	2013-04-13 13:43:11.351056	\N	2013-04-13 13:43:11.351056	\N	disabled	189
095	a	f	f	2013-04-13 13:43:11.351056	\N	2013-04-13 13:43:11.351056	\N	fixed_table	192
100	b	f	f	2013-04-13 13:43:11.351056	\N	2013-04-13 13:43:11.351056	\N	disabled	198
100	c	f	t	2013-04-13 13:43:11.351056	\N	2013-04-13 13:43:11.351056	\N	disabled	199
100	d	f	f	2013-04-13 13:43:11.351056	\N	2013-04-13 13:43:11.351056	\N	disabled	200
100	q	f	f	2013-04-13 13:43:11.351056	\N	2013-04-13 13:43:11.351056	\N	disabled	213
110	b	f	t	2013-04-13 13:43:11.351056	\N	2013-04-13 13:43:11.351056	\N	disabled	208
110	c	f	f	2013-04-13 13:43:11.351056	\N	2013-04-13 13:43:11.351056	\N	disabled	209
110	d	f	t	2013-04-13 13:43:11.351056	\N	2013-04-13 13:43:11.351056	\N	disabled	210
110	l	f	f	2013-04-13 13:43:11.351056	\N	2013-04-13 13:43:11.351056	\N	disabled	218
110	n	f	t	2013-04-13 13:43:11.351056	\N	2013-04-13 13:43:11.351056	\N	disabled	220
111	c	f	f	2013-04-13 13:43:11.351056	\N	2013-04-13 13:43:11.351056	\N	disabled	210
111	d	f	f	2013-04-13 13:43:11.351056	\N	2013-04-13 13:43:11.351056	\N	disabled	211
111	e	f	t	2013-04-13 13:43:11.351056	\N	2013-04-13 13:43:11.351056	\N	disabled	212
111	g	f	f	2013-04-13 13:43:11.351056	\N	2013-04-13 13:43:11.351056	\N	disabled	214
111	k	f	t	2013-04-13 13:43:11.351056	\N	2013-04-13 13:43:11.351056	\N	disabled	218
111	n	f	t	2013-04-13 13:43:11.351056	\N	2013-04-13 13:43:11.351056	\N	disabled	221
130	a	f	f	2013-04-13 13:43:11.351056	\N	2013-04-13 13:43:11.351056	\N	disabled	227
130	d	f	t	2013-04-13 13:43:11.351056	\N	2013-04-13 13:43:11.351056	\N	disabled	230
130	f	f	f	2013-04-13 13:43:11.351056	\N	2013-04-13 13:43:11.351056	\N	disabled	232
130	g	f	f	2013-04-13 13:43:11.351056	\N	2013-04-13 13:43:11.351056	\N	disabled	233
130	k	f	t	2013-04-13 13:43:11.351056	\N	2013-04-13 13:43:11.351056	\N	disabled	237
130	l	f	f	2013-04-13 13:43:11.351056	\N	2013-04-13 13:43:11.351056	\N	disabled	238
130	p	f	t	2013-04-13 13:43:11.351056	\N	2013-04-13 13:43:11.351056	\N	disabled	242
240	a	f	f	2013-04-13 13:43:11.351056	\N	2013-04-13 13:43:11.351056	\N	disabled	337
240	b	f	t	2013-04-13 13:43:11.351056	\N	2013-04-13 13:43:11.351056	\N	disabled	338
240	f	f	f	2013-04-13 13:43:11.351056	\N	2013-04-13 13:43:11.351056	\N	disabled	342
243	f	f	f	2013-04-13 13:43:11.351056	\N	2013-04-13 13:43:11.351056	\N	disabled	345
243	g	f	f	2013-04-13 13:43:11.351056	\N	2013-04-13 13:43:11.351056	\N	disabled	346
243	k	f	t	2013-04-13 13:43:11.351056	\N	2013-04-13 13:43:11.351056	\N	disabled	350
243	l	f	f	2013-04-13 13:43:11.351056	\N	2013-04-13 13:43:11.351056	\N	disabled	351
245	a	f	f	2013-04-13 13:43:11.351056	\N	2013-04-13 13:43:11.351056	\N	disabled	342
245	b	f	f	2013-04-13 13:43:11.351056	\N	2013-04-13 13:43:11.351056	\N	disabled	343
245	c	f	t	2013-04-13 13:43:11.351056	\N	2013-04-13 13:43:11.351056	\N	disabled	344
245	h	f	f	2013-04-13 13:43:11.351056	\N	2013-04-13 13:43:11.351056	\N	disabled	349
245	n	f	t	2013-04-13 13:43:11.351056	\N	2013-04-13 13:43:11.351056	\N	disabled	355
245	p	f	t	2013-04-13 13:43:11.351056	\N	2013-04-13 13:43:11.351056	\N	disabled	357
250	a	f	f	2013-04-13 13:43:11.351056	\N	2013-04-13 13:43:11.351056	\N	disabled	347
250	b	f	f	2013-04-13 13:43:11.351056	\N	2013-04-13 13:43:11.351056	\N	disabled	348
255	a	f	f	2013-04-13 13:43:11.351056	\N	2013-04-13 13:43:11.351056	\N	disabled	352
256	a	f	f	2013-04-13 13:43:11.351056	\N	2013-04-13 13:43:11.351056	\N	disabled	353
257	a	f	f	2013-04-13 13:43:11.351056	\N	2013-04-13 13:43:11.351056	\N	disabled	354
258	a	f	f	2013-04-13 13:43:11.351056	\N	2013-04-13 13:43:11.351056	\N	disabled	355
258	b	f	f	2013-04-13 13:43:11.351056	\N	2013-04-13 13:43:11.351056	\N	disabled	356
260	a	f	t	2013-04-13 13:43:11.351056	\N	2013-04-13 13:43:11.351056	\N	disabled	357
260	b	f	t	2013-04-13 13:43:11.351056	\N	2013-04-13 13:43:11.351056	\N	disabled	358
260	c	f	t	2013-04-13 13:43:11.351056	\N	2013-04-13 13:43:11.351056	\N	disabled	359
243	a	f	f	2013-04-13 13:43:11.351056	\N	2013-04-13 13:43:11.351056	\N	disabled	340
240	p	f	t	2013-04-13 13:43:11.351056	\N	2013-04-13 13:43:11.351056	\N	disabled	352
240	n	f	t	2013-04-13 13:43:11.351056	\N	2013-04-13 13:43:11.351056	\N	disabled	350
240	l	f	f	2013-04-13 13:43:11.351056	\N	2013-04-13 13:43:11.351056	\N	disabled	348
240	k	f	t	2013-04-13 13:43:11.351056	\N	2013-04-13 13:43:11.351056	\N	disabled	347
240	g	f	f	2013-04-13 13:43:11.351056	\N	2013-04-13 13:43:11.351056	\N	disabled	343
260	e	f	f	2013-04-13 13:43:11.351056	\N	2013-04-13 13:43:11.351056	\N	disabled	361
260	f	f	f	2013-04-13 13:43:11.351056	\N	2013-04-13 13:43:11.351056	\N	disabled	362
260	g	f	f	2013-04-13 13:43:11.351056	\N	2013-04-13 13:43:11.351056	\N	disabled	363
300	a	f	t	2013-04-13 13:43:11.351056	\N	2013-04-13 13:43:11.351056	\N	disabled	397
300	b	f	f	2013-04-13 13:43:11.351056	\N	2013-04-13 13:43:11.351056	\N	disabled	398
300	c	f	t	2013-04-13 13:43:11.351056	\N	2013-04-13 13:43:11.351056	\N	disabled	399
300	e	f	f	2013-04-13 13:43:11.351056	\N	2013-04-13 13:43:11.351056	\N	disabled	401
306	a	f	f	2013-04-13 13:43:11.351056	\N	2013-04-13 13:43:11.351056	\N	disabled	403
340	a	f	f	2013-04-13 13:43:11.351056	\N	2013-04-13 13:43:11.351056	\N	disabled	437
340	b	f	f	2013-04-13 13:43:11.351056	\N	2013-04-13 13:43:11.351056	\N	disabled	438
340	c	f	f	2013-04-13 13:43:11.351056	\N	2013-04-13 13:43:11.351056	\N	disabled	439
340	d	f	f	2013-04-13 13:43:11.351056	\N	2013-04-13 13:43:11.351056	\N	disabled	440
340	e	f	t	2013-04-13 13:43:11.351056	\N	2013-04-13 13:43:11.351056	\N	disabled	441
342	a	f	f	2013-04-13 13:43:11.351056	\N	2013-04-13 13:43:11.351056	\N	disabled	439
342	b	f	f	2013-04-13 13:43:11.351056	\N	2013-04-13 13:43:11.351056	\N	disabled	440
342	c	f	f	2013-04-13 13:43:11.351056	\N	2013-04-13 13:43:11.351056	\N	disabled	441
342	d	f	f	2013-04-13 13:43:11.351056	\N	2013-04-13 13:43:11.351056	\N	disabled	442
343	a	f	f	2013-04-13 13:43:11.351056	\N	2013-04-13 13:43:11.351056	\N	disabled	440
343	b	f	f	2013-04-13 13:43:11.351056	\N	2013-04-13 13:43:11.351056	\N	disabled	441
490	a	f	f	2013-04-13 13:43:11.351056	\N	2013-04-13 13:43:11.351056	\N	disabled	587
490	v	f	f	2013-04-13 13:43:11.351056	\N	2013-04-13 13:43:11.351056	\N	disabled	608
500	a	f	f	2013-04-13 13:43:11.351056	\N	2013-04-13 13:43:11.351056	\N	disabled	597
501	a	f	f	2013-04-13 13:43:11.351056	\N	2013-04-13 13:43:11.351056	\N	disabled	598
502	a	f	f	2013-04-13 13:43:11.351056	\N	2013-04-13 13:43:11.351056	\N	disabled	599
504	a	f	f	2013-04-13 13:43:11.351056	\N	2013-04-13 13:43:11.351056	\N	disabled	601
505	a	f	f	2013-04-13 13:43:11.351056	\N	2013-04-13 13:43:11.351056	\N	disabled	602
520	a	f	f	2013-04-13 13:43:11.351056	\N	2013-04-13 13:43:11.351056	\N	disabled	617
595	b	f	f	2013-04-13 13:43:11.351056	\N	2013-04-13 13:43:11.351056	\N	disabled	693
600	b	f	f	2013-04-13 13:43:11.351056	\N	2013-04-13 13:43:11.351056	\N	disabled	698
600	c	f	t	2013-04-13 13:43:11.351056	\N	2013-04-13 13:43:11.351056	\N	disabled	699
600	d	f	f	2013-04-13 13:43:11.351056	\N	2013-04-13 13:43:11.351056	\N	disabled	700
600	k	f	t	2013-04-13 13:43:11.351056	\N	2013-04-13 13:43:11.351056	\N	disabled	707
600	q	f	f	2013-04-13 13:43:11.351056	\N	2013-04-13 13:43:11.351056	\N	disabled	713
600	t	f	f	2013-04-13 13:43:11.351056	\N	2013-04-13 13:43:11.351056	\N	disabled	716
600	x	f	t	2013-04-13 13:43:11.351056	\N	2013-04-13 13:43:11.351056	\N	disabled	720
600	y	f	t	2013-04-13 13:43:11.351056	\N	2013-04-13 13:43:11.351056	\N	disabled	721
600	z	f	t	2013-04-13 13:43:11.351056	\N	2013-04-13 13:43:11.351056	\N	disabled	722
610	b	f	t	2013-04-13 13:43:11.351056	\N	2013-04-13 13:43:11.351056	\N	disabled	708
610	c	f	t	2013-04-13 13:43:11.351056	\N	2013-04-13 13:43:11.351056	\N	disabled	709
610	d	f	t	2013-04-13 13:43:11.351056	\N	2013-04-13 13:43:11.351056	\N	disabled	710
610	g	f	f	2013-04-13 13:43:11.351056	\N	2013-04-13 13:43:11.351056	\N	disabled	713
610	k	f	t	2013-04-13 13:43:11.351056	\N	2013-04-13 13:43:11.351056	\N	disabled	717
610	l	f	f	2013-04-13 13:43:11.351056	\N	2013-04-13 13:43:11.351056	\N	disabled	718
610	n	f	t	2013-04-13 13:43:11.351056	\N	2013-04-13 13:43:11.351056	\N	disabled	720
610	t	f	t	2013-04-13 13:43:11.351056	\N	2013-04-13 13:43:11.351056	\N	disabled	726
610	x	f	t	2013-04-13 13:43:11.351056	\N	2013-04-13 13:43:11.351056	\N	disabled	730
610	y	f	t	2013-04-13 13:43:11.351056	\N	2013-04-13 13:43:11.351056	\N	disabled	731
610	z	f	t	2013-04-13 13:43:11.351056	\N	2013-04-13 13:43:11.351056	\N	disabled	732
611	c	f	f	2013-04-13 13:43:11.351056	\N	2013-04-13 13:43:11.351056	\N	disabled	710
611	d	f	t	2013-04-13 13:43:11.351056	\N	2013-04-13 13:43:11.351056	\N	disabled	711
611	e	f	f	2013-04-13 13:43:11.351056	\N	2013-04-13 13:43:11.351056	\N	disabled	712
611	n	f	t	2013-04-13 13:43:11.351056	\N	2013-04-13 13:43:11.351056	\N	disabled	721
611	t	f	f	2013-04-13 13:43:11.351056	\N	2013-04-13 13:43:11.351056	\N	disabled	727
611	x	f	t	2013-04-13 13:43:11.351056	\N	2013-04-13 13:43:11.351056	\N	disabled	731
611	y	f	t	2013-04-13 13:43:11.351056	\N	2013-04-13 13:43:11.351056	\N	disabled	732
611	z	f	t	2013-04-13 13:43:11.351056	\N	2013-04-13 13:43:11.351056	\N	disabled	733
630	d	f	t	2013-04-13 13:43:11.351056	\N	2013-04-13 13:43:11.351056	\N	disabled	730
630	f	f	f	2013-04-13 13:43:11.351056	\N	2013-04-13 13:43:11.351056	\N	disabled	732
630	g	f	f	2013-04-13 13:43:11.351056	\N	2013-04-13 13:43:11.351056	\N	disabled	733
630	k	f	t	2013-04-13 13:43:11.351056	\N	2013-04-13 13:43:11.351056	\N	disabled	737
630	l	f	f	2013-04-13 13:43:11.351056	\N	2013-04-13 13:43:11.351056	\N	disabled	738
630	p	f	t	2013-04-13 13:43:11.351056	\N	2013-04-13 13:43:11.351056	\N	disabled	742
630	x	f	t	2013-04-13 13:43:11.351056	\N	2013-04-13 13:43:11.351056	\N	disabled	750
630	y	f	t	2013-04-13 13:43:11.351056	\N	2013-04-13 13:43:11.351056	\N	disabled	751
630	z	f	t	2013-04-13 13:43:11.351056	\N	2013-04-13 13:43:11.351056	\N	disabled	752
650	x	f	t	2013-04-13 13:43:11.351056	\N	2013-04-13 13:43:11.351056	\N	disabled	770
650	y	f	t	2013-04-13 13:43:11.351056	\N	2013-04-13 13:43:11.351056	\N	disabled	771
650	z	f	t	2013-04-13 13:43:11.351056	\N	2013-04-13 13:43:11.351056	\N	disabled	772
651	x	f	t	2013-04-13 13:43:11.351056	\N	2013-04-13 13:43:11.351056	\N	disabled	771
651	y	f	t	2013-04-13 13:43:11.351056	\N	2013-04-13 13:43:11.351056	\N	disabled	772
651	z	f	t	2013-04-13 13:43:11.351056	\N	2013-04-13 13:43:11.351056	\N	disabled	773
700	a	f	f	2013-04-13 13:43:11.351056	\N	2013-04-13 13:43:11.351056	\N	disabled	797
700	b	f	f	2013-04-13 13:43:11.351056	\N	2013-04-13 13:43:11.351056	\N	disabled	798
700	c	f	t	2013-04-13 13:43:11.351056	\N	2013-04-13 13:43:11.351056	\N	disabled	799
700	d	f	f	2013-04-13 13:43:11.351056	\N	2013-04-13 13:43:11.351056	\N	disabled	800
700	e	f	t	2013-04-13 13:43:11.351056	\N	2013-04-13 13:43:11.351056	\N	disabled	801
700	l	f	f	2013-04-13 13:43:11.351056	\N	2013-04-13 13:43:11.351056	\N	disabled	808
700	q	f	f	2013-04-13 13:43:11.351056	\N	2013-04-13 13:43:11.351056	\N	disabled	813
700	t	f	f	2013-04-13 13:43:11.351056	\N	2013-04-13 13:43:11.351056	\N	disabled	816
710	a	f	f	2013-04-13 13:43:11.351056	\N	2013-04-13 13:43:11.351056	\N	disabled	807
710	b	f	t	2013-04-13 13:43:11.351056	\N	2013-04-13 13:43:11.351056	\N	disabled	808
710	c	f	f	2013-04-13 13:43:11.351056	\N	2013-04-13 13:43:11.351056	\N	disabled	809
710	d	f	t	2013-04-13 13:43:11.351056	\N	2013-04-13 13:43:11.351056	\N	disabled	810
710	g	f	f	2013-04-13 13:43:11.351056	\N	2013-04-13 13:43:11.351056	\N	disabled	813
710	l	f	f	2013-04-13 13:43:11.351056	\N	2013-04-13 13:43:11.351056	\N	disabled	818
710	n	f	t	2013-04-13 13:43:11.351056	\N	2013-04-13 13:43:11.351056	\N	disabled	820
710	t	f	f	2013-04-13 13:43:11.351056	\N	2013-04-13 13:43:11.351056	\N	disabled	826
711	a	f	f	2013-04-13 13:43:11.351056	\N	2013-04-13 13:43:11.351056	\N	disabled	808
711	c	f	f	2013-04-13 13:43:11.351056	\N	2013-04-13 13:43:11.351056	\N	disabled	810
711	d	f	f	2013-04-13 13:43:11.351056	\N	2013-04-13 13:43:11.351056	\N	disabled	811
711	e	f	t	2013-04-13 13:43:11.351056	\N	2013-04-13 13:43:11.351056	\N	disabled	812
711	g	f	f	2013-04-13 13:43:11.351056	\N	2013-04-13 13:43:11.351056	\N	disabled	814
711	k	f	t	2013-04-13 13:43:11.351056	\N	2013-04-13 13:43:11.351056	\N	disabled	818
711	n	f	t	2013-04-13 13:43:11.351056	\N	2013-04-13 13:43:11.351056	\N	disabled	821
711	t	f	f	2013-04-13 13:43:11.351056	\N	2013-04-13 13:43:11.351056	\N	disabled	827
730	a	f	f	2013-04-13 13:43:11.351056	\N	2013-04-13 13:43:11.351056	\N	disabled	827
730	d	f	t	2013-04-13 13:43:11.351056	\N	2013-04-13 13:43:11.351056	\N	disabled	830
730	f	f	f	2013-04-13 13:43:11.351056	\N	2013-04-13 13:43:11.351056	\N	disabled	832
730	g	f	f	2013-04-13 13:43:11.351056	\N	2013-04-13 13:43:11.351056	\N	disabled	833
730	k	f	t	2013-04-13 13:43:11.351056	\N	2013-04-13 13:43:11.351056	\N	disabled	837
730	l	f	f	2013-04-13 13:43:11.351056	\N	2013-04-13 13:43:11.351056	\N	disabled	838
730	p	f	t	2013-04-13 13:43:11.351056	\N	2013-04-13 13:43:11.351056	\N	disabled	842
730	x	f	t	2013-04-13 13:43:11.351056	\N	2013-04-13 13:43:11.351056	\N	disabled	850
730	y	f	t	2013-04-13 13:43:11.351056	\N	2013-04-13 13:43:11.351056	\N	disabled	851
730	z	f	t	2013-04-13 13:43:11.351056	\N	2013-04-13 13:43:11.351056	\N	disabled	852
740	a	f	f	2013-04-13 13:43:11.351056	\N	2013-04-13 13:43:11.351056	\N	disabled	837
740	n	f	t	2013-04-13 13:43:11.351056	\N	2013-04-13 13:43:11.351056	\N	disabled	850
740	p	f	t	2013-04-13 13:43:11.351056	\N	2013-04-13 13:43:11.351056	\N	disabled	852
830	a	f	f	2013-04-13 13:43:11.351056	\N	2013-04-13 13:43:11.351056	\N	disabled	927
830	v	f	f	2013-04-13 13:43:11.351056	\N	2013-04-13 13:43:11.351056	\N	disabled	948
856	d	f	f	2013-04-13 13:43:11.351056	\N	2013-04-13 13:43:11.351056	\N	disabled	956
856	f	f	f	2013-04-13 13:43:11.351056	\N	2013-04-13 13:43:11.351056	\N	disabled	958
856	u	f	f	2013-04-13 13:43:11.351056	\N	2013-04-13 13:43:11.351056	\N	disabled	973
856	y	f	f	2013-04-13 13:43:11.351056	\N	2013-04-13 13:43:11.351056	\N	disabled	977
210	a	f	f	2014-04-28 19:25:12.931	1	2014-04-28 19:25:12.931	\N	disabled	307
210	b	f	f	2014-04-28 19:25:12.931	1	2014-04-28 19:25:12.931	\N	disabled	308
246	a	f	f	2014-04-28 19:25:12.931	1	2014-04-28 19:25:12.931	\N	disabled	343
246	b	f	f	2014-04-28 19:25:12.931	1	2014-04-28 19:25:12.931	\N	disabled	344
246	f	f	f	2014-04-28 19:25:12.931	1	2014-04-28 19:25:12.931	\N	disabled	348
246	g	f	f	2014-04-28 19:25:12.931	1	2014-04-28 19:25:12.931	\N	disabled	349
246	h	f	f	2014-04-28 19:25:12.931	1	2014-04-28 19:25:12.931	\N	disabled	350
246	i	f	f	2014-04-28 19:25:12.931	1	2014-04-28 19:25:12.931	\N	disabled	351
246	n	f	t	2014-04-28 19:25:12.931	1	2014-04-28 19:25:12.931	\N	disabled	356
246	p	f	t	2014-04-28 19:25:12.931	1	2014-04-28 19:25:12.931	\N	disabled	358
310	a	f	f	2014-04-28 19:25:12.931	1	2014-04-28 19:25:12.931	\N	disabled	407
310	b	f	f	2014-04-28 19:25:12.931	1	2014-04-28 19:25:12.931	\N	disabled	408
321	a	f	f	2014-04-28 19:25:12.931	1	2014-04-28 19:25:12.931	\N	disabled	418
321	b	f	f	2014-04-28 19:25:12.931	1	2014-04-28 19:25:12.931	\N	disabled	419
362	a	f	f	2014-04-28 19:25:12.931	1	2014-04-28 19:25:12.931	\N	disabled	459
362	z	f	f	2014-04-28 19:25:12.931	1	2014-04-28 19:25:12.931	\N	disabled	484
515	a	f	f	2014-04-28 19:25:12.931	1	2014-04-28 19:25:12.931	\N	disabled	612
525	a	f	f	2014-04-28 19:25:12.931	1	2014-04-28 19:25:12.931	\N	disabled	622
550	a	f	f	2014-04-28 19:25:12.931	1	2014-04-28 19:25:12.931	\N	disabled	647
555	a	f	f	2014-04-28 19:25:12.931	1	2014-04-28 19:25:12.931	\N	disabled	652
555	b	f	t	2014-04-28 19:25:12.931	1	2014-04-28 19:25:12.931	\N	disabled	653
555	c	f	f	2014-04-28 19:25:12.931	1	2014-04-28 19:25:12.931	\N	disabled	654
555	d	f	f	2014-04-28 19:25:12.931	1	2014-04-28 19:25:12.931	\N	disabled	655
555	u	f	t	2014-04-28 19:25:12.931	1	2014-04-28 19:25:12.931	\N	disabled	672
555	3	f	f	2014-04-28 19:25:12.931	1	2014-04-28 19:25:12.931	\N	disabled	606
580	a	f	f	2014-04-28 19:25:12.931	1	2014-04-28 19:25:12.931	\N	disabled	677
947	a	f	f	2014-04-28 19:25:12.931	1	2014-04-28 19:25:12.931	\N	disabled	1044
947	b	f	f	2014-04-28 19:25:12.931	1	2014-04-28 19:25:12.931	\N	disabled	1045
947	c	f	f	2014-04-28 19:25:12.931	1	2014-04-28 19:25:12.931	\N	disabled	1046
947	d	f	f	2014-04-28 19:25:12.931	1	2014-04-28 19:25:12.931	\N	disabled	1047
947	e	f	f	2014-04-28 19:25:12.931	1	2014-04-28 19:25:12.931	\N	disabled	1048
947	f	f	f	2014-04-28 19:25:12.931	1	2014-04-28 19:25:12.931	\N	disabled	1049
947	g	f	f	2014-04-28 19:25:12.931	1	2014-04-28 19:25:12.931	\N	disabled	1050
947	i	f	f	2014-04-28 19:25:12.931	1	2014-04-28 19:25:12.931	\N	disabled	1052
947	j	f	f	2014-04-28 19:25:12.931	1	2014-04-28 19:25:12.931	\N	disabled	1053
947	k	f	f	2014-04-28 19:25:12.931	1	2014-04-28 19:25:12.931	\N	disabled	1054
947	l	f	f	2014-04-28 19:25:12.931	1	2014-04-28 19:25:12.931	\N	disabled	1055
947	m	f	f	2014-04-28 19:25:12.931	1	2014-04-28 19:25:12.931	\N	disabled	1056
947	n	f	f	2014-04-28 19:25:12.931	1	2014-04-28 19:25:12.931	\N	disabled	1057
947	o	f	f	2014-04-28 19:25:12.931	1	2014-04-28 19:25:12.931	\N	disabled	1058
947	p	f	f	2014-04-28 19:25:12.931	1	2014-04-28 19:25:12.931	\N	disabled	1059
947	q	f	f	2014-04-28 19:25:12.931	1	2014-04-28 19:25:12.931	\N	disabled	1060
947	r	f	f	2014-04-28 19:25:12.931	1	2014-04-28 19:25:12.931	\N	disabled	1061
947	s	f	f	2014-04-28 19:25:12.931	1	2014-04-28 19:25:12.931	\N	disabled	1062
947	t	f	f	2014-04-28 19:25:12.931	1	2014-04-28 19:25:12.931	\N	disabled	1063
947	u	f	f	2014-04-28 19:25:12.931	1	2014-04-28 19:25:12.931	\N	disabled	1064
947	z	f	f	2014-04-28 19:25:12.931	1	2014-04-28 19:25:12.931	\N	disabled	1069
100	a	f	f	2013-04-13 13:43:11.351056	\N	2013-04-13 13:43:11.351056	\N	authorities	197
110	a	f	f	2013-04-13 13:43:11.351056	\N	2013-04-13 13:43:11.351056	\N	authorities	207
111	a	f	f	2013-04-13 13:43:11.351056	\N	2013-04-13 13:43:11.351056	\N	authorities	208
600	a	f	f	2013-04-13 13:43:11.351056	\N	2013-04-13 13:43:11.351056	\N	vocabulary	697
610	a	f	f	2013-04-13 13:43:11.351056	\N	2013-04-13 13:43:11.351056	\N	vocabulary	707
611	a	f	f	2013-04-13 13:43:11.351056	\N	2013-04-13 13:43:11.351056	\N	vocabulary	708
630	a	f	f	2013-04-13 13:43:11.351056	\N	2013-04-13 13:43:11.351056	\N	vocabulary	727
650	a	f	f	2013-04-13 13:43:11.351056	\N	2013-04-13 13:43:11.351056	\N	vocabulary	747
651	a	f	f	2013-04-13 13:43:11.351056	\N	2013-04-13 13:43:11.351056	\N	vocabulary	748
\.


--
-- Data for Name: biblio_holdings; Type: TABLE DATA; Schema: bib4template; Owner: biblivre
--

COPY bib4template.biblio_holdings (id, record_id, iso2709, database, accession_number, location_d, created, created_by, modified, modified_by, material, availability, label_printed) FROM stdin;
\.


--
-- Data for Name: biblio_idx_autocomplete; Type: TABLE DATA; Schema: bib4template; Owner: biblivre
--

COPY bib4template.biblio_idx_autocomplete (id, datafield, subfield, word, phrase, record_id) FROM stdin;
2	095	a	1.00.00.00-3	1.00.00.00-3 Ciências Exatas e da Terra	\N
3	095	a	ciencias	1.00.00.00-3 Ciências Exatas e da Terra	\N
4	095	a	exatas	1.00.00.00-3 Ciências Exatas e da Terra	\N
5	095	a	da	1.00.00.00-3 Ciências Exatas e da Terra	\N
6	095	a	terra	1.00.00.00-3 Ciências Exatas e da Terra	\N
7	095	a	1.01.00.00-8	1.01.00.00-8 Matemática	\N
8	095	a	matematica	1.01.00.00-8 Matemática	\N
9	095	a	1.01.01.00-4	1.01.01.00-4 Álgebra	\N
10	095	a	algebra	1.01.01.00-4 Álgebra	\N
11	095	a	1.01.01.01-2	1.01.01.01-2 Conjuntos	\N
12	095	a	conjuntos	1.01.01.01-2 Conjuntos	\N
13	095	a	1.01.01.02-0	1.01.01.02-0 Lógica Matemática	\N
14	095	a	logica	1.01.01.02-0 Lógica Matemática	\N
15	095	a	matematica	1.01.01.02-0 Lógica Matemática	\N
16	095	a	1.01.01.03-9	1.01.01.03-9 Teoria dos Números	\N
17	095	a	teoria	1.01.01.03-9 Teoria dos Números	\N
18	095	a	dos	1.01.01.03-9 Teoria dos Números	\N
19	095	a	numeros	1.01.01.03-9 Teoria dos Números	\N
20	095	a	1.01.01.04-7	1.01.01.04-7 Grupos de Algebra Não-Comutaviva	\N
21	095	a	grupos	1.01.01.04-7 Grupos de Algebra Não-Comutaviva	\N
22	095	a	de	1.01.01.04-7 Grupos de Algebra Não-Comutaviva	\N
23	095	a	algebra	1.01.01.04-7 Grupos de Algebra Não-Comutaviva	\N
24	095	a	nao-comutaviva	1.01.01.04-7 Grupos de Algebra Não-Comutaviva	\N
25	095	a	1.01.01.05-5	1.01.01.05-5 Algebra Comutativa	\N
26	095	a	algebra	1.01.01.05-5 Algebra Comutativa	\N
27	095	a	comutativa	1.01.01.05-5 Algebra Comutativa	\N
28	095	a	1.01.01.06-3	1.01.01.06-3 Geometria Algebrica	\N
29	095	a	geometria	1.01.01.06-3 Geometria Algebrica	\N
30	095	a	algebrica	1.01.01.06-3 Geometria Algebrica	\N
31	095	a	1.01.02.00-0	1.01.02.00-0 Análise	\N
32	095	a	analise	1.01.02.00-0 Análise	\N
33	095	a	1.01.02.01-9	1.01.02.01-9 Análise Complexa	\N
34	095	a	analise	1.01.02.01-9 Análise Complexa	\N
35	095	a	complexa	1.01.02.01-9 Análise Complexa	\N
36	095	a	1.01.02.02-7	1.01.02.02-7 Análise Funcional	\N
37	095	a	analise	1.01.02.02-7 Análise Funcional	\N
38	095	a	funcional	1.01.02.02-7 Análise Funcional	\N
39	095	a	1.01.02.03-5	1.01.02.03-5 Análise Funcional Não-Linear	\N
40	095	a	analise	1.01.02.03-5 Análise Funcional Não-Linear	\N
41	095	a	funcional	1.01.02.03-5 Análise Funcional Não-Linear	\N
42	095	a	nao-linear	1.01.02.03-5 Análise Funcional Não-Linear	\N
43	095	a	1.01.02.04-3	1.01.02.04-3 Equações Diferênciais Ordinárias	\N
44	095	a	equacoes	1.01.02.04-3 Equações Diferênciais Ordinárias	\N
45	095	a	diferenciais	1.01.02.04-3 Equações Diferênciais Ordinárias	\N
46	095	a	ordinarias	1.01.02.04-3 Equações Diferênciais Ordinárias	\N
47	095	a	1.01.02.05-1	1.01.02.05-1 Equações Diferênciais Parciais	\N
48	095	a	equacoes	1.01.02.05-1 Equações Diferênciais Parciais	\N
49	095	a	diferenciais	1.01.02.05-1 Equações Diferênciais Parciais	\N
50	095	a	parciais	1.01.02.05-1 Equações Diferênciais Parciais	\N
51	095	a	1.01.02.06-0	1.01.02.06-0 Equações Diferênciais Funcionais	\N
52	095	a	equacoes	1.01.02.06-0 Equações Diferênciais Funcionais	\N
53	095	a	diferenciais	1.01.02.06-0 Equações Diferênciais Funcionais	\N
54	095	a	funcionais	1.01.02.06-0 Equações Diferênciais Funcionais	\N
55	095	a	1.01.03.00-7	1.01.03.00-7 Geometria e Topologia	\N
56	095	a	geometria	1.01.03.00-7 Geometria e Topologia	\N
57	095	a	topologia	1.01.03.00-7 Geometria e Topologia	\N
58	095	a	1.01.03.01-5	1.01.03.01-5 Geometria Diferêncial	\N
59	095	a	geometria	1.01.03.01-5 Geometria Diferêncial	\N
60	095	a	diferencial	1.01.03.01-5 Geometria Diferêncial	\N
61	095	a	1.01.03.02-3	1.01.03.02-3 Topologia Algébrica	\N
62	095	a	topologia	1.01.03.02-3 Topologia Algébrica	\N
63	095	a	algebrica	1.01.03.02-3 Topologia Algébrica	\N
64	095	a	1.01.03.03-1	1.01.03.03-1 Topologia das Variedades	\N
65	095	a	topologia	1.01.03.03-1 Topologia das Variedades	\N
66	095	a	das	1.01.03.03-1 Topologia das Variedades	\N
67	095	a	variedades	1.01.03.03-1 Topologia das Variedades	\N
68	095	a	1.01.03.04-0	1.01.03.04-0 Sistemas Dinâmicos	\N
69	095	a	sistemas	1.01.03.04-0 Sistemas Dinâmicos	\N
70	095	a	dinamicos	1.01.03.04-0 Sistemas Dinâmicos	\N
71	095	a	1.01.03.05-8	1.01.03.05-8 Teoria das Singularidades e Teoria das Catástrofes	\N
72	095	a	teoria	1.01.03.05-8 Teoria das Singularidades e Teoria das Catástrofes	\N
73	095	a	das	1.01.03.05-8 Teoria das Singularidades e Teoria das Catástrofes	\N
74	095	a	singularidades	1.01.03.05-8 Teoria das Singularidades e Teoria das Catástrofes	\N
75	095	a	teoria	1.01.03.05-8 Teoria das Singularidades e Teoria das Catástrofes	\N
76	095	a	das	1.01.03.05-8 Teoria das Singularidades e Teoria das Catástrofes	\N
77	095	a	catastrofes	1.01.03.05-8 Teoria das Singularidades e Teoria das Catástrofes	\N
78	095	a	1.01.03.06-6	1.01.03.06-6 Teoria das Folheações	\N
79	095	a	teoria	1.01.03.06-6 Teoria das Folheações	\N
80	095	a	das	1.01.03.06-6 Teoria das Folheações	\N
81	095	a	folheacoes	1.01.03.06-6 Teoria das Folheações	\N
82	095	a	1.01.04.00-3	1.01.04.00-3 Matemática Aplicada	\N
83	095	a	matematica	1.01.04.00-3 Matemática Aplicada	\N
84	095	a	aplicada	1.01.04.00-3 Matemática Aplicada	\N
85	095	a	1.01.04.01-1	1.01.04.01-1 Física Matemática	\N
86	095	a	fisica	1.01.04.01-1 Física Matemática	\N
87	095	a	matematica	1.01.04.01-1 Física Matemática	\N
88	095	a	1.01.04.02-0	1.01.04.02-0 Análise Numérica	\N
89	095	a	analise	1.01.04.02-0 Análise Numérica	\N
90	095	a	numerica	1.01.04.02-0 Análise Numérica	\N
91	095	a	1.01.04.03-8	1.01.04.03-8 Matemática Discreta e Combinatoria	\N
92	095	a	matematica	1.01.04.03-8 Matemática Discreta e Combinatoria	\N
93	095	a	discreta	1.01.04.03-8 Matemática Discreta e Combinatoria	\N
94	095	a	combinatoria	1.01.04.03-8 Matemática Discreta e Combinatoria	\N
95	095	a	1.02.00.00-2	1.02.00.00-2 Probabilidade e Estatística	\N
96	095	a	probabilidade	1.02.00.00-2 Probabilidade e Estatística	\N
97	095	a	estatistica	1.02.00.00-2 Probabilidade e Estatística	\N
98	095	a	1.02.01.00-9	1.02.01.00-9 Probabilidade	\N
99	095	a	probabilidade	1.02.01.00-9 Probabilidade	\N
100	095	a	1.02.01.01-7	1.02.01.01-7 Teoria Geral e Fundamentos da Probabilidade	\N
101	095	a	teoria	1.02.01.01-7 Teoria Geral e Fundamentos da Probabilidade	\N
102	095	a	geral	1.02.01.01-7 Teoria Geral e Fundamentos da Probabilidade	\N
103	095	a	fundamentos	1.02.01.01-7 Teoria Geral e Fundamentos da Probabilidade	\N
104	095	a	da	1.02.01.01-7 Teoria Geral e Fundamentos da Probabilidade	\N
105	095	a	probabilidade	1.02.01.01-7 Teoria Geral e Fundamentos da Probabilidade	\N
106	095	a	1.02.01.02-5	1.02.01.02-5 Teoria Geral e Processos Estocásticos	\N
107	095	a	teoria	1.02.01.02-5 Teoria Geral e Processos Estocásticos	\N
108	095	a	geral	1.02.01.02-5 Teoria Geral e Processos Estocásticos	\N
109	095	a	processos	1.02.01.02-5 Teoria Geral e Processos Estocásticos	\N
110	095	a	estocasticos	1.02.01.02-5 Teoria Geral e Processos Estocásticos	\N
111	095	a	1.02.01.03-3	1.02.01.03-3 Teoremas de Limite	\N
112	095	a	teoremas	1.02.01.03-3 Teoremas de Limite	\N
113	095	a	de	1.02.01.03-3 Teoremas de Limite	\N
114	095	a	limite	1.02.01.03-3 Teoremas de Limite	\N
115	095	a	1.02.01.04-1	1.02.01.04-1 Processos Markovianos	\N
116	095	a	processos	1.02.01.04-1 Processos Markovianos	\N
117	095	a	markovianos	1.02.01.04-1 Processos Markovianos	\N
118	095	a	1.02.01.05-0	1.02.01.05-0 Análise Estocástica	\N
119	095	a	analise	1.02.01.05-0 Análise Estocástica	\N
120	095	a	estocastica	1.02.01.05-0 Análise Estocástica	\N
121	095	a	1.02.01.06-8	1.02.01.06-8 Processos Estocásticos Especiais	\N
122	095	a	processos	1.02.01.06-8 Processos Estocásticos Especiais	\N
123	095	a	estocasticos	1.02.01.06-8 Processos Estocásticos Especiais	\N
124	095	a	especiais	1.02.01.06-8 Processos Estocásticos Especiais	\N
125	095	a	1.02.02.00-5	1.02.02.00-5 Estatística	\N
126	095	a	estatistica	1.02.02.00-5 Estatística	\N
127	095	a	1.02.02.01-3	1.02.02.01-3 Fundamentos da Estatística	\N
128	095	a	fundamentos	1.02.02.01-3 Fundamentos da Estatística	\N
129	095	a	da	1.02.02.01-3 Fundamentos da Estatística	\N
130	095	a	estatistica	1.02.02.01-3 Fundamentos da Estatística	\N
131	095	a	1.02.02.02-1	1.02.02.02-1 Inferência Paramétrica	\N
132	095	a	inferencia	1.02.02.02-1 Inferência Paramétrica	\N
133	095	a	parametrica	1.02.02.02-1 Inferência Paramétrica	\N
134	095	a	1.02.02.03-0	1.02.02.03-0 Inferência Nao-Paramétrica	\N
135	095	a	inferencia	1.02.02.03-0 Inferência Nao-Paramétrica	\N
136	095	a	nao-parametrica	1.02.02.03-0 Inferência Nao-Paramétrica	\N
137	095	a	1.02.02.04-8	1.02.02.04-8 Inferência em Processos Estocásticos	\N
138	095	a	inferencia	1.02.02.04-8 Inferência em Processos Estocásticos	\N
139	095	a	em	1.02.02.04-8 Inferência em Processos Estocásticos	\N
140	095	a	processos	1.02.02.04-8 Inferência em Processos Estocásticos	\N
141	095	a	estocasticos	1.02.02.04-8 Inferência em Processos Estocásticos	\N
142	095	a	1.02.02.05-6	1.02.02.05-6 Análise Multivariada	\N
143	095	a	analise	1.02.02.05-6 Análise Multivariada	\N
144	095	a	multivariada	1.02.02.05-6 Análise Multivariada	\N
145	095	a	1.02.02.06-4	1.02.02.06-4 Regressão e Correlação	\N
146	095	a	regressao	1.02.02.06-4 Regressão e Correlação	\N
147	095	a	correlacao	1.02.02.06-4 Regressão e Correlação	\N
148	095	a	1.02.02.07-2	1.02.02.07-2 Planejamento de Experimentos	\N
149	095	a	planejamento	1.02.02.07-2 Planejamento de Experimentos	\N
150	095	a	de	1.02.02.07-2 Planejamento de Experimentos	\N
151	095	a	experimentos	1.02.02.07-2 Planejamento de Experimentos	\N
152	095	a	1.02.02.08-0	1.02.02.08-0 Análise de Dados	\N
153	095	a	analise	1.02.02.08-0 Análise de Dados	\N
154	095	a	de	1.02.02.08-0 Análise de Dados	\N
155	095	a	dados	1.02.02.08-0 Análise de Dados	\N
156	095	a	1.02.03.00-1	1.02.03.00-1 Probabilidade e Estatística Aplicadas	\N
157	095	a	probabilidade	1.02.03.00-1 Probabilidade e Estatística Aplicadas	\N
158	095	a	estatistica	1.02.03.00-1 Probabilidade e Estatística Aplicadas	\N
159	095	a	aplicadas	1.02.03.00-1 Probabilidade e Estatística Aplicadas	\N
160	095	a	1.03.00.00-7	1.03.00.00-7 Ciência da Computação	\N
161	095	a	ciencia	1.03.00.00-7 Ciência da Computação	\N
162	095	a	da	1.03.00.00-7 Ciência da Computação	\N
163	095	a	computacao	1.03.00.00-7 Ciência da Computação	\N
164	095	a	1.03.01.00-3	1.03.01.00-3 Teoria da Computação	\N
165	095	a	teoria	1.03.01.00-3 Teoria da Computação	\N
166	095	a	da	1.03.01.00-3 Teoria da Computação	\N
167	095	a	computacao	1.03.01.00-3 Teoria da Computação	\N
168	095	a	1.03.01.01-1	1.03.01.01-1 Computabilidade e Modelos de Computação	\N
169	095	a	computabilidade	1.03.01.01-1 Computabilidade e Modelos de Computação	\N
170	095	a	modelos	1.03.01.01-1 Computabilidade e Modelos de Computação	\N
171	095	a	de	1.03.01.01-1 Computabilidade e Modelos de Computação	\N
172	095	a	computacao	1.03.01.01-1 Computabilidade e Modelos de Computação	\N
173	095	a	1.03.01.02-0	1.03.01.02-0 Linguagem Formais e Automatos	\N
174	095	a	linguagem	1.03.01.02-0 Linguagem Formais e Automatos	\N
175	095	a	formais	1.03.01.02-0 Linguagem Formais e Automatos	\N
176	095	a	automatos	1.03.01.02-0 Linguagem Formais e Automatos	\N
177	095	a	1.03.01.03-8	1.03.01.03-8 Análise de Algoritmos e Complexidade de Computação	\N
178	095	a	analise	1.03.01.03-8 Análise de Algoritmos e Complexidade de Computação	\N
179	095	a	de	1.03.01.03-8 Análise de Algoritmos e Complexidade de Computação	\N
180	095	a	algoritmos	1.03.01.03-8 Análise de Algoritmos e Complexidade de Computação	\N
181	095	a	complexidade	1.03.01.03-8 Análise de Algoritmos e Complexidade de Computação	\N
182	095	a	de	1.03.01.03-8 Análise de Algoritmos e Complexidade de Computação	\N
183	095	a	computacao	1.03.01.03-8 Análise de Algoritmos e Complexidade de Computação	\N
184	095	a	1.03.01.04-6	1.03.01.04-6 Lógicas e Semântica de Programas	\N
185	095	a	logicas	1.03.01.04-6 Lógicas e Semântica de Programas	\N
186	095	a	semantica	1.03.01.04-6 Lógicas e Semântica de Programas	\N
187	095	a	de	1.03.01.04-6 Lógicas e Semântica de Programas	\N
188	095	a	programas	1.03.01.04-6 Lógicas e Semântica de Programas	\N
189	095	a	1.03.02.00-0	1.03.02.00-0 Matemática da Computação	\N
190	095	a	matematica	1.03.02.00-0 Matemática da Computação	\N
191	095	a	da	1.03.02.00-0 Matemática da Computação	\N
192	095	a	computacao	1.03.02.00-0 Matemática da Computação	\N
193	095	a	1.03.02.01-8	1.03.02.01-8 Matemática Simbólica	\N
194	095	a	matematica	1.03.02.01-8 Matemática Simbólica	\N
195	095	a	simbolica	1.03.02.01-8 Matemática Simbólica	\N
196	095	a	1.03.02.02-6	1.03.02.02-6 Modelos Analíticos e de Simulação	\N
197	095	a	modelos	1.03.02.02-6 Modelos Analíticos e de Simulação	\N
198	095	a	analiticos	1.03.02.02-6 Modelos Analíticos e de Simulação	\N
199	095	a	de	1.03.02.02-6 Modelos Analíticos e de Simulação	\N
200	095	a	simulacao	1.03.02.02-6 Modelos Analíticos e de Simulação	\N
201	095	a	1.03.03.00-6	1.03.03.00-6 Metodologia e Técnicas da Computação	\N
202	095	a	metodologia	1.03.03.00-6 Metodologia e Técnicas da Computação	\N
203	095	a	tecnicas	1.03.03.00-6 Metodologia e Técnicas da Computação	\N
204	095	a	da	1.03.03.00-6 Metodologia e Técnicas da Computação	\N
205	095	a	computacao	1.03.03.00-6 Metodologia e Técnicas da Computação	\N
206	095	a	1.03.03.01-4	1.03.03.01-4 Linguagens de Programação	\N
207	095	a	linguagens	1.03.03.01-4 Linguagens de Programação	\N
208	095	a	de	1.03.03.01-4 Linguagens de Programação	\N
209	095	a	programacao	1.03.03.01-4 Linguagens de Programação	\N
210	095	a	1.03.03.02-2	1.03.03.02-2 Engenharia de Software	\N
211	095	a	engenharia	1.03.03.02-2 Engenharia de Software	\N
212	095	a	de	1.03.03.02-2 Engenharia de Software	\N
213	095	a	software	1.03.03.02-2 Engenharia de Software	\N
214	095	a	1.03.03.03-0	1.03.03.03-0 Banco de Dados	\N
215	095	a	banco	1.03.03.03-0 Banco de Dados	\N
216	095	a	de	1.03.03.03-0 Banco de Dados	\N
217	095	a	dados	1.03.03.03-0 Banco de Dados	\N
218	095	a	1.03.03.04-9	1.03.03.04-9 Sistemas de Informação	\N
219	095	a	sistemas	1.03.03.04-9 Sistemas de Informação	\N
220	095	a	de	1.03.03.04-9 Sistemas de Informação	\N
221	095	a	informacao	1.03.03.04-9 Sistemas de Informação	\N
222	095	a	1.03.03.05-7	1.03.03.05-7 Processamento Gráfico (Graphics)	\N
223	095	a	processamento	1.03.03.05-7 Processamento Gráfico (Graphics)	\N
224	095	a	grafico	1.03.03.05-7 Processamento Gráfico (Graphics)	\N
225	095	a	(graphics)	1.03.03.05-7 Processamento Gráfico (Graphics)	\N
226	095	a	1.03.04.00-2	1.03.04.00-2 Sistemas de Computação	\N
227	095	a	sistemas	1.03.04.00-2 Sistemas de Computação	\N
228	095	a	de	1.03.04.00-2 Sistemas de Computação	\N
229	095	a	computacao	1.03.04.00-2 Sistemas de Computação	\N
230	095	a	1.03.04.01-0	1.03.04.01-0 Hardware	\N
231	095	a	hardware	1.03.04.01-0 Hardware	\N
232	095	a	1.03.04.02-9	1.03.04.02-9 Arquitetura de Sistemas de Computação	\N
233	095	a	arquitetura	1.03.04.02-9 Arquitetura de Sistemas de Computação	\N
234	095	a	de	1.03.04.02-9 Arquitetura de Sistemas de Computação	\N
235	095	a	sistemas	1.03.04.02-9 Arquitetura de Sistemas de Computação	\N
236	095	a	de	1.03.04.02-9 Arquitetura de Sistemas de Computação	\N
237	095	a	computacao	1.03.04.02-9 Arquitetura de Sistemas de Computação	\N
238	095	a	1.03.04.03-7	1.03.04.03-7 Software Básico	\N
239	095	a	software	1.03.04.03-7 Software Básico	\N
240	095	a	basico	1.03.04.03-7 Software Básico	\N
241	095	a	1.03.04.04-5	1.03.04.04-5 Teleinformática	\N
242	095	a	teleinformatica	1.03.04.04-5 Teleinformática	\N
243	095	a	1.04.00.00-1	1.04.00.00-1 Astronomia	\N
244	095	a	astronomia	1.04.00.00-1 Astronomia	\N
245	095	a	1.04.01.00-8	1.04.01.00-8 Astronomia de Posição e Mecânica Celeste	\N
246	095	a	astronomia	1.04.01.00-8 Astronomia de Posição e Mecânica Celeste	\N
247	095	a	de	1.04.01.00-8 Astronomia de Posição e Mecânica Celeste	\N
248	095	a	posicao	1.04.01.00-8 Astronomia de Posição e Mecânica Celeste	\N
249	095	a	mecanica	1.04.01.00-8 Astronomia de Posição e Mecânica Celeste	\N
250	095	a	celeste	1.04.01.00-8 Astronomia de Posição e Mecânica Celeste	\N
251	095	a	1.04.01.01-6	1.04.01.01-6 Astronomia Fundamental	\N
252	095	a	astronomia	1.04.01.01-6 Astronomia Fundamental	\N
253	095	a	fundamental	1.04.01.01-6 Astronomia Fundamental	\N
254	095	a	1.04.01.02-4	1.04.01.02-4 Astronomia Dinâmica	\N
255	095	a	astronomia	1.04.01.02-4 Astronomia Dinâmica	\N
256	095	a	dinamica	1.04.01.02-4 Astronomia Dinâmica	\N
257	095	a	1.04.02.00-4	1.04.02.00-4 Astrofísica Estelar	\N
258	095	a	astrofisica	1.04.02.00-4 Astrofísica Estelar	\N
259	095	a	estelar	1.04.02.00-4 Astrofísica Estelar	\N
260	095	a	1.04.03.00-0	1.04.03.00-0 Astrofísica do Meio Interestelar	\N
261	095	a	astrofisica	1.04.03.00-0 Astrofísica do Meio Interestelar	\N
262	095	a	do	1.04.03.00-0 Astrofísica do Meio Interestelar	\N
263	095	a	meio	1.04.03.00-0 Astrofísica do Meio Interestelar	\N
264	095	a	interestelar	1.04.03.00-0 Astrofísica do Meio Interestelar	\N
265	095	a	1.04.03.01-9	1.04.03.01-9 Meio Interestelar	\N
266	095	a	meio	1.04.03.01-9 Meio Interestelar	\N
267	095	a	interestelar	1.04.03.01-9 Meio Interestelar	\N
268	095	a	1.04.03.02-7	1.04.03.02-7 Nebulosa	\N
269	095	a	nebulosa	1.04.03.02-7 Nebulosa	\N
270	095	a	1.04.04.00-7	1.04.04.00-7 Astrofísica Extragaláctica	\N
271	095	a	astrofisica	1.04.04.00-7 Astrofísica Extragaláctica	\N
272	095	a	extragalactica	1.04.04.00-7 Astrofísica Extragaláctica	\N
273	095	a	1.04.04.01-5	1.04.04.01-5 Galáxias	\N
274	095	a	galaxias	1.04.04.01-5 Galáxias	\N
275	095	a	1.04.04.02-3	1.04.04.02-3 Aglomerados de Galáxias	\N
276	095	a	aglomerados	1.04.04.02-3 Aglomerados de Galáxias	\N
277	095	a	de	1.04.04.02-3 Aglomerados de Galáxias	\N
278	095	a	galaxias	1.04.04.02-3 Aglomerados de Galáxias	\N
279	095	a	1.04.04.03-1	1.04.04.03-1 Quasares	\N
280	095	a	quasares	1.04.04.03-1 Quasares	\N
281	095	a	1.04.04.04-0	1.04.04.04-0 Cosmologia	\N
282	095	a	cosmologia	1.04.04.04-0 Cosmologia	\N
283	095	a	1.04.05.00-3	1.04.05.00-3 Astrofísica do Sistema Solar	\N
284	095	a	astrofisica	1.04.05.00-3 Astrofísica do Sistema Solar	\N
285	095	a	do	1.04.05.00-3 Astrofísica do Sistema Solar	\N
286	095	a	sistema	1.04.05.00-3 Astrofísica do Sistema Solar	\N
287	095	a	solar	1.04.05.00-3 Astrofísica do Sistema Solar	\N
288	095	a	1.04.05.01-1	1.04.05.01-1 Física Solar	\N
289	095	a	fisica	1.04.05.01-1 Física Solar	\N
290	095	a	solar	1.04.05.01-1 Física Solar	\N
291	095	a	1.04.05.02-0	1.04.05.02-0 Movimento da Terra	\N
292	095	a	movimento	1.04.05.02-0 Movimento da Terra	\N
293	095	a	da	1.04.05.02-0 Movimento da Terra	\N
294	095	a	terra	1.04.05.02-0 Movimento da Terra	\N
295	095	a	1.04.05.03-8	1.04.05.03-8 Sistema Planetário	\N
296	095	a	sistema	1.04.05.03-8 Sistema Planetário	\N
297	095	a	planetario	1.04.05.03-8 Sistema Planetário	\N
298	095	a	1.04.06.00-0	1.04.06.00-0 Instrumentação Astronômica	\N
299	095	a	instrumentacao	1.04.06.00-0 Instrumentação Astronômica	\N
300	095	a	astronomica	1.04.06.00-0 Instrumentação Astronômica	\N
301	095	a	1.04.06.01-8	1.04.06.01-8 Astronômia Ótica	\N
302	095	a	astronomia	1.04.06.01-8 Astronômia Ótica	\N
303	095	a	otica	1.04.06.01-8 Astronômia Ótica	\N
304	095	a	1.04.06.02-6	1.04.06.02-6 Radioastronomia	\N
305	095	a	radioastronomia	1.04.06.02-6 Radioastronomia	\N
306	095	a	1.04.06.03-4	1.04.06.03-4 Astronomia Espacial	\N
307	095	a	astronomia	1.04.06.03-4 Astronomia Espacial	\N
308	095	a	espacial	1.04.06.03-4 Astronomia Espacial	\N
309	095	a	1.04.06.04-2	1.04.06.04-2 Processamento de Dados Astronômicos	\N
310	095	a	processamento	1.04.06.04-2 Processamento de Dados Astronômicos	\N
311	095	a	de	1.04.06.04-2 Processamento de Dados Astronômicos	\N
312	095	a	dados	1.04.06.04-2 Processamento de Dados Astronômicos	\N
313	095	a	astronomicos	1.04.06.04-2 Processamento de Dados Astronômicos	\N
314	095	a	1.05.00.00-6	1.05.00.00-6 Física	\N
315	095	a	fisica	1.05.00.00-6 Física	\N
316	095	a	1.05.01.00-2	1.05.01.00-2 Física Geral	\N
317	095	a	fisica	1.05.01.00-2 Física Geral	\N
318	095	a	geral	1.05.01.00-2 Física Geral	\N
319	095	a	1.05.01.01-0	1.05.01.01-0 Métodos Matemáticos da Física	\N
320	095	a	metodos	1.05.01.01-0 Métodos Matemáticos da Física	\N
321	095	a	matematicos	1.05.01.01-0 Métodos Matemáticos da Física	\N
322	095	a	da	1.05.01.01-0 Métodos Matemáticos da Física	\N
323	095	a	fisica	1.05.01.01-0 Métodos Matemáticos da Física	\N
324	095	a	1.05.01.02-9	1.05.01.02-9 Física Clássica e Física Quântica; Mecânica e Campos	\N
325	095	a	fisica	1.05.01.02-9 Física Clássica e Física Quântica; Mecânica e Campos	\N
326	095	a	classica	1.05.01.02-9 Física Clássica e Física Quântica; Mecânica e Campos	\N
327	095	a	fisica	1.05.01.02-9 Física Clássica e Física Quântica; Mecânica e Campos	\N
328	095	a	quantica;	1.05.01.02-9 Física Clássica e Física Quântica; Mecânica e Campos	\N
329	095	a	mecanica	1.05.01.02-9 Física Clássica e Física Quântica; Mecânica e Campos	\N
330	095	a	campos	1.05.01.02-9 Física Clássica e Física Quântica; Mecânica e Campos	\N
331	095	a	1.05.01.03-7	1.05.01.03-7 Relatividade e Gravitação	\N
332	095	a	relatividade	1.05.01.03-7 Relatividade e Gravitação	\N
333	095	a	gravitacao	1.05.01.03-7 Relatividade e Gravitação	\N
334	095	a	1.05.01.04-5	1.05.01.04-5 Física Estatística e Termodinâmica	\N
335	095	a	fisica	1.05.01.04-5 Física Estatística e Termodinâmica	\N
336	095	a	estatistica	1.05.01.04-5 Física Estatística e Termodinâmica	\N
337	095	a	termodinamica	1.05.01.04-5 Física Estatística e Termodinâmica	\N
338	095	a	1.05.01.05-3	1.05.01.05-3 Metrologia, Técnicas Gerais de Laboratório, Sistema de Instrumentação	\N
339	095	a	metrologia,	1.05.01.05-3 Metrologia, Técnicas Gerais de Laboratório, Sistema de Instrumentação	\N
340	095	a	tecnicas	1.05.01.05-3 Metrologia, Técnicas Gerais de Laboratório, Sistema de Instrumentação	\N
341	095	a	gerais	1.05.01.05-3 Metrologia, Técnicas Gerais de Laboratório, Sistema de Instrumentação	\N
342	095	a	de	1.05.01.05-3 Metrologia, Técnicas Gerais de Laboratório, Sistema de Instrumentação	\N
343	095	a	laboratorio,	1.05.01.05-3 Metrologia, Técnicas Gerais de Laboratório, Sistema de Instrumentação	\N
344	095	a	sistema	1.05.01.05-3 Metrologia, Técnicas Gerais de Laboratório, Sistema de Instrumentação	\N
345	095	a	de	1.05.01.05-3 Metrologia, Técnicas Gerais de Laboratório, Sistema de Instrumentação	\N
346	095	a	instrumentacao	1.05.01.05-3 Metrologia, Técnicas Gerais de Laboratório, Sistema de Instrumentação	\N
347	095	a	1.05.01.06-1	1.05.01.06-1 Instrumentação Específica de Uso Geral em Física	\N
348	095	a	instrumentacao	1.05.01.06-1 Instrumentação Específica de Uso Geral em Física	\N
349	095	a	especifica	1.05.01.06-1 Instrumentação Específica de Uso Geral em Física	\N
350	095	a	de	1.05.01.06-1 Instrumentação Específica de Uso Geral em Física	\N
351	095	a	uso	1.05.01.06-1 Instrumentação Específica de Uso Geral em Física	\N
352	095	a	geral	1.05.01.06-1 Instrumentação Específica de Uso Geral em Física	\N
353	095	a	em	1.05.01.06-1 Instrumentação Específica de Uso Geral em Física	\N
354	095	a	fisica	1.05.01.06-1 Instrumentação Específica de Uso Geral em Física	\N
355	095	a	1.05.02.00-9	1.05.02.00-9 Áreas Clássicas de Fenomenologia e suas Aplicações	\N
356	095	a	areas	1.05.02.00-9 Áreas Clássicas de Fenomenologia e suas Aplicações	\N
357	095	a	classicas	1.05.02.00-9 Áreas Clássicas de Fenomenologia e suas Aplicações	\N
358	095	a	de	1.05.02.00-9 Áreas Clássicas de Fenomenologia e suas Aplicações	\N
359	095	a	fenomenologia	1.05.02.00-9 Áreas Clássicas de Fenomenologia e suas Aplicações	\N
360	095	a	suas	1.05.02.00-9 Áreas Clássicas de Fenomenologia e suas Aplicações	\N
361	095	a	aplicacoes	1.05.02.00-9 Áreas Clássicas de Fenomenologia e suas Aplicações	\N
362	095	a	1.05.02.01-7	1.05.02.01-7 Eletricidade e Magnetismo; Campos e Partículas Carregadas	\N
363	095	a	eletricidade	1.05.02.01-7 Eletricidade e Magnetismo; Campos e Partículas Carregadas	\N
364	095	a	magnetismo;	1.05.02.01-7 Eletricidade e Magnetismo; Campos e Partículas Carregadas	\N
365	095	a	campos	1.05.02.01-7 Eletricidade e Magnetismo; Campos e Partículas Carregadas	\N
366	095	a	particulas	1.05.02.01-7 Eletricidade e Magnetismo; Campos e Partículas Carregadas	\N
367	095	a	carregadas	1.05.02.01-7 Eletricidade e Magnetismo; Campos e Partículas Carregadas	\N
368	095	a	1.05.02.02-5	1.05.02.02-5 Ótica	\N
369	095	a	otica	1.05.02.02-5 Ótica	\N
370	095	a	1.05.02.03-3	1.05.02.03-3 Acústica	\N
371	095	a	acustica	1.05.02.03-3 Acústica	\N
372	095	a	1.05.02.04-1	1.05.02.04-1 Transferência de Calor; Processos Térmicos e Termodinâmicos	\N
373	095	a	transferencia	1.05.02.04-1 Transferência de Calor; Processos Térmicos e Termodinâmicos	\N
374	095	a	de	1.05.02.04-1 Transferência de Calor; Processos Térmicos e Termodinâmicos	\N
375	095	a	calor;	1.05.02.04-1 Transferência de Calor; Processos Térmicos e Termodinâmicos	\N
376	095	a	processos	1.05.02.04-1 Transferência de Calor; Processos Térmicos e Termodinâmicos	\N
377	095	a	termicos	1.05.02.04-1 Transferência de Calor; Processos Térmicos e Termodinâmicos	\N
378	095	a	termodinamicos	1.05.02.04-1 Transferência de Calor; Processos Térmicos e Termodinâmicos	\N
379	095	a	1.05.02.05-0	1.05.02.05-0 Mecânica, Elasticidade e Reologia	\N
380	095	a	mecanica,	1.05.02.05-0 Mecânica, Elasticidade e Reologia	\N
381	095	a	elasticidade	1.05.02.05-0 Mecânica, Elasticidade e Reologia	\N
382	095	a	reologia	1.05.02.05-0 Mecânica, Elasticidade e Reologia	\N
383	095	a	1.05.02.06-8	1.05.02.06-8 Dinâmica dos Fluidos	\N
384	095	a	dinamica	1.05.02.06-8 Dinâmica dos Fluidos	\N
385	095	a	dos	1.05.02.06-8 Dinâmica dos Fluidos	\N
386	095	a	fluidos	1.05.02.06-8 Dinâmica dos Fluidos	\N
387	095	a	1.05.03.00-5	1.05.03.00-5 Física das Partículas Elementares e Campos	\N
388	095	a	fisica	1.05.03.00-5 Física das Partículas Elementares e Campos	\N
389	095	a	das	1.05.03.00-5 Física das Partículas Elementares e Campos	\N
390	095	a	particulas	1.05.03.00-5 Física das Partículas Elementares e Campos	\N
391	095	a	elementares	1.05.03.00-5 Física das Partículas Elementares e Campos	\N
392	095	a	campos	1.05.03.00-5 Física das Partículas Elementares e Campos	\N
393	095	a	1.05.03.01-3	1.05.03.01-3 Teoria Geral de Partículas e Campos	\N
394	095	a	teoria	1.05.03.01-3 Teoria Geral de Partículas e Campos	\N
395	095	a	geral	1.05.03.01-3 Teoria Geral de Partículas e Campos	\N
396	095	a	de	1.05.03.01-3 Teoria Geral de Partículas e Campos	\N
397	095	a	particulas	1.05.03.01-3 Teoria Geral de Partículas e Campos	\N
398	095	a	campos	1.05.03.01-3 Teoria Geral de Partículas e Campos	\N
399	095	a	1.05.03.02-1	1.05.03.02-1 Teorias Específicas e Modelos de Interação; Sistematica de Partículas; Raios Cósmicos	\N
400	095	a	teorias	1.05.03.02-1 Teorias Específicas e Modelos de Interação; Sistematica de Partículas; Raios Cósmicos	\N
401	095	a	especificas	1.05.03.02-1 Teorias Específicas e Modelos de Interação; Sistematica de Partículas; Raios Cósmicos	\N
402	095	a	modelos	1.05.03.02-1 Teorias Específicas e Modelos de Interação; Sistematica de Partículas; Raios Cósmicos	\N
403	095	a	de	1.05.03.02-1 Teorias Específicas e Modelos de Interação; Sistematica de Partículas; Raios Cósmicos	\N
404	095	a	interacao;	1.05.03.02-1 Teorias Específicas e Modelos de Interação; Sistematica de Partículas; Raios Cósmicos	\N
405	095	a	sistematica	1.05.03.02-1 Teorias Específicas e Modelos de Interação; Sistematica de Partículas; Raios Cósmicos	\N
406	095	a	de	1.05.03.02-1 Teorias Específicas e Modelos de Interação; Sistematica de Partículas; Raios Cósmicos	\N
407	095	a	particulas;	1.05.03.02-1 Teorias Específicas e Modelos de Interação; Sistematica de Partículas; Raios Cósmicos	\N
408	095	a	raios	1.05.03.02-1 Teorias Específicas e Modelos de Interação; Sistematica de Partículas; Raios Cósmicos	\N
409	095	a	cosmicos	1.05.03.02-1 Teorias Específicas e Modelos de Interação; Sistematica de Partículas; Raios Cósmicos	\N
410	095	a	1.05.03.03-0	1.05.03.03-0 Reações Específicas e Fenomiologia de Partículas	\N
411	095	a	reacoes	1.05.03.03-0 Reações Específicas e Fenomiologia de Partículas	\N
412	095	a	especificas	1.05.03.03-0 Reações Específicas e Fenomiologia de Partículas	\N
413	095	a	fenomiologia	1.05.03.03-0 Reações Específicas e Fenomiologia de Partículas	\N
414	095	a	de	1.05.03.03-0 Reações Específicas e Fenomiologia de Partículas	\N
415	095	a	particulas	1.05.03.03-0 Reações Específicas e Fenomiologia de Partículas	\N
416	095	a	1.05.03.04-8	1.05.03.04-8 Propriedades de Partículas Específicas e Ressonâncias	\N
417	095	a	propriedades	1.05.03.04-8 Propriedades de Partículas Específicas e Ressonâncias	\N
418	095	a	de	1.05.03.04-8 Propriedades de Partículas Específicas e Ressonâncias	\N
419	095	a	particulas	1.05.03.04-8 Propriedades de Partículas Específicas e Ressonâncias	\N
420	095	a	especificas	1.05.03.04-8 Propriedades de Partículas Específicas e Ressonâncias	\N
421	095	a	ressonancias	1.05.03.04-8 Propriedades de Partículas Específicas e Ressonâncias	\N
422	095	a	1.05.04.00-1	1.05.04.00-1 Física Nuclear	\N
423	095	a	fisica	1.05.04.00-1 Física Nuclear	\N
424	095	a	nuclear	1.05.04.00-1 Física Nuclear	\N
425	095	a	1.05.04.01-0	1.05.04.01-0 Estrutura Nuclear	\N
426	095	a	estrutura	1.05.04.01-0 Estrutura Nuclear	\N
427	095	a	nuclear	1.05.04.01-0 Estrutura Nuclear	\N
428	095	a	1.05.04.02-8	1.05.04.02-8 Desintegração Nuclear e Radioatividade	\N
429	095	a	desintegracao	1.05.04.02-8 Desintegração Nuclear e Radioatividade	\N
430	095	a	nuclear	1.05.04.02-8 Desintegração Nuclear e Radioatividade	\N
431	095	a	radioatividade	1.05.04.02-8 Desintegração Nuclear e Radioatividade	\N
432	095	a	1.05.04.03-6	1.05.04.03-6 Reações Nucleares e Espalhamento Geral	\N
433	095	a	reacoes	1.05.04.03-6 Reações Nucleares e Espalhamento Geral	\N
434	095	a	nucleares	1.05.04.03-6 Reações Nucleares e Espalhamento Geral	\N
435	095	a	espalhamento	1.05.04.03-6 Reações Nucleares e Espalhamento Geral	\N
436	095	a	geral	1.05.04.03-6 Reações Nucleares e Espalhamento Geral	\N
437	095	a	1.05.04.04-4	1.05.04.04-4 Reações Nucleares e Espalhamento (Reações Específicas)	\N
438	095	a	reacoes	1.05.04.04-4 Reações Nucleares e Espalhamento (Reações Específicas)	\N
439	095	a	nucleares	1.05.04.04-4 Reações Nucleares e Espalhamento (Reações Específicas)	\N
440	095	a	espalhamento	1.05.04.04-4 Reações Nucleares e Espalhamento (Reações Específicas)	\N
441	095	a	(reacoes	1.05.04.04-4 Reações Nucleares e Espalhamento (Reações Específicas)	\N
442	095	a	especificas)	1.05.04.04-4 Reações Nucleares e Espalhamento (Reações Específicas)	\N
443	095	a	1.05.04.05-2	1.05.04.05-2 Propriedades de Núcleos Específicos	\N
444	095	a	propriedades	1.05.04.05-2 Propriedades de Núcleos Específicos	\N
445	095	a	de	1.05.04.05-2 Propriedades de Núcleos Específicos	\N
446	095	a	nucleos	1.05.04.05-2 Propriedades de Núcleos Específicos	\N
447	095	a	especificos	1.05.04.05-2 Propriedades de Núcleos Específicos	\N
448	095	a	1.05.04.06-0	1.05.04.06-0 Métodos Experimentais e Instrumentação para Partículas Elementares e Física Nuclear	\N
449	095	a	metodos	1.05.04.06-0 Métodos Experimentais e Instrumentação para Partículas Elementares e Física Nuclear	\N
450	095	a	experimentais	1.05.04.06-0 Métodos Experimentais e Instrumentação para Partículas Elementares e Física Nuclear	\N
451	095	a	instrumentacao	1.05.04.06-0 Métodos Experimentais e Instrumentação para Partículas Elementares e Física Nuclear	\N
452	095	a	para	1.05.04.06-0 Métodos Experimentais e Instrumentação para Partículas Elementares e Física Nuclear	\N
453	095	a	particulas	1.05.04.06-0 Métodos Experimentais e Instrumentação para Partículas Elementares e Física Nuclear	\N
454	095	a	elementares	1.05.04.06-0 Métodos Experimentais e Instrumentação para Partículas Elementares e Física Nuclear	\N
455	095	a	fisica	1.05.04.06-0 Métodos Experimentais e Instrumentação para Partículas Elementares e Física Nuclear	\N
456	095	a	nuclear	1.05.04.06-0 Métodos Experimentais e Instrumentação para Partículas Elementares e Física Nuclear	\N
457	095	a	1.05.05.00-8	1.05.05.00-8 Física Atômica e Molécular	\N
458	095	a	fisica	1.05.05.00-8 Física Atômica e Molécular	\N
459	095	a	atomica	1.05.05.00-8 Física Atômica e Molécular	\N
460	095	a	molecular	1.05.05.00-8 Física Atômica e Molécular	\N
461	095	a	1.05.05.01-6	1.05.05.01-6 Estrutura Eletrônica de Átomos e Moléculas; Teoria	\N
462	095	a	estrutura	1.05.05.01-6 Estrutura Eletrônica de Átomos e Moléculas; Teoria	\N
463	095	a	eletronica	1.05.05.01-6 Estrutura Eletrônica de Átomos e Moléculas; Teoria	\N
464	095	a	de	1.05.05.01-6 Estrutura Eletrônica de Átomos e Moléculas; Teoria	\N
465	095	a	atomos	1.05.05.01-6 Estrutura Eletrônica de Átomos e Moléculas; Teoria	\N
466	095	a	moleculas;	1.05.05.01-6 Estrutura Eletrônica de Átomos e Moléculas; Teoria	\N
467	095	a	teoria	1.05.05.01-6 Estrutura Eletrônica de Átomos e Moléculas; Teoria	\N
468	095	a	1.05.05.02-4	1.05.05.02-4 Espectros Atômicos e Integração de Fótons	\N
469	095	a	espectros	1.05.05.02-4 Espectros Atômicos e Integração de Fótons	\N
470	095	a	atomicos	1.05.05.02-4 Espectros Atômicos e Integração de Fótons	\N
471	095	a	integracao	1.05.05.02-4 Espectros Atômicos e Integração de Fótons	\N
472	095	a	de	1.05.05.02-4 Espectros Atômicos e Integração de Fótons	\N
473	095	a	fotons	1.05.05.02-4 Espectros Atômicos e Integração de Fótons	\N
474	095	a	1.05.05.03-2	1.05.05.03-2 Espectros Moléculares e Interações de Fótons com Moléculas	\N
475	095	a	espectros	1.05.05.03-2 Espectros Moléculares e Interações de Fótons com Moléculas	\N
476	095	a	moleculares	1.05.05.03-2 Espectros Moléculares e Interações de Fótons com Moléculas	\N
477	095	a	interacoes	1.05.05.03-2 Espectros Moléculares e Interações de Fótons com Moléculas	\N
478	095	a	de	1.05.05.03-2 Espectros Moléculares e Interações de Fótons com Moléculas	\N
479	095	a	fotons	1.05.05.03-2 Espectros Moléculares e Interações de Fótons com Moléculas	\N
480	095	a	com	1.05.05.03-2 Espectros Moléculares e Interações de Fótons com Moléculas	\N
481	095	a	moleculas	1.05.05.03-2 Espectros Moléculares e Interações de Fótons com Moléculas	\N
482	095	a	1.05.05.04-0	1.05.05.04-0 Processos de Colisão e Interações de Átomos e Moléculas	\N
483	095	a	processos	1.05.05.04-0 Processos de Colisão e Interações de Átomos e Moléculas	\N
484	095	a	de	1.05.05.04-0 Processos de Colisão e Interações de Átomos e Moléculas	\N
485	095	a	colisao	1.05.05.04-0 Processos de Colisão e Interações de Átomos e Moléculas	\N
486	095	a	interacoes	1.05.05.04-0 Processos de Colisão e Interações de Átomos e Moléculas	\N
487	095	a	de	1.05.05.04-0 Processos de Colisão e Interações de Átomos e Moléculas	\N
488	095	a	atomos	1.05.05.04-0 Processos de Colisão e Interações de Átomos e Moléculas	\N
489	095	a	moleculas	1.05.05.04-0 Processos de Colisão e Interações de Átomos e Moléculas	\N
490	095	a	1.05.05.05-9	1.05.05.05-9 Inf.Sobre Átomos e Moléculas Obtidos Experimentalmente;Instrumentação e Técnicas	\N
491	095	a	inf.sobre	1.05.05.05-9 Inf.Sobre Átomos e Moléculas Obtidos Experimentalmente;Instrumentação e Técnicas	\N
492	095	a	atomos	1.05.05.05-9 Inf.Sobre Átomos e Moléculas Obtidos Experimentalmente;Instrumentação e Técnicas	\N
493	095	a	moleculas	1.05.05.05-9 Inf.Sobre Átomos e Moléculas Obtidos Experimentalmente;Instrumentação e Técnicas	\N
494	095	a	obtidos	1.05.05.05-9 Inf.Sobre Átomos e Moléculas Obtidos Experimentalmente;Instrumentação e Técnicas	\N
495	095	a	experimentalmente;instrumentacao	1.05.05.05-9 Inf.Sobre Átomos e Moléculas Obtidos Experimentalmente;Instrumentação e Técnicas	\N
496	095	a	tecnicas	1.05.05.05-9 Inf.Sobre Átomos e Moléculas Obtidos Experimentalmente;Instrumentação e Técnicas	\N
497	095	a	1.05.05.06-7	1.05.05.06-7 Estudos de Átomos e Moléculas Especiais	\N
498	095	a	estudos	1.05.05.06-7 Estudos de Átomos e Moléculas Especiais	\N
499	095	a	de	1.05.05.06-7 Estudos de Átomos e Moléculas Especiais	\N
500	095	a	atomos	1.05.05.06-7 Estudos de Átomos e Moléculas Especiais	\N
501	095	a	moleculas	1.05.05.06-7 Estudos de Átomos e Moléculas Especiais	\N
502	095	a	especiais	1.05.05.06-7 Estudos de Átomos e Moléculas Especiais	\N
503	095	a	1.05.06.00-4	1.05.06.00-4 Física dos Fluidos, Física de Plasmas e Descargas Elétricas	\N
504	095	a	fisica	1.05.06.00-4 Física dos Fluidos, Física de Plasmas e Descargas Elétricas	\N
505	095	a	dos	1.05.06.00-4 Física dos Fluidos, Física de Plasmas e Descargas Elétricas	\N
506	095	a	fluidos,	1.05.06.00-4 Física dos Fluidos, Física de Plasmas e Descargas Elétricas	\N
507	095	a	fisica	1.05.06.00-4 Física dos Fluidos, Física de Plasmas e Descargas Elétricas	\N
508	095	a	de	1.05.06.00-4 Física dos Fluidos, Física de Plasmas e Descargas Elétricas	\N
509	095	a	plasmas	1.05.06.00-4 Física dos Fluidos, Física de Plasmas e Descargas Elétricas	\N
510	095	a	descargas	1.05.06.00-4 Física dos Fluidos, Física de Plasmas e Descargas Elétricas	\N
511	095	a	eletricas	1.05.06.00-4 Física dos Fluidos, Física de Plasmas e Descargas Elétricas	\N
512	095	a	1.05.06.01-2	1.05.06.01-2 Cinética e Teoria de Transporte de Fluidos; Propriedades Físicas de Gases	\N
513	095	a	cinetica	1.05.06.01-2 Cinética e Teoria de Transporte de Fluidos; Propriedades Físicas de Gases	\N
514	095	a	teoria	1.05.06.01-2 Cinética e Teoria de Transporte de Fluidos; Propriedades Físicas de Gases	\N
515	095	a	de	1.05.06.01-2 Cinética e Teoria de Transporte de Fluidos; Propriedades Físicas de Gases	\N
516	095	a	transporte	1.05.06.01-2 Cinética e Teoria de Transporte de Fluidos; Propriedades Físicas de Gases	\N
517	095	a	de	1.05.06.01-2 Cinética e Teoria de Transporte de Fluidos; Propriedades Físicas de Gases	\N
518	095	a	fluidos;	1.05.06.01-2 Cinética e Teoria de Transporte de Fluidos; Propriedades Físicas de Gases	\N
519	095	a	propriedades	1.05.06.01-2 Cinética e Teoria de Transporte de Fluidos; Propriedades Físicas de Gases	\N
520	095	a	fisicas	1.05.06.01-2 Cinética e Teoria de Transporte de Fluidos; Propriedades Físicas de Gases	\N
521	095	a	de	1.05.06.01-2 Cinética e Teoria de Transporte de Fluidos; Propriedades Físicas de Gases	\N
522	095	a	gases	1.05.06.01-2 Cinética e Teoria de Transporte de Fluidos; Propriedades Físicas de Gases	\N
523	095	a	1.05.06.02-0	1.05.06.02-0 Física de Plasmas e Descargas Elétricas	\N
524	095	a	fisica	1.05.06.02-0 Física de Plasmas e Descargas Elétricas	\N
525	095	a	de	1.05.06.02-0 Física de Plasmas e Descargas Elétricas	\N
526	095	a	plasmas	1.05.06.02-0 Física de Plasmas e Descargas Elétricas	\N
527	095	a	descargas	1.05.06.02-0 Física de Plasmas e Descargas Elétricas	\N
528	095	a	eletricas	1.05.06.02-0 Física de Plasmas e Descargas Elétricas	\N
529	095	a	1.05.07.00-0	1.05.07.00-0 Física da Matéria Condensada	\N
530	095	a	fisica	1.05.07.00-0 Física da Matéria Condensada	\N
531	095	a	da	1.05.07.00-0 Física da Matéria Condensada	\N
532	095	a	materia	1.05.07.00-0 Física da Matéria Condensada	\N
533	095	a	condensada	1.05.07.00-0 Física da Matéria Condensada	\N
534	095	a	1.05.07.01-9	1.05.07.01-9 Estrutura de Líquidos e Sólidos; Cristalografia	\N
535	095	a	estrutura	1.05.07.01-9 Estrutura de Líquidos e Sólidos; Cristalografia	\N
536	095	a	de	1.05.07.01-9 Estrutura de Líquidos e Sólidos; Cristalografia	\N
537	095	a	liquidos	1.05.07.01-9 Estrutura de Líquidos e Sólidos; Cristalografia	\N
538	095	a	solidos;	1.05.07.01-9 Estrutura de Líquidos e Sólidos; Cristalografia	\N
539	095	a	cristalografia	1.05.07.01-9 Estrutura de Líquidos e Sólidos; Cristalografia	\N
540	095	a	1.05.07.02-7	1.05.07.02-7 Propriedades Mecânicas e Acústicas da Matéria Condensada	\N
541	095	a	propriedades	1.05.07.02-7 Propriedades Mecânicas e Acústicas da Matéria Condensada	\N
542	095	a	mecanicas	1.05.07.02-7 Propriedades Mecânicas e Acústicas da Matéria Condensada	\N
543	095	a	acusticas	1.05.07.02-7 Propriedades Mecânicas e Acústicas da Matéria Condensada	\N
544	095	a	da	1.05.07.02-7 Propriedades Mecânicas e Acústicas da Matéria Condensada	\N
545	095	a	materia	1.05.07.02-7 Propriedades Mecânicas e Acústicas da Matéria Condensada	\N
546	095	a	condensada	1.05.07.02-7 Propriedades Mecânicas e Acústicas da Matéria Condensada	\N
547	095	a	1.05.07.03-5	1.05.07.03-5 Dinâmica da Rede e Estatística de Cristais	\N
548	095	a	dinamica	1.05.07.03-5 Dinâmica da Rede e Estatística de Cristais	\N
549	095	a	da	1.05.07.03-5 Dinâmica da Rede e Estatística de Cristais	\N
550	095	a	rede	1.05.07.03-5 Dinâmica da Rede e Estatística de Cristais	\N
551	095	a	estatistica	1.05.07.03-5 Dinâmica da Rede e Estatística de Cristais	\N
552	095	a	de	1.05.07.03-5 Dinâmica da Rede e Estatística de Cristais	\N
553	095	a	cristais	1.05.07.03-5 Dinâmica da Rede e Estatística de Cristais	\N
554	095	a	1.05.07.04-3	1.05.07.04-3 Equação de Estado, Equilíbrio de Fases e Transições de Fase	\N
555	095	a	equacao	1.05.07.04-3 Equação de Estado, Equilíbrio de Fases e Transições de Fase	\N
556	095	a	de	1.05.07.04-3 Equação de Estado, Equilíbrio de Fases e Transições de Fase	\N
557	095	a	estado,	1.05.07.04-3 Equação de Estado, Equilíbrio de Fases e Transições de Fase	\N
558	095	a	equilibrio	1.05.07.04-3 Equação de Estado, Equilíbrio de Fases e Transições de Fase	\N
559	095	a	de	1.05.07.04-3 Equação de Estado, Equilíbrio de Fases e Transições de Fase	\N
560	095	a	fases	1.05.07.04-3 Equação de Estado, Equilíbrio de Fases e Transições de Fase	\N
561	095	a	transicoes	1.05.07.04-3 Equação de Estado, Equilíbrio de Fases e Transições de Fase	\N
562	095	a	de	1.05.07.04-3 Equação de Estado, Equilíbrio de Fases e Transições de Fase	\N
563	095	a	fase	1.05.07.04-3 Equação de Estado, Equilíbrio de Fases e Transições de Fase	\N
564	095	a	1.05.07.05-1	1.05.07.05-1 Propriedades Térmicas da Matéria Condensada	\N
565	095	a	propriedades	1.05.07.05-1 Propriedades Térmicas da Matéria Condensada	\N
566	095	a	termicas	1.05.07.05-1 Propriedades Térmicas da Matéria Condensada	\N
567	095	a	da	1.05.07.05-1 Propriedades Térmicas da Matéria Condensada	\N
568	095	a	materia	1.05.07.05-1 Propriedades Térmicas da Matéria Condensada	\N
569	095	a	condensada	1.05.07.05-1 Propriedades Térmicas da Matéria Condensada	\N
570	095	a	1.05.07.06-0	1.05.07.06-0 Propriedades de Transportes de Matéria Condensada (Não Eletrônicas)	\N
571	095	a	propriedades	1.05.07.06-0 Propriedades de Transportes de Matéria Condensada (Não Eletrônicas)	\N
572	095	a	de	1.05.07.06-0 Propriedades de Transportes de Matéria Condensada (Não Eletrônicas)	\N
573	095	a	transportes	1.05.07.06-0 Propriedades de Transportes de Matéria Condensada (Não Eletrônicas)	\N
574	095	a	de	1.05.07.06-0 Propriedades de Transportes de Matéria Condensada (Não Eletrônicas)	\N
575	095	a	materia	1.05.07.06-0 Propriedades de Transportes de Matéria Condensada (Não Eletrônicas)	\N
576	095	a	condensada	1.05.07.06-0 Propriedades de Transportes de Matéria Condensada (Não Eletrônicas)	\N
577	095	a	(nao	1.05.07.06-0 Propriedades de Transportes de Matéria Condensada (Não Eletrônicas)	\N
578	095	a	eletronicas)	1.05.07.06-0 Propriedades de Transportes de Matéria Condensada (Não Eletrônicas)	\N
579	095	a	1.05.07.07-8	1.05.07.07-8 Campos Quânticos e Sólidos, Hélio, Líquido, Sólido	\N
580	095	a	campos	1.05.07.07-8 Campos Quânticos e Sólidos, Hélio, Líquido, Sólido	\N
581	095	a	quanticos	1.05.07.07-8 Campos Quânticos e Sólidos, Hélio, Líquido, Sólido	\N
582	095	a	solidos,	1.05.07.07-8 Campos Quânticos e Sólidos, Hélio, Líquido, Sólido	\N
583	095	a	helio,	1.05.07.07-8 Campos Quânticos e Sólidos, Hélio, Líquido, Sólido	\N
584	095	a	liquido,	1.05.07.07-8 Campos Quânticos e Sólidos, Hélio, Líquido, Sólido	\N
585	095	a	solido	1.05.07.07-8 Campos Quânticos e Sólidos, Hélio, Líquido, Sólido	\N
586	095	a	1.05.07.08-6	1.05.07.08-6 Superfícies e Interfaces; Películas e Filamentos	\N
587	095	a	superficies	1.05.07.08-6 Superfícies e Interfaces; Películas e Filamentos	\N
588	095	a	interfaces;	1.05.07.08-6 Superfícies e Interfaces; Películas e Filamentos	\N
589	095	a	peliculas	1.05.07.08-6 Superfícies e Interfaces; Películas e Filamentos	\N
590	095	a	filamentos	1.05.07.08-6 Superfícies e Interfaces; Películas e Filamentos	\N
591	095	a	1.05.07.09-4	1.05.07.09-4 Estados Eletrônicos	\N
592	095	a	estados	1.05.07.09-4 Estados Eletrônicos	\N
593	095	a	eletronicos	1.05.07.09-4 Estados Eletrônicos	\N
594	095	a	1.05.07.10-8	1.05.07.10-8 Transp.Eletrônicos e Prop. Elétricas de Superfícies; Interfaces e Películas	\N
595	095	a	transp.eletronicos	1.05.07.10-8 Transp.Eletrônicos e Prop. Elétricas de Superfícies; Interfaces e Películas	\N
596	095	a	prop.	1.05.07.10-8 Transp.Eletrônicos e Prop. Elétricas de Superfícies; Interfaces e Películas	\N
597	095	a	eletricas	1.05.07.10-8 Transp.Eletrônicos e Prop. Elétricas de Superfícies; Interfaces e Películas	\N
598	095	a	de	1.05.07.10-8 Transp.Eletrônicos e Prop. Elétricas de Superfícies; Interfaces e Películas	\N
599	095	a	superficies;	1.05.07.10-8 Transp.Eletrônicos e Prop. Elétricas de Superfícies; Interfaces e Películas	\N
600	095	a	interfaces	1.05.07.10-8 Transp.Eletrônicos e Prop. Elétricas de Superfícies; Interfaces e Películas	\N
601	095	a	peliculas	1.05.07.10-8 Transp.Eletrônicos e Prop. Elétricas de Superfícies; Interfaces e Películas	\N
602	095	a	1.05.07.11-6	1.05.07.11-6 Estruturas Eletrônicas e Propriedades Elétricas de Superfícies Interfaces e Películas	\N
603	095	a	estruturas	1.05.07.11-6 Estruturas Eletrônicas e Propriedades Elétricas de Superfícies Interfaces e Películas	\N
604	095	a	eletronicas	1.05.07.11-6 Estruturas Eletrônicas e Propriedades Elétricas de Superfícies Interfaces e Películas	\N
605	095	a	propriedades	1.05.07.11-6 Estruturas Eletrônicas e Propriedades Elétricas de Superfícies Interfaces e Películas	\N
606	095	a	eletricas	1.05.07.11-6 Estruturas Eletrônicas e Propriedades Elétricas de Superfícies Interfaces e Películas	\N
607	095	a	de	1.05.07.11-6 Estruturas Eletrônicas e Propriedades Elétricas de Superfícies Interfaces e Películas	\N
608	095	a	superficies	1.05.07.11-6 Estruturas Eletrônicas e Propriedades Elétricas de Superfícies Interfaces e Películas	\N
609	095	a	interfaces	1.05.07.11-6 Estruturas Eletrônicas e Propriedades Elétricas de Superfícies Interfaces e Películas	\N
610	095	a	peliculas	1.05.07.11-6 Estruturas Eletrônicas e Propriedades Elétricas de Superfícies Interfaces e Películas	\N
611	095	a	1.05.07.12-4	1.05.07.12-4 Supercondutividade	\N
612	095	a	supercondutividade	1.05.07.12-4 Supercondutividade	\N
613	095	a	1.05.07.13-2	1.05.07.13-2 Materiais Magnéticos e Propriedades Magnéticas	\N
614	095	a	materiais	1.05.07.13-2 Materiais Magnéticos e Propriedades Magnéticas	\N
615	095	a	magneticos	1.05.07.13-2 Materiais Magnéticos e Propriedades Magnéticas	\N
616	095	a	propriedades	1.05.07.13-2 Materiais Magnéticos e Propriedades Magnéticas	\N
617	095	a	magneticas	1.05.07.13-2 Materiais Magnéticos e Propriedades Magnéticas	\N
618	095	a	1.05.07.14-0	1.05.07.14-0 Ressonância Mag.e Relax.Na Mat.Condens;Efeitos Mosbauer;Corr.Ang.Pertubada	\N
619	095	a	ressonancia	1.05.07.14-0 Ressonância Mag.e Relax.Na Mat.Condens;Efeitos Mosbauer;Corr.Ang.Pertubada	\N
620	095	a	mag.e	1.05.07.14-0 Ressonância Mag.e Relax.Na Mat.Condens;Efeitos Mosbauer;Corr.Ang.Pertubada	\N
621	095	a	relax.na	1.05.07.14-0 Ressonância Mag.e Relax.Na Mat.Condens;Efeitos Mosbauer;Corr.Ang.Pertubada	\N
622	095	a	mat.condens;efeitos	1.05.07.14-0 Ressonância Mag.e Relax.Na Mat.Condens;Efeitos Mosbauer;Corr.Ang.Pertubada	\N
623	095	a	mosbauer;corr.ang.pertubada	1.05.07.14-0 Ressonância Mag.e Relax.Na Mat.Condens;Efeitos Mosbauer;Corr.Ang.Pertubada	\N
624	095	a	1.05.07.15-9	1.05.07.15-9 Materiais Dielétricos e Propriedades Dielétricas	\N
625	095	a	materiais	1.05.07.15-9 Materiais Dielétricos e Propriedades Dielétricas	\N
626	095	a	dieletricos	1.05.07.15-9 Materiais Dielétricos e Propriedades Dielétricas	\N
627	095	a	propriedades	1.05.07.15-9 Materiais Dielétricos e Propriedades Dielétricas	\N
628	095	a	dieletricas	1.05.07.15-9 Materiais Dielétricos e Propriedades Dielétricas	\N
629	095	a	1.05.07.16-7	1.05.07.16-7 Prop.Óticas e Espectrosc.da Mat.Condens;Outras Inter.da Mat.Com Rad.e Part.	\N
630	095	a	prop.oticas	1.05.07.16-7 Prop.Óticas e Espectrosc.da Mat.Condens;Outras Inter.da Mat.Com Rad.e Part.	\N
631	095	a	espectrosc.da	1.05.07.16-7 Prop.Óticas e Espectrosc.da Mat.Condens;Outras Inter.da Mat.Com Rad.e Part.	\N
632	095	a	mat.condens;outras	1.05.07.16-7 Prop.Óticas e Espectrosc.da Mat.Condens;Outras Inter.da Mat.Com Rad.e Part.	\N
633	095	a	inter.da	1.05.07.16-7 Prop.Óticas e Espectrosc.da Mat.Condens;Outras Inter.da Mat.Com Rad.e Part.	\N
634	095	a	mat.com	1.05.07.16-7 Prop.Óticas e Espectrosc.da Mat.Condens;Outras Inter.da Mat.Com Rad.e Part.	\N
635	095	a	rad.e	1.05.07.16-7 Prop.Óticas e Espectrosc.da Mat.Condens;Outras Inter.da Mat.Com Rad.e Part.	\N
636	095	a	part.	1.05.07.16-7 Prop.Óticas e Espectrosc.da Mat.Condens;Outras Inter.da Mat.Com Rad.e Part.	\N
637	095	a	1.05.07.17-5	1.05.07.17-5 Emissão Eletrônica e Iônica por Líquidos e Sólidos; Fenômenos de Impacto	\N
638	095	a	emissao	1.05.07.17-5 Emissão Eletrônica e Iônica por Líquidos e Sólidos; Fenômenos de Impacto	\N
639	095	a	eletronica	1.05.07.17-5 Emissão Eletrônica e Iônica por Líquidos e Sólidos; Fenômenos de Impacto	\N
640	095	a	ionica	1.05.07.17-5 Emissão Eletrônica e Iônica por Líquidos e Sólidos; Fenômenos de Impacto	\N
641	095	a	por	1.05.07.17-5 Emissão Eletrônica e Iônica por Líquidos e Sólidos; Fenômenos de Impacto	\N
642	095	a	liquidos	1.05.07.17-5 Emissão Eletrônica e Iônica por Líquidos e Sólidos; Fenômenos de Impacto	\N
643	095	a	solidos;	1.05.07.17-5 Emissão Eletrônica e Iônica por Líquidos e Sólidos; Fenômenos de Impacto	\N
644	095	a	fenomenos	1.05.07.17-5 Emissão Eletrônica e Iônica por Líquidos e Sólidos; Fenômenos de Impacto	\N
645	095	a	de	1.05.07.17-5 Emissão Eletrônica e Iônica por Líquidos e Sólidos; Fenômenos de Impacto	\N
646	095	a	impacto	1.05.07.17-5 Emissão Eletrônica e Iônica por Líquidos e Sólidos; Fenômenos de Impacto	\N
647	095	a	1.06.00.00-0	1.06.00.00-0 Química	\N
648	095	a	quimica	1.06.00.00-0 Química	\N
649	095	a	1.06.01.00-7	1.06.01.00-7 Química Orgânica	\N
650	095	a	quimica	1.06.01.00-7 Química Orgânica	\N
651	095	a	organica	1.06.01.00-7 Química Orgânica	\N
652	095	a	1.06.01.01-5	1.06.01.01-5 Estrutura, Conformação e Estereoquímica	\N
653	095	a	estrutura,	1.06.01.01-5 Estrutura, Conformação e Estereoquímica	\N
654	095	a	conformacao	1.06.01.01-5 Estrutura, Conformação e Estereoquímica	\N
655	095	a	estereoquimica	1.06.01.01-5 Estrutura, Conformação e Estereoquímica	\N
656	095	a	1.06.01.02-3	1.06.01.02-3 Sintese Orgânica	\N
657	095	a	sintese	1.06.01.02-3 Sintese Orgânica	\N
658	095	a	organica	1.06.01.02-3 Sintese Orgânica	\N
659	095	a	1.06.01.03-1	1.06.01.03-1 Fisico-Química Orgânica	\N
660	095	a	fisico-quimica	1.06.01.03-1 Fisico-Química Orgânica	\N
661	095	a	organica	1.06.01.03-1 Fisico-Química Orgânica	\N
662	095	a	1.06.01.04-0	1.06.01.04-0 Fotoquímica Orgânica	\N
663	095	a	fotoquimica	1.06.01.04-0 Fotoquímica Orgânica	\N
664	095	a	organica	1.06.01.04-0 Fotoquímica Orgânica	\N
665	095	a	1.06.01.05-8	1.06.01.05-8 Química dos Produtos Naturais	\N
666	095	a	quimica	1.06.01.05-8 Química dos Produtos Naturais	\N
667	095	a	dos	1.06.01.05-8 Química dos Produtos Naturais	\N
668	095	a	produtos	1.06.01.05-8 Química dos Produtos Naturais	\N
669	095	a	naturais	1.06.01.05-8 Química dos Produtos Naturais	\N
670	095	a	1.06.01.06-6	1.06.01.06-6 Evolução, Sistemática e Ecologia Química	\N
671	095	a	evolucao,	1.06.01.06-6 Evolução, Sistemática e Ecologia Química	\N
672	095	a	sistematica	1.06.01.06-6 Evolução, Sistemática e Ecologia Química	\N
673	095	a	ecologia	1.06.01.06-6 Evolução, Sistemática e Ecologia Química	\N
674	095	a	quimica	1.06.01.06-6 Evolução, Sistemática e Ecologia Química	\N
675	095	a	1.06.01.07-4	1.06.01.07-4 Polimeros e Colóides	\N
676	095	a	polimeros	1.06.01.07-4 Polimeros e Colóides	\N
677	095	a	coloides	1.06.01.07-4 Polimeros e Colóides	\N
678	095	a	1.06.02.00-3	1.06.02.00-3 Química Inorgânica	\N
679	095	a	quimica	1.06.02.00-3 Química Inorgânica	\N
680	095	a	inorganica	1.06.02.00-3 Química Inorgânica	\N
681	095	a	1.06.02.01-1	1.06.02.01-1 Campos de Coordenação	\N
682	095	a	campos	1.06.02.01-1 Campos de Coordenação	\N
683	095	a	de	1.06.02.01-1 Campos de Coordenação	\N
684	095	a	coordenacao	1.06.02.01-1 Campos de Coordenação	\N
685	095	a	1.06.02.02-0	1.06.02.02-0 Não-Metais e Seus Compostos	\N
686	095	a	nao-metais	1.06.02.02-0 Não-Metais e Seus Compostos	\N
687	095	a	seus	1.06.02.02-0 Não-Metais e Seus Compostos	\N
688	095	a	compostos	1.06.02.02-0 Não-Metais e Seus Compostos	\N
689	095	a	1.06.02.03-8	1.06.02.03-8 Compostos Organo-Metálicos	\N
690	095	a	compostos	1.06.02.03-8 Compostos Organo-Metálicos	\N
691	095	a	organo-metalicos	1.06.02.03-8 Compostos Organo-Metálicos	\N
692	095	a	1.06.02.04-6	1.06.02.04-6 Determinação de Estrutura de Compostos Inorgânicos	\N
693	095	a	determinacao	1.06.02.04-6 Determinação de Estrutura de Compostos Inorgânicos	\N
694	095	a	de	1.06.02.04-6 Determinação de Estrutura de Compostos Inorgânicos	\N
695	095	a	estrutura	1.06.02.04-6 Determinação de Estrutura de Compostos Inorgânicos	\N
696	095	a	de	1.06.02.04-6 Determinação de Estrutura de Compostos Inorgânicos	\N
697	095	a	compostos	1.06.02.04-6 Determinação de Estrutura de Compostos Inorgânicos	\N
698	095	a	inorganicos	1.06.02.04-6 Determinação de Estrutura de Compostos Inorgânicos	\N
699	095	a	1.06.02.05-4	1.06.02.05-4 Foto-Química Inorgânica	\N
700	095	a	foto-quimica	1.06.02.05-4 Foto-Química Inorgânica	\N
701	095	a	inorganica	1.06.02.05-4 Foto-Química Inorgânica	\N
702	095	a	1.06.02.06-2	1.06.02.06-2 Fisico Química Inorgânica	\N
703	095	a	fisico	1.06.02.06-2 Fisico Química Inorgânica	\N
704	095	a	quimica	1.06.02.06-2 Fisico Química Inorgânica	\N
705	095	a	inorganica	1.06.02.06-2 Fisico Química Inorgânica	\N
706	095	a	1.06.02.07-0	1.06.02.07-0 Química Bio-Inorgânica	\N
707	095	a	quimica	1.06.02.07-0 Química Bio-Inorgânica	\N
708	095	a	bio-inorganica	1.06.02.07-0 Química Bio-Inorgânica	\N
709	095	a	1.06.03.00-0	1.06.03.00-0 Fisico-Química	\N
710	095	a	fisico-quimica	1.06.03.00-0 Fisico-Química	\N
711	095	a	1.06.03.01-8	1.06.03.01-8 Cinética Química e Catálise	\N
712	095	a	cinetica	1.06.03.01-8 Cinética Química e Catálise	\N
713	095	a	quimica	1.06.03.01-8 Cinética Química e Catálise	\N
714	095	a	catalise	1.06.03.01-8 Cinética Química e Catálise	\N
715	095	a	1.06.03.02-6	1.06.03.02-6 Eletroquímica	\N
716	095	a	eletroquimica	1.06.03.02-6 Eletroquímica	\N
717	095	a	1.06.03.03-4	1.06.03.03-4 Espectroscopia	\N
718	095	a	espectroscopia	1.06.03.03-4 Espectroscopia	\N
719	095	a	1.06.03.04-2	1.06.03.04-2 Química de Interfaces	\N
720	095	a	quimica	1.06.03.04-2 Química de Interfaces	\N
721	095	a	de	1.06.03.04-2 Química de Interfaces	\N
722	095	a	interfaces	1.06.03.04-2 Química de Interfaces	\N
723	095	a	1.06.03.05-0	1.06.03.05-0 Química do Estado Condensado	\N
724	095	a	quimica	1.06.03.05-0 Química do Estado Condensado	\N
725	095	a	do	1.06.03.05-0 Química do Estado Condensado	\N
726	095	a	estado	1.06.03.05-0 Química do Estado Condensado	\N
727	095	a	condensado	1.06.03.05-0 Química do Estado Condensado	\N
728	095	a	1.06.03.06-9	1.06.03.06-9 Química Nuclear e Radioquímica	\N
729	095	a	quimica	1.06.03.06-9 Química Nuclear e Radioquímica	\N
730	095	a	nuclear	1.06.03.06-9 Química Nuclear e Radioquímica	\N
731	095	a	radioquimica	1.06.03.06-9 Química Nuclear e Radioquímica	\N
732	095	a	1.06.03.07-7	1.06.03.07-7 Química Teórica	\N
733	095	a	quimica	1.06.03.07-7 Química Teórica	\N
734	095	a	teorica	1.06.03.07-7 Química Teórica	\N
735	095	a	1.06.03.08-5	1.06.03.08-5 Termodinâmica Química	\N
736	095	a	termodinamica	1.06.03.08-5 Termodinâmica Química	\N
737	095	a	quimica	1.06.03.08-5 Termodinâmica Química	\N
738	095	a	1.06.04.00-6	1.06.04.00-6 Química Analítica	\N
739	095	a	quimica	1.06.04.00-6 Química Analítica	\N
740	095	a	analitica	1.06.04.00-6 Química Analítica	\N
741	095	a	1.06.04.01-4	1.06.04.01-4 Separação	\N
742	095	a	separacao	1.06.04.01-4 Separação	\N
743	095	a	1.06.04.02-2	1.06.04.02-2 Métodos Óticos de Análise	\N
744	095	a	metodos	1.06.04.02-2 Métodos Óticos de Análise	\N
745	095	a	oticos	1.06.04.02-2 Métodos Óticos de Análise	\N
746	095	a	de	1.06.04.02-2 Métodos Óticos de Análise	\N
747	095	a	analise	1.06.04.02-2 Métodos Óticos de Análise	\N
748	095	a	1.06.04.03-0	1.06.04.03-0 Eletroanalítica	\N
749	095	a	eletroanalitica	1.06.04.03-0 Eletroanalítica	\N
750	095	a	1.06.04.04-9	1.06.04.04-9 Gravimetria	\N
751	095	a	gravimetria	1.06.04.04-9 Gravimetria	\N
752	095	a	1.06.04.05-7	1.06.04.05-7 Titimetria	\N
753	095	a	titimetria	1.06.04.05-7 Titimetria	\N
754	095	a	1.06.04.06-5	1.06.04.06-5 Instrumentação Analítica	\N
755	095	a	instrumentacao	1.06.04.06-5 Instrumentação Analítica	\N
756	095	a	analitica	1.06.04.06-5 Instrumentação Analítica	\N
757	095	a	1.06.04.07-3	1.06.04.07-3 Análise de Traços e Química Ambiental	\N
758	095	a	analise	1.06.04.07-3 Análise de Traços e Química Ambiental	\N
759	095	a	de	1.06.04.07-3 Análise de Traços e Química Ambiental	\N
760	095	a	tracos	1.06.04.07-3 Análise de Traços e Química Ambiental	\N
761	095	a	quimica	1.06.04.07-3 Análise de Traços e Química Ambiental	\N
762	095	a	ambiental	1.06.04.07-3 Análise de Traços e Química Ambiental	\N
763	095	a	1.07.00.00-5	1.07.00.00-5 GeoCiências	\N
764	095	a	geociencias	1.07.00.00-5 GeoCiências	\N
765	095	a	1.07.01.00-1	1.07.01.00-1 Geologia	\N
766	095	a	geologia	1.07.01.00-1 Geologia	\N
767	095	a	1.07.01.01-0	1.07.01.01-0 Mineralogia	\N
768	095	a	mineralogia	1.07.01.01-0 Mineralogia	\N
769	095	a	1.07.01.02-8	1.07.01.02-8 Petrologia	\N
770	095	a	petrologia	1.07.01.02-8 Petrologia	\N
771	095	a	1.07.01.03-6	1.07.01.03-6 Geoquímica	\N
772	095	a	geoquimica	1.07.01.03-6 Geoquímica	\N
773	095	a	1.07.01.04-4	1.07.01.04-4 Geologia Regional	\N
774	095	a	geologia	1.07.01.04-4 Geologia Regional	\N
775	095	a	regional	1.07.01.04-4 Geologia Regional	\N
776	095	a	1.07.01.05-2	1.07.01.05-2 Geotectônica	\N
777	095	a	geotectonica	1.07.01.05-2 Geotectônica	\N
778	095	a	1.07.01.06-0	1.07.01.06-0 Geocronologia	\N
779	095	a	geocronologia	1.07.01.06-0 Geocronologia	\N
780	095	a	1.07.01.07-9	1.07.01.07-9 Cartografia Geológica	\N
781	095	a	cartografia	1.07.01.07-9 Cartografia Geológica	\N
782	095	a	geologica	1.07.01.07-9 Cartografia Geológica	\N
783	095	a	1.07.01.08-7	1.07.01.08-7 Metalogenia	\N
784	095	a	metalogenia	1.07.01.08-7 Metalogenia	\N
785	095	a	1.07.01.09-5	1.07.01.09-5 Hidrogeologia	\N
786	095	a	hidrogeologia	1.07.01.09-5 Hidrogeologia	\N
787	095	a	1.07.01.10-9	1.07.01.10-9 Prospecção Mineral	\N
788	095	a	prospeccao	1.07.01.10-9 Prospecção Mineral	\N
789	095	a	mineral	1.07.01.10-9 Prospecção Mineral	\N
790	095	a	1.07.01.11-7	1.07.01.11-7 Sedimentologia	\N
791	095	a	sedimentologia	1.07.01.11-7 Sedimentologia	\N
792	095	a	1.07.01.12-5	1.07.01.12-5 Paleontologia Estratigráfica	\N
793	095	a	paleontologia	1.07.01.12-5 Paleontologia Estratigráfica	\N
794	095	a	estratigrafica	1.07.01.12-5 Paleontologia Estratigráfica	\N
795	095	a	1.07.01.13-3	1.07.01.13-3 Estratigrafia	\N
796	095	a	estratigrafia	1.07.01.13-3 Estratigrafia	\N
797	095	a	1.07.01.14-1	1.07.01.14-1 Geologia Ambiental	\N
798	095	a	geologia	1.07.01.14-1 Geologia Ambiental	\N
799	095	a	ambiental	1.07.01.14-1 Geologia Ambiental	\N
800	095	a	1.07.02.00-8	1.07.02.00-8 Geofísica	\N
801	095	a	geofisica	1.07.02.00-8 Geofísica	\N
802	095	a	1.07.02.01-6	1.07.02.01-6 Geomagnetismo	\N
803	095	a	geomagnetismo	1.07.02.01-6 Geomagnetismo	\N
804	095	a	1.07.02.02-4	1.07.02.02-4 Sismologia	\N
805	095	a	sismologia	1.07.02.02-4 Sismologia	\N
806	095	a	1.07.02.03-2	1.07.02.03-2 Geotermia e Fluxo Térmico	\N
807	095	a	geotermia	1.07.02.03-2 Geotermia e Fluxo Térmico	\N
808	095	a	fluxo	1.07.02.03-2 Geotermia e Fluxo Térmico	\N
809	095	a	termico	1.07.02.03-2 Geotermia e Fluxo Térmico	\N
810	095	a	1.07.02.04-0	1.07.02.04-0 Propriedades Físicas das Rochas	\N
811	095	a	propriedades	1.07.02.04-0 Propriedades Físicas das Rochas	\N
812	095	a	fisicas	1.07.02.04-0 Propriedades Físicas das Rochas	\N
813	095	a	das	1.07.02.04-0 Propriedades Físicas das Rochas	\N
814	095	a	rochas	1.07.02.04-0 Propriedades Físicas das Rochas	\N
815	095	a	1.07.02.05-9	1.07.02.05-9 Geofísica Nuclear	\N
816	095	a	geofisica	1.07.02.05-9 Geofísica Nuclear	\N
817	095	a	nuclear	1.07.02.05-9 Geofísica Nuclear	\N
818	095	a	1.07.02.06-7	1.07.02.06-7 Sensoriamento Remoto	\N
819	095	a	sensoriamento	1.07.02.06-7 Sensoriamento Remoto	\N
820	095	a	remoto	1.07.02.06-7 Sensoriamento Remoto	\N
821	095	a	1.07.02.07-5	1.07.02.07-5 Aeronomia	\N
822	095	a	aeronomia	1.07.02.07-5 Aeronomia	\N
823	095	a	1.07.02.08-3	1.07.02.08-3 Desenvolvimento de Instrumentação Geofísica	\N
824	095	a	desenvolvimento	1.07.02.08-3 Desenvolvimento de Instrumentação Geofísica	\N
825	095	a	de	1.07.02.08-3 Desenvolvimento de Instrumentação Geofísica	\N
826	095	a	instrumentacao	1.07.02.08-3 Desenvolvimento de Instrumentação Geofísica	\N
827	095	a	geofisica	1.07.02.08-3 Desenvolvimento de Instrumentação Geofísica	\N
828	095	a	1.07.02.09-1	1.07.02.09-1 Geofísica Aplicada	\N
829	095	a	geofisica	1.07.02.09-1 Geofísica Aplicada	\N
830	095	a	aplicada	1.07.02.09-1 Geofísica Aplicada	\N
831	095	a	1.07.02.10-5	1.07.02.10-5 Gravimetria	\N
832	095	a	gravimetria	1.07.02.10-5 Gravimetria	\N
833	095	a	1.07.03.00-4	1.07.03.00-4 Meteorologia	\N
834	095	a	meteorologia	1.07.03.00-4 Meteorologia	\N
835	095	a	1.07.03.01-2	1.07.03.01-2 Meteorologia Dinâmica	\N
836	095	a	meteorologia	1.07.03.01-2 Meteorologia Dinâmica	\N
837	095	a	dinamica	1.07.03.01-2 Meteorologia Dinâmica	\N
838	095	a	1.07.03.02-0	1.07.03.02-0 Meteorologia Sinótica	\N
839	095	a	meteorologia	1.07.03.02-0 Meteorologia Sinótica	\N
840	095	a	sinotica	1.07.03.02-0 Meteorologia Sinótica	\N
841	095	a	1.07.03.03-9	1.07.03.03-9 Meteorologia Física	\N
842	095	a	meteorologia	1.07.03.03-9 Meteorologia Física	\N
843	095	a	fisica	1.07.03.03-9 Meteorologia Física	\N
844	095	a	1.07.03.04-7	1.07.03.04-7 Química da Atmosfera	\N
845	095	a	quimica	1.07.03.04-7 Química da Atmosfera	\N
846	095	a	da	1.07.03.04-7 Química da Atmosfera	\N
847	095	a	atmosfera	1.07.03.04-7 Química da Atmosfera	\N
848	095	a	1.07.03.05-5	1.07.03.05-5 Instrumentação Meteorológica	\N
849	095	a	instrumentacao	1.07.03.05-5 Instrumentação Meteorológica	\N
850	095	a	meteorologica	1.07.03.05-5 Instrumentação Meteorológica	\N
851	095	a	1.07.03.06-3	1.07.03.06-3 Climatologia	\N
852	095	a	climatologia	1.07.03.06-3 Climatologia	\N
853	095	a	1.07.03.07-1	1.07.03.07-1 Micrometeorologia	\N
854	095	a	micrometeorologia	1.07.03.07-1 Micrometeorologia	\N
855	095	a	1.07.03.08-0	1.07.03.08-0 Sensoriamento Remoto da Atmosfera	\N
856	095	a	sensoriamento	1.07.03.08-0 Sensoriamento Remoto da Atmosfera	\N
857	095	a	remoto	1.07.03.08-0 Sensoriamento Remoto da Atmosfera	\N
858	095	a	da	1.07.03.08-0 Sensoriamento Remoto da Atmosfera	\N
859	095	a	atmosfera	1.07.03.08-0 Sensoriamento Remoto da Atmosfera	\N
860	095	a	1.07.03.09-8	1.07.03.09-8 Meteorologia Aplicada	\N
861	095	a	meteorologia	1.07.03.09-8 Meteorologia Aplicada	\N
862	095	a	aplicada	1.07.03.09-8 Meteorologia Aplicada	\N
863	095	a	1.07.04.00-0	1.07.04.00-0 Geodesia	\N
864	095	a	geodesia	1.07.04.00-0 Geodesia	\N
865	095	a	1.07.04.01-9	1.07.04.01-9 Geodesia Física	\N
866	095	a	geodesia	1.07.04.01-9 Geodesia Física	\N
867	095	a	fisica	1.07.04.01-9 Geodesia Física	\N
868	095	a	1.07.04.02-7	1.07.04.02-7 Geodesia Geométrica	\N
869	095	a	geodesia	1.07.04.02-7 Geodesia Geométrica	\N
870	095	a	geometrica	1.07.04.02-7 Geodesia Geométrica	\N
871	095	a	1.07.04.03-5	1.07.04.03-5 Geodesia Celeste	\N
872	095	a	geodesia	1.07.04.03-5 Geodesia Celeste	\N
873	095	a	celeste	1.07.04.03-5 Geodesia Celeste	\N
874	095	a	1.07.04.04-3	1.07.04.04-3 Fotogrametria	\N
875	095	a	fotogrametria	1.07.04.04-3 Fotogrametria	\N
876	095	a	1.07.04.05-1	1.07.04.05-1 Cartografia Básica	\N
877	095	a	cartografia	1.07.04.05-1 Cartografia Básica	\N
878	095	a	basica	1.07.04.05-1 Cartografia Básica	\N
879	095	a	1.07.05.00-7	1.07.05.00-7 Geografia Física	\N
880	095	a	geografia	1.07.05.00-7 Geografia Física	\N
881	095	a	fisica	1.07.05.00-7 Geografia Física	\N
882	095	a	1.07.05.01-5	1.07.05.01-5 Geomorfologia	\N
883	095	a	geomorfologia	1.07.05.01-5 Geomorfologia	\N
884	095	a	1.07.05.02-3	1.07.05.02-3 Climatologia Geográfica	\N
885	095	a	climatologia	1.07.05.02-3 Climatologia Geográfica	\N
886	095	a	geografica	1.07.05.02-3 Climatologia Geográfica	\N
887	095	a	1.07.05.03-1	1.07.05.03-1 Pedologia	\N
888	095	a	pedologia	1.07.05.03-1 Pedologia	\N
889	095	a	1.07.05.04-0	1.07.05.04-0 Hidrogeografia	\N
890	095	a	hidrogeografia	1.07.05.04-0 Hidrogeografia	\N
891	095	a	1.07.05.05-8	1.07.05.05-8 Geoecologia	\N
892	095	a	geoecologia	1.07.05.05-8 Geoecologia	\N
893	095	a	1.07.05.06-6	1.07.05.06-6 Fotogeografia (Físico-Ecológica)	\N
894	095	a	fotogeografia	1.07.05.06-6 Fotogeografia (Físico-Ecológica)	\N
895	095	a	(fisico-ecologica)	1.07.05.06-6 Fotogeografia (Físico-Ecológica)	\N
896	095	a	1.07.05.07-4	1.07.05.07-4 Geocartografia	\N
897	095	a	geocartografia	1.07.05.07-4 Geocartografia	\N
898	095	a	1.08.00.00-0	1.08.00.00-0 Oceanografia	\N
899	095	a	oceanografia	1.08.00.00-0 Oceanografia	\N
900	095	a	1.08.01.00-6	1.08.01.00-6 Oceanografia Biológica	\N
901	095	a	oceanografia	1.08.01.00-6 Oceanografia Biológica	\N
902	095	a	biologica	1.08.01.00-6 Oceanografia Biológica	\N
903	095	a	1.08.01.01-4	1.08.01.01-4 Interação entre os Organismos Marinhos e os Parâmetros Ambientais	\N
904	095	a	interacao	1.08.01.01-4 Interação entre os Organismos Marinhos e os Parâmetros Ambientais	\N
905	095	a	entre	1.08.01.01-4 Interação entre os Organismos Marinhos e os Parâmetros Ambientais	\N
906	095	a	os	1.08.01.01-4 Interação entre os Organismos Marinhos e os Parâmetros Ambientais	\N
907	095	a	organismos	1.08.01.01-4 Interação entre os Organismos Marinhos e os Parâmetros Ambientais	\N
908	095	a	marinhos	1.08.01.01-4 Interação entre os Organismos Marinhos e os Parâmetros Ambientais	\N
909	095	a	os	1.08.01.01-4 Interação entre os Organismos Marinhos e os Parâmetros Ambientais	\N
910	095	a	parametros	1.08.01.01-4 Interação entre os Organismos Marinhos e os Parâmetros Ambientais	\N
911	095	a	ambientais	1.08.01.01-4 Interação entre os Organismos Marinhos e os Parâmetros Ambientais	\N
912	095	a	1.08.02.00-2	1.08.02.00-2 Oceanografia Física	\N
913	095	a	oceanografia	1.08.02.00-2 Oceanografia Física	\N
914	095	a	fisica	1.08.02.00-2 Oceanografia Física	\N
915	095	a	1.08.02.01-0	1.08.02.01-0 Variáveis Físicas da Água do Mar	\N
916	095	a	variaveis	1.08.02.01-0 Variáveis Físicas da Água do Mar	\N
917	095	a	fisicas	1.08.02.01-0 Variáveis Físicas da Água do Mar	\N
918	095	a	da	1.08.02.01-0 Variáveis Físicas da Água do Mar	\N
919	095	a	agua	1.08.02.01-0 Variáveis Físicas da Água do Mar	\N
920	095	a	do	1.08.02.01-0 Variáveis Físicas da Água do Mar	\N
921	095	a	mar	1.08.02.01-0 Variáveis Físicas da Água do Mar	\N
922	095	a	1.08.02.02-9	1.08.02.02-9 Movimento da Água do Mar	\N
923	095	a	movimento	1.08.02.02-9 Movimento da Água do Mar	\N
924	095	a	da	1.08.02.02-9 Movimento da Água do Mar	\N
925	095	a	agua	1.08.02.02-9 Movimento da Água do Mar	\N
926	095	a	do	1.08.02.02-9 Movimento da Água do Mar	\N
927	095	a	mar	1.08.02.02-9 Movimento da Água do Mar	\N
928	095	a	1.08.02.03-7	1.08.02.03-7 Origem das Massas de Água	\N
929	095	a	origem	1.08.02.03-7 Origem das Massas de Água	\N
930	095	a	das	1.08.02.03-7 Origem das Massas de Água	\N
931	095	a	massas	1.08.02.03-7 Origem das Massas de Água	\N
932	095	a	de	1.08.02.03-7 Origem das Massas de Água	\N
933	095	a	agua	1.08.02.03-7 Origem das Massas de Água	\N
934	095	a	1.08.02.04-5	1.08.02.04-5 Interação do Oceano com o Leito do Mar	\N
935	095	a	interacao	1.08.02.04-5 Interação do Oceano com o Leito do Mar	\N
936	095	a	do	1.08.02.04-5 Interação do Oceano com o Leito do Mar	\N
937	095	a	oceano	1.08.02.04-5 Interação do Oceano com o Leito do Mar	\N
938	095	a	com	1.08.02.04-5 Interação do Oceano com o Leito do Mar	\N
939	095	a	leito	1.08.02.04-5 Interação do Oceano com o Leito do Mar	\N
940	095	a	do	1.08.02.04-5 Interação do Oceano com o Leito do Mar	\N
941	095	a	mar	1.08.02.04-5 Interação do Oceano com o Leito do Mar	\N
942	095	a	1.08.02.05-3	1.08.02.05-3 Interação do Oceano com a Atmosfera	\N
943	095	a	interacao	1.08.02.05-3 Interação do Oceano com a Atmosfera	\N
944	095	a	do	1.08.02.05-3 Interação do Oceano com a Atmosfera	\N
945	095	a	oceano	1.08.02.05-3 Interação do Oceano com a Atmosfera	\N
946	095	a	com	1.08.02.05-3 Interação do Oceano com a Atmosfera	\N
947	095	a	atmosfera	1.08.02.05-3 Interação do Oceano com a Atmosfera	\N
948	095	a	1.08.03.00-9	1.08.03.00-9 Oceanografia Química	\N
949	095	a	oceanografia	1.08.03.00-9 Oceanografia Química	\N
950	095	a	quimica	1.08.03.00-9 Oceanografia Química	\N
951	095	a	1.08.03.01-7	1.08.03.01-7 Propriedades Químicas da Água do Mar	\N
952	095	a	propriedades	1.08.03.01-7 Propriedades Químicas da Água do Mar	\N
953	095	a	quimicas	1.08.03.01-7 Propriedades Químicas da Água do Mar	\N
954	095	a	da	1.08.03.01-7 Propriedades Químicas da Água do Mar	\N
955	095	a	agua	1.08.03.01-7 Propriedades Químicas da Água do Mar	\N
956	095	a	do	1.08.03.01-7 Propriedades Químicas da Água do Mar	\N
957	095	a	mar	1.08.03.01-7 Propriedades Químicas da Água do Mar	\N
958	095	a	1.08.03.02-5	1.08.03.02-5 Interações Químico-Biológicas/Geológicas das Substâncias Químicas da Água do Mar	\N
959	095	a	interacoes	1.08.03.02-5 Interações Químico-Biológicas/Geológicas das Substâncias Químicas da Água do Mar	\N
960	095	a	quimico-biologicas/geologicas	1.08.03.02-5 Interações Químico-Biológicas/Geológicas das Substâncias Químicas da Água do Mar	\N
961	095	a	das	1.08.03.02-5 Interações Químico-Biológicas/Geológicas das Substâncias Químicas da Água do Mar	\N
962	095	a	substancias	1.08.03.02-5 Interações Químico-Biológicas/Geológicas das Substâncias Químicas da Água do Mar	\N
963	095	a	quimicas	1.08.03.02-5 Interações Químico-Biológicas/Geológicas das Substâncias Químicas da Água do Mar	\N
964	095	a	da	1.08.03.02-5 Interações Químico-Biológicas/Geológicas das Substâncias Químicas da Água do Mar	\N
965	095	a	agua	1.08.03.02-5 Interações Químico-Biológicas/Geológicas das Substâncias Químicas da Água do Mar	\N
966	095	a	do	1.08.03.02-5 Interações Químico-Biológicas/Geológicas das Substâncias Químicas da Água do Mar	\N
967	095	a	mar	1.08.03.02-5 Interações Químico-Biológicas/Geológicas das Substâncias Químicas da Água do Mar	\N
968	095	a	1.08.04.00-5	1.08.04.00-5 Oceanografia Geológica	\N
969	095	a	oceanografia	1.08.04.00-5 Oceanografia Geológica	\N
970	095	a	geologica	1.08.04.00-5 Oceanografia Geológica	\N
971	095	a	1.08.04.01-3	1.08.04.01-3 Geomorfologia Submarina	\N
972	095	a	geomorfologia	1.08.04.01-3 Geomorfologia Submarina	\N
973	095	a	submarina	1.08.04.01-3 Geomorfologia Submarina	\N
974	095	a	1.08.04.02-1	1.08.04.02-1 Sedimentologia Marinha	\N
975	095	a	sedimentologia	1.08.04.02-1 Sedimentologia Marinha	\N
976	095	a	marinha	1.08.04.02-1 Sedimentologia Marinha	\N
977	095	a	1.08.04.03-0	1.08.04.03-0 Geofísica Marinha	\N
978	095	a	geofisica	1.08.04.03-0 Geofísica Marinha	\N
979	095	a	marinha	1.08.04.03-0 Geofísica Marinha	\N
980	095	a	2.00.00.00-6	2.00.00.00-6 Ciências Biológicas	\N
981	095	a	ciencias	2.00.00.00-6 Ciências Biológicas	\N
982	095	a	biologicas	2.00.00.00-6 Ciências Biológicas	\N
983	095	a	2.01.00.00-0	2.01.00.00-0 Biologia Geral	\N
984	095	a	biologia	2.01.00.00-0 Biologia Geral	\N
985	095	a	geral	2.01.00.00-0 Biologia Geral	\N
986	095	a	2.02.00.00-5	2.02.00.00-5 Genética	\N
987	095	a	genetica	2.02.00.00-5 Genética	\N
988	095	a	2.02.01.00-1	2.02.01.00-1 Genética Quantitativa	\N
989	095	a	genetica	2.02.01.00-1 Genética Quantitativa	\N
990	095	a	quantitativa	2.02.01.00-1 Genética Quantitativa	\N
991	095	a	2.02.02.00-8	2.02.02.00-8 Genética Molecular e de Microorganismos	\N
992	095	a	genetica	2.02.02.00-8 Genética Molecular e de Microorganismos	\N
993	095	a	molecular	2.02.02.00-8 Genética Molecular e de Microorganismos	\N
994	095	a	de	2.02.02.00-8 Genética Molecular e de Microorganismos	\N
995	095	a	microorganismos	2.02.02.00-8 Genética Molecular e de Microorganismos	\N
996	095	a	2.02.03.00-4	2.02.03.00-4 Genética Vegetal	\N
997	095	a	genetica	2.02.03.00-4 Genética Vegetal	\N
998	095	a	vegetal	2.02.03.00-4 Genética Vegetal	\N
999	095	a	2.02.04.00-0	2.02.04.00-0 Genética Animal	\N
1000	095	a	genetica	2.02.04.00-0 Genética Animal	\N
1001	095	a	animal	2.02.04.00-0 Genética Animal	\N
1002	095	a	2.02.05.00-7	2.02.05.00-7 Genética Humana e Médica	\N
1003	095	a	genetica	2.02.05.00-7 Genética Humana e Médica	\N
1004	095	a	humana	2.02.05.00-7 Genética Humana e Médica	\N
1005	095	a	medica	2.02.05.00-7 Genética Humana e Médica	\N
1006	095	a	2.02.06.00-3	2.02.06.00-3 Mutagênese	\N
1007	095	a	mutagenese	2.02.06.00-3 Mutagênese	\N
1008	095	a	2.03.00.00-0	2.03.00.00-0 Botânica	\N
1009	095	a	botanica	2.03.00.00-0 Botânica	\N
1010	095	a	2.03.01.00-6	2.03.01.00-6 Paleobotânica	\N
1011	095	a	paleobotanica	2.03.01.00-6 Paleobotânica	\N
1012	095	a	2.03.02.00-2	2.03.02.00-2 Morfologia Vegetal	\N
1013	095	a	morfologia	2.03.02.00-2 Morfologia Vegetal	\N
1014	095	a	vegetal	2.03.02.00-2 Morfologia Vegetal	\N
1015	095	a	2.03.02.01-0	2.03.02.01-0 Morfologia Externa	\N
1016	095	a	morfologia	2.03.02.01-0 Morfologia Externa	\N
1017	095	a	externa	2.03.02.01-0 Morfologia Externa	\N
1018	095	a	2.03.02.02-9	2.03.02.02-9 Citologia Vegetal	\N
1019	095	a	citologia	2.03.02.02-9 Citologia Vegetal	\N
1020	095	a	vegetal	2.03.02.02-9 Citologia Vegetal	\N
1021	095	a	2.03.02.03-7	2.03.02.03-7 Anatomia Vegetal	\N
1022	095	a	anatomia	2.03.02.03-7 Anatomia Vegetal	\N
1023	095	a	vegetal	2.03.02.03-7 Anatomia Vegetal	\N
1024	095	a	2.03.02.04-5	2.03.02.04-5 Palinologia	\N
1025	095	a	palinologia	2.03.02.04-5 Palinologia	\N
1026	095	a	2.03.03.00-9	2.03.03.00-9 Fisiologia Vegetal	\N
1027	095	a	fisiologia	2.03.03.00-9 Fisiologia Vegetal	\N
1028	095	a	vegetal	2.03.03.00-9 Fisiologia Vegetal	\N
1029	095	a	2.03.03.01-7	2.03.03.01-7 Nutrição e Crescimento Vegetal	\N
1030	095	a	nutricao	2.03.03.01-7 Nutrição e Crescimento Vegetal	\N
1031	095	a	crescimento	2.03.03.01-7 Nutrição e Crescimento Vegetal	\N
1032	095	a	vegetal	2.03.03.01-7 Nutrição e Crescimento Vegetal	\N
1033	095	a	2.03.03.02-5	2.03.03.02-5 Reprodução Vegetal	\N
1034	095	a	reproducao	2.03.03.02-5 Reprodução Vegetal	\N
1035	095	a	vegetal	2.03.03.02-5 Reprodução Vegetal	\N
1036	095	a	2.03.03.03-3	2.03.03.03-3 Ecofisiologia Vegetal	\N
1037	095	a	ecofisiologia	2.03.03.03-3 Ecofisiologia Vegetal	\N
1038	095	a	vegetal	2.03.03.03-3 Ecofisiologia Vegetal	\N
1039	095	a	2.03.04.00-5	2.03.04.00-5 Taxonomia Vegetal	\N
1040	095	a	taxonomia	2.03.04.00-5 Taxonomia Vegetal	\N
1041	095	a	vegetal	2.03.04.00-5 Taxonomia Vegetal	\N
1042	095	a	2.03.04.01-3	2.03.04.01-3 Taxonomia de Criptógamos	\N
1043	095	a	taxonomia	2.03.04.01-3 Taxonomia de Criptógamos	\N
1044	095	a	de	2.03.04.01-3 Taxonomia de Criptógamos	\N
1045	095	a	criptogamos	2.03.04.01-3 Taxonomia de Criptógamos	\N
1046	095	a	2.03.04.02-1	2.03.04.02-1 Taxonomia de Fanerógamos	\N
1047	095	a	taxonomia	2.03.04.02-1 Taxonomia de Fanerógamos	\N
1048	095	a	de	2.03.04.02-1 Taxonomia de Fanerógamos	\N
1049	095	a	fanerogamos	2.03.04.02-1 Taxonomia de Fanerógamos	\N
1050	095	a	2.03.05.00-1	2.03.05.00-1 Fitogeografia	\N
1051	095	a	fitogeografia	2.03.05.00-1 Fitogeografia	\N
1052	095	a	2.03.06.00-8	2.03.06.00-8 Botânica Aplicada	\N
1053	095	a	botanica	2.03.06.00-8 Botânica Aplicada	\N
1054	095	a	aplicada	2.03.06.00-8 Botânica Aplicada	\N
1055	095	a	2.04.00.00-4	2.04.00.00-4 Zoologia	\N
1056	095	a	zoologia	2.04.00.00-4 Zoologia	\N
1057	095	a	2.04.01.00-0	2.04.01.00-0 Paleozoologia	\N
1058	095	a	paleozoologia	2.04.01.00-0 Paleozoologia	\N
1059	095	a	2.04.02.00-7	2.04.02.00-7 Morfologia dos Grupos Recentes	\N
1060	095	a	morfologia	2.04.02.00-7 Morfologia dos Grupos Recentes	\N
1061	095	a	dos	2.04.02.00-7 Morfologia dos Grupos Recentes	\N
1062	095	a	grupos	2.04.02.00-7 Morfologia dos Grupos Recentes	\N
1063	095	a	recentes	2.04.02.00-7 Morfologia dos Grupos Recentes	\N
1064	095	a	2.04.03.00-3	2.04.03.00-3 Fisiologia dos Grupos Recentes	\N
1065	095	a	fisiologia	2.04.03.00-3 Fisiologia dos Grupos Recentes	\N
1066	095	a	dos	2.04.03.00-3 Fisiologia dos Grupos Recentes	\N
1067	095	a	grupos	2.04.03.00-3 Fisiologia dos Grupos Recentes	\N
1068	095	a	recentes	2.04.03.00-3 Fisiologia dos Grupos Recentes	\N
1069	095	a	2.04.04.00-0	2.04.04.00-0 Comportamento Animal	\N
1070	095	a	comportamento	2.04.04.00-0 Comportamento Animal	\N
1071	095	a	animal	2.04.04.00-0 Comportamento Animal	\N
1072	095	a	2.04.05.00-6	2.04.05.00-6 Taxonomia dos Grupos Recentes	\N
1073	095	a	taxonomia	2.04.05.00-6 Taxonomia dos Grupos Recentes	\N
1074	095	a	dos	2.04.05.00-6 Taxonomia dos Grupos Recentes	\N
1075	095	a	grupos	2.04.05.00-6 Taxonomia dos Grupos Recentes	\N
1076	095	a	recentes	2.04.05.00-6 Taxonomia dos Grupos Recentes	\N
1077	095	a	2.04.06.00-2	2.04.06.00-2 Zoologia Aplicada	\N
1078	095	a	zoologia	2.04.06.00-2 Zoologia Aplicada	\N
1079	095	a	aplicada	2.04.06.00-2 Zoologia Aplicada	\N
1080	095	a	2.04.06.01-0	2.04.06.01-0 Conservação das Espécies Animais	\N
1081	095	a	conservacao	2.04.06.01-0 Conservação das Espécies Animais	\N
1082	095	a	das	2.04.06.01-0 Conservação das Espécies Animais	\N
1083	095	a	especies	2.04.06.01-0 Conservação das Espécies Animais	\N
1084	095	a	animais	2.04.06.01-0 Conservação das Espécies Animais	\N
1085	095	a	2.04.06.02-9	2.04.06.02-9 Utilização dos Animais	\N
1086	095	a	utilizacao	2.04.06.02-9 Utilização dos Animais	\N
1087	095	a	dos	2.04.06.02-9 Utilização dos Animais	\N
1088	095	a	animais	2.04.06.02-9 Utilização dos Animais	\N
1089	095	a	2.04.06.03-7	2.04.06.03-7 Controle Populacional de Animais	\N
1090	095	a	controle	2.04.06.03-7 Controle Populacional de Animais	\N
1091	095	a	populacional	2.04.06.03-7 Controle Populacional de Animais	\N
1092	095	a	de	2.04.06.03-7 Controle Populacional de Animais	\N
1093	095	a	animais	2.04.06.03-7 Controle Populacional de Animais	\N
1094	095	a	2.05.00.00-9	2.05.00.00-9 Ecologia	\N
1095	095	a	ecologia	2.05.00.00-9 Ecologia	\N
1096	095	a	2.05.01.00-5	2.05.01.00-5 Ecologia Teórica	\N
1097	095	a	ecologia	2.05.01.00-5 Ecologia Teórica	\N
1098	095	a	teorica	2.05.01.00-5 Ecologia Teórica	\N
1099	095	a	2.05.02.00-1	2.05.02.00-1 Ecologia de Ecossistemas	\N
1100	095	a	ecologia	2.05.02.00-1 Ecologia de Ecossistemas	\N
1101	095	a	de	2.05.02.00-1 Ecologia de Ecossistemas	\N
1102	095	a	ecossistemas	2.05.02.00-1 Ecologia de Ecossistemas	\N
1103	095	a	2.05.03.00-8	2.05.03.00-8 Ecologia Aplicada	\N
1104	095	a	ecologia	2.05.03.00-8 Ecologia Aplicada	\N
1105	095	a	aplicada	2.05.03.00-8 Ecologia Aplicada	\N
1106	095	a	2.06.00.00-3	2.06.00.00-3 Morfologia	\N
1107	095	a	morfologia	2.06.00.00-3 Morfologia	\N
1108	095	a	2.06.01.00-0	2.06.01.00-0 Citologia e Biologia Celular	\N
1109	095	a	citologia	2.06.01.00-0 Citologia e Biologia Celular	\N
1110	095	a	biologia	2.06.01.00-0 Citologia e Biologia Celular	\N
1111	095	a	celular	2.06.01.00-0 Citologia e Biologia Celular	\N
1112	095	a	2.06.02.00-6	2.06.02.00-6 Embriologia	\N
1113	095	a	embriologia	2.06.02.00-6 Embriologia	\N
1114	095	a	2.06.03.00-2	2.06.03.00-2 Histologia	\N
1115	095	a	histologia	2.06.03.00-2 Histologia	\N
1116	095	a	2.06.04.00-9	2.06.04.00-9 Anatomia	\N
1117	095	a	anatomia	2.06.04.00-9 Anatomia	\N
1118	095	a	2.06.04.01-7	2.06.04.01-7 Anatomia Humana	\N
1119	095	a	anatomia	2.06.04.01-7 Anatomia Humana	\N
1120	095	a	humana	2.06.04.01-7 Anatomia Humana	\N
1121	095	a	2.07.00.00-8	2.07.00.00-8 Fisiologia	\N
1122	095	a	fisiologia	2.07.00.00-8 Fisiologia	\N
1123	095	a	2.07.01.00-4	2.07.01.00-4 Fisiologia Geral	\N
1124	095	a	fisiologia	2.07.01.00-4 Fisiologia Geral	\N
1125	095	a	geral	2.07.01.00-4 Fisiologia Geral	\N
1126	095	a	2.07.02.00-0	2.07.02.00-0 Fisiologia de Órgaos e Sistemas	\N
1127	095	a	fisiologia	2.07.02.00-0 Fisiologia de Órgaos e Sistemas	\N
1128	095	a	de	2.07.02.00-0 Fisiologia de Órgaos e Sistemas	\N
1129	095	a	orgaos	2.07.02.00-0 Fisiologia de Órgaos e Sistemas	\N
1130	095	a	sistemas	2.07.02.00-0 Fisiologia de Órgaos e Sistemas	\N
1131	095	a	2.07.02.01-9	2.07.02.01-9 Neurofisiologia	\N
1132	095	a	neurofisiologia	2.07.02.01-9 Neurofisiologia	\N
1133	095	a	2.07.02.02-7	2.07.02.02-7 Fisiologia Cardiovascular	\N
1134	095	a	fisiologia	2.07.02.02-7 Fisiologia Cardiovascular	\N
1135	095	a	cardiovascular	2.07.02.02-7 Fisiologia Cardiovascular	\N
1136	095	a	2.07.02.03-5	2.07.02.03-5 Fisiologia da Respiração	\N
1137	095	a	fisiologia	2.07.02.03-5 Fisiologia da Respiração	\N
1138	095	a	da	2.07.02.03-5 Fisiologia da Respiração	\N
1139	095	a	respiracao	2.07.02.03-5 Fisiologia da Respiração	\N
1140	095	a	2.07.02.04-3	2.07.02.04-3 Fisiologia Renal	\N
1141	095	a	fisiologia	2.07.02.04-3 Fisiologia Renal	\N
1142	095	a	renal	2.07.02.04-3 Fisiologia Renal	\N
1143	095	a	2.07.02.05-1	2.07.02.05-1 Fisiologia Endocrina	\N
1144	095	a	fisiologia	2.07.02.05-1 Fisiologia Endocrina	\N
1145	095	a	endocrina	2.07.02.05-1 Fisiologia Endocrina	\N
1146	095	a	2.07.02.06-0	2.07.02.06-0 Fisiologia da Digestão	\N
1147	095	a	fisiologia	2.07.02.06-0 Fisiologia da Digestão	\N
1148	095	a	da	2.07.02.06-0 Fisiologia da Digestão	\N
1149	095	a	digestao	2.07.02.06-0 Fisiologia da Digestão	\N
1150	095	a	2.07.02.07-8	2.07.02.07-8 Cinesiologia	\N
1151	095	a	cinesiologia	2.07.02.07-8 Cinesiologia	\N
1152	095	a	2.07.03.00-7	2.07.03.00-7 Fisiologia do Esforço	\N
1153	095	a	fisiologia	2.07.03.00-7 Fisiologia do Esforço	\N
1154	095	a	do	2.07.03.00-7 Fisiologia do Esforço	\N
1155	095	a	esforco	2.07.03.00-7 Fisiologia do Esforço	\N
1156	095	a	2.07.04.00-3	2.07.04.00-3 Fisiologia Comparada	\N
1157	095	a	fisiologia	2.07.04.00-3 Fisiologia Comparada	\N
1158	095	a	comparada	2.07.04.00-3 Fisiologia Comparada	\N
1159	095	a	2.08.00.00-2	2.08.00.00-2 Bioquímica	\N
1160	095	a	bioquimica	2.08.00.00-2 Bioquímica	\N
1161	095	a	2.08.01.00-9	2.08.01.00-9 Química de Macromoléculas	\N
1162	095	a	quimica	2.08.01.00-9 Química de Macromoléculas	\N
1163	095	a	de	2.08.01.00-9 Química de Macromoléculas	\N
1164	095	a	macromoleculas	2.08.01.00-9 Química de Macromoléculas	\N
1165	095	a	2.08.01.01-7	2.08.01.01-7 Proteínas	\N
1166	095	a	proteinas	2.08.01.01-7 Proteínas	\N
1167	095	a	2.08.01.02-5	2.08.01.02-5 Lipídeos	\N
1168	095	a	lipideos	2.08.01.02-5 Lipídeos	\N
1169	095	a	2.08.01.03-3	2.08.01.03-3 Glicídeos	\N
1170	095	a	glicideos	2.08.01.03-3 Glicídeos	\N
1171	095	a	2.08.02.00-5	2.08.02.00-5 Bioquímica dos Microorganismos	\N
1172	095	a	bioquimica	2.08.02.00-5 Bioquímica dos Microorganismos	\N
1173	095	a	dos	2.08.02.00-5 Bioquímica dos Microorganismos	\N
1174	095	a	microorganismos	2.08.02.00-5 Bioquímica dos Microorganismos	\N
1175	095	a	2.08.03.00-1	2.08.03.00-1 Metabolismo e Bioenergética	\N
1176	095	a	metabolismo	2.08.03.00-1 Metabolismo e Bioenergética	\N
1177	095	a	bioenergetica	2.08.03.00-1 Metabolismo e Bioenergética	\N
1178	095	a	2.08.04.00-8	2.08.04.00-8 Biologia Molecular	\N
1179	095	a	biologia	2.08.04.00-8 Biologia Molecular	\N
1180	095	a	molecular	2.08.04.00-8 Biologia Molecular	\N
1181	095	a	2.08.05.00-4	2.08.05.00-4 Enzimologia	\N
1182	095	a	enzimologia	2.08.05.00-4 Enzimologia	\N
1183	095	a	2.09.00.00-7	2.09.00.00-7 Biofísica	\N
1184	095	a	biofisica	2.09.00.00-7 Biofísica	\N
1185	095	a	2.09.01.00-3	2.09.01.00-3 Biofísica Molecular	\N
1186	095	a	biofisica	2.09.01.00-3 Biofísica Molecular	\N
1187	095	a	molecular	2.09.01.00-3 Biofísica Molecular	\N
1188	095	a	2.09.02.00-0	2.09.02.00-0 Biofísica Celular	\N
1189	095	a	biofisica	2.09.02.00-0 Biofísica Celular	\N
1190	095	a	celular	2.09.02.00-0 Biofísica Celular	\N
1191	095	a	2.09.03.00-6	2.09.03.00-6 Biofísica de Processos e Sistemas	\N
1192	095	a	biofisica	2.09.03.00-6 Biofísica de Processos e Sistemas	\N
1193	095	a	de	2.09.03.00-6 Biofísica de Processos e Sistemas	\N
1194	095	a	processos	2.09.03.00-6 Biofísica de Processos e Sistemas	\N
1195	095	a	sistemas	2.09.03.00-6 Biofísica de Processos e Sistemas	\N
1196	095	a	2.09.04.00-2	2.09.04.00-2 Radiologia e Fotobiologia	\N
1197	095	a	radiologia	2.09.04.00-2 Radiologia e Fotobiologia	\N
1198	095	a	fotobiologia	2.09.04.00-2 Radiologia e Fotobiologia	\N
1199	095	a	2.10.00.00-0	2.10.00.00-0 Farmacologia	\N
1200	095	a	farmacologia	2.10.00.00-0 Farmacologia	\N
1201	095	a	2.10.01.00-6	2.10.01.00-6 Farmacologia Geral	\N
1202	095	a	farmacologia	2.10.01.00-6 Farmacologia Geral	\N
1203	095	a	geral	2.10.01.00-6 Farmacologia Geral	\N
1204	095	a	2.10.01.01-4	2.10.01.01-4 Farmacocinética	\N
1205	095	a	farmacocinetica	2.10.01.01-4 Farmacocinética	\N
1206	095	a	2.10.01.02-2	2.10.01.02-2 Biodisponibilidade	\N
1207	095	a	biodisponibilidade	2.10.01.02-2 Biodisponibilidade	\N
1208	095	a	2.10.02.00-2	2.10.02.00-2 Farmacologia Autonômica	\N
1209	095	a	farmacologia	2.10.02.00-2 Farmacologia Autonômica	\N
1210	095	a	autonomica	2.10.02.00-2 Farmacologia Autonômica	\N
1211	095	a	2.10.03.00-9	2.10.03.00-9 Neuropsicofarmacologia	\N
1212	095	a	neuropsicofarmacologia	2.10.03.00-9 Neuropsicofarmacologia	\N
1213	095	a	2.10.04.00-5	2.10.04.00-5 Farmacologia Cardiorenal	\N
1214	095	a	farmacologia	2.10.04.00-5 Farmacologia Cardiorenal	\N
1215	095	a	cardiorenal	2.10.04.00-5 Farmacologia Cardiorenal	\N
1216	095	a	2.10.05.00-1	2.10.05.00-1 Farmacologia Bioquímica e Molecular	\N
1217	095	a	farmacologia	2.10.05.00-1 Farmacologia Bioquímica e Molecular	\N
1218	095	a	bioquimica	2.10.05.00-1 Farmacologia Bioquímica e Molecular	\N
1219	095	a	molecular	2.10.05.00-1 Farmacologia Bioquímica e Molecular	\N
1220	095	a	2.10.06.00-8	2.10.06.00-8 Etnofarmacologia	\N
1221	095	a	etnofarmacologia	2.10.06.00-8 Etnofarmacologia	\N
1222	095	a	2.10.07.00-4	2.10.07.00-4 Toxicologia	\N
1223	095	a	toxicologia	2.10.07.00-4 Toxicologia	\N
1224	095	a	2.10.08.00-0	2.10.08.00-0 Farmacologia Clínica	\N
1225	095	a	farmacologia	2.10.08.00-0 Farmacologia Clínica	\N
1226	095	a	clinica	2.10.08.00-0 Farmacologia Clínica	\N
1227	095	a	2.11.00.00-4	2.11.00.00-4 Imunologia	\N
1228	095	a	imunologia	2.11.00.00-4 Imunologia	\N
1229	095	a	2.11.01.00-0	2.11.01.00-0 Imunoquímica	\N
1230	095	a	imunoquimica	2.11.01.00-0 Imunoquímica	\N
1231	095	a	2.11.02.00-7	2.11.02.00-7 Imunologia Celular	\N
1232	095	a	imunologia	2.11.02.00-7 Imunologia Celular	\N
1233	095	a	celular	2.11.02.00-7 Imunologia Celular	\N
1234	095	a	2.11.03.00-3	2.11.03.00-3 Imunogenética	\N
1235	095	a	imunogenetica	2.11.03.00-3 Imunogenética	\N
1236	095	a	2.11.04.00-0	2.11.04.00-0 Imunologia Aplicada	\N
1237	095	a	imunologia	2.11.04.00-0 Imunologia Aplicada	\N
1238	095	a	aplicada	2.11.04.00-0 Imunologia Aplicada	\N
1239	095	a	2.12.00.00-9	2.12.00.00-9 Microbiologia	\N
1240	095	a	microbiologia	2.12.00.00-9 Microbiologia	\N
1241	095	a	2.12.01.00-5	2.12.01.00-5 Biologia e Fisiologia dos Microorganismos	\N
1242	095	a	biologia	2.12.01.00-5 Biologia e Fisiologia dos Microorganismos	\N
1243	095	a	fisiologia	2.12.01.00-5 Biologia e Fisiologia dos Microorganismos	\N
1244	095	a	dos	2.12.01.00-5 Biologia e Fisiologia dos Microorganismos	\N
1245	095	a	microorganismos	2.12.01.00-5 Biologia e Fisiologia dos Microorganismos	\N
1246	095	a	2.12.01.01-3	2.12.01.01-3 Virologia	\N
1247	095	a	virologia	2.12.01.01-3 Virologia	\N
1248	095	a	2.12.01.02-1	2.12.01.02-1 Bacterologia	\N
1249	095	a	bacterologia	2.12.01.02-1 Bacterologia	\N
1250	095	a	2.12.01.03-0	2.12.01.03-0 Micologia	\N
1251	095	a	micologia	2.12.01.03-0 Micologia	\N
1252	095	a	2.12.02.00-1	2.12.02.00-1 Microbiologia Aplicada	\N
1253	095	a	microbiologia	2.12.02.00-1 Microbiologia Aplicada	\N
1254	095	a	aplicada	2.12.02.00-1 Microbiologia Aplicada	\N
1255	095	a	2.12.02.01-0	2.12.02.01-0 Microbiologia Médica	\N
1256	095	a	microbiologia	2.12.02.01-0 Microbiologia Médica	\N
1257	095	a	medica	2.12.02.01-0 Microbiologia Médica	\N
1258	095	a	2.12.02.02-8	2.12.02.02-8 Microbiologia Industrial e de Fermentação	\N
1259	095	a	microbiologia	2.12.02.02-8 Microbiologia Industrial e de Fermentação	\N
1260	095	a	industrial	2.12.02.02-8 Microbiologia Industrial e de Fermentação	\N
1261	095	a	de	2.12.02.02-8 Microbiologia Industrial e de Fermentação	\N
1262	095	a	fermentacao	2.12.02.02-8 Microbiologia Industrial e de Fermentação	\N
1263	095	a	2.13.00.00-3	2.13.00.00-3 Parasitologia	\N
1264	095	a	parasitologia	2.13.00.00-3 Parasitologia	\N
1265	095	a	2.13.01.00-0	2.13.01.00-0 Protozoologia de Parasitos	\N
1266	095	a	protozoologia	2.13.01.00-0 Protozoologia de Parasitos	\N
1267	095	a	de	2.13.01.00-0 Protozoologia de Parasitos	\N
1268	095	a	parasitos	2.13.01.00-0 Protozoologia de Parasitos	\N
1269	095	a	2.13.01.01-8	2.13.01.01-8 Protozoologia Parasitária Humana	\N
1270	095	a	protozoologia	2.13.01.01-8 Protozoologia Parasitária Humana	\N
1271	095	a	parasitaria	2.13.01.01-8 Protozoologia Parasitária Humana	\N
1272	095	a	humana	2.13.01.01-8 Protozoologia Parasitária Humana	\N
1273	095	a	2.13.01.02-6	2.13.01.02-6 Protozoologia Parasitária Animal	\N
1274	095	a	protozoologia	2.13.01.02-6 Protozoologia Parasitária Animal	\N
1275	095	a	parasitaria	2.13.01.02-6 Protozoologia Parasitária Animal	\N
1276	095	a	animal	2.13.01.02-6 Protozoologia Parasitária Animal	\N
1277	095	a	2.13.02.00-6	2.13.02.00-6 Helmintologia de Parasitos	\N
1278	095	a	helmintologia	2.13.02.00-6 Helmintologia de Parasitos	\N
1279	095	a	de	2.13.02.00-6 Helmintologia de Parasitos	\N
1280	095	a	parasitos	2.13.02.00-6 Helmintologia de Parasitos	\N
1281	095	a	2.13.02.01-4	2.13.02.01-4 Helmintologia Humana	\N
1282	095	a	helmintologia	2.13.02.01-4 Helmintologia Humana	\N
1283	095	a	humana	2.13.02.01-4 Helmintologia Humana	\N
1284	095	a	2.13.02.02-2	2.13.02.02-2 Helmintologia Animal	\N
1285	095	a	helmintologia	2.13.02.02-2 Helmintologia Animal	\N
1286	095	a	animal	2.13.02.02-2 Helmintologia Animal	\N
1287	095	a	2.13.03.00-2	2.13.03.00-2 Entomologia e Malacologia de Parasitos e Vetores	\N
1288	095	a	entomologia	2.13.03.00-2 Entomologia e Malacologia de Parasitos e Vetores	\N
1289	095	a	malacologia	2.13.03.00-2 Entomologia e Malacologia de Parasitos e Vetores	\N
1290	095	a	de	2.13.03.00-2 Entomologia e Malacologia de Parasitos e Vetores	\N
1291	095	a	parasitos	2.13.03.00-2 Entomologia e Malacologia de Parasitos e Vetores	\N
1292	095	a	vetores	2.13.03.00-2 Entomologia e Malacologia de Parasitos e Vetores	\N
1293	095	a	3.00.00.00-9	3.00.00.00-9 Engenharias	\N
1294	095	a	engenharias	3.00.00.00-9 Engenharias	\N
1295	095	a	3.01.00.00-3	3.01.00.00-3 Engenharia Civil	\N
1296	095	a	engenharia	3.01.00.00-3 Engenharia Civil	\N
1297	095	a	civil	3.01.00.00-3 Engenharia Civil	\N
1298	095	a	3.01.01.00-0	3.01.01.00-0 Construção Civil	\N
1299	095	a	construcao	3.01.01.00-0 Construção Civil	\N
1300	095	a	civil	3.01.01.00-0 Construção Civil	\N
1301	095	a	3.01.01.01-8	3.01.01.01-8 Materiais e Componentes de Construção	\N
1302	095	a	materiais	3.01.01.01-8 Materiais e Componentes de Construção	\N
1303	095	a	componentes	3.01.01.01-8 Materiais e Componentes de Construção	\N
1304	095	a	de	3.01.01.01-8 Materiais e Componentes de Construção	\N
1305	095	a	construcao	3.01.01.01-8 Materiais e Componentes de Construção	\N
1306	095	a	3.01.01.02-6	3.01.01.02-6 Processos Construtivos	\N
1307	095	a	processos	3.01.01.02-6 Processos Construtivos	\N
1308	095	a	construtivos	3.01.01.02-6 Processos Construtivos	\N
1309	095	a	3.01.01.03-4	3.01.01.03-4 Instalações Prediais	\N
1310	095	a	instalacoes	3.01.01.03-4 Instalações Prediais	\N
1311	095	a	prediais	3.01.01.03-4 Instalações Prediais	\N
1312	095	a	3.01.02.00-6	3.01.02.00-6 Estruturas	\N
1313	095	a	estruturas	3.01.02.00-6 Estruturas	\N
1314	095	a	3.01.02.01-4	3.01.02.01-4 Estruturas de Concreto	\N
1315	095	a	estruturas	3.01.02.01-4 Estruturas de Concreto	\N
1316	095	a	de	3.01.02.01-4 Estruturas de Concreto	\N
1317	095	a	concreto	3.01.02.01-4 Estruturas de Concreto	\N
1318	095	a	3.01.02.02-2	3.01.02.02-2 Estruturas de Madeiras	\N
1319	095	a	estruturas	3.01.02.02-2 Estruturas de Madeiras	\N
1320	095	a	de	3.01.02.02-2 Estruturas de Madeiras	\N
1321	095	a	madeiras	3.01.02.02-2 Estruturas de Madeiras	\N
1322	095	a	3.01.02.03-0	3.01.02.03-0 Estruturas Metálicas	\N
1323	095	a	estruturas	3.01.02.03-0 Estruturas Metálicas	\N
1324	095	a	metalicas	3.01.02.03-0 Estruturas Metálicas	\N
1325	095	a	3.01.02.04-9	3.01.02.04-9 Mecânica das Estruturas	\N
1326	095	a	mecanica	3.01.02.04-9 Mecânica das Estruturas	\N
1327	095	a	das	3.01.02.04-9 Mecânica das Estruturas	\N
1328	095	a	estruturas	3.01.02.04-9 Mecânica das Estruturas	\N
1329	095	a	3.01.03.00-2	3.01.03.00-2 Geotécnica	\N
1330	095	a	geotecnica	3.01.03.00-2 Geotécnica	\N
1331	095	a	3.01.03.01-0	3.01.03.01-0 Fundações e Escavações	\N
1332	095	a	fundacoes	3.01.03.01-0 Fundações e Escavações	\N
1333	095	a	escavacoes	3.01.03.01-0 Fundações e Escavações	\N
1334	095	a	3.01.03.02-9	3.01.03.02-9 Mecânicas das Rochas	\N
1335	095	a	mecanicas	3.01.03.02-9 Mecânicas das Rochas	\N
1336	095	a	das	3.01.03.02-9 Mecânicas das Rochas	\N
1337	095	a	rochas	3.01.03.02-9 Mecânicas das Rochas	\N
1338	095	a	3.01.03.03-7	3.01.03.03-7 Mecânicas dos Solos	\N
1339	095	a	mecanicas	3.01.03.03-7 Mecânicas dos Solos	\N
1340	095	a	dos	3.01.03.03-7 Mecânicas dos Solos	\N
1341	095	a	solos	3.01.03.03-7 Mecânicas dos Solos	\N
1342	095	a	3.01.03.04-5	3.01.03.04-5 Obras de Terra e Enrocamento	\N
1343	095	a	obras	3.01.03.04-5 Obras de Terra e Enrocamento	\N
1344	095	a	de	3.01.03.04-5 Obras de Terra e Enrocamento	\N
1345	095	a	terra	3.01.03.04-5 Obras de Terra e Enrocamento	\N
1346	095	a	enrocamento	3.01.03.04-5 Obras de Terra e Enrocamento	\N
1347	095	a	3.01.03.05-3	3.01.03.05-3 Pavimentos	\N
1348	095	a	pavimentos	3.01.03.05-3 Pavimentos	\N
1349	095	a	3.01.04.00-9	3.01.04.00-9 Engenharia Hidráulica	\N
1350	095	a	engenharia	3.01.04.00-9 Engenharia Hidráulica	\N
1351	095	a	hidraulica	3.01.04.00-9 Engenharia Hidráulica	\N
1352	095	a	3.01.04.01-7	3.01.04.01-7 Hidráulica	\N
1353	095	a	hidraulica	3.01.04.01-7 Hidráulica	\N
1354	095	a	3.01.04.02-5	3.01.04.02-5 Hidrologia	\N
1355	095	a	hidrologia	3.01.04.02-5 Hidrologia	\N
1356	095	a	3.01.05.00-5	3.01.05.00-5 Infra-Estrutura de Transportes	\N
1357	095	a	infra-estrutura	3.01.05.00-5 Infra-Estrutura de Transportes	\N
1358	095	a	de	3.01.05.00-5 Infra-Estrutura de Transportes	\N
1359	095	a	transportes	3.01.05.00-5 Infra-Estrutura de Transportes	\N
1360	095	a	3.01.05.01-3	3.01.05.01-3 Aeroportos; Projeto e Construção	\N
1361	095	a	aeroportos;	3.01.05.01-3 Aeroportos; Projeto e Construção	\N
1362	095	a	projeto	3.01.05.01-3 Aeroportos; Projeto e Construção	\N
1363	095	a	construcao	3.01.05.01-3 Aeroportos; Projeto e Construção	\N
1364	095	a	3.01.05.02-1	3.01.05.02-1 Ferrovias; Projetos e Construção	\N
1365	095	a	ferrovias;	3.01.05.02-1 Ferrovias; Projetos e Construção	\N
1366	095	a	projetos	3.01.05.02-1 Ferrovias; Projetos e Construção	\N
1367	095	a	construcao	3.01.05.02-1 Ferrovias; Projetos e Construção	\N
1368	095	a	3.01.05.03-0	3.01.05.03-0 Portos e Vias Nevegáveis; Projeto e Construção	\N
1369	095	a	portos	3.01.05.03-0 Portos e Vias Nevegáveis; Projeto e Construção	\N
1370	095	a	vias	3.01.05.03-0 Portos e Vias Nevegáveis; Projeto e Construção	\N
1371	095	a	nevegaveis;	3.01.05.03-0 Portos e Vias Nevegáveis; Projeto e Construção	\N
1372	095	a	projeto	3.01.05.03-0 Portos e Vias Nevegáveis; Projeto e Construção	\N
1373	095	a	construcao	3.01.05.03-0 Portos e Vias Nevegáveis; Projeto e Construção	\N
1374	095	a	3.01.05.04-8	3.01.05.04-8 Rodovias; Projeto e Construção	\N
1375	095	a	rodovias;	3.01.05.04-8 Rodovias; Projeto e Construção	\N
1376	095	a	projeto	3.01.05.04-8 Rodovias; Projeto e Construção	\N
1377	095	a	construcao	3.01.05.04-8 Rodovias; Projeto e Construção	\N
1378	095	a	3.02.00.00-8	3.02.00.00-8 Engenharia de Minas	\N
1379	095	a	engenharia	3.02.00.00-8 Engenharia de Minas	\N
1380	095	a	de	3.02.00.00-8 Engenharia de Minas	\N
1381	095	a	minas	3.02.00.00-8 Engenharia de Minas	\N
1382	095	a	3.02.01.00-4	3.02.01.00-4 Pesquisa Mineral	\N
1383	095	a	pesquisa	3.02.01.00-4 Pesquisa Mineral	\N
1384	095	a	mineral	3.02.01.00-4 Pesquisa Mineral	\N
1385	095	a	3.02.01.01-2	3.02.01.01-2 Caracterização do Minério	\N
1386	095	a	caracterizacao	3.02.01.01-2 Caracterização do Minério	\N
1387	095	a	do	3.02.01.01-2 Caracterização do Minério	\N
1388	095	a	minerio	3.02.01.01-2 Caracterização do Minério	\N
1389	095	a	3.02.01.02-0	3.02.01.02-0 Dimensionamento de Jazidas	\N
1390	095	a	dimensionamento	3.02.01.02-0 Dimensionamento de Jazidas	\N
1391	095	a	de	3.02.01.02-0 Dimensionamento de Jazidas	\N
1392	095	a	jazidas	3.02.01.02-0 Dimensionamento de Jazidas	\N
1393	095	a	3.02.02.00-0	3.02.02.00-0 Lavra	\N
1394	095	a	lavra	3.02.02.00-0 Lavra	\N
1395	095	a	3.02.02.01-9	3.02.02.01-9 Lavra a Céu Aberto	\N
1396	095	a	lavra	3.02.02.01-9 Lavra a Céu Aberto	\N
1397	095	a	ceu	3.02.02.01-9 Lavra a Céu Aberto	\N
1398	095	a	aberto	3.02.02.01-9 Lavra a Céu Aberto	\N
1399	095	a	3.02.02.02-7	3.02.02.02-7 Lavra de Mina Subterrânea	\N
1400	095	a	lavra	3.02.02.02-7 Lavra de Mina Subterrânea	\N
1401	095	a	de	3.02.02.02-7 Lavra de Mina Subterrânea	\N
1402	095	a	mina	3.02.02.02-7 Lavra de Mina Subterrânea	\N
1403	095	a	subterranea	3.02.02.02-7 Lavra de Mina Subterrânea	\N
1404	095	a	3.02.02.03-5	3.02.02.03-5 Equipamentos de Lavra	\N
1405	095	a	equipamentos	3.02.02.03-5 Equipamentos de Lavra	\N
1406	095	a	de	3.02.02.03-5 Equipamentos de Lavra	\N
1407	095	a	lavra	3.02.02.03-5 Equipamentos de Lavra	\N
1408	095	a	3.02.03.00-7	3.02.03.00-7 Tratamento de Minérios	\N
1409	095	a	tratamento	3.02.03.00-7 Tratamento de Minérios	\N
1410	095	a	de	3.02.03.00-7 Tratamento de Minérios	\N
1411	095	a	minerios	3.02.03.00-7 Tratamento de Minérios	\N
1412	095	a	3.02.03.01-5	3.02.03.01-5 Métodos de Concentração e Enriquecimento de Minérios	\N
1413	095	a	metodos	3.02.03.01-5 Métodos de Concentração e Enriquecimento de Minérios	\N
1414	095	a	de	3.02.03.01-5 Métodos de Concentração e Enriquecimento de Minérios	\N
1415	095	a	concentracao	3.02.03.01-5 Métodos de Concentração e Enriquecimento de Minérios	\N
1416	095	a	enriquecimento	3.02.03.01-5 Métodos de Concentração e Enriquecimento de Minérios	\N
1417	095	a	de	3.02.03.01-5 Métodos de Concentração e Enriquecimento de Minérios	\N
1418	095	a	minerios	3.02.03.01-5 Métodos de Concentração e Enriquecimento de Minérios	\N
1419	095	a	3.02.03.02-3	3.02.03.02-3 Equipamentos de Beneficiamento de Minérios	\N
1420	095	a	equipamentos	3.02.03.02-3 Equipamentos de Beneficiamento de Minérios	\N
1421	095	a	de	3.02.03.02-3 Equipamentos de Beneficiamento de Minérios	\N
1422	095	a	beneficiamento	3.02.03.02-3 Equipamentos de Beneficiamento de Minérios	\N
1423	095	a	de	3.02.03.02-3 Equipamentos de Beneficiamento de Minérios	\N
1424	095	a	minerios	3.02.03.02-3 Equipamentos de Beneficiamento de Minérios	\N
1425	095	a	3.03.00.00-2	3.03.00.00-2 Engenharia de Materiais e Metalúrgica	\N
1426	095	a	engenharia	3.03.00.00-2 Engenharia de Materiais e Metalúrgica	\N
1427	095	a	de	3.03.00.00-2 Engenharia de Materiais e Metalúrgica	\N
1428	095	a	materiais	3.03.00.00-2 Engenharia de Materiais e Metalúrgica	\N
1429	095	a	metalurgica	3.03.00.00-2 Engenharia de Materiais e Metalúrgica	\N
1430	095	a	3.03.01.00-9	3.03.01.00-9 Instalações e Equipamentos Metalúrgicos	\N
1431	095	a	instalacoes	3.03.01.00-9 Instalações e Equipamentos Metalúrgicos	\N
1432	095	a	equipamentos	3.03.01.00-9 Instalações e Equipamentos Metalúrgicos	\N
1433	095	a	metalurgicos	3.03.01.00-9 Instalações e Equipamentos Metalúrgicos	\N
1434	095	a	3.03.01.01-7	3.03.01.01-7 Instalações Metalúrgicas	\N
1435	095	a	instalacoes	3.03.01.01-7 Instalações Metalúrgicas	\N
1436	095	a	metalurgicas	3.03.01.01-7 Instalações Metalúrgicas	\N
1437	095	a	3.03.01.02-5	3.03.01.02-5 Equipamentos Metalúrgicos	\N
1438	095	a	equipamentos	3.03.01.02-5 Equipamentos Metalúrgicos	\N
1439	095	a	metalurgicos	3.03.01.02-5 Equipamentos Metalúrgicos	\N
1440	095	a	3.03.02.00-5	3.03.02.00-5 Metalurgia Extrativa	\N
1441	095	a	metalurgia	3.03.02.00-5 Metalurgia Extrativa	\N
1442	095	a	extrativa	3.03.02.00-5 Metalurgia Extrativa	\N
1443	095	a	3.03.02.01-3	3.03.02.01-3 Aglomeração	\N
1444	095	a	aglomeracao	3.03.02.01-3 Aglomeração	\N
1445	095	a	3.03.02.02-1	3.03.02.02-1 Eletrometalurgia	\N
1446	095	a	eletrometalurgia	3.03.02.02-1 Eletrometalurgia	\N
1447	095	a	3.03.02.03-0	3.03.02.03-0 Hidrometalurgia	\N
1448	095	a	hidrometalurgia	3.03.02.03-0 Hidrometalurgia	\N
1449	095	a	3.03.02.04-8	3.03.02.04-8 Pirometalurgia	\N
1450	095	a	pirometalurgia	3.03.02.04-8 Pirometalurgia	\N
1451	095	a	3.03.02.05-6	3.03.02.05-6 Tratamento de Minérios	\N
1452	095	a	tratamento	3.03.02.05-6 Tratamento de Minérios	\N
1453	095	a	de	3.03.02.05-6 Tratamento de Minérios	\N
1454	095	a	minerios	3.03.02.05-6 Tratamento de Minérios	\N
1455	095	a	3.03.03.00-1	3.03.03.00-1 Metalurgia de Transformação	\N
1456	095	a	metalurgia	3.03.03.00-1 Metalurgia de Transformação	\N
1457	095	a	de	3.03.03.00-1 Metalurgia de Transformação	\N
1458	095	a	transformacao	3.03.03.00-1 Metalurgia de Transformação	\N
1459	095	a	3.03.03.01-0	3.03.03.01-0 Conformação Mecânica	\N
1460	095	a	conformacao	3.03.03.01-0 Conformação Mecânica	\N
1461	095	a	mecanica	3.03.03.01-0 Conformação Mecânica	\N
1462	095	a	3.03.03.02-8	3.03.03.02-8 Fundição	\N
1463	095	a	fundicao	3.03.03.02-8 Fundição	\N
1464	095	a	3.03.03.03-6	3.03.03.03-6 Metalurgia de Po	\N
1465	095	a	metalurgia	3.03.03.03-6 Metalurgia de Po	\N
1466	095	a	de	3.03.03.03-6 Metalurgia de Po	\N
1467	095	a	po	3.03.03.03-6 Metalurgia de Po	\N
1468	095	a	3.03.03.04-4	3.03.03.04-4 Recobrimentos	\N
1469	095	a	recobrimentos	3.03.03.04-4 Recobrimentos	\N
1470	095	a	3.03.03.05-2	3.03.03.05-2 Soldagem	\N
1471	095	a	soldagem	3.03.03.05-2 Soldagem	\N
1472	095	a	3.03.03.06-0	3.03.03.06-0 Tratamento Térmicos, Mecânicos e Químicos	\N
1473	095	a	tratamento	3.03.03.06-0 Tratamento Térmicos, Mecânicos e Químicos	\N
1474	095	a	termicos,	3.03.03.06-0 Tratamento Térmicos, Mecânicos e Químicos	\N
1475	095	a	mecanicos	3.03.03.06-0 Tratamento Térmicos, Mecânicos e Químicos	\N
1476	095	a	quimicos	3.03.03.06-0 Tratamento Térmicos, Mecânicos e Químicos	\N
1477	095	a	3.03.03.07-9	3.03.03.07-9 Usinagem	\N
1478	095	a	usinagem	3.03.03.07-9 Usinagem	\N
1479	095	a	3.03.04.00-8	3.03.04.00-8 Metalurgia Fisica	\N
1480	095	a	metalurgia	3.03.04.00-8 Metalurgia Fisica	\N
1481	095	a	fisica	3.03.04.00-8 Metalurgia Fisica	\N
1482	095	a	3.03.04.01-6	3.03.04.01-6 Estrutura dos Metais e Ligas	\N
1483	095	a	estrutura	3.03.04.01-6 Estrutura dos Metais e Ligas	\N
1484	095	a	dos	3.03.04.01-6 Estrutura dos Metais e Ligas	\N
1485	095	a	metais	3.03.04.01-6 Estrutura dos Metais e Ligas	\N
1486	095	a	ligas	3.03.04.01-6 Estrutura dos Metais e Ligas	\N
1487	095	a	3.03.04.02-4	3.03.04.02-4 Propriedades Físicas dos Metais e Ligas	\N
1488	095	a	propriedades	3.03.04.02-4 Propriedades Físicas dos Metais e Ligas	\N
1489	095	a	fisicas	3.03.04.02-4 Propriedades Físicas dos Metais e Ligas	\N
1490	095	a	dos	3.03.04.02-4 Propriedades Físicas dos Metais e Ligas	\N
1491	095	a	metais	3.03.04.02-4 Propriedades Físicas dos Metais e Ligas	\N
1492	095	a	ligas	3.03.04.02-4 Propriedades Físicas dos Metais e Ligas	\N
1493	095	a	3.03.04.03-2	3.03.04.03-2 Propriedades Mecânicas dos Metais e Ligas	\N
1494	095	a	propriedades	3.03.04.03-2 Propriedades Mecânicas dos Metais e Ligas	\N
1495	095	a	mecanicas	3.03.04.03-2 Propriedades Mecânicas dos Metais e Ligas	\N
1496	095	a	dos	3.03.04.03-2 Propriedades Mecânicas dos Metais e Ligas	\N
1497	095	a	metais	3.03.04.03-2 Propriedades Mecânicas dos Metais e Ligas	\N
1498	095	a	ligas	3.03.04.03-2 Propriedades Mecânicas dos Metais e Ligas	\N
1499	095	a	3.03.04.04-0	3.03.04.04-0 Transformação de Fases	\N
1500	095	a	transformacao	3.03.04.04-0 Transformação de Fases	\N
1501	095	a	de	3.03.04.04-0 Transformação de Fases	\N
1502	095	a	fases	3.03.04.04-0 Transformação de Fases	\N
1503	095	a	3.03.04.05-9	3.03.04.05-9 Corrosão	\N
1504	095	a	corrosao	3.03.04.05-9 Corrosão	\N
1505	095	a	3.03.05.00-4	3.03.05.00-4 Materiais não Metálicos	\N
1506	095	a	materiais	3.03.05.00-4 Materiais não Metálicos	\N
1507	095	a	nao	3.03.05.00-4 Materiais não Metálicos	\N
1508	095	a	metalicos	3.03.05.00-4 Materiais não Metálicos	\N
1509	095	a	3.03.05.01-2	3.03.05.01-2 Extração e Transformação de Materiais	\N
1510	095	a	extracao	3.03.05.01-2 Extração e Transformação de Materiais	\N
1511	095	a	transformacao	3.03.05.01-2 Extração e Transformação de Materiais	\N
1512	095	a	de	3.03.05.01-2 Extração e Transformação de Materiais	\N
1513	095	a	materiais	3.03.05.01-2 Extração e Transformação de Materiais	\N
1514	095	a	3.03.05.02-0	3.03.05.02-0 Cerâmicos	\N
1515	095	a	ceramicos	3.03.05.02-0 Cerâmicos	\N
1516	095	a	3.03.05.03-9	3.03.05.03-9 Materiais Conjugados não Metálicos	\N
1517	095	a	materiais	3.03.05.03-9 Materiais Conjugados não Metálicos	\N
1518	095	a	conjugados	3.03.05.03-9 Materiais Conjugados não Metálicos	\N
1519	095	a	nao	3.03.05.03-9 Materiais Conjugados não Metálicos	\N
1520	095	a	metalicos	3.03.05.03-9 Materiais Conjugados não Metálicos	\N
1521	095	a	3.03.05.04-7	3.03.05.04-7 Polímeros, Aplicações	\N
1522	095	a	polimeros,	3.03.05.04-7 Polímeros, Aplicações	\N
1523	095	a	aplicacoes	3.03.05.04-7 Polímeros, Aplicações	\N
1524	095	a	3.04.00.00-7	3.04.00.00-7 Engenharia Elétrica	\N
1525	095	a	engenharia	3.04.00.00-7 Engenharia Elétrica	\N
1526	095	a	eletrica	3.04.00.00-7 Engenharia Elétrica	\N
1527	095	a	3.04.01.00-3	3.04.01.00-3 Materiais Elétricos	\N
1528	095	a	materiais	3.04.01.00-3 Materiais Elétricos	\N
1529	095	a	eletricos	3.04.01.00-3 Materiais Elétricos	\N
1530	095	a	3.04.01.01-1	3.04.01.01-1 Materiais Condutores	\N
1531	095	a	materiais	3.04.01.01-1 Materiais Condutores	\N
1532	095	a	condutores	3.04.01.01-1 Materiais Condutores	\N
1533	095	a	3.04.01.02-0	3.04.01.02-0 Materiais e Componentes Semicondutores	\N
1534	095	a	materiais	3.04.01.02-0 Materiais e Componentes Semicondutores	\N
1535	095	a	componentes	3.04.01.02-0 Materiais e Componentes Semicondutores	\N
1536	095	a	semicondutores	3.04.01.02-0 Materiais e Componentes Semicondutores	\N
1537	095	a	3.04.01.03-8	3.04.01.03-8 Materiais e Dispositivos Supercondutores	\N
1538	095	a	materiais	3.04.01.03-8 Materiais e Dispositivos Supercondutores	\N
1539	095	a	dispositivos	3.04.01.03-8 Materiais e Dispositivos Supercondutores	\N
1540	095	a	supercondutores	3.04.01.03-8 Materiais e Dispositivos Supercondutores	\N
1541	095	a	3.04.01.04-6	3.04.01.04-6 Materiais Dielétricos, Piesoelétricos e Ferroelétricos	\N
1542	095	a	materiais	3.04.01.04-6 Materiais Dielétricos, Piesoelétricos e Ferroelétricos	\N
1543	095	a	dieletricos,	3.04.01.04-6 Materiais Dielétricos, Piesoelétricos e Ferroelétricos	\N
1544	095	a	piesoeletricos	3.04.01.04-6 Materiais Dielétricos, Piesoelétricos e Ferroelétricos	\N
1545	095	a	ferroeletricos	3.04.01.04-6 Materiais Dielétricos, Piesoelétricos e Ferroelétricos	\N
1546	095	a	3.04.01.05-4	3.04.01.05-4 Materiais e Componentes Eletroóticos e Magnetoóticos, Materiais Fotoelétricos	\N
1547	095	a	materiais	3.04.01.05-4 Materiais e Componentes Eletroóticos e Magnetoóticos, Materiais Fotoelétricos	\N
1548	095	a	componentes	3.04.01.05-4 Materiais e Componentes Eletroóticos e Magnetoóticos, Materiais Fotoelétricos	\N
1549	095	a	eletrooticos	3.04.01.05-4 Materiais e Componentes Eletroóticos e Magnetoóticos, Materiais Fotoelétricos	\N
1550	095	a	magnetooticos,	3.04.01.05-4 Materiais e Componentes Eletroóticos e Magnetoóticos, Materiais Fotoelétricos	\N
1551	095	a	materiais	3.04.01.05-4 Materiais e Componentes Eletroóticos e Magnetoóticos, Materiais Fotoelétricos	\N
1552	095	a	fotoeletricos	3.04.01.05-4 Materiais e Componentes Eletroóticos e Magnetoóticos, Materiais Fotoelétricos	\N
1553	095	a	3.04.01.06-2	3.04.01.06-2 Materiais e Dispositivos Magnéticos	\N
1554	095	a	materiais	3.04.01.06-2 Materiais e Dispositivos Magnéticos	\N
1555	095	a	dispositivos	3.04.01.06-2 Materiais e Dispositivos Magnéticos	\N
1556	095	a	magneticos	3.04.01.06-2 Materiais e Dispositivos Magnéticos	\N
1557	095	a	3.04.02.00-0	3.04.02.00-0 Medidas Elétricas, Magnéticas e Eletrônicas; Instrumentação	\N
1558	095	a	medidas	3.04.02.00-0 Medidas Elétricas, Magnéticas e Eletrônicas; Instrumentação	\N
1559	095	a	eletricas,	3.04.02.00-0 Medidas Elétricas, Magnéticas e Eletrônicas; Instrumentação	\N
1560	095	a	magneticas	3.04.02.00-0 Medidas Elétricas, Magnéticas e Eletrônicas; Instrumentação	\N
1561	095	a	eletronicas;	3.04.02.00-0 Medidas Elétricas, Magnéticas e Eletrônicas; Instrumentação	\N
1562	095	a	instrumentacao	3.04.02.00-0 Medidas Elétricas, Magnéticas e Eletrônicas; Instrumentação	\N
1563	095	a	3.04.02.01-8	3.04.02.01-8 Medidas Elétricas	\N
1564	095	a	medidas	3.04.02.01-8 Medidas Elétricas	\N
1565	095	a	eletricas	3.04.02.01-8 Medidas Elétricas	\N
1566	095	a	3.04.02.02-6	3.04.02.02-6 Medidas Magnéticas	\N
1567	095	a	medidas	3.04.02.02-6 Medidas Magnéticas	\N
1568	095	a	magneticas	3.04.02.02-6 Medidas Magnéticas	\N
1569	095	a	3.04.02.03-4	3.04.02.03-4 Instrumentação Eletromecânica	\N
1570	095	a	instrumentacao	3.04.02.03-4 Instrumentação Eletromecânica	\N
1571	095	a	eletromecanica	3.04.02.03-4 Instrumentação Eletromecânica	\N
1572	095	a	3.04.02.04-2	3.04.02.04-2 Instrumentação Eletrônica	\N
1573	095	a	instrumentacao	3.04.02.04-2 Instrumentação Eletrônica	\N
1574	095	a	eletronica	3.04.02.04-2 Instrumentação Eletrônica	\N
1575	095	a	3.04.02.05-0	3.04.02.05-0 Sistemas Eletrônicos de Medida e de Controle	\N
1576	095	a	sistemas	3.04.02.05-0 Sistemas Eletrônicos de Medida e de Controle	\N
1577	095	a	eletronicos	3.04.02.05-0 Sistemas Eletrônicos de Medida e de Controle	\N
1578	095	a	de	3.04.02.05-0 Sistemas Eletrônicos de Medida e de Controle	\N
1579	095	a	medida	3.04.02.05-0 Sistemas Eletrônicos de Medida e de Controle	\N
1580	095	a	de	3.04.02.05-0 Sistemas Eletrônicos de Medida e de Controle	\N
1581	095	a	controle	3.04.02.05-0 Sistemas Eletrônicos de Medida e de Controle	\N
1582	095	a	3.04.03.00-6	3.04.03.00-6 Circuitos Elétricos, Magnéticos e Eletrônicos	\N
1583	095	a	circuitos	3.04.03.00-6 Circuitos Elétricos, Magnéticos e Eletrônicos	\N
1584	095	a	eletricos,	3.04.03.00-6 Circuitos Elétricos, Magnéticos e Eletrônicos	\N
1585	095	a	magneticos	3.04.03.00-6 Circuitos Elétricos, Magnéticos e Eletrônicos	\N
1586	095	a	eletronicos	3.04.03.00-6 Circuitos Elétricos, Magnéticos e Eletrônicos	\N
1587	095	a	3.04.03.01-4	3.04.03.01-4 Teoria Geral dos Circuitos Elétricos	\N
1588	095	a	teoria	3.04.03.01-4 Teoria Geral dos Circuitos Elétricos	\N
1589	095	a	geral	3.04.03.01-4 Teoria Geral dos Circuitos Elétricos	\N
1590	095	a	dos	3.04.03.01-4 Teoria Geral dos Circuitos Elétricos	\N
1591	095	a	circuitos	3.04.03.01-4 Teoria Geral dos Circuitos Elétricos	\N
1592	095	a	eletricos	3.04.03.01-4 Teoria Geral dos Circuitos Elétricos	\N
1593	095	a	3.04.03.02-2	3.04.03.02-2 Circuitos Lineares e Não-Lineares	\N
1594	095	a	circuitos	3.04.03.02-2 Circuitos Lineares e Não-Lineares	\N
1595	095	a	lineares	3.04.03.02-2 Circuitos Lineares e Não-Lineares	\N
1596	095	a	nao-lineares	3.04.03.02-2 Circuitos Lineares e Não-Lineares	\N
1597	095	a	3.04.03.03-0	3.04.03.03-0 Circuitos Eletrônicos	\N
1598	095	a	circuitos	3.04.03.03-0 Circuitos Eletrônicos	\N
1599	095	a	eletronicos	3.04.03.03-0 Circuitos Eletrônicos	\N
1600	095	a	3.04.03.04-9	3.04.03.04-9 Circuitos Magnéticos, Magnetismos, Eletromagnetismo	\N
1601	095	a	circuitos	3.04.03.04-9 Circuitos Magnéticos, Magnetismos, Eletromagnetismo	\N
1602	095	a	magneticos,	3.04.03.04-9 Circuitos Magnéticos, Magnetismos, Eletromagnetismo	\N
1603	095	a	magnetismos,	3.04.03.04-9 Circuitos Magnéticos, Magnetismos, Eletromagnetismo	\N
1604	095	a	eletromagnetismo	3.04.03.04-9 Circuitos Magnéticos, Magnetismos, Eletromagnetismo	\N
1605	095	a	3.04.04.00-2	3.04.04.00-2 Sistemas Elétricos de Potência	\N
1606	095	a	sistemas	3.04.04.00-2 Sistemas Elétricos de Potência	\N
1607	095	a	eletricos	3.04.04.00-2 Sistemas Elétricos de Potência	\N
1608	095	a	de	3.04.04.00-2 Sistemas Elétricos de Potência	\N
1609	095	a	potencia	3.04.04.00-2 Sistemas Elétricos de Potência	\N
1610	095	a	3.04.04.01-0	3.04.04.01-0 Geração da Energia Elétrica	\N
1611	095	a	geracao	3.04.04.01-0 Geração da Energia Elétrica	\N
1612	095	a	da	3.04.04.01-0 Geração da Energia Elétrica	\N
1613	095	a	energia	3.04.04.01-0 Geração da Energia Elétrica	\N
1614	095	a	eletrica	3.04.04.01-0 Geração da Energia Elétrica	\N
1615	095	a	3.04.04.02-9	3.04.04.02-9 Transmissão da Energia Elétrica, Distribuição da Energia Elétrica	\N
1616	095	a	transmissao	3.04.04.02-9 Transmissão da Energia Elétrica, Distribuição da Energia Elétrica	\N
1617	095	a	da	3.04.04.02-9 Transmissão da Energia Elétrica, Distribuição da Energia Elétrica	\N
1618	095	a	energia	3.04.04.02-9 Transmissão da Energia Elétrica, Distribuição da Energia Elétrica	\N
1619	095	a	eletrica,	3.04.04.02-9 Transmissão da Energia Elétrica, Distribuição da Energia Elétrica	\N
1620	095	a	distribuicao	3.04.04.02-9 Transmissão da Energia Elétrica, Distribuição da Energia Elétrica	\N
1621	095	a	da	3.04.04.02-9 Transmissão da Energia Elétrica, Distribuição da Energia Elétrica	\N
1622	095	a	energia	3.04.04.02-9 Transmissão da Energia Elétrica, Distribuição da Energia Elétrica	\N
1623	095	a	eletrica	3.04.04.02-9 Transmissão da Energia Elétrica, Distribuição da Energia Elétrica	\N
1624	095	a	3.04.04.03-7	3.04.04.03-7 Conversão e Retificação da Energia Elétrica	\N
1625	095	a	conversao	3.04.04.03-7 Conversão e Retificação da Energia Elétrica	\N
1626	095	a	retificacao	3.04.04.03-7 Conversão e Retificação da Energia Elétrica	\N
1627	095	a	da	3.04.04.03-7 Conversão e Retificação da Energia Elétrica	\N
1628	095	a	energia	3.04.04.03-7 Conversão e Retificação da Energia Elétrica	\N
1629	095	a	eletrica	3.04.04.03-7 Conversão e Retificação da Energia Elétrica	\N
1630	095	a	3.04.04.04-5	3.04.04.04-5 Medição, Controle, Correção e Proteção de Sistemas Elétricos de Potência	\N
1631	095	a	medicao,	3.04.04.04-5 Medição, Controle, Correção e Proteção de Sistemas Elétricos de Potência	\N
1632	095	a	controle,	3.04.04.04-5 Medição, Controle, Correção e Proteção de Sistemas Elétricos de Potência	\N
1633	095	a	correcao	3.04.04.04-5 Medição, Controle, Correção e Proteção de Sistemas Elétricos de Potência	\N
1634	095	a	protecao	3.04.04.04-5 Medição, Controle, Correção e Proteção de Sistemas Elétricos de Potência	\N
1635	095	a	de	3.04.04.04-5 Medição, Controle, Correção e Proteção de Sistemas Elétricos de Potência	\N
1636	095	a	sistemas	3.04.04.04-5 Medição, Controle, Correção e Proteção de Sistemas Elétricos de Potência	\N
1637	095	a	eletricos	3.04.04.04-5 Medição, Controle, Correção e Proteção de Sistemas Elétricos de Potência	\N
1638	095	a	de	3.04.04.04-5 Medição, Controle, Correção e Proteção de Sistemas Elétricos de Potência	\N
1639	095	a	potencia	3.04.04.04-5 Medição, Controle, Correção e Proteção de Sistemas Elétricos de Potência	\N
1640	095	a	3.04.04.05-3	3.04.04.05-3 Máquinas Elétricas e Dispositivos de Potência	\N
1641	095	a	maquinas	3.04.04.05-3 Máquinas Elétricas e Dispositivos de Potência	\N
1642	095	a	eletricas	3.04.04.05-3 Máquinas Elétricas e Dispositivos de Potência	\N
1643	095	a	dispositivos	3.04.04.05-3 Máquinas Elétricas e Dispositivos de Potência	\N
1644	095	a	de	3.04.04.05-3 Máquinas Elétricas e Dispositivos de Potência	\N
1645	095	a	potencia	3.04.04.05-3 Máquinas Elétricas e Dispositivos de Potência	\N
1646	095	a	3.04.04.06-1	3.04.04.06-1 Instalações Elétricas Prediais e Industriais	\N
1647	095	a	instalacoes	3.04.04.06-1 Instalações Elétricas Prediais e Industriais	\N
1648	095	a	eletricas	3.04.04.06-1 Instalações Elétricas Prediais e Industriais	\N
1649	095	a	prediais	3.04.04.06-1 Instalações Elétricas Prediais e Industriais	\N
1650	095	a	industriais	3.04.04.06-1 Instalações Elétricas Prediais e Industriais	\N
1651	095	a	3.04.05.00-9	3.04.05.00-9 Eletrônica Industrial, Sistemas e Controles Eletrônicos	\N
1652	095	a	eletronica	3.04.05.00-9 Eletrônica Industrial, Sistemas e Controles Eletrônicos	\N
1653	095	a	industrial,	3.04.05.00-9 Eletrônica Industrial, Sistemas e Controles Eletrônicos	\N
1654	095	a	sistemas	3.04.05.00-9 Eletrônica Industrial, Sistemas e Controles Eletrônicos	\N
1655	095	a	controles	3.04.05.00-9 Eletrônica Industrial, Sistemas e Controles Eletrônicos	\N
1656	095	a	eletronicos	3.04.05.00-9 Eletrônica Industrial, Sistemas e Controles Eletrônicos	\N
1657	095	a	3.04.05.01-7	3.04.05.01-7 Eletrônica Industrial	\N
1658	095	a	eletronica	3.04.05.01-7 Eletrônica Industrial	\N
1659	095	a	industrial	3.04.05.01-7 Eletrônica Industrial	\N
1660	095	a	3.04.05.02-5	3.04.05.02-5 Automação Eletrônica de Processos Elétricos e Industriais	\N
1661	095	a	automacao	3.04.05.02-5 Automação Eletrônica de Processos Elétricos e Industriais	\N
1662	095	a	eletronica	3.04.05.02-5 Automação Eletrônica de Processos Elétricos e Industriais	\N
1663	095	a	de	3.04.05.02-5 Automação Eletrônica de Processos Elétricos e Industriais	\N
1664	095	a	processos	3.04.05.02-5 Automação Eletrônica de Processos Elétricos e Industriais	\N
1665	095	a	eletricos	3.04.05.02-5 Automação Eletrônica de Processos Elétricos e Industriais	\N
1666	095	a	industriais	3.04.05.02-5 Automação Eletrônica de Processos Elétricos e Industriais	\N
1667	095	a	3.04.05.03-3	3.04.05.03-3 Controle de Processos Eletrônicos, Retroalimentação	\N
1668	095	a	controle	3.04.05.03-3 Controle de Processos Eletrônicos, Retroalimentação	\N
1669	095	a	de	3.04.05.03-3 Controle de Processos Eletrônicos, Retroalimentação	\N
1670	095	a	processos	3.04.05.03-3 Controle de Processos Eletrônicos, Retroalimentação	\N
1671	095	a	eletronicos,	3.04.05.03-3 Controle de Processos Eletrônicos, Retroalimentação	\N
1672	095	a	retroalimentacao	3.04.05.03-3 Controle de Processos Eletrônicos, Retroalimentação	\N
1673	095	a	3.04.06.00-5	3.04.06.00-5 Telecomunicações	\N
1674	095	a	telecomunicacoes	3.04.06.00-5 Telecomunicações	\N
1675	095	a	3.04.06.01-3	3.04.06.01-3 Teoria Eletromagnética, Microondas, Propagação de Ondas, Antenas	\N
1676	095	a	teoria	3.04.06.01-3 Teoria Eletromagnética, Microondas, Propagação de Ondas, Antenas	\N
1677	095	a	eletromagnetica,	3.04.06.01-3 Teoria Eletromagnética, Microondas, Propagação de Ondas, Antenas	\N
1678	095	a	microondas,	3.04.06.01-3 Teoria Eletromagnética, Microondas, Propagação de Ondas, Antenas	\N
1679	095	a	propagacao	3.04.06.01-3 Teoria Eletromagnética, Microondas, Propagação de Ondas, Antenas	\N
1680	095	a	de	3.04.06.01-3 Teoria Eletromagnética, Microondas, Propagação de Ondas, Antenas	\N
1681	095	a	ondas,	3.04.06.01-3 Teoria Eletromagnética, Microondas, Propagação de Ondas, Antenas	\N
1682	095	a	antenas	3.04.06.01-3 Teoria Eletromagnética, Microondas, Propagação de Ondas, Antenas	\N
1683	095	a	3.04.06.02-1	3.04.06.02-1 Radionavegação e Radioastronomia	\N
1684	095	a	radionavegacao	3.04.06.02-1 Radionavegação e Radioastronomia	\N
1685	095	a	radioastronomia	3.04.06.02-1 Radionavegação e Radioastronomia	\N
1686	095	a	3.04.06.03-0	3.04.06.03-0 Sistemas de Telecomunicações	\N
1687	095	a	sistemas	3.04.06.03-0 Sistemas de Telecomunicações	\N
1688	095	a	de	3.04.06.03-0 Sistemas de Telecomunicações	\N
1689	095	a	telecomunicacoes	3.04.06.03-0 Sistemas de Telecomunicações	\N
1690	095	a	3.05.00.00-1	3.05.00.00-1 Engenharia Mecânica	\N
1691	095	a	engenharia	3.05.00.00-1 Engenharia Mecânica	\N
1692	095	a	mecanica	3.05.00.00-1 Engenharia Mecânica	\N
1693	095	a	3.05.01.00-8	3.05.01.00-8 Fenômenos de Transporte	\N
1694	095	a	fenomenos	3.05.01.00-8 Fenômenos de Transporte	\N
1695	095	a	de	3.05.01.00-8 Fenômenos de Transporte	\N
1696	095	a	transporte	3.05.01.00-8 Fenômenos de Transporte	\N
1697	095	a	3.05.01.01-6	3.05.01.01-6 Transferência de Calor	\N
1698	095	a	transferencia	3.05.01.01-6 Transferência de Calor	\N
1699	095	a	de	3.05.01.01-6 Transferência de Calor	\N
1700	095	a	calor	3.05.01.01-6 Transferência de Calor	\N
1701	095	a	3.05.01.02-4	3.05.01.02-4 Mecânica dos Fluidos	\N
1702	095	a	mecanica	3.05.01.02-4 Mecânica dos Fluidos	\N
1703	095	a	dos	3.05.01.02-4 Mecânica dos Fluidos	\N
1704	095	a	fluidos	3.05.01.02-4 Mecânica dos Fluidos	\N
1705	095	a	3.05.01.03-2	3.05.01.03-2 Dinâmica dos Gases	\N
1706	095	a	dinamica	3.05.01.03-2 Dinâmica dos Gases	\N
1707	095	a	dos	3.05.01.03-2 Dinâmica dos Gases	\N
1708	095	a	gases	3.05.01.03-2 Dinâmica dos Gases	\N
1709	095	a	3.05.01.04-0	3.05.01.04-0 Principios Variacionais e Métodos Numéricos	\N
1710	095	a	principios	3.05.01.04-0 Principios Variacionais e Métodos Numéricos	\N
1711	095	a	variacionais	3.05.01.04-0 Principios Variacionais e Métodos Numéricos	\N
1712	095	a	metodos	3.05.01.04-0 Principios Variacionais e Métodos Numéricos	\N
1713	095	a	numericos	3.05.01.04-0 Principios Variacionais e Métodos Numéricos	\N
1714	095	a	3.05.02.00-4	3.05.02.00-4 Engenharia Térmica	\N
1715	095	a	engenharia	3.05.02.00-4 Engenharia Térmica	\N
1716	095	a	termica	3.05.02.00-4 Engenharia Térmica	\N
1717	095	a	3.05.02.01-2	3.05.02.01-2 Termodinâmica	\N
1718	095	a	termodinamica	3.05.02.01-2 Termodinâmica	\N
1719	095	a	3.05.02.02-0	3.05.02.02-0 Controle Ambiental	\N
1720	095	a	controle	3.05.02.02-0 Controle Ambiental	\N
1721	095	a	ambiental	3.05.02.02-0 Controle Ambiental	\N
1722	095	a	3.05.02.03-9	3.05.02.03-9 Aproveitamento da Energia	\N
1723	095	a	aproveitamento	3.05.02.03-9 Aproveitamento da Energia	\N
1724	095	a	da	3.05.02.03-9 Aproveitamento da Energia	\N
1725	095	a	energia	3.05.02.03-9 Aproveitamento da Energia	\N
1726	095	a	3.05.03.00-0	3.05.03.00-0 Mecânica dos Sólidos	\N
1727	095	a	mecanica	3.05.03.00-0 Mecânica dos Sólidos	\N
1728	095	a	dos	3.05.03.00-0 Mecânica dos Sólidos	\N
1729	095	a	solidos	3.05.03.00-0 Mecânica dos Sólidos	\N
1730	095	a	3.05.03.01-9	3.05.03.01-9 Mecânica dos Corpos Sólidos, Elásticos e Plásticos	\N
1731	095	a	mecanica	3.05.03.01-9 Mecânica dos Corpos Sólidos, Elásticos e Plásticos	\N
1732	095	a	dos	3.05.03.01-9 Mecânica dos Corpos Sólidos, Elásticos e Plásticos	\N
1733	095	a	corpos	3.05.03.01-9 Mecânica dos Corpos Sólidos, Elásticos e Plásticos	\N
1734	095	a	solidos,	3.05.03.01-9 Mecânica dos Corpos Sólidos, Elásticos e Plásticos	\N
1735	095	a	elasticos	3.05.03.01-9 Mecânica dos Corpos Sólidos, Elásticos e Plásticos	\N
1736	095	a	plasticos	3.05.03.01-9 Mecânica dos Corpos Sólidos, Elásticos e Plásticos	\N
1737	095	a	3.05.03.02-7	3.05.03.02-7 Dinâmica dos Corpos Rígidos, Elásticos e Plásticos	\N
1738	095	a	dinamica	3.05.03.02-7 Dinâmica dos Corpos Rígidos, Elásticos e Plásticos	\N
1739	095	a	dos	3.05.03.02-7 Dinâmica dos Corpos Rígidos, Elásticos e Plásticos	\N
1740	095	a	corpos	3.05.03.02-7 Dinâmica dos Corpos Rígidos, Elásticos e Plásticos	\N
1741	095	a	rigidos,	3.05.03.02-7 Dinâmica dos Corpos Rígidos, Elásticos e Plásticos	\N
1742	095	a	elasticos	3.05.03.02-7 Dinâmica dos Corpos Rígidos, Elásticos e Plásticos	\N
1743	095	a	plasticos	3.05.03.02-7 Dinâmica dos Corpos Rígidos, Elásticos e Plásticos	\N
1744	095	a	3.05.03.03-5	3.05.03.03-5 Análise de Tensões	\N
1745	095	a	analise	3.05.03.03-5 Análise de Tensões	\N
1746	095	a	de	3.05.03.03-5 Análise de Tensões	\N
1747	095	a	tensoes	3.05.03.03-5 Análise de Tensões	\N
1748	095	a	3.05.03.04-3	3.05.03.04-3 Termoelasticidade	\N
1749	095	a	termoelasticidade	3.05.03.04-3 Termoelasticidade	\N
1750	095	a	3.05.04.00-7	3.05.04.00-7 Projetos de Máquinas	\N
1751	095	a	projetos	3.05.04.00-7 Projetos de Máquinas	\N
1752	095	a	de	3.05.04.00-7 Projetos de Máquinas	\N
1753	095	a	maquinas	3.05.04.00-7 Projetos de Máquinas	\N
1754	095	a	3.05.04.01-5	3.05.04.01-5 Teoria dos Mecanismos	\N
1755	095	a	teoria	3.05.04.01-5 Teoria dos Mecanismos	\N
1756	095	a	dos	3.05.04.01-5 Teoria dos Mecanismos	\N
1757	095	a	mecanismos	3.05.04.01-5 Teoria dos Mecanismos	\N
1758	095	a	3.05.04.02-3	3.05.04.02-3 Estática e Dinâmica Aplicada	\N
1759	095	a	estatica	3.05.04.02-3 Estática e Dinâmica Aplicada	\N
1760	095	a	dinamica	3.05.04.02-3 Estática e Dinâmica Aplicada	\N
1761	095	a	aplicada	3.05.04.02-3 Estática e Dinâmica Aplicada	\N
1762	095	a	3.05.04.03-1	3.05.04.03-1 Elementos de Máquinas	\N
1763	095	a	elementos	3.05.04.03-1 Elementos de Máquinas	\N
1764	095	a	de	3.05.04.03-1 Elementos de Máquinas	\N
1765	095	a	maquinas	3.05.04.03-1 Elementos de Máquinas	\N
1766	095	a	3.05.04.04-0	3.05.04.04-0 Fundamentos Gerais de Projetos das Máquinas	\N
1767	095	a	fundamentos	3.05.04.04-0 Fundamentos Gerais de Projetos das Máquinas	\N
1768	095	a	gerais	3.05.04.04-0 Fundamentos Gerais de Projetos das Máquinas	\N
1769	095	a	de	3.05.04.04-0 Fundamentos Gerais de Projetos das Máquinas	\N
1770	095	a	projetos	3.05.04.04-0 Fundamentos Gerais de Projetos das Máquinas	\N
1771	095	a	das	3.05.04.04-0 Fundamentos Gerais de Projetos das Máquinas	\N
1772	095	a	maquinas	3.05.04.04-0 Fundamentos Gerais de Projetos das Máquinas	\N
1773	095	a	3.05.04.05-8	3.05.04.05-8 Máquinas, Motores e Equipamentos	\N
1774	095	a	maquinas,	3.05.04.05-8 Máquinas, Motores e Equipamentos	\N
1775	095	a	motores	3.05.04.05-8 Máquinas, Motores e Equipamentos	\N
1776	095	a	equipamentos	3.05.04.05-8 Máquinas, Motores e Equipamentos	\N
1777	095	a	3.05.04.06-6	3.05.04.06-6 Métodos de Síntese e Otimização Aplicados ao Projeto Mecânico	\N
1778	095	a	metodos	3.05.04.06-6 Métodos de Síntese e Otimização Aplicados ao Projeto Mecânico	\N
1779	095	a	de	3.05.04.06-6 Métodos de Síntese e Otimização Aplicados ao Projeto Mecânico	\N
1780	095	a	sintese	3.05.04.06-6 Métodos de Síntese e Otimização Aplicados ao Projeto Mecânico	\N
1781	095	a	otimizacao	3.05.04.06-6 Métodos de Síntese e Otimização Aplicados ao Projeto Mecânico	\N
1782	095	a	aplicados	3.05.04.06-6 Métodos de Síntese e Otimização Aplicados ao Projeto Mecânico	\N
1783	095	a	ao	3.05.04.06-6 Métodos de Síntese e Otimização Aplicados ao Projeto Mecânico	\N
1784	095	a	projeto	3.05.04.06-6 Métodos de Síntese e Otimização Aplicados ao Projeto Mecânico	\N
1785	095	a	mecanico	3.05.04.06-6 Métodos de Síntese e Otimização Aplicados ao Projeto Mecânico	\N
1786	095	a	3.05.04.07-4	3.05.04.07-4 Controle de Sistemas Mecânicos	\N
1787	095	a	controle	3.05.04.07-4 Controle de Sistemas Mecânicos	\N
1788	095	a	de	3.05.04.07-4 Controle de Sistemas Mecânicos	\N
1789	095	a	sistemas	3.05.04.07-4 Controle de Sistemas Mecânicos	\N
1790	095	a	mecanicos	3.05.04.07-4 Controle de Sistemas Mecânicos	\N
1791	095	a	3.05.04.08-2	3.05.04.08-2 Aproveitamento de Energia	\N
1792	095	a	aproveitamento	3.05.04.08-2 Aproveitamento de Energia	\N
1793	095	a	de	3.05.04.08-2 Aproveitamento de Energia	\N
1794	095	a	energia	3.05.04.08-2 Aproveitamento de Energia	\N
1795	095	a	3.05.05.00-3	3.05.05.00-3 Processos de Fabricação	\N
1796	095	a	processos	3.05.05.00-3 Processos de Fabricação	\N
1797	095	a	de	3.05.05.00-3 Processos de Fabricação	\N
1798	095	a	fabricacao	3.05.05.00-3 Processos de Fabricação	\N
1799	095	a	3.05.05.01-1	3.05.05.01-1 Matrizes e Ferramentas	\N
1800	095	a	matrizes	3.05.05.01-1 Matrizes e Ferramentas	\N
1801	095	a	ferramentas	3.05.05.01-1 Matrizes e Ferramentas	\N
1802	095	a	3.05.05.02-0	3.05.05.02-0 Máquinas de Usinagem e Conformação	\N
1803	095	a	maquinas	3.05.05.02-0 Máquinas de Usinagem e Conformação	\N
1804	095	a	de	3.05.05.02-0 Máquinas de Usinagem e Conformação	\N
1805	095	a	usinagem	3.05.05.02-0 Máquinas de Usinagem e Conformação	\N
1806	095	a	conformacao	3.05.05.02-0 Máquinas de Usinagem e Conformação	\N
1807	095	a	3.05.05.03-8	3.05.05.03-8 Controle Numérico	\N
1808	095	a	controle	3.05.05.03-8 Controle Numérico	\N
1809	095	a	numerico	3.05.05.03-8 Controle Numérico	\N
1810	095	a	3.05.05.04-6	3.05.05.04-6 Robotização	\N
1811	095	a	robotizacao	3.05.05.04-6 Robotização	\N
1812	095	a	3.05.05.05-4	3.05.05.05-4 Processos de Fabricação, Seleção Econômica	\N
1813	095	a	processos	3.05.05.05-4 Processos de Fabricação, Seleção Econômica	\N
1814	095	a	de	3.05.05.05-4 Processos de Fabricação, Seleção Econômica	\N
1815	095	a	fabricacao,	3.05.05.05-4 Processos de Fabricação, Seleção Econômica	\N
1816	095	a	selecao	3.05.05.05-4 Processos de Fabricação, Seleção Econômica	\N
1817	095	a	economica	3.05.05.05-4 Processos de Fabricação, Seleção Econômica	\N
1818	095	a	3.06.00.00-6	3.06.00.00-6 Engenharia Química	\N
1819	095	a	engenharia	3.06.00.00-6 Engenharia Química	\N
1820	095	a	quimica	3.06.00.00-6 Engenharia Química	\N
1821	095	a	3.06.01.00-2	3.06.01.00-2 Processos Industriais de Engenharia Química	\N
1822	095	a	processos	3.06.01.00-2 Processos Industriais de Engenharia Química	\N
1823	095	a	industriais	3.06.01.00-2 Processos Industriais de Engenharia Química	\N
1824	095	a	de	3.06.01.00-2 Processos Industriais de Engenharia Química	\N
1825	095	a	engenharia	3.06.01.00-2 Processos Industriais de Engenharia Química	\N
1826	095	a	quimica	3.06.01.00-2 Processos Industriais de Engenharia Química	\N
1827	095	a	3.06.01.01-0	3.06.01.01-0 Processos Bioquimicos	\N
1828	095	a	processos	3.06.01.01-0 Processos Bioquimicos	\N
1829	095	a	bioquimicos	3.06.01.01-0 Processos Bioquimicos	\N
1830	095	a	3.06.01.02-9	3.06.01.02-9 Processos Orgânicos	\N
1831	095	a	processos	3.06.01.02-9 Processos Orgânicos	\N
1832	095	a	organicos	3.06.01.02-9 Processos Orgânicos	\N
1833	095	a	3.06.01.03-7	3.06.01.03-7 Processos Inorgânicos	\N
1834	095	a	processos	3.06.01.03-7 Processos Inorgânicos	\N
1835	095	a	inorganicos	3.06.01.03-7 Processos Inorgânicos	\N
1836	095	a	3.06.02.00-9	3.06.02.00-9 Operações Industriais e Equipamentos para Engenharia Química	\N
1837	095	a	operacoes	3.06.02.00-9 Operações Industriais e Equipamentos para Engenharia Química	\N
1838	095	a	industriais	3.06.02.00-9 Operações Industriais e Equipamentos para Engenharia Química	\N
1839	095	a	equipamentos	3.06.02.00-9 Operações Industriais e Equipamentos para Engenharia Química	\N
1840	095	a	para	3.06.02.00-9 Operações Industriais e Equipamentos para Engenharia Química	\N
1841	095	a	engenharia	3.06.02.00-9 Operações Industriais e Equipamentos para Engenharia Química	\N
1842	095	a	quimica	3.06.02.00-9 Operações Industriais e Equipamentos para Engenharia Química	\N
1843	095	a	3.06.02.01-7	3.06.02.01-7 Reatores Químicos	\N
1844	095	a	reatores	3.06.02.01-7 Reatores Químicos	\N
1845	095	a	quimicos	3.06.02.01-7 Reatores Químicos	\N
1846	095	a	3.06.02.02-5	3.06.02.02-5 Operações Características de Processos Bioquímicos	\N
1847	095	a	operacoes	3.06.02.02-5 Operações Características de Processos Bioquímicos	\N
1848	095	a	caracteristicas	3.06.02.02-5 Operações Características de Processos Bioquímicos	\N
1849	095	a	de	3.06.02.02-5 Operações Características de Processos Bioquímicos	\N
1850	095	a	processos	3.06.02.02-5 Operações Características de Processos Bioquímicos	\N
1851	095	a	bioquimicos	3.06.02.02-5 Operações Características de Processos Bioquímicos	\N
1852	095	a	3.06.02.03-3	3.06.02.03-3 Operações de Separação e Mistura	\N
1853	095	a	operacoes	3.06.02.03-3 Operações de Separação e Mistura	\N
1854	095	a	de	3.06.02.03-3 Operações de Separação e Mistura	\N
1855	095	a	separacao	3.06.02.03-3 Operações de Separação e Mistura	\N
1856	095	a	mistura	3.06.02.03-3 Operações de Separação e Mistura	\N
1857	095	a	3.06.03.00-5	3.06.03.00-5 Tecnologia Química	\N
1858	095	a	tecnologia	3.06.03.00-5 Tecnologia Química	\N
1859	095	a	quimica	3.06.03.00-5 Tecnologia Química	\N
1860	095	a	3.06.03.01-3	3.06.03.01-3 Balancos Globais de Matéria e Energia	\N
1861	095	a	balancos	3.06.03.01-3 Balancos Globais de Matéria e Energia	\N
1862	095	a	globais	3.06.03.01-3 Balancos Globais de Matéria e Energia	\N
1863	095	a	de	3.06.03.01-3 Balancos Globais de Matéria e Energia	\N
1864	095	a	materia	3.06.03.01-3 Balancos Globais de Matéria e Energia	\N
1865	095	a	energia	3.06.03.01-3 Balancos Globais de Matéria e Energia	\N
1866	095	a	3.06.03.02-1	3.06.03.02-1 Água	\N
1867	095	a	agua	3.06.03.02-1 Água	\N
1868	095	a	3.06.03.03-0	3.06.03.03-0 Álcool	\N
1869	095	a	alcool	3.06.03.03-0 Álcool	\N
1870	095	a	3.06.03.04-8	3.06.03.04-8 Alimentos	\N
1871	095	a	alimentos	3.06.03.04-8 Alimentos	\N
1872	095	a	3.06.03.05-6	3.06.03.05-6 Borrachas	\N
1873	095	a	borrachas	3.06.03.05-6 Borrachas	\N
1874	095	a	3.06.03.06-4	3.06.03.06-4 Carvão	\N
1875	095	a	carvao	3.06.03.06-4 Carvão	\N
1876	095	a	3.06.03.07-2	3.06.03.07-2 Cerâmica	\N
1877	095	a	ceramica	3.06.03.07-2 Cerâmica	\N
1878	095	a	3.06.03.08-0	3.06.03.08-0 Cimento	\N
1879	095	a	cimento	3.06.03.08-0 Cimento	\N
1880	095	a	3.06.03.09-9	3.06.03.09-9 Couro	\N
1881	095	a	couro	3.06.03.09-9 Couro	\N
1882	095	a	3.06.03.10-2	3.06.03.10-2 Detergentes	\N
1883	095	a	detergentes	3.06.03.10-2 Detergentes	\N
1884	095	a	3.06.03.11-0	3.06.03.11-0 Fertilizantes	\N
1885	095	a	fertilizantes	3.06.03.11-0 Fertilizantes	\N
1886	095	a	3.06.03.12-9	3.06.03.12-9 Medicamentos	\N
1887	095	a	medicamentos	3.06.03.12-9 Medicamentos	\N
1888	095	a	3.06.03.13-7	3.06.03.13-7 Metais não-Ferrosos	\N
1889	095	a	metais	3.06.03.13-7 Metais não-Ferrosos	\N
1890	095	a	nao-ferrosos	3.06.03.13-7 Metais não-Ferrosos	\N
1891	095	a	3.06.03.14-5	3.06.03.14-5 Óleos	\N
1892	095	a	oleos	3.06.03.14-5 Óleos	\N
1893	095	a	3.06.03.15-3	3.06.03.15-3 Papel e Celulose	\N
1894	095	a	papel	3.06.03.15-3 Papel e Celulose	\N
1895	095	a	celulose	3.06.03.15-3 Papel e Celulose	\N
1896	095	a	3.06.03.16-1	3.06.03.16-1 Petróleo e Petroquímica	\N
1897	095	a	petroleo	3.06.03.16-1 Petróleo e Petroquímica	\N
1898	095	a	petroquimica	3.06.03.16-1 Petróleo e Petroquímica	\N
1899	095	a	3.06.03.17-0	3.06.03.17-0 Polímeros	\N
1900	095	a	polimeros	3.06.03.17-0 Polímeros	\N
1901	095	a	3.06.03.18-8	3.06.03.18-8 Produtos Naturais	\N
1902	095	a	produtos	3.06.03.18-8 Produtos Naturais	\N
1903	095	a	naturais	3.06.03.18-8 Produtos Naturais	\N
1904	095	a	3.06.03.19-6	3.06.03.19-6 Têxteis	\N
1905	095	a	texteis	3.06.03.19-6 Têxteis	\N
1906	095	a	3.06.03.20-0	3.06.03.20-0 Tratamentos e Aproveitamento de Rejeitos	\N
1907	095	a	tratamentos	3.06.03.20-0 Tratamentos e Aproveitamento de Rejeitos	\N
1908	095	a	aproveitamento	3.06.03.20-0 Tratamentos e Aproveitamento de Rejeitos	\N
1909	095	a	de	3.06.03.20-0 Tratamentos e Aproveitamento de Rejeitos	\N
1910	095	a	rejeitos	3.06.03.20-0 Tratamentos e Aproveitamento de Rejeitos	\N
1911	095	a	3.06.03.21-8	3.06.03.21-8 Xisto	\N
1912	095	a	xisto	3.06.03.21-8 Xisto	\N
1913	095	a	3.07.00.00-0	3.07.00.00-0 Engenharia Sanitária	\N
1914	095	a	engenharia	3.07.00.00-0 Engenharia Sanitária	\N
1915	095	a	sanitaria	3.07.00.00-0 Engenharia Sanitária	\N
1916	095	a	3.07.01.00-7	3.07.01.00-7 Recursos Hídricos	\N
1917	095	a	recursos	3.07.01.00-7 Recursos Hídricos	\N
1918	095	a	hidricos	3.07.01.00-7 Recursos Hídricos	\N
1919	095	a	3.07.01.01-5	3.07.01.01-5 Planejamento Integrado dos Recursos Hídricos	\N
1920	095	a	planejamento	3.07.01.01-5 Planejamento Integrado dos Recursos Hídricos	\N
1921	095	a	integrado	3.07.01.01-5 Planejamento Integrado dos Recursos Hídricos	\N
1922	095	a	dos	3.07.01.01-5 Planejamento Integrado dos Recursos Hídricos	\N
1923	095	a	recursos	3.07.01.01-5 Planejamento Integrado dos Recursos Hídricos	\N
1924	095	a	hidricos	3.07.01.01-5 Planejamento Integrado dos Recursos Hídricos	\N
1925	095	a	3.07.01.02-3	3.07.01.02-3 Tecnologia e Problemas Sanitários de Irrigação	\N
1926	095	a	tecnologia	3.07.01.02-3 Tecnologia e Problemas Sanitários de Irrigação	\N
1927	095	a	problemas	3.07.01.02-3 Tecnologia e Problemas Sanitários de Irrigação	\N
1928	095	a	sanitarios	3.07.01.02-3 Tecnologia e Problemas Sanitários de Irrigação	\N
1929	095	a	de	3.07.01.02-3 Tecnologia e Problemas Sanitários de Irrigação	\N
1930	095	a	irrigacao	3.07.01.02-3 Tecnologia e Problemas Sanitários de Irrigação	\N
1931	095	a	3.07.01.03-1	3.07.01.03-1 Águas Subterrâneas e Poços Profundos	\N
1932	095	a	aguas	3.07.01.03-1 Águas Subterrâneas e Poços Profundos	\N
1933	095	a	subterraneas	3.07.01.03-1 Águas Subterrâneas e Poços Profundos	\N
1934	095	a	pocos	3.07.01.03-1 Águas Subterrâneas e Poços Profundos	\N
1935	095	a	profundos	3.07.01.03-1 Águas Subterrâneas e Poços Profundos	\N
1936	095	a	3.07.01.04-0	3.07.01.04-0 Controle de Enchentes e de Barragens	\N
1937	095	a	controle	3.07.01.04-0 Controle de Enchentes e de Barragens	\N
1938	095	a	de	3.07.01.04-0 Controle de Enchentes e de Barragens	\N
1939	095	a	enchentes	3.07.01.04-0 Controle de Enchentes e de Barragens	\N
1940	095	a	de	3.07.01.04-0 Controle de Enchentes e de Barragens	\N
1941	095	a	barragens	3.07.01.04-0 Controle de Enchentes e de Barragens	\N
1942	095	a	3.07.01.05-8	3.07.01.05-8 Sedimentologia	\N
1943	095	a	sedimentologia	3.07.01.05-8 Sedimentologia	\N
1944	095	a	3.07.02.00-3	3.07.02.00-3 Tratamento de Águas de Abastecimento e Residuárias	\N
1945	095	a	tratamento	3.07.02.00-3 Tratamento de Águas de Abastecimento e Residuárias	\N
1946	095	a	de	3.07.02.00-3 Tratamento de Águas de Abastecimento e Residuárias	\N
1947	095	a	aguas	3.07.02.00-3 Tratamento de Águas de Abastecimento e Residuárias	\N
1948	095	a	de	3.07.02.00-3 Tratamento de Águas de Abastecimento e Residuárias	\N
1949	095	a	abastecimento	3.07.02.00-3 Tratamento de Águas de Abastecimento e Residuárias	\N
1950	095	a	residuarias	3.07.02.00-3 Tratamento de Águas de Abastecimento e Residuárias	\N
1951	095	a	3.07.02.01-1	3.07.02.01-1 Química Sanitária	\N
1952	095	a	quimica	3.07.02.01-1 Química Sanitária	\N
1953	095	a	sanitaria	3.07.02.01-1 Química Sanitária	\N
1954	095	a	3.07.02.02-0	3.07.02.02-0 Processos Simplificados de Tratamento de Águas	\N
1955	095	a	processos	3.07.02.02-0 Processos Simplificados de Tratamento de Águas	\N
1956	095	a	simplificados	3.07.02.02-0 Processos Simplificados de Tratamento de Águas	\N
1957	095	a	de	3.07.02.02-0 Processos Simplificados de Tratamento de Águas	\N
1958	095	a	tratamento	3.07.02.02-0 Processos Simplificados de Tratamento de Águas	\N
1959	095	a	de	3.07.02.02-0 Processos Simplificados de Tratamento de Águas	\N
1960	095	a	aguas	3.07.02.02-0 Processos Simplificados de Tratamento de Águas	\N
1961	095	a	3.07.02.03-8	3.07.02.03-8 Técnicas Convencionais de Tratamento de Águas	\N
1962	095	a	tecnicas	3.07.02.03-8 Técnicas Convencionais de Tratamento de Águas	\N
1963	095	a	convencionais	3.07.02.03-8 Técnicas Convencionais de Tratamento de Águas	\N
1964	095	a	de	3.07.02.03-8 Técnicas Convencionais de Tratamento de Águas	\N
1965	095	a	tratamento	3.07.02.03-8 Técnicas Convencionais de Tratamento de Águas	\N
1966	095	a	de	3.07.02.03-8 Técnicas Convencionais de Tratamento de Águas	\N
1967	095	a	aguas	3.07.02.03-8 Técnicas Convencionais de Tratamento de Águas	\N
1968	095	a	3.07.02.04-6	3.07.02.04-6 Técnicas Avancadas de Tratamento de Águas	\N
1969	095	a	tecnicas	3.07.02.04-6 Técnicas Avancadas de Tratamento de Águas	\N
1970	095	a	avancadas	3.07.02.04-6 Técnicas Avancadas de Tratamento de Águas	\N
1971	095	a	de	3.07.02.04-6 Técnicas Avancadas de Tratamento de Águas	\N
1972	095	a	tratamento	3.07.02.04-6 Técnicas Avancadas de Tratamento de Águas	\N
1973	095	a	de	3.07.02.04-6 Técnicas Avancadas de Tratamento de Águas	\N
1974	095	a	aguas	3.07.02.04-6 Técnicas Avancadas de Tratamento de Águas	\N
1975	095	a	3.07.02.05-4	3.07.02.05-4 Estudos e Caracterização de Efluentes Industriais	\N
1976	095	a	estudos	3.07.02.05-4 Estudos e Caracterização de Efluentes Industriais	\N
1977	095	a	caracterizacao	3.07.02.05-4 Estudos e Caracterização de Efluentes Industriais	\N
1978	095	a	de	3.07.02.05-4 Estudos e Caracterização de Efluentes Industriais	\N
1979	095	a	efluentes	3.07.02.05-4 Estudos e Caracterização de Efluentes Industriais	\N
1980	095	a	industriais	3.07.02.05-4 Estudos e Caracterização de Efluentes Industriais	\N
1981	095	a	3.07.02.06-2	3.07.02.06-2 Lay Out de Processos Industriais	\N
1982	095	a	lay	3.07.02.06-2 Lay Out de Processos Industriais	\N
1983	095	a	out	3.07.02.06-2 Lay Out de Processos Industriais	\N
1984	095	a	de	3.07.02.06-2 Lay Out de Processos Industriais	\N
1985	095	a	processos	3.07.02.06-2 Lay Out de Processos Industriais	\N
1986	095	a	industriais	3.07.02.06-2 Lay Out de Processos Industriais	\N
1987	095	a	3.07.02.07-0	3.07.02.07-0 Resíduos Radioativos	\N
1988	095	a	residuos	3.07.02.07-0 Resíduos Radioativos	\N
1989	095	a	radioativos	3.07.02.07-0 Resíduos Radioativos	\N
1990	095	a	3.07.03.00-0	3.07.03.00-0 Saneamento Básico	\N
1991	095	a	saneamento	3.07.03.00-0 Saneamento Básico	\N
1992	095	a	basico	3.07.03.00-0 Saneamento Básico	\N
1993	095	a	3.07.03.01-8	3.07.03.01-8 Técnicas de Abastecimento da Água	\N
1994	095	a	tecnicas	3.07.03.01-8 Técnicas de Abastecimento da Água	\N
1995	095	a	de	3.07.03.01-8 Técnicas de Abastecimento da Água	\N
1996	095	a	abastecimento	3.07.03.01-8 Técnicas de Abastecimento da Água	\N
1997	095	a	da	3.07.03.01-8 Técnicas de Abastecimento da Água	\N
1998	095	a	agua	3.07.03.01-8 Técnicas de Abastecimento da Água	\N
1999	095	a	3.07.03.02-6	3.07.03.02-6 Drenagem de Águas Residuárias	\N
2000	095	a	drenagem	3.07.03.02-6 Drenagem de Águas Residuárias	\N
2001	095	a	de	3.07.03.02-6 Drenagem de Águas Residuárias	\N
2002	095	a	aguas	3.07.03.02-6 Drenagem de Águas Residuárias	\N
2003	095	a	residuarias	3.07.03.02-6 Drenagem de Águas Residuárias	\N
2004	095	a	3.07.03.03-4	3.07.03.03-4 Drenagem Urbana de Águas Pluviais	\N
2005	095	a	drenagem	3.07.03.03-4 Drenagem Urbana de Águas Pluviais	\N
2006	095	a	urbana	3.07.03.03-4 Drenagem Urbana de Águas Pluviais	\N
2007	095	a	de	3.07.03.03-4 Drenagem Urbana de Águas Pluviais	\N
2008	095	a	aguas	3.07.03.03-4 Drenagem Urbana de Águas Pluviais	\N
2009	095	a	pluviais	3.07.03.03-4 Drenagem Urbana de Águas Pluviais	\N
2010	095	a	3.07.03.04-2	3.07.03.04-2 Resíduos Sólidos, Domésticos e Industriais	\N
2011	095	a	residuos	3.07.03.04-2 Resíduos Sólidos, Domésticos e Industriais	\N
2012	095	a	solidos,	3.07.03.04-2 Resíduos Sólidos, Domésticos e Industriais	\N
2013	095	a	domesticos	3.07.03.04-2 Resíduos Sólidos, Domésticos e Industriais	\N
2014	095	a	industriais	3.07.03.04-2 Resíduos Sólidos, Domésticos e Industriais	\N
2015	095	a	3.07.03.05-0	3.07.03.05-0 Limpeza Pública	\N
2016	095	a	limpeza	3.07.03.05-0 Limpeza Pública	\N
2017	095	a	publica	3.07.03.05-0 Limpeza Pública	\N
2018	095	a	3.07.03.06-9	3.07.03.06-9 Instalações Hidráulico-Sanitárias	\N
2019	095	a	instalacoes	3.07.03.06-9 Instalações Hidráulico-Sanitárias	\N
2020	095	a	hidraulico-sanitarias	3.07.03.06-9 Instalações Hidráulico-Sanitárias	\N
2021	095	a	3.07.04.00-6	3.07.04.00-6 Saneamento Ambiental	\N
2022	095	a	saneamento	3.07.04.00-6 Saneamento Ambiental	\N
2023	095	a	ambiental	3.07.04.00-6 Saneamento Ambiental	\N
2024	095	a	3.07.04.01-4	3.07.04.01-4 Ecologia Aplicada à Engenharia Sanitária	\N
2025	095	a	ecologia	3.07.04.01-4 Ecologia Aplicada à Engenharia Sanitária	\N
2026	095	a	aplicada	3.07.04.01-4 Ecologia Aplicada à Engenharia Sanitária	\N
2027	095	a	engenharia	3.07.04.01-4 Ecologia Aplicada à Engenharia Sanitária	\N
2028	095	a	sanitaria	3.07.04.01-4 Ecologia Aplicada à Engenharia Sanitária	\N
2029	095	a	3.07.04.02-2	3.07.04.02-2 Microbiologia Aplicada e Engenharia Sanitária	\N
2030	095	a	microbiologia	3.07.04.02-2 Microbiologia Aplicada e Engenharia Sanitária	\N
2031	095	a	aplicada	3.07.04.02-2 Microbiologia Aplicada e Engenharia Sanitária	\N
2032	095	a	engenharia	3.07.04.02-2 Microbiologia Aplicada e Engenharia Sanitária	\N
2033	095	a	sanitaria	3.07.04.02-2 Microbiologia Aplicada e Engenharia Sanitária	\N
2034	095	a	3.07.04.03-0	3.07.04.03-0 Parasitologia Aplicada à Engenharia Sanitária	\N
2035	095	a	parasitologia	3.07.04.03-0 Parasitologia Aplicada à Engenharia Sanitária	\N
2036	095	a	aplicada	3.07.04.03-0 Parasitologia Aplicada à Engenharia Sanitária	\N
2037	095	a	engenharia	3.07.04.03-0 Parasitologia Aplicada à Engenharia Sanitária	\N
2038	095	a	sanitaria	3.07.04.03-0 Parasitologia Aplicada à Engenharia Sanitária	\N
2039	095	a	3.07.04.04-9	3.07.04.04-9 Qualidade do Ar, das Águas e do Solo	\N
2040	095	a	qualidade	3.07.04.04-9 Qualidade do Ar, das Águas e do Solo	\N
2041	095	a	do	3.07.04.04-9 Qualidade do Ar, das Águas e do Solo	\N
2042	095	a	ar,	3.07.04.04-9 Qualidade do Ar, das Águas e do Solo	\N
2043	095	a	das	3.07.04.04-9 Qualidade do Ar, das Águas e do Solo	\N
2044	095	a	aguas	3.07.04.04-9 Qualidade do Ar, das Águas e do Solo	\N
2045	095	a	do	3.07.04.04-9 Qualidade do Ar, das Águas e do Solo	\N
2046	095	a	solo	3.07.04.04-9 Qualidade do Ar, das Águas e do Solo	\N
2047	095	a	3.07.04.05-7	3.07.04.05-7 Controle da Poluição	\N
2048	095	a	controle	3.07.04.05-7 Controle da Poluição	\N
2049	095	a	da	3.07.04.05-7 Controle da Poluição	\N
2050	095	a	poluicao	3.07.04.05-7 Controle da Poluição	\N
2051	095	a	3.07.04.06-5	3.07.04.06-5 Legislação Ambiental	\N
2052	095	a	legislacao	3.07.04.06-5 Legislação Ambiental	\N
2053	095	a	ambiental	3.07.04.06-5 Legislação Ambiental	\N
2054	095	a	3.08.00.00-5	3.08.00.00-5 Engenharia de Produção	\N
2055	095	a	engenharia	3.08.00.00-5 Engenharia de Produção	\N
2056	095	a	de	3.08.00.00-5 Engenharia de Produção	\N
2057	095	a	producao	3.08.00.00-5 Engenharia de Produção	\N
2058	095	a	3.08.01.00-1	3.08.01.00-1 Gerência de Produção	\N
2059	095	a	gerencia	3.08.01.00-1 Gerência de Produção	\N
2060	095	a	de	3.08.01.00-1 Gerência de Produção	\N
2061	095	a	producao	3.08.01.00-1 Gerência de Produção	\N
2062	095	a	3.08.01.01-0	3.08.01.01-0 Planejamento de Instalações Industriais	\N
2063	095	a	planejamento	3.08.01.01-0 Planejamento de Instalações Industriais	\N
2064	095	a	de	3.08.01.01-0 Planejamento de Instalações Industriais	\N
2065	095	a	instalacoes	3.08.01.01-0 Planejamento de Instalações Industriais	\N
2066	095	a	industriais	3.08.01.01-0 Planejamento de Instalações Industriais	\N
2067	095	a	3.08.01.02-8	3.08.01.02-8 Planejamento, Projeto e Controle de Sistemas de Produção	\N
2068	095	a	planejamento,	3.08.01.02-8 Planejamento, Projeto e Controle de Sistemas de Produção	\N
2069	095	a	projeto	3.08.01.02-8 Planejamento, Projeto e Controle de Sistemas de Produção	\N
2070	095	a	controle	3.08.01.02-8 Planejamento, Projeto e Controle de Sistemas de Produção	\N
2071	095	a	de	3.08.01.02-8 Planejamento, Projeto e Controle de Sistemas de Produção	\N
2072	095	a	sistemas	3.08.01.02-8 Planejamento, Projeto e Controle de Sistemas de Produção	\N
2073	095	a	de	3.08.01.02-8 Planejamento, Projeto e Controle de Sistemas de Produção	\N
2074	095	a	producao	3.08.01.02-8 Planejamento, Projeto e Controle de Sistemas de Produção	\N
2075	095	a	3.08.01.03-6	3.08.01.03-6 Higiene e Segurança do Trabalho	\N
2076	095	a	higiene	3.08.01.03-6 Higiene e Segurança do Trabalho	\N
2077	095	a	seguranca	3.08.01.03-6 Higiene e Segurança do Trabalho	\N
2078	095	a	do	3.08.01.03-6 Higiene e Segurança do Trabalho	\N
2079	095	a	trabalho	3.08.01.03-6 Higiene e Segurança do Trabalho	\N
2080	095	a	3.08.01.04-4	3.08.01.04-4 Suprimentos	\N
2081	095	a	suprimentos	3.08.01.04-4 Suprimentos	\N
2082	095	a	3.08.01.05-2	3.08.01.05-2 Garantia de Controle de Qualidade	\N
2083	095	a	garantia	3.08.01.05-2 Garantia de Controle de Qualidade	\N
2084	095	a	de	3.08.01.05-2 Garantia de Controle de Qualidade	\N
2085	095	a	controle	3.08.01.05-2 Garantia de Controle de Qualidade	\N
2086	095	a	de	3.08.01.05-2 Garantia de Controle de Qualidade	\N
2087	095	a	qualidade	3.08.01.05-2 Garantia de Controle de Qualidade	\N
2088	095	a	3.08.02.00-8	3.08.02.00-8 Pesquisa Operacional	\N
2089	095	a	pesquisa	3.08.02.00-8 Pesquisa Operacional	\N
2090	095	a	operacional	3.08.02.00-8 Pesquisa Operacional	\N
2091	095	a	3.08.02.01-6	3.08.02.01-6 Processos Estocásticos e Teorias da Filas	\N
2092	095	a	processos	3.08.02.01-6 Processos Estocásticos e Teorias da Filas	\N
2093	095	a	estocasticos	3.08.02.01-6 Processos Estocásticos e Teorias da Filas	\N
2094	095	a	teorias	3.08.02.01-6 Processos Estocásticos e Teorias da Filas	\N
2095	095	a	da	3.08.02.01-6 Processos Estocásticos e Teorias da Filas	\N
2096	095	a	filas	3.08.02.01-6 Processos Estocásticos e Teorias da Filas	\N
2097	095	a	3.08.02.02-4	3.08.02.02-4 Programação Linear, Não-Linear, Mista e Dinâmica	\N
2098	095	a	programacao	3.08.02.02-4 Programação Linear, Não-Linear, Mista e Dinâmica	\N
2099	095	a	linear,	3.08.02.02-4 Programação Linear, Não-Linear, Mista e Dinâmica	\N
2100	095	a	nao-linear,	3.08.02.02-4 Programação Linear, Não-Linear, Mista e Dinâmica	\N
2101	095	a	mista	3.08.02.02-4 Programação Linear, Não-Linear, Mista e Dinâmica	\N
2102	095	a	dinamica	3.08.02.02-4 Programação Linear, Não-Linear, Mista e Dinâmica	\N
2103	095	a	3.08.02.03-2	3.08.02.03-2 Séries Temporais	\N
2104	095	a	series	3.08.02.03-2 Séries Temporais	\N
2105	095	a	temporais	3.08.02.03-2 Séries Temporais	\N
2106	095	a	3.08.02.04-0	3.08.02.04-0 Teoria dos Grafos	\N
2107	095	a	teoria	3.08.02.04-0 Teoria dos Grafos	\N
2108	095	a	dos	3.08.02.04-0 Teoria dos Grafos	\N
2109	095	a	grafos	3.08.02.04-0 Teoria dos Grafos	\N
2110	095	a	3.08.02.05-9	3.08.02.05-9 Teoria dos Jogos	\N
2111	095	a	teoria	3.08.02.05-9 Teoria dos Jogos	\N
2112	095	a	dos	3.08.02.05-9 Teoria dos Jogos	\N
2113	095	a	jogos	3.08.02.05-9 Teoria dos Jogos	\N
2114	095	a	3.08.03.00-4	3.08.03.00-4 Engenharia do Produto	\N
2115	095	a	engenharia	3.08.03.00-4 Engenharia do Produto	\N
2116	095	a	do	3.08.03.00-4 Engenharia do Produto	\N
2117	095	a	produto	3.08.03.00-4 Engenharia do Produto	\N
2118	095	a	3.08.03.01-2	3.08.03.01-2 Ergonomia	\N
2119	095	a	ergonomia	3.08.03.01-2 Ergonomia	\N
2120	095	a	3.08.03.02-0	3.08.03.02-0 Metodologia de Projeto do Produto	\N
2121	095	a	metodologia	3.08.03.02-0 Metodologia de Projeto do Produto	\N
2122	095	a	de	3.08.03.02-0 Metodologia de Projeto do Produto	\N
2123	095	a	projeto	3.08.03.02-0 Metodologia de Projeto do Produto	\N
2124	095	a	do	3.08.03.02-0 Metodologia de Projeto do Produto	\N
2125	095	a	produto	3.08.03.02-0 Metodologia de Projeto do Produto	\N
2126	095	a	3.08.03.03-9	3.08.03.03-9 Processos de Trabalho	\N
2127	095	a	processos	3.08.03.03-9 Processos de Trabalho	\N
2128	095	a	de	3.08.03.03-9 Processos de Trabalho	\N
2129	095	a	trabalho	3.08.03.03-9 Processos de Trabalho	\N
2130	095	a	3.08.03.04-7	3.08.03.04-7 Gerência do Projeto e do Produto	\N
2131	095	a	gerencia	3.08.03.04-7 Gerência do Projeto e do Produto	\N
2132	095	a	do	3.08.03.04-7 Gerência do Projeto e do Produto	\N
2133	095	a	projeto	3.08.03.04-7 Gerência do Projeto e do Produto	\N
2134	095	a	do	3.08.03.04-7 Gerência do Projeto e do Produto	\N
2135	095	a	produto	3.08.03.04-7 Gerência do Projeto e do Produto	\N
2136	095	a	3.08.03.05-5	3.08.03.05-5 Desenvolvimento de Produto	\N
2137	095	a	desenvolvimento	3.08.03.05-5 Desenvolvimento de Produto	\N
2138	095	a	de	3.08.03.05-5 Desenvolvimento de Produto	\N
2139	095	a	produto	3.08.03.05-5 Desenvolvimento de Produto	\N
2140	095	a	3.08.04.00-0	3.08.04.00-0 Engenharia Econômica	\N
2141	095	a	engenharia	3.08.04.00-0 Engenharia Econômica	\N
2142	095	a	economica	3.08.04.00-0 Engenharia Econômica	\N
2143	095	a	3.08.04.01-9	3.08.04.01-9 Estudo de Mercado	\N
2144	095	a	estudo	3.08.04.01-9 Estudo de Mercado	\N
2145	095	a	de	3.08.04.01-9 Estudo de Mercado	\N
2146	095	a	mercado	3.08.04.01-9 Estudo de Mercado	\N
2147	095	a	3.08.04.02-7	3.08.04.02-7 Localização Industrial	\N
2148	095	a	localizacao	3.08.04.02-7 Localização Industrial	\N
2149	095	a	industrial	3.08.04.02-7 Localização Industrial	\N
2150	095	a	3.08.04.03-5	3.08.04.03-5 Análise de Custos	\N
2151	095	a	analise	3.08.04.03-5 Análise de Custos	\N
2152	095	a	de	3.08.04.03-5 Análise de Custos	\N
2153	095	a	custos	3.08.04.03-5 Análise de Custos	\N
2154	095	a	3.08.04.04-3	3.08.04.04-3 Economia de Tecnologia	\N
2155	095	a	economia	3.08.04.04-3 Economia de Tecnologia	\N
2156	095	a	de	3.08.04.04-3 Economia de Tecnologia	\N
2157	095	a	tecnologia	3.08.04.04-3 Economia de Tecnologia	\N
2158	095	a	3.08.04.05-1	3.08.04.05-1 Vida Econômica dos Equipamentos	\N
2159	095	a	vida	3.08.04.05-1 Vida Econômica dos Equipamentos	\N
2160	095	a	economica	3.08.04.05-1 Vida Econômica dos Equipamentos	\N
2161	095	a	dos	3.08.04.05-1 Vida Econômica dos Equipamentos	\N
2162	095	a	equipamentos	3.08.04.05-1 Vida Econômica dos Equipamentos	\N
2163	095	a	3.08.04.06-0	3.08.04.06-0 Avaliação de Projetos	\N
2164	095	a	avaliacao	3.08.04.06-0 Avaliação de Projetos	\N
2165	095	a	de	3.08.04.06-0 Avaliação de Projetos	\N
2166	095	a	projetos	3.08.04.06-0 Avaliação de Projetos	\N
2167	095	a	3.09.00.00-0	3.09.00.00-0 Engenharia Nuclear	\N
2168	095	a	engenharia	3.09.00.00-0 Engenharia Nuclear	\N
2169	095	a	nuclear	3.09.00.00-0 Engenharia Nuclear	\N
2170	095	a	3.09.01.00-6	3.09.01.00-6 Aplicações de Radioisotopos	\N
2171	095	a	aplicacoes	3.09.01.00-6 Aplicações de Radioisotopos	\N
2172	095	a	de	3.09.01.00-6 Aplicações de Radioisotopos	\N
2173	095	a	radioisotopos	3.09.01.00-6 Aplicações de Radioisotopos	\N
2174	095	a	3.09.01.01-4	3.09.01.01-4 Produção de Radioisotopos	\N
2175	095	a	producao	3.09.01.01-4 Produção de Radioisotopos	\N
2176	095	a	de	3.09.01.01-4 Produção de Radioisotopos	\N
2177	095	a	radioisotopos	3.09.01.01-4 Produção de Radioisotopos	\N
2178	095	a	3.09.01.02-2	3.09.01.02-2 Aplicações Industriais de Radioisotopos	\N
2179	095	a	aplicacoes	3.09.01.02-2 Aplicações Industriais de Radioisotopos	\N
2180	095	a	industriais	3.09.01.02-2 Aplicações Industriais de Radioisotopos	\N
2181	095	a	de	3.09.01.02-2 Aplicações Industriais de Radioisotopos	\N
2182	095	a	radioisotopos	3.09.01.02-2 Aplicações Industriais de Radioisotopos	\N
2183	095	a	3.09.01.03-0	3.09.01.03-0 Instrumentação para Medida e Controle de Radiação	\N
2184	095	a	instrumentacao	3.09.01.03-0 Instrumentação para Medida e Controle de Radiação	\N
2185	095	a	para	3.09.01.03-0 Instrumentação para Medida e Controle de Radiação	\N
2186	095	a	medida	3.09.01.03-0 Instrumentação para Medida e Controle de Radiação	\N
2187	095	a	controle	3.09.01.03-0 Instrumentação para Medida e Controle de Radiação	\N
2188	095	a	de	3.09.01.03-0 Instrumentação para Medida e Controle de Radiação	\N
2189	095	a	radiacao	3.09.01.03-0 Instrumentação para Medida e Controle de Radiação	\N
2190	095	a	3.09.02.00-2	3.09.02.00-2 Fusão Controlada	\N
2191	095	a	fusao	3.09.02.00-2 Fusão Controlada	\N
2192	095	a	controlada	3.09.02.00-2 Fusão Controlada	\N
2193	095	a	3.09.02.01-0	3.09.02.01-0 Processos Industriais da Fusão Controlada	\N
2194	095	a	processos	3.09.02.01-0 Processos Industriais da Fusão Controlada	\N
2195	095	a	industriais	3.09.02.01-0 Processos Industriais da Fusão Controlada	\N
2196	095	a	da	3.09.02.01-0 Processos Industriais da Fusão Controlada	\N
2197	095	a	fusao	3.09.02.01-0 Processos Industriais da Fusão Controlada	\N
2198	095	a	controlada	3.09.02.01-0 Processos Industriais da Fusão Controlada	\N
2199	095	a	3.09.02.02-9	3.09.02.02-9 Problemas Tecnológicos da Fusão Controlada	\N
2200	095	a	problemas	3.09.02.02-9 Problemas Tecnológicos da Fusão Controlada	\N
2201	095	a	tecnologicos	3.09.02.02-9 Problemas Tecnológicos da Fusão Controlada	\N
2202	095	a	da	3.09.02.02-9 Problemas Tecnológicos da Fusão Controlada	\N
2203	095	a	fusao	3.09.02.02-9 Problemas Tecnológicos da Fusão Controlada	\N
2204	095	a	controlada	3.09.02.02-9 Problemas Tecnológicos da Fusão Controlada	\N
2205	095	a	3.09.03.00-9	3.09.03.00-9 Combustível Nuclear	\N
2206	095	a	combustivel	3.09.03.00-9 Combustível Nuclear	\N
2207	095	a	nuclear	3.09.03.00-9 Combustível Nuclear	\N
2208	095	a	3.09.03.01-7	3.09.03.01-7 Extração de Combustível Nuclear	\N
2209	095	a	extracao	3.09.03.01-7 Extração de Combustível Nuclear	\N
2210	095	a	de	3.09.03.01-7 Extração de Combustível Nuclear	\N
2211	095	a	combustivel	3.09.03.01-7 Extração de Combustível Nuclear	\N
2212	095	a	nuclear	3.09.03.01-7 Extração de Combustível Nuclear	\N
2213	095	a	3.09.03.02-5	3.09.03.02-5 Conversão, Enriquecimento e Fabricação de Combustível Nuclear	\N
2214	095	a	conversao,	3.09.03.02-5 Conversão, Enriquecimento e Fabricação de Combustível Nuclear	\N
2215	095	a	enriquecimento	3.09.03.02-5 Conversão, Enriquecimento e Fabricação de Combustível Nuclear	\N
2216	095	a	fabricacao	3.09.03.02-5 Conversão, Enriquecimento e Fabricação de Combustível Nuclear	\N
2217	095	a	de	3.09.03.02-5 Conversão, Enriquecimento e Fabricação de Combustível Nuclear	\N
2218	095	a	combustivel	3.09.03.02-5 Conversão, Enriquecimento e Fabricação de Combustível Nuclear	\N
2219	095	a	nuclear	3.09.03.02-5 Conversão, Enriquecimento e Fabricação de Combustível Nuclear	\N
2220	095	a	3.09.03.03-3	3.09.03.03-3 Reprocessamento de Combustível Nuclear	\N
2221	095	a	reprocessamento	3.09.03.03-3 Reprocessamento de Combustível Nuclear	\N
2222	095	a	de	3.09.03.03-3 Reprocessamento de Combustível Nuclear	\N
2223	095	a	combustivel	3.09.03.03-3 Reprocessamento de Combustível Nuclear	\N
2224	095	a	nuclear	3.09.03.03-3 Reprocessamento de Combustível Nuclear	\N
2225	095	a	3.09.03.04-1	3.09.03.04-1 Rejeitos de Combustível Nuclear	\N
2226	095	a	rejeitos	3.09.03.04-1 Rejeitos de Combustível Nuclear	\N
2227	095	a	de	3.09.03.04-1 Rejeitos de Combustível Nuclear	\N
2228	095	a	combustivel	3.09.03.04-1 Rejeitos de Combustível Nuclear	\N
2229	095	a	nuclear	3.09.03.04-1 Rejeitos de Combustível Nuclear	\N
2230	095	a	3.09.04.00-5	3.09.04.00-5 Tecnologia dos Reatores	\N
2231	095	a	tecnologia	3.09.04.00-5 Tecnologia dos Reatores	\N
2232	095	a	dos	3.09.04.00-5 Tecnologia dos Reatores	\N
2233	095	a	reatores	3.09.04.00-5 Tecnologia dos Reatores	\N
2234	095	a	3.09.04.01-3	3.09.04.01-3 Núcleo do Reator	\N
2235	095	a	nucleo	3.09.04.01-3 Núcleo do Reator	\N
2236	095	a	do	3.09.04.01-3 Núcleo do Reator	\N
2237	095	a	reator	3.09.04.01-3 Núcleo do Reator	\N
2238	095	a	3.09.04.02-1	3.09.04.02-1 Materiais Nucleares e Blindagem de Reatores	\N
2239	095	a	materiais	3.09.04.02-1 Materiais Nucleares e Blindagem de Reatores	\N
2240	095	a	nucleares	3.09.04.02-1 Materiais Nucleares e Blindagem de Reatores	\N
2241	095	a	blindagem	3.09.04.02-1 Materiais Nucleares e Blindagem de Reatores	\N
2242	095	a	de	3.09.04.02-1 Materiais Nucleares e Blindagem de Reatores	\N
2243	095	a	reatores	3.09.04.02-1 Materiais Nucleares e Blindagem de Reatores	\N
2244	095	a	3.09.04.03-0	3.09.04.03-0 Transferência de Calor em Reatores	\N
2245	095	a	transferencia	3.09.04.03-0 Transferência de Calor em Reatores	\N
2246	095	a	de	3.09.04.03-0 Transferência de Calor em Reatores	\N
2247	095	a	calor	3.09.04.03-0 Transferência de Calor em Reatores	\N
2248	095	a	em	3.09.04.03-0 Transferência de Calor em Reatores	\N
2249	095	a	reatores	3.09.04.03-0 Transferência de Calor em Reatores	\N
2250	095	a	3.09.04.04-8	3.09.04.04-8 Geração e Integração Com Sistemas Elétricos em Reatores	\N
2251	095	a	geracao	3.09.04.04-8 Geração e Integração Com Sistemas Elétricos em Reatores	\N
2252	095	a	integracao	3.09.04.04-8 Geração e Integração Com Sistemas Elétricos em Reatores	\N
2253	095	a	com	3.09.04.04-8 Geração e Integração Com Sistemas Elétricos em Reatores	\N
2254	095	a	sistemas	3.09.04.04-8 Geração e Integração Com Sistemas Elétricos em Reatores	\N
2255	095	a	eletricos	3.09.04.04-8 Geração e Integração Com Sistemas Elétricos em Reatores	\N
2256	095	a	em	3.09.04.04-8 Geração e Integração Com Sistemas Elétricos em Reatores	\N
2257	095	a	reatores	3.09.04.04-8 Geração e Integração Com Sistemas Elétricos em Reatores	\N
2258	095	a	3.09.04.05-6	3.09.04.05-6 Instrumentação Para Operação e Controle de Reatores	\N
2259	095	a	instrumentacao	3.09.04.05-6 Instrumentação Para Operação e Controle de Reatores	\N
2260	095	a	para	3.09.04.05-6 Instrumentação Para Operação e Controle de Reatores	\N
2261	095	a	operacao	3.09.04.05-6 Instrumentação Para Operação e Controle de Reatores	\N
2262	095	a	controle	3.09.04.05-6 Instrumentação Para Operação e Controle de Reatores	\N
2263	095	a	de	3.09.04.05-6 Instrumentação Para Operação e Controle de Reatores	\N
2264	095	a	reatores	3.09.04.05-6 Instrumentação Para Operação e Controle de Reatores	\N
2265	095	a	3.09.04.06-4	3.09.04.06-4 Seguranca, Localização e Licênciamento de Reatores	\N
2266	095	a	seguranca,	3.09.04.06-4 Seguranca, Localização e Licênciamento de Reatores	\N
2267	095	a	localizacao	3.09.04.06-4 Seguranca, Localização e Licênciamento de Reatores	\N
2268	095	a	licenciamento	3.09.04.06-4 Seguranca, Localização e Licênciamento de Reatores	\N
2269	095	a	de	3.09.04.06-4 Seguranca, Localização e Licênciamento de Reatores	\N
2270	095	a	reatores	3.09.04.06-4 Seguranca, Localização e Licênciamento de Reatores	\N
2271	095	a	3.09.04.07-2	3.09.04.07-2 Aspectos Econômicos de Reatores	\N
2272	095	a	aspectos	3.09.04.07-2 Aspectos Econômicos de Reatores	\N
2273	095	a	economicos	3.09.04.07-2 Aspectos Econômicos de Reatores	\N
2274	095	a	de	3.09.04.07-2 Aspectos Econômicos de Reatores	\N
2275	095	a	reatores	3.09.04.07-2 Aspectos Econômicos de Reatores	\N
2276	095	a	3.10.00.00-2	3.10.00.00-2 Engenharia de Transportes	\N
2277	095	a	engenharia	3.10.00.00-2 Engenharia de Transportes	\N
2278	095	a	de	3.10.00.00-2 Engenharia de Transportes	\N
2279	095	a	transportes	3.10.00.00-2 Engenharia de Transportes	\N
2280	095	a	3.10.01.00-9	3.10.01.00-9 Planejamento de Transportes	\N
2281	095	a	planejamento	3.10.01.00-9 Planejamento de Transportes	\N
2282	095	a	de	3.10.01.00-9 Planejamento de Transportes	\N
2283	095	a	transportes	3.10.01.00-9 Planejamento de Transportes	\N
2284	095	a	3.10.01.01-7	3.10.01.01-7 Planejamento e Organização do Sistema de Transporte	\N
2285	095	a	planejamento	3.10.01.01-7 Planejamento e Organização do Sistema de Transporte	\N
2286	095	a	organizacao	3.10.01.01-7 Planejamento e Organização do Sistema de Transporte	\N
2287	095	a	do	3.10.01.01-7 Planejamento e Organização do Sistema de Transporte	\N
2288	095	a	sistema	3.10.01.01-7 Planejamento e Organização do Sistema de Transporte	\N
2289	095	a	de	3.10.01.01-7 Planejamento e Organização do Sistema de Transporte	\N
2290	095	a	transporte	3.10.01.01-7 Planejamento e Organização do Sistema de Transporte	\N
2291	095	a	3.10.01.02-5	3.10.01.02-5 Economia dos Transportes	\N
2292	095	a	economia	3.10.01.02-5 Economia dos Transportes	\N
2293	095	a	dos	3.10.01.02-5 Economia dos Transportes	\N
2294	095	a	transportes	3.10.01.02-5 Economia dos Transportes	\N
2295	095	a	3.10.02.00-5	3.10.02.00-5 Veículos e Equipamentos de Controle	\N
2296	095	a	veiculos	3.10.02.00-5 Veículos e Equipamentos de Controle	\N
2297	095	a	equipamentos	3.10.02.00-5 Veículos e Equipamentos de Controle	\N
2298	095	a	de	3.10.02.00-5 Veículos e Equipamentos de Controle	\N
2299	095	a	controle	3.10.02.00-5 Veículos e Equipamentos de Controle	\N
2300	095	a	3.10.02.01-3	3.10.02.01-3 Vias de Transporte	\N
2301	095	a	vias	3.10.02.01-3 Vias de Transporte	\N
2302	095	a	de	3.10.02.01-3 Vias de Transporte	\N
2303	095	a	transporte	3.10.02.01-3 Vias de Transporte	\N
2304	095	a	3.10.02.02-1	3.10.02.02-1 Veículos de Transportes	\N
2305	095	a	veiculos	3.10.02.02-1 Veículos de Transportes	\N
2306	095	a	de	3.10.02.02-1 Veículos de Transportes	\N
2307	095	a	transportes	3.10.02.02-1 Veículos de Transportes	\N
2308	095	a	3.10.02.03-0	3.10.02.03-0 Estação de Transporte	\N
2309	095	a	estacao	3.10.02.03-0 Estação de Transporte	\N
2310	095	a	de	3.10.02.03-0 Estação de Transporte	\N
2311	095	a	transporte	3.10.02.03-0 Estação de Transporte	\N
2312	095	a	3.10.02.04-8	3.10.02.04-8 Equipamentos Auxiliares e Controles	\N
2313	095	a	equipamentos	3.10.02.04-8 Equipamentos Auxiliares e Controles	\N
2314	095	a	auxiliares	3.10.02.04-8 Equipamentos Auxiliares e Controles	\N
2315	095	a	controles	3.10.02.04-8 Equipamentos Auxiliares e Controles	\N
2316	095	a	3.10.03.00-1	3.10.03.00-1 Operações de Transportes	\N
2317	095	a	operacoes	3.10.03.00-1 Operações de Transportes	\N
2318	095	a	de	3.10.03.00-1 Operações de Transportes	\N
2319	095	a	transportes	3.10.03.00-1 Operações de Transportes	\N
2320	095	a	3.10.03.01-0	3.10.03.01-0 Engenharia de Tráfego	\N
2321	095	a	engenharia	3.10.03.01-0 Engenharia de Tráfego	\N
2322	095	a	de	3.10.03.01-0 Engenharia de Tráfego	\N
2323	095	a	trafego	3.10.03.01-0 Engenharia de Tráfego	\N
2324	095	a	3.10.03.02-8	3.10.03.02-8 Capacidade de Vias de Transporte	\N
2325	095	a	capacidade	3.10.03.02-8 Capacidade de Vias de Transporte	\N
2326	095	a	de	3.10.03.02-8 Capacidade de Vias de Transporte	\N
2327	095	a	vias	3.10.03.02-8 Capacidade de Vias de Transporte	\N
2328	095	a	de	3.10.03.02-8 Capacidade de Vias de Transporte	\N
2329	095	a	transporte	3.10.03.02-8 Capacidade de Vias de Transporte	\N
2330	095	a	3.10.03.03-6	3.10.03.03-6 Operação de Sistemas de Transporte	\N
2331	095	a	operacao	3.10.03.03-6 Operação de Sistemas de Transporte	\N
2332	095	a	de	3.10.03.03-6 Operação de Sistemas de Transporte	\N
2333	095	a	sistemas	3.10.03.03-6 Operação de Sistemas de Transporte	\N
2334	095	a	de	3.10.03.03-6 Operação de Sistemas de Transporte	\N
2335	095	a	transporte	3.10.03.03-6 Operação de Sistemas de Transporte	\N
2336	095	a	3.11.00.00-7	3.11.00.00-7 Engenharia Naval e Oceânica	\N
2337	095	a	engenharia	3.11.00.00-7 Engenharia Naval e Oceânica	\N
2338	095	a	naval	3.11.00.00-7 Engenharia Naval e Oceânica	\N
2339	095	a	oceanica	3.11.00.00-7 Engenharia Naval e Oceânica	\N
2340	095	a	3.11.01.00-3	3.11.01.00-3 Hidrodinâmica de Navios e Sistemas Oceânicos	\N
2341	095	a	hidrodinamica	3.11.01.00-3 Hidrodinâmica de Navios e Sistemas Oceânicos	\N
2342	095	a	de	3.11.01.00-3 Hidrodinâmica de Navios e Sistemas Oceânicos	\N
2343	095	a	navios	3.11.01.00-3 Hidrodinâmica de Navios e Sistemas Oceânicos	\N
2344	095	a	sistemas	3.11.01.00-3 Hidrodinâmica de Navios e Sistemas Oceânicos	\N
2345	095	a	oceanicos	3.11.01.00-3 Hidrodinâmica de Navios e Sistemas Oceânicos	\N
2346	095	a	3.11.01.01-1	3.11.01.01-1 Resistência Hidrodinâmica	\N
2347	095	a	resistencia	3.11.01.01-1 Resistência Hidrodinâmica	\N
2348	095	a	hidrodinamica	3.11.01.01-1 Resistência Hidrodinâmica	\N
2349	095	a	3.11.01.02-0	3.11.01.02-0 Propulsão de Navios	\N
2350	095	a	propulsao	3.11.01.02-0 Propulsão de Navios	\N
2351	095	a	de	3.11.01.02-0 Propulsão de Navios	\N
2352	095	a	navios	3.11.01.02-0 Propulsão de Navios	\N
2353	095	a	3.11.02.00-0	3.11.02.00-0 Estruturas Navais e Oceânicas	\N
2354	095	a	estruturas	3.11.02.00-0 Estruturas Navais e Oceânicas	\N
2355	095	a	navais	3.11.02.00-0 Estruturas Navais e Oceânicas	\N
2356	095	a	oceanicas	3.11.02.00-0 Estruturas Navais e Oceânicas	\N
2357	095	a	3.11.02.01-8	3.11.02.01-8 Análise Teórica e Experimental de Estrutura	\N
2358	095	a	analise	3.11.02.01-8 Análise Teórica e Experimental de Estrutura	\N
2359	095	a	teorica	3.11.02.01-8 Análise Teórica e Experimental de Estrutura	\N
2360	095	a	experimental	3.11.02.01-8 Análise Teórica e Experimental de Estrutura	\N
2361	095	a	de	3.11.02.01-8 Análise Teórica e Experimental de Estrutura	\N
2362	095	a	estrutura	3.11.02.01-8 Análise Teórica e Experimental de Estrutura	\N
2363	095	a	3.11.02.02-6	3.11.02.02-6 Dinâmica Estrutural Naval e Oceânica	\N
2364	095	a	dinamica	3.11.02.02-6 Dinâmica Estrutural Naval e Oceânica	\N
2365	095	a	estrutural	3.11.02.02-6 Dinâmica Estrutural Naval e Oceânica	\N
2366	095	a	naval	3.11.02.02-6 Dinâmica Estrutural Naval e Oceânica	\N
2367	095	a	oceanica	3.11.02.02-6 Dinâmica Estrutural Naval e Oceânica	\N
2368	095	a	3.11.02.03-4	3.11.02.03-4 Síntese Estrutural Naval e Oceânica	\N
2369	095	a	sintese	3.11.02.03-4 Síntese Estrutural Naval e Oceânica	\N
2370	095	a	estrutural	3.11.02.03-4 Síntese Estrutural Naval e Oceânica	\N
2371	095	a	naval	3.11.02.03-4 Síntese Estrutural Naval e Oceânica	\N
2372	095	a	oceanica	3.11.02.03-4 Síntese Estrutural Naval e Oceânica	\N
2373	095	a	3.11.03.00-6	3.11.03.00-6 Máquinas Marítimas	\N
2374	095	a	maquinas	3.11.03.00-6 Máquinas Marítimas	\N
2375	095	a	maritimas	3.11.03.00-6 Máquinas Marítimas	\N
2376	095	a	3.11.03.01-4	3.11.03.01-4 Análise de Sistemas Propulsores	\N
2377	095	a	analise	3.11.03.01-4 Análise de Sistemas Propulsores	\N
2378	095	a	de	3.11.03.01-4 Análise de Sistemas Propulsores	\N
2379	095	a	sistemas	3.11.03.01-4 Análise de Sistemas Propulsores	\N
2380	095	a	propulsores	3.11.03.01-4 Análise de Sistemas Propulsores	\N
2381	095	a	3.11.03.02-2	3.11.03.02-2 Controle e Automação de Sistemas Propulsores	\N
2382	095	a	controle	3.11.03.02-2 Controle e Automação de Sistemas Propulsores	\N
2383	095	a	automacao	3.11.03.02-2 Controle e Automação de Sistemas Propulsores	\N
2384	095	a	de	3.11.03.02-2 Controle e Automação de Sistemas Propulsores	\N
2385	095	a	sistemas	3.11.03.02-2 Controle e Automação de Sistemas Propulsores	\N
2386	095	a	propulsores	3.11.03.02-2 Controle e Automação de Sistemas Propulsores	\N
2387	095	a	3.11.03.03-0	3.11.03.03-0 Equipamentos Auxiliares do Sistema Propulsivo	\N
2388	095	a	equipamentos	3.11.03.03-0 Equipamentos Auxiliares do Sistema Propulsivo	\N
2389	095	a	auxiliares	3.11.03.03-0 Equipamentos Auxiliares do Sistema Propulsivo	\N
2390	095	a	do	3.11.03.03-0 Equipamentos Auxiliares do Sistema Propulsivo	\N
2391	095	a	sistema	3.11.03.03-0 Equipamentos Auxiliares do Sistema Propulsivo	\N
2392	095	a	propulsivo	3.11.03.03-0 Equipamentos Auxiliares do Sistema Propulsivo	\N
2393	095	a	3.11.03.04-9	3.11.03.04-9 Motor de Propulsão	\N
2394	095	a	motor	3.11.03.04-9 Motor de Propulsão	\N
2395	095	a	de	3.11.03.04-9 Motor de Propulsão	\N
2396	095	a	propulsao	3.11.03.04-9 Motor de Propulsão	\N
2397	095	a	3.11.04.00-2	3.11.04.00-2 Projeto de Navios e de Sistemas Oceânicos	\N
2398	095	a	projeto	3.11.04.00-2 Projeto de Navios e de Sistemas Oceânicos	\N
2399	095	a	de	3.11.04.00-2 Projeto de Navios e de Sistemas Oceânicos	\N
2400	095	a	navios	3.11.04.00-2 Projeto de Navios e de Sistemas Oceânicos	\N
2401	095	a	de	3.11.04.00-2 Projeto de Navios e de Sistemas Oceânicos	\N
2402	095	a	sistemas	3.11.04.00-2 Projeto de Navios e de Sistemas Oceânicos	\N
2403	095	a	oceanicos	3.11.04.00-2 Projeto de Navios e de Sistemas Oceânicos	\N
2404	095	a	3.11.04.01-0	3.11.04.01-0 Projetos de Navios	\N
2405	095	a	projetos	3.11.04.01-0 Projetos de Navios	\N
2406	095	a	de	3.11.04.01-0 Projetos de Navios	\N
2407	095	a	navios	3.11.04.01-0 Projetos de Navios	\N
2408	095	a	3.11.04.02-9	3.11.04.02-9 Projetos de Sistemas Oceânicos Fixos e Semi-Fixos	\N
2409	095	a	projetos	3.11.04.02-9 Projetos de Sistemas Oceânicos Fixos e Semi-Fixos	\N
2410	095	a	de	3.11.04.02-9 Projetos de Sistemas Oceânicos Fixos e Semi-Fixos	\N
2411	095	a	sistemas	3.11.04.02-9 Projetos de Sistemas Oceânicos Fixos e Semi-Fixos	\N
2412	095	a	oceanicos	3.11.04.02-9 Projetos de Sistemas Oceânicos Fixos e Semi-Fixos	\N
2413	095	a	fixos	3.11.04.02-9 Projetos de Sistemas Oceânicos Fixos e Semi-Fixos	\N
2414	095	a	semi-fixos	3.11.04.02-9 Projetos de Sistemas Oceânicos Fixos e Semi-Fixos	\N
2415	095	a	3.11.04.03-7	3.11.04.03-7 Projetos de Embarcações Não-Convencionais	\N
2416	095	a	projetos	3.11.04.03-7 Projetos de Embarcações Não-Convencionais	\N
2417	095	a	de	3.11.04.03-7 Projetos de Embarcações Não-Convencionais	\N
2418	095	a	embarcacoes	3.11.04.03-7 Projetos de Embarcações Não-Convencionais	\N
2419	095	a	nao-convencionais	3.11.04.03-7 Projetos de Embarcações Não-Convencionais	\N
2420	095	a	3.11.05.00-9	3.11.05.00-9 Tecnologia de Construção Naval e de Sistemas Oceânicas	\N
2421	095	a	tecnologia	3.11.05.00-9 Tecnologia de Construção Naval e de Sistemas Oceânicas	\N
2422	095	a	de	3.11.05.00-9 Tecnologia de Construção Naval e de Sistemas Oceânicas	\N
2423	095	a	construcao	3.11.05.00-9 Tecnologia de Construção Naval e de Sistemas Oceânicas	\N
2424	095	a	naval	3.11.05.00-9 Tecnologia de Construção Naval e de Sistemas Oceânicas	\N
2425	095	a	de	3.11.05.00-9 Tecnologia de Construção Naval e de Sistemas Oceânicas	\N
2426	095	a	sistemas	3.11.05.00-9 Tecnologia de Construção Naval e de Sistemas Oceânicas	\N
2427	095	a	oceanicas	3.11.05.00-9 Tecnologia de Construção Naval e de Sistemas Oceânicas	\N
2428	095	a	3.11.05.01-7	3.11.05.01-7 Métodos de Fabricação de Navios e Sistemas Oceânicos	\N
2429	095	a	metodos	3.11.05.01-7 Métodos de Fabricação de Navios e Sistemas Oceânicos	\N
2430	095	a	de	3.11.05.01-7 Métodos de Fabricação de Navios e Sistemas Oceânicos	\N
2431	095	a	fabricacao	3.11.05.01-7 Métodos de Fabricação de Navios e Sistemas Oceânicos	\N
2432	095	a	de	3.11.05.01-7 Métodos de Fabricação de Navios e Sistemas Oceânicos	\N
2433	095	a	navios	3.11.05.01-7 Métodos de Fabricação de Navios e Sistemas Oceânicos	\N
2434	095	a	sistemas	3.11.05.01-7 Métodos de Fabricação de Navios e Sistemas Oceânicos	\N
2435	095	a	oceanicos	3.11.05.01-7 Métodos de Fabricação de Navios e Sistemas Oceânicos	\N
2436	095	a	3.11.05.02-5	3.11.05.02-5 Soldagem de Estruturas Navais e Oceânicos	\N
2437	095	a	soldagem	3.11.05.02-5 Soldagem de Estruturas Navais e Oceânicos	\N
2438	095	a	de	3.11.05.02-5 Soldagem de Estruturas Navais e Oceânicos	\N
2439	095	a	estruturas	3.11.05.02-5 Soldagem de Estruturas Navais e Oceânicos	\N
2440	095	a	navais	3.11.05.02-5 Soldagem de Estruturas Navais e Oceânicos	\N
2441	095	a	oceanicos	3.11.05.02-5 Soldagem de Estruturas Navais e Oceânicos	\N
2442	095	a	3.11.05.03-3	3.11.05.03-3 Custos de Construção Naval	\N
2443	095	a	custos	3.11.05.03-3 Custos de Construção Naval	\N
2444	095	a	de	3.11.05.03-3 Custos de Construção Naval	\N
2445	095	a	construcao	3.11.05.03-3 Custos de Construção Naval	\N
2446	095	a	naval	3.11.05.03-3 Custos de Construção Naval	\N
2447	095	a	3.11.05.04-1	3.11.05.04-1 Normatização e Certificação de Qualidade de Navios	\N
2448	095	a	normatizacao	3.11.05.04-1 Normatização e Certificação de Qualidade de Navios	\N
2449	095	a	certificacao	3.11.05.04-1 Normatização e Certificação de Qualidade de Navios	\N
2450	095	a	de	3.11.05.04-1 Normatização e Certificação de Qualidade de Navios	\N
2451	095	a	qualidade	3.11.05.04-1 Normatização e Certificação de Qualidade de Navios	\N
2452	095	a	de	3.11.05.04-1 Normatização e Certificação de Qualidade de Navios	\N
2453	095	a	navios	3.11.05.04-1 Normatização e Certificação de Qualidade de Navios	\N
2454	095	a	3.12.00.00-1	3.12.00.00-1 Engenharia Aeroespacial	\N
2455	095	a	engenharia	3.12.00.00-1 Engenharia Aeroespacial	\N
2456	095	a	aeroespacial	3.12.00.00-1 Engenharia Aeroespacial	\N
2457	095	a	3.12.01.00-8	3.12.01.00-8 Aerodinâmica	\N
2458	095	a	aerodinamica	3.12.01.00-8 Aerodinâmica	\N
2459	095	a	3.12.01.01-6	3.12.01.01-6 Aerodinâmica de Aeronaves Espaciais	\N
2460	095	a	aerodinamica	3.12.01.01-6 Aerodinâmica de Aeronaves Espaciais	\N
2461	095	a	de	3.12.01.01-6 Aerodinâmica de Aeronaves Espaciais	\N
2462	095	a	aeronaves	3.12.01.01-6 Aerodinâmica de Aeronaves Espaciais	\N
2463	095	a	espaciais	3.12.01.01-6 Aerodinâmica de Aeronaves Espaciais	\N
2464	095	a	3.12.01.02-4	3.12.01.02-4 Aerodinâmica dos Processos Geofísicos e Interplanetarios	\N
2465	095	a	aerodinamica	3.12.01.02-4 Aerodinâmica dos Processos Geofísicos e Interplanetarios	\N
2466	095	a	dos	3.12.01.02-4 Aerodinâmica dos Processos Geofísicos e Interplanetarios	\N
2467	095	a	processos	3.12.01.02-4 Aerodinâmica dos Processos Geofísicos e Interplanetarios	\N
2468	095	a	geofisicos	3.12.01.02-4 Aerodinâmica dos Processos Geofísicos e Interplanetarios	\N
2469	095	a	interplanetarios	3.12.01.02-4 Aerodinâmica dos Processos Geofísicos e Interplanetarios	\N
2470	095	a	3.12.02.00-4	3.12.02.00-4 Dinâmica de Vôo	\N
2471	095	a	dinamica	3.12.02.00-4 Dinâmica de Vôo	\N
2472	095	a	de	3.12.02.00-4 Dinâmica de Vôo	\N
2473	095	a	voo	3.12.02.00-4 Dinâmica de Vôo	\N
2474	095	a	3.12.02.01-2	3.12.02.01-2 Trajetorias e Orbitas	\N
2475	095	a	trajetorias	3.12.02.01-2 Trajetorias e Orbitas	\N
2476	095	a	orbitas	3.12.02.01-2 Trajetorias e Orbitas	\N
2477	095	a	3.12.02.02-0	3.12.02.02-0 Estabilidade e Controle	\N
2478	095	a	estabilidade	3.12.02.02-0 Estabilidade e Controle	\N
2479	095	a	controle	3.12.02.02-0 Estabilidade e Controle	\N
2480	095	a	3.12.03.00-0	3.12.03.00-0 Estruturas Aeroespaciais	\N
2481	095	a	estruturas	3.12.03.00-0 Estruturas Aeroespaciais	\N
2482	095	a	aeroespaciais	3.12.03.00-0 Estruturas Aeroespaciais	\N
2483	095	a	3.12.03.01-9	3.12.03.01-9 Aeroelasticidade	\N
2484	095	a	aeroelasticidade	3.12.03.01-9 Aeroelasticidade	\N
2485	095	a	3.12.03.02-7	3.12.03.02-7 Fadiga	\N
2486	095	a	fadiga	3.12.03.02-7 Fadiga	\N
2487	095	a	3.12.03.03-5	3.12.03.03-5 Projeto de Estruturas Aeroespaciais	\N
2488	095	a	projeto	3.12.03.03-5 Projeto de Estruturas Aeroespaciais	\N
2489	095	a	de	3.12.03.03-5 Projeto de Estruturas Aeroespaciais	\N
2490	095	a	estruturas	3.12.03.03-5 Projeto de Estruturas Aeroespaciais	\N
2491	095	a	aeroespaciais	3.12.03.03-5 Projeto de Estruturas Aeroespaciais	\N
2492	095	a	3.12.04.00-7	3.12.04.00-7 Materiais e Processos para Engenharia Aeronáutica e Aeroespacial	\N
2493	095	a	materiais	3.12.04.00-7 Materiais e Processos para Engenharia Aeronáutica e Aeroespacial	\N
2494	095	a	processos	3.12.04.00-7 Materiais e Processos para Engenharia Aeronáutica e Aeroespacial	\N
2495	095	a	para	3.12.04.00-7 Materiais e Processos para Engenharia Aeronáutica e Aeroespacial	\N
2496	095	a	engenharia	3.12.04.00-7 Materiais e Processos para Engenharia Aeronáutica e Aeroespacial	\N
2497	095	a	aeronautica	3.12.04.00-7 Materiais e Processos para Engenharia Aeronáutica e Aeroespacial	\N
2498	095	a	aeroespacial	3.12.04.00-7 Materiais e Processos para Engenharia Aeronáutica e Aeroespacial	\N
2499	095	a	3.12.05.00-3	3.12.05.00-3 Propulsão Aeroespacial	\N
2500	095	a	propulsao	3.12.05.00-3 Propulsão Aeroespacial	\N
2501	095	a	aeroespacial	3.12.05.00-3 Propulsão Aeroespacial	\N
2502	095	a	3.12.05.01-1	3.12.05.01-1 Combustão e Escoamento com Reações Químicas	\N
2503	095	a	combustao	3.12.05.01-1 Combustão e Escoamento com Reações Químicas	\N
2504	095	a	escoamento	3.12.05.01-1 Combustão e Escoamento com Reações Químicas	\N
2505	095	a	com	3.12.05.01-1 Combustão e Escoamento com Reações Químicas	\N
2506	095	a	reacoes	3.12.05.01-1 Combustão e Escoamento com Reações Químicas	\N
2507	095	a	quimicas	3.12.05.01-1 Combustão e Escoamento com Reações Químicas	\N
2508	095	a	3.12.05.02-0	3.12.05.02-0 Propulsão de Foguetes	\N
2509	095	a	propulsao	3.12.05.02-0 Propulsão de Foguetes	\N
2510	095	a	de	3.12.05.02-0 Propulsão de Foguetes	\N
2511	095	a	foguetes	3.12.05.02-0 Propulsão de Foguetes	\N
2512	095	a	3.12.05.03-8	3.12.05.03-8 Máquinas de Fluxo	\N
2513	095	a	maquinas	3.12.05.03-8 Máquinas de Fluxo	\N
2514	095	a	de	3.12.05.03-8 Máquinas de Fluxo	\N
2515	095	a	fluxo	3.12.05.03-8 Máquinas de Fluxo	\N
2516	095	a	3.12.05.04-6	3.12.05.04-6 Motores Alternativos	\N
2517	095	a	motores	3.12.05.04-6 Motores Alternativos	\N
2518	095	a	alternativos	3.12.05.04-6 Motores Alternativos	\N
2519	095	a	3.12.06.00-0	3.12.06.00-0 Sistemas Aeroespaciais	\N
2520	095	a	sistemas	3.12.06.00-0 Sistemas Aeroespaciais	\N
2521	095	a	aeroespaciais	3.12.06.00-0 Sistemas Aeroespaciais	\N
2522	095	a	3.12.06.01-8	3.12.06.01-8 Aviões	\N
2523	095	a	avioes	3.12.06.01-8 Aviões	\N
2524	095	a	3.12.06.02-6	3.12.06.02-6 Foguetes	\N
2525	095	a	foguetes	3.12.06.02-6 Foguetes	\N
2526	095	a	3.12.06.03-4	3.12.06.03-4 Helicópteros	\N
2527	095	a	helicopteros	3.12.06.03-4 Helicópteros	\N
2528	095	a	3.12.06.04-2	3.12.06.04-2 Hovercraft	\N
2529	095	a	hovercraft	3.12.06.04-2 Hovercraft	\N
2530	095	a	3.12.06.05-0	3.12.06.05-0 Satélites e Outros Dispositivos Aeroespaciais	\N
2531	095	a	satelites	3.12.06.05-0 Satélites e Outros Dispositivos Aeroespaciais	\N
2532	095	a	outros	3.12.06.05-0 Satélites e Outros Dispositivos Aeroespaciais	\N
2533	095	a	dispositivos	3.12.06.05-0 Satélites e Outros Dispositivos Aeroespaciais	\N
2534	095	a	aeroespaciais	3.12.06.05-0 Satélites e Outros Dispositivos Aeroespaciais	\N
2535	095	a	3.12.06.06-9	3.12.06.06-9 Normatização e Certificação de Qualidade de Aeronaves e Componentes	\N
2536	095	a	normatizacao	3.12.06.06-9 Normatização e Certificação de Qualidade de Aeronaves e Componentes	\N
2537	095	a	certificacao	3.12.06.06-9 Normatização e Certificação de Qualidade de Aeronaves e Componentes	\N
2538	095	a	de	3.12.06.06-9 Normatização e Certificação de Qualidade de Aeronaves e Componentes	\N
2539	095	a	qualidade	3.12.06.06-9 Normatização e Certificação de Qualidade de Aeronaves e Componentes	\N
2540	095	a	de	3.12.06.06-9 Normatização e Certificação de Qualidade de Aeronaves e Componentes	\N
2541	095	a	aeronaves	3.12.06.06-9 Normatização e Certificação de Qualidade de Aeronaves e Componentes	\N
2542	095	a	componentes	3.12.06.06-9 Normatização e Certificação de Qualidade de Aeronaves e Componentes	\N
2543	095	a	3.12.06.07-7	3.12.06.07-7 Manutenção de Sistemas Aeroespaciais	\N
2544	095	a	manutencao	3.12.06.07-7 Manutenção de Sistemas Aeroespaciais	\N
2545	095	a	de	3.12.06.07-7 Manutenção de Sistemas Aeroespaciais	\N
2546	095	a	sistemas	3.12.06.07-7 Manutenção de Sistemas Aeroespaciais	\N
2547	095	a	aeroespaciais	3.12.06.07-7 Manutenção de Sistemas Aeroespaciais	\N
2548	095	a	3.13.00.00-6	3.13.00.00-6 Engenharia Biomédica	\N
2549	095	a	engenharia	3.13.00.00-6 Engenharia Biomédica	\N
2550	095	a	biomedica	3.13.00.00-6 Engenharia Biomédica	\N
2551	095	a	3.13.01.00-2	3.13.01.00-2 Bioengenharia	\N
2552	095	a	bioengenharia	3.13.01.00-2 Bioengenharia	\N
2553	095	a	3.13.01.01-0	3.13.01.01-0 Processamento de Sinais Biológicos	\N
2554	095	a	processamento	3.13.01.01-0 Processamento de Sinais Biológicos	\N
2555	095	a	de	3.13.01.01-0 Processamento de Sinais Biológicos	\N
2556	095	a	sinais	3.13.01.01-0 Processamento de Sinais Biológicos	\N
2557	095	a	biologicos	3.13.01.01-0 Processamento de Sinais Biológicos	\N
2558	095	a	3.13.01.02-9	3.13.01.02-9 Modelagem de Fenomenos Biológicos	\N
2559	095	a	modelagem	3.13.01.02-9 Modelagem de Fenomenos Biológicos	\N
2560	095	a	de	3.13.01.02-9 Modelagem de Fenomenos Biológicos	\N
2561	095	a	fenomenos	3.13.01.02-9 Modelagem de Fenomenos Biológicos	\N
2562	095	a	biologicos	3.13.01.02-9 Modelagem de Fenomenos Biológicos	\N
2563	095	a	3.13.01.03-7	3.13.01.03-7 Modelagem de Sistemas Biológicos	\N
2564	095	a	modelagem	3.13.01.03-7 Modelagem de Sistemas Biológicos	\N
2565	095	a	de	3.13.01.03-7 Modelagem de Sistemas Biológicos	\N
2566	095	a	sistemas	3.13.01.03-7 Modelagem de Sistemas Biológicos	\N
2567	095	a	biologicos	3.13.01.03-7 Modelagem de Sistemas Biológicos	\N
2568	095	a	3.13.02.00-9	3.13.02.00-9 Engenharia Médica	\N
2569	095	a	engenharia	3.13.02.00-9 Engenharia Médica	\N
2570	095	a	medica	3.13.02.00-9 Engenharia Médica	\N
2571	095	a	3.13.02.01-7	3.13.02.01-7 Biomateriais e Materiais Biocompatíveis	\N
2572	095	a	biomateriais	3.13.02.01-7 Biomateriais e Materiais Biocompatíveis	\N
2573	095	a	materiais	3.13.02.01-7 Biomateriais e Materiais Biocompatíveis	\N
2574	095	a	biocompativeis	3.13.02.01-7 Biomateriais e Materiais Biocompatíveis	\N
2575	095	a	3.13.02.02-5	3.13.02.02-5 Transdutores para Aplicações Biomédicas	\N
2576	095	a	transdutores	3.13.02.02-5 Transdutores para Aplicações Biomédicas	\N
2577	095	a	para	3.13.02.02-5 Transdutores para Aplicações Biomédicas	\N
2578	095	a	aplicacoes	3.13.02.02-5 Transdutores para Aplicações Biomédicas	\N
2579	095	a	biomedicas	3.13.02.02-5 Transdutores para Aplicações Biomédicas	\N
2580	095	a	3.13.02.03-3	3.13.02.03-3 Instrumentação Odontológica e Médico-Hospitalar	\N
2581	095	a	instrumentacao	3.13.02.03-3 Instrumentação Odontológica e Médico-Hospitalar	\N
2582	095	a	odontologica	3.13.02.03-3 Instrumentação Odontológica e Médico-Hospitalar	\N
2583	095	a	medico-hospitalar	3.13.02.03-3 Instrumentação Odontológica e Médico-Hospitalar	\N
2584	095	a	3.13.02.04-1	3.13.02.04-1 Tecnologia de Próteses	\N
2585	095	a	tecnologia	3.13.02.04-1 Tecnologia de Próteses	\N
2586	095	a	de	3.13.02.04-1 Tecnologia de Próteses	\N
2587	095	a	proteses	3.13.02.04-1 Tecnologia de Próteses	\N
2588	095	a	4.00.00.00-1	4.00.00.00-1 Ciências da Saúde	\N
2589	095	a	ciencias	4.00.00.00-1 Ciências da Saúde	\N
2590	095	a	da	4.00.00.00-1 Ciências da Saúde	\N
2591	095	a	saude	4.00.00.00-1 Ciências da Saúde	\N
2592	095	a	4.01.00.00-6	4.01.00.00-6 Medicina	\N
2593	095	a	medicina	4.01.00.00-6 Medicina	\N
2594	095	a	4.01.01.00-2	4.01.01.00-2 Clínica Médica	\N
2595	095	a	clinica	4.01.01.00-2 Clínica Médica	\N
2596	095	a	medica	4.01.01.00-2 Clínica Médica	\N
2597	095	a	4.01.01.01-0	4.01.01.01-0 Angiologia	\N
2598	095	a	angiologia	4.01.01.01-0 Angiologia	\N
2599	095	a	4.01.01.02-9	4.01.01.02-9 Dermatologia	\N
2600	095	a	dermatologia	4.01.01.02-9 Dermatologia	\N
2601	095	a	4.01.01.03-7	4.01.01.03-7 Alergologia e Imunologia Clínica	\N
2602	095	a	alergologia	4.01.01.03-7 Alergologia e Imunologia Clínica	\N
2603	095	a	imunologia	4.01.01.03-7 Alergologia e Imunologia Clínica	\N
2604	095	a	clinica	4.01.01.03-7 Alergologia e Imunologia Clínica	\N
2605	095	a	4.01.01.04-5	4.01.01.04-5 Cancerologia	\N
2606	095	a	cancerologia	4.01.01.04-5 Cancerologia	\N
2607	095	a	4.01.01.05-3	4.01.01.05-3 Hematologia	\N
2608	095	a	hematologia	4.01.01.05-3 Hematologia	\N
2609	095	a	4.01.01.06-1	4.01.01.06-1 Endocrinologia	\N
2610	095	a	endocrinologia	4.01.01.06-1 Endocrinologia	\N
2611	095	a	4.01.01.07-0	4.01.01.07-0 Neurologia	\N
2612	095	a	neurologia	4.01.01.07-0 Neurologia	\N
2613	095	a	4.01.01.08-8	4.01.01.08-8 Pediatria	\N
2614	095	a	pediatria	4.01.01.08-8 Pediatria	\N
2615	095	a	4.01.01.09-6	4.01.01.09-6 Doenças Infecciosas e Parasitárias	\N
2616	095	a	doencas	4.01.01.09-6 Doenças Infecciosas e Parasitárias	\N
2617	095	a	infecciosas	4.01.01.09-6 Doenças Infecciosas e Parasitárias	\N
2618	095	a	parasitarias	4.01.01.09-6 Doenças Infecciosas e Parasitárias	\N
2619	095	a	4.01.01.10-0	4.01.01.10-0 Cardiologia	\N
2620	095	a	cardiologia	4.01.01.10-0 Cardiologia	\N
2621	095	a	4.01.01.11-8	4.01.01.11-8 Gastroenterologia	\N
2622	095	a	gastroenterologia	4.01.01.11-8 Gastroenterologia	\N
2623	095	a	4.01.01.12-6	4.01.01.12-6 Pneumologia	\N
2624	095	a	pneumologia	4.01.01.12-6 Pneumologia	\N
2625	095	a	4.01.01.13-4	4.01.01.13-4 Nefrologia	\N
2626	095	a	nefrologia	4.01.01.13-4 Nefrologia	\N
2627	095	a	4.01.01.14-2	4.01.01.14-2 Reumatologia	\N
2628	095	a	reumatologia	4.01.01.14-2 Reumatologia	\N
2629	095	a	4.01.01.15-0	4.01.01.15-0 Ginecologia e Obstetrícia	\N
2630	095	a	ginecologia	4.01.01.15-0 Ginecologia e Obstetrícia	\N
2631	095	a	obstetricia	4.01.01.15-0 Ginecologia e Obstetrícia	\N
2632	095	a	4.01.01.16-9	4.01.01.16-9 Fisiatria	\N
2633	095	a	fisiatria	4.01.01.16-9 Fisiatria	\N
2634	095	a	4.01.01.17-7	4.01.01.17-7 Oftalmologia	\N
2635	095	a	oftalmologia	4.01.01.17-7 Oftalmologia	\N
2636	095	a	4.01.01.18-6	4.01.01.18-6 Ortopedia	\N
2637	095	a	ortopedia	4.01.01.18-6 Ortopedia	\N
2638	095	a	4.01.02.00-9	4.01.02.00-9 Cirurgia	\N
2639	095	a	cirurgia	4.01.02.00-9 Cirurgia	\N
2640	095	a	4.01.02.01-7	4.01.02.01-7 Cirurgia Plástica e Restauradora	\N
2641	095	a	cirurgia	4.01.02.01-7 Cirurgia Plástica e Restauradora	\N
2642	095	a	plastica	4.01.02.01-7 Cirurgia Plástica e Restauradora	\N
2643	095	a	restauradora	4.01.02.01-7 Cirurgia Plástica e Restauradora	\N
2644	095	a	4.01.02.02-5	4.01.02.02-5 Cirurgia Otorrinolaringológica	\N
2645	095	a	cirurgia	4.01.02.02-5 Cirurgia Otorrinolaringológica	\N
2646	095	a	otorrinolaringologica	4.01.02.02-5 Cirurgia Otorrinolaringológica	\N
2647	095	a	4.01.02.03-3	4.01.02.03-3 Cirurgia Oftalmológica	\N
2648	095	a	cirurgia	4.01.02.03-3 Cirurgia Oftalmológica	\N
2649	095	a	oftalmologica	4.01.02.03-3 Cirurgia Oftalmológica	\N
2650	095	a	4.01.02.04-1	4.01.02.04-1 Cirurgia Cardiovascular	\N
2651	095	a	cirurgia	4.01.02.04-1 Cirurgia Cardiovascular	\N
2652	095	a	cardiovascular	4.01.02.04-1 Cirurgia Cardiovascular	\N
2653	095	a	4.01.02.05-0	4.01.02.05-0 Cirurgia Toráxica	\N
2654	095	a	cirurgia	4.01.02.05-0 Cirurgia Toráxica	\N
2655	095	a	toraxica	4.01.02.05-0 Cirurgia Toráxica	\N
2656	095	a	4.01.02.06-8	4.01.02.06-8 Cirurgia Gastroenterologia	\N
2657	095	a	cirurgia	4.01.02.06-8 Cirurgia Gastroenterologia	\N
2658	095	a	gastroenterologia	4.01.02.06-8 Cirurgia Gastroenterologia	\N
2659	095	a	4.01.02.07-6	4.01.02.07-6 Cirurgia Pediátrica	\N
2660	095	a	cirurgia	4.01.02.07-6 Cirurgia Pediátrica	\N
2661	095	a	pediatrica	4.01.02.07-6 Cirurgia Pediátrica	\N
2662	095	a	4.01.02.08-4	4.01.02.08-4 Neurocirurgia	\N
2663	095	a	neurocirurgia	4.01.02.08-4 Neurocirurgia	\N
2664	095	a	4.01.02.09-2	4.01.02.09-2 Cirurgia Urológica	\N
2665	095	a	cirurgia	4.01.02.09-2 Cirurgia Urológica	\N
2666	095	a	urologica	4.01.02.09-2 Cirurgia Urológica	\N
2667	095	a	4.01.02.10-6	4.01.02.10-6 Cirurgia Proctológica	\N
2668	095	a	cirurgia	4.01.02.10-6 Cirurgia Proctológica	\N
2669	095	a	proctologica	4.01.02.10-6 Cirurgia Proctológica	\N
2670	095	a	4.01.02.11-4	4.01.02.11-4 Cirurgia Ortopédica	\N
2671	095	a	cirurgia	4.01.02.11-4 Cirurgia Ortopédica	\N
2672	095	a	ortopedica	4.01.02.11-4 Cirurgia Ortopédica	\N
2673	095	a	4.01.02.12-2	4.01.02.12-2 Cirurgia Traumatológica	\N
2674	095	a	cirurgia	4.01.02.12-2 Cirurgia Traumatológica	\N
2675	095	a	traumatologica	4.01.02.12-2 Cirurgia Traumatológica	\N
2676	095	a	4.01.02.13-0	4.01.02.13-0 Anestesiologia	\N
2677	095	a	anestesiologia	4.01.02.13-0 Anestesiologia	\N
2678	095	a	4.01.02.14-9	4.01.02.14-9 Cirurgia Experimental	\N
2679	095	a	cirurgia	4.01.02.14-9 Cirurgia Experimental	\N
2680	095	a	experimental	4.01.02.14-9 Cirurgia Experimental	\N
2681	095	a	4.01.03.00-5	4.01.03.00-5 Saúde Materno-Infantil	\N
2682	095	a	saude	4.01.03.00-5 Saúde Materno-Infantil	\N
2683	095	a	materno-infantil	4.01.03.00-5 Saúde Materno-Infantil	\N
2684	095	a	4.01.04.00-1	4.01.04.00-1 Psiquiatria	\N
2685	095	a	psiquiatria	4.01.04.00-1 Psiquiatria	\N
2686	095	a	4.01.05.00-8	4.01.05.00-8 Anatomia Patológica e Patologia Clínica	\N
2687	095	a	anatomia	4.01.05.00-8 Anatomia Patológica e Patologia Clínica	\N
2688	095	a	patologica	4.01.05.00-8 Anatomia Patológica e Patologia Clínica	\N
2689	095	a	patologia	4.01.05.00-8 Anatomia Patológica e Patologia Clínica	\N
2690	095	a	clinica	4.01.05.00-8 Anatomia Patológica e Patologia Clínica	\N
2691	095	a	4.01.06.00-4	4.01.06.00-4 Radiologia Médica	\N
2692	095	a	radiologia	4.01.06.00-4 Radiologia Médica	\N
2693	095	a	medica	4.01.06.00-4 Radiologia Médica	\N
2694	095	a	4.01.07.00-0	4.01.07.00-0 Medicina Legal e Deontologia	\N
2695	095	a	medicina	4.01.07.00-0 Medicina Legal e Deontologia	\N
2696	095	a	legal	4.01.07.00-0 Medicina Legal e Deontologia	\N
2697	095	a	deontologia	4.01.07.00-0 Medicina Legal e Deontologia	\N
2698	095	a	4.02.00.00-0	4.02.00.00-0 Odontologia	\N
2699	095	a	odontologia	4.02.00.00-0 Odontologia	\N
2700	095	a	4.02.01.00-7	4.02.01.00-7 Clínica Odontológica	\N
2701	095	a	clinica	4.02.01.00-7 Clínica Odontológica	\N
2702	095	a	odontologica	4.02.01.00-7 Clínica Odontológica	\N
2703	095	a	4.02.02.00-3	4.02.02.00-3 Cirurgia Buco-Maxilo-Facial	\N
2704	095	a	cirurgia	4.02.02.00-3 Cirurgia Buco-Maxilo-Facial	\N
2705	095	a	buco-maxilo-facial	4.02.02.00-3 Cirurgia Buco-Maxilo-Facial	\N
2706	095	a	4.02.03.00-0	4.02.03.00-0 Ortodontia	\N
2707	095	a	ortodontia	4.02.03.00-0 Ortodontia	\N
2708	095	a	4.02.04.00-6	4.02.04.00-6 Odontopediatria	\N
2709	095	a	odontopediatria	4.02.04.00-6 Odontopediatria	\N
2710	095	a	4.02.05.00-2	4.02.05.00-2 Periodontia	\N
2711	095	a	periodontia	4.02.05.00-2 Periodontia	\N
2712	095	a	4.02.06.00-9	4.02.06.00-9 Endodontia	\N
2713	095	a	endodontia	4.02.06.00-9 Endodontia	\N
2714	095	a	4.02.07.00-5	4.02.07.00-5 Radiologia Odontológica	\N
2715	095	a	radiologia	4.02.07.00-5 Radiologia Odontológica	\N
2716	095	a	odontologica	4.02.07.00-5 Radiologia Odontológica	\N
2717	095	a	4.02.08.00-1	4.02.08.00-1 Odontologia Social e Preventiva	\N
2718	095	a	odontologia	4.02.08.00-1 Odontologia Social e Preventiva	\N
2719	095	a	social	4.02.08.00-1 Odontologia Social e Preventiva	\N
2720	095	a	preventiva	4.02.08.00-1 Odontologia Social e Preventiva	\N
2721	095	a	4.02.09.00-8	4.02.09.00-8 Materiais Odontológicos	\N
2722	095	a	materiais	4.02.09.00-8 Materiais Odontológicos	\N
2723	095	a	odontologicos	4.02.09.00-8 Materiais Odontológicos	\N
2724	095	a	4.03.00.00-5	4.03.00.00-5 Farmácia	\N
2725	095	a	farmacia	4.03.00.00-5 Farmácia	\N
2726	095	a	4.03.01.00-1	4.03.01.00-1 Farmacotecnia	\N
2727	095	a	farmacotecnia	4.03.01.00-1 Farmacotecnia	\N
2728	095	a	4.03.02.00-8	4.03.02.00-8 Farmacognosia	\N
2729	095	a	farmacognosia	4.03.02.00-8 Farmacognosia	\N
2730	095	a	4.03.03.00-4	4.03.03.00-4 Análise Toxicológica	\N
2731	095	a	analise	4.03.03.00-4 Análise Toxicológica	\N
2732	095	a	toxicologica	4.03.03.00-4 Análise Toxicológica	\N
2733	095	a	4.03.04.00-0	4.03.04.00-0 Análise e Controle e Medicamentos	\N
2734	095	a	analise	4.03.04.00-0 Análise e Controle e Medicamentos	\N
2735	095	a	controle	4.03.04.00-0 Análise e Controle e Medicamentos	\N
2736	095	a	medicamentos	4.03.04.00-0 Análise e Controle e Medicamentos	\N
2737	095	a	4.03.05.00-7	4.03.05.00-7 Bromatologia	\N
2738	095	a	bromatologia	4.03.05.00-7 Bromatologia	\N
2739	095	a	4.04.00.00-0	4.04.00.00-0 Enfermagem	\N
2740	095	a	enfermagem	4.04.00.00-0 Enfermagem	\N
2741	095	a	4.04.01.00-6	4.04.01.00-6 Enfermagem Médico-Cirúrgica	\N
2742	095	a	enfermagem	4.04.01.00-6 Enfermagem Médico-Cirúrgica	\N
2743	095	a	medico-cirurgica	4.04.01.00-6 Enfermagem Médico-Cirúrgica	\N
2744	095	a	4.04.02.00-2	4.04.02.00-2 Enfermagem Obstétrica	\N
2745	095	a	enfermagem	4.04.02.00-2 Enfermagem Obstétrica	\N
2746	095	a	obstetrica	4.04.02.00-2 Enfermagem Obstétrica	\N
2747	095	a	4.04.03.00-9	4.04.03.00-9 Enfermagem Pediátrica	\N
2748	095	a	enfermagem	4.04.03.00-9 Enfermagem Pediátrica	\N
2749	095	a	pediatrica	4.04.03.00-9 Enfermagem Pediátrica	\N
2750	095	a	4.04.04.00-5	4.04.04.00-5 Enfermagem Psiquiátrica	\N
2751	095	a	enfermagem	4.04.04.00-5 Enfermagem Psiquiátrica	\N
2752	095	a	psiquiatrica	4.04.04.00-5 Enfermagem Psiquiátrica	\N
2753	095	a	4.04.05.00-1	4.04.05.00-1 Enfermagem de Doenças Contagiosas	\N
2754	095	a	enfermagem	4.04.05.00-1 Enfermagem de Doenças Contagiosas	\N
2755	095	a	de	4.04.05.00-1 Enfermagem de Doenças Contagiosas	\N
2756	095	a	doencas	4.04.05.00-1 Enfermagem de Doenças Contagiosas	\N
2757	095	a	contagiosas	4.04.05.00-1 Enfermagem de Doenças Contagiosas	\N
2758	095	a	4.04.06.00-8	4.04.06.00-8 Enfermagem de Saúde Pública	\N
2759	095	a	enfermagem	4.04.06.00-8 Enfermagem de Saúde Pública	\N
2760	095	a	de	4.04.06.00-8 Enfermagem de Saúde Pública	\N
2761	095	a	saude	4.04.06.00-8 Enfermagem de Saúde Pública	\N
2762	095	a	publica	4.04.06.00-8 Enfermagem de Saúde Pública	\N
2763	095	a	4.05.00.00-4	4.05.00.00-4 Nutrição	\N
2764	095	a	nutricao	4.05.00.00-4 Nutrição	\N
2765	095	a	4.05.01.00-0	4.05.01.00-0 Bioquímica da Nutrição	\N
2766	095	a	bioquimica	4.05.01.00-0 Bioquímica da Nutrição	\N
2767	095	a	da	4.05.01.00-0 Bioquímica da Nutrição	\N
2768	095	a	nutricao	4.05.01.00-0 Bioquímica da Nutrição	\N
2769	095	a	4.05.02.00-7	4.05.02.00-7 Dietética	\N
2770	095	a	dietetica	4.05.02.00-7 Dietética	\N
2771	095	a	4.05.03.00-3	4.05.03.00-3 Análise Nutricional de População	\N
2772	095	a	analise	4.05.03.00-3 Análise Nutricional de População	\N
2773	095	a	nutricional	4.05.03.00-3 Análise Nutricional de População	\N
2774	095	a	de	4.05.03.00-3 Análise Nutricional de População	\N
2775	095	a	populacao	4.05.03.00-3 Análise Nutricional de População	\N
2776	095	a	4.05.04.00-0	4.05.04.00-0 Desnutrição e Desenvolvimento Fisiológico	\N
2777	095	a	desnutricao	4.05.04.00-0 Desnutrição e Desenvolvimento Fisiológico	\N
2778	095	a	desenvolvimento	4.05.04.00-0 Desnutrição e Desenvolvimento Fisiológico	\N
2779	095	a	fisiologico	4.05.04.00-0 Desnutrição e Desenvolvimento Fisiológico	\N
2780	095	a	4.06.00.00-9	4.06.00.00-9 Saúde Coletiva	\N
2781	095	a	saude	4.06.00.00-9 Saúde Coletiva	\N
2782	095	a	coletiva	4.06.00.00-9 Saúde Coletiva	\N
2783	095	a	4.06.01.00-5	4.06.01.00-5 Epidemiologia	\N
2784	095	a	epidemiologia	4.06.01.00-5 Epidemiologia	\N
2785	095	a	4.06.02.00-1	4.06.02.00-1 Saúde Publica	\N
2786	095	a	saude	4.06.02.00-1 Saúde Publica	\N
2787	095	a	publica	4.06.02.00-1 Saúde Publica	\N
2788	095	a	4.06.03.00-8	4.06.03.00-8 Medicina Preventiva	\N
2789	095	a	medicina	4.06.03.00-8 Medicina Preventiva	\N
2790	095	a	preventiva	4.06.03.00-8 Medicina Preventiva	\N
2791	095	a	4.07.00.00-3	4.07.00.00-3 Fonoaudiologia	\N
2792	095	a	fonoaudiologia	4.07.00.00-3 Fonoaudiologia	\N
2793	095	a	4.08.00.00-8	4.08.00.00-8 Fisioterapia e Terapia Ocupacional	\N
2794	095	a	fisioterapia	4.08.00.00-8 Fisioterapia e Terapia Ocupacional	\N
2795	095	a	terapia	4.08.00.00-8 Fisioterapia e Terapia Ocupacional	\N
2796	095	a	ocupacional	4.08.00.00-8 Fisioterapia e Terapia Ocupacional	\N
2797	095	a	4.09.00.00-2	4.09.00.00-2 Educação Física	\N
2798	095	a	educacao	4.09.00.00-2 Educação Física	\N
2799	095	a	fisica	4.09.00.00-2 Educação Física	\N
2800	095	a	5.00.00.00-4	5.00.00.00-4 Ciências Agrárias	\N
2801	095	a	ciencias	5.00.00.00-4 Ciências Agrárias	\N
2802	095	a	agrarias	5.00.00.00-4 Ciências Agrárias	\N
2803	095	a	5.01.00.00-9	5.01.00.00-9 Agronomia	\N
2804	095	a	agronomia	5.01.00.00-9 Agronomia	\N
2805	095	a	5.01.01.00-5	5.01.01.00-5 Ciência do Solo	\N
2806	095	a	ciencia	5.01.01.00-5 Ciência do Solo	\N
2807	095	a	do	5.01.01.00-5 Ciência do Solo	\N
2808	095	a	solo	5.01.01.00-5 Ciência do Solo	\N
2809	095	a	5.01.01.01-3	5.01.01.01-3 Genese, Morfologia e Classificação dos Solos	\N
2810	095	a	genese,	5.01.01.01-3 Genese, Morfologia e Classificação dos Solos	\N
2811	095	a	morfologia	5.01.01.01-3 Genese, Morfologia e Classificação dos Solos	\N
2812	095	a	classificacao	5.01.01.01-3 Genese, Morfologia e Classificação dos Solos	\N
2813	095	a	dos	5.01.01.01-3 Genese, Morfologia e Classificação dos Solos	\N
2814	095	a	solos	5.01.01.01-3 Genese, Morfologia e Classificação dos Solos	\N
2815	095	a	5.01.01.02-1	5.01.01.02-1 Física do Solo	\N
2816	095	a	fisica	5.01.01.02-1 Física do Solo	\N
2817	095	a	do	5.01.01.02-1 Física do Solo	\N
2818	095	a	solo	5.01.01.02-1 Física do Solo	\N
2819	095	a	5.01.01.03-0	5.01.01.03-0 Química do Solo	\N
2820	095	a	quimica	5.01.01.03-0 Química do Solo	\N
2821	095	a	do	5.01.01.03-0 Química do Solo	\N
2822	095	a	solo	5.01.01.03-0 Química do Solo	\N
2823	095	a	5.01.01.04-8	5.01.01.04-8 Microbiologia e Bioquímica do Solo	\N
2824	095	a	microbiologia	5.01.01.04-8 Microbiologia e Bioquímica do Solo	\N
2825	095	a	bioquimica	5.01.01.04-8 Microbiologia e Bioquímica do Solo	\N
2826	095	a	do	5.01.01.04-8 Microbiologia e Bioquímica do Solo	\N
2827	095	a	solo	5.01.01.04-8 Microbiologia e Bioquímica do Solo	\N
2828	095	a	5.01.01.05-6	5.01.01.05-6 Fertilidade do Solo e Adubação	\N
2829	095	a	fertilidade	5.01.01.05-6 Fertilidade do Solo e Adubação	\N
2830	095	a	do	5.01.01.05-6 Fertilidade do Solo e Adubação	\N
2831	095	a	solo	5.01.01.05-6 Fertilidade do Solo e Adubação	\N
2832	095	a	adubacao	5.01.01.05-6 Fertilidade do Solo e Adubação	\N
2833	095	a	5.01.01.06-4	5.01.01.06-4 Manejo e Conservação do Solo	\N
2834	095	a	manejo	5.01.01.06-4 Manejo e Conservação do Solo	\N
2835	095	a	conservacao	5.01.01.06-4 Manejo e Conservação do Solo	\N
2836	095	a	do	5.01.01.06-4 Manejo e Conservação do Solo	\N
2837	095	a	solo	5.01.01.06-4 Manejo e Conservação do Solo	\N
2838	095	a	5.01.02.00-1	5.01.02.00-1 Fitossanidade	\N
2839	095	a	fitossanidade	5.01.02.00-1 Fitossanidade	\N
2840	095	a	5.01.02.01-0	5.01.02.01-0 Fitopatologia	\N
2841	095	a	fitopatologia	5.01.02.01-0 Fitopatologia	\N
2842	095	a	5.01.02.02-8	5.01.02.02-8 Entomologia Agrícola	\N
2843	095	a	entomologia	5.01.02.02-8 Entomologia Agrícola	\N
2844	095	a	agricola	5.01.02.02-8 Entomologia Agrícola	\N
2845	095	a	5.01.02.03-6	5.01.02.03-6 Parasitologia Agrícola	\N
2846	095	a	parasitologia	5.01.02.03-6 Parasitologia Agrícola	\N
2847	095	a	agricola	5.01.02.03-6 Parasitologia Agrícola	\N
2848	095	a	5.01.02.04-4	5.01.02.04-4 Microbiologia Agrícola	\N
2849	095	a	microbiologia	5.01.02.04-4 Microbiologia Agrícola	\N
2850	095	a	agricola	5.01.02.04-4 Microbiologia Agrícola	\N
2851	095	a	5.01.02.05-2	5.01.02.05-2 Defesa Fitossanitária	\N
2852	095	a	defesa	5.01.02.05-2 Defesa Fitossanitária	\N
2853	095	a	fitossanitaria	5.01.02.05-2 Defesa Fitossanitária	\N
2854	095	a	5.01.03.00-8	5.01.03.00-8 Fitotecnia	\N
2855	095	a	fitotecnia	5.01.03.00-8 Fitotecnia	\N
2856	095	a	5.01.03.01-6	5.01.03.01-6 Manejo e Tratos Culturais	\N
2857	095	a	manejo	5.01.03.01-6 Manejo e Tratos Culturais	\N
2858	095	a	tratos	5.01.03.01-6 Manejo e Tratos Culturais	\N
2859	095	a	culturais	5.01.03.01-6 Manejo e Tratos Culturais	\N
2860	095	a	5.01.03.02-4	5.01.03.02-4 Mecanização Agrícola	\N
2861	095	a	mecanizacao	5.01.03.02-4 Mecanização Agrícola	\N
2862	095	a	agricola	5.01.03.02-4 Mecanização Agrícola	\N
2863	095	a	5.01.03.03-2	5.01.03.03-2 Produção e Beneficiamento de Sementes	\N
2864	095	a	producao	5.01.03.03-2 Produção e Beneficiamento de Sementes	\N
2865	095	a	beneficiamento	5.01.03.03-2 Produção e Beneficiamento de Sementes	\N
2866	095	a	de	5.01.03.03-2 Produção e Beneficiamento de Sementes	\N
2867	095	a	sementes	5.01.03.03-2 Produção e Beneficiamento de Sementes	\N
2868	095	a	5.01.03.04-0	5.01.03.04-0 Produção de Mudas	\N
2869	095	a	producao	5.01.03.04-0 Produção de Mudas	\N
2870	095	a	de	5.01.03.04-0 Produção de Mudas	\N
2871	095	a	mudas	5.01.03.04-0 Produção de Mudas	\N
2872	095	a	5.01.03.05-9	5.01.03.05-9 Melhoramento Vegetal	\N
2873	095	a	melhoramento	5.01.03.05-9 Melhoramento Vegetal	\N
2874	095	a	vegetal	5.01.03.05-9 Melhoramento Vegetal	\N
2875	095	a	5.01.03.06-7	5.01.03.06-7 Fisiologia de Plantas Cultivadas	\N
2876	095	a	fisiologia	5.01.03.06-7 Fisiologia de Plantas Cultivadas	\N
2877	095	a	de	5.01.03.06-7 Fisiologia de Plantas Cultivadas	\N
2878	095	a	plantas	5.01.03.06-7 Fisiologia de Plantas Cultivadas	\N
2879	095	a	cultivadas	5.01.03.06-7 Fisiologia de Plantas Cultivadas	\N
2880	095	a	5.01.03.07-5	5.01.03.07-5 Matologia	\N
2881	095	a	matologia	5.01.03.07-5 Matologia	\N
2882	095	a	5.01.04.00-4	5.01.04.00-4 Floricultura, Parques e Jardins	\N
2883	095	a	floricultura,	5.01.04.00-4 Floricultura, Parques e Jardins	\N
2884	095	a	parques	5.01.04.00-4 Floricultura, Parques e Jardins	\N
2885	095	a	jardins	5.01.04.00-4 Floricultura, Parques e Jardins	\N
2886	095	a	5.01.04.01-2	5.01.04.01-2 Floricultura	\N
2887	095	a	floricultura	5.01.04.01-2 Floricultura	\N
2888	095	a	5.01.04.02-0	5.01.04.02-0 Parques e Jardins	\N
2889	095	a	parques	5.01.04.02-0 Parques e Jardins	\N
2890	095	a	jardins	5.01.04.02-0 Parques e Jardins	\N
2891	095	a	5.01.04.03-9	5.01.04.03-9 Arborização de Vias Públicas	\N
2892	095	a	arborizacao	5.01.04.03-9 Arborização de Vias Públicas	\N
2893	095	a	de	5.01.04.03-9 Arborização de Vias Públicas	\N
2894	095	a	vias	5.01.04.03-9 Arborização de Vias Públicas	\N
2895	095	a	publicas	5.01.04.03-9 Arborização de Vias Públicas	\N
2896	095	a	5.01.05.00-0	5.01.05.00-0 Agrometeorologia	\N
2897	095	a	agrometeorologia	5.01.05.00-0 Agrometeorologia	\N
2898	095	a	5.01.06.00-7	5.01.06.00-7 Extensão Rural	\N
2899	095	a	extensao	5.01.06.00-7 Extensão Rural	\N
2900	095	a	rural	5.01.06.00-7 Extensão Rural	\N
2901	095	a	5.02.00.00-3	5.02.00.00-3 Recursos Florestais e Engenharia Florestal	\N
2902	095	a	recursos	5.02.00.00-3 Recursos Florestais e Engenharia Florestal	\N
2903	095	a	florestais	5.02.00.00-3 Recursos Florestais e Engenharia Florestal	\N
2904	095	a	engenharia	5.02.00.00-3 Recursos Florestais e Engenharia Florestal	\N
2905	095	a	florestal	5.02.00.00-3 Recursos Florestais e Engenharia Florestal	\N
2906	095	a	5.02.01.00-0	5.02.01.00-0 Silvicultura	\N
2907	095	a	silvicultura	5.02.01.00-0 Silvicultura	\N
2908	095	a	5.02.01.01-8	5.02.01.01-8 Dendrologia	\N
2909	095	a	dendrologia	5.02.01.01-8 Dendrologia	\N
2910	095	a	5.02.01.02-6	5.02.01.02-6 Florestamento e Reflorestamento	\N
2911	095	a	florestamento	5.02.01.02-6 Florestamento e Reflorestamento	\N
2912	095	a	reflorestamento	5.02.01.02-6 Florestamento e Reflorestamento	\N
2913	095	a	5.02.01.03-4	5.02.01.03-4 Genética e Melhoramento Florestal	\N
2914	095	a	genetica	5.02.01.03-4 Genética e Melhoramento Florestal	\N
2915	095	a	melhoramento	5.02.01.03-4 Genética e Melhoramento Florestal	\N
2916	095	a	florestal	5.02.01.03-4 Genética e Melhoramento Florestal	\N
2917	095	a	5.02.01.04-2	5.02.01.04-2 Sementes Florestais	\N
2918	095	a	sementes	5.02.01.04-2 Sementes Florestais	\N
2919	095	a	florestais	5.02.01.04-2 Sementes Florestais	\N
2920	095	a	5.02.01.05-0	5.02.01.05-0 Nutrição Florestal	\N
2921	095	a	nutricao	5.02.01.05-0 Nutrição Florestal	\N
2922	095	a	florestal	5.02.01.05-0 Nutrição Florestal	\N
2923	095	a	5.02.01.06-9	5.02.01.06-9 Fisiologia Florestal	\N
2924	095	a	fisiologia	5.02.01.06-9 Fisiologia Florestal	\N
2925	095	a	florestal	5.02.01.06-9 Fisiologia Florestal	\N
2926	095	a	5.02.01.07-7	5.02.01.07-7 Solos Florestais	\N
2927	095	a	solos	5.02.01.07-7 Solos Florestais	\N
2928	095	a	florestais	5.02.01.07-7 Solos Florestais	\N
2929	095	a	5.02.01.08-5	5.02.01.08-5 Proteção Florestal	\N
2930	095	a	protecao	5.02.01.08-5 Proteção Florestal	\N
2931	095	a	florestal	5.02.01.08-5 Proteção Florestal	\N
2932	095	a	5.02.02.00-6	5.02.02.00-6 Manejo Florestal	\N
2933	095	a	manejo	5.02.02.00-6 Manejo Florestal	\N
2934	095	a	florestal	5.02.02.00-6 Manejo Florestal	\N
2935	095	a	5.02.02.01-4	5.02.02.01-4 Economia Florestal	\N
2936	095	a	economia	5.02.02.01-4 Economia Florestal	\N
2937	095	a	florestal	5.02.02.01-4 Economia Florestal	\N
2938	095	a	5.02.02.02-2	5.02.02.02-2 Politica e Legislação Florestal	\N
2939	095	a	politica	5.02.02.02-2 Politica e Legislação Florestal	\N
2940	095	a	legislacao	5.02.02.02-2 Politica e Legislação Florestal	\N
2941	095	a	florestal	5.02.02.02-2 Politica e Legislação Florestal	\N
2942	095	a	5.02.02.03-0	5.02.02.03-0 Administração Florestal	\N
2943	095	a	administracao	5.02.02.03-0 Administração Florestal	\N
2944	095	a	florestal	5.02.02.03-0 Administração Florestal	\N
2945	095	a	5.02.02.04-9	5.02.02.04-9 Dendrometria e Inventário Florestal	\N
2946	095	a	dendrometria	5.02.02.04-9 Dendrometria e Inventário Florestal	\N
2947	095	a	inventario	5.02.02.04-9 Dendrometria e Inventário Florestal	\N
2948	095	a	florestal	5.02.02.04-9 Dendrometria e Inventário Florestal	\N
2949	095	a	5.02.02.05-7	5.02.02.05-7 Fotointerpretação Florestal	\N
2950	095	a	fotointerpretacao	5.02.02.05-7 Fotointerpretação Florestal	\N
2951	095	a	florestal	5.02.02.05-7 Fotointerpretação Florestal	\N
2952	095	a	5.02.02.06-5	5.02.02.06-5 Ordenamento Florestal	\N
2953	095	a	ordenamento	5.02.02.06-5 Ordenamento Florestal	\N
2954	095	a	florestal	5.02.02.06-5 Ordenamento Florestal	\N
2955	095	a	5.02.03.00-2	5.02.03.00-2 Técnicas e Operações Florestais	\N
2956	095	a	tecnicas	5.02.03.00-2 Técnicas e Operações Florestais	\N
2957	095	a	operacoes	5.02.03.00-2 Técnicas e Operações Florestais	\N
2958	095	a	florestais	5.02.03.00-2 Técnicas e Operações Florestais	\N
2959	095	a	5.02.03.01-0	5.02.03.01-0 Exploração Florestal	\N
2960	095	a	exploracao	5.02.03.01-0 Exploração Florestal	\N
2961	095	a	florestal	5.02.03.01-0 Exploração Florestal	\N
2962	095	a	5.02.03.02-9	5.02.03.02-9 Mecanização Florestal	\N
2963	095	a	mecanizacao	5.02.03.02-9 Mecanização Florestal	\N
2964	095	a	florestal	5.02.03.02-9 Mecanização Florestal	\N
2965	095	a	5.02.04.00-9	5.02.04.00-9 Tecnologia e Utilização de Produtos Florestais	\N
2966	095	a	tecnologia	5.02.04.00-9 Tecnologia e Utilização de Produtos Florestais	\N
2967	095	a	utilizacao	5.02.04.00-9 Tecnologia e Utilização de Produtos Florestais	\N
2968	095	a	de	5.02.04.00-9 Tecnologia e Utilização de Produtos Florestais	\N
2969	095	a	produtos	5.02.04.00-9 Tecnologia e Utilização de Produtos Florestais	\N
2970	095	a	florestais	5.02.04.00-9 Tecnologia e Utilização de Produtos Florestais	\N
2971	095	a	5.02.04.01-7	5.02.04.01-7 Anatomia e Identificação de Produtos Florestais	\N
2972	095	a	anatomia	5.02.04.01-7 Anatomia e Identificação de Produtos Florestais	\N
2973	095	a	identificacao	5.02.04.01-7 Anatomia e Identificação de Produtos Florestais	\N
2974	095	a	de	5.02.04.01-7 Anatomia e Identificação de Produtos Florestais	\N
2975	095	a	produtos	5.02.04.01-7 Anatomia e Identificação de Produtos Florestais	\N
2976	095	a	florestais	5.02.04.01-7 Anatomia e Identificação de Produtos Florestais	\N
2977	095	a	5.02.04.02-5	5.02.04.02-5 Propriedades Físico-Mecânicas da Madeira	\N
2978	095	a	propriedades	5.02.04.02-5 Propriedades Físico-Mecânicas da Madeira	\N
2979	095	a	fisico-mecanicas	5.02.04.02-5 Propriedades Físico-Mecânicas da Madeira	\N
2980	095	a	da	5.02.04.02-5 Propriedades Físico-Mecânicas da Madeira	\N
2981	095	a	madeira	5.02.04.02-5 Propriedades Físico-Mecânicas da Madeira	\N
2982	095	a	5.02.04.03-3	5.02.04.03-3 Relações Água-Madeira e Secagem	\N
2983	095	a	relacoes	5.02.04.03-3 Relações Água-Madeira e Secagem	\N
2984	095	a	agua-madeira	5.02.04.03-3 Relações Água-Madeira e Secagem	\N
2985	095	a	secagem	5.02.04.03-3 Relações Água-Madeira e Secagem	\N
2986	095	a	5.02.04.04-1	5.02.04.04-1 Tratamento da Madeira	\N
2987	095	a	tratamento	5.02.04.04-1 Tratamento da Madeira	\N
2988	095	a	da	5.02.04.04-1 Tratamento da Madeira	\N
2989	095	a	madeira	5.02.04.04-1 Tratamento da Madeira	\N
2990	095	a	5.02.04.05-0	5.02.04.05-0 Processamento Mecânico da Madeira	\N
2991	095	a	processamento	5.02.04.05-0 Processamento Mecânico da Madeira	\N
2992	095	a	mecanico	5.02.04.05-0 Processamento Mecânico da Madeira	\N
2993	095	a	da	5.02.04.05-0 Processamento Mecânico da Madeira	\N
2994	095	a	madeira	5.02.04.05-0 Processamento Mecânico da Madeira	\N
2995	095	a	5.02.04.06-8	5.02.04.06-8 Química da Madeira	\N
2996	095	a	quimica	5.02.04.06-8 Química da Madeira	\N
2997	095	a	da	5.02.04.06-8 Química da Madeira	\N
2998	095	a	madeira	5.02.04.06-8 Química da Madeira	\N
2999	095	a	5.02.04.07-6	5.02.04.07-6 Resinas de Madeiras	\N
3000	095	a	resinas	5.02.04.07-6 Resinas de Madeiras	\N
3001	095	a	de	5.02.04.07-6 Resinas de Madeiras	\N
3002	095	a	madeiras	5.02.04.07-6 Resinas de Madeiras	\N
3003	095	a	5.02.04.08-4	5.02.04.08-4 Tecnologia de Celulose e Papel	\N
3004	095	a	tecnologia	5.02.04.08-4 Tecnologia de Celulose e Papel	\N
3005	095	a	de	5.02.04.08-4 Tecnologia de Celulose e Papel	\N
3006	095	a	celulose	5.02.04.08-4 Tecnologia de Celulose e Papel	\N
3007	095	a	papel	5.02.04.08-4 Tecnologia de Celulose e Papel	\N
3008	095	a	5.02.04.09-2	5.02.04.09-2 Tecnologia de Chapas	\N
3009	095	a	tecnologia	5.02.04.09-2 Tecnologia de Chapas	\N
3010	095	a	de	5.02.04.09-2 Tecnologia de Chapas	\N
3011	095	a	chapas	5.02.04.09-2 Tecnologia de Chapas	\N
3012	095	a	5.02.05.00-5	5.02.05.00-5 Conservação da Natureza	\N
3013	095	a	conservacao	5.02.05.00-5 Conservação da Natureza	\N
3014	095	a	da	5.02.05.00-5 Conservação da Natureza	\N
3015	095	a	natureza	5.02.05.00-5 Conservação da Natureza	\N
3016	095	a	5.02.05.01-3	5.02.05.01-3 Hidrologia Florestal	\N
3017	095	a	hidrologia	5.02.05.01-3 Hidrologia Florestal	\N
3018	095	a	florestal	5.02.05.01-3 Hidrologia Florestal	\N
3019	095	a	5.02.05.02-1	5.02.05.02-1 Conservação de Áreas Silvestres	\N
3020	095	a	conservacao	5.02.05.02-1 Conservação de Áreas Silvestres	\N
3021	095	a	de	5.02.05.02-1 Conservação de Áreas Silvestres	\N
3022	095	a	areas	5.02.05.02-1 Conservação de Áreas Silvestres	\N
3023	095	a	silvestres	5.02.05.02-1 Conservação de Áreas Silvestres	\N
3024	095	a	5.02.05.03-0	5.02.05.03-0 Conservação de Bacias Hidrográficas	\N
3025	095	a	conservacao	5.02.05.03-0 Conservação de Bacias Hidrográficas	\N
3026	095	a	de	5.02.05.03-0 Conservação de Bacias Hidrográficas	\N
3027	095	a	bacias	5.02.05.03-0 Conservação de Bacias Hidrográficas	\N
3028	095	a	hidrograficas	5.02.05.03-0 Conservação de Bacias Hidrográficas	\N
3029	095	a	5.02.05.04-8	5.02.05.04-8 Recuperação de Áreas Degradadas	\N
3030	095	a	recuperacao	5.02.05.04-8 Recuperação de Áreas Degradadas	\N
3031	095	a	de	5.02.05.04-8 Recuperação de Áreas Degradadas	\N
3032	095	a	areas	5.02.05.04-8 Recuperação de Áreas Degradadas	\N
3033	095	a	degradadas	5.02.05.04-8 Recuperação de Áreas Degradadas	\N
3034	095	a	5.02.06.00-1	5.02.06.00-1 Energia de Biomassa Florestal	\N
3035	095	a	energia	5.02.06.00-1 Energia de Biomassa Florestal	\N
3036	095	a	de	5.02.06.00-1 Energia de Biomassa Florestal	\N
3037	095	a	biomassa	5.02.06.00-1 Energia de Biomassa Florestal	\N
3038	095	a	florestal	5.02.06.00-1 Energia de Biomassa Florestal	\N
3039	095	a	5.03.00.00-8	5.03.00.00-8 Engenharia Agrícola	\N
3040	095	a	engenharia	5.03.00.00-8 Engenharia Agrícola	\N
3041	095	a	agricola	5.03.00.00-8 Engenharia Agrícola	\N
3042	095	a	5.03.01.00-4	5.03.01.00-4 Máquinas e Implementos Agrícolas	\N
3043	095	a	maquinas	5.03.01.00-4 Máquinas e Implementos Agrícolas	\N
3044	095	a	implementos	5.03.01.00-4 Máquinas e Implementos Agrícolas	\N
3045	095	a	agricolas	5.03.01.00-4 Máquinas e Implementos Agrícolas	\N
3046	095	a	5.03.02.00-0	5.03.02.00-0 Engenharia de Água e Solo	\N
3047	095	a	engenharia	5.03.02.00-0 Engenharia de Água e Solo	\N
3048	095	a	de	5.03.02.00-0 Engenharia de Água e Solo	\N
3049	095	a	agua	5.03.02.00-0 Engenharia de Água e Solo	\N
3050	095	a	solo	5.03.02.00-0 Engenharia de Água e Solo	\N
3051	095	a	5.03.02.01-9	5.03.02.01-9 Irrigação e Drenagem	\N
3052	095	a	irrigacao	5.03.02.01-9 Irrigação e Drenagem	\N
3053	095	a	drenagem	5.03.02.01-9 Irrigação e Drenagem	\N
3054	095	a	5.03.02.02-7	5.03.02.02-7 Conservação de Solo e Água	\N
3055	095	a	conservacao	5.03.02.02-7 Conservação de Solo e Água	\N
3056	095	a	de	5.03.02.02-7 Conservação de Solo e Água	\N
3057	095	a	solo	5.03.02.02-7 Conservação de Solo e Água	\N
3058	095	a	agua	5.03.02.02-7 Conservação de Solo e Água	\N
3059	095	a	5.03.03.00-7	5.03.03.00-7 Engenharia de Processamento de Produtos Agrícolas	\N
3060	095	a	engenharia	5.03.03.00-7 Engenharia de Processamento de Produtos Agrícolas	\N
3061	095	a	de	5.03.03.00-7 Engenharia de Processamento de Produtos Agrícolas	\N
3062	095	a	processamento	5.03.03.00-7 Engenharia de Processamento de Produtos Agrícolas	\N
3063	095	a	de	5.03.03.00-7 Engenharia de Processamento de Produtos Agrícolas	\N
3064	095	a	produtos	5.03.03.00-7 Engenharia de Processamento de Produtos Agrícolas	\N
3065	095	a	agricolas	5.03.03.00-7 Engenharia de Processamento de Produtos Agrícolas	\N
3066	095	a	5.03.03.01-5	5.03.03.01-5 Pré-Processamento de Produtos Agrícolas	\N
3067	095	a	pre-processamento	5.03.03.01-5 Pré-Processamento de Produtos Agrícolas	\N
3068	095	a	de	5.03.03.01-5 Pré-Processamento de Produtos Agrícolas	\N
3069	095	a	produtos	5.03.03.01-5 Pré-Processamento de Produtos Agrícolas	\N
3070	095	a	agricolas	5.03.03.01-5 Pré-Processamento de Produtos Agrícolas	\N
3071	095	a	5.03.03.02-3	5.03.03.02-3 Armazenamento de Produtos Agrícolas	\N
3072	095	a	armazenamento	5.03.03.02-3 Armazenamento de Produtos Agrícolas	\N
3073	095	a	de	5.03.03.02-3 Armazenamento de Produtos Agrícolas	\N
3074	095	a	produtos	5.03.03.02-3 Armazenamento de Produtos Agrícolas	\N
3075	095	a	agricolas	5.03.03.02-3 Armazenamento de Produtos Agrícolas	\N
3076	095	a	5.03.03.03-1	5.03.03.03-1 Transferência de Produtos Agrícolas	\N
3077	095	a	transferencia	5.03.03.03-1 Transferência de Produtos Agrícolas	\N
3078	095	a	de	5.03.03.03-1 Transferência de Produtos Agrícolas	\N
3079	095	a	produtos	5.03.03.03-1 Transferência de Produtos Agrícolas	\N
3080	095	a	agricolas	5.03.03.03-1 Transferência de Produtos Agrícolas	\N
3081	095	a	5.03.04.00-3	5.03.04.00-3 Construções Rurais e Ambiência	\N
3082	095	a	construcoes	5.03.04.00-3 Construções Rurais e Ambiência	\N
3083	095	a	rurais	5.03.04.00-3 Construções Rurais e Ambiência	\N
3084	095	a	ambiencia	5.03.04.00-3 Construções Rurais e Ambiência	\N
3085	095	a	5.03.04.01-1	5.03.04.01-1 Assentamento Rural	\N
3086	095	a	assentamento	5.03.04.01-1 Assentamento Rural	\N
3087	095	a	rural	5.03.04.01-1 Assentamento Rural	\N
3088	095	a	5.03.04.02-0	5.03.04.02-0 Engenharia de Construções Rurais	\N
3089	095	a	engenharia	5.03.04.02-0 Engenharia de Construções Rurais	\N
3090	095	a	de	5.03.04.02-0 Engenharia de Construções Rurais	\N
3091	095	a	construcoes	5.03.04.02-0 Engenharia de Construções Rurais	\N
3092	095	a	rurais	5.03.04.02-0 Engenharia de Construções Rurais	\N
3093	095	a	5.03.04.03-8	5.03.04.03-8 Saneamento Rural	\N
3094	095	a	saneamento	5.03.04.03-8 Saneamento Rural	\N
3095	095	a	rural	5.03.04.03-8 Saneamento Rural	\N
3096	095	a	5.03.05.00-0	5.03.05.00-0 Energização Rural	\N
3097	095	a	energizacao	5.03.05.00-0 Energização Rural	\N
3098	095	a	rural	5.03.05.00-0 Energização Rural	\N
3099	095	a	5.04.00.00-2	5.04.00.00-2 Zootecnia	\N
3100	095	a	zootecnia	5.04.00.00-2 Zootecnia	\N
3101	095	a	5.04.01.00-9	5.04.01.00-9 Ecologia dos Animais Domésticos e Etologia	\N
3102	095	a	ecologia	5.04.01.00-9 Ecologia dos Animais Domésticos e Etologia	\N
3103	095	a	dos	5.04.01.00-9 Ecologia dos Animais Domésticos e Etologia	\N
3104	095	a	animais	5.04.01.00-9 Ecologia dos Animais Domésticos e Etologia	\N
3105	095	a	domesticos	5.04.01.00-9 Ecologia dos Animais Domésticos e Etologia	\N
3106	095	a	etologia	5.04.01.00-9 Ecologia dos Animais Domésticos e Etologia	\N
3107	095	a	5.04.02.00-5	5.04.02.00-5 Genética e Melhoramento dos Animais Domésticos	\N
3108	095	a	genetica	5.04.02.00-5 Genética e Melhoramento dos Animais Domésticos	\N
3109	095	a	melhoramento	5.04.02.00-5 Genética e Melhoramento dos Animais Domésticos	\N
3110	095	a	dos	5.04.02.00-5 Genética e Melhoramento dos Animais Domésticos	\N
3111	095	a	animais	5.04.02.00-5 Genética e Melhoramento dos Animais Domésticos	\N
3112	095	a	domesticos	5.04.02.00-5 Genética e Melhoramento dos Animais Domésticos	\N
3113	095	a	5.04.03.00-1	5.04.03.00-1 Nutrição e Alimentação Animal	\N
3114	095	a	nutricao	5.04.03.00-1 Nutrição e Alimentação Animal	\N
3115	095	a	alimentacao	5.04.03.00-1 Nutrição e Alimentação Animal	\N
3116	095	a	animal	5.04.03.00-1 Nutrição e Alimentação Animal	\N
3117	095	a	5.04.03.01-0	5.04.03.01-0 Exigências Nutricionais dos Animais	\N
3118	095	a	exigencias	5.04.03.01-0 Exigências Nutricionais dos Animais	\N
3119	095	a	nutricionais	5.04.03.01-0 Exigências Nutricionais dos Animais	\N
3120	095	a	dos	5.04.03.01-0 Exigências Nutricionais dos Animais	\N
3121	095	a	animais	5.04.03.01-0 Exigências Nutricionais dos Animais	\N
3122	095	a	5.04.03.02-8	5.04.03.02-8 Avaliação de Alimentos para Animais	\N
3123	095	a	avaliacao	5.04.03.02-8 Avaliação de Alimentos para Animais	\N
3124	095	a	de	5.04.03.02-8 Avaliação de Alimentos para Animais	\N
3125	095	a	alimentos	5.04.03.02-8 Avaliação de Alimentos para Animais	\N
3126	095	a	para	5.04.03.02-8 Avaliação de Alimentos para Animais	\N
3127	095	a	animais	5.04.03.02-8 Avaliação de Alimentos para Animais	\N
3128	095	a	5.04.03.03-6	5.04.03.03-6 Conservação de Alimentos para Animais	\N
3129	095	a	conservacao	5.04.03.03-6 Conservação de Alimentos para Animais	\N
3130	095	a	de	5.04.03.03-6 Conservação de Alimentos para Animais	\N
3131	095	a	alimentos	5.04.03.03-6 Conservação de Alimentos para Animais	\N
3132	095	a	para	5.04.03.03-6 Conservação de Alimentos para Animais	\N
3133	095	a	animais	5.04.03.03-6 Conservação de Alimentos para Animais	\N
3134	095	a	5.04.04.00-8	5.04.04.00-8 Pastagem e Forragicultura	\N
3135	095	a	pastagem	5.04.04.00-8 Pastagem e Forragicultura	\N
3136	095	a	forragicultura	5.04.04.00-8 Pastagem e Forragicultura	\N
3137	095	a	5.04.04.01-6	5.04.04.01-6 Avaliação, Produção e Conservação de Forragens	\N
3138	095	a	avaliacao,	5.04.04.01-6 Avaliação, Produção e Conservação de Forragens	\N
3139	095	a	producao	5.04.04.01-6 Avaliação, Produção e Conservação de Forragens	\N
3140	095	a	conservacao	5.04.04.01-6 Avaliação, Produção e Conservação de Forragens	\N
3141	095	a	de	5.04.04.01-6 Avaliação, Produção e Conservação de Forragens	\N
3142	095	a	forragens	5.04.04.01-6 Avaliação, Produção e Conservação de Forragens	\N
3143	095	a	5.04.04.02-4	5.04.04.02-4 Manejo e Conservação de Pastagens	\N
3144	095	a	manejo	5.04.04.02-4 Manejo e Conservação de Pastagens	\N
3145	095	a	conservacao	5.04.04.02-4 Manejo e Conservação de Pastagens	\N
3146	095	a	de	5.04.04.02-4 Manejo e Conservação de Pastagens	\N
3147	095	a	pastagens	5.04.04.02-4 Manejo e Conservação de Pastagens	\N
3148	095	a	5.04.04.03-2	5.04.04.03-2 Fisiologia de Plantas Forrageiras	\N
3149	095	a	fisiologia	5.04.04.03-2 Fisiologia de Plantas Forrageiras	\N
3150	095	a	de	5.04.04.03-2 Fisiologia de Plantas Forrageiras	\N
3151	095	a	plantas	5.04.04.03-2 Fisiologia de Plantas Forrageiras	\N
3152	095	a	forrageiras	5.04.04.03-2 Fisiologia de Plantas Forrageiras	\N
3153	095	a	5.04.04.04-0	5.04.04.04-0 Melhoramento de Plantas Forrageiras e Produção de Sementes	\N
3154	095	a	melhoramento	5.04.04.04-0 Melhoramento de Plantas Forrageiras e Produção de Sementes	\N
3155	095	a	de	5.04.04.04-0 Melhoramento de Plantas Forrageiras e Produção de Sementes	\N
3156	095	a	plantas	5.04.04.04-0 Melhoramento de Plantas Forrageiras e Produção de Sementes	\N
3157	095	a	forrageiras	5.04.04.04-0 Melhoramento de Plantas Forrageiras e Produção de Sementes	\N
3158	095	a	producao	5.04.04.04-0 Melhoramento de Plantas Forrageiras e Produção de Sementes	\N
3159	095	a	de	5.04.04.04-0 Melhoramento de Plantas Forrageiras e Produção de Sementes	\N
3160	095	a	sementes	5.04.04.04-0 Melhoramento de Plantas Forrageiras e Produção de Sementes	\N
3161	095	a	5.04.04.05-9	5.04.04.05-9 Toxicologia e Plantas Tóxicas	\N
3162	095	a	toxicologia	5.04.04.05-9 Toxicologia e Plantas Tóxicas	\N
3163	095	a	plantas	5.04.04.05-9 Toxicologia e Plantas Tóxicas	\N
3164	095	a	toxicas	5.04.04.05-9 Toxicologia e Plantas Tóxicas	\N
3165	095	a	5.04.05.00-4	5.04.05.00-4 Produção Animal	\N
3166	095	a	producao	5.04.05.00-4 Produção Animal	\N
3167	095	a	animal	5.04.05.00-4 Produção Animal	\N
3168	095	a	5.04.05.01-2	5.04.05.01-2 Criação de Animais	\N
3169	095	a	criacao	5.04.05.01-2 Criação de Animais	\N
3170	095	a	de	5.04.05.01-2 Criação de Animais	\N
3171	095	a	animais	5.04.05.01-2 Criação de Animais	\N
3172	095	a	5.04.05.02-0	5.04.05.02-0 Manejo de Animais	\N
3173	095	a	manejo	5.04.05.02-0 Manejo de Animais	\N
3174	095	a	de	5.04.05.02-0 Manejo de Animais	\N
3175	095	a	animais	5.04.05.02-0 Manejo de Animais	\N
3176	095	a	5.04.05.03-9	5.04.05.03-9 Instalações para Produção Animal	\N
3177	095	a	instalacoes	5.04.05.03-9 Instalações para Produção Animal	\N
3178	095	a	para	5.04.05.03-9 Instalações para Produção Animal	\N
3179	095	a	producao	5.04.05.03-9 Instalações para Produção Animal	\N
3180	095	a	animal	5.04.05.03-9 Instalações para Produção Animal	\N
3181	095	a	5.05.00.00-7	5.05.00.00-7 Medicina Veterinária	\N
3182	095	a	medicina	5.05.00.00-7 Medicina Veterinária	\N
3183	095	a	veterinaria	5.05.00.00-7 Medicina Veterinária	\N
3184	095	a	5.05.01.00-3	5.05.01.00-3 Clínica e Cirurgia Animal	\N
3185	095	a	clinica	5.05.01.00-3 Clínica e Cirurgia Animal	\N
3186	095	a	cirurgia	5.05.01.00-3 Clínica e Cirurgia Animal	\N
3187	095	a	animal	5.05.01.00-3 Clínica e Cirurgia Animal	\N
3188	095	a	5.05.01.01-1	5.05.01.01-1 Anestesiologia Animal	\N
3189	095	a	anestesiologia	5.05.01.01-1 Anestesiologia Animal	\N
3190	095	a	animal	5.05.01.01-1 Anestesiologia Animal	\N
3191	095	a	5.05.01.02-0	5.05.01.02-0 Técnica Cirúrgica Animal	\N
3192	095	a	tecnica	5.05.01.02-0 Técnica Cirúrgica Animal	\N
3193	095	a	cirurgica	5.05.01.02-0 Técnica Cirúrgica Animal	\N
3194	095	a	animal	5.05.01.02-0 Técnica Cirúrgica Animal	\N
3195	095	a	5.05.01.03-8	5.05.01.03-8 Radiologia de Animais	\N
3196	095	a	radiologia	5.05.01.03-8 Radiologia de Animais	\N
3197	095	a	de	5.05.01.03-8 Radiologia de Animais	\N
3198	095	a	animais	5.05.01.03-8 Radiologia de Animais	\N
3199	095	a	5.05.01.04-6	5.05.01.04-6 Farmacologia e Terapêutica Animal	\N
3200	095	a	farmacologia	5.05.01.04-6 Farmacologia e Terapêutica Animal	\N
3201	095	a	terapeutica	5.05.01.04-6 Farmacologia e Terapêutica Animal	\N
3202	095	a	animal	5.05.01.04-6 Farmacologia e Terapêutica Animal	\N
3203	095	a	5.05.01.05-4	5.05.01.05-4 Obstetrícia Animal	\N
3204	095	a	obstetricia	5.05.01.05-4 Obstetrícia Animal	\N
3205	095	a	animal	5.05.01.05-4 Obstetrícia Animal	\N
3206	095	a	5.05.01.06-2	5.05.01.06-2 Clínica Veterinária	\N
3207	095	a	clinica	5.05.01.06-2 Clínica Veterinária	\N
3208	095	a	veterinaria	5.05.01.06-2 Clínica Veterinária	\N
3209	095	a	5.05.01.07-0	5.05.01.07-0 Clínica Cirúrgica Animal	\N
3210	095	a	clinica	5.05.01.07-0 Clínica Cirúrgica Animal	\N
3211	095	a	cirurgica	5.05.01.07-0 Clínica Cirúrgica Animal	\N
3212	095	a	animal	5.05.01.07-0 Clínica Cirúrgica Animal	\N
3213	095	a	5.05.01.08-9	5.05.01.08-9 Toxicologia Animal	\N
3214	095	a	toxicologia	5.05.01.08-9 Toxicologia Animal	\N
3215	095	a	animal	5.05.01.08-9 Toxicologia Animal	\N
3216	095	a	5.05.02.00-0	5.05.02.00-0 Medicina Veterinária Preventiva	\N
3217	095	a	medicina	5.05.02.00-0 Medicina Veterinária Preventiva	\N
3218	095	a	veterinaria	5.05.02.00-0 Medicina Veterinária Preventiva	\N
3219	095	a	preventiva	5.05.02.00-0 Medicina Veterinária Preventiva	\N
3220	095	a	5.05.02.01-8	5.05.02.01-8 Epidemiologia Animal	\N
3221	095	a	epidemiologia	5.05.02.01-8 Epidemiologia Animal	\N
3222	095	a	animal	5.05.02.01-8 Epidemiologia Animal	\N
3223	095	a	5.05.02.02-6	5.05.02.02-6 Saneamento Aplicado à Saúde do Homem	\N
3224	095	a	saneamento	5.05.02.02-6 Saneamento Aplicado à Saúde do Homem	\N
3225	095	a	aplicado	5.05.02.02-6 Saneamento Aplicado à Saúde do Homem	\N
3226	095	a	saude	5.05.02.02-6 Saneamento Aplicado à Saúde do Homem	\N
3227	095	a	do	5.05.02.02-6 Saneamento Aplicado à Saúde do Homem	\N
3228	095	a	homem	5.05.02.02-6 Saneamento Aplicado à Saúde do Homem	\N
3229	095	a	5.05.02.03-4	5.05.02.03-4 Doenças Infecciosas de Animais	\N
3230	095	a	doencas	5.05.02.03-4 Doenças Infecciosas de Animais	\N
3231	095	a	infecciosas	5.05.02.03-4 Doenças Infecciosas de Animais	\N
3232	095	a	de	5.05.02.03-4 Doenças Infecciosas de Animais	\N
3233	095	a	animais	5.05.02.03-4 Doenças Infecciosas de Animais	\N
3234	095	a	5.05.02.04-2	5.05.02.04-2 Doenças Parasitárias de Animais	\N
3235	095	a	doencas	5.05.02.04-2 Doenças Parasitárias de Animais	\N
3236	095	a	parasitarias	5.05.02.04-2 Doenças Parasitárias de Animais	\N
3237	095	a	de	5.05.02.04-2 Doenças Parasitárias de Animais	\N
3238	095	a	animais	5.05.02.04-2 Doenças Parasitárias de Animais	\N
3239	095	a	5.05.02.05-0	5.05.02.05-0 Saúde Animal (Programas Sanitários)	\N
3240	095	a	saude	5.05.02.05-0 Saúde Animal (Programas Sanitários)	\N
3241	095	a	animal	5.05.02.05-0 Saúde Animal (Programas Sanitários)	\N
3242	095	a	(programas	5.05.02.05-0 Saúde Animal (Programas Sanitários)	\N
3243	095	a	sanitarios)	5.05.02.05-0 Saúde Animal (Programas Sanitários)	\N
3244	095	a	5.05.03.00-6	5.05.03.00-6 Patologia Animal	\N
3245	095	a	patologia	5.05.03.00-6 Patologia Animal	\N
3246	095	a	animal	5.05.03.00-6 Patologia Animal	\N
3247	095	a	5.05.03.01-4	5.05.03.01-4 Patologia Aviária	\N
3248	095	a	patologia	5.05.03.01-4 Patologia Aviária	\N
3249	095	a	aviaria	5.05.03.01-4 Patologia Aviária	\N
3250	095	a	5.05.03.02-2	5.05.03.02-2 Anatomia Patologia Animal	\N
3251	095	a	anatomia	5.05.03.02-2 Anatomia Patologia Animal	\N
3252	095	a	patologia	5.05.03.02-2 Anatomia Patologia Animal	\N
3253	095	a	animal	5.05.03.02-2 Anatomia Patologia Animal	\N
3254	095	a	5.05.03.03-0	5.05.03.03-0 Patologia Clínica Animal	\N
3255	095	a	patologia	5.05.03.03-0 Patologia Clínica Animal	\N
3256	095	a	clinica	5.05.03.03-0 Patologia Clínica Animal	\N
3257	095	a	animal	5.05.03.03-0 Patologia Clínica Animal	\N
3258	095	a	5.05.04.00-2	5.05.04.00-2 Reprodução Animal	\N
3259	095	a	reproducao	5.05.04.00-2 Reprodução Animal	\N
3260	095	a	animal	5.05.04.00-2 Reprodução Animal	\N
3261	095	a	5.05.04.01-0	5.05.04.01-0 Ginecologia e Andrologia Animal	\N
3262	095	a	ginecologia	5.05.04.01-0 Ginecologia e Andrologia Animal	\N
3263	095	a	andrologia	5.05.04.01-0 Ginecologia e Andrologia Animal	\N
3264	095	a	animal	5.05.04.01-0 Ginecologia e Andrologia Animal	\N
3265	095	a	5.05.04.02-9	5.05.04.02-9 Inseminação Artificial Animal	\N
3266	095	a	inseminacao	5.05.04.02-9 Inseminação Artificial Animal	\N
3267	095	a	artificial	5.05.04.02-9 Inseminação Artificial Animal	\N
3268	095	a	animal	5.05.04.02-9 Inseminação Artificial Animal	\N
3269	095	a	5.05.04.03-7	5.05.04.03-7 Fisiopatologia da Reprodução Animal	\N
3270	095	a	fisiopatologia	5.05.04.03-7 Fisiopatologia da Reprodução Animal	\N
3271	095	a	da	5.05.04.03-7 Fisiopatologia da Reprodução Animal	\N
3272	095	a	reproducao	5.05.04.03-7 Fisiopatologia da Reprodução Animal	\N
3273	095	a	animal	5.05.04.03-7 Fisiopatologia da Reprodução Animal	\N
3274	095	a	5.05.05.00-9	5.05.05.00-9 Inspeção de Produtos de Origem Animal	\N
3275	095	a	inspecao	5.05.05.00-9 Inspeção de Produtos de Origem Animal	\N
3276	095	a	de	5.05.05.00-9 Inspeção de Produtos de Origem Animal	\N
3277	095	a	produtos	5.05.05.00-9 Inspeção de Produtos de Origem Animal	\N
3278	095	a	de	5.05.05.00-9 Inspeção de Produtos de Origem Animal	\N
3279	095	a	origem	5.05.05.00-9 Inspeção de Produtos de Origem Animal	\N
3280	095	a	animal	5.05.05.00-9 Inspeção de Produtos de Origem Animal	\N
3281	095	a	5.06.00.00-1	5.06.00.00-1 Recursos Pesqueiros e Engenharia de Pesca	\N
3282	095	a	recursos	5.06.00.00-1 Recursos Pesqueiros e Engenharia de Pesca	\N
3283	095	a	pesqueiros	5.06.00.00-1 Recursos Pesqueiros e Engenharia de Pesca	\N
3284	095	a	engenharia	5.06.00.00-1 Recursos Pesqueiros e Engenharia de Pesca	\N
3285	095	a	de	5.06.00.00-1 Recursos Pesqueiros e Engenharia de Pesca	\N
3286	095	a	pesca	5.06.00.00-1 Recursos Pesqueiros e Engenharia de Pesca	\N
3287	095	a	5.06.01.00-8	5.06.01.00-8 Recursos Pesqueiros Marinhos	\N
3288	095	a	recursos	5.06.01.00-8 Recursos Pesqueiros Marinhos	\N
3289	095	a	pesqueiros	5.06.01.00-8 Recursos Pesqueiros Marinhos	\N
3290	095	a	marinhos	5.06.01.00-8 Recursos Pesqueiros Marinhos	\N
3291	095	a	5.06.01.01-6	5.06.01.01-6 Fatores Abióticos do Mar	\N
3292	095	a	fatores	5.06.01.01-6 Fatores Abióticos do Mar	\N
3293	095	a	abioticos	5.06.01.01-6 Fatores Abióticos do Mar	\N
3294	095	a	do	5.06.01.01-6 Fatores Abióticos do Mar	\N
3295	095	a	mar	5.06.01.01-6 Fatores Abióticos do Mar	\N
3296	095	a	5.06.01.02-4	5.06.01.02-4 Avaliação de Estoques Pesqueiros Marinhos	\N
3297	095	a	avaliacao	5.06.01.02-4 Avaliação de Estoques Pesqueiros Marinhos	\N
3298	095	a	de	5.06.01.02-4 Avaliação de Estoques Pesqueiros Marinhos	\N
3299	095	a	estoques	5.06.01.02-4 Avaliação de Estoques Pesqueiros Marinhos	\N
3300	095	a	pesqueiros	5.06.01.02-4 Avaliação de Estoques Pesqueiros Marinhos	\N
3301	095	a	marinhos	5.06.01.02-4 Avaliação de Estoques Pesqueiros Marinhos	\N
3302	095	a	5.06.01.03-2	5.06.01.03-2 Exploração Pesqueira Marinha	\N
3303	095	a	exploracao	5.06.01.03-2 Exploração Pesqueira Marinha	\N
3304	095	a	pesqueira	5.06.01.03-2 Exploração Pesqueira Marinha	\N
3305	095	a	marinha	5.06.01.03-2 Exploração Pesqueira Marinha	\N
3306	095	a	5.06.01.04-0	5.06.01.04-0 Manejo e Conservação de Recursos Pesqueiros Marinhos	\N
3552	095	a	especiais	6.01.04.00-7 Direitos Especiais	\N
3307	095	a	manejo	5.06.01.04-0 Manejo e Conservação de Recursos Pesqueiros Marinhos	\N
3308	095	a	conservacao	5.06.01.04-0 Manejo e Conservação de Recursos Pesqueiros Marinhos	\N
3309	095	a	de	5.06.01.04-0 Manejo e Conservação de Recursos Pesqueiros Marinhos	\N
3310	095	a	recursos	5.06.01.04-0 Manejo e Conservação de Recursos Pesqueiros Marinhos	\N
3311	095	a	pesqueiros	5.06.01.04-0 Manejo e Conservação de Recursos Pesqueiros Marinhos	\N
3312	095	a	marinhos	5.06.01.04-0 Manejo e Conservação de Recursos Pesqueiros Marinhos	\N
3313	095	a	5.06.02.00-4	5.06.02.00-4 Recursos Pesqueiros de Águas Interiores	\N
3314	095	a	recursos	5.06.02.00-4 Recursos Pesqueiros de Águas Interiores	\N
3315	095	a	pesqueiros	5.06.02.00-4 Recursos Pesqueiros de Águas Interiores	\N
3316	095	a	de	5.06.02.00-4 Recursos Pesqueiros de Águas Interiores	\N
3317	095	a	aguas	5.06.02.00-4 Recursos Pesqueiros de Águas Interiores	\N
3318	095	a	interiores	5.06.02.00-4 Recursos Pesqueiros de Águas Interiores	\N
3319	095	a	5.06.02.01-2	5.06.02.01-2 Fatores Abióticos de Águas Interiores	\N
3320	095	a	fatores	5.06.02.01-2 Fatores Abióticos de Águas Interiores	\N
3321	095	a	abioticos	5.06.02.01-2 Fatores Abióticos de Águas Interiores	\N
3322	095	a	de	5.06.02.01-2 Fatores Abióticos de Águas Interiores	\N
3323	095	a	aguas	5.06.02.01-2 Fatores Abióticos de Águas Interiores	\N
3324	095	a	interiores	5.06.02.01-2 Fatores Abióticos de Águas Interiores	\N
3325	095	a	5.06.02.02-0	5.06.02.02-0 Avaliação de Estoques Pesqueiros de Águas Interiores	\N
3326	095	a	avaliacao	5.06.02.02-0 Avaliação de Estoques Pesqueiros de Águas Interiores	\N
3327	095	a	de	5.06.02.02-0 Avaliação de Estoques Pesqueiros de Águas Interiores	\N
3328	095	a	estoques	5.06.02.02-0 Avaliação de Estoques Pesqueiros de Águas Interiores	\N
3329	095	a	pesqueiros	5.06.02.02-0 Avaliação de Estoques Pesqueiros de Águas Interiores	\N
3330	095	a	de	5.06.02.02-0 Avaliação de Estoques Pesqueiros de Águas Interiores	\N
3331	095	a	aguas	5.06.02.02-0 Avaliação de Estoques Pesqueiros de Águas Interiores	\N
3332	095	a	interiores	5.06.02.02-0 Avaliação de Estoques Pesqueiros de Águas Interiores	\N
3333	095	a	5.06.02.03-9	5.06.02.03-9 Explotação Pesqueira de Águas Interiores	\N
3334	095	a	explotacao	5.06.02.03-9 Explotação Pesqueira de Águas Interiores	\N
3335	095	a	pesqueira	5.06.02.03-9 Explotação Pesqueira de Águas Interiores	\N
3336	095	a	de	5.06.02.03-9 Explotação Pesqueira de Águas Interiores	\N
3337	095	a	aguas	5.06.02.03-9 Explotação Pesqueira de Águas Interiores	\N
3338	095	a	interiores	5.06.02.03-9 Explotação Pesqueira de Águas Interiores	\N
3339	095	a	5.06.02.04-7	5.06.02.04-7 Manejo e Conservação de Recursos Pesqueiros de Águas Interiores	\N
3340	095	a	manejo	5.06.02.04-7 Manejo e Conservação de Recursos Pesqueiros de Águas Interiores	\N
3341	095	a	conservacao	5.06.02.04-7 Manejo e Conservação de Recursos Pesqueiros de Águas Interiores	\N
3342	095	a	de	5.06.02.04-7 Manejo e Conservação de Recursos Pesqueiros de Águas Interiores	\N
3343	095	a	recursos	5.06.02.04-7 Manejo e Conservação de Recursos Pesqueiros de Águas Interiores	\N
3344	095	a	pesqueiros	5.06.02.04-7 Manejo e Conservação de Recursos Pesqueiros de Águas Interiores	\N
3345	095	a	de	5.06.02.04-7 Manejo e Conservação de Recursos Pesqueiros de Águas Interiores	\N
3346	095	a	aguas	5.06.02.04-7 Manejo e Conservação de Recursos Pesqueiros de Águas Interiores	\N
3347	095	a	interiores	5.06.02.04-7 Manejo e Conservação de Recursos Pesqueiros de Águas Interiores	\N
3348	095	a	5.06.03.00-0	5.06.03.00-0 Aqüicultura	\N
3349	095	a	aquicultura	5.06.03.00-0 Aqüicultura	\N
3350	095	a	5.06.03.01-9	5.06.03.01-9 Maricultura	\N
3351	095	a	maricultura	5.06.03.01-9 Maricultura	\N
3352	095	a	5.06.03.02-7	5.06.03.02-7 Carcinocultura	\N
3353	095	a	carcinocultura	5.06.03.02-7 Carcinocultura	\N
3354	095	a	5.06.03.03-5	5.06.03.03-5 Ostreicultura	\N
3355	095	a	ostreicultura	5.06.03.03-5 Ostreicultura	\N
3356	095	a	5.06.03.04-3	5.06.03.04-3 Piscicultura	\N
3357	095	a	piscicultura	5.06.03.04-3 Piscicultura	\N
3358	095	a	5.06.04.00-7	5.06.04.00-7 Engenharia de Pesca	\N
3359	095	a	engenharia	5.06.04.00-7 Engenharia de Pesca	\N
3360	095	a	de	5.06.04.00-7 Engenharia de Pesca	\N
3361	095	a	pesca	5.06.04.00-7 Engenharia de Pesca	\N
3362	095	a	5.07.00.00-6	5.07.00.00-6 Ciência e Tecnologia de Alimentos	\N
3363	095	a	ciencia	5.07.00.00-6 Ciência e Tecnologia de Alimentos	\N
3364	095	a	tecnologia	5.07.00.00-6 Ciência e Tecnologia de Alimentos	\N
3365	095	a	de	5.07.00.00-6 Ciência e Tecnologia de Alimentos	\N
3366	095	a	alimentos	5.07.00.00-6 Ciência e Tecnologia de Alimentos	\N
3367	095	a	5.07.01.00-2	5.07.01.00-2 Ciência de Alimentos	\N
3368	095	a	ciencia	5.07.01.00-2 Ciência de Alimentos	\N
3369	095	a	de	5.07.01.00-2 Ciência de Alimentos	\N
3370	095	a	alimentos	5.07.01.00-2 Ciência de Alimentos	\N
3371	095	a	5.07.01.01-0	5.07.01.01-0 Valor Nutritivo de Alimentos	\N
3372	095	a	valor	5.07.01.01-0 Valor Nutritivo de Alimentos	\N
3373	095	a	nutritivo	5.07.01.01-0 Valor Nutritivo de Alimentos	\N
3374	095	a	de	5.07.01.01-0 Valor Nutritivo de Alimentos	\N
3375	095	a	alimentos	5.07.01.01-0 Valor Nutritivo de Alimentos	\N
3376	095	a	5.07.01.02-9	5.07.01.02-9 Química, Física, Físico-Química e Bioquímica dos Alim. e das Mat.-Primas Alimentares	\N
3377	095	a	quimica,	5.07.01.02-9 Química, Física, Físico-Química e Bioquímica dos Alim. e das Mat.-Primas Alimentares	\N
3378	095	a	fisica,	5.07.01.02-9 Química, Física, Físico-Química e Bioquímica dos Alim. e das Mat.-Primas Alimentares	\N
3379	095	a	fisico-quimica	5.07.01.02-9 Química, Física, Físico-Química e Bioquímica dos Alim. e das Mat.-Primas Alimentares	\N
3380	095	a	bioquimica	5.07.01.02-9 Química, Física, Físico-Química e Bioquímica dos Alim. e das Mat.-Primas Alimentares	\N
3381	095	a	dos	5.07.01.02-9 Química, Física, Físico-Química e Bioquímica dos Alim. e das Mat.-Primas Alimentares	\N
4969	095	a	musica	8.03.03.00-5 Música	\N
3382	095	a	alim.	5.07.01.02-9 Química, Física, Físico-Química e Bioquímica dos Alim. e das Mat.-Primas Alimentares	\N
3383	095	a	das	5.07.01.02-9 Química, Física, Físico-Química e Bioquímica dos Alim. e das Mat.-Primas Alimentares	\N
3384	095	a	mat.-primas	5.07.01.02-9 Química, Física, Físico-Química e Bioquímica dos Alim. e das Mat.-Primas Alimentares	\N
3385	095	a	alimentares	5.07.01.02-9 Química, Física, Físico-Química e Bioquímica dos Alim. e das Mat.-Primas Alimentares	\N
3386	095	a	5.07.01.03-7	5.07.01.03-7 Microbiologia de Alimentos	\N
3387	095	a	microbiologia	5.07.01.03-7 Microbiologia de Alimentos	\N
3388	095	a	de	5.07.01.03-7 Microbiologia de Alimentos	\N
3389	095	a	alimentos	5.07.01.03-7 Microbiologia de Alimentos	\N
3390	095	a	5.07.01.04-5	5.07.01.04-5 Fisiologia Pós-Colheita	\N
3391	095	a	fisiologia	5.07.01.04-5 Fisiologia Pós-Colheita	\N
3392	095	a	pos-colheita	5.07.01.04-5 Fisiologia Pós-Colheita	\N
3393	095	a	5.07.01.05-3	5.07.01.05-3 Toxicidade e Resíduos de Pesticidas em Alimentos	\N
3394	095	a	toxicidade	5.07.01.05-3 Toxicidade e Resíduos de Pesticidas em Alimentos	\N
3395	095	a	residuos	5.07.01.05-3 Toxicidade e Resíduos de Pesticidas em Alimentos	\N
3396	095	a	de	5.07.01.05-3 Toxicidade e Resíduos de Pesticidas em Alimentos	\N
3397	095	a	pesticidas	5.07.01.05-3 Toxicidade e Resíduos de Pesticidas em Alimentos	\N
3398	095	a	em	5.07.01.05-3 Toxicidade e Resíduos de Pesticidas em Alimentos	\N
3399	095	a	alimentos	5.07.01.05-3 Toxicidade e Resíduos de Pesticidas em Alimentos	\N
3400	095	a	5.07.01.06-1	5.07.01.06-1 Avaliação e Controle de Qualidade de Alimentos	\N
3401	095	a	avaliacao	5.07.01.06-1 Avaliação e Controle de Qualidade de Alimentos	\N
3402	095	a	controle	5.07.01.06-1 Avaliação e Controle de Qualidade de Alimentos	\N
3403	095	a	de	5.07.01.06-1 Avaliação e Controle de Qualidade de Alimentos	\N
3404	095	a	qualidade	5.07.01.06-1 Avaliação e Controle de Qualidade de Alimentos	\N
3405	095	a	de	5.07.01.06-1 Avaliação e Controle de Qualidade de Alimentos	\N
3406	095	a	alimentos	5.07.01.06-1 Avaliação e Controle de Qualidade de Alimentos	\N
3407	095	a	5.07.01.07-0	5.07.01.07-0 Padrões, Legislação e Fiscalização de Alimentos	\N
3408	095	a	padroes,	5.07.01.07-0 Padrões, Legislação e Fiscalização de Alimentos	\N
3409	095	a	legislacao	5.07.01.07-0 Padrões, Legislação e Fiscalização de Alimentos	\N
3410	095	a	fiscalizacao	5.07.01.07-0 Padrões, Legislação e Fiscalização de Alimentos	\N
3411	095	a	de	5.07.01.07-0 Padrões, Legislação e Fiscalização de Alimentos	\N
3412	095	a	alimentos	5.07.01.07-0 Padrões, Legislação e Fiscalização de Alimentos	\N
3413	095	a	5.07.02.00-9	5.07.02.00-9 Tecnologia de Alimentos	\N
3414	095	a	tecnologia	5.07.02.00-9 Tecnologia de Alimentos	\N
3415	095	a	de	5.07.02.00-9 Tecnologia de Alimentos	\N
3416	095	a	alimentos	5.07.02.00-9 Tecnologia de Alimentos	\N
3417	095	a	5.07.02.01-7	5.07.02.01-7 Tecnologia de Produtos de Origem Animal	\N
3418	095	a	tecnologia	5.07.02.01-7 Tecnologia de Produtos de Origem Animal	\N
3419	095	a	de	5.07.02.01-7 Tecnologia de Produtos de Origem Animal	\N
3420	095	a	produtos	5.07.02.01-7 Tecnologia de Produtos de Origem Animal	\N
3421	095	a	de	5.07.02.01-7 Tecnologia de Produtos de Origem Animal	\N
3422	095	a	origem	5.07.02.01-7 Tecnologia de Produtos de Origem Animal	\N
3423	095	a	animal	5.07.02.01-7 Tecnologia de Produtos de Origem Animal	\N
3424	095	a	5.07.02.02-5	5.07.02.02-5 Tecnologia de Produtos de Origem Vegetal	\N
3425	095	a	tecnologia	5.07.02.02-5 Tecnologia de Produtos de Origem Vegetal	\N
3426	095	a	de	5.07.02.02-5 Tecnologia de Produtos de Origem Vegetal	\N
3427	095	a	produtos	5.07.02.02-5 Tecnologia de Produtos de Origem Vegetal	\N
3428	095	a	de	5.07.02.02-5 Tecnologia de Produtos de Origem Vegetal	\N
3429	095	a	origem	5.07.02.02-5 Tecnologia de Produtos de Origem Vegetal	\N
3430	095	a	vegetal	5.07.02.02-5 Tecnologia de Produtos de Origem Vegetal	\N
3431	095	a	5.07.02.03-3	5.07.02.03-3 Tecnologia das Bebidas	\N
3432	095	a	tecnologia	5.07.02.03-3 Tecnologia das Bebidas	\N
3433	095	a	das	5.07.02.03-3 Tecnologia das Bebidas	\N
3434	095	a	bebidas	5.07.02.03-3 Tecnologia das Bebidas	\N
3435	095	a	5.07.02.04-1	5.07.02.04-1 Tecnologia de Alimentos Dietéticos e Nutricionais	\N
3436	095	a	tecnologia	5.07.02.04-1 Tecnologia de Alimentos Dietéticos e Nutricionais	\N
3437	095	a	de	5.07.02.04-1 Tecnologia de Alimentos Dietéticos e Nutricionais	\N
3438	095	a	alimentos	5.07.02.04-1 Tecnologia de Alimentos Dietéticos e Nutricionais	\N
3439	095	a	dieteticos	5.07.02.04-1 Tecnologia de Alimentos Dietéticos e Nutricionais	\N
3440	095	a	nutricionais	5.07.02.04-1 Tecnologia de Alimentos Dietéticos e Nutricionais	\N
3441	095	a	5.07.02.05-0	5.07.02.05-0 Aproveitamento de Subprodutos	\N
3442	095	a	aproveitamento	5.07.02.05-0 Aproveitamento de Subprodutos	\N
3443	095	a	de	5.07.02.05-0 Aproveitamento de Subprodutos	\N
3444	095	a	subprodutos	5.07.02.05-0 Aproveitamento de Subprodutos	\N
3445	095	a	5.07.02.06-8	5.07.02.06-8 Embalagens de Produtos Alimentares	\N
3446	095	a	embalagens	5.07.02.06-8 Embalagens de Produtos Alimentares	\N
3447	095	a	de	5.07.02.06-8 Embalagens de Produtos Alimentares	\N
3448	095	a	produtos	5.07.02.06-8 Embalagens de Produtos Alimentares	\N
3449	095	a	alimentares	5.07.02.06-8 Embalagens de Produtos Alimentares	\N
3450	095	a	5.07.03.00-5	5.07.03.00-5 Engenharia de Alimentos	\N
3451	095	a	engenharia	5.07.03.00-5 Engenharia de Alimentos	\N
3452	095	a	de	5.07.03.00-5 Engenharia de Alimentos	\N
3453	095	a	alimentos	5.07.03.00-5 Engenharia de Alimentos	\N
3454	095	a	5.07.03.01-3	5.07.03.01-3 Instalações Industriais de Produção de Alimentos	\N
3455	095	a	instalacoes	5.07.03.01-3 Instalações Industriais de Produção de Alimentos	\N
3456	095	a	industriais	5.07.03.01-3 Instalações Industriais de Produção de Alimentos	\N
3457	095	a	de	5.07.03.01-3 Instalações Industriais de Produção de Alimentos	\N
3458	095	a	producao	5.07.03.01-3 Instalações Industriais de Produção de Alimentos	\N
3459	095	a	de	5.07.03.01-3 Instalações Industriais de Produção de Alimentos	\N
3460	095	a	alimentos	5.07.03.01-3 Instalações Industriais de Produção de Alimentos	\N
3461	095	a	5.07.03.02-1	5.07.03.02-1 Armazenamento de Alimentos	\N
3462	095	a	armazenamento	5.07.03.02-1 Armazenamento de Alimentos	\N
3463	095	a	de	5.07.03.02-1 Armazenamento de Alimentos	\N
3464	095	a	alimentos	5.07.03.02-1 Armazenamento de Alimentos	\N
3465	095	a	6.00.00.00-7	6.00.00.00-7 Ciências Sociais Aplicadas	\N
3466	095	a	ciencias	6.00.00.00-7 Ciências Sociais Aplicadas	\N
3467	095	a	sociais	6.00.00.00-7 Ciências Sociais Aplicadas	\N
3468	095	a	aplicadas	6.00.00.00-7 Ciências Sociais Aplicadas	\N
3469	095	a	6.01.00.00-1	6.01.00.00-1 Direito	\N
3470	095	a	direito	6.01.00.00-1 Direito	\N
3471	095	a	6.01.01.00-8	6.01.01.00-8 Teoria do Direito	\N
3472	095	a	teoria	6.01.01.00-8 Teoria do Direito	\N
3473	095	a	do	6.01.01.00-8 Teoria do Direito	\N
3474	095	a	direito	6.01.01.00-8 Teoria do Direito	\N
3475	095	a	6.01.01.01-6	6.01.01.01-6 Teoria Geral do Direito	\N
3476	095	a	teoria	6.01.01.01-6 Teoria Geral do Direito	\N
3477	095	a	geral	6.01.01.01-6 Teoria Geral do Direito	\N
3478	095	a	do	6.01.01.01-6 Teoria Geral do Direito	\N
3479	095	a	direito	6.01.01.01-6 Teoria Geral do Direito	\N
3480	095	a	6.01.01.02-4	6.01.01.02-4 Teoria Geral do Processo	\N
3481	095	a	teoria	6.01.01.02-4 Teoria Geral do Processo	\N
3482	095	a	geral	6.01.01.02-4 Teoria Geral do Processo	\N
3483	095	a	do	6.01.01.02-4 Teoria Geral do Processo	\N
3484	095	a	processo	6.01.01.02-4 Teoria Geral do Processo	\N
3485	095	a	6.01.01.03-2	6.01.01.03-2 Teoria do Estado	\N
3486	095	a	teoria	6.01.01.03-2 Teoria do Estado	\N
3487	095	a	do	6.01.01.03-2 Teoria do Estado	\N
3488	095	a	estado	6.01.01.03-2 Teoria do Estado	\N
3489	095	a	6.01.01.04-0	6.01.01.04-0 História do Direito	\N
3490	095	a	historia	6.01.01.04-0 História do Direito	\N
3491	095	a	do	6.01.01.04-0 História do Direito	\N
3492	095	a	direito	6.01.01.04-0 História do Direito	\N
3493	095	a	6.01.01.05-9	6.01.01.05-9 Filosofia do Direito	\N
3494	095	a	filosofia	6.01.01.05-9 Filosofia do Direito	\N
3495	095	a	do	6.01.01.05-9 Filosofia do Direito	\N
3496	095	a	direito	6.01.01.05-9 Filosofia do Direito	\N
3497	095	a	6.01.01.06-7	6.01.01.06-7 Lógica Jurídica	\N
3498	095	a	logica	6.01.01.06-7 Lógica Jurídica	\N
3499	095	a	juridica	6.01.01.06-7 Lógica Jurídica	\N
3500	095	a	6.01.01.07-5	6.01.01.07-5 Sociologia Jurídica	\N
3501	095	a	sociologia	6.01.01.07-5 Sociologia Jurídica	\N
3502	095	a	juridica	6.01.01.07-5 Sociologia Jurídica	\N
3503	095	a	6.01.01.08-3	6.01.01.08-3 Antropologia Jurídica	\N
3504	095	a	antropologia	6.01.01.08-3 Antropologia Jurídica	\N
3505	095	a	juridica	6.01.01.08-3 Antropologia Jurídica	\N
3506	095	a	6.01.02.00-4	6.01.02.00-4 Direito Público	\N
3507	095	a	direito	6.01.02.00-4 Direito Público	\N
3508	095	a	publico	6.01.02.00-4 Direito Público	\N
3509	095	a	6.01.02.01-2	6.01.02.01-2 Direito Tributário	\N
3510	095	a	direito	6.01.02.01-2 Direito Tributário	\N
3511	095	a	tributario	6.01.02.01-2 Direito Tributário	\N
3512	095	a	6.01.02.02-0	6.01.02.02-0 Direito Penal	\N
3513	095	a	direito	6.01.02.02-0 Direito Penal	\N
3514	095	a	penal	6.01.02.02-0 Direito Penal	\N
3515	095	a	6.01.02.03-9	6.01.02.03-9 Direito Processual Penal	\N
3516	095	a	direito	6.01.02.03-9 Direito Processual Penal	\N
3517	095	a	processual	6.01.02.03-9 Direito Processual Penal	\N
3518	095	a	penal	6.01.02.03-9 Direito Processual Penal	\N
3519	095	a	6.01.02.04-7	6.01.02.04-7 Direito Processual Civil	\N
3520	095	a	direito	6.01.02.04-7 Direito Processual Civil	\N
3521	095	a	processual	6.01.02.04-7 Direito Processual Civil	\N
3522	095	a	civil	6.01.02.04-7 Direito Processual Civil	\N
3523	095	a	6.01.02.05-5	6.01.02.05-5 Direito Constitucional	\N
3524	095	a	direito	6.01.02.05-5 Direito Constitucional	\N
3525	095	a	constitucional	6.01.02.05-5 Direito Constitucional	\N
3526	095	a	6.01.02.06-3	6.01.02.06-3 Direito Administrativo	\N
3527	095	a	direito	6.01.02.06-3 Direito Administrativo	\N
3528	095	a	administrativo	6.01.02.06-3 Direito Administrativo	\N
3529	095	a	6.01.02.07-1	6.01.02.07-1 Direito Internacional Público	\N
3530	095	a	direito	6.01.02.07-1 Direito Internacional Público	\N
3531	095	a	internacional	6.01.02.07-1 Direito Internacional Público	\N
3532	095	a	publico	6.01.02.07-1 Direito Internacional Público	\N
3533	095	a	6.01.03.00-0	6.01.03.00-0 Direito Privado	\N
3534	095	a	direito	6.01.03.00-0 Direito Privado	\N
3535	095	a	privado	6.01.03.00-0 Direito Privado	\N
3536	095	a	6.01.03.01-9	6.01.03.01-9 Direito Civil	\N
3537	095	a	direito	6.01.03.01-9 Direito Civil	\N
3538	095	a	civil	6.01.03.01-9 Direito Civil	\N
3539	095	a	6.01.03.02-7	6.01.03.02-7 Direito Comercial	\N
3540	095	a	direito	6.01.03.02-7 Direito Comercial	\N
3541	095	a	comercial	6.01.03.02-7 Direito Comercial	\N
3542	095	a	6.01.03.03-5	6.01.03.03-5 Direito do Trabalho	\N
3543	095	a	direito	6.01.03.03-5 Direito do Trabalho	\N
3544	095	a	do	6.01.03.03-5 Direito do Trabalho	\N
3545	095	a	trabalho	6.01.03.03-5 Direito do Trabalho	\N
3546	095	a	6.01.03.04-3	6.01.03.04-3 Direito Internacional Privado	\N
3547	095	a	direito	6.01.03.04-3 Direito Internacional Privado	\N
3548	095	a	internacional	6.01.03.04-3 Direito Internacional Privado	\N
3549	095	a	privado	6.01.03.04-3 Direito Internacional Privado	\N
3550	095	a	6.01.04.00-7	6.01.04.00-7 Direitos Especiais	\N
3551	095	a	direitos	6.01.04.00-7 Direitos Especiais	\N
3553	095	a	6.02.00.00-6	6.02.00.00-6 Administração	\N
3554	095	a	administracao	6.02.00.00-6 Administração	\N
3555	095	a	6.02.01.00-2	6.02.01.00-2 Administração de Empresas	\N
3556	095	a	administracao	6.02.01.00-2 Administração de Empresas	\N
3557	095	a	de	6.02.01.00-2 Administração de Empresas	\N
3558	095	a	empresas	6.02.01.00-2 Administração de Empresas	\N
3559	095	a	6.02.01.01-0	6.02.01.01-0 Administração da Produção	\N
3560	095	a	administracao	6.02.01.01-0 Administração da Produção	\N
3561	095	a	da	6.02.01.01-0 Administração da Produção	\N
3562	095	a	producao	6.02.01.01-0 Administração da Produção	\N
3563	095	a	6.02.01.02-9	6.02.01.02-9 Administração Financeira	\N
3564	095	a	administracao	6.02.01.02-9 Administração Financeira	\N
3565	095	a	financeira	6.02.01.02-9 Administração Financeira	\N
3566	095	a	6.02.01.03-7	6.02.01.03-7 Mercadologia	\N
3567	095	a	mercadologia	6.02.01.03-7 Mercadologia	\N
3568	095	a	6.02.01.04-5	6.02.01.04-5 Negócios Internacionais	\N
3569	095	a	negocios	6.02.01.04-5 Negócios Internacionais	\N
3570	095	a	internacionais	6.02.01.04-5 Negócios Internacionais	\N
3571	095	a	6.02.01.05-3	6.02.01.05-3 Administração de Recursos Humanos	\N
3572	095	a	administracao	6.02.01.05-3 Administração de Recursos Humanos	\N
3573	095	a	de	6.02.01.05-3 Administração de Recursos Humanos	\N
3574	095	a	recursos	6.02.01.05-3 Administração de Recursos Humanos	\N
3575	095	a	humanos	6.02.01.05-3 Administração de Recursos Humanos	\N
3576	095	a	6.02.02.00-9	6.02.02.00-9 Administração Pública	\N
3577	095	a	administracao	6.02.02.00-9 Administração Pública	\N
3578	095	a	publica	6.02.02.00-9 Administração Pública	\N
3579	095	a	6.02.02.01-7	6.02.02.01-7 Contabilidade e Financas Públicas	\N
3580	095	a	contabilidade	6.02.02.01-7 Contabilidade e Financas Públicas	\N
3581	095	a	financas	6.02.02.01-7 Contabilidade e Financas Públicas	\N
3582	095	a	publicas	6.02.02.01-7 Contabilidade e Financas Públicas	\N
3583	095	a	6.02.02.02-5	6.02.02.02-5 Organizações Públicas	\N
3584	095	a	organizacoes	6.02.02.02-5 Organizações Públicas	\N
3585	095	a	publicas	6.02.02.02-5 Organizações Públicas	\N
3586	095	a	6.02.02.03-3	6.02.02.03-3 Política e Planejamento Governamentais	\N
3587	095	a	politica	6.02.02.03-3 Política e Planejamento Governamentais	\N
3588	095	a	planejamento	6.02.02.03-3 Política e Planejamento Governamentais	\N
3589	095	a	governamentais	6.02.02.03-3 Política e Planejamento Governamentais	\N
3590	095	a	6.02.02.04-1	6.02.02.04-1 Administração de Pessoal	\N
3591	095	a	administracao	6.02.02.04-1 Administração de Pessoal	\N
3592	095	a	de	6.02.02.04-1 Administração de Pessoal	\N
3593	095	a	pessoal	6.02.02.04-1 Administração de Pessoal	\N
3594	095	a	6.02.03.00-5	6.02.03.00-5 Administração de Setores Específicos	\N
3595	095	a	administracao	6.02.03.00-5 Administração de Setores Específicos	\N
3596	095	a	de	6.02.03.00-5 Administração de Setores Específicos	\N
3597	095	a	setores	6.02.03.00-5 Administração de Setores Específicos	\N
3598	095	a	especificos	6.02.03.00-5 Administração de Setores Específicos	\N
3599	095	a	6.02.04.00-1	6.02.04.00-1 Ciências Contábeis	\N
3600	095	a	ciencias	6.02.04.00-1 Ciências Contábeis	\N
3601	095	a	contabeis	6.02.04.00-1 Ciências Contábeis	\N
3602	095	a	6.03.00.00-0	6.03.00.00-0 Economia	\N
3603	095	a	economia	6.03.00.00-0 Economia	\N
3604	095	a	6.03.01.00-7	6.03.01.00-7 Teoria Econômica	\N
3605	095	a	teoria	6.03.01.00-7 Teoria Econômica	\N
3606	095	a	economica	6.03.01.00-7 Teoria Econômica	\N
3607	095	a	6.03.01.01-5	6.03.01.01-5 Economia Geral	\N
3608	095	a	economia	6.03.01.01-5 Economia Geral	\N
3609	095	a	geral	6.03.01.01-5 Economia Geral	\N
3610	095	a	6.03.01.02-3	6.03.01.02-3 Teoria Geral da Economia	\N
3611	095	a	teoria	6.03.01.02-3 Teoria Geral da Economia	\N
3612	095	a	geral	6.03.01.02-3 Teoria Geral da Economia	\N
3613	095	a	da	6.03.01.02-3 Teoria Geral da Economia	\N
3614	095	a	economia	6.03.01.02-3 Teoria Geral da Economia	\N
3615	095	a	6.03.01.03-1	6.03.01.03-1 História do Pensamento Econômico	\N
3616	095	a	historia	6.03.01.03-1 História do Pensamento Econômico	\N
3617	095	a	do	6.03.01.03-1 História do Pensamento Econômico	\N
3618	095	a	pensamento	6.03.01.03-1 História do Pensamento Econômico	\N
3619	095	a	economico	6.03.01.03-1 História do Pensamento Econômico	\N
3620	095	a	6.03.01.04-0	6.03.01.04-0 História Econômica	\N
3621	095	a	historia	6.03.01.04-0 História Econômica	\N
3622	095	a	economica	6.03.01.04-0 História Econômica	\N
3623	095	a	6.03.01.05-8	6.03.01.05-8 Sistemas Econômicos	\N
3624	095	a	sistemas	6.03.01.05-8 Sistemas Econômicos	\N
3625	095	a	economicos	6.03.01.05-8 Sistemas Econômicos	\N
3626	095	a	6.03.02.00-3	6.03.02.00-3 Métodos Quantitativos em Economia	\N
3627	095	a	metodos	6.03.02.00-3 Métodos Quantitativos em Economia	\N
3628	095	a	quantitativos	6.03.02.00-3 Métodos Quantitativos em Economia	\N
3629	095	a	em	6.03.02.00-3 Métodos Quantitativos em Economia	\N
3630	095	a	economia	6.03.02.00-3 Métodos Quantitativos em Economia	\N
3631	095	a	6.03.02.01-1	6.03.02.01-1 Métodos e Modelos Matemáticos, Econométricos e Estatísticos	\N
3632	095	a	metodos	6.03.02.01-1 Métodos e Modelos Matemáticos, Econométricos e Estatísticos	\N
3633	095	a	modelos	6.03.02.01-1 Métodos e Modelos Matemáticos, Econométricos e Estatísticos	\N
3634	095	a	matematicos,	6.03.02.01-1 Métodos e Modelos Matemáticos, Econométricos e Estatísticos	\N
3635	095	a	econometricos	6.03.02.01-1 Métodos e Modelos Matemáticos, Econométricos e Estatísticos	\N
3636	095	a	estatisticos	6.03.02.01-1 Métodos e Modelos Matemáticos, Econométricos e Estatísticos	\N
3637	095	a	6.03.02.02-0	6.03.02.02-0 Estatística Sócio-Econômica	\N
3638	095	a	estatistica	6.03.02.02-0 Estatística Sócio-Econômica	\N
3639	095	a	socio-economica	6.03.02.02-0 Estatística Sócio-Econômica	\N
3640	095	a	6.03.02.03-8	6.03.02.03-8 Contabilidade Nacional	\N
3641	095	a	contabilidade	6.03.02.03-8 Contabilidade Nacional	\N
3642	095	a	nacional	6.03.02.03-8 Contabilidade Nacional	\N
3643	095	a	6.03.02.04-6	6.03.02.04-6 Economia Matemática	\N
3644	095	a	economia	6.03.02.04-6 Economia Matemática	\N
3645	095	a	matematica	6.03.02.04-6 Economia Matemática	\N
3646	095	a	6.03.03.00-0	6.03.03.00-0 Economia Monetária e Fiscal	\N
3647	095	a	economia	6.03.03.00-0 Economia Monetária e Fiscal	\N
3648	095	a	monetaria	6.03.03.00-0 Economia Monetária e Fiscal	\N
3649	095	a	fiscal	6.03.03.00-0 Economia Monetária e Fiscal	\N
3650	095	a	6.03.03.01-8	6.03.03.01-8 Teoria Monetária e Financeira	\N
3651	095	a	teoria	6.03.03.01-8 Teoria Monetária e Financeira	\N
3652	095	a	monetaria	6.03.03.01-8 Teoria Monetária e Financeira	\N
3653	095	a	financeira	6.03.03.01-8 Teoria Monetária e Financeira	\N
3654	095	a	6.03.03.02-6	6.03.03.02-6 Instituições Monetárias e Financeiras do Brasil	\N
3655	095	a	instituicoes	6.03.03.02-6 Instituições Monetárias e Financeiras do Brasil	\N
3656	095	a	monetarias	6.03.03.02-6 Instituições Monetárias e Financeiras do Brasil	\N
3657	095	a	financeiras	6.03.03.02-6 Instituições Monetárias e Financeiras do Brasil	\N
3658	095	a	do	6.03.03.02-6 Instituições Monetárias e Financeiras do Brasil	\N
3659	095	a	brasil	6.03.03.02-6 Instituições Monetárias e Financeiras do Brasil	\N
3660	095	a	6.03.03.03-4	6.03.03.03-4 Financas Públicas Internas	\N
3661	095	a	financas	6.03.03.03-4 Financas Públicas Internas	\N
3662	095	a	publicas	6.03.03.03-4 Financas Públicas Internas	\N
3663	095	a	internas	6.03.03.03-4 Financas Públicas Internas	\N
3664	095	a	6.03.03.04-2	6.03.03.04-2 Política Fiscal do Brasil	\N
3665	095	a	politica	6.03.03.04-2 Política Fiscal do Brasil	\N
3666	095	a	fiscal	6.03.03.04-2 Política Fiscal do Brasil	\N
3667	095	a	do	6.03.03.04-2 Política Fiscal do Brasil	\N
3668	095	a	brasil	6.03.03.04-2 Política Fiscal do Brasil	\N
3669	095	a	6.03.04.00-6	6.03.04.00-6 Crescimento, Flutuações e Planejamento Econômico	\N
3670	095	a	crescimento,	6.03.04.00-6 Crescimento, Flutuações e Planejamento Econômico	\N
3671	095	a	flutuacoes	6.03.04.00-6 Crescimento, Flutuações e Planejamento Econômico	\N
3672	095	a	planejamento	6.03.04.00-6 Crescimento, Flutuações e Planejamento Econômico	\N
3673	095	a	economico	6.03.04.00-6 Crescimento, Flutuações e Planejamento Econômico	\N
3674	095	a	6.03.04.01-4	6.03.04.01-4 Crescimento e Desenvolvimento Econômico	\N
3675	095	a	crescimento	6.03.04.01-4 Crescimento e Desenvolvimento Econômico	\N
3676	095	a	desenvolvimento	6.03.04.01-4 Crescimento e Desenvolvimento Econômico	\N
3677	095	a	economico	6.03.04.01-4 Crescimento e Desenvolvimento Econômico	\N
3678	095	a	6.03.04.02-2	6.03.04.02-2 Teoria e Política de Planejamento Econômico	\N
3679	095	a	teoria	6.03.04.02-2 Teoria e Política de Planejamento Econômico	\N
3680	095	a	politica	6.03.04.02-2 Teoria e Política de Planejamento Econômico	\N
3681	095	a	de	6.03.04.02-2 Teoria e Política de Planejamento Econômico	\N
3682	095	a	planejamento	6.03.04.02-2 Teoria e Política de Planejamento Econômico	\N
3683	095	a	economico	6.03.04.02-2 Teoria e Política de Planejamento Econômico	\N
3684	095	a	6.03.04.03-0	6.03.04.03-0 Flutuações Cíclicas e Projeções Econômicas	\N
3685	095	a	flutuacoes	6.03.04.03-0 Flutuações Cíclicas e Projeções Econômicas	\N
3686	095	a	ciclicas	6.03.04.03-0 Flutuações Cíclicas e Projeções Econômicas	\N
3687	095	a	projecoes	6.03.04.03-0 Flutuações Cíclicas e Projeções Econômicas	\N
3688	095	a	economicas	6.03.04.03-0 Flutuações Cíclicas e Projeções Econômicas	\N
3689	095	a	6.03.04.04-9	6.03.04.04-9 Inflação	\N
3690	095	a	inflacao	6.03.04.04-9 Inflação	\N
3691	095	a	6.03.05.00-2	6.03.05.00-2 Economia Internacional	\N
3692	095	a	economia	6.03.05.00-2 Economia Internacional	\N
3693	095	a	internacional	6.03.05.00-2 Economia Internacional	\N
3694	095	a	6.03.05.01-0	6.03.05.01-0 Teoria do Comércio Internacional	\N
3695	095	a	teoria	6.03.05.01-0 Teoria do Comércio Internacional	\N
3696	095	a	do	6.03.05.01-0 Teoria do Comércio Internacional	\N
3697	095	a	comercio	6.03.05.01-0 Teoria do Comércio Internacional	\N
3698	095	a	internacional	6.03.05.01-0 Teoria do Comércio Internacional	\N
3699	095	a	6.03.05.02-9	6.03.05.02-9 Relações do Comércio; Política Comercial; Integração Econômica	\N
3700	095	a	relacoes	6.03.05.02-9 Relações do Comércio; Política Comercial; Integração Econômica	\N
3701	095	a	do	6.03.05.02-9 Relações do Comércio; Política Comercial; Integração Econômica	\N
3702	095	a	comercio;	6.03.05.02-9 Relações do Comércio; Política Comercial; Integração Econômica	\N
3703	095	a	politica	6.03.05.02-9 Relações do Comércio; Política Comercial; Integração Econômica	\N
3704	095	a	comercial;	6.03.05.02-9 Relações do Comércio; Política Comercial; Integração Econômica	\N
3705	095	a	integracao	6.03.05.02-9 Relações do Comércio; Política Comercial; Integração Econômica	\N
3706	095	a	economica	6.03.05.02-9 Relações do Comércio; Política Comercial; Integração Econômica	\N
3707	095	a	6.03.05.03-7	6.03.05.03-7 Balanço de Pagamentos; Financas Internacionais	\N
3708	095	a	balanco	6.03.05.03-7 Balanço de Pagamentos; Financas Internacionais	\N
3709	095	a	de	6.03.05.03-7 Balanço de Pagamentos; Financas Internacionais	\N
3710	095	a	pagamentos;	6.03.05.03-7 Balanço de Pagamentos; Financas Internacionais	\N
3711	095	a	financas	6.03.05.03-7 Balanço de Pagamentos; Financas Internacionais	\N
3712	095	a	internacionais	6.03.05.03-7 Balanço de Pagamentos; Financas Internacionais	\N
3713	095	a	6.03.05.04-5	6.03.05.04-5 Investimentos Internacionais e Ajuda Externa	\N
3714	095	a	investimentos	6.03.05.04-5 Investimentos Internacionais e Ajuda Externa	\N
3715	095	a	internacionais	6.03.05.04-5 Investimentos Internacionais e Ajuda Externa	\N
3716	095	a	ajuda	6.03.05.04-5 Investimentos Internacionais e Ajuda Externa	\N
3717	095	a	externa	6.03.05.04-5 Investimentos Internacionais e Ajuda Externa	\N
3718	095	a	6.03.06.00-9	6.03.06.00-9 Economia dos Recursos Humanos	\N
3719	095	a	economia	6.03.06.00-9 Economia dos Recursos Humanos	\N
3720	095	a	dos	6.03.06.00-9 Economia dos Recursos Humanos	\N
3721	095	a	recursos	6.03.06.00-9 Economia dos Recursos Humanos	\N
3722	095	a	humanos	6.03.06.00-9 Economia dos Recursos Humanos	\N
3723	095	a	6.03.06.01-7	6.03.06.01-7 Treinamento e Alocação de Mão-de-Obra; Oferta de Mão-de-Obra e Força de Trabalho	\N
3724	095	a	treinamento	6.03.06.01-7 Treinamento e Alocação de Mão-de-Obra; Oferta de Mão-de-Obra e Força de Trabalho	\N
3725	095	a	alocacao	6.03.06.01-7 Treinamento e Alocação de Mão-de-Obra; Oferta de Mão-de-Obra e Força de Trabalho	\N
3726	095	a	de	6.03.06.01-7 Treinamento e Alocação de Mão-de-Obra; Oferta de Mão-de-Obra e Força de Trabalho	\N
3727	095	a	mao-de-obra;	6.03.06.01-7 Treinamento e Alocação de Mão-de-Obra; Oferta de Mão-de-Obra e Força de Trabalho	\N
3728	095	a	oferta	6.03.06.01-7 Treinamento e Alocação de Mão-de-Obra; Oferta de Mão-de-Obra e Força de Trabalho	\N
3729	095	a	de	6.03.06.01-7 Treinamento e Alocação de Mão-de-Obra; Oferta de Mão-de-Obra e Força de Trabalho	\N
3730	095	a	mao-de-obra	6.03.06.01-7 Treinamento e Alocação de Mão-de-Obra; Oferta de Mão-de-Obra e Força de Trabalho	\N
3731	095	a	forca	6.03.06.01-7 Treinamento e Alocação de Mão-de-Obra; Oferta de Mão-de-Obra e Força de Trabalho	\N
3732	095	a	de	6.03.06.01-7 Treinamento e Alocação de Mão-de-Obra; Oferta de Mão-de-Obra e Força de Trabalho	\N
3733	095	a	trabalho	6.03.06.01-7 Treinamento e Alocação de Mão-de-Obra; Oferta de Mão-de-Obra e Força de Trabalho	\N
3734	095	a	6.03.06.02-5	6.03.06.02-5 Mercado de Trabalho; Política do Governo	\N
3735	095	a	mercado	6.03.06.02-5 Mercado de Trabalho; Política do Governo	\N
3736	095	a	de	6.03.06.02-5 Mercado de Trabalho; Política do Governo	\N
3737	095	a	trabalho;	6.03.06.02-5 Mercado de Trabalho; Política do Governo	\N
3738	095	a	politica	6.03.06.02-5 Mercado de Trabalho; Política do Governo	\N
3739	095	a	do	6.03.06.02-5 Mercado de Trabalho; Política do Governo	\N
3740	095	a	governo	6.03.06.02-5 Mercado de Trabalho; Política do Governo	\N
3741	095	a	6.03.06.03-3	6.03.06.03-3 Sindicatos, Dissídios Coletivos, Relações de Emprego (Empregador/Empregado)	\N
3742	095	a	sindicatos,	6.03.06.03-3 Sindicatos, Dissídios Coletivos, Relações de Emprego (Empregador/Empregado)	\N
3743	095	a	dissidios	6.03.06.03-3 Sindicatos, Dissídios Coletivos, Relações de Emprego (Empregador/Empregado)	\N
3744	095	a	coletivos,	6.03.06.03-3 Sindicatos, Dissídios Coletivos, Relações de Emprego (Empregador/Empregado)	\N
3745	095	a	relacoes	6.03.06.03-3 Sindicatos, Dissídios Coletivos, Relações de Emprego (Empregador/Empregado)	\N
3746	095	a	de	6.03.06.03-3 Sindicatos, Dissídios Coletivos, Relações de Emprego (Empregador/Empregado)	\N
3747	095	a	emprego	6.03.06.03-3 Sindicatos, Dissídios Coletivos, Relações de Emprego (Empregador/Empregado)	\N
3748	095	a	(empregador/empregado)	6.03.06.03-3 Sindicatos, Dissídios Coletivos, Relações de Emprego (Empregador/Empregado)	\N
3749	095	a	6.03.06.04-1	6.03.06.04-1 Capital Humano	\N
3750	095	a	capital	6.03.06.04-1 Capital Humano	\N
3751	095	a	humano	6.03.06.04-1 Capital Humano	\N
3752	095	a	6.03.06.05-0	6.03.06.05-0 Demografia Econômica	\N
3753	095	a	demografia	6.03.06.05-0 Demografia Econômica	\N
3754	095	a	economica	6.03.06.05-0 Demografia Econômica	\N
3755	095	a	6.03.07.00-5	6.03.07.00-5 Economia Industrial	\N
3756	095	a	economia	6.03.07.00-5 Economia Industrial	\N
3757	095	a	industrial	6.03.07.00-5 Economia Industrial	\N
3758	095	a	6.03.07.01-3	6.03.07.01-3 Organização Industrial e Estudos Industriais	\N
3759	095	a	organizacao	6.03.07.01-3 Organização Industrial e Estudos Industriais	\N
3760	095	a	industrial	6.03.07.01-3 Organização Industrial e Estudos Industriais	\N
3761	095	a	estudos	6.03.07.01-3 Organização Industrial e Estudos Industriais	\N
3762	095	a	industriais	6.03.07.01-3 Organização Industrial e Estudos Industriais	\N
3763	095	a	6.03.07.02-1	6.03.07.02-1 Mudança Tecnologica	\N
3764	095	a	mudanca	6.03.07.02-1 Mudança Tecnologica	\N
3765	095	a	tecnologica	6.03.07.02-1 Mudança Tecnologica	\N
3766	095	a	6.03.08.00-1	6.03.08.00-1 Economia do Bem-Estar Social	\N
3767	095	a	economia	6.03.08.00-1 Economia do Bem-Estar Social	\N
3768	095	a	do	6.03.08.00-1 Economia do Bem-Estar Social	\N
3769	095	a	bem-estar	6.03.08.00-1 Economia do Bem-Estar Social	\N
3770	095	a	social	6.03.08.00-1 Economia do Bem-Estar Social	\N
3771	095	a	6.03.08.01-0	6.03.08.01-0 Economia dos Programas de Bem-Estar Social	\N
3772	095	a	economia	6.03.08.01-0 Economia dos Programas de Bem-Estar Social	\N
3773	095	a	dos	6.03.08.01-0 Economia dos Programas de Bem-Estar Social	\N
3774	095	a	programas	6.03.08.01-0 Economia dos Programas de Bem-Estar Social	\N
3775	095	a	de	6.03.08.01-0 Economia dos Programas de Bem-Estar Social	\N
3776	095	a	bem-estar	6.03.08.01-0 Economia dos Programas de Bem-Estar Social	\N
3777	095	a	social	6.03.08.01-0 Economia dos Programas de Bem-Estar Social	\N
3778	095	a	6.03.08.02-8	6.03.08.02-8 Economia do Consumidor	\N
3779	095	a	economia	6.03.08.02-8 Economia do Consumidor	\N
3780	095	a	do	6.03.08.02-8 Economia do Consumidor	\N
3781	095	a	consumidor	6.03.08.02-8 Economia do Consumidor	\N
3782	095	a	6.03.09.00-8	6.03.09.00-8 Economia Regional e Urbana	\N
3783	095	a	economia	6.03.09.00-8 Economia Regional e Urbana	\N
3784	095	a	regional	6.03.09.00-8 Economia Regional e Urbana	\N
3785	095	a	urbana	6.03.09.00-8 Economia Regional e Urbana	\N
3786	095	a	6.03.09.01-6	6.03.09.01-6 Economia Regional	\N
3787	095	a	economia	6.03.09.01-6 Economia Regional	\N
3788	095	a	regional	6.03.09.01-6 Economia Regional	\N
3789	095	a	6.03.09.02-4	6.03.09.02-4 Economia Urbana	\N
3790	095	a	economia	6.03.09.02-4 Economia Urbana	\N
3791	095	a	urbana	6.03.09.02-4 Economia Urbana	\N
3792	095	a	6.03.09.03-2	6.03.09.03-2 Renda e Tributação	\N
3793	095	a	renda	6.03.09.03-2 Renda e Tributação	\N
3794	095	a	tributacao	6.03.09.03-2 Renda e Tributação	\N
3795	095	a	6.03.10.00-6	6.03.10.00-6 Economias Agrária e dos Recursos Naturais	\N
3796	095	a	economias	6.03.10.00-6 Economias Agrária e dos Recursos Naturais	\N
3797	095	a	agraria	6.03.10.00-6 Economias Agrária e dos Recursos Naturais	\N
3798	095	a	dos	6.03.10.00-6 Economias Agrária e dos Recursos Naturais	\N
3799	095	a	recursos	6.03.10.00-6 Economias Agrária e dos Recursos Naturais	\N
3800	095	a	naturais	6.03.10.00-6 Economias Agrária e dos Recursos Naturais	\N
3801	095	a	6.03.10.01-4	6.03.10.01-4 Economia Agrária	\N
3802	095	a	economia	6.03.10.01-4 Economia Agrária	\N
3803	095	a	agraria	6.03.10.01-4 Economia Agrária	\N
3804	095	a	6.03.10.02-2	6.03.10.02-2 Economia dos Recursos Naturais	\N
3805	095	a	economia	6.03.10.02-2 Economia dos Recursos Naturais	\N
3806	095	a	dos	6.03.10.02-2 Economia dos Recursos Naturais	\N
3807	095	a	recursos	6.03.10.02-2 Economia dos Recursos Naturais	\N
3808	095	a	naturais	6.03.10.02-2 Economia dos Recursos Naturais	\N
3809	095	a	6.04.00.00-5	6.04.00.00-5 Arquitetura e Urbanismo	\N
3810	095	a	arquitetura	6.04.00.00-5 Arquitetura e Urbanismo	\N
3811	095	a	urbanismo	6.04.00.00-5 Arquitetura e Urbanismo	\N
3812	095	a	6.04.01.00-1	6.04.01.00-1 Fundamentos de Arquitetura e Urbanismo	\N
3813	095	a	fundamentos	6.04.01.00-1 Fundamentos de Arquitetura e Urbanismo	\N
3814	095	a	de	6.04.01.00-1 Fundamentos de Arquitetura e Urbanismo	\N
3815	095	a	arquitetura	6.04.01.00-1 Fundamentos de Arquitetura e Urbanismo	\N
3816	095	a	urbanismo	6.04.01.00-1 Fundamentos de Arquitetura e Urbanismo	\N
3817	095	a	6.04.01.01-0	6.04.01.01-0 História da Arquitetura e Urbanismo	\N
3818	095	a	historia	6.04.01.01-0 História da Arquitetura e Urbanismo	\N
3819	095	a	da	6.04.01.01-0 História da Arquitetura e Urbanismo	\N
3820	095	a	arquitetura	6.04.01.01-0 História da Arquitetura e Urbanismo	\N
3821	095	a	urbanismo	6.04.01.01-0 História da Arquitetura e Urbanismo	\N
3822	095	a	6.04.01.02-8	6.04.01.02-8 Teoria da Arquitetura	\N
3823	095	a	teoria	6.04.01.02-8 Teoria da Arquitetura	\N
3824	095	a	da	6.04.01.02-8 Teoria da Arquitetura	\N
3825	095	a	arquitetura	6.04.01.02-8 Teoria da Arquitetura	\N
3826	095	a	6.04.01.03-6	6.04.01.03-6 História do Urbanismo	\N
3827	095	a	historia	6.04.01.03-6 História do Urbanismo	\N
3828	095	a	do	6.04.01.03-6 História do Urbanismo	\N
3829	095	a	urbanismo	6.04.01.03-6 História do Urbanismo	\N
3830	095	a	6.04.01.04-4	6.04.01.04-4 Teoria do Urbanismo	\N
3831	095	a	teoria	6.04.01.04-4 Teoria do Urbanismo	\N
3832	095	a	do	6.04.01.04-4 Teoria do Urbanismo	\N
3833	095	a	urbanismo	6.04.01.04-4 Teoria do Urbanismo	\N
3834	095	a	6.04.02.00-8	6.04.02.00-8 Projeto de Arquitetuta e Urbanismo	\N
3835	095	a	projeto	6.04.02.00-8 Projeto de Arquitetuta e Urbanismo	\N
3836	095	a	de	6.04.02.00-8 Projeto de Arquitetuta e Urbanismo	\N
3837	095	a	arquitetuta	6.04.02.00-8 Projeto de Arquitetuta e Urbanismo	\N
3838	095	a	urbanismo	6.04.02.00-8 Projeto de Arquitetuta e Urbanismo	\N
3839	095	a	6.04.02.01-6	6.04.02.01-6 Planejamento e Projetos da Edificação	\N
3840	095	a	planejamento	6.04.02.01-6 Planejamento e Projetos da Edificação	\N
3841	095	a	projetos	6.04.02.01-6 Planejamento e Projetos da Edificação	\N
3842	095	a	da	6.04.02.01-6 Planejamento e Projetos da Edificação	\N
3843	095	a	edificacao	6.04.02.01-6 Planejamento e Projetos da Edificação	\N
3844	095	a	6.04.02.02-4	6.04.02.02-4 Planejamento e Projeto do Espaço Urbano	\N
3845	095	a	planejamento	6.04.02.02-4 Planejamento e Projeto do Espaço Urbano	\N
3846	095	a	projeto	6.04.02.02-4 Planejamento e Projeto do Espaço Urbano	\N
3847	095	a	do	6.04.02.02-4 Planejamento e Projeto do Espaço Urbano	\N
3848	095	a	espaco	6.04.02.02-4 Planejamento e Projeto do Espaço Urbano	\N
3849	095	a	urbano	6.04.02.02-4 Planejamento e Projeto do Espaço Urbano	\N
3850	095	a	6.04.02.03-2	6.04.02.03-2 Planejamento e Projeto do Equipamento	\N
3851	095	a	planejamento	6.04.02.03-2 Planejamento e Projeto do Equipamento	\N
3852	095	a	projeto	6.04.02.03-2 Planejamento e Projeto do Equipamento	\N
3853	095	a	do	6.04.02.03-2 Planejamento e Projeto do Equipamento	\N
3854	095	a	equipamento	6.04.02.03-2 Planejamento e Projeto do Equipamento	\N
3855	095	a	6.04.03.00-4	6.04.03.00-4 Tecnologia de Arquitetura e Urbanismo	\N
3856	095	a	tecnologia	6.04.03.00-4 Tecnologia de Arquitetura e Urbanismo	\N
3857	095	a	de	6.04.03.00-4 Tecnologia de Arquitetura e Urbanismo	\N
3858	095	a	arquitetura	6.04.03.00-4 Tecnologia de Arquitetura e Urbanismo	\N
3859	095	a	urbanismo	6.04.03.00-4 Tecnologia de Arquitetura e Urbanismo	\N
3860	095	a	6.04.03.01-2	6.04.03.01-2 Adequação Ambiental	\N
3861	095	a	adequacao	6.04.03.01-2 Adequação Ambiental	\N
3862	095	a	ambiental	6.04.03.01-2 Adequação Ambiental	\N
3863	095	a	6.04.04.00-0	6.04.04.00-0 Paisagismo	\N
3864	095	a	paisagismo	6.04.04.00-0 Paisagismo	\N
3865	095	a	6.04.04.01-9	6.04.04.01-9 Desenvolvimento Histórico do Paisagismo	\N
3866	095	a	desenvolvimento	6.04.04.01-9 Desenvolvimento Histórico do Paisagismo	\N
3867	095	a	historico	6.04.04.01-9 Desenvolvimento Histórico do Paisagismo	\N
3868	095	a	do	6.04.04.01-9 Desenvolvimento Histórico do Paisagismo	\N
3869	095	a	paisagismo	6.04.04.01-9 Desenvolvimento Histórico do Paisagismo	\N
3870	095	a	6.04.04.02-7	6.04.04.02-7 Conceituação de Paisagismo e Metodologia do Paisagismo	\N
3871	095	a	conceituacao	6.04.04.02-7 Conceituação de Paisagismo e Metodologia do Paisagismo	\N
3872	095	a	de	6.04.04.02-7 Conceituação de Paisagismo e Metodologia do Paisagismo	\N
3873	095	a	paisagismo	6.04.04.02-7 Conceituação de Paisagismo e Metodologia do Paisagismo	\N
3874	095	a	metodologia	6.04.04.02-7 Conceituação de Paisagismo e Metodologia do Paisagismo	\N
3875	095	a	do	6.04.04.02-7 Conceituação de Paisagismo e Metodologia do Paisagismo	\N
3876	095	a	paisagismo	6.04.04.02-7 Conceituação de Paisagismo e Metodologia do Paisagismo	\N
3877	095	a	6.04.04.03-5	6.04.04.03-5 Estudos de Organização do Espaço Exterior	\N
3878	095	a	estudos	6.04.04.03-5 Estudos de Organização do Espaço Exterior	\N
3879	095	a	de	6.04.04.03-5 Estudos de Organização do Espaço Exterior	\N
3880	095	a	organizacao	6.04.04.03-5 Estudos de Organização do Espaço Exterior	\N
3881	095	a	do	6.04.04.03-5 Estudos de Organização do Espaço Exterior	\N
3882	095	a	espaco	6.04.04.03-5 Estudos de Organização do Espaço Exterior	\N
3883	095	a	exterior	6.04.04.03-5 Estudos de Organização do Espaço Exterior	\N
3884	095	a	6.04.04.04-3	6.04.04.04-3 Projetos de Espaços Livres Urbanos	\N
3885	095	a	projetos	6.04.04.04-3 Projetos de Espaços Livres Urbanos	\N
3886	095	a	de	6.04.04.04-3 Projetos de Espaços Livres Urbanos	\N
3887	095	a	espacos	6.04.04.04-3 Projetos de Espaços Livres Urbanos	\N
3888	095	a	livres	6.04.04.04-3 Projetos de Espaços Livres Urbanos	\N
3889	095	a	urbanos	6.04.04.04-3 Projetos de Espaços Livres Urbanos	\N
3890	095	a	6.05.00.00-0	6.05.00.00-0 Planejamento Urbano e Regional	\N
3891	095	a	planejamento	6.05.00.00-0 Planejamento Urbano e Regional	\N
3892	095	a	urbano	6.05.00.00-0 Planejamento Urbano e Regional	\N
3893	095	a	regional	6.05.00.00-0 Planejamento Urbano e Regional	\N
3894	095	a	6.05.01.00-6	6.05.01.00-6 Fundamentos do Planejamento Urbano e Regional	\N
3895	095	a	fundamentos	6.05.01.00-6 Fundamentos do Planejamento Urbano e Regional	\N
3896	095	a	do	6.05.01.00-6 Fundamentos do Planejamento Urbano e Regional	\N
3897	095	a	planejamento	6.05.01.00-6 Fundamentos do Planejamento Urbano e Regional	\N
3898	095	a	urbano	6.05.01.00-6 Fundamentos do Planejamento Urbano e Regional	\N
3899	095	a	regional	6.05.01.00-6 Fundamentos do Planejamento Urbano e Regional	\N
3900	095	a	6.05.01.01-4	6.05.01.01-4 Teoria do Planejamento Urbano e Regional	\N
3901	095	a	teoria	6.05.01.01-4 Teoria do Planejamento Urbano e Regional	\N
3902	095	a	do	6.05.01.01-4 Teoria do Planejamento Urbano e Regional	\N
3903	095	a	planejamento	6.05.01.01-4 Teoria do Planejamento Urbano e Regional	\N
3904	095	a	urbano	6.05.01.01-4 Teoria do Planejamento Urbano e Regional	\N
3905	095	a	regional	6.05.01.01-4 Teoria do Planejamento Urbano e Regional	\N
3906	095	a	6.05.01.02-2	6.05.01.02-2 Teoria da Urbanização	\N
3907	095	a	teoria	6.05.01.02-2 Teoria da Urbanização	\N
3908	095	a	da	6.05.01.02-2 Teoria da Urbanização	\N
3909	095	a	urbanizacao	6.05.01.02-2 Teoria da Urbanização	\N
3910	095	a	6.05.01.03-0	6.05.01.03-0 Política Urbana	\N
3911	095	a	politica	6.05.01.03-0 Política Urbana	\N
3912	095	a	urbana	6.05.01.03-0 Política Urbana	\N
3913	095	a	6.05.01.04-9	6.05.01.04-9 História Urbana	\N
3914	095	a	historia	6.05.01.04-9 História Urbana	\N
3915	095	a	urbana	6.05.01.04-9 História Urbana	\N
3916	095	a	6.05.02.00-2	6.05.02.00-2 Métodos e Técnicas do Planejamento Urbano e Regional	\N
3917	095	a	metodos	6.05.02.00-2 Métodos e Técnicas do Planejamento Urbano e Regional	\N
3918	095	a	tecnicas	6.05.02.00-2 Métodos e Técnicas do Planejamento Urbano e Regional	\N
3919	095	a	do	6.05.02.00-2 Métodos e Técnicas do Planejamento Urbano e Regional	\N
3920	095	a	planejamento	6.05.02.00-2 Métodos e Técnicas do Planejamento Urbano e Regional	\N
3921	095	a	urbano	6.05.02.00-2 Métodos e Técnicas do Planejamento Urbano e Regional	\N
3922	095	a	regional	6.05.02.00-2 Métodos e Técnicas do Planejamento Urbano e Regional	\N
3923	095	a	6.05.02.01-0	6.05.02.01-0 Informação, Cadastro e Mapeamento	\N
3924	095	a	informacao,	6.05.02.01-0 Informação, Cadastro e Mapeamento	\N
3925	095	a	cadastro	6.05.02.01-0 Informação, Cadastro e Mapeamento	\N
3926	095	a	mapeamento	6.05.02.01-0 Informação, Cadastro e Mapeamento	\N
3927	095	a	6.05.02.02-9	6.05.02.02-9 Técnica de Previsão Urbana e Regional	\N
3928	095	a	tecnica	6.05.02.02-9 Técnica de Previsão Urbana e Regional	\N
3929	095	a	de	6.05.02.02-9 Técnica de Previsão Urbana e Regional	\N
3930	095	a	previsao	6.05.02.02-9 Técnica de Previsão Urbana e Regional	\N
3931	095	a	urbana	6.05.02.02-9 Técnica de Previsão Urbana e Regional	\N
3932	095	a	regional	6.05.02.02-9 Técnica de Previsão Urbana e Regional	\N
3933	095	a	6.05.02.03-7	6.05.02.03-7 Técnicas de Análise e Avaliação Urbana e Regional	\N
3934	095	a	tecnicas	6.05.02.03-7 Técnicas de Análise e Avaliação Urbana e Regional	\N
3935	095	a	de	6.05.02.03-7 Técnicas de Análise e Avaliação Urbana e Regional	\N
3936	095	a	analise	6.05.02.03-7 Técnicas de Análise e Avaliação Urbana e Regional	\N
3937	095	a	avaliacao	6.05.02.03-7 Técnicas de Análise e Avaliação Urbana e Regional	\N
3938	095	a	urbana	6.05.02.03-7 Técnicas de Análise e Avaliação Urbana e Regional	\N
3939	095	a	regional	6.05.02.03-7 Técnicas de Análise e Avaliação Urbana e Regional	\N
3940	095	a	6.05.02.04-5	6.05.02.04-5 Técnicas de Planejamento e Projeto Urbanos e Regionais	\N
3941	095	a	tecnicas	6.05.02.04-5 Técnicas de Planejamento e Projeto Urbanos e Regionais	\N
3942	095	a	de	6.05.02.04-5 Técnicas de Planejamento e Projeto Urbanos e Regionais	\N
3943	095	a	planejamento	6.05.02.04-5 Técnicas de Planejamento e Projeto Urbanos e Regionais	\N
3944	095	a	projeto	6.05.02.04-5 Técnicas de Planejamento e Projeto Urbanos e Regionais	\N
3945	095	a	urbanos	6.05.02.04-5 Técnicas de Planejamento e Projeto Urbanos e Regionais	\N
3946	095	a	regionais	6.05.02.04-5 Técnicas de Planejamento e Projeto Urbanos e Regionais	\N
3947	095	a	6.05.03.00-9	6.05.03.00-9 Serviços Urbanos e Regionais	\N
3948	095	a	servicos	6.05.03.00-9 Serviços Urbanos e Regionais	\N
3949	095	a	urbanos	6.05.03.00-9 Serviços Urbanos e Regionais	\N
3950	095	a	regionais	6.05.03.00-9 Serviços Urbanos e Regionais	\N
3951	095	a	6.05.03.01-7	6.05.03.01-7 Administração Municipal e Urbana	\N
3952	095	a	administracao	6.05.03.01-7 Administração Municipal e Urbana	\N
3953	095	a	municipal	6.05.03.01-7 Administração Municipal e Urbana	\N
3954	095	a	urbana	6.05.03.01-7 Administração Municipal e Urbana	\N
3955	095	a	6.05.03.02-5	6.05.03.02-5 Estudos da Habitação	\N
3956	095	a	estudos	6.05.03.02-5 Estudos da Habitação	\N
3957	095	a	da	6.05.03.02-5 Estudos da Habitação	\N
3958	095	a	habitacao	6.05.03.02-5 Estudos da Habitação	\N
3959	095	a	6.05.03.03-3	6.05.03.03-3 Aspectos Sociais do Planejamento Urbano e Regional	\N
3960	095	a	aspectos	6.05.03.03-3 Aspectos Sociais do Planejamento Urbano e Regional	\N
3961	095	a	sociais	6.05.03.03-3 Aspectos Sociais do Planejamento Urbano e Regional	\N
3962	095	a	do	6.05.03.03-3 Aspectos Sociais do Planejamento Urbano e Regional	\N
3963	095	a	planejamento	6.05.03.03-3 Aspectos Sociais do Planejamento Urbano e Regional	\N
3964	095	a	urbano	6.05.03.03-3 Aspectos Sociais do Planejamento Urbano e Regional	\N
3965	095	a	regional	6.05.03.03-3 Aspectos Sociais do Planejamento Urbano e Regional	\N
3966	095	a	6.05.03.04-1	6.05.03.04-1 Aspectos Econômicos do Planejamento Urbano e Regional	\N
3967	095	a	aspectos	6.05.03.04-1 Aspectos Econômicos do Planejamento Urbano e Regional	\N
3968	095	a	economicos	6.05.03.04-1 Aspectos Econômicos do Planejamento Urbano e Regional	\N
3969	095	a	do	6.05.03.04-1 Aspectos Econômicos do Planejamento Urbano e Regional	\N
3970	095	a	planejamento	6.05.03.04-1 Aspectos Econômicos do Planejamento Urbano e Regional	\N
3971	095	a	urbano	6.05.03.04-1 Aspectos Econômicos do Planejamento Urbano e Regional	\N
3972	095	a	regional	6.05.03.04-1 Aspectos Econômicos do Planejamento Urbano e Regional	\N
3973	095	a	6.05.03.05-0	6.05.03.05-0 Aspectos Físico-Ambientais do Planejamento Urbano e Regional	\N
3974	095	a	aspectos	6.05.03.05-0 Aspectos Físico-Ambientais do Planejamento Urbano e Regional	\N
3975	095	a	fisico-ambientais	6.05.03.05-0 Aspectos Físico-Ambientais do Planejamento Urbano e Regional	\N
3976	095	a	do	6.05.03.05-0 Aspectos Físico-Ambientais do Planejamento Urbano e Regional	\N
3977	095	a	planejamento	6.05.03.05-0 Aspectos Físico-Ambientais do Planejamento Urbano e Regional	\N
3978	095	a	urbano	6.05.03.05-0 Aspectos Físico-Ambientais do Planejamento Urbano e Regional	\N
3979	095	a	regional	6.05.03.05-0 Aspectos Físico-Ambientais do Planejamento Urbano e Regional	\N
3980	095	a	6.05.03.06-8	6.05.03.06-8 Serviços Comunitários	\N
3981	095	a	servicos	6.05.03.06-8 Serviços Comunitários	\N
3982	095	a	comunitarios	6.05.03.06-8 Serviços Comunitários	\N
3983	095	a	6.05.03.07-6	6.05.03.07-6 Infra-Estruturas Urbanas e Regionais	\N
3984	095	a	infra-estruturas	6.05.03.07-6 Infra-Estruturas Urbanas e Regionais	\N
3985	095	a	urbanas	6.05.03.07-6 Infra-Estruturas Urbanas e Regionais	\N
3986	095	a	regionais	6.05.03.07-6 Infra-Estruturas Urbanas e Regionais	\N
3987	095	a	6.05.03.08-4	6.05.03.08-4 Transporte e Tráfego Urbano e Regional	\N
3988	095	a	transporte	6.05.03.08-4 Transporte e Tráfego Urbano e Regional	\N
3989	095	a	trafego	6.05.03.08-4 Transporte e Tráfego Urbano e Regional	\N
3990	095	a	urbano	6.05.03.08-4 Transporte e Tráfego Urbano e Regional	\N
3991	095	a	regional	6.05.03.08-4 Transporte e Tráfego Urbano e Regional	\N
3992	095	a	6.05.03.09-2	6.05.03.09-2 Legislação Urbana e Regional	\N
3993	095	a	legislacao	6.05.03.09-2 Legislação Urbana e Regional	\N
3994	095	a	urbana	6.05.03.09-2 Legislação Urbana e Regional	\N
3995	095	a	regional	6.05.03.09-2 Legislação Urbana e Regional	\N
3996	095	a	6.06.00.00-4	6.06.00.00-4 Demografia	\N
3997	095	a	demografia	6.06.00.00-4 Demografia	\N
3998	095	a	6.06.01.00-0	6.06.01.00-0 Distribuição Espacial	\N
3999	095	a	distribuicao	6.06.01.00-0 Distribuição Espacial	\N
4000	095	a	espacial	6.06.01.00-0 Distribuição Espacial	\N
4001	095	a	6.06.01.01-9	6.06.01.01-9 Distribuição Espacial Geral	\N
4002	095	a	distribuicao	6.06.01.01-9 Distribuição Espacial Geral	\N
4003	095	a	espacial	6.06.01.01-9 Distribuição Espacial Geral	\N
4004	095	a	geral	6.06.01.01-9 Distribuição Espacial Geral	\N
4005	095	a	6.06.01.02-7	6.06.01.02-7 Distribuição Espacial Urbana	\N
4006	095	a	distribuicao	6.06.01.02-7 Distribuição Espacial Urbana	\N
4007	095	a	espacial	6.06.01.02-7 Distribuição Espacial Urbana	\N
4008	095	a	urbana	6.06.01.02-7 Distribuição Espacial Urbana	\N
4009	095	a	6.06.01.03-5	6.06.01.03-5 Distribuição Espacial Rural	\N
4010	095	a	distribuicao	6.06.01.03-5 Distribuição Espacial Rural	\N
4011	095	a	espacial	6.06.01.03-5 Distribuição Espacial Rural	\N
4012	095	a	rural	6.06.01.03-5 Distribuição Espacial Rural	\N
4013	095	a	6.06.02.00-7	6.06.02.00-7 Tendência Populacional	\N
4014	095	a	tendencia	6.06.02.00-7 Tendência Populacional	\N
4015	095	a	populacional	6.06.02.00-7 Tendência Populacional	\N
4016	095	a	6.06.02.01-5	6.06.02.01-5 Tendências Passadas	\N
4017	095	a	tendencias	6.06.02.01-5 Tendências Passadas	\N
4018	095	a	passadas	6.06.02.01-5 Tendências Passadas	\N
4019	095	a	6.06.02.02-3	6.06.02.02-3 Taxas e Estimativas Correntes	\N
4020	095	a	taxas	6.06.02.02-3 Taxas e Estimativas Correntes	\N
4021	095	a	estimativas	6.06.02.02-3 Taxas e Estimativas Correntes	\N
4022	095	a	correntes	6.06.02.02-3 Taxas e Estimativas Correntes	\N
4023	095	a	6.06.02.03-1	6.06.02.03-1 Projeções	\N
4024	095	a	projecoes	6.06.02.03-1 Projeções	\N
4025	095	a	6.06.03.00-3	6.06.03.00-3 Componentes da Dinâmica Demográfica	\N
4026	095	a	componentes	6.06.03.00-3 Componentes da Dinâmica Demográfica	\N
4027	095	a	da	6.06.03.00-3 Componentes da Dinâmica Demográfica	\N
4028	095	a	dinamica	6.06.03.00-3 Componentes da Dinâmica Demográfica	\N
4029	095	a	demografica	6.06.03.00-3 Componentes da Dinâmica Demográfica	\N
4030	095	a	6.06.03.01-1	6.06.03.01-1 Fecundidade	\N
4031	095	a	fecundidade	6.06.03.01-1 Fecundidade	\N
4032	095	a	6.06.03.02-0	6.06.03.02-0 Mortalidade	\N
4033	095	a	mortalidade	6.06.03.02-0 Mortalidade	\N
4034	095	a	6.06.03.03-8	6.06.03.03-8 Migração	\N
4035	095	a	migracao	6.06.03.03-8 Migração	\N
4036	095	a	6.06.04.00-0	6.06.04.00-0 Nupcialidade e Família	\N
4037	095	a	nupcialidade	6.06.04.00-0 Nupcialidade e Família	\N
4038	095	a	familia	6.06.04.00-0 Nupcialidade e Família	\N
4039	095	a	6.06.04.01-8	6.06.04.01-8 Casamento e Divórcio	\N
4040	095	a	casamento	6.06.04.01-8 Casamento e Divórcio	\N
4041	095	a	divorcio	6.06.04.01-8 Casamento e Divórcio	\N
4042	095	a	6.06.04.02-6	6.06.04.02-6 Família e Reprodução	\N
4043	095	a	familia	6.06.04.02-6 Família e Reprodução	\N
4044	095	a	reproducao	6.06.04.02-6 Família e Reprodução	\N
4045	095	a	6.06.05.00-6	6.06.05.00-6 Demografia Histórica	\N
4046	095	a	demografia	6.06.05.00-6 Demografia Histórica	\N
4047	095	a	historica	6.06.05.00-6 Demografia Histórica	\N
4048	095	a	6.06.05.01-4	6.06.05.01-4 Distribuição Espacial	\N
4049	095	a	distribuicao	6.06.05.01-4 Distribuição Espacial	\N
4050	095	a	espacial	6.06.05.01-4 Distribuição Espacial	\N
4051	095	a	6.06.05.02-2	6.06.05.02-2 Natalidade, Mortalidade, Migração	\N
4052	095	a	natalidade,	6.06.05.02-2 Natalidade, Mortalidade, Migração	\N
4053	095	a	mortalidade,	6.06.05.02-2 Natalidade, Mortalidade, Migração	\N
4054	095	a	migracao	6.06.05.02-2 Natalidade, Mortalidade, Migração	\N
4055	095	a	6.06.05.03-0	6.06.05.03-0 Nupcialidade e Família	\N
4056	095	a	nupcialidade	6.06.05.03-0 Nupcialidade e Família	\N
4057	095	a	familia	6.06.05.03-0 Nupcialidade e Família	\N
4058	095	a	6.06.05.04-9	6.06.05.04-9 Métodos e Técnicas de Demografia Histórica	\N
4059	095	a	metodos	6.06.05.04-9 Métodos e Técnicas de Demografia Histórica	\N
4060	095	a	tecnicas	6.06.05.04-9 Métodos e Técnicas de Demografia Histórica	\N
4061	095	a	de	6.06.05.04-9 Métodos e Técnicas de Demografia Histórica	\N
4062	095	a	demografia	6.06.05.04-9 Métodos e Técnicas de Demografia Histórica	\N
4063	095	a	historica	6.06.05.04-9 Métodos e Técnicas de Demografia Histórica	\N
4064	095	a	6.06.06.00-2	6.06.06.00-2 Política Pública e População	\N
4065	095	a	politica	6.06.06.00-2 Política Pública e População	\N
4066	095	a	publica	6.06.06.00-2 Política Pública e População	\N
4067	095	a	populacao	6.06.06.00-2 Política Pública e População	\N
4068	095	a	6.06.06.01-0	6.06.06.01-0 Política Populacional	\N
4069	095	a	politica	6.06.06.01-0 Política Populacional	\N
4070	095	a	populacional	6.06.06.01-0 Política Populacional	\N
4071	095	a	6.06.06.02-9	6.06.06.02-9 Políticas de Redistribuição de População	\N
4072	095	a	politicas	6.06.06.02-9 Políticas de Redistribuição de População	\N
4073	095	a	de	6.06.06.02-9 Políticas de Redistribuição de População	\N
4074	095	a	redistribuicao	6.06.06.02-9 Políticas de Redistribuição de População	\N
4075	095	a	de	6.06.06.02-9 Políticas de Redistribuição de População	\N
4076	095	a	populacao	6.06.06.02-9 Políticas de Redistribuição de População	\N
4077	095	a	6.06.06.03-7	6.06.06.03-7 Políticas de Planejamento Familiar	\N
4078	095	a	politicas	6.06.06.03-7 Políticas de Planejamento Familiar	\N
4079	095	a	de	6.06.06.03-7 Políticas de Planejamento Familiar	\N
4080	095	a	planejamento	6.06.06.03-7 Políticas de Planejamento Familiar	\N
4081	095	a	familiar	6.06.06.03-7 Políticas de Planejamento Familiar	\N
4082	095	a	6.06.07.00-9	6.06.07.00-9 Fontes de Dados Demográficos	\N
4083	095	a	fontes	6.06.07.00-9 Fontes de Dados Demográficos	\N
4084	095	a	de	6.06.07.00-9 Fontes de Dados Demográficos	\N
4085	095	a	dados	6.06.07.00-9 Fontes de Dados Demográficos	\N
4086	095	a	demograficos	6.06.07.00-9 Fontes de Dados Demográficos	\N
4087	095	a	6.07.00.00-9	6.07.00.00-9 Ciência da Informação	\N
4088	095	a	ciencia	6.07.00.00-9 Ciência da Informação	\N
4089	095	a	da	6.07.00.00-9 Ciência da Informação	\N
4090	095	a	informacao	6.07.00.00-9 Ciência da Informação	\N
4091	095	a	6.07.01.00-5	6.07.01.00-5 Teoria da Informação	\N
4092	095	a	teoria	6.07.01.00-5 Teoria da Informação	\N
4093	095	a	da	6.07.01.00-5 Teoria da Informação	\N
4094	095	a	informacao	6.07.01.00-5 Teoria da Informação	\N
4095	095	a	6.07.01.01-3	6.07.01.01-3 Teoria Geral da Informação	\N
4096	095	a	teoria	6.07.01.01-3 Teoria Geral da Informação	\N
4097	095	a	geral	6.07.01.01-3 Teoria Geral da Informação	\N
4098	095	a	da	6.07.01.01-3 Teoria Geral da Informação	\N
4099	095	a	informacao	6.07.01.01-3 Teoria Geral da Informação	\N
4100	095	a	6.07.01.02-1	6.07.01.02-1 Processos da Comunicação	\N
4101	095	a	processos	6.07.01.02-1 Processos da Comunicação	\N
4102	095	a	da	6.07.01.02-1 Processos da Comunicação	\N
4103	095	a	comunicacao	6.07.01.02-1 Processos da Comunicação	\N
4104	095	a	6.07.01.03-0	6.07.01.03-0 Representação da Informação	\N
4105	095	a	representacao	6.07.01.03-0 Representação da Informação	\N
4106	095	a	da	6.07.01.03-0 Representação da Informação	\N
4107	095	a	informacao	6.07.01.03-0 Representação da Informação	\N
4108	095	a	6.07.02.00-1	6.07.02.00-1 Biblioteconomia	\N
4109	095	a	biblioteconomia	6.07.02.00-1 Biblioteconomia	\N
4110	095	a	6.07.02.01-0	6.07.02.01-0 Teoria da Classificação	\N
4111	095	a	teoria	6.07.02.01-0 Teoria da Classificação	\N
4112	095	a	da	6.07.02.01-0 Teoria da Classificação	\N
4113	095	a	classificacao	6.07.02.01-0 Teoria da Classificação	\N
4114	095	a	6.07.02.02-8	6.07.02.02-8 Métodos Quantitativos. Bibliometria	\N
4115	095	a	metodos	6.07.02.02-8 Métodos Quantitativos. Bibliometria	\N
4116	095	a	quantitativos.	6.07.02.02-8 Métodos Quantitativos. Bibliometria	\N
4117	095	a	bibliometria	6.07.02.02-8 Métodos Quantitativos. Bibliometria	\N
4118	095	a	6.07.02.03-6	6.07.02.03-6 Técnicas de Recuperação de Informação	\N
4119	095	a	tecnicas	6.07.02.03-6 Técnicas de Recuperação de Informação	\N
4120	095	a	de	6.07.02.03-6 Técnicas de Recuperação de Informação	\N
4121	095	a	recuperacao	6.07.02.03-6 Técnicas de Recuperação de Informação	\N
4122	095	a	de	6.07.02.03-6 Técnicas de Recuperação de Informação	\N
4123	095	a	informacao	6.07.02.03-6 Técnicas de Recuperação de Informação	\N
4124	095	a	6.07.02.04-4	6.07.02.04-4 Processos de Disseminação da Informação	\N
4125	095	a	processos	6.07.02.04-4 Processos de Disseminação da Informação	\N
4126	095	a	de	6.07.02.04-4 Processos de Disseminação da Informação	\N
4127	095	a	disseminacao	6.07.02.04-4 Processos de Disseminação da Informação	\N
4128	095	a	da	6.07.02.04-4 Processos de Disseminação da Informação	\N
4129	095	a	informacao	6.07.02.04-4 Processos de Disseminação da Informação	\N
4130	095	a	6.07.03.00-8	6.07.03.00-8 Arquivologia	\N
4131	095	a	arquivologia	6.07.03.00-8 Arquivologia	\N
4132	095	a	6.07.03.01-6	6.07.03.01-6 Organização de Arquivos	\N
4133	095	a	organizacao	6.07.03.01-6 Organização de Arquivos	\N
4134	095	a	de	6.07.03.01-6 Organização de Arquivos	\N
4135	095	a	arquivos	6.07.03.01-6 Organização de Arquivos	\N
4136	095	a	6.08.00.00-3	6.08.00.00-3 Museologia	\N
4137	095	a	museologia	6.08.00.00-3 Museologia	\N
4138	095	a	6.09.00.00-8	6.09.00.00-8 Comunicação	\N
4139	095	a	comunicacao	6.09.00.00-8 Comunicação	\N
4140	095	a	6.09.01.00-4	6.09.01.00-4 Teoria da Comunicação	\N
4141	095	a	teoria	6.09.01.00-4 Teoria da Comunicação	\N
4142	095	a	da	6.09.01.00-4 Teoria da Comunicação	\N
4143	095	a	comunicacao	6.09.01.00-4 Teoria da Comunicação	\N
4144	095	a	6.09.02.00-0	6.09.02.00-0 Jornalismo e Editoração	\N
4145	095	a	jornalismo	6.09.02.00-0 Jornalismo e Editoração	\N
4146	095	a	editoracao	6.09.02.00-0 Jornalismo e Editoração	\N
4147	095	a	6.09.02.01-9	6.09.02.01-9 Teoria e Ética do Jornalismo	\N
4148	095	a	teoria	6.09.02.01-9 Teoria e Ética do Jornalismo	\N
4149	095	a	etica	6.09.02.01-9 Teoria e Ética do Jornalismo	\N
4150	095	a	do	6.09.02.01-9 Teoria e Ética do Jornalismo	\N
4151	095	a	jornalismo	6.09.02.01-9 Teoria e Ética do Jornalismo	\N
4152	095	a	6.09.02.02-7	6.09.02.02-7 Organização Editorial de Jornais	\N
4153	095	a	organizacao	6.09.02.02-7 Organização Editorial de Jornais	\N
4154	095	a	editorial	6.09.02.02-7 Organização Editorial de Jornais	\N
4155	095	a	de	6.09.02.02-7 Organização Editorial de Jornais	\N
4156	095	a	jornais	6.09.02.02-7 Organização Editorial de Jornais	\N
4157	095	a	6.09.02.03-5	6.09.02.03-5 Organização Comercial de Jornais	\N
4158	095	a	organizacao	6.09.02.03-5 Organização Comercial de Jornais	\N
4159	095	a	comercial	6.09.02.03-5 Organização Comercial de Jornais	\N
4160	095	a	de	6.09.02.03-5 Organização Comercial de Jornais	\N
4161	095	a	jornais	6.09.02.03-5 Organização Comercial de Jornais	\N
4162	095	a	6.09.02.04-3	6.09.02.04-3 Jornalismo Especializado (Comunitário, Rural, Empresarial, Científico)	\N
4163	095	a	jornalismo	6.09.02.04-3 Jornalismo Especializado (Comunitário, Rural, Empresarial, Científico)	\N
4164	095	a	especializado	6.09.02.04-3 Jornalismo Especializado (Comunitário, Rural, Empresarial, Científico)	\N
4165	095	a	(comunitario,	6.09.02.04-3 Jornalismo Especializado (Comunitário, Rural, Empresarial, Científico)	\N
4166	095	a	rural,	6.09.02.04-3 Jornalismo Especializado (Comunitário, Rural, Empresarial, Científico)	\N
4167	095	a	empresarial,	6.09.02.04-3 Jornalismo Especializado (Comunitário, Rural, Empresarial, Científico)	\N
4168	095	a	cientifico)	6.09.02.04-3 Jornalismo Especializado (Comunitário, Rural, Empresarial, Científico)	\N
4169	095	a	6.09.03.00-7	6.09.03.00-7 Rádio e Televisão	\N
4170	095	a	radio	6.09.03.00-7 Rádio e Televisão	\N
4171	095	a	televisao	6.09.03.00-7 Rádio e Televisão	\N
4172	095	a	6.09.03.01-5	6.09.03.01-5 Radiodifusão	\N
4173	095	a	radiodifusao	6.09.03.01-5 Radiodifusão	\N
4174	095	a	6.09.03.02-3	6.09.03.02-3 Videodifusão	\N
4175	095	a	videodifusao	6.09.03.02-3 Videodifusão	\N
4176	095	a	6.09.04.00-3	6.09.04.00-3 Relações Públicas e Propaganda	\N
4177	095	a	relacoes	6.09.04.00-3 Relações Públicas e Propaganda	\N
4178	095	a	publicas	6.09.04.00-3 Relações Públicas e Propaganda	\N
4179	095	a	propaganda	6.09.04.00-3 Relações Públicas e Propaganda	\N
4180	095	a	6.09.05.00-0	6.09.05.00-0 Comunicação Visual	\N
4181	095	a	comunicacao	6.09.05.00-0 Comunicação Visual	\N
4182	095	a	visual	6.09.05.00-0 Comunicação Visual	\N
4183	095	a	6.10.00.00-0	6.10.00.00-0 Serviço Social	\N
4184	095	a	servico	6.10.00.00-0 Serviço Social	\N
4185	095	a	social	6.10.00.00-0 Serviço Social	\N
4186	095	a	6.10.01.00-7	6.10.01.00-7 Fundamentos do Serviço Social	\N
4187	095	a	fundamentos	6.10.01.00-7 Fundamentos do Serviço Social	\N
4188	095	a	do	6.10.01.00-7 Fundamentos do Serviço Social	\N
4189	095	a	servico	6.10.01.00-7 Fundamentos do Serviço Social	\N
4190	095	a	social	6.10.01.00-7 Fundamentos do Serviço Social	\N
4191	095	a	6.10.02.00-3	6.10.02.00-3 Serviço Social Aplicado	\N
4192	095	a	servico	6.10.02.00-3 Serviço Social Aplicado	\N
4193	095	a	social	6.10.02.00-3 Serviço Social Aplicado	\N
4194	095	a	aplicado	6.10.02.00-3 Serviço Social Aplicado	\N
4195	095	a	6.10.02.01-1	6.10.02.01-1 Serviço Social do Trabalho	\N
4196	095	a	servico	6.10.02.01-1 Serviço Social do Trabalho	\N
4197	095	a	social	6.10.02.01-1 Serviço Social do Trabalho	\N
4198	095	a	do	6.10.02.01-1 Serviço Social do Trabalho	\N
4199	095	a	trabalho	6.10.02.01-1 Serviço Social do Trabalho	\N
4200	095	a	6.10.02.02-0	6.10.02.02-0 Serviço Social da Educação	\N
4201	095	a	servico	6.10.02.02-0 Serviço Social da Educação	\N
4202	095	a	social	6.10.02.02-0 Serviço Social da Educação	\N
4203	095	a	da	6.10.02.02-0 Serviço Social da Educação	\N
4204	095	a	educacao	6.10.02.02-0 Serviço Social da Educação	\N
4205	095	a	6.10.02.03-8	6.10.02.03-8 Serviço Social do Menor	\N
4206	095	a	servico	6.10.02.03-8 Serviço Social do Menor	\N
4207	095	a	social	6.10.02.03-8 Serviço Social do Menor	\N
4208	095	a	do	6.10.02.03-8 Serviço Social do Menor	\N
4209	095	a	menor	6.10.02.03-8 Serviço Social do Menor	\N
4210	095	a	6.10.02.04-6	6.10.02.04-6 Serviço Social da Saúde	\N
4211	095	a	servico	6.10.02.04-6 Serviço Social da Saúde	\N
4212	095	a	social	6.10.02.04-6 Serviço Social da Saúde	\N
4213	095	a	da	6.10.02.04-6 Serviço Social da Saúde	\N
4214	095	a	saude	6.10.02.04-6 Serviço Social da Saúde	\N
4215	095	a	6.10.02.05-4	6.10.02.05-4 Serviço Social da Habitação	\N
4216	095	a	servico	6.10.02.05-4 Serviço Social da Habitação	\N
4217	095	a	social	6.10.02.05-4 Serviço Social da Habitação	\N
4218	095	a	da	6.10.02.05-4 Serviço Social da Habitação	\N
4219	095	a	habitacao	6.10.02.05-4 Serviço Social da Habitação	\N
4220	095	a	6.11.00.00-5	6.11.00.00-5 Economia Doméstica	\N
4221	095	a	economia	6.11.00.00-5 Economia Doméstica	\N
4222	095	a	domestica	6.11.00.00-5 Economia Doméstica	\N
4223	095	a	6.12.00.00-0	6.12.00.00-0 Desenho Industrial	\N
4224	095	a	desenho	6.12.00.00-0 Desenho Industrial	\N
4225	095	a	industrial	6.12.00.00-0 Desenho Industrial	\N
4226	095	a	6.12.01.00-6	6.12.01.00-6 Programação Visual	\N
4227	095	a	programacao	6.12.01.00-6 Programação Visual	\N
4228	095	a	visual	6.12.01.00-6 Programação Visual	\N
4229	095	a	6.12.02.00-2	6.12.02.00-2 Desenho de Produto	\N
4230	095	a	desenho	6.12.02.00-2 Desenho de Produto	\N
4231	095	a	de	6.12.02.00-2 Desenho de Produto	\N
4232	095	a	produto	6.12.02.00-2 Desenho de Produto	\N
4233	095	a	6.13.00.00-4	6.13.00.00-4 Turismo	\N
4234	095	a	turismo	6.13.00.00-4 Turismo	\N
4235	095	a	7.00.00.00-0	7.00.00.00-0 Ciências Humanas	\N
4236	095	a	ciencias	7.00.00.00-0 Ciências Humanas	\N
4237	095	a	humanas	7.00.00.00-0 Ciências Humanas	\N
4238	095	a	7.01.00.00-4	7.01.00.00-4 Filosofia	\N
4239	095	a	filosofia	7.01.00.00-4 Filosofia	\N
4240	095	a	7.01.01.00-0	7.01.01.00-0 História da Filosofia	\N
4241	095	a	historia	7.01.01.00-0 História da Filosofia	\N
4242	095	a	da	7.01.01.00-0 História da Filosofia	\N
4243	095	a	filosofia	7.01.01.00-0 História da Filosofia	\N
4244	095	a	7.01.02.00-7	7.01.02.00-7 Metafísica	\N
4245	095	a	metafisica	7.01.02.00-7 Metafísica	\N
4246	095	a	7.01.03.00-3	7.01.03.00-3 Lógica	\N
4247	095	a	logica	7.01.03.00-3 Lógica	\N
4248	095	a	7.01.04.00-0	7.01.04.00-0 Ética	\N
4249	095	a	etica	7.01.04.00-0 Ética	\N
4250	095	a	7.01.05.00-6	7.01.05.00-6 Epistemologia	\N
4251	095	a	epistemologia	7.01.05.00-6 Epistemologia	\N
4252	095	a	7.01.06.00-2	7.01.06.00-2 Filosofia Brasileira	\N
4253	095	a	filosofia	7.01.06.00-2 Filosofia Brasileira	\N
4254	095	a	brasileira	7.01.06.00-2 Filosofia Brasileira	\N
4255	095	a	7.02.00.00-9	7.02.00.00-9 Sociologia	\N
4256	095	a	sociologia	7.02.00.00-9 Sociologia	\N
4257	095	a	7.02.01.00-5	7.02.01.00-5 Fundamentos da Sociologia	\N
4258	095	a	fundamentos	7.02.01.00-5 Fundamentos da Sociologia	\N
4259	095	a	da	7.02.01.00-5 Fundamentos da Sociologia	\N
4260	095	a	sociologia	7.02.01.00-5 Fundamentos da Sociologia	\N
4261	095	a	7.02.01.01-3	7.02.01.01-3 Teoria Sociológica	\N
4262	095	a	teoria	7.02.01.01-3 Teoria Sociológica	\N
4263	095	a	sociologica	7.02.01.01-3 Teoria Sociológica	\N
4264	095	a	7.02.01.02-1	7.02.01.02-1 História da Sociologia	\N
4265	095	a	historia	7.02.01.02-1 História da Sociologia	\N
4266	095	a	da	7.02.01.02-1 História da Sociologia	\N
4267	095	a	sociologia	7.02.01.02-1 História da Sociologia	\N
4268	095	a	7.02.02.00-1	7.02.02.00-1 Sociologia do Conhecimento	\N
4269	095	a	sociologia	7.02.02.00-1 Sociologia do Conhecimento	\N
4270	095	a	do	7.02.02.00-1 Sociologia do Conhecimento	\N
4271	095	a	conhecimento	7.02.02.00-1 Sociologia do Conhecimento	\N
4272	095	a	7.02.03.00-8	7.02.03.00-8 Sociologia do Desenvolvimento	\N
4273	095	a	sociologia	7.02.03.00-8 Sociologia do Desenvolvimento	\N
4274	095	a	do	7.02.03.00-8 Sociologia do Desenvolvimento	\N
4275	095	a	desenvolvimento	7.02.03.00-8 Sociologia do Desenvolvimento	\N
4276	095	a	7.02.04.00-4	7.02.04.00-4 Sociologia Urbana	\N
4277	095	a	sociologia	7.02.04.00-4 Sociologia Urbana	\N
4278	095	a	urbana	7.02.04.00-4 Sociologia Urbana	\N
4279	095	a	7.02.05.00-0	7.02.05.00-0 Sociologia Rural	\N
4280	095	a	sociologia	7.02.05.00-0 Sociologia Rural	\N
4281	095	a	rural	7.02.05.00-0 Sociologia Rural	\N
4282	095	a	7.02.06.00-7	7.02.06.00-7 Sociologia da Saúde	\N
4283	095	a	sociologia	7.02.06.00-7 Sociologia da Saúde	\N
4284	095	a	da	7.02.06.00-7 Sociologia da Saúde	\N
4285	095	a	saude	7.02.06.00-7 Sociologia da Saúde	\N
4286	095	a	7.02.07.00-3	7.02.07.00-3 Outras Sociologias Específicas	\N
4287	095	a	outras	7.02.07.00-3 Outras Sociologias Específicas	\N
4288	095	a	sociologias	7.02.07.00-3 Outras Sociologias Específicas	\N
4289	095	a	especificas	7.02.07.00-3 Outras Sociologias Específicas	\N
4290	095	a	7.03.00.00-3	7.03.00.00-3 Antropologia	\N
4291	095	a	antropologia	7.03.00.00-3 Antropologia	\N
4292	095	a	7.03.01.00-0	7.03.01.00-0 Teoria Antropológica	\N
4293	095	a	teoria	7.03.01.00-0 Teoria Antropológica	\N
4294	095	a	antropologica	7.03.01.00-0 Teoria Antropológica	\N
4295	095	a	7.03.02.00-6	7.03.02.00-6 Etnologia Indígena	\N
4296	095	a	etnologia	7.03.02.00-6 Etnologia Indígena	\N
4297	095	a	indigena	7.03.02.00-6 Etnologia Indígena	\N
4298	095	a	7.03.03.00-2	7.03.03.00-2 Antropologia Urbana	\N
4299	095	a	antropologia	7.03.03.00-2 Antropologia Urbana	\N
4300	095	a	urbana	7.03.03.00-2 Antropologia Urbana	\N
4301	095	a	7.03.04.00-9	7.03.04.00-9 Antropologia Rural	\N
4302	095	a	antropologia	7.03.04.00-9 Antropologia Rural	\N
4303	095	a	rural	7.03.04.00-9 Antropologia Rural	\N
4304	095	a	7.03.05.00-5	7.03.05.00-5 Antropologia das Populações Afro-Brasileiras	\N
4305	095	a	antropologia	7.03.05.00-5 Antropologia das Populações Afro-Brasileiras	\N
4306	095	a	das	7.03.05.00-5 Antropologia das Populações Afro-Brasileiras	\N
4307	095	a	populacoes	7.03.05.00-5 Antropologia das Populações Afro-Brasileiras	\N
4308	095	a	afro-brasileiras	7.03.05.00-5 Antropologia das Populações Afro-Brasileiras	\N
4309	095	a	7.04.00.00-8	7.04.00.00-8 Arqueologia	\N
4310	095	a	arqueologia	7.04.00.00-8 Arqueologia	\N
4311	095	a	7.04.01.00-4	7.04.01.00-4 Teoria e Método em Arqueologia	\N
4312	095	a	teoria	7.04.01.00-4 Teoria e Método em Arqueologia	\N
4313	095	a	metodo	7.04.01.00-4 Teoria e Método em Arqueologia	\N
4314	095	a	em	7.04.01.00-4 Teoria e Método em Arqueologia	\N
4315	095	a	arqueologia	7.04.01.00-4 Teoria e Método em Arqueologia	\N
4316	095	a	7.04.02.00-0	7.04.02.00-0 Arqueologia Pré-Histórica	\N
4317	095	a	arqueologia	7.04.02.00-0 Arqueologia Pré-Histórica	\N
4318	095	a	pre-historica	7.04.02.00-0 Arqueologia Pré-Histórica	\N
4319	095	a	7.04.03.00-7	7.04.03.00-7 Arqueologia Histórica	\N
4320	095	a	arqueologia	7.04.03.00-7 Arqueologia Histórica	\N
4321	095	a	historica	7.04.03.00-7 Arqueologia Histórica	\N
4322	095	a	7.05.00.00-2	7.05.00.00-2 História	\N
4323	095	a	historia	7.05.00.00-2 História	\N
4324	095	a	7.05.01.00-9	7.05.01.00-9 Teoria e Filosofia da História	\N
4325	095	a	teoria	7.05.01.00-9 Teoria e Filosofia da História	\N
4326	095	a	filosofia	7.05.01.00-9 Teoria e Filosofia da História	\N
4327	095	a	da	7.05.01.00-9 Teoria e Filosofia da História	\N
4328	095	a	historia	7.05.01.00-9 Teoria e Filosofia da História	\N
4329	095	a	7.05.02.00-5	7.05.02.00-5 História Antiga e Medieval	\N
4330	095	a	historia	7.05.02.00-5 História Antiga e Medieval	\N
4331	095	a	antiga	7.05.02.00-5 História Antiga e Medieval	\N
4332	095	a	medieval	7.05.02.00-5 História Antiga e Medieval	\N
4333	095	a	7.05.03.00-1	7.05.03.00-1 História Moderna e Contemporânea	\N
4334	095	a	historia	7.05.03.00-1 História Moderna e Contemporânea	\N
4335	095	a	moderna	7.05.03.00-1 História Moderna e Contemporânea	\N
4336	095	a	contemporanea	7.05.03.00-1 História Moderna e Contemporânea	\N
4337	095	a	7.05.04.00-8	7.05.04.00-8 História da América	\N
4338	095	a	historia	7.05.04.00-8 História da América	\N
4339	095	a	da	7.05.04.00-8 História da América	\N
4340	095	a	america	7.05.04.00-8 História da América	\N
4341	095	a	7.05.04.01-6	7.05.04.01-6 História dos Estados Unidos	\N
4342	095	a	historia	7.05.04.01-6 História dos Estados Unidos	\N
4343	095	a	dos	7.05.04.01-6 História dos Estados Unidos	\N
4344	095	a	estados	7.05.04.01-6 História dos Estados Unidos	\N
4345	095	a	unidos	7.05.04.01-6 História dos Estados Unidos	\N
4346	095	a	7.05.04.02-4	7.05.04.02-4 História Latino-Americana	\N
4347	095	a	historia	7.05.04.02-4 História Latino-Americana	\N
4348	095	a	latino-americana	7.05.04.02-4 História Latino-Americana	\N
4349	095	a	7.05.05.00-4	7.05.05.00-4 História do Brasil	\N
4350	095	a	historia	7.05.05.00-4 História do Brasil	\N
4351	095	a	do	7.05.05.00-4 História do Brasil	\N
4352	095	a	brasil	7.05.05.00-4 História do Brasil	\N
4353	095	a	7.05.05.01-2	7.05.05.01-2 História do Brasil Colônia	\N
4354	095	a	historia	7.05.05.01-2 História do Brasil Colônia	\N
4355	095	a	do	7.05.05.01-2 História do Brasil Colônia	\N
4356	095	a	brasil	7.05.05.01-2 História do Brasil Colônia	\N
4357	095	a	colonia	7.05.05.01-2 História do Brasil Colônia	\N
4358	095	a	7.05.05.02-0	7.05.05.02-0 História do Brasil Império	\N
4359	095	a	historia	7.05.05.02-0 História do Brasil Império	\N
4360	095	a	do	7.05.05.02-0 História do Brasil Império	\N
4361	095	a	brasil	7.05.05.02-0 História do Brasil Império	\N
4362	095	a	imperio	7.05.05.02-0 História do Brasil Império	\N
4363	095	a	7.05.05.03-9	7.05.05.03-9 História do Brasil República	\N
4364	095	a	historia	7.05.05.03-9 História do Brasil República	\N
4365	095	a	do	7.05.05.03-9 História do Brasil República	\N
4366	095	a	brasil	7.05.05.03-9 História do Brasil República	\N
4367	095	a	republica	7.05.05.03-9 História do Brasil República	\N
4368	095	a	7.05.05.04-7	7.05.05.04-7 História Regional do Brasil	\N
4369	095	a	historia	7.05.05.04-7 História Regional do Brasil	\N
4370	095	a	regional	7.05.05.04-7 História Regional do Brasil	\N
4371	095	a	do	7.05.05.04-7 História Regional do Brasil	\N
4372	095	a	brasil	7.05.05.04-7 História Regional do Brasil	\N
4373	095	a	7.05.06.00-0	7.05.06.00-0 História das Ciências	\N
4374	095	a	historia	7.05.06.00-0 História das Ciências	\N
4375	095	a	das	7.05.06.00-0 História das Ciências	\N
4376	095	a	ciencias	7.05.06.00-0 História das Ciências	\N
4377	095	a	7.06.00.00-7	7.06.00.00-7 Geografia	\N
4378	095	a	geografia	7.06.00.00-7 Geografia	\N
4379	095	a	7.06.01.00-3	7.06.01.00-3 Geografia Humana	\N
4380	095	a	geografia	7.06.01.00-3 Geografia Humana	\N
4381	095	a	humana	7.06.01.00-3 Geografia Humana	\N
4382	095	a	7.06.01.01-1	7.06.01.01-1 Geografia da População	\N
4383	095	a	geografia	7.06.01.01-1 Geografia da População	\N
4384	095	a	da	7.06.01.01-1 Geografia da População	\N
4385	095	a	populacao	7.06.01.01-1 Geografia da População	\N
4386	095	a	7.06.01.02-0	7.06.01.02-0 Geografia Agrária	\N
4387	095	a	geografia	7.06.01.02-0 Geografia Agrária	\N
4388	095	a	agraria	7.06.01.02-0 Geografia Agrária	\N
4389	095	a	7.06.01.03-8	7.06.01.03-8 Geografia Urbana	\N
4390	095	a	geografia	7.06.01.03-8 Geografia Urbana	\N
4391	095	a	urbana	7.06.01.03-8 Geografia Urbana	\N
4392	095	a	7.06.01.04-6	7.06.01.04-6 Geografia Econômica	\N
4393	095	a	geografia	7.06.01.04-6 Geografia Econômica	\N
4394	095	a	economica	7.06.01.04-6 Geografia Econômica	\N
4395	095	a	7.06.01.05-4	7.06.01.05-4 Geografia Política	\N
4396	095	a	geografia	7.06.01.05-4 Geografia Política	\N
4397	095	a	politica	7.06.01.05-4 Geografia Política	\N
4398	095	a	7.06.02.00-0	7.06.02.00-0 Geografia Regional	\N
4399	095	a	geografia	7.06.02.00-0 Geografia Regional	\N
4400	095	a	regional	7.06.02.00-0 Geografia Regional	\N
4401	095	a	7.06.02.01-8	7.06.02.01-8 Teoria do Desenvolvimento Regional	\N
4402	095	a	teoria	7.06.02.01-8 Teoria do Desenvolvimento Regional	\N
4403	095	a	do	7.06.02.01-8 Teoria do Desenvolvimento Regional	\N
4404	095	a	desenvolvimento	7.06.02.01-8 Teoria do Desenvolvimento Regional	\N
4405	095	a	regional	7.06.02.01-8 Teoria do Desenvolvimento Regional	\N
4406	095	a	7.06.02.02-6	7.06.02.02-6 Regionalização	\N
4407	095	a	regionalizacao	7.06.02.02-6 Regionalização	\N
4408	095	a	7.06.02.03-4	7.06.02.03-4 Análise Regional	\N
4409	095	a	analise	7.06.02.03-4 Análise Regional	\N
4410	095	a	regional	7.06.02.03-4 Análise Regional	\N
4411	095	a	7.07.00.00-1	7.07.00.00-1 Psicologia	\N
4412	095	a	psicologia	7.07.00.00-1 Psicologia	\N
4413	095	a	7.07.01.00-8	7.07.01.00-8 Fundamentos e Medidas da Psicologia	\N
4414	095	a	fundamentos	7.07.01.00-8 Fundamentos e Medidas da Psicologia	\N
4415	095	a	medidas	7.07.01.00-8 Fundamentos e Medidas da Psicologia	\N
4416	095	a	da	7.07.01.00-8 Fundamentos e Medidas da Psicologia	\N
4417	095	a	psicologia	7.07.01.00-8 Fundamentos e Medidas da Psicologia	\N
4418	095	a	7.07.01.01-6	7.07.01.01-6 História, Teorias e Sistemas em Psicologia	\N
4419	095	a	historia,	7.07.01.01-6 História, Teorias e Sistemas em Psicologia	\N
4420	095	a	teorias	7.07.01.01-6 História, Teorias e Sistemas em Psicologia	\N
4421	095	a	sistemas	7.07.01.01-6 História, Teorias e Sistemas em Psicologia	\N
4422	095	a	em	7.07.01.01-6 História, Teorias e Sistemas em Psicologia	\N
4423	095	a	psicologia	7.07.01.01-6 História, Teorias e Sistemas em Psicologia	\N
4424	095	a	7.07.01.02-4	7.07.01.02-4 Metodologia, Instrumentação e Equipamento em Psicologia	\N
4425	095	a	metodologia,	7.07.01.02-4 Metodologia, Instrumentação e Equipamento em Psicologia	\N
4426	095	a	instrumentacao	7.07.01.02-4 Metodologia, Instrumentação e Equipamento em Psicologia	\N
4427	095	a	equipamento	7.07.01.02-4 Metodologia, Instrumentação e Equipamento em Psicologia	\N
4428	095	a	em	7.07.01.02-4 Metodologia, Instrumentação e Equipamento em Psicologia	\N
4429	095	a	psicologia	7.07.01.02-4 Metodologia, Instrumentação e Equipamento em Psicologia	\N
4430	095	a	7.07.01.03-2	7.07.01.03-2 Construção e Validade de Testes, Escalas e Outras Medidas Psicológicas	\N
4431	095	a	construcao	7.07.01.03-2 Construção e Validade de Testes, Escalas e Outras Medidas Psicológicas	\N
4432	095	a	validade	7.07.01.03-2 Construção e Validade de Testes, Escalas e Outras Medidas Psicológicas	\N
4433	095	a	de	7.07.01.03-2 Construção e Validade de Testes, Escalas e Outras Medidas Psicológicas	\N
4434	095	a	testes,	7.07.01.03-2 Construção e Validade de Testes, Escalas e Outras Medidas Psicológicas	\N
4435	095	a	escalas	7.07.01.03-2 Construção e Validade de Testes, Escalas e Outras Medidas Psicológicas	\N
4436	095	a	outras	7.07.01.03-2 Construção e Validade de Testes, Escalas e Outras Medidas Psicológicas	\N
4437	095	a	medidas	7.07.01.03-2 Construção e Validade de Testes, Escalas e Outras Medidas Psicológicas	\N
4438	095	a	psicologicas	7.07.01.03-2 Construção e Validade de Testes, Escalas e Outras Medidas Psicológicas	\N
4439	095	a	7.07.01.04-0	7.07.01.04-0 Técnicas de Processamento Estatístico, Matemático e Computacional em Psicologia	\N
4440	095	a	tecnicas	7.07.01.04-0 Técnicas de Processamento Estatístico, Matemático e Computacional em Psicologia	\N
4441	095	a	de	7.07.01.04-0 Técnicas de Processamento Estatístico, Matemático e Computacional em Psicologia	\N
4442	095	a	processamento	7.07.01.04-0 Técnicas de Processamento Estatístico, Matemático e Computacional em Psicologia	\N
4443	095	a	estatistico,	7.07.01.04-0 Técnicas de Processamento Estatístico, Matemático e Computacional em Psicologia	\N
4444	095	a	matematico	7.07.01.04-0 Técnicas de Processamento Estatístico, Matemático e Computacional em Psicologia	\N
4445	095	a	computacional	7.07.01.04-0 Técnicas de Processamento Estatístico, Matemático e Computacional em Psicologia	\N
4446	095	a	em	7.07.01.04-0 Técnicas de Processamento Estatístico, Matemático e Computacional em Psicologia	\N
4447	095	a	psicologia	7.07.01.04-0 Técnicas de Processamento Estatístico, Matemático e Computacional em Psicologia	\N
4448	095	a	7.07.02.00-4	7.07.02.00-4 Psicologia Experimental	\N
4449	095	a	psicologia	7.07.02.00-4 Psicologia Experimental	\N
4450	095	a	experimental	7.07.02.00-4 Psicologia Experimental	\N
4451	095	a	7.07.02.01-2	7.07.02.01-2 Processos Perceptuais e Motores	\N
4452	095	a	processos	7.07.02.01-2 Processos Perceptuais e Motores	\N
4453	095	a	perceptuais	7.07.02.01-2 Processos Perceptuais e Motores	\N
4454	095	a	motores	7.07.02.01-2 Processos Perceptuais e Motores	\N
4455	095	a	7.07.02.02-0	7.07.02.02-0 Processos de Aprendizagem, Memória e Motivação	\N
4456	095	a	processos	7.07.02.02-0 Processos de Aprendizagem, Memória e Motivação	\N
4457	095	a	de	7.07.02.02-0 Processos de Aprendizagem, Memória e Motivação	\N
4458	095	a	aprendizagem,	7.07.02.02-0 Processos de Aprendizagem, Memória e Motivação	\N
4459	095	a	memoria	7.07.02.02-0 Processos de Aprendizagem, Memória e Motivação	\N
4460	095	a	motivacao	7.07.02.02-0 Processos de Aprendizagem, Memória e Motivação	\N
4461	095	a	7.07.02.03-9	7.07.02.03-9 Processos Cognitivos e Atencionais	\N
4462	095	a	processos	7.07.02.03-9 Processos Cognitivos e Atencionais	\N
4463	095	a	cognitivos	7.07.02.03-9 Processos Cognitivos e Atencionais	\N
4464	095	a	atencionais	7.07.02.03-9 Processos Cognitivos e Atencionais	\N
4465	095	a	7.07.02.04-7	7.07.02.04-7 Estados Subjetivos e Emoção	\N
4466	095	a	estados	7.07.02.04-7 Estados Subjetivos e Emoção	\N
4467	095	a	subjetivos	7.07.02.04-7 Estados Subjetivos e Emoção	\N
4468	095	a	emocao	7.07.02.04-7 Estados Subjetivos e Emoção	\N
4469	095	a	7.07.03.00-0	7.07.03.00-0 Psicologia Fisiológica	\N
4470	095	a	psicologia	7.07.03.00-0 Psicologia Fisiológica	\N
4471	095	a	fisiologica	7.07.03.00-0 Psicologia Fisiológica	\N
4472	095	a	7.07.03.01-9	7.07.03.01-9 Neurologia, Eletrofisiologia e Comportamento	\N
4473	095	a	neurologia,	7.07.03.01-9 Neurologia, Eletrofisiologia e Comportamento	\N
4474	095	a	eletrofisiologia	7.07.03.01-9 Neurologia, Eletrofisiologia e Comportamento	\N
4475	095	a	comportamento	7.07.03.01-9 Neurologia, Eletrofisiologia e Comportamento	\N
4476	095	a	7.07.03.02-7	7.07.03.02-7 Processos Psico-Fisiológicos	\N
4477	095	a	processos	7.07.03.02-7 Processos Psico-Fisiológicos	\N
4478	095	a	psico-fisiologicos	7.07.03.02-7 Processos Psico-Fisiológicos	\N
4479	095	a	7.07.03.03-5	7.07.03.03-5 Estimulação Elétrica e com Drogas; Comportamento	\N
4480	095	a	estimulacao	7.07.03.03-5 Estimulação Elétrica e com Drogas; Comportamento	\N
4481	095	a	eletrica	7.07.03.03-5 Estimulação Elétrica e com Drogas; Comportamento	\N
4482	095	a	com	7.07.03.03-5 Estimulação Elétrica e com Drogas; Comportamento	\N
4483	095	a	drogas;	7.07.03.03-5 Estimulação Elétrica e com Drogas; Comportamento	\N
4484	095	a	comportamento	7.07.03.03-5 Estimulação Elétrica e com Drogas; Comportamento	\N
4485	095	a	7.07.03.04-3	7.07.03.04-3 Psicobiologia	\N
4486	095	a	psicobiologia	7.07.03.04-3 Psicobiologia	\N
4487	095	a	7.07.04.00-7	7.07.04.00-7 Psicologia Comparativa	\N
4488	095	a	psicologia	7.07.04.00-7 Psicologia Comparativa	\N
4489	095	a	comparativa	7.07.04.00-7 Psicologia Comparativa	\N
4490	095	a	7.07.04.01-5	7.07.04.01-5 Estudos Naturalísticos do Comportamento Animal	\N
4491	095	a	estudos	7.07.04.01-5 Estudos Naturalísticos do Comportamento Animal	\N
4492	095	a	naturalisticos	7.07.04.01-5 Estudos Naturalísticos do Comportamento Animal	\N
4493	095	a	do	7.07.04.01-5 Estudos Naturalísticos do Comportamento Animal	\N
4494	095	a	comportamento	7.07.04.01-5 Estudos Naturalísticos do Comportamento Animal	\N
4495	095	a	animal	7.07.04.01-5 Estudos Naturalísticos do Comportamento Animal	\N
4496	095	a	7.07.04.02-3	7.07.04.02-3 Mecanismos Instintivos e Processos Sociais em Animais	\N
4497	095	a	mecanismos	7.07.04.02-3 Mecanismos Instintivos e Processos Sociais em Animais	\N
4498	095	a	instintivos	7.07.04.02-3 Mecanismos Instintivos e Processos Sociais em Animais	\N
4499	095	a	processos	7.07.04.02-3 Mecanismos Instintivos e Processos Sociais em Animais	\N
4500	095	a	sociais	7.07.04.02-3 Mecanismos Instintivos e Processos Sociais em Animais	\N
4501	095	a	em	7.07.04.02-3 Mecanismos Instintivos e Processos Sociais em Animais	\N
4502	095	a	animais	7.07.04.02-3 Mecanismos Instintivos e Processos Sociais em Animais	\N
4503	095	a	7.07.05.00-3	7.07.05.00-3 Psicologia Social	\N
4504	095	a	psicologia	7.07.05.00-3 Psicologia Social	\N
4505	095	a	social	7.07.05.00-3 Psicologia Social	\N
4506	095	a	7.07.05.01-1	7.07.05.01-1 Relações Interpessoais	\N
4507	095	a	relacoes	7.07.05.01-1 Relações Interpessoais	\N
4508	095	a	interpessoais	7.07.05.01-1 Relações Interpessoais	\N
4509	095	a	7.07.05.02-0	7.07.05.02-0 Processos Grupais e de Comunicação	\N
4510	095	a	processos	7.07.05.02-0 Processos Grupais e de Comunicação	\N
4511	095	a	grupais	7.07.05.02-0 Processos Grupais e de Comunicação	\N
4512	095	a	de	7.07.05.02-0 Processos Grupais e de Comunicação	\N
4513	095	a	comunicacao	7.07.05.02-0 Processos Grupais e de Comunicação	\N
4514	095	a	7.07.05.03-8	7.07.05.03-8 Papéis e Estruturas Sociais; Indivíduo	\N
4515	095	a	papeis	7.07.05.03-8 Papéis e Estruturas Sociais; Indivíduo	\N
4516	095	a	estruturas	7.07.05.03-8 Papéis e Estruturas Sociais; Indivíduo	\N
4517	095	a	sociais;	7.07.05.03-8 Papéis e Estruturas Sociais; Indivíduo	\N
4518	095	a	individuo	7.07.05.03-8 Papéis e Estruturas Sociais; Indivíduo	\N
4519	095	a	7.07.06.00-0	7.07.06.00-0 Psicologia Cognitiva	\N
4520	095	a	psicologia	7.07.06.00-0 Psicologia Cognitiva	\N
4521	095	a	cognitiva	7.07.06.00-0 Psicologia Cognitiva	\N
4522	095	a	7.07.07.00-6	7.07.07.00-6 Psicologia do Desenvolvimento Humano	\N
4523	095	a	psicologia	7.07.07.00-6 Psicologia do Desenvolvimento Humano	\N
4524	095	a	do	7.07.07.00-6 Psicologia do Desenvolvimento Humano	\N
4525	095	a	desenvolvimento	7.07.07.00-6 Psicologia do Desenvolvimento Humano	\N
4526	095	a	humano	7.07.07.00-6 Psicologia do Desenvolvimento Humano	\N
4527	095	a	7.07.07.01-4	7.07.07.01-4 Processos Perceptuais e Cognitivos; Desenvolvimento	\N
4528	095	a	processos	7.07.07.01-4 Processos Perceptuais e Cognitivos; Desenvolvimento	\N
4529	095	a	perceptuais	7.07.07.01-4 Processos Perceptuais e Cognitivos; Desenvolvimento	\N
4530	095	a	cognitivos;	7.07.07.01-4 Processos Perceptuais e Cognitivos; Desenvolvimento	\N
4531	095	a	desenvolvimento	7.07.07.01-4 Processos Perceptuais e Cognitivos; Desenvolvimento	\N
4532	095	a	7.07.07.02-2	7.07.07.02-2 Desenvolvimento Social e da Personalidade	\N
4533	095	a	desenvolvimento	7.07.07.02-2 Desenvolvimento Social e da Personalidade	\N
4534	095	a	social	7.07.07.02-2 Desenvolvimento Social e da Personalidade	\N
4535	095	a	da	7.07.07.02-2 Desenvolvimento Social e da Personalidade	\N
4536	095	a	personalidade	7.07.07.02-2 Desenvolvimento Social e da Personalidade	\N
4537	095	a	7.07.08.00-2	7.07.08.00-2 Psicologia do Ensino e da Aprendizagem	\N
4538	095	a	psicologia	7.07.08.00-2 Psicologia do Ensino e da Aprendizagem	\N
4539	095	a	do	7.07.08.00-2 Psicologia do Ensino e da Aprendizagem	\N
4540	095	a	ensino	7.07.08.00-2 Psicologia do Ensino e da Aprendizagem	\N
4541	095	a	da	7.07.08.00-2 Psicologia do Ensino e da Aprendizagem	\N
4542	095	a	aprendizagem	7.07.08.00-2 Psicologia do Ensino e da Aprendizagem	\N
4543	095	a	7.07.08.01-0	7.07.08.01-0 Planejamento Institucional	\N
4544	095	a	planejamento	7.07.08.01-0 Planejamento Institucional	\N
4545	095	a	institucional	7.07.08.01-0 Planejamento Institucional	\N
4546	095	a	7.07.08.02-9	7.07.08.02-9 Programação de Condições de Ensino	\N
4547	095	a	programacao	7.07.08.02-9 Programação de Condições de Ensino	\N
4548	095	a	de	7.07.08.02-9 Programação de Condições de Ensino	\N
4549	095	a	condicoes	7.07.08.02-9 Programação de Condições de Ensino	\N
4550	095	a	de	7.07.08.02-9 Programação de Condições de Ensino	\N
4551	095	a	ensino	7.07.08.02-9 Programação de Condições de Ensino	\N
4552	095	a	7.07.08.03-7	7.07.08.03-7 Treinamento de Pessoal	\N
4553	095	a	treinamento	7.07.08.03-7 Treinamento de Pessoal	\N
4554	095	a	de	7.07.08.03-7 Treinamento de Pessoal	\N
4555	095	a	pessoal	7.07.08.03-7 Treinamento de Pessoal	\N
4556	095	a	7.07.08.04-5	7.07.08.04-5 Aprendizagem e Desempenho Acadêmicos	\N
4557	095	a	aprendizagem	7.07.08.04-5 Aprendizagem e Desempenho Acadêmicos	\N
4558	095	a	desempenho	7.07.08.04-5 Aprendizagem e Desempenho Acadêmicos	\N
4559	095	a	academicos	7.07.08.04-5 Aprendizagem e Desempenho Acadêmicos	\N
4560	095	a	7.07.08.05-3	7.07.08.05-3 Ensino e Aprendizagem na Sala de Aula	\N
4561	095	a	ensino	7.07.08.05-3 Ensino e Aprendizagem na Sala de Aula	\N
4562	095	a	aprendizagem	7.07.08.05-3 Ensino e Aprendizagem na Sala de Aula	\N
4563	095	a	na	7.07.08.05-3 Ensino e Aprendizagem na Sala de Aula	\N
4564	095	a	sala	7.07.08.05-3 Ensino e Aprendizagem na Sala de Aula	\N
4565	095	a	de	7.07.08.05-3 Ensino e Aprendizagem na Sala de Aula	\N
4566	095	a	aula	7.07.08.05-3 Ensino e Aprendizagem na Sala de Aula	\N
4567	095	a	7.07.09.00-9	7.07.09.00-9 Psicologia do Trabalho e Organizacional	\N
4568	095	a	psicologia	7.07.09.00-9 Psicologia do Trabalho e Organizacional	\N
4569	095	a	do	7.07.09.00-9 Psicologia do Trabalho e Organizacional	\N
4570	095	a	trabalho	7.07.09.00-9 Psicologia do Trabalho e Organizacional	\N
4571	095	a	organizacional	7.07.09.00-9 Psicologia do Trabalho e Organizacional	\N
4572	095	a	7.07.09.01-7	7.07.09.01-7 Análise Institucional	\N
4573	095	a	analise	7.07.09.01-7 Análise Institucional	\N
4574	095	a	institucional	7.07.09.01-7 Análise Institucional	\N
4575	095	a	7.07.09.02-5	7.07.09.02-5 Recrutamento e Seleção de Pessoal	\N
4576	095	a	recrutamento	7.07.09.02-5 Recrutamento e Seleção de Pessoal	\N
4577	095	a	selecao	7.07.09.02-5 Recrutamento e Seleção de Pessoal	\N
4578	095	a	de	7.07.09.02-5 Recrutamento e Seleção de Pessoal	\N
4579	095	a	pessoal	7.07.09.02-5 Recrutamento e Seleção de Pessoal	\N
4580	095	a	7.07.09.03-3	7.07.09.03-3 Treinamento e Avaliação	\N
4581	095	a	treinamento	7.07.09.03-3 Treinamento e Avaliação	\N
4582	095	a	avaliacao	7.07.09.03-3 Treinamento e Avaliação	\N
4583	095	a	7.07.09.04-1	7.07.09.04-1 Fatores Humanos no Trabalho	\N
4584	095	a	fatores	7.07.09.04-1 Fatores Humanos no Trabalho	\N
4585	095	a	humanos	7.07.09.04-1 Fatores Humanos no Trabalho	\N
4586	095	a	no	7.07.09.04-1 Fatores Humanos no Trabalho	\N
4587	095	a	trabalho	7.07.09.04-1 Fatores Humanos no Trabalho	\N
4588	095	a	7.07.09.05-0	7.07.09.05-0 Planejamento Ambiental e Comportamento Humano	\N
4589	095	a	planejamento	7.07.09.05-0 Planejamento Ambiental e Comportamento Humano	\N
4590	095	a	ambiental	7.07.09.05-0 Planejamento Ambiental e Comportamento Humano	\N
4591	095	a	comportamento	7.07.09.05-0 Planejamento Ambiental e Comportamento Humano	\N
4592	095	a	humano	7.07.09.05-0 Planejamento Ambiental e Comportamento Humano	\N
4593	095	a	7.07.10.00-7	7.07.10.00-7 Tratamento e Prevenção Psicológica	\N
4594	095	a	tratamento	7.07.10.00-7 Tratamento e Prevenção Psicológica	\N
4595	095	a	prevencao	7.07.10.00-7 Tratamento e Prevenção Psicológica	\N
4596	095	a	psicologica	7.07.10.00-7 Tratamento e Prevenção Psicológica	\N
4597	095	a	7.07.10.01-5	7.07.10.01-5 Intervenção Terapêutica	\N
4598	095	a	intervencao	7.07.10.01-5 Intervenção Terapêutica	\N
4599	095	a	terapeutica	7.07.10.01-5 Intervenção Terapêutica	\N
4600	095	a	7.07.10.02-3	7.07.10.02-3 Programas de Atendimento Comunitário	\N
4601	095	a	programas	7.07.10.02-3 Programas de Atendimento Comunitário	\N
4602	095	a	de	7.07.10.02-3 Programas de Atendimento Comunitário	\N
4603	095	a	atendimento	7.07.10.02-3 Programas de Atendimento Comunitário	\N
4604	095	a	comunitario	7.07.10.02-3 Programas de Atendimento Comunitário	\N
4605	095	a	7.07.10.03-1	7.07.10.03-1 Treinamento e Reabilitação	\N
4606	095	a	treinamento	7.07.10.03-1 Treinamento e Reabilitação	\N
4607	095	a	reabilitacao	7.07.10.03-1 Treinamento e Reabilitação	\N
4608	095	a	7.07.10.04-0	7.07.10.04-0 Desvios da Conduta	\N
4609	095	a	desvios	7.07.10.04-0 Desvios da Conduta	\N
4610	095	a	da	7.07.10.04-0 Desvios da Conduta	\N
4611	095	a	conduta	7.07.10.04-0 Desvios da Conduta	\N
4612	095	a	7.07.10.05-8	7.07.10.05-8 Distúrbios da Linguagem	\N
4613	095	a	disturbios	7.07.10.05-8 Distúrbios da Linguagem	\N
4614	095	a	da	7.07.10.05-8 Distúrbios da Linguagem	\N
4615	095	a	linguagem	7.07.10.05-8 Distúrbios da Linguagem	\N
4616	095	a	7.07.10.06-6	7.07.10.06-6 Distúrbios Psicossomáticos	\N
4617	095	a	disturbios	7.07.10.06-6 Distúrbios Psicossomáticos	\N
4618	095	a	psicossomaticos	7.07.10.06-6 Distúrbios Psicossomáticos	\N
4619	095	a	7.08.00.00-6	7.08.00.00-6 Educação	\N
4620	095	a	educacao	7.08.00.00-6 Educação	\N
4621	095	a	7.08.01.00-2	7.08.01.00-2 Fundamentos da Educação	\N
4622	095	a	fundamentos	7.08.01.00-2 Fundamentos da Educação	\N
4623	095	a	da	7.08.01.00-2 Fundamentos da Educação	\N
4624	095	a	educacao	7.08.01.00-2 Fundamentos da Educação	\N
4625	095	a	7.08.01.01-0	7.08.01.01-0 Filosofia da Educação	\N
4626	095	a	filosofia	7.08.01.01-0 Filosofia da Educação	\N
4627	095	a	da	7.08.01.01-0 Filosofia da Educação	\N
4628	095	a	educacao	7.08.01.01-0 Filosofia da Educação	\N
4629	095	a	7.08.01.02-9	7.08.01.02-9 História da Educação	\N
4630	095	a	historia	7.08.01.02-9 História da Educação	\N
4631	095	a	da	7.08.01.02-9 História da Educação	\N
4632	095	a	educacao	7.08.01.02-9 História da Educação	\N
4633	095	a	7.08.01.03-7	7.08.01.03-7 Sociologia da Educação	\N
4634	095	a	sociologia	7.08.01.03-7 Sociologia da Educação	\N
4635	095	a	da	7.08.01.03-7 Sociologia da Educação	\N
4636	095	a	educacao	7.08.01.03-7 Sociologia da Educação	\N
4637	095	a	7.08.01.04-5	7.08.01.04-5 Antropologia Educacional	\N
4638	095	a	antropologia	7.08.01.04-5 Antropologia Educacional	\N
4639	095	a	educacional	7.08.01.04-5 Antropologia Educacional	\N
4640	095	a	7.08.01.05-3	7.08.01.05-3 Economia da Educação	\N
4641	095	a	economia	7.08.01.05-3 Economia da Educação	\N
4642	095	a	da	7.08.01.05-3 Economia da Educação	\N
4643	095	a	educacao	7.08.01.05-3 Economia da Educação	\N
4644	095	a	7.08.01.06-1	7.08.01.06-1 Psicologia Educacional	\N
4645	095	a	psicologia	7.08.01.06-1 Psicologia Educacional	\N
4646	095	a	educacional	7.08.01.06-1 Psicologia Educacional	\N
4647	095	a	7.08.02.00-9	7.08.02.00-9 Administração Educacional	\N
4648	095	a	administracao	7.08.02.00-9 Administração Educacional	\N
4649	095	a	educacional	7.08.02.00-9 Administração Educacional	\N
4650	095	a	7.08.02.01-7	7.08.02.01-7 Administração de Sistemas Educacionais	\N
4651	095	a	administracao	7.08.02.01-7 Administração de Sistemas Educacionais	\N
4652	095	a	de	7.08.02.01-7 Administração de Sistemas Educacionais	\N
4653	095	a	sistemas	7.08.02.01-7 Administração de Sistemas Educacionais	\N
4654	095	a	educacionais	7.08.02.01-7 Administração de Sistemas Educacionais	\N
4655	095	a	7.08.02.02-5	7.08.02.02-5 Administração de Unidades Educativas	\N
4656	095	a	administracao	7.08.02.02-5 Administração de Unidades Educativas	\N
4657	095	a	de	7.08.02.02-5 Administração de Unidades Educativas	\N
4658	095	a	unidades	7.08.02.02-5 Administração de Unidades Educativas	\N
4659	095	a	educativas	7.08.02.02-5 Administração de Unidades Educativas	\N
4660	095	a	7.08.03.00-5	7.08.03.00-5 Planejamento e Avaliação Educacional	\N
4661	095	a	planejamento	7.08.03.00-5 Planejamento e Avaliação Educacional	\N
4662	095	a	avaliacao	7.08.03.00-5 Planejamento e Avaliação Educacional	\N
4663	095	a	educacional	7.08.03.00-5 Planejamento e Avaliação Educacional	\N
4664	095	a	7.08.03.01-3	7.08.03.01-3 Política Educacional	\N
4665	095	a	politica	7.08.03.01-3 Política Educacional	\N
4666	095	a	educacional	7.08.03.01-3 Política Educacional	\N
4667	095	a	7.08.03.02-1	7.08.03.02-1 Planejamento Educacional	\N
4668	095	a	planejamento	7.08.03.02-1 Planejamento Educacional	\N
4669	095	a	educacional	7.08.03.02-1 Planejamento Educacional	\N
4670	095	a	7.08.03.03-0	7.08.03.03-0 Avaliação de Sistemas, Instituições, Planos e Programas Educacionais	\N
4671	095	a	avaliacao	7.08.03.03-0 Avaliação de Sistemas, Instituições, Planos e Programas Educacionais	\N
4672	095	a	de	7.08.03.03-0 Avaliação de Sistemas, Instituições, Planos e Programas Educacionais	\N
4673	095	a	sistemas,	7.08.03.03-0 Avaliação de Sistemas, Instituições, Planos e Programas Educacionais	\N
4674	095	a	instituicoes,	7.08.03.03-0 Avaliação de Sistemas, Instituições, Planos e Programas Educacionais	\N
4675	095	a	planos	7.08.03.03-0 Avaliação de Sistemas, Instituições, Planos e Programas Educacionais	\N
4676	095	a	programas	7.08.03.03-0 Avaliação de Sistemas, Instituições, Planos e Programas Educacionais	\N
4677	095	a	educacionais	7.08.03.03-0 Avaliação de Sistemas, Instituições, Planos e Programas Educacionais	\N
4678	095	a	7.08.04.00-1	7.08.04.00-1 Ensino-Aprendizagem	\N
4679	095	a	ensino-aprendizagem	7.08.04.00-1 Ensino-Aprendizagem	\N
4680	095	a	7.08.04.01-0	7.08.04.01-0 Teorias da Instrução	\N
4681	095	a	teorias	7.08.04.01-0 Teorias da Instrução	\N
4682	095	a	da	7.08.04.01-0 Teorias da Instrução	\N
4683	095	a	instrucao	7.08.04.01-0 Teorias da Instrução	\N
4684	095	a	7.08.04.02-8	7.08.04.02-8 Métodos e Técnicas de Ensino	\N
4685	095	a	metodos	7.08.04.02-8 Métodos e Técnicas de Ensino	\N
4686	095	a	tecnicas	7.08.04.02-8 Métodos e Técnicas de Ensino	\N
4687	095	a	de	7.08.04.02-8 Métodos e Técnicas de Ensino	\N
4688	095	a	ensino	7.08.04.02-8 Métodos e Técnicas de Ensino	\N
4689	095	a	7.08.04.03-6	7.08.04.03-6 Tecnologia Educacional	\N
4690	095	a	tecnologia	7.08.04.03-6 Tecnologia Educacional	\N
4691	095	a	educacional	7.08.04.03-6 Tecnologia Educacional	\N
4692	095	a	7.08.04.04-4	7.08.04.04-4 Avaliação da Aprendizagem	\N
4693	095	a	avaliacao	7.08.04.04-4 Avaliação da Aprendizagem	\N
4694	095	a	da	7.08.04.04-4 Avaliação da Aprendizagem	\N
4695	095	a	aprendizagem	7.08.04.04-4 Avaliação da Aprendizagem	\N
4696	095	a	7.08.05.00-8	7.08.05.00-8 Currículo	\N
4697	095	a	curriculo	7.08.05.00-8 Currículo	\N
4698	095	a	7.08.05.01-6	7.08.05.01-6 Teoria Geral de Planejamento e Desenvolvimento Curricular	\N
4699	095	a	teoria	7.08.05.01-6 Teoria Geral de Planejamento e Desenvolvimento Curricular	\N
4700	095	a	geral	7.08.05.01-6 Teoria Geral de Planejamento e Desenvolvimento Curricular	\N
4701	095	a	de	7.08.05.01-6 Teoria Geral de Planejamento e Desenvolvimento Curricular	\N
4702	095	a	planejamento	7.08.05.01-6 Teoria Geral de Planejamento e Desenvolvimento Curricular	\N
4703	095	a	desenvolvimento	7.08.05.01-6 Teoria Geral de Planejamento e Desenvolvimento Curricular	\N
4704	095	a	curricular	7.08.05.01-6 Teoria Geral de Planejamento e Desenvolvimento Curricular	\N
4705	095	a	7.08.05.02-4	7.08.05.02-4 Currículos Específicos para Níveis e Tipos de Educação	\N
4706	095	a	curriculos	7.08.05.02-4 Currículos Específicos para Níveis e Tipos de Educação	\N
4707	095	a	especificos	7.08.05.02-4 Currículos Específicos para Níveis e Tipos de Educação	\N
4708	095	a	para	7.08.05.02-4 Currículos Específicos para Níveis e Tipos de Educação	\N
4709	095	a	niveis	7.08.05.02-4 Currículos Específicos para Níveis e Tipos de Educação	\N
4710	095	a	tipos	7.08.05.02-4 Currículos Específicos para Níveis e Tipos de Educação	\N
4711	095	a	de	7.08.05.02-4 Currículos Específicos para Níveis e Tipos de Educação	\N
4712	095	a	educacao	7.08.05.02-4 Currículos Específicos para Níveis e Tipos de Educação	\N
4713	095	a	7.08.06.00-4	7.08.06.00-4 Orientação e Aconselhamento	\N
4714	095	a	orientacao	7.08.06.00-4 Orientação e Aconselhamento	\N
4715	095	a	aconselhamento	7.08.06.00-4 Orientação e Aconselhamento	\N
4716	095	a	7.08.06.01-2	7.08.06.01-2 Orientação Educacional	\N
4717	095	a	orientacao	7.08.06.01-2 Orientação Educacional	\N
4718	095	a	educacional	7.08.06.01-2 Orientação Educacional	\N
4719	095	a	7.08.06.02-0	7.08.06.02-0 Orientação Vocacional	\N
4720	095	a	orientacao	7.08.06.02-0 Orientação Vocacional	\N
4721	095	a	vocacional	7.08.06.02-0 Orientação Vocacional	\N
4722	095	a	7.08.07.00-0	7.08.07.00-0 Tópicos Específicos de Educação	\N
4723	095	a	topicos	7.08.07.00-0 Tópicos Específicos de Educação	\N
4724	095	a	especificos	7.08.07.00-0 Tópicos Específicos de Educação	\N
4725	095	a	de	7.08.07.00-0 Tópicos Específicos de Educação	\N
4726	095	a	educacao	7.08.07.00-0 Tópicos Específicos de Educação	\N
4727	095	a	7.08.07.01-9	7.08.07.01-9 Educação de Adultos	\N
4728	095	a	educacao	7.08.07.01-9 Educação de Adultos	\N
4729	095	a	de	7.08.07.01-9 Educação de Adultos	\N
4730	095	a	adultos	7.08.07.01-9 Educação de Adultos	\N
4731	095	a	7.08.07.02-7	7.08.07.02-7 Educação Permanente	\N
4732	095	a	educacao	7.08.07.02-7 Educação Permanente	\N
4733	095	a	permanente	7.08.07.02-7 Educação Permanente	\N
4734	095	a	7.08.07.03-5	7.08.07.03-5 Educação Rural	\N
4735	095	a	educacao	7.08.07.03-5 Educação Rural	\N
4736	095	a	rural	7.08.07.03-5 Educação Rural	\N
4737	095	a	7.08.07.04-3	7.08.07.04-3 Educação em Periferias Urbanas	\N
4738	095	a	educacao	7.08.07.04-3 Educação em Periferias Urbanas	\N
4739	095	a	em	7.08.07.04-3 Educação em Periferias Urbanas	\N
4740	095	a	periferias	7.08.07.04-3 Educação em Periferias Urbanas	\N
4741	095	a	urbanas	7.08.07.04-3 Educação em Periferias Urbanas	\N
4742	095	a	7.08.07.05-1	7.08.07.05-1 Educação Especial	\N
4743	095	a	educacao	7.08.07.05-1 Educação Especial	\N
4744	095	a	especial	7.08.07.05-1 Educação Especial	\N
4745	095	a	7.08.07.06-0	7.08.07.06-0 Educação Pré-Escolar	\N
4746	095	a	educacao	7.08.07.06-0 Educação Pré-Escolar	\N
4747	095	a	pre-escolar	7.08.07.06-0 Educação Pré-Escolar	\N
4748	095	a	7.08.07.07-8	7.08.07.07-8 Ensino Profissionalizante	\N
4749	095	a	ensino	7.08.07.07-8 Ensino Profissionalizante	\N
4750	095	a	profissionalizante	7.08.07.07-8 Ensino Profissionalizante	\N
4751	095	a	7.09.00.00-0	7.09.00.00-0 Ciência Política	\N
4752	095	a	ciencia	7.09.00.00-0 Ciência Política	\N
4753	095	a	politica	7.09.00.00-0 Ciência Política	\N
4754	095	a	7.09.01.00-7	7.09.01.00-7 Teoria Política	\N
4755	095	a	teoria	7.09.01.00-7 Teoria Política	\N
4756	095	a	politica	7.09.01.00-7 Teoria Política	\N
4757	095	a	7.09.01.01-5	7.09.01.01-5 Teoria Política Clássica	\N
4758	095	a	teoria	7.09.01.01-5 Teoria Política Clássica	\N
4759	095	a	politica	7.09.01.01-5 Teoria Política Clássica	\N
4760	095	a	classica	7.09.01.01-5 Teoria Política Clássica	\N
4761	095	a	7.09.01.02-3	7.09.01.02-3 Teoria Política Medieval	\N
4762	095	a	teoria	7.09.01.02-3 Teoria Política Medieval	\N
4763	095	a	politica	7.09.01.02-3 Teoria Política Medieval	\N
4764	095	a	medieval	7.09.01.02-3 Teoria Política Medieval	\N
4765	095	a	7.09.01.03-1	7.09.01.03-1 Teoria Política Moderna	\N
4766	095	a	teoria	7.09.01.03-1 Teoria Política Moderna	\N
4767	095	a	politica	7.09.01.03-1 Teoria Política Moderna	\N
4768	095	a	moderna	7.09.01.03-1 Teoria Política Moderna	\N
4769	095	a	7.09.01.04-0	7.09.01.04-0 Teoria Política Contemporânea	\N
4770	095	a	teoria	7.09.01.04-0 Teoria Política Contemporânea	\N
4771	095	a	politica	7.09.01.04-0 Teoria Política Contemporânea	\N
4772	095	a	contemporanea	7.09.01.04-0 Teoria Política Contemporânea	\N
4773	095	a	7.09.02.00-3	7.09.02.00-3 Estado e Governo	\N
4774	095	a	estado	7.09.02.00-3 Estado e Governo	\N
4775	095	a	governo	7.09.02.00-3 Estado e Governo	\N
4776	095	a	7.09.02.01-1	7.09.02.01-1 Estrutura e Transformação do Estado	\N
4777	095	a	estrutura	7.09.02.01-1 Estrutura e Transformação do Estado	\N
4778	095	a	transformacao	7.09.02.01-1 Estrutura e Transformação do Estado	\N
4779	095	a	do	7.09.02.01-1 Estrutura e Transformação do Estado	\N
4780	095	a	estado	7.09.02.01-1 Estrutura e Transformação do Estado	\N
4781	095	a	7.09.02.02-0	7.09.02.02-0 Sistemas Governamentais Comparados	\N
4782	095	a	sistemas	7.09.02.02-0 Sistemas Governamentais Comparados	\N
4783	095	a	governamentais	7.09.02.02-0 Sistemas Governamentais Comparados	\N
4784	095	a	comparados	7.09.02.02-0 Sistemas Governamentais Comparados	\N
4785	095	a	7.09.02.03-8	7.09.02.03-8 Relações Intergovernamentais	\N
4786	095	a	relacoes	7.09.02.03-8 Relações Intergovernamentais	\N
4787	095	a	intergovernamentais	7.09.02.03-8 Relações Intergovernamentais	\N
4788	095	a	7.09.02.04-6	7.09.02.04-6 Estudos do Poder Local	\N
4789	095	a	estudos	7.09.02.04-6 Estudos do Poder Local	\N
4790	095	a	do	7.09.02.04-6 Estudos do Poder Local	\N
4791	095	a	poder	7.09.02.04-6 Estudos do Poder Local	\N
4792	095	a	local	7.09.02.04-6 Estudos do Poder Local	\N
4793	095	a	7.09.02.05-4	7.09.02.05-4 Instituições Governamentais Específicas	\N
4794	095	a	instituicoes	7.09.02.05-4 Instituições Governamentais Específicas	\N
4795	095	a	governamentais	7.09.02.05-4 Instituições Governamentais Específicas	\N
4796	095	a	especificas	7.09.02.05-4 Instituições Governamentais Específicas	\N
4797	095	a	7.09.03.00-0	7.09.03.00-0 Comportamento Político	\N
4798	095	a	comportamento	7.09.03.00-0 Comportamento Político	\N
4799	095	a	politico	7.09.03.00-0 Comportamento Político	\N
4800	095	a	7.09.03.01-8	7.09.03.01-8 Estudos Eleitorais e Partidos Políticos	\N
4801	095	a	estudos	7.09.03.01-8 Estudos Eleitorais e Partidos Políticos	\N
4802	095	a	eleitorais	7.09.03.01-8 Estudos Eleitorais e Partidos Políticos	\N
4803	095	a	partidos	7.09.03.01-8 Estudos Eleitorais e Partidos Políticos	\N
4804	095	a	politicos	7.09.03.01-8 Estudos Eleitorais e Partidos Políticos	\N
4805	095	a	7.09.03.02-6	7.09.03.02-6 Atitude e Ideologias Políticas	\N
4806	095	a	atitude	7.09.03.02-6 Atitude e Ideologias Políticas	\N
4807	095	a	ideologias	7.09.03.02-6 Atitude e Ideologias Políticas	\N
4808	095	a	politicas	7.09.03.02-6 Atitude e Ideologias Políticas	\N
4809	095	a	7.09.03.03-4	7.09.03.03-4 Conflitos e Coalizões Políticas	\N
4810	095	a	conflitos	7.09.03.03-4 Conflitos e Coalizões Políticas	\N
4811	095	a	coalizoes	7.09.03.03-4 Conflitos e Coalizões Políticas	\N
4812	095	a	politicas	7.09.03.03-4 Conflitos e Coalizões Políticas	\N
4813	095	a	7.09.03.04-2	7.09.03.04-2 Comportamento Legislativo	\N
4814	095	a	comportamento	7.09.03.04-2 Comportamento Legislativo	\N
4815	095	a	legislativo	7.09.03.04-2 Comportamento Legislativo	\N
4816	095	a	7.09.03.05-0	7.09.03.05-0 Classes Sociais e Grupos de Interesse	\N
4817	095	a	classes	7.09.03.05-0 Classes Sociais e Grupos de Interesse	\N
4818	095	a	sociais	7.09.03.05-0 Classes Sociais e Grupos de Interesse	\N
4819	095	a	grupos	7.09.03.05-0 Classes Sociais e Grupos de Interesse	\N
4820	095	a	de	7.09.03.05-0 Classes Sociais e Grupos de Interesse	\N
4821	095	a	interesse	7.09.03.05-0 Classes Sociais e Grupos de Interesse	\N
4822	095	a	7.09.04.00-6	7.09.04.00-6 Políticas Públicas	\N
4823	095	a	politicas	7.09.04.00-6 Políticas Públicas	\N
4824	095	a	publicas	7.09.04.00-6 Políticas Públicas	\N
4825	095	a	7.09.04.01-4	7.09.04.01-4 Análise do Processo Decisório	\N
4826	095	a	analise	7.09.04.01-4 Análise do Processo Decisório	\N
4827	095	a	do	7.09.04.01-4 Análise do Processo Decisório	\N
4828	095	a	processo	7.09.04.01-4 Análise do Processo Decisório	\N
4829	095	a	decisorio	7.09.04.01-4 Análise do Processo Decisório	\N
4830	095	a	7.09.04.02-2	7.09.04.02-2 Análise Institucional	\N
4831	095	a	analise	7.09.04.02-2 Análise Institucional	\N
4832	095	a	institucional	7.09.04.02-2 Análise Institucional	\N
4833	095	a	7.09.04.03-0	7.09.04.03-0 Técnicas de Antecipação	\N
4834	095	a	tecnicas	7.09.04.03-0 Técnicas de Antecipação	\N
4835	095	a	de	7.09.04.03-0 Técnicas de Antecipação	\N
4836	095	a	antecipacao	7.09.04.03-0 Técnicas de Antecipação	\N
4837	095	a	7.09.05.00-2	7.09.05.00-2 Política Internacional	\N
4838	095	a	politica	7.09.05.00-2 Política Internacional	\N
4839	095	a	internacional	7.09.05.00-2 Política Internacional	\N
4840	095	a	7.09.05.01-0	7.09.05.01-0 Política Externa do Brasil	\N
4841	095	a	politica	7.09.05.01-0 Política Externa do Brasil	\N
4842	095	a	externa	7.09.05.01-0 Política Externa do Brasil	\N
4843	095	a	do	7.09.05.01-0 Política Externa do Brasil	\N
4844	095	a	brasil	7.09.05.01-0 Política Externa do Brasil	\N
4845	095	a	7.09.05.02-9	7.09.05.02-9 Organizações Internacionais	\N
4846	095	a	organizacoes	7.09.05.02-9 Organizações Internacionais	\N
4847	095	a	internacionais	7.09.05.02-9 Organizações Internacionais	\N
4848	095	a	7.09.05.03-7	7.09.05.03-7 Integração Internacional, Conflito, Guerra e Paz	\N
4849	095	a	integracao	7.09.05.03-7 Integração Internacional, Conflito, Guerra e Paz	\N
4850	095	a	internacional,	7.09.05.03-7 Integração Internacional, Conflito, Guerra e Paz	\N
4851	095	a	conflito,	7.09.05.03-7 Integração Internacional, Conflito, Guerra e Paz	\N
4852	095	a	guerra	7.09.05.03-7 Integração Internacional, Conflito, Guerra e Paz	\N
4853	095	a	paz	7.09.05.03-7 Integração Internacional, Conflito, Guerra e Paz	\N
4854	095	a	7.09.05.04-5	7.09.05.04-5 Relações Internacionais, Bilaterais e Multilaterais	\N
4855	095	a	relacoes	7.09.05.04-5 Relações Internacionais, Bilaterais e Multilaterais	\N
4856	095	a	internacionais,	7.09.05.04-5 Relações Internacionais, Bilaterais e Multilaterais	\N
4857	095	a	bilaterais	7.09.05.04-5 Relações Internacionais, Bilaterais e Multilaterais	\N
4858	095	a	multilaterais	7.09.05.04-5 Relações Internacionais, Bilaterais e Multilaterais	\N
4859	095	a	7.10.00.00-3	7.10.00.00-3 Teologia	\N
4860	095	a	teologia	7.10.00.00-3 Teologia	\N
4861	095	a	7.10.01.00-0	7.10.01.00-0 História da Teologia	\N
4862	095	a	historia	7.10.01.00-0 História da Teologia	\N
4863	095	a	da	7.10.01.00-0 História da Teologia	\N
4864	095	a	teologia	7.10.01.00-0 História da Teologia	\N
4865	095	a	7.10.02.00-6	7.10.02.00-6 Teologia Moral	\N
4866	095	a	teologia	7.10.02.00-6 Teologia Moral	\N
4867	095	a	moral	7.10.02.00-6 Teologia Moral	\N
4868	095	a	7.10.03.00-2	7.10.03.00-2 Teologia Sistemática	\N
4869	095	a	teologia	7.10.03.00-2 Teologia Sistemática	\N
4870	095	a	sistematica	7.10.03.00-2 Teologia Sistemática	\N
4871	095	a	7.10.04.00-9	7.10.04.00-9 Teologia Pastoral	\N
4872	095	a	teologia	7.10.04.00-9 Teologia Pastoral	\N
4873	095	a	pastoral	7.10.04.00-9 Teologia Pastoral	\N
4874	095	a	8.00.00.00-2	8.00.00.00-2 Lingüística, Letras e Artes	\N
4875	095	a	linguistica,	8.00.00.00-2 Lingüística, Letras e Artes	\N
4876	095	a	letras	8.00.00.00-2 Lingüística, Letras e Artes	\N
4877	095	a	artes	8.00.00.00-2 Lingüística, Letras e Artes	\N
4878	095	a	8.01.00.00-7	8.01.00.00-7 Lingüística	\N
4879	095	a	linguistica	8.01.00.00-7 Lingüística	\N
4880	095	a	8.01.01.00-3	8.01.01.00-3 Teoria e Análise Lingüística	\N
4881	095	a	teoria	8.01.01.00-3 Teoria e Análise Lingüística	\N
4882	095	a	analise	8.01.01.00-3 Teoria e Análise Lingüística	\N
4883	095	a	linguistica	8.01.01.00-3 Teoria e Análise Lingüística	\N
4884	095	a	8.01.02.00-0	8.01.02.00-0 Fisiologia da Linguagem	\N
4885	095	a	fisiologia	8.01.02.00-0 Fisiologia da Linguagem	\N
4886	095	a	da	8.01.02.00-0 Fisiologia da Linguagem	\N
4887	095	a	linguagem	8.01.02.00-0 Fisiologia da Linguagem	\N
4888	095	a	8.01.03.00-6	8.01.03.00-6 Lingüística Histórica	\N
4889	095	a	linguistica	8.01.03.00-6 Lingüística Histórica	\N
4890	095	a	historica	8.01.03.00-6 Lingüística Histórica	\N
4891	095	a	8.01.04.00-2	8.01.04.00-2 Sociolingüística e Dialetologia	\N
4892	095	a	sociolinguistica	8.01.04.00-2 Sociolingüística e Dialetologia	\N
4893	095	a	dialetologia	8.01.04.00-2 Sociolingüística e Dialetologia	\N
4894	095	a	8.01.05.00-9	8.01.05.00-9 Psicolingüística	\N
4895	095	a	psicolinguistica	8.01.05.00-9 Psicolingüística	\N
4896	095	a	8.01.06.00-5	8.01.06.00-5 Lingüística Aplicada	\N
4897	095	a	linguistica	8.01.06.00-5 Lingüística Aplicada	\N
4898	095	a	aplicada	8.01.06.00-5 Lingüística Aplicada	\N
4899	095	a	8.02.00.00-1	8.02.00.00-1 Letras	\N
4900	095	a	letras	8.02.00.00-1 Letras	\N
4901	095	a	8.02.01.00-8	8.02.01.00-8 Língua Portuguesa	\N
4902	095	a	lingua	8.02.01.00-8 Língua Portuguesa	\N
4903	095	a	portuguesa	8.02.01.00-8 Língua Portuguesa	\N
4904	095	a	8.02.02.00-4	8.02.02.00-4 Línguas Estrangeiras Modernas	\N
4905	095	a	linguas	8.02.02.00-4 Línguas Estrangeiras Modernas	\N
4906	095	a	estrangeiras	8.02.02.00-4 Línguas Estrangeiras Modernas	\N
4907	095	a	modernas	8.02.02.00-4 Línguas Estrangeiras Modernas	\N
4908	095	a	8.02.03.00-0	8.02.03.00-0 Línguas Clássicas	\N
4909	095	a	linguas	8.02.03.00-0 Línguas Clássicas	\N
4910	095	a	classicas	8.02.03.00-0 Línguas Clássicas	\N
4911	095	a	8.02.04.00-7	8.02.04.00-7 Línguas Indígenas	\N
4912	095	a	linguas	8.02.04.00-7 Línguas Indígenas	\N
4913	095	a	indigenas	8.02.04.00-7 Línguas Indígenas	\N
4914	095	a	8.02.05.00-3	8.02.05.00-3 Teoria Literária	\N
4915	095	a	teoria	8.02.05.00-3 Teoria Literária	\N
4916	095	a	literaria	8.02.05.00-3 Teoria Literária	\N
4917	095	a	8.02.06.00-0	8.02.06.00-0 Literatura Brasileira	\N
4918	095	a	literatura	8.02.06.00-0 Literatura Brasileira	\N
4919	095	a	brasileira	8.02.06.00-0 Literatura Brasileira	\N
4920	095	a	8.02.07.00-6	8.02.07.00-6 Outras Literaturas Vernáculas	\N
4921	095	a	outras	8.02.07.00-6 Outras Literaturas Vernáculas	\N
4922	095	a	literaturas	8.02.07.00-6 Outras Literaturas Vernáculas	\N
4923	095	a	vernaculas	8.02.07.00-6 Outras Literaturas Vernáculas	\N
4924	095	a	8.02.08.00-2	8.02.08.00-2 Literaturas Estrangeiras Modernas	\N
4925	095	a	literaturas	8.02.08.00-2 Literaturas Estrangeiras Modernas	\N
4926	095	a	estrangeiras	8.02.08.00-2 Literaturas Estrangeiras Modernas	\N
4927	095	a	modernas	8.02.08.00-2 Literaturas Estrangeiras Modernas	\N
4928	095	a	8.02.09.00-9	8.02.09.00-9 Literaturas Clássicas	\N
4929	095	a	literaturas	8.02.09.00-9 Literaturas Clássicas	\N
4930	095	a	classicas	8.02.09.00-9 Literaturas Clássicas	\N
4931	095	a	8.02.10.00-7	8.02.10.00-7 Literatura Comparada	\N
4932	095	a	literatura	8.02.10.00-7 Literatura Comparada	\N
4933	095	a	comparada	8.02.10.00-7 Literatura Comparada	\N
4934	095	a	8.03.00.00-6	8.03.00.00-6 Artes	\N
4935	095	a	artes	8.03.00.00-6 Artes	\N
4936	095	a	8.03.01.00-2	8.03.01.00-2 Fundamentos e Crítica das Artes	\N
4937	095	a	fundamentos	8.03.01.00-2 Fundamentos e Crítica das Artes	\N
4938	095	a	critica	8.03.01.00-2 Fundamentos e Crítica das Artes	\N
4939	095	a	das	8.03.01.00-2 Fundamentos e Crítica das Artes	\N
4940	095	a	artes	8.03.01.00-2 Fundamentos e Crítica das Artes	\N
4941	095	a	8.03.01.01-0	8.03.01.01-0 Teoria da Arte	\N
4942	095	a	teoria	8.03.01.01-0 Teoria da Arte	\N
4943	095	a	da	8.03.01.01-0 Teoria da Arte	\N
4944	095	a	arte	8.03.01.01-0 Teoria da Arte	\N
4945	095	a	8.03.01.02-9	8.03.01.02-9 História da Arte	\N
4946	095	a	historia	8.03.01.02-9 História da Arte	\N
4947	095	a	da	8.03.01.02-9 História da Arte	\N
4948	095	a	arte	8.03.01.02-9 História da Arte	\N
4949	095	a	8.03.01.03-7	8.03.01.03-7 Crítica da Arte	\N
4950	095	a	critica	8.03.01.03-7 Crítica da Arte	\N
4951	095	a	da	8.03.01.03-7 Crítica da Arte	\N
4952	095	a	arte	8.03.01.03-7 Crítica da Arte	\N
4953	095	a	8.03.02.00-9	8.03.02.00-9 Artes Plásticas	\N
4954	095	a	artes	8.03.02.00-9 Artes Plásticas	\N
4955	095	a	plasticas	8.03.02.00-9 Artes Plásticas	\N
4956	095	a	8.03.02.01-7	8.03.02.01-7 Pintura	\N
4957	095	a	pintura	8.03.02.01-7 Pintura	\N
4958	095	a	8.03.02.02-5	8.03.02.02-5 Desenho	\N
4959	095	a	desenho	8.03.02.02-5 Desenho	\N
4960	095	a	8.03.02.03-3	8.03.02.03-3 Gravura	\N
4961	095	a	gravura	8.03.02.03-3 Gravura	\N
4962	095	a	8.03.02.04-1	8.03.02.04-1 Escultura	\N
4963	095	a	escultura	8.03.02.04-1 Escultura	\N
4964	095	a	8.03.02.05-0	8.03.02.05-0 Cerâmica	\N
4965	095	a	ceramica	8.03.02.05-0 Cerâmica	\N
4966	095	a	8.03.02.06-8	8.03.02.06-8 Tecelagem	\N
4967	095	a	tecelagem	8.03.02.06-8 Tecelagem	\N
4968	095	a	8.03.03.00-5	8.03.03.00-5 Música	\N
4970	095	a	8.03.03.01-3	8.03.03.01-3 Regência	\N
4971	095	a	regencia	8.03.03.01-3 Regência	\N
4972	095	a	8.03.03.02-1	8.03.03.02-1 Instrumentação Musical	\N
4973	095	a	instrumentacao	8.03.03.02-1 Instrumentação Musical	\N
4974	095	a	musical	8.03.03.02-1 Instrumentação Musical	\N
4975	095	a	8.03.03.03-0	8.03.03.03-0 Composição Musical	\N
4976	095	a	composicao	8.03.03.03-0 Composição Musical	\N
4977	095	a	musical	8.03.03.03-0 Composição Musical	\N
4978	095	a	8.03.03.04-8	8.03.03.04-8 Canto	\N
4979	095	a	canto	8.03.03.04-8 Canto	\N
4980	095	a	8.03.04.00-1	8.03.04.00-1 Dança	\N
4981	095	a	danca	8.03.04.00-1 Dança	\N
4982	095	a	8.03.04.01-0	8.03.04.01-0 Execução da Dança	\N
4983	095	a	execucao	8.03.04.01-0 Execução da Dança	\N
4984	095	a	da	8.03.04.01-0 Execução da Dança	\N
4985	095	a	danca	8.03.04.01-0 Execução da Dança	\N
4986	095	a	8.03.04.02-8	8.03.04.02-8 Coreografia	\N
4987	095	a	coreografia	8.03.04.02-8 Coreografia	\N
4988	095	a	8.03.05.00-8	8.03.05.00-8 Teatro	\N
4989	095	a	teatro	8.03.05.00-8 Teatro	\N
4990	095	a	8.03.05.01-6	8.03.05.01-6 Dramaturgia	\N
4991	095	a	dramaturgia	8.03.05.01-6 Dramaturgia	\N
4992	095	a	8.03.05.02-4	8.03.05.02-4 Direção Teatral	\N
4993	095	a	direcao	8.03.05.02-4 Direção Teatral	\N
4994	095	a	teatral	8.03.05.02-4 Direção Teatral	\N
4995	095	a	8.03.05.03-2	8.03.05.03-2 Cenografia	\N
4996	095	a	cenografia	8.03.05.03-2 Cenografia	\N
4997	095	a	8.03.05.04-0	8.03.05.04-0 Interpretação Teatral	\N
4998	095	a	interpretacao	8.03.05.04-0 Interpretação Teatral	\N
4999	095	a	teatral	8.03.05.04-0 Interpretação Teatral	\N
5000	095	a	8.03.06.00-4	8.03.06.00-4 Ópera	\N
5001	095	a	opera	8.03.06.00-4 Ópera	\N
5002	095	a	8.03.07.00-0	8.03.07.00-0 Fotografia	\N
5003	095	a	fotografia	8.03.07.00-0 Fotografia	\N
5004	095	a	8.03.08.00-7	8.03.08.00-7 Cinema	\N
5005	095	a	cinema	8.03.08.00-7 Cinema	\N
5006	095	a	8.03.08.01-5	8.03.08.01-5 Administração e Produção de Filmes	\N
5007	095	a	administracao	8.03.08.01-5 Administração e Produção de Filmes	\N
5008	095	a	producao	8.03.08.01-5 Administração e Produção de Filmes	\N
5009	095	a	de	8.03.08.01-5 Administração e Produção de Filmes	\N
5010	095	a	filmes	8.03.08.01-5 Administração e Produção de Filmes	\N
5011	095	a	8.03.08.02-3	8.03.08.02-3 Roteiro e Direção Cinematográficos	\N
5012	095	a	roteiro	8.03.08.02-3 Roteiro e Direção Cinematográficos	\N
5013	095	a	direcao	8.03.08.02-3 Roteiro e Direção Cinematográficos	\N
5014	095	a	cinematograficos	8.03.08.02-3 Roteiro e Direção Cinematográficos	\N
5015	095	a	8.03.08.03-1	8.03.08.03-1 Técnicas de Registro e Processamento de Filmes	\N
5016	095	a	tecnicas	8.03.08.03-1 Técnicas de Registro e Processamento de Filmes	\N
5017	095	a	de	8.03.08.03-1 Técnicas de Registro e Processamento de Filmes	\N
5018	095	a	registro	8.03.08.03-1 Técnicas de Registro e Processamento de Filmes	\N
5019	095	a	processamento	8.03.08.03-1 Técnicas de Registro e Processamento de Filmes	\N
5020	095	a	de	8.03.08.03-1 Técnicas de Registro e Processamento de Filmes	\N
5021	095	a	filmes	8.03.08.03-1 Técnicas de Registro e Processamento de Filmes	\N
5022	095	a	8.03.08.04-0	8.03.08.04-0 Interpretação Cinematográfica	\N
5023	095	a	interpretacao	8.03.08.04-0 Interpretação Cinematográfica	\N
5024	095	a	cinematografica	8.03.08.04-0 Interpretação Cinematográfica	\N
5025	095	a	8.03.09.00-3	8.03.09.00-3 Artes do Vídeo	\N
5026	095	a	artes	8.03.09.00-3 Artes do Vídeo	\N
5027	095	a	do	8.03.09.00-3 Artes do Vídeo	\N
5028	095	a	video	8.03.09.00-3 Artes do Vídeo	\N
5029	095	a	8.03.10.00-1	8.03.10.00-1 Educação Artística	\N
5030	095	a	educacao	8.03.10.00-1 Educação Artística	\N
5031	095	a	artistica	8.03.10.00-1 Educação Artística	\N
5032	095	a	9.00.00.00-5	9.00.00.00-5 Outros	\N
5033	095	a	outros	9.00.00.00-5 Outros	\N
5034	095	a	9.01.00.00-0	9.01.00.00-0 Administração Hospitalar	\N
5035	095	a	administracao	9.01.00.00-0 Administração Hospitalar	\N
5036	095	a	hospitalar	9.01.00.00-0 Administração Hospitalar	\N
5037	095	a	9.02.00.00-4	9.02.00.00-4 Administração Rural	\N
5038	095	a	administracao	9.02.00.00-4 Administração Rural	\N
5039	095	a	rural	9.02.00.00-4 Administração Rural	\N
5040	095	a	9.03.00.00-9	9.03.00.00-9 Carreira Militar	\N
5041	095	a	carreira	9.03.00.00-9 Carreira Militar	\N
5042	095	a	militar	9.03.00.00-9 Carreira Militar	\N
5043	095	a	9.04.00.00-3	9.04.00.00-3 Carreira Religiosa	\N
5044	095	a	carreira	9.04.00.00-3 Carreira Religiosa	\N
5045	095	a	religiosa	9.04.00.00-3 Carreira Religiosa	\N
5046	095	a	9.05.00.00-8	9.05.00.00-8 Ciências	\N
5047	095	a	ciencias	9.05.00.00-8 Ciências	\N
5048	095	a	9.06.00.00-2	9.06.00.00-2 Biomedicina	\N
5049	095	a	biomedicina	9.06.00.00-2 Biomedicina	\N
5050	095	a	9.07.00.00-7	9.07.00.00-7 Ciências Atuariais	\N
5051	095	a	ciencias	9.07.00.00-7 Ciências Atuariais	\N
5052	095	a	atuariais	9.07.00.00-7 Ciências Atuariais	\N
5053	095	a	9.08.00.00-1	9.08.00.00-1 Ciências Sociais	\N
5054	095	a	ciencias	9.08.00.00-1 Ciências Sociais	\N
5055	095	a	sociais	9.08.00.00-1 Ciências Sociais	\N
5056	095	a	9.09.00.00-6	9.09.00.00-6 Decoração	\N
5057	095	a	decoracao	9.09.00.00-6 Decoração	\N
5058	095	a	9.10.00.00-9	9.10.00.00-9 Desenho de Moda	\N
5059	095	a	desenho	9.10.00.00-9 Desenho de Moda	\N
5060	095	a	de	9.10.00.00-9 Desenho de Moda	\N
5061	095	a	moda	9.10.00.00-9 Desenho de Moda	\N
5062	095	a	9.11.00.00-3	9.11.00.00-3 Desenho de Projetos	\N
5063	095	a	desenho	9.11.00.00-3 Desenho de Projetos	\N
5064	095	a	de	9.11.00.00-3 Desenho de Projetos	\N
5065	095	a	projetos	9.11.00.00-3 Desenho de Projetos	\N
5066	095	a	9.12.00.00-8	9.12.00.00-8 Diplomacia	\N
5067	095	a	diplomacia	9.12.00.00-8 Diplomacia	\N
5068	095	a	9.13.00.00-2	9.13.00.00-2 Engenharia de Agrimensura	\N
5069	095	a	engenharia	9.13.00.00-2 Engenharia de Agrimensura	\N
5070	095	a	de	9.13.00.00-2 Engenharia de Agrimensura	\N
5071	095	a	agrimensura	9.13.00.00-2 Engenharia de Agrimensura	\N
5072	095	a	9.14.00.00-7	9.14.00.00-7 Engenharia Cartográfica	\N
5073	095	a	engenharia	9.14.00.00-7 Engenharia Cartográfica	\N
5074	095	a	cartografica	9.14.00.00-7 Engenharia Cartográfica	\N
5075	095	a	9.15.00.00-1	9.15.00.00-1 Engenharia de Armamentos	\N
5076	095	a	engenharia	9.15.00.00-1 Engenharia de Armamentos	\N
5077	095	a	de	9.15.00.00-1 Engenharia de Armamentos	\N
5078	095	a	armamentos	9.15.00.00-1 Engenharia de Armamentos	\N
5079	095	a	9.16.00.00-6	9.16.00.00-6 Engenharia Mecatrônica	\N
5080	095	a	engenharia	9.16.00.00-6 Engenharia Mecatrônica	\N
5081	095	a	mecatronica	9.16.00.00-6 Engenharia Mecatrônica	\N
5082	095	a	9.17.00.00-0	9.17.00.00-0 Engenharia Têxtil	\N
5083	095	a	engenharia	9.17.00.00-0 Engenharia Têxtil	\N
5084	095	a	textil	9.17.00.00-0 Engenharia Têxtil	\N
5085	095	a	9.18.00.00-5	9.18.00.00-5 Estudos Sociais	\N
5086	095	a	estudos	9.18.00.00-5 Estudos Sociais	\N
5087	095	a	sociais	9.18.00.00-5 Estudos Sociais	\N
5088	095	a	9.19.00.00-0	9.19.00.00-0 História Natural	\N
5089	095	a	historia	9.19.00.00-0 História Natural	\N
5090	095	a	natural	9.19.00.00-0 História Natural	\N
5091	095	a	9.20.00.00-2	9.20.00.00-2 Química Industrial	\N
5092	095	a	quimica	9.20.00.00-2 Química Industrial	\N
5093	095	a	industrial	9.20.00.00-2 Química Industrial	\N
5094	095	a	9.21.00.00-7	9.21.00.00-7 Relações Internacionais	\N
5095	095	a	relacoes	9.21.00.00-7 Relações Internacionais	\N
5096	095	a	internacionais	9.21.00.00-7 Relações Internacionais	\N
5097	095	a	9.22.00.00-1	9.22.00.00-1 Relações Publicas	\N
5098	095	a	relacoes	9.22.00.00-1 Relações Publicas	\N
5099	095	a	publicas	9.22.00.00-1 Relações Publicas	\N
5100	095	a	9.23.00.00-6	9.23.00.00-6 Secretariado Executivo	\N
5101	095	a	secretariado	9.23.00.00-6 Secretariado Executivo	\N
5102	095	a	executivo	9.23.00.00-6 Secretariado Executivo	\N
\.


--
-- Data for Name: biblio_idx_fields; Type: TABLE DATA; Schema: bib4template; Owner: biblivre
--

COPY bib4template.biblio_idx_fields (record_id, indexing_group_id, word, datafield) FROM stdin;
\.


--
-- Data for Name: biblio_idx_sort; Type: TABLE DATA; Schema: bib4template; Owner: biblivre
--

COPY bib4template.biblio_idx_sort (record_id, indexing_group_id, phrase, ignore_chars_count) FROM stdin;
0	1		0
\.


--
-- Data for Name: biblio_indexing_groups; Type: TABLE DATA; Schema: bib4template; Owner: biblivre
--

COPY bib4template.biblio_indexing_groups (id, translation_key, datafields, sortable, default_sort, created, created_by, modified, modified_by) FROM stdin;
4	subject	650_a_x_y_z,600_a_x_y_z,610_a_x_y_z,611_a_x_y_z,630_a_x_y_z,651_a_x_y_z,699_a_x_y_z	f	f	2013-04-13 13:45:00.977717	\N	2013-04-13 13:45:00.977717	\N
5	isbn	020_a	f	f	2013-04-13 13:45:00.977717	\N	2013-04-13 13:45:00.977717	\N
6	issn	022_a	f	f	2013-04-13 13:45:00.977717	\N	2013-04-13 13:45:00.977717	\N
1	author	100_a,110_a,111_a,700_a,710_a,711_a	t	f	2013-04-13 13:45:00.977717	\N	2013-04-13 13:45:00.977717	\N
0	all	\N	f	f	2013-04-13 13:45:00.977717	\N	2013-04-13 13:45:00.977717	\N
2	year	260_c	t	f	2013-04-13 13:45:00.977717	\N	2013-04-13 13:45:00.977717	\N
7	publisher	260_b	t	f	2022-12-04 11:05:57.138539	\N	2022-12-04 11:05:57.138539	\N
8	series	490_a	t	f	2022-12-04 11:05:57.138539	\N	2022-12-04 11:05:57.138539	\N
3	title	245_a_b,243_a_f,240_a,730_a,740_a_n_p,830_a_v,250_a,130_a	t	t	2013-04-13 13:45:00.977717	\N	2013-04-13 13:45:00.977717	\N
\.


--
-- Data for Name: biblio_records; Type: TABLE DATA; Schema: bib4template; Owner: biblivre
--

COPY bib4template.biblio_records (id, iso2709, material, database, created, created_by, modified, modified_by) FROM stdin;
\.


--
-- Data for Name: biblio_search_results; Type: TABLE DATA; Schema: bib4template; Owner: biblivre
--

COPY bib4template.biblio_search_results (search_id, indexing_group_id, record_id) FROM stdin;
\.


--
-- Data for Name: biblio_searches; Type: TABLE DATA; Schema: bib4template; Owner: biblivre
--

COPY bib4template.biblio_searches (id, parameters, created, created_by) FROM stdin;
\.


--
-- Data for Name: configurations; Type: TABLE DATA; Schema: bib4template; Owner: biblivre
--

COPY bib4template.configurations (key, value, type, required, modified, modified_by) FROM stdin;
setup.new_library	false	boolean	f	2022-12-04 11:06:02.505131	0
\.


--
-- Data for Name: digital_media; Type: TABLE DATA; Schema: bib4template; Owner: biblivre
--

COPY bib4template.digital_media (id, name, blob, content_type, size, created, created_by) FROM stdin;
\.


--
-- Data for Name: holding_creation_counter; Type: TABLE DATA; Schema: bib4template; Owner: biblivre
--

COPY bib4template.holding_creation_counter (id, user_name, user_login, created, created_by) FROM stdin;
\.


--
-- Data for Name: holding_form_datafields; Type: TABLE DATA; Schema: bib4template; Owner: biblivre
--

COPY bib4template.holding_form_datafields (datafield, collapsed, repeatable, indicator_1, indicator_2, material_type, created, created_by, modified, modified_by, sort_order) FROM stdin;
949	f	f			holdings	2014-02-08 15:07:07.222864	\N	2014-02-08 15:07:07.222864	\N	949
541	f	t	_,0,1		holdings	2014-02-08 15:07:07.222864	\N	2014-02-08 15:07:07.222864	\N	541
090	f	f			holdings	2014-02-08 15:07:07.222864	\N	2014-02-08 15:07:07.222864	\N	90
852	f	t	_,0,1,2,4,5,6,8	0,1,2	holdings	2014-02-08 15:07:07.222864	\N	2014-02-08 15:07:07.222864	\N	852
856	f	t			holdings	2014-02-08 15:07:07.222864	\N	2014-02-08 15:07:07.222864	\N	856
\.


--
-- Data for Name: holding_form_subfields; Type: TABLE DATA; Schema: bib4template; Owner: biblivre
--

COPY bib4template.holding_form_subfields (datafield, subfield, collapsed, repeatable, created, created_by, modified, modified_by, autocomplete_type, sort_order) FROM stdin;
949	a	f	f	2014-02-08 15:10:19.847594	\N	2014-02-08 15:10:19.847594	\N	disabled	1046
541	a	f	f	2014-02-08 15:10:19.847594	\N	2014-02-08 15:10:19.847594	\N	disabled	638
541	b	f	f	2014-02-08 15:10:19.847594	\N	2014-02-08 15:10:19.847594	\N	disabled	639
541	c	f	f	2014-02-08 15:10:19.847594	\N	2014-02-08 15:10:19.847594	\N	disabled	640
541	d	f	f	2014-02-08 15:10:19.847594	\N	2014-02-08 15:10:19.847594	\N	disabled	641
541	e	f	t	2014-02-08 15:10:19.847594	\N	2014-02-08 15:10:19.847594	\N	disabled	642
541	f	f	t	2014-02-08 15:10:19.847594	\N	2014-02-08 15:10:19.847594	\N	disabled	643
541	h	f	t	2014-02-08 15:10:19.847594	\N	2014-02-08 15:10:19.847594	\N	disabled	645
541	n	f	t	2014-02-08 15:10:19.847594	\N	2014-02-08 15:10:19.847594	\N	disabled	651
541	o	f	t	2014-02-08 15:10:19.847594	\N	2014-02-08 15:10:19.847594	\N	disabled	652
541	3	f	t	2014-02-08 15:10:19.847594	\N	2014-02-08 15:10:19.847594	\N	disabled	592
090	a	f	f	2014-02-08 15:10:19.847594	\N	2014-02-08 15:10:19.847594	\N	disabled	187
090	b	f	f	2014-02-08 15:10:19.847594	\N	2014-02-08 15:10:19.847594	\N	disabled	188
090	c	f	f	2014-02-08 15:10:19.847594	\N	2014-02-08 15:10:19.847594	\N	disabled	189
090	d	f	f	2014-02-08 15:10:19.847594	\N	2014-02-08 15:10:19.847594	\N	disabled	190
852	a	f	f	2014-02-08 15:10:19.847594	\N	2014-02-08 15:10:19.847594	\N	disabled	949
852	b	f	t	2014-02-08 15:10:19.847594	\N	2014-02-08 15:10:19.847594	\N	disabled	950
852	c	f	t	2014-02-08 15:10:19.847594	\N	2014-02-08 15:10:19.847594	\N	disabled	951
852	e	f	t	2014-02-08 15:10:19.847594	\N	2014-02-08 15:10:19.847594	\N	disabled	953
852	j	f	f	2014-02-08 15:10:19.847594	\N	2014-02-08 15:10:19.847594	\N	disabled	958
852	n	f	t	2014-02-08 15:10:19.847594	\N	2014-02-08 15:10:19.847594	\N	disabled	962
852	q	f	f	2014-02-08 15:10:19.847594	\N	2014-02-08 15:10:19.847594	\N	disabled	965
852	x	f	t	2014-02-08 15:10:19.847594	\N	2014-02-08 15:10:19.847594	\N	disabled	972
852	z	f	t	2014-02-08 15:10:19.847594	\N	2014-02-08 15:10:19.847594	\N	disabled	974
856	d	f	t	2014-02-08 15:10:19.847594	\N	2014-02-08 15:10:19.847594	\N	disabled	956
856	f	f	t	2014-02-08 15:10:19.847594	\N	2014-02-08 15:10:19.847594	\N	disabled	958
856	u	f	t	2014-02-08 15:10:19.847594	\N	2014-02-08 15:10:19.847594	\N	disabled	973
856	y	f	t	2014-02-08 15:10:19.847594	\N	2014-02-08 15:10:19.847594	\N	disabled	977
\.


--
-- Data for Name: lending_fines; Type: TABLE DATA; Schema: bib4template; Owner: biblivre
--

COPY bib4template.lending_fines (id, lending_id, user_id, fine_value, payment_date, created, created_by) FROM stdin;
\.


--
-- Data for Name: lendings; Type: TABLE DATA; Schema: bib4template; Owner: biblivre
--

COPY bib4template.lendings (id, holding_id, user_id, previous_lending_id, expected_return_date, return_date, created, created_by) FROM stdin;
\.


--
-- Data for Name: logins; Type: TABLE DATA; Schema: bib4template; Owner: biblivre
--

COPY bib4template.logins (id, login, employee, password, created, created_by, modified, modified_by) FROM stdin;
1	admin	t	C4wx3TpMHnSwdk1bUQ/V6qwAQmw=	2014-05-18 15:46:31.632	1	2014-05-18 15:46:31.632	\N
\.


--
-- Data for Name: orders; Type: TABLE DATA; Schema: bib4template; Owner: biblivre
--

COPY bib4template.orders (id, info, status, invoice_number, receipt_date, total_value, delivered_quantity, terms_of_payment, deadline_date, created, created_by, modified, modified_by, quotation_id) FROM stdin;
\.


--
-- Data for Name: permissions; Type: TABLE DATA; Schema: bib4template; Owner: biblivre
--

COPY bib4template.permissions (login_id, permission) FROM stdin;
\.


--
-- Data for Name: quotations; Type: TABLE DATA; Schema: bib4template; Owner: biblivre
--

COPY bib4template.quotations (id, supplier_id, response_date, expiration_date, delivery_time, info, created, created_by, modified, modified_by) FROM stdin;
\.


--
-- Data for Name: request_quotation; Type: TABLE DATA; Schema: bib4template; Owner: biblivre
--

COPY bib4template.request_quotation (request_id, quotation_id, quotation_quantity, unit_value, response_quantity) FROM stdin;
\.


--
-- Data for Name: requests; Type: TABLE DATA; Schema: bib4template; Owner: biblivre
--

COPY bib4template.requests (id, requester, author, item_title, item_subtitle, edition_number, publisher, info, status, quantity, created, created_by, modified, modified_by) FROM stdin;
\.


--
-- Data for Name: reservations; Type: TABLE DATA; Schema: bib4template; Owner: biblivre
--

COPY bib4template.reservations (id, record_id, user_id, expires, created, created_by) FROM stdin;
\.


--
-- Data for Name: suppliers; Type: TABLE DATA; Schema: bib4template; Owner: biblivre
--

COPY bib4template.suppliers (id, trademark, supplier_name, supplier_number, vat_registration_number, address, address_number, address_complement, area, city, state, country, zip_code, telephone_1, telephone_2, telephone_3, telephone_4, contact_1, contact_2, contact_3, contact_4, info, url, email, created, created_by, modified, modified_by) FROM stdin;
\.


--
-- Data for Name: translations; Type: TABLE DATA; Schema: bib4template; Owner: biblivre
--

COPY bib4template.translations (language, key, text, created, created_by, modified, modified_by, user_created) FROM stdin;
\.


--
-- Data for Name: users; Type: TABLE DATA; Schema: bib4template; Owner: biblivre
--

COPY bib4template.users (id, name, type, photo_id, status, login_id, created, created_by, modified, modified_by, user_card_printed, name_ascii) FROM stdin;
\.


--
-- Data for Name: users_fields; Type: TABLE DATA; Schema: bib4template; Owner: biblivre
--

COPY bib4template.users_fields (key, type, required, max_length, sort_order, created, created_by, modified, modified_by) FROM stdin;
email	string	t	0	1	2013-04-13 13:47:34.765388	\N	2013-04-13 13:47:34.765388	\N
gender	list	f	2	2	2014-06-07 12:51:14.565458	\N	2014-06-07 12:51:14.565458	\N
phone_cel	string	f	25	3	2014-06-07 12:47:50.811875	\N	2014-06-07 12:47:50.811875	\N
phone_home	string	f	25	4	2014-06-07 12:47:33.283702	\N	2014-06-07 12:47:33.283702	\N
phone_work	string	f	25	5	2014-06-07 12:47:42.779511	\N	2014-06-07 12:47:42.779511	\N
obs	text	f	0	1002	2013-04-13 13:47:34.765388	\N	2013-04-13 13:47:34.765388	\N
id_cpf	string	f	20	8	2014-06-07 12:46:47.409991	\N	2014-06-07 12:46:47.409991	\N
address	string	f	500	9	2014-06-07 12:41:23.221671	\N	2014-06-07 12:41:23.221671	\N
address_number	string	f	100	10	2014-06-07 12:42:30.610671	\N	2014-06-07 12:42:30.610671	\N
address_complement	string	f	100	11	2014-06-07 12:44:27.624027	\N	2014-06-07 12:44:27.624027	\N
address_zip	string	f	20	12	2014-06-07 12:45:05.425222	\N	2014-06-07 12:45:05.425222	\N
address_city	string	f	100	13	2014-06-07 12:45:21.458004	\N	2014-06-07 12:45:21.458004	\N
address_state	string	f	100	14	2014-06-07 12:45:31.657995	\N	2014-06-07 12:45:31.657995	\N
birthday	date	f	0	15	2014-06-07 12:50:08.933974	\N	2014-06-07 12:50:08.933974	\N
id_rg	string	f	20	7	2014-06-07 12:46:30.386262	\N	2014-06-07 12:46:30.386262	\N
phone_work_extension	string	f	10	6	2014-06-07 12:53:42.743594	\N	2014-06-07 12:53:42.743594	\N
\.


--
-- Data for Name: users_types; Type: TABLE DATA; Schema: bib4template; Owner: biblivre
--

COPY bib4template.users_types (id, name, description, lending_limit, reservation_limit, lending_time_limit, reservation_time_limit, fine_value, created, created_by, modified, modified_by) FROM stdin;
1	Leitor	Leitores	3	3	15	10	0	2014-05-18 15:46:31.379	1	2014-05-18 15:46:31.379	\N
2	Funcionário	Funcionários	99	99	365	365	0	2014-05-18 15:46:31.379	1	2014-05-18 15:46:31.379	\N
\.


--
-- Data for Name: users_values; Type: TABLE DATA; Schema: bib4template; Owner: biblivre
--

COPY bib4template.users_values (user_id, key, value, ascii) FROM stdin;
\.


--
-- Data for Name: versions; Type: TABLE DATA; Schema: bib4template; Owner: biblivre
--

COPY bib4template.versions (installed_versions) FROM stdin;
4.0.0b
4.0.1b
4.0.2b
4.0.3b
4.0.4b
4.0.5b
4.0.6b
4.0.7b
4.0.8b
4.0.9b
4.0.10b
4.0.11b
4.0.12b
4.1.0
4.1.1
4.1.2
4.1.3
4.1.4
4.1.5
4.1.6
4.1.7
4.1.8
4.1.9
4.1.10
4.1.10a
4.1.11
4.1.11a
5.0.0
5.0.1
5.0.1b
6.0.0-1.0.0-alpha
6.0.0-1.0.1-alpha
6.0.0-1.0.2-alpha
v6_0_0$1_1_0$alpha
\.


--
-- Data for Name: vocabulary_brief_formats; Type: TABLE DATA; Schema: bib4template; Owner: biblivre
--

COPY bib4template.vocabulary_brief_formats (datafield, format, sort_order, created, created_by, modified, modified_by) FROM stdin;
040	${a}_{- }${b}	1	2014-03-20 12:26:27.691	\N	2014-03-20 12:26:27.691	\N
150	${a}_{- }${i}_{; }${x}	2	2014-03-20 12:28:43.529	\N	2014-03-20 12:28:43.529	\N
450	${a}_{; }${x}	4	2014-03-20 12:29:58.601	\N	2014-03-20 12:29:58.601	\N
550	${a}_{; }${x}	5	2014-03-20 12:30:37.837	\N	2014-03-20 12:30:37.837	\N
670	${a}	6	2014-03-20 12:30:52.156	\N	2014-03-20 12:30:52.156	\N
680	${a}	7	2014-03-20 12:31:13.64	\N	2014-03-20 12:31:13.64	\N
685	${a}	8	2014-03-20 12:31:24.135	\N	2014-03-20 12:31:24.135	\N
750	${a}_{; }${x}_{; }${y}_{; }${z}	9	2014-03-20 12:32:37.881	\N	2014-03-20 12:32:37.881	\N
913	${a}	10	2014-03-20 12:32:57.598	\N	2014-03-20 12:32:57.598	\N
360	${a}_{; }${x}	3	2014-03-20 13:03:12.684	\N	2014-03-20 13:03:12.684	\N
\.


--
-- Data for Name: vocabulary_form_datafields; Type: TABLE DATA; Schema: bib4template; Owner: biblivre
--

COPY bib4template.vocabulary_form_datafields (datafield, collapsed, repeatable, indicator_1, indicator_2, material_type, created, created_by, modified, modified_by, sort_order) FROM stdin;
040	f	f				2014-02-08 15:29:41.844864	\N	2014-02-08 15:29:41.844864	\N	40
150	f	f				2014-02-08 15:29:41.844864	\N	2014-02-08 15:29:41.844864	\N	150
450	f	t				2014-02-08 15:29:41.844864	\N	2014-02-08 15:29:41.844864	\N	450
550	f	t				2014-02-08 15:29:41.844864	\N	2014-02-08 15:29:41.844864	\N	550
360	f	t				2014-02-08 15:29:41.844864	\N	2014-02-08 15:29:41.844864	\N	360
670	f	f				2014-02-08 15:29:41.844864	\N	2014-02-08 15:29:41.844864	\N	670
680	f	t				2014-02-08 15:29:41.844864	\N	2014-02-08 15:29:41.844864	\N	680
685	f	t				2014-02-08 15:29:41.844864	\N	2014-02-08 15:29:41.844864	\N	685
750	f	f	0,1,2	0,4		2014-02-08 15:29:41.844864	\N	2014-02-08 15:29:41.844864	\N	750
913	f	f				2014-02-08 15:29:41.844864	\N	2014-02-08 15:29:41.844864	\N	913
\.


--
-- Data for Name: vocabulary_form_subfields; Type: TABLE DATA; Schema: bib4template; Owner: biblivre
--

COPY bib4template.vocabulary_form_subfields (datafield, subfield, collapsed, repeatable, created, created_by, modified, modified_by, autocomplete_type, sort_order) FROM stdin;
040	a	f	f	2014-02-08 15:30:23.272565	\N	2014-02-08 15:30:23.272565	\N	disabled	137
040	b	f	f	2014-02-08 15:30:23.272565	\N	2014-02-08 15:30:23.272565	\N	disabled	138
040	c	f	f	2014-02-08 15:30:23.272565	\N	2014-02-08 15:30:23.272565	\N	disabled	139
040	d	f	t	2014-02-08 15:30:23.272565	\N	2014-02-08 15:30:23.272565	\N	disabled	140
040	e	f	t	2014-02-08 15:30:23.272565	\N	2014-02-08 15:30:23.272565	\N	disabled	141
150	a	f	f	2014-02-08 15:30:23.272565	\N	2014-02-08 15:30:23.272565	\N	disabled	247
150	i	f	f	2014-02-08 15:30:23.272565	\N	2014-02-08 15:30:23.272565	\N	disabled	255
150	x	f	t	2014-02-08 15:30:23.272565	\N	2014-02-08 15:30:23.272565	\N	disabled	270
150	y	f	t	2014-02-08 15:30:23.272565	\N	2014-02-08 15:30:23.272565	\N	disabled	271
150	z	f	t	2014-02-08 15:30:23.272565	\N	2014-02-08 15:30:23.272565	\N	disabled	272
450	a	f	f	2014-02-08 15:30:23.272565	\N	2014-02-08 15:30:23.272565	\N	disabled	547
550	a	f	f	2014-02-08 15:30:23.272565	\N	2014-02-08 15:30:23.272565	\N	disabled	647
550	x	f	t	2014-02-08 15:30:23.272565	\N	2014-02-08 15:30:23.272565	\N	disabled	670
550	y	f	t	2014-02-08 15:30:23.272565	\N	2014-02-08 15:30:23.272565	\N	disabled	671
550	z	f	t	2014-02-08 15:30:23.272565	\N	2014-02-08 15:30:23.272565	\N	disabled	672
360	a	f	f	2014-02-08 15:30:23.272565	\N	2014-02-08 15:30:23.272565	\N	disabled	457
360	x	f	t	2014-02-08 15:30:23.272565	\N	2014-02-08 15:30:23.272565	\N	disabled	480
360	y	f	t	2014-02-08 15:30:23.272565	\N	2014-02-08 15:30:23.272565	\N	disabled	481
360	z	f	t	2014-02-08 15:30:23.272565	\N	2014-02-08 15:30:23.272565	\N	disabled	482
670	a	f	f	2014-02-08 15:30:23.272565	\N	2014-02-08 15:30:23.272565	\N	disabled	767
680	a	f	f	2014-02-08 15:30:23.272565	\N	2014-02-08 15:30:23.272565	\N	disabled	777
685	i	f	f	2014-02-08 15:30:23.272565	\N	2014-02-08 15:30:23.272565	\N	disabled	790
750	a	f	f	2014-02-08 15:30:23.272565	\N	2014-02-08 15:30:23.272565	\N	disabled	847
750	x	f	t	2014-02-08 15:30:23.272565	\N	2014-02-08 15:30:23.272565	\N	disabled	870
750	y	f	t	2014-02-08 15:30:23.272565	\N	2014-02-08 15:30:23.272565	\N	disabled	871
750	z	f	t	2014-02-08 15:30:23.272565	\N	2014-02-08 15:30:23.272565	\N	disabled	872
913	a	f	f	2014-02-08 15:30:23.272565	\N	2014-02-08 15:30:23.272565	\N	disabled	1010
\.


--
-- Data for Name: vocabulary_idx_autocomplete; Type: TABLE DATA; Schema: bib4template; Owner: biblivre
--

COPY bib4template.vocabulary_idx_autocomplete (id, datafield, subfield, word, phrase, record_id) FROM stdin;
\.


--
-- Data for Name: vocabulary_idx_fields; Type: TABLE DATA; Schema: bib4template; Owner: biblivre
--

COPY bib4template.vocabulary_idx_fields (record_id, indexing_group_id, word, datafield) FROM stdin;
\.


--
-- Data for Name: vocabulary_idx_sort; Type: TABLE DATA; Schema: bib4template; Owner: biblivre
--

COPY bib4template.vocabulary_idx_sort (record_id, indexing_group_id, phrase, ignore_chars_count) FROM stdin;
\.


--
-- Data for Name: vocabulary_indexing_groups; Type: TABLE DATA; Schema: bib4template; Owner: biblivre
--

COPY bib4template.vocabulary_indexing_groups (id, translation_key, datafields, sortable, default_sort, created, created_by, modified, modified_by) FROM stdin;
0	all	\N	f	f	2014-03-04 11:16:16.428	\N	2014-03-04 11:16:16.428	\N
1	te_term	150_a	t	t	2014-03-04 11:16:31.24	\N	2014-03-04 11:16:31.24	\N
2	up_term	450_a	t	f	2014-03-04 11:16:48.069	\N	2014-03-04 11:16:48.069	\N
3	tg_term	550_a	t	f	2014-03-04 11:16:59.899	\N	2014-03-04 11:16:59.899	\N
4	vt_ta_term	360_a	t	f	2014-03-04 11:17:18.848	\N	2014-03-04 11:17:18.848	\N
\.


--
-- Data for Name: vocabulary_records; Type: TABLE DATA; Schema: bib4template; Owner: biblivre
--

COPY bib4template.vocabulary_records (id, iso2709, material, database, created, created_by, modified, modified_by) FROM stdin;
\.


--
-- Data for Name: vocabulary_search_results; Type: TABLE DATA; Schema: bib4template; Owner: biblivre
--

COPY bib4template.vocabulary_search_results (search_id, indexing_group_id, record_id) FROM stdin;
\.


--
-- Data for Name: vocabulary_searches; Type: TABLE DATA; Schema: bib4template; Owner: biblivre
--

COPY bib4template.vocabulary_searches (id, parameters, created, created_by) FROM stdin;
\.


--
-- Data for Name: z3950_addresses; Type: TABLE DATA; Schema: bib4template; Owner: biblivre
--

COPY bib4template.z3950_addresses (id, name, url, port, collection) FROM stdin;
1	Universidad de Chile - Santiago, Chile	unicornio.uchile.cl	2200	default
2	Université Laval - Quebec, Canadá	ariane2.ulaval.ca	2200	default
3	Brunel University - Londres, Reino Unido	library.brunel.ac.uk	2200	default
4	Acadia University - Nova Escócia, Canada	jasper.acadiau.ca	2200	default
5	Carnegie Mellon University - Pittsburgh, PA - EUA	webcat.library.cmu.edu	2200	unicorn
6	New York Public Library - EUA	catalog.nypl.org	210	INNOPAC
7	Biblioteca Nacional da Espanha - Madrid	sigb.bne.es	2200	default
8	Library of Congress Online Catalog - EUA	140.147.249.67	210	LCDB
9	South University New Orleans, EUA	suno.louislibraries.org	7705	default
10	Penn State University, EUA	zcat.libraries.psu.edu	2200	default
11	The Fletcher School, Tufts University, EUA	fletcher.louislibraries.org	8205	default
12	Univerdidad de Madrid, Espanha	marte.biblioteca.upm.es	2200	default
\.


--
-- Name: access_cards_id_seq; Type: SEQUENCE SET; Schema: bib4template; Owner: biblivre
--

SELECT pg_catalog.setval('bib4template.access_cards_id_seq', 1, false);


--
-- Name: access_control_id_seq; Type: SEQUENCE SET; Schema: bib4template; Owner: biblivre
--

SELECT pg_catalog.setval('bib4template.access_control_id_seq', 1, false);


--
-- Name: authorities_idx_autocomplete_id_seq; Type: SEQUENCE SET; Schema: bib4template; Owner: biblivre
--

SELECT pg_catalog.setval('bib4template.authorities_idx_autocomplete_id_seq', 1, false);


--
-- Name: authorities_indexing_groups_id_seq; Type: SEQUENCE SET; Schema: bib4template; Owner: biblivre
--

SELECT pg_catalog.setval('bib4template.authorities_indexing_groups_id_seq', 5, false);


--
-- Name: authorities_records_id_seq; Type: SEQUENCE SET; Schema: bib4template; Owner: biblivre
--

SELECT pg_catalog.setval('bib4template.authorities_records_id_seq', 1, false);


--
-- Name: authorities_searches_id_seq; Type: SEQUENCE SET; Schema: bib4template; Owner: biblivre
--

SELECT pg_catalog.setval('bib4template.authorities_searches_id_seq', 1, false);


--
-- Name: backups_id_seq; Type: SEQUENCE SET; Schema: bib4template; Owner: biblivre
--

SELECT pg_catalog.setval('bib4template.backups_id_seq', 1, false);


--
-- Name: biblio_holdings_id_seq; Type: SEQUENCE SET; Schema: bib4template; Owner: biblivre
--

SELECT pg_catalog.setval('bib4template.biblio_holdings_id_seq', 1, false);


--
-- Name: biblio_idx_autocomplete_id_seq; Type: SEQUENCE SET; Schema: bib4template; Owner: biblivre
--

SELECT pg_catalog.setval('bib4template.biblio_idx_autocomplete_id_seq', 5103, false);


--
-- Name: biblio_indexing_groups_id_seq; Type: SEQUENCE SET; Schema: bib4template; Owner: biblivre
--

SELECT pg_catalog.setval('bib4template.biblio_indexing_groups_id_seq', 8, true);


--
-- Name: biblio_records_id_seq; Type: SEQUENCE SET; Schema: bib4template; Owner: biblivre
--

SELECT pg_catalog.setval('bib4template.biblio_records_id_seq', 1, false);


--
-- Name: biblio_searches_id_seq; Type: SEQUENCE SET; Schema: bib4template; Owner: biblivre
--

SELECT pg_catalog.setval('bib4template.biblio_searches_id_seq', 1, false);


--
-- Name: digital_media_id_seq; Type: SEQUENCE SET; Schema: bib4template; Owner: biblivre
--

SELECT pg_catalog.setval('bib4template.digital_media_id_seq', 1, false);


--
-- Name: holding_creation_counter_id_seq; Type: SEQUENCE SET; Schema: bib4template; Owner: biblivre
--

SELECT pg_catalog.setval('bib4template.holding_creation_counter_id_seq', 1, false);


--
-- Name: lending_fines_id_seq; Type: SEQUENCE SET; Schema: bib4template; Owner: biblivre
--

SELECT pg_catalog.setval('bib4template.lending_fines_id_seq', 1, false);


--
-- Name: lendings_id_seq; Type: SEQUENCE SET; Schema: bib4template; Owner: biblivre
--

SELECT pg_catalog.setval('bib4template.lendings_id_seq', 1, false);


--
-- Name: logins_id_seq; Type: SEQUENCE SET; Schema: bib4template; Owner: biblivre
--

SELECT pg_catalog.setval('bib4template.logins_id_seq', 2, false);


--
-- Name: orders_id_seq; Type: SEQUENCE SET; Schema: bib4template; Owner: biblivre
--

SELECT pg_catalog.setval('bib4template.orders_id_seq', 1, false);


--
-- Name: quotations_id_seq; Type: SEQUENCE SET; Schema: bib4template; Owner: biblivre
--

SELECT pg_catalog.setval('bib4template.quotations_id_seq', 1, false);


--
-- Name: request_id_seq; Type: SEQUENCE SET; Schema: bib4template; Owner: biblivre
--

SELECT pg_catalog.setval('bib4template.request_id_seq', 1, false);


--
-- Name: reservations_id_seq; Type: SEQUENCE SET; Schema: bib4template; Owner: biblivre
--

SELECT pg_catalog.setval('bib4template.reservations_id_seq', 1, false);


--
-- Name: supplier_id_seq; Type: SEQUENCE SET; Schema: bib4template; Owner: biblivre
--

SELECT pg_catalog.setval('bib4template.supplier_id_seq', 1, false);


--
-- Name: users_id_seq; Type: SEQUENCE SET; Schema: bib4template; Owner: biblivre
--

SELECT pg_catalog.setval('bib4template.users_id_seq', 1, false);


--
-- Name: users_types_id_seq; Type: SEQUENCE SET; Schema: bib4template; Owner: biblivre
--

SELECT pg_catalog.setval('bib4template.users_types_id_seq', 3, false);


--
-- Name: vocabulary_idx_autocomplete_id_seq; Type: SEQUENCE SET; Schema: bib4template; Owner: biblivre
--

SELECT pg_catalog.setval('bib4template.vocabulary_idx_autocomplete_id_seq', 1, false);


--
-- Name: vocabulary_indexing_groups_id_seq; Type: SEQUENCE SET; Schema: bib4template; Owner: biblivre
--

SELECT pg_catalog.setval('bib4template.vocabulary_indexing_groups_id_seq', 5, false);


--
-- Name: vocabulary_records_id_seq; Type: SEQUENCE SET; Schema: bib4template; Owner: biblivre
--

SELECT pg_catalog.setval('bib4template.vocabulary_records_id_seq', 1, false);


--
-- Name: vocabulary_searches_id_seq; Type: SEQUENCE SET; Schema: bib4template; Owner: biblivre
--

SELECT pg_catalog.setval('bib4template.vocabulary_searches_id_seq', 1, false);


--
-- Name: z3950_addresses_id_seq; Type: SEQUENCE SET; Schema: bib4template; Owner: biblivre
--

SELECT pg_catalog.setval('bib4template.z3950_addresses_id_seq', 13, false);


--
-- Name: access_cards PK_access_cards; Type: CONSTRAINT; Schema: bib4template; Owner: biblivre
--

ALTER TABLE ONLY bib4template.access_cards
    ADD CONSTRAINT "PK_access_cards" PRIMARY KEY (id);


--
-- Name: access_control PK_access_control; Type: CONSTRAINT; Schema: bib4template; Owner: biblivre
--

ALTER TABLE ONLY bib4template.access_control
    ADD CONSTRAINT "PK_access_control" PRIMARY KEY (id);


--
-- Name: authorities_brief_formats PK_authorities_brief_formats; Type: CONSTRAINT; Schema: bib4template; Owner: biblivre
--

ALTER TABLE ONLY bib4template.authorities_brief_formats
    ADD CONSTRAINT "PK_authorities_brief_formats" PRIMARY KEY (datafield);


--
-- Name: authorities_form_datafields PK_authorities_form_datafields; Type: CONSTRAINT; Schema: bib4template; Owner: biblivre
--

ALTER TABLE ONLY bib4template.authorities_form_datafields
    ADD CONSTRAINT "PK_authorities_form_datafields" PRIMARY KEY (datafield);


--
-- Name: authorities_form_subfields PK_authorities_form_subfields; Type: CONSTRAINT; Schema: bib4template; Owner: biblivre
--

ALTER TABLE ONLY bib4template.authorities_form_subfields
    ADD CONSTRAINT "PK_authorities_form_subfields" PRIMARY KEY (datafield, subfield);


--
-- Name: authorities_idx_autocomplete PK_authorities_idx_autocomplete; Type: CONSTRAINT; Schema: bib4template; Owner: biblivre
--

ALTER TABLE ONLY bib4template.authorities_idx_autocomplete
    ADD CONSTRAINT "PK_authorities_idx_autocomplete" PRIMARY KEY (id);


--
-- Name: authorities_idx_fields PK_authorities_idx_fields; Type: CONSTRAINT; Schema: bib4template; Owner: biblivre
--

ALTER TABLE ONLY bib4template.authorities_idx_fields
    ADD CONSTRAINT "PK_authorities_idx_fields" PRIMARY KEY (record_id, indexing_group_id, word, datafield);


--
-- Name: authorities_idx_sort PK_authorities_idx_sort; Type: CONSTRAINT; Schema: bib4template; Owner: biblivre
--

ALTER TABLE ONLY bib4template.authorities_idx_sort
    ADD CONSTRAINT "PK_authorities_idx_sort" PRIMARY KEY (record_id, indexing_group_id);


--
-- Name: authorities_indexing_groups PK_authorities_indexing_groups; Type: CONSTRAINT; Schema: bib4template; Owner: biblivre
--

ALTER TABLE ONLY bib4template.authorities_indexing_groups
    ADD CONSTRAINT "PK_authorities_indexing_groups" PRIMARY KEY (id);


--
-- Name: authorities_records PK_authorities_records; Type: CONSTRAINT; Schema: bib4template; Owner: biblivre
--

ALTER TABLE ONLY bib4template.authorities_records
    ADD CONSTRAINT "PK_authorities_records" PRIMARY KEY (id);


--
-- Name: authorities_search_results PK_authorities_search_results; Type: CONSTRAINT; Schema: bib4template; Owner: biblivre
--

ALTER TABLE ONLY bib4template.authorities_search_results
    ADD CONSTRAINT "PK_authorities_search_results" PRIMARY KEY (search_id, indexing_group_id, record_id);


--
-- Name: authorities_searches PK_authorities_searches; Type: CONSTRAINT; Schema: bib4template; Owner: biblivre
--

ALTER TABLE ONLY bib4template.authorities_searches
    ADD CONSTRAINT "PK_authorities_searches" PRIMARY KEY (id);


--
-- Name: backups PK_backups; Type: CONSTRAINT; Schema: bib4template; Owner: biblivre
--

ALTER TABLE ONLY bib4template.backups
    ADD CONSTRAINT "PK_backups" PRIMARY KEY (id);


--
-- Name: biblio_brief_formats PK_biblio_brief_formats; Type: CONSTRAINT; Schema: bib4template; Owner: biblivre
--

ALTER TABLE ONLY bib4template.biblio_brief_formats
    ADD CONSTRAINT "PK_biblio_brief_formats" PRIMARY KEY (datafield);


--
-- Name: biblio_form_datafields PK_biblio_form_datafields; Type: CONSTRAINT; Schema: bib4template; Owner: biblivre
--

ALTER TABLE ONLY bib4template.biblio_form_datafields
    ADD CONSTRAINT "PK_biblio_form_datafields" PRIMARY KEY (datafield);


--
-- Name: biblio_form_subfields PK_biblio_form_subfields; Type: CONSTRAINT; Schema: bib4template; Owner: biblivre
--

ALTER TABLE ONLY bib4template.biblio_form_subfields
    ADD CONSTRAINT "PK_biblio_form_subfields" PRIMARY KEY (datafield, subfield);


--
-- Name: biblio_idx_autocomplete PK_biblio_idx_autocomplete; Type: CONSTRAINT; Schema: bib4template; Owner: biblivre
--

ALTER TABLE ONLY bib4template.biblio_idx_autocomplete
    ADD CONSTRAINT "PK_biblio_idx_autocomplete" PRIMARY KEY (id);


--
-- Name: biblio_idx_fields PK_biblio_idx_fields; Type: CONSTRAINT; Schema: bib4template; Owner: biblivre
--

ALTER TABLE ONLY bib4template.biblio_idx_fields
    ADD CONSTRAINT "PK_biblio_idx_fields" PRIMARY KEY (record_id, indexing_group_id, word, datafield);


--
-- Name: biblio_idx_sort PK_biblio_idx_sort; Type: CONSTRAINT; Schema: bib4template; Owner: biblivre
--

ALTER TABLE ONLY bib4template.biblio_idx_sort
    ADD CONSTRAINT "PK_biblio_idx_sort" PRIMARY KEY (record_id, indexing_group_id);


--
-- Name: biblio_indexing_groups PK_biblio_indexing_groups; Type: CONSTRAINT; Schema: bib4template; Owner: biblivre
--

ALTER TABLE ONLY bib4template.biblio_indexing_groups
    ADD CONSTRAINT "PK_biblio_indexing_groups" PRIMARY KEY (id);


--
-- Name: biblio_records PK_biblio_records; Type: CONSTRAINT; Schema: bib4template; Owner: biblivre
--

ALTER TABLE ONLY bib4template.biblio_records
    ADD CONSTRAINT "PK_biblio_records" PRIMARY KEY (id);


--
-- Name: biblio_search_results PK_biblio_search_results; Type: CONSTRAINT; Schema: bib4template; Owner: biblivre
--

ALTER TABLE ONLY bib4template.biblio_search_results
    ADD CONSTRAINT "PK_biblio_search_results" PRIMARY KEY (search_id, indexing_group_id, record_id);


--
-- Name: biblio_searches PK_biblio_searches; Type: CONSTRAINT; Schema: bib4template; Owner: biblivre
--

ALTER TABLE ONLY bib4template.biblio_searches
    ADD CONSTRAINT "PK_biblio_searches" PRIMARY KEY (id);


--
-- Name: configurations PK_configurations; Type: CONSTRAINT; Schema: bib4template; Owner: biblivre
--

ALTER TABLE ONLY bib4template.configurations
    ADD CONSTRAINT "PK_configurations" PRIMARY KEY (key);


--
-- Name: digital_media PK_digital_media; Type: CONSTRAINT; Schema: bib4template; Owner: biblivre
--

ALTER TABLE ONLY bib4template.digital_media
    ADD CONSTRAINT "PK_digital_media" PRIMARY KEY (id);


--
-- Name: holding_creation_counter PK_holding_creation_counter; Type: CONSTRAINT; Schema: bib4template; Owner: biblivre
--

ALTER TABLE ONLY bib4template.holding_creation_counter
    ADD CONSTRAINT "PK_holding_creation_counter" PRIMARY KEY (id);


--
-- Name: holding_form_datafields PK_holding_form_datafields; Type: CONSTRAINT; Schema: bib4template; Owner: biblivre
--

ALTER TABLE ONLY bib4template.holding_form_datafields
    ADD CONSTRAINT "PK_holding_form_datafields" PRIMARY KEY (datafield);


--
-- Name: holding_form_subfields PK_holding_form_subfields; Type: CONSTRAINT; Schema: bib4template; Owner: biblivre
--

ALTER TABLE ONLY bib4template.holding_form_subfields
    ADD CONSTRAINT "PK_holding_form_subfields" PRIMARY KEY (datafield, subfield);


--
-- Name: lending_fines PK_lending_fines; Type: CONSTRAINT; Schema: bib4template; Owner: biblivre
--

ALTER TABLE ONLY bib4template.lending_fines
    ADD CONSTRAINT "PK_lending_fines" PRIMARY KEY (id);


--
-- Name: logins PK_logins; Type: CONSTRAINT; Schema: bib4template; Owner: biblivre
--

ALTER TABLE ONLY bib4template.logins
    ADD CONSTRAINT "PK_logins" PRIMARY KEY (id);


--
-- Name: orders PK_order; Type: CONSTRAINT; Schema: bib4template; Owner: biblivre
--

ALTER TABLE ONLY bib4template.orders
    ADD CONSTRAINT "PK_order" PRIMARY KEY (id);


--
-- Name: permissions PK_permissions; Type: CONSTRAINT; Schema: bib4template; Owner: biblivre
--

ALTER TABLE ONLY bib4template.permissions
    ADD CONSTRAINT "PK_permissions" PRIMARY KEY (login_id, permission);


--
-- Name: quotations PK_quotations; Type: CONSTRAINT; Schema: bib4template; Owner: biblivre
--

ALTER TABLE ONLY bib4template.quotations
    ADD CONSTRAINT "PK_quotations" PRIMARY KEY (id);


--
-- Name: requests PK_request; Type: CONSTRAINT; Schema: bib4template; Owner: biblivre
--

ALTER TABLE ONLY bib4template.requests
    ADD CONSTRAINT "PK_request" PRIMARY KEY (id);


--
-- Name: request_quotation PK_request_quotation; Type: CONSTRAINT; Schema: bib4template; Owner: biblivre
--

ALTER TABLE ONLY bib4template.request_quotation
    ADD CONSTRAINT "PK_request_quotation" PRIMARY KEY (request_id, quotation_id);


--
-- Name: suppliers PK_supplier; Type: CONSTRAINT; Schema: bib4template; Owner: biblivre
--

ALTER TABLE ONLY bib4template.suppliers
    ADD CONSTRAINT "PK_supplier" PRIMARY KEY (id);


--
-- Name: translations PK_translations; Type: CONSTRAINT; Schema: bib4template; Owner: biblivre
--

ALTER TABLE ONLY bib4template.translations
    ADD CONSTRAINT "PK_translations" PRIMARY KEY (language, key);


--
-- Name: users_fields PK_users_fields; Type: CONSTRAINT; Schema: bib4template; Owner: biblivre
--

ALTER TABLE ONLY bib4template.users_fields
    ADD CONSTRAINT "PK_users_fields" PRIMARY KEY (key);


--
-- Name: users_types PK_users_types; Type: CONSTRAINT; Schema: bib4template; Owner: biblivre
--

ALTER TABLE ONLY bib4template.users_types
    ADD CONSTRAINT "PK_users_types" PRIMARY KEY (id);


--
-- Name: users_values PK_users_values; Type: CONSTRAINT; Schema: bib4template; Owner: biblivre
--

ALTER TABLE ONLY bib4template.users_values
    ADD CONSTRAINT "PK_users_values" PRIMARY KEY (user_id, key);


--
-- Name: versions PK_versions; Type: CONSTRAINT; Schema: bib4template; Owner: biblivre
--

ALTER TABLE ONLY bib4template.versions
    ADD CONSTRAINT "PK_versions" PRIMARY KEY (installed_versions);


--
-- Name: vocabulary_brief_formats PK_vocabulary_brief_formats; Type: CONSTRAINT; Schema: bib4template; Owner: biblivre
--

ALTER TABLE ONLY bib4template.vocabulary_brief_formats
    ADD CONSTRAINT "PK_vocabulary_brief_formats" PRIMARY KEY (datafield);


--
-- Name: vocabulary_form_datafields PK_vocabulary_form_datafields; Type: CONSTRAINT; Schema: bib4template; Owner: biblivre
--

ALTER TABLE ONLY bib4template.vocabulary_form_datafields
    ADD CONSTRAINT "PK_vocabulary_form_datafields" PRIMARY KEY (datafield);


--
-- Name: vocabulary_form_subfields PK_vocabulary_form_subfields; Type: CONSTRAINT; Schema: bib4template; Owner: biblivre
--

ALTER TABLE ONLY bib4template.vocabulary_form_subfields
    ADD CONSTRAINT "PK_vocabulary_form_subfields" PRIMARY KEY (datafield, subfield);


--
-- Name: vocabulary_idx_autocomplete PK_vocabulary_idx_autocomplete; Type: CONSTRAINT; Schema: bib4template; Owner: biblivre
--

ALTER TABLE ONLY bib4template.vocabulary_idx_autocomplete
    ADD CONSTRAINT "PK_vocabulary_idx_autocomplete" PRIMARY KEY (id);


--
-- Name: vocabulary_idx_fields PK_vocabulary_idx_fields; Type: CONSTRAINT; Schema: bib4template; Owner: biblivre
--

ALTER TABLE ONLY bib4template.vocabulary_idx_fields
    ADD CONSTRAINT "PK_vocabulary_idx_fields" PRIMARY KEY (record_id, indexing_group_id, word, datafield);


--
-- Name: vocabulary_idx_sort PK_vocabulary_idx_sort; Type: CONSTRAINT; Schema: bib4template; Owner: biblivre
--

ALTER TABLE ONLY bib4template.vocabulary_idx_sort
    ADD CONSTRAINT "PK_vocabulary_idx_sort" PRIMARY KEY (record_id, indexing_group_id);


--
-- Name: vocabulary_indexing_groups PK_vocabulary_indexing_groups; Type: CONSTRAINT; Schema: bib4template; Owner: biblivre
--

ALTER TABLE ONLY bib4template.vocabulary_indexing_groups
    ADD CONSTRAINT "PK_vocabulary_indexing_groups" PRIMARY KEY (id);


--
-- Name: vocabulary_records PK_vocabulary_records; Type: CONSTRAINT; Schema: bib4template; Owner: biblivre
--

ALTER TABLE ONLY bib4template.vocabulary_records
    ADD CONSTRAINT "PK_vocabulary_records" PRIMARY KEY (id);


--
-- Name: vocabulary_search_results PK_vocabulary_search_results; Type: CONSTRAINT; Schema: bib4template; Owner: biblivre
--

ALTER TABLE ONLY bib4template.vocabulary_search_results
    ADD CONSTRAINT "PK_vocabulary_search_results" PRIMARY KEY (search_id, indexing_group_id, record_id);


--
-- Name: vocabulary_searches PK_vocabulary_searches; Type: CONSTRAINT; Schema: bib4template; Owner: biblivre
--

ALTER TABLE ONLY bib4template.vocabulary_searches
    ADD CONSTRAINT "PK_vocabulary_searches" PRIMARY KEY (id);


--
-- Name: z3950_addresses PK_z3950_addresses; Type: CONSTRAINT; Schema: bib4template; Owner: biblivre
--

ALTER TABLE ONLY bib4template.z3950_addresses
    ADD CONSTRAINT "PK_z3950_addresses" PRIMARY KEY (id);


--
-- Name: access_cards UN_access_cards; Type: CONSTRAINT; Schema: bib4template; Owner: biblivre
--

ALTER TABLE ONLY bib4template.access_cards
    ADD CONSTRAINT "UN_access_cards" UNIQUE (code);


--
-- Name: logins UN_logins; Type: CONSTRAINT; Schema: bib4template; Owner: biblivre
--

ALTER TABLE ONLY bib4template.logins
    ADD CONSTRAINT "UN_logins" UNIQUE (login);


--
-- Name: biblio_holdings pk_biblio_holdings; Type: CONSTRAINT; Schema: bib4template; Owner: biblivre
--

ALTER TABLE ONLY bib4template.biblio_holdings
    ADD CONSTRAINT pk_biblio_holdings PRIMARY KEY (id);


--
-- Name: lendings pk_lendings; Type: CONSTRAINT; Schema: bib4template; Owner: biblivre
--

ALTER TABLE ONLY bib4template.lendings
    ADD CONSTRAINT pk_lendings PRIMARY KEY (id);


--
-- Name: reservations pk_reservations; Type: CONSTRAINT; Schema: bib4template; Owner: biblivre
--

ALTER TABLE ONLY bib4template.reservations
    ADD CONSTRAINT pk_reservations PRIMARY KEY (id);


--
-- Name: users users_pkey; Type: CONSTRAINT; Schema: bib4template; Owner: biblivre
--

ALTER TABLE ONLY bib4template.users
    ADD CONSTRAINT users_pkey PRIMARY KEY (id);


--
-- Name: FKI_access_control_access_cards; Type: INDEX; Schema: bib4template; Owner: biblivre
--

CREATE INDEX "FKI_access_control_access_cards" ON bib4template.access_control USING btree (access_card_id);


--
-- Name: FKI_access_control_users; Type: INDEX; Schema: bib4template; Owner: biblivre
--

CREATE INDEX "FKI_access_control_users" ON bib4template.access_control USING btree (user_id);


--
-- Name: IX_authorities_idx_autocomplete_record_id; Type: INDEX; Schema: bib4template; Owner: biblivre
--

CREATE INDEX "IX_authorities_idx_autocomplete_record_id" ON bib4template.authorities_idx_autocomplete USING btree (record_id);


--
-- Name: IX_authorities_idx_autocomplete_word; Type: INDEX; Schema: bib4template; Owner: biblivre
--

CREATE INDEX "IX_authorities_idx_autocomplete_word" ON bib4template.authorities_idx_autocomplete USING btree (datafield, subfield, word varchar_pattern_ops);


--
-- Name: IX_authorities_idx_fields_word; Type: INDEX; Schema: bib4template; Owner: biblivre
--

CREATE INDEX "IX_authorities_idx_fields_word" ON bib4template.authorities_idx_fields USING btree (word varchar_pattern_ops);


--
-- Name: IX_authorities_search_results; Type: INDEX; Schema: bib4template; Owner: biblivre
--

CREATE INDEX "IX_authorities_search_results" ON bib4template.authorities_search_results USING btree (search_id, record_id);


--
-- Name: IX_biblio_holdings_accession_number; Type: INDEX; Schema: bib4template; Owner: biblivre
--

CREATE UNIQUE INDEX "IX_biblio_holdings_accession_number" ON bib4template.biblio_holdings USING btree (accession_number);


--
-- Name: IX_biblio_holdings_biblio_record; Type: INDEX; Schema: bib4template; Owner: biblivre
--

CREATE INDEX "IX_biblio_holdings_biblio_record" ON bib4template.biblio_holdings USING btree (record_id, database);


--
-- Name: IX_biblio_holdings_location_d; Type: INDEX; Schema: bib4template; Owner: biblivre
--

CREATE INDEX "IX_biblio_holdings_location_d" ON bib4template.biblio_holdings USING btree (record_id, location_d);


--
-- Name: IX_biblio_idx_autocomplete_record_id; Type: INDEX; Schema: bib4template; Owner: biblivre
--

CREATE INDEX "IX_biblio_idx_autocomplete_record_id" ON bib4template.biblio_idx_autocomplete USING btree (record_id);


--
-- Name: IX_biblio_idx_autocomplete_word; Type: INDEX; Schema: bib4template; Owner: biblivre
--

CREATE INDEX "IX_biblio_idx_autocomplete_word" ON bib4template.biblio_idx_autocomplete USING btree (datafield, subfield, word varchar_pattern_ops);


--
-- Name: IX_biblio_idx_fields_word; Type: INDEX; Schema: bib4template; Owner: biblivre
--

CREATE INDEX "IX_biblio_idx_fields_word" ON bib4template.biblio_idx_fields USING btree (word varchar_pattern_ops);


--
-- Name: IX_biblio_search_results; Type: INDEX; Schema: bib4template; Owner: biblivre
--

CREATE INDEX "IX_biblio_search_results" ON bib4template.biblio_search_results USING btree (search_id, record_id);


--
-- Name: IX_users_name; Type: INDEX; Schema: bib4template; Owner: biblivre
--

CREATE INDEX "IX_users_name" ON bib4template.users USING btree (name varchar_pattern_ops);


--
-- Name: IX_vocabulary_idx_autocomplete_record_id; Type: INDEX; Schema: bib4template; Owner: biblivre
--

CREATE INDEX "IX_vocabulary_idx_autocomplete_record_id" ON bib4template.vocabulary_idx_autocomplete USING btree (record_id);


--
-- Name: IX_vocabulary_idx_autocomplete_word; Type: INDEX; Schema: bib4template; Owner: biblivre
--

CREATE INDEX "IX_vocabulary_idx_autocomplete_word" ON bib4template.vocabulary_idx_autocomplete USING btree (datafield, subfield, word varchar_pattern_ops);


--
-- Name: IX_vocabulary_idx_fields_word; Type: INDEX; Schema: bib4template; Owner: biblivre
--

CREATE INDEX "IX_vocabulary_idx_fields_word" ON bib4template.vocabulary_idx_fields USING btree (word varchar_pattern_ops);


--
-- Name: IX_vocabulary_search_results; Type: INDEX; Schema: bib4template; Owner: biblivre
--

CREATE INDEX "IX_vocabulary_search_results" ON bib4template.vocabulary_search_results USING btree (search_id, record_id);


--
-- Name: ix_access_control_departure_time_user_id; Type: INDEX; Schema: bib4template; Owner: biblivre
--

CREATE INDEX ix_access_control_departure_time_user_id ON bib4template.access_control USING btree (departure_time, user_id);


--
-- Name: authorities_indexing_groups TRIGGER_clear_authorities_indexing_type; Type: TRIGGER; Schema: bib4template; Owner: biblivre
--

CREATE TRIGGER "TRIGGER_clear_authorities_indexing_type" AFTER DELETE ON bib4template.authorities_indexing_groups FOR EACH ROW EXECUTE PROCEDURE bib4template.clear_indexing_type('authorities');


--
-- Name: authorities_records TRIGGER_clear_authorities_record; Type: TRIGGER; Schema: bib4template; Owner: biblivre
--

CREATE TRIGGER "TRIGGER_clear_authorities_record" AFTER DELETE ON bib4template.authorities_records FOR EACH ROW EXECUTE PROCEDURE bib4template.clear_record('authorities');


--
-- Name: biblio_indexing_groups TRIGGER_clear_biblio_indexing_type; Type: TRIGGER; Schema: bib4template; Owner: biblivre
--

CREATE TRIGGER "TRIGGER_clear_biblio_indexing_type" AFTER DELETE ON bib4template.biblio_indexing_groups FOR EACH ROW EXECUTE PROCEDURE bib4template.clear_indexing_type('biblio');


--
-- Name: biblio_records TRIGGER_clear_biblio_record; Type: TRIGGER; Schema: bib4template; Owner: biblivre
--

CREATE TRIGGER "TRIGGER_clear_biblio_record" AFTER DELETE ON bib4template.biblio_records FOR EACH ROW EXECUTE PROCEDURE bib4template.clear_record('biblio');


--
-- Name: vocabulary_indexing_groups TRIGGER_clear_vocabulary_indexing_type; Type: TRIGGER; Schema: bib4template; Owner: biblivre
--

CREATE TRIGGER "TRIGGER_clear_vocabulary_indexing_type" AFTER DELETE ON bib4template.vocabulary_indexing_groups FOR EACH ROW EXECUTE PROCEDURE bib4template.clear_indexing_type('vocabulary');


--
-- Name: vocabulary_records TRIGGER_clear_vocabulary_record; Type: TRIGGER; Schema: bib4template; Owner: biblivre
--

CREATE TRIGGER "TRIGGER_clear_vocabulary_record" AFTER DELETE ON bib4template.vocabulary_records FOR EACH ROW EXECUTE PROCEDURE bib4template.clear_record('vocabulary');


--
-- Name: access_control FK_access_control_access_cards; Type: FK CONSTRAINT; Schema: bib4template; Owner: biblivre
--

ALTER TABLE ONLY bib4template.access_control
    ADD CONSTRAINT "FK_access_control_access_cards" FOREIGN KEY (access_card_id) REFERENCES bib4template.access_cards(id) ON DELETE CASCADE;


--
-- Name: access_control FK_access_control_users; Type: FK CONSTRAINT; Schema: bib4template; Owner: biblivre
--

ALTER TABLE ONLY bib4template.access_control
    ADD CONSTRAINT "FK_access_control_users" FOREIGN KEY (user_id) REFERENCES bib4template.users(id) ON DELETE CASCADE;


--
-- Name: authorities_form_subfields FK_authorities_form_subfields_authorities_form_datafields; Type: FK CONSTRAINT; Schema: bib4template; Owner: biblivre
--

ALTER TABLE ONLY bib4template.authorities_form_subfields
    ADD CONSTRAINT "FK_authorities_form_subfields_authorities_form_datafields" FOREIGN KEY (datafield) REFERENCES bib4template.authorities_form_datafields(datafield) ON DELETE CASCADE;


--
-- Name: biblio_form_subfields FK_biblio_form_subfields_biblio_form_datafields; Type: FK CONSTRAINT; Schema: bib4template; Owner: biblivre
--

ALTER TABLE ONLY bib4template.biblio_form_subfields
    ADD CONSTRAINT "FK_biblio_form_subfields_biblio_form_datafields" FOREIGN KEY (datafield) REFERENCES bib4template.biblio_form_datafields(datafield) ON DELETE CASCADE;


--
-- Name: holding_form_subfields FK_holding_form_subfields_holding_form_datafields; Type: FK CONSTRAINT; Schema: bib4template; Owner: biblivre
--

ALTER TABLE ONLY bib4template.holding_form_subfields
    ADD CONSTRAINT "FK_holding_form_subfields_holding_form_datafields" FOREIGN KEY (datafield) REFERENCES bib4template.holding_form_datafields(datafield) ON DELETE CASCADE;


--
-- Name: lending_fines FK_lending_fines_lendings; Type: FK CONSTRAINT; Schema: bib4template; Owner: biblivre
--

ALTER TABLE ONLY bib4template.lending_fines
    ADD CONSTRAINT "FK_lending_fines_lendings" FOREIGN KEY (lending_id) REFERENCES bib4template.lendings(id) ON DELETE CASCADE;


--
-- Name: lending_fines FK_lending_fines_users; Type: FK CONSTRAINT; Schema: bib4template; Owner: biblivre
--

ALTER TABLE ONLY bib4template.lending_fines
    ADD CONSTRAINT "FK_lending_fines_users" FOREIGN KEY (user_id) REFERENCES bib4template.users(id) ON DELETE CASCADE;


--
-- Name: orders FK_orders_quotations; Type: FK CONSTRAINT; Schema: bib4template; Owner: biblivre
--

ALTER TABLE ONLY bib4template.orders
    ADD CONSTRAINT "FK_orders_quotations" FOREIGN KEY (quotation_id) REFERENCES bib4template.quotations(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: permissions FK_permissions_logins; Type: FK CONSTRAINT; Schema: bib4template; Owner: biblivre
--

ALTER TABLE ONLY bib4template.permissions
    ADD CONSTRAINT "FK_permissions_logins" FOREIGN KEY (login_id) REFERENCES bib4template.logins(id) ON DELETE CASCADE;


--
-- Name: quotations FK_quotations_suppliers; Type: FK CONSTRAINT; Schema: bib4template; Owner: biblivre
--

ALTER TABLE ONLY bib4template.quotations
    ADD CONSTRAINT "FK_quotations_suppliers" FOREIGN KEY (supplier_id) REFERENCES bib4template.suppliers(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: request_quotation FK_request_quotation_quotations; Type: FK CONSTRAINT; Schema: bib4template; Owner: biblivre
--

ALTER TABLE ONLY bib4template.request_quotation
    ADD CONSTRAINT "FK_request_quotation_quotations" FOREIGN KEY (quotation_id) REFERENCES bib4template.quotations(id) ON DELETE CASCADE;


--
-- Name: request_quotation FK_request_quotation_requests; Type: FK CONSTRAINT; Schema: bib4template; Owner: biblivre
--

ALTER TABLE ONLY bib4template.request_quotation
    ADD CONSTRAINT "FK_request_quotation_requests" FOREIGN KEY (request_id) REFERENCES bib4template.requests(id) ON DELETE CASCADE;


--
-- Name: users_values FK_users_values_users; Type: FK CONSTRAINT; Schema: bib4template; Owner: biblivre
--

ALTER TABLE ONLY bib4template.users_values
    ADD CONSTRAINT "FK_users_values_users" FOREIGN KEY (user_id) REFERENCES bib4template.users(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: users_values FK_users_values_users_fields; Type: FK CONSTRAINT; Schema: bib4template; Owner: biblivre
--

ALTER TABLE ONLY bib4template.users_values
    ADD CONSTRAINT "FK_users_values_users_fields" FOREIGN KEY (key) REFERENCES bib4template.users_fields(key) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: vocabulary_form_subfields FK_vocabulary_form_subfields_vocabulary_form_datafields; Type: FK CONSTRAINT; Schema: bib4template; Owner: biblivre
--

ALTER TABLE ONLY bib4template.vocabulary_form_subfields
    ADD CONSTRAINT "FK_vocabulary_form_subfields_vocabulary_form_datafields" FOREIGN KEY (datafield) REFERENCES bib4template.vocabulary_form_datafields(datafield) ON DELETE CASCADE;


--
-- Name: biblio_holdings fk_biblio_holdings_biblio_records; Type: FK CONSTRAINT; Schema: bib4template; Owner: biblivre
--

ALTER TABLE ONLY bib4template.biblio_holdings
    ADD CONSTRAINT fk_biblio_holdings_biblio_records FOREIGN KEY (record_id) REFERENCES bib4template.biblio_records(id) ON DELETE CASCADE;


--
-- Name: lendings fk_lendings_biblio_holdings; Type: FK CONSTRAINT; Schema: bib4template; Owner: biblivre
--

ALTER TABLE ONLY bib4template.lendings
    ADD CONSTRAINT fk_lendings_biblio_holdings FOREIGN KEY (holding_id) REFERENCES bib4template.biblio_holdings(id) ON DELETE CASCADE;


--
-- Name: lendings fk_lendings_lendings; Type: FK CONSTRAINT; Schema: bib4template; Owner: biblivre
--

ALTER TABLE ONLY bib4template.lendings
    ADD CONSTRAINT fk_lendings_lendings FOREIGN KEY (previous_lending_id) REFERENCES bib4template.lendings(id) ON DELETE CASCADE;


--
-- Name: lendings fk_lendings_users; Type: FK CONSTRAINT; Schema: bib4template; Owner: biblivre
--

ALTER TABLE ONLY bib4template.lendings
    ADD CONSTRAINT fk_lendings_users FOREIGN KEY (user_id) REFERENCES bib4template.users(id) ON DELETE CASCADE;


--
-- Name: reservations fk_lendings_users; Type: FK CONSTRAINT; Schema: bib4template; Owner: biblivre
--

ALTER TABLE ONLY bib4template.reservations
    ADD CONSTRAINT fk_lendings_users FOREIGN KEY (user_id) REFERENCES bib4template.users(id) ON DELETE CASCADE;


--
-- Name: reservations fk_reservations_biblio_records; Type: FK CONSTRAINT; Schema: bib4template; Owner: biblivre
--

ALTER TABLE ONLY bib4template.reservations
    ADD CONSTRAINT fk_reservations_biblio_records FOREIGN KEY (record_id) REFERENCES bib4template.biblio_records(id) ON DELETE CASCADE;


--
-- PostgreSQL database dump complete
--

