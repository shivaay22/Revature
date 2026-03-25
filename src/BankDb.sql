create database bankDB;
use bankDB;

create table Branch(
BranchId int auto_increment primary key,
Branchcode char(6) not null unique,
BranchName varchar(100) not null,
IFSCCode char(11) not null Unique,
Address varchar(150),
City varchar(50),
Email varchar(100) not null unique,
OpenDate date Not null,
isActive bit not null default 1
);

INSERT INTO Branch 
(Branchcode, BranchName, IFSCCode, Address, City, Email, OpenDate, isActive)
VALUES
('B001', 'Main Branch', 'IFSC0001', 'MG Road, Sector 1', 'Delhi', 'main@bank.com', '2015-01-10', 1),
('B002', 'City Center Branch', 'IFSC0002', 'Connaught Place', 'Delhi', 'citycenter@bank.com', '2016-03-15', 1),
('B003', 'North Branch', 'IFSC0003', 'Civil Lines', 'Lucknow', 'north@bank.com', '2017-05-20', 1),
('B004', 'South Branch', 'IFSC0004', 'T Nagar', 'Chennai', 'south@bank.com', '2018-07-25', 1),
('B005', 'West Branch', 'IFSC0005', 'Andheri West', 'Mumbai', 'west@bank.com', '2019-09-30', 1),
('B006', 'East Branch', 'IFSC0006', 'Salt Lake', 'Kolkata', 'east@bank.com', '2020-11-05', 1),
('B007', 'Tech Park Branch', 'IFSC0007', 'Whitefield', 'Bangalore', 'techpark@bank.com', '2021-02-18', 1),
('B008', 'Industrial Branch', 'IFSC0008', 'MIDC Area', 'Pune', 'industrial@bank.com', '2022-04-22', 1),
('B009', 'Market Branch', 'IFSC0009', 'Chandni Chowk', 'Delhi', 'market@bank.com', '2023-06-12', 1),
('B010', 'University Branch', 'IFSC0010', 'Near DU Campus', 'Delhi', 'university@bank.com', '2024-08-01', 1);

select * from Branch;

create schema Branches;

Create table Customer(
CustomerId int,
CustomerName varchar(50));

insert into Customer values (101,'Bhardwaj');

insert into Customer (CustomerName)
values ('Shivam'),
('Ananya'),
('Deekshita');

select * from Customer;

set SQL_SAFE_UPDATES=0;
delete from Customer where CustomerId is NULL;

Alter table Customer Modify CustomerId int not null;

desc Customer;

alter table Customer Modify 
CustomerId int not null auto_increment,
Add Primary Key(CustomerId);

Alter Table Customer
Add Email Varchar(100) unique,
Add ContactNo bigint;


SELECT department, COUNT(*) AS total_employees
FROM Employee
GROUP BY department
ORDER BY total_employees DESC;



