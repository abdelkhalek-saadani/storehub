-- This schema init script is used by the compose.e2e.yml compose file to spin up an order container.

-- Dumped from database version 16.15 (Ubuntu 16.15-0ubuntu0.24.04.1)
-- Dumped by pg_dump version 16.15 (Ubuntu 16.15-0ubuntu0.24.04.1)

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
-- Name: pgcrypto; Type: EXTENSION; Schema: -; Owner: -
--

CREATE EXTENSION IF NOT EXISTS pgcrypto WITH SCHEMA public;


--
-- Name: uuid-ossp; Type: EXTENSION; Schema: -; Owner: -
--

CREATE EXTENSION IF NOT EXISTS "uuid-ossp" WITH SCHEMA public;


SET default_table_access_method = heap;

--
-- Name: address; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.address (
                                id uuid DEFAULT public.uuid_generate_v4() NOT NULL,
                                city character varying(255),
                                street character varying(255),
                                number integer
);


--
-- Name: cart; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.cart (
                             id uuid DEFAULT gen_random_uuid() NOT NULL,
                             user_id uuid,
                             store_id uuid NOT NULL,
                             original_total numeric(19,2) DEFAULT 0 NOT NULL,
                             final_total numeric(19,2) DEFAULT 0 NOT NULL,
                             total_discount numeric(19,2) DEFAULT 0 NOT NULL,
                             created_at timestamp without time zone DEFAULT now() NOT NULL,
                             updated_at timestamp without time zone DEFAULT now() NOT NULL,
                             guest_id uuid,
                             CONSTRAINT "Cart has one owner, either a user or a guest" CHECK ((((guest_id IS NULL) AND (user_id IS NOT NULL)) OR ((guest_id IS NOT NULL) AND (user_id IS NULL))))
);


--
-- Name: cart_item; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.cart_item (
                                  id uuid DEFAULT gen_random_uuid() NOT NULL,
                                  product_id uuid NOT NULL,
                                  quantity integer NOT NULL,
                                  unit_price numeric(19,2) NOT NULL,
                                  original_line_total numeric(19,2) NOT NULL,
                                  discount_amount numeric(19,2) DEFAULT 0 NOT NULL,
                                  final_line_total numeric(19,2) NOT NULL,
                                  applied_offer_label character varying(255),
                                  cart_id uuid NOT NULL,
                                  created_at timestamp without time zone DEFAULT now() NOT NULL,
                                  product_name character varying,
                                    product_image_url character varying
);


--
-- Name: order_item; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.order_item (
                                   id uuid DEFAULT gen_random_uuid() NOT NULL,
                                   product_name text NOT NULL,
                                   product_image_url character varying,
                                   product_id uuid NOT NULL,
                                   quantity integer NOT NULL,
                                   unit_price numeric(12,2) NOT NULL,
                                   original_line_total numeric(12,2) NOT NULL,
                                   discount_amount numeric(12,2) DEFAULT 0 NOT NULL,
                                   final_line_total numeric(12,2) NOT NULL,
                                   applied_offer_label text,
                                   order_id uuid NOT NULL,
                                   created_at timestamp without time zone DEFAULT now()
);


--
-- Name: orders; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.orders (
                               id uuid DEFAULT gen_random_uuid() NOT NULL,
                               user_id uuid,
                               store_id uuid NOT NULL,
                               original_total numeric(12,2) NOT NULL,
                               final_total numeric(12,2) NOT NULL,
                               total_discount numeric(12,2) DEFAULT 0 NOT NULL,
                               created_at timestamp without time zone DEFAULT now() NOT NULL,
                               updated_at timestamp without time zone,
                               delivery_address jsonb,
                               billing_address text,
                               slot_id uuid,
                               delivery_fee numeric(12,2) DEFAULT 10,
                               slot_retain_id uuid,
                               inventory_retain_ids uuid[],
                               status character varying(50),
                               payment_id uuid,
                               payment_approval_link text,
                               idempotency_key uuid DEFAULT gen_random_uuid() NOT NULL,
                               payment_order_id character varying,
                               guest_id uuid,
                               email character varying,
                               CONSTRAINT "Order have either guestId or userId, not both" CHECK ((((user_id IS NOT NULL) AND (guest_id IS NULL)) OR ((user_id IS NULL) AND (guest_id IS NOT NULL))))
);


--
-- Name: slot; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.slot (
                             id uuid DEFAULT public.uuid_generate_v4() NOT NULL,
                             date date,
                             start_time time without time zone,
                             end_time time without time zone
);


--
-- Name: store_memberships; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.store_memberships (
                                          id uuid DEFAULT gen_random_uuid() NOT NULL,
                                          user_id uuid NOT NULL,
                                          store_id uuid NOT NULL,
                                          role character varying(50) NOT NULL,
                                          created_at timestamp without time zone DEFAULT now()
);


--
-- Name: stores; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.stores (
                               id uuid DEFAULT gen_random_uuid() NOT NULL,
                               name character varying(255) NOT NULL,
                               description text,
                               created_at timestamp without time zone DEFAULT now(),
                               address character varying(250),
                               slug character varying
);


--
-- Name: users; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.users (
                              id uuid DEFAULT gen_random_uuid() NOT NULL,
                              keycloak_id character varying(255) NOT NULL,
                              email character varying(255) NOT NULL,
                              first_name character varying(255),
                              last_name character varying(255),
                              address character varying(255),
                              phone_number character varying(50),
                              created_at timestamp without time zone DEFAULT now() NOT NULL,
                              preferred_store_id uuid
);


--
-- Name: address address_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.address
    ADD CONSTRAINT address_pkey PRIMARY KEY (id);


--
-- Name: cart_item cart_item_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.cart_item
    ADD CONSTRAINT cart_item_pkey PRIMARY KEY (id);


--
-- Name: cart cart_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.cart
    ADD CONSTRAINT cart_pkey PRIMARY KEY (id);


--
-- Name: order_item order_item_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.order_item
    ADD CONSTRAINT order_item_pkey PRIMARY KEY (id);


--
-- Name: orders orders_idempotency_key_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.orders
    ADD CONSTRAINT orders_idempotency_key_key UNIQUE (idempotency_key);


--
-- Name: orders orders_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.orders
    ADD CONSTRAINT orders_pkey PRIMARY KEY (id);


--
-- Name: slot slot_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.slot
    ADD CONSTRAINT slot_pkey PRIMARY KEY (id);


--
-- Name: store_memberships store_memberships_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.store_memberships
    ADD CONSTRAINT store_memberships_pkey PRIMARY KEY (id);


--
-- Name: store_memberships store_memberships_user_id_store_id_role_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.store_memberships
    ADD CONSTRAINT store_memberships_user_id_store_id_role_key UNIQUE (user_id, store_id, role);


--
-- Name: stores stores_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.stores
    ADD CONSTRAINT stores_pkey PRIMARY KEY (id);


--
-- Name: users users_email_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_email_key UNIQUE (email);


--
-- Name: users users_keycloak_id_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_keycloak_id_key UNIQUE (keycloak_id);


--
-- Name: users users_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_pkey PRIMARY KEY (id);


--
-- Name: idx_cart_item_cart_id; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_cart_item_cart_id ON public.cart_item USING btree (cart_id);


--
-- Name: idx_cart_item_product_id; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_cart_item_product_id ON public.cart_item USING btree (product_id);


--
-- Name: idx_cart_user_store; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_cart_user_store ON public.cart USING btree (user_id, store_id);


--
-- Name: idx_order_item_order_id; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_order_item_order_id ON public.order_item USING btree (order_id);


--
-- Name: cart_item fk_cart_item_cart; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.cart_item
    ADD CONSTRAINT fk_cart_item_cart FOREIGN KEY (cart_id) REFERENCES public.cart(id) ON DELETE CASCADE;


--
-- Name: order_item order_item_order_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.order_item
    ADD CONSTRAINT order_item_order_id_fkey FOREIGN KEY (order_id) REFERENCES public.orders(id) ON DELETE CASCADE;


--
-- Name: store_memberships store_memberships_store_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.store_memberships
    ADD CONSTRAINT store_memberships_store_id_fkey FOREIGN KEY (store_id) REFERENCES public.stores(id);


--
-- Name: store_memberships store_memberships_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.store_memberships
    ADD CONSTRAINT store_memberships_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.users(id);


--
-- PostgreSQL database dump complete
--
