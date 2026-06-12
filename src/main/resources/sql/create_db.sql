USE master;
GO

IF EXISTS (SELECT * FROM sys.databases WHERE name = 'SmarthomeDB')
BEGIN
DROP DATABASE SmarthomeDB;
END
    GO

CREATE DATABASE SmarthomeDB;
GO

USE SmarthomeDB;
GO

PRINT 'SmarthomeDB database created successfully.';
GO