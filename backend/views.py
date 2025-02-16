from rest_framework.decorators import api_view
from rest_framework.response import Response
from .services import fetch_stock_price, fetch_ipo_calendar, fetch_company_logo

@api_view(["GET"])
def stock_price_view(request, symbol):
    """Fetch and return stock price for a given symbol"""
    price = fetch_stock_price(symbol)
    if price:
        return Response({"symbol": symbol, "price": price})
    return Response({"error": "Stock price not found"}, status=404)

@api_view(["GET"])
def ipo_calendar_view(request):
    """Fetch and return IPO calendar data"""
    ipo_data = fetch_ipo_calendar()
    return Response({"ipo_calendar": ipo_data})

@api_view(["GET"])
def company_logo_view(request, company_name):
    """Fetch and return company logo URL by company name"""
    logo_data = fetch_company_logo(company_name)
    if "logo_url" in logo_data:
        return Response({"company": company_name, "logo_url": logo_data["logo_url"]})
    return Response(logo_data, status=404)  # Return error message directly
