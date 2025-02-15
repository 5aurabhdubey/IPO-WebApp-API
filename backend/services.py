import requests
from django.conf import settings

def fetch_stock_price(symbol):
    """Fetch real-time stock price from Twelve Data API with error handling."""
    api_key = settings.API_KEYS.get("TWELVE_DATA")
    if not api_key:
        return {"error": "API Key missing for TWELVE_DATA"}

    url = f"https://api.twelvedata.com/price?symbol={symbol}&apikey={api_key}"
    
    try:
        response = requests.get(url)
        response.raise_for_status()
        data = response.json()
        return data.get("price")  # Returns stock price
    except requests.RequestException as e:
        return {"error": f"Failed to fetch stock price: {str(e)}"}

def fetch_ipo_calendar():
    """Fetch IPO-related stocks and retrieve detailed IPO information with proper error handling."""
    api_key = settings.API_KEYS.get("FMP")
    if not api_key:
        return {"error": "API Key missing for FMP"}

    base_url = "https://financialmodelingprep.com/api/v3/search"
    
    try:
        response = requests.get(f"{base_url}?query=IPO&apikey={api_key}")
        response.raise_for_status()
        ipo_data = response.json()

        if not ipo_data:
            return {"error": "No IPO stocks found"}

        detailed_ipo_list = []

        for stock in ipo_data:
            symbol = stock.get("symbol", "N/A")
            company_name = stock.get("name", "N/A")
            stock_exchange = stock.get("stockExchange", "N/A")
            currency = stock.get("currency", "N/A")

            # Fetch additional IPO details from Company Profile API
            profile_url = f"https://financialmodelingprep.com/api/v3/profile/{symbol}?apikey={api_key}"
            
            try:
                profile_response = requests.get(profile_url)
                profile_response.raise_for_status()
                profile_data = profile_response.json()

                listing_date, ipo_price, cmp, current_return, listing_price, issue_type, status, rhp_pdf, drhp_pdf = (
                    "N/A", "N/A", "N/A", "N/A", "N/A", "N/A", "N/A", "N/A", "N/A"
                )

                if profile_data and isinstance(profile_data, list):
                    company_info = profile_data[0]
                    listing_date = company_info.get("ipoDate", "N/A")
                    ipo_price = company_info.get("ipoPrice", "N/A")
                    cmp = company_info.get("price", "N/A")
                    listing_price = company_info.get("listingPrice", "N/A")
                    issue_type = company_info.get("issueType", "N/A")
                    status = company_info.get("status", "N/A")
                    rhp_pdf = company_info.get("rhp", "N/A")  
                    drhp_pdf = company_info.get("drhp", "N/A")

                    # Prevent division errors if IPO price is not valid
                    try:
                        if ipo_price not in ["N/A", None] and cmp not in ["N/A", None]:
                            current_return = f"{((float(cmp) - float(ipo_price)) / float(ipo_price) * 100):.2f}%"
                    except (ValueError, ZeroDivisionError):
                        current_return = "N/A"

            except requests.RequestException as e:
                return {"error": f"Failed to fetch IPO details for {symbol}: {str(e)}"}

            detailed_ipo_list.append({
                "symbol": symbol,
                "company_name": company_name,
                "stock_exchange": stock_exchange,
                "currency": currency,
                "listing_date": listing_date,
                "ipo_price": ipo_price,
                "listing_price": listing_price,
                "current_market_price": cmp,
                "current_return": current_return,
                "issue_type": issue_type,
                "status": status,
                "rhp_pdf": rhp_pdf,
                "drhp_pdf": drhp_pdf
            })

        return {"ipo_calendar": detailed_ipo_list}

    except requests.RequestException as e:
        return {"error": f"Failed to fetch IPO stocks: {str(e)}"}

def fetch_company_logo(company_name):
    """Fetch company logo (or icon) from Brandfetch API with error handling."""
    api_key = settings.API_KEYS.get("BRANDFETCH")
    if not api_key:
        return {"error": "API Key missing for BRANDFETCH"}

    url = f"https://api.brandfetch.io/v2/search/{company_name}?c={api_key}"
    headers = {
        "Authorization": f"Bearer {api_key}",
        "Accept": "application/json"
    }

    try:
        response = requests.get(url, headers=headers)
        response.raise_for_status()
        data = response.json()

        # Ensure response contains at least one brand
        if isinstance(data, list) and len(data) > 0:
            first_company = data[0]
            if "icon" in first_company:
                return {"company": company_name, "logo_url": first_company["icon"]}

        return {"error": f"No logo or icon found for {company_name}. API response: {data}"}

    except requests.RequestException as e:
        return {"error": f"Brandfetch API error: {str(e)}"}
