drop procedure if exists selectCurrentWeatherIfUpdate;
drop procedure if exists selectHourlyWeatherIfUpdate;
drop procedure if exists selectDailyWeatherIfUpdate;

delimiter $$

create procedure selectCurrentWeatherIfUpdate(IN cityName varchar(25))
begin
    declare difference bigint default 100;
    select timestampdiff(minute, updated_time, now()) into difference from locations where city = cityName;
    if  difference > 5 then select null;
    else select * from current_weather where city = cityName;
    end if;
end$$

create procedure selectHourlyWeatherIfUpdate(IN cityName varchar(25))
begin
    declare difference bigint default 100;
    select timestampdiff(minute, updated_time, now()) into difference from locations where city = cityName;
    if  difference > 5 then select null;
    else select * from hourly_weather where city = cityName;
    end if;
end$$
create procedure selectDailyWeatherIfUpdate(IN cityName varchar(25))
begin
    declare difference bigint default 100;
    select timestampdiff(minute, updated_time, now()) into difference from locations where city = cityName;
    if  difference > 5 then select null;
    else select * from daily_weather where city = cityName;
    end if;
end$$

delimiter ;