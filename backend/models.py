from django.db import models

class IPO(models.Model):
    company_name = models.CharField(max_length=100)
    ticker = models.CharField(max_length=10, unique=True)
    offering_date = models.DateField()
    price_range_min = models.FloatField()
    price_range_max = models.FloatField()
    shares_offered = models.BigIntegerField()
    status = models.CharField(max_length=20, choices=[('pending', 'Pending'), ('active', 'Active'), ('completed', 'Completed'), ('cancelled', 'Cancelled')], default='pending')
    created_at = models.DateTimeField(auto_now_add=True)
    updated_at = models.DateTimeField(auto_now=True)

    def __str__(self):
        return self.company_name