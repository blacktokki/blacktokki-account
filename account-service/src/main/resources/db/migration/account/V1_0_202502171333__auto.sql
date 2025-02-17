    drop table membership;

    drop table `group`;
    
    alter table `user` 
       add column us_oauth varchar(255);
