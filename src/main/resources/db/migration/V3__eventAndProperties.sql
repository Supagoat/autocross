drop table session_data;

CREATE TABLE session_data (
    id varchar(64) PRIMARY KEY,
    data text,
    created TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
    updated TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now()
);

create table event (
    event_id varchar(38) PRIMARY KEY,
    club_id varchar(38),
    run_groups integer,
    num_corners integer
);


create table event_group_map(
	event_id varchar(38),
	car_group varchar(10),
	run_group integer
);

create table ability (
	ability_id varchar(38) PRIMARY KEY,
	ability_name varchar(24) unique
);

create table person_ability (
	person_id varchar(38),
	ability_id varchar(38),
	rank integer,
	unique(person_id, ability_id)
);

create table event_reg (
	event_id varchar(38),
	person_id varchar(38),
	car_group varchar(10),
	pax_group varchar(10),
	car_type varchar(50),
	car_number varchar(10)	
);

create table worker_assignment (
	event_id varchar(38),
	person_id varchar(38),
	run_group integer,
	job_type varchar(38),
	location integer
);

