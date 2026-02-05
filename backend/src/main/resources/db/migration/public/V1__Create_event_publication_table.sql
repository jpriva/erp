create table event_publication
(
    id                     uuid    not null primary key,
    completion_attempts    integer not null,
    completion_date        timestamp(6) with time zone,
    event_type             varchar(255),
    last_resubmission_date timestamp(6) with time zone,
    listener_id            varchar(255),
    publication_date       timestamp(6) with time zone,
    serialized_event       varchar(255),
    status                 varchar(255)
        constraint event_publication_status_check
            check ((status)::text = ANY
                   ((ARRAY ['PUBLISHED'::character varying, 'PROCESSING'::character varying, 'COMPLETED'::character varying, 'FAILED'::character varying, 'RESUBMITTED'::character varying])::text[]))
);