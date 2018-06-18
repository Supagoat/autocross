create table event (
    event_id varchar(18) PRIMARY KEY,
    club_id varchar(18),
    run_groups integer,
    num_corners integer
);


create table event_group_map(
	event_id varchar(18),
	car_group varchar(10),
	run_group integer
);

create table ability (
	ability_id varchar(18) PRIMARY KEY,
	ability_name varchar(24) unique
);

create table person_ability (
	person_id varchar(18),
	ability_id varchar(18),
	unique(person_id, ability_id)
);

create table event_reg (
	event_id varchar(18),
	person_id varchar(18),
	car_group varchar(10),
	pax_group varchar(10),
	car_type varchar(50),
	car_number varchar(10)	
);

create table worker_assignment (
	event_id varchar(18),
	person_id varchar(18),
	run_group integer,
	job_type varchar(18),
	location integer
);


insert into ability(ability_id, ability_name) values
("7ba7040f-a346-4fd7-99da-a4fce8382112", "novice");

insert into ability(ability_id, ability_name) values
("e1d8e7c1-0f1e-4f02-9627-333f1c09cced","course work");
insert into ability(ability_id, ability_name) values
("373280ad-e12f-4a61-8351-39e0ed3f27e7","control");
insert into ability(ability_id, ability_name) values
("d5a0ecae-452f-47f1-8f69-fc044c0e2332","computer");
insert into ability(ability_id, ability_name) values
("ad84570b-f319-4648-ac9e-58fccf36d45c","recorder");
insert into ability(ability_id, ability_name) values
("8b4c9a64-a5fc-4723-88f4-26722875adc7","timing");
insert into ability(ability_id, ability_name) values
("6b61f7d0-cf3d-4c78-88a4-f5b21ee51447","sound");
insert into ability(ability_id, ability_name) values
("49862dc1-40e4-44fc-82c4-5bf7fc892e45","starter");
insert into ability(ability_id, ability_name) values
("588f39fd-33d1-4b65-b50a-210f2dfde1fe","grid");
insert into ability(ability_id, ability_name) values
("42a717fe-98cc-4b64-a6f3-c0d15e06a69b","lunch");
insert into ability(ability_id, ability_name) values
("ded593eb-168e-47a6-adeb-1fa4e09261c6","course design");
insert into ability(ability_id, ability_name) values
("9d42c4fa-071b-4464-9d5c-c6cdb3fc6be4","instructor");
insert into ability(ability_id, ability_name) values
("2525a759-04c9-4087-84f2-04c449a4c829","setup/teardown");
insert into ability(ability_id, ability_name) values
("4788bc7c-2d7e-4b40-87e7-bd5588c8e66d","early gate");
insert into ability(ability_id, ability_name) values
("b386a84f-8a25-4459-bf9d-8d2768d3c90b","tech");
insert into ability(ability_id, ability_name) values
("c3bb88c1-7c76-4044-8679-3ebf383d7984","worker boss");
insert into ability(ability_id, ability_name) values
("9a5f2830-84b6-4e14-8ea9-2e58cadff3d6","announcer");
insert into ability(ability_id, ability_name) values
("0ac58028-c5f1-4882-868b-5d24f3d4acc3", "corner boss");