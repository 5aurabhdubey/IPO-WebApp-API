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
    currency = serializers.CharField()
    listing_date = serializers.CharField()
    ipo_price = serializers.FloatField(required=False, allow_null=True, default=None)
    listing_price = serializers.FloatField(required=False, allow_null=True, default=None)
    current_market_price = serializers.FloatField(required=False, allow_null=True, default=None)
    current_return = serializers.CharField()
    issue_type = serializers.CharField()
    status = serializers.CharField()
    rhp_pdf = serializers.CharField()
    drhp_pdf = serializers.CharField()