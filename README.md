# DBReplica-Heterogeneous-Algorithm
This project implements three algorithm, our Simulate Anneal DBReplica Heterogeneos algorithm, Divergent Physical Design Tuning algorithm, and RITA.

## DBReplica Heterogeneous


## Divergent Physical Tuning
[Divergent Design Tuning](https://dl.acm.org/citation.cfm?id=2791376) was proposed on SIGMOD 2012.


## RITA

## Implementation


## Comparison

## EXPERIMENT ON CASSANDRA
### Data Prepare
1. start cassandra local server.
```
$ cassandra
```
2. use `cqlsh` to create key space and table.
```
$ cd ~/.../apache-cassandra
$ cqlsh localhost
cqlsh> CREATE KEYSPACE tpch with replication={'class':'SimpleStrategy','replication_factor':1};
cqlsh> USE tpch
cqlsh:tpch> CREATE TABLE default_table(pkey INT,
            L_ORDERKEY INT,
            L_PARTKEY INT,
            L_SUPPKEY INT,
            L_LINENUMBER INT,
            L_QUANTITY DECIMAL,
            L_EXTENDEDPRICE DECIMAL,
            L_DISCOUNT DECIMAL,
            L_TAX DECIMAL,
            L_RETURNFLAG TEXT,
            L_LINESTATUS TEXT,
            L_SHIPDATE DATE,
            L_COMMITDATE DATE,
            L_RECEIPTDATE DATE,
            L_SHIPINSTRUCT TEXT,
            L_SHIPMODE TEXT,
            L_COMMENT TEXT,
            primary key(pkey, L_ORDERKEY, L_PARTKEY, L_SUPPKEY, L_LINENUMBER, L_SHIPDATE, L_COMMITDATE, L_RECEIPTDATE)
            ) with dclocal_read_repair_chance=0;
```
3. load data in "lineitem.tbl" to cassandra
   
Use cassandra-loader to load the csv file into cassandra. [Cassandra-loader](https://github.com/brianmhess/cassandra-loader) is an open source project, a general-purpose, delimited-file, bulk loader for Cassandra. Conventinally, separator of a csv file is comma, so merely cqlsh command `cqlsh> COPY table_name (col1, col2, ...) FROM 'filepath.csv' WITH HEADER = true/false` works well. However, content in TEXT column may have the charactor ',' as part of the string. As a result, comma separator is replaced with '|'. In this case, cqlsh command fails to work. But cassandra-loader provide flexible choices for users. That is, custmized separator can be specified by the parameter `-delim`. 
```
sh cassandra-loader -f 2019-04-08/lineitem3.tbl -delim "|" -host localhost -schema "tpch.default_table(pkey, L_ORDERKEY, L_PARTKEY, L_SUPPKEY, L_LINENUMBER, L_QUANTITY, L_EXTENDEDPRICE, L_DISCOUNT, L_TAX, L_RETURNFLAG, L_LINESTATUS, L_SHIPDATE, L_COMMITDATE, L_RECEIPTDATE, L_SHIPINSTRUCT, L_SHIPMODE, L_COMMENT)"
```

### Experiment on Simulate Annealing Algorithm
 1. Create data tables. There are 6 tables totally. Mode number indicates the cost model we use. Mode 0 means cask effect cost model, while mode 1 means total cost model. For mode 0 experiment, we choose `[4, 5, 6, 1, 3, 0, 2][0, 4, 5, 6, 1, 3, 2][2, 4, 5, 6, 1, 0, 3]` as column permutations for the 3 replicas. For mode 0, we choose `[4, 3, 2, 1, 5, 0, 6][0, 5, 6, 1, 2, 3, 4][2, 1, 6, 0, 4, 5, 3]`.
```
cqlsh:tpch> CREATE TABLE simulateanneal_mod0_rp0(pkey INT,
            L_ORDERKEY INT,
            L_PARTKEY INT,
            L_SUPPKEY INT,
            L_LINENUMBER INT,
            L_QUANTITY DECIMAL,
            L_EXTENDEDPRICE DECIMAL,
            L_DISCOUNT DECIMAL,
            L_TAX DECIMAL,
            L_RETURNFLAG TEXT,
            L_LINESTATUS TEXT,
            L_SHIPDATE DATE,
            L_COMMITDATE DATE,
            L_RECEIPTDATE DATE,
            L_SHIPINSTRUCT TEXT,
            L_SHIPMODE TEXT,
            L_COMMENT TEXT,
            primary key(pkey, L_QUANTITY, L_EXTENDEDPRICE, L_DISCOUNT, L_PARTKEY, L_LINENUMBER, L_ORDERKEY, L_SUPPKEY)
            ) with dclocal_read_repair_chance=0;

cqlsh:tpch> CREATE TABLE simulateanneal_mod0_rp1(pkey INT,
            L_ORDERKEY INT,
            L_PARTKEY INT,
            L_SUPPKEY INT,
            L_LINENUMBER INT,
            L_QUANTITY DECIMAL,
            L_EXTENDEDPRICE DECIMAL,
            L_DISCOUNT DECIMAL,
            L_TAX DECIMAL,
            L_RETURNFLAG TEXT,
            L_LINESTATUS TEXT,
            L_SHIPDATE DATE,
            L_COMMITDATE DATE,
            L_RECEIPTDATE DATE,
            L_SHIPINSTRUCT TEXT,
            L_SHIPMODE TEXT,
            L_COMMENT TEXT,
            primary key(pkey, L_ORDERKEY,L_QUANTITY,L_EXTENDEDPRICE,L_DISCOUNT,L_PARTKEY,L_LINENUMBER,L_SUPPKEY)
            ) with dclocal_read_repair_chance=0;

cqlsh:tpch> CREATE TABLE simulateanneal_mod0_rp2(pkey INT,
            L_ORDERKEY INT,
            L_PARTKEY INT,
            L_SUPPKEY INT,
            L_LINENUMBER INT,
            L_QUANTITY DECIMAL,
            L_EXTENDEDPRICE DECIMAL,
            L_DISCOUNT DECIMAL,
            L_TAX DECIMAL,
            L_RETURNFLAG TEXT,
            L_LINESTATUS TEXT,
            L_SHIPDATE DATE,
            L_COMMITDATE DATE,
            L_RECEIPTDATE DATE,
            L_SHIPINSTRUCT TEXT,
            L_SHIPMODE TEXT,
            L_COMMENT TEXT,
            primary key(pkey, L_SUPPKEY,L_QUANTITY,L_EXTENDEDPRICE,L_DISCOUNT,L_PARTKEY,L_ORDERKEY,L_LINENUMBER)
            ) with dclocal_read_repair_chance=0;

cqlsh:tpch> CREATE TABLE simulateanneal_mod1_rp0(pkey INT,
            L_ORDERKEY INT,
            L_PARTKEY INT,
            L_SUPPKEY INT,
            L_LINENUMBER INT,
            L_QUANTITY DECIMAL,
            L_EXTENDEDPRICE DECIMAL,
            L_DISCOUNT DECIMAL,
            L_TAX DECIMAL,
            L_RETURNFLAG TEXT,
            L_LINESTATUS TEXT,
            L_SHIPDATE DATE,
            L_COMMITDATE DATE,
            L_RECEIPTDATE DATE,
            L_SHIPINSTRUCT TEXT,
            L_SHIPMODE TEXT,
            L_COMMENT TEXT,
            primary key(pkey,L_QUANTITY,L_LINENUMBER,L_SUPPKEY,L_PARTKEY,L_EXTENDEDPRICE,L_ORDERKEY,L_DISCOUNT)
            ) with dclocal_read_repair_chance=0;

cqlsh:tpch> CREATE TABLE simulateanneal_mod1_rp1(pkey INT,
            L_ORDERKEY INT,
            L_PARTKEY INT,
            L_SUPPKEY INT,
            L_LINENUMBER INT,
            L_QUANTITY DECIMAL,
            L_EXTENDEDPRICE DECIMAL,
            L_DISCOUNT DECIMAL,
            L_TAX DECIMAL,
            L_RETURNFLAG TEXT,
            L_LINESTATUS TEXT,
            L_SHIPDATE DATE,
            L_COMMITDATE DATE,
            L_RECEIPTDATE DATE,
            L_SHIPINSTRUCT TEXT,
            L_SHIPMODE TEXT,
            L_COMMENT TEXT,
            primary key(pkey,L_ORDERKEY,L_EXTENDEDPRICE,L_DISCOUNT,L_PARTKEY,L_SUPPKEY,L_LINENUMBER,L_QUANTITY)
            ) with dclocal_read_repair_chance=0;

cqlsh:tpch> CREATE TABLE simulateanneal_mod1_rp2(pkey INT,
            L_ORDERKEY INT,
            L_PARTKEY INT,
            L_SUPPKEY INT,
            L_LINENUMBER INT,
            L_QUANTITY DECIMAL,
            L_EXTENDEDPRICE DECIMAL,
            L_DISCOUNT DECIMAL,
            L_TAX DECIMAL,
            L_RETURNFLAG TEXT,
            L_LINESTATUS TEXT,
            L_SHIPDATE DATE,
            L_COMMITDATE DATE,
            L_RECEIPTDATE DATE,
            L_SHIPINSTRUCT TEXT,
            L_SHIPMODE TEXT,
            L_COMMENT TEXT,
            primary key(pkey,L_SUPPKEY,L_PARTKEY,L_DISCOUNT,L_ORDERKEY,L_QUANTITY,L_EXTENDEDPRICE,L_LINENUMBER)
            ) with dclocal_read_repair_chance=0;

```
Then load the data to these 6 tables.
```
$ sh cassandra-loader -f 2019-04-08/lineitem3.tbl -host localhost -delim "|" -schema "tpch.simulateanneal_mod0_rp0(pkey, L_ORDERKEY, L_PARTKEY, L_SUPPKEY, L_LINENUMBER, L_QUANTITY, L_EXTENDEDPRICE, L_DISCOUNT, L_TAX, L_RETURNFLAG, L_LINESTATUS, L_SHIPDATE, L_COMMITDATE, L_RECEIPTDATE, L_SHIPINSTRUCT, L_SHIPMODE, L_COMMENT)"
$ sh cassandra-loader -f 2019-04-08/lineitem3.tbl -host 127.0.0.1 -delim "|" -schema "tpch.simulateanneal_mod0_rp1(pkey, L_ORDERKEY, L_PARTKEY, L_SUPPKEY, L_LINENUMBER, L_QUANTITY, L_EXTENDEDPRICE, L_DISCOUNT, L_TAX, L_RETURNFLAG, L_LINESTATUS, L_SHIPDATE, L_COMMITDATE, L_RECEIPTDATE, L_SHIPINSTRUCT, L_SHIPMODE, L_COMMENT)"
$ sh cassandra-loader -f 2019-04-08/lineitem3.tbl -host 127.0.0.1 -delim "|" -schema "tpch.simulateanneal_mod0_rp2(pkey, L_ORDERKEY, L_PARTKEY, L_SUPPKEY, L_LINENUMBER, L_QUANTITY, L_EXTENDEDPRICE, L_DISCOUNT, L_TAX, L_RETURNFLAG, L_LINESTATUS, L_SHIPDATE, L_COMMITDATE, L_RECEIPTDATE, L_SHIPINSTRUCT, L_SHIPMODE, L_COMMENT)"
$ sh cassandra-loader -f 2019-04-08/lineitem3.tbl -host 127.0.0.1 -delim "|" -schema "tpch.simulateanneal_mod1_rp0(pkey, L_ORDERKEY, L_PARTKEY, L_SUPPKEY, L_LINENUMBER, L_QUANTITY, L_EXTENDEDPRICE, L_DISCOUNT, L_TAX, L_RETURNFLAG, L_LINESTATUS, L_SHIPDATE, L_COMMITDATE, L_RECEIPTDATE, L_SHIPINSTRUCT, L_SHIPMODE, L_COMMENT)"
$ sh cassandra-loader -f 2019-04-08/lineitem3.tbl -host 127.0.0.1 -delim "|" -schema "tpch.simulateanneal_mod1_rp1(pkey, L_ORDERKEY, L_PARTKEY, L_SUPPKEY, L_LINENUMBER, L_QUANTITY, L_EXTENDEDPRICE, L_DISCOUNT, L_TAX, L_RETURNFLAG, L_LINESTATUS, L_SHIPDATE, L_COMMITDATE, L_RECEIPTDATE, L_SHIPINSTRUCT, L_SHIPMODE, L_COMMENT)"
$ sh cassandra-loader -f 2019-04-08/lineitem3.tbl -host 127.0.0.1 -delim "|" -schema "tpch.simulateanneal_mod1_rp2(pkey, L_ORDERKEY, L_PARTKEY, L_SUPPKEY, L_LINENUMBER, L_QUANTITY, L_EXTENDEDPRICE, L_DISCOUNT, L_TAX, L_RETURNFLAG, L_LINESTATUS, L_SHIPDATE, L_COMMITDATE, L_RECEIPTDATE, L_SHIPINSTRUCT, L_SHIPMODE, L_COMMENT)"

```