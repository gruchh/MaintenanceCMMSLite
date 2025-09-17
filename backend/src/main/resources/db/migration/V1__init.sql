CREATE TABLE employee_roles (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    created_at TIMESTAMP(6) NOT NULL,
    updated_at TIMESTAMP(6)
);

CREATE TABLE user_roles (
    user_id BIGINT NOT NULL,
    role VARCHAR(255) CHECK (role IN ('ADMIN','TECHNICAN','SUBCONTRACTOR'))
);

CREATE TABLE machines (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(50) NOT NULL UNIQUE,
    full_name VARCHAR(200) NOT NULL,
    serial_number VARCHAR(255) UNIQUE,
    manufacturer VARCHAR(100),
    production_date DATE,
    description TEXT
);

CREATE TABLE spare_parts (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(150) NOT NULL UNIQUE,
    price NUMERIC(38,2) NOT NULL,
    producer VARCHAR(150)
);

CREATE TABLE employees (
    id BIGINT NOT NULL PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    avatar_url VARCHAR(255),
    employee_role_id BIGINT NOT NULL
);

CREATE TABLE employee_details (
    id BIGINT NOT NULL PRIMARY KEY,
    date_of_birth DATE NOT NULL,
    hire_date DATE NOT NULL,
    phone_number VARCHAR(255),
    contract_end_date DATE,
    salary NUMERIC(10,2),
    education_level VARCHAR(255) CHECK (education_level IN ('PRIMARY','SECONDARY','BACHELOR','MASTER','DOCTORATE')),
    field_of_study VARCHAR(255),
    emergency_contact_name VARCHAR(255),
    emergency_contact_phone VARCHAR(255),
    street VARCHAR(255),
    city VARCHAR(255),
    postal_code VARCHAR(255),
    country VARCHAR(255)
);

CREATE TABLE breakdowns (
    id BIGSERIAL PRIMARY KEY,
    description VARCHAR(1000) NOT NULL,
    reported_at TIMESTAMP(6) NOT NULL,
    started_at TIMESTAMP(6),
    finished_at TIMESTAMP(6),
    opened BOOLEAN NOT NULL,
    specialist_comment VARCHAR(2000),
    breakdown_type VARCHAR(255) NOT NULL CHECK (breakdown_type IN ('MECHANICAL','AUTOMATICAL','PARAMETERS')),
    machine_id BIGINT NOT NULL,
    total_cost NUMERIC(19,4)
);

CREATE TABLE breakdown_used_parts (
    id BIGSERIAL PRIMARY KEY,
    breakdown_id BIGINT NOT NULL,
    spare_part_id BIGINT NOT NULL,
    quantity INTEGER NOT NULL CHECK (quantity >= 1)
);

CREATE TABLE breakdowns_employees (
    breakdown_id BIGINT NOT NULL,
    employee_id BIGINT NOT NULL,
    PRIMARY KEY (breakdown_id, employee_id)
);

CREATE TABLE refresh_tokens (
    id BIGSERIAL PRIMARY KEY,
    token VARCHAR(255) NOT NULL UNIQUE,
    user_id BIGINT NOT NULL,
    expiry_date TIMESTAMP NOT NULL,
    CONSTRAINT fk_user_id FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE shift_schedules (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    created_at TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP(6)
);

CREATE TABLE shift_entries (
    id BIGSERIAL PRIMARY KEY,
    schedule_id BIGINT NOT NULL,
    work_date DATE NOT NULL,
    brigade_type CHAR(1) NOT NULL,
    shift_type VARCHAR(10) NOT NULL,
    CONSTRAINT uq_schedule_date_brigade UNIQUE (schedule_id, work_date, brigade_type),
    CONSTRAINT fk_shift_entries_schedule FOREIGN KEY (schedule_id) REFERENCES shift_schedules(id)
);

ALTER TABLE user_roles ADD CONSTRAINT FKhfh9dx7w3ubf1co1vdev94g3f FOREIGN KEY (user_id) REFERENCES users;
ALTER TABLE employees ADD CONSTRAINT FKd6th9xowehhf1kmmq1dsseq28 FOREIGN KEY (id) REFERENCES users;
ALTER TABLE employees ADD CONSTRAINT FKdah2rhynngctvnn264fv63e9i FOREIGN KEY (employee_role_id) REFERENCES employee_roles;
ALTER TABLE employee_details ADD CONSTRAINT FKtlax9cis723ibdj9a43vwq9e FOREIGN KEY (id) REFERENCES employees;
ALTER TABLE breakdowns ADD CONSTRAINT FKcmwlgwcblt9nwl37dk9twkt8o FOREIGN KEY (machine_id) REFERENCES machines;
ALTER TABLE breakdown_used_parts ADD CONSTRAINT FKcwytxxrofd4x5bquhegfae4bd FOREIGN KEY (breakdown_id) REFERENCES breakdowns;
ALTER TABLE breakdown_used_parts ADD CONSTRAINT FKp4gu68aofvs2rpo1rmq8gi5a1 FOREIGN KEY (spare_part_id) REFERENCES spare_parts;
ALTER TABLE breakdowns_employees ADD CONSTRAINT FK93bvc33bdwdfqd154hcqmyamr FOREIGN KEY (breakdown_id) REFERENCES breakdowns;
ALTER TABLE breakdowns_employees ADD CONSTRAINT FK9n09qdriftydkwwu5ri4yf0aw FOREIGN KEY (employee_id) REFERENCES employees;

INSERT INTO employee_roles (name) VALUES
('Automatyk'),
('Mechanik'),
('Ślusarz'),
('Spawacz'),
('Elektronik'),
('Kierownik'),
('Manager');

INSERT INTO machines (code, full_name, serial_number, manufacturer, production_date, description) VALUES
('TKR', 'Tokarka CNC T-500', 'SN-TKR-2015', 'DMG Mori', '2015-01-01', 'Precyzyjna tokarka do produkcji seryjnej.'),
('VOO', 'Frezarka 5-osiowa V-90', 'SN-VOO-2016', 'Haas', '2016-01-01', 'Centrum obróbcze do skomplikowanych detali.'),
('LCC', 'Maszyna do cięcia laserowego L-3030', 'SN-LCC-2017', 'Trumpf', '2017-01-01', 'Wysokowydajny laser do cięcia blach stalowych.'),
('RER', 'Robot spawalniczy Fanuc R-2000iC', 'SN-RER-2018', 'Fanuc', '2018-01-01', 'Zrobotyzowane stanowisko do spawania MIG/MAG.'),
('PPP', 'Piec hartowniczy P-1200', 'SN-PPP-2019', 'Seco/Warwick', '2019-01-01', 'Piec z kontrolowaną atmosferą do obróbki cieplnej.'),
('UEW', 'Zgrzewarka ultradźwiękowa U-50', 'SN-UEW-2020', 'Branson', '2020-01-01', 'Urządzenie do precyzyjnego łączenia tworzyw sztucznych.'),
('CSD', 'Maszyna inspekcyjna 3D Zeiss Contura', 'SN-CSD-2021', 'Zeiss', '2021-01-01', 'Współrzędnościowa maszyna pomiarowa o wysokiej dokładności.'),
('PKK', 'Prasa krawędziowa Amada HFE', 'SN-PKK-2022', 'Amada', '2022-01-01', 'Hydrauliczna prasa do gięcia blach o nacisku 150 ton.'),
('SWA', 'Szlifierka do wałków Jotes SWA-10', 'SN-SWA-2023', 'Jotes', '2023-01-01', 'Szlifierka do precyzyjnego wykańczania powierzchni zewnętrznych.'),
('WMN', 'Wiertarka wielowrzecionowa Wotan B-75', 'SN-WMN-2024', 'Wotan', '2024-01-01', 'Maszyna do wykonywania otworów w produkcji wielkoseryjnej.');

INSERT INTO spare_parts (name, price, producer) VALUES
('Bezpiecznik 2A typ 1', 27.00, 'Siemens'),
('Czujnik indukcyjny typ 2', 39.00, 'Festo'),
('Filtr oleju hydraulicznego typ 3', 51.00, 'Siemens'),
('Przekaźnik bezpieczeństwa typ 4', 63.00, 'Festo'),
('Stycznik mocy typ 5', 75.00, 'Siemens'),
('Zawór pneumatyczny typ 6', 87.00, 'Festo'),
('Wężyk chłodziwa typ 7', 99.00, 'Siemens'),
('Uszczelka siłownika typ 8', 111.00, 'Festo');