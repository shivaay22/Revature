use BankDb;

create Table Customers(
customerId int primary Key auto_increment,
customerName varchar(100) not null,
gender varchar(10),
dateOfBirth date,
phoneNumber varchar(15) unique not null,
email varchar(100) unique not null,
city varchar(50),
state varchar(50),
customerStatus varchar(10) check (customerStatus in(
'active','inActive'))
);

insert into Customers(customerName, gender, dateOfBirth, phoneNumber, email, city, state, customerStatus)
values 
('shivam', 'male', '2002-05-10', '9876543210', 'shivam@gmail.com', 'patna', 'bihar', 'active'),
('rahul', 'male', '1998-02-20', '9123456780', 'rahul@gmail.com', 'chennai', 'tamilnadu', 'active');

drop table branches;
create table branches (
branchId int primary key auto_increment,
branchName varchar(100) not null,
branchCode varchar(100) unique not null,
city varchar(50),
state varchar(50),
ifsccode varchar(20) unique not null
);

insert into branches(branchName, branchCode, city, state, ifsccode)
values 
('main branch', 'BR001', 'patna', 'bihar', 'IFSC001'),
('south branch', 'BR002', 'chennai', 'tamilnadu', 'IFSC002');

drop table accounts;

create table accounts (
accountId int primary key auto_increment,
customerId int,
branchId int,
accountNumber varchar(20) unique not null,
accountType varchar(20) check (accountType in ('savings','current',
'fixed deposit')),
openDate date,
balance decimal(10,2) not null,
accountStatus varchar(20) check (accountStatus in ('active','closed','frozen')),

foreign key (customerId) references Customers(customerId),
foreign key (branchId) references branches(branchId)
);

insert into accounts(customerId, branchId, accountNumber, accountType, openDate, balance, accountStatus)
values
(1,1,'ACC001','savings','2023-01-01',50000,'active'),
(2,2,'ACC002','current','2023-02-01',150000,'active');

drop table transactions;

create table transactions(
    transactionId int primary key auto_increment,
    accountId int,
    transactionDate datetime default current_timestamp,
    transactionType varchar(20) check (transactionType in ('deposit','withdrawal','transfer')),
    amount decimal(10,2),
    description varchar(255),

    foreign key (accountId) references accounts(accountId)
);

insert into transactions(accountId, transactionType, amount, description)
values
(1,'deposit',5000,'salary'),
(2,'withdrawal',2000,'atm');


drop table loans;


create table loans(
laonId int primary key auto_increment,
customerId int,
branchId int,
loanType varchar(50),
loanAmount decimal(12,2),
interestRate decimal(5,2),
loanStartDate date,
loanStatus varchar(20) check (loanStatus in ('approved','pending','closed')),

foreign key (customerId) references Customers(customerId),
foreign key (branchId) references branches(branchId)
);

insert into loans (customerId, branchId, loanType, loanAmount, interestRate, loanStartdate, loanStatus)
values
(1,1,'home loan',500000,7.5,'2023-01-10','approved');

drop table accountAudit;

create table accountAudit (
    auditId int primary key auto_increment,
    accountId int,
    actionType varchar(50),
    oldBalance decimal(10,2),
    newBalance decimal(10,2),
    actionDate datetime default current_timestamp,
    remarks varchar(255)
);

select * from Customers where customerId in (
select customerId from accounts
where balance > (select avg(balance) from accounts)
);

select * from Customers where customerId not in (
select customerId from loans
);

select * from accounts where balance = (
select distinct balance from accounts
order by balance desc limit 1 offset 1
);

select * from customers where customerId in (
select customerId from accounts
group by customerId
having count(*) > 1
);

create view vw_customerAccountDetails as 
select c.customerId, c.customerName, a.accountNumber,
a.accountType, a.balance, a.accountStatus from Customers
join accounts a on c.customerId = a.customerId;

create view vw_highValueAccounts as
select * from accounts
where balance > 100000;
