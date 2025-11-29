# Lottery Result Data Warehouse
A Data Warehouse system for storing lottery result.

## Structure
Each directory is a Java project represent a process in Data Warehouse system that can packaged into a `.jar` file and run it on a server.

| ProcessID | Name                          | Description                                                                                     |
|:----------|:------------------------------|:------------------------------------------------------------------------------------------------|
| 0         | LoadConfig                    | This process is run under other process. It load config data for other process used             |
| 1, 2, 3   | CrawlData                     | Collect data for system and save it to a *result directory*                                     |
| 4         | LoadDataToStaging             | Read data stored in *result directory* and load it into **Staging** database                    |
| 5, 6      | TransformDataToWarehouse      | Transform data in Staging by formatting to prepare for load it into **Data Warehouse** database |
| 7, 8      | LoadDataToDataWarehouse       | Load data after transformed into Data Warehouse database                                        |
| 9         | LoadDimDataWarehouseToStaging | Load `dim_date` data into **Staging** and **Data Warehouse** database                           |
| 10        | LoadAggregate                 | Measure data for `aggregate` tables in **Data Warehouse** database                              |
| 11        | DumpAggregate                 | Dump data in `aggregate` tables into a result file                                              |
| 12        | LoadDataToDataWarehouse       | Load data in result file for process 11 into **Data Mart** database                             |