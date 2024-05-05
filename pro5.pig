customers = LOAD '/home/cloudera/Desktop/customer.csv' USING PigStorage(',') AS (customerID:chararray, customerName:chararray, customerAddress:chararray, customerPhone:chararray);
orders = LOAD '/home/cloudera/Desktop/order.csv' USING PigStorage(',') AS (customerID:chararray, itemID:chararray, itemName:chararray, orderDate:chararray, deliveryDate:chararray);
orders_by_customer = GROUP orders BY customerID;
items_bought_by_customer = FOREACH orders_by_customer GENERATE group AS customerID, COUNT(orders.itemID) AS num_items_bought;
joined_data = JOIN items_bought_by_customer BY customerID, customers BY customerID;
DUMP joined_data;
## pig -x local process_data.pig
