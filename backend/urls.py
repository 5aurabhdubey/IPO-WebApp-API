from django.urls import path
from .views import stock_price_view, ipo_calendar_view, company_logo_view

urlpatterns = [
    path("api/stock/<str:symbol>/", stock_price_view, name="stock-price"),
    path("api/ipo-calendar/", ipo_calendar_view, name="ipo-calendar"),
    path("api/company-logo/<str:company_name>/", company_logo_view, name="company-logo"),
]
