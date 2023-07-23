--
-- PostgreSQL database dump
--

-- Dumped from database version 11.14 (Debian 11.14-1.pgdg90+1)
-- Dumped by pg_dump version 15.3

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
    password text,
    created timestamp without time zone DEFAULT now() NOT NULL,
    created_by integer,
    modified timestamp without time zone DEFAULT now() NOT NULL,
    modified_by integer,
    password_salt bytea,
    salted_password bytea
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



--
-- Data for Name: access_control; Type: TABLE DATA; Schema: bib4template; Owner: biblivre
--



--
-- Data for Name: authorities_brief_formats; Type: TABLE DATA; Schema: bib4template; Owner: biblivre
--

INSERT INTO bib4template.authorities_brief_formats VALUES ('100', '${a}_{; }${c}_{ - }${d}', 1, '2014-03-20 12:20:01.029', NULL, '2014-03-20 12:20:01.029', NULL);
INSERT INTO bib4template.authorities_brief_formats VALUES ('670', '${a}', 7, '2014-03-20 12:23:36.822', NULL, '2014-03-20 12:23:36.822', NULL);
INSERT INTO bib4template.authorities_brief_formats VALUES ('400', '${a}', 4, '2014-03-20 12:22:53.502', NULL, '2014-03-20 12:22:53.502', NULL);
INSERT INTO bib4template.authorities_brief_formats VALUES ('410', '${a}', 5, '2014-03-20 12:23:04.503', NULL, '2014-03-20 12:23:04.503', NULL);
INSERT INTO bib4template.authorities_brief_formats VALUES ('411', '${a}', 6, '2014-03-20 12:23:14.566', NULL, '2014-03-20 12:23:14.566', NULL);
INSERT INTO bib4template.authorities_brief_formats VALUES ('111', '${a}_{; }${c}_{ - }${d}', 3, '2014-03-20 12:22:40.585', NULL, '2014-03-20 12:22:40.585', NULL);
INSERT INTO bib4template.authorities_brief_formats VALUES ('110', '${a}_{; }${b}_{; }${c}_{ - }${d}', 2, '2014-03-20 12:22:07.272', NULL, '2014-03-20 12:22:07.272', NULL);


--
-- Data for Name: authorities_form_datafields; Type: TABLE DATA; Schema: bib4template; Owner: biblivre
--

INSERT INTO bib4template.authorities_form_datafields VALUES ('100', false, false, '0,1,2,3', '', '100', '2014-02-08 15:21:25.813358', NULL, '2014-02-08 15:21:25.813358', NULL, 100);
INSERT INTO bib4template.authorities_form_datafields VALUES ('110', false, false, '0,1,2', '', '110', '2014-02-08 15:21:25.813358', NULL, '2014-02-08 15:21:25.813358', NULL, 110);
INSERT INTO bib4template.authorities_form_datafields VALUES ('111', false, false, '0,1,2', '', '111', '2014-02-08 15:21:25.813358', NULL, '2014-02-08 15:21:25.813358', NULL, 111);
INSERT INTO bib4template.authorities_form_datafields VALUES ('400', false, true, '', '', '100', '2014-02-08 15:21:25.813358', NULL, '2014-02-08 15:21:25.813358', NULL, 400);
INSERT INTO bib4template.authorities_form_datafields VALUES ('410', false, true, '', '', '110', '2014-02-08 15:21:25.813358', NULL, '2014-02-08 15:21:25.813358', NULL, 410);
INSERT INTO bib4template.authorities_form_datafields VALUES ('411', false, true, '', '', '111', '2014-02-08 15:21:25.813358', NULL, '2014-02-08 15:21:25.813358', NULL, 411);
INSERT INTO bib4template.authorities_form_datafields VALUES ('670', false, true, '', '', '100,110,111', '2014-02-08 15:21:25.813358', NULL, '2014-02-08 15:21:25.813358', NULL, 670);


--
-- Data for Name: authorities_form_subfields; Type: TABLE DATA; Schema: bib4template; Owner: biblivre
--

INSERT INTO bib4template.authorities_form_subfields VALUES ('100', 'a', false, false, '2014-02-08 15:26:31.667337', NULL, '2014-02-08 15:26:31.667337', NULL, 'disabled', 197);
INSERT INTO bib4template.authorities_form_subfields VALUES ('100', 'b', false, false, '2014-02-08 15:26:31.667337', NULL, '2014-02-08 15:26:31.667337', NULL, 'disabled', 198);
INSERT INTO bib4template.authorities_form_subfields VALUES ('100', 'c', false, true, '2014-02-08 15:26:31.667337', NULL, '2014-02-08 15:26:31.667337', NULL, 'disabled', 199);
INSERT INTO bib4template.authorities_form_subfields VALUES ('100', 'd', false, false, '2014-02-08 15:26:31.667337', NULL, '2014-02-08 15:26:31.667337', NULL, 'disabled', 200);
INSERT INTO bib4template.authorities_form_subfields VALUES ('100', 'q', false, false, '2014-02-08 15:26:31.667337', NULL, '2014-02-08 15:26:31.667337', NULL, 'disabled', 213);
INSERT INTO bib4template.authorities_form_subfields VALUES ('400', 'a', false, false, '2014-02-08 15:26:31.667337', NULL, '2014-02-08 15:26:31.667337', NULL, 'disabled', 497);
INSERT INTO bib4template.authorities_form_subfields VALUES ('670', 'a', false, false, '2014-02-08 15:26:31.667337', NULL, '2014-02-08 15:26:31.667337', NULL, 'disabled', 767);
INSERT INTO bib4template.authorities_form_subfields VALUES ('670', 'b', false, false, '2014-02-08 15:26:31.667337', NULL, '2014-02-08 15:26:31.667337', NULL, 'disabled', 768);
INSERT INTO bib4template.authorities_form_subfields VALUES ('111', 'a', false, false, '2014-02-08 15:26:31.667337', NULL, '2014-02-08 15:26:31.667337', NULL, 'disabled', 208);
INSERT INTO bib4template.authorities_form_subfields VALUES ('111', 'c', false, false, '2014-02-08 15:26:31.667337', NULL, '2014-02-08 15:26:31.667337', NULL, 'disabled', 210);
INSERT INTO bib4template.authorities_form_subfields VALUES ('111', 'd', false, false, '2014-02-08 15:26:31.667337', NULL, '2014-02-08 15:26:31.667337', NULL, 'disabled', 211);
INSERT INTO bib4template.authorities_form_subfields VALUES ('111', 'e', false, true, '2014-02-08 15:26:31.667337', NULL, '2014-02-08 15:26:31.667337', NULL, 'disabled', 212);
INSERT INTO bib4template.authorities_form_subfields VALUES ('111', 'g', false, false, '2014-02-08 15:26:31.667337', NULL, '2014-02-08 15:26:31.667337', NULL, 'disabled', 214);
INSERT INTO bib4template.authorities_form_subfields VALUES ('111', 'k', false, false, '2014-02-08 15:26:31.667337', NULL, '2014-02-08 15:26:31.667337', NULL, 'disabled', 218);
INSERT INTO bib4template.authorities_form_subfields VALUES ('111', 'n', false, true, '2014-02-08 15:26:31.667337', NULL, '2014-02-08 15:26:31.667337', NULL, 'disabled', 221);
INSERT INTO bib4template.authorities_form_subfields VALUES ('411', 'a', false, false, '2014-02-08 15:26:31.667337', NULL, '2014-02-08 15:26:31.667337', NULL, 'disabled', 508);
INSERT INTO bib4template.authorities_form_subfields VALUES ('110', 'a', false, false, '2014-02-08 15:26:31.667337', NULL, '2014-02-08 15:26:31.667337', NULL, 'disabled', 207);
INSERT INTO bib4template.authorities_form_subfields VALUES ('110', 'b', false, true, '2014-02-08 15:26:31.667337', NULL, '2014-02-08 15:26:31.667337', NULL, 'disabled', 208);
INSERT INTO bib4template.authorities_form_subfields VALUES ('110', 'c', false, false, '2014-02-08 15:26:31.667337', NULL, '2014-02-08 15:26:31.667337', NULL, 'disabled', 209);
INSERT INTO bib4template.authorities_form_subfields VALUES ('110', 'd', false, true, '2014-02-08 15:26:31.667337', NULL, '2014-02-08 15:26:31.667337', NULL, 'disabled', 210);
INSERT INTO bib4template.authorities_form_subfields VALUES ('110', 'l', false, false, '2014-02-08 15:26:31.667337', NULL, '2014-02-08 15:26:31.667337', NULL, 'disabled', 218);
INSERT INTO bib4template.authorities_form_subfields VALUES ('110', 'n', false, true, '2014-02-08 15:26:31.667337', NULL, '2014-02-08 15:26:31.667337', NULL, 'disabled', 220);
INSERT INTO bib4template.authorities_form_subfields VALUES ('410', 'a', false, false, '2014-02-08 15:26:31.667337', NULL, '2014-02-08 15:26:31.667337', NULL, 'disabled', 507);


--
-- Data for Name: authorities_idx_autocomplete; Type: TABLE DATA; Schema: bib4template; Owner: biblivre
--



--
-- Data for Name: authorities_idx_fields; Type: TABLE DATA; Schema: bib4template; Owner: biblivre
--



--
-- Data for Name: authorities_idx_sort; Type: TABLE DATA; Schema: bib4template; Owner: biblivre
--



--
-- Data for Name: authorities_indexing_groups; Type: TABLE DATA; Schema: bib4template; Owner: biblivre
--

INSERT INTO bib4template.authorities_indexing_groups VALUES (0, 'all', NULL, false, false, '2014-03-04 11:09:07.241', NULL, '2014-03-04 11:09:07.241', NULL);
INSERT INTO bib4template.authorities_indexing_groups VALUES (1, 'author', '100_a', true, true, '2014-03-04 11:13:31.512', NULL, '2014-03-04 11:13:31.512', NULL);
INSERT INTO bib4template.authorities_indexing_groups VALUES (2, 'entity', '110_a', true, false, '2014-03-04 11:13:46.059', NULL, '2014-03-04 11:13:46.059', NULL);
INSERT INTO bib4template.authorities_indexing_groups VALUES (3, 'event', '111_a', true, false, '2014-03-04 11:14:39.973', NULL, '2014-03-04 11:14:39.973', NULL);
INSERT INTO bib4template.authorities_indexing_groups VALUES (4, 'other_name', '400_a, 410_a, 411_a', true, false, '2014-03-04 11:14:55.617', NULL, '2014-03-04 11:14:55.617', NULL);


--
-- Data for Name: authorities_records; Type: TABLE DATA; Schema: bib4template; Owner: biblivre
--



--
-- Data for Name: authorities_search_results; Type: TABLE DATA; Schema: bib4template; Owner: biblivre
--



--
-- Data for Name: authorities_searches; Type: TABLE DATA; Schema: bib4template; Owner: biblivre
--



--
-- Data for Name: backups; Type: TABLE DATA; Schema: bib4template; Owner: biblivre
--



--
-- Data for Name: biblio_brief_formats; Type: TABLE DATA; Schema: bib4template; Owner: biblivre
--

INSERT INTO bib4template.biblio_brief_formats VALUES ('260', '${a}_{: }${b}_{, }${c}', 20, '2014-02-01 12:04:44.576993', NULL, '2014-02-01 12:04:44.576993', NULL);
INSERT INTO bib4template.biblio_brief_formats VALUES ('650', '${a}_{ - }${x}_{ - }${y}_{ - }${z}', 41, '2014-02-01 12:06:20.547937', NULL, '2014-02-01 12:06:20.547937', NULL);
INSERT INTO bib4template.biblio_brief_formats VALUES ('651', '${a}_{ - }${x}_{ - }${y}_{ - }${z}', 42, '2014-02-01 12:06:27.116236', NULL, '2014-02-01 12:06:27.116236', NULL);
INSERT INTO bib4template.biblio_brief_formats VALUES ('699', '${a}_{ - }${x}_{ - }${y}_{ - }${z}', 43, '2014-02-01 12:06:34.276548', NULL, '2014-02-01 12:06:34.276548', NULL);
INSERT INTO bib4template.biblio_brief_formats VALUES ('306', '${a}', 25, '2014-02-01 12:13:07.424794', NULL, '2014-02-01 12:13:07.424794', NULL);
INSERT INTO bib4template.biblio_brief_formats VALUES ('520', '${a}', 27, '2014-02-01 12:13:19.273647', NULL, '2014-02-01 12:13:19.273647', NULL);
INSERT INTO bib4template.biblio_brief_formats VALUES ('020', '${a}', 22, '2014-02-01 12:04:17.840354', NULL, '2014-02-01 12:04:17.840354', NULL);
INSERT INTO bib4template.biblio_brief_formats VALUES ('022', '${a}', 23, '2014-02-01 12:04:24.056557', NULL, '2014-02-01 12:04:24.056557', NULL);
INSERT INTO bib4template.biblio_brief_formats VALUES ('024', '${a}', 24, '2014-02-01 12:04:30.936737', NULL, '2014-02-01 12:04:30.936737', NULL);
INSERT INTO bib4template.biblio_brief_formats VALUES ('256', '${a}', 44, '2014-02-01 12:15:25.005212', NULL, '2014-02-01 12:15:25.005212', NULL);
INSERT INTO bib4template.biblio_brief_formats VALUES ('043', '${a}', 45, '2014-02-01 12:15:33.581457', NULL, '2014-02-01 12:15:33.581457', NULL);
INSERT INTO bib4template.biblio_brief_formats VALUES ('045', '${a}', 46, '2014-02-01 12:15:42.981651', NULL, '2014-02-01 12:15:42.981651', NULL);
INSERT INTO bib4template.biblio_brief_formats VALUES ('255', '${a}', 47, '2014-02-01 12:15:46.853963', NULL, '2014-02-01 12:15:46.853963', NULL);
INSERT INTO bib4template.biblio_brief_formats VALUES ('041', '${a}', 48, '2014-02-01 12:15:50.093832', NULL, '2014-02-01 12:15:50.093832', NULL);
INSERT INTO bib4template.biblio_brief_formats VALUES ('090', '${a}_{ }${b}_{ }${c}_{ }${d}', 49, '2014-02-01 12:05:17.018043', NULL, '2014-02-01 12:05:17.018043', NULL);
INSERT INTO bib4template.biblio_brief_formats VALUES ('310', '${a}', 50, '2014-02-01 12:16:08.046403', NULL, '2014-02-01 12:16:08.046403', NULL);
INSERT INTO bib4template.biblio_brief_formats VALUES ('362', '${a}', 51, '2014-02-01 12:16:11.822932', NULL, '2014-02-01 12:16:11.822932', NULL);
INSERT INTO bib4template.biblio_brief_formats VALUES ('555', '${a}', 52, '2014-02-01 12:16:16.422789', NULL, '2014-02-01 12:16:16.422789', NULL);
INSERT INTO bib4template.biblio_brief_formats VALUES ('852', '${a}', 53, '2014-02-01 12:16:20.998809', NULL, '2014-02-01 12:16:20.998809', NULL);
INSERT INTO bib4template.biblio_brief_formats VALUES ('400', '${a}', 5, '2014-02-01 12:03:15.21457', NULL, '2014-02-01 12:03:15.21457', NULL);
INSERT INTO bib4template.biblio_brief_formats VALUES ('410', '${a}', 6, '2014-02-01 12:03:19.934289', NULL, '2014-02-01 12:03:19.934289', NULL);
INSERT INTO bib4template.biblio_brief_formats VALUES ('411', '${a}', 7, '2014-02-01 12:03:27.070922', NULL, '2014-02-01 12:03:27.070922', NULL);
INSERT INTO bib4template.biblio_brief_formats VALUES ('243', '${a}_{ }${f}', 12, '2014-02-01 12:09:14.089412', NULL, '2014-02-01 12:09:14.089412', NULL);
INSERT INTO bib4template.biblio_brief_formats VALUES ('240', '${a}', 13, '2014-02-01 12:09:24.050018', NULL, '2014-02-01 12:09:24.050018', NULL);
INSERT INTO bib4template.biblio_brief_formats VALUES ('730', '${a}', 14, '2014-02-01 12:09:30.866228', NULL, '2014-02-01 12:09:30.866228', NULL);
INSERT INTO bib4template.biblio_brief_formats VALUES ('740', '${a}_{ }${n}_{ }${p}', 15, '2014-02-01 12:09:44.610667', NULL, '2014-02-01 12:09:44.610667', NULL);
INSERT INTO bib4template.biblio_brief_formats VALUES ('250', '${a}', 17, '2014-02-01 12:10:18.923422', NULL, '2014-02-01 12:10:18.923422', NULL);
INSERT INTO bib4template.biblio_brief_formats VALUES ('257', '${a}', 18, '2014-02-01 12:10:36.436278', NULL, '2014-02-01 12:10:36.436278', NULL);
INSERT INTO bib4template.biblio_brief_formats VALUES ('258', '${a}', 19, '2014-02-01 12:10:41.228113', NULL, '2014-02-01 12:10:41.228113', NULL);
INSERT INTO bib4template.biblio_brief_formats VALUES ('300', '${a}_{ }${b}_{ }${c}_{ }${e}', 21, '2014-02-01 12:12:58.160709', NULL, '2014-02-01 12:12:58.160709', NULL);
INSERT INTO bib4template.biblio_brief_formats VALUES ('013', '${a}_{; }${b}_{; }${c}_{; }${d}_{; }${e}_{; }${f}', 56, '2014-02-01 12:18:17.042647', NULL, '2014-02-01 12:18:17.042647', NULL);
INSERT INTO bib4template.biblio_brief_formats VALUES ('095', '${a}', 57, '2014-02-01 12:18:25.330589', NULL, '2014-02-01 12:18:25.330589', NULL);
INSERT INTO bib4template.biblio_brief_formats VALUES ('500', '${a}', 28, '2014-02-01 12:13:34.913539', NULL, '2014-02-01 12:13:34.913539', NULL);
INSERT INTO bib4template.biblio_brief_formats VALUES ('504', '${a}', 29, '2014-02-01 12:13:43.050064', NULL, '2014-02-01 12:13:43.050064', NULL);
INSERT INTO bib4template.biblio_brief_formats VALUES ('505', '${a}', 30, '2014-02-01 12:13:49.402136', NULL, '2014-02-01 12:13:49.402136', NULL);
INSERT INTO bib4template.biblio_brief_formats VALUES ('521', '${a}', 31, '2014-02-01 12:13:56.762462', NULL, '2014-02-01 12:13:56.762462', NULL);
INSERT INTO bib4template.biblio_brief_formats VALUES ('534', '${a}', 32, '2014-02-01 12:14:02.570381', NULL, '2014-02-01 12:14:02.570381', NULL);
INSERT INTO bib4template.biblio_brief_formats VALUES ('590', '${a}', 33, '2014-02-01 12:14:06.138512', NULL, '2014-02-01 12:14:06.138512', NULL);
INSERT INTO bib4template.biblio_brief_formats VALUES ('502', '${a}', 34, '2014-02-01 12:14:11.876239', NULL, '2014-02-01 12:14:11.876239', NULL);
INSERT INTO bib4template.biblio_brief_formats VALUES ('506', '${a}', 35, '2014-02-01 12:14:17.131068', NULL, '2014-02-01 12:14:17.131068', NULL);
INSERT INTO bib4template.biblio_brief_formats VALUES ('876', '${h}', 36, '2014-02-01 12:14:30.355534', NULL, '2014-02-01 12:14:30.355534', NULL);
INSERT INTO bib4template.biblio_brief_formats VALUES ('080', '${a}_{ }${2}', 54, '2014-02-01 12:16:38.015323', NULL, '2014-02-01 12:16:38.015323', NULL);
INSERT INTO bib4template.biblio_brief_formats VALUES ('082', '${a}_{ }${2}', 55, '2014-02-01 12:17:24.376916', NULL, '2014-02-01 12:17:24.376916', NULL);
INSERT INTO bib4template.biblio_brief_formats VALUES ('501', '${a}', 28, '2022-12-04 11:05:57.138539', NULL, '2022-12-04 11:05:57.138539', NULL);
INSERT INTO bib4template.biblio_brief_formats VALUES ('530', '${a}', 31, '2022-12-04 11:05:57.138539', NULL, '2022-12-04 11:05:57.138539', NULL);
INSERT INTO bib4template.biblio_brief_formats VALUES ('595', '${a}', 33, '2022-12-04 11:05:57.138539', NULL, '2022-12-04 11:05:57.138539', NULL);
INSERT INTO bib4template.biblio_brief_formats VALUES ('245', '${a}_{: }${b}_{ / }${c}', 11, '2013-05-11 14:09:53.242277', NULL, '2013-05-11 14:09:53.242277', NULL);
INSERT INTO bib4template.biblio_brief_formats VALUES ('100', '${a}_{ - }${d}_{ }(${q})', 1, '2013-05-11 14:09:22.681914', NULL, '2013-05-11 14:09:22.681914', NULL);
INSERT INTO bib4template.biblio_brief_formats VALUES ('110', '${a}_{. }${b. }_{ }(${n}_{ : }${d}_{ : }${c})', 2, '2013-05-11 14:09:28.849574', NULL, '2013-05-11 14:09:28.849574', NULL);
INSERT INTO bib4template.biblio_brief_formats VALUES ('111', '${a}_{. }(${n}_{ : }${d}_{ : }${c})', 3, '2013-05-11 14:09:33.10575', NULL, '2013-05-11 14:09:33.10575', NULL);
INSERT INTO bib4template.biblio_brief_formats VALUES ('130', '${a}_{. }${l}_{. }${f}', 4, '2014-02-01 11:43:41.882279', NULL, '2014-02-01 11:43:41.882279', NULL);
INSERT INTO bib4template.biblio_brief_formats VALUES ('600', '${a}_{. }${b}_{. }${c}_{. }${d}_{ - }${x}_{ - }${y}_{ - }${z}', 37, '2014-02-01 12:05:47.099015', NULL, '2014-02-01 12:05:47.099015', NULL);
INSERT INTO bib4template.biblio_brief_formats VALUES ('610', '${a}_{. }${b}_{ - }${x}_{ - }${y}_{ - }${z}', 38, '2014-02-01 12:05:55.971671', NULL, '2014-02-01 12:05:55.971671', NULL);
INSERT INTO bib4template.biblio_brief_formats VALUES ('611', '${a}_{. }${b. }_{ }(${n}_{ : }${d}_{ : }${c})_{ - }${x}_{ - }${y}_{ - }${z}', 39, '2014-02-01 12:06:02.963596', NULL, '2014-02-01 12:06:02.963596', NULL);
INSERT INTO bib4template.biblio_brief_formats VALUES ('700', '${a}_{. }${d}', 8, '2014-02-01 11:44:15.995588', NULL, '2014-02-01 11:44:15.995588', NULL);
INSERT INTO bib4template.biblio_brief_formats VALUES ('710', '${a}_{. }${b. }_{ }(${n}_{ : }${d}_{ : }${c})', 9, '2014-02-01 11:44:21.019794', NULL, '2014-02-01 11:44:21.019794', NULL);
INSERT INTO bib4template.biblio_brief_formats VALUES ('711', '${a}_{. }${b. }_{ }(${n}_{ : }${d}_{ : }${c})', 10, '2014-02-01 11:44:27.579924', NULL, '2014-02-01 11:44:27.579924', NULL);
INSERT INTO bib4template.biblio_brief_formats VALUES ('630', '${a}_{. }(${d})', 40, '2014-02-01 12:06:13.363821', NULL, '2014-02-01 12:06:13.363821', NULL);
INSERT INTO bib4template.biblio_brief_formats VALUES ('490', '(${a}_{ ; }${v})', 26, '2014-02-01 12:13:12.984999', NULL, '2014-02-01 12:13:12.984999', NULL);
INSERT INTO bib4template.biblio_brief_formats VALUES ('830', '${a}_{. }${p}_{ ; }${v}', 16, '2014-02-01 12:09:56.027131', NULL, '2014-02-01 12:09:56.027131', NULL);


--
-- Data for Name: biblio_form_datafields; Type: TABLE DATA; Schema: bib4template; Owner: biblivre
--

INSERT INTO bib4template.biblio_form_datafields VALUES ('020', false, false, '', '', 'book', '2013-04-13 13:42:03.23405', NULL, '2013-04-13 13:42:03.23405', NULL, 20);
INSERT INTO bib4template.biblio_form_datafields VALUES ('255', false, true, '', '', 'map', '2013-04-13 13:42:03.23405', NULL, '2013-04-13 13:42:03.23405', NULL, 255);
INSERT INTO bib4template.biblio_form_datafields VALUES ('013', false, true, '', '', 'book,manuscript,pamphlet,thesis,computer_legible,map,movie,score,object_3d,photo,periodic,articles,music,nonmusical_sound', '2013-04-13 13:42:03.23405', NULL, '2013-04-13 13:42:03.23405', NULL, 13);
INSERT INTO bib4template.biblio_form_datafields VALUES ('022', false, false, '', '', 'book,periodic,articles', '2013-04-13 13:42:03.23405', NULL, '2013-04-13 13:42:03.23405', NULL, 22);
INSERT INTO bib4template.biblio_form_datafields VALUES ('029', false, true, '', '', 'score,music,nonmusical_sound', '2013-04-13 13:42:03.23405', NULL, '2013-04-13 13:42:03.23405', NULL, 29);
INSERT INTO bib4template.biblio_form_datafields VALUES ('040', false, false, '', '', 'book,manuscript,pamphlet,thesis,computer_legible,map,movie,score,photo,periodic,articles,music,nonmusical_sound', '2013-04-13 13:42:03.23405', NULL, '2013-04-13 13:42:03.23405', NULL, 40);
INSERT INTO bib4template.biblio_form_datafields VALUES ('045', false, false, '_,0,1,2', '', 'map', '2013-04-13 13:42:03.23405', NULL, '2013-04-13 13:42:03.23405', NULL, 45);
INSERT INTO bib4template.biblio_form_datafields VALUES ('080', false, false, '', '', 'book,manuscript,pamphlet,thesis,computer_legible,map,movie,score,object_3d,photo,periodic,articles,music,nonmusical_sound', '2013-04-13 13:42:03.23405', NULL, '2013-04-13 13:42:03.23405', NULL, 80);
INSERT INTO bib4template.biblio_form_datafields VALUES ('082', false, false, '', '', 'book,manuscript,pamphlet,thesis,computer_legible,map,movie,score,object_3d,photo,periodic,articles,music,nonmusical_sound', '2013-04-13 13:42:03.23405', NULL, '2013-04-13 13:42:03.23405', NULL, 82);
INSERT INTO bib4template.biblio_form_datafields VALUES ('090', false, false, '', '', 'book,manuscript,pamphlet,thesis,computer_legible,map,movie,score,object_3d,photo,periodic,articles,music,nonmusical_sound', '2013-04-13 13:42:03.23405', NULL, '2013-04-13 13:42:03.23405', NULL, 90);
INSERT INTO bib4template.biblio_form_datafields VALUES ('095', false, false, '', '', 'book,manuscript,pamphlet,thesis,computer_legible,map,movie,score,object_3d,photo,periodic,articles,music,nonmusical_sound', '2013-04-13 13:42:03.23405', NULL, '2013-04-13 13:42:03.23405', NULL, 95);
INSERT INTO bib4template.biblio_form_datafields VALUES ('100', false, false, '1,0,2,3', '', 'book,manuscript,pamphlet,thesis,computer_legible,map,movie,score,object_3d,photo,articles,music,nonmusical_sound', '2013-04-13 13:42:03.23405', NULL, '2013-04-13 13:42:03.23405', NULL, 100);
INSERT INTO bib4template.biblio_form_datafields VALUES ('110', false, false, '0,1,2', '', 'book,manuscript,pamphlet,thesis,computer_legible,map,movie,score,object_3d,photo,articles,music,nonmusical_sound', '2013-04-13 13:42:03.23405', NULL, '2013-04-13 13:42:03.23405', NULL, 110);
INSERT INTO bib4template.biblio_form_datafields VALUES ('111', false, false, '0,1,2', '', 'book,manuscript,pamphlet,thesis,computer_legible,map,movie,score,object_3d,photo,articles,music,nonmusical_sound', '2013-04-13 13:42:03.23405', NULL, '2013-04-13 13:42:03.23405', NULL, 111);
INSERT INTO bib4template.biblio_form_datafields VALUES ('245', false, false, '1,0', '0,1,2,3,4,5,6,7,8,9', 'book,manuscript,pamphlet,thesis,computer_legible,map,movie,score,object_3d,photo,periodic,articles,music,nonmusical_sound', '2013-04-13 13:42:03.23405', NULL, '2013-04-13 13:42:03.23405', NULL, 245);
INSERT INTO bib4template.biblio_form_datafields VALUES ('250', false, false, '', '', 'book,thesis', '2013-04-13 13:42:03.23405', NULL, '2013-04-13 13:42:03.23405', NULL, 250);
INSERT INTO bib4template.biblio_form_datafields VALUES ('256', false, false, '', '', 'computer_legible', '2013-04-13 13:42:03.23405', NULL, '2013-04-13 13:42:03.23405', NULL, 256);
INSERT INTO bib4template.biblio_form_datafields VALUES ('257', false, false, '', '', 'movie', '2013-04-13 13:42:03.23405', NULL, '2013-04-13 13:42:03.23405', NULL, 257);
INSERT INTO bib4template.biblio_form_datafields VALUES ('258', false, false, '', '', 'photo', '2013-04-13 13:42:03.23405', NULL, '2013-04-13 13:42:03.23405', NULL, 258);
INSERT INTO bib4template.biblio_form_datafields VALUES ('260', false, true, '', '', 'book,manuscript,thesis,computer_legible,map,movie,score,photo,periodic,articles,music,nonmusical_sound', '2013-04-13 13:42:03.23405', NULL, '2013-04-13 13:42:03.23405', NULL, 260);
INSERT INTO bib4template.biblio_form_datafields VALUES ('300', false, false, '', '', 'book,manuscript,thesis,computer_legible,map,movie,score,object_3d,photo,periodic,articles,music,nonmusical_sound', '2013-04-13 13:42:03.23405', NULL, '2013-04-13 13:42:03.23405', NULL, 300);
INSERT INTO bib4template.biblio_form_datafields VALUES ('306', false, true, '', '', 'movie,music,nonmusical_sound', '2013-04-13 13:42:03.23405', NULL, '2013-04-13 13:42:03.23405', NULL, 306);
INSERT INTO bib4template.biblio_form_datafields VALUES ('340', false, true, '', '', 'map', '2013-04-13 13:42:03.23405', NULL, '2013-04-13 13:42:03.23405', NULL, 340);
INSERT INTO bib4template.biblio_form_datafields VALUES ('342', false, true, '0,1', '0,1,2,3,4,5,6,7,8', 'map', '2013-04-13 13:42:03.23405', NULL, '2013-04-13 13:42:03.23405', NULL, 342);
INSERT INTO bib4template.biblio_form_datafields VALUES ('343', false, true, '', '', 'map', '2013-04-13 13:42:03.23405', NULL, '2013-04-13 13:42:03.23405', NULL, 343);
INSERT INTO bib4template.biblio_form_datafields VALUES ('490', false, true, '0,1', '', 'book,movie,score,object_3d,photo,periodic,articles,music,nonmusical_sound', '2013-04-13 13:42:03.23405', NULL, '2013-04-13 13:42:03.23405', NULL, 490);
INSERT INTO bib4template.biblio_form_datafields VALUES ('500', false, true, '', '', 'book,manuscript,pamphlet,thesis,computer_legible,map,movie,score,object_3d,photo,periodic,articles,music,nonmusical_sound', '2013-04-13 13:42:03.23405', NULL, '2013-04-13 13:42:03.23405', NULL, 500);
INSERT INTO bib4template.biblio_form_datafields VALUES ('501', false, true, '', '', 'book,manuscript,pamphlet,thesis,computer_legible,map,movie,score,object_3d,photo,periodic,articles,music,nonmusical_sound', '2013-04-13 13:42:03.23405', NULL, '2013-04-13 13:42:03.23405', NULL, 501);
INSERT INTO bib4template.biblio_form_datafields VALUES ('502', false, true, '', '', 'book,manuscript,pamphlet,thesis,computer_legible,map,movie,score,object_3d,photo,periodic,articles,music,nonmusical_sound', '2013-04-13 13:42:03.23405', NULL, '2013-04-13 13:42:03.23405', NULL, 502);
INSERT INTO bib4template.biblio_form_datafields VALUES ('504', false, true, '', '', 'book,manuscript,pamphlet,thesis,computer_legible,map,movie,score,object_3d,photo,periodic,articles,music,nonmusical_sound', '2013-04-13 13:42:03.23405', NULL, '2013-04-13 13:42:03.23405', NULL, 504);
INSERT INTO bib4template.biblio_form_datafields VALUES ('505', false, true, '', '', 'book,manuscript,pamphlet,thesis,computer_legible,map,movie,score,object_3d,photo,periodic,articles,music,nonmusical_sound', '2013-04-13 13:42:03.23405', NULL, '2013-04-13 13:42:03.23405', NULL, 505);
INSERT INTO bib4template.biblio_form_datafields VALUES ('520', false, true, '', '', 'book,manuscript,pamphlet,thesis,computer_legible,map,movie,score,object_3d,photo,periodic,articles,music,nonmusical_sound', '2013-04-13 13:42:03.23405', NULL, '2013-04-13 13:42:03.23405', NULL, 520);
INSERT INTO bib4template.biblio_form_datafields VALUES ('521', false, true, '', '', 'book,manuscript,pamphlet,thesis,computer_legible,map,movie,score,object_3d,photo,periodic,articles,music,nonmusical_sound', '2013-04-13 13:42:03.23405', NULL, '2013-04-13 13:42:03.23405', NULL, 521);
INSERT INTO bib4template.biblio_form_datafields VALUES ('530', false, true, '', '', 'book,manuscript,pamphlet,thesis,computer_legible,map,movie,score,object_3d,photo,periodic,articles,music,nonmusical_sound', '2013-04-13 13:42:03.23405', NULL, '2013-04-13 13:42:03.23405', NULL, 530);
INSERT INTO bib4template.biblio_form_datafields VALUES ('534', false, true, '', '', 'book,manuscript,pamphlet,thesis,computer_legible,map,movie,score,object_3d,photo,periodic,articles,music,nonmusical_sound', '2013-04-13 13:42:03.23405', NULL, '2013-04-13 13:42:03.23405', NULL, 534);
INSERT INTO bib4template.biblio_form_datafields VALUES ('590', false, true, '', '', 'book,manuscript,pamphlet,thesis,computer_legible,map,movie,score,object_3d,photo,periodic,articles,music,nonmusical_sound', '2013-04-13 13:42:03.23405', NULL, '2013-04-13 13:42:03.23405', NULL, 590);
INSERT INTO bib4template.biblio_form_datafields VALUES ('595', false, true, '', '', 'book,manuscript,pamphlet,thesis,computer_legible,map,movie,score,object_3d,photo,periodic,articles,music,nonmusical_sound', '2013-04-13 13:42:03.23405', NULL, '2013-04-13 13:42:03.23405', NULL, 595);
INSERT INTO bib4template.biblio_form_datafields VALUES ('600', false, true, '0,1,2,3', '', 'book,manuscript,pamphlet,thesis,computer_legible,map,movie,score,object_3d,photo,periodic,articles,music,nonmusical_sound', '2013-04-13 13:42:03.23405', NULL, '2013-04-13 13:42:03.23405', NULL, 600);
INSERT INTO bib4template.biblio_form_datafields VALUES ('610', false, true, '0,1,2', '', 'book,manuscript,pamphlet,thesis,computer_legible,map,movie,score,object_3d,photo,periodic,articles,music,nonmusical_sound', '2013-04-13 13:42:03.23405', NULL, '2013-04-13 13:42:03.23405', NULL, 610);
INSERT INTO bib4template.biblio_form_datafields VALUES ('611', false, true, '0,1,2', '', 'book,manuscript,pamphlet,thesis,computer_legible,map,movie,score,object_3d,photo,periodic,articles,music,nonmusical_sound', '2013-04-13 13:42:03.23405', NULL, '2013-04-13 13:42:03.23405', NULL, 611);
INSERT INTO bib4template.biblio_form_datafields VALUES ('650', false, true, '', '', 'book,manuscript,pamphlet,thesis,computer_legible,map,movie,score,object_3d,photo,periodic,articles,music,nonmusical_sound', '2013-04-13 13:42:03.23405', NULL, '2013-04-13 13:42:03.23405', NULL, 650);
INSERT INTO bib4template.biblio_form_datafields VALUES ('651', false, true, '', '', 'book,manuscript,pamphlet,thesis,computer_legible,map,movie,score,object_3d,photo,periodic,articles,music,nonmusical_sound', '2013-04-13 13:42:03.23405', NULL, '2013-04-13 13:42:03.23405', NULL, 651);
INSERT INTO bib4template.biblio_form_datafields VALUES ('700', false, true, '1,0,2,3', '_,2', 'book,manuscript,pamphlet,thesis,computer_legible,map,movie,score,object_3d,photo,periodic,articles,music,nonmusical_sound', '2013-04-13 13:42:03.23405', NULL, '2013-04-13 13:42:03.23405', NULL, 700);
INSERT INTO bib4template.biblio_form_datafields VALUES ('710', false, true, '0,1,2', '_,2', 'book,manuscript,pamphlet,thesis,computer_legible,map,movie,score,object_3d,photo,periodic,articles,music,nonmusical_sound', '2013-04-13 13:42:03.23405', NULL, '2013-04-13 13:42:03.23405', NULL, 710);
INSERT INTO bib4template.biblio_form_datafields VALUES ('711', false, true, '0,1,2', '', 'book,manuscript,pamphlet,thesis,computer_legible,map,movie,score,object_3d,photo,periodic,articles,music,nonmusical_sound', '2013-04-13 13:42:03.23405', NULL, '2013-04-13 13:42:03.23405', NULL, 711);
INSERT INTO bib4template.biblio_form_datafields VALUES ('856', false, true, '', '', 'book,manuscript,pamphlet,thesis,computer_legible,map,movie,score,object_3d,photo,periodic,articles,music,nonmusical_sound', '2013-04-13 13:42:03.23405', NULL, '2013-04-13 13:42:03.23405', NULL, 856);
INSERT INTO bib4template.biblio_form_datafields VALUES ('630', false, true, '0,1,2,3,4,5,6,7,8,9', '', 'book,manuscript,pamphlet,thesis,computer_legible,map,movie,score,object_3d,photo,periodic,articles,music,nonmusical_sound', '2013-04-13 13:42:03.23405', NULL, '2013-04-13 13:42:03.23405', NULL, 630);
INSERT INTO bib4template.biblio_form_datafields VALUES ('730', false, true, '0,1,2,3,4,5,6,7,8,9', '_,2', 'book,manuscript,pamphlet,thesis,computer_legible,map,movie,score,object_3d,photo,periodic,articles,music,nonmusical_sound', '2013-04-13 13:42:03.23405', NULL, '2013-04-13 13:42:03.23405', NULL, 730);
INSERT INTO bib4template.biblio_form_datafields VALUES ('740', false, true, '0,1,2,3,4,5,6,7,8,9', '_,2', 'book,manuscript,pamphlet,thesis,computer_legible,map,movie,score,object_3d,photo,periodic,articles,music,nonmusical_sound', '2013-04-13 13:42:03.23405', NULL, '2013-04-13 13:42:03.23405', NULL, 740);
INSERT INTO bib4template.biblio_form_datafields VALUES ('043', false, false, '', '', 'map,periodic', '2013-04-13 13:42:03.23405', NULL, '2013-04-13 13:42:03.23405', NULL, 43);
INSERT INTO bib4template.biblio_form_datafields VALUES ('830', false, true, '', '0,1,2,3,4,5,6,7,8,9', 'book,manuscript,pamphlet,thesis,computer_legible,map,movie,score,object_3d,photo,periodic,articles,music,nonmusical_sound', '2013-04-13 13:42:03.23405', NULL, '2013-04-13 13:42:03.23405', NULL, 830);
INSERT INTO bib4template.biblio_form_datafields VALUES ('041', false, true, '0,1', '', 'book,manuscript,pamphlet,thesis,computer_legible,map,movie,score,photo,periodic,articles,music,nonmusical_sound', '2013-04-13 13:42:03.23405', NULL, '2013-04-13 13:42:03.23405', NULL, 41);
INSERT INTO bib4template.biblio_form_datafields VALUES ('240', false, false, '1,0', '0,1,2,3,4,5,6,7,8,9', 'book,manuscript,pamphlet,thesis,computer_legible,map,movie,score,object_3d,photo,periodic,articles,music,nonmusical_sound', '2013-04-13 13:42:03.23405', NULL, '2013-04-13 13:42:03.23405', NULL, 240);
INSERT INTO bib4template.biblio_form_datafields VALUES ('243', false, false, '1,0', '0,1,2,3,4,5,6,7,8,9', 'book,manuscript,pamphlet,thesis,computer_legible,map,movie,score,object_3d,photo,periodic,articles,music,nonmusical_sound', '2013-04-13 13:42:03.23405', NULL, '2013-04-13 13:42:03.23405', NULL, 243);
INSERT INTO bib4template.biblio_form_datafields VALUES ('130', false, false, '0,1,2,3,4,5,6,7,8,9', '', 'book,manuscript,pamphlet,thesis,computer_legible,map,movie,score,object_3d,photo,periodic,articles,music,nonmusical_sound', '2013-04-13 13:42:03.23405', NULL, '2013-04-13 13:42:03.23405', NULL, 130);
INSERT INTO bib4template.biblio_form_datafields VALUES ('210', false, true, '0,1', '_,0', 'periodic', '2014-04-28 19:25:12.931', 1, '2014-04-28 19:25:12.931', NULL, 210);
INSERT INTO bib4template.biblio_form_datafields VALUES ('246', false, true, '0,1,2,3', '_,0,1,2,3,4,5,6,7,8', 'periodic', '2014-04-28 19:25:12.931', 1, '2014-04-28 19:25:12.931', NULL, 246);
INSERT INTO bib4template.biblio_form_datafields VALUES ('310', false, false, '', '', 'periodic', '2014-04-28 19:25:12.931', 1, '2014-04-28 19:25:12.931', NULL, 310);
INSERT INTO bib4template.biblio_form_datafields VALUES ('321', false, false, '', '', 'periodic', '2014-04-28 19:25:12.931', 1, '2014-04-28 19:25:12.931', NULL, 321);
INSERT INTO bib4template.biblio_form_datafields VALUES ('362', false, true, '0,1', '', 'periodic', '2014-04-28 19:25:12.931', 1, '2014-04-28 19:25:12.931', NULL, 362);
INSERT INTO bib4template.biblio_form_datafields VALUES ('515', false, true, '', '', 'periodic', '2014-04-28 19:25:12.931', 1, '2014-04-28 19:25:12.931', NULL, 515);
INSERT INTO bib4template.biblio_form_datafields VALUES ('525', false, true, '', '', 'periodic', '2014-04-28 19:25:12.931', 1, '2014-04-28 19:25:12.931', NULL, 525);
INSERT INTO bib4template.biblio_form_datafields VALUES ('550', false, true, '', '', 'periodic', '2014-04-28 19:25:12.931', 1, '2014-04-28 19:25:12.931', NULL, 550);
INSERT INTO bib4template.biblio_form_datafields VALUES ('555', false, true, '_,0,8', '', 'periodic', '2014-04-28 19:25:12.931', 1, '2014-04-28 19:25:12.931', NULL, 555);
INSERT INTO bib4template.biblio_form_datafields VALUES ('580', false, true, '', '', 'periodic', '2014-04-28 19:25:12.931', 1, '2014-04-28 19:25:12.931', NULL, 580);
INSERT INTO bib4template.biblio_form_datafields VALUES ('947', false, true, '', '', 'periodic', '2014-04-28 19:25:12.931', 1, '2014-04-28 19:25:12.931', NULL, 947);


--
-- Data for Name: biblio_form_subfields; Type: TABLE DATA; Schema: bib4template; Owner: biblivre
--

INSERT INTO bib4template.biblio_form_subfields VALUES ('013', 'a', false, false, '2013-04-13 13:43:11.351056', NULL, '2013-04-13 13:43:11.351056', NULL, 'disabled', 110);
INSERT INTO bib4template.biblio_form_subfields VALUES ('013', 'b', false, false, '2013-04-13 13:43:11.351056', NULL, '2013-04-13 13:43:11.351056', NULL, 'disabled', 111);
INSERT INTO bib4template.biblio_form_subfields VALUES ('013', 'c', false, false, '2013-04-13 13:43:11.351056', NULL, '2013-04-13 13:43:11.351056', NULL, 'disabled', 112);
INSERT INTO bib4template.biblio_form_subfields VALUES ('520', 'u', false, false, '2013-04-13 13:43:11.351056', NULL, '2013-04-13 13:43:11.351056', NULL, 'disabled', 637);
INSERT INTO bib4template.biblio_form_subfields VALUES ('521', 'a', false, false, '2013-04-13 13:43:11.351056', NULL, '2013-04-13 13:43:11.351056', NULL, 'disabled', 618);
INSERT INTO bib4template.biblio_form_subfields VALUES ('530', 'a', false, false, '2013-04-13 13:43:11.351056', NULL, '2013-04-13 13:43:11.351056', NULL, 'disabled', 627);
INSERT INTO bib4template.biblio_form_subfields VALUES ('534', 'a', false, false, '2013-04-13 13:43:11.351056', NULL, '2013-04-13 13:43:11.351056', NULL, 'disabled', 631);
INSERT INTO bib4template.biblio_form_subfields VALUES ('590', 'a', false, false, '2013-04-13 13:43:11.351056', NULL, '2013-04-13 13:43:11.351056', NULL, 'disabled', 687);
INSERT INTO bib4template.biblio_form_subfields VALUES ('595', 'a', false, false, '2013-04-13 13:43:11.351056', NULL, '2013-04-13 13:43:11.351056', NULL, 'disabled', 692);
INSERT INTO bib4template.biblio_form_subfields VALUES ('013', 'd', false, true, '2013-04-13 13:43:11.351056', NULL, '2013-04-13 13:43:11.351056', NULL, 'disabled', 113);
INSERT INTO bib4template.biblio_form_subfields VALUES ('013', 'e', false, true, '2013-04-13 13:43:11.351056', NULL, '2013-04-13 13:43:11.351056', NULL, 'disabled', 114);
INSERT INTO bib4template.biblio_form_subfields VALUES ('013', 'f', false, true, '2013-04-13 13:43:11.351056', NULL, '2013-04-13 13:43:11.351056', NULL, 'disabled', 115);
INSERT INTO bib4template.biblio_form_subfields VALUES ('020', 'a', false, false, '2013-04-13 13:43:11.351056', NULL, '2013-04-13 13:43:11.351056', NULL, 'disabled', 117);
INSERT INTO bib4template.biblio_form_subfields VALUES ('022', 'a', false, false, '2013-04-13 13:43:11.351056', NULL, '2013-04-13 13:43:11.351056', NULL, 'disabled', 119);
INSERT INTO bib4template.biblio_form_subfields VALUES ('029', 'a', false, false, '2013-04-13 13:43:11.351056', NULL, '2013-04-13 13:43:11.351056', NULL, 'disabled', 126);
INSERT INTO bib4template.biblio_form_subfields VALUES ('040', 'a', false, false, '2013-04-13 13:43:11.351056', NULL, '2013-04-13 13:43:11.351056', NULL, 'disabled', 137);
INSERT INTO bib4template.biblio_form_subfields VALUES ('040', 'b', false, false, '2013-04-13 13:43:11.351056', NULL, '2013-04-13 13:43:11.351056', NULL, 'disabled', 138);
INSERT INTO bib4template.biblio_form_subfields VALUES ('041', 'a', false, false, '2013-04-13 13:43:11.351056', NULL, '2013-04-13 13:43:11.351056', NULL, 'disabled', 138);
INSERT INTO bib4template.biblio_form_subfields VALUES ('041', 'b', false, false, '2013-04-13 13:43:11.351056', NULL, '2013-04-13 13:43:11.351056', NULL, 'disabled', 139);
INSERT INTO bib4template.biblio_form_subfields VALUES ('041', 'h', false, true, '2013-04-13 13:43:11.351056', NULL, '2013-04-13 13:43:11.351056', NULL, 'disabled', 145);
INSERT INTO bib4template.biblio_form_subfields VALUES ('043', 'a', false, true, '2013-04-13 13:43:11.351056', NULL, '2013-04-13 13:43:11.351056', NULL, 'disabled', 140);
INSERT INTO bib4template.biblio_form_subfields VALUES ('045', 'a', false, true, '2013-04-13 13:43:11.351056', NULL, '2013-04-13 13:43:11.351056', NULL, 'disabled', 142);
INSERT INTO bib4template.biblio_form_subfields VALUES ('045', 'b', false, true, '2013-04-13 13:43:11.351056', NULL, '2013-04-13 13:43:11.351056', NULL, 'disabled', 143);
INSERT INTO bib4template.biblio_form_subfields VALUES ('045', 'c', false, true, '2013-04-13 13:43:11.351056', NULL, '2013-04-13 13:43:11.351056', NULL, 'disabled', 144);
INSERT INTO bib4template.biblio_form_subfields VALUES ('080', '2', false, false, '2013-04-13 13:43:11.351056', NULL, '2013-04-13 13:43:11.351056', NULL, 'disabled', 130);
INSERT INTO bib4template.biblio_form_subfields VALUES ('080', 'a', false, true, '2013-04-13 13:43:11.351056', NULL, '2013-04-13 13:43:11.351056', NULL, 'disabled', 177);
INSERT INTO bib4template.biblio_form_subfields VALUES ('082', '2', false, false, '2013-04-13 13:43:11.351056', NULL, '2013-04-13 13:43:11.351056', NULL, 'disabled', 132);
INSERT INTO bib4template.biblio_form_subfields VALUES ('082', 'a', false, true, '2013-04-13 13:43:11.351056', NULL, '2013-04-13 13:43:11.351056', NULL, 'disabled', 179);
INSERT INTO bib4template.biblio_form_subfields VALUES ('090', 'a', false, false, '2013-04-13 13:43:11.351056', NULL, '2013-04-13 13:43:11.351056', NULL, 'disabled', 187);
INSERT INTO bib4template.biblio_form_subfields VALUES ('090', 'b', false, false, '2013-04-13 13:43:11.351056', NULL, '2013-04-13 13:43:11.351056', NULL, 'disabled', 188);
INSERT INTO bib4template.biblio_form_subfields VALUES ('090', 'c', false, false, '2013-04-13 13:43:11.351056', NULL, '2013-04-13 13:43:11.351056', NULL, 'disabled', 189);
INSERT INTO bib4template.biblio_form_subfields VALUES ('095', 'a', false, false, '2013-04-13 13:43:11.351056', NULL, '2013-04-13 13:43:11.351056', NULL, 'fixed_table', 192);
INSERT INTO bib4template.biblio_form_subfields VALUES ('100', 'b', false, false, '2013-04-13 13:43:11.351056', NULL, '2013-04-13 13:43:11.351056', NULL, 'disabled', 198);
INSERT INTO bib4template.biblio_form_subfields VALUES ('100', 'c', false, true, '2013-04-13 13:43:11.351056', NULL, '2013-04-13 13:43:11.351056', NULL, 'disabled', 199);
INSERT INTO bib4template.biblio_form_subfields VALUES ('100', 'd', false, false, '2013-04-13 13:43:11.351056', NULL, '2013-04-13 13:43:11.351056', NULL, 'disabled', 200);
INSERT INTO bib4template.biblio_form_subfields VALUES ('100', 'q', false, false, '2013-04-13 13:43:11.351056', NULL, '2013-04-13 13:43:11.351056', NULL, 'disabled', 213);
INSERT INTO bib4template.biblio_form_subfields VALUES ('110', 'b', false, true, '2013-04-13 13:43:11.351056', NULL, '2013-04-13 13:43:11.351056', NULL, 'disabled', 208);
INSERT INTO bib4template.biblio_form_subfields VALUES ('110', 'c', false, false, '2013-04-13 13:43:11.351056', NULL, '2013-04-13 13:43:11.351056', NULL, 'disabled', 209);
INSERT INTO bib4template.biblio_form_subfields VALUES ('110', 'd', false, true, '2013-04-13 13:43:11.351056', NULL, '2013-04-13 13:43:11.351056', NULL, 'disabled', 210);
INSERT INTO bib4template.biblio_form_subfields VALUES ('110', 'l', false, false, '2013-04-13 13:43:11.351056', NULL, '2013-04-13 13:43:11.351056', NULL, 'disabled', 218);
INSERT INTO bib4template.biblio_form_subfields VALUES ('110', 'n', false, true, '2013-04-13 13:43:11.351056', NULL, '2013-04-13 13:43:11.351056', NULL, 'disabled', 220);
INSERT INTO bib4template.biblio_form_subfields VALUES ('111', 'c', false, false, '2013-04-13 13:43:11.351056', NULL, '2013-04-13 13:43:11.351056', NULL, 'disabled', 210);
INSERT INTO bib4template.biblio_form_subfields VALUES ('111', 'd', false, false, '2013-04-13 13:43:11.351056', NULL, '2013-04-13 13:43:11.351056', NULL, 'disabled', 211);
INSERT INTO bib4template.biblio_form_subfields VALUES ('111', 'e', false, true, '2013-04-13 13:43:11.351056', NULL, '2013-04-13 13:43:11.351056', NULL, 'disabled', 212);
INSERT INTO bib4template.biblio_form_subfields VALUES ('111', 'g', false, false, '2013-04-13 13:43:11.351056', NULL, '2013-04-13 13:43:11.351056', NULL, 'disabled', 214);
INSERT INTO bib4template.biblio_form_subfields VALUES ('111', 'k', false, true, '2013-04-13 13:43:11.351056', NULL, '2013-04-13 13:43:11.351056', NULL, 'disabled', 218);
INSERT INTO bib4template.biblio_form_subfields VALUES ('111', 'n', false, true, '2013-04-13 13:43:11.351056', NULL, '2013-04-13 13:43:11.351056', NULL, 'disabled', 221);
INSERT INTO bib4template.biblio_form_subfields VALUES ('130', 'a', false, false, '2013-04-13 13:43:11.351056', NULL, '2013-04-13 13:43:11.351056', NULL, 'disabled', 227);
INSERT INTO bib4template.biblio_form_subfields VALUES ('130', 'd', false, true, '2013-04-13 13:43:11.351056', NULL, '2013-04-13 13:43:11.351056', NULL, 'disabled', 230);
INSERT INTO bib4template.biblio_form_subfields VALUES ('130', 'f', false, false, '2013-04-13 13:43:11.351056', NULL, '2013-04-13 13:43:11.351056', NULL, 'disabled', 232);
INSERT INTO bib4template.biblio_form_subfields VALUES ('130', 'g', false, false, '2013-04-13 13:43:11.351056', NULL, '2013-04-13 13:43:11.351056', NULL, 'disabled', 233);
INSERT INTO bib4template.biblio_form_subfields VALUES ('130', 'k', false, true, '2013-04-13 13:43:11.351056', NULL, '2013-04-13 13:43:11.351056', NULL, 'disabled', 237);
INSERT INTO bib4template.biblio_form_subfields VALUES ('130', 'l', false, false, '2013-04-13 13:43:11.351056', NULL, '2013-04-13 13:43:11.351056', NULL, 'disabled', 238);
INSERT INTO bib4template.biblio_form_subfields VALUES ('130', 'p', false, true, '2013-04-13 13:43:11.351056', NULL, '2013-04-13 13:43:11.351056', NULL, 'disabled', 242);
INSERT INTO bib4template.biblio_form_subfields VALUES ('240', 'a', false, false, '2013-04-13 13:43:11.351056', NULL, '2013-04-13 13:43:11.351056', NULL, 'disabled', 337);
INSERT INTO bib4template.biblio_form_subfields VALUES ('240', 'b', false, true, '2013-04-13 13:43:11.351056', NULL, '2013-04-13 13:43:11.351056', NULL, 'disabled', 338);
INSERT INTO bib4template.biblio_form_subfields VALUES ('240', 'f', false, false, '2013-04-13 13:43:11.351056', NULL, '2013-04-13 13:43:11.351056', NULL, 'disabled', 342);
INSERT INTO bib4template.biblio_form_subfields VALUES ('243', 'f', false, false, '2013-04-13 13:43:11.351056', NULL, '2013-04-13 13:43:11.351056', NULL, 'disabled', 345);
INSERT INTO bib4template.biblio_form_subfields VALUES ('243', 'g', false, false, '2013-04-13 13:43:11.351056', NULL, '2013-04-13 13:43:11.351056', NULL, 'disabled', 346);
INSERT INTO bib4template.biblio_form_subfields VALUES ('243', 'k', false, true, '2013-04-13 13:43:11.351056', NULL, '2013-04-13 13:43:11.351056', NULL, 'disabled', 350);
INSERT INTO bib4template.biblio_form_subfields VALUES ('243', 'l', false, false, '2013-04-13 13:43:11.351056', NULL, '2013-04-13 13:43:11.351056', NULL, 'disabled', 351);
INSERT INTO bib4template.biblio_form_subfields VALUES ('245', 'a', false, false, '2013-04-13 13:43:11.351056', NULL, '2013-04-13 13:43:11.351056', NULL, 'disabled', 342);
INSERT INTO bib4template.biblio_form_subfields VALUES ('245', 'b', false, false, '2013-04-13 13:43:11.351056', NULL, '2013-04-13 13:43:11.351056', NULL, 'disabled', 343);
INSERT INTO bib4template.biblio_form_subfields VALUES ('245', 'c', false, true, '2013-04-13 13:43:11.351056', NULL, '2013-04-13 13:43:11.351056', NULL, 'disabled', 344);
INSERT INTO bib4template.biblio_form_subfields VALUES ('245', 'h', false, false, '2013-04-13 13:43:11.351056', NULL, '2013-04-13 13:43:11.351056', NULL, 'disabled', 349);
INSERT INTO bib4template.biblio_form_subfields VALUES ('245', 'n', false, true, '2013-04-13 13:43:11.351056', NULL, '2013-04-13 13:43:11.351056', NULL, 'disabled', 355);
INSERT INTO bib4template.biblio_form_subfields VALUES ('245', 'p', false, true, '2013-04-13 13:43:11.351056', NULL, '2013-04-13 13:43:11.351056', NULL, 'disabled', 357);
INSERT INTO bib4template.biblio_form_subfields VALUES ('250', 'a', false, false, '2013-04-13 13:43:11.351056', NULL, '2013-04-13 13:43:11.351056', NULL, 'disabled', 347);
INSERT INTO bib4template.biblio_form_subfields VALUES ('250', 'b', false, false, '2013-04-13 13:43:11.351056', NULL, '2013-04-13 13:43:11.351056', NULL, 'disabled', 348);
INSERT INTO bib4template.biblio_form_subfields VALUES ('255', 'a', false, false, '2013-04-13 13:43:11.351056', NULL, '2013-04-13 13:43:11.351056', NULL, 'disabled', 352);
INSERT INTO bib4template.biblio_form_subfields VALUES ('256', 'a', false, false, '2013-04-13 13:43:11.351056', NULL, '2013-04-13 13:43:11.351056', NULL, 'disabled', 353);
INSERT INTO bib4template.biblio_form_subfields VALUES ('257', 'a', false, false, '2013-04-13 13:43:11.351056', NULL, '2013-04-13 13:43:11.351056', NULL, 'disabled', 354);
INSERT INTO bib4template.biblio_form_subfields VALUES ('258', 'a', false, false, '2013-04-13 13:43:11.351056', NULL, '2013-04-13 13:43:11.351056', NULL, 'disabled', 355);
INSERT INTO bib4template.biblio_form_subfields VALUES ('258', 'b', false, false, '2013-04-13 13:43:11.351056', NULL, '2013-04-13 13:43:11.351056', NULL, 'disabled', 356);
INSERT INTO bib4template.biblio_form_subfields VALUES ('260', 'a', false, true, '2013-04-13 13:43:11.351056', NULL, '2013-04-13 13:43:11.351056', NULL, 'disabled', 357);
INSERT INTO bib4template.biblio_form_subfields VALUES ('260', 'b', false, true, '2013-04-13 13:43:11.351056', NULL, '2013-04-13 13:43:11.351056', NULL, 'disabled', 358);
INSERT INTO bib4template.biblio_form_subfields VALUES ('260', 'c', false, true, '2013-04-13 13:43:11.351056', NULL, '2013-04-13 13:43:11.351056', NULL, 'disabled', 359);
INSERT INTO bib4template.biblio_form_subfields VALUES ('243', 'a', false, false, '2013-04-13 13:43:11.351056', NULL, '2013-04-13 13:43:11.351056', NULL, 'disabled', 340);
INSERT INTO bib4template.biblio_form_subfields VALUES ('240', 'p', false, true, '2013-04-13 13:43:11.351056', NULL, '2013-04-13 13:43:11.351056', NULL, 'disabled', 352);
INSERT INTO bib4template.biblio_form_subfields VALUES ('240', 'n', false, true, '2013-04-13 13:43:11.351056', NULL, '2013-04-13 13:43:11.351056', NULL, 'disabled', 350);
INSERT INTO bib4template.biblio_form_subfields VALUES ('240', 'l', false, false, '2013-04-13 13:43:11.351056', NULL, '2013-04-13 13:43:11.351056', NULL, 'disabled', 348);
INSERT INTO bib4template.biblio_form_subfields VALUES ('240', 'k', false, true, '2013-04-13 13:43:11.351056', NULL, '2013-04-13 13:43:11.351056', NULL, 'disabled', 347);
INSERT INTO bib4template.biblio_form_subfields VALUES ('240', 'g', false, false, '2013-04-13 13:43:11.351056', NULL, '2013-04-13 13:43:11.351056', NULL, 'disabled', 343);
INSERT INTO bib4template.biblio_form_subfields VALUES ('260', 'e', false, false, '2013-04-13 13:43:11.351056', NULL, '2013-04-13 13:43:11.351056', NULL, 'disabled', 361);
INSERT INTO bib4template.biblio_form_subfields VALUES ('260', 'f', false, false, '2013-04-13 13:43:11.351056', NULL, '2013-04-13 13:43:11.351056', NULL, 'disabled', 362);
INSERT INTO bib4template.biblio_form_subfields VALUES ('260', 'g', false, false, '2013-04-13 13:43:11.351056', NULL, '2013-04-13 13:43:11.351056', NULL, 'disabled', 363);
INSERT INTO bib4template.biblio_form_subfields VALUES ('300', 'a', false, true, '2013-04-13 13:43:11.351056', NULL, '2013-04-13 13:43:11.351056', NULL, 'disabled', 397);
INSERT INTO bib4template.biblio_form_subfields VALUES ('300', 'b', false, false, '2013-04-13 13:43:11.351056', NULL, '2013-04-13 13:43:11.351056', NULL, 'disabled', 398);
INSERT INTO bib4template.biblio_form_subfields VALUES ('300', 'c', false, true, '2013-04-13 13:43:11.351056', NULL, '2013-04-13 13:43:11.351056', NULL, 'disabled', 399);
INSERT INTO bib4template.biblio_form_subfields VALUES ('300', 'e', false, false, '2013-04-13 13:43:11.351056', NULL, '2013-04-13 13:43:11.351056', NULL, 'disabled', 401);
INSERT INTO bib4template.biblio_form_subfields VALUES ('306', 'a', false, false, '2013-04-13 13:43:11.351056', NULL, '2013-04-13 13:43:11.351056', NULL, 'disabled', 403);
INSERT INTO bib4template.biblio_form_subfields VALUES ('340', 'a', false, false, '2013-04-13 13:43:11.351056', NULL, '2013-04-13 13:43:11.351056', NULL, 'disabled', 437);
INSERT INTO bib4template.biblio_form_subfields VALUES ('340', 'b', false, false, '2013-04-13 13:43:11.351056', NULL, '2013-04-13 13:43:11.351056', NULL, 'disabled', 438);
INSERT INTO bib4template.biblio_form_subfields VALUES ('340', 'c', false, false, '2013-04-13 13:43:11.351056', NULL, '2013-04-13 13:43:11.351056', NULL, 'disabled', 439);
INSERT INTO bib4template.biblio_form_subfields VALUES ('340', 'd', false, false, '2013-04-13 13:43:11.351056', NULL, '2013-04-13 13:43:11.351056', NULL, 'disabled', 440);
INSERT INTO bib4template.biblio_form_subfields VALUES ('340', 'e', false, true, '2013-04-13 13:43:11.351056', NULL, '2013-04-13 13:43:11.351056', NULL, 'disabled', 441);
INSERT INTO bib4template.biblio_form_subfields VALUES ('342', 'a', false, false, '2013-04-13 13:43:11.351056', NULL, '2013-04-13 13:43:11.351056', NULL, 'disabled', 439);
INSERT INTO bib4template.biblio_form_subfields VALUES ('342', 'b', false, false, '2013-04-13 13:43:11.351056', NULL, '2013-04-13 13:43:11.351056', NULL, 'disabled', 440);
INSERT INTO bib4template.biblio_form_subfields VALUES ('342', 'c', false, false, '2013-04-13 13:43:11.351056', NULL, '2013-04-13 13:43:11.351056', NULL, 'disabled', 441);
INSERT INTO bib4template.biblio_form_subfields VALUES ('342', 'd', false, false, '2013-04-13 13:43:11.351056', NULL, '2013-04-13 13:43:11.351056', NULL, 'disabled', 442);
INSERT INTO bib4template.biblio_form_subfields VALUES ('343', 'a', false, false, '2013-04-13 13:43:11.351056', NULL, '2013-04-13 13:43:11.351056', NULL, 'disabled', 440);
INSERT INTO bib4template.biblio_form_subfields VALUES ('343', 'b', false, false, '2013-04-13 13:43:11.351056', NULL, '2013-04-13 13:43:11.351056', NULL, 'disabled', 441);
INSERT INTO bib4template.biblio_form_subfields VALUES ('490', 'a', false, false, '2013-04-13 13:43:11.351056', NULL, '2013-04-13 13:43:11.351056', NULL, 'disabled', 587);
INSERT INTO bib4template.biblio_form_subfields VALUES ('490', 'v', false, false, '2013-04-13 13:43:11.351056', NULL, '2013-04-13 13:43:11.351056', NULL, 'disabled', 608);
INSERT INTO bib4template.biblio_form_subfields VALUES ('500', 'a', false, false, '2013-04-13 13:43:11.351056', NULL, '2013-04-13 13:43:11.351056', NULL, 'disabled', 597);
INSERT INTO bib4template.biblio_form_subfields VALUES ('501', 'a', false, false, '2013-04-13 13:43:11.351056', NULL, '2013-04-13 13:43:11.351056', NULL, 'disabled', 598);
INSERT INTO bib4template.biblio_form_subfields VALUES ('502', 'a', false, false, '2013-04-13 13:43:11.351056', NULL, '2013-04-13 13:43:11.351056', NULL, 'disabled', 599);
INSERT INTO bib4template.biblio_form_subfields VALUES ('504', 'a', false, false, '2013-04-13 13:43:11.351056', NULL, '2013-04-13 13:43:11.351056', NULL, 'disabled', 601);
INSERT INTO bib4template.biblio_form_subfields VALUES ('505', 'a', false, false, '2013-04-13 13:43:11.351056', NULL, '2013-04-13 13:43:11.351056', NULL, 'disabled', 602);
INSERT INTO bib4template.biblio_form_subfields VALUES ('520', 'a', false, false, '2013-04-13 13:43:11.351056', NULL, '2013-04-13 13:43:11.351056', NULL, 'disabled', 617);
INSERT INTO bib4template.biblio_form_subfields VALUES ('595', 'b', false, false, '2013-04-13 13:43:11.351056', NULL, '2013-04-13 13:43:11.351056', NULL, 'disabled', 693);
INSERT INTO bib4template.biblio_form_subfields VALUES ('600', 'b', false, false, '2013-04-13 13:43:11.351056', NULL, '2013-04-13 13:43:11.351056', NULL, 'disabled', 698);
INSERT INTO bib4template.biblio_form_subfields VALUES ('600', 'c', false, true, '2013-04-13 13:43:11.351056', NULL, '2013-04-13 13:43:11.351056', NULL, 'disabled', 699);
INSERT INTO bib4template.biblio_form_subfields VALUES ('600', 'd', false, false, '2013-04-13 13:43:11.351056', NULL, '2013-04-13 13:43:11.351056', NULL, 'disabled', 700);
INSERT INTO bib4template.biblio_form_subfields VALUES ('600', 'k', false, true, '2013-04-13 13:43:11.351056', NULL, '2013-04-13 13:43:11.351056', NULL, 'disabled', 707);
INSERT INTO bib4template.biblio_form_subfields VALUES ('600', 'q', false, false, '2013-04-13 13:43:11.351056', NULL, '2013-04-13 13:43:11.351056', NULL, 'disabled', 713);
INSERT INTO bib4template.biblio_form_subfields VALUES ('600', 't', false, false, '2013-04-13 13:43:11.351056', NULL, '2013-04-13 13:43:11.351056', NULL, 'disabled', 716);
INSERT INTO bib4template.biblio_form_subfields VALUES ('600', 'x', false, true, '2013-04-13 13:43:11.351056', NULL, '2013-04-13 13:43:11.351056', NULL, 'disabled', 720);
INSERT INTO bib4template.biblio_form_subfields VALUES ('600', 'y', false, true, '2013-04-13 13:43:11.351056', NULL, '2013-04-13 13:43:11.351056', NULL, 'disabled', 721);
INSERT INTO bib4template.biblio_form_subfields VALUES ('600', 'z', false, true, '2013-04-13 13:43:11.351056', NULL, '2013-04-13 13:43:11.351056', NULL, 'disabled', 722);
INSERT INTO bib4template.biblio_form_subfields VALUES ('610', 'b', false, true, '2013-04-13 13:43:11.351056', NULL, '2013-04-13 13:43:11.351056', NULL, 'disabled', 708);
INSERT INTO bib4template.biblio_form_subfields VALUES ('610', 'c', false, true, '2013-04-13 13:43:11.351056', NULL, '2013-04-13 13:43:11.351056', NULL, 'disabled', 709);
INSERT INTO bib4template.biblio_form_subfields VALUES ('610', 'd', false, true, '2013-04-13 13:43:11.351056', NULL, '2013-04-13 13:43:11.351056', NULL, 'disabled', 710);
INSERT INTO bib4template.biblio_form_subfields VALUES ('610', 'g', false, false, '2013-04-13 13:43:11.351056', NULL, '2013-04-13 13:43:11.351056', NULL, 'disabled', 713);
INSERT INTO bib4template.biblio_form_subfields VALUES ('610', 'k', false, true, '2013-04-13 13:43:11.351056', NULL, '2013-04-13 13:43:11.351056', NULL, 'disabled', 717);
INSERT INTO bib4template.biblio_form_subfields VALUES ('610', 'l', false, false, '2013-04-13 13:43:11.351056', NULL, '2013-04-13 13:43:11.351056', NULL, 'disabled', 718);
INSERT INTO bib4template.biblio_form_subfields VALUES ('610', 'n', false, true, '2013-04-13 13:43:11.351056', NULL, '2013-04-13 13:43:11.351056', NULL, 'disabled', 720);
INSERT INTO bib4template.biblio_form_subfields VALUES ('610', 't', false, true, '2013-04-13 13:43:11.351056', NULL, '2013-04-13 13:43:11.351056', NULL, 'disabled', 726);
INSERT INTO bib4template.biblio_form_subfields VALUES ('610', 'x', false, true, '2013-04-13 13:43:11.351056', NULL, '2013-04-13 13:43:11.351056', NULL, 'disabled', 730);
INSERT INTO bib4template.biblio_form_subfields VALUES ('610', 'y', false, true, '2013-04-13 13:43:11.351056', NULL, '2013-04-13 13:43:11.351056', NULL, 'disabled', 731);
INSERT INTO bib4template.biblio_form_subfields VALUES ('610', 'z', false, true, '2013-04-13 13:43:11.351056', NULL, '2013-04-13 13:43:11.351056', NULL, 'disabled', 732);
INSERT INTO bib4template.biblio_form_subfields VALUES ('611', 'c', false, false, '2013-04-13 13:43:11.351056', NULL, '2013-04-13 13:43:11.351056', NULL, 'disabled', 710);
INSERT INTO bib4template.biblio_form_subfields VALUES ('611', 'd', false, true, '2013-04-13 13:43:11.351056', NULL, '2013-04-13 13:43:11.351056', NULL, 'disabled', 711);
INSERT INTO bib4template.biblio_form_subfields VALUES ('611', 'e', false, false, '2013-04-13 13:43:11.351056', NULL, '2013-04-13 13:43:11.351056', NULL, 'disabled', 712);
INSERT INTO bib4template.biblio_form_subfields VALUES ('611', 'n', false, true, '2013-04-13 13:43:11.351056', NULL, '2013-04-13 13:43:11.351056', NULL, 'disabled', 721);
INSERT INTO bib4template.biblio_form_subfields VALUES ('611', 't', false, false, '2013-04-13 13:43:11.351056', NULL, '2013-04-13 13:43:11.351056', NULL, 'disabled', 727);
INSERT INTO bib4template.biblio_form_subfields VALUES ('611', 'x', false, true, '2013-04-13 13:43:11.351056', NULL, '2013-04-13 13:43:11.351056', NULL, 'disabled', 731);
INSERT INTO bib4template.biblio_form_subfields VALUES ('611', 'y', false, true, '2013-04-13 13:43:11.351056', NULL, '2013-04-13 13:43:11.351056', NULL, 'disabled', 732);
INSERT INTO bib4template.biblio_form_subfields VALUES ('611', 'z', false, true, '2013-04-13 13:43:11.351056', NULL, '2013-04-13 13:43:11.351056', NULL, 'disabled', 733);
INSERT INTO bib4template.biblio_form_subfields VALUES ('630', 'd', false, true, '2013-04-13 13:43:11.351056', NULL, '2013-04-13 13:43:11.351056', NULL, 'disabled', 730);
INSERT INTO bib4template.biblio_form_subfields VALUES ('630', 'f', false, false, '2013-04-13 13:43:11.351056', NULL, '2013-04-13 13:43:11.351056', NULL, 'disabled', 732);
INSERT INTO bib4template.biblio_form_subfields VALUES ('630', 'g', false, false, '2013-04-13 13:43:11.351056', NULL, '2013-04-13 13:43:11.351056', NULL, 'disabled', 733);
INSERT INTO bib4template.biblio_form_subfields VALUES ('630', 'k', false, true, '2013-04-13 13:43:11.351056', NULL, '2013-04-13 13:43:11.351056', NULL, 'disabled', 737);
INSERT INTO bib4template.biblio_form_subfields VALUES ('630', 'l', false, false, '2013-04-13 13:43:11.351056', NULL, '2013-04-13 13:43:11.351056', NULL, 'disabled', 738);
INSERT INTO bib4template.biblio_form_subfields VALUES ('630', 'p', false, true, '2013-04-13 13:43:11.351056', NULL, '2013-04-13 13:43:11.351056', NULL, 'disabled', 742);
INSERT INTO bib4template.biblio_form_subfields VALUES ('630', 'x', false, true, '2013-04-13 13:43:11.351056', NULL, '2013-04-13 13:43:11.351056', NULL, 'disabled', 750);
INSERT INTO bib4template.biblio_form_subfields VALUES ('630', 'y', false, true, '2013-04-13 13:43:11.351056', NULL, '2013-04-13 13:43:11.351056', NULL, 'disabled', 751);
INSERT INTO bib4template.biblio_form_subfields VALUES ('630', 'z', false, true, '2013-04-13 13:43:11.351056', NULL, '2013-04-13 13:43:11.351056', NULL, 'disabled', 752);
INSERT INTO bib4template.biblio_form_subfields VALUES ('650', 'x', false, true, '2013-04-13 13:43:11.351056', NULL, '2013-04-13 13:43:11.351056', NULL, 'disabled', 770);
INSERT INTO bib4template.biblio_form_subfields VALUES ('650', 'y', false, true, '2013-04-13 13:43:11.351056', NULL, '2013-04-13 13:43:11.351056', NULL, 'disabled', 771);
INSERT INTO bib4template.biblio_form_subfields VALUES ('650', 'z', false, true, '2013-04-13 13:43:11.351056', NULL, '2013-04-13 13:43:11.351056', NULL, 'disabled', 772);
INSERT INTO bib4template.biblio_form_subfields VALUES ('651', 'x', false, true, '2013-04-13 13:43:11.351056', NULL, '2013-04-13 13:43:11.351056', NULL, 'disabled', 771);
INSERT INTO bib4template.biblio_form_subfields VALUES ('651', 'y', false, true, '2013-04-13 13:43:11.351056', NULL, '2013-04-13 13:43:11.351056', NULL, 'disabled', 772);
INSERT INTO bib4template.biblio_form_subfields VALUES ('651', 'z', false, true, '2013-04-13 13:43:11.351056', NULL, '2013-04-13 13:43:11.351056', NULL, 'disabled', 773);
INSERT INTO bib4template.biblio_form_subfields VALUES ('700', 'a', false, false, '2013-04-13 13:43:11.351056', NULL, '2013-04-13 13:43:11.351056', NULL, 'disabled', 797);
INSERT INTO bib4template.biblio_form_subfields VALUES ('700', 'b', false, false, '2013-04-13 13:43:11.351056', NULL, '2013-04-13 13:43:11.351056', NULL, 'disabled', 798);
INSERT INTO bib4template.biblio_form_subfields VALUES ('700', 'c', false, true, '2013-04-13 13:43:11.351056', NULL, '2013-04-13 13:43:11.351056', NULL, 'disabled', 799);
INSERT INTO bib4template.biblio_form_subfields VALUES ('700', 'd', false, false, '2013-04-13 13:43:11.351056', NULL, '2013-04-13 13:43:11.351056', NULL, 'disabled', 800);
INSERT INTO bib4template.biblio_form_subfields VALUES ('700', 'e', false, true, '2013-04-13 13:43:11.351056', NULL, '2013-04-13 13:43:11.351056', NULL, 'disabled', 801);
INSERT INTO bib4template.biblio_form_subfields VALUES ('700', 'l', false, false, '2013-04-13 13:43:11.351056', NULL, '2013-04-13 13:43:11.351056', NULL, 'disabled', 808);
INSERT INTO bib4template.biblio_form_subfields VALUES ('700', 'q', false, false, '2013-04-13 13:43:11.351056', NULL, '2013-04-13 13:43:11.351056', NULL, 'disabled', 813);
INSERT INTO bib4template.biblio_form_subfields VALUES ('700', 't', false, false, '2013-04-13 13:43:11.351056', NULL, '2013-04-13 13:43:11.351056', NULL, 'disabled', 816);
INSERT INTO bib4template.biblio_form_subfields VALUES ('710', 'a', false, false, '2013-04-13 13:43:11.351056', NULL, '2013-04-13 13:43:11.351056', NULL, 'disabled', 807);
INSERT INTO bib4template.biblio_form_subfields VALUES ('710', 'b', false, true, '2013-04-13 13:43:11.351056', NULL, '2013-04-13 13:43:11.351056', NULL, 'disabled', 808);
INSERT INTO bib4template.biblio_form_subfields VALUES ('710', 'c', false, false, '2013-04-13 13:43:11.351056', NULL, '2013-04-13 13:43:11.351056', NULL, 'disabled', 809);
INSERT INTO bib4template.biblio_form_subfields VALUES ('710', 'd', false, true, '2013-04-13 13:43:11.351056', NULL, '2013-04-13 13:43:11.351056', NULL, 'disabled', 810);
INSERT INTO bib4template.biblio_form_subfields VALUES ('710', 'g', false, false, '2013-04-13 13:43:11.351056', NULL, '2013-04-13 13:43:11.351056', NULL, 'disabled', 813);
INSERT INTO bib4template.biblio_form_subfields VALUES ('710', 'l', false, false, '2013-04-13 13:43:11.351056', NULL, '2013-04-13 13:43:11.351056', NULL, 'disabled', 818);
INSERT INTO bib4template.biblio_form_subfields VALUES ('710', 'n', false, true, '2013-04-13 13:43:11.351056', NULL, '2013-04-13 13:43:11.351056', NULL, 'disabled', 820);
INSERT INTO bib4template.biblio_form_subfields VALUES ('710', 't', false, false, '2013-04-13 13:43:11.351056', NULL, '2013-04-13 13:43:11.351056', NULL, 'disabled', 826);
INSERT INTO bib4template.biblio_form_subfields VALUES ('711', 'a', false, false, '2013-04-13 13:43:11.351056', NULL, '2013-04-13 13:43:11.351056', NULL, 'disabled', 808);
INSERT INTO bib4template.biblio_form_subfields VALUES ('711', 'c', false, false, '2013-04-13 13:43:11.351056', NULL, '2013-04-13 13:43:11.351056', NULL, 'disabled', 810);
INSERT INTO bib4template.biblio_form_subfields VALUES ('711', 'd', false, false, '2013-04-13 13:43:11.351056', NULL, '2013-04-13 13:43:11.351056', NULL, 'disabled', 811);
INSERT INTO bib4template.biblio_form_subfields VALUES ('711', 'e', false, true, '2013-04-13 13:43:11.351056', NULL, '2013-04-13 13:43:11.351056', NULL, 'disabled', 812);
INSERT INTO bib4template.biblio_form_subfields VALUES ('711', 'g', false, false, '2013-04-13 13:43:11.351056', NULL, '2013-04-13 13:43:11.351056', NULL, 'disabled', 814);
INSERT INTO bib4template.biblio_form_subfields VALUES ('711', 'k', false, true, '2013-04-13 13:43:11.351056', NULL, '2013-04-13 13:43:11.351056', NULL, 'disabled', 818);
INSERT INTO bib4template.biblio_form_subfields VALUES ('711', 'n', false, true, '2013-04-13 13:43:11.351056', NULL, '2013-04-13 13:43:11.351056', NULL, 'disabled', 821);
INSERT INTO bib4template.biblio_form_subfields VALUES ('711', 't', false, false, '2013-04-13 13:43:11.351056', NULL, '2013-04-13 13:43:11.351056', NULL, 'disabled', 827);
INSERT INTO bib4template.biblio_form_subfields VALUES ('730', 'a', false, false, '2013-04-13 13:43:11.351056', NULL, '2013-04-13 13:43:11.351056', NULL, 'disabled', 827);
INSERT INTO bib4template.biblio_form_subfields VALUES ('730', 'd', false, true, '2013-04-13 13:43:11.351056', NULL, '2013-04-13 13:43:11.351056', NULL, 'disabled', 830);
INSERT INTO bib4template.biblio_form_subfields VALUES ('730', 'f', false, false, '2013-04-13 13:43:11.351056', NULL, '2013-04-13 13:43:11.351056', NULL, 'disabled', 832);
INSERT INTO bib4template.biblio_form_subfields VALUES ('730', 'g', false, false, '2013-04-13 13:43:11.351056', NULL, '2013-04-13 13:43:11.351056', NULL, 'disabled', 833);
INSERT INTO bib4template.biblio_form_subfields VALUES ('730', 'k', false, true, '2013-04-13 13:43:11.351056', NULL, '2013-04-13 13:43:11.351056', NULL, 'disabled', 837);
INSERT INTO bib4template.biblio_form_subfields VALUES ('730', 'l', false, false, '2013-04-13 13:43:11.351056', NULL, '2013-04-13 13:43:11.351056', NULL, 'disabled', 838);
INSERT INTO bib4template.biblio_form_subfields VALUES ('730', 'p', false, true, '2013-04-13 13:43:11.351056', NULL, '2013-04-13 13:43:11.351056', NULL, 'disabled', 842);
INSERT INTO bib4template.biblio_form_subfields VALUES ('730', 'x', false, true, '2013-04-13 13:43:11.351056', NULL, '2013-04-13 13:43:11.351056', NULL, 'disabled', 850);
INSERT INTO bib4template.biblio_form_subfields VALUES ('730', 'y', false, true, '2013-04-13 13:43:11.351056', NULL, '2013-04-13 13:43:11.351056', NULL, 'disabled', 851);
INSERT INTO bib4template.biblio_form_subfields VALUES ('730', 'z', false, true, '2013-04-13 13:43:11.351056', NULL, '2013-04-13 13:43:11.351056', NULL, 'disabled', 852);
INSERT INTO bib4template.biblio_form_subfields VALUES ('740', 'a', false, false, '2013-04-13 13:43:11.351056', NULL, '2013-04-13 13:43:11.351056', NULL, 'disabled', 837);
INSERT INTO bib4template.biblio_form_subfields VALUES ('740', 'n', false, true, '2013-04-13 13:43:11.351056', NULL, '2013-04-13 13:43:11.351056', NULL, 'disabled', 850);
INSERT INTO bib4template.biblio_form_subfields VALUES ('740', 'p', false, true, '2013-04-13 13:43:11.351056', NULL, '2013-04-13 13:43:11.351056', NULL, 'disabled', 852);
INSERT INTO bib4template.biblio_form_subfields VALUES ('830', 'a', false, false, '2013-04-13 13:43:11.351056', NULL, '2013-04-13 13:43:11.351056', NULL, 'disabled', 927);
INSERT INTO bib4template.biblio_form_subfields VALUES ('830', 'v', false, false, '2013-04-13 13:43:11.351056', NULL, '2013-04-13 13:43:11.351056', NULL, 'disabled', 948);
INSERT INTO bib4template.biblio_form_subfields VALUES ('856', 'd', false, false, '2013-04-13 13:43:11.351056', NULL, '2013-04-13 13:43:11.351056', NULL, 'disabled', 956);
INSERT INTO bib4template.biblio_form_subfields VALUES ('856', 'f', false, false, '2013-04-13 13:43:11.351056', NULL, '2013-04-13 13:43:11.351056', NULL, 'disabled', 958);
INSERT INTO bib4template.biblio_form_subfields VALUES ('856', 'u', false, false, '2013-04-13 13:43:11.351056', NULL, '2013-04-13 13:43:11.351056', NULL, 'disabled', 973);
INSERT INTO bib4template.biblio_form_subfields VALUES ('856', 'y', false, false, '2013-04-13 13:43:11.351056', NULL, '2013-04-13 13:43:11.351056', NULL, 'disabled', 977);
INSERT INTO bib4template.biblio_form_subfields VALUES ('210', 'a', false, false, '2014-04-28 19:25:12.931', 1, '2014-04-28 19:25:12.931', NULL, 'disabled', 307);
INSERT INTO bib4template.biblio_form_subfields VALUES ('210', 'b', false, false, '2014-04-28 19:25:12.931', 1, '2014-04-28 19:25:12.931', NULL, 'disabled', 308);
INSERT INTO bib4template.biblio_form_subfields VALUES ('246', 'a', false, false, '2014-04-28 19:25:12.931', 1, '2014-04-28 19:25:12.931', NULL, 'disabled', 343);
INSERT INTO bib4template.biblio_form_subfields VALUES ('246', 'b', false, false, '2014-04-28 19:25:12.931', 1, '2014-04-28 19:25:12.931', NULL, 'disabled', 344);
INSERT INTO bib4template.biblio_form_subfields VALUES ('246', 'f', false, false, '2014-04-28 19:25:12.931', 1, '2014-04-28 19:25:12.931', NULL, 'disabled', 348);
INSERT INTO bib4template.biblio_form_subfields VALUES ('246', 'g', false, false, '2014-04-28 19:25:12.931', 1, '2014-04-28 19:25:12.931', NULL, 'disabled', 349);
INSERT INTO bib4template.biblio_form_subfields VALUES ('246', 'h', false, false, '2014-04-28 19:25:12.931', 1, '2014-04-28 19:25:12.931', NULL, 'disabled', 350);
INSERT INTO bib4template.biblio_form_subfields VALUES ('246', 'i', false, false, '2014-04-28 19:25:12.931', 1, '2014-04-28 19:25:12.931', NULL, 'disabled', 351);
INSERT INTO bib4template.biblio_form_subfields VALUES ('246', 'n', false, true, '2014-04-28 19:25:12.931', 1, '2014-04-28 19:25:12.931', NULL, 'disabled', 356);
INSERT INTO bib4template.biblio_form_subfields VALUES ('246', 'p', false, true, '2014-04-28 19:25:12.931', 1, '2014-04-28 19:25:12.931', NULL, 'disabled', 358);
INSERT INTO bib4template.biblio_form_subfields VALUES ('310', 'a', false, false, '2014-04-28 19:25:12.931', 1, '2014-04-28 19:25:12.931', NULL, 'disabled', 407);
INSERT INTO bib4template.biblio_form_subfields VALUES ('310', 'b', false, false, '2014-04-28 19:25:12.931', 1, '2014-04-28 19:25:12.931', NULL, 'disabled', 408);
INSERT INTO bib4template.biblio_form_subfields VALUES ('321', 'a', false, false, '2014-04-28 19:25:12.931', 1, '2014-04-28 19:25:12.931', NULL, 'disabled', 418);
INSERT INTO bib4template.biblio_form_subfields VALUES ('321', 'b', false, false, '2014-04-28 19:25:12.931', 1, '2014-04-28 19:25:12.931', NULL, 'disabled', 419);
INSERT INTO bib4template.biblio_form_subfields VALUES ('362', 'a', false, false, '2014-04-28 19:25:12.931', 1, '2014-04-28 19:25:12.931', NULL, 'disabled', 459);
INSERT INTO bib4template.biblio_form_subfields VALUES ('362', 'z', false, false, '2014-04-28 19:25:12.931', 1, '2014-04-28 19:25:12.931', NULL, 'disabled', 484);
INSERT INTO bib4template.biblio_form_subfields VALUES ('515', 'a', false, false, '2014-04-28 19:25:12.931', 1, '2014-04-28 19:25:12.931', NULL, 'disabled', 612);
INSERT INTO bib4template.biblio_form_subfields VALUES ('525', 'a', false, false, '2014-04-28 19:25:12.931', 1, '2014-04-28 19:25:12.931', NULL, 'disabled', 622);
INSERT INTO bib4template.biblio_form_subfields VALUES ('550', 'a', false, false, '2014-04-28 19:25:12.931', 1, '2014-04-28 19:25:12.931', NULL, 'disabled', 647);
INSERT INTO bib4template.biblio_form_subfields VALUES ('555', 'a', false, false, '2014-04-28 19:25:12.931', 1, '2014-04-28 19:25:12.931', NULL, 'disabled', 652);
INSERT INTO bib4template.biblio_form_subfields VALUES ('555', 'b', false, true, '2014-04-28 19:25:12.931', 1, '2014-04-28 19:25:12.931', NULL, 'disabled', 653);
INSERT INTO bib4template.biblio_form_subfields VALUES ('555', 'c', false, false, '2014-04-28 19:25:12.931', 1, '2014-04-28 19:25:12.931', NULL, 'disabled', 654);
INSERT INTO bib4template.biblio_form_subfields VALUES ('555', 'd', false, false, '2014-04-28 19:25:12.931', 1, '2014-04-28 19:25:12.931', NULL, 'disabled', 655);
INSERT INTO bib4template.biblio_form_subfields VALUES ('555', 'u', false, true, '2014-04-28 19:25:12.931', 1, '2014-04-28 19:25:12.931', NULL, 'disabled', 672);
INSERT INTO bib4template.biblio_form_subfields VALUES ('555', '3', false, false, '2014-04-28 19:25:12.931', 1, '2014-04-28 19:25:12.931', NULL, 'disabled', 606);
INSERT INTO bib4template.biblio_form_subfields VALUES ('580', 'a', false, false, '2014-04-28 19:25:12.931', 1, '2014-04-28 19:25:12.931', NULL, 'disabled', 677);
INSERT INTO bib4template.biblio_form_subfields VALUES ('947', 'a', false, false, '2014-04-28 19:25:12.931', 1, '2014-04-28 19:25:12.931', NULL, 'disabled', 1044);
INSERT INTO bib4template.biblio_form_subfields VALUES ('947', 'b', false, false, '2014-04-28 19:25:12.931', 1, '2014-04-28 19:25:12.931', NULL, 'disabled', 1045);
INSERT INTO bib4template.biblio_form_subfields VALUES ('947', 'c', false, false, '2014-04-28 19:25:12.931', 1, '2014-04-28 19:25:12.931', NULL, 'disabled', 1046);
INSERT INTO bib4template.biblio_form_subfields VALUES ('947', 'd', false, false, '2014-04-28 19:25:12.931', 1, '2014-04-28 19:25:12.931', NULL, 'disabled', 1047);
INSERT INTO bib4template.biblio_form_subfields VALUES ('947', 'e', false, false, '2014-04-28 19:25:12.931', 1, '2014-04-28 19:25:12.931', NULL, 'disabled', 1048);
INSERT INTO bib4template.biblio_form_subfields VALUES ('947', 'f', false, false, '2014-04-28 19:25:12.931', 1, '2014-04-28 19:25:12.931', NULL, 'disabled', 1049);
INSERT INTO bib4template.biblio_form_subfields VALUES ('947', 'g', false, false, '2014-04-28 19:25:12.931', 1, '2014-04-28 19:25:12.931', NULL, 'disabled', 1050);
INSERT INTO bib4template.biblio_form_subfields VALUES ('947', 'i', false, false, '2014-04-28 19:25:12.931', 1, '2014-04-28 19:25:12.931', NULL, 'disabled', 1052);
INSERT INTO bib4template.biblio_form_subfields VALUES ('947', 'j', false, false, '2014-04-28 19:25:12.931', 1, '2014-04-28 19:25:12.931', NULL, 'disabled', 1053);
INSERT INTO bib4template.biblio_form_subfields VALUES ('947', 'k', false, false, '2014-04-28 19:25:12.931', 1, '2014-04-28 19:25:12.931', NULL, 'disabled', 1054);
INSERT INTO bib4template.biblio_form_subfields VALUES ('947', 'l', false, false, '2014-04-28 19:25:12.931', 1, '2014-04-28 19:25:12.931', NULL, 'disabled', 1055);
INSERT INTO bib4template.biblio_form_subfields VALUES ('947', 'm', false, false, '2014-04-28 19:25:12.931', 1, '2014-04-28 19:25:12.931', NULL, 'disabled', 1056);
INSERT INTO bib4template.biblio_form_subfields VALUES ('947', 'n', false, false, '2014-04-28 19:25:12.931', 1, '2014-04-28 19:25:12.931', NULL, 'disabled', 1057);
INSERT INTO bib4template.biblio_form_subfields VALUES ('947', 'o', false, false, '2014-04-28 19:25:12.931', 1, '2014-04-28 19:25:12.931', NULL, 'disabled', 1058);
INSERT INTO bib4template.biblio_form_subfields VALUES ('947', 'p', false, false, '2014-04-28 19:25:12.931', 1, '2014-04-28 19:25:12.931', NULL, 'disabled', 1059);
INSERT INTO bib4template.biblio_form_subfields VALUES ('947', 'q', false, false, '2014-04-28 19:25:12.931', 1, '2014-04-28 19:25:12.931', NULL, 'disabled', 1060);
INSERT INTO bib4template.biblio_form_subfields VALUES ('947', 'r', false, false, '2014-04-28 19:25:12.931', 1, '2014-04-28 19:25:12.931', NULL, 'disabled', 1061);
INSERT INTO bib4template.biblio_form_subfields VALUES ('947', 's', false, false, '2014-04-28 19:25:12.931', 1, '2014-04-28 19:25:12.931', NULL, 'disabled', 1062);
INSERT INTO bib4template.biblio_form_subfields VALUES ('947', 't', false, false, '2014-04-28 19:25:12.931', 1, '2014-04-28 19:25:12.931', NULL, 'disabled', 1063);
INSERT INTO bib4template.biblio_form_subfields VALUES ('947', 'u', false, false, '2014-04-28 19:25:12.931', 1, '2014-04-28 19:25:12.931', NULL, 'disabled', 1064);
INSERT INTO bib4template.biblio_form_subfields VALUES ('947', 'z', false, false, '2014-04-28 19:25:12.931', 1, '2014-04-28 19:25:12.931', NULL, 'disabled', 1069);
INSERT INTO bib4template.biblio_form_subfields VALUES ('100', 'a', false, false, '2013-04-13 13:43:11.351056', NULL, '2013-04-13 13:43:11.351056', NULL, 'authorities', 197);
INSERT INTO bib4template.biblio_form_subfields VALUES ('110', 'a', false, false, '2013-04-13 13:43:11.351056', NULL, '2013-04-13 13:43:11.351056', NULL, 'authorities', 207);
INSERT INTO bib4template.biblio_form_subfields VALUES ('111', 'a', false, false, '2013-04-13 13:43:11.351056', NULL, '2013-04-13 13:43:11.351056', NULL, 'authorities', 208);
INSERT INTO bib4template.biblio_form_subfields VALUES ('600', 'a', false, false, '2013-04-13 13:43:11.351056', NULL, '2013-04-13 13:43:11.351056', NULL, 'vocabulary', 697);
INSERT INTO bib4template.biblio_form_subfields VALUES ('610', 'a', false, false, '2013-04-13 13:43:11.351056', NULL, '2013-04-13 13:43:11.351056', NULL, 'vocabulary', 707);
INSERT INTO bib4template.biblio_form_subfields VALUES ('611', 'a', false, false, '2013-04-13 13:43:11.351056', NULL, '2013-04-13 13:43:11.351056', NULL, 'vocabulary', 708);
INSERT INTO bib4template.biblio_form_subfields VALUES ('630', 'a', false, false, '2013-04-13 13:43:11.351056', NULL, '2013-04-13 13:43:11.351056', NULL, 'vocabulary', 727);
INSERT INTO bib4template.biblio_form_subfields VALUES ('650', 'a', false, false, '2013-04-13 13:43:11.351056', NULL, '2013-04-13 13:43:11.351056', NULL, 'vocabulary', 747);
INSERT INTO bib4template.biblio_form_subfields VALUES ('651', 'a', false, false, '2013-04-13 13:43:11.351056', NULL, '2013-04-13 13:43:11.351056', NULL, 'vocabulary', 748);


--
-- Data for Name: biblio_holdings; Type: TABLE DATA; Schema: bib4template; Owner: biblivre
--



--
-- Data for Name: biblio_idx_autocomplete; Type: TABLE DATA; Schema: bib4template; Owner: biblivre
--

INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2, '095', 'a', '1.00.00.00-3', '1.00.00.00-3 Ciências Exatas e da Terra', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3, '095', 'a', 'ciencias', '1.00.00.00-3 Ciências Exatas e da Terra', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4, '095', 'a', 'exatas', '1.00.00.00-3 Ciências Exatas e da Terra', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (5, '095', 'a', 'da', '1.00.00.00-3 Ciências Exatas e da Terra', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (6, '095', 'a', 'terra', '1.00.00.00-3 Ciências Exatas e da Terra', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (7, '095', 'a', '1.01.00.00-8', '1.01.00.00-8 Matemática', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (8, '095', 'a', 'matematica', '1.01.00.00-8 Matemática', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (9, '095', 'a', '1.01.01.00-4', '1.01.01.00-4 Álgebra', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (10, '095', 'a', 'algebra', '1.01.01.00-4 Álgebra', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (11, '095', 'a', '1.01.01.01-2', '1.01.01.01-2 Conjuntos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (12, '095', 'a', 'conjuntos', '1.01.01.01-2 Conjuntos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (13, '095', 'a', '1.01.01.02-0', '1.01.01.02-0 Lógica Matemática', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (14, '095', 'a', 'logica', '1.01.01.02-0 Lógica Matemática', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (15, '095', 'a', 'matematica', '1.01.01.02-0 Lógica Matemática', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (16, '095', 'a', '1.01.01.03-9', '1.01.01.03-9 Teoria dos Números', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (17, '095', 'a', 'teoria', '1.01.01.03-9 Teoria dos Números', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (18, '095', 'a', 'dos', '1.01.01.03-9 Teoria dos Números', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (19, '095', 'a', 'numeros', '1.01.01.03-9 Teoria dos Números', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (20, '095', 'a', '1.01.01.04-7', '1.01.01.04-7 Grupos de Algebra Não-Comutaviva', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (21, '095', 'a', 'grupos', '1.01.01.04-7 Grupos de Algebra Não-Comutaviva', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (22, '095', 'a', 'de', '1.01.01.04-7 Grupos de Algebra Não-Comutaviva', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (23, '095', 'a', 'algebra', '1.01.01.04-7 Grupos de Algebra Não-Comutaviva', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (24, '095', 'a', 'nao-comutaviva', '1.01.01.04-7 Grupos de Algebra Não-Comutaviva', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (25, '095', 'a', '1.01.01.05-5', '1.01.01.05-5 Algebra Comutativa', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (26, '095', 'a', 'algebra', '1.01.01.05-5 Algebra Comutativa', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (27, '095', 'a', 'comutativa', '1.01.01.05-5 Algebra Comutativa', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (28, '095', 'a', '1.01.01.06-3', '1.01.01.06-3 Geometria Algebrica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (29, '095', 'a', 'geometria', '1.01.01.06-3 Geometria Algebrica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (30, '095', 'a', 'algebrica', '1.01.01.06-3 Geometria Algebrica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (31, '095', 'a', '1.01.02.00-0', '1.01.02.00-0 Análise', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (32, '095', 'a', 'analise', '1.01.02.00-0 Análise', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (33, '095', 'a', '1.01.02.01-9', '1.01.02.01-9 Análise Complexa', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (34, '095', 'a', 'analise', '1.01.02.01-9 Análise Complexa', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (35, '095', 'a', 'complexa', '1.01.02.01-9 Análise Complexa', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (36, '095', 'a', '1.01.02.02-7', '1.01.02.02-7 Análise Funcional', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (37, '095', 'a', 'analise', '1.01.02.02-7 Análise Funcional', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (38, '095', 'a', 'funcional', '1.01.02.02-7 Análise Funcional', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (39, '095', 'a', '1.01.02.03-5', '1.01.02.03-5 Análise Funcional Não-Linear', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (40, '095', 'a', 'analise', '1.01.02.03-5 Análise Funcional Não-Linear', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (41, '095', 'a', 'funcional', '1.01.02.03-5 Análise Funcional Não-Linear', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (42, '095', 'a', 'nao-linear', '1.01.02.03-5 Análise Funcional Não-Linear', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (43, '095', 'a', '1.01.02.04-3', '1.01.02.04-3 Equações Diferênciais Ordinárias', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (44, '095', 'a', 'equacoes', '1.01.02.04-3 Equações Diferênciais Ordinárias', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (45, '095', 'a', 'diferenciais', '1.01.02.04-3 Equações Diferênciais Ordinárias', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (46, '095', 'a', 'ordinarias', '1.01.02.04-3 Equações Diferênciais Ordinárias', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (47, '095', 'a', '1.01.02.05-1', '1.01.02.05-1 Equações Diferênciais Parciais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (48, '095', 'a', 'equacoes', '1.01.02.05-1 Equações Diferênciais Parciais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (49, '095', 'a', 'diferenciais', '1.01.02.05-1 Equações Diferênciais Parciais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (50, '095', 'a', 'parciais', '1.01.02.05-1 Equações Diferênciais Parciais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (51, '095', 'a', '1.01.02.06-0', '1.01.02.06-0 Equações Diferênciais Funcionais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (52, '095', 'a', 'equacoes', '1.01.02.06-0 Equações Diferênciais Funcionais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (53, '095', 'a', 'diferenciais', '1.01.02.06-0 Equações Diferênciais Funcionais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (54, '095', 'a', 'funcionais', '1.01.02.06-0 Equações Diferênciais Funcionais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (55, '095', 'a', '1.01.03.00-7', '1.01.03.00-7 Geometria e Topologia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (56, '095', 'a', 'geometria', '1.01.03.00-7 Geometria e Topologia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (57, '095', 'a', 'topologia', '1.01.03.00-7 Geometria e Topologia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (58, '095', 'a', '1.01.03.01-5', '1.01.03.01-5 Geometria Diferêncial', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (59, '095', 'a', 'geometria', '1.01.03.01-5 Geometria Diferêncial', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (60, '095', 'a', 'diferencial', '1.01.03.01-5 Geometria Diferêncial', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (61, '095', 'a', '1.01.03.02-3', '1.01.03.02-3 Topologia Algébrica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (62, '095', 'a', 'topologia', '1.01.03.02-3 Topologia Algébrica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (63, '095', 'a', 'algebrica', '1.01.03.02-3 Topologia Algébrica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (64, '095', 'a', '1.01.03.03-1', '1.01.03.03-1 Topologia das Variedades', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (65, '095', 'a', 'topologia', '1.01.03.03-1 Topologia das Variedades', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (66, '095', 'a', 'das', '1.01.03.03-1 Topologia das Variedades', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (67, '095', 'a', 'variedades', '1.01.03.03-1 Topologia das Variedades', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (68, '095', 'a', '1.01.03.04-0', '1.01.03.04-0 Sistemas Dinâmicos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (69, '095', 'a', 'sistemas', '1.01.03.04-0 Sistemas Dinâmicos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (70, '095', 'a', 'dinamicos', '1.01.03.04-0 Sistemas Dinâmicos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (71, '095', 'a', '1.01.03.05-8', '1.01.03.05-8 Teoria das Singularidades e Teoria das Catástrofes', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (72, '095', 'a', 'teoria', '1.01.03.05-8 Teoria das Singularidades e Teoria das Catástrofes', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (73, '095', 'a', 'das', '1.01.03.05-8 Teoria das Singularidades e Teoria das Catástrofes', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (74, '095', 'a', 'singularidades', '1.01.03.05-8 Teoria das Singularidades e Teoria das Catástrofes', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (75, '095', 'a', 'teoria', '1.01.03.05-8 Teoria das Singularidades e Teoria das Catástrofes', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (76, '095', 'a', 'das', '1.01.03.05-8 Teoria das Singularidades e Teoria das Catástrofes', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (77, '095', 'a', 'catastrofes', '1.01.03.05-8 Teoria das Singularidades e Teoria das Catástrofes', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (78, '095', 'a', '1.01.03.06-6', '1.01.03.06-6 Teoria das Folheações', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (79, '095', 'a', 'teoria', '1.01.03.06-6 Teoria das Folheações', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (80, '095', 'a', 'das', '1.01.03.06-6 Teoria das Folheações', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (81, '095', 'a', 'folheacoes', '1.01.03.06-6 Teoria das Folheações', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (82, '095', 'a', '1.01.04.00-3', '1.01.04.00-3 Matemática Aplicada', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (83, '095', 'a', 'matematica', '1.01.04.00-3 Matemática Aplicada', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (84, '095', 'a', 'aplicada', '1.01.04.00-3 Matemática Aplicada', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (85, '095', 'a', '1.01.04.01-1', '1.01.04.01-1 Física Matemática', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (86, '095', 'a', 'fisica', '1.01.04.01-1 Física Matemática', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (87, '095', 'a', 'matematica', '1.01.04.01-1 Física Matemática', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (88, '095', 'a', '1.01.04.02-0', '1.01.04.02-0 Análise Numérica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (89, '095', 'a', 'analise', '1.01.04.02-0 Análise Numérica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (90, '095', 'a', 'numerica', '1.01.04.02-0 Análise Numérica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (91, '095', 'a', '1.01.04.03-8', '1.01.04.03-8 Matemática Discreta e Combinatoria', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (92, '095', 'a', 'matematica', '1.01.04.03-8 Matemática Discreta e Combinatoria', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (93, '095', 'a', 'discreta', '1.01.04.03-8 Matemática Discreta e Combinatoria', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (94, '095', 'a', 'combinatoria', '1.01.04.03-8 Matemática Discreta e Combinatoria', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (95, '095', 'a', '1.02.00.00-2', '1.02.00.00-2 Probabilidade e Estatística', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (96, '095', 'a', 'probabilidade', '1.02.00.00-2 Probabilidade e Estatística', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (97, '095', 'a', 'estatistica', '1.02.00.00-2 Probabilidade e Estatística', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (98, '095', 'a', '1.02.01.00-9', '1.02.01.00-9 Probabilidade', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (99, '095', 'a', 'probabilidade', '1.02.01.00-9 Probabilidade', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (100, '095', 'a', '1.02.01.01-7', '1.02.01.01-7 Teoria Geral e Fundamentos da Probabilidade', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (101, '095', 'a', 'teoria', '1.02.01.01-7 Teoria Geral e Fundamentos da Probabilidade', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (102, '095', 'a', 'geral', '1.02.01.01-7 Teoria Geral e Fundamentos da Probabilidade', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (103, '095', 'a', 'fundamentos', '1.02.01.01-7 Teoria Geral e Fundamentos da Probabilidade', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (104, '095', 'a', 'da', '1.02.01.01-7 Teoria Geral e Fundamentos da Probabilidade', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (105, '095', 'a', 'probabilidade', '1.02.01.01-7 Teoria Geral e Fundamentos da Probabilidade', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (106, '095', 'a', '1.02.01.02-5', '1.02.01.02-5 Teoria Geral e Processos Estocásticos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (107, '095', 'a', 'teoria', '1.02.01.02-5 Teoria Geral e Processos Estocásticos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (108, '095', 'a', 'geral', '1.02.01.02-5 Teoria Geral e Processos Estocásticos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (109, '095', 'a', 'processos', '1.02.01.02-5 Teoria Geral e Processos Estocásticos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (110, '095', 'a', 'estocasticos', '1.02.01.02-5 Teoria Geral e Processos Estocásticos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (111, '095', 'a', '1.02.01.03-3', '1.02.01.03-3 Teoremas de Limite', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (112, '095', 'a', 'teoremas', '1.02.01.03-3 Teoremas de Limite', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (113, '095', 'a', 'de', '1.02.01.03-3 Teoremas de Limite', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (114, '095', 'a', 'limite', '1.02.01.03-3 Teoremas de Limite', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (115, '095', 'a', '1.02.01.04-1', '1.02.01.04-1 Processos Markovianos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (116, '095', 'a', 'processos', '1.02.01.04-1 Processos Markovianos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (117, '095', 'a', 'markovianos', '1.02.01.04-1 Processos Markovianos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (118, '095', 'a', '1.02.01.05-0', '1.02.01.05-0 Análise Estocástica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (119, '095', 'a', 'analise', '1.02.01.05-0 Análise Estocástica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (120, '095', 'a', 'estocastica', '1.02.01.05-0 Análise Estocástica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (121, '095', 'a', '1.02.01.06-8', '1.02.01.06-8 Processos Estocásticos Especiais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (122, '095', 'a', 'processos', '1.02.01.06-8 Processos Estocásticos Especiais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (123, '095', 'a', 'estocasticos', '1.02.01.06-8 Processos Estocásticos Especiais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (124, '095', 'a', 'especiais', '1.02.01.06-8 Processos Estocásticos Especiais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (125, '095', 'a', '1.02.02.00-5', '1.02.02.00-5 Estatística', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (126, '095', 'a', 'estatistica', '1.02.02.00-5 Estatística', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (127, '095', 'a', '1.02.02.01-3', '1.02.02.01-3 Fundamentos da Estatística', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (128, '095', 'a', 'fundamentos', '1.02.02.01-3 Fundamentos da Estatística', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (129, '095', 'a', 'da', '1.02.02.01-3 Fundamentos da Estatística', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (130, '095', 'a', 'estatistica', '1.02.02.01-3 Fundamentos da Estatística', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (131, '095', 'a', '1.02.02.02-1', '1.02.02.02-1 Inferência Paramétrica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (132, '095', 'a', 'inferencia', '1.02.02.02-1 Inferência Paramétrica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (133, '095', 'a', 'parametrica', '1.02.02.02-1 Inferência Paramétrica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (134, '095', 'a', '1.02.02.03-0', '1.02.02.03-0 Inferência Nao-Paramétrica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (135, '095', 'a', 'inferencia', '1.02.02.03-0 Inferência Nao-Paramétrica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (136, '095', 'a', 'nao-parametrica', '1.02.02.03-0 Inferência Nao-Paramétrica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (137, '095', 'a', '1.02.02.04-8', '1.02.02.04-8 Inferência em Processos Estocásticos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (138, '095', 'a', 'inferencia', '1.02.02.04-8 Inferência em Processos Estocásticos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (139, '095', 'a', 'em', '1.02.02.04-8 Inferência em Processos Estocásticos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (140, '095', 'a', 'processos', '1.02.02.04-8 Inferência em Processos Estocásticos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (141, '095', 'a', 'estocasticos', '1.02.02.04-8 Inferência em Processos Estocásticos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (142, '095', 'a', '1.02.02.05-6', '1.02.02.05-6 Análise Multivariada', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (143, '095', 'a', 'analise', '1.02.02.05-6 Análise Multivariada', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (144, '095', 'a', 'multivariada', '1.02.02.05-6 Análise Multivariada', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (145, '095', 'a', '1.02.02.06-4', '1.02.02.06-4 Regressão e Correlação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (146, '095', 'a', 'regressao', '1.02.02.06-4 Regressão e Correlação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (147, '095', 'a', 'correlacao', '1.02.02.06-4 Regressão e Correlação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (148, '095', 'a', '1.02.02.07-2', '1.02.02.07-2 Planejamento de Experimentos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (149, '095', 'a', 'planejamento', '1.02.02.07-2 Planejamento de Experimentos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (150, '095', 'a', 'de', '1.02.02.07-2 Planejamento de Experimentos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (151, '095', 'a', 'experimentos', '1.02.02.07-2 Planejamento de Experimentos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (152, '095', 'a', '1.02.02.08-0', '1.02.02.08-0 Análise de Dados', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (153, '095', 'a', 'analise', '1.02.02.08-0 Análise de Dados', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (154, '095', 'a', 'de', '1.02.02.08-0 Análise de Dados', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (155, '095', 'a', 'dados', '1.02.02.08-0 Análise de Dados', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (156, '095', 'a', '1.02.03.00-1', '1.02.03.00-1 Probabilidade e Estatística Aplicadas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (157, '095', 'a', 'probabilidade', '1.02.03.00-1 Probabilidade e Estatística Aplicadas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (158, '095', 'a', 'estatistica', '1.02.03.00-1 Probabilidade e Estatística Aplicadas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (159, '095', 'a', 'aplicadas', '1.02.03.00-1 Probabilidade e Estatística Aplicadas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (160, '095', 'a', '1.03.00.00-7', '1.03.00.00-7 Ciência da Computação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (161, '095', 'a', 'ciencia', '1.03.00.00-7 Ciência da Computação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (162, '095', 'a', 'da', '1.03.00.00-7 Ciência da Computação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (163, '095', 'a', 'computacao', '1.03.00.00-7 Ciência da Computação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (164, '095', 'a', '1.03.01.00-3', '1.03.01.00-3 Teoria da Computação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (165, '095', 'a', 'teoria', '1.03.01.00-3 Teoria da Computação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (166, '095', 'a', 'da', '1.03.01.00-3 Teoria da Computação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (167, '095', 'a', 'computacao', '1.03.01.00-3 Teoria da Computação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (168, '095', 'a', '1.03.01.01-1', '1.03.01.01-1 Computabilidade e Modelos de Computação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (169, '095', 'a', 'computabilidade', '1.03.01.01-1 Computabilidade e Modelos de Computação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (170, '095', 'a', 'modelos', '1.03.01.01-1 Computabilidade e Modelos de Computação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (171, '095', 'a', 'de', '1.03.01.01-1 Computabilidade e Modelos de Computação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (172, '095', 'a', 'computacao', '1.03.01.01-1 Computabilidade e Modelos de Computação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (173, '095', 'a', '1.03.01.02-0', '1.03.01.02-0 Linguagem Formais e Automatos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (174, '095', 'a', 'linguagem', '1.03.01.02-0 Linguagem Formais e Automatos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (175, '095', 'a', 'formais', '1.03.01.02-0 Linguagem Formais e Automatos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (176, '095', 'a', 'automatos', '1.03.01.02-0 Linguagem Formais e Automatos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (177, '095', 'a', '1.03.01.03-8', '1.03.01.03-8 Análise de Algoritmos e Complexidade de Computação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (178, '095', 'a', 'analise', '1.03.01.03-8 Análise de Algoritmos e Complexidade de Computação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (179, '095', 'a', 'de', '1.03.01.03-8 Análise de Algoritmos e Complexidade de Computação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (180, '095', 'a', 'algoritmos', '1.03.01.03-8 Análise de Algoritmos e Complexidade de Computação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (181, '095', 'a', 'complexidade', '1.03.01.03-8 Análise de Algoritmos e Complexidade de Computação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (182, '095', 'a', 'de', '1.03.01.03-8 Análise de Algoritmos e Complexidade de Computação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (183, '095', 'a', 'computacao', '1.03.01.03-8 Análise de Algoritmos e Complexidade de Computação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (184, '095', 'a', '1.03.01.04-6', '1.03.01.04-6 Lógicas e Semântica de Programas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (185, '095', 'a', 'logicas', '1.03.01.04-6 Lógicas e Semântica de Programas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (186, '095', 'a', 'semantica', '1.03.01.04-6 Lógicas e Semântica de Programas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (187, '095', 'a', 'de', '1.03.01.04-6 Lógicas e Semântica de Programas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (188, '095', 'a', 'programas', '1.03.01.04-6 Lógicas e Semântica de Programas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (189, '095', 'a', '1.03.02.00-0', '1.03.02.00-0 Matemática da Computação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (190, '095', 'a', 'matematica', '1.03.02.00-0 Matemática da Computação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (191, '095', 'a', 'da', '1.03.02.00-0 Matemática da Computação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (192, '095', 'a', 'computacao', '1.03.02.00-0 Matemática da Computação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (193, '095', 'a', '1.03.02.01-8', '1.03.02.01-8 Matemática Simbólica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (194, '095', 'a', 'matematica', '1.03.02.01-8 Matemática Simbólica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (195, '095', 'a', 'simbolica', '1.03.02.01-8 Matemática Simbólica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (196, '095', 'a', '1.03.02.02-6', '1.03.02.02-6 Modelos Analíticos e de Simulação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (197, '095', 'a', 'modelos', '1.03.02.02-6 Modelos Analíticos e de Simulação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (198, '095', 'a', 'analiticos', '1.03.02.02-6 Modelos Analíticos e de Simulação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (199, '095', 'a', 'de', '1.03.02.02-6 Modelos Analíticos e de Simulação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (200, '095', 'a', 'simulacao', '1.03.02.02-6 Modelos Analíticos e de Simulação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (201, '095', 'a', '1.03.03.00-6', '1.03.03.00-6 Metodologia e Técnicas da Computação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (202, '095', 'a', 'metodologia', '1.03.03.00-6 Metodologia e Técnicas da Computação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (203, '095', 'a', 'tecnicas', '1.03.03.00-6 Metodologia e Técnicas da Computação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (204, '095', 'a', 'da', '1.03.03.00-6 Metodologia e Técnicas da Computação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (205, '095', 'a', 'computacao', '1.03.03.00-6 Metodologia e Técnicas da Computação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (206, '095', 'a', '1.03.03.01-4', '1.03.03.01-4 Linguagens de Programação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (207, '095', 'a', 'linguagens', '1.03.03.01-4 Linguagens de Programação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (208, '095', 'a', 'de', '1.03.03.01-4 Linguagens de Programação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (209, '095', 'a', 'programacao', '1.03.03.01-4 Linguagens de Programação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (210, '095', 'a', '1.03.03.02-2', '1.03.03.02-2 Engenharia de Software', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (211, '095', 'a', 'engenharia', '1.03.03.02-2 Engenharia de Software', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (212, '095', 'a', 'de', '1.03.03.02-2 Engenharia de Software', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (213, '095', 'a', 'software', '1.03.03.02-2 Engenharia de Software', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (214, '095', 'a', '1.03.03.03-0', '1.03.03.03-0 Banco de Dados', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (215, '095', 'a', 'banco', '1.03.03.03-0 Banco de Dados', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (216, '095', 'a', 'de', '1.03.03.03-0 Banco de Dados', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (217, '095', 'a', 'dados', '1.03.03.03-0 Banco de Dados', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (218, '095', 'a', '1.03.03.04-9', '1.03.03.04-9 Sistemas de Informação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (219, '095', 'a', 'sistemas', '1.03.03.04-9 Sistemas de Informação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (220, '095', 'a', 'de', '1.03.03.04-9 Sistemas de Informação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (221, '095', 'a', 'informacao', '1.03.03.04-9 Sistemas de Informação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (222, '095', 'a', '1.03.03.05-7', '1.03.03.05-7 Processamento Gráfico (Graphics)', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (223, '095', 'a', 'processamento', '1.03.03.05-7 Processamento Gráfico (Graphics)', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (224, '095', 'a', 'grafico', '1.03.03.05-7 Processamento Gráfico (Graphics)', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (225, '095', 'a', '(graphics)', '1.03.03.05-7 Processamento Gráfico (Graphics)', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (226, '095', 'a', '1.03.04.00-2', '1.03.04.00-2 Sistemas de Computação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (227, '095', 'a', 'sistemas', '1.03.04.00-2 Sistemas de Computação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (228, '095', 'a', 'de', '1.03.04.00-2 Sistemas de Computação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (229, '095', 'a', 'computacao', '1.03.04.00-2 Sistemas de Computação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (230, '095', 'a', '1.03.04.01-0', '1.03.04.01-0 Hardware', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (231, '095', 'a', 'hardware', '1.03.04.01-0 Hardware', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (232, '095', 'a', '1.03.04.02-9', '1.03.04.02-9 Arquitetura de Sistemas de Computação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (233, '095', 'a', 'arquitetura', '1.03.04.02-9 Arquitetura de Sistemas de Computação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (234, '095', 'a', 'de', '1.03.04.02-9 Arquitetura de Sistemas de Computação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (235, '095', 'a', 'sistemas', '1.03.04.02-9 Arquitetura de Sistemas de Computação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (236, '095', 'a', 'de', '1.03.04.02-9 Arquitetura de Sistemas de Computação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (237, '095', 'a', 'computacao', '1.03.04.02-9 Arquitetura de Sistemas de Computação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (238, '095', 'a', '1.03.04.03-7', '1.03.04.03-7 Software Básico', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (239, '095', 'a', 'software', '1.03.04.03-7 Software Básico', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (240, '095', 'a', 'basico', '1.03.04.03-7 Software Básico', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (241, '095', 'a', '1.03.04.04-5', '1.03.04.04-5 Teleinformática', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (242, '095', 'a', 'teleinformatica', '1.03.04.04-5 Teleinformática', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (243, '095', 'a', '1.04.00.00-1', '1.04.00.00-1 Astronomia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (244, '095', 'a', 'astronomia', '1.04.00.00-1 Astronomia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (245, '095', 'a', '1.04.01.00-8', '1.04.01.00-8 Astronomia de Posição e Mecânica Celeste', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (246, '095', 'a', 'astronomia', '1.04.01.00-8 Astronomia de Posição e Mecânica Celeste', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (247, '095', 'a', 'de', '1.04.01.00-8 Astronomia de Posição e Mecânica Celeste', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (248, '095', 'a', 'posicao', '1.04.01.00-8 Astronomia de Posição e Mecânica Celeste', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (249, '095', 'a', 'mecanica', '1.04.01.00-8 Astronomia de Posição e Mecânica Celeste', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (250, '095', 'a', 'celeste', '1.04.01.00-8 Astronomia de Posição e Mecânica Celeste', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (251, '095', 'a', '1.04.01.01-6', '1.04.01.01-6 Astronomia Fundamental', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (252, '095', 'a', 'astronomia', '1.04.01.01-6 Astronomia Fundamental', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (253, '095', 'a', 'fundamental', '1.04.01.01-6 Astronomia Fundamental', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (254, '095', 'a', '1.04.01.02-4', '1.04.01.02-4 Astronomia Dinâmica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (255, '095', 'a', 'astronomia', '1.04.01.02-4 Astronomia Dinâmica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (256, '095', 'a', 'dinamica', '1.04.01.02-4 Astronomia Dinâmica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (257, '095', 'a', '1.04.02.00-4', '1.04.02.00-4 Astrofísica Estelar', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (258, '095', 'a', 'astrofisica', '1.04.02.00-4 Astrofísica Estelar', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (259, '095', 'a', 'estelar', '1.04.02.00-4 Astrofísica Estelar', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (260, '095', 'a', '1.04.03.00-0', '1.04.03.00-0 Astrofísica do Meio Interestelar', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (261, '095', 'a', 'astrofisica', '1.04.03.00-0 Astrofísica do Meio Interestelar', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (262, '095', 'a', 'do', '1.04.03.00-0 Astrofísica do Meio Interestelar', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (263, '095', 'a', 'meio', '1.04.03.00-0 Astrofísica do Meio Interestelar', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (264, '095', 'a', 'interestelar', '1.04.03.00-0 Astrofísica do Meio Interestelar', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (265, '095', 'a', '1.04.03.01-9', '1.04.03.01-9 Meio Interestelar', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (266, '095', 'a', 'meio', '1.04.03.01-9 Meio Interestelar', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (267, '095', 'a', 'interestelar', '1.04.03.01-9 Meio Interestelar', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (268, '095', 'a', '1.04.03.02-7', '1.04.03.02-7 Nebulosa', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (269, '095', 'a', 'nebulosa', '1.04.03.02-7 Nebulosa', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (270, '095', 'a', '1.04.04.00-7', '1.04.04.00-7 Astrofísica Extragaláctica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (271, '095', 'a', 'astrofisica', '1.04.04.00-7 Astrofísica Extragaláctica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (272, '095', 'a', 'extragalactica', '1.04.04.00-7 Astrofísica Extragaláctica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (273, '095', 'a', '1.04.04.01-5', '1.04.04.01-5 Galáxias', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (274, '095', 'a', 'galaxias', '1.04.04.01-5 Galáxias', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (275, '095', 'a', '1.04.04.02-3', '1.04.04.02-3 Aglomerados de Galáxias', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (276, '095', 'a', 'aglomerados', '1.04.04.02-3 Aglomerados de Galáxias', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (277, '095', 'a', 'de', '1.04.04.02-3 Aglomerados de Galáxias', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (278, '095', 'a', 'galaxias', '1.04.04.02-3 Aglomerados de Galáxias', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (279, '095', 'a', '1.04.04.03-1', '1.04.04.03-1 Quasares', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (280, '095', 'a', 'quasares', '1.04.04.03-1 Quasares', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (281, '095', 'a', '1.04.04.04-0', '1.04.04.04-0 Cosmologia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (282, '095', 'a', 'cosmologia', '1.04.04.04-0 Cosmologia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (283, '095', 'a', '1.04.05.00-3', '1.04.05.00-3 Astrofísica do Sistema Solar', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (284, '095', 'a', 'astrofisica', '1.04.05.00-3 Astrofísica do Sistema Solar', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (285, '095', 'a', 'do', '1.04.05.00-3 Astrofísica do Sistema Solar', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (286, '095', 'a', 'sistema', '1.04.05.00-3 Astrofísica do Sistema Solar', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (287, '095', 'a', 'solar', '1.04.05.00-3 Astrofísica do Sistema Solar', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (288, '095', 'a', '1.04.05.01-1', '1.04.05.01-1 Física Solar', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (289, '095', 'a', 'fisica', '1.04.05.01-1 Física Solar', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (290, '095', 'a', 'solar', '1.04.05.01-1 Física Solar', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (291, '095', 'a', '1.04.05.02-0', '1.04.05.02-0 Movimento da Terra', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (292, '095', 'a', 'movimento', '1.04.05.02-0 Movimento da Terra', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (293, '095', 'a', 'da', '1.04.05.02-0 Movimento da Terra', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (294, '095', 'a', 'terra', '1.04.05.02-0 Movimento da Terra', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (295, '095', 'a', '1.04.05.03-8', '1.04.05.03-8 Sistema Planetário', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (296, '095', 'a', 'sistema', '1.04.05.03-8 Sistema Planetário', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (297, '095', 'a', 'planetario', '1.04.05.03-8 Sistema Planetário', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (298, '095', 'a', '1.04.06.00-0', '1.04.06.00-0 Instrumentação Astronômica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (299, '095', 'a', 'instrumentacao', '1.04.06.00-0 Instrumentação Astronômica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (300, '095', 'a', 'astronomica', '1.04.06.00-0 Instrumentação Astronômica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (301, '095', 'a', '1.04.06.01-8', '1.04.06.01-8 Astronômia Ótica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (302, '095', 'a', 'astronomia', '1.04.06.01-8 Astronômia Ótica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (303, '095', 'a', 'otica', '1.04.06.01-8 Astronômia Ótica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (304, '095', 'a', '1.04.06.02-6', '1.04.06.02-6 Radioastronomia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (305, '095', 'a', 'radioastronomia', '1.04.06.02-6 Radioastronomia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (306, '095', 'a', '1.04.06.03-4', '1.04.06.03-4 Astronomia Espacial', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (307, '095', 'a', 'astronomia', '1.04.06.03-4 Astronomia Espacial', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (308, '095', 'a', 'espacial', '1.04.06.03-4 Astronomia Espacial', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (309, '095', 'a', '1.04.06.04-2', '1.04.06.04-2 Processamento de Dados Astronômicos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (310, '095', 'a', 'processamento', '1.04.06.04-2 Processamento de Dados Astronômicos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (311, '095', 'a', 'de', '1.04.06.04-2 Processamento de Dados Astronômicos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (312, '095', 'a', 'dados', '1.04.06.04-2 Processamento de Dados Astronômicos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (313, '095', 'a', 'astronomicos', '1.04.06.04-2 Processamento de Dados Astronômicos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (314, '095', 'a', '1.05.00.00-6', '1.05.00.00-6 Física', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (315, '095', 'a', 'fisica', '1.05.00.00-6 Física', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (316, '095', 'a', '1.05.01.00-2', '1.05.01.00-2 Física Geral', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (317, '095', 'a', 'fisica', '1.05.01.00-2 Física Geral', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (318, '095', 'a', 'geral', '1.05.01.00-2 Física Geral', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (319, '095', 'a', '1.05.01.01-0', '1.05.01.01-0 Métodos Matemáticos da Física', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (320, '095', 'a', 'metodos', '1.05.01.01-0 Métodos Matemáticos da Física', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (321, '095', 'a', 'matematicos', '1.05.01.01-0 Métodos Matemáticos da Física', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (322, '095', 'a', 'da', '1.05.01.01-0 Métodos Matemáticos da Física', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (323, '095', 'a', 'fisica', '1.05.01.01-0 Métodos Matemáticos da Física', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (324, '095', 'a', '1.05.01.02-9', '1.05.01.02-9 Física Clássica e Física Quântica; Mecânica e Campos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (325, '095', 'a', 'fisica', '1.05.01.02-9 Física Clássica e Física Quântica; Mecânica e Campos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (326, '095', 'a', 'classica', '1.05.01.02-9 Física Clássica e Física Quântica; Mecânica e Campos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (327, '095', 'a', 'fisica', '1.05.01.02-9 Física Clássica e Física Quântica; Mecânica e Campos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (328, '095', 'a', 'quantica;', '1.05.01.02-9 Física Clássica e Física Quântica; Mecânica e Campos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (329, '095', 'a', 'mecanica', '1.05.01.02-9 Física Clássica e Física Quântica; Mecânica e Campos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (330, '095', 'a', 'campos', '1.05.01.02-9 Física Clássica e Física Quântica; Mecânica e Campos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (331, '095', 'a', '1.05.01.03-7', '1.05.01.03-7 Relatividade e Gravitação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (332, '095', 'a', 'relatividade', '1.05.01.03-7 Relatividade e Gravitação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (333, '095', 'a', 'gravitacao', '1.05.01.03-7 Relatividade e Gravitação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (334, '095', 'a', '1.05.01.04-5', '1.05.01.04-5 Física Estatística e Termodinâmica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (335, '095', 'a', 'fisica', '1.05.01.04-5 Física Estatística e Termodinâmica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (336, '095', 'a', 'estatistica', '1.05.01.04-5 Física Estatística e Termodinâmica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (337, '095', 'a', 'termodinamica', '1.05.01.04-5 Física Estatística e Termodinâmica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (338, '095', 'a', '1.05.01.05-3', '1.05.01.05-3 Metrologia, Técnicas Gerais de Laboratório, Sistema de Instrumentação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (339, '095', 'a', 'metrologia,', '1.05.01.05-3 Metrologia, Técnicas Gerais de Laboratório, Sistema de Instrumentação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (340, '095', 'a', 'tecnicas', '1.05.01.05-3 Metrologia, Técnicas Gerais de Laboratório, Sistema de Instrumentação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (341, '095', 'a', 'gerais', '1.05.01.05-3 Metrologia, Técnicas Gerais de Laboratório, Sistema de Instrumentação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (342, '095', 'a', 'de', '1.05.01.05-3 Metrologia, Técnicas Gerais de Laboratório, Sistema de Instrumentação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (343, '095', 'a', 'laboratorio,', '1.05.01.05-3 Metrologia, Técnicas Gerais de Laboratório, Sistema de Instrumentação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (344, '095', 'a', 'sistema', '1.05.01.05-3 Metrologia, Técnicas Gerais de Laboratório, Sistema de Instrumentação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (345, '095', 'a', 'de', '1.05.01.05-3 Metrologia, Técnicas Gerais de Laboratório, Sistema de Instrumentação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (346, '095', 'a', 'instrumentacao', '1.05.01.05-3 Metrologia, Técnicas Gerais de Laboratório, Sistema de Instrumentação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (347, '095', 'a', '1.05.01.06-1', '1.05.01.06-1 Instrumentação Específica de Uso Geral em Física', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (348, '095', 'a', 'instrumentacao', '1.05.01.06-1 Instrumentação Específica de Uso Geral em Física', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (349, '095', 'a', 'especifica', '1.05.01.06-1 Instrumentação Específica de Uso Geral em Física', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (350, '095', 'a', 'de', '1.05.01.06-1 Instrumentação Específica de Uso Geral em Física', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (351, '095', 'a', 'uso', '1.05.01.06-1 Instrumentação Específica de Uso Geral em Física', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (352, '095', 'a', 'geral', '1.05.01.06-1 Instrumentação Específica de Uso Geral em Física', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (353, '095', 'a', 'em', '1.05.01.06-1 Instrumentação Específica de Uso Geral em Física', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (354, '095', 'a', 'fisica', '1.05.01.06-1 Instrumentação Específica de Uso Geral em Física', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (355, '095', 'a', '1.05.02.00-9', '1.05.02.00-9 Áreas Clássicas de Fenomenologia e suas Aplicações', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (356, '095', 'a', 'areas', '1.05.02.00-9 Áreas Clássicas de Fenomenologia e suas Aplicações', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (357, '095', 'a', 'classicas', '1.05.02.00-9 Áreas Clássicas de Fenomenologia e suas Aplicações', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (358, '095', 'a', 'de', '1.05.02.00-9 Áreas Clássicas de Fenomenologia e suas Aplicações', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (359, '095', 'a', 'fenomenologia', '1.05.02.00-9 Áreas Clássicas de Fenomenologia e suas Aplicações', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (360, '095', 'a', 'suas', '1.05.02.00-9 Áreas Clássicas de Fenomenologia e suas Aplicações', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (361, '095', 'a', 'aplicacoes', '1.05.02.00-9 Áreas Clássicas de Fenomenologia e suas Aplicações', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (362, '095', 'a', '1.05.02.01-7', '1.05.02.01-7 Eletricidade e Magnetismo; Campos e Partículas Carregadas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (363, '095', 'a', 'eletricidade', '1.05.02.01-7 Eletricidade e Magnetismo; Campos e Partículas Carregadas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (364, '095', 'a', 'magnetismo;', '1.05.02.01-7 Eletricidade e Magnetismo; Campos e Partículas Carregadas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (365, '095', 'a', 'campos', '1.05.02.01-7 Eletricidade e Magnetismo; Campos e Partículas Carregadas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (366, '095', 'a', 'particulas', '1.05.02.01-7 Eletricidade e Magnetismo; Campos e Partículas Carregadas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (367, '095', 'a', 'carregadas', '1.05.02.01-7 Eletricidade e Magnetismo; Campos e Partículas Carregadas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (368, '095', 'a', '1.05.02.02-5', '1.05.02.02-5 Ótica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (369, '095', 'a', 'otica', '1.05.02.02-5 Ótica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (370, '095', 'a', '1.05.02.03-3', '1.05.02.03-3 Acústica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (371, '095', 'a', 'acustica', '1.05.02.03-3 Acústica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (372, '095', 'a', '1.05.02.04-1', '1.05.02.04-1 Transferência de Calor; Processos Térmicos e Termodinâmicos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (373, '095', 'a', 'transferencia', '1.05.02.04-1 Transferência de Calor; Processos Térmicos e Termodinâmicos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (374, '095', 'a', 'de', '1.05.02.04-1 Transferência de Calor; Processos Térmicos e Termodinâmicos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (375, '095', 'a', 'calor;', '1.05.02.04-1 Transferência de Calor; Processos Térmicos e Termodinâmicos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (376, '095', 'a', 'processos', '1.05.02.04-1 Transferência de Calor; Processos Térmicos e Termodinâmicos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (377, '095', 'a', 'termicos', '1.05.02.04-1 Transferência de Calor; Processos Térmicos e Termodinâmicos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (378, '095', 'a', 'termodinamicos', '1.05.02.04-1 Transferência de Calor; Processos Térmicos e Termodinâmicos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (379, '095', 'a', '1.05.02.05-0', '1.05.02.05-0 Mecânica, Elasticidade e Reologia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (380, '095', 'a', 'mecanica,', '1.05.02.05-0 Mecânica, Elasticidade e Reologia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (381, '095', 'a', 'elasticidade', '1.05.02.05-0 Mecânica, Elasticidade e Reologia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (382, '095', 'a', 'reologia', '1.05.02.05-0 Mecânica, Elasticidade e Reologia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (383, '095', 'a', '1.05.02.06-8', '1.05.02.06-8 Dinâmica dos Fluidos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (384, '095', 'a', 'dinamica', '1.05.02.06-8 Dinâmica dos Fluidos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (385, '095', 'a', 'dos', '1.05.02.06-8 Dinâmica dos Fluidos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (386, '095', 'a', 'fluidos', '1.05.02.06-8 Dinâmica dos Fluidos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (387, '095', 'a', '1.05.03.00-5', '1.05.03.00-5 Física das Partículas Elementares e Campos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (388, '095', 'a', 'fisica', '1.05.03.00-5 Física das Partículas Elementares e Campos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (389, '095', 'a', 'das', '1.05.03.00-5 Física das Partículas Elementares e Campos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (390, '095', 'a', 'particulas', '1.05.03.00-5 Física das Partículas Elementares e Campos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (391, '095', 'a', 'elementares', '1.05.03.00-5 Física das Partículas Elementares e Campos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (392, '095', 'a', 'campos', '1.05.03.00-5 Física das Partículas Elementares e Campos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (393, '095', 'a', '1.05.03.01-3', '1.05.03.01-3 Teoria Geral de Partículas e Campos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (394, '095', 'a', 'teoria', '1.05.03.01-3 Teoria Geral de Partículas e Campos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (395, '095', 'a', 'geral', '1.05.03.01-3 Teoria Geral de Partículas e Campos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (396, '095', 'a', 'de', '1.05.03.01-3 Teoria Geral de Partículas e Campos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (397, '095', 'a', 'particulas', '1.05.03.01-3 Teoria Geral de Partículas e Campos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (398, '095', 'a', 'campos', '1.05.03.01-3 Teoria Geral de Partículas e Campos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (399, '095', 'a', '1.05.03.02-1', '1.05.03.02-1 Teorias Específicas e Modelos de Interação; Sistematica de Partículas; Raios Cósmicos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (400, '095', 'a', 'teorias', '1.05.03.02-1 Teorias Específicas e Modelos de Interação; Sistematica de Partículas; Raios Cósmicos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (401, '095', 'a', 'especificas', '1.05.03.02-1 Teorias Específicas e Modelos de Interação; Sistematica de Partículas; Raios Cósmicos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (402, '095', 'a', 'modelos', '1.05.03.02-1 Teorias Específicas e Modelos de Interação; Sistematica de Partículas; Raios Cósmicos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (403, '095', 'a', 'de', '1.05.03.02-1 Teorias Específicas e Modelos de Interação; Sistematica de Partículas; Raios Cósmicos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (404, '095', 'a', 'interacao;', '1.05.03.02-1 Teorias Específicas e Modelos de Interação; Sistematica de Partículas; Raios Cósmicos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (405, '095', 'a', 'sistematica', '1.05.03.02-1 Teorias Específicas e Modelos de Interação; Sistematica de Partículas; Raios Cósmicos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (406, '095', 'a', 'de', '1.05.03.02-1 Teorias Específicas e Modelos de Interação; Sistematica de Partículas; Raios Cósmicos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (407, '095', 'a', 'particulas;', '1.05.03.02-1 Teorias Específicas e Modelos de Interação; Sistematica de Partículas; Raios Cósmicos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (408, '095', 'a', 'raios', '1.05.03.02-1 Teorias Específicas e Modelos de Interação; Sistematica de Partículas; Raios Cósmicos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (409, '095', 'a', 'cosmicos', '1.05.03.02-1 Teorias Específicas e Modelos de Interação; Sistematica de Partículas; Raios Cósmicos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (410, '095', 'a', '1.05.03.03-0', '1.05.03.03-0 Reações Específicas e Fenomiologia de Partículas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (411, '095', 'a', 'reacoes', '1.05.03.03-0 Reações Específicas e Fenomiologia de Partículas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (412, '095', 'a', 'especificas', '1.05.03.03-0 Reações Específicas e Fenomiologia de Partículas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (413, '095', 'a', 'fenomiologia', '1.05.03.03-0 Reações Específicas e Fenomiologia de Partículas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (414, '095', 'a', 'de', '1.05.03.03-0 Reações Específicas e Fenomiologia de Partículas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (415, '095', 'a', 'particulas', '1.05.03.03-0 Reações Específicas e Fenomiologia de Partículas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (416, '095', 'a', '1.05.03.04-8', '1.05.03.04-8 Propriedades de Partículas Específicas e Ressonâncias', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (417, '095', 'a', 'propriedades', '1.05.03.04-8 Propriedades de Partículas Específicas e Ressonâncias', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (418, '095', 'a', 'de', '1.05.03.04-8 Propriedades de Partículas Específicas e Ressonâncias', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (419, '095', 'a', 'particulas', '1.05.03.04-8 Propriedades de Partículas Específicas e Ressonâncias', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (420, '095', 'a', 'especificas', '1.05.03.04-8 Propriedades de Partículas Específicas e Ressonâncias', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (421, '095', 'a', 'ressonancias', '1.05.03.04-8 Propriedades de Partículas Específicas e Ressonâncias', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (422, '095', 'a', '1.05.04.00-1', '1.05.04.00-1 Física Nuclear', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (423, '095', 'a', 'fisica', '1.05.04.00-1 Física Nuclear', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (424, '095', 'a', 'nuclear', '1.05.04.00-1 Física Nuclear', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (425, '095', 'a', '1.05.04.01-0', '1.05.04.01-0 Estrutura Nuclear', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (426, '095', 'a', 'estrutura', '1.05.04.01-0 Estrutura Nuclear', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (427, '095', 'a', 'nuclear', '1.05.04.01-0 Estrutura Nuclear', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (428, '095', 'a', '1.05.04.02-8', '1.05.04.02-8 Desintegração Nuclear e Radioatividade', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (429, '095', 'a', 'desintegracao', '1.05.04.02-8 Desintegração Nuclear e Radioatividade', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (430, '095', 'a', 'nuclear', '1.05.04.02-8 Desintegração Nuclear e Radioatividade', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (431, '095', 'a', 'radioatividade', '1.05.04.02-8 Desintegração Nuclear e Radioatividade', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (432, '095', 'a', '1.05.04.03-6', '1.05.04.03-6 Reações Nucleares e Espalhamento Geral', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (433, '095', 'a', 'reacoes', '1.05.04.03-6 Reações Nucleares e Espalhamento Geral', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (434, '095', 'a', 'nucleares', '1.05.04.03-6 Reações Nucleares e Espalhamento Geral', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (435, '095', 'a', 'espalhamento', '1.05.04.03-6 Reações Nucleares e Espalhamento Geral', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (436, '095', 'a', 'geral', '1.05.04.03-6 Reações Nucleares e Espalhamento Geral', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (437, '095', 'a', '1.05.04.04-4', '1.05.04.04-4 Reações Nucleares e Espalhamento (Reações Específicas)', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (438, '095', 'a', 'reacoes', '1.05.04.04-4 Reações Nucleares e Espalhamento (Reações Específicas)', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (439, '095', 'a', 'nucleares', '1.05.04.04-4 Reações Nucleares e Espalhamento (Reações Específicas)', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (440, '095', 'a', 'espalhamento', '1.05.04.04-4 Reações Nucleares e Espalhamento (Reações Específicas)', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (441, '095', 'a', '(reacoes', '1.05.04.04-4 Reações Nucleares e Espalhamento (Reações Específicas)', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (442, '095', 'a', 'especificas)', '1.05.04.04-4 Reações Nucleares e Espalhamento (Reações Específicas)', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (443, '095', 'a', '1.05.04.05-2', '1.05.04.05-2 Propriedades de Núcleos Específicos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (444, '095', 'a', 'propriedades', '1.05.04.05-2 Propriedades de Núcleos Específicos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (445, '095', 'a', 'de', '1.05.04.05-2 Propriedades de Núcleos Específicos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (446, '095', 'a', 'nucleos', '1.05.04.05-2 Propriedades de Núcleos Específicos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (447, '095', 'a', 'especificos', '1.05.04.05-2 Propriedades de Núcleos Específicos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (448, '095', 'a', '1.05.04.06-0', '1.05.04.06-0 Métodos Experimentais e Instrumentação para Partículas Elementares e Física Nuclear', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (449, '095', 'a', 'metodos', '1.05.04.06-0 Métodos Experimentais e Instrumentação para Partículas Elementares e Física Nuclear', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (450, '095', 'a', 'experimentais', '1.05.04.06-0 Métodos Experimentais e Instrumentação para Partículas Elementares e Física Nuclear', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (451, '095', 'a', 'instrumentacao', '1.05.04.06-0 Métodos Experimentais e Instrumentação para Partículas Elementares e Física Nuclear', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (452, '095', 'a', 'para', '1.05.04.06-0 Métodos Experimentais e Instrumentação para Partículas Elementares e Física Nuclear', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (453, '095', 'a', 'particulas', '1.05.04.06-0 Métodos Experimentais e Instrumentação para Partículas Elementares e Física Nuclear', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (454, '095', 'a', 'elementares', '1.05.04.06-0 Métodos Experimentais e Instrumentação para Partículas Elementares e Física Nuclear', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (455, '095', 'a', 'fisica', '1.05.04.06-0 Métodos Experimentais e Instrumentação para Partículas Elementares e Física Nuclear', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (456, '095', 'a', 'nuclear', '1.05.04.06-0 Métodos Experimentais e Instrumentação para Partículas Elementares e Física Nuclear', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (457, '095', 'a', '1.05.05.00-8', '1.05.05.00-8 Física Atômica e Molécular', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (458, '095', 'a', 'fisica', '1.05.05.00-8 Física Atômica e Molécular', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (459, '095', 'a', 'atomica', '1.05.05.00-8 Física Atômica e Molécular', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (460, '095', 'a', 'molecular', '1.05.05.00-8 Física Atômica e Molécular', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (461, '095', 'a', '1.05.05.01-6', '1.05.05.01-6 Estrutura Eletrônica de Átomos e Moléculas; Teoria', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (462, '095', 'a', 'estrutura', '1.05.05.01-6 Estrutura Eletrônica de Átomos e Moléculas; Teoria', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (463, '095', 'a', 'eletronica', '1.05.05.01-6 Estrutura Eletrônica de Átomos e Moléculas; Teoria', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (464, '095', 'a', 'de', '1.05.05.01-6 Estrutura Eletrônica de Átomos e Moléculas; Teoria', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (465, '095', 'a', 'atomos', '1.05.05.01-6 Estrutura Eletrônica de Átomos e Moléculas; Teoria', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (466, '095', 'a', 'moleculas;', '1.05.05.01-6 Estrutura Eletrônica de Átomos e Moléculas; Teoria', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (467, '095', 'a', 'teoria', '1.05.05.01-6 Estrutura Eletrônica de Átomos e Moléculas; Teoria', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (468, '095', 'a', '1.05.05.02-4', '1.05.05.02-4 Espectros Atômicos e Integração de Fótons', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (469, '095', 'a', 'espectros', '1.05.05.02-4 Espectros Atômicos e Integração de Fótons', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (470, '095', 'a', 'atomicos', '1.05.05.02-4 Espectros Atômicos e Integração de Fótons', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (471, '095', 'a', 'integracao', '1.05.05.02-4 Espectros Atômicos e Integração de Fótons', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (472, '095', 'a', 'de', '1.05.05.02-4 Espectros Atômicos e Integração de Fótons', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (473, '095', 'a', 'fotons', '1.05.05.02-4 Espectros Atômicos e Integração de Fótons', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (474, '095', 'a', '1.05.05.03-2', '1.05.05.03-2 Espectros Moléculares e Interações de Fótons com Moléculas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (475, '095', 'a', 'espectros', '1.05.05.03-2 Espectros Moléculares e Interações de Fótons com Moléculas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (476, '095', 'a', 'moleculares', '1.05.05.03-2 Espectros Moléculares e Interações de Fótons com Moléculas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (477, '095', 'a', 'interacoes', '1.05.05.03-2 Espectros Moléculares e Interações de Fótons com Moléculas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (478, '095', 'a', 'de', '1.05.05.03-2 Espectros Moléculares e Interações de Fótons com Moléculas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (479, '095', 'a', 'fotons', '1.05.05.03-2 Espectros Moléculares e Interações de Fótons com Moléculas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (480, '095', 'a', 'com', '1.05.05.03-2 Espectros Moléculares e Interações de Fótons com Moléculas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (481, '095', 'a', 'moleculas', '1.05.05.03-2 Espectros Moléculares e Interações de Fótons com Moléculas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (482, '095', 'a', '1.05.05.04-0', '1.05.05.04-0 Processos de Colisão e Interações de Átomos e Moléculas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (483, '095', 'a', 'processos', '1.05.05.04-0 Processos de Colisão e Interações de Átomos e Moléculas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (484, '095', 'a', 'de', '1.05.05.04-0 Processos de Colisão e Interações de Átomos e Moléculas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (485, '095', 'a', 'colisao', '1.05.05.04-0 Processos de Colisão e Interações de Átomos e Moléculas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (486, '095', 'a', 'interacoes', '1.05.05.04-0 Processos de Colisão e Interações de Átomos e Moléculas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (487, '095', 'a', 'de', '1.05.05.04-0 Processos de Colisão e Interações de Átomos e Moléculas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (488, '095', 'a', 'atomos', '1.05.05.04-0 Processos de Colisão e Interações de Átomos e Moléculas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (489, '095', 'a', 'moleculas', '1.05.05.04-0 Processos de Colisão e Interações de Átomos e Moléculas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (490, '095', 'a', '1.05.05.05-9', '1.05.05.05-9 Inf.Sobre Átomos e Moléculas Obtidos Experimentalmente;Instrumentação e Técnicas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (491, '095', 'a', 'inf.sobre', '1.05.05.05-9 Inf.Sobre Átomos e Moléculas Obtidos Experimentalmente;Instrumentação e Técnicas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (492, '095', 'a', 'atomos', '1.05.05.05-9 Inf.Sobre Átomos e Moléculas Obtidos Experimentalmente;Instrumentação e Técnicas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (493, '095', 'a', 'moleculas', '1.05.05.05-9 Inf.Sobre Átomos e Moléculas Obtidos Experimentalmente;Instrumentação e Técnicas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (494, '095', 'a', 'obtidos', '1.05.05.05-9 Inf.Sobre Átomos e Moléculas Obtidos Experimentalmente;Instrumentação e Técnicas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (495, '095', 'a', 'experimentalmente;instrumentacao', '1.05.05.05-9 Inf.Sobre Átomos e Moléculas Obtidos Experimentalmente;Instrumentação e Técnicas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (496, '095', 'a', 'tecnicas', '1.05.05.05-9 Inf.Sobre Átomos e Moléculas Obtidos Experimentalmente;Instrumentação e Técnicas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (497, '095', 'a', '1.05.05.06-7', '1.05.05.06-7 Estudos de Átomos e Moléculas Especiais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (498, '095', 'a', 'estudos', '1.05.05.06-7 Estudos de Átomos e Moléculas Especiais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (499, '095', 'a', 'de', '1.05.05.06-7 Estudos de Átomos e Moléculas Especiais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (500, '095', 'a', 'atomos', '1.05.05.06-7 Estudos de Átomos e Moléculas Especiais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (501, '095', 'a', 'moleculas', '1.05.05.06-7 Estudos de Átomos e Moléculas Especiais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (502, '095', 'a', 'especiais', '1.05.05.06-7 Estudos de Átomos e Moléculas Especiais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (503, '095', 'a', '1.05.06.00-4', '1.05.06.00-4 Física dos Fluidos, Física de Plasmas e Descargas Elétricas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (504, '095', 'a', 'fisica', '1.05.06.00-4 Física dos Fluidos, Física de Plasmas e Descargas Elétricas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (505, '095', 'a', 'dos', '1.05.06.00-4 Física dos Fluidos, Física de Plasmas e Descargas Elétricas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (506, '095', 'a', 'fluidos,', '1.05.06.00-4 Física dos Fluidos, Física de Plasmas e Descargas Elétricas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (507, '095', 'a', 'fisica', '1.05.06.00-4 Física dos Fluidos, Física de Plasmas e Descargas Elétricas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (508, '095', 'a', 'de', '1.05.06.00-4 Física dos Fluidos, Física de Plasmas e Descargas Elétricas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (509, '095', 'a', 'plasmas', '1.05.06.00-4 Física dos Fluidos, Física de Plasmas e Descargas Elétricas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (510, '095', 'a', 'descargas', '1.05.06.00-4 Física dos Fluidos, Física de Plasmas e Descargas Elétricas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (511, '095', 'a', 'eletricas', '1.05.06.00-4 Física dos Fluidos, Física de Plasmas e Descargas Elétricas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (512, '095', 'a', '1.05.06.01-2', '1.05.06.01-2 Cinética e Teoria de Transporte de Fluidos; Propriedades Físicas de Gases', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (513, '095', 'a', 'cinetica', '1.05.06.01-2 Cinética e Teoria de Transporte de Fluidos; Propriedades Físicas de Gases', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (514, '095', 'a', 'teoria', '1.05.06.01-2 Cinética e Teoria de Transporte de Fluidos; Propriedades Físicas de Gases', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (515, '095', 'a', 'de', '1.05.06.01-2 Cinética e Teoria de Transporte de Fluidos; Propriedades Físicas de Gases', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (516, '095', 'a', 'transporte', '1.05.06.01-2 Cinética e Teoria de Transporte de Fluidos; Propriedades Físicas de Gases', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (517, '095', 'a', 'de', '1.05.06.01-2 Cinética e Teoria de Transporte de Fluidos; Propriedades Físicas de Gases', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (518, '095', 'a', 'fluidos;', '1.05.06.01-2 Cinética e Teoria de Transporte de Fluidos; Propriedades Físicas de Gases', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (519, '095', 'a', 'propriedades', '1.05.06.01-2 Cinética e Teoria de Transporte de Fluidos; Propriedades Físicas de Gases', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (520, '095', 'a', 'fisicas', '1.05.06.01-2 Cinética e Teoria de Transporte de Fluidos; Propriedades Físicas de Gases', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (521, '095', 'a', 'de', '1.05.06.01-2 Cinética e Teoria de Transporte de Fluidos; Propriedades Físicas de Gases', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (522, '095', 'a', 'gases', '1.05.06.01-2 Cinética e Teoria de Transporte de Fluidos; Propriedades Físicas de Gases', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (523, '095', 'a', '1.05.06.02-0', '1.05.06.02-0 Física de Plasmas e Descargas Elétricas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (524, '095', 'a', 'fisica', '1.05.06.02-0 Física de Plasmas e Descargas Elétricas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (525, '095', 'a', 'de', '1.05.06.02-0 Física de Plasmas e Descargas Elétricas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (526, '095', 'a', 'plasmas', '1.05.06.02-0 Física de Plasmas e Descargas Elétricas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (527, '095', 'a', 'descargas', '1.05.06.02-0 Física de Plasmas e Descargas Elétricas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (528, '095', 'a', 'eletricas', '1.05.06.02-0 Física de Plasmas e Descargas Elétricas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (529, '095', 'a', '1.05.07.00-0', '1.05.07.00-0 Física da Matéria Condensada', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (530, '095', 'a', 'fisica', '1.05.07.00-0 Física da Matéria Condensada', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (531, '095', 'a', 'da', '1.05.07.00-0 Física da Matéria Condensada', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (532, '095', 'a', 'materia', '1.05.07.00-0 Física da Matéria Condensada', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (533, '095', 'a', 'condensada', '1.05.07.00-0 Física da Matéria Condensada', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (534, '095', 'a', '1.05.07.01-9', '1.05.07.01-9 Estrutura de Líquidos e Sólidos; Cristalografia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (535, '095', 'a', 'estrutura', '1.05.07.01-9 Estrutura de Líquidos e Sólidos; Cristalografia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (536, '095', 'a', 'de', '1.05.07.01-9 Estrutura de Líquidos e Sólidos; Cristalografia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (537, '095', 'a', 'liquidos', '1.05.07.01-9 Estrutura de Líquidos e Sólidos; Cristalografia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (538, '095', 'a', 'solidos;', '1.05.07.01-9 Estrutura de Líquidos e Sólidos; Cristalografia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (539, '095', 'a', 'cristalografia', '1.05.07.01-9 Estrutura de Líquidos e Sólidos; Cristalografia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (540, '095', 'a', '1.05.07.02-7', '1.05.07.02-7 Propriedades Mecânicas e Acústicas da Matéria Condensada', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (541, '095', 'a', 'propriedades', '1.05.07.02-7 Propriedades Mecânicas e Acústicas da Matéria Condensada', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (542, '095', 'a', 'mecanicas', '1.05.07.02-7 Propriedades Mecânicas e Acústicas da Matéria Condensada', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (543, '095', 'a', 'acusticas', '1.05.07.02-7 Propriedades Mecânicas e Acústicas da Matéria Condensada', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (544, '095', 'a', 'da', '1.05.07.02-7 Propriedades Mecânicas e Acústicas da Matéria Condensada', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (545, '095', 'a', 'materia', '1.05.07.02-7 Propriedades Mecânicas e Acústicas da Matéria Condensada', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (546, '095', 'a', 'condensada', '1.05.07.02-7 Propriedades Mecânicas e Acústicas da Matéria Condensada', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (547, '095', 'a', '1.05.07.03-5', '1.05.07.03-5 Dinâmica da Rede e Estatística de Cristais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (548, '095', 'a', 'dinamica', '1.05.07.03-5 Dinâmica da Rede e Estatística de Cristais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (549, '095', 'a', 'da', '1.05.07.03-5 Dinâmica da Rede e Estatística de Cristais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (550, '095', 'a', 'rede', '1.05.07.03-5 Dinâmica da Rede e Estatística de Cristais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (551, '095', 'a', 'estatistica', '1.05.07.03-5 Dinâmica da Rede e Estatística de Cristais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (552, '095', 'a', 'de', '1.05.07.03-5 Dinâmica da Rede e Estatística de Cristais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (553, '095', 'a', 'cristais', '1.05.07.03-5 Dinâmica da Rede e Estatística de Cristais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (554, '095', 'a', '1.05.07.04-3', '1.05.07.04-3 Equação de Estado, Equilíbrio de Fases e Transições de Fase', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (555, '095', 'a', 'equacao', '1.05.07.04-3 Equação de Estado, Equilíbrio de Fases e Transições de Fase', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (556, '095', 'a', 'de', '1.05.07.04-3 Equação de Estado, Equilíbrio de Fases e Transições de Fase', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (557, '095', 'a', 'estado,', '1.05.07.04-3 Equação de Estado, Equilíbrio de Fases e Transições de Fase', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (558, '095', 'a', 'equilibrio', '1.05.07.04-3 Equação de Estado, Equilíbrio de Fases e Transições de Fase', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (559, '095', 'a', 'de', '1.05.07.04-3 Equação de Estado, Equilíbrio de Fases e Transições de Fase', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (560, '095', 'a', 'fases', '1.05.07.04-3 Equação de Estado, Equilíbrio de Fases e Transições de Fase', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (561, '095', 'a', 'transicoes', '1.05.07.04-3 Equação de Estado, Equilíbrio de Fases e Transições de Fase', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (562, '095', 'a', 'de', '1.05.07.04-3 Equação de Estado, Equilíbrio de Fases e Transições de Fase', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (563, '095', 'a', 'fase', '1.05.07.04-3 Equação de Estado, Equilíbrio de Fases e Transições de Fase', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (564, '095', 'a', '1.05.07.05-1', '1.05.07.05-1 Propriedades Térmicas da Matéria Condensada', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (565, '095', 'a', 'propriedades', '1.05.07.05-1 Propriedades Térmicas da Matéria Condensada', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (566, '095', 'a', 'termicas', '1.05.07.05-1 Propriedades Térmicas da Matéria Condensada', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (567, '095', 'a', 'da', '1.05.07.05-1 Propriedades Térmicas da Matéria Condensada', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (568, '095', 'a', 'materia', '1.05.07.05-1 Propriedades Térmicas da Matéria Condensada', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (569, '095', 'a', 'condensada', '1.05.07.05-1 Propriedades Térmicas da Matéria Condensada', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (570, '095', 'a', '1.05.07.06-0', '1.05.07.06-0 Propriedades de Transportes de Matéria Condensada (Não Eletrônicas)', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (571, '095', 'a', 'propriedades', '1.05.07.06-0 Propriedades de Transportes de Matéria Condensada (Não Eletrônicas)', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (572, '095', 'a', 'de', '1.05.07.06-0 Propriedades de Transportes de Matéria Condensada (Não Eletrônicas)', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (573, '095', 'a', 'transportes', '1.05.07.06-0 Propriedades de Transportes de Matéria Condensada (Não Eletrônicas)', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (574, '095', 'a', 'de', '1.05.07.06-0 Propriedades de Transportes de Matéria Condensada (Não Eletrônicas)', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (575, '095', 'a', 'materia', '1.05.07.06-0 Propriedades de Transportes de Matéria Condensada (Não Eletrônicas)', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (576, '095', 'a', 'condensada', '1.05.07.06-0 Propriedades de Transportes de Matéria Condensada (Não Eletrônicas)', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (577, '095', 'a', '(nao', '1.05.07.06-0 Propriedades de Transportes de Matéria Condensada (Não Eletrônicas)', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (578, '095', 'a', 'eletronicas)', '1.05.07.06-0 Propriedades de Transportes de Matéria Condensada (Não Eletrônicas)', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (579, '095', 'a', '1.05.07.07-8', '1.05.07.07-8 Campos Quânticos e Sólidos, Hélio, Líquido, Sólido', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (580, '095', 'a', 'campos', '1.05.07.07-8 Campos Quânticos e Sólidos, Hélio, Líquido, Sólido', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (581, '095', 'a', 'quanticos', '1.05.07.07-8 Campos Quânticos e Sólidos, Hélio, Líquido, Sólido', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (582, '095', 'a', 'solidos,', '1.05.07.07-8 Campos Quânticos e Sólidos, Hélio, Líquido, Sólido', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (583, '095', 'a', 'helio,', '1.05.07.07-8 Campos Quânticos e Sólidos, Hélio, Líquido, Sólido', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (584, '095', 'a', 'liquido,', '1.05.07.07-8 Campos Quânticos e Sólidos, Hélio, Líquido, Sólido', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (585, '095', 'a', 'solido', '1.05.07.07-8 Campos Quânticos e Sólidos, Hélio, Líquido, Sólido', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (586, '095', 'a', '1.05.07.08-6', '1.05.07.08-6 Superfícies e Interfaces; Películas e Filamentos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (587, '095', 'a', 'superficies', '1.05.07.08-6 Superfícies e Interfaces; Películas e Filamentos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (588, '095', 'a', 'interfaces;', '1.05.07.08-6 Superfícies e Interfaces; Películas e Filamentos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (589, '095', 'a', 'peliculas', '1.05.07.08-6 Superfícies e Interfaces; Películas e Filamentos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (590, '095', 'a', 'filamentos', '1.05.07.08-6 Superfícies e Interfaces; Películas e Filamentos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (591, '095', 'a', '1.05.07.09-4', '1.05.07.09-4 Estados Eletrônicos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (592, '095', 'a', 'estados', '1.05.07.09-4 Estados Eletrônicos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (593, '095', 'a', 'eletronicos', '1.05.07.09-4 Estados Eletrônicos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (594, '095', 'a', '1.05.07.10-8', '1.05.07.10-8 Transp.Eletrônicos e Prop. Elétricas de Superfícies; Interfaces e Películas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (595, '095', 'a', 'transp.eletronicos', '1.05.07.10-8 Transp.Eletrônicos e Prop. Elétricas de Superfícies; Interfaces e Películas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (596, '095', 'a', 'prop.', '1.05.07.10-8 Transp.Eletrônicos e Prop. Elétricas de Superfícies; Interfaces e Películas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (597, '095', 'a', 'eletricas', '1.05.07.10-8 Transp.Eletrônicos e Prop. Elétricas de Superfícies; Interfaces e Películas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (598, '095', 'a', 'de', '1.05.07.10-8 Transp.Eletrônicos e Prop. Elétricas de Superfícies; Interfaces e Películas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (599, '095', 'a', 'superficies;', '1.05.07.10-8 Transp.Eletrônicos e Prop. Elétricas de Superfícies; Interfaces e Películas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (600, '095', 'a', 'interfaces', '1.05.07.10-8 Transp.Eletrônicos e Prop. Elétricas de Superfícies; Interfaces e Películas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (601, '095', 'a', 'peliculas', '1.05.07.10-8 Transp.Eletrônicos e Prop. Elétricas de Superfícies; Interfaces e Películas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (602, '095', 'a', '1.05.07.11-6', '1.05.07.11-6 Estruturas Eletrônicas e Propriedades Elétricas de Superfícies Interfaces e Películas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (603, '095', 'a', 'estruturas', '1.05.07.11-6 Estruturas Eletrônicas e Propriedades Elétricas de Superfícies Interfaces e Películas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (604, '095', 'a', 'eletronicas', '1.05.07.11-6 Estruturas Eletrônicas e Propriedades Elétricas de Superfícies Interfaces e Películas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (605, '095', 'a', 'propriedades', '1.05.07.11-6 Estruturas Eletrônicas e Propriedades Elétricas de Superfícies Interfaces e Películas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (606, '095', 'a', 'eletricas', '1.05.07.11-6 Estruturas Eletrônicas e Propriedades Elétricas de Superfícies Interfaces e Películas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (607, '095', 'a', 'de', '1.05.07.11-6 Estruturas Eletrônicas e Propriedades Elétricas de Superfícies Interfaces e Películas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (608, '095', 'a', 'superficies', '1.05.07.11-6 Estruturas Eletrônicas e Propriedades Elétricas de Superfícies Interfaces e Películas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (609, '095', 'a', 'interfaces', '1.05.07.11-6 Estruturas Eletrônicas e Propriedades Elétricas de Superfícies Interfaces e Películas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (610, '095', 'a', 'peliculas', '1.05.07.11-6 Estruturas Eletrônicas e Propriedades Elétricas de Superfícies Interfaces e Películas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (611, '095', 'a', '1.05.07.12-4', '1.05.07.12-4 Supercondutividade', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (612, '095', 'a', 'supercondutividade', '1.05.07.12-4 Supercondutividade', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (613, '095', 'a', '1.05.07.13-2', '1.05.07.13-2 Materiais Magnéticos e Propriedades Magnéticas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (614, '095', 'a', 'materiais', '1.05.07.13-2 Materiais Magnéticos e Propriedades Magnéticas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (615, '095', 'a', 'magneticos', '1.05.07.13-2 Materiais Magnéticos e Propriedades Magnéticas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (616, '095', 'a', 'propriedades', '1.05.07.13-2 Materiais Magnéticos e Propriedades Magnéticas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (617, '095', 'a', 'magneticas', '1.05.07.13-2 Materiais Magnéticos e Propriedades Magnéticas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (618, '095', 'a', '1.05.07.14-0', '1.05.07.14-0 Ressonância Mag.e Relax.Na Mat.Condens;Efeitos Mosbauer;Corr.Ang.Pertubada', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (619, '095', 'a', 'ressonancia', '1.05.07.14-0 Ressonância Mag.e Relax.Na Mat.Condens;Efeitos Mosbauer;Corr.Ang.Pertubada', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (620, '095', 'a', 'mag.e', '1.05.07.14-0 Ressonância Mag.e Relax.Na Mat.Condens;Efeitos Mosbauer;Corr.Ang.Pertubada', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (621, '095', 'a', 'relax.na', '1.05.07.14-0 Ressonância Mag.e Relax.Na Mat.Condens;Efeitos Mosbauer;Corr.Ang.Pertubada', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (622, '095', 'a', 'mat.condens;efeitos', '1.05.07.14-0 Ressonância Mag.e Relax.Na Mat.Condens;Efeitos Mosbauer;Corr.Ang.Pertubada', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (623, '095', 'a', 'mosbauer;corr.ang.pertubada', '1.05.07.14-0 Ressonância Mag.e Relax.Na Mat.Condens;Efeitos Mosbauer;Corr.Ang.Pertubada', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (624, '095', 'a', '1.05.07.15-9', '1.05.07.15-9 Materiais Dielétricos e Propriedades Dielétricas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (625, '095', 'a', 'materiais', '1.05.07.15-9 Materiais Dielétricos e Propriedades Dielétricas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (626, '095', 'a', 'dieletricos', '1.05.07.15-9 Materiais Dielétricos e Propriedades Dielétricas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (627, '095', 'a', 'propriedades', '1.05.07.15-9 Materiais Dielétricos e Propriedades Dielétricas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (628, '095', 'a', 'dieletricas', '1.05.07.15-9 Materiais Dielétricos e Propriedades Dielétricas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (629, '095', 'a', '1.05.07.16-7', '1.05.07.16-7 Prop.Óticas e Espectrosc.da Mat.Condens;Outras Inter.da Mat.Com Rad.e Part.', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (630, '095', 'a', 'prop.oticas', '1.05.07.16-7 Prop.Óticas e Espectrosc.da Mat.Condens;Outras Inter.da Mat.Com Rad.e Part.', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (631, '095', 'a', 'espectrosc.da', '1.05.07.16-7 Prop.Óticas e Espectrosc.da Mat.Condens;Outras Inter.da Mat.Com Rad.e Part.', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (632, '095', 'a', 'mat.condens;outras', '1.05.07.16-7 Prop.Óticas e Espectrosc.da Mat.Condens;Outras Inter.da Mat.Com Rad.e Part.', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (633, '095', 'a', 'inter.da', '1.05.07.16-7 Prop.Óticas e Espectrosc.da Mat.Condens;Outras Inter.da Mat.Com Rad.e Part.', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (634, '095', 'a', 'mat.com', '1.05.07.16-7 Prop.Óticas e Espectrosc.da Mat.Condens;Outras Inter.da Mat.Com Rad.e Part.', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (635, '095', 'a', 'rad.e', '1.05.07.16-7 Prop.Óticas e Espectrosc.da Mat.Condens;Outras Inter.da Mat.Com Rad.e Part.', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (636, '095', 'a', 'part.', '1.05.07.16-7 Prop.Óticas e Espectrosc.da Mat.Condens;Outras Inter.da Mat.Com Rad.e Part.', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (637, '095', 'a', '1.05.07.17-5', '1.05.07.17-5 Emissão Eletrônica e Iônica por Líquidos e Sólidos; Fenômenos de Impacto', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (638, '095', 'a', 'emissao', '1.05.07.17-5 Emissão Eletrônica e Iônica por Líquidos e Sólidos; Fenômenos de Impacto', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (639, '095', 'a', 'eletronica', '1.05.07.17-5 Emissão Eletrônica e Iônica por Líquidos e Sólidos; Fenômenos de Impacto', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (640, '095', 'a', 'ionica', '1.05.07.17-5 Emissão Eletrônica e Iônica por Líquidos e Sólidos; Fenômenos de Impacto', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (641, '095', 'a', 'por', '1.05.07.17-5 Emissão Eletrônica e Iônica por Líquidos e Sólidos; Fenômenos de Impacto', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (642, '095', 'a', 'liquidos', '1.05.07.17-5 Emissão Eletrônica e Iônica por Líquidos e Sólidos; Fenômenos de Impacto', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (643, '095', 'a', 'solidos;', '1.05.07.17-5 Emissão Eletrônica e Iônica por Líquidos e Sólidos; Fenômenos de Impacto', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (644, '095', 'a', 'fenomenos', '1.05.07.17-5 Emissão Eletrônica e Iônica por Líquidos e Sólidos; Fenômenos de Impacto', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (645, '095', 'a', 'de', '1.05.07.17-5 Emissão Eletrônica e Iônica por Líquidos e Sólidos; Fenômenos de Impacto', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (646, '095', 'a', 'impacto', '1.05.07.17-5 Emissão Eletrônica e Iônica por Líquidos e Sólidos; Fenômenos de Impacto', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (647, '095', 'a', '1.06.00.00-0', '1.06.00.00-0 Química', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (648, '095', 'a', 'quimica', '1.06.00.00-0 Química', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (649, '095', 'a', '1.06.01.00-7', '1.06.01.00-7 Química Orgânica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (650, '095', 'a', 'quimica', '1.06.01.00-7 Química Orgânica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (651, '095', 'a', 'organica', '1.06.01.00-7 Química Orgânica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (652, '095', 'a', '1.06.01.01-5', '1.06.01.01-5 Estrutura, Conformação e Estereoquímica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (653, '095', 'a', 'estrutura,', '1.06.01.01-5 Estrutura, Conformação e Estereoquímica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (654, '095', 'a', 'conformacao', '1.06.01.01-5 Estrutura, Conformação e Estereoquímica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (655, '095', 'a', 'estereoquimica', '1.06.01.01-5 Estrutura, Conformação e Estereoquímica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (656, '095', 'a', '1.06.01.02-3', '1.06.01.02-3 Sintese Orgânica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (657, '095', 'a', 'sintese', '1.06.01.02-3 Sintese Orgânica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (658, '095', 'a', 'organica', '1.06.01.02-3 Sintese Orgânica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (659, '095', 'a', '1.06.01.03-1', '1.06.01.03-1 Fisico-Química Orgânica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (660, '095', 'a', 'fisico-quimica', '1.06.01.03-1 Fisico-Química Orgânica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (661, '095', 'a', 'organica', '1.06.01.03-1 Fisico-Química Orgânica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (662, '095', 'a', '1.06.01.04-0', '1.06.01.04-0 Fotoquímica Orgânica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (663, '095', 'a', 'fotoquimica', '1.06.01.04-0 Fotoquímica Orgânica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (664, '095', 'a', 'organica', '1.06.01.04-0 Fotoquímica Orgânica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (665, '095', 'a', '1.06.01.05-8', '1.06.01.05-8 Química dos Produtos Naturais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (666, '095', 'a', 'quimica', '1.06.01.05-8 Química dos Produtos Naturais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (667, '095', 'a', 'dos', '1.06.01.05-8 Química dos Produtos Naturais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (668, '095', 'a', 'produtos', '1.06.01.05-8 Química dos Produtos Naturais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (669, '095', 'a', 'naturais', '1.06.01.05-8 Química dos Produtos Naturais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (670, '095', 'a', '1.06.01.06-6', '1.06.01.06-6 Evolução, Sistemática e Ecologia Química', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (671, '095', 'a', 'evolucao,', '1.06.01.06-6 Evolução, Sistemática e Ecologia Química', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (672, '095', 'a', 'sistematica', '1.06.01.06-6 Evolução, Sistemática e Ecologia Química', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (673, '095', 'a', 'ecologia', '1.06.01.06-6 Evolução, Sistemática e Ecologia Química', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (674, '095', 'a', 'quimica', '1.06.01.06-6 Evolução, Sistemática e Ecologia Química', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (675, '095', 'a', '1.06.01.07-4', '1.06.01.07-4 Polimeros e Colóides', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (676, '095', 'a', 'polimeros', '1.06.01.07-4 Polimeros e Colóides', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (677, '095', 'a', 'coloides', '1.06.01.07-4 Polimeros e Colóides', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (678, '095', 'a', '1.06.02.00-3', '1.06.02.00-3 Química Inorgânica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (679, '095', 'a', 'quimica', '1.06.02.00-3 Química Inorgânica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (680, '095', 'a', 'inorganica', '1.06.02.00-3 Química Inorgânica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (681, '095', 'a', '1.06.02.01-1', '1.06.02.01-1 Campos de Coordenação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (682, '095', 'a', 'campos', '1.06.02.01-1 Campos de Coordenação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (683, '095', 'a', 'de', '1.06.02.01-1 Campos de Coordenação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (684, '095', 'a', 'coordenacao', '1.06.02.01-1 Campos de Coordenação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (685, '095', 'a', '1.06.02.02-0', '1.06.02.02-0 Não-Metais e Seus Compostos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (686, '095', 'a', 'nao-metais', '1.06.02.02-0 Não-Metais e Seus Compostos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (687, '095', 'a', 'seus', '1.06.02.02-0 Não-Metais e Seus Compostos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (688, '095', 'a', 'compostos', '1.06.02.02-0 Não-Metais e Seus Compostos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (689, '095', 'a', '1.06.02.03-8', '1.06.02.03-8 Compostos Organo-Metálicos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (690, '095', 'a', 'compostos', '1.06.02.03-8 Compostos Organo-Metálicos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (691, '095', 'a', 'organo-metalicos', '1.06.02.03-8 Compostos Organo-Metálicos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (692, '095', 'a', '1.06.02.04-6', '1.06.02.04-6 Determinação de Estrutura de Compostos Inorgânicos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (693, '095', 'a', 'determinacao', '1.06.02.04-6 Determinação de Estrutura de Compostos Inorgânicos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (694, '095', 'a', 'de', '1.06.02.04-6 Determinação de Estrutura de Compostos Inorgânicos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (695, '095', 'a', 'estrutura', '1.06.02.04-6 Determinação de Estrutura de Compostos Inorgânicos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (696, '095', 'a', 'de', '1.06.02.04-6 Determinação de Estrutura de Compostos Inorgânicos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (697, '095', 'a', 'compostos', '1.06.02.04-6 Determinação de Estrutura de Compostos Inorgânicos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (698, '095', 'a', 'inorganicos', '1.06.02.04-6 Determinação de Estrutura de Compostos Inorgânicos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (699, '095', 'a', '1.06.02.05-4', '1.06.02.05-4 Foto-Química Inorgânica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (700, '095', 'a', 'foto-quimica', '1.06.02.05-4 Foto-Química Inorgânica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (701, '095', 'a', 'inorganica', '1.06.02.05-4 Foto-Química Inorgânica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (702, '095', 'a', '1.06.02.06-2', '1.06.02.06-2 Fisico Química Inorgânica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (703, '095', 'a', 'fisico', '1.06.02.06-2 Fisico Química Inorgânica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (704, '095', 'a', 'quimica', '1.06.02.06-2 Fisico Química Inorgânica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (705, '095', 'a', 'inorganica', '1.06.02.06-2 Fisico Química Inorgânica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (706, '095', 'a', '1.06.02.07-0', '1.06.02.07-0 Química Bio-Inorgânica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (707, '095', 'a', 'quimica', '1.06.02.07-0 Química Bio-Inorgânica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (708, '095', 'a', 'bio-inorganica', '1.06.02.07-0 Química Bio-Inorgânica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (709, '095', 'a', '1.06.03.00-0', '1.06.03.00-0 Fisico-Química', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (710, '095', 'a', 'fisico-quimica', '1.06.03.00-0 Fisico-Química', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (711, '095', 'a', '1.06.03.01-8', '1.06.03.01-8 Cinética Química e Catálise', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (712, '095', 'a', 'cinetica', '1.06.03.01-8 Cinética Química e Catálise', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (713, '095', 'a', 'quimica', '1.06.03.01-8 Cinética Química e Catálise', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (714, '095', 'a', 'catalise', '1.06.03.01-8 Cinética Química e Catálise', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (715, '095', 'a', '1.06.03.02-6', '1.06.03.02-6 Eletroquímica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (716, '095', 'a', 'eletroquimica', '1.06.03.02-6 Eletroquímica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (717, '095', 'a', '1.06.03.03-4', '1.06.03.03-4 Espectroscopia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (718, '095', 'a', 'espectroscopia', '1.06.03.03-4 Espectroscopia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (719, '095', 'a', '1.06.03.04-2', '1.06.03.04-2 Química de Interfaces', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (720, '095', 'a', 'quimica', '1.06.03.04-2 Química de Interfaces', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (721, '095', 'a', 'de', '1.06.03.04-2 Química de Interfaces', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (722, '095', 'a', 'interfaces', '1.06.03.04-2 Química de Interfaces', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (723, '095', 'a', '1.06.03.05-0', '1.06.03.05-0 Química do Estado Condensado', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (724, '095', 'a', 'quimica', '1.06.03.05-0 Química do Estado Condensado', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (725, '095', 'a', 'do', '1.06.03.05-0 Química do Estado Condensado', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (726, '095', 'a', 'estado', '1.06.03.05-0 Química do Estado Condensado', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (727, '095', 'a', 'condensado', '1.06.03.05-0 Química do Estado Condensado', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (728, '095', 'a', '1.06.03.06-9', '1.06.03.06-9 Química Nuclear e Radioquímica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (729, '095', 'a', 'quimica', '1.06.03.06-9 Química Nuclear e Radioquímica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (730, '095', 'a', 'nuclear', '1.06.03.06-9 Química Nuclear e Radioquímica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (731, '095', 'a', 'radioquimica', '1.06.03.06-9 Química Nuclear e Radioquímica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (732, '095', 'a', '1.06.03.07-7', '1.06.03.07-7 Química Teórica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (733, '095', 'a', 'quimica', '1.06.03.07-7 Química Teórica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (734, '095', 'a', 'teorica', '1.06.03.07-7 Química Teórica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (735, '095', 'a', '1.06.03.08-5', '1.06.03.08-5 Termodinâmica Química', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (736, '095', 'a', 'termodinamica', '1.06.03.08-5 Termodinâmica Química', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (737, '095', 'a', 'quimica', '1.06.03.08-5 Termodinâmica Química', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (738, '095', 'a', '1.06.04.00-6', '1.06.04.00-6 Química Analítica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (739, '095', 'a', 'quimica', '1.06.04.00-6 Química Analítica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (740, '095', 'a', 'analitica', '1.06.04.00-6 Química Analítica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (741, '095', 'a', '1.06.04.01-4', '1.06.04.01-4 Separação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (742, '095', 'a', 'separacao', '1.06.04.01-4 Separação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (743, '095', 'a', '1.06.04.02-2', '1.06.04.02-2 Métodos Óticos de Análise', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (744, '095', 'a', 'metodos', '1.06.04.02-2 Métodos Óticos de Análise', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (745, '095', 'a', 'oticos', '1.06.04.02-2 Métodos Óticos de Análise', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (746, '095', 'a', 'de', '1.06.04.02-2 Métodos Óticos de Análise', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (747, '095', 'a', 'analise', '1.06.04.02-2 Métodos Óticos de Análise', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (748, '095', 'a', '1.06.04.03-0', '1.06.04.03-0 Eletroanalítica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (749, '095', 'a', 'eletroanalitica', '1.06.04.03-0 Eletroanalítica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (750, '095', 'a', '1.06.04.04-9', '1.06.04.04-9 Gravimetria', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (751, '095', 'a', 'gravimetria', '1.06.04.04-9 Gravimetria', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (752, '095', 'a', '1.06.04.05-7', '1.06.04.05-7 Titimetria', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (753, '095', 'a', 'titimetria', '1.06.04.05-7 Titimetria', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (754, '095', 'a', '1.06.04.06-5', '1.06.04.06-5 Instrumentação Analítica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (755, '095', 'a', 'instrumentacao', '1.06.04.06-5 Instrumentação Analítica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (756, '095', 'a', 'analitica', '1.06.04.06-5 Instrumentação Analítica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (757, '095', 'a', '1.06.04.07-3', '1.06.04.07-3 Análise de Traços e Química Ambiental', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (758, '095', 'a', 'analise', '1.06.04.07-3 Análise de Traços e Química Ambiental', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (759, '095', 'a', 'de', '1.06.04.07-3 Análise de Traços e Química Ambiental', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (760, '095', 'a', 'tracos', '1.06.04.07-3 Análise de Traços e Química Ambiental', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (761, '095', 'a', 'quimica', '1.06.04.07-3 Análise de Traços e Química Ambiental', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (762, '095', 'a', 'ambiental', '1.06.04.07-3 Análise de Traços e Química Ambiental', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (763, '095', 'a', '1.07.00.00-5', '1.07.00.00-5 GeoCiências', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (764, '095', 'a', 'geociencias', '1.07.00.00-5 GeoCiências', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (765, '095', 'a', '1.07.01.00-1', '1.07.01.00-1 Geologia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (766, '095', 'a', 'geologia', '1.07.01.00-1 Geologia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (767, '095', 'a', '1.07.01.01-0', '1.07.01.01-0 Mineralogia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (768, '095', 'a', 'mineralogia', '1.07.01.01-0 Mineralogia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (769, '095', 'a', '1.07.01.02-8', '1.07.01.02-8 Petrologia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (770, '095', 'a', 'petrologia', '1.07.01.02-8 Petrologia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (771, '095', 'a', '1.07.01.03-6', '1.07.01.03-6 Geoquímica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (772, '095', 'a', 'geoquimica', '1.07.01.03-6 Geoquímica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (773, '095', 'a', '1.07.01.04-4', '1.07.01.04-4 Geologia Regional', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (774, '095', 'a', 'geologia', '1.07.01.04-4 Geologia Regional', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (775, '095', 'a', 'regional', '1.07.01.04-4 Geologia Regional', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (776, '095', 'a', '1.07.01.05-2', '1.07.01.05-2 Geotectônica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (777, '095', 'a', 'geotectonica', '1.07.01.05-2 Geotectônica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (778, '095', 'a', '1.07.01.06-0', '1.07.01.06-0 Geocronologia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (779, '095', 'a', 'geocronologia', '1.07.01.06-0 Geocronologia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (780, '095', 'a', '1.07.01.07-9', '1.07.01.07-9 Cartografia Geológica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (781, '095', 'a', 'cartografia', '1.07.01.07-9 Cartografia Geológica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (782, '095', 'a', 'geologica', '1.07.01.07-9 Cartografia Geológica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (783, '095', 'a', '1.07.01.08-7', '1.07.01.08-7 Metalogenia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (784, '095', 'a', 'metalogenia', '1.07.01.08-7 Metalogenia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (785, '095', 'a', '1.07.01.09-5', '1.07.01.09-5 Hidrogeologia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (786, '095', 'a', 'hidrogeologia', '1.07.01.09-5 Hidrogeologia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (787, '095', 'a', '1.07.01.10-9', '1.07.01.10-9 Prospecção Mineral', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (788, '095', 'a', 'prospeccao', '1.07.01.10-9 Prospecção Mineral', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (789, '095', 'a', 'mineral', '1.07.01.10-9 Prospecção Mineral', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (790, '095', 'a', '1.07.01.11-7', '1.07.01.11-7 Sedimentologia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (791, '095', 'a', 'sedimentologia', '1.07.01.11-7 Sedimentologia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (792, '095', 'a', '1.07.01.12-5', '1.07.01.12-5 Paleontologia Estratigráfica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (793, '095', 'a', 'paleontologia', '1.07.01.12-5 Paleontologia Estratigráfica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (794, '095', 'a', 'estratigrafica', '1.07.01.12-5 Paleontologia Estratigráfica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (795, '095', 'a', '1.07.01.13-3', '1.07.01.13-3 Estratigrafia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (796, '095', 'a', 'estratigrafia', '1.07.01.13-3 Estratigrafia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (797, '095', 'a', '1.07.01.14-1', '1.07.01.14-1 Geologia Ambiental', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (798, '095', 'a', 'geologia', '1.07.01.14-1 Geologia Ambiental', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (799, '095', 'a', 'ambiental', '1.07.01.14-1 Geologia Ambiental', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (800, '095', 'a', '1.07.02.00-8', '1.07.02.00-8 Geofísica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (801, '095', 'a', 'geofisica', '1.07.02.00-8 Geofísica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (802, '095', 'a', '1.07.02.01-6', '1.07.02.01-6 Geomagnetismo', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (803, '095', 'a', 'geomagnetismo', '1.07.02.01-6 Geomagnetismo', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (804, '095', 'a', '1.07.02.02-4', '1.07.02.02-4 Sismologia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (805, '095', 'a', 'sismologia', '1.07.02.02-4 Sismologia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (806, '095', 'a', '1.07.02.03-2', '1.07.02.03-2 Geotermia e Fluxo Térmico', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (807, '095', 'a', 'geotermia', '1.07.02.03-2 Geotermia e Fluxo Térmico', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (808, '095', 'a', 'fluxo', '1.07.02.03-2 Geotermia e Fluxo Térmico', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (809, '095', 'a', 'termico', '1.07.02.03-2 Geotermia e Fluxo Térmico', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (810, '095', 'a', '1.07.02.04-0', '1.07.02.04-0 Propriedades Físicas das Rochas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (811, '095', 'a', 'propriedades', '1.07.02.04-0 Propriedades Físicas das Rochas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (812, '095', 'a', 'fisicas', '1.07.02.04-0 Propriedades Físicas das Rochas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (813, '095', 'a', 'das', '1.07.02.04-0 Propriedades Físicas das Rochas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (814, '095', 'a', 'rochas', '1.07.02.04-0 Propriedades Físicas das Rochas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (815, '095', 'a', '1.07.02.05-9', '1.07.02.05-9 Geofísica Nuclear', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (816, '095', 'a', 'geofisica', '1.07.02.05-9 Geofísica Nuclear', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (817, '095', 'a', 'nuclear', '1.07.02.05-9 Geofísica Nuclear', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (818, '095', 'a', '1.07.02.06-7', '1.07.02.06-7 Sensoriamento Remoto', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (819, '095', 'a', 'sensoriamento', '1.07.02.06-7 Sensoriamento Remoto', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (820, '095', 'a', 'remoto', '1.07.02.06-7 Sensoriamento Remoto', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (821, '095', 'a', '1.07.02.07-5', '1.07.02.07-5 Aeronomia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (822, '095', 'a', 'aeronomia', '1.07.02.07-5 Aeronomia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (823, '095', 'a', '1.07.02.08-3', '1.07.02.08-3 Desenvolvimento de Instrumentação Geofísica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (824, '095', 'a', 'desenvolvimento', '1.07.02.08-3 Desenvolvimento de Instrumentação Geofísica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (825, '095', 'a', 'de', '1.07.02.08-3 Desenvolvimento de Instrumentação Geofísica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (826, '095', 'a', 'instrumentacao', '1.07.02.08-3 Desenvolvimento de Instrumentação Geofísica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (827, '095', 'a', 'geofisica', '1.07.02.08-3 Desenvolvimento de Instrumentação Geofísica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (828, '095', 'a', '1.07.02.09-1', '1.07.02.09-1 Geofísica Aplicada', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (829, '095', 'a', 'geofisica', '1.07.02.09-1 Geofísica Aplicada', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (830, '095', 'a', 'aplicada', '1.07.02.09-1 Geofísica Aplicada', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (831, '095', 'a', '1.07.02.10-5', '1.07.02.10-5 Gravimetria', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (832, '095', 'a', 'gravimetria', '1.07.02.10-5 Gravimetria', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (833, '095', 'a', '1.07.03.00-4', '1.07.03.00-4 Meteorologia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (834, '095', 'a', 'meteorologia', '1.07.03.00-4 Meteorologia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (835, '095', 'a', '1.07.03.01-2', '1.07.03.01-2 Meteorologia Dinâmica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (836, '095', 'a', 'meteorologia', '1.07.03.01-2 Meteorologia Dinâmica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (837, '095', 'a', 'dinamica', '1.07.03.01-2 Meteorologia Dinâmica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (838, '095', 'a', '1.07.03.02-0', '1.07.03.02-0 Meteorologia Sinótica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (839, '095', 'a', 'meteorologia', '1.07.03.02-0 Meteorologia Sinótica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (840, '095', 'a', 'sinotica', '1.07.03.02-0 Meteorologia Sinótica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (841, '095', 'a', '1.07.03.03-9', '1.07.03.03-9 Meteorologia Física', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (842, '095', 'a', 'meteorologia', '1.07.03.03-9 Meteorologia Física', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (843, '095', 'a', 'fisica', '1.07.03.03-9 Meteorologia Física', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (844, '095', 'a', '1.07.03.04-7', '1.07.03.04-7 Química da Atmosfera', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (845, '095', 'a', 'quimica', '1.07.03.04-7 Química da Atmosfera', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (846, '095', 'a', 'da', '1.07.03.04-7 Química da Atmosfera', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (847, '095', 'a', 'atmosfera', '1.07.03.04-7 Química da Atmosfera', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (848, '095', 'a', '1.07.03.05-5', '1.07.03.05-5 Instrumentação Meteorológica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (849, '095', 'a', 'instrumentacao', '1.07.03.05-5 Instrumentação Meteorológica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (850, '095', 'a', 'meteorologica', '1.07.03.05-5 Instrumentação Meteorológica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (851, '095', 'a', '1.07.03.06-3', '1.07.03.06-3 Climatologia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (852, '095', 'a', 'climatologia', '1.07.03.06-3 Climatologia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (853, '095', 'a', '1.07.03.07-1', '1.07.03.07-1 Micrometeorologia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (854, '095', 'a', 'micrometeorologia', '1.07.03.07-1 Micrometeorologia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (855, '095', 'a', '1.07.03.08-0', '1.07.03.08-0 Sensoriamento Remoto da Atmosfera', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (856, '095', 'a', 'sensoriamento', '1.07.03.08-0 Sensoriamento Remoto da Atmosfera', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (857, '095', 'a', 'remoto', '1.07.03.08-0 Sensoriamento Remoto da Atmosfera', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (858, '095', 'a', 'da', '1.07.03.08-0 Sensoriamento Remoto da Atmosfera', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (859, '095', 'a', 'atmosfera', '1.07.03.08-0 Sensoriamento Remoto da Atmosfera', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (860, '095', 'a', '1.07.03.09-8', '1.07.03.09-8 Meteorologia Aplicada', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (861, '095', 'a', 'meteorologia', '1.07.03.09-8 Meteorologia Aplicada', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (862, '095', 'a', 'aplicada', '1.07.03.09-8 Meteorologia Aplicada', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (863, '095', 'a', '1.07.04.00-0', '1.07.04.00-0 Geodesia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (864, '095', 'a', 'geodesia', '1.07.04.00-0 Geodesia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (865, '095', 'a', '1.07.04.01-9', '1.07.04.01-9 Geodesia Física', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (866, '095', 'a', 'geodesia', '1.07.04.01-9 Geodesia Física', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (867, '095', 'a', 'fisica', '1.07.04.01-9 Geodesia Física', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (868, '095', 'a', '1.07.04.02-7', '1.07.04.02-7 Geodesia Geométrica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (869, '095', 'a', 'geodesia', '1.07.04.02-7 Geodesia Geométrica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (870, '095', 'a', 'geometrica', '1.07.04.02-7 Geodesia Geométrica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (871, '095', 'a', '1.07.04.03-5', '1.07.04.03-5 Geodesia Celeste', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (872, '095', 'a', 'geodesia', '1.07.04.03-5 Geodesia Celeste', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (873, '095', 'a', 'celeste', '1.07.04.03-5 Geodesia Celeste', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (874, '095', 'a', '1.07.04.04-3', '1.07.04.04-3 Fotogrametria', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (875, '095', 'a', 'fotogrametria', '1.07.04.04-3 Fotogrametria', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (876, '095', 'a', '1.07.04.05-1', '1.07.04.05-1 Cartografia Básica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (877, '095', 'a', 'cartografia', '1.07.04.05-1 Cartografia Básica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (878, '095', 'a', 'basica', '1.07.04.05-1 Cartografia Básica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (879, '095', 'a', '1.07.05.00-7', '1.07.05.00-7 Geografia Física', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (880, '095', 'a', 'geografia', '1.07.05.00-7 Geografia Física', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (881, '095', 'a', 'fisica', '1.07.05.00-7 Geografia Física', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (882, '095', 'a', '1.07.05.01-5', '1.07.05.01-5 Geomorfologia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (883, '095', 'a', 'geomorfologia', '1.07.05.01-5 Geomorfologia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (884, '095', 'a', '1.07.05.02-3', '1.07.05.02-3 Climatologia Geográfica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (885, '095', 'a', 'climatologia', '1.07.05.02-3 Climatologia Geográfica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (886, '095', 'a', 'geografica', '1.07.05.02-3 Climatologia Geográfica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (887, '095', 'a', '1.07.05.03-1', '1.07.05.03-1 Pedologia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (888, '095', 'a', 'pedologia', '1.07.05.03-1 Pedologia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (889, '095', 'a', '1.07.05.04-0', '1.07.05.04-0 Hidrogeografia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (890, '095', 'a', 'hidrogeografia', '1.07.05.04-0 Hidrogeografia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (891, '095', 'a', '1.07.05.05-8', '1.07.05.05-8 Geoecologia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (892, '095', 'a', 'geoecologia', '1.07.05.05-8 Geoecologia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (893, '095', 'a', '1.07.05.06-6', '1.07.05.06-6 Fotogeografia (Físico-Ecológica)', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (894, '095', 'a', 'fotogeografia', '1.07.05.06-6 Fotogeografia (Físico-Ecológica)', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (895, '095', 'a', '(fisico-ecologica)', '1.07.05.06-6 Fotogeografia (Físico-Ecológica)', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (896, '095', 'a', '1.07.05.07-4', '1.07.05.07-4 Geocartografia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (897, '095', 'a', 'geocartografia', '1.07.05.07-4 Geocartografia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (898, '095', 'a', '1.08.00.00-0', '1.08.00.00-0 Oceanografia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (899, '095', 'a', 'oceanografia', '1.08.00.00-0 Oceanografia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (900, '095', 'a', '1.08.01.00-6', '1.08.01.00-6 Oceanografia Biológica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (901, '095', 'a', 'oceanografia', '1.08.01.00-6 Oceanografia Biológica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (902, '095', 'a', 'biologica', '1.08.01.00-6 Oceanografia Biológica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (903, '095', 'a', '1.08.01.01-4', '1.08.01.01-4 Interação entre os Organismos Marinhos e os Parâmetros Ambientais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (904, '095', 'a', 'interacao', '1.08.01.01-4 Interação entre os Organismos Marinhos e os Parâmetros Ambientais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (905, '095', 'a', 'entre', '1.08.01.01-4 Interação entre os Organismos Marinhos e os Parâmetros Ambientais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (906, '095', 'a', 'os', '1.08.01.01-4 Interação entre os Organismos Marinhos e os Parâmetros Ambientais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (907, '095', 'a', 'organismos', '1.08.01.01-4 Interação entre os Organismos Marinhos e os Parâmetros Ambientais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (908, '095', 'a', 'marinhos', '1.08.01.01-4 Interação entre os Organismos Marinhos e os Parâmetros Ambientais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (909, '095', 'a', 'os', '1.08.01.01-4 Interação entre os Organismos Marinhos e os Parâmetros Ambientais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (910, '095', 'a', 'parametros', '1.08.01.01-4 Interação entre os Organismos Marinhos e os Parâmetros Ambientais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (911, '095', 'a', 'ambientais', '1.08.01.01-4 Interação entre os Organismos Marinhos e os Parâmetros Ambientais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (912, '095', 'a', '1.08.02.00-2', '1.08.02.00-2 Oceanografia Física', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (913, '095', 'a', 'oceanografia', '1.08.02.00-2 Oceanografia Física', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (914, '095', 'a', 'fisica', '1.08.02.00-2 Oceanografia Física', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (915, '095', 'a', '1.08.02.01-0', '1.08.02.01-0 Variáveis Físicas da Água do Mar', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (916, '095', 'a', 'variaveis', '1.08.02.01-0 Variáveis Físicas da Água do Mar', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (917, '095', 'a', 'fisicas', '1.08.02.01-0 Variáveis Físicas da Água do Mar', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (918, '095', 'a', 'da', '1.08.02.01-0 Variáveis Físicas da Água do Mar', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (919, '095', 'a', 'agua', '1.08.02.01-0 Variáveis Físicas da Água do Mar', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (920, '095', 'a', 'do', '1.08.02.01-0 Variáveis Físicas da Água do Mar', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (921, '095', 'a', 'mar', '1.08.02.01-0 Variáveis Físicas da Água do Mar', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (922, '095', 'a', '1.08.02.02-9', '1.08.02.02-9 Movimento da Água do Mar', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (923, '095', 'a', 'movimento', '1.08.02.02-9 Movimento da Água do Mar', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (924, '095', 'a', 'da', '1.08.02.02-9 Movimento da Água do Mar', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (925, '095', 'a', 'agua', '1.08.02.02-9 Movimento da Água do Mar', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (926, '095', 'a', 'do', '1.08.02.02-9 Movimento da Água do Mar', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (927, '095', 'a', 'mar', '1.08.02.02-9 Movimento da Água do Mar', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (928, '095', 'a', '1.08.02.03-7', '1.08.02.03-7 Origem das Massas de Água', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (929, '095', 'a', 'origem', '1.08.02.03-7 Origem das Massas de Água', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (930, '095', 'a', 'das', '1.08.02.03-7 Origem das Massas de Água', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (931, '095', 'a', 'massas', '1.08.02.03-7 Origem das Massas de Água', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (932, '095', 'a', 'de', '1.08.02.03-7 Origem das Massas de Água', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (933, '095', 'a', 'agua', '1.08.02.03-7 Origem das Massas de Água', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (934, '095', 'a', '1.08.02.04-5', '1.08.02.04-5 Interação do Oceano com o Leito do Mar', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (935, '095', 'a', 'interacao', '1.08.02.04-5 Interação do Oceano com o Leito do Mar', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (936, '095', 'a', 'do', '1.08.02.04-5 Interação do Oceano com o Leito do Mar', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (937, '095', 'a', 'oceano', '1.08.02.04-5 Interação do Oceano com o Leito do Mar', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (938, '095', 'a', 'com', '1.08.02.04-5 Interação do Oceano com o Leito do Mar', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (939, '095', 'a', 'leito', '1.08.02.04-5 Interação do Oceano com o Leito do Mar', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (940, '095', 'a', 'do', '1.08.02.04-5 Interação do Oceano com o Leito do Mar', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (941, '095', 'a', 'mar', '1.08.02.04-5 Interação do Oceano com o Leito do Mar', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (942, '095', 'a', '1.08.02.05-3', '1.08.02.05-3 Interação do Oceano com a Atmosfera', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (943, '095', 'a', 'interacao', '1.08.02.05-3 Interação do Oceano com a Atmosfera', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (944, '095', 'a', 'do', '1.08.02.05-3 Interação do Oceano com a Atmosfera', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (945, '095', 'a', 'oceano', '1.08.02.05-3 Interação do Oceano com a Atmosfera', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (946, '095', 'a', 'com', '1.08.02.05-3 Interação do Oceano com a Atmosfera', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (947, '095', 'a', 'atmosfera', '1.08.02.05-3 Interação do Oceano com a Atmosfera', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (948, '095', 'a', '1.08.03.00-9', '1.08.03.00-9 Oceanografia Química', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (949, '095', 'a', 'oceanografia', '1.08.03.00-9 Oceanografia Química', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (950, '095', 'a', 'quimica', '1.08.03.00-9 Oceanografia Química', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (951, '095', 'a', '1.08.03.01-7', '1.08.03.01-7 Propriedades Químicas da Água do Mar', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (952, '095', 'a', 'propriedades', '1.08.03.01-7 Propriedades Químicas da Água do Mar', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (953, '095', 'a', 'quimicas', '1.08.03.01-7 Propriedades Químicas da Água do Mar', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (954, '095', 'a', 'da', '1.08.03.01-7 Propriedades Químicas da Água do Mar', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (955, '095', 'a', 'agua', '1.08.03.01-7 Propriedades Químicas da Água do Mar', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (956, '095', 'a', 'do', '1.08.03.01-7 Propriedades Químicas da Água do Mar', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (957, '095', 'a', 'mar', '1.08.03.01-7 Propriedades Químicas da Água do Mar', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (958, '095', 'a', '1.08.03.02-5', '1.08.03.02-5 Interações Químico-Biológicas/Geológicas das Substâncias Químicas da Água do Mar', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (959, '095', 'a', 'interacoes', '1.08.03.02-5 Interações Químico-Biológicas/Geológicas das Substâncias Químicas da Água do Mar', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (960, '095', 'a', 'quimico-biologicas/geologicas', '1.08.03.02-5 Interações Químico-Biológicas/Geológicas das Substâncias Químicas da Água do Mar', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (961, '095', 'a', 'das', '1.08.03.02-5 Interações Químico-Biológicas/Geológicas das Substâncias Químicas da Água do Mar', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (962, '095', 'a', 'substancias', '1.08.03.02-5 Interações Químico-Biológicas/Geológicas das Substâncias Químicas da Água do Mar', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (963, '095', 'a', 'quimicas', '1.08.03.02-5 Interações Químico-Biológicas/Geológicas das Substâncias Químicas da Água do Mar', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (964, '095', 'a', 'da', '1.08.03.02-5 Interações Químico-Biológicas/Geológicas das Substâncias Químicas da Água do Mar', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (965, '095', 'a', 'agua', '1.08.03.02-5 Interações Químico-Biológicas/Geológicas das Substâncias Químicas da Água do Mar', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (966, '095', 'a', 'do', '1.08.03.02-5 Interações Químico-Biológicas/Geológicas das Substâncias Químicas da Água do Mar', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (967, '095', 'a', 'mar', '1.08.03.02-5 Interações Químico-Biológicas/Geológicas das Substâncias Químicas da Água do Mar', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (968, '095', 'a', '1.08.04.00-5', '1.08.04.00-5 Oceanografia Geológica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (969, '095', 'a', 'oceanografia', '1.08.04.00-5 Oceanografia Geológica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (970, '095', 'a', 'geologica', '1.08.04.00-5 Oceanografia Geológica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (971, '095', 'a', '1.08.04.01-3', '1.08.04.01-3 Geomorfologia Submarina', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (972, '095', 'a', 'geomorfologia', '1.08.04.01-3 Geomorfologia Submarina', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (973, '095', 'a', 'submarina', '1.08.04.01-3 Geomorfologia Submarina', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (974, '095', 'a', '1.08.04.02-1', '1.08.04.02-1 Sedimentologia Marinha', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (975, '095', 'a', 'sedimentologia', '1.08.04.02-1 Sedimentologia Marinha', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (976, '095', 'a', 'marinha', '1.08.04.02-1 Sedimentologia Marinha', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (977, '095', 'a', '1.08.04.03-0', '1.08.04.03-0 Geofísica Marinha', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (978, '095', 'a', 'geofisica', '1.08.04.03-0 Geofísica Marinha', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (979, '095', 'a', 'marinha', '1.08.04.03-0 Geofísica Marinha', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (980, '095', 'a', '2.00.00.00-6', '2.00.00.00-6 Ciências Biológicas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (981, '095', 'a', 'ciencias', '2.00.00.00-6 Ciências Biológicas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (982, '095', 'a', 'biologicas', '2.00.00.00-6 Ciências Biológicas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (983, '095', 'a', '2.01.00.00-0', '2.01.00.00-0 Biologia Geral', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (984, '095', 'a', 'biologia', '2.01.00.00-0 Biologia Geral', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (985, '095', 'a', 'geral', '2.01.00.00-0 Biologia Geral', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (986, '095', 'a', '2.02.00.00-5', '2.02.00.00-5 Genética', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (987, '095', 'a', 'genetica', '2.02.00.00-5 Genética', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (988, '095', 'a', '2.02.01.00-1', '2.02.01.00-1 Genética Quantitativa', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (989, '095', 'a', 'genetica', '2.02.01.00-1 Genética Quantitativa', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (990, '095', 'a', 'quantitativa', '2.02.01.00-1 Genética Quantitativa', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (991, '095', 'a', '2.02.02.00-8', '2.02.02.00-8 Genética Molecular e de Microorganismos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (992, '095', 'a', 'genetica', '2.02.02.00-8 Genética Molecular e de Microorganismos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (993, '095', 'a', 'molecular', '2.02.02.00-8 Genética Molecular e de Microorganismos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (994, '095', 'a', 'de', '2.02.02.00-8 Genética Molecular e de Microorganismos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (995, '095', 'a', 'microorganismos', '2.02.02.00-8 Genética Molecular e de Microorganismos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (996, '095', 'a', '2.02.03.00-4', '2.02.03.00-4 Genética Vegetal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (997, '095', 'a', 'genetica', '2.02.03.00-4 Genética Vegetal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (998, '095', 'a', 'vegetal', '2.02.03.00-4 Genética Vegetal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (999, '095', 'a', '2.02.04.00-0', '2.02.04.00-0 Genética Animal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1000, '095', 'a', 'genetica', '2.02.04.00-0 Genética Animal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1001, '095', 'a', 'animal', '2.02.04.00-0 Genética Animal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1002, '095', 'a', '2.02.05.00-7', '2.02.05.00-7 Genética Humana e Médica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1003, '095', 'a', 'genetica', '2.02.05.00-7 Genética Humana e Médica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1004, '095', 'a', 'humana', '2.02.05.00-7 Genética Humana e Médica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1005, '095', 'a', 'medica', '2.02.05.00-7 Genética Humana e Médica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1006, '095', 'a', '2.02.06.00-3', '2.02.06.00-3 Mutagênese', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1007, '095', 'a', 'mutagenese', '2.02.06.00-3 Mutagênese', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1008, '095', 'a', '2.03.00.00-0', '2.03.00.00-0 Botânica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1009, '095', 'a', 'botanica', '2.03.00.00-0 Botânica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1010, '095', 'a', '2.03.01.00-6', '2.03.01.00-6 Paleobotânica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1011, '095', 'a', 'paleobotanica', '2.03.01.00-6 Paleobotânica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1012, '095', 'a', '2.03.02.00-2', '2.03.02.00-2 Morfologia Vegetal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1013, '095', 'a', 'morfologia', '2.03.02.00-2 Morfologia Vegetal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1014, '095', 'a', 'vegetal', '2.03.02.00-2 Morfologia Vegetal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1015, '095', 'a', '2.03.02.01-0', '2.03.02.01-0 Morfologia Externa', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1016, '095', 'a', 'morfologia', '2.03.02.01-0 Morfologia Externa', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1017, '095', 'a', 'externa', '2.03.02.01-0 Morfologia Externa', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1018, '095', 'a', '2.03.02.02-9', '2.03.02.02-9 Citologia Vegetal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1019, '095', 'a', 'citologia', '2.03.02.02-9 Citologia Vegetal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1020, '095', 'a', 'vegetal', '2.03.02.02-9 Citologia Vegetal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1021, '095', 'a', '2.03.02.03-7', '2.03.02.03-7 Anatomia Vegetal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1022, '095', 'a', 'anatomia', '2.03.02.03-7 Anatomia Vegetal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1023, '095', 'a', 'vegetal', '2.03.02.03-7 Anatomia Vegetal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1024, '095', 'a', '2.03.02.04-5', '2.03.02.04-5 Palinologia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1025, '095', 'a', 'palinologia', '2.03.02.04-5 Palinologia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1026, '095', 'a', '2.03.03.00-9', '2.03.03.00-9 Fisiologia Vegetal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1027, '095', 'a', 'fisiologia', '2.03.03.00-9 Fisiologia Vegetal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1028, '095', 'a', 'vegetal', '2.03.03.00-9 Fisiologia Vegetal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1029, '095', 'a', '2.03.03.01-7', '2.03.03.01-7 Nutrição e Crescimento Vegetal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1030, '095', 'a', 'nutricao', '2.03.03.01-7 Nutrição e Crescimento Vegetal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1031, '095', 'a', 'crescimento', '2.03.03.01-7 Nutrição e Crescimento Vegetal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1032, '095', 'a', 'vegetal', '2.03.03.01-7 Nutrição e Crescimento Vegetal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1033, '095', 'a', '2.03.03.02-5', '2.03.03.02-5 Reprodução Vegetal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1034, '095', 'a', 'reproducao', '2.03.03.02-5 Reprodução Vegetal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1035, '095', 'a', 'vegetal', '2.03.03.02-5 Reprodução Vegetal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1036, '095', 'a', '2.03.03.03-3', '2.03.03.03-3 Ecofisiologia Vegetal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1037, '095', 'a', 'ecofisiologia', '2.03.03.03-3 Ecofisiologia Vegetal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1038, '095', 'a', 'vegetal', '2.03.03.03-3 Ecofisiologia Vegetal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1039, '095', 'a', '2.03.04.00-5', '2.03.04.00-5 Taxonomia Vegetal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1040, '095', 'a', 'taxonomia', '2.03.04.00-5 Taxonomia Vegetal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1041, '095', 'a', 'vegetal', '2.03.04.00-5 Taxonomia Vegetal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1042, '095', 'a', '2.03.04.01-3', '2.03.04.01-3 Taxonomia de Criptógamos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1043, '095', 'a', 'taxonomia', '2.03.04.01-3 Taxonomia de Criptógamos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1044, '095', 'a', 'de', '2.03.04.01-3 Taxonomia de Criptógamos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1045, '095', 'a', 'criptogamos', '2.03.04.01-3 Taxonomia de Criptógamos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1046, '095', 'a', '2.03.04.02-1', '2.03.04.02-1 Taxonomia de Fanerógamos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1047, '095', 'a', 'taxonomia', '2.03.04.02-1 Taxonomia de Fanerógamos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1048, '095', 'a', 'de', '2.03.04.02-1 Taxonomia de Fanerógamos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1049, '095', 'a', 'fanerogamos', '2.03.04.02-1 Taxonomia de Fanerógamos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1050, '095', 'a', '2.03.05.00-1', '2.03.05.00-1 Fitogeografia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1051, '095', 'a', 'fitogeografia', '2.03.05.00-1 Fitogeografia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1052, '095', 'a', '2.03.06.00-8', '2.03.06.00-8 Botânica Aplicada', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1053, '095', 'a', 'botanica', '2.03.06.00-8 Botânica Aplicada', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1054, '095', 'a', 'aplicada', '2.03.06.00-8 Botânica Aplicada', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1055, '095', 'a', '2.04.00.00-4', '2.04.00.00-4 Zoologia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1056, '095', 'a', 'zoologia', '2.04.00.00-4 Zoologia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1057, '095', 'a', '2.04.01.00-0', '2.04.01.00-0 Paleozoologia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1058, '095', 'a', 'paleozoologia', '2.04.01.00-0 Paleozoologia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1059, '095', 'a', '2.04.02.00-7', '2.04.02.00-7 Morfologia dos Grupos Recentes', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1060, '095', 'a', 'morfologia', '2.04.02.00-7 Morfologia dos Grupos Recentes', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1061, '095', 'a', 'dos', '2.04.02.00-7 Morfologia dos Grupos Recentes', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1062, '095', 'a', 'grupos', '2.04.02.00-7 Morfologia dos Grupos Recentes', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1063, '095', 'a', 'recentes', '2.04.02.00-7 Morfologia dos Grupos Recentes', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1064, '095', 'a', '2.04.03.00-3', '2.04.03.00-3 Fisiologia dos Grupos Recentes', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1065, '095', 'a', 'fisiologia', '2.04.03.00-3 Fisiologia dos Grupos Recentes', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1066, '095', 'a', 'dos', '2.04.03.00-3 Fisiologia dos Grupos Recentes', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1067, '095', 'a', 'grupos', '2.04.03.00-3 Fisiologia dos Grupos Recentes', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1068, '095', 'a', 'recentes', '2.04.03.00-3 Fisiologia dos Grupos Recentes', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1069, '095', 'a', '2.04.04.00-0', '2.04.04.00-0 Comportamento Animal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1070, '095', 'a', 'comportamento', '2.04.04.00-0 Comportamento Animal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1071, '095', 'a', 'animal', '2.04.04.00-0 Comportamento Animal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1072, '095', 'a', '2.04.05.00-6', '2.04.05.00-6 Taxonomia dos Grupos Recentes', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1073, '095', 'a', 'taxonomia', '2.04.05.00-6 Taxonomia dos Grupos Recentes', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1074, '095', 'a', 'dos', '2.04.05.00-6 Taxonomia dos Grupos Recentes', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1075, '095', 'a', 'grupos', '2.04.05.00-6 Taxonomia dos Grupos Recentes', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1076, '095', 'a', 'recentes', '2.04.05.00-6 Taxonomia dos Grupos Recentes', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1077, '095', 'a', '2.04.06.00-2', '2.04.06.00-2 Zoologia Aplicada', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1078, '095', 'a', 'zoologia', '2.04.06.00-2 Zoologia Aplicada', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1079, '095', 'a', 'aplicada', '2.04.06.00-2 Zoologia Aplicada', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1080, '095', 'a', '2.04.06.01-0', '2.04.06.01-0 Conservação das Espécies Animais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1081, '095', 'a', 'conservacao', '2.04.06.01-0 Conservação das Espécies Animais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1082, '095', 'a', 'das', '2.04.06.01-0 Conservação das Espécies Animais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1083, '095', 'a', 'especies', '2.04.06.01-0 Conservação das Espécies Animais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1084, '095', 'a', 'animais', '2.04.06.01-0 Conservação das Espécies Animais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1085, '095', 'a', '2.04.06.02-9', '2.04.06.02-9 Utilização dos Animais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1086, '095', 'a', 'utilizacao', '2.04.06.02-9 Utilização dos Animais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1087, '095', 'a', 'dos', '2.04.06.02-9 Utilização dos Animais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1088, '095', 'a', 'animais', '2.04.06.02-9 Utilização dos Animais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1089, '095', 'a', '2.04.06.03-7', '2.04.06.03-7 Controle Populacional de Animais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1090, '095', 'a', 'controle', '2.04.06.03-7 Controle Populacional de Animais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1091, '095', 'a', 'populacional', '2.04.06.03-7 Controle Populacional de Animais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1092, '095', 'a', 'de', '2.04.06.03-7 Controle Populacional de Animais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1093, '095', 'a', 'animais', '2.04.06.03-7 Controle Populacional de Animais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1094, '095', 'a', '2.05.00.00-9', '2.05.00.00-9 Ecologia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1095, '095', 'a', 'ecologia', '2.05.00.00-9 Ecologia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1096, '095', 'a', '2.05.01.00-5', '2.05.01.00-5 Ecologia Teórica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1097, '095', 'a', 'ecologia', '2.05.01.00-5 Ecologia Teórica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1098, '095', 'a', 'teorica', '2.05.01.00-5 Ecologia Teórica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1099, '095', 'a', '2.05.02.00-1', '2.05.02.00-1 Ecologia de Ecossistemas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1100, '095', 'a', 'ecologia', '2.05.02.00-1 Ecologia de Ecossistemas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1101, '095', 'a', 'de', '2.05.02.00-1 Ecologia de Ecossistemas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1102, '095', 'a', 'ecossistemas', '2.05.02.00-1 Ecologia de Ecossistemas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1103, '095', 'a', '2.05.03.00-8', '2.05.03.00-8 Ecologia Aplicada', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1104, '095', 'a', 'ecologia', '2.05.03.00-8 Ecologia Aplicada', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1105, '095', 'a', 'aplicada', '2.05.03.00-8 Ecologia Aplicada', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1106, '095', 'a', '2.06.00.00-3', '2.06.00.00-3 Morfologia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1107, '095', 'a', 'morfologia', '2.06.00.00-3 Morfologia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1108, '095', 'a', '2.06.01.00-0', '2.06.01.00-0 Citologia e Biologia Celular', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1109, '095', 'a', 'citologia', '2.06.01.00-0 Citologia e Biologia Celular', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1110, '095', 'a', 'biologia', '2.06.01.00-0 Citologia e Biologia Celular', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1111, '095', 'a', 'celular', '2.06.01.00-0 Citologia e Biologia Celular', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1112, '095', 'a', '2.06.02.00-6', '2.06.02.00-6 Embriologia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1113, '095', 'a', 'embriologia', '2.06.02.00-6 Embriologia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1114, '095', 'a', '2.06.03.00-2', '2.06.03.00-2 Histologia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1115, '095', 'a', 'histologia', '2.06.03.00-2 Histologia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1116, '095', 'a', '2.06.04.00-9', '2.06.04.00-9 Anatomia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1117, '095', 'a', 'anatomia', '2.06.04.00-9 Anatomia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1118, '095', 'a', '2.06.04.01-7', '2.06.04.01-7 Anatomia Humana', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1119, '095', 'a', 'anatomia', '2.06.04.01-7 Anatomia Humana', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1120, '095', 'a', 'humana', '2.06.04.01-7 Anatomia Humana', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1121, '095', 'a', '2.07.00.00-8', '2.07.00.00-8 Fisiologia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1122, '095', 'a', 'fisiologia', '2.07.00.00-8 Fisiologia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1123, '095', 'a', '2.07.01.00-4', '2.07.01.00-4 Fisiologia Geral', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1124, '095', 'a', 'fisiologia', '2.07.01.00-4 Fisiologia Geral', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1125, '095', 'a', 'geral', '2.07.01.00-4 Fisiologia Geral', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1126, '095', 'a', '2.07.02.00-0', '2.07.02.00-0 Fisiologia de Órgaos e Sistemas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1127, '095', 'a', 'fisiologia', '2.07.02.00-0 Fisiologia de Órgaos e Sistemas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1128, '095', 'a', 'de', '2.07.02.00-0 Fisiologia de Órgaos e Sistemas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1129, '095', 'a', 'orgaos', '2.07.02.00-0 Fisiologia de Órgaos e Sistemas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1130, '095', 'a', 'sistemas', '2.07.02.00-0 Fisiologia de Órgaos e Sistemas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1131, '095', 'a', '2.07.02.01-9', '2.07.02.01-9 Neurofisiologia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1132, '095', 'a', 'neurofisiologia', '2.07.02.01-9 Neurofisiologia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1133, '095', 'a', '2.07.02.02-7', '2.07.02.02-7 Fisiologia Cardiovascular', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1134, '095', 'a', 'fisiologia', '2.07.02.02-7 Fisiologia Cardiovascular', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1135, '095', 'a', 'cardiovascular', '2.07.02.02-7 Fisiologia Cardiovascular', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1136, '095', 'a', '2.07.02.03-5', '2.07.02.03-5 Fisiologia da Respiração', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1137, '095', 'a', 'fisiologia', '2.07.02.03-5 Fisiologia da Respiração', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1138, '095', 'a', 'da', '2.07.02.03-5 Fisiologia da Respiração', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1139, '095', 'a', 'respiracao', '2.07.02.03-5 Fisiologia da Respiração', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1140, '095', 'a', '2.07.02.04-3', '2.07.02.04-3 Fisiologia Renal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1141, '095', 'a', 'fisiologia', '2.07.02.04-3 Fisiologia Renal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1142, '095', 'a', 'renal', '2.07.02.04-3 Fisiologia Renal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1143, '095', 'a', '2.07.02.05-1', '2.07.02.05-1 Fisiologia Endocrina', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1144, '095', 'a', 'fisiologia', '2.07.02.05-1 Fisiologia Endocrina', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1145, '095', 'a', 'endocrina', '2.07.02.05-1 Fisiologia Endocrina', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1146, '095', 'a', '2.07.02.06-0', '2.07.02.06-0 Fisiologia da Digestão', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1147, '095', 'a', 'fisiologia', '2.07.02.06-0 Fisiologia da Digestão', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1148, '095', 'a', 'da', '2.07.02.06-0 Fisiologia da Digestão', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1149, '095', 'a', 'digestao', '2.07.02.06-0 Fisiologia da Digestão', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1150, '095', 'a', '2.07.02.07-8', '2.07.02.07-8 Cinesiologia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1151, '095', 'a', 'cinesiologia', '2.07.02.07-8 Cinesiologia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1152, '095', 'a', '2.07.03.00-7', '2.07.03.00-7 Fisiologia do Esforço', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1153, '095', 'a', 'fisiologia', '2.07.03.00-7 Fisiologia do Esforço', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1154, '095', 'a', 'do', '2.07.03.00-7 Fisiologia do Esforço', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1155, '095', 'a', 'esforco', '2.07.03.00-7 Fisiologia do Esforço', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1156, '095', 'a', '2.07.04.00-3', '2.07.04.00-3 Fisiologia Comparada', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1157, '095', 'a', 'fisiologia', '2.07.04.00-3 Fisiologia Comparada', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1158, '095', 'a', 'comparada', '2.07.04.00-3 Fisiologia Comparada', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1159, '095', 'a', '2.08.00.00-2', '2.08.00.00-2 Bioquímica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1160, '095', 'a', 'bioquimica', '2.08.00.00-2 Bioquímica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1161, '095', 'a', '2.08.01.00-9', '2.08.01.00-9 Química de Macromoléculas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1162, '095', 'a', 'quimica', '2.08.01.00-9 Química de Macromoléculas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1163, '095', 'a', 'de', '2.08.01.00-9 Química de Macromoléculas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1164, '095', 'a', 'macromoleculas', '2.08.01.00-9 Química de Macromoléculas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1165, '095', 'a', '2.08.01.01-7', '2.08.01.01-7 Proteínas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1166, '095', 'a', 'proteinas', '2.08.01.01-7 Proteínas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1167, '095', 'a', '2.08.01.02-5', '2.08.01.02-5 Lipídeos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1168, '095', 'a', 'lipideos', '2.08.01.02-5 Lipídeos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1169, '095', 'a', '2.08.01.03-3', '2.08.01.03-3 Glicídeos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1170, '095', 'a', 'glicideos', '2.08.01.03-3 Glicídeos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1171, '095', 'a', '2.08.02.00-5', '2.08.02.00-5 Bioquímica dos Microorganismos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1172, '095', 'a', 'bioquimica', '2.08.02.00-5 Bioquímica dos Microorganismos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1173, '095', 'a', 'dos', '2.08.02.00-5 Bioquímica dos Microorganismos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1174, '095', 'a', 'microorganismos', '2.08.02.00-5 Bioquímica dos Microorganismos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1175, '095', 'a', '2.08.03.00-1', '2.08.03.00-1 Metabolismo e Bioenergética', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1176, '095', 'a', 'metabolismo', '2.08.03.00-1 Metabolismo e Bioenergética', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1177, '095', 'a', 'bioenergetica', '2.08.03.00-1 Metabolismo e Bioenergética', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1178, '095', 'a', '2.08.04.00-8', '2.08.04.00-8 Biologia Molecular', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1179, '095', 'a', 'biologia', '2.08.04.00-8 Biologia Molecular', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1180, '095', 'a', 'molecular', '2.08.04.00-8 Biologia Molecular', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1181, '095', 'a', '2.08.05.00-4', '2.08.05.00-4 Enzimologia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1182, '095', 'a', 'enzimologia', '2.08.05.00-4 Enzimologia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1183, '095', 'a', '2.09.00.00-7', '2.09.00.00-7 Biofísica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1184, '095', 'a', 'biofisica', '2.09.00.00-7 Biofísica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1185, '095', 'a', '2.09.01.00-3', '2.09.01.00-3 Biofísica Molecular', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1186, '095', 'a', 'biofisica', '2.09.01.00-3 Biofísica Molecular', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1187, '095', 'a', 'molecular', '2.09.01.00-3 Biofísica Molecular', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1188, '095', 'a', '2.09.02.00-0', '2.09.02.00-0 Biofísica Celular', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1189, '095', 'a', 'biofisica', '2.09.02.00-0 Biofísica Celular', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1190, '095', 'a', 'celular', '2.09.02.00-0 Biofísica Celular', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1191, '095', 'a', '2.09.03.00-6', '2.09.03.00-6 Biofísica de Processos e Sistemas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1192, '095', 'a', 'biofisica', '2.09.03.00-6 Biofísica de Processos e Sistemas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1193, '095', 'a', 'de', '2.09.03.00-6 Biofísica de Processos e Sistemas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1194, '095', 'a', 'processos', '2.09.03.00-6 Biofísica de Processos e Sistemas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1195, '095', 'a', 'sistemas', '2.09.03.00-6 Biofísica de Processos e Sistemas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1196, '095', 'a', '2.09.04.00-2', '2.09.04.00-2 Radiologia e Fotobiologia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1197, '095', 'a', 'radiologia', '2.09.04.00-2 Radiologia e Fotobiologia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1198, '095', 'a', 'fotobiologia', '2.09.04.00-2 Radiologia e Fotobiologia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1199, '095', 'a', '2.10.00.00-0', '2.10.00.00-0 Farmacologia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1200, '095', 'a', 'farmacologia', '2.10.00.00-0 Farmacologia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1201, '095', 'a', '2.10.01.00-6', '2.10.01.00-6 Farmacologia Geral', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1202, '095', 'a', 'farmacologia', '2.10.01.00-6 Farmacologia Geral', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1203, '095', 'a', 'geral', '2.10.01.00-6 Farmacologia Geral', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1204, '095', 'a', '2.10.01.01-4', '2.10.01.01-4 Farmacocinética', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1205, '095', 'a', 'farmacocinetica', '2.10.01.01-4 Farmacocinética', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1206, '095', 'a', '2.10.01.02-2', '2.10.01.02-2 Biodisponibilidade', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1207, '095', 'a', 'biodisponibilidade', '2.10.01.02-2 Biodisponibilidade', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1208, '095', 'a', '2.10.02.00-2', '2.10.02.00-2 Farmacologia Autonômica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1209, '095', 'a', 'farmacologia', '2.10.02.00-2 Farmacologia Autonômica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1210, '095', 'a', 'autonomica', '2.10.02.00-2 Farmacologia Autonômica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1211, '095', 'a', '2.10.03.00-9', '2.10.03.00-9 Neuropsicofarmacologia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1212, '095', 'a', 'neuropsicofarmacologia', '2.10.03.00-9 Neuropsicofarmacologia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1213, '095', 'a', '2.10.04.00-5', '2.10.04.00-5 Farmacologia Cardiorenal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1214, '095', 'a', 'farmacologia', '2.10.04.00-5 Farmacologia Cardiorenal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1215, '095', 'a', 'cardiorenal', '2.10.04.00-5 Farmacologia Cardiorenal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1216, '095', 'a', '2.10.05.00-1', '2.10.05.00-1 Farmacologia Bioquímica e Molecular', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1217, '095', 'a', 'farmacologia', '2.10.05.00-1 Farmacologia Bioquímica e Molecular', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1218, '095', 'a', 'bioquimica', '2.10.05.00-1 Farmacologia Bioquímica e Molecular', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1219, '095', 'a', 'molecular', '2.10.05.00-1 Farmacologia Bioquímica e Molecular', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1220, '095', 'a', '2.10.06.00-8', '2.10.06.00-8 Etnofarmacologia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1221, '095', 'a', 'etnofarmacologia', '2.10.06.00-8 Etnofarmacologia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1222, '095', 'a', '2.10.07.00-4', '2.10.07.00-4 Toxicologia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1223, '095', 'a', 'toxicologia', '2.10.07.00-4 Toxicologia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1224, '095', 'a', '2.10.08.00-0', '2.10.08.00-0 Farmacologia Clínica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1225, '095', 'a', 'farmacologia', '2.10.08.00-0 Farmacologia Clínica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1226, '095', 'a', 'clinica', '2.10.08.00-0 Farmacologia Clínica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1227, '095', 'a', '2.11.00.00-4', '2.11.00.00-4 Imunologia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1228, '095', 'a', 'imunologia', '2.11.00.00-4 Imunologia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1229, '095', 'a', '2.11.01.00-0', '2.11.01.00-0 Imunoquímica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1230, '095', 'a', 'imunoquimica', '2.11.01.00-0 Imunoquímica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1231, '095', 'a', '2.11.02.00-7', '2.11.02.00-7 Imunologia Celular', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1232, '095', 'a', 'imunologia', '2.11.02.00-7 Imunologia Celular', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1233, '095', 'a', 'celular', '2.11.02.00-7 Imunologia Celular', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1234, '095', 'a', '2.11.03.00-3', '2.11.03.00-3 Imunogenética', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1235, '095', 'a', 'imunogenetica', '2.11.03.00-3 Imunogenética', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1236, '095', 'a', '2.11.04.00-0', '2.11.04.00-0 Imunologia Aplicada', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1237, '095', 'a', 'imunologia', '2.11.04.00-0 Imunologia Aplicada', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1238, '095', 'a', 'aplicada', '2.11.04.00-0 Imunologia Aplicada', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1239, '095', 'a', '2.12.00.00-9', '2.12.00.00-9 Microbiologia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1240, '095', 'a', 'microbiologia', '2.12.00.00-9 Microbiologia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1241, '095', 'a', '2.12.01.00-5', '2.12.01.00-5 Biologia e Fisiologia dos Microorganismos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1242, '095', 'a', 'biologia', '2.12.01.00-5 Biologia e Fisiologia dos Microorganismos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1243, '095', 'a', 'fisiologia', '2.12.01.00-5 Biologia e Fisiologia dos Microorganismos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1244, '095', 'a', 'dos', '2.12.01.00-5 Biologia e Fisiologia dos Microorganismos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1245, '095', 'a', 'microorganismos', '2.12.01.00-5 Biologia e Fisiologia dos Microorganismos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1246, '095', 'a', '2.12.01.01-3', '2.12.01.01-3 Virologia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1247, '095', 'a', 'virologia', '2.12.01.01-3 Virologia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1248, '095', 'a', '2.12.01.02-1', '2.12.01.02-1 Bacterologia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1249, '095', 'a', 'bacterologia', '2.12.01.02-1 Bacterologia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1250, '095', 'a', '2.12.01.03-0', '2.12.01.03-0 Micologia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1251, '095', 'a', 'micologia', '2.12.01.03-0 Micologia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1252, '095', 'a', '2.12.02.00-1', '2.12.02.00-1 Microbiologia Aplicada', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1253, '095', 'a', 'microbiologia', '2.12.02.00-1 Microbiologia Aplicada', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1254, '095', 'a', 'aplicada', '2.12.02.00-1 Microbiologia Aplicada', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1255, '095', 'a', '2.12.02.01-0', '2.12.02.01-0 Microbiologia Médica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1256, '095', 'a', 'microbiologia', '2.12.02.01-0 Microbiologia Médica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1257, '095', 'a', 'medica', '2.12.02.01-0 Microbiologia Médica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1258, '095', 'a', '2.12.02.02-8', '2.12.02.02-8 Microbiologia Industrial e de Fermentação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1259, '095', 'a', 'microbiologia', '2.12.02.02-8 Microbiologia Industrial e de Fermentação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1260, '095', 'a', 'industrial', '2.12.02.02-8 Microbiologia Industrial e de Fermentação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1261, '095', 'a', 'de', '2.12.02.02-8 Microbiologia Industrial e de Fermentação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1262, '095', 'a', 'fermentacao', '2.12.02.02-8 Microbiologia Industrial e de Fermentação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1263, '095', 'a', '2.13.00.00-3', '2.13.00.00-3 Parasitologia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1264, '095', 'a', 'parasitologia', '2.13.00.00-3 Parasitologia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1265, '095', 'a', '2.13.01.00-0', '2.13.01.00-0 Protozoologia de Parasitos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1266, '095', 'a', 'protozoologia', '2.13.01.00-0 Protozoologia de Parasitos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1267, '095', 'a', 'de', '2.13.01.00-0 Protozoologia de Parasitos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1268, '095', 'a', 'parasitos', '2.13.01.00-0 Protozoologia de Parasitos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1269, '095', 'a', '2.13.01.01-8', '2.13.01.01-8 Protozoologia Parasitária Humana', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1270, '095', 'a', 'protozoologia', '2.13.01.01-8 Protozoologia Parasitária Humana', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1271, '095', 'a', 'parasitaria', '2.13.01.01-8 Protozoologia Parasitária Humana', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1272, '095', 'a', 'humana', '2.13.01.01-8 Protozoologia Parasitária Humana', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1273, '095', 'a', '2.13.01.02-6', '2.13.01.02-6 Protozoologia Parasitária Animal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1274, '095', 'a', 'protozoologia', '2.13.01.02-6 Protozoologia Parasitária Animal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1275, '095', 'a', 'parasitaria', '2.13.01.02-6 Protozoologia Parasitária Animal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1276, '095', 'a', 'animal', '2.13.01.02-6 Protozoologia Parasitária Animal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1277, '095', 'a', '2.13.02.00-6', '2.13.02.00-6 Helmintologia de Parasitos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1278, '095', 'a', 'helmintologia', '2.13.02.00-6 Helmintologia de Parasitos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1279, '095', 'a', 'de', '2.13.02.00-6 Helmintologia de Parasitos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1280, '095', 'a', 'parasitos', '2.13.02.00-6 Helmintologia de Parasitos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1281, '095', 'a', '2.13.02.01-4', '2.13.02.01-4 Helmintologia Humana', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1282, '095', 'a', 'helmintologia', '2.13.02.01-4 Helmintologia Humana', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1283, '095', 'a', 'humana', '2.13.02.01-4 Helmintologia Humana', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1284, '095', 'a', '2.13.02.02-2', '2.13.02.02-2 Helmintologia Animal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1285, '095', 'a', 'helmintologia', '2.13.02.02-2 Helmintologia Animal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1286, '095', 'a', 'animal', '2.13.02.02-2 Helmintologia Animal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1287, '095', 'a', '2.13.03.00-2', '2.13.03.00-2 Entomologia e Malacologia de Parasitos e Vetores', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1288, '095', 'a', 'entomologia', '2.13.03.00-2 Entomologia e Malacologia de Parasitos e Vetores', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1289, '095', 'a', 'malacologia', '2.13.03.00-2 Entomologia e Malacologia de Parasitos e Vetores', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1290, '095', 'a', 'de', '2.13.03.00-2 Entomologia e Malacologia de Parasitos e Vetores', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1291, '095', 'a', 'parasitos', '2.13.03.00-2 Entomologia e Malacologia de Parasitos e Vetores', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1292, '095', 'a', 'vetores', '2.13.03.00-2 Entomologia e Malacologia de Parasitos e Vetores', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1293, '095', 'a', '3.00.00.00-9', '3.00.00.00-9 Engenharias', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1294, '095', 'a', 'engenharias', '3.00.00.00-9 Engenharias', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1295, '095', 'a', '3.01.00.00-3', '3.01.00.00-3 Engenharia Civil', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1296, '095', 'a', 'engenharia', '3.01.00.00-3 Engenharia Civil', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1297, '095', 'a', 'civil', '3.01.00.00-3 Engenharia Civil', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1298, '095', 'a', '3.01.01.00-0', '3.01.01.00-0 Construção Civil', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1299, '095', 'a', 'construcao', '3.01.01.00-0 Construção Civil', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1300, '095', 'a', 'civil', '3.01.01.00-0 Construção Civil', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1301, '095', 'a', '3.01.01.01-8', '3.01.01.01-8 Materiais e Componentes de Construção', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1302, '095', 'a', 'materiais', '3.01.01.01-8 Materiais e Componentes de Construção', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1303, '095', 'a', 'componentes', '3.01.01.01-8 Materiais e Componentes de Construção', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1304, '095', 'a', 'de', '3.01.01.01-8 Materiais e Componentes de Construção', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1305, '095', 'a', 'construcao', '3.01.01.01-8 Materiais e Componentes de Construção', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1306, '095', 'a', '3.01.01.02-6', '3.01.01.02-6 Processos Construtivos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1307, '095', 'a', 'processos', '3.01.01.02-6 Processos Construtivos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1308, '095', 'a', 'construtivos', '3.01.01.02-6 Processos Construtivos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1309, '095', 'a', '3.01.01.03-4', '3.01.01.03-4 Instalações Prediais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1310, '095', 'a', 'instalacoes', '3.01.01.03-4 Instalações Prediais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1311, '095', 'a', 'prediais', '3.01.01.03-4 Instalações Prediais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1312, '095', 'a', '3.01.02.00-6', '3.01.02.00-6 Estruturas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1313, '095', 'a', 'estruturas', '3.01.02.00-6 Estruturas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1314, '095', 'a', '3.01.02.01-4', '3.01.02.01-4 Estruturas de Concreto', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1315, '095', 'a', 'estruturas', '3.01.02.01-4 Estruturas de Concreto', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1316, '095', 'a', 'de', '3.01.02.01-4 Estruturas de Concreto', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1317, '095', 'a', 'concreto', '3.01.02.01-4 Estruturas de Concreto', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1318, '095', 'a', '3.01.02.02-2', '3.01.02.02-2 Estruturas de Madeiras', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1319, '095', 'a', 'estruturas', '3.01.02.02-2 Estruturas de Madeiras', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1320, '095', 'a', 'de', '3.01.02.02-2 Estruturas de Madeiras', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1321, '095', 'a', 'madeiras', '3.01.02.02-2 Estruturas de Madeiras', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1322, '095', 'a', '3.01.02.03-0', '3.01.02.03-0 Estruturas Metálicas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1323, '095', 'a', 'estruturas', '3.01.02.03-0 Estruturas Metálicas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1324, '095', 'a', 'metalicas', '3.01.02.03-0 Estruturas Metálicas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1325, '095', 'a', '3.01.02.04-9', '3.01.02.04-9 Mecânica das Estruturas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1326, '095', 'a', 'mecanica', '3.01.02.04-9 Mecânica das Estruturas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1327, '095', 'a', 'das', '3.01.02.04-9 Mecânica das Estruturas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1328, '095', 'a', 'estruturas', '3.01.02.04-9 Mecânica das Estruturas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1329, '095', 'a', '3.01.03.00-2', '3.01.03.00-2 Geotécnica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1330, '095', 'a', 'geotecnica', '3.01.03.00-2 Geotécnica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1331, '095', 'a', '3.01.03.01-0', '3.01.03.01-0 Fundações e Escavações', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1332, '095', 'a', 'fundacoes', '3.01.03.01-0 Fundações e Escavações', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1333, '095', 'a', 'escavacoes', '3.01.03.01-0 Fundações e Escavações', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1334, '095', 'a', '3.01.03.02-9', '3.01.03.02-9 Mecânicas das Rochas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1335, '095', 'a', 'mecanicas', '3.01.03.02-9 Mecânicas das Rochas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1336, '095', 'a', 'das', '3.01.03.02-9 Mecânicas das Rochas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1337, '095', 'a', 'rochas', '3.01.03.02-9 Mecânicas das Rochas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1338, '095', 'a', '3.01.03.03-7', '3.01.03.03-7 Mecânicas dos Solos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1339, '095', 'a', 'mecanicas', '3.01.03.03-7 Mecânicas dos Solos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1340, '095', 'a', 'dos', '3.01.03.03-7 Mecânicas dos Solos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1341, '095', 'a', 'solos', '3.01.03.03-7 Mecânicas dos Solos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1342, '095', 'a', '3.01.03.04-5', '3.01.03.04-5 Obras de Terra e Enrocamento', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1343, '095', 'a', 'obras', '3.01.03.04-5 Obras de Terra e Enrocamento', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1344, '095', 'a', 'de', '3.01.03.04-5 Obras de Terra e Enrocamento', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1345, '095', 'a', 'terra', '3.01.03.04-5 Obras de Terra e Enrocamento', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1346, '095', 'a', 'enrocamento', '3.01.03.04-5 Obras de Terra e Enrocamento', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1347, '095', 'a', '3.01.03.05-3', '3.01.03.05-3 Pavimentos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1348, '095', 'a', 'pavimentos', '3.01.03.05-3 Pavimentos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1349, '095', 'a', '3.01.04.00-9', '3.01.04.00-9 Engenharia Hidráulica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1350, '095', 'a', 'engenharia', '3.01.04.00-9 Engenharia Hidráulica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1351, '095', 'a', 'hidraulica', '3.01.04.00-9 Engenharia Hidráulica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1352, '095', 'a', '3.01.04.01-7', '3.01.04.01-7 Hidráulica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1353, '095', 'a', 'hidraulica', '3.01.04.01-7 Hidráulica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1354, '095', 'a', '3.01.04.02-5', '3.01.04.02-5 Hidrologia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1355, '095', 'a', 'hidrologia', '3.01.04.02-5 Hidrologia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1356, '095', 'a', '3.01.05.00-5', '3.01.05.00-5 Infra-Estrutura de Transportes', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1357, '095', 'a', 'infra-estrutura', '3.01.05.00-5 Infra-Estrutura de Transportes', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1358, '095', 'a', 'de', '3.01.05.00-5 Infra-Estrutura de Transportes', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1359, '095', 'a', 'transportes', '3.01.05.00-5 Infra-Estrutura de Transportes', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1360, '095', 'a', '3.01.05.01-3', '3.01.05.01-3 Aeroportos; Projeto e Construção', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1361, '095', 'a', 'aeroportos;', '3.01.05.01-3 Aeroportos; Projeto e Construção', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1362, '095', 'a', 'projeto', '3.01.05.01-3 Aeroportos; Projeto e Construção', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1363, '095', 'a', 'construcao', '3.01.05.01-3 Aeroportos; Projeto e Construção', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1364, '095', 'a', '3.01.05.02-1', '3.01.05.02-1 Ferrovias; Projetos e Construção', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1365, '095', 'a', 'ferrovias;', '3.01.05.02-1 Ferrovias; Projetos e Construção', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1366, '095', 'a', 'projetos', '3.01.05.02-1 Ferrovias; Projetos e Construção', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1367, '095', 'a', 'construcao', '3.01.05.02-1 Ferrovias; Projetos e Construção', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1368, '095', 'a', '3.01.05.03-0', '3.01.05.03-0 Portos e Vias Nevegáveis; Projeto e Construção', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1369, '095', 'a', 'portos', '3.01.05.03-0 Portos e Vias Nevegáveis; Projeto e Construção', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1370, '095', 'a', 'vias', '3.01.05.03-0 Portos e Vias Nevegáveis; Projeto e Construção', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1371, '095', 'a', 'nevegaveis;', '3.01.05.03-0 Portos e Vias Nevegáveis; Projeto e Construção', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1372, '095', 'a', 'projeto', '3.01.05.03-0 Portos e Vias Nevegáveis; Projeto e Construção', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1373, '095', 'a', 'construcao', '3.01.05.03-0 Portos e Vias Nevegáveis; Projeto e Construção', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1374, '095', 'a', '3.01.05.04-8', '3.01.05.04-8 Rodovias; Projeto e Construção', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1375, '095', 'a', 'rodovias;', '3.01.05.04-8 Rodovias; Projeto e Construção', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1376, '095', 'a', 'projeto', '3.01.05.04-8 Rodovias; Projeto e Construção', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1377, '095', 'a', 'construcao', '3.01.05.04-8 Rodovias; Projeto e Construção', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1378, '095', 'a', '3.02.00.00-8', '3.02.00.00-8 Engenharia de Minas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1379, '095', 'a', 'engenharia', '3.02.00.00-8 Engenharia de Minas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1380, '095', 'a', 'de', '3.02.00.00-8 Engenharia de Minas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1381, '095', 'a', 'minas', '3.02.00.00-8 Engenharia de Minas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1382, '095', 'a', '3.02.01.00-4', '3.02.01.00-4 Pesquisa Mineral', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1383, '095', 'a', 'pesquisa', '3.02.01.00-4 Pesquisa Mineral', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1384, '095', 'a', 'mineral', '3.02.01.00-4 Pesquisa Mineral', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1385, '095', 'a', '3.02.01.01-2', '3.02.01.01-2 Caracterização do Minério', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1386, '095', 'a', 'caracterizacao', '3.02.01.01-2 Caracterização do Minério', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1387, '095', 'a', 'do', '3.02.01.01-2 Caracterização do Minério', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1388, '095', 'a', 'minerio', '3.02.01.01-2 Caracterização do Minério', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1389, '095', 'a', '3.02.01.02-0', '3.02.01.02-0 Dimensionamento de Jazidas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1390, '095', 'a', 'dimensionamento', '3.02.01.02-0 Dimensionamento de Jazidas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1391, '095', 'a', 'de', '3.02.01.02-0 Dimensionamento de Jazidas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1392, '095', 'a', 'jazidas', '3.02.01.02-0 Dimensionamento de Jazidas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1393, '095', 'a', '3.02.02.00-0', '3.02.02.00-0 Lavra', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1394, '095', 'a', 'lavra', '3.02.02.00-0 Lavra', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1395, '095', 'a', '3.02.02.01-9', '3.02.02.01-9 Lavra a Céu Aberto', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1396, '095', 'a', 'lavra', '3.02.02.01-9 Lavra a Céu Aberto', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1397, '095', 'a', 'ceu', '3.02.02.01-9 Lavra a Céu Aberto', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1398, '095', 'a', 'aberto', '3.02.02.01-9 Lavra a Céu Aberto', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1399, '095', 'a', '3.02.02.02-7', '3.02.02.02-7 Lavra de Mina Subterrânea', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1400, '095', 'a', 'lavra', '3.02.02.02-7 Lavra de Mina Subterrânea', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1401, '095', 'a', 'de', '3.02.02.02-7 Lavra de Mina Subterrânea', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1402, '095', 'a', 'mina', '3.02.02.02-7 Lavra de Mina Subterrânea', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1403, '095', 'a', 'subterranea', '3.02.02.02-7 Lavra de Mina Subterrânea', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1404, '095', 'a', '3.02.02.03-5', '3.02.02.03-5 Equipamentos de Lavra', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1405, '095', 'a', 'equipamentos', '3.02.02.03-5 Equipamentos de Lavra', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1406, '095', 'a', 'de', '3.02.02.03-5 Equipamentos de Lavra', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1407, '095', 'a', 'lavra', '3.02.02.03-5 Equipamentos de Lavra', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1408, '095', 'a', '3.02.03.00-7', '3.02.03.00-7 Tratamento de Minérios', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1409, '095', 'a', 'tratamento', '3.02.03.00-7 Tratamento de Minérios', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1410, '095', 'a', 'de', '3.02.03.00-7 Tratamento de Minérios', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1411, '095', 'a', 'minerios', '3.02.03.00-7 Tratamento de Minérios', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1412, '095', 'a', '3.02.03.01-5', '3.02.03.01-5 Métodos de Concentração e Enriquecimento de Minérios', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1413, '095', 'a', 'metodos', '3.02.03.01-5 Métodos de Concentração e Enriquecimento de Minérios', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1414, '095', 'a', 'de', '3.02.03.01-5 Métodos de Concentração e Enriquecimento de Minérios', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1415, '095', 'a', 'concentracao', '3.02.03.01-5 Métodos de Concentração e Enriquecimento de Minérios', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1416, '095', 'a', 'enriquecimento', '3.02.03.01-5 Métodos de Concentração e Enriquecimento de Minérios', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1417, '095', 'a', 'de', '3.02.03.01-5 Métodos de Concentração e Enriquecimento de Minérios', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1418, '095', 'a', 'minerios', '3.02.03.01-5 Métodos de Concentração e Enriquecimento de Minérios', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1419, '095', 'a', '3.02.03.02-3', '3.02.03.02-3 Equipamentos de Beneficiamento de Minérios', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1420, '095', 'a', 'equipamentos', '3.02.03.02-3 Equipamentos de Beneficiamento de Minérios', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1421, '095', 'a', 'de', '3.02.03.02-3 Equipamentos de Beneficiamento de Minérios', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1422, '095', 'a', 'beneficiamento', '3.02.03.02-3 Equipamentos de Beneficiamento de Minérios', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1423, '095', 'a', 'de', '3.02.03.02-3 Equipamentos de Beneficiamento de Minérios', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1424, '095', 'a', 'minerios', '3.02.03.02-3 Equipamentos de Beneficiamento de Minérios', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1425, '095', 'a', '3.03.00.00-2', '3.03.00.00-2 Engenharia de Materiais e Metalúrgica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1426, '095', 'a', 'engenharia', '3.03.00.00-2 Engenharia de Materiais e Metalúrgica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1427, '095', 'a', 'de', '3.03.00.00-2 Engenharia de Materiais e Metalúrgica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1428, '095', 'a', 'materiais', '3.03.00.00-2 Engenharia de Materiais e Metalúrgica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1429, '095', 'a', 'metalurgica', '3.03.00.00-2 Engenharia de Materiais e Metalúrgica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1430, '095', 'a', '3.03.01.00-9', '3.03.01.00-9 Instalações e Equipamentos Metalúrgicos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1431, '095', 'a', 'instalacoes', '3.03.01.00-9 Instalações e Equipamentos Metalúrgicos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1432, '095', 'a', 'equipamentos', '3.03.01.00-9 Instalações e Equipamentos Metalúrgicos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1433, '095', 'a', 'metalurgicos', '3.03.01.00-9 Instalações e Equipamentos Metalúrgicos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1434, '095', 'a', '3.03.01.01-7', '3.03.01.01-7 Instalações Metalúrgicas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1435, '095', 'a', 'instalacoes', '3.03.01.01-7 Instalações Metalúrgicas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1436, '095', 'a', 'metalurgicas', '3.03.01.01-7 Instalações Metalúrgicas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1437, '095', 'a', '3.03.01.02-5', '3.03.01.02-5 Equipamentos Metalúrgicos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1438, '095', 'a', 'equipamentos', '3.03.01.02-5 Equipamentos Metalúrgicos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1439, '095', 'a', 'metalurgicos', '3.03.01.02-5 Equipamentos Metalúrgicos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1440, '095', 'a', '3.03.02.00-5', '3.03.02.00-5 Metalurgia Extrativa', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1441, '095', 'a', 'metalurgia', '3.03.02.00-5 Metalurgia Extrativa', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1442, '095', 'a', 'extrativa', '3.03.02.00-5 Metalurgia Extrativa', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1443, '095', 'a', '3.03.02.01-3', '3.03.02.01-3 Aglomeração', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1444, '095', 'a', 'aglomeracao', '3.03.02.01-3 Aglomeração', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1445, '095', 'a', '3.03.02.02-1', '3.03.02.02-1 Eletrometalurgia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1446, '095', 'a', 'eletrometalurgia', '3.03.02.02-1 Eletrometalurgia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1447, '095', 'a', '3.03.02.03-0', '3.03.02.03-0 Hidrometalurgia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1448, '095', 'a', 'hidrometalurgia', '3.03.02.03-0 Hidrometalurgia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1449, '095', 'a', '3.03.02.04-8', '3.03.02.04-8 Pirometalurgia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1450, '095', 'a', 'pirometalurgia', '3.03.02.04-8 Pirometalurgia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1451, '095', 'a', '3.03.02.05-6', '3.03.02.05-6 Tratamento de Minérios', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1452, '095', 'a', 'tratamento', '3.03.02.05-6 Tratamento de Minérios', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1453, '095', 'a', 'de', '3.03.02.05-6 Tratamento de Minérios', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1454, '095', 'a', 'minerios', '3.03.02.05-6 Tratamento de Minérios', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1455, '095', 'a', '3.03.03.00-1', '3.03.03.00-1 Metalurgia de Transformação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1456, '095', 'a', 'metalurgia', '3.03.03.00-1 Metalurgia de Transformação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1457, '095', 'a', 'de', '3.03.03.00-1 Metalurgia de Transformação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1458, '095', 'a', 'transformacao', '3.03.03.00-1 Metalurgia de Transformação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1459, '095', 'a', '3.03.03.01-0', '3.03.03.01-0 Conformação Mecânica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1460, '095', 'a', 'conformacao', '3.03.03.01-0 Conformação Mecânica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1461, '095', 'a', 'mecanica', '3.03.03.01-0 Conformação Mecânica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1462, '095', 'a', '3.03.03.02-8', '3.03.03.02-8 Fundição', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1463, '095', 'a', 'fundicao', '3.03.03.02-8 Fundição', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1464, '095', 'a', '3.03.03.03-6', '3.03.03.03-6 Metalurgia de Po', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1465, '095', 'a', 'metalurgia', '3.03.03.03-6 Metalurgia de Po', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1466, '095', 'a', 'de', '3.03.03.03-6 Metalurgia de Po', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1467, '095', 'a', 'po', '3.03.03.03-6 Metalurgia de Po', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1468, '095', 'a', '3.03.03.04-4', '3.03.03.04-4 Recobrimentos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1469, '095', 'a', 'recobrimentos', '3.03.03.04-4 Recobrimentos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1470, '095', 'a', '3.03.03.05-2', '3.03.03.05-2 Soldagem', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1471, '095', 'a', 'soldagem', '3.03.03.05-2 Soldagem', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1472, '095', 'a', '3.03.03.06-0', '3.03.03.06-0 Tratamento Térmicos, Mecânicos e Químicos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1473, '095', 'a', 'tratamento', '3.03.03.06-0 Tratamento Térmicos, Mecânicos e Químicos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1474, '095', 'a', 'termicos,', '3.03.03.06-0 Tratamento Térmicos, Mecânicos e Químicos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1475, '095', 'a', 'mecanicos', '3.03.03.06-0 Tratamento Térmicos, Mecânicos e Químicos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1476, '095', 'a', 'quimicos', '3.03.03.06-0 Tratamento Térmicos, Mecânicos e Químicos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1477, '095', 'a', '3.03.03.07-9', '3.03.03.07-9 Usinagem', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1478, '095', 'a', 'usinagem', '3.03.03.07-9 Usinagem', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1479, '095', 'a', '3.03.04.00-8', '3.03.04.00-8 Metalurgia Fisica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1480, '095', 'a', 'metalurgia', '3.03.04.00-8 Metalurgia Fisica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1481, '095', 'a', 'fisica', '3.03.04.00-8 Metalurgia Fisica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1482, '095', 'a', '3.03.04.01-6', '3.03.04.01-6 Estrutura dos Metais e Ligas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1483, '095', 'a', 'estrutura', '3.03.04.01-6 Estrutura dos Metais e Ligas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1484, '095', 'a', 'dos', '3.03.04.01-6 Estrutura dos Metais e Ligas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1485, '095', 'a', 'metais', '3.03.04.01-6 Estrutura dos Metais e Ligas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1486, '095', 'a', 'ligas', '3.03.04.01-6 Estrutura dos Metais e Ligas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1487, '095', 'a', '3.03.04.02-4', '3.03.04.02-4 Propriedades Físicas dos Metais e Ligas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1488, '095', 'a', 'propriedades', '3.03.04.02-4 Propriedades Físicas dos Metais e Ligas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1489, '095', 'a', 'fisicas', '3.03.04.02-4 Propriedades Físicas dos Metais e Ligas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1490, '095', 'a', 'dos', '3.03.04.02-4 Propriedades Físicas dos Metais e Ligas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1491, '095', 'a', 'metais', '3.03.04.02-4 Propriedades Físicas dos Metais e Ligas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1492, '095', 'a', 'ligas', '3.03.04.02-4 Propriedades Físicas dos Metais e Ligas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1493, '095', 'a', '3.03.04.03-2', '3.03.04.03-2 Propriedades Mecânicas dos Metais e Ligas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1494, '095', 'a', 'propriedades', '3.03.04.03-2 Propriedades Mecânicas dos Metais e Ligas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1495, '095', 'a', 'mecanicas', '3.03.04.03-2 Propriedades Mecânicas dos Metais e Ligas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1496, '095', 'a', 'dos', '3.03.04.03-2 Propriedades Mecânicas dos Metais e Ligas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1497, '095', 'a', 'metais', '3.03.04.03-2 Propriedades Mecânicas dos Metais e Ligas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1498, '095', 'a', 'ligas', '3.03.04.03-2 Propriedades Mecânicas dos Metais e Ligas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1499, '095', 'a', '3.03.04.04-0', '3.03.04.04-0 Transformação de Fases', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1500, '095', 'a', 'transformacao', '3.03.04.04-0 Transformação de Fases', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1501, '095', 'a', 'de', '3.03.04.04-0 Transformação de Fases', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1502, '095', 'a', 'fases', '3.03.04.04-0 Transformação de Fases', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1503, '095', 'a', '3.03.04.05-9', '3.03.04.05-9 Corrosão', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1504, '095', 'a', 'corrosao', '3.03.04.05-9 Corrosão', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1505, '095', 'a', '3.03.05.00-4', '3.03.05.00-4 Materiais não Metálicos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1506, '095', 'a', 'materiais', '3.03.05.00-4 Materiais não Metálicos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1507, '095', 'a', 'nao', '3.03.05.00-4 Materiais não Metálicos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1508, '095', 'a', 'metalicos', '3.03.05.00-4 Materiais não Metálicos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1509, '095', 'a', '3.03.05.01-2', '3.03.05.01-2 Extração e Transformação de Materiais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1510, '095', 'a', 'extracao', '3.03.05.01-2 Extração e Transformação de Materiais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1511, '095', 'a', 'transformacao', '3.03.05.01-2 Extração e Transformação de Materiais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1512, '095', 'a', 'de', '3.03.05.01-2 Extração e Transformação de Materiais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1513, '095', 'a', 'materiais', '3.03.05.01-2 Extração e Transformação de Materiais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1514, '095', 'a', '3.03.05.02-0', '3.03.05.02-0 Cerâmicos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1515, '095', 'a', 'ceramicos', '3.03.05.02-0 Cerâmicos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1516, '095', 'a', '3.03.05.03-9', '3.03.05.03-9 Materiais Conjugados não Metálicos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1517, '095', 'a', 'materiais', '3.03.05.03-9 Materiais Conjugados não Metálicos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1518, '095', 'a', 'conjugados', '3.03.05.03-9 Materiais Conjugados não Metálicos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1519, '095', 'a', 'nao', '3.03.05.03-9 Materiais Conjugados não Metálicos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1520, '095', 'a', 'metalicos', '3.03.05.03-9 Materiais Conjugados não Metálicos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1521, '095', 'a', '3.03.05.04-7', '3.03.05.04-7 Polímeros, Aplicações', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1522, '095', 'a', 'polimeros,', '3.03.05.04-7 Polímeros, Aplicações', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1523, '095', 'a', 'aplicacoes', '3.03.05.04-7 Polímeros, Aplicações', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1524, '095', 'a', '3.04.00.00-7', '3.04.00.00-7 Engenharia Elétrica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1525, '095', 'a', 'engenharia', '3.04.00.00-7 Engenharia Elétrica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1526, '095', 'a', 'eletrica', '3.04.00.00-7 Engenharia Elétrica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1527, '095', 'a', '3.04.01.00-3', '3.04.01.00-3 Materiais Elétricos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1528, '095', 'a', 'materiais', '3.04.01.00-3 Materiais Elétricos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1529, '095', 'a', 'eletricos', '3.04.01.00-3 Materiais Elétricos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1530, '095', 'a', '3.04.01.01-1', '3.04.01.01-1 Materiais Condutores', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1531, '095', 'a', 'materiais', '3.04.01.01-1 Materiais Condutores', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1532, '095', 'a', 'condutores', '3.04.01.01-1 Materiais Condutores', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1533, '095', 'a', '3.04.01.02-0', '3.04.01.02-0 Materiais e Componentes Semicondutores', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1534, '095', 'a', 'materiais', '3.04.01.02-0 Materiais e Componentes Semicondutores', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1535, '095', 'a', 'componentes', '3.04.01.02-0 Materiais e Componentes Semicondutores', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1536, '095', 'a', 'semicondutores', '3.04.01.02-0 Materiais e Componentes Semicondutores', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1537, '095', 'a', '3.04.01.03-8', '3.04.01.03-8 Materiais e Dispositivos Supercondutores', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1538, '095', 'a', 'materiais', '3.04.01.03-8 Materiais e Dispositivos Supercondutores', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1539, '095', 'a', 'dispositivos', '3.04.01.03-8 Materiais e Dispositivos Supercondutores', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1540, '095', 'a', 'supercondutores', '3.04.01.03-8 Materiais e Dispositivos Supercondutores', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1541, '095', 'a', '3.04.01.04-6', '3.04.01.04-6 Materiais Dielétricos, Piesoelétricos e Ferroelétricos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1542, '095', 'a', 'materiais', '3.04.01.04-6 Materiais Dielétricos, Piesoelétricos e Ferroelétricos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1543, '095', 'a', 'dieletricos,', '3.04.01.04-6 Materiais Dielétricos, Piesoelétricos e Ferroelétricos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1544, '095', 'a', 'piesoeletricos', '3.04.01.04-6 Materiais Dielétricos, Piesoelétricos e Ferroelétricos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1545, '095', 'a', 'ferroeletricos', '3.04.01.04-6 Materiais Dielétricos, Piesoelétricos e Ferroelétricos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1546, '095', 'a', '3.04.01.05-4', '3.04.01.05-4 Materiais e Componentes Eletroóticos e Magnetoóticos, Materiais Fotoelétricos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1547, '095', 'a', 'materiais', '3.04.01.05-4 Materiais e Componentes Eletroóticos e Magnetoóticos, Materiais Fotoelétricos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1548, '095', 'a', 'componentes', '3.04.01.05-4 Materiais e Componentes Eletroóticos e Magnetoóticos, Materiais Fotoelétricos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1549, '095', 'a', 'eletrooticos', '3.04.01.05-4 Materiais e Componentes Eletroóticos e Magnetoóticos, Materiais Fotoelétricos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1550, '095', 'a', 'magnetooticos,', '3.04.01.05-4 Materiais e Componentes Eletroóticos e Magnetoóticos, Materiais Fotoelétricos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1551, '095', 'a', 'materiais', '3.04.01.05-4 Materiais e Componentes Eletroóticos e Magnetoóticos, Materiais Fotoelétricos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1552, '095', 'a', 'fotoeletricos', '3.04.01.05-4 Materiais e Componentes Eletroóticos e Magnetoóticos, Materiais Fotoelétricos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1553, '095', 'a', '3.04.01.06-2', '3.04.01.06-2 Materiais e Dispositivos Magnéticos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1554, '095', 'a', 'materiais', '3.04.01.06-2 Materiais e Dispositivos Magnéticos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1555, '095', 'a', 'dispositivos', '3.04.01.06-2 Materiais e Dispositivos Magnéticos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1556, '095', 'a', 'magneticos', '3.04.01.06-2 Materiais e Dispositivos Magnéticos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1557, '095', 'a', '3.04.02.00-0', '3.04.02.00-0 Medidas Elétricas, Magnéticas e Eletrônicas; Instrumentação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1558, '095', 'a', 'medidas', '3.04.02.00-0 Medidas Elétricas, Magnéticas e Eletrônicas; Instrumentação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1559, '095', 'a', 'eletricas,', '3.04.02.00-0 Medidas Elétricas, Magnéticas e Eletrônicas; Instrumentação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1560, '095', 'a', 'magneticas', '3.04.02.00-0 Medidas Elétricas, Magnéticas e Eletrônicas; Instrumentação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1561, '095', 'a', 'eletronicas;', '3.04.02.00-0 Medidas Elétricas, Magnéticas e Eletrônicas; Instrumentação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1562, '095', 'a', 'instrumentacao', '3.04.02.00-0 Medidas Elétricas, Magnéticas e Eletrônicas; Instrumentação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1563, '095', 'a', '3.04.02.01-8', '3.04.02.01-8 Medidas Elétricas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1564, '095', 'a', 'medidas', '3.04.02.01-8 Medidas Elétricas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1565, '095', 'a', 'eletricas', '3.04.02.01-8 Medidas Elétricas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1566, '095', 'a', '3.04.02.02-6', '3.04.02.02-6 Medidas Magnéticas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1567, '095', 'a', 'medidas', '3.04.02.02-6 Medidas Magnéticas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1568, '095', 'a', 'magneticas', '3.04.02.02-6 Medidas Magnéticas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1569, '095', 'a', '3.04.02.03-4', '3.04.02.03-4 Instrumentação Eletromecânica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1570, '095', 'a', 'instrumentacao', '3.04.02.03-4 Instrumentação Eletromecânica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1571, '095', 'a', 'eletromecanica', '3.04.02.03-4 Instrumentação Eletromecânica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1572, '095', 'a', '3.04.02.04-2', '3.04.02.04-2 Instrumentação Eletrônica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1573, '095', 'a', 'instrumentacao', '3.04.02.04-2 Instrumentação Eletrônica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1574, '095', 'a', 'eletronica', '3.04.02.04-2 Instrumentação Eletrônica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1575, '095', 'a', '3.04.02.05-0', '3.04.02.05-0 Sistemas Eletrônicos de Medida e de Controle', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1576, '095', 'a', 'sistemas', '3.04.02.05-0 Sistemas Eletrônicos de Medida e de Controle', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1577, '095', 'a', 'eletronicos', '3.04.02.05-0 Sistemas Eletrônicos de Medida e de Controle', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1578, '095', 'a', 'de', '3.04.02.05-0 Sistemas Eletrônicos de Medida e de Controle', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1579, '095', 'a', 'medida', '3.04.02.05-0 Sistemas Eletrônicos de Medida e de Controle', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1580, '095', 'a', 'de', '3.04.02.05-0 Sistemas Eletrônicos de Medida e de Controle', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1581, '095', 'a', 'controle', '3.04.02.05-0 Sistemas Eletrônicos de Medida e de Controle', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1582, '095', 'a', '3.04.03.00-6', '3.04.03.00-6 Circuitos Elétricos, Magnéticos e Eletrônicos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1583, '095', 'a', 'circuitos', '3.04.03.00-6 Circuitos Elétricos, Magnéticos e Eletrônicos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1584, '095', 'a', 'eletricos,', '3.04.03.00-6 Circuitos Elétricos, Magnéticos e Eletrônicos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1585, '095', 'a', 'magneticos', '3.04.03.00-6 Circuitos Elétricos, Magnéticos e Eletrônicos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1586, '095', 'a', 'eletronicos', '3.04.03.00-6 Circuitos Elétricos, Magnéticos e Eletrônicos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1587, '095', 'a', '3.04.03.01-4', '3.04.03.01-4 Teoria Geral dos Circuitos Elétricos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1588, '095', 'a', 'teoria', '3.04.03.01-4 Teoria Geral dos Circuitos Elétricos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1589, '095', 'a', 'geral', '3.04.03.01-4 Teoria Geral dos Circuitos Elétricos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1590, '095', 'a', 'dos', '3.04.03.01-4 Teoria Geral dos Circuitos Elétricos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1591, '095', 'a', 'circuitos', '3.04.03.01-4 Teoria Geral dos Circuitos Elétricos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1592, '095', 'a', 'eletricos', '3.04.03.01-4 Teoria Geral dos Circuitos Elétricos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1593, '095', 'a', '3.04.03.02-2', '3.04.03.02-2 Circuitos Lineares e Não-Lineares', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1594, '095', 'a', 'circuitos', '3.04.03.02-2 Circuitos Lineares e Não-Lineares', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1595, '095', 'a', 'lineares', '3.04.03.02-2 Circuitos Lineares e Não-Lineares', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1596, '095', 'a', 'nao-lineares', '3.04.03.02-2 Circuitos Lineares e Não-Lineares', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1597, '095', 'a', '3.04.03.03-0', '3.04.03.03-0 Circuitos Eletrônicos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1598, '095', 'a', 'circuitos', '3.04.03.03-0 Circuitos Eletrônicos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1599, '095', 'a', 'eletronicos', '3.04.03.03-0 Circuitos Eletrônicos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1600, '095', 'a', '3.04.03.04-9', '3.04.03.04-9 Circuitos Magnéticos, Magnetismos, Eletromagnetismo', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1601, '095', 'a', 'circuitos', '3.04.03.04-9 Circuitos Magnéticos, Magnetismos, Eletromagnetismo', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1602, '095', 'a', 'magneticos,', '3.04.03.04-9 Circuitos Magnéticos, Magnetismos, Eletromagnetismo', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1603, '095', 'a', 'magnetismos,', '3.04.03.04-9 Circuitos Magnéticos, Magnetismos, Eletromagnetismo', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1604, '095', 'a', 'eletromagnetismo', '3.04.03.04-9 Circuitos Magnéticos, Magnetismos, Eletromagnetismo', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1605, '095', 'a', '3.04.04.00-2', '3.04.04.00-2 Sistemas Elétricos de Potência', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1606, '095', 'a', 'sistemas', '3.04.04.00-2 Sistemas Elétricos de Potência', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1607, '095', 'a', 'eletricos', '3.04.04.00-2 Sistemas Elétricos de Potência', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1608, '095', 'a', 'de', '3.04.04.00-2 Sistemas Elétricos de Potência', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1609, '095', 'a', 'potencia', '3.04.04.00-2 Sistemas Elétricos de Potência', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1610, '095', 'a', '3.04.04.01-0', '3.04.04.01-0 Geração da Energia Elétrica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1611, '095', 'a', 'geracao', '3.04.04.01-0 Geração da Energia Elétrica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1612, '095', 'a', 'da', '3.04.04.01-0 Geração da Energia Elétrica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1613, '095', 'a', 'energia', '3.04.04.01-0 Geração da Energia Elétrica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1614, '095', 'a', 'eletrica', '3.04.04.01-0 Geração da Energia Elétrica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1615, '095', 'a', '3.04.04.02-9', '3.04.04.02-9 Transmissão da Energia Elétrica, Distribuição da Energia Elétrica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1616, '095', 'a', 'transmissao', '3.04.04.02-9 Transmissão da Energia Elétrica, Distribuição da Energia Elétrica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1617, '095', 'a', 'da', '3.04.04.02-9 Transmissão da Energia Elétrica, Distribuição da Energia Elétrica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1618, '095', 'a', 'energia', '3.04.04.02-9 Transmissão da Energia Elétrica, Distribuição da Energia Elétrica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1619, '095', 'a', 'eletrica,', '3.04.04.02-9 Transmissão da Energia Elétrica, Distribuição da Energia Elétrica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1620, '095', 'a', 'distribuicao', '3.04.04.02-9 Transmissão da Energia Elétrica, Distribuição da Energia Elétrica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1621, '095', 'a', 'da', '3.04.04.02-9 Transmissão da Energia Elétrica, Distribuição da Energia Elétrica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1622, '095', 'a', 'energia', '3.04.04.02-9 Transmissão da Energia Elétrica, Distribuição da Energia Elétrica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1623, '095', 'a', 'eletrica', '3.04.04.02-9 Transmissão da Energia Elétrica, Distribuição da Energia Elétrica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1624, '095', 'a', '3.04.04.03-7', '3.04.04.03-7 Conversão e Retificação da Energia Elétrica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1625, '095', 'a', 'conversao', '3.04.04.03-7 Conversão e Retificação da Energia Elétrica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1626, '095', 'a', 'retificacao', '3.04.04.03-7 Conversão e Retificação da Energia Elétrica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1627, '095', 'a', 'da', '3.04.04.03-7 Conversão e Retificação da Energia Elétrica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1628, '095', 'a', 'energia', '3.04.04.03-7 Conversão e Retificação da Energia Elétrica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1629, '095', 'a', 'eletrica', '3.04.04.03-7 Conversão e Retificação da Energia Elétrica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1630, '095', 'a', '3.04.04.04-5', '3.04.04.04-5 Medição, Controle, Correção e Proteção de Sistemas Elétricos de Potência', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1631, '095', 'a', 'medicao,', '3.04.04.04-5 Medição, Controle, Correção e Proteção de Sistemas Elétricos de Potência', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1632, '095', 'a', 'controle,', '3.04.04.04-5 Medição, Controle, Correção e Proteção de Sistemas Elétricos de Potência', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1633, '095', 'a', 'correcao', '3.04.04.04-5 Medição, Controle, Correção e Proteção de Sistemas Elétricos de Potência', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1634, '095', 'a', 'protecao', '3.04.04.04-5 Medição, Controle, Correção e Proteção de Sistemas Elétricos de Potência', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1635, '095', 'a', 'de', '3.04.04.04-5 Medição, Controle, Correção e Proteção de Sistemas Elétricos de Potência', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1636, '095', 'a', 'sistemas', '3.04.04.04-5 Medição, Controle, Correção e Proteção de Sistemas Elétricos de Potência', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1637, '095', 'a', 'eletricos', '3.04.04.04-5 Medição, Controle, Correção e Proteção de Sistemas Elétricos de Potência', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1638, '095', 'a', 'de', '3.04.04.04-5 Medição, Controle, Correção e Proteção de Sistemas Elétricos de Potência', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1639, '095', 'a', 'potencia', '3.04.04.04-5 Medição, Controle, Correção e Proteção de Sistemas Elétricos de Potência', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1640, '095', 'a', '3.04.04.05-3', '3.04.04.05-3 Máquinas Elétricas e Dispositivos de Potência', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1641, '095', 'a', 'maquinas', '3.04.04.05-3 Máquinas Elétricas e Dispositivos de Potência', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1642, '095', 'a', 'eletricas', '3.04.04.05-3 Máquinas Elétricas e Dispositivos de Potência', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1643, '095', 'a', 'dispositivos', '3.04.04.05-3 Máquinas Elétricas e Dispositivos de Potência', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1644, '095', 'a', 'de', '3.04.04.05-3 Máquinas Elétricas e Dispositivos de Potência', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1645, '095', 'a', 'potencia', '3.04.04.05-3 Máquinas Elétricas e Dispositivos de Potência', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1646, '095', 'a', '3.04.04.06-1', '3.04.04.06-1 Instalações Elétricas Prediais e Industriais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1647, '095', 'a', 'instalacoes', '3.04.04.06-1 Instalações Elétricas Prediais e Industriais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1648, '095', 'a', 'eletricas', '3.04.04.06-1 Instalações Elétricas Prediais e Industriais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1649, '095', 'a', 'prediais', '3.04.04.06-1 Instalações Elétricas Prediais e Industriais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1650, '095', 'a', 'industriais', '3.04.04.06-1 Instalações Elétricas Prediais e Industriais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1651, '095', 'a', '3.04.05.00-9', '3.04.05.00-9 Eletrônica Industrial, Sistemas e Controles Eletrônicos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1652, '095', 'a', 'eletronica', '3.04.05.00-9 Eletrônica Industrial, Sistemas e Controles Eletrônicos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1653, '095', 'a', 'industrial,', '3.04.05.00-9 Eletrônica Industrial, Sistemas e Controles Eletrônicos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1654, '095', 'a', 'sistemas', '3.04.05.00-9 Eletrônica Industrial, Sistemas e Controles Eletrônicos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1655, '095', 'a', 'controles', '3.04.05.00-9 Eletrônica Industrial, Sistemas e Controles Eletrônicos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1656, '095', 'a', 'eletronicos', '3.04.05.00-9 Eletrônica Industrial, Sistemas e Controles Eletrônicos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1657, '095', 'a', '3.04.05.01-7', '3.04.05.01-7 Eletrônica Industrial', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1658, '095', 'a', 'eletronica', '3.04.05.01-7 Eletrônica Industrial', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1659, '095', 'a', 'industrial', '3.04.05.01-7 Eletrônica Industrial', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1660, '095', 'a', '3.04.05.02-5', '3.04.05.02-5 Automação Eletrônica de Processos Elétricos e Industriais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1661, '095', 'a', 'automacao', '3.04.05.02-5 Automação Eletrônica de Processos Elétricos e Industriais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1662, '095', 'a', 'eletronica', '3.04.05.02-5 Automação Eletrônica de Processos Elétricos e Industriais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1663, '095', 'a', 'de', '3.04.05.02-5 Automação Eletrônica de Processos Elétricos e Industriais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1664, '095', 'a', 'processos', '3.04.05.02-5 Automação Eletrônica de Processos Elétricos e Industriais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1665, '095', 'a', 'eletricos', '3.04.05.02-5 Automação Eletrônica de Processos Elétricos e Industriais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1666, '095', 'a', 'industriais', '3.04.05.02-5 Automação Eletrônica de Processos Elétricos e Industriais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1667, '095', 'a', '3.04.05.03-3', '3.04.05.03-3 Controle de Processos Eletrônicos, Retroalimentação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1668, '095', 'a', 'controle', '3.04.05.03-3 Controle de Processos Eletrônicos, Retroalimentação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1669, '095', 'a', 'de', '3.04.05.03-3 Controle de Processos Eletrônicos, Retroalimentação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1670, '095', 'a', 'processos', '3.04.05.03-3 Controle de Processos Eletrônicos, Retroalimentação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1671, '095', 'a', 'eletronicos,', '3.04.05.03-3 Controle de Processos Eletrônicos, Retroalimentação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1672, '095', 'a', 'retroalimentacao', '3.04.05.03-3 Controle de Processos Eletrônicos, Retroalimentação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1673, '095', 'a', '3.04.06.00-5', '3.04.06.00-5 Telecomunicações', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1674, '095', 'a', 'telecomunicacoes', '3.04.06.00-5 Telecomunicações', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1675, '095', 'a', '3.04.06.01-3', '3.04.06.01-3 Teoria Eletromagnética, Microondas, Propagação de Ondas, Antenas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1676, '095', 'a', 'teoria', '3.04.06.01-3 Teoria Eletromagnética, Microondas, Propagação de Ondas, Antenas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1677, '095', 'a', 'eletromagnetica,', '3.04.06.01-3 Teoria Eletromagnética, Microondas, Propagação de Ondas, Antenas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1678, '095', 'a', 'microondas,', '3.04.06.01-3 Teoria Eletromagnética, Microondas, Propagação de Ondas, Antenas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1679, '095', 'a', 'propagacao', '3.04.06.01-3 Teoria Eletromagnética, Microondas, Propagação de Ondas, Antenas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1680, '095', 'a', 'de', '3.04.06.01-3 Teoria Eletromagnética, Microondas, Propagação de Ondas, Antenas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1681, '095', 'a', 'ondas,', '3.04.06.01-3 Teoria Eletromagnética, Microondas, Propagação de Ondas, Antenas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1682, '095', 'a', 'antenas', '3.04.06.01-3 Teoria Eletromagnética, Microondas, Propagação de Ondas, Antenas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1683, '095', 'a', '3.04.06.02-1', '3.04.06.02-1 Radionavegação e Radioastronomia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1684, '095', 'a', 'radionavegacao', '3.04.06.02-1 Radionavegação e Radioastronomia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1685, '095', 'a', 'radioastronomia', '3.04.06.02-1 Radionavegação e Radioastronomia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1686, '095', 'a', '3.04.06.03-0', '3.04.06.03-0 Sistemas de Telecomunicações', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1687, '095', 'a', 'sistemas', '3.04.06.03-0 Sistemas de Telecomunicações', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1688, '095', 'a', 'de', '3.04.06.03-0 Sistemas de Telecomunicações', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1689, '095', 'a', 'telecomunicacoes', '3.04.06.03-0 Sistemas de Telecomunicações', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1690, '095', 'a', '3.05.00.00-1', '3.05.00.00-1 Engenharia Mecânica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1691, '095', 'a', 'engenharia', '3.05.00.00-1 Engenharia Mecânica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1692, '095', 'a', 'mecanica', '3.05.00.00-1 Engenharia Mecânica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1693, '095', 'a', '3.05.01.00-8', '3.05.01.00-8 Fenômenos de Transporte', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1694, '095', 'a', 'fenomenos', '3.05.01.00-8 Fenômenos de Transporte', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1695, '095', 'a', 'de', '3.05.01.00-8 Fenômenos de Transporte', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1696, '095', 'a', 'transporte', '3.05.01.00-8 Fenômenos de Transporte', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1697, '095', 'a', '3.05.01.01-6', '3.05.01.01-6 Transferência de Calor', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1698, '095', 'a', 'transferencia', '3.05.01.01-6 Transferência de Calor', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1699, '095', 'a', 'de', '3.05.01.01-6 Transferência de Calor', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1700, '095', 'a', 'calor', '3.05.01.01-6 Transferência de Calor', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1701, '095', 'a', '3.05.01.02-4', '3.05.01.02-4 Mecânica dos Fluidos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1702, '095', 'a', 'mecanica', '3.05.01.02-4 Mecânica dos Fluidos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1703, '095', 'a', 'dos', '3.05.01.02-4 Mecânica dos Fluidos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1704, '095', 'a', 'fluidos', '3.05.01.02-4 Mecânica dos Fluidos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1705, '095', 'a', '3.05.01.03-2', '3.05.01.03-2 Dinâmica dos Gases', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1706, '095', 'a', 'dinamica', '3.05.01.03-2 Dinâmica dos Gases', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1707, '095', 'a', 'dos', '3.05.01.03-2 Dinâmica dos Gases', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1708, '095', 'a', 'gases', '3.05.01.03-2 Dinâmica dos Gases', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1709, '095', 'a', '3.05.01.04-0', '3.05.01.04-0 Principios Variacionais e Métodos Numéricos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1710, '095', 'a', 'principios', '3.05.01.04-0 Principios Variacionais e Métodos Numéricos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1711, '095', 'a', 'variacionais', '3.05.01.04-0 Principios Variacionais e Métodos Numéricos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1712, '095', 'a', 'metodos', '3.05.01.04-0 Principios Variacionais e Métodos Numéricos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1713, '095', 'a', 'numericos', '3.05.01.04-0 Principios Variacionais e Métodos Numéricos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1714, '095', 'a', '3.05.02.00-4', '3.05.02.00-4 Engenharia Térmica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1715, '095', 'a', 'engenharia', '3.05.02.00-4 Engenharia Térmica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1716, '095', 'a', 'termica', '3.05.02.00-4 Engenharia Térmica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1717, '095', 'a', '3.05.02.01-2', '3.05.02.01-2 Termodinâmica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1718, '095', 'a', 'termodinamica', '3.05.02.01-2 Termodinâmica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1719, '095', 'a', '3.05.02.02-0', '3.05.02.02-0 Controle Ambiental', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1720, '095', 'a', 'controle', '3.05.02.02-0 Controle Ambiental', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1721, '095', 'a', 'ambiental', '3.05.02.02-0 Controle Ambiental', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1722, '095', 'a', '3.05.02.03-9', '3.05.02.03-9 Aproveitamento da Energia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1723, '095', 'a', 'aproveitamento', '3.05.02.03-9 Aproveitamento da Energia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1724, '095', 'a', 'da', '3.05.02.03-9 Aproveitamento da Energia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1725, '095', 'a', 'energia', '3.05.02.03-9 Aproveitamento da Energia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1726, '095', 'a', '3.05.03.00-0', '3.05.03.00-0 Mecânica dos Sólidos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1727, '095', 'a', 'mecanica', '3.05.03.00-0 Mecânica dos Sólidos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1728, '095', 'a', 'dos', '3.05.03.00-0 Mecânica dos Sólidos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1729, '095', 'a', 'solidos', '3.05.03.00-0 Mecânica dos Sólidos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1730, '095', 'a', '3.05.03.01-9', '3.05.03.01-9 Mecânica dos Corpos Sólidos, Elásticos e Plásticos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1731, '095', 'a', 'mecanica', '3.05.03.01-9 Mecânica dos Corpos Sólidos, Elásticos e Plásticos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1732, '095', 'a', 'dos', '3.05.03.01-9 Mecânica dos Corpos Sólidos, Elásticos e Plásticos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1733, '095', 'a', 'corpos', '3.05.03.01-9 Mecânica dos Corpos Sólidos, Elásticos e Plásticos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1734, '095', 'a', 'solidos,', '3.05.03.01-9 Mecânica dos Corpos Sólidos, Elásticos e Plásticos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1735, '095', 'a', 'elasticos', '3.05.03.01-9 Mecânica dos Corpos Sólidos, Elásticos e Plásticos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1736, '095', 'a', 'plasticos', '3.05.03.01-9 Mecânica dos Corpos Sólidos, Elásticos e Plásticos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1737, '095', 'a', '3.05.03.02-7', '3.05.03.02-7 Dinâmica dos Corpos Rígidos, Elásticos e Plásticos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1738, '095', 'a', 'dinamica', '3.05.03.02-7 Dinâmica dos Corpos Rígidos, Elásticos e Plásticos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1739, '095', 'a', 'dos', '3.05.03.02-7 Dinâmica dos Corpos Rígidos, Elásticos e Plásticos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1740, '095', 'a', 'corpos', '3.05.03.02-7 Dinâmica dos Corpos Rígidos, Elásticos e Plásticos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1741, '095', 'a', 'rigidos,', '3.05.03.02-7 Dinâmica dos Corpos Rígidos, Elásticos e Plásticos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1742, '095', 'a', 'elasticos', '3.05.03.02-7 Dinâmica dos Corpos Rígidos, Elásticos e Plásticos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1743, '095', 'a', 'plasticos', '3.05.03.02-7 Dinâmica dos Corpos Rígidos, Elásticos e Plásticos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1744, '095', 'a', '3.05.03.03-5', '3.05.03.03-5 Análise de Tensões', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1745, '095', 'a', 'analise', '3.05.03.03-5 Análise de Tensões', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1746, '095', 'a', 'de', '3.05.03.03-5 Análise de Tensões', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1747, '095', 'a', 'tensoes', '3.05.03.03-5 Análise de Tensões', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1748, '095', 'a', '3.05.03.04-3', '3.05.03.04-3 Termoelasticidade', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1749, '095', 'a', 'termoelasticidade', '3.05.03.04-3 Termoelasticidade', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1750, '095', 'a', '3.05.04.00-7', '3.05.04.00-7 Projetos de Máquinas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1751, '095', 'a', 'projetos', '3.05.04.00-7 Projetos de Máquinas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1752, '095', 'a', 'de', '3.05.04.00-7 Projetos de Máquinas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1753, '095', 'a', 'maquinas', '3.05.04.00-7 Projetos de Máquinas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1754, '095', 'a', '3.05.04.01-5', '3.05.04.01-5 Teoria dos Mecanismos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1755, '095', 'a', 'teoria', '3.05.04.01-5 Teoria dos Mecanismos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1756, '095', 'a', 'dos', '3.05.04.01-5 Teoria dos Mecanismos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1757, '095', 'a', 'mecanismos', '3.05.04.01-5 Teoria dos Mecanismos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1758, '095', 'a', '3.05.04.02-3', '3.05.04.02-3 Estática e Dinâmica Aplicada', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1759, '095', 'a', 'estatica', '3.05.04.02-3 Estática e Dinâmica Aplicada', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1760, '095', 'a', 'dinamica', '3.05.04.02-3 Estática e Dinâmica Aplicada', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1761, '095', 'a', 'aplicada', '3.05.04.02-3 Estática e Dinâmica Aplicada', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1762, '095', 'a', '3.05.04.03-1', '3.05.04.03-1 Elementos de Máquinas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1763, '095', 'a', 'elementos', '3.05.04.03-1 Elementos de Máquinas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1764, '095', 'a', 'de', '3.05.04.03-1 Elementos de Máquinas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1765, '095', 'a', 'maquinas', '3.05.04.03-1 Elementos de Máquinas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1766, '095', 'a', '3.05.04.04-0', '3.05.04.04-0 Fundamentos Gerais de Projetos das Máquinas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1767, '095', 'a', 'fundamentos', '3.05.04.04-0 Fundamentos Gerais de Projetos das Máquinas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1768, '095', 'a', 'gerais', '3.05.04.04-0 Fundamentos Gerais de Projetos das Máquinas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1769, '095', 'a', 'de', '3.05.04.04-0 Fundamentos Gerais de Projetos das Máquinas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1770, '095', 'a', 'projetos', '3.05.04.04-0 Fundamentos Gerais de Projetos das Máquinas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1771, '095', 'a', 'das', '3.05.04.04-0 Fundamentos Gerais de Projetos das Máquinas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1772, '095', 'a', 'maquinas', '3.05.04.04-0 Fundamentos Gerais de Projetos das Máquinas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1773, '095', 'a', '3.05.04.05-8', '3.05.04.05-8 Máquinas, Motores e Equipamentos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1774, '095', 'a', 'maquinas,', '3.05.04.05-8 Máquinas, Motores e Equipamentos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1775, '095', 'a', 'motores', '3.05.04.05-8 Máquinas, Motores e Equipamentos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1776, '095', 'a', 'equipamentos', '3.05.04.05-8 Máquinas, Motores e Equipamentos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1777, '095', 'a', '3.05.04.06-6', '3.05.04.06-6 Métodos de Síntese e Otimização Aplicados ao Projeto Mecânico', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1778, '095', 'a', 'metodos', '3.05.04.06-6 Métodos de Síntese e Otimização Aplicados ao Projeto Mecânico', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1779, '095', 'a', 'de', '3.05.04.06-6 Métodos de Síntese e Otimização Aplicados ao Projeto Mecânico', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1780, '095', 'a', 'sintese', '3.05.04.06-6 Métodos de Síntese e Otimização Aplicados ao Projeto Mecânico', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1781, '095', 'a', 'otimizacao', '3.05.04.06-6 Métodos de Síntese e Otimização Aplicados ao Projeto Mecânico', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1782, '095', 'a', 'aplicados', '3.05.04.06-6 Métodos de Síntese e Otimização Aplicados ao Projeto Mecânico', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1783, '095', 'a', 'ao', '3.05.04.06-6 Métodos de Síntese e Otimização Aplicados ao Projeto Mecânico', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1784, '095', 'a', 'projeto', '3.05.04.06-6 Métodos de Síntese e Otimização Aplicados ao Projeto Mecânico', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1785, '095', 'a', 'mecanico', '3.05.04.06-6 Métodos de Síntese e Otimização Aplicados ao Projeto Mecânico', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1786, '095', 'a', '3.05.04.07-4', '3.05.04.07-4 Controle de Sistemas Mecânicos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1787, '095', 'a', 'controle', '3.05.04.07-4 Controle de Sistemas Mecânicos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1788, '095', 'a', 'de', '3.05.04.07-4 Controle de Sistemas Mecânicos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1789, '095', 'a', 'sistemas', '3.05.04.07-4 Controle de Sistemas Mecânicos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1790, '095', 'a', 'mecanicos', '3.05.04.07-4 Controle de Sistemas Mecânicos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1791, '095', 'a', '3.05.04.08-2', '3.05.04.08-2 Aproveitamento de Energia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1792, '095', 'a', 'aproveitamento', '3.05.04.08-2 Aproveitamento de Energia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1793, '095', 'a', 'de', '3.05.04.08-2 Aproveitamento de Energia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1794, '095', 'a', 'energia', '3.05.04.08-2 Aproveitamento de Energia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1795, '095', 'a', '3.05.05.00-3', '3.05.05.00-3 Processos de Fabricação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1796, '095', 'a', 'processos', '3.05.05.00-3 Processos de Fabricação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1797, '095', 'a', 'de', '3.05.05.00-3 Processos de Fabricação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1798, '095', 'a', 'fabricacao', '3.05.05.00-3 Processos de Fabricação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1799, '095', 'a', '3.05.05.01-1', '3.05.05.01-1 Matrizes e Ferramentas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1800, '095', 'a', 'matrizes', '3.05.05.01-1 Matrizes e Ferramentas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1801, '095', 'a', 'ferramentas', '3.05.05.01-1 Matrizes e Ferramentas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1802, '095', 'a', '3.05.05.02-0', '3.05.05.02-0 Máquinas de Usinagem e Conformação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1803, '095', 'a', 'maquinas', '3.05.05.02-0 Máquinas de Usinagem e Conformação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1804, '095', 'a', 'de', '3.05.05.02-0 Máquinas de Usinagem e Conformação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1805, '095', 'a', 'usinagem', '3.05.05.02-0 Máquinas de Usinagem e Conformação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1806, '095', 'a', 'conformacao', '3.05.05.02-0 Máquinas de Usinagem e Conformação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1807, '095', 'a', '3.05.05.03-8', '3.05.05.03-8 Controle Numérico', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1808, '095', 'a', 'controle', '3.05.05.03-8 Controle Numérico', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1809, '095', 'a', 'numerico', '3.05.05.03-8 Controle Numérico', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1810, '095', 'a', '3.05.05.04-6', '3.05.05.04-6 Robotização', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1811, '095', 'a', 'robotizacao', '3.05.05.04-6 Robotização', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1812, '095', 'a', '3.05.05.05-4', '3.05.05.05-4 Processos de Fabricação, Seleção Econômica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1813, '095', 'a', 'processos', '3.05.05.05-4 Processos de Fabricação, Seleção Econômica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1814, '095', 'a', 'de', '3.05.05.05-4 Processos de Fabricação, Seleção Econômica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1815, '095', 'a', 'fabricacao,', '3.05.05.05-4 Processos de Fabricação, Seleção Econômica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1816, '095', 'a', 'selecao', '3.05.05.05-4 Processos de Fabricação, Seleção Econômica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1817, '095', 'a', 'economica', '3.05.05.05-4 Processos de Fabricação, Seleção Econômica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1818, '095', 'a', '3.06.00.00-6', '3.06.00.00-6 Engenharia Química', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1819, '095', 'a', 'engenharia', '3.06.00.00-6 Engenharia Química', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1820, '095', 'a', 'quimica', '3.06.00.00-6 Engenharia Química', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1821, '095', 'a', '3.06.01.00-2', '3.06.01.00-2 Processos Industriais de Engenharia Química', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1822, '095', 'a', 'processos', '3.06.01.00-2 Processos Industriais de Engenharia Química', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1823, '095', 'a', 'industriais', '3.06.01.00-2 Processos Industriais de Engenharia Química', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1824, '095', 'a', 'de', '3.06.01.00-2 Processos Industriais de Engenharia Química', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1825, '095', 'a', 'engenharia', '3.06.01.00-2 Processos Industriais de Engenharia Química', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1826, '095', 'a', 'quimica', '3.06.01.00-2 Processos Industriais de Engenharia Química', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1827, '095', 'a', '3.06.01.01-0', '3.06.01.01-0 Processos Bioquimicos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1828, '095', 'a', 'processos', '3.06.01.01-0 Processos Bioquimicos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1829, '095', 'a', 'bioquimicos', '3.06.01.01-0 Processos Bioquimicos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1830, '095', 'a', '3.06.01.02-9', '3.06.01.02-9 Processos Orgânicos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1831, '095', 'a', 'processos', '3.06.01.02-9 Processos Orgânicos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1832, '095', 'a', 'organicos', '3.06.01.02-9 Processos Orgânicos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1833, '095', 'a', '3.06.01.03-7', '3.06.01.03-7 Processos Inorgânicos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1834, '095', 'a', 'processos', '3.06.01.03-7 Processos Inorgânicos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1835, '095', 'a', 'inorganicos', '3.06.01.03-7 Processos Inorgânicos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1836, '095', 'a', '3.06.02.00-9', '3.06.02.00-9 Operações Industriais e Equipamentos para Engenharia Química', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1837, '095', 'a', 'operacoes', '3.06.02.00-9 Operações Industriais e Equipamentos para Engenharia Química', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1838, '095', 'a', 'industriais', '3.06.02.00-9 Operações Industriais e Equipamentos para Engenharia Química', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1839, '095', 'a', 'equipamentos', '3.06.02.00-9 Operações Industriais e Equipamentos para Engenharia Química', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1840, '095', 'a', 'para', '3.06.02.00-9 Operações Industriais e Equipamentos para Engenharia Química', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1841, '095', 'a', 'engenharia', '3.06.02.00-9 Operações Industriais e Equipamentos para Engenharia Química', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1842, '095', 'a', 'quimica', '3.06.02.00-9 Operações Industriais e Equipamentos para Engenharia Química', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1843, '095', 'a', '3.06.02.01-7', '3.06.02.01-7 Reatores Químicos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1844, '095', 'a', 'reatores', '3.06.02.01-7 Reatores Químicos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1845, '095', 'a', 'quimicos', '3.06.02.01-7 Reatores Químicos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1846, '095', 'a', '3.06.02.02-5', '3.06.02.02-5 Operações Características de Processos Bioquímicos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1847, '095', 'a', 'operacoes', '3.06.02.02-5 Operações Características de Processos Bioquímicos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1848, '095', 'a', 'caracteristicas', '3.06.02.02-5 Operações Características de Processos Bioquímicos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1849, '095', 'a', 'de', '3.06.02.02-5 Operações Características de Processos Bioquímicos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1850, '095', 'a', 'processos', '3.06.02.02-5 Operações Características de Processos Bioquímicos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1851, '095', 'a', 'bioquimicos', '3.06.02.02-5 Operações Características de Processos Bioquímicos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1852, '095', 'a', '3.06.02.03-3', '3.06.02.03-3 Operações de Separação e Mistura', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1853, '095', 'a', 'operacoes', '3.06.02.03-3 Operações de Separação e Mistura', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1854, '095', 'a', 'de', '3.06.02.03-3 Operações de Separação e Mistura', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1855, '095', 'a', 'separacao', '3.06.02.03-3 Operações de Separação e Mistura', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1856, '095', 'a', 'mistura', '3.06.02.03-3 Operações de Separação e Mistura', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1857, '095', 'a', '3.06.03.00-5', '3.06.03.00-5 Tecnologia Química', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1858, '095', 'a', 'tecnologia', '3.06.03.00-5 Tecnologia Química', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1859, '095', 'a', 'quimica', '3.06.03.00-5 Tecnologia Química', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1860, '095', 'a', '3.06.03.01-3', '3.06.03.01-3 Balancos Globais de Matéria e Energia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1861, '095', 'a', 'balancos', '3.06.03.01-3 Balancos Globais de Matéria e Energia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1862, '095', 'a', 'globais', '3.06.03.01-3 Balancos Globais de Matéria e Energia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1863, '095', 'a', 'de', '3.06.03.01-3 Balancos Globais de Matéria e Energia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1864, '095', 'a', 'materia', '3.06.03.01-3 Balancos Globais de Matéria e Energia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1865, '095', 'a', 'energia', '3.06.03.01-3 Balancos Globais de Matéria e Energia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1866, '095', 'a', '3.06.03.02-1', '3.06.03.02-1 Água', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1867, '095', 'a', 'agua', '3.06.03.02-1 Água', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1868, '095', 'a', '3.06.03.03-0', '3.06.03.03-0 Álcool', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1869, '095', 'a', 'alcool', '3.06.03.03-0 Álcool', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1870, '095', 'a', '3.06.03.04-8', '3.06.03.04-8 Alimentos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1871, '095', 'a', 'alimentos', '3.06.03.04-8 Alimentos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1872, '095', 'a', '3.06.03.05-6', '3.06.03.05-6 Borrachas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1873, '095', 'a', 'borrachas', '3.06.03.05-6 Borrachas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1874, '095', 'a', '3.06.03.06-4', '3.06.03.06-4 Carvão', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1875, '095', 'a', 'carvao', '3.06.03.06-4 Carvão', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1876, '095', 'a', '3.06.03.07-2', '3.06.03.07-2 Cerâmica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1877, '095', 'a', 'ceramica', '3.06.03.07-2 Cerâmica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1878, '095', 'a', '3.06.03.08-0', '3.06.03.08-0 Cimento', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1879, '095', 'a', 'cimento', '3.06.03.08-0 Cimento', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1880, '095', 'a', '3.06.03.09-9', '3.06.03.09-9 Couro', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1881, '095', 'a', 'couro', '3.06.03.09-9 Couro', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1882, '095', 'a', '3.06.03.10-2', '3.06.03.10-2 Detergentes', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1883, '095', 'a', 'detergentes', '3.06.03.10-2 Detergentes', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1884, '095', 'a', '3.06.03.11-0', '3.06.03.11-0 Fertilizantes', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1885, '095', 'a', 'fertilizantes', '3.06.03.11-0 Fertilizantes', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1886, '095', 'a', '3.06.03.12-9', '3.06.03.12-9 Medicamentos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1887, '095', 'a', 'medicamentos', '3.06.03.12-9 Medicamentos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1888, '095', 'a', '3.06.03.13-7', '3.06.03.13-7 Metais não-Ferrosos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1889, '095', 'a', 'metais', '3.06.03.13-7 Metais não-Ferrosos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1890, '095', 'a', 'nao-ferrosos', '3.06.03.13-7 Metais não-Ferrosos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1891, '095', 'a', '3.06.03.14-5', '3.06.03.14-5 Óleos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1892, '095', 'a', 'oleos', '3.06.03.14-5 Óleos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1893, '095', 'a', '3.06.03.15-3', '3.06.03.15-3 Papel e Celulose', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1894, '095', 'a', 'papel', '3.06.03.15-3 Papel e Celulose', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1895, '095', 'a', 'celulose', '3.06.03.15-3 Papel e Celulose', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1896, '095', 'a', '3.06.03.16-1', '3.06.03.16-1 Petróleo e Petroquímica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1897, '095', 'a', 'petroleo', '3.06.03.16-1 Petróleo e Petroquímica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1898, '095', 'a', 'petroquimica', '3.06.03.16-1 Petróleo e Petroquímica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1899, '095', 'a', '3.06.03.17-0', '3.06.03.17-0 Polímeros', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1900, '095', 'a', 'polimeros', '3.06.03.17-0 Polímeros', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1901, '095', 'a', '3.06.03.18-8', '3.06.03.18-8 Produtos Naturais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1902, '095', 'a', 'produtos', '3.06.03.18-8 Produtos Naturais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1903, '095', 'a', 'naturais', '3.06.03.18-8 Produtos Naturais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1904, '095', 'a', '3.06.03.19-6', '3.06.03.19-6 Têxteis', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1905, '095', 'a', 'texteis', '3.06.03.19-6 Têxteis', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1906, '095', 'a', '3.06.03.20-0', '3.06.03.20-0 Tratamentos e Aproveitamento de Rejeitos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1907, '095', 'a', 'tratamentos', '3.06.03.20-0 Tratamentos e Aproveitamento de Rejeitos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1908, '095', 'a', 'aproveitamento', '3.06.03.20-0 Tratamentos e Aproveitamento de Rejeitos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1909, '095', 'a', 'de', '3.06.03.20-0 Tratamentos e Aproveitamento de Rejeitos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1910, '095', 'a', 'rejeitos', '3.06.03.20-0 Tratamentos e Aproveitamento de Rejeitos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1911, '095', 'a', '3.06.03.21-8', '3.06.03.21-8 Xisto', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1912, '095', 'a', 'xisto', '3.06.03.21-8 Xisto', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1913, '095', 'a', '3.07.00.00-0', '3.07.00.00-0 Engenharia Sanitária', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1914, '095', 'a', 'engenharia', '3.07.00.00-0 Engenharia Sanitária', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1915, '095', 'a', 'sanitaria', '3.07.00.00-0 Engenharia Sanitária', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1916, '095', 'a', '3.07.01.00-7', '3.07.01.00-7 Recursos Hídricos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1917, '095', 'a', 'recursos', '3.07.01.00-7 Recursos Hídricos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1918, '095', 'a', 'hidricos', '3.07.01.00-7 Recursos Hídricos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1919, '095', 'a', '3.07.01.01-5', '3.07.01.01-5 Planejamento Integrado dos Recursos Hídricos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1920, '095', 'a', 'planejamento', '3.07.01.01-5 Planejamento Integrado dos Recursos Hídricos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1921, '095', 'a', 'integrado', '3.07.01.01-5 Planejamento Integrado dos Recursos Hídricos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1922, '095', 'a', 'dos', '3.07.01.01-5 Planejamento Integrado dos Recursos Hídricos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1923, '095', 'a', 'recursos', '3.07.01.01-5 Planejamento Integrado dos Recursos Hídricos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1924, '095', 'a', 'hidricos', '3.07.01.01-5 Planejamento Integrado dos Recursos Hídricos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1925, '095', 'a', '3.07.01.02-3', '3.07.01.02-3 Tecnologia e Problemas Sanitários de Irrigação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1926, '095', 'a', 'tecnologia', '3.07.01.02-3 Tecnologia e Problemas Sanitários de Irrigação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1927, '095', 'a', 'problemas', '3.07.01.02-3 Tecnologia e Problemas Sanitários de Irrigação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1928, '095', 'a', 'sanitarios', '3.07.01.02-3 Tecnologia e Problemas Sanitários de Irrigação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1929, '095', 'a', 'de', '3.07.01.02-3 Tecnologia e Problemas Sanitários de Irrigação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1930, '095', 'a', 'irrigacao', '3.07.01.02-3 Tecnologia e Problemas Sanitários de Irrigação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1931, '095', 'a', '3.07.01.03-1', '3.07.01.03-1 Águas Subterrâneas e Poços Profundos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1932, '095', 'a', 'aguas', '3.07.01.03-1 Águas Subterrâneas e Poços Profundos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1933, '095', 'a', 'subterraneas', '3.07.01.03-1 Águas Subterrâneas e Poços Profundos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1934, '095', 'a', 'pocos', '3.07.01.03-1 Águas Subterrâneas e Poços Profundos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1935, '095', 'a', 'profundos', '3.07.01.03-1 Águas Subterrâneas e Poços Profundos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1936, '095', 'a', '3.07.01.04-0', '3.07.01.04-0 Controle de Enchentes e de Barragens', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1937, '095', 'a', 'controle', '3.07.01.04-0 Controle de Enchentes e de Barragens', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1938, '095', 'a', 'de', '3.07.01.04-0 Controle de Enchentes e de Barragens', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1939, '095', 'a', 'enchentes', '3.07.01.04-0 Controle de Enchentes e de Barragens', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1940, '095', 'a', 'de', '3.07.01.04-0 Controle de Enchentes e de Barragens', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1941, '095', 'a', 'barragens', '3.07.01.04-0 Controle de Enchentes e de Barragens', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1942, '095', 'a', '3.07.01.05-8', '3.07.01.05-8 Sedimentologia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1943, '095', 'a', 'sedimentologia', '3.07.01.05-8 Sedimentologia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1944, '095', 'a', '3.07.02.00-3', '3.07.02.00-3 Tratamento de Águas de Abastecimento e Residuárias', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1945, '095', 'a', 'tratamento', '3.07.02.00-3 Tratamento de Águas de Abastecimento e Residuárias', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1946, '095', 'a', 'de', '3.07.02.00-3 Tratamento de Águas de Abastecimento e Residuárias', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1947, '095', 'a', 'aguas', '3.07.02.00-3 Tratamento de Águas de Abastecimento e Residuárias', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1948, '095', 'a', 'de', '3.07.02.00-3 Tratamento de Águas de Abastecimento e Residuárias', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1949, '095', 'a', 'abastecimento', '3.07.02.00-3 Tratamento de Águas de Abastecimento e Residuárias', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1950, '095', 'a', 'residuarias', '3.07.02.00-3 Tratamento de Águas de Abastecimento e Residuárias', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1951, '095', 'a', '3.07.02.01-1', '3.07.02.01-1 Química Sanitária', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1952, '095', 'a', 'quimica', '3.07.02.01-1 Química Sanitária', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1953, '095', 'a', 'sanitaria', '3.07.02.01-1 Química Sanitária', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1954, '095', 'a', '3.07.02.02-0', '3.07.02.02-0 Processos Simplificados de Tratamento de Águas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1955, '095', 'a', 'processos', '3.07.02.02-0 Processos Simplificados de Tratamento de Águas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1956, '095', 'a', 'simplificados', '3.07.02.02-0 Processos Simplificados de Tratamento de Águas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1957, '095', 'a', 'de', '3.07.02.02-0 Processos Simplificados de Tratamento de Águas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1958, '095', 'a', 'tratamento', '3.07.02.02-0 Processos Simplificados de Tratamento de Águas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1959, '095', 'a', 'de', '3.07.02.02-0 Processos Simplificados de Tratamento de Águas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1960, '095', 'a', 'aguas', '3.07.02.02-0 Processos Simplificados de Tratamento de Águas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1961, '095', 'a', '3.07.02.03-8', '3.07.02.03-8 Técnicas Convencionais de Tratamento de Águas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1962, '095', 'a', 'tecnicas', '3.07.02.03-8 Técnicas Convencionais de Tratamento de Águas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1963, '095', 'a', 'convencionais', '3.07.02.03-8 Técnicas Convencionais de Tratamento de Águas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1964, '095', 'a', 'de', '3.07.02.03-8 Técnicas Convencionais de Tratamento de Águas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1965, '095', 'a', 'tratamento', '3.07.02.03-8 Técnicas Convencionais de Tratamento de Águas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1966, '095', 'a', 'de', '3.07.02.03-8 Técnicas Convencionais de Tratamento de Águas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1967, '095', 'a', 'aguas', '3.07.02.03-8 Técnicas Convencionais de Tratamento de Águas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1968, '095', 'a', '3.07.02.04-6', '3.07.02.04-6 Técnicas Avancadas de Tratamento de Águas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1969, '095', 'a', 'tecnicas', '3.07.02.04-6 Técnicas Avancadas de Tratamento de Águas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1970, '095', 'a', 'avancadas', '3.07.02.04-6 Técnicas Avancadas de Tratamento de Águas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1971, '095', 'a', 'de', '3.07.02.04-6 Técnicas Avancadas de Tratamento de Águas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1972, '095', 'a', 'tratamento', '3.07.02.04-6 Técnicas Avancadas de Tratamento de Águas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1973, '095', 'a', 'de', '3.07.02.04-6 Técnicas Avancadas de Tratamento de Águas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1974, '095', 'a', 'aguas', '3.07.02.04-6 Técnicas Avancadas de Tratamento de Águas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1975, '095', 'a', '3.07.02.05-4', '3.07.02.05-4 Estudos e Caracterização de Efluentes Industriais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1976, '095', 'a', 'estudos', '3.07.02.05-4 Estudos e Caracterização de Efluentes Industriais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1977, '095', 'a', 'caracterizacao', '3.07.02.05-4 Estudos e Caracterização de Efluentes Industriais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1978, '095', 'a', 'de', '3.07.02.05-4 Estudos e Caracterização de Efluentes Industriais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1979, '095', 'a', 'efluentes', '3.07.02.05-4 Estudos e Caracterização de Efluentes Industriais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1980, '095', 'a', 'industriais', '3.07.02.05-4 Estudos e Caracterização de Efluentes Industriais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1981, '095', 'a', '3.07.02.06-2', '3.07.02.06-2 Lay Out de Processos Industriais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1982, '095', 'a', 'lay', '3.07.02.06-2 Lay Out de Processos Industriais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1983, '095', 'a', 'out', '3.07.02.06-2 Lay Out de Processos Industriais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1984, '095', 'a', 'de', '3.07.02.06-2 Lay Out de Processos Industriais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1985, '095', 'a', 'processos', '3.07.02.06-2 Lay Out de Processos Industriais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1986, '095', 'a', 'industriais', '3.07.02.06-2 Lay Out de Processos Industriais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1987, '095', 'a', '3.07.02.07-0', '3.07.02.07-0 Resíduos Radioativos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1988, '095', 'a', 'residuos', '3.07.02.07-0 Resíduos Radioativos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1989, '095', 'a', 'radioativos', '3.07.02.07-0 Resíduos Radioativos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1990, '095', 'a', '3.07.03.00-0', '3.07.03.00-0 Saneamento Básico', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1991, '095', 'a', 'saneamento', '3.07.03.00-0 Saneamento Básico', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1992, '095', 'a', 'basico', '3.07.03.00-0 Saneamento Básico', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1993, '095', 'a', '3.07.03.01-8', '3.07.03.01-8 Técnicas de Abastecimento da Água', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1994, '095', 'a', 'tecnicas', '3.07.03.01-8 Técnicas de Abastecimento da Água', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1995, '095', 'a', 'de', '3.07.03.01-8 Técnicas de Abastecimento da Água', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1996, '095', 'a', 'abastecimento', '3.07.03.01-8 Técnicas de Abastecimento da Água', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1997, '095', 'a', 'da', '3.07.03.01-8 Técnicas de Abastecimento da Água', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1998, '095', 'a', 'agua', '3.07.03.01-8 Técnicas de Abastecimento da Água', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (1999, '095', 'a', '3.07.03.02-6', '3.07.03.02-6 Drenagem de Águas Residuárias', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2000, '095', 'a', 'drenagem', '3.07.03.02-6 Drenagem de Águas Residuárias', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2001, '095', 'a', 'de', '3.07.03.02-6 Drenagem de Águas Residuárias', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2002, '095', 'a', 'aguas', '3.07.03.02-6 Drenagem de Águas Residuárias', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2003, '095', 'a', 'residuarias', '3.07.03.02-6 Drenagem de Águas Residuárias', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2004, '095', 'a', '3.07.03.03-4', '3.07.03.03-4 Drenagem Urbana de Águas Pluviais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2005, '095', 'a', 'drenagem', '3.07.03.03-4 Drenagem Urbana de Águas Pluviais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2006, '095', 'a', 'urbana', '3.07.03.03-4 Drenagem Urbana de Águas Pluviais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2007, '095', 'a', 'de', '3.07.03.03-4 Drenagem Urbana de Águas Pluviais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2008, '095', 'a', 'aguas', '3.07.03.03-4 Drenagem Urbana de Águas Pluviais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2009, '095', 'a', 'pluviais', '3.07.03.03-4 Drenagem Urbana de Águas Pluviais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2010, '095', 'a', '3.07.03.04-2', '3.07.03.04-2 Resíduos Sólidos, Domésticos e Industriais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2011, '095', 'a', 'residuos', '3.07.03.04-2 Resíduos Sólidos, Domésticos e Industriais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2012, '095', 'a', 'solidos,', '3.07.03.04-2 Resíduos Sólidos, Domésticos e Industriais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2013, '095', 'a', 'domesticos', '3.07.03.04-2 Resíduos Sólidos, Domésticos e Industriais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2014, '095', 'a', 'industriais', '3.07.03.04-2 Resíduos Sólidos, Domésticos e Industriais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2015, '095', 'a', '3.07.03.05-0', '3.07.03.05-0 Limpeza Pública', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2016, '095', 'a', 'limpeza', '3.07.03.05-0 Limpeza Pública', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2017, '095', 'a', 'publica', '3.07.03.05-0 Limpeza Pública', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2018, '095', 'a', '3.07.03.06-9', '3.07.03.06-9 Instalações Hidráulico-Sanitárias', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2019, '095', 'a', 'instalacoes', '3.07.03.06-9 Instalações Hidráulico-Sanitárias', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2020, '095', 'a', 'hidraulico-sanitarias', '3.07.03.06-9 Instalações Hidráulico-Sanitárias', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2021, '095', 'a', '3.07.04.00-6', '3.07.04.00-6 Saneamento Ambiental', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2022, '095', 'a', 'saneamento', '3.07.04.00-6 Saneamento Ambiental', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2023, '095', 'a', 'ambiental', '3.07.04.00-6 Saneamento Ambiental', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2024, '095', 'a', '3.07.04.01-4', '3.07.04.01-4 Ecologia Aplicada à Engenharia Sanitária', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2025, '095', 'a', 'ecologia', '3.07.04.01-4 Ecologia Aplicada à Engenharia Sanitária', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2026, '095', 'a', 'aplicada', '3.07.04.01-4 Ecologia Aplicada à Engenharia Sanitária', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2027, '095', 'a', 'engenharia', '3.07.04.01-4 Ecologia Aplicada à Engenharia Sanitária', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2028, '095', 'a', 'sanitaria', '3.07.04.01-4 Ecologia Aplicada à Engenharia Sanitária', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2029, '095', 'a', '3.07.04.02-2', '3.07.04.02-2 Microbiologia Aplicada e Engenharia Sanitária', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2030, '095', 'a', 'microbiologia', '3.07.04.02-2 Microbiologia Aplicada e Engenharia Sanitária', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2031, '095', 'a', 'aplicada', '3.07.04.02-2 Microbiologia Aplicada e Engenharia Sanitária', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2032, '095', 'a', 'engenharia', '3.07.04.02-2 Microbiologia Aplicada e Engenharia Sanitária', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2033, '095', 'a', 'sanitaria', '3.07.04.02-2 Microbiologia Aplicada e Engenharia Sanitária', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2034, '095', 'a', '3.07.04.03-0', '3.07.04.03-0 Parasitologia Aplicada à Engenharia Sanitária', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2035, '095', 'a', 'parasitologia', '3.07.04.03-0 Parasitologia Aplicada à Engenharia Sanitária', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2036, '095', 'a', 'aplicada', '3.07.04.03-0 Parasitologia Aplicada à Engenharia Sanitária', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2037, '095', 'a', 'engenharia', '3.07.04.03-0 Parasitologia Aplicada à Engenharia Sanitária', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2038, '095', 'a', 'sanitaria', '3.07.04.03-0 Parasitologia Aplicada à Engenharia Sanitária', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2039, '095', 'a', '3.07.04.04-9', '3.07.04.04-9 Qualidade do Ar, das Águas e do Solo', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2040, '095', 'a', 'qualidade', '3.07.04.04-9 Qualidade do Ar, das Águas e do Solo', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2041, '095', 'a', 'do', '3.07.04.04-9 Qualidade do Ar, das Águas e do Solo', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2042, '095', 'a', 'ar,', '3.07.04.04-9 Qualidade do Ar, das Águas e do Solo', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2043, '095', 'a', 'das', '3.07.04.04-9 Qualidade do Ar, das Águas e do Solo', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2044, '095', 'a', 'aguas', '3.07.04.04-9 Qualidade do Ar, das Águas e do Solo', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2045, '095', 'a', 'do', '3.07.04.04-9 Qualidade do Ar, das Águas e do Solo', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2046, '095', 'a', 'solo', '3.07.04.04-9 Qualidade do Ar, das Águas e do Solo', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2047, '095', 'a', '3.07.04.05-7', '3.07.04.05-7 Controle da Poluição', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2048, '095', 'a', 'controle', '3.07.04.05-7 Controle da Poluição', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2049, '095', 'a', 'da', '3.07.04.05-7 Controle da Poluição', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2050, '095', 'a', 'poluicao', '3.07.04.05-7 Controle da Poluição', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2051, '095', 'a', '3.07.04.06-5', '3.07.04.06-5 Legislação Ambiental', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2052, '095', 'a', 'legislacao', '3.07.04.06-5 Legislação Ambiental', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2053, '095', 'a', 'ambiental', '3.07.04.06-5 Legislação Ambiental', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2054, '095', 'a', '3.08.00.00-5', '3.08.00.00-5 Engenharia de Produção', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2055, '095', 'a', 'engenharia', '3.08.00.00-5 Engenharia de Produção', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2056, '095', 'a', 'de', '3.08.00.00-5 Engenharia de Produção', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2057, '095', 'a', 'producao', '3.08.00.00-5 Engenharia de Produção', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2058, '095', 'a', '3.08.01.00-1', '3.08.01.00-1 Gerência de Produção', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2059, '095', 'a', 'gerencia', '3.08.01.00-1 Gerência de Produção', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2060, '095', 'a', 'de', '3.08.01.00-1 Gerência de Produção', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2061, '095', 'a', 'producao', '3.08.01.00-1 Gerência de Produção', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2062, '095', 'a', '3.08.01.01-0', '3.08.01.01-0 Planejamento de Instalações Industriais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2063, '095', 'a', 'planejamento', '3.08.01.01-0 Planejamento de Instalações Industriais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2064, '095', 'a', 'de', '3.08.01.01-0 Planejamento de Instalações Industriais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2065, '095', 'a', 'instalacoes', '3.08.01.01-0 Planejamento de Instalações Industriais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2066, '095', 'a', 'industriais', '3.08.01.01-0 Planejamento de Instalações Industriais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2067, '095', 'a', '3.08.01.02-8', '3.08.01.02-8 Planejamento, Projeto e Controle de Sistemas de Produção', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2068, '095', 'a', 'planejamento,', '3.08.01.02-8 Planejamento, Projeto e Controle de Sistemas de Produção', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2069, '095', 'a', 'projeto', '3.08.01.02-8 Planejamento, Projeto e Controle de Sistemas de Produção', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2070, '095', 'a', 'controle', '3.08.01.02-8 Planejamento, Projeto e Controle de Sistemas de Produção', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2071, '095', 'a', 'de', '3.08.01.02-8 Planejamento, Projeto e Controle de Sistemas de Produção', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2072, '095', 'a', 'sistemas', '3.08.01.02-8 Planejamento, Projeto e Controle de Sistemas de Produção', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2073, '095', 'a', 'de', '3.08.01.02-8 Planejamento, Projeto e Controle de Sistemas de Produção', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2074, '095', 'a', 'producao', '3.08.01.02-8 Planejamento, Projeto e Controle de Sistemas de Produção', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2075, '095', 'a', '3.08.01.03-6', '3.08.01.03-6 Higiene e Segurança do Trabalho', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2076, '095', 'a', 'higiene', '3.08.01.03-6 Higiene e Segurança do Trabalho', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2077, '095', 'a', 'seguranca', '3.08.01.03-6 Higiene e Segurança do Trabalho', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2078, '095', 'a', 'do', '3.08.01.03-6 Higiene e Segurança do Trabalho', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2079, '095', 'a', 'trabalho', '3.08.01.03-6 Higiene e Segurança do Trabalho', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2080, '095', 'a', '3.08.01.04-4', '3.08.01.04-4 Suprimentos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2081, '095', 'a', 'suprimentos', '3.08.01.04-4 Suprimentos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2082, '095', 'a', '3.08.01.05-2', '3.08.01.05-2 Garantia de Controle de Qualidade', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2083, '095', 'a', 'garantia', '3.08.01.05-2 Garantia de Controle de Qualidade', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2084, '095', 'a', 'de', '3.08.01.05-2 Garantia de Controle de Qualidade', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2085, '095', 'a', 'controle', '3.08.01.05-2 Garantia de Controle de Qualidade', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2086, '095', 'a', 'de', '3.08.01.05-2 Garantia de Controle de Qualidade', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2087, '095', 'a', 'qualidade', '3.08.01.05-2 Garantia de Controle de Qualidade', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2088, '095', 'a', '3.08.02.00-8', '3.08.02.00-8 Pesquisa Operacional', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2089, '095', 'a', 'pesquisa', '3.08.02.00-8 Pesquisa Operacional', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2090, '095', 'a', 'operacional', '3.08.02.00-8 Pesquisa Operacional', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2091, '095', 'a', '3.08.02.01-6', '3.08.02.01-6 Processos Estocásticos e Teorias da Filas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2092, '095', 'a', 'processos', '3.08.02.01-6 Processos Estocásticos e Teorias da Filas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2093, '095', 'a', 'estocasticos', '3.08.02.01-6 Processos Estocásticos e Teorias da Filas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2094, '095', 'a', 'teorias', '3.08.02.01-6 Processos Estocásticos e Teorias da Filas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2095, '095', 'a', 'da', '3.08.02.01-6 Processos Estocásticos e Teorias da Filas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2096, '095', 'a', 'filas', '3.08.02.01-6 Processos Estocásticos e Teorias da Filas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2097, '095', 'a', '3.08.02.02-4', '3.08.02.02-4 Programação Linear, Não-Linear, Mista e Dinâmica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2098, '095', 'a', 'programacao', '3.08.02.02-4 Programação Linear, Não-Linear, Mista e Dinâmica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2099, '095', 'a', 'linear,', '3.08.02.02-4 Programação Linear, Não-Linear, Mista e Dinâmica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2100, '095', 'a', 'nao-linear,', '3.08.02.02-4 Programação Linear, Não-Linear, Mista e Dinâmica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2101, '095', 'a', 'mista', '3.08.02.02-4 Programação Linear, Não-Linear, Mista e Dinâmica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2102, '095', 'a', 'dinamica', '3.08.02.02-4 Programação Linear, Não-Linear, Mista e Dinâmica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2103, '095', 'a', '3.08.02.03-2', '3.08.02.03-2 Séries Temporais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2104, '095', 'a', 'series', '3.08.02.03-2 Séries Temporais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2105, '095', 'a', 'temporais', '3.08.02.03-2 Séries Temporais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2106, '095', 'a', '3.08.02.04-0', '3.08.02.04-0 Teoria dos Grafos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2107, '095', 'a', 'teoria', '3.08.02.04-0 Teoria dos Grafos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2108, '095', 'a', 'dos', '3.08.02.04-0 Teoria dos Grafos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2109, '095', 'a', 'grafos', '3.08.02.04-0 Teoria dos Grafos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2110, '095', 'a', '3.08.02.05-9', '3.08.02.05-9 Teoria dos Jogos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2111, '095', 'a', 'teoria', '3.08.02.05-9 Teoria dos Jogos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2112, '095', 'a', 'dos', '3.08.02.05-9 Teoria dos Jogos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2113, '095', 'a', 'jogos', '3.08.02.05-9 Teoria dos Jogos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2114, '095', 'a', '3.08.03.00-4', '3.08.03.00-4 Engenharia do Produto', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2115, '095', 'a', 'engenharia', '3.08.03.00-4 Engenharia do Produto', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2116, '095', 'a', 'do', '3.08.03.00-4 Engenharia do Produto', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2117, '095', 'a', 'produto', '3.08.03.00-4 Engenharia do Produto', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2118, '095', 'a', '3.08.03.01-2', '3.08.03.01-2 Ergonomia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2119, '095', 'a', 'ergonomia', '3.08.03.01-2 Ergonomia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2120, '095', 'a', '3.08.03.02-0', '3.08.03.02-0 Metodologia de Projeto do Produto', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2121, '095', 'a', 'metodologia', '3.08.03.02-0 Metodologia de Projeto do Produto', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2122, '095', 'a', 'de', '3.08.03.02-0 Metodologia de Projeto do Produto', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2123, '095', 'a', 'projeto', '3.08.03.02-0 Metodologia de Projeto do Produto', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2124, '095', 'a', 'do', '3.08.03.02-0 Metodologia de Projeto do Produto', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2125, '095', 'a', 'produto', '3.08.03.02-0 Metodologia de Projeto do Produto', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2126, '095', 'a', '3.08.03.03-9', '3.08.03.03-9 Processos de Trabalho', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2127, '095', 'a', 'processos', '3.08.03.03-9 Processos de Trabalho', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2128, '095', 'a', 'de', '3.08.03.03-9 Processos de Trabalho', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2129, '095', 'a', 'trabalho', '3.08.03.03-9 Processos de Trabalho', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2130, '095', 'a', '3.08.03.04-7', '3.08.03.04-7 Gerência do Projeto e do Produto', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2131, '095', 'a', 'gerencia', '3.08.03.04-7 Gerência do Projeto e do Produto', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2132, '095', 'a', 'do', '3.08.03.04-7 Gerência do Projeto e do Produto', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2133, '095', 'a', 'projeto', '3.08.03.04-7 Gerência do Projeto e do Produto', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2134, '095', 'a', 'do', '3.08.03.04-7 Gerência do Projeto e do Produto', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2135, '095', 'a', 'produto', '3.08.03.04-7 Gerência do Projeto e do Produto', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2136, '095', 'a', '3.08.03.05-5', '3.08.03.05-5 Desenvolvimento de Produto', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2137, '095', 'a', 'desenvolvimento', '3.08.03.05-5 Desenvolvimento de Produto', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2138, '095', 'a', 'de', '3.08.03.05-5 Desenvolvimento de Produto', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2139, '095', 'a', 'produto', '3.08.03.05-5 Desenvolvimento de Produto', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2140, '095', 'a', '3.08.04.00-0', '3.08.04.00-0 Engenharia Econômica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2141, '095', 'a', 'engenharia', '3.08.04.00-0 Engenharia Econômica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2142, '095', 'a', 'economica', '3.08.04.00-0 Engenharia Econômica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2143, '095', 'a', '3.08.04.01-9', '3.08.04.01-9 Estudo de Mercado', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2144, '095', 'a', 'estudo', '3.08.04.01-9 Estudo de Mercado', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2145, '095', 'a', 'de', '3.08.04.01-9 Estudo de Mercado', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2146, '095', 'a', 'mercado', '3.08.04.01-9 Estudo de Mercado', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2147, '095', 'a', '3.08.04.02-7', '3.08.04.02-7 Localização Industrial', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2148, '095', 'a', 'localizacao', '3.08.04.02-7 Localização Industrial', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2149, '095', 'a', 'industrial', '3.08.04.02-7 Localização Industrial', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2150, '095', 'a', '3.08.04.03-5', '3.08.04.03-5 Análise de Custos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2151, '095', 'a', 'analise', '3.08.04.03-5 Análise de Custos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2152, '095', 'a', 'de', '3.08.04.03-5 Análise de Custos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2153, '095', 'a', 'custos', '3.08.04.03-5 Análise de Custos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2154, '095', 'a', '3.08.04.04-3', '3.08.04.04-3 Economia de Tecnologia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2155, '095', 'a', 'economia', '3.08.04.04-3 Economia de Tecnologia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2156, '095', 'a', 'de', '3.08.04.04-3 Economia de Tecnologia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2157, '095', 'a', 'tecnologia', '3.08.04.04-3 Economia de Tecnologia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2158, '095', 'a', '3.08.04.05-1', '3.08.04.05-1 Vida Econômica dos Equipamentos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2159, '095', 'a', 'vida', '3.08.04.05-1 Vida Econômica dos Equipamentos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2160, '095', 'a', 'economica', '3.08.04.05-1 Vida Econômica dos Equipamentos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2161, '095', 'a', 'dos', '3.08.04.05-1 Vida Econômica dos Equipamentos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2162, '095', 'a', 'equipamentos', '3.08.04.05-1 Vida Econômica dos Equipamentos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2163, '095', 'a', '3.08.04.06-0', '3.08.04.06-0 Avaliação de Projetos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2164, '095', 'a', 'avaliacao', '3.08.04.06-0 Avaliação de Projetos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2165, '095', 'a', 'de', '3.08.04.06-0 Avaliação de Projetos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2166, '095', 'a', 'projetos', '3.08.04.06-0 Avaliação de Projetos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2167, '095', 'a', '3.09.00.00-0', '3.09.00.00-0 Engenharia Nuclear', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2168, '095', 'a', 'engenharia', '3.09.00.00-0 Engenharia Nuclear', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2169, '095', 'a', 'nuclear', '3.09.00.00-0 Engenharia Nuclear', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2170, '095', 'a', '3.09.01.00-6', '3.09.01.00-6 Aplicações de Radioisotopos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2171, '095', 'a', 'aplicacoes', '3.09.01.00-6 Aplicações de Radioisotopos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2172, '095', 'a', 'de', '3.09.01.00-6 Aplicações de Radioisotopos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2173, '095', 'a', 'radioisotopos', '3.09.01.00-6 Aplicações de Radioisotopos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2174, '095', 'a', '3.09.01.01-4', '3.09.01.01-4 Produção de Radioisotopos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2175, '095', 'a', 'producao', '3.09.01.01-4 Produção de Radioisotopos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2176, '095', 'a', 'de', '3.09.01.01-4 Produção de Radioisotopos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2177, '095', 'a', 'radioisotopos', '3.09.01.01-4 Produção de Radioisotopos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2178, '095', 'a', '3.09.01.02-2', '3.09.01.02-2 Aplicações Industriais de Radioisotopos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2179, '095', 'a', 'aplicacoes', '3.09.01.02-2 Aplicações Industriais de Radioisotopos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2180, '095', 'a', 'industriais', '3.09.01.02-2 Aplicações Industriais de Radioisotopos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2181, '095', 'a', 'de', '3.09.01.02-2 Aplicações Industriais de Radioisotopos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2182, '095', 'a', 'radioisotopos', '3.09.01.02-2 Aplicações Industriais de Radioisotopos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2183, '095', 'a', '3.09.01.03-0', '3.09.01.03-0 Instrumentação para Medida e Controle de Radiação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2184, '095', 'a', 'instrumentacao', '3.09.01.03-0 Instrumentação para Medida e Controle de Radiação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2185, '095', 'a', 'para', '3.09.01.03-0 Instrumentação para Medida e Controle de Radiação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2186, '095', 'a', 'medida', '3.09.01.03-0 Instrumentação para Medida e Controle de Radiação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2187, '095', 'a', 'controle', '3.09.01.03-0 Instrumentação para Medida e Controle de Radiação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2188, '095', 'a', 'de', '3.09.01.03-0 Instrumentação para Medida e Controle de Radiação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2189, '095', 'a', 'radiacao', '3.09.01.03-0 Instrumentação para Medida e Controle de Radiação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2190, '095', 'a', '3.09.02.00-2', '3.09.02.00-2 Fusão Controlada', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2191, '095', 'a', 'fusao', '3.09.02.00-2 Fusão Controlada', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2192, '095', 'a', 'controlada', '3.09.02.00-2 Fusão Controlada', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2193, '095', 'a', '3.09.02.01-0', '3.09.02.01-0 Processos Industriais da Fusão Controlada', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2194, '095', 'a', 'processos', '3.09.02.01-0 Processos Industriais da Fusão Controlada', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2195, '095', 'a', 'industriais', '3.09.02.01-0 Processos Industriais da Fusão Controlada', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2196, '095', 'a', 'da', '3.09.02.01-0 Processos Industriais da Fusão Controlada', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2197, '095', 'a', 'fusao', '3.09.02.01-0 Processos Industriais da Fusão Controlada', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2198, '095', 'a', 'controlada', '3.09.02.01-0 Processos Industriais da Fusão Controlada', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2199, '095', 'a', '3.09.02.02-9', '3.09.02.02-9 Problemas Tecnológicos da Fusão Controlada', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2200, '095', 'a', 'problemas', '3.09.02.02-9 Problemas Tecnológicos da Fusão Controlada', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2201, '095', 'a', 'tecnologicos', '3.09.02.02-9 Problemas Tecnológicos da Fusão Controlada', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2202, '095', 'a', 'da', '3.09.02.02-9 Problemas Tecnológicos da Fusão Controlada', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2203, '095', 'a', 'fusao', '3.09.02.02-9 Problemas Tecnológicos da Fusão Controlada', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2204, '095', 'a', 'controlada', '3.09.02.02-9 Problemas Tecnológicos da Fusão Controlada', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2205, '095', 'a', '3.09.03.00-9', '3.09.03.00-9 Combustível Nuclear', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2206, '095', 'a', 'combustivel', '3.09.03.00-9 Combustível Nuclear', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2207, '095', 'a', 'nuclear', '3.09.03.00-9 Combustível Nuclear', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2208, '095', 'a', '3.09.03.01-7', '3.09.03.01-7 Extração de Combustível Nuclear', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2209, '095', 'a', 'extracao', '3.09.03.01-7 Extração de Combustível Nuclear', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2210, '095', 'a', 'de', '3.09.03.01-7 Extração de Combustível Nuclear', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2211, '095', 'a', 'combustivel', '3.09.03.01-7 Extração de Combustível Nuclear', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2212, '095', 'a', 'nuclear', '3.09.03.01-7 Extração de Combustível Nuclear', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2213, '095', 'a', '3.09.03.02-5', '3.09.03.02-5 Conversão, Enriquecimento e Fabricação de Combustível Nuclear', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2214, '095', 'a', 'conversao,', '3.09.03.02-5 Conversão, Enriquecimento e Fabricação de Combustível Nuclear', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2215, '095', 'a', 'enriquecimento', '3.09.03.02-5 Conversão, Enriquecimento e Fabricação de Combustível Nuclear', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2216, '095', 'a', 'fabricacao', '3.09.03.02-5 Conversão, Enriquecimento e Fabricação de Combustível Nuclear', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2217, '095', 'a', 'de', '3.09.03.02-5 Conversão, Enriquecimento e Fabricação de Combustível Nuclear', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2218, '095', 'a', 'combustivel', '3.09.03.02-5 Conversão, Enriquecimento e Fabricação de Combustível Nuclear', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2219, '095', 'a', 'nuclear', '3.09.03.02-5 Conversão, Enriquecimento e Fabricação de Combustível Nuclear', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2220, '095', 'a', '3.09.03.03-3', '3.09.03.03-3 Reprocessamento de Combustível Nuclear', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2221, '095', 'a', 'reprocessamento', '3.09.03.03-3 Reprocessamento de Combustível Nuclear', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2222, '095', 'a', 'de', '3.09.03.03-3 Reprocessamento de Combustível Nuclear', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2223, '095', 'a', 'combustivel', '3.09.03.03-3 Reprocessamento de Combustível Nuclear', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2224, '095', 'a', 'nuclear', '3.09.03.03-3 Reprocessamento de Combustível Nuclear', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2225, '095', 'a', '3.09.03.04-1', '3.09.03.04-1 Rejeitos de Combustível Nuclear', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2226, '095', 'a', 'rejeitos', '3.09.03.04-1 Rejeitos de Combustível Nuclear', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2227, '095', 'a', 'de', '3.09.03.04-1 Rejeitos de Combustível Nuclear', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2228, '095', 'a', 'combustivel', '3.09.03.04-1 Rejeitos de Combustível Nuclear', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2229, '095', 'a', 'nuclear', '3.09.03.04-1 Rejeitos de Combustível Nuclear', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2230, '095', 'a', '3.09.04.00-5', '3.09.04.00-5 Tecnologia dos Reatores', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2231, '095', 'a', 'tecnologia', '3.09.04.00-5 Tecnologia dos Reatores', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2232, '095', 'a', 'dos', '3.09.04.00-5 Tecnologia dos Reatores', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2233, '095', 'a', 'reatores', '3.09.04.00-5 Tecnologia dos Reatores', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2234, '095', 'a', '3.09.04.01-3', '3.09.04.01-3 Núcleo do Reator', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2235, '095', 'a', 'nucleo', '3.09.04.01-3 Núcleo do Reator', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2236, '095', 'a', 'do', '3.09.04.01-3 Núcleo do Reator', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2237, '095', 'a', 'reator', '3.09.04.01-3 Núcleo do Reator', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2238, '095', 'a', '3.09.04.02-1', '3.09.04.02-1 Materiais Nucleares e Blindagem de Reatores', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2239, '095', 'a', 'materiais', '3.09.04.02-1 Materiais Nucleares e Blindagem de Reatores', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2240, '095', 'a', 'nucleares', '3.09.04.02-1 Materiais Nucleares e Blindagem de Reatores', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2241, '095', 'a', 'blindagem', '3.09.04.02-1 Materiais Nucleares e Blindagem de Reatores', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2242, '095', 'a', 'de', '3.09.04.02-1 Materiais Nucleares e Blindagem de Reatores', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2243, '095', 'a', 'reatores', '3.09.04.02-1 Materiais Nucleares e Blindagem de Reatores', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2244, '095', 'a', '3.09.04.03-0', '3.09.04.03-0 Transferência de Calor em Reatores', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2245, '095', 'a', 'transferencia', '3.09.04.03-0 Transferência de Calor em Reatores', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2246, '095', 'a', 'de', '3.09.04.03-0 Transferência de Calor em Reatores', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2247, '095', 'a', 'calor', '3.09.04.03-0 Transferência de Calor em Reatores', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2248, '095', 'a', 'em', '3.09.04.03-0 Transferência de Calor em Reatores', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2249, '095', 'a', 'reatores', '3.09.04.03-0 Transferência de Calor em Reatores', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2250, '095', 'a', '3.09.04.04-8', '3.09.04.04-8 Geração e Integração Com Sistemas Elétricos em Reatores', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2251, '095', 'a', 'geracao', '3.09.04.04-8 Geração e Integração Com Sistemas Elétricos em Reatores', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2252, '095', 'a', 'integracao', '3.09.04.04-8 Geração e Integração Com Sistemas Elétricos em Reatores', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2253, '095', 'a', 'com', '3.09.04.04-8 Geração e Integração Com Sistemas Elétricos em Reatores', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2254, '095', 'a', 'sistemas', '3.09.04.04-8 Geração e Integração Com Sistemas Elétricos em Reatores', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2255, '095', 'a', 'eletricos', '3.09.04.04-8 Geração e Integração Com Sistemas Elétricos em Reatores', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2256, '095', 'a', 'em', '3.09.04.04-8 Geração e Integração Com Sistemas Elétricos em Reatores', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2257, '095', 'a', 'reatores', '3.09.04.04-8 Geração e Integração Com Sistemas Elétricos em Reatores', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2258, '095', 'a', '3.09.04.05-6', '3.09.04.05-6 Instrumentação Para Operação e Controle de Reatores', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2259, '095', 'a', 'instrumentacao', '3.09.04.05-6 Instrumentação Para Operação e Controle de Reatores', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2260, '095', 'a', 'para', '3.09.04.05-6 Instrumentação Para Operação e Controle de Reatores', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2261, '095', 'a', 'operacao', '3.09.04.05-6 Instrumentação Para Operação e Controle de Reatores', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2262, '095', 'a', 'controle', '3.09.04.05-6 Instrumentação Para Operação e Controle de Reatores', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2263, '095', 'a', 'de', '3.09.04.05-6 Instrumentação Para Operação e Controle de Reatores', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2264, '095', 'a', 'reatores', '3.09.04.05-6 Instrumentação Para Operação e Controle de Reatores', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2265, '095', 'a', '3.09.04.06-4', '3.09.04.06-4 Seguranca, Localização e Licênciamento de Reatores', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2266, '095', 'a', 'seguranca,', '3.09.04.06-4 Seguranca, Localização e Licênciamento de Reatores', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2267, '095', 'a', 'localizacao', '3.09.04.06-4 Seguranca, Localização e Licênciamento de Reatores', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2268, '095', 'a', 'licenciamento', '3.09.04.06-4 Seguranca, Localização e Licênciamento de Reatores', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2269, '095', 'a', 'de', '3.09.04.06-4 Seguranca, Localização e Licênciamento de Reatores', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2270, '095', 'a', 'reatores', '3.09.04.06-4 Seguranca, Localização e Licênciamento de Reatores', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2271, '095', 'a', '3.09.04.07-2', '3.09.04.07-2 Aspectos Econômicos de Reatores', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2272, '095', 'a', 'aspectos', '3.09.04.07-2 Aspectos Econômicos de Reatores', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2273, '095', 'a', 'economicos', '3.09.04.07-2 Aspectos Econômicos de Reatores', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2274, '095', 'a', 'de', '3.09.04.07-2 Aspectos Econômicos de Reatores', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2275, '095', 'a', 'reatores', '3.09.04.07-2 Aspectos Econômicos de Reatores', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2276, '095', 'a', '3.10.00.00-2', '3.10.00.00-2 Engenharia de Transportes', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2277, '095', 'a', 'engenharia', '3.10.00.00-2 Engenharia de Transportes', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2278, '095', 'a', 'de', '3.10.00.00-2 Engenharia de Transportes', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2279, '095', 'a', 'transportes', '3.10.00.00-2 Engenharia de Transportes', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2280, '095', 'a', '3.10.01.00-9', '3.10.01.00-9 Planejamento de Transportes', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2281, '095', 'a', 'planejamento', '3.10.01.00-9 Planejamento de Transportes', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2282, '095', 'a', 'de', '3.10.01.00-9 Planejamento de Transportes', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2283, '095', 'a', 'transportes', '3.10.01.00-9 Planejamento de Transportes', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2284, '095', 'a', '3.10.01.01-7', '3.10.01.01-7 Planejamento e Organização do Sistema de Transporte', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2285, '095', 'a', 'planejamento', '3.10.01.01-7 Planejamento e Organização do Sistema de Transporte', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2286, '095', 'a', 'organizacao', '3.10.01.01-7 Planejamento e Organização do Sistema de Transporte', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2287, '095', 'a', 'do', '3.10.01.01-7 Planejamento e Organização do Sistema de Transporte', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2288, '095', 'a', 'sistema', '3.10.01.01-7 Planejamento e Organização do Sistema de Transporte', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2289, '095', 'a', 'de', '3.10.01.01-7 Planejamento e Organização do Sistema de Transporte', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2290, '095', 'a', 'transporte', '3.10.01.01-7 Planejamento e Organização do Sistema de Transporte', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2291, '095', 'a', '3.10.01.02-5', '3.10.01.02-5 Economia dos Transportes', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2292, '095', 'a', 'economia', '3.10.01.02-5 Economia dos Transportes', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2293, '095', 'a', 'dos', '3.10.01.02-5 Economia dos Transportes', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2294, '095', 'a', 'transportes', '3.10.01.02-5 Economia dos Transportes', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2295, '095', 'a', '3.10.02.00-5', '3.10.02.00-5 Veículos e Equipamentos de Controle', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2296, '095', 'a', 'veiculos', '3.10.02.00-5 Veículos e Equipamentos de Controle', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2297, '095', 'a', 'equipamentos', '3.10.02.00-5 Veículos e Equipamentos de Controle', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2298, '095', 'a', 'de', '3.10.02.00-5 Veículos e Equipamentos de Controle', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2299, '095', 'a', 'controle', '3.10.02.00-5 Veículos e Equipamentos de Controle', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2300, '095', 'a', '3.10.02.01-3', '3.10.02.01-3 Vias de Transporte', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2301, '095', 'a', 'vias', '3.10.02.01-3 Vias de Transporte', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2302, '095', 'a', 'de', '3.10.02.01-3 Vias de Transporte', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2303, '095', 'a', 'transporte', '3.10.02.01-3 Vias de Transporte', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2304, '095', 'a', '3.10.02.02-1', '3.10.02.02-1 Veículos de Transportes', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2305, '095', 'a', 'veiculos', '3.10.02.02-1 Veículos de Transportes', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2306, '095', 'a', 'de', '3.10.02.02-1 Veículos de Transportes', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2307, '095', 'a', 'transportes', '3.10.02.02-1 Veículos de Transportes', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2308, '095', 'a', '3.10.02.03-0', '3.10.02.03-0 Estação de Transporte', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2309, '095', 'a', 'estacao', '3.10.02.03-0 Estação de Transporte', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2310, '095', 'a', 'de', '3.10.02.03-0 Estação de Transporte', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2311, '095', 'a', 'transporte', '3.10.02.03-0 Estação de Transporte', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2312, '095', 'a', '3.10.02.04-8', '3.10.02.04-8 Equipamentos Auxiliares e Controles', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2313, '095', 'a', 'equipamentos', '3.10.02.04-8 Equipamentos Auxiliares e Controles', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2314, '095', 'a', 'auxiliares', '3.10.02.04-8 Equipamentos Auxiliares e Controles', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2315, '095', 'a', 'controles', '3.10.02.04-8 Equipamentos Auxiliares e Controles', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2316, '095', 'a', '3.10.03.00-1', '3.10.03.00-1 Operações de Transportes', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2317, '095', 'a', 'operacoes', '3.10.03.00-1 Operações de Transportes', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2318, '095', 'a', 'de', '3.10.03.00-1 Operações de Transportes', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2319, '095', 'a', 'transportes', '3.10.03.00-1 Operações de Transportes', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2320, '095', 'a', '3.10.03.01-0', '3.10.03.01-0 Engenharia de Tráfego', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2321, '095', 'a', 'engenharia', '3.10.03.01-0 Engenharia de Tráfego', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2322, '095', 'a', 'de', '3.10.03.01-0 Engenharia de Tráfego', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2323, '095', 'a', 'trafego', '3.10.03.01-0 Engenharia de Tráfego', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2324, '095', 'a', '3.10.03.02-8', '3.10.03.02-8 Capacidade de Vias de Transporte', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2325, '095', 'a', 'capacidade', '3.10.03.02-8 Capacidade de Vias de Transporte', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2326, '095', 'a', 'de', '3.10.03.02-8 Capacidade de Vias de Transporte', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2327, '095', 'a', 'vias', '3.10.03.02-8 Capacidade de Vias de Transporte', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2328, '095', 'a', 'de', '3.10.03.02-8 Capacidade de Vias de Transporte', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2329, '095', 'a', 'transporte', '3.10.03.02-8 Capacidade de Vias de Transporte', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2330, '095', 'a', '3.10.03.03-6', '3.10.03.03-6 Operação de Sistemas de Transporte', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2331, '095', 'a', 'operacao', '3.10.03.03-6 Operação de Sistemas de Transporte', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2332, '095', 'a', 'de', '3.10.03.03-6 Operação de Sistemas de Transporte', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2333, '095', 'a', 'sistemas', '3.10.03.03-6 Operação de Sistemas de Transporte', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2334, '095', 'a', 'de', '3.10.03.03-6 Operação de Sistemas de Transporte', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2335, '095', 'a', 'transporte', '3.10.03.03-6 Operação de Sistemas de Transporte', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2336, '095', 'a', '3.11.00.00-7', '3.11.00.00-7 Engenharia Naval e Oceânica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2337, '095', 'a', 'engenharia', '3.11.00.00-7 Engenharia Naval e Oceânica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2338, '095', 'a', 'naval', '3.11.00.00-7 Engenharia Naval e Oceânica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2339, '095', 'a', 'oceanica', '3.11.00.00-7 Engenharia Naval e Oceânica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2340, '095', 'a', '3.11.01.00-3', '3.11.01.00-3 Hidrodinâmica de Navios e Sistemas Oceânicos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2341, '095', 'a', 'hidrodinamica', '3.11.01.00-3 Hidrodinâmica de Navios e Sistemas Oceânicos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2342, '095', 'a', 'de', '3.11.01.00-3 Hidrodinâmica de Navios e Sistemas Oceânicos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2343, '095', 'a', 'navios', '3.11.01.00-3 Hidrodinâmica de Navios e Sistemas Oceânicos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2344, '095', 'a', 'sistemas', '3.11.01.00-3 Hidrodinâmica de Navios e Sistemas Oceânicos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2345, '095', 'a', 'oceanicos', '3.11.01.00-3 Hidrodinâmica de Navios e Sistemas Oceânicos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2346, '095', 'a', '3.11.01.01-1', '3.11.01.01-1 Resistência Hidrodinâmica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2347, '095', 'a', 'resistencia', '3.11.01.01-1 Resistência Hidrodinâmica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2348, '095', 'a', 'hidrodinamica', '3.11.01.01-1 Resistência Hidrodinâmica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2349, '095', 'a', '3.11.01.02-0', '3.11.01.02-0 Propulsão de Navios', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2350, '095', 'a', 'propulsao', '3.11.01.02-0 Propulsão de Navios', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2351, '095', 'a', 'de', '3.11.01.02-0 Propulsão de Navios', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2352, '095', 'a', 'navios', '3.11.01.02-0 Propulsão de Navios', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2353, '095', 'a', '3.11.02.00-0', '3.11.02.00-0 Estruturas Navais e Oceânicas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2354, '095', 'a', 'estruturas', '3.11.02.00-0 Estruturas Navais e Oceânicas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2355, '095', 'a', 'navais', '3.11.02.00-0 Estruturas Navais e Oceânicas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2356, '095', 'a', 'oceanicas', '3.11.02.00-0 Estruturas Navais e Oceânicas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2357, '095', 'a', '3.11.02.01-8', '3.11.02.01-8 Análise Teórica e Experimental de Estrutura', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2358, '095', 'a', 'analise', '3.11.02.01-8 Análise Teórica e Experimental de Estrutura', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2359, '095', 'a', 'teorica', '3.11.02.01-8 Análise Teórica e Experimental de Estrutura', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2360, '095', 'a', 'experimental', '3.11.02.01-8 Análise Teórica e Experimental de Estrutura', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2361, '095', 'a', 'de', '3.11.02.01-8 Análise Teórica e Experimental de Estrutura', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2362, '095', 'a', 'estrutura', '3.11.02.01-8 Análise Teórica e Experimental de Estrutura', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2363, '095', 'a', '3.11.02.02-6', '3.11.02.02-6 Dinâmica Estrutural Naval e Oceânica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2364, '095', 'a', 'dinamica', '3.11.02.02-6 Dinâmica Estrutural Naval e Oceânica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2365, '095', 'a', 'estrutural', '3.11.02.02-6 Dinâmica Estrutural Naval e Oceânica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2366, '095', 'a', 'naval', '3.11.02.02-6 Dinâmica Estrutural Naval e Oceânica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2367, '095', 'a', 'oceanica', '3.11.02.02-6 Dinâmica Estrutural Naval e Oceânica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2368, '095', 'a', '3.11.02.03-4', '3.11.02.03-4 Síntese Estrutural Naval e Oceânica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2369, '095', 'a', 'sintese', '3.11.02.03-4 Síntese Estrutural Naval e Oceânica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2370, '095', 'a', 'estrutural', '3.11.02.03-4 Síntese Estrutural Naval e Oceânica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2371, '095', 'a', 'naval', '3.11.02.03-4 Síntese Estrutural Naval e Oceânica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2372, '095', 'a', 'oceanica', '3.11.02.03-4 Síntese Estrutural Naval e Oceânica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2373, '095', 'a', '3.11.03.00-6', '3.11.03.00-6 Máquinas Marítimas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2374, '095', 'a', 'maquinas', '3.11.03.00-6 Máquinas Marítimas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2375, '095', 'a', 'maritimas', '3.11.03.00-6 Máquinas Marítimas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2376, '095', 'a', '3.11.03.01-4', '3.11.03.01-4 Análise de Sistemas Propulsores', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2377, '095', 'a', 'analise', '3.11.03.01-4 Análise de Sistemas Propulsores', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2378, '095', 'a', 'de', '3.11.03.01-4 Análise de Sistemas Propulsores', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2379, '095', 'a', 'sistemas', '3.11.03.01-4 Análise de Sistemas Propulsores', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2380, '095', 'a', 'propulsores', '3.11.03.01-4 Análise de Sistemas Propulsores', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2381, '095', 'a', '3.11.03.02-2', '3.11.03.02-2 Controle e Automação de Sistemas Propulsores', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2382, '095', 'a', 'controle', '3.11.03.02-2 Controle e Automação de Sistemas Propulsores', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2383, '095', 'a', 'automacao', '3.11.03.02-2 Controle e Automação de Sistemas Propulsores', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2384, '095', 'a', 'de', '3.11.03.02-2 Controle e Automação de Sistemas Propulsores', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2385, '095', 'a', 'sistemas', '3.11.03.02-2 Controle e Automação de Sistemas Propulsores', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2386, '095', 'a', 'propulsores', '3.11.03.02-2 Controle e Automação de Sistemas Propulsores', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2387, '095', 'a', '3.11.03.03-0', '3.11.03.03-0 Equipamentos Auxiliares do Sistema Propulsivo', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2388, '095', 'a', 'equipamentos', '3.11.03.03-0 Equipamentos Auxiliares do Sistema Propulsivo', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2389, '095', 'a', 'auxiliares', '3.11.03.03-0 Equipamentos Auxiliares do Sistema Propulsivo', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2390, '095', 'a', 'do', '3.11.03.03-0 Equipamentos Auxiliares do Sistema Propulsivo', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2391, '095', 'a', 'sistema', '3.11.03.03-0 Equipamentos Auxiliares do Sistema Propulsivo', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2392, '095', 'a', 'propulsivo', '3.11.03.03-0 Equipamentos Auxiliares do Sistema Propulsivo', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2393, '095', 'a', '3.11.03.04-9', '3.11.03.04-9 Motor de Propulsão', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2394, '095', 'a', 'motor', '3.11.03.04-9 Motor de Propulsão', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2395, '095', 'a', 'de', '3.11.03.04-9 Motor de Propulsão', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2396, '095', 'a', 'propulsao', '3.11.03.04-9 Motor de Propulsão', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2397, '095', 'a', '3.11.04.00-2', '3.11.04.00-2 Projeto de Navios e de Sistemas Oceânicos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2398, '095', 'a', 'projeto', '3.11.04.00-2 Projeto de Navios e de Sistemas Oceânicos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2399, '095', 'a', 'de', '3.11.04.00-2 Projeto de Navios e de Sistemas Oceânicos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2400, '095', 'a', 'navios', '3.11.04.00-2 Projeto de Navios e de Sistemas Oceânicos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2401, '095', 'a', 'de', '3.11.04.00-2 Projeto de Navios e de Sistemas Oceânicos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2402, '095', 'a', 'sistemas', '3.11.04.00-2 Projeto de Navios e de Sistemas Oceânicos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2403, '095', 'a', 'oceanicos', '3.11.04.00-2 Projeto de Navios e de Sistemas Oceânicos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2404, '095', 'a', '3.11.04.01-0', '3.11.04.01-0 Projetos de Navios', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2405, '095', 'a', 'projetos', '3.11.04.01-0 Projetos de Navios', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2406, '095', 'a', 'de', '3.11.04.01-0 Projetos de Navios', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2407, '095', 'a', 'navios', '3.11.04.01-0 Projetos de Navios', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2408, '095', 'a', '3.11.04.02-9', '3.11.04.02-9 Projetos de Sistemas Oceânicos Fixos e Semi-Fixos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2409, '095', 'a', 'projetos', '3.11.04.02-9 Projetos de Sistemas Oceânicos Fixos e Semi-Fixos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2410, '095', 'a', 'de', '3.11.04.02-9 Projetos de Sistemas Oceânicos Fixos e Semi-Fixos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2411, '095', 'a', 'sistemas', '3.11.04.02-9 Projetos de Sistemas Oceânicos Fixos e Semi-Fixos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2412, '095', 'a', 'oceanicos', '3.11.04.02-9 Projetos de Sistemas Oceânicos Fixos e Semi-Fixos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2413, '095', 'a', 'fixos', '3.11.04.02-9 Projetos de Sistemas Oceânicos Fixos e Semi-Fixos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2414, '095', 'a', 'semi-fixos', '3.11.04.02-9 Projetos de Sistemas Oceânicos Fixos e Semi-Fixos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2415, '095', 'a', '3.11.04.03-7', '3.11.04.03-7 Projetos de Embarcações Não-Convencionais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2416, '095', 'a', 'projetos', '3.11.04.03-7 Projetos de Embarcações Não-Convencionais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2417, '095', 'a', 'de', '3.11.04.03-7 Projetos de Embarcações Não-Convencionais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2418, '095', 'a', 'embarcacoes', '3.11.04.03-7 Projetos de Embarcações Não-Convencionais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2419, '095', 'a', 'nao-convencionais', '3.11.04.03-7 Projetos de Embarcações Não-Convencionais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2420, '095', 'a', '3.11.05.00-9', '3.11.05.00-9 Tecnologia de Construção Naval e de Sistemas Oceânicas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2421, '095', 'a', 'tecnologia', '3.11.05.00-9 Tecnologia de Construção Naval e de Sistemas Oceânicas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2422, '095', 'a', 'de', '3.11.05.00-9 Tecnologia de Construção Naval e de Sistemas Oceânicas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2423, '095', 'a', 'construcao', '3.11.05.00-9 Tecnologia de Construção Naval e de Sistemas Oceânicas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2424, '095', 'a', 'naval', '3.11.05.00-9 Tecnologia de Construção Naval e de Sistemas Oceânicas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2425, '095', 'a', 'de', '3.11.05.00-9 Tecnologia de Construção Naval e de Sistemas Oceânicas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2426, '095', 'a', 'sistemas', '3.11.05.00-9 Tecnologia de Construção Naval e de Sistemas Oceânicas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2427, '095', 'a', 'oceanicas', '3.11.05.00-9 Tecnologia de Construção Naval e de Sistemas Oceânicas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2428, '095', 'a', '3.11.05.01-7', '3.11.05.01-7 Métodos de Fabricação de Navios e Sistemas Oceânicos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2429, '095', 'a', 'metodos', '3.11.05.01-7 Métodos de Fabricação de Navios e Sistemas Oceânicos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2430, '095', 'a', 'de', '3.11.05.01-7 Métodos de Fabricação de Navios e Sistemas Oceânicos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2431, '095', 'a', 'fabricacao', '3.11.05.01-7 Métodos de Fabricação de Navios e Sistemas Oceânicos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2432, '095', 'a', 'de', '3.11.05.01-7 Métodos de Fabricação de Navios e Sistemas Oceânicos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2433, '095', 'a', 'navios', '3.11.05.01-7 Métodos de Fabricação de Navios e Sistemas Oceânicos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2434, '095', 'a', 'sistemas', '3.11.05.01-7 Métodos de Fabricação de Navios e Sistemas Oceânicos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2435, '095', 'a', 'oceanicos', '3.11.05.01-7 Métodos de Fabricação de Navios e Sistemas Oceânicos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2436, '095', 'a', '3.11.05.02-5', '3.11.05.02-5 Soldagem de Estruturas Navais e Oceânicos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2437, '095', 'a', 'soldagem', '3.11.05.02-5 Soldagem de Estruturas Navais e Oceânicos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2438, '095', 'a', 'de', '3.11.05.02-5 Soldagem de Estruturas Navais e Oceânicos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2439, '095', 'a', 'estruturas', '3.11.05.02-5 Soldagem de Estruturas Navais e Oceânicos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2440, '095', 'a', 'navais', '3.11.05.02-5 Soldagem de Estruturas Navais e Oceânicos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2441, '095', 'a', 'oceanicos', '3.11.05.02-5 Soldagem de Estruturas Navais e Oceânicos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2442, '095', 'a', '3.11.05.03-3', '3.11.05.03-3 Custos de Construção Naval', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2443, '095', 'a', 'custos', '3.11.05.03-3 Custos de Construção Naval', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2444, '095', 'a', 'de', '3.11.05.03-3 Custos de Construção Naval', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2445, '095', 'a', 'construcao', '3.11.05.03-3 Custos de Construção Naval', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2446, '095', 'a', 'naval', '3.11.05.03-3 Custos de Construção Naval', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2447, '095', 'a', '3.11.05.04-1', '3.11.05.04-1 Normatização e Certificação de Qualidade de Navios', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2448, '095', 'a', 'normatizacao', '3.11.05.04-1 Normatização e Certificação de Qualidade de Navios', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2449, '095', 'a', 'certificacao', '3.11.05.04-1 Normatização e Certificação de Qualidade de Navios', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2450, '095', 'a', 'de', '3.11.05.04-1 Normatização e Certificação de Qualidade de Navios', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2451, '095', 'a', 'qualidade', '3.11.05.04-1 Normatização e Certificação de Qualidade de Navios', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2452, '095', 'a', 'de', '3.11.05.04-1 Normatização e Certificação de Qualidade de Navios', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2453, '095', 'a', 'navios', '3.11.05.04-1 Normatização e Certificação de Qualidade de Navios', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2454, '095', 'a', '3.12.00.00-1', '3.12.00.00-1 Engenharia Aeroespacial', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2455, '095', 'a', 'engenharia', '3.12.00.00-1 Engenharia Aeroespacial', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2456, '095', 'a', 'aeroespacial', '3.12.00.00-1 Engenharia Aeroespacial', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2457, '095', 'a', '3.12.01.00-8', '3.12.01.00-8 Aerodinâmica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2458, '095', 'a', 'aerodinamica', '3.12.01.00-8 Aerodinâmica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2459, '095', 'a', '3.12.01.01-6', '3.12.01.01-6 Aerodinâmica de Aeronaves Espaciais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2460, '095', 'a', 'aerodinamica', '3.12.01.01-6 Aerodinâmica de Aeronaves Espaciais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2461, '095', 'a', 'de', '3.12.01.01-6 Aerodinâmica de Aeronaves Espaciais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2462, '095', 'a', 'aeronaves', '3.12.01.01-6 Aerodinâmica de Aeronaves Espaciais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2463, '095', 'a', 'espaciais', '3.12.01.01-6 Aerodinâmica de Aeronaves Espaciais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2464, '095', 'a', '3.12.01.02-4', '3.12.01.02-4 Aerodinâmica dos Processos Geofísicos e Interplanetarios', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2465, '095', 'a', 'aerodinamica', '3.12.01.02-4 Aerodinâmica dos Processos Geofísicos e Interplanetarios', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2466, '095', 'a', 'dos', '3.12.01.02-4 Aerodinâmica dos Processos Geofísicos e Interplanetarios', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2467, '095', 'a', 'processos', '3.12.01.02-4 Aerodinâmica dos Processos Geofísicos e Interplanetarios', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2468, '095', 'a', 'geofisicos', '3.12.01.02-4 Aerodinâmica dos Processos Geofísicos e Interplanetarios', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2469, '095', 'a', 'interplanetarios', '3.12.01.02-4 Aerodinâmica dos Processos Geofísicos e Interplanetarios', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2470, '095', 'a', '3.12.02.00-4', '3.12.02.00-4 Dinâmica de Vôo', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2471, '095', 'a', 'dinamica', '3.12.02.00-4 Dinâmica de Vôo', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2472, '095', 'a', 'de', '3.12.02.00-4 Dinâmica de Vôo', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2473, '095', 'a', 'voo', '3.12.02.00-4 Dinâmica de Vôo', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2474, '095', 'a', '3.12.02.01-2', '3.12.02.01-2 Trajetorias e Orbitas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2475, '095', 'a', 'trajetorias', '3.12.02.01-2 Trajetorias e Orbitas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2476, '095', 'a', 'orbitas', '3.12.02.01-2 Trajetorias e Orbitas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2477, '095', 'a', '3.12.02.02-0', '3.12.02.02-0 Estabilidade e Controle', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2478, '095', 'a', 'estabilidade', '3.12.02.02-0 Estabilidade e Controle', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2479, '095', 'a', 'controle', '3.12.02.02-0 Estabilidade e Controle', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2480, '095', 'a', '3.12.03.00-0', '3.12.03.00-0 Estruturas Aeroespaciais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2481, '095', 'a', 'estruturas', '3.12.03.00-0 Estruturas Aeroespaciais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2482, '095', 'a', 'aeroespaciais', '3.12.03.00-0 Estruturas Aeroespaciais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2483, '095', 'a', '3.12.03.01-9', '3.12.03.01-9 Aeroelasticidade', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2484, '095', 'a', 'aeroelasticidade', '3.12.03.01-9 Aeroelasticidade', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2485, '095', 'a', '3.12.03.02-7', '3.12.03.02-7 Fadiga', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2486, '095', 'a', 'fadiga', '3.12.03.02-7 Fadiga', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2487, '095', 'a', '3.12.03.03-5', '3.12.03.03-5 Projeto de Estruturas Aeroespaciais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2488, '095', 'a', 'projeto', '3.12.03.03-5 Projeto de Estruturas Aeroespaciais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2489, '095', 'a', 'de', '3.12.03.03-5 Projeto de Estruturas Aeroespaciais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2490, '095', 'a', 'estruturas', '3.12.03.03-5 Projeto de Estruturas Aeroespaciais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2491, '095', 'a', 'aeroespaciais', '3.12.03.03-5 Projeto de Estruturas Aeroespaciais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2492, '095', 'a', '3.12.04.00-7', '3.12.04.00-7 Materiais e Processos para Engenharia Aeronáutica e Aeroespacial', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2493, '095', 'a', 'materiais', '3.12.04.00-7 Materiais e Processos para Engenharia Aeronáutica e Aeroespacial', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2494, '095', 'a', 'processos', '3.12.04.00-7 Materiais e Processos para Engenharia Aeronáutica e Aeroespacial', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2495, '095', 'a', 'para', '3.12.04.00-7 Materiais e Processos para Engenharia Aeronáutica e Aeroespacial', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2496, '095', 'a', 'engenharia', '3.12.04.00-7 Materiais e Processos para Engenharia Aeronáutica e Aeroespacial', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2497, '095', 'a', 'aeronautica', '3.12.04.00-7 Materiais e Processos para Engenharia Aeronáutica e Aeroespacial', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2498, '095', 'a', 'aeroespacial', '3.12.04.00-7 Materiais e Processos para Engenharia Aeronáutica e Aeroespacial', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2499, '095', 'a', '3.12.05.00-3', '3.12.05.00-3 Propulsão Aeroespacial', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2500, '095', 'a', 'propulsao', '3.12.05.00-3 Propulsão Aeroespacial', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2501, '095', 'a', 'aeroespacial', '3.12.05.00-3 Propulsão Aeroespacial', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2502, '095', 'a', '3.12.05.01-1', '3.12.05.01-1 Combustão e Escoamento com Reações Químicas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2503, '095', 'a', 'combustao', '3.12.05.01-1 Combustão e Escoamento com Reações Químicas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2504, '095', 'a', 'escoamento', '3.12.05.01-1 Combustão e Escoamento com Reações Químicas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2505, '095', 'a', 'com', '3.12.05.01-1 Combustão e Escoamento com Reações Químicas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2506, '095', 'a', 'reacoes', '3.12.05.01-1 Combustão e Escoamento com Reações Químicas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2507, '095', 'a', 'quimicas', '3.12.05.01-1 Combustão e Escoamento com Reações Químicas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2508, '095', 'a', '3.12.05.02-0', '3.12.05.02-0 Propulsão de Foguetes', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2509, '095', 'a', 'propulsao', '3.12.05.02-0 Propulsão de Foguetes', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2510, '095', 'a', 'de', '3.12.05.02-0 Propulsão de Foguetes', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2511, '095', 'a', 'foguetes', '3.12.05.02-0 Propulsão de Foguetes', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2512, '095', 'a', '3.12.05.03-8', '3.12.05.03-8 Máquinas de Fluxo', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2513, '095', 'a', 'maquinas', '3.12.05.03-8 Máquinas de Fluxo', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2514, '095', 'a', 'de', '3.12.05.03-8 Máquinas de Fluxo', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2515, '095', 'a', 'fluxo', '3.12.05.03-8 Máquinas de Fluxo', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2516, '095', 'a', '3.12.05.04-6', '3.12.05.04-6 Motores Alternativos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2517, '095', 'a', 'motores', '3.12.05.04-6 Motores Alternativos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2518, '095', 'a', 'alternativos', '3.12.05.04-6 Motores Alternativos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2519, '095', 'a', '3.12.06.00-0', '3.12.06.00-0 Sistemas Aeroespaciais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2520, '095', 'a', 'sistemas', '3.12.06.00-0 Sistemas Aeroespaciais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2521, '095', 'a', 'aeroespaciais', '3.12.06.00-0 Sistemas Aeroespaciais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2522, '095', 'a', '3.12.06.01-8', '3.12.06.01-8 Aviões', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2523, '095', 'a', 'avioes', '3.12.06.01-8 Aviões', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2524, '095', 'a', '3.12.06.02-6', '3.12.06.02-6 Foguetes', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2525, '095', 'a', 'foguetes', '3.12.06.02-6 Foguetes', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2526, '095', 'a', '3.12.06.03-4', '3.12.06.03-4 Helicópteros', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2527, '095', 'a', 'helicopteros', '3.12.06.03-4 Helicópteros', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2528, '095', 'a', '3.12.06.04-2', '3.12.06.04-2 Hovercraft', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2529, '095', 'a', 'hovercraft', '3.12.06.04-2 Hovercraft', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2530, '095', 'a', '3.12.06.05-0', '3.12.06.05-0 Satélites e Outros Dispositivos Aeroespaciais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2531, '095', 'a', 'satelites', '3.12.06.05-0 Satélites e Outros Dispositivos Aeroespaciais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2532, '095', 'a', 'outros', '3.12.06.05-0 Satélites e Outros Dispositivos Aeroespaciais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2533, '095', 'a', 'dispositivos', '3.12.06.05-0 Satélites e Outros Dispositivos Aeroespaciais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2534, '095', 'a', 'aeroespaciais', '3.12.06.05-0 Satélites e Outros Dispositivos Aeroespaciais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2535, '095', 'a', '3.12.06.06-9', '3.12.06.06-9 Normatização e Certificação de Qualidade de Aeronaves e Componentes', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2536, '095', 'a', 'normatizacao', '3.12.06.06-9 Normatização e Certificação de Qualidade de Aeronaves e Componentes', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2537, '095', 'a', 'certificacao', '3.12.06.06-9 Normatização e Certificação de Qualidade de Aeronaves e Componentes', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2538, '095', 'a', 'de', '3.12.06.06-9 Normatização e Certificação de Qualidade de Aeronaves e Componentes', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2539, '095', 'a', 'qualidade', '3.12.06.06-9 Normatização e Certificação de Qualidade de Aeronaves e Componentes', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2540, '095', 'a', 'de', '3.12.06.06-9 Normatização e Certificação de Qualidade de Aeronaves e Componentes', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2541, '095', 'a', 'aeronaves', '3.12.06.06-9 Normatização e Certificação de Qualidade de Aeronaves e Componentes', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2542, '095', 'a', 'componentes', '3.12.06.06-9 Normatização e Certificação de Qualidade de Aeronaves e Componentes', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2543, '095', 'a', '3.12.06.07-7', '3.12.06.07-7 Manutenção de Sistemas Aeroespaciais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2544, '095', 'a', 'manutencao', '3.12.06.07-7 Manutenção de Sistemas Aeroespaciais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2545, '095', 'a', 'de', '3.12.06.07-7 Manutenção de Sistemas Aeroespaciais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2546, '095', 'a', 'sistemas', '3.12.06.07-7 Manutenção de Sistemas Aeroespaciais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2547, '095', 'a', 'aeroespaciais', '3.12.06.07-7 Manutenção de Sistemas Aeroespaciais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2548, '095', 'a', '3.13.00.00-6', '3.13.00.00-6 Engenharia Biomédica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2549, '095', 'a', 'engenharia', '3.13.00.00-6 Engenharia Biomédica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2550, '095', 'a', 'biomedica', '3.13.00.00-6 Engenharia Biomédica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2551, '095', 'a', '3.13.01.00-2', '3.13.01.00-2 Bioengenharia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2552, '095', 'a', 'bioengenharia', '3.13.01.00-2 Bioengenharia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2553, '095', 'a', '3.13.01.01-0', '3.13.01.01-0 Processamento de Sinais Biológicos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2554, '095', 'a', 'processamento', '3.13.01.01-0 Processamento de Sinais Biológicos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2555, '095', 'a', 'de', '3.13.01.01-0 Processamento de Sinais Biológicos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2556, '095', 'a', 'sinais', '3.13.01.01-0 Processamento de Sinais Biológicos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2557, '095', 'a', 'biologicos', '3.13.01.01-0 Processamento de Sinais Biológicos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2558, '095', 'a', '3.13.01.02-9', '3.13.01.02-9 Modelagem de Fenomenos Biológicos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2559, '095', 'a', 'modelagem', '3.13.01.02-9 Modelagem de Fenomenos Biológicos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2560, '095', 'a', 'de', '3.13.01.02-9 Modelagem de Fenomenos Biológicos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2561, '095', 'a', 'fenomenos', '3.13.01.02-9 Modelagem de Fenomenos Biológicos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2562, '095', 'a', 'biologicos', '3.13.01.02-9 Modelagem de Fenomenos Biológicos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2563, '095', 'a', '3.13.01.03-7', '3.13.01.03-7 Modelagem de Sistemas Biológicos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2564, '095', 'a', 'modelagem', '3.13.01.03-7 Modelagem de Sistemas Biológicos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2565, '095', 'a', 'de', '3.13.01.03-7 Modelagem de Sistemas Biológicos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2566, '095', 'a', 'sistemas', '3.13.01.03-7 Modelagem de Sistemas Biológicos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2567, '095', 'a', 'biologicos', '3.13.01.03-7 Modelagem de Sistemas Biológicos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2568, '095', 'a', '3.13.02.00-9', '3.13.02.00-9 Engenharia Médica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2569, '095', 'a', 'engenharia', '3.13.02.00-9 Engenharia Médica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2570, '095', 'a', 'medica', '3.13.02.00-9 Engenharia Médica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2571, '095', 'a', '3.13.02.01-7', '3.13.02.01-7 Biomateriais e Materiais Biocompatíveis', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2572, '095', 'a', 'biomateriais', '3.13.02.01-7 Biomateriais e Materiais Biocompatíveis', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2573, '095', 'a', 'materiais', '3.13.02.01-7 Biomateriais e Materiais Biocompatíveis', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2574, '095', 'a', 'biocompativeis', '3.13.02.01-7 Biomateriais e Materiais Biocompatíveis', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2575, '095', 'a', '3.13.02.02-5', '3.13.02.02-5 Transdutores para Aplicações Biomédicas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2576, '095', 'a', 'transdutores', '3.13.02.02-5 Transdutores para Aplicações Biomédicas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2577, '095', 'a', 'para', '3.13.02.02-5 Transdutores para Aplicações Biomédicas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2578, '095', 'a', 'aplicacoes', '3.13.02.02-5 Transdutores para Aplicações Biomédicas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2579, '095', 'a', 'biomedicas', '3.13.02.02-5 Transdutores para Aplicações Biomédicas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2580, '095', 'a', '3.13.02.03-3', '3.13.02.03-3 Instrumentação Odontológica e Médico-Hospitalar', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2581, '095', 'a', 'instrumentacao', '3.13.02.03-3 Instrumentação Odontológica e Médico-Hospitalar', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2582, '095', 'a', 'odontologica', '3.13.02.03-3 Instrumentação Odontológica e Médico-Hospitalar', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2583, '095', 'a', 'medico-hospitalar', '3.13.02.03-3 Instrumentação Odontológica e Médico-Hospitalar', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2584, '095', 'a', '3.13.02.04-1', '3.13.02.04-1 Tecnologia de Próteses', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2585, '095', 'a', 'tecnologia', '3.13.02.04-1 Tecnologia de Próteses', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2586, '095', 'a', 'de', '3.13.02.04-1 Tecnologia de Próteses', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2587, '095', 'a', 'proteses', '3.13.02.04-1 Tecnologia de Próteses', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2588, '095', 'a', '4.00.00.00-1', '4.00.00.00-1 Ciências da Saúde', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2589, '095', 'a', 'ciencias', '4.00.00.00-1 Ciências da Saúde', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2590, '095', 'a', 'da', '4.00.00.00-1 Ciências da Saúde', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2591, '095', 'a', 'saude', '4.00.00.00-1 Ciências da Saúde', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2592, '095', 'a', '4.01.00.00-6', '4.01.00.00-6 Medicina', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2593, '095', 'a', 'medicina', '4.01.00.00-6 Medicina', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2594, '095', 'a', '4.01.01.00-2', '4.01.01.00-2 Clínica Médica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2595, '095', 'a', 'clinica', '4.01.01.00-2 Clínica Médica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2596, '095', 'a', 'medica', '4.01.01.00-2 Clínica Médica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2597, '095', 'a', '4.01.01.01-0', '4.01.01.01-0 Angiologia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2598, '095', 'a', 'angiologia', '4.01.01.01-0 Angiologia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2599, '095', 'a', '4.01.01.02-9', '4.01.01.02-9 Dermatologia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2600, '095', 'a', 'dermatologia', '4.01.01.02-9 Dermatologia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2601, '095', 'a', '4.01.01.03-7', '4.01.01.03-7 Alergologia e Imunologia Clínica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2602, '095', 'a', 'alergologia', '4.01.01.03-7 Alergologia e Imunologia Clínica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2603, '095', 'a', 'imunologia', '4.01.01.03-7 Alergologia e Imunologia Clínica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2604, '095', 'a', 'clinica', '4.01.01.03-7 Alergologia e Imunologia Clínica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2605, '095', 'a', '4.01.01.04-5', '4.01.01.04-5 Cancerologia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2606, '095', 'a', 'cancerologia', '4.01.01.04-5 Cancerologia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2607, '095', 'a', '4.01.01.05-3', '4.01.01.05-3 Hematologia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2608, '095', 'a', 'hematologia', '4.01.01.05-3 Hematologia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2609, '095', 'a', '4.01.01.06-1', '4.01.01.06-1 Endocrinologia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2610, '095', 'a', 'endocrinologia', '4.01.01.06-1 Endocrinologia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2611, '095', 'a', '4.01.01.07-0', '4.01.01.07-0 Neurologia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2612, '095', 'a', 'neurologia', '4.01.01.07-0 Neurologia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2613, '095', 'a', '4.01.01.08-8', '4.01.01.08-8 Pediatria', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2614, '095', 'a', 'pediatria', '4.01.01.08-8 Pediatria', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2615, '095', 'a', '4.01.01.09-6', '4.01.01.09-6 Doenças Infecciosas e Parasitárias', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2616, '095', 'a', 'doencas', '4.01.01.09-6 Doenças Infecciosas e Parasitárias', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2617, '095', 'a', 'infecciosas', '4.01.01.09-6 Doenças Infecciosas e Parasitárias', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2618, '095', 'a', 'parasitarias', '4.01.01.09-6 Doenças Infecciosas e Parasitárias', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2619, '095', 'a', '4.01.01.10-0', '4.01.01.10-0 Cardiologia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2620, '095', 'a', 'cardiologia', '4.01.01.10-0 Cardiologia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2621, '095', 'a', '4.01.01.11-8', '4.01.01.11-8 Gastroenterologia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2622, '095', 'a', 'gastroenterologia', '4.01.01.11-8 Gastroenterologia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2623, '095', 'a', '4.01.01.12-6', '4.01.01.12-6 Pneumologia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2624, '095', 'a', 'pneumologia', '4.01.01.12-6 Pneumologia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2625, '095', 'a', '4.01.01.13-4', '4.01.01.13-4 Nefrologia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2626, '095', 'a', 'nefrologia', '4.01.01.13-4 Nefrologia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2627, '095', 'a', '4.01.01.14-2', '4.01.01.14-2 Reumatologia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2628, '095', 'a', 'reumatologia', '4.01.01.14-2 Reumatologia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2629, '095', 'a', '4.01.01.15-0', '4.01.01.15-0 Ginecologia e Obstetrícia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2630, '095', 'a', 'ginecologia', '4.01.01.15-0 Ginecologia e Obstetrícia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2631, '095', 'a', 'obstetricia', '4.01.01.15-0 Ginecologia e Obstetrícia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2632, '095', 'a', '4.01.01.16-9', '4.01.01.16-9 Fisiatria', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2633, '095', 'a', 'fisiatria', '4.01.01.16-9 Fisiatria', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2634, '095', 'a', '4.01.01.17-7', '4.01.01.17-7 Oftalmologia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2635, '095', 'a', 'oftalmologia', '4.01.01.17-7 Oftalmologia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2636, '095', 'a', '4.01.01.18-6', '4.01.01.18-6 Ortopedia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2637, '095', 'a', 'ortopedia', '4.01.01.18-6 Ortopedia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2638, '095', 'a', '4.01.02.00-9', '4.01.02.00-9 Cirurgia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2639, '095', 'a', 'cirurgia', '4.01.02.00-9 Cirurgia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2640, '095', 'a', '4.01.02.01-7', '4.01.02.01-7 Cirurgia Plástica e Restauradora', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2641, '095', 'a', 'cirurgia', '4.01.02.01-7 Cirurgia Plástica e Restauradora', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2642, '095', 'a', 'plastica', '4.01.02.01-7 Cirurgia Plástica e Restauradora', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2643, '095', 'a', 'restauradora', '4.01.02.01-7 Cirurgia Plástica e Restauradora', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2644, '095', 'a', '4.01.02.02-5', '4.01.02.02-5 Cirurgia Otorrinolaringológica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2645, '095', 'a', 'cirurgia', '4.01.02.02-5 Cirurgia Otorrinolaringológica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2646, '095', 'a', 'otorrinolaringologica', '4.01.02.02-5 Cirurgia Otorrinolaringológica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2647, '095', 'a', '4.01.02.03-3', '4.01.02.03-3 Cirurgia Oftalmológica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2648, '095', 'a', 'cirurgia', '4.01.02.03-3 Cirurgia Oftalmológica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2649, '095', 'a', 'oftalmologica', '4.01.02.03-3 Cirurgia Oftalmológica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2650, '095', 'a', '4.01.02.04-1', '4.01.02.04-1 Cirurgia Cardiovascular', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2651, '095', 'a', 'cirurgia', '4.01.02.04-1 Cirurgia Cardiovascular', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2652, '095', 'a', 'cardiovascular', '4.01.02.04-1 Cirurgia Cardiovascular', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2653, '095', 'a', '4.01.02.05-0', '4.01.02.05-0 Cirurgia Toráxica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2654, '095', 'a', 'cirurgia', '4.01.02.05-0 Cirurgia Toráxica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2655, '095', 'a', 'toraxica', '4.01.02.05-0 Cirurgia Toráxica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2656, '095', 'a', '4.01.02.06-8', '4.01.02.06-8 Cirurgia Gastroenterologia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2657, '095', 'a', 'cirurgia', '4.01.02.06-8 Cirurgia Gastroenterologia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2658, '095', 'a', 'gastroenterologia', '4.01.02.06-8 Cirurgia Gastroenterologia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2659, '095', 'a', '4.01.02.07-6', '4.01.02.07-6 Cirurgia Pediátrica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2660, '095', 'a', 'cirurgia', '4.01.02.07-6 Cirurgia Pediátrica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2661, '095', 'a', 'pediatrica', '4.01.02.07-6 Cirurgia Pediátrica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2662, '095', 'a', '4.01.02.08-4', '4.01.02.08-4 Neurocirurgia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2663, '095', 'a', 'neurocirurgia', '4.01.02.08-4 Neurocirurgia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2664, '095', 'a', '4.01.02.09-2', '4.01.02.09-2 Cirurgia Urológica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2665, '095', 'a', 'cirurgia', '4.01.02.09-2 Cirurgia Urológica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2666, '095', 'a', 'urologica', '4.01.02.09-2 Cirurgia Urológica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2667, '095', 'a', '4.01.02.10-6', '4.01.02.10-6 Cirurgia Proctológica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2668, '095', 'a', 'cirurgia', '4.01.02.10-6 Cirurgia Proctológica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2669, '095', 'a', 'proctologica', '4.01.02.10-6 Cirurgia Proctológica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2670, '095', 'a', '4.01.02.11-4', '4.01.02.11-4 Cirurgia Ortopédica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2671, '095', 'a', 'cirurgia', '4.01.02.11-4 Cirurgia Ortopédica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2672, '095', 'a', 'ortopedica', '4.01.02.11-4 Cirurgia Ortopédica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2673, '095', 'a', '4.01.02.12-2', '4.01.02.12-2 Cirurgia Traumatológica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2674, '095', 'a', 'cirurgia', '4.01.02.12-2 Cirurgia Traumatológica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2675, '095', 'a', 'traumatologica', '4.01.02.12-2 Cirurgia Traumatológica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2676, '095', 'a', '4.01.02.13-0', '4.01.02.13-0 Anestesiologia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2677, '095', 'a', 'anestesiologia', '4.01.02.13-0 Anestesiologia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2678, '095', 'a', '4.01.02.14-9', '4.01.02.14-9 Cirurgia Experimental', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2679, '095', 'a', 'cirurgia', '4.01.02.14-9 Cirurgia Experimental', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2680, '095', 'a', 'experimental', '4.01.02.14-9 Cirurgia Experimental', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2681, '095', 'a', '4.01.03.00-5', '4.01.03.00-5 Saúde Materno-Infantil', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2682, '095', 'a', 'saude', '4.01.03.00-5 Saúde Materno-Infantil', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2683, '095', 'a', 'materno-infantil', '4.01.03.00-5 Saúde Materno-Infantil', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2684, '095', 'a', '4.01.04.00-1', '4.01.04.00-1 Psiquiatria', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2685, '095', 'a', 'psiquiatria', '4.01.04.00-1 Psiquiatria', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2686, '095', 'a', '4.01.05.00-8', '4.01.05.00-8 Anatomia Patológica e Patologia Clínica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2687, '095', 'a', 'anatomia', '4.01.05.00-8 Anatomia Patológica e Patologia Clínica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2688, '095', 'a', 'patologica', '4.01.05.00-8 Anatomia Patológica e Patologia Clínica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2689, '095', 'a', 'patologia', '4.01.05.00-8 Anatomia Patológica e Patologia Clínica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2690, '095', 'a', 'clinica', '4.01.05.00-8 Anatomia Patológica e Patologia Clínica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2691, '095', 'a', '4.01.06.00-4', '4.01.06.00-4 Radiologia Médica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2692, '095', 'a', 'radiologia', '4.01.06.00-4 Radiologia Médica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2693, '095', 'a', 'medica', '4.01.06.00-4 Radiologia Médica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2694, '095', 'a', '4.01.07.00-0', '4.01.07.00-0 Medicina Legal e Deontologia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2695, '095', 'a', 'medicina', '4.01.07.00-0 Medicina Legal e Deontologia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2696, '095', 'a', 'legal', '4.01.07.00-0 Medicina Legal e Deontologia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2697, '095', 'a', 'deontologia', '4.01.07.00-0 Medicina Legal e Deontologia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2698, '095', 'a', '4.02.00.00-0', '4.02.00.00-0 Odontologia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2699, '095', 'a', 'odontologia', '4.02.00.00-0 Odontologia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2700, '095', 'a', '4.02.01.00-7', '4.02.01.00-7 Clínica Odontológica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2701, '095', 'a', 'clinica', '4.02.01.00-7 Clínica Odontológica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2702, '095', 'a', 'odontologica', '4.02.01.00-7 Clínica Odontológica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2703, '095', 'a', '4.02.02.00-3', '4.02.02.00-3 Cirurgia Buco-Maxilo-Facial', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2704, '095', 'a', 'cirurgia', '4.02.02.00-3 Cirurgia Buco-Maxilo-Facial', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2705, '095', 'a', 'buco-maxilo-facial', '4.02.02.00-3 Cirurgia Buco-Maxilo-Facial', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2706, '095', 'a', '4.02.03.00-0', '4.02.03.00-0 Ortodontia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2707, '095', 'a', 'ortodontia', '4.02.03.00-0 Ortodontia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2708, '095', 'a', '4.02.04.00-6', '4.02.04.00-6 Odontopediatria', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2709, '095', 'a', 'odontopediatria', '4.02.04.00-6 Odontopediatria', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2710, '095', 'a', '4.02.05.00-2', '4.02.05.00-2 Periodontia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2711, '095', 'a', 'periodontia', '4.02.05.00-2 Periodontia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2712, '095', 'a', '4.02.06.00-9', '4.02.06.00-9 Endodontia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2713, '095', 'a', 'endodontia', '4.02.06.00-9 Endodontia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2714, '095', 'a', '4.02.07.00-5', '4.02.07.00-5 Radiologia Odontológica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2715, '095', 'a', 'radiologia', '4.02.07.00-5 Radiologia Odontológica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2716, '095', 'a', 'odontologica', '4.02.07.00-5 Radiologia Odontológica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2717, '095', 'a', '4.02.08.00-1', '4.02.08.00-1 Odontologia Social e Preventiva', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2718, '095', 'a', 'odontologia', '4.02.08.00-1 Odontologia Social e Preventiva', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2719, '095', 'a', 'social', '4.02.08.00-1 Odontologia Social e Preventiva', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2720, '095', 'a', 'preventiva', '4.02.08.00-1 Odontologia Social e Preventiva', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2721, '095', 'a', '4.02.09.00-8', '4.02.09.00-8 Materiais Odontológicos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2722, '095', 'a', 'materiais', '4.02.09.00-8 Materiais Odontológicos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2723, '095', 'a', 'odontologicos', '4.02.09.00-8 Materiais Odontológicos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2724, '095', 'a', '4.03.00.00-5', '4.03.00.00-5 Farmácia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2725, '095', 'a', 'farmacia', '4.03.00.00-5 Farmácia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2726, '095', 'a', '4.03.01.00-1', '4.03.01.00-1 Farmacotecnia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2727, '095', 'a', 'farmacotecnia', '4.03.01.00-1 Farmacotecnia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2728, '095', 'a', '4.03.02.00-8', '4.03.02.00-8 Farmacognosia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2729, '095', 'a', 'farmacognosia', '4.03.02.00-8 Farmacognosia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2730, '095', 'a', '4.03.03.00-4', '4.03.03.00-4 Análise Toxicológica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2731, '095', 'a', 'analise', '4.03.03.00-4 Análise Toxicológica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2732, '095', 'a', 'toxicologica', '4.03.03.00-4 Análise Toxicológica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2733, '095', 'a', '4.03.04.00-0', '4.03.04.00-0 Análise e Controle e Medicamentos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2734, '095', 'a', 'analise', '4.03.04.00-0 Análise e Controle e Medicamentos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2735, '095', 'a', 'controle', '4.03.04.00-0 Análise e Controle e Medicamentos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2736, '095', 'a', 'medicamentos', '4.03.04.00-0 Análise e Controle e Medicamentos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2737, '095', 'a', '4.03.05.00-7', '4.03.05.00-7 Bromatologia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2738, '095', 'a', 'bromatologia', '4.03.05.00-7 Bromatologia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2739, '095', 'a', '4.04.00.00-0', '4.04.00.00-0 Enfermagem', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2740, '095', 'a', 'enfermagem', '4.04.00.00-0 Enfermagem', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2741, '095', 'a', '4.04.01.00-6', '4.04.01.00-6 Enfermagem Médico-Cirúrgica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2742, '095', 'a', 'enfermagem', '4.04.01.00-6 Enfermagem Médico-Cirúrgica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2743, '095', 'a', 'medico-cirurgica', '4.04.01.00-6 Enfermagem Médico-Cirúrgica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2744, '095', 'a', '4.04.02.00-2', '4.04.02.00-2 Enfermagem Obstétrica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2745, '095', 'a', 'enfermagem', '4.04.02.00-2 Enfermagem Obstétrica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2746, '095', 'a', 'obstetrica', '4.04.02.00-2 Enfermagem Obstétrica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2747, '095', 'a', '4.04.03.00-9', '4.04.03.00-9 Enfermagem Pediátrica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2748, '095', 'a', 'enfermagem', '4.04.03.00-9 Enfermagem Pediátrica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2749, '095', 'a', 'pediatrica', '4.04.03.00-9 Enfermagem Pediátrica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2750, '095', 'a', '4.04.04.00-5', '4.04.04.00-5 Enfermagem Psiquiátrica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2751, '095', 'a', 'enfermagem', '4.04.04.00-5 Enfermagem Psiquiátrica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2752, '095', 'a', 'psiquiatrica', '4.04.04.00-5 Enfermagem Psiquiátrica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2753, '095', 'a', '4.04.05.00-1', '4.04.05.00-1 Enfermagem de Doenças Contagiosas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2754, '095', 'a', 'enfermagem', '4.04.05.00-1 Enfermagem de Doenças Contagiosas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2755, '095', 'a', 'de', '4.04.05.00-1 Enfermagem de Doenças Contagiosas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2756, '095', 'a', 'doencas', '4.04.05.00-1 Enfermagem de Doenças Contagiosas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2757, '095', 'a', 'contagiosas', '4.04.05.00-1 Enfermagem de Doenças Contagiosas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2758, '095', 'a', '4.04.06.00-8', '4.04.06.00-8 Enfermagem de Saúde Pública', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2759, '095', 'a', 'enfermagem', '4.04.06.00-8 Enfermagem de Saúde Pública', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2760, '095', 'a', 'de', '4.04.06.00-8 Enfermagem de Saúde Pública', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2761, '095', 'a', 'saude', '4.04.06.00-8 Enfermagem de Saúde Pública', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2762, '095', 'a', 'publica', '4.04.06.00-8 Enfermagem de Saúde Pública', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2763, '095', 'a', '4.05.00.00-4', '4.05.00.00-4 Nutrição', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2764, '095', 'a', 'nutricao', '4.05.00.00-4 Nutrição', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2765, '095', 'a', '4.05.01.00-0', '4.05.01.00-0 Bioquímica da Nutrição', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2766, '095', 'a', 'bioquimica', '4.05.01.00-0 Bioquímica da Nutrição', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2767, '095', 'a', 'da', '4.05.01.00-0 Bioquímica da Nutrição', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2768, '095', 'a', 'nutricao', '4.05.01.00-0 Bioquímica da Nutrição', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2769, '095', 'a', '4.05.02.00-7', '4.05.02.00-7 Dietética', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2770, '095', 'a', 'dietetica', '4.05.02.00-7 Dietética', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2771, '095', 'a', '4.05.03.00-3', '4.05.03.00-3 Análise Nutricional de População', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2772, '095', 'a', 'analise', '4.05.03.00-3 Análise Nutricional de População', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2773, '095', 'a', 'nutricional', '4.05.03.00-3 Análise Nutricional de População', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2774, '095', 'a', 'de', '4.05.03.00-3 Análise Nutricional de População', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2775, '095', 'a', 'populacao', '4.05.03.00-3 Análise Nutricional de População', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2776, '095', 'a', '4.05.04.00-0', '4.05.04.00-0 Desnutrição e Desenvolvimento Fisiológico', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2777, '095', 'a', 'desnutricao', '4.05.04.00-0 Desnutrição e Desenvolvimento Fisiológico', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2778, '095', 'a', 'desenvolvimento', '4.05.04.00-0 Desnutrição e Desenvolvimento Fisiológico', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2779, '095', 'a', 'fisiologico', '4.05.04.00-0 Desnutrição e Desenvolvimento Fisiológico', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2780, '095', 'a', '4.06.00.00-9', '4.06.00.00-9 Saúde Coletiva', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2781, '095', 'a', 'saude', '4.06.00.00-9 Saúde Coletiva', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2782, '095', 'a', 'coletiva', '4.06.00.00-9 Saúde Coletiva', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2783, '095', 'a', '4.06.01.00-5', '4.06.01.00-5 Epidemiologia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2784, '095', 'a', 'epidemiologia', '4.06.01.00-5 Epidemiologia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2785, '095', 'a', '4.06.02.00-1', '4.06.02.00-1 Saúde Publica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2786, '095', 'a', 'saude', '4.06.02.00-1 Saúde Publica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2787, '095', 'a', 'publica', '4.06.02.00-1 Saúde Publica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2788, '095', 'a', '4.06.03.00-8', '4.06.03.00-8 Medicina Preventiva', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2789, '095', 'a', 'medicina', '4.06.03.00-8 Medicina Preventiva', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2790, '095', 'a', 'preventiva', '4.06.03.00-8 Medicina Preventiva', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2791, '095', 'a', '4.07.00.00-3', '4.07.00.00-3 Fonoaudiologia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2792, '095', 'a', 'fonoaudiologia', '4.07.00.00-3 Fonoaudiologia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2793, '095', 'a', '4.08.00.00-8', '4.08.00.00-8 Fisioterapia e Terapia Ocupacional', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2794, '095', 'a', 'fisioterapia', '4.08.00.00-8 Fisioterapia e Terapia Ocupacional', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2795, '095', 'a', 'terapia', '4.08.00.00-8 Fisioterapia e Terapia Ocupacional', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2796, '095', 'a', 'ocupacional', '4.08.00.00-8 Fisioterapia e Terapia Ocupacional', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2797, '095', 'a', '4.09.00.00-2', '4.09.00.00-2 Educação Física', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2798, '095', 'a', 'educacao', '4.09.00.00-2 Educação Física', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2799, '095', 'a', 'fisica', '4.09.00.00-2 Educação Física', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2800, '095', 'a', '5.00.00.00-4', '5.00.00.00-4 Ciências Agrárias', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2801, '095', 'a', 'ciencias', '5.00.00.00-4 Ciências Agrárias', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2802, '095', 'a', 'agrarias', '5.00.00.00-4 Ciências Agrárias', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2803, '095', 'a', '5.01.00.00-9', '5.01.00.00-9 Agronomia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2804, '095', 'a', 'agronomia', '5.01.00.00-9 Agronomia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2805, '095', 'a', '5.01.01.00-5', '5.01.01.00-5 Ciência do Solo', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2806, '095', 'a', 'ciencia', '5.01.01.00-5 Ciência do Solo', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2807, '095', 'a', 'do', '5.01.01.00-5 Ciência do Solo', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2808, '095', 'a', 'solo', '5.01.01.00-5 Ciência do Solo', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2809, '095', 'a', '5.01.01.01-3', '5.01.01.01-3 Genese, Morfologia e Classificação dos Solos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2810, '095', 'a', 'genese,', '5.01.01.01-3 Genese, Morfologia e Classificação dos Solos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2811, '095', 'a', 'morfologia', '5.01.01.01-3 Genese, Morfologia e Classificação dos Solos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2812, '095', 'a', 'classificacao', '5.01.01.01-3 Genese, Morfologia e Classificação dos Solos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2813, '095', 'a', 'dos', '5.01.01.01-3 Genese, Morfologia e Classificação dos Solos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2814, '095', 'a', 'solos', '5.01.01.01-3 Genese, Morfologia e Classificação dos Solos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2815, '095', 'a', '5.01.01.02-1', '5.01.01.02-1 Física do Solo', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2816, '095', 'a', 'fisica', '5.01.01.02-1 Física do Solo', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2817, '095', 'a', 'do', '5.01.01.02-1 Física do Solo', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2818, '095', 'a', 'solo', '5.01.01.02-1 Física do Solo', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2819, '095', 'a', '5.01.01.03-0', '5.01.01.03-0 Química do Solo', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2820, '095', 'a', 'quimica', '5.01.01.03-0 Química do Solo', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2821, '095', 'a', 'do', '5.01.01.03-0 Química do Solo', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2822, '095', 'a', 'solo', '5.01.01.03-0 Química do Solo', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2823, '095', 'a', '5.01.01.04-8', '5.01.01.04-8 Microbiologia e Bioquímica do Solo', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2824, '095', 'a', 'microbiologia', '5.01.01.04-8 Microbiologia e Bioquímica do Solo', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2825, '095', 'a', 'bioquimica', '5.01.01.04-8 Microbiologia e Bioquímica do Solo', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2826, '095', 'a', 'do', '5.01.01.04-8 Microbiologia e Bioquímica do Solo', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2827, '095', 'a', 'solo', '5.01.01.04-8 Microbiologia e Bioquímica do Solo', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2828, '095', 'a', '5.01.01.05-6', '5.01.01.05-6 Fertilidade do Solo e Adubação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2829, '095', 'a', 'fertilidade', '5.01.01.05-6 Fertilidade do Solo e Adubação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2830, '095', 'a', 'do', '5.01.01.05-6 Fertilidade do Solo e Adubação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2831, '095', 'a', 'solo', '5.01.01.05-6 Fertilidade do Solo e Adubação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2832, '095', 'a', 'adubacao', '5.01.01.05-6 Fertilidade do Solo e Adubação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2833, '095', 'a', '5.01.01.06-4', '5.01.01.06-4 Manejo e Conservação do Solo', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2834, '095', 'a', 'manejo', '5.01.01.06-4 Manejo e Conservação do Solo', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2835, '095', 'a', 'conservacao', '5.01.01.06-4 Manejo e Conservação do Solo', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2836, '095', 'a', 'do', '5.01.01.06-4 Manejo e Conservação do Solo', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2837, '095', 'a', 'solo', '5.01.01.06-4 Manejo e Conservação do Solo', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2838, '095', 'a', '5.01.02.00-1', '5.01.02.00-1 Fitossanidade', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2839, '095', 'a', 'fitossanidade', '5.01.02.00-1 Fitossanidade', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2840, '095', 'a', '5.01.02.01-0', '5.01.02.01-0 Fitopatologia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2841, '095', 'a', 'fitopatologia', '5.01.02.01-0 Fitopatologia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2842, '095', 'a', '5.01.02.02-8', '5.01.02.02-8 Entomologia Agrícola', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2843, '095', 'a', 'entomologia', '5.01.02.02-8 Entomologia Agrícola', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2844, '095', 'a', 'agricola', '5.01.02.02-8 Entomologia Agrícola', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2845, '095', 'a', '5.01.02.03-6', '5.01.02.03-6 Parasitologia Agrícola', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2846, '095', 'a', 'parasitologia', '5.01.02.03-6 Parasitologia Agrícola', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2847, '095', 'a', 'agricola', '5.01.02.03-6 Parasitologia Agrícola', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2848, '095', 'a', '5.01.02.04-4', '5.01.02.04-4 Microbiologia Agrícola', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2849, '095', 'a', 'microbiologia', '5.01.02.04-4 Microbiologia Agrícola', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2850, '095', 'a', 'agricola', '5.01.02.04-4 Microbiologia Agrícola', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2851, '095', 'a', '5.01.02.05-2', '5.01.02.05-2 Defesa Fitossanitária', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2852, '095', 'a', 'defesa', '5.01.02.05-2 Defesa Fitossanitária', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2853, '095', 'a', 'fitossanitaria', '5.01.02.05-2 Defesa Fitossanitária', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2854, '095', 'a', '5.01.03.00-8', '5.01.03.00-8 Fitotecnia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2855, '095', 'a', 'fitotecnia', '5.01.03.00-8 Fitotecnia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2856, '095', 'a', '5.01.03.01-6', '5.01.03.01-6 Manejo e Tratos Culturais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2857, '095', 'a', 'manejo', '5.01.03.01-6 Manejo e Tratos Culturais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2858, '095', 'a', 'tratos', '5.01.03.01-6 Manejo e Tratos Culturais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2859, '095', 'a', 'culturais', '5.01.03.01-6 Manejo e Tratos Culturais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2860, '095', 'a', '5.01.03.02-4', '5.01.03.02-4 Mecanização Agrícola', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2861, '095', 'a', 'mecanizacao', '5.01.03.02-4 Mecanização Agrícola', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2862, '095', 'a', 'agricola', '5.01.03.02-4 Mecanização Agrícola', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2863, '095', 'a', '5.01.03.03-2', '5.01.03.03-2 Produção e Beneficiamento de Sementes', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2864, '095', 'a', 'producao', '5.01.03.03-2 Produção e Beneficiamento de Sementes', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2865, '095', 'a', 'beneficiamento', '5.01.03.03-2 Produção e Beneficiamento de Sementes', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2866, '095', 'a', 'de', '5.01.03.03-2 Produção e Beneficiamento de Sementes', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2867, '095', 'a', 'sementes', '5.01.03.03-2 Produção e Beneficiamento de Sementes', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2868, '095', 'a', '5.01.03.04-0', '5.01.03.04-0 Produção de Mudas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2869, '095', 'a', 'producao', '5.01.03.04-0 Produção de Mudas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2870, '095', 'a', 'de', '5.01.03.04-0 Produção de Mudas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2871, '095', 'a', 'mudas', '5.01.03.04-0 Produção de Mudas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2872, '095', 'a', '5.01.03.05-9', '5.01.03.05-9 Melhoramento Vegetal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2873, '095', 'a', 'melhoramento', '5.01.03.05-9 Melhoramento Vegetal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2874, '095', 'a', 'vegetal', '5.01.03.05-9 Melhoramento Vegetal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2875, '095', 'a', '5.01.03.06-7', '5.01.03.06-7 Fisiologia de Plantas Cultivadas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2876, '095', 'a', 'fisiologia', '5.01.03.06-7 Fisiologia de Plantas Cultivadas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2877, '095', 'a', 'de', '5.01.03.06-7 Fisiologia de Plantas Cultivadas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2878, '095', 'a', 'plantas', '5.01.03.06-7 Fisiologia de Plantas Cultivadas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2879, '095', 'a', 'cultivadas', '5.01.03.06-7 Fisiologia de Plantas Cultivadas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2880, '095', 'a', '5.01.03.07-5', '5.01.03.07-5 Matologia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2881, '095', 'a', 'matologia', '5.01.03.07-5 Matologia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2882, '095', 'a', '5.01.04.00-4', '5.01.04.00-4 Floricultura, Parques e Jardins', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2883, '095', 'a', 'floricultura,', '5.01.04.00-4 Floricultura, Parques e Jardins', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2884, '095', 'a', 'parques', '5.01.04.00-4 Floricultura, Parques e Jardins', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2885, '095', 'a', 'jardins', '5.01.04.00-4 Floricultura, Parques e Jardins', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2886, '095', 'a', '5.01.04.01-2', '5.01.04.01-2 Floricultura', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2887, '095', 'a', 'floricultura', '5.01.04.01-2 Floricultura', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2888, '095', 'a', '5.01.04.02-0', '5.01.04.02-0 Parques e Jardins', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2889, '095', 'a', 'parques', '5.01.04.02-0 Parques e Jardins', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2890, '095', 'a', 'jardins', '5.01.04.02-0 Parques e Jardins', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2891, '095', 'a', '5.01.04.03-9', '5.01.04.03-9 Arborização de Vias Públicas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2892, '095', 'a', 'arborizacao', '5.01.04.03-9 Arborização de Vias Públicas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2893, '095', 'a', 'de', '5.01.04.03-9 Arborização de Vias Públicas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2894, '095', 'a', 'vias', '5.01.04.03-9 Arborização de Vias Públicas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2895, '095', 'a', 'publicas', '5.01.04.03-9 Arborização de Vias Públicas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2896, '095', 'a', '5.01.05.00-0', '5.01.05.00-0 Agrometeorologia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2897, '095', 'a', 'agrometeorologia', '5.01.05.00-0 Agrometeorologia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2898, '095', 'a', '5.01.06.00-7', '5.01.06.00-7 Extensão Rural', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2899, '095', 'a', 'extensao', '5.01.06.00-7 Extensão Rural', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2900, '095', 'a', 'rural', '5.01.06.00-7 Extensão Rural', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2901, '095', 'a', '5.02.00.00-3', '5.02.00.00-3 Recursos Florestais e Engenharia Florestal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2902, '095', 'a', 'recursos', '5.02.00.00-3 Recursos Florestais e Engenharia Florestal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2903, '095', 'a', 'florestais', '5.02.00.00-3 Recursos Florestais e Engenharia Florestal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2904, '095', 'a', 'engenharia', '5.02.00.00-3 Recursos Florestais e Engenharia Florestal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2905, '095', 'a', 'florestal', '5.02.00.00-3 Recursos Florestais e Engenharia Florestal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2906, '095', 'a', '5.02.01.00-0', '5.02.01.00-0 Silvicultura', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2907, '095', 'a', 'silvicultura', '5.02.01.00-0 Silvicultura', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2908, '095', 'a', '5.02.01.01-8', '5.02.01.01-8 Dendrologia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2909, '095', 'a', 'dendrologia', '5.02.01.01-8 Dendrologia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2910, '095', 'a', '5.02.01.02-6', '5.02.01.02-6 Florestamento e Reflorestamento', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2911, '095', 'a', 'florestamento', '5.02.01.02-6 Florestamento e Reflorestamento', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2912, '095', 'a', 'reflorestamento', '5.02.01.02-6 Florestamento e Reflorestamento', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2913, '095', 'a', '5.02.01.03-4', '5.02.01.03-4 Genética e Melhoramento Florestal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2914, '095', 'a', 'genetica', '5.02.01.03-4 Genética e Melhoramento Florestal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2915, '095', 'a', 'melhoramento', '5.02.01.03-4 Genética e Melhoramento Florestal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2916, '095', 'a', 'florestal', '5.02.01.03-4 Genética e Melhoramento Florestal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2917, '095', 'a', '5.02.01.04-2', '5.02.01.04-2 Sementes Florestais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2918, '095', 'a', 'sementes', '5.02.01.04-2 Sementes Florestais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2919, '095', 'a', 'florestais', '5.02.01.04-2 Sementes Florestais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2920, '095', 'a', '5.02.01.05-0', '5.02.01.05-0 Nutrição Florestal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2921, '095', 'a', 'nutricao', '5.02.01.05-0 Nutrição Florestal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2922, '095', 'a', 'florestal', '5.02.01.05-0 Nutrição Florestal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2923, '095', 'a', '5.02.01.06-9', '5.02.01.06-9 Fisiologia Florestal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2924, '095', 'a', 'fisiologia', '5.02.01.06-9 Fisiologia Florestal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2925, '095', 'a', 'florestal', '5.02.01.06-9 Fisiologia Florestal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2926, '095', 'a', '5.02.01.07-7', '5.02.01.07-7 Solos Florestais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2927, '095', 'a', 'solos', '5.02.01.07-7 Solos Florestais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2928, '095', 'a', 'florestais', '5.02.01.07-7 Solos Florestais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2929, '095', 'a', '5.02.01.08-5', '5.02.01.08-5 Proteção Florestal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2930, '095', 'a', 'protecao', '5.02.01.08-5 Proteção Florestal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2931, '095', 'a', 'florestal', '5.02.01.08-5 Proteção Florestal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2932, '095', 'a', '5.02.02.00-6', '5.02.02.00-6 Manejo Florestal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2933, '095', 'a', 'manejo', '5.02.02.00-6 Manejo Florestal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2934, '095', 'a', 'florestal', '5.02.02.00-6 Manejo Florestal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2935, '095', 'a', '5.02.02.01-4', '5.02.02.01-4 Economia Florestal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2936, '095', 'a', 'economia', '5.02.02.01-4 Economia Florestal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2937, '095', 'a', 'florestal', '5.02.02.01-4 Economia Florestal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2938, '095', 'a', '5.02.02.02-2', '5.02.02.02-2 Politica e Legislação Florestal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2939, '095', 'a', 'politica', '5.02.02.02-2 Politica e Legislação Florestal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2940, '095', 'a', 'legislacao', '5.02.02.02-2 Politica e Legislação Florestal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2941, '095', 'a', 'florestal', '5.02.02.02-2 Politica e Legislação Florestal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2942, '095', 'a', '5.02.02.03-0', '5.02.02.03-0 Administração Florestal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2943, '095', 'a', 'administracao', '5.02.02.03-0 Administração Florestal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2944, '095', 'a', 'florestal', '5.02.02.03-0 Administração Florestal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2945, '095', 'a', '5.02.02.04-9', '5.02.02.04-9 Dendrometria e Inventário Florestal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2946, '095', 'a', 'dendrometria', '5.02.02.04-9 Dendrometria e Inventário Florestal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2947, '095', 'a', 'inventario', '5.02.02.04-9 Dendrometria e Inventário Florestal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2948, '095', 'a', 'florestal', '5.02.02.04-9 Dendrometria e Inventário Florestal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2949, '095', 'a', '5.02.02.05-7', '5.02.02.05-7 Fotointerpretação Florestal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2950, '095', 'a', 'fotointerpretacao', '5.02.02.05-7 Fotointerpretação Florestal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2951, '095', 'a', 'florestal', '5.02.02.05-7 Fotointerpretação Florestal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2952, '095', 'a', '5.02.02.06-5', '5.02.02.06-5 Ordenamento Florestal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2953, '095', 'a', 'ordenamento', '5.02.02.06-5 Ordenamento Florestal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2954, '095', 'a', 'florestal', '5.02.02.06-5 Ordenamento Florestal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2955, '095', 'a', '5.02.03.00-2', '5.02.03.00-2 Técnicas e Operações Florestais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2956, '095', 'a', 'tecnicas', '5.02.03.00-2 Técnicas e Operações Florestais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2957, '095', 'a', 'operacoes', '5.02.03.00-2 Técnicas e Operações Florestais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2958, '095', 'a', 'florestais', '5.02.03.00-2 Técnicas e Operações Florestais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2959, '095', 'a', '5.02.03.01-0', '5.02.03.01-0 Exploração Florestal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2960, '095', 'a', 'exploracao', '5.02.03.01-0 Exploração Florestal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2961, '095', 'a', 'florestal', '5.02.03.01-0 Exploração Florestal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2962, '095', 'a', '5.02.03.02-9', '5.02.03.02-9 Mecanização Florestal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2963, '095', 'a', 'mecanizacao', '5.02.03.02-9 Mecanização Florestal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2964, '095', 'a', 'florestal', '5.02.03.02-9 Mecanização Florestal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2965, '095', 'a', '5.02.04.00-9', '5.02.04.00-9 Tecnologia e Utilização de Produtos Florestais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2966, '095', 'a', 'tecnologia', '5.02.04.00-9 Tecnologia e Utilização de Produtos Florestais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2967, '095', 'a', 'utilizacao', '5.02.04.00-9 Tecnologia e Utilização de Produtos Florestais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2968, '095', 'a', 'de', '5.02.04.00-9 Tecnologia e Utilização de Produtos Florestais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2969, '095', 'a', 'produtos', '5.02.04.00-9 Tecnologia e Utilização de Produtos Florestais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2970, '095', 'a', 'florestais', '5.02.04.00-9 Tecnologia e Utilização de Produtos Florestais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2971, '095', 'a', '5.02.04.01-7', '5.02.04.01-7 Anatomia e Identificação de Produtos Florestais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2972, '095', 'a', 'anatomia', '5.02.04.01-7 Anatomia e Identificação de Produtos Florestais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2973, '095', 'a', 'identificacao', '5.02.04.01-7 Anatomia e Identificação de Produtos Florestais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2974, '095', 'a', 'de', '5.02.04.01-7 Anatomia e Identificação de Produtos Florestais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2975, '095', 'a', 'produtos', '5.02.04.01-7 Anatomia e Identificação de Produtos Florestais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2976, '095', 'a', 'florestais', '5.02.04.01-7 Anatomia e Identificação de Produtos Florestais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2977, '095', 'a', '5.02.04.02-5', '5.02.04.02-5 Propriedades Físico-Mecânicas da Madeira', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2978, '095', 'a', 'propriedades', '5.02.04.02-5 Propriedades Físico-Mecânicas da Madeira', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2979, '095', 'a', 'fisico-mecanicas', '5.02.04.02-5 Propriedades Físico-Mecânicas da Madeira', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2980, '095', 'a', 'da', '5.02.04.02-5 Propriedades Físico-Mecânicas da Madeira', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2981, '095', 'a', 'madeira', '5.02.04.02-5 Propriedades Físico-Mecânicas da Madeira', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2982, '095', 'a', '5.02.04.03-3', '5.02.04.03-3 Relações Água-Madeira e Secagem', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2983, '095', 'a', 'relacoes', '5.02.04.03-3 Relações Água-Madeira e Secagem', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2984, '095', 'a', 'agua-madeira', '5.02.04.03-3 Relações Água-Madeira e Secagem', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2985, '095', 'a', 'secagem', '5.02.04.03-3 Relações Água-Madeira e Secagem', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2986, '095', 'a', '5.02.04.04-1', '5.02.04.04-1 Tratamento da Madeira', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2987, '095', 'a', 'tratamento', '5.02.04.04-1 Tratamento da Madeira', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2988, '095', 'a', 'da', '5.02.04.04-1 Tratamento da Madeira', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2989, '095', 'a', 'madeira', '5.02.04.04-1 Tratamento da Madeira', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2990, '095', 'a', '5.02.04.05-0', '5.02.04.05-0 Processamento Mecânico da Madeira', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2991, '095', 'a', 'processamento', '5.02.04.05-0 Processamento Mecânico da Madeira', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2992, '095', 'a', 'mecanico', '5.02.04.05-0 Processamento Mecânico da Madeira', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2993, '095', 'a', 'da', '5.02.04.05-0 Processamento Mecânico da Madeira', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2994, '095', 'a', 'madeira', '5.02.04.05-0 Processamento Mecânico da Madeira', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2995, '095', 'a', '5.02.04.06-8', '5.02.04.06-8 Química da Madeira', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2996, '095', 'a', 'quimica', '5.02.04.06-8 Química da Madeira', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2997, '095', 'a', 'da', '5.02.04.06-8 Química da Madeira', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2998, '095', 'a', 'madeira', '5.02.04.06-8 Química da Madeira', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (2999, '095', 'a', '5.02.04.07-6', '5.02.04.07-6 Resinas de Madeiras', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3000, '095', 'a', 'resinas', '5.02.04.07-6 Resinas de Madeiras', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3001, '095', 'a', 'de', '5.02.04.07-6 Resinas de Madeiras', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3002, '095', 'a', 'madeiras', '5.02.04.07-6 Resinas de Madeiras', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3003, '095', 'a', '5.02.04.08-4', '5.02.04.08-4 Tecnologia de Celulose e Papel', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3004, '095', 'a', 'tecnologia', '5.02.04.08-4 Tecnologia de Celulose e Papel', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3005, '095', 'a', 'de', '5.02.04.08-4 Tecnologia de Celulose e Papel', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3006, '095', 'a', 'celulose', '5.02.04.08-4 Tecnologia de Celulose e Papel', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3007, '095', 'a', 'papel', '5.02.04.08-4 Tecnologia de Celulose e Papel', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3008, '095', 'a', '5.02.04.09-2', '5.02.04.09-2 Tecnologia de Chapas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3009, '095', 'a', 'tecnologia', '5.02.04.09-2 Tecnologia de Chapas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3010, '095', 'a', 'de', '5.02.04.09-2 Tecnologia de Chapas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3011, '095', 'a', 'chapas', '5.02.04.09-2 Tecnologia de Chapas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3012, '095', 'a', '5.02.05.00-5', '5.02.05.00-5 Conservação da Natureza', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3013, '095', 'a', 'conservacao', '5.02.05.00-5 Conservação da Natureza', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3014, '095', 'a', 'da', '5.02.05.00-5 Conservação da Natureza', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3015, '095', 'a', 'natureza', '5.02.05.00-5 Conservação da Natureza', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3016, '095', 'a', '5.02.05.01-3', '5.02.05.01-3 Hidrologia Florestal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3017, '095', 'a', 'hidrologia', '5.02.05.01-3 Hidrologia Florestal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3018, '095', 'a', 'florestal', '5.02.05.01-3 Hidrologia Florestal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3019, '095', 'a', '5.02.05.02-1', '5.02.05.02-1 Conservação de Áreas Silvestres', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3020, '095', 'a', 'conservacao', '5.02.05.02-1 Conservação de Áreas Silvestres', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3021, '095', 'a', 'de', '5.02.05.02-1 Conservação de Áreas Silvestres', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3022, '095', 'a', 'areas', '5.02.05.02-1 Conservação de Áreas Silvestres', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3023, '095', 'a', 'silvestres', '5.02.05.02-1 Conservação de Áreas Silvestres', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3024, '095', 'a', '5.02.05.03-0', '5.02.05.03-0 Conservação de Bacias Hidrográficas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3025, '095', 'a', 'conservacao', '5.02.05.03-0 Conservação de Bacias Hidrográficas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3026, '095', 'a', 'de', '5.02.05.03-0 Conservação de Bacias Hidrográficas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3027, '095', 'a', 'bacias', '5.02.05.03-0 Conservação de Bacias Hidrográficas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3028, '095', 'a', 'hidrograficas', '5.02.05.03-0 Conservação de Bacias Hidrográficas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3029, '095', 'a', '5.02.05.04-8', '5.02.05.04-8 Recuperação de Áreas Degradadas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3030, '095', 'a', 'recuperacao', '5.02.05.04-8 Recuperação de Áreas Degradadas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3031, '095', 'a', 'de', '5.02.05.04-8 Recuperação de Áreas Degradadas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3032, '095', 'a', 'areas', '5.02.05.04-8 Recuperação de Áreas Degradadas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3033, '095', 'a', 'degradadas', '5.02.05.04-8 Recuperação de Áreas Degradadas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3034, '095', 'a', '5.02.06.00-1', '5.02.06.00-1 Energia de Biomassa Florestal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3035, '095', 'a', 'energia', '5.02.06.00-1 Energia de Biomassa Florestal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3036, '095', 'a', 'de', '5.02.06.00-1 Energia de Biomassa Florestal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3037, '095', 'a', 'biomassa', '5.02.06.00-1 Energia de Biomassa Florestal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3038, '095', 'a', 'florestal', '5.02.06.00-1 Energia de Biomassa Florestal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3039, '095', 'a', '5.03.00.00-8', '5.03.00.00-8 Engenharia Agrícola', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3040, '095', 'a', 'engenharia', '5.03.00.00-8 Engenharia Agrícola', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3041, '095', 'a', 'agricola', '5.03.00.00-8 Engenharia Agrícola', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3042, '095', 'a', '5.03.01.00-4', '5.03.01.00-4 Máquinas e Implementos Agrícolas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3043, '095', 'a', 'maquinas', '5.03.01.00-4 Máquinas e Implementos Agrícolas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3044, '095', 'a', 'implementos', '5.03.01.00-4 Máquinas e Implementos Agrícolas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3045, '095', 'a', 'agricolas', '5.03.01.00-4 Máquinas e Implementos Agrícolas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3046, '095', 'a', '5.03.02.00-0', '5.03.02.00-0 Engenharia de Água e Solo', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3047, '095', 'a', 'engenharia', '5.03.02.00-0 Engenharia de Água e Solo', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3048, '095', 'a', 'de', '5.03.02.00-0 Engenharia de Água e Solo', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3049, '095', 'a', 'agua', '5.03.02.00-0 Engenharia de Água e Solo', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3050, '095', 'a', 'solo', '5.03.02.00-0 Engenharia de Água e Solo', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3051, '095', 'a', '5.03.02.01-9', '5.03.02.01-9 Irrigação e Drenagem', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3052, '095', 'a', 'irrigacao', '5.03.02.01-9 Irrigação e Drenagem', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3053, '095', 'a', 'drenagem', '5.03.02.01-9 Irrigação e Drenagem', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3054, '095', 'a', '5.03.02.02-7', '5.03.02.02-7 Conservação de Solo e Água', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3055, '095', 'a', 'conservacao', '5.03.02.02-7 Conservação de Solo e Água', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3056, '095', 'a', 'de', '5.03.02.02-7 Conservação de Solo e Água', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3057, '095', 'a', 'solo', '5.03.02.02-7 Conservação de Solo e Água', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3058, '095', 'a', 'agua', '5.03.02.02-7 Conservação de Solo e Água', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3059, '095', 'a', '5.03.03.00-7', '5.03.03.00-7 Engenharia de Processamento de Produtos Agrícolas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3060, '095', 'a', 'engenharia', '5.03.03.00-7 Engenharia de Processamento de Produtos Agrícolas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3061, '095', 'a', 'de', '5.03.03.00-7 Engenharia de Processamento de Produtos Agrícolas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3062, '095', 'a', 'processamento', '5.03.03.00-7 Engenharia de Processamento de Produtos Agrícolas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3063, '095', 'a', 'de', '5.03.03.00-7 Engenharia de Processamento de Produtos Agrícolas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3064, '095', 'a', 'produtos', '5.03.03.00-7 Engenharia de Processamento de Produtos Agrícolas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3065, '095', 'a', 'agricolas', '5.03.03.00-7 Engenharia de Processamento de Produtos Agrícolas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3066, '095', 'a', '5.03.03.01-5', '5.03.03.01-5 Pré-Processamento de Produtos Agrícolas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3067, '095', 'a', 'pre-processamento', '5.03.03.01-5 Pré-Processamento de Produtos Agrícolas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3068, '095', 'a', 'de', '5.03.03.01-5 Pré-Processamento de Produtos Agrícolas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3069, '095', 'a', 'produtos', '5.03.03.01-5 Pré-Processamento de Produtos Agrícolas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3070, '095', 'a', 'agricolas', '5.03.03.01-5 Pré-Processamento de Produtos Agrícolas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3071, '095', 'a', '5.03.03.02-3', '5.03.03.02-3 Armazenamento de Produtos Agrícolas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3072, '095', 'a', 'armazenamento', '5.03.03.02-3 Armazenamento de Produtos Agrícolas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3073, '095', 'a', 'de', '5.03.03.02-3 Armazenamento de Produtos Agrícolas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3074, '095', 'a', 'produtos', '5.03.03.02-3 Armazenamento de Produtos Agrícolas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3075, '095', 'a', 'agricolas', '5.03.03.02-3 Armazenamento de Produtos Agrícolas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3076, '095', 'a', '5.03.03.03-1', '5.03.03.03-1 Transferência de Produtos Agrícolas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3077, '095', 'a', 'transferencia', '5.03.03.03-1 Transferência de Produtos Agrícolas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3078, '095', 'a', 'de', '5.03.03.03-1 Transferência de Produtos Agrícolas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3079, '095', 'a', 'produtos', '5.03.03.03-1 Transferência de Produtos Agrícolas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3080, '095', 'a', 'agricolas', '5.03.03.03-1 Transferência de Produtos Agrícolas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3081, '095', 'a', '5.03.04.00-3', '5.03.04.00-3 Construções Rurais e Ambiência', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3082, '095', 'a', 'construcoes', '5.03.04.00-3 Construções Rurais e Ambiência', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3083, '095', 'a', 'rurais', '5.03.04.00-3 Construções Rurais e Ambiência', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3084, '095', 'a', 'ambiencia', '5.03.04.00-3 Construções Rurais e Ambiência', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3085, '095', 'a', '5.03.04.01-1', '5.03.04.01-1 Assentamento Rural', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3086, '095', 'a', 'assentamento', '5.03.04.01-1 Assentamento Rural', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3087, '095', 'a', 'rural', '5.03.04.01-1 Assentamento Rural', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3088, '095', 'a', '5.03.04.02-0', '5.03.04.02-0 Engenharia de Construções Rurais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3089, '095', 'a', 'engenharia', '5.03.04.02-0 Engenharia de Construções Rurais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3090, '095', 'a', 'de', '5.03.04.02-0 Engenharia de Construções Rurais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3091, '095', 'a', 'construcoes', '5.03.04.02-0 Engenharia de Construções Rurais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3092, '095', 'a', 'rurais', '5.03.04.02-0 Engenharia de Construções Rurais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3093, '095', 'a', '5.03.04.03-8', '5.03.04.03-8 Saneamento Rural', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3094, '095', 'a', 'saneamento', '5.03.04.03-8 Saneamento Rural', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3095, '095', 'a', 'rural', '5.03.04.03-8 Saneamento Rural', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3096, '095', 'a', '5.03.05.00-0', '5.03.05.00-0 Energização Rural', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3097, '095', 'a', 'energizacao', '5.03.05.00-0 Energização Rural', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3098, '095', 'a', 'rural', '5.03.05.00-0 Energização Rural', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3099, '095', 'a', '5.04.00.00-2', '5.04.00.00-2 Zootecnia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3100, '095', 'a', 'zootecnia', '5.04.00.00-2 Zootecnia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3101, '095', 'a', '5.04.01.00-9', '5.04.01.00-9 Ecologia dos Animais Domésticos e Etologia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3102, '095', 'a', 'ecologia', '5.04.01.00-9 Ecologia dos Animais Domésticos e Etologia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3103, '095', 'a', 'dos', '5.04.01.00-9 Ecologia dos Animais Domésticos e Etologia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3104, '095', 'a', 'animais', '5.04.01.00-9 Ecologia dos Animais Domésticos e Etologia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3105, '095', 'a', 'domesticos', '5.04.01.00-9 Ecologia dos Animais Domésticos e Etologia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3106, '095', 'a', 'etologia', '5.04.01.00-9 Ecologia dos Animais Domésticos e Etologia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3107, '095', 'a', '5.04.02.00-5', '5.04.02.00-5 Genética e Melhoramento dos Animais Domésticos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3108, '095', 'a', 'genetica', '5.04.02.00-5 Genética e Melhoramento dos Animais Domésticos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3109, '095', 'a', 'melhoramento', '5.04.02.00-5 Genética e Melhoramento dos Animais Domésticos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3110, '095', 'a', 'dos', '5.04.02.00-5 Genética e Melhoramento dos Animais Domésticos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3111, '095', 'a', 'animais', '5.04.02.00-5 Genética e Melhoramento dos Animais Domésticos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3112, '095', 'a', 'domesticos', '5.04.02.00-5 Genética e Melhoramento dos Animais Domésticos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3113, '095', 'a', '5.04.03.00-1', '5.04.03.00-1 Nutrição e Alimentação Animal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3114, '095', 'a', 'nutricao', '5.04.03.00-1 Nutrição e Alimentação Animal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3115, '095', 'a', 'alimentacao', '5.04.03.00-1 Nutrição e Alimentação Animal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3116, '095', 'a', 'animal', '5.04.03.00-1 Nutrição e Alimentação Animal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3117, '095', 'a', '5.04.03.01-0', '5.04.03.01-0 Exigências Nutricionais dos Animais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3118, '095', 'a', 'exigencias', '5.04.03.01-0 Exigências Nutricionais dos Animais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3119, '095', 'a', 'nutricionais', '5.04.03.01-0 Exigências Nutricionais dos Animais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3120, '095', 'a', 'dos', '5.04.03.01-0 Exigências Nutricionais dos Animais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3121, '095', 'a', 'animais', '5.04.03.01-0 Exigências Nutricionais dos Animais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3122, '095', 'a', '5.04.03.02-8', '5.04.03.02-8 Avaliação de Alimentos para Animais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3123, '095', 'a', 'avaliacao', '5.04.03.02-8 Avaliação de Alimentos para Animais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3124, '095', 'a', 'de', '5.04.03.02-8 Avaliação de Alimentos para Animais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3125, '095', 'a', 'alimentos', '5.04.03.02-8 Avaliação de Alimentos para Animais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3126, '095', 'a', 'para', '5.04.03.02-8 Avaliação de Alimentos para Animais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3127, '095', 'a', 'animais', '5.04.03.02-8 Avaliação de Alimentos para Animais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3128, '095', 'a', '5.04.03.03-6', '5.04.03.03-6 Conservação de Alimentos para Animais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3129, '095', 'a', 'conservacao', '5.04.03.03-6 Conservação de Alimentos para Animais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3130, '095', 'a', 'de', '5.04.03.03-6 Conservação de Alimentos para Animais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3131, '095', 'a', 'alimentos', '5.04.03.03-6 Conservação de Alimentos para Animais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3132, '095', 'a', 'para', '5.04.03.03-6 Conservação de Alimentos para Animais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3133, '095', 'a', 'animais', '5.04.03.03-6 Conservação de Alimentos para Animais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3134, '095', 'a', '5.04.04.00-8', '5.04.04.00-8 Pastagem e Forragicultura', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3135, '095', 'a', 'pastagem', '5.04.04.00-8 Pastagem e Forragicultura', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3136, '095', 'a', 'forragicultura', '5.04.04.00-8 Pastagem e Forragicultura', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3137, '095', 'a', '5.04.04.01-6', '5.04.04.01-6 Avaliação, Produção e Conservação de Forragens', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3138, '095', 'a', 'avaliacao,', '5.04.04.01-6 Avaliação, Produção e Conservação de Forragens', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3139, '095', 'a', 'producao', '5.04.04.01-6 Avaliação, Produção e Conservação de Forragens', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3140, '095', 'a', 'conservacao', '5.04.04.01-6 Avaliação, Produção e Conservação de Forragens', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3141, '095', 'a', 'de', '5.04.04.01-6 Avaliação, Produção e Conservação de Forragens', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3142, '095', 'a', 'forragens', '5.04.04.01-6 Avaliação, Produção e Conservação de Forragens', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3143, '095', 'a', '5.04.04.02-4', '5.04.04.02-4 Manejo e Conservação de Pastagens', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3144, '095', 'a', 'manejo', '5.04.04.02-4 Manejo e Conservação de Pastagens', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3145, '095', 'a', 'conservacao', '5.04.04.02-4 Manejo e Conservação de Pastagens', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3146, '095', 'a', 'de', '5.04.04.02-4 Manejo e Conservação de Pastagens', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3147, '095', 'a', 'pastagens', '5.04.04.02-4 Manejo e Conservação de Pastagens', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3148, '095', 'a', '5.04.04.03-2', '5.04.04.03-2 Fisiologia de Plantas Forrageiras', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3149, '095', 'a', 'fisiologia', '5.04.04.03-2 Fisiologia de Plantas Forrageiras', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3150, '095', 'a', 'de', '5.04.04.03-2 Fisiologia de Plantas Forrageiras', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3151, '095', 'a', 'plantas', '5.04.04.03-2 Fisiologia de Plantas Forrageiras', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3152, '095', 'a', 'forrageiras', '5.04.04.03-2 Fisiologia de Plantas Forrageiras', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3153, '095', 'a', '5.04.04.04-0', '5.04.04.04-0 Melhoramento de Plantas Forrageiras e Produção de Sementes', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3154, '095', 'a', 'melhoramento', '5.04.04.04-0 Melhoramento de Plantas Forrageiras e Produção de Sementes', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3155, '095', 'a', 'de', '5.04.04.04-0 Melhoramento de Plantas Forrageiras e Produção de Sementes', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3156, '095', 'a', 'plantas', '5.04.04.04-0 Melhoramento de Plantas Forrageiras e Produção de Sementes', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3157, '095', 'a', 'forrageiras', '5.04.04.04-0 Melhoramento de Plantas Forrageiras e Produção de Sementes', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3158, '095', 'a', 'producao', '5.04.04.04-0 Melhoramento de Plantas Forrageiras e Produção de Sementes', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3159, '095', 'a', 'de', '5.04.04.04-0 Melhoramento de Plantas Forrageiras e Produção de Sementes', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3160, '095', 'a', 'sementes', '5.04.04.04-0 Melhoramento de Plantas Forrageiras e Produção de Sementes', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3161, '095', 'a', '5.04.04.05-9', '5.04.04.05-9 Toxicologia e Plantas Tóxicas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3162, '095', 'a', 'toxicologia', '5.04.04.05-9 Toxicologia e Plantas Tóxicas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3163, '095', 'a', 'plantas', '5.04.04.05-9 Toxicologia e Plantas Tóxicas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3164, '095', 'a', 'toxicas', '5.04.04.05-9 Toxicologia e Plantas Tóxicas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3165, '095', 'a', '5.04.05.00-4', '5.04.05.00-4 Produção Animal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3166, '095', 'a', 'producao', '5.04.05.00-4 Produção Animal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3167, '095', 'a', 'animal', '5.04.05.00-4 Produção Animal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3168, '095', 'a', '5.04.05.01-2', '5.04.05.01-2 Criação de Animais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3169, '095', 'a', 'criacao', '5.04.05.01-2 Criação de Animais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3170, '095', 'a', 'de', '5.04.05.01-2 Criação de Animais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3171, '095', 'a', 'animais', '5.04.05.01-2 Criação de Animais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3172, '095', 'a', '5.04.05.02-0', '5.04.05.02-0 Manejo de Animais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3173, '095', 'a', 'manejo', '5.04.05.02-0 Manejo de Animais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3174, '095', 'a', 'de', '5.04.05.02-0 Manejo de Animais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3175, '095', 'a', 'animais', '5.04.05.02-0 Manejo de Animais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3176, '095', 'a', '5.04.05.03-9', '5.04.05.03-9 Instalações para Produção Animal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3177, '095', 'a', 'instalacoes', '5.04.05.03-9 Instalações para Produção Animal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3178, '095', 'a', 'para', '5.04.05.03-9 Instalações para Produção Animal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3179, '095', 'a', 'producao', '5.04.05.03-9 Instalações para Produção Animal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3180, '095', 'a', 'animal', '5.04.05.03-9 Instalações para Produção Animal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3181, '095', 'a', '5.05.00.00-7', '5.05.00.00-7 Medicina Veterinária', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3182, '095', 'a', 'medicina', '5.05.00.00-7 Medicina Veterinária', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3183, '095', 'a', 'veterinaria', '5.05.00.00-7 Medicina Veterinária', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3184, '095', 'a', '5.05.01.00-3', '5.05.01.00-3 Clínica e Cirurgia Animal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3185, '095', 'a', 'clinica', '5.05.01.00-3 Clínica e Cirurgia Animal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3186, '095', 'a', 'cirurgia', '5.05.01.00-3 Clínica e Cirurgia Animal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3187, '095', 'a', 'animal', '5.05.01.00-3 Clínica e Cirurgia Animal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3188, '095', 'a', '5.05.01.01-1', '5.05.01.01-1 Anestesiologia Animal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3189, '095', 'a', 'anestesiologia', '5.05.01.01-1 Anestesiologia Animal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3190, '095', 'a', 'animal', '5.05.01.01-1 Anestesiologia Animal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3191, '095', 'a', '5.05.01.02-0', '5.05.01.02-0 Técnica Cirúrgica Animal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3192, '095', 'a', 'tecnica', '5.05.01.02-0 Técnica Cirúrgica Animal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3193, '095', 'a', 'cirurgica', '5.05.01.02-0 Técnica Cirúrgica Animal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3194, '095', 'a', 'animal', '5.05.01.02-0 Técnica Cirúrgica Animal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3195, '095', 'a', '5.05.01.03-8', '5.05.01.03-8 Radiologia de Animais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3196, '095', 'a', 'radiologia', '5.05.01.03-8 Radiologia de Animais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3197, '095', 'a', 'de', '5.05.01.03-8 Radiologia de Animais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3198, '095', 'a', 'animais', '5.05.01.03-8 Radiologia de Animais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3199, '095', 'a', '5.05.01.04-6', '5.05.01.04-6 Farmacologia e Terapêutica Animal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3200, '095', 'a', 'farmacologia', '5.05.01.04-6 Farmacologia e Terapêutica Animal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3201, '095', 'a', 'terapeutica', '5.05.01.04-6 Farmacologia e Terapêutica Animal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3202, '095', 'a', 'animal', '5.05.01.04-6 Farmacologia e Terapêutica Animal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3203, '095', 'a', '5.05.01.05-4', '5.05.01.05-4 Obstetrícia Animal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3204, '095', 'a', 'obstetricia', '5.05.01.05-4 Obstetrícia Animal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3205, '095', 'a', 'animal', '5.05.01.05-4 Obstetrícia Animal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3206, '095', 'a', '5.05.01.06-2', '5.05.01.06-2 Clínica Veterinária', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3207, '095', 'a', 'clinica', '5.05.01.06-2 Clínica Veterinária', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3208, '095', 'a', 'veterinaria', '5.05.01.06-2 Clínica Veterinária', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3209, '095', 'a', '5.05.01.07-0', '5.05.01.07-0 Clínica Cirúrgica Animal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3210, '095', 'a', 'clinica', '5.05.01.07-0 Clínica Cirúrgica Animal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3211, '095', 'a', 'cirurgica', '5.05.01.07-0 Clínica Cirúrgica Animal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3212, '095', 'a', 'animal', '5.05.01.07-0 Clínica Cirúrgica Animal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3213, '095', 'a', '5.05.01.08-9', '5.05.01.08-9 Toxicologia Animal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3214, '095', 'a', 'toxicologia', '5.05.01.08-9 Toxicologia Animal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3215, '095', 'a', 'animal', '5.05.01.08-9 Toxicologia Animal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3216, '095', 'a', '5.05.02.00-0', '5.05.02.00-0 Medicina Veterinária Preventiva', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3217, '095', 'a', 'medicina', '5.05.02.00-0 Medicina Veterinária Preventiva', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3218, '095', 'a', 'veterinaria', '5.05.02.00-0 Medicina Veterinária Preventiva', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3219, '095', 'a', 'preventiva', '5.05.02.00-0 Medicina Veterinária Preventiva', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3220, '095', 'a', '5.05.02.01-8', '5.05.02.01-8 Epidemiologia Animal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3221, '095', 'a', 'epidemiologia', '5.05.02.01-8 Epidemiologia Animal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3222, '095', 'a', 'animal', '5.05.02.01-8 Epidemiologia Animal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3223, '095', 'a', '5.05.02.02-6', '5.05.02.02-6 Saneamento Aplicado à Saúde do Homem', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3224, '095', 'a', 'saneamento', '5.05.02.02-6 Saneamento Aplicado à Saúde do Homem', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3225, '095', 'a', 'aplicado', '5.05.02.02-6 Saneamento Aplicado à Saúde do Homem', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3226, '095', 'a', 'saude', '5.05.02.02-6 Saneamento Aplicado à Saúde do Homem', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3227, '095', 'a', 'do', '5.05.02.02-6 Saneamento Aplicado à Saúde do Homem', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3228, '095', 'a', 'homem', '5.05.02.02-6 Saneamento Aplicado à Saúde do Homem', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3229, '095', 'a', '5.05.02.03-4', '5.05.02.03-4 Doenças Infecciosas de Animais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3230, '095', 'a', 'doencas', '5.05.02.03-4 Doenças Infecciosas de Animais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3231, '095', 'a', 'infecciosas', '5.05.02.03-4 Doenças Infecciosas de Animais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3232, '095', 'a', 'de', '5.05.02.03-4 Doenças Infecciosas de Animais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3233, '095', 'a', 'animais', '5.05.02.03-4 Doenças Infecciosas de Animais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3234, '095', 'a', '5.05.02.04-2', '5.05.02.04-2 Doenças Parasitárias de Animais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3235, '095', 'a', 'doencas', '5.05.02.04-2 Doenças Parasitárias de Animais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3236, '095', 'a', 'parasitarias', '5.05.02.04-2 Doenças Parasitárias de Animais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3237, '095', 'a', 'de', '5.05.02.04-2 Doenças Parasitárias de Animais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3238, '095', 'a', 'animais', '5.05.02.04-2 Doenças Parasitárias de Animais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3239, '095', 'a', '5.05.02.05-0', '5.05.02.05-0 Saúde Animal (Programas Sanitários)', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3240, '095', 'a', 'saude', '5.05.02.05-0 Saúde Animal (Programas Sanitários)', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3241, '095', 'a', 'animal', '5.05.02.05-0 Saúde Animal (Programas Sanitários)', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3242, '095', 'a', '(programas', '5.05.02.05-0 Saúde Animal (Programas Sanitários)', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3243, '095', 'a', 'sanitarios)', '5.05.02.05-0 Saúde Animal (Programas Sanitários)', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3244, '095', 'a', '5.05.03.00-6', '5.05.03.00-6 Patologia Animal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3245, '095', 'a', 'patologia', '5.05.03.00-6 Patologia Animal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3246, '095', 'a', 'animal', '5.05.03.00-6 Patologia Animal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3247, '095', 'a', '5.05.03.01-4', '5.05.03.01-4 Patologia Aviária', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3248, '095', 'a', 'patologia', '5.05.03.01-4 Patologia Aviária', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3249, '095', 'a', 'aviaria', '5.05.03.01-4 Patologia Aviária', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3250, '095', 'a', '5.05.03.02-2', '5.05.03.02-2 Anatomia Patologia Animal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3251, '095', 'a', 'anatomia', '5.05.03.02-2 Anatomia Patologia Animal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3252, '095', 'a', 'patologia', '5.05.03.02-2 Anatomia Patologia Animal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3253, '095', 'a', 'animal', '5.05.03.02-2 Anatomia Patologia Animal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3254, '095', 'a', '5.05.03.03-0', '5.05.03.03-0 Patologia Clínica Animal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3255, '095', 'a', 'patologia', '5.05.03.03-0 Patologia Clínica Animal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3256, '095', 'a', 'clinica', '5.05.03.03-0 Patologia Clínica Animal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3257, '095', 'a', 'animal', '5.05.03.03-0 Patologia Clínica Animal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3258, '095', 'a', '5.05.04.00-2', '5.05.04.00-2 Reprodução Animal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3259, '095', 'a', 'reproducao', '5.05.04.00-2 Reprodução Animal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3260, '095', 'a', 'animal', '5.05.04.00-2 Reprodução Animal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3261, '095', 'a', '5.05.04.01-0', '5.05.04.01-0 Ginecologia e Andrologia Animal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3262, '095', 'a', 'ginecologia', '5.05.04.01-0 Ginecologia e Andrologia Animal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3263, '095', 'a', 'andrologia', '5.05.04.01-0 Ginecologia e Andrologia Animal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3264, '095', 'a', 'animal', '5.05.04.01-0 Ginecologia e Andrologia Animal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3265, '095', 'a', '5.05.04.02-9', '5.05.04.02-9 Inseminação Artificial Animal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3266, '095', 'a', 'inseminacao', '5.05.04.02-9 Inseminação Artificial Animal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3267, '095', 'a', 'artificial', '5.05.04.02-9 Inseminação Artificial Animal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3268, '095', 'a', 'animal', '5.05.04.02-9 Inseminação Artificial Animal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3269, '095', 'a', '5.05.04.03-7', '5.05.04.03-7 Fisiopatologia da Reprodução Animal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3270, '095', 'a', 'fisiopatologia', '5.05.04.03-7 Fisiopatologia da Reprodução Animal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3271, '095', 'a', 'da', '5.05.04.03-7 Fisiopatologia da Reprodução Animal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3272, '095', 'a', 'reproducao', '5.05.04.03-7 Fisiopatologia da Reprodução Animal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3273, '095', 'a', 'animal', '5.05.04.03-7 Fisiopatologia da Reprodução Animal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3274, '095', 'a', '5.05.05.00-9', '5.05.05.00-9 Inspeção de Produtos de Origem Animal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3275, '095', 'a', 'inspecao', '5.05.05.00-9 Inspeção de Produtos de Origem Animal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3276, '095', 'a', 'de', '5.05.05.00-9 Inspeção de Produtos de Origem Animal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3277, '095', 'a', 'produtos', '5.05.05.00-9 Inspeção de Produtos de Origem Animal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3278, '095', 'a', 'de', '5.05.05.00-9 Inspeção de Produtos de Origem Animal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3279, '095', 'a', 'origem', '5.05.05.00-9 Inspeção de Produtos de Origem Animal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3280, '095', 'a', 'animal', '5.05.05.00-9 Inspeção de Produtos de Origem Animal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3281, '095', 'a', '5.06.00.00-1', '5.06.00.00-1 Recursos Pesqueiros e Engenharia de Pesca', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3282, '095', 'a', 'recursos', '5.06.00.00-1 Recursos Pesqueiros e Engenharia de Pesca', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3283, '095', 'a', 'pesqueiros', '5.06.00.00-1 Recursos Pesqueiros e Engenharia de Pesca', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3284, '095', 'a', 'engenharia', '5.06.00.00-1 Recursos Pesqueiros e Engenharia de Pesca', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3285, '095', 'a', 'de', '5.06.00.00-1 Recursos Pesqueiros e Engenharia de Pesca', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3286, '095', 'a', 'pesca', '5.06.00.00-1 Recursos Pesqueiros e Engenharia de Pesca', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3287, '095', 'a', '5.06.01.00-8', '5.06.01.00-8 Recursos Pesqueiros Marinhos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3288, '095', 'a', 'recursos', '5.06.01.00-8 Recursos Pesqueiros Marinhos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3289, '095', 'a', 'pesqueiros', '5.06.01.00-8 Recursos Pesqueiros Marinhos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3290, '095', 'a', 'marinhos', '5.06.01.00-8 Recursos Pesqueiros Marinhos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3291, '095', 'a', '5.06.01.01-6', '5.06.01.01-6 Fatores Abióticos do Mar', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3292, '095', 'a', 'fatores', '5.06.01.01-6 Fatores Abióticos do Mar', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3293, '095', 'a', 'abioticos', '5.06.01.01-6 Fatores Abióticos do Mar', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3294, '095', 'a', 'do', '5.06.01.01-6 Fatores Abióticos do Mar', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3295, '095', 'a', 'mar', '5.06.01.01-6 Fatores Abióticos do Mar', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3296, '095', 'a', '5.06.01.02-4', '5.06.01.02-4 Avaliação de Estoques Pesqueiros Marinhos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3297, '095', 'a', 'avaliacao', '5.06.01.02-4 Avaliação de Estoques Pesqueiros Marinhos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3298, '095', 'a', 'de', '5.06.01.02-4 Avaliação de Estoques Pesqueiros Marinhos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3299, '095', 'a', 'estoques', '5.06.01.02-4 Avaliação de Estoques Pesqueiros Marinhos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3300, '095', 'a', 'pesqueiros', '5.06.01.02-4 Avaliação de Estoques Pesqueiros Marinhos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3301, '095', 'a', 'marinhos', '5.06.01.02-4 Avaliação de Estoques Pesqueiros Marinhos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3302, '095', 'a', '5.06.01.03-2', '5.06.01.03-2 Exploração Pesqueira Marinha', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3303, '095', 'a', 'exploracao', '5.06.01.03-2 Exploração Pesqueira Marinha', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3304, '095', 'a', 'pesqueira', '5.06.01.03-2 Exploração Pesqueira Marinha', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3305, '095', 'a', 'marinha', '5.06.01.03-2 Exploração Pesqueira Marinha', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3306, '095', 'a', '5.06.01.04-0', '5.06.01.04-0 Manejo e Conservação de Recursos Pesqueiros Marinhos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3552, '095', 'a', 'especiais', '6.01.04.00-7 Direitos Especiais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3307, '095', 'a', 'manejo', '5.06.01.04-0 Manejo e Conservação de Recursos Pesqueiros Marinhos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3308, '095', 'a', 'conservacao', '5.06.01.04-0 Manejo e Conservação de Recursos Pesqueiros Marinhos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3309, '095', 'a', 'de', '5.06.01.04-0 Manejo e Conservação de Recursos Pesqueiros Marinhos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3310, '095', 'a', 'recursos', '5.06.01.04-0 Manejo e Conservação de Recursos Pesqueiros Marinhos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3311, '095', 'a', 'pesqueiros', '5.06.01.04-0 Manejo e Conservação de Recursos Pesqueiros Marinhos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3312, '095', 'a', 'marinhos', '5.06.01.04-0 Manejo e Conservação de Recursos Pesqueiros Marinhos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3313, '095', 'a', '5.06.02.00-4', '5.06.02.00-4 Recursos Pesqueiros de Águas Interiores', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3314, '095', 'a', 'recursos', '5.06.02.00-4 Recursos Pesqueiros de Águas Interiores', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3315, '095', 'a', 'pesqueiros', '5.06.02.00-4 Recursos Pesqueiros de Águas Interiores', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3316, '095', 'a', 'de', '5.06.02.00-4 Recursos Pesqueiros de Águas Interiores', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3317, '095', 'a', 'aguas', '5.06.02.00-4 Recursos Pesqueiros de Águas Interiores', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3318, '095', 'a', 'interiores', '5.06.02.00-4 Recursos Pesqueiros de Águas Interiores', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3319, '095', 'a', '5.06.02.01-2', '5.06.02.01-2 Fatores Abióticos de Águas Interiores', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3320, '095', 'a', 'fatores', '5.06.02.01-2 Fatores Abióticos de Águas Interiores', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3321, '095', 'a', 'abioticos', '5.06.02.01-2 Fatores Abióticos de Águas Interiores', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3322, '095', 'a', 'de', '5.06.02.01-2 Fatores Abióticos de Águas Interiores', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3323, '095', 'a', 'aguas', '5.06.02.01-2 Fatores Abióticos de Águas Interiores', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3324, '095', 'a', 'interiores', '5.06.02.01-2 Fatores Abióticos de Águas Interiores', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3325, '095', 'a', '5.06.02.02-0', '5.06.02.02-0 Avaliação de Estoques Pesqueiros de Águas Interiores', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3326, '095', 'a', 'avaliacao', '5.06.02.02-0 Avaliação de Estoques Pesqueiros de Águas Interiores', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3327, '095', 'a', 'de', '5.06.02.02-0 Avaliação de Estoques Pesqueiros de Águas Interiores', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3328, '095', 'a', 'estoques', '5.06.02.02-0 Avaliação de Estoques Pesqueiros de Águas Interiores', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3329, '095', 'a', 'pesqueiros', '5.06.02.02-0 Avaliação de Estoques Pesqueiros de Águas Interiores', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3330, '095', 'a', 'de', '5.06.02.02-0 Avaliação de Estoques Pesqueiros de Águas Interiores', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3331, '095', 'a', 'aguas', '5.06.02.02-0 Avaliação de Estoques Pesqueiros de Águas Interiores', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3332, '095', 'a', 'interiores', '5.06.02.02-0 Avaliação de Estoques Pesqueiros de Águas Interiores', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3333, '095', 'a', '5.06.02.03-9', '5.06.02.03-9 Explotação Pesqueira de Águas Interiores', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3334, '095', 'a', 'explotacao', '5.06.02.03-9 Explotação Pesqueira de Águas Interiores', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3335, '095', 'a', 'pesqueira', '5.06.02.03-9 Explotação Pesqueira de Águas Interiores', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3336, '095', 'a', 'de', '5.06.02.03-9 Explotação Pesqueira de Águas Interiores', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3337, '095', 'a', 'aguas', '5.06.02.03-9 Explotação Pesqueira de Águas Interiores', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3338, '095', 'a', 'interiores', '5.06.02.03-9 Explotação Pesqueira de Águas Interiores', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3339, '095', 'a', '5.06.02.04-7', '5.06.02.04-7 Manejo e Conservação de Recursos Pesqueiros de Águas Interiores', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3340, '095', 'a', 'manejo', '5.06.02.04-7 Manejo e Conservação de Recursos Pesqueiros de Águas Interiores', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3341, '095', 'a', 'conservacao', '5.06.02.04-7 Manejo e Conservação de Recursos Pesqueiros de Águas Interiores', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3342, '095', 'a', 'de', '5.06.02.04-7 Manejo e Conservação de Recursos Pesqueiros de Águas Interiores', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3343, '095', 'a', 'recursos', '5.06.02.04-7 Manejo e Conservação de Recursos Pesqueiros de Águas Interiores', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3344, '095', 'a', 'pesqueiros', '5.06.02.04-7 Manejo e Conservação de Recursos Pesqueiros de Águas Interiores', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3345, '095', 'a', 'de', '5.06.02.04-7 Manejo e Conservação de Recursos Pesqueiros de Águas Interiores', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3346, '095', 'a', 'aguas', '5.06.02.04-7 Manejo e Conservação de Recursos Pesqueiros de Águas Interiores', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3347, '095', 'a', 'interiores', '5.06.02.04-7 Manejo e Conservação de Recursos Pesqueiros de Águas Interiores', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3348, '095', 'a', '5.06.03.00-0', '5.06.03.00-0 Aqüicultura', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3349, '095', 'a', 'aquicultura', '5.06.03.00-0 Aqüicultura', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3350, '095', 'a', '5.06.03.01-9', '5.06.03.01-9 Maricultura', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3351, '095', 'a', 'maricultura', '5.06.03.01-9 Maricultura', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3352, '095', 'a', '5.06.03.02-7', '5.06.03.02-7 Carcinocultura', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3353, '095', 'a', 'carcinocultura', '5.06.03.02-7 Carcinocultura', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3354, '095', 'a', '5.06.03.03-5', '5.06.03.03-5 Ostreicultura', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3355, '095', 'a', 'ostreicultura', '5.06.03.03-5 Ostreicultura', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3356, '095', 'a', '5.06.03.04-3', '5.06.03.04-3 Piscicultura', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3357, '095', 'a', 'piscicultura', '5.06.03.04-3 Piscicultura', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3358, '095', 'a', '5.06.04.00-7', '5.06.04.00-7 Engenharia de Pesca', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3359, '095', 'a', 'engenharia', '5.06.04.00-7 Engenharia de Pesca', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3360, '095', 'a', 'de', '5.06.04.00-7 Engenharia de Pesca', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3361, '095', 'a', 'pesca', '5.06.04.00-7 Engenharia de Pesca', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3362, '095', 'a', '5.07.00.00-6', '5.07.00.00-6 Ciência e Tecnologia de Alimentos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3363, '095', 'a', 'ciencia', '5.07.00.00-6 Ciência e Tecnologia de Alimentos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3364, '095', 'a', 'tecnologia', '5.07.00.00-6 Ciência e Tecnologia de Alimentos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3365, '095', 'a', 'de', '5.07.00.00-6 Ciência e Tecnologia de Alimentos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3366, '095', 'a', 'alimentos', '5.07.00.00-6 Ciência e Tecnologia de Alimentos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3367, '095', 'a', '5.07.01.00-2', '5.07.01.00-2 Ciência de Alimentos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3368, '095', 'a', 'ciencia', '5.07.01.00-2 Ciência de Alimentos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3369, '095', 'a', 'de', '5.07.01.00-2 Ciência de Alimentos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3370, '095', 'a', 'alimentos', '5.07.01.00-2 Ciência de Alimentos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3371, '095', 'a', '5.07.01.01-0', '5.07.01.01-0 Valor Nutritivo de Alimentos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3372, '095', 'a', 'valor', '5.07.01.01-0 Valor Nutritivo de Alimentos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3373, '095', 'a', 'nutritivo', '5.07.01.01-0 Valor Nutritivo de Alimentos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3374, '095', 'a', 'de', '5.07.01.01-0 Valor Nutritivo de Alimentos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3375, '095', 'a', 'alimentos', '5.07.01.01-0 Valor Nutritivo de Alimentos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3376, '095', 'a', '5.07.01.02-9', '5.07.01.02-9 Química, Física, Físico-Química e Bioquímica dos Alim. e das Mat.-Primas Alimentares', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3377, '095', 'a', 'quimica,', '5.07.01.02-9 Química, Física, Físico-Química e Bioquímica dos Alim. e das Mat.-Primas Alimentares', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3378, '095', 'a', 'fisica,', '5.07.01.02-9 Química, Física, Físico-Química e Bioquímica dos Alim. e das Mat.-Primas Alimentares', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3379, '095', 'a', 'fisico-quimica', '5.07.01.02-9 Química, Física, Físico-Química e Bioquímica dos Alim. e das Mat.-Primas Alimentares', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3380, '095', 'a', 'bioquimica', '5.07.01.02-9 Química, Física, Físico-Química e Bioquímica dos Alim. e das Mat.-Primas Alimentares', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3381, '095', 'a', 'dos', '5.07.01.02-9 Química, Física, Físico-Química e Bioquímica dos Alim. e das Mat.-Primas Alimentares', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4969, '095', 'a', 'musica', '8.03.03.00-5 Música', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3382, '095', 'a', 'alim.', '5.07.01.02-9 Química, Física, Físico-Química e Bioquímica dos Alim. e das Mat.-Primas Alimentares', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3383, '095', 'a', 'das', '5.07.01.02-9 Química, Física, Físico-Química e Bioquímica dos Alim. e das Mat.-Primas Alimentares', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3384, '095', 'a', 'mat.-primas', '5.07.01.02-9 Química, Física, Físico-Química e Bioquímica dos Alim. e das Mat.-Primas Alimentares', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3385, '095', 'a', 'alimentares', '5.07.01.02-9 Química, Física, Físico-Química e Bioquímica dos Alim. e das Mat.-Primas Alimentares', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3386, '095', 'a', '5.07.01.03-7', '5.07.01.03-7 Microbiologia de Alimentos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3387, '095', 'a', 'microbiologia', '5.07.01.03-7 Microbiologia de Alimentos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3388, '095', 'a', 'de', '5.07.01.03-7 Microbiologia de Alimentos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3389, '095', 'a', 'alimentos', '5.07.01.03-7 Microbiologia de Alimentos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3390, '095', 'a', '5.07.01.04-5', '5.07.01.04-5 Fisiologia Pós-Colheita', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3391, '095', 'a', 'fisiologia', '5.07.01.04-5 Fisiologia Pós-Colheita', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3392, '095', 'a', 'pos-colheita', '5.07.01.04-5 Fisiologia Pós-Colheita', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3393, '095', 'a', '5.07.01.05-3', '5.07.01.05-3 Toxicidade e Resíduos de Pesticidas em Alimentos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3394, '095', 'a', 'toxicidade', '5.07.01.05-3 Toxicidade e Resíduos de Pesticidas em Alimentos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3395, '095', 'a', 'residuos', '5.07.01.05-3 Toxicidade e Resíduos de Pesticidas em Alimentos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3396, '095', 'a', 'de', '5.07.01.05-3 Toxicidade e Resíduos de Pesticidas em Alimentos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3397, '095', 'a', 'pesticidas', '5.07.01.05-3 Toxicidade e Resíduos de Pesticidas em Alimentos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3398, '095', 'a', 'em', '5.07.01.05-3 Toxicidade e Resíduos de Pesticidas em Alimentos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3399, '095', 'a', 'alimentos', '5.07.01.05-3 Toxicidade e Resíduos de Pesticidas em Alimentos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3400, '095', 'a', '5.07.01.06-1', '5.07.01.06-1 Avaliação e Controle de Qualidade de Alimentos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3401, '095', 'a', 'avaliacao', '5.07.01.06-1 Avaliação e Controle de Qualidade de Alimentos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3402, '095', 'a', 'controle', '5.07.01.06-1 Avaliação e Controle de Qualidade de Alimentos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3403, '095', 'a', 'de', '5.07.01.06-1 Avaliação e Controle de Qualidade de Alimentos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3404, '095', 'a', 'qualidade', '5.07.01.06-1 Avaliação e Controle de Qualidade de Alimentos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3405, '095', 'a', 'de', '5.07.01.06-1 Avaliação e Controle de Qualidade de Alimentos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3406, '095', 'a', 'alimentos', '5.07.01.06-1 Avaliação e Controle de Qualidade de Alimentos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3407, '095', 'a', '5.07.01.07-0', '5.07.01.07-0 Padrões, Legislação e Fiscalização de Alimentos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3408, '095', 'a', 'padroes,', '5.07.01.07-0 Padrões, Legislação e Fiscalização de Alimentos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3409, '095', 'a', 'legislacao', '5.07.01.07-0 Padrões, Legislação e Fiscalização de Alimentos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3410, '095', 'a', 'fiscalizacao', '5.07.01.07-0 Padrões, Legislação e Fiscalização de Alimentos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3411, '095', 'a', 'de', '5.07.01.07-0 Padrões, Legislação e Fiscalização de Alimentos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3412, '095', 'a', 'alimentos', '5.07.01.07-0 Padrões, Legislação e Fiscalização de Alimentos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3413, '095', 'a', '5.07.02.00-9', '5.07.02.00-9 Tecnologia de Alimentos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3414, '095', 'a', 'tecnologia', '5.07.02.00-9 Tecnologia de Alimentos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3415, '095', 'a', 'de', '5.07.02.00-9 Tecnologia de Alimentos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3416, '095', 'a', 'alimentos', '5.07.02.00-9 Tecnologia de Alimentos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3417, '095', 'a', '5.07.02.01-7', '5.07.02.01-7 Tecnologia de Produtos de Origem Animal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3418, '095', 'a', 'tecnologia', '5.07.02.01-7 Tecnologia de Produtos de Origem Animal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3419, '095', 'a', 'de', '5.07.02.01-7 Tecnologia de Produtos de Origem Animal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3420, '095', 'a', 'produtos', '5.07.02.01-7 Tecnologia de Produtos de Origem Animal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3421, '095', 'a', 'de', '5.07.02.01-7 Tecnologia de Produtos de Origem Animal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3422, '095', 'a', 'origem', '5.07.02.01-7 Tecnologia de Produtos de Origem Animal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3423, '095', 'a', 'animal', '5.07.02.01-7 Tecnologia de Produtos de Origem Animal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3424, '095', 'a', '5.07.02.02-5', '5.07.02.02-5 Tecnologia de Produtos de Origem Vegetal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3425, '095', 'a', 'tecnologia', '5.07.02.02-5 Tecnologia de Produtos de Origem Vegetal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3426, '095', 'a', 'de', '5.07.02.02-5 Tecnologia de Produtos de Origem Vegetal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3427, '095', 'a', 'produtos', '5.07.02.02-5 Tecnologia de Produtos de Origem Vegetal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3428, '095', 'a', 'de', '5.07.02.02-5 Tecnologia de Produtos de Origem Vegetal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3429, '095', 'a', 'origem', '5.07.02.02-5 Tecnologia de Produtos de Origem Vegetal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3430, '095', 'a', 'vegetal', '5.07.02.02-5 Tecnologia de Produtos de Origem Vegetal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3431, '095', 'a', '5.07.02.03-3', '5.07.02.03-3 Tecnologia das Bebidas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3432, '095', 'a', 'tecnologia', '5.07.02.03-3 Tecnologia das Bebidas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3433, '095', 'a', 'das', '5.07.02.03-3 Tecnologia das Bebidas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3434, '095', 'a', 'bebidas', '5.07.02.03-3 Tecnologia das Bebidas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3435, '095', 'a', '5.07.02.04-1', '5.07.02.04-1 Tecnologia de Alimentos Dietéticos e Nutricionais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3436, '095', 'a', 'tecnologia', '5.07.02.04-1 Tecnologia de Alimentos Dietéticos e Nutricionais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3437, '095', 'a', 'de', '5.07.02.04-1 Tecnologia de Alimentos Dietéticos e Nutricionais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3438, '095', 'a', 'alimentos', '5.07.02.04-1 Tecnologia de Alimentos Dietéticos e Nutricionais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3439, '095', 'a', 'dieteticos', '5.07.02.04-1 Tecnologia de Alimentos Dietéticos e Nutricionais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3440, '095', 'a', 'nutricionais', '5.07.02.04-1 Tecnologia de Alimentos Dietéticos e Nutricionais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3441, '095', 'a', '5.07.02.05-0', '5.07.02.05-0 Aproveitamento de Subprodutos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3442, '095', 'a', 'aproveitamento', '5.07.02.05-0 Aproveitamento de Subprodutos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3443, '095', 'a', 'de', '5.07.02.05-0 Aproveitamento de Subprodutos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3444, '095', 'a', 'subprodutos', '5.07.02.05-0 Aproveitamento de Subprodutos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3445, '095', 'a', '5.07.02.06-8', '5.07.02.06-8 Embalagens de Produtos Alimentares', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3446, '095', 'a', 'embalagens', '5.07.02.06-8 Embalagens de Produtos Alimentares', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3447, '095', 'a', 'de', '5.07.02.06-8 Embalagens de Produtos Alimentares', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3448, '095', 'a', 'produtos', '5.07.02.06-8 Embalagens de Produtos Alimentares', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3449, '095', 'a', 'alimentares', '5.07.02.06-8 Embalagens de Produtos Alimentares', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3450, '095', 'a', '5.07.03.00-5', '5.07.03.00-5 Engenharia de Alimentos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3451, '095', 'a', 'engenharia', '5.07.03.00-5 Engenharia de Alimentos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3452, '095', 'a', 'de', '5.07.03.00-5 Engenharia de Alimentos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3453, '095', 'a', 'alimentos', '5.07.03.00-5 Engenharia de Alimentos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3454, '095', 'a', '5.07.03.01-3', '5.07.03.01-3 Instalações Industriais de Produção de Alimentos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3455, '095', 'a', 'instalacoes', '5.07.03.01-3 Instalações Industriais de Produção de Alimentos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3456, '095', 'a', 'industriais', '5.07.03.01-3 Instalações Industriais de Produção de Alimentos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3457, '095', 'a', 'de', '5.07.03.01-3 Instalações Industriais de Produção de Alimentos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3458, '095', 'a', 'producao', '5.07.03.01-3 Instalações Industriais de Produção de Alimentos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3459, '095', 'a', 'de', '5.07.03.01-3 Instalações Industriais de Produção de Alimentos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3460, '095', 'a', 'alimentos', '5.07.03.01-3 Instalações Industriais de Produção de Alimentos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3461, '095', 'a', '5.07.03.02-1', '5.07.03.02-1 Armazenamento de Alimentos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3462, '095', 'a', 'armazenamento', '5.07.03.02-1 Armazenamento de Alimentos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3463, '095', 'a', 'de', '5.07.03.02-1 Armazenamento de Alimentos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3464, '095', 'a', 'alimentos', '5.07.03.02-1 Armazenamento de Alimentos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3465, '095', 'a', '6.00.00.00-7', '6.00.00.00-7 Ciências Sociais Aplicadas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3466, '095', 'a', 'ciencias', '6.00.00.00-7 Ciências Sociais Aplicadas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3467, '095', 'a', 'sociais', '6.00.00.00-7 Ciências Sociais Aplicadas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3468, '095', 'a', 'aplicadas', '6.00.00.00-7 Ciências Sociais Aplicadas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3469, '095', 'a', '6.01.00.00-1', '6.01.00.00-1 Direito', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3470, '095', 'a', 'direito', '6.01.00.00-1 Direito', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3471, '095', 'a', '6.01.01.00-8', '6.01.01.00-8 Teoria do Direito', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3472, '095', 'a', 'teoria', '6.01.01.00-8 Teoria do Direito', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3473, '095', 'a', 'do', '6.01.01.00-8 Teoria do Direito', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3474, '095', 'a', 'direito', '6.01.01.00-8 Teoria do Direito', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3475, '095', 'a', '6.01.01.01-6', '6.01.01.01-6 Teoria Geral do Direito', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3476, '095', 'a', 'teoria', '6.01.01.01-6 Teoria Geral do Direito', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3477, '095', 'a', 'geral', '6.01.01.01-6 Teoria Geral do Direito', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3478, '095', 'a', 'do', '6.01.01.01-6 Teoria Geral do Direito', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3479, '095', 'a', 'direito', '6.01.01.01-6 Teoria Geral do Direito', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3480, '095', 'a', '6.01.01.02-4', '6.01.01.02-4 Teoria Geral do Processo', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3481, '095', 'a', 'teoria', '6.01.01.02-4 Teoria Geral do Processo', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3482, '095', 'a', 'geral', '6.01.01.02-4 Teoria Geral do Processo', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3483, '095', 'a', 'do', '6.01.01.02-4 Teoria Geral do Processo', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3484, '095', 'a', 'processo', '6.01.01.02-4 Teoria Geral do Processo', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3485, '095', 'a', '6.01.01.03-2', '6.01.01.03-2 Teoria do Estado', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3486, '095', 'a', 'teoria', '6.01.01.03-2 Teoria do Estado', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3487, '095', 'a', 'do', '6.01.01.03-2 Teoria do Estado', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3488, '095', 'a', 'estado', '6.01.01.03-2 Teoria do Estado', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3489, '095', 'a', '6.01.01.04-0', '6.01.01.04-0 História do Direito', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3490, '095', 'a', 'historia', '6.01.01.04-0 História do Direito', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3491, '095', 'a', 'do', '6.01.01.04-0 História do Direito', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3492, '095', 'a', 'direito', '6.01.01.04-0 História do Direito', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3493, '095', 'a', '6.01.01.05-9', '6.01.01.05-9 Filosofia do Direito', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3494, '095', 'a', 'filosofia', '6.01.01.05-9 Filosofia do Direito', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3495, '095', 'a', 'do', '6.01.01.05-9 Filosofia do Direito', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3496, '095', 'a', 'direito', '6.01.01.05-9 Filosofia do Direito', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3497, '095', 'a', '6.01.01.06-7', '6.01.01.06-7 Lógica Jurídica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3498, '095', 'a', 'logica', '6.01.01.06-7 Lógica Jurídica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3499, '095', 'a', 'juridica', '6.01.01.06-7 Lógica Jurídica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3500, '095', 'a', '6.01.01.07-5', '6.01.01.07-5 Sociologia Jurídica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3501, '095', 'a', 'sociologia', '6.01.01.07-5 Sociologia Jurídica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3502, '095', 'a', 'juridica', '6.01.01.07-5 Sociologia Jurídica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3503, '095', 'a', '6.01.01.08-3', '6.01.01.08-3 Antropologia Jurídica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3504, '095', 'a', 'antropologia', '6.01.01.08-3 Antropologia Jurídica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3505, '095', 'a', 'juridica', '6.01.01.08-3 Antropologia Jurídica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3506, '095', 'a', '6.01.02.00-4', '6.01.02.00-4 Direito Público', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3507, '095', 'a', 'direito', '6.01.02.00-4 Direito Público', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3508, '095', 'a', 'publico', '6.01.02.00-4 Direito Público', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3509, '095', 'a', '6.01.02.01-2', '6.01.02.01-2 Direito Tributário', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3510, '095', 'a', 'direito', '6.01.02.01-2 Direito Tributário', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3511, '095', 'a', 'tributario', '6.01.02.01-2 Direito Tributário', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3512, '095', 'a', '6.01.02.02-0', '6.01.02.02-0 Direito Penal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3513, '095', 'a', 'direito', '6.01.02.02-0 Direito Penal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3514, '095', 'a', 'penal', '6.01.02.02-0 Direito Penal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3515, '095', 'a', '6.01.02.03-9', '6.01.02.03-9 Direito Processual Penal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3516, '095', 'a', 'direito', '6.01.02.03-9 Direito Processual Penal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3517, '095', 'a', 'processual', '6.01.02.03-9 Direito Processual Penal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3518, '095', 'a', 'penal', '6.01.02.03-9 Direito Processual Penal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3519, '095', 'a', '6.01.02.04-7', '6.01.02.04-7 Direito Processual Civil', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3520, '095', 'a', 'direito', '6.01.02.04-7 Direito Processual Civil', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3521, '095', 'a', 'processual', '6.01.02.04-7 Direito Processual Civil', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3522, '095', 'a', 'civil', '6.01.02.04-7 Direito Processual Civil', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3523, '095', 'a', '6.01.02.05-5', '6.01.02.05-5 Direito Constitucional', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3524, '095', 'a', 'direito', '6.01.02.05-5 Direito Constitucional', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3525, '095', 'a', 'constitucional', '6.01.02.05-5 Direito Constitucional', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3526, '095', 'a', '6.01.02.06-3', '6.01.02.06-3 Direito Administrativo', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3527, '095', 'a', 'direito', '6.01.02.06-3 Direito Administrativo', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3528, '095', 'a', 'administrativo', '6.01.02.06-3 Direito Administrativo', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3529, '095', 'a', '6.01.02.07-1', '6.01.02.07-1 Direito Internacional Público', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3530, '095', 'a', 'direito', '6.01.02.07-1 Direito Internacional Público', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3531, '095', 'a', 'internacional', '6.01.02.07-1 Direito Internacional Público', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3532, '095', 'a', 'publico', '6.01.02.07-1 Direito Internacional Público', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3533, '095', 'a', '6.01.03.00-0', '6.01.03.00-0 Direito Privado', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3534, '095', 'a', 'direito', '6.01.03.00-0 Direito Privado', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3535, '095', 'a', 'privado', '6.01.03.00-0 Direito Privado', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3536, '095', 'a', '6.01.03.01-9', '6.01.03.01-9 Direito Civil', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3537, '095', 'a', 'direito', '6.01.03.01-9 Direito Civil', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3538, '095', 'a', 'civil', '6.01.03.01-9 Direito Civil', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3539, '095', 'a', '6.01.03.02-7', '6.01.03.02-7 Direito Comercial', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3540, '095', 'a', 'direito', '6.01.03.02-7 Direito Comercial', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3541, '095', 'a', 'comercial', '6.01.03.02-7 Direito Comercial', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3542, '095', 'a', '6.01.03.03-5', '6.01.03.03-5 Direito do Trabalho', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3543, '095', 'a', 'direito', '6.01.03.03-5 Direito do Trabalho', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3544, '095', 'a', 'do', '6.01.03.03-5 Direito do Trabalho', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3545, '095', 'a', 'trabalho', '6.01.03.03-5 Direito do Trabalho', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3546, '095', 'a', '6.01.03.04-3', '6.01.03.04-3 Direito Internacional Privado', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3547, '095', 'a', 'direito', '6.01.03.04-3 Direito Internacional Privado', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3548, '095', 'a', 'internacional', '6.01.03.04-3 Direito Internacional Privado', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3549, '095', 'a', 'privado', '6.01.03.04-3 Direito Internacional Privado', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3550, '095', 'a', '6.01.04.00-7', '6.01.04.00-7 Direitos Especiais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3551, '095', 'a', 'direitos', '6.01.04.00-7 Direitos Especiais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3553, '095', 'a', '6.02.00.00-6', '6.02.00.00-6 Administração', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3554, '095', 'a', 'administracao', '6.02.00.00-6 Administração', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3555, '095', 'a', '6.02.01.00-2', '6.02.01.00-2 Administração de Empresas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3556, '095', 'a', 'administracao', '6.02.01.00-2 Administração de Empresas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3557, '095', 'a', 'de', '6.02.01.00-2 Administração de Empresas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3558, '095', 'a', 'empresas', '6.02.01.00-2 Administração de Empresas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3559, '095', 'a', '6.02.01.01-0', '6.02.01.01-0 Administração da Produção', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3560, '095', 'a', 'administracao', '6.02.01.01-0 Administração da Produção', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3561, '095', 'a', 'da', '6.02.01.01-0 Administração da Produção', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3562, '095', 'a', 'producao', '6.02.01.01-0 Administração da Produção', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3563, '095', 'a', '6.02.01.02-9', '6.02.01.02-9 Administração Financeira', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3564, '095', 'a', 'administracao', '6.02.01.02-9 Administração Financeira', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3565, '095', 'a', 'financeira', '6.02.01.02-9 Administração Financeira', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3566, '095', 'a', '6.02.01.03-7', '6.02.01.03-7 Mercadologia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3567, '095', 'a', 'mercadologia', '6.02.01.03-7 Mercadologia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3568, '095', 'a', '6.02.01.04-5', '6.02.01.04-5 Negócios Internacionais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3569, '095', 'a', 'negocios', '6.02.01.04-5 Negócios Internacionais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3570, '095', 'a', 'internacionais', '6.02.01.04-5 Negócios Internacionais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3571, '095', 'a', '6.02.01.05-3', '6.02.01.05-3 Administração de Recursos Humanos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3572, '095', 'a', 'administracao', '6.02.01.05-3 Administração de Recursos Humanos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3573, '095', 'a', 'de', '6.02.01.05-3 Administração de Recursos Humanos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3574, '095', 'a', 'recursos', '6.02.01.05-3 Administração de Recursos Humanos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3575, '095', 'a', 'humanos', '6.02.01.05-3 Administração de Recursos Humanos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3576, '095', 'a', '6.02.02.00-9', '6.02.02.00-9 Administração Pública', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3577, '095', 'a', 'administracao', '6.02.02.00-9 Administração Pública', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3578, '095', 'a', 'publica', '6.02.02.00-9 Administração Pública', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3579, '095', 'a', '6.02.02.01-7', '6.02.02.01-7 Contabilidade e Financas Públicas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3580, '095', 'a', 'contabilidade', '6.02.02.01-7 Contabilidade e Financas Públicas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3581, '095', 'a', 'financas', '6.02.02.01-7 Contabilidade e Financas Públicas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3582, '095', 'a', 'publicas', '6.02.02.01-7 Contabilidade e Financas Públicas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3583, '095', 'a', '6.02.02.02-5', '6.02.02.02-5 Organizações Públicas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3584, '095', 'a', 'organizacoes', '6.02.02.02-5 Organizações Públicas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3585, '095', 'a', 'publicas', '6.02.02.02-5 Organizações Públicas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3586, '095', 'a', '6.02.02.03-3', '6.02.02.03-3 Política e Planejamento Governamentais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3587, '095', 'a', 'politica', '6.02.02.03-3 Política e Planejamento Governamentais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3588, '095', 'a', 'planejamento', '6.02.02.03-3 Política e Planejamento Governamentais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3589, '095', 'a', 'governamentais', '6.02.02.03-3 Política e Planejamento Governamentais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3590, '095', 'a', '6.02.02.04-1', '6.02.02.04-1 Administração de Pessoal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3591, '095', 'a', 'administracao', '6.02.02.04-1 Administração de Pessoal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3592, '095', 'a', 'de', '6.02.02.04-1 Administração de Pessoal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3593, '095', 'a', 'pessoal', '6.02.02.04-1 Administração de Pessoal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3594, '095', 'a', '6.02.03.00-5', '6.02.03.00-5 Administração de Setores Específicos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3595, '095', 'a', 'administracao', '6.02.03.00-5 Administração de Setores Específicos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3596, '095', 'a', 'de', '6.02.03.00-5 Administração de Setores Específicos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3597, '095', 'a', 'setores', '6.02.03.00-5 Administração de Setores Específicos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3598, '095', 'a', 'especificos', '6.02.03.00-5 Administração de Setores Específicos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3599, '095', 'a', '6.02.04.00-1', '6.02.04.00-1 Ciências Contábeis', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3600, '095', 'a', 'ciencias', '6.02.04.00-1 Ciências Contábeis', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3601, '095', 'a', 'contabeis', '6.02.04.00-1 Ciências Contábeis', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3602, '095', 'a', '6.03.00.00-0', '6.03.00.00-0 Economia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3603, '095', 'a', 'economia', '6.03.00.00-0 Economia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3604, '095', 'a', '6.03.01.00-7', '6.03.01.00-7 Teoria Econômica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3605, '095', 'a', 'teoria', '6.03.01.00-7 Teoria Econômica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3606, '095', 'a', 'economica', '6.03.01.00-7 Teoria Econômica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3607, '095', 'a', '6.03.01.01-5', '6.03.01.01-5 Economia Geral', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3608, '095', 'a', 'economia', '6.03.01.01-5 Economia Geral', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3609, '095', 'a', 'geral', '6.03.01.01-5 Economia Geral', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3610, '095', 'a', '6.03.01.02-3', '6.03.01.02-3 Teoria Geral da Economia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3611, '095', 'a', 'teoria', '6.03.01.02-3 Teoria Geral da Economia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3612, '095', 'a', 'geral', '6.03.01.02-3 Teoria Geral da Economia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3613, '095', 'a', 'da', '6.03.01.02-3 Teoria Geral da Economia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3614, '095', 'a', 'economia', '6.03.01.02-3 Teoria Geral da Economia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3615, '095', 'a', '6.03.01.03-1', '6.03.01.03-1 História do Pensamento Econômico', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3616, '095', 'a', 'historia', '6.03.01.03-1 História do Pensamento Econômico', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3617, '095', 'a', 'do', '6.03.01.03-1 História do Pensamento Econômico', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3618, '095', 'a', 'pensamento', '6.03.01.03-1 História do Pensamento Econômico', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3619, '095', 'a', 'economico', '6.03.01.03-1 História do Pensamento Econômico', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3620, '095', 'a', '6.03.01.04-0', '6.03.01.04-0 História Econômica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3621, '095', 'a', 'historia', '6.03.01.04-0 História Econômica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3622, '095', 'a', 'economica', '6.03.01.04-0 História Econômica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3623, '095', 'a', '6.03.01.05-8', '6.03.01.05-8 Sistemas Econômicos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3624, '095', 'a', 'sistemas', '6.03.01.05-8 Sistemas Econômicos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3625, '095', 'a', 'economicos', '6.03.01.05-8 Sistemas Econômicos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3626, '095', 'a', '6.03.02.00-3', '6.03.02.00-3 Métodos Quantitativos em Economia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3627, '095', 'a', 'metodos', '6.03.02.00-3 Métodos Quantitativos em Economia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3628, '095', 'a', 'quantitativos', '6.03.02.00-3 Métodos Quantitativos em Economia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3629, '095', 'a', 'em', '6.03.02.00-3 Métodos Quantitativos em Economia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3630, '095', 'a', 'economia', '6.03.02.00-3 Métodos Quantitativos em Economia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3631, '095', 'a', '6.03.02.01-1', '6.03.02.01-1 Métodos e Modelos Matemáticos, Econométricos e Estatísticos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3632, '095', 'a', 'metodos', '6.03.02.01-1 Métodos e Modelos Matemáticos, Econométricos e Estatísticos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3633, '095', 'a', 'modelos', '6.03.02.01-1 Métodos e Modelos Matemáticos, Econométricos e Estatísticos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3634, '095', 'a', 'matematicos,', '6.03.02.01-1 Métodos e Modelos Matemáticos, Econométricos e Estatísticos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3635, '095', 'a', 'econometricos', '6.03.02.01-1 Métodos e Modelos Matemáticos, Econométricos e Estatísticos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3636, '095', 'a', 'estatisticos', '6.03.02.01-1 Métodos e Modelos Matemáticos, Econométricos e Estatísticos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3637, '095', 'a', '6.03.02.02-0', '6.03.02.02-0 Estatística Sócio-Econômica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3638, '095', 'a', 'estatistica', '6.03.02.02-0 Estatística Sócio-Econômica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3639, '095', 'a', 'socio-economica', '6.03.02.02-0 Estatística Sócio-Econômica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3640, '095', 'a', '6.03.02.03-8', '6.03.02.03-8 Contabilidade Nacional', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3641, '095', 'a', 'contabilidade', '6.03.02.03-8 Contabilidade Nacional', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3642, '095', 'a', 'nacional', '6.03.02.03-8 Contabilidade Nacional', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3643, '095', 'a', '6.03.02.04-6', '6.03.02.04-6 Economia Matemática', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3644, '095', 'a', 'economia', '6.03.02.04-6 Economia Matemática', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3645, '095', 'a', 'matematica', '6.03.02.04-6 Economia Matemática', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3646, '095', 'a', '6.03.03.00-0', '6.03.03.00-0 Economia Monetária e Fiscal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3647, '095', 'a', 'economia', '6.03.03.00-0 Economia Monetária e Fiscal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3648, '095', 'a', 'monetaria', '6.03.03.00-0 Economia Monetária e Fiscal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3649, '095', 'a', 'fiscal', '6.03.03.00-0 Economia Monetária e Fiscal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3650, '095', 'a', '6.03.03.01-8', '6.03.03.01-8 Teoria Monetária e Financeira', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3651, '095', 'a', 'teoria', '6.03.03.01-8 Teoria Monetária e Financeira', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3652, '095', 'a', 'monetaria', '6.03.03.01-8 Teoria Monetária e Financeira', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3653, '095', 'a', 'financeira', '6.03.03.01-8 Teoria Monetária e Financeira', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3654, '095', 'a', '6.03.03.02-6', '6.03.03.02-6 Instituições Monetárias e Financeiras do Brasil', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3655, '095', 'a', 'instituicoes', '6.03.03.02-6 Instituições Monetárias e Financeiras do Brasil', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3656, '095', 'a', 'monetarias', '6.03.03.02-6 Instituições Monetárias e Financeiras do Brasil', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3657, '095', 'a', 'financeiras', '6.03.03.02-6 Instituições Monetárias e Financeiras do Brasil', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3658, '095', 'a', 'do', '6.03.03.02-6 Instituições Monetárias e Financeiras do Brasil', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3659, '095', 'a', 'brasil', '6.03.03.02-6 Instituições Monetárias e Financeiras do Brasil', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3660, '095', 'a', '6.03.03.03-4', '6.03.03.03-4 Financas Públicas Internas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3661, '095', 'a', 'financas', '6.03.03.03-4 Financas Públicas Internas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3662, '095', 'a', 'publicas', '6.03.03.03-4 Financas Públicas Internas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3663, '095', 'a', 'internas', '6.03.03.03-4 Financas Públicas Internas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3664, '095', 'a', '6.03.03.04-2', '6.03.03.04-2 Política Fiscal do Brasil', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3665, '095', 'a', 'politica', '6.03.03.04-2 Política Fiscal do Brasil', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3666, '095', 'a', 'fiscal', '6.03.03.04-2 Política Fiscal do Brasil', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3667, '095', 'a', 'do', '6.03.03.04-2 Política Fiscal do Brasil', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3668, '095', 'a', 'brasil', '6.03.03.04-2 Política Fiscal do Brasil', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3669, '095', 'a', '6.03.04.00-6', '6.03.04.00-6 Crescimento, Flutuações e Planejamento Econômico', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3670, '095', 'a', 'crescimento,', '6.03.04.00-6 Crescimento, Flutuações e Planejamento Econômico', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3671, '095', 'a', 'flutuacoes', '6.03.04.00-6 Crescimento, Flutuações e Planejamento Econômico', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3672, '095', 'a', 'planejamento', '6.03.04.00-6 Crescimento, Flutuações e Planejamento Econômico', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3673, '095', 'a', 'economico', '6.03.04.00-6 Crescimento, Flutuações e Planejamento Econômico', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3674, '095', 'a', '6.03.04.01-4', '6.03.04.01-4 Crescimento e Desenvolvimento Econômico', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3675, '095', 'a', 'crescimento', '6.03.04.01-4 Crescimento e Desenvolvimento Econômico', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3676, '095', 'a', 'desenvolvimento', '6.03.04.01-4 Crescimento e Desenvolvimento Econômico', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3677, '095', 'a', 'economico', '6.03.04.01-4 Crescimento e Desenvolvimento Econômico', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3678, '095', 'a', '6.03.04.02-2', '6.03.04.02-2 Teoria e Política de Planejamento Econômico', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3679, '095', 'a', 'teoria', '6.03.04.02-2 Teoria e Política de Planejamento Econômico', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3680, '095', 'a', 'politica', '6.03.04.02-2 Teoria e Política de Planejamento Econômico', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3681, '095', 'a', 'de', '6.03.04.02-2 Teoria e Política de Planejamento Econômico', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3682, '095', 'a', 'planejamento', '6.03.04.02-2 Teoria e Política de Planejamento Econômico', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3683, '095', 'a', 'economico', '6.03.04.02-2 Teoria e Política de Planejamento Econômico', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3684, '095', 'a', '6.03.04.03-0', '6.03.04.03-0 Flutuações Cíclicas e Projeções Econômicas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3685, '095', 'a', 'flutuacoes', '6.03.04.03-0 Flutuações Cíclicas e Projeções Econômicas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3686, '095', 'a', 'ciclicas', '6.03.04.03-0 Flutuações Cíclicas e Projeções Econômicas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3687, '095', 'a', 'projecoes', '6.03.04.03-0 Flutuações Cíclicas e Projeções Econômicas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3688, '095', 'a', 'economicas', '6.03.04.03-0 Flutuações Cíclicas e Projeções Econômicas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3689, '095', 'a', '6.03.04.04-9', '6.03.04.04-9 Inflação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3690, '095', 'a', 'inflacao', '6.03.04.04-9 Inflação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3691, '095', 'a', '6.03.05.00-2', '6.03.05.00-2 Economia Internacional', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3692, '095', 'a', 'economia', '6.03.05.00-2 Economia Internacional', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3693, '095', 'a', 'internacional', '6.03.05.00-2 Economia Internacional', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3694, '095', 'a', '6.03.05.01-0', '6.03.05.01-0 Teoria do Comércio Internacional', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3695, '095', 'a', 'teoria', '6.03.05.01-0 Teoria do Comércio Internacional', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3696, '095', 'a', 'do', '6.03.05.01-0 Teoria do Comércio Internacional', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3697, '095', 'a', 'comercio', '6.03.05.01-0 Teoria do Comércio Internacional', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3698, '095', 'a', 'internacional', '6.03.05.01-0 Teoria do Comércio Internacional', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3699, '095', 'a', '6.03.05.02-9', '6.03.05.02-9 Relações do Comércio; Política Comercial; Integração Econômica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3700, '095', 'a', 'relacoes', '6.03.05.02-9 Relações do Comércio; Política Comercial; Integração Econômica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3701, '095', 'a', 'do', '6.03.05.02-9 Relações do Comércio; Política Comercial; Integração Econômica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3702, '095', 'a', 'comercio;', '6.03.05.02-9 Relações do Comércio; Política Comercial; Integração Econômica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3703, '095', 'a', 'politica', '6.03.05.02-9 Relações do Comércio; Política Comercial; Integração Econômica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3704, '095', 'a', 'comercial;', '6.03.05.02-9 Relações do Comércio; Política Comercial; Integração Econômica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3705, '095', 'a', 'integracao', '6.03.05.02-9 Relações do Comércio; Política Comercial; Integração Econômica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3706, '095', 'a', 'economica', '6.03.05.02-9 Relações do Comércio; Política Comercial; Integração Econômica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3707, '095', 'a', '6.03.05.03-7', '6.03.05.03-7 Balanço de Pagamentos; Financas Internacionais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3708, '095', 'a', 'balanco', '6.03.05.03-7 Balanço de Pagamentos; Financas Internacionais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3709, '095', 'a', 'de', '6.03.05.03-7 Balanço de Pagamentos; Financas Internacionais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3710, '095', 'a', 'pagamentos;', '6.03.05.03-7 Balanço de Pagamentos; Financas Internacionais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3711, '095', 'a', 'financas', '6.03.05.03-7 Balanço de Pagamentos; Financas Internacionais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3712, '095', 'a', 'internacionais', '6.03.05.03-7 Balanço de Pagamentos; Financas Internacionais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3713, '095', 'a', '6.03.05.04-5', '6.03.05.04-5 Investimentos Internacionais e Ajuda Externa', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3714, '095', 'a', 'investimentos', '6.03.05.04-5 Investimentos Internacionais e Ajuda Externa', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3715, '095', 'a', 'internacionais', '6.03.05.04-5 Investimentos Internacionais e Ajuda Externa', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3716, '095', 'a', 'ajuda', '6.03.05.04-5 Investimentos Internacionais e Ajuda Externa', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3717, '095', 'a', 'externa', '6.03.05.04-5 Investimentos Internacionais e Ajuda Externa', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3718, '095', 'a', '6.03.06.00-9', '6.03.06.00-9 Economia dos Recursos Humanos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3719, '095', 'a', 'economia', '6.03.06.00-9 Economia dos Recursos Humanos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3720, '095', 'a', 'dos', '6.03.06.00-9 Economia dos Recursos Humanos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3721, '095', 'a', 'recursos', '6.03.06.00-9 Economia dos Recursos Humanos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3722, '095', 'a', 'humanos', '6.03.06.00-9 Economia dos Recursos Humanos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3723, '095', 'a', '6.03.06.01-7', '6.03.06.01-7 Treinamento e Alocação de Mão-de-Obra; Oferta de Mão-de-Obra e Força de Trabalho', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3724, '095', 'a', 'treinamento', '6.03.06.01-7 Treinamento e Alocação de Mão-de-Obra; Oferta de Mão-de-Obra e Força de Trabalho', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3725, '095', 'a', 'alocacao', '6.03.06.01-7 Treinamento e Alocação de Mão-de-Obra; Oferta de Mão-de-Obra e Força de Trabalho', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3726, '095', 'a', 'de', '6.03.06.01-7 Treinamento e Alocação de Mão-de-Obra; Oferta de Mão-de-Obra e Força de Trabalho', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3727, '095', 'a', 'mao-de-obra;', '6.03.06.01-7 Treinamento e Alocação de Mão-de-Obra; Oferta de Mão-de-Obra e Força de Trabalho', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3728, '095', 'a', 'oferta', '6.03.06.01-7 Treinamento e Alocação de Mão-de-Obra; Oferta de Mão-de-Obra e Força de Trabalho', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3729, '095', 'a', 'de', '6.03.06.01-7 Treinamento e Alocação de Mão-de-Obra; Oferta de Mão-de-Obra e Força de Trabalho', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3730, '095', 'a', 'mao-de-obra', '6.03.06.01-7 Treinamento e Alocação de Mão-de-Obra; Oferta de Mão-de-Obra e Força de Trabalho', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3731, '095', 'a', 'forca', '6.03.06.01-7 Treinamento e Alocação de Mão-de-Obra; Oferta de Mão-de-Obra e Força de Trabalho', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3732, '095', 'a', 'de', '6.03.06.01-7 Treinamento e Alocação de Mão-de-Obra; Oferta de Mão-de-Obra e Força de Trabalho', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3733, '095', 'a', 'trabalho', '6.03.06.01-7 Treinamento e Alocação de Mão-de-Obra; Oferta de Mão-de-Obra e Força de Trabalho', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3734, '095', 'a', '6.03.06.02-5', '6.03.06.02-5 Mercado de Trabalho; Política do Governo', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3735, '095', 'a', 'mercado', '6.03.06.02-5 Mercado de Trabalho; Política do Governo', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3736, '095', 'a', 'de', '6.03.06.02-5 Mercado de Trabalho; Política do Governo', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3737, '095', 'a', 'trabalho;', '6.03.06.02-5 Mercado de Trabalho; Política do Governo', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3738, '095', 'a', 'politica', '6.03.06.02-5 Mercado de Trabalho; Política do Governo', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3739, '095', 'a', 'do', '6.03.06.02-5 Mercado de Trabalho; Política do Governo', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3740, '095', 'a', 'governo', '6.03.06.02-5 Mercado de Trabalho; Política do Governo', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3741, '095', 'a', '6.03.06.03-3', '6.03.06.03-3 Sindicatos, Dissídios Coletivos, Relações de Emprego (Empregador/Empregado)', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3742, '095', 'a', 'sindicatos,', '6.03.06.03-3 Sindicatos, Dissídios Coletivos, Relações de Emprego (Empregador/Empregado)', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3743, '095', 'a', 'dissidios', '6.03.06.03-3 Sindicatos, Dissídios Coletivos, Relações de Emprego (Empregador/Empregado)', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3744, '095', 'a', 'coletivos,', '6.03.06.03-3 Sindicatos, Dissídios Coletivos, Relações de Emprego (Empregador/Empregado)', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3745, '095', 'a', 'relacoes', '6.03.06.03-3 Sindicatos, Dissídios Coletivos, Relações de Emprego (Empregador/Empregado)', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3746, '095', 'a', 'de', '6.03.06.03-3 Sindicatos, Dissídios Coletivos, Relações de Emprego (Empregador/Empregado)', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3747, '095', 'a', 'emprego', '6.03.06.03-3 Sindicatos, Dissídios Coletivos, Relações de Emprego (Empregador/Empregado)', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3748, '095', 'a', '(empregador/empregado)', '6.03.06.03-3 Sindicatos, Dissídios Coletivos, Relações de Emprego (Empregador/Empregado)', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3749, '095', 'a', '6.03.06.04-1', '6.03.06.04-1 Capital Humano', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3750, '095', 'a', 'capital', '6.03.06.04-1 Capital Humano', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3751, '095', 'a', 'humano', '6.03.06.04-1 Capital Humano', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3752, '095', 'a', '6.03.06.05-0', '6.03.06.05-0 Demografia Econômica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3753, '095', 'a', 'demografia', '6.03.06.05-0 Demografia Econômica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3754, '095', 'a', 'economica', '6.03.06.05-0 Demografia Econômica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3755, '095', 'a', '6.03.07.00-5', '6.03.07.00-5 Economia Industrial', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3756, '095', 'a', 'economia', '6.03.07.00-5 Economia Industrial', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3757, '095', 'a', 'industrial', '6.03.07.00-5 Economia Industrial', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3758, '095', 'a', '6.03.07.01-3', '6.03.07.01-3 Organização Industrial e Estudos Industriais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3759, '095', 'a', 'organizacao', '6.03.07.01-3 Organização Industrial e Estudos Industriais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3760, '095', 'a', 'industrial', '6.03.07.01-3 Organização Industrial e Estudos Industriais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3761, '095', 'a', 'estudos', '6.03.07.01-3 Organização Industrial e Estudos Industriais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3762, '095', 'a', 'industriais', '6.03.07.01-3 Organização Industrial e Estudos Industriais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3763, '095', 'a', '6.03.07.02-1', '6.03.07.02-1 Mudança Tecnologica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3764, '095', 'a', 'mudanca', '6.03.07.02-1 Mudança Tecnologica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3765, '095', 'a', 'tecnologica', '6.03.07.02-1 Mudança Tecnologica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3766, '095', 'a', '6.03.08.00-1', '6.03.08.00-1 Economia do Bem-Estar Social', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3767, '095', 'a', 'economia', '6.03.08.00-1 Economia do Bem-Estar Social', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3768, '095', 'a', 'do', '6.03.08.00-1 Economia do Bem-Estar Social', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3769, '095', 'a', 'bem-estar', '6.03.08.00-1 Economia do Bem-Estar Social', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3770, '095', 'a', 'social', '6.03.08.00-1 Economia do Bem-Estar Social', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3771, '095', 'a', '6.03.08.01-0', '6.03.08.01-0 Economia dos Programas de Bem-Estar Social', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3772, '095', 'a', 'economia', '6.03.08.01-0 Economia dos Programas de Bem-Estar Social', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3773, '095', 'a', 'dos', '6.03.08.01-0 Economia dos Programas de Bem-Estar Social', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3774, '095', 'a', 'programas', '6.03.08.01-0 Economia dos Programas de Bem-Estar Social', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3775, '095', 'a', 'de', '6.03.08.01-0 Economia dos Programas de Bem-Estar Social', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3776, '095', 'a', 'bem-estar', '6.03.08.01-0 Economia dos Programas de Bem-Estar Social', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3777, '095', 'a', 'social', '6.03.08.01-0 Economia dos Programas de Bem-Estar Social', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3778, '095', 'a', '6.03.08.02-8', '6.03.08.02-8 Economia do Consumidor', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3779, '095', 'a', 'economia', '6.03.08.02-8 Economia do Consumidor', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3780, '095', 'a', 'do', '6.03.08.02-8 Economia do Consumidor', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3781, '095', 'a', 'consumidor', '6.03.08.02-8 Economia do Consumidor', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3782, '095', 'a', '6.03.09.00-8', '6.03.09.00-8 Economia Regional e Urbana', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3783, '095', 'a', 'economia', '6.03.09.00-8 Economia Regional e Urbana', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3784, '095', 'a', 'regional', '6.03.09.00-8 Economia Regional e Urbana', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3785, '095', 'a', 'urbana', '6.03.09.00-8 Economia Regional e Urbana', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3786, '095', 'a', '6.03.09.01-6', '6.03.09.01-6 Economia Regional', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3787, '095', 'a', 'economia', '6.03.09.01-6 Economia Regional', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3788, '095', 'a', 'regional', '6.03.09.01-6 Economia Regional', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3789, '095', 'a', '6.03.09.02-4', '6.03.09.02-4 Economia Urbana', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3790, '095', 'a', 'economia', '6.03.09.02-4 Economia Urbana', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3791, '095', 'a', 'urbana', '6.03.09.02-4 Economia Urbana', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3792, '095', 'a', '6.03.09.03-2', '6.03.09.03-2 Renda e Tributação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3793, '095', 'a', 'renda', '6.03.09.03-2 Renda e Tributação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3794, '095', 'a', 'tributacao', '6.03.09.03-2 Renda e Tributação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3795, '095', 'a', '6.03.10.00-6', '6.03.10.00-6 Economias Agrária e dos Recursos Naturais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3796, '095', 'a', 'economias', '6.03.10.00-6 Economias Agrária e dos Recursos Naturais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3797, '095', 'a', 'agraria', '6.03.10.00-6 Economias Agrária e dos Recursos Naturais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3798, '095', 'a', 'dos', '6.03.10.00-6 Economias Agrária e dos Recursos Naturais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3799, '095', 'a', 'recursos', '6.03.10.00-6 Economias Agrária e dos Recursos Naturais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3800, '095', 'a', 'naturais', '6.03.10.00-6 Economias Agrária e dos Recursos Naturais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3801, '095', 'a', '6.03.10.01-4', '6.03.10.01-4 Economia Agrária', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3802, '095', 'a', 'economia', '6.03.10.01-4 Economia Agrária', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3803, '095', 'a', 'agraria', '6.03.10.01-4 Economia Agrária', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3804, '095', 'a', '6.03.10.02-2', '6.03.10.02-2 Economia dos Recursos Naturais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3805, '095', 'a', 'economia', '6.03.10.02-2 Economia dos Recursos Naturais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3806, '095', 'a', 'dos', '6.03.10.02-2 Economia dos Recursos Naturais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3807, '095', 'a', 'recursos', '6.03.10.02-2 Economia dos Recursos Naturais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3808, '095', 'a', 'naturais', '6.03.10.02-2 Economia dos Recursos Naturais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3809, '095', 'a', '6.04.00.00-5', '6.04.00.00-5 Arquitetura e Urbanismo', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3810, '095', 'a', 'arquitetura', '6.04.00.00-5 Arquitetura e Urbanismo', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3811, '095', 'a', 'urbanismo', '6.04.00.00-5 Arquitetura e Urbanismo', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3812, '095', 'a', '6.04.01.00-1', '6.04.01.00-1 Fundamentos de Arquitetura e Urbanismo', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3813, '095', 'a', 'fundamentos', '6.04.01.00-1 Fundamentos de Arquitetura e Urbanismo', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3814, '095', 'a', 'de', '6.04.01.00-1 Fundamentos de Arquitetura e Urbanismo', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3815, '095', 'a', 'arquitetura', '6.04.01.00-1 Fundamentos de Arquitetura e Urbanismo', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3816, '095', 'a', 'urbanismo', '6.04.01.00-1 Fundamentos de Arquitetura e Urbanismo', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3817, '095', 'a', '6.04.01.01-0', '6.04.01.01-0 História da Arquitetura e Urbanismo', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3818, '095', 'a', 'historia', '6.04.01.01-0 História da Arquitetura e Urbanismo', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3819, '095', 'a', 'da', '6.04.01.01-0 História da Arquitetura e Urbanismo', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3820, '095', 'a', 'arquitetura', '6.04.01.01-0 História da Arquitetura e Urbanismo', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3821, '095', 'a', 'urbanismo', '6.04.01.01-0 História da Arquitetura e Urbanismo', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3822, '095', 'a', '6.04.01.02-8', '6.04.01.02-8 Teoria da Arquitetura', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3823, '095', 'a', 'teoria', '6.04.01.02-8 Teoria da Arquitetura', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3824, '095', 'a', 'da', '6.04.01.02-8 Teoria da Arquitetura', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3825, '095', 'a', 'arquitetura', '6.04.01.02-8 Teoria da Arquitetura', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3826, '095', 'a', '6.04.01.03-6', '6.04.01.03-6 História do Urbanismo', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3827, '095', 'a', 'historia', '6.04.01.03-6 História do Urbanismo', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3828, '095', 'a', 'do', '6.04.01.03-6 História do Urbanismo', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3829, '095', 'a', 'urbanismo', '6.04.01.03-6 História do Urbanismo', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3830, '095', 'a', '6.04.01.04-4', '6.04.01.04-4 Teoria do Urbanismo', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3831, '095', 'a', 'teoria', '6.04.01.04-4 Teoria do Urbanismo', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3832, '095', 'a', 'do', '6.04.01.04-4 Teoria do Urbanismo', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3833, '095', 'a', 'urbanismo', '6.04.01.04-4 Teoria do Urbanismo', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3834, '095', 'a', '6.04.02.00-8', '6.04.02.00-8 Projeto de Arquitetuta e Urbanismo', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3835, '095', 'a', 'projeto', '6.04.02.00-8 Projeto de Arquitetuta e Urbanismo', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3836, '095', 'a', 'de', '6.04.02.00-8 Projeto de Arquitetuta e Urbanismo', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3837, '095', 'a', 'arquitetuta', '6.04.02.00-8 Projeto de Arquitetuta e Urbanismo', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3838, '095', 'a', 'urbanismo', '6.04.02.00-8 Projeto de Arquitetuta e Urbanismo', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3839, '095', 'a', '6.04.02.01-6', '6.04.02.01-6 Planejamento e Projetos da Edificação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3840, '095', 'a', 'planejamento', '6.04.02.01-6 Planejamento e Projetos da Edificação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3841, '095', 'a', 'projetos', '6.04.02.01-6 Planejamento e Projetos da Edificação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3842, '095', 'a', 'da', '6.04.02.01-6 Planejamento e Projetos da Edificação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3843, '095', 'a', 'edificacao', '6.04.02.01-6 Planejamento e Projetos da Edificação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3844, '095', 'a', '6.04.02.02-4', '6.04.02.02-4 Planejamento e Projeto do Espaço Urbano', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3845, '095', 'a', 'planejamento', '6.04.02.02-4 Planejamento e Projeto do Espaço Urbano', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3846, '095', 'a', 'projeto', '6.04.02.02-4 Planejamento e Projeto do Espaço Urbano', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3847, '095', 'a', 'do', '6.04.02.02-4 Planejamento e Projeto do Espaço Urbano', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3848, '095', 'a', 'espaco', '6.04.02.02-4 Planejamento e Projeto do Espaço Urbano', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3849, '095', 'a', 'urbano', '6.04.02.02-4 Planejamento e Projeto do Espaço Urbano', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3850, '095', 'a', '6.04.02.03-2', '6.04.02.03-2 Planejamento e Projeto do Equipamento', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3851, '095', 'a', 'planejamento', '6.04.02.03-2 Planejamento e Projeto do Equipamento', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3852, '095', 'a', 'projeto', '6.04.02.03-2 Planejamento e Projeto do Equipamento', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3853, '095', 'a', 'do', '6.04.02.03-2 Planejamento e Projeto do Equipamento', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3854, '095', 'a', 'equipamento', '6.04.02.03-2 Planejamento e Projeto do Equipamento', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3855, '095', 'a', '6.04.03.00-4', '6.04.03.00-4 Tecnologia de Arquitetura e Urbanismo', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3856, '095', 'a', 'tecnologia', '6.04.03.00-4 Tecnologia de Arquitetura e Urbanismo', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3857, '095', 'a', 'de', '6.04.03.00-4 Tecnologia de Arquitetura e Urbanismo', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3858, '095', 'a', 'arquitetura', '6.04.03.00-4 Tecnologia de Arquitetura e Urbanismo', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3859, '095', 'a', 'urbanismo', '6.04.03.00-4 Tecnologia de Arquitetura e Urbanismo', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3860, '095', 'a', '6.04.03.01-2', '6.04.03.01-2 Adequação Ambiental', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3861, '095', 'a', 'adequacao', '6.04.03.01-2 Adequação Ambiental', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3862, '095', 'a', 'ambiental', '6.04.03.01-2 Adequação Ambiental', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3863, '095', 'a', '6.04.04.00-0', '6.04.04.00-0 Paisagismo', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3864, '095', 'a', 'paisagismo', '6.04.04.00-0 Paisagismo', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3865, '095', 'a', '6.04.04.01-9', '6.04.04.01-9 Desenvolvimento Histórico do Paisagismo', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3866, '095', 'a', 'desenvolvimento', '6.04.04.01-9 Desenvolvimento Histórico do Paisagismo', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3867, '095', 'a', 'historico', '6.04.04.01-9 Desenvolvimento Histórico do Paisagismo', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3868, '095', 'a', 'do', '6.04.04.01-9 Desenvolvimento Histórico do Paisagismo', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3869, '095', 'a', 'paisagismo', '6.04.04.01-9 Desenvolvimento Histórico do Paisagismo', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3870, '095', 'a', '6.04.04.02-7', '6.04.04.02-7 Conceituação de Paisagismo e Metodologia do Paisagismo', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3871, '095', 'a', 'conceituacao', '6.04.04.02-7 Conceituação de Paisagismo e Metodologia do Paisagismo', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3872, '095', 'a', 'de', '6.04.04.02-7 Conceituação de Paisagismo e Metodologia do Paisagismo', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3873, '095', 'a', 'paisagismo', '6.04.04.02-7 Conceituação de Paisagismo e Metodologia do Paisagismo', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3874, '095', 'a', 'metodologia', '6.04.04.02-7 Conceituação de Paisagismo e Metodologia do Paisagismo', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3875, '095', 'a', 'do', '6.04.04.02-7 Conceituação de Paisagismo e Metodologia do Paisagismo', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3876, '095', 'a', 'paisagismo', '6.04.04.02-7 Conceituação de Paisagismo e Metodologia do Paisagismo', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3877, '095', 'a', '6.04.04.03-5', '6.04.04.03-5 Estudos de Organização do Espaço Exterior', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3878, '095', 'a', 'estudos', '6.04.04.03-5 Estudos de Organização do Espaço Exterior', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3879, '095', 'a', 'de', '6.04.04.03-5 Estudos de Organização do Espaço Exterior', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3880, '095', 'a', 'organizacao', '6.04.04.03-5 Estudos de Organização do Espaço Exterior', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3881, '095', 'a', 'do', '6.04.04.03-5 Estudos de Organização do Espaço Exterior', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3882, '095', 'a', 'espaco', '6.04.04.03-5 Estudos de Organização do Espaço Exterior', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3883, '095', 'a', 'exterior', '6.04.04.03-5 Estudos de Organização do Espaço Exterior', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3884, '095', 'a', '6.04.04.04-3', '6.04.04.04-3 Projetos de Espaços Livres Urbanos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3885, '095', 'a', 'projetos', '6.04.04.04-3 Projetos de Espaços Livres Urbanos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3886, '095', 'a', 'de', '6.04.04.04-3 Projetos de Espaços Livres Urbanos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3887, '095', 'a', 'espacos', '6.04.04.04-3 Projetos de Espaços Livres Urbanos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3888, '095', 'a', 'livres', '6.04.04.04-3 Projetos de Espaços Livres Urbanos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3889, '095', 'a', 'urbanos', '6.04.04.04-3 Projetos de Espaços Livres Urbanos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3890, '095', 'a', '6.05.00.00-0', '6.05.00.00-0 Planejamento Urbano e Regional', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3891, '095', 'a', 'planejamento', '6.05.00.00-0 Planejamento Urbano e Regional', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3892, '095', 'a', 'urbano', '6.05.00.00-0 Planejamento Urbano e Regional', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3893, '095', 'a', 'regional', '6.05.00.00-0 Planejamento Urbano e Regional', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3894, '095', 'a', '6.05.01.00-6', '6.05.01.00-6 Fundamentos do Planejamento Urbano e Regional', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3895, '095', 'a', 'fundamentos', '6.05.01.00-6 Fundamentos do Planejamento Urbano e Regional', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3896, '095', 'a', 'do', '6.05.01.00-6 Fundamentos do Planejamento Urbano e Regional', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3897, '095', 'a', 'planejamento', '6.05.01.00-6 Fundamentos do Planejamento Urbano e Regional', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3898, '095', 'a', 'urbano', '6.05.01.00-6 Fundamentos do Planejamento Urbano e Regional', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3899, '095', 'a', 'regional', '6.05.01.00-6 Fundamentos do Planejamento Urbano e Regional', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3900, '095', 'a', '6.05.01.01-4', '6.05.01.01-4 Teoria do Planejamento Urbano e Regional', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3901, '095', 'a', 'teoria', '6.05.01.01-4 Teoria do Planejamento Urbano e Regional', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3902, '095', 'a', 'do', '6.05.01.01-4 Teoria do Planejamento Urbano e Regional', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3903, '095', 'a', 'planejamento', '6.05.01.01-4 Teoria do Planejamento Urbano e Regional', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3904, '095', 'a', 'urbano', '6.05.01.01-4 Teoria do Planejamento Urbano e Regional', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3905, '095', 'a', 'regional', '6.05.01.01-4 Teoria do Planejamento Urbano e Regional', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3906, '095', 'a', '6.05.01.02-2', '6.05.01.02-2 Teoria da Urbanização', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3907, '095', 'a', 'teoria', '6.05.01.02-2 Teoria da Urbanização', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3908, '095', 'a', 'da', '6.05.01.02-2 Teoria da Urbanização', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3909, '095', 'a', 'urbanizacao', '6.05.01.02-2 Teoria da Urbanização', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3910, '095', 'a', '6.05.01.03-0', '6.05.01.03-0 Política Urbana', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3911, '095', 'a', 'politica', '6.05.01.03-0 Política Urbana', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3912, '095', 'a', 'urbana', '6.05.01.03-0 Política Urbana', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3913, '095', 'a', '6.05.01.04-9', '6.05.01.04-9 História Urbana', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3914, '095', 'a', 'historia', '6.05.01.04-9 História Urbana', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3915, '095', 'a', 'urbana', '6.05.01.04-9 História Urbana', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3916, '095', 'a', '6.05.02.00-2', '6.05.02.00-2 Métodos e Técnicas do Planejamento Urbano e Regional', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3917, '095', 'a', 'metodos', '6.05.02.00-2 Métodos e Técnicas do Planejamento Urbano e Regional', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3918, '095', 'a', 'tecnicas', '6.05.02.00-2 Métodos e Técnicas do Planejamento Urbano e Regional', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3919, '095', 'a', 'do', '6.05.02.00-2 Métodos e Técnicas do Planejamento Urbano e Regional', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3920, '095', 'a', 'planejamento', '6.05.02.00-2 Métodos e Técnicas do Planejamento Urbano e Regional', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3921, '095', 'a', 'urbano', '6.05.02.00-2 Métodos e Técnicas do Planejamento Urbano e Regional', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3922, '095', 'a', 'regional', '6.05.02.00-2 Métodos e Técnicas do Planejamento Urbano e Regional', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3923, '095', 'a', '6.05.02.01-0', '6.05.02.01-0 Informação, Cadastro e Mapeamento', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3924, '095', 'a', 'informacao,', '6.05.02.01-0 Informação, Cadastro e Mapeamento', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3925, '095', 'a', 'cadastro', '6.05.02.01-0 Informação, Cadastro e Mapeamento', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3926, '095', 'a', 'mapeamento', '6.05.02.01-0 Informação, Cadastro e Mapeamento', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3927, '095', 'a', '6.05.02.02-9', '6.05.02.02-9 Técnica de Previsão Urbana e Regional', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3928, '095', 'a', 'tecnica', '6.05.02.02-9 Técnica de Previsão Urbana e Regional', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3929, '095', 'a', 'de', '6.05.02.02-9 Técnica de Previsão Urbana e Regional', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3930, '095', 'a', 'previsao', '6.05.02.02-9 Técnica de Previsão Urbana e Regional', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3931, '095', 'a', 'urbana', '6.05.02.02-9 Técnica de Previsão Urbana e Regional', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3932, '095', 'a', 'regional', '6.05.02.02-9 Técnica de Previsão Urbana e Regional', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3933, '095', 'a', '6.05.02.03-7', '6.05.02.03-7 Técnicas de Análise e Avaliação Urbana e Regional', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3934, '095', 'a', 'tecnicas', '6.05.02.03-7 Técnicas de Análise e Avaliação Urbana e Regional', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3935, '095', 'a', 'de', '6.05.02.03-7 Técnicas de Análise e Avaliação Urbana e Regional', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3936, '095', 'a', 'analise', '6.05.02.03-7 Técnicas de Análise e Avaliação Urbana e Regional', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3937, '095', 'a', 'avaliacao', '6.05.02.03-7 Técnicas de Análise e Avaliação Urbana e Regional', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3938, '095', 'a', 'urbana', '6.05.02.03-7 Técnicas de Análise e Avaliação Urbana e Regional', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3939, '095', 'a', 'regional', '6.05.02.03-7 Técnicas de Análise e Avaliação Urbana e Regional', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3940, '095', 'a', '6.05.02.04-5', '6.05.02.04-5 Técnicas de Planejamento e Projeto Urbanos e Regionais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3941, '095', 'a', 'tecnicas', '6.05.02.04-5 Técnicas de Planejamento e Projeto Urbanos e Regionais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3942, '095', 'a', 'de', '6.05.02.04-5 Técnicas de Planejamento e Projeto Urbanos e Regionais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3943, '095', 'a', 'planejamento', '6.05.02.04-5 Técnicas de Planejamento e Projeto Urbanos e Regionais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3944, '095', 'a', 'projeto', '6.05.02.04-5 Técnicas de Planejamento e Projeto Urbanos e Regionais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3945, '095', 'a', 'urbanos', '6.05.02.04-5 Técnicas de Planejamento e Projeto Urbanos e Regionais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3946, '095', 'a', 'regionais', '6.05.02.04-5 Técnicas de Planejamento e Projeto Urbanos e Regionais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3947, '095', 'a', '6.05.03.00-9', '6.05.03.00-9 Serviços Urbanos e Regionais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3948, '095', 'a', 'servicos', '6.05.03.00-9 Serviços Urbanos e Regionais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3949, '095', 'a', 'urbanos', '6.05.03.00-9 Serviços Urbanos e Regionais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3950, '095', 'a', 'regionais', '6.05.03.00-9 Serviços Urbanos e Regionais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3951, '095', 'a', '6.05.03.01-7', '6.05.03.01-7 Administração Municipal e Urbana', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3952, '095', 'a', 'administracao', '6.05.03.01-7 Administração Municipal e Urbana', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3953, '095', 'a', 'municipal', '6.05.03.01-7 Administração Municipal e Urbana', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3954, '095', 'a', 'urbana', '6.05.03.01-7 Administração Municipal e Urbana', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3955, '095', 'a', '6.05.03.02-5', '6.05.03.02-5 Estudos da Habitação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3956, '095', 'a', 'estudos', '6.05.03.02-5 Estudos da Habitação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3957, '095', 'a', 'da', '6.05.03.02-5 Estudos da Habitação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3958, '095', 'a', 'habitacao', '6.05.03.02-5 Estudos da Habitação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3959, '095', 'a', '6.05.03.03-3', '6.05.03.03-3 Aspectos Sociais do Planejamento Urbano e Regional', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3960, '095', 'a', 'aspectos', '6.05.03.03-3 Aspectos Sociais do Planejamento Urbano e Regional', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3961, '095', 'a', 'sociais', '6.05.03.03-3 Aspectos Sociais do Planejamento Urbano e Regional', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3962, '095', 'a', 'do', '6.05.03.03-3 Aspectos Sociais do Planejamento Urbano e Regional', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3963, '095', 'a', 'planejamento', '6.05.03.03-3 Aspectos Sociais do Planejamento Urbano e Regional', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3964, '095', 'a', 'urbano', '6.05.03.03-3 Aspectos Sociais do Planejamento Urbano e Regional', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3965, '095', 'a', 'regional', '6.05.03.03-3 Aspectos Sociais do Planejamento Urbano e Regional', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3966, '095', 'a', '6.05.03.04-1', '6.05.03.04-1 Aspectos Econômicos do Planejamento Urbano e Regional', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3967, '095', 'a', 'aspectos', '6.05.03.04-1 Aspectos Econômicos do Planejamento Urbano e Regional', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3968, '095', 'a', 'economicos', '6.05.03.04-1 Aspectos Econômicos do Planejamento Urbano e Regional', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3969, '095', 'a', 'do', '6.05.03.04-1 Aspectos Econômicos do Planejamento Urbano e Regional', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3970, '095', 'a', 'planejamento', '6.05.03.04-1 Aspectos Econômicos do Planejamento Urbano e Regional', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3971, '095', 'a', 'urbano', '6.05.03.04-1 Aspectos Econômicos do Planejamento Urbano e Regional', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3972, '095', 'a', 'regional', '6.05.03.04-1 Aspectos Econômicos do Planejamento Urbano e Regional', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3973, '095', 'a', '6.05.03.05-0', '6.05.03.05-0 Aspectos Físico-Ambientais do Planejamento Urbano e Regional', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3974, '095', 'a', 'aspectos', '6.05.03.05-0 Aspectos Físico-Ambientais do Planejamento Urbano e Regional', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3975, '095', 'a', 'fisico-ambientais', '6.05.03.05-0 Aspectos Físico-Ambientais do Planejamento Urbano e Regional', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3976, '095', 'a', 'do', '6.05.03.05-0 Aspectos Físico-Ambientais do Planejamento Urbano e Regional', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3977, '095', 'a', 'planejamento', '6.05.03.05-0 Aspectos Físico-Ambientais do Planejamento Urbano e Regional', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3978, '095', 'a', 'urbano', '6.05.03.05-0 Aspectos Físico-Ambientais do Planejamento Urbano e Regional', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3979, '095', 'a', 'regional', '6.05.03.05-0 Aspectos Físico-Ambientais do Planejamento Urbano e Regional', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3980, '095', 'a', '6.05.03.06-8', '6.05.03.06-8 Serviços Comunitários', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3981, '095', 'a', 'servicos', '6.05.03.06-8 Serviços Comunitários', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3982, '095', 'a', 'comunitarios', '6.05.03.06-8 Serviços Comunitários', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3983, '095', 'a', '6.05.03.07-6', '6.05.03.07-6 Infra-Estruturas Urbanas e Regionais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3984, '095', 'a', 'infra-estruturas', '6.05.03.07-6 Infra-Estruturas Urbanas e Regionais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3985, '095', 'a', 'urbanas', '6.05.03.07-6 Infra-Estruturas Urbanas e Regionais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3986, '095', 'a', 'regionais', '6.05.03.07-6 Infra-Estruturas Urbanas e Regionais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3987, '095', 'a', '6.05.03.08-4', '6.05.03.08-4 Transporte e Tráfego Urbano e Regional', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3988, '095', 'a', 'transporte', '6.05.03.08-4 Transporte e Tráfego Urbano e Regional', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3989, '095', 'a', 'trafego', '6.05.03.08-4 Transporte e Tráfego Urbano e Regional', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3990, '095', 'a', 'urbano', '6.05.03.08-4 Transporte e Tráfego Urbano e Regional', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3991, '095', 'a', 'regional', '6.05.03.08-4 Transporte e Tráfego Urbano e Regional', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3992, '095', 'a', '6.05.03.09-2', '6.05.03.09-2 Legislação Urbana e Regional', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3993, '095', 'a', 'legislacao', '6.05.03.09-2 Legislação Urbana e Regional', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3994, '095', 'a', 'urbana', '6.05.03.09-2 Legislação Urbana e Regional', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3995, '095', 'a', 'regional', '6.05.03.09-2 Legislação Urbana e Regional', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3996, '095', 'a', '6.06.00.00-4', '6.06.00.00-4 Demografia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3997, '095', 'a', 'demografia', '6.06.00.00-4 Demografia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3998, '095', 'a', '6.06.01.00-0', '6.06.01.00-0 Distribuição Espacial', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (3999, '095', 'a', 'distribuicao', '6.06.01.00-0 Distribuição Espacial', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4000, '095', 'a', 'espacial', '6.06.01.00-0 Distribuição Espacial', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4001, '095', 'a', '6.06.01.01-9', '6.06.01.01-9 Distribuição Espacial Geral', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4002, '095', 'a', 'distribuicao', '6.06.01.01-9 Distribuição Espacial Geral', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4003, '095', 'a', 'espacial', '6.06.01.01-9 Distribuição Espacial Geral', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4004, '095', 'a', 'geral', '6.06.01.01-9 Distribuição Espacial Geral', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4005, '095', 'a', '6.06.01.02-7', '6.06.01.02-7 Distribuição Espacial Urbana', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4006, '095', 'a', 'distribuicao', '6.06.01.02-7 Distribuição Espacial Urbana', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4007, '095', 'a', 'espacial', '6.06.01.02-7 Distribuição Espacial Urbana', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4008, '095', 'a', 'urbana', '6.06.01.02-7 Distribuição Espacial Urbana', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4009, '095', 'a', '6.06.01.03-5', '6.06.01.03-5 Distribuição Espacial Rural', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4010, '095', 'a', 'distribuicao', '6.06.01.03-5 Distribuição Espacial Rural', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4011, '095', 'a', 'espacial', '6.06.01.03-5 Distribuição Espacial Rural', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4012, '095', 'a', 'rural', '6.06.01.03-5 Distribuição Espacial Rural', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4013, '095', 'a', '6.06.02.00-7', '6.06.02.00-7 Tendência Populacional', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4014, '095', 'a', 'tendencia', '6.06.02.00-7 Tendência Populacional', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4015, '095', 'a', 'populacional', '6.06.02.00-7 Tendência Populacional', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4016, '095', 'a', '6.06.02.01-5', '6.06.02.01-5 Tendências Passadas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4017, '095', 'a', 'tendencias', '6.06.02.01-5 Tendências Passadas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4018, '095', 'a', 'passadas', '6.06.02.01-5 Tendências Passadas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4019, '095', 'a', '6.06.02.02-3', '6.06.02.02-3 Taxas e Estimativas Correntes', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4020, '095', 'a', 'taxas', '6.06.02.02-3 Taxas e Estimativas Correntes', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4021, '095', 'a', 'estimativas', '6.06.02.02-3 Taxas e Estimativas Correntes', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4022, '095', 'a', 'correntes', '6.06.02.02-3 Taxas e Estimativas Correntes', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4023, '095', 'a', '6.06.02.03-1', '6.06.02.03-1 Projeções', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4024, '095', 'a', 'projecoes', '6.06.02.03-1 Projeções', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4025, '095', 'a', '6.06.03.00-3', '6.06.03.00-3 Componentes da Dinâmica Demográfica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4026, '095', 'a', 'componentes', '6.06.03.00-3 Componentes da Dinâmica Demográfica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4027, '095', 'a', 'da', '6.06.03.00-3 Componentes da Dinâmica Demográfica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4028, '095', 'a', 'dinamica', '6.06.03.00-3 Componentes da Dinâmica Demográfica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4029, '095', 'a', 'demografica', '6.06.03.00-3 Componentes da Dinâmica Demográfica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4030, '095', 'a', '6.06.03.01-1', '6.06.03.01-1 Fecundidade', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4031, '095', 'a', 'fecundidade', '6.06.03.01-1 Fecundidade', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4032, '095', 'a', '6.06.03.02-0', '6.06.03.02-0 Mortalidade', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4033, '095', 'a', 'mortalidade', '6.06.03.02-0 Mortalidade', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4034, '095', 'a', '6.06.03.03-8', '6.06.03.03-8 Migração', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4035, '095', 'a', 'migracao', '6.06.03.03-8 Migração', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4036, '095', 'a', '6.06.04.00-0', '6.06.04.00-0 Nupcialidade e Família', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4037, '095', 'a', 'nupcialidade', '6.06.04.00-0 Nupcialidade e Família', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4038, '095', 'a', 'familia', '6.06.04.00-0 Nupcialidade e Família', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4039, '095', 'a', '6.06.04.01-8', '6.06.04.01-8 Casamento e Divórcio', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4040, '095', 'a', 'casamento', '6.06.04.01-8 Casamento e Divórcio', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4041, '095', 'a', 'divorcio', '6.06.04.01-8 Casamento e Divórcio', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4042, '095', 'a', '6.06.04.02-6', '6.06.04.02-6 Família e Reprodução', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4043, '095', 'a', 'familia', '6.06.04.02-6 Família e Reprodução', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4044, '095', 'a', 'reproducao', '6.06.04.02-6 Família e Reprodução', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4045, '095', 'a', '6.06.05.00-6', '6.06.05.00-6 Demografia Histórica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4046, '095', 'a', 'demografia', '6.06.05.00-6 Demografia Histórica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4047, '095', 'a', 'historica', '6.06.05.00-6 Demografia Histórica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4048, '095', 'a', '6.06.05.01-4', '6.06.05.01-4 Distribuição Espacial', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4049, '095', 'a', 'distribuicao', '6.06.05.01-4 Distribuição Espacial', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4050, '095', 'a', 'espacial', '6.06.05.01-4 Distribuição Espacial', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4051, '095', 'a', '6.06.05.02-2', '6.06.05.02-2 Natalidade, Mortalidade, Migração', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4052, '095', 'a', 'natalidade,', '6.06.05.02-2 Natalidade, Mortalidade, Migração', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4053, '095', 'a', 'mortalidade,', '6.06.05.02-2 Natalidade, Mortalidade, Migração', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4054, '095', 'a', 'migracao', '6.06.05.02-2 Natalidade, Mortalidade, Migração', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4055, '095', 'a', '6.06.05.03-0', '6.06.05.03-0 Nupcialidade e Família', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4056, '095', 'a', 'nupcialidade', '6.06.05.03-0 Nupcialidade e Família', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4057, '095', 'a', 'familia', '6.06.05.03-0 Nupcialidade e Família', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4058, '095', 'a', '6.06.05.04-9', '6.06.05.04-9 Métodos e Técnicas de Demografia Histórica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4059, '095', 'a', 'metodos', '6.06.05.04-9 Métodos e Técnicas de Demografia Histórica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4060, '095', 'a', 'tecnicas', '6.06.05.04-9 Métodos e Técnicas de Demografia Histórica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4061, '095', 'a', 'de', '6.06.05.04-9 Métodos e Técnicas de Demografia Histórica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4062, '095', 'a', 'demografia', '6.06.05.04-9 Métodos e Técnicas de Demografia Histórica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4063, '095', 'a', 'historica', '6.06.05.04-9 Métodos e Técnicas de Demografia Histórica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4064, '095', 'a', '6.06.06.00-2', '6.06.06.00-2 Política Pública e População', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4065, '095', 'a', 'politica', '6.06.06.00-2 Política Pública e População', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4066, '095', 'a', 'publica', '6.06.06.00-2 Política Pública e População', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4067, '095', 'a', 'populacao', '6.06.06.00-2 Política Pública e População', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4068, '095', 'a', '6.06.06.01-0', '6.06.06.01-0 Política Populacional', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4069, '095', 'a', 'politica', '6.06.06.01-0 Política Populacional', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4070, '095', 'a', 'populacional', '6.06.06.01-0 Política Populacional', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4071, '095', 'a', '6.06.06.02-9', '6.06.06.02-9 Políticas de Redistribuição de População', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4072, '095', 'a', 'politicas', '6.06.06.02-9 Políticas de Redistribuição de População', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4073, '095', 'a', 'de', '6.06.06.02-9 Políticas de Redistribuição de População', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4074, '095', 'a', 'redistribuicao', '6.06.06.02-9 Políticas de Redistribuição de População', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4075, '095', 'a', 'de', '6.06.06.02-9 Políticas de Redistribuição de População', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4076, '095', 'a', 'populacao', '6.06.06.02-9 Políticas de Redistribuição de População', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4077, '095', 'a', '6.06.06.03-7', '6.06.06.03-7 Políticas de Planejamento Familiar', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4078, '095', 'a', 'politicas', '6.06.06.03-7 Políticas de Planejamento Familiar', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4079, '095', 'a', 'de', '6.06.06.03-7 Políticas de Planejamento Familiar', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4080, '095', 'a', 'planejamento', '6.06.06.03-7 Políticas de Planejamento Familiar', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4081, '095', 'a', 'familiar', '6.06.06.03-7 Políticas de Planejamento Familiar', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4082, '095', 'a', '6.06.07.00-9', '6.06.07.00-9 Fontes de Dados Demográficos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4083, '095', 'a', 'fontes', '6.06.07.00-9 Fontes de Dados Demográficos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4084, '095', 'a', 'de', '6.06.07.00-9 Fontes de Dados Demográficos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4085, '095', 'a', 'dados', '6.06.07.00-9 Fontes de Dados Demográficos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4086, '095', 'a', 'demograficos', '6.06.07.00-9 Fontes de Dados Demográficos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4087, '095', 'a', '6.07.00.00-9', '6.07.00.00-9 Ciência da Informação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4088, '095', 'a', 'ciencia', '6.07.00.00-9 Ciência da Informação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4089, '095', 'a', 'da', '6.07.00.00-9 Ciência da Informação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4090, '095', 'a', 'informacao', '6.07.00.00-9 Ciência da Informação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4091, '095', 'a', '6.07.01.00-5', '6.07.01.00-5 Teoria da Informação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4092, '095', 'a', 'teoria', '6.07.01.00-5 Teoria da Informação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4093, '095', 'a', 'da', '6.07.01.00-5 Teoria da Informação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4094, '095', 'a', 'informacao', '6.07.01.00-5 Teoria da Informação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4095, '095', 'a', '6.07.01.01-3', '6.07.01.01-3 Teoria Geral da Informação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4096, '095', 'a', 'teoria', '6.07.01.01-3 Teoria Geral da Informação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4097, '095', 'a', 'geral', '6.07.01.01-3 Teoria Geral da Informação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4098, '095', 'a', 'da', '6.07.01.01-3 Teoria Geral da Informação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4099, '095', 'a', 'informacao', '6.07.01.01-3 Teoria Geral da Informação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4100, '095', 'a', '6.07.01.02-1', '6.07.01.02-1 Processos da Comunicação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4101, '095', 'a', 'processos', '6.07.01.02-1 Processos da Comunicação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4102, '095', 'a', 'da', '6.07.01.02-1 Processos da Comunicação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4103, '095', 'a', 'comunicacao', '6.07.01.02-1 Processos da Comunicação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4104, '095', 'a', '6.07.01.03-0', '6.07.01.03-0 Representação da Informação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4105, '095', 'a', 'representacao', '6.07.01.03-0 Representação da Informação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4106, '095', 'a', 'da', '6.07.01.03-0 Representação da Informação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4107, '095', 'a', 'informacao', '6.07.01.03-0 Representação da Informação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4108, '095', 'a', '6.07.02.00-1', '6.07.02.00-1 Biblioteconomia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4109, '095', 'a', 'biblioteconomia', '6.07.02.00-1 Biblioteconomia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4110, '095', 'a', '6.07.02.01-0', '6.07.02.01-0 Teoria da Classificação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4111, '095', 'a', 'teoria', '6.07.02.01-0 Teoria da Classificação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4112, '095', 'a', 'da', '6.07.02.01-0 Teoria da Classificação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4113, '095', 'a', 'classificacao', '6.07.02.01-0 Teoria da Classificação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4114, '095', 'a', '6.07.02.02-8', '6.07.02.02-8 Métodos Quantitativos. Bibliometria', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4115, '095', 'a', 'metodos', '6.07.02.02-8 Métodos Quantitativos. Bibliometria', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4116, '095', 'a', 'quantitativos.', '6.07.02.02-8 Métodos Quantitativos. Bibliometria', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4117, '095', 'a', 'bibliometria', '6.07.02.02-8 Métodos Quantitativos. Bibliometria', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4118, '095', 'a', '6.07.02.03-6', '6.07.02.03-6 Técnicas de Recuperação de Informação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4119, '095', 'a', 'tecnicas', '6.07.02.03-6 Técnicas de Recuperação de Informação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4120, '095', 'a', 'de', '6.07.02.03-6 Técnicas de Recuperação de Informação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4121, '095', 'a', 'recuperacao', '6.07.02.03-6 Técnicas de Recuperação de Informação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4122, '095', 'a', 'de', '6.07.02.03-6 Técnicas de Recuperação de Informação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4123, '095', 'a', 'informacao', '6.07.02.03-6 Técnicas de Recuperação de Informação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4124, '095', 'a', '6.07.02.04-4', '6.07.02.04-4 Processos de Disseminação da Informação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4125, '095', 'a', 'processos', '6.07.02.04-4 Processos de Disseminação da Informação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4126, '095', 'a', 'de', '6.07.02.04-4 Processos de Disseminação da Informação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4127, '095', 'a', 'disseminacao', '6.07.02.04-4 Processos de Disseminação da Informação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4128, '095', 'a', 'da', '6.07.02.04-4 Processos de Disseminação da Informação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4129, '095', 'a', 'informacao', '6.07.02.04-4 Processos de Disseminação da Informação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4130, '095', 'a', '6.07.03.00-8', '6.07.03.00-8 Arquivologia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4131, '095', 'a', 'arquivologia', '6.07.03.00-8 Arquivologia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4132, '095', 'a', '6.07.03.01-6', '6.07.03.01-6 Organização de Arquivos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4133, '095', 'a', 'organizacao', '6.07.03.01-6 Organização de Arquivos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4134, '095', 'a', 'de', '6.07.03.01-6 Organização de Arquivos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4135, '095', 'a', 'arquivos', '6.07.03.01-6 Organização de Arquivos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4136, '095', 'a', '6.08.00.00-3', '6.08.00.00-3 Museologia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4137, '095', 'a', 'museologia', '6.08.00.00-3 Museologia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4138, '095', 'a', '6.09.00.00-8', '6.09.00.00-8 Comunicação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4139, '095', 'a', 'comunicacao', '6.09.00.00-8 Comunicação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4140, '095', 'a', '6.09.01.00-4', '6.09.01.00-4 Teoria da Comunicação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4141, '095', 'a', 'teoria', '6.09.01.00-4 Teoria da Comunicação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4142, '095', 'a', 'da', '6.09.01.00-4 Teoria da Comunicação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4143, '095', 'a', 'comunicacao', '6.09.01.00-4 Teoria da Comunicação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4144, '095', 'a', '6.09.02.00-0', '6.09.02.00-0 Jornalismo e Editoração', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4145, '095', 'a', 'jornalismo', '6.09.02.00-0 Jornalismo e Editoração', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4146, '095', 'a', 'editoracao', '6.09.02.00-0 Jornalismo e Editoração', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4147, '095', 'a', '6.09.02.01-9', '6.09.02.01-9 Teoria e Ética do Jornalismo', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4148, '095', 'a', 'teoria', '6.09.02.01-9 Teoria e Ética do Jornalismo', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4149, '095', 'a', 'etica', '6.09.02.01-9 Teoria e Ética do Jornalismo', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4150, '095', 'a', 'do', '6.09.02.01-9 Teoria e Ética do Jornalismo', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4151, '095', 'a', 'jornalismo', '6.09.02.01-9 Teoria e Ética do Jornalismo', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4152, '095', 'a', '6.09.02.02-7', '6.09.02.02-7 Organização Editorial de Jornais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4153, '095', 'a', 'organizacao', '6.09.02.02-7 Organização Editorial de Jornais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4154, '095', 'a', 'editorial', '6.09.02.02-7 Organização Editorial de Jornais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4155, '095', 'a', 'de', '6.09.02.02-7 Organização Editorial de Jornais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4156, '095', 'a', 'jornais', '6.09.02.02-7 Organização Editorial de Jornais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4157, '095', 'a', '6.09.02.03-5', '6.09.02.03-5 Organização Comercial de Jornais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4158, '095', 'a', 'organizacao', '6.09.02.03-5 Organização Comercial de Jornais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4159, '095', 'a', 'comercial', '6.09.02.03-5 Organização Comercial de Jornais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4160, '095', 'a', 'de', '6.09.02.03-5 Organização Comercial de Jornais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4161, '095', 'a', 'jornais', '6.09.02.03-5 Organização Comercial de Jornais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4162, '095', 'a', '6.09.02.04-3', '6.09.02.04-3 Jornalismo Especializado (Comunitário, Rural, Empresarial, Científico)', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4163, '095', 'a', 'jornalismo', '6.09.02.04-3 Jornalismo Especializado (Comunitário, Rural, Empresarial, Científico)', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4164, '095', 'a', 'especializado', '6.09.02.04-3 Jornalismo Especializado (Comunitário, Rural, Empresarial, Científico)', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4165, '095', 'a', '(comunitario,', '6.09.02.04-3 Jornalismo Especializado (Comunitário, Rural, Empresarial, Científico)', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4166, '095', 'a', 'rural,', '6.09.02.04-3 Jornalismo Especializado (Comunitário, Rural, Empresarial, Científico)', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4167, '095', 'a', 'empresarial,', '6.09.02.04-3 Jornalismo Especializado (Comunitário, Rural, Empresarial, Científico)', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4168, '095', 'a', 'cientifico)', '6.09.02.04-3 Jornalismo Especializado (Comunitário, Rural, Empresarial, Científico)', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4169, '095', 'a', '6.09.03.00-7', '6.09.03.00-7 Rádio e Televisão', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4170, '095', 'a', 'radio', '6.09.03.00-7 Rádio e Televisão', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4171, '095', 'a', 'televisao', '6.09.03.00-7 Rádio e Televisão', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4172, '095', 'a', '6.09.03.01-5', '6.09.03.01-5 Radiodifusão', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4173, '095', 'a', 'radiodifusao', '6.09.03.01-5 Radiodifusão', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4174, '095', 'a', '6.09.03.02-3', '6.09.03.02-3 Videodifusão', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4175, '095', 'a', 'videodifusao', '6.09.03.02-3 Videodifusão', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4176, '095', 'a', '6.09.04.00-3', '6.09.04.00-3 Relações Públicas e Propaganda', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4177, '095', 'a', 'relacoes', '6.09.04.00-3 Relações Públicas e Propaganda', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4178, '095', 'a', 'publicas', '6.09.04.00-3 Relações Públicas e Propaganda', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4179, '095', 'a', 'propaganda', '6.09.04.00-3 Relações Públicas e Propaganda', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4180, '095', 'a', '6.09.05.00-0', '6.09.05.00-0 Comunicação Visual', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4181, '095', 'a', 'comunicacao', '6.09.05.00-0 Comunicação Visual', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4182, '095', 'a', 'visual', '6.09.05.00-0 Comunicação Visual', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4183, '095', 'a', '6.10.00.00-0', '6.10.00.00-0 Serviço Social', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4184, '095', 'a', 'servico', '6.10.00.00-0 Serviço Social', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4185, '095', 'a', 'social', '6.10.00.00-0 Serviço Social', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4186, '095', 'a', '6.10.01.00-7', '6.10.01.00-7 Fundamentos do Serviço Social', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4187, '095', 'a', 'fundamentos', '6.10.01.00-7 Fundamentos do Serviço Social', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4188, '095', 'a', 'do', '6.10.01.00-7 Fundamentos do Serviço Social', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4189, '095', 'a', 'servico', '6.10.01.00-7 Fundamentos do Serviço Social', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4190, '095', 'a', 'social', '6.10.01.00-7 Fundamentos do Serviço Social', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4191, '095', 'a', '6.10.02.00-3', '6.10.02.00-3 Serviço Social Aplicado', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4192, '095', 'a', 'servico', '6.10.02.00-3 Serviço Social Aplicado', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4193, '095', 'a', 'social', '6.10.02.00-3 Serviço Social Aplicado', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4194, '095', 'a', 'aplicado', '6.10.02.00-3 Serviço Social Aplicado', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4195, '095', 'a', '6.10.02.01-1', '6.10.02.01-1 Serviço Social do Trabalho', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4196, '095', 'a', 'servico', '6.10.02.01-1 Serviço Social do Trabalho', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4197, '095', 'a', 'social', '6.10.02.01-1 Serviço Social do Trabalho', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4198, '095', 'a', 'do', '6.10.02.01-1 Serviço Social do Trabalho', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4199, '095', 'a', 'trabalho', '6.10.02.01-1 Serviço Social do Trabalho', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4200, '095', 'a', '6.10.02.02-0', '6.10.02.02-0 Serviço Social da Educação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4201, '095', 'a', 'servico', '6.10.02.02-0 Serviço Social da Educação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4202, '095', 'a', 'social', '6.10.02.02-0 Serviço Social da Educação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4203, '095', 'a', 'da', '6.10.02.02-0 Serviço Social da Educação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4204, '095', 'a', 'educacao', '6.10.02.02-0 Serviço Social da Educação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4205, '095', 'a', '6.10.02.03-8', '6.10.02.03-8 Serviço Social do Menor', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4206, '095', 'a', 'servico', '6.10.02.03-8 Serviço Social do Menor', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4207, '095', 'a', 'social', '6.10.02.03-8 Serviço Social do Menor', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4208, '095', 'a', 'do', '6.10.02.03-8 Serviço Social do Menor', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4209, '095', 'a', 'menor', '6.10.02.03-8 Serviço Social do Menor', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4210, '095', 'a', '6.10.02.04-6', '6.10.02.04-6 Serviço Social da Saúde', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4211, '095', 'a', 'servico', '6.10.02.04-6 Serviço Social da Saúde', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4212, '095', 'a', 'social', '6.10.02.04-6 Serviço Social da Saúde', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4213, '095', 'a', 'da', '6.10.02.04-6 Serviço Social da Saúde', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4214, '095', 'a', 'saude', '6.10.02.04-6 Serviço Social da Saúde', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4215, '095', 'a', '6.10.02.05-4', '6.10.02.05-4 Serviço Social da Habitação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4216, '095', 'a', 'servico', '6.10.02.05-4 Serviço Social da Habitação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4217, '095', 'a', 'social', '6.10.02.05-4 Serviço Social da Habitação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4218, '095', 'a', 'da', '6.10.02.05-4 Serviço Social da Habitação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4219, '095', 'a', 'habitacao', '6.10.02.05-4 Serviço Social da Habitação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4220, '095', 'a', '6.11.00.00-5', '6.11.00.00-5 Economia Doméstica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4221, '095', 'a', 'economia', '6.11.00.00-5 Economia Doméstica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4222, '095', 'a', 'domestica', '6.11.00.00-5 Economia Doméstica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4223, '095', 'a', '6.12.00.00-0', '6.12.00.00-0 Desenho Industrial', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4224, '095', 'a', 'desenho', '6.12.00.00-0 Desenho Industrial', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4225, '095', 'a', 'industrial', '6.12.00.00-0 Desenho Industrial', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4226, '095', 'a', '6.12.01.00-6', '6.12.01.00-6 Programação Visual', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4227, '095', 'a', 'programacao', '6.12.01.00-6 Programação Visual', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4228, '095', 'a', 'visual', '6.12.01.00-6 Programação Visual', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4229, '095', 'a', '6.12.02.00-2', '6.12.02.00-2 Desenho de Produto', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4230, '095', 'a', 'desenho', '6.12.02.00-2 Desenho de Produto', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4231, '095', 'a', 'de', '6.12.02.00-2 Desenho de Produto', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4232, '095', 'a', 'produto', '6.12.02.00-2 Desenho de Produto', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4233, '095', 'a', '6.13.00.00-4', '6.13.00.00-4 Turismo', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4234, '095', 'a', 'turismo', '6.13.00.00-4 Turismo', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4235, '095', 'a', '7.00.00.00-0', '7.00.00.00-0 Ciências Humanas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4236, '095', 'a', 'ciencias', '7.00.00.00-0 Ciências Humanas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4237, '095', 'a', 'humanas', '7.00.00.00-0 Ciências Humanas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4238, '095', 'a', '7.01.00.00-4', '7.01.00.00-4 Filosofia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4239, '095', 'a', 'filosofia', '7.01.00.00-4 Filosofia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4240, '095', 'a', '7.01.01.00-0', '7.01.01.00-0 História da Filosofia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4241, '095', 'a', 'historia', '7.01.01.00-0 História da Filosofia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4242, '095', 'a', 'da', '7.01.01.00-0 História da Filosofia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4243, '095', 'a', 'filosofia', '7.01.01.00-0 História da Filosofia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4244, '095', 'a', '7.01.02.00-7', '7.01.02.00-7 Metafísica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4245, '095', 'a', 'metafisica', '7.01.02.00-7 Metafísica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4246, '095', 'a', '7.01.03.00-3', '7.01.03.00-3 Lógica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4247, '095', 'a', 'logica', '7.01.03.00-3 Lógica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4248, '095', 'a', '7.01.04.00-0', '7.01.04.00-0 Ética', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4249, '095', 'a', 'etica', '7.01.04.00-0 Ética', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4250, '095', 'a', '7.01.05.00-6', '7.01.05.00-6 Epistemologia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4251, '095', 'a', 'epistemologia', '7.01.05.00-6 Epistemologia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4252, '095', 'a', '7.01.06.00-2', '7.01.06.00-2 Filosofia Brasileira', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4253, '095', 'a', 'filosofia', '7.01.06.00-2 Filosofia Brasileira', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4254, '095', 'a', 'brasileira', '7.01.06.00-2 Filosofia Brasileira', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4255, '095', 'a', '7.02.00.00-9', '7.02.00.00-9 Sociologia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4256, '095', 'a', 'sociologia', '7.02.00.00-9 Sociologia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4257, '095', 'a', '7.02.01.00-5', '7.02.01.00-5 Fundamentos da Sociologia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4258, '095', 'a', 'fundamentos', '7.02.01.00-5 Fundamentos da Sociologia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4259, '095', 'a', 'da', '7.02.01.00-5 Fundamentos da Sociologia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4260, '095', 'a', 'sociologia', '7.02.01.00-5 Fundamentos da Sociologia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4261, '095', 'a', '7.02.01.01-3', '7.02.01.01-3 Teoria Sociológica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4262, '095', 'a', 'teoria', '7.02.01.01-3 Teoria Sociológica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4263, '095', 'a', 'sociologica', '7.02.01.01-3 Teoria Sociológica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4264, '095', 'a', '7.02.01.02-1', '7.02.01.02-1 História da Sociologia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4265, '095', 'a', 'historia', '7.02.01.02-1 História da Sociologia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4266, '095', 'a', 'da', '7.02.01.02-1 História da Sociologia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4267, '095', 'a', 'sociologia', '7.02.01.02-1 História da Sociologia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4268, '095', 'a', '7.02.02.00-1', '7.02.02.00-1 Sociologia do Conhecimento', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4269, '095', 'a', 'sociologia', '7.02.02.00-1 Sociologia do Conhecimento', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4270, '095', 'a', 'do', '7.02.02.00-1 Sociologia do Conhecimento', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4271, '095', 'a', 'conhecimento', '7.02.02.00-1 Sociologia do Conhecimento', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4272, '095', 'a', '7.02.03.00-8', '7.02.03.00-8 Sociologia do Desenvolvimento', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4273, '095', 'a', 'sociologia', '7.02.03.00-8 Sociologia do Desenvolvimento', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4274, '095', 'a', 'do', '7.02.03.00-8 Sociologia do Desenvolvimento', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4275, '095', 'a', 'desenvolvimento', '7.02.03.00-8 Sociologia do Desenvolvimento', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4276, '095', 'a', '7.02.04.00-4', '7.02.04.00-4 Sociologia Urbana', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4277, '095', 'a', 'sociologia', '7.02.04.00-4 Sociologia Urbana', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4278, '095', 'a', 'urbana', '7.02.04.00-4 Sociologia Urbana', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4279, '095', 'a', '7.02.05.00-0', '7.02.05.00-0 Sociologia Rural', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4280, '095', 'a', 'sociologia', '7.02.05.00-0 Sociologia Rural', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4281, '095', 'a', 'rural', '7.02.05.00-0 Sociologia Rural', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4282, '095', 'a', '7.02.06.00-7', '7.02.06.00-7 Sociologia da Saúde', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4283, '095', 'a', 'sociologia', '7.02.06.00-7 Sociologia da Saúde', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4284, '095', 'a', 'da', '7.02.06.00-7 Sociologia da Saúde', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4285, '095', 'a', 'saude', '7.02.06.00-7 Sociologia da Saúde', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4286, '095', 'a', '7.02.07.00-3', '7.02.07.00-3 Outras Sociologias Específicas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4287, '095', 'a', 'outras', '7.02.07.00-3 Outras Sociologias Específicas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4288, '095', 'a', 'sociologias', '7.02.07.00-3 Outras Sociologias Específicas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4289, '095', 'a', 'especificas', '7.02.07.00-3 Outras Sociologias Específicas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4290, '095', 'a', '7.03.00.00-3', '7.03.00.00-3 Antropologia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4291, '095', 'a', 'antropologia', '7.03.00.00-3 Antropologia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4292, '095', 'a', '7.03.01.00-0', '7.03.01.00-0 Teoria Antropológica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4293, '095', 'a', 'teoria', '7.03.01.00-0 Teoria Antropológica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4294, '095', 'a', 'antropologica', '7.03.01.00-0 Teoria Antropológica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4295, '095', 'a', '7.03.02.00-6', '7.03.02.00-6 Etnologia Indígena', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4296, '095', 'a', 'etnologia', '7.03.02.00-6 Etnologia Indígena', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4297, '095', 'a', 'indigena', '7.03.02.00-6 Etnologia Indígena', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4298, '095', 'a', '7.03.03.00-2', '7.03.03.00-2 Antropologia Urbana', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4299, '095', 'a', 'antropologia', '7.03.03.00-2 Antropologia Urbana', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4300, '095', 'a', 'urbana', '7.03.03.00-2 Antropologia Urbana', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4301, '095', 'a', '7.03.04.00-9', '7.03.04.00-9 Antropologia Rural', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4302, '095', 'a', 'antropologia', '7.03.04.00-9 Antropologia Rural', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4303, '095', 'a', 'rural', '7.03.04.00-9 Antropologia Rural', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4304, '095', 'a', '7.03.05.00-5', '7.03.05.00-5 Antropologia das Populações Afro-Brasileiras', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4305, '095', 'a', 'antropologia', '7.03.05.00-5 Antropologia das Populações Afro-Brasileiras', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4306, '095', 'a', 'das', '7.03.05.00-5 Antropologia das Populações Afro-Brasileiras', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4307, '095', 'a', 'populacoes', '7.03.05.00-5 Antropologia das Populações Afro-Brasileiras', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4308, '095', 'a', 'afro-brasileiras', '7.03.05.00-5 Antropologia das Populações Afro-Brasileiras', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4309, '095', 'a', '7.04.00.00-8', '7.04.00.00-8 Arqueologia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4310, '095', 'a', 'arqueologia', '7.04.00.00-8 Arqueologia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4311, '095', 'a', '7.04.01.00-4', '7.04.01.00-4 Teoria e Método em Arqueologia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4312, '095', 'a', 'teoria', '7.04.01.00-4 Teoria e Método em Arqueologia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4313, '095', 'a', 'metodo', '7.04.01.00-4 Teoria e Método em Arqueologia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4314, '095', 'a', 'em', '7.04.01.00-4 Teoria e Método em Arqueologia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4315, '095', 'a', 'arqueologia', '7.04.01.00-4 Teoria e Método em Arqueologia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4316, '095', 'a', '7.04.02.00-0', '7.04.02.00-0 Arqueologia Pré-Histórica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4317, '095', 'a', 'arqueologia', '7.04.02.00-0 Arqueologia Pré-Histórica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4318, '095', 'a', 'pre-historica', '7.04.02.00-0 Arqueologia Pré-Histórica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4319, '095', 'a', '7.04.03.00-7', '7.04.03.00-7 Arqueologia Histórica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4320, '095', 'a', 'arqueologia', '7.04.03.00-7 Arqueologia Histórica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4321, '095', 'a', 'historica', '7.04.03.00-7 Arqueologia Histórica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4322, '095', 'a', '7.05.00.00-2', '7.05.00.00-2 História', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4323, '095', 'a', 'historia', '7.05.00.00-2 História', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4324, '095', 'a', '7.05.01.00-9', '7.05.01.00-9 Teoria e Filosofia da História', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4325, '095', 'a', 'teoria', '7.05.01.00-9 Teoria e Filosofia da História', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4326, '095', 'a', 'filosofia', '7.05.01.00-9 Teoria e Filosofia da História', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4327, '095', 'a', 'da', '7.05.01.00-9 Teoria e Filosofia da História', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4328, '095', 'a', 'historia', '7.05.01.00-9 Teoria e Filosofia da História', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4329, '095', 'a', '7.05.02.00-5', '7.05.02.00-5 História Antiga e Medieval', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4330, '095', 'a', 'historia', '7.05.02.00-5 História Antiga e Medieval', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4331, '095', 'a', 'antiga', '7.05.02.00-5 História Antiga e Medieval', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4332, '095', 'a', 'medieval', '7.05.02.00-5 História Antiga e Medieval', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4333, '095', 'a', '7.05.03.00-1', '7.05.03.00-1 História Moderna e Contemporânea', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4334, '095', 'a', 'historia', '7.05.03.00-1 História Moderna e Contemporânea', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4335, '095', 'a', 'moderna', '7.05.03.00-1 História Moderna e Contemporânea', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4336, '095', 'a', 'contemporanea', '7.05.03.00-1 História Moderna e Contemporânea', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4337, '095', 'a', '7.05.04.00-8', '7.05.04.00-8 História da América', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4338, '095', 'a', 'historia', '7.05.04.00-8 História da América', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4339, '095', 'a', 'da', '7.05.04.00-8 História da América', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4340, '095', 'a', 'america', '7.05.04.00-8 História da América', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4341, '095', 'a', '7.05.04.01-6', '7.05.04.01-6 História dos Estados Unidos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4342, '095', 'a', 'historia', '7.05.04.01-6 História dos Estados Unidos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4343, '095', 'a', 'dos', '7.05.04.01-6 História dos Estados Unidos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4344, '095', 'a', 'estados', '7.05.04.01-6 História dos Estados Unidos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4345, '095', 'a', 'unidos', '7.05.04.01-6 História dos Estados Unidos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4346, '095', 'a', '7.05.04.02-4', '7.05.04.02-4 História Latino-Americana', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4347, '095', 'a', 'historia', '7.05.04.02-4 História Latino-Americana', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4348, '095', 'a', 'latino-americana', '7.05.04.02-4 História Latino-Americana', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4349, '095', 'a', '7.05.05.00-4', '7.05.05.00-4 História do Brasil', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4350, '095', 'a', 'historia', '7.05.05.00-4 História do Brasil', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4351, '095', 'a', 'do', '7.05.05.00-4 História do Brasil', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4352, '095', 'a', 'brasil', '7.05.05.00-4 História do Brasil', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4353, '095', 'a', '7.05.05.01-2', '7.05.05.01-2 História do Brasil Colônia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4354, '095', 'a', 'historia', '7.05.05.01-2 História do Brasil Colônia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4355, '095', 'a', 'do', '7.05.05.01-2 História do Brasil Colônia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4356, '095', 'a', 'brasil', '7.05.05.01-2 História do Brasil Colônia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4357, '095', 'a', 'colonia', '7.05.05.01-2 História do Brasil Colônia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4358, '095', 'a', '7.05.05.02-0', '7.05.05.02-0 História do Brasil Império', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4359, '095', 'a', 'historia', '7.05.05.02-0 História do Brasil Império', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4360, '095', 'a', 'do', '7.05.05.02-0 História do Brasil Império', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4361, '095', 'a', 'brasil', '7.05.05.02-0 História do Brasil Império', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4362, '095', 'a', 'imperio', '7.05.05.02-0 História do Brasil Império', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4363, '095', 'a', '7.05.05.03-9', '7.05.05.03-9 História do Brasil República', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4364, '095', 'a', 'historia', '7.05.05.03-9 História do Brasil República', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4365, '095', 'a', 'do', '7.05.05.03-9 História do Brasil República', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4366, '095', 'a', 'brasil', '7.05.05.03-9 História do Brasil República', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4367, '095', 'a', 'republica', '7.05.05.03-9 História do Brasil República', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4368, '095', 'a', '7.05.05.04-7', '7.05.05.04-7 História Regional do Brasil', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4369, '095', 'a', 'historia', '7.05.05.04-7 História Regional do Brasil', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4370, '095', 'a', 'regional', '7.05.05.04-7 História Regional do Brasil', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4371, '095', 'a', 'do', '7.05.05.04-7 História Regional do Brasil', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4372, '095', 'a', 'brasil', '7.05.05.04-7 História Regional do Brasil', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4373, '095', 'a', '7.05.06.00-0', '7.05.06.00-0 História das Ciências', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4374, '095', 'a', 'historia', '7.05.06.00-0 História das Ciências', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4375, '095', 'a', 'das', '7.05.06.00-0 História das Ciências', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4376, '095', 'a', 'ciencias', '7.05.06.00-0 História das Ciências', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4377, '095', 'a', '7.06.00.00-7', '7.06.00.00-7 Geografia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4378, '095', 'a', 'geografia', '7.06.00.00-7 Geografia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4379, '095', 'a', '7.06.01.00-3', '7.06.01.00-3 Geografia Humana', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4380, '095', 'a', 'geografia', '7.06.01.00-3 Geografia Humana', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4381, '095', 'a', 'humana', '7.06.01.00-3 Geografia Humana', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4382, '095', 'a', '7.06.01.01-1', '7.06.01.01-1 Geografia da População', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4383, '095', 'a', 'geografia', '7.06.01.01-1 Geografia da População', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4384, '095', 'a', 'da', '7.06.01.01-1 Geografia da População', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4385, '095', 'a', 'populacao', '7.06.01.01-1 Geografia da População', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4386, '095', 'a', '7.06.01.02-0', '7.06.01.02-0 Geografia Agrária', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4387, '095', 'a', 'geografia', '7.06.01.02-0 Geografia Agrária', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4388, '095', 'a', 'agraria', '7.06.01.02-0 Geografia Agrária', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4389, '095', 'a', '7.06.01.03-8', '7.06.01.03-8 Geografia Urbana', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4390, '095', 'a', 'geografia', '7.06.01.03-8 Geografia Urbana', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4391, '095', 'a', 'urbana', '7.06.01.03-8 Geografia Urbana', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4392, '095', 'a', '7.06.01.04-6', '7.06.01.04-6 Geografia Econômica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4393, '095', 'a', 'geografia', '7.06.01.04-6 Geografia Econômica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4394, '095', 'a', 'economica', '7.06.01.04-6 Geografia Econômica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4395, '095', 'a', '7.06.01.05-4', '7.06.01.05-4 Geografia Política', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4396, '095', 'a', 'geografia', '7.06.01.05-4 Geografia Política', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4397, '095', 'a', 'politica', '7.06.01.05-4 Geografia Política', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4398, '095', 'a', '7.06.02.00-0', '7.06.02.00-0 Geografia Regional', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4399, '095', 'a', 'geografia', '7.06.02.00-0 Geografia Regional', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4400, '095', 'a', 'regional', '7.06.02.00-0 Geografia Regional', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4401, '095', 'a', '7.06.02.01-8', '7.06.02.01-8 Teoria do Desenvolvimento Regional', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4402, '095', 'a', 'teoria', '7.06.02.01-8 Teoria do Desenvolvimento Regional', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4403, '095', 'a', 'do', '7.06.02.01-8 Teoria do Desenvolvimento Regional', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4404, '095', 'a', 'desenvolvimento', '7.06.02.01-8 Teoria do Desenvolvimento Regional', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4405, '095', 'a', 'regional', '7.06.02.01-8 Teoria do Desenvolvimento Regional', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4406, '095', 'a', '7.06.02.02-6', '7.06.02.02-6 Regionalização', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4407, '095', 'a', 'regionalizacao', '7.06.02.02-6 Regionalização', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4408, '095', 'a', '7.06.02.03-4', '7.06.02.03-4 Análise Regional', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4409, '095', 'a', 'analise', '7.06.02.03-4 Análise Regional', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4410, '095', 'a', 'regional', '7.06.02.03-4 Análise Regional', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4411, '095', 'a', '7.07.00.00-1', '7.07.00.00-1 Psicologia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4412, '095', 'a', 'psicologia', '7.07.00.00-1 Psicologia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4413, '095', 'a', '7.07.01.00-8', '7.07.01.00-8 Fundamentos e Medidas da Psicologia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4414, '095', 'a', 'fundamentos', '7.07.01.00-8 Fundamentos e Medidas da Psicologia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4415, '095', 'a', 'medidas', '7.07.01.00-8 Fundamentos e Medidas da Psicologia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4416, '095', 'a', 'da', '7.07.01.00-8 Fundamentos e Medidas da Psicologia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4417, '095', 'a', 'psicologia', '7.07.01.00-8 Fundamentos e Medidas da Psicologia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4418, '095', 'a', '7.07.01.01-6', '7.07.01.01-6 História, Teorias e Sistemas em Psicologia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4419, '095', 'a', 'historia,', '7.07.01.01-6 História, Teorias e Sistemas em Psicologia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4420, '095', 'a', 'teorias', '7.07.01.01-6 História, Teorias e Sistemas em Psicologia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4421, '095', 'a', 'sistemas', '7.07.01.01-6 História, Teorias e Sistemas em Psicologia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4422, '095', 'a', 'em', '7.07.01.01-6 História, Teorias e Sistemas em Psicologia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4423, '095', 'a', 'psicologia', '7.07.01.01-6 História, Teorias e Sistemas em Psicologia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4424, '095', 'a', '7.07.01.02-4', '7.07.01.02-4 Metodologia, Instrumentação e Equipamento em Psicologia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4425, '095', 'a', 'metodologia,', '7.07.01.02-4 Metodologia, Instrumentação e Equipamento em Psicologia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4426, '095', 'a', 'instrumentacao', '7.07.01.02-4 Metodologia, Instrumentação e Equipamento em Psicologia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4427, '095', 'a', 'equipamento', '7.07.01.02-4 Metodologia, Instrumentação e Equipamento em Psicologia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4428, '095', 'a', 'em', '7.07.01.02-4 Metodologia, Instrumentação e Equipamento em Psicologia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4429, '095', 'a', 'psicologia', '7.07.01.02-4 Metodologia, Instrumentação e Equipamento em Psicologia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4430, '095', 'a', '7.07.01.03-2', '7.07.01.03-2 Construção e Validade de Testes, Escalas e Outras Medidas Psicológicas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4431, '095', 'a', 'construcao', '7.07.01.03-2 Construção e Validade de Testes, Escalas e Outras Medidas Psicológicas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4432, '095', 'a', 'validade', '7.07.01.03-2 Construção e Validade de Testes, Escalas e Outras Medidas Psicológicas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4433, '095', 'a', 'de', '7.07.01.03-2 Construção e Validade de Testes, Escalas e Outras Medidas Psicológicas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4434, '095', 'a', 'testes,', '7.07.01.03-2 Construção e Validade de Testes, Escalas e Outras Medidas Psicológicas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4435, '095', 'a', 'escalas', '7.07.01.03-2 Construção e Validade de Testes, Escalas e Outras Medidas Psicológicas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4436, '095', 'a', 'outras', '7.07.01.03-2 Construção e Validade de Testes, Escalas e Outras Medidas Psicológicas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4437, '095', 'a', 'medidas', '7.07.01.03-2 Construção e Validade de Testes, Escalas e Outras Medidas Psicológicas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4438, '095', 'a', 'psicologicas', '7.07.01.03-2 Construção e Validade de Testes, Escalas e Outras Medidas Psicológicas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4439, '095', 'a', '7.07.01.04-0', '7.07.01.04-0 Técnicas de Processamento Estatístico, Matemático e Computacional em Psicologia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4440, '095', 'a', 'tecnicas', '7.07.01.04-0 Técnicas de Processamento Estatístico, Matemático e Computacional em Psicologia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4441, '095', 'a', 'de', '7.07.01.04-0 Técnicas de Processamento Estatístico, Matemático e Computacional em Psicologia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4442, '095', 'a', 'processamento', '7.07.01.04-0 Técnicas de Processamento Estatístico, Matemático e Computacional em Psicologia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4443, '095', 'a', 'estatistico,', '7.07.01.04-0 Técnicas de Processamento Estatístico, Matemático e Computacional em Psicologia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4444, '095', 'a', 'matematico', '7.07.01.04-0 Técnicas de Processamento Estatístico, Matemático e Computacional em Psicologia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4445, '095', 'a', 'computacional', '7.07.01.04-0 Técnicas de Processamento Estatístico, Matemático e Computacional em Psicologia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4446, '095', 'a', 'em', '7.07.01.04-0 Técnicas de Processamento Estatístico, Matemático e Computacional em Psicologia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4447, '095', 'a', 'psicologia', '7.07.01.04-0 Técnicas de Processamento Estatístico, Matemático e Computacional em Psicologia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4448, '095', 'a', '7.07.02.00-4', '7.07.02.00-4 Psicologia Experimental', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4449, '095', 'a', 'psicologia', '7.07.02.00-4 Psicologia Experimental', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4450, '095', 'a', 'experimental', '7.07.02.00-4 Psicologia Experimental', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4451, '095', 'a', '7.07.02.01-2', '7.07.02.01-2 Processos Perceptuais e Motores', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4452, '095', 'a', 'processos', '7.07.02.01-2 Processos Perceptuais e Motores', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4453, '095', 'a', 'perceptuais', '7.07.02.01-2 Processos Perceptuais e Motores', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4454, '095', 'a', 'motores', '7.07.02.01-2 Processos Perceptuais e Motores', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4455, '095', 'a', '7.07.02.02-0', '7.07.02.02-0 Processos de Aprendizagem, Memória e Motivação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4456, '095', 'a', 'processos', '7.07.02.02-0 Processos de Aprendizagem, Memória e Motivação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4457, '095', 'a', 'de', '7.07.02.02-0 Processos de Aprendizagem, Memória e Motivação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4458, '095', 'a', 'aprendizagem,', '7.07.02.02-0 Processos de Aprendizagem, Memória e Motivação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4459, '095', 'a', 'memoria', '7.07.02.02-0 Processos de Aprendizagem, Memória e Motivação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4460, '095', 'a', 'motivacao', '7.07.02.02-0 Processos de Aprendizagem, Memória e Motivação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4461, '095', 'a', '7.07.02.03-9', '7.07.02.03-9 Processos Cognitivos e Atencionais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4462, '095', 'a', 'processos', '7.07.02.03-9 Processos Cognitivos e Atencionais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4463, '095', 'a', 'cognitivos', '7.07.02.03-9 Processos Cognitivos e Atencionais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4464, '095', 'a', 'atencionais', '7.07.02.03-9 Processos Cognitivos e Atencionais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4465, '095', 'a', '7.07.02.04-7', '7.07.02.04-7 Estados Subjetivos e Emoção', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4466, '095', 'a', 'estados', '7.07.02.04-7 Estados Subjetivos e Emoção', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4467, '095', 'a', 'subjetivos', '7.07.02.04-7 Estados Subjetivos e Emoção', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4468, '095', 'a', 'emocao', '7.07.02.04-7 Estados Subjetivos e Emoção', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4469, '095', 'a', '7.07.03.00-0', '7.07.03.00-0 Psicologia Fisiológica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4470, '095', 'a', 'psicologia', '7.07.03.00-0 Psicologia Fisiológica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4471, '095', 'a', 'fisiologica', '7.07.03.00-0 Psicologia Fisiológica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4472, '095', 'a', '7.07.03.01-9', '7.07.03.01-9 Neurologia, Eletrofisiologia e Comportamento', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4473, '095', 'a', 'neurologia,', '7.07.03.01-9 Neurologia, Eletrofisiologia e Comportamento', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4474, '095', 'a', 'eletrofisiologia', '7.07.03.01-9 Neurologia, Eletrofisiologia e Comportamento', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4475, '095', 'a', 'comportamento', '7.07.03.01-9 Neurologia, Eletrofisiologia e Comportamento', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4476, '095', 'a', '7.07.03.02-7', '7.07.03.02-7 Processos Psico-Fisiológicos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4477, '095', 'a', 'processos', '7.07.03.02-7 Processos Psico-Fisiológicos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4478, '095', 'a', 'psico-fisiologicos', '7.07.03.02-7 Processos Psico-Fisiológicos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4479, '095', 'a', '7.07.03.03-5', '7.07.03.03-5 Estimulação Elétrica e com Drogas; Comportamento', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4480, '095', 'a', 'estimulacao', '7.07.03.03-5 Estimulação Elétrica e com Drogas; Comportamento', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4481, '095', 'a', 'eletrica', '7.07.03.03-5 Estimulação Elétrica e com Drogas; Comportamento', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4482, '095', 'a', 'com', '7.07.03.03-5 Estimulação Elétrica e com Drogas; Comportamento', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4483, '095', 'a', 'drogas;', '7.07.03.03-5 Estimulação Elétrica e com Drogas; Comportamento', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4484, '095', 'a', 'comportamento', '7.07.03.03-5 Estimulação Elétrica e com Drogas; Comportamento', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4485, '095', 'a', '7.07.03.04-3', '7.07.03.04-3 Psicobiologia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4486, '095', 'a', 'psicobiologia', '7.07.03.04-3 Psicobiologia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4487, '095', 'a', '7.07.04.00-7', '7.07.04.00-7 Psicologia Comparativa', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4488, '095', 'a', 'psicologia', '7.07.04.00-7 Psicologia Comparativa', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4489, '095', 'a', 'comparativa', '7.07.04.00-7 Psicologia Comparativa', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4490, '095', 'a', '7.07.04.01-5', '7.07.04.01-5 Estudos Naturalísticos do Comportamento Animal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4491, '095', 'a', 'estudos', '7.07.04.01-5 Estudos Naturalísticos do Comportamento Animal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4492, '095', 'a', 'naturalisticos', '7.07.04.01-5 Estudos Naturalísticos do Comportamento Animal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4493, '095', 'a', 'do', '7.07.04.01-5 Estudos Naturalísticos do Comportamento Animal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4494, '095', 'a', 'comportamento', '7.07.04.01-5 Estudos Naturalísticos do Comportamento Animal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4495, '095', 'a', 'animal', '7.07.04.01-5 Estudos Naturalísticos do Comportamento Animal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4496, '095', 'a', '7.07.04.02-3', '7.07.04.02-3 Mecanismos Instintivos e Processos Sociais em Animais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4497, '095', 'a', 'mecanismos', '7.07.04.02-3 Mecanismos Instintivos e Processos Sociais em Animais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4498, '095', 'a', 'instintivos', '7.07.04.02-3 Mecanismos Instintivos e Processos Sociais em Animais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4499, '095', 'a', 'processos', '7.07.04.02-3 Mecanismos Instintivos e Processos Sociais em Animais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4500, '095', 'a', 'sociais', '7.07.04.02-3 Mecanismos Instintivos e Processos Sociais em Animais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4501, '095', 'a', 'em', '7.07.04.02-3 Mecanismos Instintivos e Processos Sociais em Animais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4502, '095', 'a', 'animais', '7.07.04.02-3 Mecanismos Instintivos e Processos Sociais em Animais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4503, '095', 'a', '7.07.05.00-3', '7.07.05.00-3 Psicologia Social', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4504, '095', 'a', 'psicologia', '7.07.05.00-3 Psicologia Social', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4505, '095', 'a', 'social', '7.07.05.00-3 Psicologia Social', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4506, '095', 'a', '7.07.05.01-1', '7.07.05.01-1 Relações Interpessoais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4507, '095', 'a', 'relacoes', '7.07.05.01-1 Relações Interpessoais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4508, '095', 'a', 'interpessoais', '7.07.05.01-1 Relações Interpessoais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4509, '095', 'a', '7.07.05.02-0', '7.07.05.02-0 Processos Grupais e de Comunicação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4510, '095', 'a', 'processos', '7.07.05.02-0 Processos Grupais e de Comunicação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4511, '095', 'a', 'grupais', '7.07.05.02-0 Processos Grupais e de Comunicação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4512, '095', 'a', 'de', '7.07.05.02-0 Processos Grupais e de Comunicação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4513, '095', 'a', 'comunicacao', '7.07.05.02-0 Processos Grupais e de Comunicação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4514, '095', 'a', '7.07.05.03-8', '7.07.05.03-8 Papéis e Estruturas Sociais; Indivíduo', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4515, '095', 'a', 'papeis', '7.07.05.03-8 Papéis e Estruturas Sociais; Indivíduo', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4516, '095', 'a', 'estruturas', '7.07.05.03-8 Papéis e Estruturas Sociais; Indivíduo', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4517, '095', 'a', 'sociais;', '7.07.05.03-8 Papéis e Estruturas Sociais; Indivíduo', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4518, '095', 'a', 'individuo', '7.07.05.03-8 Papéis e Estruturas Sociais; Indivíduo', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4519, '095', 'a', '7.07.06.00-0', '7.07.06.00-0 Psicologia Cognitiva', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4520, '095', 'a', 'psicologia', '7.07.06.00-0 Psicologia Cognitiva', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4521, '095', 'a', 'cognitiva', '7.07.06.00-0 Psicologia Cognitiva', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4522, '095', 'a', '7.07.07.00-6', '7.07.07.00-6 Psicologia do Desenvolvimento Humano', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4523, '095', 'a', 'psicologia', '7.07.07.00-6 Psicologia do Desenvolvimento Humano', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4524, '095', 'a', 'do', '7.07.07.00-6 Psicologia do Desenvolvimento Humano', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4525, '095', 'a', 'desenvolvimento', '7.07.07.00-6 Psicologia do Desenvolvimento Humano', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4526, '095', 'a', 'humano', '7.07.07.00-6 Psicologia do Desenvolvimento Humano', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4527, '095', 'a', '7.07.07.01-4', '7.07.07.01-4 Processos Perceptuais e Cognitivos; Desenvolvimento', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4528, '095', 'a', 'processos', '7.07.07.01-4 Processos Perceptuais e Cognitivos; Desenvolvimento', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4529, '095', 'a', 'perceptuais', '7.07.07.01-4 Processos Perceptuais e Cognitivos; Desenvolvimento', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4530, '095', 'a', 'cognitivos;', '7.07.07.01-4 Processos Perceptuais e Cognitivos; Desenvolvimento', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4531, '095', 'a', 'desenvolvimento', '7.07.07.01-4 Processos Perceptuais e Cognitivos; Desenvolvimento', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4532, '095', 'a', '7.07.07.02-2', '7.07.07.02-2 Desenvolvimento Social e da Personalidade', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4533, '095', 'a', 'desenvolvimento', '7.07.07.02-2 Desenvolvimento Social e da Personalidade', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4534, '095', 'a', 'social', '7.07.07.02-2 Desenvolvimento Social e da Personalidade', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4535, '095', 'a', 'da', '7.07.07.02-2 Desenvolvimento Social e da Personalidade', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4536, '095', 'a', 'personalidade', '7.07.07.02-2 Desenvolvimento Social e da Personalidade', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4537, '095', 'a', '7.07.08.00-2', '7.07.08.00-2 Psicologia do Ensino e da Aprendizagem', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4538, '095', 'a', 'psicologia', '7.07.08.00-2 Psicologia do Ensino e da Aprendizagem', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4539, '095', 'a', 'do', '7.07.08.00-2 Psicologia do Ensino e da Aprendizagem', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4540, '095', 'a', 'ensino', '7.07.08.00-2 Psicologia do Ensino e da Aprendizagem', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4541, '095', 'a', 'da', '7.07.08.00-2 Psicologia do Ensino e da Aprendizagem', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4542, '095', 'a', 'aprendizagem', '7.07.08.00-2 Psicologia do Ensino e da Aprendizagem', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4543, '095', 'a', '7.07.08.01-0', '7.07.08.01-0 Planejamento Institucional', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4544, '095', 'a', 'planejamento', '7.07.08.01-0 Planejamento Institucional', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4545, '095', 'a', 'institucional', '7.07.08.01-0 Planejamento Institucional', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4546, '095', 'a', '7.07.08.02-9', '7.07.08.02-9 Programação de Condições de Ensino', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4547, '095', 'a', 'programacao', '7.07.08.02-9 Programação de Condições de Ensino', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4548, '095', 'a', 'de', '7.07.08.02-9 Programação de Condições de Ensino', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4549, '095', 'a', 'condicoes', '7.07.08.02-9 Programação de Condições de Ensino', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4550, '095', 'a', 'de', '7.07.08.02-9 Programação de Condições de Ensino', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4551, '095', 'a', 'ensino', '7.07.08.02-9 Programação de Condições de Ensino', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4552, '095', 'a', '7.07.08.03-7', '7.07.08.03-7 Treinamento de Pessoal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4553, '095', 'a', 'treinamento', '7.07.08.03-7 Treinamento de Pessoal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4554, '095', 'a', 'de', '7.07.08.03-7 Treinamento de Pessoal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4555, '095', 'a', 'pessoal', '7.07.08.03-7 Treinamento de Pessoal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4556, '095', 'a', '7.07.08.04-5', '7.07.08.04-5 Aprendizagem e Desempenho Acadêmicos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4557, '095', 'a', 'aprendizagem', '7.07.08.04-5 Aprendizagem e Desempenho Acadêmicos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4558, '095', 'a', 'desempenho', '7.07.08.04-5 Aprendizagem e Desempenho Acadêmicos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4559, '095', 'a', 'academicos', '7.07.08.04-5 Aprendizagem e Desempenho Acadêmicos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4560, '095', 'a', '7.07.08.05-3', '7.07.08.05-3 Ensino e Aprendizagem na Sala de Aula', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4561, '095', 'a', 'ensino', '7.07.08.05-3 Ensino e Aprendizagem na Sala de Aula', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4562, '095', 'a', 'aprendizagem', '7.07.08.05-3 Ensino e Aprendizagem na Sala de Aula', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4563, '095', 'a', 'na', '7.07.08.05-3 Ensino e Aprendizagem na Sala de Aula', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4564, '095', 'a', 'sala', '7.07.08.05-3 Ensino e Aprendizagem na Sala de Aula', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4565, '095', 'a', 'de', '7.07.08.05-3 Ensino e Aprendizagem na Sala de Aula', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4566, '095', 'a', 'aula', '7.07.08.05-3 Ensino e Aprendizagem na Sala de Aula', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4567, '095', 'a', '7.07.09.00-9', '7.07.09.00-9 Psicologia do Trabalho e Organizacional', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4568, '095', 'a', 'psicologia', '7.07.09.00-9 Psicologia do Trabalho e Organizacional', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4569, '095', 'a', 'do', '7.07.09.00-9 Psicologia do Trabalho e Organizacional', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4570, '095', 'a', 'trabalho', '7.07.09.00-9 Psicologia do Trabalho e Organizacional', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4571, '095', 'a', 'organizacional', '7.07.09.00-9 Psicologia do Trabalho e Organizacional', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4572, '095', 'a', '7.07.09.01-7', '7.07.09.01-7 Análise Institucional', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4573, '095', 'a', 'analise', '7.07.09.01-7 Análise Institucional', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4574, '095', 'a', 'institucional', '7.07.09.01-7 Análise Institucional', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4575, '095', 'a', '7.07.09.02-5', '7.07.09.02-5 Recrutamento e Seleção de Pessoal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4576, '095', 'a', 'recrutamento', '7.07.09.02-5 Recrutamento e Seleção de Pessoal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4577, '095', 'a', 'selecao', '7.07.09.02-5 Recrutamento e Seleção de Pessoal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4578, '095', 'a', 'de', '7.07.09.02-5 Recrutamento e Seleção de Pessoal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4579, '095', 'a', 'pessoal', '7.07.09.02-5 Recrutamento e Seleção de Pessoal', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4580, '095', 'a', '7.07.09.03-3', '7.07.09.03-3 Treinamento e Avaliação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4581, '095', 'a', 'treinamento', '7.07.09.03-3 Treinamento e Avaliação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4582, '095', 'a', 'avaliacao', '7.07.09.03-3 Treinamento e Avaliação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4583, '095', 'a', '7.07.09.04-1', '7.07.09.04-1 Fatores Humanos no Trabalho', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4584, '095', 'a', 'fatores', '7.07.09.04-1 Fatores Humanos no Trabalho', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4585, '095', 'a', 'humanos', '7.07.09.04-1 Fatores Humanos no Trabalho', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4586, '095', 'a', 'no', '7.07.09.04-1 Fatores Humanos no Trabalho', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4587, '095', 'a', 'trabalho', '7.07.09.04-1 Fatores Humanos no Trabalho', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4588, '095', 'a', '7.07.09.05-0', '7.07.09.05-0 Planejamento Ambiental e Comportamento Humano', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4589, '095', 'a', 'planejamento', '7.07.09.05-0 Planejamento Ambiental e Comportamento Humano', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4590, '095', 'a', 'ambiental', '7.07.09.05-0 Planejamento Ambiental e Comportamento Humano', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4591, '095', 'a', 'comportamento', '7.07.09.05-0 Planejamento Ambiental e Comportamento Humano', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4592, '095', 'a', 'humano', '7.07.09.05-0 Planejamento Ambiental e Comportamento Humano', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4593, '095', 'a', '7.07.10.00-7', '7.07.10.00-7 Tratamento e Prevenção Psicológica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4594, '095', 'a', 'tratamento', '7.07.10.00-7 Tratamento e Prevenção Psicológica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4595, '095', 'a', 'prevencao', '7.07.10.00-7 Tratamento e Prevenção Psicológica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4596, '095', 'a', 'psicologica', '7.07.10.00-7 Tratamento e Prevenção Psicológica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4597, '095', 'a', '7.07.10.01-5', '7.07.10.01-5 Intervenção Terapêutica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4598, '095', 'a', 'intervencao', '7.07.10.01-5 Intervenção Terapêutica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4599, '095', 'a', 'terapeutica', '7.07.10.01-5 Intervenção Terapêutica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4600, '095', 'a', '7.07.10.02-3', '7.07.10.02-3 Programas de Atendimento Comunitário', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4601, '095', 'a', 'programas', '7.07.10.02-3 Programas de Atendimento Comunitário', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4602, '095', 'a', 'de', '7.07.10.02-3 Programas de Atendimento Comunitário', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4603, '095', 'a', 'atendimento', '7.07.10.02-3 Programas de Atendimento Comunitário', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4604, '095', 'a', 'comunitario', '7.07.10.02-3 Programas de Atendimento Comunitário', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4605, '095', 'a', '7.07.10.03-1', '7.07.10.03-1 Treinamento e Reabilitação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4606, '095', 'a', 'treinamento', '7.07.10.03-1 Treinamento e Reabilitação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4607, '095', 'a', 'reabilitacao', '7.07.10.03-1 Treinamento e Reabilitação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4608, '095', 'a', '7.07.10.04-0', '7.07.10.04-0 Desvios da Conduta', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4609, '095', 'a', 'desvios', '7.07.10.04-0 Desvios da Conduta', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4610, '095', 'a', 'da', '7.07.10.04-0 Desvios da Conduta', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4611, '095', 'a', 'conduta', '7.07.10.04-0 Desvios da Conduta', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4612, '095', 'a', '7.07.10.05-8', '7.07.10.05-8 Distúrbios da Linguagem', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4613, '095', 'a', 'disturbios', '7.07.10.05-8 Distúrbios da Linguagem', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4614, '095', 'a', 'da', '7.07.10.05-8 Distúrbios da Linguagem', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4615, '095', 'a', 'linguagem', '7.07.10.05-8 Distúrbios da Linguagem', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4616, '095', 'a', '7.07.10.06-6', '7.07.10.06-6 Distúrbios Psicossomáticos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4617, '095', 'a', 'disturbios', '7.07.10.06-6 Distúrbios Psicossomáticos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4618, '095', 'a', 'psicossomaticos', '7.07.10.06-6 Distúrbios Psicossomáticos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4619, '095', 'a', '7.08.00.00-6', '7.08.00.00-6 Educação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4620, '095', 'a', 'educacao', '7.08.00.00-6 Educação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4621, '095', 'a', '7.08.01.00-2', '7.08.01.00-2 Fundamentos da Educação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4622, '095', 'a', 'fundamentos', '7.08.01.00-2 Fundamentos da Educação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4623, '095', 'a', 'da', '7.08.01.00-2 Fundamentos da Educação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4624, '095', 'a', 'educacao', '7.08.01.00-2 Fundamentos da Educação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4625, '095', 'a', '7.08.01.01-0', '7.08.01.01-0 Filosofia da Educação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4626, '095', 'a', 'filosofia', '7.08.01.01-0 Filosofia da Educação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4627, '095', 'a', 'da', '7.08.01.01-0 Filosofia da Educação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4628, '095', 'a', 'educacao', '7.08.01.01-0 Filosofia da Educação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4629, '095', 'a', '7.08.01.02-9', '7.08.01.02-9 História da Educação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4630, '095', 'a', 'historia', '7.08.01.02-9 História da Educação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4631, '095', 'a', 'da', '7.08.01.02-9 História da Educação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4632, '095', 'a', 'educacao', '7.08.01.02-9 História da Educação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4633, '095', 'a', '7.08.01.03-7', '7.08.01.03-7 Sociologia da Educação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4634, '095', 'a', 'sociologia', '7.08.01.03-7 Sociologia da Educação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4635, '095', 'a', 'da', '7.08.01.03-7 Sociologia da Educação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4636, '095', 'a', 'educacao', '7.08.01.03-7 Sociologia da Educação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4637, '095', 'a', '7.08.01.04-5', '7.08.01.04-5 Antropologia Educacional', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4638, '095', 'a', 'antropologia', '7.08.01.04-5 Antropologia Educacional', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4639, '095', 'a', 'educacional', '7.08.01.04-5 Antropologia Educacional', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4640, '095', 'a', '7.08.01.05-3', '7.08.01.05-3 Economia da Educação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4641, '095', 'a', 'economia', '7.08.01.05-3 Economia da Educação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4642, '095', 'a', 'da', '7.08.01.05-3 Economia da Educação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4643, '095', 'a', 'educacao', '7.08.01.05-3 Economia da Educação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4644, '095', 'a', '7.08.01.06-1', '7.08.01.06-1 Psicologia Educacional', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4645, '095', 'a', 'psicologia', '7.08.01.06-1 Psicologia Educacional', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4646, '095', 'a', 'educacional', '7.08.01.06-1 Psicologia Educacional', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4647, '095', 'a', '7.08.02.00-9', '7.08.02.00-9 Administração Educacional', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4648, '095', 'a', 'administracao', '7.08.02.00-9 Administração Educacional', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4649, '095', 'a', 'educacional', '7.08.02.00-9 Administração Educacional', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4650, '095', 'a', '7.08.02.01-7', '7.08.02.01-7 Administração de Sistemas Educacionais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4651, '095', 'a', 'administracao', '7.08.02.01-7 Administração de Sistemas Educacionais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4652, '095', 'a', 'de', '7.08.02.01-7 Administração de Sistemas Educacionais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4653, '095', 'a', 'sistemas', '7.08.02.01-7 Administração de Sistemas Educacionais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4654, '095', 'a', 'educacionais', '7.08.02.01-7 Administração de Sistemas Educacionais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4655, '095', 'a', '7.08.02.02-5', '7.08.02.02-5 Administração de Unidades Educativas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4656, '095', 'a', 'administracao', '7.08.02.02-5 Administração de Unidades Educativas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4657, '095', 'a', 'de', '7.08.02.02-5 Administração de Unidades Educativas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4658, '095', 'a', 'unidades', '7.08.02.02-5 Administração de Unidades Educativas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4659, '095', 'a', 'educativas', '7.08.02.02-5 Administração de Unidades Educativas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4660, '095', 'a', '7.08.03.00-5', '7.08.03.00-5 Planejamento e Avaliação Educacional', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4661, '095', 'a', 'planejamento', '7.08.03.00-5 Planejamento e Avaliação Educacional', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4662, '095', 'a', 'avaliacao', '7.08.03.00-5 Planejamento e Avaliação Educacional', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4663, '095', 'a', 'educacional', '7.08.03.00-5 Planejamento e Avaliação Educacional', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4664, '095', 'a', '7.08.03.01-3', '7.08.03.01-3 Política Educacional', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4665, '095', 'a', 'politica', '7.08.03.01-3 Política Educacional', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4666, '095', 'a', 'educacional', '7.08.03.01-3 Política Educacional', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4667, '095', 'a', '7.08.03.02-1', '7.08.03.02-1 Planejamento Educacional', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4668, '095', 'a', 'planejamento', '7.08.03.02-1 Planejamento Educacional', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4669, '095', 'a', 'educacional', '7.08.03.02-1 Planejamento Educacional', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4670, '095', 'a', '7.08.03.03-0', '7.08.03.03-0 Avaliação de Sistemas, Instituições, Planos e Programas Educacionais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4671, '095', 'a', 'avaliacao', '7.08.03.03-0 Avaliação de Sistemas, Instituições, Planos e Programas Educacionais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4672, '095', 'a', 'de', '7.08.03.03-0 Avaliação de Sistemas, Instituições, Planos e Programas Educacionais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4673, '095', 'a', 'sistemas,', '7.08.03.03-0 Avaliação de Sistemas, Instituições, Planos e Programas Educacionais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4674, '095', 'a', 'instituicoes,', '7.08.03.03-0 Avaliação de Sistemas, Instituições, Planos e Programas Educacionais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4675, '095', 'a', 'planos', '7.08.03.03-0 Avaliação de Sistemas, Instituições, Planos e Programas Educacionais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4676, '095', 'a', 'programas', '7.08.03.03-0 Avaliação de Sistemas, Instituições, Planos e Programas Educacionais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4677, '095', 'a', 'educacionais', '7.08.03.03-0 Avaliação de Sistemas, Instituições, Planos e Programas Educacionais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4678, '095', 'a', '7.08.04.00-1', '7.08.04.00-1 Ensino-Aprendizagem', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4679, '095', 'a', 'ensino-aprendizagem', '7.08.04.00-1 Ensino-Aprendizagem', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4680, '095', 'a', '7.08.04.01-0', '7.08.04.01-0 Teorias da Instrução', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4681, '095', 'a', 'teorias', '7.08.04.01-0 Teorias da Instrução', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4682, '095', 'a', 'da', '7.08.04.01-0 Teorias da Instrução', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4683, '095', 'a', 'instrucao', '7.08.04.01-0 Teorias da Instrução', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4684, '095', 'a', '7.08.04.02-8', '7.08.04.02-8 Métodos e Técnicas de Ensino', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4685, '095', 'a', 'metodos', '7.08.04.02-8 Métodos e Técnicas de Ensino', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4686, '095', 'a', 'tecnicas', '7.08.04.02-8 Métodos e Técnicas de Ensino', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4687, '095', 'a', 'de', '7.08.04.02-8 Métodos e Técnicas de Ensino', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4688, '095', 'a', 'ensino', '7.08.04.02-8 Métodos e Técnicas de Ensino', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4689, '095', 'a', '7.08.04.03-6', '7.08.04.03-6 Tecnologia Educacional', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4690, '095', 'a', 'tecnologia', '7.08.04.03-6 Tecnologia Educacional', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4691, '095', 'a', 'educacional', '7.08.04.03-6 Tecnologia Educacional', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4692, '095', 'a', '7.08.04.04-4', '7.08.04.04-4 Avaliação da Aprendizagem', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4693, '095', 'a', 'avaliacao', '7.08.04.04-4 Avaliação da Aprendizagem', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4694, '095', 'a', 'da', '7.08.04.04-4 Avaliação da Aprendizagem', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4695, '095', 'a', 'aprendizagem', '7.08.04.04-4 Avaliação da Aprendizagem', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4696, '095', 'a', '7.08.05.00-8', '7.08.05.00-8 Currículo', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4697, '095', 'a', 'curriculo', '7.08.05.00-8 Currículo', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4698, '095', 'a', '7.08.05.01-6', '7.08.05.01-6 Teoria Geral de Planejamento e Desenvolvimento Curricular', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4699, '095', 'a', 'teoria', '7.08.05.01-6 Teoria Geral de Planejamento e Desenvolvimento Curricular', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4700, '095', 'a', 'geral', '7.08.05.01-6 Teoria Geral de Planejamento e Desenvolvimento Curricular', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4701, '095', 'a', 'de', '7.08.05.01-6 Teoria Geral de Planejamento e Desenvolvimento Curricular', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4702, '095', 'a', 'planejamento', '7.08.05.01-6 Teoria Geral de Planejamento e Desenvolvimento Curricular', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4703, '095', 'a', 'desenvolvimento', '7.08.05.01-6 Teoria Geral de Planejamento e Desenvolvimento Curricular', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4704, '095', 'a', 'curricular', '7.08.05.01-6 Teoria Geral de Planejamento e Desenvolvimento Curricular', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4705, '095', 'a', '7.08.05.02-4', '7.08.05.02-4 Currículos Específicos para Níveis e Tipos de Educação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4706, '095', 'a', 'curriculos', '7.08.05.02-4 Currículos Específicos para Níveis e Tipos de Educação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4707, '095', 'a', 'especificos', '7.08.05.02-4 Currículos Específicos para Níveis e Tipos de Educação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4708, '095', 'a', 'para', '7.08.05.02-4 Currículos Específicos para Níveis e Tipos de Educação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4709, '095', 'a', 'niveis', '7.08.05.02-4 Currículos Específicos para Níveis e Tipos de Educação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4710, '095', 'a', 'tipos', '7.08.05.02-4 Currículos Específicos para Níveis e Tipos de Educação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4711, '095', 'a', 'de', '7.08.05.02-4 Currículos Específicos para Níveis e Tipos de Educação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4712, '095', 'a', 'educacao', '7.08.05.02-4 Currículos Específicos para Níveis e Tipos de Educação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4713, '095', 'a', '7.08.06.00-4', '7.08.06.00-4 Orientação e Aconselhamento', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4714, '095', 'a', 'orientacao', '7.08.06.00-4 Orientação e Aconselhamento', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4715, '095', 'a', 'aconselhamento', '7.08.06.00-4 Orientação e Aconselhamento', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4716, '095', 'a', '7.08.06.01-2', '7.08.06.01-2 Orientação Educacional', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4717, '095', 'a', 'orientacao', '7.08.06.01-2 Orientação Educacional', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4718, '095', 'a', 'educacional', '7.08.06.01-2 Orientação Educacional', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4719, '095', 'a', '7.08.06.02-0', '7.08.06.02-0 Orientação Vocacional', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4720, '095', 'a', 'orientacao', '7.08.06.02-0 Orientação Vocacional', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4721, '095', 'a', 'vocacional', '7.08.06.02-0 Orientação Vocacional', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4722, '095', 'a', '7.08.07.00-0', '7.08.07.00-0 Tópicos Específicos de Educação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4723, '095', 'a', 'topicos', '7.08.07.00-0 Tópicos Específicos de Educação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4724, '095', 'a', 'especificos', '7.08.07.00-0 Tópicos Específicos de Educação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4725, '095', 'a', 'de', '7.08.07.00-0 Tópicos Específicos de Educação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4726, '095', 'a', 'educacao', '7.08.07.00-0 Tópicos Específicos de Educação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4727, '095', 'a', '7.08.07.01-9', '7.08.07.01-9 Educação de Adultos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4728, '095', 'a', 'educacao', '7.08.07.01-9 Educação de Adultos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4729, '095', 'a', 'de', '7.08.07.01-9 Educação de Adultos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4730, '095', 'a', 'adultos', '7.08.07.01-9 Educação de Adultos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4731, '095', 'a', '7.08.07.02-7', '7.08.07.02-7 Educação Permanente', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4732, '095', 'a', 'educacao', '7.08.07.02-7 Educação Permanente', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4733, '095', 'a', 'permanente', '7.08.07.02-7 Educação Permanente', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4734, '095', 'a', '7.08.07.03-5', '7.08.07.03-5 Educação Rural', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4735, '095', 'a', 'educacao', '7.08.07.03-5 Educação Rural', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4736, '095', 'a', 'rural', '7.08.07.03-5 Educação Rural', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4737, '095', 'a', '7.08.07.04-3', '7.08.07.04-3 Educação em Periferias Urbanas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4738, '095', 'a', 'educacao', '7.08.07.04-3 Educação em Periferias Urbanas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4739, '095', 'a', 'em', '7.08.07.04-3 Educação em Periferias Urbanas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4740, '095', 'a', 'periferias', '7.08.07.04-3 Educação em Periferias Urbanas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4741, '095', 'a', 'urbanas', '7.08.07.04-3 Educação em Periferias Urbanas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4742, '095', 'a', '7.08.07.05-1', '7.08.07.05-1 Educação Especial', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4743, '095', 'a', 'educacao', '7.08.07.05-1 Educação Especial', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4744, '095', 'a', 'especial', '7.08.07.05-1 Educação Especial', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4745, '095', 'a', '7.08.07.06-0', '7.08.07.06-0 Educação Pré-Escolar', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4746, '095', 'a', 'educacao', '7.08.07.06-0 Educação Pré-Escolar', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4747, '095', 'a', 'pre-escolar', '7.08.07.06-0 Educação Pré-Escolar', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4748, '095', 'a', '7.08.07.07-8', '7.08.07.07-8 Ensino Profissionalizante', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4749, '095', 'a', 'ensino', '7.08.07.07-8 Ensino Profissionalizante', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4750, '095', 'a', 'profissionalizante', '7.08.07.07-8 Ensino Profissionalizante', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4751, '095', 'a', '7.09.00.00-0', '7.09.00.00-0 Ciência Política', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4752, '095', 'a', 'ciencia', '7.09.00.00-0 Ciência Política', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4753, '095', 'a', 'politica', '7.09.00.00-0 Ciência Política', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4754, '095', 'a', '7.09.01.00-7', '7.09.01.00-7 Teoria Política', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4755, '095', 'a', 'teoria', '7.09.01.00-7 Teoria Política', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4756, '095', 'a', 'politica', '7.09.01.00-7 Teoria Política', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4757, '095', 'a', '7.09.01.01-5', '7.09.01.01-5 Teoria Política Clássica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4758, '095', 'a', 'teoria', '7.09.01.01-5 Teoria Política Clássica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4759, '095', 'a', 'politica', '7.09.01.01-5 Teoria Política Clássica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4760, '095', 'a', 'classica', '7.09.01.01-5 Teoria Política Clássica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4761, '095', 'a', '7.09.01.02-3', '7.09.01.02-3 Teoria Política Medieval', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4762, '095', 'a', 'teoria', '7.09.01.02-3 Teoria Política Medieval', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4763, '095', 'a', 'politica', '7.09.01.02-3 Teoria Política Medieval', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4764, '095', 'a', 'medieval', '7.09.01.02-3 Teoria Política Medieval', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4765, '095', 'a', '7.09.01.03-1', '7.09.01.03-1 Teoria Política Moderna', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4766, '095', 'a', 'teoria', '7.09.01.03-1 Teoria Política Moderna', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4767, '095', 'a', 'politica', '7.09.01.03-1 Teoria Política Moderna', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4768, '095', 'a', 'moderna', '7.09.01.03-1 Teoria Política Moderna', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4769, '095', 'a', '7.09.01.04-0', '7.09.01.04-0 Teoria Política Contemporânea', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4770, '095', 'a', 'teoria', '7.09.01.04-0 Teoria Política Contemporânea', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4771, '095', 'a', 'politica', '7.09.01.04-0 Teoria Política Contemporânea', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4772, '095', 'a', 'contemporanea', '7.09.01.04-0 Teoria Política Contemporânea', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4773, '095', 'a', '7.09.02.00-3', '7.09.02.00-3 Estado e Governo', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4774, '095', 'a', 'estado', '7.09.02.00-3 Estado e Governo', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4775, '095', 'a', 'governo', '7.09.02.00-3 Estado e Governo', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4776, '095', 'a', '7.09.02.01-1', '7.09.02.01-1 Estrutura e Transformação do Estado', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4777, '095', 'a', 'estrutura', '7.09.02.01-1 Estrutura e Transformação do Estado', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4778, '095', 'a', 'transformacao', '7.09.02.01-1 Estrutura e Transformação do Estado', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4779, '095', 'a', 'do', '7.09.02.01-1 Estrutura e Transformação do Estado', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4780, '095', 'a', 'estado', '7.09.02.01-1 Estrutura e Transformação do Estado', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4781, '095', 'a', '7.09.02.02-0', '7.09.02.02-0 Sistemas Governamentais Comparados', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4782, '095', 'a', 'sistemas', '7.09.02.02-0 Sistemas Governamentais Comparados', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4783, '095', 'a', 'governamentais', '7.09.02.02-0 Sistemas Governamentais Comparados', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4784, '095', 'a', 'comparados', '7.09.02.02-0 Sistemas Governamentais Comparados', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4785, '095', 'a', '7.09.02.03-8', '7.09.02.03-8 Relações Intergovernamentais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4786, '095', 'a', 'relacoes', '7.09.02.03-8 Relações Intergovernamentais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4787, '095', 'a', 'intergovernamentais', '7.09.02.03-8 Relações Intergovernamentais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4788, '095', 'a', '7.09.02.04-6', '7.09.02.04-6 Estudos do Poder Local', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4789, '095', 'a', 'estudos', '7.09.02.04-6 Estudos do Poder Local', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4790, '095', 'a', 'do', '7.09.02.04-6 Estudos do Poder Local', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4791, '095', 'a', 'poder', '7.09.02.04-6 Estudos do Poder Local', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4792, '095', 'a', 'local', '7.09.02.04-6 Estudos do Poder Local', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4793, '095', 'a', '7.09.02.05-4', '7.09.02.05-4 Instituições Governamentais Específicas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4794, '095', 'a', 'instituicoes', '7.09.02.05-4 Instituições Governamentais Específicas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4795, '095', 'a', 'governamentais', '7.09.02.05-4 Instituições Governamentais Específicas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4796, '095', 'a', 'especificas', '7.09.02.05-4 Instituições Governamentais Específicas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4797, '095', 'a', '7.09.03.00-0', '7.09.03.00-0 Comportamento Político', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4798, '095', 'a', 'comportamento', '7.09.03.00-0 Comportamento Político', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4799, '095', 'a', 'politico', '7.09.03.00-0 Comportamento Político', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4800, '095', 'a', '7.09.03.01-8', '7.09.03.01-8 Estudos Eleitorais e Partidos Políticos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4801, '095', 'a', 'estudos', '7.09.03.01-8 Estudos Eleitorais e Partidos Políticos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4802, '095', 'a', 'eleitorais', '7.09.03.01-8 Estudos Eleitorais e Partidos Políticos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4803, '095', 'a', 'partidos', '7.09.03.01-8 Estudos Eleitorais e Partidos Políticos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4804, '095', 'a', 'politicos', '7.09.03.01-8 Estudos Eleitorais e Partidos Políticos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4805, '095', 'a', '7.09.03.02-6', '7.09.03.02-6 Atitude e Ideologias Políticas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4806, '095', 'a', 'atitude', '7.09.03.02-6 Atitude e Ideologias Políticas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4807, '095', 'a', 'ideologias', '7.09.03.02-6 Atitude e Ideologias Políticas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4808, '095', 'a', 'politicas', '7.09.03.02-6 Atitude e Ideologias Políticas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4809, '095', 'a', '7.09.03.03-4', '7.09.03.03-4 Conflitos e Coalizões Políticas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4810, '095', 'a', 'conflitos', '7.09.03.03-4 Conflitos e Coalizões Políticas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4811, '095', 'a', 'coalizoes', '7.09.03.03-4 Conflitos e Coalizões Políticas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4812, '095', 'a', 'politicas', '7.09.03.03-4 Conflitos e Coalizões Políticas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4813, '095', 'a', '7.09.03.04-2', '7.09.03.04-2 Comportamento Legislativo', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4814, '095', 'a', 'comportamento', '7.09.03.04-2 Comportamento Legislativo', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4815, '095', 'a', 'legislativo', '7.09.03.04-2 Comportamento Legislativo', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4816, '095', 'a', '7.09.03.05-0', '7.09.03.05-0 Classes Sociais e Grupos de Interesse', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4817, '095', 'a', 'classes', '7.09.03.05-0 Classes Sociais e Grupos de Interesse', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4818, '095', 'a', 'sociais', '7.09.03.05-0 Classes Sociais e Grupos de Interesse', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4819, '095', 'a', 'grupos', '7.09.03.05-0 Classes Sociais e Grupos de Interesse', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4820, '095', 'a', 'de', '7.09.03.05-0 Classes Sociais e Grupos de Interesse', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4821, '095', 'a', 'interesse', '7.09.03.05-0 Classes Sociais e Grupos de Interesse', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4822, '095', 'a', '7.09.04.00-6', '7.09.04.00-6 Políticas Públicas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4823, '095', 'a', 'politicas', '7.09.04.00-6 Políticas Públicas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4824, '095', 'a', 'publicas', '7.09.04.00-6 Políticas Públicas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4825, '095', 'a', '7.09.04.01-4', '7.09.04.01-4 Análise do Processo Decisório', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4826, '095', 'a', 'analise', '7.09.04.01-4 Análise do Processo Decisório', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4827, '095', 'a', 'do', '7.09.04.01-4 Análise do Processo Decisório', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4828, '095', 'a', 'processo', '7.09.04.01-4 Análise do Processo Decisório', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4829, '095', 'a', 'decisorio', '7.09.04.01-4 Análise do Processo Decisório', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4830, '095', 'a', '7.09.04.02-2', '7.09.04.02-2 Análise Institucional', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4831, '095', 'a', 'analise', '7.09.04.02-2 Análise Institucional', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4832, '095', 'a', 'institucional', '7.09.04.02-2 Análise Institucional', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4833, '095', 'a', '7.09.04.03-0', '7.09.04.03-0 Técnicas de Antecipação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4834, '095', 'a', 'tecnicas', '7.09.04.03-0 Técnicas de Antecipação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4835, '095', 'a', 'de', '7.09.04.03-0 Técnicas de Antecipação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4836, '095', 'a', 'antecipacao', '7.09.04.03-0 Técnicas de Antecipação', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4837, '095', 'a', '7.09.05.00-2', '7.09.05.00-2 Política Internacional', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4838, '095', 'a', 'politica', '7.09.05.00-2 Política Internacional', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4839, '095', 'a', 'internacional', '7.09.05.00-2 Política Internacional', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4840, '095', 'a', '7.09.05.01-0', '7.09.05.01-0 Política Externa do Brasil', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4841, '095', 'a', 'politica', '7.09.05.01-0 Política Externa do Brasil', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4842, '095', 'a', 'externa', '7.09.05.01-0 Política Externa do Brasil', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4843, '095', 'a', 'do', '7.09.05.01-0 Política Externa do Brasil', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4844, '095', 'a', 'brasil', '7.09.05.01-0 Política Externa do Brasil', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4845, '095', 'a', '7.09.05.02-9', '7.09.05.02-9 Organizações Internacionais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4846, '095', 'a', 'organizacoes', '7.09.05.02-9 Organizações Internacionais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4847, '095', 'a', 'internacionais', '7.09.05.02-9 Organizações Internacionais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4848, '095', 'a', '7.09.05.03-7', '7.09.05.03-7 Integração Internacional, Conflito, Guerra e Paz', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4849, '095', 'a', 'integracao', '7.09.05.03-7 Integração Internacional, Conflito, Guerra e Paz', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4850, '095', 'a', 'internacional,', '7.09.05.03-7 Integração Internacional, Conflito, Guerra e Paz', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4851, '095', 'a', 'conflito,', '7.09.05.03-7 Integração Internacional, Conflito, Guerra e Paz', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4852, '095', 'a', 'guerra', '7.09.05.03-7 Integração Internacional, Conflito, Guerra e Paz', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4853, '095', 'a', 'paz', '7.09.05.03-7 Integração Internacional, Conflito, Guerra e Paz', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4854, '095', 'a', '7.09.05.04-5', '7.09.05.04-5 Relações Internacionais, Bilaterais e Multilaterais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4855, '095', 'a', 'relacoes', '7.09.05.04-5 Relações Internacionais, Bilaterais e Multilaterais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4856, '095', 'a', 'internacionais,', '7.09.05.04-5 Relações Internacionais, Bilaterais e Multilaterais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4857, '095', 'a', 'bilaterais', '7.09.05.04-5 Relações Internacionais, Bilaterais e Multilaterais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4858, '095', 'a', 'multilaterais', '7.09.05.04-5 Relações Internacionais, Bilaterais e Multilaterais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4859, '095', 'a', '7.10.00.00-3', '7.10.00.00-3 Teologia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4860, '095', 'a', 'teologia', '7.10.00.00-3 Teologia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4861, '095', 'a', '7.10.01.00-0', '7.10.01.00-0 História da Teologia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4862, '095', 'a', 'historia', '7.10.01.00-0 História da Teologia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4863, '095', 'a', 'da', '7.10.01.00-0 História da Teologia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4864, '095', 'a', 'teologia', '7.10.01.00-0 História da Teologia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4865, '095', 'a', '7.10.02.00-6', '7.10.02.00-6 Teologia Moral', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4866, '095', 'a', 'teologia', '7.10.02.00-6 Teologia Moral', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4867, '095', 'a', 'moral', '7.10.02.00-6 Teologia Moral', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4868, '095', 'a', '7.10.03.00-2', '7.10.03.00-2 Teologia Sistemática', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4869, '095', 'a', 'teologia', '7.10.03.00-2 Teologia Sistemática', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4870, '095', 'a', 'sistematica', '7.10.03.00-2 Teologia Sistemática', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4871, '095', 'a', '7.10.04.00-9', '7.10.04.00-9 Teologia Pastoral', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4872, '095', 'a', 'teologia', '7.10.04.00-9 Teologia Pastoral', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4873, '095', 'a', 'pastoral', '7.10.04.00-9 Teologia Pastoral', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4874, '095', 'a', '8.00.00.00-2', '8.00.00.00-2 Lingüística, Letras e Artes', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4875, '095', 'a', 'linguistica,', '8.00.00.00-2 Lingüística, Letras e Artes', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4876, '095', 'a', 'letras', '8.00.00.00-2 Lingüística, Letras e Artes', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4877, '095', 'a', 'artes', '8.00.00.00-2 Lingüística, Letras e Artes', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4878, '095', 'a', '8.01.00.00-7', '8.01.00.00-7 Lingüística', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4879, '095', 'a', 'linguistica', '8.01.00.00-7 Lingüística', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4880, '095', 'a', '8.01.01.00-3', '8.01.01.00-3 Teoria e Análise Lingüística', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4881, '095', 'a', 'teoria', '8.01.01.00-3 Teoria e Análise Lingüística', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4882, '095', 'a', 'analise', '8.01.01.00-3 Teoria e Análise Lingüística', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4883, '095', 'a', 'linguistica', '8.01.01.00-3 Teoria e Análise Lingüística', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4884, '095', 'a', '8.01.02.00-0', '8.01.02.00-0 Fisiologia da Linguagem', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4885, '095', 'a', 'fisiologia', '8.01.02.00-0 Fisiologia da Linguagem', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4886, '095', 'a', 'da', '8.01.02.00-0 Fisiologia da Linguagem', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4887, '095', 'a', 'linguagem', '8.01.02.00-0 Fisiologia da Linguagem', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4888, '095', 'a', '8.01.03.00-6', '8.01.03.00-6 Lingüística Histórica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4889, '095', 'a', 'linguistica', '8.01.03.00-6 Lingüística Histórica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4890, '095', 'a', 'historica', '8.01.03.00-6 Lingüística Histórica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4891, '095', 'a', '8.01.04.00-2', '8.01.04.00-2 Sociolingüística e Dialetologia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4892, '095', 'a', 'sociolinguistica', '8.01.04.00-2 Sociolingüística e Dialetologia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4893, '095', 'a', 'dialetologia', '8.01.04.00-2 Sociolingüística e Dialetologia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4894, '095', 'a', '8.01.05.00-9', '8.01.05.00-9 Psicolingüística', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4895, '095', 'a', 'psicolinguistica', '8.01.05.00-9 Psicolingüística', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4896, '095', 'a', '8.01.06.00-5', '8.01.06.00-5 Lingüística Aplicada', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4897, '095', 'a', 'linguistica', '8.01.06.00-5 Lingüística Aplicada', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4898, '095', 'a', 'aplicada', '8.01.06.00-5 Lingüística Aplicada', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4899, '095', 'a', '8.02.00.00-1', '8.02.00.00-1 Letras', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4900, '095', 'a', 'letras', '8.02.00.00-1 Letras', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4901, '095', 'a', '8.02.01.00-8', '8.02.01.00-8 Língua Portuguesa', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4902, '095', 'a', 'lingua', '8.02.01.00-8 Língua Portuguesa', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4903, '095', 'a', 'portuguesa', '8.02.01.00-8 Língua Portuguesa', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4904, '095', 'a', '8.02.02.00-4', '8.02.02.00-4 Línguas Estrangeiras Modernas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4905, '095', 'a', 'linguas', '8.02.02.00-4 Línguas Estrangeiras Modernas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4906, '095', 'a', 'estrangeiras', '8.02.02.00-4 Línguas Estrangeiras Modernas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4907, '095', 'a', 'modernas', '8.02.02.00-4 Línguas Estrangeiras Modernas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4908, '095', 'a', '8.02.03.00-0', '8.02.03.00-0 Línguas Clássicas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4909, '095', 'a', 'linguas', '8.02.03.00-0 Línguas Clássicas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4910, '095', 'a', 'classicas', '8.02.03.00-0 Línguas Clássicas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4911, '095', 'a', '8.02.04.00-7', '8.02.04.00-7 Línguas Indígenas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4912, '095', 'a', 'linguas', '8.02.04.00-7 Línguas Indígenas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4913, '095', 'a', 'indigenas', '8.02.04.00-7 Línguas Indígenas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4914, '095', 'a', '8.02.05.00-3', '8.02.05.00-3 Teoria Literária', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4915, '095', 'a', 'teoria', '8.02.05.00-3 Teoria Literária', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4916, '095', 'a', 'literaria', '8.02.05.00-3 Teoria Literária', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4917, '095', 'a', '8.02.06.00-0', '8.02.06.00-0 Literatura Brasileira', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4918, '095', 'a', 'literatura', '8.02.06.00-0 Literatura Brasileira', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4919, '095', 'a', 'brasileira', '8.02.06.00-0 Literatura Brasileira', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4920, '095', 'a', '8.02.07.00-6', '8.02.07.00-6 Outras Literaturas Vernáculas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4921, '095', 'a', 'outras', '8.02.07.00-6 Outras Literaturas Vernáculas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4922, '095', 'a', 'literaturas', '8.02.07.00-6 Outras Literaturas Vernáculas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4923, '095', 'a', 'vernaculas', '8.02.07.00-6 Outras Literaturas Vernáculas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4924, '095', 'a', '8.02.08.00-2', '8.02.08.00-2 Literaturas Estrangeiras Modernas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4925, '095', 'a', 'literaturas', '8.02.08.00-2 Literaturas Estrangeiras Modernas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4926, '095', 'a', 'estrangeiras', '8.02.08.00-2 Literaturas Estrangeiras Modernas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4927, '095', 'a', 'modernas', '8.02.08.00-2 Literaturas Estrangeiras Modernas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4928, '095', 'a', '8.02.09.00-9', '8.02.09.00-9 Literaturas Clássicas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4929, '095', 'a', 'literaturas', '8.02.09.00-9 Literaturas Clássicas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4930, '095', 'a', 'classicas', '8.02.09.00-9 Literaturas Clássicas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4931, '095', 'a', '8.02.10.00-7', '8.02.10.00-7 Literatura Comparada', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4932, '095', 'a', 'literatura', '8.02.10.00-7 Literatura Comparada', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4933, '095', 'a', 'comparada', '8.02.10.00-7 Literatura Comparada', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4934, '095', 'a', '8.03.00.00-6', '8.03.00.00-6 Artes', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4935, '095', 'a', 'artes', '8.03.00.00-6 Artes', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4936, '095', 'a', '8.03.01.00-2', '8.03.01.00-2 Fundamentos e Crítica das Artes', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4937, '095', 'a', 'fundamentos', '8.03.01.00-2 Fundamentos e Crítica das Artes', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4938, '095', 'a', 'critica', '8.03.01.00-2 Fundamentos e Crítica das Artes', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4939, '095', 'a', 'das', '8.03.01.00-2 Fundamentos e Crítica das Artes', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4940, '095', 'a', 'artes', '8.03.01.00-2 Fundamentos e Crítica das Artes', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4941, '095', 'a', '8.03.01.01-0', '8.03.01.01-0 Teoria da Arte', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4942, '095', 'a', 'teoria', '8.03.01.01-0 Teoria da Arte', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4943, '095', 'a', 'da', '8.03.01.01-0 Teoria da Arte', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4944, '095', 'a', 'arte', '8.03.01.01-0 Teoria da Arte', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4945, '095', 'a', '8.03.01.02-9', '8.03.01.02-9 História da Arte', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4946, '095', 'a', 'historia', '8.03.01.02-9 História da Arte', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4947, '095', 'a', 'da', '8.03.01.02-9 História da Arte', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4948, '095', 'a', 'arte', '8.03.01.02-9 História da Arte', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4949, '095', 'a', '8.03.01.03-7', '8.03.01.03-7 Crítica da Arte', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4950, '095', 'a', 'critica', '8.03.01.03-7 Crítica da Arte', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4951, '095', 'a', 'da', '8.03.01.03-7 Crítica da Arte', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4952, '095', 'a', 'arte', '8.03.01.03-7 Crítica da Arte', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4953, '095', 'a', '8.03.02.00-9', '8.03.02.00-9 Artes Plásticas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4954, '095', 'a', 'artes', '8.03.02.00-9 Artes Plásticas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4955, '095', 'a', 'plasticas', '8.03.02.00-9 Artes Plásticas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4956, '095', 'a', '8.03.02.01-7', '8.03.02.01-7 Pintura', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4957, '095', 'a', 'pintura', '8.03.02.01-7 Pintura', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4958, '095', 'a', '8.03.02.02-5', '8.03.02.02-5 Desenho', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4959, '095', 'a', 'desenho', '8.03.02.02-5 Desenho', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4960, '095', 'a', '8.03.02.03-3', '8.03.02.03-3 Gravura', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4961, '095', 'a', 'gravura', '8.03.02.03-3 Gravura', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4962, '095', 'a', '8.03.02.04-1', '8.03.02.04-1 Escultura', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4963, '095', 'a', 'escultura', '8.03.02.04-1 Escultura', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4964, '095', 'a', '8.03.02.05-0', '8.03.02.05-0 Cerâmica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4965, '095', 'a', 'ceramica', '8.03.02.05-0 Cerâmica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4966, '095', 'a', '8.03.02.06-8', '8.03.02.06-8 Tecelagem', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4967, '095', 'a', 'tecelagem', '8.03.02.06-8 Tecelagem', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4968, '095', 'a', '8.03.03.00-5', '8.03.03.00-5 Música', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4970, '095', 'a', '8.03.03.01-3', '8.03.03.01-3 Regência', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4971, '095', 'a', 'regencia', '8.03.03.01-3 Regência', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4972, '095', 'a', '8.03.03.02-1', '8.03.03.02-1 Instrumentação Musical', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4973, '095', 'a', 'instrumentacao', '8.03.03.02-1 Instrumentação Musical', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4974, '095', 'a', 'musical', '8.03.03.02-1 Instrumentação Musical', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4975, '095', 'a', '8.03.03.03-0', '8.03.03.03-0 Composição Musical', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4976, '095', 'a', 'composicao', '8.03.03.03-0 Composição Musical', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4977, '095', 'a', 'musical', '8.03.03.03-0 Composição Musical', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4978, '095', 'a', '8.03.03.04-8', '8.03.03.04-8 Canto', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4979, '095', 'a', 'canto', '8.03.03.04-8 Canto', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4980, '095', 'a', '8.03.04.00-1', '8.03.04.00-1 Dança', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4981, '095', 'a', 'danca', '8.03.04.00-1 Dança', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4982, '095', 'a', '8.03.04.01-0', '8.03.04.01-0 Execução da Dança', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4983, '095', 'a', 'execucao', '8.03.04.01-0 Execução da Dança', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4984, '095', 'a', 'da', '8.03.04.01-0 Execução da Dança', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4985, '095', 'a', 'danca', '8.03.04.01-0 Execução da Dança', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4986, '095', 'a', '8.03.04.02-8', '8.03.04.02-8 Coreografia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4987, '095', 'a', 'coreografia', '8.03.04.02-8 Coreografia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4988, '095', 'a', '8.03.05.00-8', '8.03.05.00-8 Teatro', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4989, '095', 'a', 'teatro', '8.03.05.00-8 Teatro', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4990, '095', 'a', '8.03.05.01-6', '8.03.05.01-6 Dramaturgia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4991, '095', 'a', 'dramaturgia', '8.03.05.01-6 Dramaturgia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4992, '095', 'a', '8.03.05.02-4', '8.03.05.02-4 Direção Teatral', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4993, '095', 'a', 'direcao', '8.03.05.02-4 Direção Teatral', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4994, '095', 'a', 'teatral', '8.03.05.02-4 Direção Teatral', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4995, '095', 'a', '8.03.05.03-2', '8.03.05.03-2 Cenografia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4996, '095', 'a', 'cenografia', '8.03.05.03-2 Cenografia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4997, '095', 'a', '8.03.05.04-0', '8.03.05.04-0 Interpretação Teatral', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4998, '095', 'a', 'interpretacao', '8.03.05.04-0 Interpretação Teatral', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (4999, '095', 'a', 'teatral', '8.03.05.04-0 Interpretação Teatral', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (5000, '095', 'a', '8.03.06.00-4', '8.03.06.00-4 Ópera', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (5001, '095', 'a', 'opera', '8.03.06.00-4 Ópera', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (5002, '095', 'a', '8.03.07.00-0', '8.03.07.00-0 Fotografia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (5003, '095', 'a', 'fotografia', '8.03.07.00-0 Fotografia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (5004, '095', 'a', '8.03.08.00-7', '8.03.08.00-7 Cinema', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (5005, '095', 'a', 'cinema', '8.03.08.00-7 Cinema', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (5006, '095', 'a', '8.03.08.01-5', '8.03.08.01-5 Administração e Produção de Filmes', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (5007, '095', 'a', 'administracao', '8.03.08.01-5 Administração e Produção de Filmes', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (5008, '095', 'a', 'producao', '8.03.08.01-5 Administração e Produção de Filmes', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (5009, '095', 'a', 'de', '8.03.08.01-5 Administração e Produção de Filmes', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (5010, '095', 'a', 'filmes', '8.03.08.01-5 Administração e Produção de Filmes', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (5011, '095', 'a', '8.03.08.02-3', '8.03.08.02-3 Roteiro e Direção Cinematográficos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (5012, '095', 'a', 'roteiro', '8.03.08.02-3 Roteiro e Direção Cinematográficos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (5013, '095', 'a', 'direcao', '8.03.08.02-3 Roteiro e Direção Cinematográficos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (5014, '095', 'a', 'cinematograficos', '8.03.08.02-3 Roteiro e Direção Cinematográficos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (5015, '095', 'a', '8.03.08.03-1', '8.03.08.03-1 Técnicas de Registro e Processamento de Filmes', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (5016, '095', 'a', 'tecnicas', '8.03.08.03-1 Técnicas de Registro e Processamento de Filmes', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (5017, '095', 'a', 'de', '8.03.08.03-1 Técnicas de Registro e Processamento de Filmes', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (5018, '095', 'a', 'registro', '8.03.08.03-1 Técnicas de Registro e Processamento de Filmes', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (5019, '095', 'a', 'processamento', '8.03.08.03-1 Técnicas de Registro e Processamento de Filmes', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (5020, '095', 'a', 'de', '8.03.08.03-1 Técnicas de Registro e Processamento de Filmes', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (5021, '095', 'a', 'filmes', '8.03.08.03-1 Técnicas de Registro e Processamento de Filmes', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (5022, '095', 'a', '8.03.08.04-0', '8.03.08.04-0 Interpretação Cinematográfica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (5023, '095', 'a', 'interpretacao', '8.03.08.04-0 Interpretação Cinematográfica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (5024, '095', 'a', 'cinematografica', '8.03.08.04-0 Interpretação Cinematográfica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (5025, '095', 'a', '8.03.09.00-3', '8.03.09.00-3 Artes do Vídeo', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (5026, '095', 'a', 'artes', '8.03.09.00-3 Artes do Vídeo', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (5027, '095', 'a', 'do', '8.03.09.00-3 Artes do Vídeo', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (5028, '095', 'a', 'video', '8.03.09.00-3 Artes do Vídeo', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (5029, '095', 'a', '8.03.10.00-1', '8.03.10.00-1 Educação Artística', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (5030, '095', 'a', 'educacao', '8.03.10.00-1 Educação Artística', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (5031, '095', 'a', 'artistica', '8.03.10.00-1 Educação Artística', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (5032, '095', 'a', '9.00.00.00-5', '9.00.00.00-5 Outros', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (5033, '095', 'a', 'outros', '9.00.00.00-5 Outros', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (5034, '095', 'a', '9.01.00.00-0', '9.01.00.00-0 Administração Hospitalar', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (5035, '095', 'a', 'administracao', '9.01.00.00-0 Administração Hospitalar', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (5036, '095', 'a', 'hospitalar', '9.01.00.00-0 Administração Hospitalar', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (5037, '095', 'a', '9.02.00.00-4', '9.02.00.00-4 Administração Rural', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (5038, '095', 'a', 'administracao', '9.02.00.00-4 Administração Rural', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (5039, '095', 'a', 'rural', '9.02.00.00-4 Administração Rural', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (5040, '095', 'a', '9.03.00.00-9', '9.03.00.00-9 Carreira Militar', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (5041, '095', 'a', 'carreira', '9.03.00.00-9 Carreira Militar', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (5042, '095', 'a', 'militar', '9.03.00.00-9 Carreira Militar', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (5043, '095', 'a', '9.04.00.00-3', '9.04.00.00-3 Carreira Religiosa', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (5044, '095', 'a', 'carreira', '9.04.00.00-3 Carreira Religiosa', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (5045, '095', 'a', 'religiosa', '9.04.00.00-3 Carreira Religiosa', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (5046, '095', 'a', '9.05.00.00-8', '9.05.00.00-8 Ciências', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (5047, '095', 'a', 'ciencias', '9.05.00.00-8 Ciências', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (5048, '095', 'a', '9.06.00.00-2', '9.06.00.00-2 Biomedicina', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (5049, '095', 'a', 'biomedicina', '9.06.00.00-2 Biomedicina', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (5050, '095', 'a', '9.07.00.00-7', '9.07.00.00-7 Ciências Atuariais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (5051, '095', 'a', 'ciencias', '9.07.00.00-7 Ciências Atuariais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (5052, '095', 'a', 'atuariais', '9.07.00.00-7 Ciências Atuariais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (5053, '095', 'a', '9.08.00.00-1', '9.08.00.00-1 Ciências Sociais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (5054, '095', 'a', 'ciencias', '9.08.00.00-1 Ciências Sociais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (5055, '095', 'a', 'sociais', '9.08.00.00-1 Ciências Sociais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (5056, '095', 'a', '9.09.00.00-6', '9.09.00.00-6 Decoração', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (5057, '095', 'a', 'decoracao', '9.09.00.00-6 Decoração', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (5058, '095', 'a', '9.10.00.00-9', '9.10.00.00-9 Desenho de Moda', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (5059, '095', 'a', 'desenho', '9.10.00.00-9 Desenho de Moda', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (5060, '095', 'a', 'de', '9.10.00.00-9 Desenho de Moda', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (5061, '095', 'a', 'moda', '9.10.00.00-9 Desenho de Moda', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (5062, '095', 'a', '9.11.00.00-3', '9.11.00.00-3 Desenho de Projetos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (5063, '095', 'a', 'desenho', '9.11.00.00-3 Desenho de Projetos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (5064, '095', 'a', 'de', '9.11.00.00-3 Desenho de Projetos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (5065, '095', 'a', 'projetos', '9.11.00.00-3 Desenho de Projetos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (5066, '095', 'a', '9.12.00.00-8', '9.12.00.00-8 Diplomacia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (5067, '095', 'a', 'diplomacia', '9.12.00.00-8 Diplomacia', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (5068, '095', 'a', '9.13.00.00-2', '9.13.00.00-2 Engenharia de Agrimensura', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (5069, '095', 'a', 'engenharia', '9.13.00.00-2 Engenharia de Agrimensura', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (5070, '095', 'a', 'de', '9.13.00.00-2 Engenharia de Agrimensura', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (5071, '095', 'a', 'agrimensura', '9.13.00.00-2 Engenharia de Agrimensura', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (5072, '095', 'a', '9.14.00.00-7', '9.14.00.00-7 Engenharia Cartográfica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (5073, '095', 'a', 'engenharia', '9.14.00.00-7 Engenharia Cartográfica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (5074, '095', 'a', 'cartografica', '9.14.00.00-7 Engenharia Cartográfica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (5075, '095', 'a', '9.15.00.00-1', '9.15.00.00-1 Engenharia de Armamentos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (5076, '095', 'a', 'engenharia', '9.15.00.00-1 Engenharia de Armamentos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (5077, '095', 'a', 'de', '9.15.00.00-1 Engenharia de Armamentos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (5078, '095', 'a', 'armamentos', '9.15.00.00-1 Engenharia de Armamentos', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (5079, '095', 'a', '9.16.00.00-6', '9.16.00.00-6 Engenharia Mecatrônica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (5080, '095', 'a', 'engenharia', '9.16.00.00-6 Engenharia Mecatrônica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (5081, '095', 'a', 'mecatronica', '9.16.00.00-6 Engenharia Mecatrônica', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (5082, '095', 'a', '9.17.00.00-0', '9.17.00.00-0 Engenharia Têxtil', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (5083, '095', 'a', 'engenharia', '9.17.00.00-0 Engenharia Têxtil', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (5084, '095', 'a', 'textil', '9.17.00.00-0 Engenharia Têxtil', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (5085, '095', 'a', '9.18.00.00-5', '9.18.00.00-5 Estudos Sociais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (5086, '095', 'a', 'estudos', '9.18.00.00-5 Estudos Sociais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (5087, '095', 'a', 'sociais', '9.18.00.00-5 Estudos Sociais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (5088, '095', 'a', '9.19.00.00-0', '9.19.00.00-0 História Natural', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (5089, '095', 'a', 'historia', '9.19.00.00-0 História Natural', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (5090, '095', 'a', 'natural', '9.19.00.00-0 História Natural', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (5091, '095', 'a', '9.20.00.00-2', '9.20.00.00-2 Química Industrial', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (5092, '095', 'a', 'quimica', '9.20.00.00-2 Química Industrial', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (5093, '095', 'a', 'industrial', '9.20.00.00-2 Química Industrial', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (5094, '095', 'a', '9.21.00.00-7', '9.21.00.00-7 Relações Internacionais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (5095, '095', 'a', 'relacoes', '9.21.00.00-7 Relações Internacionais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (5096, '095', 'a', 'internacionais', '9.21.00.00-7 Relações Internacionais', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (5097, '095', 'a', '9.22.00.00-1', '9.22.00.00-1 Relações Publicas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (5098, '095', 'a', 'relacoes', '9.22.00.00-1 Relações Publicas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (5099, '095', 'a', 'publicas', '9.22.00.00-1 Relações Publicas', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (5100, '095', 'a', '9.23.00.00-6', '9.23.00.00-6 Secretariado Executivo', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (5101, '095', 'a', 'secretariado', '9.23.00.00-6 Secretariado Executivo', NULL);
INSERT INTO bib4template.biblio_idx_autocomplete VALUES (5102, '095', 'a', 'executivo', '9.23.00.00-6 Secretariado Executivo', NULL);


--
-- Data for Name: biblio_idx_fields; Type: TABLE DATA; Schema: bib4template; Owner: biblivre
--



--
-- Data for Name: biblio_idx_sort; Type: TABLE DATA; Schema: bib4template; Owner: biblivre
--

INSERT INTO bib4template.biblio_idx_sort VALUES (0, 1, '', 0);


--
-- Data for Name: biblio_indexing_groups; Type: TABLE DATA; Schema: bib4template; Owner: biblivre
--

INSERT INTO bib4template.biblio_indexing_groups VALUES (4, 'subject', '650_a_x_y_z,600_a_x_y_z,610_a_x_y_z,611_a_x_y_z,630_a_x_y_z,651_a_x_y_z,699_a_x_y_z', false, false, '2013-04-13 13:45:00.977717', NULL, '2013-04-13 13:45:00.977717', NULL);
INSERT INTO bib4template.biblio_indexing_groups VALUES (5, 'isbn', '020_a', false, false, '2013-04-13 13:45:00.977717', NULL, '2013-04-13 13:45:00.977717', NULL);
INSERT INTO bib4template.biblio_indexing_groups VALUES (6, 'issn', '022_a', false, false, '2013-04-13 13:45:00.977717', NULL, '2013-04-13 13:45:00.977717', NULL);
INSERT INTO bib4template.biblio_indexing_groups VALUES (1, 'author', '100_a,110_a,111_a,700_a,710_a,711_a', true, false, '2013-04-13 13:45:00.977717', NULL, '2013-04-13 13:45:00.977717', NULL);
INSERT INTO bib4template.biblio_indexing_groups VALUES (0, 'all', NULL, false, false, '2013-04-13 13:45:00.977717', NULL, '2013-04-13 13:45:00.977717', NULL);
INSERT INTO bib4template.biblio_indexing_groups VALUES (2, 'year', '260_c', true, false, '2013-04-13 13:45:00.977717', NULL, '2013-04-13 13:45:00.977717', NULL);
INSERT INTO bib4template.biblio_indexing_groups VALUES (7, 'publisher', '260_b', true, false, '2022-12-04 11:05:57.138539', NULL, '2022-12-04 11:05:57.138539', NULL);
INSERT INTO bib4template.biblio_indexing_groups VALUES (8, 'series', '490_a', true, false, '2022-12-04 11:05:57.138539', NULL, '2022-12-04 11:05:57.138539', NULL);
INSERT INTO bib4template.biblio_indexing_groups VALUES (3, 'title', '245_a_b,243_a_f,240_a,730_a,740_a_n_p,830_a_v,250_a,130_a', true, true, '2013-04-13 13:45:00.977717', NULL, '2013-04-13 13:45:00.977717', NULL);


--
-- Data for Name: biblio_records; Type: TABLE DATA; Schema: bib4template; Owner: biblivre
--



--
-- Data for Name: biblio_search_results; Type: TABLE DATA; Schema: bib4template; Owner: biblivre
--



--
-- Data for Name: biblio_searches; Type: TABLE DATA; Schema: bib4template; Owner: biblivre
--



--
-- Data for Name: configurations; Type: TABLE DATA; Schema: bib4template; Owner: biblivre
--

INSERT INTO bib4template.configurations VALUES ('setup.new_library', 'false', 'boolean', false, '2022-12-04 11:06:02.505131', 0);
INSERT INTO bib4template.configurations VALUES ('general.title', 'bib4template', 'string', false, '2023-07-22 23:36:47.010686', 0);
INSERT INTO bib4template.configurations VALUES ('general.subtitle', 'bib4template', 'string', false, '2023-07-22 23:36:47.013619', 0);


--
-- Data for Name: digital_media; Type: TABLE DATA; Schema: bib4template; Owner: biblivre
--



--
-- Data for Name: holding_creation_counter; Type: TABLE DATA; Schema: bib4template; Owner: biblivre
--



--
-- Data for Name: holding_form_datafields; Type: TABLE DATA; Schema: bib4template; Owner: biblivre
--

INSERT INTO bib4template.holding_form_datafields VALUES ('949', false, false, '', '', 'holdings', '2014-02-08 15:07:07.222864', NULL, '2014-02-08 15:07:07.222864', NULL, 949);
INSERT INTO bib4template.holding_form_datafields VALUES ('541', false, true, '_,0,1', '', 'holdings', '2014-02-08 15:07:07.222864', NULL, '2014-02-08 15:07:07.222864', NULL, 541);
INSERT INTO bib4template.holding_form_datafields VALUES ('090', false, false, '', '', 'holdings', '2014-02-08 15:07:07.222864', NULL, '2014-02-08 15:07:07.222864', NULL, 90);
INSERT INTO bib4template.holding_form_datafields VALUES ('852', false, true, '_,0,1,2,4,5,6,8', '0,1,2', 'holdings', '2014-02-08 15:07:07.222864', NULL, '2014-02-08 15:07:07.222864', NULL, 852);
INSERT INTO bib4template.holding_form_datafields VALUES ('856', false, true, '', '', 'holdings', '2014-02-08 15:07:07.222864', NULL, '2014-02-08 15:07:07.222864', NULL, 856);


--
-- Data for Name: holding_form_subfields; Type: TABLE DATA; Schema: bib4template; Owner: biblivre
--

INSERT INTO bib4template.holding_form_subfields VALUES ('949', 'a', false, false, '2014-02-08 15:10:19.847594', NULL, '2014-02-08 15:10:19.847594', NULL, 'disabled', 1046);
INSERT INTO bib4template.holding_form_subfields VALUES ('541', 'a', false, false, '2014-02-08 15:10:19.847594', NULL, '2014-02-08 15:10:19.847594', NULL, 'disabled', 638);
INSERT INTO bib4template.holding_form_subfields VALUES ('541', 'b', false, false, '2014-02-08 15:10:19.847594', NULL, '2014-02-08 15:10:19.847594', NULL, 'disabled', 639);
INSERT INTO bib4template.holding_form_subfields VALUES ('541', 'c', false, false, '2014-02-08 15:10:19.847594', NULL, '2014-02-08 15:10:19.847594', NULL, 'disabled', 640);
INSERT INTO bib4template.holding_form_subfields VALUES ('541', 'd', false, false, '2014-02-08 15:10:19.847594', NULL, '2014-02-08 15:10:19.847594', NULL, 'disabled', 641);
INSERT INTO bib4template.holding_form_subfields VALUES ('541', 'e', false, true, '2014-02-08 15:10:19.847594', NULL, '2014-02-08 15:10:19.847594', NULL, 'disabled', 642);
INSERT INTO bib4template.holding_form_subfields VALUES ('541', 'f', false, true, '2014-02-08 15:10:19.847594', NULL, '2014-02-08 15:10:19.847594', NULL, 'disabled', 643);
INSERT INTO bib4template.holding_form_subfields VALUES ('541', 'h', false, true, '2014-02-08 15:10:19.847594', NULL, '2014-02-08 15:10:19.847594', NULL, 'disabled', 645);
INSERT INTO bib4template.holding_form_subfields VALUES ('541', 'n', false, true, '2014-02-08 15:10:19.847594', NULL, '2014-02-08 15:10:19.847594', NULL, 'disabled', 651);
INSERT INTO bib4template.holding_form_subfields VALUES ('541', 'o', false, true, '2014-02-08 15:10:19.847594', NULL, '2014-02-08 15:10:19.847594', NULL, 'disabled', 652);
INSERT INTO bib4template.holding_form_subfields VALUES ('541', '3', false, true, '2014-02-08 15:10:19.847594', NULL, '2014-02-08 15:10:19.847594', NULL, 'disabled', 592);
INSERT INTO bib4template.holding_form_subfields VALUES ('090', 'a', false, false, '2014-02-08 15:10:19.847594', NULL, '2014-02-08 15:10:19.847594', NULL, 'disabled', 187);
INSERT INTO bib4template.holding_form_subfields VALUES ('090', 'b', false, false, '2014-02-08 15:10:19.847594', NULL, '2014-02-08 15:10:19.847594', NULL, 'disabled', 188);
INSERT INTO bib4template.holding_form_subfields VALUES ('090', 'c', false, false, '2014-02-08 15:10:19.847594', NULL, '2014-02-08 15:10:19.847594', NULL, 'disabled', 189);
INSERT INTO bib4template.holding_form_subfields VALUES ('090', 'd', false, false, '2014-02-08 15:10:19.847594', NULL, '2014-02-08 15:10:19.847594', NULL, 'disabled', 190);
INSERT INTO bib4template.holding_form_subfields VALUES ('852', 'a', false, false, '2014-02-08 15:10:19.847594', NULL, '2014-02-08 15:10:19.847594', NULL, 'disabled', 949);
INSERT INTO bib4template.holding_form_subfields VALUES ('852', 'b', false, true, '2014-02-08 15:10:19.847594', NULL, '2014-02-08 15:10:19.847594', NULL, 'disabled', 950);
INSERT INTO bib4template.holding_form_subfields VALUES ('852', 'c', false, true, '2014-02-08 15:10:19.847594', NULL, '2014-02-08 15:10:19.847594', NULL, 'disabled', 951);
INSERT INTO bib4template.holding_form_subfields VALUES ('852', 'e', false, true, '2014-02-08 15:10:19.847594', NULL, '2014-02-08 15:10:19.847594', NULL, 'disabled', 953);
INSERT INTO bib4template.holding_form_subfields VALUES ('852', 'j', false, false, '2014-02-08 15:10:19.847594', NULL, '2014-02-08 15:10:19.847594', NULL, 'disabled', 958);
INSERT INTO bib4template.holding_form_subfields VALUES ('852', 'n', false, true, '2014-02-08 15:10:19.847594', NULL, '2014-02-08 15:10:19.847594', NULL, 'disabled', 962);
INSERT INTO bib4template.holding_form_subfields VALUES ('852', 'q', false, false, '2014-02-08 15:10:19.847594', NULL, '2014-02-08 15:10:19.847594', NULL, 'disabled', 965);
INSERT INTO bib4template.holding_form_subfields VALUES ('852', 'x', false, true, '2014-02-08 15:10:19.847594', NULL, '2014-02-08 15:10:19.847594', NULL, 'disabled', 972);
INSERT INTO bib4template.holding_form_subfields VALUES ('852', 'z', false, true, '2014-02-08 15:10:19.847594', NULL, '2014-02-08 15:10:19.847594', NULL, 'disabled', 974);
INSERT INTO bib4template.holding_form_subfields VALUES ('856', 'd', false, true, '2014-02-08 15:10:19.847594', NULL, '2014-02-08 15:10:19.847594', NULL, 'disabled', 956);
INSERT INTO bib4template.holding_form_subfields VALUES ('856', 'f', false, true, '2014-02-08 15:10:19.847594', NULL, '2014-02-08 15:10:19.847594', NULL, 'disabled', 958);
INSERT INTO bib4template.holding_form_subfields VALUES ('856', 'u', false, true, '2014-02-08 15:10:19.847594', NULL, '2014-02-08 15:10:19.847594', NULL, 'disabled', 973);
INSERT INTO bib4template.holding_form_subfields VALUES ('856', 'y', false, true, '2014-02-08 15:10:19.847594', NULL, '2014-02-08 15:10:19.847594', NULL, 'disabled', 977);


--
-- Data for Name: lending_fines; Type: TABLE DATA; Schema: bib4template; Owner: biblivre
--



--
-- Data for Name: lendings; Type: TABLE DATA; Schema: bib4template; Owner: biblivre
--



--
-- Data for Name: logins; Type: TABLE DATA; Schema: bib4template; Owner: biblivre
--

INSERT INTO bib4template.logins VALUES (1, 'admin', true, 'C4wx3TpMHnSwdk1bUQ/V6qwAQmw=', '2014-05-18 15:46:31.632', 1, '2014-05-18 15:46:31.632', NULL, NULL, NULL);


--
-- Data for Name: orders; Type: TABLE DATA; Schema: bib4template; Owner: biblivre
--



--
-- Data for Name: permissions; Type: TABLE DATA; Schema: bib4template; Owner: biblivre
--



--
-- Data for Name: quotations; Type: TABLE DATA; Schema: bib4template; Owner: biblivre
--



--
-- Data for Name: request_quotation; Type: TABLE DATA; Schema: bib4template; Owner: biblivre
--



--
-- Data for Name: requests; Type: TABLE DATA; Schema: bib4template; Owner: biblivre
--



--
-- Data for Name: reservations; Type: TABLE DATA; Schema: bib4template; Owner: biblivre
--



--
-- Data for Name: suppliers; Type: TABLE DATA; Schema: bib4template; Owner: biblivre
--



--
-- Data for Name: translations; Type: TABLE DATA; Schema: bib4template; Owner: biblivre
--



--
-- Data for Name: users; Type: TABLE DATA; Schema: bib4template; Owner: biblivre
--



--
-- Data for Name: users_fields; Type: TABLE DATA; Schema: bib4template; Owner: biblivre
--

INSERT INTO bib4template.users_fields VALUES ('email', 'string', true, 0, 1, '2013-04-13 13:47:34.765388', NULL, '2013-04-13 13:47:34.765388', NULL);
INSERT INTO bib4template.users_fields VALUES ('gender', 'list', false, 2, 2, '2014-06-07 12:51:14.565458', NULL, '2014-06-07 12:51:14.565458', NULL);
INSERT INTO bib4template.users_fields VALUES ('phone_cel', 'string', false, 25, 3, '2014-06-07 12:47:50.811875', NULL, '2014-06-07 12:47:50.811875', NULL);
INSERT INTO bib4template.users_fields VALUES ('phone_home', 'string', false, 25, 4, '2014-06-07 12:47:33.283702', NULL, '2014-06-07 12:47:33.283702', NULL);
INSERT INTO bib4template.users_fields VALUES ('phone_work', 'string', false, 25, 5, '2014-06-07 12:47:42.779511', NULL, '2014-06-07 12:47:42.779511', NULL);
INSERT INTO bib4template.users_fields VALUES ('obs', 'text', false, 0, 1002, '2013-04-13 13:47:34.765388', NULL, '2013-04-13 13:47:34.765388', NULL);
INSERT INTO bib4template.users_fields VALUES ('id_cpf', 'string', false, 20, 8, '2014-06-07 12:46:47.409991', NULL, '2014-06-07 12:46:47.409991', NULL);
INSERT INTO bib4template.users_fields VALUES ('address', 'string', false, 500, 9, '2014-06-07 12:41:23.221671', NULL, '2014-06-07 12:41:23.221671', NULL);
INSERT INTO bib4template.users_fields VALUES ('address_number', 'string', false, 100, 10, '2014-06-07 12:42:30.610671', NULL, '2014-06-07 12:42:30.610671', NULL);
INSERT INTO bib4template.users_fields VALUES ('address_complement', 'string', false, 100, 11, '2014-06-07 12:44:27.624027', NULL, '2014-06-07 12:44:27.624027', NULL);
INSERT INTO bib4template.users_fields VALUES ('address_zip', 'string', false, 20, 12, '2014-06-07 12:45:05.425222', NULL, '2014-06-07 12:45:05.425222', NULL);
INSERT INTO bib4template.users_fields VALUES ('address_city', 'string', false, 100, 13, '2014-06-07 12:45:21.458004', NULL, '2014-06-07 12:45:21.458004', NULL);
INSERT INTO bib4template.users_fields VALUES ('address_state', 'string', false, 100, 14, '2014-06-07 12:45:31.657995', NULL, '2014-06-07 12:45:31.657995', NULL);
INSERT INTO bib4template.users_fields VALUES ('birthday', 'date', false, 0, 15, '2014-06-07 12:50:08.933974', NULL, '2014-06-07 12:50:08.933974', NULL);
INSERT INTO bib4template.users_fields VALUES ('id_rg', 'string', false, 20, 7, '2014-06-07 12:46:30.386262', NULL, '2014-06-07 12:46:30.386262', NULL);
INSERT INTO bib4template.users_fields VALUES ('phone_work_extension', 'string', false, 10, 6, '2014-06-07 12:53:42.743594', NULL, '2014-06-07 12:53:42.743594', NULL);


--
-- Data for Name: users_types; Type: TABLE DATA; Schema: bib4template; Owner: biblivre
--

INSERT INTO bib4template.users_types VALUES (1, 'Leitor', 'Leitores', 3, 3, 15, 10, 0, '2014-05-18 15:46:31.379', 1, '2014-05-18 15:46:31.379', NULL);
INSERT INTO bib4template.users_types VALUES (2, 'Funcionário', 'Funcionários', 99, 99, 365, 365, 0, '2014-05-18 15:46:31.379', 1, '2014-05-18 15:46:31.379', NULL);


--
-- Data for Name: users_values; Type: TABLE DATA; Schema: bib4template; Owner: biblivre
--



--
-- Data for Name: versions; Type: TABLE DATA; Schema: bib4template; Owner: biblivre
--

INSERT INTO bib4template.versions VALUES ('4.0.0b');
INSERT INTO bib4template.versions VALUES ('4.0.1b');
INSERT INTO bib4template.versions VALUES ('4.0.2b');
INSERT INTO bib4template.versions VALUES ('4.0.3b');
INSERT INTO bib4template.versions VALUES ('4.0.4b');
INSERT INTO bib4template.versions VALUES ('4.0.5b');
INSERT INTO bib4template.versions VALUES ('4.0.6b');
INSERT INTO bib4template.versions VALUES ('4.0.7b');
INSERT INTO bib4template.versions VALUES ('4.0.8b');
INSERT INTO bib4template.versions VALUES ('4.0.9b');
INSERT INTO bib4template.versions VALUES ('4.0.10b');
INSERT INTO bib4template.versions VALUES ('4.0.11b');
INSERT INTO bib4template.versions VALUES ('4.0.12b');
INSERT INTO bib4template.versions VALUES ('4.1.0');
INSERT INTO bib4template.versions VALUES ('4.1.1');
INSERT INTO bib4template.versions VALUES ('4.1.2');
INSERT INTO bib4template.versions VALUES ('4.1.3');
INSERT INTO bib4template.versions VALUES ('4.1.4');
INSERT INTO bib4template.versions VALUES ('4.1.5');
INSERT INTO bib4template.versions VALUES ('4.1.6');
INSERT INTO bib4template.versions VALUES ('4.1.7');
INSERT INTO bib4template.versions VALUES ('4.1.8');
INSERT INTO bib4template.versions VALUES ('4.1.9');
INSERT INTO bib4template.versions VALUES ('4.1.10');
INSERT INTO bib4template.versions VALUES ('4.1.10a');
INSERT INTO bib4template.versions VALUES ('4.1.11');
INSERT INTO bib4template.versions VALUES ('4.1.11a');
INSERT INTO bib4template.versions VALUES ('5.0.0');
INSERT INTO bib4template.versions VALUES ('5.0.1');
INSERT INTO bib4template.versions VALUES ('5.0.1b');
INSERT INTO bib4template.versions VALUES ('6.0.0-1.0.0-alpha');
INSERT INTO bib4template.versions VALUES ('6.0.0-1.0.1-alpha');
INSERT INTO bib4template.versions VALUES ('6.0.0-1.0.2-alpha');
INSERT INTO bib4template.versions VALUES ('v6_0_0$1_1_0$alpha');
INSERT INTO bib4template.versions VALUES ('biblivre.update.v6_0_0$1_0_0$alpha.Update');
INSERT INTO bib4template.versions VALUES ('biblivre.update.v6_0_0$1_0_1$alpha.Update');
INSERT INTO bib4template.versions VALUES ('biblivre.update.v6_0_0$1_0_2$alpha.Update');
INSERT INTO bib4template.versions VALUES ('biblivre.update.v6_0_0$1_1_0$alpha.Update');
INSERT INTO bib4template.versions VALUES ('biblivre.update.v6_0_0$2_0_0$alpha.Update');
INSERT INTO bib4template.versions VALUES ('biblivre.update.v6_0_0$3_0_0$alpha.Update');


--
-- Data for Name: vocabulary_brief_formats; Type: TABLE DATA; Schema: bib4template; Owner: biblivre
--

INSERT INTO bib4template.vocabulary_brief_formats VALUES ('040', '${a}_{- }${b}', 1, '2014-03-20 12:26:27.691', NULL, '2014-03-20 12:26:27.691', NULL);
INSERT INTO bib4template.vocabulary_brief_formats VALUES ('150', '${a}_{- }${i}_{; }${x}', 2, '2014-03-20 12:28:43.529', NULL, '2014-03-20 12:28:43.529', NULL);
INSERT INTO bib4template.vocabulary_brief_formats VALUES ('450', '${a}_{; }${x}', 4, '2014-03-20 12:29:58.601', NULL, '2014-03-20 12:29:58.601', NULL);
INSERT INTO bib4template.vocabulary_brief_formats VALUES ('550', '${a}_{; }${x}', 5, '2014-03-20 12:30:37.837', NULL, '2014-03-20 12:30:37.837', NULL);
INSERT INTO bib4template.vocabulary_brief_formats VALUES ('670', '${a}', 6, '2014-03-20 12:30:52.156', NULL, '2014-03-20 12:30:52.156', NULL);
INSERT INTO bib4template.vocabulary_brief_formats VALUES ('680', '${a}', 7, '2014-03-20 12:31:13.64', NULL, '2014-03-20 12:31:13.64', NULL);
INSERT INTO bib4template.vocabulary_brief_formats VALUES ('685', '${a}', 8, '2014-03-20 12:31:24.135', NULL, '2014-03-20 12:31:24.135', NULL);
INSERT INTO bib4template.vocabulary_brief_formats VALUES ('750', '${a}_{; }${x}_{; }${y}_{; }${z}', 9, '2014-03-20 12:32:37.881', NULL, '2014-03-20 12:32:37.881', NULL);
INSERT INTO bib4template.vocabulary_brief_formats VALUES ('913', '${a}', 10, '2014-03-20 12:32:57.598', NULL, '2014-03-20 12:32:57.598', NULL);
INSERT INTO bib4template.vocabulary_brief_formats VALUES ('360', '${a}_{; }${x}', 3, '2014-03-20 13:03:12.684', NULL, '2014-03-20 13:03:12.684', NULL);


--
-- Data for Name: vocabulary_form_datafields; Type: TABLE DATA; Schema: bib4template; Owner: biblivre
--

INSERT INTO bib4template.vocabulary_form_datafields VALUES ('040', false, false, '', '', '', '2014-02-08 15:29:41.844864', NULL, '2014-02-08 15:29:41.844864', NULL, 40);
INSERT INTO bib4template.vocabulary_form_datafields VALUES ('150', false, false, '', '', '', '2014-02-08 15:29:41.844864', NULL, '2014-02-08 15:29:41.844864', NULL, 150);
INSERT INTO bib4template.vocabulary_form_datafields VALUES ('450', false, true, '', '', '', '2014-02-08 15:29:41.844864', NULL, '2014-02-08 15:29:41.844864', NULL, 450);
INSERT INTO bib4template.vocabulary_form_datafields VALUES ('550', false, true, '', '', '', '2014-02-08 15:29:41.844864', NULL, '2014-02-08 15:29:41.844864', NULL, 550);
INSERT INTO bib4template.vocabulary_form_datafields VALUES ('360', false, true, '', '', '', '2014-02-08 15:29:41.844864', NULL, '2014-02-08 15:29:41.844864', NULL, 360);
INSERT INTO bib4template.vocabulary_form_datafields VALUES ('670', false, false, '', '', '', '2014-02-08 15:29:41.844864', NULL, '2014-02-08 15:29:41.844864', NULL, 670);
INSERT INTO bib4template.vocabulary_form_datafields VALUES ('680', false, true, '', '', '', '2014-02-08 15:29:41.844864', NULL, '2014-02-08 15:29:41.844864', NULL, 680);
INSERT INTO bib4template.vocabulary_form_datafields VALUES ('685', false, true, '', '', '', '2014-02-08 15:29:41.844864', NULL, '2014-02-08 15:29:41.844864', NULL, 685);
INSERT INTO bib4template.vocabulary_form_datafields VALUES ('750', false, false, '0,1,2', '0,4', '', '2014-02-08 15:29:41.844864', NULL, '2014-02-08 15:29:41.844864', NULL, 750);
INSERT INTO bib4template.vocabulary_form_datafields VALUES ('913', false, false, '', '', '', '2014-02-08 15:29:41.844864', NULL, '2014-02-08 15:29:41.844864', NULL, 913);


--
-- Data for Name: vocabulary_form_subfields; Type: TABLE DATA; Schema: bib4template; Owner: biblivre
--

INSERT INTO bib4template.vocabulary_form_subfields VALUES ('040', 'a', false, false, '2014-02-08 15:30:23.272565', NULL, '2014-02-08 15:30:23.272565', NULL, 'disabled', 137);
INSERT INTO bib4template.vocabulary_form_subfields VALUES ('040', 'b', false, false, '2014-02-08 15:30:23.272565', NULL, '2014-02-08 15:30:23.272565', NULL, 'disabled', 138);
INSERT INTO bib4template.vocabulary_form_subfields VALUES ('040', 'c', false, false, '2014-02-08 15:30:23.272565', NULL, '2014-02-08 15:30:23.272565', NULL, 'disabled', 139);
INSERT INTO bib4template.vocabulary_form_subfields VALUES ('040', 'd', false, true, '2014-02-08 15:30:23.272565', NULL, '2014-02-08 15:30:23.272565', NULL, 'disabled', 140);
INSERT INTO bib4template.vocabulary_form_subfields VALUES ('040', 'e', false, true, '2014-02-08 15:30:23.272565', NULL, '2014-02-08 15:30:23.272565', NULL, 'disabled', 141);
INSERT INTO bib4template.vocabulary_form_subfields VALUES ('150', 'a', false, false, '2014-02-08 15:30:23.272565', NULL, '2014-02-08 15:30:23.272565', NULL, 'disabled', 247);
INSERT INTO bib4template.vocabulary_form_subfields VALUES ('150', 'i', false, false, '2014-02-08 15:30:23.272565', NULL, '2014-02-08 15:30:23.272565', NULL, 'disabled', 255);
INSERT INTO bib4template.vocabulary_form_subfields VALUES ('150', 'x', false, true, '2014-02-08 15:30:23.272565', NULL, '2014-02-08 15:30:23.272565', NULL, 'disabled', 270);
INSERT INTO bib4template.vocabulary_form_subfields VALUES ('150', 'y', false, true, '2014-02-08 15:30:23.272565', NULL, '2014-02-08 15:30:23.272565', NULL, 'disabled', 271);
INSERT INTO bib4template.vocabulary_form_subfields VALUES ('150', 'z', false, true, '2014-02-08 15:30:23.272565', NULL, '2014-02-08 15:30:23.272565', NULL, 'disabled', 272);
INSERT INTO bib4template.vocabulary_form_subfields VALUES ('450', 'a', false, false, '2014-02-08 15:30:23.272565', NULL, '2014-02-08 15:30:23.272565', NULL, 'disabled', 547);
INSERT INTO bib4template.vocabulary_form_subfields VALUES ('550', 'a', false, false, '2014-02-08 15:30:23.272565', NULL, '2014-02-08 15:30:23.272565', NULL, 'disabled', 647);
INSERT INTO bib4template.vocabulary_form_subfields VALUES ('550', 'x', false, true, '2014-02-08 15:30:23.272565', NULL, '2014-02-08 15:30:23.272565', NULL, 'disabled', 670);
INSERT INTO bib4template.vocabulary_form_subfields VALUES ('550', 'y', false, true, '2014-02-08 15:30:23.272565', NULL, '2014-02-08 15:30:23.272565', NULL, 'disabled', 671);
INSERT INTO bib4template.vocabulary_form_subfields VALUES ('550', 'z', false, true, '2014-02-08 15:30:23.272565', NULL, '2014-02-08 15:30:23.272565', NULL, 'disabled', 672);
INSERT INTO bib4template.vocabulary_form_subfields VALUES ('360', 'a', false, false, '2014-02-08 15:30:23.272565', NULL, '2014-02-08 15:30:23.272565', NULL, 'disabled', 457);
INSERT INTO bib4template.vocabulary_form_subfields VALUES ('360', 'x', false, true, '2014-02-08 15:30:23.272565', NULL, '2014-02-08 15:30:23.272565', NULL, 'disabled', 480);
INSERT INTO bib4template.vocabulary_form_subfields VALUES ('360', 'y', false, true, '2014-02-08 15:30:23.272565', NULL, '2014-02-08 15:30:23.272565', NULL, 'disabled', 481);
INSERT INTO bib4template.vocabulary_form_subfields VALUES ('360', 'z', false, true, '2014-02-08 15:30:23.272565', NULL, '2014-02-08 15:30:23.272565', NULL, 'disabled', 482);
INSERT INTO bib4template.vocabulary_form_subfields VALUES ('670', 'a', false, false, '2014-02-08 15:30:23.272565', NULL, '2014-02-08 15:30:23.272565', NULL, 'disabled', 767);
INSERT INTO bib4template.vocabulary_form_subfields VALUES ('680', 'a', false, false, '2014-02-08 15:30:23.272565', NULL, '2014-02-08 15:30:23.272565', NULL, 'disabled', 777);
INSERT INTO bib4template.vocabulary_form_subfields VALUES ('685', 'i', false, false, '2014-02-08 15:30:23.272565', NULL, '2014-02-08 15:30:23.272565', NULL, 'disabled', 790);
INSERT INTO bib4template.vocabulary_form_subfields VALUES ('750', 'a', false, false, '2014-02-08 15:30:23.272565', NULL, '2014-02-08 15:30:23.272565', NULL, 'disabled', 847);
INSERT INTO bib4template.vocabulary_form_subfields VALUES ('750', 'x', false, true, '2014-02-08 15:30:23.272565', NULL, '2014-02-08 15:30:23.272565', NULL, 'disabled', 870);
INSERT INTO bib4template.vocabulary_form_subfields VALUES ('750', 'y', false, true, '2014-02-08 15:30:23.272565', NULL, '2014-02-08 15:30:23.272565', NULL, 'disabled', 871);
INSERT INTO bib4template.vocabulary_form_subfields VALUES ('750', 'z', false, true, '2014-02-08 15:30:23.272565', NULL, '2014-02-08 15:30:23.272565', NULL, 'disabled', 872);
INSERT INTO bib4template.vocabulary_form_subfields VALUES ('913', 'a', false, false, '2014-02-08 15:30:23.272565', NULL, '2014-02-08 15:30:23.272565', NULL, 'disabled', 1010);


--
-- Data for Name: vocabulary_idx_autocomplete; Type: TABLE DATA; Schema: bib4template; Owner: biblivre
--



--
-- Data for Name: vocabulary_idx_fields; Type: TABLE DATA; Schema: bib4template; Owner: biblivre
--



--
-- Data for Name: vocabulary_idx_sort; Type: TABLE DATA; Schema: bib4template; Owner: biblivre
--



--
-- Data for Name: vocabulary_indexing_groups; Type: TABLE DATA; Schema: bib4template; Owner: biblivre
--

INSERT INTO bib4template.vocabulary_indexing_groups VALUES (0, 'all', NULL, false, false, '2014-03-04 11:16:16.428', NULL, '2014-03-04 11:16:16.428', NULL);
INSERT INTO bib4template.vocabulary_indexing_groups VALUES (1, 'te_term', '150_a', true, true, '2014-03-04 11:16:31.24', NULL, '2014-03-04 11:16:31.24', NULL);
INSERT INTO bib4template.vocabulary_indexing_groups VALUES (2, 'up_term', '450_a', true, false, '2014-03-04 11:16:48.069', NULL, '2014-03-04 11:16:48.069', NULL);
INSERT INTO bib4template.vocabulary_indexing_groups VALUES (3, 'tg_term', '550_a', true, false, '2014-03-04 11:16:59.899', NULL, '2014-03-04 11:16:59.899', NULL);
INSERT INTO bib4template.vocabulary_indexing_groups VALUES (4, 'vt_ta_term', '360_a', true, false, '2014-03-04 11:17:18.848', NULL, '2014-03-04 11:17:18.848', NULL);


--
-- Data for Name: vocabulary_records; Type: TABLE DATA; Schema: bib4template; Owner: biblivre
--



--
-- Data for Name: vocabulary_search_results; Type: TABLE DATA; Schema: bib4template; Owner: biblivre
--



--
-- Data for Name: vocabulary_searches; Type: TABLE DATA; Schema: bib4template; Owner: biblivre
--



--
-- Data for Name: z3950_addresses; Type: TABLE DATA; Schema: bib4template; Owner: biblivre
--

INSERT INTO bib4template.z3950_addresses VALUES (1, 'Universidad de Chile - Santiago, Chile', 'unicornio.uchile.cl', 2200, 'default');
INSERT INTO bib4template.z3950_addresses VALUES (2, 'Université Laval - Quebec, Canadá', 'ariane2.ulaval.ca', 2200, 'default');
INSERT INTO bib4template.z3950_addresses VALUES (3, 'Brunel University - Londres, Reino Unido', 'library.brunel.ac.uk', 2200, 'default');
INSERT INTO bib4template.z3950_addresses VALUES (4, 'Acadia University - Nova Escócia, Canada', 'jasper.acadiau.ca', 2200, 'default');
INSERT INTO bib4template.z3950_addresses VALUES (5, 'Carnegie Mellon University - Pittsburgh, PA - EUA', 'webcat.library.cmu.edu', 2200, 'unicorn');
INSERT INTO bib4template.z3950_addresses VALUES (6, 'New York Public Library - EUA', 'catalog.nypl.org', 210, 'INNOPAC');
INSERT INTO bib4template.z3950_addresses VALUES (7, 'Biblioteca Nacional da Espanha - Madrid', 'sigb.bne.es', 2200, 'default');
INSERT INTO bib4template.z3950_addresses VALUES (8, 'Library of Congress Online Catalog - EUA', '140.147.249.67', 210, 'LCDB');
INSERT INTO bib4template.z3950_addresses VALUES (9, 'South University New Orleans, EUA', 'suno.louislibraries.org', 7705, 'default');
INSERT INTO bib4template.z3950_addresses VALUES (10, 'Penn State University, EUA', 'zcat.libraries.psu.edu', 2200, 'default');
INSERT INTO bib4template.z3950_addresses VALUES (11, 'The Fletcher School, Tufts University, EUA', 'fletcher.louislibraries.org', 8205, 'default');
INSERT INTO bib4template.z3950_addresses VALUES (12, 'Univerdidad de Madrid, Espanha', 'marte.biblioteca.upm.es', 2200, 'default');


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

