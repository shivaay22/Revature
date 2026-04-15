create database ecommercedb;
use ecommercedb;

create table customers (
    customerid int,
    customername varchar(100),
    city varchar(50),
    email varchar(100)
);

create table products (
    productid int,
    productname varchar(100),
    category varchar(50),
    price decimal(10,2)
);

create table orders (
    orderid int,
    customerid int,
    orderdate date,
    totalamount decimal(10,2)
);

create table orderitems (
    orderitemid int,
    orderid int,
    productid int,
    quantity int
);

alter table customers add constraint pk_customers primary key (customerid);
alter table products add constraint pk_products primary key (productid);
alter table orders add constraint pk_orders primary key (orderid);
alter table orderitems add constraint pk_orderitems primary key (orderitemid);

alter table orders
add constraint fk_orders_customers
foreign key (customerid) references customers(customerid);

alter table orderitems
add constraint fk_orderitems_orders
foreign key (orderid) references orders(orderid);

alter table orderitems
add constraint fk_orderitems_products
foreign key (productid) references products(productid);

insert into customers values
(1, 'Amit', 'Delhi', 'amit@gmail.com'),
(2, 'Anita', 'Chennai', 'anita@gmail.com'),
(3, 'Rahul', 'Mumbai', 'rahul@gmail.com'),
(4, 'Arjun', 'Bangalore', 'arjun@gmail.com'),
(5, 'Sneha', 'Chennai', null);

insert into products values
(101, 'Mobile', 'Electronics', 15000),
(102, 'Laptop', 'Electronics', 60000),
(103, 'Chair', 'Furniture', 3000),
(104, 'Table', 'Furniture', 8000),
(105, 'Headphones', 'Accessories', 2000);

insert into orders values
(201, 1, '2024-02-10', 20000),
(202, 2, '2024-03-01', 50000),
(203, 3, '2024-01-15', 15000),
(204, 4, '2024-02-20', 70000),
(205, 2, '2024-03-10', 30000);

insert into orderitems values
(1, 201, 101, 1),
(2, 202, 102, 1),
(3, 203, 103, 2),
(4, 204, 104, 3),
(5, 205, 105, 2),
(6, 202, 101, 1);

select quantity * 1000 as estimatedtotal from orderitems;
select productname, price + 500 as increasedprice from products;
select productname, price - 200 as reducedprice from products;
select quantity * 2 as doublequantity from orderitems;
select orderid, totalamount / 2 as halfamount from orders;

select * from orders where totalamount > 10000;
select * from products where price < 5000;
select * from orders where totalamount >= 25000;
select * from products where price <= 8000;
select * from customers where city = 'Chennai';
select * from customers where city <> 'Delhi';

select * from customers 
where city = 'Bangalore' and customername like 'A%';

select * from products 
where category = 'Electronics' or category = 'Furniture';

select * from orders 
where totalamount > 5000 and totalamount < 70000;

select * from customers where not city = 'Mumbai';
select * from products where category != 'Accessories';

select * from products where category in ('Electronics', 'Furniture');
select * from customers where city not in ('Chennai', 'Bangalore');

select * from orders where totalamount between 5000 and 60000;
select * from orders where totalamount not between 5000 and 60000;

select * from customers where customername like '%a';
select * from products where productname like 'M%';

select * from customers where email is null;
select * from customers where email is not null;

select * from customers where city = 'Chennai';
select * from orders where orderdate > '2024-02-01';
select * from products where price > 10000;
select * from orderitems where quantity = 1;
select * from customers where email is null;

select customerid, count(*) as totalorders 
from orders group by customerid;

select customerid, sum(totalamount) as revenue 
from orders group by customerid;

select customerid, avg(totalamount) as avgorder 
from orders group by customerid;

select productid, sum(quantity) as totalsold 
from orderitems group by productid;

select category, count(*) as productcount 
from products group by category;

select customerid, sum(totalamount) as total
from orders
group by customerid
having sum(totalamount) > 50000;

select customerid, count(*) 
from orders
group by customerid
having count(*) > 1;

select productid, sum(quantity)
from orderitems
group by productid
having sum(quantity) > 1;

select category, count(*)
from products
group by category
having count(*) > 1;

select customerid, avg(totalamount)
from orders
group by customerid
having avg(totalamount) > 20000;

select c.customername, o.totalamount
from customers c
join orders o on c.customerid = o.customerid;

select p.productname, oi.quantity
from products p
join orderitems oi on p.productid = oi.productid;

select c.customername, o.orderdate, o.totalamount
from customers c
join orders o on c.customerid = o.customerid;

select oi.orderid, p.productname, oi.quantity
from orderitems oi
join products p on oi.productid = p.productid;

select *
from customers c
join orders o on c.customerid = o.customerid
join orderitems oi on o.orderid = oi.orderid
join products p on oi.productid = p.productid;

select customerid, sum(totalamount) as revenue
from orders
group by customerid
order by revenue desc;

select productid, sum(quantity) as sold
from orderitems
group by productid
order by sold desc;

select * from customers where email is null;

select month(orderdate) as month, sum(totalamount)
from orders
group by month(orderdate);

select * from orders where totalamount > 20000;

select * from products
where productid not in (select productid from orderitems);

select * from customers
where customerid not in (select customerid from orders);

select customerid, sum(totalamount) as revenue
from orders
group by customerid
order by revenue desc
limit 3;

select p.category, sum(oi.quantity)
from products p
join orderitems oi on p.productid = oi.productid
group by p.category;

select customerid, avg(totalamount)
from orders
group by customerid;