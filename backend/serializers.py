from rest_framework import serializers
from .models import IPO

class IPOSerializer(serializers.ModelSerializer):
    class Meta:
        model = IPO
        fields = '__all__'

class IPOCalendarSerializer(serializers.Serializer):
    symbol = serializers.CharField()
    company_name = serializers.CharField()
    stock_exchange = serializers.CharField()
    currency = serializers.CharField(required=False, allow_null=True, default="")
    listing_date = serializers.DateField(required=False, allow_null=True)
    ipo_price = serializers.CharField(required=False, allow_null=True, default=None)
    listing_price = serializers.FloatField(required=False, allow_null=True, default=None)
    opening_date = serializers.DateField(required=False, allow_null=True)  # ✅ New field
    close_date = serializers.DateField(required=False, allow_null=True)  
    issue_size = serializers.CharField(required=False, allow_null=True)
    current_market_price = serializers.FloatField(required=False, allow_null=True, default=None)
    current_return = serializers.CharField(required=False, allow_null=True, default="")
    issue_type = serializers.CharField(required=False, allow_null=True, default=None)
    status = serializers.CharField(required=False, allow_null=True, default=None)
    rhp_pdf = serializers.CharField(required=False, allow_null=True, default=None)
    drhp_pdf = serializers.CharField(required=False, allow_null=True, default=None)