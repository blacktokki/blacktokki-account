
    alter table `user` 
       add column us_otp_deletion_requested bit;

    alter table `user` 
       add column us_otp_secret varchar(255);
