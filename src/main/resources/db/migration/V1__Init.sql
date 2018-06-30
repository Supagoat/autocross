create table person (
	club_id varchar(38) NOT NULL,
	person_id varchar(38) NOT NULL,
    first_name varchar(64) NOT NULL,
    last_name varchar(64) NOT NULL,
    email_hash varchar(32),
    primary_number varchar(10),
    primary key(club_id, person_id)
);

create table certification(
	person_id varchar(38) NOT NULL,
	ability_id varchar(38) NOT NULL,
	skill_level int NOT NULL,
	primary key(person_id, ability_id)
);
	