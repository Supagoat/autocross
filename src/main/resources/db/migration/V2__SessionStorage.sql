CREATE TABLE session_data (
    id varchar(64) PRIMARY KEY,
    data JSON,
    created TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
    updated TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now()
);