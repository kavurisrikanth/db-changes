create or replace function type_of_user(_id bigint, out result integer) as $$
	begin
	   select case 
	   		when a9._id is not null then 9
	   		else 23 end
	   from _user a23
	   left join _anonymous_user a9 on a9._id = a23._id
	   where a23._id = $1
	   into result;
	end
$$ language plpgsql;

