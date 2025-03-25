from django.urls import path
from . import views

urlpatterns = [
    # Existing routes
    path('api/stock/<str:symbol>/', views.stock_price_view, name='stock-price'),
    path('api/ipo-calendar/', views.ipo_calendar_view, name='ipo-calendar'),
    path('api/company-logo/<str:company_name>/', views.company_logo_view, name='company-logo'),
    # Your admin routes
    path('api/admin/ipos/', views.get_all_ipos, name='get_all_ipos'),
    path('api/admin/ipos/create/', views.create_ipo, name='create_ipo'),
    path('api/admin/ipos/<int:pk>/', views.get_ipo, name='get_ipo'),
    path('api/admin/ipos/<int:pk>/update/', views.update_ipo, name='update_ipo'),
    path('api/admin/ipos/<int:pk>/delete/', views.delete_ipo, name='delete_ipo'),
    path('api/admin/ipos/<int:pk>/status/', views.update_ipo_status, name='update_ipo_status'),
    path("ipo-calendar",views.ipo_calendar_frontend,name="ipo-calendar")

]