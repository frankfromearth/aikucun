-- 
alter table sc_checkbillapplys add column  typeCode varchar(2) comment '类型码,UM=UserMonth用户按月,UD=UserDelivery用户按发货单,UA=UserAcitivty用户按活动';

alter table sc_checkbillapplys change column applyYearMonth typeValue varchar(33) comment '类型值 ,对应于类型码' after typeCode;

   