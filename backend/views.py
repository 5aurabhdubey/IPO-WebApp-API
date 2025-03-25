from rest_framework.decorators import api_view
from rest_framework.response import Response
from rest_framework.pagination import PageNumberPagination
from rest_framework import status
from .services import fetch_stock_price, fetch_ipo_calendar, fetch_company_logo
from .serializers import IPOCalendarSerializer
from .models import IPO
from .serializers import IPOSerializer
from django.shortcuts import render

class IPOCalendarPagination(PageNumberPagination):
    """Custom pagination for IPO Calendar"""
    page_size = 12  # Default items per page
    page_size_query_param = 'page_size'
    max_page_size = 50

def ipo_calendar_frontend(request):
    return render(request, "ipo-listing/index.html")

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
    paginator = IPOCalendarPagination()
    paginated_data = paginator.paginate_queryset(ipo_data, request)
    serialized_data = IPOCalendarSerializer(paginated_data, many=True)
    return paginator.get_paginated_response(serialized_data.data)
    # print(ipo_data)
    # stock_exchange = request.GET.get("stock_exchange")
    # min_price = request.GET.get("min_price")
    # max_price = request.GET.get("max_price")
    # filtered_ipo_data = []
    # for ipo in ipo_data:
    #     if stock_exchange and ipo.get("stock_exchange") != stock_exchange:
    #         continue
    #     try:
    #         ipo_price = ipo.get("ipo_price")
    #         if min_price and (ipo_price is None or float(ipo_price) < float(min_price)):
    #             continue
    #         if max_price and (ipo_price is None or float(ipo_price) > float(max_price)):
    #             continue
    #     except ValueError:
    #         return Response({"error": "Invalid price filter value"}, status=status.HTTP_400_BAD_REQUEST)
    #     filtered_ipo_data.append(ipo)
    # ipo_data = filtered_ipo_data
    # return ipo_data

@api_view(["GET"])
def company_logo_view(request, company_name):
    """Fetch and return company logo URL by company name"""
    logo_data = fetch_company_logo(company_name)
    if "logo_url" in logo_data:
        return Response({"company": company_name, "logo_url": logo_data["logo_url"]})
    return Response(logo_data, status=status.HTTP_404_NOT_FOUND)

@api_view(['POST'])
def create_ipo(request):
    serializer = IPOSerializer(data=request.data)
    if serializer.is_valid():
        serializer.save()
        return Response(serializer.data, status=201)
    return Response(serializer.errors, status=400)

@api_view(['GET'])
def get_all_ipos(request):
    ipos = IPO.objects.all()
    serializer = IPOSerializer(ipos, many=True)
    return Response(serializer.data)

@api_view(['GET'])
def get_ipo(request, pk):
    try:
        ipo = IPO.objects.get(pk=pk)
        serializer = IPOSerializer(ipo)
        return Response(serializer.data)
    except IPO.DoesNotExist:
        return Response({'error': 'IPO not found'}, status=404)

@api_view(['PUT'])
def update_ipo(request, pk):
    try:
        ipo = IPO.objects.get(pk=pk)
        serializer = IPOSerializer(ipo, data=request.data)
        if serializer.is_valid():
            serializer.save()
            return Response(serializer.data)
        return Response(serializer.errors, status=400)
    except IPO.DoesNotExist:
        return Response({'error': 'IPO not found'}, status=404)

@api_view(['DELETE'])
def delete_ipo(request, pk):
    try:
        ipo = IPO.objects.get(pk=pk)
        ipo.delete()
        return Response(status=204)
    except IPO.DoesNotExist:
        return Response({'error': 'IPO not found'}, status=404)

@api_view(['PATCH'])
def update_ipo_status(request, pk):
    try:
        ipo = IPO.objects.get(pk=pk)
        status = request.data.get('status')
        if status not in ['pending', 'active', 'completed', 'cancelled']:
            return Response({'error': 'Invalid status'}, status=400)
        ipo.status = status
        ipo.save()
        serializer = IPOSerializer(ipo)
        return Response(serializer.data)
    except IPO.DoesNotExist:
        return Response({'error': 'IPO not found'}, status=404)