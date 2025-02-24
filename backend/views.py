from rest_framework.decorators import api_view
from rest_framework.response import Response
from rest_framework.pagination import PageNumberPagination
from rest_framework import status
from .services import fetch_stock_price, fetch_ipo_calendar, fetch_company_logo
from .serializers import IPOCalendarSerializer

class IPOCalendarPagination(PageNumberPagination):
    """Custom pagination for IPO Calendar"""
    page_size = 10  # Default items per page
    page_size_query_param = 'page_size'
    max_page_size = 50

@api_view(["GET"])
def stock_price_view(request, symbol):
    """Fetch and return stock price for a given symbol"""
    price = fetch_stock_price(symbol)
    if isinstance(price, dict) and "error" in price:
        return Response(price, status=status.HTTP_400_BAD_REQUEST)
    return Response({"symbol": symbol, "price": price})

@api_view(["GET"])
def ipo_calendar_view(request):
    """Fetch, filter, and paginate IPO calendar data"""
    ipo_data = fetch_ipo_calendar().get("ipo_calendar", [])

    # Filtering logic
    stock_exchange = request.GET.get("stock_exchange")
    min_price = request.GET.get("min_price")
    max_price = request.GET.get("max_price")

    filtered_ipo_data = []

    for ipo in ipo_data:
            if stock_exchange and ipo.get("stock_exchange") != stock_exchange:
                continue  # Skip if stock exchange does not match

            try:
                ipo_price = ipo.get("ipo_price")
                if min_price and (ipo_price is None or float(ipo_price) < float(min_price)):
                    continue  # Skip if below min price
                if max_price and (ipo_price is None or float(ipo_price) > float(max_price)):
                    continue  # Skip if above max price
            except ValueError:
                return Response({"error": "Invalid price filter value"}, status=status.HTTP_400_BAD_REQUEST)

            filtered_ipo_data.append(ipo)

    ipo_data = filtered_ipo_data  # Replace with filtered data

    # Paginate results
    paginator = IPOCalendarPagination()
    paginated_data = paginator.paginate_queryset(ipo_data, request)
    serialized_data = IPOCalendarSerializer(paginated_data, many=True)

    return paginator.get_paginated_response(serialized_data.data)

@api_view(["GET"])
def company_logo_view(request, company_name):
    """Fetch and return company logo URL by company name"""
    logo_data = fetch_company_logo(company_name)
    if "logo_url" in logo_data:
        return Response({"company": company_name, "logo_url": logo_data["logo_url"]})
    return Response(logo_data, status=status.HTTP_404_NOT_FOUND)
