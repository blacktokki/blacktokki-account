
    create table `group` (
       gr_id bigint not null,
        dt_created datetime,
        dt_updated datetime,
        gr_image_url varchar(255),
        gr_name varchar(255),
        gr_parent_id bigint,
        gr_root_id bigint,
        primary key (gr_id)
    ) engine=InnoDB;

    create table hibernate_sequence (
       next_val bigint
    ) engine=InnoDB;

    insert into hibernate_sequence values ( 1 );

    create table membership (
       mb_id bigint not null,
        dt_created datetime,
        dt_updated datetime,
        gr_id bigint,
        us_id bigint,
        primary key (mb_id)
    ) engine=InnoDB;

    create table `user` (
       us_id bigint not null,
        dt_created datetime,
        dt_updated datetime,
        us_image_url varchar(255),
        us_is_admin bit,
        us_is_guest bit,
        us_name varchar(255),
        us_password varchar(255),
        us_username varchar(255),
        primary key (us_id)
    ) engine=InnoDB;

    alter table membership 
       add constraint FKcte8cj0e29ihgrj53x5fd21uo 
       foreign key (gr_id) 
       references `group` (gr_id);

    alter table membership 
       add constraint FKmqttifplbynkviunutarapvlo 
       foreign key (us_id) 
       references `user` (us_id);
