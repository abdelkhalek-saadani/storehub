CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE IF NOT EXISTS address  (
                         id uuid NOT NULL DEFAULT uuid_generate_v4(),
                         city varchar(255),
                         street varchar(255),
                         number integer,
                         PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS money  (
                       id uuid NOT NULL DEFAULT uuid_generate_v4(),
                       value numeric(19,4),
                       currency varchar(255),
                       PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS slot  (
                      id uuid NOT NULL DEFAULT uuid_generate_v4(),
                      date date,
                      start_time time without time zone,
                      end_time time without time zone,
                      PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS customer_order  (
                                id uuid NOT NULL DEFAULT uuid_generate_v4(),
                                delivery_address_id uuid,
                                invoice_address_id uuid,
                                original_subtotal_id uuid,
                                subtotal_id uuid,
                                total_id uuid,
                                delivery_fee_id uuid,
                                slot_id uuid,
                                date timestamp without time zone,
                                delivery_mode varchar(255),
                                payment_mode varchar(255),
                                slot_retain_id uuid,
                                inventory_retain_id uuid,
                                PRIMARY KEY (id),
                                CONSTRAINT customer_order_delivery_address_id_fkey FOREIGN KEY (delivery_address_id) REFERENCES address(id) ON DELETE CASCADE,
                                CONSTRAINT customer_order_invoice_address_id_fkey FOREIGN KEY (invoice_address_id) REFERENCES address(id) ON DELETE CASCADE,
                                CONSTRAINT customer_order_original_subtotal_id_fkey FOREIGN KEY (original_subtotal_id) REFERENCES money(id) ON DELETE CASCADE,
                                CONSTRAINT customer_order_subtotal_id_fkey FOREIGN KEY (subtotal_id) REFERENCES money(id) ON DELETE CASCADE,
                                CONSTRAINT customer_order_total_id_fkey FOREIGN KEY (total_id) REFERENCES money(id) ON DELETE CASCADE,
                                CONSTRAINT customer_order_delivery_fee_id_fkey FOREIGN KEY (delivery_fee_id) REFERENCES money(id) ON DELETE CASCADE,
                                CONSTRAINT customer_order_slot_id_fkey FOREIGN KEY (slot_id) REFERENCES slot(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS cart_item  (
                           id uuid NOT NULL DEFAULT uuid_generate_v4(),
                           product_id uuid,
                           quantity integer,
                           subtotal_id uuid,
                           unit_price_id uuid,
                           original_unit_price_id uuid,
                           order_id uuid,
                           PRIMARY KEY (id),
                           CONSTRAINT cart_item_order_id_fkey FOREIGN KEY (order_id) REFERENCES customer_order(id) ON DELETE CASCADE,
                           CONSTRAINT cart_item_subtotal_id_fkey FOREIGN KEY (subtotal_id) REFERENCES money(id) ON DELETE CASCADE,
                           CONSTRAINT cart_item_unit_price_id_fkey FOREIGN KEY (unit_price_id) REFERENCES money(id) ON DELETE CASCADE,
                           CONSTRAINT cart_item_original_unit_price_id_fkey FOREIGN KEY (original_unit_price_id) REFERENCES money(id) ON DELETE CASCADE
);
