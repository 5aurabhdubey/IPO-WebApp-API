from django.db import models
class Users(models.Model):
    id = models.AutoField(primary_key=True)
    username = models.CharField(max_length=100)
    email = models.EmailField(unique=True)
    first_name = models.CharField(max_length=100)
    last_name = models.CharField(max_length=100)
    staff_status = models.BooleanField(default=False)


class IPO_INFO(models.Model):
    id = models.BigIntegerField(primary_key=True)
    company_logo_path = models.TextField()
    company_name = models.CharField(max_length=100)
    price_band = models.CharField(max_length=100)
    open = models.CharField(max_length=100)
    close = models.CharField(max_length=100)
    issue_size = models.CharField(max_length=100)
    issue_type = models.CharField(max_length=100)
    listing_date = models.CharField(max_length=100)
    status = models.IntegerField()
    ipo_price = models.CharField(max_length=100)
    listing_price = models.CharField(max_length=100)
    listing_gain = models.CharField(max_length=100)
    cmp = models.CharField(max_length=100)
    current_return = models.CharField(max_length=100)
    rhp = models.CharField(max_length=100)
    drhp = models.CharField(max_length=100)
class IPO(models.Model):
    symbol = models.CharField(max_length=10,unique=True)
    company_name = models.CharField(max_length=255)
    listing_date = models.DateField()
    ipo_price = models.DecimalField(max_digits=10,decimal_places=2)
    current_market_price = models.DecimalField(max_digits=10, decimal_places=2,null=True,blank=True)

class Transaction(models.Model):
    #Relationship with Users model
    user = models.ForeignKey(Users,on_delete=models.CASCADE)
    #Relationship with IPO model
    ipo = models.ForeignKey(IPO,on_delete=models.CASCADE)
    #number of shares bought
    quantity = models.IntegerField()
    #Purchase price per shares
    transaction_price = models.DecimalField(max_digits=10, decimal_places=2)
    transaction_date = models.DateTimeField(auto_now_add=True)

    def __str__(self):
        return self.company_name

# import os
# from dotenv import load_dotenv
# import psycopg2
# from urllib.parse import urlparse

# # Load environment variables from .env file
# load_dotenv()

# # Get the DATABASE_URL from the environment variables
# database_url = os.getenv("DATABASE_URL")

# # Parse the URL
# tmpPostgres = urlparse(database_url)

# # Connect to PostgreSQL
# conn = psycopg2.connect(
#     database=tmpPostgres.path[1:],
#     user=tmpPostgres.username,
#     password=tmpPostgres.password,
#     host=tmpPostgres.hostname,
#     port=tmpPostgres.port
# )

# # Create a cursor
# cur = conn.cursor()

# # Define the table schema
# create_table_query = '''
# CREATE TABLE test1 (
#     id SERIAL PRIMARY KEY,
#     name VARCHAR(100) NOT NULL,
#     email VARCHAR(100) UNIQUE NOT NULL
# );
# '''

# # Execute the create table query
# cur.execute(create_table_query)

# # Commit the transaction
# conn.commit()

# # Close the cursor and connection
# cur.close()
# conn.close()
